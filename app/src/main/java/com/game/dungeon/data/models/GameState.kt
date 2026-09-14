package com.game.dungeon.data.models

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.game.dungeon.R

@Entity(tableName = "game_state")
data class GameState(
    @PrimaryKey val id: Int = 1,
    val gold: Long = 0,
    val crystals: Map<CrystalColor, Boolean> = emptyMap(),  // owned crystals per color
    val magicite: Int = 0,
    val currentDimension: Int = 1,
    val highestFloor: Int = 0,
    val unlockedJobs: Set<JobClass> = setOf(JobClass.FREELANCER),

    // Hall of Fame / Achievement tracking (Resets each dimension)
    val magiciteEarnedThisDim: Int = 0,
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

    // Relics (powered by Magicite — persist across dimensions)
    val attackRelic: Int = 0,        // +2 ATK per level
    val hpRelic: Int = 0,            // +15 HP per level
    val mpRelic: Int = 0,            // +10 MP per level
    val magicRelic: Int = 0,         // +2 MAG per level
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
    val selectedPet: PetType? = null
) {
    val maxGil: Long get() = 10_000L + (vaultLevel * 50_000L)
    val upgradeDiscount: Float get() = planningLevel * 0.05f
    val restDiscount: Float get() = clinicLevel * 0.10f
    val expMultiplier: Float get() = (1.0f + (trainingLevel * 0.10f)) * (if (selectedPet == PetType.MOOGLE) 1.10f else 1.0f)
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
    val petItemFindBonus: Float get() = if (selectedPet == PetType.CHOCOBO) 0.05f else 0f
    val petGilFindBonus: Float get() = if (selectedPet == PetType.CAT) 0.05f else 0f
    val petCritChanceBonus: Int get() = if (selectedPet == PetType.CACTUAR) 2 else 0
    val petCritDamageBonus: Int get() = if (selectedPet == PetType.TONBERRY) 10 else 0
    
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

