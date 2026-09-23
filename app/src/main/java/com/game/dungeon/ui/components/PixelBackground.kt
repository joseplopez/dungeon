package com.game.dungeon.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.game.dungeon.data.models.BiomeType
import kotlin.math.*

@Composable
fun DungeonBackground(biomeType: BiomeType? = BiomeType.GENERIC_DUNGEON) {
    val infiniteTransition = rememberInfiniteTransition(label = "dungeon_bg")

    val torchFlicker by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(140, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "torch_flicker"
    )

    val scrollOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scroll_offset"
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

    Canvas(Modifier.fillMaxSize()) {
        when (biomeType ?: BiomeType.GENERIC_DUNGEON) {
            BiomeType.CORNELIA_CASTLE -> drawCorneliaCastle(scrollOffset, torchFlicker)
            BiomeType.CHAOS_SHRINE -> drawChaosShrine(scrollOffset, slowPulse, medPulse)
            BiomeType.GURGU_VOLCANO -> drawGurguVolcano(scrollOffset, torchFlicker, medPulse)
            BiomeType.SEA_SHRINE -> drawSeaShrine(scrollOffset, slowPulse)
            BiomeType.EARTH_CAVE -> drawEarthCave(scrollOffset, torchFlicker)
            BiomeType.CRYSTAL_TOWER -> drawCrystalTower(scrollOffset, slowPulse)
            BiomeType.MYSIDIAN_TOWER -> drawMysidianTower(scrollOffset, slowPulse)
            BiomeType.PANDAEMONIUM -> drawPandaemonium(scrollOffset, slowPulse)
            BiomeType.MOUNT_ORDEALS -> drawMountOrdeals(scrollOffset, slowPulse)
            BiomeType.BARON_CASTLE -> drawBaronCastle(scrollOffset, torchFlicker)
            BiomeType.ANCIENT_CASTLE -> drawAncientCastle(scrollOffset, slowPulse)
            BiomeType.NARSHE_MINES -> drawNarsheMines(scrollOffset, torchFlicker)
            BiomeType.MAGITEK_FACTORY -> drawMagitekFactory(scrollOffset, fastTick)
            BiomeType.KEFKA_TOWER -> drawKefkaTower(scrollOffset, slowPulse)
            BiomeType.FLOATING_CONTINENT -> drawFloatingContinent(scrollOffset)
            BiomeType.MIDGAR_SEWERS -> drawMidgarSewers(scrollOffset, medPulse)
            BiomeType.SHINRA_BUILDING -> drawShinraBuilding(scrollOffset, fastTick)
            BiomeType.NORTHERN_CRATER -> drawNorthernCrater(scrollOffset, slowPulse)
            BiomeType.GOLDEN_SAUCER -> drawGoldenSaucer(scrollOffset, fastTick)
            BiomeType.BEVELLE_TEMPLE -> drawBevelleTemple(scrollOffset, slowPulse)
            BiomeType.OMEGA_RUINS -> drawOmegaRuins(scrollOffset, fastTick)
            BiomeType.SIN_INTERIOR -> drawSinInterior(scrollOffset, slowPulse)
            BiomeType.GENERIC_DUNGEON -> drawGenericDungeon(scrollOffset, torchFlicker)
        }
    }
}

