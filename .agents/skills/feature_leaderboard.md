# Skill: Leaderboards & Social Competition

## Core Anchors
- ViewModel: `@LeaderboardViewModel.kt`
- Repositories: `@LeaderboardRepository.kt`, `@FriendRepository.kt`
- Models & UI: `@LeaderboardEntry.kt`, `@LeaderboardScreen.kt`

## Data Flow & Cross-Feature Coupling
- Run Completion Triggers:
    - Standard Dungeon: `InnViewModel.advanceDimension()` emits fastest clear time and highest floor to `LeaderboardRepository`.
    - Boss Lair: End of Boss Lair combat run in `FFBattleEngine` / `GameRepository` emits `highestBossLairFloor` to `LeaderboardRepository`.
- Authentication Dependency: Uses user ID from `AuthRepository` for Firebase Realtime Database score verification and ranking sync (`/leaderboards/{playerId}`).

## Key Signatures & Execution Flow
1. `uploadScore(playerId: String, playerName: String, gameState: GameState, team: List<Hero>)`:
    - Validates state integrity and pushes record to Firebase.
    - Evaluates high watermark for both `maxFloor` and `maxBossFloor` against server snapshots before updating.
2. `getGlobalLeaderboard(limit: Int = 50)`:
    - Retrieves top rankings sorted by `maxFloor` descending, then `dimension`, then `totalMagicite`.
3. `getBossLairLeaderboard(limit: Int = 50)`:
    - Retrieves top rankings sorted by `maxBossFloor` descending, then `totalMagicite`.
    - Filters out entries where `maxBossFloor <= 0`.

## Feature Invariants & Guardrails
- **Non-Zero Guardrail (`maxBossFloor`):** Never write `maxBossFloor: 0` to Firebase. Only include `"maxBossFloor"` in the payload if `gameState.highestBossLairFloor > 0` or if a valid positive record exists on the server.
- **Score Integrity:** High watermarks (`maxOf(currentRun, serverRecord)`) must always be preserved so player progress is never degraded.
- **Dual-Tab UI Display:**
    - Tab 0: Normal Floor ("Normal") — displays `maxFloor` on table rows, headers, and top 3 podium pedestals.
    - Tab 1: Boss Floor ("Boss Lair") — displays `maxBossFloor` on table rows, headers, and top 3 podium pedestals.
- **Gated Access UX:** Tab 1 is unlocked when `currentDimension >= 11` or `BuildConfig.ENABLE_BOSS_LAIR_TESTING`. When locked, Tab 1 displays `🔒 Boss Lair` and is disabled; no unlock condition text is shown.
- **Offline Handling:** Unsent leaderboard scores queue locally and sync upon a valid `AuthRepository` connection.