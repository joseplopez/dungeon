# Skill: Inn & Prestige System

## Core Anchors
- ViewModel: `@InnViewModel.kt`
- Models & Repo: `@Hero.kt`, `@GameState.kt`, `@GameRepository.kt`
- UI: `@InnScreen.kt`

## Data Flow & Feature Coupling
- Recruitment: `hireHero(job)` verifies `isJobUnlocked(job)` (Crystal unlock) & Tier 2+ `innLevel >= 1`. Freelancers are free and unconditional (`hireCost == 0`).
- Dimension Advance Contract:
  - Reset: Party removed via `removeHero()` (unequips gear), inventory cleared via `clearInventory()`, `highestFloor = 0`, dimension stats reset. Gold reduced to `gold * pocketsBonus` ("Deep Pockets" Relic).
  - Persists: `currentDimension` (incremented), accumulated `magicite`, unlocked Relics, job unlocks, town levels, and Hall of Fame clear time records.
  - Magicite Bonus: `(dimension * 250) + (magiciteEarnedThisDim * (0.50f + (dimension - 1) * 0.10f))`.

## Key Signatures & Execution Steps
1. `hireHero(heroClass: HeroClass)`: Validates unlock criteria and `gold >= hireCost`, deducts gold, creates hero (`isInParty = true`), and saves via repository.
2. `fireHero(heroId: String)`: Calls `repository.removeHero()`, unequip-recovering all gear to inventory.
3. `advanceDimension()`: Executes state reset, applies gold retention, awards bonus magicite, updates `fastestClearTime`, and increments `currentDimension`.

## Feature Invariants
- Job Unlock Rule: Tier 2+ jobs require both Crystal unlock and `innLevel >= 1`. Freelancer is always available.
- Party Capacity: Party size bounded by `getMaxPartySize()` (max 3 default, expanded via Inn upgrades).
- Prestige Persistence Rule: Relics, Magicite, and unlocked Jobs/Facilities NEVER reset on Dimension Advance.
