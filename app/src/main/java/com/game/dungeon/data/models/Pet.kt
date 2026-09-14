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
    CHOCOBO(R.string.pet_chocobo_name, R.string.pet_chocobo_desc, "🐤", PetBonusType.ITEM_FIND, 0.05f, 500),
    MOOGLE(R.string.pet_moogle_name, R.string.pet_moogle_desc, "🦇", PetBonusType.EXP_BOOST, 0.10f, 1000),
    CAT(R.string.pet_cat_name, R.string.pet_cat_desc, "🐱", PetBonusType.GIL_FIND, 0.05f, 750),
    CACTUAR(R.string.pet_cactuar_name, R.string.pet_cactuar_desc, "🌵", PetBonusType.CRIT_CHANCE, 2f, 1500),
    TONBERRY(R.string.pet_tonberry_name, R.string.pet_tonberry_desc, "🔪", PetBonusType.CRIT_DAMAGE, 10f, 2000)
}

enum class PetBonusType {
    ITEM_FIND,
    EXP_BOOST,
    GIL_FIND,
    CRIT_CHANCE,
    CRIT_DAMAGE
}
