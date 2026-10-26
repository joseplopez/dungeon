package com.game.dungeon.data.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SafariPhase1Test {

    @Test
    fun testTier2Pet_statMultiplier() {
        val rank0Pet = Tier2Pet(petId = "CHOCOBO_T2", currentRank = 0)
        assertEquals(1.00f, rank0Pet.getStatMultiplier(), 0.001f)

        val rank5Pet = Tier2Pet(petId = "CHOCOBO_T2", currentRank = 5)
        assertEquals(1.05f, rank5Pet.getStatMultiplier(), 0.001f)
    }

    @Test
    fun testGameState_safariDefaults() {
        val gs = GameState(safariTickets = 0)
        assertFalse(gs.hasSeenSafariUnlockPopup)
        assertEquals(0, gs.safariTickets)
        assertTrue(gs.tier2Pets.isEmpty())
        assertNull(gs.equippedTier2PetId)
    }

    @Test
    fun testIsSafariUnlocked() {
        var gs = GameState(currentDimension = 10, lifetimeHighestFloor = 1000)
        assertFalse(gs.isSafariUnlocked(ignoreDebugFlag = true))

        gs = GameState(currentDimension = 11, lifetimeHighestFloor = 999)
        assertFalse(gs.isSafariUnlocked(ignoreDebugFlag = true))

        gs = GameState(currentDimension = 11, lifetimeHighestFloor = 1000)
        assertTrue(gs.isSafariUnlocked(ignoreDebugFlag = true))

        gs = GameState(currentDimension = 12, lifetimeHighestFloor = 1500)
        assertTrue(gs.isSafariUnlocked(ignoreDebugFlag = true))
    }

    @Test
    fun testPetLevelCappedAt50() {
        var gs = GameState()
        
        // Add huge amount of exp to chocobo
        gs = gs.addPetExp(PetType.CHOCOBO, 1_000_000)
        
        assertEquals(50, gs.getPetLevel(PetType.CHOCOBO))
        assertEquals(0, gs.getPetExp(PetType.CHOCOBO))

        // Ensure adding further exp does nothing
        val nextGs = gs.addPetExp(PetType.CHOCOBO, 500)
        assertEquals(50, nextGs.getPetLevel(PetType.CHOCOBO))
        assertEquals(0, nextGs.getPetExp(PetType.CHOCOBO))
    }
}
