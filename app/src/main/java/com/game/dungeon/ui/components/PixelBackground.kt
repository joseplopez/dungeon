package com.game.dungeon.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

// ==========================================
// PRE-ALLOCATED COLOR CONSTANTS & PALETTES
// ==========================================
private val ColorCorneliaBgTop = Color(0xFF0A0814)
private val ColorCorneliaBgMid = Color(0xFF1A1228)
private val ColorCorneliaBgBot = Color(0xFF0D0A18)
private val ColorCorneliaTorchGlow = Color(0.8f, 0.4f, 0.1f, 0.08f)
private val ColorCorneliaColumn = Color(0xFF121020)
private val ColorCorneliaWindow = Color(0.4f, 0.5f, 0.7f, 0.15f)
private val ColorCorneliaTileEven = Color(0xFF1C1628)
private val ColorCorneliaTileOdd = Color(0xFF181420)
private val ColorCorneliaHighlight = Color(0xFF2A2038)
private val ColorCorneliaShadow = Color(0xFF0D0A14)
private val ColorCorneliaBannerPole = Color(0xFF8B6914)
private val ColorCorneliaBannerBody = Color(0xFF8B0000)
private val ColorCorneliaBannerGold = Color(0xFFFFD700)
private val ColorCorneliaBannerHi = Color(0xFFCC2222)
private val ColorCorneliaBracket = Color(0xFF3A2A10)
private val ColorCorneliaBracketInner = Color(0xFF5A4018)

private val ColorChaosVoidBase = Color(0xFF050308)
private val ColorChaosPillar = Color(0xFF0F0A18)
private val ColorChaosPillarHi = Color(0xFF1A1230)
private val ColorChaosTileEven = Color(0xFF140D20)
private val ColorChaosTileOdd = Color(0xFF1A1228)

private val ColorGurguTop = Color(0xFF0A0400)
private val ColorGurguMid = Color(0xFF1A0800)
private val ColorGurguBot = Color(0xFF2A0E00)
private val ColorGurguMtn = Color(0xFF1A0C04)
private val ColorGurguTileEven = Color(0xFF1A0C04)
private val ColorGurguTileOdd = Color(0xFF140A02)

private val ColorSeaTop = Color(0xFF0D334D)
private val ColorSeaMid = Color(0xFF051D2D)
private val ColorSeaBot = Color(0xFF020C14)
private val ColorSeaWash = Color(0f, 0.3f, 0.5f, 0.12f)
private val ColorSeaPillar = Color(0xFF0D1E2A)
private val ColorSeaCoral1 = Color(0.8f, 0.3f, 0.3f, 0.6f)
private val ColorSeaCoral2 = Color(0.9f, 0.5f, 0.1f, 0.5f)
private val ColorSeaTileEven = Color(0xFF0D1E28)
private val ColorSeaTileOdd = Color(0xFF0A1820)
private val ColorSeaAlgae = Color(0.1f, 0.5f, 0.2f, 0.25f)

private val ColorEarthTop = Color(0xFF140D08)
private val ColorEarthMid = Color(0xFF22160C)
private val ColorEarthBot = Color(0xFF100A05)
private val ColorEarthCaveBound = Color(0xFF1A1008)
private val ColorEarthStalactite = Color(0xFF281A0E)
private val ColorEarthStalagmite = Color(0xFF22140A)
private val ColorEarthWaterDrip = Color(0.4f, 0.6f, 0.8f, 0.7f)

private val ColorCrystalTop = Color(0xFF0D1B2A)
private val ColorCrystalMid = Color(0xFF1B2A4A)
private val ColorCrystalBot = Color(0xFF0D1B2A)
private val ColorCrystalLeft = Color(0xFF0A2030)
private val ColorCrystalRight = Color(0xFF1A5080)

private val ColorMysidianTop = Color(0xFF060514)
private val ColorMysidianMid = Color(0xFF120E30)
private val ColorMysidianBot = Color(0xFF080618)
private val ColorMysidianShelf = Color(0xFF181024)

private val ColorPandaemoniumTop = Color(0xFF04120A)
private val ColorPandaemoniumMid = Color(0xFF0A2214)
private val ColorPandaemoniumBot = Color(0xFF020804)
private val ColorPandaemoniumBoneOuter = Color(0xFF889988)
private val ColorPandaemoniumBoneInner = Color(0xFF556655)

private val ColorMountOrdealsTop = Color(0xFF1A0A20)
private val ColorMountOrdealsMid = Color(0xFF3A1C28)
private val ColorMountOrdealsBot = Color(0xFF5A3028)
private val ColorMountOrdealsRidge = Color(0xFF281420)

private val ColorBaronTop = Color(0xFF080D1A)
private val ColorBaronMid = Color(0xFF101A2E)
private val ColorBaronBot = Color(0xFF060912)
private val ColorBaronPanelBase = Color(0xFF142035)
private val ColorBaronPanelInner = Color(0xFF20304D)
private val ColorBaronRivet = Color(0xFF88A0C0)

private val ColorAncientTop = Color(0xFF1A140A)
private val ColorAncientMid = Color(0xFF2E2210)
private val ColorAncientBot = Color(0xFF140F06)
private val ColorAncientSand = Color(0xFF3E3018)

private val ColorNarsheTop = Color(0xFF060810)
private val ColorNarsheMid = Color(0xFF0A0E14)
private val ColorNarsheBot = Color(0xFF04060A)
private val ColorNarsheBeam = Color(0xFF3A2510)
private val ColorNarsheBeamHi = Color(0xFF5A3A18)
private val ColorNarsheIcicle = Color(0.6f, 0.8f, 1f, 0.7f)

private val ColorMagitekBg = Color(0xFF060808)
private val ColorMagitekWash = Color(0f, 0.3f, 0.1f, 0.04f)
private val ColorMagitekPipeBase = Color(0xFF1A2020)
private val ColorMagitekPipeHi = Color(0xFF3A4040)

