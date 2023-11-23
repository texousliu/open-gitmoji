package com.github.texousliu.open.emoji.persistence

import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import com.google.gson.Gson
import com.intellij.util.xmlb.Converter


/**
 * insert description here
 *
 * @author liuxiaohua
 * @since 2023-11-20
 */
class OpenEmojiInfoListConverter : Converter<MutableList<OpenEmojiInfo>>() {

    override fun toString(value: MutableList<OpenEmojiInfo>): String? {
        return Gson().toJson(value)
    }

    override fun fromString(value: String): MutableList<OpenEmojiInfo>? {
        return Gson().fromJson(value, OpenEmojiUtils.OpenEmojiInfoListTypeToken().type)
    }

}