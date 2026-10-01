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
        val isMagicalWeapon = item.slot == ItemSlot.WEAPON && (item.magicBonus > 0 && item.attackBonus == 0)
        val isAxeOrBow = item.slot == ItemSlot.WEAPON && (item.emoji in listOf("🪓", "🏹") || item.name.contains("Axe", ignoreCase = true) || item.name.contains("Bow", ignoreCase = true))
        val isRobe = item.slot == ItemSlot.ARMOR && (item.name.contains("Robe", ignoreCase = true) || item.name.contains("Garb", ignoreCase = true) || item.name.contains("Gown", ignoreCase = true) || item.emoji in listOf("👘", "🔮", "🤍", "👗"))
        val isBarrier = item.slot == ItemSlot.SHIELD && (item.name.contains("Barrier", ignoreCase = true) || item.name.contains("Mirror", ignoreCase = true) || item.emoji in listOf("🌀", "🪞"))
        val isAmulet = item.slot == ItemSlot.ACCESSORY && (item.name.contains("Amulet", ignoreCase = true) || item.name.contains("Pendant", ignoreCase = true) || item.name.contains("Talisman", ignoreCase = true) || item.emoji in listOf("📿", "🧿", "🔴", "🪨"))
        val isCrown = item.slot == ItemSlot.ACCESSORY && (item.name.contains("Crown", ignoreCase = true) || item.name.contains("Crest", ignoreCase = true) || item.emoji in listOf("👑"))

        when (item.slot) {
            ItemSlot.WEAPON -> when {
                isMagicalWeapon -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonMagicalStaff(s)
                    Rarity.RARE -> drawRareMagicalStaff(s, animTime)
                    Rarity.EPIC -> drawEpicMagicalStaff(s, animTime)
                    Rarity.LEGENDARY -> drawLegendaryMagicalStaff(s, animTime)
                }
                isAxeOrBow -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonAxeOrBow(s)
                    Rarity.RARE -> drawRareAxeOrBow(s, animTime)
                    Rarity.EPIC -> drawEpicAxeOrBow(s, animTime)
                    Rarity.LEGENDARY -> drawLegendaryAxeOrBow(s, animTime)
                }
                else -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonSword(s)
                    Rarity.RARE -> drawRareSword(s, animTime)
                    Rarity.EPIC -> drawEpicSword(s, animTime)
                    Rarity.LEGENDARY -> drawLegendarySword(s, animTime)
                }
            }
            ItemSlot.ARMOR -> when {
                isRobe -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonRobe(s)
                    Rarity.RARE -> drawRareRobe(s, animTime)
                    Rarity.EPIC -> drawEpicRobe(s, animTime)
                    Rarity.LEGENDARY -> drawLegendaryRobe(s, animTime)
                }
                else -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonHelmet(s)
                    Rarity.RARE -> drawRareHelmet(s, animTime)
                    Rarity.EPIC -> drawEpicHelmet(s, animTime)
                    Rarity.LEGENDARY -> drawLegendaryHelmet(s, animTime)
                }
            }
            ItemSlot.SHIELD -> when {
                isBarrier -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonBarrier(s)
                    Rarity.RARE -> drawRareBarrier(s, animTime)
                    Rarity.EPIC -> drawEpicBarrier(s, animTime)
                    Rarity.LEGENDARY -> drawLegendaryBarrier(s, animTime)
                }
                else -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonShield(s)
                    Rarity.RARE -> drawRareShield(s, animTime)
                    Rarity.EPIC -> drawEpicShield(s, animTime)
                    Rarity.LEGENDARY -> drawLegendaryShield(s, animTime)
                }
            }
            ItemSlot.ACCESSORY -> when {
                isAmulet -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonAmulet(s)
                    Rarity.RARE -> drawRareAmulet(s, animTime)
                    Rarity.EPIC -> drawEpicAmulet(s, animTime)
                    Rarity.LEGENDARY -> drawLegendaryAmulet(s, animTime)
                }
                isCrown -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonCrown(s)
                    Rarity.RARE -> drawRareCrown(s, animTime)
                    Rarity.EPIC -> drawEpicCrown(s, animTime)
                    Rarity.LEGENDARY -> drawLegendaryCrown(s, animTime)
                }
                else -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonAccessory(s)
                    Rarity.RARE -> drawRareAccessory(s, animTime)
                    Rarity.EPIC -> drawEpicAccessory(s, animTime)
                    Rarity.LEGENDARY -> drawLegendaryAccessory(s, animTime)
                }
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
// 1. WEAPON SPRITES (SWORDS, MAGICAL STAFFS, AXES/BOWS)
// ============================================================================

