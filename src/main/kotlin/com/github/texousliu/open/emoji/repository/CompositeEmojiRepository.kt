package com.github.texousliu.open.emoji.repository

import com.github.texousliu.open.emoji.model.OpenEmoji
import com.github.texousliu.open.emoji.model.EmojiSource

/**
 * Merges multiple [EmojiRepository] sources into a single unified list.
 *
 * Merge rules:
 * - Emojis with different [code] values are all retained.
 * - When the same [code] appears in multiple sources, the one from the
 *   source with higher [priority] wins.
 * - If a higher-priority source overrides a DEFAULT emoji, the result
 *   is marked as [EmojiSource.OVERRIDE].
 */
class CompositeEmojiRepository(
    private val repositories: List<EmojiRepository>
) : EmojiRepository {

    override val name: String = "Composite"
    override val priority: Int = 0

    override fun isAvailable(): Boolean = repositories.any { it.isAvailable() }

    override fun load(): List<OpenEmoji> {
        val merged = mutableMapOf<String, OpenEmoji>()

        // Sort by priority ascending so higher priority overwrites lower
        repositories.sortedBy { it.priority }.forEach { repo ->
            if (!repo.isAvailable()) return@forEach
            repo.load().forEach { emoji ->
                val existing = merged[emoji.code]
                if (existing == null) {
                    merged[emoji.code] = emoji
                } else {
                    // Higher priority wins; if overriding a DEFAULT, mark as OVERRIDE
                    val resolvedSource = when {
                        existing.source == EmojiSource.DEFAULT && emoji.source != EmojiSource.DEFAULT ->
                            EmojiSource.OVERRIDE
                        else -> emoji.source
                    }
                    merged[emoji.code] = OpenEmoji(emoji.data, resolvedSource, emoji.iconPath)
                }
            }
        }

        return merged.values.toList()
    }
}
