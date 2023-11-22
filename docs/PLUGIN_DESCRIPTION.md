### A plug-in for easy use of `emoji`
#### Feature:

- easy way for input emoji unicode or emoji code
- custom prompt list
- custom trigger with `:`
- custom emojis

#### Custom emoji extension description:

- Extension root directory placement: emojis.json file
- The emojis.json file format is as follows:
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
- Create the `icons` directory in the root directory to store png images corresponding to emoji (only png is supported)
- The name of the png image corresponding to emoji is `code`, which is obtained by removing `:`

### 一个便于使用 `emoji` 的插件

#### 特性：
- 输入 emoji-unicode 或 emoji-code 的简单方法。
- 自定义提示列表。
- 自定义以`:`开头提示。
- 自定义emoji扩展

#### 自定义 emoji 扩展说明：

- 扩展根目录放置：emojis.json 文件
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
