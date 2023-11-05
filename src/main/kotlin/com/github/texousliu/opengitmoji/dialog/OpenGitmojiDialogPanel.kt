// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.texousliu.opengitmoji.dialog

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.table.JBTable
import java.awt.Dimension
import java.util.*
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.table.DefaultTableColumnModel
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableColumn
import javax.swing.table.TableColumnModel


@Suppress("DialogTitleCapitalization")
fun openGitmojiDialogPanel(): DialogPanel {
    return panel {
        group("Input") {
            row {
                checkBox("Get prompt through text starting with ':' or '：'. Such as ':s'")
                        .gap(RightGap.SMALL)
                        .actionListener { event, component -> println(component.isSelected) }
                        .onApply {
                            println("apply")
                        }
                        .onReset {
                            println("reset")
                        }
            }.rowComment("Optimize input habits and reduce trouble caused by unnecessary prompts")
        }

        group("Prompt List") {
            row {
                label("Not indented row")
            }
            row {
                cell(createPromptListTable())
            }
        }.onApply {
            println("group apply")
        }.onReset {
            println("group reset")
        }
    }
}

fun createPromptListTable() : JComponent {
    val tableModel = DefaultTableModel(arrayOf("regex", "demo", "enable"), 20)

    val jbTable = JBTable(tableModel)
    val jbpopup = createRegexPopup()

//    jbTable.preferredScrollableViewportSize = Dimension(520, 500)

//    val columnModel = DefaultTableColumnModel()
//    columnModel.addColumn(TableColumn(0, 2000))
//    columnModel.addColumn(TableColumn(1, 2000))
//    columnModel.addColumn(TableColumn(2, 500))
//    jbTable.columnModel = columnModel
    jbTable.columnModel.getColumn(0).preferredWidth = 250
    jbTable.columnModel.getColumn(1).preferredWidth = 250
    jbTable.columnModel.getColumn(2).preferredWidth = 50

    val panel = ToolbarDecorator.createDecorator(jbTable).setAddAction {
        tableModel.addRow(arrayOf("test", "demo", "enable"))
        println(it)
        jbpopup.showInCenterOf(jbTable)
    }.setEditAction {
        println(it)
    }.setMoveUpAction {
        println(it)
    }.setMoveDownAction {
        println(it)
    }.setRemoveAction {
        println(it)
    }.createPanel()
    panel.preferredSize = Dimension(550, 400)
    return panel
}

fun createRegexPopup() : JBPopup {
//    return JBPopupFactory.getInstance().createBalloonBuilder(panel {
//        row { label("fadkljfalksdjflkasjdflkajsdflkjasdfl") }
//        row { label("akjhdfasdklfjaksdjflkasjdflkjasdlfkjaslkdjf") }
//    }).setTitle("title").createBalloon()
    return JBPopupFactory.getInstance()
            .createConfirmation("tanchuang", "add", "cancel", {  }, {  }, 0)
}