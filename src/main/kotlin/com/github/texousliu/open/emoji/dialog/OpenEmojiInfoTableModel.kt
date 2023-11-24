package com.github.texousliu.open.emoji.dialog

import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.intellij.util.ui.ItemRemovable
import javax.swing.Icon
import javax.swing.table.AbstractTableModel

class OpenEmojiInfoTableModel(private var openEmojiInfos: MutableList<OpenEmojiInfo>) : AbstractTableModel(),
        ItemRemovable {

    private val ourColumnNames = arrayOf(
            "Icon",
            "Code",
            "Description",
            "Type",
            "Enable",
    )
    private val ourColumnClasses =
            arrayOf<Class<*>>(
                    Icon::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    Boolean::class.java
            )


    override fun getColumnName(column: Int): String {
        return ourColumnNames[column]
    }

    override fun getColumnClass(column: Int): Class<*> {
        return ourColumnClasses[column]
    }

    override fun getColumnCount(): Int {
        return ourColumnNames.size
    }

    override fun getRowCount(): Int {
        return openEmojiInfos.size
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return columnIndex == 4
    }

    override fun getValueAt(row: Int, column: Int): Any {
        val config = openEmojiInfos[row]
        return when (column) {
            0 -> {
                config.getIcon()
            }

            1 -> {
                config.code
            }

            2 -> {
                config.description
            }

            3 -> {
                config.type
            }

            4 -> {
                config.enable
            }

            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun setValueAt(value: Any?, row: Int, column: Int) {
        val config = openEmojiInfos[row]
        when (column) {
            0 -> {
            }

            1 -> {
            }

            2 -> {
            }

            3 -> {

            }

            4 -> {
                config.enable = (value as Boolean?)!!
            }

            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun removeRow(index: Int) {
        openEmojiInfos.removeAt(index)
        fireTableRowsDeleted(index, index)
    }

}