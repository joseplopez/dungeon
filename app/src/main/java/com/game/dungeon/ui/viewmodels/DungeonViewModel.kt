package com.game.dungeon.ui.viewmodels

import android.app.Activity
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.toLong
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.dungeon.R
import com.game.dungeon.analytics.AnalyticsManager
import com.game.dungeon.data.models.*
import com.game.dungeon.data.repository.GameRepository
import com.game.dungeon.data.repository.UserPreferencesRepository
import com.game.dungeon.engine.*
import com.game.dungeon.monetization.AdManager
import com.game.dungeon.monetization.BillingManager
import com.game.dungeon.monetization.InAppProduct
import com.game.dungeon.ui.components.formatAmount
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DungeonViewModel @Inject constructor(
    private val repo: GameRepository,
    private val userPreferencesRepo: UserPreferencesRepository,
    private val analytics: AnalyticsManager,
    private val adManager: AdManager,
    val billingManager: BillingManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

  private val engine = FFBattleEngine(context)

  val battleState = MutableStateFlow(FFBattleState())
  val gameState = repo.getGameState().stateIn(viewModelScope, SharingStarted.Eagerly, GameState())

  val hasPremiumSpeed: StateFlow<Boolean> = userPreferencesRepo.hasPremiumSpeed
      .stateIn(viewModelScope, SharingStarted.Eagerly, false)

  private val _showPremiumPurchaseDialog = MutableStateFlow(false)
  val showPremiumPurchaseDialog: StateFlow<Boolean> = _showPremiumPurchaseDialog.asStateFlow()

  fun openPremiumPurchase() {
      _showPremiumPurchaseDialog.value = true
      battleState.update { it.copy(isPaused = true) }
  }

  fun closePremiumPurchase() {
      _showPremiumPurchaseDialog.value = false
      battleState.update { it.copy(isPaused = false) }
  }

  fun buyPremiumSpeed(activity: Activity) {
      billingManager.launchBillingFlow(
          activity = activity,
          product = InAppProduct.SPEED_PRODUCT,
          onSuccess = {
              viewModelScope.launch {
                  userPreferencesRepo.setHasPremiumSpeed(true)
                  _showPremiumPurchaseDialog.value = false
                  battleState.update { it.copy(isPaused = false) }
              }
          },
          onError = {
              _showPremiumPurchaseDialog.value = false
              battleState.update { it.copy(isPaused = false) }
          }
      )
  }

  init {
    viewModelScope.launch {
      userPreferencesRepo.battleSpeed.collect { savedSpeed ->
        if (!battleState.value.isRunning) {
          val actualSpeed = if (savedSpeed == BattleSpeed.HYPER && !hasPremiumSpeed.value) BattleSpeed.FAST else savedSpeed
          battleState.update { it.copy(speed = actualSpeed) }
        }
      }
    }
  }

  private var battleJob: Job? = null

  data class AbilityAnimationInfo(
    val id: String = java.util.UUID.randomUUID().toString(),
    val heroClass: HeroClass? = null,
    val abilityLevel: Int = 1,
    val abilityNameRes: Int? = null,
    val isSummon: Boolean = false,
    val summonName: String? = null,
    val isEnemyAttack: Boolean = false,
    val isBossAttack: Boolean = false,
    val monsterType: MonsterType? = null,
    val attackerId: String? = null,
    val targetId: String? = null,
    val durationMs: Long = 400L
  )

  fun calculateAbilityLevel(hero: Hero?): Int {
    if (hero == null) return 1
    val lvl = hero.jobMasteryLevel
    return when {
        lvl >= 40 -> 4
        lvl >= 25 -> 3
        lvl >= 10 -> 2
        lvl >= 1 -> 1
        else -> 1
    }
  }

  data class FloatingLootEffect(
      val id: Long = System.currentTimeMillis(),
      val materialId: String,
      val amount: Int,
      val enemySlotIndex: Int = 0,
      val startTimestamp: Long = System.currentTimeMillis()
  )

  data class RunSummary(
    val currentFloor: Int = 1,
    val gilEarnedThisRun: Long = 0,
    val magiciteEarnedThisRun: Long = 0,
    val gilLostToPenalty: Long = 0,
    val itemsFoundThisRun: List<Item> = emptyList(),
    val materialsEarned: Map<String, Int> = emptyMap(),
    val fallenHeroes: List<Hero> = emptyList(),
    val originalPartySize: Int = 0
  )

  data class FFBattleState(
    val startFloor: Int = 1,
    val currentFloor: Int = 1,
    val runLoopCount: Int = 1,
    val currentBiome: FFBiome? = null,
    val dimension: FFDimension? = null,
    val heroes: List<Hero> = emptyList(),
    val benchedHeroes: List<Hero> = emptyList(),
    val enemies: List<Enemy> = emptyList(),
    val battleLog: List<FFLogEntry> = emptyList(),
    val speed: BattleSpeed = BattleSpeed.NORMAL,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val runComplete: Boolean = false,
    val runSummary: RunSummary? = null,
    val gilEarnedThisRun: Long = 0,
    val magiciteEarnedThisRun: Long = 0,
    val lastFloorGil: Long = 0,
    val lastFloorMagicite: Long = 0,
    val gilLostToPenalty: Long = 0,
    val itemsFoundThisRun: List<Item> = emptyList(),
    val bossesKilledThisRun: Int = 0,
    val defeatedBossNamesThisRun: Set<String> = emptySet(),
    val fallenHeroes: List<Hero> = emptyList(),
    val dyingHeroIds: Set<String> = emptySet(),
    val originalPartySize: Int = 0,
    val attackingHeroId: String? = null,
    val hitEnemyId: String? = null,
    val hitHeroId: String? = null,
    val isCriticalHit: Boolean = false,
    val activeAbilityAnimation: AbilityAnimationInfo? = null,
    val recentLoot: Item? = null,
    val showFloorBanner: Boolean = false,
    val floorBannerText: String = "",
    val showBossBanner: Boolean = false,
    val bossBannerText: String = "",
    val boostFloorsRemaining: Int = 0,
    val reviveUsedThisRun: Boolean = false,
    val showReviveDialog: Boolean = false,
    val showRetreatReviveDialog: Boolean = false,
    val pendingFallenHeroIds: Set<String> = emptySet(),
    val materialsFoundThisRun: Map<String, Int> = emptyMap(),
    val lastDefeatedSlotIndex: Int = 0,
    val floatingLootEffects: List<FloatingLootEffect> = emptyList(),
    val dungeonMode: DungeonMode = DungeonMode.REGULAR,
    val hasRerolledLoot: Boolean = false
  )

  data class FFLogEntry(
    @StringRes val messageRes: Int,
    val args: List<Any> = emptyList(),
    val type: LogType,
    val timestamp: Long = System.currentTimeMillis()
  )
  enum class LogType { HERO_ATTACK, ENEMY_ATTACK, ABILITY, SUMMON, HEAL, SYSTEM, BOSS, HERO_FELL, LOOT }

  fun startRun(party: List<Hero>, startFloor: Int = 1, mode: DungeonMode = DungeonMode.REGULAR) {
    battleJob?.cancel()
    val gs = gameState.value ?: GameState()
    val dimension = FFDimensionData.getDimension(gs.currentDimension)

    viewModelScope.launch {
        val codexBonuses = repo.getCodexBonuses()
        val relics = RelicBonuses.from(gs, codexBonuses)
        val savedSpeed = userPreferencesRepo.battleSpeed.first()

        // --- FETCH BENCH ---
        val partyIds = party.map { it.id }.toSet()
        val allHeroes = repo.getRoster().first()
        val benchedHeroes = allHeroes.filter { it.id !in partyIds }
        // -------------------

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
                attackBonus = (stats["ATK"] ?: baseAttack.toLong()) - baseAttack
                defenseBonus = (stats["DEF"] ?: baseDefense.toLong()) - baseDefense
                magicBonus = (stats["MAG"] ?: baseMagic.toLong()) - baseMagic
                hpBonus = newHpBonus
                mpBonus = newMpBonus
                critChance = stats["CRIT_CHANCE"]?.toInt() ?: 5
                critDamage = stats["CRIT_DAMAGE"] ?: 50
                jobMasteryLevel = relics.jobMasteryLevels[hero.heroClass] ?: 0
            }
        }

        battleState.value = FFBattleState(
            startFloor = startFloor.coerceAtLeast(1),
            currentFloor = startFloor.coerceAtLeast(1),
            currentBiome = dimension.getBiomeForFloor(startFloor.coerceAtLeast(1)),
            benchedHeroes = benchedHeroes,
            dimension = dimension,
            heroes = partyWithStats,
            originalPartySize = party.size,
            speed = savedSpeed,
            isRunning = true,
            dungeonMode = mode
        )

        analytics.logRunStarted(startFloor.coerceAtLeast(1))

        battleJob = launch {
            engine.runBattle(
                heroes = battleState.value.heroes,
                dimension = dimension,
                startFloor = startFloor.coerceAtLeast(1),
                benchedHeroes = battleState.value.benchedHeroes,
                speed = battleState.value.speed,
                relicBonuses = relics,
                mode = mode,
                isPaused = { battleState.value.isPaused }
            ) { event -> handleEvent(event) }
        }
    }
  }

  private fun handleEvent(event: FFBattleEvent) {
    when (event) {
      is FFBattleEvent.FloorStart -> {
          val biome = battleState.value.dimension?.getBiomeForFloor(event.floor)
          battleState.update { it.copy(enemies = event.enemies, currentFloor = event.floor, currentBiome = biome ?: it.currentBiome) }
      }
      is FFBattleEvent.DamageDealt -> {
        val isHeroAttacking = findIsHero(battleState.value, event.attackerId)
        val attackerEnemy = if (!isHeroAttacking) battleState.value.enemies.find { it.id == event.attackerId } else null
        val isBoss = attackerEnemy?.isBoss == true || (battleState.value.dimension != null && FFDimensionData.getBossForFloor(battleState.value.dimension!!, battleState.value.currentFloor) != null && !isHeroAttacking)
        val enemyAnimDuration = if (isBoss) 900L else 400L

        val currentAnim = battleState.value.activeAbilityAnimation
        val animInfo = if (!isHeroAttacking) {
            AbilityAnimationInfo(
                isEnemyAttack = true,
                isBossAttack = isBoss,
                monsterType = attackerEnemy?.type,
                attackerId = event.attackerId,
                targetId = event.targetId,
                durationMs = enemyAnimDuration
            )
        } else if (currentAnim != null && currentAnim.attackerId == event.attackerId && !currentAnim.isEnemyAttack) {
            currentAnim.copy(targetId = event.targetId)
        } else {
            null
        }

        battleState.update { state ->
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
            activeAbilityAnimation = if (!isHeroAttacking) animInfo else (animInfo ?: state.activeAbilityAnimation),
            battleLog = (state.battleLog + FFLogEntry(logRes, listOf(attackerName, targetName, formatAmount(event.damage)),
              if(isHeroAttacking) LogType.HERO_ATTACK else LogType.ENEMY_ATTACK)).takeLast(25)
          )
        }
        viewModelScope.launch {
          val speedFactor = battleState.value.speed.speedFactor
          val delayMs = ((if (isBoss && attackerEnemy != null) 900L else 400L) / speedFactor).coerceAtLeast(100f).toLong()
          delay(delayMs)
          battleState.update {
              if (!isHeroAttacking && it.activeAbilityAnimation?.id == animInfo?.id) {
                  it.copy(hitEnemyId=null, hitHeroId=null, attackingHeroId=null, isCriticalHit=false, activeAbilityAnimation = null)
              } else {
                  it.copy(hitEnemyId=null, hitHeroId=null, attackingHeroId=null, isCriticalHit=false)
              }
          }
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
                          val engineHero = state.heroes.find { it.id == event.heroId }
                          engineHero?.copy(level = event.newLevel) ?: h
                      } else h
                  },
                  battleLog = (state.battleLog + logEntries).takeLast(25),
                  benchedHeroes = state.benchedHeroes.map { h -> if (h.id == event.heroId) h.copy(level = event.newLevel) else h }, // ADD THIS
              )
          }
      }
      is FFBattleEvent.EnemyDefeated -> {
          battleState.update { state ->
              val slotIndex = state.enemies.indexOfFirst { it.id == event.enemyId }.coerceAtLeast(0)
              val enemy = state.enemies.find { it.id == event.enemyId }
              state.copy(
                  lastDefeatedSlotIndex = slotIndex,
                  enemies = state.enemies.filter { it.id != event.enemyId },
                  battleLog = (state.battleLog + FFLogEntry(R.string.log_victory_xp, listOf(enemy?.name ?: "?", formatAmount(event.expDropped)), LogType.SYSTEM)).takeLast(25)
              )
          }
          viewModelScope.launch(Dispatchers.IO) {
              repo.recordMonsterKill(event.monsterType.name)
          }
      }
      is FFBattleEvent.MaterialDropped -> {
          battleState.update { state ->
              val currentCount = state.materialsFoundThisRun[event.materialId] ?: 0
              val mat = MaterialCatalog.getMaterial(event.materialId)
              val matName = runCatching { context.getString(mat.nameRes) }.getOrNull() ?: mat.id
              val materialNameWithEmoji = "${mat.emoji} $matName"
              val lootEffect = FloatingLootEffect(
                  materialId = event.materialId,
                  amount = event.amount,
                  enemySlotIndex = state.lastDefeatedSlotIndex
              )
              state.copy(
                  materialsFoundThisRun = state.materialsFoundThisRun + (event.materialId to (currentCount + event.amount)),
                  floatingLootEffects = state.floatingLootEffects + lootEffect,
                  battleLog = (state.battleLog + FFLogEntry(
                      messageRes = R.string.log_material_dropped,
                      args = listOf(materialNameWithEmoji, event.amount),
                      type = LogType.LOOT
                  )).takeLast(25)
              )
          }
          viewModelScope.launch(Dispatchers.IO) {
              repo.addMaterial(event.materialId, event.amount)
              repo.discoverMaterial(event.materialId)
          }
      }
      is FFBattleEvent.HealCast -> {
          val currentAnim = battleState.value.activeAbilityAnimation
          val updatedAnim = if (currentAnim != null && currentAnim.attackerId == event.casterId && !currentAnim.isEnemyAttack) {
              currentAnim.copy(targetId = event.targetId)
          } else currentAnim

          battleState.update { state ->
              val updatedHeroes = state.heroes.map { h ->
                  if (h.id == event.targetId) h.copy(currentHp = minOf(h.maxHp, h.currentHp + event.amount))
                  else h
              }
              state.copy(
                  heroes = updatedHeroes,
                  activeAbilityAnimation = updatedAnim,
                  battleLog = (state.battleLog + FFLogEntry(R.string.log_heal_format, listOf(findName(state, event.casterId), findName(state, event.targetId), formatAmount(event.amount.toLong())), LogType.HEAL)).takeLast(25)
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
          val hero = battleState.value.heroes.find { h -> h.id == event.heroId }
          val animInfo = AbilityAnimationInfo(
              heroClass = hero?.heroClass ?: HeroClass.WARRIOR,
              abilityLevel = calculateAbilityLevel(hero),
              abilityNameRes = event.nameRes,
              attackerId = event.heroId,
              durationMs = 450L
          )
          battleState.update { state ->
              state.copy(
                  activeAbilityAnimation = animInfo,
                  battleLog = (state.battleLog + FFLogEntry(event.descRes, event.args, LogType.ABILITY)).takeLast(25)
              )
          }
          viewModelScope.launch {
              val speedFactor = battleState.value.speed.speedFactor
              val delayMs = (450L / speedFactor).coerceAtLeast(100f).toLong()
              delay(delayMs)
              battleState.update { if (it.activeAbilityAnimation?.id == animInfo.id) it.copy(activeAbilityAnimation = null) else it }
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
        var isBiomeChange = false
        battleState.update { state ->
          val newBiome = state.dimension?.getBiomeForFloor(event.floor + 1)
          isBiomeChange = newBiome != null && (newBiome.backgroundType != state.currentBiome?.backgroundType || event.floor + 1 == newBiome.floorRange.first)
          
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
              benchedHeroes = event.updatedBenchedHeroes,
            lastFloorGil = actualGil,
            lastFloorMagicite = actualMagicite,
            itemsFoundThisRun = state.itemsFoundThisRun + event.itemsFound,
            showFloorBanner = isBiomeChange,
            boostFloorsRemaining = (state.boostFloorsRemaining - 1).coerceAtLeast(0),
            floorBannerText = if (isBiomeChange && newBiome != null) {
                context.getString(R.string.entering_biome_format, context.getString(newBiome.nameRes))
            } else {
                ""
            }
          )
        }
        viewModelScope.launch {
            event.itemsFound.forEach { repo.saveItem(it) }
            
            event.updatedHeroes.forEach { hero ->
                repo.saveHero(hero)
            }
            event.updatedBenchedHeroes.forEach { hero -> repo.saveHero(hero) }

            // Fetch latest GameState from DB to ensure atomic, non-stale updates
            val currentGs = repo.getGameStateOnce() ?: GameState()
            val newFloor = event.floor + 1
            val isNewDimMax = newFloor > currentGs.highestFloor
            val isNewLifetimeMax = newFloor > currentGs.lifetimeHighestFloor
            val isNewBossLairMax = (battleState.value.dungeonMode == DungeonMode.BOSS_LAIR) && (newFloor > currentGs.highestBossLairFloor)

            var nextGs = currentGs.copy(
                highestFloor = if (isNewDimMax) newFloor else currentGs.highestFloor,
                lifetimeHighestFloor = if (isNewLifetimeMax) newFloor else currentGs.lifetimeHighestFloor
            )

            if (battleState.value.dungeonMode == DungeonMode.BOSS_LAIR) {
                if (newFloor > currentGs.highestBossLairFloor) {
                    nextGs = nextGs.copy(highestBossLairFloor = newFloor)
                }
            }
            
            // Award Job Mastery EXP
            val heroClasses = battleState.value.heroes.map { it.heroClass }.distinct()
            heroClasses.forEach {
                nextGs = nextGs.addJobExp(it, 10)
            }
            
            // Award Pet EXP to selected active pet
            currentGs.selectedPet?.let { activePet ->
                nextGs = nextGs.addPetExp(activePet, 20)
            }

            repo.saveGameState(nextGs)
            if (isNewDimMax || isNewLifetimeMax || isNewBossLairMax) {
                repo.triggerFirebaseUpload(nextGs)
            }
        }
        if (isBiomeChange) {
            viewModelScope.launch { delay(1500); battleState.update { it.copy(showFloorBanner=false) } }
        }
      }
      is FFBattleEvent.BossDefeated -> {
        battleState.update { state ->
          analytics.logBossDefeated(event.bossName, state.currentFloor)
          state.copy(
            showBossBanner = true,
            bossBannerText = event.bossName,
            battleLog = (state.battleLog + FFLogEntry(R.string.log_boss_defeated, listOf(event.bossName), LogType.BOSS)).takeLast(25),
            bossesKilledThisRun = state.bossesKilledThisRun + 1,
            defeatedBossNamesThisRun = state.defeatedBossNamesThisRun + event.bossName
          )
        }
        viewModelScope.launch {
            delay(3000)
            battleState.update { it.copy(showBossBanner=false) }
        }
      }
      is FFBattleEvent.SummonUsed -> {
        val hero = battleState.value.heroes.find { h -> h.id == event.heroId }
        val animInfo = AbilityAnimationInfo(
            heroClass = HeroClass.SUMMONER,
            abilityLevel = calculateAbilityLevel(hero),
            isSummon = true,
            summonName = event.summonName,
            attackerId = event.heroId,
            durationMs = 900L
        )
        battleState.update { state ->
          state.copy(
            activeAbilityAnimation = animInfo,
            battleLog = (state.battleLog + FFLogEntry(
              R.string.log_summon_format,
              listOf(findName(state, event.heroId), event.summonName, formatAmount(event.totalDamage)),
              LogType.SUMMON)).takeLast(25))
        }
        viewModelScope.launch {
            delay(900L)
            battleState.update { if (it.activeAbilityAnimation?.attackerId == event.heroId) it.copy(activeAbilityAnimation = null) else it }
        }
      }
      is FFBattleEvent.MagiciteStolen -> {
          battleState.update { state ->
              state.copy(
                  magiciteEarnedThisRun = state.magiciteEarnedThisRun + event.amount,
                  battleLog = (state.battleLog + FFLogEntry(R.string.log_magicite_stolen, listOf(findName(state, event.heroId), formatAmount(event.amount)), LogType.ABILITY)).takeLast(25)
              )
          }
      }
      is FFBattleEvent.BardSong -> {
          val hero = battleState.value.heroes.find { h -> h.id == event.heroId }
          val animInfo = AbilityAnimationInfo(
              heroClass = HeroClass.BARD,
              abilityLevel = calculateAbilityLevel(hero),
              abilityNameRes = event.songRes,
              attackerId = event.heroId,
              durationMs = 450L
          )
          battleState.update { state ->
              state.copy(
                  activeAbilityAnimation = animInfo,
                  battleLog = (state.battleLog + FFLogEntry(event.effectRes, emptyList(), LogType.ABILITY)).takeLast(25)
              )
          }
          viewModelScope.launch {
              delay(450L)
              battleState.update { if (it.activeAbilityAnimation?.attackerId == event.heroId) it.copy(activeAbilityAnimation = null) else it }
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
          {
              val maxFloor = gameState.value?.highestFloor ?: 1
              val boostFloors = maxOf(10, (maxFloor * 0.05f).toInt())
              battleState.update { it.copy(boostFloorsRemaining = it.boostFloorsRemaining + boostFloors) }
          },
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
                  runComplete = false,
                  runSummary = null
              ) }

              // Restart the battle from the same floor
              startRun(healedHeroes, currentState.currentFloor, currentState.dungeonMode)
          }
      }
  }

  fun watchRetreatReviveAd(activity: Activity) {
      adManager.showRewardedAd(activity) {
          viewModelScope.launch {
              val roster = repo.getRoster().first()
              // Recover fallen heroes with 1 HP so forceRetreat() saves them instead of deleting them
              val rescuedHeroes = battleState.value.pendingFallenHeroIds.mapNotNull { id ->
                  roster.find { it.id == id }?.copy(currentHp = 1) 
              }
              
              battleState.update { it.copy(
                  heroes = it.heroes + rescuedHeroes,
                  pendingFallenHeroIds = emptySet(), // Clears permadeath queue
                  showRetreatReviveDialog = false,
                  reviveUsedThisRun = true
              ) }
              
              forceRetreat() // Safely process the retreat now
          }
      }
  }

  fun declineRetreatRevive() {
      battleState.update { it.copy(showRetreatReviveDialog = false) }
      forceRetreat() // Proceed with permadeath retreat
  }

  fun rerollRunRewards(activity: Activity) {
      adManager.showRewardedAd(
          activity,
          onEarnedReward = {
              viewModelScope.launch {
                  val state = battleState.value
                  val summary = state.runSummary ?: return@launch
                  val gs = repo.getGameStateOnce() ?: GameState()
                  val codex = repo.getCodexBonuses()
                  val relics = RelicBonuses.from(gs, codex)
                  val dimension = FFDimensionData.getDimension(gs.currentDimension)

                  val bossesKilled = state.bossesKilledThisRun
                  val newItems = mutableListOf<Item>()
                  val newMaterials = mutableMapOf<String, Int>()

                  // Recalculate new base values for this run
                  val newGil = summary.gilEarnedThisRun
                  val newMagicite = summary.magiciteEarnedThisRun

                  // Recalculate Item Drops for cleared floors
                  for (f in state.startFloor until state.currentFloor) {
                      val isBoss = FFDimensionData.getBossForFloor(dimension, f) != null
                      val dropChance = if (isBoss) 100f else 10f * (1f + relics.petItemFindBonus)
                      
                      if (kotlin.random.Random.nextFloat() * 100 < dropChance) {
                          newItems.add(Item.random(
                              floor = f,
                              context = context,
                              relicBonuses = relics,
                              minRarity = if (isBoss) Rarity.RARE else Rarity.COMMON,
                              dimension = gs.currentDimension
                          ))
                      }
                  }

                  // Recalculate Boss Material Drops
                  if (bossesKilled > 0) {
                      var shards = 0
                      repeat(bossesKilled) {
                          if (kotlin.random.Random.nextFloat() < 0.25f) {
                              shards += kotlin.random.Random.nextInt(2, 6)
                          }
                      }
                      if (shards > 0) newMaterials["mythic_shard"] = shards
                  }

                  // --- DATABASE REPLACEMENT LOGIC ---
                  
                  // 1. Adjust Gil & Magicite deltas (Difference between new roll and old roll)
                  val gilDelta = newGil - summary.gilEarnedThisRun
                  val magiciteDelta = newMagicite - summary.magiciteEarnedThisRun
                  if (gilDelta != 0L) repo.addGil(gilDelta)
                  if (magiciteDelta != 0L) repo.addMagicite(magiciteDelta)

                  // 2. Remove previously saved items from this run and save the new items
                  summary.itemsFoundThisRun.forEach { oldItem ->
                      repo.deleteItem(oldItem.id)
                  }
                  newItems.forEach { newItem ->
                      repo.saveItem(newItem)
                  }

                  // 3. Remove old materials from this run and add new materials
                  summary.materialsEarned.forEach { (matId, oldQty) ->
                      repo.addMaterial(matId, -oldQty)
                  }
                  newMaterials.forEach { (matId, newQty) ->
                      repo.addMaterial(matId, newQty)
                  }

                  // --- UPDATE RUN SUMMARY STATE (STRICT REPLACEMENT) ---
                  val updatedSummary = summary.copy(
                      gilEarnedThisRun = newGil,
                      magiciteEarnedThisRun = newMagicite,
                      itemsFoundThisRun = newItems,
                      materialsEarned = newMaterials
                  )

                  battleState.update { it.copy(
                      runSummary = updatedSummary,
                      itemsFoundThisRun = newItems,
                      materialsFoundThisRun = newMaterials,
                      hasRerolledLoot = true
                  ) }
              }
          },
          onAdClosed = {}
      )
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
      val netGil = (grossEarned - penalty)
      
      repo.addGil(netGil)
      repo.addMagicite(magicite)
      repo.trackDimensionStats(
          gil = netGil,
          items = currentState.itemsFoundThisRun.size,
          bosses = currentState.bossesKilledThisRun
      )
      repo.recordBossDefeats(currentState.defeatedBossNamesThisRun)
      
      val gs = repo.getGameStateOnce() ?: GameState()
      val isProtectionActive = gs.isPermadeathProtectionActive

      // Permadeath for those who really died
      currentState.pendingFallenHeroIds.forEach { heroId ->
          repo.getRoster().first().find { it.id == heroId }?.let { hero ->
              if (!isProtectionActive) {
                  repo.removeHero(hero)
              } else {
                  repo.saveHero(hero.copy(currentHp = hero.maxHp))
              }
          }
      }
      
      val summary = RunSummary(
          currentFloor = floor,
          gilEarnedThisRun = netGil,
          magiciteEarnedThisRun = magicite,
          gilLostToPenalty = penalty.toLong(),
          itemsFoundThisRun = currentState.itemsFoundThisRun,
          materialsEarned = currentState.materialsFoundThisRun,
          fallenHeroes = currentState.fallenHeroes,
          originalPartySize = currentState.originalPartySize
      )
      
      battleState.update { it.copy(
          isRunning = false, 
          runComplete = true, 
          showReviveDialog = false,
          gilLostToPenalty = penalty.toLong(),
          runSummary = summary,
          battleLog = (it.battleLog + FFLogEntry(if (penalty > 0) R.string.log_total_wipe else R.string.retreat_button, emptyList(), LogType.HERO_FELL)).takeLast(25)
      ) }
      
      if (currentState.dungeonMode == DungeonMode.BOSS_LAIR) {
          repo.updateHighestBossLairFloor(floor)
      } else {
          repo.triggerFirebaseUpload()
      }
    }
  }

  fun togglePause() {
    battleState.update { it.copy(isPaused = !it.isPaused) }
  }

  fun setPaused(paused: Boolean) {
    battleState.update { it.copy(isPaused = paused) }
  }

  fun setSpeed(speed: BattleSpeed) {
    battleState.update { it.copy(speed=speed) }
    viewModelScope.launch {
        userPreferencesRepo.setBattleSpeed(speed)
    }
    val gs = gameState.value ?: return
    val dimension = battleState.value.dimension ?: return
    val currentParty = battleState.value.heroes
    val currentFloor = battleState.value.currentFloor

    battleJob?.cancel()
    battleJob = viewModelScope.launch {
        val codexBonuses = repo.getCodexBonuses()
        val relics = RelicBonuses.from(gs, codexBonuses)
        engine.runBattle(
            heroes = currentParty,
            dimension = dimension,
            startFloor = currentFloor,
            speed = speed,
            relicBonuses = relics,
            benchedHeroes = battleState.value.benchedHeroes,
            mode = battleState.value.dungeonMode,
            isPaused = { battleState.value.isPaused }
        ) { event -> handleEvent(event) }
    }
  }

  fun retreat() {
      battleJob?.cancel()
      battleState.update { it.copy(showRetreatReviveDialog = true) }
  }
    fun retryRun() {
        battleJob?.cancel()
        viewModelScope.launch {
            val state = battleState.value
            val gs = repo.getGameStateOnce() ?: GameState()
            val dimension = FFDimensionData.getDimension(gs.currentDimension)
            val codexBonuses = repo.getCodexBonuses()
            val relics = RelicBonuses.from(gs, codexBonuses)

            // Gather all hero IDs that were in this run (both alive and dead)
            val allRunHeroIds = state.heroes.map { it.id }.toSet() + state.pendingFallenHeroIds

            // Fetch raw heroes from roster
            val roster = repo.getRoster().first()
            val rawParty = roster.filter { it.id in allRunHeroIds }

            // Recalculate stats with equipment and heal fully for the loop
            val partyWithStats = rawParty.map { hero ->
                val items = repo.getEquippedItems(hero.id).first()
                val stats = hero.calculateStats(items, relics)

                val newHpBonus = (stats["HP"] ?: hero.baseMaxHp) - hero.baseMaxHp
                val newMpBonus = (stats["MP"] ?: hero.baseMaxMp) - hero.baseMaxMp

                hero.copy(
                    currentHp = hero.baseMaxHp + newHpBonus, // Fully heal
                    currentMp = hero.baseMaxMp + newMpBonus
                ).apply {
                    attackBonus = (stats["ATK"] ?: baseAttack.toLong()) - baseAttack
                    defenseBonus = (stats["DEF"] ?: baseDefense.toLong()) - baseDefense
                    magicBonus = (stats["MAG"] ?: baseMagic.toLong()) - baseMagic
                    hpBonus = newHpBonus
                    mpBonus = newMpBonus
                    critChance = stats["CRIT_CHANCE"]?.toInt() ?: 5
                    critDamage = stats["CRIT_DAMAGE"] ?: 50
                    jobMasteryLevel = relics.jobMasteryLevels[hero.heroClass] ?: 0
                }
            }

            battleState.update { it.copy(
                heroes = partyWithStats,
                pendingFallenHeroIds = emptySet(),
                fallenHeroes = emptyList(),
                dyingHeroIds = emptySet(),
                currentFloor = state.startFloor,
                runLoopCount = state.runLoopCount + 1,
                showRetreatReviveDialog = false,
                showReviveDialog = false,
                isRunning = true,
                runComplete = false
            ) }

            // Restart battle loop directly (keeps Gil/Magicite accumulators intact)
            battleJob = launch {
                engine.runBattle(
                    heroes = partyWithStats,
                    dimension = dimension,
                    startFloor = state.startFloor,
                    speed = state.speed,
                    relicBonuses = relics,
                    benchedHeroes = battleState.value.benchedHeroes,
                    mode = state.dungeonMode,
                    isPaused = { battleState.value.isPaused }
                ) { event -> handleEvent(event) }
            }
        }
    }

  private fun forceRetreat() {
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
      repo.recordBossDefeats(battleState.value.defeatedBossNamesThisRun)
      
      val gs = repo.getGameStateOnce() ?: GameState()
      val isProtectionActive = gs.isPermadeathProtectionActive

      battleState.value.benchedHeroes.forEach { hero ->
          repo.saveHero(hero)
      }

      // Save current state of surviving heroes and remove/restore dead ones
      battleState.value.heroes.forEach { hero ->
          if (hero.currentHp > 0) {
              repo.saveHero(hero)
          } else {
              if (!isProtectionActive) {
                  repo.removeHero(hero)
              } else {
                  repo.saveHero(hero.copy(currentHp = hero.maxHp))
              }
          }
      }
      
      // Permadeath for those who died during the run before retreating
      battleState.value.pendingFallenHeroIds.forEach { heroId ->
          repo.getRoster().first().find { it.id == heroId }?.let { hero ->
              if (!isProtectionActive) {
                  repo.removeHero(hero)
              } else {
                  repo.saveHero(hero.copy(currentHp = hero.maxHp))
              }
          }
      }

      val currentState = battleState.value
      val summary = RunSummary(
          currentFloor = currentState.currentFloor,
          gilEarnedThisRun = currentState.gilEarnedThisRun,
          magiciteEarnedThisRun = currentState.magiciteEarnedThisRun,
          gilLostToPenalty = 0,
          itemsFoundThisRun = currentState.itemsFoundThisRun,
          materialsEarned = currentState.materialsFoundThisRun,
          fallenHeroes = currentState.fallenHeroes,
          originalPartySize = currentState.originalPartySize
      )

      battleState.update { it.copy(
          isRunning=false, 
          runComplete=true,
          runSummary = summary
      ) }
      if (currentState.dungeonMode == DungeonMode.BOSS_LAIR) {
          repo.updateHighestBossLairFloor(floor)
      } else {
          repo.triggerFirebaseUpload()
      }
    }
  }

  fun removeFloatingLootEffect(id: Long) {
      battleState.update { state ->
          state.copy(floatingLootEffects = state.floatingLootEffects.filter { it.id != id })
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
