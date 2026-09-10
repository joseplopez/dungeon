package com.game.dungeon.engine

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
    data class AbilityUsed(val heroId: String, val abilityName: String, val description: String) : FFBattleEvent()
    data class ExpGained(val heroId: String, val amount: Int, val leveledUp: Boolean, val newLevel: Int) : FFBattleEvent()
    data class EnemyDefeated(val enemyId: String, val gilDropped: Int, val magiciteDropped: Int, val expDropped: Int) : FFBattleEvent()
    data class HeroFell(val heroId: String, val heroName: String, val jobClass: JobClass) : FFBattleEvent()
    data class MagiciteStolen(val heroId: String, val amount: Int) : FFBattleEvent()
    data class SummonUsed(val heroId: String, val summonName: String, val totalDamage: Int) : FFBattleEvent()
    data class BardSong(val heroId: String, val songName: String, val effect: String) : FFBattleEvent()
    data class FloorComplete(val floor: Int, val gilEarned: Long, val magiciteEarned: Int, val itemsFound: List<Item>, val updatedHeroes: List<Hero>, val leveledUpHeroIds: List<String> = emptyList()) : FFBattleEvent()
    data class BossDefeated(val bossName: String, val dimensionComplete: Boolean) : FFBattleEvent()
    object AllHeroesFell : FFBattleEvent()
}

class FFBattleEngine {
    suspend fun runBattle(
        heroes: List<Hero>,
        dimension: FFDimension,
        startFloor: Int,
        speed: BattleSpeed,
        relicBonuses: RelicBonuses,
        onEvent: (FFBattleEvent) -> Unit
    ) {
        var currentFloor = startFloor
        val aliveHeroes = heroes.map { it.copy() }.toMutableList()
        var totalGil = 0L
        var totalMagicite = 0

        while (aliveHeroes.any { it.isAlive } && currentFloor <= 100) {
            // Spawn enemies for this floor
            val bossTemplate = FFDimensionData.getBossForFloor(dimension, currentFloor)
            val enemies = if (bossTemplate != null) {
                mutableListOf(Enemy.fromTemplate(bossTemplate, currentFloor))
            } else {
                spawnEnemies(dimension, currentFloor, relicBonuses).toMutableList()
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
                    if (!enemies.any { it.currentHp > 0 } || !aliveHeroes.any { it.isAlive }) break
                    when (actor) {
                        is Hero -> executeHeroTurn(actor, aliveHeroes, enemies, onEvent)
                        is Enemy -> executeEnemyTurn(actor, aliveHeroes, onEvent)
                    }
                    delay(speed.delayMs)
                }
            }

            // Floor cleared
            if (aliveHeroes.any { it.isAlive }) {
                val gilEarned = (enemies.sumOf { it.gilDropped }.toLong() * relicBonuses.goldMultiplier).toLong()
                val magiciteEarned = enemies.sumOf { it.magiciteDropped }
                
                // Award completion XP
                val completionExp = 5 + (currentFloor / 2)
                val leveledUpHeroIds = mutableListOf<String>()
                aliveHeroes.filter { it.isAlive }.forEach { hero ->
                    val result = awardExp(hero, completionExp, onEvent)
                    if (result) leveledUpHeroIds.add(hero.id)
                }
                
                // Loot chance
                val itemsFound = mutableListOf<Item>()
                if (Random.nextInt(100) < 10) { // 10% chance for an item
                    itemsFound.add(Item.random(currentFloor))
                }

                totalGil += gilEarned
                totalMagicite += magiciteEarned
                
                if (bossTemplate != null) {
                    onEvent(FFBattleEvent.BossDefeated(bossTemplate.name, currentFloor >= 100))
                }
                
                // CRITICAL: Remove dead heroes so they don't reappear on the next floor or in the event
                aliveHeroes.removeAll { !it.isAlive }

                onEvent(FFBattleEvent.FloorComplete(currentFloor, gilEarned, magiciteEarned, itemsFound, aliveHeroes.map { it.copy() }, leveledUpHeroIds))

                currentFloor++
            }
        }

