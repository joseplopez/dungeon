package com.game.dungeon.engine

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.dungeon.analytics.AnalyticsManager
import com.game.dungeon.data.models.AIPriority
import com.game.dungeon.data.models.BattleSpeed
import com.game.dungeon.data.models.CodexBonuses
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.repository.GameRepository
import com.game.dungeon.data.repository.UserPreferencesRepository
import com.game.dungeon.monetization.AdManager
import com.game.dungeon.monetization.BillingManager
import com.game.dungeon.ui.viewmodels.DungeonViewModel
import com.game.dungeon.ui.viewmodels.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PermadeathProtectionTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun testGameStateDefaultPermadeathProtectionState() {
        val gs = GameState()
        assertFalse(gs.hasPermadeathProtectionUnlocked)
        assertFalse(gs.isPermadeathProtectionActive)
    }

    @Test
    fun testFinalizeRun_deletesHeroWhenProtectionInactive() = runTest {
        val repository: GameRepository = mock()
        val userPreferencesRepo: UserPreferencesRepository = mock()
        val analytics: AnalyticsManager = mock()
        val adManager: AdManager = mock()
        val billingManager: BillingManager = mock()

        val initialGs = GameState(isPermadeathProtectionActive = false, hasPermadeathProtectionUnlocked = false)
        val gameStateFlow = MutableStateFlow(initialGs)
        val deadHero = Hero(
            id = "hero_dead",
            heroClass = HeroClass.WARRIOR,
            name = "Warrior",
            currentHp = 0,
            currentMp = 10,
            aiPriority = AIPriority.ATTACK
        )

        whenever(repository.getGameState()).thenReturn(gameStateFlow)
        whenever(repository.getRoster()).thenReturn(flowOf(listOf(deadHero)))
        whenever(repository.getGameStateOnce()).thenReturn(gameStateFlow.value)
        whenever(repository.getCodexBonuses()).thenReturn(CodexBonuses())
        whenever(repository.getEquippedItems(any())).thenReturn(flowOf(emptyList()))
        whenever(userPreferencesRepo.battleSpeed).thenReturn(flowOf(BattleSpeed.NORMAL))
        whenever(userPreferencesRepo.hasPremiumSpeed).thenReturn(flowOf(false))

        val dungeonViewModel = DungeonViewModel(
            repo = repository,
            userPreferencesRepo = userPreferencesRepo,
            analytics = analytics,
            adManager = adManager,
            billingManager = billingManager,
            context = context
        )

        dungeonViewModel.battleState.value = dungeonViewModel.battleState.value.copy(
            pendingFallenHeroIds = setOf("hero_dead"),
            gilEarnedThisRun = 1000L
        )

        dungeonViewModel.finalizeRun()
        advanceUntilIdle()

        verify(repository).removeHero(deadHero)
        verify(repository).addGil(700L) // 30% penalty applied: 1000 - 300 = 700
    }

    @Test
    fun testFinalizeRun_restoresHeroWhenProtectionActive() = runTest {
        val repository: GameRepository = mock()
        val userPreferencesRepo: UserPreferencesRepository = mock()
        val analytics: AnalyticsManager = mock()
        val adManager: AdManager = mock()
        val billingManager: BillingManager = mock()

        val initialGs = GameState(isPermadeathProtectionActive = true, hasPermadeathProtectionUnlocked = true)
        val gameStateFlow = MutableStateFlow(initialGs)
        val deadHero = Hero(
            id = "hero_dead",
            heroClass = HeroClass.WARRIOR,
            name = "Warrior",
            currentHp = 0,
            currentMp = 10,
            aiPriority = AIPriority.ATTACK
        )

        whenever(repository.getGameState()).thenReturn(gameStateFlow)
        whenever(repository.getRoster()).thenReturn(flowOf(listOf(deadHero)))
        whenever(repository.getGameStateOnce()).thenReturn(gameStateFlow.value)
        whenever(repository.getCodexBonuses()).thenReturn(CodexBonuses())
        whenever(repository.getEquippedItems(any())).thenReturn(flowOf(emptyList()))
        whenever(userPreferencesRepo.battleSpeed).thenReturn(flowOf(BattleSpeed.NORMAL))
        whenever(userPreferencesRepo.hasPremiumSpeed).thenReturn(flowOf(false))

        val dungeonViewModel = DungeonViewModel(
            repo = repository,
            userPreferencesRepo = userPreferencesRepo,
            analytics = analytics,
            adManager = adManager,
            billingManager = billingManager,
            context = context
        )

        dungeonViewModel.battleState.value = dungeonViewModel.battleState.value.copy(
            pendingFallenHeroIds = setOf("hero_dead"),
            gilEarnedThisRun = 1000L
        )

        dungeonViewModel.finalizeRun()
        advanceUntilIdle()

        verify(repository, never()).removeHero(deadHero)
        verify(repository).saveHero(org.mockito.kotlin.check { hero ->
            assertEquals("hero_dead", hero.id)
            assertTrue("Hero HP should be restored to max HP", hero.currentHp > 0)
        })
        verify(repository).addGil(700L) // Standard run failure penalty STILL triggers!
    }
}
