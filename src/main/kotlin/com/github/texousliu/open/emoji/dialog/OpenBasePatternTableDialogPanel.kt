package com.github.texousliu.open.emoji.dialog

import com.github.texousliu.open.emoji.config.OpenEmojiBundle
import com.github.texousliu.open.emoji.model.OpenEmojiPattern
import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.*
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.scale.JBUIScale.scale
import com.intellij.ui.table.JBTable
import java.awt.Dimension
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.table.TableCellEditor


class OpenBasePatternTableDialogPanel(private val emojiPatternList: MutableList<OpenEmojiPattern>) {

    private val emojiPatternTableModel = OpenEmojiPatternsTableModel(emojiPatternList)
    private val emojiPatternTable = JBTable(emojiPatternTableModel)
    private val configEmojiPatternList = mutableListOf<OpenEmojiPattern>()

    init {
        emojiPatternTable.setShowGrid(false)
        object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                editSelectedPattern()
                return true
            }
        }.installOn(emojiPatternTable)
    }

    fun create(): JComponent {
        val tableHeader = emojiPatternTable.tableHeader
        val headerFontMetrics = tableHeader.getFontMetrics(tableHeader.font)

        val patternColumn = emojiPatternTable.columnModel.getColumn(0)
        patternColumn.preferredWidth = scale(150)

        val exampleColumn = emojiPatternTable.columnModel.getColumn(1)
        exampleColumn.preferredWidth = scale(250)

        val enableColumn = emojiPatternTable.columnModel.getColumn(2)
        val enableWidth = headerFontMetrics.stringWidth(emojiPatternTable.getColumnName(2)) + scale(20)
        enableColumn.maxWidth = scale(enableWidth)
        enableColumn.minWidth = enableColumn.maxWidth
        enableColumn.cellRenderer = BooleanTableCellRenderer()
        enableColumn.cellEditor = BooleanTableCellEditor()

        val enableCommitColumn = emojiPatternTable.columnModel.getColumn(3)
        val enableCommitWidth = headerFontMetrics.stringWidth(emojiPatternTable.getColumnName(3)) + scale(20)
        enableCommitColumn.maxWidth = scale(enableCommitWidth)
        enableCommitColumn.minWidth = enableCommitColumn.maxWidth
        enableCommitColumn.cellRenderer = BooleanTableCellRenderer()
        enableCommitColumn.cellEditor = BooleanTableCellEditor()

        val enableEditorColumn = emojiPatternTable.columnModel.getColumn(4)
        val enableEditorWidth = headerFontMetrics.stringWidth(emojiPatternTable.getColumnName(4)) + scale(20)
        enableEditorColumn.maxWidth = scale(enableEditorWidth)
        enableEditorColumn.minWidth = enableEditorColumn.maxWidth
        enableEditorColumn.cellRenderer = BooleanTableCellRenderer()
        enableEditorColumn.cellEditor = BooleanTableCellEditor()

        val panel = ToolbarDecorator.createDecorator(emojiPatternTable)
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

    fun onReset(emojiPatternList: MutableList<OpenEmojiPattern>) {
        this.emojiPatternList.clear()
        emojiPatternList.forEach {
            this.emojiPatternList.add(it.clone())
            this.configEmojiPatternList.add(it.clone())
        }
        emojiPatternTableModel.fireTableDataChanged()
    }

    fun onApply() {
        this.configEmojiPatternList.clear()
        this.configEmojiPatternList.addAll(emojiPatternList)
    }

    fun markModifiedPatterns(): Boolean {
        return isModifiedPatterns()
    }

    fun isModifiedPatterns(): Boolean {
        if (configEmojiPatternList.size != emojiPatternList.size) return true
        for ((index, emojiPattern) in emojiPatternList.withIndex()) {
            if (configEmojiPatternList[index] != emojiPattern) return true
            if (configEmojiPatternList[index].enable != emojiPattern.enable
                || configEmojiPatternList[index].enableCommit != emojiPattern.enableCommit
                || configEmojiPatternList[index].enableEditor != emojiPattern.enableEditor
            ) return true
        }
        return false
    }

    private fun addPattern() {
        stopEditing()
        val addPatternDialog = PatternInfoDialogWrapper()
        if (addPatternDialog.showAndGet()) {
            emojiPatternList.add(addPatternDialog.load())

            val index: Int = emojiPatternList.size - 1
            emojiPatternTableModel.fireTableRowsInserted(index, index)
            emojiPatternTable.selectionModel.setSelectionInterval(index, index)
            emojiPatternTable.scrollRectToVisible(emojiPatternTable.getCellRect(index, 0, true))
        }
    }

    private fun editSelectedPattern() {
        stopEditing()
        val selectedIndex: Int = emojiPatternTable.selectedRow
        if (selectedIndex < 0 || selectedIndex >= emojiPatternTableModel.rowCount) {
            return
        }
        // 获取选择的内容
        val selectPattern = emojiPatternList[selectedIndex]

        val dialog = PatternInfoDialogWrapper(
            selectPattern.pattern,
            selectPattern.enable, selectPattern.enableCommit, selectPattern.enableEditor
        )
        dialog.title = OpenEmojiBundle.message("settings.pattern.title.edit")
        if (!dialog.showAndGet()) {
            return
        }
        emojiPatternList[selectedIndex] = dialog.load()
        emojiPatternTableModel.fireTableRowsUpdated(selectedIndex, selectedIndex)
        emojiPatternTable.selectionModel.setSelectionInterval(selectedIndex, selectedIndex)
    }

    private fun removePattern() {
        stopEditing()
        val selectedIndex: Int = emojiPatternTable.selectedRow
        if (selectedIndex >= 0 && selectedIndex < emojiPatternTableModel.rowCount) {
            TableUtil.removeSelectedItems(emojiPatternTable)
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
        val selectedIndex: Int = emojiPatternTable.selectedRow
        val swap = selectedIndex + step
        // 获取选择的内容
        val selectPattern = emojiPatternList[selectedIndex]
        emojiPatternList[selectedIndex] = emojiPatternList[swap]
        emojiPatternList[swap] = selectPattern

        emojiPatternTableModel.fireTableRowsUpdated(
            swap.coerceAtMost(selectedIndex),
            swap.coerceAtLeast(selectedIndex)
        )
        emojiPatternTable.selectionModel.setSelectionInterval(swap, swap)
    }


    private fun stopEditing() {
        if (emojiPatternTable.isEditing) {
            val editor: TableCellEditor = emojiPatternTable.cellEditor
            editor.stopCellEditing()
        }
    }

    private class PatternInfoDialogWrapper() : DialogWrapper(true) {

        var enable = JBCheckBox(OpenEmojiBundle.message("settings.pattern.enable.label"))
        var enableCommit = JBCheckBox(OpenEmojiBundle.message("settings.pattern.enable-commit.label"))
        var enableEditor = JBCheckBox(OpenEmojiBundle.message("settings.pattern.enable-editor.label"))
        var pattern = JBTextField()
        var example = JBTextField()

        constructor(pattern: String, enable: Boolean, enableCommit: Boolean, enableEditor: Boolean) : this() {
            this.enable.isSelected = enable
            this.enableCommit.isSelected = enableCommit
            this.enableEditor.isSelected = enableEditor
            this.pattern.text = pattern
        }

        init {
            title = OpenEmojiBundle.message("settings.pattern.title.add")

            OpenEmojiUtils.addDocListener(pattern, this::generatorDemo)

            example.isEditable = false
            enable.isSelected = true
            enableCommit.isSelected = true
            enableEditor.isSelected = false
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
                    cell(enableCommit)
                    cell(enableEditor)
                }
                row(OpenEmojiBundle.message("settings.pattern.pattern.label")) {
                    cell(pattern).align(Align.FILL).focused()
                        .comment(
                            OpenEmojiBundle.message("settings.pattern.pattern.comment").trimIndent(), 50
                        )
                }.layout(RowLayout.PARENT_GRID)
                row(OpenEmojiBundle.message("settings.pattern.example.label")) {
                    cell(example).align(Align.FILL)
                }.layout(RowLayout.PARENT_GRID)
            }
        }

        fun load(): OpenEmojiPattern {
            return OpenEmojiPattern(pattern.text, enable.isSelected, enableCommit.isSelected, enableEditor.isSelected)
        }

        override fun doValidate(): ValidationInfo? {
            if (pattern.text == null || pattern.text.trim().isEmpty()) {
                return ValidationInfo(OpenEmojiBundle.message("settings.pattern.validate.info"))
            }
            return super.doValidate()
        }

    }

}