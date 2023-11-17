# 扩展 emoji 说明
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
- 根目录下创建 `icons` 目录，存储对应 emoji 的 png 图片（只支持png）
- emoji 对应的 png 图片名为 code 去除 `:` 所得