private val ColorFloatingTop = Color(0xFF0A1830)
private val ColorFloatingMid = Color(0xFF1A3050)
private val ColorFloatingBot = Color(0xFF081020)
private val ColorFloatingCloud = Color(0.8f, 0.85f, 0.9f, 0.25f)

private val ColorMidgarTop = Color(0xFF06100A)
private val ColorMidgarMid = Color(0xFF0D2015)
private val ColorMidgarBot = Color(0xFF040805)
private val ColorMidgarArch = Color(0.1f, 0.3f, 0.15f, 0.2f)
private val ColorMidgarSludgeBase = Color(0xFF10300A)

private val ColorShinraBg = Color(0xFF020408)
private val ColorShinraBldg = Color(0xFF060B14)
private val ColorShinraNeonBlue = Color(0f, 0.6f, 1f, 0.7f)
private val ColorShinraNeonGlow = Color(0f, 0.4f, 0.8f, 0.08f)

private val ColorCraterTop = Color(0xFF020614)
private val ColorCraterMid = Color(0xFF081028)
private val ColorCraterBot = Color(0xFF02040A)

private val ColorGoldenSaucerTop = Color(0xFF12041A)
private val ColorGoldenSaucerMid = Color(0xFF280838)
private val ColorGoldenSaucerBot = Color(0xFF0E0214)

private val ColorBevelleTop = Color(0xFF080F1A)
private val ColorBevelleMid = Color(0xFF101C2E)
private val ColorBevelleBot = Color(0xFF040810)
private val ColorBevelleColumnOuter = Color(0xFF303848)
private val ColorBevelleColumnInner = Color(0xFF485468)

private val ColorOmegaBg = Color(0xFF020410)

private val ColorSinTop = Color(0xFF1A0408)
private val ColorSinMid = Color(0xFF380810)
private val ColorSinBot = Color(0xFF100204)
private val ColorSinRibOuter = Color(0xFF551111)
private val ColorSinRibInner = Color(0xFF882222)

private val ColorGenericBg = Color(0xFF0A0614)
private val ColorGenericArch = Color(0xFF12091E)
private val ColorGenericArchHi = Color(0xFF1A0F2A)

// Static FloatArrays for layout iterations without list allocations
private val ArrayCorneliaColumns = floatArrayOf(0.12f, 0.35f, 0.65f, 0.88f)
private val ArrayCorneliaBanners = floatArrayOf(0.2f, 0.5f, 0.8f)
private val ArrayCorneliaTorches = floatArrayOf(0.15f, 0.85f)
private val ArraySeaPillars = floatArrayOf(0.15f, 0.75f)
private val ArrayEarthStalactites = floatArrayOf(0.1f, 0.25f, 0.45f, 0.65f, 0.85f)
private val ArrayEarthStalagmites = floatArrayOf(0.18f, 0.55f, 0.78f)
private val ArrayNarshePosts = floatArrayOf(0.15f, 0.35f, 0.65f, 0.88f)
private val ArrayNarsheIcicles = floatArrayOf(0.08f, 0.25f, 0.45f, 0.75f, 0.92f)
private val ArrayMagitekPipes = floatArrayOf(0.2f, 0.35f, 0.5f)
private val ArrayMagitekConduits = floatArrayOf(0.25f, 0.75f)
private val ArrayMidgarArches = floatArrayOf(0.2f, 0.4f, 0.6f, 0.8f)
private val ArrayShinraNeon = floatArrayOf(0.2f, 0.4f, 0.6f)
private val ArrayBevelleColumns = floatArrayOf(0.12f, 0.38f, 0.62f, 0.88f)
private val ArraySinRibs = floatArrayOf(0.15f, 0.85f)
private val ArrayGenericTorches = floatArrayOf(0.18f, 0.82f)

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

    // Reusable Path instances remembered across composable recompositions to ensure 0 allocations in DrawScope
    val reusablePath = remember { Path() }
    val reusablePath2 = remember { Path() }

    Canvas(Modifier.fillMaxSize()) {
        when (effectiveBiome) {
            BiomeType.CORNELIA_CASTLE -> drawCorneliaCastle(scrollOffset, torchFlicker, reusablePath)
            BiomeType.CHAOS_SHRINE -> drawChaosShrine(scrollOffset, slowPulse, medPulse, reusablePath)
            BiomeType.GURGU_VOLCANO -> drawGurguVolcano(scrollOffset, torchFlicker, medPulse, reusablePath, reusablePath2)
            BiomeType.SEA_SHRINE -> drawSeaShrine(scrollOffset, slowPulse, reusablePath)
            BiomeType.EARTH_CAVE -> drawEarthCave(scrollOffset, torchFlicker, reusablePath)
            BiomeType.CRYSTAL_TOWER -> drawCrystalTower(scrollOffset, slowPulse, fastTick, reusablePath, reusablePath2)
            BiomeType.MYSIDIAN_TOWER -> drawMysidianTower(scrollOffset, slowPulse)
            BiomeType.PANDAEMONIUM -> drawPandaemonium(scrollOffset, medPulse)
            BiomeType.MOUNT_ORDEALS -> drawMountOrdeals(scrollOffset, slowPulse, reusablePath)
            BiomeType.BARON_CASTLE -> drawBaronCastle(scrollOffset, torchFlicker)
            BiomeType.ANCIENT_CASTLE -> drawAncientCastle(scrollOffset, slowPulse, reusablePath)
            BiomeType.NARSHE_MINES -> drawNarsheMines(scrollOffset, torchFlicker, reusablePath)
            BiomeType.MAGITEK_FACTORY -> drawMagitekFactory(scrollOffset, medPulse)
            BiomeType.KEFKA_TOWER -> drawKefkaTower(scrollOffset, slowPulse)
            BiomeType.FLOATING_CONTINENT -> drawFloatingContinent(scrollOffset, slowPulse)
            BiomeType.MIDGAR_SEWERS -> drawMidgarSewers(scrollOffset, torchFlicker)
            BiomeType.SHINRA_BUILDING -> drawShinraBuilding(scrollOffset, fastTick)
            BiomeType.NORTHERN_CRATER -> drawNorthernCrater(scrollOffset, slowPulse, reusablePath)
            BiomeType.GOLDEN_SAUCER -> drawGoldenSaucer(scrollOffset, fastTick)
            BiomeType.BEVELLE_TEMPLE -> drawBevelleTemple(scrollOffset, slowPulse, reusablePath)
            BiomeType.OMEGA_RUINS -> drawOmegaRuins(scrollOffset, fastTick)
            BiomeType.SIN_INTERIOR -> drawSinInterior(scrollOffset, slowPulse, medPulse)
            BiomeType.GENERIC_DUNGEON -> drawGenericDungeon(scrollOffset, torchFlicker)
        }
    }
}

