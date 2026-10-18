package com.game.dungeon.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.game.dungeon.BuildConfig
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
    viewModel: TownViewModel = hiltViewModel(),
    onNavigateToBossLair: (party: List<Hero>, startFloor: Int) -> Unit = { _, _ -> }
) {
    val gs by viewModel.gameState.collectAsState()
    val party by viewModel.party.collectAsState()
    val availableCrystals by viewModel.availableCrystals.collectAsState()
    val gil = gs?.gold ?: 0L
    val magicite = gs?.magicite ?: 0
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showResourceShop by viewModel.showResourceShop.collectAsState()
    val resourceShopType by viewModel.resourceShopType.collectAsState()
    val productDetailsMap by viewModel.billingManager.productDetailsMap.collectAsState()

    var showUpgrades by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showBulletinDialog by remember { mutableStateOf(false) }
    var showBossLairPrep by remember { mutableStateOf(false) }
    var showBossLairDiscoveryPopup by remember { mutableStateOf(false) }

    val canSeeBossLair = (gs?.currentDimension ?: 1) >= 11 || BuildConfig.ENABLE_BOSS_LAIR_TESTING

    LaunchedEffect(canSeeBossLair, gs?.hasSeenBossLairPopup) {
        if (gs != null && canSeeBossLair && !gs!!.hasSeenBossLairPopup) {
            showBossLairDiscoveryPopup = true
            viewModel.setHasSeenBossLairPopup()
        }
    }

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
            "merchant" to R.string.npc_tip_merchant,
            "gladiator" to R.string.npc_tip_gladiator
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
        Box(Modifier
            .fillMaxSize()
            .pointerInput(canSeeBossLair) { // Re-binds if unlock state changes
                detectTapGestures { tapOffset ->
                    if (canSeeBossLair && isBossLairClicked(
                            tapX = tapOffset.x,
                            tapY = tapOffset.y,
                            screenWidth = size.width.toFloat(),
                            screenHeight = size.height.toFloat(),
                            scrollOffset = scrollState.value.toFloat() // Gets live scroll position
                        )
                    ) {
                        showBossLairPrep = true // Trigger your boss screen!
                    }
                }
            }) {
            // Parallax Night Background
            TownParallaxBackground(
                scrollOffset = scrollState.value.toFloat(),
                isBossLairUnlocked = canSeeBossLair
            )

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
                                    PixelGoldDisplay(amount = gil)
                                    if (gs != null && gs!!.maxGil < Long.MAX_VALUE) {
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
                                    PixelMagiciteDisplay(amount = magicite)
                                    AdRewardIconButton(
                                        isMagicite = true,
                                        onClick = { viewModel.openResourceShop(com.game.dungeon.monetization.ResourceType.MAGICITE) },
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Text(safeStringResource(R.string.town_name), style = PixelHeading)
                            Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                SupportIconButton(onClick = { showSupportDialog = true })
                                MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
                                Spacer(Modifier.size(size = 16.dp))
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
                    // Buildings Row - Aligned precisely to the bottom so nameplates match perfectly horizontally
                    Row(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .align(Alignment.BottomStart),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Spacer(Modifier.width(100.dp)) // Left margin

                        TownBuilding(
                            name = safeStringResource(R.string.building_crystal_shop),
                            tag = "CRYSTAL SHOP",
                            animTime = animTime,
                            buildingWidth = 180.dp,
                            buildingHeight = 120.dp,
                            onClick = { navController.navigate("crystal_shop") {
                                launchSingleTop = true
                                restoreState = true
                            } }
                        )

                        Spacer(Modifier.width(40.dp))

                        TownBuilding(
                            name = safeStringResource(R.string.building_barracks),
                            tag = "BARRACKS",
                            animTime = animTime,
                            buildingWidth = 240.dp,
                            buildingHeight = 150.dp,
                            onClick = { showUpgrades = true }
                        )

                        Spacer(Modifier.width(50.dp))

                        TownBuilding(
                            name = safeStringResource(R.string.building_blacksmith_title),
                            tag = "BLACKSMITH",
                            animTime = animTime,
                            buildingWidth = 180.dp,
                            buildingHeight = 140.dp,
                            onClick = {
                                navController.navigate("crafting") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )

                        Spacer(Modifier.width(50.dp))

                        // Tall, dramatic Wizard Spire
                        TownBuilding(
                            name = safeStringResource(R.string.building_relics),
                            tag = "RELICS",
                            animTime = animTime,
                            buildingWidth = 140.dp,
                            buildingHeight = 240.dp,
                            onClick = {
                                navController.navigate("relics") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )

                        Spacer(Modifier.width(60.dp))

                        TownBuilding(
                            name = safeStringResource(R.string.building_library_title),
                            tag = "LIBRARY",
                            animTime = animTime,
                            buildingWidth = 200.dp,
                            buildingHeight = 180.dp,
                            onClick = {
                                navController.navigate("codex") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )

                        Spacer(Modifier.width(50.dp))

                        TownBuilding(
                            name = safeStringResource(R.string.building_inn),
                            tag = "THE INN",
                            animTime = animTime,
                            buildingWidth = 190.dp,
                            buildingHeight = 160.dp,
                            onClick = {
                                navController.navigate("inn") {
                                    launchSingleTop = true
                                    popUpTo("inn") { saveState = true }
                                    restoreState = true
                                }
                            },
                            onBulletinClick = { showBulletinDialog = true }
                        )

                        Spacer(Modifier.width(60.dp))

                        TownBuilding(
                            name = safeStringResource(R.string.building_colosseum),
                            tag = "COLOSSEUM",
                            animTime = animTime,
                            buildingWidth = 240.dp,
                            buildingHeight = 160.dp,
                            onClick = {
                                navController.navigate("leaderboard") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )

                        Spacer(Modifier.width(200.dp)) // Right margin
                    }

                    // Walking NPCs inside the SAME scrollable Box (Adjusted X coordinates for wider town)
                    val npcs = remember {
                        listOf(
                            NpcState("scholar", "Scholar", startXDp = 250f, walkRangeDp = 120f, speed = 0.001f, tipRes = R.string.npc_tip_scholar),
                            NpcState("guard", "Guard", startXDp = 580f, walkRangeDp = 160f, speed = 0.0012f, tipRes = R.string.npc_tip_guard),
                            NpcState("adventurer", "Hero", startXDp = 920f, walkRangeDp = 160f, speed = 0.0015f, tipRes = R.string.npc_tip_adventurer),
                            NpcState("merchant", "Merchant", startXDp = 1250f, walkRangeDp = 120f, speed = 0.0009f, tipRes = R.string.npc_tip_merchant),
                            NpcState("gladiator", "Gladiator", startXDp = 1600f, walkRangeDp = 140f, speed = 0.0011f, tipRes = R.string.npc_tip_gladiator)
                        )
                    }

                    npcs.forEach { npc ->
                        val phase = animTime * npc.speed
                        val offsetX = npc.startXDp + sin(phase) * (npc.walkRangeDp / 2f)
                        val isWalkingLeft = cos(phase) < 0f
                        val walkBounce = kotlin.math.abs(sin(phase * 4f)) * 4f

                        Box(
                            Modifier
                                .offset(x = offsetX.dp, y = (-50 - walkBounce).dp)
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
                                TownNpcSprite(
                                    npcId = npc.id,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .graphicsLayer(scaleX = if (isWalkingLeft) -1f else 1f)
                                )
                            }
                        }
                    }
                }

                BottomPixelNav(currentRoute, navController)
            }
        }
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

    if (showBossLairDiscoveryPopup) {
        BossLairDiscoveryDialog(
            onDismiss = { showBossLairDiscoveryPopup = false }
        )
    }

    if (showBossLairPrep) {
        val activity = androidx.compose.ui.platform.LocalContext.current as? android.app.Activity
        BossLairPrepScreen(
            party = party,
            highestBossLairFloor = gs?.highestBossLairFloor ?: 1,
            hasPermadeathProtectionUnlocked = gs?.hasPermadeathProtectionUnlocked ?: false,
            isPermadeathProtectionActive = gs?.isPermadeathProtectionActive ?: false,
            onBuyPermadeathProtection = { activity?.let { viewModel.buyPermadeathProtection(it) } },
            onTogglePermadeathProtection = { active -> viewModel.togglePermadeathProtection(active) },
            onEnterBossLair = { startFloor ->
                showBossLairPrep = false
                onNavigateToBossLair(party, startFloor)
            },
            onDismiss = { showBossLairPrep = false }
        )
    }
}

@Composable
fun BossLairDiscoveryDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        GoldenBorderBox(
            Modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = 420.dp)
                .wrapContentHeight()
                .background(BgDarkest)
                .testTag("BossLairDiscoveryDialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("💀", fontSize = 32.sp)
                Text(
                    safeStringResource(R.string.boss_lair_discovered_title),
                    style = PixelHeading,
                    color = GoldBright,
                    textAlign = TextAlign.Center
                )
                PixelDivider()
                Text(
                    safeStringResource(R.string.boss_lair_discovered_desc),
                    style = PixelBody,
                    color = StoneGray,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                PixelButton(
                    label = safeStringResource(R.string.btn_ok),
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(40.dp)
                )
            }
        }
    }
}

@Composable
fun TownBuilding(
    name: String,
    tag: String,
    animTime: Float,
    buildingWidth: Dp,
    buildingHeight: Dp,
    onClick: () -> Unit,
    onBulletinClick: (() -> Unit)? = null
) {
    val displayName = name.ifBlank { tag }
    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier
            .wrapContentWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.size(width = buildingWidth, height = buildingHeight),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(Modifier.fillMaxSize()) {
                when (tag) {
                    "THE INN" -> drawDetailedInn(animTime)
                    "CRYSTAL SHOP" -> drawDetailedCrystalShop(animTime)
                    "BARRACKS" -> drawDetailedBarracks(animTime)
                    "RELICS" -> drawDetailedPortal(animTime)
                    "COLOSSEUM" -> drawDetailedColosseum(animTime)
                    "BLACKSMITH" -> drawDetailedBlacksmith(animTime)
                    "LIBRARY" -> drawDetailedLibrary(animTime/3f)
                }
            }

            // Clickable Bulletin Board Overlay button on Inn building
            if (tag == "THE INN" && onBulletinClick != null) {
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 12.dp)
                        .size(60.dp, 50.dp)
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
                text = displayName,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                style = PixelBody,
                color = GoldBright,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BulletinBoardDialog(
    gs: GameState,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        GoldenBorderBox(
            Modifier
                .fillMaxWidth(0.90f)
                .widthIn(max = 460.dp)
                .wrapContentHeight()
                .background(BgDarkest)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
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
                            Text(safeStringResource(R.string.dimension_label_format, gs.currentDimension), style = PixelSmall, color = StoneGray)
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
                    if (gs.innLevel > 0) perks.add(safeStringResource(R.string.bulletin_perk_inn, gs.innLevel * 20))
                    if (gs.barracksLevel > 0) perks.add(safeStringResource(R.string.bulletin_perk_barracks, 3 + gs.barracksLevel))
                    if (gs.vaultLevel > 0) perks.add(safeStringResource(R.string.bulletin_perk_vault, gs.vaultLevel * 5))
                    if (gs.pathfinderLevel > 0) perks.add(safeStringResource(R.string.bulletin_perk_pathfinder, gs.pathfinderLevel * 5))
                    if (gs.planningLevel > 0) perks.add(safeStringResource(R.string.bulletin_perk_planning, (gs.upgradeDiscount * 100).toInt()))
                    if (gs.alchemistLevel > 0) perks.add(safeStringResource(R.string.bulletin_perk_alchemist, gs.alchemistLevel * 10))
                    if (gs.libraryLevel > 0) perks.add(safeStringResource(R.string.bulletin_perk_library, gs.libraryLevel * 20))
                    if (gs.forgeLevel > 0) perks.add(safeStringResource(R.string.bulletin_perk_forge, gs.forgeLevel * 5))
                    if (gs.warRoomLevel > 0) perks.add(safeStringResource(R.string.bulletin_perk_war_room, gs.warRoomLevel * 15))

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
                    label = safeStringResource(R.string.close_button),
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                )
            }
        }
    }
}


