# Skill: Sprite Matrix Conversion Tool (`convert.py`)

This document defines the usage, architecture, color palette mapping, background auto-detection, halo trimming, aspect ratio preservation, batch execution, and automated code replacement workflow for converting PNG images into Kotlin 2D pixel matrices (`drawPixelMatrix()` / `drawMonsterMatrix()`) using the `convert.py` Python script in the repository root.

---

## 🛠️ Overview & Purpose

`convert.py` is an automated Python utility located in the project root (`/convert.py`) that scans the `sprites/` directory (or a single `input.png` file) and converts raster images into JRPG-style 2D string matrices (`val matrix = arrayOf(...)`) and color palettes (`val palette = mapOf(...)`).

It automatically detects image backgrounds (transparent PNGs, white, or light gray), trims anti-aliasing halos, calculates grid dimensions to preserve aspect ratios, generates complete Kotlin functions (e.g., `fun DrawScope.drawDjinn()`), and updates `PixelCharacters.kt` directly.

This tool automates the pixel-by-pixel transcription required by `SKILL_HERO_SPRITES.md`.

---

## 💻 Manual Execution Command / Comandos de Ejecución Manual

To manually convert sprites and update `PixelCharacters.kt`:

python3 -m pip install opencv-python numpy

1. **Place your PNG image(s)** into the `sprites/` directory in the project root (e.g., `sprites/Djinn.png`, `sprites/Borghen.png`, `sprites/BlackMage.png`).
2. **Execute the script** from the project root in your terminal:
   ```bash
   python3 convert.py
   ```
3. **Verify the build**:
   ```bash
   ./gradlew :app:compileDebugKotlin
   ```

*Prerequisites*: Requires Python 3 with OpenCV and NumPy (`pip install opencv-python numpy`).

---

## ⚙️ Configuration Parameters & Aspect Ratio Scaling

Key settings defined in `convert.py`:

```python
SPRITES_DIR = "sprites"                                                    # Batch input directory
SINGLE_IMAGE_PATH = "input.png"                                            # Single-file fallback
PIXEL_CHARACTERS_PATH = "app/src/main/java/com/game/dungeon/ui/components/PixelCharacters.kt" # Target Kotlin file

MAX_GRID_SIZE = 64         # Maximum grid dimension (rows or cols)
SAMPLE_RADIUS = 2          # Pixel sampling radius around grid cell center
BG_COLOR_THRESHOLD = 55    # Color distance threshold to match detected background
BG_HALO_THRESHOLD = 85     # Distance threshold for outer boundary anti-aliasing halo cleanup
```

### 📐 Dynamic Aspect Ratio Preservation (`calculate_grid_size`)
The script automatically scales grid dimensions (`rows` and `cols`) to match the image's original aspect ratio:
- If `height >= width`: sets `rows = MAX_GRID_SIZE` (64) and `cols = round(64 * (width / height))`.
- If `width > height`: sets `cols = MAX_GRID_SIZE` (64) and `rows = round(64 * (height / width))`.

*Example:* An image of `360x424` pixels is scaled to a `54x64` matrix grid, keeping the character's exact proportions without stretching or squishing.

---

## 🧹 Background Auto-Detection & Halo Trimming

To prevent white or light gray border artifacts around sprite silhouettes:

1. **Alpha Channel & Background Auto-Detection (`detect_background_color`)**:
   - Detects if the PNG has an Alpha channel (`cv2.IMREAD_UNCHANGED`).
   - If opaque, samples the 4 image corners to detect the exact background color (white `(255,255,255)`, light gray, dark, etc.).
2. **Transparent / Background Cell Identification**:
   - Cells with average alpha < 128 or RGB distance to background color <= `BG_COLOR_THRESHOLD` are flagged as background candidates.
3. **Flood-Fill BFS from Outer Image Edges**:
   - Flood fills connected background candidates starting from the 4 outer edges of the matrix array.
