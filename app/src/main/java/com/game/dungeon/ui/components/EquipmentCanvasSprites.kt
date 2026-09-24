package com.game.dungeon.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
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
 * Upgraded with Relic-style multi-layered procedural art and rarity-based progression.
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

    withTransform({
        translate(left = offsetX, top = offsetY)
    }) {
        when (item.slot) {
            ItemSlot.WEAPON -> when (item.rarity) {
                Rarity.COMMON -> drawCommonSword(s)
                Rarity.RARE -> drawRareSword(s, animTime)
                Rarity.EPIC -> drawEpicSword(s, animTime)
                Rarity.LEGENDARY -> drawLegendarySword(s, animTime)
            }
            ItemSlot.ARMOR -> when (item.rarity) {
                Rarity.COMMON -> drawCommonHelmet(s)
                Rarity.RARE -> drawRareHelmet(s, animTime)
                Rarity.EPIC -> drawEpicHelmet(s, animTime)
                Rarity.LEGENDARY -> drawLegendaryHelmet(s, animTime)
            }
            ItemSlot.SHIELD -> when (item.rarity) {
                Rarity.COMMON -> drawCommonShield(s)
                Rarity.RARE -> drawRareShield(s, animTime)
                Rarity.EPIC -> drawEpicShield(s, animTime)
                Rarity.LEGENDARY -> drawLegendaryShield(s, animTime)
            }
            ItemSlot.ACCESSORY -> when (item.rarity) {
                Rarity.COMMON -> drawCommonAccessory(s)
                Rarity.RARE -> drawRareAccessory(s, animTime)
                Rarity.EPIC -> drawEpicAccessory(s, animTime)
                Rarity.LEGENDARY -> drawLegendaryAccessory(s, animTime)
            }
        }
    }
}

fun DrawScope.drawEmptySlotPlaceholder(slot: ItemSlot) {
    val sizeMin = minOf(size.width, size.height)
    val s = sizeMin / 100f
    val offsetX = (size.width - 100f * s) / 2f
    val offsetY = (size.height - 100f * s) / 2f

    withTransform({
        translate(left = offsetX, top = offsetY)
    }) {
        when (slot) {
            ItemSlot.WEAPON -> drawCommonSword(s, isPlaceholder = true)
            ItemSlot.ARMOR -> drawCommonHelmet(s, isPlaceholder = true)
            ItemSlot.SHIELD -> drawCommonShield(s, isPlaceholder = true)
            ItemSlot.ACCESSORY -> drawCommonAccessory(s, isPlaceholder = true)
        }
    }
}

// ============================================================================
// 1. WEAPON SPRITES (COMMON -> RARE -> EPIC -> LEGENDARY)
// ============================================================================

private fun DrawScope.drawCommonSword(s: Float, isPlaceholder: Boolean = false) {
    val steel = if (isPlaceholder) Color(0xFF555566).copy(alpha = 0.4f) else Color(0xFFBDC3C7)
    val steelDark = if (isPlaceholder) Color(0xFF333344).copy(alpha = 0.4f) else Color(0xFF7F8C8D)
    val wood = if (isPlaceholder) Color(0xFF443322).copy(alpha = 0.4f) else Color(0xFF6E2C00)
    val ironGuard = if (isPlaceholder) Color(0xFF444455).copy(alpha = 0.4f) else Color(0xFF34495E)

    // Straight Iron Blade
    val bladePath = Path().apply {
        moveTo(65f * s, 22f * s)
        lineTo(75f * s, 32f * s)
        lineTo(42f * s, 65f * s)
        lineTo(35f * s, 58f * s)
        close()
    }
    drawPath(bladePath, steel)
    drawPath(bladePath, steelDark, style = Stroke(1.5f * s))

    // Fuller Groove Line
    drawLine(steelDark, Offset(66f * s, 28f * s), Offset(40f * s, 58f * s), strokeWidth = 1.5f * s)

    // Simple Iron Crossguard
    val guardPath = Path().apply {
        moveTo(30f * s, 52f * s)
        lineTo(48f * s, 70f * s)
        lineTo(44f * s, 74f * s)
        lineTo(26f * s, 56f * s)
        close()
    }
    drawPath(guardPath, ironGuard)

    // Wooden Handle Grip
    val hiltPath = Path().apply {
        moveTo(30f * s, 68f * s)
        lineTo(20f * s, 78f * s)
        lineTo(16f * s, 74f * s)
        lineTo(26f * s, 64f * s)
        close()
    }
    drawPath(hiltPath, wood)

    // Round Steel Pommel
    drawCircle(ironGuard, radius = 4f * s, center = Offset(18f * s, 76f * s))

    if (isPlaceholder) {
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4f * s, 4f * s), 0f)
        drawPath(bladePath, Color.Gray.copy(alpha = 0.5f), style = Stroke(1.5f * s, pathEffect = dashEffect))
    }
}

