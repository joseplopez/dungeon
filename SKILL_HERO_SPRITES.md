# Skill: Hero & Monster Pixel-by-Pixel Sprite Protocol

This document defines the mandatory visual design, architecture, color palette, and pixel-by-pixel refactoring strategy for all hero class sprites and monster/NPC sprites in `PixelCharacters.kt`.

---

## 🎨 Mandatory Pixel-by-Pixel Redesign Rule

> [!IMPORTANT]
> **PIXEL-BY-PIXEL REQUIREMENT FOR ALL HEROES AND MONSTERS**:
> From now on, ALL redesigns of new and existing hero class sprites, monster sprites, and NPC sprites MUST be defined **100% pixel-by-pixel** directly from reference grid images using `drawPixelMatrix()`.
>
> - **DO NOT force a fixed canvas resolution (e.g. 64x64) if the reference sprite grid is smaller** (e.g. 22x26, 16x20, 24x32, 64x32, etc.).
> - **PRESERVE EXACT GRID DIMENSIONS**: Transcribe the reference image grid with its exact width and height. Every string row in the matrix MUST have the **exact same character length** so that `drawPixelMatrix()` renders every tile as a perfect 1:1 square without stretching, squishing, or distorting aspect ratios.
> - **MANDATORY ORIENTATION RULE**: ALL Hero sprites MUST ALWAYS face RIGHT (3/4 profile view facing right, looking right).

---

## 📐 Pixel Matrix Architecture (`drawPixelMatrix()`)

When refactoring a hero or monster sprite from a reference pixel grid image, agents MUST transcribe the reference image **pixel-by-pixel** into a 2D string matrix array and render it using `drawPixelMatrix()`:

```kotlin
private fun DrawScope.drawPixelMatrix(
    matrix: Array<String>,
    palette: Map<Char, Color>
) {
    val rows = matrix.size
    val cols = matrix.maxOf { it.length }
    val tileSize = minOf(size.width / cols, size.height / rows)
    val offsetX = (size.width - cols * tileSize) / 2f
    val offsetY = (size.height - rows * tileSize) / 2f

    for (r in 0 until rows) {
        val rowStr = matrix[r]
        for (c in 0 until rowStr.length) {
            val char = rowStr[c]
            val color = palette[char] ?: continue
            drawRect(
                color = color,
                topLeft = Offset(offsetX + c * tileSize, offsetY + r * tileSize),
                size = Size(tileSize, tileSize)
            )
        }
    }
}
```

### Key Advantages of Uniform Matrix `drawPixelMatrix()`:
1. **1:1 Exact Aspect Ratio**: Keeps original character proportions identical to reference artwork without warping.
2. **Transparent Background**: Empty grid cells represented by `.` (or unmapped chars) are automatically skipped, ensuring transparent backgrounds.
3. **Clean Code & Readability**: Matrix characters map directly to clear color keys (e.g., `'K'` = Black outline, `'Y'` = Yellow hat, `'B'` = Blue robe).

---

## 🏛️ Mandatory Hero Anatomy & Proportions

Every hero sprite MUST follow these proportional guidelines across the grid:

| Region | Key Elements & Techniques |
| :--- | :--- |
| **1. Hair / Headgear** | Dynamic spiky hair locks, wizard hats, bandannas, or hooded cowls. Hoods must feature a pointed floppy cowl tail curving back-left. |
| **2. Face Profile** | Profile / 3/4 view facing right. Skin base (`SkinLight`), jaw/chin shadow (`SkinMid`), white eye sclera with dark iris/pupil looking right. |
| **3. Neck / Chin Line** | **Horizontal Black Separation Line**: A 1-2px dark outline (`Color(0xFF17202A)`) separating head/hood from torso. |
| **4. Torso & Arm Divider** | **Vertical Black Divider Line**: A 1-2px dark outline running vertically from chin down to hem, separating back cape/arm on left from front robe/tunic on right. |
| **5. Waist / Belt** | Thick silver/gold/leather belt across waist with top highlight and bottom shadow, framed by black border lines. |
| **6. Pants / Lower Robe** | Trousers with leg divider seam or white robe hem featuring iconic red saw-tooth dagged triangle motifs. |
| **7. Boots & Soles** | Metallic/colored boot cuffs, boot body, and dark black sole base. |
| **8. Weapon & Hand** | Weapons (wand, staff, sword, rapier) MUST be held in front (right side) by a visible peach skin or mitten hand. |
| **9. Dark Outline** | Outer Boundary: Crisp dark navy/black outline (`Color(0xFF17202A)`) completely outlining the sprite's silhouette. |

---

## 🗝️ Essential Architectural Rules Learned from Sprite Construction

1. **Mandatory Orientation (Facing Right)**:
   - ALL hero sprites MUST face RIGHT (3/4 profile view facing right, looking right).

2. **Pixel-by-Pixel Copying (`drawPixelMatrix`)**:
   - Transcribe pixel grid reference images directly into a 2D `Array<String>` matrix and use `drawPixelMatrix(matrix, palette)`.

3. **Head-from-Body Separation Line (Horizontal)**:
   - Always draw a distinct horizontal dark/black line (`Color(0xFF17202A)`) under the chin/neck to clearly separate the head/hood from the torso.

4. **Arm/Cape Divider Line (Vertical)**:
   - Draw a vertical black divider line (`Color(0xFF17202A)`) running from the chin down through the torso.
   - This line clearly separates the back cape/sleeve/arm on the left from the front robe/tunic on the right.

5. **Solid Color Masses Over Noise**:
   - Capes, robes, and sleeves must use **clean solid color blocks** with 1-2 pixel dark shading along fold edges. Avoid noisy checkerboard patterns or random pixel clusters inside solid garment regions.

