package com.game.dungeon.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import com.game.dungeon.data.models.RelicType
import kotlin.math.cos
import kotlin.math.sin

/**
 * Procedural Pixel Art Drawing for Legendary Relics
 */
fun DrawScope.drawRelicSprite(relicType: RelicType, animTime: Float = 0f) {
    val sizeMin = minOf(size.width, size.height)
    val s = sizeMin / 100f
    val offsetX = (size.width - 100f * s) / 2f
    val offsetY = (size.height - 100f * s) / 2f

    withTransform({
        translate(left = offsetX, top = offsetY)
    }) {
        when (relicType) {
            RelicType.MAGIC -> drawArchmageOrb(s, animTime)
            RelicType.CRIT_DAMAGE -> drawBloodfangDagger(s, animTime)
            RelicType.ATTACK -> drawFlameGreatsword(s, animTime)
            RelicType.HP -> drawLifeHeartGem(s, animTime)
            RelicType.DEFENSE -> drawRunicAegis(s, animTime)
            RelicType.CRIT_CHANCE -> drawHawkEyeMonocle(s, animTime)
            RelicType.GOLD -> drawMidasChalice(s, animTime)
            RelicType.MAGICITE_FIND -> drawMagiciteShard(s, animTime)
            RelicType.MAGNET -> drawMagneticCompass(s, animTime)
            RelicType.POCKETS -> drawDimensionalPouch(s, animTime)
            RelicType.DOUBLE_LOOT -> drawDoubleChest(s, animTime)
            RelicType.MP -> drawSoulAthanor(s, animTime)
        }
    }
}

private fun DrawScope.drawArchmageOrb(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val blueDark = Color(0xFF1B4F72)
    val blueBright = Color(0xFF29B6F6)
    val cyanGlow = Color(0xFF00E5FF)
    val gold = Color(0xFFF1C40F)
    val purple = Color(0xFF8E44AD)

    val rot = animTime * 0.002f
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // 1. Rotating Golden Rune Rings around Sphere
    drawOval(
        color = gold,
        topLeft = Offset(center.x - 34f * s, center.y - 14f * s),
        size = Size(68f * s, 28f * s),
        style = Stroke(2.5f * s)
    )

    // 2. Deep Arcane Glowing Orb
    drawCircle(purple.copy(alpha = 0.4f * pulse), radius = 28f * s, center = center)
    drawCircle(blueDark, radius = 22f * s, center = center)
    drawCircle(blueBright.copy(alpha = pulse), radius = 16f * s, center = Offset(center.x - 4f * s, center.y - 4f * s))
    drawCircle(cyanGlow.copy(alpha = pulse), radius = 8f * s, center = Offset(center.x - 6f * s, center.y - 6f * s))
    drawCircle(Color.White, radius = 3f * s, center = Offset(center.x - 8f * s, center.y - 8f * s))

    // 3. Floating Rune Accents on Ring
    repeat(4) { i ->
        val angle = rot + i * (3.14159f / 2f)
        val rx = center.x + cos(angle) * 34f * s
        val ry = center.y + sin(angle) * 12f * s
        drawCircle(cyanGlow, radius = 2.5f * s, center = Offset(rx, ry))
    }
}

