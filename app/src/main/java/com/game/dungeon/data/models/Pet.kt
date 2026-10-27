package com.game.dungeon.data.models

import androidx.annotation.StringRes
import com.game.dungeon.R

enum class CombatStat(val displayName: String) {
    HP("HP"),
    MP("MP"),
    ATK("ATK"),
    DEF("DEF"),
    MAG("MAG")
}

// Update your PetType enum entries to include a list of two CombatStats:
enum class PetType(
    @StringRes val nameRes: Int,
    @StringRes val descRes: Int,
    val emoji: String,
    val bonusType: PetBonusType,
    val bonusValue: Float,
    val unlockCost: Int,
    val rarity: Rarity = Rarity.COMMON,
    val tier2Boosts: List<CombatStat> // ADD THIS
) {
    // TANK (HP & Defense)
    CHOCOBO(R.string.pet_chocobo_name, R.string.pet_chocobo_desc, "🐤", PetBonusType.ITEM_FIND, 0.20f, 10000, Rarity.COMMON, listOf(CombatStat.HP, CombatStat.DEF)),

    // PURE MAGE (Magic & MP)
    MOOGLE(R.string.pet_moogle_name, R.string.pet_moogle_desc, "🦇", PetBonusType.EXP_BOOST, 0.50f, 50000, Rarity.RARE, listOf(CombatStat.MAG, CombatStat.MP)),

    // SUSTAIN (HP & MP)
    CAT(R.string.pet_cat_name, R.string.pet_cat_desc, "🐱", PetBonusType.GIL_FIND, 0.30f, 25000, Rarity.COMMON, listOf(CombatStat.HP, CombatStat.MP)),

    // GLASS CANNON (Attack & Magic)
    CACTUAR(R.string.pet_cactuar_name, R.string.pet_cactuar_desc, "🌵", PetBonusType.CRIT_CHANCE, 15f, 100000, Rarity.EPIC, listOf(CombatStat.ATK, CombatStat.MAG)),

    // BRUISER (Attack & Defense)
    TONBERRY(R.string.pet_tonberry_name, R.string.pet_tonberry_desc, "🔪", PetBonusType.CRIT_DAMAGE, 100f, 250000, Rarity.LEGENDARY, listOf(CombatStat.ATK, CombatStat.DEF))
}

// Keep Tier2Pet multiplier at 3% per rank
data class Tier2Pet(
    val petId: String,
    val rarity: Rarity = Rarity.COMMON,
    val cardsCollected: Int = 0,
    val currentRank: Int = 0
) {
    fun getStatMultiplier(): Float = 1f + (currentRank * 0.03f) // 3% to both stats per rank

    fun getCardsNeededForNextRank(): Int {
// ... rest of Tier2Pet remains identical ...
        return when (currentRank) {
            0 -> 5
            1 -> 10
            2 -> 20
            3 -> 35
            4 -> 50
            else -> 0
        }
    }

    fun canRankUp(): Boolean = currentRank < 5 && cardsCollected >= getCardsNeededForNextRank() && getCardsNeededForNextRank() > 0
}

enum class PetBonusType(@StringRes val nameRes: Int) {
    ITEM_FIND(R.string.stat_item_find),
    EXP_BOOST(R.string.stat_exp_boost),
    GIL_FIND(R.string.stat_gil_find),
    CRIT_CHANCE(R.string.stat_crit_chance),
    CRIT_DAMAGE(R.string.stat_crit_damage)
}
