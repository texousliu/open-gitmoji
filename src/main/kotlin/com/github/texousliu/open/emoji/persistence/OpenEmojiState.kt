package com.github.texousliu.open.emoji.persistence

import com.github.texousliu.open.emoji.model.OpenEmojiPattern
import com.intellij.util.xmlb.annotations.OptionTag

/**
 * insert description here
 *
 * @author liuxiaohua
 * @since 2023-11-20
 */
class OpenEmojiState(
    val triggerWithColon: Boolean = false,
    val customEmojiDirectory: String = "",
    @OptionTag(converter = OpenEmojiPatternListConverter::class)
    val openEmojiPatterns: MutableList<OpenEmojiPattern> = mutableListOf()
) {


}