package com.github.texousliu.open.emoji.utils

import com.github.texousliu.open.emoji.context.OpenEmojiCache
import com.github.texousliu.open.emoji.model.*
import com.github.texousliu.open.emoji.persistence.OpenEmojiInfoSerializer
import com.github.texousliu.open.emoji.persistence.OpenEmojiPersistent
import com.github.texousliu.open.emoji.repository.BuiltInEmojiRepository
import com.github.texousliu.open.emoji.repository.LocalEmojiRepository
import com.github.texousliu.open.emoji.service.IconLoaderService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.function.Consumer
import javax.swing.Icon
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

object OpenEmojiUtils {

    const val REPLACE_SUFFIX_MARK = "$$:$$"

    val GSON = Gson()
    val GSON_INFO: Gson = GsonBuilder()
            .registerTypeAdapter(EmojiConfigState::class.java, OpenEmojiInfoSerializer())
            .create()

    private const val G = "#{G}"
    private const val GU = "#{GU}"
    private const val DESC = "#{DESC}"
    private const val DESC_CN = "#{DESC_CN}"
    private const val DATE = "#{DATE}"
    private const val TIME = "#{TIME}"

    private val PATTERNS = arrayOf(G, GU, DESC, DESC_CN, DATE, TIME)

    fun demo(pattern: String): String {
        val list = OpenEmojiCache.emojiInfoList()
        if (list.isEmpty()) return pattern
        return replace(pattern, list[0].emoji)
    }

    fun replace(pattern: String, emoji: OpenEmoji): String {
        val params = mapOf(
                G to emoji.emoji, GU to emoji.code,
                DESC to emoji.description, DESC_CN to emoji.cnDescription,
                DATE to date(), TIME to time()
        )
        return replace(pattern, params)
    }

    fun defaultEmojis(): List<OpenEmoji> {
        return BuiltInEmojiRepository().load()
    }

    fun customEmojis(directory: String?): List<OpenEmoji> {
        if (directory == null || directory.trim().isEmpty()) return emptyList()
        return LocalEmojiRepository(directory).load()
    }

    fun emojiInfoListWithCustom(
            directory: String?,
            emojiInfoList: MutableList<EmojiConfigState>
    ): MutableList<EmojiConfigState> = emojiInfoListWithCustom(customEmojiInfoList(directory), emojiInfoList)

    fun emojiInfoListWithCustom(
            customEmojiInfoList: List<EmojiConfigState>?,
            emojiInfoList: MutableList<EmojiConfigState>
    ): MutableList<EmojiConfigState> {
        customEmojiInfoList?.forEach { customState ->
            val index = emojiInfoList.indexOf(customState)
            if (index < 0) {
                // 不存在，作为 CUSTOM 新增
                emojiInfoList.add(customState)
            } else {
                val existingState = emojiInfoList[index]
                if (existingState.emoji.source == EmojiSource.DEFAULT) {
                    // 覆盖默认项，标记为 OVERRIDE，保留原有的 enabled 状态
                    val overriddenEmoji = OpenEmoji(
                            customState.emoji.data,
                            EmojiSource.OVERRIDE,
                            customState.emoji.iconPath
                    )
                    emojiInfoList[index] = EmojiConfigState(overriddenEmoji, existingState.enabled, existingState.dirty)
                }
            }
        }
        return emojiInfoList
    }

    fun defaultEmojiInfoList(): MutableList<EmojiConfigState> {
        return convert(defaultEmojis())
    }

    fun customEmojiInfoList(directory: String?): MutableList<EmojiConfigState> {
        return convert(customEmojis(directory))
    }

    fun emojiInfoList(directory: String?): MutableList<EmojiConfigState> {
        return emojiInfoListWithCustom(customEmojiInfoList(directory), defaultEmojiInfoList())
    }

    fun convert(dataList: Collection<OpenEmoji>?): MutableList<EmojiConfigState> {
        val result = mutableListOf<EmojiConfigState>()
        if (dataList.isNullOrEmpty()) return result
        dataList.forEach { result.add(EmojiConfigState(it)) }
        return result
    }

    fun getIcon(emoji: OpenEmoji): Icon {
        return IconLoaderService.getIcon(emoji)
    }

    fun getIconPath(emoji: OpenEmoji): String {
        return IconLoaderService.getIconPath(emoji).replace("\\", "/")
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
    class OpenEmojiInfoListTypeToken : TypeToken<MutableList<EmojiConfigState>>()

}
