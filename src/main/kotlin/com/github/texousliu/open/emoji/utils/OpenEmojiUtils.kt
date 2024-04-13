package com.github.texousliu.open.emoji.utils

import com.github.texousliu.open.emoji.context.OpenGitEmojiCache
import com.github.texousliu.open.emoji.model.*
import com.github.texousliu.open.emoji.persistence.OpenEmojiInfoSerializer
import com.github.texousliu.open.emoji.persistence.OpenEmojiPersistent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.function.Consumer
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

object OpenEmojiUtils {

    private const val EMOJI_FILE_NAME = "emojis.json"
    const val REPLACE_SUFFIX_MARK = "$$:$$"

    val GSON = Gson()
    val GSON_INFO: Gson = GsonBuilder()
            .registerTypeAdapter(OpenEmojiInfo::class.java, OpenEmojiInfoSerializer())
            .create()

    private const val G = "#{G}"
    private const val GU = "#{GU}"
    private const val DESC = "#{DESC}"
    private const val DESC_CN = "#{DESC_CN}"
    private const val DATE = "#{DATE}"
    private const val TIME = "#{TIME}"

    private val PATTERNS = arrayOf(G, GU, DESC, DESC_CN, DATE, TIME)

    fun demo(pattern: String): String {
        return replace(pattern, OpenGitEmojiCache.emojiInfoList()[0])
    }

    fun replace(pattern: String, emoji: OpenEmoji): String {
        val params = mapOf(
                G to emoji.emoji, GU to emoji.code,
                DESC to emoji.description, DESC_CN to emoji.cnDescription,
                DATE to date(), TIME to time()
        )
        return replace(pattern, params)
    }

    fun defaultEmojis(): MutableList<OpenEmoji> {
        val result = mutableListOf<OpenEmoji>()
        javaClass.getResourceAsStream("/${EMOJI_FILE_NAME}").use { inputStream ->
            if (inputStream != null) {
                val text = inputStream.bufferedReader().readText()
                Gson().fromJson(text, OpenEmojiList::class.java).also {
                    it.emojis.forEach(result::add)
                }
            }
        }
        return result
    }

    fun customEmojis(directory: String?): MutableList<OpenEmoji> {
        val result = mutableListOf<OpenEmoji>()
        if (directory == null || directory.trim().isEmpty()) return result
        val file = File("${directory}/$EMOJI_FILE_NAME")
        if (file.exists()) {
            file.inputStream().use { inputStream ->
                val text = inputStream.bufferedReader().readText()
                Gson().fromJson(text, OpenEmojiList::class.java).also { gmList ->
                    if (gmList?.emojis != null) {
                        gmList.emojis.forEach { gm ->
                            gm.custom()
                            result.add(gm)
                        }
                    }
                }
            }
        }
        return result
    }

    fun emojiInfoListWithCustom(
            directory: String?,
            emojiInfoList: MutableList<OpenEmojiInfo>
    ): MutableList<OpenEmojiInfo> = emojiInfoListWithCustom(customEmojiInfoList(directory), emojiInfoList)

    fun emojiInfoListWithCustom(
            customEmojiInfoList: MutableList<OpenEmojiInfo>?,
            emojiInfoList: MutableList<OpenEmojiInfo>
    ): MutableList<OpenEmojiInfo> {
        customEmojiInfoList?.forEach {
            val index = emojiInfoList.indexOf(it)
            if (index < 0) {
                it.type = OpenEmojiInfoType.CUSTOM
                emojiInfoList.add(it)
            } else {
                val oldEmojiInfo = emojiInfoList[index]
                if (oldEmojiInfo.type == OpenEmojiInfoType.DEFAULT) {
                    it.type = OpenEmojiInfoType.OVERRIDE
                    it.enable = oldEmojiInfo.enable
                    it.name = oldEmojiInfo.name
                    it.entity = oldEmojiInfo.entity
                    it.description = oldEmojiInfo.description
                    it.cnDescription = oldEmojiInfo.cnDescription

                    emojiInfoList[index] = it
                }
            }
        }
        return emojiInfoList
    }

    fun defaultEmojiInfoList(): MutableList<OpenEmojiInfo> {
        return convert(defaultEmojis())
    }

    fun customEmojiInfoList(directory: String?): MutableList<OpenEmojiInfo> {
        return convert(customEmojis(directory))
    }

    fun emojiInfoList(directory: String?): MutableList<OpenEmojiInfo> {
        return emojiInfoListWithCustom(customEmojiInfoList(directory), defaultEmojiInfoList())
    }

    fun convert(dataList: Collection<OpenEmoji>?): MutableList<OpenEmojiInfo> {
        val result = mutableListOf<OpenEmojiInfo>()
        if (dataList.isNullOrEmpty()) return result
        dataList.forEach { result.add(OpenEmojiInfo(it)) }
        return result
    }

    fun getIcon(code: String, isCustom: Boolean): Icon {
        val name = getIconName(code)
        val iconPath = getIconPath(name, isCustom)
        return if (isCustom) getCustomIcon(iconPath) else
            IconLoader.getIcon(iconPath, OpenEmoji::class.java)
    }

    fun getIconPath(code: String, isCustom: Boolean): String {
        val name = getIconName(code)
        return if (isCustom) "${
            OpenEmojiPersistent.getInstance().getCustomEmojiDirectory()
        }/icons/${name}.png" else "/icons/emojis/${name}.png"
    }

    fun getIconName(code: String): String {
        return code.replace(":".toRegex(), "")
    }

    fun addDocListener(doc: JTextField, method: Consumer<String>) {
        doc.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                method.accept(doc.text)
            }

            override fun removeUpdate(e: DocumentEvent?) {
                method.accept(doc.text)
            }

            override fun changedUpdate(e: DocumentEvent?) {
                method.accept(doc.text)
            }
        })
    }

    fun copyToClipboard(text: String) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
    }

    private fun getCustomIcon(filePath: String): Icon {
        return try {
            if (!File(filePath).exists()) return AllIcons.Actions.Refresh
            return ImageIcon(filePath)
        } catch (e: Exception) {
            AllIcons.Actions.Refresh
        }
    }

    private fun replace(script: String, replace: Map<String, String>): String {
        var r = script
        for (pattern in PATTERNS) {
            r = r.replace(pattern, replace[pattern] ?: pattern)
        }
        return r
    }

    private fun date(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return current.format(formatter)
    }

    private fun time(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return current.format(formatter)
    }

    class OpenEmojiPatternListTypeToken : TypeToken<MutableList<OpenEmojiPattern>>()
    class OpenEmojiInfoListTypeToken : TypeToken<MutableList<OpenEmojiInfo>>()

}