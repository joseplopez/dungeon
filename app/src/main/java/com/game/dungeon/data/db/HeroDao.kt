package com.game.dungeon.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.game.dungeon.data.models.Hero
import kotlinx.coroutines.flow.Flow

@Dao
interface HeroDao {
    @Query("SELECT * FROM heroes")
    fun getAllHeroes(): Flow<List<Hero>>

    @Query("SELECT * FROM heroes WHERE isInParty = 1 AND currentHp > 0")
    fun getParty(): Flow<List<Hero>>

    @Upsert
    suspend fun upsert(hero: Hero)

    @Delete
    suspend fun delete(hero: Hero)
}
