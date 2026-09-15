package com.game.dungeon.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val friendsKey = stringSetPreferencesKey("friend_ids")

    val friendIds: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[friendsKey] ?: emptySet()
    }

    suspend fun addFriend(id: String) {
        dataStore.edit { preferences ->
            val current = preferences[friendsKey] ?: emptySet()
            preferences[friendsKey] = current + id
        }
    }

    suspend fun removeFriend(id: String) {
        dataStore.edit { preferences ->
            val current = preferences[friendsKey] ?: emptySet()
            preferences[friendsKey] = current - id
        }
    }
}
