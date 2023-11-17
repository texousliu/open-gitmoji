package com.github.texousliu.opengitmoji.context

import com.github.texousliu.opengitmoji.model.OpenEmojiInputModel
import com.github.texousliu.opengitmoji.model.OpenEmojiLanguage
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
object OpenEmojiContextDisabled {

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

    fun getLanguage(): OpenEmojiLanguage {
        val projectInstance = PropertiesComponent.getInstance()
        val li = projectInstance.getValue(OPEN_GM_LANGUAGE_KEY)?.toInt() ?: OpenEmojiLanguage.ZH.ordinal
        return OpenEmojiLanguage.values()[li]
    }

    fun getInputModel(): OpenEmojiInputModel {
        val projectInstance = PropertiesComponent.getInstance()
        val mi = projectInstance.getValue(OPEN_GM_INPUT_MODEL_KEY)?.toInt() ?: OpenEmojiInputModel.EMOJI.ordinal
        return OpenEmojiInputModel.values()[mi]
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