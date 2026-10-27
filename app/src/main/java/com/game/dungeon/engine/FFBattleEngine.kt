package com.game.dungeon.engine

import android.content.Context
import androidx.annotation.StringRes
import com.game.dungeon.R
import com.game.dungeon.data.models.*
import kotlinx.coroutines.delay
import java.lang.Math.pow
import kotlin.math.pow
import kotlin.random.Random

sealed class FFBattleEvent {
    data class TurnStart(val unitId: String, val unitName: String) : FFBattleEvent()
    data class FloorStart(val floor: Int, val enemies: List<Enemy>) : FFBattleEvent()
    data class DamageDealt(val attackerId: String, val targetId: String,
                           val damage: Long, val isMagic: Boolean, val isCritical: Boolean) : FFBattleEvent()
    data class HealCast(val casterId: String, val targetId: String, val amount: Int) : FFBattleEvent()
    data class GroupHeal(val casterId: String, val amounts: Map<String, Int>) : FFBattleEvent()
    data class AbilityUsed(val heroId: String, @StringRes val nameRes: Int, @StringRes val descRes: Int, val args: List<Any> = emptyList()) : FFBattleEvent()
    data class ExpGained(val heroId: String, val amount: Long, val leveledUp: Boolean, val newLevel: Int) : FFBattleEvent()
    data class EnemyDefeated(val enemyId: String, val monsterType: MonsterType = MonsterType.GOBLIN, val gilDropped: Long, val magiciteDropped: Long, val expDropped: Long, val materialsDropped: Map<String, Int> = emptyMap()) : FFBattleEvent()
    data class HeroFell(val heroId: String, val heroName: String, val heroClass: HeroClass) : FFBattleEvent()
    data class MagiciteStolen(val heroId: String, val amount: Long) : FFBattleEvent()
    data class SummonUsed(val heroId: String, val summonName: String, val totalDamage: Long) : FFBattleEvent()
    data class BardSong(val heroId: String, @StringRes val songRes: Int, @StringRes val effectRes: Int) : FFBattleEvent()
    data class FloorComplete(val floor: Int, val gilEarned: Long, val magiciteEarned: Long, val itemsFound: List<Item>, val updatedHeroes: List<Hero>, val updatedBenchedHeroes: List<Hero> = emptyList(), val leveledUpHeroIds: List<String> = emptyList()) : FFBattleEvent()    data class BossDefeated(val bossName: String, val dimensionComplete: Boolean, val materialsDropped: Map<String, Int> = emptyMap()) : FFBattleEvent()
    data class MaterialDropped(val materialId: String, val amount: Int = 1) : FFBattleEvent()
    data class SafariTicketDropped(val amount: Int = 1) : FFBattleEvent()
    data class MonsterCardDropped(val petId: String) : FFBattleEvent()
    object AllHeroesFell : FFBattleEvent()
}

data class PetUnit(val pet: Tier2Pet, val speed: Int = 120)

