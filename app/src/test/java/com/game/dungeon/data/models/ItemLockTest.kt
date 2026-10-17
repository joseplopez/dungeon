package com.game.dungeon.data.models

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ItemLockTest {

    @Test
    fun `test item defaults to unlocked`() {
        val item = Item(
            id = "item_1",
            name = "Iron Sword",
            slot = ItemSlot.WEAPON,
            rarity = Rarity.COMMON,
            emoji = "⚔️",
            floorFound = 1
        )
        assertFalse(item.isLocked)
    }

    @Test
    fun `test item copy toggling lock state`() {
        val item = Item(
            id = "item_1",
            name = "Iron Sword",
            slot = ItemSlot.WEAPON,
            rarity = Rarity.COMMON,
            emoji = "⚔️",
            floorFound = 1,
            isLocked = false
        )

        val lockedItem = item.copy(isLocked = true)
        assertTrue(lockedItem.isLocked)

        val unlockedItem = lockedItem.copy(isLocked = false)
        assertFalse(unlockedItem.isLocked)
    }
}
