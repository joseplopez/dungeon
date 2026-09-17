package com.game.dungeon.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.models.PetType
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-detail pixel drawing helper.
 * Uses a 32x32 grid for finer details and classic FF-style shading.
 */
private fun DrawScope.px32(): (Float, Float, Float, Float, Color) -> Unit {
    val pixelSize = size.width / 32f
    return { x, y, w, h, color ->
        drawRect(
            color = color,
            topLeft = Offset(x * pixelSize, y * pixelSize),
            size = Size(w * pixelSize, h * pixelSize)
        )
    }
}

// --- ICONIC FF1 COLOR PALETTE ---
private val SkinLight = Color(0xFFFFDBAC)
private val SkinMid = Color(0xFFF1C27D)
private val SkinShadow = Color(0xFF8D5524)

private val HairBrown = Color(0xFF634721)
private val HairBlonde = Color(0xFFF1C40F)
private val HairBlue = Color(0xFF3498DB)
private val HairWhite = Color(0xFFECF0F1)

private val FFRed = Color(0xFFE74C3C)
private val FFRedDark = Color(0xFF922B21)
private val FFBlue = Color(0xFF3498DB)
private val FFBlueDark = Color(0xFF21618C)
private val FFGreen = Color(0xFF27AE60)
private val FFGreenDark = Color(0xFF196F3D)
private val FFPurple = Color(0xFF8E44AD)
private val FFGold = Color(0xFFD4AC0D)
private val FFSilver = Color(0xFFBDC3C7)
private val FFSilverDark = Color(0xFF7F8C8D)
private val FFBlack = Color(0xFF17202A)
private val FFOrange = Color(0xFFE67E22)

// --- HERO SPRITES (TIER 1) ---

fun DrawScope.drawWarrior() {
    val p = px32()
    // Helmet
    p(10f, 2f, 12f, 8f, FFRed)
    p(12f, 1f, 8f, 1f, FFRedDark) // crest
    p(16f, 0f, 2f, 2f, Color.White) // plume
    p(10f, 6f, 12f, 1f, FFBlack) // visor slit
    // Face
    p(11f, 8f, 10f, 5f, SkinMid)
    p(12f, 9f, 1f, 1f, Color.Black)
    p(18f, 9f, 1f, 1f, Color.Black)
    // Body Armor
    p(10f, 13f, 12f, 10f, FFRed)
    p(9f, 14f, 2f, 5f, FFSilver) // Pauldrons
    p(21f, 14f, 2f, 5f, FFSilver)
    p(14f, 13f, 4f, 10f, FFRedDark) // Depth
    // Shield
    p(6f, 15f, 4f, 8f, FFSilver)
    p(7f, 16f, 2f, 6f, FFBlue)
    // Legs
    p(11f, 23f, 4f, 6f, FFRedDark)
    p(17f, 23f, 4f, 6f, FFRedDark)
    p(10f, 28f, 5f, 2f, FFBlack) // Boots
    p(17f, 28f, 5f, 2f, FFBlack)
}

fun DrawScope.drawThief() {
    val p = px32()
    // Head / Bandanna
    p(10f, 3f, 12f, 5f, FFGreen)
    p(11f, 2f, 10f, 1f, FFGreenDark)
    p(21f, 4f, 2f, 2f, HairBlue) // hair tuft
    // Face
    p(11f, 8f, 10f, 6f, SkinLight)
    p(12f, 10f, 1f, 1f, Color.Black)
    p(18f, 10f, 1f, 1f, Color.Black)
    // Body / Tunic
    p(11f, 14f, 10f, 9f, FFGreen)
    p(12f, 14f, 1f, 9f, FFGreenDark) // strap
    p(18f, 14f, 1f, 9f, FFGreenDark)
    // Arms/Daggers
    p(8f, 15f, 3f, 2f, SkinLight)
    p(7f, 14f, 1f, 6f, FFSilver) // dagger
    // Legs
    p(12f, 23f, 3f, 7f, FFGreenDark)
    p(17f, 23f, 3f, 7f, FFGreenDark)
    p(11f, 29f, 4f, 2f, Color(0xFF5D4037))
    p(17f, 29f, 4f, 2f, Color(0xFF5D4037))
}

