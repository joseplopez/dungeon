package com.game.dungeon.engine

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.dungeon.data.models.Enemy
import com.game.dungeon.data.models.FFDimensionData
import com.game.dungeon.data.models.MonsterType
import com.game.dungeon.data.models.RelicBonuses
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FFBattleEngineDropTest {

    private lateinit var battleEngine: FFBattleEngine
    private lateinit var defaultRelicBonuses: RelicBonuses

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        battleEngine = FFBattleEngine(context)
        defaultRelicBonuses = createRelicBonuses()
    }

    private fun createRelicBonuses(
        alchemistDropChanceBonus: Float = 0f,
        magnetBonus: Float = 0f,
        pocketsBonus: Float = 0f,
    ): RelicBonuses {
        return RelicBonuses(
            attackBonus = 0,
            hpBonus = 0,
            mpBonus = 0,
            magicBonus = 0,
            defenseBonus = 0,
            goldMultiplier = 1f,
            magiciteChanceBonus = 0f,
            expMultiplier = 1f,
            itemStatBonus = 0f,
            magicShopLevel = 0,
            critChanceBonus = 0,
            critDamageBonus = 0,
            magnetBonus = magnetBonus,
            pocketsBonus = pocketsBonus,
            doubleLootChance = 0,
            jobMasteryLevels = emptyMap(),
            selectedPet = null,
            bossesDefeatedCount = 0,
            alchemistDropChanceBonus = alchemistDropChanceBonus,
        )
    }

    private fun createRegularEnemy(
        type: MonsterType = MonsterType.GOBLIN,
        floor: Int = 1,
    ): Enemy {
        return Enemy(
            id = "test_regular_enemy",
            type = type,
            name = type.name,
            emoji = "👹",
            currentHp = 100,
            maxHp = 100,
            attack = 10,
            defense = 5,
            gilReward = 10,
            floor = floor,
            isBoss = false,
        )
    }

    private fun createBossEnemy(
        type: MonsterType = MonsterType.GARLAND,
        floor: Int = 10,
    ): Enemy {
        return Enemy(
            id = "test_boss_enemy",
            type = type,
            name = type.name,
            emoji = "⚔️",
            currentHp = 1000,
            maxHp = 1000,
            attack = 50,
            defense = 20,
            gilReward = 200,
            floor = floor,
            isBoss = true,
        )
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 1. REGULAR ENEMY MATERIAL DROPS — FLOOR TIER CONSTRAINTS
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test regular enemy ore tier constraints match floor progression`() {
        val oreEnemyType = MonsterType.GOBLIN // General non-part, non-essence enemy

        // Floor 1..19 -> iron_ore
        assertEquals("iron_ore", battleEngine.selectMaterialForRegularEnemy(oreEnemyType, 1))
        assertEquals("iron_ore", battleEngine.selectMaterialForRegularEnemy(oreEnemyType, 15))
        assertEquals("iron_ore", battleEngine.selectMaterialForRegularEnemy(oreEnemyType, 19))

        // Floor 20..49 -> mithril_ore
        assertEquals("mithril_ore", battleEngine.selectMaterialForRegularEnemy(oreEnemyType, 20))
        assertEquals("mithril_ore", battleEngine.selectMaterialForRegularEnemy(oreEnemyType, 35))
        assertEquals("mithril_ore", battleEngine.selectMaterialForRegularEnemy(oreEnemyType, 49))

        // Floor 50..89 -> adamantite_ore
        assertEquals("adamantite_ore", battleEngine.selectMaterialForRegularEnemy(oreEnemyType, 50))
        assertEquals("adamantite_ore", battleEngine.selectMaterialForRegularEnemy(oreEnemyType, 70))
        assertEquals("adamantite_ore", battleEngine.selectMaterialForRegularEnemy(oreEnemyType, 89))

        // Floor >= 90 -> orichalcum_ore
        assertEquals("orichalcum_ore", battleEngine.selectMaterialForRegularEnemy(oreEnemyType, 90))
        assertEquals("orichalcum_ore", battleEngine.selectMaterialForRegularEnemy(oreEnemyType, 100))
        assertEquals("orichalcum_ore", battleEngine.selectMaterialForRegularEnemy(oreEnemyType, 200))
    }

    @Test
    fun `test regular enemy part tier constraints match floor progression`() {
        val partEnemyType = MonsterType.WOLF

        // Floor < 25 -> beast_fang or monster_bone
        val tier1Parts = setOf("beast_fang", "monster_bone")
        repeat(20) {
            val selected = battleEngine.selectMaterialForRegularEnemy(partEnemyType, 10)
            assertTrue("Expected tier 1 part, got $selected", tier1Parts.contains(selected))
        }

        // Floor 25..59 -> demon_horn or beast_fang
        val tier2Parts = setOf("demon_horn", "beast_fang")
        repeat(20) {
            val selected = battleEngine.selectMaterialForRegularEnemy(partEnemyType, 40)
            assertTrue("Expected tier 2 part, got $selected", tier2Parts.contains(selected))
        }

        // Floor >= 60 -> dragon_scale or demon_horn
        val tier3Parts = setOf("dragon_scale", "demon_horn")
        repeat(20) {
            val selected = battleEngine.selectMaterialForRegularEnemy(partEnemyType, 80)
            assertTrue("Expected tier 3 part, got $selected", tier3Parts.contains(selected))
        }
    }

    @Test
    fun `test regular enemy essence tier constraints match monster attributes and floor`() {
        // Elemental fire types -> fire_essence
        assertEquals("fire_essence", battleEngine.selectMaterialForRegularEnemy(MonsterType.BOMB, 10))
        assertEquals("fire_essence", battleEngine.selectMaterialForRegularEnemy(MonsterType.STOKER, 15))

        // Elemental ice types -> ice_essence
        assertEquals("ice_essence", battleEngine.selectMaterialForRegularEnemy(MonsterType.WATER_FLAN, 10))

        // Generic essence type below floor 50 -> fire, ice, or lightning essence
        val lowFloorEssences = setOf("fire_essence", "ice_essence", "lightning_essence")
        repeat(20) {
            val selected = battleEngine.selectMaterialForRegularEnemy(MonsterType.SLIME, 10)
            assertTrue("Expected basic elemental essence, got $selected", lowFloorEssences.contains(selected))
        }

        // Generic essence type or dark type at floor >= 50 -> dark_essence
        assertEquals("dark_essence", battleEngine.selectMaterialForRegularEnemy(MonsterType.SLIME, 50))
        assertEquals("dark_essence", battleEngine.selectMaterialForRegularEnemy(MonsterType.DARK_IMP, 20))
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 2. RELIC BONUSES & YIELD MULTIPLIERS
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test relic bonuses increase drop rates without exceeding soft caps`() {
        val dimension = FFDimensionData.getDimension(1)
        val enemy = createRegularEnemy(MonsterType.GOBLIN, floor = 1)

        val noRelicBonuses = createRelicBonuses(
            alchemistDropChanceBonus = 0f,
            magnetBonus = 0f,
            pocketsBonus = 0f,
        )

        val highRelicBonuses = createRelicBonuses(
            alchemistDropChanceBonus = 0.25f,
            magnetBonus = 2.0f,
            pocketsBonus = 3.0f,
        )

        val extremeRelicBonuses = createRelicBonuses(
            alchemistDropChanceBonus = 100.0f,
            magnetBonus = 100.0f,
            pocketsBonus = 100.0f,
        )

        val samples = 3000

        var dropsNoRelic = 0
        var dropsHighRelic = 0
        var dropsExtremeRelic = 0

        repeat(samples) {
            if (battleEngine.calculateMaterialDrops(enemy, 1, dimension, noRelicBonuses).isNotEmpty()) {
                dropsNoRelic++
            }
            if (battleEngine.calculateMaterialDrops(enemy, 1, dimension, highRelicBonuses).isNotEmpty()) {
                dropsHighRelic++
            }
            if (battleEngine.calculateMaterialDrops(enemy, 1, dimension, extremeRelicBonuses).isNotEmpty()) {
                dropsExtremeRelic++
            }
        }

        val rateNoRelic = dropsNoRelic.toDouble() / samples
        val rateHighRelic = dropsHighRelic.toDouble() / samples
        val rateExtremeRelic = dropsExtremeRelic.toDouble() / samples

        // High relic bonuses must produce higher drop rates than no relic bonuses
        assertTrue(
            "High relic bonuses ($rateHighRelic) should yield higher drop rate than base ($rateNoRelic)",
            rateHighRelic > rateNoRelic,
        )

        // Extreme relic bonuses must approach but never exceed the 80% soft/hard cap
        assertTrue(
            "Extreme relic bonus drop rate ($rateExtremeRelic) must strictly respect soft/hard cap (<= 0.82)",
            rateExtremeRelic <= 0.82,
        )
    }

    @Test
    fun `test pockets bonus increases yield per drop`() {
        val dimension = FFDimensionData.getDimension(1)
        val enemy = createRegularEnemy(MonsterType.GOBLIN, floor = 60) // floor >= 50 adds extraYieldChance

        val baseRelics = createRelicBonuses(pocketsBonus = 0f)
        val maxPocketsRelics = createRelicBonuses(pocketsBonus = 5f, alchemistDropChanceBonus = 1f)

        val samples = 1000
        var baseSingleQtyCount = 0
        var pocketsDoubleQtyCount = 0

        repeat(samples) {
            val baseDrops = battleEngine.calculateMaterialDrops(enemy, 60, dimension, baseRelics)
            baseDrops.values.forEach { qty ->
                if (qty == 1) baseSingleQtyCount++
            }

            val pocketsDrops = battleEngine.calculateMaterialDrops(enemy, 60, dimension, maxPocketsRelics)
            pocketsDrops.values.forEach { qty ->
                if (qty > 1) pocketsDoubleQtyCount++
            }
        }

        assertTrue("High pockets bonus should trigger extra yield quantity (>1)", pocketsDoubleQtyCount > 0)
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. BOSS DROPS & GUARANTEED TROPHIES
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test boss drops grant guaranteed boss trophies based on floor tier`() {
        val dimension = FFDimensionData.getDimension(1)

        // Tier 1 Trophy: floor <= 30
        for (floor in listOf(10, 20, 30)) {
            val boss = createBossEnemy(MonsterType.GARLAND, floor)
            val drops = battleEngine.calculateMaterialDrops(boss, floor, dimension, defaultRelicBonuses)

            assertTrue("Floor $floor boss should drop boss_trophy_1", drops.containsKey("boss_trophy_1"))
            assertFalse("Floor $floor boss should NOT drop boss_trophy_2", drops.containsKey("boss_trophy_2"))
            assertFalse("Floor $floor boss should NOT drop boss_trophy_3", drops.containsKey("boss_trophy_3"))
        }

        // Tier 2 Trophy: floor 31..70
        for (floor in listOf(40, 50, 70)) {
            val boss = createBossEnemy(MonsterType.LICH, floor)
            val drops = battleEngine.calculateMaterialDrops(boss, floor, dimension, defaultRelicBonuses)

            assertFalse("Floor $floor boss should NOT drop boss_trophy_1", drops.containsKey("boss_trophy_1"))
            assertTrue("Floor $floor boss should drop boss_trophy_2", drops.containsKey("boss_trophy_2"))
            assertFalse("Floor $floor boss should NOT drop boss_trophy_3", drops.containsKey("boss_trophy_3"))
        }

        // Tier 3 Trophy: floor > 70
        for (floor in listOf(80, 90, 100)) {
            val boss = createBossEnemy(MonsterType.TIAMAT, floor)
            val drops = battleEngine.calculateMaterialDrops(boss, floor, dimension, defaultRelicBonuses)

            assertFalse("Floor $floor boss should NOT drop boss_trophy_1", drops.containsKey("boss_trophy_1"))
            assertFalse("Floor $floor boss should NOT drop boss_trophy_2", drops.containsKey("boss_trophy_2"))
            assertTrue("Floor $floor boss should drop boss_trophy_3", drops.containsKey("boss_trophy_3"))
        }
    }

    @Test
    fun `test boss drop trophy quantity scales with dimension`() {
        val boss = createBossEnemy(MonsterType.GARLAND, floor = 10)

        val dim1 = FFDimensionData.getDimension(1)
        val dim2 = FFDimensionData.getDimension(2)
        val dim3 = FFDimensionData.getDimension(3)
        val dim4 = FFDimensionData.getDimension(4)

        // Dimension <= 2 grants 1 trophy
        assertEquals(1, battleEngine.calculateMaterialDrops(boss, 10, dim1, defaultRelicBonuses)["boss_trophy_1"])
        assertEquals(1, battleEngine.calculateMaterialDrops(boss, 10, dim2, defaultRelicBonuses)["boss_trophy_1"])

        // Dimension > 2 grants 2 trophies
        assertEquals(2, battleEngine.calculateMaterialDrops(boss, 10, dim3, defaultRelicBonuses)["boss_trophy_1"])
        assertEquals(2, battleEngine.calculateMaterialDrops(boss, 10, dim4, defaultRelicBonuses)["boss_trophy_1"])
    }

    @Test
    fun `test boss drops include additional high-tier material stack drops`() {
        val dimension = FFDimensionData.getDimension(1)
        val boss = createBossEnemy(MonsterType.TIAMAT, floor = 90)

        val highTierPool = setOf(
            "adamantite_ore", "orichalcum_ore",
            "dragon_scale", "demon_horn",
            "dark_essence", "fire_essence", "ice_essence", "lightning_essence",
            "ruby_gem_2", "sapphire_gem_2", "emerald_gem_2", "topaz_gem_2",
        )

        val drops = battleEngine.calculateMaterialDrops(boss, 90, dimension, defaultRelicBonuses)

        // Boss drops must contain the trophy plus at least 1 high-tier material stack
        assertTrue("Boss drops should contain multiple entries including trophy", drops.size >= 2)
        assertTrue("Boss drops should include trophy", drops.containsKey("boss_trophy_3"))

        val nonTrophyKeys = drops.keys.filter { !it.startsWith("boss_trophy_") }
        assertTrue("Boss drops must contain non-trophy high-tier materials", nonTrophyKeys.isNotEmpty())
        nonTrophyKeys.forEach { matId ->
            assertTrue("Material $matId should belong to high-tier boss drop pool", highTierPool.contains(matId))
        }
    }
}
