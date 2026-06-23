package com.github.texousliu.open.emoji.persistence

import com.github.texousliu.open.emoji.constants.WorkEnv
import com.github.texousliu.open.emoji.context.OpenEmojiCache
import com.github.texousliu.open.emoji.model.EmojiConfigState
import com.github.texousliu.open.emoji.model.EmojiSource
import com.github.texousliu.open.emoji.model.OpenEmoji
import com.github.texousliu.open.emoji.model.OpenEmojiData
import com.github.texousliu.open.emoji.model.OpenEmojiPattern
import com.github.texousliu.open.emoji.utils.OpenEmojiUtils
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.annotations.OptionTag
import com.intellij.util.xmlb.annotations.Property
import java.util.logging.Logger

/**
 * Persistent state for Open Gitmoji plugin settings.
 *
 * Stores user preferences including trigger behavior, custom directories,
 * pattern configurations, and emoji metadata. Auto-saved to openEmoji.xml.
 */
@Service
@State(name = "OpenEmojiPersistent", storages = [Storage("openEmoji.xml")], category = SettingsCategory.PLUGINS)
class OpenEmojiPersistent : PersistentStateComponent<OpenEmojiPersistent> {

    companion object {
        private val LOG = Logger.getLogger(OpenEmojiPersistent::class.java.name)

        @JvmStatic
        fun getInstance(): OpenEmojiPersistent {
            return ApplicationManager.getApplication().getService(
                    OpenEmojiPersistent::class.java
            )
        }
    }

    @Property
    private var triggerWithColon: Boolean = true

    @Property
    private var customEmojiDirectory: String = ""

    @Property
    private var customEmojiUrl: String = ""

    @Property
    private var networkEmojiEnabled: Boolean = false

    @Property
    private var networkEmojiCacheDurationMinutes: Int = 1440

    @Property
    private var editorEmojiSupported: Boolean = false

    @Property
    @OptionTag(converter = OpenEmojiPatternListConverter::class)
    private var openEmojiPatterns: MutableList<OpenEmojiPattern> = mutableListOf()

    @Property
    @OptionTag(converter = OpenEmojiInfoListConverter::class)
    private var openEmojiInfoList: MutableList<EmojiConfigState> = mutableListOf()

    /**
     * 旧版兼容字段：用于检测是否需要从旧格式迁移数据。
     * 旧版 XML 中可能存储的是包含 "type" 字段的 OpenEmojiInfo 格式。
     */
    @Suppress("unused")
    private var legacyOpenEmojiInfoList: MutableList<LegacyEmojiInfo>? = null

    private val defaultOpenEmojiPattern: OpenEmojiPattern =
            OpenEmojiPattern("#{G} [#{DATE}] #{DESC_CN}: ")

    private val defaultOpenEmojiPatternEditor: OpenEmojiPattern =
            OpenEmojiPattern("#{G} ", enableEditor = true)

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

    fun getCustomEmojiUrl(): String {
        return this.customEmojiUrl
    }

    fun setCustomEmojiUrl(customEmojiUrl: String?) {
        this.customEmojiUrl = customEmojiUrl ?: ""
    }

    fun getNetworkEmojiEnabled(): Boolean {
        return this.networkEmojiEnabled
    }

    fun setNetworkEmojiEnabled(networkEmojiEnabled: Boolean?) {
        this.networkEmojiEnabled = networkEmojiEnabled ?: false
    }

    fun getNetworkEmojiCacheDurationMinutes(): Int {
        return this.networkEmojiCacheDurationMinutes
    }

    fun setNetworkEmojiCacheDurationMinutes(networkEmojiCacheDurationMinutes: Int?) {
        this.networkEmojiCacheDurationMinutes = networkEmojiCacheDurationMinutes ?: 1440
    }

    fun getEditorEmojiSupported(): Boolean {
        return editorEmojiSupported
    }

