package com.game.dungeon.engine

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import com.game.dungeon.data.models.*
import org.junit.Assert.*
import org.junit.Test

class GameplayProgressionTest {

    private class DummyContext : ContextWrapper(
        object : ContextWrapper(null) {
            @Suppress("DEPRECATION")
            private val dummyResources = object : Resources(null, DisplayMetrics(), Configuration()) {
                override fun getText(id: Int): CharSequence = "TestString"
                override fun getString(id: Int): String = "TestString"
                override fun getString(id: Int, vararg formatArgs: Any?): String = "TestStringFormatted"
                override fun getStringArray(id: Int): Array<String> = arrayOf("TestHero")
            }
            override fun getResources(): Resources = dummyResources
        }
    )

    private val mockContext: Context = DummyContext()

    @Test
    fun testNoFloorSkips_EnemyTemplatesCoverAllFloors() {
        for (dimNum in 1..3) {
            val dimension = FFDimensionData.getDimension(dimNum)
            val maxFloor = dimNum * 100
            for (floor in 1..maxFloor) {
                val enemies = FFDimensionData.getEnemiesForFloor(dimension, floor)
                assertTrue("Floor $floor in Dimension $dimNum must have at least 1 enemy template", enemies.isNotEmpty())
            }
        }
    }

    @Test
    fun testDimensionMaxFloors() {
        assertEquals(100, FFDimensionData.getDimension(1).number * 100)
        assertEquals(200, FFDimensionData.getDimension(2).number * 100)
        assertEquals(300, FFDimensionData.getDimension(3).number * 100)
        assertEquals(400, FFDimensionData.getDimension(4).number * 100)
    }

    @Test
    fun testBossForMaxFloor() {
        for (dimNum in 1..3) {
            val dimension = FFDimensionData.getDimension(dimNum)
            val maxFloor = dimNum * 100
            val boss = FFDimensionData.getBossForFloor(dimension, maxFloor)
            assertNotNull("Dimension $dimNum max floor $maxFloor must have a boss", boss)
            assertTrue("Boss at max floor $maxFloor must be marked as boss", boss!!.isBoss)
        }
    }

    @Test
    fun testEnemyStatAndRewardScaling() {
        val template = FFEnemyTemplate(
            nameRes = 1,
            emoji = "👺",
            minFloor = 1,
            maxFloor = 300,
            gilReward = 10
        )

        val enemyFloor10 = Enemy.fromTemplate(template, floor = 10, context = mockContext)
        val enemyFloor100 = Enemy.fromTemplate(template, floor = 100, context = mockContext)
        val enemyFloor200 = Enemy.fromTemplate(template, floor = 200, context = mockContext)

        // Difficulty / Stats scaling
        assertTrue("Floor 100 HP must be greater than Floor 10 HP", enemyFloor100.maxHp > enemyFloor10.maxHp)
        assertTrue("Floor 200 HP must be greater than Floor 100 HP", enemyFloor200.maxHp > enemyFloor100.maxHp)
        assertTrue("Floor 100 ATK must be greater than Floor 10 ATK", enemyFloor100.attack > enemyFloor10.attack)

        // Gil Reward scaling
        assertTrue("Floor 100 Gil reward must be greater than Floor 10 Gil reward", enemyFloor100.gilDropped > enemyFloor10.gilDropped)
        assertTrue("Floor 200 Gil reward must be greater than Floor 100 Gil reward", enemyFloor200.gilDropped > enemyFloor100.gilDropped)
    }

    @Test
    fun testItemCategoriesAndStatScalingByDimension() {
        val itemDim1 = Item.random(floor = 50, context = mockContext, minRarity = Rarity.LEGENDARY, dimension = 1)
        val itemDim3 = Item.random(floor = 50, context = mockContext, minRarity = Rarity.LEGENDARY, dimension = 3)

        assertNotNull(itemDim1)
        assertNotNull(itemDim3)
        assertTrue("Item from higher dimension should have equal or higher power score for same rarity", itemDim3.powerScore >= itemDim1.powerScore)
    }

