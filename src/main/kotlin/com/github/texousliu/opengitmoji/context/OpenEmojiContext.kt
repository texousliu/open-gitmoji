package com.github.texousliu.opengitmoji.context

import com.github.texousliu.opengitmoji.model.*
import com.github.texousliu.opengitmoji.utils.OpenEmojiUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.ide.util.PropertiesComponent

const val OPEN_GIT_EMOJI_TABLE_INFO_KEY = "open.texousliu.config.settings.gm.OpenGitmojiSettings.OpenGMTableInfoKey"
const val OPEN_GIT_EMOJI_TC_TEXT_KEY = "open.texousliu.config.settings.gm.OpenGitmojiSettings.OpenGMTCTextKey"
const val OPEN_GIT_EMOJI_CUSTOM_FOLDER_KEY = "open.texousliu.config.settings.gm.OpenGitmojiSettings.OpenGMCustomFolderKey"
const val OPEN_COMPATIBLE_WITH_OLD_CONFIG = "open.texousliu.config.settings.gm.OpenGitmojiSettings.OpenGMCompatible"

object OpenEmojiContext {

    const val EMOJI_FILE_NAME = "emojis.json"
    const val REPLACE_SUFFIX_MARK = "$$:$$"
    private val openEmojiList = ArrayList<OpenEmoji>()
    private var openEmojiPatterns = mutableListOf<OpenEmojiPattern>()
    private var triggerWithColon = true
    private var customEmojiFolder = ""

    init {
        compatibleWithOldConfigurations()
        loadGM()
        reset()
    }

    fun reset() {
        triggerWithColon = getConfigTriggerWithColon()
        openEmojiPatterns = getConfigEmojiPatterns()
        customEmojiFolder = getConfigCustomEmojiFolder()
    }

    fun apply(openEmojiPatterns: MutableList<OpenEmojiPattern>,
              triggerCondition: Boolean, customEmojiFolder : String) {
        this.openEmojiPatterns.clear()
        this.openEmojiPatterns.addAll(openEmojiPatterns)
        this.triggerWithColon = triggerCondition
        this.customEmojiFolder = customEmojiFolder

        applyConfigEmojiPatterns()
        applyConfigTriggerWithColon()
        applyConfigCustomEmojiFolder()
    }

    fun getEmojiPatterns(): MutableList<OpenEmojiPattern> {
        return openEmojiPatterns
    }

    fun getTriggerWithColon(): Boolean {
        return triggerWithColon
    }

    fun getCustomEmojiFolder() : String {
        return customEmojiFolder
    }

    private fun getConfigEmojiPatterns(): MutableList<OpenEmojiPattern> {
        val projectInstance = PropertiesComponent.getInstance()
        return Gson().fromJson(projectInstance.getValue(OPEN_GIT_EMOJI_TABLE_INFO_KEY)
                ?: Gson().toJson(mutableListOf<OpenEmojiPattern>()), ListTypeToken().type)
    }

    private fun applyConfigEmojiPatterns() {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GIT_EMOJI_TABLE_INFO_KEY, Gson().toJson(openEmojiPatterns))
    }

    private fun getConfigTriggerWithColon(): Boolean {
        val projectInstance = PropertiesComponent.getInstance()
        return projectInstance.getValue(OPEN_GIT_EMOJI_TC_TEXT_KEY)?.toBoolean() ?: false
    }

    private fun applyConfigTriggerWithColon() {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GIT_EMOJI_TC_TEXT_KEY, triggerWithColon)
    }

    private fun getConfigCustomEmojiFolder(): String {
        val projectInstance = PropertiesComponent.getInstance()
        return projectInstance.getValue(OPEN_GIT_EMOJI_CUSTOM_FOLDER_KEY) ?: ""
    }

    private fun applyConfigCustomEmojiFolder() {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GIT_EMOJI_CUSTOM_FOLDER_KEY, customEmojiFolder)
    }

    private fun isCompatibleWithOldConfigurations(): Boolean {
        val projectInstance = PropertiesComponent.getInstance()
        return projectInstance.getValue(OPEN_COMPATIBLE_WITH_OLD_CONFIG)?.toBoolean() ?: false
    }

    private fun applyCompatibleWithOldConfigurations() {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_COMPATIBLE_WITH_OLD_CONFIG, true)
    }

    fun gms(): List<OpenEmoji> {
        return openEmojiList
    }

    private fun loadGM() {
        javaClass.getResourceAsStream("/${EMOJI_FILE_NAME}").use { inputStream ->
            if (inputStream != null) {
                val text = inputStream.bufferedReader().readText()
                Gson().fromJson(text, OpenEmojiList::class.java).also {
                    it.emojis.forEach(openEmojiList::add)
                }
            }
        }
    }

    class ListTypeToken : TypeToken<MutableList<OpenEmojiPattern>>()

    private fun compatibleWithOldConfigurations() {
        if (isCompatibleWithOldConfigurations() || getConfigEmojiPatterns().isNotEmpty()) {
            return
        }

        val language = OpenEmojiContextDisabled.getLanguage()
        val inputModel = OpenEmojiContextDisabled.getInputModel()
        val triggerCondition = OpenEmojiContextDisabled.getTriggerCondition()
        val suffixText = OpenEmojiContextDisabled.getSuffixText()
        var str = ""

        str += when (inputModel) {
            OpenEmojiInputModel.EMOJI -> {
                OpenEmojiUtils.G
            }

            OpenEmojiInputModel.CODE -> {
                OpenEmojiUtils.GU
            }

            OpenEmojiInputModel.EMOJI_WITH_DESC -> {
                if (OpenEmojiLanguage.ZH == language) "${OpenEmojiUtils.G} ${OpenEmojiUtils.DESC_CN}" else "${OpenEmojiUtils.G} ${OpenEmojiUtils.DESC}"
            }

            OpenEmojiInputModel.CODE_WITH_DESC -> {
                if (OpenEmojiLanguage.ZH == language) "${OpenEmojiUtils.GU} ${OpenEmojiUtils.DESC_CN}" else "${OpenEmojiUtils.GU} ${OpenEmojiUtils.DESC}"
            }
        }
        str += suffixText

        // 兼容旧配置
        this.triggerWithColon = triggerCondition
        this.openEmojiPatterns.add(OpenEmojiPattern(str, true))
        applyConfigTriggerWithColon()
        applyConfigEmojiPatterns()

        OpenEmojiContextDisabled.unsetAllValue()

        applyCompatibleWithOldConfigurations()
    }

}