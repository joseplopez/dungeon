package com.game.dungeon.data.models

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.game.dungeon.BuildConfig
import com.game.dungeon.R

@Entity(tableName = "game_state")
data class GameState(
    @PrimaryKey val id: Int = 1,
    val gold: Long = BuildConfig.INITIAL_GIL,
    val crystals: Map<CrystalColor, Boolean> = emptyMap(),  // owned crystals per color
    val magicite: Int = BuildConfig.INITIAL_MAGICITE,
    val currentDimension: Int = 1,
    val highestFloor: Int = 0,
    val totalMagiciteEarned: Int = 0,
    val fastestClearTime: Long = 0, // In milliseconds, 0 means not cleared yet
    val unlockedJobs: Set<HeroClass> = setOf(HeroClass.FREELANCER),

    // Hall of Fame / Achievement tracking (Resets each dimension)
    val magiciteEarnedThisDim: Int = 0,
    val dimStartTime: Long = System.currentTimeMillis(),
    val gilEarnedThisDim: Long = 0,
    val bossesKilledThisDim: Int = 0,
    val itemsFoundThisDim: Int = 0,
    
    // Inn upgrades (replace town buildings)
    val innLevel: Int = 0,           // unlocks advanced jobs (Max 1)
    val armoryLevel: Int = 0,        // better item stats (Max 20)
    val magicShopLevel: Int = 0,     // better rarity odds (Max 10)
    val barracksLevel: Int = 0,      // increases max party size 3→4→5 (Max 2)
    val vaultLevel: Int = 0,         // Gil cap increase (Max 10)
    val pathfinderLevel: Int = 0,    // allows choosing starting floor (Max 4)
    val trainingLevel: Int = 0,      // +10% EXP gained (Max 10)
    val planningLevel: Int = 0,      // -5% upgrade costs (Max 5)
    val clinicLevel: Int = 0,        // -10% rest cost (Max 10)
    val lastSaveTime: Long = 0,      // Track last activity
    val playerId: String? = null,    // Firebase UID
    val playerName: String = "Stranger", // Player display name
    val lifetimeHighestFloor: Int = 0, // Absolute maximum floor across all dimensions
    val totalGilEarned: Long = 0, // Lifetime gold earned

    // Relics (powered by Magicite — persist across dimensions)
    val attackRelic: Int = 0,        // +2 ATK per level
    val hpRelic: Int = 0,            // +15 HP per level
    val mpRelic: Int = 0,            // +10 MP per level
    val magicRelic: Int = 0,         // +2 MAG per level
    val defenseRelic: Int = 0,       // +2 DEF per level
    val critChanceRelic: Int = 0,    // +1% Crit Chance per level
    val critDamageRelic: Int = 0,    // +5% Crit Damage per level
    val goldRelic: Int = 0,          // +5% Gil per level
    val magiciteRelic: Int = 0,      // +Magicite find chance per level

    // Ascended Relics (unlocked by clearing dimensions)
    val magnetRelic: Int = 0,       // +5% base magicite drop rate
    val pocketsRelic: Int = 0,      // keep 10% gil on reset
    val doubleLootRelic: Int = 0,   // +5% boss double drop chance

    // Global Job Masteries & Pets
    val jobMasteryLevels: Map<HeroClass, Int> = emptyMap(),
    val jobMasteryExp: Map<HeroClass, Int> = emptyMap(),
    val unlockedPets: Set<PetType> = emptySet(),
    val selectedPet: PetType? = null,
    val petLevels: Map<PetType, Int> = emptyMap(),
    val petExp: Map<PetType, Int> = emptyMap(),
    val bossesDefeatedNames: Set<String> = emptySet(),
    val notifiedHiddenJobs: Set<HeroClass> = emptySet()
) {
    val maxGil: Long get() = 10_000L + (vaultLevel * 50_000L)
    val upgradeDiscount: Float get() = planningLevel * 0.05f
    val restDiscount: Float get() = clinicLevel * 0.10f
    val expMultiplier: Float get() = (1.0f + (trainingLevel * 0.10f)) * 
            (if (selectedPet == PetType.MOOGLE) 1.0f + getPetBonusValue(PetType.MOOGLE) else 1.0f)
    val itemStatBonus: Float get() = armoryLevel * 0.05f

    // Bonus getters for ascended relics
    val magnetBonus: Float get() = magnetRelic * 0.05f
    val pocketsBonus: Float get() = pocketsRelic * 0.10f
    val doubleLootChance: Int get() = doubleLootRelic * 5

    // Mastery Helpers
    fun getMasteryLevel(heroClass: HeroClass): Int = jobMasteryLevels[heroClass] ?: 0
    fun getMasteryBonus(heroClass: HeroClass): Int = getMasteryLevel(heroClass) * heroClass.masteryBonusPerLevel
    fun getMasteryExp(heroClass: HeroClass): Int = jobMasteryExp[heroClass] ?: 0
    fun getMasteryNextLevelExp(heroClass: HeroClass): Int = (getMasteryLevel(heroClass) + 1) * 100

    fun addJobExp(heroClass: HeroClass, amount: Int): GameState {
        val currentExp = getMasteryExp(heroClass)
        val currentLevel = getMasteryLevel(heroClass)
        
        var newExp = currentExp + amount
        var newLevel = currentLevel
        
        while (newExp >= (newLevel + 1) * 100) {
            newExp -= (newLevel + 1) * 100
            newLevel++
        }
        
        return copy(
            jobMasteryLevels = jobMasteryLevels.toMutableMap().apply { put(heroClass, newLevel) },
            jobMasteryExp = jobMasteryExp.toMutableMap().apply { put(heroClass, newExp) }
        )
    }

    // Pet Helpers
    fun getPetLevel(petType: PetType): Int = (petLevels[petType] ?: 0).coerceAtLeast(1)
    fun getPetExp(petType: PetType): Int = petExp[petType] ?: 0
    fun getPetNextLevelExp(petType: PetType): Int = getPetLevel(petType) * 200

    fun getPetBonusValue(petType: PetType): Float {
        val level = getPetLevel(petType)
        val base = petType.bonusValue
        // Scale by +10% of base value per level above 1
        return base * (1f + (level - 1) * 0.10f)
    }

    val petItemFindBonus: Float get() = if (selectedPet == PetType.CHOCOBO) getPetBonusValue(PetType.CHOCOBO) else 0f
    val petGilFindBonus: Float get() = if (selectedPet == PetType.CAT) getPetBonusValue(PetType.CAT) else 0f
    val petCritChanceBonus: Int get() = if (selectedPet == PetType.CACTUAR) getPetBonusValue(PetType.CACTUAR).toInt() else 0
    val petCritDamageBonus: Int get() = if (selectedPet == PetType.TONBERRY) getPetBonusValue(PetType.TONBERRY).toInt() else 0

    fun addPetExp(petType: PetType, amount: Int): GameState {
        val currentExp = getPetExp(petType)
        val currentLevel = getPetLevel(petType)
        
        var newExp = currentExp + amount
        var newLevel = currentLevel
        
        while (newExp >= newLevel * 200) {
            newExp -= newLevel * 200
            newLevel++
        }
        
        return copy(
            petLevels = petLevels.toMutableMap().apply { put(petType, newLevel) },
            petExp = petExp.toMutableMap().apply { put(petType, newExp) }
        )
    }

    val allStandardCrystalsUnlocked: Boolean get() {
        val standardColors = CrystalColor.entries.filter { it != CrystalColor.CLEAR && it != CrystalColor.HIDDEN }
        return standardColors.all { crystals[it] == true }
    }

    fun isJobDiscovered(job: HeroClass): Boolean {
        if (job.tier < 3) return true
        return when(job) {
            HeroClass.ONION_KNIGHT -> jobMasteryLevels.values.sum() >= 100
            HeroClass.MIME -> currentDimension >= 4
            HeroClass.NECROMANCER -> lifetimeHighestFloor >= 1000
            HeroClass.BLUE_MAGE -> bossesDefeatedNames.size >= 10
            else -> false
        }
    }

    fun isJobUnlocked(job: HeroClass): Boolean {
        if (job == HeroClass.FREELANCER) return true
        return unlockedJobs.contains(job)
    }
    
    fun getMaxPartySize(): Int = (3 + barracksLevel).coerceAtMost(5)
}

