package com.github.texousliu.open.emoji.model

import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import com.google.gson.annotations.SerializedName
import javax.swing.Icon

open class OpenEmoji(
        var emoji: String,
        var entity: String,
        val code: String,
        var name: String,
        var description: String,
        @SerializedName("cn_description")
        var cnDescription: String
) {

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