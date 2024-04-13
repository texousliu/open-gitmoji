package com.github.texousliu.open.emoji.dialog

import com.github.texousliu.open.emoji.config.OpenEmojiBundle
import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.github.texousliu.open.emoji.model.OpenEmojiPattern
import com.github.texousliu.open.emoji.persistence.OpenEmojiPersistent
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.JBColor
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.panel


class OpenEditorEmojiDialogPanel {

    private val editorEmojiPatterns = mutableListOf<OpenEmojiPattern>()
    private val editorEmojiInfoList = mutableListOf<OpenEmojiInfo>()
    private val editorEmojiPatternTableDialogPanel = OpenBasePatternTableDialogPanel(editorEmojiPatterns)
    private val editorEmojiInfoTableDialogPanel = OpenBaseEmojiTableDialogPanel(editorEmojiInfoList)
    private val editorEmojiCustomDirectory = OpenBaseDirectoryDialogPanel("")

    val editorEmojiSettingsPanel = gitEmojiSettingsDialogPanel()

    fun markModified(): Boolean {
        // 注意这边不能调换位置
        return editorEmojiInfoTableDialogPanel.markModifiedEmojis()
                || editorEmojiPatternTableDialogPanel.markModifiedPatterns()
    }

    fun apply() {
        if (editorEmojiInfoTableDialogPanel.isModifiedEmojis()) {
            OpenEmojiPersistent.getInstance().setOpenEditorEmojiInfoList(editorEmojiInfoList)
            editorEmojiInfoTableDialogPanel.onApply()
        }
        if (editorEmojiPatternTableDialogPanel.isModifiedPatterns()) {
            OpenEmojiPersistent.getInstance().setOpenEmojiPatterns(editorEmojiPatterns)
            editorEmojiPatternTableDialogPanel.onApply()
        }
    }

    private fun gitEmojiSettingsDialogPanel(): DialogPanel {
        return panel {
            row(OpenEmojiBundle.message("settings.group.pattern.label")) { }
            row {
                cell(editorEmojiPatternTableDialogPanel.create())
                    .gap(RightGap.SMALL)
                    .onReset {
                        editorEmojiPatternTableDialogPanel.onReset(
                            OpenEmojiPersistent.getInstance().getOpenEditorEmojiPatterns()
                        )
                    }.resizableColumn()
                    .align(Align.FILL)
            }.resizableRow()
            separator(JBColor.WHITE)
            panel {
                row(OpenEmojiBundle.message("settings.info.custom.directory.label")) {
                    cell(editorEmojiCustomDirectory.create())
                        .gap(RightGap.COLUMNS)
                        .resizableColumn()
                        .align(Align.FILL)
                        .onChanged {
                            editorEmojiCustomDirectory.onChange {
                                println("directory changed $it")
                            }
                        }
                        .onReset {
                            editorEmojiCustomDirectory.onReset(
                                OpenEmojiPersistent.getInstance().getCustomEmojiDirectory()
                            )
                        }
                }.rowComment(OpenEmojiBundle.message("settings.info.custom.directory.comment"))
            }
            row(OpenEmojiBundle.message("settings.group.emoji.label")) { }
            row {
                cell(editorEmojiInfoTableDialogPanel.create())
                    .onReset {
                        editorEmojiInfoTableDialogPanel.onReset(
                            OpenEmojiPersistent.getInstance().getOpenEditorEmojiInfoList()
                        )
                    }.resizableColumn()
                    .align(Align.FILL)
            }.resizableRow()
        }
    }

}