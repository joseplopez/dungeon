package com.game.dungeon.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.dungeon.R
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.models.Rarity
import com.game.dungeon.ui.components.*
import com.game.dungeon.ui.theme.*

// --- Custom Colors for Redesign ---
val OrnateGoldLight = Color(0xFFFFF2A3)
val OrnateGoldMid = Color(0xFFD4AF37)
val OrnateGoldDark = Color(0xFF8B6508)
val ChestWoodDark = Color(0xFF2A1708)
val ChestWoodLight = Color(0xFF4A2E16)
val ChestBorderGold = Color(0xFFDAA520)

@Composable
private fun TopBannerHeader(
    heroName: String,
    isMuted: Boolean = false,
    onToggleMusic: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    GoldenBorderBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(BgDarkest)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PixelButton(
                label = safeStringResource(R.string.back_button),
                onClick = onBack,
                modifier = Modifier.height(32.dp)
            )

            Text(
                text = safeStringResource(R.string.equipment_title, heroName),
                style = PixelHeading
            )

            MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
        }
    }
}

@Composable
fun OrnateStatPanel(
    stats: Map<String, Int>,
    bonuses: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val statRows = remember {
        listOf(
            Triple("❤️", R.string.stat_hp, "HP"),
            Triple("💙", R.string.stat_mp, "MP"),
            Triple("⚔️", R.string.stat_attack, "ATK"),
            Triple("🛡️", R.string.stat_defense, "DEF"),
            Triple("🪄", R.string.stat_magic, "MAG"),
            Triple("🎯", R.string.stat_crit_chance, "CRIT_CHANCE"),
            Triple("💥", R.string.stat_crit_damage, "CRIT_DAMAGE")
        )
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF281C08), Color(0xFF120C04))
                ),
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = 2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(OrnateGoldLight, OrnateGoldDark)
                ),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(8.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(statRows) { (icon, resId, key) ->
                PreviewStatRow(icon = icon, resId = resId, key = key, stats = stats, bonuses = bonuses)
            }
        }
    }
}

