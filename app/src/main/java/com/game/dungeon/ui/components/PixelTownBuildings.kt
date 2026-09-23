package com.game.dungeon.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sin

private fun DrawScope.pxRect(x: Float, y: Float, w: Float, h: Float, color: Color) {
    val scale = size.width / 100f
    drawRect(color, Offset(x * scale, y * scale), Size(w * scale, h * scale))
}

/**
 * 1. ALCHEMIST / CRYSTAL SHOP (Matching the Left building in reference image)
 * - Steep purple gabled roof with stone chimney emitting smoke.
 * - Mounted glowing green Potion Flask emblem on the roof.
 * - Slate walls, potion shop display window, arched door, wooden barrel outside.
 */
fun DrawScope.drawDetailedCrystalShop(animTime: Float = 0f) {
    val W = size.width
    val s = W / 100f

    // === 1. Main Slate Structure ===
    pxRect(15f, 40f, 70f, 55f, Color(0xFF34495E)) // Dark slate wall
    pxRect(15f, 90f, 70f, 5f, Color(0xFF2C3E50)) // Stone foundation

    // Stone brick detail on walls
    pxRect(18f, 50f, 12f, 6f, Color(0xFF2C3E50))
    pxRect(68f, 65f, 12f, 6f, Color(0xFF2C3E50))
    pxRect(22f, 75f, 10f, 5f, Color(0xFF2C3E50))

    // === 2. Steep Purple Gabled Roof ===
    val roofPath = Path().apply {
        moveTo(5f * s, 42f * s)
        lineTo(50f * s, 8f * s)
        lineTo(95f * s, 42f * s)
        close()
    }
    drawPath(roofPath, Color(0xFF8E44AD)) // Main purple roof
    drawPath(roofPath, Color(0xFF5B2C6F), style = Stroke(3f * s))

    // Inner roof eaves trim
    val roofTrim = Path().apply {
        moveTo(10f * s, 42f * s)
        lineTo(50f * s, 12f * s)
        lineTo(90f * s, 42f * s)
    }
    drawPath(roofTrim, Color(0xFFA569BD), style = Stroke(2f * s))

    // Roof tile horizontal ridge lines
    for (i in 1..4) {
        pxRect(10f + i * 8f, 42f - i * 6f, 80f - i * 16f, 1.5f, Color(0xFF5B2C6F))
    }

    // === 3. Stone Chimney & Animated Smoke (Right side) ===
    pxRect(72f, 12f, 12f, 22f, Color(0xFF5D6D7E))
    pxRect(70f, 10f, 16f, 4f, Color(0xFF34495E)) // Chimney cap
    
    // Smoke puffs
    val smokePhase = (animTime * 0.002f) % 1f
    repeat(3) { i ->
        val progress = (smokePhase + i * 0.33f) % 1f
        val smokeY = 8f - progress * 25f
        val smokeX = 78f + sin(progress * 6.28f + i) * 4f
        val smokeRadius = (3f + progress * 6f) * s
        val smokeAlpha = (1f - progress) * 0.5f
        drawCircle(
            Color.White.copy(alpha = smokeAlpha),
            radius = smokeRadius,
            center = Offset(smokeX * s, smokeY * s)
        )
    }

    // === 4. Mounted Potion Flask Signage Emblem (On Roof) ===
    pxRect(38f, 16f, 24f, 20f, Color(0xFF4A235A)) // Emblem frame
    drawRect(Color(0xFF27AE60), Offset(38f * s, 16f * s), Size(24f * s, 20f * s), style = Stroke(2f * s))
    
    // Flask Icon (Green potion flask)
    val flaskPath = Path().apply {
        moveTo(48f * s, 20f * s)
        lineTo(52f * s, 20f * s)
        lineTo(52f * s, 24f * s)
        lineTo(56f * s, 32f * s)
        lineTo(44f * s, 32f * s)
        lineTo(48f * s, 24f * s)
        close()
    }
    drawPath(flaskPath, Color(0xFF2ECC71)) // Glowing potion
    drawPath(flaskPath, Color.White, style = Stroke(1.5f * s))

    // === 5. Potion Display Window (Left) ===
    pxRect(22f, 55f, 20f, 18f, Color(0xFF1F2A38)) // Window frame
    pxRect(24f, 57f, 16f, 14f, Color(0xFF1ABC9C).copy(alpha = 0.3f)) // Glass glow
    
    // Potion bottles on shelf
    drawCircle(Color(0xFF3498DB), radius = 2.5f * s, center = Offset(28f * s, 66f * s)) // Blue potion
    drawCircle(Color(0xFF2ECC71), radius = 2.5f * s, center = Offset(35f * s, 66f * s)) // Green potion

    // === 6. Arched Doorway (Center-Right) ===
    val doorPath = Path().apply {
        moveTo(50f * s, 95f * s)
        lineTo(50f * s, 68f * s)
        quadraticTo(60f * s, 58f * s, 70f * s, 68f * s)
        lineTo(70f * s, 95f * s)
        close()
    }
    drawPath(doorPath, Color(0xFF4A2311)) // Dark wood door
    drawPath(doorPath, Color(0xFFF39C12), style = Stroke(2f * s)) // Gold door trim
    drawCircle(Color(0xFFF1C40F), radius = 2.5f * s, center = Offset(66f * s, 78f * s)) // Glowing star knob

    // === 7. Wooden Barrel Outside (Right) ===
    pxRect(76f, 75f, 14f, 18f, Color(0xFF7E5109))
    pxRect(76f, 78f, 14f, 2f, Color(0xFF422C05)) // Iron band top
    pxRect(76f, 88f, 14f, 2f, Color(0xFF422C05)) // Iron band bottom
}

