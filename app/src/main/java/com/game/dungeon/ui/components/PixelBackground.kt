package com.game.dungeon.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.game.dungeon.data.models.BiomeType
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DungeonBackground(biomeType: BiomeType? = BiomeType.GENERIC_DUNGEON) {
    val infiniteTransition = rememberInfiniteTransition(label = "dungeon_bg")

    val scrollOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scroll_offset"
    )

    val torchFlicker by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "torch_flicker"
    )

    val slowPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "slow_pulse"
    )

    val medPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "med_pulse"
    )

    val fastTick by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fast_tick"
    )

    val effectiveBiome = biomeType ?: BiomeType.GENERIC_DUNGEON

    Canvas(Modifier.fillMaxSize()) {
        when (effectiveBiome) {
            BiomeType.CORNELIA_CASTLE -> drawCorneliaCastle(scrollOffset, torchFlicker)
            BiomeType.CHAOS_SHRINE -> drawChaosShrine(scrollOffset, slowPulse, medPulse)
            BiomeType.GURGU_VOLCANO -> drawGurguVolcano(scrollOffset, torchFlicker, medPulse)
            BiomeType.SEA_SHRINE -> drawSeaShrine(scrollOffset, slowPulse)
            BiomeType.EARTH_CAVE -> drawEarthCave(scrollOffset, torchFlicker)
            BiomeType.CRYSTAL_TOWER -> drawCrystalTower(scrollOffset, slowPulse, fastTick)
            BiomeType.MYSIDIAN_TOWER -> drawMysidianTower(scrollOffset, slowPulse)
            BiomeType.PANDAEMONIUM -> drawPandaemonium(scrollOffset, medPulse)
            BiomeType.MOUNT_ORDEALS -> drawMountOrdeals(scrollOffset, slowPulse)
            BiomeType.BARON_CASTLE -> drawBaronCastle(scrollOffset, torchFlicker)
            BiomeType.ANCIENT_CASTLE -> drawAncientCastle(scrollOffset, slowPulse)
            BiomeType.NARSHE_MINES -> drawNarsheMines(scrollOffset, torchFlicker)
            BiomeType.MAGITEK_FACTORY -> drawMagitekFactory(scrollOffset, medPulse)
            BiomeType.KEFKA_TOWER -> drawKefkaTower(scrollOffset, slowPulse)
            BiomeType.FLOATING_CONTINENT -> drawFloatingContinent(scrollOffset, slowPulse)
            BiomeType.MIDGAR_SEWERS -> drawMidgarSewers(scrollOffset, torchFlicker)
            BiomeType.SHINRA_BUILDING -> drawShinraBuilding(scrollOffset, fastTick)
            BiomeType.NORTHERN_CRATER -> drawNorthernCrater(scrollOffset, slowPulse)
            BiomeType.GOLDEN_SAUCER -> drawGoldenSaucer(scrollOffset, fastTick)
            BiomeType.BEVELLE_TEMPLE -> drawBevelleTemple(scrollOffset, slowPulse)
            BiomeType.OMEGA_RUINS -> drawOmegaRuins(scrollOffset, fastTick)
            BiomeType.SIN_INTERIOR -> drawSinInterior(scrollOffset, slowPulse, medPulse)
            BiomeType.GENERIC_DUNGEON -> drawGenericDungeon(scrollOffset, torchFlicker)
        }
    }
}

