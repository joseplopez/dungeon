package com.game.dungeon.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.game.dungeon.data.models.RelicType
import kotlin.math.cos
import kotlin.math.sin

/**
 * Procedural Pixel Art Drawing for Legendary Relics
 */
fun DrawScope.drawRelicSprite(relicType: RelicType, animTime: Float = 0f) {
    val W = size.width
    val s = W / 100f

    when (relicType) {
        RelicType.MAGIC -> drawRunicAmulet(s, animTime)
        RelicType.CRIT_DAMAGE -> drawChronoWatch(s, animTime)
        RelicType.ATTACK -> drawOrbOfPower(s, animTime)
        RelicType.HP -> drawVitalitySeed(s, animTime)
        RelicType.DEFENSE -> drawRunicAegis(s, animTime)
        RelicType.CRIT_CHANCE -> drawBerserkerRing(s, animTime)
        RelicType.GOLD -> drawAvariceCoin(s, animTime)
        RelicType.MAGICITE_FIND -> drawAstralPrism(s, animTime)
        RelicType.MAGNET -> drawMagneticCompass(s, animTime)
        RelicType.POCKETS -> drawDimensionalPouch(s, animTime)
        RelicType.DOUBLE_LOOT -> drawDoubleChest(s, animTime)
        RelicType.MP -> drawSoulAthanor(s, animTime)
    }
}

private fun DrawScope.drawRunicAmulet(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val gemBlue = Color(0xFF3498DB)
    val gemLight = Color(0xFF5DADE2)

    // Golden Chain
    val chainPath = Path().apply {
        moveTo(20f * s, 10f * s)
        quadraticTo(50f * s, 45f * s, 80f * s, 10f * s)
    }
    drawPath(chainPath, gold, style = Stroke(2.5f * s))

    // Chain links dots
    repeat(9) { i ->
        val progress = i / 8f
        val cx = 20f + progress * 60f
        val cy = 10f + (1f - (2f * progress - 1f) * (2f * progress - 1f)) * 25f
        drawCircle(goldDark, radius = 1.2f * s, center = Offset(cx * s, cy * s))
    }

    // Medallion connector
    drawCircle(gold, radius = 4f * s, center = Offset(50f * s, 34f * s))

    // Clover Cross Gem Pendant
    val gemPulse = 0.85f + sin(animTime * 0.004f) * 0.15f
    val gemCenter = Offset(50f * s, 58f * s)
    val petRadius = 12f * s

    // 4 Clover lobes
    drawCircle(gemBlue.copy(alpha = gemPulse), radius = petRadius, center = Offset(gemCenter.x - 8f * s, gemCenter.y))
    drawCircle(gemBlue.copy(alpha = gemPulse), radius = petRadius, center = Offset(gemCenter.x + 8f * s, gemCenter.y))
    drawCircle(gemBlue.copy(alpha = gemPulse), radius = petRadius, center = Offset(gemCenter.x, gemCenter.y - 8f * s))
    drawCircle(gemBlue.copy(alpha = gemPulse), radius = petRadius, center = Offset(gemCenter.x, gemCenter.y + 8f * s))

    // Gold borders around lobes
    drawCircle(gold, radius = petRadius, center = Offset(gemCenter.x - 8f * s, gemCenter.y), style = Stroke(1.5f * s))
    drawCircle(gold, radius = petRadius, center = Offset(gemCenter.x + 8f * s, gemCenter.y), style = Stroke(1.5f * s))
    drawCircle(gold, radius = petRadius, center = Offset(gemCenter.x, gemCenter.y - 8f * s), style = Stroke(1.5f * s))
    drawCircle(gold, radius = petRadius, center = Offset(gemCenter.x, gemCenter.y + 8f * s), style = Stroke(1.5f * s))

    // Inner Specular Glow
    drawCircle(gemLight, radius = 5f * s, center = Offset(gemCenter.x - 3f * s, gemCenter.y - 3f * s))
    drawCircle(Color.White, radius = 2f * s, center = Offset(gemCenter.x - 4f * s, gemCenter.y - 4f * s))
}

