package com.game.dungeon.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Query("SELECT * FROM materials")
    fun getMaterials(): Flow<List<MaterialEntity>>

    @Query("SELECT * FROM materials WHERE id = :id")
    fun getMaterial(id: String): Flow<MaterialEntity?>

    @Query("SELECT * FROM materials WHERE id = :id")
    suspend fun getMaterialById(id: String): MaterialEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: MaterialEntity)

    @Transaction
    suspend fun addMaterial(id: String, amountToAdd: Int) {
        if (amountToAdd <= 0) return
        val existing = getMaterialById(id)
        val newAmount = (existing?.amount ?: 0) + amountToAdd
        insertOrUpdate(MaterialEntity(id = id, amount = newAmount))
    }

    @Transaction
    suspend fun deductMaterial(id: String, amountToDeduct: Int): Boolean {
        if (amountToDeduct <= 0) return true
        val existing = getMaterialById(id) ?: return false
        if (existing.amount < amountToDeduct) return false
        val newAmount = existing.amount - amountToDeduct
        insertOrUpdate(MaterialEntity(id = id, amount = newAmount))
        return true
    }

    @Query("DELETE FROM materials WHERE id = :id")
    suspend fun deleteMaterial(id: String)

    @Query("DELETE FROM materials")
    suspend fun clearAll()
}