private fun DrawScope.drawRareSword(s: Float, animTime: Float) {
    val steel = Color(0xFFECF0F1)
    val steelDark = Color(0xFF7F8C8D)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val sapphire = Color(0xFF29B6F6)
    val blueAura = Color(0xFF00E5FF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Soft Sapphire Blade Aura
    val auraPath = Path().apply {
        moveTo(68f * s, 16f * s)
        lineTo(82f * s, 30f * s)
        lineTo(42f * s, 70f * s)
        lineTo(28f * s, 56f * s)
        close()
    }
    drawPath(auraPath, blueAura.copy(alpha = 0.25f * pulse))

    // Polished Knight Broadsword Blade
    val bladePath = Path().apply {
        moveTo(68f * s, 18f * s)
        lineTo(80f * s, 30f * s)
        lineTo(44f * s, 66f * s)
        lineTo(32f * s, 54f * s)
        close()
    }
    drawPath(bladePath, steel)
    drawPath(bladePath, steelDark, style = Stroke(1.5f * s))

    // Glowing Central Fuller Line
    drawLine(sapphire.copy(alpha = pulse), Offset(69f * s, 25f * s), Offset(39f * s, 59f * s), strokeWidth = 2.5f * s)
    drawLine(Color.White, Offset(69f * s, 25f * s), Offset(39f * s, 59f * s), strokeWidth = 1f * s)

    // Golden Angled Crossguard
    val guardPath = Path().apply {
        moveTo(28f * s, 50f * s)
        lineTo(50f * s, 72f * s)
        lineTo(44f * s, 78f * s)
        lineTo(22f * s, 56f * s)
        close()
    }
    drawPath(guardPath, gold)
    drawPath(guardPath, goldDark, style = Stroke(1.5f * s))

    // Guard Sapphire Gem
    drawCircle(sapphire, radius = 3.5f * s, center = Offset(36f * s, 64f * s))
    drawCircle(Color.White, radius = 1.2f * s, center = Offset(35f * s, 63f * s))

    // Wrapped Leather Handle
    val hiltPath = Path().apply {
        moveTo(28f * s, 70f * s)
        lineTo(18f * s, 80f * s)
        lineTo(14f * s, 76f * s)
        lineTo(24f * s, 66f * s)
        close()
    }
    drawPath(hiltPath, steelDark)
    drawLine(gold, Offset(27f * s, 71f * s), Offset(17f * s, 81f * s), strokeWidth = 1.5f * s)

    // Golden Octagonal Pommel
    drawCircle(gold, radius = 5f * s, center = Offset(16f * s, 78f * s))
    drawCircle(goldDark, radius = 5f * s, center = Offset(16f * s, 78f * s), style = Stroke(1.2f * s))

    // Shimmer Sparkle
    val shimmerPhase = (animTime * 0.004f) % 3.14159f
    drawCircle(Color.White.copy(alpha = sin(shimmerPhase)), radius = 2.5f * s, center = Offset(72f * s, 24f * s))
}

private fun DrawScope.drawEpicSword(s: Float, animTime: Float) {
    val flameRed = Color(0xFFE74C3C)
    val flameOrange = Color(0xFFE67E22)
    val flameYellow = Color(0xFFF1C40F)
    val obsidian = Color(0xFF2C3E50)
    val steel = Color(0xFFECF0F1)
    val gold = Color(0xFFF39C12)
    val ruby = Color(0xFFC0392B)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // 1. Fiery Red/Orange Aura behind Blade
    val fireAura = Path().apply {
        moveTo(50f * s, 10f * s)
        quadraticTo(76f * s, 35f * s, 65f * s, 65f * s)
        lineTo(35f * s, 65f * s)
        quadraticTo(24f * s, 35f * s, 50f * s, 10f * s)
        close()
    }
    drawPath(fireAura, flameRed.copy(alpha = 0.35f * pulse))

    // Rising Fire Embers
    repeat(6) { i ->
        val sparkPhase = (animTime * 0.004f + i * 1.1f) % 3.14159f
        val sx = 50f * s + sin(sparkPhase * 2f + i) * 18f * s
        val sy = 62f * s - (sparkPhase / 3.14159f) * 48f * s
        drawCircle(flameYellow.copy(alpha = sin(sparkPhase)), radius = 1.8f * s, center = Offset(sx, sy))
    }

    // 2. Serrated Flame Blade (pointing straight up)
    val bladePath = Path().apply {
        moveTo(50f * s, 14f * s)
        lineTo(58f * s, 26f * s)
        lineTo(54f * s, 34f * s)
        lineTo(60f * s, 42f * s)
        lineTo(56f * s, 62f * s)
        lineTo(44f * s, 62f * s)
        lineTo(40f * s, 42f * s)
        lineTo(46f * s, 34f * s)
        lineTo(42f * s, 26f * s)
        close()
    }
    drawPath(bladePath, steel)
    drawPath(bladePath, obsidian, style = Stroke(1.5f * s))

    // Glowing Inner Blade Inscriptions / Fuller
    drawLine(flameYellow.copy(alpha = pulse), Offset(50f * s, 22f * s), Offset(50f * s, 58f * s), strokeWidth = 2.5f * s)
    drawLine(Color.White, Offset(50f * s, 24f * s), Offset(50f * s, 56f * s), strokeWidth = 1f * s)

    // 3. Winged Double Crossguard with Ruby
    val guardPath = Path().apply {
        moveTo(50f * s, 62f * s)
        lineTo(70f * s, 56f * s)
        lineTo(74f * s, 66f * s)
        lineTo(58f * s, 68f * s)
        lineTo(50f * s, 66f * s)
        lineTo(42f * s, 68f * s)
        lineTo(26f * s, 66f * s)
        lineTo(30f * s, 56f * s)
        close()
    }
    drawPath(guardPath, gold)
    drawCircle(ruby, radius = 4f * s, center = Offset(50f * s, 64f * s))
    drawCircle(Color.White, radius = 1.2f * s, center = Offset(48.5f * s, 62.5f * s))

    // Handle & Pommel Crystal
    drawRect(obsidian, Offset(47f * s, 68f * s), Size(6f * s, 12f * s))
    drawLine(flameOrange, Offset(47f * s, 71f * s), Offset(53f * s, 73f * s), strokeWidth = 1.2f * s)
    drawLine(flameOrange, Offset(47f * s, 75f * s), Offset(53f * s, 77f * s), strokeWidth = 1.2f * s)

    drawCircle(gold, radius = 5f * s, center = Offset(50f * s, 83f * s))
    drawCircle(ruby, radius = 3f * s, center = Offset(50f * s, 83f * s))
}

private fun DrawScope.drawLegendarySword(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val goldBright = Color(0xFFFFF2A3)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    val rot = animTime * 0.002f

    // 1. Rotating Golden Rune Ring around Guard
    drawOval(
        color = gold,
        topLeft = Offset(center.x - 32f * s, center.y + 10f * s),
        size = Size(64f * s, 20f * s),
        style = Stroke(2f * s)
    )

    // Floating Rune Accents on Ring
    repeat(4) { i ->
        val angle = rot + i * (3.14159f / 2f)
        val rx = center.x + cos(angle) * 32f * s
        val ry = center.y + 20f * s + sin(angle) * 8f * s
        drawCircle(cyanGlow, radius = 2.5f * s, center = Offset(rx, ry))
    }

    // 2. Radiant Golden/Cyan Sunburst Aura behind Blade
    val sunAura = Path().apply {
        moveTo(50f * s, 6f * s)
        quadraticTo(78f * s, 32f * s, 62f * s, 62f * s)
        lineTo(38f * s, 62f * s)
        quadraticTo(22f * s, 32f * s, 50f * s, 6f * s)
        close()
    }
    drawPath(sunAura, cyanGlow.copy(alpha = 0.3f * pulse))

    // Orbiting Starlight Sparkles
    repeat(8) { i ->
        val sparkAngle = (animTime * 0.003f + i * 0.8f) % 6.28318f
        val sx = 50f * s + cos(sparkAngle) * (20f + sin(sparkAngle) * 6f) * s
        val sy = 38f * s + sin(sparkAngle) * (26f + cos(sparkAngle) * 6f) * s
        drawCircle(goldBright, radius = 2f * s, center = Offset(sx, sy))
        drawCircle(pureWhite, radius = 1f * s, center = Offset(sx, sy))
    }

    // 3. Divine Dual-Edged Sword Blade
    val bladePath = Path().apply {
        moveTo(50f * s, 10f * s)
        lineTo(59f * s, 22f * s)
        lineTo(57f * s, 64f * s)
        lineTo(43f * s, 64f * s)
        lineTo(41f * s, 22f * s)
        close()
    }
    drawPath(bladePath, goldBright)
    drawPath(bladePath, goldDark, style = Stroke(1.5f * s))

    // Double Blade Fuller Line glowing White-Hot Gold
    drawLine(pureWhite, Offset(50f * s, 16f * s), Offset(50f * s, 60f * s), strokeWidth = 3f * s)
    drawLine(cyanGlow, Offset(50f * s, 18f * s), Offset(50f * s, 58f * s), strokeWidth = 1.2f * s)

    // 4. Winged Golden Dragon/Phoenix Crossguard
    val guardPath = Path().apply {
        moveTo(50f * s, 62f * s)
        quadraticTo(68f * s, 54f * s, 76f * s, 58f * s)
        lineTo(70f * s, 68f * s)
        lineTo(56f * s, 67f * s)
        lineTo(50f * s, 66f * s)
        lineTo(44f * s, 67f * s)
        lineTo(30f * s, 68f * s)
        lineTo(24f * s, 58f * s)
        quadraticTo(32f * s, 54f * s, 50f * s, 62f * s)
        close()
    }
    drawPath(guardPath, gold)
    drawPath(guardPath, goldDark, style = Stroke(1.5f * s))

    // Central Sunburst Gem
    drawCircle(cyanGlow, radius = 5f * s, center = Offset(50f * s, 65f * s))
    drawCircle(pureWhite, radius = 2.5f * s, center = Offset(50f * s, 65f * s))

    // Handle & Pommel Star Crystal
    drawRect(goldDark, Offset(47f * s, 68f * s), Size(6f * s, 14f * s))
    drawLine(goldBright, Offset(47f * s, 72f * s), Offset(53f * s, 74f * s), strokeWidth = 1.5f * s)
    drawLine(goldBright, Offset(47f * s, 77f * s), Offset(53f * s, 79f * s), strokeWidth = 1.5f * s)

    drawCircle(gold, radius = 6f * s, center = Offset(50f * s, 86f * s))
    drawCircle(cyanGlow, radius = 3.5f * s, center = Offset(50f * s, 86f * s))
    drawCircle(pureWhite, radius = 1.5f * s, center = Offset(50f * s, 86f * s))
}

// ============================================================================
// 2. ARMOR SPRITES (HELMETS: COMMON -> RARE -> EPIC -> LEGENDARY)
// ============================================================================

private fun DrawScope.drawCommonHelmet(s: Float, isPlaceholder: Boolean = false) {
    val steel = if (isPlaceholder) Color(0xFF555566).copy(alpha = 0.4f) else Color(0xFFBDC3C7)
    val steelDark = if (isPlaceholder) Color(0xFF333344).copy(alpha = 0.4f) else Color(0xFF34495E)
    val leather = if (isPlaceholder) Color(0xFF443322).copy(alpha = 0.4f) else Color(0xFF6E2C00)

    // Main Helmet Dome
    val domePath = Path().apply {
        moveTo(28f * s, 62f * s)
        lineTo(28f * s, 42f * s)
        cubicTo(28f * s, 24f * s, 72f * s, 24f * s, 72f * s, 42f * s)
        lineTo(72f * s, 62f * s)
        close()
    }
    drawPath(domePath, steel)
    drawPath(domePath, steelDark, style = Stroke(2f * s))

    // Nose Guard & Eye Shadow Area
    drawRect(Color.Black.copy(alpha = if (isPlaceholder) 0.3f else 0.8f), Offset(32f * s, 48f * s), Size(36f * s, 10f * s))
    drawRect(steelDark, Offset(48f * s, 44f * s), Size(4f * s, 18f * s))

    // Padded Leather Bottom Band
    drawRect(leather, Offset(26f * s, 62f * s), Size(48f * s, 6f * s))

    if (isPlaceholder) {
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4f * s, 4f * s), 0f)
        drawPath(domePath, Color.Gray.copy(alpha = 0.5f), style = Stroke(1.5f * s, pathEffect = dashEffect))
    }
}