private fun DrawScope.drawChronoWatch(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val face = Color(0xFFF9E79F)

    // Top loop ring & knob
    drawCircle(gold, radius = 7f * s, center = Offset(50f * s, 14f * s), style = Stroke(2f * s))
    drawRect(goldDark, Offset(47f * s, 20f * s), Size(6f * s, 6f * s))

    // Watch Body
    val center = Offset(50f * s, 55f * s)
    drawCircle(goldDark, radius = 32f * s, center = center)
    drawCircle(gold, radius = 30f * s, center = center)
    drawCircle(face, radius = 24f * s, center = center)

    // Dial tick marks
    repeat(12) { i ->
        val angle = i * (3.14159f / 6f)
        val tx = center.x + cos(angle) * 20f * s
        val ty = center.y + sin(angle) * 20f * s
        drawCircle(goldDark, radius = 1f * s, center = Offset(tx, ty))
    }

    // Animated Watch Hands
    val handAngle = animTime * 0.003f
    val hx = center.x + cos(handAngle) * 14f * s
    val hy = center.y + sin(handAngle) * 14f * s
    val mx = center.x + cos(handAngle * 2.5f) * 18f * s
    val my = center.y + sin(handAngle * 2.5f) * 18f * s

    drawLine(Color(0xFF34495E), center, Offset(hx, hy), strokeWidth = 2.5f * s)
    drawLine(Color(0xFFC0392B), center, Offset(mx, my), strokeWidth = 1.5f * s)
    drawCircle(goldDark, radius = 2.5f * s, center = center)
}

private fun DrawScope.drawOrbOfPower(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val gold = Color(0xFFF1C40F)
    val blueDark = Color(0xFF1B4F72)
    val blueLight = Color(0xFF5DADE2)

    // Outer Ring
    val rot = animTime * 0.002f
    drawCircle(gold, radius = 34f * s, center = center, style = Stroke(3f * s))

    // Rotating Ring Accents
    repeat(4) { i ->
        val angle = rot + i * (3.14159f / 2f)
        val rx = center.x + cos(angle) * 34f * s
        val ry = center.y + sin(angle) * 34f * s
        drawCircle(Color.White, radius = 2.5f * s, center = Offset(rx, ry))
    }

    // Inner Glowing Crystal Orb
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    drawCircle(blueDark.copy(alpha = pulse), radius = 22f * s, center = center)
    drawCircle(blueLight.copy(alpha = pulse), radius = 15f * s, center = Offset(center.x - 4f * s, center.y - 4f * s))
    drawCircle(Color.White, radius = 4f * s, center = Offset(center.x - 7f * s, center.y - 7f * s))
}

private fun DrawScope.drawVitalitySeed(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val greenDark = Color(0xFF1E8449)
    val greenBright = Color(0xFF2ECC71)
    val gold = Color(0xFFF1C40F)

    val leafPath = Path().apply {
        moveTo(center.x, center.y - 30f * s)
        quadraticTo(center.x + 25f * s, center.y - 10f * s, center.x + 20f * s, center.y + 25f * s)
        quadraticTo(center.x, center.y + 35f * s, center.x, center.y + 35f * s)
        quadraticTo(center.x, center.y + 35f * s, center.x - 20f * s, center.y + 25f * s)
        quadraticTo(center.x - 25f * s, center.y - 10f * s, center.x, center.y - 30f * s)
        close()
    }

    val pulse = 0.85f + sin(animTime * 0.004f) * 0.15f
    drawPath(leafPath, greenBright.copy(alpha = pulse))
    drawPath(leafPath, greenDark, style = Stroke(2.5f * s))

    // Leaf veins
    drawLine(greenDark, Offset(center.x, center.y - 20f * s), Offset(center.x, center.y + 25f * s), strokeWidth = 2f * s)
    drawLine(greenDark, Offset(center.x, center.y - 5f * s), Offset(center.x + 12f * s, center.y - 12f * s), strokeWidth = 1.5f * s)
    drawLine(greenDark, Offset(center.x, center.y + 5f * s), Offset(center.x - 12f * s, center.y - 2f * s), strokeWidth = 1.5f * s)

    // Vine Ring
    drawCircle(gold, radius = 8f * s, center = Offset(center.x, center.y + 28f * s), style = Stroke(2f * s))
}

private fun DrawScope.drawRunicAegis(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val iron = Color(0xFF34495E)
    val runeBlue = Color(0xFF3498DB)

    val shieldPath = Path().apply {
        moveTo(center.x - 25f * s, center.y - 30f * s)
        lineTo(center.x + 25f * s, center.y - 30f * s)
        lineTo(center.x + 25f * s, center.y + 5f * s)
        quadraticTo(center.x + 20f * s, center.y + 30f * s, center.x, center.y + 38f * s)
        quadraticTo(center.x - 20f * s, center.y + 30f * s, center.x - 25f * s, center.y + 5f * s)
        close()
    }

    drawPath(shieldPath, gold)
    drawPath(shieldPath, goldDark, style = Stroke(3f * s))

    // Inner iron inlay
    val innerShield = Path().apply {
        moveTo(center.x - 18f * s, center.y - 24f * s)
        lineTo(center.x + 18f * s, center.y - 24f * s)
        lineTo(center.x + 18f * s, center.y + 2f * s)
        quadraticTo(center.x + 15f * s, center.y + 22f * s, center.x, center.y + 28f * s)
        quadraticTo(center.x - 15f * s, center.y + 22f * s, center.x - 18f * s, center.y + 2f * s)
        close()
    }
    drawPath(innerShield, iron)

    // Blue Cross Rune
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    drawLine(runeBlue.copy(alpha = pulse), Offset(center.x, center.y - 18f * s), Offset(center.x, center.y + 20f * s), strokeWidth = 4f * s)
    drawLine(runeBlue.copy(alpha = pulse), Offset(center.x - 12f * s, center.y - 2f * s), Offset(center.x + 12f * s, center.y - 2f * s), strokeWidth = 4f * s)
}

