# Open Gitmoji

## [英文/English](./README_EN.md)

## Features

- [x] Emoji 提示及插入.
- [x] ~~语言配置.~~
- [x] 自定义提示排版.
    - 配置路径：`File | Settings | Other Settings | Open Gitmoji Settings | Prompt List`
- [x] `:`开头才提示配置.
    - 配置路径：`File | Settings | Other Settings | Open Gitmoji Settings | Custom`
- [x] ~~添加结束文本.~~
- [X] 选项列表配置列表.
    - 配置路径：`File | Settings | Other Settings | Open Gitmoji Settings | Prompt List`
    - 通过配置来添加选项展示（必须配置，没有配置时没有提示）.
    - 添加多个则一个 emoji 根据规则展示多行.
    - 添加占位符支持:
        - emoji（#{G}）
        - emoji-unicode（#{GU}）
        - description（#{DESC}）
        - description-cn（#{DESC_CN}）
        - date（已有）（#{DATE}）
        - time（已有）（#{TIME}）
- [X] 自定义 emoji 支持.
    - 配置路径：`File | Settings | Other Settings | Open Gitmoji Settings | Custom`

## 使用说明

### 配置说明

#### Custom

配置路径: `File | Settings | Other Settings | Open Gitmoji Settings | Custom`

1. Get prompt through text starting with `:` or `：`. Such as `:s`
    - 用途: 主要是为了减少输入提交信息时列表弹出对用户造成的干扰
    - 选中表示由 `:` 或者 `：` 开头输入才会开启提示列表
    - 取消选中表示所有字符串只要匹配到输入信息都会弹出待选列表
2. Custom Emoji Folder
    - 用途: 系统预置的 emoji 符号有限, 有些用户希望能扩展自己的 emoji
    - 选择扩展 emoji 的文件夹
    - 文件夹根目录放置：emojis.json 文件
    - emojis.json 文件格式如下:
      ```json
      {
        "emojis": [
          {
            "emoji": "🦺",
            "entity": "",
            "code": ":safety_vest:",
            "description": "Add or update code related to validation.",
            "name": "safety_vest",
            "cn_description": "添加或更新与验证相关的代码。",
            "semver": null
          }
        ]
      }
      ```
    - 根目录下创建 `icons` 目录，存储对应emoji的 png 图片（只支持png）
    - emoji 对应的 png 图片名为 code 去除 `:` 所得

#### Prompt List

配置路径: `File | Settings | Other Settings | Open Gitmoji Settings | Prompt List`

1. 用途: 满足用户自定义插入及提示内容
2. 必须要有一项或者多项, 如果没有配置则不会有提示列表
3. 每一项配置都会对所有 emoji 分别渲染出一条待选项
4. 列表说明:
    - Pattern: 表示渲染表达式, 支持特定占位符, 特定占位符列表如下
        - #{G}: 替换为 emoji 表情
        - #{GU}: 替换为 emoji 表情的 unicode 值
        - #{DESC}: 替换为 emoji 表情的英文描述
        - #{DESC_CN}: 替换为 emoji 表情的中文描述
        - #{DATE}: 替换为系统当前的日期(yyyy-MM-dd)
        - #{TIME}: 替换为系统当前的时间(HH:mm:ss)
    - Example: 表示 emoji 根据渲染表达式渲染出的选项示例
    - Enable: 表示是否启用, 勾选表示启用, 未勾选表示禁用

### 使用说明

1. 在 `Commit` 界面输入内容唤起提示列表 (勾选 `Custom | Get prompt through text starting with ':' or '：'. Such as ':s'`
   时需要以 ':' or '：' 开头)
2. 在列表中选择自己需要的内容进行插入
3. 如果自定义 emoji 有变更, 可通过 `Commit | Refresh Custom Emojis` 按钮进行刷新(按钮展示为 `刷新` 图标)

## Thanks For（感谢）

- https://github.com/hellokaton/gitmoji-plugin
- https://github.com/patou/gitmoji-intellij-plugin