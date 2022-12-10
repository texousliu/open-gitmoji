package com.github.texousliu.opengitmoji.config

import com.github.texousliu.opengitmoji.context.OpenGMContext
import com.github.texousliu.opengitmoji.model.GMField
import com.github.texousliu.opengitmoji.model.GMInputModel
import com.github.texousliu.opengitmoji.model.GMLanguage
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.util.containers.stream
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import kotlin.streams.toList


class OpenGMConfig : SearchableConfigurable {
    private val gmSettingsPanel: JPanel

    private val gmLanguageComboBox = ComboBox(GMLanguage.values().stream().map(GMLanguage::description).toArray())
    private val gmInputModelComboBox = ComboBox(GMInputModel.values().stream().map(GMInputModel::description).toArray())

    private val gmPresentableTextComboBox = ComboBox(GMField.values().stream().map(GMField::description).toArray())
    private val gmTailTextComboBox = ComboBox(GMField.values().stream().map(GMField::description).toArray())
    private val gmTypeTextComboBox = ComboBox(GMField.values().stream().map(GMField::description).toArray())

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


    override fun getDisplayName(): String = "Open Gitmoji"

    override fun getId(): String = "open.texousliu.config.settings.gm.OpenGMSettings"

    init {
        val flow = GridLayout(20, 2)
        gmSettingsPanel = JPanel(flow)

        val gmTCPanel = JPanel(FlowLayout(FlowLayout.LEADING))
        gmTCPanel.add(JLabel("Trigger Start With ':' "))
        gmTCPanel.add(gmTriggerConditionBox, null)
        gmSettingsPanel.add(gmTCPanel)

        val gmModelPanel = JPanel(FlowLayout(FlowLayout.LEADING))
        gmModelPanel.add(JLabel("Input Model    "))
        gmModelPanel.add(gmInputModelComboBox, null)
        gmModelPanel.add(gmLanguageComboBox, null)
        gmSettingsPanel.add(gmModelPanel)

        val gmTextPanel = JPanel(FlowLayout(FlowLayout.LEADING))
        gmTextPanel.add(JLabel("Hint Formatter "))
        gmTextPanel.add(gmPresentableTextComboBox, null)
        gmTextPanel.add(gmTailTextComboBox, null)
        gmTextPanel.add(gmTypeTextComboBox, null)
        gmSettingsPanel.add(gmTextPanel)
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
