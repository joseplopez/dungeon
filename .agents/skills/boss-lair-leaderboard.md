# Skill: Boss Lair Leaderboard Integration

## Core Anchors
- ViewModel: `@LeaderboardViewModel.kt`
- Repositories: `@LeaderboardRepository.kt`, `@GameRepository.kt`
- Models & UI: `@LeaderboardEntry.kt`, `@LeaderboardScreen.kt`

## Data Flow & Cross-Feature Coupling
- **End-of-Run Trigger:** Completion or defeat in `FFBattleEngine` (in `DungeonMode.BOSS_LAIR`) updates `highestBossLairFloor` in `GameState` via `GameRepository`, triggering an immediate background score sync with `LeaderboardRepository.uploadScore()`.
- **Tab Selection State:** `LeaderboardViewModel.selectedTab` toggles between Normal Dungeon rankings (`0`) and Boss Lair rankings (`1`).
- **Gated Access Evaluation:** `canSeeBossLair` state is reactively derived from `gameState.currentDimension >= 11` or `BuildConfig.ENABLE_BOSS_LAIR_TESTING`.

## Key Signatures & Execution Flow
1. `getBossLairLeaderboard(limit: Int = 50)`:
   - Queries Firebase `/leaderboards` ordered by `maxBossFloor`.
   - Filters out non-participants (`maxBossFloor <= 0`).
   - Sorts entries descending by `maxBossFloor` then `totalMagicite` and assigns ranks.
2. `uploadScore(playerId: String, playerName: String, gameState: GameState, team: List<Hero>)`:
   - Evaluates local `highestBossLairFloor` against existing server `maxBossFloor`.
   - Writes the high watermark (`maxOf(local, server)`) to keep player records synchronized across devices.
3. `selectTab(index: Int)`:
   - Switches dataset stream between standard global rankings and Boss Lair rankings in `LeaderboardViewModel`.

## Feature Invariants & Guardrails
- **Non-Zero Guardrail:** Never upload `maxBossFloor: 0` to Firebase. The `"maxBossFloor"` payload key is strictly omitted if `highestBossLairFloor <= 0` and no existing server record exists.
- **High Watermark Protection:** Progress is strictly monotonic (`maxOf(local, server)`). Past records are never degraded or overwritten with lower floor values.
- **Locked Tab Visual State:** When `canSeeBossLair == false`, Tab 1 renders as `🔒 Boss Lair` and disables click interactions. No text explaining dimension unlock conditions is displayed.
- **Dynamic Component Adaptation:** When Tab 1 is selected, table column headers ("BOSS"), row score displays, and top 3 podium pedestals render `entry.maxBossFloor` instead of standard `entry.maxFloor`.