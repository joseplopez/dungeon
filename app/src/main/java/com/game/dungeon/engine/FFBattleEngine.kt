package com.game.dungeon.engine

import android.content.Context
import androidx.annotation.StringRes
import com.game.dungeon.R
import com.game.dungeon.data.models.*
import kotlinx.coroutines.delay
import kotlin.random.Random

sealed class FFBattleEvent {
    data class TurnStart(val unitId: String, val unitName: String) : FFBattleEvent()
    data class FloorStart(val floor: Int, val enemies: List<Enemy>) : FFBattleEvent()
    data class DamageDealt(val attackerId: String, val targetId: String,
                           val damage: Int, val isMagic: Boolean, val isCritical: Boolean) : FFBattleEvent()
    data class HealCast(val casterId: String, val targetId: String, val amount: Int) : FFBattleEvent()
    data class GroupHeal(val casterId: String, val amounts: Map<String, Int>) : FFBattleEvent()
    data class AbilityUsed(val heroId: String, @StringRes val nameRes: Int, @StringRes val descRes: Int, val args: List<Any> = emptyList()) : FFBattleEvent()
    data class ExpGained(val heroId: String, val amount: Int, val leveledUp: Boolean, val newLevel: Int) : FFBattleEvent()
    data class EnemyDefeated(val enemyId: String, val monsterType: MonsterType = MonsterType.GOBLIN, val gilDropped: Int, val magiciteDropped: Int, val expDropped: Int, val materialsDropped: Map<String, Int> = emptyMap()) : FFBattleEvent()
    data class HeroFell(val heroId: String, val heroName: String, val heroClass: HeroClass) : FFBattleEvent()
    data class MagiciteStolen(val heroId: String, val amount: Int) : FFBattleEvent()
    data class SummonUsed(val heroId: String, val summonName: String, val totalDamage: Int) : FFBattleEvent()
    data class BardSong(val heroId: String, @StringRes val songRes: Int, @StringRes val effectRes: Int) : FFBattleEvent()
    data class FloorComplete(val floor: Int, val gilEarned: Long, val magiciteEarned: Int, val itemsFound: List<Item>, val updatedHeroes: List<Hero>, val leveledUpHeroIds: List<String> = emptyList()) : FFBattleEvent()
    data class BossDefeated(val bossName: String, val dimensionComplete: Boolean, val materialsDropped: Map<String, Int> = emptyMap()) : FFBattleEvent()
    data class MaterialDropped(val materialId: String, val amount: Int = 1) : FFBattleEvent()
    object AllHeroesFell : FFBattleEvent()
}

