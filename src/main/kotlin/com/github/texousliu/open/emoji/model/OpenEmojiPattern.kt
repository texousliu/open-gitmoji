package com.github.texousliu.open.emoji.model

class OpenEmojiPattern(var pattern: String = "",
                       // 全局开关
                       var enable: Boolean = true,
                       // 对代码提交生效
                       var enableCommit: Boolean = true,
                       // 对编辑器内生效
                       var enableEditor: Boolean = false) : Cloneable {

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
        return "{\"pattern\":\"${this.pattern}\"," +
                "\"enable\":\"${this.enable}\"," +
                "\"enableCommit\":\"${this.enableCommit}\"," +
                "\"enableEditor\":\"${this.enableEditor}\"}"
    }

    public override fun clone(): OpenEmojiPattern {
        return OpenEmojiPattern(pattern, enable, enableCommit, enableEditor)
    }

}