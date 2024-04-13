package com.github.texousliu.open.emoji.config

import com.github.texousliu.open.emoji.dialog.OpenEditorEmojiDialogPanel
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import javax.swing.JComponent


class OpenEditorEmojiConfiguration : SearchableConfigurable {

    private val panel = OpenEditorEmojiDialogPanel()
    private val editorEmojiSettingsPanel: DialogPanel = panel.editorEmojiSettingsPanel

    override fun isModified(): Boolean = panel.markModified()

    override fun getDisplayName(): String = OpenEmojiBundle.message("settings.editor.title")

    override fun getId(): String = "com.github.texousliu.emoji.settings.OpenEditorEmojiSettings"

    override fun apply() = panel.apply()

    override fun reset() = editorEmojiSettingsPanel.reset()

    override fun createComponent(): JComponent = editorEmojiSettingsPanel

}
