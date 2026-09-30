package com.game.dungeon.data.models

import com.game.dungeon.R

object FFDimensionData {
    val dimensions = listOf(

        // ================================================================
        // DIMENSION 1 — FINAL FANTASY I: Warriors of Light
        // 100 floors
        // Palette: royal blue, slate grey
        // ================================================================
        FFDimension(
            number = 1, titleRes = R.string.dim1_title,
            subtitleRes = R.string.dim1_subtitle,
            mainColor = 0xFF1A3A6B, accentColor = 0xFF4488CC,
            storyRes = R.string.dim1_story,
            biomes = listOf(
                FFBiome(R.string.biome_cornelia,    1..18,   BiomeType.CORNELIA_CASTLE),
                FFBiome(R.string.biome_chaos_shrine,19..35,  BiomeType.CHAOS_SHRINE),
                FFBiome(R.string.biome_gurgu,       36..55,  BiomeType.GURGU_VOLCANO),
                FFBiome(R.string.biome_sea_shrine,  56..74,  BiomeType.SEA_SHRINE),
                FFBiome(R.string.biome_earth_cave,  75..90,  BiomeType.EARTH_CAVE),
                FFBiome(R.string.biome_crystal_tower,91..100,BiomeType.CRYSTAL_TOWER)
            ),
            enemies = listOf(
                // ── Regular enemies (floors 1-99) ───────────────────────
                FFEnemyTemplate(R.string.enemy_goblin,     "👺", 1,  22,  gilReward = 8),
                FFEnemyTemplate(R.string.enemy_wolf,       "🐺", 1,  28,  gilReward = 10),
                FFEnemyTemplate(R.string.enemy_pirate,     "🏴‍☠️", 5,  38,  gilReward = 15),
                FFEnemyTemplate(R.string.enemy_ogre,       "👹", 18, 52,  hpMult = 1.5f, gilReward = 25),
                FFEnemyTemplate(R.string.enemy_evil_eye,   "👁️", 22, 62,  atkMult = 1.3f, gilReward = 20),
                FFEnemyTemplate(R.string.enemy_red_flan,   "🍮", 36, 70,  gilReward = 22),
                FFEnemyTemplate(R.string.enemy_bomb,       "💣", 45, 88,  atkMult = 1.5f, gilReward = 30),
                FFEnemyTemplate(R.string.enemy_sahagin,    "🐟", 56, 99,  gilReward = 32),
                FFEnemyTemplate(R.string.enemy_cockatrice, "🐓", 65, 99,  gilReward = 35),
                FFEnemyTemplate(R.string.enemy_zombie,     "🧟", 75, 99,  hpMult = 1.2f, gilReward = 28),
                FFEnemyTemplate(R.string.enemy_wyvern,     "🦎", 80, 99,  hpMult = 1.4f, atkMult = 1.3f, gilReward = 40),
                // ── Mini-bosses & Bosses ─────────────────────────────────
                FFEnemyTemplate(R.string.enemy_garland,    "⚔️", 20, 20,
                    hpMult = 3.0f, atkMult = 1.8f, gilReward = 180, isBoss = true),
                FFEnemyTemplate(R.string.enemy_astos,      "🧙", 40, 40,
                    hpMult = 3.5f, atkMult = 2.0f, defMult = 1.5f, gilReward = 300, isBoss = true),
                FFEnemyTemplate(R.string.enemy_lich,       "💀", 60, 60,
                    hpMult = 5.0f, atkMult = 2.5f, gilReward = 550, isBoss = true),
                FFEnemyTemplate(R.string.enemy_marilith,   "🐍", 75, 75,
                    hpMult = 5.5f, atkMult = 2.6f, gilReward = 620, isBoss = true),
                FFEnemyTemplate(R.string.enemy_kraken,     "🐙", 85, 85,
                    hpMult = 6.0f, atkMult = 2.7f, gilReward = 720, isBoss = true),
                FFEnemyTemplate(R.string.enemy_tiamat,     "🐲", 95, 95,
                    hpMult = 6.5f, atkMult = 2.8f, gilReward = 860, isBoss = true),
                FFEnemyTemplate(R.string.enemy_chaos,      "😱", 100, 100,
                    hpMult = 10.0f, atkMult = 3.5f, defMult = 2.0f, gilReward = 2500, isBoss = true)
            )
        ),

        // ================================================================
        // DIMENSION 2 — FINAL FANTASY II: Rebellion of Souls
        // 200 floors
        // Palette: deep crimson, dark red
        // ================================================================
        FFDimension(
            number = 2, titleRes = R.string.dim2_title,
            subtitleRes = R.string.dim2_subtitle,
            mainColor = 0xFF6B1A1A, accentColor = 0xFFCC4444,
            storyRes = R.string.dim2_story,
            biomes = listOf(
                FFBiome(R.string.biome_altair,       1..35,   BiomeType.CORNELIA_CASTLE),
                FFBiome(R.string.biome_kashuan,      36..80,  BiomeType.CHAOS_SHRINE),
                FFBiome(R.string.biome_tropical_island,81..130,BiomeType.SEA_SHRINE),
                FFBiome(R.string.biome_pandaemonium, 131..200,BiomeType.PANDAEMONIUM)
            ),
            enemies = listOf(
                // ── Regular enemies ──────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_wild_rat,      "🐀",  1,  45,  gilReward = 12),
                FFEnemyTemplate(R.string.enemy_black_knight,  "♟️",  5,  80,  hpMult = 1.3f, gilReward = 22),
                FFEnemyTemplate(R.string.enemy_sergeant,      "💂",  10, 110, gilReward = 28),
                FFEnemyTemplate(R.string.enemy_lamia,         "🐍",  20, 150, atkMult = 1.4f, gilReward = 38),
                FFEnemyTemplate(R.string.enemy_sea_snake,     "🐍",  40, 140, atkMult = 1.2f, gilReward = 32),
                FFEnemyTemplate(R.string.enemy_adamantoise,   "🐢",  45, 190, hpMult = 3.0f, defMult = 2.0f, gilReward = 68),
                FFEnemyTemplate(R.string.enemy_dark_knight,   "🦹",  60, 199, hpMult = 2.0f, atkMult = 1.8f, gilReward = 60),
                FFEnemyTemplate(R.string.enemy_behemoth,      "🐂",  80, 199, hpMult = 2.5f, atkMult = 2.0f, gilReward = 80),
                FFEnemyTemplate(R.string.enemy_toad,          "🐸",  30, 120, gilReward = 20),
                FFEnemyTemplate(R.string.enemy_cockatrice,    "🐓",  50, 150, atkMult = 1.2f, gilReward = 35),
                // ── Bosses ───────────────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_leon,          "🗡️",  30, 30,
                    hpMult = 3.5f, atkMult = 2.0f, gilReward = 250, isBoss = true),
                FFEnemyTemplate(R.string.enemy_borghen,       "💂",  60, 60,
                    hpMult = 4.0f, atkMult = 2.2f, gilReward = 380, isBoss = true),
                FFEnemyTemplate(R.string.enemy_gottos,        "🏴‍☠️", 90, 90,
                    hpMult = 4.5f, atkMult = 2.3f, gilReward = 500, isBoss = true),
                FFEnemyTemplate(R.string.enemy_roundworm,     "🪱",  120,120,
                    hpMult = 5.0f, atkMult = 2.0f, defMult = 1.5f, gilReward = 620, isBoss = true),
                FFEnemyTemplate(R.string.enemy_tiamat,        "🐲",  150,150,
                    hpMult = 6.0f, atkMult = 2.5f, gilReward = 850, isBoss = true),
                FFEnemyTemplate(R.string.enemy_cyclone,       "🌀",  175,175,
                    hpMult = 7.0f, atkMult = 2.8f, gilReward = 1100, isBoss = true),
                FFEnemyTemplate(R.string.enemy_emperor,       "👑",  200,200,
                    hpMult = 12.0f, atkMult = 4.0f, defMult = 2.5f, gilReward = 4000, isBoss = true)
            )
        ),