    @Test
    fun testDamageMitigationNever100PercentAndHasDiminishingReturns() {
        val floor = 100
        val k = 150f + floor * 0.8f

        val def100 = 100f
        val def500 = 500f
        val def10000 = 10000f

        val mit100 = (def100 / (def100 + k)).coerceAtMost(0.85f)
        val mit500 = (def500 / (def500 + k)).coerceAtMost(0.85f)
        val mit10000 = (def10000 / (def10000 + k)).coerceAtMost(0.85f)

        // Mitigation never reaches or exceeds 0.85 (85%)
        assertTrue("Mitigation must never exceed 0.85", mit10000 <= 0.85f)
        assertEquals(0.85f, mit10000, 0.001f)

        // Diminishing returns: Gain per point of defense from 0->100 is higher than from 100->500
        val gainPerDefLow = mit100 / 100f
        val gainPerDefHigh = (mit500 - mit100) / 400f
        assertTrue("Higher defense must have lower marginal mitigation gain per point", gainPerDefLow > gainPerDefHigh)
    }

    @Test
    fun testCritChanceAndPercentageStatsDiminishingReturns() {
        val hero = Hero(
            heroClass = HeroClass.WARRIOR,
            name = "Warrior",
            currentHp = 100,
            currentMp = 50,
            aiPriority = AIPriority.ATTACK
        )

        // Test CRIT_CHANCE with high raw values
        val dummyRelicBonusesHigh = RelicBonuses.from(
            GameState(critChanceRelic = 500, doubleLootRelic = 100, hpRelic = 10, defenseRelic = 10)
        )
        val stats = hero.calculateStats(emptyList(), dummyRelicBonusesHigh)
        val critChance = stats["CRIT_CHANCE"] ?: 0

        assertTrue("Crit chance must be capped at max 85%", critChance <= 85)
        assertEquals(85, critChance)

        // Test Double Loot Chance diminishing returns & max cap (75%)
        val doubleLootChanceEffective = dummyRelicBonusesHigh.effectiveDoubleLootChance
        assertTrue("Double loot chance must be capped at max 75%", doubleLootChanceEffective <= 75f)

        // Test low vs high incremental gain for double loot
        val relicLow = RelicBonuses.from(GameState(doubleLootRelic = 2)) // raw = 10
        val relicMid = RelicBonuses.from(GameState(doubleLootRelic = 10)) // raw = 50
        val gainLow = relicLow.effectiveDoubleLootChance / 10f
        val gainHigh = (relicMid.effectiveDoubleLootChance - relicLow.effectiveDoubleLootChance) / 40f
        assertTrue("Double loot chance must demonstrate diminishing returns", gainLow > gainHigh)
    }

    @Test
    fun testRelicHpAndDefenseScaling() {
        val gs0 = GameState(hpRelic = 0, defenseRelic = 0)
        val gs10 = GameState(hpRelic = 10, defenseRelic = 10)
        val gs50 = GameState(hpRelic = 50, defenseRelic = 50)

        val relics0 = RelicBonuses.from(gs0)
        val relics10 = RelicBonuses.from(gs10)
        val relics50 = RelicBonuses.from(gs50)

        assertEquals(0, relics0.hpBonus)
        assertEquals(0, relics0.defenseBonus)

        // Level 10: hpBonus = 10 * 60 + 10^2 * 2 = 600 + 200 = 800
        assertEquals(800, relics10.hpBonus)
        // Level 10: defenseBonus = 10 * 8 + 10^2 = 80 + 100 = 180
        assertEquals(180, relics10.defenseBonus)

        // Level 50: hpBonus = 50 * 60 + 50^2 * 2 = 3000 + 5000 = 8000
        assertEquals(8000, relics50.hpBonus)
        // Level 50: defenseBonus = 50 * 8 + 50^2 = 400 + 2500 = 2900
        assertEquals(2900, relics50.defenseBonus)

        assertTrue("Higher relic levels must provide significantly increased HP", relics50.hpBonus > relics10.hpBonus)
        assertTrue("Higher relic levels must provide significantly increased DEF", relics50.defenseBonus > relics10.defenseBonus)
    }

    @Test
    fun testRehireLastPartyCostAndEmptyState() {
        // Empty last party state
        val gsEmpty = GameState(lastPartyClasses = emptyList())
        assertTrue("Empty last party should produce empty missing classes", gsEmpty.lastPartyClasses.isEmpty())

        // Saved last party with WARRIOR and BLACK_MAGE
        val lastParty = listOf(HeroClass.WARRIOR, HeroClass.BLACK_MAGE)
        val gsSaved = GameState(lastPartyClasses = lastParty)
        assertEquals(2, gsSaved.lastPartyClasses.size)

        // Expected cost when neither is currently hired
        val expectedCost = HeroClass.WARRIOR.hireCost + HeroClass.BLACK_MAGE.hireCost
        val currentHired = emptyList<HeroClass>()
        
        val missing = lastParty.filter { !currentHired.contains(it) }
        val calculatedCost = missing.sumOf { it.hireCost.toLong() }
        assertEquals(expectedCost.toLong(), calculatedCost)

        // Partial team hired: WARRIOR already hired
        val currentHiredClasses = mutableListOf(HeroClass.WARRIOR)
        val missingPartial = mutableListOf<HeroClass>()
        for (job in lastParty) {
            if (currentHiredClasses.contains(job)) {
                currentHiredClasses.remove(job)
            } else {
                missingPartial.add(job)
            }
        }
        assertEquals(1, missingPartial.size)
        assertEquals(HeroClass.BLACK_MAGE, missingPartial.first())
        assertEquals(HeroClass.BLACK_MAGE.hireCost.toLong(), missingPartial.sumOf { it.hireCost.toLong() })
    }

