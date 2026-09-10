package com.game.dungeon.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EquipmentViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _selectedHero = MutableStateFlow<Hero?>(null)
    val selectedHero = _selectedHero.asStateFlow()

    private val _equippedItems = MutableStateFlow<List<Item>>(emptyList())
    val equippedItems = _equippedItems.asStateFlow()

    private val _inventory = MutableStateFlow<List<Item>>(emptyList())
    val inventory = _inventory.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getInventory().collect { _inventory.value = it }
        }
    }

    fun selectHero(heroId: String) {
        viewModelScope.launch {
            repository.getRoster().map { roster -> roster.find { it.id == heroId } }
                .collect { hero ->
                    _selectedHero.value = hero
                    if (hero != null) {
                        repository.getEquippedItems(hero.id).collect { _equippedItems.value = it }
                    }
                }
        }
    }

    fun equipItem(item: Item) {
        val hero = _selectedHero.value ?: return
        viewModelScope.launch {
            // If it's a unique slot (Weapon, Armor, Shield), unequip existing first
            if (item.slot != ItemSlot.ACCESSORY) {
                _equippedItems.value.find { it.slot == item.slot }?.let {
                    repository.saveItem(it.copy(ownerId = null))
                }
            } else {
                // For accessories, check if we have 2 already
                val accessories = _equippedItems.value.filter { it.slot == ItemSlot.ACCESSORY }
                if (accessories.size >= 2) {
                    repository.saveItem(accessories.first().copy(ownerId = null))
                }
            }
            repository.saveItem(item.copy(ownerId = hero.id))
        }
    }

    fun unequipItem(item: Item) {
        viewModelScope.launch {
            repository.saveItem(item.copy(ownerId = null))
        }
    }

    fun sellItem(item: Item) {
        viewModelScope.launch {
            repository.sellItem(item)
            repository.deleteItem(item)
        }
    }
}
