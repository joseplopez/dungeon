package com.game.dungeon.data.models

typealias JobClass = HeroClass

enum class HeroClass(
    val displayName: String,
    val tier: Int,                    // 1=basic, 2=advanced
    val crystalColor: CrystalColor,   // which crystal unlocks this job
    val hireCost: Int,                // Gil cost to hire one warrior of this class
    val emoji: String,
    val description: String,
    val baseHp: Int, val baseMp: Int,
    val baseAttack: Int, val baseMagic: Int,
    val baseDefense: Int, val baseSpeed: Int,
    val defaultPriority: AIPriority,
    val primaryColor: Long
) {
    // TIER 1 — BASIC JOBS (Classic FF1 Archetypes)
    FREELANCER("Freelancer", 1, CrystalColor.CLEAR, 0, "👤",
        "Jack of all trades. Balanced stats.", 60, 15, 8, 8, 6, 10, AIPriority.ATTACK, 0xFFDDDDDD),
    WARRIOR("Warrior", 1, CrystalColor.RED, 100, "⚔️",
        "Frontline fighter. High HP and attack.", 130, 10, 18, 5, 14, 8, AIPriority.ATTACK, 0xFFFF4444),
    WHITE_MAGE("White Mage", 1, CrystalColor.WHITE, 120, "✨",
        "Holy healer. Keeps the party alive.", 80, 60, 8, 20, 8, 9, AIPriority.HEAL, 0xFFFFFFFF),
    BLACK_MAGE("Black Mage", 1, CrystalColor.BLACK, 130, "🔮",
        "Destruction magic. Hits all enemies.", 65, 70, 6, 28, 5, 11, AIPriority.MAGIC, 0xFF333366),
    THIEF("Thief", 1, CrystalColor.GREEN, 110, "🗡️",
        "Fast and sneaky. Steals Magicite.", 85, 25, 15, 8, 7, 18, AIPriority.ATTACK, 0xFF44CC44),
    MONK("Monk", 1, CrystalColor.ORANGE, 100, "👊",
        "Unarmed brawler. Highest raw attack.", 110, 15, 22, 6, 10, 12, AIPriority.ATTACK, 0xFFFF8800),

    // TIER 2 — ADVANCED JOBS (Classic FF Job System Upgrades)
    KNIGHT("Knight", 2, CrystalColor.BLUE, 250, "🛡️",
        "Protects allies. Can Cover weak party members.", 160, 20, 20, 8, 20, 7, AIPriority.DEFEND, 0xFF4488FF),
    PALADIN("Paladin", 2, CrystalColor.GOLD, 400, "⚜️",
        "Holy warrior. Attacks and heals.", 140, 50, 22, 18, 18, 8, AIPriority.ATTACK, 0xFFFFD700),
    RED_MAGE("Red Mage", 2, CrystalColor.RED, 350, "🎩",
        "Versatile mage. Can fight and cast.", 100, 50, 16, 18, 12, 13, AIPriority.MAGIC, 0xFFFF4444),
    SUMMONER("Summoner", 2, CrystalColor.PURPLE, 500, "🌟",
        "Calls powerful Eidolons. Massive AoE.", 85, 90, 8, 35, 8, 9, AIPriority.MAGIC, 0xFFAA44FF),
    NINJA("Ninja", 2, CrystalColor.DARK_GREEN, 450, "🥷",
        "Dual-wields. Throws weapons at enemies.", 90, 30, 28, 12, 8, 20, AIPriority.ATTACK, 0xFF226622),
    DRAGOON("Dragoon", 2, CrystalColor.CYAN, 400, "🐉",
        "Jumps then lands for massive damage.", 120, 25, 24, 10, 16, 11, AIPriority.ATTACK, 0xFF44CCCC),
    BARD("Bard", 2, CrystalColor.PINK, 300, "🎵",
        "Songs buff entire party each round.", 85, 40, 10, 16, 10, 14, AIPriority.HEAL, 0xFFFF88CC),
    SAMURAI("Samurai", 2, CrystalColor.DARK_RED, 450, "🗾",
        "Powerful single strikes. Gil-throwing attacks.", 115, 30, 26, 14, 14, 10, AIPriority.ATTACK, 0xFF882222)
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
    DARK_RED("Bushido Crystal", 0xFF882222, 850)
}

enum class AIPriority(val displayName: String, val emoji: String) {
    ATTACK("Attack", "⚔️"),
    MAGIC("Magic", "🔮"),
    HEAL("Heal", "✨"),
    DEFEND("Defend", "🛡️")
}
