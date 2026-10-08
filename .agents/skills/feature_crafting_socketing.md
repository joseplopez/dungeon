# Skill: Relics & Town Upgrades

## Core Anchors
- ViewModels: `@RelicsViewModel.kt`, `@TownViewModel.kt`
- Models & Repo: `@RelicType.kt`, `@RelicBonuses.kt`, `@GameState.kt`, `@GameRepository.kt`
- UI: `@RelicsScreen.kt`, `@TownScreen.kt`

## Data Flow & Feature Coupling
- Currency Coupling: Relics cost **Magicite** (`cost = (level + 1) * 10`); Town upgrades & Crystals cost **Gold**. Gold exchanges to Magicite via `exchangeGoldForMagicite()`.
- Combat & Loot Scaling: `RelicBonuses.from(GameState)` maps Relic levels into combat multipliers passed to `FFBattleEngine` (`attackBonus`, quadratic `hpBonus`, `goldMultiplier`, `magiciteChanceBonus`).
- Ascended Relics (Tier 2):
  - `MAGNET`: Boosts high-tier item quality rolls.
  - `POCKETS`: Retains gold percentage on Dimension Advance (`gold * pocketsBonus`).
  - `DOUBLE_LOOT`: Adds logarithmic chance (`effectiveDoubleLootChance`, max 75%) for extra boss drops.

## Key Signatures & Execution Steps
1. `RelicsViewModel.upgradeRelic(type: RelicType)`: Validates `magicite >= cost`, deducts magicite, increments relic level, and saves `GameState`.
2. `TownViewModel.upgradeBuilding(type: UpgradeType)`: Validates `gold >= cost`, deducts gold, increments building level, updating town bonuses.
3. `RelicBonuses.from(gs: GameState)`: Aggregates base relic levels, town multipliers, and pet bonuses into an immutable combat configuration.

## Feature Invariants
- Relic Cost Invariant: Relic upgrade cost is strictly `(currentLevel + 1) * 10` Magicite.
- Currency Distinction: Relics strictly cost Magicite; Town buildings strictly cost Gold.
- Prestige Safety: Relic levels and Town facility unlocks are persistent and MUST NOT reset on Dimension Advance.
