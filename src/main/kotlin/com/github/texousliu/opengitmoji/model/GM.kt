package com.github.texousliu.opengitmoji.model

import com.github.texousliu.opengitmoji.context.OpenGitmojiContext
import com.google.gson.annotations.SerializedName
import com.intellij.openapi.util.CachedImageIcon
import com.intellij.openapi.util.IconLoader
import io.ktor.http.*
import java.net.URL
import javax.swing.Icon
import javax.swing.ImageIcon

class GM(
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
                IconLoader.getIcon("/icons/gitmoji/${code.replace(":".toRegex(), "")}.png", GM::class.java)
        }
        return _icon
    }

    fun custom() {
        isCustom = true
    }

    private fun getCustomIcon(): Icon {
        try {
            return ImageIcon(OpenGitmojiContext.getCustomEmojiFolder() + "/icons/${code.replace(":".toRegex(), "")}.png")
        } catch (e: Exception) {
            TODO("Not yet implemented")
            println(e)
        }
    }

}