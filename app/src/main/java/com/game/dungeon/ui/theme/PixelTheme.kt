package com.game.dungeon.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.game.dungeon.R

// R.font.press_start_2p is expected to be present at res/font/press_start_2p.ttf
// val PixelFont = FontFamily(Font(R.font.press_start_2p))
val PixelFont = FontFamily.Default // Fallback for compilation

// Color palette
val BgDarkest = Color(0xFF0D0720)     // near-black purple — main background
val BgDark = Color(0xFF1A0A2E)        // dark purple — card backgrounds
val BgMedium = Color(0xFF2D1654)      // medium purple — elevated surfaces
val BgPanel = Color(0xFF1E1040)       // panel background
val GoldBright = Color(0xFFFFD700)    // primary gold — titles, highlights
val GoldDark = Color(0xFFAA8800)      // darker gold — borders, inactive
val GoldAccent = Color(0xFFFFAA00)    // warm gold — special abilities
val StoneGray = Color(0xFF4A4560)     // stone UI elements
val HpGreen = Color(0xFF22CC44)       // HP bar fill
val HpYellow = Color(0xFFCCCC22)      // HP bar warning
val HpRed = Color(0xFFCC2222)         // HP bar critical
val EnemyRed = Color(0xFFFF4444)      // enemy damage text
val HeroBlue = Color(0xFF4488FF)      // hero action text
val SystemCyan = Color(0xFF44CCCC)    // system/floor messages
val RarityCommon = Color(0xFF888888)
val RarityRare = Color(0xFF4488FF)
val RarityEpic = Color(0xFFAA44FF)
val RarityLegendary = Color(0xFFFFAA00)

// Text styles
val PixelTitle = TextStyle(fontFamily = PixelFont, fontSize = 24.sp, color = GoldBright, letterSpacing = 2.sp)
val PixelHeading = TextStyle(fontFamily = PixelFont, fontSize = 18.sp, color = GoldBright, letterSpacing = 1.sp)
val PixelBody = TextStyle(fontFamily = PixelFont, fontSize = 14.sp, color = Color.White, letterSpacing = 0.5.sp)
val PixelSmall = TextStyle(fontFamily = PixelFont, fontSize = 12.sp, color = Color.White, letterSpacing = 0.sp)
val PixelGold = TextStyle(fontFamily = PixelFont, fontSize = 14.sp, color = GoldBright, letterSpacing = 0.5.sp)
val PixelEnemy = TextStyle(fontFamily = PixelFont, fontSize = 14.sp, color = EnemyRed)
val PixelHero = TextStyle(fontFamily = PixelFont, fontSize = 14.sp, color = HeroBlue)
val PixelSystem = TextStyle(fontFamily = PixelFont, fontSize = 14.sp, color = SystemCyan)

private val PixelTypography = Typography(
    displayLarge = PixelTitle,
    headlineMedium = PixelHeading,
    bodyLarge = PixelBody,
    bodySmall = PixelSmall,
    labelSmall = PixelSmall
)

@Composable
fun PixelTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = PixelTypography,
        content = content
    )
}
