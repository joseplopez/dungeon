package com.game.dungeon.data.models

import android.content.Context
import java.util.UUID

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
            relicBonuses: RelicBonuses? = null,
            dimension: FFDimension = FFDimensionData.getDimension(1)
            ): Enemy {
            // Enhanced stat scaling with floor depth
            val baseHp = (20 + floor * 12 + (dimension.number * dimension.number * 0.05f)) * template.hpMult * difficultyMult
            val baseAtk = (5 + floor * 2.2f + (dimension.number * dimension.number * 0.01f)) * template.atkMult * difficultyMult
            val baseDef = (1 + floor / 2.5f) * template.defMult * difficultyMult

            // Gil scaling with floor level and difficulty
            val scaledGil = (template.gilReward * (1f + floor * 0.12f) * (1f + (difficultyMult - 1f) * 0.5f)).toInt().coerceAtLeast(template.gilReward)

            // Magicite scaling: Bosses grant large magicite payouts; regular monsters drop higher quantities on deeper floors
            val magiciteQuantity = if (template.isBoss) {
                (5 + floor / 10).coerceAtLeast(3)
            } else {
                val baseChance = template.magiciteChance + (floor / 250f)
                val bonusChance = relicBonuses?.magnetBonus ?: 0f
                val rawChance = baseChance + bonusChance
                val finalChance = if (rawChance > 0f) (rawChance / (rawChance + 0.25f)).coerceAtMost(0.80f) else 0f
                if (Math.random() < finalChance) (1 + floor / 50).coerceAtLeast(1) else 0
            }

            val enemyName = try { context.getString(template.nameRes) } catch (_: Exception) { null } ?: "Monster"

            return Enemy(
                name = enemyName,
                emoji = template.emoji,
                maxHp = baseHp.toInt(),
                currentHp = baseHp.toInt(),
                attack = baseAtk.toInt(),
                defense = baseDef.toInt(),
                magicDefense = (baseDef * 0.8f).toInt(),
                speed = 5 + (floor / 8),
                gilReward = scaledGil,
                magiciteDropped = magiciteQuantity,
                floor = floor,
                isBoss = template.isBoss
            )
        }
    }
}

