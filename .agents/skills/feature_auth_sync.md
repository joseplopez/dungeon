# Skill: Authentication & Cloud Save Sync

## Core Anchors
- Repositories: `@AuthRepository.kt`, `@UserPreferencesRepository.kt`, `@GameRepository.kt`
- ViewModel: `@MainViewModel.kt`

## Data Flow & Cross-Feature Coupling
- Auth State: Firebase auth state controls sync eligibility in `GameRepository`. User settings (audio, notifications) load via `UserPreferencesRepository` (DataStore).
- Save Sync: Serializes `GameState`, `Hero`, `Item`, and `Relic` Room entities into encrypted cloud JSON payloads.

## Key Signatures & Execution Flow
1. `signInAnonymously()` / `linkAccount()`: Manages Firebase user session.
2. `syncCloudSave()`: Uploads local Room database payload to Firebase Firestore.
3. `resolveConflict(local, remote)`: Solves save state mismatches by prioritizing higher `currentDimension` / `highestFloor`.

## Feature Invariants
- Anti-Data-Loss Rule: Conflict resolution MUST preserve whichever save state has higher dimension/floor progression.
- Offline Fallback: Database reads/writes must target local Room SQLite first; cloud sync runs asynchronously.
