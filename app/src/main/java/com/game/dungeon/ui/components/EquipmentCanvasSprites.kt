package com.game.dungeon.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.models.Rarity
import kotlin.math.cos
import kotlin.math.sin

/**
 * Procedural Code-Based Canvas Drawing for Equipment Sprites
 */
@Composable
fun EquipmentSprite(
    item: Item?,
    slot: ItemSlot,
    modifier: Modifier = Modifier,
    animTime: Float = 0f
) {
    Canvas(modifier = modifier) {
        if (item != null) {
            drawItemSprite(item, animTime)
        } else {
            drawEmptySlotPlaceholder(slot)
        }
    }
}

fun DrawScope.drawItemSprite(item: Item, animTime: Float = 0f) {
    val sizeMin = minOf(size.width, size.height)
    val s = sizeMin / 100f
    val offsetX = (size.width - 100f * s) / 2f
    val offsetY = (size.height - 100f * s) / 2f

    val rarityColor = Color(item.rarity.color)

    withTransform({
        translate(left = offsetX, top = offsetY)
    }) {
        when (item.slot) {
            ItemSlot.WEAPON -> drawSwordSprite(s, rarityColor, animTime)
            ItemSlot.ARMOR -> drawHelmetSprite(s, rarityColor, isPlaceholder = false)
            ItemSlot.SHIELD -> drawShieldSprite(s, rarityColor, isPlaceholder = false)
            ItemSlot.ACCESSORY -> drawRingSprite(s, rarityColor, animTime)
        }
    }
}

fun DrawScope.drawEmptySlotPlaceholder(slot: ItemSlot) {
    val sizeMin = minOf(size.width, size.height)
    val s = sizeMin / 100f
    val offsetX = (size.width - 100f * s) / 2f
    val offsetY = (size.height - 100f * s) / 2f
    val gray = Color(0xFF666677).copy(alpha = 0.5f)

    withTransform({
        translate(left = offsetX, top = offsetY)
    }) {
        when (slot) {
            ItemSlot.WEAPON -> drawSwordSprite(s, gray, 0f, isPlaceholder = true)
            ItemSlot.ARMOR -> drawHelmetSprite(s, gray, isPlaceholder = true)
            ItemSlot.SHIELD -> drawShieldSprite(s, gray, isPlaceholder = true)
            ItemSlot.ACCESSORY -> drawRingSprite(s, gray, 0f, isPlaceholder = true)
        }
    }
}

private fun DrawScope.drawSwordSprite(s: Float, accentColor: Color, animTime: Float, isPlaceholder: Boolean = false) {
    val steel = if (isPlaceholder) Color(0xFF444455) else Color(0xFFECF0F1)
    val steelDark = if (isPlaceholder) Color(0xFF333344) else Color(0xFF7F8C8D)
    val gold = if (isPlaceholder) Color(0xFF555544) else Color(0xFFF1C40F)
    val goldDark = if (isPlaceholder) Color(0xFF333322) else Color(0xFFB7950B)

    // Diagonal Sword Blade
    val bladePath = Path().apply {
        moveTo(68f * s, 20f * s)
        lineTo(78f * s, 28f * s)
        lineTo(40f * s, 68f * s)
        lineTo(32f * s, 60f * s)
        close()
    }
    drawPath(bladePath, steel)
    drawPath(bladePath, steelDark, style = Stroke(1.5f * s))

    // Blade Fuller Line
    drawLine(accentColor.copy(alpha = if (isPlaceholder) 0.3f else 0.8f), Offset(68f * s, 26f * s), Offset(38f * s, 62f * s), strokeWidth = 2f * s)

    // Crossguard
    val guardPath = Path().apply {
        moveTo(28f * s, 54f * s)
        lineTo(46f * s, 72f * s)
        lineTo(42f * s, 76f * s)
        lineTo(24f * s, 58f * s)
        close()
    }
    drawPath(guardPath, gold)
    drawPath(guardPath, goldDark, style = Stroke(1.5f * s))

    // Handle Grip
    val hiltPath = Path().apply {
        moveTo(28f * s, 70f * s)
        lineTo(18f * s, 80f * s)
        lineTo(14f * s, 76f * s)
        lineTo(24f * s, 66f * s)
        close()
    }
    drawPath(hiltPath, steelDark)

    // Pommel
    drawCircle(gold, radius = 4f * s, center = Offset(16f * s, 78f * s))

    // Shimmer effect if non-placeholder
    if (!isPlaceholder) {
        val pulse = 0.8f + sin(animTime * 0.005f) * 0.2f
        drawCircle(Color.White.copy(alpha = pulse * 0.7f), radius = 3f * s, center = Offset(72f * s, 24f * s))
    }
}

