package com.game.dungeon.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.game.dungeon.data.models.BattleSpeed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class UserPreferencesRepositoryTest {

    private fun createRepository(): UserPreferencesRepository {
        val file = File.createTempFile("test_settings_", ".preferences_pb")
        file.deleteOnExit()
        val testDataStore = PreferenceDataStoreFactory.create(
            produceFile = { file }
        )
        return UserPreferencesRepository(testDataStore)
    }

    @Test
    fun testDefaultBattleSpeedIsNormal() = runBlocking {
        val repository = createRepository()
        val speed = repository.battleSpeed.first()
        assertEquals(BattleSpeed.NORMAL, speed)
    }

    @Test
    fun testSetAndGetBattleSpeed() = runBlocking {
        val repository = createRepository()

        repository.setBattleSpeed(BattleSpeed.FAST)
        assertEquals(BattleSpeed.FAST, repository.battleSpeed.first())

        repository.setBattleSpeed(BattleSpeed.ULTRAFAST)
        assertEquals(BattleSpeed.ULTRAFAST, repository.battleSpeed.first())
    }
}
