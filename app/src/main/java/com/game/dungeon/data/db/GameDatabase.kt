package com.game.dungeon.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.Item

@Database(entities = [GameState::class, Hero::class, Item::class], version = 9)
@TypeConverters(Converters::class)
abstract class GameDatabase : RoomDatabase() {
    abstract val gameStateDao: GameStateDao
    abstract val heroDao: HeroDao
    abstract val itemDao: ItemDao
}
