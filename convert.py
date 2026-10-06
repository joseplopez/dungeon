import cv2
import math
import os
import re
import glob
import numpy as np
from collections import deque
from pathlib import Path


# ============================================================
# CONFIGURATION
# ============================================================

SPRITES_DIR = "sprites"
SINGLE_IMAGE_PATH = "input.png"
PIXEL_CHARACTERS_PATH = "app/src/main/java/com/game/dungeon/ui/components/PixelCharacters.kt"

MAX_GRID_SIZE = 64

SAMPLE_RADIUS = 2

# Background color tolerance
BG_COLOR_THRESHOLD = 55
BG_HALO_THRESHOLD = 85

HERO_NAMES = {
    "warrior", "thief", "monk", "whitemage", "white_mage", "white-mage",
    "blackmage", "black_mage", "black-mage", "redmage", "red_mage", "red-mage",
    "knight", "paladin", "ninja", "dragoon", "bard", "summoner", "samurai",
    "freelancer", "onionknight", "onion_knight", "onion-knight", "mime",
    "necromancer", "bluemage", "blue_mage", "blue-mage"
}


# ============================================================
# BASIC COLOR PALETTE
# ============================================================

PALETTE = {
    # ============================================================
    # 1. MONOCHROME & METALS
    # ============================================================
    '0': (12, 12, 16),        # Near Black / Deep Shadow
    '1': (38, 38, 48),        # Dark Charcoal
    '2': (72, 72, 85),        # Mid Steel Gray
    '3': (115, 118, 132),     # Neutral Gray
    '4': (162, 166, 180),     # Light Steel Gray
    '5': (208, 212, 224),     # Silver / Pale Gray
    '6': (250, 250, 255),     # Pure White Highlight

    # ============================================================
    # DARK TEAL / SLATE GREEN (Airbuster Body, Magitek, Mech Armor)
    # ============================================================
    'g': (18, 38, 38),        # Shadow Dark Teal
    'a': (32, 68, 68),        # Deep Airbuster Teal
    'b': (52, 98, 98),        # Mid Teal Green
    'K': (78, 128, 128),      # Light Teal Green
    'v': (118, 168, 168),     # Pale Teal Highlight

    # ============================================================
    # BLUE
    # ============================================================
    '7': (18, 28, 68),        # Deep Navy / Abyss Blue
    '8': (32, 58, 122),       # Dark Royal Blue
    '9': (52, 98, 182),       # Mid Azure Blue
    'B': (88, 142, 228),      # Bright Sky Blue
    'U': (148, 188, 248),     # Ice Blue / Light Blue
    'V': (205, 228, 255),     # Pale Blue Highlight

    # ============================================================
    # CYAN & AQUA
    # ============================================================
    'C': (15, 52, 68),        # Deep Teal / Dark Ocean
    'c': (28, 92, 112),       # Mid Aqua
    'd': (52, 142, 165),      # Cyan / Bright Water
    'e': (102, 188, 205),     # Ice Teal
    'f': (168, 225, 235),     # Light Ice Blue

    # ============================================================
    # GREEN & ACID
    # ============================================================
    'G': (15, 52, 28),        # Dark Forest / Poison Shadow
    'H': (28, 98, 48),        # Deep Monster Green
    'L': (52, 152, 72),       # Bright Green
    'h': (98, 198, 98),       # Light Leaf Green
    'i': (162, 235, 142),     # Lime / Acid Highlight
    'j': (218, 252, 192),     # Pale Mint

    # ============================================================
    # YELLOW & GOLD
    # ============================================================
    'J': (112, 82, 12),       # Dark Ochre / Gold Shadow
    'Y': (218, 172, 32),      # Golden Yellow
    'k': (248, 212, 58),      # Bright Yellow
    'l': (255, 238, 118),     # Light Yellow
    'm': (255, 252, 192),     # Pale Lemon Highlight

    # ============================================================
    # ORANGE & AMBER
    # ============================================================
    'O': (118, 42, 12),       # Dark Rust / Deep Amber
    'n': (178, 72, 22),       # Terracotta / Burnt Orange
    'o': (228, 112, 32),      # Flame Orange
    'p': (248, 158, 62),      # Light Orange / Amber
    'q': (255, 202, 122),     # Soft Peach Highlight

    # ============================================================
    # RED & CRIMSON
    # ============================================================
    'R': (92, 15, 22),        # Dark Maroon / Deep Crimson
    'S': (148, 28, 38),       # Deep Red
    'Z': (208, 42, 48),       # True Red
    'X': (242, 82, 72),       # Bright Coral Red
    'r': (252, 138, 122),     # Light Salmon / Red Highlight

    # ============================================================
    # MAGENTA & PINK
    # ============================================================
    'M': (88, 18, 52),        # Dark Wine / Shadow Red-Purple
    'N': (142, 32, 88),       # Deep Magenta
    'P': (198, 52, 128),      # Bright Hot Pink
    's': (238, 102, 172),     # Light Pink
    't': (252, 168, 212),     # Soft Pink Highlight

    # ============================================================
    # PURPLE & VIOLET
    # ============================================================
    'Q': (48, 22, 68),        # Shadow Purple / Void
    'I': (85, 38, 122),       # Deep Violet
    'W': (132, 68, 182),      # Bright Purple
    'u': (178, 112, 225),     # Lavender / Light Purple

    # ============================================================
    # BROWNS & EARTH
    # ============================================================
    'A': (48, 28, 18),        # Dark Bark Brown
    'D': (88, 48, 28),        # Medium Leather Brown
    'E': (132, 78, 45),       # Earth Brown
    'F': (178, 118, 72),      # Tan / Light Wood
    'w': (218, 162, 112),     # Sand / Beige

    # ============================================================
    # FLESH & PEACH
    # ============================================================
    'x': (142, 78, 62),       # Shadow Skin / Dusky Shadow
    'y': (192, 118, 92),      # Warm Mid Skin
    'z': (235, 162, 132),     # Light Flesh Tone
    'T': (255, 202, 175),     # Peach Highlight
}


