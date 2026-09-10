package com.game.dungeon.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.models.ImpactSpark
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

private fun DrawScope.setupDrawHelpers(): (Float, Float, Float, Float, Color) -> Unit {
    val scale = size.width / 24f
    return { x, y, w, h, color ->
        drawRect(color, Offset(x * scale, y * scale), Size(w * scale, h * scale))
    }
}

// COLORS
private val Skin = Color(0xFFFFDBAC)
private val SkinDark = Color(0xFFE0AC69)
private val HairBrown = Color(0xFF634721)
private val HairBlue = Color(0xFF4A90E2)
private val HairWhite = Color(0xFFEEEEEE)
private val ClothRed = Color(0xFFC0392B)
private val ClothBlue = Color(0xFF2980B9)
private val ClothGreen = Color(0xFF27AE60)
private val ClothWhite = Color(0xFFFDFEFE)
private val ClothYellow = Color(0xFFF1C40F)
private val MetalSilver = Color(0xFFBDC3C7)
private val MetalGold = Color(0xFFF39C12)
private val MetalPurple = Color(0xFF8E44AD)

fun DrawScope.drawWarrior() {
    val px = setupDrawHelpers()
    // Spiky Hair
    px(8f, 2f, 8f, 6f, HairBrown)
    px(7f, 4f, 2f, 2f, HairBrown)
    px(15f, 4f, 2f, 2f, HairBrown)
    // Face
    px(8f, 8f, 8f, 5f, SkinDark)
    px(9f, 9f, 2f, 1f, Color.Black) // Eyes
    px(13f, 9f, 2f, 1f, Color.Black)
    // Red Armor
    px(7f, 13f, 10f, 7f, ClothRed)
    px(6f, 14f, 2f, 3f, ClothRed) // Shoulders
    px(16f, 14f, 2f, 3f, ClothRed)
    // Legs/Boots
    px(8f, 20f, 3f, 3f, Color.DarkGray)
    px(13f, 20f, 3f, 3f, Color.DarkGray)
    px(7f, 22f, 4f, 2f, ClothRed)
    px(13f, 22f, 4f, 2f, ClothRed)
}

fun DrawScope.drawThief() {
    val px = setupDrawHelpers()
    // Blue Hair
    px(8f, 3f, 8f, 5f, HairBlue)
    px(7f, 5f, 2f, 2f, HairBlue)
    px(15f, 5f, 2f, 2f, HairBlue)
    // Face
    px(8f, 8f, 8f, 5f, Skin)
    px(9f, 9f, 1f, 1f, Color.Black)
    px(14f, 9f, 1f, 1f, Color.Black)
    // Green/Brown Outfit
    px(8f, 13f, 8f, 7f, Color(0xFF8B4513)) // Brown vest
    px(9f, 14f, 6f, 6f, ClothGreen) // Green shirt
    // Pants/Boots
    px(8f, 20f, 3f, 4f, ClothGreen)
    px(13f, 20f, 3f, 4f, ClothGreen)
    px(7f, 22f, 4f, 2f, Color(0xFF5D4037))
    px(13f, 22f, 4f, 2f, Color(0xFF5D4037))
}

fun DrawScope.drawMonk() {
    val px = setupDrawHelpers()
    // Hair & Headband
    px(8f, 3f, 8f, 5f, HairBrown)
    px(7f, 6f, 10f, 2f, ClothBlue) // Blue headband
    // Face
    px(8f, 8f, 8f, 5f, SkinDark)
    px(9f, 10f, 2f, 1f, Color.Black)
    px(13f, 10f, 2f, 1f, Color.Black)
    // Light Blue Gi
    px(8f, 13f, 8f, 7f, Color(0xFFADD8E6))
    px(8f, 17f, 8f, 2f, ClothYellow) // Belt
    // Pants
    px(8f, 20f, 3f, 4f, Color(0xFFADD8E6))
    px(13f, 20f, 3f, 4f, Color(0xFFADD8E6))
    px(7f, 23f, 4f, 1f, Skin) // Bare feet? Or simple sandals
}

