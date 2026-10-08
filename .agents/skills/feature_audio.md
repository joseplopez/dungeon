# Skill: Audio System

## Core Anchors
- Managers: `@MusicManager.kt`
- UI & Entry: `@MusicToggleButton.kt`, `@MainActivity.kt`

## Data Flow & Cross-Feature Coupling
- State Toggle: Reading mute preferences from `UserPreferencesRepository` controls global playback.
- Context Transitions: Screen changes (Town -> Dungeon -> Boss) call `MusicManager` to crossfade background tracks and fire UI/Combat SFX.

## Key Signatures & Execution Flow
1. `playMusic(track: TrackType)`: Swaps background music track with crossfade.
2. `playSfx(sfx: SfxType)`: Fires short sound effect trigger if audio is enabled.

## Feature Invariants
- Lifecycle Binding: Music playback must stop/pause on `Activity.onStop()` and resume on `Activity.onStart()`.
- Global Mute Override: If `isAudioEnabled == false`, all SFX and music calls must exit silently without allocating audio resources.
