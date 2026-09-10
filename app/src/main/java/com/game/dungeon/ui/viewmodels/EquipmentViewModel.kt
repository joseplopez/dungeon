package com.game.dungeon.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EquipmentViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _selectedHeroId = MutableStateFlow<String?>(null)

    // Reactively fetch the hero whenever the ID changes
    val selectedHero: StateFlow<Hero?> = _selectedHeroId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else repository.getRoster().map { roster -> roster.find { it.id == id } }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Reactively fetch equipped items for the CURRENTly selected hero only
    val equippedItems: StateFlow<List<Item>> = _selectedHeroId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getEquippedItems(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventory: StateFlow<List<Item>> = repository.getInventory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectHero(heroId: String) {
        _selectedHeroId.value = heroId
    }

    fun equipItem(item: Item) {
        val heroId = _selectedHeroId.value ?: return
        viewModelScope.launch {
            val currentEquipped = equippedItems.value
            
            // If it's a unique slot (Weapon, Armor, Shield), unequip existing first
            if (item.slot != ItemSlot.ACCESSORY) {
                currentEquipped.find { it.slot == item.slot }?.let {
                    repository.saveItem(it.copy(ownerId = null))
                }
            } else {
                // For accessories, check if we have 2 already
                val accessories = currentEquipped.filter { it.slot == ItemSlot.ACCESSORY }
                if (accessories.size >= 2) {
                    repository.saveItem(accessories.first().copy(ownerId = null))
                }
            }
            repository.saveItem(item.copy(ownerId = heroId))
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

    fun quickEquip() {
        val heroId = _selectedHeroId.value ?: return
        viewModelScope.launch {
            val inv = repository.getInventory().first()
            val equipped = equippedItems.value
            val pool = inv + equipped
            
            val bestWeapon = pool.filter { it.slot == ItemSlot.WEAPON }.maxByOrNull { it.powerScore }
            val bestArmor = pool.filter { it.slot == ItemSlot.ARMOR }.maxByOrNull { it.powerScore }
            val bestShield = pool.filter { it.slot == ItemSlot.SHIELD }.maxByOrNull { it.powerScore }
            val bestAccessories = pool.filter { it.slot == ItemSlot.ACCESSORY }
                .sortedByDescending { it.powerScore }
                .take(2)

            val toEquip = listOfNotNull(bestWeapon, bestArmor, bestShield) + bestAccessories
            
            // 1. Unequip everything currently on this hero
            repository.unequipAll(heroId)
            
            // 2. Equip the new best ones
            toEquip.forEach {
                repository.saveItem(it.copy(ownerId = heroId))
            }
        }
    }
}
