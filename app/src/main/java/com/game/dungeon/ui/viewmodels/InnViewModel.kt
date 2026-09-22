package com.game.dungeon.ui.viewmodels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.analytics.AnalyticsManager
import com.game.dungeon.data.models.*
import com.game.dungeon.data.repository.GameRepository
import com.game.dungeon.monetization.AdManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

import android.widget.Toast
import com.game.dungeon.monetization.BillingManager
import com.game.dungeon.monetization.InAppProduct
import com.game.dungeon.monetization.ResourceType

@HiltViewModel
class InnViewModel @Inject constructor(
    private val repository: GameRepository,
    private val analytics: AnalyticsManager,
    private val adManager: AdManager,
    val billingManager: BillingManager,
    @ApplicationContext private val context: Context
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
            return HeroClass.entries.filter { job ->
                val isUnlocked = state.isJobUnlocked(job)
                val tierMet = if (job.tier >= 2) state.innLevel >= 1 else true
                isUnlocked && tierMet
            }
        }

    fun markHiddenJobNotified(heroClass: HeroClass) {
        val gs = gameState.value ?: return
        viewModelScope.launch {
            repository.saveGameState(gs.copy(notifiedHiddenJobs = gs.notifiedHiddenJobs + heroClass))
        }
    }

    val maxPartySize: Int
        get() = gameState.value?.getMaxPartySize() ?: 3

    val canSendToDungeon: Boolean
        get() = _hiredHeroes.value.isNotEmpty()

    fun hireHero(heroClass: HeroClass) {
        val gs = gameState.value ?: return
        // Ensure only unlocked jobs can be hired
        if (!unlockedJobs.contains(heroClass) && heroClass != HeroClass.FREELANCER) return
        
        if (gs.gold >= heroClass.hireCost && _hiredHeroes.value.size < maxPartySize) {
            viewModelScope.launch {
                repository.saveGameState(gs.copy(gold = gs.gold - heroClass.hireCost))
                val hero = Hero.create(heroClass, context).copy(
                    isInParty = true,
                    partyPosition = _hiredHeroes.value.size
                )
                analytics.logHeroHired(hero.name, heroClass.name)
                repository.saveHero(hero)
            }
        }
    }

    fun moveHeroUp(heroId: String) {
        val heroes = _hiredHeroes.value.toMutableList()
        val index = heroes.indexOfFirst { it.id == heroId }
        if (index > 0) {
            val hero = heroes[index]
            val prevHero = heroes[index - 1]
            
            val updatedHero = hero.copy(partyPosition = index - 1)
            val updatedPrev = prevHero.copy(partyPosition = index)
            
            viewModelScope.launch {
                repository.saveHeroes(listOf(updatedHero, updatedPrev))
            }
        }
    }

    fun moveHeroDown(heroId: String) {
        val heroes = _hiredHeroes.value.toMutableList()
        val index = heroes.indexOfFirst { it.id == heroId }
        if (index != -1 && index < heroes.size - 1) {
            val hero = heroes[index]
            val nextHero = heroes[index + 1]
            
            val updatedHero = hero.copy(partyPosition = index + 1)
            val updatedNext = nextHero.copy(partyPosition = index)
            
            viewModelScope.launch {
                repository.saveHeroes(listOf(updatedHero, updatedNext))
            }
        }
    }

    fun fireHero(heroId: String) {
        viewModelScope.launch {
            _hiredHeroes.value.find { it.id == heroId }?.let {
                analytics.logHeroFired(it.name, it.heroClass.name)
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

                // Calculate Magicite Bonus (50% + 10% per dimension above 1)
                val multiplier = 0.50f + (gs.currentDimension - 1) * 0.10f
                val bonusMagicite = (gs.magiciteEarnedThisDim * multiplier).toInt()
                
                // Calculate Gold kept (Deep Pockets Relic)
                val goldKept = (gs.gold * gs.pocketsBonus).toLong()

                val clearTime = System.currentTimeMillis() - gs.dimStartTime
                val updatedFastestTime = if (gs.fastestClearTime == 0L || clearTime < gs.fastestClearTime) {
                    clearTime
                } else {
                    gs.fastestClearTime
                }

                // Reset GameState for new dimension
                val nextGs = gs.copy(
                    gold = goldKept,
                    magicite = gs.magicite + bonusMagicite,
                    totalMagiciteEarned = gs.totalMagiciteEarned + bonusMagicite,
                    currentDimension = gs.currentDimension + 1,
                    highestFloor = 0,
                    magiciteEarnedThisDim = 0,
                    gilEarnedThisDim = 0,
                    bossesKilledThisDim = 0,
                    itemsFoundThisDim = 0,
                    dimStartTime = System.currentTimeMillis(),
                    fastestClearTime = updatedFastestTime
                )
                analytics.logDimensionAdvanced(nextGs.currentDimension)
                repository.saveGameState(nextGs)
                repository.triggerFirebaseUpload(nextGs)
                _startFloor.value = 1
            }
        }
    }

    fun setStartFloor(floor: Int) {
        val gs = gameState.value ?: return
        val pathLevel = gs.pathfinderLevel
        if (pathLevel <= 0) {
            _startFloor.value = 1
            return
        }
        val maxFloor = gs.highestFloor
        // Max allowed floor based on level: 25%, 50%, 75%, 100%
        val limit = (maxFloor * (pathLevel * 0.25f)).toInt().coerceIn(1, maxFloor)
        _startFloor.value = floor.coerceIn(1, limit)
    }

    fun selectPet(pet: PetType?) {
        val gs = gameState.value ?: return
        viewModelScope.launch {
            repository.saveGameState(gs.copy(selectedPet = pet))
        }
    }

    fun unlockPet(pet: PetType) {
        val gs = gameState.value ?: return
        if (gs.gold >= pet.unlockCost && !gs.unlockedPets.contains(pet)) {
            viewModelScope.launch {
                repository.saveGameState(gs.copy(
                    gold = gs.gold - pet.unlockCost,
                    unlockedPets = gs.unlockedPets + pet
                ))
            }
        }
    }

    fun resetStartFloor() {
        _startFloor.value = 1
    }

    fun restAtInn() {
        val gs = gameState.value ?: return
        val heroes = _hiredHeroes.value
        if (heroes.isEmpty()) return

        // Cost: 10 Gil per Level per Hero, proportional to % of HP missing
        val rawCost = heroes.sumOf { hero ->
            if (hero.currentHp < hero.maxHp) {
                val hpMissingRatio = (hero.maxHp - hero.currentHp).toFloat() / hero.maxHp.toFloat()
                val baseCost = hero.level * 10
                (baseCost * hpMissingRatio).toLong().coerceAtLeast(1L)
            } else 0L
        }
        
        val discount = gs.restDiscount
        val totalCost = (rawCost * (1f - discount)).toLong().coerceAtLeast(if (rawCost > 0) 1L else 0L)

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

    fun watchGilAd(activity: Activity) {
        adManager.showRewardedAd(activity) {
            viewModelScope.launch {
                val currentGs = repository.getGameStateOnce() ?: return@launch
                val reward = (currentGs.totalGilEarned * 0.0025f).toLong().coerceAtLeast(25L)
                repository.addGil(reward)
            }
        }
    }

    fun watchMagiciteAd(activity: Activity) {
        adManager.showRewardedAd(activity) {
            viewModelScope.launch {
                val currentGs = repository.getGameStateOnce() ?: return@launch
                val reward = (currentGs.totalMagiciteEarned * 0.005f).toInt().coerceAtLeast(2)
                repository.addMagicite(reward)
            }
        }
    }
}
