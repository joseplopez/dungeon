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

    fun upgradeBuilding(type: UpgradeType) {
        val gs = gameState.value ?: return
        val currentLevel = when (type) {
            UpgradeType.INN -> gs.innLevel
            UpgradeType.BARRACKS -> gs.barracksLevel
            UpgradeType.VAULT -> gs.vaultLevel
            UpgradeType.ARMORY -> gs.armoryLevel
            UpgradeType.MAGIC_SHOP -> gs.magicShopLevel
            UpgradeType.TRAINING -> gs.trainingLevel
            UpgradeType.PLANNING -> gs.planningLevel
            UpgradeType.CLINIC -> gs.clinicLevel
            UpgradeType.PATHFINDER -> gs.pathfinderLevel
        }

        if (currentLevel >= type.maxLevel) return

        val rawCost = type.baseCost * (currentLevel + 1)
        val discount = gs.upgradeDiscount
        val finalCost = (rawCost * (1f - discount)).toLong()

        if (gs.gold >= finalCost) {
            val nextGs = when (type) {
                UpgradeType.INN -> gs.copy(innLevel = gs.innLevel + 1)
                UpgradeType.BARRACKS -> gs.copy(barracksLevel = gs.barracksLevel + 1)
                UpgradeType.VAULT -> gs.copy(vaultLevel = gs.vaultLevel + 1)
                UpgradeType.ARMORY -> gs.copy(armoryLevel = gs.armoryLevel + 1)
                UpgradeType.MAGIC_SHOP -> gs.copy(magicShopLevel = gs.magicShopLevel + 1)
                UpgradeType.TRAINING -> gs.copy(trainingLevel = gs.trainingLevel + 1)
                UpgradeType.PLANNING -> gs.copy(planningLevel = gs.planningLevel + 1)
                UpgradeType.CLINIC -> gs.copy(clinicLevel = gs.clinicLevel + 1)
                UpgradeType.PATHFINDER -> gs.copy(pathfinderLevel = gs.pathfinderLevel + 1)
            }
            viewModelScope.launch {
                repository.saveGameState(nextGs.copy(gold = gs.gold - finalCost))
            }
        }
    }

    data class CrystalData(
        val color: CrystalColor,
        val unlocksJob: HeroClass
    )
}
