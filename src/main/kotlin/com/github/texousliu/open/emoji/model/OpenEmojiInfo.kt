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

    @Transient
    var changed: Boolean = false

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
    ) {
        isCustom = emoji.isCustom
    }

    fun markCustom(isCustom: Boolean): OpenEmojiInfo {
        super.custom(isCustom)
        return this
    }

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
        return OpenEmojiInfo(emoji, entity, code, name, description, cnDescription, enable, type).markCustom(isCustom)
    }

    fun modified(old: OpenEmojiInfo): Boolean = this.enable != old.enable || this.type != old.type || infoChanged(old)

    fun change(value: OpenEmojiInfo): OpenEmojiInfo {
        this.enable = value.enable
        if (infoChanged(value)) {
            this.type = if (this.type == OpenEmojiInfoType.DEFAULT) OpenEmojiInfoType.OVERRIDE else this.type
            this.name = value.name
            this.entity = value.entity
            this.description = value.description
            this.cnDescription = value.cnDescription
        }
        return this
    }

    private fun infoChanged(old: OpenEmojiInfo): Boolean = this.entity != old.entity
            || this.name != old.name
            || this.description != old.description
            || this.cnDescription != old.cnDescription

}