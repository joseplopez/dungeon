package com.game.dungeon.data.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ItemCraftingTest {

    private fun createBaseItem(
        attackBonus: Int = 100,
        defenseBonus: Int = 50,
        magicBonus: Int = 20,
        hpBonus: Int = 100,
        mpBonus: Int = 10,
        critChanceBonus: Int = 5,
        critDamageBonus: Int = 20,
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

        assertEquals(215, item.basePowerScore)
        assertEquals(215, item.powerScore)
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

        assertEquals(100, item.basePowerScore)

        // Key milestones check (+0%, +25%, +50%)
        assertEquals(100, item.copy(enhancementLevel = 0).powerScore)
        assertEquals(125, item.copy(enhancementLevel = 5).powerScore)
        assertEquals(150, item.copy(enhancementLevel = 10).powerScore)

        // Verify all enhancement levels 0..10 scale according to (base * (1.0 + level * 0.05)).toInt()
        for (level in 0..10) {
            val enhancedItem = item.copy(enhancementLevel = level)
            val expectedPower = (item.basePowerScore * (1.0 + (level * 0.05))).toInt()
            assertEquals("PowerScore mismatch at enhancement level $level", expectedPower, enhancedItem.powerScore)
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
        assertEquals(200, negativeEnhanced.powerScore)
        assertEquals(100, negativeEnhanced.effectiveAttackBonus)
        assertEquals(100, negativeEnhanced.effectiveDefenseBonus)

        // Above 10: enhancementLevel = 15 should be coerced to 10 (multiplier 1.5)
        val overEnhanced = item.copy(enhancementLevel = 15)
        assertEquals(300, overEnhanced.powerScore)
        assertEquals(150, overEnhanced.effectiveAttackBonus)
        assertEquals(150, overEnhanced.effectiveDefenseBonus)

        // Way above 10: enhancementLevel = 100 should also coerce to 10 (multiplier 1.5)
        val maxCoerced = item.copy(enhancementLevel = 100)
        assertEquals(300, maxCoerced.powerScore)
    }

    @Test
    fun `test socketed gem stat bonuses append correctly to power score and effective stat getters`() {
        val rubyGem = MaterialCatalog.getMaterial("ruby_gem_2") // attackBonus = 15, statBonus = 15 -> powerScore = 30
        val sapphireGem = MaterialCatalog.getMaterial("sapphire_gem_2") // magicBonus = 15, statBonus = 15 -> powerScore = 30
        val emeraldGem = MaterialCatalog.getMaterial("emerald_gem_2") // defenseBonus = 15, statBonus = 15 -> powerScore = 30
        val topazGem = MaterialCatalog.getMaterial("topaz_gem_2") // hpBonus = 75, statBonus = 15 -> powerScore = 30 (75/5 + 15)

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
        // base power scaled by 1.10 = (170 * 1.10).toInt() = 187
        // socketBonus = 30 + 30 + 30 + 30 = 120
        // expected total powerScore = 187 + 120 = 307
        assertEquals(120, baseItem.socketBonus)
        assertEquals(307, baseItem.powerScore)

        // Effective stats at enhancementLevel = 2 (1.10 multiplier on base, plus gem bonus):
        // effectiveAttackBonus: (50 * 1.10).toInt() + 15 = 55 + 15 = 70
        // effectiveDefenseBonus: (50 * 1.10).toInt() + 15 = 55 + 15 = 70
        // effectiveMagicBonus: (50 * 1.10).toInt() + 15 = 55 + 15 = 70
        // effectiveHpBonus: (100 * 1.10).toInt() + 75 = 110 + 75 = 185
        assertEquals(70, baseItem.effectiveAttackBonus)
        assertEquals(70, baseItem.effectiveDefenseBonus)
        assertEquals(70, baseItem.effectiveMagicBonus)
        assertEquals(185, baseItem.effectiveHpBonus)
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
