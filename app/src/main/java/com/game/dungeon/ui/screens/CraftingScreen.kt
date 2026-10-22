package com.game.dungeon.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.game.dungeon.BuildConfig
import com.game.dungeon.R
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.models.MaterialCatalog
import com.game.dungeon.data.models.MaterialCategory
import com.game.dungeon.data.models.MaterialInventoryItem
import com.game.dungeon.data.models.MythicSet
import com.game.dungeon.data.models.Rarity
import com.game.dungeon.data.models.RelicBonuses
import com.game.dungeon.ui.components.EquipmentSprite
import com.game.dungeon.ui.components.GoldenBorderBox
import com.game.dungeon.ui.components.MusicToggleButton
import com.game.dungeon.ui.components.PixelButton
import com.game.dungeon.ui.components.PixelGoldDisplay
import com.game.dungeon.ui.components.SupportDialog
import com.game.dungeon.ui.components.SupportIconButton
import com.game.dungeon.ui.components.safeStringResource
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.CraftingTab
import com.game.dungeon.ui.viewmodels.CraftingUiState
import com.game.dungeon.ui.viewmodels.CraftingViewModel
import com.game.dungeon.ui.components.formatAmount

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

    val snackbarHostState = remember { SnackbarHostState() }

    // 1. Resolve the string outside the LaunchedEffect using Compose's stringResource
    val currentMessageRes = uiState.userMessageRes
    val snackbarMessage = currentMessageRes?.let { stringResource(it) }

    // 2. Trigger the effect using the resource ID as the key
    LaunchedEffect(currentMessageRes) {
        if (snackbarMessage != null) {
            snackbarHostState.showSnackbar(snackbarMessage)
            viewModel.clearUserMessage()
        }
    }

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
                    gold = uiState.gold, // Added gold display support
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
                        CraftingTab.MYTHIC_FORGE -> MythicForgeSection(
                            uiState = uiState,
                            onSelectItem = { viewModel.selectItem(it) },
                            onForgeMythic = { setId, ctx -> viewModel.craftMythicSelectedItem(setId, ctx) }, // UPDATED
                            viewModel = viewModel
                        )
                    }
                }
            }

            // Non-intrusive Snackbar messages layered over the bottom
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            if (showSupportDialog) {
                SupportDialog(onDismiss = { showSupportDialog = false })
            }
        }
    }
}

