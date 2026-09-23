package com.game.dungeon.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sin

private fun DrawScope.pxRect(x: Float, y: Float, w: Float, h: Float, color: Color) {
    val scale = size.width / 100f
    drawRect(color, Offset(x * scale, y * scale), Size(w * scale, h * scale))
}

/**
 * 1. CRYSTAL BAZAAR & MAGIC MERCHANT STALL (Matching the Reference Image)
 * - Wooden market stall counter with plank detailing & corner posts with inset gem joint blocks.
 * - Scalloped striped purple & indigo fabric awnings with a side extension canopy.
 * - Hooded merchant NPC standing inside behind the counter with glowing eyes.
 * - Shelves behind merchant displaying colorful potion jars and magical bottles.
 * - 4 Front counter pedestals holding glowing raw crystals (Amber Gold, Cyan Cluster, Magenta Gem, Amethyst).
 * - Dual warm lanterns mounted on front wooden posts.
 * - Side stacked wooden crates & floating magic sparkle particles.
 */
fun DrawScope.drawDetailedCrystalShop(animTime: Float = 0f) {
    val W = size.width
    val s = W / 100f

    // === 1. Cobblestone Ground Base & Side Props ===
    pxRect(10f, 92f, 80f, 4f, Color(0xFF2C3E50)) // Stone cobblestones
    // Cobblestone paver accents
    for (i in 0..5) {
        pxRect(12f + i * 13f, 93f, 8f, 2f, Color(0xFF1B2631))
    }

    // Stacked Wooden Crates (Left Side Outside)
    pxRect(12f, 82f, 10f, 10f, Color(0xFF7E5109)) // Lower crate
    drawRect(Color(0xFF422C05), Offset(12f * s, 82f * s), Size(10f * s, 10f * s), style = Stroke(1.2f * s))
    pxRect(13f, 74f, 8f, 8f, Color(0xFF8D5B41))  // Upper crate
    drawRect(Color(0xFF422C05), Offset(13f * s, 74f * s), Size(8f * s, 8f * s), style = Stroke(1.2f * s))

    // === 2. Background Shelving Unit & Potion Bottles ===
    pxRect(24f, 32f, 58f, 32f, Color(0xFF2A1C15)) // Dark wooden back wall
    pxRect(26f, 44f, 54f, 2.5f, Color(0xFF422C05)) // Middle shelf
    pxRect(26f, 54f, 54f, 2.5f, Color(0xFF422C05)) // Lower shelf

    // Potion Bottles on Shelves
    // Top shelf potions (Left & Right of merchant area)
    drawCircle(Color(0xFF2ECC71), radius = 2.2f * s, center = Offset(30f * s, 39f * s)) // Emerald
    drawCircle(Color(0xFFF1C40F), radius = 2.2f * s, center = Offset(36f * s, 39f * s)) // Gold
    drawCircle(Color(0xFFE74C3C), radius = 2.2f * s, center = Offset(72f * s, 39f * s)) // Red
    drawCircle(Color(0xFF3498DB), radius = 2.2f * s, center = Offset(78f * s, 39f * s)) // Blue

    // Middle shelf potions
    drawCircle(Color(0xFF3498DB), radius = 2.2f * s, center = Offset(32f * s, 49f * s)) // Blue
    drawCircle(Color(0xFFA569BD), radius = 2.2f * s, center = Offset(74f * s, 49f * s)) // Purple

    // === 3. Hooded Merchant NPC (Center Behind Counter) ===
    // Crimson Hooded Cloak
    val hoodPath = Path().apply {
        moveTo(42f * s, 64f * s)  // Shoulder left
        lineTo(44f * s, 44f * s)  // Hood left
        quadraticTo(50f * s, 36f * s, 56f * s, 44f * s) // Hood top
        lineTo(58f * s, 64f * s)  // Shoulder right
        close()
    }
    drawPath(hoodPath, Color(0xFF78281F)) // Deep crimson cloak
    drawPath(hoodPath, Color(0xFF4A120B), style = Stroke(1.5f * s))

    // Shadowed Face Opening under Hood
    val facePath = Path().apply {
        moveTo(46f * s, 46f * s)
        quadraticTo(50f * s, 43f * s, 54f * s, 46f * s)
        quadraticTo(54f * s, 53f * s, 50f * s, 55f * s)
        quadraticTo(46f * s, 53f * s, 46f * s, 46f * s)
        close()
    }
    drawPath(facePath, Color(0xFF1B120C)) // Deep face shadow

    // Glowing Yellow/White NPC Eyes
    val eyeGlow = 0.85f + sin(animTime * 0.005f) * 0.15f
    drawCircle(Color(0xFFF1C40F).copy(alpha = eyeGlow), radius = 1.2f * s, center = Offset(48f * s, 48.5f * s))
    drawCircle(Color.White.copy(alpha = eyeGlow), radius = 0.6f * s, center = Offset(48f * s, 48.5f * s))
    drawCircle(Color(0xFFF1C40F).copy(alpha = eyeGlow), radius = 1.2f * s, center = Offset(52f * s, 48.5f * s))
    drawCircle(Color.White.copy(alpha = eyeGlow), radius = 0.6f * s, center = Offset(52f * s, 48.5f * s))

    // === 4. Wooden Market Counter Base ===
    pxRect(22f, 64f, 62f, 28f, Color(0xFF5D4037)) // Main counter wood
    pxRect(20f, 61f, 66f, 3.5f, Color(0xFF7E5109)) // Counter top slab

    // Vertical & Horizontal Plank Groove Lines
    for (px in 32..72 step 10) {
        pxRect(px.toFloat(), 64f, 1.2f, 28f, Color(0xFF3E2723))
    }
    pxRect(22f, 78f, 62f, 1.5f, Color(0xFF3E2723))

    // === 5. Wooden Corner Posts & Gem Joint Blocks ===
    val postColor = Color(0xFF5D4037)
    val capColor = Color(0xFF3E2723)

    // Vertical posts
    pxRect(20f, 22f, 5f, 39f, postColor)
    pxRect(81f, 22f, 5f, 39f, postColor)

    // Top Corner Joint Blocks with Inset Purple Gem
    listOf(20f, 81f).forEach { jx ->
        pxRect(jx - 1f, 18f, 7f, 6f, capColor)
        drawRect(Color(0xFF2C1A0E), Offset((jx - 1f) * s, 18f * s), Size(7f * s, 6f * s), style = Stroke(1f * s))
        // Purple gem insert
        drawCircle(Color(0xFFA569BD), radius = 1.6f * s, center = Offset((jx + 2.5f) * s, 21f * s))
    }

    // === 6. Scalloped Striped Purple & Indigo Awnings ===
    // Main Top Canopy Beam
    pxRect(18f, 18f, 70f, 4f, Color(0xFF422C05))

    // Main Scalloped Awning Stripes (Purple & Violet)
    val purpleDark = Color(0xFF5B2C6F)
    val purpleLight = Color(0xFF8E44AD)

    for (i in 0..7) {
        val ax = 20f + i * 8f
        val acolor = if (i % 2 == 0) purpleLight else purpleDark
        val aw = 8f

        // Triangular/Scalloped awning fold path
        val stripePath = Path().apply {
            moveTo(ax * s, 22f * s)
            lineTo((ax + aw) * s, 22f * s)
            lineTo((ax + aw * 0.8f) * s, 36f * s)
            lineTo((ax + aw * 0.5f) * s, 39f * s) // Scallop tip
            lineTo((ax + aw * 0.2f) * s, 36f * s)
            close()
        }
        drawPath(stripePath, acolor)
        drawPath(stripePath, Color(0xFF2C1A0E), style = Stroke(1.2f * s))
    }

    // Left Side Awning Extension
    val sideAwning = Path().apply {
        moveTo(10f * s, 32f * s)
        lineTo(20f * s, 26f * s)
        lineTo(20f * s, 42f * s)
        lineTo(10f * s, 45f * s)
        close()
    }
    drawPath(sideAwning, purpleDark)
    drawPath(sideAwning, Color(0xFF2C1A0E), style = Stroke(1.2f * s))

    // === 7. Front Counter Display Pedestals & Glowing Crystals ===
    // 4 Pedestals on Counter
    listOf(
        Triple(28f, Color(0xFFF1C40F), "AMBER"),
        Triple(43f, Color(0xFF00FF87), "CYAN"),
        Triple(59f, Color(0xFFE74C3C), "PINK"),
        Triple(74f, Color(0xFFA569BD), "AMETHYST")
    ).forEach { (cx, cColor, type) ->
        // Pedestal base dish
        pxRect(cx - 3.5f, 59.5f, 7f, 2f, Color(0xFF422C05))
        drawCircle(Color(0xFF7E5109), radius = 4f * s, center = Offset(cx * s, 60.5f * s))

        // Crystal pulse aura
        val crystalPulse = 0.35f + sin(animTime * 0.004f + cx) * 0.15f
        drawCircle(cColor.copy(alpha = crystalPulse), radius = 5.5f * s, center = Offset(cx * s, 54f * s))

        // Multi-faceted Gem Drawing
        when (type) {
            "AMBER" -> {
                // Amber Pointed Gem
                val gemPath = Path().apply {
                    moveTo(cx * s, 47f * s)
                    lineTo((cx + 3.5f) * s, 53f * s)
                    lineTo(cx * s, 59f * s)
                    lineTo((cx - 3.5f) * s, 53f * s)
                    close()
                }
                drawPath(gemPath, cColor)
                drawPath(gemPath, Color.White.copy(alpha = 0.8f), style = Stroke(1f * s))
            }
            "CYAN" -> {
                // Cyan Crystal Cluster (2 overlapping spires)
                val gemLeft = Path().apply {
                    moveTo((cx - 1.5f) * s, 48f * s)
                    lineTo((cx + 1.5f) * s, 53f * s)
                    lineTo((cx - 1.5f) * s, 59f * s)
                    lineTo((cx - 4.5f) * s, 53f * s)
                    close()
                }
                val gemRight = Path().apply {
                    moveTo((cx + 1.5f) * s, 46f * s)
                    lineTo((cx + 4.5f) * s, 52f * s)
                    lineTo((cx + 1.5f) * s, 59f * s)
                    lineTo((cx - 1.5f) * s, 52f * s)
                    close()
                }
                drawPath(gemLeft, Color(0xFF1ABC9C))
                drawPath(gemRight, Color(0xFF00FF87))
                drawPath(gemRight, Color.White.copy(alpha = 0.8f), style = Stroke(1f * s))
            }
            "PINK" -> {
                // Pink/Magenta Gem
                val gemPath = Path().apply {
                    moveTo(cx * s, 48f * s)
                    lineTo((cx + 3f) * s, 52f * s)
                    lineTo(cx * s, 59f * s)
                    lineTo((cx - 3f) * s, 52f * s)
                    close()
                }
                drawPath(gemPath, Color(0xFFFF69B4))
                drawPath(gemPath, Color.White.copy(alpha = 0.8f), style = Stroke(1f * s))
            }
            else -> {
                // Amethyst Gem
                val gemPath = Path().apply {
                    moveTo(cx * s, 46f * s)
                    lineTo((cx + 3.5f) * s, 51f * s)
                    lineTo(cx * s, 59f * s)
                    lineTo((cx - 3.5f) * s, 51f * s)
                    close()
                }
                drawPath(gemPath, cColor)
                drawPath(gemPath, Color.White.copy(alpha = 0.8f), style = Stroke(1f * s))
            }
        }
    }

    // === 8. Hanging Warm Lanterns (On Front Posts) ===
    listOf(17f, 88f).forEach { lx ->
        pxRect(lx, 34f, 2f, 4f, Color(0xFF1B2631)) // Iron bracket

        // Lantern body
        val lanternGlow = 0.85f + sin(animTime * 0.006f + lx) * 0.15f
        drawCircle(Color(0xFFE67E22).copy(alpha = 0.35f), radius = 4f * s, center = Offset(lx * s, 42f * s))
        pxRect(lx - 1.5f, 38f, 5f, 8f, Color(0xFFD35400))
        drawRect(Color(0xFFF1C40F).copy(alpha = lanternGlow), Offset((lx - 0.5f) * s, 39f * s), Size(3f * s, 6f * s))
    }

    // === 9. Floating Magic Sparkle Particles ===
    repeat(5) { i ->
        val sparklePhase = ((animTime + i * 220f) * 0.0018f) % 1f
        val floatY = 85f - sparklePhase * 60f
        val floatX = 12f + i * 18f + sin(sparklePhase * 6.28f + i) * 4f
        val sparkleColor = when (i % 3) {
            0 -> Color(0xFF00FF87)
            1 -> Color(0xFFA569BD)
            else -> Color(0xFF3498DB)
        }.copy(alpha = (1f - sparklePhase) * 0.85f)

        // Diamond sparkle shape
        val sparklePath = Path().apply {
            moveTo(floatX * s, (floatY - 2.5f) * s)
            lineTo((floatX + 2f) * s, floatY * s)
            lineTo(floatX * s, (floatY + 2.5f) * s)
            lineTo((floatX - 2f) * s, floatY * s)
            close()
        }
        drawPath(sparklePath, sparkleColor)
    }
}

