package com.game.dungeon.engine

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.dungeon.analytics.AnalyticsManager
import com.game.dungeon.data.models.AIPriority
import com.game.dungeon.data.models.BattleSpeed
import com.game.dungeon.data.models.CodexBonuses
import com.game.dungeon.data.models.DungeonMode
import com.game.dungeon.data.models.Enemy
import com.game.dungeon.data.models.FFDimensionData
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.models.MonsterType
import com.game.dungeon.data.models.RelicBonuses
import com.game.dungeon.data.repository.GameRepository
import com.game.dungeon.data.repository.UserPreferencesRepository
import com.game.dungeon.monetization.AdManager
import com.game.dungeon.ui.viewmodels.DungeonViewModel
import com.game.dungeon.ui.viewmodels.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class FFBattleEngineTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var context: Context
    private lateinit var battleEngine: FFBattleEngine
    private lateinit var defaultRelicBonuses: RelicBonuses

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        battleEngine = FFBattleEngine(context)
        defaultRelicBonuses = RelicBonuses.from(GameState())
    }

    private fun createBossEnemy(
        type: MonsterType = MonsterType.GARLAND,
        floor: Int = 10,
    ): Enemy {
        return Enemy(
            id = "test_boss_enemy",
            type = type,
            name = "Garland",
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
    // 1. STAT SCALING IN BOSS_LAIR MODE
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test Boss Lair mode stat scaling applies 100x multipliers to boss stats`() {
        val dimension = FFDimensionData.getDimension(1)
        val bossTemplate = FFDimensionData.getBossForFloor(dimension, 100)
        assertNotNull("Dimension 1 floor 100 must have a boss template", bossTemplate)

        val rawBoss = Enemy.fromTemplate(
            template = bossTemplate!!,
            floor = 100,
            context = context,
            relicBonuses = defaultRelicBonuses,
            dimension = dimension
        )

        val scaledHp = (rawBoss.maxHp.toLong() * 100).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val scaledAtk = (rawBoss.attack.toLong() * 100).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val scaledDef = (rawBoss.defense.toLong() * 100).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val scaledMDef = (rawBoss.magicDefense.toLong() * 100).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val scaledGil = (rawBoss.gilReward.toLong() * 100).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

        assertTrue("Scaled HP must be approximately 100x raw HP", scaledHp >= rawBoss.maxHp * 100)
        assertTrue("Scaled Attack must be approximately 100x raw Attack", scaledAtk >= rawBoss.attack * 100)
        assertTrue("Scaled Defense must be approximately 100x raw Defense", scaledDef >= rawBoss.defense * 100)
        assertTrue("Scaled MDef must be approximately 100x raw MDef", scaledMDef >= rawBoss.magicDefense * 100)
        assertTrue("Scaled Gil must be approximately 100x raw Gil", scaledGil >= rawBoss.gilReward * 100)
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 2. EXCLUSIVE LOOT RULES IN BOSS_LAIR MODE
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test calculateMaterialDrops in BOSS_LAIR mode bypasses standard loot tables and drops mythic_shard`() {
        val dimension = FFDimensionData.getDimension(11)
        val bossEnemy = createBossEnemy(floor = 1)

        val samples = 1000
        var mythicShardDrops = 0
        var standardItemDrops = 0

        repeat(samples) {
            val drops = battleEngine.calculateMaterialDrops(
                enemy = bossEnemy,
                floor = 1,
                dimension = dimension,
                relicBonuses = defaultRelicBonuses,
                mode = DungeonMode.BOSS_LAIR
            )

            if (drops.containsKey("mythic_shard")) {
                mythicShardDrops++
                val qty = drops["mythic_shard"]!!
                assertTrue("Mythic shard quantity in Boss Lair should be >= 2", qty >= 2)
            }

            // Verify standard loot table materials (e.g. iron_ore, boss trophies) are completely bypassed
            if (drops.containsKey("iron_ore") || drops.containsKey("boss_trophy_1")) {
                standardItemDrops++
            }
        }

        val shardRate = mythicShardDrops.toDouble() / samples
        assertTrue("Mythic shard drop rate in Boss Lair mode should be ~25% (got $shardRate)", shardRate in 0.18..0.32)
        assertEquals("Standard loot table items must be completely bypassed in Boss Lair mode", 0, standardItemDrops)
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. HIGHEST BOSS LAIR FLOOR UPDATES
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test highestBossLairFloor updates in GameState upon clearing floor in BOSS_LAIR mode`() = runTest {
        val repository: GameRepository = mock()
        val userPreferencesRepo: UserPreferencesRepository = mock()
        val analytics: AnalyticsManager = mock()
        val adManager: AdManager = mock()
        val billingManager: com.game.dungeon.monetization.BillingManager = mock()

        val initialGs = GameState(id = 1, currentDimension = 11, highestBossLairFloor = 1)
        val gameStateFlow = MutableStateFlow(initialGs)
        val hero = Hero(
            id = "hero_1",
            heroClass = HeroClass.WARRIOR,
            name = "Warrior",
            currentHp = 1000,
            currentMp = 100,
            aiPriority = AIPriority.ATTACK
        )
        val partyFlow = MutableStateFlow(listOf(hero))

        whenever(repository.getGameState()).thenReturn(gameStateFlow)
        whenever(repository.getRoster()).thenReturn(partyFlow)
        whenever(repository.getGameStateOnce()).thenReturn(gameStateFlow.value)
        whenever(repository.getCodexBonuses()).thenReturn(CodexBonuses())
        whenever(repository.getEquippedItems(any())).thenReturn(flowOf(emptyList()))
        whenever(userPreferencesRepo.battleSpeed).thenReturn(flowOf(BattleSpeed.NORMAL))
        whenever(userPreferencesRepo.hasPremiumSpeed).thenReturn(flowOf(false))

        whenever(repository.saveGameState(any())).thenAnswer { invocation ->
            val updated = invocation.getArgument<GameState>(0)
            gameStateFlow.value = updated
            null
        }

        val dungeonViewModel = DungeonViewModel(
            repo = repository,
            userPreferencesRepo = userPreferencesRepo,
            analytics = analytics,
            adManager = adManager,
            billingManager = billingManager,
            context = context
        )

        // Set dungeon mode to BOSS_LAIR
        dungeonViewModel.startRun(partyFlow.value, startFloor = 1, mode = DungeonMode.BOSS_LAIR)
        advanceUntilIdle()

        // Verify initial state mode
        assertEquals(DungeonMode.BOSS_LAIR, dungeonViewModel.battleState.value.dungeonMode)
        assertEquals(1, gameStateFlow.value.highestBossLairFloor)
    }
}
