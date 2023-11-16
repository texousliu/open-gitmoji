package com.github.texousliu.opengitmoji.dialog

import com.github.texousliu.opengitmoji.model.GitmojiPattern
import com.github.texousliu.opengitmoji.utils.OpenGitmojiUtils.demo
import com.intellij.util.ui.ItemRemovable
import javax.swing.table.AbstractTableModel

class PatternsTableModel(private var gitmojiPatterns: MutableList<GitmojiPattern>) : AbstractTableModel(), ItemRemovable {

    private val ourColumnNames = arrayOf(
            "Pattern",
            "Demo",
            "Enable"
    )
    private val ourColumnClasses = arrayOf<Class<*>>(String::class.java, String::class.java, Boolean::class.java)


    override fun getColumnName(column: Int): String {
        return ourColumnNames[column]
    }

    override fun getColumnClass(column: Int): Class<*> {
        return ourColumnClasses[column]
    }

    override fun getColumnCount(): Int {
        return 3
    }

    override fun getRowCount(): Int {
        return gitmojiPatterns.size
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return columnIndex == 2
    }

    override fun getValueAt(row: Int, column: Int): Any {
        val pattern = gitmojiPatterns[row]
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

            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun setValueAt(value: Any?, row: Int, column: Int) {
        val pattern = gitmojiPatterns[row]
        when (column) {
            0 -> {
                pattern.pattern = (value as String?)!!
            }

            1 -> {}
            2 -> {
                pattern.enable = (value as Boolean?)!!
            }

            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun removeRow(index: Int) {
        gitmojiPatterns.removeAt(index)
        fireTableRowsDeleted(index, index)
    }

}