package com.github.texousliu.open.emoji.model

/**
 * Domain object representing an emoji with its source information.
 *
 * Does NOT perform icon loading — icon resolution is handled by [IconLoaderService]
 * outside the model layer.
 */
class OpenEmoji(
    val data: OpenEmojiData,
    val source: EmojiSource = EmojiSource.DEFAULT,
    val iconPath: String? = null
) {
    val emoji: String get() = data.emoji
    val entity: String get() = data.entity
    val code: String get() = data.code
    val name: String get() = data.name
    val description: String get() = data.description
    val cnDescription: String get() = data.cnDescription
    val semver: String? get() = data.semver

    /**
     * Returns the icon file name derived from the code (without colons).
     * Example: ":art:" -> "art"
     */
    fun iconName(): String = code.replace(":", "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OpenEmoji) return false
        return this.code == other.code
    }

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String {
        return "OpenEmoji(code=$code, emoji=$emoji, source=$source)"
    }
}
