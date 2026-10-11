package com.game.dungeon.engine

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.dungeon.data.models.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JobAbilitiesTest {

    private lateinit var mockContext: Context
    private lateinit var engine: FFBattleEngine

    @Before
    fun setUp() {
        mockContext = ApplicationProvider.getApplicationContext<Context>()
        engine = FFBattleEngine(mockContext)
    }

    @Test
    fun testAll18JobAbilitiesExecuteWithoutCrashing() {
        for (job in HeroClass.entries) {
            for (masteryLvl in listOf(1, 10, 25)) {
                val hero = Hero(
                    id = "hero_${job.name}_$masteryLvl",
                    heroClass = job,
                    name = "Hero_${job.name}",
                    currentHp = 100,
                    currentMp = 50,
                    level = 1,
                    abilityCharge = 2, // Next turn reaches 3 and triggers ability
                    aiPriority = job.defaultPriority
                )
                val events = mutableListOf<FFBattleEvent>()

                runBlocking {
                    engine.runBattle(
                        heroes = listOf(hero),
                        dimension = FFDimensionData.getDimension(1),
                        startFloor = 1,
                        speed = BattleSpeed.INSTANT,
                        relicBonuses = RelicBonuses.from(GameState(jobMasteryLevels = mapOf(job to masteryLvl))),
                        isPaused = { false },
                        onEvent = { events.add(it) }
                    )
                }

                val abilityUsed = events.filterIsInstance<FFBattleEvent.AbilityUsed>().firstOrNull()
                assertNotNull("AbilityUsed event must be emitted for $job at mastery level $masteryLvl", abilityUsed)
            }
        }
    }

    @Test
    fun testBardSongPerformsGroupHeal() {
        val bard = Hero(
            id = "bard_1",
            heroClass = HeroClass.BARD,
            name = "Bard",
            currentHp = 50,
            currentMp = 50,
            abilityCharge = 2,
            aiPriority = AIPriority.ATTACK
        )
        val events = mutableListOf<FFBattleEvent>()

        runBlocking {
            engine.runBattle(
                heroes = listOf(bard),
                dimension = FFDimensionData.getDimension(1),
                startFloor = 1,
                speed = BattleSpeed.INSTANT,
                relicBonuses = RelicBonuses.from(GameState(jobMasteryLevels = mapOf(HeroClass.BARD to 10))),
                isPaused = { false },
                onEvent = { events.add(it) }
            )
        }

        val songEvent = events.filterIsInstance<FFBattleEvent.BardSong>().firstOrNull()
        assertNotNull("BardSong event should be emitted", songEvent)

        val groupHealEvent = events.filterIsInstance<FFBattleEvent.GroupHeal>().firstOrNull()
        assertNotNull("GroupHeal event must be emitted by Bard song", groupHealEvent)
        assertTrue("Bard should heal self or allies", (groupHealEvent?.amounts?.get("bard_1") ?: 0) > 0)
    }

    @Test
    fun testMimeFallbackWhenNoPriorAbility() {
        val mime = Hero(
            id = "mime_1",
            heroClass = HeroClass.MIME,
            name = "Mime",
            currentHp = 100,
            currentMp = 50,
            abilityCharge = 2,
            aiPriority = AIPriority.ATTACK
        )
        val events = mutableListOf<FFBattleEvent>()

        runBlocking {
            engine.runBattle(
                heroes = listOf(mime),
                dimension = FFDimensionData.getDimension(1),
                startFloor = 1,
                speed = BattleSpeed.INSTANT,
                relicBonuses = RelicBonuses.from(GameState(jobMasteryLevels = mapOf(HeroClass.MIME to 10))),
                isPaused = { false },
                onEvent = { events.add(it) }
            )
        }

        val dmgEvent = events.filterIsInstance<FFBattleEvent.DamageDealt>().firstOrNull()
        assertNotNull("Mime without previous ability should fall back to basic attack", dmgEvent)
    }

    @Test
    fun testRedMageDualCastAtTier1PerformsAttackAndHeal() {
        val redMage = Hero(
            id = "red_1",
            heroClass = HeroClass.RED_MAGE,
            name = "RedMage",
            currentHp = 50,
            currentMp = 50,
            abilityCharge = 2,
            aiPriority = AIPriority.ATTACK
        )
        val events = mutableListOf<FFBattleEvent>()

        runBlocking {
            engine.runBattle(
                heroes = listOf(redMage),
                dimension = FFDimensionData.getDimension(1),
                startFloor = 1,
                speed = BattleSpeed.INSTANT,
                relicBonuses = RelicBonuses.from(GameState(jobMasteryLevels = mapOf(HeroClass.RED_MAGE to 1))),
                isPaused = { false },
                onEvent = { events.add(it) }
            )
        }

        val dmgEvent = events.filterIsInstance<FFBattleEvent.DamageDealt>().firstOrNull()
        assertNotNull("Red Mage T1 should perform magic attack on enemy", dmgEvent)

        val healEvent = events.filterIsInstance<FFBattleEvent.HealCast>().firstOrNull()
        assertNotNull("Red Mage T1 Dual Cast should perform Cure heal on wounded ally", healEvent)
    }

    @Test
    fun testEnemyDefeatedEventEmittedOnlyOncePerEnemy() {
        val warrior = Hero(
            id = "warrior_1",
            heroClass = HeroClass.WARRIOR,
            name = "Warrior",
            currentHp = 100,
            currentMp = 50,
            abilityCharge = 2, // Triggers Gigantaxe Cleave at mastery 25
            aiPriority = AIPriority.ATTACK
        )
        val events = mutableListOf<FFBattleEvent>()

        runBlocking {
            engine.runBattle(
                heroes = listOf(warrior),
                dimension = FFDimensionData.getDimension(1),
                startFloor = 1,
                speed = BattleSpeed.INSTANT,
                relicBonuses = RelicBonuses.from(GameState(jobMasteryLevels = mapOf(HeroClass.WARRIOR to 25))),
                isPaused = { false },
                onEvent = { events.add(it) }
            )
        }

        val defeatedEvents = events.filterIsInstance<FFBattleEvent.EnemyDefeated>()
        val distinctEnemyIds = defeatedEvents.map { it.enemyId }.distinct()
        assertEquals("Each enemy should emit at most 1 EnemyDefeated event", distinctEnemyIds.size, defeatedEvents.size)
    }

    @Test
    fun testMultiHitSkillDoesNotAttackZeroHealthEnemy() {
        val monk = Hero(
            id = "monk_1",
            heroClass = HeroClass.MONK,
            name = "Monk",
            currentHp = 100,
            currentMp = 50,
            abilityCharge = 2,
            aiPriority = AIPriority.ATTACK
        )
        val events = mutableListOf<FFBattleEvent>()

        runBlocking {
            engine.runBattle(
                heroes = listOf(monk),
                dimension = FFDimensionData.getDimension(1),
                startFloor = 1,
                speed = BattleSpeed.INSTANT,
                relicBonuses = RelicBonuses.from(GameState(jobMasteryLevels = mapOf(HeroClass.MONK to 10))),
                isPaused = { false },
                onEvent = { events.add(it) }
            )
        }

        val enemyDefeatedEvents = events.filterIsInstance<FFBattleEvent.EnemyDefeated>()
        for (defeated in enemyDefeatedEvents) {
            val defeatedIndex = events.indexOf(defeated)
            val damageAfterDefeated = events.subList(defeatedIndex + 1, events.size)
                .filterIsInstance<FFBattleEvent.DamageDealt>()
                .filter { it.targetId == defeated.enemyId }
            assertTrue("No damage events should be dealt to enemy ${defeated.enemyId} after EnemyDefeated event", damageAfterDefeated.isEmpty())
        }
    }
}