// ==========================================
// BIOME 1: CORNELIA_CASTLE
// ==========================================
private fun DrawScope.drawCorneliaCastle(scrollOffset: Float, torchFlicker: Float) {
    val W = size.width
    val H = size.height

    // L1: Deep Background Atmosphere
    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF0A0814), Color(0xFF1A1228), Color(0xFF0D0A18))
        )
    )
    drawCircle(
        Color(0.8f, 0.4f, 0.1f, 0.08f),
        radius = W * 0.7f,
        center = Offset(W / 2f, H)
    )

    // L2: Distant Silhouette Structures
    val colW = W * 0.06f
    val colH = H * 0.75f
    val colY = H * 0.08f
    listOf(0.12f, 0.35f, 0.65f, 0.88f).forEach { xFrac ->
        val cx = W * xFrac
        drawRect(Color(0xFF121020), Offset(cx, colY), Size(colW, colH))
        // Barred windows
        drawRect(Color(0.4f, 0.5f, 0.7f, 0.15f), Offset(cx + colW * 0.2f, colY + colH * 0.2f), Size(colW * 0.6f, colH * 0.15f))
        drawRect(Color(0.4f, 0.5f, 0.7f, 0.15f), Offset(cx + colW * 0.2f, colY + colH * 0.45f), Size(colW * 0.6f, colH * 0.15f))
        // Crenellations
        drawRect(Color(0xFF121020), Offset(cx, colY - 12f), Size(colW * 0.25f, 12f))
        drawRect(Color(0xFF121020), Offset(cx + colW * 0.75f, colY - 12f), Size(colW * 0.25f, 12f))
    }

    // L3: Stone Wall Tiles (Flemish bond)
    val floorY = H * 0.72f
    val tileW = W / 22f
    val tileH = H * 0.055f
    var wy = H * 0.12f
    var row = 0
    while (wy < floorY) {
        val rowOffset = if (row % 2 == 0) 0f else tileW / 2f
        var wx = rowOffset - (scrollOffset * 0.3f % tileW)
        while (wx < W + tileW) {
            val baseColor = if ((row + (wx / tileW).toInt()) % 2 == 0) Color(0xFF1C1628) else Color(0xFF181420)
            drawRect(baseColor, Offset(wx, wy), Size(tileW - 1f, tileH - 1f))
            drawRect(Color(0xFF2A2038), Offset(wx, wy), Size(tileW - 1f, 1f)) // highlight
            drawRect(Color(0xFF0D0A14), Offset(wx, wy + tileH - 1f), Size(tileW - 1f, 1f)) // shadow
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L4: Hanging Royal Banners
    listOf(W * 0.2f, W * 0.5f, W * 0.8f).forEach { bx ->
        // Pole
        drawRect(Color(0xFF8B6914), Offset(bx - 20f, 0f), Size(40f, 12f))
        // Body
        val bW = 36f
        val bH = 110f
        drawRect(Color(0xFF8B0000), Offset(bx - bW / 2f, 12f), Size(bW, bH))
        // Bottom tip
        val bannerPath = Path().apply {
            moveTo(bx - bW / 2f, 12f + bH)
            lineTo(bx + bW / 2f, 12f + bH)
            lineTo(bx, 12f + bH + 20f)
            close()
        }
        drawPath(bannerPath, Color(0xFF8B0000))
        // Gold stripe
        drawRect(Color(0xFFFFD700), Offset(bx - 3f, 12f), Size(6f, bH + 10f))
        // Left highlight
        drawRect(Color(0xFFCC2222), Offset(bx - bW / 2f, 12f), Size(2f, bH + 10f))
    }

    // L5: Torches
    listOf(W * 0.15f, W * 0.85f).forEach { tx ->
        val ty = H * 0.32f
        drawRect(Color(0xFF3A2A10), Offset(tx - 6f, ty), Size(12f, 20f))
        drawRect(Color(0xFF5A4018), Offset(tx - 3f, ty + 2f), Size(6f, 16f))

        // Glow circles
        drawCircle(Color(1f, 0.35f, 0.1f, torchFlicker * 0.06f), radius = W * 0.18f, center = Offset(tx, ty))
        drawCircle(Color(1f, 0.45f, 0.1f, torchFlicker * 0.12f), radius = W * 0.10f, center = Offset(tx, ty))
        drawCircle(Color(1f, 0.6f, 0.2f, torchFlicker * 0.22f), radius = W * 0.05f, center = Offset(tx, ty))
        drawCircle(Color(1f, 0.8f, 0.4f, torchFlicker * 0.5f), radius = W * 0.02f, center = Offset(tx, ty))

        // Flame layers
        drawRect(Color(1f, 0.4f, 0f, torchFlicker), Offset(tx - 8f, ty - 28f), Size(16f, 28f))
        drawRect(Color(1f, 0.6f, 0f, torchFlicker), Offset(tx - 5f, ty - 38f), Size(10f, 38f))
        drawRect(Color(1f, 0.9f, 0.4f, torchFlicker), Offset(tx - 2f, ty - 20f), Size(4f, 20f))
    }

    // L6: Floor with Perspective
    drawPerspectiveFloor(floorY, Color(0xFF1A1428), Color(0xFF141020), Color(0xFF2A2038), scrollOffset)
    drawRect(Color(0.8f, 0.5f, 0.1f, torchFlicker * 0.04f), Offset(0f, floorY), Size(W, 12f))

    // L7: Vignette
    drawVignette(0.75f)
}

// ==========================================
// BIOME 2: CHAOS_SHRINE
// ==========================================
private fun DrawScope.drawChaosShrine(scrollOffset: Float, slowPulse: Float, medPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Void Atmosphere
    drawRect(Color(0xFF050308))
    drawCircle(
        Brush.radialGradient(
            listOf(Color(0.4f, 0f, 0.6f, slowPulse * 0.25f), Color.Transparent),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.55f
        ),
        radius = W * 0.55f,
        center = Offset(W / 2f, H / 2f)
    )

    // L2: Shattered Pillars
    val pillarColor = Color(0xFF0F0A18)
    val pillarHi = Color(0xFF1A1230)
    // Left broken pillar
    drawRect(pillarColor, Offset(W * 0.1f, 0f), Size(W * 0.08f, H * 0.6f))
    drawRect(pillarHi, Offset(W * 0.1f, 0f), Size(W * 0.03f, H * 0.6f))
    // Center fallen pillar section
    drawRect(pillarColor, Offset(W * 0.45f, H * 0.5f), Size(W * 0.1f, H * 0.22f))
    // Right pillar section
    drawRect(pillarColor, Offset(W * 0.8f, 0f), Size(W * 0.08f, H * 0.35f))
    drawRect(pillarColor, Offset(W * 0.8f, H * 0.45f), Size(W * 0.08f, H * 0.27f))

    // L3: Cracked Stone Walls
    val floorY = H * 0.72f
    val tileW = W / 18f
    val tileH = H * 0.05f
    var wy = H * 0.15f
    var row = 0
    while (wy < floorY) {
        var wx = -(scrollOffset * 0.35f % tileW)
        while (wx < W + tileW) {
            val c = if ((row + (wx / tileW).toInt()) % 2 == 0) Color(0xFF140D20) else Color(0xFF1A1228)
            drawRect(c, Offset(wx, wy), Size(tileW - 1f, tileH - 1f))

            // Cracks glowing purple
            if ((row + (wx / tileW).toInt()) % 5 == 0) {
                val crackPath = Path().apply {
                    moveTo(wx + 2f, wy + 2f)
                    lineTo(wx + tileW * 0.4f, wy + tileH * 0.5f)
                    lineTo(wx + tileW * 0.3f, wy + tileH * 0.8f)
                    lineTo(wx + tileW * 0.8f, wy + tileH - 2f)
                }
                drawPath(crackPath, Color(0.6f, 0f, 0.8f, medPulse * 0.6f + 0.2f), style = Stroke(width = 2f))
            }
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L4: Void Rifts
    listOf(Offset(W * 0.25f, H * 0.3f), Offset(W * 0.5f, H * 0.25f), Offset(W * 0.75f, H * 0.35f)).forEach { riftCenter ->
        drawCircle(Color(0.6f, 0f, 0.9f, 0.3f + slowPulse * 0.2f), radius = 24f, center = riftCenter, style = Stroke(width = 3f))
        drawCircle(Color(0.2f, 0f, 0.4f, 0.6f), radius = 16f, center = riftCenter)
        drawCircle(Color(0.8f, 0.4f, 1f, medPulse), radius = 6f, center = riftCenter)
    }

    // L5: Floating Debris
    repeat(15) { i ->
        val dx = (W * 0.06f * i + scrollOffset * 0.2f) % W
        val dy = (H * 0.08f * i - scrollOffset * 0.1f) % (H * 0.7f)
        val finalY = if (dy < 0) dy + H * 0.7f else dy
        drawRect(Color(0x666600AA), Offset(dx, finalY), Size(4f, 4f))
    }

    // L6: Floor & Vignette
    drawPerspectiveFloor(floorY, Color(0xFF120A1E), Color(0xFF0C0614), Color(0xFF06030A), scrollOffset)
    drawVignette(0.85f)
}

// ==========================================
// BIOME 3: GURGU_VOLCANO
// ==========================================
private fun DrawScope.drawGurguVolcano(scrollOffset: Float, torchFlicker: Float, medPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Heat Atmosphere
    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF0A0400), Color(0xFF1A0800), Color(0xFF2A0E00))
        )
    )
    drawRect(Color(1f, 0.3f, 0f, torchFlicker * 0.04f)) // Fullscreen heat wash

    // L2: Volcanic Rock Silhouettes
    val mtnPath = Path().apply {
        moveTo(0f, H * 0.6f)
        lineTo(W * 0.2f, H * 0.25f)
        lineTo(W * 0.4f, H * 0.5f)
        lineTo(W * 0.65f, H * 0.2f)
        lineTo(W * 0.85f, H * 0.45f)
        lineTo(W, H * 0.3f)
        lineTo(W, H * 0.7f)
        lineTo(0f, H * 0.7f)
        close()
    }
    drawPath(mtnPath, Color(0xFF1A0C04))

    // L3: Dark Obsidian Walls & Cracks
    val floorY = H * 0.70f
    val tileW = W / 20f
    val tileH = H * 0.05f
    var wy = H * 0.15f
    var row = 0
    while (wy < floorY) {
        var wx = -(scrollOffset * 0.3f % tileW)
        while (wx < W + tileW) {
            val c = if ((row + (wx / tileW).toInt()) % 2 == 0) Color(0xFF1A0C04) else Color(0xFF140A02)
            drawRect(c, Offset(wx, wy), Size(tileW - 1f, tileH - 1f))
            if ((row + (wx / tileW).toInt()) % 4 == 0) {
                drawRect(Color(1f, 0.4f, 0f, 0.5f + medPulse * 0.3f), Offset(wx + 4f, wy + tileH / 2f), Size(tileW - 8f, 2f))
            }
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L4: Magma Floor
    val lavaY = floorY
    drawRect(
        Brush.horizontalGradient(
            listOf(Color(0xFFFF2200), Color(0xFFFF6600), Color(0xFFFFAA00), Color(0xFFFF6600), Color(0xFFFF2200))
        ),
        Offset(0f, lavaY),
        Size(W, H - lavaY)
    )

    // Animated Lava Wave Surface
    val lavaWavePath = Path().apply {
        moveTo(0f, lavaY)
        var x = 0f
        while (x <= W) {
            val y = lavaY + sin(x * 0.03f + scrollOffset * 0.05f) * 6f
            lineTo(x, y)
            x += 8f
        }
        lineTo(W, lavaY)
        close()
    }
    drawPath(lavaWavePath, Color(1f, 0.6f, 0f, 0.8f))

    // Lava glow upward
    drawCircle(Color(1f, 0.5f, 0f, 0.15f * torchFlicker), radius = W * 0.4f, center = Offset(W / 2f, lavaY))

    // L5: Rising Embers
    repeat(25) { i ->
        val ex = (W * 0.04f * i + sin(scrollOffset * 0.02f + i) * 20f) % W
        val ey = (H * 0.9f - (scrollOffset * 0.5f + i * 30f)) % H
        val finalY = if (ey < 0) ey + H else ey
        drawCircle(Color(1f, 0.7f, 0.2f, 0.8f), radius = 2f, center = Offset(ex, finalY))
    }

    drawVignette(0.7f)
}

// ==========================================
// BIOME 4: SEA_SHRINE
// ==========================================
private fun DrawScope.drawSeaShrine(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Deep Ocean Atmosphere
    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF0D334D), Color(0xFF051D2D), Color(0xFF020C14))
        )
    )
    drawRect(Color(0f, 0.3f, 0.5f, 0.12f))

    // L2: Submerged Ruins & Coral
    val pillarC = Color(0xFF0D1E2A)
    drawRect(pillarC, Offset(W * 0.15f, H * 0.2f), Size(W * 0.08f, H * 0.52f))
    drawRect(pillarC, Offset(W * 0.75f, H * 0.25f), Size(W * 0.08f, H * 0.47f))
    // Coral growths
    drawCircle(Color(0.8f, 0.3f, 0.3f, 0.6f), radius = 18f, center = Offset(W * 0.15f + 10f, H * 0.2f))
    drawCircle(Color(0.9f, 0.5f, 0.1f, 0.5f), radius = 14f, center = Offset(W * 0.75f + 20f, H * 0.25f))

    // L3: Temple Stone Walls & Algae
    val floorY = H * 0.72f
    val tileW = W / 20f
    val tileH = H * 0.05f
    var wy = H * 0.12f
    var row = 0
    while (wy < floorY) {
        var wx = -(scrollOffset * 0.35f % tileW)
        while (wx < W + tileW) {
            val c = if ((row + (wx / tileW).toInt()) % 2 == 0) Color(0xFF0D1E28) else Color(0xFF0A1820)
            drawRect(c, Offset(wx, wy), Size(tileW - 1f, tileH - 1f))
            if ((row + (wx / tileW).toInt()) % 3 == 0) {
                drawRect(Color(0.1f, 0.5f, 0.2f, 0.25f), Offset(wx + 2f, wy), Size(3f, tileH - 1f)) // algae
            }
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L4: Caustic Light Shafts
    repeat(5) { i ->
        val xTop = W * (0.15f + i * 0.18f) + cos(scrollOffset * 0.01f + i) * 20f
        val lightPath = Path().apply {
            moveTo(xTop, 0f)
            lineTo(xTop + 25f, 0f)
            lineTo(xTop + 80f, floorY)
            lineTo(xTop - 20f, floorY)
            close()
        }
        drawPath(lightPath, Color(0.3f, 0.7f, 1f, 0.05f + sin(scrollOffset * 0.01f + i) * 0.02f))
    }

    // L5: Rising Bubbles
    repeat(25) { i ->
        val bx = (W * 0.04f * i + sin(scrollOffset * 0.02f + i) * 15f) % W
        val by = (H - (scrollOffset * 0.4f + i * 25f)) % H
        val finalY = if (by < 0) by + H else by
        drawCircle(Color(0.5f, 0.8f, 1f, 0.5f), radius = 5f, center = Offset(bx, finalY), style = Stroke(width = 1.5f))
    }

    // L6: Bioluminescent Floor
    drawPerspectiveFloor(floorY, Color(0xFF081822), Color(0xFF051018), Color(0xFF0E3040), scrollOffset)
    drawRect(Color(0.1f, 0.5f, 0.6f, 0.15f + slowPulse * 0.1f), Offset(0f, floorY), Size(W, 8f))
    drawVignette(0.7f)
}

// ==========================================
// BIOME 5: EARTH_CAVE
// ==========================================
private fun DrawScope.drawEarthCave(scrollOffset: Float, torchFlicker: Float) {
    val W = size.width
    val H = size.height

    // Atmosphere
    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF140D08), Color(0xFF22160C), Color(0xFF100A05))
        )
    )

    // Jagged Cave Walls (Top & Bottom Boundaries)
    val floorY = H * 0.72f
    val caveTop = Path().apply {
        moveTo(0f, 0f)
        lineTo(W, 0f)
        lineTo(W, H * 0.18f)
        var x = W
        while (x >= 0) {
            val y = H * 0.15f + sin(x * 0.02f + scrollOffset * 0.02f) * 15f
            lineTo(x, y)
            x -= 20f
        }
        close()
    }
    drawPath(caveTop, Color(0xFF1A1008))

    // Stalactites
    listOf(0.1f, 0.25f, 0.45f, 0.65f, 0.85f).forEach { xFrac ->
        val sx = W * xFrac
        val stalaPath = Path().apply {
            moveTo(sx - 15f, H * 0.15f)
            lineTo(sx + 15f, H * 0.15f)
            lineTo(sx, H * 0.35f)
            close()
        }
        drawPath(stalaPath, Color(0xFF281A0E))
    }

    // Stalagmites
    listOf(0.18f, 0.55f, 0.78f).forEach { xFrac ->
        val sx = W * xFrac
        val stalaPath = Path().apply {
            moveTo(sx - 20f, floorY)
            lineTo(sx + 20f, floorY)
            lineTo(sx, floorY - H * 0.18f)
            close()
        }
        drawPath(stalaPath, Color(0xFF22140A))
    }

    // Water Drips
    repeat(4) { i ->
        val dx = W * (0.25f + i * 0.2f)
        val dy = (H * 0.35f + (scrollOffset * 0.8f + i * 50f)) % (floorY - H * 0.35f) + H * 0.35f
        drawCircle(Color(0.4f, 0.6f, 0.8f, 0.7f), radius = 3f, center = Offset(dx, dy))
    }

    drawPerspectiveFloor(floorY, Color(0xFF1A1008), Color(0xFF120A04), Color(0xFF080402), scrollOffset)
    drawVignette(0.8f)
}

