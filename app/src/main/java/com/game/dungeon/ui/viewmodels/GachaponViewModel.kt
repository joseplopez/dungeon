package com.game.dungeon.ui.viewmodels

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.R
import com.game.dungeon.data.models.*
import com.game.dungeon.data.repository.GameRepository
import com.game.dungeon.monetization.AdManager
import com.game.dungeon.monetization.BillingManager
import com.game.dungeon.monetization.InAppProduct
import com.game.dungeon.ui.components.formatGold
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

enum class GachaBanner {
    ARMORY,    // Gear
    ALCHEMIST, // Materials
    TREASURY   // Wealth (Gil & Magicite)
}

sealed interface GachaReward {
    val id: String
    val name: String
    val rarity: Rarity
    val emoji: String
    val description: String

    data class GearReward(
        val item: Item
    ) : GachaReward {
        override val id: String = item.id
        override val name: String = item.name
        override val rarity: Rarity = item.rarity
        override val emoji: String = item.emoji
        override val description: String = "Slot: ${item.slot.name} • Lvl ${item.floorFound}"
    }

    data class MaterialReward(
        val material: Material,
        val amount: Int,
        val materialName: String
    ) : GachaReward {
        override val id: String = "${material.id}_${java.util.UUID.randomUUID()}"
        override val name: String = "$amount x$materialName"
        override val rarity: Rarity = material.rarity
        override val emoji: String = material.emoji
        override val description: String = "Crafting Material ($amount)"
    }

    data class GilReward(
        val amount: Long,
        val tierLabel: String,
        override val rarity: Rarity = Rarity.RARE
    ) : GachaReward {
        override val id: String = "gil_${java.util.UUID.randomUUID()}"
        override val name: String = "${formatGold(amount)} Gil"
        override val emoji: String = "🪙"
        override val description: String = tierLabel
    }

    data class MagiciteReward(
        val amount: Long,
        override val rarity: Rarity = Rarity.EPIC
    ) : GachaReward {
        override val id: String = "magicite_${java.util.UUID.randomUUID()}"
        override val name: String = "$amount Magicite"
        override val emoji: String = "💎"
        override val description: String = "Cosmic Currency"
    }
}

