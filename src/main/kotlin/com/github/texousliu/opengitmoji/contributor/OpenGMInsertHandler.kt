package com.github.texousliu.opengitmoji.contributor

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement

class OpenGMInsertHandler : InsertHandler<LookupElement> {

    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        val text = replace(context.document.text, item.lookupString)
        context.document.setText(text)
    }

    private fun replace(text: String, replaceText: String): String {
        val s = text.trim()
        val rt = ":${replaceText}"
        val rtz = "：$replaceText"
        if (s.contains(rt)) {
            return s.replace(rt, replaceText)
        } else if (s.contains(rtz)) {
            return s.replace(rtz, replaceText)
        }
        return s
    }

}