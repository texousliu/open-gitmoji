// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.texousliu.opengitmoji.dialog

import com.github.texousliu.opengitmoji.context.OpenGitmojiContext
import com.github.texousliu.opengitmoji.model.GitmojiPattern
import com.github.texousliu.opengitmoji.utils.OpenGitmojiUtils
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.*
import com.intellij.openapi.ui.ComponentWithBrowseButton.BrowseFolderActionListener
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsContexts.DialogTitle
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.*
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.scale.JBUIScale.scale
import com.intellij.ui.table.JBTable
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.table.TableCellEditor


class OpenGitmojiDialogPanel {

    val triggerWithColonCheckBox = JBCheckBox("Get prompt through text starting with ':' or '：'. Such as ':s'")
    val gitmojiPatterns = mutableListOf<GitmojiPattern>()
    val customFolderTextField = JBTextField()
    private val jbTableModel = PatternsTableModel(gitmojiPatterns)
    private val jbTable = JBTable(jbTableModel)
    private val chooseComponent = TextFieldWithBrowseButton(customFolderTextField)

    val dialogPanel = openGitmojiDialogPanel()

    init {
        jbTable.setShowGrid(false)
        object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                editSelectedPattern()
                return true
            }
        }.installOn(jbTable)
        configureStartDirectoryField()
    }

    private fun openGitmojiDialogPanel(): DialogPanel {
        return panel {
            group("Custom") {
                row {
                    cell(triggerWithColonCheckBox)
                            .gap(RightGap.SMALL)
                            .onReset {
                                triggerWithColonCheckBox.isSelected = OpenGitmojiContext.getTriggerWithColon()
                            }
                }.rowComment("Optimize input habits and reduce trouble caused by unnecessary prompts")
                row("Custom Emoji Folder:") {
                    cell(chooseComponent).resizableColumn().align(Align.FILL)
                            .onReset {
                                chooseComponent.text = OpenGitmojiContext.getCustomEmojiFolder()
                            }
//                textFieldWithBrowseButton("Choose Custom Emoji Folder", null, FileChooserDescriptorFactory.createSingleFolderDescriptor())
//                        .text(OpenGitmojiContext.getCustomEmojiFolder())
//                        .resizableColumn()
//                        .align(Align.FILL)
                }.rowComment("Configure your own emojis beyond additional system presets")
            }

            group("Prompt List") {
//                row("Configure Gitmoji filling expression and generate a filling content list through the expression for users to choose.") { }
                row("Configure prompt item expression") { }
                row {
                    cell(createPromptListTable())
                            .gap(RightGap.SMALL)
                            .onReset {
                                gitmojiPatterns.clear()
                                OpenGitmojiContext.getGitmojiPatterns().forEach {
                                    gitmojiPatterns.add(it.clone())
                                }
                                jbTableModel.fireTableDataChanged()
                            }.resizableColumn()
                            .align(Align.FILL)
                }.layout(RowLayout.PARENT_GRID).resizableRow()
            }

            separator()

        }
    }

    private fun configureStartDirectoryField() {
        chooseComponent.addBrowseFolderListener(
                "Choose Custom Emoji Folder",
                "Choose custom emoji folder",
                null,
                FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        )
    }

    private fun createPromptListTable(): JComponent {
        val patternColumn = jbTable.columnModel.getColumn(0)
        patternColumn.preferredWidth = scale(150)

        val exampleColumn = jbTable.columnModel.getColumn(1)
        exampleColumn.preferredWidth = scale(250)

        val enableColumn = jbTable.columnModel.getColumn(2)
        enableColumn.maxWidth = scale(50)
        enableColumn.minWidth = enableColumn.maxWidth
        enableColumn.cellRenderer = BooleanTableCellRenderer()
        enableColumn.cellEditor = BooleanTableCellEditor()

        val panel = ToolbarDecorator.createDecorator(jbTable)
                .setAddAction {
                    addPattern()
                }.setEditAction {
                    editSelectedPattern()
                }
                .setMoveUpAction {
                    moveUpPattern()
                }
                .setMoveDownAction {
                    moveDownPattern()
                }
                .setRemoveAction {
                    removePattern()
                }.createPanel()
        panel.preferredSize = Dimension(0, 300)
        return panel
    }

    private fun addPattern() {
        stopEditing()
        val addPatternDialog = PatternInfoDialogWrapper()
        if (addPatternDialog.showAndGet()) {
            gitmojiPatterns.add(addPatternDialog.load())

            val index: Int = gitmojiPatterns.size - 1
            jbTableModel.fireTableRowsInserted(index, index)
            jbTable.selectionModel.setSelectionInterval(index, index)
            jbTable.scrollRectToVisible(jbTable.getCellRect(index, 0, true))
        }
    }

    private fun editSelectedPattern() {
        stopEditing()
        val selectedIndex: Int = jbTable.selectedRow
        if (selectedIndex < 0 || selectedIndex >= jbTableModel.rowCount) {
            return
        }
        // 获取选择的内容
        val selectPattern = gitmojiPatterns[selectedIndex]

        val dialog = PatternInfoDialogWrapper(selectPattern.pattern, selectPattern.enable)
        dialog.title = "Edit Pattern"
        if (!dialog.showAndGet()) {
            return
        }
        gitmojiPatterns[selectedIndex] = dialog.load()
        jbTableModel.fireTableRowsUpdated(selectedIndex, selectedIndex)
        jbTable.selectionModel.setSelectionInterval(selectedIndex, selectedIndex)
    }

    private fun removePattern() {
        stopEditing()
        val selectedIndex: Int = jbTable.selectedRow
        if (selectedIndex >= 0 && selectedIndex < jbTableModel.rowCount) {
            TableUtil.removeSelectedItems(jbTable)
        }
    }

    private fun moveUpPattern() {
        movePattern(-1)
    }

    private fun moveDownPattern() {
        movePattern(1)
    }

    private fun movePattern(step: Int) {
        stopEditing()
        val selectedIndex: Int = jbTable.selectedRow
        val swap = selectedIndex + step
        // 获取选择的内容
        val selectPattern = gitmojiPatterns[selectedIndex]
        gitmojiPatterns[selectedIndex] = gitmojiPatterns[swap]
        gitmojiPatterns[swap] = selectPattern

        jbTableModel.fireTableRowsUpdated(swap.coerceAtMost(selectedIndex), swap.coerceAtLeast(selectedIndex))
        jbTable.selectionModel.setSelectionInterval(swap, swap)
    }


    private fun stopEditing() {
        if (jbTable.isEditing) {
            val editor: TableCellEditor = jbTable.cellEditor
            editor.stopCellEditing()
        }
    }

    private class PatternInfoDialogWrapper() : DialogWrapper(true) {

        var enable = JBCheckBox("Enable pattern")
        var pattern = JBTextField()
        var example = JBTextField()

        constructor(pattern: String, enable: Boolean) : this() {
            this.enable.isSelected = enable
            this.pattern.text = pattern
        }

        init {
            title = "Add Pattern"
            pattern.document.addDocumentListener(object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent?) {
                    generatorDemo(pattern.text)
                }

                override fun removeUpdate(e: DocumentEvent?) {
                    generatorDemo(pattern.text)
                }

                override fun changedUpdate(e: DocumentEvent?) {
                    generatorDemo(pattern.text)
                }

            })
            example.isEditable = false
            enable.isSelected = true
            init()
        }

        fun generatorDemo(pattern: String) {
            example.text = OpenGitmojiUtils.demo(pattern)
        }

        override fun createCenterPanel(): JComponent {
            return panel {
                row {
                    cell(enable)
                    // 添加帮助图标
                    contextHelp("Configure whether the regular expression takes effect. Some expressions do not need to take effect in real time, so this configuration item is provided.", "Enable pattern help")
                }
                row("Pattern: ") {
                    cell(pattern).align(Align.FILL)
                            .comment("""
                            The system provides the following placeholders by default:<br>
                            #{G}: Fill in gitmoji <br>
                            #{GU}: Fill in gimoji unicode <br>
                            #{DESC}: Fill in gitmoji description (English) <br>
                            #{DESC_CN}: Fill in gitmoji description (Chinese) <br>
                            #{DATE}: Fill in the current date <br>
                            #{TIME}: fill in the current time
                        """.trimIndent(), 50)
//                    cell(enable).gap(RightGap.COLUMNS)
                    // 添加帮助图标
//                    contextHelp("Configure whether the regular expression takes effect. Some expressions do not need to take effect in real time, so this configuration item is provided.", "Enable regex help")
                }.layout(RowLayout.PARENT_GRID)
                row("Example: ") {
                    cell(example).align(Align.FILL)
                }.layout(RowLayout.PARENT_GRID)
            }
        }

        fun load(): GitmojiPattern {
            return GitmojiPattern(pattern.text, enable.isSelected)
        }

        override fun doValidate(): ValidationInfo? {
            if (pattern.text == null || pattern.text.trim().isEmpty()) {
                return ValidationInfo("Pattern is required")
            }
            return super.doValidate()
        }

    }

    private class CustomEmojiBrowseFolderListener internal constructor(title: @DialogTitle String?,
                                                                       description: @NlsContexts.Label String?,
                                                                       textField: TextFieldWithBrowseButton?,
                                                                       project: Project?,
                                                                       fileChooserDescriptor: FileChooserDescriptor?)
        : BrowseFolderActionListener<JTextField?>(title, description, textField, project, fileChooserDescriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT) {

        override fun getInitialFile(): VirtualFile? {
            return super.getInitialFile()
        }

        override fun actionPerformed(e: ActionEvent?) {
            println("123")
            super.actionPerformed(e)
        }

    }

}