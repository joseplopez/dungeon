package com.game.dungeon.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

/**
 * Normalizes all drawing to a standard coordinate system where Height = 100 units.
 * Width dynamically stretches up to W based on the aspect ratio passed to the Canvas.
 * Ground line is universally at Y = 95.
 */
private fun DrawScope.pxRectS(x: Float, y: Float, w: Float, h: Float, color: Color, scale: Float) {
    drawRect(color, Offset(x * scale, y * scale), Size(w * scale, h * scale))
}

/**
 * 1. CRYSTAL BAZAAR (Wide, colorful market stall)
 */
fun DrawScope.drawDetailedCrystalShop(animTime: Float = 0f) {
    val scale = size.height / 100f
    val W = size.width / scale // Total width in normalized units (approx 150)
    val CX = W / 2f // Center X

    // Ground Base
    pxRectS(10f, 95f, W - 20f, 5f, Color(0xFF2C3E50), scale)
    for (i in 0..8) {
        pxRectS(15f + i * 15f, 96f, 8f, 3f, Color(0xFF1B2631), scale)
    }

    // Wooden Crates (Left Side)
    pxRectS(12f, 85f, 14f, 10f, Color(0xFF7E5109), scale)
    drawRect(Color(0xFF422C05), Offset(12f * scale, 85f * scale), Size(14f * scale, 10f * scale), style = Stroke(1.5f * scale))
    pxRectS(14f, 75f, 10f, 10f, Color(0xFF8D5B41), scale)
    drawRect(Color(0xFF422C05), Offset(14f * scale, 75f * scale), Size(10f * scale, 10f * scale), style = Stroke(1.5f * scale))

    // Background Shelving Unit & Potion Bottles
    pxRectS(CX - 35f, 30f, 70f, 35f, Color(0xFF2A1C15), scale)
    pxRectS(CX - 33f, 45f, 66f, 3f, Color(0xFF422C05), scale)
    pxRectS(CX - 33f, 55f, 66f, 3f, Color(0xFF422C05), scale)

    drawCircle(Color(0xFF2ECC71), radius = 2.5f * scale, center = Offset((CX - 25f) * scale, 40f * scale))
    drawCircle(Color(0xFFF1C40F), radius = 2.5f * scale, center = Offset((CX - 15f) * scale, 40f * scale))
    drawCircle(Color(0xFFE74C3C), radius = 2.5f * scale, center = Offset((CX + 15f) * scale, 40f * scale))
    drawCircle(Color(0xFF3498DB), radius = 2.5f * scale, center = Offset((CX + 25f) * scale, 40f * scale))
    drawCircle(Color(0xFFA569BD), radius = 3f * scale, center = Offset((CX + 20f) * scale, 50f * scale))

    // Hooded Merchant NPC
    val hoodPath = Path().apply {
        moveTo((CX - 10f) * scale, 65f * scale)
        lineTo((CX - 8f) * scale, 40f * scale)
        quadraticTo(CX * scale, 30f * scale, (CX + 8f) * scale, 40f * scale)
        lineTo((CX + 10f) * scale, 65f * scale)
        close()
    }
    drawPath(hoodPath, Color(0xFF78281F))
    drawPath(hoodPath, Color(0xFF4A120B), style = Stroke(2f * scale))

    val facePath = Path().apply {
        moveTo((CX - 5f) * scale, 43f * scale)
        quadraticTo(CX * scale, 38f * scale, (CX + 5f) * scale, 43f * scale)
        quadraticTo((CX + 5f) * scale, 52f * scale, CX * scale, 55f * scale)
        quadraticTo((CX - 5f) * scale, 52f * scale, (CX - 5f) * scale, 43f * scale)
        close()
    }
    drawPath(facePath, Color(0xFF1B120C))
    val eyeGlow = 0.85f + sin(animTime * 0.005f) * 0.15f
    drawCircle(Color(0xFFF1C40F).copy(alpha = eyeGlow), radius = 1.5f * scale, center = Offset((CX - 2f) * scale, 46f * scale))
    drawCircle(Color(0xFFF1C40F).copy(alpha = eyeGlow), radius = 1.5f * scale, center = Offset((CX + 2f) * scale, 46f * scale))

    // Wooden Market Counter Base
    pxRectS(CX - 40f, 65f, 80f, 30f, Color(0xFF5D4037), scale)
    pxRectS(CX - 42f, 61f, 84f, 4f, Color(0xFF7E5109), scale)
    for (px in (CX.toInt() - 30)..(CX.toInt() + 30) step 15) {
        pxRectS(px.toFloat(), 65f, 2f, 30f, Color(0xFF3E2723), scale)
    }

    // Wooden Corner Posts
    pxRectS(CX - 44f, 15f, 6f, 46f, Color(0xFF5D4037), scale)
    pxRectS(CX + 38f, 15f, 6f, 46f, Color(0xFF5D4037), scale)

    // Awnings
    pxRectS(CX - 46f, 12f, 92f, 5f, Color(0xFF422C05), scale)
    val purpleDark = Color(0xFF5B2C6F)
    val purpleLight = Color(0xFF8E44AD)
    for (i in 0..7) {
        val ax = (CX - 44f) + i * 11f
        val acolor = if (i % 2 == 0) purpleLight else purpleDark
        val stripePath = Path().apply {
            moveTo(ax * scale, 17f * scale)
            lineTo((ax + 11f) * scale, 17f * scale)
            lineTo((ax + 9f) * scale, 35f * scale)
            lineTo((ax + 5.5f) * scale, 39f * scale)
            lineTo((ax + 2f) * scale, 35f * scale)
            close()
        }
        drawPath(stripePath, acolor)
        drawPath(stripePath, Color(0xFF2C1A0E), style = Stroke(1.5f * scale))
    }

    // Glowing Display Crystals
    listOf(
        Triple(CX - 25f, Color(0xFFF1C40F), "AMBER"),
        Triple(CX - 8f, Color(0xFF00FF87), "CYAN"),
        Triple(CX + 10f, Color(0xFFE74C3C), "PINK"),
        Triple(CX + 28f, Color(0xFFA569BD), "AMETHYST")
    ).forEach { (cx, cColor, type) ->
        pxRectS(cx - 4.5f, 59f, 9f, 2.5f, Color(0xFF422C05), scale)
        val crystalPulse = 0.35f + sin(animTime * 0.004f + cx) * 0.15f
        drawCircle(cColor.copy(alpha = crystalPulse), radius = 7f * scale, center = Offset(cx * scale, 52f * scale))

        val gemPath = Path().apply {
            moveTo(cx * scale, 42f * scale)
            lineTo((cx + 4.5f) * scale, 50f * scale)
            lineTo(cx * scale, 59f * scale)
            lineTo((cx - 4.5f) * scale, 50f * scale)
            close()
        }
        drawPath(gemPath, cColor)
        drawPath(gemPath, Color.White.copy(alpha = 0.8f), style = Stroke(1.5f * scale))
    }

    // Floating Magic Particles
    repeat(6) { i ->
        val sparkPhase = ((animTime + i * 220f) * 0.0018f) % 1f
        val floatY = 85f - sparkPhase * 60f
        val floatX = 12f + i * 22f + sin(sparkPhase * 6.28f + i) * 4f
        val sparkColor = Color(0xFF00FF87).copy(alpha = (1f - sparkPhase) * 0.85f)
        drawCircle(sparkColor, radius = 1.5f * scale, center = Offset(floatX * scale, floatY * scale))
    }
}