    fun setEditorEmojiSupported(editorEmojiSupported: Boolean?) {
        this.editorEmojiSupported = editorEmojiSupported ?: false
    }

    fun getOpenEmojiPatterns(): MutableList<OpenEmojiPattern> {
        return openEmojiPatterns
    }

    fun setOpenEmojiPatterns(openEmojiPatterns: MutableList<OpenEmojiPattern>?) {
        this.openEmojiPatterns.clear()
        openEmojiPatterns?.forEach { this.openEmojiPatterns.add(it.clone()) }
    }

    fun getOpenEmojiInfoList(): MutableList<EmojiConfigState> {
        return if (openEmojiInfoList.isEmpty()) OpenEmojiCache.emojiInfoList() else openEmojiInfoList
    }

    fun setOpenEmojiInfoList(openEmojiInfoList: MutableList<EmojiConfigState>?) {
        this.openEmojiInfoList.clear()
        openEmojiInfoList?.forEach { this.openEmojiInfoList.add(it) }
    }

    fun getDefaultOpenEmojiPattern(env: WorkEnv?): OpenEmojiPattern {
        return if (env == null || env == WorkEnv.COMMIT)
            defaultOpenEmojiPattern
        else
            defaultOpenEmojiPatternEditor
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
        this.customEmojiUrl = state.customEmojiUrl
        this.networkEmojiEnabled = state.networkEmojiEnabled
        this.networkEmojiCacheDurationMinutes = state.networkEmojiCacheDurationMinutes
        this.editorEmojiSupported = state.editorEmojiSupported
        this.openEmojiPatterns = state.openEmojiPatterns

        // 尝试加载新格式
        if (state.openEmojiInfoList.isNotEmpty()) {
            this.openEmojiInfoList = state.openEmojiInfoList
        } else {
            // 新格式为空，尝试从旧格式迁移
            this.openEmojiInfoList = migrateFromLegacy(state)
        }
    }

    /**
     * 从旧版 OpenEmojiInfo 格式迁移到新的 EmojiConfigState 格式。
     * 旧版数据中 type 字段值为 "CUSTOM"/"OVERRIDE"/"DEFAULT"，
     * isCustom 字段表示是否为自定义 emoji。
     */
    private fun migrateFromLegacy(state: OpenEmojiPersistent): MutableList<EmojiConfigState> {
        val result = mutableListOf<EmojiConfigState>()
        try {
            // 如果旧版字段有数据，进行迁移
            state.legacyOpenEmojiInfoList?.forEach { legacy ->
                val source = when (legacy.type) {
                    "CUSTOM" -> EmojiSource.CUSTOM
                    "OVERRIDE" -> EmojiSource.OVERRIDE
                    else -> EmojiSource.DEFAULT
                }
                val data = OpenEmojiData(
                        emoji = legacy.emoji,
                        entity = legacy.entity,
                        code = legacy.code,
                        name = legacy.name,
                        description = legacy.description,
                        cnDescription = legacy.cnDescription
                )
                val emoji = OpenEmoji(data, source)
                result.add(EmojiConfigState(emoji, legacy.enable, false))
            }
        } catch (e: Exception) {
            LOG.warning("从旧版格式迁移 emoji 数据失败，使用默认数据: ${e.message}")
        }
        // 如果迁移结果为空，回退到默认数据
        if (result.isEmpty()) {
            result.addAll(OpenEmojiUtils.defaultEmojiInfoList())
        }
        return result
    }

    /**
     * 旧版 emoji 数据的内部表示，用于 XML 反序列化时的兼容迁移。
     * 字段名与旧版 OpenEmojiInfo 的序列化格式完全一致。
     */
    @Suppress("unused")
    internal class LegacyEmojiInfo(
            var emoji: String = "",
            var entity: String = "",
            var code: String = "",
            var name: String = "",
            var description: String = "",
            var cnDescription: String = "",
            var enable: Boolean = true,
            var type: String = "DEFAULT"
    )

}
