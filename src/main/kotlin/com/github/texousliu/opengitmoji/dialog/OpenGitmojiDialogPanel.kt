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
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
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
                editSelectedRegex()
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
//                            .actionListener { event, component -> OpenGitmojiContext.setTriggerCondition(component.isSelected) }
                            .onReset {
                                checkBox.isSelected = OpenGitmojiContext.getConfigTriggerWithColon()
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

//                                (jbTable.model as DefaultTableModel).rowCount = 0
//                                OpenGitmojiContext.getConfigRegexTableInfoObj().rows.forEach {
//                                    (jbTable.model as DefaultTableModel).addRow(arrayOf(it.regex, OpenGitmojiUtils.demo(it.regex), it.enable))
//                                }
                            }.resizableColumn()
                            .align(Align.FILL)
                }.layout(RowLayout.PARENT_GRID).resizableRow()
            }
        }
    }

    private fun editSelectedRegex() {
//    val jbTableModel = jbTable.model as DefaultTableModel
//    stopEditing(jbTable)
//    val selectedIndex: Int = jbTable.getSelectedRow()
//    if (selectedIndex < 0 || selectedIndex >= jbTableModel.getRowCount()) {
//        return
//    }
//    // 获取选择的内容
//    val
//    val rowInfo = OpenGitmojiContext.getRegexTableInfoObj().rows.get(selectedIndex)
//    val dialog = PatternDialog(myPanel, pattern, selectedIndex, myPatterns)
//    dialog.setTitle(IdeBundle.message("title.edit.todo.pattern"))
//    if (!dialog.showAndGet()) {
//        return
//    }
//    myPatterns.set(selectedIndex, pattern)
//    myPatternsModel.fireTableRowsUpdated(selectedIndex, selectedIndex)
//    myPatternsTable.getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex)
//    // Update model with patterns
//    // Update model with patterns
//    for (i in 0 until myFilters.size()) {
//        val filter: TodoFilter = myFilters.get(i)
//        if (filter.contains(sourcePattern)) {
//            filter.removeTodoPattern(sourcePattern)
//            filter.addTodoPattern(pattern)
//            myFiltersModel.fireTableRowsUpdated(i, i)
//        }
//    }
    }

    private fun stopEditing() {
        if (jbTable.isEditing) {
            val editor: TableCellEditor = jbTable.cellEditor
            editor.stopCellEditing()
        }
    }

    private fun createPromptListTable(): JComponent {
        jbTable.columnModel.getColumn(0).preferredWidth = 250
        jbTable.columnModel.getColumn(1).preferredWidth = 250
        jbTable.columnModel.getColumn(2).preferredWidth = 50

        val panel = ToolbarDecorator.createDecorator(jbTable)
                .setAddAction {
                    stopEditing()
                    val regexDialog = PatternInfoDialogWrapper()
                    if (regexDialog.showAndGet()) {
                        val load = regexDialog.load()
                        gitmojiPatterns.add(GitmojiPattern(load[0] as String, load[2] as Boolean))

                        val index: Int = gitmojiPatterns.size - 1
                        jbTableModel.fireTableRowsInserted(index, index)
                        jbTable.selectionModel.setSelectionInterval(index, index)
                        jbTable.scrollRectToVisible(jbTable.getCellRect(index, 0, true))

//                        (jbTable.model as DefaultTableModel).addRow(load)
//                        setRegexTableInfoRow(load[0] as String, load[2] as Boolean)
                    }
                }.setEditAction(null)
                .setMoveUpAction(null)
                .setMoveDownAction(null)
                .setRemoveAction {
//                    val row = jbTable.selectedRow
//                    (jbTable.model as DefaultTableModel).removeRow(row)
//                    removeRegexTableInfoRow(row)

                    stopEditing()
                    val selectedIndex: Int = jbTable.selectedRow
                    if (selectedIndex >= 0 && selectedIndex < jbTableModel.rowCount) {
                        TableUtil.removeSelectedItems(jbTable)
                    }
                }.createPanel()
        panel.preferredSize = Dimension(0, 400)
        return panel
    }

    private class PatternInfoDialogWrapper : DialogWrapper(true) {

        var enable = JBCheckBox("Enable pattern")
        var regex = JBTextField(30)
        var demo = JBTextField(30)

        init {
            title = "Pattern Info"
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
                    cell(regex).align(Align.FILL)
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
                    cell(demo).align(Align.FILL)
                }.layout(RowLayout.PARENT_GRID)
            }
        }

        fun load(): Array<Any> {
            return arrayOf(regex.text, demo.text, enable.isSelected)
        }

        override fun doValidate(): ValidationInfo? {
            if (regex.text == null || regex.text.trim().isEmpty()) {
                return ValidationInfo("Regex is required")
            }
            return super.doValidate()
        }

    }

}