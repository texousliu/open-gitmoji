package com.github.texousliu.open.emoji.dialog.renderer

import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.intellij.ui.JBColor
import java.awt.Component
import javax.swing.JTable

interface OpenEmojiInfoChangedTableCellRenderer {

    fun withBackground(
            tableCellRendererComponent: Component,
            emojiInfoList: MutableList<OpenEmojiInfo>,
            table: JTable?,
            isSelected: Boolean,
            row: Int,
    ): Component {
        val openEmojiInfo = emojiInfoList[row]
        tableCellRendererComponent.background =
                if (openEmojiInfo.changed) JBColor.GREEN
                else if (isSelected) table!!.selectionBackground
                else table!!.background
        return tableCellRendererComponent
    }

}