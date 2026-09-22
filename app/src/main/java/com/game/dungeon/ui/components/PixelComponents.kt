package com.game.dungeon.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.dungeon.ui.theme.*
import kotlinx.coroutines.delay
import androidx.compose.foundation.Canvas

@Composable
fun PixelPanel(
    modifier: Modifier = Modifier,
    borderColor: Color = GoldDark,
    bgColor: Color = BgPanel,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(0.dp))
            .drawWithContent {
                drawContent()
                val w = size.width
                val h = size.height
                val stroke2 = 2.dp.toPx()
                val stroke1 = 1.dp.toPx()
                val corner = 4.dp.toPx()

                // Outer border frame
                drawRect(
                    color = borderColor,
                    style = Stroke(width = stroke2)
                )
                // Inner highlight & shadow lines for retro inset depth
                // Top-left highlight
                drawLine(
                    color = Color.White.copy(alpha = 0.25f),
                    start = Offset(stroke2, stroke2),
                    end = Offset(w - stroke2, stroke2),
                    strokeWidth = stroke1
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.25f),
                    start = Offset(stroke2, stroke2),
                    end = Offset(stroke2, h - stroke2),
                    strokeWidth = stroke1
                )
                // Bottom-right shadow
                drawLine(
                    color = Color.Black.copy(alpha = 0.5f),
                    start = Offset(stroke2, h - stroke2),
                    end = Offset(w - stroke2, h - stroke2),
                    strokeWidth = stroke1
                )
                drawLine(
                    color = Color.Black.copy(alpha = 0.5f),
                    start = Offset(w - stroke2, stroke2),
                    end = Offset(w - stroke2, h - stroke2),
                    strokeWidth = stroke1
                )
                // Corner pixel accent blocks
                listOf(
                    Offset(0f, 0f),
                    Offset(w - corner, 0f),
                    Offset(0f, h - corner),
                    Offset(w - corner, h - corner)
                ).forEach {
                    drawRect(color = borderColor, topLeft = it, size = Size(corner, corner))
                }
            }
            .padding(8.dp),
        content = content
    )
}

@Composable
fun PixelButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    active: Boolean = false,
    horizontalPadding: androidx.compose.ui.unit.Dp = 12.dp,
    verticalPadding: androidx.compose.ui.unit.Dp = 6.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "scale")

    val bgColor = when {
        !enabled -> StoneGray
        active -> GoldBright
        else -> BgMedium
    }
    val textColor = if (active) BgDarkest else GoldBright
    val frameBorderColor = if (active) GoldBright else GoldDark

    Box(
        modifier = modifier
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, enabled = enabled) { onClick() }
            .background(bgColor)
            .drawWithContent {
                drawContent()
                val w = size.width
                val h = size.height
                val borderPx = 2.dp.toPx()
                val highlightPx = 1.dp.toPx()

                // Outer border
                drawRect(color = frameBorderColor, style = Stroke(width = borderPx))

                if (enabled) {
                    // Beveled retro button highlights
                    val lightColor = if (isPressed) Color.Black.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.35f)
                    val shadowColor = if (isPressed) Color.White.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.5f)

                    // Top & Left
                    drawLine(lightColor, Offset(borderPx, borderPx), Offset(w - borderPx, borderPx), highlightPx)
                    drawLine(lightColor, Offset(borderPx, borderPx), Offset(borderPx, h - borderPx), highlightPx)
                    // Bottom & Right
                    drawLine(shadowColor, Offset(borderPx, h - borderPx), Offset(w - borderPx, h - borderPx), highlightPx)
                    drawLine(shadowColor, Offset(w - borderPx, borderPx), Offset(w - borderPx, h - borderPx), highlightPx)
                }
            }
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        contentAlignment = Center
    ) {
        Text(
            label, 
            style = PixelBody.copy(textAlign = TextAlign.Center),
            color = textColor
        )
    }
}

