package com.github.texousliu.opengitmoji.contributor

import com.github.texousliu.opengitmoji.context.OpenGitmojiContext
import com.github.texousliu.opengitmoji.model.GM
import com.github.texousliu.opengitmoji.model.GitmojiPattern
import com.github.texousliu.opengitmoji.utils.OpenGitmojiUtils
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiPlainText
import com.intellij.util.ProcessingContext

class OpenGitmojiCompletionContributor : CompletionContributor() {

    val openGitmojiInsertHandler = OpenGitmojiInsertHandler()

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
                        if (OpenGitmojiContext.getTriggerWithColon()) {
                            val str = result.prefixMatcher.prefix
                            if (str.isEmpty()) return
                            if (!doc.contains(":$str") && !doc.contains("：$str")) return
                        }

                        val gitmojiPatterns = OpenGitmojiContext.getGitmojiPatterns()

                        OpenGitmojiContext.gms().forEach { gm ->
                            gitmojiPatterns.stream().filter { it.enable }.forEach { row ->
                                val str = lookupString(gm, row)
                                result.addElement(LookupElementBuilder
                                        .create(gm, "${str}${OpenGitmojiContext.REPLACE_SUFFIX_MARK}")
                                        .withPresentableText(str)
//                                        .withTailText(gm.description)
                                        .withTypeText(gm.description)
                                        .withLookupStrings(
                                                listOf(
                                                        gm.code.lowercase(),
                                                        gm.description.lowercase(),
                                                        gm.cnDescription.lowercase(),
                                                        gm.name.lowercase(),
                                                        gm.entity.lowercase()
                                                )
                                        )
                                        .withIcon(gm.getIcon())
                                        .withInsertHandler(openGitmojiInsertHandler)
                                )
                            }
                        }
                    }
                })
    }

    private fun lookupString(it: GM, regex: GitmojiPattern): String {
        return OpenGitmojiUtils.replace(regex.regex, it)
    }

}