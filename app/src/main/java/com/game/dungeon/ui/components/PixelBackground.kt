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
            BiomeType.SEA_SHRINE -> drawSeaShrine(scrollOffset, slowPulse, medPulse)
            BiomeType.EARTH_CAVE -> drawEarthCave(scrollOffset, torchFlicker, slowPulse)
            BiomeType.CRYSTAL_TOWER -> drawCrystalTower(scrollOffset, slowPulse, medPulse)
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

private data class ColumnSpec(
    val xFrac: Float,
    val topYFrac: Float,
    val botYFrac: Float,
    val widthFrac: Float,
    val isBroken: Boolean = false
)

private fun DrawScope.drawChaosShrine(scrollOffset: Float, slowPulse: Float, medPulse: Float) {
    val W = size.width
    val H = size.height

    // === LAYER 1: Swirling Magenta/Purple Void Sky ===
    drawRect(
        Brush.verticalGradient(
            listOf(
                Color(0xFF14081E),
                Color(0xFF2A0D38),
                Color(0xFF4A1856),
                Color(0xFF240A30),
                Color(0xFF100618)
            )
        ),
        size = size
    )

    // Animated swirling cloud masses
    repeat(6) { i ->
        val cloudY = H * (0.05f + i * 0.08f)
        val cloudPath = Path().apply {
            moveTo(-W * 0.2f, cloudY)
            var x = -W * 0.2f
            while (x <= W * 1.2f) {
                val wave = sin((x + scrollOffset * 0.3f) * 0.008f + i) * (H * 0.04f)
                lineTo(x, cloudY + wave)
                x += 40f
            }
            lineTo(W * 1.2f, cloudY + H * 0.15f)
            lineTo(-W * 0.2f, cloudY + H * 0.15f)
            close()
        }
        val cloudAlpha = 0.12f + sin(slowPulse * 3.14159f + i) * 0.05f
        drawPath(
            cloudPath,
            Brush.verticalGradient(
                listOf(Color(0xFF6B2272).copy(alpha = max(0.01f, cloudAlpha)), Color.Transparent)
            )
        )
    }

    val floorY = H * 0.50f

    // === LAYER 2: Distant Horizon Silhouettes (Floating platform edge & distant ruined shrine) ===
    val horizonPath = Path().apply {
        moveTo(0f, floorY)
        lineTo(W * 0.08f, floorY - H * 0.06f)
        lineTo(W * 0.15f, floorY - H * 0.02f)
        lineTo(W * 0.22f, floorY - H * 0.08f) // Ruined throne peak
        lineTo(W * 0.28f, floorY - H * 0.03f)
        lineTo(W * 0.35f, floorY - H * 0.05f)
        lineTo(W * 0.45f, floorY - H * 0.02f)
        lineTo(W * 0.60f, floorY - H * 0.07f)
        lineTo(W * 0.75f, floorY - H * 0.03f)
        lineTo(W * 0.88f, floorY - H * 0.06f)
        lineTo(W, floorY - H * 0.02f)
        lineTo(W, floorY)
        close()
    }
    drawPath(horizonPath, Color(0xFF120A1C))

    // Broken throne silhouette in far distance (left side)
    val throneX = W * 0.18f
    val throneY = floorY - H * 0.08f
    drawRect(Color(0xFF1A1028), Offset(throneX - 12f, throneY - 18f), Size(24f, 22f))
    drawRect(Color(0xFF10081C), Offset(throneX - 8f, throneY - 26f), Size(16f, 10f))

    // Far background broken pillar stumps
    listOf(0.32f to 0.06f, 0.48f to 0.04f, 0.68f to 0.08f, 0.78f to 0.05f).forEach { (xFrac, hFrac) ->
        val px = W * xFrac
        val pw = W * 0.025f
        val ph = H * hFrac
        drawRect(Color(0xFF1E142B), Offset(px - pw / 2f, floorY - ph), Size(pw, ph))
        drawRect(Color(0xFF281C38), Offset(px - pw / 2f, floorY - ph), Size(pw * 0.4f, ph))
    }

    // === LAYER 3: Perspective Tiled Temple Floor ===
    drawRect(Color(0xFF221830), Offset(0f, floorY), Size(W, H - floorY))

    // Perspective floor tiles (Grid and Motifs)
    val vpX = W * 0.35f // Vanishing point matching reference image perspective
    val tileRows = 12
    repeat(tileRows) { i ->
        val p1 = i / tileRows.toFloat()
        val rowY1 = floorY + (H - floorY) * (p1 * p1)

        drawLine(Color(0xFF382A48), Offset(0f, rowY1), Offset(W, rowY1), strokeWidth = 1.2f)

        // Draw tile rosettes/motifs on alternating rows
        if (i > 1 && i % 2 == 0) {
            val cols = 6
            repeat(cols) { c ->
                val colFrac = (c + 0.5f) / cols
                val tileX = vpX + (colFrac - 0.5f) * W * (p1 * 2.2f + 0.8f)
                val motifSize = (10f * p1 + 3f)
                if (tileX in 0f..W && rowY1 >= floorY) {
                    drawRect(
                        Color(0xFF302242),
                        Offset(tileX - motifSize / 2f, rowY1 - motifSize / 2f),
                        Size(motifSize, motifSize),
                        style = Stroke(1.5f)
                    )
                    drawCircle(
                        Color(0xFF44325C),
                        radius = motifSize * 0.3f,
                        center = Offset(tileX, rowY1)
                    )
                }
            }
        }
    }

    // Perspective longitudinal lines
    repeat(10) { col ->
        val startFrac = col / 9f
        val bottomX = vpX + (startFrac - 0.5f) * W * 3.0f
        drawLine(
            Color(0xFF382A48),
            Offset(vpX + (startFrac - 0.5f) * W * 0.2f, floorY),
            Offset(bottomX, H),
            strokeWidth = 1.5f
        )
    }

    // Left floating edge of floor platform dropping into void
    val cliffEdgePath = Path().apply {
        moveTo(0f, floorY + H * 0.1f)
        lineTo(W * 0.15f, floorY + H * 0.35f)
        lineTo(W * 0.08f, H)
        lineTo(0f, H)
        close()
    }
    drawPath(cliffEdgePath, Color(0xFF140D20))

    // === LAYER 4: Deep Jagged Floor Pits & Low Ruined Wall ===
    // Pit 1: Large Central Floor Hole
    val pit1Path = Path().apply {
        moveTo(W * 0.18f, floorY + H * 0.08f)
        lineTo(W * 0.38f, floorY + H * 0.06f)
        lineTo(W * 0.42f, floorY + H * 0.22f)
        lineTo(W * 0.22f, floorY + H * 0.25f)
        lineTo(W * 0.15f, floorY + H * 0.15f)
        close()
    }
    drawPath(pit1Path, Color(0xFF0A0512))
    drawPath(pit1Path, Color(0xFF4A3858), style = Stroke(2.5f))

    // Pit 2: Right Side Floor Hole
    val pit2Path = Path().apply {
        moveTo(W * 0.68f, floorY + H * 0.18f)
        lineTo(W * 0.82f, floorY + H * 0.15f)
        lineTo(W * 0.85f, floorY + H * 0.30f)
        lineTo(W * 0.70f, floorY + H * 0.32f)
        close()
    }
    drawPath(pit2Path, Color(0xFF0A0512))
    drawPath(pit2Path, Color(0xFF4A3858), style = Stroke(2.5f))

    // Low Ruined Stone Wall in Middle/Left
    val wallPath = Path().apply {
        moveTo(W * 0.15f, floorY + H * 0.18f)
        lineTo(W * 0.28f, floorY + H * 0.08f)
        lineTo(W * 0.30f, floorY + H * 0.12f)
        lineTo(W * 0.24f, floorY + H * 0.24f)
        lineTo(W * 0.12f, floorY + H * 0.26f)
        close()
    }
    drawPath(wallPath, Color(0xFF2A1E38))
    drawPath(wallPath, Color(0xFF483658), style = Stroke(1.5f))

    // === LAYER 5: Majestic Grand Pillars (Fluted Stone Columns) ===
    val columns = listOf(
        ColumnSpec(xFrac = 0.92f, topYFrac = 0.10f, botYFrac = 0.95f, widthFrac = 0.08f, isBroken = false),
        ColumnSpec(xFrac = 0.54f, topYFrac = 0.12f, botYFrac = 0.78f, widthFrac = 0.06f, isBroken = false),
        ColumnSpec(xFrac = 0.67f, topYFrac = 0.18f, botYFrac = 0.65f, widthFrac = 0.048f, isBroken = false),
        ColumnSpec(xFrac = 0.73f, topYFrac = 0.20f, botYFrac = 0.58f, widthFrac = 0.04f, isBroken = false),
        ColumnSpec(xFrac = 0.44f, topYFrac = 0.38f, botYFrac = 0.58f, widthFrac = 0.042f, isBroken = true),
        ColumnSpec(xFrac = 0.36f, topYFrac = 0.44f, botYFrac = 0.54f, widthFrac = 0.035f, isBroken = true)
    )

    columns.forEach { col ->
        drawGrandColumn(col, W, H)
    }

    // === LAYER 6: Ethereal Void Energy & Floating Particles ===
    val particleRng = java.util.Random(1337)
    repeat(25) { i ->
        val px = (particleRng.nextFloat() * W + sin(scrollOffset * 0.03f + i) * 20f) % W
        val py = (H - ((scrollOffset * 0.7f + i * 35f) % H))
        val pAlpha = 0.2f + sin(scrollOffset * 0.06f + i) * 0.25f + medPulse * 0.15f
        
        drawCircle(
            Color(0xFFD050FF).copy(alpha = max(0f, pAlpha)),
            radius = 2.5f + (i % 3),
            center = Offset(px, py)
        )
    }

    // === LAYER 7: Atmospheric Vignette ===
    drawRect(
        Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0xCC000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.78f
        ),
        size = size
    )
}