/**
 * 2. TRAINING HALL / BARRACKS (Matching the Center building in reference image)
 * - Tudor medieval style: light cream/beige plaster walls with dark timber cross-beams.
 * - Pitched brown tiled roof with wooden fascia.
 * - Arched dark entrance door, warm glowing framed windows.
 */
fun DrawScope.drawDetailedBarracks(animTime: Float = 0f) {
    val W = size.width
    val H = size.height
    val s = W / 100f

    // === 1. Main Cream Plaster Structure ===
    pxRect(15f, 35f, 70f, 60f, Color(0xFFF5E6CC)) // Light beige plaster wall
    pxRect(12f, 92f, 76f, 4f, Color(0xFF5D4037)) // Wooden foundation beam

    // === 2. Dark Timber Half-Framing (Tudor Style) ===
    val timberColor = Color(0xFF4A3423)
    // Outer border beams
    pxRect(15f, 35f, 5f, 60f, timberColor)
    pxRect(80f, 35f, 5f, 60f, timberColor)
    pxRect(15f, 35f, 70f, 4f, timberColor)
    pxRect(15f, 62f, 70f, 4f, timberColor)
    pxRect(15f, 90f, 70f, 4f, timberColor)
    
    // Vertical timber posts
    pxRect(35f, 35f, 4f, 60f, timberColor)
    pxRect(60f, 35f, 4f, 60f, timberColor)

    // Diagonal timber braces
    drawLine(timberColor, Offset(20f * s, 39f * s), Offset(35f * s, 62f * s), strokeWidth = 3f * s)
    drawLine(timberColor, Offset(80f * s, 39f * s), Offset(64f * s, 62f * s), strokeWidth = 3f * s)

    // === 3. Pitched Brown Tiled Roof ===
    val roofPath = Path().apply {
        moveTo(5f * s, 37f * s)
        lineTo(50f * s, 5f * s)
        lineTo(95f * s, 37f * s)
        close()
    }
    drawPath(roofPath, Color(0xFF8D5B41)) // Roof tiles
    drawPath(roofPath, Color(0xFF4A2311), style = Stroke(3f * s))

    // Wooden fascia trim along roof
    drawLine(Color(0xFF6E3C27), Offset(5f * s, 37f * s), Offset(50f * s, 5f * s), strokeWidth = 3.5f * s)
    drawLine(Color(0xFF6E3C27), Offset(95f * s, 37f * s), Offset(50f * s, 5f * s), strokeWidth = 3.5f * s)

    // === 4. Framed Windows with Warm Interior Glow ===
    val candleFlicker = 0.85f + sin(animTime * 0.005f) * 0.15f
    val glowColor = Color(0xFFF39C12).copy(alpha = candleFlicker)
    
    // Left Upper Window
    pxRect(22f, 42f, 10f, 15f, glowColor)
    drawRect(Color(0xFF361F10), Offset(22f * s, 42f * s), Size(10f * s, 15f * s), style = Stroke(1.5f * s))
    
    // Right Upper Window
    pxRect(68f, 42f, 10f, 15f, glowColor)
    drawRect(Color(0xFF361F10), Offset(68f * s, 42f * s), Size(10f * s, 15f * s), style = Stroke(1.5f * s))

    // === 5. Arched Dark Wood Entrance Door (Center) ===
    val doorPath = Path().apply {
        moveTo(42f * s, 94f * s)
        lineTo(42f * s, 72f * s)
        quadraticTo(50f * s, 64f * s, 58f * s, 72f * s)
        lineTo(58f * s, 94f * s)
        close()
    }
    drawPath(doorPath, Color(0xFF3B2312)) // Dark wood
    drawPath(doorPath, Color(0xFF8D5B41), style = Stroke(2f * s)) // Frame
    drawCircle(Color(0xFFF1C40F), radius = 2f * s, center = Offset(55f * s, 82f * s)) // Doorknob
}