private fun DrawScope.drawCommonSword(s: Float, isPlaceholder: Boolean = false) {
    val steel = if (isPlaceholder) Color(0xFF555566).copy(alpha = 0.4f) else Color(0xFFBDC3C7)
    val steelDark = if (isPlaceholder) Color(0xFF333344).copy(alpha = 0.4f) else Color(0xFF7F8C8D)
    val wood = if (isPlaceholder) Color(0xFF443322).copy(alpha = 0.4f) else Color(0xFF6E2C00)
    val ironGuard = if (isPlaceholder) Color(0xFF444455).copy(alpha = 0.4f) else Color(0xFF34495E)

    val bladePath = Path().apply {
        moveTo(65f * s, 22f * s)
        lineTo(75f * s, 32f * s)
        lineTo(42f * s, 65f * s)
        lineTo(35f * s, 58f * s)
        close()
    }
    drawPath(bladePath, steel)
    drawPath(bladePath, steelDark, style = Stroke(1.5f * s))

    drawLine(steelDark, Offset(66f * s, 28f * s), Offset(40f * s, 58f * s), strokeWidth = 1.5f * s)

    val guardPath = Path().apply {
        moveTo(30f * s, 52f * s)
        lineTo(48f * s, 70f * s)
        lineTo(44f * s, 74f * s)
        lineTo(26f * s, 56f * s)
        close()
    }
    drawPath(guardPath, ironGuard)

    val hiltPath = Path().apply {
        moveTo(30f * s, 68f * s)
        lineTo(20f * s, 78f * s)
        lineTo(16f * s, 74f * s)
        lineTo(26f * s, 64f * s)
        close()
    }
    drawPath(hiltPath, wood)

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

    val auraPath = Path().apply {
        moveTo(68f * s, 16f * s)
        lineTo(82f * s, 30f * s)
        lineTo(42f * s, 70f * s)
        lineTo(28f * s, 56f * s)
        close()
    }
    drawPath(auraPath, blueAura.copy(alpha = 0.25f * pulse))

    val bladePath = Path().apply {
        moveTo(68f * s, 18f * s)
        lineTo(80f * s, 30f * s)
        lineTo(44f * s, 66f * s)
        lineTo(32f * s, 54f * s)
        close()
    }
    drawPath(bladePath, steel)
    drawPath(bladePath, steelDark, style = Stroke(1.5f * s))

    drawLine(sapphire.copy(alpha = pulse), Offset(69f * s, 25f * s), Offset(39f * s, 59f * s), strokeWidth = 2.5f * s)
    drawLine(Color.White, Offset(69f * s, 25f * s), Offset(39f * s, 59f * s), strokeWidth = 1f * s)

    val guardPath = Path().apply {
        moveTo(28f * s, 50f * s)
        lineTo(50f * s, 72f * s)
        lineTo(44f * s, 78f * s)
        lineTo(22f * s, 56f * s)
        close()
    }
    drawPath(guardPath, gold)
    drawPath(guardPath, goldDark, style = Stroke(1.5f * s))

    drawCircle(sapphire, radius = 3.5f * s, center = Offset(36f * s, 64f * s))
    drawCircle(Color.White, radius = 1.2f * s, center = Offset(35f * s, 63f * s))

    val hiltPath = Path().apply {
        moveTo(28f * s, 70f * s)
        lineTo(18f * s, 80f * s)
        lineTo(14f * s, 76f * s)
        lineTo(24f * s, 66f * s)
        close()
    }
    drawPath(hiltPath, steelDark)
    drawLine(gold, Offset(27f * s, 71f * s), Offset(17f * s, 81f * s), strokeWidth = 1.5f * s)

    drawCircle(gold, radius = 5f * s, center = Offset(16f * s, 78f * s))
    drawCircle(goldDark, radius = 5f * s, center = Offset(16f * s, 78f * s), style = Stroke(1.2f * s))

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

    val fireAura = Path().apply {
        moveTo(50f * s, 10f * s)
        quadraticTo(76f * s, 35f * s, 65f * s, 65f * s)
        lineTo(35f * s, 65f * s)
        quadraticTo(24f * s, 35f * s, 50f * s, 10f * s)
        close()
    }
    drawPath(fireAura, flameRed.copy(alpha = 0.35f * pulse))

    repeat(6) { i ->
        val sparkPhase = (animTime * 0.004f + i * 1.1f) % 3.14159f
        val sx = 50f * s + sin(sparkPhase * 2f + i) * 18f * s
        val sy = 62f * s - (sparkPhase / 3.14159f) * 48f * s
        drawCircle(flameYellow.copy(alpha = sin(sparkPhase)), radius = 1.8f * s, center = Offset(sx, sy))
    }

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

    drawLine(flameYellow.copy(alpha = pulse), Offset(50f * s, 22f * s), Offset(50f * s, 58f * s), strokeWidth = 2.5f * s)
    drawLine(Color.White, Offset(50f * s, 24f * s), Offset(50f * s, 56f * s), strokeWidth = 1f * s)

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

    drawOval(
        color = gold,
        topLeft = Offset(center.x - 32f * s, center.y + 10f * s),
        size = Size(64f * s, 20f * s),
        style = Stroke(2f * s)
    )

    repeat(4) { i ->
        val angle = rot + i * (3.14159f / 2f)
        val rx = center.x + cos(angle) * 32f * s
        val ry = center.y + 20f * s + sin(angle) * 8f * s
        drawCircle(cyanGlow, radius = 2.5f * s, center = Offset(rx, ry))
    }

    val sunAura = Path().apply {
        moveTo(50f * s, 6f * s)
        quadraticTo(78f * s, 32f * s, 62f * s, 62f * s)
        lineTo(38f * s, 62f * s)
        quadraticTo(22f * s, 32f * s, 50f * s, 6f * s)
        close()
    }
    drawPath(sunAura, cyanGlow.copy(alpha = 0.3f * pulse))

    repeat(8) { i ->
        val sparkAngle = (animTime * 0.003f + i * 0.8f) % 6.28318f
        val sx = 50f * s + cos(sparkAngle) * (20f + sin(sparkAngle) * 6f) * s
        val sy = 38f * s + sin(sparkAngle) * (26f + cos(sparkAngle) * 6f) * s
        drawCircle(goldBright, radius = 2f * s, center = Offset(sx, sy))
        drawCircle(pureWhite, radius = 1f * s, center = Offset(sx, sy))
    }

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

    drawLine(pureWhite, Offset(50f * s, 16f * s), Offset(50f * s, 60f * s), strokeWidth = 3f * s)
    drawLine(cyanGlow, Offset(50f * s, 18f * s), Offset(50f * s, 58f * s), strokeWidth = 1.2f * s)

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

    drawCircle(cyanGlow, radius = 5f * s, center = Offset(50f * s, 65f * s))
    drawCircle(pureWhite, radius = 2.5f * s, center = Offset(50f * s, 65f * s))

    drawRect(goldDark, Offset(47f * s, 68f * s), Size(6f * s, 14f * s))
    drawLine(goldBright, Offset(47f * s, 72f * s), Offset(53f * s, 74f * s), strokeWidth = 1.5f * s)
    drawLine(goldBright, Offset(47f * s, 77f * s), Offset(53f * s, 79f * s), strokeWidth = 1.5f * s)

    drawCircle(gold, radius = 6f * s, center = Offset(50f * s, 86f * s))
    drawCircle(cyanGlow, radius = 3.5f * s, center = Offset(50f * s, 86f * s))
    drawCircle(pureWhite, radius = 1.5f * s, center = Offset(50f * s, 86f * s))
}

