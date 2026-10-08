# Skill: Pets & Companions

## Core Anchors
- Models: `@Pet.kt`, `@RelicBonuses.kt`
- State & UI: `@GameState.kt`, `@TownScreen.kt`

## Data Flow & Cross-Feature Coupling
- Active Companion Mapping: Selecting a pet in `TownScreen` updates `activePetId` in `GameState`.
- Combat & Loot Scaling: `RelicBonuses.from(GameState)` ingests active `Pet` traits (Chocobo = speed, Cactuar = crit, Tonberry = gold/drop rate) and applies them directly into `FFBattleEngine`.

## Key Signatures & Execution Flow
1. `setActivePet(petId: String)`: Updates active pet reference in `GameState`.
2. `Pet.getBonuses()`: Returns passive drop/stat multipliers for aggregation into `RelicBonuses`.

## Feature Invariants
- Single Active Slot: Exactly ONE companion can be active at any time (`activePetId`).
- Permanent Unlocks: Unlocked companions persist across Dimension Advances.
