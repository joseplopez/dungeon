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

    fun upgradeRelic(type: RelicType) {
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
        val cost = (currentLevel + 1) * 10

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
                val costCurrent = (level + 1) * 10

                if (currentGs.magicite >= costCurrent) {
                    val updatedGs = when (type) {
                        RelicType.ATTACK -> currentGs.copy(attackRelic = currentGs.attackRelic + 1)
                        RelicType.HP -> currentGs.copy(hpRelic = currentGs.hpRelic + 1)
                        RelicType.MP -> currentGs.copy(mpRelic = currentGs.mpRelic + 1)
                        RelicType.MAGIC -> currentGs.copy(magicRelic = currentGs.magicRelic + 1)
                        RelicType.DEFENSE -> currentGs.copy(defenseRelic = currentGs.defenseRelic + 1)
                        RelicType.GOLD -> currentGs.copy(goldRelic = currentGs.goldRelic + 1)
                        RelicType.MAGICITE_FIND -> currentGs.copy(magiciteRelic = currentGs.magiciteRelic + 1)
                        RelicType.CRIT_CHANCE -> currentGs.copy(critChanceRelic = currentGs.critChanceRelic + 1)
                        RelicType.CRIT_DAMAGE -> currentGs.copy(critDamageRelic = currentGs.critDamageRelic + 1)
                        RelicType.MAGNET -> currentGs.copy(magnetRelic = currentGs.magnetRelic + 1)
                        RelicType.POCKETS -> currentGs.copy(pocketsRelic = currentGs.pocketsRelic + 1)
                        RelicType.DOUBLE_LOOT -> currentGs.copy(doubleLootRelic = currentGs.doubleLootRelic + 1)
                    }.copy(magicite = currentGs.magicite - costCurrent)

                    analytics.logRelicUpgrade(type.name, level + 1)
                    repository.saveGameState(updatedGs)
                }
            }
        }
    }
}
