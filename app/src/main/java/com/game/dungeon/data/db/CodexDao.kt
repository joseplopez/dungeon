package com.game.dungeon.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CodexDao {
    @Query("SELECT * FROM codex_entries")
    fun getCodexEntries(): Flow<List<CodexEntity>>

    @Query("SELECT * FROM codex_entries")
    suspend fun getAllEntriesOnce(): List<CodexEntity>

    @Query("SELECT * FROM codex_entries WHERE category = :category")
    fun getCodexEntriesByCategory(category: String): Flow<List<CodexEntity>>

    @Query("SELECT * FROM codex_entries WHERE entryId = :entryId")
    fun getEntry(entryId: String): Flow<CodexEntity?>

    @Query("SELECT * FROM codex_entries WHERE entryId = :entryId")
    suspend fun getEntryById(entryId: String): CodexEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: CodexEntity)

    @Transaction
    suspend fun incrementKillCount(entryId: String, category: String = "MONSTER", amount: Int = 1) {
        val existing = getEntryById(entryId)
        if (existing == null) {
            insertOrUpdate(
                CodexEntity(
                    entryId = entryId,
                    category = category,
                    isDiscovered = true,
                    killCount = amount,
                )
            )
        } else {
            insertOrUpdate(
                existing.copy(
                    isDiscovered = true,
                    killCount = existing.killCount + amount,
                )
            )
        }
    }

    @Transaction
    suspend fun discoverEntry(entryId: String, category: String) {
        val existing = getEntryById(entryId)
        if (existing == null) {
            insertOrUpdate(
                CodexEntity(
                    entryId = entryId,
                    category = category,
                    isDiscovered = true,
                    killCount = 0,
                )
            )
        } else if (!existing.isDiscovered) {
            insertOrUpdate(existing.copy(isDiscovered = true))
        }
    }

    @Query("UPDATE codex_entries SET isDiscovered = 1 WHERE entryId = :entryId")
    suspend fun markDiscovered(entryId: String)

    @Query("DELETE FROM codex_entries")
    suspend fun clearAll()
}
