package com.game.dungeon.data.models

import kotlin.math.ln

data class RelicBonuses(
    val attackBonus: Long,    // Changed to Long
    val hpBonus: Long,        // Changed to Long
    val mpBonus: Long,        // Changed to Long
    val magicBonus: Long,     // Changed to Long
    val defenseBonus: Long,   // Changed to Long
    val goldMultiplier: Float,
    val magiciteChanceBonus: Float,
    // Town upgrade bonuses
    val expMultiplier: Float,
    val itemStatBonus: Float,
    val magicShopLevel: Int,
    // Crit
    val critChanceBonus: Int,
    val critDamageBonus: Long, // Changed to Long
    // Ascended Relics
    val magnetBonus: Float,
    val pocketsBonus: Float,
    val doubleLootChance: Int,
    // Masteries & Pets
    val jobMasteryLevels: Map<HeroClass, Int>,
    val selectedPet: PetType?,
    val bossesDefeatedCount: Int,
    // Advanced Town Upgrades
    val magiciteYieldBonus: Float = 1.0f,
    val alchemistDropChanceBonus: Float = 0.0f,
    val mythicDropBonus: Float = 0.0f,
    val mythicStatBonus: Float = 0.0f,
    val warRoomCritBonus: Int = 0,
    val warRoomAbilityChargeBonus: Float = 0.0f,
    val codexBonuses: CodexBonuses = CodexBonuses()
) {
    fun getMasteryLevel(heroClass: HeroClass): Int {
        return jobMasteryLevels[heroClass] ?: 0
    }

    // Changed return type to Long
    fun getMasteryBonus(heroClass: HeroClass, stat: StatType): Long {
        val level = getMasteryLevel(heroClass)
        return if (heroClass.masteryStatType == stat) (level * heroClass.masteryBonusPerLevel).toLong() else 0L
    }

    val petItemFindBonus: Float get() = if (selectedPet == PetType.CHOCOBO) PetType.CHOCOBO.bonusValue else 0f
    val petGilFindBonus: Float get() = if (selectedPet == PetType.CAT) PetType.CAT.bonusValue else 0f
    val petCritChanceBonus: Int get() = if (selectedPet == PetType.CACTUAR) PetType.CACTUAR.bonusValue.toInt() else 0
    val petCritDamageBonus: Long get() = if (selectedPet == PetType.TONBERRY) PetType.TONBERRY.bonusValue.toLong() else 0L

    val effectiveDoubleLootChance: Float get() {
        val raw = doubleLootChance.toFloat()
        return if (raw > 0f) (100f * raw / (raw + 40f)).coerceAtMost(75f) else 0f
    }

    companion object {
        fun from(gs: GameState, codexBonuses: CodexBonuses = CodexBonuses()): RelicBonuses {
            // Apply Log-Linear Hybrid scaling: logFactor * ln(level + 1) + linearFactor * level
            // This grants fast early growth that flattens out, but maintains a steady infinite tail.

            return RelicBonuses(
                attackBonus = (calculateHybridRelicBonus(gs.attackRelic, logFactor = 400.0, linearFactor = 8.0) * (1f + codexBonuses.attackMultiplierBonus)).toLong(),
                hpBonus = calculateHybridRelicBonus(gs.hpRelic, logFactor = 2500.0, linearFactor = 50.0).toLong(),
                mpBonus = calculateHybridRelicBonus(gs.mpRelic, logFactor = 50.0, linearFactor = 1.0).toLong(),
                magicBonus = calculateHybridRelicBonus(gs.magicRelic, logFactor = 400.0, linearFactor = 8.0).toLong(),
                defenseBonus = calculateHybridRelicBonus(gs.defenseRelic, logFactor = 300.0, linearFactor = 6.0).toLong(),
                goldMultiplier = (1f + gs.goldRelic * 0.05f) + (if (gs.selectedPet == PetType.CAT) PetType.CAT.bonusValue else 0f) + codexBonuses.goldMultiplierBonus,
                magiciteChanceBonus = gs.magiciteRelic * 0.01f + codexBonuses.magiciteChanceBonus,
                expMultiplier = gs.expMultiplier,
                itemStatBonus = gs.itemStatBonus,
                magicShopLevel = gs.magicShopLevel,
                critChanceBonus = gs.critChanceRelic + codexBonuses.critChanceBonus,
                critDamageBonus = calculateHybridRelicBonus(gs.critDamageRelic, logFactor = 25.0, linearFactor = 0.5).toLong(),
                magnetBonus = gs.magnetBonus,
                pocketsBonus = gs.pocketsBonus,
                doubleLootChance = gs.doubleLootChance,
                jobMasteryLevels = gs.jobMasteryLevels,
                selectedPet = gs.selectedPet,
                bossesDefeatedCount = gs.bossesDefeatedNames.size,
                magiciteYieldBonus = gs.magiciteYieldBonus,
                alchemistDropChanceBonus = gs.alchemistDropChanceBonus,
                mythicDropBonus = gs.mythicDropBonus,
                mythicStatBonus = gs.mythicStatBonus,
                warRoomCritBonus = gs.warRoomCritBonus.toInt(),
                warRoomAbilityChargeBonus = gs.warRoomAbilityChargeBonus,
                codexBonuses = codexBonuses
            )
        }
    }
}

/**
 * Calculates a Log-Linear Hybrid stat curve.
 * @param level The current level of the relic.
 * @param logFactor Controls the massive early-game spike.
 * @param linearFactor Controls the infinite endgame scaling (+X per level).
 */
private fun calculateHybridRelicBonus(level: Int, logFactor: Double, linearFactor: Double): Double {
    if (level <= 0) return 0.0
    val lvl = level.toDouble()

    // 1. The Logarithmic curve (Fast early growth, hits a soft cap)
    val logBoost = kotlin.math.ln(lvl + 1.0) * logFactor

    // 2. The Linear tail (Slow, infinite steady growth so upgrades always matter)
    val linearBoost = lvl * linearFactor

    return logBoost + linearBoost
}