package com.game.dungeon.ui.viewmodels

import app.cash.turbine.test
import com.game.dungeon.data.models.CodexMilestone
import com.game.dungeon.data.models.KillThreshold
import com.game.dungeon.data.models.MaterialCatalog
import com.game.dungeon.data.models.MaterialCodexDetail
import com.game.dungeon.data.models.MonsterCodexDetail
import com.game.dungeon.data.models.MonsterType
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CodexViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: GameRepository
    private lateinit var viewModel: CodexViewModel

    private val completionPercentageFlow = MutableStateFlow(0f)
    private val monsterCodexDetailsFlow = MutableStateFlow<List<MonsterCodexDetail>>(emptyList())
    private val materialCodexDetailsFlow = MutableStateFlow<List<MaterialCodexDetail>>(emptyList())

    @Before
    fun setUp() {
        repository = mock()
        whenever(repository.codexCompletionPercentage).thenReturn(completionPercentageFlow)
        whenever(repository.monsterCodexDetails).thenReturn(monsterCodexDetailsFlow)
        whenever(repository.materialCodexDetails).thenReturn(materialCodexDetailsFlow)

        viewModel = CodexViewModel(repository)
    }

    @Test
    fun `test initial state emits default values`() = runTest {
        val monsterDetail = MonsterCodexDetail(
            monsterType = MonsterType.GOBLIN,
            killCount = 5,
            threshold = KillThreshold.SIGHTED,
            isDiscovered = true
        )
        val materialDetail = MaterialCodexDetail(
            material = MaterialCatalog.getMaterial("iron_ore"),
            isDiscovered = true
        )

        monsterCodexDetailsFlow.value = listOf(monsterDetail)
        materialCodexDetailsFlow.value = listOf(materialDetail)
        completionPercentageFlow.value = 30f

        viewModel.uiState.test {
            val state = awaitItem()

            assertEquals(CodexTab.BESTIARY, state.selectedTab)
            assertEquals(30f, state.completionPercentage)
            assertEquals(1, state.bestiaryEntries.size)
            assertEquals(MonsterType.GOBLIN, state.bestiaryEntries.first().monsterType)
            assertEquals(1, state.materialEntries.size)
            assertEquals("iron_ore", state.materialEntries.first().material.id)
            assertEquals(listOf(CodexMilestone.TIER_25), state.milestones)
            assertEquals("", state.searchQuery)
            assertNull(state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test selectTab updates selectedTab in uiState`() = runTest {
        viewModel.uiState.test {
            val initial = awaitItem()
            assertEquals(CodexTab.BESTIARY, initial.selectedTab)

            viewModel.selectTab(CodexTab.MATERIALS)
            var state = awaitItem()
            while (state.selectedTab != CodexTab.MATERIALS) {
                state = awaitItem()
            }
            assertEquals(CodexTab.MATERIALS, state.selectedTab)

            viewModel.selectTab(CodexTab.EQUIPMENT)
            state = awaitItem()
            while (state.selectedTab != CodexTab.EQUIPMENT) {
                state = awaitItem()
            }
            assertEquals(CodexTab.EQUIPMENT, state.selectedTab)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test updateSearchQuery filters bestiary and material entries`() = runTest {
        val goblin = MonsterCodexDetail(MonsterType.GOBLIN, 1, KillThreshold.SIGHTED, true)
        val wolf = MonsterCodexDetail(MonsterType.WOLF, 0, KillThreshold.UNDISCOVERED, false)

        val ironOre = MaterialCodexDetail(MaterialCatalog.getMaterial("iron_ore"), true)
        val fireEssence = MaterialCodexDetail(MaterialCatalog.getMaterial("fire_essence"), true)

        monsterCodexDetailsFlow.value = listOf(goblin, wolf)
        materialCodexDetailsFlow.value = listOf(ironOre, fireEssence)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertEquals(2, initial.bestiaryEntries.size)
            assertEquals(2, initial.materialEntries.size)

            viewModel.updateSearchQuery("goblin")
            var state = awaitItem()
            while (state.searchQuery != "goblin") {
                state = awaitItem()
            }

            assertEquals("goblin", state.searchQuery)
            assertEquals(1, state.bestiaryEntries.size)
            assertEquals(MonsterType.GOBLIN, state.bestiaryEntries.first().monsterType)
            assertTrue(state.materialEntries.isEmpty())

            viewModel.updateSearchQuery("essence")
            state = awaitItem()
            while (state.searchQuery != "essence") {
                state = awaitItem()
            }

            assertTrue(state.bestiaryEntries.isEmpty())
            assertEquals(1, state.materialEntries.size)
            assertEquals("fire_essence", state.materialEntries.first().material.id)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test dismissUserMessage clears userMessageRes`() = runTest {
        viewModel.dismissUserMessage()
        assertNull(viewModel.uiState.value.userMessageRes)
    }
}