fun DrawScope.drawRedMage() {
    val px = setupDrawHelpers()
    // Iconic Red Hat
    px(5f, 3f, 14f, 2f, ClothRed)
    px(8f, 0f, 8f, 4f, ClothRed)
    px(14f, 1f, 2f, 2f, Color.White) // Plume
    // Face & White Hair
    px(8f, 8f, 8f, 5f, SkinDark)
    px(7f, 7f, 2f, 6f, HairWhite) // Hair on sides
    px(15f, 7f, 2f, 6f, HairWhite)
    px(10f, 10f, 4f, 1f, Color.Black) // Hidden eyes look
    // Red Tunic/Cape
    px(7f, 13f, 10f, 8f, ClothRed)
    px(9f, 14f, 6f, 7f, Color(0xFF922B21))
    // Legs
    px(8f, 21f, 3f, 3f, Color.Black)
    px(13f, 21f, 3f, 3f, Color.Black)
    px(7f, 22f, 4f, 2f, ClothRed)
    px(13f, 22f, 4f, 2f, ClothRed)
}

fun DrawScope.drawWhiteMage() {
    val px = setupDrawHelpers()
    // White Hood
    px(7f, 2f, 10f, 10f, ClothWhite)
    px(8f, 3f, 8f, 8f, ClothWhite)
    // Red triangle patterns
    px(7f, 4f, 2f, 2f, ClothRed)
    px(15f, 4f, 2f, 2f, ClothRed)
    px(11f, 2f, 2f, 2f, ClothRed)
    // Face peeking out
    px(9f, 6f, 6f, 5f, Skin)
    px(10f, 8f, 1f, 1f, Color.Black)
    px(13f, 8f, 1f, 1f, Color.Black)
    // Robe
    px(7f, 12f, 10f, 10f, ClothWhite)
    px(7f, 20f, 10f, 2f, ClothRed) // Red hem
    // Detail
    px(11f, 12f, 2f, 8f, ClothRed) // Vertical red stripe
}

fun DrawScope.drawBlackMage() {
    val px = setupDrawHelpers()
    // Tall Yellow Hat
    px(9f, 0f, 6f, 8f, ClothYellow)
    px(6f, 6f, 12f, 2f, ClothYellow)
    // Black Face & Yellow Eyes
    px(8f, 8f, 8f, 5f, Color.Black)
    px(9f, 10f, 2f, 1f, Color.Yellow)
    px(13f, 10f, 2f, 1f, Color.Yellow)
    // Blue Robe
    px(7f, 13f, 10f, 9f, ClothBlue)
    px(10f, 13f, 4f, 9f, Color(0xFF1B4F72)) // Inner shadow
    // Hands
    px(5f, 15f, 2f, 2f, Color.Black)
    px(17f, 15f, 2f, 2f, Color.Black)
}

fun DrawScope.drawKnight() {
    val px = setupDrawHelpers()
    // Silver Helmet
    px(8f, 2f, 8f, 8f, MetalSilver)
    px(11f, 1f, 2f, 2f, ClothBlue) // Plume
    px(9f, 5f, 6f, 2f, Color.Black) // Visor slit
    // Armor
    px(7f, 10f, 10f, 10f, MetalSilver)
    px(11f, 10f, 2f, 10f, ClothBlue) // Chest detail
    px(6f, 11f, 3f, 4f, MetalSilver) // Pauldrons
    px(15f, 11f, 3f, 4f, MetalSilver)
    // Legs
    px(8f, 20f, 3f, 4f, MetalSilver)
    px(13f, 20f, 3f, 4f, MetalSilver)
}

fun DrawScope.drawPaladin() {
    val px = setupDrawHelpers()
    // Golden Crown/Circlet
    px(8f, 2f, 8f, 4f, MetalGold)
    px(9f, 6f, 6f, 7f, Skin) // Face
    px(9f, 4f, 6f, 3f, HairWhite) // Long hair
    px(10f, 8f, 1f, 1f, Color.Blue)
    px(13f, 8f, 1f, 1f, Color.Blue)
    // White/Gold Armor
    px(7f, 13f, 10f, 8f, ClothWhite)
    px(11f, 13f, 2f, 8f, MetalGold)
    px(6f, 14f, 2f, 10f, ClothBlue) // Cape
    px(16f, 14f, 2f, 10f, ClothBlue)
    // Boots
    px(8f, 21f, 3f, 3f, MetalGold)
    px(13f, 21f, 3f, 3f, MetalGold)
}