class FFBattleEngine(private val context: Context) {
    suspend fun runBattle(
        heroes: List<Hero>,
        dimension: FFDimension,
        startFloor: Int,
        speed: BattleSpeed,
        relicBonuses: RelicBonuses,
        isPaused: () -> Boolean = { false },
        onEvent: (FFBattleEvent) -> Unit
    ) {
        var currentFloor = startFloor
        val maxFloor = dimension.maxFloor
        val aliveHeroes = heroes.map { it.copy() }.toMutableList()
        var totalGil = 0L
        var totalMagicite = 0
        var lastAbilityClass: HeroClass? = null

        while (aliveHeroes.any { it.isAlive } && currentFloor <= maxFloor) {
            while (isPaused()) { delay(200) }

            // Track all heroes that level up during the entire floor duration
            val floorLeveledUpHeroIds = mutableSetOf<String>()

            // Spawn enemies for this floor
            val bossTemplate = FFDimensionData.getBossForFloor(dimension, currentFloor)
            val enemies = if (bossTemplate != null) {
                mutableListOf(Enemy.fromTemplate(bossTemplate, currentFloor, context, relicBonuses = relicBonuses, dimension = dimension))
            } else {
                spawnEnemies(dimension, currentFloor, context, relicBonuses).toMutableList()
            }

            var floorBaseGilEarned = 0
            var floorMagiciteEarned = 0

            onEvent(FFBattleEvent.FloorStart(currentFloor, enemies))

            // Floor battle loop
            while (enemies.any { it.currentHp > 0 } && aliveHeroes.any { it.isAlive }) {
                val turnOrder = (aliveHeroes.filter { it.isAlive } + enemies.filter { it.currentHp > 0 })
                    .sortedByDescending {
                        when (it) {
                            is Hero -> it.speed
                            is Enemy -> it.speed
                            else -> 0
                        }
                    }

                for (actor in turnOrder) {
                    while (isPaused()) { delay(200) }
                    if (!enemies.any { it.currentHp > 0 } || !aliveHeroes.any { it.isAlive }) break

                    when (actor) {
                        is Hero -> {
                            if (actor.isAlive) {
                                executeHeroTurn(
                                    actor, aliveHeroes, enemies, dimension, relicBonuses,
                                    floorLeveledUpHeroIds, { lastAbilityClass }, { lastAbilityClass = it },
                                    { gil, magicite ->
                                        floorBaseGilEarned += gil
                                        floorMagiciteEarned += magicite
                                    },
                                    onEvent
                                )
                                delay(speed.delayMs)
                            }
                        }
                        is Enemy -> {
                            // STRICT CHECK: Only execute turn AND delay if enemy is actually alive
                            if (actor.currentHp > 0) {
                                executeEnemyTurn(actor, aliveHeroes, onEvent)
                                delay(speed.delayMs)
                            }
                        }
                    }
                }
            }

            // Floor cleared
            if (aliveHeroes.any { it.isAlive }) {
                val gilEarned = (floorBaseGilEarned.toLong() * relicBonuses.goldMultiplier).toLong()
                val magiciteEarned = floorMagiciteEarned

                // Award completion XP
                val completionExp = ((5 + (currentFloor / 2)) * relicBonuses.expMultiplier).toInt()
                aliveHeroes.filter { it.isAlive }.forEach { hero ->
                    val result = hero.addExperience(completionExp)
                    if (result) floorLeveledUpHeroIds.add(hero.id)
                    onEvent(FFBattleEvent.ExpGained(hero.id, completionExp, result, hero.level))
                }

                // Loot chance
                val itemsFound = mutableListOf<Item>()
                val rawDropChance = if (bossTemplate != null) 100f else 10f * (1f + relicBonuses.petItemFindBonus)
                val finalDropChance = if (bossTemplate != null) 100f else (100f * (rawDropChance / (rawDropChance + 85f))).coerceAtMost(85f)

                if (Random.nextFloat() * 100 < finalDropChance) {
                    itemsFound.add(
                        Item.random(
                            floor = currentFloor,
                            context = context,
                            relicBonuses = relicBonuses,
                            minRarity = if (bossTemplate != null) Rarity.RARE else Rarity.COMMON,
                            dimension = dimension.number
                        )
                    )
                    // Double Loot Relic
                    if (bossTemplate != null && Random.nextFloat() * 100f < relicBonuses.effectiveDoubleLootChance) {
                        itemsFound.add(
                            Item.random(
                                floor = currentFloor,
                                context = context,
                                relicBonuses = relicBonuses,
                                minRarity = Rarity.RARE,
                                dimension = dimension.number
                            )
                        )
                    }
                }

                totalGil += gilEarned
                totalMagicite += magiciteEarned

                if (bossTemplate != null) {
                    val bossName = runCatching { context.getString(bossTemplate.nameRes) }.getOrNull()
                        ?: runCatching { context.resources?.getString(bossTemplate.nameRes) }.getOrNull()
                        ?: "Boss"
                    onEvent(FFBattleEvent.BossDefeated(bossName, currentFloor >= maxFloor))
                }

                aliveHeroes.removeAll { !it.isAlive }

                onEvent(FFBattleEvent.FloorComplete(
                    currentFloor, gilEarned, magiciteEarned, itemsFound,
                    aliveHeroes.map { it.copy() }, floorLeveledUpHeroIds.toList()
                ))

                currentFloor++
            }
        }

        if (!aliveHeroes.any { it.isAlive }) onEvent(FFBattleEvent.AllHeroesFell)
    }

    private fun spawnEnemies(dimension: FFDimension, floor: Int, context: Context, relicBonuses: RelicBonuses): List<Enemy> {
        val templates = FFDimensionData.getEnemiesForFloor(dimension, floor)
        if (templates.isEmpty()) return emptyList()
        val count = Random.nextInt(1, 4)
        return List(count) { Enemy.fromTemplate(templates.random(), floor, context, relicBonuses = relicBonuses, dimension = dimension) }
    }

