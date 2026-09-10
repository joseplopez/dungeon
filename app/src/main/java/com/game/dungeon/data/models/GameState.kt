package com.game.dungeon.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_state")
data class GameState(
    @PrimaryKey val id: Int = 1,
    val gold: Long = 0,
    val crystals: Map<CrystalColor, Boolean> = emptyMap(),  // owned crystals per color
    val essence: Int = 0,
    val magicite: Int = 0,
    val currentDimension: Int = 1,
    val highestFloor: Int = 0,
    val unlockedJobs: Set<JobClass> = setOf(JobClass.FREELANCER),
    // Inn upgrades (replace town buildings)
    val innLevel: Int = 0,           // unlocks advanced jobs
    val armoryLevel: Int = 0,        // better item drops
    val magicShopLevel: Int = 0,     // unlocks magic items
    val barracksLevel: Int = 0,      // increases max party size (3→4→5→6)
    val vaultLevel: Int = 0,         // Gil cap increase
    val pathfinderLevel: Int = 0,    // allows choosing starting floor
    // Relics (powered by Magicite — persist across dimensions)
    val attackRelic: Int = 0,        // +2 ATK per level
    val hpRelic: Int = 0,            // +15 HP per level
    val mpRelic: Int = 0,            // +10 MP per level
    val magicRelic: Int = 0,         // +2 MAG per level
    val goldRelic: Int = 0,          // +5% Gil per level
    val magiciteRelic: Int = 0       // +Magicite find chance per level
)