/**
 * 2. BARRACKS / TRAINING CAMP (Asymmetrical, half palisade, half stone keep)
 */
/**
 * 2. BARRACKS / TRAINING CAMP (Highly Detailed Fortified Keep & Yard)
 * - Textured palisade wall with wood grain and rope bindings.
 * - Classic straw-stuffed scarecrow training dummy.
 * - Heavy stone keep with iron-banded double doors.
 * - Animated flaming torches and hanging crimson/gold banners.
 * - Legendary glowing relic sword embedded in a cracked stone anvil.
 * - Realistic weapon rack holding spears and a shield.
 */
fun DrawScope.drawDetailedBarracks(animTime: Float = 0f) {
    val scale = size.height / 100f
    val W = size.width / scale

    // === LEFT SIDE: PALISADE WALL ===
    // Textured wooden logs
    for (i in 0..5) {
        val px = 5f + i * 7f
        val logHeight = if (i % 2 == 0) 50f else 55f

        val logPath = Path().apply {
            moveTo(px * scale, 95f * scale)
            lineTo(px * scale, logHeight * scale)
            lineTo((px + 3.5f) * scale, (logHeight - 8f) * scale) // Pointy top
            lineTo((px + 7f) * scale, logHeight * scale)
            lineTo((px + 7f) * scale, 95f * scale)
            close()
        }
        drawPath(logPath, Color(0xFF4A3423))
        drawPath(logPath, Color(0xFF2C1A0E), style = Stroke(1.5f * scale))

        // Wood grain lines
        drawLine(Color(0xFF3E2723), Offset((px + 2f) * scale, (logHeight + 5f) * scale), Offset((px + 2f) * scale, 90f * scale), strokeWidth = 1f * scale)
        drawLine(Color(0xFF3E2723), Offset((px + 5f) * scale, logHeight * scale), Offset((px + 5f) * scale, 92f * scale), strokeWidth = 1f * scale)
    }

    // Palisade horizontal crossbeam & rope ties
    pxRectS(3f, 70f, 44f, 4f, Color(0xFF3E2723), scale)
    for (i in 0..5) {
        val px = 5f + i * 7f
        // Rope bindings
        drawLine(Color(0xFFD4AC0D), Offset((px + 1f) * scale, 70f * scale), Offset((px + 6f) * scale, 74f * scale), strokeWidth = 1.2f * scale)
        drawLine(Color(0xFFD4AC0D), Offset((px + 6f) * scale, 70f * scale), Offset((px + 1f) * scale, 74f * scale), strokeWidth = 1.2f * scale)
    }

    // === SCARECROW TRAINING DUMMY ===
    val dummyX = 22f
    pxRectS(dummyX + 3f, 75f, 4f, 20f, Color(0xFF5D4037), scale) // Center post
    pxRectS(dummyX - 4f, 78f, 18f, 3f, Color(0xFF5D4037), scale) // Arm post

    // Straw body and head
    pxRectS(dummyX, 76f, 10f, 12f, Color(0xFFE5C158), scale) // Torso
    drawRect(Color(0xFFB7950B), Offset(dummyX * scale, 76f * scale), Size(10f * scale, 12f * scale), style = Stroke(1f * scale))
    drawCircle(Color(0xFFE5C158), radius = 4.5f * scale, center = Offset((dummyX + 5f) * scale, 71f * scale)) // Head

    // Red target crosshair on chest
    drawLine(Color(0xFFC0392B), Offset((dummyX + 5f) * scale, 78f * scale), Offset((dummyX + 5f) * scale, 86f * scale), strokeWidth = 1.5f * scale)
    drawLine(Color(0xFFC0392B), Offset((dummyX + 2f) * scale, 82f * scale), Offset((dummyX + 8f) * scale, 82f * scale), strokeWidth = 1.5f * scale)

    // Scarecrow Hat & Straw poking out
    val hatPath = Path().apply {
        moveTo((dummyX - 2f) * scale, 68f * scale)
        lineTo((dummyX + 12f) * scale, 68f * scale)
        lineTo((dummyX + 8f) * scale, 63f * scale)
        lineTo((dummyX + 2f) * scale, 63f * scale)
        close()
    }
    drawPath(hatPath, Color(0xFF8D6E63))
    drawLine(Color(0xFFD4AC0D), Offset((dummyX - 4f) * scale, 80f * scale), Offset((dummyX - 1f) * scale, 79f * scale), strokeWidth = 1f * scale) // Left straw arm
    drawLine(Color(0xFFD4AC0D), Offset((dummyX + 14f) * scale, 80f * scale), Offset((dummyX + 11f) * scale, 79f * scale), strokeWidth = 1f * scale) // Right straw arm

    // === RIGHT SIDE: STONE KEEP ===
    val keepStartX = 55f
    val keepEndX = W - 5f
    val keepCX = (keepStartX + keepEndX) / 2f

    // Heavy Slate Keep Body
    pxRectS(keepStartX, 35f, keepEndX - keepStartX, 60f, Color(0xFF2C3E50), scale)
    // Stone brick horizontal seams
    for (y in 45..85 step 10) {
        pxRectS(keepStartX, y.toFloat(), keepEndX - keepStartX, 1.5f, Color(0xFF1B2631), scale)
    }

    // Keep Roof
    val roofPath = Path().apply {
        moveTo((keepStartX - 5f) * scale, 35f * scale)
        lineTo(keepCX * scale, 12f * scale)
        lineTo((keepEndX + 5f) * scale, 35f * scale)
        close()
    }
    drawPath(roofPath, Color(0xFF212F3D))
    drawPath(roofPath, Color(0xFF17202A), style = Stroke(3f * scale))
    pxRectS(keepStartX - 2f, 35f, (keepEndX - keepStartX) + 4f, 3f, Color(0xFF17202A), scale) // Roof trim

    // Center Iron-Banded Double Door
    val doorPath = Path().apply {
        moveTo((keepCX - 12f) * scale, 95f * scale)
        lineTo((keepCX - 12f) * scale, 70f * scale)
        quadraticTo(keepCX * scale, 58f * scale, (keepCX + 12f) * scale, 70f * scale)
        lineTo((keepCX + 12f) * scale, 95f * scale)
        close()
    }
    drawPath(doorPath, Color(0xFF3E2723)) // Wood
    drawPath(doorPath, Color(0xFF1B2631), style = Stroke(2.5f * scale)) // Iron frame
    drawLine(Color(0xFF1B2631), Offset(keepCX * scale, 65f * scale), Offset(keepCX * scale, 95f * scale), strokeWidth = 1.5f * scale) // Split
    pxRectS(keepCX - 10f, 75f, 20f, 2f, Color(0xFF1B2631), scale) // Iron band
    pxRectS(keepCX - 10f, 86f, 20f, 2f, Color(0xFF1B2631), scale) // Iron band
    drawCircle(Color(0xFFBDC3C7), radius = 1.5f * scale, center = Offset((keepCX - 4f) * scale, 81f * scale), style = Stroke(1.5f * scale)) // Left ring
    drawCircle(Color(0xFFBDC3C7), radius = 1.5f * scale, center = Offset((keepCX + 4f) * scale, 81f * scale), style = Stroke(1.5f * scale)) // Right ring

    // Flanking Torches
    listOf(keepCX - 18f, keepCX + 18f).forEach { tx ->
        pxRectS(tx - 1.5f, 68f, 3f, 8f, Color(0xFF1B2631), scale) // Sconce
        val firePulse = sin(animTime * 0.01f + tx) * 0.8f
        val flameY = 64f + firePulse * 0.5f
        drawCircle(Color(0xFFE67E22).copy(alpha = 0.4f), radius = 5f * scale, center = Offset(tx * scale, flameY * scale)) // Aura
        drawCircle(Color(0xFFD35400), radius = 3f * scale, center = Offset(tx * scale, flameY * scale)) // Core
        drawCircle(Color(0xFFF1C40F), radius = 1.5f * scale, center = Offset(tx * scale, (flameY - 1f) * scale)) // Hot center
    }

    // Crimson Banners
    listOf(keepStartX + 6f, keepEndX - 14f).forEach { bx ->
        pxRectS(bx, 42f, 8f, 25f, Color(0xFF78281F), scale) // Cloth
        pxRectS(bx, 42f, 8f, 2f, Color(0xFFF1C40F), scale) // Top gold
        pxRectS(bx, 65f, 8f, 2f, Color(0xFFF1C40F), scale) // Bottom gold
        drawLine(Color(0xFFF1C40F), Offset((bx + 2f) * scale, 50f * scale), Offset((bx + 6f) * scale, 58f * scale), strokeWidth = 1.2f * scale)
        drawLine(Color(0xFFF1C40F), Offset((bx + 6f) * scale, 50f * scale), Offset((bx + 2f) * scale, 58f * scale), strokeWidth = 1.2f * scale)
    }

    // === LEGENDARY RELIC SWORD IN STONE (Left yard) ===
    val swordX = 46f
    // Cracked Stone Anvil Base
    val stonePath = Path().apply {
        moveTo((swordX - 7f) * scale, 95f * scale)
        lineTo((swordX - 4f) * scale, 86f * scale)
        lineTo((swordX + 4f) * scale, 86f * scale)
        lineTo((swordX + 7f) * scale, 95f * scale)
        close()
    }
    drawPath(stonePath, Color(0xFF5D6D7E))
    drawLine(Color(0xFF2C3E50), Offset(swordX * scale, 86f * scale), Offset((swordX - 2f) * scale, 92f * scale), strokeWidth = 1.5f * scale) // Crack

    // Sword
    val glow = 0.6f + sin(animTime * 0.005f) * 0.4f
    drawCircle(Color(0xFF60A5FA).copy(alpha = glow * 0.5f), radius = 10f * scale, center = Offset(swordX * scale, 75f * scale)) // Magic Aura
    drawLine(Color(0xFFE5E7EB), Offset(swordX * scale, 65f * scale), Offset(swordX * scale, 88f * scale), strokeWidth = 2.5f * scale) // Blade
    pxRectS(swordX - 4f, 72f, 8f, 2f, Color(0xFFF1C40F), scale) // Gold Crossguard
    pxRectS(swordX - 1f, 61f, 2f, 11f, Color(0xFF8D6E63), scale) // Grip
    drawCircle(Color(0xFFF1C40F), radius = 1.5f * scale, center = Offset(swordX * scale, 60f * scale)) // Pommel

    // === WEAPON RACK (Far right edge) ===
    val rackX = W - 22f
    pxRectS(rackX, 75f, 3f, 20f, Color(0xFF4A3423), scale) // Left stand
    pxRectS(rackX + 12f, 75f, 3f, 20f, Color(0xFF4A3423), scale) // Right stand
    pxRectS(rackX - 2f, 82f, 19f, 2f, Color(0xFF2C1A0E), scale) // Crossbar

    // Halberd / Spears
    drawLine(Color(0xFF5D4037), Offset((rackX + 4f) * scale, 65f * scale), Offset((rackX + 4f) * scale, 95f * scale), strokeWidth = 1.5f * scale)
    val spearTip = Path().apply {
        moveTo((rackX + 4f) * scale, 60f * scale)
        lineTo((rackX + 2f) * scale, 65f * scale)
        lineTo((rackX + 6f) * scale, 65f * scale)
        close()
    }
    drawPath(spearTip, Color(0xFFBDC3C7))

    // Wooden Shield leaning on rack
    drawCircle(Color(0xFF7E5109), radius = 5f * scale, center = Offset((rackX + 10f) * scale, 88f * scale)) // Shield
    drawCircle(Color(0xFF2C3E50), radius = 5f * scale, center = Offset((rackX + 10f) * scale, 88f * scale), style = Stroke(1.5f * scale)) // Rim
    drawCircle(Color(0xFFBDC3C7), radius = 1.5f * scale, center = Offset((rackX + 10f) * scale, 88f * scale)) // Boss

    // Ground line
    pxRectS(2f, 95f, W - 4f, 5f, Color(0xFF1B2631), scale)
}


