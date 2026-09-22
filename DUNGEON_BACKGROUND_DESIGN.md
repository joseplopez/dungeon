# 🎨 Procedural Canvas Dungeon Background Design Plan (Approach B)

This document outlines the architectural blueprint and high-fidelity pixel-art designs for the dynamic, procedural `Canvas`-based background rendering system in *Final Dungeon*. By leveraging Jetpack Compose `DrawScope` primitives (`drawRect`, `drawCircle`, `drawPath`, `linearGradient`, `radialGradient`), we achieve stunning, reactive, animated, and lightweight visuals inspired by iconic *Final Fantasy* locations with zero app-size footprint.

---

## 🏛️ Core Architecture & Performance Strategy

To ensure fluid 60fps/120fps combat gameplay, the background canvas must perform zero heap allocations during the draw loop. 

1. **State Injection**: `DungeonBackground` will be updated to accept the current `BiomeType` enum. It will read global animation ticks from a single shared `rememberInfiniteTransition()`.
2. **Allocation-Free Drawing**: All paths, points, and gradients that depend on screen size will be computed or cached using simple mathematical functions, rather than creating objects on every frame.
3. **Layered Parallax Model**: Every biome will implement a structured multi-layer rendering stack:
   * **Layer 1: Sky/Abyss (Far background)**: Deep color gradients or vertical/radial washes.
   * **Layer 2: Silhouette Structures (Distant parallax)**: Distant pillars, mountains, castle ramparts, or giant pipes moving slowly (`scrollOffset * 0.15f`).
   * **Layer 3: Structural Mid-Wall (Main structure)**: Repetitive block grids, rock crags, or metal panels moving at medium speed (`scrollOffset * 0.4f`).
   * **Layer 4: Atmosphere & Wall Features**: Windows, banners, glowing glyphs, chains, or vents.
   * **Layer 5: Foreground Floor & Grout**: Horizontal walking planes with perspective/tiling lines moving at full speed (`scrollOffset * 1.0f`).
   * **Layer 6: Dynamic Light Wash & Ambient Glow**: Glowing pools, vignettes, and particle effects.

---

## 🗺️ Comprehensive Biome Designs & Mathematical Primitives

Here is the precise visual description and procedural composition for each `BiomeType` defined in the game:


## Core Visual Philosophy

The difference between "nice" and "just polygons" comes down to 4 principles applied to every biome:

1. **Atmospheric color washes** — fullscreen radial/linear gradients set the emotional tone before any geometry is drawn
2. **Layered transparency** — each element is drawn multiple times at different alphas to simulate depth and glow
3. **Light sources** — every biome has 1-2 dominant light sources (torch, magic orb, lava, sun) that paint warm/cool light pools on every surface near them
4. **Motion variety** — at least 3 different animation speeds create genuine parallax depth

## Shared Animation State (declare once, used by all biomes)

```kotlin
val infiniteTransition = rememberInfiniteTransition()
val scrollOffset by infiniteTransition.animateFloat(0f, 1000f, 
    infiniteRepeatable(tween(30000, easing = LinearEasing), RepeatMode.Restart))
val torchFlicker by infiniteTransition.animateFloat(0.75f, 1.0f,
    infiniteRepeatable(tween(120 + (Math.random()*80).toInt(), easing = FastOutSlowInEasing), RepeatMode.Reverse))
val slowPulse by infiniteTransition.animateFloat(0f, 1f,
    infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse))
val medPulse by infiniteTransition.animateFloat(0f, 1f,
    infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse))
val fastTick by infiniteTransition.animateFloat(0f, 1f,
    infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Restart))
```

---

## Biome 1: CORNELIA_CASTLE
**FF1 — Royal sanctuary, warm stone, medieval grandeur**

### Visual goal
A majestic castle interior that feels warm and safe — but with an undercurrent of danger. Think amber torchlight on dark grey stone, deep shadows in arched alcoves, red royal banners hanging between pillars.

### Layer stack

**L1 — Deep background atmosphere**
```
Vertical linear gradient: top=#0a0814 → mid=#1a1228 → bottom=#0d0a18
Then a soft warm radial at center-bottom (torch glow bleeding from below): Color(0.8f,0.4f,0.1f, 0.08f), radius=W*0.7
```

