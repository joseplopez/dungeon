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
        "..KKKKPPBFFFFK", // 15
        ".KYYYKKPBBBFFFK", // 16
        ".KYYYYKKBBBBBFK.", // 17
        "..KYYYMKKBBBBFK.", // 18
        "..KYYYMMKKBBBBK..", // 19
        "..KYYYYMMKKBBBK..", // 20
        "..KYYYYRRKKKBBK..", // 21
        "..KYYYYRRKKBK..", // 22
        "...KKKKKKKKK.."  // 23
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
        "..CK..KCNNNNCCK.", // 2
        "...CKKCNNNNNNNK.", // 3
        "....CLLLNNNNTSK.", // 4
        "....CCCNTTTTTSK.", // 5
        "...KCCCCSTTTTSK.", // 6
        "..KKKCCCCCNCCKK.", // 7
        ".KCNCCCCCVKKK...", // 8
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
        "...KK.............", // 0
        "..KLLPK...KK......", // 1
        ".KLLPPK..KPPK.....", // 2
        "..KLLPPKKLLPPK....", // 3
        "..KLLPLLWPPPPK....", // 4
        ".KLLPLLLLPPPPK....", // 5
        ".KPLLYYYYPPBK.....", // 6
        "KLLPPYYYYBBK..KPLK", // 7
        "KLLPKKKKKKKKK.KPLK", // 8
        "..KLLPKHHSSK..KPLK", // 9
        "...KLPKPKSSSK.KPLK", // 10
        "...KLPKKKKKK..KPLK", // 11
        "KLLPKLPPWBBK.KPPLK", // 12
        "KLLPKBLLPPPBBKKSSK", // 13
        "KPLLKYYPPYYBKKPSSK", // 14
        "KRRKKBPPYYSSBKKPPK", // 15
        ".KRRKLLPPBBSSBKKK.", // 16
        "..KLLPBK..KLLPBBK.", // 17
        "..KLLPBK..KLLPBBK.", // 18
        "...KLLPBK..KLLPBK.", // 19
        "...KLPBK...KLLPBK.", // 20
        "...KLLPBK..KLLPBBK", // 21
        "..KYLPBK..KYYPBBK.", // 22
        "..KYYPBK..KYYPBBK.", // 23
        "..KYYPBK..KYYPBBK.", // 24
        "..KKKKKK..KKKKKKK."  // 25
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
        ".KDEEEEPPEEEEDK.", // 14
        ".KDEEEEEEEEEEDK.", // 15
        ".KDEEEDEEEEEEK..", // 16
        ".KDE.EDPPEEPPK..", // 17
        ".KDE.EKPPDDPPK..", // 18
        ".KDE..KYYKKYYK..", // 19
        ".KDE..KYOKKYOK..", // 20
        ".KDK..KYOKKYOK..", // 21
        "..KK..KYYKKYYK..", // 22
        ".....KKKK.KKKKK.", // 23
        "................"  // 24
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

