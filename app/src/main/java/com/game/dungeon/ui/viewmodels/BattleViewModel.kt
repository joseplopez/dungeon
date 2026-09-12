package com.game.dungeon.ui.viewmodels

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.data.models.*
import com.game.dungeon.data.repository.GameRepository
import com.game.dungeon.engine.BattleEngine
import com.game.dungeon.engine.BattleEvent
import com.game.dungeon.ui.theme.EnemyRed
import com.game.dungeon.ui.theme.HeroBlue
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class BattleViewModel @Inject constructor(
    private val repository: GameRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _battleState = MutableStateFlow(BattleState())
    val battleState: StateFlow<BattleState> = _battleState.asStateFlow()

    private val engine = BattleEngine(context)
    private var battleJob: Job? = null

    fun startBattle(floor: Int) {
        battleJob?.cancel()
        battleJob = viewModelScope.launch {
            val party = repository.getParty().first().map { it.copy(currentHp = it.maxHp) }
            if (party.isEmpty()) {
                _battleState.update { it.copy(isRunning = false) }
                return@launch
            }

            val gameState = repository.getGameState().first() ?: GameState()
            val dimension = FFDimensionData.getDimension(gameState.currentDimension)
            val enemies = FFDimensionData.getEnemiesForFloor(dimension, floor).map { 
                Enemy.fromTemplate(it, floor, context)
            }
            
            _battleState.update { 
                it.copy(
                    currentFloor = floor,
                    heroes = party,
                    enemies = enemies,
                    isRunning = true,
                    battleLog = emptyList(),
                    goldEarned = 0,
                    floatingTexts = emptyList(),
                    recentlyHitIds = emptySet()
                )
            }

            engine.runBattle(party, enemies, _battleState.value.speed).collect { event ->
                handleBattleEvent(event)
            }
        }
    }

    private fun handleBattleEvent(event: BattleEvent) {
        when (event) {
            is BattleEvent.AttackStart -> {
                _battleState.update { it.copy(attackingUnitId = event.attackerId) }
                viewModelScope.launch {
                    kotlinx.coroutines.delay(200)
                    _battleState.update { it.copy(attackingUnitId = null) }
                }
            }
            is BattleEvent.AttackHit -> {
                _battleState.update { it.copy(hitUnitId = event.targetId, screenShake = true) }
                addImpactSpark(event.targetId)
                viewModelScope.launch {
                    kotlinx.coroutines.delay(150)
                    _battleState.update { it.copy(hitUnitId = null, screenShake = false) }
                }
            }
            is BattleEvent.Damage -> {
                updateUnitHp(event.targetId, event.amount, event.isHero)
                addFloatingText(event.targetId, "-${event.amount}", event.isHero)
            }
            is BattleEvent.Log -> {
                _battleState.update { it.copy(battleLog = it.battleLog + BattleLogEntry(event.message, event.type)) }
            }
            is BattleEvent.Victory -> {
                _battleState.update { 
                    it.copy(
                        goldEarned = it.goldEarned + event.gold,
                        recentLoot = event.loot,
                        itemsFound = if (event.loot != null) it.itemsFound + event.loot else it.itemsFound,
                        bossesKilled = it.bossesKilled + if (_battleState.value.enemies.any { e -> e.isBoss }) 1 else 0
                    ) 
                }
                event.loot?.let { loot ->
                    viewModelScope.launch {
                        repository.saveItem(loot)
                    }
                }
            }
            is BattleEvent.FloorCleared -> {
                viewModelScope.launch {
                    _battleState.update { it.copy(showFloorComplete = true) }
                    
                    val currentGameState = repository.getGameState().first()
                    currentGameState?.let {
                        val newHighestFloor = if (_battleState.value.currentFloor > it.highestFloor) _battleState.value.currentFloor else it.highestFloor
                        repository.saveGameState(it.copy(
                            gold = it.gold + _battleState.value.goldEarned,
                            highestFloor = newHighestFloor
                        ))
                        repository.trackDimensionStats(
                            gil = _battleState.value.goldEarned,
                            items = _battleState.value.itemsFound.size,
                            bosses = _battleState.value.bossesKilled
                        )
                    }
                    
                    kotlinx.coroutines.delay(1500)
                    _battleState.update { it.copy(showFloorComplete = false) }
                    nextFloor()
                }
            }
            BattleEvent.Defeat -> {
                _battleState.update { it.copy(isRunning = false) }
            }
        }
    }

    fun clearRecentLoot() {
        _battleState.update { it.copy(recentLoot = null) }
    }

    private fun updateUnitHp(id: String, damage: Int, isHero: Boolean) {
        _battleState.update { state ->
            if (isHero) {
                state.copy(
                    heroes = state.heroes.map { if (it.id == id) it.copy(currentHp = (it.currentHp - damage).coerceAtLeast(0)) else it },
                    recentlyHitIds = state.recentlyHitIds + id
                )
            } else {
                state.copy(
                    enemies = state.enemies.map { if (it.id == id) it.copy(currentHp = (it.currentHp - damage).coerceAtLeast(0)) else it },
                    recentlyHitIds = state.recentlyHitIds + id
                )
            }
        }
        viewModelScope.launch {
            kotlinx.coroutines.delay(300)
            _battleState.update { it.copy(recentlyHitIds = it.recentlyHitIds - id) }
        }
    }

    private fun addFloatingText(targetId: String, text: String, isHero: Boolean) {
        val x = if (isHero) Random.nextInt(50, 150).toFloat() else Random.nextInt(250, 350).toFloat()
        val y = if (isHero) Random.nextInt(500, 600).toFloat() else Random.nextInt(100, 200).toFloat()
        val ft = FloatingTextData(text = text, x = x, y = y, color = if (isHero) EnemyRed else HeroBlue, targetId = targetId)
        _battleState.update { it.copy(floatingTexts = it.floatingTexts + ft) }
    }

    private fun addImpactSpark(targetId: String) {
        val isHero = _battleState.value.heroes.any { it.id == targetId }
        val x = if (isHero) 100f else 300f // Approximate positions
        val y = if (isHero) 550f else 150f
        val spark = ImpactSpark(x = x, y = y)
        _battleState.update { it.copy(impactSparks = it.impactSparks + spark) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(400)
            removeSpark(spark.id)
        }
    }

    fun removeSpark(id: String) {
        _battleState.update { it.copy(impactSparks = it.impactSparks.filter { s -> s.id != id }) }
    }

    fun clearShake() {
        _battleState.update { it.copy(screenShake = false) }
    }

    fun setSpeed(speed: BattleSpeed) {
        _battleState.update { it.copy(speed = speed) }
        startBattle(_battleState.value.currentFloor) 
    }

    fun retreat() {
        battleJob?.cancel()
        viewModelScope.launch {
            val state = repository.getGameState().first()
            state?.let {
                val gold = _battleState.value.goldEarned
                repository.saveGameState(it.copy(gold = it.gold + gold))
                repository.trackDimensionStats(
                    gil = gold,
                    items = _battleState.value.itemsFound.size,
                    bosses = _battleState.value.bossesKilled
                )
            }
            _battleState.update { it.copy(isRunning = false) }
        }
    }

    fun nextFloor() {
        startBattle(_battleState.value.currentFloor + 1)
    }

    fun removeFT(id: String) {
        _battleState.update { it.copy(floatingTexts = it.floatingTexts.filter { ft -> ft.id != id }) }
    }
}
