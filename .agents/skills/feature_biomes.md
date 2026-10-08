# Skill: Dimensions & Biomes

## Core Anchors
- Engine/Models: `@FFDimension.kt`, `@FFDimensionData.kt`, `@MonsterType.kt`
- UI/Rendering: `@PixelBackground.kt`, `@DungeonScreen.kt`

## Data Flow & Cross-Feature Coupling
- Progression Coupling: `DungeonViewModel` floor updates trigger biome transitions, updating `PixelBackground` parallax themes and selecting enemy spawn pools from `FFDimensionData`.
- Boss Generation: Every 10th floor selects a scaled boss entity from `MonsterType` with forced RARE+ drop scaling.

## Key Signatures & Execution Flow
1. `getDimensionData(dimension: Int)`: Returns theme configs, background layer profiles, and monster tables.
2. `renderBiome(canvas, biomeType, scrollOffset)`: Draws 7-layer parallax background in `PixelBackground.kt`.

## Feature Invariants
- Fixed Boss Spawns: Boss encounters MUST trigger precisely every 10 floors (`floor % 10 == 0`).
- Render Isolation: Background rendering in `PixelBackground.kt` must remain purely visual and never mutate game state.
