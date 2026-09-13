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
            val updatedGs = when (type) {
                RelicType.ATTACK -> gs.copy(attackRelic = gs.attackRelic + 1)
                RelicType.HP -> gs.copy(hpRelic = gs.hpRelic + 1)
                RelicType.MP -> gs.copy(mpRelic = gs.mpRelic + 1)
                RelicType.MAGIC -> gs.copy(magicRelic = gs.magicRelic + 1)
                RelicType.GOLD -> gs.copy(goldRelic = gs.goldRelic + 1)
                RelicType.MAGICITE_FIND -> gs.copy(magiciteRelic = gs.magiciteRelic + 1)
                RelicType.CRIT_CHANCE -> gs.copy(critChanceRelic = gs.critChanceRelic + 1)
                RelicType.CRIT_DAMAGE -> gs.copy(critDamageRelic = gs.critDamageRelic + 1)
                RelicType.MAGNET -> gs.copy(magnetRelic = gs.magnetRelic + 1)
                RelicType.POCKETS -> gs.copy(pocketsRelic = gs.pocketsRelic + 1)
                RelicType.DOUBLE_LOOT -> gs.copy(doubleLootRelic = gs.doubleLootRelic + 1)
            }.copy(magicite = gs.magicite - cost)

            analytics.logRelicUpgrade(type.name, currentLevel + 1)
            viewModelScope.launch {
                repository.saveGameState(updatedGs)
            }
        }
    }
}