fun DrawScope.drawMonk() {
    val p = px32()
    // Hair & Headband
    p(11f, 2f, 10f, 6f, HairBrown)
    p(10f, 6f, 12f, 2f, FFGold) // gold headband
    // Face
    p(11f, 8f, 10f, 6f, SkinMid)
    p(12f, 10f, 1f, 1f, Color.Black)
    p(18f, 10f, 1f, 1f, Color.Black)
    // Gi
    p(11f, 14f, 10f, 10f, Color(0xFFE67E22))
    p(14f, 14f, 4f, 10f, Color(0xFFD35400)) // shading
    p(11f, 20f, 10f, 1f, FFBlack) // belt
    // Bare Arms
    p(9f, 15f, 2f, 6f, SkinMid)
    p(21f, 15f, 2f, 6f, SkinMid)
    // Legs
    p(12f, 24f, 3f, 6f, Color(0xFFD35400))
    p(17f, 24f, 3f, 6f, Color(0xFFD35400))
}

fun DrawScope.drawWhiteMage() {
    val p = px32()
    // Hood
    p(10f, 2f, 12f, 12f, Color.White)
    p(11f, 2f, 10f, 1f, FFRed) // top trim
    p(10f, 5f, 1f, 4f, FFRed) // side trim
    p(21f, 5f, 1f, 4f, FFRed)
    // Face in Shadow
    p(12f, 6f, 8f, 6f, SkinLight)
    p(13f, 8f, 1f, 1f, Color.Black)
    p(18f, 8f, 1f, 1f, Color.Black)
    // Robes
    p(10f, 14f, 12f, 10f, Color.White)
    p(10f, 20f, 12f, 2f, FFRed) // bottom trim
    // Staff
    p(7f, 10f, 1f, 18f, HairBrown)
    p(6f, 8f, 3f, 3f, FFGold)
    // Legs
    p(12f, 24f, 8f, 6f, FFSilver)
}

fun DrawScope.drawBlackMage() {
    val p = px32()
    // Tall Hat
    p(13f, 0f, 6f, 8f, FFGold)
    p(10f, 8f, 12f, 2f, FFGold) // brim
    // Shadow Face
    p(11f, 10f, 10f, 6f, FFBlack)
    p(13f, 12f, 2f, 1f, Color.Yellow) // glowing eyes
    p(17f, 12f, 2f, 1f, Color.Yellow)
    // Blue Robes
    p(10f, 16f, 12f, 10f, FFBlue)
    p(14f, 16f, 4f, 10f, FFBlueDark) // shading
    // Staff
    p(22f, 10f, 1f, 18f, HairBrown)
    p(21f, 8f, 3f, 3f, Color.Blue) // orb
    // Legs
    p(12f, 26f, 8f, 4f, FFBlack)
}

fun DrawScope.drawRedMage() {
    val p = px32()
    // Wide Hat
    p(8f, 4f, 16f, 3f, FFRed)
    p(13f, 1f, 6f, 4f, FFRed)
    p(19f, 2f, 2f, 3f, Color.White) // feather
    // Face
    p(12f, 7f, 8f, 7f, SkinMid)
    p(13f, 9f, 1f, 1f, Color.Black)
    p(18f, 9f, 1f, 1f, Color.Black)
    // Cape & Tunic
    p(10f, 14f, 12f, 10f, FFRed)
    p(12f, 14f, 8f, 2f, Color.White) // collar
    p(14f, 16f, 4f, 8f, FFRedDark)
    // Rapier
    p(23f, 12f, 1f, 14f, FFSilver)
    p(22f, 24f, 3f, 2f, FFGold)
    // Legs
    p(12f, 24f, 3f, 6f, FFBlack)
    p(17f, 24f, 3f, 6f, FFBlack)
}

