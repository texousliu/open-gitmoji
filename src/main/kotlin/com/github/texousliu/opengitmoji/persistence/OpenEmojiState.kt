package com.github.texousliu.opengitmoji.persistence

import com.github.texousliu.opengitmoji.model.OpenEmojiPattern
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