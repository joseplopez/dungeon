# Skill: Combat Engine & Turn Loop

## Core Anchors
- Engine: `@FFBattleEngine.kt`
- ViewModel: `@DungeonViewModel.kt`
- Models: `@Hero.kt`, `@Enemy.kt`, `@Item.kt`, `@RelicType.kt`

## Data Flow & Feature Coupling
- Input: `runBattle()` consumes `Hero` base stats, equipped `Item` gear, `RelicBonuses` (from `RelicType`), `FFDimension`, and `BattleSpeed`.
- Output/Events: Emits `FFBattleEvent` stream (`DamageDealt`, `HeroFell`, `EnemyDefeated`, `BossDefeated`, `FloorComplete`, `AllHeroesFell`).
- Inter-Feature Coupling:
  - `DungeonViewModel` consumes events to update state, trigger loot persistence via `GameRepository.saveItems`, and manage run completion.
  - Permanent hero death invokes `GameRepository.removeHero`, which calls `unequipAllFromHero` to guarantee item recovery into inventory.
  - Boss defeat triggers `Rarity.RARE` minimum loot roll with double loot scaling via Magicite Magnet relics.

## Key Signatures & Execution Steps
1. `runBattle(heroes: List<Hero>, dimension: FFDimension, startFloor: Int, speed: BattleSpeed, relicBonuses: RelicBonuses, isPaused: () -> Boolean, onEvent: (FFBattleEvent) -> Unit)`
2. `executeHeroTurn(hero: Hero, allies: List<Hero>, enemies: MutableList<Enemy>, relicBonuses: RelicBonuses, ...)`
3. `executeEnemyTurn(enemy: Enemy, heroes: MutableList<Hero>, onEvent: (FFBattleEvent) -> Unit)`
4. `executeJobAbility(hero: Hero, allies: List<Hero>, enemies: MutableList<Enemy>, ...)`

## Invariants
- Bosses must always drop RARE+ gear (`minRarity = Rarity.RARE`).
- Combat stats must strictly calculate Base (`Hero.kt`) x Relic (`RelicType.kt`) + Gear (`Item.kt`).
- Hero death/removal must return all equipped items to inventory via `GameRepository`.
