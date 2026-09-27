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

/**
 * Ultra-detail 64x64 pixel grid helper.
 * Provides 4x higher detail for authentic 16-bit JRPG character sprites.
 */
private fun DrawScope.px64(): (Float, Float, Float, Float, Color) -> Unit {
    val pixelSize = size.width / 64f
    return { x, y, w, h, color ->
        drawRect(
            color = color,
            topLeft = Offset(x * pixelSize, y * pixelSize),
            size = Size(w * pixelSize, h * pixelSize)
        )
    }
}

/**
 * Pixel Matrix Renderer.
 * Draws a 2D string matrix pixel-by-pixel with exact 1:1 scaling and centering.
 */
private fun DrawScope.drawPixelMatrix(
    matrix: Array<String>,
    palette: Map<Char, Color>
) {
    val rows = matrix.size
    val cols = matrix.maxOf { it.length }
    val tileSize = minOf(size.width / cols, size.height / rows)
    val offsetX = (size.width - cols * tileSize) / 2f
    val offsetY = (size.height - rows * tileSize) / 2f

    for (r in 0 until rows) {
        val rowStr = matrix[r]
        for (c in 0 until rowStr.length) {
            val char = rowStr[c]
            val color = palette[char] ?: continue
            drawRect(
                color = color,
                topLeft = Offset(offsetX + c * tileSize, offsetY + r * tileSize),
                size = Size(tileSize, tileSize)
            )
        }
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
    val matrix = arrayOf(
        "...KKKKKKK..K.K.", // 0
        "..KRRRRRRRKKRRK.", // 1
        ".KRRRRRRRRRRRRK.", // 2
        "KRRRRRRRRRRRRRK.", // 3
        ".KRRRRRRRRRRRRK.", // 4
        "KRRRRRRRRRRRRRK.", // 5
        ".KRRRRRRRRRRRRRK", // 6
        "..KRRRRRRRKKRRKK", // 7
        "..KRRRRPPKKRRK..", // 8
        "..KRRRPPPPPPK...", // 9
        "..KRRKPPPPPK....", // 10
        ".KRRRKPPPPKRRK..", // 11
        "KWRRRRKRRRRRK...", // 12
        "KRRRRKRRRPKRRK..", // 13
        "KPRRKRRRPPKRK...", // 14
        "KPPRKRRRPPK.....", // 15
        ".KRRRRRRRPPK....", // 16
        ".KKKKKKKKKKK....", // 17
        "..KWWWWWWK......", // 18
        "..KRRRRRK.......", // 19
        "..KRRRRRK.......", // 20
        "..KRRRRRK.......", // 21
        "..KRRRRRRK......", // 22
        "..KKKKKKK......."  // 23
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Black outline / pupil
        'R' to Color(0xFFD32F2F), // Crimson red hair & armor
        'P' to Color(0xFFF3C59D), // Peach skin tone
        'W' to Color(0xFFFFFFFF)  // White belt / shoulder highlight
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawThief() {
    val matrix = arrayOf(
        "........KKKKKKKK.KK...", // 0
        ".......KGGGGGGGGKHHDK.", // 1
        "......KGGGGGGGGGGHHHDK", // 2
        ".....KGGGGGGGGGGGHHHHD", // 3
        "..KK.KGGGGGGGGGHHHHHHD", // 4
        ".KGGKGGGGGGGGDSEPPPHD.", // 5
        "KGGGGGGGGGGGGSEPPPPD..", // 6
        ".KGGGKGGGGGGDPPPPPD...", // 7
        "..KKK.KGGGGGPPPPPD....", // 8
        "......KGEEEEEEEGK.....", // 9
        ".....KGGEEEEEEGGGK....", // 10
        "....KGGGEEEEEEEGGGK...", // 11
        "...KOOOGEEEEEEEGPPPK..", // 12
        "...KPPOGGGEEEGGGGPPPK.", // 13
        "...KPPPGGYYYYGGGGPPPK.", // 14
        "....KPGGGGGGGGGGGPPK..", // 15
        ".....KGGGGGGGGGGGKK...", // 16
        "......KGGGGGGGGGK.....", // 17
        "......KGGGGKGGGGK.....", // 18
        "......KPPPK.KPPPK.....", // 19
        ".....KGGGGK.KGGGGK....", // 20
        ".....KOOOK...KOOOK....", // 21
        ".....KOOOK...KOOOK....", // 22
        "....KGGGGK...KGGGGK...", // 23
        "....KKKKKK...KKKKKK..."  // 24
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline / pupil
        'G' to Color(0xFF27AE60), // Green bandanna & tunic
        'E' to Color(0xFF1E8449), // Dark green tunic shadow
        'H' to Color(0xFFD4AC0D), // Tan/brown hair
        'D' to Color(0xFF9A7D0A), // Dark hair shadow
        'P' to Color(0xFFF3C59D), // Peach skin
        'S' to Color(0xFFFFFFFF), // White sclera
        'O' to Color(0xFFE67E22), // Orange pauldron & boots
        'Y' to Color(0xFFF1C40F)  // Gold belt
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawMonk() {
    val matrix = arrayOf(
        ".......KKKKKK.....", // 0
        "......KHHHHHHK....", // 1
        ".....KHHHHHHHHK...", // 2
        "....KHHHHHHHHHHK..", // 3
        "....KHHCCCCCHHHK..", // 4
        "....KHHCYYYYCCK...", // 5
        "....KCCCYYYYYCK...", // 6
        "....KYYYYYKKKYYK..", // 7
        "....KYYYYYYKKYK...", // 8
        "....KYYYYYYYYK....", // 9
        "...KCCCYYYYYYKK...", // 10
        "..KYYYCCCYYYYYYYYK", // 11
        ".KYYYYYCCBCCCYYYYK", // 12
        ".KYYYYYCCCCCCYYYYK", // 13
        ".KYYYYGGCCCCCCYYK.", // 14
        "..KYYGGGGCCCCK....", // 15
        "...KGGGGGCCCK.....", // 16
        "....KKKKKCCCK.....", // 17
        "......KCCCCCK.....", // 18
        "......KCCCCCK.....", // 19
        "......KCCCCCK.....", // 20
        "......KCCCCCK.....", // 21
        "......KCCCKCK.....", // 22
        "......KCCCKCK.....", // 23
        "......KKKKKKK....."  // 24
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline / pupil / belt
        'H' to Color(0xFFA67C52), // Brown hair
        'D' to Color(0xFF6B4C28), // Dark hair shadow
        'Y' to Color(0xFFF7D038), // Yellow skin
        'G' to Color(0xFFD39818), // Gold/yellow muscle shading
        'C' to Color(0xFF4FB4E8), // Cyan headband / trousers / gi
        'B' to Color(0xFF2B72A8)  // Dark cyan shadow
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawWhiteMage() {
    val matrix = arrayOf(
        "..KKKKKKKKK..", // 0
        ".KWWWWWWWWWK.", // 1
        ".KWWWWWWWWWK.", // 2
        "KWWWWWWWWWWWK", // 3
        "KWWWWWWWWWWWK", // 4
        "KWWWWWWWWWWWK", // 5
        "KWWWWWWWHHHHK", // 6
        "KWWWWWWKHHHHK", // 7
        "KWWWWWKHPKKHK", // 8
        "KWWWWWKPPBKPK", // 9
        "KWWWWWKPPPPPK", // 10
        "KWWWWWKPPPPPK", // 11
        ".KWWWWKKKKKKK", // 12
        ".KWWWKWWWWWWK", // 13
        ".KWWWKWWWRWWK", // 14
        ".KWWWKWRRWWWK", // 15
        ".KWWWKRRWWRWK", // 16
        ".KWWWKRWRRRWK", // 17
        ".KWWWKRRRRRRK", // 18
        ".KWWWKRRRRRRK", // 19
        ".KWWWKRRRRRRK", // 20
        ".KRRRRRRRRRRK", // 21
        "KRRRRRRRRRRRK", // 22
        "KKKKKKKKKKKKK", // 23
        "KKKKKKKKKKKKK"  // 24
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Black outline / pupil / chin line
        'W' to Color(0xFFFFFFFF), // White hood / robe
        'H' to Color(0xFF5A3A22), // Brown hair bangs
        'P' to Color(0xFFF3C59D), // Peach skin face
        'B' to Color(0xFF2C3E80), // Blue eye pupil
        'R' to Color(0xFFC02A2A)  // Red saw-tooth trim & cape
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawBlackMage() {
    val matrix = arrayOf(
        "...KK............", // 0
        "..KDYK...........", // 1
        "..KDYK...........", // 2
        "..KDYYK..........", // 3
        "...KDYYK.........", // 4
        "...KDYYYK........", // 5
        "...KDYYYK........", // 6
        "..KDDYYYYYK......", // 7
        ".KDDYYYYYYYKKKKK.", // 8
        ".KYYYYYYYYYYYYYYK", // 9
        "KDYYYYYYYYYYYYYYKK", // 10
        "KDDDYYYYYKKKKKK..", // 11
        ".KDDYYYYKKKKKKK..", // 12
        ".KBKKKKKKKEEKKK..", // 13
        "..KBBKKKKKEEKKK..", // 14
        "..KBBBKKKKKKKKKK.", // 15
        "..KBBBKBKKBBBBBK.", // 16
        "..KBBBKBKKBBBBBK.", // 17
        "..KBBBBBKBBBYYYK.", // 18
        "..KBBBBBKBBBYYYK.", // 19
        "..KBBBBBKBBBBKKB.", // 20
        "..KBBBBBBBBBBKKB.", // 21
        "..KBBBBBBBBBKKKB.", // 22
        ".KBKBBBBBBBBKKKB.", // 23
        "KBBKBBBBBBBBBBK..", // 24
        "KKKKKKKKKKKKKKK.."  // 25
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Black outline & shadow face
        'Y' to Color(0xFFF5C542), // Light yellow hat, glove, patch
        'D' to Color(0xFFC78C16), // Dark gold hat shadow
        'E' to Color(0xFFFFDF22), // Glowing yellow eyes
        'B' to Color(0xFF389CE3)  // Robe cyan blue
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawRedMage() {
    val matrix = arrayOf(
        "......KWWKRRKK....", // 0
        "....KWWWKRRRRRK...", // 1
        "...KWWWWKRRRRRRK..", // 2
        "..KWWWWKKRRRRRRRK.", // 3
        ".KWWWWKRRRRRRRRRK.", // 4
        "KRRRRRRRRRRRRRRRK.", // 5
        ".KKKKRRRRRRRRKK...", // 6
        "..KWWWWWWKPPK.....", // 7
        "..KWWWWWWKPEPK....", // 8
        ".KWWWWWWKPPPPK....", // 9
        ".KRRRRRRKKKKKK....", // 10
        ".KRRRRRKRWWWWK....", // 11
        "KRRRRRKRRRRPPWK...", // 12
        "KRRRRRKRRRKWWWWK..", // 13
        "KRRRRRKRRPKWKWWK..", // 14
        ".KRRRRKRRKKWWWWK..", // 15
        ".KRRRRRRRPKWWWWK..", // 16
        "..KRRRRRRPKKKKK...", // 17
        "..KWWWWWWK........", // 18
        "..KRRRRRRK........", // 19
        "..KRRRRRRK........", // 20
        "..KRRRRRRK........", // 21
        "..KRRRRRRRK.......", // 22
        "..KKKKKKKKK......."  // 23
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline / pupil
        'R' to Color(0xFFD32F2F), // Crimson red cap, cape, tunic
        'D' to Color(0xFF8B0000), // Dark red shadow
        'W' to Color(0xFFFFFFFF), // White feather, collar, belt
        'P' to Color(0xFFF3C59D), // Peach skin
        'E' to Color(0xFF17202A)  // Dark pupil
    )

    drawPixelMatrix(matrix, palette)
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
    val matrix = arrayOf(
        ".........KKK....", // 0
        ".......KLLLLK...", // 1
        ".....KLLLLLLLRK.", // 2
        "...KLLLLLLYYYRK.", // 3
        "..KLLLLLLYYYYOK.", // 4
        ".KLLLLLLHYYPPPGK", // 5
        ".KLLLLLHHPPPSPGK", // 6
        "..KLLLLHHPPPSPPK", // 7
        "..KKLLLLHHPPPSKK", // 8
        ".KYYYLLLHBBBFFFK", // 9
        "KYYYYYKHHBFFFCCK", // 10
        "KYYYYYKFFFFCCCCK", // 11
        "KYYRYYKFFFFFFKKK", // 12
        ".KYYYYKFFFFFBFFK", // 13
        "..KYYK.KPFBFFFFK", // 14
        "..KKK..KPPBFFFFK", // 15
        ".KYYYK.KPBBBFFFK", // 16
        "KYYYYK.KBBBBBFK.", // 17
        "KYYYMK..KBBBBFK.", // 18
        "KYYYMMK.KBBBBK..", // 19
        "KYYYYMMK.KBBBK..", // 20
        "KYYYYRRKK.KBBK..", // 21
        ".KYYYYRRK..KBK..", // 22
        "..KKKKKKK...KK.."  // 23
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'L' to Color(0xFFB8A9E8), // Light lavender hair highlight
        'H' to Color(0xFF8E7CC3), // Medium purple hair base
        'D' to Color(0xFF5B4A9C), // Dark purple hair shadow
        'Y' to Color(0xFFF4D03F), // Gold armor / crown
        'O' to Color(0xFFD4AC0D), // Dark gold shadow
        'A' to Color(0xFF9A7D0A), // Bronze shadow
        'P' to Color(0xFFF3C59D), // Peach skin
        'S' to Color(0xFFD49B72), // Skin shadow
        'G' to Color(0xFF2ECC71), // Green eye
        'W' to Color(0xFFFFFFFF), // Eye sclera
        'F' to Color(0xFFFFFFFF), // White armor / tabard
        'E' to Color(0xFFD5D8DC), // Silver tabard shadow
        'B' to Color(0xFF2980B9), // Royal blue collar / sash
        'C' to Color(0xFF5DADE2), // Cyan chest accent
        'R' to Color(0xFFC0392B), // Crimson jewel / cape / boot trim
        'M' to Color(0xFF7B241C)  // Dark red cape shadow
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawNinja() {
    val matrix = arrayOf(
        "..K.....KKKKKK..", // 0
        ".KCK...KCCCCCCK.", // 1
        ".KCK..KCNNNNCCK.", // 2
        "KKCCKKCNNNNNNNK.", // 3
        "KCCCCLLLNNNNTSK.", // 4
        ".KCCCCCNTTTTTSK.", // 5
        ".KCCCCCCSTTTTSK.", // 6
        "KCCCCCCCCCNCCKK.", // 7
        "KCNCCCCCCVKKK...", // 8
        "KCNCCCCCCLKCK...", // 9
        "KCNNNNCCCCKCK...", // 10
        ".KNNNNNCCCKCK...", // 11
        ".KCNNNNNCC.KKK..", // 12
        ".KCNLLNNCCCKCK..", // 13
        ".KCNLLNNCCCKCK..", // 14
        "..KCNNCCCCCTCK..", // 15
        "..KCCCCCCCCTCK..", // 16
        "..KCCCCCCC.KKK..", // 17
        ".KCKCCCCCCC.....", // 18
        ".KTTK.KCNNC.....", // 19
        ".KTTK.KCNLLK....", // 20
        "..KK..KCCTTK....", // 21
        "......KCCLLK....", // 22
        ".....KLLLLLK....", // 23
        ".....KKKKKKK....", // 24
        "................"  // 25
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark navy/black outline
        'C' to Color(0xFF2C302E), // Dark charcoal cowl / Ninja suit base
        'N' to Color(0xFF3B5968), // Slate blue Ninja armor / garment
        'L' to Color(0xFF5D9CEC), // Light cyan-blue highlight
        'V' to Color(0xFF8E44AD), // Dark purple collar accent
        'S' to Color(0xFFF3C59D), // Peach skin
        'T' to Color(0xFFB57E3E)  // Tan / golden-brown mask trim, elbow guard, belt & boot accent
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawDragoon() {
    val matrix = arrayOf(
        "..KK...........", // 0
        ".KLK...KK......", // 1
        "KLPK..KPBK.....", // 2
        ".KLPKKLLPPK....", // 3
        "..KLPLLWPPK....", // 4
        ".KLPLLLLPPK....", // 5
        ".KPLLYYPBK.....", // 6
        "KLPPYYBBK...K..", // 7
        "KLPKKKKKKKKPLK.", // 8
        ".KLPKHHHK.KPLK.", // 9
        ".KPBKSSK.KPLK..", // 10
        ".KPBKKKK.KK....", // 11
        "KLPKPPBK.KPPLK.", // 12
        "KLPKBPBK.KSSK..", // 13
        "KPPKBPBK.KPSSK.", // 14
        "KRRKBPSSK.KPPK.", // 15
        ".KKBBSSKKK.....", // 16
        "..KKKPBKK......", // 17
        "...KLPRBK......", // 18
        "...KLPRBK......", // 19
        "...KLPBBK......", // 20
        "...KLLPBKK.....", // 21
        "...KLLPBPK.....", // 22
        "...KPPPBYYK....", // 23
        "...KYYKYYYK....", // 24
        "...KKKKKKKK...."  // 25
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark navy/black outline
        'W' to Color(0xFFFFFFFF), // Specular white highlight
        'L' to Color(0xFF7A7EE2), // Light lavender/purple armor highlight
        'P' to Color(0xFF504EB9), // Medium purple/indigo armor base
        'B' to Color(0xFF312E83), // Dark purple/indigo armor shadow
        'Y' to Color(0xFFF1C40F), // Gold/yellow emblem & boot trim
        'S' to Color(0xFFF3C59D), // Peach skin
        'H' to Color(0xFFB86C35), // Visor skin shadow
        'R' to Color(0xFFD32F2F)  // Red ribbon / belt accent
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawBard() {
    val matrix = arrayOf(
        "...KFFK...............", // 0
        "..KFEEFK..............", // 1
        ".KFEEFK..KKKKKK.......", // 2
        ".KFFK..KLGGGGGGK......", // 3
        "..KK.KLGGGGGGGGGGK....", // 4
        "....KLGGGGGGGGGGGGGK..", // 5
        "...KDDLLGGGGGGGGGGGGK.", // 6
        "....KAHHHHGGGGGGGGGGGK", // 7
        "...KAHHHHHHHPPPPPSSK..", // 8
        "...KAHHHHHHHPPPPPPSK..", // 9
        "...KAHHHHHHHPPPPPSK...", // 10
        "...KAHHHHHHPPPPK.KKK..", // 11
        "..KRKWWWWWPPPPK.KUUNK.", // 12
        ".KRRMKWWWKKKKK.KUUOONK", // 13
        "KRRRRMKWWKGK..KUUOONK.", // 14
        "KRRRRRMKGGGGK.KUUONK..", // 15
        "KRRRRRRMKGGGKKUONK....", // 16
        "KRRRRRRRMKKKKONK......", // 17
        "KRRRRRRRYMKOONK.......", // 18
        "KRRRRRRRYMKOONK.......", // 19
        "KYYYRRRRYMKOONK.......", // 20
        ".KKKYRRRRMKOONK.......", // 21
        "....KBBBBBBKKOOK......", // 22
        "....KBBBBBBK...K......", // 23
        "....KBBK.KBBK.........", // 24
        "....KKK..KKK.........."  // 25
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'F' to Color(0xFFFFFFFF), // White feather plume
        'E' to Color(0xFFBDC3C7), // Silver feather shadow
        'G' to Color(0xFF488A72), // Forest green hat & tunic
        'L' to Color(0xFF63B395), // Light green hat highlight
        'D' to Color(0xFF2E5B4B), // Dark green hat shadow
        'H' to Color(0xFFD7A15C), // Sandy blonde hair base
        'A' to Color(0xFF996B30), // Blonde hair shadow
        'P' to Color(0xFFF7D0B5), // Peach skin highlight
        'S' to Color(0xFFD49B72), // Skin shadow
        'W' to Color(0xFFFFFFFF), // White collar shirt
        'R' to Color(0xFF9E2A4B), // Crimson cape base
        'M' to Color(0xFF6B1D32), // Dark crimson cape shadow
        'Y' to Color(0xFFE5C158), // Gold trim on cape
        'U' to Color(0xFFC29B38), // Lute wood highlight
        'O' to Color(0xFF8C6228), // Lute wood base
        'N' to Color(0xFF5A3C16), // Lute wood shadow
        'B' to Color(0xFF512E1B), // Brown boots
        'C' to Color(0xFF331B0E)  // Dark boot sole
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawSummoner() {
    val matrix = arrayOf(
        ".....KKKKK......", // 0
        "...KGGEEEEGK....", // 1
        "..KGEEYRYEEG....", // 2
        ".KGEEEYRYEEEGK..", // 3
        ".KGGEEEEYEEEGGK.", // 4
        ".KGEEEEPPWBEGK..", // 5
        "..KGEEEEPPBEEGK.", // 6
        "..KGEEEPPPPPEDK.", // 7
        "...KEEPEPPPEEDK.", // 8
        "...KYEPEPPPEEDK.", // 9
        "...KEEPYPPPEEDK.", // 10
        ".KGEEEEPPPEEEDK.", // 11
        ".KGEEEEPPEEEEDK.", // 12
        ".KDEEEEPPEEEEDK.", // 13
        ".KDEEEEPCCEEEDK.", // 14
        ".KDEEEEPCCEEEDK.", // 15
        ".KDEEEP.CCEEEDK.", // 16
        ".KDEEP..CCEEEDK.", // 17
        ".KDEP...CCEEEK..", // 18
        ".KDEP...CCEEEK..", // 19
        ".KDEK...CCEEEK..", // 20
        ".KYOK...CCEEEK..", // 21
        ".KYOK...CCEEEDK.", // 22
        ".KEEEK..KCCCCK..", // 23
        "..KKKK...KKKK..."  // 24
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark navy/black outline
        'G' to Color(0xFFA8F080), // Light lime hair highlight
        'E' to Color(0xFF4CB33D), // Medium emerald green hair & tunic base
        'D' to Color(0xFF216E1A), // Dark green hair & dress shadow
        'Y' to Color(0xFFF1C40F), // Gold tiara & belt trim
        'R' to Color(0xFFE74C3C), // Red tiara jewel
        'P' to Color(0xFFF3C59D), // Peach skin
        'W' to Color(0xFFFFFFFF), // White eye sclera
        'B' to Color(0xFF2C3E80), // Dark blue eye iris
        'C' to Color(0xFF17202A), // Dark charcoal inner dress folds shadow
        'O' to Color(0xFFD4AC0D)  // Gold shoe tip
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawSamurai() {
    val matrix = arrayOf(
        ".....KKKKK......", // 0
        "...KKHHHHHKK....", // 1
        "..KHHHHHHHHHK...", // 2
        ".KHHHHHSSPPPK...", // 3
        ".KHHHHHSSPPPWIK.", // 4
        "KHHHHHHSSSSSSSK.", // 5
        "KHHOHHHHSSSSSPSK", // 6
        "KHHOHHHHHSSSSSK.", // 7
        "..KKHHHHHHSSSSK.", // 8
        "..KBBBBBBBKKK...", // 9
        ".KBLBBBBBBBGK..K", // 10
        "KBBLLBBBBBBGK.KK", // 11
        "KPPPPBBBBBBGK.KP", // 12
        "KPPPPPSBBBGKK.KP", // 13
        "KPPPPPSSBBGK.KKP", // 14
        ".KPPPS.KBBGK.KP.", // 15
        "..KKK..KBBGK.KK.", // 16
        "..KBK..KBOGK....", // 17
        ".KBBK.KBBBGK....", // 18
        ".KBBK.KBBBLGK...", // 19
        ".KBLK.KBBBLGK...", // 20
        ".KBLK.KBBBBLGK..", // 21
        "KBBLLK.KBBBLGK..", // 22
        "KKKKKK.KKKKKKK.."  // 23
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'H' to Color(0xFF2C3E50), // Dark charcoal hair
        'O' to Color(0xFFD4AC0D), // Gold tie & belt sash
        'P' to Color(0xFFF3C59D), // Peach skin highlight
        'S' to Color(0xFFD49B72), // Skin shadow
        'W' to Color(0xFFFFFFFF), // Eye sclera
        'I' to Color(0xFF5B4A9C), // Purple eye pupil
        'B' to Color(0xFF2980B9), // Steel blue samurai armor
        'L' to Color(0xFF5DADE2), // Light steel blue highlight
        'G' to Color(0xFFA2D9CE)  // Light cyan/sage trim
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawFreelancer() {
    val matrix = arrayOf(
        ".......DH..D......", // 0
        "......DHHHDH......", // 1
        ".....DHHHHHHHD....", // 2
        "..DDHHHHHHHHHHHHD.", // 3
        "DHHHHHHHHHHHHHHHD.", // 4
        "..DHHHHHHHHHHHHHD.", // 5
        "..DHHHHHHDPPPPPPD.", // 6
        "..DHHHHHHPPPPPPPK.", // 7
        "..DDHHHHHPPPDSEPK.", // 8
        "...DDHHHHPPPDSEPPK", // 9
        "...DDHHHHHPPPPPPK.", // 10
        "....NBBBGGGGGGGGK.", // 11
        "...KNBBBNVVVVVVGK.", // 12
        "...KNBBNNVVVVVVGK.", // 13
        "...KNBBNNVGGVVVGK.", // 14
        "...KPPPNGGGGGGGPK.", // 15
        "...KPPPNGGGGGGGPPK", // 16
        "...KPPPGGGGGGGPPK.", // 17
        "...KKKKGGGGGGGKK..", // 18
        ".....KKTTTTTTTTK..", // 19
        ".....KTTTMTTTTTK..", // 20
        ".....KTTTMTTTTTK..", // 21
        ".....KTTTOOOOOK...", // 22
        ".....KKKKKKKKKKK.."  // 23
    )

    val palette = mapOf(
        'K' to Color(0xFF281E15), // Dark outline / pupil
        'D' to Color(0xFF6B2412), // Dark brown hair shadow
        'H' to Color(0xFF9E4023), // Messy brown hair base
        'P' to Color(0xFFEE7652), // Peach skin tone
        'S' to Color(0xFFFFFFFF), // White sclera
        'E' to Color(0xFF281E15), // Dark pupil
        'N' to Color(0xFF384A6E), // Dark blue sleeve shadow
        'B' to Color(0xFF8BA5D2), // Light blue sleeve
        'G' to Color(0xFF5B5A50), // Dark grey vest shadow
        'V' to Color(0xFF959385), // Grey vest
        'M' to Color(0xFF7E7761), // Dark tan trouser shadow
        'T' to Color(0xFFC0B89C), // Tan trousers
        'O' to Color(0xFF5A3920)  // Brown boots
    )

    drawPixelMatrix(matrix, palette)
}

// --- MONSTER SPRITES (100% PIXEL MATRIX ARCHITECTURE) ---

/**
 * Monster Pixel Matrix Renderer.
 * Mirrors the matrix horizontally (facing LEFT) so monsters face the heroes who face RIGHT in combat.
 */
private fun DrawScope.drawMonsterMatrix(
    matrix: Array<String>,
    palette: Map<Char, Color>
) {
    val flippedMatrix = matrix.map { it.reversed() }.toTypedArray()
    drawPixelMatrix(flippedMatrix, palette)
}

fun DrawScope.drawGoblin() {
    val matrix = arrayOf(
        ".....KKKKK......", // 0
        "....KRRRRRK.....", // 1
        "...KRRRRRRRK....", // 2
        "..KRRRRRRRRRK...", // 3
        "..KGEEGGGGGGK...", // 4
        ".KGEEEEEGGGGK...", // 5
        ".KGEEEEEEGYSK...", // 6
        ".KGGGEEEEGYSK...", // 7
        "..KGGGGGGGGK....", // 8
        "...KGGGGGGK.KK..", // 9
        "..KGGGRRGGGKKSK.", // 10
        ".KGGGRRRRGGGKSK.", // 11
        ".KGGGRRRRGGGKSK.", // 12
        ".KGGGGRRGGGK.KK.", // 13
        "..KGGGGGGGGK....", // 14
        "..KGGGKKGGGK....", // 15
        "..KGGK..KGGK....", // 16
        "..KKK....KKK...."  // 17
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'G' to Color(0xFF27AE60), // Green skin base
        'E' to Color(0xFF196F3D), // Dark green skin shadow
        'R' to Color(0xFFE74C3C), // Crimson hat & loincloth
        'Y' to Color(0xFFF1C40F), // Yellow eye
        'S' to Color(0xFFBDC3C7), // Silver scimitar / white sclera
        'W' to Color(0xFFFFFFFF)  // White
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawWolf() {
    val matrix = arrayOf(
        ".......KKKK.........", // 0
        "......KGGGGK...KK...", // 1
        ".....KGGGGGGK.KGGK..", // 2
        "....KGGGGGGGGKGGGK..", // 3
        "...KGGGGGGGGGGGGGK..", // 4
        "..KGGGGGGGGGGGRYSK..", // 5
        ".KDDGGGGGGGGGGWWSK..", // 6
        "KDDDDGGGGGGGGGGGGK..", // 7
        "KDDDDDDGGGGGGGGGGK..", // 8
        ".KDDDDDDGGGGKKKK....", // 9
        "..KDDDDDDGGK........", // 10
        "...KDDGGGGGK........", // 11
        "...KGGK..KGGK.......", // 12
        "...KGGK..KGGK.......", // 13
        "...KDDK..KDDK.......", // 14
        "...KKK....KKK......."  // 15
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'G' to Color(0xFF5DADE2), // Slate blue/grey fur base
        'D' to Color(0xFF2874A6), // Dark slate blue shadow
        'R' to Color(0xFFE74C3C), // Red eye
        'Y' to Color(0xFFF1C40F), // Yellow iris
        'S' to Color(0xFFFFFFFF), // White teeth / snout highlight
        'W' to Color(0xFFF1948A)  // Pink open tongue/mouth
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawPirate() {
    val matrix = arrayOf(
        ".....KKKKKK.......", // 0
        "....KRRRRRRK.KK...", // 1
        "...KRRRRRRRRKRRK..", // 2
        "..KRRRRRRRRRRRRK..", // 3
        "..KRRRRRRRRRRRRK..", // 4
        "..KRRRRRRRRKKKK...", // 5
        "..KPPPPPPPSPK.....", // 6
        "..KPPEPPPSPBK.....", // 7
        "..KPPPPPPPPPK.....", // 8
        "..KPPPPPPPEPK.....", // 9
        "..KPPPPPPPPPK.....", // 10
        "...KBBBBBBBBK.KK..", // 11
        "..KBBBBBBBBBBKSK..", // 12
        ".KBBBBBBBBBBBBKSK.", // 13
        ".KBBBBBOOBBBBBKSK.", // 14
        ".KBBBBBOOBBBBBKKK.", // 15
        "..KBBBBBBBBBBK....", // 16
        "..KBBBBBBBBBBK....", // 17
        "...KBBBK.KBBBK....", // 18
        "...KOOOK.KOOOK....", // 19
        "...KOOOK.KOOOK....", // 20
        "...KKKKK.KKKKK...."  // 21
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'R' to Color(0xFFC0392B), // Dark red bandanna
        'P' to Color(0xFFF3C59D), // Peach skin
        'E' to Color(0xFF17202A), // Eye patch / pupil
        'S' to Color(0xFFFFFFFF), // White eye sclera / cutlass blade
        'B' to Color(0xFF2980B9), // Blue vest & pants
        'O' to Color(0xFF7E5109)  // Brown belt & boots
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawOgre() {
    val matrix = arrayOf(
        ".......KKKKKK.......", // 0
        "......KYYYYYYK......", // 1
        ".....KYYYYYYYYK.....", // 2
        "....KYYYYYYYYYYK....", // 3
        "....KYYEEYYEEYYK....", // 4
        "....KYYYYYYYYYYK....", // 5
        "....KYYYYWWYYYYK.KK.", // 6
        "....KYYYYYYYYYYKKBK.", // 7
        "...KYYYYYYYYYYYYKBK.", // 8
        "..KYYYYRRRRRYYYYKBK.", // 9
        ".KYYYYYRRRRRYYYYKBK.", // 10
        ".KYYYYYRRRRRYYYYKBK.", // 11
        ".KYYYYYRRRRRYYYYKBK.", // 12
        "..KYYYYRRRRRYYYYKBK.", // 13
        "..KYYYYYYYYYYYYYKBK.", // 14
        "...KYYYYYYYYYYYYKKK.", // 15
        "....KYYYYYYYYYYK....", // 16
        ".....KYYYYYYYYK.....", // 17
        ".....KYYYK.KYYYK....", // 18
        ".....KYYYK.KYYYK....", // 19
        ".....KDDDK.KDDDK....", // 20
        ".....KKKKK.KKKKK...."  // 21
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'Y' to Color(0xFFF1C40F), // Gold/tan skin base
        'D' to Color(0xFFB7950B), // Dark gold skin shadow
        'R' to Color(0xFFC0392B), // Red loincloth
        'E' to Color(0xFF17202A), // Dark eyes
        'W' to Color(0xFFFFFFFF), // White fangs
        'B' to Color(0xFF6E2C00)  // Brown wooden club
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawEye() {
    val matrix = arrayOf(
        ".KK......KK...KK..", // 0
        "KWWK....KWWK.KWWK.", // 1
        "KPPK....KPPK.KPPK.", // 2
        ".KPPK...KPPK.KPPK.", // 3
        "..KPPKKKPPPPKKPK..", // 4
        "...KPPPPPPPPPPK...", // 5
        "..KPPPPPPPPPPPPK..", // 6
        ".KPPPPWWWWWWPPPPK.", // 7
        ".KPPPWWWWWWWWPPPK.", // 8
        "KPPPWWWWBBWWWWPPPK", // 9
        "KPPPWWWWBBWWWWPPPK", // 10
        ".KPPPWWWWWWWWPPPK.", // 11
        ".KPPPPWWWWWWPPPPK.", // 12
        "..KPPPPPPPPPPPPK..", // 13
        "...KPPPPPPPPPPK...", // 14
        "..KPPKKKPPPPKKPK..", // 15
        ".KPPK...KPPK.KPPK.", // 16
        "KWWK....KWWK.KWWK.", // 17
        ".KK......KK...KK.."  // 18
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'P' to Color(0xFF8E44AD), // Purple orb body
        'W' to Color(0xFFFFFFFF), // White eye sclera / eyestalk eyes
        'B' to Color(0xFF2C3E50)  // Dark eye pupil
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawSlime() {
    val matrix = arrayOf(
        "......KKKKKK......", // 0
        "....KRRRRRRRRK....", // 1
        "...KRRRRRRRRRRRK..", // 2
        "..KRRRRRRRRRRRRRK.", // 3
        ".KRRRRRRRRRRRRRRRK", // 4
        ".KRRRRYYRRRRYYRRRK", // 5
        "KRRRRRYYRRRRYYRRRK", // 6
        "KRRRRRKKRRRRKKRRRK", // 7
        "KRRRRRRRRRRRRRRRRK", // 8
        "KRRRRRRRRRRRRRRRRK", // 9
        ".KRRRRRRRRRRRRRRRK", // 10
        ".KRRRRRRRRRRRRRRRK", // 11
        "..KRRRRRRRRRRRRRK.", // 12
        "...KDDDDDDDDDDDK..", // 13
        "....KKKKKKKKKKK..."  // 14
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline / pupil
        'R' to Color(0xFFE74C3C), // Crimson red slime base
        'D' to Color(0xFF922B21), // Dark red slime shadow
        'Y' to Color(0xFFF1C40F)  // Yellow glowing eyes
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawBomb() {
    val matrix = arrayOf(
        "...K...KK...K.....", // 0
        "..KYK.KYYK.KYK....", // 1
        ".KYYK.KYYK.KYYK...", // 2
        "..KK.KKKKKK.KK....", // 3
        "....KRRRRRRK......", // 4
        "...KRRRRRRRRK.....", // 5
        "..KRRRRRRRRRRK....", // 6
        ".KRRRYYRRRRYYRRK..", // 7
        ".KRRRYYRRRRYYRRK..", // 8
        ".KRRRKKRRRRKKRRK..", // 9
        ".KRRRRRRRRRRRRRK..", // 10
        ".KRRRKWWWWWWKRRK..", // 11
        "..KRRKWWWWWWKRRK..", // 12
        "..KRRRKKKKKKRRRK..", // 13
        "...KRRRRRRRRRRK...", // 14
        "....KDDDDDDDDK....", // 15
        ".....KKKKKKKK....."  // 16
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'R' to Color(0xFFE74C3C), // Crimson bomb body
        'D' to Color(0xFF922B21), // Dark red bomb shadow
        'Y' to Color(0xFFF39C12), // Orange/yellow flame sparks & eyes
        'W' to Color(0xFFFFFFFF)  // White teeth grin
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawSahagin() {
    val matrix = arrayOf(
        "......KKKK........", // 0
        ".....KCCCCK...K...", // 1
        "....KCCCCCCK.KSK..", // 2
        "...KCCCCCCCSKSKSK.", // 3
        "..KCCCCCCCCCSKSKSK", // 4
        ".KCCCCCRYSCCCSKSK.", // 5
        ".KCCCCCCWSCCCCKSK.", // 6
        "..KCCCCCCCCCCCKSK.", // 7
        "...KCCCCCCCCCCKSK.", // 8
        "..KCCCCCCBCCCCKSK.", // 9
        ".KCCCCCCBBBCCCKSK.", // 10
        ".KCCCCCCBBBCCCKSK.", // 11
        ".KCCCCCCBBBCCCKSK.", // 12
        "..KCCCCCCBCCCCKSK.", // 13
        "...KCCCCCCCCCCKSK.", // 14
        "....KCCCCCCCCCKKK.", // 15
        ".....KCCCCKCCCK...", // 16
        ".....KCCCK.KCCCK..", // 17
        ".....KCCCK.KCCCK..", // 18
        "....KCCCCK.KCCCCK.", // 19
        "....KKKKKK.KKKKKK."  // 20
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'C' to Color(0xFF1ABC9C), // Cyan aquatic scales
        'B' to Color(0xFF16A085), // Dark cyan shading
        'R' to Color(0xFFE74C3C), // Red eye
        'Y' to Color(0xFFF1C40F), // Yellow iris
        'S' to Color(0xFFBDC3C7), // Silver trident
        'W' to Color(0xFFFFFFFF)  // White fins/sclera
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawCockatrice() {
    val matrix = arrayOf(
        "......KRRK........", // 0
        ".....KRRRRK.......", // 1
        "....KYYYYYYK......", // 2
        "...KYYYYYYYYK.....", // 3
        "..KYYYYYRYWSK.....", // 4
        "..KYYYYYYYYWWK....", // 5
        "..KYYYYYYYYKK.....", // 6
        "...KYYYYYYK.......", // 7
        "....KYYYYYK.......", // 8
        ".KK.KYYYYYYK......", // 9
        "KGK.KYYYYYYYK.....", // 10
        "KGGK.KYYYYYYK.....", // 11
        "KGGGKKYYYYYYK.....", // 12
        ".KGGGYYYYYYYK.....", // 13
        "..KGGYYYYYYYK.....", // 14
        "...KKYYYYYYK......", // 15
        "....KYYYYYYK......", // 16
        "....KYYKKYYK......", // 17
        "....KYYK.KYYK.....", // 18
        "....KOOK.KOOK.....", // 19
        "....KKKK.KKKK....."  // 20
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'Y' to Color(0xFFF1C40F), // Gold body feathers
        'R' to Color(0xFFE74C3C), // Red rooster crest & eye
        'S' to Color(0xFFFFFFFF), // White eye sclera
        'W' to Color(0xFFE67E22), // Orange beak
        'G' to Color(0xFF27AE60), // Green tail feathers
        'O' to Color(0xFFD35400)  // Dark orange talons
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGarland() {
    val matrix = arrayOf(
        ".KK..............KK...", // 0
        "KYYK............KYYK..", // 1
        ".KYYK..........KYYK...", // 2
        "..KYYKKKKKKKKKKYYK....", // 3
        "...KBBBBBBBBBBBBK.....", // 4
        "...KBBBBBBBBBBBBK.....", // 5
        "...KBBBBERREBBBBK.....", // 6
        "...KBBBBBBBBBBBBK.....", // 7
        "...KBBBBBBBBBBBBK.....", // 8
        "..KSSBBBBBBBBBBSSK....", // 9
        ".KSSSSBBBBBBBBSSSSK...", // 10
        "KSSSSSSRRRRRRSSSSSSK..", // 11
        ".KSSSSSRRRRRRSSSSSK...", // 12
        "..KSSSSRRRRRRSSSSK....", // 13
        "...KSSSRRRRRRSSSK.....", // 14
        "...KSSSRRRRRRSSSK.....", // 15
        "...KSSSRRRRRRSSSK.....", // 16
        "...KSSSRRRRRRSSSK.....", // 17
        "...KSSSRRRRRRSSSK.....", // 18
        "...KSSSRRRRRRSSSK.....", // 19
        "...KSSSK....KSSSK.....", // 20
        "...KSSSK....KSSSK.....", // 21
        "...KSSSK....KSSSK.....", // 22
        "...KSSSK....KSSSK.....", // 23
        "...KKKKK....KKKKK....."  // 24
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'B' to Color(0xFF2C3E50), // Dark charcoal/navy armor
        'S' to Color(0xFF7F8C8D), // Steel silver plates
        'Y' to Color(0xFFF1C40F), // Gold horned helm spikes
        'R' to Color(0xFFC0392B), // Crimson cape & red visor eye glow
        'E' to Color(0xFFFF2A2A)  // Glowing red eyes
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawLich() {
    val matrix = arrayOf(
        ".......KKKKKK.........", // 0
        "......KPPPPP3K........", // 1
        ".....KPPPPPPPPK.......", // 2
        "....KPPPPPPPPPPK......", // 3
        "....KPPPWWWWPPPK......", // 4
        "....KPPWERREWPPK......", // 5
        "....KPPPWWWWPPPK......", // 6
        "....KPPPWBWBPPPK......", // 7
        "....KPPPPWWPPPPK......", // 8
        "...KPPPPPPPPPPPPK.....", // 9
        "..KPPPPPPPPPPPPPPK....", // 10
        ".KPPPPPPPDDPPPPPPPK...", // 11
        ".KPPPPPPPDDPPPPPPPK...", // 12
        ".KPPPPPPPDDPPPPPPPK...", // 13
        ".KPPPPPPPDDPPPPPPPK...", // 14
        ".KPPPPPPPDDPPPPPPPK...", // 15
        ".KPPPPPPPDDPPPPPPPK...", // 16
        ".KPPPPPPPDDPPPPPPPK...", // 17
        ".KPPPPPPPDDPPPPPPPK...", // 18
        ".KPPPPPPPDDPPPPPPPK...", // 19
        ".KPPPPPPPDDPPPPPPPK...", // 20
        "..KPPPPPPDDPPPPPPK....", // 21
        "...KPPPPPDDPPPPPK.....", // 22
        "....KKKKKKKKKKKK......"  // 23
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'P' to Color(0xFF6C3483), // Dark purple robe
        'D' to Color(0xFF4A235A), // Darker purple shadow fold
        'W' to Color(0xFFECF0F1), // Bone white skull
        'B' to Color(0xFF17202A), // Dark nasal cavity / teeth
        'E' to Color(0xFFE74C3C), // Glowing red eyes
        '3' to Color(0xFF8E44AD)  // Light purple highlight
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawKraken() {
    val matrix = arrayOf(
        "........KKKKKK..........", // 0
        ".......KBBBBBBK.........", // 1
        "......KBBBBBBBBK........", // 2
        ".....KBBBBBBBBBBK.......", // 3
        "....KBBBBBBBBBBBBK......", // 4
        "...KBBBBBYYBBBYYBBK.....", // 5
        "...KBBBBBYYBBBYYBBK.....", // 6
        "...KBBBBBKKBBBKKBBK.....", // 7
        "...KBBBBBBBBBBBBBBK.....", // 8
        "....KBBBBBBBBBBBBK......", // 9
        "....KCCCCCCCCCCCCK......", // 10
        "...KCC.CC.CC.CC.CCK.....", // 11
        "..KCC..CC..CC..CC..CK...", // 12
        ".KCC...CC..CC..CC...CK..", // 13
        ".KCC...CC..CC..CC...CK..", // 14
        "KCC....CC..CC..CC....CK.", // 15
        "KCC....CC..CC..CC....CK.", // 16
        "KCC....CC..CC..CC....CK.", // 17
        ".KCC...CC..CC..CC...CK..", // 18
        "..KCC..CC..CC..CC..CK...", // 19
        "...KK..KK..KK..KK..KK..."  // 20
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'B' to Color(0xFF1F618D), // Royal blue squid head
        'C' to Color(0xFF2980B9), // Cyan-blue tentacles
        'Y' to Color(0xFFF1C40F)  // Glowing yellow eyes
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawTiamat() {
    val matrix = arrayOf(
        ".KK......KK......KK.....", // 0
        "KGGK....KGGK....KGGK....", // 1
        "KGRK....KGRK....KGRK....", // 2
        ".KGGK...KGGK...KGGK.....", // 3
        "..KGGK..KGGK..KGGK......", // 4
        "...KGGKKKGGKKKGGK.......", // 5
        "....KGGGGGGGGGGK........", // 6
        "...KGGGGGGGGGGGGK.......", // 7
        "..KGGGGGGGGGGGGGGK......", // 8
        ".KGGGGGGGGGGGGGGGGK.....", // 9
        "KGGGGGGGGGGGGGGGGGGK....", // 10
        "KGGGGGGGGGGGGGGGGGGK....", // 11
        ".KGGGGGGGGGGGGGGGGK.....", // 12
        "..KGGGGGGGGGGGGGGK......", // 13
        "...KGGGGGGGGGGGGK.......", // 14
        "....KGGGGGGGGGGK........", // 15
        ".....KGGK....KGGK.......", // 16
        ".....KGGK....KGGK.......", // 17
        ".....KGGK....KGGK.......", // 18
        "....KGGGK...KGGGK.......", // 19
        "....KKKKK...KKKKK......."  // 20
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'G' to Color(0xFF27AE60), // Emerald green dragon body
        'R' to Color(0xFFE74C3C)  // Red glowing eyes on each head
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawDragon(color: Color) {
    val matrix = arrayOf(
        "..........KKKK..........", // 0
        ".........KCCCCK...KK....", // 1
        "........KCCCCCRK.KCCK...", // 2
        ".......KCCCCCCCCKCCCK...", // 3
        "......KCCCCCCCCCCCCCK...", // 4
        ".....KCCCCCCCCCCCCCCK...", // 5
        "....KCCCCCCCCCCCCCCCK...", // 6
        "...KCCCCCCCCCCCCCCCCK...", // 7
        "..KCCCCCCCCCCCCCCCCCK...", // 8
        ".KCCCCCCCCCCCCCCCCCK....", // 9
        ".KCCCCCCCCCCCCCCCCK.....", // 10
        "..KCCCCCCCCCCCCCCK......", // 11
        "...KCCCCCCCCCCCCK.......", // 12
        "....KCCCCK..KCCCCK......", // 13
        "....KCCCCK..KCCCCK......", // 14
        "....KCCCCK..KCCCCK......", // 15
        "...KCCCCCK.KCCCCCK......", // 16
        "...KKKKKKK.KKKKKKK......"  // 17
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'C' to color,              // Dragon color
        'R' to Color(0xFFF1C40F)  // Yellow eye
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawChaos() {
    val matrix = arrayOf(
        "KK....................KK..", // 0
        "KYYK................KYYK..", // 1
        ".KYYK..............KYYK...", // 2
        "..KYYKKKKKKKKKKKKKKYYK....", // 3
        "...KYYYYYYYYYYYYYYYYK.....", // 4
        "...KYYYYYWWWWYYYYYYYK.....", // 5
        "...KYYYYWERREWYYYYYYK.....", // 6
        "...KYYYYYWWWWYYYYYYYK.....", // 7
        "..KRRRRRYYYYYYYYRRRRRK....", // 8
        ".KRRRRRRRRRRRRRRRRRRRRK...", // 9
        "KRRRRRRRRRRRRRRRRRRRRRRK..", // 10
        "KRRRRRRRRRRRRRRRRRRRRRRK..", // 11
        ".KRRRRRRRRRRRRRRRRRRRRK...", // 12
        "..KRRRRRRRRRRRRRRRRRRK....", // 13
        "...KRRRRRRRRRRRRRRRRK.....", // 14
        "...KRRRRRRRRRRRRRRRRK.....", // 15
        "...KRRRRRRRRRRRRRRRRK.....", // 16
        "...KRRRRRRRRRRRRRRRRK.....", // 17
        "...KRRRRK......KRRRRK.....", // 18
        "...KRRRRK......KRRRRK.....", // 19
        "...KRRRRK......KRRRRK.....", // 20
        "..KRRRRRK.....KRRRRRK.....", // 21
        "..KKKKKKK.....KKKKKKK....."  // 22
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'Y' to Color(0xFFF1C40F), // Golden demon horns & chest armor
        'R' to Color(0xFFC0392B), // Crimson demonic body & wings
        'W' to Color(0xFFFFFFFF), // White demonic face
        'E' to Color(0xFFFF0000)  // Glowing evil red eyes
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawWildRat() {
    val matrix = arrayOf(
        "........KKKK....", // 0
        ".......KBBBBK...", // 1
        "......KBBBBBBK..", // 2
        ".....KBBBBBBBBK.", // 3
        "....KBBBBBBBRSK.", // 4
        "PP.KBBBBBBBBWWK.", // 5
        "PKKBBBBBBBBBBKK.", // 6
        ".KBBBBBBBBBBBKK.", // 7
        "..KBBBBBBBBBBBKK", // 8
        "...KBBBBBBBBK...", // 9
        "....KBBK.KBBK...", // 10
        "....KBBK.KBBK...", // 11
        "....KKKK.KKKK..."  // 12
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'B' to Color(0xFF7F8C8D), // Grey fur
        'P' to Color(0xFFF1948A), // Pink tail
        'R' to Color(0xFFE74C3C), // Red eye
        'S' to Color(0xFFFFFFFF), // White sclera
        'W' to Color(0xFFF5B7B1)  // Pink snout
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawDarkKnight() {
    val matrix = arrayOf(
        ".......KKKKKK.......", // 0
        "......KCCCCCCK......", // 1
        ".....KCCCCCCCCK.....", // 2
        "....KCCCCCCCCCCK....", // 3
        "....KCCCCEERCCCK....", // 4
        "....KCCCCCCCCCCK....", // 5
        "....KCCCCCCCCCCK....", // 6
        "...KSSCCCCCCCCSSK...", // 7
        "..KSSSCCCCCCSSSSK...", // 8
        ".KSSSSCCCCCCSSSSSK..", // 9
        "KSSSSSRRRRRRSSSSSSK.", // 10
        ".KSSSSRRRRRRSSSSSK..", // 11
        "..KSSSSRRRRSSSSSK...", // 12
        "...KSSSSRRRSSSSK....", // 13
        "....KSSSSSSSSSSK....", // 14
        ".....KCCCCCCCCK.....", // 15
        ".....KCCCCCCCCK.....", // 16
        ".....KCCCCKCCCK.....", // 17
        ".....KCCCK.KCCCK....", // 18
        ".....KCCCK.KCCCK....", // 19
        "....KCCCCK.KCCCCK...", // 20
        "....KKKKKK.KKKKKK..."  // 21
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'C' to Color(0xFF2C3E50), // Charcoal dark plate
        'S' to Color(0xFF5D6D7E), // Steel grey pauldron/trim
        'R' to Color(0xFF922B21), // Dark red tabard/cape
        'E' to Color(0xFFE74C3C)  // Red visor slit
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawLamia() {
    val matrix = arrayOf(
        ".......KKKKKK.......", // 0
        "......KGGGGGGK......", // 1
        ".....KGGGGGGGGK.....", // 2
        "....KGGGGGGGGGGK....", // 3
        "....KPPPPRSYPPGK....", // 4
        "....KPPPPPPPPPGK....", // 5
        "....KPPPPPPPPPGK....", // 6
        "...KGGPPPPPPPPPGK...", // 7
        "..KGGGVVVVVVVGGGK...", // 8
        ".KGGGGVVVVVVVGGGGK..", // 9
        ".KGGGGGGGGGGGGGGGK..", // 10
        "..KGGGGGGGGGGGGGK...", // 11
        "...KGGGGGGGGGGGK....", // 12
        "....KGGGGGGGGGK.....", // 13
        ".....KGGGGGGGK......", // 14
        "......KGGGGGK.......", // 15
        ".......KGGGK........", // 16
        "......KGGGGGK.......", // 17
        ".....KGGGGGGGK......", // 18
        "....KGGGGGGGGGK.....", // 19
        "....KKKKKKKKKKK....."  // 20
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'G' to Color(0xFF27AE60), // Green snake tail & hair
        'P' to Color(0xFFF3C59D), // Peach skin
        'R' to Color(0xFFE74C3C), // Red eye
        'S' to Color(0xFFFFFFFF), // White sclera
        'Y' to Color(0xFFF1C40F), // Gold tiara
        'V' to Color(0xFF8E44AD)  // Purple bra/accent
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawAdamantoise() {
    val matrix = arrayOf(
        ".........KKKKKK.........", // 0
        "........KGGGGGGK........", // 1
        ".......KGGGGGGGGK.......", // 2
        "......KGGGGGGGGGGK......", // 3
        ".....KGGGGGGGGGGGGK.....", // 4
        "....KGGGGGGGGGGGGGGK....", // 5
        "...KGGGGGGGGGGGGGGGGK...", // 6
        "..KGGGGGGGGGGGGGGGGGGK..", // 7
        ".KGGGGGGGGGGGGGGGGGGGGK.", // 8
        "KGGGGGGGGGGGGGGGGGGGGGGK", // 9
        "KTTTTTTTTTTTTTTTTTTTTTTK", // 10
        "KTTTTTTTTTTTTTTTTTTTTTTK", // 11
        ".KTTTTTTTTTTTTTTTTTTTTK.", // 12
        "..KTTTTTTTTTTTTTTTTTTK..", // 13
        "...KTTK.KTTTTK.KTTK.....", // 14
        "...KTTK.KTTTTK.KTTK.....", // 15
        "...KKKK.KKKKKK.KKKK....."  // 16
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'G' to Color(0xFF1E8449), // Dark green spiked shell
        'T' to Color(0xFFD4AC0D)  // Tan/gold underside
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawTonberry() {
    val matrix = arrayOf(
        "......KKKKKK......", // 0
        ".....KGGGGGGK.....", // 1
        "....KGGGGGGGGK....", // 2
        "...KGGGGGGGGGGK...", // 3
        "..KGGGGYYSYYGGGK..", // 4
        "..KGGGGGGGGGGGGK..", // 5
        "..KGGGGGGGGGGGGK..", // 6
        "...KGGGGGGGGGGK...", // 7
        "...KBBBBBBBBBBK...", // 8
        "..KBBBBBBBBBBBBK..", // 9
        ".KBBBBBBBBBBBBBBK.", // 10
        "KBBBBBBBBBBBBBBBBK", // 11
        "KBBBBBBBBBBBBBBBBK", // 12
        "KLLKBBBBBBBBBBKSSK", // 13
        "KLLKBBBBBBBBBBKSSK", // 14
        ".KKKBBBBBBBBBBKKKK", // 15
        "...KBBBBBBBBBBK...", // 16
        "...KBBBBBBBBBBK...", // 17
        "...KBBBKKKKBBBK...", // 18
        "...KBBK....KBBK...", // 19
        "...KKKK....KKKK..."  // 20
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'G' to Color(0xFF2ECC71), // Bright green skin
        'B' to Color(0xFF7E5109), // Brown robe
        'Y' to Color(0xFFF1C40F), // Yellow glowing eyes
        'S' to Color(0xFFBDC3C7), // Silver butcher knife / sclera
        'L' to Color(0xFFF39C12)  // Glowing yellow/orange lantern
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawDjinn() {
    val matrix = arrayOf(
        ".......KKKKKK.......", // 0
        "......KRRRRRRK......", // 1
        ".....KRRRRRRRRK.....", // 2
        "....KRRRRRRRRRRK....", // 3
        "....KRRRRRSYRRRK....", // 4
        "....KRRRRRRRRRRK....", // 5
        "....KRRRRRRRRRRK....", // 6
        "...KYYYYRRRRRYYYYK..", // 7
        "..KYYYYYRRRRRYYYYYK.", // 8
        ".KYYYYYYRRRRRYYYYYYK", // 9
        ".KYYYYYYRRRRRYYYYYYK", // 10
        "..KYYYYYYYYYYYYYYYK.", // 11
        "...KRRRRRRRRRRRRRK..", // 12
        "....KRRRRRRRRRRRK...", // 13
        ".....KRRRRRRRRRK....", // 14
        "......KRRRRRRRK.....", // 15
        ".......KRRRRRK......", // 16
        "......KRRRRRRRK.....", // 17
        ".....KRRRRRRRRRK....", // 18
        "....KKKKKKKKKKKKK..."  // 19
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'R' to Color(0xFFE74C3C), // Red elemental flame
        'Y' to Color(0xFFF1C40F), // Gold wristbands/body
        'S' to Color(0xFFFFFFFF)  // White eye sclera
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawHein() {
    val matrix = arrayOf(
        ".......KKKKKK.......", // 0
        "......KYYYYYYK......", // 1
        ".....KYYYYYYYYK.....", // 2
        "....KYYYYYYYYYYK....", // 3
        "....KPPWWWWWWPPK....", // 4
        "....KPPWERREWP3K....", // 5
        "....KPPWWWWWWPPK....", // 6
        "....KPPWBWBWBPPK....", // 7
        "....KPPPPWWPP3PK....", // 8
        "...KPPPPPPPPPPPPK...", // 9
        "..KPPPPPPPPPPPPPPK..", // 10
        ".KPPPPPPPDDPPPPPPPK.", // 11
        ".KPPPPPPPDDPPPPPPPK.", // 12
        ".KPPPPPPPDDPPPPPPPK.", // 13
        ".KPPPPPPPDDPPPPPPPK.", // 14
        ".KPPPPPPPDDPPPPPPPK.", // 15
        ".KPPPPPPPDDPPPPPPPK.", // 16
        ".KPPPPPPPDDPPPPPPPK.", // 17
        ".KPPPPPPPDDPPPPPPPK.", // 18
        "..KPPPPPPDDPPPPPPK..", // 19
        "...KKKKKKKKKKKKKK..."  // 20
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'Y' to Color(0xFFF1C40F), // Gold hat
        'P' to Color(0xFF8E44AD), // Purple robe
        'D' to Color(0xFF5B2C6F), // Dark purple shadow
        'W' to Color(0xFFECF0F1), // Bone white
        'B' to Color(0xFF17202A), // Skull details
        'E' to Color(0xFFE74C3C), // Red eyes
        '3' to Color(0xFFA569BD)  // Light purple
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawEmperor() {
    val matrix = arrayOf(
        ".......KKYYYYKK.......", // 0
        "......KYYYYYYYYK......", // 1
        ".....KYYYYRRYYYYK.....", // 2
        "....KYYYYWWWWYYYYK....", // 3
        "....KYYYYWERREYYYK....", // 4
        "....KYYYYWWWWYYYYK....", // 5
        "....KYYYYYYYYYYYYK....", // 6
        "...KPPPPYYYYYYYYPPK...", // 7
        "..KPPPPPYYYYYYYYPPPK..", // 8
        ".KPPPPPPYYYYYYYYPPPPK.", // 9
        ".KPPPPPPYYYYYYYYPPPPK.", // 10
        ".KPPPPPPYYYYYYYYPPPPK.", // 11
        "..KPPPPPYYYYYYYYPPPK..", // 12
        "...KPPPPYYYYYYYYPPK...", // 13
        "....KPPPYYYYYYYYPK....", // 14
        ".....KPPYYYYYYYYK.....", // 15
        "......KPYYYYYYPK......", // 16
        "......KYYYYYYYYK......", // 17
        "......KYYYYYYYYK......", // 18
        ".....KYYYYYYYYYYK.....", // 19
        ".....KKKKKKKKKKKK....."  // 20
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'Y' to Color(0xFFF1C40F), // Gold imperial plate
        'P' to Color(0xFF8E44AD), // Royal purple robe
        'R' to Color(0xFFC0392B), // Ruby jewel
        'W' to Color(0xFFFFFFFF), // White face/sclera
        'E' to Color(0xFF2C3E50)  // Dark eye pupil
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawCloudOfDarkness() {
    val matrix = arrayOf(
        "........KKKKKK..........", // 0
        ".......KCCCCCCK.........", // 1
        "......KCCCCCCCCK........", // 2
        ".....KCCCCCCCCCCK.......", // 3
        "....KCCCCWERRECCCK......", // 4
        "....KCCCCCCCCCCCCK......", // 5
        "....KCCCCCCCCCCCCK......", // 6
        "...KPPPPCCCCCCPPPPK.....", // 7
        "..KPPPPPPCCCCPPPPPPK....", // 8
        ".KPPPPPPPCCCCPPPPPPPK...", // 9
        ".KPPPPPPPCCCCPPPPPPPK...", // 10
        ".KPPPPPPPCCCCPPPPPPPK...", // 11
        "..KPPPPPPCCCCPPPPPPK....", // 12
        "...KPPPPCCCCCCPPPPK.....", // 13
        "....KCCC.CCCC.CCCK......", // 14
        "...KCCC..CCCC..CCCK.....", // 15
        "..KCCC...CCCC...CCCK....", // 16
        ".KCCC....CCCC....CCCK...", // 17
        "KKKK.....KKKK.....KKKK.."  // 18
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'C' to Color(0xFF34495E), // Dark slate cloud
        'P' to Color(0xFF8E44AD), // Dark purple tentacles
        'W' to Color(0xFFECF0F1), // Pale face
        'E' to Color(0xFFE74C3C), // Red eyes
        'R' to Color(0xFFFF0000)  // Glowing center
    )

    drawMonsterMatrix(matrix, palette)
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
        enemyName.contains("Chaos", true) || enemyName.contains("Demon", true) -> drawChaos()
        enemyName.contains("Tiamat", true) -> drawTiamat()
        enemyName.contains("Dragon", true) -> drawDragon(Color.Red)
        enemyName.contains("Rat", true) -> drawWildRat()
        enemyName.contains("Wolf", true) -> drawWolf()
        enemyName.contains("Sahagin", true) || enemyName.contains("Merman", true) -> drawSahagin()
        enemyName.contains("Pirate", true) -> drawPirate()
        enemyName.contains("Cockatrice", true) -> drawCockatrice()
        enemyName.contains("Kraken", true) -> drawKraken()
        enemyName.contains("Bomb", true) -> drawBomb()
        enemyName.contains("Eye", true) -> drawEye()
        enemyName.contains("Tonberry", true) -> drawTonberry()
        enemyName.contains("Lich", true) -> drawLich()
        enemyName.contains("Garland", true) -> drawGarland()
        enemyName.contains("Dark Knight", true) || enemyName.contains("Black Knight", true) || enemyName.contains("Sergeant", true) -> drawDarkKnight()
        enemyName.contains("Lamia", true) || enemyName.contains("Medusa", true) -> drawLamia()
        enemyName.contains("Adamantoise", true) -> drawAdamantoise()
        enemyName.contains("Djinn", true) -> drawDjinn()
        enemyName.contains("Hein", true) -> drawHein()
        enemyName.contains("Emperor", true) -> drawEmperor()
        enemyName.contains("Cloud", true) -> drawCloudOfDarkness()
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

// --- TOWN NPC SPRITES ---

fun DrawScope.drawTownGuard() {
    val p = px32()
    // Silver Helmet with Red Crest
    p(10f, 2f, 12f, 8f, FFSilver)
    p(13f, 0f, 6f, 3f, FFRed) // Red crest
    p(10f, 7f, 12f, 1f, FFBlack) // Visor slit
    // Face peeking
    p(11f, 8f, 10f, 5f, SkinMid)
    p(13f, 10f, 1f, 1f, Color.Black)
    p(18f, 10f, 1f, 1f, Color.Black)
    // Armor
    p(10f, 13f, 12f, 10f, FFBlue) // Cobalt tabard
    p(8f, 13f, 3f, 5f, FFSilver) // Pauldrons
    p(21f, 13f, 3f, 5f, FFSilver)
    p(11f, 14f, 10f, 2f, FFGold) // Gold chest emblem
    // Halberd / Spear
    p(24f, 0f, 1f, 28f, HairBrown) // Shaft
    p(23f, 0f, 3f, 4f, FFSilver) // Spearhead
    p(22f, 2f, 2f, 2f, FFGold) // Axe blade side
    // Shield on Left Arm
    p(5f, 14f, 4f, 10f, FFRed)
    p(6f, 15f, 2f, 8f, FFGold)
    // Legs
    p(11f, 23f, 4f, 7f, FFSilverDark)
    p(17f, 23f, 4f, 7f, FFSilverDark)
    p(10f, 29f, 5f, 2f, FFBlack)
    p(17f, 29f, 5f, 2f, FFBlack)
}

fun DrawScope.drawTownScholar() {
    val p = px32()
    // Pointed Wizard Hat
    p(13f, 0f, 6f, 5f, FFPurple)
    p(11f, 5f, 10f, 2f, FFPurple)
    p(9f, 7f, 14f, 2f, FFGold) // Brim
    // Face & Glasses / Spectacles
    p(11f, 9f, 10f, 5f, SkinLight)
    p(12f, 10f, 3f, 2f, FFGold) // Spectacles frame L
    p(17f, 10f, 3f, 2f, FFGold) // Spectacles frame R
    p(13f, 11f, 1f, 1f, Color.Black)
    p(18f, 11f, 1f, 1f, Color.Black)
    p(12f, 13f, 8f, 2f, HairWhite) // Beard
    // Robes
    p(10f, 14f, 12f, 11f, FFPurple)
    p(14f, 14f, 4f, 11f, FFGold) // Gold inner sash
    p(9f, 16f, 3f, 7f, FFPurple) // Sleeve L
    p(20f, 16f, 3f, 7f, FFPurple) // Sleeve R
    // Glowing Magic Tome held in hands
    p(19f, 18f, 6f, 6f, HairBrown) // Book cover
    p(20f, 19f, 4f, 4f, Color.Cyan) // Glowing magic pages
    // Legs
    p(12f, 25f, 8f, 5f, FFBlack)
}

fun DrawScope.drawTownAdventurer() {
    val p = px32()
    // Green Cap & Feather
    p(10f, 3f, 12f, 4f, FFGreen)
    p(19f, 1f, 2f, 4f, Color.White) // Feather
    p(11f, 7f, 10f, 2f, HairBrown) // Messy Hair
    // Face
    p(11f, 8f, 10f, 5f, SkinMid)
    p(13f, 10f, 1f, 1f, Color.Black)
    p(18f, 10f, 1f, 1f, Color.Black)
    // Tunic & Cloak
    p(10f, 13f, 12f, 10f, FFGreen)
    p(8f, 13f, 3f, 10f, Color(0xFF5D4037)) // Brown cape L
    p(21f, 13f, 3f, 10f, Color(0xFF5D4037)) // Brown cape R
    p(12f, 13f, 8f, 2f, Color.White) // Collar
    p(11f, 18f, 10f, 2f, FFGold) // Belt
    // Sword at hip
    p(22f, 16f, 1f, 8f, FFSilver)
    p(21f, 18f, 3f, 1f, FFGold)
    // Legs & Boots
    p(12f, 23f, 3f, 6f, Color(0xFF5D4037))
    p(17f, 23f, 3f, 6f, Color(0xFF5D4037))
    p(11f, 28f, 4f, 3f, Color(0xFF3E2723))
    p(17f, 28f, 4f, 3f, Color(0xFF3E2723))
}

fun DrawScope.drawTownMerchant() {
    val p = px32()
    // Turban with Gem
    p(10f, 2f, 12f, 6f, FFRed)
    p(12f, 1f, 8f, 2f, FFGold)
    p(15f, 4f, 2f, 2f, FFGreen) // Emerald in turban
    // Face with Beard
    p(11f, 8f, 10f, 6f, SkinMid)
    p(13f, 10f, 1f, 1f, Color.Black)
    p(18f, 10f, 1f, 1f, Color.Black)
    p(12f, 12f, 8f, 2f, HairBrown) // Beard
    // Rich Vest & Tunic
    p(10f, 14f, 12f, 10f, FFRed)
    p(10f, 14f, 3f, 10f, FFGold) // Gold trim L
    p(19f, 14f, 3f, 10f, FFGold) // Gold trim R
    p(14f, 18f, 4f, 4f, Color(0xFF7E5109)) // Coin pouch at belt
    // Held Gold Coin up
    p(6f, 14f, 3f, 3f, FFGold)
    p(7f, 15f, 1f, 1f, Color.White)
    // Legs & Shoes
    p(12f, 24f, 3f, 6f, FFBlue)
    p(17f, 24f, 3f, 6f, FFBlue)
    p(10f, 29f, 5f, 2f, FFGold) // Curl toe shoes
    p(17f, 29f, 5f, 2f, FFGold)
}

fun DrawScope.drawTownGladiator() {
    val p = px32()
    // Galea Gladiator Helmet with High Red Crest
    p(13f, 0f, 6f, 4f, FFRed) // High crest
    p(10f, 4f, 12f, 6f, Color(0xFFB7950B)) // Bronze helmet
    p(11f, 7f, 10f, 1f, FFBlack) // Visor opening
    // Face peeking
    p(11f, 8f, 10f, 5f, SkinMid)
    p(13f, 9f, 1f, 1f, Color.Black)
    p(18f, 9f, 1f, 1f, Color.Black)
    // Muscular Bare Torso & Manica Shoulder Guard
    p(11f, 13f, 10f, 9f, SkinMid)
    p(8f, 13f, 4f, 6f, Color(0xFFB7950B)) // Bronze Manica pauldron
    p(14f, 15f, 4f, 5f, SkinShadow) // Muscle shading
    // Leather Kilt / Pteryges
    p(10f, 21f, 12f, 4f, Color(0xFF5D4037))
    p(11f, 22f, 2f, 3f, Color(0xFFB7950B)) // Bronze studs
    p(15f, 22f, 2f, 3f, Color(0xFFB7950B))
    p(19f, 22f, 2f, 3f, Color(0xFFB7950B))
    // Gladius Sword (Right Hand)
    p(23f, 12f, 2f, 12f, FFSilver)
    p(22f, 22f, 4f, 1.5f, Color(0xFFB7950B))
    // Round Shield (Left Arm)
    p(5f, 15f, 4f, 8f, Color(0xFFB7950B))
    p(6f, 16f, 2f, 6f, FFRed)
    // Legs & Greaves
    p(12f, 25f, 3f, 5f, SkinMid)
    p(17f, 25f, 3f, 5f, SkinMid)
    p(11f, 28f, 4f, 3f, Color(0xFFB7950B)) // Bronze greaves
    p(17f, 28f, 4f, 3f, Color(0xFFB7950B))
}

@Composable fun TownNpcSprite(npcId: String, modifier: Modifier = Modifier) = Canvas(modifier) {
    when (npcId) {
        "guard" -> drawTownGuard()
        "scholar" -> drawTownScholar()
        "adventurer" -> drawTownAdventurer()
        "merchant" -> drawTownMerchant()
        "gladiator" -> drawTownGladiator()
        else -> drawTownGuard()
    }
}

