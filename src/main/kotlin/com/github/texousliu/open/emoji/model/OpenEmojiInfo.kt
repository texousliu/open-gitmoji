package com.github.texousliu.open.emoji.model

class OpenEmojiInfo(
    emoji: String = "",
    entity: String = "",
    code: String = "",
    name: String = "",
    description: String = "",
    cnDescription: String = "",
    var enable: Boolean = true,
    var type: OpenEmojiInfoType = OpenEmojiInfoType.DEFAULT
) : OpenEmoji(emoji, entity, code, name, description, cnDescription), Cloneable {

    constructor(emoji: OpenEmoji) : this(emoji, true, OpenEmojiInfoType.DEFAULT)

    constructor(emoji: OpenEmoji, enable: Boolean, type: OpenEmojiInfoType) : this(
        emoji.emoji,
        emoji.entity,
        emoji.code,
        emoji.name,
        emoji.description,
        emoji.cnDescription,
        enable,
        type
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is OpenEmojiInfo) {
            return this.emoji == other.emoji
        }
        return false
    }

    override fun hashCode(): Int {
        return this.emoji.hashCode()
    }

    override fun toString(): String {
        return """
            {"emoji": "$emoji", "entity": "$entity", "code": "$code", "name": "$name", "description": "$description", "cnDescription": "$cnDescription", "enable": $enable}
        """.trimIndent()
    }

    public override fun clone(): OpenEmojiInfo {
        return OpenEmojiInfo(emoji, entity, code, name, description, cnDescription, enable)
    }

}