// ── Magical Staff / Wand Sprites ──────────────────────────────────────────────

private fun DrawScope.drawCommonMagicalStaff(s: Float, isPlaceholder: Boolean = false) {
    val wood = if (isPlaceholder) Color(0xFF443322).copy(alpha = 0.4f) else Color(0xFF8B4513)
    val orb = if (isPlaceholder) Color(0xFF555566).copy(alpha = 0.4f) else Color(0xFF3498DB)

    drawLine(wood, Offset(25f * s, 78f * s), Offset(65f * s, 28f * s), strokeWidth = 5f * s)
    drawCircle(orb, radius = 8f * s, center = Offset(70f * s, 22f * s))
    drawCircle(Color.White, radius = 2.5f * s, center = Offset(68f * s, 20f * s))
}

private fun DrawScope.drawRareMagicalStaff(s: Float, animTime: Float) {
    val mithril = Color(0xFFBDC3C7)
    val sapphire = Color(0xFF29B6F6)
    val cyanGlow = Color(0xFF00E5FF)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    drawCircle(cyanGlow.copy(alpha = 0.3f * pulse), radius = 16f * s, center = Offset(70f * s, 22f * s))

    drawLine(mithril, Offset(22f * s, 82f * s), Offset(65f * s, 28f * s), strokeWidth = 5f * s)

    val prongs = Path().apply {
        moveTo(60f * s, 32f * s)
        lineTo(70f * s, 10f * s)
        lineTo(80f * s, 20f * s)
        close()
    }
    drawPath(prongs, mithril)

    drawCircle(sapphire, radius = 9f * s, center = Offset(70f * s, 22f * s))
    drawCircle(Color.White, radius = 3f * s, center = Offset(68f * s, 20f * s))
}

