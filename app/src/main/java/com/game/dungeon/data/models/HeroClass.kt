package com.game.dungeon.data.models

import androidx.annotation.StringRes
import com.game.dungeon.R

enum class HeroClass(
    @StringRes val nameRes: Int,
    val tier: Int,                    // 1=basic, 2=advanced
    val crystalColor: CrystalColor,   // which crystal unlocks this job
    val hireCost: Int,                // Gil cost to hire one warrior of this class
    val emoji: String,
    @StringRes val descRes: Int,
    val baseHp: Int, val baseMp: Int,
    val baseAttack: Int, val baseMagic: Int,
    val baseDefense: Int, val baseSpeed: Int,
    val defaultPriority: AIPriority,
    val primaryColor: Long,
    val masteryStatType: StatType,
    val masteryBonusPerLevel: Int
) {
    // TIER 1 — BASIC JOBS (Classic FF1 Archetypes)
    FREELANCER(R.string.class_freelancer, 1, CrystalColor.CLEAR, 0, "👤",
        R.string.class_freelancer_desc, 60, 15, 8, 8, 6, 10, AIPriority.ATTACK, 0xFFDDDDDD,
        StatType.HP, 10),
    WARRIOR(R.string.class_warrior, 1, CrystalColor.RED, 100, "⚔️",
        R.string.class_warrior_desc, 130, 10, 18, 5, 14, 8, AIPriority.ATTACK, 0xFFFF4444,
        StatType.ATTACK, 2),
    WHITE_MAGE(R.string.class_white_mage, 1, CrystalColor.WHITE, 120, "✨",
        R.string.class_white_mage_desc, 80, 60, 8, 20, 8, 9, AIPriority.HEAL, 0xFFFFFFFF,
        StatType.MAGIC, 2),
    BLACK_MAGE(R.string.class_black_mage, 1, CrystalColor.BLACK, 130, "🔮",
        R.string.class_black_mage_desc, 65, 70, 6, 28, 5, 11, AIPriority.MAGIC, 0xFF333366,
        StatType.MAGIC, 2),
    THIEF(R.string.class_thief, 1, CrystalColor.GREEN, 110, "🗡️",
        R.string.class_thief_desc, 85, 25, 15, 8, 7, 18, AIPriority.ATTACK, 0xFF44CC44,
        StatType.CRIT_CHANCE, 1),
    MONK(R.string.class_monk, 1, CrystalColor.ORANGE, 100, "👊",
        R.string.class_monk_desc, 110, 15, 22, 6, 10, 12, AIPriority.ATTACK, 0xFFFF8800,
        StatType.ATTACK, 2),

    // TIER 2 — ADVANCED JOBS (Classic FF Job System Upgrades)
    KNIGHT(R.string.class_knight, 2, CrystalColor.BLUE, 250, "🛡️",
        R.string.class_knight_desc, 160, 20, 20, 8, 20, 7, AIPriority.DEFEND, 0xFF4488FF,
        StatType.DEFENSE, 2),
    PALADIN(R.string.class_paladin, 2, CrystalColor.GOLD, 400, "⚜️",
        R.string.class_paladin_desc, 140, 50, 22, 18, 18, 8, AIPriority.ATTACK, 0xFFFFD700,
        StatType.HP, 20),
    RED_MAGE(R.string.class_red_mage, 2, CrystalColor.RED, 350, "🎩",
        R.string.class_red_mage_desc, 100, 50, 16, 18, 12, 13, AIPriority.MAGIC, 0xFFFF4444,
        StatType.MP, 10),
    SUMMONER(R.string.class_summoner, 2, CrystalColor.PURPLE, 500, "🌟",
        R.string.class_summoner_desc, 85, 90, 8, 35, 8, 9, AIPriority.MAGIC, 0xFFAA44FF,
        StatType.MAGIC, 4),
    NINJA(R.string.class_ninja, 2, CrystalColor.DARK_GREEN, 450, "🥷",
        R.string.class_ninja_desc, 90, 30, 28, 12, 8, 20, AIPriority.ATTACK, 0xFF226622,
        StatType.CRIT_CHANCE, 2),
    DRAGOON(R.string.class_dragoon, 2, CrystalColor.CYAN, 400, "🐉",
        R.string.class_dragoon_desc, 120, 25, 24, 10, 16, 11, AIPriority.ATTACK, 0xFF44CCCC,
        StatType.ATTACK, 4),
    BARD(R.string.class_bard, 2, CrystalColor.PINK, 300, "🎵",
        R.string.class_bard_desc, 85, 40, 10, 16, 10, 14, AIPriority.HEAL, 0xFFFF88CC,
        StatType.MP, 15),
    SAMURAI(R.string.class_samurai, 2, CrystalColor.DARK_RED, 450, "🗾",
        R.string.class_samurai_desc, 115, 30, 26, 14, 14, 10, AIPriority.ATTACK, 0xFF882222,
        StatType.CRIT_DAMAGE, 10),

    // TIER 3 — HIDDEN JOBS (Unlockable Secrets)
    ONION_KNIGHT(R.string.class_onion_knight, 3, CrystalColor.HIDDEN_ONION, 1000, "🧅",
        R.string.class_onion_knight_desc, 30, 10, 5, 5, 5, 8, AIPriority.ATTACK, 0xFFCCFFCC,
        StatType.HP, 50),
    MIME(R.string.class_mime, 3, CrystalColor.HIDDEN_MIME, 1200, "🤡",
        R.string.class_mime_desc, 90, 40, 12, 12, 12, 15, AIPriority.ATTACK, 0xFFFFFF99,
        StatType.SPEED, 2),
    NECROMANCER(R.string.class_necromancer, 3, CrystalColor.HIDDEN_NECRO, 1500, "💀",
        R.string.class_necromancer_desc, 110, 80, 14, 30, 15, 9, AIPriority.MAGIC, 0xFF330033,
        StatType.MAGIC, 5),
    BLUE_MAGE(R.string.class_blue_mage, 3, CrystalColor.HIDDEN_BLUE, 1300, "📘",
        R.string.class_blue_mage_desc, 100, 60, 18, 22, 14, 12, AIPriority.MAGIC, 0xFF3333FF,
        StatType.MP, 20)
}

