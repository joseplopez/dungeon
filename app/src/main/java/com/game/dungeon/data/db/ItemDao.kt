package com.game.dungeon.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.game.dungeon.data.models.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE ownerId IS NULL")
    fun getInventory(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE ownerId = :heroId")
    fun getItemsForHeroFlow(heroId: String): Flow<List<Item>>

    @Query("UPDATE items SET ownerId = NULL WHERE ownerId = :heroId")
    suspend fun unequipAllFromHero(heroId: String)

    @Upsert
    suspend fun upsert(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("DELETE FROM items")
    suspend fun deleteAll()
}
