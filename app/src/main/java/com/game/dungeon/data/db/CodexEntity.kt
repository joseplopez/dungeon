package com.game.dungeon.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "codex_entries")
data class CodexEntity(
    @PrimaryKey val entryId: String,
    val category: String,
    val isDiscovered: Boolean = false,
    val killCount: Int = 0,
)
