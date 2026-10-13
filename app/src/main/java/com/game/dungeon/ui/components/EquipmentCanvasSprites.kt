package com.game.dungeon.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.models.Rarity
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.tooling.preview.Preview

/**
 * Procedural Code-Based Canvas Drawing for Equipment Sprites
 * Upgraded with Relic-style multi-layered procedural art and rarity-based progression.
 */
@Composable
fun EquipmentSprite(
    item: Item?,
    slot: ItemSlot,
    modifier: Modifier = Modifier,
    animTime: Float = 0f
) {
    Canvas(modifier = modifier) {
        if (item != null) {
            drawItemSprite(item, animTime)
        } else {
            drawEmptySlotPlaceholder(slot)
        }
    }
}

fun DrawScope.drawItemSprite(item: Item, animTime: Float = 0f) {
    val sizeMin = minOf(size.width, size.height)
    val s = sizeMin / 100f
    val offsetX = (size.width - 100f * s) / 2f
    val offsetY = (size.height - 100f * s) / 2f

    withTransform({
        translate(left = offsetX, top = offsetY)
    }) {
        // --- NEW MYTHIC INTERCEPTOR ---
        if (item.rarity == Rarity.MYTHIC && item.setId != null) {
            drawMythicSetPiece(item, s, animTime)
            return@withTransform
        }
        val isMagicalWeapon = item.slot == ItemSlot.WEAPON && (item.magicBonus > 0 && item.attackBonus == 0L)
        val isAxeOrBow = item.slot == ItemSlot.WEAPON && (item.emoji in listOf("🪓", "🏹") || item.name.contains("Axe", ignoreCase = true) || item.name.contains("Bow", ignoreCase = true))
        val isRobe = item.slot == ItemSlot.ARMOR && (item.name.contains("Robe", ignoreCase = true) || item.name.contains("Garb", ignoreCase = true) || item.name.contains("Gown", ignoreCase = true) || item.emoji in listOf("👘", "🔮", "🤍", "👗"))
        val isBarrier = item.slot == ItemSlot.SHIELD && (item.name.contains("Barrier", ignoreCase = true) || item.name.contains("Mirror", ignoreCase = true) || item.emoji in listOf("🌀", "🪞"))
        val isAmulet = item.slot == ItemSlot.ACCESSORY && (item.name.contains("Amulet", ignoreCase = true) || item.name.contains("Pendant", ignoreCase = true) || item.name.contains("Talisman", ignoreCase = true) || item.emoji in listOf("📿", "🧿", "🔴", "🪨"))
        val isCrown = item.slot == ItemSlot.ACCESSORY && (item.name.contains("Crown", ignoreCase = true) || item.name.contains("Crest", ignoreCase = true) || item.emoji in listOf("👑"))

        when (item.slot) {
            ItemSlot.WEAPON -> when {
                isMagicalWeapon -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonMagicalStaff(s)
                    Rarity.RARE -> drawRareMagicalStaff(s, animTime)
                    Rarity.EPIC -> drawEpicMagicalStaff(s, animTime)
                    Rarity.LEGENDARY, Rarity.MYTHIC -> drawLegendaryMagicalStaff(s, animTime)
                }
                isAxeOrBow -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonAxeOrBow(s)
                    Rarity.RARE -> drawRareAxeOrBow(s, animTime)
                    Rarity.EPIC -> drawEpicAxeOrBow(s, animTime)
                    Rarity.LEGENDARY, Rarity.MYTHIC -> drawLegendaryAxeOrBow(s, animTime)
                }
                else -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonSword(s)
                    Rarity.RARE -> drawRareSword(s, animTime)
                    Rarity.EPIC -> drawEpicSword(s, animTime)
                    Rarity.LEGENDARY, Rarity.MYTHIC -> drawLegendarySword(s, animTime)
                }
            }
            ItemSlot.ARMOR -> when {
                isRobe -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonRobe(s)
                    Rarity.RARE -> drawRareRobe(s, animTime)
                    Rarity.EPIC -> drawEpicRobe(s, animTime)
                    Rarity.LEGENDARY, Rarity.MYTHIC -> drawLegendaryRobe(s, animTime)
                }
                else -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonHelmet(s)
                    Rarity.RARE -> drawRareHelmet(s, animTime)
                    Rarity.EPIC -> drawEpicHelmet(s, animTime)
                    Rarity.LEGENDARY, Rarity.MYTHIC -> drawLegendaryHelmet(s, animTime)
                }
            }
            ItemSlot.SHIELD -> when {
                isBarrier -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonBarrier(s)
                    Rarity.RARE -> drawRareBarrier(s, animTime)
                    Rarity.EPIC -> drawEpicBarrier(s, animTime)
                    Rarity.LEGENDARY, Rarity.MYTHIC -> drawLegendaryBarrier(s, animTime)
                }
                else -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonShield(s)
                    Rarity.RARE -> drawRareShield(s, animTime)
                    Rarity.EPIC -> drawEpicShield(s, animTime)
                    Rarity.LEGENDARY, Rarity.MYTHIC -> drawLegendaryShield(s, animTime)
                }
            }
            ItemSlot.ACCESSORY -> when {
                isAmulet -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonAmulet(s)
                    Rarity.RARE -> drawRareAmulet(s, animTime)
                    Rarity.EPIC -> drawEpicAmulet(s, animTime)
                    Rarity.LEGENDARY, Rarity.MYTHIC -> drawLegendaryAmulet(s, animTime)
                }
                isCrown -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonCrown(s)
                    Rarity.RARE -> drawRareCrown(s, animTime)
                    Rarity.EPIC -> drawEpicCrown(s, animTime)
                    Rarity.LEGENDARY, Rarity.MYTHIC -> drawLegendaryCrown(s, animTime)
                }
                else -> when (item.rarity) {
                    Rarity.COMMON -> drawCommonAccessory(s)
                    Rarity.RARE -> drawRareAccessory(s, animTime)
                    Rarity.EPIC -> drawEpicAccessory(s, animTime)
                    Rarity.LEGENDARY, Rarity.MYTHIC -> drawLegendaryAccessory(s, animTime)
                }
            }
        }
    }
}

// ============================================================================
// 5. MYTHIC GEAR (LORE-SPECIFIC ARTIFACTS)
// ============================================================================

// ============================================================================
// 5. MYTHIC GEAR (COMPLETE 3-PIECE SETS)
// ============================================================================

private fun DrawScope.drawMythicSetPiece(item: Item, s: Float, animTime: Float) {
    when (item.setId) {
        // --- 1. KNIGHT: Cloud Strife ---
        "set_crystal" -> when (item.slot) {
            ItemSlot.WEAPON -> drawMythicBusterSword(s, animTime)
            ItemSlot.ARMOR -> drawMythicSoldierPauldron(s, animTime)
            ItemSlot.SHIELD -> drawMythicHardedgeGuard(s, animTime)
            ItemSlot.ACCESSORY -> drawMythicMateriaRing(s, animTime)
        }
        // --- 2. BLACK MAGE: Vivi Ornitier ---
        "set_magus" -> when (item.slot) {
            ItemSlot.WEAPON -> drawMythicStardustRod(s, animTime)
            ItemSlot.ARMOR -> drawMythicViviHat(s, animTime)
            ItemSlot.ACCESSORY -> drawMythicMagusRing(s, animTime)
            else -> drawLegendaryArmorFallback(s, animTime)
        }
        // --- 3. WHITE MAGE: Aerith Gainsborough ---
        "set_devout" -> when (item.slot) {
            ItemSlot.WEAPON -> drawMythicPrincessGuard(s, animTime)
            ItemSlot.ARMOR -> drawMythicWhiteRobe(s, animTime)
            ItemSlot.ACCESSORY -> drawMythicHolyPendant(s, animTime)
            else -> drawLegendaryArmorFallback(s, animTime)
        }
        // --- 4. NINJA / THIEF: Yuffie Kisaragi ---
        "set_shadow" -> when (item.slot) {
            ItemSlot.WEAPON -> drawMythicConformer(s, animTime)
            ItemSlot.ARMOR -> drawMythicNinjaGarb(s, animTime)
            ItemSlot.ACCESSORY -> drawMythicWindsweptTalismans(s, animTime)
            else -> drawLegendaryArmorFallback(s, animTime)
        }
        // --- 5. MONK: Tifa Lockhart ---
        "set_chakra" -> when (item.slot) {
            ItemSlot.WEAPON -> drawMythicGodhand(s, animTime)
            ItemSlot.ARMOR -> drawMythicWrestlerVest(s, animTime)
            ItemSlot.ACCESSORY -> drawMythicLocketOfSeven(s, animTime)
            else -> drawLegendaryArmorFallback(s, animTime)
        }
        // --- 6. DRAGOON: Kain Highwind ---
        "set_wyvern" -> when (item.slot) {
            ItemSlot.WEAPON -> drawMythicWyvernLance(s, animTime)
            ItemSlot.ARMOR -> drawMythicDragonHelm(s, animTime)
            ItemSlot.SHIELD -> drawMythicDragonGauntlet(s, animTime)
            ItemSlot.ACCESSORY -> drawMythicDragonGauntlet(s, animTime)
        }
        // --- 7. RED MAGE: Celes / Alisaie ---
        "set_crimson" -> when (item.slot) {
            ItemSlot.WEAPON -> drawMythicRapier(s, animTime)
            ItemSlot.ARMOR -> drawMythicDuelistDoublet(s, animTime)
            ItemSlot.ACCESSORY -> drawMythicSpellRuby(s, animTime)
            else -> drawLegendaryArmorFallback(s, animTime)
        }
        // --- 8. SUMMONER: Yuna ---
        "set_evoker" -> when (item.slot) {
            ItemSlot.WEAPON -> drawMythicYunaStaff(s, animTime)
            ItemSlot.ARMOR -> drawMythicHighSummonerGown(s, animTime)
            ItemSlot.ACCESSORY -> drawMythicNirvanaOrb(s, animTime)
            else -> drawLegendaryArmorFallback(s, animTime)
        }
        // --- 9. BARD: Edward ---
        "set_apollo" -> when (item.slot) {
            ItemSlot.WEAPON -> drawMythicApollosHarp(s, animTime)
            ItemSlot.ARMOR -> drawMythicFeatheredCap(s, animTime)
            ItemSlot.ACCESSORY -> drawMythicMinstrelCloak(s, animTime)
            else -> drawLegendaryArmorFallback(s, animTime)
        }
        // --- 10. SAMURAI: Auron ---
        "set_genji" -> when (item.slot) {
            ItemSlot.WEAPON -> drawMythicMasamune(s, animTime)
            ItemSlot.ARMOR -> drawMythicKabutoArmor(s, animTime)
            ItemSlot.ACCESSORY -> drawMythicBaneBracer(s, animTime)
            else -> drawLegendaryArmorFallback(s, animTime)
        }
        // --- 11. PALADIN: Cecil Harvey ---
        "set_aegis" -> when (item.slot) {
            ItemSlot.WEAPON -> drawMythicExcalibur(s, animTime)
            ItemSlot.ARMOR -> drawMythicPaladinPlate(s, animTime)
            ItemSlot.SHIELD -> drawMythicCecilShield(s, animTime)
            ItemSlot.ACCESSORY -> drawMythicPaladinPlate(s, animTime)
        }
        // --- 12. ONION KNIGHT: Luneth ---
        "set_onion" -> when (item.slot) {
            ItemSlot.WEAPON -> drawMythicOnionSword(s, animTime)
            ItemSlot.ARMOR -> drawMythicOnionHelm(s, animTime)
            ItemSlot.SHIELD -> drawMythicOnionShield(s, animTime)
            ItemSlot.ACCESSORY -> drawMythicOnionShield(s, animTime)
        }
        else -> drawLegendaryArmorFallback(s, animTime)
    }
}

// ============================================================================
// INDIVIDUAL MYTHIC PROCEDURAL DRAWINGS (LORE-ACCURATE)
// ============================================================================

// ============================================================================
// INDIVIDUAL MYTHIC PROCEDURAL DRAWINGS (LORE-ACCURATE & HIGH DETAIL)
// ============================================================================