private fun DrawScope.drawGrandColumn(col: ColumnSpec, canvasWidth: Float, canvasHeight: Float) {
    val cx = canvasWidth * col.xFrac
    val cw = canvasWidth * col.widthFrac
    val topY = canvasHeight * col.topYFrac
    val botY = canvasHeight * col.botYFrac
    val ch = botY - topY

    val baseStone = Color(0xFF5C526A)
    val highlightStone = Color(0xFF8E849E)
    val shadowStone = Color(0xFF322A40)
    val detailColor = Color(0xFF221A30)

    val shaftBrush = Brush.horizontalGradient(
        colors = listOf(highlightStone, baseStone, shadowStone),
        startX = cx - cw / 2f,
        endX = cx + cw / 2f
    )

    if (col.isBroken) {
        val breakPath = Path().apply {
            moveTo(cx - cw / 2f, botY)
            lineTo(cx - cw / 2f, topY + 10f)
            lineTo(cx - cw * 0.2f, topY)
            lineTo(cx + cw * 0.1f, topY + 15f)
            lineTo(cx + cw / 2f, topY - 5f)
            lineTo(cx + cw / 2f, botY)
            close()
        }
        drawPath(breakPath, shaftBrush)
        drawPath(breakPath, Color(0xFF1E162B), style = Stroke(1.5f))
    } else {
        drawRect(shaftBrush, Offset(cx - cw / 2f, topY), Size(cw, ch))
        drawRect(Color(0xFF1E162B), Offset(cx - cw / 2f, topY), Size(cw, ch), style = Stroke(1.2f))

        // Capital Top
        drawRect(highlightStone, Offset(cx - cw * 0.65f, topY - 8f), Size(cw * 1.3f, 8f))
        drawRect(shadowStone, Offset(cx - cw * 0.6f, topY - 14f), Size(cw * 1.2f, 6f))
    }

    // Fluting lines along shaft
    val fluteCount = 4
    repeat(fluteCount) { i ->
        val fluteX = cx - cw * 0.35f + i * (cw * 0.7f / (fluteCount - 1))
        val fTopY = if (col.isBroken) topY + 12f else topY + 10f
        drawLine(
            Color(0xFF2B2238).copy(alpha = 0.6f),
            Offset(fluteX, fTopY),
            Offset(fluteX, botY - 10f),
            strokeWidth = max(1f, cw * 0.08f)
        )
    }

    // Carved geometric band near the base
    val bandY = botY - ch * 0.25f
    val bandH = (ch * 0.12f).coerceAtMost(28f)
    if (bandY > topY && bandY + bandH < botY) {
        drawRect(detailColor, Offset(cx - cw / 2f, bandY), Size(cw, bandH))
        val zigzagPath = Path().apply {
            moveTo(cx - cw / 2f, bandY + bandH / 2f)
            var zx = cx - cw / 2f
            var up = true
            while (zx <= cx + cw / 2f) {
                val zy = if (up) bandY + 2f else bandY + bandH - 2f
                lineTo(zx, zy)
                zx += cw * 0.25f
                up = !up
            }
        }
        drawPath(zigzagPath, Color(0xFF9084A4), style = Stroke(1.5f))
    }

    // Base Bottom Cap
    drawRect(highlightStone, Offset(cx - cw * 0.6f, botY - 6f), Size(cw * 1.2f, 6f))
    drawRect(shadowStone, Offset(cx - cw * 0.7f, botY), Size(cw * 1.4f, 8f))
}

