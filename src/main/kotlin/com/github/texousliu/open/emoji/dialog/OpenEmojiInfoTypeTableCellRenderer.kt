package com.github.texousliu.open.emoji.dialog

import com.github.texousliu.open.emoji.model.OpenEmojiInfoType
import com.intellij.ui.JBColor
import java.awt.Component
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

/**
 * insert description here
 *
 * @author liuxiaohua
 * @since 2023-11-22
 */
class OpenEmojiInfoTypeTableCellRenderer : DefaultTableCellRenderer() {

    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
        foreground = when (value) {
            OpenEmojiInfoType.OVERRIDE.name -> {
                JBColor.RED
            }

            OpenEmojiInfoType.CUSTOM.name -> {
                JBColor.GREEN
            }

            else -> {
                foreground
            }
        }
        return this
    }
}