/**
 * 6. BLACKSMITH / MYTHIC FORGE (Detailed Open-Air Forge)
 * - Supported by heavy wooden pillars holding up an iron pitched roof.
 * - Massive stone/brick hearth in the back with glowing coals and fire.
 * - Large wooden & leather bellows on the side of the hearth.
 * - Glowing blue runic engravings on the hearth stone.
 * - Steel anvil on a stump, water cooling barrel, and hanging sign.
 * - Chimney passing through the roof emitting sparks.
 */
fun DrawScope.drawDetailedBlacksmith(animTime: Float = 0f) {
    val scale = size.height / 100f
    val W = size.width / scale

    // === CHIMNEY & SPARKS ===
    val chimX = W - 35f
    pxRectS(chimX, 10f, 16f, 35f, Color(0xFF78281F), scale) // Brick chimney
    pxRectS(chimX - 2f, 8f, 20f, 4f, Color(0xFF4A120B), scale) // Cap

    // Rising Sparks
    repeat(5) { i ->
        val sparkPhase = ((animTime + i * 150f) * 0.002f) % 1f
        val sparkY = 8f - sparkPhase * 25f
        val sparkX = chimX + 8f + sin(sparkPhase * 6.28f + i) * 4f
        val sparkAlpha = (1f - sparkPhase) * 0.9f
        drawCircle(Color(0xFFFF9800).copy(alpha = sparkAlpha), radius = 1.5f * scale, center = Offset(sparkX * scale, sparkY * scale))
    }

    // === BACK WALL HEARTH (Stone & Brick) ===
    val hearthStartX = 25f
    val hearthEndX = W - 20f
    pxRectS(hearthStartX, 45f, hearthEndX - hearthStartX, 50f, Color(0xFF37474F), scale) // Stone back

    // Fire Opening
    val archPath = Path().apply {
        moveTo((hearthStartX + 12f) * scale, 95f * scale)
        lineTo((hearthStartX + 12f) * scale, 65f * scale)
        quadraticTo(((hearthStartX + hearthEndX) / 2f + 5f) * scale, 50f * scale, (hearthEndX - 12f) * scale, 65f * scale)
        lineTo((hearthEndX - 12f) * scale, 95f * scale)
        close()
    }
    drawPath(archPath, Color(0xFF1B0000))
    drawPath(archPath, Color(0xFF263238), style = Stroke(3f * scale)) // Arch trim

    // Glowing Coals and Fire
    val fireFlicker = 0.8f + sin(animTime * 0.015f) * 0.2f
    val fireCX = (hearthStartX + hearthEndX) / 2f

    drawCircle(Color(0xFFE65100).copy(alpha = 0.5f * fireFlicker), radius = 14f * scale, center = Offset(fireCX * scale, 82f * scale)) // Glow
    drawCircle(Color(0xFFFF5722), radius = 9f * scale, center = Offset(fireCX * scale, 85f * scale)) // Core flame
    drawCircle(Color(0xFFFFEB3B).copy(alpha = fireFlicker), radius = 4f * scale, center = Offset(fireCX * scale, 86f * scale)) // Hot center

    // Coals
    listOf(fireCX - 8f, fireCX - 3f, fireCX + 4f, fireCX + 9f).forEach { cx ->
        drawCircle(Color(0xFFD35400), radius = 2f * scale, center = Offset(cx * scale, 93f * scale))
        drawCircle(Color(0xFFFFEB3B).copy(alpha = fireFlicker), radius = 1f * scale, center = Offset((cx + 0.5f) * scale, 92f * scale))
    }

    // Glowing Cyan Runes on the Hearth Stone
    val runeGlow = 0.6f + sin(animTime * 0.006f) * 0.4f
    val runeColor = Color(0xFF00FF87).copy(alpha = runeGlow)
    // Rune 1 (Left)
    drawLine(runeColor, Offset((hearthStartX + 6f) * scale, 60f * scale), Offset((hearthStartX + 6f) * scale, 68f * scale), strokeWidth = 1.5f * scale)
    drawLine(runeColor, Offset((hearthStartX + 6f) * scale, 64f * scale), Offset((hearthStartX + 9f) * scale, 62f * scale), strokeWidth = 1.5f * scale)
    // Rune 2 (Right)
    drawLine(runeColor, Offset((hearthEndX - 6f) * scale, 60f * scale), Offset((hearthEndX - 6f) * scale, 68f * scale), strokeWidth = 1.5f * scale)
    drawLine(runeColor, Offset((hearthEndX - 9f) * scale, 64f * scale), Offset((hearthEndX - 6f) * scale, 66f * scale), strokeWidth = 1.5f * scale)

    // === BLACKSMITH BELLOWS (Left side of hearth) ===
    val bellowsX = hearthStartX - 10f
    val pump = sin(animTime * 0.005f) * 2f // Bellows pumping animation

    val bellowsPath = Path().apply {
        moveTo(bellowsX * scale, (80f - pump) * scale) // Top left
        lineTo((bellowsX + 12f) * scale, 85f * scale)   // Nozzle
        lineTo(bellowsX * scale, (90f + pump) * scale) // Bottom left
        close()
    }
    drawPath(bellowsPath, Color(0xFF8B0000)) // Leather
    drawPath(bellowsPath, Color(0xFF4A3423), style = Stroke(2f * scale)) // Wood trim
    pxRectS(bellowsX + 12f, 83f, 4f, 4f, Color(0xFF7F8C8D), scale) // Iron nozzle

    // === STRUCTURE: PILLARS & ROOF ===
    val roofPillars = listOf(12f, W - 18f)
    roofPillars.forEach { px ->
        pxRectS(px, 35f, 6f, 60f, Color(0xFF4A3423), scale) // Thick wood pillar
        pxRectS(px - 1f, 88f, 8f, 7f, Color(0xFF2C1A0E), scale) // Pillar base
        pxRectS(px - 1f, 35f, 8f, 4f, Color(0xFF2C1A0E), scale) // Pillar top
    }

    // Iron Pitched Roof
    val roofPath = Path().apply {
        moveTo(2f * scale, 35f * scale)
        lineTo((W / 2f) * scale, 10f * scale)
        lineTo((W - 2f) * scale, 35f * scale)
        close()
    }
    drawPath(roofPath, Color(0xFF263238)) // Iron fill
    drawPath(roofPath, Color(0xFF102027), style = Stroke(3f * scale))
    pxRectS(2f, 35f, W - 4f, 3f, Color(0xFF102027), scale) // Roof lower trim

    // === FOREGROUND DETAILS ===
    // Water Cooling Barrel (Right side)
    val barrelX = W - 32f
    pxRectS(barrelX, 78f, 14f, 17f, Color(0xFF5D4037), scale) // Barrel body
    drawRect(Color(0xFF3E2723), Offset(barrelX * scale, 78f * scale), Size(14f * scale, 17f * scale), style = Stroke(1.5f * scale))
    pxRectS(barrelX - 1f, 82f, 16f, 2f, Color(0xFFBDC3C7), scale) // Metal band
    pxRectS(barrelX - 1f, 90f, 16f, 2f, Color(0xFFBDC3C7), scale) // Metal band
    pxRectS(barrelX + 1f, 78f, 12f, 2f, Color(0xFF0288D1), scale) // Water top

    // Steam wisps from barrel
    repeat(2) { i ->
        val steamPhase = ((animTime + i * 400f) * 0.001f) % 1f
        val steamY = 78f - steamPhase * 15f
        val steamX = barrelX + 7f + sin(steamPhase * 6.28f + i) * 3f
        val steamAlpha = (1f - steamPhase) * 0.5f
        drawCircle(Color.White.copy(alpha = steamAlpha), radius = 2.5f * scale, center = Offset(steamX * scale, steamY * scale))
    }

    // Steel Anvil on Wooden Stump (Center/Left Foreground)
    val anvilX = 42f
    pxRectS(anvilX, 85f, 12f, 10f, Color(0xFF4A3423), scale) // Stump
    val anvilBody = Path().apply {
        moveTo((anvilX - 2f) * scale, 85f * scale)  // Base left
        lineTo((anvilX + 14f) * scale, 85f * scale) // Base right
        lineTo((anvilX + 10f) * scale, 75f * scale) // Waist right
        lineTo((anvilX + 18f) * scale, 72f * scale) // Horn right tip
        lineTo((anvilX - 4f) * scale, 72f * scale)  // Face top left
        lineTo((anvilX + 2f) * scale, 75f * scale)  // Waist left
        close()
    }
    drawPath(anvilBody, Color(0xFF5D6D7E))
    drawPath(anvilBody, Color(0xFFECEFF1), style = Stroke(1f * scale)) // Highlight edge

    // Hanging Signboard (Hanging from left pillar)
    pxRectS(2f, 45f, 16f, 12f, Color(0xFF37474F), scale) // Board
    drawRect(Color(0xFF102027), Offset(2f * scale, 45f * scale), Size(16f * scale, 12f * scale), style = Stroke(1.5f * scale))
    drawLine(Color(0xFF263238), Offset(15f * scale, 45f * scale), Offset(15f * scale, 38f * scale), strokeWidth = 2f * scale) // Chain

    // Signboard Anvil Icon
    val signAnvil = Path().apply {
        moveTo(6f * scale, 53f * scale)
        lineTo(14f * scale, 53f * scale)
        lineTo(12f * scale, 49f * scale)
        lineTo(16f * scale, 49f * scale)
        lineTo(4f * scale, 49f * scale)
        close()
    }
    drawPath(signAnvil, Color(0xFFF1C40F))

    // Ground line
    pxRectS(2f, 95f, W - 4f, 5f, Color(0xFF1B2631), scale)
}

