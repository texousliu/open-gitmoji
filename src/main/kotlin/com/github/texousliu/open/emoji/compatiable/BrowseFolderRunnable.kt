// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.github.texousliu.open.emoji.compatiable

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.BrowseFolderDescriptor
import com.intellij.openapi.ui.BrowseFolderDescriptor.Companion.asBrowseFolderDescriptor
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsContexts.DialogTitle
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.io.NioFiles
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.NonNls
import javax.swing.JComponent

/**
 * 兼容 IntelliJ IDEA Ultimate 2024.3 eap (243.16718.32)
 *
 *
 * 代码来源 intellij-community 中的 idea/243.16718.32 tag 下的同名类
 */
@ApiStatus.Experimental
open class BrowseFolderRunnable<T : JComponent?> : Runnable {
    private val myProject: Project?
    private val myAccessor: TextComponentAccessor<in T?>
    private val myFileChooserDescriptor: FileChooserDescriptor
    private var myTextComponent: T?

    constructor(
        project: Project?,
        fileChooserDescriptor: FileChooserDescriptor,
        component: T?,
        accessor: TextComponentAccessor<in T?>
    ) {
        if (fileChooserDescriptor.isChooseMultiple) {
            Logger.getInstance(BrowseFolderRunnable::class.java).warn("multiple selection not supported")
        }
        myTextComponent = component
        this.myProject = project
        myFileChooserDescriptor = fileChooserDescriptor
        myAccessor = accessor
    }

    @Deprecated(
        """use {@link #BrowseFolderRunnable(Project, FileChooserDescriptor, JComponent, TextComponentAccessor)}
    together with {@link FileChooserDescriptor#withTitle} and {@link FileChooserDescriptor#withDescription}"""
    )
    constructor(
        title: @DialogTitle String?,
        description: @NlsContexts.Label String?,
        project: Project?,
        fileChooserDescriptor: FileChooserDescriptor,
        component: T?,
        accessor: TextComponentAccessor<in T?>
    ) {
        var descriptor = fileChooserDescriptor
        if (descriptor.isChooseMultiple) {
            Logger.getInstance(BrowseFolderRunnable::class.java).error("multiple selection not supported")
        }
        if (title != null) {
            descriptor = descriptor.withTitle(title)
        }
        if (description != null) {
            descriptor = descriptor.withDescription(description)
        }
        myTextComponent = component
        this.myProject = project
        myFileChooserDescriptor = descriptor
        myAccessor = accessor
    }

    override fun run() {
        chooseFile(myFileChooserDescriptor)
    }

    private fun chooseFile(descriptor: FileChooserDescriptor) {
        FileChooser.chooseFile(
            descriptor, myProject, myTextComponent, initialFile
        ) { chosenFile: VirtualFile -> this.onFileChosen(chosenFile) }
    }

    private val initialFile: VirtualFile?
        get() {
            val directoryName = myAccessor.getText(myTextComponent).trim { it <= ' ' }
            if (directoryName.isBlank()) return null

            var path = NioFiles.toPath(expandPath(directoryName))
            if (path == null || !path.isAbsolute) return null

            while (path != null) {
                val result = LocalFileSystem.getInstance().findFileByNioFile(path)
                if (result != null) return result
                path = path.parent
            }
            return null
        }

    protected fun expandPath(path: String): @NonNls String {
        BrowseFolderDescriptor
        val descriptor: BrowseFolderDescriptor =
            myFileChooserDescriptor.asBrowseFolderDescriptor()
        val convertTextToPath: Function1<String, String>? = descriptor.convertTextToPath
        return convertTextToPath?.invoke(path) ?: path
    }

    protected fun chosenFileToResultingText(chosenFile: VirtualFile): @NlsSafe String {
        val descriptor: BrowseFolderDescriptor =
            myFileChooserDescriptor.asBrowseFolderDescriptor()
        val convertFileToText: Function1<VirtualFile, String>? = descriptor.convertFileToText
        if (convertFileToText != null) {
            return convertFileToText.invoke(chosenFile)
        }
        val convertPathToText: Function1<String, String>? = descriptor.convertPathToText
        if (convertPathToText != null) {
            return convertPathToText.invoke(chosenFile.path)
        }
        return chosenFile.presentableUrl
    }

    private val componentText: String
        get() = myAccessor.getText(myTextComponent).trim { it <= ' ' }

    private fun onFileChosen(chosenFile: VirtualFile) {
        myAccessor.setText(myTextComponent, chosenFileToResultingText(chosenFile))
    }

}

