# Skill: Leaderboards & Social Competition

## Core Anchors
- ViewModel: `@LeaderboardViewModel.kt`
- Repositories: `@LeaderboardRepository.kt`, `@FriendRepository.kt`
- Models & UI: `@LeaderboardEntry.kt`, `@LeaderboardScreen.kt`

## Data Flow & Cross-Feature Coupling
- Run Completion Trigger: `InnViewModel.advanceDimension()` emits fastest clear time and highest floor to `LeaderboardRepository`.
- Authentication Dependency: Uses user ID from `AuthRepository` for Firebase score verification and ranking sync.

## Key Signatures & Execution Flow
1. `submitScore(dimension: Int, clearTimeMs: Long, maxFloor: Int)`: Validates state integrity and pushes record to Firebase.
2. `fetchGlobalLeaderboards(dimension: Int)`: Retrieves paginated rankings flow for UI display.

## Feature Invariants
- Integrity Rule: Scores must be validated against local `GameState` dimension metrics before remote submission.
- Offline Handling: Unsent leaderboard scores must queue locally and sync upon valid `AuthRepository` connection.
