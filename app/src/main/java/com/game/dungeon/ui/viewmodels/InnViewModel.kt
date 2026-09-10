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
class InnViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    val gameState = repository.getGameState().stateIn(viewModelScope, SharingStarted.Eagerly, GameState())
    
    private val _hiredHeroes = MutableStateFlow<List<Hero>>(emptyList())
    val hiredHeroes: StateFlow<List<Hero>> = _hiredHeroes.asStateFlow()

    private val _startFloor = MutableStateFlow(1)
    val startFloor: StateFlow<Int> = _startFloor.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getGameState().collect { state ->
                if (state == null) {
                    repository.saveGameState(repository.newGame())
                }
            }
        }
        viewModelScope.launch {
            repository.getParty().collect { party ->
                _hiredHeroes.value = party
            }
        }
    }

    val unlockedJobs: List<HeroClass>
        get() {
            val state = gameState.value ?: return listOf(HeroClass.FREELANCER)
            val unlockedFromCrystals = state.unlockedJobs
            
            // Filter by Inn Level (tier restriction)
            // Tier 1 is always visible if unlocked via crystal
            // Tier 2 requires Inn Level >= 1
            return HeroClass.entries.filter { job ->
                val isUnlocked = unlockedFromCrystals.contains(job) || job == HeroClass.FREELANCER
                val tierMet = if (job.tier >= 2) state.innLevel >= 1 else true
                isUnlocked && tierMet
            }
        }

    val maxPartySize: Int
        get() = 3 + (gameState.value?.barracksLevel ?: 0)

    val canSendToDungeon: Boolean
        get() = _hiredHeroes.value.isNotEmpty()

    fun hireHero(jobClass: HeroClass) {
        val gs = gameState.value ?: return
        // Ensure only unlocked jobs can be hired
        if (!unlockedJobs.contains(jobClass) && jobClass != HeroClass.FREELANCER) return
        
        if (gs.gold >= jobClass.hireCost && _hiredHeroes.value.size < maxPartySize) {
            viewModelScope.launch {
                repository.saveGameState(gs.copy(gold = gs.gold - jobClass.hireCost))
                val hero = Hero.create(jobClass).copy(isInParty = true)
                repository.saveHero(hero)
            }
        }
    }

    fun fireHero(heroId: String) {
        viewModelScope.launch {
            _hiredHeroes.value.find { it.id == heroId }?.let {
                repository.removeHero(it)
            }
        }
    }

    fun setHeroPriority(heroId: String, priority: AIPriority) {
        viewModelScope.launch {
            _hiredHeroes.value.find { it.id == heroId }?.let {
                repository.saveHero(it.copy(aiPriority = priority))
            }
        }
    }

    fun advanceDimension() {
        val gs = gameState.value ?: return
        if (gs.highestFloor >= 100) {
            viewModelScope.launch {
                // Remove all heroes from party
                _hiredHeroes.value.forEach { repository.removeHero(it) }
                
                // Clear inventory
                repository.clearInventory()

                // Reset GameState for new dimension
                val nextGs = gs.copy(
                    gold = 0,
                    currentDimension = gs.currentDimension + 1,
                    highestFloor = 0
                )
                repository.saveGameState(nextGs)
                _startFloor.value = 1
            }
        }
    }

    fun setStartFloor(floor: Int) {
        val gs = gameState.value ?: return
        if (gs.pathfinderLevel <= 0) {
            _startFloor.value = 1
            return
        }
        val maxFloor = gs.highestFloor
        _startFloor.value = floor.coerceIn(1, maxOf(1, maxFloor))
    }

    fun resetStartFloor() {
        _startFloor.value = 1
    }

    fun restAtInn() {
        val gs = gameState.value ?: return
        val heroes = _hiredHeroes.value
        if (heroes.isEmpty()) return

        // Cost: 10 Gil per Level per Hero, proportional to % of HP missing
        val totalCost = heroes.sumOf { hero ->
            if (hero.currentHp < hero.maxHp) {
                val hpMissingRatio = (hero.maxHp - hero.currentHp).toFloat() / hero.maxHp.toFloat()
                val baseCost = hero.level * 10
                (baseCost * hpMissingRatio).toLong().coerceAtLeast(1L)
            } else 0L
        }

        if (totalCost > 0 && gs.gold >= totalCost) {
            viewModelScope.launch {
                repository.saveGameState(gs.copy(gold = gs.gold - totalCost))
                heroes.forEach { hero ->
                    if (hero.currentHp < hero.maxHp) {
                        repository.saveHero(hero.copy(currentHp = hero.maxHp))
                    }
                }
            }
        }
    }
}
