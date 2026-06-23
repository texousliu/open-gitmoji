package com.github.texousliu.open.emoji.dialog.renderer

import com.github.texousliu.open.emoji.model.EmojiConfigState
import com.github.texousliu.open.emoji.model.EmojiSource
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
class OpenEmojiInfoTypeTableCellRenderer(private val emojiInfoList: MutableList<EmojiConfigState>)
    : DefaultTableCellRenderer(), OpenEmojiInfoChangedTableCellRenderer {

    override fun getTableCellRendererComponent(
            table: JTable?,
            value: Any?,
            isSelected: Boolean,
            hasFocus: Boolean,
            row: Int,
            column: Int
    ): Component {
        val tableCellRendererComponent = super
                .getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
        tableCellRendererComponent.background = when (value) {
            EmojiSource.OVERRIDE.name -> {
                JBColor.CYAN
            }

            EmojiSource.CUSTOM.name -> {
                JBColor.GREEN
            }

            else -> {
                if (isSelected) table!!.selectionBackground else table!!.background
            }
        }
        withForeground(tableCellRendererComponent, emojiInfoList, table, isSelected, row)
        return tableCellRendererComponent
    }
}
