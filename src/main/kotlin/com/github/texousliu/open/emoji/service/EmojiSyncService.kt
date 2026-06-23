package com.github.texousliu.open.emoji.service

import com.github.texousliu.open.emoji.model.OpenEmoji
import com.github.texousliu.open.emoji.model.OpenEmojiData
import com.github.texousliu.open.emoji.model.EmojiSource
import com.github.texousliu.open.emoji.repository.BuiltInEmojiRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.util.io.HttpRequests
import java.io.File

/**
 * Service for synchronizing emoji definitions from the gitmoji official repository.
 *
 * Supports manual sync and background checking. Downloads from GitHub Raw,
 * with fallback to mirrors. Preserves user custom emojis.
 */
class EmojiSyncService(private val project: Project) {

    private val logger = Logger.getInstance(EmojiSyncService::class.java)

    companion object {
        const val OFFICIAL_URL = "https://raw.githubusercontent.com/carloscuesta/gitmoji/main/packages/gitmojis/src/gitmojis.json"
        const val JSDELIVR_URL = "https://cdn.jsdelivr.net/gh/carloscuesta/gitmoji@main/packages/gitmojis/src/gitmojis.json"
        const val CACHE_FILE_NAME = "synced_gitmojis.json"
    }

    private val cacheDir: File
        get() {
            val dir = File(System.getProperty("user.home"), ".open-gitmoji/cache")
            if (!dir.exists()) dir.mkdirs()
            return dir
        }

    private val cacheFile: File
        get() = File(cacheDir, CACHE_FILE_NAME)

    private val lastSyncFile: File
        get() = File(cacheDir, "last_sync_time.txt")

    /**
     * Checks whether the official gitmoji repository has newer definitions.
     * Compares downloaded content against the current built-in list.
     */
    fun checkForUpdate(): SyncResult {
        return try {
            val remoteJson = fetchRemoteJson() ?: return SyncResult(false, error = "无法获取远程数据")
            val remoteEmojis = parseEmojis(remoteJson)
            val localEmojis = BuiltInEmojiRepository().load()

            val localCodes = localEmojis.map { it.code }.toSet()
            val remoteCodes = remoteEmojis.map { it.code }.toSet()

            val added = remoteEmojis.filter { it.code !in localCodes }
            val removed = localEmojis.filter { it.code !in remoteCodes }
            val modified = findModified(localEmojis, remoteEmojis)

            SyncResult(
                success = true,
                added = added,
                modified = modified,
                removed = removed
            )
        } catch (e: Exception) {
            logger.error("Check for update failed", e)
            SyncResult(false, error = e.message)
        }
    }

    /**
     * Performs a full sync: downloads remote definitions and saves to cache.
     * Does NOT overwrite user custom emojis.
     */
    fun sync(): SyncResult {
        return try {
            val remoteJson = fetchRemoteJson() ?: return SyncResult(false, error = "无法获取远程数据")
            cacheFile.writeText(remoteJson)
            lastSyncFile.writeText(System.currentTimeMillis().toString())

            val remoteEmojis = parseEmojis(remoteJson)
            val localEmojis = BuiltInEmojiRepository().load()

            val localCodes = localEmojis.map { it.code }.toSet()
            val remoteCodes = remoteEmojis.map { it.code }.toSet()

            val added = remoteEmojis.filter { it.code !in localCodes }
            val removed = localEmojis.filter { it.code !in remoteCodes }
            val modified = findModified(localEmojis, remoteEmojis)

            SyncResult(
                success = true,
                added = added,
                modified = modified,
                removed = removed
            )
        } catch (e: Exception) {
            logger.error("Sync failed", e)
            SyncResult(false, error = e.message)
        }
    }

    /**
     * Returns the timestamp of the last successful sync, or null if never synced.
     */
    fun lastSyncTime(): Long? {
        return if (lastSyncFile.exists()) lastSyncFile.readText().toLongOrNull() else null
    }

    /**
     * Returns the synced emoji list from cache, or empty if no cache exists.
     */
    fun getSyncedEmojis(): List<OpenEmoji> {
        if (!cacheFile.exists()) return emptyList()
        return try {
            parseEmojis(cacheFile.readText())
        } catch (e: Exception) {
            logger.error("Failed to read sync cache", e)
            emptyList()
        }
    }

    private fun fetchRemoteJson(): String? {
        val urls = listOf(OFFICIAL_URL, JSDELIVR_URL)
        for (url in urls) {
            try {
                return HttpRequests.request(url)
                    .connectTimeout(10_000)
                    .readTimeout(10_000)
                    .readString()
            } catch (e: Exception) {
                logger.warn("Failed to fetch from $url", e)
            }
        }
        return null
    }

    private fun parseEmojis(jsonText: String): List<OpenEmoji> {
        val result = mutableListOf<OpenEmoji>()
        val listType = object : TypeToken<Map<String, List<OpenEmojiData>>>() {}.type
        val map: Map<String, List<OpenEmojiData>> = Gson().fromJson(jsonText, listType)
        map["gitmojis"]?.forEach { data ->
            result.add(OpenEmoji(data, EmojiSource.DEFAULT))
        }
        // Fallback: some versions use "emojis" key
        if (result.isEmpty()) {
            map["emojis"]?.forEach { data ->
                result.add(OpenEmoji(data, EmojiSource.DEFAULT))
            }
        }
        return result
    }

    private fun findModified(
        local: List<OpenEmoji>,
        remote: List<OpenEmoji>
    ): List<Pair<OpenEmoji, OpenEmoji>> {
        val remoteMap = remote.associateBy { it.code }
        return local.mapNotNull { l ->
            val r = remoteMap[l.code]
            if (r != null && l.data != r.data) Pair(l, r) else null
        }
    }
}

/**
 * Result of a sync or check operation.
 */
data class SyncResult(
    val success: Boolean,
    val added: List<OpenEmoji> = emptyList(),
    val modified: List<Pair<OpenEmoji, OpenEmoji>> = emptyList(),
    val removed: List<OpenEmoji> = emptyList(),
    val error: String? = null
)
