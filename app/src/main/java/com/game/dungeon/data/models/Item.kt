package com.game.dungeon.data.models

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.game.dungeon.R
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
    val mpBonus: Int = 0,
    val critChanceBonus: Int = 0,
    val critDamageBonus: Int = 0,
    val emoji: String,
    val floorFound: Int,
    val ownerId: String? = null // To track who is wearing it, or if it's in inventory
) {
    val sellValue: Long get() = (floorFound * 5L + rarity.ordinal * 20L).coerceAtLeast(5L)

    val powerScore: Int get() = attackBonus + defenseBonus + magicBonus + (hpBonus / 5) + (mpBonus / 2) + (critChanceBonus * 2) + (critDamageBonus / 2)

    companion object {
        fun random(
            floor: Int,
            context: Context,
            relicBonuses: RelicBonuses? = null,
            minRarity: Rarity = Rarity.COMMON
        ): Item {
            return random(floor, context, relicBonuses, minRarity, dimension = 1)
        }

        fun random(
            floor: Int,
            context: Context,
            relicBonuses: RelicBonuses? = null,
            minRarity: Rarity = Rarity.COMMON,
            dimension: Int = 1
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

            // Equipment category sub-types based on unlocked Dimension
            val gearCategory = when (slot) {
                ItemSlot.WEAPON -> when {
                    dimension >= 4 -> listOf("Dimensional Blade" to "⚔️", "Void Scythe" to "🌌", "Astral Wand" to "🌟").random()
                    dimension == 3 -> listOf("Katana" to "⚔️", "Greatsword" to "🗡️", "Scythe" to "🌾", "Starlight Wand" to "🪄").random()
                    dimension == 2 -> listOf("Battle Axe" to "🪓", "Hunting Bow" to "🏹", "Knight Lance" to "🗡️").random()
                    else -> listOf("Sword" to "⚔️", "Dagger" to "🗡️", "Staff" to "🪄").random()
                }
                ItemSlot.ARMOR -> when {
                    dimension >= 4 -> listOf("Dimensional Cuirass" to "🌌", "Astral Vestment" to "🌟").random()
                    dimension == 3 -> listOf("Dragon Scale Armor" to "🐉", "Archmage Robes" to "🔮").random()
                    dimension == 2 -> listOf("Scale Mail" to "🦺", "Heavy Cuirass" to "🛡️").random()
                    else -> listOf("Plate Armor" to "🛡️", "Leather Helm" to "🪖", "Cloth Robe" to "👘").random()
                }
                ItemSlot.SHIELD -> when {
                    dimension >= 4 -> listOf("Dimensional Barrier" to "🌀", "Void Aegis" to "🛡️").random()
                    dimension == 3 -> listOf("Force Shield" to "🛡️", "Mirror Shield" to "🪞").random()
                    dimension == 2 -> listOf("Aegis Shield" to "🛡️", "Tower Shield" to "🛡️").random()
                    else -> listOf("Round Shield" to "🛡️", "Guard Shield" to "🔰").random()
                }
                ItemSlot.ACCESSORY -> when {
                    dimension >= 4 -> listOf("Dimensional Core" to "🌌", "Cosmic Crest" to "👑").random()
                    dimension == 3 -> listOf("Relic Amulet" to "🧿", "Cosmic Charm" to "🔮").random()
                    dimension == 2 -> listOf("Ruby Bracelet" to "📿", "Jade Earring" to "💎").random()
                    else -> listOf("Copper Ring" to "💍", "Silver Pendant" to "📿").random()
                }
            }

            val categoryName = gearCategory.first
            val emoji = gearCategory.second

            val rarityRes = when (rarity) {
                Rarity.COMMON -> R.string.item_rarity_common
                Rarity.RARE -> R.string.item_rarity_rare
                Rarity.EPIC -> R.string.item_rarity_epic
                Rarity.LEGENDARY -> R.string.item_rarity_legendary
            }
            
            val rarityStr = try { context.getString(rarityRes) } catch (_: Exception) { null } ?: rarity.name
            val fullName = try { context.getString(R.string.item_name_template, rarityStr, categoryName) } catch (_: Exception) { null } ?: "$rarityStr $categoryName"
            
            val bonusMult = when (rarity) {
                Rarity.COMMON -> 1f
                Rarity.RARE -> 2f
                Rarity.EPIC -> 4f
                Rarity.LEGENDARY -> 8f
            }
            
            // Armory & Dimension influence: Increase base stats
            val statBonus = 1f + (relicBonuses?.itemStatBonus ?: 0f)
            val dimBonus = 1f + (dimension - 1) * 0.4f
            val finalMult = bonusMult * statBonus * dimBonus

            var critChance = 0
            var critDmg = 0
            if (rarity.ordinal >= Rarity.RARE.ordinal) {
                if (Random.nextInt(100) < 30) critChance = (Random.nextInt(2, 6) * bonusMult).toInt()
                if (Random.nextInt(100) < 30) critDmg = (Random.nextInt(5, 15) * bonusMult).toInt()
            }

            return Item(
                id = id,
                name = fullName,
                slot = slot,
                rarity = rarity,
                attackBonus = if (slot == ItemSlot.WEAPON) ((1 + floor / 4) * finalMult).toInt() else 0,
                defenseBonus = if (slot == ItemSlot.ARMOR || slot == ItemSlot.SHIELD) ((2 + floor / 3) * finalMult).toInt() else 0,
                magicBonus = if (slot == ItemSlot.ACCESSORY) ((1 + floor / 8) * finalMult).toInt() else 0,
                hpBonus = ((5 + floor * 1.2f) * finalMult).toInt(),
                mpBonus = if (slot == ItemSlot.ACCESSORY) ((1 + floor / 10) * finalMult).toInt() else 0,
                critChanceBonus = critChance,
                critDamageBonus = critDmg,
                emoji = emoji,
                floorFound = floor
            )
        }
    }
}