// --- 3. AERITH GAINSBOROUGH (Devout / Aerith's Blessing) ---
private fun DrawScope.drawMythicPrincessGuard(s: Float, animTime: Float) {
    val rodPink = Color(0xFFE91E63); val gold = Color(0xFFFFD700); val holyGreen = Color(0xFF2ECC71)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    // Shaft
    drawLine(Color(0xFFF8F9FA), Offset(50f * s, 85f * s), Offset(50f * s, 35f * s), strokeWidth = 4f * s)
    // Detailed Floral Guard Top
    val leftWing = Path().apply { moveTo(50f * s, 35f * s); quadraticTo(20f * s, 25f * s, 40f * s, 10f * s); quadraticTo(50f * s, 20f * s, 50f * s, 35f * s) }
    val rightWing = Path().apply { moveTo(50f * s, 35f * s); quadraticTo(80f * s, 25f * s, 60f * s, 10f * s); quadraticTo(50f * s, 20f * s, 50f * s, 35f * s) }
    drawPath(leftWing, gold); drawPath(rightWing, gold)
    drawPath(leftWing, rodPink, style = Stroke(1.5f * s)); drawPath(rightWing, rodPink, style = Stroke(1.5f * s))
    // Glowing Materia Center
    drawCircle(holyGreen.copy(alpha = pulse), radius = 8f * s, center = Offset(50f * s, 35f * s))
    drawCircle(Color.White, radius = 3f * s, center = Offset(49f * s, 34f * s))
}
private fun DrawScope.drawMythicWhiteRobe(s: Float, animTime: Float) {
    val jacketRed = Color(0xFFC0392B); val dressPink = Color(0xFFFADBD8); val beltBrown = Color(0xFF5D4037)
    // Flowing Pink Dress
    val dress = Path().apply { moveTo(40f * s, 40f * s); lineTo(60f * s, 40f * s); quadraticTo(80f * s, 80f * s, 70f * s, 90f * s); lineTo(30f * s, 90f * s); quadraticTo(20f * s, 80f * s, 40f * s, 40f * s) }
    drawPath(dress, dressPink); drawPath(dress, Color.White, style = Stroke(2f * s))
    // Iconic Red Jacket Top
    val jacket = Path().apply { moveTo(35f * s, 15f * s); lineTo(65f * s, 15f * s); lineTo(70f * s, 45f * s); lineTo(30f * s, 45f * s); close() }
    drawPath(jacket, jacketRed); drawPath(jacket, Color(0xFF922B21), style = Stroke(2f * s))
    drawRect(beltBrown, Offset(35f * s, 42f * s), Size(30f * s, 6f * s))
}
private fun DrawScope.drawMythicHolyPendant(s: Float, animTime: Float) {
    val gold = Color(0xFFFFD700); val whiteGlow = Color(0xFFE0FFFF)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    // Delicate Chain
    val chain = Path().apply { moveTo(25f * s, 20f * s); quadraticTo(50f * s, 60f * s, 75f * s, 20f * s) }
    drawPath(chain, gold, style = Stroke(2f * s))
    // Holy Materia Pendant
    drawCircle(whiteGlow.copy(alpha = pulse), radius = 10f * s, center = Offset(50f * s, 45f * s))
    drawCircle(gold, radius = 10f * s, center = Offset(50f * s, 45f * s), style = Stroke(3f * s))
}

// --- 4. YUFFIE KISARAGI (Shadow / Yuffie's Defiance) ---
private fun DrawScope.drawMythicConformer(s: Float, animTime: Float) {
    val steel = Color(0xFF95A5A6); val darkSteel = Color(0xFF2C3E50); val rivet = Color(0xFFF1C40F)
    val rot = animTime * 0.1f // Fast spin effect
    withTransform({ rotate(rot, Offset(50f * s, 50f * s)) }) {
        // 4-Pointed Giant Shuriken
        val shuriken = Path().apply {
            moveTo(50f * s, 10f * s); lineTo(55f * s, 45f * s); lineTo(90f * s, 50f * s); lineTo(55f * s, 55f * s)
            lineTo(50f * s, 90f * s); lineTo(45f * s, 55f * s); lineTo(10f * s, 50f * s); lineTo(45f * s, 45f * s); close()
        }
        drawPath(shuriken, steel); drawPath(shuriken, darkSteel, style = Stroke(2f * s))
        // Inner Ring
        drawCircle(darkSteel, radius = 15f * s, center = Offset(50f * s, 50f * s), style = Stroke(4f * s))
        drawCircle(rivet, radius = 4f * s, center = Offset(50f * s, 50f * s))
    }
}
private fun DrawScope.drawMythicNinjaGarb(s: Float, animTime: Float) {
    val greenCloth = Color(0xFF27AE60); val netting = Color(0xFF1B2631); val armor = Color(0xFFE67E22)
    val garbPath = Path().apply { moveTo(35f * s, 15f * s); lineTo(65f * s, 15f * s); lineTo(75f * s, 85f * s); lineTo(25f * s, 85f * s); close() }
    drawPath(garbPath, greenCloth)
    // Left arm netting texture
    drawRect(netting, Offset(25f * s, 25f * s), Size(15f * s, 30f * s))
    // Orange Shoulder Guard
    drawOval(armor, topLeft = Offset(20f * s, 15f * s), size = Size(25f * s, 15f * s))
}
private fun DrawScope.drawMythicWindsweptTalismans(s: Float, animTime: Float) {
    val leather = Color(0xFF78281F); val jade = Color(0xFF2ECC71)
    // Ninja Armguard
    drawRect(leather, Offset(35f * s, 20f * s), Size(30f * s, 60f * s), style = Stroke(4f * s))
    drawRect(Color(0xFF212F3D), Offset(35f * s, 20f * s), Size(30f * s, 60f * s))
    // Embedded Jade Materia
    drawCircle(jade, radius = 8f * s, center = Offset(50f * s, 40f * s))
    drawCircle(jade, radius = 8f * s, center = Offset(50f * s, 60f * s))
}

// --- 5. TIFA LOCKHART (Chakra / Tifa's Final Heaven) ---
private fun DrawScope.drawMythicGodhand(s: Float, animTime: Float) {
    val redLeather = Color(0xFFC0392B); val silverKnuckles = Color(0xFFBDC3C7); val darkStrap = Color(0xFF1C2833)
    val materia = Color(0xFF2ECC71)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    // Detailed Boxing Glove Shape
    val glovePath = Path().apply { moveTo(40f * s, 20f * s); quadraticTo(70f * s, 15f * s, 80f * s, 40f * s); quadraticTo(80f * s, 70f * s, 60f * s, 80f * s); lineTo(30f * s, 80f * s); lineTo(25f * s, 40f * s); close() }
    drawPath(glovePath, redLeather); drawPath(glovePath, Color(0xFF922B21), style = Stroke(2f * s))
    // Silver Knuckle Plate
    drawRect(silverKnuckles, Offset(50f * s, 25f * s), Size(25f * s, 15f * s))
    drawCircle(materia.copy(alpha = pulse), radius = 4f * s, center = Offset(62f * s, 32f * s))
    // Wrist Strap
    drawRect(darkStrap, Offset(28f * s, 65f * s), Size(35f * s, 10f * s))
}
private fun DrawScope.drawMythicWrestlerVest(s: Float, animTime: Float) {
    val whiteCotton = Color(0xFFEAEDED); val suspender = Color(0xFF2C3E50); val skirt = Color(0xFF1C2833)
    // White Crop Top
    drawRect(whiteCotton, Offset(35f * s, 15f * s), Size(30f * s, 35f * s))
    // Dark Suspenders
    drawRect(suspender, Offset(38f * s, 15f * s), Size(4f * s, 35f * s))
    drawRect(suspender, Offset(58f * s, 15f * s), Size(4f * s, 35f * s))
    // Belt & Skirt
    drawRect(skirt, Offset(30f * s, 50f * s), Size(40f * s, 35f * s))
    drawRect(Color(0xFFBDC3C7), Offset(45f * s, 48f * s), Size(10f * s, 8f * s), style = Stroke(2f * s)) // Buckle
}
private fun DrawScope.drawMythicLocketOfSeven(s: Float, animTime: Float) {
    val silver = Color(0xFFBDC3C7); val blueStone = Color(0xFF2980B9)
    val chain = Path().apply { moveTo(30f * s, 20f * s); quadraticTo(50f * s, 45f * s, 70f * s, 20f * s) }
    drawPath(chain, silver, style = Stroke(2f * s))
    // Teardrop Pendant
    val pendant = Path().apply { moveTo(50f * s, 40f * s); quadraticTo(60f * s, 55f * s, 50f * s, 65f * s); quadraticTo(40f * s, 55f * s, 50f * s, 40f * s) }
    drawPath(pendant, blueStone); drawPath(pendant, silver, style = Stroke(2f * s))
}

// --- 6. KAIN HIGHWIND (Wyvern / Kain's Atonement) ---
private fun DrawScope.drawMythicWyvernLance(s: Float, animTime: Float) {
    val pole = Color(0xFF2C3E50); val dragonBlue = Color(0xFF2E86C1); val edgeSilver = Color(0xFFF8F9FA)
    drawLine(pole, Offset(50f * s, 90f * s), Offset(50f * s, 30f * s), strokeWidth = 5f * s)
    // Winged Spear Head
    val spearHead = Path().apply { moveTo(50f * s, 5f * s); lineTo(65f * s, 30f * s); lineTo(55f * s, 35f * s); lineTo(50f * s, 45f * s); lineTo(45f * s, 35f * s); lineTo(35f * s, 30f * s); close() }
    drawPath(spearHead, dragonBlue); drawPath(spearHead, edgeSilver, style = Stroke(2f * s))
    drawLine(edgeSilver, Offset(50f * s, 5f * s), Offset(50f * s, 40f * s), strokeWidth = 1.5f * s)
}
private fun DrawScope.drawMythicDragonHelm(s: Float, animTime: Float) {
    val dragonPurple = Color(0xFF512E5F); val trimGold = Color(0xFFF1C40F); val eyeGlow = Color(0xFFE74C3C)
    // Scaled Helmet
    val helm = Path().apply { moveTo(25f * s, 70f * s); quadraticTo(15f * s, 40f * s, 50f * s, 10f * s); quadraticTo(85f * s, 40f * s, 75f * s, 70f * s); close() }
    drawPath(helm, dragonPurple); drawPath(helm, trimGold, style = Stroke(2.5f * s))
    // Dragon Horns
    val leftHorn = Path().apply { moveTo(40f * s, 25f * s); quadraticTo(10f * s, 15f * s, 5f * s, 30f * s); quadraticTo(20f * s, 35f * s, 35f * s, 40f * s) }
    val rightHorn = Path().apply { moveTo(60f * s, 25f * s); quadraticTo(90f * s, 15f * s, 95f * s, 30f * s); quadraticTo(80f * s, 35f * s, 65f * s, 40f * s) }
    drawPath(leftHorn, trimGold); drawPath(rightHorn, trimGold)
    // Visor Slit
    drawRect(Color(0xFF111111), Offset(40f * s, 45f * s), Size(20f * s, 8f * s))
    drawCircle(eyeGlow, radius = 2f * s, center = Offset(45f * s, 49f * s)); drawCircle(eyeGlow, radius = 2f * s, center = Offset(55f * s, 49f * s))
}
private fun DrawScope.drawMythicDragonGauntlet(s: Float, animTime: Float) {
    val purpleArmor = Color(0xFF512E5F); val spikeSilver = Color(0xFFBDC3C7)
    // Gauntlet Base
    val gauntlet = Path().apply { moveTo(35f * s, 20f * s); lineTo(65f * s, 20f * s); lineTo(75f * s, 80f * s); lineTo(25f * s, 80f * s); close() }
    drawPath(gauntlet, purpleArmor); drawPath(gauntlet, Color(0xFF1C2833), style = Stroke(2f * s))
    // Dragoon Spikes
    val spike = Path().apply { moveTo(50f * s, 25f * s); lineTo(60f * s, 45f * s); lineTo(40f * s, 45f * s); close() }
    drawPath(spike, spikeSilver)
    val spike2 = Path().apply { moveTo(50f * s, 50f * s); lineTo(65f * s, 70f * s); lineTo(35f * s, 70f * s); close() }
    drawPath(spike2, spikeSilver)
}

// --- 7. RED MAGE (Crimson / Alisaie's Resolve) ---
private fun DrawScope.drawMythicRapier(s: Float, animTime: Float) {
    val steel = Color(0xFFECF0F1); val goldHilt = Color(0xFFFFD700); val ruby = Color(0xFFE74C3C)
    // Thin Rapier Blade
    drawLine(steel, Offset(45f * s, 80f * s), Offset(80f * s, 15f * s), strokeWidth = 2.5f * s)
    // Intricate Basket Hilt
    val hiltGuard = Path().apply { moveTo(35f * s, 70f * s); quadraticTo(20f * s, 60f * s, 50f * s, 50f * s); quadraticTo(65f * s, 75f * s, 45f * s, 80f * s) }
    drawPath(hiltGuard, goldHilt, style = Stroke(3f * s))
    drawCircle(ruby, radius = 4f * s, center = Offset(45f * s, 70f * s))
    drawLine(Color(0xFF2C3E50), Offset(45f * s, 80f * s), Offset(35f * s, 95f * s), strokeWidth = 3f * s) // Handle
}
private fun DrawScope.drawMythicDuelistDoublet(s: Float, animTime: Float) {
    val crimson = Color(0xFF922B21); val whiteRuffle = Color(0xFFFDFEFE); val goldTrim = Color(0xFFFFD700)
    // Coat
    val coat = Path().apply { moveTo(30f * s, 15f * s); lineTo(70f * s, 15f * s); lineTo(85f * s, 85f * s); lineTo(50f * s, 70f * s); lineTo(15f * s, 85f * s); close() }
    drawPath(coat, crimson); drawPath(coat, goldTrim, style = Stroke(2f * s))
    // White Ruffle Collar
    drawCircle(whiteRuffle, radius = 10f * s, center = Offset(50f * s, 25f * s))
    drawCircle(whiteRuffle, radius = 8f * s, center = Offset(45f * s, 35f * s))
    drawCircle(whiteRuffle, radius = 8f * s, center = Offset(55f * s, 35f * s))
}
private fun DrawScope.drawMythicSpellRuby(s: Float, animTime: Float) {
    val gold = Color(0xFFFFD700); val ruby = Color(0xFFE74C3C); val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    // Elaborate Brooch
    val brooch = Path().apply { moveTo(50f * s, 20f * s); lineTo(70f * s, 50f * s); lineTo(50f * s, 80f * s); lineTo(30f * s, 50f * s); close() }
    drawPath(brooch, gold, style = Stroke(4f * s))
    drawPath(brooch, ruby.copy(alpha = pulse))
    drawCircle(Color.White, radius = 3f * s, center = Offset(45f * s, 40f * s))
}

