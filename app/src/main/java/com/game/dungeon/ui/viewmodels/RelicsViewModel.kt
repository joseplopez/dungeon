package com.game.dungeon.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.analytics.AnalyticsManager
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.RelicType
import com.game.dungeon.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RelicsViewModel @Inject constructor(
    private val repository: GameRepository,
    private val analytics: AnalyticsManager
) : ViewModel() {

    val gameState: StateFlow<GameState?> = repository.getGameState()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun upgradeRelic(type: RelicType, levels: Int = 1) {
        if (levels <= 0) return
        val gs = gameState.value ?: return
        val currentLevel = when (type) {
            RelicType.ATTACK -> gs.attackRelic
            RelicType.HP -> gs.hpRelic
            RelicType.MP -> gs.mpRelic
            RelicType.MAGIC -> gs.magicRelic
            RelicType.DEFENSE -> gs.defenseRelic
            RelicType.GOLD -> gs.goldRelic
            RelicType.MAGICITE_FIND -> gs.magiciteRelic
            RelicType.CRIT_CHANCE -> gs.critChanceRelic
            RelicType.CRIT_DAMAGE -> gs.critDamageRelic
            RelicType.MAGNET -> gs.magnetRelic
            RelicType.POCKETS -> gs.pocketsRelic
            RelicType.DOUBLE_LOOT -> gs.doubleLootRelic
        }

        val L = currentLevel.toLong()
        val N = levels.toLong()
        // Arithmetic progression sum for cost
        val cost = 5L * N * N + (10L * L + 5L) * N

        if (gs.magicite >= cost) {
            viewModelScope.launch {
                val currentGs = repository.getGameStateOnce() ?: gs
                val level = when (type) {
                    RelicType.ATTACK -> currentGs.attackRelic
                    RelicType.HP -> currentGs.hpRelic
                    RelicType.MP -> currentGs.mpRelic
                    RelicType.MAGIC -> currentGs.magicRelic
                    RelicType.DEFENSE -> currentGs.defenseRelic
                    RelicType.GOLD -> currentGs.goldRelic
                    RelicType.MAGICITE_FIND -> currentGs.magiciteRelic
                    RelicType.CRIT_CHANCE -> currentGs.critChanceRelic
                    RelicType.CRIT_DAMAGE -> currentGs.critDamageRelic
                    RelicType.MAGNET -> currentGs.magnetRelic
                    RelicType.POCKETS -> currentGs.pocketsRelic
                    RelicType.DOUBLE_LOOT -> currentGs.doubleLootRelic
                }

                val currentL = level.toLong()
                val costCurrent = 5L * N * N + (10L * currentL + 5L) * N

                if (currentGs.magicite >= costCurrent) {
                    val updatedGs = when (type) {
                        RelicType.ATTACK -> currentGs.copy(attackRelic = currentGs.attackRelic + levels)
                        RelicType.HP -> currentGs.copy(hpRelic = currentGs.hpRelic + levels)
                        RelicType.MP -> currentGs.copy(mpRelic = currentGs.mpRelic + levels)
                        RelicType.MAGIC -> currentGs.copy(magicRelic = currentGs.magicRelic + levels)
                        RelicType.DEFENSE -> currentGs.copy(defenseRelic = currentGs.defenseRelic + levels)
                        RelicType.GOLD -> currentGs.copy(goldRelic = currentGs.goldRelic + levels)
                        RelicType.MAGICITE_FIND -> currentGs.copy(magiciteRelic = currentGs.magiciteRelic + levels)
                        RelicType.CRIT_CHANCE -> currentGs.copy(critChanceRelic = currentGs.critChanceRelic + levels)
                        RelicType.CRIT_DAMAGE -> currentGs.copy(critDamageRelic = currentGs.critDamageRelic + levels)
                        RelicType.MAGNET -> currentGs.copy(magnetRelic = currentGs.magnetRelic + levels)
                        RelicType.POCKETS -> currentGs.copy(pocketsRelic = currentGs.pocketsRelic + levels)
                        RelicType.DOUBLE_LOOT -> currentGs.copy(doubleLootRelic = currentGs.doubleLootRelic + levels)
                    }.copy(magicite = currentGs.magicite - costCurrent)

                    analytics.logRelicUpgrade(type.name, level + levels)
                    repository.saveGameState(updatedGs)
                }
            }
        }
    }

    fun exchangeGoldForMagicite(goldCost: Long, magiciteAmount: Long) { // CHANGED to Long
        if (goldCost <= 0 || magiciteAmount <= 0L) return
        val gs = gameState.value ?: return
        if (gs.gold >= goldCost) {
            viewModelScope.launch {
                val currentGs = repository.getGameStateOnce() ?: gs
                if (currentGs.gold >= goldCost) {
                    val updatedGs = currentGs.copy(
                        gold = currentGs.gold - goldCost,
                        magicite = currentGs.magicite + magiciteAmount,
                        magiciteEarnedThisDim = currentGs.magiciteEarnedThisDim + magiciteAmount,
                        totalMagiciteEarned = currentGs.totalMagiciteEarned + magiciteAmount
                    )
                    val params = android.os.Bundle().apply {
                        putLong("gold_spent", goldCost)
                        putLong("magicite_gained", magiciteAmount) // CHANGED to putLong
                    }
                    analytics.logEvent("gold_gems_exchange", params)
                    repository.saveGameState(updatedGs)
                }
            }
        }
    }
}

fun calculateMaxRelicUpgrades(currentLevel: Int, maxMagicite: Long): Pair<Int, Long> {
    if (maxMagicite < (currentLevel + 1) * 10L) return 0 to 0L

    val L = currentLevel.toLong()
    val b = 10L * L + 5L
    val discriminant = (b * b) + 20L * maxMagicite

    // Solve the quadratic equation for N
    val n = ((-b + kotlin.math.sqrt(discriminant.toDouble())) / 10.0).toLong()
    if (n <= 0) return 0 to 0L

    val cost = 5L * n * n + b * n
    return n.toInt() to cost
}