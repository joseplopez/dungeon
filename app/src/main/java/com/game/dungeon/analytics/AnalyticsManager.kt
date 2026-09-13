package com.game.dungeon.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

interface AnalyticsManager {
    fun logEvent(name: String, params: Bundle? = null)
    
    // Helper methods for common events
    fun logRunStarted(floor: Int)
    fun logRunFinished(floor: Int, gil: Long, magicite: Int, status: String)
    fun logFloorReached(floor: Int)
    fun logBossDefeated(bossName: String, floor: Int)
    fun logHeroLevelUp(heroName: String, job: String, level: Int)
    fun logHeroHired(heroName: String, job: String)
    fun logHeroFired(heroName: String, job: String)
    fun logDimensionAdvanced(dimension: Int)
    fun logCrystalPurchased(color: String, job: String)
    fun logTownUpgrade(type: String, level: Int)
    fun logRelicUpgrade(type: String, level: Int)
}

@Singleton
class FirebaseAnalyticsManager @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsManager {

    override fun logEvent(name: String, params: Bundle?) {
        firebaseAnalytics.logEvent(name, params)
    }

    override fun logRunStarted(floor: Int) {
        val params = Bundle().apply {
            putInt("start_floor", floor)
        }
        logEvent("run_started", params)
    }

    override fun logRunFinished(floor: Int, gil: Long, magicite: Int, status: String) {
        val params = Bundle().apply {
            putInt("end_floor", floor)
            putLong("gil_earned", gil)
            putInt("magicite_earned", magicite)
            putString("status", status) // e.g., "RETREAT", "DEFEAT"
        }
        logEvent("run_finished", params)
    }

    override fun logFloorReached(floor: Int) {
        val params = Bundle().apply {
            putInt("floor", floor)
        }
        logEvent("floor_reached", params)
    }

    override fun logBossDefeated(bossName: String, floor: Int) {
        val params = Bundle().apply {
            putString("boss_name", bossName)
            putInt("floor", floor)
        }
        logEvent("boss_defeated", params)
    }

    override fun logHeroLevelUp(heroName: String, job: String, level: Int) {
        val params = Bundle().apply {
            putString("hero_name", heroName)
            putString("job_class", job)
            putInt("new_level", level)
        }
        logEvent("hero_level_up", params)
    }

    override fun logHeroHired(heroName: String, job: String) {
        val params = Bundle().apply {
            putString("hero_name", heroName)
            putString("job_class", job)
        }
        logEvent("hero_hired", params)
    }

    override fun logHeroFired(heroName: String, job: String) {
        val params = Bundle().apply {
            putString("hero_name", heroName)
            putString("job_class", job)
        }
        logEvent("hero_fired", params)
    }

    override fun logDimensionAdvanced(dimension: Int) {
        val params = Bundle().apply {
            putInt("dimension", dimension)
        }
        logEvent("dimension_advanced", params)
    }

    override fun logCrystalPurchased(color: String, job: String) {
        val params = Bundle().apply {
            putString("crystal_color", color)
            putString("unlocked_job", job)
        }
        logEvent("crystal_purchased", params)
    }

    override fun logTownUpgrade(type: String, level: Int) {
        val params = Bundle().apply {
            putString("upgrade_type", type)
            putInt("new_level", level)
        }
        logEvent("town_upgrade_purchased", params)
    }

    override fun logRelicUpgrade(type: String, level: Int) {
        val params = Bundle().apply {
            putString("relic_type", type)
            putInt("new_level", level)
        }
        logEvent("relic_upgraded", params)
    }
}
