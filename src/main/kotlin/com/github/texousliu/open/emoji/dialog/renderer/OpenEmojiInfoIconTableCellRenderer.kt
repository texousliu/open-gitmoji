package com.github.texousliu.open.emoji.dialog.renderer

import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.intellij.util.ui.table.IconTableCellRenderer
import org.jetbrains.annotations.NotNull
import java.awt.Component
import javax.swing.Icon
import javax.swing.JTable

/**
 * insert description here
 *
 * @author liuxiaohua
 * @since 2023-11-22
 */
class OpenEmojiInfoIconTableCellRenderer(private val emojiInfoList: MutableList<OpenEmojiInfo>)
    : IconTableCellRenderer<Icon>(), OpenEmojiInfoChangedTableCellRenderer {

    @NotNull
    override fun getIcon(@NotNull value: Icon, table: JTable, row: Int): Icon {
        return value
    }

    override fun getTableCellRendererComponent(
            table: JTable,
            value: Any,
            selected: Boolean,
            focus: Boolean,
            row: Int,
            column: Int
    ): Component {
        val tableCellRendererComponent = super
                .getTableCellRendererComponent(table, value, selected, focus, row, column)
        withForeground(tableCellRendererComponent, emojiInfoList, table, selected, row)
        setText("")
        return tableCellRendererComponent
    }

    override fun isCenterAlignment(): Boolean {
        return true
    }

}