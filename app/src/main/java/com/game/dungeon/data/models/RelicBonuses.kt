package com.game.dungeon.data.models

data class RelicBonuses(
    val attackBonus: Int,
    val hpBonus: Int,
    val mpBonus: Int,
    val magicBonus: Int,
    val defenseBonus: Int,
    val goldMultiplier: Float,
    val magiciteChanceBonus: Float,
    // Town upgrade bonuses
    val expMultiplier: Float,
    val itemStatBonus: Float,
    val magicShopLevel: Int,
    // Crit
    val critChanceBonus: Int,
    val critDamageBonus: Int,
    // Ascended Relics
    val magnetBonus: Float,
    val pocketsBonus: Float,
    val doubleLootChance: Int,
    // Masteries & Pets
    val jobMasteryLevels: Map<HeroClass, Int>,
    val selectedPet: PetType?,
    val bossesDefeatedCount: Int
) {
    fun getMasteryBonus(heroClass: HeroClass, stat: StatType): Int {
        val level = jobMasteryLevels[heroClass] ?: 0
        return if (heroClass.masteryStatType == stat) level * heroClass.masteryBonusPerLevel else 0
    }

    val petItemFindBonus: Float get() = if (selectedPet == PetType.CHOCOBO) PetType.CHOCOBO.bonusValue else 0f
    val petGilFindBonus: Float get() = if (selectedPet == PetType.CAT) PetType.CAT.bonusValue else 0f
    val petCritChanceBonus: Int get() = if (selectedPet == PetType.CACTUAR) PetType.CACTUAR.bonusValue.toInt() else 0
    val petCritDamageBonus: Int get() = if (selectedPet == PetType.TONBERRY) PetType.TONBERRY.bonusValue.toInt() else 0

    companion object {
        fun from(gs: GameState) = RelicBonuses(
            attackBonus = gs.attackRelic * 2,
            hpBonus = gs.hpRelic * 15,
            mpBonus = gs.mpRelic * 10,
            magicBonus = gs.magicRelic * 2,
            defenseBonus = gs.defenseRelic * 2,
            goldMultiplier = (1f + gs.goldRelic * 0.05f) + (if (gs.selectedPet == PetType.CAT) PetType.CAT.bonusValue else 0f),
            magiciteChanceBonus = gs.magiciteRelic * 0.01f,
            expMultiplier = gs.expMultiplier,
            itemStatBonus = gs.itemStatBonus,
            magicShopLevel = gs.magicShopLevel,
            critChanceBonus = gs.critChanceRelic,
            critDamageBonus = gs.critDamageRelic * 5,
            magnetBonus = gs.magnetBonus,
            pocketsBonus = gs.pocketsBonus,
            doubleLootChance = gs.doubleLootChance,
            jobMasteryLevels = gs.jobMasteryLevels,
            selectedPet = gs.selectedPet,
            bossesDefeatedCount = gs.bossesDefeatedNames.size
        )
    }
}
