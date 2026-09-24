package com.game.dungeon.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.dungeon.R
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.ui.components.*
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.EquipmentViewModel

@Composable
fun EquipmentScreen(
    heroId: String,
    viewModel: EquipmentViewModel,
    isMuted: Boolean,
    onToggleMusic: () -> Unit,
    onBack: () -> Unit
) {
    val hero by viewModel.selectedHero.collectAsState()
    val equipped by viewModel.equippedItems.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    val relicBonuses by viewModel.relicBonuses.collectAsState()

    var selectedItemForDetail by remember { mutableStateOf<Item?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "equipmentAnim")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100000f,
        animationSpec = infiniteRepeatable(
            animation = tween(100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animTime"
    )

    LaunchedEffect(heroId) {
        viewModel.selectHero(heroId)
    }

    PixelTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0512))
                .padding(8.dp)
        ) {
            Column(Modifier.fillMaxSize()) {
                // Top Header Banner
                TopBannerHeader(
                    heroName = hero?.name ?: "",
                    isMuted = isMuted,
                    onToggleMusic = onToggleMusic,
                    onBack = onBack
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Left Column: Ornate Stat Panel
                    hero?.let { h ->
                        OrnateStatPanel(
                            hero = h,
                            equipped = equipped,
                            relicBonuses = relicBonuses,
                            modifier = Modifier.weight(0.28f)
                        )
                    } ?: Spacer(Modifier.weight(0.28f))

                    // Middle Column: Hero & Equipment Slots
                    hero?.let { h ->
                        HeroPlatformArea(
                            hero = h,
                            equipped = equipped,
                            onQuickEquip = { viewModel.quickEquip() },
                            onSelectSlot = { selectedItemForDetail = it },
                            animTime = animTime,
                            modifier = Modifier.weight(0.44f)
                        )
                    } ?: Spacer(Modifier.weight(0.44f))

                    // Right Column: Inventory Chest
                    InventoryChestPanel(
                        inventory = inventory,
                        onSelectItem = { selectedItemForDetail = it },
                        animTime = animTime,
                        modifier = Modifier.weight(0.28f)
                    )
                }
            }

            // Item Detail Modal Overlay
            selectedItemForDetail?.let { item ->
                ItemDetailOverlay(
                    item = item,
                    isEquipped = equipped.any { it.id == item.id },
                    animTime = animTime,
                    onEquip = {
                        viewModel.equipItem(item)
                        selectedItemForDetail = null
                    },
                    onUnequip = {
                        viewModel.unequipItem(item)
                        selectedItemForDetail = null
                    },
                    onSell = {
                        viewModel.sellItem(item)
                        selectedItemForDetail = null
                    },
                    onClose = { selectedItemForDetail = null }
                )
            }
        }
    }
}

@Composable
private fun TopBannerHeader(
    heroName: String,
    isMuted: Boolean,
    onToggleMusic: () -> Unit,
    onBack: () -> Unit
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
private fun OrnateStatPanel(
    hero: Hero,
    equipped: List<Item>,
    relicBonuses: com.game.dungeon.data.models.RelicBonuses?,
    modifier: Modifier = Modifier
) {
    val baseOnly = hero.calculateStats(emptyList(), null)
    val totalStats = hero.calculateStats(equipped, relicBonuses)

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
            items(statRows) { row ->
                val (icon, resId, key) = row
                val label = safeStringResource(resId)
                val total = totalStats[key] ?: 0
                val base = baseOnly[key] ?: 0
                val bonus = total - base
                val isPercent = key == "CRIT_CHANCE" || key == "CRIT_DAMAGE"
                val valueStr = if (isPercent) "$total%" else total.toString()
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
        }
    }
}