@Composable
fun AdRewardIconButton(
    isMagicite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val baseScale by animateFloatAsState(if (isPressed) 0.92f else 1.0f, label = "baseScale")
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .scale(baseScale * pulseScale)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .background(BgMedium)
            .border(2.dp, if (isMagicite) Color(0xFF00E5FF) else GoldBright)
            .padding(4.dp),
        contentAlignment = Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            
            if (!isMagicite) {
                // Draw Retro Pixel Coin
                // Main yellow/gold circle
                drawCircle(
                    color = Color(0xFFFFD700),
                    radius = minOf(w, h) * 0.4f,
                    center = Offset(w * 0.45f, h * 0.5f)
                )
                // Dark gold border/shadow for depth
                drawCircle(
                    color = Color(0xFFC5A000),
                    radius = minOf(w, h) * 0.4f,
                    center = Offset(w * 0.45f, h * 0.5f),
                    style = Stroke(width = 2.dp.toPx())
                )
                // Inner core detail
                drawCircle(
                    color = Color(0xFFFFF8DC),
                    radius = minOf(w, h) * 0.15f,
                    center = Offset(w * 0.42f, h * 0.48f)
                )
            } else {
                // Draw Retro Pixel Diamond/Crystal
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.45f, h * 0.15f) // Top
                    lineTo(w * 0.75f, h * 0.5f)  // Right
                    lineTo(w * 0.45f, h * 0.85f) // Bottom
                    lineTo(w * 0.15f, h * 0.5f)  // Left
                    close()
                }
                drawPath(path = path, color = Color(0xFF00E5FF))
                
                // Highlight path for facet depth
                val highlightPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.45f, h * 0.15f)
                    lineTo(w * 0.45f, h * 0.85f)
                    lineTo(w * 0.15f, h * 0.5f)
                    close()
                }
                drawPath(path = highlightPath, color = Color(0xFFE0FFFF).copy(alpha = 0.6f))
            }
            
            // Draw prominent green "+" icon in the top right / foreground
            val plusSize = minOf(w, h) * 0.35f
            val px = w * 0.72f
            val py = h * 0.35f
            
            // Draw horizontal bar of the plus
            drawRect(
                color = Color(0xFF00FF00),
                topLeft = Offset(px - plusSize / 2, py - 1.5.dp.toPx()),
                size = Size(plusSize, 3.dp.toPx())
            )
            // Draw vertical bar of the plus
            drawRect(
                color = Color(0xFF00FF00),
                topLeft = Offset(px - 1.5.dp.toPx(), py - plusSize / 2),
                size = Size(3.dp.toPx(), plusSize)
            )
            // Black thin crisp outline around the green plus for retro pop
            drawRect(
                color = Color.Black,
                topLeft = Offset(px - plusSize / 2, py - 1.5.dp.toPx()),
                size = Size(plusSize, 3.dp.toPx()),
                style = Stroke(width = 0.5.dp.toPx())
            )
            drawRect(
                color = Color.Black,
                topLeft = Offset(px - 1.5.dp.toPx(), py - plusSize / 2),
                size = Size(3.dp.toPx(), plusSize),
                style = Stroke(width = 0.5.dp.toPx())
            )
        }
    }
}

@Composable
fun AdBoostIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val baseScale by animateFloatAsState(if (isPressed) 0.92f else 1.0f, label = "baseScale")
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .scale(baseScale * pulseScale)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .background(BgMedium)
            .border(2.dp, GoldBright)
            .padding(4.dp),
        contentAlignment = Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(horizontal = 2.dp)
        ) {
            Canvas(modifier = Modifier.size(width = 12.dp, height = 16.dp)) {
                val w = size.width
                val h = size.height
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.5f, 0f)
                    lineTo(w, h * 0.45f)
                    lineTo(w * 0.6f, h * 0.45f)
                    lineTo(w * 0.8f, h)
                    lineTo(w * 0.1f, h * 0.55f)
                    lineTo(w * 0.5f, h * 0.55f)
                    close()
                }
                val shadowPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.5f + 1.dp.toPx(), 1.dp.toPx())
                    lineTo(w + 1.dp.toPx(), h * 0.45f + 1.dp.toPx())
                    lineTo(w * 0.6f + 1.dp.toPx(), h * 0.45f + 1.dp.toPx())
                    lineTo(w * 0.8f + 1.dp.toPx(), h + 1.dp.toPx())
                    lineTo(w * 0.1f + 1.dp.toPx(), h * 0.55f + 1.dp.toPx())
                    lineTo(w * 0.5f + 1.dp.toPx(), h * 0.55f + 1.dp.toPx())
                    close()
                }
                drawPath(path = shadowPath, color = Color(0xFFC5A000))
                drawPath(path = path, color = Color(0xFFFFD700))
            }
            Text(
                "2x",
                style = PixelSmall,
                color = GoldBright
            )
        }
    }
}

