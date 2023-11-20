package com.github.texousliu.opengitmoji.config

import com.github.texousliu.opengitmoji.context.OpenEmojiContext
import com.github.texousliu.opengitmoji.dialog.OpenEmojiDialogPanel
import com.github.texousliu.opengitmoji.persistence.OpenEmojiPersistent
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import javax.swing.JComponent


class OpenEmojiConfiguration : SearchableConfigurable {

    private val panel = OpenEmojiDialogPanel()
    private val emojiSettingsPanel: DialogPanel = panel.emojiSettingsPanel

    private fun emojiPatternsModified(): Boolean {
        val config = OpenEmojiPersistent.getInstance().getOpenEmojiPatterns()
        if (config.size != panel.emojiPatterns.size) return true
        for ((index, emojiPattern) in panel.emojiPatterns.withIndex()) {
            if (config[index] != emojiPattern) return true
            if (config[index].enable != emojiPattern.enable) return true
        }
        return false
    }

    private fun customEmojiDirectoryModified(): Boolean {
        return panel.customEmojiDirectoryTextField.text != OpenEmojiPersistent.getInstance().getCustomEmojiDirectory()
    }

    private fun triggerWithColonModified(): Boolean {
        return panel.triggerWithColonCheckBox.isSelected != OpenEmojiPersistent.getInstance().getTriggerWithColon()
    }

    override fun disposeUIResources() {
        panel.disposeUIResources()
    }

    override fun isModified(): Boolean =
        triggerWithColonModified() || customEmojiDirectoryModified() || emojiPatternsModified()


    override fun getDisplayName(): String = "Open Emoji Settings"

    override fun getId(): String = "com.github.texousliu.emoji.settings.OpenEmojiSettings"

    override fun apply() {
        if (customEmojiDirectoryModified()) {
            OpenEmojiPersistent.getInstance().setCustomEmojiDirectory(panel.customEmojiDirectoryTextField.text)
            OpenEmojiContext.loadCustomEmojis()
        }
        if (triggerWithColonModified()) {
            OpenEmojiPersistent.getInstance().setTriggerWithColon(panel.triggerWithColonCheckBox.isSelected)
        }
        if (emojiPatternsModified()) {
            OpenEmojiPersistent.getInstance().setOpenEmojiPatterns(panel.emojiPatterns)
        }
    }

    override fun reset() {
        emojiSettingsPanel.reset()
    }

    override fun createComponent(): JComponent = emojiSettingsPanel

}
