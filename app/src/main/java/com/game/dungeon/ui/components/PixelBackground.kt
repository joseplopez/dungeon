package com.game.dungeon.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.game.dungeon.ui.theme.BgDarkest

@Composable
fun DungeonBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "dungeon_bg")
    // Torch flicker animation
    val torchFlicker by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "torch_flicker"
    )
    // Slow parallax scroll
    val scrollOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 64f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scroll_offset"
    )

    Canvas(Modifier.fillMaxSize()) {
        val W = size.width
        val H = size.height

        // === LAYER 1: Far background — deep dark gradient ===
        drawRect(Color(0xFF0A0614), size = size) // base dark

        // === LAYER 2: Distant stone arch (large background structure) ===
        val archColor = Color(0xFF12091E)
        // Left pillar
        drawRect(archColor, Offset(W * 0.08f, H * 0.1f), Size(W * 0.06f, H * 0.85f))
        drawRect(Color(0xFF1A0F2A), Offset(W * 0.085f, H * 0.1f), Size(W * 0.04f, H * 0.83f))
        // Right pillar
        drawRect(archColor, Offset(W * 0.86f, H * 0.1f), Size(W * 0.06f, H * 0.85f))
        drawRect(Color(0xFF1A0F2A), Offset(W * 0.865f, H * 0.1f), Size(W * 0.04f, H * 0.83f))
        // Arch top connecting beam
        drawRect(archColor, Offset(W * 0.08f, H * 0.1f), Size(W * 0.84f, H * 0.08f))

        // === LAYER 3: Stone tile floor ===
        val floorY = H * 0.72f
        val tileW = W / 20f
        val tileH = H * 0.06f
        var col = 0
        var x = -(scrollOffset % (tileW * 2))
        while (x < W + tileW) {
            val tileColor = if (col % 2 == 0) Color(0xFF1C1230) else Color(0xFF160E26)
            drawRect(tileColor, Offset(x, floorY), Size(tileW - 2f, tileH))
            // Grout lines
            drawRect(Color(0xFF0A0614), Offset(x + tileW - 2f, floorY), Size(2f, tileH))
            drawRect(Color(0xFF0A0614), Offset(x, floorY + tileH - 1f), Size(tileW, 1f))
            x += tileW
            col++
        }
        // Floor shadow gradient strip
        drawRect(Color(0x880A0614), Offset(0f, floorY - 10f), Size(W, 10f))

        // === LAYER 4: Stone wall tiles (mid layer) ===
        val wallBottom = H * 0.72f
        val wallTileH = H * 0.055f
        val wallTileW = W / 24f
        var wy = H * 0.18f
        var row = 0
        while (wy < wallBottom) {
            val offsetX = if (row % 2 == 0) 0f else wallTileW / 2f
            var wx = offsetX - (scrollOffset * 0.3f % wallTileW)
            while (wx < W) {
                val brightness = if ((row + wx.toInt()) % 3 == 0) 0.14f else 0.10f
                drawRect(
                    Color(brightness, brightness * 0.7f, brightness * 1.2f, 1f),
                    Offset(wx, wy),
                    Size(wallTileW - 1f, wallTileH - 1f)
                )
                // Mortar
                drawRect(Color(0xFF080412), Offset(wx, wy + wallTileH - 1f), Size(wallTileW, 1f))
                wx += wallTileW
            }
            wy += wallTileH
            row++
        }

        // === LAYER 5: Wall decorations — chains ===
        // Left chain
        val chainX = W * 0.22f
        for (i in 0..8) {
            val cy = H * 0.15f + i * 22f
            drawRect(Color(0xFF555544), Offset(chainX, cy), Size(6f, 10f))
            drawRect(Color(0xFF444433), Offset(chainX + 2f, cy + 2f), Size(2f, 6f))
        }
        // Right chain
        val chainX2 = W * 0.78f
        for (i in 0..8) {
            val cy = H * 0.15f + i * 22f
            drawRect(Color(0xFF555544), Offset(chainX2, cy), Size(6f, 10f))
            drawRect(Color(0xFF444433), Offset(chainX2 + 2f, cy + 2f), Size(2f, 6f))
        }

        // === LAYER 6: Torches (left and right wall) ===
        val torchPositions = listOf(W * 0.18f, W * 0.82f)
        torchPositions.forEach { tx ->
            val ty = H * 0.3f
            // Torch bracket
            drawRect(Color(0xFF554400), Offset(tx - 4f, ty + 8f), Size(8f, 16f))
            drawRect(Color(0xFF776600), Offset(tx - 2f, ty + 10f), Size(4f, 12f))
            // Flame glow (flicker using animation)
            val glowAlpha = torchFlicker * 0.4f
            drawCircle(Color(1f, 0.4f, 0f, glowAlpha), radius = 40f, center = Offset(tx, ty))
            drawCircle(Color(1f, 0.7f, 0f, glowAlpha * 0.6f), radius = 25f, center = Offset(tx, ty))
            // Flame body
            drawRect(
                Color(1f, 0.5f, 0f, torchFlicker),
                Offset(tx - 4f, ty - 14f), Size(8f, 16f)
            )
            drawRect(
                Color(1f, 0.8f, 0f, torchFlicker),
                Offset(tx - 2f, ty - 18f), Size(4f, 12f)
            )
            drawRect(
                Color(1f, 1f, 0.6f, torchFlicker * 0.8f),
                Offset(tx - 1f, ty - 22f), Size(2f, 8f)
            )
        }

        // === LAYER 7: Ambient light pools on floor from torches ===
        torchPositions.forEach { tx ->
            drawCircle(
                Color(1f, 0.4f, 0f, torchFlicker * 0.08f),
                radius = W * 0.18f, center = Offset(tx, H * 0.72f)
            )
        }

        // === LAYER 8: Vignette (dark corners) ===
        drawRect(
            Brush.radialGradient(
                colors = listOf(Color.Transparent, Color(0xAA000000)),
                center = Offset(W / 2f, H / 2f), radius = W * 0.7f
            ), size = size
        )
    }
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
