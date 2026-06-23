package com.github.texousliu.open.emoji.service

import com.github.texousliu.open.emoji.model.OpenEmoji
import com.github.texousliu.open.emoji.model.EmojiSource
import com.github.texousliu.open.emoji.repository.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Central service for managing emoji repositories and loading merged emoji lists.
 *
 * Combines built-in, local, and network sources into a unified view,
 * respecting source priorities and user configuration.
 */
@Service(Service.Level.PROJECT)
class EmojiRepositoryService(private val project: Project) {

    private var compositeRepository: CompositeEmojiRepository? = null
    private var lastLoadedEmojis: List<OpenEmoji> = emptyList()

    /**
     * Loads the full merged emoji list from all configured sources.
     *
     * @param customDirectory local folder path (may be empty)
     * @param networkUrl remote URL (may be empty)
     * @param networkEnabled whether network source is enabled
     */
    fun loadEmojis(
        customDirectory: String = "",
        networkUrl: String = "",
        networkEnabled: Boolean = false
    ): List<OpenEmoji> {
        val repos = mutableListOf<EmojiRepository>(BuiltInEmojiRepository())

        if (customDirectory.isNotBlank()) {
            repos.add(LocalEmojiRepository(customDirectory))
        }

        if (networkEnabled && networkUrl.isNotBlank()) {
            val cacheDir = getPluginCacheDir()
            repos.add(NetworkEmojiRepository(networkUrl, cacheDir))
        }

        compositeRepository = CompositeEmojiRepository(repos)
        lastLoadedEmojis = compositeRepository!!.load()
        return lastLoadedEmojis
    }

    /**
     * Returns the last loaded emoji list without re-fetching.
     */
    fun getCachedEmojis(): List<OpenEmoji> = lastLoadedEmojis

    /**
     * Filters emojis by source type.
     */
    fun getEmojisBySource(source: EmojiSource): List<OpenEmoji> {
        return lastLoadedEmojis.filter { it.source == source }
    }

    /**
     * Searches emojis by code, name, description, or cnDescription.
     */
    fun searchEmojis(query: String): List<OpenEmoji> {
        if (query.isBlank()) return lastLoadedEmojis
        val lower = query.lowercase()
        return lastLoadedEmojis.filter {
            it.code.lowercase().contains(lower) ||
                    it.name.lowercase().contains(lower) ||
                    it.description.lowercase().contains(lower) ||
                    it.cnDescription.lowercase().contains(lower)
        }
    }

    /**
     * Clears the network cache to force a re-download on next load.
     */
    fun clearNetworkCache() {
        val cacheFile = getPluginCacheDir().resolve("network_emojis.json").toFile()
        if (cacheFile.exists()) cacheFile.delete()
    }

    private fun getPluginCacheDir(): Path {
        val base = System.getProperty("user.home")
        return Paths.get(base, ".open-gitmoji", "cache")
    }
}
