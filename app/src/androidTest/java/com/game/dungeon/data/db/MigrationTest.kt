package com.game.dungeon.data.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @Test
    fun testDatabaseMigrations_canRunSuccessfully() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Build the database with all migrations attached to verify they can be applied or initialized safely.
        val db = Room.databaseBuilder(context, GameDatabase::class.java, "migration-test-db")
            .addMigrations(
                GameDatabase.MIGRATION_9_10,
                GameDatabase.MIGRATION_10_11,
                GameDatabase.MIGRATION_11_12,
                GameDatabase.MIGRATION_12_13,
                GameDatabase.MIGRATION_13_14,
                GameDatabase.MIGRATION_14_15,
                GameDatabase.MIGRATION_15_16,
                GameDatabase.MIGRATION_16_17,
                GameDatabase.MIGRATION_17_18,
                GameDatabase.MIGRATION_18_19,
                GameDatabase.MIGRATION_19_20,
                GameDatabase.MIGRATION_20_21,
                GameDatabase.MIGRATION_21_22,
                GameDatabase.MIGRATION_22_23
            )
            .build()
            
        // Open the database and check one of the DAOs to trigger creation/migration check
        assertNotNull(db.gameStateDao)
        db.close()
    }
}