// --- 9. EDWARD (Apollo / Edward's Melody) ---
private fun DrawScope.drawMythicApollosHarp(s: Float, animTime: Float) {
    val gold = Color(0xFFFFD700); val magicString = Color(0xFF00E5FF); val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    val harpBase = Path().apply { moveTo(20f * s, 20f * s); quadraticTo(80f * s, 10f * s, 80f * s, 80f * s); quadraticTo(40f * s, 90f * s, 20f * s, 80f * s); close() }
    drawPath(harpBase, gold, style = Stroke(6f * s))
    // Glowing Strings
    repeat(5) { i -> drawLine(magicString.copy(alpha = pulse), Offset((35 + i * 8).toFloat() * s, 32f * s), Offset((35 + i * 8).toFloat() * s, 80f * s), strokeWidth = 1.5f * s) }
}
private fun DrawScope.drawMythicFeatheredCap(s: Float, animTime: Float) {
    val greenCloth = Color(0xFF27AE60); val whiteFeather = Color(0xFFFDFEFE); val ruby = Color(0xFFE74C3C)
    // Bard Beret
    drawOval(greenCloth, topLeft = Offset(20f * s, 40f * s), size = Size(60f * s, 25f * s))
    drawOval(Color(0xFF1E8449), topLeft = Offset(20f * s, 40f * s), size = Size(60f * s, 25f * s), style = Stroke(2f * s))
    // Iconic Long Feather
    val feather = Path().apply { moveTo(65f * s, 45f * s); quadraticTo(90f * s, 15f * s, 70f * s, 5f * s); quadraticTo(60f * s, 25f * s, 65f * s, 45f * s) }
    drawPath(feather, whiteFeather); drawPath(feather, Color(0xFFBDC3C7), style = Stroke(1f * s))
    drawCircle(ruby, radius = 5f * s, center = Offset(65f * s, 50f * s)) // Pin
}
private fun DrawScope.drawMythicMinstrelCloak(s: Float, animTime: Float) {
    val purpleSilk = Color(0xFF8E44AD); val goldTrim = Color(0xFFFFD700)
    // Flowing Cape
    val cloak = Path().apply { moveTo(40f * s, 15f * s); lineTo(60f * s, 15f * s); quadraticTo(90f * s, 60f * s, 80f * s, 90f * s); quadraticTo(50f * s, 80f * s, 20f * s, 90f * s); quadraticTo(10f * s, 60f * s, 40f * s, 15f * s) }
    drawPath(cloak, purpleSilk); drawPath(cloak, goldTrim, style = Stroke(2.5f * s))
}

// --- 10. AURON (Genji / Auron's Bushido) ---
private fun DrawScope.drawMythicMasamune(s: Float, animTime: Float) {
    val bladeSteel = Color(0xFFECF0F1); val darkRedWrap = Color(0xFF641E16); val goldTsuba = Color(0xFFF1C40F)
    val spiritFire = Color(0xFF3498DB); val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    // Spirit Aura
    val aura = Path().apply { moveTo(20f * s, 90f * s); quadraticTo(40f * s, 30f * s, 90f * s, 5f * s); quadraticTo(60f * s, 50f * s, 25f * s, 90f * s) }
    drawPath(aura, spiritFire.copy(alpha = 0.3f * pulse), style = Stroke(8f * s))
    // Curved Katana Blade
    val katana = Path().apply { moveTo(25f * s, 70f * s); quadraticTo(50f * s, 30f * s, 85f * s, 10f * s); lineTo(80f * s, 15f * s); quadraticTo(50f * s, 35f * s, 30f * s, 75f * s); close() }
    drawPath(katana, bladeSteel); drawPath(katana, Color(0xFFBDC3C7), style = Stroke(1f * s))
    // Tsuba (Guard) & Tsuka (Handle)
    drawOval(goldTsuba, topLeft = Offset(18f * s, 68f * s), size = Size(14f * s, 8f * s))
    drawRect(darkRedWrap, Offset(10f * s, 75f * s), Size(12f * s, 20f * s))
    drawLine(Color.Black, Offset(10f * s, 78f * s), Offset(22f * s, 82f * s), strokeWidth = 1f * s)
    drawLine(Color.Black, Offset(10f * s, 85f * s), Offset(22f * s, 89f * s), strokeWidth = 1f * s)
}
private fun DrawScope.drawMythicKabutoArmor(s: Float, animTime: Float) {
    val redLaminar = Color(0xFF922B21); val goldCrescent = Color(0xFFF1C40F); val darkIron = Color(0xFF1C2833)
    // Samurai Helm Dome
    val helm = Path().apply { moveTo(30f * s, 50f * s); quadraticTo(50f * s, 10f * s, 70f * s, 50f * s); close() }
    drawPath(helm, darkIron)
    // Neck Guard (Shikoro)
    val neckGuard = Path().apply { moveTo(25f * s, 50f * s); lineTo(75f * s, 50f * s); lineTo(85f * s, 75f * s); lineTo(15f * s, 75f * s); close() }
    drawPath(neckGuard, redLaminar)
    drawLine(goldCrescent, Offset(20f * s, 60f * s), Offset(80f * s, 60f * s), strokeWidth = 2f * s)
    // Iconic Crescent Moon Crest
    val crescent = Path().apply { moveTo(35f * s, 20f * s); quadraticTo(50f * s, -5f * s, 65f * s, 20f * s); quadraticTo(50f * s, 5f * s, 35f * s, 20f * s) }
    drawPath(crescent, goldCrescent)
}
private fun DrawScope.drawMythicBaneBracer(s: Float, animTime: Float) {
    val blackIron = Color(0xFF1B2631); val goldTrim = Color(0xFFF1C40F); val redRope = Color(0xFFC0392B)
    drawRect(blackIron, Offset(35f * s, 25f * s), Size(30f * s, 50f * s))
    drawRect(goldTrim, Offset(35f * s, 25f * s), Size(30f * s, 50f * s), style = Stroke(3f * s))
    // Rope Tie Detail
    drawLine(redRope, Offset(35f * s, 40f * s), Offset(65f * s, 50f * s), strokeWidth = 3f * s)
    drawLine(redRope, Offset(35f * s, 60f * s), Offset(65f * s, 70f * s), strokeWidth = 3f * s)
}

// --- 12. LUNETH (Onion / Luneth's Destiny) ---
private fun DrawScope.drawMythicOnionSword(s: Float, animTime: Float) {
    val blueSteel = Color(0xFF3498DB); val redHilt = Color(0xFFE74C3C); val gold = Color(0xFFF1C40F)
    val pureWhite = Color(0xFFFFFFFF); val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    // Vibrant Blue Blade
    val sword = Path().apply { moveTo(50f * s, 10f * s); lineTo(58f * s, 30f * s); lineTo(54f * s, 70f * s); lineTo(46f * s, 70f * s); lineTo(42f * s, 30f * s); close() }
    drawPath(sword, blueSteel); drawPath(sword, pureWhite.copy(alpha = pulse), style = Stroke(1.5f * s))
    // Round Onion Guard
    drawOval(gold, topLeft = Offset(35f * s, 65f * s), size = Size(30f * s, 10f * s))
    drawRect(redHilt, Offset(45f * s, 75f * s), Size(10f * s, 15f * s))
}
private fun DrawScope.drawMythicOnionHelm(s: Float, animTime: Float) {
    val silverHelm = Color(0xFFECF0F1); val redPlume = Color(0xFFE74C3C); val darkShadow = Color(0xFF7F8C8D)
    // Bulbous Onion Shape
    val onionDome = Path().apply { moveTo(30f * s, 80f * s); quadraticTo(10f * s, 50f * s, 50f * s, 15f * s); quadraticTo(90f * s, 50f * s, 70f * s, 80f * s); close() }
    drawPath(onionDome, silverHelm)
    drawPath(onionDome, darkShadow, style = Stroke(2f * s))
    // Cheek Guards
    drawOval(darkShadow, topLeft = Offset(25f * s, 70f * s), size = Size(10f * s, 15f * s))
    drawOval(darkShadow, topLeft = Offset(65f * s, 70f * s), size = Size(10f * s, 15f * s))
    // Spiky Red Feather
    val plume = Path().apply { moveTo(50f * s, 15f * s); quadraticTo(70f * s, 0f * s, 85f * s, 10f * s); quadraticTo(65f * s, 15f * s, 50f * s, 25f * s) }
    drawPath(plume, redPlume)
}
private fun DrawScope.drawMythicOnionShield(s: Float, animTime: Float) {
    val roundBlue = Color(0xFF2E86C1); val goldBorder = Color(0xFFF1C40F); val redCenter = Color(0xFFE74C3C)
    // Classic Buckler Shield
    drawCircle(roundBlue, radius = 32f * s, center = Offset(50f * s, 50f * s))
    drawCircle(goldBorder, radius = 32f * s, center = Offset(50f * s, 50f * s), style = Stroke(5f * s))
    // 3 Gold Dots
    drawCircle(goldBorder, radius = 4f * s, center = Offset(50f * s, 30f * s))
    drawCircle(goldBorder, radius = 4f * s, center = Offset(35f * s, 60f * s))
    drawCircle(goldBorder, radius = 4f * s, center = Offset(65f * s, 60f * s))
    // Center Ruby
    drawCircle(redCenter, radius = 8f * s, center = Offset(50f * s, 50f * s))
    drawCircle(Color.White, radius = 2f * s, center = Offset(48f * s, 48f * s))
}

// 🗡️ CLOUD: Buster Sword (Strife's Legacy)
private fun DrawScope.drawMythicBusterSword(s: Float, animTime: Float) {
    val steel = Color(0xFF95A5A6)
    val darkIron = Color(0xFF2C3E50)
    val edge = Color(0xFFECF0F1)
    val makoGreen = Color(0xFF2ECC71)
    val gold = Color(0xFFF1C40F)
    val leather = Color(0xFF8B0000)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Giant Blade Base
    val bladePath = Path().apply {
        moveTo(35f * s, 25f * s)  // Top Left
        lineTo(65f * s, 10f * s)  // Angled Tip Right
        lineTo(65f * s, 65f * s)  // Bottom Right
        lineTo(35f * s, 65f * s)  // Bottom Left
        close()
    }
    drawPath(bladePath, darkIron)

    // Sharp Edge
    val edgePath = Path().apply {
        moveTo(35f * s, 25f * s)
        lineTo(45f * s, 20f * s)
        lineTo(45f * s, 65f * s)
        lineTo(35f * s, 65f * s)
        close()
    }
    drawPath(edgePath, edge)

    // Mako Materia Slots
    drawCircle(darkIron, radius = 4f * s, center = Offset(55f * s, 45f * s))
    drawCircle(makoGreen.copy(alpha = pulse), radius = 3f * s, center = Offset(55f * s, 45f * s))

    drawCircle(darkIron, radius = 4f * s, center = Offset(55f * s, 55f * s))
    drawCircle(makoGreen.copy(alpha = pulse), radius = 3f * s, center = Offset(55f * s, 55f * s))

    // Heavy Guard
    drawRect(gold, Offset(30f * s, 65f * s), Size(40f * s, 6f * s))
    drawRect(darkIron, Offset(30f * s, 65f * s), Size(40f * s, 6f * s), style = Stroke(1.5f * s))

    // Handle
    drawRect(leather, Offset(45f * s, 71f * s), Size(10f * s, 16f * s))
    drawLine(gold, Offset(45f * s, 73f * s), Offset(55f * s, 77f * s), strokeWidth = 1.5f * s)
    drawLine(gold, Offset(45f * s, 79f * s), Offset(55f * s, 83f * s), strokeWidth = 1.5f * s)
}

