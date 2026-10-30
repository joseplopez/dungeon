package com.game.dungeon.ui.viewmodels

import android.app.Activity
import android.content.Context
import app.cash.turbine.test
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Rarity
import com.game.dungeon.data.repository.GameRepository
import com.game.dungeon.monetization.BillingManager
import com.game.dungeon.monetization.InAppProduct
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GachaponViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: GameRepository
    private lateinit var billingManager: BillingManager
    private lateinit var mockContext: Context
    private lateinit var mockActivity: Activity
    private lateinit var viewModel: GachaponViewModel

    private val gameStateFlow = MutableStateFlow<GameState?>(
        GameState(
            id = 1,
            highestFloor = 150,
            totalGilEarned = 50_000L,
            lastFreeGachaTime = 0L
        )
    )

    @Before
    fun setUp() {
        repository = mock()
        billingManager = mock()
        mockContext = mock()
        mockActivity = mock()

        whenever(repository.getGameState()).thenReturn(gameStateFlow)
        org.mockito.kotlin.wheneverBlocking { repository.getGameStateOnce() }.thenAnswer { gameStateFlow.value }
        org.mockito.kotlin.wheneverBlocking { repository.recordGachaSpin(any()) }.thenAnswer { invocation ->
            val isFree = invocation.arguments[0] as Boolean
            val current = gameStateFlow.value ?: GameState()
            val isLegendary = current.gachaPityCounter >= 9
            val nextPity = if (isLegendary) 0 else current.gachaPityCounter + 1
            val updated = current.copy(
                lastFreeGachaTime = if (isFree) System.currentTimeMillis() else current.lastFreeGachaTime,
                gachaPityCounter = nextPity
            )
            gameStateFlow.value = updated
            updated
        }

        whenever(mockContext.getString(any())).thenAnswer { invocation ->
            val resId = invocation.arguments[0] as Int
            "MockRes_$resId"
        }

        viewModel = GachaponViewModel(repository, billingManager)
    }

    @Test
    fun `test initial state emissions and carousel initialization`() = runTest {
        viewModel.ensureCarouselInitialized(mockContext)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(GachaBanner.ARMORY, state.selectedBanner)
            assertTrue(state.isFreeAvailable)
            assertEquals(0L, state.timeUntilNextFreeMs)
            assertFalse(state.isSpinning)
            assertFalse(state.isBillingInProcess)
            assertTrue(state.carouselItems.isNotEmpty())
            assertNull(state.pendingClaimReward)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test banner selection updates carousel display items`() = runTest {
        viewModel.ensureCarouselInitialized(mockContext)

        viewModel.uiState.test {
            awaitItem() // initial state

            viewModel.selectBanner(GachaBanner.ALCHEMIST, mockContext)
            val stateAlchemist = awaitItem()
            assertEquals(GachaBanner.ALCHEMIST, stateAlchemist.selectedBanner)
            assertTrue(stateAlchemist.carouselItems.isNotEmpty())

            viewModel.selectBanner(GachaBanner.TREASURY, mockContext)
            val stateTreasury = awaitItem()
            assertEquals(GachaBanner.TREASURY, stateTreasury.selectedBanner)
            assertTrue(stateTreasury.carouselItems.isNotEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test free spin triggers animation and pre-determines winning item`() = runTest {
        viewModel.ensureCarouselInitialized(mockContext)

        viewModel.spin(mockActivity)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isSpinning)
            assertNotNull(state.winningReward)
            assertEquals(25, state.targetWinningIndex)
            assertEquals(state.winningReward, state.carouselItems[25])

            cancelAndIgnoreRemainingEvents()
        }

        verify(repository).recordGachaSpin(eq(true))
    }

    @Test
    fun `test paid spin launches billing flow when free spin is on cooldown`() = runTest {
        // Set lastFreeGachaTime to current time so free spin is unavailable
        val recentTime = System.currentTimeMillis()
        gameStateFlow.value = GameState(
            highestFloor = 200,
            totalGilEarned = 100_000L,
            lastFreeGachaTime = recentTime
        )

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.isFreeAvailable) {
                state = awaitItem()
            }
            assertFalse(state.isFreeAvailable)

            viewModel.spin(mockActivity)

            // Billing flow should be launched
            verify(billingManager).launchBillingFlow(
                eq(mockActivity),
                eq(InAppProduct.MISC_PRODUCT),
                any(),
                any()
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test paid spin increments pity counter when free spin is on cooldown`() = runTest {
        val recentTime = System.currentTimeMillis()
        val gsOnCooldown = GameState(
            highestFloor = 200,
            totalGilEarned = 100_000L,
            lastFreeGachaTime = recentTime,
            gachaPityCounter = 3
        )
        gameStateFlow.value = gsOnCooldown

        whenever(billingManager.launchBillingFlow(any(), any(), any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val onSuccess = invocation.arguments[2] as (InAppProduct) -> Unit
            onSuccess(InAppProduct.MISC_PRODUCT)
        }

        viewModel.spin(mockActivity)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isSpinning)
            assertEquals(4, state.gachaPityCounter)
            cancelAndIgnoreRemainingEvents()
        }

        verify(repository).recordGachaSpin(eq(false))
    }

    @Test
    fun `test spin animation completion sets pending claim reward`() = runTest {
        viewModel.ensureCarouselInitialized(mockContext)
        viewModel.spin(mockActivity)

        val winningReward = viewModel.uiState.value.winningReward
        assertNotNull(winningReward)

        viewModel.onSpinAnimationComplete()

        val state = viewModel.uiState.value
        assertFalse(state.isSpinning)
        assertEquals(winningReward, state.pendingClaimReward)
    }

    @Test
    fun `test claiming gear reward saves item to repository and clears pending state`() = runTest {
        viewModel.ensureCarouselInitialized(mockContext)
        viewModel.spin(mockActivity)
        viewModel.onSpinAnimationComplete()

        val winning = viewModel.uiState.value.pendingClaimReward
        assertNotNull(winning)

        viewModel.claimPendingReward()

        if (winning is GachaReward.GearReward) {
            verify(repository).saveItem(winning.item)
        }

        assertNull(viewModel.uiState.value.pendingClaimReward)
        assertNull(viewModel.uiState.value.winningReward)
    }

    @Test
    fun `test armory loot generation level and rarity requirements`() = runTest {
        // Armory floor must be highestFloor + 100, and exclude COMMON
        val gs = GameState(highestFloor = 250, totalGilEarned = 10_000L)
        gameStateFlow.value = gs
        viewModel.selectBanner(GachaBanner.ARMORY, mockContext)

        for (i in 0 until 20) {
            viewModel.spin(mockActivity)
            val winning = viewModel.uiState.value.winningReward
            assertTrue(winning is GachaReward.GearReward)

            val gearReward = winning as GachaReward.GearReward
            assertEquals(350, gearReward.item.floorFound) // 250 + 100
            assertTrue("Gear rarity must strictly exclude COMMON", gearReward.item.rarity != Rarity.COMMON)

            viewModel.onSpinAnimationComplete()
            viewModel.claimPendingReward()
        }
    }

    @Test
    fun `test treasury loot generation enforces minimum flat 100 gil baseline`() = runTest {
        // totalGilEarned = 0L -> 10% of 0 is 0, but baseline must be 100 Gil
        val gs = GameState(highestFloor = 10, totalGilEarned = 0L)
        gameStateFlow.value = gs
        viewModel.selectBanner(GachaBanner.TREASURY, mockContext)

        for (i in 0 until 20) {
            viewModel.spin(mockActivity)
            val winning = viewModel.uiState.value.winningReward
            assertNotNull(winning)

            if (winning is GachaReward.GilReward) {
                assertTrue("Gil reward must enforce minimum 100 Gil baseline", winning.amount >= 100L)
            }

            viewModel.onSpinAnimationComplete()
            viewModel.claimPendingReward()
        }
    }

    @Test
    fun `test pity counter reaching 9 flags legendary pull and resets after legendary spin`() = runTest {
        val gsWithPity = GameState(
            highestFloor = 100,
            totalGilEarned = 10_000L,
            lastFreeGachaTime = 0L,
            gachaPityCounter = 9
        )
        gameStateFlow.value = gsWithPity

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(9, state.gachaPityCounter)
            assertTrue(state.isLegendaryPull)

            viewModel.spin(mockActivity)

            val spinState = awaitItem()
            assertTrue(spinState.isSpinning)
            assertTrue(spinState.isLegendaryPull)

            cancelAndIgnoreRemainingEvents()
        }

        verify(repository).recordGachaSpin(any())
    }

    @Test
    fun `test legendary armory pull strictly produces legendary or mythic`() = runTest {
        val gs = GameState(highestFloor = 200, gachaPityCounter = 9)
        gameStateFlow.value = gs
        viewModel.selectBanner(GachaBanner.ARMORY, mockContext)

        for (i in 0 until 10) {
            viewModel.spin(mockActivity)
            val winning = viewModel.uiState.value.winningReward
            assertTrue(winning is GachaReward.GearReward)

            val gear = winning as GachaReward.GearReward
            assertTrue(
                "Legendary pull must strictly produce LEGENDARY or MYTHIC",
                gear.item.rarity == Rarity.LEGENDARY || gear.item.rarity == Rarity.MYTHIC
            )

            viewModel.onSpinAnimationComplete()
            viewModel.claimPendingReward()
        }
    }

    @Test
    fun `test legendary alchemist pull produces high tier material with boosted multiplier`() = runTest {
        val gs = GameState(highestFloor = 200, gachaPityCounter = 9)
        gameStateFlow.value = gs
        viewModel.selectBanner(GachaBanner.ALCHEMIST, mockContext)

        viewModel.spin(mockActivity)
        val winning = viewModel.uiState.value.winningReward
        assertTrue(winning is GachaReward.MaterialReward)

        val mat = winning as GachaReward.MaterialReward
        assertTrue(
            "Legendary Alchemist pull must yield LEGENDARY or MYTHIC material",
            mat.rarity == Rarity.LEGENDARY || mat.rarity == Rarity.MYTHIC
        )
        assertTrue("Material amount should be multi-dropped (at least 3)", mat.amount >= 3)
    }

    @Test
    fun `test legendary treasury pull forces jackpot tiers and excludes low 10 percent option`() = runTest {
        val gs = GameState(highestFloor = 200, totalGilEarned = 1_000_000L, gachaPityCounter = 9)
        gameStateFlow.value = gs
        viewModel.selectBanner(GachaBanner.TREASURY, mockContext)

        for (i in 0 until 10) {
            viewModel.spin(mockActivity)
            val winning = viewModel.uiState.value.winningReward
            assertNotNull(winning)

            if (winning is GachaReward.GilReward) {
                assertTrue(
                    "Legendary Gil reward must be Major (50%) or Jackpot (100%)",
                    winning.amount >= 500_000L
                )
            } else if (winning is GachaReward.MagiciteReward) {
                assertTrue(
                    "Legendary Magicite reward must be 500 or 2000",
                    winning.amount >= 500L
                )
            }

            viewModel.onSpinAnimationComplete()
            viewModel.claimPendingReward()
        }
    }
}