/**
 * 3. ADVENTURER'S GUILD / RELICS (Matching the Right building in reference image)
 * - Grand two-story stone manor with roof peak golden shield emblem.
 * - Red banner with gold crest hanging beside doorway.
 * - Wooden BULLETIN board with pinned papers beside entrance.
 * - Arched upper windows.
 */
fun DrawScope.drawDetailedPortal(animTime: Float = 0f) {
    val W = size.width
    val H = size.height
    val s = W / 100f

    // === 1. Grand Stone Masonry Walls ===
    pxRect(10f, 30f, 80f, 65f, Color(0xFF6C7A89)) // Main grey stone
    pxRect(8f, 92f, 84f, 4f, Color(0xFF34495E)) // Foundation ledge

    // Stone blocks texture grid
    for (row in 0..4) {
        val y = 35f + row * 12f
        pxRect(10f, y, 80f, 1f, Color(0xFF4A5665))
        for (col in 0..6) {
            val x = 10f + col * 12f + (if (row % 2 == 0) 0f else 6f)
            pxRect(x, y, 1f, 12f, Color(0xFF4A5665))
        }
    }

    // Horizontal floor dividing trim
    pxRect(10f, 58f, 80f, 3f, Color(0xFF4A5665))

    // === 2. Roof with Golden Shield Emblem Peak ===
    val roofPath = Path().apply {
        moveTo(2f * s, 32f * s)
        lineTo(50f * s, 4f * s)
        lineTo(98f * s, 32f * s)
        close()
    }
    drawPath(roofPath, Color(0xFF4A3423)) // Roof tiles
    drawPath(roofPath, Color(0xFF2C1E14), style = Stroke(3f * s))

    // Golden Shield Emblem Peak (Roof top)
    val shieldPath = Path().apply {
        moveTo(50f * s, 8f * s)
        lineTo(57f * s, 14f * s)
        lineTo(57f * s, 22f * s)
        quadraticTo(50f * s, 28f * s, 50f * s, 28f * s)
        quadraticTo(50f * s, 28f * s, 43f * s, 22f * s)
        lineTo(43f * s, 14f * s)
        close()
    }
    drawPath(shieldPath, Color(0xFFF1C40F)) // Gold shield
    drawPath(shieldPath, Color(0xFFB7950B), style = Stroke(1.5f * s))
    pxRect(48f, 16f, 4f, 8f, Color(0xFF7D6608)) // Sword emblem inside shield

    // === 3. Arched Upper Windows (3 windows) ===
    listOf(22f, 50f, 78f).forEach { wx ->
        val winPath = Path().apply {
            moveTo((wx - 5f) * s, 50f * s)
            lineTo((wx - 5f) * s, 42f * s)
            quadraticTo(wx * s, 36f * s, (wx + 5f) * s, 42f * s)
            lineTo((wx + 5f) * s, 50f * s)
            close()
        }
        drawPath(winPath, Color(0xFF2C3E50)) // Dark interior
        drawPath(winPath, Color(0xFFBDC3C7), style = Stroke(1.5f * s)) // Stone arch trim
    }

    // === 4. Red Banner with Crest (Left side) ===
    val bannerPulse = 0.85f + sin(animTime * 0.003f) * 0.15f
    pxRect(20f, 62f, 12f, 26f, Color(0xFFC0392B).copy(alpha = bannerPulse)) // Red banner
    pxRect(20f, 62f, 12f, 2f, Color(0xFFF1C40F)) // Top gold bar
    pxRect(20f, 88f, 12f, 2f, Color(0xFFF1C40F)) // Bottom gold fringe
    drawCircle(Color(0xFFF1C40F), radius = 2.5f * s, center = Offset(26f * s, 74f * s)) // Crest

    // === 5. Arched Entrance Doorway (Center) ===
    val doorPath = Path().apply {
        moveTo(42f * s, 95f * s)
        lineTo(42f * s, 70f * s)
        quadraticTo(50f * s, 60f * s, 58f * s, 70f * s)
        lineTo(58f * s, 95f * s)
        close()
    }
    drawPath(doorPath, Color(0xFF2C1E14)) // Dark entrance
    drawPath(doorPath, Color(0xFF8D5B41), style = Stroke(2.5f * s))

    // === 6. BULLETIN Board (Right side next to door) ===
    pxRect(68f, 65f, 24f, 18f, Color(0xFF7E5109)) // Wooden board background
    drawRect(Color(0xFF422C05), Offset(68f * s, 65f * s), Size(24f * s, 18f * s), style = Stroke(1.5f * s))
    pxRect(72f, 83f, 3f, 10f, Color(0xFF422C05)) // Support post left
    pxRect(85f, 83f, 3f, 10f, Color(0xFF422C05)) // Support post right

    // Header "BULLETIN" text plate
    pxRect(70f, 63f, 20f, 4f, Color(0xFFD35400))

    // Pinned quest papers (white parchment squares)
    pxRect(71f, 69f, 5f, 6f, Color(0xFFF5EEF8))
    pxRect(78f, 69f, 5f, 6f, Color(0xFFF5EEF8))
    pxRect(85f, 69f, 5f, 6f, Color(0xFFF5EEF8))
    pxRect(74f, 76f, 6f, 5f, Color(0xFFF5EEF8))
    pxRect(82f, 76f, 6f, 5f, Color(0xFFF5EEF8))
}

