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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.Dp

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
                // Outer 2dp stroke
                drawRect(
                    color = borderColor,
                    style = Stroke(width = 2.dp.toPx())
                )
                // Inner 1dp stroke for depth
                drawRect(
                    color = borderColor.copy(alpha = 0.4f),
                    topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                    size = Size(size.width - 4.dp.toPx(), size.height - 4.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            .padding(8.dp),
        content = content
    )
}

@Composable
fun AutoResizedText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    maxLines: Int = 2,
    minFontSize: TextUnit = 8.sp,
    textAlign: TextAlign = TextAlign.Center
) {
    var resizedTextStyle by remember(text, style) {
        mutableStateOf(style.copy(textAlign = textAlign))
    }
    var readyToDraw by remember(text, style) { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        style = resizedTextStyle,
        maxLines = maxLines,
        softWrap = true,
        onTextLayout = { result ->
            if (result.hasVisualOverflow) {
                val currentSize = resizedTextStyle.fontSize
                val baseSize = if (currentSize != TextUnit.Unspecified) currentSize else style.fontSize
                val effectiveBase = if (baseSize != TextUnit.Unspecified) baseSize else 14.sp
                if (effectiveBase.value > minFontSize.value) {
                    val newSize = (effectiveBase.value - 1f).coerceAtLeast(minFontSize.value).sp
                    resizedTextStyle = resizedTextStyle.copy(
                        fontSize = newSize,
                        lineHeight = (newSize.value * 1.15f).sp
                    )
                } else {
                    readyToDraw = true
                }
            } else {
                readyToDraw = true
            }
        }
    )
}

@Composable
fun PixelButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    active: Boolean = false,
    horizontalPadding: Dp = 12.dp,
    verticalPadding: Dp = 4.dp,
    fontSize: TextUnit = TextUnit.Unspecified,
    maxLines: Int = 2,
    minFontSize: TextUnit = 8.sp
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

    val textStyle = if (fontSize != TextUnit.Unspecified) {
        PixelBody.copy(fontSize = fontSize, textAlign = TextAlign.Center)
    } else {
        PixelBody.copy(textAlign = TextAlign.Center)
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 64.dp, minHeight = 38.dp)
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, enabled = enabled) { onClick() }
            .background(bgColor)
            .border(2.dp, if (active) GoldBright else GoldDark)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        contentAlignment = Center
    ) {
        AutoResizedText(
            text = label,
            style = textStyle,
            color = textColor,
            maxLines = maxLines,
            minFontSize = minFontSize
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
fun PixelGoldDisplay(
    amount: Long,
    modifier: Modifier = Modifier,
    style: TextStyle = PixelGold,
    iconSize: TextUnit = 14.sp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("🪙", fontSize = iconSize)
        Text(formatGold(amount), style = style)
    }
}

@Composable
fun PixelGoldDisplay(
    amount: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = PixelGold,
    iconSize: TextUnit = 14.sp
) = PixelGoldDisplay(amount.toLong(), modifier, style, iconSize)

@Composable
fun PixelMagiciteDisplay(
    amount: Long,
    modifier: Modifier = Modifier,
    style: TextStyle = PixelGold,
    iconSize: TextUnit = 14.sp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("💎", fontSize = iconSize)
        Text(formatMagicite(amount), style = style)
    }
}

@Composable
fun PixelMagiciteDisplay(
    amount: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = PixelGold,
    iconSize: TextUnit = 14.sp
) = PixelMagiciteDisplay(amount.toLong(), modifier, style, iconSize)

fun formatAmount(n: Long): String {
    val absN = kotlin.math.abs(n)
    val formatted = when {
        absN >= 1_000_000 -> {
            val v = absN / 1_000_000.0
            val s = String.format(java.util.Locale.US, "%.3f", v)
                .dropLastWhile { it == '0' }
                .removeSuffix(".")
            "${s}M"
        }
        absN >= 1_000 -> {
            val v = absN / 1_000.0
            val s = String.format(java.util.Locale.US, "%.3f", v)
                .dropLastWhile { it == '0' }
                .removeSuffix(".")
            "${s}K"
        }
        else -> absN.toString()
    }
    return if (n < 0) "-$formatted" else formatted
}

fun formatAmount(n: Int): String = formatAmount(n.toLong())

fun formatGold(n: Long): String = formatAmount(n)
fun formatGold(n: Int): String = formatAmount(n.toLong())

fun formatMagicite(n: Long): String = formatAmount(n)
fun formatMagicite(n: Int): String = formatAmount(n.toLong())

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
fun GoldenBorderBox(modifier: Modifier = Modifier, cornerSize: Dp = 8.dp, content: @Composable BoxScope.() -> Unit) {
    Box(modifier) {
        content()
        Canvas(Modifier.matchParentSize()) {
            val cornerSize = cornerSize.toPx()
            val stroke = 2.dp.toPx()
            val gold = GoldBright.copy(alpha = 0.8f)
            // Draw 2px gold border
            drawRect(color = gold, style = Stroke(width = stroke))
            // Draw 1px inner border
            drawRect(
                color = GoldDark,
                topLeft = Offset(4f, 4f),
                size = Size(size.width - 8f, size.height - 8f),
                style = Stroke(width = 1.dp.toPx())
            )
            // Corner squares (filled gold)
            listOf(
                Offset(0f, 0f),
                Offset(size.width - cornerSize, 0f),
                Offset(0f, size.height - cornerSize),
                Offset(size.width - cornerSize, size.height - cornerSize)
            ).forEach {
                drawRect(color = GoldBright, topLeft = it, size = Size(cornerSize, cornerSize))
            }
        }
    }
}