private fun DrawScope.drawRareHelmet(s: Float, animTime: Float) {
    val steel = Color(0xFFECF0F1)
    val steelDark = Color(0xFF2C3E50)
    val gold = Color(0xFFF1C40F)
    val sapphire = Color(0xFF29B6F6)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Helmet Dome
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

    // Breathing Slits Visor Grid
    drawRect(steelDark, Offset(30f * s, 46f * s), Size(40f * s, 12f * s))
    drawRect(sapphire.copy(alpha = pulse), Offset(32f * s, 48f * s), Size(36f * s, 3f * s))
    repeat(5) { i ->
        drawRect(steel, Offset((35f + i * 6f) * s, 52f * s), Size(2f * s, 5f * s))
    }

    // Plume Crest on Top (Royal Blue)
    val plumePath = Path().apply {
        moveTo(50f * s, 22f * s)
        quadraticTo(50f * s, 8f * s, 68f * s, 10f * s)
        quadraticTo(58f * s, 20f * s, 50f * s, 22f * s)
    }
    drawPath(plumePath, sapphire)

    // Golden Trim & Cheek Guards
    drawRect(gold, Offset(25f * s, 64f * s), Size(50f * s, 4f * s))
}

private fun DrawScope.drawEpicHelmet(s: Float, animTime: Float) {
    val obsidian = Color(0xFF1C2833)
    val steelDark = Color(0xFF2C3E50)
    val flameRed = Color(0xFFE74C3C)
    val gold = Color(0xFFF39C12)
    val ruby = Color(0xFFC0392B)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Rising Ember Particles
    repeat(6) { i ->
        val sparkPhase = (animTime * 0.004f + i * 1.05f) % 3.14159f
        val sx = 50f * s + sin(sparkPhase * 2f + i) * 22f * s
        val sy = 60f * s - (sparkPhase / 3.14159f) * 45f * s
        drawCircle(flameRed.copy(alpha = sin(sparkPhase)), radius = 1.8f * s, center = Offset(sx, sy))
    }

    // Angular Templar Helmet Dome
    val domePath = Path().apply {
        moveTo(24f * s, 68f * s)
        lineTo(24f * s, 38f * s)
        lineTo(38f * s, 22f * s)
        lineTo(62f * s, 22f * s)
        lineTo(76f * s, 38f * s)
        lineTo(76f * s, 68f * s)
        lineTo(50f * s, 76f * s)
        close()
    }
    drawPath(domePath, obsidian)
    drawPath(domePath, steelDark, style = Stroke(2.5f * s))

    // Glowing Crimson Visor Eye Slit
    drawRect(Color.Black, Offset(28f * s, 44f * s), Size(44f * s, 12f * s))
    drawRect(flameRed.copy(alpha = pulse), Offset(30f * s, 47f * s), Size(40f * s, 6f * s))
    drawRect(Color.White, Offset(42f * s, 49f * s), Size(16f * s, 2f * s))

    // Golden Dragon Scale Cheek Plates
    val cheekLeft = Path().apply {
        moveTo(24f * s, 50f * s)
        lineTo(34f * s, 64f * s)
        lineTo(24f * s, 68f * s)
        close()
    }
    val cheekRight = Path().apply {
        moveTo(76f * s, 50f * s)
        lineTo(66f * s, 64f * s)
        lineTo(76f * s, 68f * s)
        close()
    }
    drawPath(cheekLeft, gold)
    drawPath(cheekRight, gold)

    // Forehead Ruby Gem
    drawCircle(ruby, radius = 4f * s, center = Offset(50f * s, 30f * s))
    drawCircle(Color.White, radius = 1.2f * s, center = Offset(48.5f * s, 28.5f * s))
}

