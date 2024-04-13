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
import java.awt.Color


class OpenGitEmojiDialogPanel {

    private val gitEmojiPatterns = mutableListOf<OpenEmojiPattern>()
    private val gitEmojiInfoList = mutableListOf<OpenEmojiInfo>()
    private val gitEmojiPatternTableDialogPanel = OpenBasePatternTableDialogPanel(gitEmojiPatterns)
    private val gitEmojiInfoTableDialogPanel = OpenBaseEmojiTableDialogPanel(gitEmojiInfoList)
    private val gitEmojiCustomDirectory = OpenBaseDirectoryDialogPanel("")

    val gitEmojiSettingsPanel = gitEmojiSettingsDialogPanel()

    fun markModified(): Boolean {
        // 注意这边不能调换位置
        return gitEmojiInfoTableDialogPanel.markModifiedEmojis()
                || gitEmojiPatternTableDialogPanel.markModifiedPatterns()
                || gitEmojiCustomDirectory.markModifiedDirectory()
    }

    fun apply() {
        if (gitEmojiInfoTableDialogPanel.isModifiedEmojis()) {
            OpenEmojiPersistent.getInstance().setOpenEmojiInfoList(gitEmojiInfoList)
            gitEmojiInfoTableDialogPanel.onApply()
        }
        if (gitEmojiPatternTableDialogPanel.isModifiedPatterns()) {
            OpenEmojiPersistent.getInstance().setOpenEmojiPatterns(gitEmojiPatterns)
            gitEmojiPatternTableDialogPanel.onApply()
        }
        if (gitEmojiCustomDirectory.isModifiedDirectory()) {
            OpenEmojiPersistent.getInstance()
                .setCustomEmojiDirectory(gitEmojiCustomDirectory.directory())
            gitEmojiCustomDirectory.onApply()
        }
    }

    private fun gitEmojiSettingsDialogPanel(): DialogPanel {
        return panel {
            row(OpenEmojiBundle.message("settings.group.pattern.label")) { }
            row {
                cell(gitEmojiPatternTableDialogPanel.create())
                    .gap(RightGap.SMALL)
                    .onReset {
                        gitEmojiPatternTableDialogPanel.onReset(
                            OpenEmojiPersistent.getInstance().getOpenEmojiPatterns()
                        )
                    }.resizableColumn()
                    .align(Align.FILL)
            }.resizableRow()
            separator(JBColor.WHITE)
            panel {
                row(OpenEmojiBundle.message("settings.info.custom.directory.label")) {
                    cell(gitEmojiCustomDirectory.create())
                        .gap(RightGap.COLUMNS)
                        .resizableColumn()
                        .align(Align.FILL)
                        .onChanged {
                            gitEmojiCustomDirectory.onChange {
                                println("directory changed $it")
                            }
                        }
                        .onReset {
                            gitEmojiCustomDirectory.onReset(
                                OpenEmojiPersistent.getInstance().getCustomEmojiDirectory()
                            )
                        }
                }.rowComment(OpenEmojiBundle.message("settings.info.custom.directory.comment"))
            }
            row(OpenEmojiBundle.message("settings.group.emoji.label")) { }
            row {
                cell(gitEmojiInfoTableDialogPanel.create())
                    .onReset {
                        gitEmojiInfoTableDialogPanel.onReset(
                            OpenEmojiPersistent.getInstance().getOpenEmojiInfoList()
                        )
                    }.resizableColumn()
                    .align(Align.FILL)
            }.resizableRow()
        }
    }

}