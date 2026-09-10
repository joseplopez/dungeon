package com.game.dungeon.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.game.dungeon.data.models.GameState
import kotlinx.coroutines.flow.Flow

@Dao
interface GameStateDao {
    @Query("SELECT * FROM game_state WHERE id = 1")
    fun getGameState(): Flow<GameState?>

    @Query("SELECT * FROM game_state WHERE id = 1")
    suspend fun getGameStateOnce(): GameState?

    @Upsert
    suspend fun upsert(state: GameState)
}
