package com.game.dungeon.analytics

import android.os.Bundle
import org.junit.Test
import org.junit.Assert.assertEquals

class AnalyticsManagerTest {

    private class MockAnalyticsManager : AnalyticsManager {
        val loggedEvents = mutableListOf<Pair<String, Bundle?>>()

        override fun logEvent(name: String, params: Bundle?) {
            loggedEvents.add(name to params)
        }

        override fun logRunStarted(floor: Int) {
            val params = Bundle().apply { putInt("start_floor", floor) }
            logEvent("run_started", params)
        }

        override fun logRunFinished(floor: Int, gil: Long, magicite: Int, status: String) {
            val params = Bundle().apply {
                putInt("end_floor", floor)
                putLong("gil_earned", gil)
                putInt("magicite_earned", magicite)
                putString("status", status)
            }
            logEvent("run_finished", params)
        }

        override fun logFloorReached(floor: Int) {
            val params = Bundle().apply { putInt("floor", floor) }
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
            val params = Bundle().apply { putInt("dimension", dimension) }
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

    @Test
    fun testLogEvent() {
        val mock = MockAnalyticsManager()
        mock.logRunStarted(5)
        
        assertEquals(1, mock.loggedEvents.size)
        assertEquals("run_started", mock.loggedEvents[0].first)
        // Note: In local JVM unit tests, Bundle might not work as expected without Robolectric,
        // but since we're testing the logic and not the Android SDK, this is a basic sanity check.
    }
}
