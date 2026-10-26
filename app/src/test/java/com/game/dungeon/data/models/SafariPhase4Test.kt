package com.game.dungeon.data.models

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock

class SafariPhase4Test {

    private val mockContext: Context = mock(Context::class.java)

    @Test
    fun testSafariDimension_RosterAndEnemies() {
        val safariDim = FFDimensionData.safariDimension
        assertEquals(99, safariDim.number)

        val retrievedDim = FFDimensionData.getDimension(99)
        assertEquals(99, retrievedDim.number)

        val templates = FFDimensionData.getEnemiesForFloor(safariDim, 1)
        assertEquals(5, templates.size)

        val monsterTypes = templates.map { it.type }.toSet()
        assertTrue(monsterTypes.contains(MonsterType.CHOCOBO))
        assertTrue(monsterTypes.contains(MonsterType.MOOGLE))
        assertTrue(monsterTypes.contains(MonsterType.CAT))
        assertTrue(monsterTypes.contains(MonsterType.CACTUAR))
        assertTrue(monsterTypes.contains(MonsterType.TONBERRY))
    }

    @Test
    fun testSafariDifficultyScaling_LinearBypass() {
        val template = FFEnemyTemplate(MonsterType.CHOCOBO, 0, "🐤", 1, 999999, gilReward = 15)
        val safariDim = FFDimensionData.safariDimension

        val enemyFloor1 = Enemy.fromTemplate(
            template = template,
            floor = 1,
            context = mockContext,
            dimension = safariDim,
            mode = DungeonMode.SAFARI
        )

        val enemyFloor100 = Enemy.fromTemplate(
            template = template,
            floor = 100,
            context = mockContext,
            dimension = safariDim,
            mode = DungeonMode.SAFARI
        )

        // Floor 1 SAFARI stats
        assertEquals(175_000L, enemyFloor1.maxHp)
        assertEquals(16_500L, enemyFloor1.attack)

        // Floor 100 SAFARI stats (linear curve: 150k + 100 * 25k = 2.65M HP, 15k + 100 * 1.5k = 165k ATK)
        assertEquals(2_650_000L, enemyFloor100.maxHp)
        assertEquals(165_000L, enemyFloor100.attack)

        // Verify stats progression from Floor 1 to Floor 100 in Safari Mode
        assertTrue(enemyFloor100.maxHp > enemyFloor1.maxHp)
        assertTrue(enemyFloor100.attack > enemyFloor1.attack)
    }

    @Test
    fun testPetMonsterTypesInEnum() {
        assertNotNull(MonsterType.valueOf("CHOCOBO"))
        assertNotNull(MonsterType.valueOf("MOOGLE"))
        assertNotNull(MonsterType.valueOf("CAT"))
        assertNotNull(MonsterType.valueOf("CACTUAR"))
        assertNotNull(MonsterType.valueOf("TONBERRY"))
    }
}
