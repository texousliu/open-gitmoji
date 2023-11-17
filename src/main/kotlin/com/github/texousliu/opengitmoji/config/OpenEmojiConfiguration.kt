package com.github.texousliu.opengitmoji.config

import com.github.texousliu.opengitmoji.context.OpenEmojiContext
import com.github.texousliu.opengitmoji.context.OpenEmojiCustomContext
import com.github.texousliu.opengitmoji.dialog.OpenEmojiDialogPanel
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import javax.swing.JComponent


class OpenEmojiConfiguration : SearchableConfigurable {

    private val panel = OpenEmojiDialogPanel()
    private val gmSettingsPanel: DialogPanel = panel.dialogPanel

    private fun tableModified(): Boolean {
        val config = OpenEmojiContext.getEmojiPatterns()
        if (config.size != panel.openEmojiPatterns.size) return true
        for ((index, emojiPattern) in panel.openEmojiPatterns.withIndex()) {
            if (config[index] != emojiPattern) return true
            if (config[index].enable != emojiPattern.enable) return true
        }
        return false
    }

    private fun customEmojiFolderModified(): Boolean {
        return panel.customFolderTextField.text != OpenEmojiContext.getCustomEmojiFolder()
    }

    private fun triggerWithColonModified(): Boolean {
        return OpenEmojiContext.getTriggerWithColon() != panel.triggerWithColonCheckBox.isSelected
    }

    override fun isModified(): Boolean = tableModified()
            || triggerWithColonModified()
            || customEmojiFolderModified()


    override fun getDisplayName(): String = "Open Gitmoji Settings"

    override fun getId(): String = "open.texousliu.config.settings.gm.OpenGitmojiSettings"

    override fun apply() {
        val customFolderChange = customEmojiFolderModified()
        OpenEmojiContext.apply(panel.openEmojiPatterns,
                panel.triggerWithColonCheckBox.isSelected, panel.customFolderTextField.text)
        if (customFolderChange) {
            // 重载自定义 emoji
            OpenEmojiCustomContext.loadCustomEmojis()
        }
    }

    override fun reset() {
        OpenEmojiContext.reset()
        gmSettingsPanel.reset()
    }

    override fun createComponent(): JComponent = gmSettingsPanel

}
