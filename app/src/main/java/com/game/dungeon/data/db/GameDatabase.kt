package com.game.dungeon.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.Item

@Database(entities = [GameState::class, Hero::class, Item::class], version = 12, exportSchema = true)
@TypeConverters(Converters::class)
abstract class GameDatabase : RoomDatabase() {
    abstract val gameStateDao: GameStateDao
    abstract val heroDao: HeroDao
    abstract val itemDao: ItemDao

    companion object {
        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE game_state ADD COLUMN trainingLevel INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE game_state ADD COLUMN planningLevel INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE game_state ADD COLUMN clinicLevel INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE game_state ADD COLUMN lastSaveTime INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Create a temporary table with the NEW schema (including new columns, excluding removed ones)
                db.execSQL("""
                    CREATE TABLE game_state_new (
                        id INTEGER NOT NULL PRIMARY KEY,
                        gold INTEGER NOT NULL,
                        crystals TEXT NOT NULL,
                        magicite INTEGER NOT NULL,
                        currentDimension INTEGER NOT NULL,
                        highestFloor INTEGER NOT NULL,
                        unlockedJobs TEXT NOT NULL,
                        magiciteEarnedThisDim INTEGER NOT NULL,
                        gilEarnedThisDim INTEGER NOT NULL,
                        bossesKilledThisDim INTEGER NOT NULL,
                        itemsFoundThisDim INTEGER NOT NULL,
                        innLevel INTEGER NOT NULL,
                        armoryLevel INTEGER NOT NULL,
                        magicShopLevel INTEGER NOT NULL,
                        barracksLevel INTEGER NOT NULL,
                        vaultLevel INTEGER NOT NULL,
                        pathfinderLevel INTEGER NOT NULL,
                        trainingLevel INTEGER NOT NULL,
                        planningLevel INTEGER NOT NULL,
                        clinicLevel INTEGER NOT NULL,
                        lastSaveTime INTEGER NOT NULL,
                        attackRelic INTEGER NOT NULL,
                        hpRelic INTEGER NOT NULL,
                        mpRelic INTEGER NOT NULL,
                        magicRelic INTEGER NOT NULL,
                        goldRelic INTEGER NOT NULL,
                        magiciteRelic INTEGER NOT NULL,
                        magnetRelic INTEGER NOT NULL,
                        pocketsRelic INTEGER NOT NULL,
                        doubleLootRelic INTEGER NOT NULL
                    )
                """.trimIndent())

                // 2. Copy data from the old table to the new one
                // We provide 0 for new columns and map existing ones
                db.execSQL("""
                    INSERT INTO game_state_new (
                        id, gold, crystals, magicite, currentDimension, highestFloor, unlockedJobs,
                        magiciteEarnedThisDim, gilEarnedThisDim, bossesKilledThisDim, itemsFoundThisDim,
                        innLevel, armoryLevel, magicShopLevel, barracksLevel, vaultLevel, 
                        pathfinderLevel, trainingLevel, planningLevel, clinicLevel, lastSaveTime,
                        attackRelic, hpRelic, mpRelic, magicRelic, goldRelic, magiciteRelic,
                        magnetRelic, pocketsRelic, doubleLootRelic
                    )
                    SELECT 
                        id, gold, crystals, magicite, currentDimension, highestFloor, unlockedJobs,
                        0, 0, 0, 0,
                        innLevel, armoryLevel, magicShopLevel, barracksLevel, vaultLevel,
                        pathfinderLevel, trainingLevel, planningLevel, clinicLevel, lastSaveTime,
                        attackRelic, hpRelic, mpRelic, magicRelic, goldRelic, magiciteRelic,
                        0, 0, 0
                    FROM game_state
                """.trimIndent())

                // 3. Drop the old table
                db.execSQL("DROP TABLE game_state")

                // 4. Rename the new table to the original name
                db.execSQL("ALTER TABLE game_state_new RENAME TO game_state")
            }
        }
    }
}