private fun DrawScope.drawEpicMagicalStaff(s: Float, animTime: Float) {
    val obsidian = Color(0xFF2C3E50)
    val flameRed = Color(0xFFE74C3C)
    val gold = Color(0xFFF39C12)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    drawLine(obsidian, Offset(20f * s, 85f * s), Offset(65f * s, 28f * s), strokeWidth = 6f * s)

    val crest = Path().apply {
        moveTo(58f * s, 34f * s)
        lineTo(50f * s, 12f * s)
        lineTo(70f * s, 20f * s)
        lineTo(88f * s, 10f * s)
        lineTo(80f * s, 32f * s)
        close()
    }
    drawPath(crest, gold)

    drawCircle(flameRed.copy(alpha = 0.4f * pulse), radius = 14f * s, center = Offset(70f * s, 22f * s))
    drawCircle(flameRed, radius = 9f * s, center = Offset(70f * s, 22f * s))
    drawCircle(Color.White, radius = 3f * s, center = Offset(68f * s, 20f * s))
}

private fun DrawScope.drawLegendaryMagicalStaff(s: Float, animTime: Float) {
    val center = Offset(70f * s, 22f * s)
    val gold = Color(0xFFF1C40F)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    val rot = animTime * 0.002f

    drawOval(
        color = gold,
        topLeft = Offset(center.x - 22f * s, center.y - 10f * s),
        size = Size(44f * s, 20f * s),
        style = Stroke(2f * s)
    )

    repeat(4) { i ->
        val angle = rot + i * (3.14159f / 2f)
        val rx = center.x + cos(angle) * 22f * s
        val ry = center.y + sin(angle) * 10f * s
        drawCircle(cyanGlow, radius = 2.5f * s, center = Offset(rx, ry))
    }

    drawLine(gold, Offset(18f * s, 88f * s), Offset(65f * s, 28f * s), strokeWidth = 6f * s)

    drawCircle(cyanGlow.copy(alpha = 0.45f * pulse), radius = 16f * s, center = center)
    drawCircle(cyanGlow, radius = 10f * s, center = center)
    drawCircle(pureWhite, radius = 5f * s, center = center)
}

// ── Axe / Bow Sprites ────────────────────────────────────────────────────────

private fun DrawScope.drawCommonAxeOrBow(s: Float) {
    val wood = Color(0xFF8B4513)
    val iron = Color(0xFF7F8C8D)

    val bowPath = Path().apply {
        moveTo(30f * s, 20f * s)
        quadraticTo(70f * s, 50f * s, 30f * s, 80f * s)
    }
    drawPath(bowPath, wood, style = Stroke(4f * s))
    drawLine(iron, Offset(30f * s, 20f * s), Offset(30f * s, 80f * s), strokeWidth = 1.5f * s)
}

private fun DrawScope.drawRareAxeOrBow(s: Float, animTime: Float) {
    val mithril = Color(0xFFBDC3C7)
    val sapphire = Color(0xFF29B6F6)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val bowPath = Path().apply {
        moveTo(28f * s, 18f * s)
        quadraticTo(74f * s, 50f * s, 28f * s, 82f * s)
    }
    drawPath(bowPath, mithril, style = Stroke(5f * s))
    drawLine(sapphire.copy(alpha = pulse), Offset(28f * s, 18f * s), Offset(28f * s, 82f * s), strokeWidth = 2.5f * s)
}

private fun DrawScope.drawEpicAxeOrBow(s: Float, animTime: Float) {
    val gold = Color(0xFFF39C12)
    val flameRed = Color(0xFFE74C3C)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val axePath = Path().apply {
        moveTo(20f * s, 20f * s)
        lineTo(80f * s, 20f * s)
        quadraticTo(60f * s, 50f * s, 80f * s, 80f * s)
        lineTo(20f * s, 80f * s)
        close()
    }
    drawPath(axePath, gold)
    drawCircle(flameRed.copy(alpha = pulse), radius = 6f * s, center = Offset(50f * s, 50f * s))
}

private fun DrawScope.drawLegendaryAxeOrBow(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    drawCircle(cyanGlow.copy(alpha = 0.35f * pulse), radius = 30f * s, center = Offset(50f * s, 50f * s))
    drawCircle(gold, radius = 20f * s, center = Offset(50f * s, 50f * s), style = Stroke(4f * s))
    drawCircle(pureWhite, radius = 8f * s, center = Offset(50f * s, 50f * s))
}

// ============================================================================
// 2. ARMOR SPRITES (HELMETS & ROBES)
// ============================================================================