private fun getUpgradeLevel(gs: GameState, type: UpgradeType): Int {
    return when (type) {
        UpgradeType.INN -> gs.innLevel
        UpgradeType.BARRACKS -> gs.barracksLevel
        UpgradeType.VAULT -> gs.vaultLevel
        UpgradeType.ARMORY -> gs.armoryLevel
        UpgradeType.MAGIC_SHOP -> gs.magicShopLevel
        UpgradeType.TRAINING -> gs.trainingLevel
        UpgradeType.PLANNING -> gs.planningLevel
        UpgradeType.CLINIC -> gs.clinicLevel
        UpgradeType.PATHFINDER -> gs.pathfinderLevel
        UpgradeType.ALCHEMIST -> gs.alchemistLevel
        UpgradeType.LIBRARY -> gs.libraryLevel
        UpgradeType.FORGE -> gs.forgeLevel
        UpgradeType.WAR_ROOM -> gs.warRoomLevel
    }
}

@Composable
fun UpgradesDialog(
    gs: GameState,
    onUpgrade: (UpgradeType) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val basicUpgrades = remember { UpgradeType.entries.filter { !it.isAdvanced } }
    val advancedUpgrades = remember { UpgradeType.entries.filter { it.isAdvanced } }

    val basicMaxed = basicUpgrades.count { getUpgradeLevel(gs, it) >= it.maxLevel }
    val advancedMaxed = advancedUpgrades.count { getUpgradeLevel(gs, it) >= it.maxLevel }

    val activeList = if (selectedTab == 0) basicUpgrades else advancedUpgrades

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        GoldenBorderBox(
            Modifier
                .fillMaxSize()
                .background(BgDarkest)
        ) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                // Top Header Bar
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(safeStringResource(R.string.town_upgrades), style = PixelHeading, fontSize = 20.sp)
                        Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PixelGoldDisplay(amount = gs.gold)
                            PixelMagiciteDisplay(amount = gs.magicite)
                        }
                    }
                    Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (gs.planningLevel > 0) {
                            Text(safeStringResource(R.string.upgrade_discount, (gs.upgradeDiscount * 100).toInt()), style = PixelSmall, color = HpGreen)
                        }
                        PixelButton(
                            label = "X",
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp),
                            horizontalPadding = 0.dp,
                            verticalPadding = 0.dp
                        )
                    }
                }

                PixelDivider()
                Spacer(Modifier.height(8.dp))

                // Navigation Tabs
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PixelButton(
                        label = "${safeStringResource(R.string.tab_basic_upgrades)} ($basicMaxed/${basicUpgrades.size})",
                        onClick = { selectedTab = 0 },
                        active = selectedTab == 0,
                        modifier = Modifier.weight(1f).height(38.dp)
                    )
                    PixelButton(
                        label = "✨ ${safeStringResource(R.string.tab_advanced_upgrades)} ($advancedMaxed/${advancedUpgrades.size})",
                        onClick = { selectedTab = 1 },
                        active = selectedTab == 1,
                        modifier = Modifier.weight(1f).height(38.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // 2-Column Responsive Card Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    items(activeList) { type ->
                        val currentLevel = getUpgradeLevel(gs, type)
                        val isMax = currentLevel >= type.maxLevel
                        val rawCost = type.getCost(currentLevel)
                        val finalCost = (rawCost * (1f - gs.upgradeDiscount)).toLong()
                        val canAfford = gs.gold >= finalCost

                        PixelPanel(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = if (isMax) StoneGray else if (type.isAdvanced) GoldBright else GoldDark
                        ) {
                            Column(
                                Modifier.fillMaxWidth().padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Card Top Row: Emoji + Name + Level / Badge
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(type.emoji, fontSize = 22.sp)
                                        Text(
                                            safeStringResource(type.nameRes),
                                            style = PixelBody,
                                            color = if (isMax) StoneGray else GoldBright,
                                            maxLines = 1
                                        )
                                    }
                                    Row(
                                        verticalAlignment = CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (type.isAdvanced) {
                                            Box(
                                                Modifier
                                                    .background(GoldBright, RoundedCornerShape(3.dp))
                                                    .border(1.dp, GoldDark, RoundedCornerShape(3.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    safeStringResource(R.string.badge_advanced_upgrade),
                                                    style = PixelSmall,
                                                    color = BgDarkest,
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                        val levelText = if (type == UpgradeType.PATHFINDER) {
                                            val maxStart = (gs.highestFloor * (currentLevel * 0.25f)).toInt().coerceIn(1, gs.highestFloor.coerceAtLeast(1))
                                            safeStringResource(R.string.pathfinder_level_format, currentLevel, type.maxLevel, maxStart)
                                        } else {
                                            safeStringResource(R.string.level_format, currentLevel, type.maxLevel)
                                        }
                                        Text(levelText, style = PixelSmall, color = GoldDark)
                                    }
                                }

                                // Level Progress Bar
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .background(BgDark, RoundedCornerShape(3.dp))
                                ) {
                                    val progressFraction = (currentLevel.toFloat() / type.maxLevel.toFloat()).coerceIn(0f, 1f)
                                    Box(
                                        Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(progressFraction)
                                            .background(if (isMax) HpGreen else GoldBright, RoundedCornerShape(3.dp))
                                    )
                                }

                                // Description Text - Unconstrained vertical height and high-contrast text color
                                Text(
                                    safeStringResource(type.descRes),
                                    style = PixelSmall,
                                    color = Color(0xFFE0E0E0),
                                    lineHeight = 15.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Upgrade Cost Button or MAX
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = CenterVertically
                                ) {
                                    if (isMax) {
                                        Text(
                                            safeStringResource(R.string.max_level),
                                            style = PixelBody,
                                            color = HpGreen,
                                            textAlign = TextAlign.Center
                                        )
                                    } else {
                                        PixelButton(
                                            label = "${formatGold(finalCost)}G",
                                            onClick = { onUpgrade(type) },
                                            enabled = canAfford,
                                            modifier = Modifier.width(110.dp).height(36.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}