package com.game.dungeon.data.models

import org.junit.Assert.*
import org.junit.Test

class SafariPhase5Test {

    @Test
    fun testSafariUnlockCondition() {
        val lockedState = GameState(currentDimension = 10, lifetimeHighestFloor = 999)
        assertFalse(lockedState.isSafariUnlocked(ignoreDebugFlag = true))

        val dim11State = GameState(currentDimension = 11, lifetimeHighestFloor = 1000)
        assertTrue(dim11State.isSafariUnlocked(ignoreDebugFlag = true))
    }

    @Test
    fun testTier2PetCardsNeededAndRankUpLogic() {
        val pet0 = Tier2Pet(petId = "CHOCOBO", cardsCollected = 4, currentRank = 0)
        assertEquals(5, pet0.getCardsNeededForNextRank())
        assertFalse(pet0.canRankUp())

        val pet0Ready = Tier2Pet(petId = "CHOCOBO", cardsCollected = 5, currentRank = 0)
        assertTrue(pet0Ready.canRankUp())

        val petRank5 = Tier2Pet(petId = "CHOCOBO", cardsCollected = 100, currentRank = 5)
        assertEquals(0, petRank5.getCardsNeededForNextRank())
        assertFalse(petRank5.canRankUp())
    }

    @Test
    fun testTier2PetStatMultipliers() {
        assertEquals(1.0f, Tier2Pet(petId = "CAT", currentRank = 0).getStatMultiplier(), 0.001f)
        assertEquals(1.01f, Tier2Pet(petId = "CAT", currentRank = 1).getStatMultiplier(), 0.001f)
        assertEquals(1.05f, Tier2Pet(petId = "CAT", currentRank = 5).getStatMultiplier(), 0.001f)
    }

    @Test
    fun testGameStateAddMonsterCard() {
        var gs = GameState()
        assertEquals(0, gs.tier2Pets["CHOCOBO"]?.cardsCollected ?: 0)

        gs = gs.addMonsterCard("CHOCOBO")
        assertEquals(1, gs.tier2Pets["CHOCOBO"]?.cardsCollected ?: 0)

        gs = gs.addMonsterCard("CHOCOBO")
        assertEquals(2, gs.tier2Pets["CHOCOBO"]?.cardsCollected ?: 0)
    }
}