// ==========================================
// BIOME 6: CRYSTAL_TOWER
// ==========================================
private fun DrawScope.drawCrystalTower(scrollOffset: Float, slowPulse: Float, fastTick: Float) {
    val W = size.width
    val H = size.height

    // L1: Light Atmosphere
    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF0D1B2A), Color(0xFF1B2A4A), Color(0xFF0D1B2A))
        )
    )
    drawCircle(
        Brush.radialGradient(
            listOf(Color(0.6f, 0.9f, 1f, 0.18f), Color.Transparent),
            center = Offset(W / 2f, H / 3f),
            radius = W * 0.45f
        ),
        radius = W * 0.45f,
        center = Offset(W / 2f, H / 3f)
    )

    // L2: Giant Crystal Formations
    val floorY = H * 0.72f
    listOf(W * 0.1f to H * 0.15f, W * 0.3f to H * 0.22f, W * 0.7f to H * 0.18f, W * 0.88f to H * 0.1f).forEach { (cx, cy) ->
        val cW = 40f
        // Left face
        val pLeft = Path().apply {
            moveTo(cx, cy + 20f)
            lineTo(cx + cW / 2f, cy)
            lineTo(cx + cW / 2f, floorY)
            lineTo(cx, floorY)
            close()
        }
        drawPath(pLeft, Color(0xFF0A2030))
        // Right face
        val pRight = Path().apply {
            moveTo(cx + cW / 2f, cy)
            lineTo(cx + cW, cy + 20f)
            lineTo(cx + cW, floorY)
            lineTo(cx + cW / 2f, floorY)
            close()
        }
        drawPath(pRight, Color(0xFF1A5080))
        // Highlight cap
        drawCircle(Color(0.5f, 0.8f, 1f, 0.3f), radius = 15f, center = Offset(cx + cW / 2f, cy))
    }

    // L3: Mirror Tile Floor
    drawPerspectiveFloor(floorY, Color(0xFF0D253A), Color(0xFF081828), Color(0xFF1A5070), scrollOffset)

    // L4: Light Sweep Band
    val sweepX = (scrollOffset * 2f) % (W + 400f) - 200f
    val sweepPath = Path().apply {
        moveTo(sweepX, 0f)
        lineTo(sweepX + 150f, 0f)
        lineTo(sweepX + 50f, H)
        lineTo(sweepX - 100f, H)
        close()
    }
    drawPath(sweepPath, Color(1f, 1f, 1f, 0.05f))

    // L5: Sparkle Particles
    repeat(20) { i ->
        val sx = (W * 0.05f * i + 30f) % W
        val sy = (H * 0.03f * i + 50f) % (floorY - 20f)
        val sparkAlpha = (sin(scrollOffset * 0.05f + i) + 1f) / 2f * 0.8f
        drawRect(Color(0.8f, 1f, 1f, sparkAlpha), Offset(sx - 4f, sy - 1f), Size(8f, 2f))
        drawRect(Color(0.8f, 1f, 1f, sparkAlpha), Offset(sx - 1f, sy - 4f), Size(2f, 8f))
    }

    drawVignette(0.6f)
}

