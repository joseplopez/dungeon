# Skill: Database Migration & Safety Strategy

This document defines the mandatory strategy for any modifications to the Room database in this project. All future database changes must follow these steps.

## 🛡️ Migration Protocol

1.  **INCREMENT VERSION FIRST**: The very first thing to do when a model changes is increment the database version in `GameDatabase.kt` by exactly 1. **DO NOT SKIP THIS.**
2.  **Explicit Migration Object**: Create a new `MIGRATION_X_Y` object in `GameDatabase.kt`.
3.  **Hilt Integration**: Add the new migration to the `provideGameDatabase` method in `AppModule.kt`.
4.  **Verification**: Always use `ensureColumn` to check if a column exists before adding it, to avoid errors if a previous failed run left the schema in a partial state.

## 🆘 Fallback & Crash Prevention

To ensure existing users never experience a crash due to a failed migration:

1.  **Try-Catch Wrapper**: Every `migrate(db: SupportSQLiteDatabase)` implementation must be wrapped in a `try-catch` block.
2.  **Emergency Clean**: If a migration step fails (reaches the `catch` block), the strategy is to **clean the database** instead of letting the app crash.
    -   Perform `DELETE FROM [table_name]` for all critical tables.
    -   This allows the app to start fresh for the user, preserving stability at the cost of a reset.
3.  **Room Fallback**: Maintain `.fallbackToDestructiveMigration(true)` in `AppModule.kt` as a secondary safety net.
4.  **Schema Check (Identity Hash Fix)**: If a "Room cannot verify data integrity" error occurs (Identity Hash mismatch), immediately increment the version and add a migration that uses `PRAGMA table_info` to safely ensure all columns exist without crashing if they were already added in a "dirty" state.

## ⚠️ Removing or Ignoring Columns

When you mark an existing field with `@Ignore` or delete a variable from an `@Entity`, Room expects the column to be **physically removed** from the SQLite database. SQLite does not support `DROP COLUMN` in older versions (or it's unreliable).

**NEVER use a "no-op" (empty) migration when removing fields.** It will cause an `IllegalStateException: Migration didn't properly handle`.

**The Mandatory "Recreate Table" Pattern:**
1.  **Create** a new temporary table with the exact *new* schema (omitting the ignored/deleted columns).
2.  **Copy** data from the old table to the new table using `INSERT INTO ... SELECT ...`.
3.  **Drop** the old table.
4.  **Rename** the temporary table to the original name.

## 📝 Example Template

### Adding a Column
```kotlin
db.execSQL("ALTER TABLE heroes ADD COLUMN new_stat INTEGER NOT NULL DEFAULT 0")
```

### Removing or Ignoring a Column (Recreate Table)
```kotlin
// 1. Create new table
db.execSQL("CREATE TABLE heroes_new (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
// 2. Copy data (only columns you want to keep)
db.execSQL("INSERT INTO heroes_new (id, name) SELECT id, name FROM heroes")
// 3. Drop old
db.execSQL("DROP TABLE heroes")
// 4. Rename
db.execSQL("ALTER TABLE heroes_new RENAME TO heroes")
```

### Safe Migration Wrapper
```kotlin
val MIGRATION_X_Y = object : Migration(X, Y) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE ...")
            // other SQL statements
        } catch (e: Exception) {
            // Emergency fallback: Avoid runtime crashes
            db.execSQL("DELETE FROM game_state")
            db.execSQL("DELETE FROM heroes")
            db.execSQL("DELETE FROM items")
            // Add other tables as necessary
        }
    }
}
```

*Always read this file before modifying models with `@Entity` or changing the database schema.*