fun DrawScope.drawZombie() {
    val matrix = arrayOf(
        ".......KKKKK......", // 0
        "......KGGGGGK.....", // 1
        ".....KGGGGGGGK....", // 2
        "....KGGYYYYGGK....", // 3
        "....KGGKKKKGGK....", // 4
        "....KGGGGGGGGK....", // 5
        "....KGGGGWWGGK....", // 6
        "....KGGGGGGGGK.KK.", // 7
        "...KRRRRGGGGGKKSK.", // 8
        "..KRRRRRRGGGGKKSK.", // 9
        ".KRRRRRRRRGGGKKK..", // 10
        ".KRRRRRRRRRRRK....", // 11
        ".KRRRRRRRRRRRK....", // 12
        "..KRRRRRRRRRRK....", // 13
        "...KRRRRRRRRRK....", // 14
        "....KDDDDDDDK.....", // 15
        "....KGGGGGGGK.....", // 16
        "....KGGKKKGGK.....", // 17
        "....KGGK.KGGK.....", // 18
        "....KGGK.KGGK.....", // 19
        "....KDDK.KDDK.....", // 20
        "....KKKK.KKKK....."  // 21
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'G' to Color(0xFF556B2F), // Sickly olive green skin
        'D' to Color(0xFF3B4A20), // Dark green skin shadow
        'R' to Color(0xFF8B0000), // Dark red tattered rags
        'Y' to Color(0xFFF1C40F), // Sunken glowing yellow eyes
        'W' to Color(0xFFECF0F1), // Exposed bone / fangs
        'S' to Color(0xFFBDC3C7)  // Bone claws
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawWyvern() {
    val matrix = arrayOf(
        "............KKKK........", // 0
        "...........KPPPPK...KK..", // 1
        "..........KPPPPPRK.KPK..", // 2
        ".........KPPPPPPPPKPPK..", // 3
        "........KPPPPPPPPPPK....", // 4
        "...KKKKKPPPPPPPPPPPK....", // 5
        "..KBBBBBPPPPPPPPPPPK....", // 6
        ".KBBBBBBBPPPPPPPPPPK....", // 7
        "KBBBBBBBBBPPPPPPPPPK....", // 8
        ".KBBBBBBBBBPPPPPPPK.....", // 9
        "..KBBBBBBBBBPPPPPK......", // 10
        "...KKBBBBBBBPPPPK.......", // 11
        ".....KBBBBBBPPPPK.......", // 12
        "......KBBBBBPPPPK.......", // 13
        ".......KBBBBPPPPK.......", // 14
        "........KPPP.PPPK.......", // 15
        "........KPPK.KPPK.......", // 16
        "........KPPK.KPPK.......", // 17
        ".......KYYPK.KYYPK......", // 18
        ".......KYYPK.KYYPK......", // 19
        ".......KKKKK.KKKKK......", // 20
        "........................"  // 21
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'P' to Color(0xFF6C3483), // Purple Wyvern body
        'B' to Color(0xFF2980B9), // Blue wing membrane
        'R' to Color(0xFFE74C3C), // Crimson eye
        'Y' to Color(0xFFF1C40F)  // Gold horn & talons
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawBehemoth() {
    val matrix = arrayOf(
        "...............KKK........", // 0
        "..............KYYYK.......", // 1
        ".............KYYYYK...KK..", // 2
        "............KYYYYYK..KPK..", // 3
        "...........KYYYYYRK.KPPK..", // 4
        "..........KYYYYYYPPKPPPPK.", // 5
        ".........KYYYYYPPPPPPPPPK.", // 6
        "......KKKCCCCCPPPPPPPPPPK.", // 7
        ".....KCCCCCCCPPPPPPPPPPPK.", // 8
        "....KCCCCCCCCPPPPPPPPPPPK.", // 9
        "...KCCCCCCCCCPPPPPPPPPPPK.", // 10
        "..KCCCCCCCCC3PPPPPPPPPPPK.", // 11
        ".KCCCCCCCCC33PPPPPPPPPPK..", // 12
        "KCCCCCCCCCCC3PPPPPPPPPK...", // 13
        ".KCCCCCCCCCC3PPPPPPPPK....", // 14
        "..KCCCCCCCCCPPPPPPPPPK....", // 15
        "...KCCCCK....KPPPPPPPK....", // 16
        "...KCCCK......KPPPPPPK....", // 17
        "...KCCCK......KPPPPPPK....", // 18
        "..KSSSK........KSSSKKK....", // 19
        "..KSSSK........KSSSK......", // 20
        "..KKKKK........KKKKK......"  // 21
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'P' to Color(0xFF4A235A), // Deep purple body
        '3' to Color(0xFF6C3483), // Lighter purple shading
        'C' to Color(0xFF1ABC9C), // Cyan wild mane
        'Y' to Color(0xFFF1C40F), // Gold curved horns
        'R' to Color(0xFFE74C3C), // Red eye
        'S' to Color(0xFFBDC3C7)  // Silver claws
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawToad() {
    val matrix = arrayOf(
        "......KKKKKK......", // 0
        "....KGGGGGGGGK....", // 1
        "...KGGYYYYYGGGK...", // 2
        "..KGGGKKKKKGGGGK..", // 3
        ".KGGGGGGGGGGGGGGK.", // 4
        ".KGGGGGWWWWGGGGGK.", // 5
        "KGGGGGGWWWWGGGGGGK", // 6
        "KGGYYYYYYYYYYYYGGK", // 7
        "KGGYYYYYYYYYYYYGGK", // 8
        ".KGGYYYYYYYYYYGGK.", // 9
        ".KGGYYYYYYYYYYGGK.", // 10
        "..KGGGYYYYYYGGGK..", // 11
        "..KKGGGGGGGGGGKK..", // 12
        ".KGGK.KKKKKK.KGGK.", // 13
        ".KGGK........KGGK.", // 14
        "..KK..........KK.."  // 15
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'G' to Color(0xFF27AE60), // Emerald green skin
        'Y' to Color(0xFFF1C40F), // Yellow belly & eye iris
        'W' to Color(0xFFFFFFFF)  // Eye sclera
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawIfrit() {
    val matrix = arrayOf(
        ".KK..............KK...", // 0
        "KYYK............KYYK..", // 1
        ".KYYK..........KYYK...", // 2
        "..KYYKKKKKKKKKKYYK....", // 3
        "...KRRRRRRRRRRRRK.....", // 4
        "...KRRRRWERRE3RRK.....", // 5
        "...KRRRRRRRRRRRRK.....", // 6
        "...KRRRRWWWW3RRRK.....", // 7
        "..KYYYYRRRRRRYYYYK....", // 8
        ".KYYYYYRRRRRRYYYYYK...", // 9
        "KYYYYYYRRRRRRYYYYYYK..", // 10
        ".KRRRRRRRRRRRRRRRRK...", // 11
        "..KRRRRRRRRRRRRRRK....", // 12
        "...KRRRRRRRRRRRRK.....", // 13
        "...KRRRRRRRRRRRRK.....", // 14
        "...KRRRRRRRRRRRRK.....", // 15
        "...KRRRRRRRRRRRRK.....", // 16
        "...KRRRRRRRRRRRRK.....", // 17
        "...KRRRRK....KRRRRK...", // 18
        "...KRRRRK....KRRRRK...", // 19
        "...KRRRRK....KRRRRK...", // 20
        "..KYYYYYK...KYYYYYK...", // 21
        "..KYYYYYK...KYYYYYK...", // 22
        "..KKKKKKK...KKKKKKK..."  // 23
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'R' to Color(0xFFC0392B), // Fiery crimson red skin
        '3' to Color(0xFFE74C3C), // Bright orange-red highlights
        'Y' to Color(0xFFF1C40F), // Gold horns & arm bracers/greaves
        'W' to Color(0xFFFFFFFF), // White fangs
        'E' to Color(0xFFFFCC00)  // Glowing yellow eyes
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawOmega() {
    val matrix = arrayOf(
        "........KKKKKK..........", // 0
        ".......KYYYYYYK.........", // 1
        "......KYYYYYYYYK........", // 2
        ".....KSSSSSSSSSSK.......", // 3
        "....KSSSSEERSSSSSK......", // 4
        "...KSSSSSSSSSSSSSSK.....", // 5
        "..KSSSSSSSSSSSSSSSSK....", // 6
        ".KSSSSSSSSSSSSSSSSSSK...", // 7
        "KSSSSSSSSSSSSSSSSSSSSK..", // 8
        "KSSSSSSSSSSSSSSSSSSSSK..", // 9
        ".KSSSSSSSSSSSSSSSSSSK...", // 10
        "..KSSSSSSSSSSSSSSSSK....", // 11
        "..KYYK.KSSSSSSK.KYYK....", // 12
        ".KYYK..KSSSSSSK..KYYK...", // 13
        "KYYK...KSSSSSSK...KYYK..", // 14
        "KYYK...KSSSSSSK...KYYK..", // 15
        "KYYK...KSSKKSSK...KYYK..", // 16
        "KKK....KSSK.KSSK...KKK..", // 17
        ".......KSSK.KSSK........", // 18
        ".......KKKK.KKKK........"  // 19
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'S' to Color(0xFF7F8C8D), // Steel silver body
        'Y' to Color(0xFFF1C40F), // Gold limbs & cannon accents
        'E' to Color(0xFFFF0000), // Glowing red laser visor eye
        'R' to Color(0xFFFF5252)  // Laser core center
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawHellhound() {
    val matrix = arrayOf(
        ".......KKKK.........", // 0
        "......KRRRRK...KK...", // 1
        ".....KRRRRRRK.KRRK..", // 2
        "....KRRRRRRRRKRRRK..", // 3
        "...KYYYRRRRRRRRRRK..", // 4
        "..KYYYYYRRRRRREWSK..", // 5
        ".KDDYYYYRRRRRRWW3K..", // 6
        "KDDDDYYYYRRRRRRRRK..", // 7
        "KDDDDDDYYYRRRRRRRK..", // 8
        ".KDDDDDDYYYYRRKK....", // 9
        "..KDDDDDDYYYK.......", // 10
        "...KDDYYYYYK........", // 11
        "...KRRK..KRRK.......", // 12
        "...KRRK..KRRK.......", // 13
        "...KDDK..KDDK.......", // 14
        "...KKK....KKK.......", // 15
        "....................", // 16
        "...................."  // 17
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'R' to Color(0xFF922B21), // Dark red fur
        'D' to Color(0xFF641E16), // Dark charcoal/red shadow
        'Y' to Color(0xFFF39C12), // Fiery orange spine mane
        'E' to Color(0xFFF1C40F), // Yellow eye
        'W' to Color(0xFFFFFFFF), // Teeth
        'S' to Color(0xFFE74C3C), // Red inner mouth
        '3' to Color(0xFFD35400)  // Dark flame accent
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawJenova() {
    val matrix = arrayOf(
        "........KKKK..........", // 0
        ".......KPPPPK.........", // 1
        "......KPPPPPPK........", // 2
        ".....KPPPPPPPPK.......", // 3
        "....KPPPWWWWPPPK......", // 4
        "....KPPWERRE3PPK......", // 5
        "....KPPPWWWWPPPK......", // 6
        "...KCCCCWWWWCCCCK.....", // 7
        "..KCCCCCCWWCCCCCCK....", // 8
        ".KCCCCCCCWWCCCCCCCK...", // 9
        ".KCCCCCCCWWCCCCCCCK...", // 10
        ".KPPPPPPPWWPPPPPPPK...", // 11
        "..KPPPPPPWWPPPPPPK....", // 12
        "...KPPPPCRRCPPPPK.....", // 13
        "....KPPCRRRRCPPK......", // 14
        "....KPPCRRRRCPPK......", // 15
        "....KPPPCRRCPPPK......", // 16
        "....KPPPPPPPPPPK......", // 17
        "....KPPPP..PPPPK......", // 18
        "....KPPK....KPPK......", // 19
        "....KPPK....KPPK......", // 20
        "....KPPK....KPPK......", // 21
        "....KKKK....KKKK......", // 22
        "......................"  // 23
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'P' to Color(0xFF5B2C6F), // Deep purple bio-structure
        'C' to Color(0xFF16A085), // Cyan/teal wing-like organic appendages
        'W' to Color(0xFFECF0F1), // Pale alien torso/face
        'E' to Color(0xFFFF2A2A), // Glowing crimson eyes
        'R' to Color(0xFFE74C3C), // Red organic heart/core
        '3' to Color(0xFF8E44AD)  // Purple shading
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawEvrae() {
    val matrix = arrayOf(
        ".KK......KK......KK.....", // 0
        "KYYK....KYYK....KYYK....", // 1
        "KYSK....KYSK....KYSK....", // 2
        ".KYYK...KYYK...KYYK.....", // 3
        "..KYYK..KYYK..KYYK......", // 4
        "...KYYKKKYYKKKYYK.......", // 5
        "....KCCCCCCCCCCK........", // 6
        "...KCCCCCCCCCCCCK.......", // 7
        "..KCCCCCCCCCCCCCCK......", // 8
        ".KCCCCCCCCCCCCCCCCK.....", // 9
        "KCCCCCCCCCCCCCCCCCCK....", // 10
        "KCCCCCCCCCCCCCCCCCCK....", // 11
        ".KCCCCCCCCCCCCCCCCK.....", // 12
        "..KCCCCCCCCCCCCCCK......", // 13
        "...KCCCCCCCCCCCCK.......", // 14
        "....KCCCCCCCCCCK........", // 15
        ".....KCCK....KCCK.......", // 16
        ".....KCCK....KCCK.......", // 17
        ".....KCCK....KCCK.......", // 18
        "....KCCCK...KCCCK.......", // 19
        "....KKKKK...KKKKK.......", // 20
        "........................"  // 21
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline
        'C' to Color(0xFF3498DB), // Celestial cyan dragon body
        'S' to Color(0xFFECF0F1), // Silver dragon scales
        'Y' to Color(0xFFF1C40F)  // Majestic gold horns & fins
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawAstos() {
    val matrix = arrayOf(
        ".......KKKKK........",
        "......KBBBBBK.......",
        ".....KBBBBBBBK......",
        "....KBBYYYYYBBK.....",
        "....KBBWWWWWBBK.KK..",
        "....KBBWERREBBKKSK..",
        "....KBBWWWWWBBKKSK..",
        "...KBBBBBBBBBBBKSK..",
        "..KBBBBBBBBBBBBKKSK.",
        ".KBBBBBBBBBBBBBBKSK.",
        ".KBBBBBBBBBBBBBBKSK.",
        ".KBBBBBBBBBBBBBBKSK.",
        "..KBBBBBBBBBBBBKKSK.",
        "...KBBBBBBBBBBBKSK..",
        "....KDDDDDDDDDKKSK..",
        "....KBBKKKKKBBKKSK..",
        "....KBBK...KBBK.KK..",
        "....KBBK...KBBK.....",
        "....KDDK...KDDK.....",
        "....KKKK...KKKK....."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'B' to Color(0xFF1F618D),
        'D' to Color(0xFF114B72),
        'Y' to Color(0xFFF1C40F),
        'W' to Color(0xFFECF0F1),
        'E' to Color(0xFFE74C3C),
        'S' to Color(0xFF795548)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawMarilith() {
    val matrix = arrayOf(
        ".......KKKKK........",
        "......KPPPPP3K......",
        ".....KPPPPPPPPK.....",
        "....KPPPWRYWPPPK....",
        "....KPPPPPPPPPPK....",
        "....KPPPPPPPPPPK....",
        "...KSSPPPPPPPPSSK...",
        "..KSSSSPPPPPPSSSSK..",
        ".KSSSSSSRRRRSSSSSSK.",
        "KSSSSSSSRRRRSSSSSSSK",
        ".KSSSSSSRRRRSSSSSSK.",
        "..KSSSSSRRRRSSSSSK..",
        "...KRRRRRRRRRRRRK...",
        "....KRRRRRRRRRRK....",
        ".....KRRRRRRRRK.....",
        "......KRRRRRRK......",
        ".......KRRRRK.......",
        "......KRRRRRRK......",
        ".....KRRRRRRRRK.....",
        "....KKKKKKKKKKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'P' to Color(0xFF8E44AD),
        '3' to Color(0xFFA569BD),
        'W' to Color(0xFFECF0F1),
        'R' to Color(0xFFC0392B),
        'Y' to Color(0xFFF1C40F),
        'S' to Color(0xFFBDC3C7)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawLeon() {
    val matrix = arrayOf(
        ".......KKKKKK.......",
        "......KCCCCCCK......",
        ".....KCCCCCCCCK.....",
        "....KCCCCEERCCCK....",
        "....KCCCCCCCCCCK....",
        "...KPPCCCCCCCCPPK...",
        "..KPPPCCCCCCCCPPPK..",
        ".KPPPPCCCCCCCCPPPPK.",
        ".KPPPPCCCCCCSSPPPPK.",
        ".KPPPPCCCCCCSSPPPPK.",
        "..KPPPCCCCCCSSPPPK..",
        "...KPPCCCCCCSSPPK...",
        "....KCCCCCCCCCK.....",
        "....KCCCCCCCCCK.....",
        "....KCCCCKCCCCK.....",
        "....KCCCK.KCCCK.....",
        "....KCCCK.KCCCK.....",
        "....KDDK...KDDK.....",
        "....KKKK...KKKK....."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'C' to Color(0xFF2C3E50),
        'P' to Color(0xFF6C3483),
        'R' to Color(0xFFE74C3C),
        'E' to Color(0xFFFF5252),
        'D' to Color(0xFF1A252F),
        'S' to Color(0xFFBDC3C7)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawBorghen() {
    val matrix = arrayOf(
        ".......KKKKKK.......",
        "......KWWWWWWK......",
        ".....KWWWWWWWWK.....",
        "....KWWWWERREWWK....",
        "....KWWWWWWWWWWK....",
        "...KYYYYYYYYYYYYK...",
        "..KYYYYYYYYYYYYYYK..",
        ".KYYYYYYYYYYYYYYYYK.",
        ".KYYYYYYYYYYYYYYYYK.",
        ".KYYYYYYYYYYYYYYYYK.",
        "..KYYYYYYYYYYYYYYK..",
        "...KYYYYYYYYYYYYK...",
        "....KYYYYYYYYYYK....",
        "....KYYYYKYYYYK.....",
        "....KYYYK.KYYYK.....",
        "....KYYYK.KYYYK.....",
        "....KDDK...KDDK.....",
        "....KKKK...KKKK....."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'W' to Color(0xFFF3C59D),
        'Y' to Color(0xFFD4AC0D),
        'R' to Color(0xFFE74C3C),
        'E' to Color(0xFF2C3E50),
        'D' to Color(0xFF7D6608)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGottos() {
    val matrix = arrayOf(
        "......KKYYYYKK......",
        ".....KYYYYYYYYK.....",
        "....KYYYYYYYYYYK....",
        "....KYYYYWERREYK....",
        "....KYYYYWWWWYYK....",
        "...KBBBBYYYYBBBBK...",
        "..KBBBBBBYYBBBBBBK..",
        ".KBBBBBBBBBBBBBBBBK.",
        ".KBBBBBBBBBBBBBBBBK.",
        ".KBBBBBBBBBBBBBBBBK.",
        "..KBBBBBBBBBBBBBBK..",
        "...KBBBBBBBBBBBBK...",
        "....KBBBBBBBBBBK....",
        "....KBBBBKBBBBK.....",
        "....KBBBK.KBBBK.....",
        "....KBBBK.KBBBK.....",
        "....KDDK...KDDK.....",
        "....KKKK...KKKK....."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'B' to Color(0xFF2980B9),
        'W' to Color(0xFFF3C59D),
        'R' to Color(0xFFE74C3C),
        'E' to Color(0xFF17202A),
        'D' to Color(0xFF1F618D)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawRoundworm() {
    val matrix = arrayOf(
        "......KKKKKK......",
        "....KOOOOOOOOK....",
        "...KOOOOOOOOOOK...",
        "..KOOWWWWWWWWOOK..",
        ".KOOWWEERRRRWEEOK.",
        ".KOOWWEERRRRWEEOK.",
        "KOOWWWWWWWWWWWWOOK",
        "KOOOOOOOOOOOOOOOOK",
        ".KOOOOOOOOOOOOOOK.",
        "..KOOOOOOOOOOOOK..",
        "...KOOOOOOOOOOK...",
        "....KOOOOOOOOK....",
        ".....KOOOOOOK.....",
        "......KOOOOK......",
        ".......KOOK.......",
        "........KK........"
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'O' to Color(0xFFE67E22),
        'W' to Color(0xFFECF0F1),
        'R' to Color(0xFFC0392B),
        'E' to Color(0xFFFF5252)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawCyclone() {
    val matrix = arrayOf(
        "..KKKKKKKKKKKKKK..",
        ".KCCCCCCCCCCCCCCK.",
        "KCCCCCCCCCCCCCCCCK",
        ".KCCCCCCCCCCCCCCK.",
        "..KCCCCCCCCCCCCK..",
        "...KCCCCWWCCCCK...",
        "....KCCWEERCCK....",
        ".....KCWWWWCK.....",
        "......KCCCCK......",
        ".......KCCK.......",
        "........KC........",
        ".......KCCK.......",
        "......KCCCCK......",
        ".....KCCCCCCK.....",
        "....KCCCCCCCCK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'C' to Color(0xFF5DADE2),
        'W' to Color(0xFFECF0F1),
        'E' to Color(0xFFF1C40F),
        'R' to Color(0xFFE74C3C)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawNeptoDragon() {
    val matrix = arrayOf(
        "............KKKK........",
        "...........KCCCCK...KK..",
        "..........KCCCCCRK.KCK..",
        ".........KCCCCCCCCKCCK..",
        "........KCCCCCCCCCCK....",
        "...KKKKKCCCCCCCCCCCK....",
        "..KBBBBBCCCCCCCCCCCK....",
        ".KBBBBBBBCCCCCCCCCCK....",
        "KBBBBBBBBBCCCCCCCCCK....",
        ".KBBBBBBBBBCCCCCCCK.....",
        "..KBBBBBBBBBCCCCCK......",
        "...KKBBBBBBBCCCCK.......",
        ".....KBBBBBBCCCCK.......",
        "......KBBBBBCCCCK.......",
        ".......KBBBBCCCCK.......",
        "........KCCC.CCCK.......",
        "........KCCK.KCCK.......",
        "........KCCK.KCCK.......",
        ".......KYYCK.KYYCK......",
        ".......KKKKK.KKKKK......"
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'C' to Color(0xFF1ABC9C),
        'B' to Color(0xFF16A085),
        'R' to Color(0xFFE74C3C),
        'Y' to Color(0xFFF1C40F)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGaruda() {
    val matrix = arrayOf(
        "......KRRK........",
        ".....KRRRRK.......",
        "....KYYYYYYK......",
        "...KYYYYYYYYK.....",
        "..KYYYYYRYWSK.....",
        "..KYYYYYYYYWWK....",
        "..KYYYYYYYYKK.....",
        "...KYYYYYYK.......",
        ".KK.KYYYYYYK.KK...",
        "KRRK.KYYYYYK.KRRK.",
        "KRRRK.KYYYK.KRRRK.",
        ".KRRRK.KYYK.KRRRK.",
        "..KRRRRKYYKRRRRK..",
        "...KRRRRKKRRRRK...",
        "....KRRRRRRRRK....",
        ".....KYYYYYYK.....",
        ".....KYYKKYYK.....",
        ".....KOOK.KOOK....",
        ".....KKKK.KKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'R' to Color(0xFFE74C3C),
        'S' to Color(0xFFFFFFFF),
        'W' to Color(0xFFE67E22),
        'O' to Color(0xFFD35400)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGoldor() {
    val matrix = arrayOf(
        ".......KKYYYYKK.......",
        "......KYYYYYYYYK......",
        ".....KYYYYYYYYYYK.....",
        "....KYYYYWERREYYK.....",
        "....KYYYYWWWWYYYK.....",
        "...KYYYYYYYYYYYYYK.KK.",
        "..KYYYYYYYYYYYYYYYKKSK",
        ".KYYYYYYYYYYYYYYYYKKSK",
        ".KYYYYYYYYYYYYYYYYKKSK",
        ".KYYYYYYYYYYYYYYYYKKSK",
        "..KYYYYYYYYYYYYYYYKKSK",
        "...KYYYYYYYYYYYYYK.KK.",
        "....KYYYYYYYYYYYK.....",
        "....KYYYYKYYYYKK......",
        "....KYYYK.KYYYK.......",
        "....KDDK...KDDK.......",
        "....KKKK...KKKK......."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'D' to Color(0xFFB7950B),
        'W' to Color(0xFFFFFFFF),
        'R' to Color(0xFFE74C3C),
        'E' to Color(0xFF17202A),
        'S' to Color(0xFFF39C12)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawXande() {
    val matrix = arrayOf(
        ".......KKWWWWKK.......",
        "......KWWWWWWWWK......",
        ".....KWWWWWWWWWWK.....",
        "....KRRRWERRE3RRK.....",
        "....KRRRRRRRRRRRK.....",
        "...KPPPPPRRRRPPPPPK...",
        "..KPPPPPPPRRPPPPPPK..",
        ".KPPPPPPPPPPPPPPPPK..",
        ".KPPPPPPPPPPPPPPPPK..",
        ".KPPPPPPPPPPPPPPPPK..",
        "..KPPPPPPPPPPPPPPK...",
        "...KPPPPPPPPPPPPK....",
        "....KPPPPPPPPPPK.....",
        "....KPPPPKPPPPK......",
        "....KPPPK.KPPPK......",
        "....KDDK...KDDK......",
        "....KKKK...KKKK......"
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'W' to Color(0xFFECF0F1),
        'R' to Color(0xFFC0392B),
        'E' to Color(0xFFF1C40F),
        '3' to Color(0xFFE74C3C),
        'P' to Color(0xFF4A235A),
        'D' to Color(0xFF2C3E50)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawMistDragon() {
    val matrix = arrayOf(
        "..........KKKK..........",
        ".........KCCCCK...KK....",
        "........KCCCCCRK.KCCK...",
        ".......KCCCCCCCCKCCCK...",
        "......KCCCCCCCCCCCCCK...",
        ".....KCCCCCCCCCCCCCCK...",
        "....KCCCCCCCCCCCCCCCK...",
        "...KCCCCCCCCCCCCCCCCK...",
        "..KCCCCCCCCCCCCCCCCCK...",
        ".KCCCCCCCCCCCCCCCCCK....",
        ".KCCCCCCCCCCCCCCCCK.....",
        "..KCCCCCCCCCCCCCCK......",
        "...KCCCCCCCCCCCCK.......",
        "....KCCCCK..KCCCCK......",
        "....KCCCCK..KCCCCK......",
        "...KCCCCCK.KCCCCCK......",
        "...KKKKKKK.KKKKKKK......"
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'C' to Color(0xFFA3E4D7),
        'R' to Color(0xFFF1C40F)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawAntlionBoss() {
    val matrix = arrayOf(
        "KK....................KK",
        "KYYK................KYYK",
        ".KYYK..............KYYK.",
        "..KYYKKKKKKKKKKKKKKYYK..",
        "...KYYYYYYYYYYYYYYYYK...",
        "...KYYYYYWWWWYYYYYYYK...",
        "...KYYYYWERREWYYYYYYK...",
        "...KYYYYYWWWWYYYYYYYK...",
        "..KYYYYYYYYYYYYYYYYYK...",
        ".KYYYYYYYYYYYYYYYYYYYK..",
        "KYYYYYYYYYYYYYYYYYYYYYK.",
        "KYYYYYYYYYYYYYYYYYYYYYK.",
        ".KYYYYYYYYYYYYYYYYYYYK..",
        "..KYYYYYYYYYYYYYYYYYK...",
        "...KYYYYK......KYYYYK...",
        "...KYYYYK......KYYYYK...",
        "...KKKKKK......KKKKKK..."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFD4AC0D),
        'W' to Color(0xFFFFFFFF),
        'R' to Color(0xFFC0392B),
        'E' to Color(0xFFE74C3C)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGolbez() {
    val matrix = arrayOf(
        "..KK................KK..",
        ".KYYK..............KYYK.",
        "KYYYK..............KYYYK",
        ".KYYYKKKKKKKKKKKKKKYYYK.",
        "..KCCCCCCCCCCCCCCCCCCK..",
        "...KCCCCCCCCCCCCCCCK....",
        "...KCCCCCCCEERCCCCCK....",
        "...KCCCCCCCCCCCCCCCK....",
        "..KBBCCCCCCCCCCCCCCBK...",
        ".KBBBCCCCCCCCCCCCCCBBK..",
        "KBBBBCCCCCCCCCCCCCCBBBK.",
        "KBBBBCCCCCCCCCCCCCCBBBK.",
        ".KBBBCCCCCCCCCCCCCCBBK..",
        "..KBBCCCCCCCCCCCCCCBK...",
        "...KCCCCCCCCCCCCCCCK....",
        "....KCCCCCCCCCKCCCCK....",
        "....KCCCCK....KCCCCK....",
        "....KCCCCK....KCCCCK....",
        "....KDDK......KDDK......",
        "....KKKK......KKKK......"
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'C' to Color(0xFF1B2631),
        'B' to Color(0xFF1B4F72),
        'E' to Color(0xFFFF2A2A),
        'R' to Color(0xFFE74C3C),
        'D' to Color(0xFF11161B)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawCagnazzo() {
    val matrix = arrayOf(
        ".........KKKKKK.........",
        "........KBBBBBBK........",
        ".......KBBBBBBBBK.......",
        "......KBBBBBBBBBBK......",
        ".....KBBBBBBBBBBBBK.....",
        "....KBBBBBBBBBBBBBBK....",
        "...KBBBBBWERREBBBBBBK...",
        "..KBBBBBBBBBBBBBBBBBBK..",
        ".KBBBBBBBBBBBBBBBBBBBBK.",
        "KBBBBBBBBBBBBBBBBBBBBBBK",
        "KTTTTTTTTTTTTTTTTTTTTTTK",
        "KTTTTTTTTTTTTTTTTTTTTTTK",
        ".KTTTTTTTTTTTTTTTTTTTTK.",
        "..KTTTTTTTTTTTTTTTTTTK..",
        "...KTTK.KTTTTK.KTTK.....",
        "...KKKK.KKKKKK.KKKK....."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'B' to Color(0xFF2980B9),
        'T' to Color(0xFFD4AC0D),
        'R' to Color(0xFFE74C3C),
        'E' to Color(0xFFF1C40F)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawBarbariccia() {
    val matrix = arrayOf(
        ".......KKYYYYKK.......",
        "......KYYYYYYYYK......",
        ".....KYYYYYYYYYYK.....",
        "....KYYYYWERREYYK.....",
        "....KYYYYWWWWYYYK.....",
        "...KGGGGYYYYGGGGK.....",
        "..KGGGGGGYYGGGGGGK....",
        ".KGGGGGGGGGGGGGGGGK...",
        ".KGGGGGGGGGGGGGGGGK...",
        ".KGGGGGGGGGGGGGGGGK...",
        "..KGGGGGGGGGGGGGGK....",
        "...KGGGGGGGGGGGGK.....",
        "....KGGGGGGGGGGK......",
        "....KGGGGKGGGGK.......",
        "....KGGGK.KGGGK.......",
        "....KDDK...KDDK.......",
        "....KKKK...KKKK......."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'G' to Color(0xFF27AE60),
        'W' to Color(0xFFF3C59D),
        'R' to Color(0xFFE74C3C),
        'E' to Color(0xFF17202A),
        'D' to Color(0xFF196F3D)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawScarmiglione() {
    val matrix = arrayOf(
        ".......KKKKKK.......",
        "......KBBBBBBK......",
        ".....KBBBBBBBBK.....",
        "....KBBBWWWWBBBK....",
        "....KBBBWERREBBK....",
        "....KBBBWWWWBBBK....",
        "....KBBBWBWBBBBK....",
        "....KBBBBWWBBBBK....",
        "...KBBBBBBBBBBBBK...",
        "..KBBBBBBBBBBBBBBK..",
        ".KBBBBBBBDDBBBBBBBK.",
        ".KBBBBBBBDDBBBBBBBK.",
        ".KBBBBBBBDDBBBBBBBK.",
        ".KBBBBBBBDDBBBBBBBK.",
        ".KBBBBBBBDDBBBBBBBK.",
        ".KBBBBBBBDDBBBBBBBK.",
        "..KBBBBBBDDBBBBBBK..",
        "...KKKKKKKKKKKKKK..."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'B' to Color(0xFF6E2C00),
        'D' to Color(0xFF421B00),
        'W' to Color(0xFFECF0F1),
        'E' to Color(0xFFE74C3C)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawRubicante() {
    val matrix = arrayOf(
        ".......KKKKKK.......",
        "......KRRRRRRK......",
        ".....KRRRRRRRRK.....",
        "....KRRRWERRE3RK....",
        "....KRRRRRRRRRRK....",
        "...KRRRRRRRRRRRRK...",
        "..KRRRRRRRRRRRRRRK..",
        ".KRRRRRRRRRRRRRRRRK.",
        ".KRRRRRRRRRRRRRRRRK.",
        ".KRRRRRRRRRRRRRRRRK.",
        "..KRRRRRRRRRRRRRRK..",
        "...KRRRRRRRRRRRRK...",
        "....KRRRRRRRRRRK....",
        "....KRRRRKRRRRK.....",
        "....KRRRK.KRRRK.....",
        "....KDDK...KDDK.....",
        "....KKKK...KKKK....."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'R' to Color(0xFFC0392B),
        '3' to Color(0xFFE74C3C),
        'E' to Color(0xFFF1C40F),
        'D' to Color(0xFF7B241C)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawDarkBahamut() {
    val matrix = arrayOf(
        "..........KKKK..........",
        ".........KCCCCK...KK....",
        "........KCCCCCRK.KCCK...",
        ".......KCCCCCCCCKCCCK...",
        "......KCCCCCCCCCCCCCK...",
        ".....KCCCCCCCCCCCCCCK...",
        "....KCCCCCCCCCCCCCCCK...",
        "...KCCCCCCCCCCCCCCCCK...",
        "..KCCCCCCCCCCCCCCCCCK...",
        ".KCCCCCCCCCCCCCCCCCK....",
        ".KCCCCCCCCCCCCCCCCK.....",
        "..KCCCCCCCCCCCCCCK......",
        "...KCCCCCCCCCCCCK.......",
        "....KCCCCK..KCCCCK......",
        "....KCCCCK..KCCCCK......",
        "...KCCCCCK.KCCCCCK......",
        "...KKKKKKK.KKKKKKK......"
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'C' to Color(0xFF1A252F),
        'R' to Color(0xFFE74C3C)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawZeromus() {
    val matrix = arrayOf(
        "........KKKKKK..........",
        ".......KPPPPPPK.........",
        "......KPPPPPPPPK........",
        ".....KPPPPERREPPK.......",
        "....KPPPPPPPPPPPPK......",
        "...KPPPPPCRRRCPPPPK.....",
        "..KPPPPPCRRRRRCPPPPK....",
        ".KPPPPPPCRRRRRCPPPPPK...",
        ".KPPPPPPCRRRRRCPPPPPK...",
        ".KPPPPPPCRRRRRCPPPPPK...",
        "..KPPPPPCRRRRCPPPPK.....",
        "...KPPPPPCRRCPPPPK......",
        "....KPPPPPPPPPPPK.......",
        "....KPPPP..PPPPK........",
        "....KPPK....KPPK........",
        "....KKKK....KKKK........"
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'P' to Color(0xFF5B2C6F),
        'C' to Color(0xFF8E44AD),
        'R' to Color(0xFFFF0000),
        'E' to Color(0xFFF1C40F)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawWingRaptor() {
    val matrix = arrayOf(
        "......KRRK........",
        ".....KRRRRK.......",
        "....KYYYYYYK......",
        "...KYYYYYYYYK.....",
        "..KYYYYYRYWSK.....",
        "..KYYYYYYYYWWK....",
        "..KYYYYYYYYKK.....",
        "...KYYYYYYK.......",
        ".KK.KYYYYYYK.KK...",
        "KGGK.KYYYYYK.KGGK.",
        "KGGGK.KYYYK.KGGGK.",
        ".KGGGK.KYYK.KGGGK.",
        "..KGGGGKYYKGGGGK..",
        "...KGGGGKKGGGGK...",
        "....KGGGGGGGGK....",
        ".....KYYYYYYK.....",
        ".....KYYKKYYK.....",
        ".....KOOK.KOOK....",
        ".....KKKK.KKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'R' to Color(0xFFE74C3C),
        'G' to Color(0xFF27AE60),
        'S' to Color(0xFFFFFFFF),
        'W' to Color(0xFFE67E22),
        'O' to Color(0xFFD35400)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawKarlabos() {
    val matrix = arrayOf(
        "KK....................KK",
        "KCCK................KCCK",
        ".KCCK..............KCCK.",
        "..KCCKKKKKKKKKKKKKKCCK..",
        "...KCCCCCCCCCCCCCCCCK...",
        "...KCCCCCWWWWCCCCCCYK...",
        "...KCCCCWERRECCCCCCYK...",
        "...KCCCCCWWWWCCCCCCYK...",
        "..KCCCCCCCCCCCCCCCCYK...",
        ".KCCCCCCCCCCCCCCCCCCCK..",
        "KCCCCCCCCCCCCCCCCCCCCCK.",
        "KCCCCCCCCCCCCCCCCCCCCCK.",
        ".KCCCCCCCCCCCCCCCCCCCK..",
        "..KCCCCCCCCCCCCCCCCYK...",
        "...KCCCCK......KCCCCK...",
        "...KCCCCK......KCCCCK...",
        "...KKKKKK......KKKKKK..."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'C' to Color(0xFF1ABC9C),
        'Y' to Color(0xFFF1C40F),
        'W' to Color(0xFFFFFFFF),
        'R' to Color(0xFFE74C3C),
        'E' to Color(0xFF17202A)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGilgamesh() {
    val matrix = arrayOf(
        "......KKYYYYKK......",
        ".....KYYYYYYYYK.....",
        "....KYYYYYYYYYYK....",
        "....KYYYYWERREYK....",
        "....KYYYYWWWWYYK....",
        "...KRRRRYYYYRRRRK...",
        "..KRRRRRRYYRRRRRRK..",
        ".KRRRRRRRRRRRRRRRRK.",
        ".KRRRRRRRRRRRRRRRRK.",
        ".KRRRRRRRRRRRRRRRRK.",
        "..KRRRRRRRRRRRRRRK..",
        "...KRRRRRRRRRRRRK...",
        "....KRRRRRRRRRRK....",
        "....KRRRRKRRRRK.....",
        "....KRRRK.KRRRK.....",
        "....KDDK...KDDK.....",
        "....KKKK...KKKK....."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'R' to Color(0xFFC0392B),
        'W' to Color(0xFFFFFFFF),
        'E' to Color(0xFF17202A),
        'D' to Color(0xFF7B241C)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawAtomos() {
    val matrix = arrayOf(
        "........KKKKKK..........",
        ".......KPPPPPPK.........",
        "......KPPPPPPPPK........",
        ".....KPPWWWWWWPPK.......",
        "....KPPWERREEEWPK.......",
        "....KPPWERREEEWPK.......",
        "....KPPWERREEEWPK.......",
        ".....KPPWWWWWWPPK.......",
        "......KPPPPPPPPK........",
        ".......KPPPPPPK.........",
        "........KKKKKK.........."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'P' to Color(0xFF6C3483),
        'W' to Color(0xFF17202A),
        'R' to Color(0xFFE74C3C),
        'E' to Color(0xFF8E44AD)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawExdeath() {
    val matrix = arrayOf(
        "..KK................KK..",
        ".KYYK..............KYYK.",
        "KYYYK..............KYYYK",
        ".KYYYKKKKKKKKKKKKKKYYYK.",
        "..KBBBBBBBBBBBBBBBBBBK..",
        "...KBBBBBBBBBBBBBBBBK...",
        "...KBBBBBBBEERBBBBBBK...",
        "...KBBBBBBBBBBBBBBBBK...",
        "..KPPBBBBBBBBBBBBBBPPK..",
        ".KPPPBBBBBBBBBBBBBBPPPK.",
        "KPPPPBBBBBBBBBBBBBBPPPPK",
        "KPPPPBBBBBBBBBBBBBBPPPPK",
        ".KPPPBBBBBBBBBBBBBBPPPK.",
        "..KPPBBBBBBBBBBBBBBPPK..",
        "...KBBBBBBBBBBBBBBBBK...",
        "....KBBBBBBBBKBBBBBBK...",
        "....KBBBBK...KBBBBKK....",
        "....KDDK......KDDK......",
        "....KKKK......KKKK......"
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'B' to Color(0xFF1F618D),
        'P' to Color(0xFF6C3483),
        'E' to Color(0xFFFF0000),
        'R' to Color(0xFFE74C3C),
        'D' to Color(0xFF114B72)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawNeoExdeath() {
    val matrix = arrayOf(
        "........KKKKKK..........",
        ".......KPPPPPPK.........",
        "......KPPPPPPPPK........",
        ".....KPPPPERREPPK.......",
        "....KPPPPPPPPPPPPK......",
        "...KPPPPPCRRRCPPPPK.....",
        "..KPPPPPCRRRRRCPPPPK....",
        ".KPPPPPPCRRRRRCPPPPPK...",
        ".KPPPPPPCRRRRRCPPPPPK...",
        ".KPPPPPPCRRRRRCPPPPPK...",
        "..KPPPPPCRRRRCPPPPK.....",
        "...KPPPPPCRRCPPPPK......",
        "....KPPPPPPPPPPPK.......",
        "....KPPPP..PPPPK........",
        "....KPPK....KPPK........",
        "....KKKK....KKKK........"
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'P' to Color(0xFF4A235A),
        'C' to Color(0xFF8E44AD),
        'R' to Color(0xFFC0392B),
        'E' to Color(0xFFF1C40F)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawWhelk() {
    val matrix = arrayOf(
        ".........KKKKKK.........",
        "........KPPPPPPK........",
        ".......KPPPPPPPPK.......",
        "......KPPYYYYYYPPK......",
        ".....KPPYWEERREYYPPK....",
        "....KPPYYWWWWWWYYPPK....",
        "...KPPYYYYYYYYYYPPK.....",
        "..KPPYYYYYYYYYYYYPPK....",
        ".KPPYYYYYYYYYYYYYYPPK...",
        "KPPYYYYYYYYYYYYYYYYPPK..",
        ".KPPYYYYYYYYYYYYYYPPK...",
        "..KPPYYYYYYYYYYYYPPK....",
        "...KPPYYYYYYYYYYPPK.....",
        "....KKKKKKKKKKKKKK......"
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'P' to Color(0xFF8E44AD),
        'Y' to Color(0xFFF1C40F),
        'W' to Color(0xFFFFFFFF),
        'R' to Color(0xFFE74C3C),
        'E' to Color(0xFF2C3E50)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawVargas() {
    val matrix = arrayOf(
        ".......KKRRRRKK.......",
        "......KRRRRRRRRK......",
        ".....KRRRRRRRRRRK.....",
        "....KWWWWWERREWWK.....",
        "....KWWWWWWWWWWWK.....",
        "...KBBBBWWWWWWBBBK....",
        "..KBBBBBBWWWWBBBBBK...",
        ".KBBBBBBBBBBBBBBBBBK..",
        ".KBBBBBBBBBBBBBBBBBK..",
        ".KBBBBBBBBBBBBBBBBBK..",
        "..KBBBBBBBBBBBBBBBK...",
        "...KBBBBBBBBBBBBBK....",
        "....KBBBBBBBBBBBK.....",
        "....KBBBBK.KBBBBK.....",
        "....KBBBK...KBBBK.....",
        "....KDDK.....KDDK.....",
        "....KKKK.....KKKK....."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'R' to Color(0xFFC0392B),
        'W' to Color(0xFFF3C59D),
        'B' to Color(0xFF6E2C00),
        'E' to Color(0xFF17202A),
        'D' to Color(0xFF421B00)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawNumber024() {
    val matrix = arrayOf(
        ".......KKKKKK.......",
        "......KCCCCCCK......",
        ".....KCCCCCCCCK.....",
        "....KCCCCEERCCCK....",
        "....KCCCCCCCCCCK....",
        "...KSSCCCCCCCCSSK...",
        "..KSSSCCCCCCSSSSK...",
        ".KSSSSCCCCCCSSSSSK..",
        "KSSSSSRRRRRRSSSSSSK.",
        ".KSSSSRRRRRRSSSSSK..",
        "..KSSSSRRRRSSSSSK...",
        "...KSSSSRRRSSSSK....",
        "....KSSSSSSSSSSK....",
        ".....KCCCCCCCCK.....",
        ".....KCCCCKCCCK.....",
        "....KKKKKK.KKKKKK..."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'C' to Color(0xFF34495E),
        'S' to Color(0xFFBDC3C7),
        'R' to Color(0xFFE74C3C),
        'E' to Color(0xFFF1C40F)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawUltros() {
    val matrix = arrayOf(
        "........KKKKKK..........",
        ".......KPPPPP3K.........",
        "......KPPPPPPPPK........",
        ".....KPPPPPPPPPPK.......",
        "....KPPPPWWWWPPPPK......",
        "...KPPPPWERRE3PPPK......",
        "...KPPPPWWWWWWPPPK......",
        "...KPPPPWWWBWBPPPK......",
        "...KPPPPPWWWWPPPPK......",
        "....KPPPPPPPPPPPK.......",
        "....KCCCCCCCCCCCCK......",
        "...KCC.CC.CC.CC.CCK.....",
        "..KCC..CC..CC..CC..CK...",
        ".KCC...CC..CC..CC...CK..",
        "KCC....CC..CC..CC....CK.",
        "..KK..KK..KK..KK..KK..."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'P' to Color(0xFF8E44AD),
        '3' to Color(0xFFA569BD),
        'C' to Color(0xFF9B59B6),
        'W' to Color(0xFFFFFFFF),
        'E' to Color(0xFF17202A),
        'B' to Color(0xFF17202A),
        'R' to Color(0xFFE74C3C)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawTyphon() {
    val matrix = arrayOf(
        "......KKKKKK......",
        "....KPPPPPPPPK....",
        "...KPPPPPPPPPPK...",
        "..KPPPPWWWWPPPPK..",
        ".KPPPPWERRE3PPPPK.",
        ".KPPPPWWWWWWPPPPK.",
        "KPPPPWWWBWBWWPPPPK",
        "KPPPPPPPPPPPPPPPPK",
        ".KPPPPPPPPPPPPPPK.",
        "..KPPPPPPPPPPPPK..",
        "...KPPPPPPPPPPK...",
        "....KDDDDDDDDK....",
        ".....KKKKKKKK....."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'P' to Color(0xFFE74C3C),
        'D' to Color(0xFFB03A2E),
        'W' to Color(0xFFFFFFFF),
        'E' to Color(0xFF17202A),
        'B' to Color(0xFF17202A),
        '3' to Color(0xFFF1948A)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawAirForce() {
    val matrix = arrayOf(
        "........KKKKKK..........",
        ".......KSSSSSSK.........",
        "......KSSSSSSSSK........",
        ".....KSSSSSSSSSSK.......",
        "....KSSSSEERSSSSSK......",
        "...KSSSSSSSSSSSSSSK.....",
        "..KSSSSSSSSSSSSSSSSK....",
        ".KSSSSSSSSSSSSSSSSSSK...",
        "KSSSSSSSSSSSSSSSSSSSSK..",
        ".KSSSSSSSSSSSSSSSSSSK...",
        "..KYYK.KSSSSSSK.KYYK....",
        ".KYYK..KSSSSSSK..KYYK...",
        "KKK....KSSK.KSSK...KKK.."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'S' to Color(0xFF7F8C8D),
        'Y' to Color(0xFFF1C40F),
        'E' to Color(0xFFFF0000),
        'R' to Color(0xFFFF5252)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGuardian() {
    val matrix = arrayOf(
        ".......KKYYYYKK.......",
        "......KYYYYYYYYK......",
        ".....KYYYYYYYYYYK.....",
        "....KYYYYWERREYYK.....",
        "....KYYYYWWWWYYYK.....",
        "...KSSSSYYYYSSSSK.....",
        "..KSSSSSSYYSSSSSSK....",
        ".KSSSSSSSSSSSSSSSSK...",
        ".KSSSSSSSSSSSSSSSSK...",
        ".KSSSSSSSSSSSSSSSSK...",
        "..KSSSSSSSSSSSSSSK....",
        "...KSSSSSSSSSSSSK.....",
        "....KSSSSSSSSSSK......",
        "....KSSSSKSSSSK.......",
        "....KSSSK.KSSSK.......",
        "....KDDK...KDDK.......",
        "....KKKK...KKKK......."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFD4AC0D),
        'S' to Color(0xFF7F8C8D),
        'W' to Color(0xFFFFFFFF),
        'R' to Color(0xFFE74C3C),
        'E' to Color(0xFF17202A),
        'D' to Color(0xFF7D6608)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawUltimaWeapon() {
    val matrix = arrayOf(
        "..........KKKK..........",
        ".........KCCCCK...KK....",
        "........KCCCCCRK.KCCK...",
        ".......KCCCCCCCCKCCCK...",
        "......KCCCCCCCCCCCCCK...",
        ".....KCCCCCCCCCCCCCCK...",
        "....KCCCCCCCCCCCCCCCK...",
        "...KCCCCCCCCCCCCCCCCK...",
        "..KCCCCCCCCCCCCCCCCCK...",
        ".KCCCCCCCCCCCCCCCCCK....",
        ".KCCCCCCCCCCCCCCCCK.....",
        "..KCCCCCCCCCCCCCCK......",
        "...KCCCCCCCCCCCCK.......",
        "....KCCCCK..KCCCCK......",
        "....KCCCCK..KCCCCK......",
        "...KCCCCCK.KCCCCCK......",
        "...KKKKKKK.KKKKKKK......"
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'C' to Color(0xFF2980B9),
        'R' to Color(0xFFF1C40F)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawKefka() {
    val matrix = arrayOf(
        ".......KKYYYYKK.......",
        "......KYYYYYYYYK......",
        ".....KYYYYWWWWYYK.....",
        "....KYYYYWWWWWWYYK....",
        "....KYYYYWERRE3YYK....",
        "....KYYYYWWWWWWYYK....",
        "...KRRRRYYYYYYYYRRRK..",
        "..KRRRRRRYYYYRRRRRRK..",
        ".KRRRRRRRRRRRRRRRRRRK.",
        ".KRRRRRRRRRRRRRRRRRRK.",
        ".KRRRRRRRRRRRRRRRRRRK.",
        "..KRRRRRRRRRRRRRRRRK..",
        "...KRRRRRRRRRRRRRRK...",
        "....KRRRRRRRRRRRRK....",
        "....KRRRRK..KRRRRK....",
        "....KRRRK....KRRRK....",
        "....KDDK......KDDK....",
        "....KKKK......KKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'W' to Color(0xFFFFFFFF),
        'E' to Color(0xFF8E44AD),
        'R' to Color(0xFFE74C3C),
        '3' to Color(0xFF27AE60),
        'D' to Color(0xFFB03A2E)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGuardScorpion() {
    val matrix = arrayOf(
        "KK....................KK",
        "KBBK................KBBK",
        ".KBBK..............KBBK.",
        "..KBBKKKKKKKKKKKKKKBBK..",
        "...KBBBBBBBBBBBBBBBBK...",
        "...KBBBBBWWWWBBBBBB3K...",
        "...KBBBBWERREBBBBBB3K...",
        "...KBBBBBWWWWBBBBBB3K...",
        "..KBBBBBBBBBBBBBBBB3K...",
        ".KBBBBBBBBBBBBBBBBB3K.",
        "KBBBBBBBBBBBBBBBBBBBBBBK",
        "KBBBBBBBBBBBBBBBBBBBBBBK",
        ".KBBBBBBBBBBBBBBBBB3K.",
        "..KBBBBBBBBBBBBBBBB3K...",
        "...KBBBBK......KBBBBK...",
        "...KBBBBK......KBBBBK...",
        "...KKKKKK......KKKKKK..."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'B' to Color(0xFF1F618D),
        '3' to Color(0xFF114B72),
        'W' to Color(0xFFFFFFFF),
        'R' to Color(0xFFFF0000),
        'E' to Color(0xFFF1C40F)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawAirbuster() {
    val matrix = arrayOf(
        ".......KKKKKK.......",
        "......KCCCCCCK......",
        ".....KCCCCCCCCK.....",
        "....KCCCCEERCCCK....",
        "....KCCCCCCCCCCK....",
        "...KRRCCCCCCCCRRK...",
        "..KRRRCCCCCCCCRRRK..",
        ".KRRRRCCCCCCCCRRRRK.",
        ".KRRRRCCCCCCSSRRRRK.",
        ".KRRRRCCCCCCSSRRRRK.",
        "..KRRRCCCCCCSSRRRK..",
        "...KRRCCCCCCSSRRK...",
        "....KCCCCCCCCCK.....",
        "....KCCCCCCCCCK.....",
        "....KCCCCKCCCCK.....",
        "....KCCCK.KCCCK.....",
        "....KCCCK.KCCCK.....",
        "....KDDK...KDDK.....",
        "....KKKK...KKKK....."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'C' to Color(0xFF7F8C8D),
        'R' to Color(0xFFC0392B),
        'E' to Color(0xFFFF5252),
        'S' to Color(0xFFBDC3C7),
        'D' to Color(0xFF34495E)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawRufus() {
    val matrix = arrayOf(
        ".......KKYYYYKK.......",
        "......KYYYYYYYYK......",
        ".....KYYYYWWWWYYK.....",
        "....KYYYYWWWWWWYYK....",
        "....KYYYYWERRE3YYK....",
        "....KYYYYWWWWWWYYK....",
        "...KWWWWWWWWWWWWWWK.KK",
        "..KWWWWWWWWWWWWWWWWKSK",
        ".KWWWWWWWWWWWWWWWWWWKSK",
        ".KWWWWWWWWWWWWWWWWWWKSK",
        ".KWWWWWWWWWWWWWWWWWWKSK",
        "..KWWWWWWWWWWWWWWWWKSK",
        "...KWWWWWWWWWWWWWWK.KK",
        "....KWWWWWWWWWWWWK....",
        "....KWWWWK..KWWWWK....",
        "....KWWWK....KWWWK....",
        "....KDDK......KDDK....",
        "....KKKK......KKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'W' to Color(0xFFECF0F1),
        'R' to Color(0xFFC0392B),
        'E' to Color(0xFF17202A),
        '3' to Color(0xFFF3C59D),
        'D' to Color(0xFFBDC3C7),
        'S' to Color(0xFF34495E)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawHojo() {
    val matrix = arrayOf(
        ".......KKKKKK.......",
        "......KWWWWWWK......",
        ".....KWWWWWWWWK.....",
        "....KWWWWERREWWK....",
        "....KWWWWWWWWWWK....",
        "...KWWWWWWWWWWWWK.KK",
        "..KWWWWWWWWWWWWWWKSK",
        ".KWWWWWWWWWWWWWWWWKSK",
        ".KWWWWWWWWWWWWWWWWKSK",
        ".KWWWWWWWWWWWWWWWWKSK",
        "..KWWWWWWWWWWWWWWKSK",
        "...KWWWWWWWWWWWWK.KK",
        "....KWWWWWWWWWWK....",
        "....KWWWWK..KWWWWK..",
        "....KWWWK....KWWWK..",
        "....KDDK......KDDK..",
        "....KKKK......KKKK.."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'W' to Color(0xFFECF0F1),
        'E' to Color(0xFF27AE60),
        'R' to Color(0xFF17202A),
        'S' to Color(0xFF2ECC71),
        'D' to Color(0xFFBDC3C7)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawBizarroSephiroth() {
    val matrix = arrayOf(
        "KK....................KK",
        "KSSK................KSSK",
        ".KSSK..............KSSK.",
        "..KSSKKKKKKKKKKKKKKSSK..",
        "...KWWWWWWWWWWWWWWWWK...",
        "...KWWWWWERREWWWWWW3K...",
        "...KWWWWWERREWWWWWW3K...",
        "...KWWWWWWWWWWWWWWWWK...",
        "..KRRRRRRWWWWRRRRRRRK...",
        ".KRRRRRRRRRRRRRRRRRRRK..",
        "KRRRRRRRRRRRRRRRRRRRRRK.",
        "KRRRRRRRRRRRRRRRRRRRRRK.",
        ".KRRRRRRRRRRRRRRRRRRRK..",
        "..KRRRRRRRRRRRRRRRRRK...",
        "...KRRRRK......KRRRRK...",
        "...KKKKKK......KKKKKK..."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'S' to Color(0xFFECF0F1),
        'W' to Color(0xFFECF0F1),
        'R' to Color(0xFFC0392B),
        'E' to Color(0xFF17202A),
        '3' to Color(0xFF922B21)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawSephiroth() {
    val matrix = arrayOf(
        ".......KKYYYYKK.......",
        "......KYYYYYYYYK......",
        ".....KYYYYWWWWYYK.....",
        "....KYYYYWWWWWWYYK....",
        "....KYYYYWERRE3YYK....",
        "....KYYYYWWWWWWYYK....",
        "...KSSSSYYYYYYYYSSSSK.",
        "..KSSSSSSYYSSSSSSSSSK.",
        ".KSSSSSSSSSSSSSSSSSSK.",
        ".KSSSSSSSSSSSSSSSSSSK.",
        ".KSSSSSSSSSSSSSSSSSSK.",
        "..KSSSSSSSSSSSSSSSSK..",
        "...KSSSSSSSSSSSSSSK...",
        "....KSSSSSSSSSSSSK....",
        "....KSSSSK..KSSSSK....",
        "....KSSSK....KSSSK....",
        "....KDDK......KDDK....",
        "....KKKK......KKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFECF0F1),
        'W' to Color(0xFFF3C59D),
        'E' to Color(0xFF17202A),
        'R' to Color(0xFF1ABC9C),
        '3' to Color(0xFFF3C59D),
        'S' to Color(0xFF17202A),
        'D' to Color(0xFF2C3E50)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawNorg() {
    val matrix = arrayOf(
        "........KKKKKK..........",
        ".......KRRRRRRK.........",
        "......KRRRRRRRRK........",
        ".....KRRRWWWWRRRK.......",
        "....KRRRWERRE3RRK.......",
        "....KRRRWWWWWWRRK.......",
        "....KRRRWWWBWBRRK.......",
        ".....KRRRWWWWRRRK.......",
        "......KRRRRRRRRK........",
        ".......KRRRRRRK.........",
        "........KKKKKK.........."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'R' to Color(0xFFE74C3C),
        'W' to Color(0xFFF1C40F),
        'E' to Color(0xFF17202A),
        'B' to Color(0xFF17202A),
        '3' to Color(0xFFD35400)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawEdea() {
    val matrix = arrayOf(
        ".......KKSSSSKK.......",
        "......KSSSSSSSSK......",
        ".....KSSSSSSSSSSK.....",
        "....KSSSSWERRE3SK.....",
        "....KSSSSWWWWWWSSK....",
        "...KCCCCSSSSSSSSCCCCK.",
        "..KCCCCCCSSCCCCCCKKK..",
        ".KCCCCCCCCCCCCCCCCCK..",
        ".KCCCCCCCCCCCCCCCCCK..",
        ".KCCCCCCCCCCCCCCCCCK..",
        "..KCCCCCCCCCCCCCCCK...",
        "...KCCCCCCCCCCCCCK....",
        "....KCCCCCCCCCCCK.....",
        "....KCCCCK..KCCCCK....",
        "....KCCCK....KCCCK....",
        "....KDDK......KDDK....",
        "....KKKK......KKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'S' to Color(0xFF34495E),
        'W' to Color(0xFFECF0F1),
        'E' to Color(0xFF17202A),
        '3' to Color(0xFF8E44AD),
        'C' to Color(0xFF17202A),
        'D' to Color(0xFF2C3E50)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawFujinRaijin() {
    val matrix = arrayOf(
        ".......KKWWWWKK.......",
        "......KWWWWWWWWK......",
        ".....KWWWWWWWWWWK.....",
        "....KWWWWERRE3WWK.....",
        "....KWWWWWWWWWWWK.....",
        "...KBBBBWWWWWWBBBK....",
        "..KBBBBBBWWWWBBBBBK...",
        ".KBBBBBBBBBBBBBBBBBK..",
        ".KBBBBBBBBBBBBBBBBBK..",
        ".KBBBBBBBBBBBBBBBBBK..",
        "..KBBBBBBBBBBBBBBBK...",
        "...KBBBBBBBBBBBBBK....",
        "....KBBBBBBBBBBBK.....",
        "....KBBBBK.KBBBBK.....",
        "....KBBBK...KBBBK.....",
        "....KDDK.....KDDK.....",
        "....KKKK.....KKKK....."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'W' to Color(0xFFECF0F1),
        'E' to Color(0xFF17202A),
        '3' to Color(0xFFF3C59D),
        'R' to Color(0xFF17202A),
        'B' to Color(0xFF2C3E50),
        'D' to Color(0xFF1A252F)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawSeifer() {
    val matrix = arrayOf(
        ".......KKYYYYKK.......",
        "......KYYYYYYYYK......",
        ".....KYYYYWWWWYYK.....",
        "....KYYYYWWWWWWYYK....",
        "....KYYYYWERRE3YYK....",
        "....KYYYYWWWWWWYYK....",
        "...KYYYYYYYYYYYYYYK.KK",
        "..KYYYYYYYYYYYYYYYYKSK",
        ".KYYYYYYYYYYYYYYYYYKSK",
        ".KYYYYYYYYYYYYYYYYYKSK",
        ".KYYYYYYYYYYYYYYYYYKSK",
        "..KYYYYYYYYYYYYYYYYKSK",
        "...KYYYYYYYYYYYYYYK.KK",
        "....KYYYYYYYYYYYYK....",
        "....KYYYYK..KYYYYK....",
        "....KYYYK....KYYYK....",
        "....KDDK......KDDK....",
        "....KKKK......KKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'W' to Color(0xFFF3C59D),
        'R' to Color(0xFFC0392B),
        'E' to Color(0xFF17202A),
        '3' to Color(0xFFF3C59D),
        'D' to Color(0xFFB7950B),
        'S' to Color(0xFFBDC3C7)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawAdel() {
    val matrix = arrayOf(
        "......KKYYYYYYKK......",
        ".....KYYYYYYYYYYK.....",
        "....KYYYYYYYYYYYYK....",
        "....KYYYYWERRE3YYK....",
        "....KYYYYWWWWWWYYK....",
        "...KRRRRRRRRRRRRRRK...",
        "..KRRRRRRRRRRRRRRRRK..",
        ".KRRRRRRRRRRRRRRRRRRK.",
        ".KRRRRRRRRRRRRRRRRRRK.",
        ".KRRRRRRRRRRRRRRRRRRK.",
        "..KRRRRRRRRRRRRRRRRK..",
        "...KRRRRRRRRRRRRRRK...",
        "....KRRRRRRRRRRRRK....",
        "....KRRRRK..KRRRRK....",
        "....KRRRK....KRRRK....",
        "....KDDK......KDDK....",
        "....KKKK......KKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'W' to Color(0xFFF3C59D),
        'E' to Color(0xFF17202A),
        '3' to Color(0xFFF3C59D),
        'R' to Color(0xFFC0392B),
        'D' to Color(0xFF7B241C)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawTrauma() {
    val matrix = arrayOf(
        "........KKKKKK..........",
        ".......KCCCCCCK.........",
        "......KCCCCCCCCK........",
        ".....KCCCCWERRECK.......",
        "....KCCCCCCCCCCCCK......",
        "...KCCCCPCRRRCPCCCK.....",
        "..KCCCCPCRRRRRCPCCCK....",
        ".KCCCCCCCRRRRRCCCCCCK...",
        ".KCCCCCCCRRRRRCCCCCCK...",
        ".KCCCCCCCRRRRRCCCCCCK...",
        "..KCCCCPCRRRRCPCCCK.....",
        "...KCCCCPCRRCPCCCK......",
        "....KCCCCCCCCCCCCK......",
        "....KCCCC....CCCCK......",
        "....KCCK......KCCK......",
        "....KKKK......KKKK......"
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'C' to Color(0xFF7F8C8D),
        'P' to Color(0xFF8E44AD),
        'R' to Color(0xFFFF0000),
        'E' to Color(0xFFF1C40F)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawOmegaWeapon() {
    val matrix = arrayOf(
        "........KKKKKK..........",
        ".......KYYYYYYK.........",
        "......KYYYYYYYYK........",
        ".....KSSSSSSSSSSK.......",
        "....KSSSSEERSSSSSK......",
        "...KSSSSSSSSSSSSSSK.....",
        "..KSSSSSSSSSSSSSSSSK....",
        ".KSSSSSSSSSSSSSSSSSSK...",
        "KSSSSSSSSSSSSSSSSSSSSK..",
        "KSSSSSSSSSSSSSSSSSSSSK..",
        ".KSSSSSSSSSSSSSSSSSSK...",
        "..KSSSSSSSSSSSSSSSSK....",
        "..KRRK.KSSSSSSK.KRRK....",
        ".KRRK..KSSSSSSK..KRRK...",
        "KRRK...KSSSSSSK...KRRK..",
        "KKK....KSSK.KSSK...KKK.."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'S' to Color(0xFF2C3E50),
        'Y' to Color(0xFFF1C40F),
        'E' to Color(0xFFFF0000),
        'R' to Color(0xFFE74C3C)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawUltimecia() {
    val matrix = arrayOf(
        ".KK..............KK...",
        "KYYK............KYYK..",
        ".KYYK..........KYYK...",
        "..KYYKKKKKKKKKKYYK....",
        "...KRRRRRRRRRRRRK.....",
        "...KRRRRWERRE3RRK.....",
        "...KRRRRWWWW3RRRK.....",
        "...KRRRRWWWW3RRRK.....",
        "..KRRRRRRRRRRRRRRK....",
        ".KRRRRRRRRRRRRRRRRK...",
        "KRRRRRRRRRRRRRRRRRRK..",
        ".KRRRRRRRRRRRRRRRRK...",
        "..KRRRRRRRRRRRRRRK....",
        "...KRRRRRRRRRRRRK.....",
        "...KRRRRK....KRRRRK...",
        "...KDDK.......KDDK....",
        "...KKKK.......KKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'R' to Color(0xFFC0392B),
        '3' to Color(0xFFE74C3C),
        'W' to Color(0xFFECF0F1),
        'E' to Color(0xFF17202A),
        'D' to Color(0xFF7B241C)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawPlantBrain() {
    val matrix = arrayOf(
        "......KKKKKK......",
        "....KGGGGGGGGK....",
        "...KGGGGGGGGGGK...",
        "..KGGGGWWWWGGGGK..",
        ".KGGGGWERRE3GGGGK.",
        ".KGGGGWWWWWWGGGGK.",
        "KGGGGGGWWWWGGGGGGK",
        "KGGGGGGGGGGGGGGGGK",
        ".KGGGGGGGGGGGGGGK.",
        "..KRRRRGGGGRRRRK..",
        ".KRRRRRRK.KRRRRRRK",
        "KRRRRRRRK.KRRRRRRK",
        ".KKKKKKK...KKKKKKK"
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'G' to Color(0xFF1E8449),
        'R' to Color(0xFFC0392B),
        'W' to Color(0xFFFFFFFF),
        'E' to Color(0xFFFF0000),
        '3' to Color(0xFFE74C3C)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawBlackWaltz() {
    val matrix = arrayOf(
        ".......KKYYYYKK.......",
        "......KYYYYYYYYK......",
        ".....KYYYYYYYYYYK.....",
        "....KYYYYYYYYYYYYK....",
        "....KBBBBERRE3BBBK....",
        "....KBBBBBBBBBBBBK....",
        "...KBBBBBBBBBBBBBBK.KK",
        "..KBBBBBBBBBBBBBBBBKSK",
        ".KBBBBBBBBBBBBBBBBBKSK",
        ".KBBBBBBBBBBBBBBBBBKSK",
        ".KBBBBBBBBBBBBBBBBBKSK",
        "..KBBBBBBBBBBBBBBBBKSK",
        "...KBBBBBBBBBBBBBBK.KK",
        "....KBBBBBBBBBBBBK....",
        "....KBBBBK..KBBBBK....",
        "....KBBBK....KBBBK....",
        "....KDDK......KDDK....",
        "....KKKK......KKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'B' to Color(0xFF1F618D),
        'E' to Color(0xFFF1C40F),
        'R' to Color(0xFFF1C40F),
        '3' to Color(0xFF17202A),
        'D' to Color(0xFF114B72),
        'S' to Color(0xFF17202A)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawZornThorn() {
    val matrix = arrayOf(
        "......KKYYYYKK......",
        ".....KYYYYYYYYK.....",
        "....KYYYYYYYYYYK....",
        "....KYYYYWERREYK....",
        "....KYYYYWWWWYYK....",
        "...KRRRRYYYYRRRRK...",
        "..KRRRRRRYYRRRRRRK..",
        ".KRRRRRRRRRRRRRRRRK.",
        ".KRRRRRRRRRRRRRRRRK.",
        ".KRRRRRRRRRRRRRRRRK.",
        "..KRRRRRRRRRRRRRRK..",
        "...KRRRRRRRRRRRRK...",
        "....KRRRRRRRRRRK....",
        "....KRRRRKRRRRK.....",
        "....KRRRK.KRRRK.....",
        "....KDDK...KDDK.....",
        "....KKKK...KKKK....."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFE74C3C),
        'R' to Color(0xFF2980B9),
        'W' to Color(0xFFFFFFFF),
        'E' to Color(0xFF17202A),
        'D' to Color(0xFF1F618D)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawRalvurahva() {
    val matrix = arrayOf(
        ".......KKKKKK.......",
        "......KSSSSSSK......",
        ".....KSSSSSSSSK.....",
        "....KSSSSEERSSSSK...",
        "....KSSSSSSSSSSSK...",
        "...KSSSSSSSSSSSSSK..",
        "..KSSSSSSSSSSSSSSSK.",
        ".KSSSSSSSSSSSSSSSSSK",
        ".KSSSSSSSSSSSSSSSSSK",
        ".KSSSSSSSSSSSSSSSSSK",
        "..KSSSSSSSSSSSSSSSK.",
        "...KSSSSSSSSSSSSSK..",
        "....KSSSSSSSSSSSK...",
        "....KSSSSK..KSSSSK..",
        "....KSSSK....KSSSK..",
        "....KDDK......KDDK..",
        "....KKKK......KKKK.."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'S' to Color(0xFF7F8C8D),
        'E' to Color(0xFFE74C3C),
        'R' to Color(0xFFFF5252),
        'D' to Color(0xFF34495E)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawMaliris() {
    val matrix = arrayOf(
        ".......KKKKK........",
        "......KRRRRR3K......",
        ".....KRRRRRRRRK.....",
        "....KRRRWRYW3RRK....",
        "....KRRRRRRRRRRK....",
        "....KRRRRRRRRRRK....",
        "...KSSRRRRRRRRSSK...",
        "..KSSSSRRRRRRSSSSK..",
        ".KSSSSSSRRRRSSSSSSK.",
        "KSSSSSSSRRRRSSSSSSSK",
        ".KSSSSSSRRRRSSSSSSK.",
        "..KSSSSSRRRRSSSSSK..",
        "...KRRRRRRRRRRRRK...",
        "....KRRRRRRRRRRK....",
        ".....KRRRRRRRRK.....",
        "......KRRRRRRK......",
        ".......KRRRRK.......",
        "......KRRRRRRK......",
        ".....KRRRRRRRRK.....",
        "....KKKKKKKKKKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'R' to Color(0xFFC0392B),
        '3' to Color(0xFFE74C3C),
        'W' to Color(0xFFECF0F1),
        'Y' to Color(0xFFF1C40F),
        'S' to Color(0xFFBDC3C7)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawKuja() {
    val matrix = arrayOf(
        ".......KKYYYYKK.......",
        "......KYYYYYYYYK......",
        ".....KYYYYWWWWYYK.....",
        "....KYYYYWWWWWWYYK....",
        "....KYYYYWERRE3YYK....",
        "....KYYYYWWWWWWYYK....",
        "...KPPPPYYYYYYYYPPPPK.",
        "..KPPPPPPYYSSSSSSSSSK.",
        ".KPPPPPPPPPPPPPPPPPPK.",
        ".KPPPPPPPPPPPPPPPPPPK.",
        ".KPPPPPPPPPPPPPPPPPPK.",
        "..KPPPPPPPPPPPPPPPPK..",
        "...KPPPPPPPPPPPPPPK...",
        "....KPPPPPPPPPPPPK....",
        "....KPPPPK..KPPPPK....",
        "....KPPPK....KPPPK....",
        "....KDDK......KDDK....",
        "....KKKK......KKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFECF0F1),
        'W' to Color(0xFFF3C59D),
        'E' to Color(0xFF17202A),
        'R' to Color(0xFF8E44AD),
        '3' to Color(0xFFF3C59D),
        'P' to Color(0xFF6C3483),
        'S' to Color(0xFFECF0F1),
        'D' to Color(0xFF4A235A)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawTranceKuja() {
    val matrix = arrayOf(
        ".......KKRRRRKK.......",
        "......KRRRRRRRRK......",
        ".....KRRRRWWWW3RK.....",
        "....KRRRRWWWWWW3RK....",
        "....KRRRRWERRE33RK....",
        "....KRRRRWWWWWW3RK....",
        "...KRRRRRRRRRRRRRRRK..",
        "..KRRRRRRRRRRRRRRRRRK.",
        ".KRRRRRRRRRRRRRRRRRRK.",
        ".KRRRRRRRRRRRRRRRRRRK.",
        ".KRRRRRRRRRRRRRRRRRRK.",
        "..KRRRRRRRRRRRRRRRRK..",
        "...KRRRRRRRRRRRRRRK...",
        "....KRRRRRRRRRRRRK....",
        "....KRRRRK..KRRRRK....",
        "....KRRRK....KRRRK....",
        "....KDDK......KDDK....",
        "....KKKK......KKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'R' to Color(0xFFE74C3C),
        '3' to Color(0xFFF1948A),
        'W' to Color(0xFFF3C59D),
        'E' to Color(0xFF17202A),
        'D' to Color(0xFFB03A2E)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawNecron() {
    val matrix = arrayOf(
        ".......KKKKKK.......",
        "......KBBBBBBK......",
        ".....KBBBBBBBBK.....",
        "....KBBBWWWWBBBK....",
        "....KBBBWERREBBK....",
        "....KBBBWWWWBBBK....",
        "....KBBBWBWBBBBK....",
        "....KBBBBWWBBBBK....",
        "...KBBBBBBBBBBBBK...",
        "..KBBBBBBBBBBBBBBK..",
        ".KBBBBBBBCCCCCCBBBK.",
        ".KBBBBBBCCCCCCCCBBK.",
        ".KBBBBBBCCCCCCCCBBK.",
        ".KBBBBBBBCCCCCCBBBK.",
        "..KBBBBBBBBBBBBBBK..",
        "...KKKKKKKKKKKKKK..."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'B' to Color(0xFF2980B9),
        'W' to Color(0xFFECF0F1),
        'E' to Color(0xFFF1C40F),
        'R' to Color(0xFFE74C3C),
        'C' to Color(0xFF5DADE2)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawKlikk() {
    val matrix = arrayOf(
        "KK....................KK",
        "KCCK................KCCK",
        ".KCCK..............KCCK.",
        "..KCCKKKKKKKKKKKKKKCCK..",
        "...KCCCCCCCCCCCCCCCCK...",
        "...KCCCCCWWWWCCCCCCYK...",
        "...KCCCCWERRECCCCCCYK...",
        "...KCCCCCWWWWCCCCCCYK...",
        "..KCCCCCCCCCCCCCCCCYK...",
        ".KCCCCCCCCCCCCCCCCCCCK..",
        "KCCCCCCCCCCCCCCCCCCCCCK.",
        "KCCCCCCCCCCCCCCCCCCCCCK.",
        ".KCCCCCCCCCCCCCCCCCCCK..",
        "..KCCCCCCCCCCCCCCCCYK...",
        "...KCCCCK......KCCCCK...",
        "...KCCCCK......KCCCCK...",
        "...KKKKKK......KKKKKK..."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'C' to Color(0xFF16A085),
        'Y' to Color(0xFFF1C40F),
        'W' to Color(0xFFFFFFFF),
        'R' to Color(0xFFE74C3C),
        'E' to Color(0xFF17202A)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawOblitzerator() {
    val matrix = arrayOf(
        ".......KKKKKK.......",
        "......KYYYYYYK......",
        ".....KYYYYYYYYK.....",
        "....KYYYYEERYYYK....",
        "....KYYYYYYYYYYK....",
        "...KSSYYYYYYYYSSK...",
        "..KSSSYYYYYYSSSSK...",
        ".KSSSSYYYYYYYYSSSK..",
        "KSSSSSRRRRRRSSSSSSK.",
        ".KSSSSRRRRRRSSSSSK..",
        "..KSSSSRRRRSSSSSK...",
        "...KSSSSRRRSSSSK....",
        "....KSSSSSSSSSSK....",
        ".....KYYYYYYYYK.....",
        ".....KYYYYKYYYK.....",
        "....KKKKKK.KKKKKK..."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'S' to Color(0xFFBDC3C7),
        'R' to Color(0xFF34495E),
        'E' to Color(0xFFFF0000)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawSeymour() {
    val matrix = arrayOf(
        "......KKCCCCKK......",
        ".....KCCCCCCCCK.....",
        "....KCCCCCCCCCCK....",
        "....KCCCCWWWWCCK....",
        "....KCCCCWERRECK....",
        "....KCCCCWWWWCCK....",
        "...KPPPPCCCCCCCCPK..",
        "..KPPPPPPCCCCCPPPPK.",
        ".KPPPPPPPPPPPPPPPPK.",
        ".KPPPPPPPPPPPPPPPPK.",
        ".KPPPPPPPPPPPPPPPPK.",
        "..KPPPPPPPPPPPPPPK..",
        "...KPPPPPPPPPPPPK...",
        "....KPPPPPPPPPPK....",
        "....KPPPPK..KPPPPK..",
        "....KPPPK....KPPPK..",
        "....KDDK......KDDK..",
        "....KKKK......KKKK.."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'C' to Color(0xFF3498DB),
        'W' to Color(0xFFF3C59D),
        'E' to Color(0xFF17202A),
        'R' to Color(0xFF8E44AD),
        'P' to Color(0xFF4A235A),
        'D' to Color(0xFF2C3E50)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawJecht() {
    val matrix = arrayOf(
        ".......KKRRRRKK.......",
        "......KRRRRRRRRK......",
        ".....KRRRRWWWW3RK.....",
        "....KRRRRWWWWWW3RK....",
        "....KRRRRWERRE33RK....",
        "....KRRRRWWWWWW3RK....",
        "...KWWWWWWWWWWWWWWK.KK",
        "..KWWWWWWWWWWWWWWWWKSK",
        ".KWWWWWWWWWWWWWWWWWKSK",
        ".KWWWWWWWWWWWWWWWWWKSK",
        ".KWWWWWWWWWWWWWWWWWKSK",
        "..KWWWWWWWWWWWWWWWWKSK",
        "...KWWWWWWWWWWWWWWK.KK",
        "....KWWWWWWWWWWWWK....",
        "....KWWWWK..KWWWWK....",
        "....KWWWK....KWWWK....",
        "....KDDK......KDDK....",
        "....KKKK......KKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'R' to Color(0xFFC0392B),
        'W' to Color(0xFFF3C59D),
        'E' to Color(0xFF17202A),
        '3' to Color(0xFFF3C59D),
        'D' to Color(0xFF7B241C),
        'S' to Color(0xFFE74C3C)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawPenance() {
    val matrix = arrayOf(
        ".......KKYYYYKK.......",
        "......KYYYYYYYYK......",
        ".....KYYYYYYYYYYK.....",
        "....KYYYYWERREYYK.....",
        "....KYYYYWWWWYYYK.....",
        "...KSSSSYYYYSSSSK.....",
        "..KSSSSSSYYSSSSSSK....",
        ".KSSSSSSSSSSSSSSSSK...",
        ".KSSSSSSSSSSSSSSSSK...",
        ".KSSSSSSSSSSSSSSSSK...",
        "..KSSSSSSSSSSSSSSK....",
        "...KSSSSSSSSSSSSK.....",
        "....KSSSSSSSSSSK......",
        "....KSSSSKSSSSK.......",
        "....KSSSK.KSSSK.......",
        "....KDDK...KDDK.......",
        "....KKKK...KKKK......."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'Y' to Color(0xFFF1C40F),
        'S' to Color(0xFFBDC3C7),
        'W' to Color(0xFFFFFFFF),
        'R' to Color(0xFFE74C3C),
        'E' to Color(0xFF17202A),
        'D' to Color(0xFFB7950B)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawYuYevon() {
    val matrix = arrayOf(
        "........KKKKKK..........",
        ".......KGGGGGGK.........",
        "......KGGGGGGGGK........",
        ".....KGGYYYYYYGGK.......",
        "....KGGYWEERREYYGK......",
        "....KGGYWEERREYYGK......",
        "....KGGYWEERREYYGK......",
        ".....KGGYYYYYYGGK.......",
        "......KGGGGGGGGK........",
        ".......KGGGGGGK.........",
        "........KKKKKK.........."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'G' to Color(0xFF27AE60),
        'Y' to Color(0xFFF1C40F),
        'W' to Color(0xFFFFFFFF),
        'R' to Color(0xFFE74C3C),
        'E' to Color(0xFF17202A)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawSin() {
    val matrix = arrayOf(
        ".........KKKKKK.........",
        "........KCCCCCCCK.......",
        ".......KCCCCCCCCCK......",
        "......KCCCCCCCCCCCK.....",
        ".....KCCCCCCCCCCCCCK....",
        "....KCCCWERRECCCCCCCK...",
        "...KCCCCCCCCCCCCCCCCCK..",
        "..KCCCCCCCCCCCCCCCCCCCK.",
        ".KCCCCCCCCCCCCCCCCCCCCCK",
        "KCCCCCCCCCCCCCCCCCCCCCCK",
        ".KCCCCCCCCCCCCCCCCCCCCCK",
        "..KCCCCCCCCCCCCCCCCCCCK.",
        "...KCCCCCCCCCCCCCCCCCK..",
        "....KCCCCCCCCCCCCCCCK...",
        ".....KKKKKKKKKKKKKKK...."
    )
    val palette = mapOf(
        'K' to Color(0xFF17202A),
        'C' to Color(0xFF34495E),
        'R' to Color(0xFFFF0000),
        'E' to Color(0xFFE74C3C)
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
        // ── FF1-FF10 Bosses ──
        enemyName.contains("Garland", true) -> drawGarland()
        enemyName.contains("Astos", true) -> drawAstos()
        enemyName.contains("Lich", true) -> drawLich()
        enemyName.contains("Marilith", true) -> drawMarilith()
        enemyName.contains("Kraken", true) -> drawKraken()
        enemyName.contains("Tiamat", true) -> drawTiamat()
        enemyName.contains("Chaos", true) -> drawChaos()

        enemyName.contains("Leon", true) -> drawLeon()
        enemyName.contains("Borghen", true) -> drawBorghen()
        enemyName.contains("Gottos", true) -> drawGottos()
        enemyName.contains("Roundworm", true) -> drawRoundworm()
        enemyName.contains("Cyclone", true) -> drawCyclone()
        enemyName.contains("Emperor", true) -> drawEmperor()

        enemyName.contains("Djinn", true) -> drawDjinn()
        enemyName.contains("Nepto Dragon", true) || enemyName.contains("Nepto", true) -> drawNeptoDragon()
        enemyName.contains("Hein", true) -> drawHein()
        enemyName.contains("Garuda", true) -> drawGaruda()
        enemyName.contains("Goldor", true) -> drawGoldor()
        enemyName.contains("Xande", true) -> drawXande()
        enemyName.contains("Cloud of Darkness", true) -> drawCloudOfDarkness()

        enemyName.contains("Mist Dragon", true) -> drawMistDragon()
        enemyName.contains("Antlion", true) -> drawAntlionBoss()
        enemyName.contains("Golbez", true) -> drawGolbez()
        enemyName.contains("Cagnazzo", true) -> drawCagnazzo()
        enemyName.contains("Barbariccia", true) -> drawBarbariccia()
        enemyName.contains("Scarmiglione", true) -> drawScarmiglione()
        enemyName.contains("Rubicante", true) -> drawRubicante()
        enemyName.contains("Dark Bahamut", true) -> drawDarkBahamut()
        enemyName.contains("Zeromus", true) -> drawZeromus()

        enemyName.contains("Wing Raptor", true) -> drawWingRaptor()
        enemyName.contains("Karlabos", true) -> drawKarlabos()
        enemyName.contains("Ifrit", true) -> drawIfrit()
        enemyName.contains("Gilgamesh", true) -> drawGilgamesh()
        enemyName.contains("Atomos", true) -> drawAtomos()
        enemyName.contains("Exdeath", true) -> drawExdeath()
        enemyName.contains("Neo Exdeath", true) -> drawNeoExdeath()
        enemyName.contains("Omega Weapon", true) -> drawOmegaWeapon()
        enemyName.contains("Omega", true) -> drawOmega()

        enemyName.contains("Whelk", true) -> drawWhelk()
        enemyName.contains("Vargas", true) -> drawVargas()
        enemyName.contains("Number 024", true) || enemyName.contains("Number 128", true) || enemyName.contains("Number", true) -> drawNumber024()
        enemyName.contains("Ultros", true) -> drawUltros()
        enemyName.contains("Typhon", true) -> drawTyphon()
        enemyName.contains("Air Force", true) -> drawAirForce()
        enemyName.contains("Guardian", true) -> drawGuardian()
        enemyName.contains("Ultima Weapon", true) -> drawUltimaWeapon()
        enemyName.contains("Kefka", true) -> drawKefka()

        enemyName.contains("Guard Scorpion", true) -> drawGuardScorpion()
        enemyName.contains("Airbuster", true) -> drawAirbuster()
        enemyName.contains("Rufus", true) -> drawRufus()
        enemyName.contains("Hojo", true) -> drawHojo()
        enemyName.contains("Bizarro Sephiroth", true) || enemyName.contains("Bizarro Seph", true) -> drawBizarroSephiroth()
        enemyName.contains("Sephiroth", true) -> drawSephiroth()
        enemyName.contains("Jenova", true) -> drawJenova()

        enemyName.contains("NORG", true) -> drawNorg()
        enemyName.contains("Edea", true) -> drawEdea()
        enemyName.contains("Fujin", true) || enemyName.contains("Raijin", true) -> drawFujinRaijin()
        enemyName.contains("Seifer", true) -> drawSeifer()
        enemyName.contains("Adel", true) -> drawAdel()
        enemyName.contains("Trauma", true) -> drawTrauma()
        enemyName.contains("Ultimecia", true) -> drawUltimecia()

        enemyName.contains("Plant Brain", true) -> drawPlantBrain()
        enemyName.contains("Black Waltz", true) -> drawBlackWaltz()
        enemyName.contains("Zorn", true) || enemyName.contains("Thorn", true) -> drawZornThorn()
        enemyName.contains("Ralvurahva", true) -> drawRalvurahva()
        enemyName.contains("Maliris", true) -> drawMaliris()
        enemyName.contains("Trance Kuja", true) -> drawTranceKuja()
        enemyName.contains("Kuja", true) -> drawKuja()
        enemyName.contains("Necron", true) -> drawNecron()

        enemyName.contains("Klikk", true) -> drawKlikk()
        enemyName.contains("Oblitzerator", true) -> drawOblitzerator()
        enemyName.contains("Evrae", true) -> drawEvrae()
        enemyName.contains("Seymour", true) -> drawSeymour()
        enemyName.contains("Jecht", true) -> drawJecht()
        enemyName.contains("Penance", true) -> drawPenance()
        enemyName.contains("Yu Yevon", true) -> drawYuYevon()
        enemyName.contains("Sin", true) -> drawSin()

        // ── Regular Monsters ──
        enemyName.contains("Slime", true) || enemyName.contains("Flan", true) -> drawSlime()
        enemyName.contains("Goblin", true) -> drawGoblin()
        enemyName.contains("Orc", true) || enemyName.contains("Ogre", true) -> drawOgre()
        enemyName.contains("Dragon", true) -> drawDragon(Color.Red)
        enemyName.contains("Rat", true) -> drawWildRat()
        enemyName.contains("Wolf", true) || enemyName.contains("Dingo", true) -> drawWolf()
        enemyName.contains("Sahagin", true) || enemyName.contains("Merman", true) || enemyName.contains("Sea Snake", true) -> drawSahagin()
        enemyName.contains("Pirate", true) -> drawPirate()
        enemyName.contains("Cockatrice", true) -> drawCockatrice()
        enemyName.contains("Bomb", true) || enemyName.contains("Stoker", true) -> drawBomb()
        enemyName.contains("Eye", true) || enemyName.contains("Malboro", true) -> drawEye()
        enemyName.contains("Tonberry", true) -> drawTonberry()
        enemyName.contains("Mindflayer", true) || enemyName.contains("Dark Force", true) -> drawLich()
        enemyName.contains("Dark Knight", true) || enemyName.contains("Black Knight", true) || enemyName.contains("Sergeant", true) || enemyName.contains("Captain", true) || enemyName.contains("Soldier", true) || enemyName.contains("Grunt", true) || enemyName.contains("Sweeper", true) -> drawDarkKnight()
        enemyName.contains("Lamia", true) || enemyName.contains("Medusa", true) -> drawLamia()
        enemyName.contains("Adamantoise", true) -> drawAdamantoise()
        enemyName.contains("Zombie", true) -> drawZombie()
        enemyName.contains("Wyvern", true) || enemyName.contains("Elnoyle", true) -> drawWyvern()
        enemyName.contains("Behemoth", true) || enemyName.contains("Giant", true) || enemyName.contains("Brawler", true) || enemyName.contains("Wendigo", true) || enemyName.contains("Zaghnol", true) || enemyName.contains("Mistodon", true) -> drawBehemoth()
        enemyName.contains("Toad", true) -> drawToad()
        enemyName.contains("Hellhound", true) || enemyName.contains("Geezard", true) -> drawHellhound()

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

