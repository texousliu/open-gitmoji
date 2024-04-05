package com.github.texousliu.open.emoji.dialog

import com.github.texousliu.open.emoji.model.OpenEmojiPattern
import com.github.texousliu.open.emoji.utils.OpenEmojiUtils.demo
import com.intellij.util.ui.ItemRemovable
import javax.swing.table.AbstractTableModel

class OpenEmojiPatternsTableModel(private var openEmojiPatterns: MutableList<OpenEmojiPattern>) : AbstractTableModel(),
        ItemRemovable {

    private val ourColumnNames = arrayOf(
            "Pattern",
            "Example",
            "Enable",
            "Commit",
            "Editor"
    )
    private val ourColumnClasses = arrayOf<Class<*>>(
            String::class.java,
            String::class.java,
            Boolean::class.java,
            Boolean::class.java,
            Boolean::class.java)


    override fun getColumnName(column: Int): String {
        return ourColumnNames[column]
    }

    override fun getColumnClass(column: Int): Class<*> {
        return ourColumnClasses[column]
    }

    override fun getColumnCount(): Int {
        return 5
    }

    override fun getRowCount(): Int {
        return openEmojiPatterns.size
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return columnIndex == 2 || columnIndex == 3 || columnIndex == 4
    }

    override fun getValueAt(row: Int, column: Int): Any {
        val pattern = openEmojiPatterns[row]
        return when (column) {
            0 -> {
                pattern.pattern
            }

            1 -> {
                demo(pattern.pattern)
            }

            2 -> {
                pattern.enable
            }

            3 -> {
                pattern.enableCommit
            }

            4 -> {
                pattern.enableEditor
            }

            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun setValueAt(value: Any?, row: Int, column: Int) {
        val pattern = openEmojiPatterns[row]
        when (column) {
            0 -> {
                pattern.pattern = (value as String?)!!
            }

            1 -> {}
            2 -> {
                pattern.enable = (value as Boolean?)!!
            }

            3 -> {
                pattern.enableCommit = (value as Boolean?)!!
            }

            4 -> {
                pattern.enableEditor = (value as Boolean?)!!
            }

            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun removeRow(index: Int) {
        openEmojiPatterns.removeAt(index)
        fireTableRowsDeleted(index, index)
    }

}