# ============================================================
# COLOR DISTANCE
# ============================================================

def color_distance(c1, c2):
    """
    Weighted RGB distance (redmean formula) that matches human visual perception.
    Prevents dark greens/teals from collapsing into grays.
    """
    r1, g1, b1 = c1
    r2, g2, b2 = c2
    r_mean = (r1 + r2) / 2.0
    dr = r1 - r2
    dg = g1 - g2
    db = b1 - b2

    return math.sqrt((2 + r_mean / 256.0) * dr**2 + 4.0 * dg**2 + (2 + (255 - r_mean) / 256.0) * db**2)

def nearest_palette_color(rgb):
    best_char = None
    best_distance = float("inf")
    for char, palette_rgb in PALETTE.items():
        distance = color_distance(rgb, palette_rgb)
        if distance < best_distance:
            best_distance = distance
            best_char = char
    return best_char


# ============================================================
# CALCULATE DYNAMIC GRID DIMENSIONS (ASPECT RATIO PRESERVATION)
# ============================================================

def calculate_grid_size(image, max_dim=MAX_GRID_SIZE):
    height, width = image.shape[:2]
    if height >= width:
        rows = max_dim
        cols = max(1, int(round(max_dim * (width / height))))
    else:
        cols = max_dim
        rows = max(1, int(round(max_dim * (height / width))))
    return rows, cols


# ============================================================
# BACKGROUND & ALPHA DETECTION AND REMOVAL
# ============================================================

def detect_background_color(image):
    """
    Detects background color from the 4 corners of the image.
    Returns (bg_bgr, has_alpha).
    """
    has_alpha = len(image.shape) == 3 and image.shape[2] == 4
    corners = [image[0, 0], image[0, -1], image[-1, 0], image[-1, -1]]

    if has_alpha:
        corner_alphas = [c[3] for c in corners]
        if np.mean(corner_alphas) < 128:
            # Transparent background
            return None, True

    corners_bgr = [c[:3] for c in corners]
    bg_bgr = np.mean(corners_bgr, axis=0)
    return bg_bgr, has_alpha