// --- ADVANCED JOBS (TIER 2) ---

fun DrawScope.drawKnight() {
    val p = px32()
    // Full Armor Silver/Blue
    p(10f, 2f, 12f, 10f, FFSilver)
    p(10f, 7f, 12f, 1f, FFBlack) // visor
    p(15f, 1f, 2f, 3f, FFBlue) // plume
    // Body
    p(10f, 12f, 12f, 12f, FFSilver)
    p(11f, 13f, 4f, 10f, FFBlue) // chest plate
    p(17f, 13f, 4f, 10f, FFSilverDark) // shading
    // Pauldrons
    p(8f, 12f, 3f, 6f, FFSilver)
    p(21f, 12f, 3f, 6f, FFSilver)
    // Legs
    p(11f, 24f, 4f, 6f, FFSilverDark)
    p(17f, 24f, 4f, 6f, FFSilverDark)
}

fun DrawScope.drawPaladin() {
    val p = px32()
    // Golden Shine
    p(10f, 2f, 12f, 8f, FFGold)
    p(11f, 4f, 10f, 10f, SkinMid) // Face peeking
    p(11f, 2f, 10f, 3f, HairWhite) // Long white hair
    p(13f, 7f, 1f, 1f, FFBlue)
    p(18f, 7f, 1f, 1f, FFBlue)
    // Armor
    p(10f, 13f, 12f, 11f, Color.White) // White tabard
    p(10f, 13f, 2f, 11f, FFGold) // Gold trim
    p(20f, 13f, 2f, 11f, FFGold)
    p(14f, 15f, 4f, 4f, FFGold) // Cross
    // Legs
    p(11f, 24f, 4f, 6f, FFGold)
    p(17f, 24f, 4f, 6f, FFGold)
}

fun DrawScope.drawNinja() {
    val p = px32()
    // Black Mask
    p(11f, 4f, 10f, 8f, FFBlack)
    p(12f, 6f, 8f, 2f, SkinShadow) // eyes area
    p(13f, 6f, 1f, 1f, Color.Red)
    p(18f, 6f, 1f, 1f, Color.Red)
    // Scarf
    p(9f, 10f, 14f, 2f, FFRed)
    p(23f, 8f, 4f, 2f, FFRed) // blowing tail
    // Gi
    p(11f, 12f, 10f, 11f, FFBlack)
    p(11f, 13f, 1f, 10f, FFGreenDark) // straps
    p(20f, 13f, 1f, 10f, FFGreenDark)
    // Katanas
    p(8f, 12f, 1f, 10f, FFSilver)
    p(23f, 12f, 1f, 10f, FFSilver)
    // Legs
    p(12f, 23f, 3f, 7f, FFBlack)
    p(17f, 23f, 3f, 7f, FFBlack)
}

fun DrawScope.drawDragoon() {
    val p = px32()
    // Dragon Helm
    p(10f, 1f, 12f, 10f, FFPurple)
    p(15f, 0f, 2f, 4f, FFGold) // spike
    p(9f, 4f, 2f, 4f, FFPurple) // wing ear
    p(21f, 4f, 2f, 4f, FFPurple)
    p(10f, 7f, 12f, 1f, FFBlack) // visor
    // Scale Armor
    p(10f, 11f, 12f, 12f, FFPurple)
    p(12f, 13f, 8f, 8f, FFBlack.copy(alpha = 0.3f)) // scales
    // Lance
    p(24f, 2f, 1f, 24f, FFSilver)
    p(23f, 2f, 3f, 4f, FFBlue) // tip
    // Legs
    p(11f, 23f, 4f, 7f, FFPurple)
    p(17f, 23f, 4f, 7f, FFPurple)
}

