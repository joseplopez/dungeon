package com.game.dungeon.data.models

import androidx.annotation.StringRes

data class FFDimension(
    val number: Int,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    val mainColor: Long,
    val accentColor: Long,
    val biomes: List<FFBiome>,
    val enemies: List<FFEnemyTemplate>,
    @StringRes val storyRes: Int
)

data class FFBiome(
    @StringRes val nameRes: Int,
    val floorRange: IntRange,
    val backgroundType: BiomeType
)

enum class BiomeType {
    CORNELIA_CASTLE, CHAOS_SHRINE, GURGU_VOLCANO, SEA_SHRINE, EARTH_CAVE, CRYSTAL_TOWER,
    MYSIDIAN_TOWER, PANDAEMONIUM, MOUNT_ORDEALS, BARON_CASTLE, ANCIENT_CASTLE,
    NARSHE_MINES, MAGITEK_FACTORY, KEFKA_TOWER, FLOATING_CONTINENT,
    MIDGAR_SEWERS, SHINRA_BUILDING, NORTHERN_CRATER, GOLDEN_SAUCER,
    BEVELLE_TEMPLE, OMEGA_RUINS, SIN_INTERIOR,
    GENERIC_DUNGEON
}

data class FFEnemyTemplate(
    @StringRes val nameRes: Int,
    val emoji: String,
    val minFloor: Int, val maxFloor: Int,
    val hpMult: Float = 1f,
    val atkMult: Float = 1f,
    val defMult: Float = 1f,
    val gilReward: Int,
    val magiciteChance: Float = 0.05f,
    val isBoss: Boolean = false
)

