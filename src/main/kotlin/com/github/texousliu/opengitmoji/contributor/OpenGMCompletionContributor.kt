package com.github.texousliu.opengitmoji.contributor

import com.github.texousliu.opengitmoji.context.OpenGMContext
import com.github.texousliu.opengitmoji.model.GM
import com.github.texousliu.opengitmoji.model.GMField
import com.github.texousliu.opengitmoji.model.GMInputModel
import com.github.texousliu.opengitmoji.model.GMLanguage
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
                    val doc = parameters.editor.document.charsSequence
                    if (doc.isEmpty()) return
                    if (OpenGMContext.getTriggerCondition()) {
                        val str = result.prefixMatcher.prefix
                        if (str.isEmpty()) return
                        if (!doc.contains(":$str") && !doc.contains("：$str")) return
                    }

                    val language = OpenGMContext.getLanguage()
                    val inputModel = OpenGMContext.getInputModel()
                    val presentableText = OpenGMContext.getPresentableText()
                    val tailText = OpenGMContext.getTailText()
                    val typeText = OpenGMContext.getTypeText()
                    OpenGMContext.gms().forEach {
                        result.addElement(
                            LookupElementBuilder.create(it, lookupString(it, language, inputModel))
                                .withPresentableText(text(it, presentableText))
                                .withTailText(text(it, tailText))
                                .withTypeText(text(it, typeText))
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
        return when (inputModel) {
            GMInputModel.CODE -> it.code
            GMInputModel.EMOJI_WITH_DESC -> "${it.emoji} $desc"
            GMInputModel.CODE_WITH_DESC -> "${it.code} $desc"
            else -> it.emoji
        }
    }

    private fun description(it: GM, language: GMLanguage): String {
        return when (language) {
            GMLanguage.EN -> it.description
            else -> it.cn_description
        }
    }

    private fun text(it: GM, filed: GMField): String {
        return when (filed) {
            GMField.CODE -> it.code
            GMField.NAME -> it.name
            GMField.EMOJI -> it.emoji
            GMField.ENTITY -> it.entity
            GMField.DESCRIPTION -> it.description
            GMField.CN_DESCRIPTION -> it.cn_description
            else -> ""
        }
    }

}