fun DrawScope.drawBard() {
    val p = px32()
    // Pink Feathered Cap
    p(11f, 3f, 10f, 4f, Color(0xFFF06292))
    p(19f, 1f, 2f, 3f, Color.White)
    // Blonde Hair
    p(10f, 7f, 12f, 4f, HairBlonde)
    // Face
    p(11f, 9f, 10f, 5f, SkinLight)
    p(13f, 11f, 1f, 1f, Color.Black)
    p(18f, 11f, 1f, 1f, Color.Black)
    // Tunic
    p(11f, 14f, 10f, 10f, Color(0xFFF06292))
    p(10f, 15f, 12f, 2f, Color.White.copy(alpha = 0.4f)) // frills
    // Harp
    p(7f, 16f, 6f, 6f, FFGold)
    p(8f, 17f, 4f, 4f, Color.White.copy(alpha = 0.2f))
    // Legs
    p(12f, 24f, 3f, 6f, FFGreen)
    p(17f, 24f, 3f, 6f, FFGreen)
}

fun DrawScope.drawSummoner() {
    val p = px32()
    // Green Robe & Horn
    p(11f, 3f, 10f, 10f, FFGreen)
    p(15f, 1f, 2f, 4f, Color.White) // Horn
    // Face
    p(12f, 7f, 8f, 6f, SkinMid)
    p(13f, 9f, 1f, 1f, Color.Black)
    p(18f, 9f, 1f, 1f, Color.Black)
    // Robes
    p(10f, 13f, 12f, 12f, FFGreen)
    p(14f, 13f, 4f, 12f, Color.White) // inner
    p(14f, 18f, 4f, 2f, FFGold) // belt
    // Legs
    p(12f, 25f, 8f, 5f, FFBlack)
}

fun DrawScope.drawSamurai() {
    val p = px32()
    // Kabuto
    p(9f, 3f, 14f, 8f, FFBlack)
    p(15f, 2f, 2f, 2f, FFGold) // ornament
    p(8f, 6f, 2f, 6f, FFRed) // side flaps
    p(22f, 6f, 2f, 6f, FFRed)
    // Face
    p(11f, 10f, 10f, 4f, SkinMid)
    // Armor
    p(10f, 12f, 12f, 12f, FFBlack)
    p(10f, 13f, 12f, 2f, FFRed) // lacing
    p(10f, 17f, 12f, 2f, FFRed)
    // Katana
    p(7f, 14f, 1f, 10f, FFSilver)
    p(6f, 24f, 3f, 1f, FFBlack)
    // Legs
    p(11f, 24f, 4f, 6f, FFBlack)
    p(17f, 24f, 4f, 6f, FFBlack)
}

fun DrawScope.drawFreelancer() {
    val p = px32()
    // Brown Hair
    p(12f, 4f, 8f, 5f, HairBrown)
    // Face
    p(12f, 9f, 8f, 5f, SkinMid)
    p(13f, 11f, 1f, 1f, Color.Black)
    p(18f, 11f, 1f, 1f, Color.Black)
    // Grey Tunic
    p(11f, 14f, 10f, 9f, Color.Gray)
    p(14f, 14f, 4f, 9f, Color.DarkGray)
    // Legs
    p(12f, 23f, 3f, 7f, Color.DarkGray)
    p(17f, 23f, 3f, 7f, Color.DarkGray)
    p(11f, 30f, 10f, 1f, FFBlack)
}

// --- MONSTER SPRITES (NEW HIGH DETAIL) ---

fun DrawScope.drawSlime() {
    val p = px32()
    p(8f, 12f, 16f, 14f, FFPurple)
    p(10f, 10f, 12f, 3f, Color(0xFFBB8FCE))
    p(10f, 15f, 3f, 3f, Color.White) // Eye
    p(19f, 15f, 3f, 3f, Color.White)
    p(11f, 16f, 1f, 1f, Color.Black)
    p(20f, 16f, 1f, 1f, Color.Black)
}