private fun DrawScope.drawCorneliaCastle(scrollOffset: Float, torchFlicker: Float) {
    val W = size.width
    val H = size.height

    // L1: Deep royal dark blue/purple atmosphere
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF0A0714), Color(0xFF1B122B), Color(0xFF0E091B))),
        size = size
    )
    drawCircle(
        Color(0.9f, 0.45f, 0.1f, 0.08f * torchFlicker),
        radius = W * 0.6f,
        center = Offset(W / 2f, H)
    )

    // L2: Stone wall tiles (Flemish bond brick wall)
    val floorY = H * 0.72f
    val tileW = W / 18f
    val tileH = H * 0.055f
    var wy = H * 0.05f
    var row = 0
    while (wy < floorY) {
        val shift = if (row % 2 == 0) 0f else tileW / 2f
        var wx = shift - (scrollOffset * 0.15f % tileW)
        while (wx < W + tileW) {
            val baseColor = if ((row + (wx / tileW).toInt()) % 2 == 0) Color(0xFF221A30) else Color(0xFF1B1428)
            drawRect(baseColor, Offset(wx, wy), Size(tileW - 1.5f, tileH - 1.5f))
            drawLine(Color(0xFF352B48), Offset(wx, wy), Offset(wx + tileW - 1.5f, wy), strokeWidth = 1f)
            drawLine(Color(0xFF0E0B18), Offset(wx, wy + tileH - 1.5f), Offset(wx + tileW - 1.5f, wy + tileH - 1.5f), strokeWidth = 1f)
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L3: Arched Alcoves with Iron-Barred Windows
    val alcovePositions = listOf(0.18f, 0.5f, 0.82f)
    val alcoveW = W * 0.14f
    val alcoveH = H * 0.4f
    val alcoveTopY = H * 0.12f

    alcovePositions.forEach { xFrac ->
        val ax = W * xFrac - alcoveW / 2f
        
        // Dark recessed arch background
        val archPath = Path().apply {
            moveTo(ax, alcoveTopY + alcoveH)
            lineTo(ax, alcoveTopY + alcoveW / 2f)
            quadraticTo(ax + alcoveW / 2f, alcoveTopY - 10f, ax + alcoveW, alcoveTopY + alcoveW / 2f)
            lineTo(ax + alcoveW, alcoveTopY + alcoveH)
            close()
        }
        drawPath(archPath, Color(0xFF0C0916))
        drawPath(archPath, Color(0xFF423458), style = Stroke(3f))

        // Window sky light inside alcove
        val winW = alcoveW * 0.6f
        val winH = alcoveH * 0.55f
        val winLeft = ax + (alcoveW - winW) / 2f
        val winTop = alcoveTopY + alcoveH * 0.15f

        val windowPath = Path().apply {
            moveTo(winLeft, winTop + winH)
            lineTo(winLeft, winTop + winW / 2f)
            quadraticTo(winLeft + winW / 2f, winTop - 5f, winLeft + winW, winTop + winW / 2f)
            lineTo(winLeft + winW, winTop + winH)
            close()
        }
        // Glowing sky behind bars
        drawPath(windowPath, Color(0.45f, 0.58f, 0.8f, 0.4f))

        // Iron bars in window
        val barCount = 3
        repeat(barCount) { i ->
            val bx = winLeft + (i + 1) * (winW / (barCount + 1))
            drawLine(Color(0xFF0E0B18), Offset(bx, winTop), Offset(bx, winTop + winH), strokeWidth = 3f)
        }
        drawLine(Color(0xFF0E0B18), Offset(winLeft, winTop + winH * 0.5f), Offset(winLeft + winW, winTop + winH * 0.5f), strokeWidth = 3f)
        drawPath(windowPath, Color(0xFF605078), style = Stroke(2f))
    }

    // L4: Pillars in front of wall
    listOf(0.06f, 0.34f, 0.66f, 0.94f).forEach { xFrac ->
        val px = W * xFrac
        val pw = W * 0.05f
        drawRect(Color(0xFF161126), Offset(px - pw / 2f, H * 0.05f), Size(pw, floorY - H * 0.05f))
        drawRect(Color(0xFF2B223E), Offset(px - pw / 2f, H * 0.05f), Size(pw * 0.4f, floorY - H * 0.05f))
        drawRect(Color(0xFF423458), Offset(px - pw / 2f - 4f, H * 0.05f), Size(pw + 8f, 10f))
        drawRect(Color(0xFF423458), Offset(px - pw / 2f - 4f, floorY - 10f), Size(pw + 8f, 10f))
    }

    // L5: Hanging Royal Banners
    listOf(0.28f, 0.72f).forEach { xFrac ->
        val bx = W * xFrac
        val bannerW = W * 0.07f
        val bannerH = H * 0.35f
        val topY = H * 0.15f

        // Pole
        drawRect(Color(0xFF8B6914), Offset(bx - bannerW / 2f - 8f, topY - 6f), Size(bannerW + 16f, 6f))
        drawCircle(Color(0xFFFFD700), radius = 5f, center = Offset(bx - bannerW / 2f - 8f, topY - 3f))
        drawCircle(Color(0xFFFFD700), radius = 5f, center = Offset(bx + bannerW / 2f + 8f, topY - 3f))

        // Body
        val bannerPath = Path().apply {
            moveTo(bx - bannerW / 2f, topY)
            lineTo(bx + bannerW / 2f, topY)
            lineTo(bx + bannerW / 2f, topY + bannerH)
            lineTo(bx, topY + bannerH + bannerW * 0.4f)
            lineTo(bx - bannerW / 2f, topY + bannerH)
            close()
        }
        drawPath(bannerPath, Color(0xFF8B0000))
        drawPath(bannerPath, Color(0xFFFFD700), style = Stroke(2.5f))

        // Center Gold Stripe
        drawRect(Color(0xFFFFD700), Offset(bx - 3f, topY), Size(6f, bannerH + bannerW * 0.2f))
        
        // Tassel
        drawCircle(Color(0xFFFFD700), radius = 4f, center = Offset(bx, topY + bannerH + bannerW * 0.4f))
    }

    // L6: Flaming Wall Torches
    listOf(0.12f, 0.88f).forEach { txFrac ->
        val tx = W * txFrac
        val ty = H * 0.38f

        // Bracket
        drawRect(Color(0xFF5A4018), Offset(tx - 6f, ty), Size(12f, 22f))
        drawRect(Color(0xFF8B6914), Offset(tx - 3f, ty + 2f), Size(6f, 18f))

        // Light glow pools
        drawCircle(Color(1f, 0.4f, 0.1f, torchFlicker * 0.08f), radius = W * 0.18f, center = Offset(tx, ty))
        drawCircle(Color(1f, 0.5f, 0.1f, torchFlicker * 0.16f), radius = W * 0.10f, center = Offset(tx, ty))
        drawCircle(Color(1f, 0.7f, 0.2f, torchFlicker * 0.35f), radius = W * 0.04f, center = Offset(tx, ty))

        // Flame layers
        val flameW = W * 0.018f
        val flameH = H * 0.06f
        drawRect(Color(1f, 0.35f, 0f, torchFlicker), Offset(tx - flameW / 2f, ty - flameH), Size(flameW, flameH))
        drawRect(Color(1f, 0.65f, 0.1f, torchFlicker), Offset(tx - flameW * 0.3f, ty - flameH * 1.2f), Size(flameW * 0.6f, flameH * 1.1f))
        drawRect(Color(1f, 0.9f, 0.4f, torchFlicker * 0.9f), Offset(tx - flameW * 0.15f, ty - flameH * 1.35f), Size(flameW * 0.3f, flameH * 0.8f))
    }

    // L7: Perspective Tiled Floor
    drawRect(Color(0xFF140F20), Offset(0f, floorY), Size(W, H - floorY))
    val vpX = W / 2f
    repeat(10) { i ->
        val progress = (i + 1) / 10f
        val lineY = floorY + (H - floorY) * progress
        val leftX = vpX - vpX * (progress * 1.5f)
        val rightX = vpX + (W - vpX) * (progress * 1.5f)
        drawLine(Color(0xFF322646), Offset(leftX, lineY), Offset(rightX, lineY), strokeWidth = 1.5f)
    }

    // Torch light reflection pool on floor
    listOf(0.12f, 0.88f).forEach { txFrac ->
        drawCircle(
            Color(1f, 0.4f, 0.1f, torchFlicker * 0.08f),
            radius = W * 0.18f,
            center = Offset(W * txFrac, floorY + (H - floorY) * 0.3f)
        )
    }

    // L8: Atmospheric Vignette
    drawRect(
        Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0xBB000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawChaosShrine(scrollOffset: Float, slowPulse: Float, medPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Void atmosphere
    drawRect(Color(0xFF050308), size = size)
    drawCircle(
        Brush.radialGradient(
            listOf(Color(0.4f, 0f, 0.6f, 0.15f + slowPulse * 0.15f), Color.Transparent),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.55f
        ),
        radius = W * 0.55f,
        center = Offset(W / 2f, H / 2f)
    )

    // L2: Shattered pillars
    listOf(0.18f to 0.6f, 0.5f to 0.3f, 0.82f to 0.7f).forEach { (xFrac, heightFrac) ->
        val px = W * xFrac
        val pw = W * 0.08f
        val pTop = H * 0.15f
        val pLen = H * heightFrac
        drawRect(Color(0xFF0F0A18), Offset(px, pTop), Size(pw, pLen))
        drawRect(Color(0xFF1A1230), Offset(px, pTop), Size(pw * 0.4f, pLen))
        val breakPath = Path().apply {
            moveTo(px, pTop + pLen)
            lineTo(px + pw * 0.3f, pTop + pLen - 15f)
            lineTo(px + pw * 0.7f, pTop + pLen + 10f)
            lineTo(px + pw, pTop + pLen - 20f)
            lineTo(px + pw, pTop + pLen + 30f)
            lineTo(px, pTop + pLen + 30f)
            close()
        }
        drawPath(breakPath, Color(0xFF050308))
    }

    // L3: Cracked stone walls
    val wallBottom = H * 0.72f
    val tileW = W / 20f
    val tileH = H * 0.055f
    var wy = H * 0.05f
    var row = 0
    while (wy < wallBottom) {
        val shift = if (row % 2 == 0) 0f else tileW / 2f
        var wx = shift - (scrollOffset * 0.1f % tileW)
        while (wx < W + tileW) {
            val c = if ((row + (wx / tileW).toInt()) % 3 == 0) Color(0xFF1A1228) else Color(0xFF140D20)
            drawRect(c, Offset(wx, wy), Size(tileW - 1f, tileH - 1f))
            wx += tileW
        }
        wy += tileH
        row++
    }

    repeat(4) { i ->
        val cx = W * (0.2f + i * 0.22f)
        val cy = H * (0.2f + i * 0.1f)
        val crackPath = Path().apply {
            moveTo(cx, cy)
            lineTo(cx + 15f, cy + 25f)
            lineTo(cx - 10f, cy + 50f)
            lineTo(cx + 20f, cy + 80f)
        }
        drawPath(crackPath, Color(0.4f, 0f, 0.6f, 0.15f), style = Stroke(5f))
        drawPath(crackPath, Color(0.7f, 0.1f, 0.9f, 0.4f + medPulse * 0.4f), style = Stroke(2f))
    }

    // L4: Void rifts
    repeat(3) { i ->
        val rx = W * (0.25f + i * 0.28f)
        val ry = H * (0.22f + (i % 2) * 0.2f)
        val riftPath = Path().apply {
            moveTo(rx, ry - 30f)
            quadraticTo(rx + 20f, ry, rx, ry + 30f)
            quadraticTo(rx - 20f, ry, rx, ry - 30f)
            close()
        }
        drawPath(riftPath, Color(0.2f, 0f, 0.4f, 0.6f))
        drawPath(riftPath, Color(0.7f, 0.1f, 1f, 0.3f + slowPulse * 0.3f), style = Stroke(3f))
        drawCircle(Color(0.9f, 0.5f, 1f, medPulse), radius = 4f, center = Offset(rx, ry))
    }

    // L5: Floating particles
    val particleRng = java.util.Random(1337)
    repeat(20) { i ->
        val px = (particleRng.nextFloat() * W + i * 30f) % W
        val py = ((particleRng.nextFloat() * H) - (scrollOffset * 0.5f + i * 20f)) % H
        val drawY = if (py < 0) py + H else py
        val pAlpha = 0.2f + sin(scrollOffset * 0.05f + i) * 0.2f
        drawRect(Color(0.6f, 0f, 0.8f, max(0f, pAlpha)), Offset(px, drawY), Size(4f, 4f))
    }

    // L6: Floor
    val floorY = H * 0.72f
    drawRect(Color(0xFF0F081C), Offset(0f, floorY), Size(W, H - floorY))
    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xEE000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.7f
        ),
        size = size
    )
}

private fun DrawScope.drawGurguVolcano(scrollOffset: Float, torchFlicker: Float, medPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Heat atmosphere
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF0A0400), Color(0xFF1A0800), Color(0xFF2A0E00))),
        size = size
    )
    drawRect(Color(1f, 0.3f, 0f, torchFlicker * 0.05f), size = size)

    // L2: Volcanic rock silhouettes
    val mtnPath = Path().apply {
        moveTo(0f, H * 0.65f)
        lineTo(W * 0.2f, H * 0.45f)
        lineTo(W * 0.35f, H * 0.55f)
        lineTo(W * 0.55f, H * 0.38f)
        lineTo(W * 0.75f, H * 0.58f)
        lineTo(W, H * 0.42f)
        lineTo(W, H * 0.7f)
        lineTo(0f, H * 0.7f)
        close()
    }
    drawPath(mtnPath, Color(0xFF1A0C04))
    drawPath(mtnPath, Color(1f, 0.4f, 0f, 0.2f), style = Stroke(2f))

    // L3: Dark obsidian walls with lava cracks
    val wallBottom = H * 0.68f
    val tileW = W / 18f
    val tileH = H * 0.055f
    var wy = H * 0.05f
    var row = 0
    while (wy < wallBottom) {
        var wx = -(scrollOffset * 0.1f % tileW)
        while (wx < W + tileW) {
            val isLavaCrack = (row * 7 + (wx / tileW).toInt() * 3) % 11 == 0
            val baseC = if ((row + (wx / tileW).toInt()) % 2 == 0) Color(0xFF1A0C04) else Color(0xFF140A02)
            drawRect(baseC, Offset(wx, wy), Size(tileW - 1f, tileH - 1f))
            if (isLavaCrack) {
                drawRect(
                    Color(1f, 0.4f, 0f, 0.4f + medPulse * 0.4f),
                    Offset(wx + 2f, wy + tileH / 2f),
                    Size(tileW - 5f, 2f)
                )
            }
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L4: Magma river at floor
    val lavaY = H * 0.68f
    val lavaPath = Path().apply {
        moveTo(0f, lavaY)
        var x = 0f
        while (x <= W + 20f) {
            val y = lavaY + sin(x * 0.03f + scrollOffset * 0.08f) * 8f
            lineTo(x, y)
            x += 10f
        }
        lineTo(W, H)
        lineTo(0f, H)
        close()
    }
    drawPath(
        lavaPath,
        Brush.verticalGradient(listOf(Color(0xFFFF8800), Color(0xFFFF2200), Color(0xFF880000)))
    )

    drawCircle(
        Color(1f, 0.5f, 0f, 0.18f * torchFlicker),
        radius = W * 0.3f,
        center = Offset(W * 0.3f, lavaY)
    )
    drawCircle(
        Color(1f, 0.5f, 0f, 0.18f * torchFlicker),
        radius = W * 0.3f,
        center = Offset(W * 0.7f, lavaY)
    )

    // Floating embers
    val emberRng = java.util.Random(999)
    repeat(25) { i ->
        val ex = (emberRng.nextFloat() * W + i * 20f) % W
        val ey = ((lavaY + 50f) - ((scrollOffset * 1.5f + i * 35f) % (lavaY + 50f)))
        val eAlpha = 0.4f + sin(scrollOffset * 0.1f + i) * 0.4f
        drawCircle(
            Color(1f, 0.6f + (i % 4) * 0.1f, 0f, max(0f, eAlpha)),
            radius = 2.5f,
            center = Offset(ex, ey)
        )
    }

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xBB000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawSeaShrine(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Deep ocean atmosphere
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF0D334D), Color(0xFF082033), Color(0xFF030D1A))),
        size = size
    )
    drawRect(Color(0f, 0.3f, 0.5f, 0.12f), size = size)

    // L2: Submerged ruins & coral
    listOf(0.15f, 0.45f, 0.8f).forEach { xFrac ->
        val px = W * xFrac
        val pw = W * 0.08f
        drawRect(Color(0xFF0D1E2A), Offset(px, H * 0.25f), Size(pw, H * 0.5f))
        drawCircle(Color(0.8f, 0.3f, 0.3f, 0.6f), radius = 14f, center = Offset(px + 10f, H * 0.24f))
        drawCircle(Color(0.9f, 0.6f, 0.2f, 0.5f), radius = 18f, center = Offset(px + pw - 5f, H * 0.23f))
    }

    // L3: Temple walls with algae streaks
    val wallBottom = H * 0.72f
    val tileW = W / 20f
    val tileH = H * 0.055f
    var wy = H * 0.05f
    var row = 0
    while (wy < wallBottom) {
        val shift = if (row % 2 == 0) 0f else tileW / 2f
        var wx = shift - (scrollOffset * 0.1f % tileW)
        while (wx < W + tileW) {
            val c = if ((row + (wx / tileW).toInt()) % 2 == 0) Color(0xFF0D1E28) else Color(0xFF0A1820)
            drawRect(c, Offset(wx, wy), Size(tileW - 1f, tileH - 1f))
            wx += tileW
        }
        wy += tileH
        row++
    }

    repeat(6) { i ->
        val ax = W * (0.12f + i * 0.16f)
        drawLine(
            Color(0.1f, 0.5f, 0.2f, 0.25f),
            Offset(ax, H * 0.1f),
            Offset(ax + 10f, H * 0.65f),
            strokeWidth = 6f
        )
    }

    // L4: Caustic light rays
    repeat(6) { i ->
        val topX = W * (0.1f + i * 0.16f) + sin(scrollOffset * 0.02f + i) * 15f
        val rayPath = Path().apply {
            moveTo(topX, 0f)
            lineTo(topX + 25f, 0f)
            lineTo(topX + 80f, H)
            lineTo(topX - 20f, H)
            close()
        }
        val rayAlpha = 0.04f + sin(scrollOffset * 0.03f + i * 0.7f) * 0.03f
        drawPath(rayPath, Color(0.4f, 0.8f, 1f, max(0.01f, rayAlpha)))
    }

    // L5: Rising bubbles
    val bubbleRng = java.util.Random(4242)
    repeat(30) { i ->
        val bx = (bubbleRng.nextFloat() * W + sin(scrollOffset * 0.05f + i) * 12f)
        val by = (H - ((scrollOffset * 0.8f + i * 30f) % H))
        val bRadius = 3f + (i % 5) * 1.5f
        drawCircle(
            Color(0.5f, 0.8f, 1f, 0.4f),
            radius = bRadius,
            center = Offset(bx, by),
            style = Stroke(1.5f)
        )
        drawCircle(
            Color.White.copy(alpha = 0.6f),
            radius = 1f,
            center = Offset(bx - bRadius * 0.3f, by - bRadius * 0.3f)
        )
    }

    // L6: Bioluminescent floor
    val floorY = H * 0.72f
    drawRect(Color(0xFF06121C), Offset(0f, floorY), Size(W, H - floorY))
    drawRect(
        Color(0.1f, 0.6f, 0.7f, 0.15f + slowPulse * 0.1f),
        Offset(0f, floorY),
        Size(W, 8f)
    )

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xDD000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawEarthCave(scrollOffset: Float, torchFlicker: Float) {
    val W = size.width
    val H = size.height

    // L1: Clay/mud atmosphere
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF0E0A06), Color(0xFF18100A), Color(0xFF0B0704))),
        size = size
    )
    drawRect(Color(0.8f, 0.4f, 0.1f, torchFlicker * 0.02f), size = size)

    // L2: Earthy rock walls
    val wallBottom = H * 0.7f
    val tileW = W / 16f
    val tileH = H * 0.06f
    var wy = H * 0.1f
    var row = 0
    while (wy < wallBottom) {
        var wx = -(scrollOffset * 0.1f % tileW)
        while (wx < W + tileW) {
            val isMoss = (row * 5 + (wx / tileW).toInt()) % 7 == 0
            val c = if (isMoss) Color(0xFF1E2812) else if ((row + (wx / tileW).toInt()) % 2 == 0) Color(0xFF22160C) else Color(0xFF1A1008)
            drawRect(c, Offset(wx, wy), Size(tileW - 2f, tileH - 2f))
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L3: Jagged stalactites ceiling
    val ceilingPath = Path().apply {
        moveTo(0f, 0f)
        var x = 0f
        while (x <= W + 20f) {
            val len = 30f + sin(x * 0.05f) * 20f + (x.toInt() % 7) * 8f
            lineTo(x, len)
            x += 25f
        }
        lineTo(W, 0f)
        close()
    }
    drawPath(ceilingPath, Color(0xFF120B05))

    // Jagged stalagmites floor
    val floorY = H * 0.72f
    val floorPath = Path().apply {
        moveTo(0f, H)
        var x = 0f
        while (x <= W + 20f) {
            val height = 20f + cos(x * 0.04f) * 15f + (x.toInt() % 5) * 10f
            lineTo(x, floorY - height)
            x += 30f
        }
        lineTo(W, H)
        close()
    }
    drawPath(floorPath, Color(0xFF120B05))

    // Water drips
    repeat(4) { i ->
        val dx = W * (0.2f + i * 0.22f)
        val dripY = ((scrollOffset * 1.2f + i * 80f) % (floorY - 40f)) + 30f
        drawCircle(Color(0.4f, 0.7f, 1f, 0.6f), radius = 3f, center = Offset(dx, dripY))
        if (dripY > floorY - 50f) {
            drawCircle(Color(0.4f, 0.7f, 1f, 0.3f), radius = 10f, center = Offset(dx, floorY - 10f), style = Stroke(1.5f))
        }
    }

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xDD000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawCrystalTower(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Crystal atmosphere
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF0D1A33), Color(0xFF1A3359), Color(0xFF0D1A33))),
        size = size
    )
    drawCircle(
        Brush.radialGradient(
            listOf(Color(0.6f, 0.9f, 1f, 0.2f), Color.Transparent),
            center = Offset(W / 2f, H * 0.4f),
            radius = W * 0.45f
        ),
        radius = W * 0.45f,
        center = Offset(W / 2f, H * 0.4f)
    )

    // L2: Giant crystal columns
    listOf(0.1f, 0.3f, 0.5f, 0.7f, 0.9f).forEach { xFrac ->
        val cx = W * xFrac
        val cw = W * 0.07f
        val topY = H * 0.1f
        val botY = H * 0.72f
        val leftPath = Path().apply {
            moveTo(cx, topY + 20f)
            lineTo(cx + cw * 0.5f, topY)
            lineTo(cx + cw * 0.5f, botY)
            lineTo(cx, botY + 20f)
            close()
        }
        drawPath(leftPath, Color(0xFF0A2030))
        val rightPath = Path().apply {
            moveTo(cx + cw * 0.5f, topY)
            lineTo(cx + cw, topY + 20f)
            lineTo(cx + cw, botY + 20f)
            lineTo(cx + cw * 0.5f, botY)
            close()
        }
        drawPath(rightPath, Color(0xFF1A5080))
        drawCircle(Color(0.6f, 0.9f, 1f, 0.2f + slowPulse * 0.2f), radius = 15f, center = Offset(cx + cw * 0.5f, topY + 10f))
    }

    // L3: Mirror tile floor
    val floorY = H * 0.72f
    drawRect(Color(0xFF081826), Offset(0f, floorY), Size(W, H - floorY))
    val tileW = W / 16f
    var fx = -(scrollOffset * 0.2f % tileW)
    while (fx < W + tileW) {
        drawLine(Color(0.2f, 0.6f, 0.9f, 0.25f), Offset(fx, floorY), Offset(fx, H), strokeWidth = 1f)
        fx += tileW
    }

    // L4: Light sweep band
    val sweepX = ((scrollOffset * 2f) % (W + 400f)) - 200f
    val sweepPath = Path().apply {
        moveTo(sweepX, 0f)
        lineTo(sweepX + 150f, 0f)
        lineTo(sweepX - 50f, H)
        lineTo(sweepX - 200f, H)
        close()
    }
    drawPath(sweepPath, Color(1f, 1f, 1f, 0.05f))

    // L5: Sparkles
    val sparkRng = java.util.Random(777)
    repeat(30) { i ->
        val sx = sparkRng.nextFloat() * W
        val sy = sparkRng.nextFloat() * H * 0.7f
        val sAlpha = max(0f, sin(scrollOffset * 0.08f + i) * 0.8f)
        drawLine(Color(0.8f, 1f, 1f, sAlpha), Offset(sx - 6f, sy), Offset(sx + 6f, sy), strokeWidth = 2f)
        drawLine(Color(0.8f, 1f, 1f, sAlpha), Offset(sx, sy - 6f), Offset(sx, sy + 6f), strokeWidth = 2f)
    }

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xCC000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawMysidianTower(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Cosmic indigo
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF0A081D), Color(0xFF120F33), Color(0xFF0A081D))),
        size = size
    )

    // L2: Distant bookshelves
    listOf(0.18f, 0.38f, 0.58f).forEach { yFrac ->
        val sy = H * yFrac
        drawRect(Color(0xFF2A1810), Offset(0f, sy), Size(W, 12f))
        var bx = 10f
        while (bx < W - 10f) {
            val bookH = 20f + (bx.toInt() % 5) * 4f
            val bookC = when ((bx.toInt() / 15) % 4) {
                0 -> Color(0xFF883333)
                1 -> Color(0xFF336688)
                2 -> Color(0xFF338855)
                else -> Color(0xFF888833)
            }
            drawRect(bookC, Offset(bx, sy - bookH), Size(12f, bookH))
            bx += 14f
        }
    }

    // L3: Arcane stone walls
    val floorY = H * 0.72f
    drawRect(Color(0xFF100C26), Offset(0f, floorY), Size(W, H - floorY))

    // L4: Floating magical scrolls
    repeat(3) { i ->
        val sx = W * (0.25f + i * 0.28f) + sin(scrollOffset * 0.03f + i) * 15f
        val sy = H * (0.25f + (i % 2) * 0.2f) + cos(scrollOffset * 0.02f + i) * 10f
        drawRect(Color(0xFFE8D8B0), Offset(sx - 20f, sy - 6f), Size(40f, 12f))
        drawCircle(Color(0xFFFFD700).copy(alpha = 0.3f + slowPulse * 0.3f), radius = 25f, center = Offset(sx, sy))
    }

    // L5: Glowing golden wall runes
    listOf(0.15f, 0.5f, 0.85f).forEach { xFrac ->
        val rx = W * xFrac
        val ry = H * 0.45f
        drawCircle(Color(0xFFFFD700).copy(alpha = 0.2f + slowPulse * 0.2f), radius = 35f, center = Offset(rx, ry), style = Stroke(2f))
        drawCircle(Color(0xFFFFD700).copy(alpha = 0.15f), radius = 20f, center = Offset(rx, ry), style = Stroke(1.5f))
    }

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xDD000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawPandaemonium(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Venomous green atmosphere
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF040D08), Color(0xFF0A1A10), Color(0xFF040D08))),
        size = size
    )

    // L2: Spinal bone pillars
    listOf(0.12f, 0.88f).forEach { xFrac ->
        val px = W * xFrac
        repeat(8) { i ->
            val py = H * (0.1f + i * 0.08f)
            drawRect(Color(0xFFD0C8B8), Offset(px - 15f, py), Size(30f, 22f))
            drawRect(Color(0xFF8A8272), Offset(px - 12f, py + 2f), Size(24f, 18f))
        }
    }

    // L3: Dark jadeite walls
    val wallBottom = H * 0.72f
    val tileW = W / 18f
    val tileH = H * 0.055f
    var wy = H * 0.05f
    var row = 0
    while (wy < wallBottom) {
        var wx = -(scrollOffset * 0.1f % tileW)
        while (wx < W + tileW) {
            val c = if ((row + (wx / tileW).toInt()) % 2 == 0) Color(0xFF0B1A12) else Color(0xFF07120C)
            drawRect(c, Offset(wx, wy), Size(tileW - 1f, tileH - 1f))
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L4: Jade liquid pools on floor/wall
    drawCircle(Color(0f, 1f, 0.4f, 0.18f + slowPulse * 0.1f), radius = W * 0.25f, center = Offset(W * 0.3f, wallBottom))
    drawCircle(Color(0f, 1f, 0.4f, 0.18f + slowPulse * 0.1f), radius = W * 0.25f, center = Offset(W * 0.7f, wallBottom))

    // L5: Soul spirits
    repeat(4) { i ->
        val sx = W * (0.2f + i * 0.2f) + sin(scrollOffset * 0.04f + i) * 20f
        val sy = H * (0.2f + (i % 3) * 0.18f) - (scrollOffset * 0.3f % (H * 0.5f))
        val drawY = if (sy < H * 0.1f) sy + H * 0.5f else sy
        drawCircle(Color(0.2f, 1f, 0.6f, 0.3f), radius = 12f, center = Offset(sx, drawY))
    }

    val floorY = H * 0.72f
    drawRect(Color(0xFF06120A), Offset(0f, floorY), Size(W, H - floorY))

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xDD000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawMountOrdeals(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Dusk orange sky
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF1A0A10), Color(0xFF2D1420), Color(0xFF401F28))),
        size = size
    )

    // L2: Mountain ridges (3 parallax layers)
    val layerColors = listOf(Color(0xFF220F18), Color(0xFF1A0B12), Color(0xFF12070C))
    val parallaxSpeeds = listOf(0.05f, 0.15f, 0.3f)
    listOf(0.45f, 0.55f, 0.68f).forEachIndexed { index, mtnYFrac ->
        val mtnY = H * mtnYFrac
        val speed = parallaxSpeeds[index]
        val path = Path().apply {
            moveTo(0f, H)
            var x = 0f
            while (x <= W + 40f) {
                val offset = (x + scrollOffset * speed) * 0.02f
                val y = mtnY + sin(offset) * 40f + cos(offset * 0.5f) * 20f
                lineTo(x, y)
                x += 20f
            }
            lineTo(W, H)
            close()
        }
        drawPath(path, layerColors[index])
    }

    // L3: Sacred light beams
    repeat(3) { i ->
        val lx = W * (0.25f + i * 0.28f)
        val beamPath = Path().apply {
            moveTo(lx - 15f, 0f)
            lineTo(lx + 25f, 0f)
            lineTo(lx + 60f, H)
            lineTo(lx - 40f, H)
            close()
        }
        drawPath(beamPath, Color(1f, 0.85f, 0.6f, 0.08f + slowPulse * 0.04f))
    }

    // Petals / holy sparks
    val petalRng = java.util.Random(123)
    repeat(20) { i ->
        val px = (petalRng.nextFloat() * W - scrollOffset * 0.8f + i * 30f) % W
        val drawX = if (px < 0) px + W else px
        val py = (petalRng.nextFloat() * H + scrollOffset * 0.4f + i * 20f) % H
        drawCircle(Color(1f, 0.8f, 0.9f, 0.5f), radius = 3f, center = Offset(drawX, py))
    }

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xBB000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawBaronCastle(scrollOffset: Float, torchFlicker: Float) {
    val W = size.width
    val H = size.height

    // L1: Dark navy iron atmosphere
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF060A14), Color(0xFF0D1424), Color(0xFF060A14))),
        size = size
    )
    drawCircle(Color(1f, 0.4f, 0.1f, torchFlicker * 0.04f), radius = W * 0.25f, center = Offset(W / 2f, H * 0.35f))

    // L2: Steel riveted panels
    val wallBottom = H * 0.72f
    val panelW = W / 8f
    val panelH = H * 0.15f
    var py = H * 0.08f
    var row = 0
    while (py < wallBottom) {
        var px = -(scrollOffset * 0.15f % panelW)
        while (px < W + panelW) {
            drawRect(Color(0xFF141C2B), Offset(px, py), Size(panelW - 2f, panelH - 2f))
            drawRect(Color(0xFF0A0F1A), Offset(px + 4f, py + 4f), Size(panelW - 10f, panelH - 10f), style = Stroke(1.5f))
            listOf(
                Offset(px + 6f, py + 6f),
                Offset(px + panelW - 8f, py + 6f),
                Offset(px + 6f, py + panelH - 8f),
                Offset(px + panelW - 8f, py + panelH - 8f)
            ).forEach { rivetPos ->
                drawCircle(Color(0xFF2A3850), radius = 2.5f, center = rivetPos)
            }
            px += panelW
        }
        py += panelH
        row++
    }

    // L3: Crimson drapes / banners
    listOf(0.2f, 0.8f).forEach { xFrac ->
        val bx = W * xFrac
        drawRect(Color(0xFF8B0000), Offset(bx - 20f, H * 0.1f), Size(40f, H * 0.4f))
        drawRect(Color(0xFFFFD700), Offset(bx - 20f, H * 0.1f), Size(40f, H * 0.4f), style = Stroke(2f))
    }

    // L4: Floor
    val floorY = H * 0.72f
    drawRect(Color(0xFF0A0E18), Offset(0f, floorY), Size(W, H - floorY))

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xDD000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawAncientCastle(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Sand gold dusk
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF1A1208), Color(0xFF281B0C), Color(0xFF1A1208))),
        size = size
    )
    drawCircle(Color(1f, 0.8f, 0.4f, 0.04f + slowPulse * 0.04f), radius = W * 0.4f, center = Offset(W * 0.8f, H * 0.2f))

    // L2: Sand dune background
    val dunePath = Path().apply {
        moveTo(0f, H * 0.65f)
        quadraticTo(W * 0.3f, H * 0.5f, W * 0.6f, H * 0.6f)
        quadraticTo(W * 0.8f, H * 0.68f, W, H * 0.58f)
        lineTo(W, H)
        lineTo(0f, H)
        close()
    }
    drawPath(dunePath, Color(0xFF2E1F0E))

    // L3: Half-buried sandstone walls & pillars
    listOf(0.2f, 0.5f, 0.8f).forEach { xFrac ->
        val px = W * xFrac
        val pw = W * 0.08f
        drawRect(Color(0xFF3A2A18), Offset(px, H * 0.25f), Size(pw, H * 0.4f))
        drawLine(Color(0xFF1E140A), Offset(px + 10f, H * 0.3f), Offset(px + pw - 5f, H * 0.5f), strokeWidth = 2f)
    }

    // L4: Sand drifts on floor
    val floorY = H * 0.68f
    val sandDriftPath = Path().apply {
        moveTo(0f, floorY)
        var x = 0f
        while (x <= W + 20f) {
            val y = floorY + sin(x * 0.02f + scrollOffset * 0.05f) * 12f
            lineTo(x, y)
            x += 15f
        }
        lineTo(W, H)
        lineTo(0f, H)
        close()
    }
    drawPath(sandDriftPath, Color(0xFF4A361E))

    // Sand particles blowing
    val sandRng = java.util.Random(555)
    repeat(20) { i ->
        val sx = (sandRng.nextFloat() * W - scrollOffset * 2f + i * 25f) % W
        val drawX = if (sx < 0) sx + W else sx
        val sy = H * 0.3f + sandRng.nextFloat() * H * 0.4f
        drawRect(Color(0xFFD4A359).copy(alpha = 0.5f), Offset(drawX, sy), Size(3f, 2f))
    }

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xCC000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawNarsheMines(scrollOffset: Float, torchFlicker: Float) {
    val W = size.width
    val H = size.height

    // L1: Cold mine atmosphere
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF060810), Color(0xFF0A0E14), Color(0xFF060810))),
        size = size
    )
    drawRect(Color(0f, 0.1f, 0.2f, 0.08f), size = size)

    // L2: Central mine shaft depth
    val tunnelW = W * 0.35f
    val tunnelH = H * 0.35f
    val tunnelLeft = W / 2f - tunnelW / 2f
    val tunnelTop = H * 0.25f
    drawRect(Color(0xFF030408), Offset(tunnelLeft, tunnelTop), Size(tunnelW, tunnelH))
    drawCircle(
        Color(0.2f, 0.5f, 0.8f, 0.15f),
        radius = tunnelW * 0.4f,
        center = Offset(W / 2f, tunnelTop + tunnelH / 2f)
    )

    // L3: Rock walls with coal veins
    val wallBottom = H * 0.7f
    val tileW = W / 16f
    val tileH = H * 0.06f
    var wy = H * 0.05f
    var row = 0
    while (wy < wallBottom) {
        var wx = -(scrollOffset * 0.1f % tileW)
        while (wx < W + tileW) {
            val c = if ((row + (wx / tileW).toInt()) % 2 == 0) Color(0xFF1A1E24) else Color(0xFF141820)
            drawRect(c, Offset(wx, wy), Size(tileW - 2f, tileH - 2f))
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L4: Wooden support trusses
    val beamY1 = H * 0.12f
    val beamY2 = H * 0.7f
    drawRect(Color(0xFF3A2510), Offset(0f, beamY1), Size(W, 16f))
    drawRect(Color(0xFF5A3A18), Offset(0f, beamY1), Size(W, 3f))
    drawRect(Color(0xFF3A2510), Offset(0f, beamY2), Size(W, 16f))

    listOf(0.15f, 0.35f, 0.65f, 0.85f).forEach { xFrac ->
        val postX = W * xFrac
        drawRect(Color(0xFF3A2510), Offset(postX - 8f, beamY1), Size(16f, beamY2 - beamY1))
        drawRect(Color(0xFF5A3A18), Offset(postX - 8f, beamY1), Size(3f, beamY2 - beamY1))
    }

    // L5: Hanging swinging lantern
    val swingAngle = sin(scrollOffset * 0.05f) * 0.1f
    val lanternX = W / 2f + sin(swingAngle) * 40f
    val lanternY = H * 0.28f
    drawLine(Color(0xFF666666), Offset(W / 2f, beamY1 + 16f), Offset(lanternX, lanternY), strokeWidth = 2f)
    drawRect(Color(0xFF2A1A04), Offset(lanternX - 10f, lanternY), Size(20f, 26f))
    drawCircle(
        Color(1f, 0.7f, 0.2f, torchFlicker * 0.3f),
        radius = W * 0.15f,
        center = Offset(lanternX, lanternY + 13f)
    )

    // Icicles ceiling
    listOf(0.1f, 0.25f, 0.4f, 0.6f, 0.75f, 0.9f).forEach { xFrac ->
        val ix = W * xFrac
        val iciclePath = Path().apply {
            moveTo(ix - 6f, 0f)
            lineTo(ix + 6f, 0f)
            lineTo(ix, 40f + (xFrac * 100f % 20f))
            close()
        }
        drawPath(iciclePath, Color(0.6f, 0.8f, 1f, 0.7f))
    }

    val floorY = H * 0.7f
    drawRect(Color(0xFF0C1016), Offset(0f, floorY), Size(W, H - floorY))

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xDD000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawMagitekFactory(scrollOffset: Float, fastTick: Float) {
    val W = size.width
    val H = size.height

    // L1: Factory dark atmosphere
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF060808), Color(0xFF0A0E0C), Color(0xFF060808))),
        size = size
    )
    drawRect(Color(0f, 0.3f, 0.1f, 0.04f), size = size)

    // L2: Horizontal pipe bundles
    listOf(0.18f, 0.32f, 0.46f).forEach { yFrac ->
        val py = H * yFrac
        drawRect(Color(0xFF1A2020), Offset(0f, py), Size(W, 20f))
        drawRect(Color(0xFF3A4040), Offset(0f, py + 2f), Size(W, 4f))
    }

    // L3: Metal panel walls
    val wallBottom = H * 0.72f
    val panelW = W / 10f
    val panelH = H * 0.12f
    var py = H * 0.05f
    var row = 0
    while (py < wallBottom) {
        var px = -(scrollOffset * 0.2f % panelW)
        while (px < W + panelW) {
            drawRect(Color(0xFF0D1210), Offset(px, py), Size(panelW - 2f, panelH - 2f))
            if ((row + (px / panelW).toInt()) % 3 == 0) {
                drawRect(Color(0f, 0.7f, 0.3f, 0.6f), Offset(px + panelW / 2f - 3f, py), Size(6f, panelH))
                drawRect(Color(0f, 1f, 0.4f, 0.15f), Offset(px + panelW / 2f - 8f, py), Size(16f, panelH))
            }
            px += panelW
        }
        py += panelH
        row++
    }

    // L4: Steam vents
    listOf(0.2f, 0.5f, 0.8f).forEach { xFrac ->
        val vx = W * xFrac
        val vy = H * 0.58f
        drawRect(Color(0xFF222222), Offset(vx - 15f, vy), Size(30f, 12f))
        drawCircle(
            Color(0.8f, 0.9f, 0.8f, 0.15f),
            radius = 25f + fastTick * 15f,
            center = Offset(vx, vy - 20f - fastTick * 20f)
        )
    }

    // L5: Warning lights
    listOf(0.15f, 0.5f, 0.85f).forEach { xFrac ->
        val lx = W * xFrac
        val ly = H * 0.08f
        val isOn = (fastTick * 4f).toInt() % 2 == 0
        val lightC = if (isOn) Color(0.9f, 0.1f, 0f, 0.9f) else Color(0.3f, 0f, 0f, 0.5f)
        drawCircle(lightC, radius = 6f, center = Offset(lx, ly))
        if (isOn) {
            drawCircle(Color(1f, 0.2f, 0f, 0.25f), radius = 20f, center = Offset(lx, ly))
        }
    }

    val floorY = H * 0.72f
    drawRect(Color(0xFF080C0A), Offset(0f, floorY), Size(W, H - floorY))

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xDD000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawKefkaTower(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Hue cycling chaos background
    val bgR = sin(slowPulse * 3.14159f) * 0.2f + 0.12f
    val bgB = cos(slowPulse * 3.14159f) * 0.2f + 0.12f
    drawRect(Color(bgR, 0.04f, bgB, 1f), size = size)

    // L2: Mismatched structure sections side-by-side
    val chunkW = W / 4f
    drawRect(Color(0xFF1C1628), Offset(0f, H * 0.1f), Size(chunkW, H * 0.62f))
    drawRect(Color(0xFF0D1210), Offset(chunkW, H * 0.1f), Size(chunkW, H * 0.62f))
    drawRect(Color(0xFF0D3050), Offset(chunkW * 2f, H * 0.1f), Size(chunkW, H * 0.62f))
    drawRect(Color(0xFF1A1228), Offset(chunkW * 3f, H * 0.1f), Size(chunkW, H * 0.62f))

    // L3: Floating rotating debris
    repeat(20) { i ->
        val dx = (W * (0.1f + i * 0.045f) + sin(scrollOffset * 0.02f + i) * 30f) % W
        val dy = (H * 0.15f + (i * 37f) % (H * 0.5f))
        val rotAngle = scrollOffset * 0.1f + i * 30f
        val debrisC = when (i % 4) {
            0 -> Color(0xFF884488)
            1 -> Color(0xFF448888)
            2 -> Color(0xFF888844)
            else -> Color(0xFF448844)
        }
        rotate(degrees = rotAngle, pivot = Offset(dx, dy)) {
            drawRect(debrisC, Offset(dx - 12f, dy - 8f), Size(24f, 16f))
        }
    }

    // L4: God aura gold shimmer
    val sweepX = (scrollOffset * 3f) % (W + 300f) - 150f
    drawRect(
        Color(1f, 0.85f, 0.2f, 0.08f),
        Offset(sweepX, 0f),
        Size(120f, H)
    )

    val floorY = H * 0.72f
    drawRect(Color(0xFF100814), Offset(0f, floorY), Size(W, H - floorY))

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xDD000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawFloatingContinent(scrollOffset: Float) {
    val W = size.width
    val H = size.height

    // L1: High altitude sky
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF0A1A30), Color(0xFF102B4C), Color(0xFF1C4066))),
        size = size
    )

    // L2: Distant floating islands
    listOf(0.2f, 0.6f, 0.85f).forEach { xFrac ->
        val ix = W * xFrac
        val iy = H * 0.25f
        drawCircle(Color(0xFF0D1B2A), radius = 35f, center = Offset(ix, iy))
        drawRect(Color(0xFF0D1B2A), Offset(ix - 35f, iy), Size(70f, 15f))
    }

    // L3: Jagged crag floor
    val floorY = H * 0.68f
    val cragPath = Path().apply {
        moveTo(0f, floorY)
        var x = 0f
        while (x <= W + 20f) {
            val y = floorY + sin(x * 0.04f) * 15f + cos(x * 0.02f) * 10f
            lineTo(x, y)
            x += 20f
        }
        lineTo(W, H)
        lineTo(0f, H)
        close()
    }
    drawPath(cragPath, Color(0xFF182433))

    // L4: Cloud sea beneath floor
    repeat(8) { i ->
        val cx = ((i * W * 0.18f) - (scrollOffset * 0.6f % W))
        val drawX = if (cx < -100f) cx + W + 200f else cx
        drawCircle(
            Color(0.8f, 0.9f, 1f, 0.25f),
            radius = 60f,
            center = Offset(drawX, H * 0.85f)
        )
    }

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xBB000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawMidgarSewers(scrollOffset: Float, medPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Slimy dark atmosphere
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF040A06), Color(0xFF0A140D), Color(0xFF040A06))),
        size = size
    )

    // L2: Drainage tunnel perspective arch overhead
    drawCircle(
        Color(0xFF0D1E14),
        radius = W * 0.65f,
        center = Offset(W / 2f, H * 0.2f),
        style = Stroke(40f)
    )

    // L3: Slimy brick walls
    val wallBottom = H * 0.7f
    val tileW = W / 18f
    val tileH = H * 0.055f
    var wy = H * 0.1f
    var row = 0
    while (wy < wallBottom) {
        var wx = -(scrollOffset * 0.1f % tileW)
        while (wx < W + tileW) {
            val isSlime = (row + (wx / tileW).toInt()) % 5 == 0
            val c = if (isSlime) Color(0xFF1A2E1E) else Color(0xFF101A12)
            drawRect(c, Offset(wx, wy), Size(tileW - 1f, tileH - 1f))
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L4: Sludge water channel at floor
    val sludgeY = H * 0.7f
    drawRect(Color(0xFF0A2210), Offset(0f, sludgeY), Size(W, H - sludgeY))
    repeat(5) { i ->
        val ry = sludgeY + 15f + i * 20f
        val rx = (scrollOffset * (0.8f + i * 0.2f)) % W
        drawLine(Color(0.2f, 0.8f, 0.3f, 0.3f + medPulse * 0.2f), Offset(rx, ry), Offset(min(W, rx + 100f), ry), strokeWidth = 3f)
    }

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xDD000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawShinraBuilding(scrollOffset: Float, fastTick: Float) {
    val W = size.width
    val H = size.height

    // L1: Night city corporate black
    drawRect(Color(0xFF020408), size = size)
    drawRect(Color(0f, 0.2f, 0.4f, 0.06f), size = size)

    // L2: City skyline silhouettes in background
    val skyRng = java.util.Random(101)
    var bx = 0f
    while (bx < W) {
        val bw = 40f + skyRng.nextFloat() * 50f
        val bh = H * 0.25f + skyRng.nextFloat() * H * 0.35f
        drawRect(Color(0xFF050A14), Offset(bx, H * 0.7f - bh), Size(bw, bh))
        repeat(6) { wi ->
            drawRect(
                Color(1f, 0.85f, 0.3f, 0.25f),
                Offset(bx + 8f + (wi % 2) * 16f, H * 0.7f - bh + 15f + (wi / 2) * 20f),
                Size(6f, 6f)
            )
        }
        bx += bw + 5f
    }

    // L3: Steel/glass grid panels
    val wallBottom = H * 0.72f
    val panelW = W / 6f
    val panelH = H * 0.18f
    var py = H * 0.05f
    while (py < wallBottom) {
        var px = -(scrollOffset * 0.15f % panelW)
        while (px < W + panelW) {
            drawRect(Color(0xFF08101F), Offset(px, py), Size(panelW - 2f, panelH - 2f))
            drawRect(Color(0f, 0.4f, 0.8f, 0.3f), Offset(px, py), Size(panelW - 2f, panelH - 2f), style = Stroke(1.5f))
            px += panelW
        }
        py += panelH
    }

    // L4: Blue neon trim light strips
    listOf(0.2f, 0.4f, 0.6f).forEach { yFrac ->
        val ny = H * yFrac
        val isFlicker = (fastTick * 10f).toInt() % 7 == 0
        val alpha = if (isFlicker) 0.3f else 0.8f
        drawRect(Color(0f, 0.6f, 1f, alpha), Offset(0f, ny), Size(W, 3f))
        drawRect(Color(0f, 0.4f, 0.8f, alpha * 0.3f), Offset(0f, ny - 6f), Size(W, 15f))
    }

    val floorY = H * 0.72f
    drawRect(Color(0xFF040812), Offset(0f, floorY), Size(W, H - floorY))

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xDD000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawNorthernCrater(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Celestial abyss
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF030814), Color(0xFF081026), Color(0xFF030814))),
        size = size
    )

    // L2: Swirling emerald green Lifestream vortexes
    repeat(4) { i ->
        val vortexPath = Path().apply {
            val startX = W * (0.2f + i * 0.2f)
            moveTo(startX, H)
            cubicTo(
                startX + 60f * sin(scrollOffset * 0.03f + i), H * 0.6f,
                startX - 60f * cos(scrollOffset * 0.03f + i), H * 0.3f,
                startX + 20f, 0f
            )
        }
        drawPath(vortexPath, Color(0f, 1f, 0.5f, 0.25f + slowPulse * 0.1f), style = Stroke(12f))
    }

    // L3: Crystalline bone ribs framing sides
    listOf(0.08f, 0.92f).forEach { xFrac ->
        val rx = W * xFrac
        repeat(6) { i ->
            val ry = H * (0.15f + i * 0.1f)
            drawCircle(Color(0xFFD0D8E0), radius = 18f, center = Offset(rx, ry))
        }
    }

    // L4: Bottomless pit glow
    val floorY = H * 0.72f
    drawRect(Color(0xFF040A18), Offset(0f, floorY), Size(W, H - floorY))
    drawCircle(
        Color(0f, 1f, 0.6f, 0.2f + slowPulse * 0.15f),
        radius = W * 0.4f,
        center = Offset(W / 2f, H)
    )

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xDD000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawGoldenSaucer(scrollOffset: Float, fastTick: Float) {
    val W = size.width
    val H = size.height

    // L1: Neon casino background
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF12041C), Color(0xFF240838), Color(0xFF12041C))),
        size = size
    )

    // L2: Chaser light border tracks
    val step = (fastTick * 8f).toInt()
    val colors = listOf(Color.Red, Color.Yellow, Color.Cyan, Color.Magenta, Color.Green)
    val dotCount = 20
    repeat(dotCount) { i ->
        val x = W * (i.toFloat() / dotCount)
        val color = colors[(i + step) % colors.size]
        drawCircle(color, radius = 5f, center = Offset(x, 15f))
        drawCircle(color, radius = 5f, center = Offset(x, H * 0.7f))
    }

    // L3: Golden matrix grid wall
    val wallBottom = H * 0.7f
    val tileW = W / 12f
    val tileH = H * 0.08f
    var wy = H * 0.08f
    var row = 0
    while (wy < wallBottom) {
        var wx = -(scrollOffset * 0.2f % tileW)
        while (wx < W + tileW) {
            drawRect(Color(0xFF2D103A), Offset(wx, wy), Size(tileW - 2f, tileH - 2f))
            drawCircle(Color(1f, 0.85f, 0.2f, 0.4f), radius = 3f, center = Offset(wx + tileW / 2f, wy + tileH / 2f))
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L4: Floating confetti
    val confRng = java.util.Random(888)
    repeat(30) { i ->
        val cx = (confRng.nextFloat() * W + i * 20f) % W
        val cy = (confRng.nextFloat() * H + scrollOffset * 1.5f + i * 30f) % H
        val color = colors[i % colors.size]
        drawRect(color.copy(alpha = 0.7f), Offset(cx, cy), Size(5f, 5f))
    }

    val floorY = H * 0.7f
    drawRect(Color(0xFF180424), Offset(0f, floorY), Size(W, H - floorY))

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xCC000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawBevelleTemple(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Sacred light atmosphere
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF0D121F), Color(0xFF1A2438), Color(0xFF0D121F))),
        size = size
    )
    drawCircle(
        Color(0.5f, 0.7f, 1f, 0.08f),
        radius = W * 0.5f,
        center = Offset(W / 2f, 0f)
    )

    // L2: Pristine marble columns
    listOf(0.12f, 0.38f, 0.62f, 0.88f).forEach { xFrac ->
        val cx = W * xFrac
        val cw = W * 0.07f
        drawRect(Color(0xFF263248), Offset(cx, H * 0.1f), Size(cw, H * 0.62f))
        drawRect(Color(0xFF384868), Offset(cx + 2f, H * 0.1f), Size(cw * 0.4f, H * 0.62f))
        drawLine(Color(0xFF1A2232).copy(alpha = 0.4f), Offset(cx + 4f, H * 0.15f), Offset(cx + cw - 4f, H * 0.55f), strokeWidth = 1.5f)
        drawRect(Color(0xFF4A5C80), Offset(cx - 6f, H * 0.1f), Size(cw + 12f, 12f))
    }

    // L3: Animated cyan energy ribbon channels
    listOf(0.25f, 0.45f, 0.65f).forEachIndexed { i, yFrac ->
        val ry = H * yFrac
        drawRect(Color(0xFF060812), Offset(0f, ry - 4f), Size(W, 8f))
        val wavePath = Path().apply {
            moveTo(0f, ry)
            var x = 0f
            while (x <= W + 20f) {
                val y = ry + sin(x * 0.03f + scrollOffset * 0.1f + i) * 6f
                lineTo(x, y)
                x += 10f
            }
        }
        drawPath(wavePath, Color(0.1f, 0.8f, 1f, 0.8f), style = Stroke(3f))
        drawRect(Color(0f, 0.5f, 1f, 0.12f), Offset(0f, ry - 10f), Size(W, 20f))
    }

    // L4: Rotating Yevon gear symbols
    listOf(0.25f, 0.75f).forEach { xFrac ->
        val gx = W * xFrac
        val gy = H * 0.15f
        drawCircle(Color(0.3f, 0.6f, 0.8f, 0.3f), radius = 25f, center = Offset(gx, gy), style = Stroke(2f))
        val rot = scrollOffset * 0.05f
        repeat(8) { i ->
            val angle = rot + i * (PI.toFloat() / 4f)
            drawLine(
                Color(0.3f, 0.6f, 0.8f, 0.3f),
                Offset(gx, gy),
                Offset(gx + cos(angle) * 25f, gy + sin(angle) * 25f),
                strokeWidth = 1.5f
            )
        }
    }

    val floorY = H * 0.72f
    drawRect(Color(0xFF0B101C), Offset(0f, floorY), Size(W, H - floorY))

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xDD000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawOmegaRuins(scrollOffset: Float, fastTick: Float) {
    val W = size.width
    val H = size.height

    // L1: Cyber glitch atmosphere
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF02050E), Color(0xFF060B1C), Color(0xFF02050E))),
        size = size
    )

    // L2: Matrix code columns
    val colRng = java.util.Random(321)
    repeat(12) { i ->
        val cx = W * (i / 12f)
        val cy = (scrollOffset * (1f + colRng.nextFloat()) + i * 40f) % H
        drawRect(Color(0f, 1f, 0.5f, 0.3f), Offset(cx, cy), Size(6f, 20f))
    }

    // L3: Fractured digital wall tiles with random displacement
    val wallBottom = H * 0.7f
    val tileW = W / 16f
    val tileH = H * 0.06f
    var wy = H * 0.08f
    var row = 0
    while (wy < wallBottom) {
        var wx = -(scrollOffset * 0.15f % tileW)
        while (wx < W + tileW) {
            val isDisplaced = (row * 3 + (wx / tileW).toInt() + (fastTick * 5f).toInt()) % 13 == 0
            val offsetX = if (isDisplaced) 15f else 0f
            drawRect(Color(0xFF0A1428), Offset(wx + offsetX, wy), Size(tileW - 2f, tileH - 2f))
            drawRect(Color(0f, 0.8f, 1f, 0.25f), Offset(wx + offsetX, wy), Size(tileW - 2f, tileH - 2f), style = Stroke(1f))
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L4: Scanning laser sweep
    val scanY = (scrollOffset * 2f) % H
    drawLine(Color(0f, 1f, 0.8f, 0.8f), Offset(0f, scanY), Offset(W, scanY), strokeWidth = 2f)
    drawRect(Color(0f, 1f, 0.8f, 0.1f), Offset(0f, scanY - 10f), Size(W, 20f))

    val floorY = H * 0.7f
    drawRect(Color(0xFF040814), Offset(0f, floorY), Size(W, H - floorY))

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xDD000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawSinInterior(scrollOffset: Float, slowPulse: Float) {
    val W = size.width
    val H = size.height

    // L1: Organic maroon void
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF140408), Color(0xFF240810), Color(0xFF140408))),
        size = size
    )

    // L2: Heartbeat pulse & organic ribcage pillars
    val pulseScale = 1f + sin(slowPulse * PI.toFloat() * 2f) * 0.05f
    listOf(0.1f, 0.9f).forEach { xFrac ->
        val rx = W * xFrac
        repeat(5) { i ->
            val ry = H * (0.15f + i * 0.12f)
            val ribW = 40f * pulseScale
            drawOval(
                Color(0xFF3D121B),
                topLeft = Offset(rx - ribW / 2f, ry - 15f),
                size = Size(ribW, 30f)
            )
        }
    }

    // L3: Pulsing nerve paths
    repeat(3) { i ->
        val nx = W * (0.25f + i * 0.25f)
        val nervePath = Path().apply {
            moveTo(nx, 0f)
            quadraticTo(nx + 30f * sin(slowPulse * 3f + scrollOffset * 0.02f + i), H * 0.5f, nx, H)
        }
        drawPath(nervePath, Color(0.9f, 0.1f, 0.3f, 0.4f + slowPulse * 0.3f), style = Stroke(3f))
    }

    val floorY = H * 0.72f
    drawRect(Color(0xFF0F0206), Offset(0f, floorY), Size(W, H - floorY))

    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xEE000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.75f
        ),
        size = size
    )
}

