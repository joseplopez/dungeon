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
    data class EnemyDefeated(val enemyId: String, val gilDropped: Int, val magiciteDropped: Int, val expDropped: Int) : FFBattleEvent()
    data class HeroFell(val heroId: String, val heroName: String, val heroClass: HeroClass) : FFBattleEvent()
    data class MagiciteStolen(val heroId: String, val amount: Int) : FFBattleEvent()
    data class SummonUsed(val heroId: String, val summonName: String, val totalDamage: Int) : FFBattleEvent()
    data class BardSong(val heroId: String, @StringRes val songRes: Int, @StringRes val effectRes: Int) : FFBattleEvent()
    data class FloorComplete(val floor: Int, val gilEarned: Long, val magiciteEarned: Int, val itemsFound: List<Item>, val updatedHeroes: List<Hero>, val leveledUpHeroIds: List<String> = emptyList()) : FFBattleEvent()
    data class BossDefeated(val bossName: String, val dimensionComplete: Boolean) : FFBattleEvent()
    object AllHeroesFell : FFBattleEvent()
}

class FFBattleEngine(private val context: Context) {
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
        var lastAbilityClass: HeroClass? = null

        while (aliveHeroes.any { it.isAlive } && currentFloor <= 100) {
            // Spawn enemies for this floor
            val bossTemplate = FFDimensionData.getBossForFloor(dimension, currentFloor)
            val enemies = if (bossTemplate != null) {
                mutableListOf(Enemy.fromTemplate(bossTemplate, currentFloor, context, relicBonuses = relicBonuses))
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
                    if (!enemies.any { it.currentHp > 0 } || !aliveHeroes.any { it.isAlive }) break
                    
                    when (actor) {
                        is Hero -> {
                            if (actor.isAlive) executeHeroTurn(actor, aliveHeroes, enemies, relicBonuses, { lastAbilityClass }, { lastAbilityClass = it }, onEvent)
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
                val baseDropChance = if (bossTemplate != null) 100f else 10f
                val finalDropChance = baseDropChance * (1f + relicBonuses.petItemFindBonus)
                
                if (Random.nextFloat() * 100 < finalDropChance) {
                    itemsFound.add(
                        Item.random(
                            floor = currentFloor, 
                            context = context,
                            relicBonuses = relicBonuses,
                            minRarity = if (bossTemplate != null) Rarity.RARE else Rarity.COMMON
                        )
                    )
                    // Double Loot Relic: +5% boss double drop chance per level
                    if (bossTemplate != null && Random.nextInt(100) < relicBonuses.doubleLootChance) {
                        itemsFound.add(
                            Item.random(
                                floor = currentFloor, 
                                context = context,
                                relicBonuses = relicBonuses,
                                minRarity = Rarity.RARE
                            )
                        )
                    }
                }

                totalGil += gilEarned
                totalMagicite += magiciteEarned
                
                if (bossTemplate != null) {
                    onEvent(FFBattleEvent.BossDefeated(context.getString(bossTemplate.nameRes), currentFloor >= 100))
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
        return List(count) { Enemy.fromTemplate(templates.random(), floor, context, relicBonuses = relicBonuses) }
    }

    private fun executeHeroTurn(hero: Hero, allies: List<Hero>, enemies: MutableList<Enemy>, relicBonuses: RelicBonuses, getLastAbility: () -> HeroClass?, setLastAbility: (HeroClass) -> Unit, onEvent: (FFBattleEvent)->Unit) {
        onEvent(FFBattleEvent.TurnStart(hero.id, hero.name))
        hero.abilityCharge++
        val useAbility = hero.abilityCharge >= 3

        if (useAbility) {
            hero.abilityCharge = 0
            executeJobAbility(hero, allies, enemies, relicBonuses, getLastAbility, setLastAbility, onEvent)
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
                    val exp = ((target.floor * 2) * relicBonuses.expMultiplier).toInt()
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, exp))
                    awardExpToParty(allies, exp, onEvent)
                }
            }
            AIPriority.MAGIC -> {
                val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp } ?: return
                val (dmg, isCrit) = calcMagicDamage(hero, target)
                target.currentHp -= dmg
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, true, isCrit))
                
