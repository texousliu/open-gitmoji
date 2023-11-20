package com.github.texousliu.opengitmoji.persistence

import com.github.texousliu.opengitmoji.model.OpenEmojiPattern
import com.github.texousliu.opengitmoji.utils.OpenEmojiUtils
import com.google.gson.Gson
import com.intellij.util.xmlb.Converter


/**
 * insert description here
 *
 * @author liuxiaohua
 * @since 2023-11-20
 */
class OpenEmojiPatternListConverter : Converter<MutableList<OpenEmojiPattern>>() {

    override fun toString(value: MutableList<OpenEmojiPattern>): String? {
        return Gson().toJson(value)
    }

    override fun fromString(value: String): MutableList<OpenEmojiPattern>? {
        return Gson().fromJson(value, OpenEmojiUtils.ListTypeToken().type)
    }

}