/**
 * 3. BLACKSMITH (Squat, asymmetrical forge with glowing crucible)
 */


/**
 * 4. RELICS / WIZARD TOWER (Extreme verticality, floating shattering crystal)
 */
fun DrawScope.drawDetailedPortal(animTime: Float = 0f) {
    val scale = size.height / 100f
    val W = size.width / scale
    val CX = W / 2f

    // Massive Floating Cracked Magicite Crystal at the TOP (Y: 5..35)
    val floatY = 20f + sin(animTime * 0.003f) * 3f
    val auraPulse = 0.4f + sin(animTime * 0.005f) * 0.2f
    drawCircle(Color(0xFF00FF87).copy(alpha = auraPulse), radius = 18f * scale, center = Offset(CX * scale, floatY * scale))

    val crystal = Path().apply {
        moveTo(CX * scale, (floatY - 15f) * scale)
        lineTo((CX + 12f) * scale, floatY * scale)
        lineTo(CX * scale, (floatY + 15f) * scale)
        lineTo((CX - 12f) * scale, floatY * scale)
        close()
    }
    drawPath(crystal, Color(0xFF00FF87))
    drawPath(crystal, Color.White.copy(alpha = 0.8f), style = Stroke(1.5f * scale))

    // Orbiting Gold/Energy Rings around Crystal
    val ringPhase = animTime * 0.002f
    val rY = floatY + sin(ringPhase) * 5f
    val rPath = Path().apply {
        moveTo((CX - 18f) * scale, rY * scale)
        quadraticTo(CX * scale, (rY + 10f) * scale, (CX + 18f) * scale, rY * scale)
    }
    drawPath(rPath, Color(0xFFF1C40F), style = Stroke(2.5f * scale))

    // Slender Twisted Spire Body (Detached from Crystal!)
    val spire = Path().apply {
        moveTo((CX - 4f) * scale, 45f * scale) // Peak of base
        quadraticTo((CX - 15f) * scale, 70f * scale, (CX - 22f) * scale, 95f * scale) // Left flare
        lineTo((CX + 22f) * scale, 95f * scale) // Base width
        quadraticTo((CX + 15f) * scale, 70f * scale, (CX + 4f) * scale, 45f * scale) // Right flare
        close()
    }
    drawPath(spire, Color(0xFF16202C))
    drawPath(spire, Color(0xFF0F172A), style = Stroke(2.5f * scale))

    // Runic energy bands climbing the spire
    for (y in 55..85 step 15) {
        val wAtY = 8f + (y - 45f) * 0.4f
        pxRectS(CX - wAtY, y.toFloat(), wAtY * 2, 2f, Color(0xFF00FF87).copy(alpha = auraPulse), scale)
    }

    // Base Portal Doorway
    val door = Path().apply {
        moveTo((CX - 8f) * scale, 95f * scale)
        lineTo((CX - 8f) * scale, 75f * scale)
        quadraticTo(CX * scale, 65f * scale, (CX + 8f) * scale, 75f * scale)
        lineTo((CX + 8f) * scale, 95f * scale)
        close()
    }
    drawPath(door, Color(0xFF051C14))
    drawPath(door, Color(0xFF00FF87), style = Stroke(2f * scale))

    // Ground
    pxRectS(5f, 95f, W - 10f, 5f, Color(0xFF111827), scale)
}

