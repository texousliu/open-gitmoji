package com.github.texousliu.opengitmoji.config

import com.github.texousliu.opengitmoji.context.OpenGMContext
import com.github.texousliu.opengitmoji.model.GMInputModel
import com.github.texousliu.opengitmoji.model.GMLanguage
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.ComboBox
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel


class OpenGMConfig // constructor(private val project: Project)
    : SearchableConfigurable {
    private val gmSettingsPanel: JPanel

    private val gmLanguageComboBox = ComboBox(GMLanguage.values())
    private val gmInputModelComboBox = ComboBox(GMInputModel.values())

    private var configGMLanguage: GMLanguage = OpenGMContext.getLanguage()
    private var configGMInputModel: GMInputModel = OpenGMContext.getInputModel()

    override fun isModified(): Boolean = gmLanguageComboBox.selectedIndex != configGMLanguage.ordinal
            || gmInputModelComboBox.selectedIndex != configGMInputModel.ordinal


    override fun getDisplayName(): String = "OpenGM"

    override fun getId(): String = "open.texousliu.config.settings.gm.OpenGMSettings"

    init {
        val flow = GridLayout(20, 2)
        gmSettingsPanel = JPanel(flow)
        val gmLanguagePanel = JPanel(FlowLayout(FlowLayout.LEADING))
        gmLanguagePanel.add(JLabel("GM Language"))
        gmLanguagePanel.add(gmLanguageComboBox, null)
        gmSettingsPanel.add(gmLanguagePanel)

        val gmModelPanel = JPanel(FlowLayout(FlowLayout.LEADING))
        gmModelPanel.add(JLabel("GM Input Model"))
        gmModelPanel.add(gmInputModelComboBox, null)
        gmSettingsPanel.add(gmModelPanel)
    }

    override fun apply() {
        configGMLanguage = GMLanguage.values()[gmLanguageComboBox.selectedIndex]
        configGMInputModel = GMInputModel.values()[gmInputModelComboBox.selectedIndex]

        OpenGMContext.setLanguage(configGMLanguage)
        OpenGMContext.setInputModel(configGMInputModel)
    }

    override fun reset() {
        configGMLanguage = OpenGMContext.getLanguage()
        configGMInputModel = OpenGMContext.getInputModel()

        gmLanguageComboBox.selectedIndex = configGMLanguage.ordinal
        gmInputModelComboBox.selectedIndex = configGMInputModel.ordinal
    }

    override fun createComponent(): JComponent = gmSettingsPanel

}
