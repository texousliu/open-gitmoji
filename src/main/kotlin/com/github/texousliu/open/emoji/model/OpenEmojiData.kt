package com.github.texousliu.open.emoji.model

import com.google.gson.annotations.SerializedName

/**
 * Pure data class representing an emoji definition.
 * Immutable — all properties are read-only.
 */
data class OpenEmojiData(
    val emoji: String,
    val entity: String,
    val code: String,
    val name: String,
    val description: String,
    @SerializedName("cn_description")
    val cnDescription: String,
    val semver: String? = null
)
