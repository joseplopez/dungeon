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
import com.game.dungeon.data.models.MonsterType
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

        "..........................................", // 6
        "........DKKD...KKKKKKKKK..................", // 7
        ".......KAQQA..DtgLLLLgttK.................", // 8
        "......DAPCACANDLLLLLLLLgtGD...............", // 9
        "........DPQQPCNDLLLLLLLLgtGD..............", // 10
        "........DQEQPCANGDLLLLLggttK..............", // 11
        ".......DPEQQQPAAANGDLLLgtttGD.............", // 12
        ".......NPQE1ANNAAPCNDLLLttttK.............", // 13
        ".......APQPANAPNAPQPNgLLLtttK.............", // 14
        ".......APPNDAQPCNAPPPNGgLgttK.............", // 15
        ".......NPAKTAPAKDDNAANKtggtG..............", // 16
        ".......DAN..APNKTSNACPNKtttK..............", // 17
        "........NK..PQNKWTACPQQKttGGDG............", // 18
        "........D...PECNWFPPACEKttKDLtND..........", // 19
        "........D...PEPAWQQPAAKKKKKGgttgGK........", // 20
        "............AQEEEEQQ4ll11gGKtttLLLK.......", // 21
        ".........DGGNCEEEQPQlLttGGGGKGtttLLK......", // 22
        ".........PlgGNPEEPAPSttKNAAANNGttgLD......", // 23
        ".........KPLtGNPAA1lLtKAPOOOCANGGtLLD.....", // 24
        ".........ANllgGNNAlgtKAOJ2E211NDDKGDD.....", // 25
        "........A1ADtLgPPLtttK1llLNKKKK...........", // 26
        "........KKLNKttEQttttKKKKKNPEEPN..........", // 27
        ".........NANNGgPPLLgtGNPQPDLQEEPN.........", // 28
        "........PAEPAKDSSLLgtKQQQQPLgSQPN.........", // 29
        "........PPECANDttgtttKQEEQQDttPAD.........", // 30
        ".........QPAANDDSSDDDKPEEQQDttA...........", // 31
        "........PEPCANNAPPCAAKCQEQPDtCD...........", // 32
        "........CQPCANGtgtLLgtKPPPADKK............", // 33
        ".........AAADDDtttLLLgtKNNDD..............", // 34
        "..............LLgDllllLgNK................", // 35
        "..............DNNDSSSLLgGG................", // 36
        "..............DNNNGDSSDDDK................", // 37
        "..............DNAANAEQPPAD................", // 38
        "...............KKANNPPCCANK...............", // 39
        "...............KGKNlLlll1gN...............", // 40
        "...............DNAACAA111AD...............", // 41
        "................KANNPOCCAND...............", // 42
        "...............KNNNAOOOCAAN...............", // 43
        "...............NLNA121OCAAN...............", // 44
        "..............DKKNLlllLAAgN...............", // 45
        ".................KKKKKKKKKK...............", // 46

    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '4' to Color(0xFFFFF56E),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'L' to Color(0xFF50BE28),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'g' to Color(0xFF199123),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469)
    )

    drawPixelMatrix(flip(matrix), palette)
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
        "................................................................", // 0
        "................................................................", // 1
        "............................EQQ....QF...........................", // 2
        "............................PPQE...PQF..........................", // 3
        "............................FFPPE...PQ..........................", // 4
        "...............................PP....P..........................", // 5
        "................................PE...PF.........................", // 6
        "................................PQKD.PE.........................", // 7
        "...............................KKAQKKKE.........................", // 8
        "..............................DKDDPQEWE.........................", // 9
        ".............................KDDKKKKKKKD........................", // 10
        ".............................KKKSDDDDSD.D.......................", // 11
        ".............................KKQKKKKKKKKD.......................", // 12
        ".............................KDDDNPAAKDW........................", // 13
        "............................DKDSKAQAPAD.........................", // 14
        "...........................DKDDmKQQPPKD.........................", // 15
        "...........................DDDDSKNNPNKKD........................", // 16
        ".............................KKKDKKKKKKD........................", // 17
        "...........................DKDKKSKKKDDK.........................", // 18
        "..........................DDSSDKDKDDDDKDD.......................", // 19
        ".........................DKDDDSSKKKDDDDK.D......................", // 20
        ".........................DDDDDDDKKDDDDDKDD......................", // 21
        "........................DKKDSSDKKDDDDSSKKD......................", // 22
        "........................DKDDSDDKKDDDDDDKKK......................", // 23
        "........................DKKDDSKKDDDDDDSDKD......................", // 24
        "........................DDSSSDKKDDDDDDDKKDD.....................", // 25
        "........................KKNDDDKKDDDDSSSDKKD.....................", // 26
        "........................KKKKKKKKKDKKDDDKKKD.....................", // 27
        ".......................DDSSSSKKKDDDDDDSDKKKK....................", // 28
        ".......................KKKKKKKKKKKKKDDDKKKKD....................", // 29
        "........................DKDDKKKKDDDDISSDKKD.....................", // 30
        "........................DKKKKKNRKKKKKKKKKD......................", // 31
        "........................DDDSDKKKCCCRRRRRAKD....KDD..............", // 32
        ".........................DDDKKNRKKKrRrRrRNKKKKKKSD..............", // 33
        ".........................DDSDKKRCCCRRrRRAKDPNKKKK...............", // 34
        ".........................DDDSKKKKKKKRrNKKDDQDKSW................", // 35
        "..........................KDDKDDDDKKRRNKDDKNSWW.................", // 36
        "..........................DKKKKDKKKKCCAKDSKKSW..................", // 37
        "..........................DNQQNDDKKKCCAKDDSWW...................", // 38
        "..........................DNPQPKKKKKCCAKDDSW....................", // 39
        "........................DKKNAAPKKKKKCCAKKDS.....................", // 40
        "......................DKKKDKNKKDKKKKCCAKKKD.....................", // 41
        "...................DKKKKKKKKKDDDKKKKSSrKKDK.....................", // 42
        ".................KKKKKDSSKKDDDDDKKKNSSSKKDD.....................", // 43
        "..............KKKKKDSSTWWDKDDDDDKKKRPrrKKDD.....................", // 44
        "...........DKKKKKDSTWWWWTDKKDDDDKKKRSPSKKKK.....................", // 45
        "..........KKKKSSSTWWWWWWDKKKDDDDKKKNNNNKKKK.....................", // 46
        "........................DKDDDDSSKKKKKKKKKDD.....................", // 47
        "........................DKKKKKDDKKKKKKKKKD......................", // 48
        "........................DKKKKKKKKKKKKKKKKKK.....................", // 49
        "..........................DKKKKKKKKKKKKKDT......................", // 50
        "...........................DKKKKKTKKKKKDT.......................", // 51
        "...........................DKKKKKWKKKKKD........................", // 52
        "...........................DKKKKTWTKKKN.........................", // 53
        "...........................DKKKTWWWKKDD.........................", // 54
        "............................DDKWWWWKKKD.........................", // 55
        "............................DDKWWWWKKDKD........................", // 56
        "............................DKKTWWWKKKDKKD......................", // 57
        "...........................DKKDKWWWKKKDDSDK.....................", // 58
        "...........................DKDDDWWWWKKKKKKK.....................", // 59
        "...........................DDSDDWWWWWWWWWW......................", // 60
        "............................DDDDWWWWWWWWW.......................", // 61
        "................................................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'm' to Color(0xFFA578D7),
        'r' to Color(0xFF961950)
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
        "................................................................", // 0
        "................................................................", // 1
        "................................................................", // 2
        "................................................................", // 3
        "................................................................", // 4
        "................................................................", // 5
        "................................................................", // 6
        "................................................................", // 7
        "......................................KKKKKK....................", // 8
        "......................................KKDSKKD...................", // 9
        "........................KKKKKK..KKKKKKKKTWDKKK..................", // 10
        "........................KKDDNN..NNNNNNKKTTSDDD..................", // 11
        "........................KKWWZZKKZZZZZZKKSSWWWWDK................", // 12
        "........................DDEEZZKKZZZXZZDDTTWWWWDK................", // 13
        "......................KKWWZZZZKKZZZZZZFWWWWWWWDK................", // 14
        "......................DDWEZZZZNNRRZZZZETTTWWWWDK................", // 15
        "................KKKKKNWWZZZXZZZRKKZZZXZZKKWWWWDK................", // 16
        "................KKDDDDWWZZZXZZZZKKZZZZZZNKWWWWDK................", // 17
        "................KDWWWWWWZXXZZZZZZZKKRZZZZZKKWWDK................", // 18
        "................DDTTWWWWPPPPPPZZZZNNRRZZZZKKWWDK................", // 19
        "..................KKKKKKWWWWWWZZZZZZKKRZZZKKWWDK................", // 20
        "..................DDDDKKWWWWWWPCZZRRDDRRZZDDWWDD................", // 21
        "......................KKKKKKKKWWWWKKWWKKRZTWDK..................", // 22
        "......................KKNNKKNNWWWWKKTTNKRZWWDD..................", // 23
        "......................KKEEUUFWKKKKWWKKRZZZKK....................", // 24
        "......................KKEEUUWWKKKKWWKKRRRRKK....................", // 25
        "......................KKEEUUWWQQKKKKKKKKKKKKKK..................", // 26
        "......................KKEEUUWWQPKKKKKKKKKKKKKK..................", // 27
        "......................KKEEEEQQKKZZZZKKSTWWTSKK..................", // 28
        "......................KKEEEEQQKKZZZRKKSTWWTSDK..................", // 29
        "....................KKWWKKEEQQKKZZKKSSWWWWTSS.DK................", // 30
        "....................KKWWKKEEQQKKZZKKSTWWWWTS..DK................", // 31
        "..................KNQPKKWWKKKKWWKKSSWWKKKKKKKKKK................", // 32
        "..................KNQPKKWWKKKKWWKKSSWWKKKKKKKKKK................", // 33
        "................KNQPKKKKWWKKWWTTKKKKKKZZKKTWTSDK................", // 34
        "................KNQPKKKKWWKKWWTSKKKKKKZZKKTWTSDK................", // 35
        "................KNEEPPKKKKKKKKKKQQPPKKRZZZNKTWDK................", // 36
        "................KNEEJPKKKKKKKKKKEEQQKKRZZZKKTWDK................", // 37
        "................KDTQJPKKKKRRRRKKEEFFSSZZZZKKTWDK................", // 38
        "................KDSSJPKKKKRRRRKKEEWWTSZZZZKKTWDK................", // 39
        "..................KKNNNNKKKKKKKKQQTTTTNKKKKKTWDK................", // 40
        "..................KKKKRRKKKKKKKKQQSTTTKKKKKKTWDK................", // 41
        "..................KKSSRRKKRRKKRRNNDDDDKKNNKKTWDK................", // 42
        "..................KKTSRRKKZZKKZZKKKKKKKKRRKKTWDK................", // 43
        "..................KKSSRRKKZZKKZZZZRRRRRRKKKKTWDK................", // 44
        "..................KKSSRRKKZZKKZZZZZZZZZZKKKKTWDK................", // 45
        "..................KKSSRRNNNNSSNNRRRRRRNNNNKKTWDK................", // 46
        "..................KKSSRRRRKKSSKKRRRRRRKKRRKKTWDK................", // 47
        "..................KKSSKKKKKKrrKKSSTTSSKKRRKKTWDK................", // 48
        "..................KKTSKKKKKKRRKKSTWWTTKKRRKKTWDK................", // 49
        "....................DDKKDSSSSSKKrPSSSTSDNKKKTWDK................", // 50
        "....................KKKKTTWWTSKKRRRRSSTSKKKKTWDK................", // 51
        "......................KKDDSSDDDDSSSSPPSSKKKKDS..................", // 52
        "......................KKKKKKKKSTWWTTARSSKKKKKK..................", // 53
        "..............................DDSSDDNNDDKKKKD...................", // 54
        "..............................KKKKKKKKKKKKKK....................", // 55
        "................................................................", // 56
        "................................................................", // 57
        "................................................................", // 58
        "................................................................", // 59
        "................................................................", // 60
        "................................................................", // 61
        "................................................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'W' to Color(0xFFFFFFFF),
        'X' to Color(0xFFF5372D),
        'Z' to Color(0xFFD21E19),
        'r' to Color(0xFF961950)
    )

    drawPixelMatrix(flip(matrix), palette)
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
    val flippedMatrix = flip(matrix)
    drawPixelMatrix(flippedMatrix, palette)
}

fun DrawScope.drawGoblin() {
    val matrix = arrayOf(
        ".............................................................", // 0
        ".............................................................", // 1
        ".............................................................", // 2
        ".............................................................", // 3
        ".............................................................", // 4
        ".............................................................", // 5
        ".............................................................", // 6
        ".............................................................", // 7
        ".............................................................", // 8
        ".............................................................", // 9
        ".............................................................", // 10
        ".............................................................", // 11
        "....................DDDDDDD..................................", // 12
        ".................DDDDDDDDKKDDD...............................", // 13
        "................DDDDGKKKSSSWW................................", // 14
        "................GDDDDGKSSKSGGGGGG............................", // 15
        "................GDDgLLgGKDSGgLSLgGD...D......................", // 16
        ".................DGggLLgGSTgLlLLLggG.........................", // 17
        ".................DGGgLLgGSFLLLLLLLgGGDm......................", // 18
        "..................GGGgLLGSTTggLLLLLLgGD......................", // 19
        "...................DGgggG0mimGgLLlSllLgGK11P.................", // 20
        "....................KGgGGK0I0gLLgLLLLLLgKAA1D................", // 21
        ".................NNNKGGGGGKKDLgGKNKNNKGgGA111................", // 22
        "...............ACQQPANGGGGKgggKNCCAAAAKGA11AAD...............", // 23
        "..............NPQTQPPNKGGGGgGKNA1QQC11CN1DDDDD...............", // 24
        ".............ACPPPPPANKGGGGGKNNAAQFP1QQKADImS................", // 25
        "...........DD.....rrANKGGggKNCASNDTQA1AKNMIIIm...............", // 26
        "...........D.DNDDDrDDDNKGGGKA1PTSNNANNNKNB0Immm..............", // 27
        "............DKKKKNDDDDDNKNNNCPQQDC1PPN......Imm..............", // 28
        ".............KGggGKDDDDDNNNNPCAQ1C1QEQ.......Imm.............", // 29
        "...........DDGgGGGGKDDDDNNKKCQAA1ANCQFP.......Dmm............", // 30
        "..........DCCNKKKNNKKNDDDKKKN1PNNANNAQEP........mm...........", // 31
        "..........NAAAPACQQN1NKKKGGKKNPCNNKDDPEP........mm...........", // 32
        "..........KACC1EPPEPPQNGtGGGGNNAAAD..AP........Dmm...........", // 33
        "..........KAPPAQENQQN11KtgLgGGGKKS...KN........Dmm...........", // 34
        "...........NAEPPQ1NADNAPDtLLgggGK...............mm...........", // 35
        "............AQSN1PKKQNKKDKGgggGGGGK.............mm...........", // 36
        ".............CPKKSDKDGGGGGKKNKNAGgGN............m............", // 37
        "..............PKKNSGKGGGGggGPDADggNA..........Im.............", // 38
        "..............PDKKDGGGgLLLLLDGGGGGAAK........................", // 39
        "................DKGGgLLLgLSLLgGGGDAAN........................", // 40
        "................KKKGgggGKKGgggGKDPAAN........................", // 41
        "...............DNNKKGKG1EDKKKGNPQPAAN........................", // 42
        "...............ANNKKKNNCPAKKKPQQAAAAN........................", // 43
        "..............DANNKKKKNA1PKNPQSAAAAN.........................", // 44
        "..............DNNNK..KKD1PNPQPANAAAN.........................", // 45
        "..............DNNK....DDANPSANNNAAA..........................", // 46
        "..............NNK......DNNPPNNNNNNK..........................", // 47
        "...............N........DNPAKKNNN............................", // 48
        "...............N.........DPANNKK.............................", // 49
        "................D.........NNNNN..............................", // 50
        "..........................KNNNN..............................", // 51
        "...........................NNN...............................", // 52
        ".............................................................", // 53
        )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'L' to Color(0xFF50BE28),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'g' to Color(0xFF199123),
        'i' to Color(0xFFD2B4F0),
        'l' to Color(0xFF96DC37),
        'm' to Color(0xFFA578D7),
        'r' to Color(0xFF961950),
        't' to Color(0xFF0A6469)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawWolf() {
    val matrix = arrayOf(
        "...........DDDD.................................................", // 0
        "..............DDD...............................................", // 1
        "............DDDNNN..............................................", // 2
        "..........DDNKKKKNN.............................................", // 3
        ".........DNKKD..DDNN............................................", // 4
        "........DKKD........N...........................................", // 5
        ".......DND......................................................", // 6
        "D......N........................................................", // 7
        "D......D........................................................", // 8
        "N...D...........................................................", // 9
        "KD..D....D......................................................", // 10
        "KNDDDD...DNKKD..................................................", // 11
        ".KNDDND..DDNKKKN....NNKN........................................", // 12
        ".KKNNDKNNNKNNKKKKNDKKKKKKN...............DD.....................", // 13
        "..NKNKNKKKKKKKKKKKKDDDNNNKN..............DNDKND.................", // 14
        "...KKKKKKK..KKKKKDSTSSDDDDND...........KKKKDNKKK.D..............", // 15
        ".....KKK.....KKKDTTSDDDSDDDNNK.......KKKKKKNDNKKKKD.............", // 16
        "............NKKDSSDDDNNDDDDDDNKDDKKNKKKKKKSDDDNNNNDD............", // 17
        "............KKNSSDDDNNNKNDSSDDNNKKKKKNKKDKDSDDDNDKDD.DD.........", // 18
        "............KKNDDDDNNNNKKNDSDDDNKKKKKNDKDDNDDDDDKKDDKDD.........", // 19
        "...........NKKDDNDDDDDNKKKNDDSSDNKKDKNNDSSDNNNKKKDDKNDDD........", // 20
        "..........KKKDDDNDDDDDKKKKNDDDDDDKKKDNNNDSSDNKKKKDDNNDDK........", // 21
        "..........KKKDDDDDDDDNKKNNNNDDDDDNKKDSDKDDTSDKKKKDSNKNDKK.......", // 22
        ".........KKKNDDDNDDDDKKKDDDDDDDDDNKKKTSNDDSSDKKKKDTDNKDDKK....N.", // 23
        "........KKKKKNKKDDDDNKKNDSSSDDDKKKKKKSTSDDDDDNKKKDTSDKKDDDD..KN.", // 24
        "........KKKKKKNNDDSDKKKNDSSTSSDKKKNDNSSSDSDDNKKDKDTWSDKNDDDDNKK.", // 25
        "......KKKKKKKKDDDSSDKKKKDDSTTSDDKKKDTSSDDSSDNDDDKNSTTSDKNNDNKKK.", // 26
        "......KKKKKKNDDDSSSNKKKKNDDSTTSDNKKKTWTSDSSDKDSDKNDDDDDDNKNDNKK.", // 27
        "......KKKNDNDDDSTSDKKKKKKKDDSSSSNKKKSSWTSTDDKKNDKDDDNDDSDDDDDND.", // 28
        ".....KKKKDDDDSTTSDNKKKKNKKNDDSSDKNNKSSSWTSDDKDDNDKNNDDSSSDDDDDD.", // 29
        ".....KKKKDDDSTTSDNKKKKKKKKKNNDDNNDKKDDSWSSSDKDSDKDNKDSTDTTSDDDD.", // 30
        ".....KKKKNDSSSDDNKKKKKKKKKKNKDDKNKKDDDDSDDDDKKDDKDDDSTTNNSTDDD..", // 31
        "....KKNKKKNDDDDKKKKKKKKKKKKKKNDKNKKNDDDDDNDSDKNDNKSSDTTDTSSTDD.D", // 32
        "....KKNKKKKKKKKKDNKKKKKKKKKKKKKKKKKDDSSSDDDDDKKDDDDSTTWTDDDSDD..", // 33
        "....KNKKKKKDDDDKKDKKKKKKKKKDDKKKKKKNTTTSSDDDKKKDDSSDTTTTDSSDDDD.", // 34
        "...KNDKKKDKKKKKKDKKKKKKNNKD...KKKKKDSWTSSSNSKKKDKDDSDDSSSSTDSSD.", // 35
        "...KDDNNDKKKKKKKKKKKKKNDKK.....DKKKKSSTTTDKNDNKKKNDDDNDSSTTDTSD.", // 36
        "...KDDNKDKKKKDDKKKKKKNDNKK......KKKKDSSTTDNKNDKKKKDSSDKDSWTSWSD.", // 37
        "..KNDDNKDKKKDDKKKKKNKKKKK........KKKKDSSSDDNKKNKKKK..DDDTSSTWSD.", // 38
        "..KDDDKKKKKKKKKKN...DKKD..........KKKDDDSSDNKKKKKKN..DDDDSSTSDDK", // 39
        "..NSDDD.DKKKKKK...................KKKDDDTSSDKKKKKK...DDDNSSSDDDD", // 40
        "..DSSN...NKKKK.....................KKDSSSDDNKKKKKK....DSNSSDNKN.", // 41
        ".KDTS......DD......................KKSTTSDKKKKKKKN....DDDDKKD...", // 42
        ".NDW...............................NNSWWWSDKKKKKK......DDD......", // 43
        ".KT................................KDSTWTTTSDKKKK.......D.......", // 44
        ".D...D............................DKNDSTTWTTSSDKK...............", // 45
        ".....D............................NKKNDSSTWTTTTDKD..............", // 46
        "...DDN...............................KKNDDSSSTWTS...............", // 47
        "KD.KN..................................KDKNDDDST................", // 48
        "KDDK......................................DKDDDD................", // 49
        ".DD.........................................DNNDD...............", // 50
        ".............................................DKKKN..............", // 51
        ".............................................DKKKKN.............", // 52
        ".............................................KKKKDND............", // 53
        "............................................NKKKK..ND...........", // 54
        "............................................KKKKN...............", // 55
        "............................................DKKN................", // 56
        ".....................................................D..........", // 57
        ".....................................................D..........", // 58
        "................................................................", // 59
        "........................................................D.......", // 60
        "................................................................", // 61
        "................................................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        'D' to Color(0xFF4B4B4B),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawPirate() {
    val matrix = arrayOf(
        "..........DK...........DN.............c0.....d..................", // 0
        ".............DDD.........D...........d0.....d0..................", // 1
        "...............DDDP.DDDD..N.........Dd0....dc0..................", // 2
        "..D...............DDDPDDDDND......D.dtKD...c0...................", // 3
        "...DDD.......DDDDDNNNNNNDDNNND...DNDdBrrDDDc0...................", // 4
        "....DKDD....DDDDNNDDPPPPPPADNNNDDDDDd0qqqrDG0...................", // 5
        "......DKKNNNNNNNDPPPAADDDDDDDNNNDSDDd0qqqqqDD...................", // 6
        ".........DKNNNNDADDDNNNNNNNNNNNDDDDKc0piiiqpr...................", // 7
        ".D..........DDDADNKKNNDrrrSrrDNNNNNKKDrqiiqqAD..................", // 8
        "..D........DNNNKKNNDrpqqqqiiiqDNDDADNNrqqqqSS...................", // 9
        ".....D..DNNKSDKBKNrrprrrppqiiqqNKDDPQQPrpppDdDN.................", // 10
        "......DNKSTTSDttDrrD00SSSTSSqqqrNKKNA11QPArrDDN...............DD", // 11
        "...........DDttNNDSdcc0DDSddSIqqDKDPDKNAQQQArNN..............DWN", // 12
        "..........NKttGD0cc0DDDAADDSdcIprNAPDNNKNN1QQDND.............DWN", // 13
        "........DDADttGtttDDAPQQQQPA0dSrrNNPNNPADSDP1QPD............DWWN", // 14
        ".......DNAADGtttKKACPQEEEEEPDSdDNKNPANPPDSGKNDDN...........DWWWN", // 15
        "......NPPPDttttKNACPPQEEEEEQPDcINKNNAAPQPNNNNN.............DWWWK", // 16
        ".....DPPPN0ccttDPQQPPQQQEEQQPNtSNNNNNNAAPAAQQD............DWWWWK", // 17
        "....NDPPNDc0tDPQEEEQPPPQQQQQPNDcKKNNNNNNNANPQD...........DWWWWWK", // 18
        "...DDDDNDctDDPQQEEQQPPPPPQQPPNGtKKKNDNKNAANKN...........DWWWWWmK", // 19
        "...DDDNDctNPPPPQQQQQQPCCPPPPADtDND.DNNNNAAD............KImWWWWWK", // 20
        "...DDDKttNQQPAPQQQQQQPCAACAADtDDD....DNDNKD............DSWWWWWD.", // 21
        "...DDNNGNPQQQPAPPPPPPPPAAANNDBN1AD....DNNND...........DWWWWWWWN.", // 22
        "...DDKDGDPQQPPANACPCAANNNNNNNKDCD......DND...........DWWWWWWWK..", // 23
        "....NKDGDAPPPAANNNNNNNNNNANNNNPDD...................DWWWWWWWWK..", // 24
        "....DDDDDDDPPPCANKNNNNAANNNNDPCD...................DWWWWWWWWD...", // 25
        "....DDDDDDDPPPPANNNNNNNNNKKAPAD...................DWWWWWWWWD....", // 26
        "....DDDDKDDPPPPNNKKKNNNNNDDDND...................DWWWWWWWWD.....", // 27
        "....DDDDGDDDPPANKGGGGKDDDNKD...................NDWWWWWWWWD......", // 28
        "...DDDDDDDDDADSSDDDDGGGBD....................NDWWWWWWWWWD.......", // 29
        "...DGDDDDKNDSSddDGDDKD.D...................KDWWWWWWWWWWD........", // 30
        "...DKDDDGDDSddccKKDGN..........DNNNNND..KNDWWWWWWWWWWDD.........", // 31
        "..NDDGGKGSdSSttctGDGDAD......NNAPPPPSDNDWWWWWWWWWWWDD...........", // 32
        "..DDDDDGGtttDScctGKGKPD....NNCPDDDDDDSSWWWWWWWWWWDD.............", // 33
        "..DDDDDDDDtcddSttDNDKPP..NNDDDDDDISSSSWWWWWWWWDDD...............", // 34
        ".DDSDDDDDDtcctKNAAPPNPPKNDDDDISSSTTTTWWWWWWWDDD.................", // 35
        ".DDSDDDDDDtttDNDCCAPNPPDIDSSSSTTTWWWWWWWWDDDD...................", // 36
        ".DDSDNDCAPNtDNDAPPPANA1STTTTTTiTTTS.DDDNK.......................", // 37
        ".DDSKADDDDDtKDDCPPPANAADSSSSSDDDDDNAAAND........................", // 38
        ".DDDKNAtc0tDNAACPPPPDDCDDKNNNNNNNAAPPAND........................", // 39
        "KDSDKAADDANNKNAPPPPPDD1DDNAAAANNNAAAAAND........................", // 40
        "KDSDKNADKKKKKNDPQPPPANCSNNAANNNNNNAAANN.........................", // 41
        "KDSDDKKDKGDDDNNAAAAPPNNPNKNNNNNNNNNAAND.........................", // 42
        "KDSGDDDDGGDNNNNNNNNAANKCAKNNNNNA1ANNNND.........................", // 43
        "KDDGDDDDGGDNNNNNNNNNNKKKANKNNAPP1PPNNN..........................", // 44
        "DDDGDDDDGDNNNNAAANNNNDNKKKGNDNNNDCPPND..........................", // 45
        ".DDGGDDDGGNNNNAAAAAAAAANKKKGGKKKGGAAND..........................", // 46
        ".DDKDDDDGKNNNNNAAAAAPPPPANKKGKKGGGGDAD..........................", // 47
        "..DKDDDDDNNNNNNNAACCPPQQPANKGKGDDDGDND..........................", // 48
        "...DKDDDDGKKNNNNAACPPPQQQCADGGGDDDGGDD..........................", // 49
        "...DKGDDDGNKKNNNNAAPPPPQQPPAKGDGGDDKD...........................", // 50
        "..DGGGGDDDNNKKNNNAAAPPAPQPPANKKKKGGD............................", // 51
        "..DDGGDGGNAADKKNNAAAAPAAAPPANKGGDDKD............................", // 52
        "..DDDGDGGNNAANKKNNAAAAAAAACPAKGDDDGD............................", // 53
        "..DKGDGGGKNNADNNNNNAAAAAPPPPANKGDDDDD...........................", // 54
        "..DKDGGGGKKNANNAAANNNAAAAPPPPANKDDDDKD..........................", // 55
        "..DNDDGGGGKNANKDDAANNNNAACPAAANKGDDDDKD.........................", // 56
        "..DNDDDGKKKNANKNDDDDNNNNAAAAAANKGGDDDDDDD.......................", // 57
        "..DKKDGGKKKNDNKNNNDDNNNNAAANNANKKDDDDDDDDND.....................", // 58
        "..DNDDDGKSTDNN..NNNNNNNNAANNNND..NKGGDDDDDDD....................", // 59
        "...DDDGGKNTWDN...DKKKNNNNNNNNND....KKKKKKKD.....................", // 60
        "....DDDDDDD.........DKNNNNNDND..................................", // 61
        ".....DKKKKK.............KKKKD..................................." // 62
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        'r' to Color(0xFF961950),
        't' to Color(0xFF0A6469)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawOgre() {
    val matrix = arrayOf(
        "...........................KD.............NNN..................", // 0
        "......................DNNDAAAAAD......DNNAQQEPAD...............", // 1
        "..................DDDDDDDDAPACCPPANNDPPCPPPQQQEEPAD............", // 2
        ".................DDDDDDDDLLDAPCAAAACAAAQPCCPQQPQQQQA...........", // 3
        "................DDDDDDDDDDlLDNAPPPPACCQPCQCAPQQ1QPPED..........", // 4
        "...............DDDDDDDDDIDDLLDNANNNCCAQPAQQCAPQCPQPPQ..........", // 5
        ".............DmDDDDDDDDDDDDDLLDNCCCPNCQPAPQPACQCAPQCPP.........", // 6
        "............DDDDDDDDDDDDmDDDLLLDNAANAPPCAPPPAAQAACPCCPN........", // 7
        "............DDDDmDDDDDDDDDDDGlLLNANAAPPANPPPAAPNNACCACN........", // 8
        "............DDDDDiDDDDDDDDDDGLLLDNNPAPPANCPPNAAADNAAAAN........", // 9
        "...........DDDDmDDmmDDDDDDDDDDLLLKACAPPAACPANDDDDDNNDSN........", // 10
        "..........DDDDDDDDDDDDDDDSSSDGLLLDAAAACNCCAADSSSSDDDT.D........", // 11
        "...........DDDDDDDDDDDDDDmSSDNgggDNNNAPNCCCNDSTSSDSD...........", // 12
        "........D..DDDDDDDDDDDDDDmSSDNDgDDNNNACNAAPDSSDDDDSD...........", // 13
        "...........DDDDDDDDDDDDDDDDDDNDGDDNNNAAAAACNSDDDSDS............", // 14
        ".......D......DDDDDDDDDmDDDDDNDDDLNNAANDNAANDDSSSDD............", // 15
        "......D........DDDDDDDDDDDDDNDLDDLNNAADDNANNDDSSDD.............", // 16
        "......DDDDD...DDSSTSSDDDDDNDDDLDDLNDAADDNANKNDDSD..............", // 17
        ".....DDDDDD...DSTiTTSSNNNDDSDDLGDDKDNNDDDNNDNKND...............", // 18
        "....DDDDDDDDDDDSTiTmSDNDDISSDLLGDDGNNNDDDDDDDDDD...............", // 19
        "....DDDDDDDDDDDSmTmSSDDDSSSDDLDGLGDKNDDDDDDDDDS.D..............", // 20
        "...DDDDDDDDDDDDDSSSDDKDIISDGLLGDDGDKDDDDIIDDDDD.D..............", // 21
        "..DDGDDDDDDDSDDDDDDDKDDDDDDgLDDLDGDKDDDDSSDDDDDDD..............", // 22
        "..DDKDDDDDDSTTSDDDDKDDDDDDDLDGLDGGDDDDISSDKDDDDDD..............", // 23
        ".DDDKDDDDDSSTTTSDNKDDKKGDDDGGLgGGGDDKDISSDDDDSSDD..............", // 24
        ".DDDKD....DSSTTTSNKKKKDDGGGDLDGGGGGDGDDDDKDDDSSSDD.............", // 25
        ".DDGKN...........DGGGGGGGDDDDGKGGGGGGNDDDDDDDSSSDDNDD..........", // 26
        ".DDGKKDD.........DKKKGGDDDDGKGGGGGGGDNDKDDDDDDSDNDPQEPD........", // 27
        "KDDGGKDD..........KNKKKKKKKKGGGGKDGKDNTDDDDDDDDDQPPQPQN........", // 28
        "KDGGDGKDD.........NNNKKGGGGGGGGKKGDKNS.SDDDDKNDEPPQPCPPPP......", // 29
        "KDGGDGKDDD....D.m.DKNDDKKKKDKKKTSDDTT...DDDKAQPPPFEPAAAEQD.....", // 30
        "KDGGDGKKDDD.mm.D..DKKDDDDDDKNKDT........SDKDQQPCQQPNPCAPPAD....", // 31
        "KDKGDGGKNDDD...D...NKDDDSDDDDKDDDT....TDNNAPAAAPNNNPQQAADND....", // 32
        "KDKKDDGGKND.DD.DD..DKKDDSSDDDKGDDDT..SSQANDNADNDDSQDAADDDDN....", // 33
        "DGKKGDGGDGND.DD.DD.DNKDDDDDDNKGGGDDTSDQPAPAAEPDDSDSSPDDNQQN....", // 34
        ".KKKGDDGGDKDDDDDDDDDDNKDSDDDKKKKGGDKAPNNQQPPDNNDGDDDPDDDSE.D...", // 35
        ".KKKGDDGDDGKDDDDDDDDDNNKKDDKKKKKKKKPQPAPADDGDSDDGDDDNGGGGDD....", // 36
        ".DKKKDGGGKKKDDDDDDDDDSDNKKKKKKKKNPPPSSDDDDKDSDDDDDDDDKDDDDD....", // 37
        "..KKKGDGKKKKKDDDDDDSDSSSSDKKKKKNPPDSSDDNDPDDDDDPPPADKGDDD......", // 38
        "..K..KDKKKDDKNDDDDSSSSSSSTNKKNAPPDDDDDDSQADPAQPAQPDGKGGGD......", // 39
        ".....KGKKDBDKKDDDDSSSSSSSSDNAPPPDDGDDGDKKDGDDADDDDDGKDDD.......", // 40
        "......KKKDDDDKKDDDSSSTSSSDNDQPDGDDDDDDKKKGKDDDGDDGGDKKDDK......", // 41
        ".......KKDDDDDKDDDSSSSTSSNDDDDGGDDGGGKKNDGGGGGDGGKKKKGDDDDD....", // 42
        "........DDDDDDDDDDDmSDTTSNNDGDDGDKDDDDNDDDDDGKGDDDDGGGDKDND.D..", // 43
        ".........DDDDDDDDDDSSDSTSDKKKKKKKKKKDDN...KDDKDDKKKKDGKKKD..DD.", // 44
        "..........KDDDDDDDDDTSDSDSNKKDDDDDDDDD...........NDDKNDDKDDDDD.", // 45
        ".........DDDDDDKDDDDSDDDDDDNNDDDDDDDDD...........DDDDDDKKKNDSD.", // 46
        "........DD0DDDKNDSDDDDDDDDDKD00DDDDDDD............DDDNKDNDDDS.D", // 47
        "........DD00DDNKNDDDDDDDDN.DD00DDDDDK.............DDDDDDDSSDD.D", // 48
        ".......DDD0DDDKNKDDDDDDDD...DDDDDDDD..............DDDDDSDSmSDDD", // 49
        ".......DD0DDDDNDDNKNNND.....DDDDDDDD...............DDDDSDNDDDD.", // 50
        ".......DD0DDDDDDDD..........DDDDDDDD................DDDDDDDD...", // 51
        ".......DDDDDDDDDD...........DDDDDDDD.................NNDDDD....", // 52
        ".......DDDDDDDDD............DDDDDDDD...........................", // 53
        "......DDDDDDDDDD...........DDDDDDDDDD..........................", // 54
        "......DDDDDDDDDDD..........KDDDDDISDDDD........................", // 55
        "......DDDDDDIIIDD..........KDDDDDSSSSDDD.......................", // 56
        "......D0DDDISSSSID.........KDDDDDDSSDDDDD......................", // 57
        "......D0DDDSSSSmS.D........DDDDDDDDDDDDDDD.....................", // 58
        "......DDDDIIISSI.DDDD.......DKKDDDDDDDSNDDN....................", // 59
        "......DDDDDDDDDDI.DS.D.........DDDSSSDSSDD.....................", // 60
        "........DDSDSDSSD..D..D...........DDDDNN.......................", // 61
        ".........DDDSDDSD..DDD.............DDD.........................", // 62
        "............NNNNKNN............................................" // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'L' to Color(0xFF50BE28),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'g' to Color(0xFF199123),
        'i' to Color(0xFFD2B4F0),
        'l' to Color(0xFF96DC37),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawEye() {
    val matrix = arrayOf(
        "......................DDDDDD...........................", // 0
        "...................D00UVVTQPD................D.........", // 1
        "................DBUUUVVVTPDDAD.............PP.V0.......", // 2
        ".............DB0UVUUUVVUDQSSNN............DPAD.V0......", // 3
        "...........DBUUVU00UVVV0SCPD.D............ADDPDVU0.....", // 4
        "..........0UUUU000UVVVUSWDAN..............D.PADUVU.....", // 5
        "........D0UU0DD0UUVVVUSWWWDD...00000000.....ADDUVVB....", // 6
        "......DBUU0DDrDUVVVVUSWWWWT..0UVVVVVVVVVU0..DSDDUV0....", // 7
        ".....D0UU0DDDDUVUVVVSWWWWW.0UVVVVVVVVVVVVVUD.DDDUVUB...", // 8
        "....D0UBDDDDDUVUUVVVUTWWW.0VVViidVVVVVVVVVVUBDDUVVVB...", // 9
        "....00DSDDrDUVUUUVVVVUTW.UVVVdidVVVVVVVVVVVVU0DVVVV0D..", // 10
        "..D0B..DDDD0VU0VUUVVdVUDUVVVViiVVVVVVVVVVVVVVU00VVU0D..", // 11
        ".D0D...DrD0UU0UU0DUVVdVU0VVVVdVVUUUVVVVVVVVVUUU0BU0UB..", // 12
        "DBD...DDrDUUDDUUDD0VVVdV0UVVVVVUUUVVUVVVVVVVVVUUB0BU0..", // 13
        "D.....DrD0U0D0V0NND0VVVdVUVVVVVUUUUVVVU0000UUUVU0DBU0..", // 14
        ".....DDrDUUDDUUDNNNN0VVVVVVVVVV00UVVUDCPPPPPD0UVUDD0U..", // 15
        ".....DDD0U0DDU0DNNDDD0UVVVVVVVUBUVU0CQEFEEQQPP0UUBD0U..", // 16
        "....DDrDUUDD0U0DNDDDDD0UVVVVVVUK0U0CQFWFEFEEQQP0U0K0U..", // 17
        "....DDD0U0DD0UDNDDDDD0UUUVVVVdVDNDAQFFFFEQPCACQP00KDU..", // 18
        "....DDDUUDDDU0NDDDDDD0UVVVVVVdV0KAPEFEEEPPEEPPAPP0BDU..", // 19
        "...DDD0U0DDDUDNDrrDD0UUUUUUVUVVUBNPEFQEQPEPAAPPAPDBB0..", // 20
        "...DDDUUDDD0UDADrrDN0UUUVVVVVVVVUNAPEQEPPPCPNAPAPABD0..", // 21
        "...DrDU0DDD0UDDDDDDD0UUVU00VVVVVUBNCQPQAPCNANAPAPNBNB..", // 22
        "..DDr0UDDrD00NDDDDTSB0UV0NP0VVVVVUBNAPPCACANACAADBBND..", // 23
        "..DDDUUDrrD00DDDDSWSB0UUDAQPIVVVVVUBDNAACANNNAANB0BND..", // 24
        "..Dr0U0DDrD00DDDSWTSB0VIDAPAQSVVVVVVUBBKNNNNNNNB00BDD..", // 25
        "..DD0UDDDDD00DDSWTDBB0USANNAQDIUVVVVVVUUBBBBBB0UU0KDD..", // 26
        "..DDU0DDDDD00DDTWD0BD0USANNNAAESIVVVVVVVUUUUUUUU0BNDD..", // 27
        "..DDU0DD..DD0DSWSBU0BBUVSQPNNAQAET0I0IVUVVIVVI0B0BDDD..", // 28
        "..D0UDD....D0DTTD0U0BD0UTEANAAANFQPFPPFITSSQQEDDBDSDD..", // 29
        "..D0UDD....D0SWSBU0BBKBUVSCPPNNNQCAQACPAQPPCPPNDD.DD...", // 30
        "..D00N.....DBTWD0UBBBUBBUVTFCNAACANPNAANPNANNADB...D...", // 31
        "..D00D......D..BU0B0VVUB0UVSAPQAANKANNANANPDKND....D...", // 32
        "..D0DD.........0UB0VVVU0B0UVSQEAPPKANNQNPAQQCP.........", // 33
        "..D0D.........D00BUVVU00BD0UVVSPFAAQAPFPESSSDD.........", // 34
        "..D0D.........B0BUVVU0DDD..D0UUSV0PQDPQDSI000DP........", // 35
        "..D0D.........B0DUVVUDQD.....D00UUIIU0D000B0KCQ........", // 36
        "...BD.........B0PP0UUDQD........DDDDDDDB00UUDAP....AP..", // 37
        "...D..........DPPCA0UKAD................B0UUDCA...PCPP.", // 38
        "..............DDDDA00DAD.................DBUBDD..DANAPD", // 39
        "..............DNB0DU0BDD...................0BUUD00N..AD", // 40
        "...............B0U00U0UD...................DBVVVUB....N", // 41
        "...............B0UUBUVVD....DBBDACDD.......DBVVUB......", // 42
        "................B0U00UVDDDBB0UIQQQQPAD......DVV0.......", // 43
        "................DB0UB0UBB0UUUUIPPPPQQPD.....DUUB.......", // 44
        ".................DB00K0UBUUU00BD..DDAPPD....BU0........", // 45
        "..................DBDDK0B00DD.......DNPPD...00D........", // 46
        ".....................PPDDD............DPA...AD.........", // 47
        "......................AD...............DP...APD.D......", // 48
        "........................................DD..NCPP.......", // 49
        ".........................................D...DN........", // 50
        ".......................................................", // 51
        ".......................................................", // 52
        ".......................................................", // 53
        ".......................................................", // 54
        ".......................................................", // 55
        ".......................................................", // 56
        ".....................DKKKKKKKDDD.......................", // 57
        ".................KKKKKKKKKKKKKKKKKKKKKKKKKD............", // 58
        "...............KKKKKKKKKKKKKKKKKKKKKKKKKKKKKK..DD......", // 59
        "............KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKD......", // 60
        ".............DDKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKDD.......", // 61
        "................NKKKKKKKKKKKKKKKKKKKKKKKKKKKKD.........", // 62
        "........................DKKKKKKKKKKKKKKK..............." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'd' to Color(0xFF5AD2CD),
        'i' to Color(0xFFD2B4F0),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawSlime() {
    val matrix = arrayOf(
        "................................DDDMrrrrrrrI....................", // 0
        "..............................DrpppppqqqqqqqII..................", // 1
        "..........................DDIrpppqqqqqqqqqiqqmmr................", // 2
        ".......................DIrrrrppppqqqqqqqqiiiiiimmI..............", // 3
        ".....................DrpqqqppppppqqqqqqqiiWWWWWWiqm.............", // 4
        "....................rrpqiiqprpppqqqqqqqqqiWWWWWWWiqp............", // 5
        "...................rpqqiiiqpppppqqqqqqqqqqiWWWWWWiiqI...........", // 6
        "..................rpqqqqqqpppppqqqqqqqqqqqiWiWWWWWiiqr..........", // 7
        "..................rqqqqqpppppppqqqqqqqqqqqiWiiWWWWiiqpI.........", // 8
        ".................rqiWiqppppppppqqqqqqqqqqqiiqqiWWiiiiqr.........", // 9
        "................rqiWWiqpppppppqqqqqqqqqqqqqiqqqiiiiiiqpI........", // 10
        "...............DpqiWiqpppppppppqqiiiiqqqqqqiqqqqqiiWiqpr........", // 11
        "...............rqqiiiqpppppppppqqqiWiiqqqqqiqqqqqqiiiqprD.......", // 12
        "..............rpqiiiqppprrpppppqqqqiWiqqqqqiqqqqqiiiiqprr.......", // 13
        ".............DrqqqqqqpprrrppppppqqqqWiqqqqqiqqqqqiiqqpprr.......", // 14
        "..............rqqqpppprrrrpppppppqqqiWiqqqqiqqqqiiqqppprr.......", // 15
        ".............rpqqqpprrrrppppqqqiiqqqqiiqqqqiqqqiiiqpppprr.......", // 16
        "............DrqiqpIrrrrpppppqqiiiiiqqiiqqqqqqqiiiqpqqqppr.......", // 17
        "............DrqqmIrrrrrpppprrrrrpqiqqqiiqqqqqqiiqqqiiiippr......", // 18
        "............IrqqIIrrrrrpprrCPPQPrRpqqqqqqqqqqqqqqqiiqIrrrpD.....", // 19
        "............IrqprrrrrrrprACPQEFFEPCpqqqqqqqqqqqqqqqrrPQQPrD.....", // 20
        ".............rprrrrrrrrrRCPPPQEEFFQCpqqqqqqqqqqqqprCQFWFEPA.....", // 21
        "............rrrrrrrrrrpRACCCOPQEFFFQArqqqqqqqqqqqrCEFWWWFQA.....", // 22
        "...........DrrrrrrrrrrpRCOCCCPQEEFWWQCrpqqpppqqprrQEFWWWFQA.....", // 23
        "............rrrrrrrrrrrrACCCCPQEEFWWWEQPrrppppprRPQEFWFQEPA.....", // 24
        "............rrrrrrrrrrprrACCCCPQEFWWWTQQCCCrpprRCPPPQEEPQAA.....", // 25
        "............rrrrrrrrrrprrRACCCCPQQEFFEPQQOCrpprACCCCPPQQPrr.....", // 26
        "............rrrrrrrrrrrrrrrACCCCCPPQEEEPCrrpppprrRACCCCArrpr....", // 27
        "...........rrrrrrrrrrrrppprrRACCCCCPOCCrrrppppppprrRAArrpqqpr...", // 28
        "..........DrrrrrrpqprrrrrpppprrrCCCCrrrppppppppppppprrpqqiiqr...", // 29
        "..........IrrrrrrrqqqprrrrppppprrrrrrppppppqqqqqqqqqqqqqiiiqD...", // 30
        "..........rrrrrIrrqqqqprrrrrrppppppppppqqqqqqqqqqqiqqpiIiiqI....", // 31
        ".........rrrrIIppppqqqqpppppppppppqqqqqqiWiqqpIirqqqrNSNIqqD....", // 32
        ".....rrrrrrrIIqqqqppqqqqppppppqppqqiiqqqqiqpprNSKrrrMMNMrppD....", // 33
        ".....rpprrrrrpqiiqqpppqqqqqpqqpmmrpIrmmpqqrrNrrrrrrrpppppprD....", // 34
        "......rrrrrrrpqqqqqqpqqqqprpqpNDSrNDDDSNrrDNrqiiiiiiiqqqpprr....", // 35
        "......DrrrrrrpqqqqqqqqqqpIDDIIDKrqpSTNrrNITrqiWWiqqiiiiqqpprD...", // 36
        ".....DpprrrrrrpqqqqprrrINSmrrSmriiqpppqqprIqWWWiqqqqqqqiiqqprr..", // 37
        "....DqqqprrrrrrpqprNDDNrrImqqqqqqqqpppppprpiWWiqqqqqqqqqiiiqqpr.", // 38
        ".rrrqqqpprrrrrrpprNNTSrqqqqqiqqqqqppppppprqiiiqqpqqqqqqqqqiiiqpD", // 39
        "DrrrpppprrrrrrrrppqqqqqqqqqqiqqqpppppppprpqiqqqpppqqqqqqqqqqiqpI", // 40
        "..DDrrIrrrrrrrrrrrppppqqqpqqqqqqppppppprrpqqqiqppppqqqqqqpqqqqr.", // 41
        ".....rrrppprrrrrrrrpppppprpqqpqppppppprrpppqqiqpppppqqqiqpppqp..", // 42
        ".....rrpqqpprrrrrrrrrrppprpqppqpppppprrppppqqiqppppppqqiiqpprI..", // 43
        "....Ipqqqqpprrrrrrrrrrrrrrppppqppprrrrrppqqqqiqppppppqqqiiqpr...", // 44
        "...IqqqqqqpIrrrprrrrrrrrrrppppqppprrrrpqqqqqqiqppppppqqqiWiqpr..", // 45
        ".DrqqqqqppprrppprrrrrrrrrppppqqprrrrrpqqqqqqqiqppppppppqqWWiqpr.", // 46
        "DrppppppppppqqpprrrrrrrppppppqqprrrrrpqqqqqqqiiqppprrppqqqiiqpI.", // 47
        ".DDDDDrprppqqqqprrrrrrrpppppqqqprrrrrqqqqqqqqWWqppprrppppqqqpD..", // 48
        ".......DDpqqqqprrrrrrrIpqqppqqprrrrrpqqqqqqqqiiqppprrrppppppI...", // 49
        ".......DpqqqqpprrrrrrrIpqqppqqqprrrrpqqqqqqqqqqqpppprrrppppD....", // 50
        ".......qqqqqppIrrrrrrrppqqpIqqqpprrrrqqqqqqqqIpppppppppppppD....", // 51
        "....DDmiiqqpppppqprrrrpqqqpIqqqqprrrrqiqqpqiqqpppppppppppprD....", // 52
        "..DDpqiiqqppppqqqprrrrpqqqpIpqqqqprrpqiiqpqiqqqpppppppppprD.....", // 53
        ".DpqqqqqpppppqqqpprrrrpqqqpIpqqiqprrpqWiqppqqqqprrrrpppppD......", // 54
        "NrpqqqqpppppqqqppppppppqqqppIpqqpprrpqiipIppqpprrrpppppppD......", // 55
        ".DDDDDDDDDpqqqqpppppppppqqprrrppppprIpqqpIrrprrrrpppppqqprD.....", // 56
        "........DrpqqpprDrpprrpppIrrrrrrrrrrDIIIIpprrrrpprpppqqqqqpD....", // 57
        ".........DNNNDN...NNNNDrprrrrrrDNNN..rpIIpprprpprNNpqqqqqqqpDD..", // 58
        ".......................rrrrrrr.......rppprDrppprD..DppqqqqqpprD.", // 59
        "........................NNNNN.........NNND..NNND....NNNNNNNNNN.." // 60
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawBomb() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        "................................................................", // 2
        "................................................................", // 3
        "................................................................", // 4
        "................................................................", // 5
        "................................................................", // 6
        ".........................QPP....................................", // 7
        "........................PARNN.....D.............................", // 8
        ".......................1ARNKKN....RR.....PAR....................", // 9
        ".......................1CRRNNNR....P....PARNN...................", // 10
        ".........CRA....RPQ....1CRRRRAR........ECRRNKA..................", // 11
        "........CRNND...PAC....F1CCRC1Q........E1CRRNA..................", // 12
        ".......ECRNNR...........F11C1Q..........2CCCCP..................", // 13
        ".......F1RRRR............F44Q...E.......42212...................", // 14
        "........Q1ZZP.........EF.......E3.....EF.FQEE..QRN..............", // 15
        ".........2JQ..........E3F...r..E3....E3F...FE...CRA.............", // 16
        ".................EE..AQ333.rR.E33E.A.334.A.J....................", // 17
        ".................EJE.RRJ333ARC33JJRNQ333CRJJ..P.................", // 18
        "...............D..QJQRROJ33CRAJJJJCRJJJ3JRJQ.PQ.................", // 19
        ".............FF..ARJ3JZOJJ3JRCJJJJJCJJJJJCOCZC..................", // 20
        "..............PJEPRO33CZOJJJZCOJJJJJJJJJJOXZZA..................", // 21
        "..........r....OOJOOJJCZCOOJOZZOOJJJJ2332JOXZA.C................", // 22
        ".........PA...CROOJ3JJCRCCZOOXZZOOJJ233332JXCNN.................", // 23
        "............PCZZZOOJJJCRRRZZXXXCZOOOJ33333JXZRN.................", // 24
        "..............AZOOOOJJZZRRRZZXXOOZOOOJ3333JOZRND................", // 25
        "...............ROOOZOOZZRRRRZZZXJJXJJO2222JOZZZN................", // 26
        ".........D.....RROOCZXZRRNNRRARZOJOOJZOJ2JJOZOOZN...............", // 27
        ".........RN..RNRROOZZZZARNRRZZZZZXOOJJZOJJJOXOOZN...............", // 28
        "........NKNNNRNNNCOZRRRZZRRZZZXOCZXXOJXZOJJOZZZRNK.NN..NN.......", // 29
        ".......NNNNNRRKNNRZARRRRRRRRZZXOJOZXZOOZOJJXZZZRRNDNRNNNKN......", // 30
        "........NNNRRNNNRRRZZZZRRNNNRZZXOJOZXXXXXOOXZCXCRNNKRNNNNNN.....", // 31
        ".......DRRRNNRNNZRZZOOXZRNKKKKNRZXJJZXXXXXZZCOXRNNNNNNRRNN......", // 32
        "...........DNRRNRRZZOOZAARKKKKKKRZOJAZZZZXZCOOCNKKNRNNRRNN......", // 33
        "............DNRRRZRZXZRRRRKKNDDDARZOOCRZZZAOOANKKKRRN...........", // 34
        ".............DNZZANROOOOZRKKKDDDCARZJZNRZRROCNDDKKRN............", // 35
        ".............DNRZZNNXJ33OANNKNDDCOCARRRAZRCCN1DDKNND............", // 36
        ".............DNNACNKZOO23332OANKA12ANRZZZRAN1CNKAAND............", // 37
        ".............DNNRRNKCZZOJJ222JOARRANKRZZXACNNKNCJOND............", // 38
        ".............DNNRRRKRZRZXOOOJJOXARNNNRZCOZZZRRZOJOND............", // 39
        "..............DNNRRKKRNNZOOZXXXXZZZZZXOJJOZAZXZCZRD.............", // 40
        "..............DNNRRNNRKKCOCZZZZZZZZZCZZOOXZZRZRNOAD.............", // 41
        "...............NNNRARRKKACKKRZZRZZZZZZZZZZRRKCNDCN..............", // 42
        "...............KNRRCZRKKRAKKNZRKNZRRZZNRZNNNKRPSRN..............", // 43
        "................NNRAZZKKNRKKKRNKKRRKRZNKAKKKKNPSN...............", // 44
        "................NNRAZZNKRRKKKNNKKNNKNRKKRKKKNRPSN...............", // 45
        ".................KNRRZZNCCNKKKKKKKNKKNKKKKNNNZP.................", // 46
        ".................DNNRRZZCORKKNNKKKKKNKKNKKRRAOD.................", // 47
        "..................DNNRRZCORNKRNKKRKKRKKRNKRRCA..................", // 48
        "...................NNRRACJCZAZNKRAKNCKKRRRRRN...................", // 49
        "....................NNRRAOORZZZRZZAZZRZAZZRN....................", // 50
        ".....................DNNRRRRRZRRZZZZZZRRRND.....................", // 51
        "........................NNRRRRRRRRRRRRNN........................", // 52
        "..........................NNNRNNRRNNNN..........................", // 53
        ".............................KKKKN..............................", // 54
        "................................................................", // 55
        "................................................................", // 56
        "................................................................", // 57
        "................................................................", // 58
        "................................................................", // 59
        "................................................................", // 60
        "................................................................", // 61
        "................................................................" // 62
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'X' to Color(0xFFF5372D),
        'Z' to Color(0xFFD21E19),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawSahagin() {
    val matrix = arrayOf(
        "...............DK...............................................", // 0
        "..............DD0DD.................D........D..................", // 1
        "...............DKB0D...............DU0DD....D0D.................", // 2
        ".................DDUD...............DI00BDDD0UD.................", // 3
        ".................DDIU0..............DSIIVIISSIU.................", // 4
        "..................DDIV0..........DDD.mSIIIVSSIVD................", // 5
        "...................DIII0D.......D000mmmmSIIVSSII................", // 6
        "........D.........D..II0UDD......KBUVmmmmSIIVIII0...............", // 7
        ".......D0D......DD.mm.II00BDDDDD...DIIVmmmSIIVUU0D..............", // 8
        "........D0DDDDDD.mi.m..ID0BK0UUU0DDISIIVVII0ViiVU0DD............", // 9
        ".........D00DISmmi.mm..IDNBViiVVV00ImmSI0UVUVViiWVBK............", // 10
        ".........DDIVmmmmmmmm.IDD0VViiVVVVU0DIIU0UVVUVU0SUVBD...........", // 11
        ".........DDI0VSSSSSSIIDD0UVVVVVUUU0000BUVUVVU00UUU0VU0D.........", // 12
        ".........DDS00UVIISIDD0UUUUUUUU00000UU0U00UU0BUUSiU00BD.........", // 13
        ".........DSmSI0UUIDD0UVVVU0B0000BBB0B00UUUUU00UISSSBUUD.........", // 14
        "........D..mmmID0BB0VVVVU0BBBBBDDDDBBUVVVIU0000DSSSBUVVD........", // 15
        "........D....mSIDBUVVVUUBBKBBDDDDDDDBUIIIII0U00BDDDUU000........", // 16
        "......DD.mmmmmmD0VVUUU0BKKDDDSSDDDDDKBISSmVUU0B0000VVU0UN.......", // 17
        "..DDDDD.....mmI0VVVVU0BDDDDDDSTSDSSDDD0mmVV0B0B0VB0UUU0D........", // 18
        "DDDBDD........D0VVViVUBDDDDDDSTSDSSSDDBVVIIM0V0BBBUBUD0D........", // 19
        ".DD0II0I...IIIDB0UVVVVBDDDDDDSSSDDSSSDDB0SIUUU0KKB0SD.D.........", // 20
        "...D0IUUI0DDDDDBBUVVVVUDDDDSDDSSSDDSDDDDBDSUU000KBD.............", // 21
        "...DDIIIIUU000U0BBUVVVVDDDDSDDDDSDDDDDDDDBU0B00UBKD.............", // 22
        "....DSSIDI000UVU0BBUVVV0DDDDDDDDDDDDDDDDDD0KKBB0U00B............", // 23
        "....DSSSSSIDB00BBBBBUVVUBDDDDDDDDDSSDDDDDDDBBKKBBBBD............", // 24
        "....DSSSSSSSIBBBB00BBUVVUDDDDDDDDDDSSSSSDDDBBBDD................", // 25
        "....DSSSSSSSID0BBBBDDB0VVUBDDDDDDDDNDDDDDDDB000D................", // 26
        "....DSSSSSSIDD0BBB0DDDDUVVU000DDDDDSSSSSDNDBBB00D...............", // 27
        "....DSSSSIIDDD000UUDDDDDUVVVVVU0DDTWWWW...DKKB0UUD..............", // 28
        "...DDIIIIIDDDB0000BDDDDKBVVVVVVU0DTWSS......DKBUUUKD............", // 29
        "...DDIIDDD00B00BB0000DDBB0VVVVVVI0DSDBBD......DBUU0UD...........", // 30
        "...DDDMI0M00BBMBBBUU000UBBVmIVmIVV0BUVVUD......DB00U0N..........", // 31
        ".DKIIM0000MDDKBDB0000VVVUBUVSSmmSV00UVVV0D......DDBUVD..........", // 32
        "DDDDB0MDSSIIDDBBBBB0UUUUVBBVmSUVSIUBUUVUD.........D0UVD.........", // 33
        "...DDDDSSSSSSDBBBBBM0000UBBUVSIVImVBUUUBD.........K0UVVD........", // 34
        "......DSSSSIDDBDKKBBBBBBBBK0VImmD0VBUUBBD.........DD0UUVBD......", // 35
        ".......DSSIID00BKKBMMBBBBBKSmDVIKBU0UBBBBDD........DB00UVVDD....", // 36
        ".......DDSIDD00BBBBM00BBBBKUVD00BB0UBBB0UU0D........DD0VVVVVD...", // 37
        "........DIDI0DKKBBKBBBBBKKKB0KKB0UUBKBB0000BD........DDVVVUVVD..", // 38
        "........DDI0DDDDDBBBBDKKDDDDDKB0U0BKBBBB00BBD.........NUViIIDV0.", // 39
        "........DDIDDDDDDKBBKDDD.....KB00BKBBBBBDDBK..........DBUiSSSUVK", // 40
        "........D0DDDDDDDDBKD......DKB0BBBBBBKDDDDD............K0iD0mDVK", // 41
        ".......DDDDDIDDDD0BD....DKKKB00BBBKDDDD................D0iDBIITK", // 42
        "......DDDDDDDDDDDMD....DKDKK00BKDDDD....................DVD00ITN", // 43
        ".....DDD....DDDDMK....DKKKKBBBKDD.......................KBDDU0SD", // 44
        ".....DD.......DDMK....KKKKBBBKN.........................DDDIUUD.", // 45
        "..............DDDD....D0000UUD...........................DDIIUD.", // 46
        "..............DDK.....KB0UUVUBD...........................DIIVD.", // 47
        "..............DBK.....KBB0UVVUB..........................D00IVD.", // 48
        "...............DD.....KBBBBUVVUBK........................D0DU0D.", // 49
        "...............D......KDDKK0VVUUU0......................D0DDUD..", // 50
        "......................KM0KND0U0UUI0D.....................D..D...", // 51
        ".....................DDM0D..KUV0UV0U0D..........................", // 52
        ".....................DBDD....DUV0UV0U0D.........................", // 53
        "....................DDIN......DUV0UU000.........................", // 54
        "....................DDID.......DUV00U0U0D.......................", // 55
        "....................DDD.........DU00UU00D.......................", // 56
        ".....................DD..........BUIU0D.........................", // 57
        ".....................D...........DU0I0D.........................", // 58
        "..................................BU............................", // 59
        "..................................D0............................", // 60
        "..................................DV............................", // 61
        "...................................D............................" // 62
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'B' to Color(0xFF1E3782),
        'D' to Color(0xFF4B4B4B),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawCockatrice() {
    val matrix = arrayOf(
        "KKK...............DKKK..........................................", // 0
        ".D0DD..............DDBDD........................................", // 1
        ".DD00DD.............DD0BDD......................................", // 2
        "..DDDIIDD............DDS00BDD...................................", // 3
        "...DDSSSIDD....DD.....DDSS00U0DD................................", // 4
        "....D...mVIDD..DDDDD...DDSSSSI00BD..............................", // 5
        "DD...D...VVSIDD..DDDDDDNNDDSSSSIUU0D............................", // 6
        ".NDD..DD...mVV.DKKDSSSSSSDDDDSSSSS0IID..........................", // 7
        ".DDDDDNND.....m.I0DDDSSSSSSDDDDSSSSSSI0.........................", // 8
        "..DDDDDDDD......mimSIDDDSSSSDSSSSSSSSIVID.......................", // 9
        "...DNDSSS.D.........imVDDDDSSSSSSSSSSS0VI.......................", // 10
        ".....DND....DD.......mimSI0DDDSSSTSSSTUU.D......................", // 11
        "DD.....NND....D..........mVSS0DSSSSSDSTVUI......................", // 12
        ".NDDDDDDDDD....DDDD.........imSDSS0SSSS0UID.....................", // 13
        "..NDDNDDDDDDDDDDSS...........mmSStcSSTSSUUD...............D.....", // 14
        "...DNDDDSDDDSSSSS..............VmSS0SSSS0U0.......D......DN.....", // 15
        "....DDDDDDDSDDDDDDD....D......DIVimS0DSSUU0D......DN....DN......", // 16
        "......D.NKNDDDDSSSDD......D.....ISimD0cc0U0D.......NA...NA....D.", // 17
        ".........DSDNDDSSDDD......D......DSmS0cc0D00..DD...DA..DNP...DD.", // 18
        "......DDDDKDDDDNDDS.DD.....DD....DDSi0D0tD00D..DN...N..N1A...N..", // 19
        ".......KNDDSSSDDDD.DDD......DD....DSiVBD0tBBB...DD..D..DPN..N...", // 20
        "........DDDDDDDDDDDDS.D......D.......iU000DD0D...NA.N..NQD.DA...", // 21
        ".........DDDDSSDSDDS..DDDDDDDDD...F..mV0B00BBBK.DBD1AA.NQNDD....", // 22
        "...........D.NNNDDDDDDDDDSSSSS.......IVVBBB0BB0BB00SQD.D2NK.....", // 23
        "..............DD.DDDDDSDSSSSS.DD.....0VVVBBUUUUUUUUSSUUIQDN...N.", // 24
        "..................KNDDDNDDDD..DDD.....0VVUBBUUUUUSSSTSmSSV...NP.", // 25
        "................DDNNDDDDDDDDDDDDDD....DUVV0B0UVVSSTTTTTmmUV.N1A.", // 26
        "................DDNDDDSSDDNDDDSSDD....D0VVUB0UUVSTTTFTVUTmVPNQN.", // 27
        "..................DDDKNNKNDDSSSSDDDDD..DUVVUB00UISTTFTUB0iTCNQN.", // 28
        "....................DKKKKKDDDDDDDSSDD..D0VVUB0DDDSSSTTSBBUTSA2N.", // 29
        "..................DKBBUUUBKKKKDDDDDD.DD.DVVU0SDDDDDSSST0B0VS11N.", // 30
        ".......BBBBBB...DKBBUUUUUUU00DDDDDDDDDD.DDVU0SDDDDDDDSSUBBUS1AD.", // 31
        ".....0BDDDDB00BBB00UUUUU0BBBDDDDDDDDDDDDDDD0DDDDDDDSDDDDADUSPK..", // 32
        "....00STWWDKKB000BBBBBBDDDDDDSSSSDDSSSSSDDDDDDDDDD..AANNAP0SDD..", // 33
        "...0IWWWTT0BKKKKKKBBBKKDSSDDSSSSSSSSSSSSSSDDDDDDDD.NCNKANr0SN...", // 34
        "...IiWWi0BKKKKKKBBBBKKKDSSSSSTTSSSSSSSSSSDDSDDDDD..NADK1NDUDN...", // 35
        "..B...i0DDBBKKBBBKKKNKNSSSSSSSSSDDSSSSSSSSSSSDDD...KD.DACPCND...", // 36
        "..B...0DTUBBBBBKKKKNNNNSSSSSSSDDDDSSSDSDDSSDDDD....N...NPAAD....", // 37
        "..I..0KTIBBBBKBBBKKKKKDSSDDDDDDDDDSDDSSSDDDDDD.....D...N1AD.....", // 38
        "...I.0DTIB0KKKBKBBBKDNNDDDDNKKKDDDDDDDDDDDDDD..........NAD......", // 39
        "....0KT0BUBKKBBBBKKNDDNNNDNDDDNKNNDDDDDDDDD...........NAD.......", // 40
        "....0KDBBUBKB0BKKKDDKDNAAKD.DNKNNKKKKDDD.............DDD........", // 41
        "....BKDDBUBKBBDBBDDKKKDNAN....NNDDNKD...........................", // 42
        "...0BD.DKU0B0B.mKSKKKKDAN......D..KNND..........................", // 43
        "...IB..DKBUBBD.mKDKKPCNAND.....D..KDAD..........................", // 44
        "..B.D..DBKBBK..IKDKDNNN1NND.......DSN...........................", // 45
        "..B..D.DBKB0B...KSKDD.DCANA.......DDD...........................", // 46
        "..B..D..DBKBB...DDD....PPDN......D..D...........................", // 47
        "..0...D.DBBKBB...ID....PDDN........D............................", // 48
        "...0....DBBBDK00...D...NDDN.....................................", // 49
        ".........DBTVB.IBB....N.KDN.....................................", // 50
        ".........DBimB.......NDDDND.....................................", // 51
        "..........DDTmD.....DD..D.......................................", // 52
        "...........DBT.K................................................", // 53
        "............DD..DD..............................................", // 54
        "..............I................................................." // 55
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'c' to Color(0xFF14A5A5),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'r' to Color(0xFF961950),
        't' to Color(0xFF0A6469)
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

    drawMonsterMatrix(flip(matrix), palette)
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
        ".....................................DKKKKKN....................", // 0
        "..................................DNNKKKNNKNDDD.................", // 1
        "................................DKNNNNNNNNNNDDDDD...............", // 2
        "...............................DNNNRRrCRRNNNNDSDDD..............", // 3
        "..............................DNNRRRZCCCRRNNNNDSSDD.............", // 4
        ".............................DNNNRCRCCCCCRRNNNNSSSN.............", // 5
        ".............................KNNRCCRRCOOCRRRNNNSTTDNDDD.........", // 6
        "............................DKNRCCCCRRPJJCRRRNNNTFDKKNNND.......", // 7
        "...........................DKNNRCCCCRRCCJCZRRNNNTWSKNNDNKD......", // 8
        "..........................DNDKNNRCCCRRRCCCCRRNNNTWSKNDDDNND.....", // 9
        "..........................KNDNNNRRCCPCRRCCCRRNNNTWSKNDSDDNN.....", // 10
        "..........................DKDDNNNRRCJPARZCRRRNNDTFDKDSSTSDND....", // 11
        "...........................DNDDNNNNRCRRRRZRRNNNSFSNKDSDSSDDN....", // 12
        "............................NKDSDNNNRRRNRRRNNNDTTSKKDDSSSSDN....", // 13
        "......................D...D...DDTSDNNRNNNNNNNNTTSDKKDDDDSDNN....", // 14
        ".......................DN..DD.DKSTSDNNNNNNNNNSTSDKKNNDDNDDN.....", // 15
        ".....DKKKKK............DKN.DKKKKKSTSDNNNKNNNSTSDKKKKNNNNNNN.....", // 16
        "...DKKDDDDKKD...........KKNKKKKKKNSTSDKKKKNSTSDKKKKKKNKNKK......", // 17
        "..DKDD0SS0DDKK..........KKNKNKNKNNNSTSNKKNSTSSDDKKKKKKKKK.......", // 18
        ".DKDDKDDDDDDDDD.......D..KKNKNNDDDDDTTDNNSTSSSTSDKKKKKK.........", // 19
        ".DDKKKKKKDDSSDKD.......DNKKKNDDSSSSSSTSDDSSSDTTTTDK.............", // 20
        "KKKK....KKD0S0DK.......NKNNNDDSSTTTTSSTSSSSSDSTTTSNN............", // 21
        "KDK......KDDDDKKD.......KKKNDSSTTWWWWWWFTSSSNDSSSTDK............", // 22
        "KKK......KKDDDDDK.......KKKDDSTSTTTTTTTTTSSDKKDDDSDK............", // 23
        ".K.......KKDSmSDK....DDNKKKNDSSTSSSDDDDSSDNK13NKNDNK............", // 24
        ".D.......KDDSS0DDD.DDDDDDNNDDSTSSDDDDDDDDDNKD1KKKDK.............", // 25
        "..N.....DKDD00DDKKKKDSTTSDNDSSSSDDTTWWSDDDDDKKKKKNK.............", // 26
        "........KKKKDDKKKKKNSTWWTSDDSSSDSTWWWTTSDDSSKKNNKK..............", // 27
        ".......DKDD0SSDKKKKDTWWWTSDDSSSSSTSDDDSTDDSSDKDDK......DKKK.....", // 28
        "......DKDD0mTSDDKKNDTWTTSDDDSSSSTSDKKKDSSDSSSDSSKD....DKKKKKD...", // 29
        ".....DKKDD000DDDKKNSSSSSDNNDDSSTSDKKKKKSTSTSSSSTSKKNNNKKKKKKKN..", // 30
        ".....KKKKD0DKKKKKNDSSDNKKKNNDDSTDKKKKKKDSTFFSSDTTNKKKKKKKKKKKKN.", // 31
        "....DKDDDSSSDKKKNDTSNKKKKKKKNNDSDKKKKKDDDSSTTSDSTSKKKKKK..KKKKKK", // 32
        "....KKDDDmTSDDKKDTFSDDKKKKKKKKNDKKKKKKTTDNDDDSNDSSNKKK....KKKKKK", // 33
        "....KDDDSTWTSDKNSTFTSSDNKNDDNKNNKKKKTDTSND.DDDNNDSDN......KKTKKK", // 34
        "....KDDDSTmS0DKKDSSSSSSDDNNSSDKKNDKKTDSD...DTTDNNDDNDD....KK.NKK", // 35
        "....DKDD0S0DKKKKKKKNDDDDSSNNSSDKDSKKTTD....DTD..NDNKRPD...N..KKK", // 36
        ".....KKDD0DKKKKKKKKKKNNDDSSDNDDKSSKDDTD....DTD..DTSNCrN......KK.", // 37
        ".....DKKKD0DDDKKKKKKKKKKKDTSKKDKSNKSKN......D...DTD.DN.......K..", // 38
        "......KKDD00DKKKNDDDNKKKKKNSDKKKDKKTNND.....D...DD..........D...", // 39
        "......NKDDDDDKKDDDDDDDKKKKKNDKKKDNDTNRN.D.......DD..............", // 40
        ".....DKKKKDKKKDDDDSSSDDKKKKKNKNDDDDTKNRND.......D...............", // 41
        ".....KKKKKKKKNNDDSTTSSDDKKNNKKDSSDDDKKNKDD......................", // 42
        "....DKNNKKKKKNDDSTTTTSSDNNDDDNDSTSSDDNKNSD..D...................", // 43
        "....NNNNNKKKKDNDSSTWTTSSDDDDDDDSTFSSSSNDTD.D.D..................", // 44
        "....NNNNNKKKKNDDSTWWWTTSSDDSSSSDSTWTSSSDTDNN.D..................", // 45
        "....NNNNKKKKKKNDSTWWWTTSSDDSTSTSSSTWTTSDDNKD.D..................", // 46
        "...DNNNNKKKKKKKNDSTWWWTSSDSSSTTTSSSTWWWTSSDDDN..................", // 47
        "...DNNNNKKNNNKKKDSTTTTTSSDSSTTTTTTTTTTTFTTSSDDD.................", // 48
        "...NNNNNKKNRRNKKDSSTTTSSDDSTTTWTFFTTSSSSSDDNND..................", // 49
        "..DKNNNNNKNRrRKKNDSSSSDDDNSTTTWWWWFTTSSDDNKN....................", // 50
        "..KNNKNNNKKRRCNKNDSSSDDDNNDTTWWWWWWTTTSDDNN.....................", // 51
        ".DNNNKKNKKKNCCNKNNDDDDDNKNDSTTWWWWWFTTSSSDN.....................", // 52
        ".NNNKKKKKKKNCPRKKNNDNDNKKNDSTTWWWWWFTTSSSDN.....................", // 53
        "KKKKKKKKKKKKPORNKKKKNNKKKNDSSTFFWWFTTSSSSDN.....................", // 54
        ".DKKKKKKKKKKPJRNNKKKKKKKKNDDSTTTTTTTSSSSDDD.....................", // 55
        "..KKKKKKKKKKCJRNNNNNKKKKKNDDDSSSTSTSSSSDDN......................", // 56
        "....NKKKKKKKRCCRNRRRNKKNKKDDDDSSSSSSSSDDND......................", // 57
        "......NKKKKKRCOPNROPRNRCRKNDDDDDDDSDDDNND.......................", // 58
        "........KKKKNRCPRRCJCRCCCKKNDDDDDDDNNNND........................", // 59
        ".........DKKKNRRRNRRRNRRRKKKKNNNNNNNKKKKKKKKD...................", // 60
        "..........KKKKKNNKKNNKNNKKKKKKKKKKKKKKKKKKKKKKKKKK..............", // 61
        "...........DKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK.....................", // 62
        ".............KKKKKKKKKKKKKKKKKKKKKKKKKK........................." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '3' to Color(0xFFFFDC23),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'F' to Color(0xFFFFE1AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'Z' to Color(0xFFD21E19),
        'm' to Color(0xFFA578D7),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawDarkKnight() {
    val matrix = arrayOf(
        "......................D......................................", // 0
        ".........................................0...................", // 1
        "......................DDD...D.......D....D..D...DND..........", // 2
        "......................DDD...D.......D..0D..D...DNNDD.........", // 3
        "........................K....D.....00m0K..DDD.DKNNSD.........", // 4
        ".......................DDD...D.....0mDK00KDS.DDDDDKDD........", // 5
        ".D.....................KGKDD.DD...D0DKKDKKS.D.DSDNKKD........", // 6
        "..D...................DKKDSDDKD...DDKKKKKKKN..DDKKKK.........", // 7
        "...D.................DDDDSKKKKD....DDDKKKKKND.DKKKKD.........", // 8
        "....D.................DNDSSDKD....D.DKKKKSSADDKKDD.......D...", // 9
        ".......................NSSDKKD...DKDKKKKKDSDDKKKDD....D..D...", // 10
        "......D.................DKKKD.....DDDKKDKDDDKNNKK....DD.D....", // 11
        "......DD............DD.NDSKK.....DIDKKKDSSDNNNKKD...DKDD.....", // 12
        "...................DKKD.DKKKKDKNKKDDKDDKKKKKKKKKDDKN.DD......", // 13
        "........D.........DDDS...KKDDKKKKKDDKDSDNKKKKKKKKKT.D........", // 14
        ".........DD.......NKKKKKD...DKDKKDDKDSDSDKKKKKKKKDNDDD..DD...", // 15
        ".........DDD......DDDKKKD..DKKDKKIKKDSSDDNKKKKKKKSDDDDDD.....", // 16
        "...........D.DD.DD.DKKKKDKKKKKKKKIKDIDSDDKKKNKKKSDD..........", // 17
        "...........DDKDDKDKKKKKKKKKDDKKKKDDDSDDDNKKKDNKDSN..DDD......", // 18
        ".....D......DDSDDKKKDDDKKKKDKKKKKDDNDmIDNKKKDDNKKKKN....DD...", // 19
        ".....D......KKDSSDDTWDDNKKKKDDDKNWSNNDSSDKKKDDNKD.N..........", // 20
        "....DD......NDKKDSDDDTDDKKKKKKKKNTWDTSKDNKKKNDNKND...........", // 21
        "....ND...DD..DKKDDSTTDTDDKKKKKKNNDWTDNNDKKKKNDNND............", // 22
        "...DN...m0DK..D..DSWWTDTSNNNNKKAKNSDTANDKKKKNNNKD............", // 23
        "...DD.m..0KKK....D.....DTTNNKKKNKKKPPKNNKKKKKNNK.............", // 24
        "...KD..Im.DKKD....DD....DDNKKKKKKKNSAKNNKKKKKNKKK............", // 25
        "...ND.DDIDKKKKDD.DKND..FDNKKKKDDKKKPPKNNKNKKKNKKKD...........", // 26
        ".mDK.IDDDDDKKKNNCNKKND..FPNKKDSSDKKANKNKKNKKKKKKKK...........", // 27
        ".mDKDDKDISDKKKNNANPANNND...KKDDDDDDSSKNKKNKKKKKKKK...........", // 28
        "..IKKKKDDDKKKDKDNNANOCNND...KKKKDSDKDKNKKKKKKKKKKKK..........", // 29
        ".DDDKKDDDNKKTSNDNNNNNKKNN....KDDKDDKNNNKKKKKKKKKKKKD......D..", // 30
        "..DKDDDDDKKTWSDDNDNKKKKNAN...DSDKKKKNNNNKKKKKKKKKKKK....DNDD.", // 31
        "...DKDIDKKKTSDDDDDDNNKKARNDD..DKKKKKNSNNNNKKKKKKKKKKD..DKKDN.", // 32
        "...DKKKKKKKTSDDDDDDNNKKANKKND..KKKKKKDDNDNNKKKKKKKKKD..KNKNN.", // 33
        "...DKKKKKKKTDDDDDDDDNKKNKKDDDD..KKNNNNKKNNNNNKKKKKKKD.KNNNKK.", // 34
        "...DNDKKNNKTDDDDNNDNKKKKKDDNDKD..KCANNKKKKKDNKKKNKKKKKNNKDND.", // 35
        "...DDSKNKNKKKDDDNNDNKKKKKNDNDKKD..ANNNNKKKKNNKKKNKKKKNDNKDND.", // 36
        "...DDTDKKKDKKDDDDNDDKKKKKKDNKNNKD.ENNDNNKKKKNKKKNKKKNNNKKDKK.", // 37
        "....KDTDKKKNKDDDNNDDKKKKKKNKDANNKD..NDNKNKKKNKKKNKKKNNNDDDNK.", // 38
        "....DDD.DKKKKDDDNNNDNKKKKKDKNKKKKKD..NKKNNKKKKKKKKKKKND..DNK.", // 39
        "......DD.DKKDDDDNNNNNKKKKKKDKKKKKKKD..KNNNNKKKKKKKKKKD...DNK.", // 40
        "......mD.DKKSSDDNNNKKKKKKKKSKKKDKKKND..KNNNDKKKKKKKKD....DNK.", // 41
        ".......D..DKSSNDNKKKKKKKKKKKKKKDSKKNKD..NDNDKKKKKKND....NDNK.", // 42
        ".......D..DKKDNNKKKKKKNKKKKNNNNKDSKKNKD.DNDDKKKKDDD....NKNDN.", // 43
        "......D....KNDNKKKKKKD.KKKKKDDDDDKKKNNND.DDDKKDDDD....NKKKK..", // 44
        ".....DD....DDKKKKKKD...KKKKKK.DNNND...DDD.DD..........DNKKKD.", // 45
        ".........DDNKKKKND...DDKNNNND..........DDK.D..........DDKKKD.", // 46
        "........NDDKKKKD...DKKKNDDDD...........DDDK.D.........DDDNN..", // 47
        "........DDKN.KK....KNKKD................NDKKD..........DNN...", // 48
        "........DNKD.DKK...DKKKD.................NNKK................", // 49
        "........DNKD...DK....DKNND...............DDKD................", // 50
        "........DNK....DDK.....DNNKD..............NKD..D.............", // 51
        "........DKK..D..DN......DNDK..............DKD..D.............", // 52
        ".........KK...DDN.......DKNDD.............DKD................", // 53
        "........DNK..............DKNND............DND................", // 54
        ".......DDKK..............DKKNKD...........DNDD...............", // 55
        "......DDKKK..............KKKDN............DNND...............", // 56
        ".....DDDKKK................................KKNG..............", // 57
        ".....DNNNNK................................NKNKK.............", // 58
        "......DNNNK................................KKNDD.............", // 59
        "......DKKKK................................DNDDDD............", // 60
        ".......DDDD................................DDDPDN............", // 61
        "...........................................DNNNND............", // 62
        "............................................................." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'm' to Color(0xFFA578D7)
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
        "..........................................", // 0
        "..............DDDD........................", // 1
        ".............PRAARA.......................", // 2
        "...........ANNRCCARA......................", // 3
        "..........NRRNKKKNRAK.....................", // 4
        ".........NRKKS...DDT.NN.......Z..QZZ......", // 5
        "........ACNTTSDD.DNACOCA......FPPQ..PC....", // 6
        ".......DDAS.TPANSAACOQQQP......EQ...QZPE..", // 7
        "....NNNDDD..SAACNACCOQFEPA...........ZZQ..", // 8
        "...NRCRNS....SNOACANAOQPAQN..........ZZP..", // 9
        ".DRACNKS.....SNCOOCANKANNNK.......EZZJZQ..", // 10
        "...NNKT....DNAANCNAOATDNDTKN.....QZZOJZQ..", // 11
        "....KS..TSSAAAAAANNACDCPPDNNA....QZO2JZQ..", // 12
        ".........NNAAANNANKNCKAPPKKAANAN.PC22JPE..", // 13
        "......ANNA11CANNANKDKKKNNKKCCCCANRC22OF...", // 14
        ".....NACCCEEQOOCNKKKDKKKKKKCAACOANAJ2ZE...", // 15
        "....NAOCCETJOOOOCANKKKKKNKKACCOCQARZJF..NN", // 16
        "....NCCCPEECCPQQOOCNKKKDDNDNAOQAJADEADD.PK", // 17
        "....KOAA1QQAAOQEQJOCNKKKKKKNCJQNCNDSCNNAQK", // 18
        "....KCNACOANACOQEQJOCNNKKNNCOJQNANNNQCNAQK", // 19
        "....KANACANKNACOQEEQACANNAOAPQOANPPANQCC1K", // 20
        "...KNANNANKKKNACCOOOONANNAN1OOCANKDQAECND.", // 21
        "...KANACC1AKKNNAAAAAAANNNNAAAAANNKAJO1ANC.", // 22
        "...KANCPQECKKNNNNNNNNNAANNCNNNNKKNAOCARAPQ", // 23
        "...KAAOJEFOKKNNNNAAAAAAAANAAANNKKNAANKRPEC", // 24
        "...NACOOOQAKKNNACOQEEJANAAN1OOANKKKNNKRPTZ", // 25
        ".DNAACNNNAAKKNACOQEETCOOOCOCQFQAKNAANRCQTZ", // 26
        ".DNAOACCCNNKNAAOQQETWQFEQCQTTTTONKKKRCQETC", // 27
        ".DNCQCOJQCKKNACOQEEWWFTFQOQTWWTQANNRCZQEQF", // 28
        ".DNCQCOQQJKNNACOQQEFWWFEQJQFTTFEANRCCOTEP.", // 29
        "..NAOAOQEQKNNACOOQQETTEEEQQQFFQQAACCCEFQP.", // 30
        "...NCACQQOKNNAACOOJEEEEEQOOQEEQCACCCETPQ..", // 31
        "...NACACANKKKNAACCOOOOOOOCAOOANRCZOETECP..", // 32
        "....NCCANCCKSSNAAACCCCCCCANNNNRCZPETWCQ...", // 33
        "....KACCC1ASTTSNAAACAAAAANNNRACPQEFT.Q....", // 34
        "....KACOOOKTTTTKAAAAANNNNNRRCOPQTTT.P.....", // 35
        "....KNAACOKTSKKKNNNNNRRRRRCCPEWWTEPC......", // 36
        ".....KNNACKSDNRRRRRRCCCPQQQTTWWWPCP.......", // 37
        "......DNNAKSDNRCCZCPQPPETTTWWTPCT.........", // 38
        "......DKNNKSDNRCPPPQETEFFFFESDNN..........", // 39
        "......DKKNKDDNRAPPQQFTTEQPPPNKKKK.........", // 40
        "......DKKKKKDDKKKPQQQQQQPKKKKKBBBKKD......", // 41
        "......DKKKKKKKMMBKKKKKKKKKKKKKKMMMBKK.....", // 42
        "......DKKKKBMMMMMMMMMIIIIMMMKKKKMIIMMD....", // 43
        ".......DKKKKKMMMMIIIIIIIIIIMMKKKMMIIMDD...", // 44
        "........KBKKKKKBMMMMIIIIIIIIMMKKMMIIIMK...", // 45
        "........DKMBKKKKKBMMMMMMMIIIIMMKBMMIIMK...", // 46
        ".........KKMMMMMMMMMMMMMMIIMIIMKKBMIIMK...", // 47
        "..........KKKBKKKKKKKKKKKMMMIIMKKBBMMBK...", // 48
        "............DKKKKKKKKKKKBBMMMMMKKKKBBK....", // 49
        "............DKKKKKKKMMMMMBMMMBBKKKKKK.....", // 50
        "...........DNNKKKBBBMMMMMMMMBKKKKKKKD.....", // 51
        "..........KNNNNKKKKKKKKKKKKKKKKKKKKD......", // 52
        "..........KRRNNNK..KKKKKKKKBKD............", // 53
        ".........DNRRRRK....KKKKKKKKKND...........", // 54
        ".........KNRRRNK......DKKKKKNNK...........", // 55
        ".........DNRAAND.......DKKNNRRN...........", // 56
        "..........KACAD.........KNNRRRRK..........", // 57
        "..........KRRAD.........KNNRRRCRK.........", // 58
        "...........DNRND.........KNNRRCPRN........", // 59
        "............DNNK..........NKNNRCCAA.......", // 60
        ".............DNK............DNNAAND.......", // 61
        "..............DD.............DDDDD........", // 62
        ".........................................." // 63
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'Z' to Color(0xFFD21E19)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawHein() {
    val matrix = arrayOf(
        "........TSSTARASW...............", // 0
        ".......TSDDDROOCSSSW............", // 1
        ".......SDSSSNN121ACSW...........", // 2
        "......WSSSSDDDDPQCOCT...........", // 3
        ".....WTSSSNNKDDDSPCPT...........", // 4
        ".....WTT.SRRNNNNDSDDT...........", // 5
        "......W..TANKNNNAPC1T...........", // 6
        "..........SRKKKKNRAPT...........", // 7
        "........TTTANNAOPPASW...........", // 8
        "....WSSSDAPACAOQETST............", // 9
        "...WSAA1NNNNPAQNNSSTTT.TTTTST...", // 10
        "...TDNNNANNDDDTDDSSDDSSSSDSSSSTW", // 11
        "..TSAAANAANDDDDSTSDKDDSTSDmiTSTS", // 12
        ".TSACSSARRKBMDDSSSKBDSWSDSTTSSSS", // 13
        ".TAPTWTDAZRDIMDSSDKDSTWSDDSSDSTW", // 14
        ".TST.TSDAAASIDDDDSSSWWTDDSSSTW..", // 15
        "..W..TSDDDSSSTTSDSTTWWSDDDDDTW..", // 16
        ".....TSSSSSDTWTSTTSTTTSDMMDSDT..", // 17
        ".....TPCCSSSTTTTTSTTSTSSMDDSSS..", // 18
        ".....WSNDDDSTTSSSTTSWSTDSSSTSST.", // 19
        "......WAADDSSSSTTTSWWSSDSTTSSSS.", // 20
        "....TTTCATTDDTWTSSWWSSSNNADAPST.", // 21
        "..TSPPPDSWWSSTTSSTWTSTSRNRZZZS..", // 22
        ".TP12221PTTTSSSSTWWSSTPAKAAZZS..", // 23
        "TPOOOCCC11PSDDTTWWTSTT1CDAAAAS..", // 24
        "SAOJ1111111SDDSTWWSSTPANAAAAAT..", // 25
        "SAJOOOJ2211SSSSWWTSTTAANAOOCPT..", // 26
        "SCOXO221PPANDSTWWSSTDNRCCZOAT...", // 27
        "WPZO21PTTTSDDTWWTSTSDNNPQCASW...", // 28
        ".TCO1SW...TSTWWTSTTMBNKPESAPTT..", // 29
        "..T1AS....TSTWWSSTSIMNRPTS111ST.", // 30
        "...TPSW..TSSTTTSTTIIBRRASSSAC1PT", // 31
        "....TT...SSTTTSTTIIIDrRNNTWSRA1S", // 32
        ".........SDSSSSWSIVIMMNNAPSSCC1A", // 33
        ".........TDDSSTTIVmIIMRRC1CC211A", // 34
        ".........TSSSTWIIVmIIMZRC121111S", // 35
        "........TSSSTWTMVVmDIMZRZAA111PT", // 36
        ".......TSSSTWWSIVmIKMMZRRZNA1PT.", // 37
        "....WTSSSSTWWTMVVIDNKNZZRZZRDT..", // 38
        "..TSSSSSSTWWWSMVmIDSDKRXZZZZCST.", // 39
        "WSSSSTTSTWWWTDImmDSTSNRXXZRZOCPW", // 40
        "SSTTWWTTWWWWSImSDDSTDNRZXXZROJOS", // 41
        "STWWWTSWWWWTDDDNRNSSNRNZXXXOACAS", // 42
        "SWWWTSTWWWWSDDKNZZSSZZRCJOO21AAT", // 43
        "SWWWSTWWWWTDSKNRZZSSZXR12JOOCAPT", // 44
        "SWWTSWWWWWSSSKRACCSCOOZNAAAPSTT.", // 45
        "SWWTTWWWWTKDDKC11ADROJXC111S....", // 46
        "STWTTWWWWSNKKNO1AADNC1OO222PT...", // 47
        "SSWWWWWWTAAANNAAAADN11OO2222ST..", // 48
        "WTTWWWWWSCZANNNANAAAAA1O22221T..", // 49
        ".TSWWWWWDCCAANAANNANNN1O222221T.", // 50
        "..STWWWTNCAAAN11AAAAAA1OJ22222P.", // 51
        "..SSWWWSDAARZN11ARACAA2JJ22221S.", // 52
        "..SSWWWSDAZZZK11AZZZRA2JJ2221SW.", // 53
        "..TSTWWSTPZXZNN1NRXXR12JJ222PT..", // 54
        "...TTWWSWSRXZRSTSRXXR12JJ221T...", // 55
        "...TSWWSWSRXZATWTCZXRN1111PT....", // 56
        "....TTWSWSRXZPWWTCZZNSTSSST.....", // 57
        ".....TSTWSACZSWWSRZANDT.........", // 58
        "......TWTDAACCTTPZCAAAPW........", // 59
        ".......WSNNNZRSTCZAARRRST.......", // 60
        "......TSARRRRAT.SRAARRZCPT......", // 61
        ".....TPZXXZAAS..TSTTCZZZZAT.....", // 62
        ".....SARRRASTW......TSSSST......" // 63
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

fun DrawScope.drawEmperor() {
    val matrix = arrayOf(
        ".......................................", // 0
        ".................NND...................", // 1
        "................DQQ1D..................", // 2
        "......ND.......D1NKKKN......KKN........", // 3
        ".....D1AKND...NANDS........A1N....DK...", // 4
        "....NEKK111KND.D1AD......DADN....D.DD..", // 5
        "...KDENKDDDQSKD...P..DDKN1D.....D.IK...", // 6
        "...KmPSDWWTNPE.....m..DAQD.....D..MD...", // 7
        "...DTDTDTWSKD11P.....D.PD....DNDm.D....", // 8
        "..D.IDISDTDSSS1P...DDA1D.......MIK.....", // 9
        "...KDIDNTWTKMSS...i..DDIM..D..IIMK.....", // 10
        "...NTDTKDTSDIID..Im.DMIM.DNDIm.IDD.....", // 11
        "...DID0KIDDSDI.......ImDKDDImmm.N......", // 12
        "....KIKKISmDDI..D...IIIDDMImmIIDD......", // 13
        "...DKMKDDmISSD..DD..MIQ2CMmmIMDD.......", // 14
        "..NmDSDmKMmmI..DNDDD.EEQ1DMIDDK........", // 15
        "..NTISITKSDII..NAP1DPQQ1DP1DDDD........", // 16
        "..NmDIDmDTKN..D.1QPDDDANAQEPDS.N...MM..", // 17
        "...DDIMDKSAA..DQEQPSDDDKN1QPPPPN..MIM..", // 18
        "...KIIMKANAP.DA1SP1PSDKKKNAA1QQ.MMIMM..", // 19
        "...KIIMNECNA.N1DPSA11DKKKDNN1QEQIIMDMM.", // 20
        "...KIIKKQ1KNDNAPEESDANKKKDKKA11ANIDKKMK", // 21
        "....DMKNA1KDDNA2PQQ1NKKDKDDKN11NSDIK.D.", // 22
        "....KMKANNKDDKN1111ANKKDDKDKKN1N..DK...", // 23
        "....KIK1KKDDDNN1QE11NKKDDKDMKKN........", // 24
        "....KIKNKDMMDKKA121ADDDDDKKKKDK........", // 25
        "....KIKTKMIDKMDNA1NDIIDKKANDKDDD.......", // 26
        "....KIKDSMIKKMID11PmIDD11NDMKDSN.......", // 27
        "....KIKKMIIDKKMS1E1DDDPQDNDIMKDN.......", // 28
        "....KIKSMIDDN1SS11NDDSSPDKDIIKDN.......", // 29
        "....KIKMIINN1PISDQASSSSPNKDIIKD.D......", // 30
        "....KIKMIMNN1CMSD1PSTSTSNDDIIIKDN......", // 31
        "...DKIKIIDNQQMIIDNSmTTTSDDDDIIDDK......", // 32
        "...KKIKIMDN1PImSDKDTTTTTDDDDImIKK......", // 33
        "..D.KIKIDN1PImmSKKDTTTiTDDDDImIDK......", // 34
        "..N.KIKMK1EQmImSKKDSSTiTDDSDImmMK......", // 35
        ".D..KIKMKD1PIIISKKDSSTiTDDSDDImID......", // 36
        ".D.KKIKDDEDPIDNSSDDSSTTSDDSDDImmIK.....", // 37
        "K.KKKIKNQE1DDKDDSDNDSTSSDDDSDDImmD.....", // 38
        "D.DKKIKKNAPDDKDDSDKDSSTDKKDSDDDmmmK....", // 39
        ".D.KKIKK1ACIPMKDDDDKDSTDKKDSDDSImmD....", // 40
        "..KMKIKNA1NDQIDKDDDKDP2AKKDSDDSDImmK...", // 41
        "..NMKIKDNADQQMMDKDDKNQQ1NKDSDDDSDImM...", // 42
        "..DIKIKDDN11PMIMDSDKA111NKDSSDDSDDmmK..", // 43
        ".NMIKIKSDDNAPImIDSDKNNPPKKDSSDDSSDmmK..", // 44
        ".DMIKIKSDDDNPIIIDSDKNA1ANNDSSDDDSSImM..", // 45
        ".DIIKIKSDD.DPTIMNSDKAADA1NDDSDTDDSDmmK.", // 46
        "KMIDKIKD....DTTDKDDKA11QPNDDSD..DSSImK.", // 47
        "KImDKIK......DTSDDDDIP2ESKKDSD..DDSDmM.", // 48
        "KImDKID......DSSDDDKMS1ESDKDSD...DSDmmK", // 49
        "KmmD0m0......KDSDDDKMIPEmMKDSD...DSDImK", // 50
        "KmmDDSD......KSTSKKDDMSQmMKDSD...DSSDmK", // 51
        "KmmDKID......NSTSDD..MP1IMSDDD...DDSDmK", // 52
        "KImNKTD......DSSSD...DD1SDSSDDD...DSDmK", // 53
        "KImKKID....D..mDDD....AQSKSSDSD...DDDIK", // 54
        "KIIKKID........NDD....AQQNTTDDD...DDDIK", // 55
        ".DIKTD....KDDDD..D....N11NSWDDD....DDIK", // 56
        ".DIK..................NQEPD..DDD...DDMK", // 57
        "..DID................D11A1N..DDD....DMK", // 58
        "..NIK................NAN1AN...D.....DMK", // 59
        "...DID................N1EQN..........D.", // 60
        "....DD.................A121D...........", // 61
        ".....D..................K.EQD..........", // 62
        ".........................DKKK.........." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(flip(matrix), palette)
}

private fun flip(matrix: Array<String>): Array<String> =
    matrix.map { row -> row.reversed() }.toTypedArray()


fun DrawScope.drawCloudOfDarkness() {
    val matrix = arrayOf(
        "........................................D.......................", // 0
        ".......................BB...............DA......A...............", // 1
        "...................ggKMMD.gLggg...........ACD...DC..........g...", // 2
        "..................LLLLggLLLLLGGL...........DCA....A.......IM0.L.", // 3
        ".................LgKBBDgDLLggLLLK0...D.......CC....AA.....MT.D.D", // 4
        "...............DLDSMVBS.TDDDSgggS0IM.DAAAA...DCCDA...AA..00..ILg", // 5
        "..............DLSTMIMBMS.0DgGGMS..TIIDADKACA..CAADC...CODI0.DDLg", // 6
        "..............gD.SIV0TMIDMMDgSMS...MVDSDCDTCC.ACCS.C.0NODI..DGLG", // 7
        "....gD......D.DLTDIIS.SIMIISASSM0TTIVST.TCDNCA.CCN.CDMOAD..DDLgK", // 8
        "..DgLKgD...DB..LSMVISSDMIIINAKMMVIKVMTMT.DAAKONACANCDOCS...DDLL.", // 9
        "..DLLLLg...I..gDSMVMMMAIVMMBIIBKNKMKD.0ITSOKAAAANCCNAON.....gD..", // 10
        ".....DgL..BM..LTDIVMMACMVKBIrSISOCDKNASMM.AKCAAAAOAACCA......D..", // 11
        ".....DLLLDMM.Dg.DIVMKCOABBMMA2JPO2PBMNOMVSAAAAANA1CNAOAA........", // 12
        "....gDTTgGIM.gDSBIIIKNNMIDCC2222JJJCBKOMVSSCAAONCOOCNOOOC..M....", // 13
        ".......BgGIM..LBBMMBKNMIDOJJOJ22211OKOAIISAAOKOACC2OOCOOO1DM....", // 14
        "...D....gDIV.MDIKMG0CAIINOOOJ22CMIIDKCKVM.DAOANCCCA1OOA1OONM....", // 15
        ".DDDDD..gSMIMBMMMK0MNABMCCAA2ODMDDDIIMBVBSNANOKO1COCJOOA1ONM....", // 16
        ".D....D.gSKIVBKMBKMBKMNNDIMAOCNOOOOCMVMMBIAKANONO1A2J22OAJOD....", // 17
        "g.....GDKgLBIMBDGDBKMDANIIAOOOO222OOAMVKKSAKOKOCCOJ22J2J1JJA....", // 18
        ".....MKDMBGggggGKMMKMNNCCNNNCCO222OOAKIBIKATANA2OJ2J22O2222C....", // 19
        ".....IIMKBMBKBMKKIIMKCOOOOO11AAACAAACCCNMKCSSAAC22222222222ND...", // 20
        "....BIVIMKMMKBMKBIIKNOOOOJ2JJOO11O2JJ222CKKADAACO222222222ONC...", // 21
        "....BMIIMBMBMBKIBIIKNOOOJ2JOOOOOOO222JOJ2OANANNCOO22222A11CNND..", // 22
        ".....MMIMKDKBKMIKMIBNOOOCJ222JOOOOJ2222JOJ2OCNA11222222AMAAKCC..", // 23
        ".........DgMMKIIBKMMNOO2AAO22OOOOOOO22222OOOJJAAO22C222212AO2O..", // 24
        ".........LBMBBMIMKMMKOO2JONAOOOOOOJ2JOJ22OOOJOJJ2J2IKDA12AAO22D.", // 25
        "........gLMMBBIIIBBMKCO22OAAKNAADJOOJ222OOO2OJOJOJ2JDNNN1NCOJ2C.", // 26
        "........ggMMBKIIMMBKKCO222NNADANANAOOOOOOAO2O2OJ2222JJJJ1NC2O2O.", // 27
        "........DLBMMKMIVIIBKNJJ2O2ATTACKAAAAOOOONO22JOOO22222221TT1OJ2.", // 28
        ".........gLMMMKBMIIVVMMMANDSASAN1OJJOOOOOACJ2O2OAA2222211T.TAOJC", // 29
        "..........LGSBGLLgKBMMMMMIMMS.DNJ2JOOJOOOONO2J2JANN22QSrSSSCA1OA", // 30
        ".........DLGSTLKgGgMMSMAO1KMMSMNCJOOJ22OOOAO222OOOOA22QD.TAADCJA", // 31
        ".........LDBMgLLLLgKMBKBC2ABMSMDACOJJO2JO22O222OOOOOAAAT....SOOC", // 32
        ".........LT.KSLGGDDSSMVMA11NSI0TDA2222J2OJ2J22JOOOOOOOS....TC2O.", // 33
        ".........DLg.D.DSTDTIIMMA2JAKBT..A2O222JOJJ22OJJOOOAASTT.SSC22O.", // 34
        "...............Dgg..MDNAAJ22JJCA.AOJ222JOJJOJ22JOCNSTTMDMMD122CD", // 35
        "....................IIMDKC22221OSAOO22CDAOOOOJ2JCDMBMMBM..D2221.", // 36
        "......................IIIDO22AS1CSOOOMBDBCOO222ONIVIIIII..C2A12.", // 37
        ".........................M1J2A.SODSCMMCOAOOJ222AKMMII0....11TAJP", // 38
        "........................IINJJ1S.SIDII.DNCOO2222SIIID.....I1T.S1A", // 39
        "........................IISCO2N0SIII....OJOO22A.SIVI........PN1P", // 40
        "........................IVMNC1DMNI0......OOOOCK0IIVI......MMAN1.", // 41
        ".........................IIKAA1KA.........CCDIIIIII.........A1..", // 42
        "..............................CA...........MI0..................", // 43
        "..........................................I0....................", // 44
        ".........................................M......................", // 45
        ".........................................M......................", // 46
        ".........................................M......................" // 47
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'L' to Color(0xFF50BE28),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'g' to Color(0xFF199123),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawZombie() {
    val matrix = arrayOf(
        ".................................................", // 0
        "....................DDDDDD.......................", // 1
        "..................D0DImVi.m......................", // 2
        ".................D.DIiii..m.KD...................", // 3
        "..................IDImm..DDD.....................", // 4
        "................DDDS0mm.D....D...................", // 5
        ".................DSDKDmmD.D.0.D..................", // 6
        ".................00KDDDm.DD.DDD..................", // 7
        "..................KDKSDD.m.mmSD..................", // 8
        ".................KKKKDSDDD.DSD...................", // 9
        "..................KKKKDD0K.DKKD..................", // 10
        ".................DPNKKKKI0DKKD...................", // 11
        "..................NANKKKDI0KKD...................", // 12
        "..............ND.NAPNKKDKDDSDD...................", // 13
        "................PPASSKDDDKDIS....................", // 14
        ".............AQDNNAAPPDDKKDDK....................", // 15
        "............DDKSTWSKNADKKKKNN....................", // 16
        "..........NDDDTmmTiDKNNKKKNNK....................", // 17
        "........PDNDSWTBSSmDKKNKKKKKKNDD.................", // 18
        "........DDSWTI0K0STD0SDKKKKDDNNNK................", // 19
        ".....DPDKmiS0KKKTmiSTDTDKKKDKKKKNN...............", // 20
        "....NDNKDm0DKKKKTTTSTSSDKKKKKKNDDDD..............", // 21
        "......NKI0DKDKDDDTSSSSD0DD0DKDKK..........DD.....", // 22
        ".....DDKD0KDDDTSDmSS0TD0KDDKKDKN..........VD.....", // 23
        "....DPKKD0DKD..DDSIDDSKDKKDKKKDDD.....D.IIm.D....", // 24
        "....DNDNKDKKT.SDmDIDDIKKD0DKKKDKAD...DI0K0iIN....", // 25
        "...D...NNKKS.TKDDKIK00KDKKDDKKKKNAKDDDDDN0mS0D...", // 26
        ".D......D.NT.TKDKDIK0KKKDSDKKKKKKNKDDKDSDSII0....", // 27
        "............DNKKKKDKDKKKKKKKKKNKNNDDDKDTDm0mI....", // 28
        "..........DNKKKKKKDKKKKKKKKKKDSDNNDDKNSSIDKm0I...", // 29
        "........DANKKKKKKKKKKKKKKKKKKD..DKKDN...0DSSD0IK.", // 30
        ".......PPNNNNNKKKKKKDKKKNNKNKD..DNNNN...DTSDDD0K.", // 31
        "......PPNAANKKKKKKKKKKNKTNNSDD..DNSSD..DDTSDKD0D.", // 32
        ".....NPNNANKNNKKKKKDKKNN.DN..D..DK.....DD.SDDDD..", // 33
        ".....PANCAKNNNNNKKKKKDDD..K..D..DD.........DK0D..", // 34
        "....AANAANNNNNNNKKKKN............D........DDDD...", // 35
        "...AAKDANNNNKNAKKKKKN............D........N..D...", // 36
        "...NNNANNNNNKNAKKKKKKD...........................", // 37
        "..N.DNNKKNNKNANKKKKKND...........................", // 38
        "....DNKKNNNKNAKKKKKKDDD..........................", // 39
        "...DNKKNANKKNAKKKKKKKDDND........................", // 40
        "...NKKKAANKKNAKKKKKKKKDSN........................", // 41
        "..N...NPNKKKNNKKSKKKKKDD.........................", // 42
        "......AAND.NKKKNTSNKKKKD.........................", // 43
        ".....DADD..NKKDDT.SNKKKD.N.......................", // 44
        ".....ND....NKKDDT.TNDDKK.D.......................", // 45
        "..........K.DKDDT..DT.DKDD.......................", // 46
        ".........D..DKDDT..DTTDKDK.......................", // 47
        "...........NKKDNT....TKKKKN......................", // 48
        "...........NKKDD.....TKKKKN......................", // 49
        "..........DDKKKT.....TKKKKKN.....................", // 50
        "........DDDKKKS.......SKKKKKN....................", // 51
        ".........NKKKNS.......SNNKKKKN....DD.............", // 52
        "........DKDDKD........SNKKKDD...DNNK.............", // 53
        ".......DKD0KKD........SDDKK0D.DNANKD.............", // 54
        "......NKK0DKDT........T.DNNNNNNNAKK..............", // 55
        ".....DDKDDSSD...........DKNANNKNKK...............", // 56
        ".....NKDDDTSSTTTTTTTTTTTTNKNKKKKK................", // 57
        "....NNNNNNDSNKKKKKKKKKKKDNKKNKKKK................", // 58
        "...DNNKNKNKKKKKKKKKKKKKKKNNKKKKKKKDDDDDDD........", // 59
        "..DDNKNNAANNNKKKKKKKKKKKKKKKKKKKKKKKKKKKKKDD.....", // 60
        "......DNAAPPANNKKKKKKKKKDDDDDDDKKKKKKNDDDDDD.....", // 61
        ".......DNNNNNNKKKKKD.............................", // 62
        "................................................." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawWyvern() {
    val matrix = arrayOf(
        "....D...........................................................", // 0
        ".....DD.........................................................", // 1
        "......DK......................DDD...............................", // 2
        ".......DD...................DD......D...............D...........", // 3
        "........DD..............DD0.U....0D.................DVD.........", // 4
        "........DDD..........DD..UU..d.0D...........D.......D0tD........", // 5
        ".........D0........D0...0U.d.0D.............D...DD..0NDtD.......", // 6
        ".........DDK.....DD...000...DK..............D...DDDD0NNDD.......", // 7
        "..........D0D.DDt0...000d.0DK.........DDDD..DD...DDtKNND0D......", // 8
        "..........D0ND0tSd.U00Udd0KDD.......D......DD0D..DIDNNNNDt......", // 9
        "..........NK000ST.0000Sd0KKtD....D...........DIDDNIDNNNDDBD.....", // 10
        ".........DD0t0SS00t0USd0KNDtt..............P0DIDSDDINKNDDDtD....", // 11
        ".......D.U0t0SS0ttt0STSKNDDt0DD........P..DD00DINPDIMKKDDDDD....", // 12
        ".....DD.0tt0dS0ttt0USStKDDDD0tDAD.D...DD00DNttDIIDDDDDKDDDDD....", // 13
        "...D..00ttUSS0tttt0ST0KDDDDDt0t0.DDD00DNttKKGBDDIDNND0KDDrDDD...", // 14
        "........D0SSttttt0UTSDDDDDDDtt0U..DNKtGKKGDKKGGK0SDNDSKDDrDDD...", // 15
        "........DmS0tttt0UdSDDDDDDDDDt0000DNKttDNDttKKKKtSSDADSKDrDDD...", // 16
        "..........0ttBtt0UTSDTDDDDDDDttttBDKKt0tGKGBGK0DK0TSDrS0KrrDD...", // 17
        ".........0BGDttt0SSDT.SNDDDDDKDttGKKNDttGKKKKDS0GKDSSrSSKDIDDD..", // 18
        "......D.0tDKKKt00S0D..SDDDDDKKKGGKGtDDGDKKKKK0TSDKSDSD0KNDIDDK..", // 19
        ".....D.0tBKDKKt0USD..SSSSDDKKKKGGKGGtDKKKKKKKtSTSDSDDD0DNDIrDK..", // 20
        ".....D.ttKBDDKt0SSD.SSSSTTSDtGGGKKKKDGDKKKKKKGDDSDDKK0SSKDrIDtD.", // 21
        "....D00DDK0DKKtUSDTSSSSTTTSStGBDKKDtKKGKKNNNKKBKtSDNKDSSDNDIDDD.", // 22
        "....D0tDSD00KK0USDTDSSTTTTTS0DDttGKDtKKKNDDNNKDDKDSSDDST0DDIrDD.", // 23
        "....0tDTDDDDKD0UUDDSPSTTTTSmSDKt00GKtGKKNNDDDNKDDKDDSSSTSDDIrDD.", // 24
        "....0KTDD00DTD0S0NSSSTTTTTSSS0KDUU0DDtKK...DDNNDDDDSDSTTSDDIrDD.", // 25
        "...DDSDDUtDSTD0UtNSUSSTTTTSSSUtDDUc0DDtK....DDDNDSNNKDDSDDDDIDD.", // 26
        "...KSD0DDtDTTD0UDDD00DDSSSSScctGDDtU0GDD.....NDDN0DNNDDSDNDDIDBK", // 27
        "....DD0DNGD.TD00KDNDS0KK00DD0U0tKKGDDDD......DDDDDDDNDKDKNDDIDDK", // 28
        "...DDDDtDK...D00K0DSSS0DKttDDtttDKKKKKD.......NDIDDNNDKKKNDDrDDK", // 29
        "..DD0U0tKS...S0DKt0SSTTSDKttGKGtttDGKKD.......DDSINKNNK..DDDDDDK", // 30
        "..DD00tDS...TK0KKD0SSSTTSDKtDGKKDDDDDGKK.......DSSDNKND...DDDDDK", // 31
        ".DDD0tDS....DKBKDDS0USTTT0KGBtGKKKGGDKDDD......DSSDNSND...DDDDDK", // 32
        ".KDSDDD....TKDDDtUSSD0mTTSKKKGtDGKKKKKDDD.........DD.ND....DDDDK", // 33
        "DG0S0tD...TNDSDBt0UUDDSTTSDKKKDDDDGDKKKKD.......I.D..DD.....DDIK", // 34
        "KtDSDGDT.SDDDSStGtttDDDDDKKKGKKGGDGKKKKKD.......D.D...D.....DDDK", // 35
        "KDDSDDKDDKtSSSStKGKKt0DNDKKKGGKKKKKKKKKGD.......DD.....D....DDDK", // 36
        "KG0SSSDDDDSdSSDtKGGKSSDDGKKKKDDKKKKKKKKtD.......DD......NN...DD.", // 37
        "ND0SSSSSSSSSSSDtGKGDSSDKGGGKDDNGKNDDDttDD....................ND.", // 38
        ".KDUSSSSSSSPS0tDDKKDSS0DGGKD...DDDDKDDDNKKKKD................DD.", // 39
        ".Kt0DSSDSSSSD00tDDKKDDDDKDD........DDGKKDGGtDDD...............D.", // 40
        "..DtDDSDSSU0tDttDDGKDDDDDDD.........DGKKDKDttttDD.............D.", // 41
        "..DDND00D0D00ttDGDDGKDDDDDD.........KKKBDDttt00tDD............D.", // 42
        "...DDKDtttDDDDDDGGGD.DNDNDD.........DDtttBDDDDt0KD..............", // 43
        "....NKKKDDDGNKGDDK....DDDDDD........ttGKKK....DKSDD.............", // 44
        ".......DDGGGKKDD......DDDDDD........tDKD.........DD.............", // 45
        ".......................DSDDDD......DtKD.........D...............", // 46
        ".......................DSDDDKN.....DD...........................", // 47
        ".....................D.DDGDDtDDD...DD...........................", // 48
        "....................D.tDDKDttDDDD...............................", // 49
        "..........................DD0t0tDD..............................", // 50
        "............................000StDD.............................", // 51
        ".............................D00SDD.............................", // 52
        "..............................0SS0.D............................", // 53
        "..............................D0t000D...........................", // 54
        "..............................0UtSS0............................", // 55
        "..............................DSKS0D............................", // 56
        "..............................DD.0DD............................", // 57
        ".................................D.............................." // 58
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'D' to Color(0xFF4B4B4B),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'm' to Color(0xFFA578D7),
        'r' to Color(0xFF961950),
        't' to Color(0xFF0A6469)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawBehemoth() {
    val matrix = arrayOf(
        "................................................................", // 0
        ".....DA.........................................................", // 1
        "......DAAND.NAD.................................................", // 2
        "..AAD.DKNACANRAANA..............................................", // 3
        "..DDNAAANNNNNKNAAAC.DD......DDDDD...............................", // 4
        "......NNRRNKKKBBDDDKDDD.......DDDDD.............................", // 5
        "....DDAAAAANKKBBBBBDKS.D........DDDDD...........................", // 6
        "....DN.KNAARNKKDBBBBDD.............DDDD.........................", // 7
        ".......DANNAANNKBBBBBDD...D..........DDDDD......................", // 8
        ".........DNNNANKKKBBBBDD...............DD.DD....................", // 9
        ".............D..DKKBKDDBD....D..D........DDD.DD.................", // 10
        ".................KKKDDtBK.....D...D....DDDKKD..DD...............", // 11
        ".................KKDDtBBK......DD...D...DKKDKKKDDDD.............", // 12
        "................KKDDttBKK......DDDD.....DKKDDDKKKD.D............", // 13
        "..............DDDDtttBBKD.....DDDDDDDD.FFSDKKKKKKKKDD...........", // 14
        "............DDDBttttBBKKD...DDDDDDKDDKDDSFFSSDKKKKKN............", // 15
        ".........KDDDBttttBBBKKDDDKDDDKKKKKDKKDKKKDSSTTTTSDDND..........", // 16
        ".......DKDDDttttBBBBKKKDSSSDDDDKKKKDDDDKKKKKKDDSTFFTSDD..D......", // 17
        ".....DKKDDDBBBBBBBBKKKSSDDDDDDDKKKKDGKKKKKKKKKKNDDSSFS.D..D.....", // 18
        "....DBKDDKBBGBBKKKKKKSTDDDDDDKKKKGtccttKKKKDDDDKKKDSS.F.DD.DDD..", // 19
        "...DBBDDKKKKDKKDKKKDSDKDDDDDDKKKtcccttKKKKKKKDDKDDKKD.EE.DD...D.", // 20
        "..DBtBBDDKKKDDDDSDKKDDSDDDDKKGttccttBBtKKKKKDDDDKKKKKD..E..D..D.", // 21
        "..DtttBDDDDKDDDDDDDDDDDSDDDDDttttttctttBKKKKKKDDDDKKKKND.DDDDD..", // 22
        "..BtttBtDDDDDDDDDDDDDDDDDDDBttttttcdddttBKKKKKKKKKKKDDDDDKDDD...", // 23
        ".DtttctBttDDDDDtttDDDDKKKGttttttccdddcctttBKtcttKKKKDDDDttD.....", // 24
        ".KBttcttttttBBttccttBBBBtttttcttccdccctttttKKKGtctGKDtttcttD....", // 25
        ".KBtttcccctttctttcccttttttttcctttctctttcddcBKKKKKttBBtBtctttD...", // 26
        ".KKBttttttttcccctccctttttttcdctttttccttcddctKKKKKGtttttKDDBBtt..", // 27
        ".DKBttttttttttccddctttttccdcctttttcdddctcddctKKKKKtBtccDDDttttt.", // 28
        "..KKBBtttttctcccdddctBttcccttttttccddddccccctKKKKKDttcdctttccttK", // 29
        "...KKKBBBBtttccccdddtBttttttttttttcccddccctctKKKKKKKtttttDKDttKD", // 30
        "....KKKKKtttttcttccctttttttttttBBttttccccttttBKKKKDKBtttDDSDDDD.", // 31
        ".....KKKKBtBtttttcccctBBtBtBBttttBtttttttttttBKKKKKKttGDS.TSSD..", // 32
        ".....KKKKBBBtttttttcctBKBKBBKtttBKKDBBtttttBtBKKDBKKtcKDT.D.D...", // 33
        "...DKKKKKKBKBttttBttttBDttBKKKBBBBKKKBtBBBKtttKKKK.DttDGD.......", // 34
        ".DDDKKKKKKKKKttttBttttBKKBBKKBKBBBKKKBttttKBcctKK...DttDKD......", // 35
        ".KKKKKKKKKKKKKBtBKBtttKKKKKKBBBKKKKKKKttcttBcctG.....DtDDDD.....", // 36
        ".KBKKKKKKKDDKKKBKKBttKKKKKKKKKBBBKKKKKtccctttctG......tttK......", // 37
        ".KKKKKKKDSTDKKBKKKBBKK....KKKKKKKKKKKKtcccttttttD.....DtD.......", // 38
        "..KKKKDTTTDDKKKKKBBKD.......DKKKKKKKKKBtcttttttBD...............", // 39
        "..KKKKD.TDDKKKKKKKKD...........KKKKKKKKttttttttDD...............", // 40
        "...KKKKSTKKKBtGKDD................KKKKKKttttBttKD...............", // 41
        "...KKKKKKKKKBttK..................KKKKKKKtBBtcctD...............", // 42
        "...KKKKNKDKKDBBKD..................DKKKKKKBKtccctD..............", // 43
        "...DKKKKDN.DKBBBKD.DKD...............KKKKKtKtttcccD.............", // 44
        ".....D......DKKKDKKDKKD..............DKKKKBKKttttct.............", // 45
        ".............KKKKDDDDSND..............KKKKKKKttttttt............", // 46
        "..............DKKKGSDDD.....................DBBtttttD...KKD.....", // 47
        "...................D..........................DBttBBBKKKDDDD....", // 48
        "...............................................DDGDDDDKKDDKD....", // 49
        ".................................................DKKDDDDKDSK....", // 50
        "..................................................DDKKKNSDD.....", // 51
        "........................................................D......." // 52
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        't' to Color(0xFF0A6469)
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
        "..323...............................", // 0
        ".322G2..............................", // 1
        "....1D2.............................", // 2
        "....2xa....3333.....................", // 3
        "...23D3..22FFEDA3...................", // .
        "..33E2..2wFExEExD23322D2............", // .
        "..1FE133yEDAADEEDDAExFFx1...........", // .
        "..AFxDxxDD1122DExDADD1EFx2..........", // 7
        "..AExFED11xx..2AFEDA..2EFA.....23...", // 8
        "..3DDDD2..DF3EE12xDA..3ExA...22ED1..", // 9
        "....213...3ADDA3AxED2.2DD0212xxEFx2.", // 10
        "............33..AxED11000D2FFww3GDD.", // 11
        "................0xD01E3FFFFE2E2A1A1.", // 12
        ".......vg113.312D1DxwwwwFED2A00A2.0.", // 13
        "......2EwFE21EwiwEFwwwFE2212...10...", // 1.
        ".....33ijwFExiwwEFFFFx2DD10.....2...", // 1.
        ".....2wTwwxDwwF22EE22DD211..........", // 1.
        "....aEFwFxDE22222EDDDD20132.........", // 17
        "....1EFFEDDDDDDDDG1A100A0...........", // 18
        "...31EE2DGGDDDDGA0A0001220..........", // 19
        "...1xF2GG0AAAA00000100AEEA..........", // 20
        "..12wF2GA00011A010AAAAADxG2.3.......", // 21
        "..1FFF2GD11D22D1GA0000GDE2AA3A2.....", // 22
        "..DwF2DD2EE2222DGGA00A0ADEA212E3....", // 23
        ".32FED2xFFFE2222G222DA0Dxx0203AA....", // 2.
        ".3D3D3wFFwFxEE2D2xEED0ADDD21103A....", // 2.
        ".32DE2FiFxFFEEDDFFF2g.1A13D22A2A....", // 2.
        ".32DFFDxEE22DGG1EE11...1200AAA31....", // 27
        ".32E2FFD1D1DDGGGGGg.....10G001A3....", // 28
        "..DFE22DG2E32D212g.......3AGGA0.....", // 29
        "..AEwF2DG2ED222DE3.........2113.....", // 30
        "..1DFFE2GDEEDD2D23..................", // 31
        "..23DDEDDGDD22FD2A..1g..............", // 32
        "..32w2ED121x3DE22A0113..............", // 33
        "...23FD1iwEA2D2G20AA2...............", // 3.
        "....1E1FiF2201D002AAA12.............", // 3.
        "....302FFED2D0A1DA01G1A1............", // 3.
        "....203wEDG2D111AD112EF312..........", // 37
        "....10.FEFE21211DA22D2EFw3a.........", // 38
        "....212x2xA12211DAEFwF3EFw32........", // 39
        "....30DA11222D12AA2FwTiwFEwF2.......", // .0
        "...2E0A00122D21A101ExwiTwxFx1.......", // .1
        "...21DD11222221A200GDEFwwExF2.......", // .2
        ".....FAx10221A211201gGDEFEEF2.......", // .3
        "....xADD11gg1011121.300ADxE21.......", // ..
        "....30DDgQ112A02221201121D2D1.......", // ..
        "......D0aQ22g1122220gA2a21A1........", // ..
        "......012222AD12112Ag211g03.........", // .7
        ".....212231ADD02110ADg1g2...........", // .8
        ".....1222112ED121.122D01............", // .9
        ".....121ADExEg.2..3EE2D3............", // .0
        "....3110AwF222.....D32xD............", // .1
        "....12000FFEDA.....a2FD23...........", // .2
        "....213g.12DGDa.....3D30............", // .3
        "....22...31DDDa......123............", // 5.
        "..........2DDG.......3..............", // 55
        "...........1D12.....................", // 5.
        "...........1DGA.....................", // 57
        "..........3E2DA.....................", // 58
        ".........23EE13.....................", // 59
        "........3F1F1.......................", // 60
        "........31323.......................", // 61
        ".........323........................", // 62
        "..........2........................." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF0C0C10),
        '1' to Color(0xFF262630),
        '2' to Color(0xFF484855),
        '3' to Color(0xFF737684),
        '4' to Color(0xFFA2A6B4),
        '5' to Color(0xFFD0D4E0),
        '6' to Color(0xFFFAFAFF),
        'A' to Color(0xFF301C12),
        'D' to Color(0xFF58301C),
        'E' to Color(0xFF844E2D),
        'F' to Color(0xFFB27648),
        'G' to Color(0xFF0F341C),
        'Q' to Color(0xFF301644),
        'T' to Color(0xFFFFCAAF),
        'a' to Color(0xFF204444),
        'g' to Color(0xFF122626),
        'i' to Color(0xFFA2EB8E),
        'j' to Color(0xFFDAFCC0),
        'v' to Color(0xFF76A8A8),
        'w' to Color(0xFFDAA270),
        'x' to Color(0xFF8E4E3E),
        'y' to Color(0xFFC0765C)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawShiva() {
    val matrix = arrayOf(
        "........................................", // 0
        "........................................", // 1
        ".....................BBBt0..............", // 2
        ".................0DDP0SSDU0B............", // 3
        "................DDCNNPSTQBSD............", // 4
        "...............DDAKKKDCQ2QSPN...........", // 5
        "...............DDKSSKSA1CSTSN...........", // 6
        "..............DDKDWDKDPDS0SKNN..........", // 7
        ".............DDNDTSKDNPBSTSK1N..........", // 8
        ".............DDKNWSDKN1KDSKNPK..........", // 9
        "............DDDDWTDNKCP0DKKANND.........", // 10
        "...........DDASWWSB0KPSS0KK1NAD.........", // 11
        "...........N1NTWWSSTK1DSS0D1N1D.........", // 12
        "..........DD2DWWTBmTK1DTWT0CN1D.........", // 13
        "..........DPDSWWSSSIDQSWTWSDKPD.........", // 14
        "..........DPNWWW0TIBPPVTSTT0KP1.........", // 15
        "...........DNIT0mSDD2DB0BV0DKNN.........", // 16
        ".............II0mDKD2AD0m0BDNNND........", // 17
        ".............D0mIKKD2DSTTSK1CTDN........", // 18
        ".............DSTDDDN2PmTT0K1C..D........", // 19
        ".............DISKSKKCQSTmBKD2...........", // 20
        "...............DDWTDN2STTBDK2P..........", // 21
        "...............DWWS0N2SWT0DKAP..........", // 22
        "............0.....D0SAQTTISDNEP.........", // 23
        "............00D.DI0USDQSd00DNE..........", // 24
        "............0D..DB0VTDQSUI0SKDE.........", // 25
        "...........D00...D0VTSSPST0SDDQP........", // 26
        "..........00U0....BITTDSSS0SSDC.........", // 27
        "..........0VBD....DBSTSDTTPPPQ..PP......", // 28
        "..........DUD.....D0TWSDDQTTTPDD1F......", // 29
        "...........D......DBmWTD0DDD....DPE.....", // 30
        "...................DmWT0SSD.......D.QP..", // 31
        "...................DVTWIISD........DDND.", // 32
        "....................0TWS0S..............", // 33
        "....................DSWT0...............", // 34
        "....................DDTT0...............", // 35
        "..................DI.DTTV.DD............", // 36
        "...................DDDSV00N.............", // 37
        "...................DDUU0DD..............", // 38
        "....................DVVDDD..............", // 39
        "....................DdUD0D..............", // 40
        "...................DUdDBDD..............", // 41
        "...................DUUDSD...............", // 42
        "...................DV0DUD...............", // 43
        "...................DUD00D...............", // 44
        "...................D0DSD................", // 45
        "..................DBDD0D................", // 46
        "..................00BD0D................", // 47
        "..................0UBD0N................", // 48
        "..................DU0DDN................", // 49
        "..................DV0DDN................", // 50
        "..................DU0D0N................", // 51
        "...................DDDSK................", // 52
        "....................D0VDD...............", // 53
        "....................D000D...............", // 54
        ".....................0DD................", // 55
        "......................D.................", // 56
        "........................................", // 57
        "........................................", // 58
        "........................................", // 59
        "........................................", // 60
        "........................................", // 61
        "........................................", // 62
        "........................................" // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'd' to Color(0xFF5AD2CD),
        'm' to Color(0xFFA578D7),
        't' to Color(0xFF0A6469)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawRamuh() {
    val matrix = arrayOf(
        ".........................D..GKG.K..33...................", // 0
        "........................DlDDUDUDlD......................", // 1
        ".........................D22UAAGN..4....................", // 2
        "..........................DDAAAD....4...................", // 3
        ".........................4..GKG....4.4..................", // 4
        ".............................DKDD.4.....................", // 5
        "............................DDSS.D......................", // 6
        "...........................D.DDP.D......................", // 7
        ".............DGGG...........GKDP..DD....................", // 8
        "............DDSDDD............DGD.NlD...................", // 9
        "...........D...DGGD..........DDDKDDD....................", // 10
        "..........DD..D...D..........DTGDGDG3...................", // 11
        "..........D.PG..D.D..DGGG...GDTGDDDG.43.................", // 12
        "..........D.D....D..D...DD.D.DTSGGDDD.34................", // 13
        "..........D.K..D...D.DP...DD.DTSKDDDK.3.................", // 14
        "..........D..D.GDDGG.PDD.D.D.....DDD233.................", // 15
        "..........D..D.GSSGNPNT.N.N..D..D2DDDK2.................", // 16
        "...........D..DGSTD2NDDD.D.D.D..2AGDDDDD................", // 17
        "............D..GKTD2NNS..DPDNDD.32GDDDDDDD..............", // 18
        ".............D..PDKNDS..NDNSPGDD.DlN2232S.DD............", // 19
        ".............DKP......PGDDDPSGDD..DKDDDD3D..G...........", // 20
        "............DDDGDP.Q.DGDDPSDSDGDD..DDDDG3DD..D..........", // 21
        "............GDDDGGGGGDDDTPTDPSGDDDD.DDG23SDD.D..........", // 22
        "...........DDDSSDSSDDKDSFSFSKSPGD22D3DDDDGSD..D.........", // 23
        "...........DSSDSTDTTDGTSFSFSKDSG3DSSSDDDDUGDD.G.........", // 24
        "..........DDSDDDSTSTDKTSFSFSDGSGD23TTDDDDUGDD.G.........", // 25
        ".........DDDSSSDDDDSSGSFFQFSDKTGDDD33TDDDDUGDG..........", // 26
        ".........DSSSDSDDDDDSGSFSFFSDKTGDDDST43DDGDGDG..........", // 27
        "........DDTSDSDSSSSDDKTFSFSSKDTGDDDDSTTDDDKlKDD.........", // 28
        "........DDTSSSTTSDGSGKSFFTFSKSTGDDSDSSTDDDD3DGG.........", // 29
        "........GDSSTTSSGKDGDKSFFSTDDTSGDDSSDSTTDDD3SDGD........", // 30
        "........GDDSSSTGDDDGDGDFSFFGTSSKDDDSTSSTDD2STSDG........", // 31
        "........GDDDDTTGDDDDKKDTSFSGTTGDDSDSTTDSS2DSTTSG........", // 32
        "........GSDDDSTGKKDSTDKTSFSGFSGDDSDDSTTD2DDDSSD.........", // 33
        "........GSDDDDTGDDKSSSGSSFSGFDDDDDSDSTTT3DDGGG..........", // 34
        "........DDSDDDSGDGDTSSGSFFSGTDDDDSSSDmWTS2DDD...........", // 35
        ".........DDSDDSDKKDFPTDSFTSGSSGDSDTSDSWWDD3DG...........", // 36
        "..........DDDDDSK0GDGSGSFSTGSSGDSSSTDDTSKGGD23..........", // 37
        "..........DDDDDSKUUGDGDSFSTGPSGDSSDSTDTDDKTD33.4........", // 38
        "...........DDDDSDGUGDKDSFTFPKSGDSSDDTTDGDSDTDDD3........", // 39
        "...........GDSSDSGDKDKDSFFFSKSPKSSSDSTTSKSTDDDD.........", // 40
        "...........DDSTDSDGGDKSSSFTSKDSGDSSDSTWTTDDG.DDD........", // 41
        "............GDTTDDKKDKSSSFSFDNSDKSSDSSTWiTD..DDD........", // 42
        "............GDDTSDDGDKSFSFSFSGSSKSSDDSTTTDG...DD........", // 43
        "............GDDDSSDGDKSFSFSFSGDSKDSDDSTTKDK...DDD2......", // 44
        "...........DDGDDDSSDKDSFSFSFSPDPDDSDDDSSKDDD..DDD.4.....", // 45
        "...........DSGDSDDSDKSSSSFSFSSGDPGSDDDSGDDDG...DDD.4....", // 46
        "...........DSDGDSDDDKSSSFFSSFSDGPNDSDDSGDDS.G..DDA3.....", // 47
        "..........DDTSGDTSDDKSFSFSSSFSSGPPGSSDDKDD..K...DAC.....", // 48
        "..........DSTSGDSTSDKSFSFSSSQPSKDDGSSSSDKD..K..4DAA....4", // 49
        ".........DDSiSGSDTSDKSFSFSSPSPSDGDGSSTTSSDD..D.3.DAP..4.", // 50
        "........DDSTWDDSDSDGDTSSPSSPSSFPKDKDSTTTSSG..D..3DAC..3.", // 51
        ".......DDDSTTDDSSDDGSFPFPSSSSPFSKDKDDSTiTTGD.D...DA333..", // 52
        "......DDDDmiSGDTSDDKTTSFPSSSSPSSDKDDSTSTiTSG.G....DCC2..", // 53
        "....DD.DDSTTSGDTTSGDFSFSSSPSSPPTPGDDSTTTiTTGG.....DAA2..", // 54
        "..DD.m.......GDTTSGTSFFSSSPSPSPSPGDDDSTiTTTGG......DAC..", // 55
        "GG..........DGDTSGDSSFTPSSPSPSPPSSGDDDSTWTSD.......DCA.3", // 56
        ".DGD........DGSSGDSSFFDSSDPSPPSDDSGDDSTTTSDGD.....4.DA..", // 57
        "..DGD.......DGSGKDSFTDDSPPSPPDPDDDDGDDDDDDGKGD....3.DAP.", // 58
        "...DGKD....DKDDGDSTSDDSDDDDPDDDDDDKDKGKGGGGG......4..DA.", // 59
        "....GGKKKKDKKDKSSSDDKDDKDDDPDKNDKDKGKKKKGK..3...33.4.DAC", // 60
        ".......GGGGGKGDDDGGGKDGKDGKDKKDGDKKKKKKG.....43.3..34DAA", // 61
        "............GGGGGGGGGGKKKKGKKKKKGKKGGGGG....3.........DA", // 62
        "......................DKGKKGKKGKKGG....................." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'l' to Color(0xFF96DC37),
        'm' to Color(0xFFA578D7)
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
        ".................................................", // 0
        ".................................................", // 1
        "........................D........................", // 2
        "........................DD.......................", // 3
        "....................DKK..DKKD....................", // 4
        "...................D.KK..DDD.D.......KKK.........", // 5
        ".................DKDDK0...VK0DKD....DgS.D........", // 6
        "................KDDKDV0...0VDKDDK.DDDDKKDD.......", // 7
        "................KSDDSSD..0DSSDDSK.DDDD...........", // 8
        "................KSS00D0...DDD0SSK.DDDD...........", // 9
        ".................DSSDD.....DDSSD..DDDD...........", // 10
        "..................DSS.dprrdVS0D....DDD...........", // 11
        "...........DNND....D...rRrVSSD......DgK..........", // 12
        ".....DDDDD...NAD..KKKKK..MKKKKK.....DgK.....DDD..", // 13
        ".....DMIS.....NN.DDDKD.DDDSDKDDD....DgK.....GK...", // 14
        "....MMMMMIM...KA.D00KD0..00DK00D....DgK....DG....", // 15
        "...D.MDDDM.D..KAKK0SKDK...KDK00KM.DGDgD...DDD....", // 16
        "..DIMKd.dKMIDKKKAKDDKT0..V0TDDDKKKGggg...GDK.....", // 17
        "..DMK.....KMSMKKKrrDKVdd.VVVKDrrKGggGD.DDD.......", // 18
        "...D......KMIIIMKrDKKDVV..VDKKDrrKDDKK.DK........", // 19
        "...........DMIIIDDD0SDD...DDS0DDDKKKKGDG.........", // 20
        "............MMIIDDSVTSD0.0DSVVSDKDDDDGGK...NNp...", // 21
        ".............DMMKTddTVSDKDSVTddTKMMMMKKKKNAAD....", // 22
        ".............KKKKTdTTdTVSVTdTTdTKMMMImmIDNNK.....", // 23
        "...........DMDGKSVVVdddddddddVVVSKKKKDIIIM.......", // 24
        "...........DDDGKVV00TddTTTddT00VVKNNNKDDMI.......", // 25
        "........DDKDDGDDVSDDSTTVVVTTSDDSVDDNNKTTMMIM.....", // 26
        "......DDggGGGKDSV0DDMSVVVVVSMDD0VSDKKD..TMMID....", // 27
        ".....DgDGGKKKKDSSDDDKMSSVSSMKDDKSSDGGGG..TDMmD...", // 28
        "....KgDGKK...DSS0KDrKSVVVVVSKANK0VVDS.KG..dKmID..", // 29
        "..dDgDK......0TSKDrMKSVVVVVSKNrDKTTDS..DD...KID..", // 30
        "...KgG..........KMMDDVTVVVVSDDDMKSTS........KID..", // 31
        "...KgKd....D..VDKrDDSTTVVTVVSDNAKMS..D...d..KM...", // 32
        "...DgK.....DV..KKrDDSTTTTVVTSDDAKD...D......KD...", // 33
        "....KgK...KV.VDKKKDSVSVVVVVTTSDKK.....K...dKK....", // 34
        ".....K....0...KKKK0VVMSVVVSTTSDD..D...0..........", // 35
        ".....d.....V..KKKK0VTSMSSSSTTS0D..D..............", // 36
        "........dK..V.KKKK0VTSMDMSTTTT0D..D.V..Kd........", // 37
        "........dKVK0DKKKK0VTSSDDSTTTS0KK.DD0K.Kd........", // 38
        "........dKSKKKKKMKDSTTSDDSTTTSDKK..KKKVKd........", // 39
        "........dK0KT..0SKDSTTSDDVTTTDNAAK...KVKd........", // 40
        "..........KKTTDSS0D0TTSDSTTTVDKNAK...KK..........", // 41
        "..............MS0DKDVTSDSTTTSDSTNN...............", // 42
        ".............D.VDSDDSVVDSTTT0...KAK..............", // 43
        ".............D.VK.TD0VVDSTdTD...DAN..............", // 44
        "...........DD.V0K..TDVSD0VTVK....KAN.............", // 45
        "...........DD..K...TK00KDSV0K....KAN.............", // 46
        "...........DD..K...TKMMDD0S0K....KAN.............", // 47
        "............0D.K...TKM0DD0SSK....KN..............", // 48
        ".............D.K....MMMKDSSSK....NK..............", // 49
        ".............D.0.....KMKDSSSK...KAD..............", // 50
        "..............0.K....KMDDSS0K...KK...............", // 51
        "..............D0.DT...0MD0SKd....................", // 52
        "...............D0MST.S0MD0SK.....................", // 53
        "................DDDS.KMDD0SK.....................", // 54
        "................DDD..KSDD00K.....................", // 55
        ".....................KMDD0SK.....................", // 56
        ".....................KSDDVVK.....................", // 57
        "......................DKDVVK.....................", // 58
        ".......................D0VVK.....................", // 59
        "........................0VSK.....................", // 60
        "........................0SMD.....................", // 61
        ".........................KK......................", // 62
        "................................................." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF304D6E),
        'A' to Color(0xFF844A3E),
        'D' to Color(0xFF3E3948),
        'G' to Color(0xFF264E3B),
        'I' to Color(0xFF6C488A),
        'K' to Color(0xFF18151F),
        'M' to Color(0xFF44305C),
        'N' to Color(0xFF582E2A),
        'R' to Color(0xFF8A2639),
        'S' to Color(0xFF69677A),
        'T' to Color(0xFF989BB2),
        'V' to Color(0xFF628EA8),
        'W' to Color(0xFFC6E2EB),
        'd' to Color(0xFF90B9CB),
        'g' to Color(0xFF3E7A58),
        'm' to Color(0xFF9870B6),
        'p' to Color(0xFFAF5282),
        'r' to Color(0xFF80345E)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawEvrae() {
    val matrix = arrayOf(
        "................................................................", // 0
        "............................TSmDS...............................", // 1
        "...........................STSSDDW..............................", // 2
        "...........................TSiiiKT..............................", // 3
        "..........................TDiiTiKT...TSSSDSSSST.................", // 4
        "..........................SSWiWIKWTSSSDKSSSmSDDDT...............", // 5
        "........TSSmTT.............TiiDKDWWKmTWWWWWiiWTSDT..............", // 6
        ".......TTiiiiimST........WiSSKKKTWSTTWWWiiiiWWiWTIT.............", // 7
        "........iiiiiiiimmTiTTTiSSTKKDDmmSTWWWWWWWiiWWWiWTST............", // 8
        "........WiiiiiiTT00SSSDSDSEDSSSTTFFTSTWWTiWWWiWWiWSS............", // 9
        "........iiiWWWiTTTSDSEEFFEEEEEEEQEEEEPASiWiiWiWiWWWIT...........", // 10
        "........iiTWiTmSTTEFFFEEEPAPQQQQPQEPPENASTWTiiiWiWWiS...........", // 11
        ".......iWWWTSDKDDFEEEEPPANANCPQQAPPQPQARCWWWTiiWiWWiI...........", // 12
        ".....WiWWiTTTSSTEFQPQPAANANNNAPPNPPNQQAKAQWWWiiWWTWiST..........", // 13
        "....iWWWiiWWTmDEFEQPPPAANKNNKKAPAQANPPAKNAWWWWTiWiiWTT..........", // 14
        "...iWWWiWWWTSKDEEQQACANNKNNNKKNANAPNAPNKKNEWWWWiWWiiTD..........", // 15
        "..iWWWiiWWTDmSSEQPPANNKDDKDDDKNAKNPNADKKKNQWWWWTiWWiWD..........", // 16
        "..WWiiiiWWTiTEFFQPDNDK0IIDDDDDNAKKANNPKKKNQFWWWWTWWiWDi.........", // 17
        "...iiiiWWTiTDKFEQPNKDBDDISDDDDDDNKNrNQNKKNQQWWWWTiWiWDi.........", // 18
        "..WWWi..WiTKDDEQPPD00SSTSSSSIDDDNKNANANKKSQPWWWWiiiWTSiT........", // 19
        ".........TTTFFEQPDDIST..KSDNDDDNDKNRNNNKKQTS......i.STii........", // 20
        "..........iTEFEPPKDS....KDDKKDrNAKNNNNNKSQ.F......i.DTiiT.......", // 21
        "..........TDDFEAPDS.....DNPKKPNNANNNANNN.T........i.0iiiiT......", // 22
        ".........TDDSEQPDS.......NNKKNKKKKANAANA.T......T.iTSi.iii......", // 23
        ".........SSEFEQQNQ.......SAKKKKKKKANNANC........T.TS.T..T.T.....", // 24
        "........TSTSFQQQSDS......PNKKDKDKKKNNNNA........T.SD.T..T.T.....", // 25
        "........T.DDSQPQPKAP.....AKKNNKDDKKAKNNAF.....FFSTSDQT.iii......", // 26
        ".........TKKSQPQPNNNQ....AKKNKSDDDKRKNNAT....TFQPADDPSFTii......", // 27
        "........TTKQQQQPQTTNRT...AKNDDNDDDKNNNRCT...TEQNKKKKKAES.i.T....", // 28
        ".........SPSPPPPPT.SNS...NNS.SDDSDKKNKNS...FEQNKKKKKKNPSSi......", // 29
        "........TDQSDQPQPT.TNNF.TRA..TSKSSKKNKKD...TPAKKKKKKKKNPQTT.....", // 30
        "........TTSIKPQQPS..AKP.PRT..FSDSSKKNPKA...ENNKKNKKKKKKASTT.....", // 31
        "........TEKBDPPPPS..PNNNNA....TSDmDKNANA..TENKKKKKKKKKKNPQT.....", // 32
        "........TEDKASDPQPT.TNKNNr.....SDmDKKNRR..QQKKKMMDKKKKKKAQS.....", // 33
        "........TS0DNSDPPTT..NNNNT.....SDmDKKNNA.SQEKDMImTDSDDKKAST.....", // 34
        "........SSKSPNDDPS...SNNSiTmmT.TSTIDKKKN.DPEDDSi..S.TTSKDPT...F.", // 35
        "........PSISTNDTPT....SDIDMDIMITSmSDKKKD.DSFSS....T....SNPT...F.", // 36
        ".......TTSKDSTD.TTT.TSDDAAArDDBMKiSBDKKT.NPEF......TT..TNSE...F.", // 37
        "........SSDTDTD..T.SSDrAAPPAANKDKimIBKK..NAEE......ST...DSE...F.", // 38
        ".......T.SK.DDD..T.SDANPEEEEQPNKKSmIBKKT.DNQEF.....T....DPQ...E.", // 39
        ".........ID.DDD...S0DNAEFEEEEPAKDmSDKKKS.DNPEF..........NDQ..FE.", // 40
        "........TTS.DTT...TKNNPEEE..FEQKMmmIBKKN.DNAQFF.........NPE..FT.", // 41
        ".......TTTTTKS....DKNNQET.....DDImSSBKKNTSKNPEFF.......mNAEFFET.", // 42
        ".......S.T.SDS...TNNNRAAS...FSKDmm00KKKNSTKKRPEFF......INPFEEP..", // 43
        "......T..T.DST..TDNANACNNTTEDKK0mmIIKNKKSTDKNrQEEFFFF.SDNPFQQD..", // 44
        "......S....K.....KNANKNNNAPNKKDSmS00KNKKDIIDDrrqPQQETSDKNQECAS..", // 45
        "......T...TT....TDKNNKKKKNKKKK0miSBBNNNKKKMISmmSDDSSSMKNAEPND...", // 46
        "..........T......TKNNKKKKKNKKDSmmS0DNNPANKKDImmmIIMIDKNNPENDT...", // 47
        "..........T......TKKNNKKKKNNDSmmm00DNPQPDKKKDIIIIDDNKKNPEPDS....", // 48
        ".........T.......TNKKNNNNNNDDSmmS0KNNFESQNNKArrrrNKNNAPESDI.....", // 49
        "..................KKKKNKNDDDImmSS0DNT..FEQANAPPPPNNPPQESDSi.....", // 50
        "..................SDDDDKDDDDSmiS0KNA.....FEEQEEEEPPQEETST.......", // 51
        "..................TDDKKDDDSSmTI0DDN.......FFEFFEEQEFF...........", // 52
        "...................SDDDDSImmmSDKKNT.........FFFFFF..............", // 53
        "...................DDDDSSSSSSDKKDT..............................", // 54
        "..................SKKDDSISSDD0ST................................", // 55
        "............S.....NKKDDKKST.....................................", // 56
        "...........SS....DNNT...T.......................................", // 57
        "..........TNS..TrNNT............................................", // 58
        "..........TNDTTARNS.............................................", // 59
        "..........SDNACAND..............................................", // 60
        "..........SNNNARD...............................................", // 61
        "...........SSNNS................................................", // 62
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

    drawMonsterMatrix(flip(matrix), palette)
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
        "................................................................", // 0
        "................................................................", // 1
        "................................................................", // 2
        "................................................................", // 3
        "................................................................", // 4
        "................................................................", // 5
        "................................................................", // 6
        "................................................................", // 7
        "..........................KKKKKKKKKK..DKKK......................", // 8
        "..........................KKKKBB00BB..KKDB......................", // 9
        "......................KKKKMMMMIVmmVVKKKKIVDK....................", // 10
        "......................KKDDIIIIIIIIVVBDKKImDK....................", // 11
        "....................KKMMVVmmmmMMMMmmVVMMmmDK....................", // 12
        "....................DDBBIImmmmIIMMmmIIIIImDK....................", // 13
        "......................KKMMIVVVmmMMIVMMIVIMKK....................", // 14
        "......................KKIIIVIImmIDmmSIIIMBDD....................", // 15
        "....................KKMMVVVVMMMMPQEEEEIMKK0VDK..................", // 16
        "....................DDBBIIIIIMIDQQEEEESIKK0IDK..................", // 17
        "......................KKMMMMIVQQJJJJEEEENKBMKK..................", // 18
        "......................KKMMIImmPPQJPPPQEENKDIDK..................", // 19
        "......................KKMMIVmmMMPQKKKKQENK..DK..................", // 20
        "......................DDBMIIISIIQJDDKKQENK...D..................", // 21
        "..................KKKK..KKKKKKEEQJWWIIPJNK......................", // 22
        "..................KKDD..KKKKKKQQQJWWIIQQNK......................", // 23
        "..................KKW.KKKKRRKKKKPQWWIIEENK......................", // 24
        "..................KK..KKKKRRKKKKPPFWIIEENK......................", // 25
        "..................KK..UUVVKKRRXXKKPQEEEENKKK....................", // 26
        "..................KK..00VVKKRRXXNKPPEEEENKKK....................", // 27
        "................KK112222VVVVKKUUPXKKQENKRRKK....................", // 28
        "................KKC12222VVVVKK0UPXKKQENKRRKK....................", // 29
        "..................KKKKKK222211KK0UmmKKSmDKBUKK..................", // 30
        "..................KKKKKK222211KK0UmmKKmmDKBUKK..................", // 31
        "..................KK0UVVKKKKKKKK0UUUKK0UKKKKCQAK................", // 32
        "..................KKUUVVKKKKKKKK0UUUKK0UKKKKCJAK................", // 33
        "..................KKUUUUVVKKQEEENKKK12NKKKCQQEDK................", // 34
        "..................KKUUUUVVKKEEEEKKKK22NKKKPJEEDK................", // 35
        "....................KKUUUUPPQQEEKKRROONKKKPQQQAK................", // 36
        "....................KKUUU0QJJJEEKKRRXXNKKKPJJJAK................", // 37
        "......................KKKKQJJJQQKKKKKKKK..DKKK..................", // 38
        "......................KKKKQJJJJQKKKKKKKK..DKKK..................", // 39
        "......................KK00NNNNNNB000ImKK........................", // 40
        "......................KKUUKKKKKK0UUUVmKK........................", // 41
        "......................KKVVKKUU00KKKKDD..........................", // 42
        "......................KKmmKKVVUUKKKKKK..........................", // 43
        "......................KKKD00VVVVKKAAKK..........................", // 44
        "......................KKKK0UVVVVKKC1KK..........................", // 45
        "........................KKDPQQSSKKDDKK..........................", // 46
        "........................KK112211KK0UKK..........................", // 47
        "........................KKDDSSSDKKKKKKDD........................", // 48
        "........................KK0UVVUUKKKKKKKK........................", // 49
        "......................DDBBUUBBKKKKNA11AADD......................", // 50
        "......................KKUUUUKKKKKKC12211KK......................", // 51
        "......................KKDDDDNN11ANNNAANNKK......................", // 52
        "......................KK11KKC12211KKKKKKKK......................", // 53
        "......................KKNNKKNNAANN..............................", // 54
        "......................KKKKKKKKKKKK..............................", // 55
        "................................................................", // 56
        "................................................................", // 57
        "................................................................", // 58
        "................................................................", // 59
        "................................................................", // 60
        "................................................................", // 61
        "................................................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'X' to Color(0xFFF5372D),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawBorghen() {
    val matrix = arrayOf(
        "...........................KKKKKKK.................", // 0
        "...........................DB0000BD................", // 1
        "..........................DDIIIIIIMD...............", // 2
        "...........................KDDDDDDDDK..............", // 3
        "............................DKDVIMMMDKKKKKK........", // 4
        "...........................DDM0DKKKKK1QTT11PDDDDDDD", // 5
        "............................DD000BDNAPSSSP11ANNNNNK", // 6
        "............................DKBVVIMDQDMMMMA12CCCCAK", // 7
        "............................D0IIIMP2BVVVUIMACMMMMDK", // 8
        "............................DDMMMBP2DIVVVIIMDDDDDDK", // 9
        "...........................DKDDCCQPDIVVVVIIIMDDDMDK", // 10
        "...........................NNAAAAPS0VVVVVIIIIDDMDD.", // 11
        "..........................K111AMMM0IVVVVVIIIIDDMKD.", // 12
        "....................KKKKKKKKKKDIIIIIIVVVIII0DDMMDD.", // 13
        ".................DDAAANKNNKDDNDDDDKDDDDDDMMMMDMK...", // 14
        ".................NNAZZANNNNNANNKNNNKNNNNNDDDDDDK...", // 15
        "..............NKNZAAAAADNNAKDNKKDDNKDAADDNNNNKKK...", // 16
        ".............KCZANKKKKNTE32NKKNADKKKKDNKKPFFFPQN...", // 17
        "............NCNNKKD00DKCEE32NKKKKPQFPNNASFFFEPAK...", // 18
        "...........DARKM0IIII0DNPP11NKKDKPPPENDPADPCDNN....", // 19
        "..........DNANDIVVVVVIMDNAAANKKNKPDNQDSQPDANNDK....", // 20
        "..........DKKKIIVVVVVVIMKKKNZNKKKPDKNPEFFQNNPQN....", // 21
        ".........KKDIVVMMUVVVIIIDKNZZANK1KNAKTFFFFQPQFQN...", // 22
        ".........K0IIIVI0MII0MIVDNZZZANKIQ1KPFFFFQPEFFFD...", // 23
        ".........K0IIMIVI0MMMMVVDNZAZAANDSS1NTEQQADPEFFD...", // 24
        ".........D0IIDIIIIMMDDVVDNZNCZANKUSQNSQPPANDSTTD...", // 25
        "........KIVVIDDIIIIMDKVVDNZNCZANKIII3NDPAPQDKKK....", // 26
        "........KVVIIIMKKMMKDIVIDKNZNAZANKDI1KSFEANKKK.....", // 27
        ".......D0VVVIIIDDKKDMII00DNZNNAZNKNPNDQFFQPDDK.....", // 28
        ".......KIVVIII00DKKDM0IMI0KAANNAANNAKAQFFEQPD......", // 29
        ".......KIIVIIVD00DDDDDIMIIKNCANNCRNKKNSTEPSDD......", // 30
        ".......KMII0MIKDMDKDDDKIIUIKNAAKNCANNKKKKKKS.K.....", // 31
        ".......KMMMMDKKKKKKDMDKDIIIDKNNNKKNNNNNNNNKKD.D....", // 32
        ".......KKDDKNARNKKDKKKDDMMMMKKNNNNNKKKKKKKKDD.K....", // 33
        ".......KKDKNNARNKKDKKKMDDDDDKKKNNNNKKKKKKKKD..K....", // 34
        ".......KNKNAAKKKKKKKNDDDDDDKDDDKKNNNNKKKKKD...D....", // 35
        ".......KNKKKKDDMDKK11KDDDDKDKKKMDKKKKKDDDD...K.....", // 36
        ".......KNKDMIDIDKKA22CKKKKDMKKKKKDDDDDDSS....K.....", // 37
        ".......KNKKDBDDDDK1J1NKKKKDKDDDDDDDDDDSS...........", // 38
        ".......KKKKKKKAPDKQ1AKKKKKKKDDDDDDDDDDS.....D......", // 39
        ".....DA2NKPEFFFFSKTDNDDDDDDDDDDDDDSSTW....DDDK.....", // 40
        ".....DKNKKPQFFFFSKTDDSSSSSSSSSSTTTTTW...NNNDDDD....", // 41
        ".....DKNNKNPEFQFSKEPNDDDDSSSSSSTTTTTDDDDDDDSSDDD...", // 42
        ".....DNNNKKDPSPQSK21CNKKDDDSSSSSSSSSKKNDDDSTTIMD...", // 43
        ".....DNNNKKKKKNPDKC22CKKKKKKKKKKKKKKMDPSDDUmFFIIBD.", // 44
        ".....DNCNKKKKKKKKKK11KKDDDPPDDDPAPSMIMDQFIUTFFIIBD.", // 45
        "....DDAANKKKKKKKKDKNNDDD0SFFIISFPDMIM0SFFIUTFFIMDD.", // 46
        "....KAACNKKKKKKKDMDDDDMMImFFIISQDDMMK0TFFIITFFIDD..", // 47
        "....KAZANKKKKKKDMDDSQSMIImFTDDSSDDDDKISETIITFFIK...", // 48
        "....KAZAKKKKKKKDDMSPQTIUTFFUDDDDKKKKKPPPSIITFTIK...", // 49
        "....KAANKKKKKKKDMIFFTIIUTFFIKKKKKKKKDKDSMUTFTIMK...", // 50
        "...DNAANKNNNNKKKBIFFTUIUTFFMKNNKKKKDSDNNKDSSDMK....", // 51
        "...KNAANKNNNNKKKDITFTUIUTFFDKNNKKKKDPANKKAPDDDK....", // 52
        "...KNARKNNARKDMKKDPETUIITFTDKNNKKKKKNPDMDPPDDDK....", // 53
        "...KCZRKNZZRKMIMDQQDDMITFTIMKNNKKNNKKD0IIQDKKK.....", // 54
        "..DNZZRKKZZRKSTIDDIIIIUFFSBKKNNNNKKKNKKKKNDDDK.....", // 55
        "..KNAZRKNNNNKPQIMDM00ImFTSDKKNNKNKKNNNNRNNKKKK.....", // 56
        "..KNNARKDKKKKDSI0MMMMIEFSMDKKNNKKKKNNNAACDDNNK.....", // 57
        "..KNZZNKKAANKDDIIIII0DPPDDKKKNNKKKKKKKKNSFFEPKK....", // 58
        "KAZZZNKKNCZANKDMMMMMDQQDKKKKNNNN.DNNNKDPNZZZZCNK...", // 59
        "..NNNKKKKNAZANKKNDDDDDDKNNNNKKD....KKKNACZZZZZCN...", // 60
        "......DKKNNNNNKKKNNNNNDKKNNN.........NKNANNNNNRK...", // 61
        ".......DDDDDDDDDDDDDDDDDDDDD..........DDDDDDDDDD...", // 62
        "..................................................." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'Z' to Color(0xFFD21E19),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGottos() {
    val matrix = arrayOf(
        ".....................K..........K.....................", // 0
        "...............................D......................", // 1
        "......................D........K......................", // 2
        "........KKD.....KD....D........K......................", // 3
        "......DD0S.K..KKK..D.KK........KK.....................", // 4
        ".....K.DKK00KKDK....KAK........KPN....................", // 5
        ".....KPDDTKDDKD....DAN..........N.....................", // 6
        ".....AQPDSSDKD....DACK..........KQPD..................", // 7
        "....KCEQADWSD....DNCPK....N.D...KPQPD.................", // 8
        "....DNPADDWW.....DAQFK.DKKAKKKD.NEF.D.................", // 9
        ".....KNNSWW......DAQFPNNAAXAAAAKPPN...................", // 10
        ".....KPNSW.D.......DQFAADDADDDDAPAN............D......", // 11
        ".DDDDAEPDDDK.......KDTDD0SSmSSIDSARD...........D......", // 12
        ".DDDNCQPADDK......DKD0B0ImTTTmS00DANK..........D......", // 13
        "...EPADDAPTK..DKKKDDDKBISTSSSmTSDBDNKKD.......KD......", // 14
        "...KKPFQAKKK...K0SSI0KDB0STSTmI0D0ISSK....KKKKD.......", // 15
        "...NPPFQPPKDKKD.KDB0SKKKB00S0BBBKSIBDK.DKKDK..........", // 16
        "...KAPPPCDKKKKDDKKDDDBASSKB0BSSPDDDDKKDDKKK...........", // 17
        "...NSCPCPSKKDD0DDKKKKIDQPKKDDSQA0DDKKKDD0DD...........", // 18
        "...DTDPDPSKDDIIIDKKKDSDAADKDDAAKSDKKKDDISIDKK.........", // 19
        "....NPPPAKDDDDDDDDDKD0SDDD0T0DDS0KKKDDISIDDDDKD.......", // 20
        ".....KKKKDDSmmIDDDNRKDKDDKDSBDDKKNDDDDDDImSSMDDD......", // 21
        ".....KANKDKKKKKKDDAANKDDSTSSSSDBKANDDDKKKDMSI0DD......", // 22
        ".....KQDKDDDDDDDKKACAKDDDSDDSDDDKCADDKDDDKKD0MDD......", // 23
        ".....KTDKDISSII0DKACANKBDDDDDD0KKCANKD000DKDDDDDK.....", // 24
        ".....KEDD0TS000SS0PCNANDDSDDSSDKNCCNDSSSI00VDKDDDK....", // 25
        "....KKPNDSSSSI00iiQCDPANDIFSIDKDARCQTiS000SiVDKKDDK...", // 26
        "..DD...DKD0S00DK00DAKAAKKKI0DKKDAAAr000BDBSS00BDKKK...", // 27
        "DDDD..DDD0K00BKKKDDNKANKGDKKDGKDADNDBDDKKDD0III000D...", // 28
        "K000m0DD0SKBBDKKKKNNKANKGDGGDGKKDKNNKKKKKDB0SSI0mm0...", // 29
        "K000TSS00SKDDKKKDKKKKDKKKKggGKKKDKKKKKDKKD0SS00SiiV0D.", // 30
        "KDBDS0KKDDKDDKKDD000SSSSI0KKBISSSSI00DDKKKDDDBITiSSI0K", // 31
        "KKBIiSS0DKKDKKKD00ISSSTiTSSSSmiiSSSI00DKKKD0S000SS000K", // 32
        "KKD0m0DDDKKKKKKKDD00SSSVS0000SVSSSI00DKKK0SISSI0B000DK", // 33
        ".DDD00DDKKKKKKKKDBB0III00BBBB00III0BBBKDD00IVSI0BBBBDK", // 34
        "..DDDDSDKKDDKKKK00BD000DDDDDDDD000BDB0KD0DK0mSI00DDDDD", // 35
        "...NKKNNKKDDKKKKD000DDDDB0SS0BDDDDB00DKKKKKT000S0KKDD.", // 36
        ".....KPDKKDDKKKKKKB000D00000000D000BDKKKKKS0BST00KK...", // 37
        ".....KPDKKDDKKDKNDNKKDDDDKKKKDDDDKKNDNKKDI0KBISBSK....", // 38
        ".....KSDKKDDKKDKNNNNDKKKDDSSDDKKKDDDKNKKDDDK00DKIK....", // 39
        ".....KTDKKDDKKDDKKDDDNDDDDSSPDDDNDDDKKKKKKKD0DKK0K....", // 40
        ".....KTDKKDDKKDDKKDDKSTFSKKKKSFTSKDDDDKKDDKSDKKK0K....", // 41
        ".....KTDKKDDKKDDKKDDDFDBDDPPDDBDFDDDKKKKDKKDKKKKDK....", // 42
        ".....KTDKKDDKKDDKNSSPSTSDKKKKDSTSPSTTNKKDKKKKKKKK.....", // 43
        ".....KTDKKDDKKDKNDPDDNDDDDSSDDDDNNDPPDNKKKKKKDKK......", // 44
        ".....KTDKKDDKKKKNDDNNKDKDSSSDDKKKKNNDSDKKKKDDDDD......", // 45
        ".....KTDKKDDKKKKK0DKKDDKKDKDKKDDDDDKDS0DKKKDDDDD......", // 46
        ".....KDDKKDDKKK0TSSSKDDKKDDDKDDDSKDISiS0DKKDDDDD......", // 47
        ".....KPDKKDDKKDiSSmTSKDKDIDSDKDSKSmTSSTIDKKDDDDD......", // 48
        ".....KSDKKDDKK0VS0mimDKKDIDmDDDDDmTTS0V0DKKDDDDD......", // 49
        ".....KSDKKDDKD0II0Smm0KKDIDmDKDK0mTTS0I0DKKDDDDD......", // 50
        ".....KTDKKDDKD0II0ISS0DKDIDIDDDD0ImmSI0BKKKDDDDD......", // 51
        ".....KTDKKDDKDIiiSSI0DDKKKKDD..KD00miiS0DKKDDDDD......", // 52
        ".....KPDKKDDKDSSiVI0S0KK...K....KKB0SSS0DKKDDDDD......", // 53
        ".....KPDKKDKKD0SS0ISS0DK........K00B0S0BKKKDDDDD......", // 54
        "......DDDKKKKDDD00000BK.........K00000DKKKKKDDDD......", // 55
        "......DDDKKKKKNDDD00BKK.........DD000KDDDNKKKDDD......", // 56
        "......DDDKKKKDSFFDDDDKN..........DKBDKTFQDKKKDDD......", // 57
        "......DNNKKKDDNNDTSNPK...........DDDKFDNNNPKTSD.......", // 58
        "......DNNKKDEFETFSPDDK.............DDDNSTEFPDS........", // 59
        ".......DNNNDDPPPSESDD..............NDDSQSPPDND........", // 60
        "........NDDPPPPDPSDD...............DNDSPPSPPDDD.......", // 61
        "........DDDTTTEDDDD.................DNNDSTTTSPDD......", // 62
        ".........KKKKKKKK....................KKKKKKKKKD......." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'X' to Color(0xFFF5372D),
        'g' to Color(0xFF199123),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawRoundworm() {
    val matrix = arrayOf(
        "......................................r..DNr..A.................", // 0
        "......................................r..NNR..A.................", // 1
        ".....rQ........................PP..rNKNKKKKKKKrq.Pr.............", // 2
        "Q....ND.....Q...............EF.DN.DNNNBDCDDRDGND.Ar..Q..........", // 3
        "rP..KKKKNA..r...............PQ.DKKKKND0SP00rDBKKNNN..r..........", // 4
        "..NKKNNtrrNKKKNN............NNZDDPS000rDB000DNBDXDBKKK....rND...", // 5
        "..DDKNNBNAKKDNND............DDrBBPD000rDB000DDDDPDBNNK...PRD....", // 6
        "....KKKKKKtBUrrBKKKD...rAKDtUUU00BDZ0c0U0rrt00rrUU0PrtKNZRK.....", // 7
        ".......KKKBDN00000BDD..NACD0rD0Uc0DR00PD0000cc000Dr00ctDNS......", // 8
        ".......NDDBDN00c00ttK..KNPD0rDUcc0DN00PD000ccccU0rr00ctBK.......", // 9
        "..........KKKBBZD0ccBKKtBBNN0Uc00PrB000DZ0SXSc000U0Btt00tDK.....", // 10
        "...........DKBBr0U0tBKKBBtDDS0c00rDB0UUSPcSXSU0000tBBBBBtGK.....", // 11
        "............KKKttctBKBttB0ccPPc00ttU0Udddc0r0000BttKKKKKtKK.....", // 12
        "............KBB0ttKNrNNKtccdSSr00Bt0PPrSc00000tBKB00cSc00BtD....", // 13
        "............KBt0BtKNrNNKtcdddSr00B0UPPrDc00U00BBK0Uccdcc00BB0...", // 14
        "..........KKttttKKBtttt0Ucddcc0000UdddcU0000BtBtcdd0000000000K..", // 15
        "..........DB0tttKKBtBBB00cccUU0000cdccUU000000000UU00UUUU0000K..", // 16
        "........0KtBUBttKK00ttt00ccc000ttccdcc00000000000000UcddcU000K..", // 17
        ".....DD000ccc00KKB00ttt00cU0ttBttccc00000tB0UctBB0cdUtBBKGBBt00B", // 18
        ".....KDt0Uccc00KKt00ttt00c00tttttccc00000ttU0cttt0cS0tDKKKtBB0BK", // 19
        "....KBB000000U0KKt00UBttBUttBBtttcU000UBt0UcttKBcU0KKNAANAANDtBK", // 20
        "...DB00Uccc000BKBt00BtttBUtttBBtt00B00BtB00ttBttUtBDDASSANNNNDDK", // 21
        "..KDB0Ucddcc0BBKBt00ttttB0tttBKtt00B00ttB00tBKcU0BKAAATTANKKNANK", // 22
        "..KDBcccU000BKKtttBB0ttttttttBKttBB0ttt00BttBB0BKNNNNK00KKKTTKKK", // 23
        "..KBtccc0000tKKtBBtt0BBBBBtBttBBBBB0Btt00ttBttBDKDSNNKDDKKKSSNNK", // 24
        "KKKBUcc000000KKtBKtttBKKBt00tttKKBBtKKt0UttKB0KNNTWKKKKKKNN00PDK", // 25
        "KKBB0U0t0UVcUKK0BBBB0BBBB000BBBKKtttBBttBttB00NNKDStDKKKNNNDDPNK", // 26
        "KKt0000t0UdccKKUBtBBUtt0UU00tBKKKtBtttttBttt00NNKNNcDKKKNNNAAPDK", // 27
        "KKt00tt0VdccUBKB0000cU0cccU0000BtKBtBBttBtttKKNNKKKKKKKKNAA0SWSK", // 28
        "KKtB0Bt0ccc0BBBt0U00DD0DDDD000U0tBBtKKttBtttKKADSDDKKKKNNAADSTDK", // 29
        "KKtttBt0ccUBKttUUcU0AANAAAAA0UcU0ttBKKtBttBUDKPQWdcKKKNNNAAAAPNK", // 30
        "KKtBBBB000BBKttUttBBAAANAPANDDDtt0BKBBKBtBBUDKPDDDKKKNNNADcPAN..", // 31
        "KKBBKtt000BGKBtUtttDAAANAPANNNNDt0BKtBKBtKBUDKPAKKKKNNNNADcSPK..", // 32
        "KKKKKttUttKKK0U0DPAAAANNNNNKNNNAANDtKKKBtKKUDKAANNNcDNNNAANTTK..", // 33
        ".DKKKBt0ttKKBBBSDCAAANNNNNNNNNNAANDDKKKKBKK0BDNNASSDDNDDCAAS....", // 34
        "..KKKKBttBKKtKDQPANNNNNNNNNNKKNNNNNNKKKKKKKt0UKNPTWNNN0cPPPND...", // 35
        "..KKKKBtBKKKtSPNNKKNNAANNNNNNNNNKKNNNNNKKNNKBt0DNDDPANTTPDN.....", // 36
        "..KKKKBtKKKKtSPKKKKNNAANNNNNNNNNKKNNNNNNKNNKKt0BKNNPDNTTPDD.....", // 37
        "..NNKKKKKKKKBKKNNNAAANNKKNAAAANNNNNKKKKKtKKKKKKBtBBtBtKKK.......", // 38
        "..NKKKKNKKKKBKKANNAAPAANNNNNNNNNNNNNKKKBtKK0DBKKKKKKKK..........", // 39
        "NNKKKKNNKKKKKKKAANAAPCAAANNNKKKKKNAAKKKBtKBdU0KKKKKKKK..........", // 40
        "KD..NNNKKKKKKDANAAPPAPPAAAAANNNNNKKKADKBtKBUUcttctBKS...........", // 41
        "D...NNNKKKKKKAANAAPPACPAAAAANNNNNKKKDDKBBKB00cttcttB............", // 42
        "....KKKKKKKKKPPAANAAAAAAAAAAAANNNNNKKKBBKBBttcU0t00tDK..........", // 43
        "..D.....DKKNPAANNNAANAAAAAANANNNNANNKKtBKttt0U0BK00tBK..........", // 44
        "..K.....DKKDQANKKKAANNNNNANNNNKKKAANKKBBKttt000BKB0tBK..........", // 45
        "..........PDKKKKNNNAPPPPPAAAAANNNKNPDKKBttBKB0tBBBBttBDD........", // 46
        "..........DD.NKNNNAAPPPPCAAAAANNNNDDKKBBttBKB0BttBBtBBKD........", // 47
        "..........K..DKNNNPPAAAAAAAANNNNNADKKKtttttKBUBttBKBBKKD........", // 48
        ".............DKANNAAAAAANNNNNNNAAPDKBtttBttKBttBtBKBBKKD........", // 49
        ".............DNAANNNAAANNNNNNNNAAPDKttttBBBKBttBtBKtBKKD........", // 50
        "............NPPAANNNNNNNNNNNNNKKKAANKBtttKBBBttBKKKtGKKD........", // 51
        "............NDDNNNNNNNNNNNNNNNNNNNNNKKBBBKBBBBtBKKKBKNND........", // 52
        "............PNKKNNNNNNNNNKKKNAANNKKKKKKKKKKtBBtBKKKKKNADK.......", // 53
        "............N...DKKNNNNCCAAAAAANNDADKKKKKBBtttBBKKKKKNNNDDDD....", // 54
        "............D....KKNNNNPPPAAAAANNAAAKKKKKBBBtBKKKKKKKKKKDNKKD...", // 55
        ".................KNNAAAAAAAAAANNNKNPNKNNNKKKKKKKKNNKKKKKKKKKKKKK", // 56
        "................DKNNAAAAAAAAANNNNNNDNNNNNKKKKKNKKNNNNDKKKKKKKKD.", // 57
        "...............KKKNNNNNAAAANNNNNNNNKNNNNKNNKKKNNKNNNACNKKKKKKK..", // 58
        "..............DNNANNKNNNNNNNKKKAAANKKNANNKKNNNANKKKKKNKKKKK.....", // 59
        ".............DKNNDNNKNNNNNNNKKKAAANKKKAAAKKNNNAANKKKKKKKDDD.....", // 60
        "............KKKKKKKKKKKKKKKKNNNNNNNNKKNAPKKKKKACPNKKKKKD........", // 61
        ".......................KKKKKNNNNNNNNKKNNDKKKKKNND...............", // 62
        ".......................DKKKKKKKKKKKKKKKKKKKKKKKKK..............." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'X' to Color(0xFFF5372D),
        'Z' to Color(0xFFD21E19),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'q' to Color(0xFFFA649B),
        'r' to Color(0xFF961950),
        't' to Color(0xFF0A6469)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawCyclone() {
    val matrix = arrayOf(
        "..................NKKD............", // 0
        "................DNNARCD...........", // 1
        "...............DAONCKKKAN.........", // 2
        "...............DCCRKNNRNND........", // 3
        "...........DNRNKNRRNCRRNND........", // 4
        "..........NRRRKNNKKKKNNKND........", // 5
        "......DDNCOCRRKKRKKNNANNNN........", // 6
        "....DNRKKNNCRRNKRNCNNNKNKRN.......", // 7
        "...DRRNRCCCNRNKKKNRRNNRNKRCN......", // 8
        "...NRRNCOOCRAKRKKNNACANNKNCKN.....", // 9
        "..NRRCNAAAROCNKKKRKNNNNNKRNNOA....", // 10
        "..NNNNKAAANNCNKKKNRCOCRKNRANNN....", // 11
        "..KNRARNKCCCKRRKKKNRKRKKNNKKNRA...", // 12
        "..KROOCCCARRRCRKKNKKKKKNNNCCRCON..", // 13
        "..NRCOCAKAKRCOCRKKNRKKRCRROOROON..", // 14
        "..RNRCNRCNKRCOOCNNNKNROOCAOCACON..", // 15
        ".NRKKNROOCNROOOORRCKRCOOOCCNAOCN..", // 16
        ".NRNRRCOOCKRCCCRRACNROOOOCRKACA...", // 17
        "..RKKKNCONNRCCCCRCCKRCOOCRNKCRD...", // 18
        "..KNRAAANKKKROOORCNNKRRRNNKKAN....", // 19
        ".DRRNAACNKRNNNANKKNKNNNNRCKAN.....", // 20
        ".NRNCCCNCNRRONKNRRNNROOCRANCD.....", // 21
        ".KKOOCOCAKRROCAAOOKRRAOANKRA......", // 22
        ".KNACRRONKKNACRCOAKKKKNNKKRD......", // 23
        ".NRAONKNCCNRKKKKKKNRRAANKRNA......", // 24
        "KCCKCRNRNCNRRCCCAKRACOAKNNNRC.....", // 25
        ".ACNCCKNAOAKRCOONKRKKKNKNKAOON....", // 26
        ".KCANNKRNOAKKNNNKKRRNNKKKNAAON....", // 27
        ".NNORRKNKKKKKNKNNKKKKKNKKRCANN....", // 28
        ".NRNNKKKKKNNKRRKNNRKRNRNKNKNKND...", // 29
        ".KRCNKKKNNNNKRRRKNRNCKKRKKNRACOD..", // 30
        "..DD.KKNNRRRNNKKKNNNNRKKKKNOARONA.", // 31
        ".....KNNNNNNNKNRNNKKRANRRRKANRCNCN", // 32
        ".....NCOOOCNRRKKKKKNRONRRCANKRONCN", // 33
        ".....NRCOOORNRNNRNNNCCNRCOOKKNNNC.", // 34
        "....DRRRRRNKNRONRNRCNKRCOOOCKKRRD.", // 35
        "....DRRNNNRRNCONNNRCOARCOOOOKDD...", // 36
        "....DRKNRRRCACCKKNRCONNKRAAAK.....", // 37
        "....NRANRRCOCAA...RCCNRRNKKNAN....", // 38
        "...DCOOCKCOOONA....RKROCRRKROC....", // 39
        "...NCOOCNKNNOND.....NNCOCRNAOOA...", // 40
        "...NROOCNKRRNND......KNACRNOOOA...", // 41
        "...KRRCNRNNRRN........RNNKNCOOA...", // 42
        "...NNNNRRRNAAK........NKKKNRCOA...", // 43
        "...NRCCRCRNNAN.......NRRRNNNNAD...", // 44
        "....RCORCNRAON......NROOAKRRNN....", // 45
        "....NRANKRAOORD.....RCOOCNOOCN....", // 46
        "....DKNNNRCOORD.....ROOCNROOA.....", // 47
        "....DRRNROCCCA......ACCONRCOA.....", // 48
        "....DRRAAOCRRN......RRCCKNRR......", // 49
        ".....RCONOOCN.......DAONRRN.......", // 50
        ".....NAONOOON.......DRANOOD.......", // 51
        "......RCRNNRD.......DKKAOC........", // 52
        "......NRCORN........NANCCND.......", // 53
        "......NNCCNKD.......RCNRAAND......", // 54
        "......CKNNNCN......DNRKACANAN.....", // 55
        ".....AONNNNCN......NRNANNNNOOCDD..", // 56
        "....NRRNRNNNA.....NCAACANOANCNNRC.", // 57
        "...DRCARNNCOA.....NCCNCCNOOAKCCAAK", // 58
        "..DRCNCRKNNNA......NRNRNNRCCNCOONN", // 59
        "..NNNKAKKAOON.............DNKNNN..", // 60
        ".NNCACONCOOON.....................", // 61
        ".NKAKRRNCRRRN.....................", // 62
        ".KKKKKKKNKKN......................" // 63
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'R' to Color(0xFF821414)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawNeptoDragon() {
    val matrix = arrayOf(
        ".......KKKD.....................................................", // 0
        "......DBBBB.D.........................D........DD...............", // 1
        "......DUUU0KK........................DKK.......KK...............", // 2
        ".......KKKKUUBKK....................KBUUKKD..DKUVBD.............", // 3
        "........KtcKKBUUKKD..................DKKUUUKK..KKBUK............", // 4
        "........KgdtgtBUKDD.......DDD......DD...BBB000...0B0D...........", // 5
        "........GLdddttUKtcG.....DKKK......KK...DDDUV0D..DKVB...........", // 6
        ".....DKKddddddcKUBGdK...KBUUUBKK.DKUUBD....KUVUK..KUVBD.........", // 7
        "...KKBUUUdddddcKUBGddDD..DKKBUUUKKKKUUBKK..KUUUK...KUBD.........", // 8
        "..DBBBBBBUcccdcKBKKDdDD..DKKKBBUBBBKBBBBB0DKUBBKD..KUU0.........", // 9
        ".DBUUBGGGBUUUccKKKKKcDD..DKKKKKBUUUKKBBBBBKKUBKKK..KUVUK........", // 10
        "KBUKKtdddDKKKBBBBBBKK...KBUUKKKKKBUUUBKBBBBKK0VVVBKKUUUK........", // 11
        ".DGddddddDKBUUUUBUdUBBD..DKUUUBKKBBBKBBBUBKUUUUUUVIKUBKKK.......", // 12
        "...DdddtUBBUUUUUBUUUUU0....KUUUBBBKKBBBBUUBUBBBBUUUKBBBBB0......", // 13
        "...KcctKBBUUUUUUUUUUUUUK...DBBBUUBKKBUUUUUUUBKKNUUBKKBUUUBD.....", // 14
        "...KKKKBBUUUdUUUUUUUBcdUKKD.KKKKKKKUUUUUBUUUUBN22AKUUUUKKBUK....", // 15
        "..KUUUBBUUUddUUUUUUUBcddUUUKKKKUUUUBBBUUKBUKUBN22AKUUBN22AD.....", // 16
        ".0BBBBBUUUUcUUUUUUUUBUUcUUUKBBBBBBBBBUUBBBBKBBBAAKKVUUBA2AD.....", // 17
        "DB0GGBUUUUUUUUUUUUUUUUUUUUBKUUBGGKKBBUBKBBBKKBBKKKKVUUUK1DD.....", // 18
        ".DGddcUUUUUUUUUUUUUUUUUUUBKUKKGddcUKUBKBBUUUUBKUUBKKVUUUKBUK....", // 19
        ".DGd0UUUBBUUUUUUUUUUUUUUKBUKddddUBGdUBKBUUUKKBUUUUUUUUUUUUUK....", // 20
        ".DKUUUUUcUBBUUUUUUUcUUUBBBBtdddcBtcdUBKBUUBK1DBUVVVUUBBUBUUBD...", // 21
        ".DKBUUUBdcBKBUUUUUUcUUBKUBKcddc0GcddUBKBBBBK2AKUUVVUUBBUK0UUK...", // 22
        ".DKBUUUBddcKKBUUUUBBUBKUKKKKdUUKddddUBKBKBUK2NKUKBUVUUUUUUUUK...", // 23
        ".DKBUUUBBBBKBBBKBUUUUUBKBBBKKBUKKtdUKBBUBBKUKKKK2AKUKKKUKBUVUBD.", // 24
        ".BBUcUUUUBBBcUBBKBUUUUBBBBBKBBBBKGtUKBUUBBBUKKKB2AKSKBBBBBBBUU0.", // 25
        "KBUUdUUUUBGUdUUUKBBUUUUUUBBKUBKBKKKUKBUUBUUUKKKU1DN2KB0KUBKNUUUK", // 26
        "KBUUdUUUKtddddddUBKBBUUUdUBKUBKBBBKKKBUUKBBUKKN2KBU2K11K2AN2KBUK", // 27
        "KBUUUUUUKKGdddcKKSTKBBUUUUBBKBBBUUUBBBBBKBUUKKKKK11KUS1K2AN2KKD.", // 28
        "KBUUUUUBBBBDDtDSST.SBKBBUUBUBUUBUUUUUUBBKBBUKKKKSDNKSAA1ASSA....", // 29
        "KBUUUUUKUUUKKKDT....DDDDUBBUUUUBUUUUUBBBKBBUKKNKTSDK1AD1D..D....", // 30
        "KBUUUUUKdddUUUUK........KKKKBBBBBBBUBBKKKBBUK11KKSTKK..K........", // 31
        "KBUUUUUKdddddtD.............KKKKKKKKKS...DKUUS1K2AN2K...........", // 32
        "KBUcUBBgdddddDD.............SSSKKBBBKS...TSBUUDK2AN2K...........", // 33
        "KBUdUKGddddddDD..............TSKKBUUDT..TTSDUUBK1DK2K...........", // 34
        "KBUBBKGddddddDD..............DKUUBKKT...KKD.KKKUUUUUK...........", // 35
        "KBUUUBGdddddGS..............DB0KKSTT.SKKUUUKTTTKKKKK............", // 36
        "KBUUUBKtddddKS............TDBBBttDDDDDtBBBBS.SDKKD..............", // 37
        "KBUUUBKKccccKST...........TKUBGddtGGGccUGKD.TDKKK...............", // 38
        "KBUUUBKUKKKKUBD...........TKUBGdUcdddcUKdDD.KBUUUBD.............", // 39
        "KBUUUBGdUUUUdDD..........SKUKtddddddUBGddDKKUBKKK...............", // 40
        "KBUUUBGdccccdDD.......TS.DKUKBcdddccBtcdU0BBBttttD..............", // 41
        "KBUUUBGddddddDD......TSKTDKUKBUddUUUGtddUUUUGtdddDN.............", // 42
        "KtddUBGddddddDD......DKBKKKUKgddUBGddUBBUUBBdUBBBKKKK...........", // 43
        "KBUdUBGddddddDD......DKBdDKUKgdUUBGd0UUUUUUUUUUUUUUddDD.........", // 44
        "KBUUUUBGtcddtB0S....SBBUdtKUKtUKUU0VUUUUcUUUUUUUUUUctKD.........", // 45
        "KBUUUUUKKtccK0UK....KBUUdtKUKBBKUUUUUUUBcUUUUUUUUUUUBKD.........", // 46
        "KBUUUUUKUBKKUccK....KBUUdtKUUBKUBUUUUUBBBBBBUUUBdddBUUBK........", // 47
        "KBUUUUUKdcUUddcK....KBUUddcKKBUddUBBBBBKBBBKBBUUBcdBUUBK....N...", // 48
        "KBUUUUUKDcccdddtST..KBBUcUUBBBUdUUBBBKBBKKB.BKBUBUUUUUUBD...B0..", // 49
        "DBBUBUUKKcddddddKS..DBBBUUUUUUUdBBBBKKKBDDD.DDDBBBBUUUUBK..DBKD.", // 50
        ".DKUBUUBUcddddddKS...DKBUUUUUUUBKBBKKKKK.......KKKKUUUUBBKD.KKD.", // 51
        ".DKBBBUUBBGddddddDD..DKBUUUBBBBBBBKKBKD.......KBKKKKBBBBBKKKKBBK", // 52
        ".DKBBBUUBB0ddddddctD.TSKBBUBBBKKKBBBBKD........KBBBKKKKKKKKBBBB.", // 53
        ".DKBBBBUBUUdcdddddcGTTSDDBBBBKKKKBUBBB.........DBBBKKKKKKKKBBB..", // 54
        ".DKBBBBBBBBUKtdUdtGdKKD..DKKKKKBBBBKK...........KKKBBBBBBBBKK...", // 55
        ".DKKKBBBBBUUUBKKKBUBBBBKKBBBKKKKKKD................KKKKKKKD.....", // 56
        ".BBBKKBBBBBUUBBBttBcBBBBKKBUBBBKKD..............................", // 57
        "DBBUKKKBBBBBBUUUdttdBBBBKKKUBBBKK...............................", // 58
        "..KBBKKKKKKKBBUUUUUBKKKKKKKBBBBBBBD.............................", // 59
        "...KKBBBUUUKKKKKKKKKUUUBBBBBBBBBBBBK............................", // 60
        ".....DKKBBBBBBBBBBBBUBBKKKBBBBBBKKB.............................", // 61
        "......DDDBBBBBBBBBBBBBDDDDDBBBBBDDD.............................", // 62
        ".........DKKKKKKKKKKK......KKKKK................................" // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'D' to Color(0xFF4B4B4B),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'L' to Color(0xFF50BE28),
        'N' to Color(0xFF502314),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        'g' to Color(0xFF199123),
        't' to Color(0xFF0A6469)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGaruda() {
    val matrix = arrayOf(
        "...........................................", // 0
        "...........DA......................DC......", // 1
        "...........PD......................DC......", // 2
        "......AD...P.......................DC......", // 3
        "......PN.CAP.......................P.......", // 4
        ".....DQA.CAP.......................P..P....", // 5
        ".....DQQKAAP......................AP..PD...", // 6
        ".....DQJKZAP......................PD.DQD...", // 7
        "...DP.AJCZRQA....................PCCDPQN...", // 8
        "...DQD.PQXRJA....................PAZKPC.CD.", // 9
        "...DQD.PQORQQD..................PDZKAQAKQD.", // 10
        "...DQPDPAJZAQD.................DQNZNACACQ..", // 11
        ".DCDPQNPNPXNQD.................PDZRPAPNOA..", // 12
        ".DPSDQCPCPONQP................AQAZAPACAJND.", // 13
        ".DQNSPCPONCNLg...............DQLNZAACNCPKP.", // 14
        "..PAAAPNJPACCQA..............PQPRNONCPANCP.", // 15
        "..AQAPPNJPKRCQA..............DQAZNPKPPNCOA.", // 16
        "..AQAQCKOXAZAPQD..D.DGg.....DLQARKNNONCXO..", // 17
        "...PPPQKAXORRglD.RNRGK......NQPRRACZCKCXA..", // 18
        "...POAJONPOARPQPNRZKNG......CLgRAJARNPAP...", // 19
        "...DJPCXANCJXNlgRNRNN......DQLNZOJRNQPN..DD", // 20
        ".PD.PONPONACXALgRNRRN......DLQNXONKQQD.DPP.", // 21
        ".PP.DOOAONQNOZCQARRRN......AQlNXCKPJPNPOJD.", // 22
        ".DQP.PXCAAPONCgPGZKRZRRA..DQLGXCNKOJNCXCD..", // 23
        "..COPNCCANAOAAGQNRZKZNCRP.DQgGAKPNXPNZASNC.", // 24
        "...OXCNAPANJOONgNKRXXZRAPAKLDNKPOACKRNACQP.", // 25
        "....OXOAOPKNOXNDKCZZOAPJJQCKGKOXCRKRZOJCD..", // 26
        ".....OXOAOOCAONgNRZRPNKNNKNNKNOCNKNARRDS...", // 27
        "DPDD.DPOPNOXOJNGNKZRKPNKRRRNRANNOJPAJPNDDAD", // 28
        ".DJOCAKNPCNOXXNGAPXXRNAACNKRKCCOXJNZXJQQPD.", // 29
        "..PPOXOONNPNPOZGGNARNZZRKKKKOXOOAKRRNKKD...", // 30
        "....AAJXXPCAKNNGgNNZRJXZALgAJAAKKKRZJPPDD..", // 31
        "......DPCZZXJCNCgGAGAKPACgKNNNJJJJNNAPPPP..", // 32
        "..CDDDDCCKNNRCOZGgggggDGggKNACXXJOAARRKKK..", // 33
        "..DPPJCNNPQARRNNGgggggggGKCOPNPPNNRRCQQPP..", // 34
        ".....AXOCCANKNCZGgggggggGKKPJOPNRXZCKNN....", // 35
        "......DDRARANOZRKgggggGGKKKRKKKNCNCACCD....", // 36
        ".........CPCNNKZNggggGGGggGKNZCPXPKCPD.....", // 37
        ".........KNAAAPZNggggGggggggGCOACXCD.......", // 38
        "......APPPPNCOOANKgGKAgggGGggGOASPOD.......", // 39
        "........CCAJJRKJAKAACNggggGggGCC..PD.......", // 40
        "........NPPCNKAXASACPKgggggGgGNOD.AD.......", // 41
        "........AASKPCPASGQPAKGgGgGGGNDPD..........", // 42
        "...........PANCSAGNNPKKGggGKKCKP...........", // 43
        ".............D..ANPAPGGKGGGK.D.............", // 44
        "...............NLKCAQGGGKgGK...............", // 45
        "..............PONPCACGGGKKAC...............", // 46
        ".............AJCACPNGGNKKDNKC..............", // 47
        "............ADCNDNQ..KPGggGGG..............", // 48
        "...........AOCDNQKD...NGggggGG.............", // 49
        ".........DRlJA.DLD...AAGGKKGGG.............", // 50
        ".......AAOCGN...DP.....KGGGGK..............", // 51
        "......AOPAARN....lg....DGGGGGD.............", // 52
        "......DPRZA......DPP....GtGGGG.............", // 53
        ".....DPCRZ........CCN...DggGK..............", // 54
        ".....DCRCO........GDD....DggGK.............", // 55
        ".....DXNOXAD......GG......DgGGCGG..........", // 56
        "......ZNXOQOZA.............DgGDAKK.........", // 57
        "......ANZJJXACD.............GGDDGK.........", // 58
        "........RJXO1KgD............AAAGGG.........", // 59
        ".........CONGGGg.............DGgGGK........", // 60
        "..........DgKGNP...............DgggGD......", // 61
        "...........DADD.................DggGGAD....", // 62
        "..................................G..D....." // 63
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'G' to Color(0xFF0F5019),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'L' to Color(0xFF50BE28),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'X' to Color(0xFFF5372D),
        'Z' to Color(0xFFD21E19),
        'g' to Color(0xFF199123),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGoldor() {
    val matrix = arrayOf(
        "........KKK.....................................................", // 0
        "......DNppprN...................................................", // 1
        ".....Dpppppppr.ND.............NKN...............................", // 2
        "....IppppppppprrrrI........rrrppprrrr...........................", // 3
        "...IppppppppppprNprrr...rrrpppppppppprrrrr..rrrI................", // 4
        "..Dppppppppppppprrppprrrpprppppppppppppprrrrppprrr..............", // 5
        ".DpprNNNNNrppppppKrppppppppppppppppppppNrppppppppppprD..........", // 6
        ".NpK.......KrpppprKrpppprrpppppppppppprppppppppppppppprK........", // 7
        "DrD..........NpppprrppprrppppppppppppNppppppKKKKKKppppppN.......", // 8
        ".D............DppprKppprrpppppppppppNppppNKKpppppppprppppD......", // 9
        "...............DppprrpprrprppppppppKppprNpppppppppppppppppD.....", // 10
        ".................rppNrprKANrppppprKrpprppprrrrrrrrrppppppppr....", // 11
        "..................rprKprKNAAppppprKprrpprr.........rrrrpppppI...", // 12
        "...................rpKrprK1NppprNKrrK1NN..............INppppr...", // 13
        ".............D1N1N.DpKKprK1NpppAAKpN1D..................NKNppr..", // 14
        "...........N..N1N21KKrKprK1NKprNNrNK1D.....................rpr..", // 15
        ".....1N...A2NKKN1A22NKKNrK12KKKKKKKK1D......................rpM.", // 16
        ".....1NN.N2AA1NKN1N22NKKKKN1NKNAAAKK1D.....NND...............rr.", // 17
        "......12KA2NNA11KAA112NKKAKA1K11NA1AA...DNN211................rK", // 18
        ".......AK11NAK11NAA1221K11NNNA11NA2NK..1A11A..................I.", // 19
        "........K21N1AN1CAA1221N2NKNK1NA1ANKAK11A12N....................", // 20
        "........K21N11K1AKAA11NKA1NA1N11111KN11AA12K....................", // 21
        "........K11KA1K11NAA1N1AK1NK1KNNAANKK11AA12NAD..................", // 22
        ".....1N.KAKNNKSD1AKAN11AKA1NAN1AKANK1K12AAK221A.................", // 23
        ".....1NNKN1N11DTD1NKK111AK1NK1NKKKKK1K11AKK1222N................", // 24
        "......121N1NCD..TDDANNK21KAAKA2121K12KANA1NK122N................", // 25
        ".......1NN1NNT....TD11KAAP11KKAA1NN11NNK111NKAANA...............", // 26
        "..........A1NT.....S11KAS32ANKKKAAAA11DDN111KAN12A..............", // 27
        ".........D11KDT....TDNK121QF2AKPQ11SEN...DNNK1A12EK.D1..........", // 28
        ".........N1AACD.....TKAN1QE32212EQ23PD.....K11A12FKD1D..........", // 29
        "........N21A111D.....TKKDE2211112EP1D.......N11N1N121...........", // 30
        "........K1N11N11D.SSTTKAKN11NKN111KK.........N1N1N11............", // 31
        "........KN11NTDA..CANNK11CND12QNNKK..D.......KAN1KDD............", // 32
        "........K1A1NT.....121KN122EAACQPNNNA........NA1AK..............", // 33
        "........K1NNA.......CAKNAAAAAAAANKN11......AA11N1...............", // 34
        ".........A1A1AA......K111ANN111NN1KNN.....A1NNA1NQK.............", // 35
        "..........K.DKN.....D121221NKKKN1A111D...D1NTTSA1NK.............", // 36
        "....................122A3QNKA1AKANC1221...D....ANAK.............", // 37
        "...................A221N1AKA111AKKKK111A.....KN1K1K.............", // 38
        "...................111NCKA1NA1AKA111NKK.....D1AKKAD.............", // 39
        "..................A1NN11111NK1KN1111111A.....D..AD..............", // 40
        "..................NN112222CKDADN11122221N.......................", // 41
        "..................N1222EQNKD....A12222221D......................", // 42
        "..................A222EDDNND.....NNEF2221N......................", // 43
        ".................A222FD11AA1D....DADDFE211D.....................", // 44
        ".................A22FN121A11K.....DC1KDE21K.....................", // 45
        ".................K12TN11KANAK......D11ASQ1KK....................", // 46
        ".................N1SN12KNAN1N.......DDAAAAND.D..................", // 47
        ".................NAAAA1NADA11K........DDNN1EN1D.................", // 48
        ".................K12EAKA..D1A...........D111N11D................", // 49
        "..................A1QN.....D.............DNNA11N................", // 50
        "...................NN....................AAA122N................", // 51
        ".........................................D111121NK..............", // 52
        "..........................................DN1112NAN.............", // 53
        "............................................DNN1N1N.............", // 54
        "..............................................KNN12D............", // 55
        "..............................................DA111A............", // 56
        "................................................D111C...........", // 57
        ".................................................DN11...........", // 58
        "...................................................KD..........." // 59
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'p' to Color(0xFFDC2D6E),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawXande() {
    val matrix = arrayOf(
        "................................Mm..............................", // 0
        "............P.PP.................0000II..mI.....................", // 1
        "..............P..P.....II.......0DIMIMVIM0U......Im.............", // 2
        "..........CAP.IC.C.....0N00.m...D00I0IUIBNBD.M0.MMMM..I.........", // 3
        "...........DCCAAA......BRNBBBBMMBDBMUVVV0DIU0IVUVVVVIVVII.......", // 4
        ".............APC.......BBBNNKNKBKKKIKNBDKMVVVVIIUVVUUVKIBVI.....", // 5
        ".............DAA........BKMBRNIIDMMM0D0DDI0UVMUMMUUMMVDIDIB.....", // 6
        "..............NN....m..DK0VVANIIDMDK0IU0MUIMMDMDSDDSSIB..D......", // 7
        "..............AP...III0VIViiVN0MMDMDMDDDDVMMDKDT.....D..........", // 8
        "............D0DA..IIVVIVVIUVUIVVMDNKD0UKMUKKKKKBBBS.............", // 9
        ".............00ANV0UVIUVVMDMIVIVVDNMMKDA0UKKBBBBBBU00B..........", // 10
        "............DKDAA0IDUVMMMMMKMMVVVUUVIMID00KSTSBUUBBUUUB.........", // 11
        "...........00V0NADI0DDDSBKKKKMIVVVVIMKMIIKBD..T00UUBUUB.........", // 12
        "............0I.DCNMVD..SBBKKMDMMMMMB0MMKDBBBT..TB0UUBBB.........", // 13
        "................APT00TT0BBKKKDDMMMIVBBMDSBU0S...TBUBBBK.........", // 14
        "................RA.TT.00BBBKKKMMIVIIIDSTB0UUS...SBBBBB..........", // 15
        "................DA...T0BBBBKKDMBDMMMDSTBBB0BS..TBBBBK...........", // 16
        "................DCP.S0BBBBBKNRNDDNAANKBBBB0D.SIBBKD.............", // 17
        ".................ACS0BBBBBBKKKNrrCAPRKKKBBDTDBBKK.........PZQ...", // 18
        ".................NABBBBBBBBKNMMMBNACNBBDDSSDBKKKD......QPCOOOCQ.", // 19
        ".................NCBB0B0BBBNRNrMMBACNB00STBBBBD.......PCAACOJOCE", // 20
        "................0BCDBB0UBBKRRRNNArNNKBBB0BKKD..............POJJZ", // 21
        "...............UUBANB0UBBBNRCRRRNNNDBB0BBB0U0BBBI...........CJJA", // 22
        "..............0UBBNABUUBBBNRCRCNRRRADND0U0BBBUUUUUU0BB.CP..QOJOC", // 23
        "............ABU0BUNCDUUBBKNAAACNZRZNMMDDDD000BBB0UUUUMCOOCPCOOC.", // 24
        ".........PPADUUBUBKADU0BBKNCRCCROZCNNSIMDDAD0UUU0BBUrCOCCCCCCC..", // 25
        ".....QPCCPED0U0BUBBAAUBBBKNCROCAOZOAKNASDIMISBUUUUMrrMM.........", // 26
        "..QCCOOCT.TBUU0U0BBNA0BB0KNCAOZCJCOCNKKNPmMISDB0IrrMUU0.........", // 27
        ".POJOCPTTTDBUUUUBBUDCBBBBKACZORCJCOONRKBKDPIPBU0M0UUUUB.........", // 28
        ".CJJOCTPCZAMUUUUBUUDADBBBKCZZOROJOOORRNK0KPIDUUUUUUUUU..........", // 29
        ".POJOCOOCAACrrI0BUU0AAB0BNZZCOROJOOJRZAKMBNPBUUUUUUU0...........", // 30
        "..CCOJCP..0DJJOCDMUUNAB0BNOROZZJJCOJRCCKBBKKUUUUUU0.............", // 31
        "....PP...D0UACCAMUUUDCDBKROROZCJOZOOROAKKBBBBUUUU0..............", // 32
        ".........DUUU0UUUUUUBANBKRZROZOJOZOOAONNKB0BBBUUB...............", // 33
        "..........0UUUUUUUUUBRABNZZROZCJCCOACCNNCKB00BBB................", // 34
        "..........BUUUUU0UUUBNAKNZZROOZOZOOCCRCAOCADB0BBA...............", // 35
        "........PZCNBB...BBUBKQNNZOZCCZOZZCZCROCCJOONBBBRCQ.............", // 36
        ".......POOOP........BKQNKROZCOZOZZOZRZOOAOCONB0BSCOOPQ..........", // 37
        ".......CO2C..........DDANROCZOZOCZOCACRCCCOABB0D.TCCOCQ.........", // 38
        ".......PCJOP...........PNNROOCOCOZOOZRCOOONKBBBS..TCJJZ.........", // 39
        "........CCJOCQ.........DNRRROOOROAORRRCCRNBBB0BT..TCJOP.........", // 40
        "..........ACOOCZC........NACRCORZRANRRRNKKBBBBT.TTCOOC..........", // 41
        "............PPPCCOQ.......DRAZANRNNKNNKBBBBBDPPPOOOCP...........", // 42
        "................AOC.........NNNAARNBK.DDD..PCCCCPP..............", // 43
        "...............CJC...........NACRCKBN.....QAP...................", // 44
        "...............CJOCQQ........NRCCAKA............................", // 45
        "................PPCCACP.......NRANKD............................", // 46
        "......................C.......KNBMMMN...........................", // 47
        "..............................BMDDDNAA..........................", // 48
        "..............................DDID.NRCA.........................", // 49
        "..............................NCND..DRCC........................", // 50
        ".............................DCZRD....D.........................", // 51
        ".............................ARRA...............................", // 52
        ".............................ROC................................", // 53
        ".............................DND................................" // 54
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '2' to Color(0xFFE6B414),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'I' to Color(0xFF7346AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'Z' to Color(0xFFD21E19),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawMistDragon() {
    val matrix = arrayOf(
        "...........................................WS..WS...............", // 0
        "............................................TT..TT..............", // 1
        "............................................TT..TST.............", // 2
        ".......................................TT..WSSW.TISW............", // 3
        ".......................................TS..TDDT.TIIT............", // 4
        "......................................WSTT.TISTWTSST............", // 5
        "......................................TSTT..SSSTWSST.TT.........", // 6
        "......................................TSSSTWWTSSTSSS.TT.........", // 7
        ".................................TT....TTTSSTWSSSSmSTTT.........", // 8
        ".................................SST..WSSTSDSTTSSSTSSST.........", // 9
        ".................................TSTT.TDSTTSSSSSTDTTSDT.........", // 10
        "..................................STSSSTTWWTSTDDSSTiTSSW........", // 11
        ".................................TTiSSTWWTTiSTTTTTTWWTST........", // 12
        ".................................TTmmTWWWSSmSTWWWWWiWWST........", // 13
        "..................................TTiWWWiSISSSTiWWWiiWST........", // 14
        ".................................TTWWWWWWTSSSSTiTWWiTWTT........", // 15
        "..............iTi.........WSSTW.TSiWWWWWWiSSTSSTTmiWTiWS........", // 16
        ".............iTTTi.........SSTT.STWWWWWWimIImTTTTSSTTTWS........", // 17
        ".............iTiiTTTT......WSTTSSTWWWWWiiSSTTSTimSSSiTiST.......", // 18
        "..............WTiiTTiTTTi...TmTSmWWWWWWiSSTTSSWWTSSSSmmIS.......", // 19
        "...............TmiiiiiTiT...TTiSTWWWWWimSWWWSmiiiTSDSiiSS.......", // 20
        "...............TTiiiiiiiii..TSSSTWWWWWiSSiWWmSSTmiTmISWTS.......", // 21
        "...............iTWiiiimmimW..TSSWWWWWiTSSTiimSSSmiiimSTTS.......", // 22
        "................TiWWWiimimW...TSWWWWWimSiimTimSSSSmiWTTTST......", // 23
        "................TiWWWWiiimi...TSWWWWWimSiiiimSW.TSTiiWWWTS......", // 24
        "..............iiTWWWWiiiiST.TTTTWWWWiiSTWiiTST.....WmiWTTSW.....", // 25
        ".............iTmiWWiiiiiiSTTSSSWWWWWiiSiiiTST......WSTWTiST.....", // 26
        ".............iTiiiiiiiiiiSSSSTSWWWWWiiSmiiTS........TSTiiST.....", // 27
        "..............TTmiiiiiiiiSSTTTSiWWWWiiSiiimT.........TSiTST.....", // 28
        "................iiiiiWiiiSSSSSSWWWWWiiSiiiST..........TSSS......", // 29
        ".................iiiWWiiiSSTSSSWWWWWiiSiiiST...........TST......", // 30
        ".................WmWWiiiimSTTTSWWWWWiiSTimTW....................", // 31
        "...............iiiiWWiiimTSWWTSWWiWWiimSiST.....................", // 32
        "..............WTmTWiiiiimTSTWTSWiiWWWimSmIT.....................", // 33
        "..............iTiWWiiiWWmTTSSSSWiiWWWiTSSSW.....................", // 34
        "...............iTiiiiWWWimiiSTWWiiWWWWiSST......................", // 35
        ".................iTiiWWWiTmmiWWWWiiWWWimSW......................", // 36
        "..................iTTiWWTSSiWWWWWiiiWWiimS......................", // 37
        ".....................iiTSSTWWWWWWWiiWWWiiST.....................", // 38
        "......................TSTWWWWWWWWWiiWWWiiST.....................", // 39
        "......................TTWWWWWWWWWWiiWWiiiiST....................", // 40
        ".....................iiWWWWWWiWWWWiiiiiiWimT....................", // 41
        ".....................mWWWWWiiWWWWWiiiiiiiimm...ii...............", // 42
        ".....................TWWWWiiiWWWWWiWiiiWWWim...ii.WW............", // 43
        "....................iiWWWiiiiWWWWWiWiiWWiiiiW..iiiii............", // 44
        "....................TWWWWWiiiWWWWWiiiWWiiiiii.iiTiiW............", // 45
        "...................iTWWWiiiiWWWWWiiiiiiiiiiiiiimmmiW.W..........", // 46
        "..............Wimi.TiWiiiiiiWWWWWiiiiiiiiiiiiiiiiiTTiiiW........", // 47
        ".........WW..iimmmiiiiiiiiiiiWWWWiWWWWWWWWWWWWWWWWiiiiTTimi.....", // 48
        ".........mmiiiiiiiiiWWWiiiiiiiiiWiWWWWWWWWWWWiiiiiiiiiiimmi.....", // 49
        ".........immiiiWWWiiiWWWWiiiiiiiWWWWWWiiiiiiiiiiiiimiimTWW......", // 50
        ".......WWWiiiiiiiiiiiiiiiiiiiiiiiiiiiiimmmmimiWiiiii.WiW........", // 51
        "......iTiiiiiiiiWiiiiiiimiiWiiiWiiiiiimmmmiiiiWW......WW.iW.....", // 52
        ".......iWWiiTTiiWiiiiiiiiiiiiiiWWiiiWWWiiiiWWWii.......i........", // 53
        ".........WiWiiWWWWWiWWWWWWWiiiiiWWWWWWWWiiiWWWWiiiii............", // 54
        "........iTiiiiWWWWiiiWWWiiiiiiiiiiiiWWWWWWWWiiWWiiiii...........", // 55
        ".........WW..iTiiiiiiTiiiiiiiiiiiiiiiiiiiiWWiiiiiiiiiiiW........", // 56
        "iiW...........WWWWWWWiiiiiiiWTTii.......Wiiiiiii...iiiWiii......", // 57
        ".....ii........WWWWWWWiiiiiiWWW...W......iiiiiW.................", // 58
        "WWW.WiW..W....imiWWWWWWWWWiiTi....Ti..WW......................WW", // 59
        "TTiiiiiiiiiiWiiiiiimmiWWW...iiiiiiiiiiimi..W....iiii..iiWWiiiiiT", // 60
        "WWW.WiiimiiiiWiWWiiimiiii..iiWWWWiiWiiimiiiiiiiWWiiiWWiiW.iii.WW", // 61
        ".....iiiiiWiW...iiiiWWW.W.iiiWWiW..iTiiW.WWW.WiWW..WWiiT........", // 62
        ".....WWWW.......Wmmi..W...iimW......TmiW.W...iiiii....WW........" // 63
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

fun DrawScope.drawGolbez() {
    val matrix = arrayOf(
        "..................D.............................", // 0
        ".............K....D.............................", // 1
        "..............D..Dl.....D.......................", // 2
        ".............I.D.0.I0...I.......................", // 3
        "..............I.BImimID1........................", // 4
        "...D..........D.BUVI0IMB.......D................", // 5
        "...l...........BBBBBDIBB......D.................", // 6
        "...121DBM0M0..0MMKImmmU...BBMD1.................", // 7
        "...D231MVVimMPDBUBBBUIB.BBUImm0.................", // 8
        "....D21UmmiiIS2DDSDBIIKNKKUImmM.................", // 9
        "....KKMIQI0mmBA2AD11ISKN1KMImQ2.................", // 10
        "....BBUISSD0IBBDl11DDDKDAK0S2DGD................", // 11
        "...DDIU0332D1DMMMDA11111KG1GBMSD................", // 12
        "...DDI021DDDDBBBKKKKB0MBKKKKBBD.................", // 13
        "....BB21KKMBBUBDKBKKBBB0GKBBUUBD................", // 14
        "....BB1KKMBMmmSSmISImmI1DmmIUBBII.0.............", // 15
        ".....D1KMBBBMQIimlUimIPMVmmmKSTIADD.............", // 16
        ".....DKBUBVmSPmiSSUM0DBBVmiI0D1EB...............", // 17
        "......BBMKMUSSIIDSS1DSUBBBUISmDNP0..............", // 18
        "......BUBKBBDSMMDDDBBASUBBBDSSIK.D..............", // 19
        "......MMKKKBKDDBBMBKBBDDBIMBlDKB................", // 20
        "......MMKBBBBKKBKBBBISD1KDKDBGBB................", // 21
        ".......MKBBBBBBBKBKB1GKKKNNB0KMU................", // 22
        ".......UBBKBKDMUMUIBKKKKGKGKBKUU................", // 23
        ".......UBBBBBBD1STIPKKKK1KKKKKUU................", // 24
        ".......IBBMBMBMIADD0BMMKDDKKKBIBI...............", // 25
        ".......IBBIBBBIUmTISBVIB1IBKKBIBI...............", // 26
        ".......IBBUBBDDPE3SP0mIBD0BKKBIMI...............", // 27
        ".......IMKMMKGMITmMD0iVB1IBKKMUMB...............", // 28
        ".......IMKBUKKNQ3I0lBUUB1S0KKMMMBI..............", // 29
        ".......IMBKUBBBD1llDBMBKGDBKKMB0MB..............", // 30
        ".......IMBKBUBBMBBDBBKKBDPBKBIBMUUU.............", // 31
        "........BMBBBBD1P1SBKKBUMD1KBIBBUBUm............", // 32
        "........BUKBBMBBSDIMBBBMVIKKBUMBIBMM............", // 33
        "........00BBKBBBMUmIBBMUmVKKBUMKUMKUI...........", // 34
        ".........MBKMBMBBUiIBMUUmVKKUUMKMUKBUI..........", // 35
        ".........BUBBMBBBBUMBMUUIBKKIMMKBIBKBUI.........", // 36
        ".........KUBKBMKKKBBBIIM11DKIUBKKUUKKBUI........", // 37
        ".........KMUKKBMKKKKBMMPUMKKIUKKKMIBKKBUI.......", // 38
        ".........DBUBKKBMKKKKBDImMBKUUKKKBIBKKKBUI......", // 39
        ".........DKMBKKKBBKKKKMmmMBKBUKKKKMUKKKKBUI.....", // 40
        "..........KBUBKKKBBKKBBIIBBKBIKKKKBIBKKKBMI.....", // 41
        "..........KKIBBKKBMKKKBBMBKKBIKKKKKMUBKKKBU.m...", // 42
        "..........KKUBKBKKMBKKBMBKKKKUBKKKKBUUBKKKUI....", // 43
        "...........KMBKBKKBMKKBIMBBKMMMKKKKKBIUBKKMMQi..", // 44
        "...........KBBKBKKKMBKBUUBMKIBIKKKKKKBIUBKBD....", // 45
        "...........KKKKBBKKBBKKMIBBBMBIBKKKKKKUIUBK.m...", // 46
        "...........KKKKBBKKBBKKMIMBBBBUBKKKKKKBUIUB.....", // 47
        "...........KKKKBBKKBKKKBUUBMBBUBKKKKKKKBUVI.....", // 48
        "...........KKKKBMBKKKKKBUUBBBBMUKKKKKKKKBUII....", // 49
        "...........KKBKBMBKKKKKBUVBBBMBUBBKKKKKKKBUMQm..", // 50
        "...........KBBKBMBKKKKKBUVMBMMBUBBKKKKKKKKBBPm..", // 51
        "..........KKMBKBUBKKKKKBUVUBMBIBMBBKKKKKKKKPm...", // 52
        "..........KKMBKMIBKKKKKBUVIMUBMBUKMBKKKKKKKPm...", // 53
        "..........KKUBKUIBKKKKKBUVIMUBBMMBBMKKKKKKD.....", // 54
        "..........KBIBKUIBKKKKKBUVIMIDBUBMKUMKKKKBQi....", // 55
        ".........DKBUBKUIBKKKKKBUVUBDDKBBBBBUUKKKBQi....", // 56
        ".........DKMMKBUIBKKKKKBIIMD1KKKMKBKBUMBKBQi....", // 57
        ".........DBUBKBIUBKKKKBUIMD1KKKKMBKKKMUUBDm.....", // 58
        ".........DBUBKBUBKBBBBDSP11KKKKKBMBKKBUIMM......", // 59
        ".........KBBKBM0DDIBKD1DKKKKKKKKKBMBKKMUD.......", // 60
        "........1GKKKBD1DAD1GDIIBKKKKKKKKKKBKD0S........", // 61
        ".........ll1DD1KMUmBDKImmIBKKDDD....DBS.m.......", // 62
        ".............DKKBB0BDKBB00BK.........D.m........" // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'i' to Color(0xFFD2B4F0),
        'l' to Color(0xFF96DC37),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawCagnazzo() {
    val matrix = arrayOf(
        "................................................................", // 0
        ".D0D.....................D000DKDDDKD............................", // 1
        "..DVBD.............DDIDDUVV0DBVV0NKKKKD.........................", // 2
        "...DID...........DDI0KDI0DD0VU0DUII00MNKN.......................", // 3
        "...DVD.........DK0INDI0DD0IUDD0VddVIIIIMDD......................", // 4
        "..DVVD.D.....D0I0VDIIDD0IIMDIVVVdTV0DNM0MNDDD0Vdd.D.............", // 5
        "..DdVMDDDBBDDD00VMU0MIIIMMDIIUVVU00VII0DDMNDDIUVVVd0............", // 6
        "...0VUD0VdVVUDD0DVVDIIMDM0III00DDUdiVIIIDNMDDDIUVVVd0...........", // 7
        "...DVKDVVVVVVUMNITDVV0DUUII0MNM0VVdTVVIIIDKDMDD0UVddU...........", // 8
        "....DDIUUIVVVUID000dIDVVV0DD0IIIUVVVV0DDDMMDDMKMIVdVVK..........", // 9
        "....DUVVVVUVU0IMDDVVDddVB0UUUIIIIIMD000DDKDMDKDDI0VVUN..........", // 10
        ".....VddVUIVVIMMDDU0UVVUdVVVUI0DMBIVVdVIIIDDMMDKM0UUMD..V.......", // 11
        "....DVdVVUMVVUMDNNMD0VDVdTdV0DD0UUVVdddVIIIDDMMMN00MK...........", // 12
        "....DIVVU0IdVUMKKKKDDDDUVVUDUVUUIIIVVdTdVIIIDKDDDDMDKN...i....i.", // 13
        "....DMUUMUVdVIDUVV0DKKD0I00dTdVUIIIIVVdTdVIIIMDNNNKKMIBD......i.", // 14
        "....DMIIIUVVVMVVVddV0MDD0NIVVVVVVUIIIVVddVVIIMMMMNNM0UVDV.......", // 15
        "....DMMUV0UV0IVVVVVddVV0NKNNKKKB0VVVUIUVVVVIIMDDDNUVdVVIK..d....", // 16
        ".....DMUV0IVBUVdVVVVddVUI0UU0UIDDKDIVVUIIUUDKDD00DBVdVVIK..Vd...", // 17
        "..i.VDMUVVMIDVVVVUUVVVVIVVddVVVUMMDKDVVVV0KMUVVVVUMDVVUMKVdVVd..", // 18
        "...dd0KMmmMIDUV0VVI0VVVVVVVdddVVUIMMND00DD0VdddVVVVMDT0MKVVUVV..", // 19
        "....dUDMIdUMN0V0VdVV0VVTdVVVVVVVVUIIIIDDDIVVdVdVVVVUD0SDKUVIUV..", // 20
        "....dVIN0iI0MDV0VidVVVVdWdVVVVVVVUUVUVUIMUVVVVVVVVVUDDDD0VUUid..", // 21
        ".....dVMKdVUMKV0UVidVVVVdTdVVVVVVUVVU00MMUVIVVVVVVVVIKDD0TVTWd..", // 22
        "......dD0ddUMKVVDVVVViVVVdddVVVVUMU00UVUMIVMBVVVVVVVIDDdVTWTVVi.", // 23
        "....Vi.VTVVVIKUdddVVViVVViVVVVVUMMIUVdV0M0I0I0VVUViVVSMVdWddVV..", // 24
        "..i.VV...dVVVUVTdWWTWWWVVVVVVUUVMUVVTdVVMUDIUDUVVVmVVKMmdWdVTV..", // 25
        ".....Vd.d...VTWVTTWTTddiVVVVVVVdUUViWTiiUMKID0IVVVVVUK0ididdVV..", // 26
        "...ddVVVdddd.dimVddddTddTTVVdUdWdTWWWWWdiVImDU0VUVV00SDiVVViVd..", // 27
        "..didVVVVVVVVddiVVVVVdVdWWTVdTdWdWTTTTWTWdWiVTdVD0SDSDddViVdV...", // 28
        "......dVVVVVViVTVVVVViVddTdWdTddVdVidTddTTdWdWTTSTDDDdiiVidVdd..", // 29
        "......ddVdVdVVVVVVVVVVVVdVTTdTVVVddVVTVddTdddTdddWdTWdiiVidd....", // 30
        "...V..idVdTdd.dddVddVVVVVVidTdVVVVVVVdVVVdVVVdVdViVdiVd..d..i...", // 31
        "........ddWd...dTVdVVddVVVVVVVVVVdVVVVVVVVVVdVVVVVVdVVdV........", // 32
        "......d..d..i..d.dWdVi.ddidVViV.dWdV.VVVVVVViVVVVVViVVii........", // 33
        "...........i....i...di.....V.......d.ddVVdVddVddVVidd.di........", // 34
        "........................m..di.i........ddTdidd..ddT....i........", // 35
        "...................d.d..mVi..........d.........d...V............", // 36
        "................................................d...............", // 37
        "................................................................" // 38
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'B' to Color(0xFF1E3782),
        'D' to Color(0xFF4B4B4B),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'd' to Color(0xFF5AD2CD),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawBarbariccia() {
    val matrix = arrayOf(
        ".............................................................", // 0
        ".....................F.................ll....................", // 1
        "......................F.........l2233llLtl...................", // 2
        "................FF...........F4llll24323lLFF.................", // 3
        "..............Ql32lEE...4...424432332224l2...................", // 4
        "............llFFFF432lE.444424WF34F432243l3F.................", // 5
        "..........ll3FWWWWF444342lll34F343FFFll44ll4.................", // 6
        "........Fl34WWWFWF4FFF44443444324l3F4lSl4lL4.................", // 7
        "..ll...llll4FWF4444344FFFF4443224ll4lPESll3.4................", // 8
        "...Ell3lll4FWF443llll333344F3ll33ll3lPFFQ44..................", // 9
        "......Ell4WWF42lLLLLLLll22EllLl33LP24lTWQAlE...FFF4..........", // 10
        ".......34FWF42lLLllllLLLLLLLlDA23lPP2lTENK1l44E4..43.........", // 11
        "......Q4FFF42llll32lllllLLLDKNPl24lFFEPNANT.lE.4....l4.......", // 12
        "......4F4443lll344443llDNNNPEFEP14lFQESTNAD........F4........", // 13
        ".....l.44442ll3FWF442lDNQFWWFFFEQ2lEFWWTNQPD........F4.......", // 14
        ".....l443342l3FW4432lDAQFWWWFQEEE3lQEWFAPEFQND........4F.....", // 15
        "....E44l1342l4W443lllGQFWWWWWFEQQ321PPSKQEWFFCD.......Fl.....", // 16
        "....4.4ll432l4F4lLlLDAFWWWWWWWWFE23llPAKQEFWFQPDA......4.....", // 17
        ".....4lL243234F4lDDDKEWWFFFWWWWWWE334WQDNQFFWQFWE.D....4.....", // 18
        "...FF4ll3l423FF42lDKPFWFFEEEFFWWWWFF4EFPGNDQFFFFEQ.P......4..", // 19
        "...F44L33l42FW4432LNTWWFEQNQEFWWWWWWWWFPG..NDPPPCQ.FN.....4..", // 20
        "...FF4L43l43FW4443DPWWFFQNAEFWWWWWEFWWFDK.....DDD...P.....F..", // 21
        "....4lL42l34FW444lAEWFFFAKPEFWFFWWEFFFQ4N........D.FEN.......", // 22
        "....2lL42ll3FW444APFWFFPKDQEFFQQFFPEEQEPK........D.EE.K......", // 23
        "...F4SL32ll3FWF41AEWWFQNNDQQEQQ4QQNAQEQN............QFPD.....", // 24
        "...FE..22ll24F4lDQFWFEN2LGQAPPEQ4AKANNK.........D.NEDQDC.....", // 25
        "....l..l2lll3F41AEWWENl3lGQQAQPQPNPQQN..........K..DDPDND....", // 26
        "........2llll3lNQEFFPL222LPFQNNANPQEFA.......................", // 27
        "........Q22LL21AFFFQKl322lKTFFQDQFEQEALLE....................", // 28
        ".........12lLLNEFWWENl443LSWWFEFFFFFPA321E...................", // 29
        "..........l2lLNFWWFDGLlllKTWFFEFFFWENl33421l.................", // 30
        "..........LllDPFWFQNDLLLGPFFFFWETWFCNl24FF42L................", // 31
        "...........LLNFWFENDLLLSSKQEFWWFFFPNPLl2FWWF2l24l............", // 32
        "............NPFWEADlllD4FQNPFWWFEQNQQDll4FFF4llllE...........", // 33
        "............DPQEADlllDDWWQNNQWFFQNQFFDLl2FF3442ll............", // 34
        "............DSAANLLLDLEFFDCPNEQQAAEFWPDllFW4l332ll...........", // 35
        "............FElDDTTDGEF41AQEAPEQAQFWWENDl3FF324322l..........", // 36
        "............42LDTWWDlWTl1PFFPDQAPEWWWENDll4W414F2l2l.........", // 37
        "..........KA23DTWWWDTFl1NEFFQP1DQEWWFFDDLl4WE24WF2l3F........", // 38
        "..........AEQNTWWWTDQ4lPAFWFEPKSFFWWFFDDLLl4Fl3WF32llF.......", // 39
        ".........NEWFEDTWTDTlFl1DWWFEENSFFWWFFDDlll4WE2FWF3lS2.......", // 40
        ".........DWWFTEDTSSlQ3lASWWWFFASFFWFFQN12ll2F422WF32F4.......", // 41
        ".........PFFANSSDSDlFllNFWWWFFSPFWWFEAD33lllEFllFWFlF4.......", // 42
        "........NQQSSTSDDWDllllDFWWWFWFASFFEPN1242lll4ll3FW4F4Q......", // 43
        "...E...DQQSSWWWTSDDll4GDFWWWFFFAAEEPDTll44ll233l34F2FE2......", // 44
        "...Fl..DPSSKSWWTNP344DKDEWWWFFFPKPPADWSl343ll33l3WF2FW4......", // 45
        "....EL..DDDDDTTN4FElGGSSDWWWWFFPNNNKSWTl244ll33l3F32FW4......", // 46
        ".....l....DDTWTN3lgGKSWSAFWWWWWENPPDWWWl244ll33l4F32FW4......", // 47
        ".....4L.........NKKSSWWTDQWWWWFFDPKDWWWl244ll3324432FW4......", // 48
        ".....4l.......4EDSSWWWWWSPWWWWFFDNKDWWWl243l24234233FW4......", // 49
        "....F4l...FEF433lFWWWWWWTDEWWWFEDKSTWWTl242l3FF43l34WFF......", // 50
        ".....4l....4lFFl3lFWWWWWWTDTWFEPNSWWWWQ244l24WW42l34W4.......", // 51
        ".....l...F3lTWWTl2FWWWWWWWTDQQPNSWWWWE2F42l4FF43l32EW4.......", // 52
        ".....l....3EWWWTE3FWWWWWWWWTDDDSWWTFl4FF424FF432l34W44.......", // 53
        "....F3E...2LTWWWFlFWWWWWWWWWTFTTFFSFWF4444FW4l2234FW4........", // 54
        ".....43lE421TWWEFlTWWWWWTE4Fl3ll4FWF434FFWF422234lW.4........", // 55
        "......433322lQFFlSWWWWS44334444FWW4424FF443ll233lT.E.........", // 56
        ".........ll3Ll4lSWWWS34432lll23444ll4W443llll33lS.FE.........", // 57
        "..........l4lFFWWWWll42ll2F4F443ll44FF3llll343lF..4..........", // 58
        "............4llFFQl442l223FFF33344FF3lll3343llF..EE..........", // 59
        "............433l33442l1lE444444l343l233433l4...F4F...........", // 60
        ".............ll3323l1lF.........Fl1333lFFF....F..............", // 61
        "...............Fl11lF........................................", // 62
        "............................................................." // 63
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'K' to Color(0xFF141414),
        'L' to Color(0xFF50BE28),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'g' to Color(0xFF199123),
        'l' to Color(0xFF96DC37),
        't' to Color(0xFF0A6469)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawScarmiglione() {
    val matrix = arrayOf(
        "...........................AAAAA.........PAAAA..................", // 0
        ".........................PPEFQFQPP......PPEQQQP.................", // 1
        ".....................P..PQFF44QQQ11....AEFFFFFEP................", // 2
        ".................P..Q1DPQF4l1DKNA1PA..AQFFQPEQF.Q...............", // 3
        "................P4PQ1DA1FQ1AKNDDDNN1NN1FE1AAAAA1EQ..............", // 4
        "..............D13SA41D14F4AKDDDDDDDNKN1Q1ADDDKKDCPQ.............", // 5
        ".............PPP1AP1DAlFF1KDDPDDPDDDDDDDDDPSEPD..DPP............", // 6
        "............DPCPPPDDD11FQAKDDAPPC1DDPPDPPSPDSPDN...D1...........", // 7
        "...........NDDPQSPADD14FPDNDPPD1PSQNDDDPDDDDDDDD....D...........", // 8
        "..........ACDQSrDA1DDl4Q1DDPPPDD1PPDDNNDDLSLLDDDD...............", // 9
        "..........DPQPDPlADDDQQQPDDDPDDPD1PDANNNDgLSTLDND...............", // 10
        ".........DPQSDDP1KDDPEFFPDDDDPPPPDD1DDQQPNLTSLNK................", // 11
        ".........DQPDD1AKDDrQFFQSDDDDPPPPNAll1PQPDLLgSDD................", // 12
        "........DPPAADlANDPQEQPPPPPDDDDDN1lA12PDSSSLLg11D...............", // 13
        "........DPDAAPlADPPQQQPDDPPDDDDDLELSElQDPQPDEDDlDD..............", // 14
        ".......DDPAANQQNDPDPPPPDDDDDDDNDTgSTSDlEEQQSP1DSD...............", // 15
        ".......DDDCPAQSNDDDPPDDDDDDPPPDSLLSDLD1SDDPQDDLGgD..............", // 16
        ".......DDDPPAQPKNDEEPDDDDPPQQDlLSTLDDSDPPNNPDQDgD...............", // 17
        "......DDPDPQPQCNDSQSPPPPPQEFEPDLTLLDDlSCFDDDKDDSND..............", // 18
        ".....DDDPDPPAEQADPDDPPPQSFEQPDKSSLLKDFEPDPQNDPNSD...............", // 19
        "....DDDPQDACD1PDPDDDQEEPQEPPDDADSSGDDSFFlFQKPDKDL...............", // 20
        "....DPPPQPDAAACDQQPQEFQPPSPDDKNSTgGGSPPlFFPDDPNDGG..............", // 21
        "...DPPPPPDNN11ADSEFFFFEQDDNKKKDlDgLDlTSSPFSTNADPD...............", // 22
        "..DDDQQPDNNDN1PDPEF.FEQPNKKDDA1GDLlSPESQCCCACN.DP...............", // 23
        "..DDDQPPDDNKNP1NDQFFFQPDKKDDNKNgLSSLNDTQZCZCPD..D...............", // 24
        "..DDPQEQPDNKKA11DPQFEPPKNDNKKKKNLgLlgNPFPTPPN...................", // 25
        "..DDPFFEQPDNKN1QlDPEQPDKNKKNDNKKDlGlSKNCPDPNN...................", // 26
        "..DDQFFFQPDDNN114PDQSDDKKKKNDDNKDLgSDNNKDDNDAN..................", // 27
        "..DDPEEEQPDDNNA1Q1DSPPQNKKKKNDDDKGDDGPDNNKA11111111P............", // 28
        ".DDPPQQEQPDNKNA1QlDPPEFNKKKKKDPPNKKNDQSKN11QEFEQEFEQ1P..........", // 29
        ".DDDPQQQPPDDNKN144DDPQFDDKKDNDPSDDKNPQDNNC1QEFFFFF.FE1P.........", // 30
        ".DDDQQEPPDDNDDN1lFDDPPQD.SKNDKDPDDNDPDDPDNA11Q1124FFFE1.........", // 31
        ".DDPEFFPPDDKSTD1E4ASPDDNT.SKDNDPDDNNDPQQPKSDAADAAP4FFEPP........", // 32
        ".DDPEFFPDDNDT.TAQFDSPPDN..TKDDPPDDNDPPQTSNTTSSSSSS1EFEQ1........", // 33
        ".DDPPFFPPDNS..SAlFSPDQPDS.TKNDPPDNNPDPDTSKT.......SP4.E1........", // 34
        ".DPQPQEQPDNS..TS14DPDEQDD.TKKDPPDKDPAADQDS........TDQFQ1........", // 35
        "DDPQPQQPPDKNT..SAlDDPFEPD.DKKDDPNNPDPPDSDT........TD1EQ1........", // 36
        "DDQEQPPPDDKKNS.TS1DDQFQDKDKKNNDDDPPP2NDPNT....TSSSSDDDDD......P.", // 37
        "DPEFQPPDDDKNKS..SADDEFPDNKKKNKNNNSPQPDPDS...PPPPA1DDAPA.........", // 38
        "DPEFQPDDDNDDNS..TSDPFEPDAKKKDNKKDPP2ASSKT.SPPP1CDDAQE1A.......D.", // 39
        "DPEFEPPDNKNNS....SPQFPQDAAKKKNKKPASQNSPKTS1Q11STTTDFFQ.......QD.", // 40
        "DPEFQPDDKNNNS....SPFQPPD11AKKKKKDDPPDPNKA1P1NT...TDEF1.......A..", // 41
        "DDQFPPDDNNNNS...SDPQPSPA1P1AKKKKDDAADDKN1QPDT....TAPEP......1D..", // 42
        "DDPQPQPDNKNNKST.SPDPDQDAPQQQ1NKKNDDNNNNPEQDT.....TDCQQ.....PA...", // 43
        "DDDPQEPDNKKNNKS.SPDANSD11EQEEPADNDDNNAPFQNTTTT....TA1P1ACAQQN...", // 44
        "DDDPEFQDDKNDDND.SDD1NDKDllEFFF1lADA1Q4FF1KKKNDTTT.TDA111QQAD....", // 45
        ".DPPFFQPDKNDDDKSSDAlDNKNAl14FFFFQQFF4EE1KKKKKKKKDTTTDAAAADD.....", // 46
        ".DPPQEPPDKDDDPNTSD14DNKDDN1114F4FEFEl1AKKKKKKKKKKKD...DDD.......", // 47
        "..DQQQPDNNDDNPKSSDEQDDKTTKNN1lQll11AAKKNNNKNNNKKKKKD............", // 48
        "...DQPDNKNNNADKKKAFSPDSTSKKKNNNNNNNKKNDDDDDPDDDDKKKD............", // 49
        "...NDDDNNAAAAKKKKC4DPNSSKKKKKKKKKKNNDDDDPPPDDDPQSKKKD...........", // 50
        "...DNNNNNNNDKKKKKPlDDKKKKKKKKKKKKKDDDDDDPPPDDPPSSKKKD...........", // 51
        "...NKKKKKKKKKKKKKPPPDKKKKKKKKKKKKKKDDPPDNDDPDDPESKKD............", // 52
        "..KKKKKNDPDNDDNKKADSNKKKKKKKKKKKKKKKKDPPDNKDPQSASKD.............", // 53
        "...NKNANDPSPDPPDKPDPDKKKKKKKKKKKKKKKKKNQPDKKDPESKKD.............", // 54
        "....NDDDPSEESDDPSEDDPKKKKKKKKKKKKKKKKKKNTSKKKKNDD...............", // 55
        "....DTPSPSSQPPQFFQDDPKKKKKKKKKKKD.........DK....................", // 56
        "....EQNNNNDPDDSSQPNKNKKD........................................", // 57
        "....DN....KKKKKKKKKKK..........................................." // 58
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'K' to Color(0xFF141414),
        'L' to Color(0xFF50BE28),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'Z' to Color(0xFFD21E19),
        'g' to Color(0xFF199123),
        'l' to Color(0xFF96DC37),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawRubicante() {
    val matrix = arrayOf(
        ".................................", // 0
        ".............A...DD..r...........", // 1
        ".............rQDD.mDrr.....D.....", // 2
        "............ARrMm.rTN............", // 3
        ".............rRMV.qm.D.....DDD...", // 4
        ".............NNNrDNDND......DD...", // 5
        "..............KRNNrSRD.......N...", // 6
        ".............DNrNNRRrD.......D...", // 7
        "..........DNNNNNNNRSD....D....D..", // 8
        ".......DNNNrrNNKKNNrNNNDD........", // 9
        "......DBrRNNRNNNNKNNNNNrAP.......", // 10
        "......KUmprNKKKKNNNrrrrpprN......", // 11
        "......KImPrmUUIIqpmIqrppprN......", // 12
        "......NSmpmUViqppqiiqpqpprD......", // 13
        "......KRRrmVmpppppqqpqimpN.......", // 14
        "......KrrRrSrppppppqiiIIpN.......", // 15
        "......NrprrppprpqTqpqqqprNN......", // 16
        ".....DNRrpprRRrqiVqpppprNKD..DD..", // 17
        "......NNRRRRrppqmUIprpprNDMKND.D.", // 18
        "......NDRRrpppppm0qprpprNBMDD....", // 19
        "......KIPpppprpppSqprppRDMIDD....", // 20
        ".....DMUTpprrpqrrpppRppNNIINND.D.", // 21
        "......D0mrRRpimrppprRppNNDSrNDD..", // 22
        "......NSSNrpqIBIqpprrprKNrrmISK..", // 23
        "......KNNRrprDBUiprRppRKNrSUBD...", // 24
        "......DNrPrrrRIVTprRppNNRrS0D....", // 25
        ".......NrIDNrpTmTpRqiqNDrrDD.....", // 26
        ".......NDDMRpppqprRTIIDBSD.......", // 27
        "......DNNBNpppppprriUBK0B........", // 28
        ".....NKKNNrppppprRrqmMKUK........", // 29
        "...ND..NRrqiSrpprRpprKB0.........", // 30
        "..N..DNrrrTIDpprRrprNNBK.........", // 31
        "....NRrrrpmMIpprRpPrNNDD.........", // 32
        "...NRrrrppSUTprRrTINNrND.........", // 33
        "...NrRrpprDiprrprmBNrpDN....r....", // 34
        "..NrNNpprRrprrprrDNrPpAN.....RD..", // 35
        "..NrKrprRrrrrprrrNrpSqrNN....DrD.", // 36
        "..NNNrrRrrpppprrNKSSTiSNrD....rN.", // 37
        "..NNrrNrrpqTMrrNNDSTSWSNrN....rR.", // 38
        "..DNrRNrppqIrrNNMBSSpTSNR.DDDrpN.", // 39
        "...NRNRrppP0rRNSBMDTqqSNRmMMrprD.", // 40
        "..NRNRrppprIrNDMMrNTqppNRrIrrRN..", // 41
        ".DDDNrrrprDINNRrrNNDSrSDDSNNNS...", // 42
        ".KSKRrRprrRNNRrrNNKDSRSSNTTTT....", // 43
        ".KKNRNrprRrNRrRNKKKSiTqSRSWW.....", // 44
        "..KNNNprRRANRNKKDDNDiTqSRDW.D....", // 45
        "..KNNrprNrNRrKNSWWDNTqqpNS.r.....", // 46
        "..NNNprRRrNrrNNTWDSDSpqpN........", // 47
        ".D.DrprRprNrrNNTTDTTNrSrN........", // 48
        "...NrpRrpRKNrRRNNNSNKrPrD........", // 49
        "..NrppRrpNKKNRrrNNNNNDrSD..P.....", // 50
        "..NrppRrpRKKNNNNNNNNDNRS..r......", // 51
        "..NrppRrprNKNNNNNNNDTNR..........", // 52
        "..NRrprrprRNKKNNDSTTTSD.Nr.......", // 53
        "..NNrprRprrrNKDTTSDDTTDrrD.......", // 54
        "..DNrpprrrNrrrANNRDTWTNrND.......", // 55
        "...NRppprrRNNrrrNNTSTDrNDD.......", // 56
        "...DRrppprrrNNNNNNNNNrrNSID......", // 57
        "....NrrpppppppprrrCrrrNDPID......", // 58
        "....NNrrrpppppppppprRN.NpI.D.....", // 59
        ".....NNRrrrppppprrrNN...MDID.....", // 60
        ".......NNNRRRRRRRNK.....0D.......", // 61
        "........DDDNNNNDD................", // 62
        "................................." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawDarkBahamut() {
    val matrix = arrayOf(
        ".................D..........DDD........D..........", // 0
        ".............D...DDD........DD.PPPD...............", // 1
        "...........DDADNDDT.D.....D...DNAArDD...D.........", // 2
        "..........DDDDND...............DNDDADD..D.........", // 3
        ".........rNNDDND...............PNDDNDDD.D.........", // 4
        "........rDNDNDND...............PNDDNNNND.P........", // 5
        ".......DDNDNDNDDD.........DD..DPNDDNNNNNDA...D....", // 6
        "......DNDDNNDNDDD......D....D.DDNDSDNDNNNNQ..N....", // 7
        ".....DDDNNDDNDDD.......DKNDDNDDNNDSDNNDNNK..KK....", // 8
        ".....DDDNDNDDNDD....NNNDNDDPNDSDNDDDNNADDNP.DKNDN.", // 9
        "....DDDNDDDDDNDN....DNDNNDDDDSSDPNDrNNNANNDQ.NNKN.", // 10
        "....DDDDNDNNDKDN...DNDNNNNKNDSPDSDKDSNNNrrND..NKN.", // 11
        "...DDDDDDDDDDNND..NKKNNNNNKNDDNSSDNDNSDKNNKP..DNN.", // 12
        "...DDSDDDDADSDNDD.DKKNANKNKNNKDSDPNNDKSDNKK.F.DNN.", // 13
        "..DADSDDDDNDSSDDDDDNNKNDKKKKKNDDDTDKDNKSSNN.E..DKD", // 14
        "..DDSSDDSDNSSSSDNDDNDDKKNAANNNDNSSDKKNKAPNrPP..DKK", // 15
        "..DDSSDDSDNSSSSNNNNNDANNPCPCANKDSDDDNKNAPDPT...DN.", // 16
        "..ADSDDSSNDSSSSNDPrrNNNAPEPAAKKDDDSDKNKNSSSD..PDND", // 17
        ".DASSDNSSDDSSSDASSTSDKAQPQPANKNDKSTDNNNDKKNNADPSNN", // 18
        ".DNSSDNSSDDSSSKrQTTSSNAPECAANKKKDSSDKNKDNPDDCNDTDD", // 19
        ".NDSSDDTSDDSSDKDPSSTDAPQPAANKNDNDSDSNKNSNPANDDDTD.", // 20
        ".NDSSDDTSNDSSSKDDDDDNPQQPANANDTNNNSSDKDPNNNANNDSD.", // 21
        "DDDSSDDTSNDSSDNNNDDNCPE.FQPQAKTDKDSSSDSDNAAAADNDD.", // 22
        "NNSTSDDTSNDDTDDNrTDNACQFFQPQCNDTNrDTSSDDKAADDDDNP.", // 23
        "NNSTSDDTSNDDTNNDSSNNNACPPAAANNDTNNDSSDNNNPNDPDDD..", // 24
        "NDSTSNSTSNDDSKNDDNKNNNAAANNNNNNDDKDDDNKKACNSDADDD.", // 25
        "NDSTSDSTSNNSSKNNNDDDDDDSQPPCKNNADKNNKNKNANKSDNPN..", // 26
        "NDSSSNDSSDNSSNDDSSDNDDDDSPPAKKNNNNKKKKNAAKKSDK....", // 27
        "NNSSSNDSDDNSNNDDNNKKDSDSDDANKKKKKNNNKKNNDKKDDK....", // 28
        "NDSSSND...NSNKKNNKKNNSDDDSDNSTSSS...TKDDDKDDDK....", // 29
        "NDSSSND...NNNNNNKKKKKDDKSDDKKNNNDNNDSKDNDNSSDK....", // 30
        "NNSSSNN...DND...DNKKKDDDSNTKKKKNNDNNNDDKDNDTDK....", // 31
        "NNSSSDK....N.....KKKKSDDTDSKKNNDDDNNNSDKSKDTDK....", // 32
        "NNS...K....N.....KNNNDKDSAAKKKNNNDDDNDKDSSSTKD....", // 33
        "NND...K.....D....KNANNNDACPNKNDrDDNKKKKDDS..ND....", // 34
        "DND...N.....N....KNADNDNNNAAKKKNNNKKKKKNKS..DD....", // 35
        ".ND...DD.....D...NKDDNNNANNANKKKDDTDNNKNKT..DD....", // 36
        ".KD....N.........DNNDNDDDDANNDDT...DrNNDNT..D.....", // 37
        ".NN....D.........DKNNNNDSSDNNT.....NrNNSS...D.....", // 38
        ".NN.....N.........DKNNDDSTTPDAT...TKANNST.........", // 39
        "..N.....D..........DKNNDDPTTSNNT...KNNSS....D.....", // 40
        "..N..................DNKDDDSTSACT.TKKKTT....D.....", // 41
        "..DD.............D.....KKNDSSTSAADDKKKS....DD.....", // 42
        "...D...........DNNAD....NKDSDSDNCPAKKKNS..AADD....", // 43
        "...D..........NNNKNNN....NNDNNNKNAAANKKNKNADDD....", // 44
        "....D........NNKDSSKNA..DDDDNNKKNAPQANKKKKNND.....", // 45
        "............DNKS...TKAA.NrDDNNKKNDAAPQCKKD........", // 46
        "............NDD.....DNA.DNDDNKKKNDNrPQCA..........", // 47
        "............KND.....DNA..NNNNTTNKKNADAQQA.........", // 48
        "............NAD....SKA...DNNKT..SDNNNDSQPP........", // 49
        "........DDDDNND...SKND...DKND.....SDKDSPEPP.......", // 50
        "......DNNNNKNNKDDDNND....NNND......TDNNDPQP.......", // 51
        ".....DNN...DNNKKKNN......NAND.......TDKDSQP.......", // 52
        ".....NN.....NNND..........NND........TKNDQQA......", // 53
        ".....A......DNNK..........DNND........KNDAPA......", // 54
        "....DA.......NNNK..........NNNST......KNSSQA......", // 55
        "....DA.......DNNNK.........NKNDDDDDS..KNPPPD......", // 56
        ".....A........DNDNKK.......DNKNNDKSD.SKNNQC.......", // 57
        ".....DA........DNNNNKKD.....NDNNSNNSTDNrPPA.......", // 58
        "......A..........NNNANKKK..DKDSKDSTSDNNPPC........", // 59
        ".......NND........DNNNNANNKKKKSNKSDNDANNAD........", // 60
        "....................DNKNNANANNSDNDDANDNNP.........", // 61
        ".......................DNNKNKANNNKNNNND...........", // 62
        ".............................NNNNNN..............." // 63
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawZeromus() {
    val matrix = arrayOf(
        "................................................................", // 0
        ".....................PAPPPCAACAD.NAQ............................", // 1
        ".......................DAKKNDNACRNANE.D.........................", // 2
        ".........QCCZOCQ.....D.DNNDDDPDNNNNCZCR.........................", // 3
        "........PNCCCRZZZCNNNDD..DDDNNNNKACCZARZOQ......................", // 4
        "..........DNRZORZCRZDDKN..KNKNZCNNNZNBNROP......................", // 5
        "..........DNNCCRZOZRDKNANANNCZZZOAKKNRCOCCANQ...................", // 6
        "...........PNNACOOZRKDNNNCZANCOOOAKKNNNROAAAP...................", // 7
        "............DNNNCZADNNNNDNOCNOOOCNKKDKZCRCAACQ..................", // 8
        ".............NDDDAANQPNKKKRCCZCOAKKPDKKRZABDNCP.................", // 9
        "........PPPANKDDKNRANPDKDSDAAZZANNDPNNDKCCNANZCQ................", // 10
        "......QNCRORANKDSDDARKDSTTTDDRNNARNCKDNACNNNNAOQ................", // 11
        ".....ENRRCCRARNNKSDNZKDKDTQPDKPNRCNDNDDNDTTDRCAC................", // 12
        ".....ACNCCZANNASSDNDNKKRNDSSDDNDROOANKNKDBSDNCD.................", // 13
        "....PRCAORNPTTT..SSTAKNCCNSSQSKNCZCANDRKKDSNKND...D.............", // 14
        "...QRZRCZANP........TNRNAADTQTDNZOADNRRANDDNNTDK...A............", // 15
        "...ARZRCNACOQQ......ENKDKNKKDTDNRCADDRCNNNKNDDKDN..N............", // 16
        "...DARRRCCCCROT.....TTDDSDDPKSDNZNNNSKACAANRDSTDGDNCE...........", // 17
        "...DDNRRRARRZZJF..T..TSSD0DGKKKKNAJCDDNROONAS..TDDDOQ...........", // 18
        "...DDKRNCRCCOZZCQQAETSSDDDTTSDDKKCAADSDDARRNSS.TDDNNC...........", // 19
        ".....KKRNNRRAACZCANNDDDDSTT.TDDDKKNDSD0I0AONP.S.NSAAA...........", // 20
        "....D..KKNNZOCCARNNNDNDT..SDDKDDDDDSDDDUQDP.T.TTSDDPD...........", // 21
        ".......GDKRRZRANRNRANPDSTSSKDKKKSDDDD0S0Q0T.......DAN...........", // 22
        "......DDDKRRRCPAACNZCNDDSKDDDDDDDDDDB00SQDV.......TKDD..........", // 23
        ".......DSKNRNAPDDNRROONDKKSDDDDDNDDDPESPSEV.......TDKP..........", // 24
        ".........DKNNCDTDDNRZONDDRDDSGDDDDBDQESESSST......TDNNQF........", // 25
        "........DDDKDDSDDSNZRNANSNNDGDDDDKDPAQSETSTT.......DDKNE........", // 26
        "..........DDKSDDDGNARZONDDNKDDDDDA1DAZJPPTV......FTDNKAP........", // 27
        "..........DSSDDKSKDDNRCOOARKGDDDSDKDPQOPASSQCDQP.TNDKNP.........", // 28
        ".............DDDDRKNCNCRCDDDDDDDNNAQEFEDBDDCCNZASTP.............", // 29
        ".........QND.DKKNRNKNNNRRDDDDGDDARAPQCDDKDDNNDDDS.D.............", // 30
        "........EPONDD..NRRRRKRNNDSDSSNNNCCAANNDD..DKDSK................", // 31
        "........AAAZD....NNNRNNRRDSDKDNAARZCAN......DDD.................", // 32
        "........DDKNP...D.DDNNNNNNGSDNNKNZCAD.......D.DAD...............", // 33
        "......D.KKSD.......DSNNDTDDDDDDDDNNDD.........QOOD..............", // 34
        "..QQQ.D..DDD.......DTQEDDDADNNADDD............ECCA..............", // 35
        "..........D.........Q.SADACKNNNNS................AC.............", // 36
        "............QF.........ASA..A..PD.................DAD...........", // 37
        ".......................PDC.........................DAOO.........", // 38
        ".......................QDP..........................RRR.........", // 39
        ".......................PDA..........................AKKDDQF.....", // 40
        ".......................NKNE.........................DKKKDPEEQ...", // 41
        ".......................CRO.........................PQ........P..", // 42
        ".......................NDRQ.....................................", // 43
        "....................D.KDSDN.....................................", // 44
        ".................EPDDKGDDDKD....................................", // 45
        "......................DDS....PE.................................", // 46
        "........................Q.......................................", // 47
        ".......................EQ.......................................", // 48
        ".......................EP.......................................", // 49
        ".......................P........................................", // 50
        "................................................................", // 51
        "................................................................" // 52
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'Z' to Color(0xFFD21E19)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawWingRaptor() {
    val matrix = arrayOf(
        "...............................DD......................", // 0
        ".............................DNCD......................", // 1
        "............................KN1D.......................", // 2
        "..........................KKNCD.....................DKD", // 3
        "........................DNDAAS....................ARRD.", // 4
        ".......................DDRANS...................CAAC...", // 5
        "....................DDDDCNNKNDAN..............DANNRD...", // 6
        "...................DKDDKNKKKKAN...............CNKNA....", // 7
        ".................KKDANKKDDNAA................CNKKRD.KKK", // 8
        "................DDDAANDNDACR................ANKKRKKRZN.", // 9
        "...............DDDAANAZC1RKKKKD............ANKKKRNRRRA.", // 10
        "..............DDDARNCNAAKKKNAD.............RNKKKNRKNA..", // 11
        "..............DDARKDAAARKKNAP.............ANKKKNRKKN...", // 12
        ".............DAARNPCAANKNAAD.............NCKKKNNKKRD...", // 13
        "............DDNNDNNACDDDCND.............DCRKKKRKKRD....", // 14
        "...........DDrN1NDKKNCCCNKK.............NZKKKNNKKCD....", // 15
        "..........DDCCNNCNOCDKKNKNA............DCKKKNKKKAKKNN..", // 16
        ".........KDACDDNKCDNKDDNNC.............KRKKNKKKKKNZZR..", // 17
        ".........NDACAAANACNNANAC..............NRRNNKKKRRKKRA..", // 18
        "........KDAAKACOCCANCC1D..............DZKKRKKKNKKNRA...", // 19
        "........KDADDKKAKDDKKKN...............DZKNNKKNKKRRA....", // 20
        "........KDANDDKKANAKNKNA..............ARKRKNKKKRAS.....", // 21
        ".......DDANNADACNKACAKKNKD...........DZRKRKKKRRNKDD....", // 22
        ".......DCNCKNCADNKKKA11OK.m.D........AZKRNRNRKKRRZR....", // 23
        ".......DNN1AANNCNKKNRKNDZOCKDI......AZNKRKNNKKKKNCD....", // 24
        "......DKZDAPAOONNKKKRNSROJ2JA.D....RZRKKRRNKKKKNCD.....", // 25
        "......DNADACDDDKC1NNNAKRJ243JA.D.NRZRKKRKKRKKNAAK......", // 26
        "......DZKCCCAANKKK1CRNKC24F43JNKAOZRNKNRKNRNRRKNNND....", // 27
        "......DZNRANKNANKKKKDDNO24FF3JCKOZRNKKNKNNKNRRRRRA.....", // 28
        "......DRKZKDKKNRAKDDSmNO234F32JNZRNKKKNNNKNRRRRKRD.....", // 29
        "......ANKNADKKKNNKDmTiNRJ23432JDRNKKKNKKKKRNKKRRN......", // 30
        "......CNKNAKNRNKKDIiiSTNOJ233JOZNKKKKKKKNRKNRRNN.......", // 31
        "......CKKCKNADRKDIWimiWTNOOOOZNTKKKDSKNNKKKKKKRRKD.....", // 32
        ".......KKZKKNZRKDimiWTSmKZOZZZNSDSKKSDKKKRNNKKNNR......", // 33
        ".......DRRKKKKKKDmSiiSiSKZZAAZRKKDSK1NKKNCRKKKRRD......", // 34
        ".......DAKKKNKKKDSiimSiiDRRDSNNKNN1N1NKNKKNNRRD........", // 35
        ".......DAKNNNKKKNiWimiimmNRA11NK2N211KKKKKRKKK.........", // 36
        ".......DZRNNRKKKNSiiiimImNNRA21N2A12AKKNRRNRNKK........", // 37
        ".......DRNSRRKKKDDiTSiSDmDKNRA2131A3ONKKKKKNRRCN.......", // 38
        "........N..NRKKKDKDmDmSSmDDSRN1322J32NNKKRKKKNN........", // 39
        "...........KKKKKDDKDmiSSSSDDEWDJJ2332TDRRKRKK..........", // 40
        "............DKKDDDDDDDDSSDDDDTTWFJJ32FDKKKNRRD.........", // 41
        "............KKDDDDDDKDISDDDDKDSWWTWFJFiKKKKKD..........", // 42
        "...........DKDDDDKDDDKSDSDSDDKDTSImTTSiKARK............", // 43
        "..........DKKDDDNKKDDKIDSSDDKKKTDKDSTDSK...............", // 44
        ".........DKKDDIDNKKNDKDSIDDKKKKSDNPPIDAK...............", // 45
        "........KKKKDSSDDKKNKKKSDDKKKKKNmDNDDRORD..............", // 46
        ".....DDNNKKKDDSDDKKKKKNDKKKKNKNKSmDDrOJ2D..............", // 47
        "...KKNNNNNKKDDDDNKKKKKNNKKNK..DRNDNKNAOJN..............", // 48
        "..KNNNNNRKNKKDDDKKKKKDDNK.NR...D......NCN..............", // 49
        ".KNNRRRRNRNKKNNAKKKNKDKKK..............DD..............", // 50
        ".NACOCRNRNNKKNCZNKNRKKNK...............................", // 51
        "KC2JCRCZRNRNKRQCNKNRKNQN...............................", // 52
        "K2AAACORZKRNNKQJNKRZNNQ1..D............................", // 53
        ".D.NCORRNRRNNK1AKKRRNRN2KDQ.DAACD......................", // 54
        "...AOON1KZKKNRNRKKKKNZNN22QEANRZ2AN....................", // 55
        "..N2AKKKKKKK1JNNANDQANCRNNNJ2NKKRZZAKKKKD..............", // 56
        ".KNCKKKKKKKD42NKKKN23JNKKKKA2AKKKKNAKKKKKKKK...........", // 57
        "DDKKKKKKKKKA2JNKKKKNC2JKKKKKANKKKKKKKKKKKKKKKK.........", // 58
        "..DDDDDDKKKNOZNKKKKKKAJKKKKKKKKKKKKKKKKKKKKKKKK........", // 59
        ".........KKKARKKKKKKKKAKKKKKKKKKKKKKKKKKKKKKKKK........", // 60
        "..........DKNKKKKKKKKKKKKKKKKKKKKKKKKKKKK..............", // 61
        ".............DKKKKKKKKKKKKKKKKKKKKKKD..................", // 62
        ".................KKKKKKKKKKKKKKKKKKKK.................." // 63
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'Z' to Color(0xFFD21E19),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawKarlabos() {
    val matrix = arrayOf(
        "..........................KKKKKK................................", // 0
        ".......................NKKNCQPKANKD.............................", // 1
        "......................KOCRRNZZRNANNKK...........................", // 2
        "......................P3SKKNKNRRNNKRRD..........................", // 3
        "......................DACANNNAANNNSRCND.........................", // 4
        "......................DAPONKNPCANOTRPCKD........................", // 5
        "......................AFSrPANCEECOQSCORKK.......................", // 6
        "........DDD........DDKrPQQNKKAOEOROOZQAKND......................", // 7
        ".......DPRND.....DKKNPNKQ4PrrACCAOCAZCANCRD.....................", // 8
        "....KKPCNKNRD..DKKKAACNMTFIPCKTJNZCNRANNCKND....................", // 9
        "..KDDDDD.DCRK.D.NNCAKAAKC3EONAEPKROAKRZNRKRNK...................", // 10
        ".D........NPNKPNNCFPNKRRMPPNKSPRKNZNKKANKKrIIIK.................", // 11
        "..........NCNKNKRJPOANNRKMKKNQPNKKRKDDADDDDM0SMD................", // 12
        "..........DNRKMDAACOCCNNKMDKNPCN..A...........BIK...............", // 13
        "..........DNNRrCRKNrKRRNKiSSDPrRD..............DKB..............", // 14
        "...........KKNNAEQCZRRNKT.DDSSSIN................KD.............", // 15
        "...........KrAKKQAORAANKNDCNSTDPMDD.............................", // 16
        "............KAQMCPOZRRNKCTAD...DMMBD............................", // 17
        "...........AZNKKKNCRRRKKQNKS...TDTMMD...........................", // 18
        ".........KNQZRRQONNNNKKKNNAS..TDSDSSKD..........................", // 19
        "........NCACZRRPOACARNKKNAD..TDCKSTDDNK.........................", // 20
        ".......NNOARNRRNAKOPNKKNNNSSSDCZKK...T..........................", // 21
        ".....PAN.NNZNNMMPPDNNPCZNNDKNKCNKKS.....D.DDDND.................", // 22
        "....DCN...KNZNNNZCOPRONKACRRCNKKKNKD..DDRRCNRRK.................", // 23
        "...DDK....KRKNAENRNAOORTOORRRRRNKNRKKNRNCEJCNKNN................", // 24
        "...K.......KKSPCNRNAKKAOKKOANPOOTKRRNKCCRRRNNCCAK...............", // 25
        "..K........KNACNRNANQCCONNKNAONNCPKNRANKKNNSSRZANN..............", // 26
        ".D..........NOECRRNKNCEOCNRCEENPOZZRRNKPKS...SRORN..............", // 27
        "...........KNRQARRRNKRZPRRKNZOARPZAANNNNNKSSSSKACA..............", // 28
        "........DDKANNZRRNNNAARNRRNNRCCNNNAZRAZNCKKKKDSNOPND............", // 29
        "........NNNPPRNRKKCPCCAZOZNRNRRKPCOOPDACNKRKPDDSCOND............", // 30
        ".......D.PCRACKKAKZAKAEQOZRRNRZRNJCKAPOZCANRCCKTNNAR............", // 31
        ".......DQZRNNNAKAKKNCOQONRRRNNRRNOPNCCJNCCKKKRANKKNR............", // 32
        ".......DRNNKKSDANCCQQEJZTKRQQNNNNOOAAOZNRCNPNNKKRRNK............", // 33
        ".......DNKD...DKKNCQQOZRRZCZRRKNPZRAAAACOCAKNRKKCORNK...........", // 34
        "........DND...TNNCNPQCRRRPPZRNKARRRZPPKNANZANRNANANCRNKKK.......", // 35
        ".......DQND...DRAANNRNNNNNNARNKKRRRRAONPAZOOCCRNKDNNNNKNAN......", // 36
        ".......NQND...DRKNKNNNQPRNNNNKKNNRRRRRKNQOCROJONKTDDSDNAONKD....", // 37
        ".......NORNS..TNNNNKKNQQZZRNKNNNRKNRNNAKJECRROENKSTSTTDZZKRN....", // 38
        ".......DQAKAS..NSRRNNNACOZRNNNKRPACRNRNANDZCPRCCKRSTSTTNNPZRD...", // 39
        ".......DNORRNS.NDFZRKKKNCRRNKACNQCPSNZNNCRKNOZNRKAD.SS.TKQAZN...", // 40
        "........NORRNS.NNQCNRKKKRNKKKROCCKZONARNNCCRAONNKCRD.SS.KEFOZN..", // 41
        "........NRRNRNSDRRORNAKKDDNKKNRJCNOONNRCKKNNNNCNNKNS.SKTDCQRNRN.", // 42
        ".........RNNNNKTNZOAPPNT...TDKNRCZRZRNRRKNESKNANKND...DS.KANNK..", // 43
        ".........AZZRRNSSRCNRRD......SDNPZRRNNNNAASANKNRKRD...SK.DKCRRN.", // 44
        "........ACOZRRRKTSRNNKDS.......SNNRRNKNRRAAARNNRNRAT...D.TKCKRRK", // 45
        ".......NCPOZRNCKT.KRRRKNS....ST.SSSKKKNAAAACARNNRKRD...SS.SDSDAK", // 46
        "........NCCNKKCRDTKZOZARND...TSD...SDKKANNNRCANKRKAD....K..D.DRK", // 47
        "........DKNANKARKDRPEORCRK....TTDDDSTTTDRAANNRNRZRNRS..DRD...DK.", // 48
        ".........KCP..DTD.NZOEPNCKKS......TSKS..NNRAKRTCNANZRS.DRD...K..", // 49
        ".........DRA..DRN..NRRPORKCRKKS.TTKKSSS..DKNAKAOCRKNZNKRK.......", // 50
        "..........KA...NK...DNRRNNQJZRNSNKNS..DS..TNSDKANRRKAQPND.......", // 51
        "..........KA...DD.....DRNOFQAKANRRNST.SKST.TDRNKZKRDSAND........", // 52
        "..........DD...D........AAPPNAQONNKKDT..KNSSTSNNCKRR............", // 53
        "...........D..............NNKAPCNNKD.....NKADDAAN..N............", // 54
        "............................DKNKK..........NRRNN................", // 55
        "............................................KK.................." // 56
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'Z' to Color(0xFFD21E19),
        'i' to Color(0xFFD2B4F0),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGilgamesh() {
    val matrix = arrayOf(
        "........................................................", // 0
        "......Vm................................................", // 1
        ".....mU.................................................", // 2
        "......V.................................................", // 3
        ".......V................................................", // 4
        ".......Vm...............................................", // 5
        "........Vm..............................................", // 6
        ".........Vm.........d...................................", // 7
        ".......i..V0.......d.D..................................", // 8
        "...........DN.......KD..................................", // 9
        "............AA........NNNNN.............................", // 10
        ".............NA......DAQQPAND...........................", // 11
        "....KAD.......NN..DNNNCQCCAPPA.....NK...................", // 12
        "....QPN.DD.....NND1KNACPAACQEA.....APDD.................", // 13
        "...AEQCN4FA.....KN2NNPAANKDAAA.....CJPPD................", // 14
        "..NCPPPNQSN....KAA11KAAKADSDD.....NDNN1P................", // 15
        "..NACCPNDDDA.GNNAAC1KNNNNDNSDD...N1NACND................", // 16
        ".NPAAACNKSP111GGA12D0DNANNKNNKN..KS1NAPAD...............", // 17
        ".DEPCAAKKD1NNN11NN2NIDNNCC1C111KD1DD1NPNN...............", // 18
        "..DNNANKN11N00KNNNA1KKNA2NNAQQNKSD1DDNAKD...............", // 19
        "....KNNK111Nd0N31NANNNNKNA12EE1KKDPAKANSDK..............", // 20
        "...APNNDN111NN1CCNNCPANNKDNN11NDDKAANDADSDD.............", // 21
        ".KPEQCDTIDC121CQQANNNNDDKKDDKKSKDDKACTCDTK..............", // 22
        "..DPAKIDDNN1CNAPANNDDDSTSSKSNNSNTDNAAQNDSDD.............", // 23
        ".....QNPAGg1KKKNNDKDSSDDDDKSDKSKSDKNPPKDDK..............", // 24
        "....PCKPEAgNTKDKKKDKDDSSDKDDKKSDKKAAPNDDD...............", // 25
        "....KD1NCCDT.TNSTSSDDKDDDDSNSSKKKSNANDDDD...............", // 26
        "...NDDD1KD....DDTSDKKDDSSDKSTTDDKDKNDD..D...............", // 27
        "...DSDD1KD....TDDDKDDKNKKKDSTSDKDNKK....................", // 28
        "...DDD1NDS.....TDDKKDDDDKNDNDDDKNANDN...................", // 29
        "...NPADDT.......TSSKSKKKKSSAEENSKNANDK..................", // 30
        "..NKNKKT...........DUdUKKDDN11ND0SNANDD.................", // 31
        "..KD13N..........SKAKVi..TdBKK0d.VKKKKD.................", // 32
        "..DD1AKT......TSNK12ADVUKKKKKKKKK0N121ND................", // 33
        "..DANDDDDT..TSNNAPE1N1DKANNCKKPKNKNKN1S1NK..............", // 34
        "...KDDDSDD.SCS1AQE1AQ1NACNADDDAANNNANNNAAN.DD...........", // 35
        "...DDPDKDDTDEQNQEPAEQANCANPKDDKPKDNNAACNNNDDD...........", // 36
        "....DDPDDTS1QN1QPAQQNNPCAKQKDSKEKDDDNNAPPANNK...........", // 37
        ".....DNKD..AN12AN12NN1CCKPDDDDDDPKDDSDDNNKKKND..........", // 38
        "..........DN22AN1AKACJOAKQKDDDDNQKSSDDKKNN1NKDD.........", // 39
        ".........KK1AAKKKNACCCONNSKDDNSDTDSDDKSDKNQ1KKD.........", // 40
        ".......NNNNAKNAACCOJ1CCNPNKNDDDDKPDDKDTDNAE2KDN.........", // 41
        ".....DNL1ANKDrrPPC2C1CNK2NNNKDDDK1DDKDDNN12NK.DN........", // 42
        "......IDLANNNQQQPPCJJANKTNNNNKDDKSDDK11QCNNNK..DAD......", // 43
        ".....DdDNNNNNKN1PrCCCNNCNNAANNKKKDAKKKKKKAENND..DPD.....", // 44
        "....DNNNKA1CAKKAPrJCCKN1KNAANNNNNN1NSSDDNKNKND...DP.....", // 45
        "..K1QPKN22OPCNKAPPCJNNN1NNAANAANNN1DTTSNKN2NND....DPD...", // 46
        "..D1QPNAACPPNANAAACAKNKQNNACANCANNPKTSDDDA2NNN.....DAD..", // 47
        "...1JQPNNACAAPANNNANSSDDNNACANACNDDKDDDSNPPKNND......A..", // 48
        "....PJQPCNNNNANANDSS..SANNACCANNNNNKNDTDNEPKNAD......DA.", // 49
        "....DNJPCNKDggNANS....S1KKAPPAKTTN1NKDSKDEPKNCD.......DK", // 50
        "......DNNKDKKLKNS.....SNKTNNNS...NNNNKDKDQNNNAD.........", // 51
        ".........NDKKLKNS..................SKKKDDQNNCCD.........", // 52
        ".........DPDKGKS......................KDDQNNCPD.........", // 53
        "..........E1KNDT......................KDA1NDAAD.........", // 54
        ".......DKDQADNT.......................KKN1K.............", // 55
        "........DNNDKT........................KDNDK.............", // 56
        "......D..DKDKT.......................SDDKKDD............", // 57
        ".....KKND.DDKT......TTTTTTTTTTTTTTTTTDSNPDT.N...........", // 58
        "...NDDTDKDD1NSTTTTTSKKKKKKKKKKKKKKKKKDTDDDDDKD..........", // 59
        ".KD.DKDTDKDDDKKKKKKKKKKKKKKKKKKKKKKKKDTDDKSSDDD.........", // 60
        ".N...DKSSNDDKKKKKKKKKKKKKKKKKKKKKKKKKKDNDKDDNS.D........", // 61
        ".KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKD.......", // 62
        "...............KKKKKKKKKKKKKKKKKKKKKKKKKKK.............." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'L' to Color(0xFF50BE28),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'd' to Color(0xFF5AD2CD),
        'g' to Color(0xFF199123),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawAtomos() {
    val matrix = arrayOf(
        "..............................................", // 0
        "..............................................", // 1
        "...i..........................................", // 2
        "......................D.......................", // 3
        ".....i....i...........D.......................", // 4
        "...........i..........D.......................", // 5
        "............i.........D......i........F.......", // 6
        "........i...i.........D...............E.......", // 7
        "........ii............D..............EF.......", // 8
        ".........iiiii........D......i......EF.F......", // 9
        ".........iiiiii.......D.............EEEEF.....", // 10
        "..........i...i.......D.................F.....", // 11
        "...........i..ii......D......i................", // 12
        "............ii.i......AN.....i.m......F.......", // 13
        ".............ii.......AN.......i....FE.i......", // 14
        "............iim......QPAD....m.i....EF.i......", // 15
        ".............i..D...DNNAD.....m...............", // 16
        ".......I.i.i.i..DD..DPANK....Dii...Q.F.i......", // 17
        ".........miii....ID.DNNNDD..DDmm....EE........", // 18
        ".....D..mmmi.....DrDDrSrNKKDKSmI.....i........", // 19
        "..........m.....DDDIIrIDDDDDKSSD.....i........", // 20
        ".......DD...DI.DKDDrDPSSIDIDDDD....m.i........", // 21
        "........DDDDrDMDNDIIDSSSSSDDDDK.....m.m.......", // 22
        ".........DDKDDIIDKDrSTTTTETSIrr.....mm.i......", // 23
        "...........DSmIIMSDPEFTTSQQQSSm.MD...Iii......", // 24
        "..........NDSDMDIrQEDDNNNNNDDmm.ID...Imi......", // 25
        ".........DNDSISDIQFSKKKKKKKDSm.mIK...Im.......", // 26
        "......F..IDDImIDDTSSKKKKKKKKD..iDKK.mV........", // 27
        ".........DDSISSrSqSDKKKKKKKKK..mKKK..m.m......", // 28
        ".....F..NrrDPQDrTSKDDKKKKKKKKDQmKKNNDIm.......", // 29
        ".....E.IDMSrSTDMTSDKKKKKKKKKKNSDNKDDDD0I......", // 30
        ".....E.DSSDDDSDDTNKKKKKKKKKKKKNKNDDDNDKM......", // 31
        "......m...MBDDrDSKKKKKKKKKKKKKKKDNDTTMMI......", // 32
        "....E.mmmIMIDDDrmKKKKKKKKKKKKKKKKDKNTim.......", // 33
        ".....E.mIMISSDDDSDKKKKKKKKKKKKKKNKKDBT........", // 34
        "......Q.mISSTDKDiDKBBBBBKKKKKKKDSDKKB.........", // 35
        ".......mISSDSDNiSKKKKKBBBBKKKKKSQSKKK.........", // 36
        "....DmmmITDDKNDSDKKDBBBBBBBKKKKSQKKKKD.D......", // 37
        ".....MIITSDDDDDIDKBBMI0BKBBKKKKDQDDKKD.D......", // 38
        ".....DITTKNKDDSDDBB0II00BKBKKKKNPPDKKKMD......", // 39
        ".....DDSSKNrDDIDKBVVIVIBBKBKKKKNSPDKKID.......", // 40
        ".......MIDDSSIIDDBViiiV0KKBBKKKDSPNKDD........", // 41
        "...mi..KB0NDSDIDIMViiiVIBKBBKKKDQDKNKKD.......", // 42
        "...D.iDKKKKDDDmDDMIiiiVVBKB0KKKDQDKK.DK.......", // 43
        "......IDKKKDmSmKDIIViVI0KK0BKKKDQDDD..........", // 44
        ".....DmIDDKNDDIDDDSIVIIBKB0BKKKDQDD.D.........", // 45
        "......imMDrKDDIDDDDBBKKBB0BKKKKDPDDD..........", // 46
        "......mmSKDDDDSDDDKBB0IV0KKKKKKPPDK...D.......", // 47
        "......imDKKDDISDDKKBBBBBKKKKKKNPDDK..D........", // 48
        ".......mIKKKDIDDDKKKKKKKKKKKKKNDDDKKDD........", // 49
        ".......ITSKKNDDINKKKKKKKKKKKKKDDDKKDBKK.......", // 50
        "......iiWSNKKDDIDKKKKKKKKKKKKKDDDKKDKKKD......", // 51
        "......iSDDNKKIDDmDKDKKKKDKKKKKKNNKDKIDD.......", // 52
        ".....i.DKDKKDMKKDmimImIKKDDDBDKNKKDKDDD.......", // 53
        "...iimDrKKKKDDKDDImMIIISIDDIISIKKKDKKD........", // 54
        "...mimDDQKKKKDDmmmIIISmiiTIIDSTKKKKKKKDm.mi...", // 55
        "...mimDDDNKKD0mmmmiiIISmiWiiTiiSIDKKKK0IIIIi..", // 56
        "...iimmDDNKKDImiWiiiimmmImmIIISTmMBBK0MB0mIm..", // 57
        "...miImIMDDDIIImiWimmiimIIIIIISmmSI0MMI0mII...", // 58
        "...miTSmSDDImmmImiiimImiiiiiiiimmmImmmII00....", // 59
        "....miSmmmiiiWimmmmmImmmmiimmmmmmmmmIMMMIVD...", // 60
        "...DDmiimiiiiimmm0DDDMIImImmiiimmmiiiimimDD...", // 61
        ".....DIDDDDDDDDDDDDDDDDDDDD0miiiiiiTD000KD....", // 62
        ".............................mm..............." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'q' to Color(0xFFFA649B),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawExdeath() {
    val matrix = arrayOf(
        ".............................................", // 0
        ".............................................", // 1
        "................3.4144431....................", // 2
        "................1..42b324..2.................", // 3
        "............4...2v...1E4...0.................", // 4
        "...........23...b34.4233..23.................", // .
        "..........22....4bK32K32222.42...............", // .
        "..........22.....2b23dv3b22..23..............", // 7
        ".........341......22333v21...144.............", // 8
        ".........3424...4D022133x1...233.............", // 9
        ".........3332...x11C22DAAAE..233.............", // 10
        ".........324334222a1Kb3F21E33233.............", // 11
        "..........23422123222vKxD01xa222.............", // 12
        "..........2233332bv3EbK3EAAxbK32.............", // 13
        "..........1Ex3vv3323FE3F1E0EaK322............", // 14
        ".........3F2b3d33wFax2FD1F1xx22xD2...........", // 1.
        ".....3...3DEx32FEAF3bx2x2D22F21g2Ka..........", // 1.
        "....32...23II1E1D1F2vK33FxxK22g2K2K2......g..", // 17
        "....23..23333321D22v.evv332bvbgKKK22D2...21..", // 18
        "....22..Q2a111g01223vvvvbFD3K20222x2Fx1Q22...", // 19
        "....22..0aaKvK11a222bbb23F22a10g1D33E2EQ2....", // 20
        "....232.1vaKvK11221Dxx22b3Kb21Qaa2aFFD13.....", // 21
        ".....232aKa1aCA0111x12KKbKb2gAQ22K312A.......", // 22
        ".....13332vv201A0D1g122xEFF011Q1b1vCb2.......", // 23
        ".....3233bvebgA1Axx12Q112aA20MAba0b1Ka3......", // 2.
        "......322bbb32AA1EEDg22x2FgE0M12202gg2.......", // 2.
        ".......2EFbK3yED11A22b22xE0D0MQQa1ED.........", // 2.
        ".......2QJF2FFy33b0112DED1Ax0MQa3DD3.........", // 27
        ".......2I1ADEEF2.21FD11DxxFD2g22.3x3.........", // 28
        ".......2IQ11ADE3w22FDFD12DA21112.2ED.........", // 29
        ".......23Q11101D221Ex22222021g023DDx.........", // 30
        ".......Q3Q1110a1aDEJF2x21222g212..xD.........", // 31
        ".......Q311AA11K31EDD3E212221012..Ax3........", // 32
        ".......Q3A12D11bb1EDx322211g2102..AD.........", // 33
        ".......Q3A1AF1Aa1DnDx32a2a112112.2EA.........", // 3.
        ".......2vAM1AxFDADAD2F1212a221A22.DE.........", // 3.
        "......22vAM1DFD111211122112g22AQ2.2E3........", // 3.
        "......233A21F1x102a2D1g1Dx2F22AQ2.DE2........", // 37
        "......233AN01x332100D1221xFD22AQ2.DD3........", // 38
        "......2321N11x2x3222x3331F2222AQ22ww3........", // 39
        "......232MNA1xxy323333331x2x22A123AwD........", // .0
        "......232M202x33333xF3331xa222A12v1FE........", // .1
        "......I31MMA2x3332x333331x22231Q2.3Dw3.......", // .2
        ".....2331MMA2x3x3x3333331FE2231M2.31w3.......", // .3
        ".....233AMM12x223x32y3331xF122gMQ231w3.......", // ..
        ".....333AMM1b2xxF222y3331222221MQ2.0FD.......", // ..
        "....3333AMMA02vv3xxxF333ExE12112M2.03J.......", // ..
        "....23321NM0a2111Kv33333011Q0112M2v0xx.......", // .7
        "....33v1MNM02vvba11111g12K37AM12M2.1DF.......", // .8
        "...333v1MNM02vvbbg1QQQA1bv3g1MQ2MQ231w3......", // .9
        "...2v331MN102vvb2AMMM1A1av3g1MQM2Q231w3......", // .0
        "..32333AMN10b.3211MMM1A113301NMMN2231w3......", // .1
        "..233y31NMQ0KvKCAMNMMADD1K2aA2MMNM22A3E......", // .2
        "..x33y1MNMAg1121AMNM100FD13K031MN223AEE......", // .3
        ".3xv331A23D0bKg00AM2..3Jx23.1..32M23A1E......", // ..
        ".3x333a...DCbK1D2.....a1xab2F12..31331F3.....", // ..
        "..Q33y3...02Eb1F2.....2C1EDE2KC23.3..1E2.....", // ..
        "..3xxy3..302wEA1.......21A12K23vb11..AD2.....", // .7
        "...23y3..2222FA1..........1aC22222...312.....", // .8
        "....233..1bvaaDa......................12.....", // .9
        ".....31.3b32211..............................", // 60
        ".........1111g3..............................", // 61
        ".............................................", // 62
        "............................................." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF0C0C10),
        '1' to Color(0xFF262630),
        '2' to Color(0xFF484855),
        '3' to Color(0xFF737684),
        '4' to Color(0xFFA2A6B4),
        '5' to Color(0xFFD0D4E0),
        '6' to Color(0xFFFAFAFF),
        '7' to Color(0xFF121C44),
        'A' to Color(0xFF301C12),
        'C' to Color(0xFF0F3444),
        'D' to Color(0xFF58301C),
        'E' to Color(0xFF844E2D),
        'F' to Color(0xFFB27648),
        'I' to Color(0xFF55267A),
        'J' to Color(0xFF70520C),
        'K' to Color(0xFF4E8080),
        'M' to Color(0xFF581234),
        'N' to Color(0xFF8E2058),
        'Q' to Color(0xFF301644),
        'a' to Color(0xFF204444),
        'b' to Color(0xFF346262),
        'd' to Color(0xFF348EA5),
        'e' to Color(0xFF66BCCD),
        'g' to Color(0xFF122626),
        'n' to Color(0xFFB24816),
        'v' to Color(0xFF76A8A8),
        'w' to Color(0xFFDAA270),
        'x' to Color(0xFF8E4E3E),
        'y' to Color(0xFFC0765C)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawNeoExdeath() {
    val matrix = arrayOf(
        ".........................................D.DD...................", // 0
        "..................................D.I.D...DDD.D...D.............", // 1
        ".................................DD.DDD.D.DND.DDII..............", // 2
        "..........................D......D...DNDDDDDNDDSSDDD............", // 3
        "........................D..D......D...DDDNDSDDSNNDNDDD..D.DND.D.", // 4
        "............................D............DKNKNNKDDDDDDDAADDDDD..", // 5
        ".......................DD................DDDNDSNNSDDDPADDDSSDDD.", // 6
        ".........................DD...DDD.D........DNKNDDNNNDDSSSSSDDND.", // 7
        "........................D..D...D.D..........DNDSDNDDNPDDSSSrDD..", // 8
        "..........................D...DDD.....DDDD.....DDDDDKNPIDDDDSD..", // 9
        ".......................D.rAA.DDS.DD..DDDDDD.D..DSDDDDNKDDSDSDD..", // 10
        "......................DAArrrP..D.D.D.DNDDSD....NDNKSDDDSSSDDD...", // 11
        "......................rrrrArPr.D..D..DDNDD.DDD.DNKKDSDSDSDSNDD..", // 12
        ".......DIMI..........rPAArPrPADD.DDDDDDDD.D.D.DDDNDDNKDTSDKDDDI.", // 13
        "....IMI.II.....I...APPrPrrPrrDD..DDDDDSSD..DDDDDDSDDDKNDDSSNDD..", // 14
        "..IImm...DDmI.mmmmIrPrrrARrASDDDDDDDDDDS.....DSDDNDNTSDDDS.DD...", // 15
        ".......IMDAANDMMImMDrrrrAANNDDIDDDDSDDDD...DD...........DDTTDD..", // 16
        "..Im.ImDAAPPAAPNNDNAPrPrrNNMIISDDSTDDDS....D............DSS.....", // 17
        ".m....IA1AQQ1APAPPPAAPrrNDDDIISSDPDDDD..D.......................", // 18
        ".......QCNAAAPNNP1PPDDDDDDSDSSISSDSDS..NDD......................", // 19
        ".mmI.mDPNADDN1APQPDASDDDDSDDIIIISIIDDrPDSD.DD...................", // 20
        "......DNCPImMDQADDDNNNDSDNSDDISISIIDDSSDDDDD....................", // 21
        "......DQA1DDDSDNNNDAANDDSDDSDIDISIISDSDDDDDD....................", // 22
        ".mDDDPDAPANNDTSNDSNDPNNSDNDDDDDDImDDDDDDDDD.D.........DD........", // 23
        ".IDPDAAAPANDDSSDDDSDqDDNDSSDDDDDDDSDSDSTDDDK........DDDD........", // 24
        ".DPAAPAPNDSDNDDDDDSDSrNDSSDSDSDSSNDSSDDDDNND........DDD.........", // 25
        "PPCPSTAPDDSSDDDDSDSNrPDSNDDDDSDNNDDNDNNNNDDr.......DDD..........", // 26
        "DP1NSSNNDDSSSSSDSDDDDDDDDDDDDDDDNDDDNNrrDIm.N.......DD.D........", // 27
        "APASSDDAPDSSSSSNSTSDrDSDDSSDDSDDKDNNAPRNDDmMND.m.....Pr.........", // 28
        "PDDSrrTDDDDDDNDSDDDNPDDDDDSSDSDDDNrrANDDDPCDNDPmD...............", // 29
        "NNDPTTrNDDDPrNDDDDDNPNDDDDDSSDDDDNrAASSTTSSDANPDN....rP.........", // 30
        "NDMDTSNNDDDrSDDDNrDIDNDDDDDDDSDSDDARDSDS.TSDrDQPNA.DPAD.........", // 31
        "MDNDTTNDDSSDDDDmINMmDDDNDNDDDDSDDDArNNDT.TSrrDPQArArrD..........", // 32
        "DNKISSSDDDSSDSSImMMmSDAADNNDDDDDDNNPRNDSSDTASIPEAPPPR...........", // 33
        "IMNDmIDDSSSSSDSSISMMMDDANNNNDDNDANrNrPSSTTDPDIPSSSSr............", // 34
        "MIDPASQADDDrPSDDSDIIIIDDDDNSNArASNRArrD..TDSDIS.TTT.............", // 35
        ".rDNPPASPPSSrqPANDDDSSmNDDDDNKNDDAArrPS..TTT....................", // 36
        "rPqPDDSSSDDSSNADNNDSSTTDDSSNDDDDDNrrNrDS........................", // 37
        "rTrqrTPrNDNSSSDISDDDS.TDT.TDDSDIDDNDSSDS........................", // 38
        "rirqSrSDDDMDDT.SmDDDS.......SIDSmDST............................", // 39
        "DTSr.rDDSSDSDS..SMSDS......TSiimSDT.............................", // 40
        "QSDrDDDDDSSIII..............IiISDT..............................", // 41
        "QPSAS..TDD.DID..................................................", // 42
        "N..............................................................." // 43
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'q' to Color(0xFFFA649B),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawWhelk() {
    val matrix = arrayOf(
        "........................D.......................................", // 0
        "........................DD......................................", // 1
        "........................DD......................................", // 2
        "........................DI......................................", // 3
        "........................DD......................................", // 4
        "........................D.......................................", // 5
        "........................Dm......................................", // 6
        "........................KI......................................", // 7
        "........................KII.....................................", // 8
        "........................DmD.....D...............................", // 9
        "........DD..............DMD....N................................", // 10
        ".........DD....D.....NDKISD...DD................................", // 11
        "..........D.....D..DDDKKMID..DD.................................", // 12
        "...........DD...DMNDDMNNMm.DDID..D..............................", // 13
        "............DD..DDIDIDNDD.mDDMK.DD..............................", // 14
        ".......D....NIIDDKSSDDMm..IMIDKID...DD.....D.DDDD...............", // 15
        ".......DD....DmSDDMTSSII....DDNSD..MD....DKKNNNDNDD.............", // 16
        "........ND..DKMIMIMSWST.IMIIImIKDDID....DKNNDPPQPPDND...ADD.....", // 17
        "........DDD.KMIIISSMTT........IIDKD...DMNDPPTEPPPPADNDPQPDDDDN..", // 18
        ".........DMDNMTDMSmIT.......m...DKD.D.INDPPQPQFEPPPNNQDNKKTW....", // 19
        "..........DISMmSDMSI........m..MDKKDIDNDPPPPEEQPADNDPAKNDDDD....", // 20
        ".D.......NKITSMDSIDMI.......IDMIDDKNDNNPPPTEPPADDNASQANKNPPDND..", // 21
        "..DD....NDKNITSSSImmIIM.....mIIMDMDMDNDPDQFPAPAPQAPDDDDDD....KD.", // 22
        "..DDDD.DDDNDDTTIITFTSI..m.I.MMMMMDDMDNAPPEQDPESPQPQDtNDG........", // 23
        "...DMIKKDKDIKIMITWWTm......mIISmIMDDDDDEPPASSDNNNDPPDDPND.......", // 24
        "....DISDKDSIDDDITWFT........mmTmIMDDNDAPPPPDKKDNKKNDDNDD........", // 25
        ".....DSmSIIMNNMMSTTmm........ImSIDDDNPPPPPPNNNND.DKKDDDD........", // 26
        "......DITTIMDDMMMISI........IMSDDDDNNDPPPPPNDDD....DQNNP........", // 27
        "......KMMTSIIDDIIIIMIm.....mmIIDIIINKNDPDDPNNN......QDDDD.......", // 28
        "......KKDSIMSmSMIISIImI....mIMImSMIDNNDDDNDDNK.....DP..DD.......", // 29
        "......KKDMDISTTTSIIImS....mIMImWSmIDNNDDDNDAQD.....D....N.......", // 30
        "......DDIMDMTTTWTmmIIMIm.IMDSmSTmTMDKNNDDPANPQP...N.....D.......", // 31
        "......NNMIDDSSTWWTmSIISIIMMMTSISISSDNDDNDPQPAS..................", // 32
        ".......KMMmMMITFSmTWTTTTTIMIFSImTSIDNNDDDDPQAPPD................", // 33
        ".......DIISTMImFTTTWTTTWFTISTSMSTSMKNNNDNPEQAPPD................", // 34
        ".......KImmTSImTFmSWWmWWSSMTTSmMIIIMKNNNAQQPPEQA................", // 35
        ".......DDImImTSISIITTSTWSTIIISTIDMImDKNDDPAPPESD................", // 36
        "........NDIMSTSmIMDImSmTTTSMDDMIMMmIIKKNDCPPTED.................", // 37
        "........DDDDDMSIMSSMIMImmSIIDDITSDIIISDKNPQPQPD.................", // 38
        ".........DKNNMIDMISDDMMMMMMDDDITmDDIIITDDNDADD..................", // 39
        "......DNKKKKKNNNKDDKKKDMMSSITTIMIDKKDDSmIDKKKND.................", // 40
        ".........DDDDKKKKKKKKNDMMMIMMISDKKKKKNKKNKKDD...................", // 41
        "...................KKKKKDNKDKKKKKKKKD..........................." // 42
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'm' to Color(0xFFA578D7),
        't' to Color(0xFF0A6469)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawVargas() {
    val matrix = arrayOf(
        "...............DND......DDKK.................", // 0
        ".......................D0IDDD................", // 1
        "..............D.......00DSSDKAD..............", // 2
        "..............KD...D.DKKN..DRND0.............", // 3
        "...............DDDD0DK0S0DSKKD00.D..DD.......", // 4
        "................DD0DK0S0DDNND0IDDDDDDD.......", // 5
        "...................ND0DSSSSNDDDDDDN..K.......", // 6
        ".................D.D0DT...TNKDDRDDD...D..DDD.", // 7
        "................0IDDDT...TDDNPDPDNN....DD...D", // 8
        "...............DDDDD...TTNDNCNDDCD..........K", // 9
        "...............D.....TSDKDDNAKDPDD.........DD", // 10
        ".....................DKNDDDNNNNDD.........DK.", // 11
        "................D...DDDDNNDNDDNKND.......DD..", // 12
        "................D..DDDNDDDNDDNNNKNK..........", // 13
        "...............D...DPDDPQDNNDDNNNKNK.........", // 14
        ".............DK....DDDPPSDNNDDPDDNDN.........", // 15
        ".............D....NDNDDDDDKNDPDDDNNDD........", // 16
        ".............D....NDNDNNNNKNNDSSSDNPN........", // 17
        "..................DDDDSDKKNKNDDDDDNND........", // 18
        "..................NDDPSAKKNNKNNDDDDN.........", // 19
        "................DNNDNDDDKKNDDNDDPNDK....D....", // 20
        "................NDSSDNKNKNDDDNNDNDN..DDDDD...", // 21
        "..NKD..........DNDPDDDKKKDPDDDKNDDKK.KNDPP...", // 22
        "...NRAN........DNDDNDDNNRDPPDNNDNNNNNNNDPD...", // 23
        "....NACAN.......NNDDPDNZRDDNKNNDDKNKRNDDD....", // 24
        "......NZZD.......KNNNNNNNNKKKKNNDKNDKKKN.....", // 25
        ".......RPR............NKKKKAPCDDNANDDKD......", // 26
        ".......NRCN........DNNNNKNNDPQQSPNDT.DK......", // 27
        ".......DACZRRNDDDNRNNKKNKAKNAPPCNNKD.TKND....", // 28
        ".......DNRRAZCCZPPRNKNNDNNKKAQEEPNNNT.SNND...", // 29
        "........NRZZCARPRNKNDNNANNNKKKDDDDNAN..SNAD..", // 30
        ".........NNRCARKKKNNDPADDNNKD0U00DKPAT..DRN..", // 31
        ".........DDKKKKNNCAAAAACNANKD0000DKACDSTDNCD.", // 32
        "....DD00DDKKKNDNDPPDACPAAAKDDII00DKKNDTTTNZN.", // 33
        "...D00DKKKNNDCPPZCCPPAAACNKDDIS00KKDNT...NRRN", // 34
        "..D.DNNDPJCADDAPQEEQCCPQAKKDDIS0DDKDDD...KRON", // 35
        "..D0NPPQEQACPQPEQPC1PPQAKKDDD0SIDDKSSST.SKNCN", // 36
        ".D0DPPAPPEJOPQPAACQQQPPNI00I0DI0DDDDT.T.DNAXN", // 37
        "DDSDNNKAZAACJJJJPQPQQQNK0II0SD00DDDKT...DNAXN", // 38
        "D0IDDDDKNAQQCCQOQQZQQNKDDIS0SI0DDDDKT..TNZRRD", // 39
        ".D0DDDD..AQQPPPQEQQCAKKDD0I00V0DDDDKT.TPZRRN.", // 40
        ".DDDIK....AZPEQEEPPDKKKD0III0I0DDD0KTTDCRZC..", // 41
        "..K0SD....KNDQEQPDN..KDD0II00ISDD00KTTNRAPA..", // 42
        "..DDDD......DDDDD....K0DD0S000SDD00DSNANRN...", // 43
        "....D...............ND0D0IS00V0KD0DDARKND....", // 44
        "....................D00DD0IS00DDDDKKZKD......", // 45
        "....................DI00D000DKKD0DKKND.......", // 46
        "...................D00I0DDDDKKKDDK..ND.......", // 47
        "...................DI000DKKKKKDDDD..DD.......", // 48
        "...................DII00DKKKKDDDK....N.......", // 49
        "...................DDI00DKKKNNDK.............", // 50
        "....................D000DDSTDDPN.............", // 51
        "...................DK00DKD.DANN..............", // 52
        "...................DDNDKKS.SNPP..............", // 53
        "....................NDAKN..SAPA..............", // 54
        ".....................DANT.SDPDND.............", // 55
        "...................DPPND..SKANPNND...........", // 56
        "...................DDPAT..SDKDNNDDN..........", // 57
        "...................DPPNT.TDNKKKKNDDNN........", // 58
        "...................PDKKNDNKKKKKKKKKKKK.......", // 59
        "..................DAKNNKKKKKKKKKKKKKD........", // 60
        ".................KKKKNDDNNKKKKKKKN...........", // 61
        "................KKKKNDDPDDDKKKKK.............", // 62
        "...................KKKNNNKKKKD..............." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'I' to Color(0xFF7346AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'X' to Color(0xFFF5372D),
        'Z' to Color(0xFFD21E19)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawNumber024() {
    val matrix = arrayOf(
        "....................PDA.............................", // 0
        "...................DP1A1............................", // 1
        "..................DAPSAA............................", // 2
        "..................AASSSA...Q........................", // 3
        "..................DASTSD....P.......................", // 4
        ".............QQ.PADDDDDNP...DE......................", // 5
        ".............A...ASDSDmDP...P.......................", // 6
        "............PA..D..ADSSNN..PCC.QD...................", // 7
        "............AP.....NASDNAPPCPACA..P.................", // 8
        "............NC...KADNDDKAPPC1PAP....................", // 9
        "...........PAA.EDADAAPPNNADAS1DD....................", // 10
        "............NAPDAACNKNANKKNAPCAAPQQP................", // 11
        "............NAATPANKD0DKKDKKAKDNNN..................", // 12
        ".......Q.QQPACAA1ANKKDKKKDNAPDDKNP..............FQEE", // 13
        ".............C1CCKNKKKKKKKNNADKKKDAN.........EQ.F...", // 14
        ".............NNAAKDDNKKDDDACASKKKKKKAPP..EQQQ.......", // 15
        "...........Q.KKNPAANAKNNDDNNPTKKKKKKDSPQPPE.........", // 16
        ".............KKKNA1CNADKKKKKP.SKKDDDTSDQ............", // 17
        "..............KKKSNNNKKBDDNNA.A.KK0STSD.............", // 18
        "..............KKDTNDNNNPAQCAN....KD..IDP............", // 19
        "...........Q..KDTTNPAAPPSTAAD.......................", // 20
        "...........Q.KKT.TDP1PSQmSDS........................", // 21
        "...........PNKKD.QQSPSTTTTDKD.......................", // 22
        "..........DANKKK.P.QDSDDDSDKD.......................", // 23
        "..........BANAKD...P1PDSSAQDD.......................", // 24
        "..........DANND...APQAAPPAEDKKKK....................", // 25
        "..........ADDN....CQPNDDDNAAKKNND...................", // 26
        "..........ADS.....PQQD0S0KDDKKNPN...................", // 27
        ".........NNS0....PPPD0mS0KSSNKKND.D.................", // 28
        ".........NNSm...PCAASSmSSBSPD.DD..D.................", // 29
        ".........AA.....PPPDSmSI0BDCD......D................", // 30
        "......E..AN.....CDDSTmI00BDD........................", // 31
        ".......Q1N.........mmIDND00.I.......D.......D.......", // 32
        ".......QAD...........DKKB0.m.........D.....DAA......", // 33
        ".......QNQ......m..mIBKK00.m.........D.....DNP......", // 34
        ".......1A.......mm.mSSSD00...........D....K.........", // 35
        "......EP...........m0SSDD.............D..D..........", // 36
        "......1.........m.m.0S.DD..............DD...........", // 37
        ".....QQ...........m.0T.TD..m........................", // 38
        ".....1..............D...D0..P.......................", // 39
        ".....Q..........mm.D....D0...A......................", // 40
        "....QE..........m.I.....D0DPQE......................", // 41
        "....Q...................DDDAP.......................", // 42
        "...Q..........m...m......D1CD.......................", // 43
        "...Q...........mm........DND.I......................", // 44
        "..F..........0...m.......DD.m0......................", // 45
        ".............00mI.........0m........................", // 46
        "............ADBS0...................................", // 47
        ".Q...........1AP0.........00.I......................", // 48
        ".............1PPD..........DDB0.....................", // 49
        "P...........QPC1...........ANND.....................", // 50
        "...........PP1C...........QNNNN.....................", // 51
        "...........PCCAN..........EC1AP.....................", // 52
        "............DCPA............AKDP....................", // 53
        ".............AAA...........DKDKA....................", // 54
        ".............NAA...........DDDN.....................", // 55
        ".............AAN...........DND......................", // 56
        "............PNKK...........DKD......................", // 57
        ".............KKK...........DKNm.....................", // 58
        ".............NKKDDDKKKKKKDDDKKD.....................", // 59
        "...........DKKKKKKKKKKKKKKKKKKK.....................", // 60
        "...........DKDKKKKKKK..DKKKKKKDD....................", // 61
        "..........KNKKKK.......DKKKKKKKNDD..................", // 62
        "..........KKD............KKKKKKKKD.................." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawUltros() {
    val matrix = arrayOf(
        "..................................................DNNN..........", // 0
        "..................................................N.NND.........", // 1
        ".................................................D...DND........", // 2
        ".................................................D...DDK........", // 3
        "...........................................DDDD......DDK........", // 4
        "..........................................DMMMMND....DMK........", // 5
        ".........................................DMMMMMIMDDDDIDN........", // 6
        ".........................................DMDNKDDIMDDDDD.........", // 7
        "........................................DMDNKKNNNDDDND..........", // 8
        "..................DDDDNN................DDDNK...DNND............", // 9
        "..............DDDMMMMMDNKN..............DNDNK...................", // 10
        ".............DDDMMIMIMMKARDD............DNNNKD..................", // 11
        "............DDMMMMIIIID1ANNM..DDDDDDD....DDMDD..................", // 12
        "..........DDDMMMMMIImmD21ADIIMMDl21ANN....DDMIM.................", // 13
        ".........DDNNDMMIIImimM1DDImIDNKKANKKKD....DDMMIII..............", // 14
        "........DNNNNMMIIImImmIMMIIIIIll1NK.........DDMIIMD.............", // 15
        "........NNNNNDMMIIIIIIIIIIIIINA12l1D.........DDIIMM.............", // 16
        "........NMDKKKNDMIIImIIIIIIIIDNNA11ADD.......DDDIDMD............", // 17
        ".......DNDKNANPDDMIIIIIIIIDDDP1NKKNNNKNDM.....DMmIMD............", // 18
        "......DNNNKRRKNMMIIMIIIIDDP1KNl1KKKKKKKNDDD...DIWDMD............", // 19
        ".....DDDDKAANMMMMIIIIIIIMNAF1KNA1KNDDMDNKKDN..DIWIDN...DDID.....", // 20
        ".....DNMMK1DMIIMIIIIMMQPDDNDlAKKNNKNDMMDNKKK..DImINN..DDMMMDD...", // 21
        ".....NNDDNADImIIIIMMDN1FPNKKN1NKKKKKNMMMMDKKDDMMIMK..DDMIIMMD...", // 22
        ".....DNDDDKDIIIIMDDPTPNDlAKKKKNKKKDDDDMMIIDMMMIIMDN..DDDDDDDNDD.", // 23
        ".....DNDMMNMMMMMD1NN1lNKK1NKKKKNDMIIIMNDDMIIMMMMDN...NDMD...DND.", // 24
        "......DNDMDDDDDlN1NKK11KKKNKKNDDMIImiIDKKNDMMDDNK....NNMD.....ND", // 25
        "......DKDDMMMMN1KA1KKKNNKKKNDDMMIIMImIMKKKNNNNKD......NDDD....NN", // 26
        "......DKNDDMDPKAKN1KKKKNKKKKNDMMMDNDIMMKKKNKKKN.......NDMDD....N", // 27
        ".......DNDNDNDKNKKNNKKKNKNKKKNNNNKNDIMDKKNNNKKD.......NDDDN....D", // 28
        ".....DDKKNNNKNKKKKKKKNKKDDDNKKKKKKDDMMNKNNNDMDDDD...NKDMMND.....", // 29
        "N...DMIMKKNKKKNNKKKNKNNKDMMDDKKKKKNNMMNKTDNNDNMMMDDDDMMMDK......", // 30
        "KDDDMNDDDKKKKNDDKKDDNDDMNDMDMDNKKKNDMMDKTTNKKKDMMMDDIIMMNK......", // 31
        ".DDMDKKNDNKKKMMMNKMDDMMDDNDIIMMDKKKDMIMNDDNDDDKNDMNDMMDND.......", // 32
        "..DDD..DNDDDKDMDNKMDMDMIMNDDIMMDNKKNDIMDNNDMIIDKKNNNDNKKKDDDD...", // 33
        "........MIIIMKNNKNMMMNDMDKTNDMMDNKKKNMDMMDMMDDMDNKKKKKKKKKKKKNDD", // 34
        ".....DDDMDMIIMKKNDIMDKNDNKTTKKKNNKKKKDMmIIMDNKDMMDKKKKKKD.......", // 35
        "...DDDMDKKNDMMImIIMMKKNNKKNDKKKKKK...KNDDDNN..DKKNKN............", // 36
        "...KNDNDTTTDNDIMDMDDKKKKKKN...........KKKKN......DKKD...........", // 37
        "DDDKKKKKDDDNKNDNNNKKKK..........................................", // 38
        "................DKKKKK..........................................", // 39
        ".................DNNNMD.DNDD....................................", // 40
        "..................KKNMMDMMII....................................", // 41
        "...................KNDMIDMDDII..................................", // 42
        "....................DKNDNK.NND..................................", // 43
        "......................DND...DD..................................", // 44
        ".............................DN.................................", // 45
        "..............................D................................." // 46
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        'A' to Color(0xFF823719),
        'D' to Color(0xFF4B4B4B),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'l' to Color(0xFF96DC37),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawTyphon() {
    val matrix = arrayOf(
        "...................................................", // 0
        ".......................D...........................", // 1
        ".......................D...........................", // 2
        "...............I.......D...........................", // 3
        ".................FFE.m.D...........................", // 4
        "...............I....E.DDD..........................", // 5
        "...............DQ.....DrD..........................", // 6
        "..............DMQ.M...DrDD.........................", // 7
        "..............DD.MD...DSDDIQ.Q.....................", // 8
        "..............DDDD..D.DSDD.E.......................", // 9
        "...............PDD..DNDSMD....F....................", // 10
        "...............2D..D.DDSIrD........................", // 11
        "...............PD..DD.DIIDI.KDD....................", // 12
        "..............D.D.....DDDKMDDIrD...................", // 13
        "..............KID......DMrPrIQSMMD.................", // 14
        "..............KMMIDDD...DDPSSQPISMD......EQ........", // 15
        "...............KDDDD...DIISQQQpSSMN.....Q..........", // 16
        "...............NKDDDDIDDDDISQPPQrrID....E........D.", // 17
        "..............DPKDDMSMDMIrISQPP2ISSDMDKD.......BD..", // 18
        ".............DDSKMMMIMMISIqqQPpQPQqSMMIDKNNKDDMK...", // 19
        ".............DDDKMMQSIMDISqqPPPQSQppIISIrDDIIMD....", // 20
        ".............NDDKKDIPSIMDIIpSSpQIQQpSPSPPISqID.....", // 21
        ".............KKKKKDSPPSMMIqqpppQppSSQPP2IIrIDKDD...", // 22
        ".............KKDMKKrMIrDMSqqIpqSPPppQSQQIIDNDDD....", // 23
        "...........DNDmIDSSDDIINDMIMMIpqpPPpSISIpDDTT......", // 24
        ".............DDDKDDSSDMDKNSDKDMSiQlSISSpSDDTD......", // 25
        "..........DKDDDKDMDKKDDMKNrMDSDIqPPIIpSIrDST.......", // 26
        "..........DKKDDKMDNSDKDMKNDIISSDSSIPISSDKDDD.......", // 27
        "..........DKKDDKDKKKDNIDKNDSSINKIpIIISMDKNI........", // 28
        "..........DDKIDKKKMDDDIDKKPSiSMKMSSIIIDSKD.Eq......", // 29
        ".......0..KMDISDDIIrDIrKKDDIqqIMIDlrSDKDDDQF.......", // 30
        ".......M..DMDISrIISQSIDKKKDQQqSrPSPIpDDMDNP........", // 31
        ".......ID.rNDDIIIIISQQrNDKDDrSIMSpIIpMrSINP........", // 32
        ".......IKKIMrDISrMSrISSDKDKNPQqISppSIIISrD.m.......", // 33
        ".......MDMIrMIIMDMSrSIMDDKDDASQSQqqPIIMDDP.........", // 34
        ".......DDMIIIIrDMSQ2QSDDKKKK2QSpPqiqprNDPE.........", // 35
        "........DDrIpSIMDrrS2QDDDDKL1PQQqiqSSSNIQQE........", // 36
        ".......I.QQSSSpIDDISIID1DKDDNPQQQSQSP1SSq..........", // 37
        "......mIMDISIpqpMMIIpIDDKKKKDPESQQQPDDS............", // 38
        ".......MNDDSSpSSISQQSSSDDDKKLDQDDP2LKND.........qE.", // 39
        "....M..DKDSQQpQSIISQQSPLDKBDKKLDDDLDKKKD..N...mQ.FQ", // 40
        "....D.QNDSQQQpSQIIISISDDDKKKKDDKNNDKKNMK.N.D...EE..", // 41
        "....KIqDSQ33QSpQpSSSqSDDDNKBKKKKKKKKMDDDKDDrDmD....", // 42
        ".....DSrQ33QQSqQSISSQQD1DDBKKKKKKKNDIMIMDDDIDDKD...", // 43
        "......rSQ44QQpqQSMIQQTIDKKKDNKKKKKDSSISIDNDISSM....", // 44
        "......ISQ44QQQQQSIMQPQSrDKDDKKDKKKDQISIIrDIMMDDDD..", // 45
        "........QQQQQQSqqSIQrQQSD11KKKDKKDSSMMKNDDMDNDD....", // 46
        "......m.pSQQSQSqISSSSPQQDlDKKKLDNDIDMM....KMKKN....", // 47
        ".......QQISQQSQQSSSSqQ3QPlDDKD1DDPDKKD....DKDrN....", // 48
        "........QIS3QQpQQSqISSQQQPDIMM11DMNDD....KDSSDD....", // 49
        ".......DIMPQQPSSQSSSSMQEEQSQQIPPID.......KKDDD.....", // 50
        ".......DMIQPIrIpQQSSSMrEFQQQQ2Q.N........DKDD......", // 51
        ".....DDDQQPDDPPIQ3PSqIMrQQQSSQDD...................", // 52
        "......DSQDD1KDDDSQQSqSIIISIrDDD....................", // 53
        "....NDDrDDDPDrDDSqSrIqIMIIDrD......................", // 54
        ".....KDKDDArSrPSSIIIMIIMMISDD......................", // 55
        "D...KKNK1DPrQQQqQrMMMMMMDMDD.......................", // 56
        "D.DDKDDDPMQQQETSrMMDBDDKKDD........................", // 57
        ".NDPDPPDIIIQQFFSMMD................................", // 58
        "DDDSrSSSSQPQQ4QSIMK................................", // 59
        "KDISQQSSQQ3Q3QSIMK.................................", // 60
        ".DDDDSQP3QQ3QSMDD..................................", // 61
        "....KDIPPDSSrDD....................................", // 62
        "......NN..NND......................................" // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'L' to Color(0xFF50BE28),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'i' to Color(0xFFD2B4F0),
        'l' to Color(0xFF96DC37),
        'm' to Color(0xFFA578D7),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawAirForce() {
    val matrix = arrayOf(
        ".........................................D......................", // 0
        ".........................................D......................", // 1
        "............................K...........DDK.....................", // 2
        "...........................DK...........DDDD....................", // 3
        ".........................EQNKN..D........DD.....................", // 4
        "..........................NKKKD.D........DDD....................", // 5
        ".........................DKKDKD.D........DDD....................", // 6
        ".........................KNNDDD..D........DD....................", // 7
        ".........................KKKGKK..D........DDD...................", // 8
        ".........................KKKDAD...........DDD...................", // 9
        ".......................KKDKDDKK..DD.DKDK..DDDN..................", // 10
        "..D....................KKDDSSD...DKKDKDD..DDDD..................", // 11
        ".D.D..D...............DKSDKDD.DDDDKDKKK....DDD..................", // 12
        "..D.....D.............DTDDGKDDKGDKDDKKKD..DDDDD.................", // 13
        "...DDDDDD.............DDNDDDDDDDKKKDKKKD.DDKDSD.................", // 14
        "....DDDDDDDD..D.......DKKDSDKDDDDKKKKKKK.DDKKSD.................", // 15
        "......DDDDDDDD..D.D.DDDDKDDKKKDGKKKKKKKKKDDDKSK.................", // 16
        "......DDNDSSDDD.DD..DDDDGDDDKKKKKDDDDDDDDDDDSSND................", // 17
        ".......DDDDDDDDDDD.....DDDDDKKDKKDDDDSSSDSDKDDDND...............", // 18
        "......DDDKWWSSNDDDDDDD.DDSSDKKDDDDDDDSSSDDSDDNNKG...............", // 19
        "......DDDKWWWSDSDNDDDDD.......DDDDDDDSSSDDSSDDDKKK..............", // 20
        "......DDDKWWSKDSKKDKDDDDDDD......DDDSSSSDDDSWFDDKDK.............", // 21
        "......KTWKTSDDDDDKDDDDrDDDD........DDDDDSSDSTSSSDDDD............", // 22
        ".........DKDDDSSDDrrrrrDKKDDD.D..DDDDDDDTSSDSDSDDDGKD...........", // 23
        "......DDKKDDDDSSDKNKNKKKKKKKKDDDDDDDDDDKSSSTSDDDDDKNDK...D.K....", // 24
        ".......DDDSDSKDDKKKKKKKKKKKDKDDNDDKDDNKKSSSSSSSDDDDKNKKK...DD.D.", // 25
        ".........DDKSKDDKKKKKKKKKDDDKDDKGDNKKDDDSDTSSSSDDKKDDNKK.......D", // 26
        "..........DKSKDKKKKKKKKKKSDGDKDSDDDKKKDTSSDSSDDDDDDSDKKK......KD", // 27
        ".........DDDDKDKKKKKKDKDKDDDDDDSSDDDDDDSWTSDSSDDDKDSDKKK...DDDG.", // 28
        ".........D.DKDDNNNNNNNNDDSSSSSSSSDDSSSSSTDDDSSDDGDSDDKK.......0D", // 29
        "...........KKKKNCAACCCADDDSSSSDKDDDSSSSSSSNSSDDKDKKKNKK.DKK.KDKD", // 30
        "...........DKDKKKNNNNNDSDDSSSSSKKDDDSSTDDDNTDDDKKKNNDKD..KDKKKDD", // 31
        "..........DKDKKKKKKNNDSSDDSSSSSKKKDDDSSDSDNPDSKKKKKKKK......DDK.", // 32
        ".........DDKDDKKKKKDSSSSDDSSDSDKKKNKDSSSTSADDDNKKKKKKDD0.0....K.", // 33
        "..........DKTSDKDDDSSSSDDDSSKKKKDDKNKSSSSDDSDDDDSNKKKDKKKKKKD.D.", // 34
        "..........DDSKKDSSDSTTSSDDSDKSDKDDKKKDDDDDDDDDDDNK..DDDKKKKKD...", // 35
        "..........DSDKDDDSDDSSSSSDDDDSKKDKKKKKKDDDDDKNNKK...............", // 36
        "............DKDDDDSSSSDDDDDDDDDNKDKKKKKKDDKKKKKS................", // 37
        "............DKDKDDSSDDDDGKKSDDASSSKKKKKKKKKKKKW.................", // 38
        ".............NKDDDDDDDKKKKKSDSSDSSKKKKKKKK...K..D...............", // 39
        "..............KKDDKKKKKKKKDSKTDDTDKKKKKDKD...KDD................", // 40
        "...............KKSSKKKKKKKDSKSPDSKKDKDGDD..K..DDD...............", // 41
        "..................DDKDDDKKKDKSPPDKDDDDGD...KD.DDD...............", // 42
        "...............DKDKDSDDKDKDDKAPNNrDDDDK.....DDDD................", // 43
        ".............DD.DDDDDSKKDDDDArNDDNNNGD......DDDD................", // 44
        ".............KDDDDDDSSKKDDDDrrDTFSDDN.......DSDD................", // 45
        "............DDD.D.DKSDKDGDDDrDSWWWSDD..........D................", // 46
        "..........DKDK.....DWSDDDDDrrDSWWWTSD........D..................", // 47
        ".........KDD.......DDDDDDNDDrADSFTDDD........D..................", // 48
        ".......DDD.........KDKDDDGDDrNrANrDNK...........................", // 49
        "......DD...........KDDKKKDDNAANNNNNKN........D..................", // 50
        "......................DDDDDDDNKDDKADN...........D...............", // 51
        ".....................DD..KKDNNKNDND...........D.D...............", // 52
        "...........................DKRDDD.............D.D...............", // 53
        "...........................DKNGD................D...............", // 54
        "............................K..................DD..............." // 55
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGuardian() {
    val matrix = arrayOf(
        "..............................DD................................", // 0
        "............................DDDDDDDD............................", // 1
        ".........................D.....DDDDNNNNDDDD.....................", // 2
        ".......................D.........DSSDDDDDS.DNKKKD...............", // 3
        ".....................D.....D...PPPSDDSSSD..KDPSDNKD.............", // 4
        "..........................NKKKP....DDNSS.PNKSTSDDDD.............", // 5
        "................DD.DDD...NKKKKN...ANNNDDDDNNPSSKKKKNNDDDDD......", // 6
        "............DDD.....DD..DDSDNNKN..PSSDNDNNNKNANKKKDDKDDDDD......", // 7
        "...........DDD..D...D...DSTSDNNK..PDDNNNNNNKKNDSSDDD............", // 8
        "..........DNNDD.DDDDD..D.....DNKD..DNNNNNSSDKNDSSDKD............", // 9
        "..........NNNS..NNNN...DNKN..DNKD...PANNDSSSKKNKKKKD............", // 10
        ".........DKKD.NDDDND...NKKKD..NDNNKKKKKKDSDDKKKKKKKKK...........", // 11
        ".........DKKD.DSSSDD...KNKNKKK...DDKKKKKKNNNNKKKKKKD............", // 12
        ".........DKKD.DSSSDKKNNNSKDDKD....DNKKKKKKKKNKKKKKKKD.DDD.......", // 13
        ".........DKKD.DSSDNKKKKNSDSDNN....DDNNKKKKKKKKKKKKKKDKKKKND.KD..", // 14
        "..........NKND...DNKKKKDSDSSSD.....DNNKKNKKKKKKKKKKNKKKKKNNNKK..", // 15
        "............DKKKKKKKKKNNNNNNSNNDDDDNNKKNNNNKKKKKNNNNSDKKKNNNKD..", // 16
        "..DDDDDKD....DKKKKDNNNNKKNKKNDKNNNNNKKKNNNNNDNKDPPDNNSKKKNDKKD..", // 17
        "..NDDNKNANKDD.DNNDDDNNDDDDDKKNNKKKKKKKKKKNNDSSSSSDKKNSKKKNKKKD..", // 18
        ".DDSDKNPSSSS...PNDDNDDDDDDKKKKKKKKKNDDNKKKNNSTTTDKKKNDKKKKKKNDD.", // 19
        "D.DSDNDSTTT....DNSDDDDSDSDKDSSSSSNNSSNKKKKNKNNDDKKNDNNKKKKKDDKN.", // 20
        "....NNASDD....PNSSSSSSSSSDDDDDDNNKNSDKDSDKKKDKKD................", // 21
        "....NANKKKKDDDNNSSDSSDSSSSSSSDNDDDNPNKKKKKKKKKD.................", // 22
        "....NNKKKKNNDDSSSDTTSDDTTTTTSSSSSSDAANNKNNKKKK..................", // 23
        "DD.DKKDSDDDSSSSDNNSSDNDTSDDDSSSTTSDADSSDDNKDDN..................", // 24
        ".DNNKNSSKDDDKKKDDDSSDNNSNKNNDDDKKNSSNKKNNNKNK...................", // 25
        "..NKKKNNNSSDNDDSDSSSDNDSKNDDSTTSKNSSKKKKKNKKD...................", // 26
        ".....NDDNSDNDSNDNNDDNNNNKKKKKKDNKNADKDSDDKK.....................", // 27
        ".....DSDDDNDSNKKKKDDDNNNNNKKKKKKKKNNNNNKKKKD....................", // 28
        ".....DDDDD.DNN.DKKDSDNKKKKKKKKKKKKKKKKKKKKKD....................", // 29
        ".................DNNNKKKKKKKKKKKKKKKKKKKKKKKNND.DD..............", // 30
        "......................KKKKKKKKKKKKKKKKKKKKKKNDDDDKKN............", // 31
        "......................KKKKKKKKKKKKKKKKKKKKKKKKKKNDSDDD..........", // 32
        "...................DKKKKKKKKKKKKKKKKKKKKKKKKDKKKKKDDSDK.........", // 33
        "..................KKKKKKKKKKKKKKKKKKKKKKKKKKKKKDDNDKKSDN........", // 34
        "..................KKKKKKNNKKKKKKKKKKKKKKKKKKKKKKKKDNKKNKDD......", // 35
        "..................DKKNDNNDDNNKKNKDKKKKKKKKKDDDDDDDDD............", // 36
        "...................KKDDDKNDDNNKKDDSKKKKKD.......................", // 37
        "....................KKDDNNDDNNDDSNDK............................", // 38
        "....................DNDKKKDSNDDNNDD.............................", // 39
        ".....................DDDNKNSDDDDKD..............................", // 40
        "......................DKKKKDSDSSKKD.............................", // 41
        "..........................DD....D..............................." // 42
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'D' to Color(0xFF4B4B4B),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawUltimaWeapon() {
    val matrix = arrayOf(
        "................DDDNDDDD........................................", // 0
        "..............KD..DTSTS.........................................", // 1
        "............ND..................................................", // 2
        "..........D......D..DNNDD.D.....................................", // 3
        "........D.DD...DDDNKKNNKKDD..............D......................", // 4
        ".......DD.D....NKNKKKKKNKKDDD.............K.....................", // 5
        "......DD....DDKKKK......KKNDD.............N.....................", // 6
        "......KDDD.DKKKK.........DKND.............D.....................", // 7
        ".....DDND.NKKKD...........KKKD..D......D..N.....................", // 8
        ".....KDDDKKKKD.............KKKND..DDDDD.DK......................", // 9
        ".....NNDKKKNK...............KKKKNNNKNNKK........................", // 10
        "....KNNKKKND..................DKKKKKD...........................", // 11
        "...DDDKKKNDK....................................................", // 12
        "...NDNKNDDDD....................................................", // 13
        "..KDSNNDDSN.....................................................", // 14
        "..NDDDDDDSK.....................................................", // 15
        ".KDDDDSSDDD.....................................................", // 16
        ".KDDDSSSSK.......................N..............................", // 17
        ".NDDDSDSDK.......................N..............................", // 18
        ".NDDSDDDDK.......................N..............................", // 19
        ".DSDSDSSNK......D.......A........A..............................", // 20
        "KDSSDSDDKN.............DNP.......P..............................", // 21
        "KSTSTSDDKK............0NKN.......A...........K..D...............", // 22
        "KSSDSSSDKN.......Q....0NND.......NN..........N.D................", // 23
        "KSSSSDDDKND......E......NK0D....KNN..........N0K................", // 24
        "KSSDSDDDKDK.....DQ......NNKAKNAAKNPP.........Kd...N.............", // 25
        "KDDSDDSDNDD.....NP..Q..D1P1NANNNQANNNND......D0...N.............", // 26
        "KDTSSSDDNNDN....DN.D1DAPKDNSSAAAKKKASND......D.D..N.............", // 27
        "KKSDDDDDDKNND...NPDNNNNNAKKNANNPNKNPDDA00..D.D.K..N.............", // 28
        "KKSDDNNNSDNNNK.DNAKNNAQAANNANKDKNDNKAEAD..DAAN.0D.A.D.D0KK......", // 29
        "KKDNDDDDDDNNNKKNNAN1NK1NNNANNNNKDSSKANNANDDNKKN.00.DDDD.........", // 30
        "DKNDDNDDDNDKNNDNNAANNANNKNNNKNPDNDAADNANPPNAKKADBBBDSUD.........", // 31
        ".KKDDDDDNDNKNANPNNNA1NNQNNKDNANNADDDDKNBDAANKNQTSKNDDT..........", // 32
        ".KNKDNDNDDNNKNA1KAPNKNNNKDSDDP1KNSSDNAAPAA1KDCQDTSNBD.0.........", // 33
        ".DDKNDDNDDNDNK1KNAP1NNNKNKDNNKKDSTDN1AQNNDPKDNAN0SD0D0KK........", // 34
        "..NNKNDDDDDNKKN0DNANANKKKDSDDKNSSDKDNQKN1DNNPPNADKSSDDK00.......", // 35
        "..DKNKNDDDNKDDNDNNKKNDKKNDDSTSDDSNKDDSNDNANNKKNPND0KST0DDK...D..", // 36
        "...KDNKKKKKDSSNANKKNDNKNKNSSTSDKSDNKDDDSSNANSSNAN0D00KD0DK.DD...", // 37
        "....KNDKKKKNDSNKDKDNDKDNKNNDSTSDNKDSDSDNDD1PNDDNADDB0KNK0.0D....", // 38
        "....DKNKKDDNSDKDDKNKNKNDNDNNTT.SDKNSTSDDNNNNNNNNKNBKDDNNKKD.....", // 39
        "......KKDDDDDDDSNKKKKKNSD.DKDDSSSSKKDSDNKKKNAAAPNAKKNKKN1.......", // 40
        ".......KKKDDTSDNKKDKDSDDSTDKKNSSDDDDKDDDSDKAPKNAAANNKDDSKP......", // 41
        "......NDKKDSSDNKKKKKTSDDSSDKKKSSSDKKNDNKDKNNNNANANNDNNDN1N......", // 42
        "......DSNNDDDDKKNKNSSTNDDDKNKKDSTSSKKKNDDDDKKKNANPKNSDSSN1......", // 43
        "......NSNDNDNKKKDSDSDSDNKKDDKNNSTSDDKKNKKKKKDTDNNKDKNDDDNK......", // 44
        "......DDKDDNKNNK..NDDDDDKNNKKKDSSSDKKKNKKNDT0dTSKDDDDKDDNNK...D.", // 45
        "......DNKDDNDNKD..KDNSDDKDNKKKDDDDDKDSNKDNK.T0SS0KDDS0NDNDNBD...", // 46
        "....DKNNNSDDDND...SDNSSKKDDKDKKDDNKDSDKNDDD.T..SSTDNDd0NADD.....", // 47
        "...DDKKDDSSSDKT...SNDDTNNSSKDSDKKKKDNKNKNDDS...TSSNDKSTDKN......", // 48
        "..DDKKDDSSTDKT....SKDDSNDSNDKNKKNKKKKNSDNDDS....T0DDNDSDDD......", // 49
        ".KDDKKNDDSSD......KKNTDKDDD.TSDKD.DNKDDDKKNS.....SSDNSTSD.......", // 50
        ".KDKKDDNNDNT.....DDKNSDKDSDDT......DDKDNKNKD...SSNDSSTV.........", // 51
        "..DDK..KKKT......DNKNDDNDDSSNS.....SDNDSDNDDDDKDDDDND0..........", // 52
        "..DDNT..ST........NNKDDDSDTTSSSDT...DDDDSNNDDSDNKNDKDD..........", // 53
        ".K.NDNDT..........DKKDDDDDDNDDDSDT..TKDSDDKDDDND.TS.KND.........", // 54
        "KK.DDDKDS..SSSSSKKKKKDDNKNKSSSSDDDS...SDNDDDDKD.....DKN.........", // 55
        ".K.KKDDKDKKKKKKKKKKKKDDDD......DNNDDS....NDNDNNT...TDSD.........", // 56
        "..DD..B..D......KKKKKDDDDKDST...SDDNDD...TKDKDDDD..ST.K.........", // 57
        "..D...D.............KKDSTDDDDSSDKDKSKKKKKS..DKSDDKKK............", // 58
        ".....................DDDDNKNKDNDKKKDKD.........DDK..............", // 59
        ".......................KK.DSKDDDKKKD.............D..............", // 60
        "...........................0DD0DK.D.............................", // 61
        "..........................DD..D.................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'd' to Color(0xFF5AD2CD)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawKefka() {
    val matrix = arrayOf(
        "............................................i...................", // 0
        "....................................P....ppq....................", // 1
        ".....................................P...rr.q...................", // 2
        "..........mm.........................P....rrp...................", // 3
        "............m..mmmm..................P.....r....................", // 4
        ".............m......m.................PP..........D.............", // 5
        ".............m..mm......m.............DPP......DDDDD............", // 6
        "................m........m.mmm.i.......DPPDD...NDDPAPDD.........", // 7
        "......................I.m......mm.......DAPPP..DDDPDDPP.........", // 8
        "......................m....DD..mm.........NDDD.NDDDrSSD.........", // 9
        ".....................DDD.m.DDI....m.........DNKKDNDDSi..........", // 10
        "....................DDDKKDImSD..m.m.....DDD.DKKNDNDSD...........", // 11
        "...................D..DDDITTmm....mm..DNDD.DNDDDNDSS............", // 12
        ".....................I.DDDDDSIDD......KDD..mSDDDDDDD............", // 13
        "...................DDmIDDNKDSTm.DDm..DDKDD.DSSSSTDS.............", // 14
        "...................DSSDDDDDDDDD.DDDDDKDDD.DNSSSmSS..............", // 15
        "................DDDDDDDDSSDDND.m..DSNKKKDDKDSTmDDDm.............", // 16
        "................DDDDSDITmSSDDDm....DKKKDDDDKDSTSDDDD............", // 17
        "...............ImSDmSSSmSTISDDDDDm.KNDDDDSmSSDDDDSDK............", // 18
        "................DKSTSSmSmSDSDDDNDDKKDSSDDDSSSSSDDSDD............", // 19
        "............mm.DDDSSSSTITSISDDDDDDNKKNDNDDDIDDSTIDN.............", // 20
        "............m.IDDDDSISTSTTSDDDSSISDNNKKKKKKKKKDiSSKD............", // 21
        ".........mm.ID...DDDNSTSSmSDSSSSDDDNNNKKKKKKKNDSSDK.D...........", // 22
        "........m.ID.....DDDDDTmSKKD....TSDDKKDKKKKNDNDDIDDD............", // 23
        ".......m.D......DIDDDDSmSKKN.....TNDKKDNKKKKDDDSDDKDDD..........", // 24
        "......m.I.....I....DDKDSDKKKS...TDDDKKDDKKKKDDSTSKKDSD..........", // 25
        "......D.m...I.D.I...DKKNKKKKD..SKDNDKKDNKKKKDDmTSKKKDD..........", // 26
        ".....DD.I....I..m...KKKKKKKKKDSDNDDNKKNDNKKKDDDNNNNDDDD.........", // 27
        "....DDDDD.....m...DKKKKKKKKKKNDDDDDNSKDIDKKKKKKDDNDSrD..........", // 28
        "...DNDDD.D.......DKKKKKKKKKKDKNKDrKKDKDDDDKKKDDDDNrSDND.D.......", // 29
        "..DNNDDDI........KKKKKKKKKKKDDKKNDDNDKKDDDNNDrrDKrSrNK.DD.......", // 30
        ".DDNDD..........KKKKKKKDKNKKDDKD.DrrrNKNDDDrPSDNDrrrNKD.D.......", // 31
        ".DKD...........DKKKKKDKDKDKKKDD...DprNNDrrSrDNKDrrSNKKDDDD......", // 32
        "IDD............DKKKNKDKDKDDKKDD...DNDNDrPSDDrDDDDDKNDKDSDD......", // 33
        "DD.............KDKNNKDKDKDDDDDD....KKKDrDDDDSSrSDDNDND...D...DD.", // 34
        ".D............DKDKDKNDKDKND........NKKDDNNNrrDDDKNrDDD..DDKDDD..", // 35
        "..............KDDKDK...............NKKNDrDrDDDSTDrrrrND..NDSDD..", // 36
        ".............DNINKD................DNDKDDDKKDDSmDDrDDKD..DSSD...", // 37
        ".............KDSD.D................rDDKDNDKNDDDmSNrNKKD...DD....", // 38
        ".............KI...................rDDKKKNDKNDSDSSrDKDDD.........", // 39
        "............DKD...................DNKKNDDKNDDDNDTrNNSSN.........", // 40
        "............DK....................KNDDrrNKKDDKKDTDKDDmD.........", // 41
        "............D.....................DDDrrNKKKDDKKSiDKDSS..........", // 42
        "................................DDDDrrNKKDDDDKDSmDKDSD..........", // 43
        "...............................DDNNDDNKDSSDNDKDDDKDDDD..........", // 44
        "..............................DKDKDDNKNSmDDKDDDNDNDKDD..........", // 45
        ".............................DKKNDDNKKDSDNDSSSNDrDKKD...........", // 46
        "..............................DKKKKKKKDDNDSTSDDrKKNDND..........", // 47
        "...............................DDDKKKNDNDSTTDrrDKNDD............", // 48
        "...............................DKKKKNDDDSmTDrPrrD...............", // 49
        "..............................DKKKKKDDDSSDDDrrrD................", // 50
        "..............................DNKKKDDKDDDKNDDDD.................", // 51
        "...............................NKKNDKKNKKKDDNND.................", // 52
        "..............................DKKKDNKKKKKDDKKD..................", // 53
        "..............................NKKDDKKKKKDDKK....................", // 54
        "..............................KKKDDKKKNrrND.....................", // 55
        "..............................DDDDNKKDDrD.......................", // 56
        "..............................DDSDKKNKKD........................", // 57
        "...............................DDDKKKKKD........................", // 58
        "...............................DDKKKKKN.........................", // 59
        "............................D.DD...N............................", // 60
        "...........................DDDND................................", // 61
        "...........................DDDD.................................", // 62
        "...........................DD..................................." // 63
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'D' to Color(0xFF4B4B4B),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawGuardScorpion() {
    val matrix = arrayOf(
        "......................................D.........................", // 0
        ".......................................D........................", // 1
        "......................................DA1.......................", // 2
        "......................................ARCR......................", // 3
        "....................................DDNAPP.D....................", // 4
        "..............DP..CPC...........DD.DDDANAAADDD..................", // 5
        "............NNRCNCCNNNR..........DNNNNAAAANAPAA.................", // 6
        "...........DANAAANANCCPP.........DDNNKNNCAACQPCA................", // 7
        ".....m..DCNNRANNAKCRAOPA........DDDNAAANAS1AOPCN................", // 8
        "....D.DKNNNKNKDDD.NNNNKK...RRA...DNNKAOCNNTARCOCAARAAR..........", // 9
        "..DD..DNNNNKN.....DNNNNA.DRAPAAA..NNDKRANKN1NRCCNAACCCCC........", // 10
        "D....DDNNKK.........KNAAN.NADDSPAACCPARNKKK1ANNSDNRRRCCCCPPQ....", // 11
        ".DDDDDDKDDD........DNDAARDRRDDDDNNRAACOOCANNNDSSDNRRRRRRRRCQA...", // 12
        "....KDDD.D.........NAANSRRNRNNNNNRRANNNACOCANDKNNNNAAARRRRRSDD..", // 13
        ".....DKKK..........NRCNASPKRRRNNRRASSNDRNRCECKKKKKNNNRCCRRADS..D", // 14
        "....................NRAAASNNNNRRRRRNDSARRNRADKKSWWSDNNNNAAANDDDD", // 15
        ".....................NNRACNNNKKNRRRRRRRRRRCNDSSDSWWWTTTDNKKND...", // 16
        "....................NKKNKKNKKKNKKKNRRRRRRRCKKDDDSWWNNNNTWKKSD...", // 17
        "..................DDDDDKNKNNKKNNNKKKKNRRRRRNKKDDSTDSSSSSTTTD....", // 18
        ".................K.....KNDDDSDKKKKNNNKKKNNNKKASTTAADDDDDKS......", // 19
        "................DKD...DNSSTTTDNNKKKKNNNAKKKDSNAND12NKDDDK1......", // 20
        "................1NDDDKKKDDSDDKNANNKKKKNNDSNDDDCAPC1AKDDKA1......", // 21
        "...............N21KKSNKNKKDKSDK1C1KKKKKDDDKDDDC1DPANKKKN1A.PP...", // 22
        "..............KA1ENKKN121AKKDDK111KDKKDDN1111NANDNNADDKNAAN.CPP.", // 23
        "...............AA1AKK121Q1KKKKK1ANKKKKKKN121EAAR1NND..KNNNNDAPC.", // 24
        "...........PCP.KAANKSNA111NKK.NNNNKKKKKKA121QAAQA.......KDDDNACP", // 25
        "..........ACQPRKKNSWNA1AAAN....DNKKKKD.KNNAAANAP1........DDKNAAN", // 26
        ".........NACCCNKSTTPNKDAAN.......DKKK..KNNNNNNAD..........DKNARN", // 27
        ".........KRACRNDWWROOCRKN........DKNK...KKBDKNN...........DKNRNK", // 28
        ".........DNRARNTTNCQQPAND.........KK....NARADA............DKNNN.", // 29
        "..........NRRRRSDRCPPCRN...........K....AOOEQC.............KNND.", // 30
        "...........NRRRDDRRCCCRN...............NRRRCOC.............KNK..", // 31
        "...........NNRND.RRCCRRN...............KRRCCOC.............KN...", // 32
        "............KNND.KRRRRRN...............KRRCCOA.............ND...", // 33
        "............DNND..DNRRNN...............KNRCCCA.............N....", // 34
        ".............NND...DNNND................NRACA...................", // 35
        "..............KN...DNNND................KNRRRD..................", // 36
        "...............D....DNND................DNNRN...................", // 37
        ".....................DND.................NNND...................", // 38
        "......................DN.................DNN....................", // 39
        ".......................D.................DND....................", // 40
        ".........................................DK....................." // 41
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawAirbuster() {
    val matrix = arrayOf(
        ".................................................", // 0
        ".................................................", // 1
        ".......................3333......................", // 2
        "......................3xEDnE.....................", // 3
        "......................3EAAnOE....................", // 4
        "......................3EAAnOOE...................", // .
        "......................3DAAEOOD...................", // 6
        "......................EAAADEOD...................", // 7
        "......................EAAADEOD...................", // 8
        "......................EAAADEOO4.3DD1.............", // 9
        "......................DDDDDOOO3.DFFD.............", // 10
        "......................22DDAAOO3.DFFE3............", // 11
        "..........3AA2..........1OOEEO3.DnoE3aag3........", // 12
        "..........1AAA......aa132FFFEO363EoEDaa1111......", // 13
        "..........DExD......aaaaa2221D263EFEDaaaaaaa3....", // 14
        "........43DFnA.....41a211bbb2a13.DED12a22baag2...", // 1.
        ".......112AFnA.....31E211bKb211g22Dg1abbbba111...", // 16
        "......3111AnEA.....3OZO11aKba1133A11aaa2bbC1113..", // 17
        "......gC20ADEA221122122a1a211124x1a1baaab2C1113..", // 18
        ".....31a2A1DD143aaag11a1gggaaa34D1a12aaaa2C111g..", // 19
        ".....11a1122124a22a1gg1g11aa2144R1a112aaaaa11113.", // 20
        ".....g1211aa1331aaa2222222aaa133R1aa12a111a11111.", // 21
        "....411a11aa13311ab222b2222a1g33RA1ag12111aa1111.", // 22
        "....211ag1aag33ga2aa222ab222ag33RA111g2a111a1111.", // 23
        "....211ag111g311a211121A1a222123RRg111aa1111aaa1.", // 24
        "....211aA111g2ARgaDOAaAOR1222a12DRA2331211agggg2.", // 2.
        ".....g1aA321gAOOgAOO1aDSR122a112DRR13311aa1ggg2..", // 26
        ".....3gaA2332AORgASOgaDOR12a11112DD2211gagggg2...", // 27
        "......313A122AORgASR1aDOR12a1111g2.621111gA24....", // 28
        "........41111AOR1AORAaROR12a1111g3433443aa1......", // 29
        ".......322211ARRg1RRAaARR12111a2a234.466311......", // 30
        ".....23466.2g1RA11RR11gRA12111343.23.66.311......", // 31
        "....24.666441122ggA2a1AD2111a3v313..2..4314......", // 32
        "...2.34664431a11a132aa13aaa2a332..4.A44332.......", // 33
        "..4a2.3.44321aa22aa222aaaaaC14423334A34222.......", // 34
        "..223331432211CCaaaaaaaCCC1C1332..44A2332........", // 3.
        ".3.433212222211aaaaaaaaa111C132433331221.........", // 36
        ".3332331a23.3111aaaaaaaCCCCC143243332111.........", // 37
        "..243A321...31a2AAAAAA1111111124.4234a31.........", // 38
        "..3342133....3..AAJJJJDDAAAAAAA133...a21.........", // 39
        "...233124.......41DJJJJJDAAAAg24.....121.........", // 40
        "....224..........4AAAAAAAAA011ggA3...121.........", // 41
        "...................11g11112111g121a..a21.........", // 42
        "...................1111111a111gg1a13.a21.........", // 43
        "...................11111111111444111.221.........", // 44
        "...................3111112111g113.113423.........", // 4.
        "...................12222221111111131a433.........", // 46
        ".................311222222112211111g123a.........", // 47
        "...............411g1111231g1a11a22a1g111.........", // 48
        ".............11ga112A1A1111a2222222aag11.........", // 49
        "............222111212111223222222122a11g3........", // .0
        ".........42ga21a12112121223222212A1222a1g2.......", // .1
        ".........11111a112A21222233222212A12222a1a.......", // .2
        ".........g21aaa1211211122332222223312222113......", // .3
        ".........12a1a22221312222332222212a12221111......", // .4
        ".........112211a11222322233222222211111222A......", // ..
        ".........g1a222222a11112222222111a22221111A......", // .6
        ".........31a222222222322221112222221111111A......", // .7
        "..........112222222222222222222222a1111111A......", // .8
        "..........412222222222222222222222a111111g4......", // .9
        "...........3111aa2222222222222222a111g1aa4.......", // 60
        ".............442g111a2222222222211A0244..........", // 61
        "...................43331111a11g133...............", // 62
        "..........................4223..................." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF0C0C10),
        '1' to Color(0xFF262630),
        '2' to Color(0xFF484855),
        '3' to Color(0xFF737684),
        '4' to Color(0xFFA2A6B4),
        '5' to Color(0xFFD0D4E0),
        '6' to Color(0xFFFAFAFF),
        'A' to Color(0xFF301C12),
        'C' to Color(0xFF0F3444),
        'D' to Color(0xFF58301C),
        'E' to Color(0xFF844E2D),
        'F' to Color(0xFFB27648),
        'J' to Color(0xFF70520C),
        'K' to Color(0xFF4E8080),
        'O' to Color(0xFF762A0C),
        'R' to Color(0xFF5C0F16),
        'S' to Color(0xFF941C26),
        'Z' to Color(0xFFD02A30),
        'a' to Color(0xFF204444),
        'b' to Color(0xFF346262),
        'g' to Color(0xFF122626),
        'n' to Color(0xFFB24816),
        'o' to Color(0xFFE47020),
        'v' to Color(0xFF76A8A8),
        'x' to Color(0xFF8E4E3E)
    )

    drawMonsterMatrix(flip(matrix), palette)
}

fun DrawScope.drawRufus() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        "................................................................", // 2
        "................................................................", // 3
        "................................................................", // 4
        "................................................................", // 5
        "................................................................", // 6
        "............................KKKKKKKKKKK.........................", // 7
        "............................KKKKKKKKKKKD........................", // 8
        "..........................DDAAACPQQQQPADK.......................", // 9
        ".........................KKA111PQ4FFFQ1AKK......................", // 10
        "......................KKKNAPQQEE4QQQEQQPAAK.....................", // 11
        ".....................KKKKA1QQEFFFQ1QQQQQ1AKK....................", // 12
        "....................D..AAPQPPQQEFEEQPPPPQPAN....................", // 13
        "...................KKEFP1QQP111QFFFQ111PQQ1A....................", // 14
        "...................KKEFDNPPPQP1QEQQQEQ11P1NK....................", // 15
        "...................KKEFDKA1QQP1PQP1EFQ111AKK....................", // 16
        "...................KKQENKPQQQQPQECNDSP1ANNKK....................", // 17
        "...................KKPQNKSEEQQQEFDKKKN1AKKKK....................", // 18
        ".....................DANNQEPAAAQFPNDPDNNNNKK....................", // 19
        ".....................KKA1EEDKKKSFQ1QESKN1AKK....................", // 20
        ".......................NNQESBDSSEPNPEDKKND......................", // 21
        ".......................KKSESUTWFQNKAQAKKKK......................", // 22
        ".......................KKSESUmFEQNKNCDDKKD......................", // 23
        ".......................KKSESUSEQ1NKKKDTSKKKD....................", // 24
        ".......................KKSESSSEQ1NKKKDTSDDDD....................", // 25
        ".......................KKSEEEQQP1DDKKSWWWTT.KK..................", // 26
        ".......................KKPQEEQPCADDKKSWWWT..KK..................", // 27
        ".....................KKKKKKSEDKKDNKKKSWTT...KK..................", // 28
        ".....................DKKKKKSQDKKKDDDDSWT....DD..................", // 29
        "...................KK..KKDDKKNDNKSTTWTTDKD....KK................", // 30
        "...................NK..KKDDDKDDDKSSTTTSDKD....KK................", // 31
        ".................KKPQKKKKKKSWWWTTDKKKKKDT.....KK................", // 32
        ".................KKPQNKKKKKSWWWTSDKKKKKD......KK................", // 33
        ".................KKDDPQNKSTDKSWSKSFFFSKD......KK................", // 34
        ".................KKDDPQNKSTDKSWSKSEEESKD......KK................", // 35
        ".................KKKKPQNKKKKKDTDKSESDDKD....KK..................", // 36
        ".................KKKKPQNKKKKKDTDKSESDDKD....KK..................", // 37
        "...................KKKK..KKDSDKKKCQANKKKKKKD....................", // 38
        "...................KKKK..KKSTDKKKCQAKKKKKKKD....................", // 39
        ".........................KKSWSKSTDKKKKKDSSS.K...................", // 40
        ".........................KKSWSKSWDKKKKKDTT..KK..................", // 41
        ".........................DDDNDDDNSTTSSSDKKKKKK..................", // 42
        ".........................DDKKDDKKSWTTTTDKKKKKK..................", // 43
        "........................K..DKDSDKKKDDDDDDDKK....................", // 44
        ".......................KK..DKDTDKKKDDDDDDDKK....................", // 45
        ".......................KK..DKDTDKKKSTTSSSDKK....................", // 46
        ".......................KK..DKDTDKKKSWTTTTSKK....................", // 47
        ".........................DDKKKDKKDSDDSTTTSDDKK..................", // 48
        ".........................KKKKKKKKSTDKSWTTSDDKK..................", // 49
        "........................DNNNANNKKKDKKNDDDDKKKK..................", // 50
        ".......................KKNAACAANKKKKKKKKKKKKKK..................", // 51
        ".......................KKKKNNNKNNNNNNKKKNKKK....................", // 52
        ".......................KKKKKKKKKAACAANKKANKK....................", // 53
        "...............................KNNNNKKKKKKKK....................", // 54
        "................................KKKKKKKKKKKK....................", // 55
        "................................................................", // 56
        "................................................................", // 57
        "................................................................", // 58
        "................................................................", // 59
        "................................................................", // 60
        "................................................................", // 61
        "................................................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        '4' to Color(0xFFFFF56E),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'W' to Color(0xFFFFFFFF),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(flip(matrix), palette)
}

fun DrawScope.drawHojo() {
    val matrix = arrayOf(
        "................................", // 0
        "..............222224............", // 1
        "............411233322...........", // 2
        "...........41123333322..........", // 3
        "...........22221x23221..........", // 4
        "...........1a32xyw3231..........", // .
        "...........11223wwwwy1..........", // 6
        "..........3g222y33wy33..........", // 7
        ".........4112xx3343343222.......", // 8
        ".........22113xw3432430223......", // 9
        ".........21100FFwx3w1gg1333.....", // 10
        ".........213012FwwF232212a2.....", // 11
        "........2335211xw3xA4532132.....", // 12
        "......33554V4g11xwx122422321....", // 13
        ".....345VV45511gA00334a0g1g22...", // 14
        "....3555554552g0100533311g252...", // 15
        "....35VV454343000004243234552...", // 16
        "....35V5555444g10g043333245544..", // 17
        "...5245V545455111g035323244552..", // 18
        "...5355V53535V211g0352133345535.", // 19
        "...2455543544V211g0244233345534.", // 20
        "...255V52455353gg00122013334534.", // 21
        "..525454245535301E3332221244434.", // 22
        "..435432344444302222222.5v2333..", // 23
        "..345432234443212222212...555...", // 24
        "..355454322232122222212.........", // 25
        "..245V555551AyD1DD22132.........", // 26
        "..324555553Aywwy2A22232.........", // 27
        "...31234452AFwwF1D21242.........", // 28
        "....55222320D3FDA1A0242.........", // 29
        ".......411113g00110g2524........", // 30
        ".......v333444g111112534........", // 31
        ".......24455531211212544........", // 32
        "......52455VV3a21111g543........", // 33
        "......334555V3a110g10433........", // 34
        "......244555V3a10001g342........", // 35
        "......24455V532101121252........", // 36
        ".....3344VV5522g012212534.......", // 37
        ".....3445V5551200g2212534.......", // 38
        ".....a4455555110g112a1544.......", // 39
        ".....245VVV55111g1222g552.......", // 40
        "....5245VV5541g1g1a22g552.......", // 41
        "....3445VV5541g2g1122g453.......", // 42
        "....345555553112g1122g3535......", // 43
        "....245555553022g112203543......", // 44
        "....345555553022g111102543......", // 45
        "....345555552g22g111101543......", // 46
        "....1455555502.30111101543......", // 47
        ".....335555503..g11113243.......", // 48
        "......3123440...g111g522........", // 49
        "......311ggg1...g11a0...........", // 50
        "......31a1113...1112g...........", // 51
        "......022g11....211213..........", // 52
        "......g22111....212112..........", // 53
        "......121113....g11110..........", // 54
        ".....3110gg.....01122g..........", // 55
        ".....312a10.....00g0003.........", // 56
        ".....3100g1.....1ggg11ga........", // 57
        ".....301101.....20gg11121a......", // 58
        ".....a11111......gg2g11g1g......", // 59
        ".....0aa111..........3333.......", // 60
        ".....111113.....................", // 61
        "......2223......................", // 62
        "................................" // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF0C0C10),
        '1' to Color(0xFF262630),
        '2' to Color(0xFF484855),
        '3' to Color(0xFF737684),
        '4' to Color(0xFFA2A6B4),
        '5' to Color(0xFFD0D4E0),
        'A' to Color(0xFF301C12),
        'D' to Color(0xFF58301C),
        'E' to Color(0xFF844E2D),
        'F' to Color(0xFFB27648),
        'V' to Color(0xFFCDE4FF),
        'a' to Color(0xFF204444),
        'g' to Color(0xFF122626),
        'v' to Color(0xFF76A8A8),
        'w' to Color(0xFFDAA270),
        'x' to Color(0xFF8E4E3E),
        'y' to Color(0xFFC0765C)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawSephiroth() {
    val matrix = arrayOf(
        "................................................................", // 0
        "......................3222......................................", // 1
        ".............43.....3221334........4.................44.........", // 2
        "..............I3..4a2210............3.434...........44..........", // 3
        "..............3333a22g02........4..433434..........4.4..........", // 4
        "...............3211a10g3...4...34434343444v4.....4..44..........", // .
        "..............22112g0012...444343..v444bv44...343.4343..........", // .
        ".............4a11110001g..v43333.344.43v3v..4444v34.v...........", // 7
        "...........4g100110000014.3b323wv3b33v323b24.4434.443...........", // 8
        ".............21010000g01233332233423324334443433v443............", // 9
        "............3110200g1101a222322z3322134F33v983v34v3b............", // 10
        "...........41a11000010g011121g322b132.333339933v334.............", // 11
        "...........aa111000g1001g11a2b2x3323.3x223983993334.............", // 12
        "..........a1012111g11gg00012123y34243428229899v3b...............", // 13
        ".........22012211100g11g00gagwzz4z222331228993244...............", // 14
        "..........312b2g110000000021g2wyyx12233bQ23433..................", // 1.
        "..........g222211g0010g1gg0202zzzy23x311232344..................", // 1.
        ".........31a212g010011g001100AywwF227xx23123344v44..............", // 17
        ".........1112210111011g11aagg1ywyx23223322293v3334..............", // 18
        "........411112111111.343a1133Dx3x2x2.33332b433332...............", // 19
        "........311g0211011...v1113.133323213.2322b4.b4.................", // 20
        "........101101110g2..4a2g3422Eb3x323Q2232.384.4.................", // 21
        ".......423111001g33.4aaa18822D22Da332823233I8.44..4.............", // 22
        ".........211110113.49ag07872213223332QxyxbI883.444b.4...........", // 23
        ".........2ga1a0113.3810088Q2vgg02423IQ1218II334.4...4...........", // 24
        ".........10a12011.38888888143Q002U323271Q883382.U343............", // 2.
        ".........g311201..888888723.0QQQ3.3321278333II8v3434............", // 2.
        "..........3112ga..888887234v0II2..v3272222388823338.............", // 27
        "..........312213.b8888232vU37334V444a8Q323c888873.43............", // 28
        "..........v0aa1..88888433342Q3aV.44478a2432889882.43............", // 29
        "...........011a.488882483427223.4U4v7982W4.388988.Uv3...........", // 30
        "...........1112.29898b4234773114U44b8887I34.a888c4Uv3...........", // 31
        "............g1a38888333Q34Q031b4.4v79987233.432983.v3...........", // 32
        "............21agc382.332238g22.3.389998g833.3.38ab.433..........", // 33
        "............312C8.8..388a322133b4a8999878834v..238.4b3..........", // 34
        "............30114....388Q2.272Q2189ddd878II34....a.v8...........", // 3.
        "............3313....43II81.Q87Q2g7BBd987WIWI4.....433...........", // 3.
        "..............13....33II2..C87870gBBd991IWI844....vb............", // 37
        "..............33....2IIQ3..28771g0BBd99.2WWW43....3v............", // 38
        "..............3....43I22...4877300BK99C.3IWW344...34............", // 39
        "...................33I4.....782.g8e9982..333343.................", // 40
        "...................333......2834g8B988....323344................", // 41
        "..................33.........C3.289982......3133................", // 42
        ".............................4a..889a4.........33...............", // 43
        "..............................4.49Q8............................", // 44
        "................................3843............................", // 4.
        "................................84..............................", // 46
        "................................................................" // 47
    )

    val palette = mapOf(
        '0' to Color(0xFF0C0C10),
        '1' to Color(0xFF262630),
        '2' to Color(0xFF484855),
        '3' to Color(0xFF737684),
        '4' to Color(0xFFA2A6B4),
        '5' to Color(0xFFD0D4E0),
        '6' to Color(0xFFFAFAFF),
        '7' to Color(0xFF121C44),
        '8' to Color(0xFF203A7A),
        '9' to Color(0xFF3462B6),
        'A' to Color(0xFF301C12),
        'B' to Color(0xFF588EE4),
        'C' to Color(0xFF0F3444),
        'D' to Color(0xFF58301C),
        'E' to Color(0xFF844E2D),
        'F' to Color(0xFFB27648),
        'I' to Color(0xFF55267A),
        'K' to Color(0xFF4E8080),
        'Q' to Color(0xFF301644),
        'U' to Color(0xFF94BCF8),
        'V' to Color(0xFFCDE4FF),
        'W' to Color(0xFF8444B6),
        'a' to Color(0xFF204444),
        'b' to Color(0xFF346262),
        'c' to Color(0xFF1C5C70),
        'd' to Color(0xFF348EA5),
        'e' to Color(0xFF66BCCD),
        'g' to Color(0xFF122626),
        'v' to Color(0xFF76A8A8),
        'w' to Color(0xFFDAA270),
        'x' to Color(0xFF8E4E3E),
        'y' to Color(0xFFC0765C),
        'z' to Color(0xFFEBA284)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawNorg() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        ".............................................Q..................", // 2
        "............................................FQ..................", // 3
        "............................................EQ........Q.........", // 4
        "............................................PQ.......QQ.........", // 5
        "............................................P.......EQF....Q....", // 6
        "............................................C.......PQ...QQE....", // 7
        "...........................................EC......AP...QPT.E...", // 8
        "............................................C.....AP...APE.QQ...", // 9
        "...........................................PA....AA...AASEPQ....", // 10
        ".............................FQQ...........AA...AA...ACSSPQ.....", // 11
        "............................EQQEE......F...AA.DAD...AASPCQ......", // 12
        "...........................EQAPEE....QPQ...AAANNK.DAAPCPPQ......", // 13
        "..........................DDDNDQEQQ.PPQ...DAAANNKNAAAAAPQ.......", // 14
        "........................KKKKDSPQEEQPP.....KNAAANNNNNAAAP........", // 15
        "......................KKKKKKDEEEEFFEQEF..DKKNNNNNNKAACP.........", // 16
        ".....................DDKKKKKQQEEEEQEEEQ.DDKKKKKNNNNNAPE.........", // 17
        ".........................DDSEQQEQCAEFSKDDDKKKKKKNNNNA...........", // 18
        ".........................0SQQEFEAAPQQSKKNDDKKKKKNNNAQ...........", // 19
        ".........................DEPQEEAAANAPAKKKDDKKKKKNNNP............", // 20
        ".........................KPQQPAAANKNNKKKKKDDKKDDKNN.............", // 21
        ".........................KQEQQQAKDDDDDKKKKKDDKDDDD..............", // 22
        ".........................KEQPEPNKDSSSSDKKKKDDDDDD...............", // 23
        ".........................NQPEEDKKKDDDDDKKKDDDS..................", // 24
        ".........................KDPEQNKKKNDDDNKK.......................", // 25
        "........................0KKQECKKKKDDDDDKD.......................", // 26
        "........................0DNEQDKKKKASSSSND.......................", // 27
        "........................D0DQPNKKKKDSTTSDK.......................", // 28
        ".........................0DDANKKKKDSTTTPK.......................", // 29
        ".........................D0DDKKKKKDQTTTSKD......................", // 30
        "......................K..DKKKKKKKKDSTTTSKK......................", // 31
        "......................KKKKKKKKKKKKNQTTTSNK......................", // 32
        ".....................DKKKKKKKKKKKKKSTTTSDK......................", // 33
        ".....................KKKKKKKKKKKKKKPTTTTDKD.....................", // 34
        ".....................KKKKKKKKKKKKKKSTTTTDKD.....................", // 35
        ".....................KKKKKKKKKKKKKKSTTTTDKD.....................", // 36
        ".....................DDDDDDDDDDDDDDSTTTTSDD.....................", // 37
        ".....................DDD00000DDDDDDSTTTTSU0.....................", // 38
        "....................DDDDDDDD000UUU0STTTTSDD.....................", // 39
        "....................DKKKKKDDDDDDDDDSTTTTSDD.....................", // 40
        "....................DKKKKKNDDDDDDDDSSTSSSDD.....................", // 41
        "....................KKKKKKNNNDDDDDDSSSTSSDD.....................", // 42
        "...................DKKKKKNKNNNDDDDDSSSSSSDND....................", // 43
        "...................KKKKKKKKKKNNNNDDDSSSSPNKK....................", // 44
        "..................KKKKKKKKKKKNNNNNKDSSSSDKKK....................", // 45
        "..................KKKKKKKKKKKKKKKKKDSDSDDKKKD...................", // 46
        "..................DKKKKKKKKKKKKKKKKDDDDDNKKKK...................", // 47
        "...................NKKKKKKKKKKKKKKKNDDDDNKKKD...................", // 48
        "...................NDNNKKKKKKKKKKKKKDDSSSDDDD...................", // 49
        "....................DNDDNKKKKKKKKKKND...........................", // 50
        ".......................DNNDDDDDDDDDD............................", // 51
        ".........................DDDDDDDDD..............................", // 52
        "................................................................", // 53
        "................................................................" // 54
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawEdea() {
    val matrix = arrayOf(
        ".............................................................", // 0
        "...............................I.............................", // 1
        "..............................iIi............................", // 2
        ".............................iIIIi...........................", // 3
        ".............................mIPIm...........................", // 4
        ".................UUi........iIIPIIi.im.......................", // 5
        ".................UIIIi......mImQSIm.IIm....iiIII.............", // 6
        ".................IIIIIm.....UIP1PIImIIU...mIIIII.............", // 7
        ".................IIPmIII....UIPS1MImIII..mIISIII.............", // 8
        ".................IIP2PmIm...UIS1AIImIrII.mIS1III.............", // 9
        ".................mIA11SIImi.UII1MIImqRmImIP11III.............", // 10
        ".................iII1D1IIIIIIIR1DMImpRmmISDI1IIi.............", // 11
        ".................iIIACNDSIIPANRZAAPTRNmmIS1CIII..............", // 12
        "..................IIIKNNSSDDMKNZRDmTNDICC2DMIm...............", // 13
        "..................mUIMDSSDDII0AZNDMSKMRRA1SIUi...............", // 14
        ".................iIIIDSSDDDDSDIZNKKKKDNNANASII...............", // 15
        "................iIIIKDSSDDDSDDIIDAQ1AMNNNMIASII..............", // 16
        ".........mmiIUUIIIIDMIKDDDSSDDIMDESA1AANDIIIPSIIIUUUUUIUm....", // 17
        ".......imIIIIIIIIIIDIMNMDDSDDSDDDSN1AANKNDIIIPIIIIIIIIIIm....", // 18
        ".......IIIIPPQEmIIDIIAMISSTTTEQDDQQNNNKIIMPIIDPmmSQEQDIIm....", // 19
        "........IIIM1DPAAANIINII1QPEE2P1KC1I1NIIImDPIID1PPAIAIIm.....", // 20
        ".........mVIDAAN1QQAAMIIAQFFFQPAKGDCNDIUIIINSPEQ2AAAIIIi.....", // 21
        "...........VIIIK11111CIIDTFFTIMMNKKKKMIUIIIQQ2221MIIIUi......", // 22
        "............IIIINNA11111MBmTMNSMNKKKDIIImS1221ANNIIIm........", // 23
        "............iUIIDMDKDA1KKNSFQ1EDPQNKNIII1111ANII1III.........", // 24
        "............iUIPNmDMIMKKAPFFFQPCPAKKKDA1AADMIDNIPIII.........", // 25
        "..........immIIANINIIIKKKNQFFQPDNKDKKNANDIIIIPNmIDIIm........", // 26
        "........IIIImmIADSAIKKKDNNNQQPNKAKKKKNNIIIIIIPNISNmIIIIm.....", // 27
        ".......IIImPQQPQEE211ANKDKKDPAKKDDKKNIIIIImmmP111AIImmIIm....", // 28
        "......iIIPAAPCA1111AANKDDNDPQPNDDDKKKNA111112QQQQQC1PQSIIm...", // 29
        "........IIIDNDSNNNNNNKKDSSEETDDDKKKKKKNNNNNA12222AA1P1NIIm...", // 30
        "........IImIImimSmTTTTDKSEETDI0DKKKKmmTTTTTSNNNNNSmNANIIIi...", // 31
        ".........mmImmiimiiiiiDKDSQPDDKKKKKKSiiWWiiWWWWiiimmmmIIi....", // 32
        "...........VIImmmiiiimDKKNSDKKKKIMKKDiiWWiiWWWWiiiimmII......", // 33
        "............IIimmiiiiSKKKKSDKKKImIKDDiiWWWiWWWWWiiiIUUi......", // 34
        ".......i....ImimiiiiiKKKSKSKKKKImIKDDiiiWWiWWiWWiWWmmV..mU...", // 35
        ".......m....ImimiiiiiKKDDKKKKKKKmmDKDSiiWWiWWiWWWiWiIi.iUUV..", // 36
        "...........iImimiiiiDKKSKKDDKKKKMmIKDmiiWWWWWiiWWiWiII...m...", // 37
        "..miii.....mImimiiiiKKSDKDKKKKKKKImKKDiiWWWWWiiWWiWWmUmm.....", // 38
        "...iIIm....mImimiimDDKmDKKKKKDKKKImmKDDDiWWWWiiiiiWWiIi....mm", // 39
        "....IIm...mImimmmIDKKDiDKKKKKDKKKMmmDDDDSWWWWiiiiiiWiII....mm", // 40
        "....mmi...mImimmIIISKmiDKKKKKKKKKMmmmDSDSWWWiiiiiiiWWmI......", // 41
        ".........mmIiimimiiiiiiDKKKKKKKKKMmmmiiSSWWWiiiiiiiWWmIm.....", // 42
        ".........mImiimiimiiiiiDKDKKDKKKMmmmmiiiiWWiiiiiiiimiimm.....", // 43
        ".m.......mImimmiiiiiiiiSKDKDDDKKMmmImiiiiWWWiiiimmimiWiIm.i..", // 44
        ".i....mi.ImiimmmiiiiiiiiKDKDDDKKIImImiiiiiWWiiimmmimmiiIIii..", // 45
        "......IIUImimmmmmiiiiiiiKDDDDDKDmImImiiiiiWWWiimmmimmiimIi...", // 46
        "......mmIiiimmmmmmiiiiiiM0DDDDKKmmmImmiiiiiWiiimmiiimiiimU...", // 47
        ".......ImiWTmmmmmiiiiiiiM0DDSDDKImmImmiiiiiiiiimiiimiWiimIm..", // 48
        "......iIiWimmmmmmiiiiimiSDDSSDDKMmIImmiiiiiiiiimiimmmiiWimI..", // 49
        "......mIiWTmmiiimmiiiiiimDDSSDDDMIUImmiiiiiiiiimmmmmmmiWWmIm.", // 50
        "......IIiimmmmmimiiiiimiSDImSSSDMmImmmiiiiiiiiimmmmmmmiWWiII.", // 51
        "......IIimmIIIImmmiiiimiDIImmSSDMImimmiiiiiiiiiimmmmmmmiiiIIi", // 52
        "......mIiIIIUUUImmmmmmmi0ImimmSS0IImmmmmmiiiimimmmmmmmmiiimIi", // 53
        "......mIIIImmVVIImmmmmiIIVmimmSmVIImmmmmmmimmmmmImmIIImiimIIi", // 54
        ".......IIm..UVImUIImmmmmmmiiiimimIImmIImmmmmmIIIImmVUImimIII.", // 55
        ".......iIi..UUUUUUIImmmimmiiiimiimmmIIUIIIImmIUmi...mImmIIIi.", // 56
        "............UUVUUIImmiiiiiWiiimiiimmmmIUUIIIIVmi....mIIIII...", // 57
        "............mUiUUImmiiiiiiWiiiiiWWiimmmIUUUUUUmi....mUUIm....", // 58
        "............mVmUUImmmmmmiiiiiiiiiiiimmmIIUUUUUmi....immi.....", // 59
        "............iiUUUIImmmmmiiimmiiimmmmmmmIUUUUUVm..............", // 60
        "..............mUUUIImmmmmimmmmiimmmmmmIUUUUUVi...............", // 61
        ".................imIIImmmmmmmmmmmmmmIIIUUUi..................", // 62
        ".......................immmmmmmmmmmmm........................" // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'Z' to Color(0xFFD21E19),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(flip(matrix), palette)
}

fun DrawScope.drawSeifer() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        "......................QPQQQQQP..................DD..............", // 2
        ".....................EQ44Q4QQ11N...............DD...............", // 3
        ".....................CQQ24Q112PPC............DD.................", // 4
        "....................FQ11Q12QP11PPP.........DD...................", // 5
        "....................1C111Q4QQ4Q1AA.......DD.....................", // 6
        "......................AQFFFFE11PCA......DD......................", // 7
        ".....................EQFFFFFQPP1NA....DD........................", // 8
        ".....................FEFQFFFQCAPAN..DD..........................", // 9
        ".....................FFSPFEDKKAAAND.............................", // 10
        ".....................DAAEENGSNCQAD..............................", // 11
        "......................DAFDKgTDCQN...............................", // 12
        ".......................EFFDcEPNPN...............................", // 13
        ".......................EFFFFECNN................................", // 14
        "..........KDDD........DPFFFFPNK.................................", // 15
        ".........DKDS.DKKD.....DSFEQCNKN................................", // 16
        "........DDKKDD.DKDD.D.DKNACQQAKK..DD............................", // 17
        "........DDKKNKDDKDDD..NKAPQEECKD...D............................", // 18
        ".........KKKDDDKKSW..DDPQEEETDDD..D.............................", // 19
        "..........KKKKKKST..DDPTFFFTMDD.....Z..F........................", // 20
        "..........DDDDDDD..D..QEETTIKDDD....Z...........................", // 21
        "...............DD...DD...IMMDDS....QZZE.........................", // 22
        "...............DD...DDI.mIIDDD......C...........................", // 23
        "....................DS....IDD.....D.PP..........................", // 24
        ".....................DM.IMMDD...DDDQ.Z....F.....................", // 25
        "...................D..MIMMMD...D.DP..Z...FF.....................", // 26
        "...................D.IMSIMM...DD.Q...Z..FE......................", // 27
        "...............Q.....IMSSM....DFF....Z..FE......................", // 28
        "..............F.......ISSI....DE.....X..........................", // 29
        "............F......D...DD.m...D......P..........................", // 30
        "..........................D....D................................", // 31
        ".....................D...PA....DDDD...D.........................", // 32
        "....................DD...DN.....DS.DDDDD........................", // 33
        "....................DKKKKKK.....DD....DD........................", // 34
        "....................KKKDKKDD....DDD...D.........................", // 35
        "....................KDKKKKDD....DDKDDDD.........................", // 36
        "...................DDKKKKKKN....DDKDDKK.........................", // 37
        "...................KDKKKKKKK.....NDDDK..........................", // 38
        "...................DDKKKKKKK......DDD...........................", // 39
        "..................DDDDKKKKKKD......D............................", // 40
        "..................DDDDNKKKKND...................................", // 41
        "..................DDDDDKDKKDD...................................", // 42
        "..................DDDDDKDDKKD...................................", // 43
        "..................DDDDDKDDKKD...................................", // 44
        ".................DDKNNKKDDDKN...................................", // 45
        ".................DDKNNKKKDDKK...................................", // 46
        ".................DDKNDKKKDDKK...................................", // 47
        ".................DDDDDKKKPQQN...................................", // 48
        "..................QSDDNKKQEQN...................................", // 49
        ".................EEPDDNNKQEQN....DD..E..........................", // 50
        "..............E..EEPSSDNKQEEN...DDKQQF..........................", // 51
        ".............EEE.EEESSDNKPEEQDDDDDKQE...........................", // 52
        "..............EQEEQSDDNNKPEETNNDDDSNQ...........................", // 53
        "...........QQQQQQQDKKNDANPQQSDADDSDK............................", // 54
        "...........ETQQQSDNKKKAAAPPPSNNNKNDN............................", // 55
        "..........EQQSPSSSSDNNNAAAPPDDAANNND.QQ.........................", // 56
        "..........QQPPPPSTEQDDNNACCCDSQQPCANDPPQE.......................", // 57
        "..........QPPPPCAACAAAAACCCCPTEQQQCNCPPPQ.......................", // 58
        "...........PPAAAAACCCCCCCPCPAANAAANNAPPS........................", // 59
        ".............DDDDDDAAACPPCACCADDAAAADPP.........................", // 60
        "..................DDDDDDDDDDDDDDPPSSS...........................", // 61
        "................................................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '4' to Color(0xFFFFF56E),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'X' to Color(0xFFF5372D),
        'Z' to Color(0xFFD21E19),
        'c' to Color(0xFF14A5A5),
        'g' to Color(0xFF199123),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(flip(matrix), palette)
}

fun DrawScope.drawAdel() {
    val matrix = arrayOf(
        "..............................................................", // 0
        "....D.........................................................", // 1
        ".....D........................................................", // 2
        "......D.......................................................", // 3
        ".......KD.....................................................", // 4
        "........KD....................................................", // 5
        ".........DD...................................................", // 6
        "..........DK..................................................", // 7
        "...........KK.................................................", // 8
        "............KKD........................D......................", // 9
        ".............KKD..............................................", // 10
        "..............DKD.............................................", // 11
        "...............KKD............4E......P.......................", // 12
        "..D.............KKD.........C1.PJ....QE.......................", // 13
        "....DD...........KK.............N1NAAP........................", // 14
        "......DKD.........DK...........NNNZRNC........................", // 15
        "........DDK........KKD.........KKRNDDD........................", // 16
        "...........DDD......KKD.........KRNDDD.D......................", // 17
        ".............DDD....DKKD......NNDRSSSD........................", // 18
        "...............DDDD.KKDDK....PRCDRDSS.........................", // 19
        ".................KK.KDWTDK...RNNRNKDA.........................", // 20
        "..................D.KDWWW.D..DNSNSDKD.........................", // 21
        "...............DDDK.DSWW..KD...DDSTDD..D......................", // 22
        "........DKKKKKDDKKKD....NKD.D...DdSS..DKD.....................", // 23
        "....DDDD...........DDDNKRNDK.D...KS.DDKKD.....................", // 24
        "...................DDDDNKKS.DDDDDDD..DDDD.....................", // 25
        ".................DKKKKKNSD....KKKK.DDQDSD.....................", // 26
        "................RRDDSDDKTD....DKKKDDNDDS......................", // 27
        "...............RRNSWSSDDWD.D.DDKKKADDKDKDD....................", // 28
        "..............KNATWTSDDWWKDD.DSPCAPPDKKSD.D...................", // 29
        "..............DDWWWTDDT.DDDDD..DPPNDDDDD.D....................", // 30
        ".............ANWWWWSDKK...DDD...KDDDKDD.D.DD..................", // 31
        ".............NTWTSDDD......DD.A.DDDANTQ..DKDD.................", // 32
        ".............NTSDD...........PQ1NRACAS.D..DDDP................", // 33
        "............DDDD.............DDDSDDAPPDN...DAD................", // 34
        "...........DKK................DSTSDDACCAAD..K.................", // 35
        ".........K..D.................DSTTDDCADQK.....DD..............", // 36
        ".......D....t.....................DKKSFED......DK.............", // 37
        "............D..................KD..KKQFFD......D..............", // 38
        "............AA................DDKKKDNQEQP.....................", // 39
        ".............R................DDKKDDKQQQE.....................", // 40
        ".............RA...............DKKDDDKQEFFD....................", // 41
        "..............R...............KKDDDDKQEEEK....................", // 42
        "..............................KDKDDKKEQQEK....................", // 43
        ".............................DDKKKDKKEEEFKD...................", // 44
        ".............................KKKKKDKNQFFFNK...................", // 45
        "............................KKKKKDKDNPFFFDK...................", // 46
        "............................KKKKKKDDKPFFEDK...................", // 47
        "...........................DKKKKDDDKKPEFQDD...................", // 48
        "..........................DKKKKDDDKDKPQFQS....................", // 49
        ".........................DKKKKDDDKDDKPEFQ.....................", // 50
        "........................DKKKKKDDKDDKKPEEE.....................", // 51
        "......................DKKKDKDDDKDDDKKDEEFQK...................", // 52
        ".....................DKKDDKDDKKDDDKKKDEFFQK...................", // 53
        ".....................KDDKKDDKKDD0DKKKAEFFQK...................", // 54
        "....................KKKKDDDKKD00DKKKKAQFFPD...................", // 55
        "..................DKKKDDDKKKDD0DKKDKKAQFFPDD..................", // 56
        ".................KKKDDDDKKKDD0DDKDKKKNPEQPDD..................", // 57
        "...............DKKDDDDDKKDD00DDKDDKKKKPQAPKKDK................", // 58
        ".............KKKKKDDDDDDDD0DDKKDDKKKKKPAACKKKN................", // 59
        "...........DDDDDDDDDDDDD00DDKKDDDKKKKKAPAPAKNRRRRRDDD.........", // 60
        "..........D0DDKKKDDDD000DDKKKDKKKKKKD..NACRNNNNNNNNRNNNKN.....", // 61
        "..........KKD.KD00000DDDKDDKK...DKD................KNKNKKKKK..", // 62
        "................DKDDKKK......................................." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '4' to Color(0xFFFFF56E),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'Z' to Color(0xFFD21E19),
        'd' to Color(0xFF5AD2CD),
        't' to Color(0xFF0A6469)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawTrauma() {
    val matrix = arrayOf(
        "................................F............", // 0
        "..............................F31............", // 1
        ".............................431A............", // 2
        "............................E21AA............", // 3
        "...........................421AA.............", // 4
        "..........................F221AA.............", // 5
        "......E44.................4221AN......P11....", // 6
        "......12344...............331CAN...AAAACQ....", // 7
        "......Q12344.............4221AAN.DNAAAAP.....", // 8
        ".......12234F............321ACCCAANNAC.......", // 9
        ".......122234............21C111222111........", // 10
        ".......1222234..........E11123442211A........", // 11
        "......AC122333F.........212332211CCAA........", // 12
        ".....E1C1122223........F11321AAACAAA.........", // 13
        "...FQ1111111222F.......Q1321CACAAAA..........", // 14
        "..Q343332111222Q.......Q121CA1AANAA..........", // 15
        ".P1111234211121C......F1Q1CAACANNAP..........", // 16
        "..AAAA1123Q1112AP.....Q1111AAANNAAQ..........", // 17
        ".....PCC11QQ1A1A12F4EQ1111CACANAAA....FQ.....", // 18
        "......AA11121AA121111111CAACANCAAQ...Q22E....", // 19
        ".......AC11111111C1AA111ACAAAASPQ..E21111F...", // 20
        "........ACAC1111111AA1ACAAAANSTS..E4111111...", // 21
        ".....P...AACCAAACQ1ANAP1CAAASSP..E43111C11P..", // 22
        "...E21P..PACCAPCAPAAAPP1AANTWW.PP243211111A..", // 23
        "..Q3311...AAAPPQ1PP1PQPNNNSWW...13321111111..", // 24
        "..14211CEPP....FPPSPEPPNNNTW...E2311CCC1111P.", // 25
        ".E33211PSAE....QEPSEQPNNNAW...E22221A1C1111A.", // 26
        ".P2322111AP......DPSDNANAW...F222111CC1C111A.", // 27
        ".C1222211AAA.....DAAAACAW....E222111C11C121A.", // 28
        "Q1112221CAA1A......1CACP.....E22211CC11C121C.", // 29
        "Q1122221AAA11.....P1CC1P.....Q22321C1CC1121A.", // 30
        ".1232211AA11C....QCA1111Q....Q123421CCC1221A.", // 31
        ".C331211AA111....1P1111A1....Q12442CCA11221P.", // 32
        ".1431111AA11P....ANNNA1C1....Q12431CA11111A..", // 33
        ".P43211AAA11....P1NA1CCAAA...Q1242CAC11111A..", // 34
        ".Q23211AAA11....11NN111AAAC..P1241CA11111A...", // 35
        "..142111AA1P....P1NN1111AACQQ11241CAAAA11A...", // 36
        "..141111AA1Q....1CNNA211CAC1111241AAAAC1NA...", // 37
        "..A21A111A1Q...Q1AAAA1111AA11A1121AAA11AN....", // 38
        ".EA1ACCQ1C1....P1CAACA1111111AAC11AA121AA....", // 39
        ".E1A111321P....1AAAAAAC1112Q1A11CAA112ANC....", // 40
        ".F1AAA12Q1....Q1NNCCANNC1241CCCA1A1121AA1....", // 41
        "..1AAA122.....P1KA1CCNNNC2111A1AAA122AA1P....", // 42
        "..PAAAC21.....1NKAPPPCAN1Q1111A1AA121AC1P....", // 43
        "...111111....Q1NNNAAANNN2311C11AAA11AA11.....", // 44
        "...Q111QF....1CAP.AAAAAC22111A2CCAACA11C.....", // 45
        "...E111Q.....1CQ..CAAAA111FP111111AAACCP.....", // 46
        "....Q11Q.....CQ....A1AA1CFWTC21111111AA......", // 47
        ".....1111QF........P11C1CWWWT1111111Q1Q......", // 48
        ".....FQ222E........Q11C1PWWWWP1111C111F......", // 49
        ".......E212........Q11C1AWWETP121F111Q.......", // 50
        ".........EE........P22C1CQF11111EQ121........", // 51
        "...................132111CE22QQEF111E........", // 52
        "..................F132111CQ....1121Q.........", // 53
        "..................EPJJ11QP....E22PF..........", // 54
        "..................QPEQQPQ1....FQE............", // 55
        "...................1Q111PP...................", // 56
        "....................ACAAAA...................", // 57
        "....................ANAAAA...................", // 58
        "....................AAAAAA...................", // 59
        "....................1CCA11...................", // 60
        "....................P111PP...................", // 61
        ".....................QPPPQ...................", // 62
        ".....................QPPE...................." // 63
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF)
    )

    drawMonsterMatrix(flip(matrix), palette)
}

fun DrawScope.drawOmegaWeapon() {
    val matrix = arrayOf(
        ".................................KKK............................", // 0
        "................................KDSDK...........................", // 1
        "................................DDKKK...........................", // 2
        "...............................D..DDED..........................", // 3
        "...............................KD.DKSD..........................", // 4
        "................................KDDDN...........................", // 5
        "................................KKDDD...........................", // 6
        ".................................KK.............................", // 7
        "...........................DD....DD................ND...........", // 8
        "..........................K..D...DD...............D.DD..........", // 9
        "..........................KD.DD..KDD...DNDK......KKD.DD.........", // 10
        ".........................KD.N.DKD.DDNKKKSTDK...KKDKDD.K.........", // 11
        ".........................KDKKDDNDDNKDDDDDSDDKKDDDDDDDDDD........", // 12
        ".........................KKDDNDDNNKKDSDDSDDDKKDDDDKDSKDK........", // 13
        "........................KDDSSNKDDDDKDSSSDDDDDKDSDDDDSKNKD.......", // 14
        "........................DSSTSDDDDSSDNDSSSDDDDDDNDD..DDDDDD......", // 15
        "........DD.DD........D....DDDSSSSDDDKKDDSSSTSDDK.....DDSSDD.....", // 16
        ".......DND.DD.......D...DDDKKKDDDDNKKDDDSSSTSDDKN....DDDDKD.....", // 17
        ".......KDKDKD......NDDDDDDKDKDKKDDSDKDSDDSSSDDDKD.....KDKKD.....", // 18
        "......D..DKKK..D..D.DKKDNKKDDDSDKDDSSDSDDSSDDSTDKD....DKDDK.....", // 19
        ".....DDD.KKKKK...NKD..DKKDKSSDSTDDDDSDDDKDDKSSTSKD.....DKDDD....", // 20
        ".....KSDD...KKD...KKD..DKDKDTDDSKKKKDKKDKDKDSSSSPKD....KKSSN....", // 21
        "....D.DKD.....DD.DKDKD.DKDKDTSDSDDKKKSNKNDKNDSTQNKD....DDSDK....", // 22
        "....K.KD.......DDKDKKK.DKKKSDSKDDDDDDNSKDDKA1ESSDKD....DDDD.....", // 23
        "...DDKKD........DKDDK..DKKKSSNKKDDKKNKSDDSKKDSSSKDD....KDD......", // 24
        "..NKKSSD........DNKDK.DDDKKSTSKKKDDKKKSDDSDKDSTSKSD...DNDDKK....", // 25
        ".DKDSKD.........DDKK..DDDKKDSTDKKKKKSSDDSTSKKDSKDD....KKSDDK....", // 26
        "K..KDSD.........DSSK.DDDKKKKSSDKKDKSSDDSTTSDKKKK.D....DSSDDDK...", // 27
        "K..DKD...........DS.DDDKKDKKSDKKKKKDDKSTTSDDDKD.D......DSSDDK...", // 28
        ".KDKDD...........DKDDKKKDDSKDDSKDDDDSSKSSDDDDKDD.....KK.DSDDK...", // 29
        ".KKSSDD..........DDKKKDDDDDSKTTDKDTDDDKDDDDDKKN....KKDD.DDDKK...", // 30
        ".KKSDK............DDDKKKKDDDKSSDKKSSNKDDDKKKN.....D.DSDKKKKK.D..", // 31
        ".N..DKKD...........DKDKKKKKKKNDKKKDSNDDDKTKK......KDDKNDSSDDD.K.", // 32
        "..DKKDD.K............KKKKKKKKSDSDDDNDDNKSKD.........KKKDSDNKDDK.", // 33
        "...KSD.DD............KKDKKKDDTTSDKKKKD..DD............DDKSDKKD.K", // 34
        "...KD.DKKD..........KKDSKKKDKSSDDDDDD...................KSTDD..K", // 35
        "...K.KDDDD..........KDSSDDKDKDSDKD.........................D.N.K", // 36
        "...D.DSDDK..........DDDDKKDDKKDKDD.........................D.D.D", // 37
        "....D.DTKDD.......DD.DNNDTSKDKDSDD..............................", // 38
        "....D.DSDKK......D....KKSTKKDDSSDDD.............................", // 39
        ".....K.DSKKD......DDDDDKKSKKDDDDDDKKKK..........................", // 40
        ".....KDKDKDDN............KKKDDSKDKKKKKKKKKKK.KKKKKD.............", // 41
        ".....DKKKDKKDD...........KKKSDSSDDKKKKKKKKKKKKKKSDKKKKD.........", // 42
        "...KDDNDDDKKKNKD.KKKKKKKKKKKDKDSDDKKKKKKKKKKKKKKS..DKKKD........", // 43
        ".KDKDKSTDKKKKKKD....KKKKKKKDKKKDDDKKKKKKKKKKKKKKK...............", // 44
        "....KKDDDKD............KKKKDDKKDKNDDKKKKKKKKKKKKK...............", // 45
        "........................KDSDSDDDKKDSDKKKKKKKKK..................", // 46
        ".......................DDTTKSDSSDKDSDDKKKKKKK...................", // 47
        "........................NDSKDDDSDKDDKSSKKKK.....................", // 48
        "..........................KKKKDDDDKKKSTTN.......................", // 49
        "............................KKKDTTD..DDTD.......................", // 50
        "..............................KKSW.....D........................", // 51
        "................................................................", // 52
        "..................................K............................." // 53
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        'A' to Color(0xFF823719),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawUltimecia() {
    val matrix = arrayOf(
        "................................................", // 0
        ".............DPqr...............................", // 1
        "................rrr.............................", // 2
        "....................P...........................", // 3
        "................................................", // 4
        "....................D...........................", // 5
        ".....................D..DDN........DD...........", // 6
        "..........................AN.DDDD.DN............", // 7
        ".......................D..DAD..DDDD.............", // 8
        ".........................KDN....DKD.............", // 9
        "..........................KD..NQQD..............", // 10
        "......................D..NK.D..EDN..............", // 11
        "....................D.DKKNKND...DND.............", // 12
        "....................DKDDKDKDD..DDDDDD...........", // 13
        "....................DDDNDKDDD.DKDDKDDD..........", // 14
        ".....................KKKDDDDD..QDSDDK...........", // 15
        "....................DDKKKDDDD..QASSDN...........", // 16
        "...................DNNDKNNDDD.DQPDSDDD..........", // 17
        "...................DKDKKNRKKD..PEDSSKD..........", // 18
        ".................KDDKKKKNRNKD..EESSDND..........", // 19
        "...............DDKKDKKKKNNNNN..PPNDDKKD.........", // 20
        "...............KSNDDKKKKNRKNN...QNDDKD..........", // 21
        ".............D..DKKKKKKKRRNNN...QKDDKKDD........", // 22
        "...........DDD.DKKNNDKDDRA.DN.D..NAANKKKK.......", // 23
        "...........DNNDDKDDDNDDNRS.DNDDP.NDARrDDKD......", // 24
        ".........DDNKKKDKDDKSDSRNS.DNDD.EDDNKrIIDD......", // 25
        "........DDKKDDDDKDKK..NrD.SKNSD.PKDKKKKNDD......", // 26
        ".......DDDDDDDKKDDDD..RCD.DNASNPDKDDKNKKKID.....", // 27
        ".......DDDDDDKKK.S...SrASSKNNSRDDKD.SKDDKDKD....", // 28
        ".......KDDDDKKKS.....DIN.DNRNSRANND..KDDKDKKK...", // 29
        ".......KDKDDDKKS.....DID.KNRNSRRNND.DDDKDK...D..", // 30
        ".......KDKKDKKDD....DMD..DNNKDRRNNN..KDDKK......", // 31
        ".......KDDKDKDD.....DMD..SKNKARRRNK.SKKDKD......", // 32
        ".......KDKDDDKKD...SIND..SKNNNRRNNKDKKKDKD......", // 33
        ".......KKKDDKKS...NDD.S...KNRRRNNNKS.KKKKD......", // 34
        ".......KDKDDKKDD..DDK.D...SNRRRANRKSKKKKKD......", // 35
        ".......NDKKDDKD...D.SS....SNRRRARRKDKKDKKD......", // 36
        ".......DKDKDKDD...........SNRRNNNNKDKKKKKD......", // 37
        ".......DKDKDKKKK..........SNRRAKDNKSDKKDKDD.....", // 38
        ".......DDKKKKKDDKD........SNRRCNPNNKKDDDKKDKD...", // 39
        ".......DKKKKKDDSS.........DNRRPNPNND....DD......", // 40
        ".......KKKKKKDDDS......DSSKNRZQNANN.............", // 41
        "......DKKDKDDKDKS.....DKKNKNRAPNANN.D...........", // 42
        "...DKDDDDKD.DS.SS.DDSKDKKKKNRNANANNKK...........", // 43
        "..KK...DK.........KKKKKKKKKRRANNPDNKKKDD........", // 44
        "...............DDDKKKKKKKKNRRCNDNNNKKKKK.D......", // 45
        "..............KKKKKKKKKNKNRRNANPDNNKKKKKKKKK....", // 46
        ".............DDKKKKKKKNKKNRRNADDDTNKKKKKKKK.....", // 47
        "..............KKKKKKKKKKKNRNDDKD..NNKKKKKKKKK...", // 48
        "...........DD.KKKKKKKKNKNNNKDKNK..DNNKKKKKKKKD..", // 49
        "............KKKKNNKKKKKKKKNKDDDK....NNKKKNNKK...", // 50
        "............KKKKKKKKKKNKKNNKPKDN.....NNKKKKKKKDD", // 51
        "...........DKKKKKKKKKKKKNNKKSKDD.....DKKKKKKKKDD", // 52
        ".........KDKKKKKKKKKKKKNKKSDSDDD.......KKKKNKKD.", // 53
        "..........KNKKKKNKKKNNKKNSSDSDD.D.......DKKKKKN.", // 54
        "........DKKKKNNNKKKKKKKN...DSKNDD..........DD...", // 55
        "..........DKKKKKKKKKKKND..DDSDDDDD..............", // 56
        "..........DKKKKNKKKKKKD...DSS..DDD..............", // 57
        "........D..DKKKKKKKKKD.....D...D................", // 58
        ".........DKKKKKKKKKKD.......D...................", // 59
        "...........DKKKKKKD.............................", // 60
        "...........N..KKK...............................", // 61
        ".............DD.................................", // 62
        "................................................" // 63
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'Z' to Color(0xFFD21E19),
        'q' to Color(0xFFFA649B),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawPlantBrain() {
    val matrix = arrayOf(
        "................................................................", // 0
        "......................................................QPPQ......", // 1
        ".....................................................PPPPN......", // 2
        "....................................................QPCACP......", // 3
        "..................................................FCAANNNN..D...", // 4
        ".................................................QPAANKKKK...D..", // 5
        "...............................................EQCAANKNNNKN..D..", // 6
        ".............................................PCAACAANNKKKKK..DD.", // 7
        ".............PP................QQ..........PQPAPCAANNKDTKKK...D.", // 8
        ".............APP...............PP.........QPPCACANDNNNDTDKK...D.", // 9
        "............DAACPE............EPP........EPPCAAANNNKNNDDSKKD..D.", // 10
        "............KNNAAPP...........PCCQ......QPPACANADNDKKNNDDKKG..D.", // 11
        "............DNNNAACCCP........XZZP.....PPPAAAANNNNKKDKKNNNNNDDD.", // 12
        ".............KKNNNAPAPP.F....QXXppE..EPPCANNNNKKKKD.......KKGDD.", // 13
        "..............NKNNANACPPPQ...QXppXQ..CCANANNKKKKKK........KKKK..", // 14
        ".............DNNNNNNNNACPPP.FQXZZXQ..NANNNNKKKKKK..........KKD..", // 15
        "............DDNNKNKKNNNPPPPPQQXZZXq..AANNNNKKKKKD...............", // 16
        "..........DDNKKKKKKKKNNPPACpQPXZZXQ.PPPANKKKKKKK................", // 17
        "............DDNKKKKKKKKAQAZZPPZZZXPPAPPAKKKNKKK.................", // 18
        "..............DKKKKKKKNPQPCZpXZZZXPPACPNKKKKNK..................", // 19
        "...............KGSTArPqPPCCZZZZRZZPAPPQCPANNK...................", // 20
        ".................QPZZXpXXZPRZAARRZRCZZXZppPPN...................", // 21
        "................CCRZZZZZZZCPCAARZCCCZZXXXXZPPQ..................", // 22
        "................PPPZZXpZZZRRRPrZZCRRRRZZZZZZZCC.................", // 23
        "...............NACPPCppXXXZRRRrRRRRRRZZXXXZZZCPP..............D.", // 24
        ".............ACAAPCPQQPppZZZRANKKARRZZXXXXZPPPCAA............D..", // 25
        "...........AAACAAAPPAAPPPCCprRRrARrCCCqPPQPQPPCAAA..........D...", // 26
        "..........AAPPCCCAAAACANKNQPZZRPARRCQAAAPAAAPANACAA........K....", // 27
        ".......QAAAAPCPAAAANAANNNAPZPPCPRPZXPNNNNNPNAAACCPCAAND..DK.....", // 28
        "....PPPCAAACCCN..NNNNNKNNPZXPQPACPPpPPNNNNDNNAACPPAAAN...KD.....", // 29
        "..DPPPPANDKDCC.......DKNPPCQQQCRRKSpZCNNKKNNNDNNACAAAND.KK......", // 30
        "....DNANNNNNAA........DNCPPEQPRZrKKAPPANKKNDSSS.QCAANNDNK.......", // 31
        ".......DKDGKND..........AAQQPCZZPKKNAAANNNS.....SAAAADNKN.......", // 32
        "........DDNNND..........NNPPPZZZQKKKNAANKKT....SDDNADNKKAP......", // 33
        "......DDDDDN............KKKNEXZpENKNNNKKKKD...SDNNDDKKKDAND.....", // 34
        "...DDDDDNNDD..........DGKKKNqZZPQKNNNKKKKKDD.TDDNDNKKD..........", // 35
        "..DDNNKGKD...........DGNKKKKQZZPQDNKKKKKGGNDSSDDDNKKD...........", // 36
        ".DDDKKD.............DDDKKKKKQZZqPNNKKKKKDNDDDDDDKKKD............", // 37
        ".DSD..............DDDDGDKKNKPZZpPDKKKKKKDNDDDDNNKKD.............", // 38
        ".NDN.............DDDDDDGKDNKPZZPNNKKKKDNDDDGGKKKKK..............", // 39
        ".NSD.............DDDDDDGDDNNPZZPNKKDNKDDDGDKKKKKD...............", // 40
        "..DD............DDDDDDDDDKKNCZCANNKNDNKDDDDDGKGDDD..............", // 41
        "..NDD............DDDDDDDGKKKPZPNNNKKDDKGDDDDDDDDDDD.............", // 42
        "..DDD..........DDDDDDDDDKKNNCCAKNKNKDDGGDDDDDSDDS...............", // 43
        "...ND............DDSTDDDKKDDCCNKDNDKDDDKDDDDDDTSD...............", // 44
        "....DN...............DDGKKKKAPNKKDKGGDDKGDD.SDS.................", // 45
        "....DD..............D..DKNDKNCKDKKKKGDDD..DS..DD................", // 46
        ".....KN...................KGDAKDKDKKK.D....D....................", // 47
        "......N......................D..................................", // 48
        ".......D........................................................", // 49
        ".......D........................................................", // 50
        "........D.......................................................", // 51
        "........D.......................................................", // 52
        ".........D......................................................", // 53
        "................................................................" // 54
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'X' to Color(0xFFF5372D),
        'Z' to Color(0xFFD21E19),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}



fun DrawScope.drawRalvurahva() {
    val matrix = arrayOf(
        "............................W...................................", // 0
    "...........................TST..................................", // 1
    "......................ST...SDS..................................", // 2
    ".....................TDS..TSDSTT................................", // 3
    ".....................WSDTTSDSTTTTTTW............................", // 4
    ".....................TSTTTSDDTTTSSSS............................", // 5
    ".....................TTTTSSDDDSSSSSDT...........................", // 6
    ".....................STTSSSSSDDSSSSSST..........................", // 7
    "....................TSTSSSSDSDDSSDDDSS..........................", // 8
    "....................SSSSSDDSSDNSSSSDDDT.........................", // 9
    "...................WDSSSSDDSDDDDSSSSSNS.........................", // 10
    "....................DSSSSDSDNDSSSDDDSDDW........................", // 11
    "...................WDSSTSDDSSDSSSSSDKDDT........................", // 12
    "....................SSSSSSSSSSSDSSSSDKDS........................", // 13
    "...................TSSSSSSTTSSSSDDDDSDDDW.......................", // 14
    "...................TSSSSSTSSTSSDDDDDDDDDT.......................", // 15
    ".................WTTSSSDSSSSDKKDSDDDNNDDT.......................", // 16
    "................TSSSSSSSSSSKNDDKDDNDDDDDW.......................", // 17
    "................SSSDDDDDDSNNDSSDDDKKNNNS........................", // 18
    "................SSDSNKKNDDNKDKKDDDNNNNNS........................", // 19
    "................DSDDKDDDDDDDDNNDDDAAAADT........................", // 20
    "................DDNDDDDDDDSDDDSDDNAAANDT........................", // 21
    "................SDDDDDDDDDDDDDDDDPAAAAD.........................", // 22
    "................SSDDDDDDSDDSDDDDNAAAAAS.........................", // 23
    ".................DDSDDDT.TDSDSDPAAAAADSTW.......................", // 24
    ".................TSDDDT..WAAAANAAAAANDSSSTW.....................", // 25
    "..................TTTW...TNAAANNAAAADDSSSTT.....................", // 26
    "........................WPAANNANAAAANSSSSSSW....................", // 27
    ".......................WCACAAAAANNNNDSSSSSST....................", // 28
    ".......................SCCCAAAANDSSDSSSSSSSS....................", // 29
    "......................TAAAACAAAASDDDTSSSSSST....................", // 30
    "......................SAAANAAAADDDDSWSSSSSSST...................", // 31
    "......................APPPAAAADDDDDTWSSSSSSSS...................", // 32
    ".....................SAACCPCADDDDDSWWTSSSSSSS...................", // 33
    ".....................SACCACAASDDDDWWWWSSSSSSSW..................", // 34
    "....................WAAAACCASSDDSSWWWWTSSSSSSW..................", // 35
    "....................SACCAAADSSSSDTWWWTSSSSSSSW..................", // 36
    "....................SPPPAAASSSSSDWWWTSSSSSSTSW..................", // 37
    "....................DAPPPCASSSSSDTTSSSSSSSSSD...................", // 38
    "...................TCPPAAPDSSSSSSSSSSSSSSSSSS...................", // 39
    ".................WTSPPPPCCDSSSSSSSSSSTSSSSSDT...................", // 40
    "........WTTTTTSSSSSPAACAPPDSTSSSSSSSSTSSSSSS....................", // 41
    ".....TTTSSSSSSSSSSSPCPANACSSSSSDDSSSSSSSSSS.....................", // 42
    "....TTSSSSSSSSSSTTSPPPAAAASSSSSDDSTSSTTTSSSTTTTTTTTTT...........", // 43
    "..WTSSTTSTTSSSSTTSSPPPACPCSSSSSSSDSSSSSSSSSSSSSSSSSSSSSTT.......", // 44
    "..SSSSSSSSSSSSSSTTTPPCAPPASSSSSSSDSSSSSSSSSSTTTTTTSSSSSSST......", // 45
    ".TSSSSSSSSSSSSSSSSSSAPCPPPSTSSSSSSSTTTTTSSTTTTTTTSTTSSSSSSST....", // 46
    ".SSSSSTTTTTTSSSSSSSSCCAAPPSTTSSSDSSSSSSSSSSSSSTSTSTSSSSSSSSST...", // 47
    ".SSSSSSTTSSTTTTTTTSSPCACPPTTTSSSDSSSSSSSSSSSTSTSSSSSSSSSSSSSST..", // 48
    ".SSSSSTSSSSTTTSSSSTSPPPCPPTTSSSSSSDSSDDSSSSSSSSSSSSSSSSDSSSTTSW.", // 49
    ".SSSSSSSDSTSSSSSSSTSPACPPCSSSSSSDSDDSDDDDDSSDSDSDDDDDDDSSSSSSST.", // 50
    ".TSSDDSSSSSSTTSSSTSSPCAAPASSSSSSDSSSSSDDDDDDDDDDDSSSDDSSSDDDSSS.", // 51
    ".TDDDDDSSSSSSSSDDSSSSAPPPASSSSSSSSSSSSSDDDSDDDNNDDSDDSDSSSSSSDST", // 52
    "..SDDDDDDDDDDDSSSDSDDDAAACSSSTTTTSSSSSSSDDDDDDNSSSSSSSSSSSSSSDST", // 53
    "..TDDDDDDDDDDDDDDDDDDDANAAPSSSTSSSSSDDSDDDDSDSDSSSSSDDDSDSSSSDDW", // 54
    "...WSDKKKKKDDDDDDDDDDDDPCPPSSSTTSDSSDSSDSDDSSSSSSSDSDDSSSSDSDDS.", // 55
    "......TTSDDDDDNDDKKKKKNPPPPDSSTTSSDDSSSSSSDSSSSDDDDDSSDDDDDDDDT.", // 56
    "............TSSSSTTTTTTTPCPPDSSSSSDSDDDDNDDSSSSDNDDDDDNDDDDDSS..", // 57
    "........................FPCPADSSSSSSDSDDDDDDSSDDDDDDDDDDDDDT....", // 58
    ".........................TAACDDSSSSDDSSDDDDDSDDDDNDDDDDDST......", // 59
    "..........................WSPCAASSSSDDDDSSDDDDDDDDDDDST.........", // 60
    "............................TSPPADDDDDDDDSDDDDDDSSSTW...........", // 61
    "..............................TPAADDDDDSSDSSTT..................", // 62
    "................................TTTTT..........................." // 63
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

    drawMonsterMatrix(flip(matrix), palette)
}

fun DrawScope.drawMaliris() {
    val matrix = arrayOf(
        "................................................................", // 0
        "....................................K....K......................", // 1
        "........................................S.......................", // 2
        "...................................K....K..................S....", // 3
        "...................................S.K.K..K................KS...", // 4
        "..................................KSSK.KK.K........K........S...", // 5
        "...................................ISSKSS.K........K........S...", // 6
        "..................................KSPPSSSS........K........SS...", // 7
        "....................KSSS..........SSPPrSIi.K......SK......SIm...", // 8
        "..................KDArrrSK........rPQPrPS..K......KS.mSSSSSrS...", // 9
        "..................NNAAArPrS.....KPCQPAArS..KKiSS..SSSISSSSPCAD..", // 10
        ".................KNDNArPArDS...KPCPCAAAPS..SiSrmSrSrSSSSSQCSK...", // 11
        ".................DrrAAAPPADNS..SCPPAAAPCQPPSKrPPrPPPrPPPECA.....", // 12
        "................SPAPPDAPAAAADK.PCPPCAAAPPPrrrCCPPPrPPPPQPAKK....", // 13
        "..............SSAANDPDNPPAAADDKCAPPCAAAACCPPAAAACCPPCPPPCAS.....", // 14
        ".............SPPANDDPADDPANDND.DAAPPAAAAAAACCPPCCCPCPPPCSK......", // 15
        ".............SPSDSSSDrDDANNNDN..AAAPCPAAAACPPPPCAACPPPCS........", // 16
        ".............SP..SSSDPDDDNNNNNK.SAAACPPAACPPCCPCAAAAAAP.........", // 17
        ".............SP..DSSSPDDDDNDNNS..SSAACPPCPAAAAAAAAAAAA..........", // 18
        ".............KP..DDSSDDDDDDSSDS.....DAPPPPAAAAAAAAAAAK..........", // 19
        "..............PK.DDDSDNDDDSSSSS.....SKAPPCAAAAAAAADS............", // 20
        "..............SD.DKSSDDDDDSSSKSS..KSSKAPPAD.KSPPS...............", // 21
        "..............KD.KSSSSDDDDKSSSDDKNNNNNAPPS......................", // 22
        "...............KSSKDSSSDDDKSDSKSDNNNNAAPCP......................", // 23
        ".................SDDSSSSDSKSSSDDNNNNAAAPCA......................", // 24
        ".................KDSDDDSDSSSSDDDDNNAAACCPCK.....................", // 25
        ".................DDDDDDSDDDDSDDDDNAAAAACPCA.....................", // 26
        "................SDSDDNDDDNDDDDDDDNAAAAACPCCK....................", // 27
        "................DDSDDNNNNNSDDSDDNNAAACAACPPAK...................", // 28
        "...............DDDDDSDDNNSDSKDDDNAAAACAACPPCCK..................", // 29
        "..............SDISISSSSSDSKDSDDDNAACACAAAPPCCAS.................", // 30
        ".............KADDDSDDDDSDDSSNDDDAACCAAAAACCPPCCA................", // 31
        ".............SANDDDDDDDDDSDSDDDAACrPAAAANACPPPCAAK..............", // 32
        ".............DANDDSSDDDNDDDDDDNAAPPrAAAANNCCPPPCAAK.............", // 33
        ".............AADSISDDDDNDDDDDNNACPCAAANNNNACPPPCCAAK............", // 34
        ".............ANNIDDDDDDDDDDDNAAPPPCANNNNND.ACPPPPCAA............", // 35
        ".............DNDDDDDDDDDDDDNAPPPPrAANNNNN..KCPPPCCCAA...........", // 36
        ".............DNSMDDNNNNNNDAAPPPPPAANNANNS...PCPPCCAAAS..........", // 37
        ".............DDSMDDNNNNAAAAPPPPPAANNNNND.....APPCCCAAA..........", // 38
        ".............KDMMDDNNNAAAAPPPrPAAANNNNN......KCPCCCAAAS.........", // 39
        ".............KSDDDNNNAAAAPAPPCPAANNNNNK.......ACPCCAAAD.........", // 40
        ".............SDDDNNNNAAAACAArANNNNNNNK........PCPCCAAAN.........", // 41
        "............SSSDDNNNNNNAAAAAANDNNNNNK.........SCPCAAAANK........", // 42
        "............SDDDDNNNNNNNNNNNNNNNNND...........SACCAAAANK........", // 43
        ".......K.KDDIDDD.SNNNNNNNNNNNNNNNS............ACCCAAAANS........", // 44
        ".....KDDDDDSSDDD...DNNNNNNNNNNNNND............ACAAAAAANS........", // 45
        "....KDDDSDDDDDDS.....SNNNNNNNNNNNNS..........SCCAAAAANNK........", // 46
        "....SDSDDDDDDDDK......DNNNNNNNNNNNNK........KAAAAAAANNNK........", // 47
        "....KDDDDDDDDDD.......KNNNNNNNNNNNNNS......AAAAAAAAANNN.........", // 48
        "......KSDS.DDDK........SNNNNNNNNNNNNNS..KSAAAAAAAAANNND.........", // 49
        ".........KDDDDNNNNNNNNNNNAAAAAAAANNNNNNNNNAAACAAAAANNNS.........", // 50
        ".......KAAAAAAAAAAANNNNNNAAAAAAAAAANAANNNNNNAAAAAANNND..........", // 51
        "......SACCCCPCCACCAAAAAAAAAAACCAAAAAAAAANNNNNNAAANNNNK..........", // 52
        ".....KACCCCPCCPPCPPPCCCPCCCCCCCCCAAAAAAANNNNNNNNNNNNS...........", // 53
        ".....DAACCCCPPCPPCCPPCPPPPCCCCCCCCAAAAAANNNNNNNNNNND............", // 54
        ".....DAAACCCCPPPCPPPCPPPCPPPPCCCCAAAAAAANNNNNNNNNNS.............", // 55
        ".....DNAAACCCCCCCCCPPPCPPCCCCPPCAAAAAANNANNNNNNNDK..............", // 56
        ".....DNNNAAAAAACCPPCCCCCCCCCCCCAAAAANNNNNNNNNNDK................", // 57
        ".....DNNNNAAAAAAAAAAAAAAAAAAAAAAAAANNNNNNNDDS...................", // 58
        "......DNNNNNNNNNAAAAAAAAAAAAAAAANNNNNNNNN.......................", // 59
        "......SDNNNNNNNNNNNNANNNNNANANNNNNNNNNNNK.......................", // 60
        "........DDNNNNNNNNNNNNNNNNNNNNNNNNNNNNDK........................", // 61
        "..........KSDDDDNNNNNNNNNNNAANNNNNNNDK..........................", // 62
        "..................KKKKKKSSKSKKKKK..............................." // 63
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

    drawMonsterMatrix(flip(matrix), palette)
}

fun DrawScope.drawKuja() {
    val matrix = arrayOf(
        ".....................iII........................", // 0
        ".....................iII........................", // 1
        "......................mmmm......mi..............", // 2
        "......................iiIIm....mIm..............", // 3
        "......................iiIIm....mIm..............", // 4
        "..............DKKKKMMIiiIIMKKiimIm..............", // 5
        "..............DKKKKMMIiiIIMKKiimIm..............", // 6
        "...........D.D..DDDIImiiIIISSiiIMI..............", // 7
        ".........DKKKK...00SSTWWMMmWWiiDKD..............", // 8
        ".........DKKKK...00STTWWMMmWWiiDKD..............", // 9
        ".......KKD0ST.I0000WWmMMWWTSSKKD0DKK............", // 10
        ".......KKD0S..I0000WWmMMWWTSSKKD0DKK............", // 11
        ".......KKSS...DDDSSmmmIImmIDDDD00DKK............", // 12
        ".......KKS..00KKDSSIMITTMMBKK0000DKK............", // 13
        ".......KK..m00KKDTTIMISSIIMKKII000KK............", // 14
        ".......KK...00QQQFFS0000WWT00TTS0000DKD.........", // 15
        ".......KK...00QQEFFS0000WWT00TTS0000KKD.........", // 16
        ".....DD....0KKQEEFFQQS00TTSKKWWS0ImSDKD.........", // 17
        "....DKK...0DKKEFFFFQQS00TTDKKWWS0ITTDKD.........", // 18
        "....DKK..0DDKKQEESSPPSSSSSDKKTTS0ISSDDD.........", // 19
        "....DKK..DKKKKPQPKKKKDTTKKD00TTS0DKKST.KK.......", // 20
        "....DKK..DKKKKPQPKKKKDTTKKD00TTS0DKKS..KK.......", // 21
        ".......KKD0DKKQESIITTSKK00SmSKKD0DKKKKD.........", // 22
        ".......KKD0DKKEFTIIWWSKK00STTKKD0DKKKKD.........", // 23
        ".......DD..DKKEFTIIWWSKKSSSSSKKDDDKKKKD.........", // 24
        "............KKEFTIIWWT00TTDKK00KKK00KKD.........", // 25
        "............KKEFTIIWWT00TTDKK00KKK00KKD.........", // 26
        "............KKEFFTTFFSKKKKKKKTmDKKKKD0DDD.......", // 27
        "............KKEFFFFFFSKKKKKKKTTDKKKKD00KK.......", // 28
        "............KKSSEFFSSPNNKKKKKSSDKKKKDDDKK.......", // 29
        ".........DKKDDKKDFFNKA33AANKKKKDDDDDKKK00KKD....", // 30
        ".........DKKDDKKDFFKKA33AANKKKKDDDDDNKK00KKD....", // 31
        ".......KK...KK11AKKKKN11NNKKKNNNNDDDDDDKKKKD....", // 32
        ".......KK...KK11AKKKKN11NNKKKNNNNDDDDDDKKKKD....", // 33
        ".......DD..DKK121KKDNA22KKKKKNNC11PPDDDKKKKD....", // 34
        "....DKK..NKKND231NNDDP33KKKKKAA23211ANKKKKKD....", // 35
        "....DKK..DKKKK231DDDDP33KKKKKAA23211ANKKKKKD....", // 36
        "....DKNFFQQPKKAAPSTWWSKKFFFFFSSTTTWWPANKK.......", // 37
        "....DKNFFQQPKKNAPSSWWSKKFFEFFSSSSTWWPANKK.......", // 38
        "....DKNEEQQPKKKKDQQFFSKKFFEQQTTTTFWWDKN.........", // 39
        "....DKNQQQQPKKKKNQQFFSKKFFEQQWWWWWWWDKD.........", // 40
        "....DKNQQQQPKKKKNQQFFSKKFFQQQFWWWWWWDKD.........", // 41
        ".......KKKKDTTKKKKKQQAKKQQQQQWWTTTSTDKD.........", // 42
        ".......KKKKDTSKKKKKQQAKKQQQQQWWTSSSSDKD.........", // 43
        ".........KKKSSKKKDDDADDDAAAAAWWTSSDD............", // 44
        ".........DKKDDKKDSTDNDDDKKKKKWWTSSKK............", // 45
        ".........DKKDDKKDSSDDDDDKKKKKFFTSSKK............", // 46
        "............KKSTSDDQQPDNNNSWWKKKKDS.DKD.........", // 47
        "............KKSSSDDQQPDNNNSWWKKKKD..DKD.........", // 48
        "..............DKKKKEESKKEESDDTTQ2Q.....DD.......", // 49
        "..............DKKKKEFSKKFFSKKWW434.....KK.......", // 50
        "...............DDKKSSDKKQEDKKTTE44.FEQ.KK.......", // 51
        ".................KKKNKKKDDDDDKKSW.33332KK.......", // 52
        ".................KKKNKKKDDDDDKKS..33332KK.......", // 53
        ".................KK11AKKDDDDDDKKKKKKSFF11.......", // 54
        ".................KK11AKKNNDDDNNKKKKKSWF11.......", // 55
        ".................KKACNKKDACPPDDKKKKKDSSPP.......", // 56
        "..............DKKDDDNKKK11233DDDNKKKKKKS........", // 57
        "..............DKKDDDNKKK11233DDDNKKKKKK.........", // 58
        "..............DKKKKKKKDDSSSDDKKDDDKK...KK.......", // 59
        "..............DKKKKKKKDDSSSDDKKDDDKK...KK.......", // 60
        "...............DDDDKKKDDSSDDDKKKDNKK...DD.......", // 61
        "...................DKKKKKKKKKKKKKKKK............", // 62
        "...................DKKKKKKKKKKKKKKKK............" // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        '3' to Color(0xFFFFDC23),
        '4' to Color(0xFFFFF56E),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(flip(matrix), palette)
}

fun DrawScope.drawTranceKuja() {
        val matrix = arrayOf(
            "................................................................", // 0
    ".....................................T..........................", // 1
    ".....................................T..........................", // 2
    ".....................................P..........................", // 3
    "....................................WP..........................", // 4
    "....................................TAT.........................", // 5
    "....................................TAT.........................", // 6
    "....................................qCW.........................", // 7
    "....................................PR..........................", // 8
    "...................................WCNW.........................", // 9
    ".............................PrPSAQQRPS.........................", // 10
    "..........................T.QRRRRRRCrTQ.........................", // 11
    "..........................T.RNRRRNRRTCT.T.......................", // 12
    "...........................DNNRRNRARAPWSD.......................", // 13
    "...........................SRNRRRRNRRSPCT.......................", // 14
    ".........................TSPNNRRRNNRAAPS........................", // 15
    ".........................TANRRNRNNNRRRS.........................", // 16
    "..........................SARRSSSSANNRPTW.......................", // 17
    "...........................rNASTTPANRRAST.......................", // 18
    "...........................rRRATSDANAA..........................", // 19
    "...........................rAAQTTTCAST..........................", // 20
    "..........................PNNRASTTARS..TW.......................", // 21
    "......................WTTTPNNNNSSNNNSTPSrSSW....................", // 22
    ".....................TAASPASDDANNDDSSDAPSrRr....................", // 23
    ".....................rrPQARATSNNRDSSNNRrrAPAT......WTW..........", // 24
    "....................TAARrRRNSSTNDTPPAArANNNAS..TTTTTWW..........", // 25
    "....................WANNRRAAADTSSSDNANNRNNNNT.SSSSSTTTW.........", // 26
    ".........TSSTTSSTT...TNNNNRNNNSSDDNRNNNNNNATSDSSTSSTTTT.........", // 27
    ".........TTTTSSDDSSTWWSNNNSAANNNNNNARNTSrDSSSSSSTTSDSWT.........", // 28
    ".........TSSTTSSSSSTSTSDDTWSNRNNNNRRNDWWWSDSSSSSDTTDSST.........", // 29
    "........WTSTTTSSSTWTSSSSTWWWANNDDNNNATWWWDDDSSSSDDTSDS..........", // 30
    "........TSSTTTSSSSSDSSSDTWWWTSASSARPTTWWTDDDTSDDSDTSST..........", // 31
    "........WSTTTWSSSTSDSSSSTTSSTSASNRATSAWWWSDDSDNSTWTTSW..........", // 32
    ".........WTTSTWSDSDDTTTWWSTTSDNRNNSTTTSTWWDDSDSTTWTT............", // 33
    "..........TTSSTTTTTTTWWWWSTSSDNRNKDTTSTWWWWArTSTWWWTTT..........", // 34
    "............TTTTSTTSTTWWSSSDSDNNDSDDTTTTWWFCrTSSWWTTSSS.........", // 35
    "................TTTSTWWTSDKAQSNNSSSSSTTTWTSTSDDSTTT.............", // 36
    ".....................TSDKKSQPSNNDDDTTSSTSSSTSSTTTST.............", // 37
    "...................TDDDDNSTFPSNNDTTTTSSSTSSTTTT.................", // 38
    ".................TSDDDDDSrSFTSNNNSTTSDTSSSWWWTS.................", // 39
    "................SSTSDDSNDASSSSKKKSTTSDDTSSWWWWT.................", // 40
    "................S.....TNNNDNSNKKKSSSSSDSDSSTTT..................", // 41
    "................T.....SNRNRASDNDDNDDDDSTSSNDT...................", // 42
    ".....................TNNNNRDTSDDDDDSDDDDSTSSWS..................", // 43
    "....................TDNRNNS.TSDDDDDDDDSDSTSTST..................", // 44
    "....................NNNNNS..TSSDSSSNDDDDSSSTW...................", // 45
    "...................SRNNRP....TSDS..NDSSDTSS.....................", // 46
    "..................TNPCrNT........T.SNDDSTSDS....................", // 47
    "..................PNRARS...........TNDSDSSND....................", // 48
    ".................SRNNNS.............NNWTDDNNT...................", // 49
    ".................SNAAr..............DNSTSDrRNW..................", // 50
    ".................AARP...............TKATSTTAND..................", // 51
    "................TNRS.............WNrTKNWWS.TNND.................", // 52
    "................SNNT..............DNTNNT....SKNS................", // 53
    "................DAAT..............WARNKS.....NNNNS..............", // 54
    "...............TDDT................TRNKS.....SKNNAS.............", // 55
    "...............SSSW.................SNNT.....TNNANrT............", // 56
    "..............TTST............................NRRRNr............", // 57
    ".............TDTTT............................PNRRRA............", // 58
    ".............SRNST............................TNNANS............", // 59
    "..............DNA.............................TNNNPT............", // 60
    "..............TST..............................ANAAT............", // 61
    "...............................................TAAS.............", // 62
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

    drawMonsterMatrix(flip(matrix), palette)
}

fun DrawScope.drawNecron() {
    val matrix = arrayOf(
        "...............................II...............................", // 0
        "...........................I0...m..0............................", // 1
        "......................DD..0..........I..DD......................", // 2
        "......................KD0..............IDK......................", // 3
        "......................D0D..............DID......................", // 4
        ".....................D.DDK............KDD.D.....................", // 5
        ".....................D.DDDD..........DDDDID.....................", // 6
        "....................D.DKDDD...0II0...DDDKD.D....................", // 7
        "....................D.KKKDDD0......0DDDKKK.D....................", // 8
        "....................D.KDKKD0...m.m..0DKKDKDD....................", // 9
        "...................D.DKDKKDD........DDKKDKD.D...................", // 10
        "..................DD.DKKDDSD.D....D.DSDDKKD.DD..................", // 11
        ".................DDD.DDSSSS..DD..DD0.....DD.DDD.................", // 12
        ".................DD..DSTTSD.0KD..DDB......D..DD.................", // 13
        "................DKDDDDDSSSDD.DDDDDDD0D.m..D.DDKD................", // 14
        "................DDDKDDDSSSDDDDDSSDDDDD...DDDKDDD................", // 15
        "................KDD0KIK000SDDDSSSSDDD.I00KDKDDDK................", // 16
        "................KDDDKDDDS00DDDDDDSDKDD0SDDDKDDDK................", // 17
        "................KDKDDKDDSSSSTSSTmSSDSSSSDDDDDKDK................", // 18
        "................DDKDDKDDDSSSSSmS00SSSSSDDDKDDKDD................", // 19
        "................KDKDKKKDDDD0SSS00SSS0DDDDKKKDKDK................", // 20
        "................KDKDDKKKDDDKDSTSSTS0KDDDKKKDDKDK................", // 21
        "................DKKDDDKKKDDDD0DDDDDDDDDKKKD0DKKD................", // 22
        "................DDKKDSKKKKKDDKKKKKKDDKKKKKIDKKDD................", // 23
        "..................KKKKKKKKKDDDBI0BDDDKKKKKKKKKS.................", // 24
        ".................DDKDKKKDDKTKD0000DKTKDDKKKDKDD.................", // 25
        ".................DSDDKKKDKSTKD0000DKTSKDKKKDDSD.................", // 26
        ".................DSDKKDKKDSWD00DB00DWSDKKDKKDSD.................", // 27
        ".................DDDKKKKKDSTKDDDDDDKTSDKKKKKDDD.................", // 28
        "................DDDDKKKKKKTTDKDSS0KKSTKKKDKKDDDD................", // 29
        "...............DDDDDKKBKKKDS0D0SSSD0SDKKKKBKDGDDD...............", // 30
        "..............DKKDKDKKKKB0D0DS0SSS0D0D0BKKKKDKDKKD..............", // 31
        "..............DDDKKDDKDSSSDDTSSSSSSTDDSSSDKDDKKDDD..............", // 32
        ".............DDDDKDDDKDSSSSDSSSSSSSmDSSSSDKDDDKDDDD.............", // 33
        "............DDDDKKDDDD0DDDDSIS0SS0SSSDDDD0DDDDKKDDDD............", // 34
        "............DDDDKDDDDDBKDDDSSSB00BSISDDDKB0DDDDKDDDD............", // 35
        "............DDDKDDDDD0KKKKDB00DDDD000DKKKK0DDDDDKDDD............", // 36
        "...........D.DDKDKDDD0KKKD0SS00B000SSSDKKK0DDDKDKKD.D...........", // 37
        "...........DDDKKDDDKD0KKDSSSSSm00mISSSSDDKDDKDDKKKDDD...........", // 38
        "............DKKDDDDDDDKSDDDD0SVSSSI0DSDDSKDDDDDDDGKD............", // 39
        ".............KDDDDDDD0KSDDDDSW0000TTDDDDSKDDDDDDDDK.............", // 40
        ".............KDDKKD.DSKSKSSDSW0000TSDDSKSKSD.DKKDDK.D...........", // 41
        "...........DDDDDGK..D0KSKTWDBSSUUSSDDWTKSK0D..KKDDDDD...........", // 42
        "...........DDDDDDD...DKSDDWTDKSIUSDKTWSDSKD...DDDDDDD...........", // 43
        "...........DDDD.......DDTNSWTDDSSDKTWSKTDD.......DDDD...........", // 44
        "...........DDD.........KSTDDTSD0IDSTDDTSK.........DDD...........", // 45
        "...........DDD..........KSWSDKD0DDKDSTSK..........DDD...........", // 46
        "...........DD............KDTWSDDDDSWTDK............DD...........", // 47
        "...........DD.............DKDDD0SDDDKD..............D...........", // 48
        "................................................................", // 49
        "...............................m................................", // 50
        "................................................................", // 51
        "................................................................", // 52
        "................................................................", // 53
        "................................................................" // 54
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'B' to Color(0xFF1E3782),
        'D' to Color(0xFF4B4B4B),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawKlikk() {
    val matrix = arrayOf(
        "................................................................", // 0
        ".................................................DDDDDD.........", // 1
        "..............................................DDDDDDDDDD........", // 2
        ".............................................DDD.........D......", // 3
        "............................................DD..................", // 4
        "..........................................DDD...................", // 5
        ".........................................DDD....................", // 6
        ".........................................DD.....................", // 7
        "........................................DD......................", // 8
        ".......................................DDD......................", // 9
        ".......................................DD.......................", // 10
        "......................................DDD.......................", // 11
        ".....................................DDD........................", // 12
        ".....................................DDD........................", // 13
        "....................................DDD.........................", // 14
        "....................................DDD.........D...............", // 15
        "...................................PDDD.........D...............", // 16
        "...................................DDDD.........D...............", // 17
        "...................................DDDD.........DD..............", // 18
        "..................................ADDD..........DD..............", // 19
        "........................D.........ADDD..........DD..............", // 20
        ".......................D..........DDN...........DKD.............", // 21
        "......................DD..........DNN...........DKK.............", // 22
        ".....................DK........DDDDDD............DKK............", // 23
        "..................D.DKK........NDDDDD............DKKD...........", // 24
        ".................DDDDKDD......DADDDDDD..........DDDKKD..........", // 25
        "................NDDDKDDDDD...DDADDDDDDD..........KKKKKD.........", // 26
        "...............DDDDKNDDNNNDDDADDDDDDDND..........KKNNNK.........", // 27
        "..............NNDDDDDNNNNNNNADDDDDNDDNNDD.......DKNNDDDD........", // 28
        ".............DDNDDDNKKNNDDDNNDDDNKDDDNNDD.......KKNNNNNK........", // 29
        ".............NNDKDNKDDKNDNNDDDNNKKNNDDDNN......KKD.DKDDK........", // 30
        "............NKDDDNKDDKKKKNDDNNNNNNNNDDNND.....DKD....KKKD.......", // 31
        "...........DKNDDDKNNKKKNNNNNNNNNANNKDNKKD....DKD......NNK.......", // 32
        "..........DNDDNDNKKKKKKKKKDNNNNNNKKNNNDKD...DDK.......DNND......", // 33
        ".........DDDDNDNNKKKKKKNKND.KNNNNKK.NNDKK..DDK.........KNDD.....", // 34
        "........KNDDDNDNDNKDDNKD....NKNNNN...KNNK.DDDN.........DDDKD....", // 35
        ".......KDNNNKKND............NKKKD....DKKKDDDD...........NDDKKD..", // 36
        ".....DKNKDDNKD.............DKKNKD.....NGKKKDN...........DKKDKKK.", // 37
        "....DKNNK..D...............KKDNK......DKDKDDD............KDDDKKD", // 38
        "....KDDK..................KKNKNN.......KDKDN.............DKDDGK.", // 39
        "....DNKD..................DNDKKD.......DDKKD..............KNDDK.", // 40
        "...KKKN...................N.DKK........DDDKDD.............KKNDK.", // 41
        "..DKK....................D..KKD.........NDKKDD.............KKDK.", // 42
        "..KG........................DK.........DNDKKKDD............KKKD.", // 43
        ".DD.........................D..........DDKKKKKD............DKK..", // 44
        ".........................................DDNKKK............DKK..", // 45
        ".........................................DDDKK..............D...", // 46
        ".........................................DDKKK..............D...", // 47
        "..........................................GKKK..................", // 48
        "..........................................DKKK..................", // 49
        "...........................................KKK..................", // 50
        "............................................KK..................", // 51
        ".............................................D..................", // 52
        "................................................................", // 53
        "................................................................" // 54
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'D' to Color(0xFF4B4B4B),
        'G' to Color(0xFF0F5019),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41)
    )

    drawMonsterMatrix(flip(matrix), palette)
}

fun DrawScope.drawOblitzerator() {
    val matrix = arrayOf(
        "................................................................", // 0
        "..................................DAAAANNAD.....................", // 1
        ".....................AAADDD.............DDAAAAAAD...............", // 2
        ".................PNDD....D..D...DDDD..DD.......DD...............", // 3
        "...............AD....DD..DD.DD..DDDD.DDD.DDD..D.................", // 4
        "..................DD.DDD.DD.DD..DDDD.DDDDDD..DD.DD..............", // 5
        "...............DD.DDD.DDDDDDDD..DSDD.DDSDDD.DDDDDD..............", // 6
        "...............DDDDDD.DDDDDDDD..DDDDDDDSDDD.DDDDDD..............", // 7
        "..............DDDDSDDDDDDDDDDD..DDDDDDDDDDDDDDDDDD..............", // 8
        "..............DDDDDDDDDDDDDDDD..DDDDDDDDDDDDDDDDD...............", // 9
        "...............DDDDDDDDDDDDDDD..DDDDDDDDDDDDDDDDD...............", // 10
        "...............DDDDDDDDDDDDDDD..DDDDDDDDDSDDDDDD................", // 11
        "................DDDDDDDDDDDDDD..NDDDDDDDDDDDDDDD................", // 12
        "................DDDDDDDDDDDDDD..KDDDDDDDDDDNAAADDPP.E...........", // 13
        ".................DDDDDDDDDDDDD..KDDDDDAANDDDDANDDNANADDNAP......", // 14
        "....AAADDD.......DDDDDDDDDDDNDDDKDDDNNNDDDDDDDDGDDDDNDDDNDDDP...", // 15
        "...DDDDNNNDD.....DDDDDDNDDNDDDDDKDDDKKNGDDGDDDDDDGDDDDKDDDDDDD..", // 16
        "...DDDDKKKKKD.....DDDNDDDDDDDDDDKDDNKKDDDDPSDDDDDSDDDDNKGGDDDD..", // 17
        "..DDDDDKKKKKKND...DDDDDDKDDDDDDDKNDNKKKDDAAAAAADDQESDDSDDDDDDD..", // 18
        "..DAADDKKKKKKNKKKKNDDDNDDDDKDDDKKNDNKKDDDDDDDDAADDAADDDSQSDDDDD.", // 19
        "..AADDDKKKKKKKNNKNNDDDDDDKDDNDDKKKDKNNDDDSDDSDANSSDSDADDAADDSS..", // 20
        ".DDDDDDNNKNKNKKKKKKKDDDNDDDDGKKKKKNDNNDDDDDDDDDNDDDDDADSDDDADDP.", // 21
        ".DDDDDDDDNNNNKKKKKKKNDDDDKKKKKKKKKKKNDDDtDDDDDDDDDDDDADDDDDADDAD", // 22
        ".DDDDDDDDNDNDKKKKKKKKKKKKKKKKKKGDDKNDDDDDDDDDDDDDDDDDDDDDDDADDD.", // 23
        ".DDDDDDDDNDDDKKDDNKDDKKKKKKKKKNDNNKNNDDDDDDDGDDDDDDDDDDDDDDDDDD.", // 24
        ".DDDDDDDDKKDDKKKKKKNDKKKKKKKKKKNKKKKDDDDDDDKNDDDDDDKDDDtDDDDDDD.", // 25
        ".DDNGDDDKKKKDKKKKKKKKKKKKKKKKKKKKKKNKDNKNNNNAADANDDNDDDDDKDNDDD.", // 26
        ".DANNNDDDDDDDKKKKKKKKKKKKKNNDDDDDDDDDDDDDKKKKNNNNNNAAAANGNADDND.", // 27
        ".NKKKDDDDDDDDKKKKKKKKKKKKKKKKKKKKKKDNDNDDDGKKKKKKKKKKKNNNNAANAN.", // 28
        ".KKKDDDKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKNNDDDDKKKKKKKKKKKKKKKKKNN.", // 29
        ".DKKDKNKKKKKNKKKKKKKNDDDDDDKKNKDDDDKKDKNKKKDDNGGKKKKKKKKKKKKKKK.", // 30
        "..DDDNNNKKNKKKKKKKKDDDDSSDDDDDDSSSSDDDDNNNNDNNDDKGKNGKKKKKKKKK..", // 31
        "..DNNNNKKKKKKKKKKDDDGDNDDGDDDDDSNDSDDGDDDNNNNNNNNKNNDDKDKGGKGD..", // 32
        "...NKKKKKKKKKKKDDGDGDDNDtGDDDGDDDDDGDKGDDDDDKGNNNNNNKKNKNDKND...", // 33
        "...DKKKKKKKKKD..DDGDDDDDGNDDDKDDDDDDKKKDDDDDKKDDDDKDDNNNNNNNN...", // 34
        "................DKKDSSDDKNDDKKDSSSSDKKKDDSSSDKKDSS.........DD...", // 35
        "...............ADKKDCADKKKDDKKDACADDKKKKDACADKKGDAD.............", // 36
        "...............NDKKACANKKNDDKKNAPCNDKKKKDACANKKGNAN.............", // 37
        "...............NNKKNDNNKKDDDKKNNDDNNKKKKNNDDNKKKNDN.............", // 38
        "...............NKKKKNNKKKDDDKKKNDDKKKKKKKKNNKKKKKND.............", // 39
        "...............DKKKKKKKKKNDNKKKKNKKKKKKKKKKKKKKKKD..............", // 40
        "..............DD.KKKKKKKKKDKKKKKKKKKKKKKKKKKKKKN................", // 41
        "............DD...KKKKKKKKKKKKKKKKKKKKKKKKKKKDNNN................", // 42
        ".................NDKKKDDNKGGKNNKDDKKKKKKKKKKDDDDDD..............", // 43
        ".................DNNKKDDNNKKKNDNDDNKKKDDNKNNDDDND...............", // 44
        "..................KKNKKNNNKKKDDNDDKKKNNKNDKDNDDD................", // 45
        "....................KKKKNDKKDDD..NKKKNKKDN......................", // 46
        ".................NNDDNKKNKKND....DNKKKKKN.......................", // 47
        "................DKDDNDKNDDKD.......KKKKNDDNAP...................", // 48
        "................DKNKKKKD...........NKSDDNDDNCP..................", // 49
        ".................NNNKKK............DNSSKKDNKAD..................", // 50
        "...................NNK.............DDDDKNKNKA...................", // 51
        "...................NKD.............DND..NNN.....................", // 52
        "..................DND...................NNND....................", // 53
        "..................DN.....................NDD....................", // 54
        "...........QPADDDDN......................NKD....................", // 55
        "..........................................DD....................", // 56
        "...........................................DDPDDP...............", // 57
        ".............................................PPPPP.............." // 58
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'G' to Color(0xFF0F5019),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        't' to Color(0xFF0A6469)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawSeymour() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        "...............S................................................", // 2
        "........TDT...TD................................................", // 3
        "........TND.T.DD................................................", // 4
        ".........DNTD.TS................................................", // 5
        "...T.T.STDDDDTTT................................................", // 6
        "....STTWDSDDTDDTTSTTST...............T0......S0T................", // 7
        ".....TSWDTDDDSSTTDDDTST.............S0USS0US..T0UT..............", // 8
        "......TSDDDTSSDWTSDSS..............T000000U0S..TUUS.............", // 9
        ".....TWSSDTSDWPDDDDT.............WSUBDDDSS000000S0UT............", // 10
        ".....TTTWDWDTDS..DT................00WDTFFSD0BT..T00............", // 11
        "....TTSSSSDSDT...S.................USSDFDSTD000S..TUS...........", // 12
        "...DSTSDT...SS.....................STSDTFFFS00S....0U...........", // 13
        "..TDS.STS...TST....................STSTTFFFS0DT....TUS..........", // 14
        "........TT...DS...................TSTSDDFFSS00KNDDDTUU..........", // 15
        ".............SDS..................DGKDKDDTFTS0DKDDNSTUT.........", // 16
        "..............SSST................DDKKDDSFFFFSDDDDKT.US.........", // 17
        "..............SSS.ST.............DKDKSDSPSSSSTSDNKKD.SU.........", // 18
        "..............TDTS..ST..........NKKDKNDSSDDDDDDDKKKK.TV.........", // 19
        "..............TS.TS...SS.......SKKKDKDSDFDNDDDDKKKKKSiV.........", // 20
        "...............SS..T....SSSS...DKKKDDDDDTSDDDKKKKKKKS.VT........", // 21
        "...............SS...S.....TT...NKKKDKDDDSSDDNKKKKKKKS.VT........", // 22
        "................ST...S........SKKKKDNFDFSKDSKKKKKKKKD.SS........", // 23
        "................SS....QF......DKKKKNNEDFQNNDKKKKKKDKD.TS........", // 24
        "................TST....QS....DKKKKKNNDNQSANDKKKKKKDND..T........", // 25
        ".................DS.....T.TSDKKKKKKNNTDTADNNDKKKKKKDK..TT.......", // 26
        ".................ST.......KKNKKKKKKANTSSrNANNKKKKKKNNS.TT.......", // 27
        "..................S.....TNKKKKKKKKNADSSAAANDNKKKKKKNDN..T.......", // 28
        "..................SS...SNKKKKKKKKKNADDSAAANANKKKKKKNDKT.T.......", // 29
        "...................S..SNKKKKKKKKKKDANDNAAAANDKKKKKKKNK..T.......", // 30
        "...................STDNKKKKKKKKKKNAAKDAAAArNDKKKKKKKKK...T......", // 31
        "...................STNKKKKKKKKKKKKDANDANAAADNKKKKKKKSN...S......", // 32
        "...................TTDKKKKKKKKKKKKNNDAArNDNNDKKKKKKKDKT...T.....", // 33
        "...................TSSDKKKKKKKKKD.SKDDDDKKSSDKKKKKKKDKD...T.....", // 34
        "....................TS.SNKNNNNKS.SDDDSSSDSSDKKKKKKKKKNNT........", // 35
        ".....................D..TDNKNS..SDSDSSDSSDDSDKKKKKKKKNDD...T....", // 36
        ".....................ST........DDSSDSSSSSSDDDDKKKKKKKKDKS.......", // 37
        ".....................TD........SSSSKSDSSSSSGSSKKKKKKKKDNS.......", // 38
        "......................D........SSSSKKSSSSSSSDSGKKKKKKKKND.......", // 39
        "......................TS.......DSSSKKSSSSSSSDSGKKKKKKKNDS.......", // 40
        ".......................D.......SSSSKSSSSSSSSSSDKKKKKKKDKD.......", // 41
        ".......................DT.....TSSSSNKDSSSSSSSSDKKKKKKKDKT.......", // 42
        ".......................TS.....SSSSSDKKSSSSSSSSSKKKKKKKKNS.......", // 43
        "........................D.....SSSSDDDNDSSSSSSSSKKSNNNKDDT.......", // 44
        "........................ST....SSSSNDDNKSSSSSSSSDKTTTSST.T.......", // 45
        ".........................D...SSSSSDNDDKNSSSSSSSSKST.TT..........", // 46
        ".........................D...SSSSSDNKKKKSSSSSSSSKD..............", // 47
        ".........................SS.TSSSSDSNNDKDNSSSSSSSDKT.............", // 48
        "..........................D.SSSSSNSKDDKDKSSSSSSSDSD.............", // 49
        "..........................S..SSSSDSNNDKDDDSSSSSSDDD.............", // 50
        "..........................TS.DSSSDDDDNNDDNSSSSDDSDSS............", // 51
        "...........................D.DDDDDNDDKDNDNSSGSSKDKDD............", // 52
        "...........................SSSSSDSNNNKNNDNSKKDSDKKKDT...........", // 53
        "...........................TDDSSSSKNDKNDDDKKKKSSKDDDS...........", // 54
        "............................DDDSSDKADKNPDDKDDKDDDSKDS...........", // 55
        "............................SDSDSNKCPKNQNDDKDSDKSSDKK...........", // 56
        "............................SSDKSNKPPKNQDDDKKSDKSSDKKD..........", // 57
        "...........................DDDKDDDKPPKNQDNDKKKSSSSDKDSS.........", // 58
        "..........................SNKKKDDDKPQKNQNNDDKKDDKDDDSSD.........", // 59
        ".........................SSDKKDDANKPQKNPNrDDKKKDKSDSDSSDT.......", // 60
        "......................TDDDNKKKDDANKASNNSNANDDKKKKSSSSDSSSST.....", // 61
        "....................STSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSST....", // 62
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

    drawMonsterMatrix(flip(matrix), palette)
}

fun DrawScope.drawJecht() {
    val matrix = arrayOf(
        "................................................................", // 0
        "............................T....T..............................", // 1
        "............................NTTTTKT.............................", // 2
        "............................DNDNKNKT............................", // 3
        "..........................TDKKKNNKNKT...........................", // 4
        "........................DNNNDNDNNNKKS...........................", // 5
        ".........................SNDNNNACCKNNS..........................", // 6
        "........................DNNNNNQQQSNNKS.SSS......................", // 7
        ".......................DKKDENEAPQACKDSSSS.......................", // 8
        "........................KNNKNDEQQPCDDSS...SSSSS.................", // 9
        ".......................NNNNNQADPQQRDDDD.SSSSS..DSTSS............", // 10
        ".......................NKNDQQPADDDNSSDDDSSSS....TSSSSST.........", // 11
        "......................NKKDDDQQQPNNNSDDDDDSSSSS.....TSTTTS.......", // 12
        "....................TQPPQQPDDDDDSDDDSSSDDDSSSSS.TTSTSTTTSD......", // 13
        "...................EQPQQPQPPPPPDDDDDDDDDDDSS...TSS.TSSST........", // 14
        "...................SQQQQPPPKKEQQQQDDDSSDD...S..DSSTSSTTS........", // 15
        "...................EQQQAPPPSKKKDQQKKPSSDDST....DSSTSSDDT........", // 16
        "..................QQQQQPAADKQQQSKQKEDKKKDST...TTSDSSSTT.........", // 17
        ".................EQQQPPPDDDSKKQQQKSKPDKKK...STSTSSSSS...........", // 18
        "................EPQPQQQDDDDDDQPDKSPQQDKSDKKKDDTSSSSS............", // 19
        "...............FPQPDAAADDDDADDPPSKPDNDDKKDDDSDDDDDDDDT..........", // 20
        "..............TQQQPQPD.NDDDASPPPSSKQDDSKKDDDTSDSDDDDS...SD......", // 21
        "..............SPPPEQSS...SDDDPPDNQKATSKSDSTTSDDDDDSDDDSS..DS....", // 22
        ".................PPESSS...TDSPANKPKSST...KSTTDDDAPDDDSSSD.ST....", // 23
        ".................TPSSSSS...TDAPPSNQPPT...KSTSDDCANDDDSSTDST.....", // 24
        "..................TSSSSSST..DDSPPSQPPT...TTTDDDCADDDDSS.TD......", // 25
        "...................TTTSSSSSTDDDDDSSSDSS...TSDDDCADDDDST.TS......", // 26
        "......................TTSSPQSDDDDDDSDDD...SSDDAADDDDDTT.TS......", // 27
        "........................QQQQQDSDDDDDDDDT..SSADXNDDDDS....STT....", // 28
        "........................TQQQQDTSDDDDDDDS..SSADXNDADDS....DTT....", // 29
        ".........................TQQQPSSDDIDDSSS.TSSDDCNDDDDS...TS......", // 30
        ".......................FESSEEQPSSDSNSSmD.TSDDNDCADDDT...TS......", // 31
        ".......................QEDDEQQQQSDNDSDDD.TDDNXDCDDDDT....STT....", // 32
        ".......................EQPPEEQQPPDDDSSDDDSDDNCNDDDDS.....SDS....", // 33
        ".......................EEEQNEEPDDNTSDDDDDSDNPDXNDDDS....TS......", // 34
        ".....................FQQQQQQDDDDNDESDDDDDSDDNNCDDDDT....TD......", // 35
        "....................TQQAPPPPPNDDDDTDDDDDSDDNCCNDDDST.....ST.....", // 36
        "...............FP.PKNPQQAAAADNSSDSDDDDDDSDDDNNCDDDT......STT....", // 37
        ".............KCCKCKPPPPKQQQQDSDSSSSDDDDDSDDAOODDDST.....TS......", // 38
        ".........SKKCPPPPNPPANKPPQQEDDDSSSDDDDDDDDPCDDDDSS......TD......", // 39
        ".........ECCPKAPPPPKNPPPPQQNSDSSSSDDDDDTDDDAPDDDST.......ST.....", // 40
        "........PQ.CKPPPPKPPPPKKPADDDSSSTDDDDDSDDDDDDDDDST.......SDS....", // 41
        ".........EPPKPPPPKPKKKPPNDDDDTEETSSDDDSDDDDDDDDDT.......TS......", // 42
        "......P.CCPKCPPCKPCPPPPPDDDSSDTSDQTDDSSDDDDDDDDDT.......TT......", // 43
        "....FPPPPPPPP.NNPCKPPPPNDDDSSDSSDFDDDDDDDDDDDDDT................", // 44
        "...PPCP.CQ.PCCPP.PKPPPPSSDDSSDDSS.DDSSDDDDDDDDD.................", // 45
        "..PF...PPCP.CCPCPPPPPPNSSSSDTDTTT..DSSDDSDDDDDD.................", // 46
        "......CC...PP.PCCC.PPPSSEEQDSSFFT..TSSSSSDDDDDT.................", // 47
        "...........P.PFFPKPPANSSTTTDQ.TF...DSSSSSDSDDS..................", // 48
        ".........TP.CPFFCPCPPESTFPDDTTST...SSSSSSSSSSS..................", // 49
        "........PS...CPP.CPFFTTFNDDDFTSSSTTSSSSSSSSSSS..................", // 50
        ".............CFFC.P.FFFFPPPPF.SSSSTSSSSSSSSSSS..................", // 51
        "...............FC..PS.F.DPPS..SSSSSSST.SSSSSTT..................", // 52
        "...............FC.....F.QQPS..TSSSSSSSSSSSSSTTT...TT............", // 53
        "................FP......APPP...TSSSSSSSSSSSSSTTTTTST............", // 54
        ".................F......QPPP...TTSSSSSS.SSSSSSSSSSST............", // 55
        ".......................TQQPP.....SSSSSSTSSSSSSSSSST.............", // 56
        "......................TQQQQQT....TSSSSSSSSSSSSSSTT..............", // 57
        "....................FFQPQPPQP.....SSSSSSSSSSSSSST...............", // 58
        "....................FTTSTSSTS.....TSSSSSSSSSTTTET...............", // 59
        "...................................TSSSSSTTT....................", // 60
        "....................................TSTT........................", // 61
        "....................................TT..........................", // 62
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

fun DrawScope.drawPenance() {
    val matrix = arrayOf(
        "..........................DDDNNNDD.........................", // 0
        "......D..............DNNNNNNNDDDDNNNNND.............D......", // 1
        "......DD...........NNNNDDDDDKKNDDDDKDDNND...........D......", // 2
        "......DN..........NNNDKKKKKKKKKKKKKKDDDKKD.........DD......", // 3
        "......DND........NKKKKKKKKKKKKKKKKKKKKKKKD.........ND......", // 4
        ".....PDNKD........KKKKKKKKKKKKKKKKKKKKKKD.........KNDP.....", // 5
        ".....PDNKDD........DKKKKKKKKKKKKKKKKKKD..........DKNNPP....", // 6
        ".....DDDDDDD...........DKKKKKKKKKKD............DDDNDDDD....", // 7
        "..P.....DDDDD..............DAAAA..............DDDDDSSS..P..", // 8
        ".PPADDD.DDDDDDD..........PN.....ND...........DDDDDDSSD.DPP.", // 9
        ".DDNNDDDDDDDDD..DD......NN.......NN.......DD.DDDDDDDDDDNDP.", // 10
        ".NNNKKDDDDDSDD...DD.DNDNNDNDDDDDNDNNDND.DDD..DDSSDDDDNKNND.", // 11
        "DDDNKKKNDDDSDDD.DDD.DDDDNDNKNDNKKDNDDDD.DDD..DDSDDDDKKKKNKD", // 12
        "......DKKKNDDDD..DDDDDDDDNDNKKKDDDDDDDDDDD..DDDDDNKKKD.....", // 13
        ".........DKKKDDD.DDDDDDDDDKDDDDDNDDDDDDDDD.DDDDKKKD........", // 14
        "............DKKNDDDDDDDNDDDDKNKDDDDNDDDDDDDNKKKD...........", // 15
        "................DDNKKDDDSDDDNNNDDDSDDDKKNDD................", // 16
        ".....................DDSSDNDDDDDNDSSDD.....................", // 17
        "..........................DNADANDDST.......................", // 18
        "........................DDDNNNNNNDD........................", // 19
        "......................DDDDNNNNNNNDDDD......................", // 20
        ".....................DDDDDNNNNNNNDDDDD.....................", // 21
        "................D.....KKKDDNNNNNDDKNK.....D................", // 22
        "...............NND....DKNSDNNNNNDSNKK....NND...............", // 23
        ".............DNNNKD...DKNDDNNKNNDDNKD...DKNNND.............", // 24
        "............NNNANNND..DNKDDDNKNDDKKND..DNNNANND............", // 25
        "...........DNNNANNNN..DDKKDDNKNDDKKDD..NNNNANNN............", // 26
        "...........NNNNNNNNND.DDKKKDDKDDKKKD..NNNNNNNNND...........", // 27
        "...........NNKNNNNNND..KKDDDSDSDDDKK..NNNNNNNKND...........", // 28
        "...........NNNNNNNNK...KNDDDDDDDDDNK..DNNNNNNNND...........", // 29
        "...........DKDNNNNNK...KNDDDDDDDDDNK...KNNNNDDK............", // 30
        "............DDSNNNKD...DKDSDDDSDSDKD...NNNNDSDD............", // 31
        "............DDSDDDD.....KDDNDSSNDDK.....DDSDSDD............", // 32
        "............NNDNNN......KDDDDDDDDDK.....DNNNDN.............", // 33
        "............NNNNNK......DDDDDEDDDDD......KNNNND............", // 34
        "............NNNNNN.......DDDNNNDDDD......KNNNNN............", // 35
        "............NDNNNN.......NDNDDDNDD.......KNNNDN............", // 36
        "...........DDDNDDN.......NDDDDDDDD.......NDDNDD............", // 37
        "...........DSDDDDN.......KDNDDDNDN.......NDDDDDD...........", // 38
        "...........DDDSSDK.......KDNDDDNDN.......NDSDDS............", // 39
        "...........DDDSSND.......KDDDDNDDN.......KDSDDD............", // 40
        "...........DDDDDK........KDDDEDDDK........NDDDDD...........", // 41
        "...........KNDDD.........KDDDDNDDN.........DDDND...........", // 42
        "...........DDDK..........KDDNDDDDN.........DKDDD...........", // 43
        "..........DDDDD..........KDNDDDNDN..........DDDD...........", // 44
        "...........NNNK..........KDDDDDDDN..........KNNN...........", // 45
        "...........NND...........KDNDDDNDN...........DND...........", // 46
        ".........................KDDDDDDDK.........................", // 47
        ".........................KDDDSDDDN.........................", // 48
        ".........................KDDDSDDDD.........................", // 49
        ".........................DDDDDDDDD.........................", // 50
        ".........................NDDDDDDDDD........................", // 51
        "........................KDDDDDDDDDK........................", // 52
        ".......................DKNDDDDDDDDKN.......................", // 53
        ".......................NSDDDDDNDDDDDD......................", // 54
        "......................N..DDDDTDDDDDTN......................", // 55
        ".....................D..DDDKKDKKDDNW.N.....................", // 56
        ".....................K..KKDKKKKKDKK..N.....................", // 57
        ".....................K.DKKDKKKKKDKKN.KD....................", // 58
        ".....................KKKKNNKKKKKNNKKKK.....................", // 59
        ".....................DKKKKNNNKNNNKKKKD.....................", // 60
        ".......................KNDNNNNNNNDNK.......................", // 61
        "........................KKNAPPPPNKK........................", // 62
        "..........................DDNNNDD.........................." // 63
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawYuYevon() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        "................................................................", // 2
        "................................................................", // 3
        "................................................................", // 4
        "................................................................", // 5
        "................................................................", // 6
        "................................................................", // 7
        "............................DKDDDMM.............................", // 8
        "..........................DDMImSSmIDD...........................", // 9
        ".........................DDImmSISimmID..........................", // 10
        ".......................KKNSimIDDImmmDID.........................", // 11
        "....................KKKKKDmimIDDImiTDMIK........................", // 12
        ".................KKNANNKKKMmiWWWiiiiiimMK.......................", // 13
        "................NDDPDDKKKNDMImiiIMIIMiImDKKK....................", // 14
        "...............N..PANKKKKDMmiWWTMIiiMDiMDKKKKK..................", // 15
        "..............NP.NKKKKKKKDIiWWmMImimmMImmKKDNNN.................", // 16
        "..............APDDTTTKKKKDmiWimIDImiIDDimDKKNCQ.................", // 17
        "..............DNDWWWWKKKKMmmiiiSDSiWiIDimDKKKNNI................", // 18
        "..............ADSWWTDKKKKMmIImiiISimmmIWIMK...DNrD..............", // 19
        "...............DPSWTKKKKKDSDDIiimmiImiiTDID.....AP..............", // 20
        "................DDSTKKKKKDSDDImimmmIimmSDID.....NAD.............", // 21
        "....................KKKKKDSIMImmIImDiIMmIMK.....AN..............", // 22
        "....................KKKKKDSIIIIIDDIDmIDImDK....N................", // 23
        "....................KKKKKKSmmIMDDIIISmMDIDK....D................", // 24
        "....................KKKKKDImIDDDImIIMIMDNMK.....................", // 25
        "................KDDKKKKKKDIIDDDDDIIIMDDDDMK.....................", // 26
        "................DSSDDKKKKDMDKKDDDDDDDDDNKKK.....................", // 27
        "...............DETDKKKKKKDDDNNNKDDDDDKKKKKKKKKK.................", // 28
        "...............ASMKKKKKKKKKKNNNKKKKNNKKKKKKKDDDND...............", // 29
        ".................DDWWKKKKKKKKNNNNNNNNKKKKKKKKKDT.D..............", // 30
        "...............D.DWTNKKKKKKKKKNNNNNNNKKKKKKSTSNDPD..............", // 31
        "..............NCDDTKKDKKKKKKKKKKKNKKKKKKKKKKSWSKD.P.............", // 32
        "..............KDDKDSDKKKKKKKKKKKKKKKKNKKKKKKKSWSNPC.............", // 33
        "..............KDAKDSKTTKKKKKKKKKKKNNNANKKKKNDDSSDTA.............", // 34
        "..............DNCDNDTWDKKKKKKKKKKDAPAPAKKTTDDDSSDQD.............", // 35
        "...............KAPPDWDDKKKKKKKKKDMPPPQAKKDTDDSSDDPD.............", // 36
        "...............DKNPSDDSKKSNKKKKKDrQQQQAKKKDSDDDNCAD.............", // 37
        "................DNDDKSDSSSDKKKKKDCQEQQPNKDKDDKDPAD..............", // 38
        ".................DSKDDKWSDDKDSDKMITEFTPRKKKKDDDAD...............", // 39
        ".................NCNNDSSKDDD...DImmTiiqRKKKKSSNND...............", // 40
        ".................DACKDTKDMK....MImmiimmMMKDKSSDDD...............", // 41
        "..................KNNPNDSDD...DMImmmmImMIDBKNAAD................", // 42
        "..................KKKKKSMD....MIImmmmIIKMINNPPN.................", // 43
        "...................KDKMDD.....DIImImIMIKKMKNKK..................", // 44
        "...................NPKKK......KKImMIIMMKTNIrN...................", // 45
        "...................NPANEK.......MMMmmKMIKNNPN...................", // 46
        "....................NPQK........DmTMi.KMKKSDK...................", // 47
        ".....................KNK...........Km.KKKTDK....................", // 48
        "....................D.DK............D.KKDD......................", // 49
        "....................KDK................KDK......................", // 50
        "....................KNK................KNK......................", // 51
        ".....................NA................KNK......................", // 52
        ".....................KPD...ED..........NNK......................", // 53
        "......................NANNPCD.........APNK......................", // 54
        "......................DDNAPD..........PPND......................", // 55
        "........................DDD............DD.......................", // 56
        "................................................................", // 57
        "................................................................", // 58
        "................................................................", // 59
        "................................................................", // 60
        "................................................................", // 61
        "................................................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'q' to Color(0xFFFA649B),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}

fun DrawScope.drawSin() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        ".........................3...........1.................23.......", // 2
        ".......................33223........31...............322........", // 3
        ".......................2212.........10............v212A2........", // .
        "..................22....1232233....212.3223......211212.........", // .
        ".................22D2...A221233a..2113.3122...321112221.........", // .
        ".................1D212323331222D1a1a1v.32113.3211222113.........", // 7
        ".................1AAA012232111a2A11101322.1.3211112223..........", // 8
        "...............2221g21A1221x1121111122g2332.211111103...........", // 9
        "...........3.21222g222322212D222D11122222222111122223...........", // 10
        "..........32g112132111a21a112D1111a122112a12g122123V............", // 11
        "........32121232222201111211g111aag1121111ag11113....2..........", // 12
        "........2312332122211222a31g112g110g121A11gg1101..322...........", // 13
        ".......2122321011212331ag111232a1211g1g01g0011111223............", // 1.
        "......32223112122322211gg22232g1g21g11gg1000gg11212.............", // 1.
        "......1223322222320A0g0g11g111233121ga11g01111111111a1abv.......", // 1.
        ".....223321g2a11101E11111a21122220gA111g1g0011100ga22222b23.....", // 17
        "....12a211221gg0111121122332101ggg122221g1ggggg1A1111232b333....", // 18
        "...a333a12211111222221112AAA1001a13323220120aa110ga2a23..32b33..", // 19
        "..22322222a3232322a2211g01D1gg210g12a223212g1122101222b3.....3..", // 20
        ".23332223323221a201a22111221Aa21121g0112112g0021212.va223.......", // 21
        ".23333232221g1a21232aa3211g12211233211ga121ggaggg1g2...b33......", // 22
        ".23322231112232aa222a21232221122222111gg110gg121ggA112..b33.....", // 23
        ".21111112232221a22a2222111a11122222210011g112a1g1g1g122v..3.....", // 2.
        ".22232233a112a2223322212a21a11a2221g2100121011111111131a..33....", // 2.
        ".32a2212221232223233223a22221gga23aa1100gg1a1ggga111g...........", // 2.
        "...21121232a2323232232a22111110122111gg0g1a2a2111g1a13..........", // 27
        "....2112a2322222222222222221gg0g12g12ag0g11111121112gg..........", // 28
        ".....32g11222232a222221111111110g21232110g11g1211211Ag..........", // 29
        ".......3112a121a211111222111100g02223222202v3ag0g121113.........", // 30
        "........3112222a11222a1a221g01a10a32232222......2gg1111.........", // 31
        "..........1111a1a1g01a1g1gg1111g1g322323222......0011112........", // 32
        "............221011111gg1gg111g11g0211322312....3a1011ga11a......", // 33
        "..............21gg00g11111g0g111.323a2322212..2111011111111.....", // 3.
        "..............0a11111.31gg23......232232a111..11111111111121....", // 3.
        "..............2111113............3233222122132111a1g1g1111112...", // 36
        ".............a20111g.............21123222122111111331.3111111...", // 37
        ".............201a11g..............220322321211013.......a111a3..", // 38
        ".............10aaa13..............22g232332a22g..........3ag12..", // 39
        ".............3g1a1a3..............220a3323221222...........312..", // .0
        "..............11g1g...............v2a232122g1111............31..", // .1
        "..............21a10................32.21211111113...............", // .2
        "...............11ga3..................3132g23a1a2...............", // .3
        "................3222....................2321232a1a..............", // ..
        ".........................................22a2222222.............", // ..
        "...........................................2...333..............", // 46
        "................................................................" // 47
    )

    val palette = mapOf(
        '0' to Color(0xFF0C0C10),
        '1' to Color(0xFF262630),
        '2' to Color(0xFF484855),
        '3' to Color(0xFF737684),
        '4' to Color(0xFFA2A6B4),
        '5' to Color(0xFFD0D4E0),
        '6' to Color(0xFFFAFAFF),
        'A' to Color(0xFF301C12),
        'D' to Color(0xFF58301C),
        'E' to Color(0xFF844E2D),
        'V' to Color(0xFFCDE4FF),
        'a' to Color(0xFF204444),
        'b' to Color(0xFF346262),
        'g' to Color(0xFF122626),
        'v' to Color(0xFF76A8A8),
        'x' to Color(0xFF8E4E3E)
    )

    drawMonsterMatrix(flip(matrix), palette)
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
        HeroClass.MIME -> drawMime()
        HeroClass.NECROMANCER -> drawNecromancer()
        HeroClass.BLUE_MAGE -> drawBlueMage()
    }
}

fun DrawScope.drawMonsterSprite(monsterType: MonsterType) {
    when (monsterType) {
        MonsterType.GARLAND, MonsterType.ASTOS, MonsterType.LICH, MonsterType.MARILITH, MonsterType.MALIRIS,
        MonsterType.KRAKEN, MonsterType.TIAMAT, MonsterType.CHAOS, MonsterType.LEON, MonsterType.BORGHEN,
        MonsterType.GOTTOS, MonsterType.ROUNDWORM, MonsterType.CYCLONE, MonsterType.EMPEROR,
        MonsterType.DJINN, MonsterType.DJINN_BOSS, MonsterType.NEPTO_DRAGON, MonsterType.HEIN, MonsterType.GARUDA,
        MonsterType.GOLDOR, MonsterType.XANDE, MonsterType.CLOUD_OF_DARKNESS, MonsterType.MIST_DRAGON,
        MonsterType.MIST_DRAGON_BOSS, MonsterType.ANTLION, MonsterType.ANTLION_BOSS, MonsterType.GOLBEZ,
        MonsterType.CAGNAZZO, MonsterType.BARBARICCIA, MonsterType.SCARMIGLIONE, MonsterType.RUBICANTE,
        MonsterType.DARK_BAHAMUT, MonsterType.ZEROMUS, MonsterType.WING_RAPTOR, MonsterType.KARLABOS,
        MonsterType.IFRIT_BOSS, MonsterType.GILGAMESH, MonsterType.GILGAMESH_2, MonsterType.ATOMOS,
        MonsterType.EXDEATH, MonsterType.NEO_EXDEATH, MonsterType.OMEGA, MonsterType.OMEGA_PROTO,
        MonsterType.OMEGA_WEAPON, MonsterType.WEAPON, MonsterType.OMEGA_WEAPON_X -> drawBossSpriteFF1To5(monsterType)

        MonsterType.WHELK, MonsterType.VARGAS, MonsterType.NUMBER_024, MonsterType.ULTROS, MonsterType.TYPHON,
        MonsterType.AIR_FORCE, MonsterType.GUARDIAN, MonsterType.ULTIMA_WEAPON, MonsterType.KEFKA,
        MonsterType.GUARD_SCORPION, MonsterType.MAGITEK_ARMOR, MonsterType.AIRBUSTER, MonsterType.RUFUS,
        MonsterType.HOJO, MonsterType.BIZARRO_SEPH, MonsterType.SEPHIROT, MonsterType.JENOVA_CELL,
        MonsterType.JENOVA_BIRTH, MonsterType.JENOVA_LIFE, MonsterType.JENOVA_SYNTHESIS, MonsterType.NORG,
        MonsterType.EDEA, MonsterType.FUJIN_RAIJIN, MonsterType.SEIFER, MonsterType.ADEL, MonsterType.TRAUMA,
        MonsterType.ULTIMECIA, MonsterType.PLANT_BRAIN, MonsterType.BLACK_WALTZ, MonsterType.ZORN_THORN,
        MonsterType.RALVURAHVA, MonsterType.KUJA, MonsterType.TRANCE_KUJA, MonsterType.NECRON,
        MonsterType.KLIKK, MonsterType.KLIKK_BOSS, MonsterType.OBLITZERATOR, MonsterType.EVRAE,
        MonsterType.SEYMOUR, MonsterType.JECHT, MonsterType.PENANCE, MonsterType.YU_YEVON,
        MonsterType.SIN -> drawBossSpriteFF6To10(monsterType)

        else -> drawRegularMonsterSprite(monsterType)
    }
}

private fun DrawScope.drawBossSpriteFF1To5(monsterType: MonsterType) {
    when (monsterType) {
        MonsterType.GARLAND -> drawGarland()
        MonsterType.ASTOS -> drawAstos()
        MonsterType.LICH -> drawLich()
        MonsterType.MARILITH, MonsterType.MALIRIS -> drawMarilith()
        MonsterType.KRAKEN -> drawKraken()
        MonsterType.TIAMAT -> drawTiamat()
        MonsterType.CHAOS -> drawChaos()
        MonsterType.LEON -> drawLeon()
        MonsterType.BORGHEN -> drawBorghen()
        MonsterType.GOTTOS -> drawGottos()
        MonsterType.ROUNDWORM -> drawRoundworm()
        MonsterType.CYCLONE -> drawCyclone()
        MonsterType.EMPEROR -> drawEmperor()
        MonsterType.DJINN, MonsterType.DJINN_BOSS -> drawDjinn()
        MonsterType.NEPTO_DRAGON -> drawNeptoDragon()
        MonsterType.HEIN -> drawHein()
        MonsterType.GARUDA -> drawGaruda()
        MonsterType.GOLDOR -> drawGoldor()
        MonsterType.XANDE -> drawXande()
        MonsterType.CLOUD_OF_DARKNESS -> drawCloudOfDarkness()
        MonsterType.MIST_DRAGON, MonsterType.MIST_DRAGON_BOSS -> drawMistDragon()
        MonsterType.ANTLION, MonsterType.ANTLION_BOSS -> drawAntlionBoss()
        MonsterType.GOLBEZ -> drawGolbez()
        MonsterType.CAGNAZZO -> drawCagnazzo()
        MonsterType.BARBARICCIA -> drawBarbariccia()
        MonsterType.SCARMIGLIONE -> drawScarmiglione()
        MonsterType.RUBICANTE -> drawRubicante()
        MonsterType.DARK_BAHAMUT -> drawDarkBahamut()
        MonsterType.ZEROMUS -> drawZeromus()
        MonsterType.WING_RAPTOR -> drawWingRaptor()
        MonsterType.KARLABOS -> drawKarlabos()
        MonsterType.IFRIT_BOSS -> drawIfrit()
        MonsterType.GILGAMESH, MonsterType.GILGAMESH_2 -> drawGilgamesh()
        MonsterType.ATOMOS -> drawAtomos()
        MonsterType.EXDEATH -> drawExdeath()
        MonsterType.NEO_EXDEATH -> drawNeoExdeath()
        MonsterType.OMEGA, MonsterType.OMEGA_PROTO, MonsterType.OMEGA_WEAPON, MonsterType.WEAPON, MonsterType.OMEGA_WEAPON_X -> drawOmegaWeapon()
        else -> drawSlime()
    }
}

private fun DrawScope.drawBossSpriteFF6To10(monsterType: MonsterType) {
    when (monsterType) {
        MonsterType.WHELK -> drawWhelk()
        MonsterType.VARGAS -> drawVargas()
        MonsterType.NUMBER_024 -> drawNumber024()
        MonsterType.ULTROS -> drawUltros()
        MonsterType.TYPHON -> drawTyphon()
        MonsterType.AIR_FORCE -> drawAirForce()
        MonsterType.GUARDIAN -> drawGuardian()
        MonsterType.ULTIMA_WEAPON -> drawUltimaWeapon()
        MonsterType.KEFKA -> drawKefka()
        MonsterType.GUARD_SCORPION, MonsterType.MAGITEK_ARMOR -> drawGuardScorpion()
        MonsterType.AIRBUSTER -> drawAirbuster()
        MonsterType.RUFUS -> drawRufus()
        MonsterType.HOJO -> drawHojo()
        MonsterType.BIZARRO_SEPH -> drawBizarrosephiroth()
        MonsterType.SEPHIROT -> drawSephiroth()
        MonsterType.JENOVA_CELL, MonsterType.JENOVA_BIRTH, MonsterType.JENOVA_LIFE, MonsterType.JENOVA_SYNTHESIS -> drawJenova()
        MonsterType.NORG -> drawNorg()
        MonsterType.EDEA -> drawEdea()
        MonsterType.FUJIN_RAIJIN -> drawFujinRaijin()
        MonsterType.SEIFER -> drawSeifer()
        MonsterType.ADEL -> drawAdel()
        MonsterType.TRAUMA -> drawTrauma()
        MonsterType.ULTIMECIA -> drawUltimecia()
        MonsterType.PLANT_BRAIN -> drawPlantBrain()
        MonsterType.BLACK_WALTZ -> drawBlackWaltz()
        MonsterType.ZORN_THORN -> drawZornThorn()
        MonsterType.RALVURAHVA -> drawRalvurahva()
        MonsterType.KUJA -> drawKuja()
        MonsterType.TRANCE_KUJA -> drawTranceKuja()
        MonsterType.NECRON -> drawNecron()
        MonsterType.KLIKK, MonsterType.KLIKK_BOSS -> drawKlikk()
        MonsterType.OBLITZERATOR -> drawOblitzerator()
        MonsterType.EVRAE -> drawEvrae()
        MonsterType.SEYMOUR -> drawSeymour()
        MonsterType.JECHT -> drawJecht()
        MonsterType.PENANCE -> drawPenance()
        MonsterType.YU_YEVON -> drawYuYevon()
        MonsterType.SIN -> drawSin()
        else -> drawSlime()
    }
}

private fun DrawScope.drawRegularMonsterSprite(monsterType: MonsterType) {
    when (monsterType) {
        MonsterType.RED_FLAN, MonsterType.SLIME, MonsterType.WATER_FLAN, MonsterType.FARIIS, MonsterType.SKULL_EATER -> drawSlime()
        MonsterType.GOBLIN -> drawGoblin()
        MonsterType.OGRE, MonsterType.GARGOYLE, MonsterType.ORC -> drawOgre()
        MonsterType.LUNAR_DRAGON, MonsterType.DRAGON, MonsterType.DRAGON_RIDER, MonsterType.RUBY_DRAGON, MonsterType.SILVER_DRAGON, MonsterType.DARK_AEON, MonsterType.SHINRYU -> drawDragon()
        MonsterType.WILD_RAT, MonsterType.RAT -> drawWildRat()
        MonsterType.WOLF, MonsterType.DINGO -> drawWolf()
        MonsterType.SAHAGIN, MonsterType.SEA_SNAKE, MonsterType.MERMAN -> drawSahagin()
        MonsterType.PIRATE -> drawPirate()
        MonsterType.COCKATRICE, MonsterType.BITE_BUG -> drawCockatrice()
        MonsterType.BOMB, MonsterType.DARK_IMP, MonsterType.STOKER, MonsterType.STOKER_MONSTER -> drawBomb()
        MonsterType.EVIL_EYE-> drawEye()
        MonsterType.MALBORO, MonsterType.GREAT_MALBORO -> drawMalboro()
        MonsterType.TONBERRY -> drawTonberry()
        MonsterType.MINDFLAYER, MonsterType.DARK_FORCE -> drawLich()
        MonsterType.BLACK_KNIGHT, MonsterType.SERGEANT, MonsterType.DARK_KNIGHT, MonsterType.CAPTAIN, MonsterType.GRUNT, MonsterType.SWEEPER, MonsterType.GALBADIAN_SOLDIER, MonsterType.GUADO_GUARDIAN -> drawDarkKnight()
        MonsterType.LAMIA, MonsterType.MEDUSA -> drawLamia()
        MonsterType.ADAMANTOISE -> drawAdamantoise()
        MonsterType.ZOMBIE -> drawZombie()
        MonsterType.WYVERN, MonsterType.ELNOYLE -> drawWyvern()
        MonsterType.BEHEMOTH, MonsterType.BRAWLER, MonsterType.WENDIGO, MonsterType.ZAGHNOL, MonsterType.MISTODON -> drawBehemoth()
        MonsterType.GIANT, MonsterType.IRON_GIANT -> drawGiant()
        MonsterType.TOAD -> drawToad()
        MonsterType.HELLHOUND, MonsterType.GEEZARD -> drawHellhound()
        MonsterType.MP, MonsterType.MAGIC_MASTER, MonsterType.BLACK_MAGE_UNIT -> drawBlackMage()
        MonsterType.NINJA -> drawNinja()
        else -> drawSlime()
    }
}

@Composable fun EnemySprite(monsterType: MonsterType, modifier: Modifier = Modifier) = Canvas(modifier) {
    drawMonsterSprite(monsterType)
}

@Composable fun EnemySprite(enemyName: String, modifier: Modifier = Modifier) {
    val type = MonsterType.entries.find { 
        it.name.equals(enemyName, ignoreCase = true) || enemyName.contains(it.name.replace("_", " "), ignoreCase = true)
    } ?: MonsterType.SLIME
    EnemySprite(monsterType = type, modifier = modifier)
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



fun DrawScope.drawAntlionBoss() {
    val matrix = arrayOf(
        ".....................KKKK.................KKKKKKK...............", // 0
        ".......KKK...........KANNKK.............DKA1SKK.................", // 1
        "......NKNK...........KANKKKK...........DAAOKK...................", // 2
        ".....DNNNK....D.......NNKTKKKKK........KAANT....................", // 3
        ".....KNANK.....K..D...KAKKSSOAAN...NKKKKKKKKKK..................", // 4
        "....KNNOCNK....KK.KK..KAKKKKKOCAKKKNNNKKKKNNKKKK................", // 5
        "....KNKNANK..D.KNKNK.DKNNKTTTNAKKKKKKKKKNKKSKTNNK...............", // 6
        "...DANKKNNNK.KKKKKKKKKKNNKTWWTKKNNNNNKKNKKSiTDSNNK.......DK.....", // 7
        "...KANKKKANK.NAKNNNNNKKKNKTKKKNKKKKNNNAAKNSWWTKSNKK.....KKKKK...", // 8
        "...KNNK.KANNKKKNNNNAONKKKKKKNKSTSSSNKKOKKNKSiWKSNKAK...DKAA1KK..", // 9
        "...KNK..KNNKKKNAAKNNANNKKNKNKTWDTWWTNKKKKKNNSTSNNKKKKKKNNNNAA.K.", // 10
        "...KNK..KNNKKKAOCNKNNNKKANKNSTDTWWWTNKNKKNKKNNNNNNKKKKKNKKKSKK.D", // 11
        ".D.KNK...KNKKNAOANKKNNKANNKNSTKTWWWTANNKKAANNKNNKNNKNNKKTTKSKK.K", // 12
        "KNKKNK...KNKKNNCNNNKNKKNNKKNTTKTWWTSNKNNSSAANSKNNNOAKKNNKKSKK.K.", // 13
        ".NNNNK....KKKNNNNNNKKKKNKKKNNSTDTWTDKKNKKNSNNKKNNNCAKNNANNK..K..", // 14
        "KKANKK......KKNNNKNKKKNKKNNKNNSSTNNKNNNNKKNKKKKKKNKKNNANNANK....", // 15
        "KSNK....DKKKKKKNNKKKKKKKANNNKNNNNKKAANNNKKNNKKK1KKKKKKNANANK....", // 16
        "KSK......KKNNNNKKKKKKNKK1ANNNKKKKKNANNNNNNKKCKTPKKCKKKKNAOONK...", // 17
        "KK.K.....KNNNNNNNNNNNNNKKAANNNNNNAAANKKKNKKTOKKTKKTKCOKKNCOCKK..", // 18
        ".KKK....DKNNAANNAANANNKKNNKKNNKKNOOAKKKCOKKKTKKTKKTKTNKNKNCOAK..", // 19
        "..K.....KNANKAAANNNNNNKKNNNNKKKNKNONKKKKTTKKTKKKKKKKKKKNKKKAAKK.", // 20
        "........KNNNKNAANNKNKKANNNNKKKNNKNNKKCKKKTKKKKKKKKKKKKTOKKNNANK.", // 21
        "........NANKKNNNNNNKKKAAKNNKKKNNKNKKKTTKKKKKKKKKKKKKTKKOKNNANAK.", // 22
        ".......KNANKKKKKNAANKKAANKKKKNNNKKNKKKKKKKKKKKKKKKKKKTNNKKNNNOA.", // 23
        ".......KAONNK...KNNNKKAANNNKKNANKNKNKKKTKKKKKKKKTKTKKOANKKKNAOCK", // 24
        ".......KAOANK....KNKKKNANNNKNNAKNANNNKKTKKTKKTKKTKKTKKNNKKNNOOCK", // 25
        "......NKNCNNK.....NKKKNAANKKNNNKNANNNNOFKKTKKTKKTKKOKNNKKKKNAOCK", // 26
        ".....NNNKNNK.....DKKKKNNNNKKNKNNNANNNNONKCOKKTKTQKKNNNAKTTKNNCCK", // 27
        "..KKKKANNKKK..........KNNKKKKKNNNAANKNKNNNOKCOKPANNAKNKK..KNNANK", // 28
        ".KNNKAKNNNNNKK........KKKKKKKNNANOCNKNKNNNNNNNNNNNACKKKT..KNNNNK", // 29
        ".KNKNKNNNNNSNK.........KKKK.KKNANCCNKKKKKNNNNNANNNKKKKT...KKKNKK", // 30
        "KNKSNKNSSKKKS.D.............KKNANNANNKKKKKKKKAAKKKKKT.....KSSKSK", // 31
        "KNKSKKSSKT..K.K.............KKNNANAANKKKT.KKKKKKKT.KT....NKSSTSK", // 32
        "KSKSKKSKKKKTK.K..............KKNAAACNNKKT.TTTTTKT..T.....KKSiSK.", // 33
        "KSKSKKSKKKKK.K...............KKNNAOCANNKKT...........KKKKKKTTSK.", // 34
        "KSKSKKSKKKKKKKKKKK...........KKKNAOOCNKNKT.........DTTKNNKDTTSK.", // 35
        "KKSKSKKKKK.KKKKKK.............KKNNCOCNKNNKT.......NKT..KKKSTSK..", // 36
        ".KKKKKK.......................KKKNNCANNKNKK.....KKNKT....KTTSK..", // 37
        "...KK..........................KKKNNNNNNNKSSDKKKNNKT....NSTSK...", // 38
        "...............................NKKKNNNNKKSmTSSKANKK.....KSSKKKK.", // 39
        "................................KKKNNKNKSmTmmSSKKKT....KSSKKKKKK", // 40
        ".................................KKKKKNKKSmiTmSSSSKKK.DSSKKKK...", // 41
        "..................................KKKKKKKSm...mmmSSSSKKKKK......", // 42
        "....................................KNKDSSSTT..iTSSmSSSSKKKKKKKK", // 43
        "....................................KKKSSmmTTTTTTTi.TmSSSSSSKKK.", // 44
        "..............................KKKKKKKKKKKSSSSSSmTTTTTSSSSKKKK...", // 45
        "...........................DKKKKKKKKKKKKKKKSSKSSSSSSSSKKKKK.....", // 46
        "................................KKKKKKKKKKKKK.KKKKKKKK.........." // 47
    )

    val palette = mapOf(
        '1' to Color(0xFFB4820F),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'F' to Color(0xFFFFE1AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(matrix, palette)
}


fun DrawScope.drawFujinRaijin() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        "................................................................", // 2
        "................................................................", // 3
        "................................................................", // 4
        "................................................................", // 5
        "................................................................", // 6
        "................................................................", // 7
        ".........................KKKKKKKKKKKKKK.........................", // 8
        ".........................NNNDDDNNNNDDNN.........................", // 9
        ".....................KKKKDDDDSSDDDDSSDDKK.......................", // 10
        ".....................KKDDSSSSSSSSSSSSSSDK.......................", // 11
        ".....................KKSTWWSSDDSSSSDDSSDDKK.....................", // 12
        ".....................DDTTTTSSDDTTSSSSSSSSKK.....................", // 13
        "...................KD......DDKKWWSSWWDDSSKK.....................", // 14
        "...................KD......PPNNTTTTTTSSSSDK.....................", // 15
        "...................KD....DDJJJPSSWWSSWWSDDDKK...................", // 16
        "...................ND....DDCCPPSSWWSSTFSSDDKK...................", // 17
        ".....................D...DDKKNDDDWWDDSSSSSDKK...................", // 18
        ".....................DD..DDKKKKDDTTDDSSSSSSKK...................", // 19
        ".....................KK..NNNNKKDDSSDDDDSSSSKK...................", // 20
        ".....................KK..DNNNDDDDSSDDDDSSSSDK...................", // 21
        "...................DDKKDDJPNAWWDDKKKKDDKKDDS....................", // 22
        "...................DDDDDDQPNAWWSDNNKKDDKKDD.....................", // 23
        "...................KD..KNEENAWWJJEEKKDDKKKKKK...................", // 24
        "...................KD..KNEEAAFFJJEEKKDDKKKKKK...................", // 25
        ".......................KNEEEEEEJPKKDDKKKK.......................", // 26
        ".......................KDEEEEEEPPKKDDKKKK.......................", // 27
        ".........................KKEEJPBBBBKKB0KKKK.....................", // 28
        ".........................KKEEJP0BBBKKB0KKKK.....................", // 29
        ".........................1AKK11UU11KKUUSSSDKK...................", // 30
        ".........................1AKK11UU11KKUUSSSDKK...................", // 31
        ".......................KKUUBBUU21KKBBBBSSSSKK...................", // 32
        ".......................KKUUBBUU22KKBBBBSSSSKK...................", // 33
        ".....................KKKK0BBB000BKKKKBBUUBBKK...................", // 34
        ".....................KKKKBBBBBBBBKKKKBBUUBBKK...................", // 35
        "...................KD..EQKKKKBBKKTFEEUUUU0BKK...................", // 36
        "...................KD..EQKKKKBBKKWWFFUUUU0BKK...................", // 37
        "...................KDFEQPKKKKBBKKFFQQ0000KK.....................", // 38
        "...................KDEEJPKKKKBBKKEEJJ0BBBKK.....................", // 39
        ".....................NNNKKKBBKKBBNNNNKKKK.......................", // 40
        ".....................KKKKKKBBKKBBKKKKKKKK.......................", // 41
        ".........................KKAAKKBB1111AAAADD.....................", // 42
        ".........................KK1CKKBB22221111KK.....................", // 43
        "...........................KKKK11DDDDDDKK.......................", // 44
        "...........................KKBD22BBBBBBKK.......................", // 45
        "...........................KKDDNNBBBBBBKK.......................", // 46
        "...........................KKNNKKBBBBBBKK.......................", // 47
        "...........................KKDNKKDDDDDDKKDD.....................", // 48
        "...........................KKDNKKDDDDDDDNKK.....................", // 49
        ".........................DDDDNNKKDDDDDDDNKK.....................", // 50
        ".........................KKDDNNKKNNNNDDDNKK.....................", // 51
        ".........................KKKKKKDDDDDDNNDDKK.....................", // 52
        ".........................KKKKKKDDDDDDKKDDKK.....................", // 53
        ".............................KKDDDDDDKKNDKK.....................", // 54
        ".............................KKKKKKKKKKKKKK.....................", // 55
        "................................................................", // 56
        "................................................................", // 57
        "................................................................", // 58
        "................................................................", // 59
        "................................................................", // 60
        "................................................................", // 61
        "................................................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        '1' to Color(0xFFB4820F),
        '2' to Color(0xFFE6B414),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'W' to Color(0xFFFFFFFF)
    )

    drawMonsterMatrix(flip(matrix), palette)
}


fun DrawScope.drawZornThorn() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        ".......................................TS.......................", // 2
        ".......................................SmT......................", // 3
        "..........T...........................WmVTT.....................", // 4
        "..........TT..........................SmVVTT....................", // 5
        "............T........................WSVm0VSDT..................", // 6
        ".........T...T.......................SmIV000WS..................", // 7
        "..............T.....................TmVI00ITWD.........WS.......", // 8
        "........T.....ST...................STmVIBISWWS.........SWW......", // 9
        ".......TT....TqS....................TIV0DSWWWTT.......SWWT......", // 10
        "......ST....Tqqq.........SS........TTIIBDWWTWWS......TWWWT......", // 11
        "......qS...TCPPQT.......SPPT.......TTS00DTTWWWD.....TWWWTIT.....", // 12
        "......QP...DCCQqT......TPZRS........STS0KSWWWWSW..TTWWWWS0W.....", // 13
        "......TPP.TTZCqqT.....SPXZTP..........KDBDFTWWWS.TWWWWWSV0W.....", // 14
        "........PTWTCPQPPT..WSqpXPWPT.....SW..TBDE4SWWWSTWWWWWmV00TW....", // 15
        ".........TSTQQPPpS.SPpppPTWSP.....S0DSSKKQETWWWWSWWWWTmVBBTT....", // 16
        ".....T...TWSEEPQqPDpPPpPTWTSP.....D0BBDKBKSWWWWWTTWWiSVIBIST....", // 17
        ".....TTSQQWTPAqqqqPCXZZQWWTTP....T0BBBBKKBSWWWWWWTWTIII0B0ST....", // 18
        ".....T..TQTWTQQQqEqCZZCWWWTWP....000000KBBBWWWWWWTSSUIIBB0SS....", // 19
        "..........TWWQQqqPqPPSWWWWWWP...TBB0000KBBBWWWWWTSSSII0KBBSS....", // 20
        "....T.....TWWPPpPPPPSWWWWTWWPT..TTT0BBBDBBBTWWTSISISI0BWDBIWW...", // 21
        "...SSST...TWWSpPpPSAZSWWWSTWAS..TBDSWWWD0DBDTDSVVVUS0BS..DSWS...", // 22
        "...STPPPPPTTWTPQPCPCZAWWS.TWAr...DB0DTWSBBSTSSSSSS00DS...STTS...", // 23
        "....SWWSCPPrSTSDSPCZZCTS...SAr...TB0BTSSKBSDSWWSDS00K..TTSTT....", // 24
        ".....TWTSPPCCSTSTTTSPZS...TPPT....S0DWWTDKDSSWWTWSSDS..TET......", // 25
        ".....TWQWWDCrSWTWTTWSrS..QPT.......DDSWWSDSWDWTDTTKSW..EET......", // 26
        "......TTTWWSSWWTWTTWSS...EE.........TDEDTDTDTDTTSWSS...TS.......", // 27
        ".......TTQFTSSSSTSSDSS...ST..........SEQSSSWDTWSTWST............", // 28
        "........Q4TTSTDDTSSDSS...............TQTTTDWDSSSWWST............", // 29
        "........TT.TSTWTWTTWSW...................TTSSTTTTSS.............", // 30
        "............TSWTTSWTS.....................DDSTTTTKS.............", // 31
        "............TrSTDSTDP....................SDKTWTTSK0T............", // 32
        "............SRDWTTWKPS...................DSDSWTSSTSS............", // 33
        "............STDTWWTTTS..................TSTSSSTDSTTS............", // 34
        "...........ST.SSSSSTTTS..............W.SIT..DSSSS..TTT..........", // 35
        ".........WSTS.SSSSA..TTST.............SDTT..KSSSS..TTST.........", // 36
        "..........WT..DATSR........................SBS0DD...............", // 37
        ".............TACSPZP......................TB0SDDSS..............", // 38
        ".............PPCSPCPT.....................D00STSSSD.............", // 39
        "............SCPQTTpZPT...................S0BISTSS0ST............", // 40
        "............PrPQTWQRPQ...................DI0mTWTI0VS............", // 41
        "...........TQrPTTWSAPP..................T0I0STSTS0mm............", // 42
        "...........PPAPWTWTPPPS.................SIISTTTWSBmVS...........", // 43
        "..........TQPASWTWWPPPP.................0S0SWTTWTDSVS...........", // 44
        "..........QQPATWTWWSPPPT...............SIS0TWTTWWDSSS...........", // 45
        "..........PQPDWWTWWTAPPS...............DIV0SWTWWWDISSS..........", // 46
        ".........TQPPDTWSWWTDPPT..............TD0IDTWTSWWTDSSS..........", // 47
        ".........TQPPTWWTWWTSPPWT.............T0IISTWTWWWTDSSST.........", // 48
        "........WWQPPWWWSWWWSPPWT.............TSSSSTWTTWWTDSSST.........", // 49
        "........TWQPPWWWTWWWTpPS.............TDIVSSTWTiWWTDSSS..........", // 50
        "........WWPPAWWWTWWWWPPPTT...........DDIISWWWTTWWWSSID.T........", // 51
        ".......TWEPXPSSSSSTSSCPPPT...........TWISISSTSSSSSDSIDWT........", // 52
        ".......TTQPCSSSSSSTWTPPPPP..........SWiSSSSSTTSTTTTIISTTT.......", // 53
        ".......TqQPCW.TSSTTT.SZPPPS.........DWSISS.SSTTTTT.DI00DD.......", // 54
        "......TEqQPA..TSSTTT.WCPqpP........TDD0IIW.STTTTTT.SI000D.......", // 55
        "......SqqQPS..WTTTT...SXqqqT.......SI0IID...STTTT...00000S......", // 56
        "......qqQPP....TTTS....PPQpP.......IIISST...TSTTT...TI0000......", // 57
        ".....TqqQPW....TSTT....TPpPP......TIIIII....SSTTT....SI00IT.....", // 58
        ".....SQQPT.....TSTT.....TPpPS.....SIIISS....TSTTT.....DIIIS.....", // 59
        "....TQqPT......TTWT......TPPP.....IIISS.....TTSTT......SSIS.....", // 60
        "....DqPT.....TTTSTTT......TPPT...TIIIS......STSTT.......SSSS....", // 61
        "...TQS....TTTWWWWWWTTTTTT..TPP...0I0S......TTTSTT........SSS....", // 62
        "...ST......TTTTW.TTTTWT......S...TTW..W.T.....W......T....TT...." // 63
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


fun DrawScope.drawEvilEye() {
    val matrix = arrayOf(
        "......................DDDDDD...........................", // 0
        "...................D00UVVTQPD................D.........", // 1
        "................DBUUUVVVTPDDAD.............PP.V0.......", // 2
        ".............DB0UVUUUVVUDQSSNN............DPAD.V0......", // 3
        "...........DBUUVU00UVVV0SCPD.D............ADDPDVU0.....", // 4
        "..........0UUUU000UVVVUSWDAN..............D.PADUVU.....", // 5
        "........D0UU0DD0UUVVVUSWWWDD...00000000.....ADDUVVB....", // 6
        "......DBUU0DDrDUVVVVUSWWWWT..0UVVVVVVVVVU0..DSDDUV0....", // 7
        ".....D0UU0DDDDUVUVVVSWWWWW.0UVVVVVVVVVVVVVUD.DDDUVUB...", // 8
        "....D0UBDDDDDUVUUVVVUTWWW.0VVViidVVVVVVVVVVUBDDUVVVB...", // 9
        "....00DSDDrDUVUUUVVVVUTW.UVVVdidVVVVVVVVVVVVU0DVVVV0D..", // 10
        "..D0B..DDDD0VU0VUUVVdVUDUVVVViiVVVVVVVVVVVVVVU00VVU0D..", // 11
        ".D0D...DrD0UU0UU0DUVVdVU0VVVVdVVUUUVVVVVVVVVUUU0BU0UB..", // 12
        "DBD...DDrDUUDDUUDD0VVVdV0UVVVVVUUUVVUVVVVVVVVVUUB0BU0..", // 13
        "D.....DrD0U0D0V0NND0VVVdVUVVVVVUUUUVVVU0000UUUVU0DBU0..", // 14
        ".....DDrDUUDDUUDNNNN0VVVVVVVVVV00UVVUDCPPPPPD0UVUDD0U..", // 15
        ".....DDD0U0DDU0DNNDDD0UVVVVVVVUBUVU0CQEFEEQQPP0UUBD0U..", // 16
        "....DDrDUUDD0U0DNDDDDD0UVVVVVVUK0U0CQFWFEFEEQQP0U0K0U..", // 17
        "....DDD0U0DD0UDNDDDDD0UUUVVVVdVDNDAQFFFFEQPCACQP00KDU..", // 18
        "....DDDUUDDDU0NDDDDDD0UVVVVVVdV0KAPEFEEEPPEEPPAPP0BDU..", // 19
        "...DDD0U0DDDUDNDrrDD0UUUUUUVUVVUBNPEFQEQPEPAAPPAPDBB0..", // 20
        "...DDDUUDDD0UDADrrDN0UUUVVVVVVVVUNAPEQEPPPCPNAPAPABD0..", // 21
        "...DrDU0DDD0UDDDDDDD0UUVU00VVVVVUBNCQPQAPCNANAPAPNBNB..", // 22
        "..DDr0UDDrD00NDDDDTSB0UV0NP0VVVVVUBNAPPCACANACAADBBND..", // 23
        "..DDDUUDrrD00DDDDSWSB0UUDAQPIVVVVVUBDNAACANNNAANB0BND..", // 24
        "..Dr0U0DDrD00DDDSWTSB0VIDAPAQSVVVVVVUBBKNNNNNNNB00BDD..", // 25
        "..DD0UDDDDD00DDSWTDBB0USANNAQDIUVVVVVVUUBBBBBB0UU0KDD..", // 26
        "..DDU0DDDDD00DDTWD0BD0USANNNAAESIVVVVVVVUUUUUUUU0BNDD..", // 27
        "..DDU0DD..DD0DSWSBU0BBUVSQPNNAQAET0I0IVUVVIVVI0B0BDDD..", // 28
        "..D0UDD....D0DTTD0U0BD0UTEANAAANFQPFPPFITSSQQEDDBDSDD..", // 29
        "..D0UDD....D0SWSBU0BBKBUVSCPPNNNQCAQACPAQPPCPPNDD.DD...", // 30
        "..D00N.....DBTWD0UBBBUBBUVTFCNAACANPNAANPNANNADB...D...", // 31
        "..D00D......D..BU0B0VVUB0UVSAPQAANKANNANANPDKND....D...", // 32
        "..D0DD.........0UB0VVVU0B0UVSQEAPPKANNQNPAQQCP.........", // 33
        "..D0D.........D00BUVVU00BD0UVVSPFAAQAPFPESSSDD.........", // 34
        "..D0D.........B0BUVVU0DDD..D0UUSV0PQDPQDSI000DP........", // 35
        "..D0D.........B0DUVVUDQD.....D00UUIIU0D000B0KCQ........", // 36
        "...BD.........B0PP0UUDQD........DDDDDDDB00UUDAP....AP..", // 37
        "...D..........DPPCA0UKAD................B0UUDCA...PCPP.", // 38
        "..............DDDDA00DAD.................DBUBDD..DANAPD", // 39
        "..............DNB0DU0BDD...................0BUUD00N..AD", // 40
        "...............B0U00U0UD...................DBVVVUB....N", // 41
        "...............B0UUBUVVD....DBBDACDD.......DBVVUB......", // 42
        "................B0U00UVDDDBB0UIQQQQPAD......DVV0.......", // 43
        "................DB0UB0UBB0UUUUIPPPPQQPD.....DUUB.......", // 44
        ".................DB00K0UBUUU00BD..DDAPPD....BU0........", // 45
        "..................DBDDK0B00DD.......DNPPD...00D........", // 46
        ".....................PPDDD............DPA...AD.........", // 47
        "......................AD...............DP...APD.D......", // 48
        "........................................DD..NCPP.......", // 49
        ".........................................D...DN........", // 50
        ".......................................................", // 51
        ".......................................................", // 52
        ".......................................................", // 53
        ".......................................................", // 54
        ".......................................................", // 55
        ".......................................................", // 56
        ".....................DKKKKKKKDDD.......................", // 57
        ".................KKKKKKKKKKKKKKKKKKKKKKKKKD............", // 58
        "...............KKKKKKKKKKKKKKKKKKKKKKKKKKKKKK..DD......", // 59
        "............KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKD......", // 60
        ".............DDKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKDD.......", // 61
        "................NKKKKKKKKKKKKKKKKKKKKKKKKKKKKD.........", // 62
        "........................DKKKKKKKKKKKKKKK..............." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'W' to Color(0xFFFFFFFF),
        'd' to Color(0xFF5AD2CD),
        'i' to Color(0xFFD2B4F0),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}


fun DrawScope.drawRedFlan() {
    val matrix = arrayOf(
        "................................DDDMrrrrrrrI....................", // 0
        "..............................DrpppppqqqqqqqII..................", // 1
        "..........................DDIrpppqqqqqqqqqiqqmmr................", // 2
        ".......................DIrrrrppppqqqqqqqqiiiiiimmI..............", // 3
        ".....................DrpqqqppppppqqqqqqqiiWWWWWWiqm.............", // 4
        "....................rrpqiiqprpppqqqqqqqqqiWWWWWWWiqp............", // 5
        "...................rpqqiiiqpppppqqqqqqqqqqiWWWWWWiiqI...........", // 6
        "..................rpqqqqqqpppppqqqqqqqqqqqiWiWWWWWiiqr..........", // 7
        "..................rqqqqqpppppppqqqqqqqqqqqiWiiWWWWiiqpI.........", // 8
        ".................rqiWiqppppppppqqqqqqqqqqqiiqqiWWiiiiqr.........", // 9
        "................rqiWWiqpppppppqqqqqqqqqqqqqiqqqiiiiiiqpI........", // 10
        "...............DpqiWiqpppppppppqqiiiiqqqqqqiqqqqqiiWiqpr........", // 11
        "...............rqqiiiqpppppppppqqqiWiiqqqqqiqqqqqqiiiqprD.......", // 12
        "..............rpqiiiqppprrpppppqqqqiWiqqqqqiqqqqqiiiiqprr.......", // 13
        ".............DrqqqqqqpprrrppppppqqqqWiqqqqqiqqqqqiiqqpprr.......", // 14
        "..............rqqqpppprrrrpppppppqqqiWiqqqqiqqqqiiqqppprr.......", // 15
        ".............rpqqqpprrrrppppqqqiiqqqqiiqqqqiqqqiiiqpppprr.......", // 16
        "............DrqiqpIrrrrpppppqqiiiiiqqiiqqqqqqqiiiqpqqqppr.......", // 17
        "............DrqqmIrrrrrpppprrrrrpqiqqqiiqqqqqqiiqqqiiiippr......", // 18
        "............IrqqIIrrrrrpprrCPPQPrRpqqqqqqqqqqqqqqqiiqIrrrpD.....", // 19
        "............IrqprrrrrrrprACPQEFFEPCpqqqqqqqqqqqqqqqrrPQQPrD.....", // 20
        ".............rprrrrrrrrrRCPPPQEEFFQCpqqqqqqqqqqqqprCQFWFEPA.....", // 21
        "............rrrrrrrrrrpRACCCOPQEFFFQArqqqqqqqqqqqrCEFWWWFQA.....", // 22
        "...........DrrrrrrrrrrpRCOCCCPQEEFWWQCrpqqpppqqprrQEFWWWFQA.....", // 23
        "............rrrrrrrrrrrrACCCCPQEEFWWWEQPrrppppprRPQEFWFQEPA.....", // 24
        "............rrrrrrrrrrprrACCCCPQEFWWWTQQCCCrpprRCPPPQEEPQAA.....", // 25
        "............rrrrrrrrrrprrRACCCCPQQEFFEPQQOCrpprACCCCPPQQPrr.....", // 26
        "............rrrrrrrrrrrrrrrACCCCCPPQEEEPCrrpppprrRACCCCArrpr....", // 27
        "...........rrrrrrrrrrrrppprrRACCCCCPOCCrrrppppppprrRAArrpqqpr...", // 28
        "..........DrrrrrrpqprrrrrpppprrrCCCCrrrppppppppppppprrpqqiiqr...", // 29
        "..........IrrrrrrrqqqprrrrppppprrrrrrppppppqqqqqqqqqqqqqiiiqD...", // 30
        "..........rrrrrIrrqqqqprrrrrrppppppppppqqqqqqqqqqqiqqpiIiiqI....", // 31
        ".........rrrrIIppppqqqqpppppppppppqqqqqqiWiqqpIirqqqrNSNIqqD....", // 32
        ".....rrrrrrrIIqqqqppqqqqppppppqppqqiiqqqqiqpprNSKrrrMMNMrppD....", // 33
        ".....rpprrrrrpqiiqqpppqqqqqpqqpmmrpIrmmpqqrrNrrrrrrrpppppprD....", // 34
        "......rrrrrrrpqqqqqqpqqqqprpqpNDSrNDDDSNrrDNrqiiiiiiiqqqpprr....", // 35
        "......DrrrrrrpqqqqqqqqqqpIDDIIDKrqpSTNrrNITrqiWWiqqiiiiqqpprD...", // 36
        ".....DpprrrrrrpqqqqprrrINSmrrSmriiqpppqqprIqWWWiqqqqqqqiiqqprr..", // 37
        "....DqqqprrrrrrpqprNDDNrrImqqqqqqqqpppppprpiWWiqqqqqqqqqiiiqqpr.", // 38
        ".rrrqqqpprrrrrrpprNNTSrqqqqqiqqqqqppppppprqiiiqqpqqqqqqqqqiiiqpD", // 39
        "DrrrpppprrrrrrrrppqqqqqqqqqqiqqqpppppppprpqiqqqpppqqqqqqqqqqiqpI", // 40
        "..DDrrIrrrrrrrrrrrppppqqqpqqqqqqppppppprrpqqqiqppppqqqqqqpqqqqr.", // 41
        ".....rrrppprrrrrrrrpppppprpqqpqppppppprrpppqqiqpppppqqqiqpppqp..", // 42
        ".....rrpqqpprrrrrrrrrrppprpqppqpppppprrppppqqiqppppppqqiiqpprI..", // 43
        "....Ipqqqqpprrrrrrrrrrrrrrppppqppprrrrrppqqqqiqppppppqqqiiqpr...", // 44
        "...IqqqqqqpIrrrprrrrrrrrrrppppqppprrrrpqqqqqqiqppppppqqqiWiqpr..", // 45
        ".DrqqqqqppprrppprrrrrrrrrppppqqprrrrrpqqqqqqqiqppppppppqqWWiqpr.", // 46
        "DrppppppppppqqpprrrrrrrppppppqqprrrrrpqqqqqqqiiqppprrppqqqiiqpI.", // 47
        ".DDDDDrprppqqqqprrrrrrrpppppqqqprrrrrqqqqqqqqWWqppprrppppqqqpD..", // 48
        ".......DDpqqqqprrrrrrrIpqqppqqprrrrrpqqqqqqqqiiqppprrrppppppI...", // 49
        ".......DpqqqqpprrrrrrrIpqqppqqqprrrrpqqqqqqqqqqqpppprrrppppD....", // 50
        ".......qqqqqppIrrrrrrrppqqpIqqqpprrrrqqqqqqqqIpppppppppppppD....", // 51
        "....DDmiiqqpppppqprrrrpqqqpIqqqqprrrrqiqqpqiqqpppppppppppprD....", // 52
        "..DDpqiiqqppppqqqprrrrpqqqpIpqqqqprrpqiiqpqiqqqpppppppppprD.....", // 53
        ".DpqqqqqpppppqqqpprrrrpqqqpIpqqiqprrpqWiqppqqqqprrrrpppppD......", // 54
        "NrpqqqqpppppqqqppppppppqqqppIpqqpprrpqiipIppqpprrrpppppppD......", // 55
        ".DDDDDDDDDpqqqqpppppppppqqprrrppppprIpqqpIrrprrrrpppppqqprD.....", // 56
        "........DrpqqpprDrpprrpppIrrrrrrrrrrDIIIIpprrrrpprpppqqqqqpD....", // 57
        ".........DNNNDN...NNNNDrprrrrrrDNNN..rpIIpprprpprNNpqqqqqqqpDD..", // 58
        ".......................rrrrrrr.......rppprDrppprD..DppqqqqqpprD.", // 59
        "........................NNNNN.........NNND..NNND....NNNNNNNNNN.." // 60
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}


fun DrawScope.drawFlan() {
    val matrix = arrayOf(
        "................................DDDMrrrrrrrI....................", // 0
        "..............................DrpppppqqqqqqqII..................", // 1
        "..........................DDIrpppqqqqqqqqqiqqmmr................", // 2
        ".......................DIrrrrppppqqqqqqqqiiiiiimmI..............", // 3
        ".....................DrpqqqppppppqqqqqqqiiWWWWWWiqm.............", // 4
        "....................rrpqiiqprpppqqqqqqqqqiWWWWWWWiqp............", // 5
        "...................rpqqiiiqpppppqqqqqqqqqqiWWWWWWiiqI...........", // 6
        "..................rpqqqqqqpppppqqqqqqqqqqqiWiWWWWWiiqr..........", // 7
        "..................rqqqqqpppppppqqqqqqqqqqqiWiiWWWWiiqpI.........", // 8
        ".................rqiWiqppppppppqqqqqqqqqqqiiqqiWWiiiiqr.........", // 9
        "................rqiWWiqpppppppqqqqqqqqqqqqqiqqqiiiiiiqpI........", // 10
        "...............DpqiWiqpppppppppqqiiiiqqqqqqiqqqqqiiWiqpr........", // 11
        "...............rqqiiiqpppppppppqqqiWiiqqqqqiqqqqqqiiiqprD.......", // 12
        "..............rpqiiiqppprrpppppqqqqiWiqqqqqiqqqqqiiiiqprr.......", // 13
        ".............DrqqqqqqpprrrppppppqqqqWiqqqqqiqqqqqiiqqpprr.......", // 14
        "..............rqqqpppprrrrpppppppqqqiWiqqqqiqqqqiiqqppprr.......", // 15
        ".............rpqqqpprrrrppppqqqiiqqqqiiqqqqiqqqiiiqpppprr.......", // 16
        "............DrqiqpIrrrrpppppqqiiiiiqqiiqqqqqqqiiiqpqqqppr.......", // 17
        "............DrqqmIrrrrrpppprrrrrpqiqqqiiqqqqqqiiqqqiiiippr......", // 18
        "............IrqqIIrrrrrpprrCPPQPrRpqqqqqqqqqqqqqqqiiqIrrrpD.....", // 19
        "............IrqprrrrrrrprACPQEFFEPCpqqqqqqqqqqqqqqqrrPQQPrD.....", // 20
        ".............rprrrrrrrrrRCPPPQEEFFQCpqqqqqqqqqqqqprCQFWFEPA.....", // 21
        "............rrrrrrrrrrpRACCCOPQEFFFQArqqqqqqqqqqqrCEFWWWFQA.....", // 22
        "...........DrrrrrrrrrrpRCOCCCPQEEFWWQCrpqqpppqqprrQEFWWWFQA.....", // 23
        "............rrrrrrrrrrrrACCCCPQEEFWWWEQPrrppppprRPQEFWFQEPA.....", // 24
        "............rrrrrrrrrrprrACCCCPQEFWWWTQQCCCrpprRCPPPQEEPQAA.....", // 25
        "............rrrrrrrrrrprrRACCCCPQQEFFEPQQOCrpprACCCCPPQQPrr.....", // 26
        "............rrrrrrrrrrrrrrrACCCCCPPQEEEPCrrpppprrRACCCCArrpr....", // 27
        "...........rrrrrrrrrrrrppprrRACCCCCPOCCrrrppppppprrRAArrpqqpr...", // 28
        "..........DrrrrrrpqprrrrrpppprrrCCCCrrrppppppppppppprrpqqiiqr...", // 29
        "..........IrrrrrrrqqqprrrrppppprrrrrrppppppqqqqqqqqqqqqqiiiqD...", // 30
        "..........rrrrrIrrqqqqprrrrrrppppppppppqqqqqqqqqqqiqqpiIiiqI....", // 31
        ".........rrrrIIppppqqqqpppppppppppqqqqqqiWiqqpIirqqqrNSNIqqD....", // 32
        ".....rrrrrrrIIqqqqppqqqqppppppqppqqiiqqqqiqpprNSKrrrMMNMrppD....", // 33
        ".....rpprrrrrpqiiqqpppqqqqqpqqpmmrpIrmmpqqrrNrrrrrrrpppppprD....", // 34
        "......rrrrrrrpqqqqqqpqqqqprpqpNDSrNDDDSNrrDNrqiiiiiiiqqqpprr....", // 35
        "......DrrrrrrpqqqqqqqqqqpIDDIIDKrqpSTNrrNITrqiWWiqqiiiiqqpprD...", // 36
        ".....DpprrrrrrpqqqqprrrINSmrrSmriiqpppqqprIqWWWiqqqqqqqiiqqprr..", // 37
        "....DqqqprrrrrrpqprNDDNrrImqqqqqqqqpppppprpiWWiqqqqqqqqqiiiqqpr.", // 38
        ".rrrqqqpprrrrrrpprNNTSrqqqqqiqqqqqppppppprqiiiqqpqqqqqqqqqiiiqpD", // 39
        "DrrrpppprrrrrrrrppqqqqqqqqqqiqqqpppppppprpqiqqqpppqqqqqqqqqqiqpI", // 40
        "..DDrrIrrrrrrrrrrrppppqqqpqqqqqqppppppprrpqqqiqppppqqqqqqpqqqqr.", // 41
        ".....rrrppprrrrrrrrpppppprpqqpqppppppprrpppqqiqpppppqqqiqpppqp..", // 42
        ".....rrpqqpprrrrrrrrrrppprpqppqpppppprrppppqqiqppppppqqiiqpprI..", // 43
        "....Ipqqqqpprrrrrrrrrrrrrrppppqppprrrrrppqqqqiqppppppqqqiiqpr...", // 44
        "...IqqqqqqpIrrrprrrrrrrrrrppppqppprrrrpqqqqqqiqppppppqqqiWiqpr..", // 45
        ".DrqqqqqppprrppprrrrrrrrrppppqqprrrrrpqqqqqqqiqppppppppqqWWiqpr.", // 46
        "DrppppppppppqqpprrrrrrrppppppqqprrrrrpqqqqqqqiiqppprrppqqqiiqpI.", // 47
        ".DDDDDrprppqqqqprrrrrrrpppppqqqprrrrrqqqqqqqqWWqppprrppppqqqpD..", // 48
        ".......DDpqqqqprrrrrrrIpqqppqqprrrrrpqqqqqqqqiiqppprrrppppppI...", // 49
        ".......DpqqqqpprrrrrrrIpqqppqqqprrrrpqqqqqqqqqqqpppprrrppppD....", // 50
        ".......qqqqqppIrrrrrrrppqqpIqqqpprrrrqqqqqqqqIpppppppppppppD....", // 51
        "....DDmiiqqpppppqprrrrpqqqpIqqqqprrrrqiqqpqiqqpppppppppppprD....", // 52
        "..DDpqiiqqppppqqqprrrrpqqqpIpqqqqprrpqiiqpqiqqqpppppppppprD.....", // 53
        ".DpqqqqqpppppqqqpprrrrpqqqpIpqqiqprrpqWiqppqqqqprrrrpppppD......", // 54
        "NrpqqqqpppppqqqppppppppqqqppIpqqpprrpqiipIppqpprrrpppppppD......", // 55
        ".DDDDDDDDDpqqqqpppppppppqqprrrppppprIpqqpIrrprrrrpppppqqprD.....", // 56
        "........DrpqqpprDrpprrpppIrrrrrrrrrrDIIIIpprrrrpprpppqqqqqpD....", // 57
        ".........DNNNDN...NNNNDrprrrrrrDNNN..rpIIpprprpprNNpqqqqqqqpDD..", // 58
        ".......................rrrrrrr.......rppprDrppprD..DppqqqqqpprD.", // 59
        "........................NNNNN.........NNND..NNND....NNNNNNNNNN.." // 60
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'M' to Color(0xFF46238C),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'i' to Color(0xFFD2B4F0),
        'm' to Color(0xFFA578D7),
        'p' to Color(0xFFDC2D6E),
        'q' to Color(0xFFFA649B),
        'r' to Color(0xFF961950)
    )

    drawMonsterMatrix(matrix, palette)
}


fun DrawScope.drawDarkknight() {
    val matrix = arrayOf(
        "......................D......................................", // 0
        ".........................................0...................", // 1
        "......................DDD...D.......D....D......DN...........", // 2
        ".......................DD...D.......D..0D..D...DNND..........", // 3
        "........................K....D.....00m0K..DDD.DKNN.D.........", // 4
        ".......................DDD...D.....0mDK00KDS.DDDDDKD.........", // 5
        ".......................KGKDD.D....D0DKKDKKS.D.DSDNKKD........", // 6
        "......................DKKDSDDK....DDKKKKKKKN..DDKKKK.........", // 7
        ".....................DDDDSKKKK.....DDDKKKKKND.DNKKK..........", // 8
        "......................DNDSSDKD....D.DKKKKSSADDKKDD.......D...", // 9
        ".......................NSSDKKD...DKDKKKKKDSDDKKKDD....D..D...", // 10
        "......D.................DKKKD.....DDDKKDKDDDKNNKK....DD.D....", // 11
        ".......D...............NDSKK.....D0DKKKDSSDNNNKKD...DKDD.....", // 12
        "...................DKKD.DKKKKDKNKKDDKDDKKKKKKKKKDDKN.DS......", // 13
        "........D.........DDDS...KKDDKKKKKDDKDSDNKKKKKKKKKT.D........", // 14
        ".........DD.......NKKKKKD...DKDKKDDKDSDSDKKKKKKKKDNDDD..DD...", // 15
        "...........D......DDDKKKD..DKKDKKIKKDSSDDNKKKKKKKSDDD........", // 16
        "...........D.DD.DD.DKKKKDKKKKKKKKIKDIDSDDKKKNKKKSDDT.........", // 17
        "...........DDKDDKDKKKKKKKKKDDKKKKDDDSDDDNKKKDNKDSNT.DDD......", // 18
        ".............DSDDKKKDDDKKKKDKKKKKDDNDmIDNKKKDDNKKKKN.........", // 19
        ".....D......KKDSSDDTWDDNKKKKDDDKNWSNNDSSDKKKDDNKDTN..........", // 20
        "....D.......NDKKDSDDDTDDKKKKKKKKNTWDTSKDNKKKNDNKND...........", // 21
        "....N....DD..DKKDDSTTDTDDKKKKKKNNDWTDNNDKKKKNDNN.............", // 22
        "...DN...m0DK.....DSWWTDTSNNNNKKAKNSDTANDKKKKNNNK.............", // 23
        "...DD....0KKK....DSTTTSDTTNNKKKNNKKPPKNNKKKKKNNK.............", // 24
        "...KD..Im.DKK.....DDSTTSDDNKKKKKKKNSAKNNKKKKKNKKK............", // 25
        "...N.mDDIDKKKK...DKNDSTFDNKKKKDDKKKPPKNNKNKKKNKKK............", // 26
        ".mDK.IDDDDDKKKNNCNKKNDSTFPNKKDSSDKKANKNKKNKKKKKKKK...........", // 27
        ".mDKDDKDISDKKKNNANPANNNDTTSKKDDDDDDSSKNKKNKKKKKKKK...........", // 28
        "..IKKKKDDDKKKDKDNNANOCNNDTTSKKKKDSDKDKNKKKKKKKKKKKK..........", // 29
        "..DDKKDDDNKKTSNDNNNNNKKNNSTTSKDDKDDKNNNKKKKKKKKKKKKD.........", // 30
        "...KDDDDDKKTWSDDNDNNKKKNANSTTDSDKKKKNNNNKKKKKKKKKKKK.....NDD.", // 31
        "...DKDIDKKKTSDDDDDDNNKKARNDDTTDKKKKKNSNNNNKKKKKKKKKK....KKDN.", // 32
        "...DKKKKKKKTSDDDDDDNNKKANKKNDTSKKKKKKDDNDNNKKKKKKKKK...KNKNN.", // 33
        "...DKKKKKKKTDDDDDDDDNKKNKKDDDDTTKKNNNNKKNNNNNKKKKKKKD.KNNNKK.", // 34
        "...DNDKKNNKTDDDDNNDNKKKKKDDNDKDTSKAANNKKKKKDNKKKNKKKKKNNKDN..", // 35
        "...DDSKNKNKKKDDDNNDNKKKKKNDNDKKDTSANNNNKKKKNNKKKNKKKKNNNKDN..", // 36
        "...DDTDKKKDKKDDDDNDDKKKKKKDNKNNKDTENNDNNKKKKNKKKNKKKNNNKKDKK.", // 37
        "....KDTDKKKNKDDDNNDDKKKKKKNKDANNKDTSNDNKNKKKNKKKNKKKNNNDDDNK.", // 38
        ".....DDTDKKKKDDDNNNDNKKKKKDKNKKKKKDTSNKKNNKKKKKKKKKKKND..DNK.", // 39
        "......DDTDKKDDDDNNNNNKKKKKKDKKKKKKKDTSKNNNNKKKKKKKKKKD...DNK.", // 40
        "......mD.DKKSSDDNNNKKKKKKKKSKKKDKKKNDTSKNNNDKKKKKKKKD....DNK.", // 41
        ".......D.mDKSSNDNKKKKKKKKKKKKKKDSKKNKDTSNDNDKKKKKKND....NDNK.", // 42
        ".......D..DKKDNNKKKKKKNKKKKNNNNKDSKKNKDTDNDDKKKKDDD....NKNDN.", // 43
        "......D....KNDNKKKKKKDTKKKKKDDDDDKKKNNNDSDDDKKDD......NKKKK..", // 44
        "...........DDKKKKKKDSWTKKKKKKSSNNNDSSSDDDSDD..........DNKKK..", // 45
        ".........DDNKKKKNDTTTSDKNNNNDWWTTTWWWWTDDKSD..........DDKKK..", // 46
        "........NDDKKKKSWWWSKKKNDDDDTWWWWWWWWWWSDDK...........DDDNN..", // 47
        ".........DKNSKKSWWTKKKKDTWWWWWWWWWWWWWWTNDKKD..........DNN...", // 48
        ".........NKDWSKKSWWSKKKDTWWWWWWWWWWWWWWWSNNKK................", // 49
        ".........NKDWWSDKSWWSDKNNDTWWWWWWWWWWWWWTDDKD................", // 50
        ".........NKTWTWDDKWWWWSDNNKDWWWWWWWWWWWWWSNKD................", // 51
        ".........KKTWSSSDNWWWWWTDNDKTWWWWWWWWWWWWSDKD................", // 52
        ".........KKTWWDDNTWWWWWWSKNDDWWWWWWWWWWWWSDKD................", // 53
        "........DNKTWWWWWWWWWWWWWDKNNDTWWWWWWWWWWSDND................", // 54
        ".......DDKKTWWWWWWWWWWWWWDKKNKSWWWWWWWWWWSDND................", // 55
        "......DDKKKTDDTWWWWWWWTDDKKKDNSWWWWWWWWWWSDNND...............", // 56
        ".....DDDKKKTDKKGDDDGGDKKKKKKKKDTTTTTTTTTTGKKKNGDDDD..........", // 57
        ".....DNNNNKTSKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKNKNKK...DD........", // 58
        "......DNNNKDKKKKKK.KKKKKKKKKKKKKKKKKKKKKKKKKKNDD.............", // 59
        "......DKKKKDD...........DGGGGDDDGKKKKKKKKKKDNDDDD............", // 60
        ".......DDDD.......................DDD..DDKDDDDPDN............", // 61
        "...........................................DNNNND............", // 62
        "............................................................." // 63
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'I' to Color(0xFF7346AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'm' to Color(0xFFA578D7)
    )

    drawMonsterMatrix(matrix, palette)
}


fun DrawScope.drawBehemot() {
    val matrix = arrayOf(
        "................................................................", // 0
        ".....DA.........................................................", // 1
        "......DAAND.NAD.................................................", // 2
        "..AAD.DKNACANRAANA..............................................", // 3
        "..DDNAAANNNNNKNAAAC.DD......DDDDD...............................", // 4
        "......NNRRNKKKBBDDDKDDD.......DDDDD.............................", // 5
        "....DDAAAAANKKBBBBBDKS.D........DDDDD...........................", // 6
        "....DN.KNAARNKKDBBBBDD.............DDDD.........................", // 7
        ".......DANNAANNKBBBBBDD...D..........DDDDD......................", // 8
        ".........DNNNANKKKBBBBDD...............DD.DD....................", // 9
        ".............D..DKKBKDDBD....D..D........DDD.DD.................", // 10
        ".................KKKDDtBK.....D...D....DDDKKD..DD...............", // 11
        ".................KKDDtBBK......DD...D...DKKDKKKDDDD.............", // 12
        "................KKDDttBKK......DDDD.....DKKDDDKKKD.D............", // 13
        "..............DDDDtttBBKD.....DDDDDDDD.FFSDKKKKKKKKDD...........", // 14
        "............DDDBttttBBKKD...DDDDDDKDDKDDSFFSSDKKKKKN............", // 15
        ".........KDDDBttttBBBKKDDDKDDDKKKKKDKKDKKKDSSTTTTSDDND..........", // 16
        ".......DKDDDttttBBBBKKKDSSSDDDDKKKKDDDDKKKKKKDDSTFFTSDD..D......", // 17
        ".....DKKDDDBBBBBBBBKKKSSDDDDDDDKKKKDGKKKKKKKKKKNDDSSFS.D..D.....", // 18
        "....DBKDDKBBGBBKKKKKKSTDDDDDDKKKKGtccttKKKKDDDDKKKDSS.F.DD.DDD..", // 19
        "...DBBDDKKKKDKKDKKKDSDKDDDDDDKKKtcccttKKKKKKKDDKDDKKD.EE.DD...D.", // 20
        "..DBtBBDDKKKDDDDSDKKDDSDDDDKKGttccttBBtKKKKKDDDDKKKKKD..E..D..D.", // 21
        "..DtttBDDDDKDDDDDDDDDDDSDDDDDttttttctttBKKKKKKDDDDKKKKND.DDDDD..", // 22
        "..BtttBtDDDDDDDDDDDDDDDDDDDBttttttcdddttBKKKKKKKKKKKDDDDDKDDD...", // 23
        ".DtttctBttDDDDDtttDDDDKKKGttttttccdddcctttBKtcttKKKKDDDDttD.....", // 24
        ".KBttcttttttBBttccttBBBBtttttcttccdccctttttKKKGtctGKDtttcttD....", // 25
        ".KBtttcccctttctttcccttttttttcctttctctttcddcBKKKKKttBBtBtctttD...", // 26
        ".KKBttttttttcccctccctttttttcdctttttccttcddctKKKKKGtttttKDDBBtt..", // 27
        ".DKBttttttttttccddctttttccdcctttttcdddctcddctKKKKKtBtccDDDttttt.", // 28
        "..KKBBtttttctcccdddctBttcccttttttccddddccccctKKKKKDttcdctttccttK", // 29
        "...KKKBBBBtttccccdddtBttttttttttttcccddccctctKKKKKKKtttttDKDttKD", // 30
        "....KKKKKtttttcttccctttttttttttBBttttccccttttBKKKKDKBtttDDSDDDD.", // 31
        ".....KKKKBtBtttttcccctBBtBtBBttttBtttttttttttBKKKKKKttGDSWTSSD..", // 32
        ".....KKKKBBBtttttttcctBKBKBBKtttBKKDBBtttttBtBKKDBKKtcKDTWD.D...", // 33
        "...DKKKKKKBKBttttBttttBDttBKKKBBBBKKKBtBBBKtttKKKK.DttDGDW......", // 34
        ".DDDKKKKKKKKKttttBttttBKKBBKKBKBBBKKKBttttKBcctKK...DttDKD......", // 35
        ".KKKKKKKKKKKKKBtBKBtttKKKKKKBBBKKKKKKKttcttBcctG.....DtDDDD.....", // 36
        ".KBKKKKKKKDDKKKBKKBttKKKKKKKKKBBBKKKKKtccctttctG......tttK......", // 37
        ".KKKKKKKDSTDKKBKKKBBKK....KKKKKKKKKKKKtcccttttttD.....DtD.......", // 38
        "..KKKKDTTTDDKKKKKBBKD.......DKKKKKKKKKBtcttttttBD...............", // 39
        "..KKKKDWTDDKKKKKKKKD...........KKKKKKKKttttttttDD...............", // 40
        "...KKKKSTKKKBtGKDD................KKKKKKttttBttKD...............", // 41
        "...KKKKKKKKKBttK..................KKKKKKKtBBtcctD...............", // 42
        "...KKKKNKDKKDBBKD..................DKKKKKKBKtccctD..............", // 43
        "...DKKKKDN.DKBBBKD.DKD...............KKKKKtKtttcccD.............", // 44
        ".....D......DKKKDKKDKKD..............DKKKKBKKttttct.............", // 45
        ".............KKKKDDDDSND..............KKKKKKKttttttt............", // 46
        "..............DKKKGSDDD.....................DBBtttttD...KKD.....", // 47
        "...................D..........................DBttBBBKKKDDDD....", // 48
        "...............................................DDGDDDDKKDDKD....", // 49
        ".................................................DKKDDDDKDSK....", // 50
        "..................................................DDKKKNSDD.....", // 51
        "........................................................D......." // 52
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'G' to Color(0xFF0F5019),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD),
        't' to Color(0xFF0A6469)
    )

    drawMonsterMatrix(matrix, palette)
}


fun DrawScope.drawNecromancer() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        "..............................DDDDD.............................", // 2
        ".............................KKKDKDD............DDDDD...........", // 3
        "............................DDDDKKKKD..............DDD..........", // 4
        "...........................DDDDKSSSKD.................D.........", // 5
        "..........................DKDDKDTTTDKD................K.........", // 6
        "..........................DDDKDSWXXTKD.................N........", // 7
        ".........................DKDKKSWWWWWSD.........K.......DK.......", // 8
        ".........................DKDKSWDKSWSKN.......KKKK..DKK..K.......", // 9
        ".........................KDDKSTNZNWKKN.......KKKK.DKKKK.K.......", // 10
        ".........................KDDKDSTKWKTDD......D.KK..DKKKK.N.......", // 11
        "........................KDDDKKKTTTTTKN......D....D.DKK.K........", // 12
        ".......................DKKDDKKKSTTSTKN........D........D........", // 13
        "......................DKKKKDKKKDDDDKKD........D.D.D.N.D.........", // 14
        "......................DDDDKKDKKKDSSKKN..........KKKKDD..........", // 15
        ".....................DDDDDDKKDKKKKKKKKD.........KNNKT...........", // 16
        "....................KDDDDDDDKKKDKKDKKDD.........KNNK............", // 17
        "....................DDDDDDDDDKKKKKKKDDKD........NNND............", // 18
        "...................DDDDDDDDDDDDKRRKDDKKD........NNK.............", // 19
        "..................KDKDDDDKKKKDDKZZKDKKKD........NKD.............", // 20
        "..................DKKDDDDKKKNKKKZZKKRKKD........NK..............", // 21
        "...................DSDDDDKKKZRRRZZRRZKKKD......DNK..............", // 22
        ".....................DKDKKDKZZZZZZZZZKDND....DDKKKD.............", // 23
        "......................DKKKDKNNNNZZNRNKDD......DKDS..............", // 24
        "..................DD...DKKDKKKKKZZKKKKD.....DDDDDDDK............", // 25
        "................D..D....KKDDDDDKZZKDNSK.DD...DDDDDSD............", // 26
        "................D.......DKDDDDDKZZKDDW.DDDNDDKKDTDD.............", // 27
        "................D........KDDDDDKZZKDD...NKKDTTDKDSD.............", // 28
        "................D........KKDDDDKZZKDD..........NKK..............", // 29
        "................D........NKKDDDKNNKDKD.........NK...............", // 30
        "................D.D......KNKKKDDDDDKNN.........NK...............", // 31
        "................D.D......NNSSKKKKKKNKD.........NK...............", // 32
        "................D.DK.....NANSSSSNKNKNN.........NK...............", // 33
        ".................D......NKDSDSDNSKKNKKD........NK...............", // 34
        ".................D...D..NDNKKKKKDNKKDKD........NK...............", // 35
        ".................D...D..KNNNNSSSKKKKDKD.......NNK...............", // 36
        ".................D...D.DKKKKNNNKNKKKKKD.......NNK...............", // 37
        ".................N....KKDDKDKKKNNSKDKKKD......NN................", // 38
        ".................DD.DDNKDKKKKDKNKSKDDKKD......NN................", // 39
        "..................DDDSSKDKDDDDKDKDKDDDKD......NN................", // 40
        "......................DKDDDDDDKSKNKDDDKD......NN................", // 41
        "......................KDDDDDDDKDKNKDDDDD......NN................", // 42
        ".....................DDDDDDDDKKNKNKKDDDKD.....NN................", // 43
        ".....................KDDDDDDDKKKKKKKDDDKD....DDN................", // 44
        "....................DDDDDDDDDKKDDDDKDDDKD....NDN................", // 45
        "....................KDDDDDDDKKDDDDDKDDDDD....NND................", // 46
        "...................DKDDDDDDDKKDDDDDKKDDDD....NN.................", // 47
        "..................DKDDDDDDDKKKDDDDDKKDDDD....NN.................", // 48
        "..................KDDDDDKKDKKKDDDDDKKDDDD....NN.................", // 49
        ".................DDDDDKDDKKKKKDDDDDKKKDDD....NN.................", // 50
        ".................KDDKDDDKKKKKKDDDDDKKKDDD....DD.................", // 51
        ".................KKKDSDKKKKKKKDDDDDKKKDKD....NN.................", // 52
        ".................KKKDSDKKKKKKKDDDDDKKKDKD...DNN.................", // 53
        "..................KDTTDKKKKKKKDDDDDKKKKD....KNN.................", // 54
        "...................DSSSWWWWWDKDDKKKDSDW.....KNN.................", // 55
        "..................D..DSWWWWWWDKKWKTTWSKKD...NNK.................", // 56
        ".................D...DSWWWWWWWWWWDSTTWTD.D..NN..................", // 57
        "................D.D...............KKKST.ND..NN..................", // 58
        "...............D.KDDDD...............NNNK.......................", // 59
        "...............DNDSDSD..........................................", // 60
        ".................DDDD...........................................", // 61
        "................................................................", // 62
        "................................................................" // 63
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'D' to Color(0xFF4B4B4B),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF),
        'X' to Color(0xFFF5372D),
        'Z' to Color(0xFFD21E19)
    )

    drawPixelMatrix(matrix, palette)
}


fun DrawScope.drawMime() {
    val matrix = arrayOf(
        ".........................................", // 0
        ".........................................", // 1
        ".........................................", // 2
        ".....................EQQEQ...............", // 3
        ".....................QQEQ................", // 4
        "....................EQQQ.................", // 5
        "...................QQPP..................", // 6
        "...................QQP...................", // 7
        "....................PQ.....Q.............", // 8
        "...................EPP...PZQ.............", // 9
        "...............P...QPAACZXZP.............", // 10
        "..................QPAAZZZZZC.............", // 11
        "...............EEQPCANAACZZP.............", // 12
        "..............QAAAAPCARZCQP..............", // 13
        "..............QQAZZQPCAAZRP..............", // 14
        "..............EPCRAQQJPQCA.P.............", // 15
        ".............EECCCAQEPJJCRA..............", // 16
        "............FEQPQCPEQPJCPACQ.............", // 17
        "............EEPOPPOEQPCCACOJ.............", // 18
        "...........EEEPOCCOQQPACCACOQ............", // 19
        "...........EEQPACACQQPTCOOJJQQ...........", // 20
        "..........EEQQPPACPEPEFPOJOJJCE..........", // 21
        "..........EQQQQPPPQECEFECCOCCCQ..........", // 22
        "..........EQEQJPPPQQCQE.PJJJJJOQ.........", // 23
        "...........EQPPPPPPPCPE.QOOZOOOOQ........", // 24
        "............PJOOOOOCCCQ..CCCPJJOJF.......", // 25
        "...........EQJPQCOOOOOP..OCPJJOOJQ.......", // 26
        "...........QJCJCCCJOJOC..POCOJOOOOE......", // 27
        "..........QQOPOCCOOOOOC..QJOCQOOOOC......", // 28
        "..........QJCPJCCOOOOCC..QJJCOJOOJOP.....", // 29
        ".........FPJPJOCXCOOCCOQ..PJOOQQOJOPE....", // 30
        ".........QJJCJCOOPCCACOP..AOOQ2JOOCOPF...", // 31
        ".........QJ2PQPCQOOOCOOC..PCOOOOOOJJQQ...", // 32
        ".........QPPQFEPCCPPPPJQ..QOCCCOOJJJQQ...", // 33
        "........QQQOPFFFEQ.PQQQQ..QJOCPJQJPE.....", // 34
        ".......EQQCPJFFFFF.EEFFFE.FPCPPPPPQ......", // 35
        "......EQQQJJOFFFFF..FFFFE...Q..QQ........", // 36
        ".....FQQJQOJQQFFFE..FFFFFF.....PQ........", // 37
        ".....QPJJJJQQEQQQQ...FFQPQ...............", // 38
        "....QPPPPJQQF....E...EETTP...............", // 39
        "....CCPPQQQE.............QQ..............", // 40
        "...QPCCPQQQ..............QP..............", // 41
        "...PCCPPCQ......EQ........P..............", // 42
        "...PAPPPP....PQQPP.......QPP.............", // 43
        "...PPQQ......QPAAP.....QPCAP.............", // 44
        "..QQQQ........PAPQ......PPCCP............", // 45
        "..PPE.........PPQ........PPQQQ...........", // 46
        "...........Q..QQ..........EQEE...........", // 47
        ".............QE.............Q............", // 48
        "..........Q.QQ...........................", // 49
        "...........EQEE...............EP.........", // 50
        "...........QQQQ..............EQC.........", // 51
        ".........EEPQP..............QEPC.........", // 52
        "..........EPPP..............PPPQ.........", // 53
        ".........PPCP...............QPQPP........", // 54
        ".........QQQQ................PQQQ........", // 55
        ".........QQSQP...............QQQPP.......", // 56
        ".........QTTTE...............PTTQQF......", // 57
        ".............................ETTTTQ......", // 58
        "..............................ETTTQ......", // 59
        "..........QQEQQ...............QQEQPP.....", // 60
        "..........PPQP.................PPPQ......", // 61
        ".................................Q.......", // 62
        "........................................." // 63
    )

    val palette = mapOf(
        '2' to Color(0xFFE6B414),
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'J' to Color(0xFFF59128),
        'N' to Color(0xFF502314),
        'O' to Color(0xFFDC6919),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'R' to Color(0xFF821414),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'X' to Color(0xFFF5372D),
        'Z' to Color(0xFFD21E19)
    )

    drawPixelMatrix(matrix, palette)
}


fun DrawScope.drawBlueMage() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        "................................................................", // 2
        "................................................................", // 3
        "................................................................", // 4
        "................................................................", // 5
        "................KBBBBBD.........................................", // 6
        "..............D0UUUVVVVUUB......................................", // 7
        "............D0UUUUUVVVVVVVVUB...................................", // 8
        "..........D0UUUUUUUVVVVVVdddVc0.................................", // 9
        ".........K0U0UUUUUUUVVVVVVdddddc................................", // 10
        ".........DKKDD00UUUUUVVVVVVdddddc...............................", // 11
        "..............KDBUU0UVVVVVVVddddVU..............................", // 12
        "................KB00UVVUVVVVVVdVVVU.............................", // 13
        "..................D0UUUVVVVVVVVVVVVU............................", // 14
        "..................KBU0UUVVVVVVVVVVV.............................", // 15
        "...................DBUUUUVVVVVVVVTEEQPA.........................", // 16
        "...................DBUUUUVVVVVSEFFFFEQ.DUcccUD..................", // 17
        "...................KD0UUUUVUSEFFFFFSSUVVdddddVU.................", // 18
        "....................DD0UUUUSEFFFFS0VVVUUUUUUUUV0................", // 19
        "....................DDDU00SEEFES0UVUUUU0000000UU................", // 20
        "....................DDDPPQEEES0UUUUU00DDDDDDD00U................", // 21
        "....................APQQQQQS0UUUU00DDKKKKKKDDD00................", // 22
        "................DDDKDPQQSS0UU00BDKKKKKKKKKKDDD0K................", // 23
        "................0U0BKDDDUUUU0DKKKTDKKKKSSKKKD0D.................", // 24
        "................K00UUUUUU00DKKKKDFSKKKKFFDKKK...................", // 25
        "...................DB000BDKKKKKKNTDKKKKSSKKKB...................", // 26
        "....................DBBBBBBDKKKKKKKKKKKKKKKBUB..................", // 27
        "....................0U00UUUVVUBBKKKKKKKKD0UVUK..................", // 28
        "....................D0DD00UUUUUVVUBKKKK0VVVUB...................", // 29
        ".....................B0DDD00UUUUUUVUBKKUVUU0D...................", // 30
        "......................B0DDDD0UUUUUU0DKKVUU0D....................", // 31
        ".......................DDDDD0UUUUUUU0DBVU0DK....................", // 32
        "........................DDDB0UUUUUUU0DDUU0D.....................", // 33
        "........................KDDB0UUUUUUUU0KBU0D.....................", // 34
        "........................B0UUU00UUUUUUUBUUB......................", // 35
        ".......................BU0UUUVVUUUUUU0B0BK......................", // 36
        ".......................BUUUUUUUUUUUUUUBUU0......................", // 37
        ".......................UUUUUUUUUUUUUUU0UU0K.....................", // 38
        "......................KUUUUUUUUUUUUUUU0UUUD.....................", // 39
        "......................BUUUUUUUUUUUUUUU0UUUB.....................", // 40
        "......................0UUUUUUUUUUUUUUU0UUU0.....................", // 41
        ".....................KUUUUUUUUUUUUUUUU0UUU0.....................", // 42
        ".....................BUUUUUUUUUUUUUUUU0UUU0K....................", // 43
        ".....................0UUUUUUUUUUUUUUUUBUUUUK....................", // 44
        "....................DUUUUUUUUUUUUUUUUUBUUUUK....................", // 45
        "...................K0UUUUUUUUUUUUUUUUUBUUUUB....................", // 46
        "..................KBUUUUUUUUUUUUUUUUUUBUUUU0....................", // 47
        "................KDBUUUUUUUUUUUUUUUUUUUBUUUUUD...................", // 48
        "..............KD00UUUUUUUUUUUUUUUUUUUUKBUUUU0...................", // 49
        "..............KD0UUUUUUUUUUUUUUUUUUUUBKD0UVV0K..................", // 50
        ".................DB0UUUU0DDDDDB0UUUU0KNADBBBDD..................", // 51
        ".......................KPEEEEQPKBBBBK.AQPEEEQD..................", // 52
        "........................EEEEEQQ.......DPP...PD..................", // 53
        ".........................NNNKK..................................", // 54
        "................................................................", // 55
        "................................................................", // 56
        "................................................................", // 57
        "................................................................" // 58
    )

    val palette = mapOf(
        '0' to Color(0xFF2D4691),
        'A' to Color(0xFF823719),
        'B' to Color(0xFF1E3782),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'U' to Color(0xFF2D5ABE),
        'V' to Color(0xFF5A91EB),
        'c' to Color(0xFF14A5A5),
        'd' to Color(0xFF5AD2CD)
    )

    drawPixelMatrix(matrix, palette)
}


fun DrawScope.drawDragon() {
    val matrix = arrayOf(
        "................................................................", // 0
        ".D..............................................................", // 1
        "..DD............................................................", // 2
        "....DD..........................................................", // 3
        "......DD........................................................", // 4
        ".......DD....................................................DD.", // 5
        "........DN.................................................DD...", // 6
        ".........DNN.............................................DD.....", // 7
        "..........KKND.........................................DD.......", // 8
        "...........KKAA....................D...D..............N.........", // 9
        "...........DNKNPD..................PCD.DA..D........DNN.........", // 10
        "............KDDKKNN............CADDDEQANPNN.DPP...DNKK..........", // 11
        "............KKDDDKNC...........PEQANDPNKDQANDA...NKKKN..D.......", // 12
        "............DDDDSDKNAN.....DADDDKNNKKANKNPANKK.NKKKDK...ND......", // 13
        ".............DSDDDDNKKA.....QQCAKKNKKAAKKANKKNQNKKDD....PN......", // 14
        ".............DKDDDSDNKNA.....QANKKNPNNAAKANKKCAKNDDD..DPCD......", // 15
        "..............KKSDDSDDKNND...NANNNNKKNKKKNNKNQAKDDND.AQPD.......", // 16
        "..............KKDSDDSSDKKAQPNKKNCPNKKKKNKKNKNQPNKKK.DPAS........", // 17
        "..............DDNTDDDDDDKKNPQNKKKKKDDKKDPANKKPEEPNK.NPD.........", // 18
        ".............KDDDSSDDSSDDKKKNANKKKKNNKT.DACANKNQEPNAKAA.........", // 19
        ".............KDDDSDSTDDNDDKKKNANKNKDDN..SKKNPPNKAQANAKN.D.......", // 20
        "............DDSSDDNKNKKKNKKNNAQPAQSNDNSSNKKKNQEAKNNANNKDN.......", // 21
        "............DDNKNAAAPCNACNKKANAPKNAQDDDKKKKDKKNPNKNAANNNN.......", // 22
        "..........KKNACKNANNNNKKKKKAAKKNKKKKADNKKKKNDKKKNNNAAPCD........", // 23
        ".......DNNNKKKKKKKDDDDDDNKNKKNKAKKNKKNDDKKKKKKKNAAPADNN.........", // 24
        ".....D......DDKKKDKNSSKKANKDDDKPNKKKNDDDKKKKKKKKAPAPNNPCD.......", // 25
        ".................DDDNDDNAKDDDDKNAKKKNNNDPSDKKKKDDNNKNANPP.......", // 26
        "..................KDNKNNKNDSDSNKAPCNKAANDPQDDKKDNNKNKSSNA.......", // 27
        "...................KNAKKNDDNSSNKNNCPNKAADSDDDKDTSDANNSTTN.......", // 28
        "..................DKANKKKKKDDSNKNAKKANNANDSSDDD...TPNDS.........", // 29
        ".................NKNKKKKKKKKDNDNNNCNKAKKNNKNDQDS...TAS..........", // 30
        "..............D...NKKKKKKKKKKKDKCNKNKPNNADPTFSDNS...T...........", // 31
        "...............NKKKKKNNNNKKKKKKKNAKNKPCNANNDDDSDNT......CP.D....", // 32
        "...............KKKKNNNPPAANNKKKKNNNKKNPNNNNDDDSDNS.....NN.......", // 33
        "............AAKKKKKNANNPACANKKKKNNKKKKNNNKKDQSDDDDS...DNKNDD....", // 34
        "............KKKKKKNNCPANACPNKKKKKKKKKNCAANKKDSFSDDKD.KKKDT......", // 35
        "..........DNKKKKKKKKNCCNAAJCKKKKKKKKNPQENANKKDQSDDNKKKKKDD......", // 36
        "........DAANKNNKKKKKKKNNANPCKKKNKNNKNPPAAAKKNDSFSNDKNAND........", // 37
        "........PPNNKNKKKNNAANNNNACAKKNNNANKNAANNNNKDSSPDNNKDDNN..D.....", // 38
        "......DDACCAKKKKKNNNPANANKNKKKKAAANKKNNNAQAKDQEESNNS..T.D.......", // 39
        "........KKNAKDDNKKNKNNNNKKKKKDNANNNNKKKNPPNKNSPPPNK.............", // 40
        ".......DNKKKNPNKKKAKKKKNNNKKKDDNNNNNKKNNANKKDSTTDKS.............", // 41
        "......DAKKDKNDDKKKCNKSDKDSDKKDDDDNDKKKANKKKKKDDDNS..............", // 42
        "......AANNPKDKDT.DPAKS.....DKDDDDDDKKKNANKKKDNKNDS..............", // 43
        "......PQCAAKDD...SACNN......SKDPDDPNKKNNANKKKDKDD...............", // 44
        "......CNKKKDNS.SNKANNKDST.TSKKKPNNPNKKAANKKKKKKKS...............", // 45
        "......DDNKKDDSKKKKPNANKNKKKKKKKKKKNNKKNAANKKKKKKKKK.............", // 46
        ".......DNNKKKKKKKKPNNPCKPNKKKKNKKKKKKKKKNNANKKKKKKKKK...........", // 47
        "........KPPNKTDKKKCCKNPSNSPKKKKNKKKKKKKKNNNNCKKKKKKK............", // 48
        "........KNQQNT.TDKADKKKPKKKNKKKKKKKKKKKKKKNNNNNKKKD.............", // 49
        "........NKAANNT...DPTTSNSTSST.TSSDKKKKKKNNANAANAPT..............", // 50
        "........NNANNNS...SS...S..........TSDKKNNKSKNNNDSD..............", // 51
        ".........APKNAPST.TT.................SKPKNEKKNFT................", // 52
        ".........KNKNKAANT....................SADNCKSKAS................", // 53
        "......DKKKKNNAAANNSSSTT...............SD.TNT..S.................", // 54
        "......DKKKKDDNANKNAPQQADTT............TS..S.....................", // 55
        "..........DDDNKDDNKNCNKDS..............T........................", // 56
        "...............DNDDNNDSDSS......................................", // 57
        ".................DPDNPQDDPS.....................................", // 58
        "..................DSTDDNDQPS....................................", // 59
        ".......................DDSEDT...................................", // 60
        ".........................DDDDS..................................", // 61
        "............................KKD................................." // 62
    )

    val palette = mapOf(
        'A' to Color(0xFF823719),
        'C' to Color(0xFFAF501E),
        'D' to Color(0xFF4B4B4B),
        'E' to Color(0xFFFAC382),
        'F' to Color(0xFFFFE1AF),
        'J' to Color(0xFFF59128),
        'K' to Color(0xFF141414),
        'N' to Color(0xFF502314),
        'P' to Color(0xFFBE6E41),
        'Q' to Color(0xFFE19B5F),
        'S' to Color(0xFF8C8C8C),
        'T' to Color(0xFFC8C8C8),
        'W' to Color(0xFFFFFFFF)
    )

    drawMonsterMatrix(matrix, palette)
}


fun DrawScope.drawBlackWaltz() {
    val matrix = arrayOf(
        "................................................................", // 0
        "..................................x.............................", // 1
        ".................................3F.............................", // 2
        "................................3yx.............................", // 3
        "................................FFx.............................", // .
        "...............................xyEx.............................", // .
        ".......................bb......xxE3......aaaa3..................", // .
        ".......................Ca.....3FFx3......2ccccab................", // 7
        "......................37C2....EFxD2.......cCg7a82v..............", // 8
        "......................27gC....FxDAD.......C7gg0g8c3.............", // 9
        "......................170Ca..3xFyxD.......Cgg00gggc3............", // 10
        "......................CgggC2.DxFxxD3......cgggggg7gc3...........", // 11
        "......................a7g01DxFyFxEDA.....2a777gggCC7a3..........", // 12
        "......................3gDEFFxwyFwFxxE3..aC77gg0gC7CCg1..........", // 13
        ".......................70AE0DDAxxEyFxxD1g7770ggCCaC1g7..........", // 1.
        ".......................000000YAAYAAEExEEDg7gC00aaagg0Ca.........", // 1.
        ".......................0gg0100000021020g77gg0000g1C70ga.........", // 1.
        "........................g0g12000127g77g0g0g0g00g0g7gC0a2........", // 17
        "........................00g1110027gCCCCg0g0000000C7C7g0C........", // 18
        "........................1ggaC10g7Ccag71C00g0g0g0777a7C01b.......", // 19
        "........................30ggCa0CCaaag1g0g00g00007gaCCC031.......", // 20
        "........................100ggC1gaga0ggga0g0000007ga0ag02........", // 21
        "........................1107ggQ1177g0gCcCg000g0g0gC1g0g0........", // 22
        "..........................0777Q1777g000CCg3g.b000g0100001.......", // 23
        "..........................7Cg7QQ77C70g0gCCa3..0g0g0g000032......", // 2.
        ".........................C8CgC12C87g2.20gaa2..13gg0g00gg3.2.....", // 2.
        "........................2877Ca12CCC723DDg8c2....00g000gga.......", // 2.
        "......................3322Qg82Q12787013.28Ky3...00g000ggg.......", // 27
        "......................3y2Q7C82DQ287C7a..32by22..20gg00g00a......", // 28
        "......................22ADCc2AE22ccgC73..Q23yx3.3100g0g000v.....", // 29
        ".....................32xAg8822DF12caCa1..2F1QDE.330000g0g31.....", // 30
        "..............3......DAQgc82A21g12bcCaa1.EANWI22.320000031.2....", // 31
        "............3E...F2JD310cc21227Q221c878a..2233x3..303g0g.3......", // 32
        "............wJ33FFE3..38ab1322Q2Q222cc78a3x322x2...0.g0g2.2.....", // 33
        "...........FEEFFJ32...a7b13zy222a22D2cacCv2..x22...g.1001.......", // 3.
        "..........wwFFF2.....2aa21wz32Q2222F22cC82...221...3.3g303......", // 3.
        "........FwFEEFE3.....aC201yw3133222332a8CCa.2.2.......g0.g......", // 3.
        ".......FEE..xYF3....3721g0233222a222322777C1..........3g..3.....", // 37
        ".....3F2....wF3.....202g0g13w22Q272A2120Cgg7...........32.......", // 38
        "....3......wE3......a1203233w32Q12212217C7C11...........3.......", // 39
        "........2FF3.........02....3.32227212212gC13....................", // .0
        ".....................32...3x222120231122122.....................", // .1
        "......................2.....11221323.x123.3.....................", // .2
        "...........................2xD1.2.33.331........................", // .3
        ".........................32x213.................................", // 44
        ".........................DEDx3..................................", // 45
        "..........................323...................................", // 46
        "................................................................" // 47
    )

    val palette = mapOf(
        '0' to Color(0xFF0C0C10),
        '1' to Color(0xFF262630),
        '2' to Color(0xFF484855),
        '3' to Color(0xFF737684),
        '4' to Color(0xFFA2A6B4),
        '5' to Color(0xFFD0D4E0),
        '6' to Color(0xFFFAFAFF),
        '7' to Color(0xFF121C44),
        '8' to Color(0xFF203A7A),
        'A' to Color(0xFF301C12),
        'C' to Color(0xFF0F3444),
        'D' to Color(0xFF58301C),
        'E' to Color(0xFF844E2D),
        'F' to Color(0xFFB27648),
        'I' to Color(0xFF55267A),
        'J' to Color(0xFF70520C),
        'K' to Color(0xFF4E8080),
        'N' to Color(0xFF8E2058),
        'Q' to Color(0xFF301644),
        'W' to Color(0xFF8444B6),
        'Y' to Color(0xFFDAAC20),
        'a' to Color(0xFF204444),
        'b' to Color(0xFF346262),
        'c' to Color(0xFF1C5C70),
        'g' to Color(0xFF122626),
        'v' to Color(0xFF76A8A8),
        'w' to Color(0xFFDAA270),
        'x' to Color(0xFF8E4E3E),
        'y' to Color(0xFFC0765C),
        'z' to Color(0xFFEBA284)
    )

    drawMonsterMatrix(flip(matrix), palette)
}


fun DrawScope.drawBizarrosephiroth() {
    val matrix = arrayOf(
        "................................................................", // 0
        "................................................................", // 1
        "...........................32..33..22...........................", // 2
        "...3......................3213.33..22b3.....................b...", // 3
        "....b3..................232a2222222a2123..................3b....", // .
        ".....bb3...............32222Q2122a212122................3bb.....", // .
        "...vKvb2b23............3122321a1101232323............3bb2b333...", // 6
        ".....bb2aaaa23.........312..31111g12..322..33.....vbaaaa2bb3....", // 7
        ".....3aaaaa1Q22bb......232.3EgC0g11D22232.23...3222Q1aaaa13.....", // 8
        "......bbbaaCQ2QQ1bb....23221bD0011g1212222...3KaQQ2Q1aabbb3.....", // 9
        ".......3222aa1QIQ12....3233K2K30a1AA2cb1.....b1QIQ1Ca2223.......", // 10
        ".......v3b1aa1IIQQa3...223K3223D10AAE2b2x...3aQ2I221aa233.......", // 11
        ".........bbaaaaQ2QQ1a332222x2E22xx2DExDgC3321QQQ2aaaa23.........", // 12
        "..........3baa1a11Q0111I82D1122K22E2b2g88c1gQQ1011aab3..........", // 13
        "..............b1aa11Q12822Q028dvvdx12Kg8c22E10C1gb3.............", // 1.
        "...............3b112gg112K7g122bK2xE2C011EE2JQ12v...............", // 1.
        "...................2Q022KK7121DE12bKaDgggaFDD3..3...............", // 1.
        "..............3b...222a8C700111C2b32D011CKC13...bKb.............", // 17
        "............3b1a233a222KvC1gDE11DDDD1g1a12a21aaa1Q12............", // 18
        "...........32QIQQ11Q1228cb10DEDQ2221D3g201ggQQQIQI2Q23..........", // 19
        ".........va22QQQQQQQQ1JxbFED1012112AD3.2gg111QQQ11Q2Q1a.........", // 20
        "........3a1111111111111AJEx23gE11OEEO12..2g111111aaa11aa3.......", // 21
        ".......b22aaaaaaaa1ag2v.2AD3FaEE11DR11113..aaaaa2aaaa22222......", // 22
        ".....321222aaaa3333....110g1aDEE212011gQQ1.......32a21222223....", // 23
        "...v22221212a3........11101aaDD2E11211gAQQa3.......b1a21223223..", // 2.
        "..22..22222b3....32.31QQD012aDD2E1211gg1QMQ1a12......b33332.....", // 2.
        ".....3.33........2111QQ0AED1AOD22121a1ga11QQ1Qa3................", // 2.
        "................31QQQA10QJE1A1111QQ21N01aa111Q1a................", // 27
        "................a1Q10QQNDD1A1QD11EMNQNN0g1g1QQQ13...............", // 28
        "...............21Q2.QMsNQ01EYJEwlkYxM1NNMA11.2QQ1...............", // 29
        "..............31QQ.3QyN2g1DFYkmmmlkYNE1AQ1M11.QQ13..............", // 30
        "..............aQ2..QxPa3g1DYYlmmmmkkxED021a313QQQ1..............", // 31
        "..............11...QPN2.01EYYklmmlkkFDx1231a2331Q1..............", // 32
        ".............32...QQP23311xFYkklkkkYxD2a1.2a02..QQ3.............", // 33
        ".............3...QQ2N2.21C2EFYFYkkYYD122g.3ag...221.............", // 3.
        "................Q2..N2.212aEFxwFYYYJ22F2g.31g..3..2.............", // 3.
        "...............22...3N2312Fa1x2FFEE2122a2.113...................", // 3.
        "...............Q.....N2.ga2a222222E11aC13.1g....................", // 37
        "...............2......233111a2FFa211Q111.122b..233..............", // 38
        "...............Q......31.111a2EEa11a111.32..a11.................", // 39
        "................2.....32..111a2a111AA1..a.......................", // .0
        "......................x3...111111gAAA...1.......................", // .1
        ".....................33.....3111g0AA3...33......................", // .2
        ".....................2........31112......2......................", // .3
        ".....................2....................3.....................", // ..
        "..........................................2.....................", // ..
        "................................................................", // 46
        "................................................................" // 47
    )

    val palette = mapOf(
        '0' to Color(0xFF0C0C10),
        '1' to Color(0xFF262630),
        '2' to Color(0xFF484855),
        '3' to Color(0xFF737684),
        '4' to Color(0xFFA2A6B4),
        '5' to Color(0xFFD0D4E0),
        '6' to Color(0xFFFAFAFF),
        '7' to Color(0xFF121C44),
        '8' to Color(0xFF203A7A),
        'A' to Color(0xFF301C12),
        'C' to Color(0xFF0F3444),
        'D' to Color(0xFF58301C),
        'E' to Color(0xFF844E2D),
        'F' to Color(0xFFB27648),
        'I' to Color(0xFF55267A),
        'J' to Color(0xFF70520C),
        'K' to Color(0xFF4E8080),
        'M' to Color(0xFF581234),
        'N' to Color(0xFF8E2058),
        'O' to Color(0xFF762A0C),
        'P' to Color(0xFFC63480),
        'Q' to Color(0xFF301644),
        'R' to Color(0xFF5C0F16),
        'Y' to Color(0xFFDAAC20),
        'a' to Color(0xFF204444),
        'b' to Color(0xFF346262),
        'c' to Color(0xFF1C5C70),
        'd' to Color(0xFF348EA5),
        'g' to Color(0xFF122626),
        'k' to Color(0xFFF8D43A),
        'l' to Color(0xFFFFEE76),
        'm' to Color(0xFFFFFCC0),
        's' to Color(0xFFEE66AC),
        'v' to Color(0xFF76A8A8),
        'w' to Color(0xFFDAA270),
        'x' to Color(0xFF8E4E3E),
        'y' to Color(0xFFC0765C)
    )

    drawMonsterMatrix(matrix, palette)
}


fun DrawScope.drawMalboro() {
    val matrix = arrayOf(
        "..........................323...................................", // 0
        ".........................3D211..................................", // 1
        ".........................23..1D........3........................", // 2
        ".............................222...3..x22.......................", // 3
        ".....................3F2.....31a...1..Dx2.......................", // .
        "................1D3..3xx..1..1a3..2g2.3D.....x2.................", // .
        "...............33322..Dx..2121a2J1H1G.3x3...2FD.................", // .
        "..................322.2FxDDa12gDJD11112x333.D12.................", // 7
        "...................12b2Ax2xg1QDJJJgADxx21D33x2..................", // 8
        "...............32..11aADA2xAD0JJJJ0xFxDADJA223..................", // 9
        "..............3yxx3.111g02220DJDJDDx2201DDAD1...................", // 10
        "..............3x22Fx221A02x221JJDA2x21ggADA01..3................", // 11
        "..................2x2xx2012221DD11DD1A0DJDgDg12D2...............", // 12
        "..................21A1221a222E22aH2D111DEADAa1AxA21.............", // 13
        "...............3D11100D1a22EFF2EEE222a1DA1g001x1a13.....a22.33..", // 1.
        "..............DD322DA1a22a222aJFE2E2aagg0A2xxx202..3...D2a221...", // 1.
        "..............33..1M1aa22axxxxE2112EE12122xxD1A2..22..2D231122..", // 1.
        ".................2v10111DDADDD122x211g2gD22A01a11D1...22....12..", // 17
        "......333........210ggD21DDDDDDDDD12EDgaaD10AAA112...322....31..", // 18
        "....3233a12.......20gDDAD2320x212EDA12g11g10ADDD2.3D3.a1....3...", // 19
        "....3....213......21A1AD21AA0AA102E2DA2D11gDDDAADDD2..222.......", // 20
        "..........a1...Dx2DgA11DAAAAA00A000221021ggAJDJD22.....1a3......", // 21
        "...........13...32Dg01DADAAA00000001AAA1Dgg00DJJ.......3a1......", // 22
        "...........12....31gg01ADAA000000001AA01A11gg11AE.......11......", // 23
        "...........Ab...3JDD101AxAADDDDDDA01AA0201ggg112g...A...1D3.....", // 2.
        "......3DE221b...JJJA01AADAxSxyxSDMA2110111gg011a2ga2g..3a13.....", // 2.
        ".....322222A3..23.00A1A1AxxxyEAAAAD22DA11ggg0011a202...121......", // 2.
        "..222222321a3....31A12ADxyxyDA1DDD2DDADg1g10A1A11213..212A......", // 27
        "..2DDE2...Aa1...311Dg1DADnxFAEDDEDD2D21g1JD00AAA11221a12DD......", // 28
        ".2DDD13...10aa11a11A1gA2AxxxADDDD3311gA1DJJD1g2A211111221.......", // 29
        "31D12....212g11111AD1A1AAEExD22E2211AADDDD0J11.D..1111A23.......", // 30
        "33D33....213.31013.D1JJD0SSE00gggag01gJJDJD0a1.D...322.....21A1.", // 31
        "332.2....21a......22EJJAAEx2g111DJDADDAJFJJ01D.3D.........2D3...", // 32
        "..3.......11........DA0AFx22011DDEJJ1DDADJJ0.12..........3D2....", // 33
        "..........2113a1a12..10D23110AJJ0AJD0AJD10Ag..3...........D1....", // 3.
        "...........210111aa13ggJ021g1gJJ010A01AA110gg3.32112......113...", // 3.
        "............11111111011A021111DAga1g101gga0g1011g1111.....3a2...", // 36
        ".........32agg1A001012a1gE21100A01a1a011111011012J21g2.....gA...", // 37
        "........222a2100A0ga2a111EE1gA010aJ1aAA1a1110A1211g1ag..2..g1...", // 38
        "...2g132a1111g1EEED1a1111EJaAA0AAa2Ga01012aaaaa1gg1g11g1a..11...", // 39
        "...2.11111331AE211a11Q1012aa10A012Da10111g1aa11A0A0Ag11g33112...", // .0
        ".......3.....J211112A0A02Ja11AD012D2gA0A11A011A00gg10g111111....", // .1
        "............3E211111aDD2J2111ADA122a1AAA012EEEEED....20Ag01.....", // .2
        "......v33...J213.11A1a22aa110AA1AaE1aagg2E22111D22..............", // .3
        ".....2222aaD211...211111111a....2122Da2E2D11A2221E3.............", // ..
        ".....1.3321112.....3210112......2A11a2a11113..32122.............", // ..
        "..........2......................E211A111A.....221..............", // .6
        "......................................3........................." // 47
    )

    val palette = mapOf(
        '0' to Color(0xFF0C0C10),
        '1' to Color(0xFF262630),
        '2' to Color(0xFF484855),
        '3' to Color(0xFF737684),
        '4' to Color(0xFFA2A6B4),
        '5' to Color(0xFFD0D4E0),
        '6' to Color(0xFFFAFAFF),
        'A' to Color(0xFF301C12),
        'D' to Color(0xFF58301C),
        'E' to Color(0xFF844E2D),
        'F' to Color(0xFFB27648),
        'G' to Color(0xFF0F341C),
        'H' to Color(0xFF1C6230),
        'J' to Color(0xFF70520C),
        'M' to Color(0xFF581234),
        'Q' to Color(0xFF301644),
        'S' to Color(0xFF941C26),
        'a' to Color(0xFF204444),
        'b' to Color(0xFF346262),
        'g' to Color(0xFF122626),
        'n' to Color(0xFFB24816),
        'v' to Color(0xFF76A8A8),
        'x' to Color(0xFF8E4E3E),
        'y' to Color(0xFFC0765C)
    )

    drawMonsterMatrix(flip(matrix), palette)
}


fun DrawScope.drawGiant() {
    val matrix = arrayOf(
        "................................................................", // 0
        "..............................2.......2.........................", // 1
        ".............................32...3....2........................", // 2
        ".............................2a..2112.323..23....3..............", // 3
        ".........................2...12a12221a32v.2113..32..............", // .
        ".........................g2.211Aa2a112aA3b21gg1a3a..............", // .
        ".........................2g12g0012211A0A0321221221..............", // .
        ".........................g2a2000A2AE1g01g32233b21g2.3...........", // 7
        ".......................g0aba200A1DEDg0112223K3321g1g1...........", // 8
        ".......................2ga2120A1A1D10g2011abbab2a11g............", // 9
        ".......................112111A11gAAAaa1A111a1ga2111g3...........", // 10
        "......................31gggA1A2gabb22A2D111agga112211...........", // 11
        ".......................a1A11101a21122121g2211113v2001...........", // 12
        ".......................32212012223322211g121A123g1212...........", // 13
        "........................1gg001bb2b22bKK21012221gba002...........", // 1.
        "........................ggg001a2a21ab2aD1g0000110aa11.3.........", // 1.
        ".......................101a10011011gg1D1g0g00001a11A111.........", // 1.
        "......................310011g0011121a1g0g100011a2a023a12........", // 17
        "......................31gA0001gA01g100111ggg3.2000222211........", // 18
        "......................21a0A00.30g1g1110g1g01...0A1g22111........", // 19
        "......................12g110g..001gag00g0g0....302332213........", // 20
        "......................1121g02.30g1111A10ggga....11121A1.........", // 21
        ".....................31121g013g1122211a1110A3...0a2321g.........", // 22
        ".....................111gg0A001012321111Aa1gg...1122211.........", // 23
        "..................3b.100g0A32120A12110002221g1..3122a12.........", // 2.
        ".................322ag210033120011a101002ab2113..A2211..........", // 2.
        ".................31220gg0g3g20001ag11100022ba10.3gga01..........", // 2.
        ".................32ga10103211ga01a2a1Ag12g2211122aabag..........", // 27
        "...............332a2g1003.AA0221g1211002Ka12111g102221..........", // 28
        "..............3b2a2ag11...1gab21A1211001b31g111gg01aa1..........", // 29
        ".............b3212a10012..a122a10D21DA112Kb10002111g00..........", // 30
        ".............3b121111ggA.1221a11002D3.g11a22210..2g0gv..........", // 31
        "...........v3b121112132..1b2ag11g1....211a2321A3................", // 32
        "..........333112aa213...22K210000......0ga2321A0................", // 33
        ".........3331a222213....2a2211A02.......012b21A0................", // 3.
        "........3v311222213......g1g11A01.......A1111gA01...............", // 3.
        ".......3.311222213.......ggA1g11g.......301Agg101...............", // 36
        "......v.3112221a3........31g0a21g........g0111011...............", // 37
        "......v311a22aa3..........0111a1g........010001g1...............", // 38
        "......311222aa3...........21111g2........211121gg...............", // 39
        "...3v311aD211.............21110g..........201211g...............", // .0
        "..3v31122D11..............12a1g13..........g122112..............", // .1
        ".3.31aa2211..............10111Ag3..........3122111..............", // .2
        ".3v2122211..............1ga10A001..........21100003.............", // .3
        ".3va12211.............212a1g1001g..........20g11g02.............", // ..
        "..32aa11..............011g0A00113..........3012K210.............", // ..
        ".v111a3................33333................011a11g3............", // .6
        "............................................3222222............." // 47
    )

    val palette = mapOf(
        '0' to Color(0xFF0C0C10),
        '1' to Color(0xFF262630),
        '2' to Color(0xFF484855),
        '3' to Color(0xFF737684),
        '4' to Color(0xFFA2A6B4),
        '5' to Color(0xFFD0D4E0),
        '6' to Color(0xFFFAFAFF),
        'A' to Color(0xFF301C12),
        'D' to Color(0xFF58301C),
        'E' to Color(0xFF844E2D),
        'K' to Color(0xFF4E8080),
        'a' to Color(0xFF204444),
        'b' to Color(0xFF346262),
        'g' to Color(0xFF122626),
        'v' to Color(0xFF76A8A8)
    )

    drawMonsterMatrix(flip(matrix), palette)
}
