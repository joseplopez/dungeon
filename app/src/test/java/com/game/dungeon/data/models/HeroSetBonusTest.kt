package com.game.dungeon.data.models

import org.junit.Assert.assertEquals
import org.junit.Test

class HeroSetBonusTest {

    @Test
    fun testCrystalSetBonus_activeOnKnightWith3Pieces() {
        val knight = Hero(
            heroClass = HeroClass.KNIGHT,
            name = "Cecil",
            currentHp = 100,
            currentMp = 20,
            aiPriority = AIPriority.DEFEND
        )

        val crystalArmor = Item(
            id = "c1", name = "Crystal Armor", slot = ItemSlot.ARMOR, rarity = Rarity.MYTHIC,
            defenseBonus = 50, hpBonus = 100, emoji = "🛡️", floorFound = 300, setId = "set_crystal"
        )
        val crystalShield = Item(
            id = "c2", name = "Crystal Shield", slot = ItemSlot.SHIELD, rarity = Rarity.MYTHIC,
            defenseBonus = 30, hpBonus = 50, emoji = "🛡️", floorFound = 300, setId = "set_crystal"
        )
        val crystalHelm = Item(
            id = "c3", name = "Crystal Helm", slot = ItemSlot.WEAPON, rarity = Rarity.MYTHIC,
            defenseBonus = 20, hpBonus = 50, emoji = "🪖", floorFound = 300, setId = "set_crystal"
        )

        val baseDef = knight.baseDefense + 100
        val baseHp = knight.baseMaxHp + 200

        val boostedStats = knight.calculateStats(listOf(crystalArmor, crystalShield, crystalHelm))

        // CRYSTAL set: +40% DEF, +25% HP
        val expectedDef = baseDef + (baseDef * 0.40f).toInt()
        val expectedHp = baseHp + (baseHp * 0.25f).toInt()

        assertEquals(expectedDef, boostedStats["DEF"])
        assertEquals(expectedHp, boostedStats["HP"])
    }

    @Test
    fun testSetBonus_inactiveWhenFewerThan3Pieces() {
        val knight = Hero(
            heroClass = HeroClass.KNIGHT,
            name = "Cecil",
            currentHp = 100,
            currentMp = 20,
            aiPriority = AIPriority.DEFEND
        )

        val crystalArmor = Item(
            id = "c1", name = "Crystal Armor", slot = ItemSlot.ARMOR, rarity = Rarity.MYTHIC,
            defenseBonus = 50, hpBonus = 100, emoji = "🛡️", floorFound = 300, setId = "set_crystal"
        )
        val crystalShield = Item(
            id = "c2", name = "Crystal Shield", slot = ItemSlot.SHIELD, rarity = Rarity.MYTHIC,
            defenseBonus = 30, hpBonus = 50, emoji = "🛡️", floorFound = 300, setId = "set_crystal"
        )

        val stats = knight.calculateStats(listOf(crystalArmor, crystalShield))
        val expectedDef = knight.baseDefense + 80
        val expectedHp = knight.baseMaxHp + 150

        // Inactive because only 2 pieces equipped
        assertEquals(expectedDef, stats["DEF"])
        assertEquals(expectedHp, stats["HP"])
    }

    @Test
    fun testSetBonus_inactiveWhenClassMismatch() {
        val blackMage = Hero(
            heroClass = HeroClass.BLACK_MAGE,
            name = "Vivi",
            currentHp = 50,
            currentMp = 100,
            aiPriority = AIPriority.MAGIC
        )

        // Equipped with Crystal (Knight) set
        val crystalArmor = Item(
            id = "c1", name = "Crystal Armor", slot = ItemSlot.ARMOR, rarity = Rarity.MYTHIC,
            defenseBonus = 50, hpBonus = 100, emoji = "🛡️", floorFound = 300, setId = "set_crystal"
        )
        val crystalShield = Item(
            id = "c2", name = "Crystal Shield", slot = ItemSlot.SHIELD, rarity = Rarity.MYTHIC,
            defenseBonus = 30, hpBonus = 50, emoji = "🛡️", floorFound = 300, setId = "set_crystal"
        )
        val crystalHelm = Item(
            id = "c3", name = "Crystal Helm", slot = ItemSlot.WEAPON, rarity = Rarity.MYTHIC,
            defenseBonus = 20, hpBonus = 50, emoji = "🪖", floorFound = 300, setId = "set_crystal"
        )

        val stats = blackMage.calculateStats(listOf(crystalArmor, crystalShield, crystalHelm))
        val expectedDef = blackMage.baseDefense + 100
        val expectedHp = blackMage.baseMaxHp + 200

        // Inactive because Black Mage is not targetClass (KNIGHT)
        assertEquals(expectedDef, stats["DEF"])
        assertEquals(expectedHp, stats["HP"])
    }

    @Test
    fun testMagusSetBonus_activeOnBlackMage() {
        val blackMage = Hero(
            heroClass = HeroClass.BLACK_MAGE,
            name = "Vivi",
            currentHp = 50,
            currentMp = 100,
            aiPriority = AIPriority.MAGIC
        )

        val magusStaff = Item(
            id = "m1", name = "Magus Staff", slot = ItemSlot.WEAPON, rarity = Rarity.MYTHIC,
            magicBonus = 100, emoji = "🪄", floorFound = 300, setId = "set_magus"
        )
        val magusRobe = Item(
            id = "m2", name = "Magus Robe", slot = ItemSlot.ARMOR, rarity = Rarity.MYTHIC,
            magicBonus = 50, emoji = "👘", floorFound = 300, setId = "set_magus"
        )
        val magusRing = Item(
            id = "m3", name = "Magus Ring", slot = ItemSlot.ACCESSORY, rarity = Rarity.MYTHIC,
            magicBonus = 50, emoji = "💍", floorFound = 300, setId = "set_magus"
        )

        val stats = blackMage.calculateStats(listOf(magusStaff, magusRobe, magusRing))

        // MAGUS set: +40% MAG, +15 Flat Crit Chance
        val baseMag = blackMage.baseMagic + 200
        val expectedMag = baseMag + (baseMag * 0.40f).toInt()
        assertEquals(expectedMag, stats["MAG"])

        // Crit Chance addition (+15 flat to calculated final crit)
        val initialCrit = (100f * 5 / (5 + 50)).toInt() // 9
        val expectedCrit = initialCrit + 15 // 24
        assertEquals(expectedCrit, stats["CRIT_CHANCE"])
    }
}