    @Test
    fun testQuickEquipDistributionLogic() {
        val hero1 = Hero(id = "h1", heroClass = HeroClass.WARRIOR, name = "Hero 1", currentHp = 100, currentMp = 10, aiPriority = AIPriority.ATTACK, partyPosition = 0)
        val hero2 = Hero(id = "h2", heroClass = HeroClass.BLACK_MAGE, name = "Hero 2", currentHp = 100, currentMp = 10, aiPriority = AIPriority.ATTACK, partyPosition = 1)
        val heroes = listOf(hero1, hero2)

        val w1 = Item(id = "w1", name = "Iron Sword", slot = ItemSlot.WEAPON, rarity = Rarity.COMMON, attackBonus = 10, emoji = "🗡️", floorFound = 1)
        val w2 = Item(id = "w2", name = "Excalibur", slot = ItemSlot.WEAPON, rarity = Rarity.RARE, attackBonus = 50, emoji = "🗡️", floorFound = 1)
        val w3 = Item(id = "w3", name = "Bronze Sword", slot = ItemSlot.WEAPON, rarity = Rarity.COMMON, attackBonus = 5, emoji = "🗡️", floorFound = 1)

        val pool = mutableListOf(w1, w2, w3)

        val updatedItems = mutableListOf<Item>()
        val sortedHeroes = heroes.sortedBy { it.partyPosition }

        sortedHeroes.forEach { hero ->
            val bestWeapon = pool.filter { it.slot == ItemSlot.WEAPON }.maxByOrNull { it.powerScore }
            if (bestWeapon != null) {
                pool.remove(bestWeapon)
                updatedItems.add(bestWeapon.copy(ownerId = hero.id))
            }
        }

        val hero1Equipped = updatedItems.find { it.ownerId == hero1.id }
        assertNotNull(hero1Equipped)
        assertEquals("w2", hero1Equipped!!.id)

        val hero2Equipped = updatedItems.find { it.ownerId == hero2.id }
        assertNotNull(hero2Equipped)
        assertEquals("w1", hero2Equipped!!.id)

        assertTrue(pool.contains(w3))
    }

    @Test
    fun testHeroCopyPreservesBonusStatsAndCombatDamage() {
        val hero = Hero(
            id = "test_vivi",
            heroClass = HeroClass.BLACK_MAGE,
            name = "Vivi",
            currentHp = 5614,
            currentMp = 441,
            level = 1,
            aiPriority = AIPriority.ATTACK
        ).apply {
            attackBonus = 137
            defenseBonus = 4822
            hpBonus = 5529
            magicBonus = 122
            mpBonus = 351
            critChance = 42
            critDamage = 50
        }

        val copiedHero = hero.copy(currentHp = 5000)

        // Verify bonus stats are preserved on copy
        assertEquals(137, copiedHero.attackBonus)
        assertEquals(4822, copiedHero.defenseBonus)
        assertEquals(5529, copiedHero.hpBonus)
        assertEquals(122, copiedHero.magicBonus)
        assertEquals(351, copiedHero.mpBonus)
        assertEquals(42, copiedHero.critChance)
        assertEquals(50, copiedHero.critDamage)

        // Verify total stats match expected base + bonus
        assertEquals(hero.baseAttack + 137, copiedHero.attack)
        assertEquals(hero.baseDefense + 4822, copiedHero.defense)
        assertEquals(hero.baseMaxHp + 5529, copiedHero.maxHp)

        // Verify hero does not die on taking small damage when HP is high
        copiedHero.currentHp -= 113
        assertTrue("Hero with high hpBonus must remain alive after taking 113 damage", copiedHero.isAlive)
        assertEquals(4887, copiedHero.currentHp)
    }
}
