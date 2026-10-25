package com.game.dungeon.data.models

import com.game.dungeon.engine.FFBattleEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SafariPhase3Test {

    @Test
    fun testAddMonsterCardToGameState() {
        var gs = GameState()
        assertTrue(gs.tier2Pets.isEmpty())

        gs = gs.addMonsterCard("CHOCOBO")
        assertEquals(1, gs.tier2Pets["CHOCOBO"]?.cardsCollected)
        assertEquals(0, gs.tier2Pets["CHOCOBO"]?.currentRank)

        gs = gs.addMonsterCard("CHOCOBO")
        assertEquals(2, gs.tier2Pets["CHOCOBO"]?.cardsCollected)
    }

    @Test
    fun testMonsterCardDroppedEvent() {
        val event = FFBattleEvent.MonsterCardDropped("CHOCOBO")
        assertEquals("CHOCOBO", event.petId)
    }

    @Test
    fun testTier2PetMultiplierStatScaling() {
        val petRank0 = Tier2Pet(petId = "CHOCOBO", currentRank = 0)
        assertEquals(1.00f, petRank0.getStatMultiplier(), 0.001f)

        val petRank5 = Tier2Pet(petId = "CHOCOBO", currentRank = 5)
        assertEquals(1.05f, petRank5.getStatMultiplier(), 0.001f)
    }
}
