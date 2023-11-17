package com.github.texousliu.opengitmoji.context

import com.github.texousliu.opengitmoji.model.OpenEmoji
import com.github.texousliu.opengitmoji.model.OpenEmojiList
import com.google.gson.Gson
import java.io.File

object OpenEmojiCustomContext {

    private val customEmojiList = ArrayList<OpenEmoji>()

    init {
        loadCustomEmojis()
    }

    fun gms(): List<OpenEmoji> {
        return customEmojiList
    }

    fun loadCustomEmojis() {
        customEmojiList.clear()
        if (OpenEmojiContext.getCustomEmojiFolder().trim().isEmpty()) return

        val file = File(OpenEmojiContext.getCustomEmojiFolder() + "/${OpenEmojiContext.EMOJI_FILE_NAME}")
        if (file.exists()) {
            file.inputStream().use { inputStream ->
                val text = inputStream.bufferedReader().readText()
                Gson().fromJson(text, OpenEmojiList::class.java).also { gmList ->
                    if (gmList?.emojis != null) {
                        gmList.emojis.forEach { gm ->
                            gm.custom()
                            customEmojiList.add(gm)
                        }
                    }
                }
            }
        }
    }

}