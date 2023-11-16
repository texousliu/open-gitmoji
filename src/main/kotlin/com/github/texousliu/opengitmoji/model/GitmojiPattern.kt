package com.github.texousliu.opengitmoji.model

class GitmojiPattern(var pattern : String, var enable : Boolean) : Cloneable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is GitmojiPattern) {
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

    public override fun clone(): GitmojiPattern {
        return GitmojiPattern(pattern, enable)
    }

}