private fun DrawScope.drawGurguVolcano(scrollOffset: Float, torchFlicker: Float, medPulse: Float) {
    val W = size.width
    val H = size.height

    // === LAYER 1: Deep Cavern Fiery Atmosphere & Roof Overhang ===
    drawRect(
        Brush.verticalGradient(
            listOf(
                Color(0xFF140500),
                Color(0xFF280B00),
                Color(0xFF481200),
                Color(0xFF320A00),
                Color(0xFF180400)
            )
        ),
        size = size
    )

    // Heat haze ambient pulse
    drawRect(Color(1f, 0.35f, 0f, 0.06f * torchFlicker + medPulse * 0.03f), size = size)

    // Jagged Cavern Ceiling / Stalactite Roof Overhang
    val ceilingPath = Path().apply {
        moveTo(0f, 0f)
        lineTo(W, 0f)
        lineTo(W, H * 0.08f)
        lineTo(W * 0.88f, H * 0.14f)
        lineTo(W * 0.78f, H * 0.06f)
        lineTo(W * 0.65f, H * 0.18f)
        lineTo(W * 0.52f, H * 0.09f)
        lineTo(W * 0.40f, H * 0.16f)
        lineTo(W * 0.28f, H * 0.07f)
        lineTo(W * 0.12f, H * 0.15f)
        lineTo(0f, H * 0.06f)
        close()
    }
    drawPath(ceilingPath, Color(0xFF180A04))
    drawPath(ceilingPath, Color(0xFF38180A), style = Stroke(2f))

    val floorY = H * 0.52f

    // === LAYER 2: Background Basalt Rock Cavern Arches & Lava Waterfalls ===
    val archPath1 = Path().apply {
        moveTo(W * 0.45f, floorY)
        quadraticTo(W * 0.60f, floorY - H * 0.28f, W * 0.75f, floorY - H * 0.05f)
        lineTo(W * 0.80f, floorY)
        close()
    }
    drawPath(archPath1, Color(0xFF200E06))

    val archPath2 = Path().apply {
        moveTo(W * 0.65f, floorY)
        quadraticTo(W * 0.78f, floorY - H * 0.32f, W * 0.92f, floorY - H * 0.08f)
        lineTo(W, floorY)
        close()
    }
    drawPath(archPath2, Color(0xFF1A0A04))

    // Vertical Cascading Lava Waterfalls
    listOf(0.58f, 0.76f, 0.88f).forEachIndexed { index, xFrac ->
        val lavaX = W * xFrac
        val lavaTop = H * (0.18f + index * 0.04f)
        val lavaWidth = W * (0.025f - index * 0.004f)

        // Outer Orange/Red Stream
        val waterfallPath = Path().apply {
            moveTo(lavaX - lavaWidth / 2f, lavaTop)
            var curY = lavaTop
            while (curY <= floorY + H * 0.25f) {
                val waveX = lavaX + sin(curY * 0.04f + scrollOffset * 0.08f + index) * 6f
                lineTo(waveX - lavaWidth / 2f, curY)
                curY += 15f
            }
            lineTo(lavaX + lavaWidth / 2f, floorY + H * 0.25f)
            while (curY >= lavaTop) {
                val waveX = lavaX + sin(curY * 0.04f + scrollOffset * 0.08f + index) * 6f
                lineTo(waveX + lavaWidth / 2f, curY)
                curY -= 15f
            }
            close()
        }
        drawPath(
            waterfallPath,
            Brush.verticalGradient(
                listOf(Color(0xFFFFDD00), Color(0xFFFF5500), Color(0xFFFF1100))
            )
        )

        // Core Yellow Glowing Core
        drawLine(
            Color(0xFFFFFF88),
            Offset(lavaX, lavaTop),
            Offset(lavaX, floorY + H * 0.25f),
            strokeWidth = lavaWidth * 0.35f
        )
    }

    // === LAYER 3: Perspective Volcanic Obsidian Ledge & Basalt Flooring ===
    val ledgePath = Path().apply {
        moveTo(0f, floorY - H * 0.04f)
        lineTo(W * 0.58f, floorY)
        lineTo(W * 0.45f, H)
        lineTo(0f, H)
        close()
    }
    drawPath(ledgePath, Color(0xFF28180E))

    // Hexagonal / Square Basalt Tile Lines on the Ledge
    repeat(8) { row ->
        val progress = (row + 1) / 8f
        val lineY = floorY - H * 0.04f + (H - (floorY - H * 0.04f)) * progress
        val rightX = W * 0.58f - (W * 0.13f) * progress
        drawLine(Color(0xFF3E2818), Offset(0f, lineY), Offset(rightX, lineY), strokeWidth = 1.5f)

        // Glowing lava crack veins running across floor
        if (row % 2 == 1) {
            val crackY = lineY - 8f
            val crackPath = Path().apply {
                moveTo(10f, crackY)
                lineTo(rightX * 0.3f, crackY + 4f)
                lineTo(rightX * 0.6f, crackY - 3f)
                lineTo(rightX * 0.9f, crackY + 2f)
            }
            val crackGlow = 0.5f + sin(medPulse * 3.14159f + row) * 0.3f
            drawPath(crackPath, Color(1f, 0.4f, 0f, crackGlow), style = Stroke(2f))
        }
    }

    // Vertical cliff edge drop on the right side of the obsidian pathway
    val cliffDropPath = Path().apply {
        moveTo(W * 0.58f, floorY)
        lineTo(W * 0.62f, floorY + H * 0.12f)
        lineTo(W * 0.52f, floorY + H * 0.32f)
        lineTo(W * 0.58f, floorY + H * 0.55f)
        lineTo(W * 0.45f, H)
        lineTo(W * 0.40f, H)
        lineTo(W * 0.52f, floorY + H * 0.30f)
        close()
    }
    drawPath(cliffDropPath, Color(0xFF140A04))

    // === LAYER 4: Cascading Magma Abyss & Moving Lava Rivers ===
    val magmaY = floorY + H * 0.08f
    val magmaPath = Path().apply {
        moveTo(W * 0.58f, magmaY)
        var x = W * 0.58f
        while (x <= W + 20f) {
            val waveY = magmaY + sin((x + scrollOffset * 0.6f) * 0.03f) * 10f + cos(x * 0.02f) * 6f
            lineTo(x, waveY)
            x += 15f
        }
        lineTo(W, H)
        lineTo(W * 0.45f, H)
        close()
    }
    drawPath(
        magmaPath,
        Brush.verticalGradient(
            listOf(Color(0xFFFFCC00), Color(0xFFFF4400), Color(0xFF880000))
        )
    )

    // Moving Lava Wave Highlights
    val wavePath = Path().apply {
        moveTo(W * 0.62f, magmaY + 20f)
        var x = W * 0.62f
        while (x <= W + 20f) {
            val waveY = magmaY + 20f + sin((x - scrollOffset * 0.8f) * 0.04f) * 8f
            lineTo(x, waveY)
            x += 12f
        }
        lineTo(W, H)
        lineTo(W * 0.50f, H)
        close()
    }
    drawPath(wavePath, Color(1f, 0.7f, 0.1f, 0.4f + torchFlicker * 0.2f))

    // Magma reflection glow pools
    drawCircle(
        Color(1f, 0.5f, 0f, 0.22f * torchFlicker),
        radius = W * 0.35f,
        center = Offset(W * 0.75f, H * 0.75f)
    )

    // === LAYER 5: Basalt Columns & Cavern Pillars ===
    listOf(0.06f to 0.12f, 0.18f to 0.10f, 0.32f to 0.08f).forEach { (xFrac, wFrac) ->
        val px = W * xFrac
        val pw = W * wFrac
        val pTop = H * 0.10f
        val pBot = floorY + H * 0.35f

        drawRect(
            Brush.horizontalGradient(
                colors = listOf(Color(0xFF503828), Color(0xFF322218), Color(0xFF180E08)),
                startX = px - pw / 2f,
                endX = px + pw / 2f
            ),
            Offset(px - pw / 2f, pTop),
            Size(pw, pBot - pTop)
        )
        drawRect(Color(0xFF100804), Offset(px - pw / 2f, pTop), Size(pw, pBot - pTop), style = Stroke(1.2f))

        drawRect(
            Color(1f, 0.4f, 0f, 0.15f * torchFlicker),
            Offset(px - pw / 2f, pTop),
            Size(pw * 0.3f, pBot - pTop)
        )
    }

    // === LAYER 6: Floating Fire Embers & Smoke Motes ===
    val emberRng = java.util.Random(999)
    repeat(35) { i ->
        val ex = (emberRng.nextFloat() * W + sin(scrollOffset * 0.04f + i) * 25f) % W
        val ey = (H - ((scrollOffset * 1.2f + i * 30f) % H))
        val eAlpha = max(0f, 0.3f + sin(scrollOffset * 0.08f + i) * 0.4f)
        val eRadius = 1.5f + (i % 3) * 1.2f

        drawCircle(
            Color(1f, 0.6f + (i % 4) * 0.1f, 0.1f, eAlpha),
            radius = eRadius,
            center = Offset(ex, ey)
        )
    }

    // === LAYER 7: Atmospheric Heat Vignette ===
    drawRect(
        Brush.radialGradient(
            listOf(Color.Transparent, Color(0xDD000000)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.78f
        ),
        size = size
    )
}

private data class SeaColumnSpec(
    val xFrac: Float,
    val topYFrac: Float,
    val botYFrac: Float,
    val widthFrac: Float,
    val isBroken: Boolean = false,
    val hasGlowAura: Boolean = false
)

private fun DrawScope.drawSeaColumn(
    col: SeaColumnSpec,
    canvasWidth: Float,
    canvasHeight: Float,
    auraPulse: Float
) {
    val cx = canvasWidth * col.xFrac
    val cw = canvasWidth * col.widthFrac
    val topY = canvasHeight * col.topYFrac
    val botY = canvasHeight * col.botYFrac
    val ch = botY - topY

    if (ch <= 0f || cw <= 0f) return

    val highlightStone = Color(0xFF50E6FF)
    val midtoneStone = Color(0xFF146A94)
    val shadowStone = Color(0xFF06334D)
    val deepDark = Color(0xFF021B2A)

    val shaftBrush = Brush.horizontalGradient(
        colors = listOf(highlightStone, midtoneStone, shadowStone, deepDark),
        startX = cx - cw / 2f,
        endX = cx + cw / 2f
    )

    // Outer Bioluminescent Aura Glow if enabled (e.g. for main left column)
    if (col.hasGlowAura) {
        val auraAlpha = 0.25f + auraPulse * 0.15f
        repeat(3) { pass ->
            val strokeW = cw * (0.15f + pass * 0.12f)
            val auraColor = Color(0xFF00E5FF).copy(alpha = max(0.01f, auraAlpha / (pass + 1)))
            if (col.isBroken) {
                val auraPath = Path().apply {
                    moveTo(cx - cw / 2f, botY)
                    lineTo(cx - cw / 2f, topY + 14f)
                    lineTo(cx - cw * 0.15f, topY)
                    lineTo(cx + cw * 0.1f, topY + 18f)
                    lineTo(cx + cw / 2f, topY - 4f)
                    lineTo(cx + cw / 2f, botY)
                    close()
                }
                drawPath(auraPath, auraColor, style = Stroke(strokeW))
            } else {
                drawRect(
                    auraColor,
                    Offset(cx - cw / 2f - strokeW / 2f, topY - strokeW / 2f),
                    Size(cw + strokeW, ch + strokeW),
                    style = Stroke(strokeW)
                )
            }
        }
    }

    // Shaft Body
    if (col.isBroken) {
        val breakPath = Path().apply {
            moveTo(cx - cw / 2f, botY)
            lineTo(cx - cw / 2f, topY + 14f)
            lineTo(cx - cw * 0.15f, topY)
            lineTo(cx + cw * 0.1f, topY + 18f)
            lineTo(cx + cw / 2f, topY - 4f)
            lineTo(cx + cw / 2f, botY)
            close()
        }
        drawPath(breakPath, shaftBrush)
        drawPath(breakPath, Color(0xFF02101C), style = Stroke(1.8f))
    } else {
        drawRect(shaftBrush, Offset(cx - cw / 2f, topY), Size(cw, ch))
        drawRect(Color(0xFF02101C), Offset(cx - cw / 2f, topY), Size(cw, ch), style = Stroke(1.5f))

        // Capital Top
        drawRect(highlightStone, Offset(cx - cw * 0.65f, topY - 10f), Size(cw * 1.3f, 10f))
        drawRect(shadowStone, Offset(cx - cw * 0.58f, topY - 18f), Size(cw * 1.16f, 8f))
        drawRect(Color(0xFF02101C), Offset(cx - cw * 0.65f, topY - 18f), Size(cw * 1.3f, 18f), style = Stroke(1.2f))
    }

    // Fluting grooves along shaft
    val fluteCount = 5
    repeat(fluteCount) { i ->
        val fluteX = cx - cw * 0.36f + i * (cw * 0.72f / (fluteCount - 1))
        val fTopY = if (col.isBroken) topY + 14f else topY + 10f
        drawLine(
            Color(0xFF021524).copy(alpha = 0.75f),
            Offset(fluteX, fTopY),
            Offset(fluteX, botY - 12f),
            strokeWidth = max(1.2f, cw * 0.07f)
        )
        drawLine(
            Color(0xFF80EEFF).copy(alpha = 0.35f),
            Offset(fluteX + 1f, fTopY),
            Offset(fluteX + 1f, botY - 12f),
            strokeWidth = 1f
        )
    }

    // Decorative carved base band
    val bandH = (ch * 0.08f).coerceIn(10f, 22f)
    val bandY = botY - 14f - bandH
    if (bandY > topY + 15f) {
        drawRect(Color(0xFF042033), Offset(cx - cw / 2f, bandY), Size(cw, bandH))
        val zigzagPath = Path().apply {
            moveTo(cx - cw / 2f, bandY + bandH / 2f)
            var zx = cx - cw / 2f
            var up = true
            while (zx <= cx + cw / 2f) {
                val zy = if (up) bandY + 2f else bandY + bandH - 2f
                lineTo(zx, zy)
                zx += cw * 0.25f
                up = !up
            }
        }
        drawPath(zigzagPath, Color(0xFF00E5FF), style = Stroke(1.2f))
    }

    // Base Bottom Cap
    drawRect(highlightStone, Offset(cx - cw * 0.62f, botY - 10f), Size(cw * 1.24f, 10f))
    drawRect(shadowStone, Offset(cx - cw * 0.58f, botY - 14f), Size(cw * 1.16f, 4f))
    drawRect(Color(0xFF02101C), Offset(cx - cw * 0.62f, botY - 14f), Size(cw * 1.24f, 14f), style = Stroke(1.2f))

    // Coral / Barnacle accretions on column base and shaft
    val coralRng = java.util.Random((cx * 1000 + topY).toLong())
    repeat(4) {
        val coralX = cx + (coralRng.nextFloat() - 0.5f) * cw
        val coralY = botY - 10f - coralRng.nextFloat() * (ch * 0.35f)
        val coralR = 3f + coralRng.nextFloat() * 4f
        val coralColor = if (coralRng.nextBoolean()) Color(0xFFF05580) else Color(0xFF00E0A0)
        drawCircle(coralColor, radius = coralR, center = Offset(coralX, coralY))
        drawCircle(Color.White.copy(alpha = 0.5f), radius = coralR * 0.4f, center = Offset(coralX - 1f, coralY - 1f))
    }
}

private fun DrawScope.drawSeaShrine(scrollOffset: Float, slowPulse: Float, medPulse: Float) {
    val W = size.width
    val H = size.height

    // === LAYER 1: Deep Ocean Sky & Water Currents ===
    drawRect(
        Brush.verticalGradient(
            listOf(
                Color(0xFF020818),
                Color(0xFF051733),
                Color(0xFF0A2A4E),
                Color(0xFF061C36),
                Color(0xFF010A16)
            )
        ),
        size = size
    )

    repeat(5) { i ->
        val currentY = H * (0.04f + i * 0.09f)
        val currentPath = Path().apply {
            moveTo(-W * 0.2f, currentY)
            var x = -W * 0.2f
            while (x <= W * 1.2f) {
                val wave = sin((x + scrollOffset * 0.4f) * 0.007f + i) * (H * 0.03f)
                lineTo(x, currentY + wave)
                x += 35f
            }
            lineTo(W * 1.2f, currentY + H * 0.12f)
            lineTo(-W * 0.2f, currentY + H * 0.12f)
            close()
        }
        val currentAlpha = 0.08f + sin(slowPulse * 3.14159f + i) * 0.04f
        drawPath(
            currentPath,
            Brush.verticalGradient(
                listOf(Color(0xFF00B0FF).copy(alpha = max(0.01f, currentAlpha)), Color.Transparent)
            )
        )
    }

    val floorY = H * 0.52f

    // === LAYER 2: Distant Horizon Silhouettes & Coral Reef Ridge ===
    val horizonPath = Path().apply {
        moveTo(0f, floorY)
        lineTo(W * 0.06f, floorY - H * 0.08f)
        lineTo(W * 0.14f, floorY - H * 0.04f)
        lineTo(W * 0.22f, floorY - H * 0.10f)
        lineTo(W * 0.30f, floorY - H * 0.03f)
        lineTo(W * 0.42f, floorY - H * 0.07f)
        lineTo(W * 0.55f, floorY - H * 0.03f)
        lineTo(W * 0.68f, floorY - H * 0.09f)
        lineTo(W * 0.82f, floorY - H * 0.05f)
        lineTo(W * 0.92f, floorY - H * 0.08f)
        lineTo(W, floorY - H * 0.03f)
        lineTo(W, floorY)
        close()
    }
    drawPath(horizonPath, Color(0xFF031424))

    listOf(0.26f to 0.07f, 0.48f to 0.05f, 0.72f to 0.08f).forEach { (xFrac, hFrac) ->
        val px = W * xFrac
        val pw = W * 0.022f
        val ph = H * hFrac
        drawRect(Color(0xFF062035), Offset(px - pw / 2f, floorY - ph), Size(pw, ph))
        drawRect(Color(0xFF092A45), Offset(px - pw / 2f, floorY - ph), Size(pw * 0.35f, ph))
    }

    listOf(0.12f, 0.38f, 0.64f, 0.88f).forEach { coralXFrac ->
        val cx = W * coralXFrac
        val cy = floorY - H * 0.03f
        drawCircle(Color(0xFFE03868).copy(alpha = 0.7f), radius = 8f, center = Offset(cx, cy))
        drawCircle(Color(0xFF00E0A0).copy(alpha = 0.6f), radius = 6f, center = Offset(cx + 10f, cy + 2f))
        drawCircle(Color(0xFF00D0FF).copy(alpha = 0.8f), radius = 4f, center = Offset(cx - 8f, cy + 4f))
    }

    // === LAYER 3: Perspective Tiled Floor & Sunken Temple Roof ===
    drawRect(Color(0xFF0A2438), Offset(0f, floorY), Size(W, H - floorY))

    val vpX = W * 0.35f
    val tileRows = 12
    repeat(tileRows) { i ->
        val p = i / tileRows.toFloat()
        val rowY = floorY + (H - floorY) * (p * p)

        drawLine(Color(0xFF144666), Offset(0f, rowY), Offset(W, rowY), strokeWidth = 1.2f)

        if (i > 1 && i % 2 == 0) {
            val cols = 6
            repeat(cols) { c ->
                val colFrac = (c + 0.5f) / cols
                val tileX = vpX + (colFrac - 0.5f) * W * (p * 2.2f + 0.8f)
                val motifSize = (9f * p + 3f)
                if (tileX in 0f..W && rowY >= floorY) {
                    drawRect(
                        Color(0xFF1D5A80),
                        Offset(tileX - motifSize / 2f, rowY - motifSize / 2f),
                        Size(motifSize, motifSize),
                        style = Stroke(1.5f)
                    )
                    drawCircle(
                        Color(0xFF00D5FF).copy(alpha = 0.6f),
                        radius = motifSize * 0.35f,
                        center = Offset(tileX, rowY)
                    )
                }
            }
        }
    }

    repeat(10) { col ->
        val startFrac = col / 9f
        val bottomX = vpX + (startFrac - 0.5f) * W * 3.0f
        drawLine(
            Color(0xFF144666),
            Offset(vpX + (startFrac - 0.5f) * W * 0.2f, floorY),
            Offset(bottomX, H),
            strokeWidth = 1.5f
        )
    }

    // Sunken Temple Roof Structure on Right Side
    val templeRoofPath = Path().apply {
        moveTo(W * 0.45f, H)
        lineTo(W * 1.05f, floorY + H * 0.08f)
        lineTo(W * 1.05f, H)
        close()
    }
    drawPath(
        templeRoofPath,
        Brush.verticalGradient(
            listOf(Color(0xFF126893), Color(0xFF093954), Color(0xFF031A2B))
        )
    )
    drawPath(templeRoofPath, Color(0xFF4CD8FF), style = Stroke(2.5f))

    val roofSteps = 10
    repeat(roofSteps) { step ->
        val frac = step / roofSteps.toFloat()
        val stepX1 = W * 0.45f + (W * 0.60f) * frac
        val stepY1 = H - (H - (floorY + H * 0.08f)) * frac

        drawLine(
            Color(0xFF4CD8FF).copy(alpha = 0.8f),
            Offset(stepX1, stepY1),
            Offset(W, stepY1 + (H - stepY1) * 0.2f),
            strokeWidth = 1.8f
        )

        val blockCount = 5
        repeat(blockCount) { b ->
            val bFrac = b / blockCount.toFloat()
            val bx = stepX1 + (W - stepX1) * bFrac
            val by = stepY1 + (H - stepY1) * (bFrac * 0.2f)
            drawLine(
                Color(0xFF0088CC).copy(alpha = 0.6f),
                Offset(bx, by),
                Offset(bx + 12f, by + (H * 0.05f)),
                strokeWidth = 1.2f
            )
            drawRect(
                Color(0xFF80EEFF).copy(alpha = 0.7f),
                Offset(bx - 3f, by - 3f),
                Size(6f, 6f)
            )
        }
    }

    // === LAYER 4: Hazard Pits & Ruined Barriers ===
    val pitPath = Path().apply {
        moveTo(W * 0.15f, floorY + H * 0.12f)
        lineTo(W * 0.36f, floorY + H * 0.09f)
        lineTo(W * 0.40f, floorY + H * 0.24f)
        lineTo(W * 0.18f, floorY + H * 0.28f)
        close()
    }
    drawPath(pitPath, Color(0xFF010610))
    drawPath(pitPath, Color(0xFF00E5FF).copy(alpha = 0.7f + slowPulse * 0.2f), style = Stroke(2.2f))

    drawPath(
        pitPath,
        Brush.radialGradient(
            listOf(Color(0xFF00E5FF).copy(alpha = 0.35f + medPulse * 0.15f), Color.Transparent),
            center = Offset(W * 0.27f, floorY + H * 0.18f),
            radius = W * 0.15f
        )
    )

    val wallPath = Path().apply {
        moveTo(W * 0.08f, floorY + H * 0.20f)
        lineTo(W * 0.22f, floorY + H * 0.10f)
        lineTo(W * 0.24f, floorY + H * 0.14f)
        lineTo(W * 0.10f, floorY + H * 0.26f)
        close()
    }
    drawPath(wallPath, Color(0xFF082C44))
    drawPath(wallPath, Color(0xFF00A080), style = Stroke(1.5f))

    // === LAYER 5: Architectural Foreground Structures (Grand Fluted Columns) ===
    val columns = listOf(
        SeaColumnSpec(xFrac = 0.16f, topYFrac = 0.06f, botYFrac = 0.96f, widthFrac = 0.11f, isBroken = true, hasGlowAura = true),
        SeaColumnSpec(xFrac = 0.42f, topYFrac = 0.18f, botYFrac = 0.68f, widthFrac = 0.052f, isBroken = true, hasGlowAura = false),
        SeaColumnSpec(xFrac = 0.65f, topYFrac = 0.12f, botYFrac = 0.60f, widthFrac = 0.045f, isBroken = false, hasGlowAura = false),
        SeaColumnSpec(xFrac = 0.85f, topYFrac = 0.08f, botYFrac = 0.54f, widthFrac = 0.038f, isBroken = false, hasGlowAura = false)
    )

    columns.forEach { col ->
        drawSeaColumn(col, W, H, slowPulse)
    }

    // === LAYER 6: Dynamic Caustic Light Rays & Particles ===
    repeat(7) { i ->
        val topX = W * (0.05f + i * 0.15f) + sin(scrollOffset * 0.02f + i * 1.3f) * 18f
        val rayWidth = 28f + (i % 3) * 10f
        val rayOffset = 90f + (i % 4) * 20f
        val rayPath = Path().apply {
            moveTo(topX, 0f)
            lineTo(topX + rayWidth, 0f)
            lineTo(topX + rayOffset + rayWidth, H)
            lineTo(topX + rayOffset, H)
            close()
        }
        val rayAlpha = 0.05f + sin(slowPulse * 3.14159f + i * 0.8f) * 0.035f + medPulse * 0.02f
        drawPath(
            rayPath,
            Brush.verticalGradient(
                listOf(
                    Color(0xFF40C0FF).copy(alpha = max(0.01f, rayAlpha)),
                    Color(0xFF00E5FF).copy(alpha = max(0.005f, rayAlpha * 0.5f)),
                    Color.Transparent
                )
            )
        )
    }

    val bubbleRng = java.util.Random(4242)
    repeat(35) { i ->
        val bx = (bubbleRng.nextFloat() * W + sin(scrollOffset * 0.05f + i) * 14f) % W
        val by = (H - ((scrollOffset * 0.8f + i * 32f) % H))
        val bRadius = 3f + (i % 5) * 1.5f
        
        drawCircle(
            Color(0xFF80E0FF).copy(alpha = 0.45f),
            radius = bRadius,
            center = Offset(bx, by),
            style = Stroke(1.5f)
        )
        drawCircle(
            Color.White.copy(alpha = 0.7f),
            radius = 1.2f,
            center = Offset(bx - bRadius * 0.35f, by - bRadius * 0.35f)
        )
    }

    val planktonRng = java.util.Random(9999)
    repeat(25) { i ->
        val px = (planktonRng.nextFloat() * W + cos(scrollOffset * 0.04f + i) * 22f) % W
        val py = (H - ((scrollOffset * 0.4f + i * 28f) % H))
        val pAlpha = 0.3f + sin(slowPulse * 3.14159f + i) * 0.25f
        
        drawCircle(
            Color(0xFF00F0FF).copy(alpha = max(0f, pAlpha)),
            radius = 2.2f + (i % 3) * 0.8f,
            center = Offset(px, py)
        )
    }

    // === LAYER 7: Atmospheric Vignette ===
    drawRect(
        Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0xEE020814)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.78f
        ),
        size = size
    )
}

