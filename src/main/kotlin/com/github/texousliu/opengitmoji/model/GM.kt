package com.github.texousliu.opengitmoji.model

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

class GM(
    val emoji: String,
    val entity: String,
    val code: String,
    val name: String,
    val description: String,
    val cn_description: String
) {

    private lateinit var _icon: Icon

    fun getIcon(): Icon {
        if (!this::_icon.isInitialized) {
            _icon = IconLoader.getIcon("/icons/gitmoji/${code.replace(":".toRegex(), "")}.png", GM::class.java)
        }
        return _icon
    }

}