/**
 * 2. TRAINING HALL / BARRACKS (Heavy Stone Fortress Keep Redesign)
 * - Heavy stone slate masonry foundation and walls with iron pillar reinforcements.
 * - Pitched dark steel roof with central crenellated stone watchtower.
 * - Antique golden Lion crest plate mounted above main door.
 * - Arched iron-banded timber double door.
 * - Dual animated flickering wall torches flanking entrance.
 * - Straw Training Dummy on left side with target crosshair & wooden crossbar.
 * - Weapon Rack on right side with steel broadswords, spears & round shield.
 */
fun DrawScope.drawDetailedBarracks(animTime: Float = 0f) {
    val W = size.width
    val s = W / 100f

    // === 1. Heavy Slate Stone Masonry Wall Base ===
    pxRect(15f, 36f, 70f, 58f, Color(0xFF2C3E50)) // Slate stone wall
    pxRect(10f, 92f, 80f, 4f, Color(0xFF1B2631))  // Heavy dark foundation ledge

    // Stone block grid texture
    for (row in 0..3) {
        val y = 42f + row * 12f
        pxRect(15f, y, 70f, 1f, Color(0xFF1B2631))
        for (col in 0..4) {
            val x = 15f + col * 14f + (if (row % 2 == 0) 0f else 7f)
            pxRect(x, y, 1f, 12f, Color(0xFF1B2631))
        }
    }

    // === 2. Iron Pillar Reinforcements & Horizontal Beams ===
    val ironColor = Color(0xFF1A252F)
    val rivetColor = Color(0xFF7F8C8D)

    // Vertical corner pillars
    pxRect(15f, 36f, 5f, 58f, ironColor)
    pxRect(80f, 36f, 5f, 58f, ironColor)
    // Horizontal iron beams
    pxRect(15f, 36f, 70f, 3.5f, ironColor)
    pxRect(15f, 62f, 70f, 3.5f, ironColor)
    pxRect(15f, 90f, 70f, 3.5f, ironColor)

    // Rivets on iron pillars
    listOf(17.5f, 82.5f).forEach { rx ->
        for (ry in 38..88 step 12) {
            drawCircle(rivetColor, radius = 0.8f * s, center = Offset(rx * s, ry.toFloat() * s))
        }
    }

    // === 3. Pitched Dark Steel Roof & Watchtower ===
    // Main dark steel pitched roof
    val roofPath = Path().apply {
        moveTo(5f * s, 38f * s)
        lineTo(50f * s, 8f * s)
        lineTo(95f * s, 38f * s)
        close()
    }
    drawPath(roofPath, Color(0xFF212F3D)) // Steel roof fill
    drawPath(roofPath, Color(0xFF17202A), style = Stroke(3f * s))

    // Roof ridge/eaves trim
    drawLine(Color(0xFF34495E), Offset(5f * s, 38f * s), Offset(50f * s, 8f * s), strokeWidth = 3f * s)
    drawLine(Color(0xFF34495E), Offset(95f * s, 38f * s), Offset(50f * s, 8f * s), strokeWidth = 3f * s)

    // Central Stone Watchtower Battlements (Peak)
    pxRect(38f, 10f, 24f, 16f, Color(0xFF2C3E50)) // Watchtower stone base
    pxRect(36f, 10f, 28f, 2f, Color(0xFF1B2631))  // Ledge molding

    // Crenellations (Merlons) on Watchtower top
    pxRect(37f, 6f, 5f, 4f, Color(0xFF2C3E50))
    pxRect(47f, 6f, 6f, 4f, Color(0xFF2C3E50))
    pxRect(58f, 6f, 5f, 4f, Color(0xFF2C3E50))

    // Watchtower narrow arrow-slit window
    pxRect(48f, 15f, 4f, 8f, Color(0xFF111827))
    val windowGlow = 0.7f + sin(animTime * 0.004f) * 0.2f
    drawRect(Color(0xFFF39C12).copy(alpha = windowGlow), Offset(48.5f * s, 15.5f * s), Size(3f * s, 7f * s))

    // === 4. Arched Fortified Iron Double Doorway (Center) ===
    val doorPath = Path().apply {
        moveTo(42f * s, 94f * s)
        lineTo(42f * s, 70f * s)
        quadraticTo(50f * s, 60f * s, 58f * s, 70f * s)
        lineTo(58f * s, 94f * s)
        close()
    }
    drawPath(doorPath, Color(0xFF3B2312)) // Dark timber door
    drawPath(doorPath, Color(0xFF1B2631), style = Stroke(2.5f * s)) // Iron frame

    // Door center divide line
    drawLine(Color(0xFF1B2631), Offset(50f * s, 63f * s), Offset(50f * s, 94f * s), strokeWidth = 1.5f * s)
    // Horizontal iron straps across door
    pxRect(43f, 72f, 14f, 2f, Color(0xFF1B2631))
    pxRect(43f, 84f, 14f, 2f, Color(0xFF1B2631))
    // Door iron ring handles
    drawCircle(Color(0xFFBDC3C7), radius = 1.8f * s, center = Offset(47f * s, 80f * s), style = Stroke(1f * s))
    drawCircle(Color(0xFFBDC3C7), radius = 1.8f * s, center = Offset(53f * s, 80f * s), style = Stroke(1f * s))

    // === 5. Lion Crest Plate Above Doorway ===
    pxRect(41f, 52f, 18f, 9f, Color(0xFFB7950B)) // Antique gold plate
    drawRect(Color(0xFF7D6608), Offset(41f * s, 52f * s), Size(18f * s, 9f * s), style = Stroke(1.5f * s))

    // Lion Emblem icon inside plate
    drawCircle(Color(0xFF7D6608), radius = 2.5f * s, center = Offset(50f * s, 56.5f * s)) // Mane
    drawCircle(Color(0xFFF1C40F), radius = 1.6f * s, center = Offset(50f * s, 56.5f * s)) // Snout highlight
    pxRect(49f, 54f, 2f, 2f, Color(0xFF7D6608)) // Crown top

    // === 6. Dual Wall Torches (Flanking Door) ===
    listOf(36f, 64f).forEach { tx ->
        pxRect(tx - 1f, 72f, 2f, 6f, Color(0xFF1B2631)) // Sconce bracket
        pxRect(tx - 1.5f, 68f, 3f, 4f, Color(0xFF5D4037)) // Torch handle

        // Animated torch flame
        val firePulse = sin(animTime * 0.01f + tx) * 0.8f
        val flameY = 66f + firePulse * 0.5f

        // Outer fire glow aura
        drawCircle(
            Color(0xFFE67E22).copy(alpha = 0.35f),
            radius = 4.5f * s,
            center = Offset(tx * s, flameY * s)
        )
        // Core orange flame
        drawCircle(
            Color(0xFFD35400),
            radius = 2.5f * s,
            center = Offset(tx * s, flameY * s)
        )
        // Inner bright yellow flame
        drawCircle(
            Color(0xFFF1C40F),
            radius = 1.3f * s,
            center = Offset(tx * s, (flameY - 0.5f) * s)
        )
    }

    // === 7. Straw Training Dummy (Left Side Outside) ===
    // Wooden main post
    pxRect(22f, 72f, 3f, 22f, Color(0xFF5D4037))
    // Wooden base feet
    pxRect(18f, 91f, 11f, 3f, Color(0xFF422C05))

    // Wooden crossbar arms
    pxRect(16f, 75f, 15f, 2.5f, Color(0xFF5D4037))

    // Straw torso body
    pxRect(19f, 73f, 9f, 13f, Color(0xFFD4AC0D))
    // Straw body texture & rope ties
    pxRect(19f, 76f, 9f, 1.2f, Color(0xFF78281F)) // Top rope tie
    pxRect(19f, 82f, 9f, 1.2f, Color(0xFF78281F)) // Bottom rope tie
    // Red target crosshair on dummy chest
    drawLine(Color(0xFFC0392B), Offset(23.5f * s, 77f * s), Offset(23.5f * s, 81f * s), strokeWidth = 1.5f * s)
    drawLine(Color(0xFFC0392B), Offset(21.5f * s, 79f * s), Offset(25.5f * s, 79f * s), strokeWidth = 1.5f * s)

    // Straw head with target cap
    drawCircle(Color(0xFFF1C40F), radius = 2.5f * s, center = Offset(23.5f * s, 70f * s))

    // === 8. Weapon Rack (Right Side Outside) ===
    // Wooden A-frame rack structure
    pxRect(71f, 75f, 2.5f, 19f, Color(0xFF4A3423)) // Left post
    pxRect(83f, 75f, 2.5f, 19f, Color(0xFF4A3423)) // Right post
    pxRect(69f, 83f, 18f, 2.5f, Color(0xFF3E2723)) // Crossbar

    // Broadsword upright
    pxRect(74.5f, 70f, 1.5f, 15f, Color(0xFFBDC3C7)) // Steel blade
    pxRect(73f, 81f, 4.5f, 1.2f, Color(0xFFF1C40F))  // Gold crossguard
    drawCircle(Color(0xFFF1C40F), radius = 1f * s, center = Offset(75.25f * s, 85f * s)) // Pommel

    // Spears leaning upright
    listOf(78f, 81f).forEach { spx ->
        drawLine(Color(0xFF6E2C00), Offset(spx * s, 68f * s), Offset((spx - 1f) * s, 92f * s), strokeWidth = 1.8f * s)
        // Spearhead tip
        val tipPath = Path().apply {
            moveTo(spx * s, 64f * s)
            lineTo((spx + 1.2f) * s, 68f * s)
            lineTo((spx - 1.2f) * s, 68f * s)
            close()
        }
        drawPath(tipPath, Color(0xFFECF0F1))
    }

    // Wooden Round Shield Leaning against Rack
    drawCircle(Color(0xFF7E5109), radius = 5.5f * s, center = Offset(84f * s, 88f * s)) // Shield wood body
    drawCircle(Color(0xFF2C3E50), radius = 5.5f * s, center = Offset(84f * s, 88f * s), style = Stroke(1.5f * s)) // Iron rim
    drawCircle(Color(0xFFBDC3C7), radius = 1.8f * s, center = Offset(84f * s, 88f * s)) // Center iron boss
}

