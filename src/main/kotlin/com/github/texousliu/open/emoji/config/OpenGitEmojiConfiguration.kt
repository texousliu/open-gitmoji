package com.github.texousliu.open.emoji.config

import com.github.texousliu.open.emoji.dialog.OpenGitEmojiDialogPanel
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import javax.swing.JComponent


class OpenGitEmojiConfiguration : SearchableConfigurable {

    private val panel = OpenGitEmojiDialogPanel()
    private val gitEmojiSettingsPanel: DialogPanel = panel.gitEmojiSettingsPanel

    override fun isModified(): Boolean = panel.markModified()

    override fun getDisplayName(): String = OpenEmojiBundle.message("settings.git.title")

    override fun getId(): String = "com.github.texousliu.emoji.settings.OpenGitEmojiSettings"

    override fun apply() = panel.apply()

    override fun reset() = gitEmojiSettingsPanel.reset()

    override fun createComponent(): JComponent = gitEmojiSettingsPanel

}
