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
) {
    val maxFloor: Int get() = if (number >= 11) 99999999 else number * 100

    fun getBiomeForFloor(floor: Int): FFBiome {
        if (number >= 11) {
            val biomesList = listOf(
                FFBiome(com.game.dungeon.R.string.biome_cornelia, 1..10, BiomeType.CORNELIA_CASTLE),
                FFBiome(com.game.dungeon.R.string.biome_chaos_shrine, 1..10, BiomeType.CHAOS_SHRINE),
                FFBiome(com.game.dungeon.R.string.biome_gurgu, 1..10, BiomeType.GURGU_VOLCANO),
                FFBiome(com.game.dungeon.R.string.biome_sea_shrine, 1..10, BiomeType.SEA_SHRINE),
                FFBiome(com.game.dungeon.R.string.biome_earth_cave, 1..10, BiomeType.EARTH_CAVE),
                FFBiome(com.game.dungeon.R.string.biome_crystal_tower, 1..10, BiomeType.CRYSTAL_TOWER),
                FFBiome(com.game.dungeon.R.string.biome_pandaemonium, 1..10, BiomeType.PANDAEMONIUM),
                FFBiome(com.game.dungeon.R.string.biome_mount_ordeals, 1..10, BiomeType.MOUNT_ORDEALS),
                FFBiome(com.game.dungeon.R.string.biome_baron_castle, 1..10, BiomeType.BARON_CASTLE),
                FFBiome(com.game.dungeon.R.string.biome_library_ancients, 1..10, BiomeType.MYSIDIAN_TOWER),
                FFBiome(com.game.dungeon.R.string.biome_narshe_mines, 1..10, BiomeType.NARSHE_MINES),
                FFBiome(com.game.dungeon.R.string.biome_magitek_factory, 1..10, BiomeType.MAGITEK_FACTORY),
                FFBiome(com.game.dungeon.R.string.biome_floating_continent, 1..10, BiomeType.FLOATING_CONTINENT),
                FFBiome(com.game.dungeon.R.string.biome_kefka_tower, 1..10, BiomeType.KEFKA_TOWER),
                FFBiome(com.game.dungeon.R.string.biome_midgar_slums, 1..10, BiomeType.MIDGAR_SEWERS),
                FFBiome(com.game.dungeon.R.string.biome_shinra_building, 1..10, BiomeType.SHINRA_BUILDING),
                FFBiome(com.game.dungeon.R.string.biome_northern_crater, 1..10, BiomeType.NORTHERN_CRATER),
                FFBiome(com.game.dungeon.R.string.biome_bevelle_temple, 1..10, BiomeType.BEVELLE_TEMPLE),
                FFBiome(com.game.dungeon.R.string.biome_omega_ruins, 1..10, BiomeType.OMEGA_RUINS),
                FFBiome(com.game.dungeon.R.string.biome_sin_interior, 1..10, BiomeType.SIN_INTERIOR)
            )
            val index = (((floor - 1) / 10).coerceAtLeast(0)) % biomesList.size
            val start = (((floor - 1) / 10).coerceAtLeast(0)) * 10 + 1
            val baseBiome = biomesList[index]
            return baseBiome.copy(floorRange = start..(start + 9))
        }
        return biomes.find { floor in it.floorRange } ?: biomes.lastOrNull() ?: FFBiome(com.game.dungeon.R.string.biome_cornelia, 1..100, BiomeType.GENERIC_DUNGEON)
    }
}

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
    val type: MonsterType = MonsterType.GOBLIN,
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

