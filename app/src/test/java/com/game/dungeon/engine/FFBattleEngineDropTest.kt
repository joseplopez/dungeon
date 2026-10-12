package com.game.dungeon.engine

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.dungeon.data.models.Enemy
import com.game.dungeon.data.models.FFDimensionData
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.MonsterLootTable
import com.game.dungeon.data.models.MonsterType
import com.game.dungeon.data.models.MythicSet
import com.game.dungeon.data.models.Rarity
import com.game.dungeon.data.models.RelicBonuses
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    // 1. MONSTER LOOT TABLE TESTS
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test monster loot table provides rules for regular and boss monsters`() {
        val goblinRules = MonsterLootTable.getDropsForMonster(MonsterType.GOBLIN, isBoss = false)
        assertTrue("Goblin should have drop rules", goblinRules.isNotEmpty())
        assertEquals("iron_ore", goblinRules.first().materialId)

        val garlandRules = MonsterLootTable.getDropsForMonster(MonsterType.GARLAND, isBoss = true)
        assertTrue("Garland boss should have trophy rule", garlandRules.any { it.isBossTrophy })
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

        assertTrue(
            "High relic bonuses ($rateHighRelic) should yield higher drop rate than base ($rateNoRelic)",
            rateHighRelic > rateNoRelic,
        )

        assertTrue(
            "Base drop rate without relics ($rateNoRelic) should be reduced and controlled (<= 0.18)",
            rateNoRelic <= 0.18,
        )

        assertTrue(
            "Extreme relic bonus drop rate ($rateExtremeRelic) must strictly respect soft/hard cap (<= 0.82)",
            rateExtremeRelic <= 0.82,
        )
    }

    @Test
    fun `test reduced base drop chance and relic scaling`() {
        val dimension = FFDimensionData.getDimension(1)
        val goblin = createRegularEnemy(MonsterType.GOBLIN, floor = 1)

        val noRelics = createRelicBonuses()
        val medRelics = createRelicBonuses(alchemistDropChanceBonus = 0.5f, magnetBonus = 1.0f)

        val samples = 5000
        var noRelicDrops = 0
        var medRelicDrops = 0

        repeat(samples) {
            if (battleEngine.calculateMaterialDrops(goblin, 1, dimension, noRelics).isNotEmpty()) {
                noRelicDrops++
            }
            if (battleEngine.calculateMaterialDrops(goblin, 1, dimension, medRelics).isNotEmpty()) {
                medRelicDrops++
            }
        }

        val noRelicRate = noRelicDrops.toDouble() / samples
        val medRelicRate = medRelicDrops.toDouble() / samples

        // Primary base drop is 0.08, secondary is 0.04 -> combined chance ~ 11.68%
        assertTrue("Base drop rate ($noRelicRate) should be between 8% and 15%", noRelicRate in 0.08..0.15)
        assertTrue("Medium relics drop rate ($medRelicRate) should scale up higher than base ($noRelicRate)", medRelicRate > noRelicRate)
    }

    @Test
    fun `test pockets bonus increases yield per drop`() {
        val dimension = FFDimensionData.getDimension(1)
        val enemy = createRegularEnemy(MonsterType.GOBLIN, floor = 60)

        val maxPocketsRelics = createRelicBonuses(pocketsBonus = 5f, alchemistDropChanceBonus = 1f)

        val samples = 1000
        var pocketsDoubleQtyCount = 0

        repeat(samples) {
            val pocketsDrops = battleEngine.calculateMaterialDrops(enemy, 60, dimension, maxPocketsRelics)
            pocketsDrops.values.forEach { qty ->
                if (qty > 1) pocketsDoubleQtyCount++
            }
        }

        assertTrue("High pockets bonus should trigger extra yield quantity (>1)", pocketsDoubleQtyCount > 0)
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. BOSS DROPS
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test boss drops include boss trophies from loot table`() {
        val dimension = FFDimensionData.getDimension(1)
        val boss = createBossEnemy(MonsterType.GARLAND, floor = 10)

        val samples = 50
        var trophyCount = 0
        repeat(samples) {
            val drops = battleEngine.calculateMaterialDrops(boss, 10, dimension, defaultRelicBonuses)
            if (drops.containsKey("boss_trophy_1")) {
                trophyCount++
            }
        }
        assertTrue("Boss Garland should drop boss_trophy_1 across samples", trophyCount > 0)
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 4. MYTHIC DROPS & SHARDS GATING
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test calculateMaterialDrops at floor under 100 yields NO mythic shard`() {
        val dimension = FFDimensionData.getDimension(1)
        val bossUnder100 = createBossEnemy(MonsterType.GARLAND, floor = 99)
        val enemyUnder100 = createRegularEnemy(MonsterType.GOBLIN, floor = 99)

        val bossDrops = battleEngine.calculateMaterialDrops(bossUnder100, 99, dimension, defaultRelicBonuses)
        val enemyDrops = battleEngine.calculateMaterialDrops(enemyUnder100, 99, dimension, defaultRelicBonuses)

        assertEquals("Boss at floor 99 should not drop mythic_shard", null, bossDrops["mythic_shard"])
        assertEquals("Enemy at floor 99 should not drop mythic_shard", null, enemyDrops["mythic_shard"])
    }

    @Test
    fun `test calculateMaterialDrops on boss at floor 100+ ALWAYS drops mythic shard`() {
        val dimension = FFDimensionData.getDimension(1)
        val boss100 = createBossEnemy(MonsterType.GARLAND, floor = 100)
        val boss200 = createBossEnemy(MonsterType.GARLAND, floor = 200)

        val drops100 = battleEngine.calculateMaterialDrops(boss100, 100, dimension, defaultRelicBonuses)
        val drops200 = battleEngine.calculateMaterialDrops(boss200, 200, dimension, defaultRelicBonuses)

        assertEquals("Boss at floor 100 should drop 1 mythic_shard", 1, drops100["mythic_shard"])
        assertEquals("Boss at floor 200 should drop 1 mythic_shard", 1, drops200["mythic_shard"])
    }

    @Test
    fun `test Item random generates Mythic item with non-null setId`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mythicItem = Item.random(
            floor = 100,
            context = context,
            relicBonuses = defaultRelicBonuses,
            minRarity = Rarity.MYTHIC,
            dimension = 1
        )

        assertEquals(Rarity.MYTHIC, mythicItem.rarity)
        assertNotNull("Mythic item must have non-null setId", mythicItem.setId)
        assertTrue("setId should match a known MythicSet id", MythicSet.fromId(mythicItem.setId) != null)
    }

    @Test
    fun `test standard item generation below floor 100 yields non mythic item`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        repeat(100) {
            val item = Item.random(
                floor = 99,
                context = context,
                relicBonuses = defaultRelicBonuses,
                minRarity = Rarity.COMMON,
                dimension = 1
            )
            assertTrue("Item generated on floor 99 should not be MYTHIC", item.rarity != Rarity.MYTHIC)
            assertEquals("Non-mythic item should have null setId", null, item.setId)
        }
    }
}