enum class StatType(@StringRes val nameRes: Int) {
    HP(R.string.stat_hp),
    MP(R.string.stat_mp),
    ATTACK(R.string.stat_attack),
    MAGIC(R.string.stat_magic),
    DEFENSE(R.string.stat_defense),
    SPEED(R.string.stat_speed),
    CRIT_CHANCE(R.string.stat_crit_chance),
    CRIT_DAMAGE(R.string.stat_crit_damage)
}


enum class CrystalColor(val displayName: String, val colorHex: Long, val baseCost: Int) {
    CLEAR("Clear Crystal", 0xFFDDDDDD, 0),      // always available (Freelancer)
    RED("Fire Crystal", 0xFFFF4444, 200),
    WHITE("Light Crystal", 0xFFFFFFFF, 250),
    BLACK("Dark Crystal", 0xFF333366, 275),
    GREEN("Wind Crystal", 0xFF44CC44, 225),
    ORANGE("Earth Crystal", 0xFFFF8800, 200),
    BLUE("Ice Crystal", 0xFF4488FF, 600),
    GOLD("Holy Crystal", 0xFFFFD700, 900),
    PURPLE("Void Crystal", 0xFFAA44FF, 1100),
    CYAN("Storm Crystal", 0xFF44CCCC, 800),
    PINK("Life Crystal", 0xFFFF88CC, 700),
    DARK_GREEN("Shadow Crystal", 0xFF226622, 850),
    DARK_RED("Bushido Crystal", 0xFF882222, 850),
    HIDDEN_ONION("Onion Crystal", 0xFFCCFFCC, 2000),
    HIDDEN_MIME("Mime Crystal", 0xFFFFFF99, 2500),
    HIDDEN_NECRO("Death Crystal", 0xFF330033, 3000),
    HIDDEN_BLUE("Lore Crystal", 0xFF3333FF, 2500),
    HIDDEN("Hidden Path", 0xFF000000, 0)
}

enum class AIPriority(val displayName: String, val emoji: String) {
    ATTACK("Attack", "⚔️"),
    MAGIC("Magic", "🔮"),
    HEAL("Heal", "✨"),
    DEFEND("Defend", "🛡️")
}
