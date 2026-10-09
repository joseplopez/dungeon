# Skill: Compendium & Bestiary System (Codex)

## Core Anchors
- Models: `@CodexEntry.kt`, `@MaterialCatalog.kt`, `@MonsterType.kt`
- Persistence: `@CodexEntity.kt`, `@CodexDao.kt`
- Engine & Repo: `@FFBattleEngine.kt`, `@GameRepository.kt`
- ViewModels & UI: `@CodexViewModel.kt`, `@CodexScreen.kt`

## Data Flow & Feature Coupling
- Discovery Hooks: Defeating enemies or looting materials in `FFBattleEngine` triggers discovery/kill calls in `GameRepository`.
- Progression Integration: Codex completion percentages calculate global stat bonuses aggregating into `RelicBonuses.kt`.
- Crafting Interop: Selecting a material in `CodexScreen` provides direct action links to `CraftingScreen`.

## Key Signatures & Execution Steps
1. `recordMonsterKill(monsterTypeId: String)`: Increments kill count and evaluates threshold unlocks (1, 10, 50 kills).
2. `discoverMaterial(materialId: String)`: Flags material as discovered in SQLite.
3. `getCodexCompletionPercentage(): Float`: Calculates discovered entries vs total catalog size.
4. `getBonusFromCodex(): RelicBonuses`: Returns global stat passive multipliers based on milestone tiers.

## Feature Invariants
- Permanent Discovery: Unlocked codex entries and kill counts NEVER reset across Dimension Advances / Prestiges.
- Thread-Safe Event Incrementing: Kill counts and discovery writes during high-speed auto-combat execute asynchronously without blocking the main combat loop.
- Zero Hardcoded Strings: All monster names, descriptions, and category labels resolve via `@res/values/strings.xml`.