    private fun handleEnemyDefeated(
        enemy: Enemy,
        dimension: FFDimension,
        relicBonuses: RelicBonuses,
        allies: List<Hero>,
        levelUpTracker: MutableSet<String>,
        onLootCollected: (Int, Int) -> Unit,
        onEvent: (FFBattleEvent) -> Unit
    ) {
        val exp = ((enemy.floor * 2) * relicBonuses.expMultiplier).toInt()
        val materials = calculateMaterialDrops(enemy, enemy.floor, dimension, relicBonuses)
        onLootCollected(enemy.gilDropped, enemy.magiciteDropped)
        onEvent(FFBattleEvent.EnemyDefeated(enemy.id, enemy.type, enemy.gilDropped, enemy.magiciteDropped, exp, materials))
        materials.forEach { (matId, amt) ->
            onEvent(FFBattleEvent.MaterialDropped(matId, amt))
        }
        awardExpToParty(allies, exp, levelUpTracker, onEvent)
    }

    internal fun calculateMaterialDrops(
        enemy: Enemy,
        floor: Int,
        dimension: FFDimension,
        relicBonuses: RelicBonuses
    ): Map<String, Int> {
        val drops = mutableMapOf<String, Int>()
        val relicMultiplier = 1f + relicBonuses.alchemistDropChanceBonus + (relicBonuses.magnetBonus * 0.10f)

        // Strictly uses enemy.isBoss — NO floor % 10 check
        val dropRules = MonsterLootTable.getDropsForMonster(enemy.type, enemy.isBoss)

        dropRules.forEach { rule ->
            val rawChance = rule.baseDropChance * relicMultiplier
            val maxCap = if (rule.isBossTrophy) 0.50f else 0.25f
            val finalChance = rawChance.coerceAtMost(maxCap)

            if (Random.nextFloat() < finalChance) {
                val matId = rule.materialId
                val baseAmount = rule.minQuantity
                val extraYieldChance = (relicBonuses.pocketsBonus * 0.2f) + (if (floor >= 50) 0.2f else 0.0f)
                val qty = if (Random.nextFloat() < extraYieldChance) baseAmount + 1 else baseAmount
                drops[matId] = (drops[matId] ?: 0) + qty
            }
        }

        return drops
    }

