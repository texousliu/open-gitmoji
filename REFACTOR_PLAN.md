# Open Gitmoji 重构计划

## 一、现状诊断

### 1.1 项目定位

Open Gitmoji 是一款 IntelliJ IDEA 平台插件，核心功能是在 Git Commit 界面和代码编辑器中提供 Emoji 快捷输入。当前版本 `243.4.0.0`，基于 Kotlin + Gradle 构建，支持 IDEA 2024.3+。

### 1.2 核心问题清单

经过对源码的逐层分析，项目存在以下五类结构性问题：

| 问题类别 | 严重程度 | 具体表现 |
|----------|----------|----------|
| 实体类设计混乱 | 高 | 三层继承链（`OpenEmojiBase` → `OpenEmoji` → `OpenEmojiInfo`）职责边界模糊，`OpenEmoji` 直接依赖 `OpenEmojiUtils` 加载图标，违反单一职责原则 |
| Emoji 数据源单一 | 中 | 仅支持本地文件夹（`customEmojis(directory)`），无法从网络加载，用户扩展成本高 |
| 缺少自动同步能力 | 中 | 无爬虫或 API 同步机制，gitmoji 规范更新后需手动维护 `emojis.json` |
| 配置面板体验欠佳 | 中 | Pattern 表格和 Emoji 信息表格各自独立，缺少统一搜索和筛选；对话框内字段布局密集 |
| 待实现功能堆积 | 低 | 多语言支持、多 Emoji 选择、自定义示例拷贝等需求长期未落地 |

---

## 二、重构目标

### 2.1 总体目标

将项目从「功能可用」提升到「架构清晰、易于扩展、体验流畅」的状态，使后续功能迭代和维护成本显著降低。

### 2.2 分项目标

| 维度 | 目标描述 |
|------|----------|
| 架构 | 实体类高内聚、低耦合，引入 Repository + Service 分层 |
| 数据源 | 支持本地文件夹、网络 URL、内置默认三种来源，可动态切换和组合 |
| 同步 | 支持从 gitmoji 官方仓库自动同步 Emoji 定义，提供手动刷新和定时检查 |
| 配置 | 配置面板支持搜索、筛选、分组，交互更符合 IntelliJ 平台规范 |
| 功能补全 | 完成多语言、多 Emoji 选择等遗留需求 |

---

## 三、重构方案详解

### 方向一：实体类重构 —— 高内聚、低耦合、易扩展

#### 3.1.1 当前问题分析

现有三层继承结构：

```
OpenEmojiBase (数据字段)
    └── OpenEmoji (+ isCustom, + getIcon() 依赖 OpenEmojiUtils)
            └── OpenEmojiInfo (+ enable, type, changed, clone, modified检测)
```

问题点：

- `OpenEmoji.getIcon()` 直接调用 `OpenEmojiUtils.getIcon()`，将图标加载逻辑硬编码在实体类中
- `OpenEmojiInfo` 同时承担「数据载体」和「变更检测」两种职责，`modified()` / `change()` / `infoChanged()` 逻辑复杂且与 UI 状态耦合
- 继承链导致构造器爆炸，`OpenEmojiInfo` 有 3 个构造器重载
- `equals()` 仅比较 `emoji` 字段，无法区分同 emoji 不同 code 的情况

#### 3.1.2 重构方案

**方案：拆分为「数据实体 + 领域对象 + 状态包装」三层**

```
// 纯数据实体（不可变或最小可变）
OpenEmojiData
    ├── emoji: String
    ├── entity: String
    ├── code: String
    ├── name: String
    ├── description: String
    ├── cnDescription: String
    └── semver: String?

// 领域对象（带业务行为，不依赖 UI 工具类）
OpenEmoji
    ├── data: OpenEmojiData
    ├── source: EmojiSource          // DEFAULT / CUSTOM / NETWORK
    └── iconRef: IconReference       // 图标引用描述，非实际 Icon 对象

// UI 状态包装（仅用于配置面板）
EmojiConfigState
    ├── emoji: OpenEmoji
    ├── enabled: Boolean
    ├── overridden: Boolean
    └── dirty: Boolean              // 是否被用户修改过
```

