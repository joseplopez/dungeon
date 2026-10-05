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
            // Moderate boss dimension multiplier to prevent extreme stat inflation at high dimensions
            val bossDimExtra = if (template.isBoss) 1f + (dimension.number - 1) * 0.02f else 1f
            val totalDimMult = dimMult * bossDimExtra

            // For bosses in late game (high floors > 100), apply a soft dampening factor
            // to keep boss stats aligned with hero progression curves.
            val bossLateGameDampener = if (template.isBoss && floor > 100) {
                1f / (1f + (floor - 100) * 0.0012f)
            } else {
                1f
            }

            // Enhanced stat scaling with floor depth and dimension level
            val baseHp = (20 + floor * 12) * template.hpMult * difficultyMult * totalDimMult * bossLateGameDampener
            val baseAtk = (5 + floor * 2.2f) * template.atkMult * difficultyMult * totalDimMult * bossLateGameDampener
            val baseDef = (1 + floor / 2.5f) * template.defMult * difficultyMult * totalDimMult * bossLateGameDampener

            // Gil scaling with floor level and difficulty (sub-linear curve to prevent high floor inflation)
            val floorGilMult = 1f + (floor * 0.02f) + (kotlin.math.sqrt(floor.toFloat()) * 0.15f)
            val scaledGil = (template.gilReward * floorGilMult * (1f + (difficultyMult - 1f) * 0.5f)).toInt().coerceAtLeast(template.gilReward)

            // Magicite scaling: Bosses grant scaling magicite payouts per dimension; regular monsters drop quantity scales in late dimensions
            val magiciteQuantity = if (template.isBoss) {
                val baseBossMagicite = 3f + kotlin.math.sqrt(floor.toFloat()) * 0.75f
                val dimMagiciteMult = 1f + (dimension.number - 1) * 0.50f
                val maxCap = 20 + (dimension.number - 1) * 15
                (baseBossMagicite * dimMagiciteMult).toInt().coerceIn(3, maxCap)
            } else {
                val dimChanceBonus = (dimension.number - 1) * 0.015f
                val baseChance = template.magiciteChance + (floor / 1000f) + dimChanceBonus
                val bonusChance = (relicBonuses?.magiciteChanceBonus ?: 0f) + (relicBonuses?.magnetBonus ?: 0f)
                val rawChance = baseChance + bonusChance
                val maxChanceCap = (0.25f + (dimension.number - 1) * 0.02f).coerceAtMost(0.50f)
                val finalChance = if (rawChance > 0f) (rawChance / (rawChance + 0.25f)).coerceAtMost(maxChanceCap) else 0f
                if (Math.random() < finalChance) {
                    val dimQtyBonus = ((dimension.number - 1) * 0.5f).toInt()
                    val floorQtyBonus = if (floor >= 300) 1 else 0
                    1 + dimQtyBonus + floorQtyBonus
                } else 0
            }/2

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

