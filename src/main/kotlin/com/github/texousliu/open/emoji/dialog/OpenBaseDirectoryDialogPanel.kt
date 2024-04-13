package com.github.texousliu.open.emoji.dialog

import com.github.texousliu.open.emoji.config.OpenEmojiBundle
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import java.util.function.Consumer
import java.util.function.Function
import javax.swing.JComponent


class OpenBaseDirectoryDialogPanel(directory: String) {

    private val customEmojiDirectoryTextField = JBTextField(directory)
    private val customEmojiDirectoryComponent = TextFieldWithBrowseButton(customEmojiDirectoryTextField)
    private var configDirectory = ""

    init {
        configureStartDirectoryField()
    }

    fun directory(): String {
        return customEmojiDirectoryComponent.text
    }

    fun onReset(directory: String) {
        customEmojiDirectoryComponent.text = directory
        configDirectory = directory
    }

    fun onApply() {
        configDirectory = customEmojiDirectoryComponent.text
    }

    fun markModifiedDirectory(): Boolean {
        return isModifiedDirectory()
    }

    fun isModifiedDirectory(): Boolean {
        return configDirectory != customEmojiDirectoryComponent.text
    }

    fun create(): JComponent {
        return customEmojiDirectoryComponent;
    }

    fun onChange(changeHandler: Consumer<String>) {
        changeHandler.accept(customEmojiDirectoryComponent.text)
    }

    private fun configureStartDirectoryField() {
        customEmojiDirectoryComponent.addBrowseFolderListener(
            OpenEmojiBundle.message("settings.info.custom.directory.choose.title"),
            OpenEmojiBundle.message("settings.info.custom.directory.choose.desc"),
            null,
            FileChooserDescriptorFactory.createSingleFolderDescriptor(),
            TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        )
    }

}