package com.github.texousliu.open.emoji.context

import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.github.texousliu.open.emoji.persistence.OpenEmojiPersistent
import com.github.texousliu.open.emoji.utils.OpenEmojiUtils

object OpenEmojiCache {

    private val emojiInfoList = mutableListOf<OpenEmojiInfo>()

    init {
        refresh(OpenEmojiPersistent.getInstance().getCustomEmojiDirectory())
    }

    fun emojiInfoList(): MutableList<OpenEmojiInfo> {
        return emojiInfoList
    }

    fun refresh(directory: String) {
        emojiInfoList.clear()
        emojiInfoList.addAll(OpenEmojiUtils.emojiInfoList(directory))
    }

}