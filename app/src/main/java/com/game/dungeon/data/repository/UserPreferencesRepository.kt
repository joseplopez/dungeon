package com.game.dungeon.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.game.dungeon.data.models.BattleSpeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val battleSpeedKey = stringPreferencesKey("battle_speed")

    val battleSpeed: Flow<BattleSpeed> = dataStore.data.map { preferences ->
        val speedName = preferences[battleSpeedKey] ?: BattleSpeed.NORMAL.name
        try {
            BattleSpeed.valueOf(speedName)
        } catch (e: Exception) {
            BattleSpeed.NORMAL
        }
    }

    suspend fun setBattleSpeed(speed: BattleSpeed) {
        dataStore.edit { preferences ->
            preferences[battleSpeedKey] = speed.name
        }
    }
}
