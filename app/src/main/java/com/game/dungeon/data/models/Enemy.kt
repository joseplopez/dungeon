package com.game.dungeon.data.models

import android.content.Context
import java.util.UUID
import kotlin.math.roundToInt

data class Enemy(
    val id: String = UUID.randomUUID().toString(),
    val type: MonsterType = MonsterType.GOBLIN,
    val name: String,
    val emoji: String,
    var currentHp: Long,
    val maxHp: Long,
    val attack: Long,
    val defense: Long,
    val magicDefense: Long = 0,
    val speed: Int = 5,
    val gilReward: Long,
    var magiciteDropped: Long = 0,
    val floor: Int,
    val isBoss: Boolean = false
) {
    val gilDropped: Long get() = gilReward

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
            // Dim 1-10: 0.75x to 3.0x. Dim 11+: accelerated scaling (+0.80x per dim past 10)
            val dimMult = if (dimension.number <= 10) {
                0.75f + (dimension.number - 1) * 0.25f
            } else {
                3.0f + (dimension.number - 10) * 0.80f
            }
            val bossDimExtra = if (template.isBoss) {
                if (dimension.number <= 10) 1f + (dimension.number - 1) * 0.02f else 1f + (dimension.number - 1) * 0.05f
            } else 1f
            val totalDimMult = dimMult * bossDimExtra

            // ── EARLY FLOOR ONBOARDING RAMP (Floors 1–20) ──
            // Scales monster stats from 60% on Floor 1 up to 100% by Floor 20
            val floorRamp = (0.60f + (floor / 20.0f) * 0.40f).coerceAtMost(1.0f)

            // Boss dampener only for story dimensions (Dim 1-10 <= floor 1000). Dim 11+ bosses are undampened!
            val bossLateGameDampener = if (template.isBoss && dimension.number <= 10 && floor > 100) {
                1f / (1f + (floor - 100) * 0.0006f)
            } else {
                1f
            }

            // ── STAT TUNING ──
            val mobHpBonus = if (template.isBoss) 1.0 else 1.40
            val globalAtkBoost = 1.25

            // Non-linear floor scaling for high floors (Floors > 200 / Dimension 11+)
            val baseHp = 25.0 + floor.toDouble() * 16.0
            val baseAtk = 7.0 + floor.toDouble() * 2.1
            val baseDef = 1.0 + floor.toDouble() / 2.5

            // True quadratic scaling for high floors (Floors > 200) to match Gear/Hero curves
            val floorHpBonus = if (floor > 200) {
                val scaledFloor = (floor - 200).toDouble() / 5.0 // Steepened HP curve
                scaledFloor * scaledFloor * 1.5
            } else 0.0

            val floorAtkBonus = if (floor > 200) {
                val scaledFloor = (floor - 200).toDouble() / 15.0 // Steepened ATK curve
                scaledFloor * scaledFloor * 1.2
            } else 0.0

            val floorDefBonus = if (floor > 200) {
                val scaledFloor = (floor - 200).toDouble() / 20.0 // Steepened DEF curve
                scaledFloor * scaledFloor * 0.8
            } else 0.0

            val rawHp = (baseHp + floorHpBonus) * template.hpMult * mobHpBonus * difficultyMult * totalDimMult * floorRamp * bossLateGameDampener
            val rawAtk = (baseAtk + floorAtkBonus) * template.atkMult * globalAtkBoost * difficultyMult * totalDimMult * floorRamp * bossLateGameDampener
            val rawDef = (baseDef + floorDefBonus) * template.defMult * difficultyMult * totalDimMult * floorRamp * bossLateGameDampener

            val safeHp = rawHp.coerceIn(10.0, Double.MAX_VALUE).toLong()
            val safeAtk = rawAtk.coerceIn(1.0, (Double.MAX_VALUE / 2)).toLong()
            val safeDef = rawDef.coerceIn(0.0, (Double.MAX_VALUE / 4)).toLong()

            // Gil scaling
            val floorGilMult = 1.0 + (floor.toDouble() * 0.02) + (kotlin.math.sqrt(floor.toDouble()) * 0.15)
            val scaledGil = (template.gilReward * floorGilMult * (1.0 + (difficultyMult - 1.0) * 0.5)).coerceIn(template.gilReward.toDouble(),
                Double.MAX_VALUE)

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
                magicDefense = (safeDef * 0.8).toLong(),
                speed = (5 + (floor / 20) + (dimension.number - 1)).coerceAtMost(50),
                gilReward = scaledGil.toLong(),
                magiciteDropped = magiciteQuantity.toLong(),
                floor = floor,
                isBoss = template.isBoss
            )
        }
    }
}