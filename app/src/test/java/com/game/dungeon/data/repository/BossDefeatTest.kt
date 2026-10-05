package com.game.dungeon.data.repository

import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.models.RelicBonuses
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BossDefeatTest {

    @Test
    fun testSameBossKilledMultipleTimesIncreasesDefeatedCount() {
        val initialState = GameState()
        assertEquals(0, initialState.bossesDefeatedNames.size)

        // Simulate killing the same boss ("Garland") 5 times with unique timestamp/uuid entries
        var currentState = initialState
        repeat(5) { index ->
            val entry = "Garland_${System.currentTimeMillis()}_${index}"
            currentState = currentState.copy(bossesDefeatedNames = currentState.bossesDefeatedNames + entry)
        }

        assertEquals("Killing the same boss 5 times must result in size 5", 5, currentState.bossesDefeatedNames.size)
    }

    @Test
    fun testRelicBonusesUsesBossesDefeatedCount() {
        val bosses = (1..100).map { "Boss_$it" }.toSet()
        val gameState = GameState(bossesDefeatedNames = bosses)

        val relicBonuses = RelicBonuses.from(gameState)
        assertEquals(100, relicBonuses.bossesDefeatedCount)
    }

    @Test
    fun testBlueMageJobDiscoveryAt100Bosses() {
        val bosses99 = (1..99).map { "Boss_$it" }.toSet()
        val stateUnder100 = GameState(bossesDefeatedNames = bosses99)
        assertFalse("Blue Mage should not be discovered with under 100 boss defeats", stateUnder100.isJobDiscovered(HeroClass.BLUE_MAGE))

        val bosses100 = (1..100).map { "Boss_$it" }.toSet()
        val stateWith100 = GameState(bossesDefeatedNames = bosses100)
        assertTrue("Blue Mage should be discovered with 100 or more boss defeats", stateWith100.isJobDiscovered(HeroClass.BLUE_MAGE))
    }
}