private fun DrawScope.drawBerserkerRing(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val gold = Color(0xFFF1C40F)
    val ruby = Color(0xFFE74C3C)
    val rubyDark = Color(0xFF922B21)

    // Gold Ring Oval
    drawOval(
        color = gold,
        topLeft = Offset(center.x - 28f * s, center.y - 15f * s),
        size = Size(56f * s, 45f * s),
        style = Stroke(6f * s)
    )

    // Gem Mount Setting
    drawRect(gold, Offset(center.x - 12f * s, center.y - 22f * s), Size(24f * s, 16f * s))

    // Glowing Flame Ruby
    val rubyPath = Path().apply {
        moveTo(center.x, center.y - 28f * s)
        lineTo(center.x + 10f * s, center.y - 18f * s)
        lineTo(center.x, center.y - 8f * s)
        lineTo(center.x - 10f * s, center.y - 18f * s)
        close()
    }

    val pulse = 0.85f + sin(animTime * 0.006f) * 0.15f
    drawPath(rubyPath, ruby.copy(alpha = pulse))
    drawPath(rubyPath, rubyDark, style = Stroke(1.5f * s))
    drawCircle(Color.White, radius = 2f * s, center = Offset(center.x - 3f * s, center.y - 20f * s))
}

private fun DrawScope.drawAvariceCoin(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val shimmer = 0.85f + sin(animTime * 0.005f) * 0.15f
    val gold = Color(0xFFF39C12).copy(alpha = shimmer)
    val goldBright = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)

    // Outer Thick Rim
    drawCircle(goldDark, radius = 32f * s, center = center)
    drawCircle(gold, radius = 30f * s, center = center)
    drawCircle(goldBright, radius = 26f * s, center = center)

    // Inner Ridge & Dragon Motif
    drawCircle(goldDark, radius = 24f * s, center = center, style = Stroke(1.5f * s))

    // Embossed Star Motif
    repeat(5) { i ->
        val angle = i * (3.14159f * 2f / 5f) - 3.14159f / 2f
        val px = center.x + cos(angle) * 16f * s
        val py = center.y + sin(angle) * 16f * s
        drawLine(goldDark, center, Offset(px, py), strokeWidth = 2.5f * s)
    }
    drawCircle(goldDark, radius = 6f * s, center = center)
    drawCircle(goldBright, radius = 3f * s, center = center)
}

private fun DrawScope.drawAstralPrism(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val purple = Color(0xFF9B59B6)
    val purpleDark = Color(0xFF6C3483)
    val purpleLight = Color(0xFFD2B4DE)

    val rot = animTime * 0.002f
    val bobY = center.y + sin(rot * 2f) * 4f * s

    // Diamond Prism Facets
    val topFacet = Path().apply {
        moveTo(center.x, bobY - 32f * s)
        lineTo(center.x + 22f * s, bobY - 8f * s)
        lineTo(center.x, bobY)
        lineTo(center.x - 22f * s, bobY - 8f * s)
        close()
    }
    val botFacet = Path().apply {
        moveTo(center.x, bobY)
        lineTo(center.x + 22f * s, bobY - 8f * s)
        lineTo(center.x, bobY + 32f * s)
        lineTo(center.x - 22f * s, bobY - 8f * s)
        close()
    }

    drawPath(topFacet, purpleLight)
    drawPath(botFacet, purple)
    drawPath(topFacet, purpleDark, style = Stroke(1.5f * s))
    drawPath(botFacet, purpleDark, style = Stroke(1.5f * s))

    // Specular Sparkle
    drawCircle(Color.White, radius = 3f * s, center = Offset(center.x - 6f * s, bobY - 16f * s))
}

