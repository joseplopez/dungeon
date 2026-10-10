package com.game.dungeon.data.models

import android.content.Context
import java.util.UUID
import kotlin.math.roundToInt

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
    var magiciteDropped: Int = 0,
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
            // ── DIMENSION SCALING ──
            // Dim 1 = 0.75x (easier for starter Freelancers), scaling up +0.25x per dimension (Dim 10 = 3.0x)
            val dimMult = 0.75f + (dimension.number - 1) * 0.25f
            val bossDimExtra = if (template.isBoss) 1f + (dimension.number - 1) * 0.02f else 1f
            val totalDimMult = dimMult * bossDimExtra

            // ── EARLY FLOOR ONBOARDING RAMP (Floors 1–20) ──
            // Scales monster stats from 60% on Floor 1 up to 100% by Floor 20
            val floorRamp = (0.60f + (floor / 20.0f) * 0.40f).coerceAtMost(1.0f)

            // Softened dampener for bosses past floor 100
            val bossLateGameDampener = if (template.isBoss && floor > 100) {
                1f / (1f + (floor - 100) * 0.0006f)
            } else {
                1f
            }

            // ── STAT TUNING ──
            val mobHpBonus = if (template.isBoss) 1.0 else 1.40
            val globalAtkBoost = 1.35

            // Enhanced stat scaling with onboarding ramp and dimension multiplier
            val rawHp = (25.0 + floor.toDouble() * 16.0) * template.hpMult * mobHpBonus * difficultyMult * totalDimMult * floorRamp * bossLateGameDampener
            val rawAtk = (7.0 + floor.toDouble() * 3.0) * template.atkMult * globalAtkBoost * difficultyMult * totalDimMult * floorRamp * bossLateGameDampener
            val rawDef = (1.0 + floor.toDouble() / 2.5) * template.defMult * difficultyMult * totalDimMult * floorRamp * bossLateGameDampener

            val safeHp = rawHp.coerceIn(10.0, Int.MAX_VALUE.toDouble()).toInt()
            val safeAtk = rawAtk.coerceIn(1.0, (Int.MAX_VALUE / 2).toDouble()).toInt()
            val safeDef = rawDef.coerceIn(0.0, (Int.MAX_VALUE / 4).toDouble()).toInt()

            // Gil scaling
            val floorGilMult = 1.0 + (floor.toDouble() * 0.02) + (kotlin.math.sqrt(floor.toDouble()) * 0.15)
            val scaledGil = (template.gilReward * floorGilMult * (1.0 + (difficultyMult - 1.0) * 0.5)).coerceIn(template.gilReward.toDouble(), Int.MAX_VALUE.toDouble()).toInt()

            // Magicite scaling
            val yieldMult = relicBonuses?.magiciteYieldBonus ?: 1.0f
            val rawMagicite = if (template.isBoss) {
                val baseBossMagicite = 3f + kotlin.math.sqrt(floor.toFloat()) * 0.75f
                val dimMagiciteMult = 1f + (dimension.number - 1) * 0.50f
                val maxCap = 20 + (dimension.number - 1) * 15
                (baseBossMagicite * dimMagiciteMult).toInt().coerceIn(3, maxCap)
            } else {
                val dimChanceBonus = (dimension.number - 1) * 0.015f
                val baseChance = template.magiciteChance + (floor / 1000f) + dimChanceBonus
                val bonusChance = (relicBonuses?.magiciteChanceBonus ?: 0f) + (relicBonuses?.magnetBonus ?: 0f) + (relicBonuses?.alchemistDropChanceBonus ?: 0f)
                val rawChance = baseChance + bonusChance
                val maxChanceCap = (0.25f + (dimension.number - 1) * 0.02f + (relicBonuses?.alchemistDropChanceBonus ?: 0f)).coerceAtMost(0.75f)
                val finalChance = if (rawChance > 0f) (rawChance / (rawChance + 0.25f)).coerceAtMost(maxChanceCap) else 0f
                if (Math.random() < finalChance) {
                    val dimQtyBonus = ((dimension.number - 1) * 0.5f).roundToInt()
                    val floorQtyBonus = if (floor >= 300) 1 else 0
                    1 + dimQtyBonus + floorQtyBonus
                } else 0
            }
            val magiciteQuantity = if (rawMagicite > 0) (rawMagicite * yieldMult).toInt().coerceAtLeast(1) else 0

            val enemyName = try { context.getString(template.nameRes) } catch (_: Exception) { null } ?: "Monster"

            return Enemy(
                type = template.type,
                name = enemyName,
                emoji = template.emoji,
                maxHp = safeHp,
                currentHp = safeHp,
                attack = safeAtk,
                defense = safeDef,
                magicDefense = (safeDef * 0.8f).toInt(),
                speed = (5 + (floor / 20) + (dimension.number - 1)).coerceAtMost(35),
                gilReward = scaledGil,
                magiciteDropped = magiciteQuantity,
                floor = floor,
                isBoss = template.isBoss
            )
        }
    }
}