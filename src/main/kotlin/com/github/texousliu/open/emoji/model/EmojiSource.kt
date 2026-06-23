package com.github.texousliu.open.emoji.model

/**
 * Identifies the origin of an emoji definition.
 *
 * - DEFAULT: shipped with the plugin
 * - CUSTOM: loaded from a user-defined local folder
 * - NETWORK: loaded from a remote URL
 * - OVERRIDE: a default emoji whose fields were overwritten by a custom/network one
 */
enum class EmojiSource {
    DEFAULT,
    CUSTOM,
    NETWORK,
    OVERRIDE
}
