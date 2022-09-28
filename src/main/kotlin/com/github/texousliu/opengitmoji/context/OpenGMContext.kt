package com.github.texousliu.opengitmoji.context

import com.github.texousliu.opengitmoji.model.GM
import com.github.texousliu.opengitmoji.model.GMLanguage
import com.github.texousliu.opengitmoji.model.GMList
import com.github.texousliu.opengitmoji.model.GMInputModel
import com.google.gson.Gson
import com.intellij.ide.util.PropertiesComponent

const val OPEN_GM_INPUT_MODEL_KEY = "open.texousliu.config.settings.gm.OpenGMSettings.OpenGMInputModelKey"
const val OPEN_GM_LANGUAGE_KEY = "open.texousliu.config.settings.gm.OpenGMSettings.OpenGMLanguageKey"

object OpenGMContext {

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
        val mi = projectInstance.getValue(OPEN_GM_INPUT_MODEL_KEY)?.toInt() ?: GMInputModel.UNICODE.ordinal
        return GMInputModel.values()[mi]
    }

    fun setInputModel(model: GMInputModel) {
        val projectInstance = PropertiesComponent.getInstance()
        projectInstance.setValue(OPEN_GM_INPUT_MODEL_KEY, model.ordinal.toString())
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