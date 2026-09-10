package com.game.dungeon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var selectedItemForDetail by remember { mutableStateOf<Item?>(null) }

    LaunchedEffect(heroId) {
        viewModel.selectHero(heroId)
    }

    PixelTheme {
        Box(Modifier.fillMaxSize().background(BgDarkest)) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                // Top Bar
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    PixelButton("◀ BACK", onClick = onBack)
                    Text("EQUIPMENT: ${hero?.name ?: ""}", style = PixelHeading, color = GoldBright)
                    MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
                }

                Spacer(Modifier.height(16.dp))

                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Left Column: Hero Stats & Equipped Slots
                    Column(Modifier.weight(0.4f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        hero?.let { h ->
                            StatPanel(h, equipped)
                            EquippedPanel(
                                equipped = equipped,
                                onQuickEquip = { viewModel.quickEquip() },
                                onSelect = { selectedItemForDetail = it }
                            )
                        }
                    }

                    // Right Column: Inventory
                    Column(Modifier.weight(0.6f)) {
                        Text("INVENTORY", style = PixelHeading, color = GoldBright)
                        Spacer(Modifier.height(8.dp))
                        InventoryPanel(inventory, onSelect = { selectedItemForDetail = it })
                    }
                }
            }

            // Item Detail Overlay
            selectedItemForDetail?.let { item ->
                ItemDetailOverlay(
                    item = item,
                    isEquipped = equipped.any { it.id == item.id },
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
fun StatPanel(hero: Hero, equipped: List<Item>) {
    val baseStats = hero.calculateStats(emptyList())
    val currentStats = hero.calculateStats(equipped)

    PixelPanel(Modifier.fillMaxWidth(), borderColor = GoldDark) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("STATS", style = PixelHeading, color = GoldBright)
            listOf("HP", "ATK", "DEF", "MAG").forEach { stat ->
                val base = baseStats[stat] ?: 0
                val curr = currentStats[stat] ?: 0
                val diff = curr - base
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stat, style = PixelBody, color = Color.White)
                    Row {
                        Text("$curr", style = PixelBody, color = GoldBright)
                        if (diff > 0) {
                            Text(" (+$diff)", style = PixelSmall, color = HpGreen)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EquippedPanel(equipped: List<Item>, onQuickEquip: () -> Unit, onSelect: (Item) -> Unit) {
    PixelPanel(Modifier.fillMaxWidth(), borderColor = GoldDark) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("EQUIPPED", style = PixelHeading, color = GoldBright)
                PixelButton(
                    label = "QUICK EQUIP",
                    onClick = onQuickEquip,
                    modifier = Modifier.height(28.dp),
                    horizontalPadding = 8.dp
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ItemSlot.entries.forEach { slot ->
                    val itemsInSlot = equipped.filter { it.slot == slot }
                    val slotCount = if (slot == ItemSlot.ACCESSORY) 2 else 1
                    
                    items(slotCount) { index ->
                        val item = itemsInSlot.getOrNull(index)
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(BgMedium)
                                .border(1.dp, if (item != null) Color(item.rarity.color) else StoneGray)
                                .clickable { item?.let { onSelect(it) } }
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (item != null) item.emoji else "➕", fontSize = 18.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = item?.name ?: "${slot.name} SLOT",
                                style = PixelBody,
                                color = if (item != null) Color.White else StoneGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryPanel(inventory: List<Item>, onSelect: (Item) -> Unit) {
    PixelPanel(Modifier.fillMaxSize(), borderColor = GoldDark) {
        if (inventory.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("EMPTY", style = PixelBody, color = StoneGray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(inventory) { item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(BgMedium)
                            .border(1.dp, Color(item.rarity.color))
                            .clickable { onSelect(item) }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.emoji, fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(item.name, style = PixelBody, color = Color.White)
                            Text("Lvl ${item.floorFound} ${item.rarity.name}", style = PixelSmall, color = StoneGray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemDetailOverlay(
    item: Item,
    isEquipped: Boolean,
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
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(item.emoji, fontSize = 48.sp)
                Text(item.name, style = PixelHeading, color = Color(item.rarity.color))
                
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (item.attackBonus > 0) Text("ATK: +${item.attackBonus}", style = PixelBody, color = GoldBright)
                    if (item.defenseBonus > 0) Text("DEF: +${item.defenseBonus}", style = PixelBody, color = GoldBright)
                    if (item.magicBonus > 0) Text("MAG: +${item.magicBonus}", style = PixelBody, color = GoldBright)
                    if (item.hpBonus > 0) Text("HP: +${item.hpBonus}", style = PixelBody, color = GoldBright)
                }

                Spacer(Modifier.height(8.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isEquipped) {
                        PixelButton("UNEQUIP", onClick = onUnequip, modifier = Modifier.weight(1f))
                    } else {
                        PixelButton("EQUIP", onClick = onEquip, modifier = Modifier.weight(1f))
                    }
                    PixelButton("SELL (${item.sellValue}G)", onClick = onSell, modifier = Modifier.weight(1f), active = true)
                }
                
                PixelButton("CLOSE", onClick = onClose, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
