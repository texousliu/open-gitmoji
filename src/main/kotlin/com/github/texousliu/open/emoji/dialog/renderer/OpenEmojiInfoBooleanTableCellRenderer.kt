package com.github.texousliu.open.emoji.dialog.renderer

import com.github.texousliu.open.emoji.model.EmojiConfigState
import com.intellij.ui.BooleanTableCellRenderer
import java.awt.Component
import javax.swing.JTable

class OpenEmojiInfoBooleanTableCellRenderer(private val emojiInfoList: MutableList<EmojiConfigState>)
    : BooleanTableCellRenderer(), OpenEmojiInfoChangedTableCellRenderer {

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
        withForeground(tableCellRendererComponent, emojiInfoList, table, isSelected, row)
        return tableCellRendererComponent
    }

}