6. **Weapon & Hand Placement**:
   - Weapons, staves, wands, and rapiers MUST be placed in front of the hero on the right side, held firmly by a visible front hand or mitten glove.

---

## 🎨 Standard JRPG Color Palettes

Always use 3-tone shading (Highlight, Base, Shadow) for main colors:

- **Red (Warrior / Red Mage)**: Highlight `0xFFFF5252`, Base `0xFFD32F2F`, Shadow `0xFF8B0000`
- **White Mage (White & Red)**: Robe `0xFFFFFFFF`, Robe Shadow `0xFFD5D8DC`, Red Trim `0xFFE74C3C`, Dark Boot Red `0xFF922B21`
- **Green (Thief / Ranger)**: Highlight `0xFF2ECC71`, Base `0xFF27AE60`, Shadow `0xFF196F3D`
- **Blue (Black Mage / Knight)**: Highlight `0xFF5DADE2`, Base `0xFF3498DB`, Shadow `0xFF1B4F72`
- **Gold / Yellow (Monk / Paladin)**: Highlight `0xFFF4D03F`, Base `0xFFF1C40F`, Shadow `0xFFB7950B`
- **Silver / Metal (Armor / Belts / Cuffs)**: Highlight `0xFFFFFFFF`, Base `0xFFE0E0E0`, Shadow `0xFF8E8E8E`
- **Skin Tone**: Light `0xFFFFDBAC`, Shadow `0xFFE5A36F`
- **Dark Outline**: `Color(0xFF17202A)`

---

## 🗺️ Hero Class Refactoring Roadmap

Each hero class must be refactored using this protocol:

1. **`WARRIOR`** *(Completed)*: Spiky crimson red hair, 3/4 right profile face, red tunic vest, left pauldron with silver accent plate, silver belt, red pants, and silver-cuffed red boots.
2. **`WHITE_MAGE`** *(Completed)*: Floppy pointed cowl hood curving back-left, dark red hair bangs, right-facing face with black vertical eye pupil, horizontal black chin line, vertical black arm/cape divider line, solid red back cape, white front robe with red saw-tooth dagged triangles along lower hem, deep red boots, and golden-headed wooden wand held in right hand.
3. **`BLACK_MAGE`** *(Completed)*: Pointed yellow wizard hat leaning back-left with wide brim, pitch-black shadow face with glowing yellow eyes looking right, horizontal black chin separation line, cyan/blue robe with vertical black arm/sleeve divider line, yellow mitten glove on right hand, blue boot tips, and bottom black sole. Copy 100% pixel-by-pixel via `drawPixelMatrix`.
4. **`THIEF`**: Green bandanna with sweeping hair tuft, green tunic with cross-body leather straps, daggers at waist, agile boots.
5. **`MONK`**: Martial headband, sleeveless yellow gi/tunic with black belt sash, wrapped wrist bands and cloth boot wraps.
6. **`RED_MAGE`**: Crimson feathered cap, stylish red doublet with white collar, rapier, polished leather boots.
7. **`KNIGHT`**: Full silver plate armor with blue cape, crested helmet with open face visor option, broadsword/shield.
8. **`PALADIN`**: Radiant gold/white plate armor, holy blue mantle, glowing shield and broadsword.
9. **`NINJA`**: Dark cowl mask with glowing red eyes, red flowing scarf, dual katanas, dark ninja shozoku outfit.
10. **`DRAGOON`**: Purple/dragon scale armor, horned dragon helm, long polearm spear, wing pauldrons.
11. **`BARD`**: Feathered cap, lute/harp instrument, green & pink tunic with silver trim.
12. **`SUMMONER`**: Green robe with white horn headband, gold waist ornament, summoner horn.
13. **`SAMURAI`**: Full Kabuto helmet with gold crescent moon crest, red/black banded armor (Laminar), katana at hip.
14. **`ONION_KNIGHT`**: Classic silver helmet with feather plume, blue tunic, small shield.
15. **`MIME`**: Colorful pink & green doublet, feathered cap, white gloves.
16. **`FREELANCER`**: Simple brown vest, blue trousers, brown travel boots, messy brown hair.

---

## 🧪 Preview & Verification Protocol

When refactoring a hero sprite:
1. Update `draw<Hero>()` in `PixelCharacters.kt` using `drawPixelMatrix()` or `px64()`.
2. Inspect the sprite in `HeroSpritesPreview.kt` (`WarriorSpritePreview`, `WhiteMageSpritePreview`, `BlackMageSpritePreview`, or `AllHeroSpritesPreview`).
3. Render the preview using `render_compose_preview` to verify visual accuracy.
4. Ensure the project builds cleanly with `./gradlew :app:assembleDebug`.
5. Run unit tests with `./gradlew :app:testDebugUnitTest`.

---

## 📝 Agent Execution Checklist

Before considering a hero sprite refactor complete:
- [ ] Transcribed pixel grid image directly to 2D `Array<String>` matrix via `drawPixelMatrix()`.
- [ ] Facing RIGHT orientation maintained.
- [ ] Horizontal black chin/neck line separates head from torso.
- [ ] Vertical black line separates back cape/arm from front tunic/robe.
- [ ] Hand / Glove placed on right side.
- [ ] Dark outline (`0xFF17202A`) frames outer boundary.
- [ ] Code compiles cleanly with `./gradlew :app:assembleDebug`.
- [ ] Unit tests pass with `./gradlew :app:testDebugUnitTest`.
- [ ] Rendered preview verified with `render_compose_preview`.

*Always read this file before refactoring any hero sprite in `PixelCharacters.kt`.*
