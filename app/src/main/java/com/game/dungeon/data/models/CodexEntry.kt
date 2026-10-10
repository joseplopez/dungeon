package com.game.dungeon.data.models

enum class KillThreshold(val requiredKills: Int) {
    UNDISCOVERED(0),
    SIGHTED(1),
    ANALYZED(10),
    MASTERED(50);

    companion object {
        fun fromKillCount(count: Int): KillThreshold = when {
            count >= 50 -> MASTERED
            count >= 10 -> ANALYZED
            count >= 1 -> SIGHTED
            else -> UNDISCOVERED
        }
    }
}

data class MonsterCodexDetail(
    val monsterType: MonsterType,
    val killCount: Int,
    val threshold: KillThreshold,
    val isDiscovered: Boolean,
    val drops: List<MaterialDropRule> = emptyList()
)

data class MaterialCodexDetail(
    val material: Material,
    val isDiscovered: Boolean,
    val primaryDropSources: List<MonsterType> = emptyList()
)

enum class CodexMilestone(
    val requiredPercentage: Float,
    val goldMultiplierBonus: Float = 0f,
    val critChanceBonus: Int = 0,
    val magiciteChanceBonus: Float = 0f,
    val attackMultiplierBonus: Float = 0f
) {
    TIER_25(25f, goldMultiplierBonus = 0.05f),
    TIER_50(50f, goldMultiplierBonus = 0.05f, critChanceBonus = 5),
    TIER_75(75f, goldMultiplierBonus = 0.05f, critChanceBonus = 5, attackMultiplierBonus = 0.05f),
    TIER_100(100f, goldMultiplierBonus = 0.05f, critChanceBonus = 5, attackMultiplierBonus = 0.05f, magiciteChanceBonus = 0.10f);

    companion object {
        fun getActiveMilestones(completionPercentage: Float): List<CodexMilestone> {
            return entries.filter { completionPercentage >= it.requiredPercentage }
        }

        fun calculateBonuses(completionPercentage: Float): CodexBonuses {
            var gold = 0f
            var crit = 0
            var magicite = 0f
            var atk = 0f

            if (completionPercentage >= 25f) gold += 0.05f
            if (completionPercentage >= 50f) crit += 5
            if (completionPercentage >= 75f) atk += 0.05f
            if (completionPercentage >= 100f) magicite += 0.10f

            return CodexBonuses(
                goldMultiplierBonus = gold,
                critChanceBonus = crit,
                magiciteChanceBonus = magicite,
                attackMultiplierBonus = atk
            )
        }
    }
}

data class CodexBonuses(
    val goldMultiplierBonus: Float = 0f,
    val critChanceBonus: Int = 0,
    val magiciteChanceBonus: Float = 0f,
    val attackMultiplierBonus: Float = 0f
)
