# Final Dungeon: Project Overview

**Main Goal**: A pixel art idle dungeon RPG for Android that merges the "hirable/disposable heroes" loop of *Soda Dungeon 2* with the rich job system and thematic progression of the *Final Fantasy* franchise.

## ⚔️ The Lore: The Shattered Dimensions
The world is fractured into **Dimensions**, each echoing a legendary era of the Four Warriors of Light. As the Void consumes reality, heroes are no longer legends—they are resources. 
*   **Grand Capital (Town)**: The thriving hub where permanent progress happens. Players unlock Jobs via Crystals and expand facilities like the Barracks.
*   **The Inn**: A hub outside of time where warriors gather to be hired for dangerous descents. **Freelancers** are always available for free, but specialized Jobs must be unlocked.
*   **The Crystals**: Ancient elemental shards that, when purchased in Town, unlock the knowledge of legendary Jobs (Warrior, Dragoon, Summoner, etc.).
*   **The Relics**: Powerful cosmic artifacts fueled by Magicite that grant permanent stat bonuses transcending the reset of individual Dimensions.

## Core Logic Locations
*   **Combat Engine**: `com.game.dungeon.engine.FFBattleEngine` — A turn-based system utilizing `AIPriority` and speed-based turn orders. Handles 15+ unique Job abilities.
*   **State Management**: `com.game.dungeon.ui.viewmodels`
    *   `DungeonViewModel`: Manages the active run loop, biome transitions, and hero permadeath.
    *   `InnViewModel`: Handles recruitment and party management.
    *   `TownViewModel`: Manages Crystal purchases and permanent facility upgrades.
    *   `RelicsViewModel`: Manages cosmic stat upgrades via Magicite.
*   **Data Persistence**: `com.game.dungeon.data.repository.GameRepository` — Orchestrates data flow between UI and Room SQLite, including atomic resource updates.
*   **Visual System**: `com.game.dungeon.ui.components` — Pure programmatic drawing using Compose `Canvas`. No static image assets are used for units, effects, or parallax backgrounds.

---

## Folder & File Structure

### 📂 `data/models` (The Foundation)
*   **`Hero.kt`**: Stats, job assignments, and name generation. Includes `abilityCharge` for special moves.
*   **`HeroClass.kt` (JobClass)**: Enum for 15+ jobs. **Freelancer** is the entry-level job (0G hire cost).
*   **`Enemy.kt`**: Dynamic generation including Boss scaling and Magicite drop logic.
*   **`RelicType.kt` & `RelicBonuses.kt`**: Definitions for permanent meta-progression scaling.
*   **`FFDimension.kt` & `FFDimensionData.kt`**: Lore and data for FFI, FFII, and FFIII.

### 📂 `data/db` (Persistence)
*   **`GameDatabase.kt`**: Room entry point.
*   **`GameStateDao.kt` / `HeroDao.kt` / `ItemDao.kt`**: SQL definitions with Flow-based reactive updates.

### 📂 `engine` (The Mechanics)
*   **`FFBattleEngine.kt`**: The core logic processing turns, damage, and events. Implements Job-specific logic.

### 📂 `ui/screens` (The Views)
*   *   **`TownScreen.kt`**: A horizontal parallax scrolling hub for the **Crystal Shop** and **Barracks Upgrades**.
*   *   **`InnScreen.kt`**: The recruitment center for hiring disposable heroes. Ensures Jobs are unlocked via Crystals first.
*   *   **`DungeonScreen.kt`**: High-stakes battle view with biome banners, boss pulse effects, and a "Run Complete" memorial.
*   *   **`RelicsScreen.kt`**: A cosmic starfield UI where players spend Magicite on permanent upgrades.

---

## Technical Stack
*   **Jetpack Compose**: 100% Declarative UI with custom `Canvas` rendering.
*   **Parallax Engine**: Custom-built `TownParallaxBackground` for multi-layered scrolling.
*   **Room**: Local SQLite persistence with complex type converters.
*   **Hilt**: Dependency Injection for singletons and ViewModels.
*   **Kotlin Coroutines/Flow**: Asynchronous engine loop and state observation.
*   **Accompanist**: Used for system UI control (transient bars, full-screen landscape).