// ==========================================
// BIOME 1: CORNELIA_CASTLE
// ==========================================
private fun DrawScope.drawCorneliaCastle(scrollOffset: Float, torchFlicker: Float, path: Path) {
    val W = size.width
    val H = size.height

    // L1: Deep Background Atmosphere
    drawRect(
        Brush.verticalGradient(
            0.0f to ColorCorneliaBgTop,
            0.5f to ColorCorneliaBgMid,
            1.0f to ColorCorneliaBgBot
        )
    )
    drawCircle(
        ColorCorneliaTorchGlow,
        radius = W * 0.7f,
        center = Offset(W / 2f, H)
    )

    // L2: Distant Silhouette Structures
    val colW = W * 0.06f
    val colH = H * 0.75f
    val colY = H * 0.08f
    for (i in ArrayCorneliaColumns.indices) {
        val cx = W * ArrayCorneliaColumns[i]
        drawRect(ColorCorneliaColumn, Offset(cx, colY), Size(colW, colH))
        // Barred windows
        drawRect(ColorCorneliaWindow, Offset(cx + colW * 0.2f, colY + colH * 0.2f), Size(colW * 0.6f, colH * 0.15f))
        drawRect(ColorCorneliaWindow, Offset(cx + colW * 0.2f, colY + colH * 0.45f), Size(colW * 0.6f, colH * 0.15f))
        // Crenellations
        drawRect(ColorCorneliaColumn, Offset(cx, colY - 12f), Size(colW * 0.25f, 12f))
        drawRect(ColorCorneliaColumn, Offset(cx + colW * 0.75f, colY - 12f), Size(colW * 0.25f, 12f))
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
            val baseColor = if ((row + (wx / tileW).toInt()) % 2 == 0) ColorCorneliaTileEven else ColorCorneliaTileOdd
            drawRect(baseColor, Offset(wx, wy), Size(tileW - 1f, tileH - 1f))
            drawRect(ColorCorneliaHighlight, Offset(wx, wy), Size(tileW - 1f, 1f)) // highlight
            drawRect(ColorCorneliaShadow, Offset(wx, wy + tileH - 1f), Size(tileW - 1f, 1f)) // shadow
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L4: Hanging Royal Banners
    for (i in ArrayCorneliaBanners.indices) {
        val bx = W * ArrayCorneliaBanners[i]
        // Pole
        drawRect(ColorCorneliaBannerPole, Offset(bx - 20f, 0f), Size(40f, 12f))
        // Body
        val bW = 36f
        val bH = 110f
        drawRect(ColorCorneliaBannerBody, Offset(bx - bW / 2f, 12f), Size(bW, bH))
        // Bottom tip
        path.reset()
        path.moveTo(bx - bW / 2f, 12f + bH)
        path.lineTo(bx + bW / 2f, 12f + bH)
        path.lineTo(bx, 12f + bH + 20f)
        path.close()
        drawPath(path, ColorCorneliaBannerBody)
        // Gold stripe
        drawRect(ColorCorneliaBannerGold, Offset(bx - 3f, 12f), Size(6f, bH + 10f))
        // Left highlight
        drawRect(ColorCorneliaBannerHi, Offset(bx - bW / 2f, 12f), Size(2f, bH + 10f))
    }

    // L5: Torches
    for (i in ArrayCorneliaTorches.indices) {
        val tx = W * ArrayCorneliaTorches[i]
        val ty = H * 0.32f
        drawRect(ColorCorneliaBracket, Offset(tx - 6f, ty), Size(12f, 20f))
        drawRect(ColorCorneliaBracketInner, Offset(tx - 3f, ty + 2f), Size(6f, 16f))

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
private fun DrawScope.drawChaosShrine(scrollOffset: Float, slowPulse: Float, medPulse: Float, path: Path) {
    val W = size.width
    val H = size.height

    // L1: Void Atmosphere
    drawRect(ColorChaosVoidBase)
    drawCircle(
        Brush.radialGradient(
            0.0f to Color(0.4f, 0f, 0.6f, slowPulse * 0.25f),
            1.0f to Color.Transparent,
            center = Offset(W / 2f, H / 2f),
            radius = W * 0.55f
        ),
        radius = W * 0.55f,
        center = Offset(W / 2f, H / 2f)
    )

    // L2: Shattered Pillars
    // Left broken pillar
    drawRect(ColorChaosPillar, Offset(W * 0.1f, 0f), Size(W * 0.08f, H * 0.6f))
    drawRect(ColorChaosPillarHi, Offset(W * 0.1f, 0f), Size(W * 0.03f, H * 0.6f))
    // Center fallen pillar section
    drawRect(ColorChaosPillar, Offset(W * 0.45f, H * 0.5f), Size(W * 0.1f, H * 0.22f))
    // Right pillar section
    drawRect(ColorChaosPillar, Offset(W * 0.8f, 0f), Size(W * 0.08f, H * 0.35f))
    drawRect(ColorChaosPillar, Offset(W * 0.8f, H * 0.45f), Size(W * 0.08f, H * 0.27f))

    // L3: Cracked Stone Walls
    val floorY = H * 0.72f
    val tileW = W / 18f
    val tileH = H * 0.05f
    var wy = H * 0.15f
    var row = 0
    while (wy < floorY) {
        var wx = -(scrollOffset * 0.35f % tileW)
        while (wx < W + tileW) {
            val c = if ((row + (wx / tileW).toInt()) % 2 == 0) ColorChaosTileEven else ColorChaosTileOdd
            drawRect(c, Offset(wx, wy), Size(tileW - 1f, tileH - 1f))

            // Cracks glowing purple
            if ((row + (wx / tileW).toInt()) % 5 == 0) {
                path.reset()
                path.moveTo(wx + 2f, wy + 2f)
                path.lineTo(wx + tileW * 0.4f, wy + tileH * 0.5f)
                path.lineTo(wx + tileW * 0.3f, wy + tileH * 0.8f)
                path.lineTo(wx + tileW * 0.8f, wy + tileH - 2f)
                drawPath(path, Color(0.6f, 0f, 0.8f, medPulse * 0.6f + 0.2f), style = Stroke(width = 2f))
            }
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L4: Void Rifts
    val rx1 = W * 0.25f
    val ry1 = H * 0.3f
    val rx2 = W * 0.5f
    val ry2 = H * 0.25f
    val rx3 = W * 0.75f
    val ry3 = H * 0.35f

    drawCircle(Color(0.6f, 0f, 0.9f, 0.3f + slowPulse * 0.2f), radius = 24f, center = Offset(rx1, ry1), style = Stroke(width = 3f))
    drawCircle(Color(0.2f, 0f, 0.4f, 0.6f), radius = 16f, center = Offset(rx1, ry1))
    drawCircle(Color(0.8f, 0.4f, 1f, medPulse), radius = 6f, center = Offset(rx1, ry1))

    drawCircle(Color(0.6f, 0f, 0.9f, 0.3f + slowPulse * 0.2f), radius = 24f, center = Offset(rx2, ry2), style = Stroke(width = 3f))
    drawCircle(Color(0.2f, 0f, 0.4f, 0.6f), radius = 16f, center = Offset(rx2, ry2))
    drawCircle(Color(0.8f, 0.4f, 1f, medPulse), radius = 6f, center = Offset(rx2, ry2))

    drawCircle(Color(0.6f, 0f, 0.9f, 0.3f + slowPulse * 0.2f), radius = 24f, center = Offset(rx3, ry3), style = Stroke(width = 3f))
    drawCircle(Color(0.2f, 0f, 0.4f, 0.6f), radius = 16f, center = Offset(rx3, ry3))
    drawCircle(Color(0.8f, 0.4f, 1f, medPulse), radius = 6f, center = Offset(rx3, ry3))

    // L5: Floating Debris
    for (i in 0 until 15) {
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
private fun DrawScope.drawGurguVolcano(
    scrollOffset: Float,
    torchFlicker: Float,
    medPulse: Float,
    mtnPath: Path,
    wavePath: Path
) {
    val W = size.width
    val H = size.height

    // L1: Heat Atmosphere
    drawRect(
        Brush.verticalGradient(
            0.0f to ColorGurguTop,
            0.5f to ColorGurguMid,
            1.0f to ColorGurguBot
        )
    )
    drawRect(Color(1f, 0.3f, 0f, torchFlicker * 0.04f)) // Fullscreen heat wash

    // L2: Volcanic Rock Silhouettes
    mtnPath.reset()
    mtnPath.moveTo(0f, H * 0.6f)
    mtnPath.lineTo(W * 0.2f, H * 0.25f)
    mtnPath.lineTo(W * 0.4f, H * 0.5f)
    mtnPath.lineTo(W * 0.65f, H * 0.2f)
    mtnPath.lineTo(W * 0.85f, H * 0.45f)
    mtnPath.lineTo(W, H * 0.3f)
    mtnPath.lineTo(W, H * 0.7f)
    mtnPath.lineTo(0f, H * 0.7f)
    mtnPath.close()
    drawPath(mtnPath, ColorGurguMtn)

    // L3: Dark Obsidian Walls & Cracks
    val floorY = H * 0.70f
    val tileW = W / 20f
    val tileH = H * 0.05f
    var wy = H * 0.15f
    var row = 0
    while (wy < floorY) {
        var wx = -(scrollOffset * 0.3f % tileW)
        while (wx < W + tileW) {
            val c = if ((row + (wx / tileW).toInt()) % 2 == 0) ColorGurguTileEven else ColorGurguTileOdd
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
            0.0f to Color(0xFFFF2200),
            0.25f to Color(0xFFFF6600),
            0.5f to Color(0xFFFFAA00),
            0.75f to Color(0xFFFF6600),
            1.0f to Color(0xFFFF2200)
        ),
        Offset(0f, lavaY),
        Size(W, H - lavaY)
    )

    // Animated Lava Wave Surface
    wavePath.reset()
    wavePath.moveTo(0f, lavaY)
    var x = 0f
    while (x <= W) {
        val y = lavaY + sin(x * 0.03f + scrollOffset * 0.05f) * 6f
        wavePath.lineTo(x, y)
        x += 8f
    }
    wavePath.lineTo(W, lavaY)
    wavePath.close()
    drawPath(wavePath, Color(1f, 0.6f, 0f, 0.8f))

    // Lava glow upward
    drawCircle(Color(1f, 0.5f, 0f, 0.15f * torchFlicker), radius = W * 0.4f, center = Offset(W / 2f, lavaY))

    // L5: Rising Embers
    for (i in 0 until 25) {
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
private fun DrawScope.drawSeaShrine(scrollOffset: Float, slowPulse: Float, path: Path) {
    val W = size.width
    val H = size.height

    // L1: Deep Ocean Atmosphere
    drawRect(
        Brush.verticalGradient(
            0.0f to ColorSeaTop,
            0.5f to ColorSeaMid,
            1.0f to ColorSeaBot
        )
    )
    drawRect(ColorSeaWash)

    // L2: Submerged Ruins & Coral
    for (i in ArraySeaPillars.indices) {
        val px = W * ArraySeaPillars[i]
        val py = H * (0.2f + i * 0.05f)
        val pH = H * (0.52f - i * 0.05f)
        drawRect(ColorSeaPillar, Offset(px, py), Size(W * 0.08f, pH))
        val coralC = if (i % 2 == 0) ColorSeaCoral1 else ColorSeaCoral2
        drawCircle(coralC, radius = 18f - i * 4f, center = Offset(px + 10f, py))
    }

    // L3: Temple Stone Walls & Algae
    val floorY = H * 0.72f
    val tileW = W / 20f
    val tileH = H * 0.05f
    var wy = H * 0.12f
    var row = 0
    while (wy < floorY) {
        var wx = -(scrollOffset * 0.35f % tileW)
        while (wx < W + tileW) {
            val c = if ((row + (wx / tileW).toInt()) % 2 == 0) ColorSeaTileEven else ColorSeaTileOdd
            drawRect(c, Offset(wx, wy), Size(tileW - 1f, tileH - 1f))
            if ((row + (wx / tileW).toInt()) % 3 == 0) {
                drawRect(ColorSeaAlgae, Offset(wx + 2f, wy), Size(3f, tileH - 1f)) // algae
            }
            wx += tileW
        }
        wy += tileH
        row++
    }

    // L4: Caustic Light Shafts
    for (i in 0 until 5) {
        val xTop = W * (0.15f + i * 0.18f) + cos(scrollOffset * 0.01f + i) * 20f
        path.reset()
        path.moveTo(xTop, 0f)
        path.lineTo(xTop + 25f, 0f)
        path.lineTo(xTop + 80f, floorY)
        path.lineTo(xTop - 20f, floorY)
        path.close()
        drawPath(path, Color(0.3f, 0.7f, 1f, 0.05f + sin(scrollOffset * 0.01f + i) * 0.02f))
    }

    // L5: Rising Bubbles
    for (i in 0 until 25) {
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
private fun DrawScope.drawEarthCave(scrollOffset: Float, torchFlicker: Float, path: Path) {
    val W = size.width
    val H = size.height

    // Atmosphere
    drawRect(
        Brush.verticalGradient(
            0.0f to ColorEarthTop,
            0.5f to ColorEarthMid,
            1.0f to ColorEarthBot
        )
    )

    // Jagged Cave Walls (Top & Bottom Boundaries)
    val floorY = H * 0.72f
    path.reset()
    path.moveTo(0f, 0f)
    path.lineTo(W, 0f)
    path.lineTo(W, H * 0.18f)
    var x = W
    while (x >= 0) {
        val y = H * 0.15f + sin(x * 0.02f + scrollOffset * 0.02f) * 15f
        path.lineTo(x, y)
        x -= 20f
    }
    path.close()
    drawPath(path, ColorEarthCaveBound)

    // Stalactites
    for (i in ArrayEarthStalactites.indices) {
        val sx = W * ArrayEarthStalactites[i]
        path.reset()
        path.moveTo(sx - 15f, H * 0.15f)
        path.lineTo(sx + 15f, H * 0.15f)
        path.lineTo(sx, H * 0.35f)
        path.close()
        drawPath(path, ColorEarthStalactite)
    }

    // Stalagmites
    for (i in ArrayEarthStalagmites.indices) {
        val sx = W * ArrayEarthStalagmites[i]
        path.reset()
        path.moveTo(sx - 20f, floorY)
        path.lineTo(sx + 20f, floorY)
        path.lineTo(sx, floorY - H * 0.18f)
        path.close()
        drawPath(path, ColorEarthStalagmite)
    }

    // Water Drips
    for (i in 0 until 4) {
        val dx = W * (0.25f + i * 0.2f)
        val dy = (H * 0.35f + (scrollOffset * 0.8f + i * 50f)) % (floorY - H * 0.35f) + H * 0.35f
        drawCircle(ColorEarthWaterDrip, radius = 3f, center = Offset(dx, dy))
    }

    drawPerspectiveFloor(floorY, Color(0xFF1A1008), Color(0xFF120A04), Color(0xFF080402), scrollOffset)
    drawVignette(0.8f)
}

// ==========================================
// BIOME 6: CRYSTAL_TOWER
// ==========================================
private fun DrawScope.drawCrystalTower(
    scrollOffset: Float,
    slowPulse: Float,
    fastTick: Float,
    pLeft: Path,
    pRight: Path
) {
    val W = size.width
    val H = size.height

    // L1: Light Atmosphere
    drawRect(
        Brush.verticalGradient(
            0.0f to ColorCrystalTop,
            0.5f to ColorCrystalMid,
            1.0f to ColorCrystalBot
        )
    )
    drawCircle(
        Brush.radialGradient(
            0.0f to Color(0.6f, 0.9f, 1f, 0.18f),
            1.0f to Color.Transparent,
            center = Offset(W / 2f, H / 3f),
            radius = W * 0.45f
        ),
        radius = W * 0.45f,
        center = Offset(W / 2f, H / 3f)
    )

    // L2: Giant Crystal Formations
    val floorY = H * 0.72f
    val crystalPositionsX = floatArrayOf(W * 0.1f, W * 0.3f, W * 0.7f, W * 0.88f)
    val crystalPositionsY = floatArrayOf(H * 0.15f, H * 0.22f, H * 0.18f, H * 0.1f)

    for (i in crystalPositionsX.indices) {
        val cx = crystalPositionsX[i]
        val cy = crystalPositionsY[i]
        val cW = 40f

        // Left face
        pLeft.reset()
        pLeft.moveTo(cx, cy + 20f)
        pLeft.lineTo(cx + cW / 2f, cy)
        pLeft.lineTo(cx + cW / 2f, floorY)
        pLeft.lineTo(cx, floorY)
        pLeft.close()
        drawPath(pLeft, ColorCrystalLeft)

        // Right face
        pRight.reset()
        pRight.moveTo(cx + cW / 2f, cy)
        pRight.lineTo(cx + cW, cy + 20f)
        pRight.lineTo(cx + cW, floorY)
        pRight.lineTo(cx + cW / 2f, floorY)
        pRight.close()
        drawPath(pRight, ColorCrystalRight)

        // Highlight cap
        drawCircle(Color(0.5f, 0.8f, 1f, 0.3f), radius = 15f, center = Offset(cx + cW / 2f, cy))
    }

    // L3: Mirror Tile Floor
    drawPerspectiveFloor(floorY, Color(0xFF0D253A), Color(0xFF081828), Color(0xFF1A5070), scrollOffset)

    // L4: Light Sweep Band
    val sweepX = (scrollOffset * 2f) % (W + 400f) - 200f
    pLeft.reset()
    pLeft.moveTo(sweepX, 0f)
    pLeft.lineTo(sweepX + 150f, 0f)
    pLeft.lineTo(sweepX + 50f, H)
    pLeft.lineTo(sweepX - 100f, H)
    pLeft.close()
    drawPath(pLeft, Color(1f, 1f, 1f, 0.05f))

    // L5: Sparkle Particles
    for (i in 0 until 20) {
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
            0.0f to ColorMysidianTop,
            0.5f to ColorMysidianMid,
            1.0f to ColorMysidianBot
        )
    )

    // L2: Distant Bookshelves
    val shelfY = H * 0.15f
    val shelfH = H * 0.55f
    val floorY = H * 0.70f
    drawRect(ColorMysidianShelf, Offset(W * 0.05f, shelfY), Size(W * 0.9f, shelfH))
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
    val g1 = Offset(W * 0.25f, H * 0.35f)
    val g2 = Offset(W * 0.75f, H * 0.35f)

    drawCircle(Color(0.9f, 0.7f, 0.2f, 0.2f + slowPulse * 0.15f), radius = 45f, center = g1, style = Stroke(width = 2f))
    drawCircle(Color(0.9f, 0.7f, 0.2f, 0.15f), radius = 30f, center = g1, style = Stroke(width = 1f))

    drawCircle(Color(0.9f, 0.7f, 0.2f, 0.2f + slowPulse * 0.15f), radius = 45f, center = g2, style = Stroke(width = 2f))
    drawCircle(Color(0.9f, 0.7f, 0.2f, 0.15f), radius = 30f, center = g2, style = Stroke(width = 1f))

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
            0.0f to ColorPandaemoniumTop,
            0.5f to ColorPandaemoniumMid,
            1.0f to ColorPandaemoniumBot
        )
    )

    // Bone Pillars
    val floorY = H * 0.72f
    val pillarX1 = W * 0.15f
    val pillarX2 = W * 0.85f

    var py = H * 0.1f
    while (py < floorY) {
        drawRect(ColorPandaemoniumBoneOuter, Offset(pillarX1 - 15f, py), Size(30f, 18f))
        drawRect(ColorPandaemoniumBoneInner, Offset(pillarX1 - 12f, py + 2f), Size(24f, 14f))

        drawRect(ColorPandaemoniumBoneOuter, Offset(pillarX2 - 15f, py), Size(30f, 18f))
        drawRect(ColorPandaemoniumBoneInner, Offset(pillarX2 - 12f, py + 2f), Size(24f, 14f))
        py += 24f
    }

    // Jade Pools
    drawCircle(Color(0f, 0.8f, 0.3f, 0.15f + medPulse * 0.1f), radius = W * 0.35f, center = Offset(W / 2f, floorY))

    drawPerspectiveFloor(floorY, Color(0xFF0A1A10), Color(0xFF05100A), Color(0xFF153520), scrollOffset)
    drawVignette(0.8f)
}

// ==========================================
// BIOME 9: MOUNT_ORDEALS
// ==========================================
private fun DrawScope.drawMountOrdeals(scrollOffset: Float, slowPulse: Float, r1: Path) {
    val W = size.width
    val H = size.height

    // Open Sky Dusk
    drawRect(
        Brush.verticalGradient(
            0.0f to ColorMountOrdealsTop,
            0.5f to ColorMountOrdealsMid,
            1.0f to ColorMountOrdealsBot
        )
    )

    // Parallax Mountain Ridges
    r1.reset()
    r1.moveTo(0f, H * 0.5f)
    r1.lineTo(W * 0.3f, H * 0.3f)
    r1.lineTo(W * 0.7f, H * 0.45f)
    r1.lineTo(W, H * 0.25f)
    r1.lineTo(W, H * 0.75f)
    r1.lineTo(0f, H * 0.75f)
    r1.close()
    drawPath(r1, ColorMountOrdealsRidge)

    val floorY = H * 0.72f
    drawPerspectiveFloor(floorY, Color(0xFF3A2020), Color(0xFF2A1515), Color(0xFF5A3535), scrollOffset)

    // Holy Light Shafts
    drawRect(
        Brush.verticalGradient(0.0f to Color(1f, 0.9f, 0.6f, 0.12f), 1.0f to Color.Transparent),
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
            0.0f to ColorBaronTop,
            0.5f to ColorBaronMid,
            1.0f to ColorBaronBot
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
            drawRect(ColorBaronPanelBase, Offset(px, py), Size(panelW - 2f, panelH - 2f))
            drawRect(ColorBaronPanelInner, Offset(px + 2f, py + 2f), Size(panelW - 6f, panelH - 6f))
            // Rivets
            drawCircle(ColorBaronRivet, radius = 2f, center = Offset(px + 6f, py + 6f))
            drawCircle(ColorBaronRivet, radius = 2f, center = Offset(px + panelW - 8f, py + 6f))
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
private fun DrawScope.drawAncientCastle(scrollOffset: Float, slowPulse: Float, sandPath: Path) {
    val W = size.width
    val H = size.height

    drawRect(
        Brush.verticalGradient(
            0.0f to ColorAncientTop,
            0.5f to ColorAncientMid,
            1.0f to ColorAncientBot
        )
    )

    val floorY = H * 0.72f

    // Sand Drifts along floor
    sandPath.reset()
    sandPath.moveTo(0f, floorY)
    var x = 0f
    while (x <= W) {
        val y = floorY - 15f - sin(x * 0.015f + scrollOffset * 0.01f) * 12f
        sandPath.lineTo(x, y)
        x += 20f
    }
    sandPath.lineTo(W, floorY)
    sandPath.close()
    drawPath(sandPath, ColorAncientSand)

    drawPerspectiveFloor(floorY, Color(0xFF281E0F), Color(0xFF1C140A), Color(0xFF4A381C), scrollOffset)
    drawVignette(0.75f)
}

// ==========================================
// BIOME 12: NARSHE_MINES
// ==========================================
private fun DrawScope.drawNarsheMines(scrollOffset: Float, torchFlicker: Float, icicleP: Path) {
    val W = size.width
    val H = size.height

    // Cold Mine Atmosphere
    drawRect(
        Brush.verticalGradient(
            0.0f to ColorNarsheTop,
            0.5f to ColorNarsheMid,
            1.0f to ColorNarsheBot
        )
    )

    val floorY = H * 0.70f

    // Wooden Support Trusses
    // Horizontal beams
    drawRect(ColorNarsheBeam, Offset(0f, H * 0.12f), Size(W, H * 0.04f))
    drawRect(ColorNarsheBeamHi, Offset(0f, H * 0.12f), Size(W, 3f))

    drawRect(ColorNarsheBeam, Offset(0f, floorY), Size(W, H * 0.04f))
    drawRect(ColorNarsheBeamHi, Offset(0f, floorY), Size(W, 3f))

    // Vertical posts
    for (i in ArrayNarshePosts.indices) {
        val px = W * ArrayNarshePosts[i]
        drawRect(ColorNarsheBeam, Offset(px - 9f, H * 0.12f), Size(18f, floorY - H * 0.12f))
        drawRect(ColorNarsheBeamHi, Offset(px - 9f, H * 0.12f), Size(3f, floorY - H * 0.12f))
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
    for (i in ArrayNarsheIcicles.indices) {
        val ix = W * ArrayNarsheIcicles[i]
        icicleP.reset()
        icicleP.moveTo(ix - 8f, 0f)
        icicleP.lineTo(ix + 8f, 0f)
        icicleP.lineTo(ix, 45f)
        icicleP.close()
        drawPath(icicleP, ColorNarsheIcicle)
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

    drawRect(ColorMagitekBg)
    drawRect(ColorMagitekWash) // Green chemical wash

    val floorY = H * 0.72f

    // Pipe bundles
    for (i in ArrayMagitekPipes.indices) {
        val py = H * ArrayMagitekPipes[i]
        drawRect(ColorMagitekPipeBase, Offset(0f, py), Size(W, 16f))
        drawRect(ColorMagitekPipeHi, Offset(0f, py + 2f), Size(W, 4f))
    }

    // Glowing Green Conduits
    for (i in ArrayMagitekConduits.indices) {
        val cx = W * ArrayMagitekConduits[i]
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
    for (i in 0 until 12) {
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
            0.0f to ColorFloatingTop,
            0.5f to ColorFloatingMid,
            1.0f to ColorFloatingBot
        )
    )

    val floorY = H * 0.70f

    // Rushing Cloud Sea Below
    for (i in 0 until 8) {
        val cx = (scrollOffset * 0.6f + i * 120f) % (W + 200f) - 100f
        drawCircle(ColorFloatingCloud, radius = 60f, center = Offset(cx, floorY + 40f))
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
            0.0f to ColorMidgarTop,
            0.5f to ColorMidgarMid,
            1.0f to ColorMidgarBot
        )
    )

    val floorY = H * 0.72f

    // Tunnel Perspective Arches
    for (i in ArrayMidgarArches.indices) {
        val scale = ArrayMidgarArches[i]
        drawCircle(ColorMidgarArch, radius = W * scale, center = Offset(W / 2f, H * 0.4f), style = Stroke(width = 3f))
    }

    // Toxic Sludge Floor Channel
    drawRect(ColorMidgarSludgeBase, Offset(0f, floorY), Size(W, H - floorY))
    drawRect(Color(0.2f, 0.8f, 0.1f, 0.3f + torchFlicker * 0.1f), Offset(W * 0.2f, floorY), Size(W * 0.6f, H - floorY))

    drawVignette(0.8f)
}

// ==========================================
// BIOME 17: SHINRA_BUILDING
// ==========================================
private fun DrawScope.drawShinraBuilding(scrollOffset: Float, fastTick: Float) {
    val W = size.width
    val H = size.height

    drawRect(ColorShinraBg)

    val floorY = H * 0.72f

    // Skyline (fixed xFrac and hFrac values)
    val skylineX = floatArrayOf(0.05f, 0.2f, 0.45f, 0.7f, 0.85f)
    val skylineH = floatArrayOf(0.4f, 0.55f, 0.35f, 0.6f, 0.45f)

    for (i in skylineX.indices) {
        val xFrac = skylineX[i]
        val hFrac = skylineH[i]
        val bW = W * 0.12f
        val bH = H * hFrac
        drawRect(ColorShinraBldg, Offset(W * xFrac, floorY - bH), Size(bW, bH))
        // Antenna red blink
        if (fastTick > 0.5f) {
            drawCircle(Color.Red, radius = 2.5f, center = Offset(W * xFrac + bW / 2f, floorY - bH))
        }
    }

    // Blue Neon Light Strips
    for (i in ArrayShinraNeon.indices) {
        val ny = H * ArrayShinraNeon[i]
        drawRect(ColorShinraNeonBlue, Offset(0f, ny), Size(W, 3f))
        drawRect(ColorShinraNeonGlow, Offset(0f, ny - 8f), Size(W, 19f))
    }

    drawPerspectiveFloor(floorY, Color(0xFF081018), Color(0xFF040810), Color(0xFF0066CC), scrollOffset)
    drawVignette(0.7f)
}

// ==========================================
// BIOME 18: NORTHERN_CRATER
// ==========================================
private fun DrawScope.drawNorthernCrater(scrollOffset: Float, slowPulse: Float, lifestreamP: Path) {
    val W = size.width
    val H = size.height

    drawRect(
        Brush.verticalGradient(
            0.0f to ColorCraterTop,
            0.5f to ColorCraterMid,
            1.0f to ColorCraterBot
        )
    )

    val floorY = H * 0.72f

    // Lifestream Strands (Spiraling upward)
    for (i in 0 until 6) {
        val lx = W * (0.15f + i * 0.14f)
        lifestreamP.reset()
        lifestreamP.moveTo(lx, floorY)
        lifestreamP.cubicTo(lx + 30f, H * 0.5f, lx - 30f, H * 0.25f, lx, 0f)
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
            0.0f to ColorGoldenSaucerTop,
            0.5f to ColorGoldenSaucerMid,
            1.0f to ColorGoldenSaucerBot
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
private fun DrawScope.drawBevelleTemple(scrollOffset: Float, slowPulse: Float, waveP: Path) {
    val W = size.width
    val H = size.height

    drawRect(
        Brush.verticalGradient(
            0.0f to ColorBevelleTop,
            0.5f to ColorBevelleMid,
            1.0f to ColorBevelleBot
        )
    )

    val floorY = H * 0.72f

    // Ivory Marble Columns
    for (i in ArrayBevelleColumns.indices) {
        val px = W * ArrayBevelleColumns[i]
        drawRect(ColorBevelleColumnOuter, Offset(px - 16f, 0f), Size(32f, floorY))
        drawRect(ColorBevelleColumnInner, Offset(px - 8f, 0f), Size(16f, floorY))
    }

    // Energy Ribbon Tracks
    for (i in 0 until 3) {
        val ry = H * (0.25f + i * 0.15f)
        waveP.reset()
        waveP.moveTo(0f, ry)
        var x = 0f
        while (x <= W) {
            val y = ry + sin(x * 0.02f + scrollOffset * 0.05f + i) * 8f
            waveP.lineTo(x, y)
            x += 10f
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

    drawRect(ColorOmegaBg)

    val floorY = H * 0.72f

    // Scanning Laser
    val laserY = (scrollOffset * 3f) % floorY
    drawRect(Color(0f, 1f, 0.8f, 0.6f), Offset(0f, laserY), Size(W, 2f))
    drawRect(Color(0f, 1f, 0.8f, 0.1f), Offset(0f, laserY - 10f), Size(W, 22f))

    // Digital Code Streams
    for (i in 0 until 15) {
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
            0.0f to ColorSinTop,
            0.5f to ColorSinMid,
            1.0f to ColorSinBot
        )
    )

    val floorY = H * 0.72f

    // Pulsing Bio-Ribs
    val ribScale = 1f + medPulse * 0.05f
    for (i in ArraySinRibs.indices) {
        val rx = W * ArraySinRibs[i]
        var ry = H * 0.1f
        while (ry < floorY) {
            drawCircle(ColorSinRibOuter, radius = 18f * ribScale, center = Offset(rx, ry))
            drawCircle(ColorSinRibInner, radius = 10f * ribScale, center = Offset(rx, ry))
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

    drawRect(ColorGenericBg, size = size)

    // Arch
    drawRect(ColorGenericArch, Offset(W * 0.08f, H * 0.1f), Size(W * 0.06f, H * 0.85f))
    drawRect(ColorGenericArchHi, Offset(W * 0.085f, H * 0.1f), Size(W * 0.04f, H * 0.83f))
    drawRect(ColorGenericArch, Offset(W * 0.86f, H * 0.1f), Size(W * 0.06f, H * 0.85f))
    drawRect(ColorGenericArchHi, Offset(W * 0.865f, H * 0.1f), Size(W * 0.04f, H * 0.83f))
    drawRect(ColorGenericArch, Offset(W * 0.08f, H * 0.1f), Size(W * 0.84f, H * 0.08f))

    val floorY = H * 0.72f
    drawPerspectiveFloor(floorY, Color(0xFF1C1230), Color(0xFF160E26), Color(0xFF0A0614), scrollOffset)

    // Torches
    for (i in ArrayGenericTorches.indices) {
        val tx = W * ArrayGenericTorches[i]
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
            0.0f to Color.Transparent,
            1.0f to Color(0, 0, 0, (maxAlpha * 255).toInt()),
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
        val beamYPositions = floatArrayOf(0.0f, 0.18f, 0.36f)
        for (i in beamYPositions.indices) {
            val yFrac = beamYPositions[i]
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
        for (i in 0 until 5) {
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
                0.0f to Color.Transparent,
                1.0f to Color(0xBB000000),
                center = Offset(W / 2f, H / 2f),
                radius = W * 0.72f
            ), size = size
        )
    }
}

@Composable
fun TownParallaxBackground(scrollOffset: Float) {
    val mtnPath = remember { Path() }

    Canvas(Modifier.fillMaxSize()) {
        val W = size.width
        val H = size.height

        // LAYER 1: Deep Sky & Stars (0.05x scroll)
        drawRect(Color(0xFF050A1A), size = size)
        val starRng = java.util.Random(42)
        for (i in 0 until 60) {
            val sx = (starRng.nextFloat() * W - (scrollOffset * 0.05f)) % W
            val drawX = if (sx < 0) sx + W else sx
            drawCircle(Color.White.copy(alpha = 0.4f), radius = 1.5f, center = Offset(drawX, starRng.nextFloat() * H * 0.4f))
        }

        // LAYER 2: Distant Mountains (0.15x scroll)
        val mountainY = H * 0.5f
        val mWidth = 500f
        var mx = -(scrollOffset * 0.15f % mWidth)
        while (mx < W + mWidth) {
            mtnPath.reset()
            mtnPath.moveTo(mx, mountainY)
            mtnPath.lineTo(mx + mWidth * 0.3f, mountainY - 100f)
            mtnPath.lineTo(mx + mWidth * 0.6f, mountainY - 180f)
            mtnPath.lineTo(mx + mWidth * 0.8f, mountainY - 80f)
            mtnPath.lineTo(mx + mWidth, mountainY)
            mtnPath.close()
            drawPath(mtnPath, Color(0xFF1B0F2E)) // Dark purple mountains
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
