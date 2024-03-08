# Open Gitmoji

## [中文/Chinese](../README.md)

## [CHANGELOG](CHANGELOG.md)

## Features

- [X] Emoji prompt and insert
- [x] ~~Language configuration~~
- [x] Custom prompt layout
    - Configuration path: `File | Settings | Other Settings | Open Gitmoji Settings | Prompt List`
- [x] `:` Configuration is only prompted at the beginning
    - Configuration path: `File | Settings | Other Settings | Open Gitmoji Settings | Custom`
- [x] ~~Add ending text~~
- [X] Option list configuration list
    - Configuration path: `File | Settings | Other Settings | Open Gitmoji Settings | Prompt List`
    - Add option display through configuration (mandatory, no prompt when not configured)
    - Add multiple emoji to display multiple rows according to rules
    - Add placeholder support:
        - emoji (#{G})
        - emoji-unicode (#{GU})
        - description (#{DESC})
        - description-cn (#{DESC_CN})
        - date (#{DATE})
        - time (#{TIME})
- [X] Custom emoji support.
    - Configuration path: `File | Settings | Other Settings | Open Gitmoji Settings | Emoji Info List`
-
- [X] emoji display interface
    - Configuration path: `File | Settings | Other Settings | Open Gitmoji Settings | Emoji Info List`
    - [X] emoji list
    - [X] emoji editor
    - [X] emoji json copy
    - [X] emoji refresh
    - [ ] Custom sample copy
    - [X] emoji enable or disable
    - [X] emoji duplication check (custom, override, default)
    - [X] reset button
    - [X] refresh button
- [ ] MessageBound support

## Instructions for use

### Configuration Description

#### Custom

Configuration path: `File | Settings | Other Settings | Open Gitmoji Settings | Emoji Info List`

1. Get prompt through text starting with `:` or `:` Such as `: s`
    - Purpose: Mainly to reduce interference caused by pop-up lists when inputting and submitting information to users
    - Selecting indicates that input starting with `:` or `:` will only activate the prompt list
    - Unchecking means that all strings that match the input information will pop up a list to be selected
2. Custom Emoji Folder
    - Purpose: The system preset emoji symbols are limited, and some users hope to expand their emoji
    - Select a folder for expanding emoji
    - Folder root directory placement: emojis.json file
    - The format of the emojis.json file is as follows:
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
    - Create a `icons` directory under the root directory to store png images corresponding to emoji (only png is
      supported)
    - The png image name corresponding to emoji is obtained by removing `:` from the code

#### Prompt List

Configuration path: `File | Settings | Other Settings | Open Gitmoji Settings | Prompt List`

1. Purpose: To meet user defined insertion and prompt content
2. There must be one or more items, and if not configured, there will be no prompt list
3. Each configuration will render a pending option for all emoji separately
4. List Description:
    - Pattern: Represents a rendering expression that supports specific placeholders. The list of specific placeholders
      is as follows:
        - #{G}: Replace with emoji emoji
        - #{GU}: Replace with the unicode value of emoji emojis
        - #{DESC}: Replace with the English description of emoji emojis
        - 3#{DESC_CN}: Replace with the Chinese description of emoji emoji emoji
        - #{DATE}: Replace with the current system date (yyyy-MM-dd)
        - #{TIME}: Replace with the current system time (HH:mm:ss)
    - Example: Represents an example of options rendered by emoji based on rendering expressions
    - Enable: indicates whether to enable, check to enable, uncheck to disable

### Instructions for use

1. In the `Commit` interface, enter the content to call up the prompt list (
   check `Custom | Get prompt through text starting with ':' or '：'. Such as ':s'` and start with ':' or '：')
2. Select the content you need in the list to insert
3. If there are changes to the custom emoji, it can be refreshed using the `Commit | Refresh Custom Emojis` button (
   displayed as the `Refresh` icon)

## Thanks For

- https://github.com/hellokaton/gitmoji-plugin
- https://github.com/patou/gitmoji-intellij-plugin