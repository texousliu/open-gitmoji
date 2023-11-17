package com.github.texousliu.opengitmoji.config

import com.github.texousliu.opengitmoji.context.OpenGitmojiCustomContext
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class GitCommitEmojiRefreshAction : AnAction() {

    override fun isDumbAware(): Boolean {
        return true
    }

    override fun actionPerformed(actionEvent: AnActionEvent) {
        OpenGitmojiCustomContext.loadCustomEmojis()
    }

}
