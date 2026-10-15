package com.game.dungeon.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.BuildConfig
import com.game.dungeon.data.models.LeaderboardEntry
import com.game.dungeon.data.repository.AuthRepository
import com.game.dungeon.data.repository.FriendRepository
import com.game.dungeon.data.repository.GameRepository
import com.game.dungeon.data.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val repository: GameRepository,
    private val leaderboardRepository: LeaderboardRepository,
    private val authRepository: AuthRepository,
    private val friendRepository: FriendRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0) // 0: Global, 1: Boss Lair
    val selectedTab = _selectedTab.asStateFlow()

    val gameState = repository.getGameState().stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val currentParty = repository.getParty().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val canSeeBossLair: StateFlow<Boolean> = gameState.map { gs ->
        (gs?.currentDimension ?: 1) >= 11 || BuildConfig.ENABLE_BOSS_LAIR_TESTING
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _refreshTrigger = MutableStateFlow(0)

    val leaderboardEntries: StateFlow<List<LeaderboardEntry>> = combine(
        _selectedTab,
        _refreshTrigger,
        gameState,
        currentParty
    ) { tab, _, gs, party ->
        if (gs == null) return@combine emptyList()
        
        val entries = when (tab) {
            0 -> leaderboardRepository.getGlobalLeaderboard()
            1 -> leaderboardRepository.getBossLairLeaderboard()
            else -> leaderboardRepository.getGlobalLeaderboard()
        }
        
        val mappedEntries = entries.map { entry ->
            val isUser = gs.playerId != null && entry.playerId == gs.playerId
            // Preserve the team that actually set the record from Firebase.
            // Only fallback to active party if no team was saved for the record yet.
            if (isUser && entry.team.isEmpty()) {
                entry.copy(isUser = true, team = party)
            } else {
                entry.copy(isUser = isUser)
            }
        }.toMutableList()

        val hasUser = mappedEntries.any { it.isUser }
        if (!hasUser && gs.playerId != null) {
            val userBossFloor = gs.highestBossLairFloor
            val userFloor = gs.lifetimeHighestFloor
            val showUser = if (tab == 1) userBossFloor > 0 else userFloor > 0
            if (showUser) {
                val userEntry = LeaderboardEntry(
                    rank = mappedEntries.size + 1,
                    playerId = gs.playerId,
                    playerName = gs.playerName,
                    maxFloor = gs.lifetimeHighestFloor,
                    maxBossFloor = gs.highestBossLairFloor,
                    dimension = gs.currentDimension,
                    dimensionMaxFloor = gs.highestFloor,
                    totalMagicite = gs.totalMagiciteEarned.toInt(),
                    fastestClearMs = gs.fastestClearTime,
                    isUser = true,
                    team = party
                )
                mappedEntries.add(userEntry)
                val comparator = if (tab == 1) {
                    compareByDescending<LeaderboardEntry> { it.maxBossFloor }.thenByDescending { it.totalMagicite }
                } else {
                    compareByDescending<LeaderboardEntry> { it.maxFloor }.thenByDescending { it.dimension }.thenByDescending { it.totalMagicite }
                }
                return@combine mappedEntries.sortedWith(comparator)
                    .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
            }
        }

        mappedEntries
    }.onStart { _isLoading.value = true }
     .onEach { _isLoading.value = false }
     .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        ensureUserIdentity()
    }

    fun ensureUserIdentity() {
        viewModelScope.launch {
            repository.getGameState().first()?.let { currentGs ->
                var uid = currentGs.playerId
                if (uid == null) {
                    uid = authRepository.signInAnonymously()
                    if (uid != null) {
                        val updatedGs = currentGs.copy(playerId = uid)
                        repository.saveGameState(updatedGs)
                        repository.triggerFirebaseUpload(updatedGs)
                    }
                } else {
                    repository.triggerFirebaseUpload(currentGs)
                }
            }
        }
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
        viewModelScope.launch {
            repository.triggerFirebaseUpload()
            _refreshTrigger.value += 1
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.triggerFirebaseUpload()
            _refreshTrigger.value += 1
        }
    }

    fun addFriend(id: String) {
        viewModelScope.launch {
            friendRepository.addFriend(id)
        }
    }

    fun removeFriend(id: String) {
        viewModelScope.launch {
            friendRepository.removeFriend(id)
        }
    }

    fun updatePlayerName(name: String) {
        viewModelScope.launch {
            val currentGs = repository.getGameStateOnce() ?: return@launch
            repository.saveGameState(currentGs.copy(playerName = name))
        }
    }
}
