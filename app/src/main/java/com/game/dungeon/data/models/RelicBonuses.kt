package com.game.dungeon.data.models

data class RelicBonuses(
    val attackBonus: Int,
    val hpBonus: Int,
    val mpBonus: Int,
    val magicBonus: Int,
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
    val doubleLootChance: Int
) {
    companion object {
        fun from(gs: GameState) = RelicBonuses(
            attackBonus = gs.attackRelic * 2,
            hpBonus = gs.hpRelic * 15,
            mpBonus = gs.mpRelic * 10,
            magicBonus = gs.magicRelic * 2,
            goldMultiplier = 1f + gs.goldRelic * 0.05f,
            magiciteChanceBonus = gs.magiciteRelic * 0.01f,
            expMultiplier = gs.expMultiplier,
            itemStatBonus = gs.itemStatBonus,
            magicShopLevel = gs.magicShopLevel,
            critChanceBonus = gs.critChanceRelic,
            critDamageBonus = gs.critDamageRelic * 5,
            magnetBonus = gs.magnetBonus,
            pocketsBonus = gs.pocketsBonus,
            doubleLootChance = gs.doubleLootChance
        )
    }
}