@Composable
private fun HeroPlatformArea(
    hero: Hero,
    equipped: List<Item>,
    onQuickEquip: () -> Unit,
    onSelectSlot: (Item) -> Unit,
    modifier: Modifier = Modifier,
    animTime: Float = 0f
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
        // Starry particles & Stone walkway Canvas
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

        // Hero Sprite & Equipment Slots
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeroSprite(
                heroClass = hero.heroClass,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 5 Equipment Slots
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
                                )
                                .clickable { item?.let { onSelectSlot(it) } },
                            contentAlignment = Alignment.Center
                        ) {
                            EquipmentSprite(
                                item = item,
                                slot = slotType,
                                modifier = Modifier.size(28.dp),
                                animTime = animTime
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
private fun InventoryChestPanel(
    inventory: List<Item>,
    onSelectItem: (Item) -> Unit,
    modifier: Modifier = Modifier,
    animTime: Float = 0f
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
                Text(
                    text = "🔒 " + safeStringResource(R.string.inventory_chest_title),
                    color = ChestWoodDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(6.dp))

            // Inventory List
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
                            .clickable { onSelectItem(item) }
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EquipmentSprite(
                            item = item,
                            slot = item.slot,
                            modifier = Modifier.size(24.dp),
                            animTime = animTime
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

            // Chest Footer Inventory Count
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .background(Color(0xFF100804), RoundedCornerShape(2.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = safeStringResource(R.string.inventory_count_format, inventory.size),
                    color = Color(0xFFCCAA88),
                    fontSize = 9.sp
                )
            }
        }
    }
}

private data class StatBonusDisplay(val resId: Int, val amount: Int, val isPercent: Boolean, val color: Color)

@Composable
private fun ItemStatBonusList(item: Item) {
    val statList = remember(item) { getItemStatBonuses(item) }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        statList.forEach { stat ->
            val label = safeStringResource(stat.resId)
            val suffix = if (stat.isPercent) "%" else ""
            Text("$label: +${stat.amount}$suffix", style = PixelBody, color = stat.color)
        }
    }
}

private fun getItemStatBonuses(item: Item): List<StatBonusDisplay> = buildList {
    if (item.attackBonus > 0) add(StatBonusDisplay(R.string.stat_attack, item.attackBonus, false, GoldBright))
    if (item.defenseBonus > 0) add(StatBonusDisplay(R.string.stat_defense, item.defenseBonus, false, GoldBright))
    if (item.magicBonus > 0) add(StatBonusDisplay(R.string.stat_magic, item.magicBonus, false, GoldBright))
    if (item.hpBonus > 0) add(StatBonusDisplay(R.string.stat_hp, item.hpBonus, false, GoldBright))
    if (item.critChanceBonus > 0) add(StatBonusDisplay(R.string.stat_crit_chance, item.critChanceBonus, true, HpGreen))
    if (item.critDamageBonus > 0) add(StatBonusDisplay(R.string.stat_crit_damage, item.critDamageBonus, true, HpGreen))
}

@Composable
private fun ItemDetailOverlay(
    item: Item,
    isEquipped: Boolean,
    animTime: Float = 0f,
    onEquip: () -> Unit,
    onUnequip: () -> Unit,
    onSell: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        PixelPanel(
            Modifier
                .width(300.dp)
                .clickable(enabled = false) {},
            borderColor = Color(item.rarity.color)
        ) {
            Column(
                Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EquipmentSprite(
                    item = item,
                    slot = item.slot,
                    modifier = Modifier.size(56.dp),
                    animTime = animTime
                )
                Text(item.name, style = PixelHeading, color = Color(item.rarity.color))

                ItemStatBonusList(item)

                Spacer(Modifier.height(8.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val actionLabel = safeStringResource(
                        if (isEquipped) R.string.unequip_button else R.string.equip_button
                    )
                    val actionClick = if (isEquipped) onUnequip else onEquip

                    PixelButton(actionLabel, onClick = actionClick, modifier = Modifier.weight(1f))
                    PixelButton(safeStringResource(R.string.sell_button_format, item.sellValue), onClick = onSell, modifier = Modifier.weight(1f), active = true)
                }

                PixelButton(safeStringResource(R.string.close_button), onClick = onClose, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