// ==========================================
// BIOME 7: MYSIDIAN_TOWER
// ==========================================
private fun DrawScope.drawMysidianTower(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Arcane Indigo Atmosphere
    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF060514), Color(0xFF120E30), Color(0xFF080618))
        )
    )

    // L2: Distant Bookshelves
    val shelfY = H * 0.15f
    val shelfH = H * 0.55f
    val floorY = H * 0.70f
    drawRect(Color(0xFF181024), Offset(W * 0.05f, shelfY), Size(W * 0.9f, shelfH))
    var bkX = W * 0.08f
    while (bkX < W * 0.9f) {
        val bkColor = when ((bkX % 5).toInt()) {
            0 -> Color(0xFF8B2222)
            1 -> Color(0xFF22668B)
            2 -> Color(0xFF8B6622)
            else -> Color(0xFF442266)
        }
        drawRect(bkColor, Offset(bkX, shelfY + 20f), Size(12f, shelfH - 40f))
        bkX += 16f
    }

    // L3: Glowing Arcane Glyphs
    listOf(Offset(W * 0.25f, H * 0.35f), Offset(W * 0.75f, H * 0.35f)).forEach { gc ->
        drawCircle(Color(0.9f, 0.7f, 0.2f, 0.2f + slowPulse * 0.15f), radius = 45f, center = gc, style = Stroke(width = 2f))
        drawCircle(Color(0.9f, 0.7f, 0.2f, 0.15f), radius = 30f, center = gc, style = Stroke(width = 1f))
    }

    drawPerspectiveFloor(floorY, Color(0xFF140D20), Color(0xFF0D0818), Color(0xFF281A38), scrollOffset)
    drawVignette(0.75f)
}

// ==========================================
// BIOME 8: PANDAEMONIUM
// ==========================================
private fun DrawScope.drawPandaemonium(scrollOffset: Float, medPulse: Float) {
    val W = size.width
    val H = size.height

    // Atmosphere
    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF04120A), Color(0xFF0A2214), Color(0xFF020804))
        )
    )

    // Bone Pillars
    val floorY = H * 0.72f
    listOf(W * 0.15f, W * 0.85f).forEach { px ->
        var py = H * 0.1f
        while (py < floorY) {
            drawRect(Color(0xFF889988), Offset(px - 15f, py), Size(30f, 18f))
            drawRect(Color(0xFF556655), Offset(px - 12f, py + 2f), Size(24f, 14f))
            py += 24f
        }
    }

    // Jade Pools
    drawCircle(Color(0f, 0.8f, 0.3f, 0.15f + medPulse * 0.1f), radius = W * 0.35f, center = Offset(W / 2f, floorY))

    drawPerspectiveFloor(floorY, Color(0xFF0A1A10), Color(0xFF05100A), Color(0xFF153520), scrollOffset)
    drawVignette(0.8f)
}

// ==========================================
// BIOME 9: MOUNT_ORDEALS
// ==========================================
private fun DrawScope.drawMountOrdeals(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // Open Sky Dusk
    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF1A0A20), Color(0xFF3A1C28), Color(0xFF5A3028))
        )
    )

    // Parallax Mountain Ridges
    val r1 = Path().apply {
        moveTo(0f, H * 0.5f)
        lineTo(W * 0.3f, H * 0.3f)
        lineTo(W * 0.7f, H * 0.45f)
        lineTo(W, H * 0.25f)
        lineTo(W, H * 0.75f)
        lineTo(0f, H * 0.75f)
        close()
    }
    drawPath(r1, Color(0xFF281420))

    val floorY = H * 0.72f
    drawPerspectiveFloor(floorY, Color(0xFF3A2020), Color(0xFF2A1515), Color(0xFF5A3535), scrollOffset)

    // Holy Light Shafts
    drawRect(
        Brush.verticalGradient(listOf(Color(1f, 0.9f, 0.6f, 0.12f), Color.Transparent)),
        Offset(W * 0.3f, 0f), Size(W * 0.4f, floorY)
    )
    drawVignette(0.65f)
}

