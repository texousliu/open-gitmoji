package com.github.texousliu.open.emoji.context

import com.github.texousliu.open.emoji.model.OpenEmoji
import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.github.texousliu.open.emoji.model.OpenEmojiInfoType
import com.github.texousliu.open.emoji.model.OpenEmojiList
import com.github.texousliu.open.emoji.persistence.OpenEmojiPersistent
import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
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

    fun emojiInfos(): List<OpenEmojiInfo> {
        val emojiInfos = mutableListOf<OpenEmojiInfo>()
        emojis().forEach {
            emojiInfos.add(OpenEmojiInfo(it))
        }

        customEmojis().forEach {
            val openEmojiInfo = OpenEmojiInfo(it)
            val indexOf = emojiInfos.indexOf(openEmojiInfo)
            if (indexOf < 0) {
                openEmojiInfo.type = OpenEmojiInfoType.CUSTOM
                emojiInfos.add(openEmojiInfo)
            } else {
                openEmojiInfo.type = OpenEmojiInfoType.OVERRIDE
                emojiInfos.set(indexOf, openEmojiInfo)
            }
        }
        return emojiInfos
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

}