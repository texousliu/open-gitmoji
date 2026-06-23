package com.github.texousliu.open.emoji.service

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project

/**
 * Background task for syncing emoji definitions from the gitmoji official repository.
 *
 * Shows a progress indicator during download and displays a notification
 * with the sync result summary.
 */
class EmojiSyncBackgroundTask(
    project: Project,
    private val onComplete: ((SyncResult) -> Unit)? = null
) : Task.Backgroundable(project, "Syncing Gitmoji Definitions", true) {

    override fun run(indicator: ProgressIndicator) {
        indicator.isIndeterminate = true
        indicator.text = "Checking for updates from gitmoji official repository..."

        val syncService = EmojiSyncService(project)
        val result = syncService.sync()

        indicator.fraction = 1.0
        indicator.text = "Sync complete"

        // Notify on EDT
        com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
            showNotification(result)
            onComplete?.invoke(result)
        }
    }

    private fun showNotification(result: SyncResult) {
        val group = NotificationGroupManager.getInstance()
            .getNotificationGroup("Open Gitmoji")

        if (!result.success) {
            group.createNotification(
                "Gitmoji Sync Failed",
                result.error ?: "Unknown error",
                NotificationType.ERROR
            ).notify(project)
            return
        }

        val summary = buildString {
            append("Sync completed. ")
            if (result.added.isNotEmpty()) append("Added: ${result.added.size}. ")
            if (result.modified.isNotEmpty()) append("Modified: ${result.modified.size}. ")
            if (result.removed.isNotEmpty()) append("Removed: ${result.removed.size}. ")
            if (result.added.isEmpty() && result.modified.isEmpty() && result.removed.isEmpty()) {
                append("No changes detected.")
            }
        }

        group.createNotification(
            "Gitmoji Sync Complete",
            summary,
            NotificationType.INFORMATION
        ).notify(project)
    }
}
