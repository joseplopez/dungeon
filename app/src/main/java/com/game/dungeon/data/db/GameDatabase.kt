package com.game.dungeon.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.Item

@Database(entities = [GameState::class, Hero::class, Item::class], version = 16, exportSchema = true)
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
                // Keep the existing migration logic if it already ran or just ensure it's safe
                // This one seems to have tried to add dimension tracking stats
                try {
                    db.execSQL("ALTER TABLE game_state ADD COLUMN magiciteEarnedThisDim INTEGER NOT NULL DEFAULT 0")
                    db.execSQL("ALTER TABLE game_state ADD COLUMN gilEarnedThisDim INTEGER NOT NULL DEFAULT 0")
                    db.execSQL("ALTER TABLE game_state ADD COLUMN bossesKilledThisDim INTEGER NOT NULL DEFAULT 0")
                    db.execSQL("ALTER TABLE game_state ADD COLUMN itemsFoundThisDim INTEGER NOT NULL DEFAULT 0")
                } catch (e: Exception) {}
            }
        }

        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // GameState crit relics
                db.execSQL("ALTER TABLE game_state ADD COLUMN critChanceRelic INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE game_state ADD COLUMN critDamageRelic INTEGER NOT NULL DEFAULT 0")
                
                // Hero bonuses and crit
                db.execSQL("ALTER TABLE heroes ADD COLUMN attackBonus INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE heroes ADD COLUMN defenseBonus INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE heroes ADD COLUMN magicBonus INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE heroes ADD COLUMN hpBonus INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE heroes ADD COLUMN critChance INTEGER NOT NULL DEFAULT 5")
                db.execSQL("ALTER TABLE heroes ADD COLUMN critDamage INTEGER NOT NULL DEFAULT 50")
                
                // Item crit bonuses
                db.execSQL("ALTER TABLE items ADD COLUMN critChanceBonus INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE items ADD COLUMN critDamageBonus INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE game_state ADD COLUMN jobMasteryLevels TEXT NOT NULL DEFAULT '{}'")
                    db.execSQL("ALTER TABLE game_state ADD COLUMN jobMasteryExp TEXT NOT NULL DEFAULT '{}'")
                    db.execSQL("ALTER TABLE game_state ADD COLUMN unlockedPets TEXT NOT NULL DEFAULT '[]'")
                    db.execSQL("ALTER TABLE game_state ADD COLUMN selectedPet TEXT")
                    db.execSQL("ALTER TABLE heroes ADD COLUMN mpBonus INTEGER NOT NULL DEFAULT 0")
                    db.execSQL("ALTER TABLE items ADD COLUMN mpBonus INTEGER NOT NULL DEFAULT 0")
                } catch (e: Exception) {
                    // Safety wipe
                    db.execSQL("DELETE FROM game_state")
                    db.execSQL("DELETE FROM heroes")
                    db.execSQL("DELETE FROM items")
                }
            }
        }

        val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    ensureColumn(db, "game_state", "jobMasteryLevels", "TEXT NOT NULL DEFAULT '{}'")
                    ensureColumn(db, "game_state", "jobMasteryExp", "TEXT NOT NULL DEFAULT '{}'")
                    ensureColumn(db, "game_state", "unlockedPets", "TEXT NOT NULL DEFAULT '[]'")
                    ensureColumn(db, "game_state", "selectedPet", "TEXT")
                    ensureColumn(db, "heroes", "mpBonus", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "items", "mpBonus", "INTEGER NOT NULL DEFAULT 0")
                } catch (e: Exception) {
                    wipeDatabase(db)
                }
            }
        }

        val MIGRATION_15_16 = object : Migration(15, 16) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    ensureColumn(db, "game_state", "defenseRelic", "INTEGER NOT NULL DEFAULT 0")
                } catch (e: Exception) {
                    wipeDatabase(db)
                }
            }
        }

        private fun ensureColumn(db: SupportSQLiteDatabase, table: String, column: String, type: String) {
            val cursor = db.query("PRAGMA table_info($table)")
            var exists = false
            while (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndexOrThrow("name")) == column) {
                    exists = true
                    break
                }
            }
            cursor.close()
            if (!exists) {
                db.execSQL("ALTER TABLE $table ADD COLUMN $column $type")
            }
        }

        private fun wipeDatabase(db: SupportSQLiteDatabase) {
            db.execSQL("DELETE FROM game_state")
            db.execSQL("DELETE FROM heroes")
            db.execSQL("DELETE FROM items")
        }
    }
}