// ==========================================
// BIOME 10: BARON_CASTLE
// ==========================================
private fun DrawScope.drawBaronCastle(scrollOffset: Float, torchFlicker: Float) {
    val W = size.width
    val H = size.height

    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF080D1A), Color(0xFF101A2E), Color(0xFF060912))
        )
    )

    val floorY = H * 0.72f

    // Riveted Metal Panels
    val panelW = W / 6f
    val panelH = H * 0.15f
    var py = H * 0.1f
    while (py < floorY) {
        var px = 0f
        while (px < W) {
            drawRect(Color(0xFF142035), Offset(px, py), Size(panelW - 2f, panelH - 2f))
            drawRect(Color(0xFF20304D), Offset(px + 2f, py + 2f), Size(panelW - 6f, panelH - 6f))
            // Rivets
            drawCircle(Color(0xFF88A0C0), radius = 2f, center = Offset(px + 6f, py + 6f))
            drawCircle(Color(0xFF88A0C0), radius = 2f, center = Offset(px + panelW - 8f, py + 6f))
            px += panelW
        }
        py += panelH
    }

    drawPerspectiveFloor(floorY, Color(0xFF101A28), Color(0xFF0A101C), Color(0xFF203550), scrollOffset)
    drawVignette(0.75f)
}

// ==========================================
// BIOME 11: ANCIENT_CASTLE
// ==========================================
private fun DrawScope.drawAncientCastle(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF1A140A), Color(0xFF2E2210), Color(0xFF140F06))
        )
    )

    val floorY = H * 0.72f

    // Sand Drifts along floor
    val sandPath = Path().apply {
        moveTo(0f, floorY)
        var x = 0f
        while (x <= W) {
            val y = floorY - 15f - sin(x * 0.015f + scrollOffset * 0.01f) * 12f
            lineTo(x, y)
            x += 20f
        }
        lineTo(W, floorY)
        close()
    }
    drawPath(sandPath, Color(0xFF3E3018))

    drawPerspectiveFloor(floorY, Color(0xFF281E0F), Color(0xFF1C140A), Color(0xFF4A381C), scrollOffset)
    drawVignette(0.75f)
}

// ==========================================
// BIOME 12: NARSHE_MINES
// ==========================================
private fun DrawScope.drawNarsheMines(scrollOffset: Float, torchFlicker: Float) {
    val W = size.width
    val H = size.height

    // Cold Mine Atmosphere
    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF060810), Color(0xFF0A0E14), Color(0xFF04060A))
        )
    )

    val floorY = H * 0.70f

    // Wooden Support Trusses
    val beamColor = Color(0xFF3A2510)
    val beamHi = Color(0xFF5A3A18)

    // Horizontal beams
    drawRect(beamColor, Offset(0f, H * 0.12f), Size(W, H * 0.04f))
    drawRect(beamHi, Offset(0f, H * 0.12f), Size(W, 3f))

    drawRect(beamColor, Offset(0f, floorY), Size(W, H * 0.04f))
    drawRect(beamHi, Offset(0f, floorY), Size(W, 3f))

    // Vertical posts
    listOf(W * 0.15f, W * 0.35f, W * 0.65f, W * 0.85f).forEach { px ->
        drawRect(beamColor, Offset(px - 9f, H * 0.12f), Size(18f, floorY - H * 0.12f))
        drawRect(beamHi, Offset(px - 9f, H * 0.12f), Size(3f, floorY - H * 0.12f))
    }

    // Hanging Swinging Lantern
    val swingAngle = sin(scrollOffset * 0.03f) * 15f
    val lx = W * 0.5f + swingAngle
    val ly = H * 0.28f
    drawLine(Color(0xFF666666), Offset(W * 0.5f, H * 0.14f), Offset(lx, ly), strokeWidth = 3f)

    drawRect(Color(0xFF2A1A04), Offset(lx - 10f, ly), Size(20f, 28f))
    drawRect(Color(1f, 0.7f, 0.2f, torchFlicker), Offset(lx - 5f, ly + 4f), Size(10f, 20f))
    drawCircle(Color(1f, 0.7f, 0.2f, torchFlicker * 0.25f), radius = W * 0.15f, center = Offset(lx, ly + 14f))

    // Icicles
    listOf(W * 0.08f, W * 0.25f, W * 0.45f, W * 0.75f, W * 0.92f).forEach { ix ->
        val icicleP = Path().apply {
            moveTo(ix - 8f, 0f)
            lineTo(ix + 8f, 0f)
            lineTo(ix, 45f)
            close()
        }
        drawPath(icicleP, Color(0.6f, 0.8f, 1f, 0.7f))
    }

    drawPerspectiveFloor(floorY, Color(0xFF10141A), Color(0xFF0A0C10), Color(0xFF1A222C), scrollOffset)
    drawVignette(0.8f)
}

// ==========================================
// BIOME 13: MAGITEK_FACTORY
// ==========================================
private fun DrawScope.drawMagitekFactory(scrollOffset: Float, medPulse: Float) {
    val W = size.width
    val H = size.height

    drawRect(Color(0xFF060808))
    drawRect(Color(0f, 0.3f, 0.1f, 0.04f)) // Green chemical wash

    val floorY = H * 0.72f

    // Pipe bundles
    listOf(H * 0.2f, H * 0.35f, H * 0.5f).forEach { py ->
        drawRect(Color(0xFF1A2020), Offset(0f, py), Size(W, 16f))
        drawRect(Color(0xFF3A4040), Offset(0f, py + 2f), Size(W, 4f))
    }

    // Glowing Green Conduits
    listOf(W * 0.25f, W * 0.75f).forEach { cx ->
        drawRect(Color(0f, 0.7f, 0.3f, 0.6f), Offset(cx - 3f, 0f), Size(6f, floorY))
        drawRect(Color(0f, 1f, 0.4f, 0.1f + medPulse * 0.1f), Offset(cx - 10f, 0f), Size(20f, floorY))
    }

    drawPerspectiveFloor(floorY, Color(0xFF0D1210), Color(0xFF080C0A), Color(0xFF1A2A22), scrollOffset)
    drawVignette(0.75f)
}

