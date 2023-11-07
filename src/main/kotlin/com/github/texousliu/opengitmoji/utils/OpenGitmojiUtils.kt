package com.github.texousliu.opengitmoji.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.github.texousliu.opengitmoji.context.OpenGMContext
import com.github.texousliu.opengitmoji.context.OpenGitmojiContext
import com.github.texousliu.opengitmoji.model.GM
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object OpenGitmojiUtils {

    val G = "#{G}"
    val GU = "#{GU}"
    val DESC = "#{DESC}"
    val DESC_CN = "#{DESC_CN}"
    val DATE = "#{DATE}"
    val TIME = "#{TIME}"

    val REGEX = arrayOf(G, GU, DESC, DESC_CN, DATE, TIME)
    private val GM = OpenGMContext.gms().get(0)

    fun demo(script: String): String {
        return replace(script, OpenGitmojiContext.gms()[0])
    }

    fun replace(script: String, gm : GM) : String {
        val params = mapOf(G to gm.emoji, GU to gm.code,
                DESC to gm.description, DESC_CN to gm.cn_description,
                DATE to date(), TIME to time())
        return replace(script, params)
    }

    fun replace(script: String, replace: Map<String, String>): String {
        var r = script
        for (regex1 in REGEX) {
            r = r.replace(regex1, replace[regex1] ?: regex1)
        }
        return r
    }

    fun date(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return current.format(formatter)
    }

    fun time(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return current.format(formatter)
    }

    val om = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
            .setSerializationInclusion(JsonInclude.Include.ALWAYS)

    fun toJson(data : Any) : String {
        return om.writeValueAsString(data)
    }

    fun <T> fromJson(json : String, tCls : Class<T>) : T {
        return om.readValue(json, tCls)
    }

}