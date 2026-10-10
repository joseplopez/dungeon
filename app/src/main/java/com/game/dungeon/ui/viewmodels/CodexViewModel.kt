package com.game.dungeon.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.data.models.CodexMilestone
import com.game.dungeon.data.models.MaterialCodexDetail
import com.game.dungeon.data.models.MonsterCodexDetail
import com.game.dungeon.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class CodexTab {
    BESTIARY,
    MATERIALS,
    EQUIPMENT
}

data class CodexUiState(
    val selectedTab: CodexTab = CodexTab.BESTIARY,
    val completionPercentage: Float = 0f,
    val bestiaryEntries: List<MonsterCodexDetail> = emptyList(),
    val materialEntries: List<MaterialCodexDetail> = emptyList(),
    val milestones: List<CodexMilestone> = emptyList(),
    val searchQuery: String = "",
    val userMessageRes: Int? = null,
)

@HiltViewModel
class CodexViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(CodexTab.BESTIARY)
    private val _searchQuery = MutableStateFlow("")
    private val _userMessageRes = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<CodexUiState> = combine(
        repository.codexCompletionPercentage,
        repository.monsterCodexDetails,
        repository.materialCodexDetails,
        _selectedTab,
        _searchQuery
    ) { completionPercentage, monsterDetails, materialDetails, selectedTab, searchQuery ->
        val query = searchQuery.trim().lowercase()

        val filteredBestiary = if (query.isEmpty()) {
            monsterDetails
        } else {
            monsterDetails.filter { detail ->
                detail.monsterType.name.lowercase().contains(query) ||
                        detail.monsterType.name.replace("_", " ").lowercase().contains(query)
            }
        }

        val filteredMaterials = if (query.isEmpty()) {
            materialDetails
        } else {
            materialDetails.filter { detail ->
                detail.material.id.lowercase().contains(query) ||
                        detail.material.id.replace("_", " ").lowercase().contains(query) ||
                        detail.material.category.name.lowercase().contains(query)
            }
        }

        val activeMilestones = CodexMilestone.getActiveMilestones(completionPercentage)

        CodexUiState(
            selectedTab = selectedTab,
            completionPercentage = completionPercentage,
            bestiaryEntries = filteredBestiary,
            materialEntries = filteredMaterials,
            milestones = activeMilestones,
            searchQuery = searchQuery,
            userMessageRes = _userMessageRes.value
        )
    }.combine(_userMessageRes) { state, userMessageRes ->
        state.copy(userMessageRes = userMessageRes)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CodexUiState()
    )

    fun selectTab(tab: CodexTab) {
        _selectedTab.value = tab
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun dismissUserMessage() {
        _userMessageRes.value = null
    }
}
