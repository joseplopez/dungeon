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
        "......................", // 0
        "....KKKKKKKKK..K.K....", // 1
        "...KRRRRRRRRRKKRKRK...", // 2
        "..KKKKRRRRRRRRRRRKRKK.", // 3
        ".KRRRRRRRRRRRRRRRRRKK.", // 4
        "..KKRRRRRRRRRRRRRRK...", // 5
        "..KRRRRRRRRRRRRRRRRKK.", // 6
        ".KRRRRRRRRRKKKRPRKRKK.", // 7
        "..KKRRRRRRRPKKKKRKRKK.", // 8
        "..KRRRPPPRRPWKKWK.K...", // 9
        "...KRRPPPRPWWKKWK.....", // 10
        "...KKKRRRPWWWWWWKK....", // 11
        "..KRWRRRKKWWWWWWRWK...", // 12
        ".KRRRRRRRKKWWWPKRRRKR.", // 13
        ".KRRRRRRRKRKKKKKRRKPP.", // 14
        "..KKPPPRKRRRKKKRRRPKP.", // 15
        "..KRPRRRRPRRKKKRKPWKK.", // 16
        "..KPPRRRWWRRKKKRKPPKK.", // 17
        "..KPRRRPWWWRKKKKRRRRRR", // 18
        "..KRRRRRPPPRRRRKKPWWRK", // 19
        "...KKKKKKRRRRRRKKPPPRK", // 20
        "........KRRRRKK..KKKR.", // 21
        "......KKPWWWWKK.......", // 22
        "......KKRRRRRKK.......", // 23
        "......KKRRRRRKK.......", // 24
        "......KKRRRRRKK.......", // 25
        "......KKRRRRRRRK......", // 26
        "........KKKKKKK......." // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline / details
        'W' to Color(0xFFFFFFFF), // White plume, collar, socks
        'R' to Color(0xFFE74C3C), // Crimson red ribbon / dress
        'P' to Color(0xFFF3C59D)  // Peach skin tone
    )


    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawThief() {
    val matrix = arrayOf(
        "......................", // 0
        "......................", // 1
        ".........iTTTTT..TT...", // 2
        "........DDSblbSTS1PN..", // 3
        ".......DtSllllbaPPaa..", // 4
        ".......tSSblbKA1PPQD..", // 5
        "......KttSlaOPaNaPPa..", // 6
        "......KtSbD1QaHPaNP1..", // 7
        ".......tStKaaTKCaTaa..", // 8
        ".....KbKtKQaHWNbb.ND..", // 9
        "...DStSKKKDaHQaeb.....", // 10
        "..DbtSKttabeOFFQNDS...", // 11
        ".TbStKCOONtba1EAtlS...", // 12
        ".SDKia2E2HAtLAADeea...", // 13
        "....SbbaDaNttbQLDaaD..", // 14
        "....aFeSPPDtbSbtNaa...", // 15
        "....aPSHQFbtSbSKCP1D..", // 16
        ".....at1QEC1aQaNaaED..", // 17
        "......aDOODSlttKaHPS..", // 18
        "........KDSlltSDbDS...", // 19
        "........DDtttNKN......", // 20
        "........aOPQKaK.......", // 21
        ".......Dalla1KK.......", // 22
        "........AaOOKC........", // 23
        ".......DCOHHCaK.......", // 24
        ".......DaC1llKK.......", // 25
        "......................" // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF181818),
        'D' to Color(0xFF373737),
        'S' to Color(0xFF646464),
        'T' to Color(0xFFA0A09B),
        'W' to Color(0xFFF7F7F7),
        'N' to Color(0xFF371912),
        'A' to Color(0xFF5F2314),
        'C' to Color(0xFF8C3719),
        'O' to Color(0xFFB45523),
        'H' to Color(0xFFD27337),
        'J' to Color(0xFFE78C39),
        'P' to Color(0xFFCD8C55),
        'Q' to Color(0xFFE6AA6E),
        'E' to Color(0xFFFEC784),
        'F' to Color(0xFFFFDCA5),
        'B' to Color(0xFF283782),
        'U' to Color(0xFF3750AF),
        'V' to Color(0xFF648CDC),
        'M' to Color(0xFF462891),
        'I' to Color(0xFF735A9C),
        'm' to Color(0xFF916EC3),
        'i' to Color(0xFFB59CDE),
        'v' to Color(0xFFE7D6FF),
        'R' to Color(0xFF8C210F),
        'Z' to Color(0xFFD21914),
        'X' to Color(0xFFF03C37),
        'Y' to Color(0xFFFA6964),
        'r' to Color(0xFF962350),
        'p' to Color(0xFFDC3C6E),
        'q' to Color(0xFFFA789B),
        '1' to Color(0xFFAA8C2D),
        '2' to Color(0xFFCDAF46),
        '3' to Color(0xFFF6F000),
        '4' to Color(0xFFFFF564),
        'G' to Color(0xFF144619),
        'g' to Color(0xFF188B08),
        'L' to Color(0xFF4BAA28),
        'l' to Color(0xFF8CD241),
        'a' to Color(0xFF5F5A37),
        'b' to Color(0xFF8C855B),
        'e' to Color(0xFFBEB478),
        't' to Color(0xFF145A5A),
        'c' to Color(0xFF23918C),
        'd' to Color(0xFF64C8BE),
        '0' to Color(0xFFFFFFFF)
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawMonk() {
    val matrix = arrayOf(
        "......................", // 0
        "......................", // 1
        "........KKKKKK........", // 2
        ".......KS1111SKKD.....", // 3
        "......K111111111SK....", // 4
        ".....KS1111111S111K...", // 5
        ".....K11111S11CCS1K...", // 6
        ".....K1111111SPOC1K...", // 7
        ".....K1VVVT1SVVBKSK...", // 8
        ".....K111111WKKKKK....", // 9
        ".....KS11111WCEDD.....", // 10
        ".....KC1111PEEEDD.....", // 11
        ".....KKS11PQEEESD.....", // 12
        "....STUV111PQESKD.....", // 13
        "...S1QTVTT1JSTBBBT....", // 14
        "..KQQEEVVVT1KKVVVK....", // 15
        ".KJEEEEVVVVVPVVVVPK...", // 16
        ".KPESCCKKVVVVVVVVCCK..", // 17
        ".KJCCSPEEKUVVVVSCJEEK.", // 18
        "..KCCSPEEKCJJJKKKJEEK.", // 19
        "...KKKJPPKCJVK...KPPK.", // 20
        "......KKKV2JVK....KK..", // 21
        "......KVVVVVK.........", // 22
        "......KVVVVVK.........", // 23
        "......KVVVBK..........", // 24
        ".....KJJJJ1JK.........", // 25
        ".....KKKKKKKK.........", // 26
        "......................" // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF231E19),
        'D' to Color(0xFF413C32),
        'S' to Color(0xFF645F55),
        'T' to Color(0xFF918C7D),
        'W' to Color(0xFFF5F5F0),
        'N' to Color(0xFF371912),
        'A' to Color(0xFF552314),
        'C' to Color(0xFF783219),
        'O' to Color(0xFF9B411E),
        'H' to Color(0xFFB95528),
        'J' to Color(0xFFD26E37),
        'P' to Color(0xFFD2A56E),
        'Q' to Color(0xFFE1B987),
        'E' to Color(0xFFF0CD9B),
        'F' to Color(0xFFFADCAF),
        'B' to Color(0xFF2D4682),
        'U' to Color(0xFF3C5A9B),
        'V' to Color(0xFF5573B4),
        'M' to Color(0xFF5F417D),
        'I' to Color(0xFF7D5596),
        'R' to Color(0xFFBE3723),
        'Z' to Color(0xFFE14B32),
        'X' to Color(0xFFF06946),
        'Y' to Color(0xFFFA8C55),
        '1' to Color(0xFFB49655),
        '2' to Color(0xFFCDB478),
        '3' to Color(0xFFE6CD96),
        '4' to Color(0xFFF5E1AF),
        '0' to Color(0xFF2D4187)
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawWhiteMage() {
    val matrix = arrayOf(
        "......................", // 2
        "......................", // 2
        "....KKKKKKKKKK........", // 4
        "...KKPPPPPPPPKKK......", // 5
        "...KPPWPWWWWWPPKK.....", // 6
        "...KKPPWWWWWWWPPKK....", // 7
        "...KKKWWWWWWWKKKPKK...", // 8
        "...KKPWWWWWWKKKKKPK...", // 9
        "...KPWWWWWPKKKRKKPK...", // 10
        "...KPWWWWPPKKKPKKPK...", // 11
        "...KPWWWWPKRPKPKPKK...", // 12
        "...KPPWWWPKPPKPKKK....", // 13
        "...KKPPWWWWKPPPKK.....", // 14
        "....KKPWWWWWKKKPK.....", // 15
        "...KKPWWPPPPWWWPK.....", // 16
        "...KKWWWPPPPPWWPKKR...", // 17
        "...KPWWWPRRRKWWKPPK...", // 18
        "...KPWWWWPRRKWWKPRK...", // 19
        "...KPWWWWWWRKWWKPRK...", // 20
        "...KPWWRRRRRKWWKRKK...", // 21
        "...KPWWWPRRKPWWKRKK...", // 22
        "..KKPWRPPPRKPWPKKK....", // 23
        "..KKPPRRRKKKRPRKKK....", // 24
        "..KKKRRKKKKKRRKKK.....", // 25
        "..KKKKKKKKKKKKKKK.....", // 26
        "......................" // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline / details
        'W' to Color(0xFFFFFFFF), // White plume, collar, socks
        'R' to Color(0xFFE74C3C), // Crimson red ribbon / dress
        'P' to Color(0xFFF3C59D)  // Peach skin tone
    )


    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawBlackMage() {
    val matrix = arrayOf(
        "......................", // 0
        "......................", // 1
        "..KKK.................", // 2
        ".KLLLKK...............", // 3
        ".KDOLLLK..............", // 4
        "..KKOLLLKKD...........", // 5
        "...KOOLLLLNKK.........", // 6
        "....KOOOLLLLLKKKKKKK..", // 7
        "....KOOOOOLLLLLLLLLLK.", // 8
        "....KNOOOLLLLLONNNNK..", // 9
        ".....DOLLLLONNNKKKD...", // 10
        "....DOOONNNNKK........", // 11
        "...NOONNKKKNKKLK......", // 12
        ".NNOKCKKKKDLKKDKBDD...", // 13
        ".KKKBDKKKKKDKKKKDBBD..", // 14
        "...KCCCCCCCCKKKCCCBK..", // 15
        "....KCCKKKDCCCCCCKKK..", // 16
        "...KCCCCCCBKKCCCKLLLK.", // 17
        "...KCCCCCCBKKKCCKLLLK.", // 18
        "...KCCCCCCBKLLCCCKKK..", // 19
        "...KCCCCCCBKLLCCKCBK..", // 20
        "....KCCCCCBKKKCCKCBK..", // 21
        "....KCCCCKDCCCCKCCBK..", // 22
        "....KCCCCKDCCCKCCCBK..", // 23
        "...KCKCCKCCCKKCCCCBK..", // 24
        ".KDCCCKKCCCCCCCCCCDSK.", // 25
        "...KKK..KKKKKKKKKKKKD.", // 26
        "......................" // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF000000),
        'W' to Color(0xFFFFFFFF),
        'R' to Color(0xFFFF0000),
        'G' to Color(0xFF00B400),
        'B' to Color(0xFF0064C8),
        'Y' to Color(0xFFFFFF00),
        'C' to Color(0xFF00C8C8),
        'M' to Color(0xFFDC00DC),
        'O' to Color(0xFFFF8C00),
        'P' to Color(0xFFF5C8A0),
        'N' to Color(0xFF82460A),
        'L' to Color(0xFFDCB478),
        'S' to Color(0xFFC8C8C8),
        'D' to Color(0xFF464646),
        'V' to Color(0xFF823CB4)
    )



    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawRedMage() {
    val matrix = arrayOf(

        "......................", // 4
        ".....KKK..KKKKK.......", // 5
        ".....KWWKKXXXPAKK.....", // 6
        "......KWWTPXXPPPK.....", // 7
        "..KPPNNCFWEPPPXPK.....", // 8
        "...KXPPXPPTQXPPPNK....", // 9
        "...KNPPPXPPPXXXPPDK...", // 10
        ".....KNWPPPPPPPPPPDKK.", // 11
        ".....KDWWWWSNAPPPPPPK.", // 12
        "....KNWWWWWWTTDDDDDKK.", // 13
        "...KSWWWTWWWFFDWDNK...", // 14
        "...KWSWQPEWTEEETNPAK..", // 15
        "...KPPWPPXFQXXPAAAAAK.", // 16
        "..KAPPPPXPPANNDDDDDDK.", // 17
        ".KDAPPPPPNFFSAPPDTTFK.", // 18
        ".KPXXXPPNWFFSAPPNFFFK.", // 19
        ".KPPXPPNPFFFSAPPDTFTK.", // 20
        ".KPPPPNPPNNNNAPDDDPNK.", // 21
        ".KPPADDNPPPPPPPDWDPK..", // 22
        ".KPCADTNTTEETSDDTDPK..", // 23
        ".KPPCCDNPPPXXANAACPK..", // 24
        ".KPANKKNPPXPPPANKKKK..", // 25
        ".KKKK..KKKKKKKKK......", // 26
        "......................" // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawPixelMatrix(matrix, palette)
}

// --- ADVANCED JOBS (TIER 2) ---

fun DrawScope.drawKnight() {
    val matrix = arrayOf(
        "......................", // 2
        ".........DDD..........", // 3
        "........S0SQD.........", // 4
        "........SDSQD.........", // 5
        ".......DK0PKN.........", // 6
        "......DDQ0SNKD........", // 7
        ".....D0TSDNDPPD.......", // 8
        ".....CPSDSDSSD0AAD....", // 9
        ".....DDAKSSSSKNDDC....", // 10
        ".....DDDD0DD0DKDDC....", // 11
        ".....0DKKKSSDKDDDP....", // 12
        ".....0SKKDDPDKNDDA....", // 13
        ".....KAQKDDDDNDDDA....", // 14
        "....ANSQDSSSSSSKDD....", // 15
        ".....AKPDDSSDSSTD.....", // 16
        ".....rNDDKKDDDDNC.....", // 17
        "....DrRKSSNN0SRD......", // 18
        "....RRr0DKRDKKR.......", // 19
        "....NRD0SNNDSDN.......", // 20
        ".....S.DSS.KDD........", // 21
        ".......DDD.DDD........", // 22
        "......................", // 23
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )
    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawPaladin() {
    val matrix = arrayOf(
        ".......KKIIiKIDS......", // 0
        ".....SSSImDmIiS1S.....", // 1
        ".....KDIimKIivI1K.....", // 2
        "....KIvviKbIIibbK.....", // 3
        ".....DmvIK3KKK3ZK.....", // 4
        ".....iSiKbW3b3vZIK....", // 5
        ".....DmIK3KKiKNaiK....", // 6
        ".....iSiIKIiKKHKIK....", // 7
        "......SIIIKKWgHTK.....", // 8
        "....SSDDDDSOWgPTT.....", // 9
        "....KKDKKKIJWgQT......", // 10
        "....KiWbKFKJEEQKK.....", // 11
        ".....DFWFKWKJENeK.....", // 12
        ".....KMiFbFbKKbMK.....", // 13
        "....KbaKKKKMVMFKJK....", // 14
        "....KeIMKEEKFFbOEK....", // 15
        "....KeMiMJEKbbKOJK....", // 16
        "....KiKDPJJKIDKADi....", // 17
        "....KiKKJJJKMKNNK.....", // 18
        "....KiKKKKKMKMNaK.....", // 19
        "....KiKMbFbMKbNaK.....", // 20
        "....KiAKbKKKbKReK.....", // 21
        "....KeAKKFFKFKReK.....", // 22
        "....KeAKFWbKZRNeK.....", // 23
        "....KeKbFZRKbFbeK.....", // 24
        "....KeKbYOeaDaDDT.....", // 25
        "....DeKbZbFbDDDD......", // 26
        ".....DSKKKKK.........." // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF181818),
        'D' to Color(0xFF373737),
        'S' to Color(0xFF646464),
        'T' to Color(0xFFA0A09B),
        'W' to Color(0xFFF7F7F7),
        'N' to Color(0xFF371912),
        'A' to Color(0xFF5F2314),
        'C' to Color(0xFF8C3719),
        'O' to Color(0xFFB45523),
        'H' to Color(0xFFD27337),
        'J' to Color(0xFFE78C39),
        'P' to Color(0xFFCD8C55),
        'Q' to Color(0xFFE6AA6E),
        'E' to Color(0xFFFEC784),
        'F' to Color(0xFFFFDCA5),
        'B' to Color(0xFF283782),
        'U' to Color(0xFF3750AF),
        'V' to Color(0xFF648CDC),
        'M' to Color(0xFF462891),
        'I' to Color(0xFF735A9C),
        'm' to Color(0xFF916EC3),
        'i' to Color(0xFFB59CDE),
        'v' to Color(0xFFE7D6FF),
        'R' to Color(0xFF8C210F),
        'Z' to Color(0xFFD21914),
        'X' to Color(0xFFF03C37),
        'Y' to Color(0xFFFA6964),
        'r' to Color(0xFF962350),
        'p' to Color(0xFFDC3C6E),
        'q' to Color(0xFFFA789B),
        '1' to Color(0xFFAA8C2D),
        '2' to Color(0xFFCDAF46),
        '3' to Color(0xFFF6F000),
        '4' to Color(0xFFFFF564),
        'G' to Color(0xFF144619),
        'g' to Color(0xFF188B08),
        'L' to Color(0xFF4BAA28),
        'l' to Color(0xFF8CD241),
        'a' to Color(0xFF5F5A37),
        'b' to Color(0xFF8C855B),
        'e' to Color(0xFFBEB478),
        't' to Color(0xFF145A5A),
        'c' to Color(0xFF23918C),
        'd' to Color(0xFF64C8BE),
        '0' to Color(0xFFFFFFFF)
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawNinja() {
    val matrix = arrayOf(
        "......................", // 0
        "......................", // 1
        "...K....KKKKKKKK......", // 2
        "...KKK.KNNNDDDNNK.....", // 3
        "...KKNKNNDDDVDDDDK....", // 4
        "....DKDKDDNDDND11K....", // 5
        "......K11NK1N1ADDDK...", // 6
        "....DKKDNKKK11AND1K...", // 7
        "...KDDKNNNNKKKN11K....", // 8
        "..KDDDNKNDDKKIDKK.....", // 9
        "..KVDDKKKKDDNKKKK.....", // 10
        "..KPNKNNNKKDDVIDK.....", // 11
        "...KKNDDDDNKDDDKNK....", // 12
        "..KKDDVDKKDDKKKDKDKD..", // 13
        "..KNDDVDKKKDDKDDKKDDS.", // 14
        "..KNDD0DDDDDDNDIKKIDK.", // 15
        "...KDDKDVVVKDNDIKDI0K.", // 16
        "...KKKKDVVVKNNNKKDDDK.", // 17
        ".....KKDDDDK11AKKKKD..", // 18
        ".....KNKKKKNKKKK......", // 19
        "...KKDDNKKDVKKD.......", // 20
        "..KNDDDKN0VKDKD.......", // 21
        "..K1111KNKKKK.........", // 22
        "...KKKKK111KK.........", // 23
        ".....KDDDDDKVK........", // 24
        ".....KVKVVVVKK........", // 25
        ".....KKKKKKKK.........", // 26
        "......................" // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawDragoon() {
    val matrix = arrayOf(
        "......................", // 0
        "......................", // 1
        ".....K................", // 2
        "....KmKKK.KKKK........", // 3
        ".....KmmIB0IIKKK......", // 4
        "......KIB0IIImmDK.....", // 5
        ".....KmIIB0IImWiDK....", // 6
        "......Km0B0IIImmVBK...", // 7
        "......KIB0IIQ4SIIBK...", // 8
        ".....KKB0BB0IQ4SIBK...", // 9
        "....KIKK000B0ISIIBKK..", // 10
        "....KIImK0PPKKKKBBDIK.", // 11
        "....KKIVKKN1AAANKBBSK.", // 12
        "......DK0KBNPQNDBBSK..", // 13
        ".....KBDBKIBAAKDBKDK..", // 14
        ".....K00KK0IDKD0KKNDK.", // 15
        "....KD00KNNMV0IIBDQPK.", // 16
        "....KImIKQQK0B0KDSQPK.", // 17
        "....KZZBIQQKBBKKBD1AK.", // 18
        ".....K0B0P1KB0mDKKKK..", // 19
        "......KKKKKZIK0BK.....", // 20
        "......K00K0ZIKKK......", // 21
        ".......K00BBBVKK......", // 22
        "........K0ImVBKK......", // 23
        "........K0Im0PNK......", // 24
        "........KCI0Q32NK.....", // 25
        "........KKKKKKKK......", // 26
        "......................" // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawBard() {
    val matrix = arrayOf(
        "......................", // 0
        "......................", // 1
        "...K..................", // 2
        "..TDTKSSSDKKKKK.......", // 3
        "..DTTTSSDDSSSSSK......", // 4
        "..TTSTDDSSSSSSSST.....", // 5
        "...TKKSSDDDDSSKT......", // 6
        "....SSSDDPPQDK........", // 7
        "....KSDPPPPPQT........", // 8
        "....KQPPQQPPPT........", // 9
        ".....KQPEQQPP.........", // 10
        ".....KEPEEEEK....TKKK.", // 11
        "......KEEEEEK..KKAAKK.", // 12
        ".......KEEEEK.KKPPKT..", // 13
        "......NNTQETSKKDQN....", // 14
        ".....KrNDTQDNKPESK....", // 15
        ".....KrNNSQEPPAASK....", // 16
        "....KrrSSASTNPAQDS....", // 17
        "....KrrTTQSKKAAKT.....", // 18
        "....RrRTTQPKPAK.......", // 19
        "...KRrSNNAPPAK........", // 20
        "...KrATSNNAAAT........", // 21
        "...KrANTDNNAT.........", // 22
        "....QPNPPNPPK.........", // 23
        "....KKKNNKNNK.........", // 24
        ".....KKNNDNNK.........", // 25
        "......KNKKKNNK........", // 26
        ".......KKKKKK........." // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF0F5019),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawSummoner() {
    val matrix = arrayOf(
        "......................", // 0
        ".........KKK..........", // 1
        "......KKKgggTK........", // 2
        ".....KSgLlllggKK......", // 3
        ".....KLl2233llLTKK....", // 4
        "....Kgl2P33332lLgSK...", // 5
        "....KL2PqQQ333lllLK...", // 6
        "....Kl2Ppq3E233lLSK...", // 7
        "...KLlll23E3Qll3lLDK..", // 8
        "...KlLlll23KKQlll2lD..", // 9
        "...KLLLT4lKW0ElLGKD...", // 10
        "....DLGS32FWU4llgS....", // 11
        ".....DgGD4EF44LLlLK...", // 12
        "......SKKKE44KLGgLK...", // 13
        ".......TKKFQKLLLgS....", // 14
        "......SSF4lFWKgLgD....", // 15
        ".....DTFF4llF2LGS.....", // 16
        ".....D1Q1KllllGKS.....", // 17
        "....Kl23F4K3KLQFTD....", // 18
        "....Kl23F4K2Kl2ETD....", // 19
        "...Kl23LKD4KKllgS.....", // 20
        "...Kl3SSl24KKl2S......", // 21
        "...K23SDLl2KKl2S......", // 22
        "...K2gKD1l2KKG2S......", // 23
        "...K2GKS1D2lKKTg......", // 24
        "....DTSKSTDDK2ST......", // 25
        "......................", // 26
        "......................" // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF231E19),
        'D' to Color(0xFF413C32),
        'S' to Color(0xFF645F55),
        'T' to Color(0xFF918C7D),
        'W' to Color(0xFFF5F5F0),
        'N' to Color(0xFF371912),
        'A' to Color(0xFF552314),
        'C' to Color(0xFF783219),
        'O' to Color(0xFF9B411E),
        'H' to Color(0xFFB95528),
        'J' to Color(0xFFD26E37),
        'P' to Color(0xFFD2A56E),
        'Q' to Color(0xFFE1B987),
        'E' to Color(0xFFF0CD9B),
        'F' to Color(0xFFFADCAF),
        'B' to Color(0xFF2D4682),
        'U' to Color(0xFF3C5A9B),
        'V' to Color(0xFF5573B4),
        'M' to Color(0xFF5F417D),
        'I' to Color(0xFF7D5596),
        'R' to Color(0xFFBE3723),
        'Z' to Color(0xFFE14B32),
        'X' to Color(0xFFF06946),
        'Y' to Color(0xFFFA8C55),
        'r' to Color(0xFFA02350),
        'p' to Color(0xFFDC3C6E),
        'q' to Color(0xFFFA6E96),
        '1' to Color(0xFFB49655),
        '2' to Color(0xFFCDB478),
        '3' to Color(0xFFE6CD96),
        '4' to Color(0xFFF5E1AF),
        'G' to Color(0xFF19461E),
        'g' to Color(0xFF2D6E28),
        'L' to Color(0xFF4B9B32),
        'l' to Color(0xFF7DC841),
        't' to Color(0xFF19645F),
        'c' to Color(0xFF28968C),
        'i' to Color(0xFF5AC8B9),
        '0' to Color(0xFF2D4187)
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawSamurai() {
    val matrix = arrayOf(
        "......................", // 0
        "......................", // 1
        ".............N...N....", // 2
        "............TP...NN...", // 3
        "............KPK0DKN...", // 4
        "...........KDDPDDFS...", // 5
        "...........KK0KEWKK...", // 6
        "..........KBKK0000....", // 7
        "..........00DKKKKBK...", // 8
        ".........KD0DKDKD0T...", // 9
        "........KKKEDKKKKBK...", // 10
        ".......000DKDKKKDK0KD.", // 11
        "......DB0EKBD00PBKKEP.", // 12
        "......NPSNKDB00DKKKNT.", // 13
        ".......KDDKKB0KKKDDT..", // 14
        "........KKKSSKSDKKPW..", // 15
        "........SKKDDKSKDKW...", // 16
        "....TTKKKNKDDKKKS.....", // 17
        "..KKDSSDDKKKKDKBD.....", // 18
        "KDNKKK..KBBK0000BK....", // 19
        ".......KD00B000K0C....", // 20
        ".......TKAPDDSDKAK....", // 21
        ".......KKKKNNNNKKK....", // 22
        ".......KNK.....KKDK...", // 23
        ".......KDK......KDDT..", // 24
        ".......KKK.......KKT..", // 25
        "......KKKK.......KKKD.", // 26
        "......................" // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawFreelancer() {
    val matrix = arrayOf(
        "......................", // 0
        "......................", // 1
        ".......N.AA..N........", // 2
        "......NANACAAONAA.....", // 3
        ".....NACACOHHCOJHA....", // 4
        "...NNCOOCHHHOAACN.....", // 5
        "..NACACAAOCCCJOHON....", // 6
        "...NACACOCCAAAAOJA....", // 7
        "....NCOANAAACXNNHA....", // 8
        "...NAACNXA4TSXN.A.....", // 9
        "....NAANXX4TSXN.......", // 10
        ".....NKAHXXXXXYN......", // 11
        ".....KTTNHJXXXN.......", // 12
        "....KTTTTNNNNNTK......", // 13
        "....KTTKTTTSSTTK......", // 14
        "....KTTKSTTSSTSK......", // 15
        "....KTTKDTTSSTDK......", // 16
        "....NJXHNSSTTTNHN.....", // 17
        "....NJJHNDDTTDNHN.....", // 18
        "....NJJHNDSTTTNHN.....", // 19
        ".....CCCDT2SS2KC......", // 20
        ".......NS23SD3K.......", // 21
        ".......DT22SDQK.......", // 22
        "......KS22TDDTK.......", // 23
        "......KS2TDDKAK.......", // 24
        "......KSSADSSKHK......", // 25
        "......KKKKKKKKKK......", // 26
        "......................" // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF231E19),
        'D' to Color(0xFF413C32),
        'S' to Color(0xFF645F55),
        'T' to Color(0xFF918C7D),
        'W' to Color(0xFFF5F5F0),
        'N' to Color(0xFF371912),
        'A' to Color(0xFF552314),
        'C' to Color(0xFF783219),
        'O' to Color(0xFF9B411E),
        'H' to Color(0xFFB95528),
        'J' to Color(0xFFD26E37),
        'P' to Color(0xFFD2A56E),
        'Q' to Color(0xFFE1B987),
        'E' to Color(0xFFF0CD9B),
        'F' to Color(0xFFFADCAF),
        'B' to Color(0xFF2D4682),
        'U' to Color(0xFF3C5A9B),
        'V' to Color(0xFF5573B4),
        'M' to Color(0xFF5F417D),
        'I' to Color(0xFF7D5596),
        'R' to Color(0xFFBE3723),
        'Z' to Color(0xFFE14B32),
        'X' to Color(0xFFF06946),
        'Y' to Color(0xFFFA8C55),
        '1' to Color(0xFFB49655),
        '2' to Color(0xFFCDB478),
        '3' to Color(0xFFE6CD96),
        '4' to Color(0xFFF5E1AF),
        '0' to Color(0xFF2D4187)
    )


    drawPixelMatrix(matrix, palette)
}

fun DrawScope.drawOnionKnight() {
    val matrix = arrayOf(
        "......................", // 0
        "...KKKKKKKK...........", // 1
        ".KKKWWKKPWKKKKKK......", // 2
        ".KWKKWPRKKKKRWRKKK....", // 3
        ".KKWWWPRRRRKRRWRKKK...", // 4
        ".KKPPKRRRRKRRRRPRKKK..", // 5
        ".KPPKRRRKKRRRPPPRRPKK.", // 6
        ".KPPKRRKKKRRRKKKKKKKK.", // 7
        ".KPRKRKKKRRRRKKKKKKRK.", // 8
        ".KPKPRKKKRPKKPPKKKPKK.", // 9
        ".KPKKRRKKPKPPPPPKKKK..", // 10
        ".KKWKKPPPKPKKPKKKKP...", // 11
        "..KKKRPPPPKKPPKPKK....", // 12
        "...KRRKKKPPKPPKPKKKK..", // 13
        "...KKKPPPKPPPPPPKKWKK.", // 14
        "....KWPPPWKWPPWKPPWWK.", // 15
        "...KKKRRRRKKKKKKRRRKK.", // 16
        "...KWRRKKPPKRRWWKKPPK.", // 17
        "...KWRRKKPPKWRRWKKPPK.", // 18
        "...KWKRRRRKWWWRRKKRKK.", // 19
        "..KKWKKKKKRRRRRWRKKK..", // 20
        "..KWWKKKRRRRRRRRRKK...", // 21
        "..KWWKKKRRRRRRRRRKK...", // 22
        ".KKWWKKKKRRRRRRRKKK...", // 23
        ".KWWWKPRKWWWWWKKKK....", // 24
        ".KWWWKPRKWWWWWWKPP....", // 25
        ".KKKKKPRKKKKKKKKPP....", // 26
        "......PPP.......PP...." // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF17202A), // Dark outline / details
        'W' to Color(0xFFFFFFFF), // White plume, collar, socks
        'R' to Color(0xFFE74C3C), // Crimson red ribbon / dress
        'P' to Color(0xFFF3C59D)  // Peach skin tone
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
        "......KNKKKrrrK..KNK..", // 0
        "......KrrrrppprrKKPK..", // 1
        ".......KrpppppprA1pK..", // 2
        ".......KrrppppprrppK..", // 3
        "........KprpppprNrMK..", // 4
        "........KrKIppINNNKKrK", // 5
        "......KKKrPDrpD111AIK.", // 6
        ".....KDDKrrPArADKNMrrK", // 7
        ".....KTDKMMppNATKNrK..", // 8
        "...KrNWDMNKppNASSKpK..", // 9
        "....KSWDrKPKK1rrDKQK..", // 10
        ".K11KWTrpMr111rQ112JK.", // 11
        "K1ArDTNqprKMrKQK1DpQK.", // 12
        "KKNKTDK12pKDKKPNKSNPK.", // 13
        "A21NZK.KA1GggKK1AANK..", // 14
        "12ANNK.KGGGggGGGNANK..", // 15
        "1NAGK...KgGGGGLKGGK...", // 16
        "AKGgK.KRKGgGGK.KglK...", // 17
        ".....KGKRNKKKKNKLNK11K", // 18
        "....KLgGKZZA2RKK1NGGAN", // 19
        "....KA1LgKKN1KGKA2A12N", // 20
        "..KZNKKN1NGKKGNAKAKGAK", // 21
        "..KZZNKKGGGKGGGGKKKGDK", // 22
        ".KNRZGGGggKKKKGgAKKKKK", // 23
        ".KRKNKggg1KKRKg1K.....", // 24
        ".KZKKKGLGKKKZRK.......", // 25
        ".KZKK.......KZNK......", // 26
        ".KNZK................." // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawWolf() {
    val matrix = arrayOf(
        "......................", // 0
        "......................", // 1
        "......................", // 2
        "..............K.......", // 3
        "....KK........DK.K....", // 4
        "...KK.........DK.KK...", // 5
        "..KKK........KKDKKK...", // 6
        "..KK........KKKNDDK...", // 7
        "..KKK.....KKDKNDDDD...", // 8
        "..KDNK..KKDKDNNDDDD...", // 9
        "..KNDKKKDDDDDNNDKNDK..", // 10
        "...KDDDDDDDDDNNDKRDK..", // 11
        "...KDDDDDDDDDNNDDNKDD.", // 12
        "....KNNKNDNNNDKNDDDKK.", // 13
        ".....KKNDDDDDNNNDNNNK.", // 14
        "......KNDDDDDDDNNNNKK.", // 15
        "......DNNNDDNDDDKKNK..", // 16
        "......DDDNNDNNDDK.K...", // 17
        ".....KDDNKNNKNNDK.....", // 18
        ".....KDDNKKKKKDND.....", // 19
        "....KKNNKKKDKKDDD.....", // 20
        "....KDDNDK...KDDDD....", // 21
        "....KNDKIK...KNDDND...", // 22
        ".....KNKDK....KNNNK...", // 23
        ".....KKKKKK...KKKKK...", // 24
        "......................", // 26
        "......................" // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawPirate() {
    val matrix = arrayOf(
        "......................", // 0
        "......................", // 1
        "......STSSASTDSSS.....", // 2
        "......AWWWWSKSAPNT....", // 3
        "....KNWWNKKDNNPPAN....", // 4
        "......DDDPPKKKSTAK....", // 5
        ".....KKGKKLAKPKKSN....", // 6
        "....KAKNQNKPKAANKK....", // 7
        "..SPGKQCPAKKAKNQQT..S.", // 8
        "..SAKPQQANKNAKANK..DS.", // 9
        "..SNPPPAAKAAN.NND..ST.", // 10
        "..SKPAKKGGDDS..K..STD.", // 11
        "..SKPPKGGGGKS....STSD.", // 12
        "..DGPQNGKKS.....STSDD.", // 13
        "..GGPPKGGTKKK.DTTSDK..", // 14
        ".KGGNAGKKKNAAKTTSDK...", // 15
        ".KGNGGKKQDSTSDDKK.....", // 16
        ".KDKKGANPDDDDKK.......", // 17
        ".KGQKAQKPKKKNN........", // 18
        ".KGKKKAKKKGNN.........", // 19
        ".KGNPAKKGKGGK.........", // 20
        "..DKCPCNGKKGS.........", // 21
        "..KGKAQCKGKK..........", // 22
        "..KGNKQPNGKK..........", // 23
        ".KGGKAAAKKGGD.........", // 24
        ".KGKKKNNKKKGGKKK......", // 25
        ".KGGKKKKKKKKGGKKKKK...", // 26
        "......................" // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawOgre() {
    val matrix = arrayOf(
        "......................", // 0
        "...........PNKKKK.....", // 1
        ".......KAAPAAQAPAAK...", // 2
        ".....TKDNNAAPCAPQPA...", // 3
        "....KmINlKKQPNPNAPP...", // 4
        "...KiWiMLDIKKAKKNAKK..", // 5
        "..MriimIDSMmmQDNKKKK..", // 6
        "..DImmiIDlDmIPIiIK.K..", // 7
        ".KmDmimMDSNIMKmDKK....", // 8
        ".ImIDrMDlLSMDIKKM.....", // 9
        "KmmImmKDDlLMmIKmr.....", // 10
        "KmiMrDDlSlLDIDKiI.....", // 11
        "KmmWIKKGlDDDDKDmmK....", // 12
        ".ImmIKDDKDDKTKIImD....", // 13
        ".IDDNKMDDDK...NNMCT...", // 14
        ".KllDKmmKDK..KKQPPNK..", // 15
        "KDEKDKIIDKDKKQPANQPA..", // 16
        "DKNlDKNMNNDKPAPQQPAP..", // 17
        "MKGDKIMDKKAPPQPAPPQN..", // 18
        "MKKImIDKNAPPAANAPPAKm.", // 19
        ".KKIiWmKNCAAAAPNAPAKKK", // 20
        ".SKKKMIDANNNNMNrKNKKKK", // 21
        "..KKIrKKKKKDImMIKKKIIN", // 22
        "...KMMKKNNMKKIIIK.KKK.", // 23
        "...DIDWTMMKKDDMK......", // 24
        "..TImID....TT.........", // 25
        "..Kiiim...............", // 26
        "...KKKK..............." // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawEye() {
    val matrix = arrayOf(
        "......................", // 0
        ".......KPCAKKKSN......", // 1
        ".......PNKKKCNKNC.....", // 2
        ".....KPKNCCNKCANKK....", // 3
        "....CKANCNNKCNASSSDS..", // 4
        "....AKKKKAKKPCDSTSSD..", // 5
        "...KKKKAANNCPAPQTTTSK.", // 6
        "...KNKNKKAKCNDSTTTTDD.", // 7
        "...PNAKNKKNNASSTTSSKM.", // 8
        "...AANKPNKKNNAAPTTMDD.", // 9
        "..KKKACSTTSNNCDSSTTSK.", // 10
        "..PKNPDSTTTKAAADSSSSK.", // 11
        ".KKNACDSTDTMNANDDCDS..", // 12
        ".AAKAADSTDDMNKNKKDK...", // 13
        ".NCNACDQTSMSKNKNKK....", // 14
        ".KKCNCNSQSSDAK........", // 15
        ".KKPKKAADNKKK.........", // 16
        "..CCNKKNAKKK..........", // 17
        "..KKNNKKKNKNA.........", // 18
        "...KAKNAAAKWN.........", // 19
        "...NAKANKNKAN.........", // 20
        "...NDNNKADN..NS.......", // 21
        "....TKKNA.............", // 22
        "......NNNT.N..........", // 23
        ".........N............", // 24
        ".....A..AT............", // 25
        ".....A..AT............", // 26
        "........C............." // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawSlime() {
    val matrix = arrayOf(
        "......................", // 0
        "......................", // 1
        ".........KKKKKK.......", // 2
        "........KNZXXXRK......", // 3
        "......KAXXXXXXOZD.....", // 4
        "......AZQQXXXXZQZK....", // 5
        "......RQXXXXXXXXXK.KKK", // 6
        ".....KXQXAKZXXXXXCKZZ.", // 7
        "....KRZXXXXAPZOOXCZJN.", // 8
        "....SXXWXXXKQZNKXCZJK.", // 9
        "....NXEWWFXKQZACXCZJK.", // 10
        "..KKZQTWWWZZCCXXFZKXK.", // 11
        ".SAXQOPWWTWERRQESFKXK.", // 12
        ".KXXRKKCWWWWXWWWWZKC..", // 13
        ".CXNZQQNCZZZXZZZCXKA..", // 14
        ".CXXXXJQZXKXXXNCZZXA..", // 15
        ".CXOXZPKXAWRRRDKDAXA..", // 16
        ".NXQPZNCNNKZWCCZKNXXK.", // 17
        "..RXPZNCWNZXKXXZZCXXK.", // 18
        "..NXXZNXKXXXXXXXXXXXZ.", // 19
        "..KZXZNXXXXXXXXXXXXXZ.", // 20
        "...KXZPNZXXXXXXXNXXXZ.", // 21
        "...KXZJKZOXQJXXQCNXJK.", // 22
        "...KXXQNXZZXZQXXCAQJK.", // 23
        "...DCCNXAADAAXXZQZARD.", // 24
        "....SKCZ.....KKXZXK...", // 25
        ".....KKK.......KKN....", // 26
        "......................" // 27
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawBomb() {
    val matrix = arrayOf(
        "..DS..E......DD.......", // 0
        ".NEP..OSN...K4FK......", // 1
        ".OEEN..PEJ..TOJN......", // 2
        ".COPN..ACS.EKNTS......", // 3
        ".NAA...SNNK..K........", // 4
        ".......FKKKKK4KKN.....", // 5
        "...FQ.SPNNNAAPANASS...", // 6
        "......NNNJNNNAACCKK...", // 7
        ".....KKAKCNNAJ4EEOK...", // 8
        ".....SNNNNNAAACQEENS..", // 9
        ".....KNANNNNNNJAJ4AK..", // 10
        ".....KANNNNAANNQNANAS.", // 11
        "..TT.NNNNNNNPCACANCCN.", // 12
        ".KKS.NNNNCKIKr4CKKCKK.", // 13
        "KKQQAAAAKAAMINKANNrIK.", // 14
        ".TPCOOANNJJNMCAKAPCNCK", // 15
        "....KrANKO4JC4AOJ4rJAK", // 16
        ".....NNNKCNPOANAACANCK", // 17
        ".....KNAKCKQQNCAANONPK", // 18
        "......NAKKKCKKrKKKKKNK", // 19
        "......NANNKKKKKKKKKK..", // 20
        ".......NAOKKAKNKNKN...", // 21
        "........KNCOOKJKPCK...", // 22
        ".........KKNNNNNNK....", // 23
        "..........KKKNNKD.....", // 24
        "......................", // 25


    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawSahagin() {
    val matrix = arrayOf(
        "................................", // 0
        ".........W..WDW.................", // 1
        "........SDS.SDSS..SSW...........", // 2
        ".......DSDDDDDWKTDKSS...........", // 3
        "......DTSKKSKDTKDTSSSKK.........", // 4
        "......WKSSKdTKSBDTDSSSTK........", // 5
        "...KK.KdTDDDtTtdtSDSSTD.......D.", // 6
        "..KSSKSSSSGDDtTtSTSDSDT...K.KDTD", // 7
        ".DDSKSdtSTdKDBStddSSSDDDDKSDDTD.", // 8
        "..TDSStGGSTSDKtSctSTSDDttDDSTTD.", // 9
        "..KTttKDKtTTSDKtSDQDdDKKKKKBTSD.", // 10
        ".KdTdKDKDKSStBKdKKtdcDW...KdSTDD", // 11
        ".KSTTSKKtStttdKdKKKtDT....KTttSD", // 12
        ".KTDDFKDtDSSTK.KdK.KS.....KTSDSD", // 13
        "KtTDSKKDDtSdSD.DD.........DSKDDS", // 14
        "SDDSDKDDDttcK..............TTS..", // 15
        "...KSKDDtDtK....DDD.............", // 16
        "..KTDKDDttDK..KKTTSD............", // 17
        ".KWDDKDBKKBKKKdTSStDS...........", // 18
        ".KSKDKDtBKKBddtBBtSDS...........", // 19
        "KTKWKDKDtSStDDttDKtSDT......KK..", // 20
        ".DTDDSKDtttttttDKtSSST...TDDTTS.", // 21
        ".DSSDSDDDtttDDKKKGtSSS..SSSSSSD.", // 22
        ".KSKSKKDKDDDBKKKDKKtSDTKTDSTTWSD", // 23
        "..KSKWKDKDDDK.KBtK.GctKTDdtKKKD.", // 24
        "..KtKWKDDKKK..KcK..TDStSdKK.....", // 25
        "...K.KSDDGDK..KBK...TDStK.......", // 26
        ".....KDGDTWD..KBtK...DDDD.......", // 27
        "....KDDGD...SDDKKDDS.TDS........", // 28
        "....KKWTS...KKK..KDDD...........", // 29
        "....K.............KKK..........", // 30
        "................................" // 31
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawCockatrice() {
    val matrix = arrayOf(
        ".......................TSSS.....", // 0
        "...........TTTSSDDDDTTDD0ctGDT..", // 1
        ".........SD00tVVViiiV0Dtt00VSdD.", // 2
        ".......Gt0cVtViiVV0DD0Kt00tGKKK.", // 3
        "......Gd0VciVVVUDGGDKDDDtKT.....", // 4
        ".....D0SVV0VUm0KBKKGDD0tD.......", // 5
        "....SGV0VUVVVUKKBD000DtK........", // 6
        "....DStUUVVVUGDG0DG00UV0S.......", // 7
        "...KD0UUUVVVGKGBKB000tUVVD...S..", // 8
        "..TttV00VUVBKGKB0BGG00UVVV0KD...", // 9
        "..GVtDtVUVVKGK0KDDDDDDB0VVVVK.N.", // 10
        ".KVDUDUV0VVKK0KDDDDSDDK000VVV0D.", // 11
        ".VtKVU000VGGGKNNDDDDDKKUVU0VVI..", // 12
        "DdKcDVt0VUGKKDNDDNK....DKK0BGiB.", // 13
        "K.NVDU00VBKKKKDDK......DT.B0SBB.", // 14
        "..Gct0VV00KDDKNN..........SDVtN.", // 15
        "..00ttV0UVGNDNKK..........KrSTS.", // 16
        ".KdtcDVV0VVDDND...........KS.T0.", // 17
        ".KdGVDUV0VUGDNKDKK...........K..", // 18
        ".KdKdDVV0VDVDDrSDSSKD...........", // 19
        ".StKdDcUUVK0DDDNDKNDD...........", // 20
        ".dKKdDcUtVKKVKKNKrNrD...........", // 21
        ".dKKdKVGcctKDVK.KKKD............", // 22
        ".DDKdKdGVDUD.DD.DSDK............", // 23
        ".KKKDKdDcDVD..N.DDDNN...........", // 24
        ".KKSKKSDccDcK....STN............", // 25
        ".KKSK.GDGdKVST..................", // 26
        ".K.KN.KGKdDGdS....TTTD..........", // 27
        ".K.K...GDGVKSSDTGGWKS...........", // 28
        ".K.K...N.TDKS0DNS...............", // 29
        "...K......DK..DDKT..............", // 30
        "...............DD..............." // 31
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGarland() {
    val matrix = arrayOf(
        "................................", // 0
        "................................", // 1
        "................................", // 2
        "..........MMK..KKK.KK....KK.....", // 3
        "..........MMKKKKKKKIIK..KSmK....", // 4
        "..........KKSTTmKKIMMK..KTWK....", // 5
        "........SKSSTWWTmmKBBKKKTSSK....", // 6
        ".......DDDTTTTTTSSDIISSSTDK.....", // 7
        ".......KK0TTSSSSIIISSTTTSKK.....", // 8
        ".......KKSmmSB0SB0TTTmSSKKK.....", // 9
        ".......KKSBBKKKBB0mmmKKKK.......", // 10
        ".......KKBKKpKKSBBKKKBKK........", // 11
        ".......KKKKKKKKKSSBKKKKKK.......", // 12
        ".......KKKKKMMMJKKKMMImmIKK.....", // 13
        ".......KKMmmmIIMJJKPPPMMMMMK....", // 14
        "......KIImmmIIIMEEKKKKPPMMMK....", // 15
        "......KIImmmIIIMEEKKKKNArIIKDD..", // 16
        "......KIIIIIIIIMEEKDDKKKPIIMKK..", // 17
        "....KKIIIIIIIMMEKKKJJmKKKMMMKK..", // 18
        "....KKIIIIIIIMMEKKBPPTTTKIIMKK..", // 19
        "....KKIIIMIIIEEKSSBPPSSSKPJIMMK.", // 20
        "...KIIIMMMIIMEEKKKKKKKKKKPJIMMK.", // 21
        "...KIIIMMIIIMEEKBBBBBKKKKPJIMMK.", // 22
        "..KKMMMMMIMMEKKSB0SBBBKKKKKJIIK.", // 23
        ".DDMMMMMMMMMEKK0KK0BBIDDKKKJIIK.", // 24
        ".KKMMMMMMMMMQKKKKKKSS00BDKKJIIK.", // 25
        ".KKMMMMMMMMMJKKKMMKTT000SKKJIIK.", // 26
        ".KKMMMMMMMMMJKKMMMKBBmTTSKKJKKK.", // 27
        ".KNPMMMMMMMMJKKJJJKJJBTTSKKKKK..", // 28
        "...KJQEEEEJJJJJKKKJEEJDBPKKK....", // 29
        "....KKKKKKKKKKKK..KKKKKKK.......", // 30
        "................................" // 31
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawLich() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        "............................DKKKKKKD.........KKKKK..............", // 2
        ".....................DKKKKKKImmmmmmIK.DKD..KKK..rKK.............", // 3
        "................DKKKKrrCOOmPNKKKKKKDmKCOAKKK.....IDK............", // 4
        "........KKKKKKKKrrrrrKKKKKKKImmKmmmIKOKKKK..DKKK.KKKK...........", // 5
        ".....KKKKrrrrIrrIKKKKrrmmOmKKKKK.NKD.KKDD.KKKKKK.KKKrK..........", // 6
        "....KrrrrKKKKKrKKrrN.KKKKKKKKKD...KD.KK.KK.KKKKK.KKKrK..........", // 7
        ".....DKKD.KrrrrrrNDKK.DKKKKmDKKCFPAKKmKK..KNKKDKKKDDKK..........", // 8
        "...........KKKKKKKKKKKDKNCCKDKKKXNDKKmKKKKK.DKKKrKKDKN..........", // 9
        ".................DKKKKKKKKKKIIDmmmIDmKKDKD.KDNrrrrKKD...........", // 10
        "................NP2QQKK...KDImmmmmmmmKKK.KKNrrrrrQAK............", // 11
        "..............KKPQ1NNKKKKKKIIIDDIDDDIIDKKKK1CrP1PDK.............", // 12
        ".........KKKKKNNN1QQ1KKKKNNKKDKKDKKKKKIDNNKK11PDKK.K............", // 13
        ".......KKNNNNA111111KKKNNAADKKNK.DKNKDKNAANKKKD.KKKNAKK.........", // 14
        ".....KDNK1111AKKKKKKKKKACKKKDNADKDDAK.KKKKOKKNI.KN13QNNK........", // 15
        ".....KPKKKKKNNKKNKKDrDKNRAAKKKKKKDKKKKKKKAOKKNI.KN1333Q1PK......", // 16
        ".........KDNNNNKrrMrrNKKKNNKIK.KKKKiKKKKKNNKKNI.KNNAAA23CD......", // 17
        "........KrKrKKrDrrrrrDrKNKKKKKKD.KKKKKKKKKKKKKI.KrrKKKQ3CD......", // 18
        ".......KKrrrrDKKKKNrrrNKK.DKKKDKDKNDKKKDK.KKKmIKKrrr11Q3CD......", // 19
        "........KKrrrrrrKKKKKKKKNNKKKKKKKKKKKKKDNIIKKmIKKMrP333PDD.D....", // 20
        ".......KDrrrrrrrKKKKKKKKKKKKDDKNKKKDKKDKKKKrDKKNrQQQ33rKNrDrDK..", // 21
        "......KDrrrrrrrrKK...KKDKK.DKKNrKINKK.KKDKKrrDNrQQ3QrrrKNrrrKKK.", // 22
        "......KNrrrrrrrrKKKKK.KmKKKKKKKr.INKKKKKKKrrrrrr33PrrrrrNKKKrrDD", // 23
        ".....KDrrrrrrrrrKKKKKKKKD.KKDKKKKKDKKKKDNrrQrrr333PrrKrrrrK.KKD.", // 24
        ".....KMrrrrrrrrrrKKN3KKK..KKDDKK.NKD.KKDKrrQ333QrrrrrrKrrrrKKKD.", // 25
        "....KrrNKrrrrrrrrKNQKIKK..KKDKKK3NKKKKQ33333rrrrrrrrrrKKKKrrKNDK", // 26
        ".....KMNKrrrrrrrrKNPKrKKD.KKKKKK.E33333NNrrrrrrrrrrPQrrKKKKrrrMK", // 27
        ".....KMNKKrrrrrrIKNQKKKDD.KKKDKK.DK..KKKKKrrrrrrrQ3PIrrrrMKKKKD.", // 28
        ".....KMNKrrrrrrKrNKN3KKDD.KKKKKK.NKKKKKDNrrrrrr33rrrrrrrrrrKKK..", // 29
        "......KKKrrrrrrKKKKKKKKK..KKKN333NKKK.KKNrrrQ33QIrrP2KKKNrrIrND.", // 30
        ".......KKrrrrrrKKDNQKKKKKKKKKNQ3333333Q3QQQQQPIMrIP3QKKKKKKrKNDK", // 31
        ".......KKrKrrrrKK11KrrrrrKKKDKK1EQ1111DKKKrrrrrrrrPQQrrrNKKKKKK.", // 32
        "......KNrKKDKKrKN11KrrrrrrKKDKKK.DKKDDKDNrrrrrrr33QPrrKKKrrrKK..", // 33
        "......KNrKrKKKMK2QPNrrrrrKKKKKKK.NKDKKDrrrrrrrP33QPrrrrNKKKKKKK.", // 34
        "......DDrKrDDDDD1QQrrrrrrrKKKKKKKDMMKKrrrrrrrP3QQrNKrrrrrrrrrNDK", // 35
        ".....KDrrKK.KK..K13QPPPrrKNrKKNNKrrrNKPPPP333QPPrrMKKNNNDKKKKDDK", // 36
        "......KDDK.....KDCPP333PPAPPCArrKCPPPA2333QPPrrrrNrrNNKKK....KK.", // 37
        "........K...KKKNrrrrPPQ333333QPPAQ333233QPPrrrrrrKNCPNNNDKK.....", // 38
        "..........KKDNMrrrrrrIPPPPPPQ333333QPPPPrrrrrrrrrDKNQKNNNDDK....", // 39
        "......KDDDrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrPQKKKKKKrDK..", // 40
        ".....DDrrrKrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrPPrMDKKKKrKK..", // 41
        "....DrNKKKKDKKKrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrQQPrrKKK.KKrDK.", // 42
        "....KKKKKKKDrrrrrrrrrrrrrrrrrrrQQQQQQrrrrrrrrPQQQ3PrrrrrDD.KrNK.", // 43
        ".......KKrrrrrrrrrrrrrrrrrrMQQQ333333QQQQQQQQQ333rrrrrrrrrKKrNK.", // 44
        "......KDrIKKKKrrrrrrrrrrrrQQ333333333333333333QrrrrrrrrrrrK.KMMK", // 45
        "......DNrK.DDrrKrrrrrrrrr333333333333333QrrrrrrrrrrrrrrrrrK..KK.", // 46
        ".....DMNN..DNrKKrrrrrrrrr3333333rrrrrrrrrrrrrrrrrrrrrrrrrrK.....", // 47
        ".....KrNK..DDrKKrrrrrrrrrrQ33PrMrrrrrrrrrrrrrrrrrrrrrrrrrrrK....", // 48
        ".....KMNK.KMNKKrrrrrrrrrrrrrIrrrrrrrrrrrrrrrrrrrrQ33QrKKNrrK....", // 49
        ".....KMDD.KrNKKrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr33AKKKK.KKrK....", // 50
        "....KrNK..KrNKKrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrP32KKDrIrKK.KKrKK..", // 51
        "...KKKK.KKrNKKIrrrrrrrrrrrrrrrrrrrrrrrrQPrrrrNKKrrrrrrrKKKKK....", // 52
        "..KNrNK..KKKDNrrrrrrrrrrrrrrrrrrrrP3QQQrrrrrKKNrrrrrrKNrrrrKK...", // 53
        "....D.KKKKKKDrrrrrrrrrrrrrQrrrQQQQQPrrrrrrrrrrrrrKKKK..KDDDrDK..", // 54
        ".....KDKKrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrD.....D....", // 55
        ".....KrrDKDKKKNrrrrrrrrrrrrrrrrrrMDDDDDrrrrrrrrrNNNKKKKKKKKK....", // 56
        ".....KrNK.KDNNrDNNNDrrrrrrrrrrrNDKKNNNNrrrrrrrrrrrrrNNNNNNNNK...", // 57
        "....KNNDKKNrMNDKKKNNrNNrrrrrrrNNNNMrrrrrrNDNNrrrrDNNNNNNNNNNK...", // 58
        "....KNDKKNNNKKKKKKrrNNKKMrNNNNDrNrrrrNNNDKKKKDNDNKKKKKKKKKKK....", // 59
        ".....KKKKrKNMMMrrKKrKKKKKKKKNMrKrrrKKKKKKDK...KK................", // 60
        ".......KKrrNKKKKKKKNrrDDDrrrNKDKKKNrrrrrrrrD....................", // 61
        ".......KKKKKKKKKKK.KKKMKKKKKKK..KKKKKKKKKKKK....................", // 62
        ".........KK.........KKDKKKKK.......KKKKKKK......................" // 63
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawKraken() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        "................................................................", // 2
        ".......B00S..................KKK................................", // 3
        "......IUUUUS................0VVV0SK.............................", // 4
        ".....SUU0UVU...............DUUVdVVUD............................", // 5
        ".....00NNBUU0..............D0VdVVVU0D............KD.............", // 6
        ".....BSPAKmU0...KKKN.....KK0VVdVUUUV0K..........SKUKS...........", // 7
        "....DUPPN..K...KPDEmID..S0VVVVVUDD0U0VD........S0000US..........", // 8
        "....B0CCN......DDMDSmmNMKDUVdVUVKKBB0KDK...KKKK000B0U0K..K......", // 9
        "....0DPA...........IDImNDNK0UUVUBUdVVKND..SSQPK00BKK0UUNN0D.....", // 10
        "...K0PPD............KPIiNDMK0VVVVVUS00DDKKSDDKADBD..D0UU0DS.....", // 11
        "...BUCAD...........DNKASMNKK0UUVUSKNKKBKNIASK00DBN...K00K.......", // 12
        "...KUAQN.........KNIIMKAIIMKDBUUNKDDSDKDNrNNK0BBD.....DD........", // 13
        "...KUPAN........NMMKKKKNNPmMMMKUUU0U000DIANKKKKBS...............", // 14
        "...KUQPD......KS0UUUUUUBKNrIIIINBUUUUUBMAKD00UUDD...............", // 15
        "...KUAAD......UUVVVVVVUU0DKrMImMMK0000NrKKKK0UVU0D......D.......", // 16
        "...B0SPA....SUVVVVVUUUUUU0DAImIIIDNGDKNB0UU0BB0UU0DS....0D......", // 17
        "....0UNP...KVVVVVVUUUUUUU0BKQSmmIrNLgK0VVVUUVUK0UU00K...U0......", // 18
        "....0UPPN.KUdVVUUUUUUUU0000KIrQQPrAgGUVVUUUUUUU0D00000D.DUD.....", // 19
        "....D00PPKBdVVVUUUU0000000BBSIKKDIDPKVU0KKNNSUUUBB0000BK.DU0....", // 20
        "....KB0APKUdVVUUBKKBDDDBBBBBKNSmmIIKUUB000BKEDUUUBKK0U0K.SUD....", // 21
        ".....B00PDVVVUU0KKNKKNPABBBKKKDIiIMKVUK0000DKEPDVU00UUU0KS0UK...", // 22
        "......D00UVVVU0NANN0KBKKNKKBBBDNmmMKVKD000KNDNPPDDUVVUU0K.KUQK..", // 23
        "......K0BUVVUUKADD0BK0BBKK00BBDKQmID0KKBBKNKM.DQPP00UU0DK.KUDS..", // 24
        ".......KKVVVU0K0000K000BKK00BBBKKSIMKKKKBKKNM..DAAQD0DDK..K0QN..", // 25
        "........KUVVU0KDDKKD000BK0000BBBKNIMKVV0KKKKDK.DAAPAAAN...K0PS..", // 26
        "........D0UVU0BKKKKD000BK0U000BBKKND0SSVdKDDKK..SNPNAK....KUEN..", // 27
        ".........0UUU000BKMB000BBUUU00BKKKKKKQQPQVK0BKK...SSS....D0VPS..", // 28
        ".........D00UU000BK0UU0BBUVU00KBBBBKKKKPEdD000K..KKK0...00UQPK..", // 29
        "..........DB0UU00B0K0U00BUVUUBKBB0BBKKNAADVK0UDSDVVU00BB00UPK...", // 30
        "...........KK0000000KU00BVVVUBKB0000BKKPEPSD0UKUVVUUUU00UUPQ....", // 31
        "...........KMNKB0000BK00BUVVU0K000000KKKPFS0BBUUUUUUUUUUDDPK....", // 32
        "...........NDKMKB0000000DUVVVUBK0UUU0BKDPEDUKUUUUUUDAPANQPD.....", // 33
        "..........K0UKDMKB0000K0D0VVVVUBKUVUU0KKACESKUU0UDNNAAPNNK......", // 34
        "..........DUUVKDKKB000B0DKVVVVVV0KUUU0KKFCQDKU00DPN.NNN.........", // 35
        "..........KSK00DKMB000BB0KUVVVdVVU0BDKKKQNPSB00BNK..............", // 36
        "...........KK0U0KN0U000D0KKUVVVddVVUUUUUKCED000DS...............", // 37
        "...........KDA0UU00UU0BB0DKKUUVVVVVdVdVVKNAU00D0S...............", // 38
        "...........KIKD0UU000BKB0DBKDUUUVVVVVVVV0PQSUKB00..KKDDK........", // 39
        "...........KmK1SD00BBBK00BKBKKDUUUUUUUUUNQPDUBKU0.DUVddUK.......", // 40
        "...........KmKKAEDDDAK0000KBNKKKD000000BDPQPV0BUBKUVVVVd0K......", // 41
        "...........KmKNKNNPKKB0U00KBNNAKK0KKKKKKKPNPV0U0N0VVNNUVVB......", // 42
        "...........KIKDKKKNKB00U000BNNAKK0UUU0KKKQQPDUUDNUUNNNKUVUK.....", // 43
        "...........KMKMMNKMMD0UVU0BBNNCNK0UVU0KNKPAAQNPNKUVPNK.KUdD.....", // 44
        "...........NMKDMNNMMK0UVU0BKANNKK0UVU0KKNKPAANADDUDQPD..0VD.....", // 45
        "........KKKKMDNMNMMMNK0VVU0KNNNKD0UVU0KNNKKPCNK.BUNEPN..DUD.....", // 46
        ".......DVVVUKMNMNMMMKK0VVU0KANNKB0UU00KNKNDKNS.SUUQAQA..KDK.....", // 47
        ".......KVVcU0KKMNMMNNK0VVU00NNNKBUUU00KNANBBD..DUcEPQD..........", // 48
        "......SVVDNDUKKMKMMNMKKUUU00KNNK0UUU0BKANNBBD..0USAAAK..........", // 49
        "......SVUKASVUKMKMKNNKK0UU00KNNK00U00KKNNNB0D..0UAECPD..........", // 50
        "......SUKPACV0KNKKKKKKBBU000BNNKB0000KKNNNBBBDDUUAEAPKB0K.......", // 51
        ".......D.NAAD0UKKKKKKKBB0UU0BNKBB0UUUKKNKNBB0BUUDAPPN000BS......", // 52
        ".........KDEDUUDKKKKKBK00U00BKKB0U0UVMKNNPDBBKVUSFNEN0DD0BS.....", // 53
        "........KKPQN0UBKKKKBB000000BKSB00UU00BKDNNNKUUUAEPPDNDDNDD.....", // 54
        ".......KIKAPCdUUKKKKK00UU0000KKBB00UUUU0000UVVUDPNNKAD.K.KDN....", // 55
        "......SDMKAEPc0UUDDBUUU00000K..DB00UUUUUUUVVVU0DQPPNS.....K.K...", // 56
        ".....SMMKKNPPSU0UUVVUU000000K..KB0000VUUVVVUU0DPAQA.............", // 57
        ".....SSKKKNPNA000UUUUU00000K....SDB000UUUUU00DNPADK.............", // 58
        "..........SQNQDU0000000000KK......KK00000000SPKNNS..............", // 59
        "...........SDAANDUU000DDKS......SKKSKKKK00DDNANNS...............", // 60
        "............KNPANNNDNND..............NNNKDDDSDD.................", // 61
        "...........DKKKKNNNNDK..........................................", // 62
        "............KKK................................................." // 63
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawTiamat() {
    val matrix = arrayOf(
        "................................................................", // 0
        "...............................................DK.S.............", // 1
        "...............................................NN.KS............", // 2
        "...............................................SNSKD............", // 3
        "...............................................KDKKPK...........", // 4
        ".....................................K...DS.....D1K1S...........", // 5
        "....................................KS...DPD.K.DD1DDN...........", // 6
        "............................SSK.KKSNKNS.SSKrNADSKNDDAS..........", // 7
        ".............................NN.KADCrDCKKrPDtKK4DKDDQK..........", // 8
        "........................SSS..KDNrADttSSSD00tDSSDDBGtSK..........", // 9
        ".......................DDDS..NrDtDdddddSddddDKKQDtdScK..........", // 10
        ".....................SNASS.SKDNGDNdDKSS0S0DB0BBKtcSStDDK........", // 11
        "....................D4AK..SKCrDKCAKPKMIMIBUUKK00tdddt4DK........", // 12
        "...................KDQDKSNAQPGDSSDDDNDDDSDKB0BBtcttSSSDK........", // 13
        "..................KD1KKDrCQKttSddSttrNK.KB0KBKKtSddSSdtG........", // 14
        ".................SGtGGttSQADPPDdSDtDDrDKDSSKKKSKDcSdDDSDS.......", // 15
        "................DKdtdddddDQKK0DKKDKQKNPK..KPK....KKcdKGctK......", // 16
        "............SSSSSSSSDDtcdcDSDBNrNDDSDDPKDKKPS....SKDtdGtdK......", // 17
        "...........KSDKddtttDtdddd00KrrDSdddDSDDAKKQS.......DKDDtK......", // 18
        "........KD.StddSKcSccctttSDNNDddddddDlSDrNNQDSSSS......SS.K.....", // 19
        "........DK.KKttttdddddddttKDtddSddddSSEdDN1QKKKKKKKS......KK....", // 20
        ".......SNK..DNKKGDDDtccttKDStdddScDSDDESSDEQKDDttttDGS...SD.....", // 21
        ".SKK...KPN.KAKDKKKKKKKKKKKDdddSdtBKKDP14Dt1QKKtttddSttD.KID.....", // 22
        "..SDDK.SDQKNNDdDDtDK..KKKGttddtIIMKKtDDDttSDKttttSddStDDIDSKSSD.", // 23
        "...KrN..NQEKttDPDtdDK..DKtSdttIIMKP4Q1DdStdSDKtcdtBMIIIIK.KIIK..", // 24
        "....DQDSKPE1DtNlKttSK..DDttStMMKKKKDDDStSSdSDK00MIIIIIMDDDMDS.KK", // 25
        "....KCE1KK1QDtEDKKtStK.DttttBDDKDKP4PKdDSSdtGMIIMIMMMMMMMDDDKKS.", // 26
        "KNDDDDQQ1DttcdDDKDKttKSKtttDKrDDKNKKBBtdDDStKIMIMMBMDKDMBKKDKS..", // 27
        "KKKKPEK1EddcddtK0KKDttKDtKKCDttttGKKBBKtScdDKBBMBKKKKKDMBKKK....", // 28
        ".SDtBDSDcddSSdSKKBBKtDKDKKDrtSctttKKKDKKttdSKBKKKKKKKBKDD.......", // 29
        "...KttKtDdddcKPKB0KKKDKKKGtDQttttKtKKKKDGttdGKKKKDDKKK..........", // 30
        "..KDDBKStDDttttKKKKBBKKKKttcrSdStKtKKKDMKttcDKDAKKKS............", // 31
        "KKKKKKKtdtDtSdtDKBMMKKKKKKDSDSddStKKKNDMMKtKPKttKPKS............", // 32
        "..KK0BKDcStdSdScKDKKKKKKKKStSPdddDKKNDDDDKKKKEKDDrKDNK...KD.....", // 33
        ".S0KKBKKKDttStddtKKKtKKKKKQDdDSddcGKQNDKKKKPBKEKrKDrN...KDD.....", // 34
        "..DK0K0KKBBSDStdtKDBKBKKKKAKSdPddcKKCKKKKKKNSKENQKtDNKKDrD......", // 35
        "...KDDKKSKBBDKKttKDMBKKKKKrDtdDddSKEQKKKKKPBDDANQKtDNKDCK.......", // 36
        "......DKKKDSMKKKKDBMBMKDKKDPDSSPdcDQAKKKKBKSDDtttGKDNrPDKN......", // 37
        "......DttKK0IDKD.KBBMBDAKDNPPttCdtPEQKEKKKKBNttSStKDQPDCKN......", // 38
        ".....DtStGKDSmDKK.DKDDKSKDNQDGtrttPQAPKKB00KKKdSSttcDDDK........", // 39
        "....DttKKK.KKKMIIKKKKKAD4KNQ1GtCDtPEDDDPKBKtDPKddtdtDGB0D.......", // 40
        "...KGtKDS.....DDDSKKKKDDKQKPPttrDtSPKtDQK0KtcQDSSDSBB0KD........", // 41
        "..DttKK..........KKKKKKKEtDtdStDtSdttSGDAKKtdtDtKDKKKKAK........", // 42
        "..KttGKGK.......KKKtKKNDK1ttSdStcddtDDDPKKKGdStSDKKKKDAS.K......", // 43
        "..KttttDAK.....DKKttKtKKKDKttddSddttKDDKKKKtdddDKKKKKKDDDCS.....", // 44
        ".SKttDDDDK..KKKKKtttttKKBBKKdSddddSKKKKKKKKDSdSKKKKtGtKDDPD.....", // 45
        "KGKtDD..SK.DKKKKtttttKK00KGKKSdtdSGKtKBKKKKDdtKKKKKDttKtKDDK....", // 46
        "KDStKKKK.KKKKKKGttttGKB0KBGKlDtttD1KtKK0KtKttDDNNDKKKKGtKCtKK...", // 47
        "..DtDKCKSKtKKKKtttSDK0K00BBDDGKtKKDtKKK00DtKND.DK.KSKKDDDDttK...", // 48
        "...NKrN.SKKKKKKDtttDGKK0K0KDdttdttdDKBKK0DtKDDKASN.SKKADDctttN..", // 49
        "....KK..KtKD.KKKSdtDtKD0K0KKtttdtttKKBKKBSKKGtDDrCDCDrDSttSttK..", // 50
        ".......KDGK..KKtSttBKKBBK0BKKtStdtKBBBGttSKKGtdtttdtStSddtdctK..", // 51
        ".......KGKK..KKtttGKKKBBBKBKKtcdttKKBDttctKKDtddddddddddddttKK..", // 52
        ".......KKK...DKDtKKKKDBBKBBKDttdtt0KBDKtttKKttdddddddddddStBN...", // 53
        ".......KBK....KKKKK...BKK0SDDKtKtK0KKKtdGKKDBttcddddcdctttBK....", // 54
        "........KK............KKK0S.KDKKKKKKDNrKKKBBMMBtSdtdcdttBIDK....", // 55
        "........SKK......DDDD.KD0D....KKKKDtSSDtKDMMIIIIIIISIIIMKK......", // 56
        "........KKKDK..SKKD.DNKMK......KKGttttKNNDKIIIMIIMIIMDKN........", // 57
        ".........KKKKKKKKK.....DK.....StKKKGKSK..KKKKKKKKKKKKK..........", // 58
        "...........DNKNS.............KDKGtKKD...........................", // 59
        ".............................KSKDttK............................", // 60
        ".............................DSDANN.............................", // 61
        ".............................KDKDNK.............................", // 62
        ".............................KKKKS.............................." // 63
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
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
        "................................................................", // 0
        "................................................................", // 1
        ".................TTTT.............TT.....TTTT...................", // 2
        "...............DTLND....DDD.S.....ST....EEDEDN..................", // 3
        ".............SDKDDT..TSSSD.TmMSS..TT...DDNNlEDDST...............", // 4
        "............DFDKK...D.DKS....SIMK.DD...D.SKKKNSTDD..............", // 5
        "...........DPDNNT...DS..ST..DDKT..TND.....NKKNNNDSD.............", // 6
        "..........DSNNNNT...SN..STDDTN...DDSD.....NKNKNNKKFN............", // 7
        ".........DQKKKKNN...SNT..KDNDSSTEK.SSD....DKNKKDNKKES...........", // 8
        "........TPDKKKKNN...DSNTTDNQSDSNNDTSSD...SDKKKKNDKKNES..........", // 9
        "........QDKKKKNKDT..KDTSNKSNNACNNNNTN....SDKKNKKNNKKNES.........", // 10
        ".......DEKKKNKNKDN...NDSSS1APQQ1ANSDD....DNKKDKKNDKNKDPT........", // 11
        "......DFKKKKNKDKKDS...KDNPD1AAAAPPDK....DDKKNDKNNDNNNNPD........", // 12
        ".....TPDKNNNNNSDDNDS...SKNDAAP1ADDND...TDNKNNNNNNNDNNNNES.......", // 13
        ".....DQKNNNNKNKNTFKK.....NPNASKDTNN...KNDKKNNKNNNNSNNNNSDT......", // 14
        ".....SDKNNNDKNNDNNDTDSPSSSNNAPCQNKDDDNDANKNDDNNNNNDNNNNNQD......", // 15
        "....DEKNNNNDNNNKDKKKTQCQ1ASN1NSKSNN1ADLQ1DSSNNNNDDNDNDNNSN......", // 16
        "....DNNNNNDNNNNKSNKNKF1NCPDDDNKKNNDANDLDQSKDNNNDNDNSNDDDD1T.....", // 17
        "....SNNNNNDNNNDKKDKN1DSLNDNTDNSDNKDKNDDSKKKNDNNDNDDSNDDDN4N.....", // 18
        "...NSNNDNNDNNNDKKKNPNNKDDKKTSDPANSKNDDKKDPDDDNNDNNDSDNDDDlD.....", // 19
        "...KDNDDNNLNDDPKKNKD11NSDNDNSSNNSSDNKKKNNAANDNNNDDDDDNDDDLDS....", // 20
        "...NNNNDNNLNDDSKNKKNNQDKSSNPDSSSDTPPKKN11A11NDNNDDDDSNDDDDSS....", // 21
        "...DNNNDDDLNNDLNKNDAADDNDAQ1DSPDTSCAKK1Q4QAQCNKDDDDDSNDDDDSD....", // 22
        "...SNNDNDDLNDNSND141NNDKKP14SSDKDSPKKKN111AN4DNNDDLLSNDLLLLD....", // 23
        "..DPNNDDDDLNDSPQ11Q4QQNKNNNNDNDSSNKKKNKKNNKSTDNNDLLLSDDLLLDSD...", // 24
        "..KDNNDDLDDNDKA1Q411Q1E1NNDNKKKDNNNDKKKKKK1QDNDNLLlLDDDllLDSD...", // 25
        "..KKDDDLLDDNDN1NA1PAA1Q4QNNNKKKKKKPNKNKNAAAANDKNGDlLLSDllLDLN...", // 26
        "..KKDDLDDDDNDNNNDDNDNN14P1lPNDKKNDNKDS.NQ1NNDNNDDGLLLSDLlSLDD...", // 27
        ".TDNDLLLLNDNDLDSKKKKKNAAQ14NNKNKKKNS..KN4NNNPNDKPKSlLLDDSlLDD...", // 28
        ".TDNDDLLDSDNDLLPKKDTDSSD1PDSKKAQAKNS..K1PN1NNKSDLLSSLlDDSSlDDD..", // 29
        ".TDNDLDD..NNDLDDKKTD.DNNKSNSKNPPNNKT...NKPANKNKDNNDSlSNS.DlLDN..", // 30
        ".TDDDLLD..NDDSNNNDSTDQQANNKNNKDDAKK....KNNDN.DSS..SSSSN..TLLLN..", // 31
        ".TNDLLLD..NDDD.DDK.TNl1ADNNDDKDNNKKT...SDDNK.D.T...DlSD...DLDN..", // 32
        ".TNDLLLD..DDND..DNTDDDDDADDNKNNA4KDNS.D..KKD.D......LLS...SLDK..", // 33
        ".TNDLlD...SDNS...DSSImIDAPDNKKNNNNANNDTDDTKi..T.....DLS...SLDN..", // 34
        ".TNPLlD....DD.....SDimiMNPNNKKKNKNAPAPNKKSDm.IB.S...DDS...SDND..", // 35
        ".TNPLLD....DD.....SNIiKINANDKKKKKNN1PP1DKDDDNDDT0...SD....TDDS..", // 36
        "..KPLlS....DD.....DDKiMIKNNNKKKKKKN11PDNNNAlSPNKM....N.....DDS..", // 37
        "..KDLLS....TN.....1DIMIBKNKKKKNKKKA1QEQDDNPPSPQPNSIB.N.....NDD..", // 38
        "..KDLlS.....N.....1ADiMNKNNKKKKKNN1311FSNKDAE11PSNK..S.....NDD..", // 39
        "..TNDLS.....S.....AQPSDPDDSKKKNNNNA1PP1QPNKDPP1SDDN..S.....SSD..", // 40
        "...NLlS.....T.....DQSFl1DNDK.DNNNKNAADQSDDKDDNDPDDNT.T.....SDD..", // 41
        "...NLlS.....T.....NPQPlQDNDK..SNNNKNNNTmISKD..DDDKDN.......SDD..", // 42
        "...KLlS...........DPP44EPPN.......TSKDSSmDDNN..DDDNK.......SDD..", // 43
        "...KDLS...........SN11Q1NDKD.........KND0MDNPN.DDKNK.......SND..", // 44
        "...KDlS...........TNA44QDDKND..TS....DNDIIDDNKTDDKNS.......TND..", // 45
        "....NLS............NNQ3QDPSPDNTD.....TPNKKNDDKDDDKNT........NN..", // 46
        "....KLD.........TSPNN1QQDDSllPDD.S.i..DQSlDmIDDDKNK.........NN..", // 47
        "....KDD........DPSDDDPQQDNDDDD1PNNTM..DDQSSmDMKKNDD.........NN..", // 48
        "....DDD.......DPNMKKPNEQDNKIBNDNDDDNDK.DPDS0QNKNNN..........NN..", // 49
        ".....DDT.....TDKMD.KNPPPDNKDIBMBNNNNDNKKNNDDPPNNNS..........NN..", // 50
        ".....NDD....TDDK..SNNNFQNAN...KMMDNNDDPDDKDPPPDK............TD..", // 51
        ".....DDD....NNK.TSPE11QQDNN.....SD0DDDNNNKNDSAND............TT..", // 52
        "......ND...SKK..DNQ4PPNKPKN........SKKKKKKSDNPAN............T...", // 53
        "......SD...SKT..PAAADNSTNKD..............SSNPA1DD...........T...", // 54
        "......TN...KK..KQNKSSDITKKNN............SKKDENQNNK..........T...", // 55
        ".......TN..KD..N11DDiBMmMKKT...............TTKPN1NNDT...........", // 56
        "........D..KS..NQDNDSIDIIKm..................S1AAQNPS...........", // 57
        "...........K....PTSD.KM.KTm...................DlKDSSD...........", // 58
        "...........D....SII0..DTT.S...................TDTBiImS..........", // 59
        "..................SKK.....D.....................DI0SIT..........", // 60
        "................................................SmKBT...........", // 61
        ".................................................K..............", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFF141414),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawWildRat() {
    val matrix = arrayOf(
        "................................", // 0
        "...Q33Q.........................", // 1
        "..QRKKCE..........KKK...........", // 2
        ".QK...KAQ........KCCCNK.........", // 3
        ".O.....NP.....KKKCAQPQCK........", // 4
        ".R.....KX....KEEFNNAPPPCK.......", // 5
        ".KZ....KX...KQANNEENAPEAK.......", // 6
        "..K....KQ...KANCCNAENNAPNKK.....", // 7
        "..K...NQN...KCCXXCNAQAPEFECN....", // 8
        "......KEK...KCXXXXCNAQANKAQQNNK.", // 9
        ".....KEK.....KCXXCNCQANK3NAPEEQK", // 10
        "....KQPK......KKANACAPEEPCAXAXNK", // 11
        "...AOQK.........KACAPQPCNXNWNWN.", // 12
        "...KQOK.......NAPANPAPNCNFNFNFA.", // 13
        "..AAQN......NCQQPAACPCNQNPAQAP..", // 14
        "..KQPK....DAPQPPCAACPANCAAWWWA..", // 15
        "..KEPK...DNPCPPCPAACCNPACNWWQW..", // 16
        ".AAQCK..DNACPCCPCAACCNQCPAQDEA..", // 17
        ".NAQCN..KPACCCCCAAACCAPAEAFAFN..", // 18
        ".KAOORSDPAAAPPCAAAAPPAPAPAQPPN..", // 19
        ".KROJCAKPNCAQPPAAPQQPAANNNDDA...", // 20
        ".KNZOPNNAAANQPPNCQQQANAN........", // 21
        "..KRCPKAACANPPANPQPANNAA........", // 22
        "...KAAKAAAPAQPNKPPANSNAANPNDDT..", // 23
        "....NKNAAAQAEQDDDNNN....AQAPPN..", // 24
        ".....KPAAAQASPEQSDN.....SPSSQN..", // 25
        ".....NPPCAPANKPWKEKTTT...A..PD..", // 26
        ".....AQNPPANKKDQNPNDNNT..D.TD...", // 27
        "....KCENNNKK.KQKPKNPAADT...D....", // 28
        "....KPFCNNKK.......KQKPD........", // 29
        "....NKPFPQCK...................", // 30
        "......KAFKEK...................." // 31
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawDarkKnight() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        "................................................................", // 2
        "................................................................", // 3
        ".......................................DK...KDS..D.SD...........", // 4
        ".......Q....................S.........SKSDDSSKKSSKDS............", // 5
        ".......QF...................SSK.K...KSKSDD0SSDDD0DSSSSSK........", // 6
        ".......QE..................KmDDKSSK.S0KDKD0SDKKDDKSSDDDDK.......", // 7
        ".......QQQ................KDiDKKiSK.SDSKKKDKKNKKKKKDSSDDD.......", // 8
        ".......CQEP...............KDSNKKSDK.DDDKKKKKNCKD0SSDDKKKK.......", // 9
        ".......PQEPF.............DISKKKD0DKKDDKDSSDKKKKKKDDDDDGKK.......", // 10
        "........SAFQK...........DKSDKKKKKDDKSDKKDDSSSSSDDDDDKKKD........", // 11
        "........SCEQPP..........DSDKKDDSDKKKKDDKKDDSSSDDGDKKDK..........", // 12
        ".........KPQQP..........KIDKDDDDKKKSDKKKKKDDDDKKKDKKD...........", // 13
        "..........NQQQK.........KDDDDNNNNKKSDDDKKKKKDDKKKKKDK...........", // 14
        "..........DPQFAK........DKDDKNAPDNKDDDSKKKKKKKKKKKKS............", // 15
        "...........SPEAK.........DKKDSmiiS0.DKKKKKKKKKKKKKKS..SS........", // 16
        "...........SAQQCP..SKDSSKKKKDKD0DDKKKKKKKKKKDDKKKKDDDSKKDSDD.SS.", // 17
        "...........SNAEDNQCD0SKiKSSDDKKKKKKD0DKKKKKKKDDKKKKDSK0DSKSSDK..", // 18
        "............KNAQEFNSKKKD00DKKKKKKKKKKKKKKKKKKDSDKKKKKKDDKDDD....", // 19
        "............KNQQQDNS.KKKDKDDDSSSKDSSDDKKKKDKKDSDDKKDDDSSDKKK....", // 20
        ".............NQPAAKDSDDDDKDDISSSKDSDDDKKKKDKKKS0DKKKDDSDD.......", // 21
        ".............PPNKDKKKDSSDKDSS0DDKDDKKDDKKKDDKKSSDKKKKKKDDK......", // 22
        "..............DKKDDKKKDDKKDDDDKKKKKKKDKKKKDDDKD00KKKDDDSSDK.....", // 23
        "...............SDSiSKDKKKKKKKSSSKKKDDKKKKKDDDKKDSDKSKKKKK.......", // 24
        "...............SKKKKD0DKKKKDKDDKKKKKKKKKKKKDDKKDDDKKD...........", // 25
        "...............SKDSDKKSKKNKKKKD0DDKKKKKKKKKDDDKKDDKKKDK.........", // 26
        "................SDDDDKK..KDKKDB0DKKKKKKKKKKDDDKKKGKKKKK.........", // 27
        ".........KSKK....SDKDKDSSSDKKKDDKKKKDDSKKDKDDDDKKKKKKKDK........", // 28
        "........KS0DS.....DKKNNNNKKKKKKKDDDKSSKKDDKKDDDDKKKKKKKD........", // 29
        ".......SdKKSSD....DKKANNQNNKKKKDSSSKD0SKDDDKKDDDDDKKKDKD........", // 30
        "......DSdKSDDSKKKKKKNPEEANNDDSSSDDDKKKKKKKDDDKD0SDKKKDKD........", // 31
        "......DD0SDKDDKKKDKSFFQPNNND00DKKKKKDDKKKDDS0DKDSSDDKDKD.DDDD...", // 32
        ".....KKKDDDKKKDKKSSDKKACFANKDDKKKKDDSDKKKKDDDSDDDSDDKKKKKD0SDDS.", // 33
        "....KS0SSS0DKKKKKSDDDKKNQPANKKKKKKDDmSKKDDDD0S0DDSDDKKKKKD0S0DS.", // 34
        "...KSSSKdS0DKKKKDSDDDDKNPQPNKKKKKKKDmSKKDDDDD0SDD0DKKKKKDDDDDKS.", // 35
        "...DSSKKS0DDDKKDDSDDDDGKAEPAKKKKKKKKSSKKDDDDDDSDKDKKKKKKDKKKDKS.", // 36
        "...D0SKSDDDKKKKDDDDDKDDKKPPPKKKKKKKKDDKKKKDDDKDKKKKKKKDDKKKKKDS.", // 37
        "...DDDSSDDDKKKKKKDSDKDDDKKAQANDDKKKKDKKKKKKDDKKKKKKKKDKKKKDDKK..", // 38
        "....DKKDSKSDDK.SKKSDKKDDKKNAQDDDDGKKDS.SDKDS0DKKKKKKKKKKKKDSKK..", // 39
        "....SDDKKKDDKK..KKDDSKDDDKKNQADDKKKDKKKKKKDSSSKKKKKKKKSKKKDSK...", // 40
        ".....SDDD0SDDKDKKKKDSKDDDKKKCPAKKKKDDKKKKKD0SSKKKKKSKKSDKKDDK...", // 41
        "......KKDDKSDDKKKKKDDDDDDKKKNPPNKKKKKKKKKKKD0SKSSSS..SDKKDDDK...", // 42
        "........SDKSSDKSSKKDDDDDDKKKKCCAKKKKDDDDDDKDDSKK....SKKKKDDDD...", // 43
        "........SGKKSDKKKKKDDDDDKKKKKNNAKKKKK....KKDDSKK...SKKDDKKKD....", // 44
        "........SKSDKKKDDSS0DKKKKKKKKKKNKS.......KKDDSKK...SKSKKKKS.....", // 45
        "..........DKD.KKKDKKKKKKKKDDDKKKKS.......KKDDSDKD..SKK0DK.......", // 46
        "..........DK..KDDDKKKKKKKDKKKKKK.........KKKDSSDD...KKKD........", // 47
        "........KS....KD0DKSKKDKKKKKKKKK.........KKKDDDSK....KK.........", // 48
        ".........K....KDSDKS..KSKKKKS.............SDKDKDK...............", // 49
        ".............KKDDKKS....DKKKKK.............DKKGKD...............", // 50
        "............KKKDDKDS....KDKKKK.............DKKDKD...............", // 51
        "............KKKDDK......KKDDKKS............DKKSDD...............", // 52
        "...........DKKDDDK......KKDSiDS............DKKSDD...............", // 53
        "............KKKDDK.......DKKKK...........KNKKDDDKK..............", // 54
        "............KKKKKK.......................KKKKKDDDK..............", // 55
        "...........KDKDDDK........................KDKKDDKK..............", // 56
        "...........DKKDSSDS........................DKKDDDKS.............", // 57
        "...........DKD0SKSKS.......................DKDKS0KKS............", // 58
        "...........DKKKDDKGS.......................DKKSKKSKS............", // 59
        "..............KKKK..........................KKKKKK..............", // 60
        "................................................................", // 61
        "................................................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawLamia() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        ".......................KKKKKK..........KK.......................", // 2
        "......................KQSSSSDK........KQmK......................", // 3
        ".......................NDDDKDID...DDDDKNKDD....SSSSS............", // 4
        ".......................KKKKKDDDS..NNDDKKKDDD...PAAAN............", // 5
        "......................KKNNNKKKDDKKPPPPDKKKDIK.NPPPPAN...........", // 6
        ".................NNNNKKAAAAQPNKDmDKKDPQBDKDDKKPANKKNKNS.........", // 7
        "..........PPPAAAPPPAAFQKNNNPNANNKDSKKKKQSSDKKANNKKNPQQQS........", // 8
        "........SSNDAAPCPANNNPAAAANNAPCANKDDDDPEDSIDKAAPPPPPAAPPQ.......", // 9
        ".......SPDDDDAAAANNNNDNPPPADAPPCAKDSDDPSSSIIKACPPQPANNDAP.......", // 10
        "......PANK..DNNNKKKKKKNNPPQQQQPPPNFQANKKFDIIKQNNAPANKKKKAP......", // 11
        ".....PAND..ANCPPPAEEQAPPANNEEPANAFAPPPPKKQDDKPQPCNKKAANNKKA...N.", // 12
        ".....ANS.APPPAAAANAAAANPQQQPNNQSAAFPPPQPAKDDKKFNDPQPANKD..KKND..", // 13
        "....SNSSSCANNNNNNNAACPPQPPCACPPPAAPQQPPNNNNNNKFNNNPPAPPSS.......", // 14
        "....AN.SAPNDKKKKNNAAAPPQPAAAPPPAAAAQQPAKDDDDNNEDNNNCPPPPA.......", // 15
        "....AKNAPAK.DKNNPPANNNNNNAPEPNNAPENNAPQD.DSSKEPPANKNNNNAEN......", // 16
        "....NKPANNKKDCPPNNKKKPPEEQPNAKPQPNAQCAPQWWWFQKKNAPEEFFEPNPA.....", // 17
        "....NNPANKNPANNNKKNS.PAANKKKNPPAAKKQDKKSWFF.KKAANKKKNDPQPNA.....", // 18
        "....NAANNNPANNNNKNSSPANNDSSSNQPANDKPDKDD.FSDDKANDSSKS.SPPNNS....", // 19
        "....NPNNNAPNNNANNAQQPNNNS...NQADDSKPNDSK..SDSKNDS..D...APNDDS...", // 20
        "....PAKNAPKNPANNNEPANNKS.FFWNEDSWSKPNKDSWFSDDKK.WWWWK..NPN.DND..", // 21
        "...NPAKNNKPPANKNNEANNKWWWWWWNPNSFWKNS.WWFW.SDKD.F.D.K.SKAK...D..", // 22
        ".SAPPAKKNPANKDSDPPANKSWFFWWWNPNSWF.NSSSWW.SSKKWFF.KDKKNNK.......", // 23
        ".SAAPNKNAANSSS.SPANDD.W..WFWKANSFWECDDSSSSSDKDWF.SKNSDKDS.......", // 24
        ".SANPNNACNN.FF.SPANDS.FS..FWKAND.FQQDDSDDSDKD.W..SKK.DDD........", // 25
        "PANNPNPPCNSWWF.DPAKD.S.DS.WW.AAS.QFCDDK..KDSWWWDNKKKS.SD........", // 26
        "PANNANPANKWWW.SDPAKDDKKDDSS.DKNNKQAFW.DKKS.WWWN.WW.KKS..K.......", // 27
        "ANNNANPDNDWW.SDDPAKKDDDDDDDKKKNNKPSFFF.S..F.SKSS.FFSKKDS.K......", // 28
        "NSSNAAANKD.W.SDDAAND0000DDDKKKNNKDD..FF.FF..DKDDSS.SKKDD.SS.....", // 29
        "N..DNPNNNDSF.SDDNPD0SSSS0DDDDKNNKKKS.FFFF..SKDKKDDSSKKKDS.K.....", // 30
        "N...NPKKK.DSSSSDKPD0SSS..dS0DDKKDDDKS..F.DKKDKd.GKKKKKS.K.K.....", // 31
        ".SD.NPKKDDDSVd.SDPDD0SSS.dS.00DD0dSdDN..KSSd...SSSDK.DS..K.DS...", // 32
        "....NPKKNKDD.SSSDPDD00SSSSSSSSDDdS.SS0NNSdSS..SddSSK.DND..DKS...", // 33
        "...SACKNNKDDD.00DPDDD0II000SSS0SSSdSSSDDSSSSdSd.dSSDSSDD..SDS...", // 34
        "...NPNKNNKKDDDDDKPDDD000DDD0SSSSSSSSSSSSSSSSSS..SSS0K..D...DS...", // 35
        "...NPKKNNNKKKKDDKADDDDDDDDDDDDDDDDDDDDDK00DDSSSSSSS0K...K.......", // 36
        ".SNAAK.SNAKKDKKKNAKGDDDDDDDDDKKKKKKKKKKDDDDDDDSSSS0DK...........", // 37
        ".SNPAK.SNPKDDDDKAKKKKKKKKKKKKKKDDDDDDKKKKKKKDDDDDDDDKDS.........", // 38
        ".SDPNK.SNPKDDDDKAKDDDKKKKKGGKKKKKKDDDDKKKKKGDDDDDDDKKGD.........", // 39
        ".SAPNK.SNPKDDDKKAKD00DDDDDDDKKKKKKDDDDDDDDDDDDDDDDDKKDDD........", // 40
        ".SNNNK.SNNKD00DKAKS0000SSSSddDDDDDKKKKKKKKKKDDDDDDKKDDDDK.......", // 41
        ".SNAKNKNNAKDDDDKNKSd.S..dSSSSS000SSSSSS0DDKKKKKKKKDDDDDDK.......", // 42
        ".SNAKNKSSNNKDDDKNKDDDDDS000SSdSSdS0SSddS00DDDSSSSSSDDDDDK.......", // 43
        ".DNDDNNSSNAKKKKKNKDDDDDS0DD00SSSdS0ISSdd0SSS0dSSSSS.DDDDK.......", // 44
        "NNNN.NANKNAKKKKKNKDDDDDDDDDDDDSSS00000S.S..dS..SSSSSDDDDK.......", // 45
        "NNS..NAND.NAKKKKKKKKKKKKDDDDDDDDDDDDD00DDSSSSSS0000KDDDD........", // 46
        "N....NNKD.NAKKKKKDDDDDDDKKKKKKKKKDDDDDDDDDDDD000DDDDDKS.........", // 47
        "N...DNNSSDNNKKKKKDD00SSS00000DDDDKKKKKKKKDDDDDDDDDKKKS..........", // 48
        "S...NND..NANKKKDDD00SSSSSSSSS....DDKKKKKKDDDDKKKKKKKK...........", // 49
        "...NAK...NAKKKDD0S0DDDDD0000SSSSSSd00DDDDKKKKKKKKKDDK...KKK.....", // 50
        "...NN..SNAKKKGD0SDDDDKKKKKKKK000DDddSSSSd0000DDDDDDDK.SKDddKS...", // 51
        "...NK.DNNNKKDD.SSDDDD........KKKKKKDD.SSSSSSDD0DDDDK.DDDSKKSDD..", // 52
        "...K..DNNKKKDD0SdDDS...............DKGDDDD.DDDDDDKK..DDS.K.DS...", // 53
        "...K..NNNKDKDD0Sd.DS...............DKKKKKDDDDDDDDDD..DDS.K.SS...", // 54
        "...K..KKNK.KDD0S.S.GKK..............DKKKKKKKKKKKS....DDS.K......", // 55
        "....K.KKD...KGDDS.ddSDKK.........KKKKDD00SDKK......KKSd.DK......", // 56
        "......NS.....SKDD0SS.SSDGKKKKKKKKDDSSSSS..SSSKKKKKKD0S0DK.......", // 57
        ".....SS.......SGKDD0SSd0DDDDDDDDDSSSSSDDSSSSSDSSSDD0IDDD........", // 58
        ".....D.........DDDDD0S.SSSSSSSSSSSdSS0DDDD0SSSSSSSSSSDDD........", // 59
        ".................KDDDDddSS00dS000KDDDKKKKKKKDDDDDDDDKKS.........", // 60
        "..................DKKKKDDDDDDDDDDDDKKD......KKKKKKKK............", // 61
        ".......................KKKKKKKKKKKK.............................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawAdamantoise() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        "................................................................", // 2
        "...........................T....................................", // 3
        "...........................KT...................................", // 4
        "...........................KT..........K........................", // 5
        "...........................DT........TTK........................", // 6
        "...........................d.........DDK........................", // 7
        "...........................TDN.......S0K........................", // 8
        "...........................TDN......TSSK........................", // 9
        "............................DN......KddK..........TKKKKKD.......", // 10
        ".........................Td.S0......KTTK......DNNNDQFFFEQK......", // 11
        ".................T.......dd.dST...TTDTTK....TTDDDDPQEEEEQDT.....", // 12
        "................TKT......S0TdVKT..DDTddK...PCKQQ4FFEQQEFF4KD....", // 13
        "................SDdUD....StTT.KT..DD.TdKNDDNNQNNKAPQPPEEEQPCN...", // 14
        "................TSSSS....SDTT.KT.TSS.SSKDDANNQNDSDAPQQEQQQPCK...", // 15
        "..................KS.....DKT..0T.KdTTDKKQEENNJNDTDNAEEEQQQPCK...", // 16
        "..................KSTDT..DKTTTdU0KTTKPPFQQKPQEPPPQQENNNNAQPCK...", // 17
        "..................KSdST..DNddTd0DDSSNQQEQPKQQEQQQPPQNNNNNPPCK...", // 18
        "..................tcd....DKddTdDNTDKQFFQPPKQEFFF4PCKNNKKKKPCK...", // 19
        "...................SKTSD.DKtcdUDD.QPFPPAPPPQQEQQCNNNNNK...ND....", // 20
        "............T......TKTStTDKG0V0DDTQQEPPACCPQQQQPANNNNND...DS....", // 21
        "............NT.....TKdVUKKKKKG0dTKQFOCAKNAQQQOCAKNNAND..........", // 22
        "............DSTDT..TKKKKd0Ddc0KDDPQEAANKNNPPPCNNNNNK............", // 23
        "............DSd0ST.SKDDDd0tdcUKDDQQQAANKNNPPPANAANNK...T........", // 24
        ".............SKTSt.SK0dTTS0TddG0SFQPAAAKKNNAAKPJENKK...NS.......", // 25
        ".............SDdDKN0dTdd0dTTdd0DN4PCAAAANKKNNNNAPPCKNNNNS.......", // 26
        ".............TDSDDDSdddd0ddddVDDAEPCNAAANNNNNNNAPCCNDDNNS.......", // 27
        "...............KD0TTTVdT0Vddc0KCPQPCKAAACCONNANNNNAQFEJNS.......", // 28
        "...............K0dTdd000dU0tttKQFPAAANNKKKKKKKKKKKKKKKN.........", // 29
        "..............TDSdddV0USdSS0ttNQEPAAAANNNNNKKKKKKKKDNNNT........", // 30
        ".............SKddTt00KST...TdUOQEAAAPAACCCNNKKKKKDtcKKKKD.......", // 31
        "............ND00tG...Td0DtDUDKQQQANNAAACANKKKKKKKKNADt0VSNNNN...", // 32
        "...........TKDSSStTTTSSDDDD0DNEQQCNNANACNNKKKKKKKKNADDDc0DDNN...", // 33
        "..........SKGS...T0tGACCOCAKCPEQPCAAKKKKKKKKKKKKKKKKNNCGtctDK...", // 34
        "........NdTTdd0DDACAANNNNNNPQQEPCACCAAACNNKKKKKKKKKKKKNDGKKD....", // 35
        "........DSd.dSDDNAAANKNNKNNPPQEPCAACAAACNNKKKKKKKKKKKKKGGKDS....", // 36
        ".........KST0DAOANKKKKNNKKKCPPQPCAAAAAAPNKKKKKKKKKKKKKKKKK......", // 37
        "......NDNdDNtNANNNKKKKNKNNKNACPANKAAAAAKNNKKKKNNNNKKKNNNKKDD....", // 38
        "...T..DDDVDKDNANNNNNNNNKNNNNAAPANNNAAAANNNKKKNAAANNKKNNKKKDS....", // 39
        "...KDTTdd0ttNKKNAAAAANNKANKANNCAANNKKKKCCCKKNNPPJANKKKKKKK......", // 40
        "....SKDNNtGKKNAPPQEQQAANKNNKNNNAAAAAAAAACCKKNNPPQPCNNKKND.......", // 41
        "...TSKKKKDKKNACPQQEQQCANKNNKNNNAAAAAAAAAAAKKNNPPQPPANNKNDT......", // 42
        "...KKKKKKKKKNCPEEEFEEQCNKKKKNNKNNAANAAAPNKKKNNPPQQQOANKKKK......", // 43
        "....SKKKKKKKNCQQQEEQQPCAKKNNNKKNNKNNNNNANKKKKKNAPQEEANK.........", // 44
        "...TSKKKKKKKNCQQQEEQQPCAKKKNNKNNNKNNNNNANKKKKKNAPQQEAND.........", // 45
        "...KKKKKKKKKKAPPQQQPPAACKKKKKKANKKKKANNANKKNKKNAAPPQNA..........", // 46
        "....SKKKKKKKKNNPPPEQPPCCKKKKKKNKNNNKNKKNKKKDDNNNPQQPPPK.........", // 47
        "....TDDDKKKKKNNPPPEQPPPCKNDDKKKKKNNKKKKKDDDDGKKNCQQPPPKT........", // 48
        ".......SKKKKKKKPQQEQPPQQK0UUDKKKKKKKKKKK0ccDNKKNNQQ4QPKKD.......", // 49
        "..........SKKKKCPQPQQPPQKKKKDD000DDDK00tKKKKKKKNNPPPCPQAANND....", // 50
        "..........SDKKKCPQPPPPPPKNKKGD000GDGKDDGKNKKKKKNNCPPPPPCANDDT...", // 51
        "............KKKOQEPCAANNKKKKKKKKKNKKKKKNKKKKKKKKKNAOEQNPPCdVK...", // 52
        "............KKKQPPQQQQPANNKKKKKKKKKKKKKKKKKKKKKKKNNNNDTQQdDNK...", // 53
        "............DDKPPPQQEQPCANNKKKKKKKKKKKKKKKKKKKKKKKKKNDdPSVNNK...", // 54
        ".............SKKAQPEFEQP4ANKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK...", // 55
        ".............SKPQFACCFQCPSTKKKKKKKKKKKKKKKKKKKKKKKKKKKKKD.......", // 56
        ".............SKPQFAACFPCCSdKKKKKKKKKKKKKKKKKKDDDDDDDDDDDS.......", // 57
        ".............SKCSTKACTSCKD0KKKKKKKKKKKKKKKKKD...................", // 58
        "...............NDSKKNcDNKKDKKKK.................................", // 59
        "...............DD0KKK0DKDDDDDDD.................................", // 60
        "................SKKKKKKK........................................", // 61
        "................................................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawTonberry() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        ".............................KKKKKKKKKKKD.......................", // 2
        ".............................KKKKKKKKKKKD.......................", // 3
        ".....................KDDDDDDDGGGGGGGGGGGGDD.....................", // 4
        ".....................KKKKKKKKgLgLgLggLggGKK.....................", // 5
        "...................KKSKKKKKGGgggggggggggGGGKK...................", // 6
        "...................KKKNNKKGLgLLLggggLLgggLgKK...................", // 7
        "...................KKKNNKKGggggggggggggggggKKK..................", // 8
        ".................KKNNNKKgggggLLgLLllllllLLLgLKKD................", // 9
        ".................KKNNNKKgLgLgLgggLllllllLLggLKKD................", // 10
        "..............KKKQQANNKKgLgLgLLggLllllllLllgLKKD................", // 11
        "..............KKKQJANNKKgLgLgLLgLLllllllLllLLKKD................", // 12
        "..............KKKQQNNNKKggggggggggggll1lLllLLllDDD..............", // 13
        "..............KKK44ANNKKgLgLggggggLgllllLlllll41KK..............", // 14
        "............KSDAAllNNNKKGGGgggggggggLLLLD1L11PQAKKSS............", // 15
        "............KKNJJ44NNNKKKKGLggLgLgLgLgggLllllPJAKKKKK...........", // 16
        "............KKKPPllNNNKKKKGgGggggggggggggLL1LPPAKKKKK...........", // 17
        "............KKKJQ4434QNNKKKKKggggggg4444LLgllgLGKKNNKKK.........", // 18
        "............KKKJJ4434lNNKKKKKggggggg4444LLgllgLGKKNNKKK.........", // 19
        "............KKNJJ4434l4444PJPKKKKGgg4444LggllggGKKNNNNNKK.......", // 20
        "............KKKJJ443434444PJPKKKKGLg4444lggllgLGKKNNNNNKK.......", // 21
        "............KKKAAQJPJPQQQJPQPPPNNNKKLLgLLlLllKKKKKKKDggggKKK....", // 22
        "............KKKNNQJPJPJJJJPJPJJNNKKKLLgLLllllKKKKKKKgLgLgKKK....", // 23
        "............SSDKNAAAAAAAAAAAAAAPCANNGGGGGNNNNKKKNNKKDLLggDDDDD..", // 24
        "..............KKKNNNNNNNNNNNNNNJJANNKKKKKKKKKKKKNNKK1lLLgllKKK..", // 25
        "..........KKKKSKKNNNNNAAAANANAAPPAANKKKKKKKKKKKKAAKKDLLGGLLKKK..", // 26
        "..........KKKKKKKKKKKK4444l4QJJNNCJJJJANNKKKKNNAJQKKgggKKLLKKK..", // 27
        "..........KKKKKKKKKKKK4444l4QJJNNCJPQJNNNKKKKNNAJQKKGLgKKggKKK..", // 28
        "..........KKgLGKKKKPJPNNNNNNNNNNNCJJJJ44l444434QJQNNKKKKKKKK....", // 29
        "..........KKggGKKKKPJPNNNNNNNNNNNCJJJJ44l444434QJQNNKKKiKKKK....", // 30
        ".....KKKKKKKgLGKKQQANA4444l4QQQNNNNNNNPJQ4444l41NNKKSKmKKKKKKK..", // 31
        ".....KKKKKKKgLGKKQJANA4444l4QJJANNNNNNPJP4444341NNKKSKVdKdKKKK..", // 32
        "...DDDGGgGgGgggggAAPQ1llNNNNNNNPPANNPPAAAAAAAAANKKKKSVSKKmVKKK..", // 33
        "...KKgggLgLggLgLgNNQ4l44KKKKKKKQJCNNJJANNNNNNNNNKKKKSKmKKKdKKK..", // 34
        "...KKgLDLLggggGDGAAlll11GGGGGGGACANNPPAAAAAAAAANKKKKSSSKKSVKKK..", // 35
        "...KKllLllLLgLgNN4434lKKLLgLgLLKKNNNJJ34l4444PJCNNKKSKmKKKKKKK..", // 36
        "...KKllLllggggGNN44l4lKKgggggggKKKNNJQQ4l4444PJCNNKKSKSKKKdKKK..", // 37
        ".....KKKKKgggggNN4434lKKLgLlLKKKKDKKNNQ4l4444PJPQQKKSKV00KKKKK..", // 38
        ".....KKKKKggggGNN4434lKKggLlLKKKKDKKNNQ4l4444PJPJQKKSKV00dKKKK..", // 39
        "..........KKgLGNNQJQ4lKKKKDlLKKKKSKmKKPQP4444PJPJQKKSKSKKKKKKK..", // 40
        "..........KKggGNNQJ34lKKKKDlLKKKKVdSKKPJP4444JJPJQKKSKmKKKiKKK..", // 41
        "..........KKKKKNNQJPQPNNKKKKK000UUVSSSAAAJQQJPQCAAKKKKKddKKKKK..", // 42
        "............KKKNNJJJJPNNKKKKK0U0UUKdKKNNAJJJJJJCNNKKKKKKKKKKKK..", // 43
        "............KKKCCCCPPCNNKKKKKSSSS0U0UUDSDCACCACACCKKNAASSKKDSS..", // 44
        "............KKNJJNNPJPNNKKKKKKKKK0U000dKDNNNNNNAJQKKPJP44KKK....", // 45
        "............KKKJPNNPPPNNKKKKKSSSS00000SdDNNNNNNAJQKKPJPQQKKK....", // 46
        "............KKKJJJJANNNNNNKKKKKKKKKSKK000U0NNNNAJJNNPJPJJKKK....", // 47
        "............KKKJJJJNNNNNNNKKKKKKKKKSKK00B00NNNNAJQNNPJPJQKKK....", // 48
        "..............KKK4434QJQNNKKKNNNNKKKNNNNNNNQJNNNKKKKKKKKK.......", // 49
        "..............KKK4434QJJNNKKKNNNNKKKNNNNNNNJJANNKKKKKKKKK.......", // 50
        "...............KKKKl4l34KKNNNQQPJNKKNNNNAQPQJKKSKKKKKKKKK.......", // 51
        ".................KKQ4Q44KKNNNJJJJAKKNNNNAJJQJKKD................", // 52
        ".................SSDNNNNKKAPCAAAANNNKNKKNNNNNSSK................", // 53
        "...................KKKKKKKAJPNNNNNNNKKKKKKKKK...................", // 54
        "...................DKNKNKKAPPNNNANANKKKDDNNNN...................", // 55
        "........................KKAJPNN44l4QKKK.........................", // 56
        "........................KKAQPNN44l4lKKK.........................", // 57
        "..........................DKKJJQJPJPKKK.........................", // 58
        "..........................SKKJJJJPJPKKK.........................", // 59
        ".............................KKKKKKK............................", // 60
        ".............................KKKKKKK............................", // 61
        ".............................KKKKKKK............................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
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
        "................................", // 0
        "................................", // 1
        ".................KK.K...........", // 2
        "...............KKDGKDK..........", // 3
        "..............KDSG0S0GK.........", // 4
        ".........DDDD.DSD00t0tK.........", // 5
        "........KtK0K.KDD0GpGpK.........", // 6
        ".......Kt0KD...KDtStSGK.........", // 7
        ".......DDKKKTKNKKKtDDK..........", // 8
        "........KKKKKDNNNGK0tK..........", // 9
        "........KKKKKDNNDGKKKK..........", // 10
        ".......KNNKKNNKNDKGNKKK..KK.....", // 11
        ".......KDNNKKKKDKGtGKNK.KtStK...", // 12
        ".......KNNKK.KNDKKGKNKKK0KK0K...", // 13
        ".......KNNK..KDNKGKKNKNKtKKK....", // 14
        "........SS..KNDKKtKKKKKKKKKK....", // 15
        "...........KNDNKNKKNKK.KKKNK....", // 16
        "..........KNDNKKDDKNNK..KKNK....", // 17
        ".........KNNNNKNDNNKNK...KNK....", // 18
        "......KNNNDNKKNNNNNKKNK...K.....", // 19
        ".....KNNNDNNKKNDNKNNKNNK........", // 20
        "....KNNKDNNKKKDDKKNNKKNK........", // 21
        "....KKNNNKKKKDANKKKNKKKNK.......", // 22
        "....KKDNNK.KKNDKKKKNNKKNKK......", // 23
        ".....DNKK.KNNNKKKKKKKKKKNK......", // 24
        "....KNKK.KNDNKKKKK.KKKDKKK......", // 25
        "....KNK.KDKKK.......KKDK........", // 26
        "....KKK.KDKKK.......KDDK........", // 27
        ".....N..KDDK.........KK.........", // 28
        ".....S..KDD.S...................", // 29
        "................................", // 30
        "................................" // 31
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawWyvern() {
    val matrix = arrayOf(
        "................................", // 0
        "................................", // 1
        "...K...........KKKK.............", // 2
        "...SK.......SS0USSS.............", // 3
        "...DS......KSSUSSK.........T....", // 4
        "...SDS...SDUUUS0ST0SST....SD....", // 5
        "....KK..SU00UUBT0BSNS0K..T0UK...", // 6
        "....KK.S00BUS0S0SBSKUD0KPDK0K...", // 7
        ".....0KU0B0U0KKDSBUK0KKUPKKKU...", // 8
        ".....DD0BBUSKK00BRB0KKPDDNKKU...", // 9
        ".....00BB0S0KKBSU0N0KBPKU0KNKK..", // 10
        "....D0BKB0UKKKKUS00BKR0URD0NKK..", // 11
        "....KBBK000KKNK0SNBKKRU0KR0NKK..", // 12
        "...D0KNK0UK.KBKK0NKKKKRRDK0KNUK.", // 13
        "...0BNK.BUK.U0BK0RNKKKKBU0UKNUK.", // 14
        "..KBN0R.KS.KRU0RB0RKKKKKKKBBKUK.", // 15
        "..KBNR0.SS.KRA0NR0BKSKKKKRBBKKU.", // 16
        ".K0KKSRK.KKK0RUBNNNN..KNUNKKNKU.", // 17
        ".K0KKRSK..BKB0RBKRR...KNKNKKNNU.", // 18
        "..K.K0RUDKRUU0KR0KKK...KNKRKKN0.", // 19
        ".....0RURB0URUKNRBKUS..KNKRKKN0.", // 20
        ".....K0RSBRBRRKKNB00D...NDKT.NB.", // 21
        ".....KBRU0RB00KKKBR0D...K..RRKK.", // 22
        "......KB00KR0BKTKKKBKK..........", // 23
        "......KKBBKBRR...KNKBUKK........", // 24
        ".......KKKKRBD...KKKBKUB........", // 25
        "........SKKRBD....K0KKPKK.......", // 26
        "............00K....BS.K.........", // 27
        "............0A0K...K............", // 28
        "............K00DE...............", // 29
        "............KKKDE...............", // 30
        "...............TK..............." // 31
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawBehemoth() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        "..........................................KK....................", // 2
        "..........................................KQNNK..KK.............", // 3
        "...................................KK.....K.KKEDKKKDKK..........", // 4
        "...................................KKKKK.KBK..KDFPDDKPD.........", // 5
        "................................K....DB0KKBG.K..DKEN.KQN........", // 6
        "................................KKDK.KKKB0KKKDK...DENK.QKK......", // 7
        ".....KKNNDK................KKKK..KB0KKB0KB0BKKKKKKKDEEKDQD......", // 8
        "...KDRRGK0BGKK.........KKK..DBBKKKK0B0cKcBKKKDKKKKBKKED.DEK.....", // 9
        "...KRRRKrKArKKK........DKKKDDtttBttttBBBBBKKKKKKKKKKKPEDDEK.....", // 10
        "....DDKZDRABttD.........KKDB0ttBtBB0tBBBKKKKBKBBBKKBKGENKED.....", // 11
        ".......K.K..KtBK.......DKBKKKKKKtBBBKKGDtDKKKKBBKKKKK0KEGD......", // 12
        ".............DcBK........KGtttttGKKKKtGGKKKBBKGKBBttKKBKD.......", // 13
        ".............KttKKKDD..Ktt0ttttt0BKttBB0GGKKKBKG0BBBtt0cD.......", // 14
        ".............Kt0KDBDKKKttttttttttcBttKKKKKKBBBKKDtDDtt0BtD......", // 15
        ".............K00tBKKKKBtttttB0tttBttKtGGt0tKKKKKKttGKKtttK......", // 16
        ".............KctttBBKK0tttt0Btt0tBtK0ttttGKKKKKKKtD0KRRKtBK.....", // 17
        ".............KDBtt0tKB0ttttc0tKBtt0tBtBtttDDKKBGtBKttDKBtt0GK...", // 18
        "..............K0BtttK0BttttttttKBtttt0tttttttKKKKttDBtGGKt0tD...", // 19
        "...............KttBBKtBtttttt0BtKttttDttctttttDK0Bt0KKBKRZKCD...", // 20
        "...............KKtcBGKtBtcttttttKBtttDBBt0ttttKBBtttKNK.DZNA....", // 21
        "...............KBBKKGKB0t0tttt0BKD0BtKB000BttKtKKKKcDNZ.KKK.....", // 22
        "...............KBBBBBBKKttctGttGKKGttBKBBBtttKDRR..KGGK.........", // 23
        "..............KKBBBKKKDBBBBtttBKBBBBBBKt00ttttKKKK..............", // 24
        ".............KBBBBK.DttttttBKKK.KDKBBBBKKBtttt0K................", // 25
        "..............KKBBBNNKt00tttK......KKKK.GttBB0ttD...............", // 26
        "................KKNKDKKDKt0BtDDD........KKGGtBtt0ttDK...........", // 27
        ".........................KttKKNNK...........KtBt0BBNNK..........", // 28
        "..........................DKKNPK.............KKttQQFQD..........", // 29
        ".................................................DK..D..........", // 30
        "................................................................" // 31
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawToad() {
    val matrix = arrayOf(

        "................................................................", // 4
        "......................M.........................................", // 5
        "......................MI........................................", // 6
        "......................MIm.......................................", // 7
        "......................BmII......................................", // 8
        "..................KDI..MSKM.....................................", // 9
        "..................KDDM.MSmM..............DDDD...................", // 10
        "...................DDSKDSmK.............KPDDAS..................", // 11
        "...................DDiBKDSK.............K444QKK.................", // 12
        "...................DDmBKDSK...........KK3EQQJKK.................", // 13
        "....................KKmKDMKKKS.M.....SDPl1DNCKK.................", // 14
        "....................KKmKDDNDNDKM...KKDDPPDDKAKK.................", // 15
        ".........KKD........KKIKGDDDDDKM...KKDDlDDNKNK..................", // 16
        "........K3JCK.........KDDPlPDDDIKKKQJQQQDKNNK...................", // 17
        "........K44EQK.......KDDPlQlQPBmMDNKCPPPCKNCK...................", // 18
        "........KQ4EQAKKK...KKDDPQQQQPBKMDAKAACPCKNCK...................", // 19
        "........KPQQEQKKD...KKDDPQE4QPBiMDCNKKNCCKNAK...................", // 20
        "........KNPPQlPDDKKKKGDPQQQ4PDKiIDDDNNNKPANKK...................", // 21
        "........KKACPPlPDDNKKDCPPPQ1PDKmIDDDDDDGKKNNK...................", // 22
        "........KNNNDDPPPANKNDPPPPPPPPDIIDDDDDDGKKKNK...................", // 23
        "........KNKKDDPPPCNKNDPPPPDPlPPBmIDDDDDDGKKKK...................", // 24
        ".........KNNKDCCCNCPPPPPPPDPlPDKImmBCPPJCCDBK...................", // 25
        ".........KCAKNCANKQPPQQQPPDDLP4JKKKCQCCPJDMINKK.................", // 26
        ".........KDNNKANNNQQQQQPPPDDPQEQAAAAPCCPDIDDANS..S..............", // 27
        "..........DKDKNNNCQQQElPPPPDQQEQPPCDDDDDMSDNCNNS.K..............", // 28
        "..........DNDKKGDPQQQQPlPPPLPPQ4QPPPDDDBiMPJQCND.B..............", // 29
        "...........KKGKDDPQQQlQlllDQPPPQEQlPPDDBiKDDDDDDKiKK............", // 30
        "..........SDCDKDDCQQEQQlQQPQPPPQPPlPPDDMKKGDDDDGKiKK............", // 31
        "..........DAPDKDDDQQQEQlQQQQPPPPPPlPPDDImKNDDDDDKiKK............", // 32
        ".........KPJQCKGDDDQQEE4EQQQPPPDLP1lQSIiIKNCCCANBiKK............", // 33
        ".........KQQPCKGDDDDPQ4EEQQPDDDDPPPQQNDmKPPCCCDMKK..............", // 34
        ".......KDAQPPNKGDDDPPQQQQQQPDDDDDD1QQPADNDPPCADMmKDDDDSSK.......", // 35
        ".....KKKNPQPDKDDDDDPPQQQQQQDDDDDDPPE4QPKDDPPANDMSKKKKNNND.......", // 36
        ".....KKKJ4PDDK.SKDDPQQQQQPAQPDPDPQQQ44QPPDDBBBSSKKDDPllQQK......", // 37
        "..KKKPQQ4QDDK..SKGDACPPPPC44QQPPPPPDllll1BSKimDKDPKDllDPPQK.....", // 38
        ".SDDGNPQPQCAK...SKGACCPAPQQQQPPPPPPDPPPlDIIIDDDDNAKAPDDDAPK.....", // 39
        ".DNDGNPQCQADD....DGAACANPQPQPPPPPCDDDPPPKIIDNKDDAANNADDNNAD.....", // 40
        ".DKKKPPPKQND......KNACNKCQAPPPPPCANNNACPKMDKPDNAPEDNKPKNNK......", // 41
        ".DDllDNKQGKD......KKKKKNACJPPPClQQQlPAACLMKKPNPQQlPDDKNKD.......", // 42
        "..SNNKDDPKK....SDDNACPQQPPNQPPPQlPP1PPPAPDDDPNPQPKKKKKCND.......", // 43
        "..KDDDDDDD....KDNDACPQQQPPAQPPPlPDDDLPPNPPPDLNPQDKDDKKADK.......", // 44
        "......SKK.....KDD4lll4QPAAQPDLlDKKKKGDLDCPPPDCPDKImIMKK.........", // 45
        "..............KDDlDDDDJNAPQPDDPKMmmIMGGPCPPDGQPDKiMImK..........", // 46
        "..............KACPQPPCQNACQCDDPKmIMKMDNDPPPPAQNDMmKKKN...DDD....", // 47
        "..............DDAPQPPAPNACPADDPKmIMiIDNDPPPPCPNKDSKKKNKKSNNNSK..", // 48
        "...............SKDQQPNCNACCNNNDKmIMKmDKDLPPCPDGKKKGKKNKKNCDDDK..", // 49
        "..............KDlPDPCKKNAAKDPPDDBKKmIKKDLQQQQCCADDGKKPNCCDDKKK..", // 50
        "..............KDDDDCAK.NNNKDPPQQDDKNKDDDPQQQlPPPPPANKCPNNKKKKK..", // 51
        "..............SDDDAPCNSSDNKDPPQQPDDDDD11QQQQllPPQPANKAPADDNSK...", // 52
        "...............SKNCPPCK.SKKDDPQQ4QlP1QQQQQQQlQPPQPNNKNCQQPDKS...", // 53
        "................KKCQPCK..KKDDDKQPJJQQE444llllQQQlDKNA.KGDDPDDK..", // 54
        "................KKPQQPPNDD.KDDDKKKKKKPPQ1PDGDlQlDKANK..DKKKKS...", // 55
        ".............SSSSDPQPPCNNDSKGGDKNNNNNACCDDDDDPlPDKNDS..SSSSSK...", // 56
        ".............KKKKPPPPCNCADKKKKKDDDDDDDGKKGDDKDPPDKKK............", // 57
        "...........KKlPPPCNCCPKKGDDlND.KKKKKKKNLDDGKDDDDK...............", // 58
        "...........KKDDNKKKNADlKKKKKK.........KKKKGDKKKD................", // 59
        "............SDNDSSSDNDlDNNKS...........SSSDNKKDK................", // 60
        ".............KKK...DDDLlDDK...............SKKKK.................", // 61
        "....................KKKKKD......................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
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
        "................................................................", // 0
        "................................................................", // 1
        "................................................................", // 2
        "................................................................", // 3
        "........................................FE......................", // 4
        "........................................FE......................", // 5
        "........................................FE......................", // 6
        "..............................QF........FE......................", // 7
        "..........A...................QQF..KPQE.FQQQ..A.....K...........", // 8
        "..........K...................QQF..KPQQFFQQQF.KKF...KK..........", // 9
        "..............................QQF..KPQQE.FQQQ..KQ...KK..........", // 10
        "........CQ.r..................PPQF.KPQQQEKrCQQKKCC...KK.........", // 11
        "........rQQr...................PPQQPCPQQEKAKNNKKNKD..DK.........", // 12
        "........rQQr...................PPQQPrPQQEKAKKKKKKKK..DK.........", // 13
        ".......QrCQQA..................PrrrrNKKKKKKDDDKKKDKKKKKK........", // 14
        ".......QrPPP.............QQ.....PrANKKKKKKDDDKKKKKDKDDKKK.......", // 15
        ".......PCPPP.............PQF..KKPrAKKKKKKDDDDKKKKKKKDDKDK.......", // 16
        ".......rPPPC.............rQQF.PrrrNKKKKKDDDDKKKKKKKKDDKDK.......", // 17
        ".......APQC..............rrPPQCrrKKKDDDDPDDKKKKKKKKKDDDDDK......", // 18
        "......KNAQC..............KACPPrNNKKKDDDDPDDDKKKKKKKKDDDDDKK.....", // 19
        "......KKNQr...............AACrrKKKKKDDDDPDDDKKKKKKDKDDKDDDK.....", // 20
        "......KKKr...................DKDKKKKKKDDQPDDKKKKKKDKKDKDDKK.....", // 21
        ".....KKK............KKKKKKKKKKKDKKKKKKKDDQPDKKKKKKKKKCQKKKK.....", // 22
        "....KKKK..........KKKKKKKKKKKKKDDKKKKKKDDQPDKKKKKKKKKAQDKKK.....", // 23
        "....KKK...........KKKDDDDKKKKKDDDKKKKKKDDQPrKKKKKKDKKKCQKKK.....", // 24
        "...KK...........KKKKKDKKDDKDDDDDDKKKKDDDDAQPDKKKKKKKKKKKKDDK....", // 25
        "...KK..........KKKKKKDKKKDDDDDDDDKKKDDDDDNAPAKKKKKKKKKKDKDDKK...", // 26
        "...KK..........KKKKDDDKKKDDDDDDDDKKKDDDDDKNPrKKKKKKKKKKDDDDDK...", // 27
        "..KK..........KKKKDDDDKKKKDDDDDDDKKDDDDDDDNPCKKKKKKKKrNKKDDDDK..", // 28
        "..KK.........DKKKKDDDDKKKKDDDDDDDKKKDDDDDDDDPAKKKKKKKNCKKKKKKK..", // 29
        "..KK........KKKKKKDDDDKKKKDDDDDDDKKKDDDDDDDDPrKKKKDKKKrFFKDKDD..", // 30
        "..KK........KKKKKKDDDDKKKKDDDDDDDKKKDDDDDDDDPrKKKK.KKKKPCK.K....", // 31
        "..KK.......KKKKKDDDDDDKKKKKDDDDDKKKKDDDDDDDNPrCKK....KKKr.......", // 32
        "..KKK.....KKKKKKDDDDDDKKKDDKKKKKKKKKDDDDDKKKCPPKK....KKDDKK.....", // 33
        "..KKK.....KKKKKKDDDDDDKKKDDKKKKKKKKKDDDDDKKKCPPKK.....KDDKK.....", // 34
        "...KKK...KKKKK.KDDDDDDKKKKKKKKKKKKKDKKDDDKKrCQrK.......KKK......", // 35
        "...KKKKKKKKK..KKDDKKKKANNKKKKKKKKDDDKKKKKNAPrrrK................", // 36
        "...DKKKKKKKD.KKKDDKKKKANNKKKKKKKKDDDKKKKKNAPrrAD................", // 37
        "....KKKKKKK..KKKKDDDKKNNNNKKKKKKKDDKKKKKKNAAAAK.................", // 38
        "......KK...KKKKKKKKK.KNNNNNKKKKKKDDDDKKKNAAAKK..................", // 39
        "......KK.KKKKKKKKKKKKKNNNNNKKKKKKDDDDKKKNAANKK..................", // 40
        ".........KKKKKKKKKK.KNNNNNN....DKDDDDDKKNAAKD...................", // 41
        ".........KKKKKKKKK.KNNNNNKK.....DKDDDDKKKKKKD...................", // 42
        ".........KKKKKKK..KNNNNNKK.......DKDDDKKKNNND...................", // 43
        ".........KKKKDDD.KKNKKKKDD.......DKDDDKKDKNNDK..................", // 44
        ".........KKKK....KNNKKKK.........DKKDDKKKKNNKK..................", // 45
        "........KKKKK....KNNNK............KKDDAKKKNNNNK.................", // 46
        "........KKKKK....KKNKK............KDDPPDKDKNNAK.................", // 47
        "........KKKK......KNKK.............KAQQAKKKNNrK.................", // 48
        "........KKKK......KKNK.............KAPQCK.KKNCCK................", // 49
        ".......DKKKK......KKNKD............KNCPPAK.KKrrAD...............", // 50
        ".......KKKKK.......KNKK............KNCPQCK.KKAAAK...............", // 51
        ".......KKKKK.......KNNNKK...........KNCPQCD.KKAAAK..............", // 52
        ".......KKKAKKK.....KKNKNNK...........KNPQQQPKKKKAAKK............", // 53
        ".......KNANNKAK....KKKKKKK...........KKAPPPPNKKKNAKKK...........", // 54
        ".......KACKrKCK....KKKKKKK............KNPCCCrKNNKAKNK...........", // 55
        ".......KKKKKKKK.......................KNCACPrKKKKKKKK...........", // 56
        ".......KKKKKKKK.......................KNANNPAKKKKKKKK...........", // 57
        "......................................KDANKCND..................", // 58
        ".......................................KKKKKD...................", // 59
        "................................................................", // 60
        "................................................................", // 61
        "................................................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
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
        "................................................................", // 0
        "................................................................", // 1
        "................................................................", // 2
        "................................................................", // 3
        "................................................................", // 4
        "...................KK....KKKKK.KK......KKKKK....................", // 5
        "..............KKK..KSSSSSSDDDSSSN....KKIPPrDS...................", // 6
        ".............KSDDKKKKDKDDDDSSDDKDKKKKrrIQSrNKSK.................", // 7
        ".............DSKKKGS.KKSSDKGSSDDDKKDPQSrrNKKKKS.................", // 8
        "................KKSDGKKGSSDDGSSDSKDISQPPNS........DNNNNKKKK.....", // 9
        ".........KDDDDDDSKDDSSDDSSDDKSSDDDPSSrrNKDS...KDDDNNNKKKKKK.....", // 10
        "......SSSDKKKNNRNSNNDDSSSSSDKSSDKrKEQPAKDDDSSSrRRNNNDSS.........", // 11
        "......SSSSSSSNNRNNNNNNSDDSSSDDDDKIKSPPPKDDDNNNRARNKDK...........", // 12
        ".............DKKNNArANNNNDSSDDDDKDIrrrDKKNNArrANNNNK............", // 13
        ".........KGGGLDKKKKKGDSLDKKDSDKKKKDDDSKKKGDGGKKKD...............", // 14
        ".......DDDSGLDGNSSKKKGGGDDGKKDSSDSSDSSDSDGGKKKKD..DGSKKKKK......", // 15
        "......SDDDDGSDKKDDDDDKKKGGDSSKKSDDSDDKDSDDGDDDDDDDKDDDDDKDK.....", // 16
        ".....SDDDDKDlDGDKDDDDGKKDDSKSSDDDDSDDSGDSDGDDDDDDDKDDDDDKKDK....", // 17
        "....KKDDDDKLlDDDKDDDDDKKDSSKDKKDSDDDDSDGSSDGGDDSDDKGLLGKDNKD....", // 18
        "....KKKK.KKLlLDDDKKDDKGSSSDGDGGDKKlLDDSLGDSDDGKKKKKKDSGKKSKD....", // 19
        "....KK...KGKKDGGDSDGKDSKSDDDGSSNGDLDDLDGDDDSGDDKKGDDLSLGK.SD....", // 20
        "....KKDK.KGKSGGGGDSSDDSDDDDGGSKSASDGGDSASDKDDKKKGGDDSSLGK.SD....", // 21
        "...KSSADKKGKDGGKGGSSSDDDDDDGDDSDADGDDDDADDKKDKGGDDGSSlLGKKSK....", // 22
        "...KNSrNSKGKDGDDKGDLSDNDGDSGLDDDNGDSSSGDDDKKGGDDDDGSKlSGKSDS....", // 23
        "...KDrADKKGlDDSKKGDDDAAKKDSGDSSLGKSSSKGLLDKGGDLDGKKSllKGKSKNK...", // 24
        "....KKKK.KGSGK.KKKPQQQPKKSKDKDDGGDKDSSGGKDDGGGGKKKKDlSKGKArAKK..", // 25
        ".....KK..KGSKKKDArPCANGGGSKDKKGGGSDGDSGKKDDGGGKKDKKDLSKGKNAAKK..", // 26
        ".......KDDGLKDDPQPNNKKKDDSSSKKKGDPSDGDKGDSSKKKNNNDKDDLKGSDNNK...", // 27
        "......KDDDGDKKDPPDSDKKKLDDSKKKKGDDPDGKKGSSSKKDPPAK.SGDSGKSKKS...", // 28
        "......DDQQDDGKNPNKKSKGKSDDSKDKGKKDPDDKKKDSSKDPPQPNKDKDSGSDKKDK..", // 29
        ".......DDSKPNKNAKKKSKGGDLDDKSDGGGKNrAKKGGDSDKDNArANKKDSGKNND.DK.", // 30
        "........KKNQPNKNAPNKKGGGLLDSKDDDGGKArAKKKDSSDKSKNPQQDDSGNNS.....", // 31
        "........KKGDNKDKKNADKKGGSSDDKDDDGGGNNrNKKDSSGK.KKNNANGLGKSK.....", // 32
        "........KGLDGKDSKKDSSKGGSlLDKDDGGGDDNNNKKKDSDSK..KKDKDSLGK......", // 33
        "........KDlLLDDDK.SKKKGGLlSDSDGGGGDDDGGKDDDDSDS...KDKDSSGK......", // 34
        "........KDKSSSLDDK..KGDGDSSDSDGGKGDSSDGDK.KKSDS..KDGGDLLGK......", // 35
        "........KDKGGKKGGKK.KGLDGGGGKKDDKGGDDGD...KKDDS.KGSlSLSGGGK.....", // 36
        "........KDSGKKDKKKNSKGLDGDDDDDDDGDGKKD.....KKK.KDDGGGDSGGGK.....", // 37
        ".......KDLLKKSK.KKKSKGSLSSKSSDDDSSDKDK.....KKK.KKGKKSGLGGGK.....", // 38
        "......KGDLGKKK...KSSDDLDDSKSSDKDDDKKDS......K..KDS..SGSDDDDK....", // 39
        ".....KNGLLKKKSK..DKSGLDDDDSSDGKGDKKGKDKKKKKKK..KS...SGLLDDDKK...", // 40
        "....KKADDDKKGGD....SGDGDDGKKKKKGGKDDDKKGGGGGGKK.....SGGlDLDGKK..", // 41
        "....KKNGAAKKGGGD...SKGGSSDDKKKDDDSSDKGGLSSlSLGGS.....KGKDDDGKK..", // 42
        "....KKKANDKKDKDKDDDGKKGSSSSSDDSSSDDGGGLlKKKKKLLLD....KGSDGDDGK..", // 43
        "....KKKRND..KDGDDLSSLDGDDDSDDDDDDDGDDDLSSSSDDGDDDD..KDGLGKGDKDK.", // 44
        "....KKKNND..KDDDSSSSLDDLDGDDKKDDKKGDDDDLLLLDGGGGDS..SNDDDDDGNKK.", // 45
        "....KKDNDK.KDDKKKKKLDDKlLGKKGKKKKKKKKKGDLKLDGKDDK.KKKNADK.KKAKK.", // 46
        "....KD.DK.KKGLLDLDDGGDLKLDKKGGGKKKKKGGKKKKKNNNK...DKNND...KKNNK.", // 47
        ".......DKDKKDDGGDLKKLDDKKKKKKKKKKKDKGGKKKKD........DKD...KNNKK..", // 48
        ".......KDD.KDKKKGGGGGGGKKKKKKKKKN.DKGDGGKDK........KKK..KKKNK...", // 49
        "........KK..KDKKKKKKKGGGGKGGKKKKN.KDGDDGKK..............DDKK....", // 50
        "............KDNKKKDDDKGGGGGGGKKKN..KDGLDKDK.............KK......", // 51
        "...........KDNANKKKKKKKGGDDKKKKKN...KKLDGKD.....................", // 52
        "..........KKrNKNAANKKKGGDDGKKGGKN...KKDLGKD.....................", // 53
        "....KD...KrrNK.KKKNNGLKLDGDKKGGKN...KKDKDGD.....................", // 54
        ".....KDDDPrNK...KKDNDKlDGDKDGGKDK...KKGKDGD.....................", // 55
        "......KKNNDK......DGLllGKDKGGGKK....KKGKDGDKKKK.................", // 56
        ".......KDDD....KKKDDKKLKKKKGKKDK....KDGLDGGGKKKKK...............", // 57
        "..............KKKKGDLDGKKKKKKKK......KKDDDLDNKKKD...............", // 58
        "............DKNNKNNNKKKKKKKK.........KKGDLKNrANKKK..............", // 59
        "..........KDNNNNRrANKKK..KKK..........KNKKKKKKKKKKDK............", // 60
        "..........KKKNNKNNNNKNK................KKKKKKKKKKKKK............", // 61
        "...........DDDDDDDDDDK..........................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
    )
    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawMarilith() {
    val matrix = arrayOf(
        "......................KKKKKKSK.....KDK..............KKKSKKKKKKDK", // 0
        ".....................KDppppprDS...SDKDK......KKKKKKSDDDSKKKKKKKD", // 1
        "....................KDPppppppprS..KDSDK..KKKSDDDDDDKKKKKSDDDDDDK", // 2
        ".....................SAQppprrprS..KSSSDKKSDDSKKKKKKKDDDDSKKKKKK.", // 3
        ".................KSSKSDQppprNNSSSKSDKKDDDSiKmppmKDDDKKKK........", // 4
        "........KKKK.....SN1NAPppppppPArrKNDKDpmimqmSDDDDK..............", // 5
        ".......SDDDDKKKKKDKN1PpppppppANKKNZPKNpqqmSDSKKKK...............", // 6
        ".K...KSrIIppMNNNDrrKNpppppprKC1rrNADSDImDNSK.......K............", // 7
        "KSSKKSrrDDDpppppppprKKNKrpN1ArrNNRRKNKDNpqrS......KSS...........", // 8
        "DSDSSrrSKKKDIpppppprKN1KDrN1ArppprKNCSDrqmSS.....KDSDK..........", // 9
        "KSDS.SS....KDDDrprDSSD1DSDK1ArpppprNZNKriDDDS....SDKDS..........", // 10
        "KSDS..........KSDK...KN1N1AK1ArppprNZZNriDKNSK..KDKWDS..........", // 11
        "KSDS.........KDSKKKK.KN1N12N1AKrpprNZZNriSKSSS..KDKWDS..........", // 12
        "DSDSK........D1ASSNDKSA1NK1N1AKKrprNZZRrpmiISK..KDKWWSS.........", // 13
        "KDKDS........D1ND121KN1N1N1A1AKKrprNZXZNpppprD..KSSWWWSSK.......", // 14
        "KDKSSK.......S1ASSP1NN1N1NKAA1ArrrrNZXZNpppprD...KSSWWWDSKK.....", // 15
        "KDK.SS........D1ASSDAA1A1NKK1AAAKArNCZZArprrrS....KSKWWSSSSK....", // 16
        ".SSKSSK.......S1ASSSA1N1NrpN1AAAN1NKKCN1NKAAS.......SSKWSDSD....", // 17
        "..DS.SS.......SrAAANA1N1NrpN1AAAKN1KKCACDKAAS........DSWKSSD....", // 18
        "..DS.SSK.....KDprA11AAN1NKrN1ANAKN1KNC1NrNAAS........SSKWKSS....", // 19
        "..DSiiSD....KSppprrA1NKKAAKKNAN11N1NAA1NrAADS.........SSWSS.....", // 20
        "..DSiiSD...KDKqpppprA1KNK1NKK1111NNKAAKNA1NS..........KSKSS.....", // 21
        "..DSiiSD...KDKqpppppA1ArNCNNK111AKKKAKN11NKSK..........DSKSK....", // 22
        ".KSKiiSDKSSSSSIppppprN1NrrC1A11NKKNNKKANNAADS..........SSSSSSSK.", // 23
        "KDKiimSSSSSSmDNrppppprAAArAKNAANKNO1NRNKA1PK.........KSSSSSSSSSS", // 24
        "KDKmiSDSIiiiqrCArrppprN11A1AKA1NKNCANZZNDDS.........KSDKS.SDKKSD", // 25
        "KDKmiSKNpppppprAAArrrAANKAA1AKKNANKNKZRNSK..........DSKSSSDDSDS.", // 26
        "KDKmiSKNppppppprA1AAA111AAANRNKN11NCNRRRRDSSSSSS....SSSSDRRNN...", // 27
        "KDKqiSKrpppqqqqprKN1AKNA11ANARKKNANRKRZXXANNNNNASSSSSDDDAZZAN...", // 28
        "KDKmiSrpppqmSSPPqrDSSD1AKKA1NNNKKKKKKZXZZXXXXXZRRNRRRRRZZAAAS...", // 29
        "KDKqiSrppqmSSSN1PIKKSDKAAAKNRRNKKKKKKZZNZXZRZXZRRXXXXXZRNDSD....", // 30
        ".KSIiSrppqKDK.SDSKKS111AAAAZXXZRNKKKRXZNNRRNNRRASRRRRRDSSSSK....", // 31
        "..DriSrppqKDK.SSK.KS11AKNKZXXZZXZRNNZXXRKKRARNKDKSSSSSK..KK.....", // 32
        "..DriSrppqmSSSSSK..KSDKRZNZXXNRXXXZRRXXQSNRXXZRASS..............", // 33
        "..DriSrpppqmSSSKS....KNZZCRZXZRPQXZNNQEqmDNZXXZRRASK............", // 34
        "..SrmSIrpppImirrIK.KSSAXXXNRXXRIqQQDNmiqrKKNRRRRZZASK...........", // 35
        "...DriSNpprSrppppISDRAXZRRKAXXRrqiimNrrrKKKKKKNXXXZNK..KK.......", // 36
        "...KImKSrrDKDrprrKNZXZRNKKRRZXRNrpprRRKKKKKKKKKNRXXCDKKSSS......", // 37
        "....SIiSNrSSrprRRRZXXZKKKNZNRXRKNrrRZRKKNKDDNNKKKRZXNDSKSD......", // 38
        "....KIIIDSSrprRZXXXZRNKKKZZNRXRNRRZXZKrrprSDrpNKKKRZRNSSSK......", // 39
        ".....SDiSDKrpZZXANNNKKKKRZRKRXRRXXXZRNppprSDrprDSKRZZNDSK.......", // 40
        "....KSKKDRRAZARRDDKKKKKAZXNKRXZRZXXZKKpprISrpprDKNZAZNKS........", // 41
        "...SSSSDNZRZRKrKSDKKNRRXZRKKRXXRRXXZRNppNSSrppprSSAKCNDSK.......", // 42
        "...KSSSSSAAZNrpprSSSRXXZRrSDRXXRDQXXXSDDSKSrppprKDSDNSSSK.......", // 43
        "....KK..KSARrppprDSDZXZRrmSNRXXRAQQXXKDKKSIpppprSDKDKSSS........", // 44
        ".........KSNppppprNNXZRrpiDDDZXRRXQEEPNDKDrrrrIKKDKDKSSDDS......", // 45
        "...........SDMprNNRZCRrpqiDDKACSSRXQPASK.DKKKKDKSrDrrDSSSSS.....", // 46
        "............KKDRZCANNNpppqmKDAASSNCNNmmiSKKKKDKKKNrppprDDSK.....", // 47
        "..............DACNDSSSrpppqmrNDKSKDSSqqiDKKKDmiKKKppppprrrS.....", // 48
        "..............KDSKmmDDrppppppNSSSKmmmiiSrNKDK.iKKKppppppprS.....", // 49
        "..............SSmqmSNppppprNNKSSDqmiiDDrpDDKSDDNKNpppprNNDK.....", // 50
        "..............DSSNSKSMrNrMSSSDSSKNDDDrrprSKSDKKKKrpppprDSSS.....", // 51
        "..............KSSDKSKKSDSK..SAASDNrrppprSKSKKKKKKpppprSSDSK.....", // 52
        "...............KSSSDqmKDK...KDKSSNpppprS..SrrKDSNMpprSSSKK......", // 53
        "................KKKSNrmSSK...KDSSNppprSK.SrrISKDSNppDSSS........", // 54
        "....................KSMmmSK...KSNNpprSDDKDrrSSKDKDpppDSSS.......", // 55
        "......................KMImSK...SNprDSDrNKDrrSSDSDpppprNSSS......", // 56
        ".......................SrImSK..SrpDSDrpNKDrrSKSDNppppprrSK......", // 57
        "........................KDmmSK.KSDSDKNDrDrpprDNNrpppppprS.......", // 58
        ".........................KDImSS..SSKKKKDNrpppppppppppprS........", // 59
        "..........................KDImSK..SDDDDSKDrppppprNrppprS........", // 60
        "...........................KSImSS..KKKKSKDrppprDKKSDNNS.........", // 61
        ".............................SDS.......KDDrpprSKKDDKKK..........", // 62
        "..............................K..........KSKKDSKDK.............." // 63
    )

    val palette = mapOf(
        'K' to Color(0xFF141414),
        'D' to Color(0xFF4B4B4B),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'N' to Color(0xFF502314),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'O' to Color(0xFFDC6919),
        'J' to Color(0xFFF59128),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'R' to Color(0xFF821414),
        'Z' to Color(0xFFD21E19),
        'X' to Color(0xFFF5372D),
        'r' to Color(0xFF961950),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'G' to Color(0xFF0F5019),
        'g' to Color(0xFF199123),
        'L' to Color(0xFF50BE28),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'B' to Color(0xFF1E3782),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'M' to Color(0xFF46238C),
        'I' to Color(0xFF7346AF),
        'm' to Color(0xFFA578D7),
        'i' to Color(0xFFD2B4F0),
        '0' to Color(0xFF2D4691)
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
        HeroClass.ONION_KNIGHT -> drawOnionKnight()
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
        enemyName.equals("Rat", true) || enemyName.equals("Wild Rat", true)-> drawWildRat()
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