/**
 * 5. LIBRARY / ARCHIVES (Domed observatory with floating books)
 */
fun DrawScope.drawDetailedLibrary(animTime: Float = 0f) {
    val scale = size.height / 100f
    val W = size.width / scale
    val CX = W / 2f

    // Floating Books Orbiting the Library!
    repeat(3) { i ->
        val bPhase = ((animTime + i * 333f) * 0.001f) % 1f
        val bX = CX + sin(bPhase * 6.28f) * (W * 0.4f)
        val bY = 40f + cos(bPhase * 6.28f) * 20f

        val book = Path().apply {
            moveTo(bX * scale, bY * scale)
            lineTo((bX + 8f) * scale, (bY - 4f) * scale)
            lineTo((bX + 12f) * scale, bY * scale)
            lineTo((bX + 4f) * scale, (bY + 4f) * scale)
            close()
        }
        drawPath(book, Color(0xFFFDE68A))
        drawPath(book, Color(0xFFD97706), style = Stroke(1.5f * scale))
        drawCircle(Color(0xFFC084FC).copy(alpha = 0.5f), radius = 4f * scale, center = Offset((bX + 6f) * scale, bY * scale))
    }

    // Main Square Base
    pxRectS(15f, 50f, W - 30f, 45f, Color(0xFF1F2937), scale)

    // Golden Pillars
    listOf(15f, CX - 15f, CX + 10f, W - 20f).forEach { px ->
        pxRectS(px, 50f, 5f, 45f, Color(0xFF374151), scale)
        pxRectS(px - 1f, 50f, 7f, 3f, Color(0xFFD97706), scale)
    }

    // Grand Indigo Dome
    val dome = Path().apply {
        moveTo(10f * scale, 50f * scale)
        quadraticTo(CX * scale, 0f, (W - 10f) * scale, 50f * scale)
        close()
    }
    drawPath(dome, Color(0xFF1E1B4B))
    drawPath(dome, Color(0xFFD97706), style = Stroke(2.5f * scale))

    // Sticking out Observatory Telescope (From Dome)
    val scope = Path().apply {
        moveTo((CX + 15f) * scale, 35f * scale)
        lineTo((CX + 35f) * scale, 15f * scale)
        lineTo((CX + 38f) * scale, 18f * scale)
        lineTo((CX + 18f) * scale, 38f * scale)
        close()
    }
    drawPath(scope, Color(0xFFF59E0B))
    drawCircle(Color(0xFF60A5FA), radius = 2f * scale, center = Offset((CX + 36.5f) * scale, 16.5f * scale)) // Lens

    // Grand Arched Window (Glowing Blue)
    val window = Path().apply {
        moveTo((CX - 10f) * scale, 85f * scale)
        lineTo((CX - 10f) * scale, 65f * scale)
        quadraticTo(CX * scale, 50f * scale, (CX + 10f) * scale, 65f * scale)
        lineTo((CX + 10f) * scale, 85f * scale)
        close()
    }
    drawPath(window, Color(0xFF312E81))
    val windowGlow = 0.6f + sin(animTime * 0.004f) * 0.25f
    drawPath(window, Color(0xFF818CF8).copy(alpha = windowGlow))
    drawPath(window, Color(0xFFF59E0B), style = Stroke(2f * scale))

    // Ground
    pxRectS(5f, 95f, W - 10f, 5f, Color(0xFF111827), scale)
}

