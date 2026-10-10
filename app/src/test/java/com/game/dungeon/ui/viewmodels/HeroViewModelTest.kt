package com.game.dungeon.ui.viewmodels

import app.cash.turbine.*
import com.game.dungeon.R
import com.game.dungeon.data.models.AIPriority
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.models.MaterialCatalog
import com.game.dungeon.data.models.MaterialInventoryItem
import com.game.dungeon.data.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HeroViewModelTestRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HeroViewModelTest {

    @get:Rule
    val mainDispatcherRule = HeroViewModelTestRule()

    private lateinit var repository: GameRepository
    private lateinit var viewModel: HeroViewModel

    private val rosterFlow = MutableStateFlow<List<Hero>>(emptyList())
    private val materialInventoryFlow = MutableStateFlow<List<MaterialInventoryItem>>(emptyList())

    private val hero1 = Hero(
        id = "hero_1",
        heroClass = HeroClass.WARRIOR,
        name = "Warrior",
        currentHp = 100,
        currentMp = 10,
        level = 20,
        maxLevel = 20,
        aiPriority = AIPriority.ATTACK,
    )

    @Before
    fun setUp() {
        repository = mock()
        whenever(repository.getRoster()).thenReturn(rosterFlow)
        whenever(repository.getMaterialInventory()).thenReturn(materialInventoryFlow)

        rosterFlow.value = listOf(hero1)
        materialInventoryFlow.value = emptyList()

        viewModel = HeroViewModel(repository, mainDispatcherRule.testDispatcher)
    }

    private suspend fun TurbineTestContext<HeroUiState>.awaitStateWithMessage(): HeroUiState {
        var state = awaitItem()
        while (state.userMessageRes == null && state.errorMessageRes == null) {
            state = awaitItem()
        }
        return state
    }

    @Test
    fun testHeroUiStateIncludesLevelCapAndTrophyStatus(): Unit = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.heroes.size)
            assertEquals(20, state.maxLevel)
            assertTrue(state.isCapped)
            assertEquals("boss_trophy_1", state.requiredTrophyId)
            assertFalse(state.hasRequiredTrophy)

            // Add required trophy
            materialInventoryFlow.value = listOf(
                MaterialInventoryItem(
                    material = MaterialCatalog.getMaterial("boss_trophy_1"),
                    amount = 1,
                ),
            )

            val updatedState = awaitItem()
            assertTrue(updatedState.hasRequiredTrophy)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testBreakthroughHeroFailsWhenLackingRequiredTrophy(): Unit = runTest {
        materialInventoryFlow.value = emptyList()

        viewModel.uiState.test {
            awaitItem() // Initial state
            viewModel.breakthroughHero("hero_1")

            val state = awaitStateWithMessage()
            assertEquals(R.string.craft_err_insufficient_materials, state.errorMessageRes)
            assertEquals(R.string.craft_err_insufficient_materials, state.userMessageRes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testBreakthroughHeroSuccess(): Unit = runTest {
        materialInventoryFlow.value = listOf(
            MaterialInventoryItem(
                material = MaterialCatalog.getMaterial("boss_trophy_1"),
                amount = 1,
            ),
        )
        val uncappedHero = hero1.copy(maxLevel = 40)
        whenever(repository.uncapHeroLevel(any())).thenReturn(Result.success(uncappedHero))

        viewModel.uiState.test {
            awaitItem() // Initial state
            viewModel.breakthroughHero("hero_1")

            val state = awaitStateWithMessage()
            assertEquals(R.string.craft_msg_hero_uncapped, state.userMessageRes)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