        // ================================================================
        // DIMENSION 3 — FINAL FANTASY III: Light of the Crystals
        // 300 floors
        // Palette: emerald green, crystal blue
        // ================================================================
        FFDimension(
            number = 3, titleRes = R.string.dim3_title,
            subtitleRes = R.string.dim3_subtitle,
            mainColor = 0xFF1A4A1A, accentColor = 0xFF44CCCC,
            storyRes = R.string.dim3_story,
            biomes = listOf(
                FFBiome(R.string.biome_ur_village,      1..50,   BiomeType.GENERIC_DUNGEON),
                FFBiome(R.string.biome_nepto_shrine,    51..110, BiomeType.SEA_SHRINE),
                FFBiome(R.string.biome_crystal_tower_dim3,111..200,BiomeType.CRYSTAL_TOWER),
                FFBiome(R.string.biome_dark_world,      201..300,BiomeType.CHAOS_SHRINE)
            ),
            enemies = listOf(
                // ── Regular enemies ──────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_goblin,       "👺",  1,  60,  gilReward = 14),
                FFEnemyTemplate(R.string.enemy_djinn,        "🧞",  15, 100, atkMult = 1.3f, gilReward = 32),
                FFEnemyTemplate(R.string.enemy_medusa,       "🐍",  35, 150, atkMult = 1.5f, gilReward = 48),
                FFEnemyTemplate(R.string.enemy_tonberry,     "🔪",  60, 299, hpMult = 2.0f, atkMult = 2.0f, gilReward = 85),
                FFEnemyTemplate(R.string.enemy_giant,        "🗿",  80, 200, hpMult = 2.0f, atkMult = 1.6f, gilReward = 65),
                FFEnemyTemplate(R.string.enemy_lamia,        "🐍",  50, 180, atkMult = 1.4f, gilReward = 50),
                FFEnemyTemplate(R.string.enemy_iron_giant,   "🤖",  120,299, hpMult = 3.0f, atkMult = 1.8f, gilReward = 95),
                FFEnemyTemplate(R.string.enemy_behemoth,     "🐂",  150,299, hpMult = 2.5f, atkMult = 2.2f, gilReward = 100),
                FFEnemyTemplate(R.string.enemy_malboro,      "🌿",  180,299, atkMult = 2.0f, gilReward = 90),
                // ── Bosses ───────────────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_djinn_boss,   "🧞",  50, 50,
                    hpMult = 4.0f, atkMult = 2.0f, gilReward = 350, isBoss = true),
                FFEnemyTemplate(R.string.enemy_nepto_dragon, "🐉",  80, 80,
                    hpMult = 4.5f, atkMult = 2.2f, gilReward = 480, isBoss = true),
                FFEnemyTemplate(R.string.enemy_hein,         "🧙",  100,100,
                    hpMult = 5.0f, atkMult = 2.3f, defMult = 1.5f, gilReward = 650, isBoss = true),
                FFEnemyTemplate(R.string.enemy_kraken,       "🐙",  150,150,
                    hpMult = 6.0f, atkMult = 2.5f, gilReward = 900, isBoss = true),
                FFEnemyTemplate(R.string.enemy_garuda,       "🦅",  180,180,
                    hpMult = 6.5f, atkMult = 2.6f, gilReward = 1050, isBoss = true),
                FFEnemyTemplate(R.string.enemy_goldor,       "💰",  200,200,
                    hpMult = 7.0f, atkMult = 2.5f, defMult = 2.0f, gilReward = 1200, isBoss = true),
                FFEnemyTemplate(R.string.enemy_xande,        "💎",  250,250,
                    hpMult = 8.0f, atkMult = 3.0f, gilReward = 1800, isBoss = true),
                FFEnemyTemplate(R.string.enemy_cloud_of_darkness,"🌑",300,300,
                    hpMult = 14.0f, atkMult = 4.5f, defMult = 2.5f, gilReward = 5500, isBoss = true)
            )
        ),

        // ================================================================
        // DIMENSION 4 — FINAL FANTASY IV: The Second Moon
        // 400 floors
        // Palette: deep purple-blue, lunar silver
        // ================================================================
        FFDimension(
            number = 4, titleRes = R.string.dim4_title,
            subtitleRes = R.string.dim4_subtitle,
            mainColor = 0xFF2A1A5A, accentColor = 0xFF8866DD,
            storyRes = R.string.dim4_story,
            biomes = listOf(
                FFBiome(R.string.biome_baron_castle,    1..60,   BiomeType.BARON_CASTLE),
                FFBiome(R.string.biome_mount_ordeals,   61..130, BiomeType.MOUNT_ORDEALS),
                FFBiome(R.string.biome_dwarven_castle,  131..220,BiomeType.EARTH_CAVE),
                FFBiome(R.string.biome_lunar_subterrane,221..350,BiomeType.CHAOS_SHRINE),
                FFBiome(R.string.biome_moon_core,       351..400,BiomeType.CRYSTAL_TOWER)
            ),
            enemies = listOf(
                // ── Regular enemies ──────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_captain,     "💂",  1,  70,  gilReward = 16),
                FFEnemyTemplate(R.string.enemy_antlion,     "🦂",  15, 90,  hpMult = 1.4f, gilReward = 25),
                FFEnemyTemplate(R.string.enemy_mist_dragon, "🌫️",  30, 110, atkMult = 1.5f, gilReward = 35),
                FFEnemyTemplate(R.string.enemy_bomb,        "💣",  40, 160, atkMult = 1.6f, gilReward = 40),
                FFEnemyTemplate(R.string.enemy_dark_imp,    "😈",  60, 200, atkMult = 1.4f, gilReward = 42),
                FFEnemyTemplate(R.string.enemy_behemoth,    "🐂",  90, 280, hpMult = 2.5f, atkMult = 2.0f, gilReward = 95),
                FFEnemyTemplate(R.string.enemy_malboro,     "🌿",  120,320, atkMult = 2.0f, gilReward = 85),
                FFEnemyTemplate(R.string.enemy_iron_giant,  "🤖",  150,360, hpMult = 3.0f, atkMult = 1.8f, gilReward = 110),
                FFEnemyTemplate(R.string.enemy_lunar_dragon,"🐉",  220,399, hpMult = 2.8f, atkMult = 2.2f, gilReward = 130),
                FFEnemyTemplate(R.string.enemy_mindflayer,  "🧠",  180,350, atkMult = 2.3f, gilReward = 100),
                // ── Bosses ───────────────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_mist_dragon_boss,"🌫️",40,40,
                    hpMult = 3.5f, atkMult = 2.0f, gilReward = 300, isBoss = true),
                FFEnemyTemplate(R.string.enemy_antlion_boss,"🦂",  70, 70,
                    hpMult = 4.0f, atkMult = 2.0f, gilReward = 450, isBoss = true),
                FFEnemyTemplate(R.string.enemy_golbez,      "🌑",  100,100,
                    hpMult = 5.5f, atkMult = 2.5f, defMult = 1.8f, gilReward = 750, isBoss = true),
                FFEnemyTemplate(R.string.enemy_cagnazzo,    "🦀",  140,140,
                    hpMult = 5.5f, atkMult = 2.3f, defMult = 2.0f, gilReward = 850, isBoss = true),
                FFEnemyTemplate(R.string.enemy_barbariccia, "🌪️",  180,180,
                    hpMult = 6.0f, atkMult = 2.5f, gilReward = 1050, isBoss = true),
                FFEnemyTemplate(R.string.enemy_scarmiglione,"💀",  220,220,
                    hpMult = 6.5f, atkMult = 2.6f, gilReward = 1200, isBoss = true),
                FFEnemyTemplate(R.string.enemy_rubicante,   "🔥",  270,270,
                    hpMult = 7.5f, atkMult = 2.8f, gilReward = 1550, isBoss = true),
                FFEnemyTemplate(R.string.enemy_dark_bahamut,"🐉",  330,330,
                    hpMult = 9.0f, atkMult = 3.2f, gilReward = 2200, isBoss = true),
                FFEnemyTemplate(R.string.enemy_zeromus,     "💢",  400,400,
                    hpMult = 16.0f, atkMult = 5.0f, defMult = 2.5f, gilReward = 7000, isBoss = true)
            )
        ),

        // ================================================================
        // DIMENSION 5 — FINAL FANTASY V: A Warrior's Journey
        // 500 floors
        // Palette: warm amber, adventure brown
        // ================================================================
        FFDimension(
            number = 5, titleRes = R.string.dim5_title,
            subtitleRes = R.string.dim5_subtitle,
            mainColor = 0xFF5A3A0A, accentColor = 0xFFDDAA44,
            storyRes = R.string.dim5_story,
            biomes = listOf(
                FFBiome(R.string.biome_tule_wind_shrine, 1..80,   BiomeType.GENERIC_DUNGEON),
                FFBiome(R.string.biome_karnak_castle,    81..180, BiomeType.BARON_CASTLE),
                FFBiome(R.string.biome_library_ancients, 181..280,BiomeType.MYSIDIAN_TOWER),
                FFBiome(R.string.biome_exdeath_castle,   281..420,BiomeType.CHAOS_SHRINE),
                FFBiome(R.string.biome_void,             421..500,BiomeType.CRYSTAL_TOWER)
            ),
            enemies = listOf(
                // ── Regular enemies ──────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_goblin,      "👺",  1,  80,  gilReward = 16),
                FFEnemyTemplate(R.string.enemy_fariis,      "🧚",  10, 120, atkMult = 1.2f, gilReward = 22),
                FFEnemyTemplate(R.string.enemy_skull_eater, "💀",  30, 180, atkMult = 1.6f, gilReward = 45),
                FFEnemyTemplate(R.string.enemy_gargoyle,    "🗿",  50, 230, hpMult = 1.8f, atkMult = 1.4f, gilReward = 55),
                FFEnemyTemplate(R.string.enemy_stoker,      "🔥",  70, 280, atkMult = 1.5f, gilReward = 50),
                FFEnemyTemplate(R.string.enemy_tonberry,    "🔪",  90, 450, hpMult = 2.5f, atkMult = 2.5f, gilReward = 110),
                FFEnemyTemplate(R.string.enemy_behemoth,    "🐂",  120,380, hpMult = 2.8f, atkMult = 2.0f, gilReward = 105),
                FFEnemyTemplate(R.string.enemy_malboro,     "🌿",  160,450, atkMult = 2.2f, gilReward = 95),
                FFEnemyTemplate(R.string.enemy_omega_proto, "⚙️",  280,490, hpMult = 4.0f, atkMult = 3.0f, defMult = 2.0f, gilReward = 180),
                FFEnemyTemplate(R.string.enemy_shinryu,     "🐉",  350,490, hpMult = 4.5f, atkMult = 3.5f, gilReward = 220),
                // ── Bosses ───────────────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_wing_raptor, "🦅",  40, 40,
                    hpMult = 3.5f, atkMult = 1.8f, gilReward = 320, isBoss = true),
                FFEnemyTemplate(R.string.enemy_karlabos,    "🦂",  80, 80,
                    hpMult = 4.0f, atkMult = 2.0f, gilReward = 480, isBoss = true),
                FFEnemyTemplate(R.string.enemy_ifrit_boss,  "🔥",  120,120,
                    hpMult = 5.0f, atkMult = 2.3f, gilReward = 700, isBoss = true),
                FFEnemyTemplate(R.string.enemy_gilgamesh,   "⚔️",  160,160,
                    hpMult = 5.5f, atkMult = 2.3f, gilReward = 900, isBoss = true),
                FFEnemyTemplate(R.string.enemy_atomos,      "🌀",  200,200,
                    hpMult = 6.0f, atkMult = 2.5f, gilReward = 1100, isBoss = true),
                FFEnemyTemplate(R.string.enemy_gilgamesh_2, "⚔️",  250,250,
                    hpMult = 6.5f, atkMult = 2.6f, gilReward = 1300, isBoss = true),
                FFEnemyTemplate(R.string.enemy_exdeath,     "🌲",  350,350,
                    hpMult = 8.0f, atkMult = 3.0f, defMult = 2.0f, gilReward = 2000, isBoss = true),
                FFEnemyTemplate(R.string.enemy_omega,       "⚙️",  450,450,
                    hpMult = 12.0f, atkMult = 4.0f, defMult = 3.0f, gilReward = 4000, isBoss = true),
                FFEnemyTemplate(R.string.enemy_neo_exdeath, "🌌",  500,500,
                    hpMult = 18.0f, atkMult = 5.5f, defMult = 2.5f, gilReward = 9000, isBoss = true)
            )
        ),

        // ================================================================
        // DIMENSION 6 — FINAL FANTASY VI: The World of Ruin
        // 600 floors
        // Palette: dark emerald, mad gold
        // ================================================================
        FFDimension(
            number = 6, titleRes = R.string.dim6_title,
            subtitleRes = R.string.dim6_subtitle,
            mainColor = 0xFF1A4A2A, accentColor = 0xFFCCAA00,
            storyRes = R.string.dim6_story,
            biomes = listOf(
                FFBiome(R.string.biome_narshe_mines,     1..90,   BiomeType.NARSHE_MINES),
                FFBiome(R.string.biome_magitek_factory,  91..200, BiomeType.MAGITEK_FACTORY),
                FFBiome(R.string.biome_floating_continent,201..340,BiomeType.FLOATING_CONTINENT),
                FFBiome(R.string.biome_kefka_tower,      341..540,BiomeType.KEFKA_TOWER),
                FFBiome(R.string.biome_world_of_ruin,    541..600,BiomeType.CHAOS_SHRINE)
            ),
            enemies = listOf(
                // ── Regular enemies ──────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_mp,          "🔮",  1,  90,  gilReward = 18),
                FFEnemyTemplate(R.string.enemy_brawler,     "💪",  10, 130, hpMult = 1.3f, gilReward = 24),
                FFEnemyTemplate(R.string.enemy_magitek_armor,"🤖", 30, 200, hpMult = 1.8f, atkMult = 1.5f, gilReward = 45),
                FFEnemyTemplate(R.string.enemy_ninja,       "🥷",  50, 280, atkMult = 2.0f, gilReward = 60),
                FFEnemyTemplate(R.string.enemy_behemoth,    "🐂",  80, 380, hpMult = 3.0f, atkMult = 2.2f, gilReward = 110),
                FFEnemyTemplate(R.string.enemy_tonberry,    "🔪",  100,550, hpMult = 2.5f, atkMult = 2.5f, gilReward = 115),
                FFEnemyTemplate(R.string.enemy_malboro,     "🌿",  150,520, atkMult = 2.3f, gilReward = 100),
                FFEnemyTemplate(R.string.enemy_dragon,      "🐉",  200,550, hpMult = 3.5f, atkMult = 2.5f, gilReward = 145),
                FFEnemyTemplate(R.string.enemy_dark_force,  "🌑",  300,580, hpMult = 2.8f, atkMult = 2.8f, gilReward = 140),
                FFEnemyTemplate(R.string.enemy_magic_master,"🧙",  400,599, hpMult = 2.5f, atkMult = 3.0f, gilReward = 160),
                // ── Bosses ───────────────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_whelk,       "🐌",  50, 50,
                    hpMult = 3.5f, atkMult = 1.8f, defMult = 1.5f, gilReward = 380, isBoss = true),
                FFEnemyTemplate(R.string.enemy_vargas,      "🐻",  100,100,
                    hpMult = 4.5f, atkMult = 2.0f, gilReward = 600, isBoss = true),
                FFEnemyTemplate(R.string.enemy_number_024,  "🤖",  150,150,
                    hpMult = 5.0f, atkMult = 2.2f, defMult = 1.8f, gilReward = 850, isBoss = true),
                FFEnemyTemplate(R.string.enemy_ultros,      "🐙",  200,200,
                    hpMult = 5.5f, atkMult = 2.3f, gilReward = 1050, isBoss = true),
                FFEnemyTemplate(R.string.enemy_typhon,      "🌪️",  250,250,
                    hpMult = 6.0f, atkMult = 2.4f, gilReward = 1200, isBoss = true),
                FFEnemyTemplate(R.string.enemy_air_force,   "✈️",  300,300,
                    hpMult = 6.5f, atkMult = 2.6f, gilReward = 1450, isBoss = true),
                FFEnemyTemplate(R.string.enemy_guardian,    "🛡️",  380,380,
                    hpMult = 7.5f, atkMult = 2.8f, defMult = 2.5f, gilReward = 2000, isBoss = true),
                FFEnemyTemplate(R.string.enemy_ultima_weapon,"⚔️", 450,450,
                    hpMult = 10.0f, atkMult = 3.5f, defMult = 2.0f, gilReward = 3500, isBoss = true),
                FFEnemyTemplate(R.string.enemy_kefka,       "🃏",  600,600,
                    hpMult = 20.0f, atkMult = 6.0f, defMult = 2.5f, gilReward = 12000, isBoss = true)
            )
        ),

        // ================================================================
        // DIMENSION 7 — FINAL FANTASY VII: Jenova's Legacy
        // 700 floors
        // Palette: mako green, cyberpunk purple
        // ================================================================
        FFDimension(
            number = 7, titleRes = R.string.dim7_title,
            subtitleRes = R.string.dim7_subtitle,
            mainColor = 0xFF0A2A1A, accentColor = 0xFF00FF88,
            storyRes = R.string.dim7_story,
            biomes = listOf(
                FFBiome(R.string.biome_midgar_slums,     1..100,  BiomeType.MIDGAR_SEWERS),
                FFBiome(R.string.biome_shinra_building,  101..230,BiomeType.SHINRA_BUILDING),
                FFBiome(R.string.biome_junon_underwater, 231..360,BiomeType.SEA_SHRINE),
                FFBiome(R.string.biome_northern_crater,  361..560,BiomeType.NORTHERN_CRATER),
                FFBiome(R.string.biome_sephiroth_realm,  561..700,BiomeType.CHAOS_SHRINE)
            ),
            enemies = listOf(
                // ── Regular enemies ──────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_grunt,       "💂",  1,  100, gilReward = 20),
                FFEnemyTemplate(R.string.enemy_sweeper,     "🤖",  15, 160, hpMult = 1.5f, atkMult = 1.3f, gilReward = 30),
                FFEnemyTemplate(R.string.enemy_hellhound,   "🐺",  30, 220, atkMult = 1.5f, gilReward = 40),
                FFEnemyTemplate(R.string.enemy_jenova_cell, "🧫",  60, 320, atkMult = 1.6f, gilReward = 55),
                FFEnemyTemplate(R.string.enemy_dragon_rider,"🐉",  100,420, hpMult = 2.0f, atkMult = 1.8f, gilReward = 80),
                FFEnemyTemplate(R.string.enemy_behemoth,    "🐂",  150,520, hpMult = 3.0f, atkMult = 2.3f, gilReward = 120),
                FFEnemyTemplate(R.string.enemy_malboro,     "🌿",  200,580, atkMult = 2.4f, gilReward = 110),
                FFEnemyTemplate(R.string.enemy_tonberry,    "🔪",  250,680, hpMult = 3.0f, atkMult = 2.8f, gilReward = 145),
                FFEnemyTemplate(R.string.enemy_weapon,      "⚙️",  350,680, hpMult = 4.0f, atkMult = 3.0f, defMult = 2.0f, gilReward = 200),
                // ── Bosses ───────────────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_guard_scorpion,"🦂",50,50,
                    hpMult = 3.5f, atkMult = 1.8f, gilReward = 400, isBoss = true),
                FFEnemyTemplate(R.string.enemy_airbuster,   "✈️",  100,100,
                    hpMult = 4.5f, atkMult = 2.0f, defMult = 1.5f, gilReward = 650, isBoss = true),
                FFEnemyTemplate(R.string.enemy_jenova_birth,"🧫",  150,150,
                    hpMult = 5.0f, atkMult = 2.3f, gilReward = 900, isBoss = true),
                FFEnemyTemplate(R.string.enemy_rufus,       "🔫",  200,200,
                    hpMult = 4.5f, atkMult = 2.5f, gilReward = 1000, isBoss = true),
                FFEnemyTemplate(R.string.enemy_jenova_life, "🧫",  280,280,
                    hpMult = 6.0f, atkMult = 2.6f, gilReward = 1400, isBoss = true),
                FFEnemyTemplate(R.string.enemy_hojo,        "🧬",  380,380,
                    hpMult = 7.0f, atkMult = 2.8f, gilReward = 2000, isBoss = true),
                FFEnemyTemplate(R.string.enemy_jenova_synthesis,"🧫",480,480,
                    hpMult = 9.0f, atkMult = 3.2f, gilReward = 2800, isBoss = true),
                FFEnemyTemplate(R.string.enemy_bizarro_seph,"⚔️",  580,580,
                    hpMult = 12.0f, atkMult = 4.0f, defMult = 2.0f, gilReward = 4500, isBoss = true),
                FFEnemyTemplate(R.string.enemy_sephiroth,   "🪶",  700,700,
                    hpMult = 22.0f, atkMult = 6.5f, defMult = 2.5f, gilReward = 15000, isBoss = true)
            )
        ),

        // ================================================================
        // DIMENSION 8 — FINAL FANTASY VIII: Ultimecia's Time Compression
        // 800 floors
        // Palette: moonlit grey, cold steel blue
        // ================================================================
        FFDimension(
            number = 8, titleRes = R.string.dim8_title,
            subtitleRes = R.string.dim8_subtitle,
            mainColor = 0xFF1A2A3A, accentColor = 0xFF88AACC,
            storyRes = R.string.dim8_story,
            biomes = listOf(
                FFBiome(R.string.biome_balamb_garden,    1..110,  BiomeType.BARON_CASTLE),
                FFBiome(R.string.biome_galbadia_missile, 111..260,BiomeType.SHINRA_BUILDING),
                FFBiome(R.string.biome_deep_sea_deposit, 261..430,BiomeType.SEA_SHRINE),
                FFBiome(R.string.biome_lunatic_pandora,  431..620,BiomeType.CHAOS_SHRINE),
                FFBiome(R.string.biome_ultimecia_castle, 621..800,BiomeType.CRYSTAL_TOWER)
            ),
            enemies = listOf(
                // ── Regular enemies ──────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_galbadian_soldier,"💂",1,120,gilReward = 22),
                FFEnemyTemplate(R.string.enemy_bite_bug,    "🐛",  10, 180, atkMult = 1.3f, gilReward = 28),
                FFEnemyTemplate(R.string.enemy_geezard,     "🦎",  25, 240, hpMult = 1.4f, gilReward = 35),
                FFEnemyTemplate(R.string.enemy_wendigo,     "🐂",  60, 360, hpMult = 2.0f, atkMult = 1.6f, gilReward = 65),
                FFEnemyTemplate(R.string.enemy_elnoyle,     "🐉",  100,480, hpMult = 2.5f, atkMult = 1.8f, gilReward = 90),
                FFEnemyTemplate(R.string.enemy_behemoth,    "🐂",  150,600, hpMult = 3.0f, atkMult = 2.3f, gilReward = 130),
                FFEnemyTemplate(R.string.enemy_malboro,     "🌿",  200,700, atkMult = 2.5f, gilReward = 120),
                FFEnemyTemplate(R.string.enemy_ruby_dragon, "🔴",  300,760, hpMult = 3.5f, atkMult = 2.5f, gilReward = 165),
                FFEnemyTemplate(R.string.enemy_tonberry,    "🔪",  350,780, hpMult = 3.0f, atkMult = 3.0f, gilReward = 175),
                // ── Bosses ───────────────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_norg,        "🧅",  80, 80,
                    hpMult = 3.5f, atkMult = 1.8f, gilReward = 520, isBoss = true),
                FFEnemyTemplate(R.string.enemy_edea,        "🔮",  160,160,
                    hpMult = 5.0f, atkMult = 2.3f, gilReward = 1000, isBoss = true),
                FFEnemyTemplate(R.string.enemy_fujin_raijin,"⚡",  240,240,
                    hpMult = 5.5f, atkMult = 2.4f, gilReward = 1250, isBoss = true),
                FFEnemyTemplate(R.string.enemy_seifer,      "⚔️",  320,320,
                    hpMult = 6.0f, atkMult = 2.6f, gilReward = 1600, isBoss = true),
                FFEnemyTemplate(R.string.enemy_adel,        "👑",  440,440,
                    hpMult = 8.0f, atkMult = 3.0f, defMult = 2.0f, gilReward = 2500, isBoss = true),
                FFEnemyTemplate(R.string.enemy_trauma,      "🧠",  560,560,
                    hpMult = 9.0f, atkMult = 3.2f, gilReward = 3200, isBoss = true),
                FFEnemyTemplate(R.string.enemy_omega_weapon,"⚙️",  650,650,
                    hpMult = 15.0f, atkMult = 5.0f, defMult = 3.5f, gilReward = 6000, isBoss = true),
                FFEnemyTemplate(R.string.enemy_ultimecia,   "⏳",  800,800,
                    hpMult = 25.0f, atkMult = 7.0f, defMult = 3.0f, gilReward = 18000, isBoss = true)
            )
        ),

        // ================================================================
        // DIMENSION 9 — FINAL FANTASY IX: The Crystal's Memory
        // 900 floors
        // Palette: warm terracotta, theatrical purple
        // ================================================================
        FFDimension(
            number = 9, titleRes = R.string.dim9_title,
            subtitleRes = R.string.dim9_subtitle,
            mainColor = 0xFF3A1A4A, accentColor = 0xFFCC88DD,
            storyRes = R.string.dim9_story,
            biomes = listOf(
                FFBiome(R.string.biome_lindblum,         1..120,  BiomeType.BARON_CASTLE),
                FFBiome(R.string.biome_conde_petie,      121..280,BiomeType.GENERIC_DUNGEON),
                FFBiome(R.string.biome_oeilvert,         281..460,BiomeType.MYSIDIAN_TOWER),
                FFBiome(R.string.biome_pandemonium_ix,   461..680,BiomeType.PANDAEMONIUM),
                FFBiome(R.string.biome_memoria,          681..900,BiomeType.CRYSTAL_TOWER)
            ),
            enemies = listOf(
                // ── Regular enemies ──────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_black_mage_unit,"🔮",1,130,gilReward = 24),
                FFEnemyTemplate(R.string.enemy_zaghnol,    "🐗",  15, 200, hpMult = 1.5f, atkMult = 1.4f, gilReward = 35),
                FFEnemyTemplate(R.string.enemy_goblin,     "👺",  30, 270, gilReward = 20),
                FFEnemyTemplate(R.string.enemy_mistodon,   "🐘",  60, 380, hpMult = 2.0f, atkMult = 1.6f, gilReward = 65),
                FFEnemyTemplate(R.string.enemy_antlion,    "🦂",  90, 460, hpMult = 1.8f, atkMult = 1.7f, gilReward = 72),
                FFEnemyTemplate(R.string.enemy_tonberry,   "🔪",  150,820, hpMult = 3.0f, atkMult = 3.0f, gilReward = 185),
                FFEnemyTemplate(R.string.enemy_behemoth,   "🐂",  200,720, hpMult = 3.2f, atkMult = 2.5f, gilReward = 145),
                FFEnemyTemplate(R.string.enemy_malboro,    "🌿",  250,800, atkMult = 2.6f, gilReward = 135),
                FFEnemyTemplate(R.string.enemy_silver_dragon,"🥈",350,860, hpMult = 3.8f, atkMult = 2.8f, gilReward = 185),
                // ── Bosses ───────────────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_plant_brain,"🌳",  80, 80,
                    hpMult = 3.5f, atkMult = 1.8f, gilReward = 580, isBoss = true),
                FFEnemyTemplate(R.string.enemy_black_waltz,"🔮",  160,160,
                    hpMult = 5.0f, atkMult = 2.3f, gilReward = 1050, isBoss = true),
                FFEnemyTemplate(R.string.enemy_zorn_thorn, "🎭",  280,280,
                    hpMult = 5.5f, atkMult = 2.4f, gilReward = 1350, isBoss = true),
                FFEnemyTemplate(R.string.enemy_ralvurahva, "🗿",  380,380,
                    hpMult = 6.0f, atkMult = 2.6f, gilReward = 1700, isBoss = true),
                FFEnemyTemplate(R.string.enemy_maliris,    "🐍",  500,500,
                    hpMult = 7.0f, atkMult = 2.8f, gilReward = 2300, isBoss = true),
                FFEnemyTemplate(R.string.enemy_kuja,       "🌙",  620,620,
                    hpMult = 9.0f, atkMult = 3.3f, gilReward = 3500, isBoss = true),
                FFEnemyTemplate(R.string.enemy_trance_kuja,"🌟",  750,750,
                    hpMult = 12.0f, atkMult = 4.2f, defMult = 2.0f, gilReward = 6000, isBoss = true),
                FFEnemyTemplate(R.string.enemy_necron,     "💀",  900,900,
                    hpMult = 28.0f, atkMult = 7.5f, defMult = 3.0f, gilReward = 22000, isBoss = true)
            )
        ),

        // ================================================================
        // DIMENSION 10 — FINAL FANTASY X: Spira's Dream
        // 1000 floors — the ultimate dimension
        // Palette: ocean turquoise, sacred gold
        // ================================================================
        FFDimension(
            number = 10, titleRes = R.string.dim10_title,
            subtitleRes = R.string.dim10_subtitle,
            mainColor = 0xFF0A3A4A, accentColor = 0xFF44CCDD,
            storyRes = R.string.dim10_story,
            biomes = listOf(
                FFBiome(R.string.biome_besaid_island,    1..130,  BiomeType.SEA_SHRINE),
                FFBiome(R.string.biome_mi_ihen_highroad, 131..300,BiomeType.GENERIC_DUNGEON),
                FFBiome(R.string.biome_bevelle_temple,   301..500,BiomeType.BEVELLE_TEMPLE),
                FFBiome(R.string.biome_omega_ruins,      501..700,BiomeType.OMEGA_RUINS),
                FFBiome(R.string.biome_sin_interior,     701..900,BiomeType.SIN_INTERIOR),
                FFBiome(R.string.biome_dream_zanarkand,  901..1000,BiomeType.CRYSTAL_TOWER)
            ),
            enemies = listOf(
                // ── Regular enemies ──────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_dingo,       "🐕",  1,  140, gilReward = 25),
                FFEnemyTemplate(R.string.enemy_water_flan,  "💧",  10, 200, atkMult = 1.3f, gilReward = 30),
                FFEnemyTemplate(R.string.enemy_sahagin,     "🐟",  25, 300, gilReward = 38),
                FFEnemyTemplate(R.string.enemy_klikk,       "🦐",  50, 400, hpMult = 1.5f, atkMult = 1.4f, gilReward = 50),
                FFEnemyTemplate(R.string.enemy_guado_guardian,"💂",80, 500, hpMult = 1.8f, atkMult = 1.6f, gilReward = 70),
                FFEnemyTemplate(R.string.enemy_behemoth,    "🐂",  150,700, hpMult = 3.2f, atkMult = 2.4f, gilReward = 145),
                FFEnemyTemplate(R.string.enemy_malboro,     "🌿",  200,800, atkMult = 2.7f, gilReward = 140),
                FFEnemyTemplate(R.string.enemy_tonberry,    "🔪",  300,950, hpMult = 3.5f, atkMult = 3.2f, gilReward = 200),
                FFEnemyTemplate(R.string.enemy_great_malboro,"🌿", 400,900, hpMult = 2.5f, atkMult = 3.0f, gilReward = 190),
                FFEnemyTemplate(R.string.enemy_dark_aeon,   "🌑",  500,980, hpMult = 4.0f, atkMult = 3.0f, defMult = 2.0f, gilReward = 220),
                // ── Bosses ───────────────────────────────────────────────
                FFEnemyTemplate(R.string.enemy_klikk_boss,  "🦐",  100,100,
                    hpMult = 3.5f, atkMult = 1.8f, gilReward = 680, isBoss = true),
                FFEnemyTemplate(R.string.enemy_oblitzerator,"⚙️",  200,200,
                    hpMult = 5.0f, atkMult = 2.2f, gilReward = 1200, isBoss = true),
                FFEnemyTemplate(R.string.enemy_evrae,       "🐉",  320,320,
                    hpMult = 6.5f, atkMult = 2.6f, gilReward = 2000, isBoss = true),
                FFEnemyTemplate(R.string.enemy_seymour,     "🌸",  450,450,
                    hpMult = 8.0f, atkMult = 3.0f, defMult = 2.0f, gilReward = 2800, isBoss = true),
                FFEnemyTemplate(R.string.enemy_omega_weapon_x,"⚙️",560,560,
                    hpMult = 14.0f, atkMult = 5.0f, defMult = 3.5f, gilReward = 5500, isBoss = true),
                FFEnemyTemplate(R.string.enemy_jecht,       "🌊",  700,700,
                    hpMult = 11.0f, atkMult = 4.0f, defMult = 2.5f, gilReward = 4500, isBoss = true),
                FFEnemyTemplate(R.string.enemy_penance,     "⚡",  800,800,
                    hpMult = 20.0f, atkMult = 6.0f, defMult = 5.0f, gilReward = 9000, isBoss = true),
                FFEnemyTemplate(R.string.enemy_yu_yevon,    "🌐",  900,900,
                    hpMult = 12.0f, atkMult = 2.0f, defMult = 4.0f, gilReward = 6000, isBoss = true),
                FFEnemyTemplate(R.string.enemy_sin,         "🌊",  1000,1000,
                    hpMult = 35.0f, atkMult = 8.0f, defMult = 3.5f, gilReward = 30000, isBoss = true)
            )
        )
    )

    fun getDimension(number: Int): FFDimension {
        val base = dimensions.getOrElse((number - 1).coerceAtLeast(0)) { dimensions.last() }
        if (number <= dimensions.size) return base
        // Dynamic dimension above 10: scale biomes and enemies to number * 100 max floor
        val maxFloor = number * 100
        return base.copy(
            number = number,
            biomes = base.biomes.map { it.copy(floorRange = it.floorRange.first..(if (it.floorRange.last >= 100) maxFloor else it.floorRange.last)) },
            enemies = base.enemies.map { enemy ->
                if (enemy.isBoss) {
                    if (enemy.minFloor % 100 == 0) enemy.copy(minFloor = maxFloor, maxFloor = maxFloor)
                    else enemy
                } else {
                    enemy.copy(maxFloor = (maxFloor - 1).coerceAtLeast(enemy.minFloor))
                }
            }
        )
    }

    fun getEnemiesForFloor(dimension: FFDimension, floor: Int): List<FFEnemyTemplate> {
        val matches = dimension.enemies.filter { !it.isBoss && floor in it.minFloor..it.maxFloor }
        if (matches.isNotEmpty()) return matches
        // Fallback: return non-boss enemies sorted by maxFloor descending so we never return empty list
        val nonBosses = dimension.enemies.filter { !it.isBoss }
        if (nonBosses.isNotEmpty()) {
            val highestAvailable = nonBosses.maxByOrNull { it.maxFloor }
            if (highestAvailable != null) return listOf(highestAvailable)
        }
        return listOf(FFEnemyTemplate(R.string.enemy_goblin, "👺", 1, 9999, gilReward = 15))
    }

    fun getBossForFloor(dimension: FFDimension, floor: Int): FFEnemyTemplate? {
        val maxFloor = dimension.number * 100
        val explicitBoss = dimension.bosses().find { floor == it.minFloor }
        if (explicitBoss != null) return explicitBoss
        if (floor == maxFloor || (floor % 100 == 0 && floor <= maxFloor)) {
            return dimension.bosses().maxByOrNull { it.minFloor } ?: explicitBoss
        }
        return null
    }

    private fun FFDimension.bosses() = enemies.filter { it.isBoss }
}