/**
 * 6. THE INN (Cozy, Crooked Tavern with Giant Roof Barrel)
 */
fun DrawScope.drawDetailedInn(animTime: Float = 0f) {
    val scale = size.height / 100f
    val W = size.width / scale

    // Crooked Stone Base
    pxRectS(15f, 60f, W - 30f, 35f, Color(0xFF5D6D7E), scale)

    // Timber Upper Story hanging over the edge!
    pxRectS(10f, 30f, W - 20f, 30f, Color(0xFF6D4C41), scale)
    for (y in 35..55 step 6) {
        pxRectS(10f, y.toFloat(), W - 20f, 1.5f, Color(0xFF3E2723), scale)
    }

    // Giant Steep Roof
    val roof = Path().apply {
        moveTo(0f * scale, 30f * scale)
        lineTo((W / 2f) * scale, 5f * scale)
        lineTo(W * scale, 30f * scale)
        close()
    }
    drawPath(roof, Color(0xFF900C3F))
    drawPath(roof, Color(0xFF581845), style = Stroke(3f * scale))

    // Giant Wooden Beer Keg on the Roof
    val keg = Path().apply {
        moveTo((W / 2f + 10f) * scale, 25f * scale)
        quadraticTo((W / 2f + 20f) * scale, 15f * scale, (W / 2f + 30f) * scale, 25f * scale)
        lineTo((W / 2f + 30f) * scale, 32f * scale)
        lineTo((W / 2f + 10f) * scale, 32f * scale)
        close()
    }
    drawPath(keg, Color(0xFF7E5109))
    drawPath(keg, Color(0xFF3E2723), style = Stroke(2f * scale))
    pxRectS(W / 2f + 15f, 22f, 10f, 2f, Color(0xFFBDC3C7), scale) // Metal band

    // Warm Windows
    val glow = Color(0xFFF4D03F).copy(alpha = 0.85f + sin(animTime * 0.005f) * 0.15f)
    pxRectS(15f, 40f, 12f, 12f, glow, scale)
    drawRect(Color(0xFF3E2723), Offset(15f * scale, 40f * scale), Size(12f * scale, 12f * scale), style = Stroke(2f * scale))
    pxRectS(W - 27f, 40f, 12f, 12f, glow, scale)
    drawRect(Color(0xFF3E2723), Offset((W - 27f) * scale, 40f * scale), Size(12f * scale, 12f * scale), style = Stroke(2f * scale))

    // Hanging Signboard (Left)
    pxRectS(5f, 40f, 14f, 12f, Color(0xFF5D4037), scale)
    drawRect(Color(0xFF3E2723), Offset(5f * scale, 40f * scale), Size(14f * scale, 12f * scale), style = Stroke(2f * scale))
    drawCircle(Color(0xFFF1C40F), radius = 2.5f * scale, center = Offset(12f * scale, 46f * scale)) // Beer icon

    // Entrance & Bulletin Board
    val door = Path().apply {
        moveTo((W / 2f - 10f) * scale, 95f * scale)
        lineTo((W / 2f - 10f) * scale, 75f * scale)
        quadraticTo((W / 2f) * scale, 65f * scale, (W / 2f + 10f) * scale, 75f * scale)
        lineTo((W / 2f + 10f) * scale, 95f * scale)
        close()
    }
    drawPath(door, Color(0xFF3E2723))

    // Bulletin Board on the right of the door
    pxRectS(W / 2f + 15f, 70f, 22f, 16f, Color(0xFF7E5109), scale)
    drawRect(Color(0xFF422C05), Offset((W / 2f + 15f) * scale, 70f * scale), Size(22f * scale, 16f * scale), style = Stroke(1.5f * scale))
    pxRectS(W / 2f + 17f, 72f, 5f, 6f, Color(0xFFF5EEF8), scale) // Note
    pxRectS(W / 2f + 25f, 75f, 6f, 5f, Color(0xFFF5EEF8), scale) // Note

    // Ground
    pxRectS(5f, 95f, W - 10f, 5f, Color(0xFF34495E), scale)
}