// 🎩 VIVI: Pointy Wizard Hat (Vivi's Memories)
private fun DrawScope.drawMythicViviHat(s: Float, animTime: Float) {
    val hatLeather = Color(0xFFC08A47)
    val hatShadow = Color(0xFF8B5A2B)
    val band = Color(0xFF34495E)
    val stitch = Color(0xFFE67E22)
    val shadowFace = Color(0xFF111111)
    val glowingYellow = Color(0xFFFFD700)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    // Dark Face Void inside the hat
    drawOval(shadowFace, topLeft = Offset(30f * s, 65f * s), size = Size(40f * s, 25f * s))
    // Glowing Eyes
    drawCircle(glowingYellow.copy(alpha = pulse), radius = 3.5f * s, center = Offset(42f * s, 75f * s))
    drawCircle(glowingYellow.copy(alpha = pulse), radius = 3.5f * s, center = Offset(58f * s, 75f * s))
    drawCircle(Color.White, radius = 1.5f * s, center = Offset(42f * s, 75f * s))
    drawCircle(Color.White, radius = 1.5f * s, center = Offset(58f * s, 75f * s))

    // Wide Crumpled Brim
    drawOval(hatLeather, topLeft = Offset(10f * s, 60f * s), size = Size(80f * s, 20f * s))
    drawOval(hatShadow, topLeft = Offset(10f * s, 60f * s), size = Size(80f * s, 20f * s), style = Stroke(2f * s))

    // Pointy Crooked Cone
    val conePath = Path().apply {
        moveTo(25f * s, 65f * s)
        quadraticTo(35f * s, 30f * s, 75f * s, 10f * s) // Bends far right
        quadraticTo(85f * s, 20f * s, 65f * s, 40f * s)
        quadraticTo(60f * s, 50f * s, 75f * s, 65f * s)
        close()
    }
    drawPath(conePath, hatLeather)
    drawPath(conePath, hatShadow, style = Stroke(2f * s))

    // Dark Band
    val bandPath = Path().apply {
        moveTo(26f * s, 60f * s)
        quadraticTo(50f * s, 50f * s, 72f * s, 60f * s)
        lineTo(74f * s, 65f * s)
        quadraticTo(50f * s, 55f * s, 25f * s, 65f * s)
        close()
    }
    drawPath(bandPath, band)
    // Stitches
    drawLine(stitch, Offset(40f * s, 55f * s), Offset(42f * s, 62f * s), strokeWidth = 1.5f * s)
    drawLine(stitch, Offset(60f * s, 55f * s), Offset(58f * s, 62f * s), strokeWidth = 1.5f * s)
}

// 🌟 YUNA: Summoner's Staff (Yuna's Sending)
private fun DrawScope.drawMythicYunaStaff(s: Float, animTime: Float) {
    val wood = Color(0xFFCD853F)
    val gold = Color(0xFFFFD700)
    val redTassel = Color(0xFFDC143C)
    val holyGlow = Color(0xFFE0FFFF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    val sway = sin(animTime * 0.003f) * 4f

    // Glowing Aura
    drawCircle(holyGlow.copy(alpha = 0.3f * pulse), radius = 24f * s, center = Offset(50f * s, 25f * s))

    // Staff Shaft
    drawLine(wood, Offset(50f * s, 45f * s), Offset(50f * s, 95f * s), strokeWidth = 4f * s)

    // Top Ring (The Sending Loop)
    drawCircle(gold, radius = 16f * s, center = Offset(50f * s, 25f * s), style = Stroke(5f * s))

    // Tassel / Bell hanging from the center
    drawLine(gold, Offset(50f * s, 25f * s), Offset(50f * s + sway, 45f * s), strokeWidth = 2f * s)
    drawRect(redTassel, Offset(46f * s + sway, 45f * s), Size(8f * s, 12f * s))
    drawLine(Color.White, Offset(48f * s + sway, 45f * s), Offset(48f * s + sway, 57f * s), strokeWidth = 1f * s)
    drawLine(Color.White, Offset(52f * s + sway, 45f * s), Offset(52f * s + sway, 57f * s), strokeWidth = 1f * s)
}

// 🛡️ CECIL: Paladin's Aegis (Cecil's Redemption)
private fun DrawScope.drawMythicCecilShield(s: Float, animTime: Float) {
    val whiteEnamel = Color(0xFFF8F9FA)
    val holyBlue = Color(0xFF3498DB)
    val goldTrim = Color(0xFFF1C40F)
    val holyAura = Color(0xFF85C1E9)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val shieldShape = Path().apply {
        moveTo(20f * s, 15f * s)
        lineTo(80f * s, 15f * s)
        lineTo(80f * s, 40f * s)
        quadraticTo(75f * s, 85f * s, 50f * s, 95f * s)
        quadraticTo(25f * s, 85f * s, 20f * s, 40f * s)
        close()
    }

    // Outer Aura
    drawPath(shieldShape, holyAura.copy(alpha = 0.4f * pulse))

    // Base Shield
    drawPath(shieldShape, whiteEnamel)
    drawPath(shieldShape, goldTrim, style = Stroke(4f * s))

    // Holy Blue Cross / Wings inlay
    val crestPath = Path().apply {
        moveTo(50f * s, 25f * s)
        lineTo(65f * s, 40f * s)
        lineTo(55f * s, 40f * s)
        lineTo(55f * s, 75f * s)
        lineTo(45f * s, 75f * s)
        lineTo(45f * s, 40f * s)
        lineTo(35f * s, 40f * s)
        close()
    }
    drawPath(crestPath, holyBlue)
    drawPath(crestPath, goldTrim, style = Stroke(1.5f * s))

    // Central Gem
    drawCircle(Color.White, radius = 6f * s, center = Offset(50f * s, 40f * s))
    drawCircle(holyAura, radius = 4f * s, center = Offset(50f * s, 40f * s))
}

// Fallback for Mythic items you haven't drawn yet
private fun DrawScope.drawLegendaryArmorFallback(s: Float, animTime: Float, glowColor: Color = Color(0xFFFF2255)) {
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    drawCircle(glowColor.copy(alpha = 0.5f * pulse), radius = 30f * s, center = Offset(50f * s, 50f * s))
    drawCircle(Color.White, radius = 15f * s, center = Offset(50f * s, 50f * s), style = Stroke(3f * s))
}

fun DrawScope.drawEmptySlotPlaceholder(slot: ItemSlot) {
    val sizeMin = minOf(size.width, size.height)
    val s = sizeMin / 100f
    val offsetX = (size.width - 100f * s) / 2f
    val offsetY = (size.height - 100f * s) / 2f

    withTransform({
        translate(left = offsetX, top = offsetY)
    }) {
        when (slot) {
            ItemSlot.WEAPON -> drawCommonSword(s, isPlaceholder = true)
            ItemSlot.ARMOR -> drawCommonHelmet(s, isPlaceholder = true)
            ItemSlot.SHIELD -> drawCommonShield(s, isPlaceholder = true)
            ItemSlot.ACCESSORY -> drawCommonAccessory(s, isPlaceholder = true)
        }
    }
}

// ============================================================================
// 1. WEAPON SPRITES (SWORDS, MAGICAL STAFFS, AXES/BOWS)
// ============================================================================

private fun DrawScope.drawCommonSword(s: Float, isPlaceholder: Boolean = false) {
    val steel = if (isPlaceholder) Color(0xFF555566).copy(alpha = 0.4f) else Color(0xFFBDC3C7)
    val steelDark = if (isPlaceholder) Color(0xFF333344).copy(alpha = 0.4f) else Color(0xFF7F8C8D)
    val wood = if (isPlaceholder) Color(0xFF443322).copy(alpha = 0.4f) else Color(0xFF6E2C00)
    val ironGuard = if (isPlaceholder) Color(0xFF444455).copy(alpha = 0.4f) else Color(0xFF34495E)

    val bladePath = Path().apply {
        moveTo(65f * s, 22f * s)
        lineTo(75f * s, 32f * s)
        lineTo(42f * s, 65f * s)
        lineTo(35f * s, 58f * s)
        close()
    }
    drawPath(bladePath, steel)
    drawPath(bladePath, steelDark, style = Stroke(1.5f * s))

    drawLine(steelDark, Offset(66f * s, 28f * s), Offset(40f * s, 58f * s), strokeWidth = 1.5f * s)

    val guardPath = Path().apply {
        moveTo(30f * s, 52f * s)
        lineTo(48f * s, 70f * s)
        lineTo(44f * s, 74f * s)
        lineTo(26f * s, 56f * s)
        close()
    }
    drawPath(guardPath, ironGuard)

    val hiltPath = Path().apply {
        moveTo(30f * s, 68f * s)
        lineTo(20f * s, 78f * s)
        lineTo(16f * s, 74f * s)
        lineTo(26f * s, 64f * s)
        close()
    }
    drawPath(hiltPath, wood)

    drawCircle(ironGuard, radius = 4f * s, center = Offset(18f * s, 76f * s))

    if (isPlaceholder) {
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4f * s, 4f * s), 0f)
        drawPath(bladePath, Color.Gray.copy(alpha = 0.5f), style = Stroke(1.5f * s, pathEffect = dashEffect))
    }
}

// ============================================================================
// MISSING MYTHIC PROCEDURAL DRAWINGS (Cloud, Vivi, Yuna, Edward, Auron, Cecil)
// ============================================================================

// --- CLOUD STRIFE MISSING PIECES ---
private fun DrawScope.drawMythicSoldierPauldron(s: Float, animTime: Float) {
    val darkSteel = Color(0xFF2C3E50); val silver = Color(0xFFBDC3C7); val rivet = Color(0xFFF1C40F)
    // Heavy Shoulder Plate
    val pauldron = Path().apply { moveTo(20f * s, 40f * s); quadraticTo(50f * s, 10f * s, 80f * s, 40f * s); lineTo(80f * s, 70f * s); quadraticTo(50f * s, 85f * s, 20f * s, 70f * s); close() }
    drawPath(pauldron, silver); drawPath(pauldron, darkSteel, style = Stroke(3f * s))
    // Inner segmented plates
    drawLine(darkSteel, Offset(25f * s, 50f * s), Offset(75f * s, 50f * s), strokeWidth = 2f * s)
    drawLine(darkSteel, Offset(30f * s, 60f * s), Offset(70f * s, 60f * s), strokeWidth = 2f * s)
    // Rivets
    drawCircle(rivet, radius = 3f * s, center = Offset(30f * s, 30f * s))
    drawCircle(rivet, radius = 3f * s, center = Offset(70f * s, 30f * s))
}

private fun DrawScope.drawMythicHardedgeGuard(s: Float, animTime: Float) {
    val bladeSteel = Color(0xFF95A5A6); val redTrim = Color(0xFFC0392B); val mako = Color(0xFF2ECC71)
    val guard = Path().apply { moveTo(25f * s, 20f * s); lineTo(75f * s, 20f * s); lineTo(85f * s, 50f * s); lineTo(50f * s, 85f * s); lineTo(15f * s, 50f * s); close() }
    drawPath(guard, bladeSteel); drawPath(guard, redTrim, style = Stroke(4f * s))
    drawCircle(Color(0xFF2C3E50), radius = 10f * s, center = Offset(50f * s, 45f * s))
    drawCircle(mako, radius = 6f * s, center = Offset(50f * s, 45f * s))
}

private fun DrawScope.drawMythicMateriaRing(s: Float, animTime: Float) {
    val silver = Color(0xFFBDC3C7); val makoGreen = Color(0xFF00FF88)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    drawCircle(silver, radius = 24f * s, center = Offset(50f * s, 50f * s), style = Stroke(6f * s))
    drawCircle(makoGreen.copy(alpha = pulse), radius = 12f * s, center = Offset(50f * s, 50f * s))
    drawCircle(Color.White, radius = 4f * s, center = Offset(48f * s, 48f * s))
}

// --- VIVI ORNITIER MISSING PIECES ---
private fun DrawScope.drawMythicStardustRod(s: Float, animTime: Float) {
    val wood = Color(0xFF5D4037); val starGold = Color(0xFFFFD700)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    drawLine(wood, Offset(30f * s, 85f * s), Offset(70f * s, 35f * s), strokeWidth = 6f * s)
    // Star on top
    val star = Path().apply { moveTo(70f * s, 10f * s); lineTo(75f * s, 25f * s); lineTo(90f * s, 25f * s); lineTo(78f * s, 35f * s); lineTo(82f * s, 50f * s); lineTo(70f * s, 40f * s); lineTo(58f * s, 50f * s); lineTo(62f * s, 35f * s); lineTo(50f * s, 25f * s); lineTo(65f * s, 25f * s); close() }
    drawPath(star, starGold)
    drawCircle(Color.White.copy(alpha = pulse), radius = 6f * s, center = Offset(70f * s, 30f * s))
}

private fun DrawScope.drawMythicMagusRing(s: Float, animTime: Float) {
    val obsidian = Color(0xFF2C3E50); val voidPurple = Color(0xFF9B59B6)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    drawCircle(obsidian, radius = 22f * s, center = Offset(50f * s, 50f * s), style = Stroke(8f * s))
    drawCircle(voidPurple.copy(alpha = pulse), radius = 10f * s, center = Offset(50f * s, 50f * s))
    drawCircle(Color.White, radius = 3f * s, center = Offset(48f * s, 48f * s))
}

// --- YUNA MISSING PIECES ---
private fun DrawScope.drawMythicHighSummonerGown(s: Float, animTime: Float) {
    val whiteSilk = Color(0xFFFDFEFE); val obiYellow = Color(0xFFF1C40F); val floralPink = Color(0xFFFF69B4)
    val gown = Path().apply { moveTo(35f * s, 15f * s); lineTo(65f * s, 15f * s); lineTo(85f * s, 85f * s); lineTo(15f * s, 85f * s); close() }
    drawPath(gown, whiteSilk)
    // Obi sash
    drawRect(obiYellow, Offset(30f * s, 45f * s), Size(40f * s, 12f * s))
    drawRect(Color(0xFFE67E22), Offset(30f * s, 45f * s), Size(40f * s, 12f * s), style = Stroke(2f * s))
    // Floral accent
    drawCircle(floralPink, radius = 6f * s, center = Offset(30f * s, 70f * s))
    drawCircle(floralPink, radius = 6f * s, center = Offset(70f * s, 70f * s))
}