/**
 * 3. RELICS SANCTUARY (FFVI Green Magicite Inspired Arcane Temple)
 * - Tall wizard tower with a glowing astral orb / planetarium dome at the peak.
 * - Multi-faceted levitating green Magicite crystal at the roof peak with glowing emerald aura and floating animation.
 * - Smaller green Magicite crystals orbiting the structure.
 * - Swirling green/emerald magicite portal in the doorway.
 * - Mystical green & gold runic banners and rising magic spark particles.
 */
fun DrawScope.drawDetailedPortal(animTime: Float = 0f) {
    val W = size.width
    val s = W / 100f

    // === 1. Sanctuary Base & Central Wizard Tower ===
    // Flanking lower sanctuary structure
    pxRect(12f, 48f, 76f, 44f, Color(0xFF1F2937)) // Dark slate lower walls
    pxRect(8f, 92f, 84f, 4f, Color(0xFF111827))   // Foundation ledge

    // Central Tall Wizard Tower
    pxRect(28f, 26f, 44f, 66f, Color(0xFF16202C)) // Darker slate central tower
    pxRect(25f, 24f, 50f, 3f, Color(0xFF0F172A))  // Tower cornice / molding line

    // Stone block texture with glowing emerald rune lines
    val runeGlow = 0.6f + sin(animTime * 0.003f) * 0.25f
    val emeraldLine = Color(0xFF00FF87).copy(alpha = runeGlow)

    // Base stone bricks
    for (row in 0..2) {
        val y = 52f + row * 12f
        pxRect(12f, y, 76f, 1f, Color(0xFF111827))
        for (col in 0..5) {
            val x = 12f + col * 13f + (if (row % 2 == 0) 0f else 6.5f)
            pxRect(x, y, 1f, 12f, Color(0xFF111827))
        }
    }

    // Tower stone bricks
    for (row in 0..2) {
        val y = 28f + row * 8f
        pxRect(28f, y, 44f, 1f, Color(0xFF0F172A))
    }

    // Glowing runic engravings across walls
    pxRect(15f, 56f, 10f, 1.5f, emeraldLine)
    pxRect(75f, 56f, 10f, 1.5f, emeraldLine)
    pxRect(15f, 74f, 10f, 1.5f, emeraldLine)
    pxRect(75f, 74f, 10f, 1.5f, emeraldLine)
    pxRect(32f, 34f, 8f, 1.5f, emeraldLine)
    pxRect(60f, 34f, 8f, 1.5f, emeraldLine)

    // === 2. Astral Orb / Planetarium Dome at Tower Peak ===
    // Dome pedestal support
    pxRect(34f, 22f, 32f, 3f, Color(0xFF2C3E50))

    // Planetarium Glass Dome
    val domePath = Path().apply {
        moveTo(34f * s, 22f * s)
        quadraticTo(50f * s, 8f * s, 66f * s, 22f * s)
        close()
    }
    drawPath(domePath, Color(0xFF1ABC9C).copy(alpha = 0.35f)) // Translucent glass fill
    drawPath(domePath, Color(0xFF00FF87), style = Stroke(2f * s)) // Emerald dome rim

    // Astral Orb Planetarium Latitude/Longitude Rings inside Dome
    val ringGlow = 0.7f + sin(animTime * 0.004f) * 0.3f
    drawCircle(
        Color(0xFFF1C40F).copy(alpha = ringGlow),
        radius = 5f * s,
        center = Offset(50f * s, 16f * s)
    ) // Glowing celestial core star

    // Armillary/Astral Rings
    val ringPath1 = Path().apply {
        moveTo(38f * s, 18f * s)
        quadraticTo(50f * s, 12f * s, 62f * s, 18f * s)
    }
    val ringPath2 = Path().apply {
        moveTo(42f * s, 21f * s)
        quadraticTo(50f * s, 24f * s, 58f * s, 21f * s)
    }
    drawPath(ringPath1, Color(0xFFF39C12).copy(alpha = 0.8f), style = Stroke(1.5f * s))
    drawPath(ringPath2, Color(0xFF00FF87).copy(alpha = 0.8f), style = Stroke(1.5f * s))

    // === 3. Main FFVI Floating Green Magicite Crystal (Levitating Above Peak) ===
    val crystalFloat = sin(animTime * 0.003f) * 2.5f
    val crystalY = 7f + crystalFloat

    // Emerald aura glow behind main crystal
    val auraPulse = 0.35f + sin(animTime * 0.005f) * 0.15f
    drawCircle(
        color = Color(0xFF00FF87).copy(alpha = auraPulse),
        radius = 11f * s,
        center = Offset(50f * s, crystalY * s)
    )

    // Multi-faceted Gem drawing for Magicite Crystal
    val crystalFrontLeft = Path().apply {
        moveTo(50f * s, (crystalY - 8f) * s)  // Top tip
        lineTo(43f * s, crystalY * s)          // Mid left
        lineTo(50f * s, (crystalY + 2f) * s)   // Center front
        close()
    }
    val crystalFrontRight = Path().apply {
        moveTo(50f * s, (crystalY - 8f) * s)  // Top tip
        lineTo(57f * s, crystalY * s)          // Mid right
        lineTo(50f * s, (crystalY + 2f) * s)   // Center front
        close()
    }
    val crystalBottomLeft = Path().apply {
        moveTo(43f * s, crystalY * s)
        lineTo(50f * s, (crystalY + 10f) * s)  // Bottom tip
        lineTo(50f * s, (crystalY + 2f) * s)
        close()
    }
    val crystalBottomRight = Path().apply {
        moveTo(57f * s, crystalY * s)
        lineTo(50f * s, (crystalY + 10f) * s)  // Bottom tip
        lineTo(50f * s, (crystalY + 2f) * s)
        close()
    }

    drawPath(crystalFrontLeft, Color(0xFFA2F9C4))   // Bright lime highlight
    drawPath(crystalFrontRight, Color(0xFF00FF87))  // Bright emerald
    drawPath(crystalBottomLeft, Color(0xFF2ECC71))  // Deep green
    drawPath(crystalBottomRight, Color(0xFF117A65)) // Dark emerald shadow

    // Inner facet highlights
    drawLine(Color.White.copy(alpha = 0.85f), Offset(50f * s, (crystalY - 8f) * s), Offset(50f * s, (crystalY + 10f) * s), strokeWidth = 1.2f * s)

    // === 4. Orbiting Smaller Magicite Crystals ===
    val orbAngle = animTime * 0.002f
    val orb1X = 50f + kotlin.math.cos(orbAngle) * 25f
    val orb1Y = 12f + sin(orbAngle) * 6f
    val orb2X = 50f - kotlin.math.cos(orbAngle) * 25f
    val orb2Y = 12f - sin(orbAngle) * 6f

    // Draw small crystal 1
    val orb1Path = Path().apply {
        moveTo(orb1X * s, (orb1Y - 3.5f) * s)
        lineTo((orb1X + 2.5f) * s, orb1Y * s)
        lineTo(orb1X * s, (orb1Y + 4f) * s)
        lineTo((orb1X - 2.5f) * s, orb1Y * s)
        close()
    }
    drawPath(orb1Path, Color(0xFF00FF87))
    drawCircle(Color(0xFF00FF87).copy(alpha = 0.4f), radius = 3.5f * s, center = Offset(orb1X * s, orb1Y * s))

    // Draw small crystal 2
    val orb2Path = Path().apply {
        moveTo(orb2X * s, (orb2Y - 3.5f) * s)
        lineTo((orb2X + 2.5f) * s, orb2Y * s)
        lineTo(orb2X * s, (orb2Y + 4f) * s)
        lineTo((orb2X - 2.5f) * s, orb2Y * s)
        close()
    }
    drawPath(orb2Path, Color(0xFFA2F9C4))
    drawCircle(Color(0xFF00FF87).copy(alpha = 0.4f), radius = 3.5f * s, center = Offset(orb2X * s, orb2Y * s))

    // === 5. Mystical Runic Banners (Flanks) ===
    listOf(17f, 73f).forEach { bx ->
        pxRect(bx, 52f, 10f, 26f, Color(0xFF0E6655)) // Deep green banner
        pxRect(bx, 52f, 10f, 2f, Color(0xFFF1C40F))  // Gold top bar
        pxRect(bx, 78f, 10f, 2f, Color(0xFFF1C40F))  // Gold bottom fringe
        drawCircle(Color(0xFF00FF87), radius = 2f * s, center = Offset((bx + 5f) * s, 65f * s)) // Rune crest
    }

    // === 6. Swirling Green Magicite Portal Doorway (Center Base) ===
    val doorPath = Path().apply {
        moveTo(38f * s, 95f * s)
        lineTo(38f * s, 64f * s)
        quadraticTo(50f * s, 52f * s, 62f * s, 64f * s)
        lineTo(62f * s, 95f * s)
        close()
    }
    drawPath(doorPath, Color(0xFF051C14)) // Dark portal doorway interior
    drawPath(doorPath, Color(0xFF00FF87), style = Stroke(2.5f * s)) // Emerald portal frame

    // Swirling portal energy rings inside doorway
    val portalPulse = 0.6f + sin(animTime * 0.006f) * 0.3f
    drawCircle(
        Color(0xFF00FF87).copy(alpha = portalPulse),
        radius = 8f * s,
        center = Offset(50f * s, 74f * s)
    )
    drawCircle(
        Color(0xFFA2F9C4).copy(alpha = portalPulse * 0.8f),
        radius = 4f * s,
        center = Offset(50f * s, 74f * s)
    )

    // === 7. Animated Rising Spark Particles ===
    repeat(5) { i ->
        val sparkPhase = ((animTime + i * 200f) * 0.0015f) % 1f
        val sparkY = 90f - sparkPhase * 65f
        val sparkX = 32f + (i * 9f) + sin(sparkPhase * 6.28f + i) * 3f
        val sparkAlpha = (1f - sparkPhase) * 0.85f
        drawCircle(
            Color(0xFFA2F9C4).copy(alpha = sparkAlpha),
            radius = 1.5f * s,
            center = Offset(sparkX * s, sparkY * s)
        )
    }
}