fun DrawScope.drawGoblin() {
    val p = px32()
    // Pointy Ears
    p(8f, 8f, 3f, 4f, FFGreen)
    p(21f, 8f, 3f, 4f, FFGreen)
    // Head
    p(11f, 6f, 10f, 8f, FFGreen)
    p(10f, 5f, 12f, 2f, FFRed) // Red Hat Brim
    p(12f, 2f, 8f, 4f, FFRed) // Top hat
    // Eyes
    p(12f, 9f, 2f, 2f, Color.Yellow)
    p(18f, 9f, 2f, 2f, Color.Yellow)
    // Body / Gear
    p(11f, 14f, 10f, 10f, Color(0xFF8B4513)) // Brown clothes
    p(11f, 16f, 10f, 1f, FFSilverDark) // belt
    // Legs
    p(12f, 24f, 3f, 6f, FFGreen)
    p(17f, 24f, 3f, 6f, FFGreen)
}

fun DrawScope.drawWolf() {
    val p = px32()
    // Snout and Head
    p(22f, 12f, 6f, 4f, Color.Gray) // snout
    p(12f, 8f, 10f, 10f, Color.Gray) // head
    p(12f, 6f, 3f, 3f, Color.Gray) // ear
    p(19f, 6f, 3f, 3f, Color.Gray) // ear
    p(22f, 13f, 2f, 1f, FFBlack) // nose
    p(14f, 11f, 2f, 2f, Color.Red) // fierce eye
    // Body
    p(6f, 14f, 14f, 12f, Color.Gray)
    p(6f, 18f, 14f, 8f, Color.DarkGray) // shading
    // Tail
    p(2f, 15f, 4f, 8f, Color.Gray)
    // Legs
    p(8f, 26f, 3f, 5f, Color.DarkGray)
    p(16f, 26f, 3f, 5f, Color.DarkGray)
}

fun DrawScope.drawSahagin() {
    val p = px32()
    // Scaly body
    p(10f, 4f, 12f, 20f, FFBlue)
    p(10f, 2f, 4f, 4f, FFGreen) // head fin
    p(18f, 2f, 4f, 4f, FFGreen) // head fin
    p(8f, 10f, 2f, 10f, FFGreen) // arm fins
    p(22f, 10f, 2f, 10f, FFGreen)
    // Face
    p(12f, 8f, 2f, 2f, Color.Red) // eyes
    p(18f, 8f, 2f, 2f, Color.Red)
    // Trident
    p(24f, 6f, 1f, 20f, FFSilver)
    p(23f, 5f, 3f, 3f, FFSilverDark)
    // Legs
    p(12f, 24f, 8f, 6f, FFBlueDark)
}

fun DrawScope.drawPirate() {
    val p = px32()
    // Bandanna
    p(11f, 3f, 10f, 4f, FFRed)
    p(20f, 4f, 3f, 2f, FFRedDark) // knot
    // Face with Stubble
    p(11f, 7f, 10f, 7f, SkinMid)
    p(11f, 12f, 10f, 2f, Color.DarkGray.copy(alpha = 0.5f)) // beard
    p(12f, 9f, 2f, 2f, FFBlack) // eye patch
    p(11f, 9f, 4f, 1f, FFBlack) // strap
    p(18f, 9f, 1f, 1f, Color.Black) // normal eye
    // Shirt
    p(10f, 14f, 12f, 9f, Color.White)
    p(10f, 14f, 3f, 9f, FFBlue) // vest
    p(19f, 14f, 3f, 9f, FFBlue)
    // Cutlass
    p(24f, 10f, 2f, 12f, FFSilver)
    p(23f, 22f, 4f, 2f, FFGold)
    // Legs
    p(12f, 23f, 3f, 7f, Color(0xFF5D4037))
    p(17f, 23f, 3f, 7f, Color(0xFF5D4037))
}