/**
 * 7. COLOSSEUM (Wide sweeping amphitheater with giant crossed swords)
 */
fun DrawScope.drawDetailedColosseum(animTime: Float = 0f) {
    val scale = size.height / 100f
    val W = size.width / scale
    val CX = W / 2f

    // Sweeping Curved Arena Body
    val arena = Path().apply {
        moveTo(5f * scale, 95f * scale)
        lineTo(5f * scale, 55f * scale)
        quadraticTo(CX * scale, 35f * scale, (W - 5f) * scale, 55f * scale)
        lineTo((W - 5f) * scale, 95f * scale)
        close()
    }
    drawPath(arena, Color(0xFF2C3E50))
    drawPath(arena, Color(0xFF1B2631), style = Stroke(3f * scale))

    // Multiple Archway Tiers
    for (yOffset in listOf(55f, 75f)) {
        for (i in 0..6) {
            if (yOffset == 75f && i in 2..4) continue // Skip center bottom for main gate

            val archX = 15f + i * ((W - 30f) / 6f)
            val arch = Path().apply {
                moveTo((archX - 5f) * scale, (yOffset + 15f) * scale)
                lineTo((archX - 5f) * scale, (yOffset + 5f) * scale)
                quadraticTo(archX * scale, yOffset * scale, (archX + 5f) * scale, (yOffset + 5f) * scale)
                lineTo((archX + 5f) * scale, (yOffset + 15f) * scale)
                close()
            }
            drawPath(arch, Color(0xFF111827))
        }
    }

    // Giant Main Entrance Gate
    val gate = Path().apply {
        moveTo((CX - 15f) * scale, 95f * scale)
        lineTo((CX - 15f) * scale, 65f * scale)
        quadraticTo(CX * scale, 50f * scale, (CX + 15f) * scale, 65f * scale)
        lineTo((CX + 15f) * scale, 95f * scale)
        close()
    }
    drawPath(gate, Color(0xFF0F172A))
    drawPath(gate, Color(0xFF1B2631), style = Stroke(3f * scale))

    // Spiked Iron Bars
    for (gx in (CX.toInt() - 10)..(CX.toInt() + 10) step 5) {
        drawLine(Color(0xFF5D6D7E), Offset(gx.toFloat() * scale, 58f * scale), Offset(gx.toFloat() * scale, 95f * scale), strokeWidth = 2f * scale)
    }

    // Giant Crossed Bronze Swords above Gate
    drawLine(Color(0xFFD35400), Offset((CX - 15f) * scale, 35f * scale), Offset((CX + 15f) * scale, 65f * scale), strokeWidth = 3f * scale)
    drawLine(Color(0xFFD35400), Offset((CX + 15f) * scale, 35f * scale), Offset((CX - 15f) * scale, 65f * scale), strokeWidth = 3f * scale)
    drawCircle(Color(0xFFF1C40F), radius = 4f * scale, center = Offset(CX * scale, 50f * scale)) // Shield boss in center

    // Braziers flanking
    listOf(CX - 22f, CX + 22f).forEach { bx ->
        pxRectS(bx - 3f, 80f, 6f, 15f, Color(0xFF34495E), scale)
        val fire = 0.8f + sin(animTime * 0.01f + bx) * 0.2f
        drawCircle(Color(0xFFE67E22).copy(alpha = 0.5f * fire), radius = 6f * scale, center = Offset(bx * scale, 75f * scale))
        drawCircle(Color(0xFFD35400).copy(alpha = fire), radius = 4f * scale, center = Offset(bx * scale, 75f * scale))
    }

    // Ground
    pxRectS(2f, 95f, W - 4f, 5f, Color(0xFF1B2631), scale)
}