package com.github.texousliu.opengitmoji.model

class RegexTableRowInfo(val regex : String, val enable : Boolean) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is RegexTableRowInfo) {
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

}