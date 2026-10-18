package com.game.dungeon.data.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ItemCraftingTest {

    private fun createBaseItem(
        attackBonus: Long = 100L,
        defenseBonus: Long = 50L,
        magicBonus: Long = 20L,
        hpBonus: Long = 100L,
        mpBonus: Long = 10L,
        critChanceBonus: Int = 5,
        critDamageBonus: Long = 20L,
        enhancementLevel: Int = 0,
        sockets: List<SocketSlot> = emptyList(),
    ): Item {
        return Item(
            id = "test_item_1",
            name = "Test Sword",
            slot = ItemSlot.WEAPON,
            rarity = Rarity.RARE,
            attackBonus = attackBonus,
            defenseBonus = defenseBonus,
            magicBonus = magicBonus,
            hpBonus = hpBonus,
            mpBonus = mpBonus,
            critChanceBonus = critChanceBonus,
            critDamageBonus = critDamageBonus,
            emoji = "⚔️",
            floorFound = 5,
            enhancementLevel = enhancementLevel,
            sockets = sockets,
        )
    }

    @Test
    fun `test base power score calculation at enhancement level 0`() {
        // basePowerScore = attackBonus(100) + defenseBonus(50) + magicBonus(20) + (hpBonus/5 = 20) + (mpBonus/2 = 5) + (critChance*2 = 10) + (critDamage/2 = 10) = 215
        val item = createBaseItem()

        assertEquals(215L, item.basePowerScore)
        assertEquals(215.0, item.powerScore, 0.001)
    }

    @Test
    fun `test enhancement level scaling up to level 10`() {
        // Item with base power score = 100 (attackBonus = 100, all other stats = 0)
        val item = Item(
            id = "test_item_scaling",
            name = "Simple Blade",
            slot = ItemSlot.WEAPON,
            rarity = Rarity.COMMON,
            attackBonus = 100,
            emoji = "⚔️",
            floorFound = 1,
        )

        assertEquals(100L, item.basePowerScore)

        // Key milestones check (+0%, +25%, +50%)
        assertEquals(100.0, item.copy(enhancementLevel = 0).powerScore, 0.001)
        assertEquals(125.0, item.copy(enhancementLevel = 5).powerScore, 0.001)
        assertEquals(150.0, item.copy(enhancementLevel = 10).powerScore, 0.001)

        // Verify all enhancement levels 0..10 scale according to (base * (1.0 + level * 0.05))
        for (level in 0..10) {
            val enhancedItem = item.copy(enhancementLevel = level)
            val expectedPower = item.basePowerScore * (1.0 + (level * 0.05))
            assertEquals("PowerScore mismatch at enhancement level $level", expectedPower, enhancedItem.powerScore, 0.001)
        }
    }

    @Test
    fun `test enhancement level safely coerced when below 0 or above 10`() {
        val item = Item(
            id = "test_coercion",
            name = "Coerced Item",
            slot = ItemSlot.ARMOR,
            rarity = Rarity.RARE,
            attackBonus = 100,
            defenseBonus = 100,
            emoji = "🛡️",
            floorFound = 10,
        )

        // basePowerScore = 100 + 100 = 200

        // Below 0: enhancementLevel = -5 should be coerced to 0 (multiplier 1.0)
        val negativeEnhanced = item.copy(enhancementLevel = -5)
        assertEquals(200.0, negativeEnhanced.powerScore, 0.001)
        assertEquals(100.0, negativeEnhanced.effectiveAttackBonus, 0.001)
        assertEquals(100.0, negativeEnhanced.effectiveDefenseBonus, 0.001)

        // Above 10: enhancementLevel = 15 should be coerced to 10 (multiplier 1.5)
        val overEnhanced = item.copy(enhancementLevel = 15)
        assertEquals(300.0, overEnhanced.powerScore, 0.001)
        assertEquals(150.0, overEnhanced.effectiveAttackBonus, 0.001)
        assertEquals(150.0, overEnhanced.effectiveDefenseBonus, 0.001)

        // Way above 10: enhancementLevel = 100 should also coerce to 10 (multiplier 1.5)
        val maxCoerced = item.copy(enhancementLevel = 100)
        assertEquals(300.0, maxCoerced.powerScore, 0.001)
    }

    @Test
    fun `test socketed gem stat bonuses append correctly to power score and effective stat getters`() {
        val rubyGem = MaterialCatalog.getMaterial("ruby_gem_2") // attackBonus = 2, statBonus = 2 -> powerScore = 4
        val sapphireGem = MaterialCatalog.getMaterial("sapphire_gem_2") // magicBonus = 2, statBonus = 2 -> powerScore = 4
        val emeraldGem = MaterialCatalog.getMaterial("emerald_gem_2") // defenseBonus = 2, statBonus = 2 -> powerScore = 4
        val topazGem = MaterialCatalog.getMaterial("topaz_gem_2") // hpBonus = 2, statBonus = 2 -> powerScore = 4

        val sockets = listOf(
            SocketSlot(id = "slot_1", socketedGem = rubyGem),
            SocketSlot(id = "slot_2", socketedGem = sapphireGem),
            SocketSlot(id = "slot_3", socketedGem = emeraldGem),
            SocketSlot(id = "slot_4", socketedGem = topazGem),
        )

        val baseItem = Item(
            id = "socketed_item",
            name = "Gemmed Plate",
            slot = ItemSlot.ARMOR,
            rarity = Rarity.LEGENDARY,
            attackBonus = 50,
            defenseBonus = 50,
            magicBonus = 50,
            hpBonus = 100,
            emoji = "🛡️",
            floorFound = 10,
            enhancementLevel = 2, // 1.10 multiplier for base stats
            sockets = sockets,
        )

        // Base stats at enhancementLevel = 2:
        // basePowerScore = 50 + 50 + 50 + (100/5 = 20) = 170
        // base power scaled by 1.10 = (170 * 1.10) = 187
        // socketBonus = 4 + 4 + 4 + 2 = 14
        // expected total powerScore = 187 + 14 = 201
        assertEquals(14, baseItem.socketBonus)
        assertEquals(201.0, baseItem.powerScore, 0.001)

        // Effective stats at enhancementLevel = 2 (1.10 multiplier on base, scaled by gem %):
        // effectiveAttackBonus: (50 * 1.10) * 1.02 = 56.1
        // effectiveDefenseBonus: (50 * 1.10) * 1.02 = 56.1
        // effectiveMagicBonus: (50 * 1.10) * 1.02 = 56.1
        // effectiveHpBonus: (100 * 1.10) * 1.02 = 112.2
        assertEquals(56.1, baseItem.effectiveAttackBonus, 0.001)
        assertEquals(56.1, baseItem.effectiveDefenseBonus, 0.001)
        assertEquals(56.1, baseItem.effectiveMagicBonus, 0.001)
        assertEquals(112.2, baseItem.effectiveHpBonus, 0.001)
    }

    @Test
    fun `test material power score calculation and socket slot helper properties`() {
        // Material powerScore = attackBonus + defenseBonus + magicBonus + (hpBonus / 5) + statBonus
        val customGem = Material(
            id = "custom_gem",
            nameRes = 0,
            category = MaterialCategory.ESSENCE,
            attackBonus = 10,
            defenseBonus = 5,
            magicBonus = 15,
            hpBonus = 50, // 50 / 5 = 10
            statBonus = 20,
        )

        // expected powerScore = 10 + 5 + 15 + 10 + 20 = 60
        assertEquals(60, customGem.powerScore)

        val emptySocket = SocketSlot(id = "empty_1")
        assertFalse(emptySocket.isFilled)
        assertNull(emptySocket.gem)
        assertEquals(0, emptySocket.powerBonus)

        val filledSocket = SocketSlot(id = "filled_1", socketedGem = customGem)
        assertTrue(filledSocket.isFilled)
        assertEquals(customGem, filledSocket.gem)
        assertEquals(60, filledSocket.powerBonus)
    }

    @Test
    fun `test material catalog retrieves known materials and provides default for unknown id`() {
        val ironOre = MaterialCatalog.getMaterial("iron_ore")
        assertEquals("iron_ore", ironOre.id)
        assertEquals(MaterialCategory.ORE, ironOre.category)

        val unknownMat = MaterialCatalog.getMaterial("non_existent_material_id")
        assertEquals("non_existent_material_id", unknownMat.id)
        assertEquals(MaterialCategory.ORE, unknownMat.category)

        val allMaterials = MaterialCatalog.getAllKnownMaterials()
        assertTrue(allMaterials.isNotEmpty())
        assertTrue(allMaterials.any { it.id == "ruby_gem_1" })
    }
}
