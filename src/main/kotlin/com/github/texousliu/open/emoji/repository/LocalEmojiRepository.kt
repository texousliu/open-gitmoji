package com.github.texousliu.open.emoji.repository

import com.github.texousliu.open.emoji.model.OpenEmoji
import com.github.texousliu.open.emoji.model.OpenEmojiData
import com.github.texousliu.open.emoji.model.EmojiSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class LocalEmojiRepository(
    private val directory: String
) : EmojiRepository {

    override val name: String = "Local: $directory"
    override val priority: Int = 30

    override fun isAvailable(): Boolean {
        if (directory.isBlank()) return false
        return File(directory, "emojis.json").exists()
    }

    override fun load(): List<OpenEmoji> {
        val result = mutableListOf<OpenEmoji>()
        if (!isAvailable()) return result

        val file = File(directory, "emojis.json")
        file.inputStream().use { stream ->
            val text = stream.bufferedReader().readText()
            val listType = object : TypeToken<Map<String, List<OpenEmojiData>>>() {}.type
            val map: Map<String, List<OpenEmojiData>> = Gson().fromJson(text, listType)
            map["emojis"]?.forEach { data ->
                val iconName = data.code.replace(":", "")
                val iconPath = "$directory/icons/$iconName.png"
                result.add(OpenEmoji(data, EmojiSource.CUSTOM, iconPath))
            }
        }
        return result
    }
}
