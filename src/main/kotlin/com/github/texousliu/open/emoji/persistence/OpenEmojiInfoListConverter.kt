package com.github.texousliu.open.emoji.persistence

import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import com.google.gson.Gson
import com.intellij.util.xmlb.Converter
import java.util.*
import java.util.stream.Collectors
import kotlin.streams.toList


/**
 * insert description here
 *
 * @author liuxiaohua
 * @since 2023-11-20
 */
class OpenEmojiInfoListConverter : Converter<MutableList<OpenEmojiInfo>>() {

    override fun toString(value: MutableList<OpenEmojiInfo>): String? {
        val storage = value.stream().map {
            val clone = it.clone()
            clone.emoji = serializer(clone.emoji)
            clone
        }.toList()
        return Gson().toJson(storage)
    }

    override fun fromString(value: String): MutableList<OpenEmojiInfo>? {
        val emojiInfoList = fromStringInternal(value)
        return emojiInfoList?.stream()?.map {
            val clone = it.clone()
            clone.emoji = deserializer(clone.emoji)
            clone
        }?.collect(Collectors.toList())
    }

    private fun fromStringInternal(value: String): MutableList<OpenEmojiInfo>? {
        return Gson().fromJson(value, OpenEmojiUtils.OpenEmojiInfoListTypeToken().type)
    }


    private fun serializer(str: String): String {
        return Base64.getEncoder().encodeToString(str.toByteArray())
    }

    private fun deserializer(str: String): String {
        return try {
            val bytes = Base64.getDecoder().decode(str)
            String(bytes)
        } catch (e: Exception) {
            str
        }
    }

}