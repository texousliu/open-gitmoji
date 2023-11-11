package com.github.texousliu.opengitmoji.config

import com.github.texousliu.opengitmoji.context.OpenGitmojiContext
import com.github.texousliu.opengitmoji.dialog.OpenGitmojiDialogPanel
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import javax.swing.JComponent


class OpenGitmojiConfiguration : SearchableConfigurable {

    private val panel = OpenGitmojiDialogPanel()
    private val gmSettingsPanel: DialogPanel = panel.dialogPanel

    private fun tableModified(): Boolean {
        val config = OpenGitmojiContext.getGitmojiPatterns()
        if (config.size != panel.gitmojiPatterns.size) return true
        for ((index, gitmojiPattern) in panel.gitmojiPatterns.withIndex()) {
            if (config[index] != gitmojiPattern) return true
            if (config[index].enable != gitmojiPattern.enable) return true
        }
        return false
    }

    override fun isModified(): Boolean = tableModified()
            || OpenGitmojiContext.getTriggerWithColon() != panel.checkBox.isSelected


    override fun getDisplayName(): String = "Open Gitmoji Settings"

    override fun getId(): String = "open.texousliu.config.settings.gm.OpenGitmojiSettings"

    override fun apply() {
        OpenGitmojiContext.apply(panel.gitmojiPatterns, panel.checkBox.isSelected)
    }

    override fun reset() {
        OpenGitmojiContext.reset()
        gmSettingsPanel.reset()
    }

    override fun createComponent(): JComponent = gmSettingsPanel

}
