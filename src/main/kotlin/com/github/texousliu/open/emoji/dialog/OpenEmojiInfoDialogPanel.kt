package com.github.texousliu.open.emoji.dialog

import com.github.texousliu.open.emoji.context.OpenEmojiContext
import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.intellij.icons.AllIcons
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
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
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.table.TableCellEditor


class OpenEmojiInfoDialogPanel {

    val emojiInfos = mutableListOf<OpenEmojiInfo>()
    private val emojiInfoTableModel = OpenEmojiInfoTableModel(emojiInfos)
    private val emojiInfoTable = JBTable(emojiInfoTableModel)

    val emojiInfoSettingsPanel = emojiInfoSettingsDialogPanel()

    init {
        emojiInfoTable.setShowGrid(false)
        object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                editSelectedEmoji()
                return true
            }
        }.installOn(emojiInfoTable)
    }

    private fun emojiInfoSettingsDialogPanel(): DialogPanel {
        return panel {
            row("List of all loaded emojis") { }
            row {
                cell(createEmojiConfigTable())
                    .gap(RightGap.SMALL)
                    .onReset {
                        emojiInfos.clear()
                        OpenEmojiContext.emojiInfos().forEach {
                            emojiInfos.add(it.clone())
                        }
                        emojiInfoTableModel.fireTableDataChanged()
                    }.resizableColumn()
                    .align(Align.FILL)
            }.layout(RowLayout.PARENT_GRID).resizableRow()
        }
    }

    private fun createEmojiConfigTable(): JComponent {
        val tableHeader = emojiInfoTable.tableHeader
        val headerFontMetrics = tableHeader.getFontMetrics(tableHeader.font)

        val iconColumn = emojiInfoTable.columnModel.getColumn(0)
        val iconColumnWidth = headerFontMetrics.stringWidth(
            emojiInfoTable.getColumnName(0) + scale(20)
        )
        iconColumn.setPreferredWidth(iconColumnWidth)
        iconColumn.setMinWidth(iconColumnWidth)
        iconColumn.setCellRenderer(OpenEmojiIconTableCellRenderer())

        val codeColumn = emojiInfoTable.columnModel.getColumn(1)
        codeColumn.preferredWidth = scale(100)

        val descriptionColumn = emojiInfoTable.columnModel.getColumn(2)
        descriptionColumn.preferredWidth = descriptionColumn.maxWidth

        val enableColumn = emojiInfoTable.columnModel.getColumn(3)
        val enableWidth = headerFontMetrics.stringWidth(emojiInfoTable.getColumnName(3)) + scale(20)
        enableColumn.maxWidth = scale(enableWidth)
        enableColumn.minWidth = enableColumn.maxWidth
        enableColumn.cellRenderer = BooleanTableCellRenderer()
        enableColumn.cellEditor = BooleanTableCellEditor()

        val panel = ToolbarDecorator.createDecorator(emojiInfoTable)
            .setAddAction {
                addEmoji()
            }.setEditAction {
                editSelectedEmoji()
            }
            .setMoveUpAction(null)
            .setMoveDownAction(null)
            .setRemoveAction {
                removeEmoji()
            }.createPanel()
        panel.preferredSize = Dimension(0, 300)
        return panel
    }

    private fun addEmoji() {
        stopEditing()
        val addEmojiDialog = EmojiConfigInfoDialogWrapper()
        if (addEmojiDialog.showAndGet()) {
            emojiInfos.add(addEmojiDialog.load())

            val index: Int = emojiInfos.size - 1
            emojiInfoTableModel.fireTableRowsInserted(index, index)
            emojiInfoTable.selectionModel.setSelectionInterval(index, index)
            emojiInfoTable.scrollRectToVisible(emojiInfoTable.getCellRect(index, 0, true))
        }
    }

    private fun editSelectedEmoji() {
        stopEditing()
        val selectedIndex: Int = emojiInfoTable.selectedRow
        if (selectedIndex < 0 || selectedIndex >= emojiInfoTableModel.rowCount) {
            return
        }
        // 获取选择的内容
        val selectConfig = emojiInfos[selectedIndex]

        val dialog = EmojiConfigInfoDialogWrapper(selectConfig)
        dialog.title = "Edit Emoji"
        if (!dialog.showAndGet()) {
            return
        }
        emojiInfos[selectedIndex] = dialog.load()
        emojiInfoTableModel.fireTableRowsUpdated(selectedIndex, selectedIndex)
        emojiInfoTable.selectionModel.setSelectionInterval(selectedIndex, selectedIndex)
    }

    private fun removeEmoji() {
        stopEditing()
        val selectedIndex: Int = emojiInfoTable.selectedRow
        if (selectedIndex >= 0 && selectedIndex < emojiInfoTableModel.rowCount) {
            TableUtil.removeSelectedItems(emojiInfoTable)
        }
    }

    private fun stopEditing() {
        if (emojiInfoTable.isEditing) {
            val editor: TableCellEditor = emojiInfoTable.cellEditor
            editor.stopCellEditing()
        }
    }

    private class EmojiConfigInfoDialogWrapper() : DialogWrapper(true) {

        var enable = JBCheckBox("Enable emoji")
        var icon = AllIcons.Actions.Refresh
        var emoji = JBTextField()
        var entity = JBTextField()
        var code = JBTextField()
        var name = JBTextField()
        var description = JBTextField()
        var cnDescription = JBTextField()


        constructor(config: OpenEmojiInfo) : this() {
            this.enable.isSelected = config.enable
            this.icon = config.getIcon()
            this.emoji.text = config.emoji
            this.entity.text = config.entity
            this.code.text = config.code
            this.name.text = config.name
            this.description.text = config.description
            this.cnDescription.text = config.cnDescription
        }

        init {
            title = "Add Emoji"
            emoji.isEditable = false
            entity.isEditable = false
            code.isEditable = false
            name.isEditable = false
            description.isEditable = false
            cnDescription.isEditable = false
            enable.isSelected = true
            init()
        }

        override fun createCenterPanel(): JComponent {
            return panel {
                row {
                    cell(enable)
                    icon(icon)
                }
                row {
                    label("Emoji: ")
                    cell(emoji).align(Align.FILL)
                    label("Code: ")
                    cell(code).align(Align.FILL)
                }.layout(RowLayout.PARENT_GRID)
                row {
                    label("Name: ")
                    cell(name).align(Align.FILL)
                    label("Entity: ")
                    cell(entity).align(Align.FILL)
                }.layout(RowLayout.PARENT_GRID)
                row("Description: ") {
                    cell(description).align(Align.FILL)
                }.layout(RowLayout.PARENT_GRID)
                row("Description CN: ") {
                    cell(cnDescription).align(Align.FILL)
                }.layout(RowLayout.PARENT_GRID)
            }
        }

        fun load(): OpenEmojiInfo {
            return OpenEmojiInfo(
                emoji.text, entity.text, code.text,
                name.text, description.text, cnDescription.text, enable.isSelected
            )
        }

        override fun doValidate(): ValidationInfo? {
            if (isEmpty(emoji.text) || isEmpty(code.text)) {
                return ValidationInfo("Emoji and code is required")
            }
            return super.doValidate()
        }

        private fun isEmpty(str: String?): Boolean {
            return str == null || str.trim().isEmpty()
        }

    }

}