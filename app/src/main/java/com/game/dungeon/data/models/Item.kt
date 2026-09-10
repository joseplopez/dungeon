package com.game.dungeon.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class ItemSlot { WEAPON, ARMOR, SHIELD, ACCESSORY }

enum class Rarity(val color: Long) {
    COMMON(0xFF888888),
    RARE(0xFF4488FF),
    EPIC(0xFFAA44FF),
    LEGENDARY(0xFFFFAA00)
}

@Entity(tableName = "items")
data class Item(
    @PrimaryKey val id: String,
    val name: String,
    val slot: ItemSlot,
    val rarity: Rarity,
    val attackBonus: Int = 0,
    val defenseBonus: Int = 0,
    val magicBonus: Int = 0,
    val hpBonus: Int = 0,
    val emoji: String,
    val floorFound: Int,
    val ownerId: String? = null // To track who is wearing it, or if it's in inventory
) {
    val sellValue: Long get() = (floorFound * 5L + rarity.ordinal * 20L).coerceAtLeast(5L)

    companion object {
        fun random(floor: Int): Item {
            val rarity = when (val roll = (1..100).random()) {
                in 1..70 -> Rarity.COMMON
                in 71..90 -> Rarity.RARE
                in 91..98 -> Rarity.EPIC
                else -> Rarity.LEGENDARY
            }
            
            val slot = ItemSlot.entries.random()
            val id = UUID.randomUUID().toString()
            
            val (name, emoji) = when (slot) {
                ItemSlot.WEAPON -> "Sword" to "⚔️"
                ItemSlot.ARMOR -> "Plate" to "🛡️"
                ItemSlot.SHIELD -> "Shield" to "🛡️"
                ItemSlot.ACCESSORY -> "Ring" to "💍"
            }
            
            val bonusMult = when (rarity) {
                Rarity.COMMON -> 1
                Rarity.RARE -> 2
                Rarity.EPIC -> 4
                Rarity.LEGENDARY -> 8
            }
            
            return Item(
                id = id,
                name = "${rarity.name} $name",
                slot = slot,
                rarity = rarity,
                attackBonus = if (slot == ItemSlot.WEAPON) (1 + floor / 5) * bonusMult else 0,
                defenseBonus = if (slot == ItemSlot.ARMOR || slot == ItemSlot.SHIELD) (1 + floor / 10) * bonusMult else 0,
                magicBonus = if (slot == ItemSlot.ACCESSORY) (1 + floor / 10) * bonusMult else 0,
                hpBonus = (floor / 2) * bonusMult,
                emoji = emoji,
                floorFound = floor
            )
        }
    }
}