private fun DrawScope.drawLegendaryHelmet(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val goldBright = Color(0xFFFFF2A3)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    val rot = animTime * 0.002f

    // 1. Floating Golden Halo / Crown Ring behind head
    drawOval(
        color = gold,
        topLeft = Offset(center.x - 34f * s, center.y - 32f * s),
        size = Size(68f * s, 24f * s),
        style = Stroke(2.5f * s)
    )

    repeat(4) { i ->
        val angle = rot + i * (3.14159f / 2f)
        val rx = center.x + cos(angle) * 34f * s
        val ry = center.y - 20f * s + sin(angle) * 10f * s
        drawCircle(cyanGlow, radius = 2.5f * s, center = Offset(rx, ry))
    }

    // 2. Curved Golden Dragon Horns
    val hornLeft = Path().apply {
        moveTo(32f * s, 32f * s)
        quadraticTo(14f * s, 20f * s, 10f * s, 6f * s)
        quadraticTo(22f * s, 18f * s, 36f * s, 28f * s)
    }
    val hornRight = Path().apply {
        moveTo(68f * s, 32f * s)
        quadraticTo(86f * s, 20f * s, 90f * s, 6f * s)
        quadraticTo(78f * s, 18f * s, 64f * s, 28f * s)
    }
    drawPath(hornLeft, gold)
    drawPath(hornRight, gold)
    drawPath(hornLeft, goldDark, style = Stroke(1.5f * s))
    drawPath(hornRight, goldDark, style = Stroke(1.5f * s))

    // 3. Majestic Crowned Helmet Dome
    val domePath = Path().apply {
        moveTo(24f * s, 70f * s)
        lineTo(24f * s, 38f * s)
        lineTo(50f * s, 20f * s)
        lineTo(76f * s, 38f * s)
        lineTo(76f * s, 70f * s)
        lineTo(50f * s, 78f * s)
        close()
    }
    drawPath(domePath, gold)
    drawPath(domePath, goldDark, style = Stroke(2f * s))

    // Glowing Cyan Visor Grid
    drawRect(Color(0xFF101820), Offset(28f * s, 44f * s), Size(44f * s, 14f * s))
    drawRect(cyanGlow.copy(alpha = pulse), Offset(30f * s, 47f * s), Size(40f * s, 8f * s))
    drawRect(pureWhite, Offset(40f * s, 49f * s), Size(20f * s, 3f * s))

    // Sunburst Forehead Gem
    drawCircle(cyanGlow, radius = 5f * s, center = Offset(50f * s, 32f * s))
    drawCircle(pureWhite, radius = 2.5f * s, center = Offset(50f * s, 32f * s))

    // Floating Starlight Particles
    repeat(6) { i ->
        val sparkPhase = (animTime * 0.003f + i * 1.05f) % 3.14159f
        val sx = 50f * s + cos(sparkPhase * 2f + i) * 28f * s
        val sy = 50f * s - sin(sparkPhase) * 24f * s
        drawCircle(goldBright, radius = 1.8f * s, center = Offset(sx, sy))
    }
}

