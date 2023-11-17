package com.github.texousliu.opengitmoji.context

import com.github.texousliu.opengitmoji.model.GM
import com.github.texousliu.opengitmoji.model.GMList
import com.google.gson.Gson
import java.io.File

// TODO 界面上添加刷新按钮直接刷新文件夹
object OpenGitmojiCustomContext {

    private val customEmojiList = ArrayList<GM>()

    init {
        loadCustomEmojis()
    }

    fun gms(): List<GM> {
        return customEmojiList
    }

    fun loadCustomEmojis() {
        customEmojiList.clear()
        if (OpenGitmojiContext.getCustomEmojiFolder().trim().isEmpty()) return

        val file = File(OpenGitmojiContext.getCustomEmojiFolder() + "/${OpenGitmojiContext.EMOJI_FILE_NAME}")
        if (file.exists()) {
            file.inputStream().use { inputStream ->
                val text = inputStream.bufferedReader().readText()
                Gson().fromJson(text, GMList::class.java).also { gmList ->
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