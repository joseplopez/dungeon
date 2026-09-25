package com.game.dungeon.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.Item

@Database(entities = [GameState::class, Hero::class, Item::class], version = 24, exportSchema = true)
@TypeConverters(Converters::class)
abstract class GameDatabase : RoomDatabase() {
    abstract val gameStateDao: GameStateDao
    abstract val heroDao: HeroDao
    abstract val itemDao: ItemDao

    companion object {
        val MIGRATION_23_24 = object : Migration(23, 24) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    // Ensure all columns in game_state
                    ensureColumn(db, "game_state", "id", "INTEGER NOT NULL PRIMARY KEY")
                    ensureColumn(db, "game_state", "gold", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "crystals", "TEXT NOT NULL DEFAULT '{}'")
                    ensureColumn(db, "game_state", "magicite", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "currentDimension", "INTEGER NOT NULL DEFAULT 1")
                    ensureColumn(db, "game_state", "highestFloor", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "totalMagiciteEarned", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "fastestClearTime", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "unlockedJobs", "TEXT NOT NULL DEFAULT '[\"FREELANCER\"]'")
                    ensureColumn(db, "game_state", "magiciteEarnedThisDim", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "dimStartTime", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "gilEarnedThisDim", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "bossesKilledThisDim", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "itemsFoundThisDim", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "innLevel", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "armoryLevel", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "magicShopLevel", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "barracksLevel", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "vaultLevel", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "pathfinderLevel", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "trainingLevel", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "planningLevel", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "clinicLevel", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "lastSaveTime", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "playerId", "TEXT")
                    ensureColumn(db, "game_state", "playerName", "TEXT NOT NULL DEFAULT 'Stranger'")
                    ensureColumn(db, "game_state", "lifetimeHighestFloor", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "totalGilEarned", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "attackRelic", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "hpRelic", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "mpRelic", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "magicRelic", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "defenseRelic", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "critChanceRelic", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "critDamageRelic", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "goldRelic", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "magiciteRelic", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "magnetRelic", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "pocketsRelic", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "doubleLootRelic", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "jobMasteryLevels", "TEXT NOT NULL DEFAULT '{}'")
                    ensureColumn(db, "game_state", "jobMasteryExp", "TEXT NOT NULL DEFAULT '{}'")
                    ensureColumn(db, "game_state", "unlockedPets", "TEXT NOT NULL DEFAULT '[]'")
                    ensureColumn(db, "game_state", "selectedPet", "TEXT")
                    ensureColumn(db, "game_state", "petLevels", "TEXT NOT NULL DEFAULT '{}'")
                    ensureColumn(db, "game_state", "petExp", "TEXT NOT NULL DEFAULT '{}'")
                    ensureColumn(db, "game_state", "bossesDefeatedNames", "TEXT NOT NULL DEFAULT '[]'")
                    ensureColumn(db, "game_state", "notifiedHiddenJobs", "TEXT NOT NULL DEFAULT '[]'")

                    // Ensure all columns in heroes
                    ensureColumn(db, "heroes", "id", "TEXT NOT NULL PRIMARY KEY")
                    ensureColumn(db, "heroes", "heroClass", "TEXT NOT NULL DEFAULT 'FREELANCER'")
                    ensureColumn(db, "heroes", "name", "TEXT NOT NULL DEFAULT 'Hero'")
                    ensureColumn(db, "heroes", "currentHp", "INTEGER NOT NULL DEFAULT 100")
                    ensureColumn(db, "heroes", "currentMp", "INTEGER NOT NULL DEFAULT 10")
                    ensureColumn(db, "heroes", "level", "INTEGER NOT NULL DEFAULT 1")
                    ensureColumn(db, "heroes", "exp", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "heroes", "expToNextLevel", "INTEGER NOT NULL DEFAULT 10")
                    ensureColumn(db, "heroes", "abilityCharge", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "heroes", "aiPriority", "TEXT NOT NULL DEFAULT 'ATTACK'")
                    ensureColumn(db, "heroes", "weaponId", "TEXT")
                    ensureColumn(db, "heroes", "armorId", "TEXT")
                    ensureColumn(db, "heroes", "shieldId", "TEXT")
                    ensureColumn(db, "heroes", "accessory1Id", "TEXT")
                    ensureColumn(db, "heroes", "accessory2Id", "TEXT")
                    ensureColumn(db, "heroes", "isInParty", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "heroes", "partyPosition", "INTEGER NOT NULL DEFAULT 0")

                    // Ensure all columns in items
                    ensureColumn(db, "items", "id", "TEXT NOT NULL PRIMARY KEY")
                    ensureColumn(db, "items", "name", "TEXT NOT NULL DEFAULT 'Item'")
                    ensureColumn(db, "items", "slot", "TEXT NOT NULL DEFAULT 'WEAPON'")
                    ensureColumn(db, "items", "rarity", "TEXT NOT NULL DEFAULT 'COMMON'")
                    ensureColumn(db, "items", "attackBonus", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "items", "defenseBonus", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "items", "magicBonus", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "items", "hpBonus", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "items", "mpBonus", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "items", "critChanceBonus", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "items", "critDamageBonus", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "items", "emoji", "TEXT NOT NULL DEFAULT '🗡️'")
                    ensureColumn(db, "items", "floorFound", "INTEGER NOT NULL DEFAULT 1")
                    ensureColumn(db, "items", "ownerId", "TEXT")
                } catch (e: Exception) {
                    wipeDatabase(db)
                }
            }
        }

        val MIGRATION_22_23 = object : Migration(22, 23) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    ensureColumn(db, "game_state", "totalGilEarned", "INTEGER NOT NULL DEFAULT 0")
                } catch (e: Exception) {
                    wipeDatabase(db)
                }
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    ensureColumn(db, "game_state", "trainingLevel", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "planningLevel", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "clinicLevel", "INTEGER NOT NULL DEFAULT 0")
                } catch (e: Exception) {
                    wipeDatabase(db)
                }
            }
        }

        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    ensureColumn(db, "game_state", "lastSaveTime", "INTEGER NOT NULL DEFAULT 0")
                } catch (e: Exception) {
                    wipeDatabase(db)
                }
            }
        }

        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    ensureColumn(db, "game_state", "magiciteEarnedThisDim", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "gilEarnedThisDim", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "bossesKilledThisDim", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "itemsFoundThisDim", "INTEGER NOT NULL DEFAULT 0")
                } catch (e: Exception) {
                    wipeDatabase(db)
                }
            }
        }

        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    ensureColumn(db, "game_state", "critChanceRelic", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "critDamageRelic", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "heroes", "attackBonus", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "heroes", "defenseBonus", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "heroes", "magicBonus", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "heroes", "hpBonus", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "heroes", "critChance", "INTEGER NOT NULL DEFAULT 5")
                    ensureColumn(db, "heroes", "critDamage", "INTEGER NOT NULL DEFAULT 50")
                    ensureColumn(db, "items", "critChanceBonus", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "items", "critDamageBonus", "INTEGER NOT NULL DEFAULT 0")
                } catch (e: Exception) {
                    wipeDatabase(db)
                }
            }
        }

        val MIGRATION_13_14 = object : Migration(13, 14) {
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

        val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    ensureColumn(db, "game_state", "totalMagiciteEarned", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "fastestClearTime", "INTEGER NOT NULL DEFAULT 0")
                    ensureColumn(db, "game_state", "dimStartTime", "INTEGER NOT NULL DEFAULT 0")
                } catch (e: Exception) {
                    wipeDatabase(db)
                }
            }
        }

        val MIGRATION_17_18 = object : Migration(17, 18) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    ensureColumn(db, "game_state", "playerId", "TEXT")
                    ensureColumn(db, "game_state", "playerName", "TEXT NOT NULL DEFAULT 'Stranger'")
                } catch (e: Exception) {
                    wipeDatabase(db)
                }
            }
        }

        val MIGRATION_18_19 = object : Migration(18, 19) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    ensureColumn(db, "game_state", "lifetimeHighestFloor", "INTEGER NOT NULL DEFAULT 0")
                } catch (e: Exception) {
                    wipeDatabase(db)
                }
            }
        }

        val MIGRATION_19_20 = object : Migration(19, 20) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("CREATE TABLE heroes_new (" +
                            "id TEXT NOT NULL PRIMARY KEY, " +
                            "heroClass TEXT NOT NULL, " +
                            "name TEXT NOT NULL, " +
                            "currentHp INTEGER NOT NULL, " +
                            "currentMp INTEGER NOT NULL, " +
                            "level INTEGER NOT NULL, " +
                            "exp INTEGER NOT NULL, " +
                            "expToNextLevel INTEGER NOT NULL, " +
                            "abilityCharge INTEGER NOT NULL, " +
                            "aiPriority TEXT NOT NULL, " +
                            "weaponId TEXT, " +
                            "armorId TEXT, " +
                            "shieldId TEXT, " +
                            "accessory1Id TEXT, " +
                            "accessory2Id TEXT, " +
                            "isInParty INTEGER NOT NULL, " +
                            "partyPosition INTEGER NOT NULL)")
                    
                    db.execSQL("INSERT INTO heroes_new (id, heroClass, name, currentHp, currentMp, level, exp, expToNextLevel, abilityCharge, aiPriority, weaponId, armorId, shieldId, accessory1Id, accessory2Id, isInParty, partyPosition) " +
                            "SELECT id, heroClass, name, currentHp, currentMp, level, exp, expToNextLevel, abilityCharge, aiPriority, weaponId, armorId, shieldId, accessory1Id, accessory2Id, isInParty, partyPosition FROM heroes")
                    
                    db.execSQL("DROP TABLE heroes")
                    db.execSQL("ALTER TABLE heroes_new RENAME TO heroes")
                } catch (e: Exception) {
                    wipeDatabase(db)
                }
            }
        }

        val MIGRATION_20_21 = object : Migration(20, 21) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    ensureColumn(db, "game_state", "petLevels", "TEXT NOT NULL DEFAULT '{}'")
                    ensureColumn(db, "game_state", "petExp", "TEXT NOT NULL DEFAULT '{}'")
                } catch (e: Exception) {
                    wipeDatabase(db)
                }
            }
        }

        val MIGRATION_21_22 = object : Migration(21, 22) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    ensureColumn(db, "game_state", "bossesDefeatedNames", "TEXT NOT NULL DEFAULT '[]'")
                    ensureColumn(db, "game_state", "notifiedHiddenJobs", "TEXT NOT NULL DEFAULT '[]'")
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
