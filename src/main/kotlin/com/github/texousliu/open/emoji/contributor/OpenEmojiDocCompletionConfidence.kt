package com.github.texousliu.open.emoji.contributor

import com.intellij.codeInsight.completion.CompletionConfidence
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import com.intellij.util.ThreeState

class OpenEmojiDocCompletionConfidence : CompletionConfidence()
{

    private val docStart: String = "DOC_"
    private val commentEnd: String = "_COMMENT"

    @Deprecated("兼容 IntelliJ IDEA Ultimate 2024.3 eap (243.16718.32)")
    override fun shouldSkipAutopopup(contextElement: PsiElement, psiFile: PsiFile, offset: Int): ThreeState {
        val elementType = contextElement.elementType.toString()
        if (elementType.startsWith(docStart) || elementType.endsWith(commentEnd))
            return ThreeState.NO
        return ThreeState.UNSURE
    }

    fun shouldSkipAutopopup(editor: Editor, contextElement: PsiElement, psiFile: PsiFile, offset: Int): ThreeState {
        return shouldSkipAutopopup(contextElement, psiFile, offset)
    }

}