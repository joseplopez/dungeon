package com.game.dungeon.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.game.dungeon.R
import com.game.dungeon.data.models.*
import com.game.dungeon.ui.components.*
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.TownViewModel
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

data class NpcState(
    val id: String,
    val name: String,
    val emoji: String,
    val startXDp: Float,
    val walkRangeDp: Float,
    val speed: Float,
    @param:StringRes val tipRes: Int
)

@Composable
fun TownScreen(
    navController: NavController,
    isMuted: Boolean,
    onToggleMusic: () -> Unit,
    viewModel: TownViewModel = hiltViewModel()
) {
    val gs by viewModel.gameState.collectAsState()
    val availableCrystals by viewModel.availableCrystals.collectAsState()
    val gil = gs?.gold ?: 0L
    val magicite = gs?.magicite ?: 0
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showResourceShop by viewModel.showResourceShop.collectAsState()
    val resourceShopType by viewModel.resourceShopType.collectAsState()
    val productDetailsMap by viewModel.billingManager.productDetailsMap.collectAsState()

    var showCrystalShop by remember { mutableStateOf(false) }
    var showUpgrades by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showBulletinDialog by remember { mutableStateOf(false) }

    var activeSpeechNpcId by remember { mutableStateOf<String?>(null) }
    var activeSpeechText by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val resources = context.resources

    // Infinite animation transition for building animations & smoke
    val infiniteTransition = rememberInfiniteTransition(label = "town_anim")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100000f,
        animationSpec = infiniteRepeatable(tween(100000, easing = LinearEasing)),
        label = "animTime"
    )

    // Ambient NPC speech timer
    LaunchedEffect(Unit) {
        val tips = listOf(
            "guard" to R.string.npc_tip_guard,
            "scholar" to R.string.npc_tip_scholar,
            "adventurer" to R.string.npc_tip_adventurer,
            "merchant" to R.string.npc_tip_merchant
        )
        var index = 0
        while (true) {
            delay(8000)
            val (npcId, tipRes) = tips[index % tips.size]
            activeSpeechNpcId = npcId
            activeSpeechText = resources.getString(tipRes)
            delay(3500)
            activeSpeechNpcId = null
            activeSpeechText = null
            index++
        }
    }

    PixelTheme {
        Box(Modifier.fillMaxSize()) {
            // Parallax Night Background
            TownParallaxBackground(scrollState.value.toFloat())

            Column(Modifier.fillMaxSize()) {
                // Header
                GoldenBorderBox(Modifier.fillMaxWidth().height(40.dp)) {
                    Box(Modifier.fillMaxSize().background(BgDarkest)) {
                        Row(
                            Modifier.fillMaxSize().padding(horizontal = 12.dp),
                            verticalAlignment = CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.clickable { viewModel.openResourceShop(com.game.dungeon.monetization.ResourceType.GIL) },
                                    verticalAlignment = CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("🪙", fontSize = 14.sp)
                                    Text(formatGold(gil), style = PixelGold)
                                    if (gs != null) {
                                        Text("/${formatGold(gs!!.maxGil)}", style = PixelSmall, color = StoneGray)
                                    }
                                    AdRewardIconButton(
                                        isMagicite = false,
                                        onClick = { viewModel.openResourceShop(com.game.dungeon.monetization.ResourceType.GIL) },
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Row(
                                    modifier = Modifier.clickable { viewModel.openResourceShop(com.game.dungeon.monetization.ResourceType.MAGICITE) },
                                    verticalAlignment = CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("💎", fontSize = 14.sp)
                                    Text("$magicite", style = PixelGold)
                                    AdRewardIconButton(
                                        isMagicite = true,
                                        onClick = { viewModel.openResourceShop(com.game.dungeon.monetization.ResourceType.MAGICITE) },
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Text(safeStringResource(R.string.town_name), style = PixelHeading)
                            Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                PixelButton(
                                    label = "❓",
                                    onClick = { showSupportDialog = true },
                                    modifier = Modifier.size(32.dp),
                                    horizontalPadding = 0.dp,
                                    verticalPadding = 0.dp
                                )
                                MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
                            }
                        }
                    }
                }

                // Town View Area (Horizontally Scrollable)
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                ) {
                    // Buildings Row
                    Row(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .align(Alignment.BottomStart),
                        horizontalArrangement = Arrangement.spacedBy(60.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Spacer(Modifier.width(120.dp)) // Left margin

                        // 1. Crystal / Alchemist Shop (Left)
                        TownBuilding(
                            name = safeStringResource(R.string.building_crystal_shop),
                            tag = "CRYSTAL SHOP",
                            animTime = animTime,
                            onClick = { showCrystalShop = true }
                        )

                        // 2. Training Hall / Barracks (Center-Left)
                        TownBuilding(
                            name = safeStringResource(R.string.building_barracks),
                            tag = "BARRACKS",
                            animTime = animTime,
                            onClick = { showUpgrades = true }
                        )

                        // 3. Adventurer's Guild & Relics (Center-Right)
                        TownBuilding(
                            name = safeStringResource(R.string.building_relics),
                            tag = "RELICS",
                            animTime = animTime,
                            onClick = {
                                navController.navigate("relics") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onBulletinClick = { showBulletinDialog = true }
                        )

                        // 4. Inn Building (Right)
                        TownBuilding(
                            name = safeStringResource(R.string.building_inn),
                            tag = "THE INN",
                            animTime = animTime,
                            onClick = { 
                                navController.navigate("inn") {
                                    launchSingleTop = true
                                    popUpTo("inn") { saveState = true }
                                    restoreState = true
                                }
                            }
                        )

                        Spacer(Modifier.width(200.dp)) // Right margin
                    }

                    // Walking NPCs inside the SAME scrollable Box
                    val npcs = remember {
                        listOf(
                            NpcState("guard", "Guard", "💂", startXDp = 480f, walkRangeDp = 160f, speed = 0.0012f, tipRes = R.string.npc_tip_guard),
                            NpcState("scholar", "Scholar", "🧙", startXDp = 200f, walkRangeDp = 120f, speed = 0.001f, tipRes = R.string.npc_tip_scholar),
                            NpcState("adventurer", "Hero", "🗡️", startXDp = 750f, walkRangeDp = 160f, speed = 0.0015f, tipRes = R.string.npc_tip_adventurer),
                            NpcState("merchant", "Merchant", "🪙", startXDp = 1020f, walkRangeDp = 120f, speed = 0.0009f, tipRes = R.string.npc_tip_merchant)
                        )
                    }

                    npcs.forEach { npc ->
                        val phase = animTime * npc.speed
                        val offsetX = npc.startXDp + sin(phase) * (npc.walkRangeDp / 2f)
                        val isWalkingLeft = cos(phase) < 0f
                        val walkBounce = kotlin.math.abs(sin(phase * 4f)) * 4f

                        Box(
                            Modifier
                                .offset(x = offsetX.dp, y = (-20 - walkBounce).dp)
                                .align(Alignment.BottomStart)
                                .clickable {
                                    activeSpeechNpcId = npc.id
                                    activeSpeechText = resources.getString(npc.tipRes)
                                }
                        ) {
                            Column(horizontalAlignment = CenterHorizontally) {
                                // Speech Bubble
                                if (activeSpeechNpcId == npc.id && activeSpeechText != null) {
                                    GoldenBorderBox(
                                        Modifier
                                            .padding(bottom = 4.dp)
                                            .background(BgDarkest.copy(alpha = 0.95f), RoundedCornerShape(6.dp))
                                    ) {
                                        Text(
                                            activeSpeechText!!,
                                            style = PixelSmall,
                                            color = GoldBright,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                // NPC Pixel Sprite
                                Text(
                                    npc.emoji,
                                    fontSize = 28.sp,
                                    modifier = Modifier.graphicsLayer(scaleX = if (isWalkingLeft) -1f else 1f)
                                )
                            }
                        }
                    }
                }

                BottomPixelNav(currentRoute, navController)
            }
        }
    }

    // Dialogs
    if (showCrystalShop) {
        CrystalShopDialog(
            availableCrystals = availableCrystals,
            unlockedJobs = gs?.unlockedJobs ?: setOf(HeroClass.FREELANCER),
            gil = gil,
            onBuy = { viewModel.buyCrystal(it) },
            onDismiss = { showCrystalShop = false },
            gs = gs ?: GameState()
        )
    }

    if (showUpgrades) {
        UpgradesDialog(
            gs = gs ?: GameState(),
            onUpgrade = { viewModel.upgradeBuilding(it) },
            onDismiss = { showUpgrades = false }
        )
    }

    if (showSupportDialog) {
        SupportDialog(
            onDismiss = { showSupportDialog = false }
        )
    }

    if (showBulletinDialog && gs != null) {
        BulletinBoardDialog(
            gs = gs!!,
            onDismiss = { showBulletinDialog = false }
        )
    }

    if (showResourceShop) {
        ResourceShopDialog(
            initialResourceType = resourceShopType,
            currentGil = gil,
            currentMagicite = magicite,
            totalGilEarned = gs?.totalGilEarned ?: 0L,
            totalMagiciteEarned = gs?.totalMagiciteEarned ?: 0,
            productDetailsMap = productDetailsMap,
            onWatchGilAd = { act -> viewModel.watchGilAd(act) },
            onWatchMagiciteAd = { act -> viewModel.watchMagiciteAd(act) },
            onBuyProduct = { act, product -> viewModel.buyProduct(act, product) },
            onDismiss = { viewModel.closeResourceShop() }
        )
    }
}

@Composable
fun TownBuilding(
    name: String,
    tag: String,
    animTime: Float,
    onClick: () -> Unit,
    onBulletinClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(200.dp, 150.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(Modifier.fillMaxSize()) {
                when (tag) {
                    "THE INN" -> drawDetailedInn(animTime)
                    "CRYSTAL SHOP" -> drawDetailedCrystalShop(animTime)
                    "BARRACKS" -> drawDetailedBarracks(animTime)
                    "RELICS" -> drawDetailedPortal(animTime)
                }
            }

            // Clickable Bulletin Board Overlay button on Guild building
            if (tag == "RELICS" && onBulletinClick != null) {
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 12.dp)
                        .size(50.dp, 35.dp)
                        .clickable { onBulletinClick() }
                )
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        // Golden Border Badge Label
        GoldenBorderBox(
            modifier = Modifier
                .background(BgDarkest.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
        ) {
            Text(
                name,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
                style = PixelBody,
                color = GoldBright,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
fun BulletinBoardDialog(
    gs: GameState,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(
            Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .background(BgDarkest)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    Text(safeStringResource(R.string.bulletin_title), style = PixelHeading)
                    PixelButton(
                        label = "X",
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp),
                        horizontalPadding = 0.dp,
                        verticalPadding = 0.dp
                    )
                }

                PixelDivider()

                // Highest Floor Record
                GoldenBorderBox(Modifier.fillMaxWidth().background(BgPanel.copy(alpha = 0.8f))) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("🏆", fontSize = 28.sp)
                        Column {
                            Text(safeStringResource(R.string.bulletin_record_format, gs.highestFloor), style = PixelBody, color = GoldBright)
                            Text("Dimension ${gs.currentDimension}", style = PixelSmall, color = StoneGray)
                        }
                    }
                }

                // Active Town Perks
                Text(safeStringResource(R.string.bulletin_active_perks), style = PixelGold, fontSize = 12.sp)

                Column(
                    Modifier.fillMaxWidth().background(BgPanel.copy(alpha = 0.6f)).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val perks = mutableListOf<String>()
                    if (gs.innLevel > 0) perks.add("🏨 Inn Rest HP Heal: +${gs.innLevel * 20}%")
                    if (gs.barracksLevel > 0) perks.add("🛡️ Barracks Max Party Size: ${3 + gs.barracksLevel}")
                    if (gs.vaultLevel > 0) perks.add("💰 Vault Gold Interest: +${gs.vaultLevel * 5}%")
                    if (gs.pathfinderLevel > 0) perks.add("🧭 Pathfinder Start Floor: Floor ${gs.pathfinderLevel * 5}")
                    if (gs.planningLevel > 0) perks.add("📜 Upgrade Discount: ${(gs.upgradeDiscount * 100).toInt()}%")

                    if (perks.isEmpty()) {
                        Text(
                            safeStringResource(R.string.bulletin_no_upgrades),
                            style = PixelSmall,
                            color = StoneGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        perks.forEach { perk ->
                            Text(perk, style = PixelSmall, color = GoldBright)
                        }
                    }
                }

                PixelButton(
                    label = "CLOSE",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                )
            }
        }
    }
}

@Composable
fun CrystalShopDialog(
    availableCrystals: List<TownViewModel.CrystalData>,
    unlockedJobs: Set<HeroClass>,
    gil: Long,
    onBuy: (CrystalColor) -> Unit,
    onDismiss: () -> Unit,
    gs: GameState
) {
    Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(
            Modifier
                .fillMaxWidth(0.95f)
                .height(450.dp)
                .background(BgDarkest)

        ) {
            Column(Modifier.padding(all = 24.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    Text(safeStringResource(R.string.building_crystal_shop), style = PixelHeading)
                    PixelButton(
                        label = "X",
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp),
                        horizontalPadding = 0.dp,
                        verticalPadding = 0.dp
                    )
                }
                Text(safeStringResource(R.string.crystal_shop_subtitle), style = PixelSmall, color = GoldDark)
                Spacer(Modifier.height(8.dp))
                PixelDivider()
                Spacer(Modifier.height(12.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    val allStandardUnlocked = HeroClass.entries.filter { it.tier == 1 || it.tier == 2 }.all { unlockedJobs.contains(it) || it == HeroClass.FREELANCER }
                    val hiddenJobsToDiscover = HeroClass.entries.filter { it.tier == 3 }.any { !gs.isJobDiscovered(it) }
                    if (allStandardUnlocked && hiddenJobsToDiscover) {
                        item {
                            Row(Modifier.fillMaxWidth().padding(4.dp), verticalAlignment = CenterVertically) {
                                Canvas(Modifier.size(32.dp)) {
                                    val path = Path().apply {
                                        moveTo(size.width / 2f, 0f)
                                        lineTo(size.width, size.height * 0.4f)
                                        lineTo(size.width / 2f, size.height)
                                        lineTo(0f, size.height * 0.4f)
                                        close()
                                    }
                                    drawPath(path, color = Color(0xFF000000))
                                    drawPath(path, color = Color.White.copy(alpha = 0.3f), style = Stroke(2f))
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("???", style = PixelBody, color = GoldBright)
                                    Text(safeStringResource(R.string.hidden_job_clue_desc), style = PixelSmall, color = StoneGray)
                                }
                            }
                        }
                    }
                    items(availableCrystals) { crystal ->
                        val isUnlocked = unlockedJobs.contains(crystal.unlocksJob)
                        CrystalShopRow(crystal, isUnlocked, gil, onBuy = { onBuy(crystal.color) })
                    }
                }
            }
        }
    }
}

@Composable
fun CrystalShopRow(
    crystal: TownViewModel.CrystalData,
    isUnlocked: Boolean,
    gil: Long,
    onBuy: () -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(4.dp).testTag("CrystalRow_${crystal.color.name}"), verticalAlignment = CenterVertically) {
        Canvas(Modifier.size(32.dp)) {
            val path = Path().apply {
                moveTo(size.width / 2f, 0f)
                lineTo(size.width, size.height * 0.4f)
                lineTo(size.width / 2f, size.height)
                lineTo(0f, size.height * 0.4f)
                close()
            }
            drawPath(path, color = Color(crystal.color.colorHex))
            drawPath(path, color = Color.White.copy(alpha = 0.3f), style = Stroke(2f))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(crystal.color.displayName, style = PixelBody, color = Color(crystal.color.colorHex))
            Text(safeStringResource(id = R.string.unlocks_job_format, safeStringResource(crystal.unlocksJob.nameRes)), style = PixelSmall, color = GoldDark)
        }
        if (isUnlocked) {
            Text(safeStringResource(R.string.owned_status), style = PixelSmall, color = HpGreen)
        } else {
            PixelButton(
                "${crystal.color.baseCost}G",
                onClick = onBuy,
                enabled = gil >= crystal.color.baseCost,
                modifier = Modifier.width(80.dp)
            )
        }
    }
}

@Composable
fun UpgradesDialog(
    gs: GameState,
    onUpgrade: (UpgradeType) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(Modifier.fillMaxWidth(0.98f).fillMaxHeight(0.85f).background(BgDarkest).padding(16.dp)) {
            Column {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), 
                    horizontalArrangement = Arrangement.SpaceBetween, 
                    verticalAlignment = CenterVertically
                ) {
                    Column {
                        Text(safeStringResource(R.string.town_upgrades), style = PixelHeading)
                        if (gs.planningLevel > 0) {
                            Text(safeStringResource(R.string.upgrade_discount, (gs.upgradeDiscount * 100).toInt()), style = PixelSmall, color = HpGreen)
                        }
                    }
                    PixelButton(
                        label = "X",
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp),
                        horizontalPadding = 0.dp,
                        verticalPadding = 0.dp
                    )
                }
                PixelDivider()
                Spacer(Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(UpgradeType.entries) { type ->
                        val currentLevel = when (type) {
                            UpgradeType.INN -> gs.innLevel
                            UpgradeType.BARRACKS -> gs.barracksLevel
                            UpgradeType.VAULT -> gs.vaultLevel
                            UpgradeType.ARMORY -> gs.armoryLevel
                            UpgradeType.MAGIC_SHOP -> gs.magicShopLevel
                            UpgradeType.TRAINING -> gs.trainingLevel
                            UpgradeType.PLANNING -> gs.planningLevel
                            UpgradeType.CLINIC -> gs.clinicLevel
                            UpgradeType.PATHFINDER -> gs.pathfinderLevel
                        }
                        
                        val isMax = currentLevel >= type.maxLevel
                        val rawCost = type.baseCost * (currentLevel + 1)
                        val finalCost = (rawCost * (1f - gs.upgradeDiscount)).toLong()
                        val canAfford = gs.gold >= finalCost

                        PixelPanel(Modifier.fillMaxWidth().wrapContentHeight(), borderColor = if(isMax) StoneGray else GoldDark) {
                            Column(Modifier.fillMaxWidth().padding(4.dp)) {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = CenterVertically) {
                                    Text(type.emoji, fontSize = 24.sp)
                                    Spacer(Modifier.width(16.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(safeStringResource(type.nameRes), style = PixelBody, color = if(isMax) StoneGray else GoldBright)
                                        val levelText = if (type == UpgradeType.PATHFINDER) {
                                            val maxStart = (gs.highestFloor * (currentLevel * 0.25f)).toInt().coerceIn(1, gs.highestFloor.coerceAtLeast(1))
                                            safeStringResource(R.string.pathfinder_level_format, currentLevel, type.maxLevel, maxStart)
                                        } else {
                                            safeStringResource(R.string.level_format, currentLevel, type.maxLevel)
                                        }
                                        Text(levelText, style = PixelSmall, color = GoldDark)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    if (isMax) {
                                        Text(safeStringResource(R.string.max_level), style = PixelBody, color = HpGreen, modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
                                    } else {
                                        PixelButton(
                                            "${finalCost}G", 
                                            onClick = { onUpgrade(type) }, 
                                            enabled = canAfford, 
                                            modifier = Modifier.width(80.dp).height(40.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    safeStringResource(type.descRes),
                                    style = PixelSmall, 
                                    color = StoneGray,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SupportDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(
            Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .background(BgDarkest)
        ) {
            Column(Modifier.padding(all = 20.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    Text("SUPPORT & FEEDBACK", style = PixelHeading)
                    PixelButton(
                        label = "X",
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp),
                        horizontalPadding = 0.dp,
                        verticalPadding = 0.dp
                    )
                }
                Spacer(Modifier.height(12.dp))
                PixelDivider()
                Spacer(Modifier.height(16.dp))
                
                Text("HELP IMPROVE THE GAME!", style = PixelGold, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Found a bug or have a suggestion? Send an email directly to the developer.",
                    style = PixelSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
                
                Spacer(Modifier.height(16.dp))
                PixelButton(
                    label = "📧 SEND FEEDBACK",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:joseplcatz@gmai.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Final Dungeon Feedback")
                        }
                        context.startActivity(Intent.createChooser(intent, "Send Email"))
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                )
                
                Spacer(Modifier.height(24.dp))
                Text("LOVE FINAL DUNGEON?", style = PixelGold, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Rating the app helps other players discover the dungeon!",
                    style = PixelSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
                
                Spacer(Modifier.height(16.dp))
                PixelButton(
                    label = "⭐ RATE ON PLAY STORE",
                    onClick = {
                        val packageName = context.packageName
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                )
            }
        }
    }
}