fun DrawScope.drawOgre() {
    val p = px32()
    p(8f, 8f, 16f, 16f, FFGold) // Massive Body
    p(11f, 4f, 10f, 8f, FFGold) // Head
    p(12f, 14f, 8f, 10f, Color(0xFFB7950B)) // shading
    // Face
    p(13f, 7f, 2f, 2f, Color.Black)
    p(17f, 7f, 2f, 2f, Color.Black)
    p(14f, 10f, 4f, 1f, Color.White) // tooth
    // Club
    p(24f, 6f, 4f, 18f, Color(0xFF5D4037))
    p(24f, 6f, 1f, 18f, FFBlack.copy(alpha = 0.3f))
    // Legs
    p(11f, 24f, 4f, 6f, Color(0xFFB7950B))
    p(17f, 24f, 4f, 6f, Color(0xFFB7950B))
}

fun DrawScope.drawBomb() {
    val p = px32()
    drawCircle(FFRed, radius = size.width * 0.45f, center = Offset(size.width/2f, size.height/2f))
    drawCircle(FFGold, radius = size.width * 0.3f, center = Offset(size.width/2f, size.height/2f))
    // Cracks/Face
    p(12f, 12f, 2f, 2f, FFBlack)
    p(18f, 12f, 2f, 2f, FFBlack)
    p(14f, 16f, 4f, 2f, FFBlack)
    // Fire tufts
    repeat(6) { i ->
        val angle = i * 60f * (Math.PI / 180f).toFloat()
        p(16f + cos(angle)*12, 16f + sin(angle)*12, 3f, 3f, FFRedDark)
    }
}

fun DrawScope.drawEye() {
    val p = px32()
    drawCircle(FFPurple, radius = size.width * 0.4f, center = Offset(size.width/2f, size.height/2f))
    drawCircle(Color.White, radius = size.width * 0.25f, center = Offset(size.width/2f, size.height/2f))
    drawCircle(FFBlack, radius = size.width * 0.12f, center = Offset(size.width/2f, size.height/2f))
    // Eye Stalks
    for (i in 0..3) {
        p(6f + i*6, 2f, 2f, 6f, FFPurple)
        drawCircle(Color.White, radius = size.width * 0.05f, center = Offset((7f + i*6) * size.width/32f, 2f * size.width/32f))
    }
}

fun DrawScope.drawTonberry() {
    val p = px32()
    p(10f, 6f, 12f, 18f, FFGreen) // Body
    p(12f, 14f, 8f, 10f, FFGreenDark) // shading
    p(13f, 10f, 2f, 2f, Color.Yellow) // eyes
    p(17f, 10f, 2f, 2f, Color.Yellow)
    // Robe
    p(9f, 16f, 14f, 12f, Color(0xFF7E5109))
    // Lantern
    p(4f, 18f, 4f, 6f, Color.Yellow)
    p(5f, 17f, 2f, 1f, FFSilver)
    // Knife
    p(23f, 20f, 6f, 2f, FFSilver)
    p(23f, 20f, 1f, 4f, FFBlack)
}

fun DrawScope.drawDragon(color: Color) {
    val p = px32()
    // Large Body
    p(6f, 12f, 18f, 12f, color)
    p(6f, 18f, 18f, 6f, FFBlack.copy(alpha = 0.2f)) // belly
    // Neck and Head
    p(22f, 4f, 4f, 10f, color)
    p(24f, 4f, 6f, 5f, color)
    p(26f, 6f, 2f, 2f, Color.Yellow) // eye
    p(28f, 8f, 3f, 2f, color) // snout
    // Wings
    p(10f, 4f, 10f, 8f, color.copy(alpha = 0.6f))
    p(11f, 5f, 8f, 6f, Color.White.copy(alpha = 0.2f)) // wing membrane
    // Tail
    p(0f, 20f, 6f, 4f, color)
}