private fun DrawScope.drawMythicNirvanaOrb(s: Float, animTime: Float) {
    val gold = Color(0xFFFFD700); val holyCyan = Color(0xFF00E5FF)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    val orb = Path().apply { moveTo(50f * s, 15f * s); lineTo(70f * s, 50f * s); lineTo(50f * s, 85f * s); lineTo(30f * s, 50f * s); close() }
    drawPath(orb, gold, style = Stroke(4f * s))
    drawCircle(holyCyan.copy(alpha = pulse), radius = 14f * s, center = Offset(50f * s, 50f * s))
    drawCircle(Color.White, radius = 4f * s, center = Offset(50f * s, 50f * s))
}


// --- CECIL MISSING PIECES ---
private fun DrawScope.drawMythicExcalibur(s: Float, animTime: Float) {
    val bladeHoly = Color(0xFFFDFEFE); val goldHilt = Color(0xFFFFD700); val blueGlow = Color(0xFF3498DB)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    val blade = Path().apply { moveTo(50f * s, 10f * s); lineTo(60f * s, 25f * s); lineTo(55f * s, 70f * s); lineTo(45f * s, 70f * s); lineTo(40f * s, 25f * s); close() }
    drawPath(blade, bladeHoly)
    drawPath(blade, blueGlow.copy(alpha = pulse), style = Stroke(2f * s))
    // Ornate Guard
    val guard = Path().apply { moveTo(25f * s, 60f * s); quadraticTo(50f * s, 75f * s, 75f * s, 60f * s); lineTo(70f * s, 75f * s); lineTo(30f * s, 75f * s); close() }
    drawPath(guard, goldHilt)
    drawRect(goldHilt, Offset(46f * s, 75f * s), Size(8f * s, 15f * s)) // Handle
}

private fun DrawScope.drawMythicPaladinPlate(s: Float, animTime: Float) {
    val plateWhite = Color(0xFFF4F6F7); val goldTrim = Color(0xFFFFD700); val holyBlue = Color(0xFF3498DB)
    val chest = Path().apply { moveTo(30f * s, 20f * s); lineTo(70f * s, 20f * s); lineTo(80f * s, 50f * s); lineTo(50f * s, 85f * s); lineTo(20f * s, 50f * s); close() }
    drawPath(chest, plateWhite); drawPath(chest, goldTrim, style = Stroke(3f * s))
    // Wings crest
    val wingLeft = Path().apply { moveTo(50f * s, 35f * s); lineTo(30f * s, 30f * s); lineTo(35f * s, 50f * s); close() }
    val wingRight = Path().apply { moveTo(50f * s, 35f * s); lineTo(70f * s, 30f * s); lineTo(65f * s, 50f * s); close() }
    drawPath(wingLeft, holyBlue); drawPath(wingRight, holyBlue)
}

private fun DrawScope.drawRareSword(s: Float, animTime: Float) {
    val steel = Color(0xFFECF0F1)
    val steelDark = Color(0xFF7F8C8D)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val sapphire = Color(0xFF29B6F6)
    val blueAura = Color(0xFF00E5FF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val auraPath = Path().apply {
        moveTo(68f * s, 16f * s)
        lineTo(82f * s, 30f * s)
        lineTo(42f * s, 70f * s)
        lineTo(28f * s, 56f * s)
        close()
    }
    drawPath(auraPath, blueAura.copy(alpha = 0.25f * pulse))

    val bladePath = Path().apply {
        moveTo(68f * s, 18f * s)
        lineTo(80f * s, 30f * s)
        lineTo(44f * s, 66f * s)
        lineTo(32f * s, 54f * s)
        close()
    }
    drawPath(bladePath, steel)
    drawPath(bladePath, steelDark, style = Stroke(1.5f * s))

    drawLine(sapphire.copy(alpha = pulse), Offset(69f * s, 25f * s), Offset(39f * s, 59f * s), strokeWidth = 2.5f * s)
    drawLine(Color.White, Offset(69f * s, 25f * s), Offset(39f * s, 59f * s), strokeWidth = 1f * s)

    val guardPath = Path().apply {
        moveTo(28f * s, 50f * s)
        lineTo(50f * s, 72f * s)
        lineTo(44f * s, 78f * s)
        lineTo(22f * s, 56f * s)
        close()
    }
    drawPath(guardPath, gold)
    drawPath(guardPath, goldDark, style = Stroke(1.5f * s))

    drawCircle(sapphire, radius = 3.5f * s, center = Offset(36f * s, 64f * s))
    drawCircle(Color.White, radius = 1.2f * s, center = Offset(35f * s, 63f * s))

    val hiltPath = Path().apply {
        moveTo(28f * s, 70f * s)
        lineTo(18f * s, 80f * s)
        lineTo(14f * s, 76f * s)
        lineTo(24f * s, 66f * s)
        close()
    }
    drawPath(hiltPath, steelDark)
    drawLine(gold, Offset(27f * s, 71f * s), Offset(17f * s, 81f * s), strokeWidth = 1.5f * s)

    drawCircle(gold, radius = 5f * s, center = Offset(16f * s, 78f * s))
    drawCircle(goldDark, radius = 5f * s, center = Offset(16f * s, 78f * s), style = Stroke(1.2f * s))

    val shimmerPhase = (animTime * 0.004f) % 3.14159f
    drawCircle(Color.White.copy(alpha = sin(shimmerPhase)), radius = 2.5f * s, center = Offset(72f * s, 24f * s))
}

private fun DrawScope.drawEpicSword(s: Float, animTime: Float) {
    val flameRed = Color(0xFFE74C3C)
    val flameOrange = Color(0xFFE67E22)
    val flameYellow = Color(0xFFF1C40F)
    val obsidian = Color(0xFF2C3E50)
    val steel = Color(0xFFECF0F1)
    val gold = Color(0xFFF39C12)
    val ruby = Color(0xFFC0392B)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val fireAura = Path().apply {
        moveTo(50f * s, 10f * s)
        quadraticTo(76f * s, 35f * s, 65f * s, 65f * s)
        lineTo(35f * s, 65f * s)
        quadraticTo(24f * s, 35f * s, 50f * s, 10f * s)
        close()
    }
    drawPath(fireAura, flameRed.copy(alpha = 0.35f * pulse))

    repeat(6) { i ->
        val sparkPhase = (animTime * 0.004f + i * 1.1f) % 3.14159f
        val sx = 50f * s + sin(sparkPhase * 2f + i) * 18f * s
        val sy = 62f * s - (sparkPhase / 3.14159f) * 48f * s
        drawCircle(flameYellow.copy(alpha = sin(sparkPhase)), radius = 1.8f * s, center = Offset(sx, sy))
    }

    val bladePath = Path().apply {
        moveTo(50f * s, 14f * s)
        lineTo(58f * s, 26f * s)
        lineTo(54f * s, 34f * s)
        lineTo(60f * s, 42f * s)
        lineTo(56f * s, 62f * s)
        lineTo(44f * s, 62f * s)
        lineTo(40f * s, 42f * s)
        lineTo(46f * s, 34f * s)
        lineTo(42f * s, 26f * s)
        close()
    }
    drawPath(bladePath, steel)
    drawPath(bladePath, obsidian, style = Stroke(1.5f * s))

    drawLine(flameYellow.copy(alpha = pulse), Offset(50f * s, 22f * s), Offset(50f * s, 58f * s), strokeWidth = 2.5f * s)
    drawLine(Color.White, Offset(50f * s, 24f * s), Offset(50f * s, 56f * s), strokeWidth = 1f * s)

    val guardPath = Path().apply {
        moveTo(50f * s, 62f * s)
        lineTo(70f * s, 56f * s)
        lineTo(74f * s, 66f * s)
        lineTo(58f * s, 68f * s)
        lineTo(50f * s, 66f * s)
        lineTo(42f * s, 68f * s)
        lineTo(26f * s, 66f * s)
        lineTo(30f * s, 56f * s)
        close()
    }
    drawPath(guardPath, gold)
    drawCircle(ruby, radius = 4f * s, center = Offset(50f * s, 64f * s))
    drawCircle(Color.White, radius = 1.2f * s, center = Offset(48.5f * s, 62.5f * s))

    drawRect(obsidian, Offset(47f * s, 68f * s), Size(6f * s, 12f * s))
    drawLine(flameOrange, Offset(47f * s, 71f * s), Offset(53f * s, 73f * s), strokeWidth = 1.2f * s)
    drawLine(flameOrange, Offset(47f * s, 75f * s), Offset(53f * s, 77f * s), strokeWidth = 1.2f * s)

    drawCircle(gold, radius = 5f * s, center = Offset(50f * s, 83f * s))
    drawCircle(ruby, radius = 3f * s, center = Offset(50f * s, 83f * s))
}

private fun DrawScope.drawLegendarySword(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val goldBright = Color(0xFFFFF2A3)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    val rot = animTime * 0.002f

    drawOval(
        color = gold,
        topLeft = Offset(center.x - 32f * s, center.y + 10f * s),
        size = Size(64f * s, 20f * s),
        style = Stroke(2f * s)
    )

    repeat(4) { i ->
        val angle = rot + i * (3.14159f / 2f)
        val rx = center.x + cos(angle) * 32f * s
        val ry = center.y + 20f * s + sin(angle) * 8f * s
        drawCircle(cyanGlow, radius = 2.5f * s, center = Offset(rx, ry))
    }

    val sunAura = Path().apply {
        moveTo(50f * s, 6f * s)
        quadraticTo(78f * s, 32f * s, 62f * s, 62f * s)
        lineTo(38f * s, 62f * s)
        quadraticTo(22f * s, 32f * s, 50f * s, 6f * s)
        close()
    }
    drawPath(sunAura, cyanGlow.copy(alpha = 0.3f * pulse))

    repeat(8) { i ->
        val sparkAngle = (animTime * 0.003f + i * 0.8f) % 6.28318f
        val sx = 50f * s + cos(sparkAngle) * (20f + sin(sparkAngle) * 6f) * s
        val sy = 38f * s + sin(sparkAngle) * (26f + cos(sparkAngle) * 6f) * s
        drawCircle(goldBright, radius = 2f * s, center = Offset(sx, sy))
        drawCircle(pureWhite, radius = 1f * s, center = Offset(sx, sy))
    }

    val bladePath = Path().apply {
        moveTo(50f * s, 10f * s)
        lineTo(59f * s, 22f * s)
        lineTo(57f * s, 64f * s)
        lineTo(43f * s, 64f * s)
        lineTo(41f * s, 22f * s)
        close()
    }
    drawPath(bladePath, goldBright)
    drawPath(bladePath, goldDark, style = Stroke(1.5f * s))

    drawLine(pureWhite, Offset(50f * s, 16f * s), Offset(50f * s, 60f * s), strokeWidth = 3f * s)
    drawLine(cyanGlow, Offset(50f * s, 18f * s), Offset(50f * s, 58f * s), strokeWidth = 1.2f * s)

    val guardPath = Path().apply {
        moveTo(50f * s, 62f * s)
        quadraticTo(68f * s, 54f * s, 76f * s, 58f * s)
        lineTo(70f * s, 68f * s)
        lineTo(56f * s, 67f * s)
        lineTo(50f * s, 66f * s)
        lineTo(44f * s, 67f * s)
        lineTo(30f * s, 68f * s)
        lineTo(24f * s, 58f * s)
        quadraticTo(32f * s, 54f * s, 50f * s, 62f * s)
        close()
    }
    drawPath(guardPath, gold)
    drawPath(guardPath, goldDark, style = Stroke(1.5f * s))

    drawCircle(cyanGlow, radius = 5f * s, center = Offset(50f * s, 65f * s))
    drawCircle(pureWhite, radius = 2.5f * s, center = Offset(50f * s, 65f * s))

    drawRect(goldDark, Offset(47f * s, 68f * s), Size(6f * s, 14f * s))
    drawLine(goldBright, Offset(47f * s, 72f * s), Offset(53f * s, 74f * s), strokeWidth = 1.5f * s)
    drawLine(goldBright, Offset(47f * s, 77f * s), Offset(53f * s, 79f * s), strokeWidth = 1.5f * s)

    drawCircle(gold, radius = 6f * s, center = Offset(50f * s, 86f * s))
    drawCircle(cyanGlow, radius = 3.5f * s, center = Offset(50f * s, 86f * s))
    drawCircle(pureWhite, radius = 1.5f * s, center = Offset(50f * s, 86f * s))
}

// ── Magical Staff / Wand Sprites ──────────────────────────────────────────────

private fun DrawScope.drawCommonMagicalStaff(s: Float, isPlaceholder: Boolean = false) {
    val wood = if (isPlaceholder) Color(0xFF443322).copy(alpha = 0.4f) else Color(0xFF8B4513)
    val orb = if (isPlaceholder) Color(0xFF555566).copy(alpha = 0.4f) else Color(0xFF3498DB)

    drawLine(wood, Offset(25f * s, 78f * s), Offset(65f * s, 28f * s), strokeWidth = 5f * s)
    drawCircle(orb, radius = 8f * s, center = Offset(70f * s, 22f * s))
    drawCircle(Color.White, radius = 2.5f * s, center = Offset(68f * s, 20f * s))
}

private fun DrawScope.drawRareMagicalStaff(s: Float, animTime: Float) {
    val mithril = Color(0xFFBDC3C7)
    val sapphire = Color(0xFF29B6F6)
    val cyanGlow = Color(0xFF00E5FF)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    drawCircle(cyanGlow.copy(alpha = 0.3f * pulse), radius = 16f * s, center = Offset(70f * s, 22f * s))

    drawLine(mithril, Offset(22f * s, 82f * s), Offset(65f * s, 28f * s), strokeWidth = 5f * s)

    val prongs = Path().apply {
        moveTo(60f * s, 32f * s)
        lineTo(70f * s, 10f * s)
        lineTo(80f * s, 20f * s)
        close()
    }
    drawPath(prongs, mithril)

    drawCircle(sapphire, radius = 9f * s, center = Offset(70f * s, 22f * s))
    drawCircle(Color.White, radius = 3f * s, center = Offset(68f * s, 20f * s))
}

private fun DrawScope.drawEpicMagicalStaff(s: Float, animTime: Float) {
    val obsidian = Color(0xFF2C3E50)
    val flameRed = Color(0xFFE74C3C)
    val gold = Color(0xFFF39C12)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    drawLine(obsidian, Offset(20f * s, 85f * s), Offset(65f * s, 28f * s), strokeWidth = 6f * s)

    val crest = Path().apply {
        moveTo(58f * s, 34f * s)
        lineTo(50f * s, 12f * s)
        lineTo(70f * s, 20f * s)
        lineTo(88f * s, 10f * s)
        lineTo(80f * s, 32f * s)
        close()
    }
    drawPath(crest, gold)

    drawCircle(flameRed.copy(alpha = 0.4f * pulse), radius = 14f * s, center = Offset(70f * s, 22f * s))
    drawCircle(flameRed, radius = 9f * s, center = Offset(70f * s, 22f * s))
    drawCircle(Color.White, radius = 3f * s, center = Offset(68f * s, 20f * s))
}

private fun DrawScope.drawLegendaryMagicalStaff(s: Float, animTime: Float) {
    val center = Offset(70f * s, 22f * s)
    val gold = Color(0xFFF1C40F)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    val rot = animTime * 0.002f

    drawOval(
        color = gold,
        topLeft = Offset(center.x - 22f * s, center.y - 10f * s),
        size = Size(44f * s, 20f * s),
        style = Stroke(2f * s)
    )

    repeat(4) { i ->
        val angle = rot + i * (3.14159f / 2f)
        val rx = center.x + cos(angle) * 22f * s
        val ry = center.y + sin(angle) * 10f * s
        drawCircle(cyanGlow, radius = 2.5f * s, center = Offset(rx, ry))
    }

    drawLine(gold, Offset(18f * s, 88f * s), Offset(65f * s, 28f * s), strokeWidth = 6f * s)

    drawCircle(cyanGlow.copy(alpha = 0.45f * pulse), radius = 16f * s, center = center)
    drawCircle(cyanGlow, radius = 10f * s, center = center)
    drawCircle(pureWhite, radius = 5f * s, center = center)
}

// ── Axe / Bow Sprites ────────────────────────────────────────────────────────

private fun DrawScope.drawCommonAxeOrBow(s: Float) {
    val wood = Color(0xFF8B4513)
    val iron = Color(0xFF7F8C8D)

    val bowPath = Path().apply {
        moveTo(30f * s, 20f * s)
        quadraticTo(70f * s, 50f * s, 30f * s, 80f * s)
    }
    drawPath(bowPath, wood, style = Stroke(4f * s))
    drawLine(iron, Offset(30f * s, 20f * s), Offset(30f * s, 80f * s), strokeWidth = 1.5f * s)
}

private fun DrawScope.drawRareAxeOrBow(s: Float, animTime: Float) {
    val mithril = Color(0xFFBDC3C7)
    val sapphire = Color(0xFF29B6F6)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val bowPath = Path().apply {
        moveTo(28f * s, 18f * s)
        quadraticTo(74f * s, 50f * s, 28f * s, 82f * s)
    }
    drawPath(bowPath, mithril, style = Stroke(5f * s))
    drawLine(sapphire.copy(alpha = pulse), Offset(28f * s, 18f * s), Offset(28f * s, 82f * s), strokeWidth = 2.5f * s)
}

private fun DrawScope.drawEpicAxeOrBow(s: Float, animTime: Float) {
    val gold = Color(0xFFF39C12)
    val flameRed = Color(0xFFE74C3C)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val axePath = Path().apply {
        moveTo(20f * s, 20f * s)
        lineTo(80f * s, 20f * s)
        quadraticTo(60f * s, 50f * s, 80f * s, 80f * s)
        lineTo(20f * s, 80f * s)
        close()
    }
    drawPath(axePath, gold)
    drawCircle(flameRed.copy(alpha = pulse), radius = 6f * s, center = Offset(50f * s, 50f * s))
}

private fun DrawScope.drawLegendaryAxeOrBow(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    drawCircle(cyanGlow.copy(alpha = 0.35f * pulse), radius = 30f * s, center = Offset(50f * s, 50f * s))
    drawCircle(gold, radius = 20f * s, center = Offset(50f * s, 50f * s), style = Stroke(4f * s))
    drawCircle(pureWhite, radius = 8f * s, center = Offset(50f * s, 50f * s))
}

// ============================================================================
// 2. ARMOR SPRITES (HELMETS & ROBES)
// ============================================================================

private fun DrawScope.drawCommonHelmet(s: Float, isPlaceholder: Boolean = false) {
    val steel = if (isPlaceholder) Color(0xFF555566).copy(alpha = 0.4f) else Color(0xFFBDC3C7)
    val steelDark = if (isPlaceholder) Color(0xFF333344).copy(alpha = 0.4f) else Color(0xFF34495E)
    val leather = if (isPlaceholder) Color(0xFF443322).copy(alpha = 0.4f) else Color(0xFF6E2C00)

    val domePath = Path().apply {
        moveTo(28f * s, 62f * s)
        lineTo(28f * s, 42f * s)
        cubicTo(28f * s, 24f * s, 72f * s, 24f * s, 72f * s, 42f * s)
        lineTo(72f * s, 62f * s)
        close()
    }
    drawPath(domePath, steel)
    drawPath(domePath, steelDark, style = Stroke(2f * s))

    drawRect(Color.Black.copy(alpha = if (isPlaceholder) 0.3f else 0.8f), Offset(32f * s, 48f * s), Size(36f * s, 10f * s))
    drawRect(steelDark, Offset(48f * s, 44f * s), Size(4f * s, 18f * s))
    drawRect(leather, Offset(26f * s, 62f * s), Size(48f * s, 6f * s))

    if (isPlaceholder) {
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4f * s, 4f * s), 0f)
        drawPath(domePath, Color.Gray.copy(alpha = 0.5f), style = Stroke(1.5f * s, pathEffect = dashEffect))
    }
}

