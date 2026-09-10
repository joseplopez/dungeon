package com.game.dungeon.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

private fun DrawScope.setupDrawHelpers(): (Float, Float, Float, Float, Color) -> Unit {
    val scale = size.width / 100f
    return { x, y, w, h, color ->
        drawRect(color, Offset(x * scale, y * scale), Size(w * scale, h * scale))
    }
}

fun DrawScope.drawDetailedInn() {
    val px = setupDrawHelpers()
    val W = size.width
    val H = size.height
    val s = W / 100f

    // 1. Foundation/Stone Base
    px(10f, 60f, 80f, 30f, Color(0xFF5D6D7E))
    for (i in 0..5) { // Stone texture
        px(15f + i * 14f, 65f + (i % 2) * 10f, 8f, 5f, Color(0xFF2C3E50).copy(alpha = 0.3f))
    }

    // 2. Main Wooden Structure (Tudor Style)
    px(15f, 25f, 70f, 40f, Color(0xFFFAD7A0)) // Cream wall
    // Timber frames
    px(15f, 25f, 5f, 40f, Color(0xFF4E342E))
    px(80f, 25f, 5f, 40f, Color(0xFF4E342E))
    px(15f, 25f, 70f, 4f, Color(0xFF4E342E))
    px(15f, 45f, 70f, 4f, Color(0xFF4E342E))
    for (x in 30..70 step 15) px(x.toFloat(), 25f, 4f, 40f, Color(0xFF4E342E))

    // 3. Complex Roof
    val roofPath = Path().apply {
        moveTo(5f, 30f)
        lineTo(50f, 0f)
        lineTo(95f, 30f)
        close()
    }
    drawPath(roofPath, Color(0xFF7B241C)) // Dark red shingles
    // Shingle lines
    for (i in 1..4) {
        px(10f + i * 8f, 30f - i * 6f, 70f - i * 16f, 1f, Color.Black.copy(alpha = 0.2f))
    }

    // 4. Windows with Warm Glow
    val glow = Color(0xFFF4D03F)
    px(25f, 32f, 12f, 10f, glow)
    px(63f, 32f, 12f, 10f, glow)
    drawRect(Color.Black.copy(alpha = 0.4f), Offset(25f * s, 32f * s), Size(12f * s, 10f * s), style = Stroke(1.5f * s))
    drawRect(Color.Black.copy(alpha = 0.4f), Offset(63f * s, 32f * s), Size(12f * s, 10f * s), style = Stroke(1.5f * s))

    // 5. Arched Doorway
    val doorPath = Path().apply {
        moveTo(40f * s, 90f * s)
        lineTo(40f * s, 65f * s)
        quadraticTo(50f * s, 55f * s, 60f * s, 65f * s)
        lineTo(60f * s, 90f * s)
        close()
    }
    drawPath(doorPath, Color(0xFF3E2723))
    drawPath(doorPath, Color(0xFFFAD7A0), style = Stroke(2f * s))
    drawCircle(Color(0xFFF4D03F), radius = 2f * s, center = Offset(56f * s, 75f * s)) // Doorknob
}

fun DrawScope.drawDetailedCrystalShop() {
    val px = setupDrawHelpers()
    val W = size.width
    val H = size.height
    val s = W / 100f

    // 1. Wizard Tower Base (Purple/Blue Stone)
    px(25f, 30f, 50f, 65f, Color(0xFF212F3D))
    // Gradient detail
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF5B2C6F), Color(0xFF212F3D))),
        Offset(25f * s, 30f * s), Size(50f * s, 65f * s)
    )

    // 2. Conical Hat Roof
    val roofPath = Path().apply {
        moveTo(15f * s, 35f * s)
        lineTo(50f * s, -10f * s)
        lineTo(85f * s, 35f * s)
        close()
    }
    drawPath(roofPath, Color(0xFF8E44AD))
    drawPath(roofPath, Color.White.copy(alpha = 0.2f), style = Stroke(3f * s))

    // 3. Floating Giant Crystal
    val cryPath = Path().apply {
        moveTo(50f * s, -35f * s)
        lineTo(60f * s, -20f * s)
        lineTo(50f * s, -5f * s)
        lineTo(40f * s, -20f * s)
        close()
    }
    drawPath(cryPath, Color(0xFF3498DB))
    drawPath(cryPath, Color.White.copy(alpha = 0.5f), style = Stroke(1f * s))

    // 4. Magical Windows (Round)
    drawCircle(Color(0xFFAED6F1).copy(alpha = 0.6f), radius = 8f * s, center = Offset(40f * s, 45f * s))
    drawCircle(Color(0xFFAED6F1).copy(alpha = 0.4f), radius = 6f * s, center = Offset(62f * s, 60f * s))

    // 5. Enchanted Door
    px(42f, 80f, 16f, 15f, Color(0xFF1A1A1A))
    px(42f, 80f, 16f, 2f, Color.Cyan) // Magic seal line
}

