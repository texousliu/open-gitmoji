package com.github.texousliu.open.emoji.repository

import com.github.texousliu.open.emoji.model.OpenEmoji
import com.github.texousliu.open.emoji.model.OpenEmojiData
import com.github.texousliu.open.emoji.model.EmojiSource
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class CompositeEmojiRepositoryTest {

    private fun emojiData(code: String, name: String) = OpenEmojiData(
        emoji = "🔧",
        entity = "",
        code = code,
        name = name,
        description = "Test emoji $name",
        cnDescription = "测试 $name"
    )

    @Test
    fun shouldMergeDifferentCodes() {
        val repoA = FakeEmojiRepository(
            listOf(OpenEmoji(emojiData(":art:", "art"), EmojiSource.DEFAULT)),
            priority = 10
        )
        val repoB = FakeEmojiRepository(
            listOf(OpenEmoji(emojiData(":zap:", "zap"), EmojiSource.CUSTOM)),
            priority = 30
        )

        val composite = CompositeEmojiRepository(listOf(repoA, repoB))
        val result = composite.load()

        assertEquals(2, result.size)
        assertTrue(result.any { it.code == ":art:" })
        assertTrue(result.any { it.code == ":zap:" })
    }

    @Test
    fun shouldOverrideDefaultWithCustom() {
        val default = OpenEmoji(emojiData(":art:", "art-default"), EmojiSource.DEFAULT)
        val custom = OpenEmoji(emojiData(":art:", "art-custom"), EmojiSource.CUSTOM)

        val repoDefault = FakeEmojiRepository(listOf(default), priority = 10)
        val repoCustom = FakeEmojiRepository(listOf(custom), priority = 30)

        val composite = CompositeEmojiRepository(listOf(repoDefault, repoCustom))
        val result = composite.load()

        assertEquals(1, result.size)
        val winner = result.first()
        assertEquals(":art:", winner.code)
        assertEquals("art-custom", winner.name)
        assertEquals(EmojiSource.OVERRIDE, winner.source)
    }

    @Test
    fun shouldSkipUnavailableRepositories() {
        val available = FakeEmojiRepository(
            listOf(OpenEmoji(emojiData(":art:", "art"), EmojiSource.DEFAULT)),
            available = true
        )
        val unavailable = FakeEmojiRepository(emptyList(), available = false)

        val composite = CompositeEmojiRepository(listOf(unavailable, available))
        val result = composite.load()

        assertEquals(1, result.size)
    }

    @Test
    fun shouldReturnEmptyWhenAllUnavailable() {
        val repo = FakeEmojiRepository(emptyList(), available = false)
        val composite = CompositeEmojiRepository(listOf(repo))

        assertTrue(composite.load().isEmpty())
        assertTrue(composite.isAvailable().not())
    }

    private class FakeEmojiRepository(
        private val data: List<OpenEmoji>,
        override val priority: Int = 10,
        private val available: Boolean = true
    ) : EmojiRepository {
        override val name: String = "Fake"
        override fun isAvailable(): Boolean = available
        override fun load(): List<OpenEmoji> = data
    }
}
