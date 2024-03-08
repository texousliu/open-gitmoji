package com.github.texousliu.open.emoji.model

import com.google.gson.annotations.SerializedName

open class OpenEmojiBase(
        var emoji: String,
        var entity: String,
        val code: String,
        var name: String,
        var description: String,
        @SerializedName("cn_description")
        var cnDescription: String
) {
    fun toBase(): OpenEmojiBase {
        return OpenEmojiBase(this.emoji, this.entity, this.code, this.name, this.description, this.cnDescription)
    }
}