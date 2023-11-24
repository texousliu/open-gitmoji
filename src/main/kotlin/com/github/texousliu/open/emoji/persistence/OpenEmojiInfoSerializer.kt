package com.github.texousliu.open.emoji.persistence

import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import com.google.gson.*
import java.lang.reflect.Type
import java.util.*

class OpenEmojiInfoSerializer : JsonSerializer<OpenEmojiInfo>, JsonDeserializer<OpenEmojiInfo> {

    override fun serialize(emojiInfo: OpenEmojiInfo?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
        val toJsonTree = OpenEmojiUtils.GSON.toJsonTree(emojiInfo).asJsonObject
        val emoji = toJsonTree?.get("emoji")?.asString
        toJsonTree.addProperty("emoji", serializer(emoji))
        return toJsonTree
    }

    override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): OpenEmojiInfo {
        val fromJson = OpenEmojiUtils.GSON.fromJson(p0?.asJsonObject, OpenEmojiInfo::class.java)
        fromJson?.emoji = deserializer(fromJson?.emoji)
        return fromJson
    }

    private fun serializer(str: String?): String? {
        if (str == null) return null
        return Base64.getEncoder().encodeToString(str.toByteArray())
    }

    private fun deserializer(str: String?): String {
        if (str == null) return ""
        return try {
            val bytes = Base64.getDecoder().decode(str)
            String(bytes)
        } catch (e: Exception) {
            str
        }
    }

}