// ============================================================================
// 3. SHIELD SPRITES (COMMON -> RARE -> EPIC -> LEGENDARY)
// ============================================================================

private fun DrawScope.drawCommonShield(s: Float, isPlaceholder: Boolean = false) {
    val wood = if (isPlaceholder) Color(0xFF443322).copy(alpha = 0.4f) else Color(0xFF6E2C00)
    val iron = if (isPlaceholder) Color(0xFF555566).copy(alpha = 0.4f) else Color(0xFF34495E)

    // Wooden Round Buckler
    drawCircle(wood, radius = 28f * s, center = Offset(50f * s, 50f * s))
    drawCircle(iron, radius = 28f * s, center = Offset(50f * s, 50f * s), style = Stroke(3f * s))

    // Wooden Plank Divider Lines
    drawLine(iron, Offset(30f * s, 32f * s), Offset(30f * s, 68f * s), strokeWidth = 1.5f * s)
    drawLine(iron, Offset(70f * s, 32f * s), Offset(70f * s, 68f * s), strokeWidth = 1.5f * s)

    // Center Iron Boss Dome
    drawCircle(iron, radius = 8f * s, center = Offset(50f * s, 50f * s))
    drawCircle(Color.Gray, radius = 3f * s, center = Offset(48f * s, 48f * s))

    if (isPlaceholder) {
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4f * s, 4f * s), 0f)
        drawCircle(Color.Gray.copy(alpha = 0.5f), radius = 28f * s, center = Offset(50f * s, 50f * s), style = Stroke(1.5f * s, pathEffect = dashEffect))
    }
}

