package com.github.texousliu.open.emoji.model

import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import javax.swing.Icon

open class OpenEmoji(
        emoji: String,
        entity: String,
        code: String,
        name: String,
        description: String,
        cnDescription: String
) : OpenEmojiBase(emoji, entity, code, name, description, cnDescription) {

    var isCustom = false

    @Transient
    private lateinit var _icon: Icon

    fun getIcon(): Icon {
        if (!this::_icon.isInitialized) {
            _icon = OpenEmojiUtils.getIcon(code, isCustom)
        }
        return _icon
    }

    fun getIconPath(): String {
        return OpenEmojiUtils.getIconPath(code, isCustom).replace("\\", "/")
    }

    fun custom() {
        custom(true)
    }

    fun custom(isCustom: Boolean) {
        this.isCustom = isCustom
    }

    fun getCustom(): Boolean {
        return this.isCustom
    }

}