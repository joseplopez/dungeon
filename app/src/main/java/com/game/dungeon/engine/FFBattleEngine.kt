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
            // Spawn enemies for this floor
            val bossTemplate = FFDimensionData.getBossForFloor(dimension, currentFloor)
            val enemies = if (bossTemplate != null) {
                mutableListOf(Enemy.fromTemplate(bossTemplate, currentFloor, context, relicBonuses = relicBonuses,  dimension = dimension))
            } else {
                spawnEnemies(dimension, currentFloor, context, relicBonuses).toMutableList()
            }
            
            onEvent(FFBattleEvent.FloorStart(currentFloor, enemies))

            // Floor battle loop
            while (enemies.any { it.currentHp > 0 } && aliveHeroes.any { it.isAlive }) {
                // Build turn order — sort by speed descending
                val turnOrder = (aliveHeroes.filter { it.isAlive } as List<Any> + enemies.filter { it.currentHp > 0 })
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
                            if (actor.isAlive) executeHeroTurn(actor, aliveHeroes, enemies, dimension, relicBonuses, { lastAbilityClass }, { lastAbilityClass = it }, onEvent)
                        }
                        is Enemy -> {
                            if (actor.currentHp > 0) executeEnemyTurn(actor, aliveHeroes, onEvent)
                        }
                    }
                    delay(speed.delayMs)
                }
            }

            // Floor cleared
            if (aliveHeroes.any { it.isAlive }) {
                val gilEarned = (enemies.sumOf { it.gilDropped }.toLong() * relicBonuses.goldMultiplier).toLong()
                val magiciteEarned = enemies.sumOf { it.magiciteDropped }
                
                // Award completion XP
                val completionExp = ((5 + (currentFloor / 2)) * relicBonuses.expMultiplier).toInt()
                val leveledUpHeroIds = mutableListOf<String>()
                aliveHeroes.filter { it.isAlive }.forEach { hero ->
                    val result = hero.addExperience(completionExp)
                    if (result) leveledUpHeroIds.add(hero.id)
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
                    // Double Loot Relic: +5% boss double drop chance per level (logarithmic curve capped at 75%)
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
                
                // CRITICAL: Remove dead heroes so they don't reappear on the next floor or in the event
                aliveHeroes.removeAll { !it.isAlive }

                onEvent(FFBattleEvent.FloorComplete(currentFloor, gilEarned, magiciteEarned, itemsFound, aliveHeroes.map { it.copy() }, leveledUpHeroIds))

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
        onEvent: (FFBattleEvent) -> Unit
    ) {
        val exp = ((enemy.floor * 2) * relicBonuses.expMultiplier).toInt()
        val materials = calculateMaterialDrops(enemy, enemy.floor, dimension, relicBonuses)
        onEvent(FFBattleEvent.EnemyDefeated(enemy.id, enemy.type, enemy.gilDropped, enemy.magiciteDropped, exp, materials))
        materials.forEach { (matId, amt) ->
            onEvent(FFBattleEvent.MaterialDropped(matId, amt))
        }
        awardExpToParty(allies, exp, onEvent)
    }

    internal fun calculateMaterialDrops(
        enemy: Enemy,
        floor: Int,
        dimension: FFDimension,
        relicBonuses: RelicBonuses
    ): Map<String, Int> {
        val drops = mutableMapOf<String, Int>()
        val relicMultiplier = 1f + relicBonuses.alchemistDropChanceBonus + (relicBonuses.magnetBonus * 0.05f)

        // Uses enemy.isBoss strictly (removes the floor % 10 == 0 bug)
        val dropRules = MonsterLootTable.getDropsForMonster(enemy.type, enemy.isBoss)

        dropRules.forEach { rule ->
            val rawChance = rule.baseDropChance * relicMultiplier
            val maxCap = if (rule.isBossTrophy) 0.50f else 0.20f
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

    internal fun selectMaterialForRegularEnemy(monsterType: MonsterType, floor: Int): String {
        val name = monsterType.name

        val isPartType = name.contains("WOLF") || name.contains("BEHEMOTH") || name.contains("RAT") ||
                name.contains("DRAGON") || name.contains("SNAKE") || name.contains("BEAST") ||
                name.contains("FANG") || name.contains("ANTLION") || name.contains("COCKATRICE") ||
                name.contains("TOAD") || name.contains("SAHAGIN") || name.contains("BUG")

        val isEssenceType = name.contains("BOMB") || name.contains("FLAN") || name.contains("SLIME") ||
                name.contains("IMP") || name.contains("MAGIC") || name.contains("MAGE") ||
                name.contains("STOKER") || name.contains("DARK") || name.contains("MIST") ||
                name.contains("ELEMENT") || name.contains("CYCLONE")

        return when {
            isPartType -> {
                when {
                    floor >= 60 -> if (Random.nextBoolean()) "dragon_scale" else "demon_horn"
                    floor >= 25 -> if (Random.nextBoolean()) "demon_horn" else "beast_fang"
                    else -> if (Random.nextBoolean()) "beast_fang" else "monster_bone"
                }
            }
            isEssenceType -> {
                when {
                    name.contains("FIRE") || name.contains("BOMB") || name.contains("RED") || name.contains("STOKER") -> "fire_essence"
                    name.contains("ICE") || name.contains("WATER") || name.contains("BLUE") -> "ice_essence"
                    name.contains("LIGHTNING") || name.contains("THUNDER") || name.contains("ELEC") -> "lightning_essence"
                    name.contains("DARK") || floor >= 50 -> "dark_essence"
                    else -> listOf("fire_essence", "ice_essence", "lightning_essence").random()
                }
            }
            else -> {
                when {
                    floor >= 90 -> "orichalcum_ore"
                    floor >= 50 -> "adamantite_ore"
                    floor >= 20 -> "mithril_ore"
                    else -> "iron_ore"
                }
            }
        }
    }

    private fun executeHeroTurn(hero: Hero, allies: List<Hero>, enemies: MutableList<Enemy>, dimension: FFDimension, relicBonuses: RelicBonuses, getLastAbility: () -> HeroClass?, setLastAbility: (HeroClass) -> Unit, onEvent: (FFBattleEvent)->Unit) {
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
            executeJobAbility(hero, allies, enemies, dimension, relicBonuses, getLastAbility, setLastAbility, onEvent)
            return
        }

        // Normal turn based on AIPriority
        when (hero.aiPriority) {
            AIPriority.ATTACK -> {
                val target = enemies.filter { it.currentHp > 0 }.minByOrNull { it.currentHp } ?: return
                val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                target.currentHp -= dmg
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, isCrit))
                
                if (target.currentHp <= 0) {
                    handleEnemyDefeated(target, dimension, relicBonuses, allies, onEvent)
                }
            }
            AIPriority.MAGIC -> {
                if (masteryLvl >= 1) {
                    val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp } ?: return
                    val (dmg, isCrit) = calcMagicDamage(hero, target)
                    target.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, true, isCrit))
                    
                    if (target.currentHp <= 0) {
                        handleEnemyDefeated(target, dimension, relicBonuses, allies, onEvent)
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.minByOrNull { it.currentHp } ?: return
                    val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                    target.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, isCrit))
                    if (target.currentHp <= 0) {
                        handleEnemyDefeated(target, dimension, relicBonuses, allies, onEvent)
                    }
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
                    // No heal unlocked or no one to heal — attack instead
                    val target = enemies.filter { it.currentHp > 0 }.minByOrNull { it.currentHp } ?: return
                    val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                    target.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, isCrit))
                    if (target.currentHp <= 0) {
                        handleEnemyDefeated(target, dimension, relicBonuses, allies, onEvent)
                    }
                }
            }
            AIPriority.DEFEND -> {
                val target = allies.filter { it.isAlive }.minByOrNull { it.currentHp }
                if (masteryLvl >= 1 && target != null && target.id != hero.id) {
                    onEvent(FFBattleEvent.AbilityUsed(hero.id, R.string.ability_cover_name, R.string.ability_cover_desc, listOf(target.name)))
                } else {
                    val enemyTarget = enemies.filter { it.currentHp > 0 }.minByOrNull { it.currentHp } ?: return
                    val (dmg, isCrit) = calcPhysicalDamage(hero, enemyTarget)
                    enemyTarget.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, enemyTarget.id, dmg, false, isCrit))
                    if (enemyTarget.currentHp <= 0) {
                        handleEnemyDefeated(enemyTarget, dimension, relicBonuses, allies, onEvent)
                    }
                }
            }
        }
    }

    private fun executeJobAbility(hero: Hero, allies: List<Hero>, enemies: MutableList<Enemy>, dimension: FFDimension, relicBonuses: RelicBonuses, getLastAbility: () -> HeroClass?, setLastAbility: (HeroClass) -> Unit, onEvent: (FFBattleEvent)->Unit) {
        if (hero.heroClass != HeroClass.MIME) {
            setLastAbility(hero.heroClass)
        }
        val masteryLvl = relicBonuses.getMasteryLevel(hero.heroClass)
        val activeSkill =
            JobAbilityData.getActiveAbilityForLevel(hero.heroClass, masteryLvl) ?: return
        onEvent(FFBattleEvent.AbilityUsed(hero.id, activeSkill.nameRes, activeSkill.descRes, listOf(hero.name)))

        when (hero.heroClass) {
            HeroClass.FREELANCER -> {
                val healAmt = (hero.maxHp * 0.25f + hero.attack * 0.5f).toInt()
                hero.currentHp = minOf(hero.currentHp + healAmt, hero.maxHp)
                onEvent(FFBattleEvent.HealCast(hero.id, hero.id, healAmt))
            }
            HeroClass.WARRIOR -> {
                val targetEnemies = if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else listOfNotNull(enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp })
                val mult = when { masteryLvl >= 25 -> 2.2f; masteryLvl >= 10 -> 1.8f; else -> 1.5f }
                targetEnemies.forEach { target ->
                    val (baseDmg, _) = calcPhysicalDamage(hero, target)
                    val dmg = (baseDmg * mult).toInt()
                    target.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, true))
                    if (target.currentHp <= 0) {
                        handleEnemyDefeated(target, dimension, relicBonuses, allies, onEvent)
                    }
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
                    onEvent(FFBattleEvent.GroupHeal(hero.id, amounts))
                } else {
                    val target = allies.filter { it.isAlive }.minByOrNull { it.currentHp.toFloat() / it.maxHp } ?: hero
                    val healMult = if (masteryLvl >= 10) 2.8f else 2.0f
                    val heal = (hero.magic * healMult + target.maxHp * 0.30f + 30).toInt()
                    target.currentHp = minOf(target.currentHp + heal, target.maxHp)
                    onEvent(FFBattleEvent.HealCast(hero.id, target.id, heal))
                }
            }
            HeroClass.BLACK_MAGE -> {
                val targetEnemies = if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else if (masteryLvl >= 10) enemies.filter { it.currentHp > 0 }.take(2) else listOfNotNull(enemies.filter { it.currentHp > 0 }.firstOrNull())
                val mult = when { masteryLvl >= 25 -> 2.2f; masteryLvl >= 10 -> 1.8f; else -> 1.4f }
                targetEnemies.forEach { enemy ->
                    val (baseDmg, _) = calcMagicDamage(hero, enemy)
                    val dmg = (baseDmg * mult).toInt()
                    enemy.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, enemy.id, dmg, true, true))
                    if (enemy.currentHp <= 0) {
                        handleEnemyDefeated(enemy, dimension, relicBonuses, allies, onEvent)
                    }
                }
            }
            HeroClass.THIEF -> {
                val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return
                val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                val mult = when { masteryLvl >= 25 -> 2.0f; masteryLvl >= 10 -> 1.5f; else -> 1.2f }
                val finalDmg = (dmg * mult).toInt()
                target.currentHp -= finalDmg
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, finalDmg, false, isCrit))
                if (target.magiciteDropped > 0 && (hero.hasRansack || masteryLvl >= 10)) {
                    onEvent(FFBattleEvent.MagiciteStolen(hero.id, target.magiciteDropped))
                    target.magiciteDropped = 0 // Prevents double-stealing / double-counting
                }
                if (target.currentHp <= 0) {
                    handleEnemyDefeated(target, dimension, relicBonuses, allies, onEvent)
                }
            }
            HeroClass.MONK -> {
                val selfHeal = (hero.maxHp * 0.35f + hero.magic * 1.5f).toInt()
                hero.currentHp = minOf(hero.currentHp + selfHeal, hero.maxHp)
                onEvent(FFBattleEvent.HealCast(hero.id, hero.id, selfHeal))
                val targetEnemies = if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else listOfNotNull(enemies.filter { it.currentHp > 0 }.firstOrNull())
                val mult = when { masteryLvl >= 25 -> 2.0f; masteryLvl >= 10 -> 1.8f; else -> 1.4f }
                targetEnemies.forEach { target ->
                    val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                    val dmg = (baseDmg * mult).toInt()
                    target.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, isCrit))
                    if (target.currentHp <= 0) {
                        handleEnemyDefeated(target, dimension, relicBonuses, allies, onEvent)
                    }
                }
            }
            HeroClass.KNIGHT -> {
                val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp } ?: return
                val (baseDmg, _) = calcPhysicalDamage(hero, target)
                val mult = when { masteryLvl >= 25 -> 2.2f; masteryLvl >= 10 -> 1.8f; else -> 1.5f }
                val dmg = (baseDmg * mult).toInt()
                target.currentHp -= dmg
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, true))
                if (target.currentHp <= 0) {
                    handleEnemyDefeated(target, dimension, relicBonuses, allies, onEvent)
                }
            }
            HeroClass.PALADIN -> {
                val targetEnemies = if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else listOfNotNull(enemies.filter { it.currentHp > 0 }.firstOrNull())
                targetEnemies.forEach { enemy ->
                    val (dmg, isCrit) = calcPhysicalDamage(hero, enemy)
                    val mult = when { masteryLvl >= 25 -> 2.0f; masteryLvl >= 10 -> 1.6f; else -> 1.3f }
                    val finalDmg = (dmg * mult).toInt()
                    enemy.currentHp -= finalDmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, enemy.id, finalDmg, false, isCrit))
                    if (enemy.currentHp <= 0) {
                        handleEnemyDefeated(enemy, dimension, relicBonuses, allies, onEvent)
                    }
                }
                val amounts = mutableMapOf<String, Int>()
                allies.filter { it.isAlive }.forEach { ally ->
                    val healAmt = (hero.magic * 1.8f + ally.maxHp * (if (masteryLvl >= 25) 0.35f else 0.20f)).toInt()
                    ally.currentHp = minOf(ally.currentHp + healAmt, ally.maxHp)
                    amounts[ally.id] = healAmt
                }
                if (amounts.isNotEmpty()) {
                    onEvent(FFBattleEvent.GroupHeal(hero.id, amounts))
                }
            }
            HeroClass.RED_MAGE -> {
                val hits = if (masteryLvl >= 25) 3 else 2
                repeat(hits) {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return@repeat
                    val (dmg, isCrit) = calcMagicDamage(hero, target)
                    target.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, true, isCrit))
                    if (target.currentHp <= 0) {
                        handleEnemyDefeated(target, dimension, relicBonuses, allies, onEvent)
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
                    enemy.currentHp -= dmg
                    totalDmg += dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, enemy.id, dmg, true, isCrit || mult > 2f))
                    if (enemy.currentHp <= 0) {
                        handleEnemyDefeated(enemy, dimension, relicBonuses, allies, onEvent)
                    }
                }
                onEvent(FFBattleEvent.SummonUsed(hero.id, name, totalDmg))
            }
            HeroClass.NINJA -> {
                val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp } ?: return
                val mult = when { masteryLvl >= 25 -> 2.8f; masteryLvl >= 10 -> 2.2f; else -> 1.8f }
                val dmg = (hero.attack * mult).toInt()
                target.currentHp -= dmg
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, true))
                if (target.currentHp <= 0) {
                    handleEnemyDefeated(target, dimension, relicBonuses, allies, onEvent)
                }
            }
            HeroClass.DRAGOON -> {
                val targetEnemies = if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else listOfNotNull(enemies.filter { it.currentHp > 0 }.firstOrNull())
                val mult = when { masteryLvl >= 25 -> 2.5f; masteryLvl >= 10 -> 2.0f; else -> 1.6f }
                targetEnemies.forEach { target ->
                    val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                    val dmg = (baseDmg * mult).toInt()
                    target.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, isCrit))
                    if (target.currentHp <= 0) {
                        handleEnemyDefeated(target, dimension, relicBonuses, allies, onEvent)
                    }
                }
            }
            HeroClass.BARD -> {
                val (songRes, effectRes) = when {
                    masteryLvl >= 25 -> R.string.song_ballad to R.string.song_ballad_effect
                    masteryLvl >= 10 -> R.string.song_minuet to R.string.song_minuet_effect
                    else -> R.string.song_paeon to R.string.song_paeon_effect
                }
                onEvent(FFBattleEvent.BardSong(hero.id, songRes, effectRes))
            }
            HeroClass.SAMURAI -> {
                val mult = when { masteryLvl >= 25 -> 2.5f; masteryLvl >= 10 -> 2.0f; else -> 1.5f }
                val dmg = (hero.attack * mult).toInt()
                enemies.filter { it.currentHp > 0 }.forEach { enemy ->
                    enemy.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, enemy.id, dmg, false, true))
                    if (enemy.currentHp <= 0) {
                        handleEnemyDefeated(enemy, dimension, relicBonuses, allies, onEvent)
                    }
                }
            }
            HeroClass.MIME -> {
                val lastAbility = getLastAbility()
                if (lastAbility != null) {
                    executeJobAbility(hero.copy(heroClass = lastAbility), allies, enemies, dimension, relicBonuses, getLastAbility, setLastAbility, onEvent)
                }
            }
            HeroClass.NECROMANCER -> {
                val targetEnemies = if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else listOfNotNull(enemies.filter { it.currentHp > 0 }.firstOrNull())
                targetEnemies.forEach { target ->
                    val (baseDmg, isCrit) = calcMagicDamage(hero, target)
                    val mult = when { masteryLvl >= 25 -> 2.2f; masteryLvl >= 10 -> 1.8f; else -> 1.4f }
                    val dmg = (baseDmg * mult).toInt()
                    target.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, true, isCrit))
                    val healAmt = (dmg * 0.5f).toInt()
                    val amounts = mutableMapOf<String, Int>()
                    allies.filter { it.isAlive }.forEach { ally ->
                        ally.currentHp = minOf(ally.currentHp + healAmt, ally.maxHp)
                        amounts[ally.id] = healAmt
                    }
                    onEvent(FFBattleEvent.GroupHeal(hero.id, amounts))
                    if (target.currentHp <= 0) {
                        handleEnemyDefeated(target, dimension, relicBonuses, allies, onEvent)
                    }
                }
            }
            HeroClass.BLUE_MAGE -> {
                val targetEnemies = if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else listOfNotNull(enemies.filter { it.currentHp > 0 }.firstOrNull())
                val mult = when { masteryLvl >= 25 -> 2.2f; masteryLvl >= 10 -> 1.8f; else -> 1.5f }
                targetEnemies.forEach { target ->
                    val (baseDmg, isCrit) = calcMagicDamage(hero, target)
                    val dmg = (baseDmg * mult).toInt()
                    target.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, true, isCrit))
                    if (target.currentHp <= 0) {
                        handleEnemyDefeated(target, dimension, relicBonuses, allies, onEvent)
                    }
                }
            }
            HeroClass.ONION_KNIGHT -> {
                val mult = when { masteryLvl >= 25 -> 3.0f; masteryLvl >= 10 -> 2.0f; else -> 1.2f }
                val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return
                val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                val dmg = (baseDmg * mult).toInt()
                target.currentHp -= dmg
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, isCrit))
                if (target.currentHp <= 0) {
                    handleEnemyDefeated(target, dimension, relicBonuses, allies, onEvent)
                }
            }
        }
    }

    private fun awardExpToParty(heroes: List<Hero>, amount: Int, onEvent: (FFBattleEvent) -> Unit) {
        heroes.filter { it.isAlive }.forEach { hero ->
            val result = hero.addExperience(amount)
            onEvent(FFBattleEvent.ExpGained(hero.id, amount, result, hero.level))
        }
    }

    private fun calcPhysicalDamage(attacker: Hero, target: Enemy): Pair<Int, Boolean> {
        val isCrit = Random.nextInt(100) < attacker.critChance
        val critMult = 1.0f + (attacker.critDamage / 100f)
        val totalAtk = attacker.attack
        val baseDmg = maxOf(1, totalAtk - target.defense + Random.nextInt(-3, 4))
        val finalDmg = if (isCrit) (baseDmg * critMult).toInt() else baseDmg
        return finalDmg to isCrit
    }

    private fun calcMagicDamage(attacker: Hero, target: Enemy): Pair<Int, Boolean> {
        val isCrit = Random.nextInt(100) < (attacker.critChance / 2) // Magic crit is half as likely but possible
        val critMult = 1.0f + (attacker.critDamage / 100f)
        val totalMag = attacker.magic
        val baseDmg = maxOf(1, (totalMag * 1.5f - target.magicDefense * 0.5f + Random.nextInt(-2, 3)).toInt())
        val finalDmg = if (isCrit) (baseDmg * critMult).toInt() else baseDmg
        return finalDmg to isCrit
    }

    private fun executeEnemyTurn(enemy: Enemy, heroes: MutableList<Hero>, onEvent: (FFBattleEvent)->Unit) {
        val aliveHeroes = heroes.filter { it.isAlive }
        if (aliveHeroes.isEmpty()) return

        // Option 3: Weighted Random (Bias towards highest Defense)
        // We assign weights based on defense. Higher defense = more likely to be hit.
        val totalDefense = aliveHeroes.sumOf { it.defense }.coerceAtLeast(1)
        var roll = Random.nextInt(totalDefense)
        
        var target = aliveHeroes.last() // Fallback
        for (hero in aliveHeroes) {
            val hDef = hero.defense
            if (roll < hDef) {
                target = hero
                break
            }
            roll -= hDef
        }

        val k = 150f + enemy.floor * 0.8f
        val mitigation = (target.defense / (target.defense + k)).coerceAtMost(0.85f)
        val rawDmg = maxOf(1f, enemy.attack.toFloat() + Random.nextInt(-2, 3))
        val dmg = maxOf(1, (rawDmg * (1f - mitigation)).toInt())
        target.currentHp -= dmg
        onEvent(FFBattleEvent.DamageDealt(enemy.id, target.id, dmg, false, false))
        if (target.currentHp <= 0) {
            target.currentHp = 0
            onEvent(FFBattleEvent.HeroFell(target.id, target.name, target.heroClass))
        }
    }
}
