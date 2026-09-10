# Final Dungeon: Project Overview

**Main Goal**: A pixel art idle dungeon RPG for Android that merges the "hirable/disposable heroes" loop of *Soda Dungeon 2* with the rich job system and thematic progression of the *Final Fantasy* franchise.

## ⚔️ The Lore: The Shattered Dimensions
The world is fractured into **Dimensions**, each echoing a legendary era of the Four Warriors of Light. As the Void consumes reality, heroes are no longer legends—they are resources. 
*   **Grand Capital (Town)**: The thriving hub where permanent progress happens. Players unlock Jobs via Crystals and expand facilities.
*   **The Inn**: A hub outside of time where warriors gather. **Freelancers** are always free, but advanced Jobs (Tier 2+) require both Crystal unlocks and Town upgrades.
*   **The Crystals**: Ancient elemental shards that unlock legendary Jobs.
*   **The Relics**: Powerful cosmic artifacts fueled by Magicite. 
    *   **Tier 1**: Core stats (ATK, HP, MP, MAG, Gil%, Magicite%).
    *   **Ascended (Tier 2)**: Specialized powers (Magicite Magnet, Deep Pockets gold retention, Boss Double Loot) unlocked by clearing higher Dimensions.

## Core Logic Locations
*   **Combat Engine**: `com.game.dungeon.engine.FFBattleEngine` — A turn-based system utilizing `AIPriority` and speed-based turn orders.
    *   **Loot Logic**: Bosses are guaranteed to drop **RARE or better** items.
*   **Prestige System**: `InnViewModel.advanceDimension` — Handles the "Ascension" process.
    *   **Hall of Fame**: Tracks stats per dimension (Gil, Magicite, Bosses Slain, Items Found).
    *   **Rewards**: Awards a scaling Magicite bonus (50% of earned + 10% per Dimension) and allows keeping a portion of Gold via the "Deep Pockets" relic.
*   **State Management**: `com.game.dungeon.ui.viewmodels`
    *   `DungeonViewModel`: Manages the run loop, biome transitions, and hero permadeath.
    *   `EquipmentViewModel`: Includes **Quick Equip** logic for optimal gear assignment.
*   **Data Persistence**: `com.game.dungeon.data.repository.GameRepository`
    *   **Item Recovery**: Automatically returns all equipped gear to inventory if a hero is fired or falls in battle.
    *   **Migrations**: Room version 12+ includes robust schema recreation to preserve player data across major updates.

---

## Folder & File Structure

### 📂 `data/models` (The Foundation)
*   **`Hero.kt`**: Stats, job assignments, and name generation.
*   **`Item.kt`**: Loot system with `powerScore` for auto-equipping and rarity-based scaling.
*   **`Enemy.kt`**: Includes Boss scaling and Magnet-influenced Magicite drop logic.
*   **`RelicType.kt`**: Definitions for both standard and Ascended permanent upgrades.
*   **`GameState.kt`**: Central state including dimension-specific achievement tracking.

### 📂 `data/db` (Persistence)
*   **`GameDatabase.kt`**: Room entry point with complex migration logic (version 12).
*   **`GameStateDao.kt` / `HeroDao.kt` / `ItemDao.kt`**: SQL definitions with Flow-based reactive updates.

### 📂 `ui/screens` (The Views)
*   **`TownScreen.kt`**: A parallax hub. Features a dense **Crystal Shop** and **Upgrades** UI with dynamic "Max Floor" tooltips for Pathfinder.
*   **`InnScreen.kt`**: Features the **Dimension Advance** flow with the Hall of Fame summary.
*   **`RelicsScreen.kt`**: A dense 3-column grid featuring stat comparison views (`+10 -> +12`) and level progress bars.
*   **`EquipmentScreen.kt`**: A dual-panel view for hero management and inventory, featuring a **Quick Equip** shortcut.

---

## Technical Stack
*   **Jetpack Compose**: 100% Declarative UI with custom `Canvas` rendering.
*   **Room**: Local SQLite persistence with multi-version migration support.
*   **Hilt**: Dependency Injection for singletons and ViewModels.
*   **Kotlin Coroutines/Flow**: Asynchronous engine loop and state observation.