private fun DrawScope.drawCommonHelmet(s: Float, isPlaceholder: Boolean = false) {
    val steel = if (isPlaceholder) Color(0xFF555566).copy(alpha = 0.4f) else Color(0xFFBDC3C7)
    val steelDark = if (isPlaceholder) Color(0xFF333344).copy(alpha = 0.4f) else Color(0xFF34495E)
    val leather = if (isPlaceholder) Color(0xFF443322).copy(alpha = 0.4f) else Color(0xFF6E2C00)

    val domePath = Path().apply {
        moveTo(28f * s, 62f * s)
        lineTo(28f * s, 42f * s)
        cubicTo(28f * s, 24f * s, 72f * s, 24f * s, 72f * s, 42f * s)
        lineTo(72f * s, 62f * s)
        close()
    }
    drawPath(domePath, steel)
    drawPath(domePath, steelDark, style = Stroke(2f * s))

    drawRect(Color.Black.copy(alpha = if (isPlaceholder) 0.3f else 0.8f), Offset(32f * s, 48f * s), Size(36f * s, 10f * s))
    drawRect(steelDark, Offset(48f * s, 44f * s), Size(4f * s, 18f * s))
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

    drawRect(steelDark, Offset(30f * s, 46f * s), Size(40f * s, 12f * s))
    drawRect(sapphire.copy(alpha = pulse), Offset(32f * s, 48f * s), Size(36f * s, 3f * s))
    repeat(5) { i ->
        drawRect(steel, Offset((35f + i * 6f) * s, 52f * s), Size(2f * s, 5f * s))
    }

    val plumePath = Path().apply {
        moveTo(50f * s, 22f * s)
        quadraticTo(50f * s, 8f * s, 68f * s, 10f * s)
        quadraticTo(58f * s, 20f * s, 50f * s, 22f * s)
    }
    drawPath(plumePath, sapphire)
    drawRect(gold, Offset(25f * s, 64f * s), Size(50f * s, 4f * s))
}

private fun DrawScope.drawEpicHelmet(s: Float, animTime: Float) {
    val obsidian = Color(0xFF1C2833)
    val steelDark = Color(0xFF2C3E50)
    val flameRed = Color(0xFFE74C3C)
    val gold = Color(0xFFF39C12)
    val ruby = Color(0xFFC0392B)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    repeat(6) { i ->
        val sparkPhase = (animTime * 0.004f + i * 1.05f) % 3.14159f
        val sx = 50f * s + sin(sparkPhase * 2f + i) * 22f * s
        val sy = 60f * s - (sparkPhase / 3.14159f) * 45f * s
        drawCircle(flameRed.copy(alpha = sin(sparkPhase)), radius = 1.8f * s, center = Offset(sx, sy))
    }

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

    drawRect(Color.Black, Offset(28f * s, 44f * s), Size(44f * s, 12f * s))
    drawRect(flameRed.copy(alpha = pulse), Offset(30f * s, 47f * s), Size(40f * s, 6f * s))
    drawRect(Color.White, Offset(42f * s, 49f * s), Size(16f * s, 2f * s))

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

    drawRect(Color(0xFF101820), Offset(28f * s, 44f * s), Size(44f * s, 14f * s))
    drawRect(cyanGlow.copy(alpha = pulse), Offset(30f * s, 47f * s), Size(40f * s, 8f * s))
    drawRect(pureWhite, Offset(40f * s, 49f * s), Size(20f * s, 3f * s))

    drawCircle(cyanGlow, radius = 5f * s, center = Offset(50f * s, 32f * s))
    drawCircle(pureWhite, radius = 2.5f * s, center = Offset(50f * s, 32f * s))

    repeat(6) { i ->
        val sparkPhase = (animTime * 0.003f + i * 1.05f) % 3.14159f
        val sx = 50f * s + cos(sparkPhase * 2f + i) * 28f * s
        val sy = 50f * s - sin(sparkPhase) * 24f * s
        drawCircle(goldBright, radius = 1.8f * s, center = Offset(sx, sy))
    }
}

// ── Robe / Garb Sprites ──────────────────────────────────────────────────────

private fun DrawScope.drawCommonRobe(s: Float) {
    val cloth = Color(0xFF2980B9)
    val belt = Color(0xFF8B4513)

    val robePath = Path().apply {
        moveTo(35f * s, 20f * s)
        lineTo(65f * s, 20f * s)
        lineTo(75f * s, 80f * s)
        lineTo(25f * s, 80f * s)
        close()
    }
    drawPath(robePath, cloth)
    drawRect(belt, Offset(28f * s, 50f * s), Size(44f * s, 6f * s))
}

private fun DrawScope.drawRareRobe(s: Float, animTime: Float) {
    val silk = Color(0xFF8E44AD)
    val gold = Color(0xFFF1C40F)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val robePath = Path().apply {
        moveTo(32f * s, 18f * s)
        lineTo(68f * s, 18f * s)
        lineTo(78f * s, 82f * s)
        lineTo(22f * s, 82f * s)
        close()
    }
    drawPath(robePath, silk)
    drawPath(robePath, gold.copy(alpha = pulse), style = Stroke(3f * s))
}

