package com.game.dungeon.ui.viewmodels

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.analytics.AnalyticsManager
import com.game.dungeon.data.models.*
import com.game.dungeon.data.repository.GameRepository
import com.game.dungeon.monetization.AdManager
import com.game.dungeon.monetization.BillingManager
import com.game.dungeon.monetization.InAppProduct
import com.game.dungeon.monetization.ResourceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TownViewModel @Inject constructor(
    private val repository: GameRepository,
    private val analytics: AnalyticsManager,
    private val adManager: AdManager,
    val billingManager: BillingManager
) : ViewModel() {

    val gameState = repository.getGameState().stateIn(viewModelScope, SharingStarted.Eagerly, GameState())

    private val _showResourceShop = MutableStateFlow(false)
    val showResourceShop = _showResourceShop.asStateFlow()

    private val _resourceShopType = MutableStateFlow(ResourceType.GIL)
    val resourceShopType = _resourceShopType.asStateFlow()

    fun openResourceShop(type: ResourceType) {
        _resourceShopType.value = type
        _showResourceShop.value = true
    }

    fun closeResourceShop() {
        _showResourceShop.value = false
    }

    fun buyProduct(activity: Activity, product: InAppProduct) {
        billingManager.launchBillingFlow(
            activity = activity,
            product = product,
            onSuccess = { boughtProduct ->
                val message = if (boughtProduct.resourceType == ResourceType.GIL) {
                    activity.getString(com.game.dungeon.R.string.purchase_success_gil, com.game.dungeon.ui.components.formatGold(boughtProduct.rewardAmount))
                } else {
                    activity.getString(com.game.dungeon.R.string.purchase_success_magicite, boughtProduct.rewardAmount.toInt())
                }
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            },
            onError = { error ->
                val message = if (error == "Cancelled") {
                    activity.getString(com.game.dungeon.R.string.purchase_failed)
                } else {
                    error
                }
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    val availableCrystals = gameState.map { gs ->
        HeroClass.entries.filter { it.crystalColor != CrystalColor.CLEAR }
            .filter { job ->
                if (job.tier >= 2) (gs?.innLevel ?: 0) >= 1 else true
            }
            .filter { job ->
                if (job.tier == 3) gs?.isJobDiscovered(job) == true else true
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
            analytics.logCrystalPurchased(color.displayName, HeroClass.entries.first { it.crystalColor == color }.name)
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
            analytics.logTownUpgrade(type.name, currentLevel + 1)
            viewModelScope.launch {
                repository.saveGameState(nextGs.copy(gold = gs.gold - finalCost))
            }
        }
    }

    fun watchGilAd(activity: Activity) {
        adManager.showRewardedAd(activity) {
            viewModelScope.launch {
                val currentGs = repository.getGameStateOnce() ?: return@launch
                val reward = (currentGs.totalGilEarned * 0.005f).toLong().coerceAtLeast(50L)
                repository.addGil(reward)
            }
        }
    }

    fun watchMagiciteAd(activity: Activity) {
        adManager.showRewardedAd(activity) {
            viewModelScope.launch {
                val currentGs = repository.getGameStateOnce() ?: return@launch
                val reward = (currentGs.totalMagiciteEarned * 0.01f).toInt().coerceAtLeast(5)
                repository.addMagicite(reward)
            }
        }
    }

    data class CrystalData(
        val color: CrystalColor,
        val unlocksJob: HeroClass
    )
}
