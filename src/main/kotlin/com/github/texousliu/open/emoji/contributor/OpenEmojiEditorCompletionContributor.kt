package com.github.texousliu.open.emoji.contributor

import com.github.texousliu.open.emoji.constants.WorkEnv
import com.github.texousliu.open.emoji.model.OpenEmoji
import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.github.texousliu.open.emoji.model.OpenEmojiPattern
import com.github.texousliu.open.emoji.persistence.OpenEmojiPersistent
import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class OpenEmojiEditorCompletionContributor : CompletionContributor() {

    private val openEmojiInsertHandler = OpenEmojiInsertHandler()

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            object : CompletionProvider<CompletionParameters>() {

                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val persistent = OpenEmojiPersistent.getInstance()
                    // 校验开关 编写
                    if (!persistent.getEditorEmojiSupported()) return

                    // 获取匹配文本
                    val matcherString = result.prefixMatcher.prefix
                    if (matcherString.isEmpty()) return

                    // 获取输入文本，以 space 或者 colon 或者中文 colon
                    val inputString = handlerString(handlerString(handlerString(matcherString, ":"), "："), " ")

                    // 如果配置了以 colon 开头
                    if (persistent.getTriggerWithColon()) {
                        // 获取输入 offset
                        val doc = parameters.editor.document.charsSequence
                        val offset = parameters.editor.caretModel.offset

                        // 获取输入及之前的文本
                        val text = doc.substring(0, offset)

                        // 判断是否是以 colon with input 结尾
                        if (!text.endsWith(":$inputString") && !text.equals("“：$inputString")) return
                    }

                    // 兼容代码提示需要在前面补全的字符
                    val needAppendString = matcherString.substring(0, matcherString.lastIndexOf(inputString))

                    // 获取 emoji 表达式
                    var emojiPatterns = persistent.getOpenEmojiPatterns()
                        .filter { it.enable && it.enableEditor }.toMutableList()
                    if (emojiPatterns.isEmpty()) {
                        emojiPatterns = mutableListOf(persistent.getDefaultOpenEmojiPattern(WorkEnv.EDITOR))
                    }

                    // 预置 git emoji
                    persistent.getOpenEmojiInfoList().filter { it.enable }
                        .forEach { emoji ->
                            emojiToPrompt(emoji, result, emojiPatterns, needAppendString)
                        }
                }
            })
    }

    private fun emojiToPrompt(
        emoji: OpenEmojiInfo,
        result: CompletionResultSet,
        emojiPatterns: MutableList<OpenEmojiPattern>,
        needAppendString: String
    ) {
        emojiPatterns.forEach { pattern ->
            val str = lookupString(emoji, pattern)
            result.addElement(
                LookupElementBuilder
                    .create(emoji, "${needAppendString}${str}${OpenEmojiUtils.REPLACE_SUFFIX_MARK}")
                    .withPresentableText(str)
                    // .withTailText(emoji.description)
                    .withTypeText(emoji.description)
                    .withLookupStrings(
                        listOf(
                            "${needAppendString}${emoji.code.lowercase()}",
                            "${needAppendString}${emoji.description.lowercase()}",
                            "${needAppendString}${emoji.cnDescription.lowercase()}",
                            "${needAppendString}${emoji.name.lowercase()}",
                            "${needAppendString}${emoji.entity.lowercase()}"
                        )
                    )
                    .withIcon(emoji.getIcon())
                    .withInsertHandler(openEmojiInsertHandler)
            )
        }
    }

    private fun lookupString(emoji: OpenEmoji, pattern: OpenEmojiPattern): String {
        return OpenEmojiUtils.replace(pattern.pattern, emoji)
    }

    private fun handlerString(str: String, split: String): String {
        val lastIndexOf = str.lastIndexOf(split)
        return if (lastIndexOf > -1) str.substring(lastIndexOf + 1) else str
    }

}