class FFBattleEngine(private val context: Context) {
    suspend fun runBattle(
        heroes: List<Hero>,
        benchedHeroes: List<Hero> = emptyList(), // ADD THIS
        dimension: FFDimension,
        startFloor: Int,
        speed: BattleSpeed,
        relicBonuses: RelicBonuses,
        mode: DungeonMode = DungeonMode.REGULAR,
        equippedTier2Pet: Tier2Pet? = null,
        isPaused: () -> Boolean = { false },
        onEvent: (FFBattleEvent) -> Unit
    ) {
        var currentFloor = startFloor
        val maxFloor = if (mode == DungeonMode.BOSS_LAIR) Int.MAX_VALUE else dimension.maxFloor
        val aliveHeroes = heroes.map { it.copy() }.toMutableList()
        val activeBenched = benchedHeroes.map { it.copy() }.toMutableList() // ADD THIS

        var totalGil = 0L
        var totalMagicite = 0L
        var lastAbilityClass: HeroClass? = null

        val allBossTemplates = if (mode == DungeonMode.BOSS_LAIR) {
            FFDimensionData.dimensions.flatMap { d -> d.enemies.filter { it.isBoss } }
        } else emptyList()

        while (aliveHeroes.any { it.isAlive } && currentFloor <= maxFloor) {
            while (isPaused()) {
                delay(200)
            }

            // Track all heroes that level up during the entire floor duration
            val floorLeveledUpHeroIds = mutableSetOf<String>()

            // --- ADD THIS INTERCEPTOR ---
            val floorOnEvent: (FFBattleEvent) -> Unit = { event ->
                onEvent(event)
                if (event is FFBattleEvent.EnemyDefeated) {
                    val benchExp = (event.expDropped * 0.25).toLong()
                    if (benchExp > 0) {
                        activeBenched.forEach { hero ->
                            val result = hero.addExperience(benchExp)
                            if (result) floorLeveledUpHeroIds.add(hero.id)
                            onEvent(FFBattleEvent.ExpGained(hero.id, benchExp, result, hero.level))
                        }
                    }
                }
            }
            // ----------------------------

            // Spawn enemies for this floor
            val bossTemplate = if (mode == DungeonMode.SAFARI) {
                null // SAFARI: Pure monster hunting, no dimension bosses
            } else if (mode == DungeonMode.BOSS_LAIR) {
                if (allBossTemplates.isNotEmpty()) allBossTemplates[(currentFloor - 1) % allBossTemplates.size] else FFDimensionData.getBossForFloor(
                    dimension,
                    currentFloor
                )
            } else {
                FFDimensionData.getBossForFloor(dimension, currentFloor)
            }

            val enemies = if (mode == DungeonMode.BOSS_LAIR && bossTemplate != null) {
                val rawBoss = Enemy.fromTemplate(
                    template = bossTemplate,
                    floor = currentFloor,
                    context = context,
                    relicBonuses = relicBonuses,
                    dimension = dimension
                )


                // Exponential difficulty: 100x base, + 5% compounding increase per floor
                val diffMult = 100.0 * pow(1.05, (currentFloor - 1).toDouble())

                val scaledHp = (rawBoss.maxHp * diffMult).toLong().coerceAtMost(Long.MAX_VALUE)
                val scaledAtk = (rawBoss.attack * diffMult).toLong().coerceAtMost(Long.MAX_VALUE)
                val scaledDef = (rawBoss.defense * diffMult).toLong().coerceAtMost(Long.MAX_VALUE)
                val scaledMDef =
                    (rawBoss.magicDefense * diffMult).toLong().coerceAtMost(Long.MAX_VALUE)
                val scaledGil = (rawBoss.gilReward * (50L + (currentFloor * 5L))).coerceAtMost(Long.MAX_VALUE)

                mutableListOf(
                    rawBoss.copy(
                        maxHp = scaledHp,
                        currentHp = scaledHp,
                        attack = scaledAtk,
                        defense = scaledDef,
                        magicDefense = scaledMDef,
                        gilReward = scaledGil
                    )
                )
            } else if (bossTemplate != null) {
                mutableListOf(
                    Enemy.fromTemplate(
                        bossTemplate,
                        currentFloor,
                        context,
                        relicBonuses = relicBonuses,
                        dimension = dimension,
                        mode = mode
                    )
                )
            } else {
                spawnEnemies(dimension, currentFloor, context, relicBonuses, mode, aliveHeroes).toMutableList()
            }

            var floorBaseGilEarned: Long = 0
            var floorMagiciteEarned: Long = 0

            onEvent(FFBattleEvent.FloorStart(currentFloor, enemies))

            // Floor battle loop
            while (enemies.any { it.currentHp > 0 } && aliveHeroes.any { it.isAlive }) {
                val aliveHeroUnits = aliveHeroes.filter { it.isAlive }
                val aliveEnemies = enemies.filter { it.currentHp > 0 }
                val hasPetTurn = equippedTier2Pet != null && equippedTier2Pet.currentRank >= 5

                val turnOrder: List<Any> = (
                    aliveHeroUnits + aliveEnemies + (if (hasPetTurn) listOf(PetUnit(equippedTier2Pet!!)) else emptyList())
                ).sortedByDescending {
                    when (it) {
                        is Hero -> it.speed
                        is Enemy -> it.speed
                        is PetUnit -> it.speed
                        else -> 0
                    }
                }

                for (actor in turnOrder) {
                    while (isPaused()) {
                        delay(200)
                    }
                    if (!enemies.any { it.currentHp > 0 } || !aliveHeroes.any { it.isAlive }) break

                    when (actor) {
                        is Hero -> {
                            if (actor.isAlive) {
                                executeHeroTurn(
                                    actor,
                                    aliveHeroes,
                                    enemies,
                                    dimension,
                                    relicBonuses,
                                    floorLeveledUpHeroIds,
                                    { lastAbilityClass },
                                    { lastAbilityClass = it },
                                    mode,
                                    { gil, magicite ->
                                        floorBaseGilEarned += gil
                                        floorMagiciteEarned += magicite
                                    },
                                    floorOnEvent
                                )
                                delay(speed.delayMs)
                            }
                        }

                        is Enemy -> {
                            // STRICT CHECK: Only execute turn AND delay if enemy is actually alive
                            if (actor.currentHp > 0) {
                                executeEnemyTurn(
                                    actor,
                                    aliveHeroes,
                                    relicBonuses,
                                    mode,
                                    floorOnEvent
                                ) // Added relicBonuses here
                                delay(speed.delayMs)
                            }
                        }

                        is PetUnit -> {
                            if (enemies.any { it.currentHp > 0 } && aliveHeroes.any { it.isAlive }) {
                                executePetTurn(
                                    actor.pet,
                                    aliveHeroes,
                                    enemies,
                                    dimension,
                                    relicBonuses,
                                    floorLeveledUpHeroIds,
                                    mode,
                                    { gil, magicite ->
                                        floorBaseGilEarned += gil
                                        floorMagiciteEarned += magicite
                                    },
                                    floorOnEvent
                                )
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

                // Award completion XP (x100 if BOSS_LAIR)
                val expMult = if (mode == DungeonMode.BOSS_LAIR) 100 else 1
                val completionExp =
                    ((5 + (currentFloor / 2)) * relicBonuses.expMultiplier * expMult).toLong()
                aliveHeroes.filter { it.isAlive }.forEach { hero ->
                    val result = hero.addExperience(completionExp)
                    if (result) floorLeveledUpHeroIds.add(hero.id)
                    onEvent(FFBattleEvent.ExpGained(hero.id, completionExp, result, hero.level))
                }

                // --- ADD BENCH COMPLETION XP ---
                val benchCompletionExp = (completionExp * 0.25).toLong()
                if (benchCompletionExp > 0) {
                    activeBenched.forEach { hero ->
                        val result = hero.addExperience(benchCompletionExp)
                        if (result) floorLeveledUpHeroIds.add(hero.id)
                        onEvent(FFBattleEvent.ExpGained(hero.id, benchCompletionExp, result, hero.level))
                    }
                }

                // Loot chance
                val itemsFound = mutableListOf<Item>()

                if (mode == DungeonMode.SAFARI) {
                    // SAFARI: Zero gear drops on floor completion
                } else if (mode == DungeonMode.BOSS_LAIR) {
                    // Custom Loot Math (Boss Lair ONLY):
                    // 1. Mythic Shards: 25% drop chance per boss kill. Yields multiple shards.
                    if (Random.nextFloat() < 0.25f) {
                        val shardQty = Random.nextInt(2, 6)
                        onEvent(FFBattleEvent.MaterialDropped("mythic_shard", shardQty))
                    }

                    // 2. Mythic Gear: 5% caped drop chance per boss kill to drop exactly one Mythic Gear piece.
                    val mythicDropChance =
                        (0.002f + (currentFloor - 1) * 0.001f).coerceAtMost(0.05f)
                    if (Random.nextFloat() < mythicDropChance) {
                        val effectiveFloor = 1000 + (currentFloor * 225)

                        val baseMythic = Item.random(
                            floor = effectiveFloor,
                            context = context,
                            relicBonuses = relicBonuses,
                            minRarity = Rarity.MYTHIC,
                            dimension = dimension.number
                        )
                        val compatibleSets =
                            MythicSet.entries.filter { baseMythic.slot in it.allowedSlots }
                        val chosenSet =
                            if (compatibleSets.isNotEmpty()) compatibleSets.random() else MythicSet.entries.random()
                        val setName = try {
                            context.getString(chosenSet.nameRes)
                        } catch (_: Exception) {
                            "Mythic Gear"
                        }
                        val finalMythicDrop = baseMythic.copy(
                            setId = chosenSet.id,
                            name = setName
                        )
                        itemsFound.add(finalMythicDrop)
                    }
                } else {
                    val rawDropChance =
                        if (bossTemplate != null) 100f else 10f * (1f + relicBonuses.petItemFindBonus)
                    val finalDropChance =
                        if (bossTemplate != null) 100f else (100f * (rawDropChance / (rawDropChance + 85f))).coerceAtMost(
                            85f
                        )

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

                    if (currentFloor >= 100) {
                        val mythicChance = if (bossTemplate != null) 0.005f else 0.0001f
                        if (Random.nextFloat() * 100f < mythicChance) {
                            // 1. Generate the base mythic item first
                            val baseMythic = Item.random(
                                floor = currentFloor,
                                context = context,
                                relicBonuses = relicBonuses,
                                minRarity = Rarity.MYTHIC,
                                dimension = dimension.number
                            )

                            // 2. Filter valid mythic sets that match this item's slot
                            val compatibleSets =
                                MythicSet.entries.filter { baseMythic.slot in it.allowedSlots }
                            val chosenSet =
                                if (compatibleSets.isNotEmpty()) compatibleSets.random() else MythicSet.entries.random()

                            // 3. Stamp the correct set ID and lore name onto the drop
                            val setName = try {
                                context.getString(chosenSet.nameRes)
                            } catch (_: Exception) {
                                "Mythic Gear"
                            }
                            val finalMythicDrop = baseMythic.copy(
                                setId = chosenSet.id,
                                name = setName
                            )

                            itemsFound.add(finalMythicDrop)
                        }
                    }
                }

                totalGil += gilEarned
                totalMagicite += magiciteEarned

                if (bossTemplate != null) {
                    val bossName =
                        runCatching { context.getString(bossTemplate.nameRes) }.getOrNull()
                            ?: runCatching { context.resources?.getString(bossTemplate.nameRes) }.getOrNull()
                            ?: "Boss"
                    onEvent(FFBattleEvent.BossDefeated(bossName, currentFloor >= maxFloor))
                    // SAFARI TICKET BALANCE:
                    // Regular Mode: 10% chance (1 boss every 10 floors = ~1 ticket per 100 floors)
                    // Boss Lair: 1% chance (1 boss every floor = ~1 ticket per 100 floors)
                    if (dimension.number>10) {
                        val ticketChance = if (mode == DungeonMode.BOSS_LAIR) 0.01f else 1.00f
                        if (Random.nextFloat() < ticketChance) {
                            onEvent(FFBattleEvent.SafariTicketDropped(1))
                        }
                    }
                }



                aliveHeroes.removeAll { !it.isAlive }

                onEvent(
                    FFBattleEvent.FloorComplete(
                        currentFloor, gilEarned, magiciteEarned, itemsFound,
                        aliveHeroes.map { it.copy() },
                        activeBenched.map { it.copy() }, // ADD THIS
                        floorLeveledUpHeroIds.toList()
                    )
                )

                currentFloor++
            }
        }

        if (!aliveHeroes.any { it.isAlive }) onEvent(FFBattleEvent.AllHeroesFell)
    }

    private fun spawnEnemies(
        dimension: FFDimension,
        floor: Int,
        context: Context,
        relicBonuses: RelicBonuses,
        mode: DungeonMode = DungeonMode.REGULAR,
        activeHeroes: List<Hero> = emptyList() // ADD THIS PARAMETER
    ): List<Enemy> {
        val targetDim = if (mode == DungeonMode.SAFARI) FFDimensionData.safariDimension else dimension
        val templates = if (mode == DungeonMode.SAFARI) targetDim.enemies else FFDimensionData.getEnemiesForFloor(targetDim, floor)
        if (templates.isEmpty()) return emptyList()
        val count  = Random.nextInt(1, 4)

        // SAFARI: Dynamic Level Scaling
        if (mode == DungeonMode.SAFARI && activeHeroes.isNotEmpty()) {
            // Updated to match actual Level 60 naked stats: ~889k HP and ~80k ATK
            val baselineLvl60Hp = 800_000L
            val baselineLvl60Atk = 80_000L

            val avgHeroHp = activeHeroes.map { it.maxHp }.average().toLong().coerceAtLeast(baselineLvl60Hp)
            val avgHeroAtk = activeHeroes.map { maxOf(it.attack, it.magic) }.average().toLong().coerceAtLeast(baselineLvl60Atk)

            return List(count) {
                val template = templates.random()
                val baseEnemy = Enemy.fromTemplate(
                    template = template,
                    floor = floor,
                    context = context,
                    relicBonuses = relicBonuses,
                    dimension = targetDim,
                    mode = mode
                )
                // HP: Scaled to take roughly 3-4 hits from the baseline/average hero.
                val scaledHp = (avgHeroAtk * 0.55 * template.hpMult).toLong().coerceAtLeast(10L)
                // ATK: Brute-forces the 85% defense mitigation cap.
                // We multiply by 2.35 so that AFTER blocking 85%, the hero still loses ~35% of their HP.
                val scaledAtk = (avgHeroHp * 2.35 * template.atkMult).toLong().coerceAtLeast(5L)

                // DEF/MDEF: Scaled to roughly 50% of the hero's attack so damage curves smoothly.
                val scaledDef = (avgHeroAtk * 0.5 * template.defMult).toLong().coerceAtLeast(5L)

                baseEnemy.copy(
                    maxHp = scaledHp,
                    currentHp = scaledHp,
                    attack = scaledAtk,
                    defense = scaledDef,
                    magicDefense = scaledDef,
                    gilReward = 0L,
                    magiciteDropped = 0L
                )
            }
        }
        return List(count) {
            Enemy.fromTemplate(
                templates.random(),
                floor,
                context,
                relicBonuses = relicBonuses,
                dimension = targetDim,
                mode = mode
            )
        }
    }

    private fun handleEnemyDefeated(
        enemy: Enemy,
        dimension: FFDimension,
        relicBonuses: RelicBonuses,
        allies: List<Hero>,
        levelUpTracker: MutableSet<String>,
        mode: DungeonMode = DungeonMode.REGULAR,
        onLootCollected: (Long, Long) -> Unit,
        onEvent: (FFBattleEvent) -> Unit
    ) {
        val expMult = if (mode == DungeonMode.BOSS_LAIR) 100 else 1
        val exp = ((enemy.floor * 2) * relicBonuses.expMultiplier * expMult).toLong()
        // SAFARI: Zero out normal materials and gear
        // SAFARI: Zero out normal materials, gil, and magicite
        val gil = if (mode == DungeonMode.SAFARI) 0L else enemy.gilDropped
        val magicite = if (mode == DungeonMode.SAFARI) 0L else enemy.magiciteDropped
        val materials = if (mode == DungeonMode.SAFARI) emptyMap() else calculateMaterialDrops(enemy, enemy.floor, dimension, relicBonuses, mode)

        onLootCollected(gil, magicite)
        onEvent(
            FFBattleEvent.EnemyDefeated(
                enemy.id,
                enemy.type,
                gil,
                magicite,
                exp,
                materials
            )
        )

        // SAFARI: Exactly 5% flat card drop rate, ignoring all multipliers/relics
        if (mode == DungeonMode.SAFARI) {
            if (kotlin.random.Random.nextFloat() < 0.05f) {
                val droppedPet = PetType.entries.random()
                onEvent(FFBattleEvent.MonsterCardDropped(droppedPet.name))
            }
        } else {
            // Standard monster card or regular drop logic
            materials.forEach { (matId, amt) ->
                onEvent(FFBattleEvent.MaterialDropped(matId, amt))
            }
        }
        awardExpToParty(allies, exp, levelUpTracker, onEvent)
    }

    internal fun calculateMaterialDrops(
        enemy: Enemy,
        floor: Int,
        dimension: FFDimension,
        relicBonuses: RelicBonuses,
        mode: DungeonMode = DungeonMode.REGULAR
    ): Map<String, Int> {
        if (mode == DungeonMode.SAFARI) return emptyMap()
        if (mode == DungeonMode.BOSS_LAIR) {
            return if (Random.nextFloat() < 0.25f) mapOf(
                "mythic_shard" to Random.nextInt(
                    2,
                    6
                )
            ) else emptyMap()
        }

        val drops = mutableMapOf<String, Int>()
        val relicMultiplier =
            1f + relicBonuses.alchemistDropChanceBonus + (relicBonuses.magnetBonus * 0.10f)
        val dropRules = MonsterLootTable.getDropsForMonster(enemy.type, enemy.isBoss)

        dropRules.forEach { rule ->
            val qty = evaluateRuleDrop(rule, floor, relicMultiplier, relicBonuses.pocketsBonus)
            if (qty > 0) {
                drops[rule.materialId] = (drops[rule.materialId] ?: 0) + qty
            }
        }

        if (floor >= 100 && enemy.isBoss) {
            drops["mythic_shard"] = (drops["mythic_shard"] ?: 0) + 1
        }

        return drops
    }

    private fun evaluateRuleDrop(
        rule: MaterialDropRule,
        floor: Int,
        relicMultiplier: Float,
        pocketsBonus: Float
    ): Int {
        val isGem = rule.materialId.contains("gem")
        val maxCap = when {
            rule.isBossTrophy -> 0.50f
            isGem -> 0.05f
            else -> 0.15f
        }
        val finalChance = (rule.baseDropChance * relicMultiplier).coerceAtMost(maxCap)
        if (Random.nextFloat() >= finalChance) return 0

        val floorBonus = if (floor >= 50 && !isGem) 0.2f else 0.0f
        val extraYieldChance = (pocketsBonus * 0.2f) + floorBonus
        return if (Random.nextFloat() < extraYieldChance) rule.minQuantity + 1 else rule.minQuantity
    }

    private fun applyDamageToEnemy(
        attacker: Hero,
        target: Enemy,
        damage: Long,
        isMagic: Boolean,
        isCritical: Boolean,
        enemies: MutableList<Enemy>,
        dimension: FFDimension,
        relicBonuses: RelicBonuses,
        allies: List<Hero>,
        levelUpTracker: MutableSet<String>,
        onLootCollected: (Long, Long) -> Unit,
        onEvent: (FFBattleEvent) -> Unit,
        mode: DungeonMode = DungeonMode.REGULAR
    ) {
        val wasAlive = target.currentHp > 0
        target.currentHp -= damage
        onEvent(FFBattleEvent.DamageDealt(attacker.id, target.id, damage, isMagic, isCritical))
        if (wasAlive && target.currentHp <= 0) {
            enemies.remove(target) // Remove defeated enemy from combat immediately so heroes never attack 0-health enemies
            handleEnemyDefeated(
                target,
                dimension,
                relicBonuses,
                allies,
                levelUpTracker,
                mode,
                onLootCollected,
                onEvent
            )
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
        mode: DungeonMode = DungeonMode.REGULAR,
        onLootCollected: (Long, Long) -> Unit,
        onEvent: (FFBattleEvent) -> Unit
    ) {
        onEvent(FFBattleEvent.TurnStart(hero.id, hero.name))
        // SAFARI FIX: Use raw level for mastery since relics are disabled
        val masteryLvl = if (mode == DungeonMode.SAFARI) hero.level else relicBonuses.getMasteryLevel(hero.heroClass)
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
            executeJobAbility(
                hero,
                allies,
                enemies,
                dimension,
                relicBonuses,
                levelUpTracker,
                getLastAbility,
                setLastAbility,
                mode,
                onLootCollected,
                onEvent
            )
            return
        }

        when (hero.aiPriority) {
            AIPriority.ATTACK -> {
                val target =
                    enemies.filter { it.currentHp > 0 }.minByOrNull { it.currentHp } ?: return
                val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                applyDamageToEnemy(
                    hero,
                    target,
                    dmg,
                    false,
                    isCrit,
                    enemies,
                    dimension,
                    relicBonuses,
                    allies,
                    levelUpTracker,
                    onLootCollected,
                    onEvent,
                    mode
                )
            }

            AIPriority.MAGIC -> {
                if (masteryLvl >= 1) {
                    val target =
                        enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp } ?: return
                    val (dmg, isCrit) = calcMagicDamage(hero, target)
                    applyDamageToEnemy(
                        hero,
                        target,
                        dmg,
                        true,
                        isCrit,
                        enemies,
                        dimension,
                        relicBonuses,
                        allies,
                        levelUpTracker,
                        onLootCollected,
                        onEvent,
                        mode
                    )
                } else {
                    val target =
                        enemies.filter { it.currentHp > 0 }.minByOrNull { it.currentHp } ?: return
                    val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                    applyDamageToEnemy(
                        hero,
                        target,
                        dmg,
                        false,
                        isCrit,
                        enemies,
                        dimension,
                        relicBonuses,
                        allies,
                        levelUpTracker,
                        onLootCollected,
                        onEvent,
                        mode
                    )
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
                    val target =
                        enemies.filter { it.currentHp > 0 }.minByOrNull { it.currentHp } ?: return
                    val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                    applyDamageToEnemy(
                        hero,
                        target,
                        dmg,
                        false,
                        isCrit,
                        enemies,
                        dimension,
                        relicBonuses,
                        allies,
                        levelUpTracker,
                        onLootCollected,
                        onEvent,
                        mode
                    )
                }
            }

            AIPriority.DEFEND -> {
                val target = allies.filter { it.isAlive }.minByOrNull { it.currentHp }
                if (masteryLvl >= 1 && target != null && target.id != hero.id) {
                    onEvent(
                        FFBattleEvent.AbilityUsed(
                            hero.id,
                            R.string.ability_cover_name,
                            R.string.ability_cover_desc,
                            listOf(target.name)
                        )
                    )
                } else {
                    val enemyTarget =
                        enemies.filter { it.currentHp > 0 }.minByOrNull { it.currentHp } ?: return
                    val (dmg, isCrit) = calcPhysicalDamage(hero, enemyTarget)
                    applyDamageToEnemy(
                        hero,
                        enemyTarget,
                        dmg,
                        false,
                        isCrit,
                        enemies,
                        dimension,
                        relicBonuses,
                        allies,
                        levelUpTracker,
                        onLootCollected,
                        onEvent,
                        mode
                    )
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
        mode: DungeonMode = DungeonMode.REGULAR,
        onLootCollected: (Long, Long) -> Unit,
        onEvent: (FFBattleEvent) -> Unit
    ) {
        if (hero.heroClass != HeroClass.MIME) {
            setLastAbility(hero.heroClass)
        }
        // SAFARI FIX: Use raw level for mastery
        val masteryLvl = if (mode == DungeonMode.SAFARI) hero.level else relicBonuses.getMasteryLevel(hero.heroClass)
        val activeSkill = JobAbilityData.getActiveAbilityForLevel(hero.heroClass, masteryLvl) ?: return
        onEvent(
            FFBattleEvent.AbilityUsed(
                hero.id,
                activeSkill.nameRes,
                activeSkill.descRes,
                listOf(hero.name)
            )
        )

        when (hero.heroClass) {
            HeroClass.FREELANCER -> {
                if (masteryLvl >= 25) {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 2.2).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                    val healAmt = (hero.maxHp * 0.30f + hero.attack * 0.5f).toInt()
                    hero.currentHp = minOf(hero.currentHp + healAmt, hero.maxHp)
                    onEvent(FFBattleEvent.HealCast(hero.id, hero.id, healAmt))
                    hero.abilityCharge = 1
                } else if (masteryLvl >= 10) {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.8).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                    val healAmt = (hero.maxHp * 0.15f + hero.attack * 0.2f).toInt()
                    hero.currentHp = minOf(hero.currentHp + healAmt, hero.maxHp)
                    onEvent(FFBattleEvent.HealCast(hero.id, hero.id, healAmt))
                } else {
                    val healAmt = (hero.maxHp * 0.05f + hero.attack * 0.1f).toInt()
                    hero.currentHp = minOf(hero.currentHp + healAmt, hero.maxHp)
                    onEvent(FFBattleEvent.HealCast(hero.id, hero.id, healAmt))
                }
            }

            HeroClass.WARRIOR -> {
                val mult = when {
                    masteryLvl >= 25 -> 2.2; masteryLvl >= 10 -> 1.8; else -> 1.5
                }
                val targetEnemies =
                    if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else listOfNotNull(
                        enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp })
                targetEnemies.forEach { target ->
                    val (baseDmg, _) = calcPhysicalDamage(hero, target)
                    val dmg = (baseDmg * mult).toLong()
                    applyDamageToEnemy(
                        hero,
                        target,
                        dmg,
                        false,
                        true,
                        enemies,
                        dimension,
                        relicBonuses,
                        allies,
                        levelUpTracker,
                        onLootCollected,
                        onEvent
                    )
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
                    val target = allies.filter { it.isAlive }
                        .minByOrNull { it.currentHp.toFloat() / it.maxHp } ?: hero
                    if (target.isAlive) {
                        val healMult = if (masteryLvl >= 10) 2.8f else 2.0f
                        val heal = (hero.magic * healMult + target.maxHp * 0.30f + 30).toInt()
                        target.currentHp = minOf(target.currentHp + heal, target.maxHp)
                        onEvent(FFBattleEvent.HealCast(hero.id, target.id, heal))
                    }
                }
            }

            HeroClass.BLACK_MAGE -> {
                if (masteryLvl >= 40) {
                    val targetEnemies = enemies.filter { it.currentHp > 0 }
                    targetEnemies.forEach { enemy ->
                        val dmg = (enemy.maxHp / 6L).coerceAtLeast(1L)
                        applyDamageToEnemy(
                            hero,
                            enemy,
                            dmg,
                            true,
                            false,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                } else {
                    val mult = when {
                        masteryLvl >= 25 -> 2.2; masteryLvl >= 10 -> 1.8; else -> 1.4
                    }
                    val targetEnemies =
                        if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else if (masteryLvl >= 10) enemies.filter { it.currentHp > 0 }
                            .take(2) else listOfNotNull(enemies.filter { it.currentHp > 0 }
                            .firstOrNull())
                    targetEnemies.forEach { enemy ->
                        val (baseDmg, _) = calcMagicDamage(hero, enemy)
                        val dmg = (baseDmg * mult).toLong()
                        applyDamageToEnemy(
                            hero,
                            enemy,
                            dmg,
                            true,
                            true,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                }
            }

            HeroClass.THIEF -> {
                val hits = if (masteryLvl >= 25) 3 else 1
                val mult = when {
                    masteryLvl >= 25 -> 2.0; masteryLvl >= 10 -> 1.5; else -> 1.2
                }
                repeat(hits) {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return@repeat
                    val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                    val finalDmg = (dmg * mult).toLong()
                    val forceCrit = masteryLvl >= 25 || isCrit
                    applyDamageToEnemy(
                        hero,
                        target,
                        finalDmg,
                        false,
                        forceCrit,
                        enemies,
                        dimension,
                        relicBonuses,
                        allies,
                        levelUpTracker,
                        onLootCollected,
                        onEvent
                    )
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
                        val dmg = (baseDmg * 2.0).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                } else if (masteryLvl >= 10) {
                    repeat(3) {
                        val target =
                            enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return@repeat
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.2).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.4).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                }
            }

            HeroClass.KNIGHT -> {
                val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp }
                if (target != null) {
                    val totalAtkDef = hero.attack + hero.defense
                    val mult = when {
                        masteryLvl >= 25 -> 2.2; masteryLvl >= 10 -> 1.8; else -> 1.5
                    }
                    val baseDmg = maxOf(1, totalAtkDef - target.defense + Random.nextInt(-3, 4))
                    val dmg = (baseDmg * mult).toLong()
                    applyDamageToEnemy(
                        hero,
                        target,
                        dmg,
                        false,
                        true,
                        enemies,
                        dimension,
                        relicBonuses,
                        allies,
                        levelUpTracker,
                        onLootCollected,
                        onEvent
                    )
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
                val targetEnemies =
                    if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else listOfNotNull(
                        enemies.filter { it.currentHp > 0 }.firstOrNull()
                    )
                val mult = when {
                    masteryLvl >= 25 -> 2.0; masteryLvl >= 10 -> 1.6; else -> 1.3
                }
                targetEnemies.forEach { enemy ->
                    val (dmg, isCrit) = calcPhysicalDamage(hero, enemy)
                    val finalDmg = (dmg * mult).toLong()
                    applyDamageToEnemy(
                        hero,
                        enemy,
                        finalDmg,
                        false,
                        isCrit,
                        enemies,
                        dimension,
                        relicBonuses,
                        allies,
                        levelUpTracker,
                        onLootCollected,
                        onEvent
                    )
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
                        lowestAlly.currentHp =
                            minOf(lowestAlly.currentHp + healAmt, lowestAlly.maxHp)
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
                        val target =
                            enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return@repeat
                        val (dmg, isCrit) = calcMagicDamage(hero, target)
                        val finalDmg = (dmg * 2.0).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            finalDmg,
                            true,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
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
                        val target =
                            enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return@repeat
                        val (dmg, isCrit) = calcMagicDamage(hero, target)
                        val finalDmg = (dmg * 1.8).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            finalDmg,
                            true,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (dmg, isCrit) = calcMagicDamage(hero, target)
                        val finalDmg = (dmg * 1.4).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            finalDmg,
                            true,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                    val lowestAlly = allies.filter { it.isAlive }
                        .minByOrNull { it.currentHp.toFloat() / it.maxHp }
                    if (lowestAlly != null) {
                        val healAmt = (hero.magic * 2.0f + lowestAlly.maxHp * 0.20f).toInt()
                        lowestAlly.currentHp =
                            minOf(lowestAlly.currentHp + healAmt, lowestAlly.maxHp)
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
                var totalDmg = 0L
                enemies.filter { it.currentHp > 0 }.forEach { enemy ->
                    val (baseDmg, isCrit) = calcMagicDamage(hero, enemy)
                    val dmg = (baseDmg * mult).toLong()
                    totalDmg += dmg
                    applyDamageToEnemy(
                        hero,
                        enemy,
                        dmg,
                        true,
                        isCrit || mult > 2f,
                        enemies,
                        dimension,
                        relicBonuses,
                        allies,
                        levelUpTracker,
                        onLootCollected,
                        onEvent
                    )
                }
                onEvent(FFBattleEvent.SummonUsed(hero.id, name, totalDmg))
            }

            HeroClass.NINJA -> {
                if (masteryLvl >= 25) {
                    val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp }
                    if (target != null) {
                        val dmg = (hero.attack * 2.8).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            true,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                } else if (masteryLvl >= 10) {
                    repeat(2) {
                        val target =
                            enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp }
                                ?: return@repeat
                        val dmg = (hero.attack * 1.5).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            true,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.maxByOrNull { it.currentHp }
                    if (target != null) {
                        val dmg = (hero.attack * 1.8).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            true,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                }
            }

            HeroClass.DRAGOON -> {
                if (masteryLvl >= 25) {
                    enemies.filter { it.currentHp > 0 }.forEach { target ->
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 2.5).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                } else if (masteryLvl >= 10) {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val baseDmg = maxOf(1, hero.attack + Random.nextInt(-3, 4))
                        val dmg = (baseDmg * 2.0).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            true,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.6).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                }
            }

            HeroClass.BARD -> {
                val (songRes, effectRes, healPct) = when {
                    masteryLvl >= 25 -> Triple(
                        R.string.song_ballad,
                        R.string.song_ballad_effect,
                        0.45f
                    )

                    masteryLvl >= 10 -> Triple(
                        R.string.song_minuet,
                        R.string.song_minuet_effect,
                        0.30f
                    )

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
                        val dmg = (hero.attack * 2.5).toLong()
                        applyDamageToEnemy(
                            hero,
                            enemy,
                            dmg,
                            false,
                            true,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                } else if (masteryLvl >= 10) {
                    enemies.filter { it.currentHp > 0 }.forEach { enemy ->
                        val dmg = (hero.attack * 2.0).toLong()
                        applyDamageToEnemy(
                            hero,
                            enemy,
                            dmg,
                            false,
                            true,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, _) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.5).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            true,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                }
            }

            HeroClass.MIME -> {
                val lastAbility = getLastAbility()
                if (lastAbility != null) {
                    val mimickedHero = hero.copy(heroClass = lastAbility)
                    executeJobAbility(
                        mimickedHero,
                        allies,
                        enemies,
                        dimension,
                        relicBonuses,
                        levelUpTracker,
                        getLastAbility,
                        setLastAbility,
                        mode,
                        onLootCollected,
                        onEvent
                    )
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (dmg, isCrit) = calcPhysicalDamage(hero, target)
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                }
            }

            HeroClass.NECROMANCER -> {
                val targetEnemies =
                    if (masteryLvl >= 25) enemies.filter { it.currentHp > 0 } else listOfNotNull(
                        enemies.filter { it.currentHp > 0 }.firstOrNull()
                    )
                val mult = when {
                    masteryLvl >= 25 -> 2.2; masteryLvl >= 10 -> 1.8; else -> 1.4
                }
                targetEnemies.forEach { target ->
                    val (baseDmg, isCrit) = calcMagicDamage(hero, target)
                    val dmg = (baseDmg * mult).toLong()
                    applyDamageToEnemy(
                        hero,
                        target,
                        dmg,
                        true,
                        isCrit,
                        enemies,
                        dimension,
                        relicBonuses,
                        allies,
                        levelUpTracker,
                        onLootCollected,
                        onEvent
                    )

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
                        val dmg = (baseDmg * 2.2).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            true,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
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
                        val dmg = (baseDmg * 1.8).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            true,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.5).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                }
            }

            HeroClass.ONION_KNIGHT -> {
                if (masteryLvl >= 25) {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, _) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 3.0).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            true,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                } else if (masteryLvl >= 10) {
                    repeat(2) {
                        val target =
                            enemies.filter { it.currentHp > 0 }.firstOrNull() ?: return@repeat
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.5).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                } else {
                    val target = enemies.filter { it.currentHp > 0 }.firstOrNull()
                    if (target != null) {
                        val (baseDmg, isCrit) = calcPhysicalDamage(hero, target)
                        val dmg = (baseDmg * 1.2).toLong()
                        applyDamageToEnemy(
                            hero,
                            target,
                            dmg,
                            false,
                            isCrit,
                            enemies,
                            dimension,
                            relicBonuses,
                            allies,
                            levelUpTracker,
                            onLootCollected,
                            onEvent
                        )
                    }
                }
            }
        }
    }

    private fun awardExpToParty(
        heroes: List<Hero>,
        amount: Long,
        tracker: MutableSet<String>,
        onEvent: (FFBattleEvent) -> Unit
    ) {
        heroes.filter { it.isAlive }.forEach { hero ->
            val result = hero.addExperience(amount)
            if (result) tracker.add(hero.id)
            onEvent(FFBattleEvent.ExpGained(hero.id, amount, result, hero.level))
        }
    }

    private fun calcPhysicalDamage(attacker: Hero, target: Enemy): Pair<Long, Boolean> {
        val isCrit = Random.nextInt(100) < attacker.critChance
        val critMult = 1.0f + (attacker.critDamage / 100f)
        val totalAtk = attacker.attack.toDouble()

        // Added quadratic tail for high floors so monsters actually mitigate damage
        val f = target.floor.toDouble()
        val quadDefK = if (f > 200) ((f - 200) / 20.0) * ((f - 200) / 20.0) * 0.5 else 0.0
        val defK = 500.0 + (f * 2.0) + quadDefK

        val mitigation = (target.defense.toDouble() / (target.defense.toDouble() + defK)).coerceAtMost(0.85)
        val baseDmg = maxOf(1.toDouble(), totalAtk * (1 - mitigation) + Random.nextInt(-2, 3))
        val finalDmg = if (isCrit) (baseDmg * critMult).toLong() else baseDmg.toLong()
        return finalDmg to isCrit
    }

    private fun calcMagicDamage(attacker: Hero, target: Enemy): Pair<Long, Boolean> {
        val isCrit = Random.nextInt(100) < (attacker.critChance / 2)
        val critMult = 1.0 + (attacker.critDamage / 100.0)
        val totalMag = attacker.magic.toDouble() * 1.5

        // Added quadratic tail for high floors
        val f = target.floor.toDouble()
        val quadMDefK = if (f > 200) ((f - 200) / 20.0) * ((f - 200) / 20.0) * 0.5 else 0.0
        val mdefK = 400.0 + (f * 1.5) + quadMDefK

        val mitigation = (target.magicDefense.toDouble() / (target.magicDefense.toDouble() + mdefK)).coerceAtMost(0.85)
        val baseDmg = maxOf(1.toDouble(), totalMag * (1 - mitigation) + Random.nextInt(-2, 3))
        val finalDmg = if (isCrit) (baseDmg * critMult).toLong() else baseDmg.toLong()
        return finalDmg to isCrit
    }

    private fun executePetTurn(
        pet: Tier2Pet,
        allies: List<Hero>,
        enemies: MutableList<Enemy>,
        dimension: FFDimension,
        relicBonuses: RelicBonuses,
        levelUpTracker: MutableSet<String>,
        mode: DungeonMode,
        onLootCollected: (Long, Long) -> Unit,
        onEvent: (FFBattleEvent) -> Unit
    ) {
        val aliveAllies = allies.filter { it.isAlive }
        val aliveEnemies = enemies.filter { it.currentHp > 0 }
        if (aliveAllies.isEmpty() || aliveEnemies.isEmpty()) return

        val petName = pet.petId.lowercase().replaceFirstChar { it.uppercase() }
        val isHealer = pet.petId.uppercase().contains("MOOGLE") || pet.petId.uppercase().contains("FAIRY")

        if (isHealer) {
            val wounded = aliveAllies.minByOrNull { it.currentHp.toFloat() / it.maxHp.toFloat() } ?: aliveAllies.random()
            val healAmount = (wounded.maxHp * 0.25f).toInt().coerceAtLeast(100)
            onEvent(
                FFBattleEvent.AbilityUsed(
                    heroId = "pet_${pet.petId}",
                    nameRes = R.string.log_generic,
                    descRes = R.string.log_generic,
                    args = listOf("🐾 $petName Healing Touch")
                )
            )
            wounded.currentHp = (wounded.currentHp + healAmount).coerceAtMost(wounded.maxHp)
            onEvent(FFBattleEvent.HealCast("pet_${pet.petId}", wounded.id, healAmount))
        } else {
            val target = aliveEnemies.minByOrNull { it.currentHp } ?: return
            val isCrit = pet.petId.uppercase().contains("CACTUAR") || pet.petId.uppercase().contains("TONBERRY")
            val damage = if (isCrit) 1500L else 800L

            onEvent(
                FFBattleEvent.AbilityUsed(
                    heroId = "pet_${pet.petId}",
                    nameRes = R.string.log_generic,
                    descRes = R.string.log_generic,
                    args = listOf("🐾 $petName Companion Strike")
                )
            )

            val wasAlive = target.currentHp > 0
            target.currentHp -= damage
            onEvent(
                FFBattleEvent.DamageDealt(
                    attackerId = "pet_${pet.petId}",
                    targetId = target.id,
                    damage = damage,
                    isMagic = false,
                    isCritical = isCrit
                )
            )

            if (wasAlive && target.currentHp <= 0) {
                enemies.remove(target)
                handleEnemyDefeated(
                    target,
                    dimension,
                    relicBonuses,
                    allies,
                    levelUpTracker,
                    mode,
                    onLootCollected,
                    onEvent
                )
            }
        }
    }

    private fun executeEnemyTurn(
        enemy: Enemy,
        heroes: MutableList<Hero>,
        relicBonuses: RelicBonuses,
        mode: DungeonMode = DungeonMode.REGULAR, // ADD THIS
        onEvent: (FFBattleEvent) -> Unit
    ) {
        val aliveHeroes = heroes.filter { it.isAlive }
        if (aliveHeroes.isEmpty()) return

        // 1. Initial Target Selection (Aggro based on Defense)
        val totalDefense = aliveHeroes.sumOf { maxOf(1, it.defense) }
        var roll = Random.nextLong(totalDefense)

        var target = aliveHeroes.last()
        for (hero in aliveHeroes) {
            val weight = maxOf(1, hero.defense)
            if (roll < weight) {
                target = hero
                break
            }
            roll -= weight
        }

        // Helper function to calculate damage against a specific defender's stats
        fun calculateDamageFor(defender: Hero): Pair<Long, Boolean> {
            val f = enemy.floor.toDouble()

            // Piecewise curve forces endgame heroes to need MASSIVE defense to hit the 85% cap
            val quadK = when {
                f > 1000 -> 800.0 + ((f - 1000) / 10.0) * ((f - 1000) / 10.0) * 2.0
                f > 200 -> ((f - 200) / 20.0) * ((f - 200) / 20.0) * 0.5
                else -> 0.0
            }
            val k = 150.0 + (f * 0.8) + quadK

            val mitigation = (defender.defense.toDouble() / (defender.defense.toDouble() + k)).coerceAtMost(0.85)
            val isCrit = Random.nextInt(100) < (if (enemy.isBoss) 20 else if (enemy.floor >= 200) 10 else 0)
            val critMult = if (isCrit) 1.5 else 1.0
            val rawDmg = maxOf(1.0, (enemy.attack.toDouble() + Random.nextInt(-2, 3)) * critMult)
            val dmg = maxOf(1, (rawDmg * (1.0 - mitigation)).toLong())
            return dmg to isCrit
        }

        var (dmg, isCrit) = calculateDamageFor(target)

        // --- NEW: COVER INTERCEPTION ---
        // If a non-tank is about to take fatal damage, a healthy tank steps in!
        val isOriginalTargetTank =
            target.heroClass == HeroClass.KNIGHT || target.heroClass == HeroClass.PALADIN

        if (!isOriginalTargetTank && dmg >= target.currentHp) {
            // Find a tank in the party who is above 50% HP (so they can successfully use Guts)
            val coveringTank = aliveHeroes.firstOrNull {
                (it.heroClass == HeroClass.KNIGHT || it.heroClass == HeroClass.PALADIN) &&
                        it.currentHp > (it.maxHp * 0.5f)
            }

            if (coveringTank != null) {
                // Emit the "Cover" text to the combat log using existing string resources
                onEvent(
                    FFBattleEvent.AbilityUsed(
                        coveringTank.id,
                        R.string.ability_cover_name,
                        R.string.ability_cover_desc,
                        listOf(target.name)
                    )
                )

                // Swap the target to the Tank and recalculate damage against their heavy armor
                target = coveringTank
                val newCalc = calculateDamageFor(target)
                dmg = newCalc.first
                isCrit = newCalc.second
            }
        }
        // -------------------------------

        // --- ENDGAME SURVIVAL MECHANICS ---

        // 1. Max HP Damage Gate: No single attack can exceed 80% of a hero's maximum HP
        val damageGateCap = (target.maxHp * 0.80).toLong()
        if (dmg > damageGateCap) {
            dmg = damageGateCap
        }

        // 2. Tank Guts: Knights and Paladins survive fatal hits with 1 HP if above 50% Health
        // SAFARI FIX: Use raw level for mastery
        val masteryLvl = if (mode == DungeonMode.SAFARI) target.level else relicBonuses.getMasteryLevel(target.heroClass)
        val isCurrentlyTank = target.heroClass == HeroClass.KNIGHT || target.heroClass == HeroClass.PALADIN
        val hasGuts = isCurrentlyTank && masteryLvl >= 25

        if (hasGuts && target.currentHp > (target.maxHp * 0.5f) && dmg >= target.currentHp) {
            dmg = target.currentHp - 1 // Leave them with exactly 1 HP
        }

        // --------------------------------------

        target.currentHp -= dmg
        onEvent(FFBattleEvent.DamageDealt(enemy.id, target.id, dmg, false, isCrit))

        if (target.currentHp <= 0) {
            target.currentHp = 0
            onEvent(FFBattleEvent.HeroFell(target.id, target.name, target.heroClass))
        }
    }
}