fun DrawScope.drawGarland() {
    val p = px32()
    // Heavy Chaos Armor
    p(10f, 2f, 12f, 22f, FFBlack)
    p(10f, 2f, 2f, 22f, FFSilverDark) // highlight edge
    p(6f, 3f, 4f, 8f, FFSilver) // Horns
    p(22f, 3f, 4f, 8f, FFSilver)
    // Face in helm
    p(13f, 8f, 6f, 4f, FFBlack)
    p(14f, 9f, 1f, 1f, Color.Red)
    p(17f, 9f, 1f, 1f, Color.Red)
    // Cape
    p(8f, 11f, 16f, 16f, Color(0xFF7B241C))
    // Giant Sword
    p(25f, 6f, 3f, 22f, FFSilver)
    p(24f, 26f, 5f, 2f, FFGold)
}

fun DrawScope.drawChaos() {
    val p = px32()
    // Demonic form
    p(9f, 8f, 14f, 16f, Color(0xFFD4AC0D))
    // Massive Wings
    p(2f, 4f, 10f, 15f, FFBlack)
    p(20f, 4f, 10f, 15f, FFBlack)
    // Head & Great Horns
    p(11f, 2f, 10f, 8f, Color(0xFFD4AC0D))
    p(8f, 0f, 3f, 8f, Color.White)
    p(21f, 0f, 3f, 8f, Color.White)
    // Eyes
    p(13f, 5f, 2f, 2f, Color.Red)
    p(17f, 5f, 2f, 2f, Color.Red)
}

fun DrawScope.drawLich() {
    val p = px32()
    // Skeleton face
    p(12f, 4f, 8f, 8f, Color.White)
    p(13f, 6f, 1f, 1f, FFBlack)
    p(18f, 6f, 1f, 1f, FFBlack)
    // Ripped Robes
    p(10f, 12f, 12f, 14f, FFPurple)
    p(9f, 14f, 14f, 2f, FFBlack.copy(alpha = 0.4f)) // rot
    p(11f, 20f, 10f, 6f, FFPurple)
}

fun DrawScope.drawKraken() {
    val p = px32()
    p(10f, 4f, 12f, 12f, FFBlueDark)
    // Eyes
    p(13f, 8f, 2f, 2f, Color.Yellow)
    p(17f, 8f, 2f, 2f, Color.Yellow)
    // Tentacles
    for (i in 0..5) {
        p(6f + i*3.5f, 16f, 2f, 10f, Color.Cyan)
    }
}

fun DrawScope.drawCockatrice() {
    val p = px32()
    // Chicken/Lizard hybrid
    p(11f, 10f, 10f, 12f, FFGold)
    p(13f, 5f, 6f, 6f, FFGold) // head
    p(12f, 4f, 8f, 2f, FFRed) // crest
    p(19f, 7f, 3f, 2f, Color(0xFFE67E22)) // beak
    p(14f, 7f, 1f, 1f, FFBlack) // eye
    // Tail
    p(6f, 12f, 5f, 8f, FFGreen)
    // Legs
    p(12f, 22f, 2f, 6f, Color(0xFFE67E22))
    p(18f, 22f, 2f, 6f, Color(0xFFE67E22))
}

// --- COMPOSABLE WRAPPERS ---

@Composable fun HeroSprite(heroClass: HeroClass, modifier: Modifier = Modifier) = Canvas(modifier) {
    when(heroClass) {
        HeroClass.WARRIOR -> drawWarrior()
        HeroClass.BLACK_MAGE -> drawBlackMage()
        HeroClass.WHITE_MAGE -> drawWhiteMage()
        HeroClass.THIEF -> drawThief()
        HeroClass.FREELANCER -> drawFreelancer()
        HeroClass.MONK -> drawMonk()
        HeroClass.KNIGHT -> drawKnight()
        HeroClass.PALADIN -> drawPaladin()
        HeroClass.RED_MAGE -> drawRedMage()
        HeroClass.SUMMONER -> drawSummoner()
        HeroClass.NINJA -> drawNinja()
        HeroClass.DRAGOON -> drawDragoon()
        HeroClass.BARD -> drawBard()
        HeroClass.SAMURAI -> drawSamurai()
        HeroClass.ONION_KNIGHT -> drawFreelancer()
        HeroClass.MIME -> drawBard()
        HeroClass.NECROMANCER -> drawBlackMage()
        HeroClass.BLUE_MAGE -> drawRedMage()
    }
}

