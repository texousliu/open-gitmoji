package com.github.texousliu.opengitmoji.context

import com.github.texousliu.opengitmoji.model.GM
import com.github.texousliu.opengitmoji.model.GMList
import com.github.texousliu.opengitmoji.model.RegexTableInfo
import com.google.gson.Gson
import com.intellij.ide.util.PropertiesComponent

const val OPEN_GIT_EMOJI_INPUT_MODEL_KEY = "open.texousliu.config.settings.gm.OpenGitmojiSettings.OpenGMInputModelKey"
const val OPEN_GIT_EMOJI_TABLE_INFO_KEY = "open.texousliu.config.settings.gm.OpenGitmojiSettings.OpenGMTableInfoKey"
const val OPEN_GIT_EMOJI_PRESENTABLE_TEXT_KEY = "open.texousliu.config.settings.gm.OpenGitmojiSettings.OpenGMPresentableTextKey"
const val OPEN_GIT_EMOJI_TAIL_TEXT_KEY = "open.texousliu.config.settings.gm.OpenGitmojiSettings.OpenGMTailTextKey"
const val OPEN_GIT_EMOJI_TYPE_TEXT_KEY = "open.texousliu.config.settings.gm.OpenGitmojiSettings.OpenGMTypeTextKey"
const val OPEN_GIT_EMOJI_TC_TEXT_KEY = "open.texousliu.config.settings.gm.OpenGitmojiSettings.OpenGMTCTextKey"
const val OPEN_GIT_EMOJI_SUFFIX_TEXT_KEY = "open.texousliu.config.settings.gm.OpenGitmojiSettings.OpenGMSuffixTextKey"

object OpenGitmojiContext {

    const val REPLACE_SUFFIX_MARK = "$$:$$"
    private val gmList = ArrayList<GM>()
    private var regexTableInfo = Gson().toJson(RegexTableInfo(mutableListOf()))
    private var triggerCondition = true

    init {
        loadGM()
    }

    fun getRegexTableInfo(): String {
        return regexTableInfo
    }

    fun getRegexTableInfoObj(): RegexTableInfo {
        return Gson().fromJson(regexTableInfo, RegexTableInfo::class.java)
    }

    fun setRegexTableInfo(tableInfo: String) {
        this.regexTableInfo = tableInfo
    }

    fun setRegexTableInfo(tableInfo: RegexTableInfo) {
        this.regexTableInfo = Gson().toJson(tableInfo)
    }

    fun getTriggerCondition(): Boolean {
        return triggerCondition
    }

    fun setTriggerCondition(v: Boolean) {
        this.triggerCondition = v
    }

    fun getConfigRegexTableInfo(): String {
        val projectInstance = PropertiesComponent.getInstance()
        return projectInstance.getValue(OPEN_GIT_EMOJI_TABLE_INFO_KEY) ?: Gson().toJson(RegexTableInfo(mutableListOf()))
    }

    fun getConfigRegexTableInfoObj(): RegexTableInfo {
        val projectInstance = PropertiesComponent.getInstance()
        return Gson().fromJson(projectInstance.getValue(OPEN_GIT_EMOJI_TABLE_INFO_KEY)
                ?: Gson().toJson(RegexTableInfo(mutableListOf())),RegexTableInfo::class.java)
    }

    fun setConfigRegexTableInfo(tableInfo: String) {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GIT_EMOJI_TABLE_INFO_KEY, tableInfo)
    }

    fun setConfigRegexTableInfo(tableInfo: RegexTableInfo) {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GIT_EMOJI_TABLE_INFO_KEY, Gson().toJson(tableInfo))
    }

    fun getConfigTriggerCondition(): Boolean {
        val projectInstance = PropertiesComponent.getInstance()
        return projectInstance.getValue(OPEN_GIT_EMOJI_TC_TEXT_KEY)?.toBoolean() ?: false
    }

    fun setConfigTriggerCondition(v: Boolean) {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GIT_EMOJI_TC_TEXT_KEY, v)
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

}