@Composable
fun PixelHpBar(
    current: Int,
    max: Int,
    modifier: Modifier = Modifier,
    showText: Boolean = false,
    barHeight: androidx.compose.ui.unit.Dp = 8.dp
) {
    val ratio = (current.toFloat() / max.toFloat()).coerceIn(0f, 1f)
    val animRatio by animateFloatAsState(ratio, animationSpec = tween(300), label = "animRatio")
    val barColor = when {
        ratio > 0.5f -> HpGreen
        ratio > 0.25f -> HpYellow
        else -> HpRed
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier
                .height(barHeight)
                .border(1.dp, Color.Black)
        ) {
            Box(Modifier.fillMaxSize().background(Color(0xFF220000))) // dark red bg
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animRatio)
                    .background(barColor)
            )
            // 3 vertical lines dividing bar into quarters
            Row(Modifier.fillMaxSize()) {
                repeat(3) {
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        if (showText) {
            Text("$current/$max", style = PixelSmall, color = Color.White)
        }
    }
}

@Composable
fun PixelExpBar(current: Int, max: Int, modifier: Modifier = Modifier) {
    val ratio = (current.toFloat() / max.toFloat()).coerceIn(0f, 1f)
    val animRatio by animateFloatAsState(ratio, animationSpec = tween(500), label = "animRatio")

    Box(
        modifier
            .height(4.dp)
            .border(1.dp, Color.Black)
            .background(Color(0xFF001122)) // deep blue bg
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(animRatio)
                .background(HeroBlue)
        )
    }
}

@Composable
fun PixelSpriteBox(
    modifier: Modifier = Modifier,
    placeholderColor: Color,
    label: String = "",
    emoji: String = ""
) {
    Box(
        modifier = modifier
            .background(placeholderColor.copy(alpha = 0.3f))
            .border(1.dp, placeholderColor),
        contentAlignment = Center
    ) {
        if (emoji.isNotEmpty()) {
            Text(emoji, fontSize = 24.sp)
        } else {
            Text(
                label,
                style = PixelSmall,
                modifier = Modifier.align(BottomCenter)
            )
        }
    }
}

@Composable
fun PixelGoldDisplay(amount: Long) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("🪙", fontSize = 14.sp)
        Text(formatGold(amount), style = PixelGold)
    }
}

fun formatGold(n: Long): String = when {
    n >= 1_000_000 -> "%.3fM".format(n / 1_000_000f)
    n >= 1_000 -> "%.3fK".format(n / 1_000f)
    else -> n.toString()
}

@Composable
fun FloatingDamageText(text: String, color: Color, onDone: () -> Unit) {
    var offsetY by remember { mutableStateOf(0f) }
    var alpha by remember { mutableStateOf(1f) }
    LaunchedEffect(Unit) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < 700) {
            val progress = (System.currentTimeMillis() - start) / 700f
            offsetY = -40f * progress
            alpha = 1f - progress
            delay(16)
        }
        onDone()
    }
    Text(
        text,
        style = PixelBody.copy(color = color.copy(alpha = alpha), fontSize = 12.sp),
        modifier = Modifier.offset(y = offsetY.dp)
    )
}

@Composable
fun PixelDivider(color: Color = GoldDark) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(color)
    ) {
        // 4px dots at each end (2x2 pixels)
        Box(
            Modifier
                .size(4.dp)
                .align(Alignment.CenterStart)
                .background(color)
        )
        Box(
            Modifier
                .size(4.dp)
                .align(Alignment.CenterEnd)
                .background(color)
        )
    }
}

@Composable
fun GoldenBorderBox(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier) {
        content()
        Canvas(Modifier.matchParentSize()) {
            val cornerSize = 6.dp.toPx()
            val stroke = 2.dp.toPx()
            val gold = GoldBright.copy(alpha = 0.9f)
            val darkGold = GoldDark

            // Outer 2px gold border
            drawRect(color = gold, style = Stroke(width = stroke))

            // Inner 1px border for double-line JRPG frame effect
            drawRect(
                color = darkGold,
                topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
                size = Size(size.width - 6.dp.toPx(), size.height - 6.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )

            // Corner squares with double-beveled gold/dark accent
            listOf(
                Offset(0f, 0f),
                Offset(size.width - cornerSize, 0f),
                Offset(0f, size.height - cornerSize),
                Offset(size.width - cornerSize, size.height - cornerSize)
            ).forEach { cornerOffset ->
                drawRect(color = GoldBright, topLeft = cornerOffset, size = Size(cornerSize, cornerSize))
                drawRect(color = GoldDark, topLeft = Offset(cornerOffset.x + 1.dp.toPx(), cornerOffset.y + 1.dp.toPx()), size = Size(cornerSize - 2.dp.toPx(), cornerSize - 2.dp.toPx()))
            }
        }
    }
}