// ==========================================
// BIOME 14: KEFKA_TOWER
// ==========================================
private fun DrawScope.drawKefkaTower(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // Chaos Cycling Atmosphere
    val r = sin(slowPulse * 3.14159f) * 0.2f + 0.1f
    val b = cos(slowPulse * 3.14159f) * 0.2f + 0.1f
    drawRect(Color(r, 0.05f, b, 1f))

    val floorY = H * 0.72f

    // Mismatched Debris Fragments
    repeat(12) { i ->
        val dx = (W * 0.08f * i + scrollOffset * 0.3f) % W
        val dy = (H * 0.05f * i + sin(scrollOffset * 0.02f + i) * 30f) % (floorY - 40f)
        drawRect(Color(0x888844AA), Offset(dx, dy), Size(30f, 20f))
    }

    drawPerspectiveFloor(floorY, Color(0xFF221020), Color(0xFF180A18), Color(0xFF442040), scrollOffset)
    drawVignette(0.8f)
}

// ==========================================
// BIOME 15: FLOATING_CONTINENT
// ==========================================
private fun DrawScope.drawFloatingContinent(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // High Altitude Sky
    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF0A1830), Color(0xFF1A3050), Color(0xFF081020))
        )
    )

    val floorY = H * 0.70f

    // Rushing Cloud Sea Below
    repeat(8) { i ->
        val cx = (scrollOffset * 0.6f + i * 120f) % (W + 200f) - 100f
        drawCircle(Color(0.8f, 0.85f, 0.9f, 0.25f), radius = 60f, center = Offset(cx, floorY + 40f))
    }

    drawPerspectiveFloor(floorY, Color(0xFF1A202A), Color(0xFF101520), Color(0xFF2A3545), scrollOffset)
    drawVignette(0.65f)
}

// ==========================================
// BIOME 16: MIDGAR_SEWERS
// ==========================================
private fun DrawScope.drawMidgarSewers(scrollOffset: Float, torchFlicker: Float) {
    val W = size.width
    val H = size.height

    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF06100A), Color(0xFF0D2015), Color(0xFF040805))
        )
    )

    val floorY = H * 0.72f

    // Tunnel Perspective Arches
    listOf(0.2f, 0.4f, 0.6f, 0.8f).forEach { scale ->
        drawCircle(Color(0.1f, 0.3f, 0.15f, 0.2f), radius = W * scale, center = Offset(W / 2f, H * 0.4f), style = Stroke(width = 3f))
    }

    // Toxic Sludge Floor Channel
    drawRect(Color(0xFF10300A), Offset(0f, floorY), Size(W, H - floorY))
    drawRect(Color(0.2f, 0.8f, 0.1f, 0.3f + torchFlicker * 0.1f), Offset(W * 0.2f, floorY), Size(W * 0.6f, H - floorY))

    drawVignette(0.8f)
}

// ==========================================
// BIOME 17: SHINRA_BUILDING
// ==========================================
private fun DrawScope.drawShinraBuilding(scrollOffset: Float, fastTick: Float) {
    val W = size.width
    val H = size.height

    drawRect(Color(0xFF020408))

    val floorY = H * 0.72f

    // Skyline
    listOf(0.05f to 0.4f, 0.2f to 0.55f, 0.45f to 0.35f, 0.7f to 0.6f, 0.85f to 0.45f).forEach { (xFrac, hFrac) ->
        val bW = W * 0.12f
        val bH = H * hFrac
        drawRect(Color(0xFF060B14), Offset(W * xFrac, floorY - bH), Size(bW, bH))
        // Antenna red blink
        if (fastTick > 0.5f) {
            drawCircle(Color.Red, radius = 2.5f, center = Offset(W * xFrac + bW / 2f, floorY - bH))
        }
    }

    // Blue Neon Light Strips
    listOf(H * 0.2f, H * 0.4f, H * 0.6f).forEach { ny ->
        drawRect(Color(0f, 0.6f, 1f, 0.7f), Offset(0f, ny), Size(W, 3f))
        drawRect(Color(0f, 0.4f, 0.8f, 0.08f), Offset(0f, ny - 8f), Size(W, 19f))
    }

    drawPerspectiveFloor(floorY, Color(0xFF081018), Color(0xFF040810), Color(0xFF0066CC), scrollOffset)
    drawVignette(0.7f)
}

// ==========================================
// BIOME 18: NORTHERN_CRATER
// ==========================================
private fun DrawScope.drawNorthernCrater(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF020614), Color(0xFF081028), Color(0xFF02040A))
        )
    )

    val floorY = H * 0.72f

    // Lifestream Strands (Spiraling upward)
    repeat(6) { i ->
        val lx = W * (0.15f + i * 0.14f)
        val lifestreamP = Path().apply {
            moveTo(lx, floorY)
            cubicTo(lx + 30f, H * 0.5f, lx - 30f, H * 0.25f, lx, 0f)
        }
        drawPath(lifestreamP, Color(0.1f, 0.9f, 0.5f, 0.25f + slowPulse * 0.15f), style = Stroke(width = 4f))
    }

    drawPerspectiveFloor(floorY, Color(0xFF0A1520), Color(0xFF050A10), Color(0xFF104030), scrollOffset)
    drawVignette(0.75f)
}

// ==========================================
// BIOME 19: GOLDEN_SAUCER
// ==========================================
private fun DrawScope.drawGoldenSaucer(scrollOffset: Float, fastTick: Float) {
    val W = size.width
    val H = size.height

    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF12041A), Color(0xFF280838), Color(0xFF0E0214))
        )
    )

    val floorY = H * 0.72f

    // Casino Chaser Light Track
    val dotStep = 24f
    var dx = 0f
    var dotIdx = 0
    val activeIdx = (fastTick * 4f).toInt() % 4
    while (dx < W) {
        val color = when ((dotIdx + activeIdx) % 4) {
            0 -> Color.Yellow
            1 -> Color.Magenta
            2 -> Color.Cyan
            else -> Color.Green
        }
        drawCircle(color, radius = 3.5f, center = Offset(dx, H * 0.2f))
        drawCircle(color, radius = 3.5f, center = Offset(dx, H * 0.4f))
        dx += dotStep
        dotIdx++
    }

    drawPerspectiveFloor(floorY, Color(0xFF281030), Color(0xFF180820), Color(0xFFFFD700), scrollOffset)
    drawVignette(0.65f)
}

// ==========================================
// BIOME 20: BEVELLE_TEMPLE
// ==========================================
private fun DrawScope.drawBevelleTemple(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF080F1A), Color(0xFF101C2E), Color(0xFF040810))
        )
    )

    val floorY = H * 0.72f

    // Ivory Marble Columns
    listOf(W * 0.12f, W * 0.38f, W * 0.62f, W * 0.88f).forEach { px ->
        drawRect(Color(0xFF303848), Offset(px - 16f, 0f), Size(32f, floorY))
        drawRect(Color(0xFF485468), Offset(px - 8f, 0f), Size(16f, floorY))
    }

    // Energy Ribbon Tracks
    repeat(3) { i ->
        val ry = H * (0.25f + i * 0.15f)
        val waveP = Path().apply {
            moveTo(0f, ry)
            var x = 0f
            while (x <= W) {
                val y = ry + sin(x * 0.02f + scrollOffset * 0.05f + i) * 8f
                lineTo(x, y)
                x += 10f
            }
        }
        drawPath(waveP, Color(0.1f, 0.7f, 1f, 0.6f), style = Stroke(width = 3f))
    }

    drawPerspectiveFloor(floorY, Color(0xFF18202C), Color(0xFF101620), Color(0xFF4080C0), scrollOffset)
    drawVignette(0.7f)
}