**关键改动：**

| 改动项 | 说明 |
|--------|------|
| 移除继承链 | `OpenEmojiBase` / `OpenEmoji` / `OpenEmojiInfo` 合并为扁平结构 |
| 图标加载外移 | 实体类只保留 `iconPath` 或 `IconReference`，实际 `Icon` 对象由 `IconLoaderService` 延迟加载 |
| 变更检测剥离 | `modified()` / `change()` 等逻辑移至 `EmojiConfigState` 或独立 comparator |
| 引入 `EmojiSource` 枚举 | 标识数据来源，替代 `isCustom` + `OpenEmojiInfoType` 的组合 |
| `equals()` / `hashCode()` 规范化 | 基于 `code` 字段（唯一标识）而非 `emoji` |

#### 3.1.3 接口设计

```kotlin
// 数据来源标识
enum class EmojiSource { DEFAULT, CUSTOM, NETWORK, OVERRIDE }

// 纯数据实体
data class OpenEmojiData(
    val emoji: String,
    val entity: String,
    val code: String,
    val name: String,
    val description: String,
    val cnDescription: String,
    val semver: String? = null
)

// 领域对象
class OpenEmoji(
    val data: OpenEmojiData,
    val source: EmojiSource = EmojiSource.DEFAULT,
    val iconPath: String? = null
) {
    val code: String get() = data.code
    val emoji: String get() = data.emoji
    // ... 委托属性
}

// UI 状态（仅配置面板使用）
class EmojiConfigState(
    val emoji: OpenEmoji,
    var enabled: Boolean = true,
    var dirty: Boolean = false
)
```

#### 3.1.4 影响范围

- `model` 包下所有类
- `utils.OpenEmojiUtils` 中的 `convert()` / `emojiInfoList()` 系列方法
- `persistence.OpenEmojiPersistent` 的存储字段
- `dialog.OpenEmojiInfoDialogPanel` 的表格模型
- `contributor` 包中的补全逻辑

---

### 方向二：自定义 Emoji 支持网络来源

#### 3.2.1 需求描述

当前仅支持本地文件夹作为自定义 Emoji 来源。重构后需支持：

1. **本地文件夹**（已有功能，保留兼容）
2. **网络 URL**（新增）：用户可配置一个 HTTP/HTTPS 地址指向 `emojis.json`
3. **内置默认**（已有功能，作为 fallback）

#### 3.2.2 架构设计

引入 **Repository 模式**，统一抽象 Emoji 数据来源：

```kotlin
// 数据来源接口
interface EmojiRepository {
    val name: String
    val priority: Int              // 加载优先级
    fun load(): List<OpenEmoji>
    fun isAvailable(): Boolean
}

// 本地文件夹实现
class LocalEmojiRepository(val directory: String) : EmojiRepository

// 网络 URL 实现
class NetworkEmojiRepository(val url: String) : EmojiRepository

// 内置资源实现
class BuiltInEmojiRepository : EmojiRepository

// 组合仓库（按优先级合并）
class CompositeEmojiRepository(val repositories: List<EmojiRepository>) : EmojiRepository
```

**合并规则：**

| 场景 | 处理逻辑 |
|------|----------|
| 不同来源，不同 code | 全部保留 |
| 不同来源，相同 code | 按优先级覆盖（CUSTOM > NETWORK > DEFAULT） |
| 同来源重复 | 去重，保留第一个 |

#### 3.2.3 网络加载细节

- 使用 `java.net.http.HttpClient`（Java 11+）或 IntelliJ 平台提供的 `HttpRequests` 工具类
- 支持超时配置（默认 10 秒）
- 失败时降级到内置默认，并弹出通知提示
- 缓存机制：下载的 `emojis.json` 保存到插件配置目录，下次启动优先使用缓存

#### 3.2.4 配置扩展

在 `OpenEmojiPersistent` 中新增字段：

```kotlin
var customEmojiUrl: String = ""        // 网络来源 URL
var networkEmojiEnabled: Boolean = false
var networkEmojiCacheDuration: Int = 24 * 60  // 缓存有效期（分钟）
```