private fun DrawScope.drawRareShield(s: Float, animTime: Float) {
    val navyBlue = Color(0xFF1B4F72)
    val steel = Color(0xFFECF0F1)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val sapphire = Color(0xFF29B6F6)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Heater Shield Shape
    val shieldPath = Path().apply {
        moveTo(25f * s, 22f * s)
        lineTo(75f * s, 22f * s)
        lineTo(75f * s, 48f * s)
        quadraticTo(70f * s, 76f * s, 50f * s, 86f * s)
        quadraticTo(30f * s, 76f * s, 25f * s, 48f * s)
        close()
    }
    drawPath(shieldPath, navyBlue)
    drawPath(shieldPath, steel, style = Stroke(3.5f * s))
    drawPath(shieldPath, goldDark, style = Stroke(1.5f * s))

    // Golden Cross Heraldic Emblem
    drawRect(gold, Offset(46f * s, 28f * s), Size(8f * s, 48f * s))
    drawRect(gold, Offset(30f * s, 40f * s), Size(40f * s, 8f * s))

    // Center Sapphire Boss Gem
    drawCircle(sapphire.copy(alpha = pulse), radius = 6f * s, center = Offset(50f * s, 44f * s))
    drawCircle(Color.White, radius = 2f * s, center = Offset(48.5f * s, 42.5f * s))
}

private fun DrawScope.drawEpicShield(s: Float, animTime: Float) {
    val obsidian = Color(0xFF1C2833)
    val flameRed = Color(0xFFE74C3C)
    val gold = Color(0xFFF39C12)
    val ruby = Color(0xFFC0392B)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Crimson Pulsing Shield Barrier Aura
    val auraShield = Path().apply {
        moveTo(20f * s, 18f * s)
        lineTo(80f * s, 18f * s)
        lineTo(80f * s, 48f * s)
        quadraticTo(74f * s, 80f * s, 50f * s, 90f * s)
        quadraticTo(26f * s, 80f * s, 20f * s, 48f * s)
        close()
    }
    drawPath(auraShield, flameRed.copy(alpha = 0.3f * pulse))

    // Dragon Aegis Main Plate
    val shieldPath = Path().apply {
        moveTo(24f * s, 20f * s)
        lineTo(76f * s, 20f * s)
        lineTo(76f * s, 48f * s)
        quadraticTo(70f * s, 76f * s, 50f * s, 86f * s)
        quadraticTo(30f * s, 76f * s, 24f * s, 48f * s)
        close()
    }
    drawPath(shieldPath, obsidian)

    // Dragon Scale Plates Overlay
    repeat(3) { i ->
        val sy = (28 + i * 14).toFloat()
        drawArc(gold, startAngle = 0f, sweepAngle = 180f, useCenter = false, topLeft = Offset(36f * s, sy * s), size = Size(28f * s, 10f * s), style = Stroke(1.5f * s))
    }

    // Golden Winged Side Frames
    drawPath(shieldPath, gold, style = Stroke(3.5f * s))

    // Central Ruby Gem Core
    drawCircle(ruby, radius = 7f * s, center = Offset(50f * s, 48f * s))
    drawCircle(flameRed.copy(alpha = pulse), radius = 5f * s, center = Offset(50f * s, 48f * s))
    drawCircle(Color.White, radius = 2f * s, center = Offset(48f * s, 46f * s))
}

