package com.game.dungeon.data.models

import org.junit.Test
import org.junit.Assert.assertEquals

class MasteryTest {

    @Test
    fun testAddJobExp() {
        val gs = GameState()
        val heroClass = HeroClass.WARRIOR
        
        // Initial state
        assertEquals(0, gs.getMasteryLevel(heroClass))
        assertEquals(0, gs.getMasteryExp(heroClass))
        
        // Add some exp
        var updatedGs = gs.addJobExp(heroClass, 50)
        assertEquals(0, updatedGs.getMasteryLevel(heroClass))
        assertEquals(50, updatedGs.getMasteryExp(heroClass))
        
        // Level up (Level 1 requires (0+1)*100 = 100 EXP)
        updatedGs = updatedGs.addJobExp(heroClass, 50)
        assertEquals(1, updatedGs.getMasteryLevel(heroClass))
        assertEquals(0, updatedGs.getMasteryExp(heroClass))
        
        // Level 2 requires (1+1)*100 = 200 EXP
        updatedGs = updatedGs.addJobExp(heroClass, 250)
        assertEquals(2, updatedGs.getMasteryLevel(heroClass))
        assertEquals(50, updatedGs.getMasteryExp(heroClass))
    }

    @Test
    fun testMasteryBonusCalculation() {
        val gs = GameState(
            jobMasteryLevels = mapOf(HeroClass.WARRIOR to 5)
        )
        val warrior = HeroClass.WARRIOR // Attack bonus 2 per level
        
        assertEquals(5, gs.getMasteryLevel(warrior))
        assertEquals(10, gs.getMasteryBonus(warrior))
    }
}
