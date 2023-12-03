package com.github.texousliu.open.emoji.dialog

import com.github.texousliu.open.emoji.config.OpenEmojiBundle
import com.github.texousliu.open.emoji.model.OpenEmojiPattern
import com.github.texousliu.open.emoji.persistence.OpenEmojiPersistent
import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.*
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.scale.JBUIScale.scale
import com.intellij.ui.table.JBTable
import java.awt.Dimension
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.table.TableCellEditor


class OpenEmojiDialogPanel {

    val triggerWithColonCheckBox = JBCheckBox(OpenEmojiBundle.message("settings.trigger.label"))
    val emojiPatterns = mutableListOf<OpenEmojiPattern>()
    private val emojiPatternsTableModel = OpenEmojiPatternsTableModel(emojiPatterns)
    private val emojiPatternsTable = JBTable(emojiPatternsTableModel)

    val emojiSettingsPanel = emojiSettingsDialogPanel()

    init {
        emojiPatternsTable.setShowGrid(false)
        object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                editSelectedPattern()
                return true
            }
        }.installOn(emojiPatternsTable)
    }

    private fun emojiSettingsDialogPanel(): DialogPanel {
        return panel {
            group(OpenEmojiBundle.message("settings.group.custom.title")) {
                row {
                    cell(triggerWithColonCheckBox)
                            .gap(RightGap.SMALL)
                            .onReset {
                                triggerWithColonCheckBox.isSelected =
                                        OpenEmojiPersistent.getInstance().getTriggerWithColon()
                            }
                }.rowComment(OpenEmojiBundle.message("settings.trigger.comment"))
            }

            group(OpenEmojiBundle.message("settings.group.prompt.title")) {
                row(OpenEmojiBundle.message("settings.group.prompt.label")) { }
                row {
                    cell(createPromptListTable())
                            .gap(RightGap.SMALL)
                            .onReset {
                                emojiPatterns.clear()
                                OpenEmojiPersistent.getInstance().getOpenEmojiPatterns().forEach {
                                    emojiPatterns.add(it.clone())
                                }
                                emojiPatternsTableModel.fireTableDataChanged()
                            }.resizableColumn()
                            .horizontalAlign(HorizontalAlign.FILL)
                }.resizableRow()
            }.resizableRow()
        }
    }

    private fun createPromptListTable(): JComponent {
        val tableHeader = emojiPatternsTable.tableHeader
        val headerFontMetrics = tableHeader.getFontMetrics(tableHeader.font)

        val patternColumn = emojiPatternsTable.columnModel.getColumn(0)
        patternColumn.preferredWidth = scale(150)

        val exampleColumn = emojiPatternsTable.columnModel.getColumn(1)
        exampleColumn.preferredWidth = scale(250)

        val enableColumn = emojiPatternsTable.columnModel.getColumn(2)
        val enableWidth = headerFontMetrics.stringWidth(emojiPatternsTable.getColumnName(2)) + scale(20)
        enableColumn.maxWidth = scale(enableWidth)
        enableColumn.minWidth = enableColumn.maxWidth
        enableColumn.cellRenderer = BooleanTableCellRenderer()
        enableColumn.cellEditor = BooleanTableCellEditor()

        val panel = ToolbarDecorator.createDecorator(emojiPatternsTable)
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
            emojiPatterns.add(addPatternDialog.load())

            val index: Int = emojiPatterns.size - 1
            emojiPatternsTableModel.fireTableRowsInserted(index, index)
            emojiPatternsTable.selectionModel.setSelectionInterval(index, index)
            emojiPatternsTable.scrollRectToVisible(emojiPatternsTable.getCellRect(index, 0, true))
        }
    }

    private fun editSelectedPattern() {
        stopEditing()
        val selectedIndex: Int = emojiPatternsTable.selectedRow
        if (selectedIndex < 0 || selectedIndex >= emojiPatternsTableModel.rowCount) {
            return
        }
        // 获取选择的内容
        val selectPattern = emojiPatterns[selectedIndex]

        val dialog = PatternInfoDialogWrapper(selectPattern.pattern, selectPattern.enable)
        dialog.title = OpenEmojiBundle.message("settings.pattern.title.edit")
        if (!dialog.showAndGet()) {
            return
        }
        emojiPatterns[selectedIndex] = dialog.load()
        emojiPatternsTableModel.fireTableRowsUpdated(selectedIndex, selectedIndex)
        emojiPatternsTable.selectionModel.setSelectionInterval(selectedIndex, selectedIndex)
    }

    private fun removePattern() {
        stopEditing()
        val selectedIndex: Int = emojiPatternsTable.selectedRow
        if (selectedIndex >= 0 && selectedIndex < emojiPatternsTableModel.rowCount) {
            TableUtil.removeSelectedItems(emojiPatternsTable)
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
        val selectedIndex: Int = emojiPatternsTable.selectedRow
        val swap = selectedIndex + step
        // 获取选择的内容
        val selectPattern = emojiPatterns[selectedIndex]
        emojiPatterns[selectedIndex] = emojiPatterns[swap]
        emojiPatterns[swap] = selectPattern

        emojiPatternsTableModel.fireTableRowsUpdated(
                swap.coerceAtMost(selectedIndex),
                swap.coerceAtLeast(selectedIndex)
        )
        emojiPatternsTable.selectionModel.setSelectionInterval(swap, swap)
    }


    private fun stopEditing() {
        if (emojiPatternsTable.isEditing) {
            val editor: TableCellEditor = emojiPatternsTable.cellEditor
            editor.stopCellEditing()
        }
    }

    private class PatternInfoDialogWrapper() : DialogWrapper(true) {

        var enable = JBCheckBox(OpenEmojiBundle.message("settings.pattern.enable.label"))
        var pattern = JBTextField()
        var example = JBTextField()

        constructor(pattern: String, enable: Boolean) : this() {
            this.enable.isSelected = enable
            this.pattern.text = pattern
        }

        init {
            title = OpenEmojiBundle.message("settings.pattern.title.add")
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
            example.text = OpenEmojiUtils.demo(pattern)
        }

        override fun createCenterPanel(): JComponent {
            return panel {
                row {
                    cell(enable)
                    // 添加帮助图标
                    contextHelp(
                            OpenEmojiBundle.message("settings.pattern.enable.context-help.description"),
                            OpenEmojiBundle.message("settings.pattern.enable.context-help.title")
                    )
                }
                row(OpenEmojiBundle.message("settings.pattern.pattern.label")) {
                    cell(pattern)
                            .horizontalAlign(HorizontalAlign.FILL)
                            .focused()
                            .comment(
                                    OpenEmojiBundle.message("settings.pattern.pattern.comment").trimIndent(), 50
                            )
                }.layout(RowLayout.PARENT_GRID)
                row(OpenEmojiBundle.message("settings.pattern.example.label")) {
                    cell(example)
                            .horizontalAlign(HorizontalAlign.FILL)
                }.layout(RowLayout.PARENT_GRID)
            }
        }

        fun load(): OpenEmojiPattern {
            return OpenEmojiPattern(pattern.text, enable.isSelected)
        }

        override fun doValidate(): ValidationInfo? {
            if (pattern.text == null || pattern.text.trim().isEmpty()) {
                return ValidationInfo(OpenEmojiBundle.message("settings.pattern.validate.info"))
            }
            return super.doValidate()
        }

    }

}