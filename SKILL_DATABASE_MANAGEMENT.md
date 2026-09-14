# Skill: Database Migration & Safety Strategy

This document defines the mandatory strategy for any modifications to the Room database in this project. All future database changes must follow these steps.

## 🛡️ Migration Protocol

1.  **Incremental Versioning**: Always increment the database version in `GameDatabase.kt` by exactly 1.
2.  **Explicit Migration Object**: Create a new `MIGRATION_X_Y` object in `GameDatabase.kt`.
3.  **Hilt Integration**: Add the new migration to the `provideGameDatabase` method in `AppModule.kt`.

## 🆘 Fallback & Crash Prevention

To ensure existing users never experience a crash due to a failed migration:

1.  **Try-Catch Wrapper**: Every `migrate(db: SupportSQLiteDatabase)` implementation must be wrapped in a `try-catch` block.
2.  **Emergency Clean**: If a migration step fails (reaches the `catch` block), the strategy is to **clean the database** instead of letting the app crash.
    -   Perform `DELETE FROM [table_name]` for all critical tables.
    -   This allows the app to start fresh for the user, preserving stability at the cost of a reset.
3.  **Room Fallback**: Maintain `.fallbackToDestructiveMigration(true)` in `AppModule.kt` as a secondary safety net.
4.  **Schema Check (Identity Hash Fix)**: If a "Room cannot verify data integrity" error occurs (Identity Hash mismatch), immediately increment the version and add a migration that uses `PRAGMA table_info` to safely ensure all columns exist without crashing if they were already added in a "dirty" state.

## 📝 Example Template

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
