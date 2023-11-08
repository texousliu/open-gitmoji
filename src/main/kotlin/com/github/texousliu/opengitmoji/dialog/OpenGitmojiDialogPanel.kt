// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.texousliu.opengitmoji.dialog

import com.github.texousliu.opengitmoji.context.OpenGitmojiContext
import com.github.texousliu.opengitmoji.model.RegexTableRowInfo
import com.github.texousliu.opengitmoji.utils.OpenGitmojiUtils
import com.intellij.ide.IdeBundle
import com.intellij.ide.todo.TodoFilter
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.psi.search.TodoPattern
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import com.intellij.ui.table.JBTable
import java.awt.Dimension
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellEditor


@Suppress("DialogTitleCapitalization")
fun openGitmojiDialogPanel(): DialogPanel {
    val checkBox = JBCheckBox("Get prompt through text starting with ':' or '：'. Such as ':s'")
    checkBox.isSelected = OpenGitmojiContext.getConfigTriggerCondition()

    val jbTableModel = DefaultTableModel(arrayOf("regex", "demo", "enable"), 0)
    val jbTable = object : JBTable(jbTableModel){
        override fun isCellEditable(row: Int, column: Int): Boolean {
            return false
        }
    }
    jbTable.setShowGrid(false)

    object : DoubleClickListener() {
        override fun onDoubleClick(event: MouseEvent): Boolean {
            editSelectedRegex(jbTable)
            return true
        }
    }

    return panel {
        group("Input") {
            row {
                cell(checkBox)
                        .gap(RightGap.SMALL)
                        .actionListener { event, component -> OpenGitmojiContext.setTriggerCondition(component.isSelected) }
                        .onReset {
                            checkBox.isSelected = OpenGitmojiContext.getConfigTriggerCondition()
                        }.resizableColumn()
            }.layout(RowLayout.PARENT_GRID)
                    .rowComment("Optimize input habits and reduce trouble caused by unnecessary prompts")
        }.resizableColumn()

        group("Prompt List") {
            row {
                label("Configure Gitmoji filling expression and generate a filling content list through the expression for users to choose.")
            }.layout(RowLayout.PARENT_GRID)
            row {
                cell(createPromptListTable(jbTable))
                        .gap(RightGap.SMALL)
                        .onReset {
                            (jbTable.model as DefaultTableModel).rowCount = 0
                            OpenGitmojiContext.getConfigRegexTableInfoObj().rows.forEach {
                                (jbTable.model as DefaultTableModel).addRow(arrayOf(it.regex, OpenGitmojiUtils.demo(it.regex), it.enable))
                            }
                        }.resizableColumn()
                        .horizontalAlign(HorizontalAlign.FILL)
                        .verticalAlign(VerticalAlign.FILL)
            }.layout(RowLayout.PARENT_GRID).resizableRow()
        }
    }
}

fun editSelectedRegex(jbTable: JBTable) {
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

fun stopEditing(jbTable: JBTable) {
    if (jbTable.isEditing()) {
        val editor: TableCellEditor = jbTable.getCellEditor()
        if (editor != null) {
            editor.stopCellEditing()
        }
    }
}

fun createPromptListTable(jbTable: JBTable): JComponent {
    jbTable.columnModel.getColumn(0).preferredWidth = 250
    jbTable.columnModel.getColumn(1).preferredWidth = 250
    jbTable.columnModel.getColumn(2).preferredWidth = 50

    val panel = ToolbarDecorator.createDecorator(jbTable)
            .setAddAction {
                val regexDialog = AddRegexDialogWrapper()
                if (regexDialog.showAndGet()) {
                    val load = regexDialog.load()
                    (jbTable.model as DefaultTableModel).addRow(load)
                    setRegexTableInfoRow(load[0] as String, load[2] as Boolean)
                }
            }.setEditAction(null)
            .setMoveUpAction(null)
            .setMoveDownAction(null)
            .setRemoveAction {
                val row = jbTable.selectedRow
                (jbTable.model as DefaultTableModel).removeRow(row)
                removeRegexTableInfoRow(row)
            }.createPanel()
    panel.preferredSize = Dimension(0, 400)
    return panel
}

fun setRegexTableInfoRow(regex: String, enable: Boolean) {
    val regexTableInfoObj = OpenGitmojiContext.getRegexTableInfoObj()
    regexTableInfoObj.add(RegexTableRowInfo(regex, enable))
    OpenGitmojiContext.setRegexTableInfo(regexTableInfoObj)
}

fun removeRegexTableInfoRow(index: Int) {
    val regexTableInfoObj = OpenGitmojiContext.getRegexTableInfoObj()
    regexTableInfoObj.removeAt(index)
    OpenGitmojiContext.setRegexTableInfo(regexTableInfoObj)
}

class AddRegexDialogWrapper : DialogWrapper(true) {

    var enable = JBCheckBox("Enable Regex")
    var regex = JBTextField(30)
    var demo = JBTextField(30)

    init {
        title = "Regex Info"
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