fun DrawScope.drawSummoner() {
    val px = setupDrawHelpers()
    // Green Hood with Horn
    px(8f, 3f, 8f, 8f, ClothGreen)
    px(11f, 1f, 2f, 3f, Color.White) // Horn
    // Face
    px(9f, 6f, 6f, 5f, Skin)
    px(10f, 8f, 1f, 1f, Color.Black)
    px(13f, 8f, 1f, 1f, Color.Black)
    // Robe
    px(7f, 11f, 10f, 12f, ClothGreen)
    px(10f, 11f, 4f, 12f, Color.White) // White front panel
    px(10f, 15f, 4f, 2f, MetalGold) // Belt/Gem
}

fun DrawScope.drawNinja() {
    val px = setupDrawHelpers()
    // Masked Head
    px(8f, 4f, 8f, 8f, Color(0xFF1A1A1A))
    px(9f, 6f, 6f, 2f, Skin) // Eyes visible
    px(10f, 6f, 1f, 1f, Color.Black)
    px(13f, 6f, 1f, 1f, Color.Black)
    px(6f, 10f, 12f, 2f, ClothRed) // Red scarf
    // Dark gear
    px(8f, 12f, 8f, 8f, Color(0xFF1A1A1A))
    px(7f, 13f, 10f, 2f, ClothGreen) // Straps
    // Legs
    px(8f, 20f, 3f, 4f, Color(0xFF1A1A1A))
    px(13f, 20f, 3f, 4f, Color(0xFF1A1A1A))
}

fun DrawScope.drawDragoon() {
    val px = setupDrawHelpers()
    // Dragon Helmet
    px(8f, 2f, 8f, 8f, MetalPurple)
    px(7f, 1f, 2f, 4f, MetalPurple) // Wings
    px(15f, 1f, 2f, 4f, MetalPurple)
    px(11f, 0f, 2f, 3f, MetalGold) // Spike
    // Armor
    px(7f, 10f, 10f, 10f, MetalPurple)
    px(6f, 11f, 3f, 4f, MetalPurple)
    px(15f, 11f, 3f, 4f, MetalPurple)
    px(11f, 10f, 2f, 10f, MetalGold) // Scale detail
    // Legs
    px(8f, 20f, 3f, 4f, MetalPurple)
    px(13f, 20f, 3f, 4f, MetalPurple)
}

fun DrawScope.drawBard() {
    val px = setupDrawHelpers()
    // Hat
    px(8f, 2f, 8f, 4f, Color(0xFFE91E63)) // Pink hat
    px(14f, 1f, 2f, 2f, Color.Green) // Feather
    // Face & Hair
    px(8f, 6f, 8f, 6f, Skin)
    px(7f, 6f, 2f, 6f, ClothYellow) // Blonde hair
    px(15f, 6f, 2f, 6f, ClothYellow)
    px(10f, 9f, 4f, 1f, Color.Black)
    // Tunic
    px(8f, 12f, 8f, 8f, Color(0xFFE91E63))
    px(7f, 15f, 10f, 2f, ClothWhite) // Frills
    // Pants
    px(8f, 20f, 3f, 4f, ClothGreen)
    px(13f, 20f, 3f, 4f, ClothGreen)
}

fun DrawScope.drawSamurai() {
    val px = setupDrawHelpers()
    // Kabuto Helmet
    px(8f, 3f, 8f, 6f, Color(0xFF1A1A1A))
    px(6f, 6f, 12f, 2f, Color(0xFF1A1A1A))
    px(11f, 1f, 2f, 2f, MetalGold) // Crest
    // Face
    px(9f, 9f, 6f, 3f, Skin)
    px(10f, 10f, 4f, 1f, Color.Black)
    // Red Armor
    px(7f, 12f, 10f, 8f, ClothRed)
    px(6f, 13f, 2f, 6f, ClothRed) // Shoulder plates
    px(16f, 13f, 2f, 6f, ClothRed)
    px(8f, 17f, 8f, 2f, Color.Black) // Belt
    // Legs
    px(8f, 20f, 3f, 4f, ClothRed)
    px(13f, 20f, 3f, 4f, ClothRed)
}

