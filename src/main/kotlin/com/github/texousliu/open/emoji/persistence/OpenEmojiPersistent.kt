package com.github.texousliu.open.emoji.persistence

import com.github.texousliu.open.emoji.model.OpenEmojiPattern
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*


/**
 * insert description here
 *
 * @author liuxiaohua
 * @since 2023-11-20
 */
@Service
@State(name = "OpenEmojiPersistent", storages = [Storage("open_emoji.xml")], category = SettingsCategory.PLUGINS)
class OpenEmojiPersistent : PersistentStateComponent<OpenEmojiState> {

    private var triggerWithColon: Boolean = true
    private var customEmojiDirectory: String = ""
    private var openEmojiPatterns: MutableList<OpenEmojiPattern> = mutableListOf()
    private val defaultOpenEmojiPattern: OpenEmojiPattern =
        OpenEmojiPattern("#{G} [#{DATE}] #{DESC_CN}: ", true)

    companion object {
        fun getInstance(): OpenEmojiPersistent {
            return ApplicationManager.getApplication().getService(
                OpenEmojiPersistent::class.java
            )
        }
    }

    fun getTriggerWithColon(): Boolean {
        return triggerWithColon
    }

    fun setTriggerWithColon(triggerWithColon: Boolean?) {
        this.triggerWithColon = triggerWithColon ?: true
    }

    fun getCustomEmojiDirectory(): String {
        return this.customEmojiDirectory
    }

    fun setCustomEmojiDirectory(customEmojiDirectory: String?) {
        this.customEmojiDirectory = customEmojiDirectory ?: ""
    }

    fun getOpenEmojiPatterns(): MutableList<OpenEmojiPattern> {
        return openEmojiPatterns
    }

    fun setOpenEmojiPatterns(openEmojiPatterns: MutableList<OpenEmojiPattern>?) {
        this.openEmojiPatterns = openEmojiPatterns ?: mutableListOf()
    }

    fun getDefaultOpenEmojiPattern(): OpenEmojiPattern {
        return defaultOpenEmojiPattern
    }

    override fun getState(): OpenEmojiState {
        return OpenEmojiState(
            triggerWithColon,
            customEmojiDirectory,
            openEmojiPatterns
        )
    }

    override fun loadState(state: OpenEmojiState) {
        triggerWithColon = state.triggerWithColon
        customEmojiDirectory = state.customEmojiDirectory
        openEmojiPatterns = state.openEmojiPatterns
    }

}