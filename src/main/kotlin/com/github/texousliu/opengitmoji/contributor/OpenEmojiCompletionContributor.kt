package com.github.texousliu.opengitmoji.contributor

import com.github.texousliu.opengitmoji.context.OpenEmojiContext
import com.github.texousliu.opengitmoji.context.OpenEmojiCustomContext
import com.github.texousliu.opengitmoji.model.OpenEmoji
import com.github.texousliu.opengitmoji.model.OpenEmojiPattern
import com.github.texousliu.opengitmoji.utils.OpenEmojiUtils
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiPlainText
import com.intellij.util.ProcessingContext

class OpenEmojiCompletionContributor : CompletionContributor() {

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
                        if (OpenEmojiContext.getTriggerWithColon()) {
                            val str = result.prefixMatcher.prefix
                            if (str.isEmpty()) return
                            if (!doc.contains(":$str") && !doc.contains("：$str")) return
                        }

                        val gitmojiPatterns = OpenEmojiContext.getEmojiPatterns()

                        // 预置 git emoji
                        OpenEmojiContext.gms().forEach { gm -> gmToPrompt(gm, result, gitmojiPatterns) }
                        // 自定义 git emoji
                        OpenEmojiCustomContext.gms().forEach{ gm -> gmToPrompt(gm, result, gitmojiPatterns) }
                    }
                })
    }

    private fun gmToPrompt(openEmoji: OpenEmoji, result: CompletionResultSet, openEmojiPatterns: MutableList<OpenEmojiPattern>) {
        openEmojiPatterns.stream().filter { it.enable }.forEach { row ->
            val str = lookupString(openEmoji, row)
            result.addElement(LookupElementBuilder
                    .create(openEmoji, "${str}${OpenEmojiContext.REPLACE_SUFFIX_MARK}")
                    .withPresentableText(str)
//                                        .withTailText(gm.description)
                    .withTypeText(openEmoji.description)
                    .withLookupStrings(
                            listOf(
                                    openEmoji.code.lowercase(),
                                    openEmoji.description.lowercase(),
                                    openEmoji.cnDescription.lowercase(),
                                    openEmoji.name.lowercase(),
                                    openEmoji.entity.lowercase()
                            )
                    )
                    .withIcon(openEmoji.getIcon())
                    .withInsertHandler(openEmojiInsertHandler)
            )
        }
    }

    private fun lookupString(it: OpenEmoji, pattern: OpenEmojiPattern): String {
        return OpenEmojiUtils.replace(pattern.pattern, it)
    }

}