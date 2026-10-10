package com.game.dungeon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.game.dungeon.R
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.models.MaterialCatalog
import com.game.dungeon.data.models.MaterialCategory
import com.game.dungeon.data.models.MaterialInventoryItem
import com.game.dungeon.data.models.Rarity
import com.game.dungeon.ui.components.GoldenBorderBox
import com.game.dungeon.ui.components.MusicToggleButton
import com.game.dungeon.ui.components.PixelButton
import com.game.dungeon.ui.components.SupportDialog
import com.game.dungeon.ui.components.SupportIconButton
import com.game.dungeon.ui.components.safeStringResource
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.CraftingTab
import com.game.dungeon.ui.viewmodels.CraftingUiState
import com.game.dungeon.ui.viewmodels.CraftingViewModel

@Composable
fun CraftingScreen(
    viewModel: CraftingViewModel = hiltViewModel(),
    isMuted: Boolean = false,
    onToggleMusic: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val roster by viewModel.roster.collectAsStateWithLifecycle()
    var showSupportDialog by remember { mutableStateOf(false) }

    PixelTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDarkest)
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Screen Header
                CraftingHeader(
                    onBack = onBack,
                    isMuted = isMuted,
                    onToggleMusic = onToggleMusic,
                    onOpenSupport = { showSupportDialog = true }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tab Bar
                CraftingTabBar(
                    selectedTab = uiState.selectedTab,
                    onSelectTab = { viewModel.selectTab(it) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // User Message / Error Banner
                uiState.userMessageRes?.let { msgRes ->
                    UserMessageBanner(
                        messageRes = msgRes,
                        onDismiss = { viewModel.clearUserMessage() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Main Tab Content Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(BgDark, RoundedCornerShape(8.dp))
                        .border(1.dp, GoldDark, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    when (uiState.selectedTab) {
                        CraftingTab.ENHANCE -> EnhanceSection(
                            uiState = uiState,
                            onSelectItem = { viewModel.selectItem(it) },
                            onEnhance = { viewModel.enhanceSelectedItem() }
                        )
                        CraftingTab.SOCKET -> SocketSection(
                            uiState = uiState,
                            onSelectItem = { viewModel.selectItem(it) },
                            onAddSocket = { viewModel.addSocketToSelectedItem() },
                            onInsertGem = { index, gemId -> viewModel.insertGemIntoSelectedItem(index, gemId) }
                        )
                        CraftingTab.GEM_FUSION -> GemFusionSection(
                            materialInventory = uiState.materialInventory,
                            isProcessing = uiState.isProcessing,
                            onFuse = { srcId, tgtId -> viewModel.craftOrFuseGem(srcId, tgtId) }
                        )
                        CraftingTab.BREAKTHROUGH -> BreakthroughSection(
                            roster = roster,
                            materialInventory = uiState.materialInventory,
                            isProcessing = uiState.isProcessing,
                            onUncap = { viewModel.uncapHero(it) }
                        )
                    }
                }
            }

            if (showSupportDialog) {
                SupportDialog(onDismiss = { showSupportDialog = false })
            }
        }
    }
}

@Composable
private fun CraftingHeader(
    onBack: () -> Unit,
    isMuted: Boolean,
    onToggleMusic: () -> Unit,
    onOpenSupport: () -> Unit
) {
    GoldenBorderBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
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
                modifier = Modifier.height(34.dp)
            )

            Text(
                text = safeStringResource(R.string.crafting_title),
                style = PixelHeading
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SupportIconButton(onClick = onOpenSupport)
                MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
            }
        }
    }
}