def image_to_clean_matrix(image, rows, cols):
    height, width = image.shape[:2]
    bg_bgr, has_alpha = detect_background_color(image)

    if bg_bgr is not None:
        bg_rgb = (int(round(bg_bgr[2])), int(round(bg_bgr[1])), int(round(bg_bgr[0])))
    else:
        bg_rgb = None

    bg_candidates = np.zeros((rows, cols), dtype=bool)
    matrix = []
    rgb_matrix = []

    for r in range(rows):
        y1, y2 = int(r * height / rows), int((r + 1) * height / rows)
        row_chars = []
        row_rgbs = []

        for c in range(cols):
            x1, x2 = int(c * width / cols), int((c + 1) * width / cols)
            cell = image[y1:y2, x1:x2]

            if cell.size == 0:
                bg_candidates[r, c] = True
                row_chars.append('.')
                row_rgbs.append((255, 255, 255))
                continue

            # Handle Alpha Transparency
            if has_alpha:
                alphas = cell[:, :, 3]
                trans_fraction = np.mean(alphas < 128)
                if trans_fraction > 0.35:
                    bg_candidates[r, c] = True
                b, g, r_val = cell[:, :, :3].mean(axis=(0, 1))
            else:
                b, g, r_val = cell.mean(axis=(0, 1))

            rgb = (int(round(r_val)), int(round(g)), int(round(b)))
            char = nearest_palette_color(rgb)

            row_chars.append(char)
            row_rgbs.append(rgb)

            # Check distance to background color
            if bg_rgb is not None:
                d_to_bg = color_distance(rgb, bg_rgb)
                if d_to_bg <= BG_COLOR_THRESHOLD:
                    bg_candidates[r, c] = True

        matrix.append(row_chars)
        rgb_matrix.append(row_rgbs)

    # Flood fill connected background from outer edges
    visited = np.zeros((rows, cols), dtype=bool)
    queue = deque()

    for c in range(cols):
        if bg_candidates[0, c]: queue.append((0, c))
        if bg_candidates[rows - 1, c]: queue.append((rows - 1, c))
    for r in range(rows):
        if bg_candidates[r, 0]: queue.append((r, 0))
        if bg_candidates[r, cols - 1]: queue.append((r, cols - 1))

    while queue:
        r, c = queue.popleft()
        if visited[r, c]: continue
        visited[r, c] = True
        if not bg_candidates[r, c]: continue

        matrix[r][c] = '.'

        for dr, dc in [(-1, 0), (1, 0), (0, -1), (0, 1)]:
            nr, nc = r + dr, c + dc
            if 0 <= nr < rows and 0 <= nc < cols and not visited[nr, nc]:
                queue.append((nr, nc))

    # TRIM HALO / ANTI-ALIASING BORDER:
    # If a non-'.' border cell touches '.' AND its color is light/gray (W, T, S)
    # OR close to bg_rgb (distance < BG_HALO_THRESHOLD), convert to '.'
    for r in range(rows):
        for c in range(cols):
            if matrix[r][c] != '.':
                touches_bg = False
                for dr, dc in [(-1, 0), (1, 0), (0, -1), (0, 1), (-1, -1), (-1, 1), (1, -1), (1, 1)]:
                    nr, nc = r + dr, c + dc
                    if nr < 0 or nr >= rows or nc < 0 or nc >= cols or matrix[nr][nc] == '.':
                        touches_bg = True
                        break

                if touches_bg:
                    char = matrix[r][c]
                    rgb = rgb_matrix[r][c]

                    if bg_rgb is not None:
                        d_to_bg = color_distance(rgb, bg_rgb)
                        # Clear boundary anti-aliasing pixels
                        if char in ('W', 'T', 'S') or d_to_bg < BG_HALO_THRESHOLD:
                            matrix[r][c] = '.'
                    elif char in ('W', 'T', 'S'):
                        matrix[r][c] = '.'

    return matrix


# ============================================================
# NAMING & KOTLIN CODE GENERATION
# ============================================================

def filename_to_func_name(filename):
    """
    Converts filename (e.g. 'Djinn.png', 'black_mage.png', 'Borghen.png')
    to Kotlin function name (e.g. 'drawDjinn', 'drawBlackMage', 'drawBorghen').
    """
    name = Path(filename).stem
    clean = re.sub(r'[^a-zA-Z0-9]', ' ', name)
    camel = "".join(word.capitalize() for word in clean.split())
    return f"draw{camel}"