fun DrawScope.drawArcher() {
    val px = setupDrawHelpers()
    // Green Hood
    px(8f, 4f, 8f, 8f, ClothGreen)
    px(9f, 6f, 6f, 5f, Skin)
    px(10f, 8f, 1f, 1f, Color.Black)
    px(13f, 8f, 1f, 1f, Color.Black)
    // Tunic
    px(8f, 12f, 8f, 8f, ClothGreen)
    px(7f, 13f, 10f, 2f, Color(0xFF8B4513)) // Leather strap
    // Detail: Quiver on back peeking out
    px(15f, 13f, 2f, 6f, Color(0xFF5D4037))
    // Legs
    px(8f, 20f, 3f, 4f, Color(0xFF8B4513))
    px(13f, 20f, 3f, 4f, Color(0xFF8B4513))
}

fun DrawScope.drawFreelancer() {
    val px = setupDrawHelpers()
    // Simple Hair
    px(9f, 4f, 6f, 4f, HairBrown)
    // Face
    px(9f, 8f, 6f, 5f, Skin)
    px(10f, 10f, 1f, 1f, Color.Black)
    px(13f, 10f, 1f, 1f, Color.Black)
    // Grey Tunic
    px(8f, 13f, 8f, 7f, Color.Gray)
    px(8f, 20f, 3f, 4f, Color.DarkGray)
    px(13f, 20f, 3f, 4f, Color.DarkGray)
}

fun DrawScope.drawTownInn() {
    val px = setupDrawHelpers()
    // Base Structure (Tudor style)
    px(4f, 10f, 24f, 18f, Color(0xFFE0C097)) // Cream walls
    // Timber frame details
    for (x in 4..28 step 6) px(x.toFloat(), 10f, 1f, 18f, Color(0xFF3D2B1F))
    px(4f, 15f, 24f, 1f, Color(0xFF3D2B1F))
    // Roof (Thatch/Shingle mix)
    val roofPath = Path().apply {
        moveTo(2f, 12f)
        lineTo(16f, 0f)
        lineTo(30f, 12f)
        close()
    }
    drawPath(roofPath, Color(0xFF8B4513))
    // Windows with warm glow
    px(7f, 17f, 4f, 4f, Color(0xFFFFD700).copy(alpha = 0.6f))
    px(19f, 17f, 4f, 4f, Color(0xFFFFD700).copy(alpha = 0.6f))
    // Big Arched Door
    px(13f, 20f, 6f, 8f, Color(0xFF2A1A0A))
    px(15f, 23f, 1f, 1f, Color.Yellow) // Handle
}

fun DrawScope.drawTownCrystalShop() {
    val px = setupDrawHelpers()
    // Wizard's Tower shape
    px(8f, 5f, 16f, 23f, Color(0xFF34495E)) // Deep blue stone
    // Conical Purple Roof
    val roofPath = Path().apply {
        moveTo(6f, 7f)
        lineTo(16f, -4f)
        lineTo(26f, 7f)
        close()
    }
    drawPath(roofPath, Color(0xFF8E44AD))
    // Floating Crystal on top
    px(15f, -8f, 2f, 4f, Color.Cyan)
    // Magic Orbs/Windows
    px(11f, 10f, 3f, 3f, Color.White.copy(alpha = 0.5f))
    px(18f, 15f, 3f, 3f, Color.White.copy(alpha = 0.5f))
    // Arched Door
    px(13f, 22f, 6f, 6f, Color(0xFF1A1A1A))
    px(14f, 22f, 4f, 1f, Color.Cyan) // Magic seal
}

fun DrawScope.drawTownBarracks() {
    val px = setupDrawHelpers()
    // Stone Forge/Training Ground look
    px(4f, 12f, 24f, 16f, Color(0xFF7F8C8D)) // Grey stone
    // Battlement top
    for (x in 4..28 step 4) px(x.toFloat(), 9f, 2f, 3f, Color(0xFF7F8C8D))
    // Chimney for the forge
    px(22f, 4f, 4f, 8f, Color(0xFF333333))
    px(21f, 3f, 6f, 2f, Color.Black)
    // Red Banner
    px(6f, 13f, 4f, 10f, Color.Red)
    px(7f, 15f, 2f, 2f, Color.Yellow)
    // Heavy Iron Door
    px(12f, 18f, 8f, 10f, Color(0xFF1A1A1A))
    px(11f, 18f, 10f, 1f, Color.DarkGray)
}