@Composable
private fun CraftingTabBar(
    selectedTab: CraftingTab,
    onSelectTab: (CraftingTab) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(CraftingTab.entries.toTypedArray()) { tab ->
            val isSelected = tab == selectedTab
            val titleRes = when (tab) {
                CraftingTab.ENHANCE -> R.string.craft_tab_enhance
                CraftingTab.SOCKET -> R.string.craft_tab_socket
                CraftingTab.GEM_FUSION -> R.string.craft_tab_gem_fusion
                CraftingTab.BREAKTHROUGH -> R.string.craft_tab_breakthrough
            }
            Box(
                modifier = Modifier
                    .background(
                        if (isSelected) GoldBright else BgDark,
                        RoundedCornerShape(6.dp)
                    )
                    .border(
                        1.dp,
                        if (isSelected) GoldBright else GoldDark,
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onSelectTab(tab) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(titleRes),
                    color = if (isSelected) BgDarkest else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun UserMessageBanner(
    messageRes: Int,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgMedium, RoundedCornerShape(6.dp))
            .border(1.dp, GoldBright, RoundedCornerShape(6.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(messageRes),
            color = Color.White,
            style = PixelBody,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onDismiss) {
            Text(
                text = stringResource(R.string.btn_ok),
                color = GoldBright
            )
        }
    }
}

@Composable
private fun ItemListSelector(
    itemsList: List<Item>,
    selectedItem: Item?,
    onSelectItem: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.craft_label_select_item),
            style = PixelHeading,
            color = GoldBright,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        if (itemsList.isEmpty()) {
            Text(
                text = stringResource(R.string.craft_label_no_items),
                style = PixelSmall,
                color = Color.Gray
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                items(itemsList) { item ->
                    val isSelected = selectedItem?.id == item.id
                    val rarityColor = when (item.rarity) {
                        Rarity.COMMON -> RarityCommon
                        Rarity.RARE -> RarityRare
                        Rarity.EPIC -> RarityEpic
                        Rarity.LEGENDARY -> RarityLegendary
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) BgMedium else BgPanel,
                                RoundedCornerShape(4.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) GoldBright else GoldDark,
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { onSelectItem(item) }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.item_name_template, item.emoji, item.name),
                                color = rarityColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            if (item.ownerId != null) {
                                Text(
                                    text = stringResource(R.string.craft_format_equipped_tag),
                                    color = GoldAccent,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.craft_format_level, item.enhancementLevel),
                            color = GoldBright,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhanceSection(
    uiState: CraftingUiState,
    onSelectItem: (Item) -> Unit,
    onEnhance: () -> Unit
) {
    val allItems = remember(uiState.inventoryItems, uiState.equippedItems) {
        uiState.inventoryItems + uiState.equippedItems
    }
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ItemListSelector(
            itemsList = allItems,
            selectedItem = uiState.selectedItem,
            onSelectItem = onSelectItem,
            modifier = Modifier.weight(0.4f)
        )

        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val item = uiState.selectedItem
            if (item == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.craft_label_select_item),
                        style = PixelBody,
                        color = Color.Gray
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.item_name_template, item.emoji, item.name),
                        style = PixelHeading,
                        color = GoldBright
                    )

                    Text(
                        text = stringResource(R.string.craft_label_stat_preview),
                        style = PixelSmall,
                        color = GoldAccent
                    )

                    val nextLevel = item.enhancementLevel + 1
                    val currentMult = 1.0 + item.enhancementLevel.coerceIn(0, 10) * 0.05
                    val nextMult = 1.0 + nextLevel.coerceIn(0, 10) * 0.05

                    if (item.attackBonus > 0) {
                        val currAtk = (item.attackBonus * currentMult).toInt()
                        val nextAtk = (item.attackBonus * nextMult).toInt()
                        Text(
                            text = stringResource(R.string.craft_format_stat, stringResource(R.string.stat_attack), currAtk, nextAtk),
                            style = PixelSmall,
                            color = Color.White
                        )
                    }
                    if (item.defenseBonus > 0) {
                        val currDef = (item.defenseBonus * currentMult).toInt()
                        val nextDef = (item.defenseBonus * nextMult).toInt()
                        Text(
                            text = stringResource(R.string.craft_format_stat, stringResource(R.string.stat_defense), currDef, nextDef),
                            style = PixelSmall,
                            color = Color.White
                        )
                    }
                    if (item.magicBonus > 0) {
                        val currMag = (item.magicBonus * currentMult).toInt()
                        val nextMag = (item.magicBonus * nextMult).toInt()
                        Text(
                            text = stringResource(R.string.craft_format_stat, stringResource(R.string.stat_magic), currMag, nextMag),
                            style = PixelSmall,
                            color = Color.White
                        )
                    }
                    if (item.hpBonus > 0) {
                        val currHp = (item.hpBonus * currentMult).toInt()
                        val nextHp = (item.hpBonus * nextMult).toInt()
                        Text(
                            text = stringResource(R.string.craft_format_stat, stringResource(R.string.stat_hp), currHp, nextHp),
                            style = PixelSmall,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (item.enhancementLevel < 10) {
                        val goldCost = (nextLevel * 100L * item.floorFound.coerceAtLeast(1)).coerceAtLeast(100L)
                        val oreRes = when (item.rarity) {
                            Rarity.COMMON -> R.string.mat_iron_ore_name
                            Rarity.RARE -> R.string.mat_mithril_ore_name
                            Rarity.EPIC -> R.string.mat_adamantite_ore_name
                            Rarity.LEGENDARY -> R.string.mat_orichalcum_ore_name
                        }
                        Text(
                            text = stringResource(R.string.craft_label_cost),
                            style = PixelSmall,
                            color = GoldBright
                        )
                        Text(
                            text = stringResource(R.string.craft_format_gold, goldCost),
                            style = PixelSmall,
                            color = Color.Yellow
                        )
                        Text(
                            text = stringResource(R.string.item_name_template, stringResource(oreRes), stringResource(R.string.craft_format_quantity, nextLevel)),
                            style = PixelSmall,
                            color = Color.Cyan
                        )
                    }
                }

                Button(
                    onClick = onEnhance,
                    enabled = !uiState.isProcessing && item.enhancementLevel < 10,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    val btnText = if (item.enhancementLevel >= 10) {
                        stringResource(R.string.craft_label_max_level_reached)
                    } else {
                        stringResource(R.string.craft_btn_enhance, item.enhancementLevel + 1)
                    }
                    Text(
                        text = btnText,
                        color = BgDarkest,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SocketSection(
    uiState: CraftingUiState,
    onSelectItem: (Item) -> Unit,
    onAddSocket: () -> Unit,
    onInsertGem: (Int, String) -> Unit
) {
    val allItems = remember(uiState.inventoryItems, uiState.equippedItems) {
        uiState.inventoryItems + uiState.equippedItems
    }
    var selectedSocketIndex by remember { mutableIntStateOf(0) }
    var selectedGemId by remember { mutableStateOf<String?>(null) }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ItemListSelector(
            itemsList = allItems,
            selectedItem = uiState.selectedItem,
            onSelectItem = onSelectItem,
            modifier = Modifier.weight(0.35f)
        )

        Column(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val item = uiState.selectedItem
            if (item == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.craft_label_select_item),
                        style = PixelBody,
                        color = Color.Gray
                    )
                }
            } else {
                val maxSockets = if (item.slot == ItemSlot.ACCESSORY) 1 else 3
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.item_name_template, item.emoji, item.name),
                        style = PixelHeading,
                        color = GoldBright
                    )

                    Text(
                        text = stringResource(R.string.craft_label_sockets),
                        style = PixelSmall,
                        color = GoldAccent
                    )

                    // Display Socket Slots
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (i in 0 until maxSockets) {
                            val slot = item.sockets.getOrNull(i)
                            val isSlotUnlocked = i < item.sockets.size
                            val isSelectedSlot = i == selectedSocketIndex

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isSelectedSlot) BgMedium else BgPanel,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelectedSlot) GoldBright else GoldDark,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable(enabled = isSlotUnlocked) { selectedSocketIndex = i }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (!isSlotUnlocked) {
                                    Text(
                                        text = stringResource(R.string.craft_slot_locked, i + 1),
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                } else if (slot?.socketedGem != null) {
                                    val gem = slot.socketedGem
                                    Text(
                                        text = stringResource(R.string.item_name_template, gem.emoji, stringResource(gem.nameRes)),
                                        color = GoldBright,
                                        fontSize = 11.sp
                                    )
                                } else {
                                    Text(
                                        text = stringResource(R.string.craft_label_empty_slot),
                                        color = Color.LightGray,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Gems selection for inserting
                    Text(
                        text = stringResource(R.string.craft_label_owned_gems),
                        style = PixelSmall,
                        color = GoldAccent
                    )

                    val gemMaterials = remember(uiState.materials) {
                        uiState.materials.filter {
                            it.category == MaterialCategory.ESSENCE ||
                                    it.attackBonus > 0 || it.defenseBonus > 0 ||
                                    it.magicBonus > 0 || it.hpBonus > 0
                        }.distinctBy { it.id }
                    }

                    if (gemMaterials.isEmpty()) {
                        Text(
                            text = stringResource(R.string.craft_label_no_gems),
                            style = PixelSmall,
                            color = Color.Gray
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(gemMaterials) { gem ->
                                val isSelected = selectedGemId == gem.id
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) GoldBright else BgPanel,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) GoldBright else GoldDark,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .clickable { selectedGemId = gem.id }
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.item_name_template, gem.emoji, stringResource(gem.nameRes)),
                                        color = if (isSelected) BgDarkest else Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAddSocket,
                        enabled = !uiState.isProcessing && item.sockets.size < maxSockets,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = StoneGray)
                    ) {
                        Text(
                            text = stringResource(R.string.craft_btn_add_socket),
                            fontSize = 10.sp,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            selectedGemId?.let { gemId ->
                                onInsertGem(selectedSocketIndex, gemId)
                            }
                        },
                        enabled = !uiState.isProcessing && selectedGemId != null && selectedSocketIndex < item.sockets.size,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text(
                            text = stringResource(R.string.craft_btn_insert_gem),
                            fontSize = 10.sp,
                            color = BgDarkest,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GemFusionSection(
    materialInventory: List<MaterialInventoryItem>,
    isProcessing: Boolean,
    onFuse: (String, String) -> Unit
) {
    val gemCounts = remember(materialInventory) {
        materialInventory.associate { item -> item.material.id to item.amount }
    }

    val fusionPairs = remember {
        listOf(
            "ruby_gem_1" to "ruby_gem_2",
            "ruby_gem_2" to "ruby_gem_3",
            "sapphire_gem_1" to "sapphire_gem_2",
            "sapphire_gem_2" to "sapphire_gem_3",
            "emerald_gem_1" to "emerald_gem_2",
            "emerald_gem_2" to "emerald_gem_3",
            "topaz_gem_1" to "topaz_gem_2",
            "topaz_gem_2" to "topaz_gem_3"
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.craft_tab_gem_fusion),
            style = PixelHeading,
            color = GoldBright,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(fusionPairs) { (srcId, tgtId) ->
                val srcMat = MaterialCatalog.getMaterial(srcId)
                val tgtMat = MaterialCatalog.getMaterial(tgtId)
                val count = gemCounts[srcId] ?: 0
                val canFuse = count >= 3

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgPanel, RoundedCornerShape(6.dp))
                        .border(1.dp, GoldDark, RoundedCornerShape(6.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "${srcMat.emoji} ${stringResource(srcMat.nameRes)} ➔ ${tgtMat.emoji} ${stringResource(tgtMat.nameRes)}",
                            style = PixelBody,
                            color = Color.White
                        )
                        Text(
                            text = stringResource(R.string.craft_socket_ratio, count),
                            style = PixelSmall,
                            color = if (canFuse) HpGreen else Color.Gray
                        )
                    }

                    Button(
                        onClick = { onFuse(srcId, tgtId) },
                        enabled = !isProcessing && canFuse,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text(
                            text = stringResource(R.string.craft_btn_fuse_gems),
                            color = BgDarkest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakthroughSection(
    roster: List<Hero>,
    materialInventory: List<MaterialInventoryItem>,
    isProcessing: Boolean,
    onUncap: (String) -> Unit
) {
    var selectedHeroId by remember { mutableStateOf<String?>(roster.firstOrNull()?.id) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = safeStringResource(R.string.craft_label_hero_breakthrough),
            style = PixelHeading,
            color = GoldBright,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(0.4f)
            ) {
                items(roster) { hero ->
                    val isSelected = selectedHeroId == hero.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) BgMedium else BgPanel,
                                RoundedCornerShape(4.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) GoldBright else GoldDark,
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedHeroId = hero.id }
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = hero.name,
                                color = GoldBright,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = safeStringResource(R.string.hero_level_normal, hero.level, hero.maxLevel),
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Breakthrough Action Panel
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val selectedHero = roster.find { it.id == selectedHeroId } ?: roster.firstOrNull()
                if (selectedHero == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = safeStringResource(R.string.craft_label_select_item),
                            style = PixelBody,
                            color = Color.Gray
                        )
                    }
                } else {
                    val isAtLevelCap = selectedHero.level >= selectedHero.maxLevel
                    val requiredTrophyId = when {
                        selectedHero.maxLevel <= 20 -> "boss_trophy_1"
                        selectedHero.maxLevel <= 40 -> "boss_trophy_2"
                        else -> "boss_trophy_3"
                    }
                    val trophyMat = MaterialCatalog.getMaterial(requiredTrophyId)
                    val trophyCount = materialInventory.find { it.material.id == requiredTrophyId }?.amount ?: 0

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = selectedHero.name,
                            style = PixelHeading,
                            color = GoldBright
                        )
                        Text(
                            text = safeStringResource(R.string.hero_level_normal, selectedHero.level, selectedHero.maxLevel),
                            style = PixelBody,
                            color = Color.White
                        )

                        Text(
                            text = if (isAtLevelCap) {
                                safeStringResource(R.string.craft_status_ready_for_breakthrough, selectedHero.maxLevel)
                            } else {
                                safeStringResource(R.string.craft_status_reach_cap_to_uncap, selectedHero.maxLevel)
                            },
                            style = PixelSmall,
                            color = if (isAtLevelCap) HpGreen else GoldAccent
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = safeStringResource(R.string.craft_label_required_materials),
                            style = PixelSmall,
                            color = GoldAccent
                        )
                        Text(
                            text = safeStringResource(
                                R.string.item_name_template,
                                "${trophyMat.emoji} ${safeStringResource(trophyMat.nameRes)}",
                                safeStringResource(R.string.craft_trophy_ratio, trophyCount)
                            ),
                            style = PixelBody,
                            color = if (trophyCount >= 1) HpGreen else HpRed
                        )
                    }

                    Button(
                        onClick = { onUncap(selectedHero.id) },
                        enabled = !isProcessing && isAtLevelCap && trophyCount >= 1,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text(
                            text = safeStringResource(R.string.craft_btn_uncap_level),
                            color = BgDarkest,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
