package com.game.dungeon.data.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MythicSetTest {

    @Test
    fun testMythicSet_fromId() {
        val crystalSet = MythicSet.fromId("set_crystal")
        assertNotNull(crystalSet)
        assertEquals(MythicSet.CRYSTAL, crystalSet)
        assertEquals(HeroClass.KNIGHT, crystalSet?.targetClass)
        assertEquals(3, crystalSet?.requiredPieces)
        assertEquals(0.4f, crystalSet?.statMultipliers?.get("DEF_MULT"))
        assertEquals(0.25f, crystalSet?.statMultipliers?.get("HP_MULT"))

        val unknownSet = MythicSet.fromId("non_existent_set")
        assertNull(unknownSet)

        val nullSet = MythicSet.fromId(null)
        assertNull(nullSet)
    }

    @Test
    fun testAll12MythicSetsExist() {
        assertEquals(12, MythicSet.entries.size)
        for (set in MythicSet.entries) {
            val retrieved = MythicSet.fromId(set.id)
            assertEquals(set, retrieved)
            assertNotNull(set.targetClass)
            assertEquals(3, set.requiredPieces)
        }
    }

    @Test
    fun testItem_mythicRarityAndSetId() {
        val item = Item(
            id = "mythic_item_1",
            name = "Crystal Aegis Shield",
            slot = ItemSlot.SHIELD,
            rarity = Rarity.MYTHIC,
            defenseBonus = 100,
            emoji = "🛡️",
            floorFound = 350,
            setId = "set_crystal"
        )

        assertEquals(Rarity.MYTHIC, item.rarity)
        assertEquals(0xFFFF2255, item.rarity.color)
        assertEquals("set_crystal", item.setId)
    }
}
