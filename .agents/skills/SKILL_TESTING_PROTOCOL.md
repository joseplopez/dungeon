# Skill: Testing & Verification Protocol

This document defines the mandatory testing standards, test commands, and verification workflows for modifications across the repository.

---

## 🧪 Testing Layers

### 1. Unit Tests (`app/src/test/`)
Fast, JVM-based unit tests for business logic, math calculations, combat rules, repositories, and ViewModels.
- **Run all unit tests**:
  ```bash
  ./gradlew :app:testDebugUnitTest
  ```
- **Run a specific unit test class**:
  ```bash
  ./gradlew :app:testDebugUnitTest --tests "com.game.dungeon.engine.FFBattleEngineTest"
  ```
- **Key Unit Test Suites**:
  - `FFBattleEngineTest`, `FFBattleEngineDropTest`: Turn loops, drop probabilities, boss drops.
  - `ItemCraftingTest`, `ItemLockTest`: Enhancement levels, gem socketing, locked items.
  - `MythicSetTest`, `HeroSetBonusTest`: Set bonus stat additions and multiplications.
  - `PermadeathProtectionTest`: Hero recovery and survival on fatal runs.
  - `MasteryTest`, `JobAbilitiesTest`: Mastery scaling and ability charge mechanics.
  - `GameRepositoryCraftingTest`, `GameRepositoryCodexTest`: Persistence and repository operations.

### 2. Room Database Migration Tests (`app/src/androidTest/`)
Mandatory when creating new Room migrations in `GameDatabase.kt`.
- **Run Migration Tests**:
  ```bash
  ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.game.dungeon.data.db.MigrationTest
  ```
- Verifies that SQLite schemas update cleanly from previous versions without crashes or data loss.

### 3. UI & Biome Instrumentation Tests (`app/src/androidTest/`)
Visual and component rendering verification using Hilt (`HiltTestRunner`).
- **Run Biome Canvas Tests**:
  ```bash
  ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.game.dungeon.ui.BiomeUITest
  ```
- **Run UI Screen Tests**:
  - `DungeonUITest`, `TownUITest`, `EquipmentUITest`, `MasteryUITest`, `PathfinderUITest`.

---

## 🛠️ Compilation & Static Analysis

Before submitting any code change:
1. Verify clean Kotlin compilation:
   ```bash
   ./gradlew :app:compileDebugKotlin
   ```
2. Verify full debug APK assembly:
   ```bash
   ./gradlew :app:assembleDebug
   ```

---

## 📋 Pre-Commit Verification Checklist

- [ ] Code compiles cleanly with `./gradlew :app:compileDebugKotlin`.
- [ ] Relevant unit tests pass with `./gradlew :app:testDebugUnitTest`.
- [ ] If an `@Entity` was updated, database version was incremented, migration script was tested in `MigrationTest`, and `ensureColumn` checks were implemented.
- [ ] Zero hardcoded strings in Compose screens (verified against `strings.xml`).
- [ ] Invariants verified (Hero death item recovery, combat stat formula, boss RARE+ loot).
