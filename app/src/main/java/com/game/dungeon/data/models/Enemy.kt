package com.game.dungeon.data.models

import android.content.Context
import java.util.UUID

typealias FFEnemy = Enemy

data class Enemy(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val emoji: String,
    var currentHp: Int,
    val maxHp: Int,
    val attack: Int,
    val defense: Int,
    val magicDefense: Int = 0,
    val speed: Int = 5,
    val gilReward: Int,
    val magiciteDropped: Int = 0,
    val floor: Int,
    val isBoss: Boolean = false
) {
    val gilDropped: Int get() = gilReward

    companion object {
        fun fromTemplate(
            template: FFEnemyTemplate,
            floor: Int,
            context: Context,
            difficultyMult: Float = 1.0f,
            relicBonuses: RelicBonuses? = null
        ): Enemy {
            val baseHp = (20 + floor * 8) * template.hpMult * difficultyMult
            val baseAtk = (5 + floor * 1.5).toInt() * template.atkMult * difficultyMult
            val baseDef = (1 + floor / 3).toInt() * template.defMult * difficultyMult
            
            // Magnet Relic: +5% base magicite drop rate
            val baseChance = template.magiciteChance
            val bonusChance = relicBonuses?.magnetBonus ?: 0f
            val finalChance = (baseChance + bonusChance).coerceIn(0f, 1f)

            return Enemy(
                name = context.getString(template.nameRes),
                emoji = template.emoji,
                maxHp = baseHp.toInt(),
                currentHp = baseHp.toInt(),
                attack = baseAtk.toInt(),
                defense = baseDef.toInt(),
                magicDefense = (baseDef * 0.8f).toInt(),
                speed = 5 + (floor / 10),
                gilReward = template.gilReward,
                magiciteDropped = if (Math.random() < finalChance) 1 else 0,
                floor = floor,
                isBoss = template.isBoss
            )
        }
    }
}