private fun DrawScope.drawRareHelmet(s: Float, animTime: Float) {
    val steel = Color(0xFFECF0F1)
    val steelDark = Color(0xFF2C3E50)
    val gold = Color(0xFFF1C40F)
    val sapphire = Color(0xFF29B6F6)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val domePath = Path().apply {
        moveTo(25f * s, 65f * s)
        lineTo(25f * s, 40f * s)
        cubicTo(25f * s, 20f * s, 75f * s, 20f * s, 75f * s, 40f * s)
        lineTo(75f * s, 65f * s)
        lineTo(65f * s, 70f * s)
        lineTo(35f * s, 70f * s)
        close()
    }
    drawPath(domePath, steel)
    drawPath(domePath, steelDark, style = Stroke(2f * s))

    drawRect(steelDark, Offset(30f * s, 46f * s), Size(40f * s, 12f * s))
    drawRect(sapphire.copy(alpha = pulse), Offset(32f * s, 48f * s), Size(36f * s, 3f * s))
    repeat(5) { i ->
        drawRect(steel, Offset((35f + i * 6f) * s, 52f * s), Size(2f * s, 5f * s))
    }

    val plumePath = Path().apply {
        moveTo(50f * s, 22f * s)
        quadraticTo(50f * s, 8f * s, 68f * s, 10f * s)
        quadraticTo(58f * s, 20f * s, 50f * s, 22f * s)
    }
    drawPath(plumePath, sapphire)
    drawRect(gold, Offset(25f * s, 64f * s), Size(50f * s, 4f * s))
}

private fun DrawScope.drawEpicHelmet(s: Float, animTime: Float) {
    val obsidian = Color(0xFF1C2833)
    val steelDark = Color(0xFF2C3E50)
    val flameRed = Color(0xFFE74C3C)
    val gold = Color(0xFFF39C12)
    val ruby = Color(0xFFC0392B)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    repeat(6) { i ->
        val sparkPhase = (animTime * 0.004f + i * 1.05f) % 3.14159f
        val sx = 50f * s + sin(sparkPhase * 2f + i) * 22f * s
        val sy = 60f * s - (sparkPhase / 3.14159f) * 45f * s
        drawCircle(flameRed.copy(alpha = sin(sparkPhase)), radius = 1.8f * s, center = Offset(sx, sy))
    }

    val domePath = Path().apply {
        moveTo(24f * s, 68f * s)
        lineTo(24f * s, 38f * s)
        lineTo(38f * s, 22f * s)
        lineTo(62f * s, 22f * s)
        lineTo(76f * s, 38f * s)
        lineTo(76f * s, 68f * s)
        lineTo(50f * s, 76f * s)
        close()
    }
    drawPath(domePath, obsidian)
    drawPath(domePath, steelDark, style = Stroke(2.5f * s))

    drawRect(Color.Black, Offset(28f * s, 44f * s), Size(44f * s, 12f * s))
    drawRect(flameRed.copy(alpha = pulse), Offset(30f * s, 47f * s), Size(40f * s, 6f * s))
    drawRect(Color.White, Offset(42f * s, 49f * s), Size(16f * s, 2f * s))

    val cheekLeft = Path().apply {
        moveTo(24f * s, 50f * s)
        lineTo(34f * s, 64f * s)
        lineTo(24f * s, 68f * s)
        close()
    }
    val cheekRight = Path().apply {
        moveTo(76f * s, 50f * s)
        lineTo(66f * s, 64f * s)
        lineTo(76f * s, 68f * s)
        close()
    }
    drawPath(cheekLeft, gold)
    drawPath(cheekRight, gold)

    drawCircle(ruby, radius = 4f * s, center = Offset(50f * s, 30f * s))
    drawCircle(Color.White, radius = 1.2f * s, center = Offset(48.5f * s, 28.5f * s))
}

private fun DrawScope.drawLegendaryHelmet(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val goldBright = Color(0xFFFFF2A3)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    val rot = animTime * 0.002f

    drawOval(
        color = gold,
        topLeft = Offset(center.x - 34f * s, center.y - 32f * s),
        size = Size(68f * s, 24f * s),
        style = Stroke(2.5f * s)
    )

    repeat(4) { i ->
        val angle = rot + i * (3.14159f / 2f)
        val rx = center.x + cos(angle) * 34f * s
        val ry = center.y - 20f * s + sin(angle) * 10f * s
        drawCircle(cyanGlow, radius = 2.5f * s, center = Offset(rx, ry))
    }

    val hornLeft = Path().apply {
        moveTo(32f * s, 32f * s)
        quadraticTo(14f * s, 20f * s, 10f * s, 6f * s)
        quadraticTo(22f * s, 18f * s, 36f * s, 28f * s)
    }
    val hornRight = Path().apply {
        moveTo(68f * s, 32f * s)
        quadraticTo(86f * s, 20f * s, 90f * s, 6f * s)
        quadraticTo(78f * s, 18f * s, 64f * s, 28f * s)
    }
    drawPath(hornLeft, gold)
    drawPath(hornRight, gold)
    drawPath(hornLeft, goldDark, style = Stroke(1.5f * s))
    drawPath(hornRight, goldDark, style = Stroke(1.5f * s))

    val domePath = Path().apply {
        moveTo(24f * s, 70f * s)
        lineTo(24f * s, 38f * s)
        lineTo(50f * s, 20f * s)
        lineTo(76f * s, 38f * s)
        lineTo(76f * s, 70f * s)
        lineTo(50f * s, 78f * s)
        close()
    }
    drawPath(domePath, gold)
    drawPath(domePath, goldDark, style = Stroke(2f * s))

    drawRect(Color(0xFF101820), Offset(28f * s, 44f * s), Size(44f * s, 14f * s))
    drawRect(cyanGlow.copy(alpha = pulse), Offset(30f * s, 47f * s), Size(40f * s, 8f * s))
    drawRect(pureWhite, Offset(40f * s, 49f * s), Size(20f * s, 3f * s))

    drawCircle(cyanGlow, radius = 5f * s, center = Offset(50f * s, 32f * s))
    drawCircle(pureWhite, radius = 2.5f * s, center = Offset(50f * s, 32f * s))

    repeat(6) { i ->
        val sparkPhase = (animTime * 0.003f + i * 1.05f) % 3.14159f
        val sx = 50f * s + cos(sparkPhase * 2f + i) * 28f * s
        val sy = 50f * s - sin(sparkPhase) * 24f * s
        drawCircle(goldBright, radius = 1.8f * s, center = Offset(sx, sy))
    }
}

// ── Robe / Garb Sprites ──────────────────────────────────────────────────────

