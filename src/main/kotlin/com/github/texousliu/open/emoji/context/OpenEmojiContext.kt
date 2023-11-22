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
        restoreCustomEmoji()
    }

    fun emojis(): List<OpenEmoji> {
        return openEmojiList
    }

    fun customEmojis(): List<OpenEmoji> {
        return customEmojiList
    }

    fun emojiInfoList(): List<OpenEmojiInfo> {
        val emojiInfoList = mutableListOf<OpenEmojiInfo>()
        emojis().forEach {
            emojiInfoList.add(OpenEmojiInfo(it))
        }

        customEmojis().forEach {
            val openEmojiInfo = OpenEmojiInfo(it)
            val indexOf = emojiInfoList.indexOf(openEmojiInfo)
            if (indexOf < 0) {
                openEmojiInfo.type = OpenEmojiInfoType.CUSTOM
                emojiInfoList.add(openEmojiInfo)
            } else {
                openEmojiInfo.type = OpenEmojiInfoType.OVERRIDE
                emojiInfoList[indexOf] = openEmojiInfo
            }
        }
        return emojiInfoList
    }

    fun restoreCustomEmoji() {
        customEmojiList.clear()
        customEmojiList.addAll(
            loadCustomEmojisFromDirectory(
                OpenEmojiPersistent.getInstance().getCustomEmojiDirectory()
            )
        )
    }

    fun refreshEmojiInfoList(directory: String, emojiInfoList: MutableList<OpenEmojiInfo>) {
        // 获取所有自定义 emoji
        val customEmojis = loadCustomEmojisFromDirectory(directory)
        customEmojis.forEach {
            val emojiInfo = OpenEmojiInfo(it)
            val index = emojiInfoList.indexOf(emojiInfo)
            if (index < 0) {
                emojiInfo.type = OpenEmojiInfoType.CUSTOM
                emojiInfoList.add(emojiInfo)
            } else {
                val oldEmojiInfo = emojiInfoList[index]
                if (oldEmojiInfo.type != OpenEmojiInfoType.CUSTOM) {
                    emojiInfo.type = OpenEmojiInfoType.OVERRIDE
                    emojiInfo.enable = oldEmojiInfo.enable
                    emojiInfoList[index] = emojiInfo
                }
            }
        }
    }

    private fun loadCustomEmojisFromDirectory(directory: String): MutableList<OpenEmoji> {
        val result = mutableListOf<OpenEmoji>()
        if (directory.trim().isEmpty()) return result
        val file = File("${directory}/${OpenEmojiUtils.EMOJI_FILE_NAME}")
        if (file.exists()) {
            file.inputStream().use { inputStream ->
                val text = inputStream.bufferedReader().readText()
                Gson().fromJson(text, OpenEmojiList::class.java).also { gmList ->
                    if (gmList?.emojis != null) {
                        gmList.emojis.forEach { gm ->
                            gm.custom()
                            result.add(gm)
                        }
                    }
                }
            }
        }
        return result
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