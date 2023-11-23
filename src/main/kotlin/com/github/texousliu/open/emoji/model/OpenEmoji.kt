package com.github.texousliu.open.emoji.model

import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import com.google.gson.annotations.SerializedName
import javax.swing.Icon

open class OpenEmoji(
    val emoji: String,
    val entity: String,
    val code: String,
    val name: String,
    val description: String,
    @SerializedName("cn_description")
    val cnDescription: String
) {

    private var isCustom = false
    @Transient
    private lateinit var _icon: Icon

    fun getIcon(): Icon {
        if (!this::_icon.isInitialized) {
            _icon = OpenEmojiUtils.getIcon(code.replace(":".toRegex(), ""), isCustom)
        }
        return _icon
    }

    fun custom() {
        isCustom = true
    }

}