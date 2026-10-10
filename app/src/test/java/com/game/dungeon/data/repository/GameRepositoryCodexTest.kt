package com.game.dungeon.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.dungeon.data.db.GameDatabase
import com.game.dungeon.data.models.CodexMilestone
import com.game.dungeon.data.models.KillThreshold
import com.game.dungeon.data.models.MaterialCatalog
import com.game.dungeon.data.models.MonsterType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class GameRepositoryCodexTest {

    private lateinit var database: GameDatabase
    private lateinit var leaderboardRepository: LeaderboardRepository
    private lateinit var repository: GameRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GameDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        leaderboardRepository = Mockito.mock(LeaderboardRepository::class.java)
        repository = GameRepository(database, leaderboardRepository, database.codexDao)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testRecordMonsterKillIncrementsCountAndUpdatesThreshold() = runBlocking {
        val monsterId = MonsterType.GOBLIN.name

        // Record 1 kill -> SIGHTED
        repository.recordMonsterKill(monsterId)
        var details = repository.monsterCodexDetails.first()
        var goblinDetail = details.first { it.monsterType == MonsterType.GOBLIN }
        assertEquals(1, goblinDetail.killCount)
        assertTrue(goblinDetail.isDiscovered)
        assertEquals(KillThreshold.SIGHTED, goblinDetail.threshold)

        // Record 9 more kills (10 total) -> ANALYZED
        repeat(9) { repository.recordMonsterKill(monsterId) }
        details = repository.monsterCodexDetails.first()
        goblinDetail = details.first { it.monsterType == MonsterType.GOBLIN }
        assertEquals(10, goblinDetail.killCount)
        assertEquals(KillThreshold.ANALYZED, goblinDetail.threshold)

        // Record 40 more kills (50 total) -> MASTERED
        repeat(40) { repository.recordMonsterKill(monsterId) }
        details = repository.monsterCodexDetails.first()
        goblinDetail = details.first { it.monsterType == MonsterType.GOBLIN }
        assertEquals(50, goblinDetail.killCount)
        assertEquals(KillThreshold.MASTERED, goblinDetail.threshold)
    }

    @Test
    fun testDiscoverMaterialAndAddMaterialMarkMaterialAsDiscovered() = runBlocking {
        val matId = "iron_ore"

        // Initially undiscovered in flow
        var details = repository.materialCodexDetails.first()
        var ironDetail = details.first { it.material.id == matId }
        assertFalse(ironDetail.isDiscovered)

        // Add material -> automatically discovers it
        repository.addMaterial(matId, 5)
        details = repository.materialCodexDetails.first()
        ironDetail = details.first { it.material.id == matId }
        assertTrue(ironDetail.isDiscovered)
    }

    @Test
    fun testCodexCompletionPercentageAndMilestoneBonuses() = runBlocking {
        val totalCatalog = MonsterType.entries.size + MaterialCatalog.getAllKnownMaterials().size
        val quarterCount = (totalCatalog * 0.25f).toInt() + 1

        // Discover 25%+ entries
        val monstersToDiscover = MonsterType.entries.take(quarterCount)
        monstersToDiscover.forEach { repository.recordMonsterKill(it.name) }

        val percentage = repository.getCodexCompletionPercentage()
        assertTrue("Percentage should be >= 25%, was $percentage", percentage >= 25f)

        val codexBonuses = repository.getCodexBonuses()
        assertEquals(0.05f, codexBonuses.goldMultiplierBonus, 0.001f)

        val relicBonuses = repository.getBonusFromCodex()
        // Default gold multiplier is 1.0f + 0.05f codex bonus = 1.05f
        assertEquals(1.05f, relicBonuses.goldMultiplier, 0.001f)
    }

    @Test
    fun testCodexMilestoneCalculationTiers() {
        var bonuses = CodexMilestone.calculateBonuses(0f)
        assertEquals(0f, bonuses.goldMultiplierBonus, 0.001f)
        assertEquals(0, bonuses.critChanceBonus)
        assertEquals(0f, bonuses.magiciteChanceBonus, 0.001f)

        bonuses = CodexMilestone.calculateBonuses(25f)
        assertEquals(0.05f, bonuses.goldMultiplierBonus, 0.001f)
        assertEquals(0, bonuses.critChanceBonus)

        bonuses = CodexMilestone.calculateBonuses(50f)
        assertEquals(0.05f, bonuses.goldMultiplierBonus, 0.001f)
        assertEquals(5, bonuses.critChanceBonus)

        bonuses = CodexMilestone.calculateBonuses(100f)
        assertEquals(0.05f, bonuses.goldMultiplierBonus, 0.001f)
        assertEquals(5, bonuses.critChanceBonus)
        assertEquals(0.10f, bonuses.magiciteChanceBonus, 0.001f)
    }
}
