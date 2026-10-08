# Skill: Analytics & Event Telemetry

## Core Anchors
- Manager: `@AnalyticsManager.kt`
- ViewModels: `@DungeonViewModel.kt`, `@InnViewModel.kt`, `@TownViewModel.kt`

## Data Flow & Cross-Feature Coupling
- Event Passive Hook: Gameplay state transitions (dimension advance, boss defeated, hero hired, purchase completed) invoke `AnalyticsManager.logEvent()`.

## Key Signatures & Execution Flow
1. `logEvent(eventName: String, params: Map<String, Any>)`: Forwards event payload to Firebase Analytics / Telemetry.
2. `logDimensionAdvance(dimension: Int, clearTime: Long)`: Tracks ascension milestones.

## Feature Invariants
- Non-Blocking Execution: Telemetry calls MUST be non-blocking and caught in internal try-catch blocks to prevent game loop interrupts.
- Zero PII Rule: Never log player credentials or raw user data; restrict payloads to gameplay metrics.