private fun DrawScope.drawGenericDungeon(scrollOffset: Float, torchFlicker: Float) {
    val W = size.width
    val H = size.height

    // === LAYER 1: Far background — deep dark gradient ===
    drawRect(Color(0xFF0A0614), size = size)

    // === LAYER 2: Distant stone arch ===
    val archColor = Color(0xFF12091E)
    drawRect(archColor, Offset(W * 0.08f, H * 0.1f), Size(W * 0.06f, H * 0.85f))
    drawRect(Color(0xFF1A0F2A), Offset(W * 0.085f, H * 0.1f), Size(W * 0.04f, H * 0.83f))
    drawRect(archColor, Offset(W * 0.86f, H * 0.1f), Size(W * 0.06f, H * 0.85f))
    drawRect(Color(0xFF1A0F2A), Offset(W * 0.865f, H * 0.1f), Size(W * 0.04f, H * 0.83f))
    drawRect(archColor, Offset(W * 0.08f, H * 0.1f), Size(W * 0.84f, H * 0.08f))

    // === LAYER 3: Stone tile floor ===
    val floorY = H * 0.72f
    val tileW = W / 20f
    val tileH = H * 0.06f
    var col = 0
    var x = -(scrollOffset * 0.5f % (tileW * 2))
    while (x < W + tileW) {
        val tileColor = if (col % 2 == 0) Color(0xFF1C1230) else Color(0xFF160E26)
        drawRect(tileColor, Offset(x, floorY), Size(tileW - 2f, tileH))
        drawRect(Color(0xFF0A0614), Offset(x + tileW - 2f, floorY), Size(2f, tileH))
        drawRect(Color(0xFF0A0614), Offset(x, floorY + tileH - 1f), Size(tileW, 1f))
        x += tileW
        col++
    }
    drawRect(Color(0x880A0614), Offset(0f, floorY - 10f), Size(W, 10f))

    // === LAYER 4: Stone wall tiles ===
    val wallBottom = H * 0.72f
    val wallTileH = H * 0.055f
    val wallTileW = W / 24f
    var wy = H * 0.18f
    var row = 0
    while (wy < wallBottom) {
        val offsetX = if (row % 2 == 0) 0f else wallTileW / 2f
        var wx = offsetX - (scrollOffset * 0.15f % wallTileW)
        while (wx < W) {
            val brightness = if ((row + wx.toInt()) % 3 == 0) 0.14f else 0.10f
            drawRect(
                Color(brightness, brightness * 0.7f, brightness * 1.2f, 1f),
                Offset(wx, wy),
                Size(wallTileW - 1f, wallTileH - 1f)
            )
            drawRect(Color(0xFF080412), Offset(wx, wy + wallTileH - 1f), Size(wallTileW, 1f))
            wx += wallTileW
        }
        wy += wallTileH
        row++
    }

    // === LAYER 5: Chains ===
    listOf(W * 0.22f, W * 0.78f).forEach { chainX ->
        for (i in 0..8) {
            val cy = H * 0.15f + i * 22f
            drawRect(Color(0xFF555544), Offset(chainX, cy), Size(6f, 10f))
            drawRect(Color(0xFF444433), Offset(chainX + 2f, cy + 2f), Size(2f, 6f))
        }
    }

    // === LAYER 6: Torches ===
    val torchPositions = listOf(W * 0.18f, W * 0.82f)
    torchPositions.forEach { tx ->
        val ty = H * 0.3f
        drawRect(Color(0xFF554400), Offset(tx - 4f, ty + 8f), Size(8f, 16f))
        drawRect(Color(0xFF776600), Offset(tx - 2f, ty + 10f), Size(4f, 12f))
        val glowAlpha = torchFlicker * 0.4f
        drawCircle(Color(1f, 0.4f, 0f, glowAlpha), radius = 40f, center = Offset(tx, ty))
        drawCircle(Color(1f, 0.7f, 0f, glowAlpha * 0.6f), radius = 25f, center = Offset(tx, ty))
        drawRect(Color(1f, 0.5f, 0f, torchFlicker), Offset(tx - 4f, ty - 14f), Size(8f, 16f))
        drawRect(Color(1f, 0.8f, 0f, torchFlicker), Offset(tx - 2f, ty - 18f), Size(4f, 12f))
        drawRect(Color(1f, 1f, 0.6f, torchFlicker * 0.8f), Offset(tx - 1f, ty - 22f), Size(2f, 8f))
    }

    // === LAYER 7: Floor light pools ===
    torchPositions.forEach { tx ->
        drawCircle(
            Color(1f, 0.4f, 0f, torchFlicker * 0.08f),
            radius = W * 0.18f,
            center = Offset(tx, H * 0.72f)
        )
    }

    // === LAYER 8: Vignette ===
    drawRect(
        Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0xAA000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.7f
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

        drawRect(Color(0xFF120A04), size = size)

        val beamColor = Color(0xFF1A0E06)
        val beamHighlight = Color(0xFF2A1A0A)
        listOf(0.0f, 0.18f, 0.36f).forEach { yFrac ->
            drawRect(beamColor, Offset(0f, H * yFrac), Size(W, H * 0.045f))
            drawRect(beamHighlight, Offset(0f, H * yFrac), Size(W, H * 0.008f))
        }

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

        val floorY = H * 0.62f
        var py = floorY
        var plankRow = 0
        while (py < H) {
            val pc = if (plankRow % 2 == 0) Color(0xFF2A1A08) else Color(0xFF241608)
            drawRect(pc, Offset(0f, py), Size(W, H * 0.058f))
            drawRect(Color(0xFF0A0604), Offset(0f, py + H * 0.055f), Size(W, H * 0.003f))
            var gx = 80f + (plankRow * 120f) % W
            while (gx < W) {
                drawRect(Color(0xFF1A0E04), Offset(gx, py), Size(2f, H * 0.055f))
                gx += W / 6f
            }
            py += H * 0.058f
            plankRow++
        }

        val fpX = W * 0.04f
        val fpY = H * 0.3f
        val fpW = W * 0.12f
        val fpH = H * 0.35f
        drawRect(Color(0xFF2A2018), Offset(fpX - 10f, fpY - 10f), Size(fpW + 20f, fpH + 10f))
        drawRect(Color(0xFF1A1410), Offset(fpX, fpY), Size(fpW, fpH))
        drawCircle(
            Color(1f, 0.35f, 0f, fireFlicker * 0.35f),
            radius = W * 0.22f, center = Offset(fpX + fpW / 2f, fpY + fpH * 0.5f)
        )
        drawCircle(
            Color(1f, 0.6f, 0f, fireFlicker * 0.25f),
            radius = W * 0.14f, center = Offset(fpX + fpW / 2f, fpY + fpH * 0.5f)
        )
        val fc = fpX + fpW / 2f
        val fb = fpY + fpH * 0.85f
        drawRect(Color(1f, 0.4f, 0f, fireFlicker), Offset(fc - 20f, fb - 50f), Size(40f, 50f))
        drawRect(Color(1f, 0.6f, 0f, fireFlicker2), Offset(fc - 14f, fb - 70f), Size(28f, 55f))
        drawRect(Color(1f, 0.85f, 0.1f, fireFlicker), Offset(fc - 8f, fb - 85f), Size(16f, 50f))
        drawRect(Color(1f, 1f, 0.5f, fireFlicker2 * 0.8f), Offset(fc - 4f, fb - 95f), Size(8f, 30f))
        repeat(5) { i ->
            drawCircle(
                Color(1f, 0.5f, 0f, fireFlicker * 0.9f),
                radius = 3f, center = Offset(fpX + fpW * 0.2f + i * fpW * 0.15f, fpY + fpH * 0.92f)
            )
        }
        drawRect(Color(0xFF3A2810), Offset(fpX - 15f, fpY - 20f), Size(fpW + 30f, 15f))
        drawRect(Color(0xFF4A3418), Offset(fpX - 15f, fpY - 25f), Size(fpW + 30f, 8f))

        drawRect(Color(1f, 0.35f, 0f, fireFlicker * 0.06f), size = size)

        val lx = W * 0.88f
        val ly = H * 0.28f
        drawRect(Color(0xFF444422), Offset(lx - 6f, ly - 20f), Size(12f, 4f))
        drawRect(Color(0xFF333311), Offset(lx - 4f, ly - 16f), Size(8f, 24f))
        drawRect(Color(1f, 0.8f, 0.2f, fireFlicker * 0.5f), Offset(lx - 2f, ly - 12f), Size(4f, 16f))
        drawCircle(Color(1f, 0.7f, 0f, fireFlicker * 0.3f), radius = 50f, center = Offset(lx, ly))

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

        // =========================================================================
        // LAYER 0: STARRY NIGHT SKY & CRESCENT MOON (Parallax 0.02x)
        // =========================================================================
        // Gradient Night Sky
        val skyGradient = Brush.verticalGradient(
            colors = listOf(Color(0xFF030712), Color(0xFF130924), Color(0xFF28103A))
        )
        drawRect(brush = skyGradient, size = size)

        // Twinkling Stars
        val starRng = java.util.Random(42)
        repeat(75) { i ->
            val origX = starRng.nextFloat() * (W * 3f)
            val starX = (origX - (scrollOffset * 0.02f)) % (W * 3f)
            val drawX = if (starX < 0) starX + (W * 3f) else starX
            val starY = starRng.nextFloat() * (H * 0.42f)
            val starAlpha = 0.3f + (starRng.nextFloat() * 0.6f)
            val starRadius = if (i % 8 == 0) 2.2f else 1.2f

            if (drawX in 0f..W) {
                drawCircle(Color.White.copy(alpha = starAlpha), radius = starRadius, center = Offset(drawX, starY))
            }
        }

        // Crescent Moon in Upper Right Sky
        val moonX = W * 0.82f - (scrollOffset * 0.02f % W)
        val moonY = H * 0.14f
        val moonRadius = 22f

        // Moon Glow Aura
        drawCircle(Color(0xFFFFF9E6).copy(alpha = 0.15f), radius = moonRadius * 1.8f, center = Offset(moonX, moonY))
        drawCircle(Color(0xFFFFF9E6).copy(alpha = 0.35f), radius = moonRadius * 1.3f, center = Offset(moonX, moonY))
        // Moon Body
        drawCircle(Color(0xFFFFFDF0), radius = moonRadius, center = Offset(moonX, moonY))
        // Shadow overlap creating crescent shape
        drawCircle(Color(0xFF130924), radius = moonRadius * 0.9f, center = Offset(moonX - 8f, moonY - 4f))

        // =========================================================================
        // LAYER 1: DISTANT CRIMSON MOUNTAINS & CLIFFSIDE GOTHIC CASTLE (Parallax 0.12x)
        // =========================================================================
        val mountainY = H * 0.52f
        val mWidth = 600f
        var mx = -(scrollOffset * 0.12f % mWidth)

        while (mx < W + mWidth) {
            // Far Mountain Silhouette (Dull Crimson/Purple)
            val pathFar = Path().apply {
                moveTo(mx, mountainY)
                lineTo(mx + mWidth * 0.25f, mountainY - 90f)
                lineTo(mx + mWidth * 0.55f, mountainY - 170f)
                lineTo(mx + mWidth * 0.75f, mountainY - 80f)
                lineTo(mx + mWidth, mountainY)
                close()
            }
            drawPath(pathFar, Color(0xFF281232))

            // Nearer Mountain Silhouette (Deep Violet/Crimson)
            val pathNear = Path().apply {
                moveTo(mx + 80f, mountainY)
                lineTo(mx + mWidth * 0.35f, mountainY - 130f)
                lineTo(mx + mWidth * 0.65f, mountainY - 60f)
                lineTo(mx + mWidth + 80f, mountainY)
                close()
            }
            drawPath(pathNear, Color(0xFF1D0A26))

            mx += mWidth
        }

        // --- CLIFFSIDE GOTHIC CASTLE KEEP (Right Side Landmark) ---
        val castleRngX = (W * 1.8f) - (scrollOffset * 0.12f % (W * 3f))
        val castleX = if (castleRngX < -300f) castleRngX + (W * 3f) else castleRngX
        val cliffY = H * 0.52f

        if (castleX in -300f..(W + 300f)) {
            // High Rock Cliff Base
            val cliffPath = Path().apply {
                moveTo(castleX - 70f, cliffY + 20f)
                lineTo(castleX - 40f, cliffY - 60f)
                lineTo(castleX + 110f, cliffY - 60f)
                lineTo(castleX + 140f, cliffY + 20f)
                close()
            }
            drawPath(cliffPath, Color(0xFF14071C))

            // Castle Main Spire Keep
            val castleColor = Color(0xFF0C0314)
            val windowGlow = Color(0xFFF39C12)

            // Main Central Tower
            drawRect(castleColor, Offset(castleX, cliffY - 170f), Size(40f, 110f))
            // Pointed Roof Peak
            val mainSpire = Path().apply {
                moveTo(castleX - 4f, cliffY - 170f)
                lineTo(castleX + 20f, cliffY - 220f)
                lineTo(castleX + 44f, cliffY - 170f)
                close()
            }
            drawPath(mainSpire, castleColor)

            // Left Side Flank Tower
            drawRect(castleColor, Offset(castleX - 30f, cliffY - 130f), Size(24f, 70f))
            val leftSpire = Path().apply {
                moveTo(castleX - 34f, cliffY - 130f)
                lineTo(castleX - 18f, cliffY - 165f)
                lineTo(castleX - 2f, cliffY - 130f)
                close()
            }
            drawPath(leftSpire, castleColor)

            // Right Side Flank Tower
            drawRect(castleColor, Offset(castleX + 46f, cliffY - 130f), Size(24f, 70f))
            val rightSpire = Path().apply {
                moveTo(castleX + 42f, cliffY - 130f)
                lineTo(castleX + 58f, cliffY - 165f)
                lineTo(castleX + 74f, cliffY - 130f)
                close()
            }
            drawPath(rightSpire, castleColor)

            // Illuminated Orange Castle Windows
            drawRect(windowGlow, Offset(castleX + 16f, cliffY - 150f), Size(8f, 14f))
            drawRect(windowGlow, Offset(castleX + 16f, cliffY - 110f), Size(8f, 14f))
            drawRect(windowGlow, Offset(castleX - 22f, cliffY - 110f), Size(6f, 10f))
            drawRect(windowGlow, Offset(castleX + 54f, cliffY - 110f), Size(6f, 10f))
        }

        // =========================================================================
        // LAYER 2: DENSE FOREST CANOPY & WOODEN FENCE (Parallax 0.35x)
        // =========================================================================
        val forestY = H * 0.64f
        val treeW = 120f
        var tx = -(scrollOffset * 0.35f % treeW)

        while (tx < W + treeW) {
            // Dark forest background trunks & shadow canopy
            drawRect(Color(0xFF061406), Offset(tx + 15f, forestY - 70f), Size(20f, 90f))
            drawCircle(Color(0xFF092109), radius = 45f, center = Offset(tx + 25f, forestY - 80f))
            drawCircle(Color(0xFF0F330F), radius = 38f, center = Offset(tx + 65f, forestY - 75f))
            drawCircle(Color(0xFF0B260B), radius = 42f, center = Offset(tx + 100f, forestY - 80f))

            tx += treeW
        }

        // Rustic Wooden Post-and-Rail Fence along Forest Line
        val fenceW = 90f
        var fx = -(scrollOffset * 0.35f % fenceW)
        val fenceY = forestY + 5f
        val fenceColor = Color(0xFF4A321A)

        while (fx < W + fenceW) {
            // Horizontal fence rails
            drawRect(fenceColor, Offset(fx, fenceY - 18f), Size(fenceW, 4f))
            drawRect(fenceColor, Offset(fx, fenceY - 8f), Size(fenceW, 4f))
            // Vertical fence posts
            drawRect(Color(0xFF38220F), Offset(fx + 10f, fenceY - 26f), Size(6f, 28f))
            drawRect(Color(0xFF38220F), Offset(fx + 55f, fenceY - 26f), Size(6f, 28f))

            fx += fenceW
        }

        // =========================================================================
        // LAYER 3: FOREGROUND DIRT GROUND, COBBLESTONE PATH & GRASS (Scroll 1.0x)
        // =========================================================================
        val groundY = H * 0.72f
        // Warm Rich Dirt Base
        drawRect(Color(0xFF382212), Offset(0f, groundY), Size(W, H - groundY))
        drawRect(Color(0xFF28150A), Offset(0f, groundY), Size(W, 6f)) // Ground border shadow

        // Winding Cobblestone Road / Pathways
        val cobW = 90f
        var cx = -(scrollOffset % cobW)
        val roadY = groundY + 8f

        while (cx < W + cobW) {
            // Rounded Cobblestone Pavers
            drawRect(Color(0xFF626F73), Offset(cx + 4f, roadY), Size(24f, 12f))
            drawRect(Color(0xFF4D5659), Offset(cx + 32f, roadY + 2f), Size(26f, 11f))
            drawRect(Color(0xFF626F73), Offset(cx + 62f, roadY), Size(22f, 12f))

            drawRect(Color(0xFF4D5659), Offset(cx + 16f, roadY + 16f), Size(28f, 12f))
            drawRect(Color(0xFF626F73), Offset(cx + 48f, roadY + 16f), Size(26f, 12f))

            drawRect(Color(0xFF626F73), Offset(cx + 6f, roadY + 32f), Size(26f, 11f))
            drawRect(Color(0xFF4D5659), Offset(cx + 36f, roadY + 32f), Size(28f, 11f))
            drawRect(Color(0xFF626F73), Offset(cx + 68f, roadY + 32f), Size(18f, 11f))

            cx += cobW
        }

        // Foreground Grass Tufts & Stones
        val grassW = 100f
        var gx = -(scrollOffset % grassW)
        val grassColor = Color(0xFF1E380C)

        while (gx < W + grassW) {
            // Grass tuft blades
            drawRect(grassColor, Offset(gx + 12f, H - 22f), Size(4f, 18f))
            drawRect(grassColor, Offset(gx + 18f, H - 26f), Size(4f, 22f))
            drawRect(grassColor, Offset(gx + 24f, H - 20f), Size(4f, 16f))

            // Small roadside pebble stones
            drawCircle(Color(0xFF525E61), radius = 2.5f, center = Offset(gx + 55f, H - 12f))
            drawCircle(Color(0xFF3B4447), radius = 3.5f, center = Offset(gx + 62f, H - 10f))

            gx += grassW
        }
    }
}