@Composable
private fun PreviewStatRow(
    icon: String,
    resId: Int,
    key: String,
    stats: Map<String, Int>,
    bonuses: Map<String, Int>
) {
    val label = safeStringResource(resId)
    val valNum = stats[key] ?: 0
    val bonus = bonuses[key] ?: 0
    val isPercent = key == "CRIT_CHANCE" || key == "CRIT_DAMAGE"
    val valueStr = if (isPercent) "$valNum%" else valNum.toString()
    val bonusStr = if (isPercent) " +$bonus%" else " +$bonus"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF38240C), Color(0xFF1C1206))
                ),
                shape = RoundedCornerShape(4.dp)
            )
            .border(1.dp, OrnateGoldDark.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 12.sp)
                Spacer(Modifier.width(6.dp))
                Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(valueStr, color = OrnateGoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                if (bonus > 0) {
                    Text(bonusStr, color = Color(0xFF44FF88), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun HeroPlatformArea(
    heroClass: HeroClass,
    equipped: List<Item>,
    onQuickEquip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF150A24), Color(0xFF08040C))
                )
            )
    ) {
        // Cosmic Star Dust particles canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val starPositions = listOf(
                Offset(size.width * 0.2f, size.height * 0.15f),
                Offset(size.width * 0.8f, size.height * 0.2f),
                Offset(size.width * 0.15f, size.height * 0.45f),
                Offset(size.width * 0.85f, size.height * 0.5f),
                Offset(size.width * 0.3f, size.height * 0.3f),
                Offset(size.width * 0.7f, size.height * 0.35f)
            )
            starPositions.forEach { pos ->
                drawCircle(color = Color(0xFFEEAAFF).copy(alpha = 0.6f), radius = 2.dp.toPx(), center = pos)
                drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 1.dp.toPx(), center = pos)
            }

            // Stone brick pathway at bottom leading to platform
            val pathW = size.width * 0.6f
            val pathStartX = (size.width - pathW) / 2f
            val stonePath = Path().apply {
                moveTo(pathStartX, size.height)
                lineTo(pathStartX + pathW, size.height)
                lineTo(size.width * 0.65f, size.height * 0.65f)
                lineTo(size.width * 0.35f, size.height * 0.65f)
                close()
            }
            drawPath(stonePath, color = Color(0xFF32283C))
            drawPath(stonePath, color = OrnateGoldDark.copy(alpha = 0.4f), style = Stroke(1.dp.toPx()))

            // Hexagonal Gold Pedestal Platform
            val pedCenterY = size.height * 0.62f
            val pedW = size.width * 0.45f
            val pedH = 20.dp.toPx()
            val pedLeft = (size.width - pedW) / 2f

            val hexPlatform = Path().apply {
                moveTo(pedLeft + pedW * 0.2f, pedCenterY - pedH / 2)
                lineTo(pedLeft + pedW * 0.8f, pedCenterY - pedH / 2)
                lineTo(pedLeft + pedW, pedCenterY)
                lineTo(pedLeft + pedW * 0.8f, pedCenterY + pedH / 2)
                lineTo(pedLeft + pedW * 0.2f, pedCenterY + pedH / 2)
                lineTo(pedLeft, pedCenterY)
                close()
            }
            drawPath(hexPlatform, brush = Brush.verticalGradient(listOf(OrnateGoldLight, OrnateGoldDark)))
            drawPath(hexPlatform, color = Color.Black, style = Stroke(1.5.dp.toPx()))
        }

        // Hero Sprite standing on pedestal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeroSprite(
                heroClass = heroClass,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 5 Equipment Slots Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val slots = listOf(
                    Pair(safeStringResource(R.string.slot_weapon), ItemSlot.WEAPON),
                    Pair(safeStringResource(R.string.slot_head), ItemSlot.ARMOR),
                    Pair(safeStringResource(R.string.slot_body), ItemSlot.SHIELD),
                    Pair(safeStringResource(R.string.slot_accessory_1), ItemSlot.ACCESSORY),
                    Pair(safeStringResource(R.string.slot_accessory_2), ItemSlot.ACCESSORY)
                )

                slots.forEachIndexed { index, (label, slotType) ->
                    val matchingItems = equipped.filter { it.slot == slotType }
                    val item = if (slotType == ItemSlot.ACCESSORY) {
                        if (index == 3) matchingItems.getOrNull(0) else matchingItems.getOrNull(1)
                    } else {
                        matchingItems.firstOrNull()
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(label, color = OrnateGoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color(0xFF180C28), RoundedCornerShape(4.dp))
                                .border(
                                    width = 1.5.dp,
                                    color = item?.rarity?.color?.let { Color(it) } ?: OrnateGoldDark,
                                    shape = RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            EquipmentSprite(
                                item = item,
                                slot = slotType,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Quick Equip Button
            PixelButton(
                label = safeStringResource(R.string.quick_equip),
                onClick = onQuickEquip,
                modifier = Modifier
                    .width(130.dp)
                    .height(36.dp)
            )
        }
    }
}

@Composable
fun InventoryChestPanel(
    inventory: List<Item>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(ChestWoodLight, ChestWoodDark)
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .border(2.5.dp, ChestBorderGold, RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Chest Top Bar Lock
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(ChestBorderGold, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🔒 " + safeStringResource(R.string.inventory_chest_title), color = ChestWoodDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(6.dp))

            // Inventory Item List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(inventory) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .background(Color(0xFF1E120A), RoundedCornerShape(4.dp))
                            .border(1.dp, Color(item.rarity.color), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EquipmentSprite(
                            item = item,
                            slot = item.slot,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = safeStringResource(R.string.item_lvl_rarity_format, item.floorFound, item.rarity.name),
                                color = Color.Gray,
                                fontSize = 8.sp
                            )
                        }
                        Text("🪙${item.sellValue}", color = OrnateGoldLight, fontSize = 9.sp)
                    }
                }

                // Empty slot placeholders if short
                if (inventory.size < 6) {
                    items(6 - inventory.size) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .background(Color(0xFF140A04).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .border(1.dp, Color.DarkGray.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))


        }
    }
}

@Preview(widthDp = 640, heightDp = 320)
@Composable
fun RedesignedEquipmentScreenPreview() {
    val sampleItems = listOf(
        Item("1", "Common Plate", ItemSlot.ARMOR, Rarity.COMMON, defenseBonus = 8, floorFound = 1, emoji = "🛡️"),
        Item("2", "Common Ring", ItemSlot.ACCESSORY, Rarity.RARE, magicBonus = 4, floorFound = 2, emoji = "💍"),
        Item("3", "Iron Sword", ItemSlot.WEAPON, Rarity.COMMON, attackBonus = 12, floorFound = 1, emoji = "⚔️")
    )

    val stats = mapOf("HP" to 198, "ATK" to 19, "DEF" to 27, "MAG" to 46, "CRIT_DAMAGE" to 11)
    val bonuses = mapOf("HP" to 30, "DEF" to 8, "MAG" to 4)

    PixelTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0512))
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopBannerHeader(heroName = "Tidus")

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Left Column: Stats
                    OrnateStatPanel(
                        stats = stats,
                        bonuses = bonuses,
                        modifier = Modifier.weight(0.28f)
                    )

                    // Middle Column: Hero & Equipment
                    HeroPlatformArea(
                        heroClass = HeroClass.WARRIOR,
                        equipped = sampleItems,
                        onQuickEquip = {},
                        modifier = Modifier.weight(0.44f)
                    )

                    // Right Column: Inventory Chest
                    InventoryChestPanel(
                        inventory = sampleItems,
                        modifier = Modifier.weight(0.28f)
                    )
                }
            }
        }
    }
}