/**
 * 4. THE INN (Grand Tavern & Lodge with attached Bulletin Board)
 * - Mix of stone masonry foundation base (from Guild manor) & warm timber upper story.
 * - Steep dark red shingled roof with left stone chimney emitting animated smoke.
 * - Arched dark wooden door with gold trim, glowing warm lattice windows.
 * - Hanging swinging wooden signboard ("INN 🍺").
 * - Attached wooden BULLETIN board with pinned quest parchment notes on right side.
 */
fun DrawScope.drawDetailedInn(animTime: Float = 0f) {
    val W = size.width
    val s = W / 100f

    // === 1. Stone Foundation & Masonry Lower Floor ===
    pxRect(10f, 55f, 80f, 40f, Color(0xFF5D6D7E)) // Slate stone main wall
    pxRect(8f, 92f, 84f, 4f, Color(0xFF34495E))   // Foundation ledge

    // Stone blocks grid texture on lower floor
    for (row in 0..2) {
        val y = 60f + row * 10f
        pxRect(10f, y, 80f, 1f, Color(0xFF34495E))
        for (col in 0..6) {
            val x = 10f + col * 12f + (if (row % 2 == 0) 0f else 6f)
            pxRect(x, y, 1f, 10f, Color(0xFF34495E))
        }
    }

    // === 2. Upper Story (Warm Timber & Planks) ===
    pxRect(12f, 26f, 76f, 30f, Color(0xFF6D4C41)) // Warm wood timber
    // Horizontal wood plank grooves
    for (y in 32..52 step 5) {
        pxRect(12f, y.toFloat(), 76f, 1f, Color(0xFF3E2723))
    }
    // Floor-dividing dark wooden beam
    pxRect(10f, 54f, 80f, 3f, Color(0xFF3E2723))

    // === 3. Steep Dark Red Roof ===
    val roofPath = Path().apply {
        moveTo(2f * s, 28f * s)
        lineTo(50f * s, 2f * s)
        lineTo(98f * s, 28f * s)
        close()
    }
    drawPath(roofPath, Color(0xFF900C3F)) // Dark red shingles
    drawPath(roofPath, Color(0xFF581845), style = Stroke(3f * s))

    // Inner roof eaves trim
    val roofTrim = Path().apply {
        moveTo(6f * s, 28f * s)
        lineTo(50f * s, 5f * s)
        lineTo(94f * s, 28f * s)
    }
    drawPath(roofTrim, Color(0xFFC0392B), style = Stroke(2f * s))

    // === 4. Stone Chimney & Animated Smoke (Left side) ===
    pxRect(18f, 6f, 10f, 18f, Color(0xFF5D6D7E))
    pxRect(16f, 4f, 14f, 3f, Color(0xFF34495E))

    val smokePhase = ((animTime + 500f) * 0.002f) % 1f
    repeat(3) { i ->
        val progress = (smokePhase + i * 0.33f) % 1f
        val smokeY = 3f - progress * 22f
        val smokeX = 23f + sin(progress * 6.28f + i) * 3.5f
        val smokeRadius = (2.5f + progress * 5f) * s
        val smokeAlpha = (1f - progress) * 0.5f
        drawCircle(
            Color.White.copy(alpha = smokeAlpha),
            radius = smokeRadius,
            center = Offset(smokeX * s, smokeY * s)
        )
    }

    // === 5. Swinging Wooden Signboard ("INN 🍺") ===
    pxRect(78f, 30f, 16f, 13f, Color(0xFF5D4037)) // Signboard
    drawRect(Color(0xFF3E2723), Offset(78f * s, 30f * s), Size(16f * s, 13f * s), style = Stroke(1.5f * s))
    drawLine(Color(0xFF2C3E50), Offset(74f * s, 30f * s), Offset(78f * s, 30f * s), strokeWidth = 2f * s) // Bracket
    drawCircle(Color(0xFFF1C40F), radius = 2.5f * s, center = Offset(86f * s, 36.5f * s)) // Mug glow symbol

    // === 6. Upper Lattice Windows with Warm Glow ===
    val candleFlicker = 0.85f + sin(animTime * 0.005f + 1f) * 0.15f
    val glow = Color(0xFFF4D03F).copy(alpha = candleFlicker)

    listOf(22f, 52f).forEach { wx ->
        pxRect(wx, 33f, 13f, 13f, glow)
        drawRect(Color(0xFF3E2723), Offset(wx * s, 33f * s), Size(13f * s, 13f * s), style = Stroke(1.5f * s))
        // Cross panes
        drawLine(Color(0xFF3E2723), Offset((wx + 6.5f) * s, 33f * s), Offset((wx + 6.5f) * s, 46f * s), strokeWidth = 1.5f * s)
        drawLine(Color(0xFF3E2723), Offset(wx * s, 39.5f * s), Offset((wx + 13f) * s, 39.5f * s), strokeWidth = 1.5f * s)
    }

    // === 7. Arched Entrance Doorway (Center-Left) ===
    val doorPath = Path().apply {
        moveTo(36f * s, 95f * s)
        lineTo(36f * s, 68f * s)
        quadraticTo(44f * s, 58f * s, 52f * s, 68f * s)
        lineTo(52f * s, 95f * s)
        close()
    }
    drawPath(doorPath, Color(0xFF3E2723)) // Dark wood door
    drawPath(doorPath, Color(0xFFF39C12), style = Stroke(2f * s))
    drawCircle(Color(0xFFF1C40F), radius = 2.5f * s, center = Offset(49f * s, 78f * s))

    // === 8. Wooden BULLETIN Board (Right side next to door) ===
    pxRect(62f, 63f, 24f, 18f, Color(0xFF7E5109)) // Wooden board background
    drawRect(Color(0xFF422C05), Offset(62f * s, 63f * s), Size(24f * s, 18f * s), style = Stroke(1.5f * s))
    pxRect(66f, 81f, 3f, 11f, Color(0xFF422C05)) // Support post left
    pxRect(79f, 81f, 3f, 11f, Color(0xFF422C05)) // Support post right

    // Header "BULLETIN" text plate
    pxRect(64f, 61f, 20f, 4f, Color(0xFFD35400))

    // Pinned quest papers (white parchment squares)
    pxRect(65f, 67f, 5f, 6f, Color(0xFFF5EEF8))
    pxRect(72f, 67f, 5f, 6f, Color(0xFFF5EEF8))
    pxRect(79f, 67f, 5f, 6f, Color(0xFFF5EEF8))
    pxRect(68f, 74f, 6f, 5f, Color(0xFFF5EEF8))
    pxRect(76f, 74f, 6f, 5f, Color(0xFFF5EEF8))
}

