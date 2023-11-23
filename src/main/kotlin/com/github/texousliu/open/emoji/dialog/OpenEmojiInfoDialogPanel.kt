package com.github.texousliu.open.emoji.dialog

import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.github.texousliu.open.emoji.model.OpenEmojiInfoType
import com.github.texousliu.open.emoji.persistence.OpenEmojiPersistent
import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.*
import com.intellij.ui.*
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
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

    private val headerGap = scale(20)

    val emojiInfoList = mutableListOf<OpenEmojiInfo>()
    private val emojiInfoTableModel = OpenEmojiInfoTableModel(emojiInfoList)
    private val emojiInfoTable = JBTable(emojiInfoTableModel)
    private val refreshAction = JBLabel(AllIcons.Actions.Refresh)

    val customEmojiDirectoryTextField = JBTextField()
    private val customEmojiDirectoryComponent = TextFieldWithBrowseButton(customEmojiDirectoryTextField)

    val emojiInfoSettingsPanel = emojiInfoSettingsDialogPanel()

    init {
        emojiInfoTable.setShowGrid(false)
        object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                editSelectedEmoji()
                return true
            }
        }.installOn(emojiInfoTable)

        object : ClickListener() {
            override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
                customEmojiDirectoryChanged(customEmojiDirectoryComponent.text)
                return true
            }
        }.installOn(refreshAction)

        configureStartDirectoryField()
    }

    private fun emojiInfoSettingsDialogPanel(): DialogPanel {
        return panel {
            row("Custom Emoji Folder:") {
                cell(customEmojiDirectoryComponent).align(Align.FILL)
                    .onChanged {
                        customEmojiDirectoryChanged(it.text)
                    }
                    .onReset {
                        customEmojiDirectoryComponent.text =
                            OpenEmojiPersistent.getInstance().getCustomEmojiDirectory()
                    }
            }.rowComment("Configure your own emojis beyond additional system presets. <a href='https://github.com/texousliu/open-gitmoji'>Documents</a>")
            row("List of all loaded emojis") {
                cell(refreshAction).gap(RightGap.SMALL)
            }
            row {
                cell(createEmojiConfigTable())
                    .onReset {
                        emojiInfoList.clear()
                        OpenEmojiPersistent.getInstance().getOpenEmojiInfoList().forEach {
                            emojiInfoList.add(it.clone())
                        }
                        emojiInfoTableModel.fireTableDataChanged()
                    }.resizableColumn()
                    .align(Align.FILL)
            }.layout(RowLayout.PARENT_GRID).resizableRow()
        }
    }

    private fun customEmojiDirectoryChanged(directory: String) {
        stopEditing()
        // 获取所有自定义 emoji
        OpenEmojiUtils.emojiInfoListWithCustom(directory, emojiInfoList)
        // 更新表格
        emojiInfoTableModel.fireTableDataChanged()
    }

    private fun configureStartDirectoryField() {
        customEmojiDirectoryComponent.addBrowseFolderListener(
            "Choose Custom Emoji Folder",
            "Choose custom emoji folder",
            null,
            FileChooserDescriptorFactory.createSingleFolderDescriptor(),
            TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        )
    }

    private fun createEmojiConfigTable(): JComponent {
        val tableHeader = emojiInfoTable.tableHeader
        val headerFontMetrics = tableHeader.getFontMetrics(tableHeader.font)

        val iconColumn = emojiInfoTable.columnModel.getColumn(0)
        val iconColumnWidth = headerFontMetrics.stringWidth(
            emojiInfoTable.getColumnName(0) + headerGap
        )
        iconColumn.preferredWidth = iconColumnWidth
        iconColumn.minWidth = iconColumnWidth
        iconColumn.cellRenderer = OpenEmojiIconTableCellRenderer()

        val codeColumn = emojiInfoTable.columnModel.getColumn(1)
        codeColumn.minWidth = scale(150)

        val descriptionColumn = emojiInfoTable.columnModel.getColumn(2)
        descriptionColumn.preferredWidth = descriptionColumn.maxWidth

        val typeColumn = emojiInfoTable.columnModel.getColumn(3)
        typeColumn.minWidth = headerFontMetrics.stringWidth(OpenEmojiInfoType.OVERRIDE.name) + headerGap
        typeColumn.cellRenderer = OpenEmojiInfoTypeTableCellRenderer()

        val enableColumn = emojiInfoTable.columnModel.getColumn(4)
        val enableWidth = headerFontMetrics.stringWidth(emojiInfoTable.getColumnName(4)) + headerGap
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
            emojiInfoList.add(addEmojiDialog.load())

            val index: Int = emojiInfoList.size - 1
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
        val selectConfig = emojiInfoList[selectedIndex]

        val dialog = EmojiConfigInfoDialogWrapper(selectConfig)
        dialog.title = "Edit Emoji"
        if (!dialog.showAndGet()) {
            return
        }
        emojiInfoList[selectedIndex] = dialog.load()
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
        var icon = JBLabel(AllIcons.Actions.Refresh)
        var type = JBTextField()
        var emoji = JBTextField()
        var entity = JBTextField()
        var code = JBTextField()
        var name = JBTextField()
        var description = JBTextField()
        var cnDescription = JBTextField()


        constructor(config: OpenEmojiInfo) : this() {
            this.enable.isSelected = config.enable
            this.type.text = config.type.name
            this.emoji.text = config.emoji
            this.entity.text = config.entity
            this.code.text = config.code
            this.name.text = config.name
            this.description.text = config.description
            this.cnDescription.text = config.cnDescription
            this.icon.icon = config.getIcon()
        }

        init {
            title = "Add Emoji"
            type.isEditable = false
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
                    cell(enable).gap(RightGap.SMALL)
                    cell(icon)
                }
                row("Type: ") {
                    cell(type).align(Align.FILL)
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