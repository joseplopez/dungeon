package com.game.dungeon.data.models

import androidx.annotation.StringRes
import com.game.dungeon.R

enum class RelicType(
    @StringRes val nameRes: Int,
    @StringRes val descRes: Int
) {
    ATTACK(R.string.relic_attack_name, R.string.relic_attack_desc),
    HP(R.string.relic_hp_name, R.string.relic_hp_desc),
    MP(R.string.relic_mp_name, R.string.relic_mp_desc),
    MAGIC(R.string.relic_magic_name, R.string.relic_magic_desc),
    DEFENSE(R.string.relic_defense_name, R.string.relic_defense_desc),
    CRIT_CHANCE(R.string.relic_crit_chance_name, R.string.relic_crit_chance_desc),
    CRIT_DAMAGE(R.string.relic_crit_damage_name, R.string.relic_crit_damage_desc),
    GOLD(R.string.relic_gold_name, R.string.relic_gold_desc),
    MAGICITE_FIND(R.string.relic_magicite_name, R.string.relic_magicite_desc),
    MAGNET(R.string.relic_magnet_name, R.string.relic_magnet_desc),
    POCKETS(R.string.relic_pockets_name, R.string.relic_pockets_desc),
    DOUBLE_LOOT(R.string.relic_double_loot_name, R.string.relic_double_loot_desc)
}
