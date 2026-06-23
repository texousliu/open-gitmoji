package com.github.texousliu.open.emoji.repository

import com.github.texousliu.open.emoji.model.OpenEmoji
import com.github.texousliu.open.emoji.model.OpenEmojiData
import com.github.texousliu.open.emoji.model.EmojiSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Loads the built-in emoji definitions shipped with the plugin.
 *
 * Reads `/emojis.json` from plugin resources.
 */
class BuiltInEmojiRepository : EmojiRepository {

    override val name: String = "Built-in"
    override val priority: Int = 10

    override fun isAvailable(): Boolean = true

    override fun load(): List<OpenEmoji> {
        val result = mutableListOf<OpenEmoji>()
        javaClass.getResourceAsStream("/emojis.json")?.use { stream ->
            val text = stream.bufferedReader().readText()
            val listType = object : TypeToken<Map<String, List<OpenEmojiData>>>() {}.type
            val map: Map<String, List<OpenEmojiData>> = Gson().fromJson(text, listType)
            map["emojis"]?.forEach { data ->
                result.add(OpenEmoji(data, EmojiSource.DEFAULT))
            }
        }
        return result
    }
}
