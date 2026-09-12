package com.game.dungeon.data.models

import com.game.dungeon.R

object FFDimensionData {
    val dimensions = listOf(
        FFDimension(
            number = 1, titleRes = R.string.dim1_title,
            subtitleRes = R.string.dim1_subtitle,
            mainColor = 0xFF1A3A6B, accentColor = 0xFF4488CC,
            storyRes = R.string.dim1_story,
            biomes = listOf(
                FFBiome(R.string.biome_cornelia, 1..15, BiomeType.CORNELIA_CASTLE),
                FFBiome(R.string.biome_chaos_shrine, 16..30, BiomeType.CHAOS_SHRINE),
                FFBiome(R.string.biome_gurgu, 31..50, BiomeType.GURGU_VOLCANO),
                FFBiome(R.string.biome_sea_shrine, 51..70, BiomeType.SEA_SHRINE),
                FFBiome(R.string.biome_earth_cave, 71..90, BiomeType.EARTH_CAVE),
                FFBiome(R.string.biome_crystal_tower, 91..100, BiomeType.CRYSTAL_TOWER)
            ),
            enemies = listOf(
                FFEnemyTemplate(R.string.enemy_goblin, "👺", 1, 15, gilReward = 8),
                FFEnemyTemplate(R.string.enemy_wolf, "🐺", 1, 20, gilReward = 10),
                FFEnemyTemplate(R.string.enemy_pirate, "🏴‍☠️", 5, 25, gilReward = 15),
                FFEnemyTemplate(R.string.enemy_ogre, "👹", 15, 40, hpMult=1.5f, gilReward = 25),
                FFEnemyTemplate(R.string.enemy_evil_eye, "👁️", 20, 50, atkMult=1.3f, gilReward = 20),
                FFEnemyTemplate(R.string.enemy_garland, "⚔️", 30, 30, hpMult=3f, atkMult=2f, gilReward=200, isBoss=true),
                FFEnemyTemplate(R.string.enemy_red_flan, "🍮", 31, 55, gilReward = 22),
                FFEnemyTemplate(R.string.enemy_bomb, "💣", 40, 70, atkMult=1.5f, gilReward = 30),
                FFEnemyTemplate(R.string.enemy_sahagin, "🐟", 51, 80, gilReward = 32),
                FFEnemyTemplate(R.string.enemy_cockatrice, "🐓", 60, 85, gilReward = 35),
                FFEnemyTemplate(R.string.enemy_lich, "💀", 90, 90, hpMult=4f, atkMult=2.5f, gilReward=500, isBoss=true),
                FFEnemyTemplate(R.string.enemy_kraken, "🐙", 91, 91, hpMult=4f, atkMult=2.5f, gilReward=500, isBoss=true),
                FFEnemyTemplate(R.string.enemy_tiamat, "🐲", 92, 92, hpMult=4f, atkMult=2.5f, gilReward=500, isBoss=true),
                FFEnemyTemplate(R.string.enemy_chaos, "😱", 100, 100, hpMult=8f, atkMult=3f, gilReward=2000, isBoss=true)
            )
        ),
        FFDimension(
            number = 2, titleRes = R.string.dim2_title,
            subtitleRes = R.string.dim2_subtitle,
            mainColor = 0xFF6B1A1A, accentColor = 0xFFCC4444,
            storyRes = R.string.dim2_story,
            biomes = listOf(
                FFBiome(R.string.biome_altair, 1..20, BiomeType.CORNELIA_CASTLE),
                FFBiome(R.string.biome_kashuan, 21..50, BiomeType.CHAOS_SHRINE),
                FFBiome(R.string.biome_tropical_island, 51..70, BiomeType.SEA_SHRINE),
                FFBiome(R.string.biome_pandaemonium, 71..100, BiomeType.PANDAEMONIUM)
            ),
            enemies = listOf(
                FFEnemyTemplate(R.string.enemy_wild_rat, "🐀", 1, 15, gilReward = 10),
                FFEnemyTemplate(R.string.enemy_black_knight, "♟️", 5, 30, hpMult=1.3f, gilReward = 20),
                FFEnemyTemplate(R.string.enemy_sergeant, "💂", 10, 40, gilReward = 25),
                FFEnemyTemplate(R.string.enemy_lamia, "🐍", 20, 55, atkMult=1.4f, gilReward = 35),
                FFEnemyTemplate(R.string.enemy_adamantoise, "🐢", 40, 70, hpMult=3f, defMult=2f, gilReward = 60),
                FFEnemyTemplate(R.string.enemy_dark_knight, "🦹", 50, 80, hpMult=2f, atkMult=1.8f, gilReward = 55),
                FFEnemyTemplate(R.string.enemy_emperor, "👑", 100, 100, hpMult=10f, atkMult=4f, gilReward=3000, isBoss=true)
            )
        ),
        FFDimension(
            number = 3, titleRes = R.string.dim3_title,
            subtitleRes = R.string.dim3_subtitle,
            mainColor = 0xFF1A4A1A, accentColor = 0xFF4488CC,
            storyRes = R.string.dim3_story,
            biomes = listOf(
                FFBiome(R.string.biome_ur_village, 1..20, BiomeType.GENERIC_DUNGEON),
                FFBiome(R.string.biome_crystal_tower_dim3, 21..60, BiomeType.CRYSTAL_TOWER),
                FFBiome(R.string.biome_dark_world, 61..100, BiomeType.CHAOS_SHRINE)
            ),
            enemies = listOf(
                FFEnemyTemplate(R.string.enemy_goblin, "👺", 1, 20, gilReward = 12),
                FFEnemyTemplate(R.string.enemy_djinn, "🧞", 15, 40, atkMult=1.3f, gilReward = 30),
                FFEnemyTemplate(R.string.enemy_medusa, "🐍", 30, 60, atkMult=1.5f, gilReward = 45),
                FFEnemyTemplate(R.string.enemy_hein, "🧙", 40, 40, hpMult=3f, gilReward=400, isBoss=true),
                FFEnemyTemplate(R.string.enemy_tonberry, "🔪", 50, 80, hpMult=2f, atkMult=2f, gilReward = 80),
                FFEnemyTemplate(R.string.enemy_cloud_of_darkness, "🌑", 100, 100, hpMult=12f, atkMult=4f, gilReward=4000, isBoss=true)
            )
        )
    )

    fun getDimension(number: Int) = dimensions.getOrElse(number - 1) { dimensions.last() }
    fun getEnemiesForFloor(dimension: FFDimension, floor: Int): List<FFEnemyTemplate> =
        dimension.enemies.filter { !it.isBoss && floor in it.minFloor..it.maxFloor }
    fun getBossForFloor(dimension: FFDimension, floor: Int): FFEnemyTemplate? =
        dimension.bosses().find { floor == it.minFloor }
    private fun FFDimension.bosses() = enemies.filter { it.isBoss }
}
