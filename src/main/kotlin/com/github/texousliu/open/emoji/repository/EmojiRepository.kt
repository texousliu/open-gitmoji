package com.github.texousliu.open.emoji.repository

import com.github.texousliu.open.emoji.model.OpenEmoji

/**
 * Abstract source of emoji definitions.
 *
 * Implementations load emojis from different origins (built-in resources,
 * local file system, remote URL). A [CompositeEmojiRepository] merges
 * multiple sources by priority.
 */
interface EmojiRepository {

    /** Human-readable name of this source. */
    val name: String

    /** Loading priority — higher values win when the same [code] exists in multiple sources. */
    val priority: Int

    /** Loads all available emojis from this source. */
    fun load(): List<OpenEmoji>

    /** Returns true if this source is currently available (e.g. file exists, URL reachable). */
    fun isAvailable(): Boolean
}