        if (!aliveHeroes.any { it.isAlive }) onEvent(FFBattleEvent.AllHeroesFell)
    }

    private fun spawnEnemies(dimension: FFDimension, floor: Int, relicBonuses: RelicBonuses): List<Enemy> {
        val templates = FFDimensionData.getEnemiesForFloor(dimension, floor)
        if (templates.isEmpty()) return emptyList()
        val count = Random.nextInt(1, 4)
        return List(count) { Enemy.fromTemplate(templates.random(), floor) }
    }

    private fun executeHeroTurn(hero: Hero, allies: List<Hero>, enemies: MutableList<Enemy>, onEvent: (FFBattleEvent)->Unit) {
        onEvent(FFBattleEvent.TurnStart(hero.id, hero.name))
        hero.abilityCharge++
        val useAbility = hero.abilityCharge >= 3

        if (useAbility) {
            hero.abilityCharge = 0
            executeJobAbility(hero, allies, enemies, onEvent)
            return
        }

        // Normal turn based on AIPriority
        when (hero.aiPriority) {
            AIPriority.ATTACK -> {
                val target = enemies.filter { it.currentHp > 0 }.minByOrNull { it.currentHp } ?: return
                val dmg = calcPhysicalDamage(hero, target)
                target.currentHp -= dmg
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, dmg > hero.attack * 1.5f))
                if (target.currentHp <= 0) {
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, target.floor * 2))
                    awardExpToParty(allies, target.floor * 2, onEvent)
                }
            }
            AIPriority.MAGIC -> {
                val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp } ?: return
                val dmg = calcMagicDamage(hero, target)
                target.currentHp -= dmg
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, true, false))
                if (target.currentHp <= 0) {
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, target.floor * 2))
                    awardExpToParty(allies, target.floor * 2, onEvent)
                }
            }
            AIPriority.HEAL -> {
                val wounded = allies.filter { it.isAlive && it.currentHp < it.maxHp * 0.6f }
                    .minByOrNull { it.currentHp }
                if (wounded != null && hero.currentMp >= 10) {
                    val healAmt = (hero.magic * 2.5f + 20).toInt()
                    wounded.currentHp = minOf(wounded.currentHp + healAmt, wounded.maxHp)
                    hero.currentMp -= 10
                    onEvent(FFBattleEvent.HealCast(hero.id, wounded.id, healAmt))
                } else {
                    // No one to heal — attack instead
                    val target = enemies.filter { it.currentHp > 0 }.minByOrNull { it.currentHp } ?: return
                    val dmg = calcPhysicalDamage(hero, target)
                    target.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, false))
                    if (target.currentHp <= 0) {
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, target.floor * 2))
                    awardExpToParty(allies, target.floor * 2, onEvent)
                }
                }
            }
            AIPriority.DEFEND -> {
                val target = allies.filter { it.isAlive }.minByOrNull { it.currentHp }
                if (target != null && target.id != hero.id) {
                    onEvent(FFBattleEvent.AbilityUsed(hero.id, "Cover", "Protecting ${target.name}!"))
                }
            }
        }
    }

    private fun executeJobAbility(hero: Hero, allies: List<Hero>, enemies: MutableList<Enemy>, onEvent: (FFBattleEvent)->Unit) {
        when (hero.heroClass) {
            JobClass.WARRIOR -> {
                val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp } ?: return
                val dmg = (calcPhysicalDamage(hero, target) * 2f).toInt()
                target.currentHp -= dmg
                onEvent(FFBattleEvent.AbilityUsed(hero.id, "Mighty Strike", "${hero.name} unleashes Mighty Strike!"))
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, true))
                if (target.currentHp <= 0) {
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, target.floor * 2))
                    awardExpToParty(allies, target.floor * 2, onEvent)
                }
            }
            JobClass.WHITE_MAGE -> {
                val amounts = mutableMapOf<String, Int>()
                allies.filter { it.isAlive }.forEach { ally ->
                    val heal = (hero.magic * 3f + 40).toInt()
                    ally.currentHp = minOf(ally.currentHp + heal, ally.maxHp)
                    amounts[ally.id] = heal
                }
                onEvent(FFBattleEvent.AbilityUsed(hero.id, "Curaga", "${hero.name} casts Curaga!"))
                onEvent(FFBattleEvent.GroupHeal(hero.id, amounts))
            }
            JobClass.BLACK_MAGE -> {
                onEvent(FFBattleEvent.AbilityUsed(hero.id, "Flare", "${hero.name} casts FLARE!"))
                enemies.filter { it.currentHp > 0 }.forEach { enemy ->
                    val dmg = (calcMagicDamage(hero, enemy) * 1.8f).toInt()
                    enemy.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, enemy.id, dmg, true, true))
                    if (enemy.currentHp <= 0) {
                        onEvent(FFBattleEvent.EnemyDefeated(enemy.id, enemy.gilDropped, enemy.magiciteDropped, enemy.floor * 2))
                        awardExpToParty(allies, enemy.floor * 2, onEvent)
                    }
                }
            }
            JobClass.THIEF -> {
                val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return
                val dmg = calcPhysicalDamage(hero, target)
                target.currentHp -= dmg
                onEvent(FFBattleEvent.AbilityUsed(hero.id, "Mug", "${hero.name} Mugs the enemy!"))
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, false))
                if (target.magiciteDropped > 0 && hero.hasRansack) {
                    onEvent(FFBattleEvent.MagiciteStolen(hero.id, target.magiciteDropped))
                }
                if (target.currentHp <= 0) {
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, target.floor * 2))
                    awardExpToParty(allies, target.floor * 2, onEvent)
                }
            }
            JobClass.MONK -> {
                val selfHeal = (hero.maxHp * 0.25f).toInt()
                hero.currentHp = minOf(hero.currentHp + selfHeal, hero.maxHp)
                onEvent(FFBattleEvent.HealCast(hero.id, hero.id, selfHeal))
                val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return
                val dmg = (calcPhysicalDamage(hero, target) * 1.5f).toInt()
                target.currentHp -= dmg
                onEvent(FFBattleEvent.AbilityUsed(hero.id, "Chakra", "${hero.name} uses Chakra!"))
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, false))
                if (target.currentHp <= 0) {
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, target.floor * 2))
                    awardExpToParty(allies, target.floor * 2, onEvent)
                }
            }
            JobClass.KNIGHT -> {
                val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp } ?: return
                val dmg = (calcPhysicalDamage(hero, target) * 1.8f).toInt()
                target.currentHp -= dmg
                onEvent(FFBattleEvent.AbilityUsed(hero.id, "Holy Sword", "${hero.name} raises Holy Sword!"))
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, true))
                if (target.currentHp <= 0) {
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, target.floor * 2))
                    awardExpToParty(allies, target.floor * 2, onEvent)
                }
            }
            JobClass.PALADIN -> {
                onEvent(FFBattleEvent.AbilityUsed(hero.id, "Saint's Fall", "${hero.name} calls Saint's Fall!"))
                enemies.filter { it.currentHp > 0 }.forEach { enemy ->
                    val dmg = calcPhysicalDamage(hero, enemy)
                    enemy.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, enemy.id, dmg, false, false))
                    if (enemy.currentHp <= 0) {
                        onEvent(FFBattleEvent.EnemyDefeated(enemy.id, enemy.gilDropped, enemy.magiciteDropped, enemy.floor * 2))
                        awardExpToParty(allies, enemy.floor * 2, onEvent)
                    }
                }
                val healAmt = (hero.magic * 1.5f).toInt()
                allies.filter { it.isAlive }.forEach { it.currentHp = minOf(it.currentHp + healAmt, it.maxHp) }
            }
            JobClass.RED_MAGE -> {
                onEvent(FFBattleEvent.AbilityUsed(hero.id, "Doublecast", "${hero.name} Doublecasts!"))
                repeat(2) {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return
                    val dmg = calcMagicDamage(hero, target)
                    target.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, true, false))
                    if (target.currentHp <= 0) {
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, target.floor * 2))
                    awardExpToParty(allies, target.floor * 2, onEvent)
                }
                }
            }
            JobClass.SUMMONER -> {
                val summons = listOf(
                    Triple("Ifrit", "🔥", 1.5f), Triple("Shiva", "❄️", 1.5f),
                    Triple("Ramuh", "⚡", 1.5f), Triple("Bahamut", "🐉", 2.5f)
                )
                val (name, _, mult) = summons.random()
                var totalDmg = 0
                enemies.filter { it.currentHp > 0 }.forEach { enemy ->
                    val dmg = (calcMagicDamage(hero, enemy) * mult).toInt()
                    enemy.currentHp -= dmg
                    totalDmg += dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, enemy.id, dmg, true, mult > 2f))
                    if (enemy.currentHp <= 0) {
                        onEvent(FFBattleEvent.EnemyDefeated(enemy.id, enemy.gilDropped, enemy.magiciteDropped, enemy.floor * 2))
                        awardExpToParty(allies, enemy.floor * 2, onEvent)
                    }
                }
                onEvent(FFBattleEvent.SummonUsed(hero.id, name, totalDmg))
            }
            JobClass.NINJA -> {
                val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp } ?: return
                val dmg = (hero.attack * 2.2f).toInt()
                target.currentHp -= dmg
                onEvent(FFBattleEvent.AbilityUsed(hero.id, "Throw", "${hero.name} throws a shuriken!"))
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, true))
                if (target.currentHp <= 0) {
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, target.floor * 2))
                    awardExpToParty(allies, target.floor * 2, onEvent)
                }
            }
            JobClass.DRAGOON -> {
                onEvent(FFBattleEvent.AbilityUsed(hero.id, "Jump", "${hero.name} leaps into the air!"))
            }
            JobClass.BARD -> {
                val songs = listOf(
                    "Paeon" to "+HP regen this floor",
                    "Minne" to "+Defense for all allies",
                    "Minuet" to "+Attack for all allies",
                    "Romeo's Ballad" to "All enemies skip next turn"
                )
                val (song, effect) = songs.random()
                onEvent(FFBattleEvent.BardSong(hero.id, song, effect))
                onEvent(FFBattleEvent.AbilityUsed(hero.id, song, "${hero.name} sings $song! $effect"))
            }
            JobClass.SAMURAI -> {
                val dmg = (hero.attack * 2f).toInt()
                enemies.filter { it.currentHp > 0 }.forEach { enemy ->
                    enemy.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, enemy.id, dmg, false, false))
                    if (enemy.currentHp <= 0) {
                        onEvent(FFBattleEvent.EnemyDefeated(enemy.id, enemy.gilDropped, enemy.magiciteDropped, enemy.floor * 2))
                        awardExpToParty(allies, enemy.floor * 2, onEvent)
                    }
                }
                onEvent(FFBattleEvent.AbilityUsed(hero.id, "Zeninage", "${hero.name} throws Gil — Zeninage!"))
            }
            else -> { /* Freelancer: no special ability */ }
        }
    }

    private fun awardExpToParty(heroes: List<Hero>, amount: Int, onEvent: (FFBattleEvent) -> Unit) {
        heroes.filter { it.isAlive }.forEach { awardExp(it, amount, onEvent) }
    }

    private fun awardExp(hero: Hero, amount: Int, onEvent: (FFBattleEvent) -> Unit): Boolean {
        hero.exp += amount
        var leveledUp = false
        while (hero.exp >= hero.expToNextLevel) {
            hero.exp -= hero.expToNextLevel
            hero.level++
            hero.expToNextLevel = (hero.expToNextLevel * 1.5).toInt()
            leveledUp = true
        }
        onEvent(FFBattleEvent.ExpGained(hero.id, amount, leveledUp, hero.level))
        return leveledUp
    }

    private fun calcPhysicalDamage(attacker: Hero, target: Enemy): Int =
        maxOf(1, attacker.attack - target.defense + Random.nextInt(-3, 4))

    private fun calcMagicDamage(attacker: Hero, target: Enemy): Int =
        maxOf(1, (attacker.magic * 1.5f - target.magicDefense * 0.5f + Random.nextInt(-2, 3)).toInt())

    private fun executeEnemyTurn(enemy: Enemy, heroes: MutableList<Hero>, onEvent: (FFBattleEvent)->Unit) {
        val target = heroes.filter { it.isAlive }.minByOrNull { it.currentHp } ?: return
        val dmg = maxOf(1, enemy.attack - target.defense + Random.nextInt(-2, 3))
        target.currentHp -= dmg
        onEvent(FFBattleEvent.DamageDealt(enemy.id, target.id, dmg, false, false))
        if (target.currentHp <= 0) {
            target.currentHp = 0
            onEvent(FFBattleEvent.HeroFell(target.id, target.name, target.heroClass))
        }
    }
}