data class GachaponUiState(
    val selectedBanner: GachaBanner = GachaBanner.ARMORY,
    val isFreeAvailable: Boolean = true,
    val timeUntilNextFreeMs: Long = 0L,
    val gachaTickets: Int = 0,
    val gachaPityCounter: Int = 0,
    val isLegendaryPull: Boolean = false,
    val isSpinning: Boolean = false,
    val isBillingInProcess: Boolean = false,
    val carouselItems: List<GachaReward> = emptyList(),
    val targetWinningIndex: Int = 25,
    val winningReward: GachaReward? = null,
    val pendingClaimReward: GachaReward? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class GachaponViewModel @Inject constructor(
    private val repository: GameRepository,
    val billingManager: BillingManager,
    private val adManager: AdManager,
) : ViewModel() {

    val gameState = repository.getGameState()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameState())

    private val _uiState = MutableStateFlow(GachaponUiState())
    val uiState: StateFlow<GachaponUiState> = _uiState.asStateFlow()

    init {
        // Observe GameState for free spin eligibility, tickets, pity counter, and countdown
        viewModelScope.launch {
            gameState.collect { gs ->
                val currentGs = gs ?: GameState()
                val freeAvailable = isFreeSpinAvailable(currentGs.lastFreeGachaTime)
                val timeRemaining = getTimeUntilNextFree(currentGs.lastFreeGachaTime)
                val pity = currentGs.gachaPityCounter
                val tickets = currentGs.gachaTickets

                _uiState.update { state ->
                    val isLegendary = pity >= 9 || (state.isSpinning && state.isLegendaryPull)
                    state.copy(
                        isFreeAvailable = freeAvailable,
                        timeUntilNextFreeMs = timeRemaining,
                        gachaTickets = tickets,
                        gachaPityCounter = pity,
                        isLegendaryPull = isLegendary
                    )
                }
            }
        }

        // Periodic ticker for countdown timer
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val gs = gameState.value ?: GameState()
                val freeAvailable = isFreeSpinAvailable(gs.lastFreeGachaTime)
                val timeRemaining = getTimeUntilNextFree(gs.lastFreeGachaTime)
                val pity = gs.gachaPityCounter
                _uiState.update { state ->
                    val isLegendary = pity >= 9 || (state.isSpinning && state.isLegendaryPull)
                    state.copy(
                        isFreeAvailable = freeAvailable,
                        timeUntilNextFreeMs = timeRemaining,
                        // Keep whichever ticket count is higher until DB flow catches up
                        gachaTickets = maxOf(gs.gachaTickets, state.gachaTickets),
                        gachaPityCounter = pity,
                        isLegendaryPull = isLegendary
                    )
                }
            }
        }
    }

    fun selectBanner(banner: GachaBanner, context: Context) {
        if (_uiState.value.isSpinning || _uiState.value.isBillingInProcess) return
        val gs = gameState.value ?: GameState()
        val isLegendary = gs.gachaPityCounter >= 9
        val dummyList = generateCarouselList(banner, context, gs, winningReward = null, targetIndex = 25, isLegendaryPull = isLegendary)
        _uiState.update {
            it.copy(
                selectedBanner = banner,
                carouselItems = dummyList,
                winningReward = null
            )
        }
    }

    // 1. Watch Ad for +1 Ticket
    fun watchAdForTicket(activity: Activity) {
        if (_uiState.value.isSpinning || _uiState.value.isBillingInProcess) return
        adManager.showRewardedAd(activity) {
            viewModelScope.launch {
                val currentGs = repository.getGameStateOnce() ?: gameState.value ?: GameState()
                val targetId = if (currentGs.id != 0) currentGs.id else 1
                val newTicketCount = currentGs.gachaTickets + 1
                val updatedGs = currentGs.copy(id = targetId, gachaTickets = newTicketCount)

                repository.saveGameState(updatedGs)
                _uiState.update { it.copy(gachaTickets = newTicketCount) }
                Toast.makeText(activity, "+1 Gachapon Ticket!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun buyTenTickets(activity: Activity) {
        if (_uiState.value.isSpinning || _uiState.value.isBillingInProcess) return
        _uiState.update { it.copy(isBillingInProcess = true, errorMessage = null) }

        billingManager.launchBillingFlow(
            activity = activity,
            product = InAppProduct.MISC_PRODUCT,
            onSuccess = {
                viewModelScope.launch {
                    // CRITICAL FIX: Wait for BillingManager to finish its own GameState saves
                    // This prevents the BillingManager from overwriting our tickets with an old snapshot.
                    delay(800)

                    val currentGs = repository.getGameStateOnce() ?: gameState.value ?: GameState()
                    val targetId = if (currentGs.id != 0) currentGs.id else 1
                    val newTicketCount = currentGs.gachaTickets + 10
                    val updatedGs = currentGs.copy(id = targetId, gachaTickets = newTicketCount)

                    repository.saveGameState(updatedGs)

                    _uiState.update { state ->
                        state.copy(
                            isBillingInProcess = false,
                            gachaTickets = newTicketCount
                        )
                    }
                    Toast.makeText(activity, "+10 Gachapon Tickets!", Toast.LENGTH_SHORT).show()
                }
            },
            onError = { err ->
                _uiState.update { state ->
                    state.copy(
                        isBillingInProcess = false,
                        errorMessage = if (err == "Cancelled") null else err
                    )
                }
            }
        )
    }

    fun ensureCarouselInitialized(context: Context) {
        if (_uiState.value.carouselItems.isEmpty() && !_uiState.value.isSpinning) {
            val gs = gameState.value ?: GameState()
            val isLegendary = gs.gachaPityCounter >= 9
            val dummyList = generateCarouselList(_uiState.value.selectedBanner, context, gs, winningReward = null, targetIndex = 25, isLegendaryPull = isLegendary)
            _uiState.update { it.copy(carouselItems = dummyList) }
        }
    }

    fun spin(activity: Activity) {
        val currentState = _uiState.value
        if (currentState.isSpinning || currentState.isBillingInProcess) return

        val isFree = currentState.isFreeAvailable
        val tickets = currentState.gachaTickets

        if (!isFree && tickets <= 0) {
            _uiState.update { it.copy(errorMessage = "No tickets available! Watch an ad or buy a 10-ticket pack.") }
            return
        }

        viewModelScope.launch {
            val gsBefore = repository.getGameStateOnce() ?: (gameState.value ?: GameState())
            val isLegendary = gsBefore.gachaPityCounter >= 9

            // Consume daily free spin without modifying ticket count, or deduct 1 ticket
            val updatedGs = if (isFree) {
                repository.recordGachaSpin(isFree = true)
            } else {
                val recordGs = repository.recordGachaSpin(isFree = false)
                val finalGs = recordGs.copy(gachaTickets = (recordGs.gachaTickets - 1).coerceAtLeast(0))
                repository.saveGameState(finalGs)
                finalGs
            }

            _uiState.update { state ->
                state.copy(
                    gachaPityCounter = updatedGs.gachaPityCounter,
                    gachaTickets = updatedGs.gachaTickets,
                    isFreeAvailable = isFreeSpinAvailable(updatedGs.lastFreeGachaTime),
                    errorMessage = null
                )
            }
            startSpinAnimation(activity, updatedGs, isLegendaryPull = isLegendary)
        }
    }

    private fun startSpinAnimation(context: Context, gs: GameState, isLegendaryPull: Boolean) {
        val banner = _uiState.value.selectedBanner
        val winning = generateWinningReward(banner, context, gs, isLegendaryPull)
        val targetIndex = 25
        val carouselList = generateCarouselList(banner, context, gs, winning, targetIndex, isLegendaryPull = isLegendaryPull)

        _uiState.update { state ->
            state.copy(
                isSpinning = true,
                isLegendaryPull = isLegendaryPull,
                carouselItems = carouselList,
                targetWinningIndex = targetIndex,
                winningReward = winning,
                errorMessage = null
            )
        }
    }

    fun onSpinAnimationComplete() {
        val winning = _uiState.value.winningReward ?: return
        _uiState.update { state ->
            state.copy(
                isSpinning = false,
                pendingClaimReward = winning
            )
        }
    }

    fun claimPendingReward() {
        val reward = _uiState.value.pendingClaimReward ?: return
        viewModelScope.launch {
            when (reward) {
                is GachaReward.GearReward -> {
                    repository.saveItem(reward.item)
                }
                is GachaReward.MaterialReward -> {
                    repository.addMaterial(reward.material.id, reward.amount)
                }
                is GachaReward.GilReward -> {
                    repository.addGil(reward.amount)
                }
                is GachaReward.MagiciteReward -> {
                    repository.addMagicite(reward.amount)
                }
            }
            _uiState.update { state ->
                state.copy(
                    pendingClaimReward = null,
                    winningReward = null
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // --- LOOT GENERATION & CAROUSEL BUILDERS ---

    private fun generateWinningReward(
        banner: GachaBanner,
        context: Context,
        gs: GameState,
        isLegendaryPull: Boolean = false
    ): GachaReward {
        return when (banner) {
            GachaBanner.ARMORY -> generateArmoryReward(context, gs, isLegendaryPull)
            GachaBanner.ALCHEMIST -> generateAlchemistReward(context, isLegendaryPull)
            GachaBanner.TREASURY -> generateTreasuryReward(gs, isLegendaryPull)
        }
    }

    private fun generateArmoryReward(
        context: Context,
        gs: GameState,
        isLegendaryPull: Boolean = false
    ): GachaReward.GearReward {
        val floorLevel = gs.highestFloor + 100
        val rarity = if (isLegendaryPull) {
            val roll = Random.nextInt(1, 101)
            if (roll <= 90) Rarity.LEGENDARY else Rarity.MYTHIC
        } else {
            val roll = Random.nextInt(1, 101)
            when {
                roll <= 60 -> Rarity.RARE       // 60%
                roll <= 90 -> Rarity.EPIC      // 30%
                roll <= 99 -> Rarity.LEGENDARY // 9%
                else -> Rarity.MYTHIC          // 1%
            }
        }
        val relicBonuses = RelicBonuses.from(gs)
        val baseItem = Item.random(
            floor = floorLevel,
            context = context,
            relicBonuses = relicBonuses,
            minRarity = rarity,
            dimension = gs.currentDimension
        )
        val item = baseItem.copy(rarity = rarity)
        return GachaReward.GearReward(item)
    }

    private fun generateAlchemistReward(
        context: Context,
        isLegendaryPull: Boolean = false
    ): GachaReward.MaterialReward {
        val targetRarity = if (isLegendaryPull) {
            val roll = Random.nextInt(1, 101)
            if (roll <= 80) Rarity.LEGENDARY else Rarity.MYTHIC
        } else {
            val roll = Random.nextInt(1, 101)
            when {
                roll <= 50 -> Rarity.COMMON    // 50%
                roll <= 80 -> Rarity.RARE      // 30%
                roll <= 95 -> Rarity.EPIC      // 15%
                roll <= 99 -> Rarity.LEGENDARY // 4%
                else -> Rarity.MYTHIC          // 1%
            }
        }
        val allMaterials = MaterialCatalog.getAllKnownMaterials()
        val matchingMaterials = allMaterials.filter { it.rarity == targetRarity }
        val selectedMaterial = if (matchingMaterials.isNotEmpty()) {
            matchingMaterials.random()
        } else {
            allMaterials.random()
        }

        val baseAmount = when (selectedMaterial.rarity) {
            Rarity.COMMON -> Random.nextInt(10, 26)
            Rarity.RARE -> Random.nextInt(5, 13)
            Rarity.EPIC -> Random.nextInt(3, 8)
            Rarity.LEGENDARY -> Random.nextInt(2, 5)
            Rarity.MYTHIC -> Random.nextInt(1, 3)
        }

        val multiplier = if (isLegendaryPull) Random.nextInt(3, 6) else 1
        val amount = baseAmount * multiplier

        val materialName = try {
            context.getString(selectedMaterial.nameRes)
        } catch (_: Exception) {
            selectedMaterial.id.replace("_", " ")
        }

        return GachaReward.MaterialReward(
            material = selectedMaterial,
            amount = amount,
            materialName = materialName
        )
    }

    private fun generateTreasuryReward(
        gs: GameState,
        isLegendaryPull: Boolean = false
    ): GachaReward {
        val isGil = Random.nextInt(100) < 80 // 80% Gil, 20% Magicite
        if (isGil) {
            val roll = Random.nextInt(1, 101)
            val (percent, tierLabel, rarity) = if (isLegendaryPull) {
                if (roll <= 50) {
                    Triple(0.50f, "Major Gold (50%)", Rarity.EPIC)
                } else {
                    Triple(1.00f, "JACKPOT Gold (100%)", Rarity.LEGENDARY)
                }
            } else {
                when {
                    roll <= 60 -> Triple(0.10f, "Minor Gold (10%)", Rarity.RARE)
                    roll <= 90 -> Triple(0.50f, "Major Gold (50%)", Rarity.EPIC)
                    else -> Triple(1.00f, "JACKPOT Gold (100%)", Rarity.LEGENDARY)
                }
            }
            val calculatedGil = (gs.totalGilEarned * percent).toLong()
            val finalAmount = calculatedGil.coerceAtLeast(100L) // Enforce flat 100 Gil minimum
            return GachaReward.GilReward(
                amount = finalAmount,
                tierLabel = tierLabel,
                rarity = rarity
            )
        } else {
            val roll = Random.nextInt(1, 101)
            val (amount, rarity) = if (isLegendaryPull) {
                if (roll <= 50) Pair(500L, Rarity.EPIC) else Pair(2000L, Rarity.LEGENDARY)
            } else {
                when {
                    roll <= 60 -> Pair(50L, Rarity.RARE)
                    roll <= 90 -> Pair(200L, Rarity.EPIC)
                    else -> Pair(1000L, Rarity.LEGENDARY)
                }
            }
            return GachaReward.MagiciteReward(amount = amount, rarity = rarity)
        }
    }

    private fun generateCarouselList(
        banner: GachaBanner,
        context: Context,
        gs: GameState,
        winningReward: GachaReward?,
        targetIndex: Int = 25,
        totalItems: Int = 36,
        isLegendaryPull: Boolean = false
    ): List<GachaReward> {
        val list = mutableListOf<GachaReward>()
        for (i in 0 until totalItems) {
            if (i == targetIndex && winningReward != null) {
                list.add(winningReward)
            } else {
                list.add(generateWinningReward(banner, context, gs, isLegendaryPull))
            }
        }
        return list
    }

    private fun isFreeSpinAvailable(lastFreeTime: Long): Boolean {
        if (lastFreeTime <= 0L) return true
        val now = System.currentTimeMillis()
        val oneDayMs = 86_400_000L
        return (now - lastFreeTime) >= oneDayMs
    }

    private fun getTimeUntilNextFree(lastFreeTime: Long): Long {
        if (lastFreeTime <= 0L) return 0L
        val now = System.currentTimeMillis()
        val oneDayMs = 86_400_000L
        val nextFree = lastFreeTime + oneDayMs
        return (nextFree - now).coerceAtLeast(0L)
    }
}