private fun DrawScope.drawBloodfangDagger(s: Float, animTime: Float) {
    val crimson = Color(0xFFE74C3C)
    val crimsonDark = Color(0xFF922B21)
    val steel = Color(0xFFECF0F1)
    val steelDark = Color(0xFF7F8C8D)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Animated Crimson Slash Energy Trail / Arc
    val arcPath = Path().apply {
        moveTo(20f * s, 25f * s)
        quadraticTo(75f * s, 15f * s, 85f * s, 70f * s)
        quadraticTo(65f * s, 55f * s, 30f * s, 45f * s)
        close()
    }
    drawPath(arcPath, crimson.copy(alpha = 0.35f * pulse))

    // Sparks along the arc
    repeat(6) { i ->
        val angle = (i * 0.4f) + (animTime * 0.003f) % 2f
        val sx = 40f * s + cos(angle) * 30f * s
        val sy = 35f * s + sin(angle) * 20f * s
        drawCircle(crimson, radius = 2f * s, center = Offset(sx, sy))
        drawCircle(Color.White, radius = 1f * s, center = Offset(sx, sy))
    }

    // Razor Curved Dagger Blade
    val bladePath = Path().apply {
        moveTo(34f * s, 64f * s)
        quadraticTo(42f * s, 45f * s, 78f * s, 20f * s)
        quadraticTo(52f * s, 38f * s, 40f * s, 68f * s)
        close()
    }
    drawPath(bladePath, steel)

    // Red Bloodfang Edge Glow
    val edgePath = Path().apply {
        moveTo(34f * s, 64f * s)
        quadraticTo(42f * s, 45f * s, 78f * s, 20f * s)
        quadraticTo(40f * s, 47f * s, 34f * s, 64f * s)
        close()
    }
    drawPath(edgePath, crimson.copy(alpha = pulse))

    // Darker steel spine shadow
    drawLine(steelDark, Offset(37f * s, 66f * s), Offset(72f * s, 24f * s), strokeWidth = 2f * s)

    // Crossguard
    val guardPath = Path().apply {
        moveTo(25f * s, 62f * s)
        lineTo(42f * s, 75f * s)
        lineTo(38f * s, 79f * s)
        lineTo(21f * s, 66f * s)
        close()
    }
    drawPath(guardPath, gold)
    drawPath(guardPath, goldDark, style = Stroke(1.5f * s))

    // Handle Grip
    val hiltPath = Path().apply {
        moveTo(27f * s, 71f * s)
        lineTo(18f * s, 80f * s)
        lineTo(14f * s, 76f * s)
        lineTo(23f * s, 67f * s)
        close()
    }
    drawPath(hiltPath, steelDark)
    drawLine(gold, Offset(26f * s, 72f * s), Offset(17f * s, 81f * s), strokeWidth = 1.5f * s)

    // Pommel Gem (Ruby)
    drawCircle(crimsonDark, radius = 5f * s, center = Offset(15f * s, 81f * s))
    drawCircle(crimson.copy(alpha = pulse), radius = 4f * s, center = Offset(15f * s, 81f * s))
    drawCircle(Color.White, radius = 1.5f * s, center = Offset(13.5f * s, 79.5f * s))
}

private fun DrawScope.drawFlameGreatsword(s: Float, animTime: Float) {
    val flameRed = Color(0xFFE74C3C)
    val flameOrange = Color(0xFFE67E22)
    val flameYellow = Color(0xFFF1C40F)
    val steel = Color(0xFFECF0F1)
    val steelDark = Color(0xFF95A5A6)
    val gold = Color(0xFFF39C12)
    val goldDark = Color(0xFFB7950B)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // 1. Animated Fiery Aura behind Blade
    val flamePath1 = Path().apply {
        moveTo(50f * s, 12f * s)
        quadraticTo(70f * s, 35f * s, 62f * s, 62f * s)
        lineTo(38f * s, 62f * s)
        quadraticTo(30f * s, 35f * s, 50f * s, 12f * s)
        close()
    }
    drawPath(flamePath1, flameRed.copy(alpha = 0.4f * pulse))

    val flamePath2 = Path().apply {
        moveTo(50f * s, 16f * s)
        quadraticTo(64f * s, 38f * s, 58f * s, 60f * s)
        lineTo(42f * s, 60f * s)
        quadraticTo(36f * s, 38f * s, 50f * s, 16f * s)
        close()
    }
    drawPath(flamePath2, flameOrange.copy(alpha = 0.6f * pulse))

    // Animated sparks floating upwards
    repeat(8) { i ->
        val sparkPhase = (animTime * 0.004f + i * 0.8f) % 3.14159f
        val sx = 50f * s + sin(sparkPhase * 2f + i) * 16f * s
        val sy = 60f * s - (sparkPhase / 3.14159f) * 45f * s
        val sparkSize = (1.5f + sin(sparkPhase) * 1f) * s
        drawCircle(flameYellow.copy(alpha = sin(sparkPhase)), radius = sparkSize, center = Offset(sx, sy))
    }

    // 2. Broad Greatsword Blade (pointing straight up)
    val bladePath = Path().apply {
        moveTo(50f * s, 18f * s)
        lineTo(58f * s, 28f * s)
        lineTo(56f * s, 65f * s)
        lineTo(44f * s, 65f * s)
        lineTo(42f * s, 28f * s)
        close()
    }
    drawPath(bladePath, steel)
    drawPath(bladePath, steelDark, style = Stroke(1.5f * s))

    // Glowing Inner Blade Channel / Fuller Line
    drawLine(flameYellow.copy(alpha = pulse), Offset(50f * s, 24f * s), Offset(50f * s, 62f * s), strokeWidth = 2.5f * s)
    drawLine(Color.White, Offset(50f * s, 26f * s), Offset(50f * s, 58f * s), strokeWidth = 1f * s)

    // 3. Winged Heavy Golden Crossguard
    val guardPath = Path().apply {
        moveTo(50f * s, 65f * s)
        lineTo(68f * s, 60f * s)
        lineTo(72f * s, 68f * s)
        lineTo(58f * s, 70f * s)
        lineTo(50f * s, 68f * s)
        lineTo(42f * s, 70f * s)
        lineTo(28f * s, 68f * s)
        lineTo(32f * s, 60f * s)
        close()
    }
    drawPath(guardPath, gold)
    drawPath(guardPath, goldDark, style = Stroke(1.5f * s))

    // Center Guard Gem (Ruby)
    drawCircle(flameRed, radius = 4f * s, center = Offset(50f * s, 66f * s))
    drawCircle(Color.White, radius = 1.2f * s, center = Offset(48.5f * s, 64.5f * s))

    // 4. Leather Grip Handle
    drawRect(goldDark, Offset(47f * s, 70f * s), Size(6f * s, 12f * s))
    drawLine(flameYellow, Offset(47f * s, 73f * s), Offset(53f * s, 75f * s), strokeWidth = 1.2f * s)
    drawLine(flameYellow, Offset(47f * s, 77f * s), Offset(53f * s, 79f * s), strokeWidth = 1.2f * s)

    // 5. Pommel
    drawCircle(gold, radius = 5f * s, center = Offset(50f * s, 84f * s))
    drawCircle(goldDark, radius = 5f * s, center = Offset(50f * s, 84f * s), style = Stroke(1.2f * s))
}