4. **Outer Boundary Halo Cleanup (Anti-Aliasing Removal)**:
   - Identifies non-background cells that touch the outer `.` background.
   - If a boundary cell contains anti-aliasing artifact colors (light gray `'T'`, gray `'S'`, white `'W'`, or RGB distance to background < `BG_HALO_THRESHOLD`), it is trimmed to `.`.
   - This cleans outer borders while leaving interior white highlights (teeth, eyes, sclera, white clothes) completely intact.

---

## 🎨 Color Palette & Character Mapping

The script samples cell colors and maps RGB values to a predefined JRPG palette:

| Category | Chars | Color Description & RGB Values |
| :--- | :--- | :--- |
| **Grays / Mono** | `K`, `D`, `S`, `T`, `W` | Black (`K`), Dark Gray (`D`), Gray (`S`), Light Gray (`T`), White (`W`) |
| **Browns** | `N`, `A`, `C` | Dark Brown (`N`), Brown (`A`), Light Brown (`C`) |
| **Oranges** | `O`, `J` | Orange (`O`), Light Orange (`J`) |
| **Skin / Peach** | `P`, `Q`, `E`, `F` | Dark Skin (`P`), Skin (`Q`), Light Skin (`E`), Skin Highlight (`F`) |
| **Reds** | `R`, `Z`, `X` | Dark Red (`R`), Red (`Z`), Bright Red (`X`) |
| **Pinks** | `r`, `p`, `q` | Dark Pink (`r`), Pink (`p`), Light Pink (`q`) |
| **Yellows / Gold** | `1`, `2`, `3`, `4` | Gold (`1`), Yellow (`2`), Bright Yellow (`3`), Pale Yellow (`4`) |
| **Greens** | `G`, `g`, `L`, `l` | Dark Green (`G`), Green (`g`), Light Green (`L`), Bright Green (`l`) |
| **Teals / Cyans** | `t`, `c`, `d` | Dark Cyan (`t`), Cyan (`c`), Light Cyan (`d`) |
| **Blues** | `B`, `U`, `V` | Dark Blue (`B`), Blue (`U`), Light Blue (`V`) |
| **Purples** | `M`, `I`, `m`, `i` | Dark Purple (`M`), Purple (`I`), Light Purple (`m`), Lavender (`i`) |
| **Background** | `.` | Transparent / Skipped background pixels |

---

## 🔄 Conversion & Replacement Pipeline

1. **Batch Image Discovery**:
   - Scans `sprites/*.png` (case-insensitive).
   - If `sprites/` is empty, falls back to `input.png` if present in root.

2. **Function Naming (`filename_to_func_name`)**:
   - Converts filenames to CamelCase function names starting with `draw`.
   - Examples:
     - `Borghen.png` ➔ `drawBorghen()`
     - `Gottos.png` ➔ `drawGottos()`
     - `black_mage.png` ➔ `drawBlackMage()`

3. **Hero vs. Monster Orientation Detection (`is_hero_function`)**:
   - Compares the function name against the `HERO_NAMES` set.
   - If **Hero**: generates `drawPixelMatrix(matrix, palette)` (faces Right).
   - If **Monster**: generates `drawMonsterMatrix(matrix, palette)` (faces Left via horizontal mirroring).

4. **Background Removal & Halo Trimming (`image_to_clean_matrix`)**:
   - Samples color/alpha, runs outer flood-fill, and cleans anti-aliasing halos on the outer boundary.

5. **Automated Replacement in `PixelCharacters.kt` (`replace_function_in_file`)**:
   - Uses regex matching `fun DrawScope.<func_name>() { ... }` in `PixelCharacters.kt`.
   - Replaces the function in `PixelCharacters.kt` directly or appends if missing.

---

## 📋 Standard Batch Usage Workflow

1. Place image files into the `sprites/` directory (e.g. `sprites/Borghen.png`, `sprites/Gottos.png`).
2. Run the script:
   ```bash
   python3 convert.py
   ```
3. The script processes each image, removes background and anti-aliasing halos, generates the Kotlin code, and updates `PixelCharacters.kt` directly.
4. Verify build and rendering:
   ```bash
   ./gradlew :app:compileDebugKotlin
   ```