private data class EarthPillarSpec(
    val xFrac: Float,
    val topYFrac: Float,
    val botYFrac: Float,
    val widthFrac: Float,
    val isStalactite: Boolean = false
)

private fun DrawScope.drawEarthPillar(
    pillar: EarthPillarSpec,
    canvasWidth: Float,
    canvasHeight: Float
) {
    val cx = canvasWidth * pillar.xFrac
    val cw = canvasWidth * pillar.widthFrac
    val topY = canvasHeight * pillar.topYFrac
    val botY = canvasHeight * pillar.botYFrac
    val ch = botY - topY

    if (ch <= 0f || cw <= 0f) return

    val highlightStone = Color(0xFF4A3C28)
    val baseStone = Color(0xFF2C2214)
    val shadowStone = Color(0xFF140D06)

    val shaftBrush = Brush.horizontalGradient(
        colors = listOf(highlightStone, baseStone, shadowStone),
        startX = cx - cw / 2f,
        endX = cx + cw / 2f
    )

    if (pillar.isStalactite) {
        val path = Path().apply {
            moveTo(cx - cw / 2f, topY)
            lineTo(cx + cw / 2f, topY)
            lineTo(cx + cw * 0.15f, botY)
            lineTo(cx - cw * 0.15f, botY)
            close()
        }
        drawPath(path, shaftBrush)
        drawPath(path, Color(0xFF0C0703), style = Stroke(1.5f))
    } else {
        val path = Path().apply {
            moveTo(cx - cw * 0.2f, topY)
            lineTo(cx + cw * 0.2f, topY)
            lineTo(cx + cw / 2f, botY)
            lineTo(cx - cw / 2f, botY)
            close()
        }
        drawPath(path, shaftBrush)
        drawPath(path, Color(0xFF0C0703), style = Stroke(1.5f))

        repeat(3) { i ->
            drawCircle(
                Color(0xFF2E3A18).copy(alpha = 0.7f),
                radius = cw * 0.25f,
                center = Offset(cx - cw * 0.25f + i * cw * 0.25f, botY - 6f)
            )
        }
    }

    val ridgeCount = 3
    repeat(ridgeCount) { i ->
        val rx = cx - cw * 0.25f + i * (cw * 0.5f / max(1, ridgeCount - 1))
        drawLine(
            Color(0xFF120B05).copy(alpha = 0.6f),
            Offset(rx, topY + 8f),
            Offset(rx, botY - 8f),
            strokeWidth = max(1f, cw * 0.08f)
        )
    }
}