private fun DrawScope.drawLifeHeartGem(s: Float, animTime: Float) {
    val ruby = Color(0xFFE74C3C)
    val rubyDark = Color(0xFF922B21)
    val rubyLight = Color(0xFFF1948A)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val greenGlow = Color(0xFF2ECC71)

    val pulse = 0.88f + sin(animTime * 0.005f) * 0.12f

    // 1. Soft Radiant Healing Aura behind Heart
    drawCircle(
        color = ruby.copy(alpha = 0.25f * pulse),
        radius = 34f * s,
        center = Offset(50f * s, 48f * s)
    )

    // 2. Floating Healing Sparkles
    repeat(6) { i ->
        val sparkPhase = (animTime * 0.003f + i * 1.05f) % 3.14159f
        val sx = 50f * s + cos(sparkPhase * 2f + i) * 28f * s
        val sy = 48f * s - sin(sparkPhase) * 22f * s
        val sz = (1.2f + sin(sparkPhase) * 0.8f) * s
        drawCircle(greenGlow.copy(alpha = sin(sparkPhase) * 0.8f), radius = sz, center = Offset(sx, sy))
    }

    // 3. Faceted Crimson Heart Crystal Path
    val heartPath = Path().apply {
        moveTo(50f * s, 38f * s)
        cubicTo(50f * s, 24f * s, 28f * s, 24f * s, 28f * s, 42f * s)
        cubicTo(28f * s, 58f * s, 46f * s, 68f * s, 50f * s, 74f * s)
        cubicTo(54f * s, 68f * s, 72f * s, 58f * s, 72f * s, 42f * s)
        cubicTo(72f * s, 24f * s, 50f * s, 24f * s, 50f * s, 38f * s)
        close()
    }
    drawPath(heartPath, ruby)

    // 4. Inner Darker Crystal Shadow Facet (Bottom Half)
    val shadowPath = Path().apply {
        moveTo(50f * s, 48f * s)
        lineTo(29f * s, 44f * s)
        cubicTo(32f * s, 58f * s, 46f * s, 68f * s, 50f * s, 74f * s)
        cubicTo(54f * s, 68f * s, 68f * s, 58f * s, 71f * s, 44f * s)
        close()
    }
    drawPath(shadowPath, rubyDark.copy(alpha = 0.6f))

    // Gem Facet Inner Lines
    drawLine(rubyDark, Offset(50f * s, 38f * s), Offset(50f * s, 74f * s), strokeWidth = 1.5f * s)
    drawLine(rubyDark, Offset(50f * s, 48f * s), Offset(28f * s, 42f * s), strokeWidth = 1.2f * s)
    drawLine(rubyDark, Offset(50f * s, 48f * s), Offset(72f * s, 42f * s), strokeWidth = 1.2f * s)

    // Gold Border / Outline around Heart
    drawPath(heartPath, goldDark, style = Stroke(2f * s))

    // 5. Cradling Golden Vine Filigree
    val vineLeft = Path().apply {
        moveTo(50f * s, 78f * s)
        quadraticTo(24f * s, 66f * s, 24f * s, 42f * s)
        quadraticTo(24f * s, 28f * s, 32f * s, 24f * s)
    }
    val vineRight = Path().apply {
        moveTo(50f * s, 78f * s)
        quadraticTo(76f * s, 66f * s, 76f * s, 42f * s)
        quadraticTo(76f * s, 28f * s, 68f * s, 24f * s)
    }
    drawPath(vineLeft, gold, style = Stroke(2.5f * s))
    drawPath(vineRight, gold, style = Stroke(2.5f * s))

    // Golden Vine Leaf Ornaments
    drawCircle(greenGlow, radius = 3f * s, center = Offset(24f * s, 42f * s))
    drawCircle(greenGlow, radius = 3f * s, center = Offset(76f * s, 42f * s))
    drawCircle(gold, radius = 4f * s, center = Offset(50f * s, 78f * s))

    // 6. Top Specular Glint & Highlight
    drawCircle(rubyLight, radius = 4f * s, center = Offset(38f * s, 34f * s))
    drawCircle(Color.White, radius = 2f * s, center = Offset(37f * s, 33f * s))
}

