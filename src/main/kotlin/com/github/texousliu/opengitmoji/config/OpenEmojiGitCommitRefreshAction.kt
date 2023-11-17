package com.github.texousliu.opengitmoji.config

import com.github.texousliu.opengitmoji.context.OpenEmojiCustomContext
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class OpenEmojiGitCommitRefreshAction : AnAction() {

    override fun isDumbAware(): Boolean {
        return true
    }

    override fun actionPerformed(actionEvent: AnActionEvent) {
        OpenEmojiCustomContext.loadCustomEmojis()
    }

}