private fun DrawScope.drawEpicRobe(s: Float, animTime: Float) {
    val crimson = Color(0xFFC0392B)
    val obsidian = Color(0xFF1C2833)
    val ruby = Color(0xFFE74C3C)

    val robePath = Path().apply {
        moveTo(30f * s, 16f * s)
        lineTo(70f * s, 16f * s)
        lineTo(82f * s, 85f * s)
        lineTo(18f * s, 85f * s)
        close()
    }
    drawPath(robePath, obsidian)
    drawRect(crimson, Offset(35f * s, 20f * s), Size(30f * s, 60f * s))
    drawCircle(ruby, radius = 6f * s, center = Offset(50f * s, 35f * s))
}

private fun DrawScope.drawLegendaryRobe(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    val robePath = Path().apply {
        moveTo(28f * s, 14f * s)
        lineTo(72f * s, 14f * s)
        lineTo(85f * s, 88f * s)
        lineTo(15f * s, 88f * s)
        close()
    }
    drawPath(robePath, gold)
    drawPath(robePath, cyanGlow, style = Stroke(3f * s))
    drawCircle(pureWhite, radius = 8f * s, center = Offset(50f * s, 32f * s))
}

// ============================================================================
// 3. SHIELD SPRITES (HEAVY SHIELDS & MAGICAL BARRIERS)
// ============================================================================

private fun DrawScope.drawCommonShield(s: Float, isPlaceholder: Boolean = false) {
    val wood = if (isPlaceholder) Color(0xFF443322).copy(alpha = 0.4f) else Color(0xFF6E2C00)
    val iron = if (isPlaceholder) Color(0xFF555566).copy(alpha = 0.4f) else Color(0xFF34495E)

    drawCircle(wood, radius = 28f * s, center = Offset(50f * s, 50f * s))
    drawCircle(iron, radius = 28f * s, center = Offset(50f * s, 50f * s), style = Stroke(3f * s))

    drawLine(iron, Offset(30f * s, 32f * s), Offset(30f * s, 68f * s), strokeWidth = 1.5f * s)
    drawLine(iron, Offset(70f * s, 32f * s), Offset(70f * s, 68f * s), strokeWidth = 1.5f * s)

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

    drawRect(gold, Offset(46f * s, 28f * s), Size(8f * s, 48f * s))
    drawRect(gold, Offset(30f * s, 40f * s), Size(40f * s, 8f * s))

    drawCircle(sapphire.copy(alpha = pulse), radius = 6f * s, center = Offset(50f * s, 44f * s))
    drawCircle(Color.White, radius = 2f * s, center = Offset(48.5f * s, 42.5f * s))
}

private fun DrawScope.drawEpicShield(s: Float, animTime: Float) {
    val obsidian = Color(0xFF1C2833)
    val flameRed = Color(0xFFE74C3C)
    val gold = Color(0xFFF39C12)
    val ruby = Color(0xFFC0392B)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val auraShield = Path().apply {
        moveTo(20f * s, 18f * s)
        lineTo(80f * s, 18f * s)
        lineTo(80f * s, 48f * s)
        quadraticTo(74f * s, 80f * s, 50f * s, 90f * s)
        quadraticTo(26f * s, 80f * s, 20f * s, 48f * s)
        close()
    }
    drawPath(auraShield, flameRed.copy(alpha = 0.3f * pulse))

    val shieldPath = Path().apply {
        moveTo(24f * s, 20f * s)
        lineTo(76f * s, 20f * s)
        lineTo(76f * s, 48f * s)
        quadraticTo(70f * s, 76f * s, 50f * s, 86f * s)
        quadraticTo(30f * s, 76f * s, 24f * s, 48f * s)
        close()
    }
    drawPath(shieldPath, obsidian)

    repeat(3) { i ->
        val sy = (28 + i * 14).toFloat()
        drawArc(gold, startAngle = 0f, sweepAngle = 180f, useCenter = false, topLeft = Offset(36f * s, sy * s), size = Size(28f * s, 10f * s), style = Stroke(1.5f * s))
    }

    drawPath(shieldPath, gold, style = Stroke(3.5f * s))

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

    val barrierPath = Path().apply {
        moveTo(18f * s, 16f * s)
        lineTo(82f * s, 16f * s)
        lineTo(82f * s, 48f * s)
        quadraticTo(74f * s, 82f * s, 50f * s, 92f * s)
        quadraticTo(26f * s, 82f * s, 18f * s, 48f * s)
        close()
    }
    drawPath(barrierPath, cyanGlow.copy(alpha = 0.35f * pulse))

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

    val innerShield = Path().apply {
        moveTo(30f * s, 26f * s)
        lineTo(70f * s, 26f * s)
        lineTo(70f * s, 46f * s)
        quadraticTo(64f * s, 70f * s, 50f * s, 78f * s)
        quadraticTo(36f * s, 70f * s, 30f * s, 46f * s)
        close()
    }
    drawPath(innerShield, Color(0xFF101820))

    drawCircle(cyanGlow, radius = 8f * s, center = center)
    drawCircle(pureWhite, radius = 4f * s, center = center)

    repeat(6) { i ->
        val sparkPhase = (animTime * 0.003f + i * 1.05f) % 3.14159f
        val sx = center.x + cos(sparkPhase * 2f + i) * 28f * s
        val sy = center.y + sin(sparkPhase * 2f + i) * 28f * s
        drawCircle(goldBright, radius = 2f * s, center = Offset(sx, sy))
    }
}

