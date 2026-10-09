package com.game.dungeon.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey val id: String,
    val amount: Int = 0,
)