fun DrawScope.drawTownPortal() {
    val px = setupDrawHelpers()
    // Ancient Stone Arch
    val archPath = Path().apply {
        moveTo(4f, 28f)
        lineTo(4f, 10f)
        quadraticTo(16f, 0f, 28f, 10f)
        lineTo(28f, 28f)
    }
    drawPath(archPath, Color(0xFF2C3E50), style = Stroke(width = 15f))
    // Runes
    for (i in 0..5) {
        px(5f, 10f + i * 3f, 1f, 1f, Color.Cyan)
        px(26f, 10f + i * 3f, 1f, 1f, Color.Cyan)
    }
    // Swirling Portal Interior (Glow)
    val interior = Path().apply {
        moveTo(8f, 28f)
        lineTo(8f, 12f)
        quadraticTo(16f, 4f, 24f, 12f)
        lineTo(24f, 28f)
    }
    drawPath(interior, Brush.radialGradient(listOf(Color(0xFF0D0720), Color(0xFF8E44AD).copy(alpha = 0.5f))))
}

fun DrawScope.drawSlime() {
    val px = setupDrawHelpers()
    // Gooey puddle
    px(6f, 15f, 12f, 9f, Color(0xFF9B59B6))
    px(8f, 13f, 8f, 3f, Color(0xFFBB8FCE))
    // Bubbles/Eyes
    px(8f, 16f, 2f, 2f, Color.White)
    px(14f, 16f, 2f, 2f, Color.White)
}

fun DrawScope.drawGoblin() {
    val px = setupDrawHelpers()
    // Green Head & Ears
    px(8f, 4f, 8f, 8f, Color(0xFF27AE60))
    px(6f, 6f, 2f, 4f, Color(0xFF27AE60)) // Left ear
    px(16f, 6f, 2f, 4f, Color(0xFF27AE60)) // Right ear
    // Face
    px(9f, 7f, 2f, 2f, Color.Yellow) // Glowing eyes
    px(13f, 7f, 2f, 2f, Color.Yellow)
    // Brown Tunic
    px(8f, 12f, 8f, 8f, Color(0xFF8B4513))
    px(7f, 13f, 10f, 2f, Color.DarkGray)
    // Legs
    px(8f, 20f, 3f, 4f, Color(0xFF27AE60))
    px(13f, 20f, 3f, 4f, Color(0xFF27AE60))
}

fun DrawScope.drawWolf() {
    val px = setupDrawHelpers()
    // Gray Fur
    px(10f, 6f, 10f, 6f, Color.Gray) // Head
    px(7f, 10f, 14f, 10f, Color.Gray) // Body
    px(11f, 8f, 2f, 2f, Color.Black) // Eye
    px(18f, 9f, 4f, 2f, Color.Gray) // Snout
    // Legs
    px(8f, 20f, 3f, 5f, Color.LightGray)
    px(16f, 20f, 3f, 5f, Color.LightGray)
    // Tail
    px(4f, 12f, 4f, 4f, Color.Gray)
}

fun DrawScope.drawSahagin() {
    val px = setupDrawHelpers()
    // Blue Scaly Body
    px(8f, 4f, 8f, 18f, Color(0xFF3498DB))
    // Fins
    px(6f, 6f, 2f, 8f, Color(0xFF1ABC9C))
    px(16f, 6f, 2f, 8f, Color(0xFF1ABC9C))
    px(10f, 2f, 4f, 2f, Color(0xFF1ABC9C)) // Head fin
    // Face
    px(9f, 8f, 2f, 2f, Color.Red)
    px(13f, 8f, 2f, 2f, Color.Red)
    // Trident (visual only)
    px(17f, 4f, 1f, 14f, Color.LightGray)
}

fun DrawScope.drawOgre() {
    val px = setupDrawHelpers()
    // Massive Yellow Body
    px(6f, 8f, 12f, 14f, Color(0xFFF1C40F))
    // Head
    px(9f, 3f, 6f, 5f, Color(0xFFF1C40F))
    px(10f, 5f, 1f, 1f, Color.Black)
    px(13f, 5f, 1f, 1f, Color.Black)
    px(11f, 7f, 2f, 1f, Color.White) // Tooth
    // Loincloth
    px(6f, 18f, 12f, 3f, Color(0xFFE67E22))
    // Club
    px(18f, 6f, 3f, 16f, Color(0xFF5D4037))
}

