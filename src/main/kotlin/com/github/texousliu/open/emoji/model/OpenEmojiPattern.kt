package com.github.texousliu.open.emoji.model

/**
 * Defines a template pattern for rendering an emoji in the completion list.
 *
 * Supports per-scene enablement: global, commit message, editor.
 */
class OpenEmojiPattern(
    var pattern: String = "",
    var enable: Boolean = true,
    var enableCommit: Boolean = true,
    var enableEditor: Boolean = false
) : Cloneable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is OpenEmojiPattern) {
            return this.pattern == other.pattern
        }
        return false
    }

    override fun hashCode(): Int = pattern.hashCode()

    override fun toString(): String {
        return "{\"pattern\":\"${pattern}\"," +
                "\"enable\":\"$enable\"," +
                "\"enableCommit\":\"$enableCommit\"," +
                "\"enableEditor\":\"$enableEditor\"}"
    }

    public override fun clone(): OpenEmojiPattern {
        return OpenEmojiPattern(pattern, enable, enableCommit, enableEditor)
    }
}
