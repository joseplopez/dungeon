package com.game.dungeon.data.models

object FFDimensionData {
    val dimensions = listOf(
        FFDimension(
            number = 1, title = "Warriors of Light",
            subtitle = "Final Fantasy I",
            mainColor = 0xFF1A3A6B, accentColor = 0xFF4488CC,
            storyIntro = "Four Warriors of Light arise to restore the crystals stolen by the Forces of Chaos...",
            biomes = listOf(
                FFBiome("Cornelia Region", 1..15, BiomeType.CORNELIA_CASTLE),
                FFBiome("Chaos Shrine", 16..30, BiomeType.CHAOS_SHRINE),
                FFBiome("Gurgu Volcano", 31..50, BiomeType.GURGU_VOLCANO),
                FFBiome("Sea Shrine", 51..70, BiomeType.SEA_SHRINE),
                FFBiome("Earth Cave", 71..90, BiomeType.EARTH_CAVE),
                FFBiome("Temple of Fiends", 91..100, BiomeType.CRYSTAL_TOWER)
            ),
            enemies = listOf(
                FFEnemyTemplate("Goblin", "👺", 1, 15, gilReward = 8),
                FFEnemyTemplate("Wolf", "🐺", 1, 20, gilReward = 10),
                FFEnemyTemplate("Pirate", "🏴‍☠️", 5, 25, gilReward = 15),
                FFEnemyTemplate("Ogre", "👹", 15, 40, hpMult=1.5f, gilReward = 25),
                FFEnemyTemplate("Evil Eye", "👁️", 20, 50, atkMult=1.3f, gilReward = 20),
                FFEnemyTemplate("Garland", "⚔️", 30, 30, hpMult=3f, atkMult=2f, gilReward=200, isBoss=true),
                FFEnemyTemplate("Red Flan", "🍮", 31, 55, gilReward = 22),
                FFEnemyTemplate("Bomb", "💣", 40, 70, atkMult=1.5f, gilReward = 30),
                FFEnemyTemplate("Sahagin", "🐟", 51, 80, gilReward = 32),
                FFEnemyTemplate("Cockatrice", "🐓", 60, 85, gilReward = 35),
                FFEnemyTemplate("Lich", "💀", 90, 90, hpMult=4f, atkMult=2.5f, gilReward=500, isBoss=true),
                FFEnemyTemplate("Kraken", "🐙", 91, 91, hpMult=4f, atkMult=2.5f, gilReward=500, isBoss=true),
                FFEnemyTemplate("Tiamat", "🐲", 92, 92, hpMult=4f, atkMult=2.5f, gilReward=500, isBoss=true),
                FFEnemyTemplate("CHAOS", "😱", 100, 100, hpMult=8f, atkMult=3f, gilReward=2000, isBoss=true)
            )
        ),
        FFDimension(
            number = 2, title = "Rebellion of Souls",
            subtitle = "Final Fantasy II",
            mainColor = 0xFF6B1A1A, accentColor = 0xFFCC4444,
            storyIntro = "The Palamecian Empire spreads darkness. Firion and his rebel friends fight for freedom...",
            biomes = listOf(
                FFBiome("Altair Region", 1..20, BiomeType.CORNELIA_CASTLE),
                FFBiome("Kashuan Keep", 21..50, BiomeType.CHAOS_SHRINE),
                FFBiome("Tropical Island", 51..70, BiomeType.SEA_SHRINE),
                FFBiome("Pandaemonium", 71..100, BiomeType.PANDAEMONIUM)
            ),
            enemies = listOf(
                FFEnemyTemplate("Wild Rat", "🐀", 1, 15, gilReward = 10),
                FFEnemyTemplate("Black Knight", "♟️", 5, 30, hpMult=1.3f, gilReward = 20),
                FFEnemyTemplate("Sergeant", "💂", 10, 40, gilReward = 25),
                FFEnemyTemplate("Lamia", "🐍", 20, 55, atkMult=1.4f, gilReward = 35),
                FFEnemyTemplate("Adamantoise", "🐢", 40, 70, hpMult=3f, defMult=2f, gilReward = 60),
                FFEnemyTemplate("Dark Knight", "🦹", 50, 80, hpMult=2f, atkMult=1.8f, gilReward = 55),
                FFEnemyTemplate("Emperor Mateus", "👑", 100, 100, hpMult=10f, atkMult=4f, gilReward=3000, isBoss=true)
            )
        ),
        FFDimension(
            number = 3, title = "Light of the Crystals",
            subtitle = "Final Fantasy III",
            mainColor = 0xFF1A4A1A, accentColor = 0xFF4488CC,
            storyIntro = "Orphaned children chosen by the Crystals of Light must restore balance to the world...",
            biomes = listOf(
                FFBiome("Ur Village", 1..20, BiomeType.GENERIC_DUNGEON),
                FFBiome("Crystal Tower", 21..60, BiomeType.CRYSTAL_TOWER),
                FFBiome("Dark World", 61..100, BiomeType.CHAOS_SHRINE)
            ),
            enemies = listOf(
                FFEnemyTemplate("Goblin", "👺", 1, 20, gilReward = 12),
                FFEnemyTemplate("Djinn", "🧞", 15, 40, atkMult=1.3f, gilReward = 30),
                FFEnemyTemplate("Medusa", "🐍", 30, 60, atkMult=1.5f, gilReward = 45),
                FFEnemyTemplate("Hein", "🧙", 40, 40, hpMult=3f, gilReward=400, isBoss=true),
                FFEnemyTemplate("Tonberry", "🔪", 50, 80, hpMult=2f, atkMult=2f, gilReward = 80),
                FFEnemyTemplate("Cloud of Darkness", "🌑", 100, 100, hpMult=12f, atkMult=4f, gilReward=4000, isBoss=true)
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
