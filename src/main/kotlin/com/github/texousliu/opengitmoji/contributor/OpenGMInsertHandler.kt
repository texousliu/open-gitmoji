package com.github.texousliu.opengitmoji.contributor

import com.github.texousliu.opengitmoji.context.OpenGMContext
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OpenGMInsertHandler : InsertHandler<LookupElement> {

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
        return s.replace(OpenGMContext.REPLACE_SUFFIX_MARK, getSuffixText());
    }

    private fun getSuffixText(): String {
        var suffixExpression = OpenGMContext.getSuffixText();
        if (suffixExpression.contains("#{DATE}") || suffixExpression.contains("#{TIME}")) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formatted = current.format(formatter)
            val dt = formatted.split(" ")
            suffixExpression = suffixExpression.replace("#{DATE}", dt[0])
            suffixExpression = suffixExpression.replace("#{TIME}", dt[1])
        }
        return suffixExpression;
    }

}