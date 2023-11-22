package com.github.texousliu.open.emoji.dialog

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
class OpenEmojiIconTableCellRenderer : IconTableCellRenderer<Icon>() {

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
        super.getTableCellRendererComponent(table, value, selected, focus, row, column)
        setText("")
        return this
    }

    override fun isCenterAlignment(): Boolean {
        return true
    }

}