package com.github.texousliu.open.emoji.dialog.renderer

import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import java.awt.Component
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

/**
 * insert description here
 *
 * @author liuxiaohua
 * @since 2023-11-22
 */
class OpenEmojiInfoStringTableCellRenderer(private val emojiInfoList: MutableList<OpenEmojiInfo>)
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
        withBackground(tableCellRendererComponent, emojiInfoList, table, isSelected, row)
        return tableCellRendererComponent
    }
}