private fun DrawScope.drawCommonRobe(s: Float) {
    val cloth = Color(0xFF2980B9)
    val belt = Color(0xFF8B4513)

    val robePath = Path().apply {
        moveTo(35f * s, 20f * s)
        lineTo(65f * s, 20f * s)
        lineTo(75f * s, 80f * s)
        lineTo(25f * s, 80f * s)
        close()
    }
    drawPath(robePath, cloth)
    drawRect(belt, Offset(28f * s, 50f * s), Size(44f * s, 6f * s))
}

private fun DrawScope.drawRareRobe(s: Float, animTime: Float) {
    val silk = Color(0xFF8E44AD)
    val gold = Color(0xFFF1C40F)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val robePath = Path().apply {
        moveTo(32f * s, 18f * s)
        lineTo(68f * s, 18f * s)
        lineTo(78f * s, 82f * s)
        lineTo(22f * s, 82f * s)
        close()
    }
    drawPath(robePath, silk)
    drawPath(robePath, gold.copy(alpha = pulse), style = Stroke(3f * s))
}

private fun DrawScope.drawEpicRobe(s: Float, animTime: Float) {
    val crimson = Color(0xFFC0392B)
    val obsidian = Color(0xFF1C2833)
    val ruby = Color(0xFFE74C3C)

    val robePath = Path().apply {
        moveTo(30f * s, 16f * s)
        lineTo(70f * s, 16f * s)
        lineTo(82f * s, 85f * s)
        lineTo(18f * s, 85f * s)
        close()
    }
    drawPath(robePath, obsidian)
    drawRect(crimson, Offset(35f * s, 20f * s), Size(30f * s, 60f * s))
    drawCircle(ruby, radius = 6f * s, center = Offset(50f * s, 35f * s))
}

private fun DrawScope.drawLegendaryRobe(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    val robePath = Path().apply {
        moveTo(28f * s, 14f * s)
        lineTo(72f * s, 14f * s)
        lineTo(85f * s, 88f * s)
        lineTo(15f * s, 88f * s)
        close()
    }
    drawPath(robePath, gold)
    drawPath(robePath, cyanGlow, style = Stroke(3f * s))
    drawCircle(pureWhite, radius = 8f * s, center = Offset(50f * s, 32f * s))
}

// ============================================================================
// 3. SHIELD SPRITES (HEAVY SHIELDS & MAGICAL BARRIERS)
// ============================================================================

private fun DrawScope.drawCommonShield(s: Float, isPlaceholder: Boolean = false) {
    val wood = if (isPlaceholder) Color(0xFF443322).copy(alpha = 0.4f) else Color(0xFF6E2C00)
    val iron = if (isPlaceholder) Color(0xFF555566).copy(alpha = 0.4f) else Color(0xFF34495E)

    drawCircle(wood, radius = 28f * s, center = Offset(50f * s, 50f * s))
    drawCircle(iron, radius = 28f * s, center = Offset(50f * s, 50f * s), style = Stroke(3f * s))

    drawLine(iron, Offset(30f * s, 32f * s), Offset(30f * s, 68f * s), strokeWidth = 1.5f * s)
    drawLine(iron, Offset(70f * s, 32f * s), Offset(70f * s, 68f * s), strokeWidth = 1.5f * s)

    drawCircle(iron, radius = 8f * s, center = Offset(50f * s, 50f * s))
    drawCircle(Color.Gray, radius = 3f * s, center = Offset(48f * s, 48f * s))

    if (isPlaceholder) {
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4f * s, 4f * s), 0f)
        drawCircle(Color.Gray.copy(alpha = 0.5f), radius = 28f * s, center = Offset(50f * s, 50f * s), style = Stroke(1.5f * s, pathEffect = dashEffect))
    }
}

private fun DrawScope.drawRareShield(s: Float, animTime: Float) {
    val navyBlue = Color(0xFF1B4F72)
    val steel = Color(0xFFECF0F1)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val sapphire = Color(0xFF29B6F6)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val shieldPath = Path().apply {
        moveTo(25f * s, 22f * s)
        lineTo(75f * s, 22f * s)
        lineTo(75f * s, 48f * s)
        quadraticTo(70f * s, 76f * s, 50f * s, 86f * s)
        quadraticTo(30f * s, 76f * s, 25f * s, 48f * s)
        close()
    }
    drawPath(shieldPath, navyBlue)
    drawPath(shieldPath, steel, style = Stroke(3.5f * s))
    drawPath(shieldPath, goldDark, style = Stroke(1.5f * s))

    drawRect(gold, Offset(46f * s, 28f * s), Size(8f * s, 48f * s))
    drawRect(gold, Offset(30f * s, 40f * s), Size(40f * s, 8f * s))

    drawCircle(sapphire.copy(alpha = pulse), radius = 6f * s, center = Offset(50f * s, 44f * s))
    drawCircle(Color.White, radius = 2f * s, center = Offset(48.5f * s, 42.5f * s))
}

private fun DrawScope.drawEpicShield(s: Float, animTime: Float) {
    val obsidian = Color(0xFF1C2833)
    val flameRed = Color(0xFFE74C3C)
    val gold = Color(0xFFF39C12)
    val ruby = Color(0xFFC0392B)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val auraShield = Path().apply {
        moveTo(20f * s, 18f * s)
        lineTo(80f * s, 18f * s)
        lineTo(80f * s, 48f * s)
        quadraticTo(74f * s, 80f * s, 50f * s, 90f * s)
        quadraticTo(26f * s, 80f * s, 20f * s, 48f * s)
        close()
    }
    drawPath(auraShield, flameRed.copy(alpha = 0.3f * pulse))

    val shieldPath = Path().apply {
        moveTo(24f * s, 20f * s)
        lineTo(76f * s, 20f * s)
        lineTo(76f * s, 48f * s)
        quadraticTo(70f * s, 76f * s, 50f * s, 86f * s)
        quadraticTo(30f * s, 76f * s, 24f * s, 48f * s)
        close()
    }
    drawPath(shieldPath, obsidian)

    repeat(3) { i ->
        val sy = (28 + i * 14).toFloat()
        drawArc(gold, startAngle = 0f, sweepAngle = 180f, useCenter = false, topLeft = Offset(36f * s, sy * s), size = Size(28f * s, 10f * s), style = Stroke(1.5f * s))
    }

    drawPath(shieldPath, gold, style = Stroke(3.5f * s))

    drawCircle(ruby, radius = 7f * s, center = Offset(50f * s, 48f * s))
    drawCircle(flameRed.copy(alpha = pulse), radius = 5f * s, center = Offset(50f * s, 48f * s))
    drawCircle(Color.White, radius = 2f * s, center = Offset(48f * s, 46f * s))
}

private fun DrawScope.drawLegendaryShield(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val goldBright = Color(0xFFFFF2A3)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    val rot = animTime * 0.002f

    drawOval(
        color = gold,
        topLeft = Offset(center.x - 36f * s, center.y - 10f * s),
        size = Size(72f * s, 24f * s),
        style = Stroke(2.5f * s)
    )

    repeat(4) { i ->
        val angle = rot + i * (3.14159f / 2f)
        val rx = center.x + cos(angle) * 36f * s
        val ry = center.y + sin(angle) * 12f * s
        drawCircle(cyanGlow, radius = 2.5f * s, center = Offset(rx, ry))
    }

    val barrierPath = Path().apply {
        moveTo(18f * s, 16f * s)
        lineTo(82f * s, 16f * s)
        lineTo(82f * s, 48f * s)
        quadraticTo(74f * s, 82f * s, 50f * s, 92f * s)
        quadraticTo(26f * s, 82f * s, 18f * s, 48f * s)
        close()
    }
    drawPath(barrierPath, cyanGlow.copy(alpha = 0.35f * pulse))

    val shieldPath = Path().apply {
        moveTo(22f * s, 20f * s)
        lineTo(78f * s, 20f * s)
        lineTo(78f * s, 48f * s)
        quadraticTo(70f * s, 78f * s, 50f * s, 88f * s)
        quadraticTo(30f * s, 78f * s, 22f * s, 48f * s)
        close()
    }
    drawPath(shieldPath, gold)
    drawPath(shieldPath, goldDark, style = Stroke(2.5f * s))

    val innerShield = Path().apply {
        moveTo(30f * s, 26f * s)
        lineTo(70f * s, 26f * s)
        lineTo(70f * s, 46f * s)
        quadraticTo(64f * s, 70f * s, 50f * s, 78f * s)
        quadraticTo(36f * s, 70f * s, 30f * s, 46f * s)
        close()
    }
    drawPath(innerShield, Color(0xFF101820))

    drawCircle(cyanGlow, radius = 8f * s, center = center)
    drawCircle(pureWhite, radius = 4f * s, center = center)

    repeat(6) { i ->
        val sparkPhase = (animTime * 0.003f + i * 1.05f) % 3.14159f
        val sx = center.x + cos(sparkPhase * 2f + i) * 28f * s
        val sy = center.y + sin(sparkPhase * 2f + i) * 28f * s
        drawCircle(goldBright, radius = 2f * s, center = Offset(sx, sy))
    }
}

// ── Barrier Shields ──────────────────────────────────────────────────────────

private fun DrawScope.drawCommonBarrier(s: Float) {
    val cyan = Color(0xFF00E5FF)
    drawCircle(cyan.copy(alpha = 0.3f), radius = 30f * s, center = Offset(50f * s, 50f * s))
    drawCircle(cyan, radius = 30f * s, center = Offset(50f * s, 50f * s), style = Stroke(2f * s))
}

private fun DrawScope.drawRareBarrier(s: Float, animTime: Float) {
    val cyanGlow = Color(0xFF00E5FF)
    val sapphire = Color(0xFF29B6F6)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    drawCircle(cyanGlow.copy(alpha = 0.4f * pulse), radius = 32f * s, center = Offset(50f * s, 50f * s))
    drawCircle(sapphire, radius = 20f * s, center = Offset(50f * s, 50f * s))
}

private fun DrawScope.drawEpicBarrier(s: Float, animTime: Float) {
    val flameRed = Color(0xFFE74C3C)
    val gold = Color(0xFFF39C12)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    drawCircle(flameRed.copy(alpha = 0.5f * pulse), radius = 34f * s, center = Offset(50f * s, 50f * s))
    drawCircle(gold, radius = 22f * s, center = Offset(50f * s, 50f * s), style = Stroke(4f * s))
}

private fun DrawScope.drawLegendaryBarrier(s: Float, animTime: Float) {
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    drawCircle(cyanGlow.copy(alpha = 0.6f), radius = 36f * s, center = Offset(50f * s, 50f * s))
    drawCircle(pureWhite, radius = 12f * s, center = Offset(50f * s, 50f * s))
}

// ============================================================================
// 4. ACCESSORY SPRITES (RINGS, AMULETS & CROWNS)
// ============================================================================

private fun DrawScope.drawCommonAccessory(s: Float, isPlaceholder: Boolean = false) {
    val silver = if (isPlaceholder) Color(0xFF555566).copy(alpha = 0.4f) else Color(0xFFBDC3C7)
    val silverDark = if (isPlaceholder) Color(0xFF333344).copy(alpha = 0.4f) else Color(0xFF7F8C8D)
    val stone = if (isPlaceholder) Color(0xFF444455).copy(alpha = 0.4f) else Color(0xFF3498DB)

    drawCircle(silver, radius = 24f * s, center = Offset(50f * s, 54f * s), style = Stroke(6f * s))
    drawCircle(silverDark, radius = 27f * s, center = Offset(50f * s, 54f * s), style = Stroke(1.5f * s))

    drawCircle(stone, radius = 5f * s, center = Offset(50f * s, 28f * s))
    drawCircle(Color.White, radius = 1.5f * s, center = Offset(48.5f * s, 26.5f * s))

    if (isPlaceholder) {
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4f * s, 4f * s), 0f)
        drawCircle(Color.Gray.copy(alpha = 0.5f), radius = 27f * s, center = Offset(50f * s, 54f * s), style = Stroke(1.5f * s, pathEffect = dashEffect))
    }
}

private fun DrawScope.drawRareAccessory(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val ruby = Color(0xFFE74C3C)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    drawCircle(gold, radius = 24f * s, center = Offset(50f * s, 54f * s), style = Stroke(7f * s))
    drawCircle(goldDark, radius = 27.5f * s, center = Offset(50f * s, 54f * s), style = Stroke(1.5f * s))

    val mountPath = Path().apply {
        moveTo(40f * s, 32f * s)
        lineTo(60f * s, 32f * s)
        lineTo(56f * s, 24f * s)
        lineTo(44f * s, 24f * s)
        close()
    }
    drawPath(mountPath, gold)

    val gemPath = Path().apply {
        moveTo(50f * s, 14f * s)
        lineTo(62f * s, 24f * s)
        lineTo(50f * s, 34f * s)
        lineTo(38f * s, 24f * s)
        close()
    }
    drawPath(gemPath, ruby.copy(alpha = pulse))
    drawPath(gemPath, Color.White, style = Stroke(1.5f * s))

    drawCircle(Color.White, radius = 2.5f * s, center = Offset(46f * s, 20f * s))
}

