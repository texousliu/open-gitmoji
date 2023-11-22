package com.github.texousliu.open.emoji.config

import com.github.texousliu.open.emoji.dialog.OpenEmojiInfoDialogPanel
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import javax.swing.JComponent


class OpenEmojiInfoConfiguration : SearchableConfigurable {

    private val panel = OpenEmojiInfoDialogPanel()
    private val emojiInfoSettingsPanel: DialogPanel = panel.emojiInfoSettingsPanel

    override fun isModified(): Boolean = false

    override fun getDisplayName(): String = "Emoji List"

    override fun getId(): String = "com.github.texousliu.emoji.settings.OpenEmojiInfoSettings"

    override fun apply() {

    }

    override fun reset() {


    }

    override fun createComponent(): JComponent = emojiInfoSettingsPanel

}
