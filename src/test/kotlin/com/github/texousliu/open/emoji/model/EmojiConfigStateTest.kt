package com.github.texousliu.open.emoji.model

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class EmojiConfigStateTest {

    private val artData = OpenEmojiData(
        emoji = "🎨",
        entity = "&#x1f3a8;",
        code = ":art:",
        name = "art",
        description = "Improve structure / format of the code.",
        cnDescription = "改进结构和代码格式"
    )

    @Test
    fun shouldHoldEmojiAndMutableState() {
        val emoji = OpenEmoji(artData)
        val state = EmojiConfigState(emoji, enabled = true, dirty = false)

        assertEquals(":art:", state.emoji.code)
        assertTrue(state.enabled)
        assertFalse(state.dirty)
    }

    @Test
    fun shouldDetectModification() {
        val emoji = OpenEmoji(artData)
        val original = EmojiConfigState(emoji, enabled = true, dirty = false)
        val modified = EmojiConfigState(emoji, enabled = false, dirty = true)

        assertTrue(modified.isModifiedFrom(original))
    }

    @Test
    fun shouldNotDetectModificationWhenIdentical() {
        val emoji = OpenEmoji(artData)
        val a = EmojiConfigState(emoji, enabled = true, dirty = false)
        val b = EmojiConfigState(emoji, enabled = true, dirty = false)

        assertFalse(a.isModifiedFrom(b))
    }

    @Test
    fun shouldBeEqualWhenEmojiCodeMatches() {
        val emojiA = OpenEmoji(artData, EmojiSource.DEFAULT)
        val emojiB = OpenEmoji(artData.copy(description = "different"), EmojiSource.CUSTOM)

        val stateA = EmojiConfigState(emojiA, enabled = true)
        val stateB = EmojiConfigState(emojiB, enabled = false)

        assertEquals(stateA, stateB)
    }
}