private fun DrawScope.drawHelmetSprite(s: Float, accentColor: Color, isPlaceholder: Boolean = false) {
    val steel = if (isPlaceholder) Color(0xFF444455) else Color(0xFFBDC3C7)
    val steelDark = if (isPlaceholder) Color(0xFF333344) else Color(0xFF34495E)
    val gold = if (isPlaceholder) Color(0xFF555544) else Color(0xFFF1C40F)

    // Main Helmet Dome
    val domePath = Path().apply {
        moveTo(25f * s, 65f * s)
        lineTo(25f * s, 40f * s)
        cubicTo(25f * s, 20f * s, 75f * s, 20f * s, 75f * s, 40f * s)
        lineTo(75f * s, 65f * s)
        lineTo(65f * s, 70f * s)
        lineTo(35f * s, 70f * s)
        close()
    }
    drawPath(domePath, steel)
    drawPath(domePath, steelDark, style = Stroke(2f * s))

    // Visor Slit
    drawRect(Color.Black, Offset(30f * s, 48f * s), Size(40f * s, 8f * s))
    drawRect(accentColor.copy(alpha = if (isPlaceholder) 0.3f else 0.8f), Offset(32f * s, 50f * s), Size(36f * s, 4f * s))

    // Helmet Crest / Plume
    val plumePath = Path().apply {
        moveTo(50f * s, 22f * s)
        quadraticTo(50f * s, 10f * s, 65f * s, 12f * s)
        quadraticTo(55f * s, 20f * s, 50f * s, 22f * s)
    }
    drawPath(plumePath, accentColor)

    // Helmet Trim
    drawRect(gold, Offset(25f * s, 64f * s), Size(50f * s, 4f * s))
}

private fun DrawScope.drawShieldSprite(s: Float, accentColor: Color, isPlaceholder: Boolean = false) {
    val steelDark = if (isPlaceholder) Color(0xFF333344) else Color(0xFF2C3E50)
    val gold = if (isPlaceholder) Color(0xFF555544) else Color(0xFFF1C40F)

    // Heater Shield Shape
    val shieldPath = Path().apply {
        moveTo(25f * s, 25f * s)
        lineTo(75f * s, 25f * s)
        lineTo(75f * s, 50f * s)
        quadraticTo(70f * s, 75f * s, 50f * s, 85f * s)
        quadraticTo(30f * s, 75f * s, 25f * s, 50f * s)
        close()
    }
    drawPath(shieldPath, steelDark)
    drawPath(shieldPath, gold, style = Stroke(3f * s))

    // Shield Core Emblem Cross
    drawRect(accentColor, Offset(46f * s, 32f * s), Size(8f * s, 42f * s))
    drawRect(accentColor, Offset(32f * s, 44f * s), Size(36f * s, 8f * s))

    // Boss Center Gem
    drawCircle(gold, radius = 6f * s, center = Offset(50f * s, 48f * s))
    drawCircle(if (isPlaceholder) Color.Gray else Color.White, radius = 3f * s, center = Offset(50f * s, 48f * s))
}

private fun DrawScope.drawRingSprite(s: Float, accentColor: Color, animTime: Float, isPlaceholder: Boolean = false) {
    val gold = if (isPlaceholder) Color(0xFF555544) else Color(0xFFF1C40F)
    val goldDark = if (isPlaceholder) Color(0xFF333322) else Color(0xFFB7950B)

    // Ring Golden Band
    drawCircle(gold, radius = 24f * s, center = Offset(50f * s, 54f * s), style = Stroke(6f * s))
    drawCircle(goldDark, radius = 27f * s, center = Offset(50f * s, 54f * s), style = Stroke(1.5f * s))

    // Gem Setting Mount
    val mountPath = Path().apply {
        moveTo(42f * s, 32f * s)
        lineTo(58f * s, 32f * s)
        lineTo(54f * s, 26f * s)
        lineTo(46f * s, 26f * s)
        close()
    }
    drawPath(mountPath, gold)

    // Diamond Gem on Ring
    val gemPath = Path().apply {
        moveTo(50f * s, 16f * s)
        lineTo(60f * s, 26f * s)
        lineTo(50f * s, 34f * s)
        lineTo(40f * s, 26f * s)
        close()
    }
    drawPath(gemPath, accentColor)
    drawPath(gemPath, Color.White, style = Stroke(1.5f * s))

    // Sparkle
    if (!isPlaceholder) {
        val pulse = 0.8f + sin(animTime * 0.005f) * 0.2f
        drawCircle(Color.White.copy(alpha = pulse), radius = 2.5f * s, center = Offset(47f * s, 22f * s))
    }
}
