# Skill: Biome Background Design & Refactoring Protocol

This document defines the mandatory visual design, architecture, and refactoring strategy for all 23 biome backgrounds in `PixelBackground.kt`.

---

## 🎨 Visual Reference & Quality Standard

All biome backgrounds MUST match the level of detail, depth, and artistic fidelity established by `CORNELIA_CASTLE` and `CHAOS_SHRINE`.

A background is NOT complete with flat solid colors or simple shapes. Every biome must feel like an immersive, 16-bit / 32-bit RPG canvas scene with realistic perspective, detailed environmental structures, dynamic lighting/animations, and atmospheric depth.

---

## 🏛️ The Mandatory 7-Layer Architecture (`DrawScope`)

Every biome drawing function (e.g., `drawGurguVolcano`, `drawSeaShrine`, etc.) MUST be constructed using the following 7-layer canvas structure:

| Layer | Component | Description & Techniques |
| :--- | :--- | :--- |
| **Layer 1** | **Atmospheric Sky & Void Gradient** | Rich multi-stop vertical/radial background gradient + animated ambient elements (swirling cloud masses, heat haze, ocean currents, or starfield aura). |
| **Layer 2** | **Distant Horizon Silhouettes** | Jagged mountain ridges, sunken ruins, distant spires, or castle walls drawn along the horizon line (`floorY ≈ 0.50f - 0.72f`). |
| **Layer 3** | **Perspective Tiled Flooring** | Stone/metal platform with perspective grid lines converging to a vanishing point (`vpX`), decorative floor rosettes/motifs, and platform cliff drops into void/abyss. |
| **Layer 4** | **Hazard Pits & Ruined Barriers** | Irregular floor holes, pits showing dark depths, pools of lava/water, low ruined stone walls, or glowing rifts. |
| **Layer 5** | **Architectural Foreground Structures** | Detailed fluted columns, iron arches, support beams, or machinery featuring left-to-right lighting gradients (highlight/base/shadow), capitals, bases, carved geometric bands, and support for intact vs. broken variants via helper spec classes (e.g., `ColumnSpec`). |
| **Layer 6** | **Dynamic Particles & Ambient Lighting** | Animated rising embers, floating void energy particles, ocean bubbles, or flickering light reflection pools on the floor. |
| **Layer 7** | **Atmospheric Vignette** | Radial gradient vignette around screen edges (`Color.Transparent` to dark overlay) to frame the battle arena and draw focus to the center. |

---

## 🗺️ Biome Refactoring Roadmap

Each of the remaining 21 biomes must be refactored sequentially using this 7-layer protocol:

1. **`CHAOS_SHRINE`** *(Completed)*: Ruined void temple with fluted columns, floor pits, low stone wall, and swirling magenta clouds.
2. **`GURGU_VOLCANO`**: Volcanic cavern with glowing lava rivers, basalt pillars, rising embers, and heat distortion waves.
3. **`SEA_SHRINE`**: Submerged sunken temple with bioluminescent coral reefs, ancient ruins, rising bubbles, and caustic light rays.
4. **`EARTH_CAVE`**: Underground stalactite cavern with crystal veins, subterranean chasm, and damp stone textures.
5. **`CRYSTAL_TOWER`**: Prism spire with floating crystal monoliths, shimmering light shafts, and polished mirror floor.
6. **`MYSIDIAN_TOWER`**: Arcane observatory with celestial star maps, floating magic glyphs, and astral aura.
7. **`PANDAEMONIUM`**: Infernal hell palace with bone columns, soul flames, and abyssal platform drop-offs.
8. **`MOUNT_ORDEALS`**: Sacred mountain summit with aurora borealis, snowy precipice, and jagged frozen peaks.
9. **`BARON_CASTLE`**: Dark Knight citadel with iron grates, gothic arches, wall banners, and blue moonlight.
10. **`ANCIENT_CASTLE`**: Desert sunken fortress with overgrown ivy, cracked sandstone tiles, and golden sunlight shafts.
11. **`NARSHE_MINES`**: Steam-powered mine shaft with wooden support beams, iron tracks, and glowing ore veins.
12. **`MAGITEK_FACTORY`**: Industrial plant with glowing green Mako pipes, iron catwalks, and steam vent particles.
13. **`KEFKA_TOWER`**: Apocalyptic Babel tower with twisted scrap metal, ruined statues, and fiery sky.
14. **`FLOATING_CONTINENT`**: Sky citadel island floating above cirrus clouds with island rock edges and wind drift.
15. **`MIDGAR_SEWERS`**: Industrial waste tunnel with arched pipe outlets, murky green sludge, and iron grates.
16. **`SHINRA_BUILDING`**: Cyberpunk corporate tower with neon grid floor, glass windows, and holographic displays.
17. **`NORTHERN_CRATER`**: Mako energy chasm with glowing Lifestream rivers, ice walls, and floating Mako motes.
18. **`GOLDEN_SAUCER`**: Neon amusement casino palace with festive lights, fireworks, and gold sparkle particles.
19. **`BEVELLE_TEMPLE`**: Sacred high-tech temple with flowing waterfalls, glowing glyph pathways, and marble columns.
20. **`OMEGA_RUINS`**: Ancient alien labyrinth with quantum glyphs, shifting walls, and glitch energy particles.
21. **`SIN_INTERIOR`**: Organic eldritch abode with pulsing biological walls, nerve tendrils, and spore drift.
22. **`GENERIC_DUNGEON`**: Classic stone brick dungeon with iron-barred alcoves, wall torches, and banner drapery.

---

## ⚡ Performance Rules for `DrawScope` Canvas

To maintain 60 FPS animation during battles:

1. **No Object Allocations inside Drawing Loops**: Do not construct heavy objects inside `repeat` or `while` loops if they can be pre-computed or re-used.
2. **Path Re-use & Smooth Bezier Curves**: Always close closed paths with `.close()`.
3. **Smooth Trigonometric Animations**: Use `scrollOffset`, `slowPulse`, `medPulse`, and `torchFlicker` inputs to drive smooth, hardware-accelerated animations.

---

## 🧪 Testing Protocol for Biome Backgrounds

When testing biome background changes:
- Run the specific test method in `BiomeUITest` corresponding to the biome being refactored (e.g. `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.game.dungeon.ui.BiomeUITest#testBiome_GurguVolcano`).
- Alternatively, verify compilation with `./gradlew :app:assembleDebug` and run unit tests with `./gradlew :app:testDebugUnitTest`.

---

## 📝 Agent Execution Checklist

Before considering a biome refactor complete:
- [ ] Refactored function implements all 7 layers.
- [ ] Helper data spec classes (e.g. `ColumnSpec`) are used for modular structures.
- [ ] Code compiles cleanly with `./gradlew :app:assembleDebug`.
- [ ] Unit tests pass with `./gradlew :app:testDebugUnitTest`.
- [ ] Ran corresponding `BiomeUITest` method for the refactored biome (e.g., `testBiome_GurguVolcano`).
- [ ] Verified visually on connected device / emulator or preview.

*Always read this file before refactoring any biome background in `PixelBackground.kt`.*
