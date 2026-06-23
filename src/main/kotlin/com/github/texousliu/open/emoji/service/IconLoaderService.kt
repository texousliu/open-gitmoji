package com.github.texousliu.open.emoji.service

import com.github.texousliu.open.emoji.model.OpenEmoji
import com.github.texousliu.open.emoji.model.EmojiSource
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon
import javax.swing.ImageIcon
import java.io.File

/**
 * Service responsible for resolving and loading emoji icons.
 *
 * Decouples icon loading from the model layer. Supports both built-in
 * resources and custom/local file system paths.
 */
object IconLoaderService {

    /**
     * Resolves the [Icon] for the given [OpenEmoji].
     *
     * - Built-in emojis load from plugin resources.
     * - Custom/local emojis load from the file system.
     * - If the icon file is missing, a fallback icon is returned.
     */
    fun getIcon(emoji: OpenEmoji): Icon {
        return when (emoji.source) {
            EmojiSource.CUSTOM, EmojiSource.OVERRIDE -> {
                emoji.iconPath?.let { loadCustomIcon(it) } ?: fallbackIcon()
            }
            else -> {
                val resourcePath = "/icons/emojis/${emoji.iconName()}.png"
                IconLoader.getIcon(resourcePath, OpenEmoji::class.java) ?: fallbackIcon()
            }
        }
    }

    /**
     * Returns the resolved icon path for the given emoji.
     */
    fun getIconPath(emoji: OpenEmoji): String {
        return when (emoji.source) {
            EmojiSource.CUSTOM, EmojiSource.OVERRIDE -> {
                emoji.iconPath ?: "/icons/emojis/${emoji.iconName()}.png"
            }
            else -> "/icons/emojis/${emoji.iconName()}.png"
        }
    }

    private fun loadCustomIcon(filePath: String): Icon {
        return try {
            val file = File(filePath)
            if (file.exists()) ImageIcon(filePath) else fallbackIcon()
        } catch (e: Exception) {
            fallbackIcon()
        }
    }

    private fun fallbackIcon(): Icon = AllIcons.Actions.Refresh
}