private fun DrawScope.drawEpicAccessory(s: Float, animTime: Float) {
    val gold = Color(0xFFF39C12)
    val goldDark = Color(0xFFB7950B)
    val purpleVoid = Color(0xFF8E44AD)
    val cyanGlow = Color(0xFF00E5FF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    repeat(6) { i ->
        val sparkAngle = (animTime * 0.003f + i * 1.05f) % 6.28318f
        val sx = 50f * s + cos(sparkAngle) * 26f * s
        val sy = 30f * s + sin(sparkAngle) * 20f * s
        drawCircle(purpleVoid.copy(alpha = sin(sparkAngle)), radius = 2f * s, center = Offset(sx, sy))
    }

    drawCircle(gold, radius = 24f * s, center = Offset(50f * s, 54f * s), style = Stroke(8f * s))
    drawCircle(goldDark, radius = 28f * s, center = Offset(50f * s, 54f * s), style = Stroke(2f * s))

    val clawLeft = Path().apply {
        moveTo(36f * s, 36f * s)
        lineTo(42f * s, 22f * s)
        lineTo(48f * s, 28f * s)
        close()
    }
    val clawRight = Path().apply {
        moveTo(64f * s, 36f * s)
        lineTo(58f * s, 22f * s)
        lineTo(52f * s, 28f * s)
        close()
    }
    drawPath(clawLeft, gold)
    drawPath(clawRight, gold)

    drawCircle(purpleVoid, radius = 12f * s, center = Offset(50f * s, 26f * s))
    drawCircle(cyanGlow.copy(alpha = pulse), radius = 8f * s, center = Offset(50f * s, 26f * s))
    drawCircle(Color.White, radius = 3f * s, center = Offset(48f * s, 24f * s))
}

private fun DrawScope.drawLegendaryAccessory(s: Float, animTime: Float) {
    val center = Offset(50f * s, 50f * s)
    val goldBright = Color(0xFFFFF2A3)
    val gold = Color(0xFFF1C40F)
    val goldDark = Color(0xFFB7950B)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f
    val rot = animTime * 0.002f

    drawOval(
        color = gold,
        topLeft = Offset(center.x - 36f * s, center.y - 30f * s),
        size = Size(72f * s, 24f * s),
        style = Stroke(2.5f * s)
    )

    repeat(4) { i ->
        val angle = rot + i * (3.14159f / 2f)
        val rx = center.x + cos(angle) * 36f * s
        val ry = center.y - 18f * s + sin(angle) * 12f * s
        drawCircle(cyanGlow, radius = 2.5f * s, center = Offset(rx, ry))
    }

    val wingLeft = Path().apply {
        moveTo(32f * s, 32f * s)
        quadraticTo(14f * s, 20f * s, 8f * s, 8f * s)
        quadraticTo(22f * s, 18f * s, 38f * s, 26f * s)
    }
    val wingRight = Path().apply {
        moveTo(68f * s, 32f * s)
        quadraticTo(86f * s, 20f * s, 92f * s, 8f * s)
        quadraticTo(78f * s, 18f * s, 62f * s, 26f * s)
    }
    drawPath(wingLeft, gold)
    drawPath(wingRight, gold)

    drawCircle(gold, radius = 24f * s, center = Offset(50f * s, 56f * s), style = Stroke(8f * s))
    drawCircle(goldDark, radius = 28f * s, center = Offset(50f * s, 56f * s), style = Stroke(2f * s))

    drawCircle(cyanGlow.copy(alpha = 0.4f * pulse), radius = 16f * s, center = Offset(50f * s, 28f * s))

    val starPath = Path().apply {
        moveTo(50f * s, 12f * s)
        lineTo(55f * s, 23f * s)
        lineTo(66f * s, 28f * s)
        lineTo(55f * s, 33f * s)
        lineTo(50f * s, 44f * s)
        lineTo(45f * s, 33f * s)
        lineTo(34f * s, 28f * s)
        lineTo(45f * s, 23f * s)
        close()
    }
    drawPath(starPath, goldBright)
    drawCircle(pureWhite, radius = 4f * s, center = Offset(50f * s, 28f * s))

    repeat(6) { i ->
        val sparkPhase = (animTime * 0.003f + i * 1.05f) % 3.14159f
        val sx = center.x + cos(sparkPhase * 2f + i) * 28f * s
        val sy = center.y - sin(sparkPhase) * 24f * s
        drawCircle(goldBright, radius = 1.8f * s, center = Offset(sx, sy))
    }
}

// ── Amulet / Pendant Sprites ──────────────────────────────────────────────────

private fun DrawScope.drawCommonAmulet(s: Float) {
    val silver = Color(0xFFBDC3C7)
    val copper = Color(0xFFD35400)

    val chainPath = Path().apply {
        moveTo(25f * s, 20f * s)
        quadraticTo(50f * s, 70f * s, 75f * s, 20f * s)
    }
    drawPath(chainPath, silver, style = Stroke(2f * s))
    drawCircle(copper, radius = 10f * s, center = Offset(50f * s, 50f * s))
}

private fun DrawScope.drawRareAmulet(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val sapphire = Color(0xFF29B6F6)
    val pulse = 0.85f + sin(animTime * 0.005f) * 0.15f

    val chainPath = Path().apply {
        moveTo(25f * s, 18f * s)
        quadraticTo(50f * s, 68f * s, 75f * s, 18f * s)
    }
    drawPath(chainPath, gold, style = Stroke(2.5f * s))
    drawCircle(sapphire.copy(alpha = pulse), radius = 12f * s, center = Offset(50f * s, 50f * s))
    drawCircle(Color.White, radius = 3f * s, center = Offset(48f * s, 48f * s))
}

private fun DrawScope.drawEpicAmulet(s: Float, animTime: Float) {
    val gold = Color(0xFFF39C12)
    val ruby = Color(0xFFE74C3C)

    val chainPath = Path().apply {
        moveTo(20f * s, 16f * s)
        quadraticTo(50f * s, 68f * s, 80f * s, 16f * s)
    }
    drawPath(chainPath, gold, style = Stroke(3f * s))
    drawCircle(ruby, radius = 14f * s, center = Offset(50f * s, 50f * s))
    drawCircle(Color.White, radius = 4f * s, center = Offset(48f * s, 48f * s))
}

private fun DrawScope.drawLegendaryAmulet(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    val chainPath = Path().apply {
        moveTo(18f * s, 14f * s)
        quadraticTo(50f * s, 68f * s, 82f * s, 14f * s)
    }
    drawPath(chainPath, gold, style = Stroke(3.5f * s))
    drawCircle(cyanGlow, radius = 16f * s, center = Offset(50f * s, 50f * s))
    drawCircle(pureWhite, radius = 6f * s, center = Offset(50f * s, 50f * s))
}

// ── Crown Sprites ────────────────────────────────────────────────────────────

private fun DrawScope.drawCommonCrown(s: Float) {
    val copper = Color(0xFFD35400)
    val crownPath = Path().apply {
        moveTo(20f * s, 70f * s)
        lineTo(20f * s, 35f * s)
        lineTo(35f * s, 55f * s)
        lineTo(50f * s, 30f * s)
        lineTo(65f * s, 55f * s)
        lineTo(80f * s, 35f * s)
        lineTo(80f * s, 70f * s)
        close()
    }
    drawPath(crownPath, copper)
}

private fun DrawScope.drawRareCrown(s: Float, animTime: Float) {
    val silver = Color(0xFFECF0F1)
    val sapphire = Color(0xFF29B6F6)

    val crownPath = Path().apply {
        moveTo(20f * s, 70f * s)
        lineTo(20f * s, 30f * s)
        lineTo(35f * s, 50f * s)
        lineTo(50f * s, 25f * s)
        lineTo(65f * s, 50f * s)
        lineTo(80f * s, 30f * s)
        lineTo(80f * s, 70f * s)
        close()
    }
    drawPath(crownPath, silver)
    drawCircle(sapphire, radius = 4f * s, center = Offset(50f * s, 45f * s))
}

private fun DrawScope.drawEpicCrown(s: Float, animTime: Float) {
    val gold = Color(0xFFF39C12)
    val ruby = Color(0xFFE74C3C)

    val crownPath = Path().apply {
        moveTo(18f * s, 70f * s)
        lineTo(18f * s, 25f * s)
        lineTo(34f * s, 48f * s)
        lineTo(50f * s, 20f * s)
        lineTo(66f * s, 48f * s)
        lineTo(82f * s, 25f * s)
        lineTo(82f * s, 70f * s)
        close()
    }
    drawPath(crownPath, gold)
    drawCircle(ruby, radius = 6f * s, center = Offset(50f * s, 45f * s))
}

private fun DrawScope.drawLegendaryCrown(s: Float, animTime: Float) {
    val gold = Color(0xFFF1C40F)
    val cyanGlow = Color(0xFF00E5FF)
    val pureWhite = Color(0xFFFFFFFF)

    val crownPath = Path().apply {
        moveTo(15f * s, 72f * s)
        lineTo(15f * s, 20f * s)
        lineTo(32f * s, 46f * s)
        lineTo(50f * s, 15f * s)
        lineTo(68f * s, 46f * s)
        lineTo(85f * s, 20f * s)
        lineTo(85f * s, 72f * s)
        close()
    }
    drawPath(crownPath, gold)
    drawCircle(cyanGlow, radius = 8f * s, center = Offset(50f * s, 45f * s))
    drawCircle(pureWhite, radius = 3f * s, center = Offset(50f * s, 45f * s))
}


@Preview(showBackground = true, backgroundColor = 0xFF0A0512)
@Composable
fun AllMythicSetsPreview() {
    Column(modifier = Modifier.padding(16.dp)) {

        // Helper function for quick item generation in preview
        @Composable
        fun PreviewItem(setId: String, slot: ItemSlot) {
            EquipmentSprite(
                item = Item(name = "Preview", rarity = Rarity.MYTHIC, slot = slot, setId = setId, id = "", emoji = "", floorFound = 100),
                slot = slot,
                modifier = Modifier.size(60.dp)
            )
        }

        // ROW 1: Cloud & Vivi
        Row {
            PreviewItem("set_crystal", ItemSlot.WEAPON)
            PreviewItem("set_crystal", ItemSlot.ARMOR)
            PreviewItem("set_crystal", ItemSlot.SHIELD)
        }
        Row {
            PreviewItem("set_magus", ItemSlot.WEAPON)
            PreviewItem("set_magus", ItemSlot.ARMOR)
            PreviewItem("set_magus", ItemSlot.ACCESSORY)
        }
        Spacer(Modifier.height(8.dp))

        // ROW 2: Aerith & Yuffie
        Row {
            PreviewItem("set_devout", ItemSlot.WEAPON)
            PreviewItem("set_devout", ItemSlot.ARMOR)
            PreviewItem("set_devout", ItemSlot.ACCESSORY)
            Spacer(modifier = Modifier.weight(1f))
            PreviewItem("set_shadow", ItemSlot.WEAPON)
            PreviewItem("set_shadow", ItemSlot.ARMOR)
            PreviewItem("set_shadow", ItemSlot.ACCESSORY)
        }
        Spacer(Modifier.height(8.dp))

        // ROW 3: Tifa & Kain
        Row {
            PreviewItem("set_chakra", ItemSlot.WEAPON)
            PreviewItem("set_chakra", ItemSlot.ARMOR)
            PreviewItem("set_chakra", ItemSlot.ACCESSORY)
            Spacer(modifier = Modifier.weight(1f))
            PreviewItem("set_wyvern", ItemSlot.WEAPON)
            PreviewItem("set_wyvern", ItemSlot.ARMOR)
            PreviewItem("set_wyvern", ItemSlot.SHIELD)
        }
        Spacer(Modifier.height(8.dp))

        // ROW 4: Red Mage & Yuna
        Row {
            PreviewItem("set_crimson", ItemSlot.WEAPON)
            PreviewItem("set_crimson", ItemSlot.ARMOR)
            PreviewItem("set_crimson", ItemSlot.ACCESSORY)
            Spacer(modifier = Modifier.weight(1f))
            PreviewItem("set_evoker", ItemSlot.WEAPON)
            PreviewItem("set_evoker", ItemSlot.ARMOR)
            PreviewItem("set_evoker", ItemSlot.ACCESSORY)
        }
        Spacer(Modifier.height(8.dp))

        // ROW 5: Edward & Auron
        Row {
            PreviewItem("set_apollo", ItemSlot.WEAPON)
            PreviewItem("set_apollo", ItemSlot.ARMOR)
            PreviewItem("set_apollo", ItemSlot.ACCESSORY)
            Spacer(modifier = Modifier.weight(1f))
            PreviewItem("set_genji", ItemSlot.WEAPON)
            PreviewItem("set_genji", ItemSlot.ARMOR)
            PreviewItem("set_genji", ItemSlot.ACCESSORY)
        }
        Spacer(Modifier.height(8.dp))

        // ROW 6: Cecil & Luneth
        Row {
            PreviewItem("set_aegis", ItemSlot.WEAPON)
            PreviewItem("set_aegis", ItemSlot.ARMOR)
            PreviewItem("set_aegis", ItemSlot.SHIELD)
            Spacer(modifier = Modifier.weight(1f))
            PreviewItem("set_onion", ItemSlot.WEAPON)
            PreviewItem("set_onion", ItemSlot.ARMOR)
            PreviewItem("set_onion", ItemSlot.SHIELD)
        }
    }
}