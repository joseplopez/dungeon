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

    fun getMasteryBonus(heroClass: HeroClass, stat: StatType): Long {
        val level = getMasteryLevel(heroClass)
        if (heroClass.masteryStatType != stat) return 0L

        return calculateMasteryBonus(level, heroClass.masteryBonusPerLevel)
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
        /**
         * Global Relic Stat Calculator
         */
        fun calculateRelicStat(level: Int, linearFactor: Double, quadraticFactor: Double): Double {
            if (level <= 0) return 0.0
            val lvl = level.toDouble()
            return (lvl * linearFactor) + (lvl * lvl * quadraticFactor)
        }

        /**
         * Global Mastery Stat Calculator
         */
        /**
         * Global Mastery Stat Calculator
         * Uses a linear curve for levels 1-50.
         * Past level 50, triggers a massive quadratic curve scaled by the job's base multiplier.
         */
        /**
         * Global Mastery Stat Calculator
         * Early game (1-50): Enhanced linear curve (2.5x base).
         * Endgame (50+): Aggressive quadratic curve scaled by 3.75x to hit 1M+ HP at lvl 166.
         */
        fun calculateMasteryBonus(level: Int, basePerLevel: Int): Long {
            if (level <= 0) return 0L

            val lvl = level.toDouble()
            val baseMult = basePerLevel.toDouble()

            // Early/Mid game rhythm remains untouched
            val linearBoost = lvl * baseMult * 2.5

            // Endgame quadratic explosion scaled to reach 1,000,000 HP
            val quadraticBoost = if (lvl > 50) {
                (lvl - 50.0) * (lvl - 50.0) * baseMult * 3.75
            } else {
                0.0
            }

            return (linearBoost + quadraticBoost).toLong()
        }
        fun from(gs: GameState, codexBonuses: CodexBonuses = CodexBonuses()): RelicBonuses {
            return RelicBonuses(
                // Target ~2,500 ATK/MAG at Level 100
                attackBonus = (calculateRelicStat(gs.attackRelic, linearFactor = 5.0, quadraticFactor = 0.2) * (1f + codexBonuses.attackMultiplierBonus)).toLong(),
                magicBonus = calculateRelicStat(gs.magicRelic, linearFactor = 5.0, quadraticFactor = 0.2).toLong(),

                // Target ~16,500 HP at Level 100
                hpBonus = calculateRelicStat(gs.hpRelic, linearFactor = 25.0, quadraticFactor = 1.4).toLong(),

                // Target ~330 MP at Level 100
                mpBonus = calculateRelicStat(gs.mpRelic, linearFactor = 1.0, quadraticFactor = 0.023).toLong(),

                // Target ~2,000 DEF at Level 100
                defenseBonus = calculateRelicStat(gs.defenseRelic, linearFactor = 4.0, quadraticFactor = 0.16).toLong(),

                goldMultiplier = (1f + gs.goldRelic * 0.05f) + (if (gs.selectedPet == PetType.CAT) PetType.CAT.bonusValue else 0f) + codexBonuses.goldMultiplierBonus,
                magiciteChanceBonus = gs.magiciteRelic * 0.01f + codexBonuses.magiciteChanceBonus,
                expMultiplier = gs.expMultiplier,
                itemStatBonus = gs.itemStatBonus,
                magicShopLevel = gs.magicShopLevel,
                critChanceBonus = gs.critChanceRelic + codexBonuses.critChanceBonus,

                // Crit Damage kept mostly linear to prevent multiplier explosion
                critDamageBonus = calculateRelicStat(gs.critDamageRelic, linearFactor = 0.5, quadraticFactor = 0.005).toLong(),

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