// ==========================================
// BIOME 21: OMEGA_RUINS
// ==========================================
private fun DrawScope.drawOmegaRuins(scrollOffset: Float, fastTick: Float) {
    val W = size.width
    val H = size.height

    drawRect(Color(0xFF020410))

    val floorY = H * 0.72f

    // Scanning Laser
    val laserY = (scrollOffset * 3f) % floorY
    drawRect(Color(0f, 1f, 0.8f, 0.6f), Offset(0f, laserY), Size(W, 2f))
    drawRect(Color(0f, 1f, 0.8f, 0.1f), Offset(0f, laserY - 10f), Size(W, 22f))

    // Digital Code Streams
    repeat(15) { i ->
        val cx = W * (0.06f * i + 0.03f)
        val cy = (scrollOffset * 2f + i * 40f) % floorY
        drawRect(Color(0f, 0.8f, 0.3f, 0.5f), Offset(cx, cy), Size(4f, 12f))
    }

    drawPerspectiveFloor(floorY, Color(0xFF060A18), Color(0xFF030510), Color(0xFF00FFCC), scrollOffset)
    drawVignette(0.75f)
}

// ==========================================
// BIOME 22: SIN_INTERIOR
// ==========================================
private fun DrawScope.drawSinInterior(scrollOffset: Float, slowPulse: Float, medPulse: Float) {
    val W = size.width
    val H = size.height

    drawRect(
        Brush.verticalGradient(
            listOf(Color(0xFF1A0408), Color(0xFF380810), Color(0xFF100204))
        )
    )

    val floorY = H * 0.72f

    // Pulsing Bio-Ribs
    val ribScale = 1f + medPulse * 0.05f
    listOf(W * 0.15f, W * 0.85f).forEach { rx ->
        var ry = H * 0.1f
        while (ry < floorY) {
            drawCircle(Color(0xFF551111), radius = 18f * ribScale, center = Offset(rx, ry))
            drawCircle(Color(0xFF882222), radius = 10f * ribScale, center = Offset(rx, ry))
            ry += 35f
        }
    }

    drawPerspectiveFloor(floorY, Color(0xFF28080C), Color(0xFF180406), Color(0xFF661111), scrollOffset)
    drawVignette(0.85f)
}

// ==========================================
// BIOME 23: GENERIC_DUNGEON
// ==========================================
private fun DrawScope.drawGenericDungeon(scrollOffset: Float, torchFlicker: Float) {
    val W = size.width
    val H = size.height

    drawRect(Color(0xFF0A0614), size = size)

    // Arch
    val archColor = Color(0xFF12091E)
    drawRect(archColor, Offset(W * 0.08f, H * 0.1f), Size(W * 0.06f, H * 0.85f))
    drawRect(Color(0xFF1A0F2A), Offset(W * 0.085f, H * 0.1f), Size(W * 0.04f, H * 0.83f))
    drawRect(archColor, Offset(W * 0.86f, H * 0.1f), Size(W * 0.06f, H * 0.85f))
    drawRect(Color(0xFF1A0F2A), Offset(W * 0.865f, H * 0.1f), Size(W * 0.04f, H * 0.83f))
    drawRect(archColor, Offset(W * 0.08f, H * 0.1f), Size(W * 0.84f, H * 0.08f))

    val floorY = H * 0.72f
    drawPerspectiveFloor(floorY, Color(0xFF1C1230), Color(0xFF160E26), Color(0xFF0A0614), scrollOffset)

    // Torches
    listOf(W * 0.18f, W * 0.82f).forEach { tx ->
        val ty = H * 0.3f
        drawRect(Color(0xFF554400), Offset(tx - 4f, ty + 8f), Size(8f, 16f))
        val glowAlpha = torchFlicker * 0.4f
        drawCircle(Color(1f, 0.4f, 0f, glowAlpha), radius = 40f, center = Offset(tx, ty))
        drawRect(Color(1f, 0.5f, 0f, torchFlicker), Offset(tx - 4f, ty - 14f), Size(8f, 16f))
    }

    drawVignette(0.75f)
}

// ==========================================
// SHARED HELPER FUNCTIONS
// ==========================================
private fun DrawScope.drawPerspectiveFloor(
    floorY: Float,
    tileColor1: Color,
    tileColor2: Color,
    groutColor: Color,
    scrollOffset: Float
) {
    val W = size.width
    val H = size.height
    val tileW = W / 20f
    val tileH = H * 0.06f
    var col = 0
    var x = -(scrollOffset % (tileW * 2))
    while (x < W + tileW) {
        val tileColor = if (col % 2 == 0) tileColor1 else tileColor2
        drawRect(tileColor, Offset(x, floorY), Size(tileW - 2f, tileH))
        drawRect(groutColor, Offset(x + tileW - 2f, floorY), Size(2f, tileH))
        drawRect(groutColor, Offset(x, floorY + tileH - 1f), Size(tileW, 1f))
        x += tileW
        col++
    }
    drawRect(Color(0x88000000), Offset(0f, floorY - 6f), Size(W, 6f))
}

private fun DrawScope.drawVignette(maxAlpha: Float) {
    val W = size.width
    val H = size.height
    drawRect(
        Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0, 0, 0, (maxAlpha * 255).toInt())),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

