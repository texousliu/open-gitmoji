package com.github.texousliu.open.emoji.dialog

import com.github.texousliu.open.emoji.dialog.renderer.OpenEmojiInfoBooleanTableCellRenderer
import com.github.texousliu.open.emoji.dialog.renderer.OpenEmojiInfoIconTableCellRenderer
import com.github.texousliu.open.emoji.dialog.renderer.OpenEmojiInfoStringTableCellRenderer
import com.github.texousliu.open.emoji.dialog.renderer.OpenEmojiInfoTypeTableCellRenderer
import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.github.texousliu.open.emoji.model.OpenEmojiInfoType
import com.github.texousliu.open.emoji.persistence.OpenEmojiPersistent
import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.observable.util.whenTextChanged
import com.intellij.openapi.ui.*
import com.intellij.ui.*
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import com.intellij.ui.scale.JBUIScale.scale
import com.intellij.ui.table.JBTable
import java.awt.Dimension
import java.awt.event.MouseEvent
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.table.TableCellEditor


class OpenEmojiInfoDialogPanel {

    private val headerGap = scale(20)

    val emojiInfoList = mutableListOf<OpenEmojiInfo>()
    private val emojiInfoTableModel = OpenEmojiInfoTableModel(emojiInfoList)
    private val emojiInfoTable = JBTable(emojiInfoTableModel)

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

