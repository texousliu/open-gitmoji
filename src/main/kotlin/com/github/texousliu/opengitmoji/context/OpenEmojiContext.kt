package com.github.texousliu.opengitmoji.context

import com.github.texousliu.opengitmoji.model.OpenEmoji
import com.github.texousliu.opengitmoji.model.OpenEmojiList
import com.github.texousliu.opengitmoji.persistence.OpenEmojiPersistent
import com.github.texousliu.opengitmoji.utils.OpenEmojiUtils
import com.google.gson.Gson
import java.io.File

object OpenEmojiContext {

    private val openEmojiList = ArrayList<OpenEmoji>()
    private val customEmojiList = ArrayList<OpenEmoji>()

    init {
        loadEmojis()
        loadCustomEmojis()
    }

    fun emojis(): List<OpenEmoji> {
        return openEmojiList
    }

    fun customEmojis(): List<OpenEmoji> {
        return customEmojiList
    }

    private fun loadEmojis() {
        javaClass.getResourceAsStream("/${OpenEmojiUtils.EMOJI_FILE_NAME}").use { inputStream ->
            if (inputStream != null) {
                val text = inputStream.bufferedReader().readText()
                Gson().fromJson(text, OpenEmojiList::class.java).also {
                    it.emojis.forEach(openEmojiList::add)
                }
            }
        }
    }

    fun loadCustomEmojis() {
        customEmojiList.clear()
        val directory = OpenEmojiPersistent.getInstance().getCustomEmojiDirectory()
        if (directory.trim().isEmpty()) return

        val file = File("${directory}/${OpenEmojiUtils.EMOJI_FILE_NAME}")
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