package com.github.texousliu.open.emoji.model

import com.github.texousliu.open.emoji.persistence.OpenEmojiPersistent
import com.google.gson.annotations.SerializedName
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon
import javax.swing.ImageIcon

class OpenEmoji(
    val emoji: String,
    val entity: String,
    val code: String,
    val name: String,
    val description: String,
    @SerializedName("cn_description")
    val cnDescription: String
) {

    private var isCustom = false
    private lateinit var _icon: Icon

    fun getIcon(): Icon {
        if (!this::_icon.isInitialized) {
            _icon = if (isCustom) getCustomIcon() else
                IconLoader.getIcon("/icons/emojis/${code.replace(":".toRegex(), "")}.png", OpenEmoji::class.java)
        }
        return _icon
    }

    fun custom() {
        isCustom = true
    }

    private fun getCustomIcon(): Icon {
        try {
            return ImageIcon(
                "${
                    OpenEmojiPersistent.getInstance().getCustomEmojiDirectory()
                }/icons/${code.replace(":".toRegex(), "")}.png"
            )
        } catch (e: Exception) {
            TODO("Not yet implemented")
        }
    }

}