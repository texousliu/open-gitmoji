package com.github.texousliu.opengitmoji.context

import com.github.texousliu.opengitmoji.model.*
import com.github.texousliu.opengitmoji.utils.OpenGitmojiUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.ide.util.PropertiesComponent

const val OPEN_GIT_EMOJI_TABLE_INFO_KEY = "open.texousliu.config.settings.gm.OpenGitmojiSettings.OpenGMTableInfoKey"
const val OPEN_GIT_EMOJI_TC_TEXT_KEY = "open.texousliu.config.settings.gm.OpenGitmojiSettings.OpenGMTCTextKey"
const val OPEN_COMPATIBLE_WITH_OLD_CONFIG = "open.texousliu.config.settings.gm.OpenGitmojiSettings.OpenGMCompatible"

object OpenGitmojiContext {

    const val REPLACE_SUFFIX_MARK = "$$:$$"
    private val gmList = ArrayList<GM>()
    private var gitmojiPatterns = mutableListOf<GitmojiPattern>()
    private var triggerWithColon = true

    init {
        compatibleWithOldConfigurations()
        loadGM()
    }

    fun reset() {
        triggerWithColon = getConfigTriggerWithColon()
        gitmojiPatterns = getConfigGitmojiPatterns()
    }

    fun apply(gitmojiPatterns: MutableList<GitmojiPattern>, triggerCondition: Boolean) {
        this.gitmojiPatterns.clear()
        this.gitmojiPatterns.addAll(gitmojiPatterns)
        this.triggerWithColon = triggerCondition

        applyConfigGitmojiPatterns()
        applyConfigTriggerWithColon()
    }

    fun getGitmojiPatterns(): MutableList<GitmojiPattern> {
        return gitmojiPatterns
    }

    fun getTriggerWithColon(): Boolean {
        return triggerWithColon
    }

    fun getConfigGitmojiPatterns(): MutableList<GitmojiPattern> {
        val projectInstance = PropertiesComponent.getInstance()
        return Gson().fromJson(projectInstance.getValue(OPEN_GIT_EMOJI_TABLE_INFO_KEY)
                ?: Gson().toJson(mutableListOf<GitmojiPattern>()), ListTypeToken().type)
    }

    private fun applyConfigGitmojiPatterns() {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GIT_EMOJI_TABLE_INFO_KEY, Gson().toJson(gitmojiPatterns))
    }

    fun getConfigTriggerWithColon(): Boolean {
        val projectInstance = PropertiesComponent.getInstance()
        return projectInstance.getValue(OPEN_GIT_EMOJI_TC_TEXT_KEY)?.toBoolean() ?: false
    }

    private fun applyConfigTriggerWithColon() {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GIT_EMOJI_TC_TEXT_KEY, triggerWithColon)
    }

    private fun isCompatibleWithOldConfigurations(): Boolean {
        val projectInstance = PropertiesComponent.getInstance()
        return projectInstance.getValue(OPEN_COMPATIBLE_WITH_OLD_CONFIG)?.toBoolean() ?: false
    }

    private fun applyCompatibleWithOldConfigurations() {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_COMPATIBLE_WITH_OLD_CONFIG, true)
    }

    fun gms(): List<GM> {
        return gmList
    }

    private fun loadGM() {
        javaClass.getResourceAsStream("/gitmojis.json").use { inputStream ->
            if (inputStream != null) {
                val text = inputStream.bufferedReader().readText()
                Gson().fromJson(text, GMList::class.java).also {
                    it.gitmojis.forEach(gmList::add)
                }
            }
        }
    }

    class ListTypeToken : TypeToken<MutableList<GitmojiPattern>>()

    private fun compatibleWithOldConfigurations() {
        if (isCompatibleWithOldConfigurations() || getConfigGitmojiPatterns().isNotEmpty()) {
            return
        }

        val language = OpenGMContext.getLanguage()
        val inputModel = OpenGMContext.getInputModel()
        val triggerCondition = OpenGMContext.getTriggerCondition()
        val suffixText = OpenGMContext.getSuffixText()
        var str = ""

        str += when (inputModel) {
            GMInputModel.EMOJI -> {
                OpenGitmojiUtils.G
            }

            GMInputModel.CODE -> {
                OpenGitmojiUtils.GU
            }

            GMInputModel.EMOJI_WITH_DESC -> {
                if (GMLanguage.ZH == language) "${OpenGitmojiUtils.G} ${OpenGitmojiUtils.DESC_CN}" else "${OpenGitmojiUtils.G} ${OpenGitmojiUtils.DESC}"
            }

            GMInputModel.CODE_WITH_DESC -> {
                if (GMLanguage.ZH == language) "${OpenGitmojiUtils.GU} ${OpenGitmojiUtils.DESC_CN}" else "${OpenGitmojiUtils.GU} ${OpenGitmojiUtils.DESC}"
            }
        }
        str += suffixText

        // 兼容旧配置
        this.triggerWithColon = triggerCondition
        this.gitmojiPatterns.add(GitmojiPattern(str, true))
        applyConfigTriggerWithColon()
        applyConfigGitmojiPatterns()

        OpenGMContext.unsetAllValue()

        applyCompatibleWithOldConfigurations()
    }

}