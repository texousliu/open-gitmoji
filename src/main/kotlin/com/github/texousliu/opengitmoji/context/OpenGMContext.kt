package com.github.texousliu.opengitmoji.context

import com.github.texousliu.opengitmoji.model.GM
import com.github.texousliu.opengitmoji.model.GMS
import com.google.gson.Gson

object OpenGMContext {

    private val gmList = ArrayList<GM>()

    init {
        loadGM()
    }

    fun gms(): List<GM> {
        return gmList
    }

    private fun loadGM() {
        javaClass.getResourceAsStream("/gitmojis.json").use { inputStream ->
            if (inputStream != null) {
                val text = inputStream.bufferedReader().readText()
                Gson().fromJson(text, GMS::class.java).also {
                    it.gitmojis.forEach(gmList::add)
                }
            }
        }
    }

}