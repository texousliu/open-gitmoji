package com.github.texousliu.open.emoji.persistence

import com.github.texousliu.open.emoji.model.EmojiConfigState
import com.github.texousliu.open.emoji.model.EmojiSource
import com.github.texousliu.open.emoji.model.OpenEmoji
import com.github.texousliu.open.emoji.model.OpenEmojiData
import com.google.gson.*
import java.lang.reflect.Type
import java.util.*

/**
 * GSON 序列化器/反序列化器，用于 [EmojiConfigState] 的 JSON 持久化。
 *
 * 序列化时将 emoji 字段做 Base64 编码（因为 emoji 可能包含多字节 Unicode 字符）。
 * 反序列化时解码并重建完整的 EmojiConfigState 对象。
 */
class OpenEmojiInfoSerializer : JsonSerializer<EmojiConfigState>, JsonDeserializer<EmojiConfigState> {

    override fun serialize(
        configState: EmojiConfigState?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        if (configState == null) return JsonNull.INSTANCE
        val obj = JsonObject()

        val emoji = configState.emoji
        val data = emoji.data

        obj.addProperty("emoji", base64Encode(data.emoji))
        obj.addProperty("entity", data.entity)
        obj.addProperty("code", data.code)
        obj.addProperty("name", data.name)
        obj.addProperty("description", data.description)
        obj.addProperty("cn_description", data.cnDescription)
        data.semver?.let { obj.addProperty("semver", it) }
        obj.addProperty("source", emoji.source.name)
        emoji.iconPath?.let { obj.addProperty("iconPath", it) }
        obj.addProperty("enabled", configState.enabled)
        obj.addProperty("dirty", configState.dirty)

        return obj
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): EmojiConfigState {
        if (json == null || json.isJsonNull) {
            return createDefaultState()
        }
        val obj = json.asJsonObject

        val data = OpenEmojiData(
            emoji = base64Decode(obj.get("emoji")?.asString ?: ""),
            entity = obj.get("entity")?.asString ?: "",
            code = obj.get("code")?.asString ?: "",
            name = obj.get("name")?.asString ?: "",
            description = obj.get("description")?.asString ?: "",
            cnDescription = obj.get("cn_description")?.asString ?: "",
            semver = obj.get("semver")?.asString
        )

        val source = try {
            EmojiSource.valueOf(obj.get("source")?.asString ?: "DEFAULT")
        } catch (e: Exception) {
            EmojiSource.DEFAULT
        }

        val iconPath = obj.get("iconPath")?.asString
        val emoji = OpenEmoji(data, source, iconPath)

        val enabled = obj.get("enabled")?.asBoolean ?: true
        val dirty = obj.get("dirty")?.asBoolean ?: false

        return EmojiConfigState(emoji, enabled, dirty)
    }

    private fun base64Encode(str: String?): String? {
        if (str == null) return null
        return Base64.getEncoder().encodeToString(str.toByteArray(Charsets.UTF_8))
    }

    private fun base64Decode(str: String): String {
        return try {
            val bytes = Base64.getDecoder().decode(str)
            String(bytes, Charsets.UTF_8)
        } catch (e: Exception) {
            str
        }
    }

    private fun createDefaultState(): EmojiConfigState {
        val data = OpenEmojiData("", "", "", "", "", "")
        return EmojiConfigState(OpenEmoji(data))
    }

}
