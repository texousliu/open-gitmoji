package com.github.texousliu.open.emoji.persistence

import com.github.texousliu.open.emoji.context.OpenEmojiCache
import com.github.texousliu.open.emoji.model.OpenEmojiInfo
import com.github.texousliu.open.emoji.model.OpenEmojiPattern
import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.annotations.OptionTag
import com.intellij.util.xmlb.annotations.Property


/**
 * insert description here
 *
 * @author liuxiaohua
 * @since 2023-11-20
 */
@Service
@State(name = "OpenEmojiPersistent", storages = [Storage("openEmoji.xml")], category = SettingsCategory.PLUGINS)
class OpenEmojiPersistent : PersistentStateComponent<OpenEmojiPersistent> {

    @Property
    private var triggerWithColon: Boolean = true

    @Property
    private var customEmojiDirectory: String = ""

    @Property
    @OptionTag(converter = OpenEmojiPatternListConverter::class)
    private var openEmojiPatterns: MutableList<OpenEmojiPattern> = mutableListOf()

    @Property
    @OptionTag(converter = OpenEmojiInfoListConverter::class)
    private var openEmojiInfoList: MutableList<OpenEmojiInfo> = mutableListOf()

    private val defaultOpenEmojiPattern: OpenEmojiPattern =
            OpenEmojiPattern("#{G} [#{DATE}] #{DESC_CN}: ", true)

    companion object {
        @JvmStatic
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

    fun getOpenEmojiInfoList(): MutableList<OpenEmojiInfo> {
        return if (openEmojiInfoList.isEmpty()) OpenEmojiCache.emojiInfoList() else openEmojiInfoList
    }

    fun setOpenEmojiInfoList(openEmojiInfoList: MutableList<OpenEmojiInfo>?) {
        this.openEmojiInfoList = openEmojiInfoList ?: mutableListOf()
    }

    fun getDefaultOpenEmojiPattern(): OpenEmojiPattern {
        return defaultOpenEmojiPattern
    }

    fun refresh() {
        // 刷新 context
        OpenEmojiCache.refresh(customEmojiDirectory)
        // 刷新存储
        OpenEmojiUtils.emojiInfoListWithCustom(customEmojiDirectory, openEmojiInfoList)
    }

    override fun getState(): OpenEmojiPersistent {
        return this
    }

    override fun loadState(state: OpenEmojiPersistent) {
        this.triggerWithColon = state.triggerWithColon
        this.customEmojiDirectory = state.customEmojiDirectory
        this.openEmojiPatterns = state.openEmojiPatterns
        this.openEmojiInfoList = state.openEmojiInfoList
    }

}