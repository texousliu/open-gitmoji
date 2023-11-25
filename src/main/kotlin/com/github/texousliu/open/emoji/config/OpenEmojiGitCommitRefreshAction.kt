package com.github.texousliu.open.emoji.config

import com.github.texousliu.open.emoji.persistence.OpenEmojiPersistent
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.action.ExternalSystemAction

class OpenEmojiGitCommitRefreshAction : ExternalSystemAction() {

    @Volatile
    var isEnable = true

    override fun isEnabled(e: AnActionEvent): Boolean = isEnable

    override fun isVisible(e: AnActionEvent): Boolean = true

    override fun isDumbAware(): Boolean = true

    override fun actionPerformed(actionEvent: AnActionEvent) {
        try {
            isEnable = false
            OpenEmojiPersistent.getInstance().refresh()
            Notifications.Bus.notify(Notification("Custom Open Emoji Notification Group",
                    "Refresh custom emoji from disk success", NotificationType.INFORMATION))
        } finally {
            isEnable = true
        }
    }

}
