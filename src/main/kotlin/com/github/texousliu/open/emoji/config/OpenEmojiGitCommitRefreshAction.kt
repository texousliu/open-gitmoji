package com.github.texousliu.open.emoji.config

import com.github.texousliu.open.emoji.context.OpenEmojiContext
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class OpenEmojiGitCommitRefreshAction : AnAction() {

    override fun isDumbAware(): Boolean {
        return true
    }

    override fun actionPerformed(actionEvent: AnActionEvent) {
        OpenEmojiContext.loadCustomEmojis()
    }

}
