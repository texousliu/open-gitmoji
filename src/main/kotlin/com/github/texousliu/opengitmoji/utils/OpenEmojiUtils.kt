package com.github.texousliu.opengitmoji.utils

import com.github.texousliu.opengitmoji.context.OpenEmojiContext
import com.github.texousliu.opengitmoji.model.OpenEmoji
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object OpenEmojiUtils {

    const val G = "#{G}"
    const val GU = "#{GU}"
    const val DESC = "#{DESC}"
    const val DESC_CN = "#{DESC_CN}"
    private const val DATE = "#{DATE}"
    private const val TIME = "#{TIME}"

    private val PATTERNS = arrayOf(G, GU, DESC, DESC_CN, DATE, TIME)

    fun demo(script: String): String {
        return replace(script, OpenEmojiContext.gms()[0])
    }

    fun replace(script: String, openEmoji: OpenEmoji): String {
        val params = mapOf(G to openEmoji.emoji, GU to openEmoji.code,
                DESC to openEmoji.description, DESC_CN to openEmoji.cnDescription,
                DATE to date(), TIME to time())
        return replace(script, params)
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

}