# Skill: Job Mastery System

## Core Anchors
- ViewModel & Repo: `@GameState.kt`, `@GameRepository.kt`
- Models: `@JobAbility.kt`, `@HeroClass.kt`
- UI: `@MasteryScreen.kt`

## Data Flow & Cross-Feature Coupling
- EXP Accumulation: Battle victories in `FFBattleEngine` emit Job EXP -> updates hero's class mastery level in `GameRepository`.
- Stat Multipliers: Mastered passive abilities aggregate into `RelicBonuses` / base hero calculations, boosting combat stats globally.
- Prestige Safety: Job levels and unlocked abilities persist permanently across Dimension Advances.

## Key Signatures & Execution Flow
1. `addJobExp(heroId: String, exp: Int)`: Updates hero class EXP, evaluates level threshold, unlocks `JobAbility`.
2. `getUnlockedAbilities(heroClass: HeroClass)`: Returns active/passive abilities available for combat assignment.

## Feature Invariants
- Class Mastery Persistence: Job levels and passive ability unlocks NEVER reset on Dimension Advance.
- Multiplier Rule: Job passive stat multipliers apply universally after base calculation: `(Base * Relic + Gear) * JobMasteryBonus`.