// ── Barrier Shields ──────────────────────────────────────────────────────────

private fun DrawScope.drawCommonBarrier(s: Float) {
    val cyan = Color(0xFF00E5FF)
    drawCircle(cyan.copy(alpha = 0.3f), radius = 30f * s, center = Offset(50f * s, 50f * s))
    drawCircle(cyan, radius = 30f * s, center = Offset(50f * s, 50f * s), style = Stroke(2f * s))
}

private fun DrawScope.drawRareBarrier(s: Float, animTime: Float) {
    val cyanGlow = Color(0xFF00E5FF)
    val sapphire = Color(0xFF29B6F6)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    drawCircle(cyanGlow.copy(alpha = 0.4f * pulse), radius = 32f * s, center = Offset(50f * s, 50f * s))
    drawCircle(sapphire, radius = 20f * s, center = Offset(50f * s, 50f * s))
}

private fun DrawScope.drawEpicBarrier(s: Float, animTime: Float) {
    val flameRed = Color(0xFFE74C3C)
    val gold = Color(0xFFF39C12)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    drawCircle(flameRed.copy(alpha = 0.5f * pulse), radius = 34f * s, center = Offset(50f * s, 50f * s))
    drawCircle(gold, radius = 22f * s, center = Offset(50f * s, 50f * s), style = Stroke(4f * s))
}

private fun DrawScope.drawLegendaryBarrier(s: Float, animTime: Float) {
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    drawCircle(cyanGlow.copy(alpha = 0.6f), radius = 36f * s, center = Offset(50f * s, 50f * s))
    drawCircle(pureWhite, radius = 12f * s, center = Offset(50f * s, 50f * s))
}

// ============================================================================
// 4. ACCESSORY SPRITES (RINGS, AMULETS & CROWNS)
// ============================================================================

private fun DrawScope.drawCommonAccessory(s: Float, isPlaceholder: Boolean = false) {
    val silver = if (isPlaceholder) Color(0xFF555566).copy(alpha = 0.4f) else Color(0xFFBDC3C7)
    val silverDark = if (isPlaceholder) Color(0xFF333344).copy(alpha = 0.4f) else Color(0xFF7F8C8D)
    val stone = if (isPlaceholder) Color(0xFF444455).copy(alpha = 0.4f) else Color(0xFF3498DB)

    drawCircle(silver, radius = 24f * s, center = Offset(50f * s, 54f * s), style = Stroke(6f * s))
    drawCircle(silverDark, radius = 27f * s, center = Offset(50f * s, 54f * s), style = Stroke(1.5f * s))

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

    drawCircle(gold, radius = 24f * s, center = Offset(50f * s, 54f * s), style = Stroke(7f * s))
    drawCircle(goldDark, radius = 27.5f * s, center = Offset(50f * s, 54f * s), style = Stroke(1.5f * s))

    val mountPath = Path().apply {
        moveTo(40f * s, 32f * s)
        lineTo(60f * s, 32f * s)
        lineTo(56f * s, 24f * s)
        lineTo(44f * s, 24f * s)
        close()
    }
    drawPath(mountPath, gold)

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

    repeat(6) { i ->
        val sparkAngle = (animTime * 0.003f + i * 1.05f) % 6.28318f
        val sx = 50f * s + cos(sparkAngle) * 26f * s
        val sy = 30f * s + sin(sparkAngle) * 20f * s
        drawCircle(purpleVoid.copy(alpha = sin(sparkAngle)), radius = 2f * s, center = Offset(sx, sy))
    }

    drawCircle(gold, radius = 24f * s, center = Offset(50f * s, 54f * s), style = Stroke(8f * s))
    drawCircle(goldDark, radius = 28f * s, center = Offset(50f * s, 54f * s), style = Stroke(2f * s))

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

    drawCircle(gold, radius = 24f * s, center = Offset(50f * s, 56f * s), style = Stroke(8f * s))
    drawCircle(goldDark, radius = 28f * s, center = Offset(50f * s, 56f * s), style = Stroke(2f * s))

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

    repeat(6) { i ->
        val sparkPhase = (animTime * 0.003f + i * 1.05f) % 3.14159f
        val sx = center.x + cos(sparkPhase * 2f + i) * 28f * s
        val sy = center.y - sin(sparkPhase) * 24f * s
        drawCircle(goldBright, radius = 1.8f * s, center = Offset(sx, sy))
    }
}

// ── Amulet / Pendant Sprites ──────────────────────────────────────────────────

