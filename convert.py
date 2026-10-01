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
    # BLACK / GRAYS / WHITE
    # ============================================================
    'K': (20, 20, 20),        # black
    'D': (75, 75, 75),        # dark gray
    'S': (140, 140, 140),     # gray
    'T': (200, 200, 200),     # light gray
    'W': (255, 255, 255),     # white

    # ============================================================
    # BROWN
    # ============================================================
    'N': (80, 35, 20),        # dark brown
    'A': (130, 55, 25),       # brown
    'C': (175, 80, 30),       # light brown

    # ============================================================
    # ORANGE
    # ============================================================
    'O': (220, 105, 25),      # orange
    'J': (245, 145, 40),      # light orange

    # ============================================================
    # SKIN / PEACH
    # ============================================================
    'P': (190, 110, 65),      # dark skin
    'Q': (225, 155, 95),      # skin
    'E': (250, 195, 130),     # light skin
    'F': (255, 225, 175),     # skin highlight

    # ============================================================
    # RED
    # ============================================================
    'R': (130, 20, 20),       # dark red
    'Z': (210, 30, 25),       # red
    'X': (245, 55, 45),       # bright red

    # ============================================================
    # PINK / MAGENTA
    # ============================================================
    'r': (150, 25, 80),       # dark pink
    'p': (220, 45, 110),      # pink
    'q': (250, 100, 155),     # light pink

    # ============================================================
    # YELLOW
    # ============================================================
    '1': (180, 130, 15),      # dark yellow / gold
    '2': (230, 180, 20),      # yellow
    '3': (255, 220, 35),      # bright yellow
    '4': (255, 245, 110),     # pale yellow

    # ============================================================
    # GREEN
    # ============================================================
    'G': (15, 80, 25),        # dark green
    'g': (25, 145, 35),       # green
    'L': (80, 190, 40),       # light green
    'l': (150, 220, 55),      # bright green

    # ============================================================
    # CYAN / TEAL
    # ============================================================
    't': (10, 100, 105),      # dark cyan
    'c': (20, 165, 165),      # cyan
    'd': (90, 210, 205),      # light cyan

    # ============================================================
    # BLUE
    # ============================================================
    'B': (30, 55, 130),       # dark blue
    'U': (45, 90, 190),       # blue
    'V': (90, 145, 235),      # light blue

    # ============================================================
    # PURPLE / VIOLET
    # ============================================================
    'M': (70, 35, 140),       # dark purple
    'I': (115, 70, 175),      # purple
    'm': (165, 120, 215),     # light purple
    'i': (210, 180, 240),     # lavender

    # ============================================================
    # BACKGROUND
    # ============================================================
    '0': (45, 70, 145),
}


# ============================================================
# COLOR DISTANCE
# ============================================================

def color_distance(c1, c2):
    r1, g1, b1 = c1
    r2, g2, b2 = c2
    return math.sqrt((r1 - r2) ** 2 + (g1 - g2) ** 2 + (b1 - b2) ** 2)


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