---

### 方向三：简易爬虫 —— 从 gitmoji 官方同步

#### 3.3.1 数据源分析

gitmoji 官方项目 [carloscuesta/gitmoji](https://github.com/carloscuesta/gitmoji) 维护了标准的 Emoji 定义，其数据文件位于：

- `packages/gitmojis/src/gitmojis.json` —— 官方标准 Emoji 列表
- 格式与当前 `emojis.json` 基本一致（含 `emoji`, `code`, `description`, `name` 等字段）

#### 3.3.2 同步策略

**方案：基于 GitHub Raw 的轻量级同步**

| 策略项 | 说明 |
|--------|------|
| 同步源 | `https://raw.githubusercontent.com/carloscuesta/gitmoji/main/packages/gitmojis/src/gitmojis.json` |
| 触发方式 | 手动刷新（按钮）+ 启动时自动检查（可选） |
| 对比机制 | 下载后与本地的 `emojis.json` 做 diff，展示新增/修改/删除项 |
| 用户确认 | 发现更新时弹出通知，用户确认后合并 |
| 冲突处理 | 用户自定义的 Emoji 不会被覆盖；仅更新 `DEFAULT` 类型的条目 |

#### 3.3.3 爬虫模块设计

```kotlin
// 同步服务
interface EmojiSyncService {
    fun checkForUpdate(): SyncResult
    fun sync(): SyncResult
    fun lastSyncTime(): Long?
}

// 同步结果
data class SyncResult(
    val success: Boolean,
    val added: List<OpenEmoji>,
    val modified: List<Pair<OpenEmoji, OpenEmoji>>,
    val removed: List<OpenEmoji>,
    val error: String? = null
)

// 后台任务（IntelliJ ProgressIndicator）
class EmojiSyncBackgroundTask : Task.Backgroundable(...)
```

#### 3.3.4 UI 集成

- 在 `OpenEmojiInfoConfiguration` 面板中新增「同步设置」分组：
  - 启用自动检查（复选框）
  - 检查频率（下拉：每天 / 每周 / 每月）
  - 手动同步按钮
  - 上次同步时间显示
- 同步完成后弹出差异摘要通知

---

### 方向四：配置面板优化

#### 3.4.1 当前问题

| 问题 | 影响 |
|------|------|
| Emoji 信息表格无搜索 | 70+ 个 Emoji 时难以定位 |
| 表格列宽固定 | 长描述文本被截断 |
| Pattern 和 Emoji 配置分散在两个标签页 | 用户需在设置面板中来回切换 |
| 添加/编辑 Emoji 的对话框字段过多 | 一次展示 8 个输入框，视觉拥挤 |
| 缺少分组/筛选 | 无法按类型（DEFAULT / CUSTOM / OVERRIDE）筛选 |

#### 3.4.2 优化方案

**4.2.1 统一配置面板结构**

将当前两个独立 Configurable 合并为一个主面板，内部用 Tab 切换：

```
┌─────────────────────────────────────────┐
│  Open Gitmoji Settings                  │
├─────────────────────────────────────────┤
│  [General] [Emoji List] [Patterns] [Sync]│
├─────────────────────────────────────────┤
│                                         │
│  Tab 内容区                              │
│                                         │
└─────────────────────────────────────────┘
```

**4.2.2 Emoji List Tab 改进**

| 改进项 | 实现方式 |
|--------|----------|
| 搜索框 | 顶部添加 `JBTextField`，实时过滤 `code` / `name` / `description` / `cnDescription` |
| 类型筛选 | 添加下拉框：全部 / 默认 / 自定义 / 覆盖 / 网络 |
| 表格优化 | 使用 `TableView` + `ListTableModel` 替代原生 `JBTable`，支持排序 |
| 分页/虚拟滚动 | Emoji 数量超过 200 时启用虚拟滚动 |
| 批量操作 | 支持多选后批量启用/禁用/删除 |

**4.2.3 Pattern Tab 改进**

| 改进项 | 实现方式 |
|--------|----------|
| 实时预览 | 添加预览区域，展示当前选中 Pattern 的渲染效果 |
| 拖拽排序 | 支持拖拽行调整优先级 |
| 导入/导出 | 支持将 Pattern 列表导出为 JSON / 从 JSON 导入 |

**4.2.4 添加/编辑对话框改进**

将单页 8 个字段拆分为两步向导：

```
Step 1: 基本信息
    - Emoji 字符（必填）
    - Code（必填，自动校验格式 `:xxx:`）

Step 2: 详细信息
    - Name / Entity / Description / CN Description
    - 图标选择（文件选择器，可选）
```

---

### 方向五：完善待实现功能

#### 3.5.1 多语言支持

| 任务 | 说明 |
|------|------|
| 提取所有硬编码中文 | 将 `OpenEmojiBundle.properties` 补全为 `OpenEmojiBundle_zh.properties` 和 `OpenEmojiBundle_en.properties` |
| 动态切换 | 跟随 IDE 语言设置自动切换，无需手动配置 |
| 描述字段多语言 | `cn_description` 保留作为中文描述，新增 `description_i18n` 映射支持更多语言 |

#### 3.5.2 多 Emoji 选择支持

| 任务 | 说明 |
|------|------|
| 组合选择 | Commit 界面支持一次选择多个 Emoji，按优先级去重 |
| 分隔符配置 | 用户可配置多个 Emoji 之间的分隔符（默认空格） |
| 快捷组合 | 支持预设组合（如 `:art:` + `:zap:` = 「优化结构和性能」） |

#### 3.5.3 自定义示例拷贝

| 任务 | 说明 |
|------|------|
| 右键菜单 | 在 Emoji 列表右键可复制「Code / Emoji 字符 / 完整模板」 |
| 快捷键 | `Ctrl+C` 复制选中 Emoji 的默认模板 |

---

## 四、重构实施计划

### 4.1 阶段划分

| 阶段 | 周期 | 目标 | 产出 |
|------|------|------|------|
| 第一阶段 | 1 周 | 实体类重构 + 单元测试 | 新 model 包、通过全部测试 |
| 第二阶段 | 1 周 | Repository 模式 + 网络支持 | EmojiRepository 接口及三种实现 |
| 第三阶段 | 1 周 | 爬虫同步 + 配置持久化扩展 | EmojiSyncService、后台任务、通知 |
| 第四阶段 | 1 周 | 配置面板重构 | 新 Tab 面板、搜索筛选、向导对话框 |
| 第五阶段 | 1 周 | 功能补全 + 回归测试 | 多语言、多选、拷贝、全量测试 |

### 4.2 依赖关系

```
第一阶段（实体类）
    └── 第二阶段（Repository）依赖第一阶段
            └── 第三阶段（爬虫）依赖第二阶段
                    └── 第四阶段（面板）依赖第一 + 三阶段
                            └── 第五阶段（功能补全）依赖全部
```

### 4.3 风险与应对

| 风险 | 影响 | 应对策略 |
|------|------|----------|
| 实体类重构破坏现有存储格式 | 高 | 提供 `OpenEmojiPersistent` 的迁移逻辑，旧版数据自动转换 |
| 网络加载失败导致插件启动变慢 | 中 | 网络请求异步化，超时 5 秒，失败静默降级 |
| GitHub Raw 访问受限 | 中 | 支持镜像源配置（Gitee / jsDelivr） |
| 配置面板改动大，用户不适应 | 低 | 保留旧面板入口一个版本，标注「即将移除」 |

---

## 五、验收标准

| 验收项 | 标准 |
|--------|------|
| 架构 | `model` 包无继承链，实体类不依赖 `utils` 或 `ui` 包 |
| 数据源 | 支持本地 + 网络 + 内置三种来源，可独立启用/禁用 |
| 同步 | 手动同步成功，差异摘要正确，用户确认后合并 |
| 面板 | 支持搜索、筛选、排序，添加 Emoji 使用两步向导 |
| 功能 | 多语言切换正常，多选插入正常，示例拷贝正常 |
| 兼容 | 旧版本配置自动迁移，无数据丢失 |
| 测试 | 新增单元测试覆盖率 ≥ 60%，全部测试通过 |
