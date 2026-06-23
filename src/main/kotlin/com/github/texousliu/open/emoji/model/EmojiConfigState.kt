package com.github.texousliu.open.emoji.model

/**
 * UI state wrapper for an emoji entry in the configuration panel.
 *
 * Holds mutable user-facing state (enabled / dirty) separately from
 * the immutable domain object [OpenEmoji].
 */
class EmojiConfigState(
    val emoji: OpenEmoji,
    var enabled: Boolean = true,
    var dirty: Boolean = false
) {

    /**
     * Checks whether this state differs from another in terms of
     * user-modifiable fields.
     */
    fun isModifiedFrom(other: EmojiConfigState): Boolean {
        return this.enabled != other.enabled || this.dirty != other.dirty
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmojiConfigState) return false
        return this.emoji == other.emoji
    }

    override fun hashCode(): Int = emoji.hashCode()

    fun clone(): EmojiConfigState {
        return EmojiConfigState(this.emoji, this.enabled, this.dirty)
    }

    override fun toString(): String {
        return "EmojiConfigState(code=${emoji.code}, enabled=$enabled, dirty=$dirty)"
    }
}
