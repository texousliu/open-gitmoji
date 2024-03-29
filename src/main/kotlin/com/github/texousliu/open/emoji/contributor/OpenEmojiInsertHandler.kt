package com.github.texousliu.open.emoji.contributor

import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement

class OpenEmojiInsertHandler : InsertHandler<LookupElement> {

    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        val text = replace(context.document.text, item.lookupString)
        context.document.setText(text)
    }

    private fun replace(text: String, replaceText: String): String {
        var s = text.trim()
        val rt = ":${replaceText}"
        val rtz = "：$replaceText"
        if (s.contains(rt)) {
            s = s.replace(rt, replaceText)
        } else if (s.contains(rtz)) {
            s = s.replace(rtz, replaceText)
        }
        return s.replace(OpenEmojiUtils.REPLACE_SUFFIX_MARK, "")
    }

}