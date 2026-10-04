package com.game.dungeon.data.models

import androidx.annotation.StringRes
import com.game.dungeon.R

data class JobAbilitySpec(
    val unlockLevel: Int, // 1, 10, or 25
    @StringRes val nameRes: Int,
    @StringRes val descRes: Int,
    val icon: String
)

object JobAbilityData {
    fun getAbilitiesForJob(job: HeroClass): List<JobAbilitySpec> {
        return when (job) {
            HeroClass.FREELANCER -> listOf(
                JobAbilitySpec(1, R.string.skill_freelancer_t1_name, R.string.skill_freelancer_t1_desc, "🩹"),
                JobAbilitySpec(10, R.string.skill_freelancer_t2_name, R.string.skill_freelancer_t2_desc, "🎯"),
                JobAbilitySpec(25, R.string.skill_freelancer_t3_name, R.string.skill_freelancer_t3_desc, "🃏")
            )
            HeroClass.WARRIOR -> listOf(
                JobAbilitySpec(1, R.string.skill_warrior_t1_name, R.string.skill_warrior_t1_desc, "⚔️"),
                JobAbilitySpec(10, R.string.skill_warrior_t2_name, R.string.skill_warrior_t2_desc, "🛡️"),
                JobAbilitySpec(25, R.string.skill_warrior_t3_name, R.string.skill_warrior_t3_desc, "🪓")
            )
            HeroClass.WHITE_MAGE -> listOf(
                JobAbilitySpec(1, R.string.skill_white_mage_t1_name, R.string.skill_white_mage_t1_desc, "✨"),
                JobAbilitySpec(10, R.string.skill_white_mage_t2_name, R.string.skill_white_mage_t2_desc, "🛡️"),
                JobAbilitySpec(25, R.string.skill_white_mage_t3_name, R.string.skill_white_mage_t3_desc, "💖")
            )
            HeroClass.BLACK_MAGE -> listOf(
                JobAbilitySpec(1, R.string.skill_black_mage_t1_name, R.string.skill_black_mage_t1_desc, "🔥"),
                JobAbilitySpec(10, R.string.skill_black_mage_t2_name, R.string.skill_black_mage_t2_desc, "⚡"),
                JobAbilitySpec(25, R.string.skill_black_mage_t3_name, R.string.skill_black_mage_t3_desc, "☄️")
            )
            HeroClass.THIEF -> listOf(
                JobAbilitySpec(1, R.string.skill_thief_t1_name, R.string.skill_thief_t1_desc, "🗡️"),
                JobAbilitySpec(10, R.string.skill_thief_t2_name, R.string.skill_thief_t2_desc, "💰"),
                JobAbilitySpec(25, R.string.skill_thief_t3_name, R.string.skill_thief_t3_desc, "👤")
            )
            HeroClass.MONK -> listOf(
                JobAbilitySpec(1, R.string.skill_monk_t1_name, R.string.skill_monk_t1_desc, "👊"),
                JobAbilitySpec(10, R.string.skill_monk_t2_name, R.string.skill_monk_t2_desc, "💥"),
                JobAbilitySpec(25, R.string.skill_monk_t3_name, R.string.skill_monk_t3_desc, "⚡")
            )
            HeroClass.KNIGHT -> listOf(
                JobAbilitySpec(1, R.string.skill_knight_t1_name, R.string.skill_knight_t1_desc, "🛡️"),
                JobAbilitySpec(10, R.string.skill_knight_t2_name, R.string.skill_knight_t2_desc, "🏰"),
                JobAbilitySpec(25, R.string.skill_knight_t3_name, R.string.skill_knight_t3_desc, "⚔️")
            )
            HeroClass.PALADIN -> listOf(
                JobAbilitySpec(1, R.string.skill_paladin_t1_name, R.string.skill_paladin_t1_desc, "⚜️"),
                JobAbilitySpec(10, R.string.skill_paladin_t2_name, R.string.skill_paladin_t2_desc, "✨"),
                JobAbilitySpec(25, R.string.skill_paladin_t3_name, R.string.skill_paladin_t3_desc, "🌟")
            )
            HeroClass.RED_MAGE -> listOf(
                JobAbilitySpec(1, R.string.skill_red_mage_t1_name, R.string.skill_red_mage_t1_desc, "🎩"),
                JobAbilitySpec(10, R.string.skill_red_mage_t2_name, R.string.skill_red_mage_t2_desc, "⚡"),
                JobAbilitySpec(25, R.string.skill_red_mage_t3_name, R.string.skill_red_mage_t3_desc, "🔮")
            )
            HeroClass.SUMMONER -> listOf(
                JobAbilitySpec(1, R.string.skill_summoner_t1_name, R.string.skill_summoner_t1_desc, "🔥"),
                JobAbilitySpec(10, R.string.skill_summoner_t2_name, R.string.skill_summoner_t2_desc, "⚡"),
                JobAbilitySpec(25, R.string.skill_summoner_t3_name, R.string.skill_summoner_t3_desc, "🐉")
            )
            HeroClass.NINJA -> listOf(
                JobAbilitySpec(1, R.string.skill_ninja_t1_name, R.string.skill_ninja_t1_desc, "🥷"),
                JobAbilitySpec(10, R.string.skill_ninja_t2_name, R.string.skill_ninja_t2_desc, "👤"),
                JobAbilitySpec(25, R.string.skill_ninja_t3_name, R.string.skill_ninja_t3_desc, "🗡️")
            )
            HeroClass.DRAGOON -> listOf(
                JobAbilitySpec(1, R.string.skill_dragoon_t1_name, R.string.skill_dragoon_t1_desc, "🐉"),
                JobAbilitySpec(10, R.string.skill_dragoon_t2_name, R.string.skill_dragoon_t2_desc, "🗡️"),
                JobAbilitySpec(25, R.string.skill_dragoon_t3_name, R.string.skill_dragoon_t3_desc, "💫")
            )
            HeroClass.BARD -> listOf(
                JobAbilitySpec(1, R.string.skill_bard_t1_name, R.string.skill_bard_t1_desc, "🎵"),
                JobAbilitySpec(10, R.string.skill_bard_t2_name, R.string.skill_bard_t2_desc, "🎶"),
                JobAbilitySpec(25, R.string.skill_bard_t3_name, R.string.skill_bard_t3_desc, "🎺")
            )
            HeroClass.SAMURAI -> listOf(
                JobAbilitySpec(1, R.string.skill_samurai_t1_name, R.string.skill_samurai_t1_desc, "🗾"),
                JobAbilitySpec(10, R.string.skill_samurai_t2_name, R.string.skill_samurai_t2_desc, "🪙"),
                JobAbilitySpec(25, R.string.skill_samurai_t3_name, R.string.skill_samurai_t3_desc, "⚔️")
            )
            HeroClass.ONION_KNIGHT -> listOf(
                JobAbilitySpec(1, R.string.skill_onion_knight_t1_name, R.string.skill_onion_knight_t1_desc, "🧅"),
                JobAbilitySpec(10, R.string.skill_onion_knight_t2_name, R.string.skill_onion_knight_t2_desc, "💥"),
                JobAbilitySpec(25, R.string.skill_onion_knight_t3_name, R.string.skill_onion_knight_t3_desc, "⚡")
            )
            HeroClass.MIME -> listOf(
                JobAbilitySpec(1, R.string.skill_mime_t1_name, R.string.skill_mime_t1_desc, "🤡"),
                JobAbilitySpec(10, R.string.skill_mime_t2_name, R.string.skill_mime_t2_desc, "✨"),
                JobAbilitySpec(25, R.string.skill_mime_t3_name, R.string.skill_mime_t3_desc, "🌟")
            )
            HeroClass.NECROMANCER -> listOf(
                JobAbilitySpec(1, R.string.skill_necromancer_t1_name, R.string.skill_necromancer_t1_desc, "💀"),
                JobAbilitySpec(10, R.string.skill_necromancer_t2_name, R.string.skill_necromancer_t2_desc, "🟣"),
                JobAbilitySpec(25, R.string.skill_necromancer_t3_name, R.string.skill_necromancer_t3_desc, "🌌")
            )
            HeroClass.BLUE_MAGE -> listOf(
                JobAbilitySpec(1, R.string.skill_blue_mage_t1_name, R.string.skill_blue_mage_t1_desc, "📘"),
                JobAbilitySpec(10, R.string.skill_blue_mage_t2_name, R.string.skill_blue_mage_t2_desc, "🍃"),
                JobAbilitySpec(25, R.string.skill_blue_mage_t3_name, R.string.skill_blue_mage_t3_desc, "🛡️")
            )
        }
    }

    fun getActiveAbilityForLevel(job: HeroClass, level: Int): JobAbilitySpec? {
        val abilities = getAbilitiesForJob(job)
        return when {
            level >= 25 -> abilities[2]
            level >= 10 -> abilities[1]
            level >= 1 -> abilities[0]
            else -> null
        }
    }
}
