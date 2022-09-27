package com.github.texousliu.opengitmoji.contributor

import com.github.texousliu.opengitmoji.context.OpenGMContext
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiPlainText
import com.intellij.util.ProcessingContext

class OpenGMCompletionContributor : CompletionContributor() {

    private val openGMInsertHandler = OpenGMInsertHandler()

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
                    if (parameters.editor.document.charsSequence.isEmpty()) return
                    OpenGMContext.gms().forEach {
                        result.addElement(
                            LookupElementBuilder.create(it, it.emoji)
                                .withPresentableText(it.cn_description)
                                .withTypeText(it.code)
                                .withTailText(it.description)
                                .withLookupStrings(
                                    listOf(
                                        it.code.lowercase(),
                                        it.description.lowercase(),
                                        it.cn_description.lowercase(),
                                        it.name.lowercase(),
                                        it.entity.lowercase()
                                    )
                                )
                                .withIcon(it.getIcon())
                                .withInsertHandler(openGMInsertHandler)
                        )
                    }
                }
            })
    }

}