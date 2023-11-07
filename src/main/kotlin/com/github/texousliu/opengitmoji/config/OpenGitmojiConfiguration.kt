package com.github.texousliu.opengitmoji.config

import com.github.texousliu.opengitmoji.context.OpenGitmojiContext
import com.github.texousliu.opengitmoji.dialog.openGitmojiDialogPanel
import com.github.texousliu.opengitmoji.model.RegexTableInfo
import com.google.gson.Gson
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel


class OpenGitmojiConfiguration : SearchableConfigurable {
    private val gmSettingsPanel: DialogPanel = openGitmojiDialogPanel()

    override fun isModified(): Boolean =
            OpenGitmojiContext.getTriggerCondition() != OpenGitmojiContext.getConfigTriggerCondition()
            || OpenGitmojiContext.getRegexTableInfo() != OpenGitmojiContext.getConfigRegexTableInfo()


    override fun getDisplayName(): String = "Open Gitmoji Settings"

    override fun getId(): String = "open.texousliu.config.settings.gm.OpenGitmojiSettings"

    init {
        // input border
    }

    override fun apply() {
        OpenGitmojiContext.setConfigTriggerCondition(OpenGitmojiContext.getTriggerCondition())
        OpenGitmojiContext.setConfigRegexTableInfo(OpenGitmojiContext.getRegexTableInfo())
        gmSettingsPanel.apply()
    }

    override fun reset() {
        OpenGitmojiContext.setRegexTableInfo(OpenGitmojiContext.getConfigRegexTableInfo())
        OpenGitmojiContext.setTriggerCondition(OpenGitmojiContext.getConfigTriggerCondition())
        gmSettingsPanel.reset()
    }

    override fun createComponent(): JComponent = gmSettingsPanel

}