private fun DrawScope.drawEarthCave(
    scrollOffset: Float,
    torchFlicker: Float,
    slowPulse: Float
) {
    val W = size.width
    val H = size.height

    // === LAYER 1: Subterranean Cavern Atmosphere & Overhang ===
    drawRect(
        Brush.verticalGradient(
            listOf(
                Color(0xFF0A0704),
                Color(0xFF181109),
                Color(0xFF281C0E),
                Color(0xFF181008),
                Color(0xFF0A0604)
            )
        ),
        size = size
    )

    val ceilingPath = Path().apply {
        moveTo(0f, 0f)
        lineTo(W, 0f)
        lineTo(W, H * 0.12f)
        lineTo(W * 0.88f, H * 0.18f)
        lineTo(W * 0.74f, H * 0.08f)
        lineTo(W * 0.62f, H * 0.22f)
        lineTo(W * 0.50f, H * 0.10f)
        lineTo(W * 0.38f, H * 0.20f)
        lineTo(W * 0.24f, H * 0.09f)
        lineTo(W * 0.12f, H * 0.16f)
        lineTo(0f, H * 0.08f)
        close()
    }
    drawPath(ceilingPath, Color(0xFF140C06))
    drawPath(ceilingPath, Color(0xFF28180C), style = Stroke(2f))

    val floorY = H * 0.52f

    // === LAYER 2: Distant Cavern Arch & Dark Tunnel Depth ===
    val tunnelPath = Path().apply {
        moveTo(W * 0.25f, floorY)
        quadraticTo(W * 0.50f, floorY - H * 0.35f, W * 0.75f, floorY)
        close()
    }
    drawPath(
        tunnelPath,
        Brush.radialGradient(
            listOf(Color(0xFF030201), Color(0xFF100B06), Color(0xFF1A120A)),
            center = Offset(W * 0.50f, floorY - H * 0.12f),
            radius = W * 0.28f
        )
    )
    drawPath(tunnelPath, Color(0xFF2A1D0E), style = Stroke(2.5f))

    listOf(0.28f to 0.06f, 0.42f to 0.04f, 0.58f to 0.05f, 0.70f to 0.07f).forEach { (xFrac, hFrac) ->
        val rx = W * xFrac
        val rw = W * 0.03f
        val rh = H * hFrac
        drawRect(Color(0xFF1A1108), Offset(rx - rw / 2f, floorY - rh), Size(rw, rh))
    }

    // === LAYER 3: Cracked Flagstone Floor & Stone Altar Platform ===
    drawRect(Color(0xFF22170E), Offset(0f, floorY), Size(W, H - floorY))

    val vpX = W * 0.40f
    val flagRows = 10
    repeat(flagRows) { r ->
        val p = r / flagRows.toFloat()
        val rowY = floorY + (H - floorY) * (p * p)

        drawLine(
            Color(0xFF0D0804),
            Offset(0f, rowY),
            Offset(W, rowY),
            strokeWidth = 2f
        )

        val slabs = 7
        repeat(slabs) { c ->
            val cFrac = (c + 0.5f) / slabs
            val slabX = vpX + (cFrac - 0.5f) * W * (p * 2.2f + 0.8f)
            val slabW = (28f * p + 8f)
            val slabH = (14f * p + 4f)
            if (slabX in 0f..W && rowY >= floorY) {
                drawRect(
                    Color(0xFF3B2E1C),
                    Offset(slabX - slabW / 2f, rowY - slabH / 2f),
                    Size(slabW, slabH)
                )
                drawRect(
                    Color(0xFF4C3C26),
                    Offset(slabX - slabW / 2f + 1f, rowY - slabH / 2f + 1f),
                    Size(slabW - 2f, slabH - 2f)
                )
                drawRect(
                    Color(0xFF120C06),
                    Offset(slabX - slabW / 2f, rowY - slabH / 2f),
                    Size(slabW, slabH),
                    style = Stroke(1.2f)
                )
            }
        }
    }

    repeat(8) { col ->
        val startFrac = col / 7f
        val bottomX = vpX + (startFrac - 0.5f) * W * 2.8f
        drawLine(
            Color(0xFF0D0804),
            Offset(vpX + (startFrac - 0.5f) * W * 0.2f, floorY),
            Offset(bottomX, H),
            strokeWidth = 1.8f
        )
    }

    val ledgePath = Path().apply {
        moveTo(W * 0.58f, H)
        lineTo(W * 1.05f, floorY + H * 0.12f)
        lineTo(W * 1.05f, H)
        close()
    }
    drawPath(
        ledgePath,
        Brush.verticalGradient(
            listOf(Color(0xFF382A1A), Color(0xFF261B0E), Color(0xFF160E06))
        )
    )
    drawPath(ledgePath, Color(0xFF5A462C), style = Stroke(2.2f))

    drawLine(
        Color(0xFF6E5638),
        Offset(W * 0.58f, H),
        Offset(W * 1.05f, floorY + H * 0.12f),
        strokeWidth = 3f
    )

    // Central Megalithic Stone Altar Ring
    val altarX = W * 0.50f
    val altarY = floorY - H * 0.02f
    val altarRadiusX = W * 0.14f
    val altarRadiusY = H * 0.07f

    val stoneCount = 12
    repeat(stoneCount) { i ->
        val angle = i * (PI.toFloat() * 2f / stoneCount)
        val sx = altarX + cos(angle) * altarRadiusX
        val sy = altarY + sin(angle) * altarRadiusY
        val sWidth = 16f
        val sHeight = 24f

        drawRect(
            Brush.verticalGradient(listOf(Color(0xFF5A4832), Color(0xFF2D2214))),
            Offset(sx - sWidth / 2f, sy - sHeight),
            Size(sWidth, sHeight)
        )
        drawRect(
            Color(0xFF140E08),
            Offset(sx - sWidth / 2f, sy - sHeight),
            Size(sWidth, sHeight),
            style = Stroke(1.2f)
        )
    }

    val glowFlicker = 0.7f + torchFlicker * 0.3f
    drawOval(
        Brush.radialGradient(
            listOf(
                Color(0xFFEEFF60).copy(alpha = glowFlicker),
                Color(0xFFFFB000).copy(alpha = glowFlicker * 0.7f),
                Color(0xFF885500).copy(alpha = glowFlicker * 0.3f),
                Color.Transparent
            ),
            center = Offset(altarX, altarY - 6f),
            radius = altarRadiusX * 1.2f
        ),
        topLeft = Offset(altarX - altarRadiusX * 0.8f, altarY - altarRadiusY * 0.8f - 6f),
        size = Size(altarRadiusX * 1.6f, altarRadiusY * 1.6f)
    )

    // === LAYER 4: Earth Fissures & Subterranean Glowing Light Rifts ===
    val fissurePath = Path().apply {
        moveTo(W * 0.22f, floorY + H * 0.18f)
        lineTo(W * 0.38f, floorY + H * 0.14f)
        lineTo(W * 0.44f, floorY + H * 0.28f)
        lineTo(W * 0.30f, floorY + H * 0.32f)
        close()
    }
    drawPath(fissurePath, Color(0xFF0A0603))
    drawPath(
        fissurePath,
        Color(0xFFFFD030).copy(alpha = 0.5f + torchFlicker * 0.3f),
        style = Stroke(2f)
    )

    listOf(
        Offset(W * 0.18f, floorY + H * 0.35f) to 14f,
        Offset(W * 0.28f, floorY + H * 0.22f) to 10f,
        Offset(W * 0.72f, floorY + H * 0.38f) to 16f,
        Offset(W * 0.82f, floorY + H * 0.26f) to 12f
    ).forEach { (pos, radius) ->
        drawCircle(Color(0xFF382A1A), radius = radius, center = pos)
        drawCircle(Color(0xFF5A442A), radius = radius * 0.6f, center = Offset(pos.x - radius * 0.3f, pos.y - radius * 0.3f))
        drawCircle(Color(0xFF120C06), radius = radius, center = pos, style = Stroke(1.2f))
    }

    // === LAYER 5: Natural Foreground Cave Pillars ===
    val pillars = listOf(
        EarthPillarSpec(xFrac = 0.10f, topYFrac = 0.04f, botYFrac = 0.96f, widthFrac = 0.14f, isStalactite = false),
        EarthPillarSpec(xFrac = 0.90f, topYFrac = 0.08f, botYFrac = 0.96f, widthFrac = 0.13f, isStalactite = false),
        EarthPillarSpec(xFrac = 0.32f, topYFrac = 0.00f, botYFrac = 0.32f, widthFrac = 0.06f, isStalactite = true),
        EarthPillarSpec(xFrac = 0.68f, topYFrac = 0.00f, botYFrac = 0.36f, widthFrac = 0.07f, isStalactite = true)
    )

    pillars.forEach { pillar ->
        drawEarthPillar(pillar, W, H)
    }

    // === LAYER 6: Dynamic Particles & Cavern Water Drips ===
    repeat(4) { i ->
        val dripX = W * (0.22f + i * 0.18f)
        val dripCycle = ((scrollOffset * 1.5f + i * 90f) % (floorY - H * 0.05f))
        val dripY = H * 0.15f + dripCycle

        drawCircle(Color(0xFF80D0FF).copy(alpha = 0.7f), radius = 2.5f, center = Offset(dripX, dripY))

        if (dripY > floorY - 20f) {
            val rippleR = ((dripY - (floorY - 20f)) * 0.8f).coerceAtMost(18f)
            val rippleAlpha = (1f - rippleR / 18f).coerceIn(0f, 0.6f)
            drawOval(
                Color(0xFF80D0FF).copy(alpha = rippleAlpha),
                topLeft = Offset(dripX - rippleR, floorY + 10f - rippleR * 0.3f),
                size = Size(rippleR * 2f, rippleR * 0.6f),
                style = Stroke(1.2f)
            )
        }
    }

    val dustRng = java.util.Random(1337)
    repeat(30) { i ->
        val dx = (dustRng.nextFloat() * W + sin(scrollOffset * 0.03f + i) * 20f) % W
        val dy = (H - ((scrollOffset * 0.6f + i * 28f) % H))
        val dAlpha = 0.2f + sin(slowPulse * 3.14159f + i) * 0.3f + torchFlicker * 0.2f

        drawCircle(
            Color(0xFFFFE060).copy(alpha = max(0f, dAlpha)),
            radius = 1.8f + (i % 3) * 0.8f,
            center = Offset(dx, dy)
        )
    }

    // === LAYER 7: Atmospheric Vignette & Warm Spotlight ===
    drawCircle(
        Color(0xFFFFD040).copy(alpha = 0.08f * torchFlicker),
        radius = W * 0.38f,
        center = Offset(W / 2f, floorY + H * 0.15f)
    )

    drawRect(
        Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0xF10A0604)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.76f
        ),
        size = size
    )
}

