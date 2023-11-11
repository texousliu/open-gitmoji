// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.texousliu.opengitmoji.dialog

import com.github.texousliu.opengitmoji.context.OpenGitmojiContext
import com.github.texousliu.opengitmoji.model.GitmojiPattern
import com.github.texousliu.opengitmoji.utils.OpenGitmojiUtils
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.TableUtil
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import com.intellij.ui.table.JBTable
import java.awt.Dimension
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.table.TableCellEditor


class OpenGitmojiDialogPanel {

    val checkBox = JBCheckBox("Get prompt through text starting with ':' or '：'. Such as ':s'")
    val gitmojiPatterns = mutableListOf<GitmojiPattern>()
    private val jbTableModel = PatternsTableModel(gitmojiPatterns)
    private val jbTable = JBTable(jbTableModel)

    val dialogPanel = openGitmojiDialogPanel()

    init {
        jbTable.setShowGrid(false)
        object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                editSelectedPattern()
                return true
            }
        }.installOn(jbTable)
    }

    private fun openGitmojiDialogPanel(): DialogPanel {
        return panel {
            group("Input") {
                row {
                    cell(checkBox)
                            .gap(RightGap.SMALL)
                            .onReset {
                                checkBox.isSelected = OpenGitmojiContext.getTriggerWithColon()
                            }
                }.rowComment("Optimize input habits and reduce trouble caused by unnecessary prompts")
            }

            group("Prompt List") {
                row("Configure Gitmoji filling expression and generate a filling content list through the expression for users to choose.") { }
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
                            .horizontalAlign(HorizontalAlign.FILL)
                            .verticalAlign(VerticalAlign.FILL)
                }.layout(RowLayout.PARENT_GRID).resizableRow()
            }
        }
    }

    private fun createPromptListTable(): JComponent {
        jbTable.columnModel.getColumn(0).preferredWidth = 250
        jbTable.columnModel.getColumn(1).preferredWidth = 250
        jbTable.columnModel.getColumn(2).preferredWidth = 50

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
        panel.preferredSize = Dimension(0, 400)
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

        val dialog = PatternInfoDialogWrapper(selectPattern.regex, selectPattern.enable)
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
        var regex = JBTextField(30)
        var demo = JBTextField(30)

        constructor(regex: String, enable: Boolean) : this() {
            this.enable.isSelected = enable
            this.regex.text = regex
        }

        init {
            title = "Add Pattern"
            regex.document.addDocumentListener(object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent?) {
                    generatorDemo(regex.text)
                }

                override fun removeUpdate(e: DocumentEvent?) {
                    generatorDemo(regex.text)
                }

                override fun changedUpdate(e: DocumentEvent?) {
                    generatorDemo(regex.text)
                }

            })
            demo.isEditable = false
            enable.isSelected = true
            init()
        }

        fun generatorDemo(regex: String) {
            demo.text = OpenGitmojiUtils.demo(regex)
        }

        override fun createCenterPanel(): JComponent {
            return panel {
                row {
                    cell(enable).gap(RightGap.SMALL)
                    // 添加帮助图标
                    contextHelp("Configure whether the regular expression takes effect. Some expressions do not need to take effect in real time, so this configuration item is provided.", "Enable regex help")
                }
                row("Regex: ") {
                    cell(regex).horizontalAlign(HorizontalAlign.FILL)
                            .comment("""
                            Prompt list expression configuration. The system provides the following placeholders by default:<br>
                            #{G}: Fill in gitmoji <br>
                            #{GU}: Fill in gimoji unicode <br>
                            #{DESC}: Fill in gitmoji description (English) <br>
                            #{DESC_CN}: Fill in gitmoji description (Chinese) <br>
                            #{DATE}: Fill in the current date <br>
                            #{TIME}: fill in the current time
                        """.trimIndent())
                }.layout(RowLayout.PARENT_GRID)
                row("Demo: ") {
                    cell(demo).horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.PARENT_GRID)
            }
        }

        fun load(): GitmojiPattern {
            return GitmojiPattern(regex.text, enable.isSelected)
        }

        override fun doValidate(): ValidationInfo? {
            if (regex.text == null || regex.text.trim().isEmpty()) {
                return ValidationInfo("Regex is required")
            }
            return super.doValidate()
        }

    }

}