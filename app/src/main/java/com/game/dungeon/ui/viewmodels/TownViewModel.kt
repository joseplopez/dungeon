package com.game.dungeon.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.data.models.*
import com.game.dungeon.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TownViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    val gameState = repository.getGameState().stateIn(viewModelScope, SharingStarted.Eagerly, GameState())

    val availableCrystals = gameState.map { gs ->
        HeroClass.entries.filter { it.crystalColor != CrystalColor.CLEAR }
            .filter { job ->
                // Basic crystals are always available.
                // Advanced crystals (Tier 2+) require Inn Level >= 1
                if (job.tier >= 2) (gs?.innLevel ?: 0) >= 1 else true
            }
            .map { it.crystalColor }
            .distinct()
            .map { color ->
                CrystalData(
                    color = color,
                    unlocksJob = HeroClass.entries.first { it.crystalColor == color }
                )
            }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun buyCrystal(color: CrystalColor) {
        val gs = gameState.value ?: return
        if (gs.gold >= color.baseCost && gs.crystals[color] != true) {
            val newCrystals = gs.crystals.toMutableMap().apply { put(color, true) }
            val newUnlockedJobs = gs.unlockedJobs.toMutableSet().apply {
                addAll(HeroClass.entries.filter { it.crystalColor == color })
            }
            viewModelScope.launch {
                repository.saveGameState(gs.copy(
                    gold = gs.gold - color.baseCost,
                    crystals = newCrystals,
                    unlockedJobs = newUnlockedJobs
                ))
            }
        }
    }

    fun upgradeInn() = upgrade { copy(innLevel = innLevel + 1) }
    fun upgradeArmory() = upgrade { copy(armoryLevel = armoryLevel + 1) }
    fun upgradeMagicShop() = upgrade { copy(magicShopLevel = magicShopLevel + 1) }
    fun upgradeBarracks() = upgrade { copy(barracksLevel = barracksLevel + 1) }
    fun upgradeVault() = upgrade { copy(vaultLevel = vaultLevel + 1) }
    fun upgradePathfinder() = upgrade { copy(pathfinderLevel = pathfinderLevel + 1) }

    private fun upgrade(block: GameState.() -> GameState) {
        val gs = gameState.value ?: return
        val cost = 500 // Simplified cost for now
        if (gs.gold >= cost) {
            viewModelScope.launch {
                repository.saveGameState(gs.block().copy(gold = gs.gold - cost))
            }
        }
    }

    data class CrystalData(
        val color: CrystalColor,
        val unlocksJob: HeroClass
    )
}
