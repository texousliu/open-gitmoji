package com.github.texousliu.open.emoji.dialog.renderer

import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.intellij.ui.JBColor
import java.awt.Component
import javax.swing.JTable

interface OpenEmojiInfoChangedTableCellRenderer {

    fun withForeground(
            tableCellRendererComponent: Component,
            emojiInfoList: MutableList<OpenEmojiInfo>,
            table: JTable?,
            isSelected: Boolean,
            row: Int,
    ): Component {
        val openEmojiInfo = emojiInfoList[row]
        tableCellRendererComponent.foreground =
                if (openEmojiInfo.changed) JBColor.RED
                else if (isSelected) table!!.selectionForeground
                else table!!.foreground
        return tableCellRendererComponent
    }

}