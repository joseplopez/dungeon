package com.game.dungeon.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
fun PixelButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    active: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)

    val bgColor = when {
        !enabled -> StoneGray
        active -> GoldBright
        else -> BgMedium
    }
    val textColor = if (active) BgDarkest else GoldBright

    Box(
        modifier = modifier
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, enabled = enabled) { onClick() }
            .background(bgColor)
            .border(2.dp, if (active) GoldBright else GoldDark)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Center
    ) {
        Text(label, style = PixelBody, color = textColor)
    }
}

@Composable
fun PixelHpBar(current: Int, max: Int, modifier: Modifier = Modifier, showText: Boolean = false) {
    val ratio = (current.toFloat() / max.toFloat()).coerceIn(0f, 1f)
    val animRatio by animateFloatAsState(ratio, animationSpec = tween(300))
    val barColor = when {
        ratio > 0.5f -> HpGreen
        ratio > 0.25f -> HpYellow
        else -> HpRed
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier
                .height(8.dp)
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
    val animRatio by animateFloatAsState(ratio, animationSpec = tween(500))

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
    n >= 1_000_000 -> "${n / 1_000_000}M"
    n >= 1_000 -> "${n / 1_000}K"
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
            val cornerSize = 8.dp.toPx()
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
