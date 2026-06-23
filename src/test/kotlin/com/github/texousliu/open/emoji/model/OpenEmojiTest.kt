package com.github.texousliu.open.emoji.model

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue

class OpenEmojiTest {

    private val artData = OpenEmojiData(
        emoji = "🎨",
        entity = "&#x1f3a8;",
        code = ":art:",
        name = "art",
        description = "Improve structure / format of the code.",
        cnDescription = "改进结构和代码格式"
    )

    private val zapData = OpenEmojiData(
        emoji = "⚡️",
        entity = "&#x26a1;",
        code = ":zap:",
        name = "zap",
        description = "Improve performance.",
        cnDescription = "优化性能",
        semver = "patch"
    )

    @Test
    fun shouldExposeDelegatedProperties() {
        val emoji = OpenEmoji(artData, EmojiSource.DEFAULT)
        assertEquals("🎨", emoji.emoji)
        assertEquals(":art:", emoji.code)
        assertEquals("art", emoji.name)
        assertEquals("改进结构和代码格式", emoji.cnDescription)
        assertEquals(null, emoji.semver)
    }

    @Test
    fun shouldExtractIconNameFromCode() {
        val emoji = OpenEmoji(artData)
        assertEquals("art", emoji.iconName())
    }

    @Test
    fun shouldBeEqualWhenCodeMatches() {
        val a = OpenEmoji(artData, EmojiSource.DEFAULT)
        val b = OpenEmoji(artData.copy(description = "different"), EmojiSource.CUSTOM)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun shouldNotBeEqualWhenCodeDiffers() {
        val a = OpenEmoji(artData)
        val b = OpenEmoji(zapData)
        assertNotEquals(a, b)
    }

    @Test
    fun shouldDefaultToDefaultSource() {
        val emoji = OpenEmoji(artData)
        assertEquals(EmojiSource.DEFAULT, emoji.source)
    }

    @Test
    fun shouldPreserveCustomSourceAndIconPath() {
        val emoji = OpenEmoji(artData, EmojiSource.CUSTOM, "/custom/icons/art.png")
        assertEquals(EmojiSource.CUSTOM, emoji.source)
        assertEquals("/custom/icons/art.png", emoji.iconPath)
    }
}
