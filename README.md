# Open Gitmoji

## Features
- [x] Keywords emoji tips and input（Emoji 提示及插入）.
- [x] ~~Language config（语言配置）.~~
- [x] custom prompt typography（自定义提示排版）.
  - 配置路径：File | Settings | Open Gitmoji Settings | Prompt List
- [x] start with `:`（`:`开头才提示配置）
  - 配置路径：File | Settings | Open Gitmoji Settings | Custom
- [x] ~~suffix text supported（添加结束文本）~~
- [X] 选项列表配置列表，
  - 配置路径：File | Settings | Open Gitmoji Settings | Prompt List
  - 通过配置来添加选项展示（必须配置，没有配置时没有提示）
  - 添加多个则一个 emoji 根据规则展示多行
  - 添加占位符支持
    - gitmoji（#{G）
    - gitmoji-unicode（#{GU）
    - description（#{DESC}）
    - description-cn（#{DESC_CN}）
    - date（已有）（#{DATE}）
    - time（已有）（#{TIME}）
- [X] 自定义 gitmoji 支持
  - 配置路径：File | Settings | Open Gitmoji Settings | Custom

## 使用演示

### 非`:`形式（Old）
![非`:`形式](doc/images/Open%20Emoji%20Finish.gif)

### `:`形式（Old）
![`:`形式](doc/images/Open%20Emoji%20Colon%20Finish.gif)


### Plugin config（插件配置）
<!-- Plugin description -->
**A must-have plugin for working with Emoji.<br>**
<ul>
<li>easy way for input emoji unicode or emoji code</li>
<li>custom prompt list</li> 
<li>custom trigger with `:`</li>  
<li>custom emojis</li>  
</ul>
<br>

**Custom emoji extension description:**
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

**使用 Emoji 的必备插件。<br>**
<ul>
<li>输入 emoji-unicode 或 emoji-code 的简单方法。</li>
<li>自定义提示列表。</li> 
<li>自定义以`:`开头提示。</li>
<li>自定义emoji扩展</li>
</ul>

**自定义 emoji 扩展说明：**
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

<!-- Plugin description end -->

## Thanks For（感谢）
- https://github.com/hellokaton/gitmoji-plugin
- https://github.com/patou/gitmoji-intellij-plugin