/**
 * 4. THE INN (Cozy 2-story Tavern & Lodge)
 * - Stone block base with warm timber upper floor.
 * - Steep dark red shingled roof with chimney.
 * - Hanging wooden swinging sign ("INN 🍺").
 * - Lattice windows with warm candlelight.
 */
fun DrawScope.drawDetailedInn(animTime: Float = 0f) {
    val W = size.width
    val H = size.height
    val s = W / 100f

    // === 1. Stone Foundation Base ===
    pxRect(10f, 60f, 80f, 35f, Color(0xFF5D6D7E))
    for (i in 0..5) {
        pxRect(15f + i * 14f, 65f + (i % 2) * 10f, 8f, 5f, Color(0xFF34495E))
    }

    // === 2. Upper Timber Floor ===
    pxRect(15f, 28f, 70f, 34f, Color(0xFF6D4C41)) // Warm brown wood
    // Horizontal plank lines
    for (y in 34..58 step 6) {
        pxRect(15f, y.toFloat(), 70f, 1f, Color(0xFF3E2723))
    }

    // === 3. Steep Dark Red Shingled Roof ===
    val roofPath = Path().apply {
        moveTo(5f * s, 30f * s)
        lineTo(50f * s, 2f * s)
        lineTo(95f * s, 30f * s)
        close()
    }
    drawPath(roofPath, Color(0xFF900C3F)) // Dark red shingles
    drawPath(roofPath, Color(0xFF581845), style = Stroke(3f * s))

    // === 4. Chimney & Smoke (Left side) ===
    pxRect(20f, 8f, 10f, 20f, Color(0xFF5D6D7E))
    pxRect(18f, 6f, 14f, 3f, Color(0xFF34495E))
    
    val smokePhase = ((animTime + 500f) * 0.002f) % 1f
    repeat(3) { i ->
        val progress = (smokePhase + i * 0.33f) % 1f
        val smokeY = 5f - progress * 22f
        val smokeX = 25f + sin(progress * 6.28f + i) * 3f
        val smokeRadius = (2.5f + progress * 5f) * s
        val smokeAlpha = (1f - progress) * 0.5f
        drawCircle(
            Color.White.copy(alpha = smokeAlpha),
            radius = smokeRadius,
            center = Offset(smokeX * s, smokeY * s)
        )
    }

    // === 5. Swinging Wooden Sign ("INN 🍺") ===
    pxRect(75f, 32f, 18f, 14f, Color(0xFF5D4037)) // Signboard
    drawRect(Color(0xFF3E2723), Offset(75f * s, 32f * s), Size(18f * s, 14f * s), style = Stroke(1.5f * s))
    drawLine(Color(0xFF2C3E50), Offset(72f * s, 32f * s), Offset(75f * s, 32f * s), strokeWidth = 2f * s) // Bracket
    drawCircle(Color(0xFFF1C40F), radius = 3f * s, center = Offset(84f * s, 39f * s)) // Mug glow symbol

    // === 6. Lattice Windows with Warm Glow ===
    val candleFlicker = 0.85f + sin(animTime * 0.005f + 1f) * 0.15f
    val glow = Color(0xFFF4D03F).copy(alpha = candleFlicker)
    
    listOf(25f, 60f).forEach { wx ->
        pxRect(wx, 36f, 14f, 14f, glow)
        drawRect(Color(0xFF3E2723), Offset(wx * s, 36f * s), Size(14f * s, 14f * s), style = Stroke(1.5f * s))
        // Cross panes
        drawLine(Color(0xFF3E2723), Offset((wx + 7f) * s, 36f * s), Offset((wx + 7f) * s, 50f * s), strokeWidth = 1.5f * s)
        drawLine(Color(0xFF3E2723), Offset(wx * s, 43f * s), Offset((wx + 14f) * s, 43f * s), strokeWidth = 1.5f * s)
    }

    // === 7. Arched Entrance Doorway ===
    val doorPath = Path().apply {
        moveTo(42f * s, 95f * s)
        lineTo(42f * s, 68f * s)
        quadraticTo(52f * s, 58f * s, 62f * s, 68f * s)
        lineTo(62f * s, 95f * s)
        close()
    }
    drawPath(doorPath, Color(0xFF3E2723)) // Dark wood door
    drawPath(doorPath, Color(0xFFF39C12), style = Stroke(2f * s))
    drawCircle(Color(0xFFF1C40F), radius = 2.5f * s, center = Offset(58f * s, 78f * s))
}