fun DrawScope.drawBomb() {
    val px = setupDrawHelpers()
    // Fiery Ball
    drawCircle(Color(0xFFE67E22), radius = size.width * 0.4f, center = Offset(size.width/2f, size.height/2f))
    drawCircle(Color(0xFFF1C40F), radius = size.width * 0.25f, center = Offset(size.width/2f, size.height/2f))
    // Angry Eyes
    px(9f, 10f, 2f, 2f, Color.White)
    px(13f, 10f, 2f, 2f, Color.White)
    px(10f, 11f, 1f, 1f, Color.Black)
    px(13f, 11f, 1f, 1f, Color.Black)
    // Flames on top
    px(11f, 2f, 2f, 4f, Color.Red)
    px(8f, 4f, 2f, 3f, Color.Red)
    px(14f, 4f, 2f, 3f, Color.Red)
}

fun DrawScope.drawEye() {
    val px = setupDrawHelpers()
    // Central Eye (Beholder style)
    drawCircle(Color(0xFF8E44AD), radius = size.width * 0.35f, center = Offset(size.width/2f, size.height/2f))
    drawCircle(Color.White, radius = size.width * 0.2f, center = Offset(size.width/2f, size.height/2f))
    drawCircle(Color.Black, radius = size.width * 0.1f, center = Offset(size.width/2f, size.height/2f))
    // Stalks
    repeat(4) {
        px(11f, 2f, 2f, 6f, Color(0xFF8E44AD))
    }
}

fun DrawScope.drawTonberry() {
    val px = setupDrawHelpers()
    // Green Body
    px(8f, 6f, 8f, 16f, Color(0xFF2ECC71))
    // Yellow Glowing Eyes
    px(10f, 9f, 1f, 1f, Color.Yellow)
    px(13f, 9f, 1f, 1f, Color.Yellow)
    // Brown Robe
    px(7f, 12f, 10f, 10f, Color(0xFF7E5109))
    // Lantern (Left)
    px(4f, 14f, 3f, 4f, Color.Yellow)
    px(5f, 13f, 1f, 1f, Color.Gray)
    // Knife (Right)
    px(16f, 15f, 4f, 2f, MetalSilver)
    px(16f, 15f, 1f, 3f, Color.Black)
}

fun DrawScope.drawDragon(color: Color) {
    val px = setupDrawHelpers()
    // Body
    px(6f, 10f, 14f, 10f, color)
    // Neck & Head
    px(16f, 4f, 4f, 6f, color)
    px(18f, 4f, 4f, 3f, color)
    px(19f, 5f, 1f, 1f, Color.Yellow) // Eye
    // Wings
    px(8f, 4f, 8f, 6f, color.copy(alpha = 0.7f))
    // Tail
    px(2f, 16f, 4f, 4f, color)
}

fun DrawScope.drawGarland() {
    val px = setupDrawHelpers()
    // Dark Knight Armor
    px(7f, 2f, 10f, 20f, Color(0xFF2C3E50))
    // Horns
    px(6f, 2f, 2f, 4f, Color.Gray)
    px(16f, 2f, 2f, 4f, Color.Gray)
    // Cape
    px(5f, 10f, 14f, 12f, Color(0xFF7B241C))
    // Glowing Red Eyes in dark helm
    px(10f, 7f, 1f, 1f, Color.Red)
    px(13f, 7f, 1f, 1f, Color.Red)
    // Sword
    px(18f, 4f, 2f, 20f, MetalSilver)
}

fun DrawScope.drawChaos() {
    val px = setupDrawHelpers()
    // Golden Demon Body
    px(8f, 8f, 10f, 14f, Color(0xFFD4AC0D))
    // Large Wings
    px(2f, 4f, 8f, 10f, Color(0xFF5D4037))
    px(16f, 4f, 8f, 10f, Color(0xFF5D4037))
    // Head & Horns
    px(10f, 2f, 6f, 6f, Color(0xFFD4AC0D))
    px(8f, 0f, 2f, 4f, Color.White)
    px(16f, 0f, 2f, 4f, Color.White)
    // Eyes
    px(11f, 4f, 1f, 1f, Color.Red)
    px(14f, 4f, 1f, 1f, Color.Red)
}

fun DrawScope.drawLich() {
    val px = setupDrawHelpers()
    // Skeletal Face
    px(10f, 4f, 6f, 6f, Color.White)
    px(11f, 6f, 1f, 1f, Color.Black)
    px(14f, 6f, 1f, 1f, Color.Black)
    // Purple Robes
    px(7f, 10f, 12f, 14f, Color(0xFF6C3483))
    // Decay detail
    px(8f, 12f, 10f, 2f, Color.DarkGray)
}