@Composable
private fun CraftingHeader(
    gold: Long,
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
                // Now shows the player's current Gil balance
                PixelGoldDisplay(amount = gold)

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
                CraftingTab.MYTHIC_FORGE -> R.string.craft_tab_mythic_forge
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
private fun ItemListSelector(
    itemsList: List<Item>,
    selectedItem: Item?,
    onSelectItem: (Item) -> Unit,
    modifier: Modifier = Modifier,
    roster: List<Hero> = emptyList()
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
                        Rarity.MYTHIC -> Color(0xFFFF2255)
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
                                val ownerHero = roster.find { it.id == item.ownerId }
                                val equippedText = if (ownerHero != null) {
                                    safeStringResource(R.string.craft_format_equipped_hero, ownerHero.name)
                                } else {
                                    safeStringResource(R.string.craft_format_equipped_tag)
                                }
                                Text(
                                    text = equippedText,
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
            modifier = Modifier.weight(0.4f),
            roster = uiState.roster
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
                var canAffordGold = false
                var canAffordMaterials = false

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
                    val currentMult = 1.0 + item.enhancementLevel * 0.05
                    val nextMult = 1.0 + nextLevel * 0.05

                    if (item.attackBonus > 0) {
                        val currAtk = (item.attackBonus * currentMult).toLong()
                        val nextAtk = (item.attackBonus * nextMult).toLong()
                        Text(
                            text = stringResource(R.string.craft_format_stat, stringResource(R.string.stat_attack), formatAmount(currAtk), formatAmount(nextAtk)),
                            style = PixelSmall,
                            color = Color.White
                        )
                    }
                    if (item.defenseBonus > 0) {
                        val currDef = (item.defenseBonus * currentMult).toLong()
                        val nextDef = (item.defenseBonus * nextMult).toLong()
                        Text(
                            text = stringResource(R.string.craft_format_stat, stringResource(R.string.stat_defense), formatAmount(currDef), formatAmount(nextDef)),
                            style = PixelSmall,
                            color = Color.White
                        )
                    }
                    if (item.magicBonus > 0) {
                        val currMag = (item.magicBonus * currentMult).toLong()
                        val nextMag = (item.magicBonus * nextMult).toLong()
                        Text(
                            text = stringResource(R.string.craft_format_stat, stringResource(R.string.stat_magic), formatAmount(currMag), formatAmount(nextMag)),
                            style = PixelSmall,
                            color = Color.White
                        )
                    }
                    if (item.hpBonus > 0) {
                        val currHp = (item.hpBonus * currentMult).toLong()
                        val nextHp = (item.hpBonus * nextMult).toLong()
                        Text(
                            text = stringResource(R.string.craft_format_stat, stringResource(R.string.stat_hp), formatAmount(currHp), formatAmount(nextHp)),
                            style = PixelSmall,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val isMythicAscension = nextLevel > 10
                    val isMaxed = item.enhancementLevel >= 10 && item.rarity != Rarity.MYTHIC

                    if (!isMaxed) {
                        val baseGoldCost = (nextLevel * 100L * item.floorFound.coerceAtLeast(1)).coerceAtLeast(100L)
                        val goldCost = if (isMythicAscension) {
                            (baseGoldCost * Math.pow(1.5, (nextLevel - 10).toDouble())).toLong()
                        } else {
                            baseGoldCost
                        }
                        
                        val oreId = if (isMythicAscension) "mythic_shard" else when (item.rarity) {
                            Rarity.COMMON -> "iron_ore"
                            Rarity.RARE -> "mithril_ore"
                            Rarity.EPIC -> "adamantite_ore"
                            Rarity.LEGENDARY, Rarity.MYTHIC -> "orichalcum_ore"
                        }
                        
                        val oreRes = MaterialCatalog.getMaterial(oreId).nameRes

                        val requiredOres = if (isMythicAscension) {
                            (5 * Math.pow(1.5, (nextLevel - 10).toDouble())).toInt()
                        } else {
                            nextLevel
                        }
                        
                        val ownedOres = uiState.materialInventory.find { it.material.id == oreId }?.amount ?: 0

                        canAffordGold = uiState.gold >= goldCost
                        canAffordMaterials = ownedOres >= requiredOres

                        Text(
                            text = stringResource(R.string.craft_label_cost),
                            style = PixelSmall,
                            color = GoldBright
                        )
                        Text(
                            // Formatted Gold Cost
                            text = stringResource(R.string.craft_format_gold, formatAmount(goldCost)),
                            style = PixelSmall,
                            color = if (canAffordGold) Color.Yellow else HpRed
                        )
                        Text(
                            // Formatted Shard/Ore Costs
                            text = stringResource(
                                R.string.item_name_template,
                                stringResource(oreRes),
                                "${formatAmount(requiredOres.toLong())} / ${formatAmount(ownedOres.toLong())}"
                            ),
                            style = PixelSmall,
                            color = if (canAffordMaterials) Color.Cyan else HpRed
                        )
                    }
                }

                val isMaxed = item.enhancementLevel >= 10 && item.rarity != Rarity.MYTHIC
                Button(
                    onClick = onEnhance,
                    enabled = !uiState.isProcessing && !isMaxed && canAffordGold && canAffordMaterials,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        disabledContainerColor = StoneGray
                    )
                ) {
                    val btnText = if (isMaxed) {
                        stringResource(R.string.craft_label_max_level_reached)
                    } else {
                        stringResource(R.string.craft_btn_enhance, item.enhancementLevel + 1)
                    }
                    Text(
                        text = btnText,
                        color = if (isMaxed || !canAffordGold || !canAffordMaterials) Color.LightGray else BgDarkest,
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
            modifier = Modifier.weight(0.35f),
            roster = uiState.roster
        )
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxHeight()
                .verticalScroll(scrollState),
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

                        val selectedGem = uiState.materials.find { it.id == selectedGemId } ?: selectedGemId?.let { MaterialCatalog.getMaterial(it) }
                        if (selectedGem != null) {
                            val effectText = when {
                                selectedGem.attackBonus > 0 -> "+${selectedGem.attackBonus}% ${stringResource(R.string.stat_attack)}"
                                selectedGem.defenseBonus > 0 -> "+${selectedGem.defenseBonus}% ${stringResource(R.string.stat_defense)}"
                                selectedGem.magicBonus > 0 -> "+${selectedGem.magicBonus}% ${stringResource(R.string.stat_magic)}"
                                selectedGem.hpBonus > 0 -> "+${selectedGem.hpBonus}% ${stringResource(R.string.stat_hp)}"
                                else -> ""
                            }
                            if (effectText.isNotEmpty()) {
                                Text(
                                    text = stringResource(R.string.effect, effectText),
                                    style = PixelSmall,
                                    color = GoldAccent,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
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
                        val tgtEffectText = when {
                            tgtMat.attackBonus > 0 -> "+${tgtMat.attackBonus}% ${stringResource(R.string.stat_attack)}"
                            tgtMat.defenseBonus > 0 -> "+${tgtMat.defenseBonus}% ${stringResource(R.string.stat_defense)}"
                            tgtMat.magicBonus > 0 -> "+${tgtMat.magicBonus}% ${stringResource(R.string.stat_magic)}"
                            tgtMat.hpBonus > 0 -> "+${tgtMat.hpBonus}% ${stringResource(R.string.stat_hp)}"
                            else -> ""
                        }
                        if (tgtEffectText.isNotEmpty()) {
                            Text(
                                text = "Result: $tgtEffectText",
                                style = PixelSmall,
                                color = GoldAccent
                            )
                        }
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

@Composable
private fun MythicForgeSection(
    uiState: CraftingUiState,
    onSelectItem: (Item) -> Unit,
    onForgeMythic: (String, Context) -> Unit,
    viewModel: CraftingViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val allItems = remember(uiState.inventoryItems, uiState.equippedItems) {
        uiState.inventoryItems + uiState.equippedItems
    }
    val eligibleItems = remember(allItems) {
        allItems.filter { it.rarity == Rarity.LEGENDARY && it.enhancementLevel >= 10 }
    }

    val selectedItem = uiState.selectedItem
    val mythicShards = remember(uiState.materialInventory) { uiState.materialInventory.find { it.material.id == "mythic_shard" }?.amount ?: 0 }
    val bossTrophies = remember(uiState.materialInventory) { uiState.materialInventory.find { it.material.id == "boss_trophy_3" }?.amount ?: 0 }

    val shardMat = MaterialCatalog.getMaterial("mythic_shard")
    val trophyMat = MaterialCatalog.getMaterial("boss_trophy_3")

    val hasItem = selectedItem != null && selectedItem.rarity == Rarity.LEGENDARY && selectedItem.enhancementLevel >= 10
    val hasShards = mythicShards >= 50
    val hasTrophies = bossTrophies >= 5
    val canForge = hasItem && hasShards && hasTrophies

    // --- RESTRICT SETS BASED ON SELECTED ITEM SLOT ---
    val validSets = remember(selectedItem) {
        if (selectedItem == null) MythicSet.entries
        else MythicSet.entries.filter { selectedItem.slot in it.allowedSlots }
    }

    var previewSetIndex by remember { mutableIntStateOf(0) }

    // Reset index safely if list size changes
    LaunchedEffect(validSets) {
        if (previewSetIndex >= validSets.size) {
            previewSetIndex = 0
        }
    }

    val currentSet = validSets.getOrNull(previewSetIndex) ?: validSets.firstOrNull()

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ItemListSelector(
            itemsList = eligibleItems,
            selectedItem = selectedItem,
            onSelectItem = onSelectItem,
            modifier = Modifier.weight(0.4f),
            roster = uiState.roster
        )

        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (BuildConfig.DEBUG) {
                Button(
                    onClick = { viewModel.triggerDebugMythicSeed(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(safeStringResource(R.string.debug_seed_mythic))
                }
            }

            if (selectedItem != null && currentSet != null) {
                val setNameText = stringResource(currentSet.nameRes)
                val previewItem = remember(selectedItem.id, currentSet.id, uiState.gameState, setNameText) {
                    val craftFloor = maxOf(1000, uiState.gameState?.lifetimeHighestFloor ?: 1000)
                    val dimension = uiState.gameState?.currentDimension ?: 1
                    val relicBonuses = uiState.gameState?.let { RelicBonuses.from(it) }

                    Item.random(
                        floor = craftFloor,
                        context = context,
                        relicBonuses = relicBonuses,
                        minRarity = Rarity.MYTHIC,
                        dimension = dimension,
                        forcedSlot = selectedItem.slot
                    ).copy(
                        setId = currentSet.id,
                        name = setNameText
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = safeStringResource(R.string.craft_preview_title), style = PixelSmall, color = GoldAccent)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        PixelButton(label = " < ", onClick = {
                            previewSetIndex = if (previewSetIndex > 0) previewSetIndex - 1 else validSets.size - 1
                        })

                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .background(BgDarkest, RoundedCornerShape(8.dp))
                                .border(1.dp, Color(Rarity.MYTHIC.color), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            EquipmentSprite(
                                item = previewItem,
                                slot = selectedItem.slot,
                                modifier = Modifier.size(50.dp)
                            )
                        }

                        PixelButton(label = " > ", onClick = {
                            previewSetIndex = (previewSetIndex + 1) % validSets.size
                        })
                    }

                    Text(
                        text = "$setNameText - ${safeStringResource(currentSet.targetClass.nameRes)}",
                        style = PixelHeading,
                        color = Color(Rarity.MYTHIC.color)
                    )
                    Text(
                        text = safeStringResource(R.string.craft_preview_class_slot, safeStringResource(currentSet.targetClass.nameRes), safeStringResource(selectedItem.slot.nameRes)),
                        style = PixelSmall,
                        color = Color.White
                    )
                }

                // STATS PREVIEW BOX
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgDarkest, RoundedCornerShape(6.dp))
                        .border(1.dp, GoldDark, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = safeStringResource(R.string.craft_label_stats_preview),
                            style = PixelSmall,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )

                        val selAtk = selectedItem.effectiveAttackBonus.toLong()
                        val preAtk = previewItem.attackBonus
                        val dAtk = preAtk - selAtk

                        val selDef = selectedItem.effectiveDefenseBonus.toLong()
                        val preDef = previewItem.defenseBonus
                        val dDef = preDef - selDef

                        val selMag = selectedItem.effectiveMagicBonus.toLong()
                        val preMag = previewItem.magicBonus
                        val dMag = preMag - selMag

                        val selHp = selectedItem.effectiveHpBonus.toLong()
                        val preHp = previewItem.hpBonus
                        val dHp = preHp - selHp

                        val selMp = selectedItem.mpBonus
                        val preMp = previewItem.mpBonus
                        val dMp = preMp - selMp

                        val selCritC = selectedItem.critChanceBonus
                        val preCritC = previewItem.critChanceBonus
                        val dCritC = preCritC - selCritC

                        val selCritD = selectedItem.critDamageBonus
                        val preCritD = previewItem.critDamageBonus
                        val dCritD = preCritD - selCritD

                        if (preAtk > 0 || selAtk > 0) {
                            StatPreviewRow(label = safeStringResource(R.string.stat_attack), value = preAtk, delta = dAtk)
                        }
                        if (preDef > 0 || selDef > 0) {
                            StatPreviewRow(label = safeStringResource(R.string.stat_defense), value = preDef, delta = dDef)
                        }
                        if (preMag > 0 || selMag > 0) {
                            StatPreviewRow(label = safeStringResource(R.string.stat_magic), value = preMag, delta = dMag)
                        }
                        if (preHp > 0 || selHp > 0) {
                            StatPreviewRow(label = safeStringResource(R.string.stat_hp), value = preHp, delta = dHp)
                        }
                        if (preMp > 0 || selMp > 0) {
                            StatPreviewRow(label = safeStringResource(R.string.stat_mp), value = preMp, delta = dMp)
                        }
                        if (preCritC > 0 || selCritC > 0) {
                            StatPreviewRow(label = safeStringResource(R.string.stat_crit_chance), value = preCritC.toLong(), delta = dCritC.toLong(), isPercentage = true)
                        }
                        if (preCritD > 0 || selCritD > 0) {
                            StatPreviewRow(label = safeStringResource(R.string.stat_crit_damage), value = preCritD, delta = dCritD, isPercentage = true)
                        }
                    }
                }

                // MYTHIC SET BONUS BOX
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E0A2A), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(Rarity.MYTHIC.color), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = safeStringResource(R.string.craft_set_bonus_pieces, setNameText, currentSet.requiredPieces),
                            style = PixelBody,
                            color = Color(Rarity.MYTHIC.color),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = safeStringResource(R.string.craft_requires_class, safeStringResource(currentSet.targetClass.nameRes)),
                            style = PixelSmall,
                            color = GoldAccent
                        )

                        val statsText = currentSet.statMultipliers.entries.joinToString(" | ") { (key, value) ->
                            val formattedVal = if (key.endsWith("_MULT")) {
                                "+${(value * 100).toInt()}%"
                            } else {
                                "+${formatAmount(value.toLong())}"
                            }
                            val statRes = when {
                                key.startsWith("ATK") -> R.string.stat_attack
                                key.startsWith("DEF") -> R.string.stat_defense
                                key.startsWith("MAG") -> R.string.stat_magic
                                key.startsWith("HP") -> R.string.stat_hp
                                key.startsWith("MP") -> R.string.stat_mp
                                key.startsWith("CRIT_CHANCE") -> R.string.stat_crit_chance
                                key.startsWith("CRIT_DMG") -> R.string.stat_crit_damage
                                else -> 0
                            }
                            val statName = if (statRes != 0) context.getString(statRes) else key.replace("_MULT", "").replace("_ADD", "")
                            context.getString(R.string.craft_stat_total_format, formattedVal, statName)
                        }

                        Text(
                            text = statsText,
                            style = PixelSmall,
                            color = Color.White
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = safeStringResource(R.string.craft_select_legendary_preview), style = PixelSmall, color = Color.Gray)
                }
            }

            Text(text = safeStringResource(R.string.craft_label_mythic_recipe), style = PixelSmall, color = GoldAccent)

            // Checklist
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = if (hasItem) "✅" else "❌", fontSize = 12.sp)
                    Text(
                        text = if (selectedItem != null) stringResource(R.string.item_name_template, selectedItem.emoji, selectedItem.name) else stringResource(R.string.craft_err_requires_level_10_legendary),
                        style = PixelBody, color = if (hasItem) HpGreen else HpRed
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = if (hasShards) "✅" else "❌", fontSize = 12.sp)
                    Text(text = "${shardMat.emoji} ${safeStringResource(shardMat.nameRes)} ($mythicShards/50)", style = PixelBody, color = if (hasShards) HpGreen else HpRed)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = if (hasTrophies) "✅" else "❌", fontSize = 12.sp)
                    Text(text = "${trophyMat.emoji} ${safeStringResource(trophyMat.nameRes)} ($bossTrophies/5)", style = PixelBody, color = if (hasTrophies) HpGreen else HpRed)
                }
            }

            Button(
                onClick = { currentSet?.let { onForgeMythic(it.id, context) } },
                enabled = !uiState.isProcessing && canForge && currentSet != null,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(Rarity.MYTHIC.color), disabledContainerColor = StoneGray)
            ) {
                Text(
                    text = safeStringResource(R.string.craft_btn_forge_mythic),
                    color = if (canForge && currentSet != null) Color.White else Color.LightGray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StatPreviewRow(
    label: String,
    value: Long,
    delta: Long,
    isPercentage: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val displayVal = if (isPercentage) "+$value%" else "$value"
        val deltaSign = if (delta >= 0) "+$delta" else "$delta"
        val displayDelta = if (isPercentage) "$deltaSign%" else deltaSign
        val deltaColor = when {
            delta > 0 -> HpGreen
            delta < 0 -> HpRed
            else -> Color.Gray
        }

        Text(
            text = "$label:",
            style = PixelSmall,
            color = Color.LightGray
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = displayVal,
                style = PixelSmall,
                color = Color.White
            )
            Text(
                text = "($displayDelta)",
                style = PixelSmall,
                color = deltaColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}