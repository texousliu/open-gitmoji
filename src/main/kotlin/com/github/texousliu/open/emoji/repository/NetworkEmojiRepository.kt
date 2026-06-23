package com.github.texousliu.open.emoji.repository

import com.github.texousliu.open.emoji.model.OpenEmoji
import com.github.texousliu.open.emoji.model.OpenEmojiData
import com.github.texousliu.open.emoji.model.EmojiSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.io.HttpRequests
import java.io.File
import java.nio.file.Path

/**
 * Loads emoji definitions from a remote URL.
 *
 * Downloads `emojis.json` and caches it locally. Icons are expected to be
 * resolvable relative to the JSON URL or provided as absolute URLs.
 */
class NetworkEmojiRepository(
    private val url: String,
    private val cacheDir: Path
) : EmojiRepository {

    private val logger = Logger.getInstance(NetworkEmojiRepository::class.java)

    override val name: String = "Network: $url"
    override val priority: Int = 20

    private val cacheFile: File
        get() = cacheDir.resolve("network_emojis.json").toFile()

    override fun isAvailable(): Boolean {
        if (url.isBlank()) return false
        return cacheFile.exists() || canReachUrl()
    }

    override fun load(): List<OpenEmoji> {
        val jsonText = fetchJson() ?: return emptyList()
        return parseEmojis(jsonText)
    }

    /** Attempts to download fresh JSON; falls back to cache on failure. */
    private fun fetchJson(): String? {
        return try {
            val text = HttpRequests.request(url)
                .connectTimeout(10_000)
                .readTimeout(10_000)
                .readString()
            cacheFile.parentFile?.mkdirs()
            cacheFile.writeText(text)
            text
        } catch (e: Exception) {
            logger.warn("Failed to fetch emoji from $url, falling back to cache", e)
            if (cacheFile.exists()) cacheFile.readText() else null
        }
    }

    private fun parseEmojis(jsonText: String): List<OpenEmoji> {
        val result = mutableListOf<OpenEmoji>()
        return try {
            val listType = object : TypeToken<Map<String, List<OpenEmojiData>>>() {}.type
            val map: Map<String, List<OpenEmojiData>> = Gson().fromJson(jsonText, listType)
            map["emojis"]?.forEach { data ->
                result.add(OpenEmoji(data, EmojiSource.NETWORK))
            }
            result
        } catch (e: Exception) {
            logger.error("Failed to parse emoji JSON", e)
            emptyList()
        }
    }

    private fun canReachUrl(): Boolean {
        return try {
            HttpRequests.request(url).connectTimeout(5_000).tryConnect() != -1
        } catch (e: Exception) {
            false
        }
    }
}