private fun DrawScope.drawMagneticCompass(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val gold = Color(0xFFD4AC0D)
    val face = Color(0xFFF9E79F)
    val needleRed = Color(0xFFE74C3C)
    val needleBlue = Color(0xFF3498DB)

    drawCircle(gold, radius = 30f * s, center = center)
    drawCircle(face, radius = 24f * s, center = center)
    drawCircle(gold, radius = 24f * s, center = center, style = Stroke(1.5f * s))

    // Oscillating Needle
    val swing = sin(animTime * 0.004f) * 0.2f
    val needlePathRed = Path().apply {
        moveTo(center.x, center.y)
        lineTo(center.x - 4f * s, center.y)
        lineTo(center.x + sin(swing) * 5f * s, center.y - 18f * s)
        lineTo(center.x + 4f * s, center.y)
        close()
    }
    val needlePathBlue = Path().apply {
        moveTo(center.x, center.y)
        lineTo(center.x + 4f * s, center.y)
        lineTo(center.x - sin(swing) * 5f * s, center.y + 18f * s)
        lineTo(center.x - 4f * s, center.y)
        close()
    }

    drawPath(needlePathRed, needleRed)
    drawPath(needlePathBlue, needleBlue)
    drawCircle(Color(0xFF2C3E50), radius = 3f * s, center = center)
}

private fun DrawScope.drawDimensionalPouch(s: Float, animTime: Float) {
    val center = Offset(50f * s, 55f * s)
    val leather = Color(0xFF7E5109)
    val leatherDark = Color(0xFF422C05)
    val gold = Color(0xFFF1C40F)
    val cyan = Color(0xFF1ABC9C)

    val pouchPath = Path().apply {
        moveTo(center.x - 12f * s, center.y - 25f * s)
        lineTo(center.x + 12f * s, center.y - 25f * s)
        quadraticTo(center.x + 28f * s, center.y - 10f * s, center.x + 24f * s, center.y + 20f * s)
        quadraticTo(center.x, center.y + 28f * s, center.x - 24f * s, center.y + 20f * s)
        quadraticTo(center.x - 28f * s, center.y - 10f * s, center.x - 12f * s, center.y - 25f * s)
        close()
    }

    drawPath(pouchPath, leather)
    drawPath(pouchPath, leatherDark, style = Stroke(2.5f * s))

    // Tie String
    drawRect(gold, Offset(center.x - 14f * s, center.y - 15f * s), Size(28f * s, 3f * s))

    // Cyan Runic Stitches
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    drawCircle(cyan.copy(alpha = pulse), radius = 4f * s, center = Offset(center.x, center.y + 2f * s))
}

private fun DrawScope.drawDoubleChest(s: Float, animTime: Float) {
    val center = Offset(50f * s, 55f * s)
    val gold = Color(0xFFF1C40F)
    val wood = Color(0xFF7E5109)
    val dark = Color(0xFF422C05)

    // Chest Body
    drawRect(wood, Offset(center.x - 26f * s, center.y - 12f * s), Size(52f * s, 32f * s))
    drawRect(dark, Offset(center.x - 26f * s, center.y - 12f * s), Size(52f * s, 32f * s), style = Stroke(2f * s))

    // Chest Lid
    val lidPath = Path().apply {
        moveTo(center.x - 28f * s, center.y - 12f * s)
        quadraticTo(center.x, center.y - 28f * s, center.x + 28f * s, center.y - 12f * s)
        close()
    }
    drawPath(lidPath, wood)
    drawPath(lidPath, dark, style = Stroke(2f * s))

    // Gold Straps
    drawRect(gold, Offset(center.x - 18f * s, center.y - 20f * s), Size(4f * s, 40f * s))
    drawRect(gold, Offset(center.x + 14f * s, center.y - 20f * s), Size(4f * s, 40f * s))

    // Center Lock
    drawRect(gold, Offset(center.x - 5f * s, center.y - 8f * s), Size(10f * s, 10f * s))
    drawCircle(dark, radius = 2f * s, center = Offset(center.x, center.y - 3f * s))
}

private fun DrawScope.drawSoulAthanor(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val gold = Color(0xFFF1C40F)
    val cyan = Color(0xFF00E5FF)
    val blue = Color(0xFF29B6F6)

    // Glass Flask Oval
    drawOval(
        color = blue.copy(alpha = 0.4f),
        topLeft = Offset(center.x - 20f * s, center.y - 15f * s),
        size = Size(40f * s, 45f * s)
    )

    // Liquid Level
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    drawOval(
        color = cyan.copy(alpha = pulse),
        topLeft = Offset(center.x - 18f * s, center.y),
        size = Size(36f * s, 28f * s)
    )

    // Golden Casing Bracket
    drawOval(
        color = gold,
        topLeft = Offset(center.x - 20f * s, center.y - 15f * s),
        size = Size(40f * s, 45f * s),
        style = Stroke(2.5f * s)
    )

    // Neck & Stopper
    drawRect(gold, Offset(center.x - 8f * s, center.y - 25f * s), Size(16f * s, 10f * s))
    drawCircle(Color(0xFF8E44AD), radius = 5f * s, center = Offset(center.x, center.y - 28f * s))
}