fun DrawScope.drawDetailedBarracks() {
    val px = setupDrawHelpers()
    val W = size.width
    val H = size.height
    val s = W / 100f

    // 1. Heavy Stone Block Base
    px(10f, 25f, 80f, 65f, Color(0xFF707B7C))
    // Battlement tops
    for (x in 10..80 step 15) px(x.toFloat(), 15f, 10f, 10f, Color(0xFF707B7C))

    // 2. The Forge Chimney
    px(65f, 0f, 15f, 25f, Color(0xFF424949))
    px(63f, -5f, 19f, 6f, Color(0xFF17202A))
    // Smoke puff (static pixel circle)
    drawCircle(Color.Gray.copy(alpha = 0.5f), radius = 8f * s, center = Offset(72f * s, -15f * s))

    // 3. Training Banner
    px(15f, 35f, 12f, 35f, Color(0xFFC0392B))
    px(15f, 35f, 12f, 3f, Color(0xFFF1C40F)) // Gold top
    px(18f, 45f, 6f, 6f, Color.White.copy(alpha = 0.5f)) // Emblem

    // 4. Heavy Iron Gate
    px(35f, 65f, 30f, 25f, Color(0xFF17202A))
    for (i in 0..4) { // Grille lines
        px(38f + i * 6f, 65f, 1f, 25f, Color(0xFF515A5A))
    }

    // 5. Weapon Rack Detail
    px(75f, 75f, 15f, 2f, Color(0xFF4E342E))
    px(78f, 65f, 2f, 10f, Color(0xFFBDC3C7)) // "Sword"
}

fun DrawScope.drawDetailedPortal() {
    val px = setupDrawHelpers()
    val W = size.width
    val H = size.height
    val s = W / 100f

    // 1. Ancient Monolith Pillars
    px(10f, 20f, 15f, 70f, Color(0xFF2C3E50))
    px(75f, 20f, 15f, 70f, Color(0xFF2C3E50))
    // Top Arch
    val archPath = Path().apply {
        moveTo(10f * s, 25f * s)
        quadraticTo(50f * s, -10f * s, 90f * s, 25f * s)
        lineTo(90f * s, 40f * s)
        quadraticTo(50f * s, 10f * s, 10f * s, 40f * s)
        close()
    }
    drawPath(archPath, Color(0xFF2C3E50))

    // 2. Swirling Nebula Interior
    val interiorPath = Path().apply {
        moveTo(25f * s, 90f * s)
        lineTo(25f * s, 40f * s)
        quadraticTo(50f * s, 15f * s, 75f * s, 40f * s)
        lineTo(75f * s, 90f * s)
        close()
    }
    drawPath(
        interiorPath,
        Brush.verticalGradient(listOf(Color(0xFF0D0720), Color(0xFF8E44AD), Color(0xFF2E86C1)))
    )

    // 3. Magic Runes
    val runeColor = Color(0xFF5DADE2)
    repeat(8) { i ->
        val ry = 25f + i * 8f
        px(15f, ry, 3f, 3f, runeColor.copy(alpha = 0.7f))
        px(82f, ry, 3f, 3f, runeColor.copy(alpha = 0.7f))
    }

    // 4. Floating Stars/Particles
    val starRng = java.util.Random(99)
    repeat(15) {
        val sx = 30f + starRng.nextFloat() * 40f
        val sy = 30f + starRng.nextFloat() * 50f
        drawCircle(Color.White, radius = 1f * s, center = Offset(sx * s, sy * s))
    }
}
