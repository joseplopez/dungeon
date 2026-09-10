package com.game.dungeon.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.data.models.*
import com.game.dungeon.data.repository.GameRepository
import com.game.dungeon.engine.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DungeonViewModel @Inject constructor(
  private val repo: GameRepository
) : ViewModel() {

  private val engine = FFBattleEngine()

  val battleState = MutableStateFlow(FFBattleState())
  val gameState = repo.getGameState().stateIn(viewModelScope, SharingStarted.Eagerly, GameState())

  private var battleJob: Job? = null

  data class FFBattleState(
    val currentFloor: Int = 1,
    val currentBiome: FFBiome? = null,
    val dimension: FFDimension? = null,
    val heroes: List<Hero> = emptyList(),
    val enemies: List<Enemy> = emptyList(),
    val battleLog: List<FFLogEntry> = emptyList(),
    val speed: BattleSpeed = BattleSpeed.NORMAL,
    val isRunning: Boolean = false,
    val runComplete: Boolean = false,
    val gilEarnedThisRun: Long = 0,
    val magiciteEarnedThisRun: Int = 0,
    val gilLostToPenalty: Long = 0,
    val itemsFoundThisRun: List<Item> = emptyList(),
    val bossesKilledThisRun: Int = 0,
    val fallenHeroes: List<Hero> = emptyList(),
    val dyingHeroIds: Set<String> = emptySet(),
    val originalPartySize: Int = 0,
    val attackingHeroId: String? = null,
    val hitEnemyId: String? = null,
    val hitHeroId: String? = null,
    val recentLoot: Item? = null,
    val showFloorBanner: Boolean = false,
    val floorBannerText: String = "",
    val showBossBanner: Boolean = false,
    val bossBannerText: String = ""
  )

  data class FFLogEntry(val message: String, val type: LogType, val timestamp: Long = System.currentTimeMillis())
  enum class LogType { HERO_ATTACK, ENEMY_ATTACK, ABILITY, SUMMON, HEAL, SYSTEM, BOSS, HERO_FELL }

  fun startRun(party: List<Hero>, startFloor: Int = 1) {
    battleJob?.cancel()
    val gs = gameState.value ?: GameState()
    val dimension = FFDimensionData.getDimension(gs.currentDimension)
    val relics = RelicBonuses.from(gs)

    battleState.value = FFBattleState(
      currentFloor = startFloor.coerceAtLeast(1),
      dimension = dimension,
      heroes = party.map { it.copy() },
      originalPartySize = party.size,
      isRunning = true
    )

    battleJob = viewModelScope.launch {
      engine.runBattle(
        heroes = battleState.value.heroes,
        dimension = dimension,
        startFloor = startFloor.coerceAtLeast(1),
        speed = battleState.value.speed,
        relicBonuses = relics
      ) { event -> handleEvent(event) }
    }
  }

  private fun handleEvent(event: FFBattleEvent) {
    when (event) {
      is FFBattleEvent.FloorStart -> {
          battleState.update { it.copy(enemies = event.enemies, currentFloor = event.floor) }
      }
      is FFBattleEvent.DamageDealt -> {
        battleState.update { state ->
          val logMsg = if (event.isMagic) "${findName(state, event.attackerId)} casts spell for ${event.damage}!" 
                       else "${findName(state, event.attackerId)} attacks for ${event.damage}${if(event.isCritical) " (CRITICAL!)" else ""}!"
          state.copy(
            heroes = state.heroes.map { h -> if (h.id == event.targetId) h.copy(currentHp = (h.currentHp - event.damage).coerceAtLeast(0)) else h },
            enemies = state.enemies.map { e -> if (e.id == event.targetId) e.copy(currentHp = (e.currentHp - event.damage).coerceAtLeast(0)) else e },
            hitEnemyId = if (!findIsHero(state, event.targetId)) event.targetId else null,
            hitHeroId = if (findIsHero(state, event.targetId)) event.targetId else null,
            attackingHeroId = if (findIsHero(state, event.attackerId)) event.attackerId else null,
            battleLog = (state.battleLog + FFLogEntry(logMsg,
              if(findIsHero(state, event.attackerId)) LogType.HERO_ATTACK else LogType.ENEMY_ATTACK)).takeLast(25)
          )
        }
        viewModelScope.launch {
          delay(150)
          battleState.update { it.copy(hitEnemyId=null, hitHeroId=null, attackingHeroId=null) }
        }
      }
      is FFBattleEvent.ExpGained -> {
          battleState.update { state ->
              val logEntries = mutableListOf<FFLogEntry>()
              if (event.leveledUp) {
                  logEntries.add(FFLogEntry("⭐ ${findName(state, event.heroId)} reached Level ${event.newLevel}!", LogType.SYSTEM))
              }
              state.copy(
                  heroes = state.heroes.map { h -> 
                      if (h.id == event.heroId) {
                          // The engine already updated the values in the Hero object in aliveHeroes list,
                          // but the state.heroes is a separate list of copies.
                          // We need to apply the logic consistently here or pass the full updated hero.
                          var newExp = h.exp + event.amount
                          var newLevel = h.level
                          var newMaxExp = h.expToNextLevel
                          
                          while (newExp >= newMaxExp) {
                              newExp -= newMaxExp
                              newLevel++
                              newMaxExp = (newMaxExp * 1.5).toInt()
                          }
                          h.copy(level = newLevel, exp = newExp, expToNextLevel = newMaxExp)
                      } else h
                  },
                  battleLog = (state.battleLog + logEntries).takeLast(25)
              )
          }
      }
      is FFBattleEvent.EnemyDefeated -> {
          battleState.update { state ->
              val enemy = state.enemies.find { it.id == event.enemyId }
              val logMsg = "Victory! ${enemy?.name} defeated. +${event.expDropped} EXP"
              state.copy(
                  enemies = state.enemies.filter { it.id != event.enemyId },
                  battleLog = (state.battleLog + FFLogEntry(logMsg, LogType.SYSTEM)).takeLast(25)
              )
          }
      }
      is FFBattleEvent.HealCast -> {
          battleState.update { state ->
              val updatedHeroes = state.heroes.map { h ->
                  if (h.id == event.targetId) h.copy(currentHp = minOf(h.maxHp, h.currentHp + event.amount))
                  else h
              }
              state.copy(
                  heroes = updatedHeroes,
                  battleLog = (state.battleLog + FFLogEntry("${findName(state, event.casterId)} heals ${findName(state, event.targetId)} for ${event.amount}", LogType.HEAL)).takeLast(25)
              )
          }
      }
      is FFBattleEvent.GroupHeal -> {
          battleState.update { state ->
              val updatedHeroes = state.heroes.map { h ->
                  val heal = event.amounts[h.id] ?: 0
                  if (heal > 0) h.copy(currentHp = minOf(h.maxHp, h.currentHp + heal))
                  else h
              }
              state.copy(
                  heroes = updatedHeroes,
                  battleLog = (state.battleLog + FFLogEntry("${findName(state, event.casterId)} heals the party!", LogType.HEAL)).takeLast(25)
              )
          }
      }
      is FFBattleEvent.AbilityUsed -> {
          battleState.update { state ->
              state.copy(
                  battleLog = (state.battleLog + FFLogEntry("✨ ${event.description}", LogType.ABILITY)).takeLast(25)
              )
          }
      }
      is FFBattleEvent.HeroFell -> {
        battleState.update { state ->
          val fallen = state.heroes.find { it.id == event.heroId } ?: return@update state
          state.copy(
            dyingHeroIds = state.dyingHeroIds + event.heroId,
            fallenHeroes = state.fallenHeroes + fallen,
            battleLog = (state.battleLog + FFLogEntry(
              "💀 ${event.heroName} has fallen...",
              LogType.HERO_FELL)).takeLast(25)
          )
        }
        viewModelScope.launch {
            delay(1000)
            battleState.update { it.copy(
                heroes = it.heroes.filter { h -> h.id != event.heroId },
                dyingHeroIds = it.dyingHeroIds - event.heroId
            ) }
            repo.getRoster().first().find { it.id == event.heroId }?.let { hero ->
                repo.removeHero(hero)
            }
        }
      }
      is FFBattleEvent.FloorComplete -> {
        battleState.update { state ->
          val newBiome = state.dimension?.biomes?.find { event.floor + 1 in it.floorRange }
          val biomeChanged = newBiome != state.currentBiome
          val levelUpText = if (event.leveledUpHeroIds.isNotEmpty()) " (Level Up!)" else ""
          state.copy(
            currentFloor = event.floor + 1,
            currentBiome = newBiome ?: state.currentBiome,
            heroes = event.updatedHeroes,
            gilEarnedThisRun = state.gilEarnedThisRun + event.gilEarned,
            magiciteEarnedThisRun = state.magiciteEarnedThisRun + event.magiciteEarned,
            itemsFoundThisRun = state.itemsFoundThisRun + event.itemsFound,
            showFloorBanner = true,
            floorBannerText = if (biomeChanged) "Entering ${newBiome?.name}..." else "Floor ${event.floor} cleared! +${event.gilEarned}G$levelUpText"
          )
        }
        viewModelScope.launch {
            event.itemsFound.forEach { repo.saveItem(it) }
            event.updatedHeroes.forEach { repo.saveHero(it) }
            repo.updateHighestFloor(event.floor + 1)
        }
        viewModelScope.launch { delay(1500); battleState.update { it.copy(showFloorBanner=false) } }
      }
      is FFBattleEvent.BossDefeated -> {
        battleState.update { state ->
          state.copy(
            showBossBanner = true,
            bossBannerText = "${event.bossName} defeated!${if(event.dimensionComplete) " ✨ Dimension Complete!" else ""}",
            battleLog = (state.battleLog + FFLogEntry("⚔️ BOSS DEFEATED: ${event.bossName}!", LogType.BOSS)).takeLast(25),
            bossesKilledThisRun = state.bossesKilledThisRun + 1
          )
        }
        viewModelScope.launch { delay(3000); battleState.update { it.copy(showBossBanner=false) } }
      }
      is FFBattleEvent.SummonUsed -> {
        battleState.update { state ->
          state.copy(battleLog = (state.battleLog + FFLogEntry(
            "🌟 ${findName(state, event.heroId)} summons ${event.summonName}! Total: ${event.totalDamage} damage!",
            LogType.SUMMON)).takeLast(25))
        }
      }
      is FFBattleEvent.MagiciteStolen -> {
          battleState.update { state ->
              state.copy(
                  magiciteEarnedThisRun = state.magiciteEarnedThisRun + event.amount,
                  battleLog = (state.battleLog + FFLogEntry("${findName(state, event.heroId)} stole ${event.amount} Magicite!", LogType.ABILITY)).takeLast(25)
              )
          }
      }
      is FFBattleEvent.BardSong -> {
           battleState.update { state ->
              state.copy(
                  battleLog = (state.battleLog + FFLogEntry("🎵 ${event.songName}: ${event.effect}", LogType.ABILITY)).takeLast(25)
              )
          }
      }
      is FFBattleEvent.EnemyDefeated -> {
          battleState.update { state ->
              val enemy = state.enemies.find { it.id == event.enemyId }
              val logMsg = "Victory! ${enemy?.name} defeated. +${event.expDropped} EXP"
              state.copy(
                  enemies = state.enemies.filter { it.id != event.enemyId },
                  battleLog = (state.battleLog + FFLogEntry(logMsg, LogType.SYSTEM)).takeLast(25)
              )
          }
      }
      is FFBattleEvent.AllHeroesFell -> {
        viewModelScope.launch {
          val grossEarned = battleState.value.gilEarnedThisRun
          val magicite = battleState.value.magiciteEarnedThisRun
          
          // Death Penalty: Lose 30% of the gold EARNED THIS RUN (Rebalanced from 50%)
          val penalty = (grossEarned * 0.30f).toLong()
          val netGil = grossEarned - penalty
          
          repo.addGil(netGil)
          repo.addMagicite(magicite)
          repo.trackDimensionStats(
              gil = netGil,
              items = battleState.value.itemsFoundThisRun.size,
              bosses = battleState.value.bossesKilledThisRun
          )
          
          // Permadeath: Remove all heroes from party/database since they all fell
          battleState.value.heroes.forEach { hero ->
              repo.removeHero(hero) 
          }
          
          battleState.update { it.copy(
              isRunning = false, 
              runComplete = true, 
              heroes = emptyList(), 
              gilLostToPenalty = penalty,
              battleLog = (it.battleLog + FFLogEntry("💀 TOTAL WIPE: Lost 30% of run earnings.", LogType.HERO_FELL)).takeLast(25)
          ) }
        }
      }
      else -> {}
    }
  }

  fun setSpeed(speed: BattleSpeed) {
    battleState.update { it.copy(speed=speed) }
    val gs = gameState.value ?: return
    val dimension = battleState.value.dimension ?: return
    val relics = RelicBonuses.from(gs)
    val currentParty = battleState.value.heroes
    val currentFloor = battleState.value.currentFloor
    
    battleJob?.cancel()
    battleJob = viewModelScope.launch {
        engine.runBattle(
            heroes = currentParty,
            dimension = dimension,
            startFloor = currentFloor,
            speed = speed,
            relicBonuses = relics
        ) { event -> handleEvent(event) }
    }
  }

  fun retreat() {
    battleJob?.cancel()
    viewModelScope.launch {
      val gil = battleState.value.gilEarnedThisRun
      val magicite = battleState.value.magiciteEarnedThisRun
      repo.addGil(gil)
      repo.addMagicite(magicite)
      repo.trackDimensionStats(
          gil = gil,
          items = battleState.value.itemsFoundThisRun.size,
          bosses = battleState.value.bossesKilledThisRun
      )
      
      // Save current state of surviving heroes and remove dead ones
      battleState.value.heroes.forEach { hero ->
          if (hero.currentHp > 0) repo.saveHero(hero)
          else repo.removeHero(hero)
      }

      battleState.update { it.copy(isRunning=false, runComplete=true) }
    }
  }

  private fun findName(state: FFBattleState, id: String): String {
    val hero = state.heroes.find { it.id == id }
    if (hero != null) return hero.name
    
    val enemy = state.enemies.find { it.id == id }
    if (enemy != null) return enemy.name
    
    // Fallback: check fallen heroes list if not found in active heroes
    val fallen = state.fallenHeroes.find { it.id == id }
    if (fallen != null) return fallen.name
    
    return "?"
  }
  private fun findIsHero(state: FFBattleState, id: String) = state.heroes.any { it.id==id }
}