private fun DrawScope.drawRunicAegis(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val iron = Color(0xFF2C3E50)
    val runeBlue = Color(0xFF00E5FF)
    val blueDark = Color(0xFF1B4F72)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // 1. Radiant Blue Protection Shield Barrier Aura
    val outerShield = Path().apply {
        moveTo(center.x - 30f * s, center.y - 34f * s)
        lineTo(center.x + 30f * s, center.y - 34f * s)
        lineTo(center.x + 30f * s, center.y + 5f * s)
        quadraticTo(center.x + 24f * s, center.y + 36f * s, center.x, center.y + 44f * s)
        quadraticTo(center.x - 24f * s, center.y + 36f * s, center.x - 30f * s, center.y + 5f * s)
        close()
    }
    drawPath(outerShield, runeBlue.copy(alpha = 0.25f * pulse))

    // 2. Heavy Golden Winged Shield Frame
    val shieldPath = Path().apply {
        moveTo(center.x - 26f * s, center.y - 30f * s)
        lineTo(center.x + 26f * s, center.y - 30f * s)
        lineTo(center.x + 26f * s, center.y + 5f * s)
        quadraticTo(center.x + 20f * s, center.y + 32f * s, center.x, center.y + 40f * s)
        quadraticTo(center.x - 20f * s, center.y + 32f * s, center.x - 26f * s, center.y + 5f * s)
        close()
    }
    drawPath(shieldPath, gold)
    drawPath(shieldPath, goldDark, style = Stroke(2.5f * s))

    // 3. Inner Dark Steel Inlay Plate
    val innerShield = Path().apply {
        moveTo(center.x - 18f * s, center.y - 23f * s)
        lineTo(center.x + 18f * s, center.y - 23f * s)
        lineTo(center.x + 18f * s, center.y + 2f * s)
        quadraticTo(center.x + 14f * s, center.y + 24f * s, center.x, center.y + 31f * s)
        quadraticTo(center.x - 14f * s, center.y + 24f * s, center.x - 18f * s, center.y + 2f * s)
        close()
    }
    drawPath(innerShield, iron)

    // 4. Glowing Cyan Arcane Cross Barrier Rune
    drawCircle(blueDark, radius = 12f * s, center = Offset(center.x, center.y - 2f * s))
    drawCircle(runeBlue.copy(alpha = pulse), radius = 10f * s, center = Offset(center.x, center.y - 2f * s))

    drawLine(Color.White, Offset(center.x, center.y - 18f * s), Offset(center.x, center.y + 14f * s), strokeWidth = 3f * s)
    drawLine(Color.White, Offset(center.x - 12f * s, center.y - 2f * s), Offset(center.x + 12f * s, center.y - 2f * s), strokeWidth = 3f * s)

    // Top Ruby Gem Ornament
    drawCircle(Color(0xFFE74C3C), radius = 4f * s, center = Offset(center.x, center.y - 30f * s))
    drawCircle(Color.White, radius = 1.2f * s, center = Offset(center.x - 1f * s, center.y - 31f * s))
}

