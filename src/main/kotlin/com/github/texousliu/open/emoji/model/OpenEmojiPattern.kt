package com.github.texousliu.open.emoji.model

class OpenEmojiPattern(var pattern: String = "", var enable: Boolean = true) : Cloneable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is OpenEmojiPattern) {
            return this.pattern == other.pattern
        }
        return false
    }

    override fun hashCode(): Int {
        return this.pattern.hashCode()
    }

    override fun toString(): String {
        return "{\"pattern\":\"${this.pattern}\",\"enable\":\"${this.enable}\"}"
    }

    public override fun clone(): OpenEmojiPattern {
        return OpenEmojiPattern(pattern, enable)
    }

}