                if (target.currentHp <= 0) {
                    val exp = ((target.floor * 2) * relicBonuses.expMultiplier).toInt()
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, exp))
                    awardExpToParty(allies, exp, onEvent)
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
                    val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                    target.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, isCrit))
                    if (target.currentHp <= 0) {
                        val exp = ((target.floor * 2) * relicBonuses.expMultiplier).toInt()
                        onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, exp))
                        awardExpToParty(allies, exp, onEvent)
                    }
                }
            }
            AIPriority.DEFEND -> {
                val target = allies.filter { it.isAlive }.minByOrNull { it.currentHp }
                if (target != null && target.id != hero.id) {
                    onEvent(FFBattleEvent.AbilityUsed(hero.id, R.string.ability_cover_name, R.string.ability_cover_desc, listOf(target.name)))
                }
            }
        }
    }

    private fun executeJobAbility(hero: Hero, allies: List<Hero>, enemies: MutableList<Enemy>, relicBonuses: RelicBonuses, getLastAbility: () -> HeroClass?, setLastAbility: (HeroClass) -> Unit, onEvent: (FFBattleEvent)->Unit) {
        if (hero.heroClass != HeroClass.MIME) {
            setLastAbility(hero.heroClass)
        }
        when (hero.heroClass) {
            HeroClass.WARRIOR -> {
                val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp } ?: return
                val (baseDmg, _) = calcPhysicalDamage(hero, target)
                val dmg = (baseDmg * 2f).toInt()
                target.currentHp -= dmg
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, true))
                if (target.currentHp <= 0) {
                    val exp = ((target.floor * 2) * relicBonuses.expMultiplier).toInt()
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, exp))
                    awardExpToParty(allies, exp, onEvent)
                }
            }
            HeroClass.WHITE_MAGE -> {
                val amounts = mutableMapOf<String, Int>()
                allies.filter { it.isAlive }.forEach { ally ->
                    val heal = (hero.magic * 3f + 40).toInt()
                    ally.currentHp = minOf(ally.currentHp + heal, ally.maxHp)
                    amounts[ally.id] = heal
                }
                onEvent(FFBattleEvent.AbilityUsed(hero.id, R.string.ability_curaga_name, R.string.ability_curaga_desc, listOf(hero.name)))
                onEvent(FFBattleEvent.GroupHeal(hero.id, amounts))
            }
            HeroClass.BLACK_MAGE -> {
                enemies.filter { it.currentHp > 0 }.forEach { enemy ->
                    val (baseDmg, _) = calcMagicDamage(hero, enemy)
                    val dmg = (baseDmg * 1.8f).toInt()
                    enemy.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, enemy.id, dmg, true, true))
                    if (enemy.currentHp <= 0) {
                        val exp = ((enemy.floor * 2) * relicBonuses.expMultiplier).toInt()
                        onEvent(FFBattleEvent.EnemyDefeated(enemy.id, enemy.gilDropped, enemy.magiciteDropped, exp))
                        awardExpToParty(allies, exp, onEvent)
                    }
                }
            }
            HeroClass.THIEF -> {
                val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return
                val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                target.currentHp -= dmg
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, isCrit))
                if (target.magiciteDropped > 0 && hero.hasRansack) {
                    onEvent(FFBattleEvent.AbilityUsed(hero.id, R.string.ability_ransack_name, R.string.log_magicite_stolen, listOf(hero.name, target.magiciteDropped)))
                    onEvent(FFBattleEvent.MagiciteStolen(hero.id, target.magiciteDropped))
                }
                if (target.currentHp <= 0) {
                    val exp = ((target.floor * 2) * relicBonuses.expMultiplier).toInt()
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, exp))
                    awardExpToParty(allies, exp, onEvent)
                }
            }
            HeroClass.MONK -> {
                val selfHeal = (hero.maxHp * 0.25f).toInt()
                hero.currentHp = minOf(hero.currentHp + selfHeal, hero.maxHp)
                onEvent(FFBattleEvent.HealCast(hero.id, hero.id, selfHeal))
                val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return
                val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                val dmg = (baseDmg * 1.5f).toInt()
                target.currentHp -= dmg
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, isCrit))
                if (target.currentHp <= 0) {
                    val exp = ((target.floor * 2) * relicBonuses.expMultiplier).toInt()
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, exp))
                    awardExpToParty(allies, exp, onEvent)
                }
            }
            HeroClass.KNIGHT -> {
                val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp } ?: return
                val (baseDmg, _) = calcPhysicalDamage(hero, target)
                val dmg = (baseDmg * 1.8f).toInt()
                target.currentHp -= dmg
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, true))
                if (target.currentHp <= 0) {
                    val exp = ((target.floor * 2) * relicBonuses.expMultiplier).toInt()
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, exp))
                    awardExpToParty(allies, exp, onEvent)
                }
            }
            HeroClass.PALADIN -> {
                enemies.filter { it.currentHp > 0 }.forEach { enemy ->
                    val (dmg, isCrit) = calcPhysicalDamage(hero, enemy)
                    enemy.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, enemy.id, dmg, false, isCrit))
                    if (enemy.currentHp <= 0) {
                        val exp = ((enemy.floor * 2) * relicBonuses.expMultiplier).toInt()
                        onEvent(FFBattleEvent.EnemyDefeated(enemy.id, enemy.gilDropped, enemy.magiciteDropped, exp))
                        awardExpToParty(allies, exp, onEvent)
                    }
                }
                val healAmt = (hero.magic * 1.5f).toInt()
                allies.filter { it.isAlive }.forEach { it.currentHp = minOf(it.currentHp + healAmt, it.maxHp) }
            }
            HeroClass.RED_MAGE -> {
                repeat(2) {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return@repeat
                    val (dmg, isCrit) = calcMagicDamage(hero, target)
                    target.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, true, isCrit))
                    if (target.currentHp <= 0) {
                        val exp = ((target.floor * 2) * relicBonuses.expMultiplier).toInt()
                        onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, exp))
                        awardExpToParty(allies, exp, onEvent)
                    }
                }
            }
            HeroClass.SUMMONER -> {
                val summons = listOf(
                    Triple("Ifrit", "🔥", 1.5f), Triple("Shiva", "❄️", 1.5f),
                    Triple("Ramuh", "⚡", 1.5f), Triple("Bahamut", "🐉", 2.5f)
                )
                val (name, _, mult) = summons.random()
                var totalDmg = 0
                enemies.filter { it.currentHp > 0 }.forEach { enemy ->
                    val (baseDmg, isCrit) = calcMagicDamage(hero, enemy)
                    val dmg = (baseDmg * mult).toInt()
                    enemy.currentHp -= dmg
                    totalDmg += dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, enemy.id, dmg, true, isCrit || mult > 2f))
                    if (enemy.currentHp <= 0) {
                        val exp = ((enemy.floor * 2) * relicBonuses.expMultiplier).toInt()
                        onEvent(FFBattleEvent.EnemyDefeated(enemy.id, enemy.gilDropped, enemy.magiciteDropped, exp))
                        awardExpToParty(allies, exp, onEvent)
                    }
                }
                onEvent(FFBattleEvent.SummonUsed(hero.id, name, totalDmg))
            }
            HeroClass.NINJA -> {
                val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp } ?: return
                val dmg = (hero.attack * 2.2f).toInt()
                target.currentHp -= dmg
                onEvent(FFBattleEvent.DamageDealt(hero.id, target.id, dmg, false, true))
                if (target.currentHp <= 0) {
                    val exp = ((target.floor * 2) * relicBonuses.expMultiplier).toInt()
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, exp))
                    awardExpToParty(allies, exp, onEvent)
                }
            }
            HeroClass.DRAGOON -> {
                onEvent(FFBattleEvent.AbilityUsed(hero.id, R.string.ability_jump_name, R.string.ability_jump_desc, listOf(hero.name)))
            }
            HeroClass.BARD -> {
                val songs = listOf(
                    Triple(R.string.song_paeon, R.string.song_paeon_effect, "Paeon"),
                    Triple(R.string.song_minne, R.string.song_minne_effect, "Minne"),
                    Triple(R.string.song_minuet, R.string.song_minuet_effect, "Minuet"),
                    Triple(R.string.song_ballad, R.string.song_ballad_effect, "Ballad")
                )
                val (songRes, effectRes, songName) = songs.random()
                onEvent(FFBattleEvent.BardSong(hero.id, songRes, effectRes))
                onEvent(FFBattleEvent.AbilityUsed(hero.id, songRes, R.string.log_generic, listOf(songName)))
            }
            HeroClass.SAMURAI -> {
                val dmg = (hero.attack * 2f).toInt()
                enemies.filter { it.currentHp > 0 }.forEach { enemy ->
                    enemy.currentHp -= dmg
                    onEvent(FFBattleEvent.DamageDealt(hero.id, enemy.id, dmg, false, true))
                    if (enemy.currentHp <= 0) {
                        val exp = ((enemy.floor * 2) * relicBonuses.expMultiplier).toInt()
                        onEvent(FFBattleEvent.EnemyDefeated(enemy.id, enemy.gilDropped, enemy.magiciteDropped, exp))
                        awardExpToParty(allies, exp, onEvent)
                    }
                }
                onEvent(FFBattleEvent.AbilityUsed(hero.id, R.string.ability_zeninage_name, R.string.ability_zeninage_desc, listOf(hero.name)))
            }
            HeroClass.MIME -> {
                onEvent(FFBattleEvent.AbilityUsed(hero.id, R.string.ability_mimic_name, R.string.ability_mimic_desc, listOf(hero.name)))
                val lastAbility = getLastAbility()
                if (lastAbility != null) {
                    executeJobAbility(hero.copy(heroClass = lastAbility), allies, enemies, relicBonuses, getLastAbility, setLastAbility, onEvent)
                }
            }
            HeroClass.NECROMANCER -> {
                onEvent(FFBattleEvent.AbilityUsed(hero.id, R.string.ability_souldrain_name, R.string.ability_souldrain_desc, listOf(hero.name)))
                val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return
                val (baseDmg, isCrit) = calcMagicDamage(hero, target)
                val dmg = (baseDmg * 2f).toInt()
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
                    val exp = ((target.floor * 2) * relicBonuses.expMultiplier).toInt()
                    onEvent(FFBattleEvent.EnemyDefeated(target.id, target.gilDropped, target.magiciteDropped, exp))
                    awardExpToParty(allies, exp, onEvent)
                }
            }
            else -> { /* Freelancer: no special ability */ }
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

        val dmg = maxOf(1, enemy.attack - target.defense + Random.nextInt(-2, 3))
        target.currentHp -= dmg
        onEvent(FFBattleEvent.DamageDealt(enemy.id, target.id, dmg, false, false))
        if (target.currentHp <= 0) {
            target.currentHp = 0
            onEvent(FFBattleEvent.HeroFell(target.id, target.name, target.heroClass))
        }
    }
}