@Composable fun EnemySprite(enemyName: String, modifier: Modifier = Modifier) = Canvas(modifier) {
    when {
        enemyName.contains("Slime", true) || enemyName.contains("Flan", true) -> drawSlime()
        enemyName.contains("Goblin", true) -> drawGoblin()
        enemyName.contains("Orc", true) || enemyName.contains("Ogre", true) -> drawOgre()
        enemyName.contains("Demon", true) || enemyName.contains("Chaos", true) -> drawChaos()
        enemyName.contains("Dragon", true) || enemyName.contains("Tiamat", true) -> drawDragon(Color.Red)
        enemyName.contains("Wolf", true) -> drawWolf()
        enemyName.contains("Sahagin", true) || enemyName.contains("Merman", true) -> drawSahagin()
        enemyName.contains("Pirate", true) -> drawPirate()
        enemyName.contains("Cockatrice", true) -> drawCockatrice()
        enemyName.contains("Kraken", true) -> drawKraken()
        enemyName.contains("Bomb", true) -> drawBomb()
        enemyName.contains("Eye", true) -> drawEye()
        enemyName.contains("Tonberry", true) -> drawTonberry()
        enemyName.contains("Lich", true) -> drawLich()
        enemyName.contains("Garland", true) || enemyName.contains("Knight", true) -> drawGarland()
        else -> drawSlime()
    }
}

@Composable fun PetSprite(petType: PetType, modifier: Modifier = Modifier) = Canvas(modifier) {
    val p = px32()
    when (petType) {
        PetType.CHOCOBO -> {
            p(12f, 8f, 10f, 10f, FFGold) // Body
            p(16f, 4f, 6f, 6f, FFGold) // Head
            p(20f, 6f, 3f, 2f, FFOrange) // Beak
            p(18f, 6f, 1f, 1f, Color.Black) // Eye
            p(12f, 18f, 2f, 6f, FFOrange) // Leg
            p(18f, 18f, 2f, 6f, FFOrange) // Leg
        }
        PetType.MOOGLE -> {
            p(12f, 10f, 8f, 10f, Color.White) // Body
            p(11f, 4f, 10f, 8f, Color.White) // Head
            p(15f, 2f, 2f, 3f, Color.Red) // Pom-pom
            p(12f, 8f, 1f, 1f, Color.Black) // Eye
            p(19f, 8f, 1f, 1f, Color.Black) // Eye
            p(8f, 10f, 4f, 6f, FFPurple) // Wing
            p(20f, 10f, 4f, 6f, FFPurple) // Wing
        }
        PetType.CAT -> {
            p(10f, 12f, 12f, 10f, Color.White) // Body
            p(11f, 6f, 10f, 8f, Color.White) // Head
            p(10f, 4f, 3f, 3f, Color.White) // Ear
            p(19f, 4f, 3f, 3f, Color.White) // Ear
            p(13f, 8f, 1f, 1f, Color.Black) // Eye
            p(18f, 8f, 1f, 1f, Color.Black) // Eye
            p(22f, 14f, 2f, 8f, Color.White) // Tail
        }
        PetType.CACTUAR -> {
            p(12f, 6f, 8f, 20f, FFGreen) // Body
            p(6f, 10f, 8f, 2f, FFGreen) // Arm L
            p(18f, 18f, 8f, 2f, FFGreen) // Arm R
            p(14f, 10f, 1f, 1f, Color.Black) // Eye
            p(17f, 10f, 1f, 1f, Color.Black) // Eye
            p(15f, 14f, 2f, 2f, Color.Black) // Mouth
        }
        PetType.TONBERRY -> drawTonberry()
    }
}