        configureStartDirectoryField()
    }

    private fun emojiInfoSettingsDialogPanel(): DialogPanel {
        return panel {
            row("Custom Emoji Folder:") {
                cell(customEmojiDirectoryComponent)
                        .resizableColumn()
                        .horizontalAlign(HorizontalAlign.FILL)
//                        .onChanged {
//                            customEmojiDirectoryChanged(it.text)
//                        }
                        .onReset {
                            customEmojiDirectoryComponent.text =
                                    OpenEmojiPersistent.getInstance().getCustomEmojiDirectory()
                        }
            }.rowComment("Configure your own emojis beyond additional system presets. <a href='https://github.com/texousliu/open-gitmoji'>Documents</a>")
            row {
                cell(createEmojiConfigTable())
                        .onReset {
                            emojiInfoList.clear()
                            OpenEmojiPersistent.getInstance().getOpenEmojiInfoList().forEach {
                                emojiInfoList.add(it.clone())
                            }
                            emojiInfoTableModel.fireTableDataChanged()
                        }.resizableColumn()
                        .horizontalAlign(HorizontalAlign.FILL)
                        .verticalAlign(VerticalAlign.FILL)
            }.resizableRow()
        }
    }

    private fun customEmojiDirectoryChanged(directory: String) {
        stopEditing()
        // 获取所有自定义 emoji
        OpenEmojiUtils.emojiInfoListWithCustom(directory, emojiInfoList)
        // 更新表格
        withSelectionFireTableDataChanged()
    }

    private fun configureStartDirectoryField() {
        customEmojiDirectoryComponent.addBrowseFolderListener(
                "Choose Custom Emoji Folder",
                "Choose custom emoji folder",
                null,
                FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        )
        customEmojiDirectoryTextField.whenTextChanged {
            customEmojiDirectoryChanged(customEmojiDirectoryComponent.text)
        }
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
        iconColumn.cellRenderer = OpenEmojiInfoIconTableCellRenderer(emojiInfoList)

        val codeColumn = emojiInfoTable.columnModel.getColumn(1)
        codeColumn.minWidth = scale(150)
        codeColumn.cellRenderer = OpenEmojiInfoStringTableCellRenderer(emojiInfoList)

        val descriptionColumn = emojiInfoTable.columnModel.getColumn(2)
        descriptionColumn.preferredWidth = descriptionColumn.maxWidth
        descriptionColumn.cellRenderer = OpenEmojiInfoStringTableCellRenderer(emojiInfoList)

        val typeColumn = emojiInfoTable.columnModel.getColumn(3)
        typeColumn.minWidth = headerFontMetrics.stringWidth(OpenEmojiInfoType.OVERRIDE.name) + headerGap
        typeColumn.cellRenderer = OpenEmojiInfoTypeTableCellRenderer(emojiInfoList)

        val enableColumn = emojiInfoTable.columnModel.getColumn(4)
        val enableWidth = headerFontMetrics.stringWidth(emojiInfoTable.getColumnName(4)) + headerGap
        enableColumn.maxWidth = scale(enableWidth)
        enableColumn.minWidth = enableColumn.maxWidth
        enableColumn.cellRenderer = OpenEmojiInfoBooleanTableCellRenderer(emojiInfoList)
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
                }.addExtraActions(OpenEmojiInfoResetFromDiskAnAction(AllIcons.General.Reset, this),
                        OpenEmojiInfoReloadCustomAnAction(AllIcons.Actions.BuildAutoReloadChanges, this))
                .createPanel()
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

    private fun withSelectionFireTableDataChanged() {
        var selectedRow = emojiInfoTable.selectedRow
        if (selectedRow < 0) selectedRow = emojiInfoList.size - 1
        if (selectedRow >= emojiInfoList.size) selectedRow = emojiInfoList.size - 1
        emojiInfoTableModel.fireTableDataChanged()
        emojiInfoTable.selectionModel.setSelectionInterval(selectedRow, selectedRow)
        emojiInfoTable.scrollRectToVisible(emojiInfoTable.getCellRect(selectedRow, 0, true))
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

    fun markModifiedEmojis(): Boolean {
        var modify = false
        val config = OpenEmojiPersistent.getInstance().getOpenEmojiInfoList()
        if (config.size != emojiInfoList.size) modify = true
        for (emojiInfo in emojiInfoList) {
            val indexOf = config.indexOf(emojiInfo)
            emojiInfo.changed = false
            if (indexOf < 0) {
                emojiInfo.changed = true
                modify = true
            } else {
                val cei = config[indexOf]
                if (cei.enable != emojiInfo.enable
                        || cei.emoji != emojiInfo.emoji
                        || cei.type != emojiInfo.type) {
                    emojiInfo.changed = true
                    modify = true
                }
            }
        }
        if (modify) {
            withSelectionFireTableDataChanged()
        }
        return modify
    }

    fun resetToDefault() {
        emojiInfoList.clear()
        emojiInfoList.addAll(OpenEmojiUtils.emojiInfoList(customEmojiDirectoryComponent.text))
        withSelectionFireTableDataChanged()
    }

    fun reloadCustom() {
        customEmojiDirectoryChanged(customEmojiDirectoryComponent.text)
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
                    cell(type).horizontalAlign(HorizontalAlign.FILL)
                }
                row {
                    label("Emoji: ")
                    cell(emoji).horizontalAlign(HorizontalAlign.FILL)
                    label("Code: ")
                    cell(code).horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.PARENT_GRID)
                row {
                    label("Name: ")
                    cell(name).horizontalAlign(HorizontalAlign.FILL)
                    label("Entity: ")
                    cell(entity).horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.PARENT_GRID)
                row("Description: ") {
                    cell(description).horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.PARENT_GRID)
                row("Description CN: ") {
                    cell(cnDescription).horizontalAlign(HorizontalAlign.FILL)
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

    private class OpenEmojiInfoResetFromDiskAnAction(icon: Icon,
                                                     var panel: OpenEmojiInfoDialogPanel) : AnActionButton({ "Reset to default" }, { "reset to default with custom directory emojis" }, icon) {
        override fun actionPerformed(e: AnActionEvent) {
            panel.resetToDefault()
        }
    }

    private class OpenEmojiInfoReloadCustomAnAction(icon: Icon,
                                                    var panel: OpenEmojiInfoDialogPanel) : AnActionButton({ "Reload custom emoji but don`t remove exists" }, { "reload custom emoji but not remove exists custom emojis" }, icon) {
        override fun actionPerformed(e: AnActionEvent) {
            panel.reloadCustom()
        }
    }

}