package com.github.texousliu.opengitmoji.config

import com.github.texousliu.opengitmoji.context.OpenGMContext
import com.github.texousliu.opengitmoji.model.GMField
import com.github.texousliu.opengitmoji.model.GMInputModel
import com.github.texousliu.opengitmoji.model.GMLanguage
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.ComboBox
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel


class OpenGMConfig // constructor(private val project: Project)
    : SearchableConfigurable {
    private val gmSettingsPanel: JPanel

    private val gmLanguageComboBox = ComboBox(GMLanguage.values())
    private val gmInputModelComboBox = ComboBox(GMInputModel.values())

    private val gmPresentableTextComboBox = ComboBox(GMField.values())
    private val gmTailTextComboBox = ComboBox(GMField.values())
    private val gmTypeTextComboBox = ComboBox(GMField.values())

    private val gmTriggerConditionBox = JCheckBox("")

    private var configGMLanguage: GMLanguage = OpenGMContext.getLanguage()
    private var configGMInputModel: GMInputModel = OpenGMContext.getInputModel()

    private var configGMPresentableText: GMField = OpenGMContext.getPresentableText()
    private var configGMTailText: GMField = OpenGMContext.getTailText()
    private var configGMTypeText: GMField = OpenGMContext.getTypeText()
    private var configGMTriggerCondition: Boolean = OpenGMContext.getTriggerCondition()

    override fun isModified(): Boolean = gmLanguageComboBox.selectedIndex != configGMLanguage.ordinal
            || gmInputModelComboBox.selectedIndex != configGMInputModel.ordinal
            || gmPresentableTextComboBox.selectedIndex != configGMPresentableText.ordinal
            || gmTailTextComboBox.selectedIndex != configGMTailText.ordinal
            || gmTypeTextComboBox.selectedIndex != configGMTypeText.ordinal
            || gmTriggerConditionBox.isSelected != configGMTriggerCondition


    override fun getDisplayName(): String = "OpenGM"

    override fun getId(): String = "open.texousliu.config.settings.gm.OpenGMSettings"

    init {
        val flow = GridLayout(20, 2)
        gmSettingsPanel = JPanel(flow)
        val gmLanguagePanel = JPanel(FlowLayout(FlowLayout.LEADING))
        gmLanguagePanel.add(JLabel("GM Language "))
        gmLanguagePanel.add(gmLanguageComboBox, null)
        gmSettingsPanel.add(gmLanguagePanel)

        val gmModelPanel = JPanel(FlowLayout(FlowLayout.LEADING))
        gmModelPanel.add(JLabel("GM Input Model "))
        gmModelPanel.add(gmInputModelComboBox, null)
        gmSettingsPanel.add(gmModelPanel)

        val gmTextPanel = JPanel(FlowLayout(FlowLayout.LEADING))
        gmTextPanel.add(JLabel("GM Hint Formatter "))
        gmTextPanel.add(gmPresentableTextComboBox, null)
        gmTextPanel.add(gmTailTextComboBox, null)
        gmTextPanel.add(gmTypeTextComboBox, null)
        gmSettingsPanel.add(gmTextPanel)

        val gmTCPanel = JPanel(FlowLayout(FlowLayout.LEADING))
        gmTCPanel.add(JLabel("GM Trigger Start With ':' "))
        gmTCPanel.add(gmTriggerConditionBox, null)
        gmSettingsPanel.add(gmTCPanel)
    }

    override fun apply() {
        configGMLanguage = GMLanguage.values()[gmLanguageComboBox.selectedIndex]
        configGMInputModel = GMInputModel.values()[gmInputModelComboBox.selectedIndex]

        configGMPresentableText = GMField.values()[gmPresentableTextComboBox.selectedIndex]
        configGMTailText = GMField.values()[gmTailTextComboBox.selectedIndex]
        configGMTypeText = GMField.values()[gmTypeTextComboBox.selectedIndex]

        configGMTriggerCondition = gmTriggerConditionBox.isSelected

        OpenGMContext.setLanguage(configGMLanguage)
        OpenGMContext.setInputModel(configGMInputModel)

        OpenGMContext.setPresentableText(configGMPresentableText)
        OpenGMContext.setTailText(configGMTailText)
        OpenGMContext.setTypeText(configGMTypeText)

        OpenGMContext.setTriggerCondition(configGMTriggerCondition)
    }

    override fun reset() {
        configGMLanguage = OpenGMContext.getLanguage()
        configGMInputModel = OpenGMContext.getInputModel()

        configGMPresentableText = OpenGMContext.getPresentableText()
        configGMTailText = OpenGMContext.getTailText()
        configGMTypeText = OpenGMContext.getTypeText()

        configGMTriggerCondition = OpenGMContext.getTriggerCondition()

        gmLanguageComboBox.selectedIndex = configGMLanguage.ordinal
        gmInputModelComboBox.selectedIndex = configGMInputModel.ordinal

        gmPresentableTextComboBox.selectedIndex = configGMPresentableText.ordinal
        gmTailTextComboBox.selectedIndex = configGMTailText.ordinal
        gmTypeTextComboBox.selectedIndex = configGMTypeText.ordinal

        gmTriggerConditionBox.isSelected = configGMTriggerCondition
    }

    override fun createComponent(): JComponent = gmSettingsPanel

}
