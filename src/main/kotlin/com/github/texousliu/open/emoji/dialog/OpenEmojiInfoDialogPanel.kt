package com.github.texousliu.open.emoji.dialog

import com.github.texousliu.open.emoji.config.OpenEmojiBundle
import com.github.texousliu.open.emoji.context.OpenEmojiCache
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
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
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
            row(OpenEmojiBundle.message("settings.info.custom.directory.label")) {
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
            }.rowComment(OpenEmojiBundle.message("settings.info.custom.directory.comment"))
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
        withSelectionFireTableDataChanged(null)
    }

    private fun configureStartDirectoryField() {
        customEmojiDirectoryComponent.addBrowseFolderListener(
                OpenEmojiBundle.message("settings.info.custom.directory.choose.title"),
                OpenEmojiBundle.message("settings.info.custom.directory.choose.desc"),
                null,
                FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        )
        customEmojiDirectoryTextField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                customEmojiDirectoryChanged(customEmojiDirectoryComponent.text)
            }

            override fun removeUpdate(e: DocumentEvent?) {
                customEmojiDirectoryChanged(customEmojiDirectoryComponent.text)
            }

            override fun changedUpdate(e: DocumentEvent?) {
                customEmojiDirectoryChanged(customEmojiDirectoryComponent.text)
            }
        })
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
            val add = addEmojiDialog.load()
            add.type = OpenEmojiInfoType.CUSTOM
            val existsIndex = emojiInfoList.indexOf(add)
            if (existsIndex < 0) {
                add.markCustom(true)
                emojiInfoList.add(add)
                val index: Int = emojiInfoList.size - 1
//                emojiInfoTableModel.fireTableRowsInserted(index, index)
//                emojiInfoTable.selectionModel.setSelectionInterval(index, index)
//                emojiInfoTable.scrollRectToVisible(emojiInfoTable.getCellRect(index, 0, true))
                withSelectionFireTableDataChanged(index)
            } else {
                val exists = emojiInfoList[existsIndex]
                exists.change(add)
                withSelectionFireTableDataChanged(existsIndex)
            }
        }
    }

    private fun withSelectionFireTableDataChanged(sr: Int?) {
        var selectedRow = sr ?: emojiInfoTable.selectedRow
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
        dialog.enableEdit()
        if (!dialog.showAndGet()) {
            return
        }
        selectConfig.change(dialog.load())
        emojiInfoTableModel.fireTableRowsUpdated(selectedIndex, selectedIndex)
        emojiInfoTable.selectionModel.setSelectionInterval(selectedIndex, selectedIndex)
    }

    private fun removeEmoji() {
        stopEditing()
        val selectedIndex: Int = emojiInfoTable.selectedRow
        if (selectedIndex >= 0 && selectedIndex < emojiInfoTableModel.rowCount) {
            val select = emojiInfoList[selectedIndex]
            if (select.type == OpenEmojiInfoType.OVERRIDE) {
                emojiInfoList[selectedIndex] = OpenEmojiCache.get(select)
                emojiInfoTableModel.fireTableRowsUpdated(selectedIndex, selectedIndex)
                emojiInfoTable.selectionModel.setSelectionInterval(selectedIndex, selectedIndex)
            } else {
                TableUtil.removeSelectedItems(emojiInfoTable)
            }
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
                if (cei.modified(emojiInfo)) {
                    emojiInfo.changed = true
                    modify = true
                }
            }
        }
        if (modify) {
            withSelectionFireTableDataChanged(null)
        }
        return modify
    }

    fun resetToDefault() {
        emojiInfoList.clear()
        emojiInfoList.addAll(OpenEmojiUtils.emojiInfoList(customEmojiDirectoryComponent.text))
        withSelectionFireTableDataChanged(null)
    }

    fun reloadCustom() {
        customEmojiDirectoryChanged(customEmojiDirectoryComponent.text)
    }

    private class EmojiConfigInfoDialogWrapper() : DialogWrapper(true) {
        var enable = JBCheckBox(OpenEmojiBundle.message("settings.info.emoji.enable.label"))
        var icon = JBLabel(AllIcons.Actions.Refresh)
        var type = JBTextField()
        var emoji = JBTextField()
        var entity = JBTextField()
        var code = JBTextField()
        var name = JBTextField()
        var description = JBTextField()
        var cnDescription = JBTextField()
        var iconPath = JBTextField()
        var isCustom = true


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
            this.iconPath.text = config.getIconPath()
            this.isCustom = config.getCustom()
        }

        init {
            title = OpenEmojiBundle.message("settings.info.emoji.title.add")
            type.isEditable = false
            emoji.isEditable = true
            entity.isEditable = true
            code.isEditable = true
            name.isEditable = true
            description.isEditable = true
            cnDescription.isEditable = true
            enable.isSelected = true
            iconPath.isEditable = false

            OpenEmojiUtils.addDocListener(code, this::generatorIconPath)

            init()
        }

        private fun generatorIconPath(code: String) {
            iconPath.text = OpenEmojiUtils.getIconPath(code, isCustom).replace("\\", "/")
        }

        override fun createCenterPanel(): JComponent {
            return panel {
                row {
                    cell(enable).gap(RightGap.SMALL)
                    cell(icon)
                }
                row {
                    cell(iconPath)
                            .horizontalAlign(HorizontalAlign.FILL)
                }
                row(OpenEmojiBundle.message("settings.info.emoji.type.label")) {
                    cell(type)
                            .horizontalAlign(HorizontalAlign.FILL)
                }
                row {
                    label(OpenEmojiBundle.message("settings.info.emoji.emoji.label"))
                    cell(emoji)
                            .horizontalAlign(HorizontalAlign.FILL)
                    label(OpenEmojiBundle.message("settings.info.emoji.code.label"))
                    cell(code)
                            .horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.PARENT_GRID)
                row {
                    label(OpenEmojiBundle.message("settings.info.emoji.name.label"))
                    cell(name)
                            .horizontalAlign(HorizontalAlign.FILL)
                    label(OpenEmojiBundle.message("settings.info.emoji.entity.label"))
                    cell(entity)
                            .horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.PARENT_GRID)
                row(OpenEmojiBundle.message("settings.info.emoji.desc.label")) {
                    cell(description)
                            .horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.PARENT_GRID)
                row(OpenEmojiBundle.message("settings.info.emoji.desc-cn.label")) {
                    cell(cnDescription)
                            .horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.PARENT_GRID)
            }
        }

        fun load(): OpenEmojiInfo {
            return OpenEmojiInfo(
                    emoji.text, entity.text, code.text,
                    name.text, description.text, cnDescription.text, enable.isSelected
            )
        }

        fun enableEdit() {
            this.title = OpenEmojiBundle.message("settings.info.emoji.title.edit")
            this.emoji.isEditable = false
            this.code.isEditable = false
            this.iconPath.isEditable = false
        }

        override fun doValidate(): ValidationInfo? {
            if (isEmpty(emoji.text) || isEmpty(code.text)) {
                return ValidationInfo(OpenEmojiBundle.message("settings.info.emoji.validate.info"))
            }
            return super.doValidate()
        }

        private fun isEmpty(str: String?): Boolean {
            return str == null || str.trim().isEmpty()
        }

    }

    private class OpenEmojiInfoResetFromDiskAnAction(icon: Icon,
                                                     var panel: OpenEmojiInfoDialogPanel)
        : AnActionButton({ OpenEmojiBundle.message("settings.info.emoji.reset.title") },
            { OpenEmojiBundle.message("settings.info.emoji.reset.desc") }, icon) {
        override fun actionPerformed(e: AnActionEvent) {
            panel.resetToDefault()
        }
    }

    private class OpenEmojiInfoReloadCustomAnAction(icon: Icon,
                                                    var panel: OpenEmojiInfoDialogPanel)
        : AnActionButton({ OpenEmojiBundle.message("settings.info.emoji.reload.title") },
            { OpenEmojiBundle.message("settings.info.emoji.reload.desc") }, icon) {
        override fun actionPerformed(e: AnActionEvent) {
            panel.reloadCustom()
        }
    }

}