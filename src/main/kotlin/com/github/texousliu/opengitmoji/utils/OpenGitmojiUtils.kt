package com.github.texousliu.opengitmoji.utils

import com.github.texousliu.opengitmoji.context.OpenGitmojiContext
import com.github.texousliu.opengitmoji.model.GM
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object OpenGitmojiUtils {

    const val G = "#{G}"
    const val GU = "#{GU}"
    const val DESC = "#{DESC}"
    const val DESC_CN = "#{DESC_CN}"
    private const val DATE = "#{DATE}"
    private const val TIME = "#{TIME}"

    private val REGEX = arrayOf(G, GU, DESC, DESC_CN, DATE, TIME)

    fun demo(script: String): String {
        return replace(script, OpenGitmojiContext.gms()[0])
    }

    fun replace(script: String, gm: GM): String {
        val params = mapOf(G to gm.emoji, GU to gm.code,
                DESC to gm.description, DESC_CN to gm.cn_description,
                DATE to date(), TIME to time())
        return replace(script, params)
    }

    private fun replace(script: String, replace: Map<String, String>): String {
        var r = script
        for (regex1 in REGEX) {
            r = r.replace(regex1, replace[regex1] ?: regex1)
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