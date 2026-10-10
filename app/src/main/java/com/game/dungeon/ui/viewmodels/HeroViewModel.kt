package com.game.dungeon.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.R
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HeroItemUiState(
    val hero: Hero,
    val maxLevel: Int = hero.maxLevel,
    val isCapped: Boolean = hero.isCapped,
    val requiredTrophyId: String = hero.requiredTrophyId,
    val hasRequiredTrophy: Boolean = false
)

data class HeroUiState(
    val heroes: List<HeroItemUiState> = emptyList(),
    val selectedHero: HeroItemUiState? = null,
    val maxLevel: Int = 20,
    val isCapped: Boolean = false,
    val requiredTrophyId: String = "boss_trophy_1",
    val hasRequiredTrophy: Boolean = false,
    val isProcessing: Boolean = false,
    val errorMessageRes: Int? = null,
    val userMessageRes: Int? = null
)

@HiltViewModel
class HeroViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    var ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    constructor(
        repository: GameRepository,
        dispatcher: CoroutineDispatcher
    ) : this(repository) {
        this.ioDispatcher = dispatcher
    }

    private val _selectedHeroId = MutableStateFlow<String?>(null)
    private val _isProcessing = MutableStateFlow(false)
    private val _errorMessageRes = MutableStateFlow<Int?>(null)
    private val _userMessageRes = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<HeroUiState> = combine(
        repository.getRoster(),
        repository.getMaterialInventory(),
        _selectedHeroId
    ) { roster, materialInventory, selectedHeroId ->
        val heroItemStates = roster.map { hero ->
            val trophyId = hero.requiredTrophyId
            val hasTrophy = materialInventory.any { it.material.id == trophyId && it.amount > 0 }
            HeroItemUiState(
                hero = hero,
                maxLevel = hero.maxLevel,
                isCapped = hero.isCapped,
                requiredTrophyId = trophyId,
                hasRequiredTrophy = hasTrophy
            )
        }

        val selectedHeroItem = heroItemStates.find { it.hero.id == selectedHeroId }
            ?: heroItemStates.firstOrNull()

        HeroUiState(
            heroes = heroItemStates,
            selectedHero = selectedHeroItem,
            maxLevel = selectedHeroItem?.maxLevel ?: 20,
            isCapped = selectedHeroItem?.isCapped ?: false,
            requiredTrophyId = selectedHeroItem?.requiredTrophyId ?: "boss_trophy_1",
            hasRequiredTrophy = selectedHeroItem?.hasRequiredTrophy ?: false
        )
    }.combine(_isProcessing) { state, isProcessing ->
        state.copy(isProcessing = isProcessing)
    }.combine(_errorMessageRes) { state, errorMessageRes ->
        state.copy(errorMessageRes = errorMessageRes)
    }.combine(_userMessageRes) { state, userMessageRes ->
        state.copy(userMessageRes = userMessageRes)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HeroUiState()
    )

    fun selectHero(heroId: String?) {
        _selectedHeroId.value = heroId
        _errorMessageRes.value = null
        _userMessageRes.value = null
    }

    fun clearUserMessage() {
        _userMessageRes.value = null
        _errorMessageRes.value = null
    }

    fun breakthroughHero(heroId: String) {
        if (_isProcessing.value) return

        viewModelScope.launch(ioDispatcher) {
            _isProcessing.value = true
            _errorMessageRes.value = null
            _userMessageRes.value = null
            try {
                val currentState = uiState.value
                val heroState = currentState.heroes.find { it.hero.id == heroId }
                if (heroState != null && !heroState.hasRequiredTrophy) {
                    val errorRes = R.string.craft_err_insufficient_materials
                    _errorMessageRes.value = errorRes
                    _userMessageRes.value = errorRes
                    return@launch
                }

                val result = repository.uncapHeroLevel(heroId)
                result.fold(
                    onSuccess = {
                        _userMessageRes.value = R.string.craft_msg_hero_uncapped
                    },
                    onFailure = { ex ->
                        val msgRes = when {
                            ex.message?.contains("trophy", ignoreCase = true) == true ||
                                    ex.message?.contains("required", ignoreCase = true) == true ||
                                    ex.message?.contains("Boss trophy", ignoreCase = true) == true -> R.string.craft_err_insufficient_materials
                            else -> R.string.craft_err_operation_failed
                        }
                        _errorMessageRes.value = msgRes
                        _userMessageRes.value = msgRes
                    }
                )
            } finally {
                _isProcessing.value = false
            }
        }
    }
}
