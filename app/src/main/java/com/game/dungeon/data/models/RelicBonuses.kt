package com.game.dungeon.data.models

data class RelicBonuses(
    val attackBonus: Int,
    val hpBonus: Int,
    val mpBonus: Int,
    val magicBonus: Int,
    val goldMultiplier: Float,
    val magiciteChanceBonus: Float
) {
    companion object {
        fun from(gs: GameState) = RelicBonuses(
            attackBonus = gs.attackRelic * 2,
            hpBonus = gs.hpRelic * 15,
            mpBonus = gs.mpRelic * 10,
            magicBonus = gs.magicRelic * 2,
            goldMultiplier = 1f + gs.goldRelic * 0.05f,
            magiciteChanceBonus = gs.magiciteRelic * 0.01f
        )
    }
}