private fun DrawScope.drawHawkEyeMonocle(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val targetRed = Color(0xFFE74C3C)
    val cyanGlow = Color(0xFF00E5FF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Outer Golden Winged Monocle Frame
    drawCircle(gold, radius = 28f * s, center = center)
    drawCircle(goldDark, radius = 28f * s, center = center, style = Stroke(2.5f * s))
    drawCircle(Color(0xFF1C2833), radius = 24f * s, center = center)

    // Inner Glowing Precision Glass Lens
    drawCircle(cyanGlow.copy(alpha = 0.35f * pulse), radius = 22f * s, center = center)

    // Target Crosshair / Reticle Lines
    drawLine(targetRed.copy(alpha = pulse), Offset(center.x, center.y - 20f * s), Offset(center.x, center.y + 20f * s), strokeWidth = 2f * s)
    drawLine(targetRed.copy(alpha = pulse), Offset(center.x - 20f * s, center.y), Offset(center.x + 20f * s, center.y), strokeWidth = 2f * s)

    // Reticle Target Rings
    drawCircle(targetRed.copy(alpha = pulse), radius = 14f * s, center = center, style = Stroke(1.5f * s))
    drawCircle(targetRed, radius = 4f * s, center = center)
    drawCircle(Color.White, radius = 1.5f * s, center = center)

    // Golden Winged Feathers on Top-Right Corner
    val wingPath = Path().apply {
        moveTo(center.x + 22f * s, center.y - 18f * s)
        quadraticTo(center.x + 38f * s, center.y - 34f * s, center.x + 42f * s, center.y - 18f * s)
        quadraticTo(center.x + 32f * s, center.y - 12f * s, center.x + 22f * s, center.y - 18f * s)
    }
    drawPath(wingPath, gold)
    drawPath(wingPath, goldDark, style = Stroke(1.5f * s))
}

private fun DrawScope.drawMidasChalice(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val goldBright = Color(0xFFF8EFBA)
    val goldDark = Color(0xFFB7950B)
    val ruby = Color(0xFFE74C3C)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // 1. Golden Chalice Cup Body
    val cupPath = Path().apply {
        moveTo(22f * s, 20f * s)
        lineTo(78f * s, 20f * s)
        quadraticTo(78f * s, 52f * s, 50f * s, 60f * s)
        quadraticTo(22f * s, 52f * s, 22f * s, 20f * s)
        close()
    }
    drawPath(cupPath, gold)

    // Inner Cup Rim Dark Shade
    drawOval(goldDark, Offset(22f * s, 16f * s), Size(56f * s, 10f * s))
    drawOval(goldBright, Offset(24f * s, 17f * s), Size(52f * s, 8f * s))

    // Overflowing Shiny Gold Coins inside Chalice
    repeat(5) { i ->
        val cx = 35f + i * 7.5f
        val cy = 20f - sin(i * 0.8f) * 4f
        drawCircle(goldBright, radius = 5f * s, center = Offset(cx * s, cy * s))
        drawCircle(goldDark, radius = 5f * s, center = Offset(cx * s, cy * s), style = Stroke(1f * s))
    }

    // Chalice Stem & Base Pedestal
    drawRect(goldDark, Offset(46f * s, 60f * s), Size(8f * s, 18f * s))
    drawRect(gold, Offset(47f * s, 60f * s), Size(6f * s, 18f * s))

    val basePath = Path().apply {
        moveTo(32f * s, 82f * s)
        lineTo(68f * s, 82f * s)
        lineTo(62f * s, 76f * s)
        lineTo(38f * s, 76f * s)
        close()
    }
    drawPath(basePath, gold)
    drawPath(basePath, goldDark, style = Stroke(1.5f * s))

    // Embossed Ruby Gem on Center Chalice Body
    drawCircle(ruby.copy(alpha = pulse), radius = 5f * s, center = Offset(50f * s, 38f * s))
    drawCircle(Color.White, radius = 1.5f * s, center = Offset(48.5f * s, 36.5f * s))

    // Outer Chalice Gold Outline
    drawPath(cupPath, goldDark, style = Stroke(2f * s))

    // Sparkling Gold Embers / Particles floating up
    repeat(6) { i ->
        val sparkPhase = (animTime * 0.004f + i * 1.1f) % 3.14159f
        val sx = 50f * s + cos(sparkPhase * 2f + i) * 26f * s
        val sy = 24f * s - sin(sparkPhase) * 18f * s
        drawCircle(goldBright.copy(alpha = sin(sparkPhase)), radius = 2f * s, center = Offset(sx, sy))
    }
}

private fun DrawScope.drawMagiciteShard(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val emerald = Color(0xFF2ECC71)
    val emeraldDark = Color(0xFF16A085)
    val emeraldLight = Color(0xFFA3E4D7)
    val cyanCore = Color(0xFF00E5FF)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)

    val bobY = center.y + sin(animTime * 0.003f) * 3f * s
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Radiant Green Magicite Aura
    drawCircle(
        color = emerald.copy(alpha = 0.3f * pulse),
        radius = 32f * s,
        center = Offset(center.x, bobY)
    )

    // Floating Mana Sparkles around Magicite
    repeat(6) { i ->
        val angle = (animTime * 0.003f + i * 1.05f) % 6.28318f
        val sx = center.x + cos(angle) * (22f + sin(angle) * 6f) * s
        val sy = bobY + sin(angle) * (22f + cos(angle) * 6f) * s
        drawCircle(cyanCore.copy(alpha = 0.8f * pulse), radius = 1.8f * s, center = Offset(sx, sy))
        drawCircle(Color.White, radius = 0.8f * s, center = Offset(sx, sy))
    }

    // Jagged FF VI Magicite Main Crystal Body
    val mainShard = Path().apply {
        moveTo(center.x - 4f * s, bobY - 36f * s)
        lineTo(center.x + 18f * s, bobY - 14f * s)
        lineTo(center.x + 22f * s, bobY + 12f * s)
        lineTo(center.x + 4f * s, bobY + 32f * s)
        lineTo(center.x - 18f * s, bobY + 22f * s)
        lineTo(center.x - 24f * s, bobY - 8f * s)
        close()
    }
    drawPath(mainShard, emerald)

    // Facet 1: Top Right Facet
    val facetTopRight = Path().apply {
        moveTo(center.x - 4f * s, bobY - 36f * s)
        lineTo(center.x + 18f * s, bobY - 14f * s)
        lineTo(center.x, bobY - 2f * s)
        close()
    }
    drawPath(facetTopRight, emeraldLight.copy(alpha = 0.8f))

    // Facet 2: Left Shadow Facet
    val facetLeft = Path().apply {
        moveTo(center.x - 4f * s, bobY - 36f * s)
        lineTo(center.x - 24f * s, bobY - 8f * s)
        lineTo(center.x - 18f * s, bobY + 22f * s)
        lineTo(center.x, bobY - 2f * s)
        close()
    }
    drawPath(facetLeft, emeraldDark.copy(alpha = 0.85f))

    // Facet 3: Bottom Right Dark Facet
    val facetBotRight = Path().apply {
        moveTo(center.x, bobY - 2f * s)
        lineTo(center.x + 22f * s, bobY + 12f * s)
        lineTo(center.x + 4f * s, bobY + 32f * s)
        close()
    }
    drawPath(facetBotRight, emeraldDark)

    // Inner Glowing Cyan Core Gem Inlay
    val corePath = Path().apply {
        moveTo(center.x - 4f * s, bobY - 12f * s)
        lineTo(center.x + 8f * s, bobY - 2f * s)
        lineTo(center.x + 2f * s, bobY + 14f * s)
        lineTo(center.x - 8f * s, bobY + 6f * s)
        close()
    }
    drawPath(corePath, cyanCore.copy(alpha = 0.9f * pulse))

    // Crystal Edges / Outline
    drawPath(mainShard, emeraldDark, style = Stroke(2f * s))

    // Golden Rune Mounting Ring at Base
    val mountPath = Path().apply {
        moveTo(center.x - 20f * s, bobY + 20f * s)
        lineTo(center.x, bobY + 34f * s)
        lineTo(center.x + 20f * s, bobY + 20f * s)
        lineTo(center.x + 14f * s, bobY + 28f * s)
        lineTo(center.x, bobY + 38f * s)
        lineTo(center.x - 14f * s, bobY + 28f * s)
        close()
    }
    drawPath(mountPath, gold)
    drawPath(mountPath, goldDark, style = Stroke(1.5f * s))

    // Specular Highlight Glint
    drawCircle(Color.White, radius = 3f * s, center = Offset(center.x + 6f * s, bobY - 18f * s))
    drawCircle(Color.White, radius = 1.5f * s, center = Offset(center.x + 10f * s, bobY - 10f * s))
}

