package com.game.dungeon.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.R
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.models.Material
import com.game.dungeon.data.models.MaterialInventoryItem
import com.game.dungeon.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class CraftingTab {
    ENHANCE,
    SOCKET,
    GEM_FUSION,
    BREAKTHROUGH,
    MYTHIC_FORGE
}

data class CraftingUiState(
    val inventoryItems: List<Item> = emptyList(),
    val equippedItems: List<Item> = emptyList(),
    val materials: List<Material> = emptyList(),
    val materialInventory: List<MaterialInventoryItem> = emptyList(),
    val selectedItem: Item? = null,
    val selectedTab: CraftingTab = CraftingTab.ENHANCE,
    val isProcessing: Boolean = false,
    val userMessageRes: Int? = null,
    val gold: Long = 0L,
    val gameState: GameState? = null,
    val roster: List<com.game.dungeon.data.models.Hero> = emptyList()
)

@HiltViewModel
class CraftingViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(CraftingTab.ENHANCE)
    private val _selectedItem = MutableStateFlow<Item?>(null)
    private val _isProcessing = MutableStateFlow(false)
    private val _userMessageRes = MutableStateFlow<Int?>(null)

    private val _inventoryItems = repository.getInventory()
    private val _equippedItems = repository.getEquippedItems()
    private val _materialInventory = repository.getMaterialInventory().map { items ->
        items.filter { it.amount > 0 }
    }

    // Fetch game state to track the player's gold
    private val _gameState = repository.getGameState()

    val roster: StateFlow<List<com.game.dungeon.data.models.Hero>> = repository.getRoster()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val uiState: StateFlow<CraftingUiState> = combine(
        _inventoryItems,
        _equippedItems,
        _materialInventory,
        _selectedItem,
        _selectedTab,
        _isProcessing,
        _userMessageRes,
        _gameState,
        repository.getRoster()
    ) { flows ->
        @Suppress("UNCHECKED_CAST")
        val inventoryItems = flows[0] as List<Item>
        @Suppress("UNCHECKED_CAST")
        val equippedItems = flows[1] as List<Item>
        @Suppress("UNCHECKED_CAST")
        val materialInventory = flows[2] as List<MaterialInventoryItem>
        @Suppress("UNCHECKED_CAST")
        val selectedItem = flows[3] as Item?
        @Suppress("UNCHECKED_CAST")
        val selectedTab = flows[4] as CraftingTab
        val isProcessing = flows[5] as Boolean
        val userMessageRes = flows[6] as Int?
        val gameState = flows[7] as? GameState
        @Suppress("UNCHECKED_CAST")
        val roster = flows[8] as List<com.game.dungeon.data.models.Hero>

        val refreshedSelectedItem = selectedItem?.let { current ->
            val found = (inventoryItems + equippedItems).find { it.id == current.id }
            if (found != null && (found.enhancementLevel > current.enhancementLevel || found.sockets.size > current.sockets.size)) {
                found
            } else {
                current
            }
        }
        CraftingUiState(
            inventoryItems = inventoryItems,
            equippedItems = equippedItems,
            materials = materialInventory.map { it.material },
            materialInventory = materialInventory,
            selectedItem = refreshedSelectedItem,
            selectedTab = selectedTab,
            isProcessing = isProcessing,
            userMessageRes = userMessageRes,
            gold = gameState?.gold ?: 0L,
            gameState = gameState,
            roster = roster
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CraftingUiState()
    )

    // Add this to your CraftingViewModel
    fun triggerDebugMythicSeed(context: Context) {
        viewModelScope.launch {
            repository.seedDebugMythicCrafting(context)
        }
    }

    fun selectTab(tab: CraftingTab) {
        _selectedTab.value = tab
        _userMessageRes.value = null
    }

    fun selectItem(item: Item?) {
        _selectedItem.value = item
        _userMessageRes.value = null
    }

    fun clearUserMessage() {
        _userMessageRes.value = null
    }

    fun enhanceSelectedItem() {
        if (_isProcessing.value) return

        val item = _selectedItem.value ?: run {
            _userMessageRes.value = R.string.craft_err_no_item_selected
            return
        }

        if (item.enhancementLevel >= 10) {
            _userMessageRes.value = R.string.craft_err_max_enhancement
            return
        }

        viewModelScope.launch {
            _isProcessing.value = true
            _userMessageRes.value = null
            try {
                val result = repository.enhanceEquipment(item)
                result.fold(
                    onSuccess = { updatedItem ->
                        _selectedItem.value = updatedItem
                        _userMessageRes.value = R.string.craft_msg_enhance_success
                    },
                    onFailure = { ex ->
                        val msgRes = when {
                            ex.message?.contains("gold", ignoreCase = true) == true -> R.string.craft_err_insufficient_gold
                            ex.message?.contains("ore", ignoreCase = true) == true ||
                                    ex.message?.contains("material", ignoreCase = true) == true -> R.string.craft_err_insufficient_materials
                            ex.message?.contains("maximum", ignoreCase = true) == true -> R.string.craft_err_max_enhancement
                            else -> R.string.craft_err_operation_failed
                        }
                        _userMessageRes.value = msgRes
                    }
                )
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun addSocketToSelectedItem() {
        if (_isProcessing.value) return

        val item = _selectedItem.value ?: run {
            _userMessageRes.value = R.string.craft_err_no_item_selected
            return
        }

        val maxSockets = if (item.slot == ItemSlot.ACCESSORY) 1 else 3
        if (item.sockets.size >= maxSockets) {
            _userMessageRes.value = R.string.craft_err_max_sockets
            return
        }

        viewModelScope.launch {
            _isProcessing.value = true
            _userMessageRes.value = null
            try {
                val result = repository.addSocketSlot(item)
                result.fold(
                    onSuccess = { updatedItem ->
                        _selectedItem.value = updatedItem
                        _userMessageRes.value = R.string.craft_msg_socket_success
                    },
                    onFailure = { ex ->
                        val msgRes = when {
                            ex.message?.contains("gold", ignoreCase = true) == true -> R.string.craft_err_insufficient_gold
                            ex.message?.contains("ore", ignoreCase = true) == true ||
                                    ex.message?.contains("material", ignoreCase = true) == true -> R.string.craft_err_insufficient_materials
                            ex.message?.contains("capacity", ignoreCase = true) == true -> R.string.craft_err_max_sockets
                            else -> R.string.craft_err_operation_failed
                        }
                        _userMessageRes.value = msgRes
                    }
                )
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun insertGemIntoSelectedItem(socketIndex: Int, gemMaterialId: String) {
        if (_isProcessing.value) return

        val item = _selectedItem.value ?: run {
            _userMessageRes.value = R.string.craft_err_no_item_selected
            return
        }

        if (socketIndex !in item.sockets.indices) {
            _userMessageRes.value = R.string.craft_err_invalid_socket
            return
        }

        viewModelScope.launch {
            _isProcessing.value = true
            _userMessageRes.value = null
            try {
                val result = repository.insertGem(item.id, socketIndex, gemMaterialId)
                result.fold(
                    onSuccess = { updatedItem ->
                        _selectedItem.value = updatedItem
                        _userMessageRes.value = R.string.craft_msg_gem_inserted
                    },
                    onFailure = { ex ->
                        val msgRes = when {
                            ex.message?.contains("available", ignoreCase = true) == true -> R.string.craft_err_insufficient_materials
                            else -> R.string.craft_err_operation_failed
                        }
                        _userMessageRes.value = msgRes
                    }
                )
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun craftOrFuseGem(sourceGemId: String, targetGemId: String) {
        if (_isProcessing.value) return

        viewModelScope.launch {
            _isProcessing.value = true
            _userMessageRes.value = null
            try {
                val result = if (sourceGemId.isNotBlank()) {
                    repository.fuseGems(sourceGemId, targetGemId, 3)
                } else {
                    val recipe = when (targetGemId) {
                        "ruby_gem_1" -> mapOf("fire_essence" to 3)
                        "sapphire_gem_1" -> mapOf("ice_essence" to 3)
                        "emerald_gem_1" -> mapOf("lightning_essence" to 3)
                        "topaz_gem_1" -> mapOf("dark_essence" to 3)
                        else -> mapOf("fire_essence" to 3)
                    }
                    repository.craftGem(targetGemId, recipe)
                }

                result.fold(
                    onSuccess = {
                        val msg = if (sourceGemId.isNotBlank()) {
                            R.string.craft_msg_gem_fused
                        } else {
                            R.string.craft_msg_gem_crafted
                        }
                        _userMessageRes.value = msg
                    },
                    onFailure = { ex ->
                        val msgRes = when {
                            ex.message?.contains("Insufficient", ignoreCase = true) == true -> R.string.craft_err_insufficient_materials
                            else -> R.string.craft_err_operation_failed
                        }
                        _userMessageRes.value = msgRes
                    }
                )
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun uncapHero(heroId: String) {
        if (_isProcessing.value) return

        val hero = roster.value.find { it.id == heroId }
        if (hero != null && hero.level < hero.maxLevel) {
            _userMessageRes.value = R.string.craft_err_hero_not_at_cap
            return
        }

        viewModelScope.launch {
            _isProcessing.value = true
            _userMessageRes.value = null
            try {
                val result = repository.uncapHeroLevel(heroId)
                result.fold(
                    onSuccess = {
                        _userMessageRes.value = R.string.craft_msg_hero_uncapped
                    },
                    onFailure = { ex ->
                        val msgRes = when {
                            ex.message?.contains("trophy", ignoreCase = true) == true ||
                                    ex.message?.contains("required", ignoreCase = true) == true -> R.string.craft_err_insufficient_materials
                            ex.message?.contains("max level", ignoreCase = true) == true ||
                                    ex.message?.contains("cap", ignoreCase = true) == true -> R.string.craft_err_hero_not_at_cap
                            else -> R.string.craft_err_operation_failed
                        }
                        _userMessageRes.value = msgRes
                    }
                )
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun craftMythicSelectedItem(context: android.content.Context) {
        craftMythicSelectedItem("", context)
    }

    fun craftMythicSelectedItem(targetSetId: String, context: android.content.Context) {

        val item = _selectedItem.value ?: run {
            _userMessageRes.value = R.string.craft_err_no_item_selected
            return
        }

        if (item.rarity != com.game.dungeon.data.models.Rarity.LEGENDARY || item.enhancementLevel < 10) {
            _userMessageRes.value = R.string.craft_err_requires_level_10_legendary
            return
        }

        viewModelScope.launch {
            _isProcessing.value = true
            _userMessageRes.value = null
            try {
                val result = repository.craftMythicEquipment(item, targetSetId, context)
                result.fold(
                    onSuccess = { updatedItem ->
                        _selectedItem.value = updatedItem
                        _userMessageRes.value = R.string.craft_msg_mythic_forge_success
                    },
                    onFailure = { ex ->
                        val msgRes = when {
                            ex.message?.contains("Shards", ignoreCase = true) == true ||
                                    ex.message?.contains("Trophies", ignoreCase = true) == true ||
                                    ex.message?.contains("material", ignoreCase = true) == true ||
                                    ex.message?.contains("Insufficient", ignoreCase = true) == true -> R.string.craft_err_insufficient_materials
                            ex.message?.contains("Legendary", ignoreCase = true) == true -> R.string.craft_err_requires_level_10_legendary
                            else -> R.string.craft_err_operation_failed
                        }
                        _userMessageRes.value = msgRes
                    }
                )
            } finally {
                _isProcessing.value = false
            }
        }
    }
}