fun DrawScope.drawPirate() {
    val px = setupDrawHelpers()
    // Blue Headband
    px(8f, 3f, 8f, 3f, Color.Blue)
    // Face
    px(8f, 6f, 8f, 6f, Skin)
    px(9f, 8f, 1f, 1f, Color.Black)
    px(14f, 8f, 1f, 1f, Color.Black)
    // Green Shirt
    px(7f, 12f, 10f, 8f, Color(0xFF1B5E20))
    // Cutlass
    px(17f, 8f, 2f, 10f, MetalSilver)
    px(16f, 16f, 4f, 2f, Color.Black)
    // Boots
    px(8f, 20f, 3f, 4f, Color.Black)
    px(13f, 20f, 3f, 4f, Color.Black)
}

fun DrawScope.drawCockatrice() {
    val px = setupDrawHelpers()
    // Yellow/Brown Feathers
    px(8f, 8f, 10f, 10f, Color(0xFFD4AC0D))
    // Red Crest
    px(10f, 4f, 4f, 4f, Color.Red)
    // Beak
    px(18f, 10f, 3f, 2f, Color(0xFFFF9800))
    // Eyes
    px(14f, 8f, 1f, 1f, Color.Black)
    // Legs
    px(10f, 18f, 2f, 4f, Color(0xFFFF9800))
    px(14f, 18f, 2f, 4f, Color(0xFFFF9800))
}

fun DrawScope.drawKraken() {
    val px = setupDrawHelpers()
    // Blue Body
    px(8f, 4f, 10f, 10f, Color.Blue)
    // Tentacles
    repeat(4) { i ->
        px(6f + i * 3, 14f, 2f, 8f, Color.Cyan)
    }
    // Eyes
    px(10f, 8f, 2f, 2f, Color.Yellow)
    px(14f, 8f, 2f, 2f, Color.Yellow)
}

@Composable fun PixelWarrior(modifier: Modifier = Modifier) = Canvas(modifier) { drawWarrior() }
@Composable fun PixelMage(modifier: Modifier = Modifier) = Canvas(modifier) { drawBlackMage() }
@Composable fun PixelWhiteMage(modifier: Modifier = Modifier) = Canvas(modifier) { drawWhiteMage() }
@Composable fun PixelRedMage(modifier: Modifier = Modifier) = Canvas(modifier) { drawRedMage() }
@Composable fun PixelThief(modifier: Modifier = Modifier) = Canvas(modifier) { drawThief() }
@Composable fun PixelMonk(modifier: Modifier = Modifier) = Canvas(modifier) { drawMonk() }
@Composable fun PixelKnight(modifier: Modifier = Modifier) = Canvas(modifier) { drawKnight() }
@Composable fun PixelPaladin(modifier: Modifier = Modifier) = Canvas(modifier) { drawPaladin() }
@Composable fun PixelSummoner(modifier: Modifier = Modifier) = Canvas(modifier) { drawSummoner() }
@Composable fun PixelNinja(modifier: Modifier = Modifier) = Canvas(modifier) { drawNinja() }
@Composable fun PixelDragoon(modifier: Modifier = Modifier) = Canvas(modifier) { drawDragoon() }
@Composable fun PixelBard(modifier: Modifier = Modifier) = Canvas(modifier) { drawBard() }
@Composable fun PixelSamurai(modifier: Modifier = Modifier) = Canvas(modifier) { drawSamurai() }
@Composable fun PixelArcher(modifier: Modifier = Modifier) = Canvas(modifier) { drawArcher() }
@Composable fun PixelFreelancer(modifier: Modifier = Modifier) = Canvas(modifier) { drawFreelancer() }

@Composable fun PixelSlime(modifier: Modifier = Modifier) = Canvas(modifier) { drawSlime() }
@Composable fun PixelGoblin(modifier: Modifier = Modifier) = Canvas(modifier) { drawGoblin() }
@Composable fun PixelOrc(modifier: Modifier = Modifier) = Canvas(modifier) { drawOgre() }
@Composable fun PixelDemon(modifier: Modifier = Modifier) = Canvas(modifier) { drawChaos() }
@Composable fun PixelDragon(modifier: Modifier = Modifier) = Canvas(modifier) { drawDragon(Color.Red) }

