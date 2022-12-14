package com.github.texousliu.opengitmoji.context

import com.github.texousliu.opengitmoji.model.*
import com.google.gson.Gson
import com.intellij.ide.util.PropertiesComponent

const val OPEN_GM_INPUT_MODEL_KEY = "open.texousliu.config.settings.gm.OpenGMSettings.OpenGMInputModelKey"
const val OPEN_GM_LANGUAGE_KEY = "open.texousliu.config.settings.gm.OpenGMSettings.OpenGMLanguageKey"
const val OPEN_GM_PRESENTABLE_TEXT_KEY = "open.texousliu.config.settings.gm.OpenGMSettings.OpenGMPresentableTextKey"
const val OPEN_GM_TAIL_TEXT_KEY = "open.texousliu.config.settings.gm.OpenGMSettings.OpenGMTailTextKey"
const val OPEN_GM_TYPE_TEXT_KEY = "open.texousliu.config.settings.gm.OpenGMSettings.OpenGMTypeTextKey"
const val OPEN_GM_TC_TEXT_KEY = "open.texousliu.config.settings.gm.OpenGMSettings.OpenGMTCTextKey"
const val OPEN_GM_SUFFIX_TEXT_KEY = "open.texousliu.config.settings.gm.OpenGMSettings.OpenGMSuffixTextKey"

object OpenGMContext {

    const val REPLACE_SUFFIX_MARK = "$$:$$"
    private val gmList = ArrayList<GM>()

    init {
        loadGM()
    }

    fun getLanguage(): GMLanguage {
        val projectInstance = PropertiesComponent.getInstance()
        val li = projectInstance.getValue(OPEN_GM_LANGUAGE_KEY)?.toInt() ?: GMLanguage.ZH.ordinal
        return GMLanguage.values()[li]
    }

    fun setLanguage(language: GMLanguage) {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GM_LANGUAGE_KEY, language.ordinal.toString())
    }

    fun getInputModel(): GMInputModel {
        val projectInstance = PropertiesComponent.getInstance()
        val mi = projectInstance.getValue(OPEN_GM_INPUT_MODEL_KEY)?.toInt() ?: GMInputModel.EMOJI.ordinal
        return GMInputModel.values()[mi]
    }

    fun setInputModel(model: GMInputModel) {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GM_INPUT_MODEL_KEY, model.ordinal.toString())
    }

    fun getPresentableText(): GMField {
        val projectInstance = PropertiesComponent.getInstance()
        val mi = projectInstance.getValue(OPEN_GM_PRESENTABLE_TEXT_KEY)?.toInt() ?: GMField.CN_DESCRIPTION.ordinal
        return GMField.values()[mi]
    }

    fun setPresentableText(filed: GMField) {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GM_PRESENTABLE_TEXT_KEY, filed.ordinal.toString())
    }

    fun getTailText(): GMField {
        val projectInstance = PropertiesComponent.getInstance()
        val mi = projectInstance.getValue(OPEN_GM_TAIL_TEXT_KEY)?.toInt() ?: GMField.DESCRIPTION.ordinal
        return GMField.values()[mi]
    }

    fun setTailText(filed: GMField) {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GM_TAIL_TEXT_KEY, filed.ordinal.toString())
    }

    fun getTypeText(): GMField {
        val projectInstance = PropertiesComponent.getInstance()
        val mi = projectInstance.getValue(OPEN_GM_TYPE_TEXT_KEY)?.toInt() ?: GMField.CODE.ordinal
        return GMField.values()[mi]
    }

    fun setTypeText(filed: GMField) {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GM_TYPE_TEXT_KEY, filed.ordinal.toString())
    }

    fun getTriggerCondition(): Boolean {
        val projectInstance = PropertiesComponent.getInstance()
        return projectInstance.getValue(OPEN_GM_TC_TEXT_KEY)?.toBoolean() ?: false
    }

    fun setTriggerCondition(v: Boolean) {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GM_TC_TEXT_KEY, v)
    }

    fun getSuffixText(): String {
        val projectInstance = PropertiesComponent.getInstance()
        return projectInstance.getValue(OPEN_GM_SUFFIX_TEXT_KEY) ?: " "
    }

    fun setSuffixText(v: String) {
        val projectInstance = PropertiesComponent.getInstance()
        return projectInstance.setValue(OPEN_GM_SUFFIX_TEXT_KEY, v)
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