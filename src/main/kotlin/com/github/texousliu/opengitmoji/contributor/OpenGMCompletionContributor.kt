package com.github.texousliu.opengitmoji.contributor

import com.github.texousliu.opengitmoji.context.OpenGMContext
import com.github.texousliu.opengitmoji.model.GM
import com.github.texousliu.opengitmoji.model.GMLanguage
import com.github.texousliu.opengitmoji.model.GMInputModel
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
                    val language = OpenGMContext.getLanguage()
                    val inputModel = OpenGMContext.getInputModel()
                    OpenGMContext.gms().forEach {
                        result.addElement(
                            LookupElementBuilder.create(it, lookupString(it, language, inputModel))
                                .withPresentableText(it.code)
                                .withTypeText(it.entity)
                                .withTailText(description(it, language))
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

    private fun lookupString(it: GM, language: GMLanguage, inputModel: GMInputModel): String {
        return lookupString(it, description(it, language), inputModel)
    }

    private fun lookupString(it: GM, desc: String, inputModel: GMInputModel): String {
        return when(inputModel) {
            GMInputModel.TEXT -> it.code
            GMInputModel.UNICODE_WITH_DESC -> "${it.emoji} $desc"
            GMInputModel.TEXT_WITH_DESC -> "${it.code} $desc"
            else -> it.emoji
        }
    }

    private fun description(it: GM, language: GMLanguage): String {
        return when(language) {
            GMLanguage.EN -> it.description
            else -> it.cn_description
        }
    }

}