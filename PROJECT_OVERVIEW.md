# Open Gitmoji 项目梳理文档

## 一、项目概述

**Open Gitmoji** 是一款 IntelliJ IDEA 平台插件，旨在帮助开发者在 Git 提交信息及代码编辑器中快速输入 Emoji。插件基于 [Gitmoji](https://gitmoji.dev/) 规范，支持自定义 Emoji、自定义提示模板、双场景补全（Commit 界面 / 编辑器）等功能。

| 属性 | 说明 |
|------|------|
| 插件 ID | `com.github.texousliu.opengitmoji` |
| 插件名称 | Open Gitmoji |
| 当前版本 | `243.4.0.0` |
| 开发语言 | Kotlin |
| 构建工具 | Gradle (Kotlin DSL) |
| 支持平台 | IntelliJ IDEA 2024.3+ |
| 许可证 | 详见 `LICENSE` |

---

## 二、技术栈与构建配置

### 2.1 核心技术栈

| 技术/框架 | 版本 | 用途 |
|-----------|------|------|
| Kotlin | `2.2.21` | 主要开发语言 |
| Java | `21` | JVM 目标版本 |
| Gradle | `9.2.0` | 构建工具 |
| IntelliJ Platform Gradle Plugin | `2.10.5` | IDE 插件构建与开发 |
| Gradle Changelog Plugin | `2.5.0` | 变更日志管理 |
| Gradle Kover Plugin | `0.9.3` | 代码覆盖率 |
| Qodana | `2025.2.2` | 静态代码分析 |
| JUnit | `4.13.2` | 单元测试 |

### 2.2 Gradle 配置要点

- **Group**: `com.github.texousliu.opengitmoji`
- **插件版本**: `243.4.0.0`（遵循 `平台版本.主版本.次版本.修订号` 格式）
- **最低支持构建号**: `243`（对应 IDEA 2024.3）
- **平台类型**: `IC`（IntelliJ IDEA Community）
- **捆绑插件依赖**: `com.intellij.java`
- **Gradle 特性**: 启用 Configuration Cache 与 Build Cache

---

## 三、项目目录结构

```
open-gitmoji/
├── .github/                    # GitHub 配置
│   ├── ISSUE_TEMPLATE/         # Issue 模板（Bug 报告、功能请求）
│   ├── workflows/              # GitHub Actions 工作流
│   │   ├── build.yml           # 构建、测试、插件验证
│   │   ├── release.yml         # 发布流程
│   │   └── run-ui-tests.yml    # UI 测试
│   └── dependabot.yml          # 依赖自动更新
├── .intellijPlatform/          # IntelliJ 平台本地缓存
├── .run/                       # IDE 运行配置
│   ├── Run Plugin.run.xml
│   ├── Run Tests.run.xml
│   └── Run Verifications.run.xml
├── docs/                       # 文档
│   ├── CHANGELOG.md            # 变更日志
│   ├── PLUGIN_DESCRIPTION.md   # 插件描述（中英文）
│   └── README_EN.md            # 英文 README
├── gradle/
│   ├── wrapper/                # Gradle Wrapper
│   └── libs.versions.toml      # 版本目录（Version Catalog）
├── src/
│   └── main/
│       ├── kotlin/             # Kotlin 源码
│       │   └── com/github/texousliu/open/emoji/
│       │       ├── compatiable/    # 兼容性代码
│       │       ├── config/         # 配置面板与国际化
│       │       ├── constants/      # 常量定义
│       │       ├── context/        # 全局缓存
│       │       ├── contributor/    # 代码补全贡献者
│       │       ├── dialog/         # UI 对话框与表格模型
│       │       ├── model/          # 数据模型
│       │       ├── persistence/    # 持久化存储
│       │       └── utils/          # 工具类
│       └── resources/
│           ├── META-INF/
│           │   ├── plugin.xml      # 插件主配置
│           │   └── pluginIcon.svg  # 插件图标
│           ├── icons/
│           │   ├── emojis/         # Emoji PNG 图标（约 70+ 个）
│           │   └── plugin/         # 插件图标
│           ├── messages/
│           │   └── OpenEmojiBundle.properties  # 国际化文本
│           └── emojis.json         # 默认 Emoji 数据
├── build.gradle.kts            # 构建脚本
├── gradle.properties           # Gradle 属性配置
├── settings.gradle.kts         # 项目设置
├── qodana.yml                  # Qodana 配置
├── codecov.yml                 # Codecov 配置
├── .gitignore
├── LICENSE
└── README.md                   # 项目说明（中文）
```

---

## 四、源码架构详解

### 4.1 包结构及职责

| 包路径 | 职责说明 |
|--------|----------|
| `model` | 核心数据模型定义（Emoji、EmojiInfo、Pattern 等） |
| `config` | IDE 设置面板（SearchableConfigurable）、国际化 Bundle、Action |
| `contributor` | 代码补全贡献者（Commit/Editor 场景）、插入处理器、补全置信度 |
| `dialog` | 设置界面的 UI 面板、表格模型 |
| `persistence` | 数据持久化（XML 存储）、类型转换器、自定义序列化 |
| `context` | 全局缓存（`OpenEmojiCache`），管理内存中的 Emoji 列表 |
| `utils` | 工具类（JSON 解析、图标加载、字符串替换、剪贴板操作等） |
| `constants` | 常量定义（`WorkEnv` 枚举：COMMIT / EDITOR） |
| `compatiable` | 兼容性代码（`BrowseFolderActionListener`，兼容 IDEA 2024.3 EAP） |

### 4.2 核心类功能说明

#### Model 层

| 类名 | 说明 |
|------|------|
| `OpenEmojiBase` | 最基础的数据类，包含 `emoji`、`entity`、`code`、`name`、`description`、`cnDescription` |
| `OpenEmoji` | 继承 `OpenEmojiBase`，增加 `isCustom` 标志和图标加载能力 |
| `OpenEmojiInfo` | 继承 `OpenEmoji`，增加 `enable`（是否启用）、`type`（CUSTOM / OVERRIDE / DEFAULT）、`changed` 标志，支持克隆和修改检测 |
| `OpenEmojiPattern` | 表情输出格式模板，包含 `pattern` 字符串及 `enable` / `enableCommit` / `enableEditor` 三个开关 |
| `OpenEmojiList` | GSON 反序列化用的包装类，内含 `List<OpenEmoji>` |
| `OpenEmojiInfoType` | 枚举，标识 Emoji 来源类型（CUSTOM 自定义、OVERRIDE 覆盖默认、DEFAULT 默认） |

#### Contributor 层（扩展点实现）

| 类名 | 说明 |
|------|------|
| `OpenEmojiCommitCompletionContributor` | Git Commit Message 场景的代码补全贡献者。监听 `PsiPlainText`，在多行文本框中触发，支持 `:` 前缀触发 |
| `OpenEmojiEditorCompletionContributor` | 编辑器内通用场景的代码补全贡献者。监听任意 PSI 元素，支持空格/冒号/中文冒号分割输入 |
| `OpenEmojiInsertHandler` | 补全选中后的插入处理器，负责将带 `REPLACE_SUFFIX_MARK` 的文本替换为实际表情内容 |
| `OpenEmojiDocCompletionConfidence` | 控制 Java 文档注释（DOC_* / *_COMMENT）中不跳过自动补全弹出 |

#### Config 层

| 类名 | 说明 |
|------|------|
| `OpenEmojiConfiguration` | 主设置面板（`SearchableConfigurable`），管理触发方式、编辑器支持开关、Pattern 列表 |
| `OpenEmojiInfoConfiguration` | Emoji 信息管理面板（子 Configurable），管理自定义目录、Emoji 启用/禁用/编辑 |
| `OpenEmojiBundle` | 国际化资源加载器（`DynamicBundle`） |
| `OpenEmojiGitCommitRefreshAction` | Action，用于从磁盘刷新自定义 Emoji，注册在 VCS Message Action Group |

#### Persistence 层

| 类名 | 说明 |
|------|------|
| `OpenEmojiPersistent` | 核心持久化服务（`@Service` + `PersistentStateComponent`），存储到 `openEmoji.xml`。保存字段包括：`triggerWithColon`、`customEmojiDirectory`、`editorEmojiSupported`、`openEmojiPatterns`、`openEmojiInfoList` |
| `OpenEmojiInfoListConverter` / `OpenEmojiPatternListConverter` | XML 存储时的 GSON 类型转换器 |
| `OpenEmojiInfoSerializer` | `OpenEmojiInfo` 的自定义 GSON 序列化器 |

#### Dialog 层

| 类名 | 说明 |
|------|------|
| `OpenEmojiDialogPanel` | 主设置界面的 Pattern 表格面板，支持增删改查、上下移动 |
| `OpenEmojiInfoDialogPanel` | Emoji 信息管理面板，支持自定义目录选择、Emoji 增删改、重置/重载/复制 JSON |
| `OpenEmojiPatternsTableModel` / `OpenEmojiInfoTableModel` | 对应的表格数据模型 |

#### Utils / Context

| 类名 | 说明 |
|------|------|
| `OpenEmojiUtils` | 核心工具类。功能包括：加载默认/自定义 `emojis.json`、合并自定义 Emoji、Pattern 字符串替换（支持 `#{G}` `#{GU}` `#{DESC}` `#{DESC_CN}` `#{DATE}` `#{TIME}`）、图标加载、剪贴板操作 |
| `OpenEmojiCache` | 全局单例缓存，持有内存中的 `emojiInfoList`，支持刷新 |

---

## 五、数据模型

### 5.1 Emoji JSON 数据结构

默认 Emoji 数据存储在 `src/main/resources/emojis.json` 中，结构如下：

```json
{
  "emojis": [
    {
      "emoji": "🎨",
      "entity": "&#x1f3a8;",
      "code": ":art:",
      "description": "Improve structure / format of the code.",
      "name": "art",
      "cn_description": "改进结构和代码格式",
      "semver": null
    }
  ]
}
```

| 字段 | 说明 |
|------|------|
| `emoji` | Unicode 字符 |
| `entity` | HTML 实体编码 |
| `code` | Emoji 代码（如 `:art:`） |
| `description` | 英文描述 |
| `name` | 名称标识 |
| `cn_description` | 中文描述 |
| `semver` | 语义化版本影响（可选，如 `patch` / `minor`） |

### 5.2 Pattern 占位符

用户可通过配置自定义提示内容的渲染格式，支持以下占位符：

| 占位符 | 说明 |
|--------|------|
| `#{G}` | 替换为 Emoji 表情 |
| `#{GU}` | 替换为 Emoji 表情的 Unicode 值 |
| `#{DESC}` | 替换为 Emoji 表情的英文描述 |
| `#{DESC_CN}` | 替换为 Emoji 表情的中文描述 |
| `#{DATE}` | 替换为系统当前日期（`yyyy-MM-dd`） |
| `#{TIME}` | 替换为系统当前时间（`HH:mm:ss`） |

---

## 六、插件扩展点（Extension Points）

插件通过 `plugin.xml` 向 IntelliJ 平台注册以下扩展点：

| 扩展点类型 | 实现类 | 作用 |
|-----------|--------|------|
| `completion.contributor` (TEXT) | `OpenEmojiCommitCompletionContributor` | Git Commit Message 输入框中的 Emoji 补全 |
| `completion.contributor` (any) | `OpenEmojiEditorCompletionContributor` | 编辑器任意位置的 Emoji 补全 |
| `completion.confidence` (JAVA) | `OpenEmojiDocCompletionConfidence` | 确保 Java 文档注释中也能弹出 Emoji 补全 |
| `projectConfigurable` | `OpenEmojiConfiguration` | IDE 设置面板（主） |
| `projectConfigurable` (子) | `OpenEmojiInfoConfiguration` | IDE 设置面板（Emoji 信息管理） |
| `action` | `OpenEmojiGitCommitRefreshAction` | VCS 工具栏的"刷新自定义 Emoji"按钮 |
| `notificationGroup` | - | 通知气泡组 |

---

## 七、CI/CD 与自动化

### 7.1 GitHub Actions 工作流

| 工作流 | 触发条件 | 功能 |
|--------|----------|------|
| `build.yml` | `push` 到 `main` 分支 / `pull_request` | Gradle Wrapper 验证、运行测试、Qodana 代码检查、插件验证、构建产物、创建 Release Draft |
| `release.yml` | Release 发布时 | 执行发布任务 |
| `run-ui-tests.yml` | 手动触发 | 运行 UI 测试 |

### 7.2 代码质量工具

- **Qodana**: JetBrains 静态代码分析
- **Kover**: Kotlin 代码覆盖率
- **Codecov**: 覆盖率报告上传

---

## 八、功能特性

### 已实现功能

- [x] Emoji 提示及插入
- [x] 自定义提示排版（Pattern 列表配置）
- [x] `:` 开头才提示配置
- [x] 选项列表配置（支持多行展示、占位符替换）
- [x] 自定义 Emoji 支持（通过 `emojis.json` + `icons/` 扩展）
- [x] Emoji 展示界面（列表、编辑、JSON 拷贝、刷新、启用/禁用、查重、Reset/Refresh）
- [x] `MessageBound` 支持
- [x] 编辑器支持 Emoji 插入（双场景：Commit / Editor）

### 待实现功能

- [ ] 多语言支持
- [ ] 多 Emoji 选择支持（组合选择、按优先级去重）
- [ ] 自定义示例拷贝

---

## 九、使用说明概要

### 配置路径

所有配置位于：`File | Settings | Other Settings | Open Gitmoji Settings`

### 快速使用

1. 在 `Commit` 界面输入内容唤起提示列表（若开启 `Custom | Get prompt through text starting with ':'` 则需以 `:` 或 `：` 开头）
2. 在列表中选择需要的内容进行插入
3. 若自定义 Emoji 有变更，可通过 Commit 界面的 `刷新` 按钮进行刷新

### 自定义 Emoji 扩展

1. 选择扩展 Emoji 的文件夹
2. 文件夹根目录放置 `emojis.json` 文件（格式见上文）
3. 根目录下创建 `icons` 目录，存储对应 Emoji 的 PNG 图片（文件名 = `code` 去除 `:`）

---

## 十、版本历史（精选）

| 版本 | 主要变更 |
|------|----------|
| `243.4.0.0` | 升级 Plugin 2.x，修改废弃 API 调用，升级 IDEA 支持至 2024.3 |
| `3.0.0` | 添加 Editor Emoji 输入支持，表达式列表添加场景开关 |
| `2.0.5` | 重构配置存储，自定义 Emoji 默认图标支持，拆分中英文文档 |
| `2.0.1` | 重构配置界面，自定义提示列表，实时渲染插入文本 |
| `1.0.3` | 添加以 `:` 开头的文本才提示配置，解决不需要提示时也会触发的问题 |
| `0.0.1` | 初始化项目 |

---

## 十一、致谢

本项目参考并感谢以下开源项目：

- [gitmoji-plugin](https://github.com/hellokaton/gitmoji-plugin)
- [gitmoji-intellij-plugin](https://github.com/patou/gitmoji-intellij-plugin)
