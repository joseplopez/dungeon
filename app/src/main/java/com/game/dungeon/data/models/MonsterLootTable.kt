package com.game.dungeon.data.models

data class MaterialDropRule(
    val materialId: String,
    val baseDropChance: Float,
    val minQuantity: Int = 1,
    val maxQuantity: Int = 1,
    val isBossTrophy: Boolean = false,
)

data class MonsterLootEntry(
    val primaryDrop: MaterialDropRule,
    val secondaryDrop: MaterialDropRule? = null,
    val bossTrophyDrop: MaterialDropRule? = null,
)

object MonsterLootTable {
    private val lootMap: Map<MonsterType, MonsterLootEntry> = mapOf(
        // ── Regular Monsters ──
        MonsterType.GOBLIN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("iron_ore", 0.20f),
            secondaryDrop = MaterialDropRule("monster_bone", 0.10f)
        ),
        MonsterType.WOLF to MonsterLootEntry(
            primaryDrop = MaterialDropRule("beast_fang", 0.20f),
            secondaryDrop = MaterialDropRule("monster_bone", 0.10f)
        ),
        MonsterType.PIRATE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("iron_ore", 0.20f),
            secondaryDrop = MaterialDropRule("beast_fang", 0.10f)
        ),
        MonsterType.OGRE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("monster_bone", 0.20f),
            secondaryDrop = MaterialDropRule("iron_ore", 0.10f)
        ),
        MonsterType.EVIL_EYE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.15f),
            secondaryDrop = MaterialDropRule("lightning_essence", 0.08f)
        ),
        MonsterType.RED_FLAN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("fire_essence", 0.18f),
            secondaryDrop = MaterialDropRule("ruby_gem_1", 0.05f)
        ),
        MonsterType.BOMB to MonsterLootEntry(
            primaryDrop = MaterialDropRule("fire_essence", 0.20f),
            secondaryDrop = MaterialDropRule("ruby_gem_1", 0.05f)
        ),
        MonsterType.SAHAGIN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("ice_essence", 0.18f),
            secondaryDrop = MaterialDropRule("beast_fang", 0.10f)
        ),
        MonsterType.COCKATRICE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("beast_fang", 0.20f),
            secondaryDrop = MaterialDropRule("topaz_gem_1", 0.05f)
        ),
        MonsterType.ZOMBIE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("monster_bone", 0.20f),
            secondaryDrop = MaterialDropRule("dark_essence", 0.08f)
        ),
        MonsterType.WYVERN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dragon_scale", 0.08f),
            secondaryDrop = MaterialDropRule("demon_horn", 0.08f)
        ),
        MonsterType.WILD_RAT to MonsterLootEntry(
            primaryDrop = MaterialDropRule("monster_bone", 0.20f),
            secondaryDrop = MaterialDropRule("beast_fang", 0.10f)
        ),
        MonsterType.BLACK_KNIGHT to MonsterLootEntry(
            primaryDrop = MaterialDropRule("mithril_ore", 0.12f),
            secondaryDrop = MaterialDropRule("dark_essence", 0.08f)
        ),
        MonsterType.SERGEANT to MonsterLootEntry(
            primaryDrop = MaterialDropRule("iron_ore", 0.20f),
            secondaryDrop = MaterialDropRule("mithril_ore", 0.08f)
        ),
        MonsterType.LAMIA to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.15f),
            secondaryDrop = MaterialDropRule("sapphire_gem_1", 0.05f)
        ),
        MonsterType.SEA_SNAKE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("ice_essence", 0.18f),
            secondaryDrop = MaterialDropRule("beast_fang", 0.10f)
        ),
        MonsterType.ADAMANTOISE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("adamantite_ore", 0.08f),
            secondaryDrop = MaterialDropRule("emerald_gem_1", 0.05f)
        ),
        MonsterType.DARK_KNIGHT to MonsterLootEntry(
            primaryDrop = MaterialDropRule("mithril_ore", 0.12f),
            secondaryDrop = MaterialDropRule("dark_essence", 0.08f)
        ),
        MonsterType.BEHEMOTH to MonsterLootEntry(
            primaryDrop = MaterialDropRule("demon_horn", 0.10f),
            secondaryDrop = MaterialDropRule("beast_fang", 0.12f)
        ),
        MonsterType.TOAD to MonsterLootEntry(
            primaryDrop = MaterialDropRule("monster_bone", 0.20f),
            secondaryDrop = MaterialDropRule("emerald_gem_1", 0.05f)
        ),
        MonsterType.DJINN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("fire_essence", 0.18f),
            secondaryDrop = MaterialDropRule("ruby_gem_1", 0.06f)
        ),
        MonsterType.MEDUSA to MonsterLootEntry(
            primaryDrop = MaterialDropRule("demon_horn", 0.08f),
            secondaryDrop = MaterialDropRule("emerald_gem_1", 0.05f)
        ),
        MonsterType.TONBERRY to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.15f),
            secondaryDrop = MaterialDropRule("topaz_gem_1", 0.06f)
        ),
        MonsterType.GIANT to MonsterLootEntry(
            primaryDrop = MaterialDropRule("monster_bone", 0.20f),
            secondaryDrop = MaterialDropRule("mithril_ore", 0.08f)
        ),
        MonsterType.IRON_GIANT to MonsterLootEntry(
            primaryDrop = MaterialDropRule("adamantite_ore", 0.10f),
            secondaryDrop = MaterialDropRule("orichalcum_ore", 0.04f)
        ),
        MonsterType.MALBORO to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.15f),
            secondaryDrop = MaterialDropRule("emerald_gem_2", 0.04f)
        ),
        MonsterType.CAPTAIN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("mithril_ore", 0.15f),
            secondaryDrop = MaterialDropRule("iron_ore", 0.12f)
        ),
        MonsterType.ANTLION to MonsterLootEntry(
            primaryDrop = MaterialDropRule("beast_fang", 0.20f),
            secondaryDrop = MaterialDropRule("topaz_gem_1", 0.05f)
        ),
        MonsterType.MIST_DRAGON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dragon_scale", 0.08f),
            secondaryDrop = MaterialDropRule("ice_essence", 0.12f)
        ),
        MonsterType.DARK_IMP to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            secondaryDrop = MaterialDropRule("sapphire_gem_1", 0.05f)
        ),
        MonsterType.LUNAR_DRAGON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dragon_scale", 0.10f),
            secondaryDrop = MaterialDropRule("sapphire_gem_2", 0.04f)
        ),
        MonsterType.MINDFLAYER to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.15f),
            secondaryDrop = MaterialDropRule("sapphire_gem_1", 0.06f)
        ),
        MonsterType.FARIIS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("mithril_ore", 0.12f),
            secondaryDrop = MaterialDropRule("ruby_gem_1", 0.05f)
        ),
        MonsterType.SKULL_EATER to MonsterLootEntry(
            primaryDrop = MaterialDropRule("monster_bone", 0.20f),
            secondaryDrop = MaterialDropRule("dark_essence", 0.08f)
        ),
        MonsterType.GARGOYLE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("demon_horn", 0.10f),
            secondaryDrop = MaterialDropRule("dark_essence", 0.08f)
        ),
        MonsterType.STOKER to MonsterLootEntry(
            primaryDrop = MaterialDropRule("fire_essence", 0.20f),
            secondaryDrop = MaterialDropRule("ruby_gem_1", 0.05f)
        ),
        MonsterType.OMEGA_PROTO to MonsterLootEntry(
            primaryDrop = MaterialDropRule("orichalcum_ore", 0.08f),
            secondaryDrop = MaterialDropRule("topaz_gem_2", 0.04f)
        ),
        MonsterType.SHINRYU to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dragon_scale", 0.12f),
            secondaryDrop = MaterialDropRule("orichalcum_ore", 0.05f)
        ),
        MonsterType.MP to MonsterLootEntry(
            primaryDrop = MaterialDropRule("iron_ore", 0.20f),
            secondaryDrop = MaterialDropRule("beast_fang", 0.10f)
        ),
        MonsterType.BRAWLER to MonsterLootEntry(
            primaryDrop = MaterialDropRule("beast_fang", 0.20f),
            secondaryDrop = MaterialDropRule("topaz_gem_1", 0.05f)
        ),
        MonsterType.MAGITEK_ARMOR to MonsterLootEntry(
            primaryDrop = MaterialDropRule("adamantite_ore", 0.08f),
            secondaryDrop = MaterialDropRule("fire_essence", 0.10f)
        ),
        MonsterType.NINJA to MonsterLootEntry(
            primaryDrop = MaterialDropRule("lightning_essence", 0.15f),
            secondaryDrop = MaterialDropRule("sapphire_gem_1", 0.05f)
        ),
        MonsterType.DRAGON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dragon_scale", 0.08f),
            secondaryDrop = MaterialDropRule("fire_essence", 0.12f)
        ),
        MonsterType.DARK_FORCE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.15f),
            secondaryDrop = MaterialDropRule("demon_horn", 0.08f)
        ),
        MonsterType.MAGIC_MASTER to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.15f),
            secondaryDrop = MaterialDropRule("sapphire_gem_2", 0.04f)
        ),
        MonsterType.GRUNT to MonsterLootEntry(
            primaryDrop = MaterialDropRule("iron_ore", 0.20f),
            secondaryDrop = MaterialDropRule("monster_bone", 0.10f)
        ),
        MonsterType.SWEEPER to MonsterLootEntry(
            primaryDrop = MaterialDropRule("iron_ore", 0.20f),
            secondaryDrop = MaterialDropRule("lightning_essence", 0.08f)
        ),
        MonsterType.HELLHOUND to MonsterLootEntry(
            primaryDrop = MaterialDropRule("fire_essence", 0.15f),
            secondaryDrop = MaterialDropRule("beast_fang", 0.10f)
        ),
        MonsterType.JENOVA_CELL to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.18f),
            secondaryDrop = MaterialDropRule("sapphire_gem_2", 0.04f)
        ),
        MonsterType.DRAGON_RIDER to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dragon_scale", 0.08f),
            secondaryDrop = MaterialDropRule("demon_horn", 0.08f)
        ),
        MonsterType.WEAPON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("orichalcum_ore", 0.08f),
            secondaryDrop = MaterialDropRule("dark_essence", 0.10f)
        ),
        MonsterType.GALBADIAN_SOLDIER to MonsterLootEntry(
            primaryDrop = MaterialDropRule("iron_ore", 0.20f),
            secondaryDrop = MaterialDropRule("mithril_ore", 0.08f)
        ),
        MonsterType.BITE_BUG to MonsterLootEntry(
            primaryDrop = MaterialDropRule("beast_fang", 0.20f),
            secondaryDrop = MaterialDropRule("monster_bone", 0.10f)
        ),
        MonsterType.GEEZARD to MonsterLootEntry(
            primaryDrop = MaterialDropRule("monster_bone", 0.20f),
            secondaryDrop = MaterialDropRule("beast_fang", 0.10f)
        ),
        MonsterType.WENDIGO to MonsterLootEntry(
            primaryDrop = MaterialDropRule("ice_essence", 0.15f),
            secondaryDrop = MaterialDropRule("demon_horn", 0.08f)
        ),
        MonsterType.ELNOYLE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.15f),
            secondaryDrop = MaterialDropRule("orichalcum_ore", 0.04f)
        ),
        MonsterType.RUBY_DRAGON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dragon_scale", 0.08f),
            secondaryDrop = MaterialDropRule("fire_essence", 0.15f)
        ),
        MonsterType.BLACK_MAGE_UNIT to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.15f),
            secondaryDrop = MaterialDropRule("lightning_essence", 0.10f)
        ),
        MonsterType.ZAGHNOL to MonsterLootEntry(
            primaryDrop = MaterialDropRule("demon_horn", 0.10f),
            secondaryDrop = MaterialDropRule("lightning_essence", 0.10f)
        ),
        MonsterType.MISTODON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("monster_bone", 0.20f),
            secondaryDrop = MaterialDropRule("dark_essence", 0.08f)
        ),
        MonsterType.SILVER_DRAGON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dragon_scale", 0.08f),
            secondaryDrop = MaterialDropRule("ice_essence", 0.12f)
        ),
        MonsterType.DINGO to MonsterLootEntry(
            primaryDrop = MaterialDropRule("beast_fang", 0.20f),
            secondaryDrop = MaterialDropRule("monster_bone", 0.10f)
        ),
        MonsterType.WATER_FLAN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("ice_essence", 0.20f),
            secondaryDrop = MaterialDropRule("sapphire_gem_1", 0.05f)
        ),
        MonsterType.KLIKK to MonsterLootEntry(
            primaryDrop = MaterialDropRule("beast_fang", 0.20f),
            secondaryDrop = MaterialDropRule("monster_bone", 0.10f)
        ),
        MonsterType.GUADO_GUARDIAN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.15f),
            secondaryDrop = MaterialDropRule("mithril_ore", 0.08f)
        ),
        MonsterType.GREAT_MALBORO to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.18f),
            secondaryDrop = MaterialDropRule("emerald_gem_2", 0.04f)
        ),
        MonsterType.DARK_AEON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            secondaryDrop = MaterialDropRule("topaz_gem_2", 0.05f)
        ),
        MonsterType.SLIME to MonsterLootEntry(
            primaryDrop = MaterialDropRule("fire_essence", 0.15f),
            secondaryDrop = MaterialDropRule("ice_essence", 0.10f)
        ),
        MonsterType.ORC to MonsterLootEntry(
            primaryDrop = MaterialDropRule("monster_bone", 0.20f),
            secondaryDrop = MaterialDropRule("iron_ore", 0.10f)
        ),
        MonsterType.RAT to MonsterLootEntry(
            primaryDrop = MaterialDropRule("monster_bone", 0.20f),
            secondaryDrop = MaterialDropRule("beast_fang", 0.10f)
        ),
        MonsterType.MERMAN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("ice_essence", 0.15f),
            secondaryDrop = MaterialDropRule("beast_fang", 0.10f)
        ),
        MonsterType.STOKER_MONSTER to MonsterLootEntry(
            primaryDrop = MaterialDropRule("fire_essence", 0.20f),
            secondaryDrop = MaterialDropRule("ruby_gem_1", 0.05f)
        ),

        // ── Bosses — Tier 1 (20% – 30% Trophy Chance) ──
        MonsterType.GARLAND to MonsterLootEntry(
            primaryDrop = MaterialDropRule("mithril_ore", 0.15f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.20f, isBossTrophy = true)
        ),
        MonsterType.ASTOS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.15f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.25f, isBossTrophy = true)
        ),
        MonsterType.LICH to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.30f, isBossTrophy = true)
        ),
        MonsterType.LEON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("mithril_ore", 0.15f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.22f, isBossTrophy = true)
        ),
        MonsterType.BORGHEN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("iron_ore", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.20f, isBossTrophy = true)
        ),
        MonsterType.GOTTOS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("beast_fang", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.25f, isBossTrophy = true)
        ),
        MonsterType.DJINN_BOSS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("fire_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.28f, isBossTrophy = true)
        ),
        MonsterType.NEPTO_DRAGON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dragon_scale", 0.12f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.30f, isBossTrophy = true)
        ),
        MonsterType.MIST_DRAGON_BOSS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dragon_scale", 0.12f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.25f, isBossTrophy = true)
        ),
        MonsterType.ANTLION_BOSS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("beast_fang", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.22f, isBossTrophy = true)
        ),
        MonsterType.SCARMIGLIONE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.28f, isBossTrophy = true)
        ),
        MonsterType.CAGNAZZO to MonsterLootEntry(
            primaryDrop = MaterialDropRule("ice_essence", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.28f, isBossTrophy = true)
        ),
        MonsterType.WING_RAPTOR to MonsterLootEntry(
            primaryDrop = MaterialDropRule("beast_fang", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.20f, isBossTrophy = true)
        ),
        MonsterType.KARLABOS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("ice_essence", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.24f, isBossTrophy = true)
        ),
        MonsterType.IFRIT_BOSS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("fire_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.30f, isBossTrophy = true)
        ),
        MonsterType.WHELK to MonsterLootEntry(
            primaryDrop = MaterialDropRule("lightning_essence", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.20f, isBossTrophy = true)
        ),
        MonsterType.VARGAS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("beast_fang", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.22f, isBossTrophy = true)
        ),
        MonsterType.NUMBER_024 to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.26f, isBossTrophy = true)
        ),
        MonsterType.ULTROS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("ice_essence", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.25f, isBossTrophy = true)
        ),
        MonsterType.GUARD_SCORPION to MonsterLootEntry(
            primaryDrop = MaterialDropRule("lightning_essence", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.20f, isBossTrophy = true)
        ),
        MonsterType.AIRBUSTER to MonsterLootEntry(
            primaryDrop = MaterialDropRule("iron_ore", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.24f, isBossTrophy = true)
        ),
        MonsterType.PLANT_BRAIN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("monster_bone", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.20f, isBossTrophy = true)
        ),
        MonsterType.BLACK_WALTZ to MonsterLootEntry(
            primaryDrop = MaterialDropRule("lightning_essence", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.28f, isBossTrophy = true)
        ),
        MonsterType.ZORN_THORN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.25f, isBossTrophy = true)
        ),
        MonsterType.RALVURAHVA to MonsterLootEntry(
            primaryDrop = MaterialDropRule("ice_essence", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.22f, isBossTrophy = true)
        ),
        MonsterType.KLIKK_BOSS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("beast_fang", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.20f, isBossTrophy = true)
        ),
        MonsterType.OBLITZERATOR to MonsterLootEntry(
            primaryDrop = MaterialDropRule("fire_essence", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.26f, isBossTrophy = true)
        ),

        // ── Bosses — Tier 2 (35% – 42% Trophy Chance) ──
        MonsterType.MARILITH to MonsterLootEntry(
            primaryDrop = MaterialDropRule("fire_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.38f, isBossTrophy = true)
        ),
        MonsterType.KRAKEN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("ice_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.38f, isBossTrophy = true)
        ),
        MonsterType.TIAMAT to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dragon_scale", 0.15f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.40f, isBossTrophy = true)
        ),
        MonsterType.ROUNDWORM to MonsterLootEntry(
            primaryDrop = MaterialDropRule("adamantite_ore", 0.12f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.35f, isBossTrophy = true)
        ),
        MonsterType.CYCLONE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("lightning_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.36f, isBossTrophy = true)
        ),
        MonsterType.HEIN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.38f, isBossTrophy = true)
        ),
        MonsterType.GARUDA to MonsterLootEntry(
            primaryDrop = MaterialDropRule("lightning_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.38f, isBossTrophy = true)
        ),
        MonsterType.GOLDOR to MonsterLootEntry(
            primaryDrop = MaterialDropRule("mithril_ore", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.35f, isBossTrophy = true)
        ),
        MonsterType.BARBARICCIA to MonsterLootEntry(
            primaryDrop = MaterialDropRule("lightning_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.40f, isBossTrophy = true)
        ),
        MonsterType.RUBICANTE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("fire_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.42f, isBossTrophy = true)
        ),
        MonsterType.GILGAMESH to MonsterLootEntry(
            primaryDrop = MaterialDropRule("adamantite_ore", 0.15f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.40f, isBossTrophy = true)
        ),
        MonsterType.ATOMOS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.38f, isBossTrophy = true)
        ),
        MonsterType.TYPHON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("fire_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.35f, isBossTrophy = true)
        ),
        MonsterType.AIR_FORCE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("adamantite_ore", 0.15f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.36f, isBossTrophy = true)
        ),
        MonsterType.GUARDIAN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("adamantite_ore", 0.15f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.38f, isBossTrophy = true)
        ),
        MonsterType.JENOVA_BIRTH to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.40f, isBossTrophy = true)
        ),
        MonsterType.RUFUS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("adamantite_ore", 0.15f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.35f, isBossTrophy = true)
        ),
        MonsterType.JENOVA_LIFE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.40f, isBossTrophy = true)
        ),
        MonsterType.HOJO to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.38f, isBossTrophy = true)
        ),
        MonsterType.NORG to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.35f, isBossTrophy = true)
        ),
        MonsterType.EDEA to MonsterLootEntry(
            primaryDrop = MaterialDropRule("ice_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.40f, isBossTrophy = true)
        ),
        MonsterType.FUJIN_RAIJIN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("lightning_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.38f, isBossTrophy = true)
        ),
        MonsterType.SEIFER to MonsterLootEntry(
            primaryDrop = MaterialDropRule("adamantite_ore", 0.15f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.38f, isBossTrophy = true)
        ),
        MonsterType.MALIRIS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("fire_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.38f, isBossTrophy = true)
        ),
        MonsterType.EVRAE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dragon_scale", 0.15f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.40f, isBossTrophy = true)
        ),
        MonsterType.SEYMOUR to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_2", 0.40f, isBossTrophy = true)
        ),

        // ── Bosses — Tier 3 (45% – 50% Maximum Trophy Chance) ──
        MonsterType.CHAOS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("orichalcum_ore", 0.15f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.48f, isBossTrophy = true)
        ),
        MonsterType.EMPEROR to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.48f, isBossTrophy = true)
        ),
        MonsterType.XANDE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("orichalcum_ore", 0.15f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.45f, isBossTrophy = true)
        ),
        MonsterType.CLOUD_OF_DARKNESS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.GOLBEZ to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.48f, isBossTrophy = true)
        ),
        MonsterType.DARK_BAHAMUT to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dragon_scale", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.ZEROMUS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.GILGAMESH_2 to MonsterLootEntry(
            primaryDrop = MaterialDropRule("orichalcum_ore", 0.15f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.45f, isBossTrophy = true)
        ),
        MonsterType.EXDEATH to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.48f, isBossTrophy = true)
        ),
        MonsterType.OMEGA to MonsterLootEntry(
            primaryDrop = MaterialDropRule("orichalcum_ore", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.NEO_EXDEATH to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.ULTIMA_WEAPON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("orichalcum_ore", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.KEFKA to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.JENOVA_SYNTHESIS to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.48f, isBossTrophy = true)
        ),
        MonsterType.BIZARRO_SEPH to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.48f, isBossTrophy = true)
        ),
        MonsterType.SEPHIROT to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.ADEL to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.45f, isBossTrophy = true)
        ),
        MonsterType.TRAUMA to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.45f, isBossTrophy = true)
        ),
        MonsterType.OMEGA_WEAPON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("orichalcum_ore", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.ULTIMECIA to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.KUJA to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.48f, isBossTrophy = true)
        ),
        MonsterType.TRANCE_KUJA to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.NECRON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.OMEGA_WEAPON_X to MonsterLootEntry(
            primaryDrop = MaterialDropRule("orichalcum_ore", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.JECHT to MonsterLootEntry(
            primaryDrop = MaterialDropRule("demon_horn", 0.18f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.48f, isBossTrophy = true)
        ),
        MonsterType.PENANCE to MonsterLootEntry(
            primaryDrop = MaterialDropRule("orichalcum_ore", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.YU_YEVON to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        ),
        MonsterType.SIN to MonsterLootEntry(
            primaryDrop = MaterialDropRule("dark_essence", 0.20f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_3", 0.50f, isBossTrophy = true)
        )
    )

    fun getDropsForMonster(monsterType: MonsterType, isBoss: Boolean): List<MaterialDropRule> {
        val entry = lootMap[monsterType] ?: MonsterLootEntry(
            primaryDrop = MaterialDropRule("iron_ore", 0.20f),
            secondaryDrop = MaterialDropRule("monster_bone", 0.10f),
            bossTrophyDrop = MaterialDropRule("boss_trophy_1", 0.25f, isBossTrophy = true)
        )
        val drops = mutableListOf<MaterialDropRule>()
        drops.add(entry.primaryDrop)
        entry.secondaryDrop?.let { drops.add(it) }
        if (isBoss) {
            val trophyRule = entry.bossTrophyDrop ?: MaterialDropRule("boss_trophy_1", 0.25f, isBossTrophy = true)
            drops.add(trophyRule)
        }
        return drops
    }
}