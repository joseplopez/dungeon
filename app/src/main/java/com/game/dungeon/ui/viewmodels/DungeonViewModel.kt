package com.game.dungeon.ui.viewmodels

import android.app.Activity
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.R
import com.game.dungeon.analytics.AnalyticsManager
import com.game.dungeon.data.models.*
import com.game.dungeon.data.repository.GameRepository
import com.game.dungeon.engine.*
import com.game.dungeon.monetization.AdManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DungeonViewModel @Inject constructor(
    private val repo: GameRepository,
    private val analytics: AnalyticsManager,
    private val adManager: AdManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

  private val engine = FFBattleEngine(context)

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
    val isPaused: Boolean = false,
    val runComplete: Boolean = false,
    val gilEarnedThisRun: Long = 0,
    val magiciteEarnedThisRun: Int = 0,
    val lastFloorGil: Long = 0,
    val lastFloorMagicite: Int = 0,
    val gilLostToPenalty: Long = 0,
    val itemsFoundThisRun: List<Item> = emptyList(),
    val bossesKilledThisRun: Int = 0,
    val fallenHeroes: List<Hero> = emptyList(),
    val dyingHeroIds: Set<String> = emptySet(),
    val originalPartySize: Int = 0,
    val attackingHeroId: String? = null,
    val hitEnemyId: String? = null,
    val hitHeroId: String? = null,
    val isCriticalHit: Boolean = false,
    val recentLoot: Item? = null,
    val showFloorBanner: Boolean = false,
    val floorBannerText: String = "",
    val showBossBanner: Boolean = false,
    val bossBannerText: String = "",
    val boostFloorsRemaining: Int = 0,
    val reviveUsedThisRun: Boolean = false,
    val showReviveDialog: Boolean = false,
    val pendingFallenHeroIds: Set<String> = emptySet()
  )

  data class FFLogEntry(
    @StringRes val messageRes: Int,
    val args: List<Any> = emptyList(),
    val type: LogType,
    val timestamp: Long = System.currentTimeMillis()
  )
  enum class LogType { HERO_ATTACK, ENEMY_ATTACK, ABILITY, SUMMON, HEAL, SYSTEM, BOSS, HERO_FELL }

  fun startRun(party: List<Hero>, startFloor: Int = 1) {
    battleJob?.cancel()
    val gs = gameState.value ?: GameState()
    val dimension = FFDimensionData.getDimension(gs.currentDimension)
    val relics = RelicBonuses.from(gs)

    viewModelScope.launch {
        // Fetch items for all heroes to bake stats
        val partyWithStats = party.map { hero ->
            val items = repo.getEquippedItems(hero.id).first()
            val stats = hero.calculateStats(items, relics)
            
            val newHpBonus = (stats["HP"] ?: hero.baseMaxHp) - hero.baseMaxHp
            val newMpBonus = (stats["MP"] ?: hero.baseMaxMp) - hero.baseMaxMp
            val oldMaxHp = hero.maxHp
            val oldMaxMp = hero.maxMp

            hero.copy(
                currentHp = if (hero.currentHp >= oldMaxHp) hero.baseMaxHp + newHpBonus else hero.currentHp,
                currentMp = if (hero.currentMp >= oldMaxMp) hero.baseMaxMp + newMpBonus else hero.currentMp
            ).apply {
                attackBonus = (stats["ATK"] ?: baseAttack) - baseAttack
                defenseBonus = (stats["DEF"] ?: baseDefense) - baseDefense
                magicBonus = (stats["MAG"] ?: baseMagic) - baseMagic
                hpBonus = newHpBonus
                mpBonus = newMpBonus
                critChance = stats["CRIT_CHANCE"] ?: 5
                critDamage = stats["CRIT_DAMAGE"] ?: 50
            }
        }

        battleState.value = FFBattleState(
            currentFloor = startFloor.coerceAtLeast(1),
            currentBiome = dimension.biomes.find { startFloor.coerceAtLeast(1) in it.floorRange },
            dimension = dimension,
            heroes = partyWithStats,
            originalPartySize = party.size,
            isRunning = true
        )

        analytics.logRunStarted(startFloor.coerceAtLeast(1))

        battleJob = launch {
            engine.runBattle(
                heroes = battleState.value.heroes,
                dimension = dimension,
                startFloor = startFloor.coerceAtLeast(1),
                speed = battleState.value.speed,
                relicBonuses = relics,
                isPaused = { battleState.value.isPaused }
            ) { event -> handleEvent(event) }
        }
    }
  }

  private fun handleEvent(event: FFBattleEvent) {
    when (event) {
      is FFBattleEvent.FloorStart -> {
          val biome = battleState.value.dimension?.biomes?.find { event.floor in it.floorRange }
          battleState.update { it.copy(enemies = event.enemies, currentFloor = event.floor, currentBiome = biome ?: it.currentBiome) }
      }
      is FFBattleEvent.DamageDealt -> {
        battleState.update { state ->
          val isHeroAttacking = findIsHero(state, event.attackerId)
          val attackerName = findName(state, event.attackerId)
          val targetName = findName(state, event.targetId)
          
          val logRes = if (event.isMagic) {
            if (event.isCritical) R.string.log_magic_crit else R.string.log_magic_attack
          } else {
            if (event.isCritical) R.string.log_physical_crit else R.string.log_physical_attack
          }
          
          state.copy(
            heroes = state.heroes.map { h -> if (h.id == event.targetId) h.copy(currentHp = (h.currentHp - event.damage).coerceAtLeast(0)) else h },
            enemies = state.enemies.map { e -> if (e.id == event.targetId) e.copy(currentHp = (e.currentHp - event.damage).coerceAtLeast(0)) else e },
            hitEnemyId = if (!findIsHero(state, event.targetId)) event.targetId else null,
            hitHeroId = if (findIsHero(state, event.targetId)) event.targetId else null,
            attackingHeroId = if (isHeroAttacking) event.attackerId else null,
            isCriticalHit = event.isCritical,
            battleLog = (state.battleLog + FFLogEntry(logRes, listOf(attackerName, targetName, event.damage),
              if(isHeroAttacking) LogType.HERO_ATTACK else LogType.ENEMY_ATTACK)).takeLast(25)
          )
        }
        viewModelScope.launch {
          delay(300) // Increased from 150ms for better animation visibility
          battleState.update { it.copy(hitEnemyId=null, hitHeroId=null, attackingHeroId=null, isCriticalHit=false) }
        }
      }
      is FFBattleEvent.ExpGained -> {
          battleState.update { state ->
              val logEntries = mutableListOf<FFLogEntry>()
              if (event.leveledUp) {
                  logEntries.add(FFLogEntry(R.string.log_level_up, listOf(findName(state, event.heroId), event.newLevel), LogType.SYSTEM))
                  analytics.logHeroLevelUp(
                      heroName = findName(state, event.heroId),
                      job = state.heroes.find { it.id == event.heroId }?.heroClass?.name ?: "Unknown",
                      level = event.newLevel
                  )
              }
              state.copy(
                  heroes = state.heroes.map { h -> 
                      if (h.id == event.heroId) {
                          // The values are already updated in the engine's hero objects,
                          // but state.heroes contains copies.
                          // We need to keep them in sync for the UI.
                          val engineHero = state.heroes.find { it.id == event.heroId }
                          engineHero?.copy(level = event.newLevel) ?: h
                      } else h
                  },
                  battleLog = (state.battleLog + logEntries).takeLast(25)
              )
          }
      }
      is FFBattleEvent.EnemyDefeated -> {
          battleState.update { state ->
              val enemy = state.enemies.find { it.id == event.enemyId }
              state.copy(
                  enemies = state.enemies.filter { it.id != event.enemyId },
                  battleLog = (state.battleLog + FFLogEntry(R.string.log_victory_xp, listOf(enemy?.name ?: "?", event.expDropped), LogType.SYSTEM)).takeLast(25)
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
                  battleLog = (state.battleLog + FFLogEntry(R.string.log_heal_format, listOf(findName(state, event.casterId), findName(state, event.targetId), event.amount), LogType.HEAL)).takeLast(25)
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
                  battleLog = (state.battleLog + FFLogEntry(R.string.log_group_heal, listOf(findName(state, event.casterId)), LogType.HEAL)).takeLast(25)
              )
          }
      }
      is FFBattleEvent.AbilityUsed -> {
          battleState.update { state ->
              state.copy(
                  battleLog = (state.battleLog + FFLogEntry(event.descRes, event.args, LogType.ABILITY)).takeLast(25)
              )
          }
      }
      is FFBattleEvent.HeroFell -> {
        battleState.update { state ->
          val fallen = state.heroes.find { it.id == event.heroId } ?: return@update state
          state.copy(
            dyingHeroIds = state.dyingHeroIds + event.heroId,
            pendingFallenHeroIds = state.pendingFallenHeroIds + event.heroId,
            fallenHeroes = state.fallenHeroes + fallen,
            battleLog = (state.battleLog + FFLogEntry(
              R.string.log_fallen_format,
              listOf(event.heroName),
              LogType.HERO_FELL)).takeLast(25)
          )
        }

        viewModelScope.launch {
            delay(1000)
            battleState.update { it.copy(
                heroes = it.heroes.filter { h -> h.id != event.heroId },
                dyingHeroIds = it.dyingHeroIds - event.heroId
            ) }
            // Removed immediate repo.removeHero(hero) to support Revive
        }
      }
      is FFBattleEvent.FloorComplete -> {
        battleState.update { state ->
          val newBiome = state.dimension?.biomes?.find { event.floor + 1 in it.floorRange }
          val isBiomeStart = newBiome != null && event.floor + 1 == newBiome.floorRange.first
          
          analytics.logFloorReached(event.floor + 1)

          val boostMultiplier = if (state.boostFloorsRemaining > 0) 2 else 1
          val actualGil = event.gilEarned * boostMultiplier
          val actualMagicite = event.magiciteEarned * boostMultiplier

          state.copy(
            currentFloor = event.floor + 1,
            currentBiome = newBiome ?: state.currentBiome,
            heroes = event.updatedHeroes,
            gilEarnedThisRun = state.gilEarnedThisRun + actualGil,
            magiciteEarnedThisRun = state.magiciteEarnedThisRun + actualMagicite,
            lastFloorGil = actualGil,
            lastFloorMagicite = actualMagicite,
            itemsFoundThisRun = state.itemsFoundThisRun + event.itemsFound,
            showFloorBanner = true,
            boostFloorsRemaining = (state.boostFloorsRemaining - 1).coerceAtLeast(0),
            floorBannerText = if (isBiomeStart && newBiome != null) {
                context.getString(R.string.entering_biome_format, context.getString(newBiome.nameRes))
            } else {
                context.getString(R.string.floor_cleared_format, event.floor, actualGil.toInt())
            }
          )
        }
        viewModelScope.launch {
            event.itemsFound.forEach { repo.saveItem(it) }
            
            event.updatedHeroes.forEach { hero ->
                repo.saveHero(hero)
            }

            repo.updateHighestFloor(event.floor + 1)
            
            // Award Job Mastery EXP
            val heroClasses = battleState.value.heroes.map { it.heroClass }.distinct()
            val currentGs = gameState.value ?: GameState()
            var nextGs = currentGs
            heroClasses.forEach {
                nextGs = nextGs.addJobExp(it, 10)
            }
            
            // Award Pet EXP to selected active pet
            currentGs.selectedPet?.let { activePet ->
                nextGs = nextGs.addPetExp(activePet, 20)
            }

            if (nextGs != currentGs) {
                repo.saveGameState(nextGs)
            }
        }
        viewModelScope.launch { delay(1500); battleState.update { it.copy(showFloorBanner=false) } }
      }
      is FFBattleEvent.BossDefeated -> {
        battleState.update { state ->
          analytics.logBossDefeated(event.bossName, state.currentFloor)
          state.copy(
            showBossBanner = true,
            bossBannerText = event.bossName,
            battleLog = (state.battleLog + FFLogEntry(R.string.log_boss_defeated, listOf(event.bossName), LogType.BOSS)).takeLast(25),
            bossesKilledThisRun = state.bossesKilledThisRun + 1
          )
        }
        viewModelScope.launch {
            val currentGs = gameState.value ?: GameState()
            val nextGs = currentGs.copy(bossesDefeatedNames = currentGs.bossesDefeatedNames + event.bossName)
            repo.saveGameState(nextGs)
            delay(3000)
            battleState.update { it.copy(showBossBanner=false) }
        }
      }
      is FFBattleEvent.SummonUsed -> {
        battleState.update { state ->
          state.copy(battleLog = (state.battleLog + FFLogEntry(
            R.string.log_summon_format,
            listOf(findName(state, event.heroId), event.summonName, event.totalDamage),
            LogType.SUMMON)).takeLast(25))
        }
      }
      is FFBattleEvent.MagiciteStolen -> {
          battleState.update { state ->
              state.copy(
                  magiciteEarnedThisRun = state.magiciteEarnedThisRun + event.amount,
                  battleLog = (state.battleLog + FFLogEntry(R.string.log_magicite_stolen, listOf(findName(state, event.heroId), event.amount), LogType.ABILITY)).takeLast(25)
              )
          }
      }
      is FFBattleEvent.BardSong -> {
           battleState.update { state ->
              state.copy(
                  battleLog = (state.battleLog + FFLogEntry(event.effectRes, emptyList(), LogType.ABILITY)).takeLast(25)
              )
          }
      }
      is FFBattleEvent.AllHeroesFell -> {
        if (!battleState.value.reviveUsedThisRun) {
            battleState.update { it.copy(showReviveDialog = true) }
        } else {
            finalizeRun()
        }
      }
      else -> {}
    }
  }

  fun watchBoostAd(activity: Activity) {
      battleState.update { it.copy(isPaused = true) }
      adManager.showRewardedAd(
          activity,
          { battleState.update { it.copy(boostFloorsRemaining = it.boostFloorsRemaining + 10) } },
          { battleState.update { it.copy(isPaused = false) } }
      )
  }

  fun watchReviveAd(activity: Activity) {
      adManager.showRewardedAd(activity) {
          viewModelScope.launch {
              val currentState = battleState.value
              val heroes = repo.getParty().first()
              // Fully heal heroes
              val healedHeroes = heroes.map { it.copy(currentHp = it.maxHp) }
              
              battleState.update { it.copy(
                  heroes = healedHeroes,
                  fallenHeroes = emptyList(),
                  pendingFallenHeroIds = emptySet(),
                  showReviveDialog = false,
                  reviveUsedThisRun = true,
                  isRunning = true,
                  runComplete = false
              ) }

              // Restart the battle from the same floor
              startRun(healedHeroes, currentState.currentFloor)
          }
      }
  }

  fun finalizeRun() {
    viewModelScope.launch {
      val currentState = battleState.value
      val grossEarned = currentState.gilEarnedThisRun
      val magicite = currentState.magiciteEarnedThisRun
      val floor = currentState.currentFloor
      
      analytics.logRunFinished(floor, grossEarned, magicite, if (currentState.heroes.isEmpty()) "DEFEAT" else "RETREAT")

      // Death Penalty
      val penalty = if (currentState.heroes.isEmpty()) (grossEarned * 0.30f).toInt() else 0
      val netGil = (grossEarned - penalty).toLong()
      
      repo.addGil(netGil)
      repo.addMagicite(magicite)
      repo.trackDimensionStats(
          gil = netGil,
          items = currentState.itemsFoundThisRun.size,
          bosses = currentState.bossesKilledThisRun
      )
      
      // Permadeath for those who really died
      currentState.pendingFallenHeroIds.forEach { heroId ->
          repo.getRoster().first().find { it.id == heroId }?.let { hero ->
              repo.removeHero(hero)
          }
      }
      
      battleState.update { it.copy(
          isRunning = false, 
          runComplete = true, 
          showReviveDialog = false,
          gilLostToPenalty = penalty.toLong(),
          battleLog = (it.battleLog + FFLogEntry(if (penalty > 0) R.string.log_total_wipe else R.string.retreat_button, emptyList(), LogType.HERO_FELL)).takeLast(25)
      ) }
      
      repo.triggerFirebaseUpload()
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
            relicBonuses = relics,
            isPaused = { battleState.value.isPaused }
        ) { event -> handleEvent(event) }
    }
  }

  fun retreat() {
    battleJob?.cancel()
    viewModelScope.launch {
      val gil = battleState.value.gilEarnedThisRun
      val magicite = battleState.value.magiciteEarnedThisRun
      val floor = battleState.value.currentFloor

      analytics.logRunFinished(floor, gil, magicite, "RETREAT")

      repo.addGil(gil)
      repo.addMagicite(magicite)
      repo.trackDimensionStats(
          gil = gil,
          items = battleState.value.itemsFoundThisRun.size,
          bosses = battleState.value.bossesKilledThisRun
      )
      
      // Save current state of surviving heroes and remove dead ones
      battleState.value.heroes.forEach { hero ->
          if (hero.currentHp > 0) {
              repo.saveHero(hero)
          } else {
              repo.removeHero(hero)
          }
      }
      
      // Permadeath for those who died during the run before retreating
      battleState.value.pendingFallenHeroIds.forEach { heroId ->
          repo.getRoster().first().find { it.id == heroId }?.let { hero ->
              repo.removeHero(hero)
          }
      }

      battleState.update { it.copy(isRunning=false, runComplete=true) }
      repo.triggerFirebaseUpload()
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
