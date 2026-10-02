package com.game.dungeon.data.models

import android.content.Context
import java.util.UUID

data class Enemy(
    val id: String = UUID.randomUUID().toString(),
    val type: MonsterType = MonsterType.GOBLIN,
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
            // Dimension difficulty scaling factor (Dimension 1 = 1.0x baseline; higher dimensions scale stats)
            val dimMult = 1f + (dimension.number - 1) * 0.30f
            val bossDimExtra = if (template.isBoss) 1f + (dimension.number - 1) * 0.15f else 1f
            val totalDimMult = dimMult * bossDimExtra

            // Enhanced stat scaling with floor depth and dimension level
            val baseHp = (20 + floor * 12) * template.hpMult * difficultyMult * totalDimMult
            val baseAtk = (5 + floor * 2.2f) * template.atkMult * difficultyMult * totalDimMult
            val baseDef = (1 + floor / 2.5f) * template.defMult * difficultyMult * totalDimMult

            // Gil scaling with floor level and difficulty (sub-linear curve to prevent high floor inflation)
            val floorGilMult = 1f + (floor * 0.02f) + (kotlin.math.sqrt(floor.toFloat()) * 0.15f)
            val scaledGil = (template.gilReward * floorGilMult * (1f + (difficultyMult - 1f) * 0.5f)).toInt().coerceAtLeast(template.gilReward)

            // Magicite scaling: Bosses grant balanced magicite payouts; regular monsters drop occasionally without flooding
            val magiciteQuantity = if (template.isBoss) {
                (3 + (kotlin.math.sqrt(floor.toFloat()) * 0.75f).toInt()).coerceIn(3, 20)
            } else {
                val baseChance = template.magiciteChance + (floor / 1000f)
                val bonusChance = relicBonuses?.magnetBonus ?: 0f
                val rawChance = baseChance + bonusChance
                val finalChance = if (rawChance > 0f) (rawChance / (rawChance + 0.25f)).coerceAtMost(0.25f) else 0f
                if (Math.random() < finalChance) (if (floor >= 300) 2 else 1) else 0
            }

            val enemyName = try { context.getString(template.nameRes) } catch (_: Exception) { null } ?: "Monster"

            return Enemy(
                type = template.type,
                name = enemyName,
                emoji = template.emoji,
                maxHp = baseHp.toInt(),
                currentHp = baseHp.toInt(),
                attack = baseAtk.toInt(),
                defense = baseDef.toInt(),
                magicDefense = (baseDef * 0.8f).toInt(),
                speed = 5 + (floor / 8) + (dimension.number - 1),
                gilReward = scaledGil,
                magiciteDropped = magiciteQuantity,
                floor = floor,
                isBoss = template.isBoss
            )
        }
    }
}