private fun DrawScope.drawLegendaryShield(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val goldBright = Color(0xFFFFF2A3)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    val rot = animTime * 0.002f

    // 1. Rotating Golden Rune Ring behind Shield
    drawOval(
        color = gold,
        topLeft = Offset(center.x - 36f * s, center.y - 10f * s),
        size = Size(72f * s, 24f * s),
        style = Stroke(2.5f * s)
    )

    repeat(4) { i ->
        val angle = rot + i * (3.14159f / 2f)
        val rx = center.x + cos(angle) * 36f * s
        val ry = center.y + sin(angle) * 12f * s
        drawCircle(cyanGlow, radius = 2.5f * s, center = Offset(rx, ry))
    }

    // 2. Translucent Cyan Energy Shield Barrier
    val barrierPath = Path().apply {
        moveTo(18f * s, 16f * s)
        lineTo(82f * s, 16f * s)
        lineTo(82f * s, 48f * s)
        quadraticTo(74f * s, 82f * s, 50f * s, 92f * s)
        quadraticTo(26f * s, 82f * s, 18f * s, 48f * s)
        close()
    }
    drawPath(barrierPath, cyanGlow.copy(alpha = 0.35f * pulse))

    // 3. Golden Winged Shield Frame
    val shieldPath = Path().apply {
        moveTo(22f * s, 20f * s)
        lineTo(78f * s, 20f * s)
        lineTo(78f * s, 48f * s)
        quadraticTo(70f * s, 78f * s, 50f * s, 88f * s)
        quadraticTo(30f * s, 78f * s, 22f * s, 48f * s)
        close()
    }
    drawPath(shieldPath, gold)
    drawPath(shieldPath, goldDark, style = Stroke(2.5f * s))

    // Inner Dark Inlay
    val innerShield = Path().apply {
        moveTo(30f * s, 26f * s)
        lineTo(70f * s, 26f * s)
        lineTo(70f * s, 46f * s)
        quadraticTo(64f * s, 70f * s, 50f * s, 78f * s)
        quadraticTo(36f * s, 70f * s, 30f * s, 46f * s)
        close()
    }
    drawPath(innerShield, Color(0xFF101820))

    // Central Diamond Star Core Gem
    drawCircle(cyanGlow, radius = 8f * s, center = center)
    drawCircle(pureWhite, radius = 4f * s, center = center)

    // Orbiting Sparkles
    repeat(6) { i ->
        val sparkPhase = (animTime * 0.003f + i * 1.05f) % 3.14159f
        val sx = center.x + cos(sparkPhase * 2f + i) * 28f * s
        val sy = center.y + sin(sparkPhase * 2f + i) * 28f * s
        drawCircle(goldBright, radius = 2f * s, center = Offset(sx, sy))
    }
}

// ============================================================================
// 4. ACCESSORY SPRITES (COMMON -> RARE -> EPIC -> LEGENDARY)
// ============================================================================

private fun DrawScope.drawCommonAccessory(s: Float, isPlaceholder: Boolean = false) {
    val silver = if (isPlaceholder) Color(0xFF555566).copy(alpha = 0.4f) else Color(0xFFBDC3C7)
    val silverDark = if (isPlaceholder) Color(0xFF333344).copy(alpha = 0.4f) else Color(0xFF7F8C8D)
    val stone = if (isPlaceholder) Color(0xFF444455).copy(alpha = 0.4f) else Color(0xFF3498DB)

    // Silver Band Ring
    drawCircle(silver, radius = 24f * s, center = Offset(50f * s, 54f * s), style = Stroke(6f * s))
    drawCircle(silverDark, radius = 27f * s, center = Offset(50f * s, 54f * s), style = Stroke(1.5f * s))

    // Small Unpolished Stone Setting
    drawCircle(stone, radius = 5f * s, center = Offset(50f * s, 28f * s))
    drawCircle(Color.White, radius = 1.5f * s, center = Offset(48.5f * s, 26.5f * s))

    if (isPlaceholder) {
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4f * s, 4f * s), 0f)
        drawCircle(Color.Gray.copy(alpha = 0.5f), radius = 27f * s, center = Offset(50f * s, 54f * s), style = Stroke(1.5f * s, pathEffect = dashEffect))
    }
}

private fun DrawScope.drawRareAccessory(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val ruby = Color(0xFFE74C3C)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Double Golden Ring Band
    drawCircle(gold, radius = 24f * s, center = Offset(50f * s, 54f * s), style = Stroke(7f * s))
    drawCircle(goldDark, radius = 27.5f * s, center = Offset(50f * s, 54f * s), style = Stroke(1.5f * s))

    // Raised Square Claw Mount
    val mountPath = Path().apply {
        moveTo(40f * s, 32f * s)
        lineTo(60f * s, 32f * s)
        lineTo(56f * s, 24f * s)
        lineTo(44f * s, 24f * s)
        close()
    }
    drawPath(mountPath, gold)

    // Cut Ruby Gem
    val gemPath = Path().apply {
        moveTo(50f * s, 14f * s)
        lineTo(62f * s, 24f * s)
        lineTo(50f * s, 34f * s)
        lineTo(38f * s, 24f * s)
        close()
    }
    drawPath(gemPath, ruby.copy(alpha = pulse))
    drawPath(gemPath, Color.White, style = Stroke(1.5f * s))

    drawCircle(Color.White, radius = 2.5f * s, center = Offset(46f * s, 20f * s))
}

