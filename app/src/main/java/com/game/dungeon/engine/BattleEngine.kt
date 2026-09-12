package com.game.dungeon.engine

import android.content.Context
import com.game.dungeon.data.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

sealed class BattleEvent {
    data class Damage(val targetId: String, val amount: Int, val isHero: Boolean) : BattleEvent()
    data class Log(val message: String, val type: LogType) : BattleEvent()
    data class Victory(val gold: Int, val loot: Item? = null) : BattleEvent()
    object Defeat : BattleEvent()
    object FloorCleared : BattleEvent()
    data class AttackStart(val attackerId: String) : BattleEvent()
    data class AttackHit(val targetId: String) : BattleEvent()
}

class BattleEngine(private val context: Context) {
    fun runBattle(heroes: List<Hero>, enemies: List<Enemy>, speed: BattleSpeed): Flow<BattleEvent> = flow {
        val currentHeroes = heroes.map { it.copy() }.toMutableList()
        val currentEnemies = enemies.map { it.copy() }.toMutableList()

        emit(BattleEvent.Log("Entering Floor...", LogType.SYSTEM))

        while (currentHeroes.any { it.currentHp > 0 } && currentEnemies.any { it.currentHp > 0 }) {
            // Hero Turn
            for (hero in currentHeroes.filter { it.currentHp > 0 }) {
                if (currentEnemies.isEmpty()) break
                
                val target = currentEnemies.random()
                val damage = (hero.attack * Random.nextFloat() * 0.5f + hero.attack * 0.75f).toInt()
                
                emit(BattleEvent.AttackStart(hero.id))
                delay(200)
                
                target.copy(currentHp = target.currentHp - damage).also { updated ->
                    val index = currentEnemies.indexOfFirst { it.id == target.id }
                    if (updated.currentHp <= 0) {
                        currentEnemies.removeAt(index)
                        emit(BattleEvent.Log("${hero.name} defeated ${target.name}!", LogType.HERO_ACTION))
                    } else {
                        currentEnemies[index] = updated
                    }
                }
                
                emit(BattleEvent.AttackHit(target.id))
                emit(BattleEvent.Damage(target.id, damage, false))
                emit(BattleEvent.Log("${hero.name} attacks ${target.name} for $damage", LogType.HERO_ACTION))
                
                delay(speed.delayMs / 2)
            }

            if (currentEnemies.isEmpty()) break

            // Enemy Turn
            for (enemy in currentEnemies.filter { it.currentHp > 0 }) {
                if (currentHeroes.isEmpty()) break
                
                val target = currentHeroes.filter { it.currentHp > 0 }.random()
                val damage = (enemy.attack * Random.nextFloat() * 0.4f + enemy.attack * 0.6f).toInt()
                
                emit(BattleEvent.AttackStart(enemy.id))
                delay(200)
                
                val index = currentHeroes.indexOfFirst { it.id == target.id }
                val updatedHp = target.currentHp - damage
                currentHeroes[index] = target.copy(currentHp = updatedHp)
                
                emit(BattleEvent.AttackHit(target.id))
                emit(BattleEvent.Damage(target.id, damage, true))
                emit(BattleEvent.Log("${enemy.name} hits ${target.name} for $damage", LogType.ENEMY_ACTION))
                
                if (updatedHp <= 0) {
                    emit(BattleEvent.Log("${target.name} has fallen!", LogType.SYSTEM))
                }
                
                delay(speed.delayMs / 2)
            }
        }

        if (currentEnemies.isEmpty()) {
            val baseGold = enemies.size * 5 + Random.nextInt(1, 10)
            val floorBonus = (enemies.firstOrNull()?.floor ?: 1) * 5
            val totalGold = baseGold + floorBonus
            
            val isBoss = enemies.any { it.isBoss }
            val dropChance = if (isBoss) 100 else 8 // Bosses always drop an item
            val loot = if (Random.nextInt(1, 101) <= dropChance) {
                Item.random(
                    floor = enemies.firstOrNull()?.floor ?: 1,
                    context = context,
                    minRarity = if (isBoss) Rarity.RARE else Rarity.COMMON
                )
            } else null

            emit(BattleEvent.Victory(totalGold, loot))
            emit(BattleEvent.FloorCleared)
        } else {
            emit(BattleEvent.Defeat)
        }
    }
}
