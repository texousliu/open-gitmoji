package com.github.texousliu.opengitmoji.config

import com.github.texousliu.opengitmoji.context.OpenGMContext
import com.github.texousliu.opengitmoji.model.GMField
import com.github.texousliu.opengitmoji.model.GMInputModel
import com.github.texousliu.opengitmoji.model.GMLanguage
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.util.containers.stream
import com.intellij.util.ui.UI
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel


class OpenGMConfig : SearchableConfigurable {
    private val gmSettingsPanel: JPanel

    private val gmLanguageComboBox = ComboBox(GMLanguage.values().stream().map(GMLanguage::description).toArray())
    private val gmInputModelComboBox = ComboBox(GMInputModel.values().stream().map(GMInputModel::description).toArray())

    private val gmPresentableTextComboBox = ComboBox(GMField.values().stream().map(GMField::description).toArray())
    private val gmTailTextComboBox = ComboBox(GMField.values().stream().map(GMField::description).toArray())
    private val gmTypeTextComboBox = ComboBox(GMField.values().stream().map(GMField::description).toArray())

    private val gmTriggerConditionBox = JCheckBox("Trigger start with colon")
    private val gmSuffixTextField = ExpandableTextField()

    private var configGMLanguage: GMLanguage = OpenGMContext.getLanguage()
    private var configGMInputModel: GMInputModel = OpenGMContext.getInputModel()

    private var configGMPresentableText: GMField = OpenGMContext.getPresentableText()
    private var configGMTailText: GMField = OpenGMContext.getTailText()
    private var configGMTypeText: GMField = OpenGMContext.getTypeText()
    private var configGMTriggerCondition: Boolean = OpenGMContext.getTriggerCondition()
    private var configGMSuffixText: String = OpenGMContext.getSuffixText();

    override fun isModified(): Boolean = gmLanguageComboBox.selectedIndex != configGMLanguage.ordinal
            || gmInputModelComboBox.selectedIndex != configGMInputModel.ordinal
            || gmPresentableTextComboBox.selectedIndex != configGMPresentableText.ordinal
            || gmTailTextComboBox.selectedIndex != configGMTailText.ordinal
            || gmTypeTextComboBox.selectedIndex != configGMTypeText.ordinal
            || gmTriggerConditionBox.isSelected != configGMTriggerCondition
            || gmSuffixTextField.text != configGMSuffixText


    override fun getDisplayName(): String = "Open Gitmoji"

    override fun getId(): String = "open.texousliu.config.settings.gm.OpenGMSettings"

    init {
        // input border
        val input = UI.PanelFactory.grid()
            .add(UI.PanelFactory.panel(gmTriggerConditionBox)
                .withComment("Get prompt through text starting with ':' or '：'. Such as ':s'"))
            .createPanel()
        input.border = IdeBorderFactory.createTitledBorder("Input")

        // format border
        // 输入格式
        val gmModelPanel = JPanel(FlowLayout(FlowLayout.LEADING))
        gmModelPanel.add(gmInputModelComboBox, null)
        gmModelPanel.add(gmLanguageComboBox, null)
        // 提示格式
        val gmTextPanel = JPanel(FlowLayout(FlowLayout.LEADING))
        gmTextPanel.add(gmPresentableTextComboBox, null)
        gmTextPanel.add(gmTailTextComboBox, null)
        gmTextPanel.add(gmTypeTextComboBox, null)

        val format = UI.PanelFactory.grid()
            .add(UI.PanelFactory.panel(gmModelPanel).withLabel("Input format:"))
            .add(UI.PanelFactory.panel(gmTextPanel).withLabel("Hint format:"))
            .add(UI.PanelFactory.panel(gmSuffixTextField).withLabel("Suffix append:")
                .withComment("Add some additional information after entering text. " +
                        "For example: text, date and time. " +
                        "The default is a space text. <br/>" +
                        "Date expression: #{DATE}, " +
                        "Time expression: #{TIME}"))
            .createPanel()
        format.border = IdeBorderFactory.createTitledBorder("Format")

        gmSettingsPanel = JPanel(BorderLayout())
        gmSettingsPanel.add(input, BorderLayout.NORTH)
        gmSettingsPanel.add(format, BorderLayout.WEST)
    }

    override fun apply() {
        configGMLanguage = GMLanguage.values()[gmLanguageComboBox.selectedIndex]
        configGMInputModel = GMInputModel.values()[gmInputModelComboBox.selectedIndex]

        configGMPresentableText = GMField.values()[gmPresentableTextComboBox.selectedIndex]
        configGMTailText = GMField.values()[gmTailTextComboBox.selectedIndex]
        configGMTypeText = GMField.values()[gmTypeTextComboBox.selectedIndex]

        configGMTriggerCondition = gmTriggerConditionBox.isSelected
        configGMSuffixText = gmSuffixTextField.text

        OpenGMContext.setLanguage(configGMLanguage)
        OpenGMContext.setInputModel(configGMInputModel)

        OpenGMContext.setPresentableText(configGMPresentableText)
        OpenGMContext.setTailText(configGMTailText)
        OpenGMContext.setTypeText(configGMTypeText)

        OpenGMContext.setTriggerCondition(configGMTriggerCondition)
        OpenGMContext.setSuffixText(configGMSuffixText)
    }

    override fun reset() {
        configGMLanguage = OpenGMContext.getLanguage()
        configGMInputModel = OpenGMContext.getInputModel()

        configGMPresentableText = OpenGMContext.getPresentableText()
        configGMTailText = OpenGMContext.getTailText()
        configGMTypeText = OpenGMContext.getTypeText()

        configGMTriggerCondition = OpenGMContext.getTriggerCondition()
        configGMSuffixText = OpenGMContext.getSuffixText()

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
