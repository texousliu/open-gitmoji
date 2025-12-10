package com.github.texousliu.open.emoji.compatiable

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsContexts.DialogTitle
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JComponent

/**
 * 兼容 IntelliJ IDEA Ultimate 2024.3 eap (243.16718.32)
 *
 *
 * 代码来源 intellij-community 中的 idea/243.16718.32 tag 下的 ComponentWithBrowseButton 的内部类 [ComponentWithBrowseButton.BrowseFolderActionListener]
 */
class BrowseFolderActionListener<T : JComponent?>(
    textField: ComponentWithBrowseButton<T>?,
    project: Project?,
    fileChooserDescriptor: FileChooserDescriptor,
    accessor: TextComponentAccessor<in T?>
) : BrowseFolderRunnable<T>(
    project, fileChooserDescriptor,
    textField?.childComponent, accessor
), ActionListener {
    @Deprecated(
        """use {@link #BrowseFolderActionListener(ComponentWithBrowseButton, Project, FileChooserDescriptor, TextComponentAccessor)}
      together with {@link FileChooserDescriptor#withTitle} and {@link FileChooserDescriptor#withDescription}"""
    )
    constructor(
        title: @DialogTitle String?,
        description: @NlsContexts.Label String?,
        textField: ComponentWithBrowseButton<T>?,
        project: Project?,
        fileChooserDescriptor: FileChooserDescriptor,
        accessor: TextComponentAccessor<in T?>
    ) : this(textField, project, fileChooserDescriptor.withTitle(title).withDescription(description), accessor)

    override fun actionPerformed(e: ActionEvent) {
        run()
    }
}