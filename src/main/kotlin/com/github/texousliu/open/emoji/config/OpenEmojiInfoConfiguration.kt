package com.github.texousliu.open.emoji.config

import com.github.texousliu.open.emoji.dialog.OpenEmojiInfoDialogPanel
import com.github.texousliu.open.emoji.persistence.OpenEmojiPersistent
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import javax.swing.JComponent


class OpenEmojiInfoConfiguration : SearchableConfigurable {

    private val panel = OpenEmojiInfoDialogPanel()
    private val emojiInfoSettingsPanel: DialogPanel = panel.emojiInfoSettingsPanel

    private fun emojiInfoListModified(): Boolean {
        val config = OpenEmojiPersistent.getInstance().getOpenEmojiInfoList()
        if (config.size != panel.emojiInfoList.size) return true
        for ((index, emojiInfo) in panel.emojiInfoList.withIndex()) {
            val cei = config[index]
            if (cei != emojiInfo) return true
            if (cei.enable != emojiInfo.enable
                || cei.emoji != emojiInfo.emoji
                || cei.type != emojiInfo.type
            )
                return true
        }
        return false
    }

    private fun customEmojiDirectoryModified(): Boolean {
        return panel.customEmojiDirectoryTextField.text != OpenEmojiPersistent.getInstance().getCustomEmojiDirectory()
    }

    override fun isModified(): Boolean = emojiInfoListModified() || customEmojiDirectoryModified()

    override fun getDisplayName(): String = "Emoji List"

    override fun getId(): String = "com.github.texousliu.emoji.settings.OpenEmojiInfoSettings"

    override fun apply() {
        if (customEmojiDirectoryModified()) {
            OpenEmojiPersistent.getInstance().setCustomEmojiDirectory(panel.customEmojiDirectoryTextField.text)
        }
        if (emojiInfoListModified()) {
            OpenEmojiPersistent.getInstance().setOpenEmojiInfoList(panel.emojiInfoList)
        }
    }

    override fun reset() {
        emojiInfoSettingsPanel.reset()
    }

    override fun createComponent(): JComponent = emojiInfoSettingsPanel

}