/**
 * 5. FORTIFIED GLADIATOR ARENA / COLOSSEUM (Hall of Fame & Leaderboards)
 * - Multi-tiered curved slate arena walls with stone arcade arches and iron reinforcement beams.
 * - Central grand entrance archway with dark arena tunnel & spiked iron portcullis gate.
 * - Dual flaming braziers on stone pedestals flanking the entrance.
 * - Crimson & gold warrior banners hanging on arena pillars.
 * - Mounted golden trophy & laurel wreath crest plate above the entrance arch.
 */
fun DrawScope.drawDetailedColosseum(animTime: Float = 0f) {
    val W = size.width
    val s = W / 100f

    // === 1. Multi-Tiered Stone Arena Foundation & Base Wall ===
    pxRect(10f, 38f, 80f, 56f, Color(0xFF2C3E50)) // Slate stone main arena body
    pxRect(6f, 92f, 88f, 4f, Color(0xFF1B2631))   // Heavy stone foundation ledge

    // Stone block horizontal dividing cornices
    pxRect(10f, 38f, 80f, 3f, Color(0xFF1B2631))
    pxRect(10f, 58f, 80f, 3f, Color(0xFF1B2631))

    // === 2. Upper Tier Arcade Arches ===
    listOf(16f, 32f, 48f, 64f, 80f).forEach { ax ->
        // Arch cutout
        val archPath = Path().apply {
            moveTo(ax * s, 58f * s)
            lineTo(ax * s, 46f * s)
            quadraticTo((ax + 4f) * s, 42f * s, (ax + 8f) * s, 46f * s)
            lineTo((ax + 8f) * s, 58f * s)
            close()
        }
        drawPath(archPath, Color(0xFF111827)) // Dark arch opening
        drawPath(archPath, Color(0xFF1B2631), style = Stroke(1.5f * s))

        // Torches inside upper arches
        drawCircle(Color(0xFFF39C12).copy(alpha = 0.5f), radius = 2f * s, center = Offset((ax + 4f) * s, 50f * s))
    }

    // === 3. Iron Pillar Reinforcements & Rivets ===
    listOf(12f, 26f, 74f, 88f).forEach { px ->
        pxRect(px, 38f, 4f, 56f, Color(0xFF1A252F))
        for (ry in 42..88 step 12) {
            drawCircle(Color(0xFF7F8C8D), radius = 0.8f * s, center = Offset((px + 2f) * s, ry.toFloat() * s))
        }
    }

    // === 4. Crimson & Gold Warrior Banners (Flanking Pillars) ===
    listOf(18f, 76f).forEach { bx ->
        pxRect(bx, 48f, 8f, 22f, Color(0xFF78281F)) // Crimson banner
        pxRect(bx, 48f, 8f, 2f, Color(0xFFF1C40F))  // Gold top bar
        pxRect(bx, 70f, 8f, 2f, Color(0xFFF1C40F))  // Gold bottom fringe
        // Crossed swords icon on banner
        drawLine(Color(0xFFF1C40F), Offset((bx + 2f) * s, 54f * s), Offset((bx + 6f) * s, 64f * s), strokeWidth = 1.2f * s)
        drawLine(Color(0xFFF1C40F), Offset((bx + 6f) * s, 54f * s), Offset((bx + 2f) * s, 64f * s), strokeWidth = 1.2f * s)
    }

    // === 5. Central Grand Archway Entrance & Spiked Portcullis ===
    val mainArch = Path().apply {
        moveTo(34f * s, 94f * s)
        lineTo(34f * s, 68f * s)
        quadraticTo(50f * s, 54f * s, 66f * s, 68f * s)
        lineTo(66f * s, 94f * s)
        close()
    }
    drawPath(mainArch, Color(0xFF0F172A)) // Dark arena tunnel interior
    drawPath(mainArch, Color(0xFF1B2631), style = Stroke(3f * s)) // Heavy arch frame

    // Spiked Portcullis Iron Bars
    for (gx in 38..62 step 6) {
        drawLine(Color(0xFF5D6D7E), Offset(gx.toFloat() * s, 62f * s), Offset(gx.toFloat() * s, 82f * s), strokeWidth = 1.8f * s)
        // Spike tip
        val spike = Path().apply {
            moveTo(gx.toFloat() * s, 82f * s)
            lineTo((gx.toFloat() - 1.2f) * s, 86f * s)
            lineTo((gx.toFloat() + 1.2f) * s, 86f * s)
            close()
        }
        drawPath(spike, Color(0xFFBDC3C7))
    }

    // === 6. Mounted Golden Trophy & Laurel Crest Plate ===
    pxRect(40f, 44f, 20f, 9f, Color(0xFFB7950B)) // Gold plate
    drawRect(Color(0xFF7D6608), Offset(40f * s, 44f * s), Size(20f * s, 9f * s), style = Stroke(1.5f * s))

    // Trophy Icon
    pxRect(48f, 46f, 4f, 4f, Color(0xFFF1C40F)) // Cup
    pxRect(49f, 50f, 2f, 2f, Color(0xFFF1C40F)) // Stem
    pxRect(47f, 52f, 6f, 1f, Color(0xFFF1C40F)) // Pedestal

    // === 7. Dual Flaming Stone Braziers (Flanking Arch) ===
    listOf(27f, 73f).forEach { bx ->
        // Stone pedestal
        pxRect(bx - 3f, 80f, 6f, 14f, Color(0xFF34495E))
        pxRect(bx - 4f, 78f, 8f, 2.5f, Color(0xFF1B2631)) // Bowl rim

        // Animated Fire Flame
        val firePulse = sin(animTime * 0.01f + bx) * 0.8f
        val flameY = 73f + firePulse * 0.5f

        // Outer fire aura
        drawCircle(Color(0xFFE67E22).copy(alpha = 0.4f), radius = 5.5f * s, center = Offset(bx * s, flameY * s))
        // Core orange flame
        drawCircle(Color(0xFFD35400), radius = 3.5f * s, center = Offset(bx * s, flameY * s))
        // Inner bright yellow core
        drawCircle(Color(0xFFF1C40F), radius = 2f * s, center = Offset(bx * s, (flameY - 0.8f) * s))
    }
}
