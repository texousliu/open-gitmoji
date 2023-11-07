// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.texousliu.opengitmoji.dialog

import com.github.texousliu.opengitmoji.context.OpenGitmojiContext
import com.github.texousliu.opengitmoji.model.RegexTableRowInfo
import com.github.texousliu.opengitmoji.utils.OpenGitmojiUtils
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.actionListener
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.table.DefaultTableModel


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

    return panel {
        group("Input") {
            row {
                cell(checkBox)
                        .gap(RightGap.SMALL)
                        .actionListener { event, component -> OpenGitmojiContext.setTriggerCondition(component.isSelected) }
                        .onReset {
                            checkBox.isSelected = OpenGitmojiContext.getConfigTriggerCondition()
                        }
            }.rowComment("Optimize input habits and reduce trouble caused by unnecessary prompts")
        }

        group("Prompt List") {
            row {
                label("Not indented row")
            }
            row {
                cell(createPromptListTable(jbTable))
                        .onReset {
                            (jbTable.model as DefaultTableModel).rowCount = 0
                            OpenGitmojiContext.getConfigRegexTableInfoObj().rows.forEach {
                                (jbTable.model as DefaultTableModel).addRow(arrayOf(it.regex, OpenGitmojiUtils.demo(it.regex), it.enable))
                            }
                        }
            }
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
    panel.preferredSize = Dimension(600, 400)
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
        title = "测试title"
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
                cell(enable)
            }
            row {
                label("Regex")
                cell(regex)
            }
            row {
                label("Demo")
                cell(demo)
            }
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