package com.game.dungeon.data.repository

import android.util.Log
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.LeaderboardEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class LeaderboardRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val rootRef = database.getReference("leaderboards")

    suspend fun uploadScore(playerId: String, playerName: String, gameState: GameState, team: List<Hero>) {
        Log.d("LeaderboardRepo", "Uploading score for $playerId ($playerName) - Floor: ${gameState.lifetimeHighestFloor}")
        val data = hashMapOf(
            "playerId" to playerId,
            "playerName" to playerName,
            "maxFloor" to gameState.lifetimeHighestFloor,
            "dimension" to gameState.currentDimension,
            "dimensionMaxFloor" to gameState.highestFloor,
            "totalMagicite" to gameState.totalMagiciteEarned,
            "fastestClearMs" to gameState.fastestClearTime,
            "currentDimTimeMs" to (System.currentTimeMillis() - gameState.dimStartTime),
            "timestamp" to System.currentTimeMillis()
        )
        
        try {
            rootRef.child(playerId).setValue(data).await()
            Log.d("LeaderboardRepo", "Upload SUCCESS")
        } catch (e: Exception) {
            Log.e("LeaderboardRepo", "Upload FAILED: ${e.message}", e)
        }
    }

    suspend fun getGlobalLeaderboard(limit: Int = 50): List<LeaderboardEntry> {
        Log.d("LeaderboardRepo", "Fetching Global Leaderboard...")
        return try {
            val snapshot = rootRef
                .orderByChild("maxFloor")
                .limitToLast(limit)
                .get()
                .await()
            
            val entries = snapshot.children.map { doc ->
                mapToEntry(doc)
            }.reversed().mapIndexed { index, entry -> entry.copy(rank = index + 1) }
            Log.d("LeaderboardRepo", "Global fetch SUCCESS. Count: ${entries.size}")
            entries
        } catch (e: Exception) {
            Log.e("LeaderboardRepo", "Global fetch ERROR: ${e.message}")
            emptyList()
        }
    }

    suspend fun getDimensionLeaderboard(limit: Int = 50): List<LeaderboardEntry> {
        Log.d("LeaderboardRepo", "Fetching Dimension Leaderboard (Global)...")
        return try {
            val snapshot = rootRef
                .orderByChild("dimension")
                .limitToLast(limit)
                .get()
                .await()
            
            val entries = snapshot.children.map { doc ->
                mapToEntry(doc, useDimensionMax = false)
            }.sortedWith(compareByDescending<LeaderboardEntry> { it.dimension }.thenByDescending { it.maxFloor })
                .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
            Log.d("LeaderboardRepo", "Dimension fetch SUCCESS. Count: ${entries.size}")
            entries
        } catch (e: Exception) {
            Log.e("LeaderboardRepo", "Dimension fetch ERROR: ${e.message}")
            emptyList()
        }
    }

    suspend fun getFriendsLeaderboard(friendIds: List<String>, currentUserId: String): List<LeaderboardEntry> {
        Log.d("LeaderboardRepo", "Fetching Friends Leaderboard...")
        val idsToFetch = (friendIds + currentUserId).distinct()
        
        return try {
            val results = mutableListOf<LeaderboardEntry>()
            for (id in idsToFetch) {
                val snapshot = rootRef.child(id).get().await()
                if (snapshot.exists()) {
                    results.add(mapToEntry(snapshot))
                }
            }
            val entries = results.sortedByDescending { it.maxFloor }
                .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
            Log.d("LeaderboardRepo", "Friends fetch SUCCESS. Count: ${entries.size}")
            entries
        } catch (e: Exception) {
            Log.e("LeaderboardRepo", "Friends fetch ERROR: ${e.message}")
            emptyList()
        }
    }

    private fun mapToEntry(snapshot: DataSnapshot, useDimensionMax: Boolean = false): LeaderboardEntry {
        val maxFloorVal = if (useDimensionMax) {
            snapshot.child("dimensionMaxFloor").getValue(Long::class.java)?.toInt() ?: 0
        } else {
            snapshot.child("maxFloor").getValue(Long::class.java)?.toInt() ?: 0
        }

        return LeaderboardEntry(
            rank = 0,
            playerId = snapshot.child("playerId").getValue(String::class.java),
            playerName = snapshot.child("playerName").getValue(String::class.java) ?: "Unknown",
            maxFloor = maxFloorVal,
            dimension = snapshot.child("dimension").getValue(Long::class.java)?.toInt() ?: 1,
            dimensionMaxFloor = snapshot.child("dimensionMaxFloor").getValue(Long::class.java)?.toInt() ?: 0,
            totalMagicite = snapshot.child("totalMagicite").getValue(Long::class.java)?.toInt() ?: 0,
            fastestClearMs = snapshot.child("fastestClearMs").getValue(Long::class.java) ?: 0L,
            currentDimTimeMs = snapshot.child("currentDimTimeMs").getValue(Long::class.java) ?: 0L,
            isUser = false,
            team = emptyList()
        )
    }
}