private data class CrystalSpireSpec(
    val xFrac: Float,
    val topYFrac: Float,
    val botYFrac: Float,
    val widthFrac: Float,
    val hasLanternTip: Boolean = false
)

private fun DrawScope.drawCrystalSpire(
    spire: CrystalSpireSpec,
    canvasWidth: Float,
    canvasHeight: Float,
    pulse: Float
) {
    val cx = canvasWidth * spire.xFrac
    val cw = canvasWidth * spire.widthFrac
    val topY = canvasHeight * spire.topYFrac
    val botY = canvasHeight * spire.botYFrac
    val ch = botY - topY

    if (ch <= 0f || cw <= 0f) return

    val lightFacet = Color(0xFFC0F0FF)
    val midFacet = Color(0xFF40A0D0)
    val darkFacet = Color(0xFF184070)
    val shadowFacet = Color(0xFF0B1A38)

    val leftPath = Path().apply {
        moveTo(cx, topY)
        lineTo(cx - cw / 2f, topY + 25f)
        lineTo(cx - cw / 2f, botY)
        lineTo(cx, botY)
        close()
    }
    drawPath(
        leftPath,
        Brush.horizontalGradient(listOf(lightFacet, midFacet), startX = cx - cw / 2f, endX = cx)
    )
    drawPath(leftPath, Color(0xFF0A2045), style = Stroke(1.2f))

    val rightPath = Path().apply {
        moveTo(cx, topY)
        lineTo(cx + cw / 2f, topY + 25f)
        lineTo(cx + cw / 2f, botY)
        lineTo(cx, botY)
        close()
    }
    drawPath(
        rightPath,
        Brush.horizontalGradient(listOf(darkFacet, shadowFacet), startX = cx, endX = cx + cw / 2f)
    )
    drawPath(rightPath, Color(0xFF0A2045), style = Stroke(1.2f))

    drawLine(
        Color(0xFFE0FFFF).copy(alpha = 0.8f),
        Offset(cx, topY),
        Offset(cx, botY),
        strokeWidth = 2f
    )

    if (spire.hasLanternTip) {
        val glowR = 12f + pulse * 4f
        drawCircle(
            Color(0xFF00E5FF).copy(alpha = 0.5f + pulse * 0.3f),
            radius = glowR * 1.8f,
            center = Offset(cx, topY - 10f)
        )
        drawCircle(
            Color(0xFFE0FFFF),
            radius = 6f,
            center = Offset(cx, topY - 10f)
        )
    }
}

