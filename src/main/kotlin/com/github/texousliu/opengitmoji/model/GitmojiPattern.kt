package com.github.texousliu.opengitmoji.model

class GitmojiPattern(var regex : String, var enable : Boolean) : Cloneable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is GitmojiPattern) {
            return this.regex == other.regex
        }
        return false
    }

    override fun hashCode(): Int {
        return this.regex.hashCode()
    }

    override fun toString(): String {
        return "{\"regex\":\"${this.regex}\",\"enable\":\"${this.enable}\"}"
    }

    public override fun clone(): GitmojiPattern {
        return GitmojiPattern(regex, enable)
    }

}