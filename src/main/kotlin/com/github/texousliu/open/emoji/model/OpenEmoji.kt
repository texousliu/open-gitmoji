package com.github.texousliu.open.emoji.model

import com.github.texousliu.open.emoji.persistence.OpenEmojiPersistent
import com.google.gson.annotations.SerializedName
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import java.io.File
import javax.swing.Icon
import javax.swing.ImageIcon

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
        return try {
            val filePath = "${
                OpenEmojiPersistent.getInstance().getCustomEmojiDirectory()
            }/icons/${code.replace(":".toRegex(), "")}.png"

            if (!File(filePath).exists()) return AllIcons.Actions.Refresh

            return ImageIcon(filePath)
        } catch (e: Exception) {
            AllIcons.Actions.Refresh
        }
    }

}