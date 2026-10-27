package com.game.dungeon.data.models

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyOverflowTest {

    @Test
    fun testSaturatingAddPositiveOverflow() {
        val max = Long.MAX_VALUE
        assertEquals(Long.MAX_VALUE, max.saturatingAdd(100L))
        assertEquals(Long.MAX_VALUE, (max - 10L).saturatingAdd(20L))
        assertEquals(100L, 50L.saturatingAdd(50L))
    }

    @Test
    fun testSaturatingAddNegative() {
        val val1 = 100L
        assertEquals(80L, val1.saturatingAdd(-20L))
        assertEquals(0L, val1.saturatingAdd(-150L))
    }

    @Test
    fun testSanitizeCorruptedGameState() {
        val corrupted = GameState(
            gold = -100L,
            magicite = -500L,
            totalGilEarned = -1000L,
            totalMagiciteEarned = -5000L,
            gilEarnedThisDim = -200L,
            magiciteEarnedThisDim = -300L
        )

        val sanitized = corrupted.sanitize()

        assertEquals(Long.MAX_VALUE, sanitized.gold)
        assertEquals(Long.MAX_VALUE, sanitized.magicite)
        assertEquals(Long.MAX_VALUE, sanitized.totalGilEarned)
        assertEquals(Long.MAX_VALUE, sanitized.totalMagiciteEarned)
        assertEquals(Long.MAX_VALUE, sanitized.gilEarnedThisDim)
        assertEquals(Long.MAX_VALUE, sanitized.magiciteEarnedThisDim)
    }

    @Test
    fun testSanitizeValidGameState() {
        val valid = GameState(
            gold = 5000L,
            magicite = 100L,
            totalGilEarned = 10000L,
            totalMagiciteEarned = 500L,
            gilEarnedThisDim = 2000L,
            magiciteEarnedThisDim = 300L
        )

        val sanitized = valid.sanitize()

        assertEquals(valid, sanitized)
    }
}
