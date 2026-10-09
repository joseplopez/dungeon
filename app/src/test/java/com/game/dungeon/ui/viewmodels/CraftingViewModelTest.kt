package com.game.dungeon.ui.viewmodels

import app.cash.turbine.*
import com.game.dungeon.R
import com.game.dungeon.data.db.MaterialEntity
import com.game.dungeon.data.models.AIPriority
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.models.Rarity
import com.game.dungeon.data.models.SocketSlot
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
import org.junit.Assert.assertNull
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
class MainDispatcherRule(
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
class CraftingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: GameRepository
    private lateinit var viewModel: CraftingViewModel

    private val inventoryFlow = MutableStateFlow<List<Item>>(emptyList())
    private val equippedFlow = MutableStateFlow<List<Item>>(emptyList())
    private val materialsFlow = MutableStateFlow<List<MaterialEntity>>(emptyList())
    private val rosterFlow = MutableStateFlow<List<Hero>>(emptyList())

    @Before
    fun setUp() {
        repository = mock()
        whenever(repository.getInventory()).thenReturn(inventoryFlow)
        whenever(repository.getEquippedItems()).thenReturn(equippedFlow)
        whenever(repository.getMaterials()).thenReturn(materialsFlow)
        whenever(repository.getRoster()).thenReturn(rosterFlow)

        viewModel = CraftingViewModel(repository)
    }

    private fun createTestItem(
        id: String = "item_1",
        name: String = "Test Blade",
        slot: ItemSlot = ItemSlot.WEAPON,
        enhancementLevel: Int = 0,
        sockets: List<SocketSlot> = emptyList(),
    ): Item {
        return Item(
            id = id,
            name = name,
            slot = slot,
            rarity = Rarity.COMMON,
            attackBonus = 20,
            emoji = "⚔️",
            floorFound = 1,
            enhancementLevel = enhancementLevel,
            sockets = sockets,
        )
    }

    private suspend fun awaitStateWithMessage(
        turbine: TurbineTestContext<CraftingUiState>,
        expectedMsgRes: Int,
    ): CraftingUiState {
        var state = turbine.awaitItem()
        while (state.userMessageRes != expectedMsgRes) {
            state = turbine.awaitItem()
        }
        return state
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 1. INITIAL UI STATE EMISSION
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test initial uiState emission collects inventory items, equipped items, and materials`() = runTest {
        val sword = createTestItem(id = "sword_1", name = "Iron Sword")
        val armor = createTestItem(id = "armor_1", name = "Leather Armor", slot = ItemSlot.ARMOR)

        val activeMaterial = MaterialEntity(id = "iron_ore", amount = 10)
        val emptyMaterial = MaterialEntity(id = "mithril_ore", amount = 0)

        inventoryFlow.value = listOf(sword)
        equippedFlow.value = listOf(armor)
        materialsFlow.value = listOf(activeMaterial, emptyMaterial)

        viewModel.uiState.test {
            val state = awaitItem()

            assertEquals(1, state.inventoryItems.size)
            assertEquals("sword_1", state.inventoryItems.first().id)

            assertEquals(1, state.equippedItems.size)
            assertEquals("armor_1", state.equippedItems.first().id)

            // Only materials with amount > 0 should be mapped and emitted
            assertEquals(1, state.materials.size)
            assertEquals("iron_ore", state.materials.first().id)

            assertEquals(CraftingTab.ENHANCE, state.selectedTab)
            assertNull(state.selectedItem)
            assertFalse(state.isProcessing)
            assertNull(state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 2. TAB AND ITEM SELECTION
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test selectTab and selectItem update state properly and reset user message`() = runTest {
        val sword = createTestItem(id = "sword_1")

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(CraftingTab.ENHANCE, initialState.selectedTab)
            assertNull(initialState.selectedItem)

            // Select tab
            viewModel.selectTab(CraftingTab.SOCKET)
            var tabState = awaitItem()
            while (tabState.selectedTab != CraftingTab.SOCKET) {
                tabState = awaitItem()
            }
            assertEquals(CraftingTab.SOCKET, tabState.selectedTab)

            // Select item
            viewModel.selectItem(sword)
            var itemState = awaitItem()
            while (itemState.selectedItem != sword) {
                itemState = awaitItem()
            }
            assertEquals(sword, itemState.selectedItem)

            // Selecting another tab updates tab and clears user message
            viewModel.selectTab(CraftingTab.GEM_FUSION)
            var fusionState = awaitItem()
            while (fusionState.selectedTab != CraftingTab.GEM_FUSION) {
                fusionState = awaitItem()
            }
            assertEquals(CraftingTab.GEM_FUSION, fusionState.selectedTab)
            assertNull(fusionState.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. ENHANCE SELECTED ITEM
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test enhanceSelectedItem with no item selected sets error message`() = runTest {
        viewModel.uiState.test {
            awaitItem() // initial state

            viewModel.enhanceSelectedItem()
            val state = awaitStateWithMessage(R.string.craft_err_no_item_selected)

            assertEquals(R.string.craft_err_no_item_selected, state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test enhanceSelectedItem on max level item sets max enhancement error`() = runTest {
        val maxItem = createTestItem(enhancementLevel = 10)
        viewModel.selectItem(maxItem)

        viewModel.uiState.test {
            awaitItem()

            viewModel.enhanceSelectedItem()
            val state = awaitStateWithMessage(R.string.craft_err_max_enhancement)

            assertEquals(R.string.craft_err_max_enhancement, state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test enhanceSelectedItem success updates selected item and emits success message`() = runTest {
        val item = createTestItem(id = "sword_1", enhancementLevel = 2)
        val enhancedItem = item.copy(enhancementLevel = 3)

        inventoryFlow.value = listOf(item)
        whenever(repository.enhanceEquipment(any<Item>())).thenAnswer {
            inventoryFlow.value = listOf(enhancedItem)
            Result.success(enhancedItem)
        }

        viewModel.selectItem(item)

        viewModel.uiState.test {
            awaitItem()

            viewModel.enhanceSelectedItem()
            val state = awaitStateWithMessage(R.string.craft_msg_enhance_success)

            assertEquals(3, state.selectedItem?.enhancementLevel)
            assertEquals(R.string.craft_msg_enhance_success, state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test enhanceSelectedItem failure maps error messages appropriately`() = runTest {
        val item = createTestItem(id = "sword_1", enhancementLevel = 1)

        // Test Gold Failure
        whenever(repository.enhanceEquipment(any<Item>())).thenReturn(Result.failure(Exception("Insufficient gold")))
        viewModel.selectItem(item)

        viewModel.uiState.test {
            awaitItem()

            viewModel.enhanceSelectedItem()
            val state = awaitStateWithMessage(R.string.craft_err_insufficient_gold)

            assertEquals(R.string.craft_err_insufficient_gold, state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }

        // Test Ore/Material Failure
        whenever(repository.enhanceEquipment(any<Item>())).thenReturn(Result.failure(Exception("Insufficient iron_ore material")))

        viewModel.uiState.test {
            awaitItem()

            viewModel.enhanceSelectedItem()
            val state = awaitStateWithMessage(R.string.craft_err_insufficient_materials)

            assertEquals(R.string.craft_err_insufficient_materials, state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 4. ADD SOCKET TO SELECTED ITEM
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test addSocketToSelectedItem with no item selected sets error message`() = runTest {
        viewModel.uiState.test {
            awaitItem()

            viewModel.addSocketToSelectedItem()
            val state = awaitStateWithMessage(R.string.craft_err_no_item_selected)

            assertEquals(R.string.craft_err_no_item_selected, state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test addSocketToSelectedItem when max sockets reached sets max socket error`() = runTest {
        val fullWeapon = createTestItem(
            slot = ItemSlot.WEAPON,
            sockets = listOf(SocketSlot("s1"), SocketSlot("s2"), SocketSlot("s3")),
        )

        viewModel.selectItem(fullWeapon)

        viewModel.uiState.test {
            awaitItem()

            viewModel.addSocketToSelectedItem()
            val state = awaitStateWithMessage(R.string.craft_err_max_sockets)

            assertEquals(R.string.craft_err_max_sockets, state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }

        val fullAccessory = createTestItem(
            slot = ItemSlot.ACCESSORY,
            sockets = listOf(SocketSlot("s1")),
        )

        viewModel.selectItem(fullAccessory)

        viewModel.uiState.test {
            awaitItem()

            viewModel.addSocketToSelectedItem()
            val state = awaitStateWithMessage(R.string.craft_err_max_sockets)

            assertEquals(R.string.craft_err_max_sockets, state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test addSocketToSelectedItem success appends slot and sets success message`() = runTest {
        val weapon = createTestItem(id = "sword_1", sockets = emptyList())
        val socketedWeapon = weapon.copy(sockets = listOf(SocketSlot("s1")))

        inventoryFlow.value = listOf(weapon)
        whenever(repository.addSocketSlot(any<Item>())).thenAnswer {
            inventoryFlow.value = listOf(socketedWeapon)
            Result.success(socketedWeapon)
        }

        viewModel.selectItem(weapon)

        viewModel.uiState.test {
            awaitItem()

            viewModel.addSocketToSelectedItem()
            val state = awaitStateWithMessage(R.string.craft_msg_socket_success)

            assertEquals(1, state.selectedItem?.sockets?.size)
            assertEquals(R.string.craft_msg_socket_success, state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 5. CRAFT OR FUSE GEM
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test craftOrFuseGem fuses gems when sourceGemId is present`() = runTest {
        whenever(repository.fuseGems(any(), any(), any())).thenReturn(Result.success(Unit))

        viewModel.uiState.test {
            awaitItem()

            viewModel.craftOrFuseGem("ruby_gem_1", "ruby_gem_2")
            val state = awaitStateWithMessage(R.string.craft_msg_gem_fused)

            assertEquals(R.string.craft_msg_gem_fused, state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test craftOrFuseGem crafts gem when sourceGemId is blank`() = runTest {
        whenever(repository.craftGem(any(), any())).thenReturn(Result.success(Unit))

        viewModel.uiState.test {
            awaitItem()

            viewModel.craftOrFuseGem("", "ruby_gem_1")
            val state = awaitStateWithMessage(R.string.craft_msg_gem_crafted)

            assertEquals(R.string.craft_msg_gem_crafted, state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test craftOrFuseGem failure sets error message`() = runTest {
        whenever(repository.fuseGems(any(), any(), any()))
            .thenReturn(Result.failure(Exception("Insufficient gems")))

        viewModel.uiState.test {
            awaitItem()

            viewModel.craftOrFuseGem("ruby_gem_1", "ruby_gem_2")
            val state = awaitStateWithMessage(R.string.craft_err_insufficient_materials)

            assertEquals(R.string.craft_err_insufficient_materials, state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 6. INSERT GEM AND UNCAP HERO
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `test insertGemIntoSelectedItem success updates item and sets message`() = runTest {
        val weapon = createTestItem(id = "sword_1", sockets = listOf(SocketSlot("s1")))
        val socketedWeapon = weapon.copy(sockets = listOf(SocketSlot("s1")))

        inventoryFlow.value = listOf(weapon)
        whenever(repository.insertGem(any(), any(), any())).thenAnswer {
            inventoryFlow.value = listOf(socketedWeapon)
            Result.success(socketedWeapon)
        }

        viewModel.selectItem(weapon)

        viewModel.uiState.test {
            awaitItem()

            viewModel.insertGemIntoSelectedItem(0, "ruby_gem_1")
            val state = awaitStateWithMessage(R.string.craft_msg_gem_inserted)

            assertEquals(R.string.craft_msg_gem_inserted, state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test uncapHero success sets uncapped message`() = runTest {
        val hero = Hero(
            id = "hero_1",
            heroClass = HeroClass.WARRIOR,
            name = "Cecil",
            currentHp = 200,
            currentMp = 50,
            aiPriority = AIPriority.ATTACK,
        )

        whenever(repository.uncapHeroLevel(any())).thenReturn(Result.success(hero))

        viewModel.uiState.test {
            awaitItem()

            viewModel.uncapHero("hero_1")
            val state = awaitStateWithMessage(R.string.craft_msg_hero_uncapped)

            assertEquals(R.string.craft_msg_hero_uncapped, state.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test clearUserMessage resets userMessageRes to null`() = runTest {
        viewModel.uiState.test {
            awaitItem()

            viewModel.enhanceSelectedItem() // trigger no item error
            val errorState = awaitStateWithMessage(R.string.craft_err_no_item_selected)
            assertEquals(R.string.craft_err_no_item_selected, errorState.userMessageRes)

            viewModel.clearUserMessage()
            val clearedState = awaitItem()
            assertNull(clearedState.userMessageRes)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