@Composable fun HeroSprite(heroClass: HeroClass, modifier: Modifier = Modifier) = when(heroClass) {
  HeroClass.WARRIOR -> PixelWarrior(modifier)
  HeroClass.BLACK_MAGE -> PixelMage(modifier)
  HeroClass.WHITE_MAGE -> PixelWhiteMage(modifier)
  HeroClass.THIEF -> PixelThief(modifier)
  HeroClass.ARCHER -> PixelArcher(modifier)
  HeroClass.FREELANCER -> PixelFreelancer(modifier)
  HeroClass.MONK -> PixelMonk(modifier)
  HeroClass.KNIGHT -> PixelKnight(modifier)
  HeroClass.PALADIN -> PixelPaladin(modifier)
  HeroClass.RED_MAGE -> PixelRedMage(modifier)
  HeroClass.SUMMONER -> PixelSummoner(modifier)
  HeroClass.NINJA -> PixelNinja(modifier)
  HeroClass.DRAGOON -> PixelDragoon(modifier)
  HeroClass.BARD -> PixelBard(modifier)
  HeroClass.SAMURAI -> PixelSamurai(modifier)
}

@Composable fun EnemySprite(enemyName: String, modifier: Modifier = Modifier) = when {
  enemyName.contains("Slime", true) || enemyName.contains("Flan", true) -> PixelSlime(modifier)
  enemyName.contains("Goblin", true) -> PixelGoblin(modifier)
  enemyName.contains("Orc", true) || enemyName.contains("Ogre", true) -> PixelOrc(modifier)
  enemyName.contains("Demon", true) || enemyName.contains("Chaos", true) -> PixelDemon(modifier)
  enemyName.contains("Dragon", true) || enemyName.contains("Tiamat", true) -> PixelDragon(modifier)
  enemyName.contains("Wolf", true) -> Canvas(modifier) { drawWolf() }
  enemyName.contains("Sahagin", true) || enemyName.contains("Merman", true) -> Canvas(modifier) { drawSahagin() }
  enemyName.contains("Pirate", true) -> Canvas(modifier) { drawPirate() }
  enemyName.contains("Cockatrice", true) -> Canvas(modifier) { drawCockatrice() }
  enemyName.contains("Kraken", true) -> Canvas(modifier) { drawKraken() }
  enemyName.contains("Bomb", true) -> Canvas(modifier) { drawBomb() }
  enemyName.contains("Eye", true) -> Canvas(modifier) { drawEye() }
  enemyName.contains("Tonberry", true) -> Canvas(modifier) { drawTonberry() }
  enemyName.contains("Lich", true) -> Canvas(modifier) { drawLich() }
  enemyName.contains("Garland", true) || enemyName.contains("Knight", true) -> Canvas(modifier) { drawGarland() }
  else -> PixelSlime(modifier) // fallback
}

@Composable fun ImpactSparkEffect(spark: ImpactSpark, onDone: () -> Unit) {
    var progress by remember { mutableStateOf(0f) }
    LaunchedEffect(spark.id) {
        val start = System.currentTimeMillis()
        while (progress < 1f) {
            progress = ((System.currentTimeMillis() - start) / 400f).coerceIn(0f, 1f)
            delay(16)
        }
        onDone()
    }
    Canvas(
        Modifier
            .size(80.dp)
            .offset(spark.x.dp - 40.dp, spark.y.dp - 40.dp)
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val colors = listOf(Color.White, Color(0xFFFFDD00), Color(0xFFFF8800), Color(0xFFFF4400))
        repeat(8) { i ->
            val angle = (i * 45f) * (Math.PI / 180f).toFloat()
            val dist = progress * size.width * 0.45f
            val endX = cx + cos(angle) * dist
            val endY = cy + sin(angle) * dist
            val sparkAlpha = 1f - progress
            val sparkColor = colors[i % colors.size].copy(alpha = sparkAlpha)
            drawLine(sparkColor, Offset(cx, cy), Offset(endX, endY), strokeWidth = 3f)
            drawCircle(sparkColor, radius = 4f * (1f - progress), center = Offset(endX, endY))
        }
        drawCircle(Color.White.copy(alpha = (1f - progress) * 0.8f), radius = 20f * progress, center = Offset(cx, cy))
    }
}
