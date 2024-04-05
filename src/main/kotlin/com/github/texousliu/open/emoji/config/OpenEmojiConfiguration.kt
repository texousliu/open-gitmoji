package com.github.texousliu.open.emoji.config

import com.github.texousliu.open.emoji.dialog.OpenEmojiDialogPanel
import com.github.texousliu.open.emoji.persistence.OpenEmojiPersistent
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
            if (config[index].enable != emojiPattern.enable
                    || config[index].enableCommit != emojiPattern.enableCommit
                    || config[index].enableEditor != emojiPattern.enableEditor) return true
        }
        return false
    }


    private fun triggerWithColonModified(): Boolean {
        return panel.triggerWithColonCheckBox.isSelected !=
                OpenEmojiPersistent.getInstance().getTriggerWithColon()
    }

    private fun editorEmojiSupportedModified(): Boolean {
        return panel.editorEmojiSupportedCheckBox.isSelected !=
                OpenEmojiPersistent.getInstance().getEditorEmojiSupported()
    }

    override fun isModified(): Boolean =
            triggerWithColonModified() || editorEmojiSupportedModified() || emojiPatternsModified()

    override fun getDisplayName(): String = OpenEmojiBundle.message("settings.title")

    override fun getId(): String = "com.github.texousliu.emoji.settings.OpenEmojiSettings"

    override fun apply() {
        if (triggerWithColonModified()) {
            OpenEmojiPersistent.getInstance().setTriggerWithColon(panel.triggerWithColonCheckBox.isSelected)
        }
        if (editorEmojiSupportedModified()) {
            OpenEmojiPersistent.getInstance().setEditorEmojiSupported(panel.editorEmojiSupportedCheckBox.isSelected)
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