enum class UpgradeType(
    @StringRes val nameRes: Int,
    @StringRes val descRes: Int,
    val baseCost: Int,
    val maxLevel: Int,
    val emoji: String
) {
    INN(R.string.upgrade_inn_name, R.string.upgrade_inn_desc, 500, 1, "🍺"),
    BARRACKS(R.string.upgrade_barracks_name, R.string.upgrade_barracks_desc, 1000, 2, "🏕"),
    VAULT(R.string.upgrade_vault_name, R.string.upgrade_vault_desc, 300, 10, "🏦"),
    ARMORY(R.string.upgrade_armory_name, R.string.upgrade_armory_desc, 400, 20, "🛡️"),
    MAGIC_SHOP(R.string.upgrade_magic_shop_name, R.string.upgrade_magic_shop_desc, 600, 10, "🔮"),
    TRAINING(R.string.upgrade_training_name, R.string.upgrade_training_desc, 500, 10, "📈"),
    PLANNING(R.string.upgrade_planning_name, R.string.upgrade_planning_desc, 1000, 5, "🏗"),
    CLINIC(R.string.upgrade_clinic_name, R.string.upgrade_clinic_desc, 200, 10, "🏥"),
    PATHFINDER(R.string.upgrade_pathfinder_name, R.string.upgrade_pathfinder_desc, 800, 4, "🧭")
}