**L2 — Distant castle silhouette (parallax 0.08x)**
Draw 4-5 tall rectangular column silhouettes spaced across the far background:
- Each column: dark charcoal (#121020), width=W*0.06, height=H*0.75, y starting at H*0.08
- On each column: draw 2-3 faint narrow rectangles (barred windows) using Color(0.4f,0.5f,0.7f,0.15f)
- Cap each column with a decorative top: 3 small battlements (crenellations) using stacked small rects

**L3 — Stone wall tiles (parallax 0.3x)**
```
tileW = W/22f, tileH = H*0.055f
For each tile: base color alternates between #1c1628 and #181420
Each tile gets a 1px top highlight line in #2a2038 (catches imaginary light from above)
Each tile gets a 1px bottom shadow line in #0d0a14
Every 3rd column of tiles: shift row offset by tileW/2 (Flemish bond pattern)
```

**L4 — Hanging royal banners**
At x positions [W*0.2, W*0.5, W*0.8]:
- Banner pole: 4px wide rect from y=0 to y=H*0.35, color=#8B6914
- Banner body: rect 40px wide, 120px tall, color=#8B0000
- Banner bottom: triangle point (Path: left corner → right corner → center-bottom tip)
- Banner highlight edge: 2px line on left side in #cc2222
- Thin gold border outline around entire banner using Stroke
- Vertical center stripe in #FFD700, width=6px

**L5 — Two torches (left and right walls)**
At x=[W*0.15, W*0.85], y=H*0.32:
```
// Wall bracket
drawRect(#3a2a10, bracket rect)
drawRect(#5a4018, smaller inner bracket)

// Glow — draw 4 circles from large→small, increasing alpha, decreasing radius
drawCircle(Color(1f,0.35f,0.1f, torchFlicker * 0.06f), radius=W*0.18, center)
drawCircle(Color(1f,0.45f,0.1f, torchFlicker * 0.12f), radius=W*0.10, center)  
drawCircle(Color(1f,0.6f,0.2f, torchFlicker * 0.22f), radius=W*0.05, center)
drawCircle(Color(1f,0.8f,0.4f, torchFlicker * 0.5f), radius=W*0.02, center)

// Flame: 3 overlapping vertical rects, each slightly narrower and offset, animated height
// Outer flame: orange, 16px wide, 28px tall
// Mid flame: amber, 10px wide, 38px tall, offset -3px x  
// Core flame: near-white yellow, 4px wide, 20px tall, offset +2px x
```

**L6 — Floor with perspective tiles**
```
floorY = H * 0.72f
// 6 horizontal lines converging to vanishing point at center for perspective
// Spaced closer together near floorY, wider toward bottom edge
// Color: #1a1428 alternating #141020, with gold grout lines at #2a2038
// Reflection wash: soft horizontal gradient strip just at floorY, 
//   Color(0.8f,0.5f,0.1f, torchFlicker*0.04f) to simulate torch glow on floor
```

**L7 — Atmospheric vignette**
```
Radial gradient: Color.Transparent at 35% radius → Color(0,0,0,0.75f) at edges
This dramatically focuses attention on the center combat area
```

---

## Biome 2: CHAOS_SHRINE
**FF1 — Ancient ruined temple, void energy, cosmic horror**

### Visual goal
Oppressive, unsettling. Dark stone that seems to breathe. Cracks glowing with void energy. A sense of wrongness — like reality is tearing.

### Layer stack

**L1 — Void atmosphere**
```
Base fill: #050308 (near-black with purple tinge)
Pulsing void center: radialGradient at screen center
  Color(0.4f, 0f, 0.6f, slowPulse * 0.25f) → transparent
  radius = W * 0.55f, pulses between 0.15f and 0.25f alpha
Outer darkness: vignette radial Color(0,0,0,0.9f) at edges
```

**L2 — Shattered pillar silhouettes (parallax 0.1x)**
3 pillars, each deliberately broken differently:
- Left pillar: full from y=0 to H*0.6, then jagged diagonal break
- Center pillar: bottom section only (H*0.5 to H*0.72), top half "fallen"
- Right pillar: full but with large chunk missing from middle
  Each pillar: base color #0f0a18, highlight face #1a1230, shadow side #080510

**L3 — Cracked stone walls (parallax 0.35x)**
Stone tiles same as Cornelia but:
- Color palette shifted purple: #140d20, #1a1228
- Every 6th tile: draw a crack — a jagged path using 4-5 points zigzagging diagonally
- Crack color: Color(0.6f, 0f, 0.8f, medPulse * 0.6f + 0.2f) — glowing purple
- Crack glow: draw the same path again 2px wider in Color(0.4f,0f,0.6f,0.15f)

**L4 — Void rifts (3 floating tears in reality)**
At random-seeded positions:
```
// Each rift: vertical eye/tear shape using two arced paths
// Outer ring: Color(0.6f, 0f, 0.9f, 0.3f + slowPulse*0.2f), Stroke 3px
// Inner fill: Color(0.2f, 0f, 0.4f, 0.6f)
// Core: tiny bright point Color(0.8f, 0.4f, 1f, medPulse)
// Energy tendrils: 6 thin lines radiating outward from rift center, 
//   length = 20-40px, Color(0.5f,0f,0.7f, 0.2f)
```

**L5 — Floating debris particles**
20 small rectangles (2-6px) at varying positions:
- Move upward slowly: y -= scrollOffset * 0.02f, wrap around
- Alpha varies: 0.1f to 0.5f based on sin(scrollOffset * 0.01f + seedPerParticle)
- Color: mix of purple #6600AA and dark charcoal #221133

**L6 — Mystical glyphs on wall**
5-6 circular glyph outlines (drawCircle Stroke style):
- Outer circle: 60px radius, Color(0.5f,0.2f,0.8f, 0.12f)
- Inner geometric: 6-pointed star via Path (2 overlapping triangles)
- Pulse: scale the star slightly using sin wave — gives "breathing" feel

---

## Biome 3: GURGU_VOLCANO
**FF1 — Volcanic hellscape, molten earth, suffocating heat**

### Visual goal
You should feel the heat through the screen. Everything glows orange. The floor churns with magma. Smoke drifts upward. Rocks jut from the walls.

### Layer stack

**L1 — Heat atmosphere**
```
Vertical gradient: #0a0400 (top) → #1a0800 (mid) → #2a0e00 (floor level)
Fullscreen heat pulse overlay: Color(1f, 0.3f, 0f, torchFlicker * 0.04f)
  This makes the entire scene "breathe" with heat
```

**L2 — Volcanic rock silhouettes (parallax 0.08x)**
Jagged mountain shapes in the far background:
- 4-5 irregular triangular shapes using Path() with pointed tops
- Use slightly lighter than background: #1a0c04
- Faint orange glow along their top edges: 2px line Color(1f,0.4f,0f,0.15f)

**L3 — Dark obsidian walls (parallax 0.3x)**
```
Tiles: #1a0c04 and #140a02 alternating, same brick pattern
Random tiles: have orange/red cracks (glowing lava seeping through)
  Crack color: Color(1f, 0.4f, 0f, 0.5f + medPulse*0.3f)
  Crack glow: wider path at 0.1f alpha — lava glow bleeding around crack
Occasional protruding rock: darker irregular rect jutting out from wall
```

**L4 — Lava river at floor**
```
lavaY = H * 0.70f
// Base lava: thick horizontal band (H*0.08 tall) 
//   fill with horizontal gradient: #FF2200 → #FF6600 → #FFAA00 → #FF6600 → #FF2200
// Animated surface: draw sine wave path on top of lava band
//   for x in 0..W step 4: y = lavaY + sin(x*0.03f + scrollOffset*0.008f) * 8f
//   fill above path with orange, below with dark to create wave surface
// Lava glow upward: radialGradient at 3 points along lava surface
//   Color(1f,0.5f,0f, 0.15f*torchFlicker) radius=W*0.2 pointing upward
// Bright crust chunks: 8-10 small irregular dark polygons floating on lava surface
//   Color: #1a0800 with orange outline
```

**L5 — Rising smoke and embers**
```
Smoke: 8 large semi-transparent circles (radius 30-60px)
  Color(0.15f,0.1f,0.1f, 0.15f-0.25f)
  Rise upward: y = baseY - (scrollOffset * speed) % H
  Drift sideways: x += sin(scrollOffset*0.02f + seed) * 2f

Embers: 25 tiny dots (2-3px)
  Color(1f, 0.6f+rand*0.4f, 0f, 0.6f+sin(scrollOffset*0.1f+seed)*0.3f)
  Float upward faster than smoke, some flicker out (alpha → 0 then reset position)
```

---

## Biome 4: SEA_SHRINE
**FF1 — Submerged temple, crushing depth, bioluminescence**

### Visual goal
Serene and alien. Everything is filtered through deep blue-green water. Light rays pierce from above in shafts. Bubbles drift lazily upward. The stone walls are encrusted with coral.

### Layer stack

**L1 — Deep ocean atmosphere**
```
Fullscreen radial gradient (brightest at top, darkest at bottom):
  Color(0.05f,0.2f,0.3f,1f) at top → Color(0.02f,0.08f,0.15f,1f) at bottom
Underwater tint wash: Color(0f,0.3f,0.5f,0.12f) fullscreen rect
```

**L2 — Distant submerged ruins (parallax 0.1x)**
Broken pillars partially buried, seaweed growing on them:
- Pillar bases: Color(#0d1e2a), height varying 30-60% of H
- Coral growths: irregular small circles in Color(0.8f,0.3f,0.3f,0.6f) and Color(0.9f,0.5f,0.1f,0.5f) clustered at pillar tops

**L3 — Temple stone walls (parallax 0.35x)**
Same brick pattern but:
- Palette: deep teal-grey #0d1e28, #0a1820
- Algae streaks: thin vertical lines Color(0.1f,0.5f,0.2f,0.2f) running down walls
- Barnacle clusters: groups of 3-5 tiny circles at tile intersections Color(0.3f,0.3f,0.2f,0.4f)

**L4 — Caustic light rays**
```
// 5-7 light shafts from top of screen
// Each shaft: thin diagonal parallelogram using Path()
//   4 points: (topLeft, topRight slightly right, bottomRight far right, bottomLeft)
//   Width at top: 15-30px, width at bottom: 60-120px (fan out downward)
//   Color: Color(0.3f,0.7f,1f, 0.04f + sin(scrollOffset*0.003f + i)*0.03f)
// Animate: each shaft sways — top point oscillates ±20px horizontally via cos
// Stagger animation phase per shaft: offset = i * 0.7f
```

**L5 — Rising bubbles**
```
// 30 bubbles, each with: x=seed*W, y=startY, radius=3-10px, speed=0.3-0.8f
// Draw as hollow circle (Stroke, 1-2px) in Color(0.5f,0.8f,1f,0.3f-0.6f)
// Small white highlight arc at top-left of each bubble
// y decreases over time, resets to bottom when y < 0
// Wobble x: x += sin(scrollOffset*0.01f*speed + seed) * 1.5f
```

**L6 — Bioluminescent floor**
```
Floor line with soft teal glow strips:
drawRect(Color(0.1f,0.5f,0.6f, 0.15f + slowPulse*0.1f), floor strip 8px tall)
// Adds magical underwater floor glow
```

---

## Biome 5: CRYSTAL_TOWER
**FF1/FF3 — Crystalline apex, blinding light, geometric perfection**

### Visual goal
Overwhelming brightness and clarity after dark dungeons. Pure light refracting through crystal. Everything sparkles. The geometry is impossibly perfect.

### Layer stack

**L1 — Crystal light atmosphere**
```
Vertical gradient: Color(0.05f,0.1f,0.2f,1f) top → Color(0.1f,0.2f,0.35f,1f) bottom
Bright center bloom: radialGradient center
  Color(0.6f,0.9f,1f, 0.15f) → transparent, radius W*0.4
```

**L2 — Giant crystal formations (parallax 0.08x)**
6-8 tall crystal columns using Path():
```
// Each crystal: hexagonal prism cross section
// Draw as: left face (darker shade), right face (lighter shade), front face (medium)
// Left face points: [x, yTop+20], [x+20, yTop], [x+20, yBottom], [x, yBottom+20]
// Right face: similar but shifted
// Colors: left=#0a2030, front=#0d3050, right=#1a5080
// Top facets: bright highlight polygon Color(0.5f,0.8f,1f,0.4f)
// Crystal glow: radialGradient at crystal top, Color(0.3f,0.8f,1f,0.1f) radius=40px
```

**L3 — Mirror tile floor (parallax 1.0x)**
```
// Reflective floor tiles with perspective
// Each tile: base Color(0.05f,0.15f,0.25f,1f)
// Highlight stripe: Color(0.2f,0.6f,0.9f,0.08f) diagonal across tile  
// Grout lines: Color(0.1f,0.4f,0.7f,0.3f), width 1px
// Faint reflection below floor line: mirror the crystal columns at 10% alpha
```

**L4 — Light refraction sweep**
```
// One diagonal band of light that sweeps across the entire scene slowly
// Draw as a diagonal parallelogram, very wide (300px), very faint (0.04f alpha)
// X position: animates from -300 to W+300 over 8 seconds, then repeats
// Color: Color(1f,1f,1f, 0.06f) — pure white sweep
// This single effect makes it feel like sunlight moving across crystal
```

**L5 — Sparkle particles**
```
// 40 sparkles: each is a 4-pointed star shape (two thin rects crossed)
// Size: 4-12px, rotate slowly each frame
// Alpha: sin wave, phase offset per particle — creates twinkling
// Color: white with cyan tint Color(0.8f,1f,1f, sparkAlpha)
// Position: fixed seeds but size/alpha pulse independently
```

---

## Biome 6: NARSHE_MINES
**FF6 — Frozen coal mine, industrial cold, danger underground**

### Visual goal
Claustrophobic, cold, and industrial. Wooden support beams creak. Ice crystals form on rock walls. A single lantern swings in a drafty shaft. Everything is muted except the ice-blue glints.

### Layer stack

**L1 — Cold mine atmosphere**
```
Near-black with blue-grey tinge: #060810 → #0a0e14
Subtle cold vignette: Color(0f,0.1f,0.2f,0.08f) fullscreen
```

**L2 — Mine shaft depth (parallax 0.1x)**
Draw a rectangular tunnel opening in the center-back:
- Outer dark frame: thick dark rect border
- Inner tunnel: deep black rect with blue center glow fading to black

**L3 — Rock walls (parallax 0.35x)**
```
// Irregular rock texture: instead of perfect tiles, draw irregular polygons
// Each "rock": roughly 4-6 sided polygon with slight random vertex offsets
// Colors: #1a1e24, #141820, #1e2228 varying
// Coal vein: occasional thin black stripe running diagonally through wall
// Ice crystal: small 4-6px diamond shapes Color(0.6f,0.8f,1f,0.3f) at crack points
```

**L4 — Wooden support trusses (key element)**
```
// Across the width at y=H*0.12 and y=H*0.70:
// Horizontal beam: rect H*0.04 tall, full width, Color(#3a2510)
// Highlight face: 3px top line Color(#5a3a18)
// Shadow underbelly: 3px bottom line Color(#1a1008)
// Vertical supports: 4 posts at W*0.15, W*0.35, W*0.65, W*0.85
//   Each post: rect 18px wide, from top beam to bottom beam
//   Same wood colors as beams
// Diagonal braces: 2 lines crossing between adjacent posts (X pattern)
//   Color(#2a1a0c), width 4px
// Bolt details: small circles at beam/post junctions Color(#888), 4px radius
```

**L5 — Hanging lantern (swings!)**
```
// Lantern at center top, hangs from ceiling
// Chain: 8 small rects stacked vertically, each 4x6px, Color(#666666)
//   Swing angle: sin(scrollOffset*0.015f) * 8° — rotate chain around top point
// Lantern body: 20x28px rect Color(#2a1a04) with orange window slots
// Glow: radialGradient at lantern center, Color(1f,0.7f,0.2f, torchFlicker*0.25f)
//   radius=W*0.12 — oval elongated glow pool on floor below swinging lantern
//   Animate glow center x: follows lantern swing
```

**L6 — Icicles from ceiling**
```
// 12 icicles at varying x positions, y starts at 0 (ceiling)
// Each icicle: triangle path, base 8-20px wide, length 30-80px
// Color: Color(0.6f,0.8f,1f,0.7f) with white tip highlight
// Drip: tiny circle below tip Color(0.5f,0.7f,1f,0.4f) that falls slowly
//   and resets — one drip per icicle with different phase offsets
```

---

## Biome 7: MAGITEK_FACTORY
**FF6 — Industrial Empire complex, steam and neon, mechanical evil**

### Visual goal
The opposite of organic nature — pure industrial menace. Pipes everywhere. Glowing green chemical conduits. Steam vents. Warning lights pulse red.

### Layer stack

**L1 — Factory atmosphere**
```
Dark industrial: #060808 → #0a0e0c
Green chemical wash: Color(0f,0.3f,0.1f,0.04f) fullscreen tint
Distant furnace glow from one side: radialGradient Color(0.8f,0.3f,0f,0.06f) at W*0.9, H*0.5
```

**L2 — Pipe bundles (background layer)**
```
// 3 massive pipe bundles running horizontally across the far back
// Each bundle: 5-8 circles side by side (circular pipe cross-sections)
// Pipe colors: #1a2020 (dark), #2a3030 (mid), #3a4040 (highlight face top)
// Center highlight on each pipe: small white rect Color(0.5f,0.6f,0.6f,0.2f)
//   This gives each pipe a 3D rounded appearance
```

**L3 — Metal panel walls (parallax 0.4x)**
```
// Large rectangular panels (wider and taller than brick tiles)
// Panel base: #0d1210
// Panel border: 2px inset line Color(#1a2a22)  
// Rivet at each corner: 4px circle Color(#2a3a32)
// Rivet highlight: 2px circle offset top-left Color(#4a5a52)
// Every 4th panel: glowing green chemical tube runs vertically through it
//   Tube: 6px wide rect Color(0f,0.7f,0.3f,0.6f) with outer glow Color(0f,1f,0.4f,0.1f)
//   Animate tube fill: dash pattern animating upward (use path effect or manual dots)
```

**L4 — Steam vents**
```
// 4 vent openings low on the wall (y=H*0.55)  
// Vent grill: 6 thin horizontal lines Color(#222)
// Steam puff: when vent active (sin(time+offset) > 0.7):
//   Draw expanding semi-transparent circles rising from vent
//   Color(0.7f,0.8f,0.7f, 0.15f - progress*0.12f), radius grows 10→50px
//   Multiple puffs at different stages overlapping
```

**L5 — Warning lights**
```
// 3 warning lights mounted at top of wall
// Light housing: small rectangle Color(#333)
// Active pulse: when (scrollOffset*0.05f + offset).toInt() % 4 < 2:
//   Draw filled circle Color(0.9f,0.1f,0f,0.9f) — red on
//   Draw glow radial Color(1f,0.2f,0f, 0.2f) radius=30px
// Else: draw dim circle Color(0.3f,0f,0f,0.5f) — red off
// Stagger the 3 lights so they don't all pulse together
```

---

## Biome 8: KEFKA_TOWER
**FF6 — Insane god's monument, surreal chaos, broken reality**

### Visual goal
Nothing should make sense. Mixed architectural styles slammed together. Colors clash violently. Sections of the background rotate or are at wrong angles. Pure visual madness reflecting Kefka's fractured mind.

### Layer stack

**L1 — Chaos atmosphere**
```
Base: fast-cycling hue rotation on a dark gradient
  Use: Color(sin(slowPulse*3.14f)*0.3f+0.1f, 0.05f, cos(slowPulse*3.14f)*0.3f+0.1f, 1f)
  This makes background slowly cycle between dark purple and dark red tones
```

**L2 — Mismatched structure sections**
Draw 4 distinct "chunks" of architecture side by side, each from a different style:
- Chunk 1 (left): Castle stone bricks, tilted 8° using canvas rotation
- Chunk 2: Factory metal panels, normal angle
- Chunk 3: Crystal facets, tilted -5°
- Chunk 4 (right): Temple ruins with void cracks
  Apply each with slight x overlap and alpha=0.8f for blending

**L3 — Floating debris**
```
// 30 fragments: rectangles of varying sizes (10-60px) at random angles
// Rotate each fragment: angle = seed + scrollOffset*0.01f*rotSpeed
// Color: mix of all palette types — stone grey, crystal cyan, factory green, castle stone
// Alpha: 0.3-0.6f so they don't dominate
// Move: each fragment has independent trajectory, bouncing within screen bounds
```

**L4 — God aura (Kefka's presence)**
```
// Periodic blast: when sin(scrollOffset*0.02f) > 0.85f:
//   Flash the entire screen with brief Color(0.8f,0.8f,0.2f,0.15f) overlay
//   This simulates divine power surges
// Permanent mad gold shimmer: diagonal gradient sweep Color(1f,0.8f,0f,0.04f)
```

---

## Biome 9: SHINRA_BUILDING
**FF7 — Corporate neo-noir tower, cyberpunk menace, power without conscience**

### Visual goal
Sleek. Cold. Corporate evil. Glass and steel. Blue neon trim. The architecture is beautiful and soulless simultaneously.

### Layer stack

**L1 — Night city atmosphere**
```
Deep corporate black: #020408
Edge glow suggestion: Color(0f,0.2f,0.4f,0.06f) at left and right edges — distant city glow
```

**L2 — City skyline silhouette (far parallax 0.05x)**
```
// 6-8 building silhouettes of varying heights in the far background
// Pure black against dark-navy sky Color(0.03f,0.06f,0.1f,1f)
// Each building: simple rect, some with a few tiny yellow window squares Color(1f,0.8f,0.2f,0.3f)
// Antenna lights on tallest buildings: blinking red dot (fastTick > 0.5 ? red : dim red)
```

**L3 — Shinra HQ wall panels (parallax 0.4x)**
```
// Very large panels: W*0.25 wide, H*0.2 tall
// Steel base: Color(0.08f,0.1f,0.12f,1f)
// Panel inset border: 3px inner rect Color(0.05f,0.08f,0.1f)
// Horizontal light strip at top of each panel: 
//   2px line Color(0f,0.5f,0.8f,0.4f) — cold blue neon trim
// Reflective surface: diagonal linear gradient across panel 
//   Color.Transparent → Color(0.2f,0.3f,0.4f,0.04f) → Color.Transparent
```

**L4 — Window panels with city reflection**
```
// Tinted glass windows: 
//   Base: Color(0.04f,0.08f,0.15f,1f) 
//   Reflection shimmer: random bright horizontal line Color(0.3f,0.5f,0.8f,0.06f) scrolling down
//   Window frame: 2px border Color(0f,0.3f,0.6f,0.5f)
// Shinra logo suggested: simple geometric S-shape outline in one window Color(0.1f,0.2f,0.8f,0.15f)
```

**L5 — Neon light strips (key element)**
```
// 4 horizontal neon strips running full width at regular intervals
// Each strip: 3px rect Color(0f,0.6f,1f,0.7f) — bright electric blue
// Glow behind each strip: 20px tall rect Color(0f,0.4f,0.8f,0.06f)
// Corner intersection boxes: 10x10px Color(0f,0.7f,1f,0.5f) — neon corner joints
// Animate: strips flicker occasionally using: if (sin(scrollOffset*0.03f+i) > 0.95f) alpha*0.3f
```

---

## Biome 10: BEVELLE_TEMPLE
**FF10 — Yevon's mechanical marble sanctum, alien beauty, divine machinery**

### Visual goal
Pristine white marble and gold, but with glowing Yevon-tech cyan energy ribbons threading through it. Sacred and advanced simultaneously. Like a church designed by an alien civilization.

### Layer stack

**L1 — Sacred light atmosphere**
```
Vertical gradient: Color(0.08f,0.1f,0.14f,1f) top → Color(0.04f,0.06f,0.1f,1f) bottom  
Sacred light bloom from top-center: Color(0.5f,0.7f,1f,0.06f) radial, radius W*0.5
```

**L2 — Marble columns (parallax 0.15x)**
4 tall marble pillars:
```
// Each pillar: 3 vertical faces to suggest 3D
// Left face: Color(0.15f,0.18f,0.22f)
// Front face: Color(0.2f,0.24f,0.3f)  
// Veining: thin irregular paths Color(0.12f,0.15f,0.2f,0.3f) — marble veins
// Capital (top): wider decorative section, Yevon geometric carved detail
// Subtle ivory tint: Color(0.9f,0.9f,0.8f,0.03f) overlay — warm marble
```

**L3 — Energy ribbon tracks (SIGNATURE ELEMENT)**
```
// 5 horizontal channel tracks embedded in walls
// Channel: recessed rect Color(0.06f,0.08f,0.12f)
// Ribbon: animated glowing energy flowing through channel
//   Draw 3 overlapping sinusoidal waves along the channel:
//     Wave 1: Color(0.1f,0.7f,1f,0.8f), amplitude 3px, speed fast
//     Wave 2: Color(0.2f,0.8f,1f,0.4f), amplitude 5px, speed medium, phase offset
//     Wave 3: Color(0.4f,0.9f,1f,0.2f), amplitude 8px, speed slow
//   Together they create a complex flowing ribbon effect
// Glow around channel: Color(0f,0.4f,0.8f,0.08f) 15px tall rect
```

**L4 — Mechanical Yevon accents**
```
// Rotating gear-like symbols at column tops:
//   Outer circle: Stroke Color(0.3f,0.6f,0.8f,0.3f)
//   Inner 8-pointed geometry: drawn with lines from center
//   Slow rotation: angle = scrollOffset * 0.003f
// Vertical light columns from floor: 
//   4px wide rect, full height, Color(0.1f,0.5f,0.9f, 0.06f)
//   Position: between every pair of pillars
```

---

## Implementation Notes for Maximum Visual Quality

### Gradient stacking technique
The key to making Canvas backgrounds look "nice not just polygons" is gradient stacking:
```kotlin
// Instead of: drawRect(solidColor, ...)
// Do this (3 passes):
drawRect(baseColor, offset, size)  // 1. solid base
drawRect(Brush.verticalGradient(listOf(highlightColor, Color.Transparent)), offset, size)  // 2. top highlight
drawRect(Brush.verticalGradient(listOf(Color.Transparent, shadowColor)), offset, size)  // 3. bottom shadow
// Result: looks like a lit 3D surface instead of a flat rectangle
```

### Glow simulation (4-circle technique)
```kotlin
// Real-looking glow from any light source:
fun DrawScope.drawGlow(center: Offset, color: Color, intensity: Float) {
    drawCircle(color.copy(alpha=0.03f*intensity), size.width*0.3f, center)
    drawCircle(color.copy(alpha=0.08f*intensity), size.width*0.15f, center)
    drawCircle(color.copy(alpha=0.2f*intensity), size.width*0.06f, center)
    drawCircle(color.copy(alpha=0.6f*intensity), size.width*0.02f, center)
}
// 4 circles from large+faint to small+bright = convincing glow
```

### Perspective floor (always makes scenes look 3D)
```kotlin
// Horizontal lines converging to vanishing point
val vpX = size.width / 2f  // vanishing point center
val vpY = floorY  // vanishing point at floor level
(1..8).forEach { i ->
    val progress = i / 8f
    val lineY = floorY + (size.height - floorY) * progress
    // Lines fan out from vanishing point
    val leftX = vpX - (vpX) * (progress * 1.5f)
    val rightX = vpX + (size.width - vpX) * (progress * 1.5f)
    drawLine(gridColor, Offset(leftX, lineY), Offset(rightX, lineY), 1f)
}
```

### Performance budget per biome
- L1 atmosphere: <0.2ms (simple gradients)
- L2 silhouettes: <0.5ms (few complex paths)
- L3 wall tiles: <1.0ms (many simple rects)
- L4-L5 details: <0.5ms each
- Total target: <3ms per frame on mid-range device


### 1. `CORNELIA_CASTLE` (Classic FFI Sanctuary - Inspired by reference image)
*   **Palette**: Dark royal blue, deep slate grey, gold trim, warm torchlight.
*   **Visual Elements**: Well-structured castle bricks, iron-barred jail windows with light leaking through, hanging house banners, large decorative arches.
*   **Procedural Mechanics**:
    *   *Walls*: Horizontal block tiling using a pseudo-random checkerboard brightness shift.
    *   *Windows*: Sliced rectangles using `drawRect` with a soft blue-grey translucent fill (`0x33FFFFFF`) representing distant sky visible behind iron bars.
    *   *Banners*: Triangle points drawn via `Path()` at fixed intervals with a gold border outline.
    *   *Lighting*: Dual flaming torches that project soft orange `radialGradient` pools onto the wall and floor tiles.

### 2. `CHAOS_SHRINE` (The Ruined Temple of Void)
*   **Palette**: Ominous dark purple, charcoal black, chaotic void-magenta glow.
*   **Visual Elements**: Cracked, weathered stone bricks, shattered pillars, eerie mystical glyphs pulsing in the background, void rifts.
*   **Procedural Mechanics**:
    *   *Cracked Walls*: Draw thin jagged lines via staggered offsets.
    *   *Shattered Pillars*: Vertical columns drawn with top/bottom sections missing or cut off diagonally.
    *   *Void Rifts*: Central radial gradient pulsing with a sinus-based alpha (`0.2f` to `0.5f`) creating an unsettling cosmic shimmer.

### 3. `GURGU_VOLCANO` (The Fiery Depths of Earth)
*   **Palette**: Obsidian black, intense magma crimson, sulfur orange, glowing amber.
*   **Visual Elements**: Dark volcanic rock faces, a flowing river of magma running along the bottom or background, rising smoke particles.
*   **Procedural Mechanics**:
    *   *Magma Flow*: A thick horizontal strip below the wall layer drawn using an animated sine wave function: `y = baseFloorY + sin(scrollOffset * 0.1f + x * 0.05f) * 8f`. Filled with an orange-to-red vertical gradient.
    *   *Heat Shimmer*: Overlay a faint fullscreen orange tint that pulses in opacity to mimic scorching atmospheric heat waves.
    *   *Ash Particles*: Tiny orange squares floating upward with varying vertical speeds (`vy`).

### 4. `SEA_SHRINE` (The Submerged Sunken Palace)
*   **Palette**: Deep aquatic teal, sapphire blue, luminescent cyan, coral pink hints.
*   **Visual Elements**: Submerged temple bricks, underwater light rays (caustics) streaming from above, floating air bubbles.
*   **Procedural Mechanics**:
    *   *Light Caustics*: Multiple diagonal elongated triangles drawn with high transparency (`0.1f` alpha) stretching from the top-right to bottom-left, waving horizontally via a slow cosine modifier.
    *   *Bubbles*: Translucent hollow circles (`Stroke` style) rising continuously from the bottom of the screen.
    *   *Aquatic Wash*: Fullscreen cyan gradient creating a heavy "underwater depth" vignette.

### 5. `EARTH_CAVE` (The Tight, Claustrophobic Cavern)
*   **Palette**: Muddy clay brown, rich ochre, dark bronze, moss green.
*   **Visual Elements**: Rough, jagged cave walls, stalactites hanging from the ceiling, stalagmites protruding from the floor, dripping water droplets.
*   **Procedural Mechanics**:
    *   *Cave Silhouette*: Top and bottom bounds of the background are drawn as highly irregular jagged polygons via `Path()`.
    *   *Drips*: Tiny blue circles that drop vertically from stalactite tips at random intervals, splashing with a expanding ring on the floor.

### 6. `CRYSTAL_TOWER` / `CRYSTAL_TOWER_DIM3` (The Shimmering Mirror Apex)
*   **Palette**: Luminescent cyan, crystal white, diamond blue, silver highlights.
*   **Visual Elements**: Massive translucent crystal columns, reflective mirror-like tiling, sparkling stardust particles.
*   **Procedural Mechanics**:
    *   *Crystal Columns*: Tall vertical polygons drawn using sharp angle paths with multi-shaded cyan/white vertical gradients to look highly faceted and geometric.
    *   *Shimmer Effect*: A diagonal white linear gradient sweep across the columns that scrolls infinitely to simulate brilliant light reflection.
    *   *Glitter*: Diamond-shaped particles pulsing randomly in size and brightness.

### 7. `MYSIDIAN_TOWER` (The Wizard's Arcane Archives)
*   **Palette**: Deep cosmic indigo, starry midnight blue, glowing runic gold.
*   **Visual Elements**: Endless rows of towering bookshelves in the distant parallax, floating magical scrolls, glowing runic arrays on the stone walls.
*   **Procedural Mechanics**:
    *   *Bookshelves*: Horizontal rows filled with small multicolored vertical rectangular strips representing ancient tomes.
    *   *Magical Glyphs*: Fine circle outlines inscribed with geometric lines, pulsing slowly with an arcane gold/yellow color.

### 8. `PANDAEMONIUM` (The Palace of Hell)
*   **Palette**: Jade green jadeite, bone white, twisted iron black, venomous green lighting.
*   **Visual Elements**: Demonic architecture, ribs or bony pillars along the walls, glowing green liquid pools, floating spirits.
*   **Procedural Mechanics**:
    *   *Bone Pillars*: Segmented white rounded-rectangles nested together to look like a giant spinal column.
    *   *Jade Fountains*: Poisonous green radial light pools leaking out from wall vents.

### 9. `MOUNT_ORDEALS` (The Peak of Trial & Light)
*   **Palette**: Dusk orange, mountain blue, sacred white, pale twilight.
*   **Visual Elements**: Open sky with rolling mountain peaks, a stone path along a precarious ridge, falling sacred cherry blossom petals or holy light beams.
*   **Procedural Mechanics**:
    *   *Parallax Ridges*: 3 overlapping layers of mountain paths moving at `0.1x`, `0.3x`, and `0.6x` speeds.
    *   *Holy Aura*: Soft vertical columns of golden-white light stretching down from the top.

### 10. `BARON_CASTLE` (The Dark Knight's Iron Stronghold)
*   **Palette**: Industrial steel grey, iron navy, crimson drapes.
*   **Visual Elements**: Reinforced metallic walls, large hanging red flags, high stone parapets.
*   **Procedural Mechanics**:
    *   *Riveted Panels*: Large rectangular blocks with tiny square dots at the corners representing iron rivets.

### 11. `ANCIENT_CASTLE` (The Sunken Desert Ruin)
*   **Palette**: Sand gold, dusty beige, eroded brown.
*   **Visual Elements**: Half-buried pillars, sand dunes winds blowing across, crumbling walls.
*   **Procedural Mechanics**:
    *   *Sand Drifts*: Flowing bezier curves at the bottom representing sand piles covering the floor tiles.

### 12. `NARSHE_MINES` (The Steampunk Winter Mineshaft)
*   **Palette**: Frosted iron grey, ice cyan, wooden beam brown, dim lantern gold.
*   **Visual Elements**: Wooden support beams, icicles hanging down, dark shafts extending into the background, coal ore veins.
*   **Procedural Mechanics**:
    *   *Support Trusses*: Thick interlocking brown diagonal bars forming structural mine support frames.

### 13. `MAGITEK_FACTORY` (The Industrial Empire Complex)
*   **Palette**: Heavy rust iron, copper orange, glowing chemical green tubes, cooling steam.
*   **Visual Elements**: Massive bundles of pipes, glowing fluid conduits, rotating ventilation fans, warning lights.
*   **Procedural Mechanics**:
    *   *Pipes & Cables*: Overlapping horizontal and vertical lines of varying thicknesses drawn with metallic center-highlights.
    *   *Conduit Fluid*: Neon green lines animating their dash patterns to simulate glowing liquid moving through glass tubes.

### 14. `KEFKA_TOWER` (The Twisted Fragmented Monument)
*   **Palette**: Chaos gold, mad god purple, mismatched neon colors.
*   **Visual Elements**: Surreal mashed-up architectural elements, asymmetrical ruins, floating debris.
*   **Procedural Mechanics**:
    *   *Debris*: Blocks floating at completely distinct angles using canvas rotate operations.

### 15. `FLOATING_CONTINENT` (The High Altitude Crag)
*   **Palette**: Sky blue, cloud white, jagged mountain slate.
*   **Visual Elements**: Floating islands drifting in the far background, heavy rushing clouds beneath the floor level.
*   **Procedural Mechanics**:
    *   *Cloud Sea*: Overlapping cream-white circles flowing horizontally at different rates below the combat floor line.

### 16. `MIDGAR_SEWERS` (The Slimy Slum Tunnels)
*   **Palette**: Dirty moss green, toxic slime yellow, slimy brick grey.
*   **Visual Elements**: Semicircular brick tunnels, sludge water channels, leaking overhead pipes.
*   **Procedural Mechanics**:
    *   *Tunnel Arch*: Concentric curved paths drawn overhead to form a circular drainage tunnel perspective.

### 17. `SHINRA_BUILDING` (The Modern Corporate Neon Tower)
*   **Palette**: Cyberpunk dark steel, electric cyan glass, neon red signage accents.
*   **Visual Elements**: Sleek metal grids, tinted window panels with city lights reflection, digital tickers.
*   **Procedural Mechanics**:
    *   *Grid Walls*: Ultra-crisp square patterns with thin cyan glowing border outlines.

### 18. `NORTHERN_CRATER` (The Cosmic Lifestream Wound)
*   **Palette**: Deep celestial navy, glowing lifestream green, alien bone white.
*   **Visual Elements**: Swirling energy vortexes, crystalline ribs, bottomless glowing pits.
*   **Procedural Mechanics**:
    *   *Lifestream*: Upward spiraling bezier curves drawn with semi-transparent emerald green lines.

### 19. `GOLDEN_SAUCER` (The Blinking Amusement Matrix)
*   **Palette**: Bright gold, neon magenta, flashing rainbow dots.
*   **Visual Elements**: Grid matrices, casino lighting tracks, confetti.
*   **Procedural Mechanics**:
    *   *Chaser Lights*: Dots changing color based on animation frame modules (`tick % 4`).

### 20. `BEVELLE_TEMPLE` (The Mechanical Ribbon Sanctuary)
*   **Palette**: Ivory white, pristine marble, floating glowing energetic ribbons.
*   **Visual Elements**: Perfect symmetric marble pillars, cyan light tracks.
*   **Procedural Mechanics**:
    *   *Ribbons*: Smooth horizontal waves drawn with neon blue lines twisting via multiple overlaid sine speeds.

### 21. `OMEGA_RUINS` (The Glitched Digital Dimension)
*   **Palette**: Ultramarine blue, glitch black, cyber code green.
*   **Visual Elements**: Fractured holographic blocks, code matrices, scanning lasers.
*   **Procedural Mechanics**:
    *   *Glitch Blocks*: Displaced rectangles that flicker completely out of position for single frames using a random number seed based on time.

### 22. `SIN_INTERIOR` (The Organic Cosmic Abyssal Core)
*   **Palette**: Meaty organic maroon, void black, pulsing nerve red.
*   **Visual Elements**: Pulsing bio-walls, cosmic star backgrounds seen through organic ribs, nightmare geometry.
*   **Procedural Mechanics**:
    *   *Bio-Pulse*: Scale the color brightness or wall path thicknesses up and down using a organic heartbeat wave function.

### 23. `GENERIC_DUNGEON` (The Fallback Dungeon)
*   *Current implementation*: Standard dark stone bricks, torches, and chains.

---

## 🛠️ Step-by-Step Refactoring & Implementation Plan

To convert this plan into clean, production code without breaking anything, we will take a highly organized, step-by-step approach:

### Step 1: Update `PixelBackground.kt` Structure
We will modify `DungeonBackground` to accept an optional `BiomeType`:
```kotlin
@Composable
fun DungeonBackground(biomeType: BiomeType? = BiomeType.GENERIC_DUNGEON) {
    // 1. Setup shared animations (torchFlicker, scrollOffset, pulseAlpha, waveTick)
    // 2. Canvas switch statement based on biomeType
}
```

### Step 2: Implement Componentized Drawing Primitives
To keep the file highly legible and clean, we will break the rendering logic into fast private extension methods on `DrawScope` inside `PixelBackground.kt`:
*   `private fun DrawScope.drawCorneliaCastle(scrollOffset: Float, torchFlicker: Float)`
*   `private fun DrawScope.drawGurguVolcano(scrollOffset: Float, waveTick: Float)`
*   `private fun DrawScope.drawSeaShrine(scrollOffset: Float, bubbleTick: Float)`
*   `private fun DrawScope.drawCrystalTower(scrollOffset: Float, shimmerTick: Float)`
*   *(And so on for each distinct visual style)*

### Step 3: Wire into `DungeonScreen.kt`
Update the invocation inside `DungeonScreen` to pass the live biome type from the game state:
```kotlin
// In DungeonScreen.kt:
DungeonBackground(biomeType = state.currentBiome?.backgroundType ?: BiomeType.GENERIC_DUNGEON)
```

### Step 4: Verification and Quality Assurance Audit
*   **Performance Monitoring**: Run frame-rendering verification to ensure the complex geometry functions execute well under 16ms (target <2ms per frame).
*   **UI Test Checks**: Run the instrumented UI tests via `./gradlew connectedDebugAndroidTest` to ensure that swapping the canvas backgrounds doesn't intercept touch targets or break the battle screen layout overlays.

---

## 📝 Modification Log
*Consulted skills:* `SKILL_LOCALIZATION_PROTOCOL.md` (ensuring zero hardcoded strings are present in any visual tags, as everything is geometric drawing), `SKILL_TESTING_PROTOCOL.md` (re-verifying standard test paths).
