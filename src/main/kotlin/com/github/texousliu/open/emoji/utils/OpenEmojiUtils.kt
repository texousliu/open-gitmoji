package com.github.texousliu.open.emoji.utils

import com.github.texousliu.open.emoji.context.OpenEmojiContext
import com.github.texousliu.open.emoji.model.OpenEmoji
import com.github.texousliu.open.emoji.model.OpenEmojiPattern
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object OpenEmojiUtils {

    const val EMOJI_FILE_NAME = "emojis.json"
    const val REPLACE_SUFFIX_MARK = "$$:$$"

    const val G = "#{G}"
    const val GU = "#{GU}"
    const val DESC = "#{DESC}"
    const val DESC_CN = "#{DESC_CN}"
    private const val DATE = "#{DATE}"
    private const val TIME = "#{TIME}"

    private val PATTERNS = arrayOf(G, GU, DESC, DESC_CN, DATE, TIME)

    fun demo(pattern: String): String {
        return replace(pattern, OpenEmojiContext.emojis()[0])
    }

    fun replace(pattern: String, emoji: OpenEmoji): String {
        val params = mapOf(
            G to emoji.emoji, GU to emoji.code,
            DESC to emoji.description, DESC_CN to emoji.cnDescription,
            DATE to date(), TIME to time()
        )
        return replace(pattern, params)
    }

    private fun replace(script: String, replace: Map<String, String>): String {
        var r = script
        for (pattern in PATTERNS) {
            r = r.replace(pattern, replace[pattern] ?: pattern)
        }
        return r
    }

    private fun date(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return current.format(formatter)
    }

    private fun time(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return current.format(formatter)
    }

    class ListTypeToken : TypeToken<MutableList<OpenEmojiPattern>>()

}