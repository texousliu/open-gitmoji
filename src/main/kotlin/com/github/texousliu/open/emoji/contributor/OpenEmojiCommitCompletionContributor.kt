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
import com.intellij.psi.PsiPlainText
import com.intellij.util.ProcessingContext

class OpenEmojiCommitCompletionContributor : CompletionContributor() {

    private val openEmojiInsertHandler = OpenEmojiInsertHandler()

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(PsiPlainText::class.java),
            object : CompletionProvider<CompletionParameters>() {

                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    // 如果不是多行文本输入框，直接返回
                    if (parameters.editor.isOneLineMode) return

                    val doc = parameters.editor.document.charsSequence
                    if (doc.isEmpty()) return

                    val persistent = OpenEmojiPersistent.getInstance()
                    val str = result.prefixMatcher.prefix
                    if (str.isEmpty()) return

                    if (persistent.getTriggerWithColon()) {
                        if (!doc.contains(":$str") && !doc.contains("：$str")) return
                    }

                    var emojiPatterns = persistent.getOpenEmojiPatterns()
                        .filter { it.enable && it.enableCommit }.toMutableList()
                    if (emojiPatterns.isEmpty()) {
                        emojiPatterns = mutableListOf(persistent.getDefaultOpenEmojiPattern(WorkEnv.COMMIT))
                    }

                    // 预置 git emoji
                    persistent.getOpenEmojiInfoList().filter { it.enable }
                        .forEach { emoji -> emojiToPrompt(emoji, result, emojiPatterns) }
                }
            })
    }

    private fun emojiToPrompt(
        emoji: OpenEmojiInfo,
        result: CompletionResultSet,
        emojiPatterns: MutableList<OpenEmojiPattern>
    ) {
        emojiPatterns.forEach { pattern ->
            val str = lookupString(emoji, pattern)
            result.addElement(
                LookupElementBuilder
                    .create(emoji, "${str}${OpenEmojiUtils.REPLACE_SUFFIX_MARK}")
                    .withPresentableText(str)
                    //.withTailText(emoji.description)
                    .withTypeText(emoji.description)
                    .withLookupStrings(
                        listOf(
                            emoji.code.lowercase(),
                            emoji.description.lowercase(),
                            emoji.cnDescription.lowercase(),
                            emoji.name.lowercase(),
                            emoji.entity.lowercase()
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

}