def is_hero_function(func_name):
    """
    Checks if function corresponds to a Hero (facing right) or Monster (facing left).
    """
    clean_name = func_name.replace("draw", "").lower()
    return clean_name in HERO_NAMES


def generate_kotlin_function(func_name, matrix):
    """
    Generates the full Kotlin function definition for a hero or monster sprite.
    """
    lines = []
    lines.append(f"fun DrawScope.{func_name}() {{")
    lines.append("    val matrix = arrayOf(")

    rows_count = len(matrix)
    for idx, row in enumerate(matrix):
        text = "".join(row)
        comma = "," if idx < rows_count - 1 else ""
        lines.append(f'        "{text}"{comma} // {idx}')
    lines.append("    )")
    lines.append("")
    lines.append("    val palette = mapOf(")

    used_chars = sorted(list(set(char for row in matrix for char in row if char in PALETTE)))
    for idx, char in enumerate(used_chars):
        r, g, b = PALETTE[char]
        argb = 0xFF000000 | (r << 16) | (g << 8) | b
        comma = "," if idx < len(used_chars) - 1 else ""
        lines.append(f"        '{char}' to Color(0x{argb:08X}){comma}")
    lines.append("    )")
    lines.append("")

    if is_hero_function(func_name):
        lines.append("    drawPixelMatrix(matrix, palette)")
    else:
        lines.append("    drawMonsterMatrix(matrix, palette)")
    lines.append("}")

    return "\n".join(lines)


# ============================================================
# REPLACE FUNCTION IN PixelCharacters.kt
# ============================================================

def replace_function_in_file(file_path, func_name, new_code):
    if not os.path.exists(file_path):
        print(f"Warning: File not found {file_path}")
        return False

    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()

    pattern = rf"fun DrawScope\.{func_name}\(\)\s*\{{[\s\S]*?\n\}}"

    if re.search(pattern, content):
        updated_content = re.sub(pattern, new_code, content)
        with open(file_path, "w", encoding="utf-8") as f:
            f.write(updated_content)
        print(f"[SUCCESS] Updated {func_name}() in {file_path}")
        return True
    else:
        print(f"[APPEND] Function {func_name}() not found in {file_path}. Appending function.")
        with open(file_path, "a", encoding="utf-8") as f:
            f.write("\n\n" + new_code + "\n")
        return True


# ============================================================
# PROCESS IMAGE FILE
# ============================================================

def process_image(image_path):
    image = cv2.imread(image_path, cv2.IMREAD_UNCHANGED)
    if image is None:
        print(f"Error: Could not open image {image_path}")
        return None

    height, width = image.shape[:2]
    func_name = filename_to_func_name(image_path)
    rows, cols = calculate_grid_size(image, max_dim=MAX_GRID_SIZE)

    print(f"Processing {image_path} ({width}x{height}) -> {func_name}() [{cols}x{rows} grid]")

    matrix = image_to_clean_matrix(image, rows, cols)
    code = generate_kotlin_function(func_name, matrix)

    return func_name, code


# ============================================================
# MAIN
# ============================================================

def main():
    png_files = []

    if os.path.exists(SPRITES_DIR):
        png_files = glob.glob(os.path.join(SPRITES_DIR, "*.png")) + glob.glob(os.path.join(SPRITES_DIR, "*.PNG"))

    if not png_files and os.path.exists(SINGLE_IMAGE_PATH):
        png_files = [SINGLE_IMAGE_PATH]

    if not png_files:
        print(f"No PNG files found in '{SPRITES_DIR}/' or '{SINGLE_IMAGE_PATH}'.")
        print("Place PNG files in 'sprites/' directory to process batch conversions.")
        return

    print(f"Found {len(png_files)} PNG file(s) to process.")

    for png_path in png_files:
        res = process_image(png_path)
        if res:
            func_name, code = res
            print("\n" + "=" * 60)
            print(f"GENERATED CODE FOR {func_name}:")
            print("=" * 60)
            print(code)
            print("=" * 60 + "\n")

            if os.path.exists(PIXEL_CHARACTERS_PATH):
                replace_function_in_file(PIXEL_CHARACTERS_PATH, func_name, code)


if __name__ == "__main__":
    main()
