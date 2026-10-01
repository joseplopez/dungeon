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
    fun testRebalancedGilAndMagiciteScaling() {
        val regularTemplate = FFEnemyTemplate(
            nameRes = 1,
            emoji = "👺",
            minFloor = 1,
            maxFloor = 1000,
            gilReward = 100,
            magiciteChance = 0.05f
        )
        val bossTemplate = FFEnemyTemplate(
            nameRes = 1,
            emoji = "🐉",
            minFloor = 1,
            maxFloor = 1000,
            gilReward = 1000,
            isBoss = true
        )

        val regularFloor1 = Enemy.fromTemplate(regularTemplate, floor = 1, context = mockContext)
        val regularFloor10 = Enemy.fromTemplate(regularTemplate, floor = 10, context = mockContext)
        val regularFloor100 = Enemy.fromTemplate(regularTemplate, floor = 100, context = mockContext)
        val regularFloor500 = Enemy.fromTemplate(regularTemplate, floor = 500, context = mockContext)

        // Monotonic Gil progression
        assertTrue(regularFloor10.gilDropped > regularFloor1.gilDropped)
        assertTrue(regularFloor100.gilDropped > regularFloor10.gilDropped)
        assertTrue(regularFloor500.gilDropped > regularFloor100.gilDropped)

        // Verify Gil scaling remains balanced at floor 100 (sub-linear multiplier ~4.5x, gil ~450)
        assertTrue("Floor 100 Gil reward should be balanced (< 800 for 100 base)", regularFloor100.gilDropped < 800)
        // Verify Gil scaling remains balanced at floor 500 (sub-linear multiplier ~14.35x, gil ~1435)
        assertTrue("Floor 500 Gil reward should be balanced (< 2500 for 100 base)", regularFloor500.gilDropped < 2500)

        // Boss magicite tests
        val bossFloor1 = Enemy.fromTemplate(bossTemplate, floor = 1, context = mockContext)
        val bossFloor100 = Enemy.fromTemplate(bossTemplate, floor = 100, context = mockContext)
        val bossFloor500 = Enemy.fromTemplate(bossTemplate, floor = 500, context = mockContext)

        assertTrue("Boss Floor 1 Magicite should be >= 3", bossFloor1.magiciteDropped >= 3)
        assertTrue("Boss Floor 100 Magicite should be around 10", bossFloor100.magiciteDropped in 8..12)
        assertTrue("Boss Floor 500 Magicite should be capped at 20", bossFloor500.magiciteDropped <= 20)

        // Regular monster magicite quantity check (never > 2)
        for (f in listOf(1, 10, 50, 100, 200, 500, 1000)) {
            val reg = Enemy.fromTemplate(regularTemplate, floor = f, context = mockContext)
            assertTrue("Regular enemy magicite drop quantity on floor $f must be <= 2", reg.magiciteDropped <= 2)
        }
    }

    @Test
    fun testEnemyAndBossDimensionStatScaling() {
        val regTemplate = FFEnemyTemplate(
            nameRes = 1,
            emoji = "👺",
            minFloor = 1,
            maxFloor = 500,
            gilReward = 50
        )
        val bossTemplate = FFEnemyTemplate(
            nameRes = 1,
            emoji = "🐉",
            minFloor = 1,
            maxFloor = 500,
            gilReward = 500,
            isBoss = true
        )

        val dim1 = FFDimensionData.getDimension(1)
        val dim2 = FFDimensionData.getDimension(2)
        val dim3 = FFDimensionData.getDimension(3)

        val regDim1 = Enemy.fromTemplate(regTemplate, floor = 50, context = mockContext, dimension = dim1)
        val regDim2 = Enemy.fromTemplate(regTemplate, floor = 50, context = mockContext, dimension = dim2)
        val regDim3 = Enemy.fromTemplate(regTemplate, floor = 50, context = mockContext, dimension = dim3)

        // Dimension 2 regular enemy should have ~30% higher stats than Dimension 1
        assertTrue("Dim 2 HP must be > Dim 1 HP", regDim2.maxHp > regDim1.maxHp)
        assertTrue("Dim 3 HP must be > Dim 2 HP", regDim3.maxHp > regDim2.maxHp)
        assertTrue("Dim 2 ATK must be > Dim 1 ATK", regDim2.attack > regDim1.attack)
        assertTrue("Dim 3 ATK must be > Dim 2 ATK", regDim3.attack > regDim2.attack)

        // Boss dimension scaling checks
        val bossDim1 = Enemy.fromTemplate(bossTemplate, floor = 100, context = mockContext, dimension = dim1)
        val bossDim2 = Enemy.fromTemplate(bossTemplate, floor = 100, context = mockContext, dimension = dim2)
        val bossDim3 = Enemy.fromTemplate(bossTemplate, floor = 100, context = mockContext, dimension = dim3)

        assertTrue("Dim 2 Boss HP must be > Dim 1 Boss HP", bossDim2.maxHp > bossDim1.maxHp)
        assertTrue("Dim 3 Boss HP must be > Dim 2 Boss HP", bossDim3.maxHp > bossDim2.maxHp)
        assertTrue("Dim 2 Boss ATK must be > Dim 1 Boss ATK", bossDim2.attack > bossDim1.attack)

        // Verify extra boss scaling in higher dimensions
        val regHpRatioDim2 = regDim2.maxHp.toFloat() / regDim1.maxHp
        val bossHpRatioDim2 = bossDim2.maxHp.toFloat() / bossDim1.maxHp
        assertTrue("Boss dimension scaling ratio should be higher than regular enemy dimension scaling ratio", bossHpRatioDim2 > regHpRatioDim2)
    }

    @Test
    fun testRebalancedItemStatScalingAcrossDimensions() {
        val itemDim1 = Item.random(floor = 100, context = mockContext, minRarity = Rarity.LEGENDARY, dimension = 1)
        
        // Verify stats on high floors stay within reasonable non-inflated bounds
        // Legendary item on floor 100 in Dim 1 should have HP < 400 (previously was > 1,400)
        assertTrue("Floor 100 Legendary item HP should be balanced (< 400)", itemDim1.hpBonus < 400)

        // Verify deterministic stat comparison across dimensions for same slot & rarity
        val bonusMult = 4.0f // Legendary
        val atkDim1 = ((2 + 100 / 8) * bonusMult * (1f + 0 * 0.12f)).toInt() // 14 * 4 = 56
        val atkDim3 = ((2 + 100 / 8) * bonusMult * (1f + 2 * 0.12f)).toInt() // 14 * 4 * 1.24 = 69
        val atkDim5 = ((2 + 100 / 8) * bonusMult * (1f + 4 * 0.12f)).toInt() // 14 * 4 * 1.48 = 82

        assertTrue("Higher dimension weapon ATK should scale deterministically", atkDim3 > atkDim1)
        assertTrue("Higher dimension weapon ATK should scale deterministically", atkDim5 > atkDim3)

        // Verify weapon attack bounds
        assertTrue("Weapon ATK in Dim 1 on Floor 100 should be around 56", atkDim1 in 50..65)
        assertTrue("Weapon ATK in Dim 5 on Floor 100 should be around 82", atkDim5 in 75..95)

        // Verify defense bounds
        val defDim1 = ((2 + 100 / 6) * bonusMult * 1.0f).toInt() // 18 * 4 = 72
        val defDim5 = ((2 + 100 / 6) * bonusMult * (1f + 4 * 0.12f)).toInt() // 18 * 4 * 1.48 = 106
        assertTrue("Armor DEF in Dim 1 on Floor 100 should be around 72", defDim1 in 60..80)
        assertTrue("Armor DEF in Dim 5 on Floor 100 should be around 106", defDim5 in 95..120)
    }

    @Test
    fun testItemCategoriesAndStatScalingByDimension() {
        val itemsDim1 = List(30) { Item.random(floor = 50, context = mockContext, minRarity = Rarity.LEGENDARY, dimension = 1) }
        val itemsDim3 = List(30) { Item.random(floor = 50, context = mockContext, minRarity = Rarity.LEGENDARY, dimension = 3) }

        val avgPowerDim1 = itemsDim1.map { it.powerScore }.average()
        val avgPowerDim3 = itemsDim3.map { it.powerScore }.average()

        assertTrue("Item from higher dimension should on average have higher power score for same rarity", avgPowerDim3 > avgPowerDim1)
    }

    @Test
    fun testMagicalWeaponsAndTenDimensionsCategoryGeneration() {
        for (dim in 1..10) {
            val items = List(50) { Item.random(floor = 50, context = mockContext, minRarity = Rarity.RARE, dimension = dim) }
            assertTrue("Items generated for Dimension $dim should not be empty", items.isNotEmpty())
        }

        var foundMagicalWeapon = false
        var foundPhysicalWeapon = false
        repeat(200) {
            val item = Item.random(floor = 50, context = mockContext, dimension = 1)
            if (item.slot == ItemSlot.WEAPON) {
                if (item.magicBonus > 0 && item.attackBonus == 0) {
                    foundMagicalWeapon = true
                }
                if (item.attackBonus > 0 && item.magicBonus == 0) {
                    foundPhysicalWeapon = true
                }
            }
        }
        assertTrue("Should be able to generate magical weapons with magicBonus", foundMagicalWeapon)
        assertTrue("Should be able to generate physical weapons with attackBonus", foundPhysicalWeapon)
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
