package com.game.dungeon.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.ui.components.safeStringResource
import com.game.dungeon.ui.viewmodels.TownViewModel
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import com.game.dungeon.R
import com.game.dungeon.ui.components.GoldenBorderBox
import com.game.dungeon.ui.components.PixelButton
import com.game.dungeon.ui.components.PixelGoldDisplay
import com.game.dungeon.ui.theme.BgDarkest
import com.game.dungeon.ui.theme.GoldBright
import com.game.dungeon.ui.theme.PixelHeading

@Composable
fun CrystalShopScreen(
    navController: NavController,
    viewModel: TownViewModel = hiltViewModel()
) {
    val gs by viewModel.gameState.collectAsState()
    val availableCrystals by viewModel.availableCrystals.collectAsState()

    val gil = gs?.gold ?: 0L
    val unlockedJobs = gs?.unlockedJobs ?: setOf(HeroClass.FREELANCER)

    var selectedCrystal by remember { mutableStateOf<TownViewModel.CrystalData?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "shop_anim")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)), label = "time"
    )
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -3f, targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse), label = "float"
    )
    val flicker by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(150, easing = LinearEasing), RepeatMode.Reverse), label = "flicker"
    )

    Column(Modifier
        .fillMaxSize()
        .background(Color(0xFF15101A))) {
        // --- HEADER BAR ---
        // --- HEADER BAR (Forge Style) ---
        GoldenBorderBox(Modifier
            .fillMaxWidth()
            .height(48.dp)) {
            Box(Modifier
                .fillMaxSize()
                .background(BgDarkest)
                .padding(horizontal = 8.dp)) {
                // Left: Back Button
                PixelButton(
                    label = stringResource(R.string.back_button) ,
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .height(32.dp)
                )

                // Center: Title
                Text(
                    stringResource(R.string.building_crystal_shop),
                    style = PixelHeading,
                    color = GoldBright,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Right: Gold
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PixelGoldDisplay(amount = gil)
                }
            }
        }

        // --- PURPLE AWNING BANNERS ---
        Canvas(Modifier
            .fillMaxWidth()
            .height(24.dp)) {
            val bannerCount = 8
            val bannerW = size.width / bannerCount
            for (i in 0 until bannerCount) {
                val bx = i * bannerW
                val path = Path().apply {
                    moveTo(bx, 0f)
                    lineTo(bx + bannerW, 0f)
                    lineTo(bx + bannerW / 2f, size.height) // Pointy bottom
                    close()
                }
                drawPath(path, Color(0xFF6A1B9A)) // Purple fabric
                drawPath(path, Color(0xFFFFD700), style = Stroke(2f)) // Gold trim
            }
            // Dark drop shadow under the banners
            drawLine(Color(0x88000000), Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 6f)
        }

        // --- MAIN SHOP AREA ---
        Box(Modifier
            .weight(1f)
            .fillMaxWidth()) {

            // LAYER 1: STATIC BRICK BACKGROUND
            Canvas(Modifier.fillMaxSize()) {
                val brickW = 70f
                val brickH = 35f
                var py = 0f
                var row = 0
                while (py < size.height) {
                    var px = if (row % 2 == 0) 0f else -brickW / 2f
                    while (px < size.width) {
                        val color = if ((row + (px / brickW).toInt()) % 3 == 0) Color(0xFF1A1423) else Color(0xFF140F1B)
                        drawRect(color, Offset(px, py), Size(brickW - 2f, brickH - 2f))
                        px += brickW
                    }
                    py += brickH
                    row++
                }
            }

            // LAYER 2: THREE EQUAL VERTICAL SECTIONS
            Column(Modifier.fillMaxSize()) {

                // --- TOP THIRD: STANDARD CRYSTALS ---
                Box(Modifier
                    .weight(1f)
                    .fillMaxWidth()) {
                    ShopShelf(
                        title = stringResource(R.string.standard_crystals),
                        crystals = availableCrystals.filter { it.unlocksJob.tier < 2 },
                        unlockedJobs = unlockedJobs,
                        floatAnim = floatAnim,
                        seed = 42,
                        onSelect = { selectedCrystal = it }
                    )
                }

                // --- MIDDLE THIRD: MYSTIC CRYSTALS ---
                Box(Modifier
                    .weight(1f)
                    .fillMaxWidth()) {
                    ShopShelf(
                        title = stringResource(R.string.mystic_crystals),
                        crystals = availableCrystals.filter { it.unlocksJob.tier >= 2 },
                        unlockedJobs = unlockedJobs,
                        floatAnim = floatAnim,
                        seed = 84,
                        onSelect = { selectedCrystal = it }
                    )
                }

                // --- BOTTOM THIRD: MERCHANT, COUNTER & FOREGROUND ---
                Box(Modifier
                    .weight(1f)
                    .fillMaxWidth()) {
                    Canvas(Modifier.fillMaxSize()) {
                        val W = size.width
                        val H = size.height

                        val counterTop = H * 0.35f
                        val mx = W / 2f
                        val my = counterTop - 25f + (sin(animTime * 0.05f) * 4f)

                        // 1. Merchant Cloak
                        val hoodPath = Path().apply {
                            moveTo(mx, my - 60f)
                            quadraticTo(mx + 60f, my - 30f, mx + 70f, counterTop + 20f)
                            lineTo(mx - 70f, counterTop + 20f)
                            quadraticTo(mx - 60f, my - 30f, mx, my - 60f)
                            close()
                        }
                        drawPath(hoodPath, Color(0xFF2D1B36))
                        drawPath(hoodPath, Color(0xFF170E1C), style = Stroke(3f))

                        // 2. Face Void & Eyes
                        drawOval(Color(0xFF0D0512), Offset(mx - 30f, my - 20f), Size(60f, 50f))
                        drawCircle(Color(0xFFFFC107), radius = 6f, center = Offset(mx - 12f, my + 5f))
                        drawCircle(Color(0xFFFFC107), radius = 6f, center = Offset(mx + 12f, my + 5f))
                        drawCircle(Color(0xFFFFC107).copy(alpha = 0.3f), radius = 12f, center = Offset(mx - 12f, my + 5f))
                        drawCircle(Color(0xFFFFC107).copy(alpha = 0.3f), radius = 12f, center = Offset(mx + 12f, my + 5f))

                        // 3. Merchant Hands
                        drawOval(Color(0xFF1A0F24), Offset(mx - 50f, counterTop - 10f), Size(25f, 20f))
                        drawOval(Color(0xFF1A0F24), Offset(mx + 25f, counterTop - 10f), Size(25f, 20f))

                        // 4. Counter Base & Planks
                        drawRect(Color(0xFF4A3219), Offset(0f, counterTop), Size(W, 15f))
                        drawRect(Color(0xFF684624), Offset(0f, counterTop), Size(W, 3f))
                        drawRect(Color(0xFF2C1E16), Offset(0f, counterTop + 15f), Size(W, 8f))
                        drawRect(Color(0xFF3B2513), Offset(0f, counterTop + 23f), Size(W, H - (counterTop + 23f)))

                        var cx = 0f
                        while(cx < W) {
                            drawLine(Color(0xFF241509), Offset(cx, counterTop + 23f), Offset(cx, H), strokeWidth = 3f)
                            cx += 40f
                        }

                        // 5. AMBIENT ITEMS ON COUNTER (FF LORE)
                        val cardX = W * 0.12f
                        val cardY = counterTop - 18f
                        rotate(-15f, Offset(cardX, cardY)) {
                            drawRect(Color(0xFF1565C0), Offset(cardX, cardY), Size(20f, 30f))
                            drawRect(Color.White.copy(alpha = 0.8f), Offset(cardX + 2f, cardY + 2f), Size(16f, 26f), style = Stroke(1f))
                        }
                        rotate(10f, Offset(cardX + 12f, cardY + 5f)) {
                            drawRect(Color(0xFFC62828), Offset(cardX + 12f, cardY + 5f), Size(20f, 30f))
                            drawRect(Color.White.copy(alpha = 0.8f), Offset(cardX + 14f, cardY + 7f), Size(16f, 26f), style = Stroke(1f))
                            drawCircle(Color(0xFFFFD700), radius = 3f, center = Offset(cardX + 22f, cardY + 20f))
                        }
                        val featherPath = Path().apply {
                            moveTo(cardX + 35f, counterTop + 5f)
                            quadraticTo(cardX + 45f, counterTop - 25f, cardX + 70f, counterTop - 35f)
                            quadraticTo(cardX + 55f, counterTop - 15f, cardX + 40f, counterTop + 8f)
                            close()
                        }
                        drawPath(featherPath, Color(0xFFFFCA28))
                        drawPath(featherPath, Color(0xFFFF8F00), style = Stroke(1f))

                        val flaskX = W * 0.28f
                        val flaskY = counterTop - 28f
                        drawOval(Color(0xFF00BFA5).copy(alpha = 0.8f), Offset(flaskX, flaskY), Size(24f, 28f))
                        drawRect(Color.White.copy(alpha = 0.4f), Offset(flaskX + 4f, flaskY + 4f), Size(4f, 12f))
                        drawRect(Color(0xFF5D4037), Offset(flaskX + 8f, flaskY - 8f), Size(8f, 12f))
                        drawCircle(Color(0xFF00BFA5).copy(alpha = 0.2f + (flicker * 0.1f)), radius = 30f, center = Offset(flaskX + 12f, flaskY + 14f))

                        val bookX = W * 0.68f
                        val bookY = counterTop - 16f
                        drawRect(Color(0xFF3E2723), Offset(bookX, bookY), Size(45f, 16f))
                        drawRect(Color(0xFFD7CCC8), Offset(bookX + 2f, bookY + 2f), Size(41f, 12f))
                        drawRect(Color(0xFF2C1E16), Offset(bookX, bookY), Size(45f, 16f), style = Stroke(2f))
                        val runeColor = Color(0xFF00E5FF)
                        drawLine(runeColor.copy(alpha = 0.8f * flicker), Offset(bookX + 15f, bookY + 8f), Offset(bookX + 30f, bookY + 8f), strokeWidth = 2f)
                        drawLine(runeColor.copy(alpha = 0.8f * flicker), Offset(bookX + 22.5f, bookY + 2f), Offset(bookX + 22.5f, bookY + 14f), strokeWidth = 2f)
                        drawCircle(runeColor.copy(alpha = 0.3f * flicker), radius = 15f, center = Offset(bookX + 22.5f, bookY + 8f))

                        val orbX = W * 0.88f
                        val orbY = counterTop - 35f
                        drawRect(Color(0xFFB8860B), Offset(orbX - 12f, counterTop - 8f), Size(24f, 8f))
                        drawRect(Color(0xFFDAA520), Offset(orbX - 6f, counterTop - 16f), Size(12f, 8f))
                        drawCircle(Color(0xFF9C27B0).copy(alpha = 0.7f), radius = 20f, center = Offset(orbX, orbY))
                        drawCircle(Color(0xFFE1BEE7).copy(alpha = 0.5f), radius = 6f, center = Offset(orbX - 6f, orbY - 6f))
                        drawCircle(Color(0xFFE040FB).copy(alpha = 0.2f + (flicker * 0.15f)), radius = 35f, center = Offset(orbX, orbY))

                        // ====================================================
                        // 6. FOREGROUND ITEMS (ON THE GROUND)
                        // ====================================================
                        val floorY = H

                        // --- Right Side: Shop Crates & Barrels ---
                        val crateX = W * 0.75f
                        drawRect(Color(0xFF5D4037), Offset(crateX, floorY - 60f), Size(60f, 60f))
                        drawRect(Color(0xFF3E2723), Offset(crateX, floorY - 60f), Size(60f, 60f), style = Stroke(3f))
                        drawLine(Color(0xFF3E2723), Offset(crateX, floorY - 60f), Offset(crateX + 60f, floorY), strokeWidth = 3f)
                        drawLine(Color(0xFF3E2723), Offset(crateX + 60f, floorY - 60f), Offset(crateX, floorY), strokeWidth = 3f)

                        val barrelX = W * 0.88f
                        drawRoundRect(Color(0xFF4E342E), Offset(barrelX, floorY - 75f), Size(45f, 75f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f))
                        drawRect(Color(0xFF212121), Offset(barrelX, floorY - 55f), Size(45f, 4f)) // Metal band
                        drawRect(Color(0xFF212121), Offset(barrelX, floorY - 25f), Size(45f, 4f)) // Metal band


                    }
                }
            }
        }
    }

    // --- PURCHASE DIALOG ---
    selectedCrystal?.let { crystal ->
        val crystalColor = Color(crystal.color.colorHex)
        val crystalName = safeStringResource(crystal.color.nameRes)
        val jobName = safeStringResource(crystal.unlocksJob.nameRes)
        val isOwned = unlockedJobs.contains(crystal.unlocksJob)
        val cost = crystal.color.baseCost

        Dialog(onDismissRequest = { selectedCrystal = null }) {
            Box(Modifier
                .width(320.dp)
                .background(Color(0xFF15101A))
                .border(2.dp, Color(0xFFFFD700))
                .padding(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Canvas(Modifier.size(64.dp)) {
                        val path = Path().apply {
                            moveTo(size.width / 2f, 0f); lineTo(size.width, size.height * 0.4f)
                            lineTo(size.width / 2f, size.height); lineTo(0f, size.height * 0.4f); close()
                        }
                        drawPath(path, color = crystalColor)
                        drawPath(path, color = Color.White.copy(alpha = 0.4f), style = Stroke(3f))
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(crystalName, color = crystalColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(  stringResource(R.string.unlocks_job_format, jobName), color = Color(0xFFBCAAA4), fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))
                    Box(Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color(0xFF4A148C)))
                    Spacer(Modifier.height(16.dp))

                    if (isOwned) {
                        Text( text = stringResource(R.string.owned_status), color = Color(0xFF69F0AE), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.cost), color = Color.White, fontSize = 16.sp)
                            Text("🪙 $cost", color = Color(0xFFFFD700), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(16.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .background(if (gil >= cost) Color(0xFF1E88E5) else Color(0xFF424242))
                                .clickable(enabled = gil >= cost) {
                                    viewModel.buyCrystal(crystal.color)
                                    selectedCrystal = null // Close dialog on purchase
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (gil >= cost) stringResource(R.string.purchase) else stringResource(
                                R.string.not_enough_gil
                            ), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShopShelf(
    title: String,
    crystals: List<TownViewModel.CrystalData>,
    unlockedJobs: Set<HeroClass>,
    floatAnim: Float,
    seed: Int,
    onSelect: (TownViewModel.CrystalData) -> Unit
) {
    // CHANGE: Use fillMaxSize() so it expands perfectly within its 1/3 weighted slot
    Box(Modifier.fillMaxSize()) {

        // 1. Static Canvas for Background Elements (Books)
        Canvas(Modifier
            .fillMaxSize()
            .padding(bottom = 14.dp)) {
            val rng = Random(seed.toLong())
            repeat(4) {
                val bw = 15f + rng.nextFloat() * 15f
                val bh = 40f + rng.nextFloat() * 30f
                val bx = 40f + rng.nextFloat() * (size.width - 80f)
                val bColor = listOf(Color(0xFF3E2723), Color(0xFF1B5E20), Color(0xFF01579B), Color(0xFF4A148C)).random(rng)

                drawRect(bColor, Offset(bx, size.height - bh), Size(bw, bh))
                drawRect(Color(0xFF110A05), Offset(bx, size.height - bh), Size(bw, bh), style = Stroke(2f))
            }
        }

        // 2. Shelf Plank
        Box(Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(14.dp)) {
            Box(Modifier
                .fillMaxSize()
                .background(Color(0xFF3E2723))
                .border(1.dp, Color(0xFF1F1208)))
            Box(Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color(0xFF5D4037)))
        }

        // 3. Static Title Label
        Text(
            title, color = Color(0xFFD7CCC8), fontSize = 12.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp) // Added slight top padding
        )

        // 4. Scrolling Crystals
        Row(
            Modifier
                .fillMaxSize()
                .padding(bottom = 14.dp)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally)
        ) {
            Spacer(Modifier.width(16.dp))
            crystals.forEach { crystal ->
                CrystalDisplayItem(
                    crystal = crystal,
                    isOwned = unlockedJobs.contains(crystal.unlocksJob),
                    floatAnim = floatAnim
                ) { onSelect(crystal) }
            }
            Spacer(Modifier.width(16.dp))
        }
    }
}

@Composable
fun CrystalDisplayItem(
    crystal: TownViewModel.CrystalData,
    isOwned: Boolean,
    floatAnim: Float,
    onClick: () -> Unit
) {
    val crystalColor = Color(crystal.color.colorHex)
    val jobName = safeStringResource(crystal.unlocksJob.nameRes)
    val cost = crystal.color.baseCost

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(bottom = 8.dp)
    ) {
        // Glowing Background Stand
        Box(contentAlignment = Alignment.Center, modifier = Modifier
            .size(64.dp)
            .offset(y = floatAnim.dp)) {
            Canvas(Modifier.fillMaxSize()) {
                val centerColor = crystalColor.copy(alpha = if (isOwned) 0.2f else 0.4f)
                val edgeColor = crystalColor.copy(alpha = 0f)
                drawCircle(Brush.radialGradient(listOf(centerColor, edgeColor)), radius = size.width / 2f)
            }

            // Detailed Faceted Crystal Drawing (FF Style)
            Canvas(Modifier.size(36.dp, 48.dp)) {
                val w = size.width
                val h = size.height
                val midX = w / 2f
                val topY = 0f
                val midY = h * 0.45f
                val botY = h

                // Color variations for facets to give 3D crystal depth
                val cLight = crystalColor.copy(alpha = if (isOwned) 0.4f else 0.95f)
                val cMid = crystalColor.copy(alpha = if (isOwned) 0.3f else 0.75f)
                val cDark = crystalColor.copy(alpha = if (isOwned) 0.2f else 0.5f)
                val cDeep = crystalColor.copy(alpha = if (isOwned) 0.1f else 0.35f)

                // 1. Top-Left Facet
                drawPath(Path().apply {
                    moveTo(midX, topY); lineTo(0f, midY); lineTo(midX, midY + h * 0.1f); close()
                }, color = cLight)

                // 2. Top-Right Facet (Brighter highlight side)
                drawPath(Path().apply {
                    moveTo(midX, topY); lineTo(w, midY); lineTo(midX, midY + h * 0.1f); close()
                }, color = cMid)

                // 3. Upper Center Left Crown Facet
                drawPath(Path().apply {
                    moveTo(midX, topY); lineTo(midX, midY + h * 0.1f); lineTo(midX * 0.5f, midY * 0.5f); close()
                }, color = cLight.copy(red = (cLight.red * 1.2f).coerceAtMost(1f), green = (cLight.green * 1.2f).coerceAtMost(1f), blue = (cLight.blue * 1.2f).coerceAtMost(1f)))

                // 4. Upper Center Right Crown Facet
                drawPath(Path().apply {
                    moveTo(midX, topY); lineTo(w * 0.5f, midY * 0.5f); lineTo(midX, midY + h * 0.1f); close()
                }, color = cLight)

                // 5. Lower-Left Facet
                drawPath(Path().apply {
                    moveTo(0f, midY); lineTo(midX, botY); lineTo(midX, midY + h * 0.1f); close()
                }, color = cDark)

                // 6. Lower-Right Facet (Shadowed side)
                drawPath(Path().apply {
                    moveTo(w, midY); lineTo(midX, botY); lineTo(midX, midY + h * 0.1f); close()
                }, color = cDeep)

                // 7. Outline / Edges for crystal sharpness
                val outlineColor = Color.White.copy(alpha = if (isOwned) 0.2f else 0.5f)
                val outerPath = Path().apply {
                    moveTo(midX, topY)
                    lineTo(w, midY)
                    lineTo(midX, botY)
                    lineTo(0f, midY)
                    close()
                }
                drawPath(outerPath, color = outlineColor, style = Stroke(1.5f))

                // Center vertical ridge line
                drawLine(outlineColor, Offset(midX, topY), Offset(midX, botY), strokeWidth = 1.5f)
            }
        }
        Spacer(Modifier.height(4.dp))

        // Wood plaque label
        Box(
            Modifier
                .background(Color(0xFF2C1E16), shape = RoundedCornerShape(2.dp))
                .border(1.dp, Color(0xFF4A3219))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(jobName, color = if(isOwned) Color.Gray else Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                if (!isOwned) {
                    Text("${cost}G", color = Color(0xFFFFD700), fontSize = 8.sp)
                } else {
                    Text(stringResource(R.string.owned_status), color = Color(0xFF69F0AE), fontSize = 8.sp)
                }
            }
        }
    }
}

// --- ANDROID STUDIO PREVIEW ---
@Preview(showBackground = true, device = "spec:parent=pixel_5,orientation=landscape")
@Composable
fun PreviewCrystalShopScreen() {
    CrystalShopScreen(NavController(LocalContext.current))
}