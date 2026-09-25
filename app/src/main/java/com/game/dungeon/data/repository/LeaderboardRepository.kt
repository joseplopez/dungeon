package com.game.dungeon.data.repository

import android.util.Log
import com.game.dungeon.data.models.AIPriority
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.models.LeaderboardEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val rootRef = database.getReference("leaderboards")

    suspend fun uploadScore(playerId: String, playerName: String, gameState: GameState, team: List<Hero>) {
        Log.d("LeaderboardRepo", "Uploading score for $playerId ($playerName) - LifetimeFloor: ${gameState.lifetimeHighestFloor}, DimFloor: ${gameState.highestFloor}")

        val MAX_VALID_TIME_MS = 864000000L // 10 days cap to avoid corrupt 55-year timestamps

        val rawDimTime = if (gameState.dimStartTime <= 0L) 0L else (System.currentTimeMillis() - gameState.dimStartTime)
        val sanitizedDimTime = if (rawDimTime <= 0L || rawDimTime > MAX_VALID_TIME_MS) 0L else rawDimTime
        val sanitizedFastestTime = if (gameState.fastestClearTime > MAX_VALID_TIME_MS) 0L else gameState.fastestClearTime

        try {
            val snapshot = rootRef.child(playerId).get().await()

            val existingMaxFloor = snapshot.child("maxFloor").getValue(Long::class.java)?.toInt() ?: 0
            val existingDim = snapshot.child("dimension").getValue(Long::class.java)?.toInt() ?: 1
            val existingDimMaxFloor = snapshot.child("dimensionMaxFloor").getValue(Long::class.java)?.toInt() ?: 0
            val existingMagicite = snapshot.child("totalMagicite").getValue(Long::class.java)?.toInt() ?: 0

            val currentRunFloor = maxOf(gameState.highestFloor, 0)
            val currentLifetimeMax = maxOf(gameState.lifetimeHighestFloor, currentRunFloor)
            val currentDim = gameState.currentDimension
            val currentMagicite = gameState.totalMagiciteEarned

            val finalMaxFloor = maxOf(currentLifetimeMax, existingMaxFloor)
            val finalDim = maxOf(currentDim, existingDim)
            val finalDimMaxFloor = maxOf(currentRunFloor, existingDimMaxFloor)
            val finalMagicite = maxOf(currentMagicite, existingMagicite)

            // Team should ONLY be updated if:
            // 1) This run's max floor reached or beat the recorded max floor (currentRunFloor >= existingMaxFloor) AND team is non-empty
            // 2) OR no team was previously saved on the server AND team is non-empty
            val existingTeamSnapshot = snapshot.child("team")
            val hasExistingTeam = existingTeamSnapshot.exists() && existingTeamSnapshot.childrenCount > 0L

            val isNewFloorRecord = currentRunFloor >= existingMaxFloor && currentRunFloor > 0
            val shouldUpdateTeam = (isNewFloorRecord || !hasExistingTeam) && team.isNotEmpty()

            val teamData: Any? = when {
                shouldUpdateTeam -> {
                    team.map { hero ->
                        hashMapOf(
                            "name" to hero.name,
                            "heroClass" to hero.heroClass.name,
                            "level" to hero.level
                        )
                    }
                }
                hasExistingTeam -> existingTeamSnapshot.value
                else -> null
            }

            val rawExistingFastest = snapshot.child("fastestClearMs").getValue(Long::class.java) ?: 0L
            val existingFastest = if (rawExistingFastest > MAX_VALID_TIME_MS) 0L else rawExistingFastest

            val finalFastest = when {
                sanitizedFastestTime > 0L && existingFastest > 0L -> minOf(sanitizedFastestTime, existingFastest)
                sanitizedFastestTime > 0L -> sanitizedFastestTime
                else -> existingFastest
            }

            val rawExistingDimTime = snapshot.child("currentDimTimeMs").getValue(Long::class.java) ?: 0L
            val existingDimTime = if (rawExistingDimTime > MAX_VALID_TIME_MS) 0L else rawExistingDimTime

            val finalDimTime = when {
                isNewFloorRecord && sanitizedDimTime > 0L -> sanitizedDimTime
                existingDimTime > 0L -> existingDimTime
                else -> sanitizedDimTime
            }

            val data = hashMapOf<String, Any?>(
                "playerId" to playerId,
                "playerName" to playerName,
                "maxFloor" to finalMaxFloor,
                "dimension" to finalDim,
                "dimensionMaxFloor" to finalDimMaxFloor,
                "totalMagicite" to finalMagicite,
                "fastestClearMs" to finalFastest,
                "currentDimTimeMs" to finalDimTime,
                "timestamp" to System.currentTimeMillis()
            )

            if (teamData != null) {
                data["team"] = teamData
            }

            rootRef.child(playerId).setValue(data).await()
            Log.d("LeaderboardRepo", "Upload SUCCESS for $playerId (MaxFloor: $finalMaxFloor, DimFloor: $finalDimMaxFloor, Team updated: $shouldUpdateTeam)")
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
            }.sortedWith(
                compareByDescending<LeaderboardEntry> { it.maxFloor }
                    .thenByDescending { it.dimension }
                    .thenByDescending { it.totalMagicite }
            ).mapIndexed { index, entry -> entry.copy(rank = index + 1) }

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
            }.sortedWith(
                compareByDescending<LeaderboardEntry> { it.dimension }
                    .thenByDescending { it.dimensionMaxFloor }
                    .thenByDescending { it.maxFloor }
            ).mapIndexed { index, entry -> entry.copy(rank = index + 1) }

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
            val entries = results.sortedWith(
                compareByDescending<LeaderboardEntry> { it.maxFloor }
                    .thenByDescending { it.dimension }
            ).mapIndexed { index, entry -> entry.copy(rank = index + 1) }

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

        val teamList = mutableListOf<Hero>()
        val teamSnapshot = snapshot.child("team")
        if (teamSnapshot.exists()) {
            teamSnapshot.children.forEach { heroDoc ->
                val name = heroDoc.child("name").getValue(String::class.java) ?: "Hero"
                val className = heroDoc.child("heroClass").getValue(String::class.java) ?: "FREELANCER"
                val level = heroDoc.child("level").getValue(Long::class.java)?.toInt() ?: 1
                
                try {
                    val heroClass = HeroClass.valueOf(className)
                    teamList.add(
                        Hero(
                            name = name,
                            heroClass = heroClass,
                            level = level,
                            currentHp = 0,
                            currentMp = 0,
                            aiPriority = AIPriority.ATTACK
                        )
                    )
                } catch (e: Exception) {
                    // Ignore invalid classes
                }
            }
        }

        val MAX_VALID_TIME_MS = 864000000L
        val rawFastest = snapshot.child("fastestClearMs").getValue(Long::class.java) ?: 0L
        val rawCurrentDimTime = snapshot.child("currentDimTimeMs").getValue(Long::class.java) ?: 0L

        return LeaderboardEntry(
            rank = 0,
            playerId = snapshot.child("playerId").getValue(String::class.java),
            playerName = snapshot.child("playerName").getValue(String::class.java) ?: "Unknown",
            maxFloor = maxFloorVal,
            dimension = snapshot.child("dimension").getValue(Long::class.java)?.toInt() ?: 1,
            dimensionMaxFloor = snapshot.child("dimensionMaxFloor").getValue(Long::class.java)?.toInt() ?: 0,
            totalMagicite = snapshot.child("totalMagicite").getValue(Long::class.java)?.toInt() ?: 0,
            fastestClearMs = if (rawFastest > MAX_VALID_TIME_MS) 0L else rawFastest,
            currentDimTimeMs = if (rawCurrentDimTime > MAX_VALID_TIME_MS) 0L else rawCurrentDimTime,
            isUser = false,
            team = teamList
        )
    }
}