private fun DrawScope.drawCrystalTower(
    scrollOffset: Float,
    slowPulse: Float,
    medPulse: Float
) {
    val W = size.width
    val H = size.height

    // === LAYER 1: Ethereal Aurora Sunset Sky & Swirling Clouds ===
    drawRect(
        Brush.verticalGradient(
            listOf(
                Color(0xFF321040),
                Color(0xFF6E1858),
                Color(0xFFC04070),
                Color(0xFFFF7080),
                Color(0xFFFFB0A0)
            )
        ),
        size = size
    )

    repeat(6) { i ->
        val cloudY = H * (0.05f + i * 0.08f)
        val cloudPath = Path().apply {
            moveTo(-W * 0.2f, cloudY)
            var x = -W * 0.2f
            while (x <= W * 1.2f) {
                val wave = sin((x + scrollOffset * 0.3f) * 0.006f + i) * (H * 0.04f)
                lineTo(x, cloudY + wave)
                x += 40f
            }
            lineTo(W * 1.2f, cloudY + H * 0.14f)
            lineTo(-W * 0.2f, cloudY + H * 0.14f)
            close()
        }
        val cloudAlpha = 0.12f + sin(slowPulse * 3.14159f + i) * 0.06f
        drawPath(
            cloudPath,
            Brush.verticalGradient(
                listOf(
                    Color(0xFFFFB0E0).copy(alpha = max(0.01f, cloudAlpha)),
                    Color(0xFF80E0FF).copy(alpha = max(0.005f, cloudAlpha * 0.5f)),
                    Color.Transparent
                )
            )
        )
    }

    val haloRadius = W * 0.35f
    val haloCenter = Offset(W * 0.50f, H * 0.18f)
    drawCircle(
        Brush.radialGradient(
            listOf(
                Color(0xFF80EEFF).copy(alpha = 0.25f + slowPulse * 0.15f),
                Color(0xFFFFB0E0).copy(alpha = 0.15f),
                Color.Transparent
            ),
            center = haloCenter,
            radius = haloRadius
        ),
        radius = haloRadius,
        center = haloCenter
    )

    val floorY = H * 0.52f

    // === LAYER 2: Majestic Central Crystal Spire / Tower ===
    val towerW = W * 0.18f
    val towerTopY = H * 0.02f
    val towerBotY = floorY + H * 0.05f
    val towerX = W * 0.50f

    val towerPath = Path().apply {
        moveTo(towerX, towerTopY)
        lineTo(towerX - towerW * 0.15f, towerTopY + H * 0.15f)
        lineTo(towerX - towerW / 2f, towerBotY)
        lineTo(towerX + towerW / 2f, towerBotY)
        lineTo(towerX + towerW * 0.15f, towerTopY + H * 0.15f)
        close()
    }
    drawPath(
        towerPath,
        Brush.horizontalGradient(
            listOf(Color(0xFFE8F8FF), Color(0xFF80C0E8), Color(0xFF3070A8), Color(0xFF103058)),
            startX = towerX - towerW / 2f,
            endX = towerX + towerW / 2f
        )
    )
    drawPath(towerPath, Color(0xFF0C2448), style = Stroke(1.8f))

    listOf(-0.45f, -0.28f, 0.28f, 0.45f).forEach { frac ->
        val px = towerX + towerW * frac
        val py = towerTopY + H * (0.12f + abs(frac) * 0.2f)
        val pw = towerW * 0.25f
        val ph = H * 0.25f

        val pinnaclePath = Path().apply {
            moveTo(px, py)
            lineTo(px - pw / 2f, py + ph)
            lineTo(px + pw / 2f, py + ph)
            close()
        }
        drawPath(pinnaclePath, Color(0xFF90D0F8).copy(alpha = 0.85f))
        drawPath(pinnaclePath, Color(0xFF0C2448), style = Stroke(1.2f))
    }

    drawLine(
        Color.White,
        Offset(towerX, 0f),
        Offset(towerX, towerTopY + H * 0.20f),
        strokeWidth = 3f
    )
    drawCircle(
        Color(0xFFE0FFFF).copy(alpha = 0.8f + slowPulse * 0.2f),
        radius = 16f,
        center = Offset(towerX, towerTopY + 10f)
    )

    // === LAYER 3: Mirror Tile Floor, Crystal Bridge & Gateway Altar ===
    drawRect(Color(0xFF102844), Offset(0f, floorY), Size(W, H - floorY))

    val vpX = W * 0.35f
    val tileRows = 12
    repeat(tileRows) { i ->
        val p = i / tileRows.toFloat()
        val rowY = floorY + (H - floorY) * (p * p)

        drawLine(Color(0xFF245078), Offset(0f, rowY), Offset(W, rowY), strokeWidth = 1.2f)

        if (i > 1 && i % 2 == 0) {
            val cols = 6
            repeat(cols) { c ->
                val colFrac = (c + 0.5f) / cols
                val tileX = vpX + (colFrac - 0.5f) * W * (p * 2.2f + 0.8f)
                val motifSize = (10f * p + 3f)
                if (tileX in 0f..W && rowY >= floorY) {
                    drawRect(
                        Color(0xFF3878A8),
                        Offset(tileX - motifSize / 2f, rowY - motifSize / 2f),
                        Size(motifSize, motifSize),
                        style = Stroke(1.5f)
                    )
                    drawCircle(
                        Color(0xFF80EEFF).copy(alpha = 0.7f),
                        radius = motifSize * 0.35f,
                        center = Offset(tileX, rowY)
                    )
                }
            }
        }
    }

    repeat(10) { col ->
        val startFrac = col / 9f
        val bottomX = vpX + (startFrac - 0.5f) * W * 3.0f
        drawLine(
            Color(0xFF245078),
            Offset(vpX + (startFrac - 0.5f) * W * 0.2f, floorY),
            Offset(bottomX, H),
            strokeWidth = 1.5f
        )
    }

    val bridgePath = Path().apply {
        moveTo(W * 0.55f, H)
        lineTo(W * 1.05f, floorY + H * 0.10f)
        lineTo(W * 1.05f, H)
        close()
    }
    drawPath(
        bridgePath,
        Brush.verticalGradient(
            listOf(Color(0xFF00C0FF), Color(0xFF0060C0), Color(0xFF002060))
        )
    )
    drawPath(bridgePath, Color(0xFF80EEFF), style = Stroke(2.5f))

    val altarX = W * 0.50f
    val altarY = floorY - H * 0.02f
    val altarW = W * 0.14f
    val altarH = H * 0.22f

    drawRect(Color(0xFF183858), Offset(altarX - altarW / 2f, altarY - altarH), Size(18f, altarH))
    drawRect(Color(0xFF183858), Offset(altarX + altarW / 2f - 18f, altarY - altarH), Size(18f, altarH))
    drawRect(Color(0xFF80E0FF), Offset(altarX - altarW / 2f, altarY - altarH), Size(altarW, 12f))

    val hexPath = Path().apply {
        val radius = 18f
        val cy = altarY - altarH * 0.55f
        repeat(6) { i ->
            val angle = i * (PI.toFloat() / 3f)
            val hx = altarX + cos(angle) * radius
            val hy = cy + sin(angle) * radius
            if (i == 0) moveTo(hx, hy) else lineTo(hx, hy)
        }
        close()
    }
    val pulseAlpha = 0.6f + slowPulse * 0.4f
    drawPath(hexPath, Color(0xFF00E5FF).copy(alpha = pulseAlpha))
    drawPath(hexPath, Color.White, style = Stroke(2f))

    // === LAYER 4: Prismatic Crystal Light Beams & Crevices ===
    val sweepX = ((scrollOffset * 2.2f) % (W + 500f)) - 250f
    val sweepPath = Path().apply {
        moveTo(sweepX, 0f)
        lineTo(sweepX + 160f, 0f)
        lineTo(sweepX - 40f, H)
        lineTo(sweepX - 200f, H)
        close()
    }
    drawPath(
        sweepPath,
        Brush.horizontalGradient(
            listOf(
                Color.Transparent,
                Color(1f, 1f, 1f, 0.10f + medPulse * 0.05f),
                Color(0.4f, 0.9f, 1f, 0.12f),
                Color.Transparent
            )
        )
    )

    val crevicePath = Path().apply {
        moveTo(W * 0.18f, floorY + H * 0.15f)
        lineTo(W * 0.36f, floorY + H * 0.12f)
        lineTo(W * 0.40f, floorY + H * 0.26f)
        lineTo(W * 0.22f, floorY + H * 0.30f)
        close()
    }
    drawPath(crevicePath, Color(0xFF061428))
    drawPath(crevicePath, Color(0xFF00E5FF).copy(alpha = 0.7f + slowPulse * 0.2f), style = Stroke(2f))

    // === LAYER 5: Architectural Foreground Structures (Grand Crystal Spire Columns) ===
    val spires = listOf(
        CrystalSpireSpec(xFrac = 0.12f, topYFrac = 0.08f, botYFrac = 0.96f, widthFrac = 0.11f, hasLanternTip = false),
        CrystalSpireSpec(xFrac = 0.88f, topYFrac = 0.10f, botYFrac = 0.96f, widthFrac = 0.11f, hasLanternTip = false),
        CrystalSpireSpec(xFrac = 0.28f, topYFrac = 0.22f, botYFrac = 0.62f, widthFrac = 0.038f, hasLanternTip = true),
        CrystalSpireSpec(xFrac = 0.72f, topYFrac = 0.22f, botYFrac = 0.62f, widthFrac = 0.038f, hasLanternTip = true)
    )

    spires.forEach { spire ->
        drawCrystalSpire(spire, W, H, slowPulse)
    }

    // === LAYER 6: Dynamic Particles & Prismatic Sparkles / Light Flares ===
    val sparkRng = java.util.Random(7777)
    repeat(35) { i ->
        val sx = (sparkRng.nextFloat() * W + sin(scrollOffset * 0.04f + i) * 16f) % W
        val sy = (H - ((scrollOffset * 0.7f + i * 32f) % H))
        val sAlpha = max(0f, 0.3f + sin(scrollOffset * 0.08f + i) * 0.5f)

        drawLine(
            Color(0xFFE0FFFF).copy(alpha = sAlpha),
            Offset(sx - 5f, sy),
            Offset(sx + 5f, sy),
            strokeWidth = 1.8f
        )
        drawLine(
            Color(0xFFE0FFFF).copy(alpha = sAlpha),
            Offset(sx, sy - 5f),
            Offset(sx, sy + 5f),
            strokeWidth = 1.8f
        )
    }

    // === LAYER 7: Atmospheric Vignette ===
    drawRect(
        Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0xDD120420)),
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.78f
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