private fun DrawScope.drawMagneticCompass(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val gold = Color(0xFFD4AC0D)
    val goldDark = Color(0xFFB7950B)
    val face = Color(0xFFF9E79F)
    val needleRed = Color(0xFFE74C3C)
    val needleBlue = Color(0xFF3498DB)
    val sparkCyan = Color(0xFF00E5FF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Outer Compass Ring with Gold Ridges
    drawCircle(goldDark, radius = 32f * s, center = center)
    drawCircle(gold, radius = 30f * s, center = center)
    drawCircle(face, radius = 24f * s, center = center)
    drawCircle(goldDark, radius = 24f * s, center = center, style = Stroke(1.5f * s))

    // Compass Card Rose Dial Ticks
    repeat(8) { i ->
        val angle = i * (3.14159f / 4f)
        val tx1 = center.x + cos(angle) * 18f * s
        val ty1 = center.y + sin(angle) * 18f * s
        val tx2 = center.x + cos(angle) * 23f * s
        val ty2 = center.y + sin(angle) * 23f * s
        drawLine(goldDark, Offset(tx1, ty1), Offset(tx2, ty2), strokeWidth = 1.5f * s)
    }

    // Oscillating Magnetic Needle
    val swing = sin(animTime * 0.004f) * 0.25f
    val needlePathRed = Path().apply {
        moveTo(center.x, center.y)
        lineTo(center.x - 4f * s, center.y)
        lineTo(center.x + sin(swing) * 6f * s, center.y - 20f * s)
        lineTo(center.x + 4f * s, center.y)
        close()
    }
    val needlePathBlue = Path().apply {
        moveTo(center.x, center.y)
        lineTo(center.x + 4f * s, center.y)
        lineTo(center.x - sin(swing) * 6f * s, center.y + 20f * s)
        lineTo(center.x - 4f * s, center.y)
        close()
    }

    drawPath(needlePathRed, needleRed)
    drawPath(needlePathBlue, needleBlue)
    drawCircle(Color(0xFF2C3E50), radius = 3.5f * s, center = center)
    drawCircle(Color.White, radius = 1.2f * s, center = Offset(center.x - 1f * s, center.y - 1f * s))

    // Magnetic Sparkles floating near tips
    repeat(4) { i ->
        val sparkPhase = (animTime * 0.005f + i * 1.5f) % 3.14159f
        val sx = center.x + sin(sparkPhase * 2f + i) * 28f * s
        val sy = center.y + cos(sparkPhase * 2f + i) * 28f * s
        drawCircle(sparkCyan.copy(alpha = sin(sparkPhase) * pulse), radius = 1.8f * s, center = Offset(sx, sy))
    }
}

private fun DrawScope.drawDimensionalPouch(s: Float, animTime: Float) {
    val center = Offset(50f * s, 52f * s)
    val leather = Color(0xFF7E5109)
    val leatherDark = Color(0xFF422C05)
    val gold = Color(0xFFF1C40F)
    val purpleVoid = Color(0xFF8E44AD)
    val cyanGlow = Color(0xFF00E5FF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Swirling Void Portal Glow coming out of pouch
    drawCircle(
        color = purpleVoid.copy(alpha = 0.35f * pulse),
        radius = 28f * s,
        center = Offset(center.x, center.y - 10f * s)
    )

    // Pouch Main Body
    val pouchPath = Path().apply {
        moveTo(center.x - 14f * s, center.y - 20f * s)
        lineTo(center.x + 14f * s, center.y - 20f * s)
        quadraticTo(center.x + 30f * s, center.y - 5f * s, center.x + 26f * s, center.y + 24f * s)
        quadraticTo(center.x, center.y + 32f * s, center.x - 26f * s, center.y + 24f * s)
        quadraticTo(center.x - 30f * s, center.y - 5f * s, center.x - 14f * s, center.y - 20f * s)
        close()
    }

    drawPath(pouchPath, leather)
    drawPath(pouchPath, leatherDark, style = Stroke(2.5f * s))

    // Void Portal Opening inside pouch neck
    drawOval(
        color = purpleVoid,
        topLeft = Offset(center.x - 14f * s, center.y - 24f * s),
        size = Size(28f * s, 10f * s)
    )
    drawOval(
        color = cyanGlow.copy(alpha = pulse),
        topLeft = Offset(center.x - 10f * s, center.y - 22f * s),
        size = Size(20f * s, 6f * s)
    )

    // Golden Belt Strap & Buckle
    drawRect(gold, Offset(center.x - 16f * s, center.y - 12f * s), Size(32f * s, 4f * s))
    drawRect(leatherDark, Offset(center.x - 5f * s, center.y - 14f * s), Size(10f * s, 8f * s))
    drawRect(gold, Offset(center.x - 5f * s, center.y - 14f * s), Size(10f * s, 8f * s), style = Stroke(1.5f * s))

    // Runic Stitches
    drawCircle(cyanGlow.copy(alpha = pulse), radius = 3f * s, center = Offset(center.x, center.y + 8f * s))
}

private fun DrawScope.drawDoubleChest(s: Float, animTime: Float) {
    val center = Offset(50f * s, 54f * s)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val wood = Color(0xFF7E5109)
    val dark = Color(0xFF422C05)
    val ruby = Color(0xFFE74C3C)
    val emerald = Color(0xFF2ECC71)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Soft Golden Treasure Aura behind Chest
    drawCircle(gold.copy(alpha = 0.25f * pulse), radius = 34f * s, center = center)

    // Double Chest Main Wood Body
    drawRect(wood, Offset(center.x - 30f * s, center.y - 10f * s), Size(60f * s, 32f * s))
    drawRect(dark, Offset(center.x - 30f * s, center.y - 10f * s), Size(60f * s, 32f * s), style = Stroke(2.5f * s))

    // Arched Lid
    val lidPath = Path().apply {
        moveTo(center.x - 32f * s, center.y - 10f * s)
        quadraticTo(center.x, center.y - 30f * s, center.x + 32f * s, center.y - 10f * s)
        close()
    }
    drawPath(lidPath, wood)
    drawPath(lidPath, dark, style = Stroke(2.5f * s))

    // Gold Metal Corner Plates & Straps
    drawRect(gold, Offset(center.x - 22f * s, center.y - 20f * s), Size(5f * s, 42f * s))
    drawRect(gold, Offset(center.x + 17f * s, center.y - 20f * s), Size(5f * s, 42f * s))

    // Golden Lock Mechanism with Embedded Gems
    drawRect(gold, Offset(center.x - 6f * s, center.y - 8f * s), Size(12f * s, 12f * s))
    drawRect(goldDark, Offset(center.x - 6f * s, center.y - 8f * s), Size(12f * s, 12f * s), style = Stroke(1.5f * s))
    drawCircle(ruby, radius = 2.5f * s, center = Offset(center.x - 2f * s, center.y - 2f * s))
    drawCircle(emerald, radius = 2.5f * s, center = Offset(center.x + 2f * s, center.y - 2f * s))

    // Sparkling Loot Embers
    repeat(5) { i ->
        val sparkPhase = (animTime * 0.004f + i * 1.1f) % 3.14159f
        val sx = center.x + sin(sparkPhase * 2f + i) * 26f * s
        val sy = center.y - 12f * s - sin(sparkPhase) * 16f * s
        drawCircle(gold.copy(alpha = sin(sparkPhase)), radius = 2f * s, center = Offset(sx, sy))
    }
}

private fun DrawScope.drawSoulAthanor(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val cyan = Color(0xFF00E5FF)
    val blue = Color(0xFF29B6F6)
    val purple = Color(0xFF8E44AD)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Radiant Mana Aura
    drawCircle(blue.copy(alpha = 0.25f * pulse), radius = 34f * s, center = center)

    // Glass Flask Outer Body
    drawOval(
        color = blue.copy(alpha = 0.35f),
        topLeft = Offset(center.x - 22f * s, center.y - 12f * s),
        size = Size(44f * s, 48f * s)
    )

    // Liquid Level Swirl
    drawOval(
        color = cyan.copy(alpha = pulse),
        topLeft = Offset(center.x - 20f * s, center.y + 4f * s),
        size = Size(40f * s, 30f * s)
    )

    // Golden Casing Bracket & Cage
    drawOval(
        color = gold,
        topLeft = Offset(center.x - 22f * s, center.y - 12f * s),
        size = Size(44f * s, 48f * s),
        style = Stroke(2.5f * s)
    )

    // Golden Vertical Rib Brackets
    drawLine(goldDark, Offset(center.x, center.y - 12f * s), Offset(center.x, center.y + 36f * s), strokeWidth = 2f * s)

    // Flask Neck & Gold Ring
    drawRect(goldDark, Offset(center.x - 9f * s, center.y - 24f * s), Size(18f * s, 12f * s))
    drawRect(gold, Offset(center.x - 8f * s, center.y - 23f * s), Size(16f * s, 10f * s))

    // Amethyst Stopper Gem
    drawCircle(purple, radius = 6f * s, center = Offset(center.x, center.y - 28f * s))
    drawCircle(Color.White, radius = 1.8f * s, center = Offset(center.x - 2f * s, center.y - 30f * s))

    // Floating Mana Bubbles
    repeat(4) { i ->
        val bubblePhase = (animTime * 0.004f + i * 0.9f) % 3.14159f
        val bx = center.x + cos(bubblePhase * 2f + i) * 12f * s
        val by = center.y + 20f * s - (bubblePhase / 3.14159f) * 24f * s
        drawCircle(Color.White.copy(alpha = sin(bubblePhase)), radius = 1.8f * s, center = Offset(bx, by))
    }
}
