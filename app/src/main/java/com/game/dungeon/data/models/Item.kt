package com.game.dungeon.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import kotlin.random.Random

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
    val critChanceBonus: Int = 0,
    val critDamageBonus: Int = 0,
    val emoji: String,
    val floorFound: Int,
    val ownerId: String? = null // To track who is wearing it, or if it's in inventory
) {
    val sellValue: Long get() = (floorFound * 5L + rarity.ordinal * 20L).coerceAtLeast(5L)

    val powerScore: Int get() = attackBonus + defenseBonus + magicBonus + (hpBonus / 5) + (critChanceBonus * 2) + (critDamageBonus / 2)

    companion object {
        fun random(
            floor: Int,
            relicBonuses: RelicBonuses? = null,
            minRarity: Rarity = Rarity.COMMON
        ): Item {
            // Magic Shop influence: Increase higher rarity odds by 1% per level
            val msLevel = relicBonuses?.magicShopLevel ?: 0
            val roll = (1..100).random()
            
            var rarity = when {
                roll >= (99 - msLevel) -> Rarity.LEGENDARY
                roll >= (91 - msLevel * 2) -> Rarity.EPIC
                roll >= (71 - msLevel * 3) -> Rarity.RARE
                else -> Rarity.COMMON
            }

            if (rarity.ordinal < minRarity.ordinal) {
                rarity = minRarity
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
                Rarity.COMMON -> 1f
                Rarity.RARE -> 2f
                Rarity.EPIC -> 4f
                Rarity.LEGENDARY -> 8f
            }
            
            // Armory influence: Increase base stats by %
            val statBonus = 1f + (relicBonuses?.itemStatBonus ?: 0f)
            val finalMult = bonusMult * statBonus

            var critChance = 0
            var critDmg = 0
            if (rarity.ordinal >= Rarity.RARE.ordinal) {
                if (Random.nextInt(100) < 30) critChance = (Random.nextInt(2, 6) * bonusMult).toInt()
                if (Random.nextInt(100) < 30) critDmg = (Random.nextInt(5, 15) * bonusMult).toInt()
            }

            return Item(
                id = id,
                name = "${rarity.name} $name",
                slot = slot,
                rarity = rarity,
                attackBonus = if (slot == ItemSlot.WEAPON) ((1 + floor / 5) * finalMult).toInt() else 0,
                defenseBonus = if (slot == ItemSlot.ARMOR || slot == ItemSlot.SHIELD) ((1 + floor / 10) * finalMult).toInt() else 0,
                magicBonus = if (slot == ItemSlot.ACCESSORY) ((1 + floor / 10) * finalMult).toInt() else 0,
                hpBonus = ((floor / 2) * finalMult).toInt(),
                critChanceBonus = critChance,
                critDamageBonus = critDmg,
                emoji = emoji,
                floorFound = floor
            )
        }
    }
}
