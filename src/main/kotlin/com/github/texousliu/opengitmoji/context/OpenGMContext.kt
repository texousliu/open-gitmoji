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

/**
 * 已废弃，只是为了兼容旧版本配置所以保留，后续随着版本迭代后移除
 */
object OpenGMContext {

    fun unsetAllValue() {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.unsetValue(OPEN_GM_INPUT_MODEL_KEY)
        projectInstance.unsetValue(OPEN_GM_LANGUAGE_KEY)
        projectInstance.unsetValue(OPEN_GM_PRESENTABLE_TEXT_KEY)
        projectInstance.unsetValue(OPEN_GM_TAIL_TEXT_KEY)
        projectInstance.unsetValue(OPEN_GM_TYPE_TEXT_KEY)
        projectInstance.unsetValue(OPEN_GM_TC_TEXT_KEY)
        projectInstance.unsetValue(OPEN_GM_SUFFIX_TEXT_KEY)
    }

    fun getLanguage(): GMLanguage {
        val projectInstance = PropertiesComponent.getInstance()
        val li = projectInstance.getValue(OPEN_GM_LANGUAGE_KEY)?.toInt() ?: GMLanguage.ZH.ordinal
        return GMLanguage.values()[li]
    }

    fun getInputModel(): GMInputModel {
        val projectInstance = PropertiesComponent.getInstance()
        val mi = projectInstance.getValue(OPEN_GM_INPUT_MODEL_KEY)?.toInt() ?: GMInputModel.EMOJI.ordinal
        return GMInputModel.values()[mi]
    }

    fun getTriggerCondition(): Boolean {
        val projectInstance = PropertiesComponent.getInstance()
        return projectInstance.getValue(OPEN_GM_TC_TEXT_KEY)?.toBoolean() ?: false
    }

    fun getSuffixText(): String {
        val projectInstance = PropertiesComponent.getInstance()
        return projectInstance.getValue(OPEN_GM_SUFFIX_TEXT_KEY) ?: " "
    }

}