private fun DrawScope.drawEpicAccessory(s: Float, animTime: Float) {
    val gold = Color(0xFFF39C12)
    val goldDark = Color(0xFFB7950B)
    val purpleVoid = Color(0xFF8E44AD)
    val cyanGlow = Color(0xFF00E5FF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Floating Purple Magic Sparkles
    repeat(6) { i ->
        val sparkAngle = (animTime * 0.003f + i * 1.05f) % 6.28318f
        val sx = 50f * s + cos(sparkAngle) * 26f * s
        val sy = 30f * s + sin(sparkAngle) * 20f * s
        drawCircle(purpleVoid.copy(alpha = sin(sparkAngle)), radius = 2f * s, center = Offset(sx, sy))
    }

    // Heavy Gold Ring Band
    drawCircle(gold, radius = 24f * s, center = Offset(50f * s, 54f * s), style = Stroke(8f * s))
    drawCircle(goldDark, radius = 28f * s, center = Offset(50f * s, 54f * s), style = Stroke(2f * s))

    // Dragon Claw Mount
    val clawLeft = Path().apply {
        moveTo(36f * s, 36f * s)
        lineTo(42f * s, 22f * s)
        lineTo(48f * s, 28f * s)
        close()
    }
    val clawRight = Path().apply {
        moveTo(64f * s, 36f * s)
        lineTo(58f * s, 22f * s)
        lineTo(52f * s, 28f * s)
        close()
    }
    drawPath(clawLeft, gold)
    drawPath(clawRight, gold)

    // Arcane Eye Sphere Orb
    drawCircle(purpleVoid, radius = 12f * s, center = Offset(50f * s, 26f * s))
    drawCircle(cyanGlow.copy(alpha = pulse), radius = 8f * s, center = Offset(50f * s, 26f * s))
    drawCircle(Color.White, radius = 3f * s, center = Offset(48f * s, 24f * s))
}

private fun DrawScope.drawLegendaryAccessory(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val goldBright = Color(0xFFFFF2A3)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    val rot = animTime * 0.002f

    // 1. Orbiting Golden Rune Ring
    drawOval(
        color = gold,
        topLeft = Offset(center.x - 36f * s, center.y - 30f * s),
        size = Size(72f * s, 24f * s),
        style = Stroke(2.5f * s)
    )

    repeat(4) { i ->
        val angle = rot + i * (3.14159f / 2f)
        val rx = center.x + cos(angle) * 36f * s
        val ry = center.y - 18f * s + sin(angle) * 12f * s
        drawCircle(cyanGlow, radius = 2.5f * s, center = Offset(rx, ry))
    }

    // 2. Angel/Phoenix Wings Setting
    val wingLeft = Path().apply {
        moveTo(32f * s, 32f * s)
        quadraticTo(14f * s, 20f * s, 8f * s, 8f * s)
        quadraticTo(22f * s, 18f * s, 38f * s, 26f * s)
    }
    val wingRight = Path().apply {
        moveTo(68f * s, 32f * s)
        quadraticTo(86f * s, 20f * s, 92f * s, 8f * s)
        quadraticTo(78f * s, 18f * s, 62f * s, 26f * s)
    }
    drawPath(wingLeft, gold)
    drawPath(wingRight, gold)

    // 3. Main Imperial Ring Band
    drawCircle(gold, radius = 24f * s, center = Offset(50f * s, 56f * s), style = Stroke(8f * s))
    drawCircle(goldDark, radius = 28f * s, center = Offset(50f * s, 56f * s), style = Stroke(2f * s))

    // 4. Starburst Diamond Crystal
    drawCircle(cyanGlow.copy(alpha = 0.4f * pulse), radius = 16f * s, center = Offset(50f * s, 28f * s))

    val starPath = Path().apply {
        moveTo(50f * s, 12f * s)
        lineTo(55f * s, 23f * s)
        lineTo(66f * s, 28f * s)
        lineTo(55f * s, 33f * s)
        lineTo(50f * s, 44f * s)
        lineTo(45f * s, 33f * s)
        lineTo(34f * s, 28f * s)
        lineTo(45f * s, 23f * s)
        close()
    }
    drawPath(starPath, goldBright)
    drawCircle(pureWhite, radius = 4f * s, center = Offset(50f * s, 28f * s))

    // Floating Starlight Embers
    repeat(6) { i ->
        val sparkPhase = (animTime * 0.003f + i * 1.05f) % 3.14159f
        val sx = center.x + cos(sparkPhase * 2f + i) * 28f * s
        val sy = center.y - sin(sparkPhase) * 24f * s
        drawCircle(goldBright, radius = 1.8f * s, center = Offset(sx, sy))
    }
}