@Composable
fun InnBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "inn_bg")
    val fireFlicker by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(120, easing = LinearEasing), RepeatMode.Reverse),
        label = "fire_flicker"
    )
    val fireFlicker2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(80, easing = LinearEasing), RepeatMode.Reverse),
        label = "fire_flicker2"
    )

    Canvas(Modifier.fillMaxSize()) {
        val W = size.width
        val H = size.height

        // Base warm dark background
        drawRect(Color(0xFF120A04), size = size)

        // === CEILING BEAMS (dark wood horizontal) ===
        val beamColor = Color(0xFF1A0E06)
        val beamHighlight = Color(0xFF2A1A0A)
        listOf(0.0f, 0.18f, 0.36f).forEach { yFrac ->
            drawRect(beamColor, Offset(0f, H * yFrac), Size(W, H * 0.045f))
            drawRect(beamHighlight, Offset(0f, H * yFrac), Size(W, H * 0.008f))
        }

        // === STONE WALL BACKGROUND (upper 60%) ===
        val wallH = H * 0.62f
        val stoneW = W / 18f
        val stoneH = H * 0.048f
        var wy = H * 0.05f
        var wrow = 0
        while (wy < wallH) {
            val offX = if (wrow % 2 == 0) 0f else stoneW / 2f
            var wx = offX
            while (wx < W) {
                val c = if ((wrow + wx.toInt()) % 3 == 0) Color(0xFF1E1208) else Color(0xFF180E06)
                drawRect(c, Offset(wx, wy), Size(stoneW - 2f, stoneH - 2f))
                wx += stoneW
            }
            wy += stoneH
            wrow++
        }

        // === WOODEN FLOOR PLANKS (lower 38%) ===
        val floorY = H * 0.62f
        var py = floorY
        var plankRow = 0
        while (py < H) {
            val pc = if (plankRow % 2 == 0) Color(0xFF2A1A08) else Color(0xFF241608)
            drawRect(pc, Offset(0f, py), Size(W, H * 0.058f))
            drawRect(Color(0xFF0A0604), Offset(0f, py + H * 0.055f), Size(W, H * 0.003f))
            // Plank grain lines
            var gx = 80f + (plankRow * 120f) % W
            while (gx < W) {
                drawRect(Color(0xFF1A0E04), Offset(gx, py), Size(2f, H * 0.055f))
                gx += W / 6f
            }
            py += H * 0.058f
            plankRow++
        }

        // === FIREPLACE (left side) ===
        val fpX = W * 0.04f
        val fpY = H * 0.3f
        val fpW = W * 0.12f
        val fpH = H * 0.35f
        // Stone surround
        drawRect(Color(0xFF2A2018), Offset(fpX - 10f, fpY - 10f), Size(fpW + 20f, fpH + 10f))
        drawRect(Color(0xFF1A1410), Offset(fpX, fpY), Size(fpW, fpH))
        // Fire glow radiating outward
        drawCircle(
            Color(1f, 0.35f, 0f, fireFlicker * 0.35f),
            radius = W * 0.22f, center = Offset(fpX + fpW / 2f, fpY + fpH * 0.5f)
        )
        drawCircle(
            Color(1f, 0.6f, 0f, fireFlicker * 0.25f),
            radius = W * 0.14f, center = Offset(fpX + fpW / 2f, fpY + fpH * 0.5f)
        )
        // Flame layers
        val fc = fpX + fpW / 2f
        val fb = fpY + fpH * 0.85f
        drawRect(Color(1f, 0.4f, 0f, fireFlicker), Offset(fc - 20f, fb - 50f), Size(40f, 50f))
        drawRect(Color(1f, 0.6f, 0f, fireFlicker2), Offset(fc - 14f, fb - 70f), Size(28f, 55f))
        drawRect(Color(1f, 0.85f, 0.1f, fireFlicker), Offset(fc - 8f, fb - 85f), Size(16f, 50f))
        drawRect(Color(1f, 1f, 0.5f, fireFlicker2 * 0.8f), Offset(fc - 4f, fb - 95f), Size(8f, 30f))
        // Embers on floor of fireplace
        repeat(5) { i ->
            drawCircle(
                Color(1f, 0.5f, 0f, fireFlicker * 0.9f),
                radius = 3f, center = Offset(fpX + fpW * 0.2f + i * fpW * 0.15f, fpY + fpH * 0.92f)
            )
        }
        // Mantel (shelf above fireplace)
        drawRect(Color(0xFF3A2810), Offset(fpX - 15f, fpY - 20f), Size(fpW + 30f, 15f))
        drawRect(Color(0xFF4A3418), Offset(fpX - 15f, fpY - 25f), Size(fpW + 30f, 8f))

        // === WARM LIGHT WASH over entire scene from fireplace ===
        drawRect(Color(1f, 0.35f, 0f, fireFlicker * 0.06f), size = size)

        // === WALL LANTERN (right side) ===
        val lx = W * 0.88f
        val ly = H * 0.28f
        drawRect(Color(0xFF444422), Offset(lx - 6f, ly - 20f), Size(12f, 4f)) // bracket
        drawRect(Color(0xFF333311), Offset(lx - 4f, ly - 16f), Size(8f, 24f)) // lantern body
        drawRect(Color(1f, 0.8f, 0.2f, fireFlicker * 0.5f), Offset(lx - 2f, ly - 12f), Size(4f, 16f))
        drawCircle(Color(1f, 0.7f, 0f, fireFlicker * 0.3f), radius = 50f, center = Offset(lx, ly))

        // === VIGNETTE ===
        drawRect(
            Brush.radialGradient(
                listOf(Color.Transparent, Color(0xBB000000)),
                Offset(W / 2f, H / 2f), W * 0.72f
            ), size = size
        )
    }
}

@Composable
fun TownParallaxBackground(scrollOffset: Float) {
    Canvas(Modifier.fillMaxSize()) {
        val W = size.width
        val H = size.height

        // LAYER 1: Deep Sky & Stars (0.05x scroll)
        drawRect(Color(0xFF050A1A), size = size)
        val starRng = java.util.Random(42)
        repeat(60) {
            val sx = (starRng.nextFloat() * W - (scrollOffset * 0.05f)) % W
            val drawX = if (sx < 0) sx + W else sx
            drawCircle(Color.White.copy(alpha = 0.4f), radius = 1.5f, center = Offset(drawX, starRng.nextFloat() * H * 0.4f))
        }

        // LAYER 2: Distant Mountains (0.15x scroll)
        val mountainY = H * 0.5f
        val mWidth = 500f
        var mx = -(scrollOffset * 0.15f % mWidth)
        while (mx < W + mWidth) {
            val path = Path().apply {
                moveTo(mx, mountainY)
                lineTo(mx + mWidth * 0.3f, mountainY - 100f)
                lineTo(mx + mWidth * 0.6f, mountainY - 180f)
                lineTo(mx + mWidth * 0.8f, mountainY - 80f)
                lineTo(mx + mWidth, mountainY)
                close()
            }
            drawPath(path, Color(0xFF1B0F2E)) // Dark purple mountains
            mx += mWidth
        }

        // LAYER 3: Forest Silhouette (0.4x scroll)
        val forestY = H * 0.62f
        val treeW = 140f
        var tx = -(scrollOffset * 0.4f % treeW)
        while (tx < W + treeW) {
            drawRect(Color(0xFF0D1B0D), Offset(tx, forestY - 80f), Size(treeW * 0.7f, 100f))
            drawCircle(Color(0xFF0D1B0D), radius = treeW * 0.5f, center = Offset(tx + treeW * 0.35f, forestY - 80f))
            tx += treeW
        }

        // LAYER 4: The Ground & Stone Road (1.0x scroll)
        val groundY = H * 0.75f
        // Dirt base
        drawRect(Color(0xFF2A1A0A), Offset(0f, groundY), Size(W, H - groundY))

        // Cobblestone Path
        val pathY = groundY + 10f
        val cobW = 80f
        var cx = -(scrollOffset % cobW)
        while (cx < W + cobW) {
            drawRect(Color(0xFF3A2A1A), Offset(cx + 5f, pathY + 10f), Size(cobW - 10f, 25f))
            drawRect(Color(0xFF3A2A1A), Offset(cx + 25f, pathY + 45f), Size(cobW - 15f, 20f))
            cx += cobW
        }

        // Foreground Grass tufts
        val grassW = 120f
        var gx = -(scrollOffset % grassW)
        while (gx < W + grassW) {
            drawRect(Color(0xFF142208), Offset(gx + 10f, H - 25f), Size(20f, 15f))
            gx += grassW
        }
    }
}
