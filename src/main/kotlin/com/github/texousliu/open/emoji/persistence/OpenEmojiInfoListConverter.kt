package com.github.texousliu.open.emoji.persistence

import com.github.texousliu.open.emoji.model.EmojiConfigState
import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import com.intellij.util.xmlb.Converter


/**
 * XML 持久化转换器，用于 [MutableList] of [EmojiConfigState] 与 JSON 字符串之间的转换。
 *
 * @author liuxiaohua
 * @since 2023-11-20
 */
class OpenEmojiInfoListConverter : Converter<MutableList<EmojiConfigState>>() {

    override fun toString(value: MutableList<EmojiConfigState>): String? {
        return OpenEmojiUtils.GSON_INFO.toJson(value)
    }

    override fun fromString(value: String): MutableList<EmojiConfigState>? {
        return OpenEmojiUtils.GSON_INFO.fromJson(value, OpenEmojiUtils.OpenEmojiInfoListTypeToken().type)
    }

}
