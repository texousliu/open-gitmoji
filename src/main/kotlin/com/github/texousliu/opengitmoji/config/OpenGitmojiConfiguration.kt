package com.github.texousliu.opengitmoji.config

import com.github.texousliu.opengitmoji.context.OpenGitmojiContext
import com.github.texousliu.opengitmoji.dialog.openGitmojiDialogPanel
import com.github.texousliu.opengitmoji.model.GMField
import com.github.texousliu.opengitmoji.model.GMInputModel
import com.github.texousliu.opengitmoji.model.GMLanguage
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.panel.PanelBuilder
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.util.containers.stream
import com.intellij.util.ui.UI
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel


class OpenGitmojiConfiguration : SearchableConfigurable {
    private val gmSettingsPanel: JPanel

    private val gmLanguageComboBox = ComboBox(GMLanguage.values().stream().map(GMLanguage::description).toArray())
    private val gmInputModelComboBox = ComboBox(GMInputModel.values().stream().map(GMInputModel::description).toArray())

    private val gmPresentableTextComboBox = ComboBox(GMField.values().stream().map(GMField::description).toArray())
    private val gmTailTextComboBox = ComboBox(GMField.values().stream().map(GMField::description).toArray())
    private val gmTypeTextComboBox = ComboBox(GMField.values().stream().map(GMField::description).toArray())

    private val gmTriggerConditionBox = JCheckBox("Trigger start with colon")
    private val gmSuffixTextField = ExpandableTextField()

    private var configGMLanguage: GMLanguage = OpenGitmojiContext.getLanguage()
    private var configGMInputModel: GMInputModel = OpenGitmojiContext.getInputModel()

    private var configGMPresentableText: GMField = OpenGitmojiContext.getPresentableText()
    private var configGMTailText: GMField = OpenGitmojiContext.getTailText()
    private var configGMTypeText: GMField = OpenGitmojiContext.getTypeText()
    private var configGMTriggerCondition: Boolean = OpenGitmojiContext.getTriggerCondition()
    private var configGMSuffixText: String = OpenGitmojiContext.getSuffixText();

    override fun isModified(): Boolean = gmLanguageComboBox.selectedIndex != configGMLanguage.ordinal
            || gmInputModelComboBox.selectedIndex != configGMInputModel.ordinal
            || gmPresentableTextComboBox.selectedIndex != configGMPresentableText.ordinal
            || gmTailTextComboBox.selectedIndex != configGMTailText.ordinal
            || gmTypeTextComboBox.selectedIndex != configGMTypeText.ordinal
            || gmTriggerConditionBox.isSelected != configGMTriggerCondition
            || gmSuffixTextField.text != configGMSuffixText


    override fun getDisplayName(): String = "Open Gitmoji Settings"

    override fun getId(): String = "open.texousliu.config.settings.gm.OpenGitmojiSettings"

    init {
        // input border
        gmSettingsPanel = openGitmojiDialogPanel()
    }

    override fun apply() {
        configGMLanguage = GMLanguage.values()[gmLanguageComboBox.selectedIndex]
        configGMInputModel = GMInputModel.values()[gmInputModelComboBox.selectedIndex]

        configGMPresentableText = GMField.values()[gmPresentableTextComboBox.selectedIndex]
        configGMTailText = GMField.values()[gmTailTextComboBox.selectedIndex]
        configGMTypeText = GMField.values()[gmTypeTextComboBox.selectedIndex]

        configGMTriggerCondition = gmTriggerConditionBox.isSelected
        configGMSuffixText = gmSuffixTextField.text

        OpenGitmojiContext.setLanguage(configGMLanguage)
        OpenGitmojiContext.setInputModel(configGMInputModel)

        OpenGitmojiContext.setPresentableText(configGMPresentableText)
        OpenGitmojiContext.setTailText(configGMTailText)
        OpenGitmojiContext.setTypeText(configGMTypeText)

        OpenGitmojiContext.setTriggerCondition(configGMTriggerCondition)
        OpenGitmojiContext.setSuffixText(configGMSuffixText)
    }

    override fun reset() {
        configGMLanguage = OpenGitmojiContext.getLanguage()
        configGMInputModel = OpenGitmojiContext.getInputModel()

        configGMPresentableText = OpenGitmojiContext.getPresentableText()
        configGMTailText = OpenGitmojiContext.getTailText()
        configGMTypeText = OpenGitmojiContext.getTypeText()

        configGMTriggerCondition = OpenGitmojiContext.getTriggerCondition()
        configGMSuffixText = OpenGitmojiContext.getSuffixText()

        gmLanguageComboBox.selectedIndex = configGMLanguage.ordinal
        gmInputModelComboBox.selectedIndex = configGMInputModel.ordinal

        gmPresentableTextComboBox.selectedIndex = configGMPresentableText.ordinal
        gmTailTextComboBox.selectedIndex = configGMTailText.ordinal
        gmTypeTextComboBox.selectedIndex = configGMTypeText.ordinal

        gmTriggerConditionBox.isSelected = configGMTriggerCondition
        gmSuffixTextField.text = configGMSuffixText
    }

    override fun createComponent(): JComponent = gmSettingsPanel

}