private fun DrawScope.drawCommonAmulet(s: Float) {
    val silver = Color(0xFFBDC3C7)
    val copper = Color(0xFFD35400)

    val chainPath = Path().apply {
        moveTo(25f * s, 20f * s)
        quadraticTo(50f * s, 70f * s, 75f * s, 20f * s)
    }
    drawPath(chainPath, silver, style = Stroke(2f * s))
    drawCircle(copper, radius = 10f * s, center = Offset(50f * s, 50f * s))
}

private fun DrawScope.drawRareAmulet(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val sapphire = Color(0xFF29B6F6)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val chainPath = Path().apply {
        moveTo(25f * s, 18f * s)
        quadraticTo(50f * s, 68f * s, 75f * s, 18f * s)
    }
    drawPath(chainPath, gold, style = Stroke(2.5f * s))
    drawCircle(sapphire.copy(alpha = pulse), radius = 12f * s, center = Offset(50f * s, 50f * s))
    drawCircle(Color.White, radius = 3f * s, center = Offset(48f * s, 48f * s))
}

private fun DrawScope.drawEpicAmulet(s: Float, animTime: Float) {
    val gold = Color(0xFFF39C12)
    val ruby = Color(0xFFE74C3C)

    val chainPath = Path().apply {
        moveTo(20f * s, 16f * s)
        quadraticTo(50f * s, 68f * s, 80f * s, 16f * s)
    }
    drawPath(chainPath, gold, style = Stroke(3f * s))
    drawCircle(ruby, radius = 14f * s, center = Offset(50f * s, 50f * s))
    drawCircle(Color.White, radius = 4f * s, center = Offset(48f * s, 48f * s))
}

private fun DrawScope.drawLegendaryAmulet(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    val chainPath = Path().apply {
        moveTo(18f * s, 14f * s)
        quadraticTo(50f * s, 68f * s, 82f * s, 14f * s)
    }
    drawPath(chainPath, gold, style = Stroke(3.5f * s))
    drawCircle(cyanGlow, radius = 16f * s, center = Offset(50f * s, 50f * s))
    drawCircle(pureWhite, radius = 6f * s, center = Offset(50f * s, 50f * s))
}

// ── Crown Sprites ────────────────────────────────────────────────────────────

private fun DrawScope.drawCommonCrown(s: Float) {
    val copper = Color(0xFFD35400)
    val crownPath = Path().apply {
        moveTo(20f * s, 70f * s)
        lineTo(20f * s, 35f * s)
        lineTo(35f * s, 55f * s)
        lineTo(50f * s, 30f * s)
        lineTo(65f * s, 55f * s)
        lineTo(80f * s, 35f * s)
        lineTo(80f * s, 70f * s)
        close()
    }
    drawPath(crownPath, copper)
}

private fun DrawScope.drawRareCrown(s: Float, animTime: Float) {
    val silver = Color(0xFFECF0F1)
    val sapphire = Color(0xFF29B6F6)

    val crownPath = Path().apply {
        moveTo(20f * s, 70f * s)
        lineTo(20f * s, 30f * s)
        lineTo(35f * s, 50f * s)
        lineTo(50f * s, 25f * s)
        lineTo(65f * s, 50f * s)
        lineTo(80f * s, 30f * s)
        lineTo(80f * s, 70f * s)
        close()
    }
    drawPath(crownPath, silver)
    drawCircle(sapphire, radius = 4f * s, center = Offset(50f * s, 45f * s))
}

private fun DrawScope.drawEpicCrown(s: Float, animTime: Float) {
    val gold = Color(0xFFF39C12)
    val ruby = Color(0xFFE74C3C)

    val crownPath = Path().apply {
        moveTo(18f * s, 70f * s)
        lineTo(18f * s, 25f * s)
        lineTo(34f * s, 48f * s)
        lineTo(50f * s, 20f * s)
        lineTo(66f * s, 48f * s)
        lineTo(82f * s, 25f * s)
        lineTo(82f * s, 70f * s)
        close()
    }
    drawPath(crownPath, gold)
    drawCircle(ruby, radius = 6f * s, center = Offset(50f * s, 45f * s))
}

private fun DrawScope.drawLegendaryCrown(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    val crownPath = Path().apply {
        moveTo(15f * s, 72f * s)
        lineTo(15f * s, 20f * s)
        lineTo(32f * s, 46f * s)
        lineTo(50f * s, 15f * s)
        lineTo(68f * s, 46f * s)
        lineTo(85f * s, 20f * s)
        lineTo(85f * s, 72f * s)
        close()
    }
    drawPath(crownPath, gold)
    drawCircle(cyanGlow, radius = 8f * s, center = Offset(50f * s, 45f * s))
    drawCircle(pureWhite, radius = 3f * s, center = Offset(50f * s, 45f * s))
}