    private fun applyDamageToEnemy(
        attacker: Hero,
        target: Enemy,
        damage: Int,
        isMagic: Boolean,
        isCritical: Boolean,
        enemies: MutableList<Enemy>,
        dimension: FFDimension,
        relicBonuses: RelicBonuses,
        allies: List<Hero>,
        levelUpTracker: MutableSet<String>,
        onLootCollected: (Int, Int) -> Unit,
        onEvent: (FFBattleEvent) -> Unit
    ) {
        val wasAlive = target.currentHp > 0
        target.currentHp -= damage
        onEvent(FFBattleEvent.DamageDealt(attacker.id, target.id, damage, isMagic, isCritical))
        if (wasAlive && target.currentHp <= 0) {
            enemies.remove(target) // Remove defeated enemy from combat immediately so heroes never attack 0-health enemies
            handleEnemyDefeated(target, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
        }
    }

    private fun executeHeroTurn(
        hero: Hero,
        allies: List<Hero>,
        enemies: MutableList<Enemy>,
        dimension: FFDimension,
        relicBonuses: RelicBonuses,
        levelUpTracker: MutableSet<String>,
        getLastAbility: () -> HeroClass?,
        setLastAbility: (HeroClass) -> Unit,
        onLootCollected: (Int, Int) -> Unit,
        onEvent: (FFBattleEvent) -> Unit
    ) {
        onEvent(FFBattleEvent.TurnStart(hero.id, hero.name))
        val masteryLvl = relicBonuses.getMasteryLevel(hero.heroClass)
        hero.jobMasteryLevel = masteryLvl
        val activeSkill = JobAbilityData.getActiveAbilityForLevel(hero.heroClass, masteryLvl)
        if (activeSkill != null) {
            hero.abilityCharge++
            if (relicBonuses.warRoomAbilityChargeBonus > 0f && Math.random() < relicBonuses.warRoomAbilityChargeBonus) {
                hero.abilityCharge++
            }
        } else {
            hero.abilityCharge = 0
        }
        val useAbility = hero.abilityCharge >= 3

        if (useAbility) {
            hero.abilityCharge = 0
            executeJobAbility(hero, allies, enemies, dimension, relicBonuses, levelUpTracker, getLastAbility, setLastAbility, onLootCollected, onEvent)
            return
        }

        when (hero.aiPriority) {
            AIPriority.ATTACK -> {
                val target = enemies.filter { it.currentHp > 0 }.minByOrNull { it.currentHp } ?: return
                val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                applyDamageToEnemy(hero, target, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
            }
            AIPriority.MAGIC -> {
                if (masteryLvl >= 1) {
                    val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp } ?: return
                    val (dmg, isCrit) = calcMagicDamage(hero, target)
                    applyDamageToEnemy(hero, target, dmg, true, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.minByOrNull { it.currentHp } ?: return
                    val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                    applyDamageToEnemy(hero, target, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                }
            }
            AIPriority.HEAL -> {
                val wounded = allies.filter { it.isAlive && it.currentHp < it.maxHp * 0.6f }
                    .minByOrNull { it.currentHp }
                if (masteryLvl >= 1 && wounded != null && hero.currentMp >= 10) {
                    val healAmt = (hero.magic * 2.5f + wounded.maxHp * 0.20f + 20).toInt()
                    wounded.currentHp = minOf(wounded.currentHp + healAmt, wounded.maxHp)
                    hero.currentMp -= 10
                    onEvent(FFBattleEvent.HealCast(hero.id, wounded.id, healAmt))
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.minByOrNull { it.currentHp } ?: return
                    val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                    applyDamageToEnemy(hero, target, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                }
            }
            AIPriority.DEFEND -> {
                val target = allies.filter { it.isAlive }.minByOrNull { it.currentHp }
                if (masteryLvl >= 1 && target != null && target.id != hero.id) {
                    onEvent(FFBattleEvent.AbilityUsed(hero.id, R.string.ability_cover_name, R.string.ability_cover_desc, listOf(target.name)))
                } else {
                    val enemyTarget = enemies.filter { it.currentHp > 0 }.minByOrNull { it.currentHp } ?: return
                    val (dmg, isCrit) = calcPhysicalDamage(hero, enemyTarget)
                    applyDamageToEnemy(hero, enemyTarget, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                }
            }
        }
    }

    private fun executeJobAbility(
        hero: Hero,
        allies: List<Hero>,
        enemies: MutableList<Enemy>,
        dimension: FFDimension,
        relicBonuses: RelicBonuses,
        levelUpTracker: MutableSet<String>,
        getLastAbility: () -> HeroClass?,
        setLastAbility: (HeroClass) -> Unit,
        onLootCollected: (Int, Int) -> Unit,
        onEvent: (FFBattleEvent) -> Unit
    ) {
        if (hero.heroClass != HeroClass.MIME) {
            setLastAbility(hero.heroClass)
        }
        val masteryLvl = relicBonuses.getMasteryLevel(hero.heroClass)
        val activeSkill = JobAbilityData.getActiveAbilityForLevel(hero.heroClass, masteryLvl) ?: return
        onEvent(FFBattleEvent.AbilityUsed(hero.id, activeSkill.nameRes, activeSkill.descRes, listOf(hero.name)))

        when (hero.heroClass) {
            HeroClass.FREELANCER -> {
                if (masteryLvl >= 25) {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 2.2f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                    val healAmt = (hero.maxHp * 0.30f + hero.attack * 0.5f).toInt()
                    hero.currentHp = minOf(hero.currentHp + healAmt, hero.maxHp)
                    onEvent(FFBattleEvent.HealCast(hero.id, hero.id, healAmt))
                    hero.abilityCharge = 2
                } else if (masteryLvl >= 10) {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.8f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                    val healAmt = (hero.maxHp * 0.30f + hero.attack * 0.5f).toInt()
                    hero.currentHp = minOf(hero.currentHp + healAmt, hero.maxHp)
                    onEvent(FFBattleEvent.HealCast(hero.id, hero.id, healAmt))
                } else {
                    val healAmt = (hero.maxHp * 0.25f + hero.attack * 0.5f).toInt()
                    hero.currentHp = minOf(hero.currentHp + healAmt, hero.maxHp)
                    onEvent(FFBattleEvent.HealCast(hero.id, hero.id, healAmt))
                }
            }
            HeroClass.WARRIOR -> {
                val mult = when { masteryLvl >= 25 -> 2.2f; masteryLvl >= 10 -> 1.8f; else -> 1.5f }
                val targetEnemies = if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else listOfNotNull(enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp })
                targetEnemies.forEach { target ->
                    val (baseDmg, _) = calcPhysicalDamage(hero, target)
                    val dmg = (baseDmg * mult).toInt()
                    applyDamageToEnemy(hero, target, dmg, false, true, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                }
            }
            HeroClass.WHITE_MAGE -> {
                if (masteryLvl >= 25) {
                    val amounts = mutableMapOf<String, Int>()
                    allies.filter { it.isAlive }.forEach { ally ->
                        val heal = (hero.magic * 3.5f + ally.maxHp * 0.40f + 50).toInt()
                        ally.currentHp = minOf(ally.currentHp + heal, ally.maxHp)
                        amounts[ally.id] = heal
                    }
                    if (amounts.isNotEmpty()) {
                        onEvent(FFBattleEvent.GroupHeal(hero.id, amounts))
                    }
                } else {
                    val target = allies.filter { it.isAlive }.minByOrNull { it.currentHp.toFloat() / it.maxHp } ?: hero
                    if (target.isAlive) {
                        val healMult = if (masteryLvl >= 10) 2.8f else 2.0f
                        val heal = (hero.magic * healMult + target.maxHp * 0.30f + 30).toInt()
                        target.currentHp = minOf(target.currentHp + heal, target.maxHp)
                        onEvent(FFBattleEvent.HealCast(hero.id, target.id, heal))
                    }
                }
            }
            HeroClass.BLACK_MAGE -> {
                val mult = when { masteryLvl >= 25 -> 2.2f; masteryLvl >= 10 -> 1.8f; else -> 1.4f }
                val targetEnemies = if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else if (masteryLvl >= 10) enemies.filter { it.currentHp > 0 }.take(2) else listOfNotNull(enemies.filter { it.currentHp > 0 }.firstOrNull())
                targetEnemies.forEach { enemy ->
                    val (baseDmg, _) = calcMagicDamage(hero, enemy)
                    val dmg = (baseDmg * mult).toInt()
                    applyDamageToEnemy(hero, enemy, dmg, true, true, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                }
            }
            HeroClass.THIEF -> {
                val hits = if (masteryLvl >= 25) 3 else 1
                val mult = when { masteryLvl >= 25 -> 2.0f; masteryLvl >= 10 -> 1.5f; else -> 1.2f }
                repeat(hits) {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return@repeat
                    val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                    val finalDmg = (dmg * mult).toInt()
                    val forceCrit = masteryLvl >= 25 || isCrit
                    applyDamageToEnemy(hero, target, finalDmg, false, forceCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    if (target.magiciteDropped > 0 && (hero.hasRansack || masteryLvl >= 10)) {
                        onEvent(FFBattleEvent.MagiciteStolen(hero.id, target.magiciteDropped))
                        target.magiciteDropped = 0
                    }
                }
            }
            HeroClass.MONK -> {
                val selfHeal = (hero.maxHp * 0.35f + hero.magic * 1.5f).toInt()
                hero.currentHp = minOf(hero.currentHp + selfHeal, hero.maxHp)
                onEvent(FFBattleEvent.HealCast(hero.id, hero.id, selfHeal))

                if (masteryLvl >= 25) {
                    enemies.filter { it.currentHp > 0 }.forEach { target ->
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 2.0f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                } else if (masteryLvl >= 10) {
                    repeat(3) {
                        val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return@repeat
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.2f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.4f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                }
            }
            HeroClass.KNIGHT -> {
                val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp }
                if (target != null) {
                    val totalAtkDef = hero.attack + hero.defense
                    val mult = when { masteryLvl >= 25 -> 2.2f; masteryLvl >= 10 -> 1.8f; else -> 1.5f }
                    val baseDmg = maxOf(1, totalAtkDef - target.defense + Random.nextInt(-3, 4))
                    val dmg = (baseDmg * mult).toInt()
                    applyDamageToEnemy(hero, target, dmg, false, true, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                }
                if (masteryLvl >= 10) {
                    val amounts = mutableMapOf<String, Int>()
                    allies.filter { it.isAlive }.forEach { ally ->
                        val healAmt = (hero.defense * 0.5f + ally.maxHp * 0.15f).toInt()
                        ally.currentHp = minOf(ally.currentHp + healAmt, ally.maxHp)
                        amounts[ally.id] = healAmt
                    }
                    if (amounts.isNotEmpty()) {
                        onEvent(FFBattleEvent.GroupHeal(hero.id, amounts))
                    }
                }
            }
            HeroClass.PALADIN -> {
                val targetEnemies = if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else listOfNotNull(enemies.filter { it.currentHp > 0 }.firstOrNull())
                val mult = when { masteryLvl >= 25 -> 2.0f; masteryLvl >= 10 -> 1.6f; else -> 1.3f }
                targetEnemies.forEach { enemy ->
                    val (dmg, isCrit) = calcPhysicalDamage(hero, enemy)
                    val finalDmg = (dmg * mult).toInt()
                    applyDamageToEnemy(hero, enemy, finalDmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                }

                val amounts = mutableMapOf<String, Int>()
                val livingAllies = allies.filter { it.isAlive }
                if (masteryLvl >= 25) {
                    livingAllies.forEach { ally ->
                        val healAmt = (ally.maxHp * 0.50f + hero.magic * 2.0f).toInt()
                        ally.currentHp = minOf(ally.currentHp + healAmt, ally.maxHp)
                        amounts[ally.id] = healAmt
                    }
                } else if (masteryLvl >= 10) {
                    livingAllies.forEach { ally ->
                        val healAmt = (hero.magic * 1.8f + ally.maxHp * 0.20f).toInt()
                        ally.currentHp = minOf(ally.currentHp + healAmt, ally.maxHp)
                        amounts[ally.id] = healAmt
                    }
                } else {
                    val lowestAlly = livingAllies.minByOrNull { it.currentHp.toFloat() / it.maxHp }
                    if (lowestAlly != null) {
                        val healAmt = (hero.magic * 1.5f + lowestAlly.maxHp * 0.15f).toInt()
                        lowestAlly.currentHp = minOf(lowestAlly.currentHp + healAmt, lowestAlly.maxHp)
                        amounts[lowestAlly.id] = healAmt
                    }
                }
                if (amounts.isNotEmpty()) {
                    onEvent(FFBattleEvent.GroupHeal(hero.id, amounts))
                }
            }
            HeroClass.RED_MAGE -> {
                if (masteryLvl >= 25) {
                    repeat(3) {
                        val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return@repeat
                        val (dmg, isCrit) = calcMagicDamage(hero, target)
                        val finalDmg = (dmg * 2.0f).toInt()
                        applyDamageToEnemy(hero, target, finalDmg, true, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                    val amounts = mutableMapOf<String, Int>()
                    allies.filter { it.isAlive }.forEach { ally ->
                        val healAmt = (hero.magic * 2.0f + ally.maxHp * 0.20f).toInt()
                        ally.currentHp = minOf(ally.currentHp + healAmt, ally.maxHp)
                        amounts[ally.id] = healAmt
                    }
                    if (amounts.isNotEmpty()) {
                        onEvent(FFBattleEvent.GroupHeal(hero.id, amounts))
                    }
                } else if (masteryLvl >= 10) {
                    repeat(2) {
                        val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return@repeat
                        val (dmg, isCrit) = calcMagicDamage(hero, target)
                        val finalDmg = (dmg * 1.8f).toInt()
                        applyDamageToEnemy(hero, target, finalDmg, true, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (dmg, isCrit) = calcMagicDamage(hero, target)
                        val finalDmg = (dmg * 1.4f).toInt()
                        applyDamageToEnemy(hero, target, finalDmg, true, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                    val lowestAlly = allies.filter { it.isAlive }.minByOrNull { it.currentHp.toFloat() / it.maxHp }
                    if (lowestAlly != null) {
                        val healAmt = (hero.magic * 2.0f + lowestAlly.maxHp * 0.20f).toInt()
                        lowestAlly.currentHp = minOf(lowestAlly.currentHp + healAmt, lowestAlly.maxHp)
                        onEvent(FFBattleEvent.HealCast(hero.id, lowestAlly.id, healAmt))
                    }
                }
            }
            HeroClass.SUMMONER -> {
                val (name, mult) = when {
                    masteryLvl >= 25 -> "Bahamut" to 2.5f
                    masteryLvl >= 10 -> "Ramuh" to 2.0f
                    else -> "Ifrit" to 1.8f
                }
                var totalDmg = 0
                enemies.filter { it.currentHp > 0 }.forEach { enemy ->
                    val (baseDmg, isCrit) = calcMagicDamage(hero, enemy)
                    val dmg = (baseDmg * mult).toInt()
                    totalDmg += dmg
                    applyDamageToEnemy(hero, enemy, dmg, true, isCrit || mult > 2f, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                }
                onEvent(FFBattleEvent.SummonUsed(hero.id, name, totalDmg))
            }
            HeroClass.NINJA -> {
                if (masteryLvl >= 25) {
                    val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp }
                    if (target != null) {
                        val dmg = (hero.attack * 2.8f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, true, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                } else if (masteryLvl >= 10) {
                    repeat(2) {
                        val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp } ?: return@repeat
                        val dmg = (hero.attack * 1.5f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, true, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp }
                    if (target != null) {
                        val dmg = (hero.attack * 1.8f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, true, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                }
            }
            HeroClass.DRAGOON -> {
                if (masteryLvl >= 25) {
                    enemies.filter { it.currentHp > 0 }.forEach { target ->
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 2.5f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                } else if (masteryLvl >= 10) {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val baseDmg = maxOf(1, hero.attack + Random.nextInt(-3, 4))
                        val dmg = (baseDmg * 2.0f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, true, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.6f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                }
            }
            HeroClass.BARD -> {
                val (songRes, effectRes, healPct) = when {
                    masteryLvl >= 25 -> Triple(R.string.song_ballad, R.string.song_ballad_effect, 0.45f)
                    masteryLvl >= 10 -> Triple(R.string.song_minuet, R.string.song_minuet_effect, 0.30f)
                    else -> Triple(R.string.song_paeon, R.string.song_paeon_effect, 0.20f)
                }
                onEvent(FFBattleEvent.BardSong(hero.id, songRes, effectRes))

                val amounts = mutableMapOf<String, Int>()
                allies.filter { it.isAlive }.forEach { ally ->
                    val healAmt = (ally.maxHp * healPct + hero.magic * 1.2f).toInt()
                    ally.currentHp = minOf(ally.currentHp + healAmt, ally.maxHp)
                    amounts[ally.id] = healAmt
                }
                if (amounts.isNotEmpty()) {
                    onEvent(FFBattleEvent.GroupHeal(hero.id, amounts))
                }
            }
            HeroClass.SAMURAI -> {
                if (masteryLvl >= 25) {
                    enemies.filter { it.currentHp > 0 }.forEach { enemy ->
                        val dmg = (hero.attack * 2.5f).toInt()
                        applyDamageToEnemy(hero, enemy, dmg, false, true, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                } else if (masteryLvl >= 10) {
                    enemies.filter { it.currentHp > 0 }.forEach { enemy ->
                        val dmg = (hero.attack * 2.0f).toInt()
                        applyDamageToEnemy(hero, enemy, dmg, false, true, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, _) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.5f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, true, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                }
            }
            HeroClass.MIME -> {
                val lastAbility = getLastAbility()
                if (lastAbility != null) {
                    val mimickedHero = hero.copy(heroClass = lastAbility)
                    executeJobAbility(mimickedHero, allies, enemies, dimension, relicBonuses, levelUpTracker, getLastAbility, setLastAbility, onLootCollected, onEvent)
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                        applyDamageToEnemy(hero, target, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                }
            }
            HeroClass.NECROMANCER -> {
                val targetEnemies = if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else listOfNotNull(enemies.filter { it.currentHp > 0 }.firstOrNull())
                val mult = when { masteryLvl >= 25 -> 2.2f; masteryLvl >= 10 -> 1.8f; else -> 1.4f }
                targetEnemies.forEach { target ->
                    val (baseDmg, isCrit) = calcMagicDamage(hero, target)
                    val dmg = (baseDmg * mult).toInt()
                    applyDamageToEnemy(hero, target, dmg, true, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)

                    val healAmt = (dmg * 0.5f).toInt()
                    if (masteryLvl >= 10) {
                        val amounts = mutableMapOf<String, Int>()
                        allies.filter { it.isAlive }.forEach { ally ->
                            ally.currentHp = minOf(ally.currentHp + healAmt, ally.maxHp)
                            amounts[ally.id] = healAmt
                        }
                        if (amounts.isNotEmpty()) {
                            onEvent(FFBattleEvent.GroupHeal(hero.id, amounts))
                        }
                    } else {
                        hero.currentHp = minOf(hero.currentHp + healAmt, hero.maxHp)
                        onEvent(FFBattleEvent.HealCast(hero.id, hero.id, healAmt))
                    }
                }
            }
            HeroClass.BLUE_MAGE -> {
                if (masteryLvl >= 25) {
                    enemies.filter { it.currentHp > 0 }.forEach { target ->
                        val (baseDmg, isCrit) = calcMagicDamage(hero, target)
                        val dmg = (baseDmg * 2.2f).toInt()
                        applyDamageToEnemy(hero, target, dmg, true, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                    val amounts = mutableMapOf<String, Int>()
                    allies.filter { it.isAlive }.forEach { ally ->
                        val healAmt = (hero.currentHp * 0.35f + hero.magic * 1.5f).toInt()
                        ally.currentHp = minOf(ally.currentHp + healAmt, ally.maxHp)
                        amounts[ally.id] = healAmt
                    }
                    if (amounts.isNotEmpty()) {
                        onEvent(FFBattleEvent.GroupHeal(hero.id, amounts))
                    }
                } else if (masteryLvl >= 10) {
                    val amounts = mutableMapOf<String, Int>()
                    allies.filter { it.isAlive }.forEach { ally ->
                        val healAmt = (hero.currentHp * 0.40f + 20).toInt()
                        ally.currentHp = minOf(ally.currentHp + healAmt, ally.maxHp)
                        amounts[ally.id] = healAmt
                    }
                    if (amounts.isNotEmpty()) {
                        onEvent(FFBattleEvent.GroupHeal(hero.id, amounts))
                    }
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, isCrit) = calcMagicDamage(hero, target)
                        val dmg = (baseDmg * 1.8f).toInt()
                        applyDamageToEnemy(hero, target, dmg, true, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.5f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                }
            }
            HeroClass.ONION_KNIGHT -> {
                if (masteryLvl >= 25) {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, _) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 3.0f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, true, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                } else if (masteryLvl >= 10) {
                    repeat(2) {
                        val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return@repeat
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.5f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.2f).toInt()
                        applyDamageToEnemy(hero, target, dmg, false, isCrit, enemies, dimension, relicBonuses, allies, levelUpTracker, onLootCollected, onEvent)
                    }
                }
            }
        }
    }

    private fun awardExpToParty(heroes: List<Hero>, amount: Int, tracker: MutableSet<String>, onEvent: (FFBattleEvent) -> Unit) {
        heroes.filter { it.isAlive }.forEach { hero ->
            val result = hero.addExperience(amount)
            if (result) tracker.add(hero.id)
            onEvent(FFBattleEvent.ExpGained(hero.id, amount, result, hero.level))
        }
    }

    private fun calcPhysicalDamage(attacker: Hero, target: Enemy): Pair<Int, Boolean> {
        val isCrit = Random.nextInt(100) < attacker.critChance
        val critMult = 1.0f + (attacker.critDamage / 100f)
        val totalAtk = attacker.attack.toFloat()
        val defK = 500f + target.floor * 2.0f
        val mitigation = (target.defense.toFloat() / (target.defense.toFloat() + defK)).coerceAtMost(0.85f)
        val baseDmg = maxOf(1f, totalAtk * (1f - mitigation) + Random.nextInt(-2, 3))
        val finalDmg = if (isCrit) (baseDmg * critMult).toInt() else baseDmg.toInt()
        return finalDmg to isCrit
    }

    private fun calcMagicDamage(attacker: Hero, target: Enemy): Pair<Int, Boolean> {
        val isCrit = Random.nextInt(100) < (attacker.critChance / 2)
        val critMult = 1.0f + (attacker.critDamage / 100f)
        val totalMag = attacker.magic.toFloat() * 1.5f
        val mdefK = 400f + target.floor * 1.5f
        val mitigation = (target.magicDefense.toFloat() / (target.magicDefense.toFloat() + mdefK)).coerceAtMost(0.85f)
        val baseDmg = maxOf(1f, totalMag * (1f - mitigation) + Random.nextInt(-2, 3))
        val finalDmg = if (isCrit) (baseDmg * critMult).toInt() else baseDmg.toInt()
        return finalDmg to isCrit
    }

    private fun executeEnemyTurn(enemy: Enemy, heroes: MutableList<Hero>, onEvent: (FFBattleEvent) -> Unit) {
        val aliveHeroes = heroes.filter { it.isAlive }
        if (aliveHeroes.isEmpty()) return

        // Aggro calculation: ensures zero-defense heroes maintain a minimum weight of 1
        val totalDefense = aliveHeroes.sumOf { maxOf(1, it.defense) }
        var roll = Random.nextInt(totalDefense)

        var target = aliveHeroes.last()
        for (hero in aliveHeroes) {
            val weight = maxOf(1, hero.defense)
            if (roll < weight) {
                target = hero
                break
            }
            roll -= weight
        }

        val k = 150f + enemy.floor * 0.8f
        val mitigation = (target.defense.toFloat() / (target.defense.toFloat() + k)).coerceAtMost(0.85f)
        
        val isCrit = Random.nextInt(100) < (if (enemy.isBoss) 20 else if (enemy.floor >= 200) 10 else 0)
        val critMult = if (isCrit) 1.5f else 1.0f

        val rawDmg = maxOf(1f, (enemy.attack.toFloat() + Random.nextInt(-2, 3)) * critMult)
        val dmg = maxOf(1, (rawDmg * (1f - mitigation)).toInt())
        target.currentHp -= dmg
        onEvent(FFBattleEvent.DamageDealt(enemy.id, target.id, dmg, false, isCrit))
        if (target.currentHp <= 0) {
            target.currentHp = 0
            onEvent(FFBattleEvent.HeroFell(target.id, target.name, target.heroClass))
        }
    }
}
