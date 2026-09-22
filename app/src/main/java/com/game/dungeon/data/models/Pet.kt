package com.game.dungeon.data.models

import androidx.annotation.StringRes
import com.game.dungeon.R

enum class PetType(
    @StringRes val nameRes: Int,
    @StringRes val descRes: Int,
    val emoji: String,
    val bonusType: PetBonusType,
    val bonusValue: Float,
    val unlockCost: Int
) {
    CHOCOBO(R.string.pet_chocobo_name, R.string.pet_chocobo_desc, "🐤", PetBonusType.ITEM_FIND, 0.20f, 10000),
    MOOGLE(R.string.pet_moogle_name, R.string.pet_moogle_desc, "🦇", PetBonusType.EXP_BOOST, 0.50f, 50000),
    CAT(R.string.pet_cat_name, R.string.pet_cat_desc, "🐱", PetBonusType.GIL_FIND, 0.30f, 25000),
    CACTUAR(R.string.pet_cactuar_name, R.string.pet_cactuar_desc, "🌵", PetBonusType.CRIT_CHANCE, 15f, 100000),
    TONBERRY(R.string.pet_tonberry_name, R.string.pet_tonberry_desc, "🔪", PetBonusType.CRIT_DAMAGE, 100f, 250000)
}

enum class PetBonusType(@StringRes val nameRes: Int) {
    ITEM_FIND(R.string.stat_item_find),
    EXP_BOOST(R.string.stat_exp_boost),
    GIL_FIND(R.string.stat_gil_find),
    CRIT_CHANCE(R.string.stat_crit_chance),
    CRIT_DAMAGE(R.string.stat_crit_damage)
}
