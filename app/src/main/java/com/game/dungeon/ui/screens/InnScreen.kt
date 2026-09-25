package com.game.dungeon.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import com.game.dungeon.ui.viewmodels.InnViewModel
import kotlin.math.sin

// --- Colors for Guild Stage Theme ---
val GuildRoyalBlue = Color(0xFF0F1B2B)
val GuildGoldAccent = Color(0xFFE5C158)

@Composable
fun InnScreen(
    onNavigateToDungeon: (List<Hero>, Int) -> Unit,
    onNavigateToEquipment: (String) -> Unit,
    onNavigateToMastery: () -> Unit,
    navController: NavController,
    isMuted: Boolean,
    onToggleMusic: () -> Unit,
    viewModel: InnViewModel = hiltViewModel(),
) {
    val gs by viewModel.gameState.collectAsState()
    val hiredHeroes by viewModel.hiredHeroes.collectAsState()
    val startFloor by viewModel.startFloor.collectAsState()
    val gil = gs?.gold ?: 0L
    val magicite = gs?.magicite ?: 0
    val unlockedJobs = viewModel.unlockedJobs
    val maxPartySize = viewModel.maxPartySize
    val canSendToDungeon = viewModel.canSendToDungeon
    val highestFloor = gs?.highestFloor ?: 0
    val currentDimension = gs?.currentDimension ?: 1
    val pathfinderLevel = gs?.pathfinderLevel ?: 0

    val showResourceShop by viewModel.showResourceShop.collectAsState()
    val resourceShopType by viewModel.resourceShopType.collectAsState()
    val productDetailsMap by viewModel.billingManager.productDetailsMap.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showDimensionResetDialog by remember { mutableStateOf(false) }
    var selectedHireJob by remember { mutableStateOf<HeroClass?>(null) }
    var showPathfinderDialog by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().background(GuildRoyalBlue)) {
        InnBackground()
        Column(Modifier.fillMaxSize()) {
            InnTopBar(
                gil = gil, 
                magicite = magicite, 
                isMuted = isMuted, 
                onToggleMusic = onToggleMusic,
                onOpenResourceShop = { viewModel.openResourceShop(it) }
            )
            
            // Dimension Advance Banner (Only shows if floor 100 reached)
            if (highestFloor >= 100) {
                DimensionAdvanceBanner(currentDimension) { showDimensionResetDialog = true }
            }

            Row(
                Modifier
                    .weight(1f)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // LEFT (70% width): Interactive Guild Stage
                GuildStageArea(
                    modifier = Modifier.weight(0.70f),
                    unlockedJobs = unlockedJobs,
                    hiredHeroes = hiredHeroes,
                    gil = gil,
                    pathfinderLevel = pathfinderLevel,
                    canSend = canSendToDungeon,
                    onSelectJobToHire = { job -> selectedHireJob = job },
                    onRest = { viewModel.restAtInn() },
                    onDepartClick = {
                        if (pathfinderLevel > 0) {
                            showPathfinderDialog = true
                        } else {
                            onNavigateToDungeon(hiredHeroes, 1)
                            viewModel.resetStartFloor()
                        }
                    },
                    onNavigateToMastery = onNavigateToMastery
                )

                // RIGHT (30% width): Party Roster Panel
                PartyPanel(
                    modifier = Modifier.weight(0.30f),
                    hiredHeroes = hiredHeroes,
                    maxPartySize = maxPartySize,
                    onFire = { viewModel.fireHero(it) },
                    onEquip = onNavigateToEquipment,
                    onMoveUp = { viewModel.moveHeroUp(it) },
                    onMoveDown = { viewModel.moveHeroDown(it) }
                )
            }

            BottomPixelNav(currentRoute, navController)
        }
    }

    // Hire Popup
    selectedHireJob?.let { job ->
        HireHeroClassDialog(
            job = job,
            gil = gil,
            partySize = hiredHeroes.size,
            maxPartySize = maxPartySize,
            onDismiss = { selectedHireJob = null },
            onHire = {
                viewModel.hireHero(job)
                selectedHireJob = null
            }
        )
    }

    // Pathfinder Selector Popup on Expedition Launch
    if (showPathfinderDialog) {
        PathfinderFloorSelectDialog(
            pathfinderLevel = pathfinderLevel,
            highestFloor = highestFloor,
            initialStartFloor = startFloor,
            onConfirm = { chosenFloor ->
                viewModel.setStartFloor(chosenFloor)
                onNavigateToDungeon(hiredHeroes, chosenFloor)
                viewModel.resetStartFloor()
                showPathfinderDialog = false
            },
            onDismiss = { showPathfinderDialog = false }
        )
    }

    if (showDimensionResetDialog) {
        DimensionResetDialog(
            gs = gs ?: GameState(),
            onDismiss = { showDimensionResetDialog = false },
            onConfirm = { 
                viewModel.advanceDimension()
                showDimensionResetDialog = false 
            }
        )
    }

    val unnotifiedJob = remember(gs) {
        HeroClass.entries.firstOrNull { job ->
            job.tier == 3 && gs?.isJobDiscovered(job) == true && gs?.notifiedHiddenJobs?.contains(job) == false
        }
    }

    if (unnotifiedJob != null) {
        HiddenJobUnlockDialog(
            heroClass = unnotifiedJob,
            onDismiss = { viewModel.markHiddenJobNotified(unnotifiedJob) }
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

// ============================================================================
// INTERACTIVE GUILD STAGE
// ============================================================================

@Composable
fun GuildStageArea(
    modifier: Modifier,
    unlockedJobs: List<HeroClass>,
    hiredHeroes: List<Hero>,
    gil: Long,
    pathfinderLevel: Int,
    canSend: Boolean,
    onSelectJobToHire: (HeroClass) -> Unit,
    onRest: () -> Unit,
    onDepartClick: () -> Unit,
    onNavigateToMastery: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF09121E))
            .border(2.dp, GuildGoldAccent)
            .padding(6.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically
            ) {
                Text(
                    text = "🛡️ " + safeStringResource(R.string.hire_warriors),
                    style = PixelHeading,
                    color = GuildGoldAccent
                )
                Text(
                    text = "${unlockedJobs.size} JOBS",
                    style = PixelSmall,
                    color = StoneGray
                )
            }

            Spacer(Modifier.height(4.dp))

            // Tavern Stage Box with Horizontal Scroll for candidate sprites
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF050B12))
                    .border(1.dp, GuildGoldAccent.copy(alpha = 0.4f)),
                contentAlignment = Center
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    drawDetailedInn()
                }

                val infiniteTransition = rememberInfiniteTransition(label = "patrol")
                val walkTime by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 6.28f,
                    animationSpec = infiniteRepeatable(tween(4500, easing = LinearEasing)),
                    label = "walk"
                )

                val scrollState = rememberScrollState()

                Row(
                    Modifier
                        .fillMaxSize()
                        .horizontalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    unlockedJobs.forEachIndexed { index, job ->
                        val rowTier = index % 2
                        val offsetY = (sin(walkTime + index * 1.4f) * 6).dp - (rowTier * 22).dp

                        Column(
                            Modifier
                                .offset(y = offsetY)
                                .clickable { onSelectJobToHire(job) }
                                .testTag("HireRow_${job.name}"),
                            horizontalAlignment = CenterHorizontally
                        ) {
                            Box(
                                Modifier
                                    .background(GuildGoldAccent, RoundedCornerShape(3.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${job.hireCost}G",
                                    style = PixelSmall,
                                    color = Color.Black,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            HeroSprite(job, Modifier.size(38.dp))
                            Text(
                                text = safeStringResource(job.nameRes),
                                style = PixelSmall,
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // Actions Row
            val woundedHeroes = hiredHeroes.filter { it.currentHp < it.maxHp }
            val restCost = woundedHeroes.sumOf { hero ->
                val hpMissingRatio = (hero.maxHp - hero.currentHp).toFloat() / hero.maxHp.toFloat()
                val baseCost = hero.level * 10
                (baseCost * hpMissingRatio).toLong().coerceAtLeast(1L)
            }
            val canAffordRest = restCost in 1..gil

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                PixelButton(
                    label = safeStringResource(R.string.masteries),
                    onClick = onNavigateToMastery,
                    horizontalPadding = 4.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                )
                PixelButton(
                    label = if (restCost > 0) safeStringResource(R.string.rest_at_inn_cost, restCost) else safeStringResource(R.string.rest_at_inn_free),
                    onClick = onRest,
                    enabled = canAffordRest || (restCost == 0L && hiredHeroes.isNotEmpty() && woundedHeroes.isNotEmpty()),
                    horizontalPadding = 4.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                )
                PixelButton(
                    label = if (canSend) safeStringResource(R.string.enter_dungeon) else safeStringResource(R.string.need_warriors),
                    onClick = onDepartClick,
                    enabled = canSend,
                    active = canSend,
                    horizontalPadding = 4.dp,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(42.dp)
                )
            }
        }
    }
}

// ============================================================================
// POPUP DIALOGS
// ============================================================================

@Composable
fun HireHeroClassDialog(
    job: HeroClass,
    gil: Long,
    partySize: Int,
    maxPartySize: Int,
    onDismiss: () -> Unit,
    onHire: () -> Unit
) {
    val canHire = gil >= job.hireCost && partySize < maxPartySize

    Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(
            Modifier
                .width(320.dp)
                .background(BgDarkest)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = safeStringResource(R.string.hire_warriors),
                    style = PixelHeading,
                    color = GoldBright
                )

                Spacer(Modifier.height(2.dp))

                Box(
                    Modifier
                        .size(64.dp)
                        .background(Color(job.crystalColor.colorHex).copy(alpha = 0.2f), CircleShape)
                        .border(2.dp, Color(job.crystalColor.colorHex), CircleShape),
                    contentAlignment = Center
                ) {
                    HeroSprite(job, Modifier.size(48.dp))
                }

                Text(
                    text = safeStringResource(job.nameRes),
                    style = PixelHeading,
                    color = GoldBright
                )

                Text(
                    text = safeStringResource(job.descRes),
                    style = PixelBody,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = safeStringResource(R.string.permadeath_warning),
                    style = PixelSmall,
                    color = EnemyRed,
                    textAlign = TextAlign.Center
                )

                PixelDivider()

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    Text("COST:", style = PixelBody, color = StoneGray)
                    Text("${job.hireCost} G", style = PixelGold, fontWeight = FontWeight.Bold)
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PixelButton(
                        label = safeStringResource(R.string.cancel_button),
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    )

                    PixelButton(
                        label = if (partySize >= maxPartySize) "FULL" else "${job.hireCost}G HIRE",
                        onClick = onHire,
                        enabled = canHire,
                        active = canHire,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PathfinderFloorSelectDialog(
    pathfinderLevel: Int,
    highestFloor: Int,
    initialStartFloor: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFloor by remember { mutableIntStateOf(initialStartFloor) }
    val maxStartFloor = (highestFloor * (pathfinderLevel * 0.25f)).toInt().coerceIn(1, highestFloor.coerceAtLeast(1))

    Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(
            Modifier
                .width(320.dp)
                .background(BgDarkest)
                .testTag("PathfinderFloorSelector")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🧭 PATHFINDER EXPEDITION", style = PixelHeading, color = GoldBright)
                Text("Select your starting dungeon floor:", style = PixelSmall, color = StoneGray)

                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF140A05), RoundedCornerShape(6.dp))
                        .border(1.5.dp, GoldDark, RoundedCornerShape(6.dp))
                        .padding(12.dp),
                    contentAlignment = Center
                ) {
                    Text("FLOOR $selectedFloor / $maxStartFloor", style = PixelHeading, color = GoldBright, fontSize = 16.sp)
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PixelButton("-5", onClick = { selectedFloor = (selectedFloor - 5).coerceAtLeast(1) }, modifier = Modifier.weight(1f).height(32.dp))
                    PixelButton("-1", onClick = { selectedFloor = (selectedFloor - 1).coerceAtLeast(1) }, modifier = Modifier.weight(1f).height(32.dp))
                    PixelButton("+1", onClick = { selectedFloor = (selectedFloor + 1).coerceAtMost(maxStartFloor) }, modifier = Modifier.weight(1f).height(32.dp))
                    PixelButton("+5", onClick = { selectedFloor = (selectedFloor + 5).coerceAtMost(maxStartFloor) }, modifier = Modifier.weight(1f).height(32.dp))
                }

                PixelDivider()

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PixelButton("CANCEL", onClick = onDismiss, modifier = Modifier.weight(1f).height(40.dp))
                    PixelButton("DEPART ⚔️", onClick = { onConfirm(selectedFloor) }, active = true, modifier = Modifier.weight(1f).height(40.dp))
                }
            }
        }
    }
}

// ============================================================================
// PARTY ROSTER PANEL
// ============================================================================

@Composable
fun PartyPanel(
    modifier: Modifier,
    hiredHeroes: List<Hero>,
    maxPartySize: Int,
    onFire: (String) -> Unit,
    onEquip: (String) -> Unit,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF09121E))
            .border(2.dp, GuildGoldAccent)
            .padding(6.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically
            ) {
                Text(
                    text = safeStringResource(R.string.party_size_format, hiredHeroes.size, maxPartySize),
                    style = PixelHeading,
                    color = GoldBright
                )
            }
            PixelDivider()
            Spacer(Modifier.height(4.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(hiredHeroes) { hero ->
                    PartyMemberCard(
                        hero = hero, 
                        onFire = { onFire(hero.id) }, 
                        onEquip = { onEquip(hero.id) },
                        onMoveUp = { onMoveUp(hero.id) },
                        onMoveDown = { onMoveDown(hero.id) }
                    )
                }
                repeat((maxPartySize - hiredHeroes.size).coerceAtLeast(0)) {
                    item { EmptyPartySlot() }
                }
            }
        }
    }
}

@Composable
fun PartyMemberCard(
    hero: Hero,
    onFire: () -> Unit,
    onEquip: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(78.dp)
            .background(BgMedium, RoundedCornerShape(4.dp))
            .border(1.dp, GoldDark, RoundedCornerShape(4.dp))
            .padding(6.dp)
    ) {
        Row(Modifier.fillMaxSize(), verticalAlignment = CenterVertically) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Box(
                    Modifier
                        .size(18.dp)
                        .background(BgDarkest)
                        .border(1.dp, GoldDark)
                        .clickable { onMoveUp() },
                    contentAlignment = Center
                ) {
                    Text("▲", fontSize = 8.sp, color = GoldBright)
                }
                Box(
                    Modifier
                        .size(18.dp)
                        .background(BgDarkest)
                        .border(1.dp, GoldDark)
                        .clickable { onMoveDown() },
                    contentAlignment = Center
                ) {
                    Text("▼", fontSize = 8.sp, color = GoldBright)
                }
            }

            Spacer(Modifier.width(6.dp))
            HeroSprite(hero.heroClass, Modifier.size(38.dp))
            Spacer(Modifier.width(6.dp))

            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(hero.name, style = PixelBody, color = GoldBright, maxLines = 1, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("LV.${hero.level}", style = PixelSmall, color = SystemCyan, fontSize = 9.sp)
                }
                Spacer(Modifier.height(2.dp))
                PixelHpBar(hero.currentHp, hero.maxHp, Modifier.fillMaxWidth().height(5.dp), barHeight = 5.dp)
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PixelButton(
                        label = safeStringResource(R.string.equip_button),
                        onClick = onEquip,
                        horizontalPadding = 2.dp,
                        verticalPadding = 1.dp,
                        fontSize = 11.sp,
                        modifier = Modifier.height(26.dp).weight(1f)
                    )
                    Box(
                        Modifier
                            .size(26.dp)
                            .background(HpRed, RoundedCornerShape(2.dp))
                            .clickable { onFire() },
                        contentAlignment = Center
                    ) {
                        Text("X", style = PixelSmall, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyPartySlot() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            .border(1.dp, StoneGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
        contentAlignment = Center
    ) {
        Text(safeStringResource(R.string.empty_slot), style = PixelSmall, color = StoneGray.copy(alpha = 0.5f))
    }
}

// ============================================================================
// TOP BAR & DIALOG HELPERS
// ============================================================================

@Composable
fun InnTopBar(
    gil: Long,
    magicite: Int,
    isMuted: Boolean,
    onToggleMusic: () -> Unit,
    onOpenResourceShop: (com.game.dungeon.monetization.ResourceType) -> Unit
) {
    GoldenBorderBox(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(BgDarkest)
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.clickable { onOpenResourceShop(com.game.dungeon.monetization.ResourceType.GIL) },
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🪙", fontSize = 16.sp)
                        Text(formatGold(gil), style = PixelGold, fontWeight = FontWeight.Bold)
                        AdRewardIconButton(
                            isMagicite = false,
                            onClick = { onOpenResourceShop(com.game.dungeon.monetization.ResourceType.GIL) },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.clickable { onOpenResourceShop(com.game.dungeon.monetization.ResourceType.MAGICITE) },
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("💎", fontSize = 16.sp)
                        Text(magicite.toString(), style = PixelGold, fontWeight = FontWeight.Bold)
                        AdRewardIconButton(
                            isMagicite = true,
                            onClick = { onOpenResourceShop(com.game.dungeon.monetization.ResourceType.MAGICITE) },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text("🍺 " + safeStringResource(R.string.building_inn), style = PixelHeading, color = GoldBright)

                MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
            }
        }
    }
}

@Composable
fun DimensionAdvanceBanner(currentDimension: Int, onClick: () -> Unit) {
    val dimension = FFDimensionData.getDimension(currentDimension)
    val nextDimension = FFDimensionData.getDimension(currentDimension + 1)
    val pulseAlpha by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = ""
    )
    GoldenBorderBox(
        Modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(Color(dimension.mainColor).copy(alpha = 0.3f))
            .border(2.dp, GoldBright.copy(alpha = pulseAlpha))
            .clickable { onClick() }
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                safeStringResource(R.string.advance_banner_format, safeStringResource(nextDimension.titleRes)),
                style = PixelSmall,
                color = GoldBright
            )
        }
    }
}

@Composable
fun DimensionResetDialog(gs: GameState, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val currentDimension = gs.currentDimension
    val nextDimension = FFDimensionData.getDimension(currentDimension + 1)
    
    val multiplier = 0.50f + (currentDimension - 1) * 0.10f
    val bonusMagicite = (gs.magiciteEarnedThisDim * multiplier).toInt()
    val goldKept = (gs.gold * gs.pocketsBonus).toLong()

    Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(
            Modifier
                .fillMaxWidth()
                .background(BgDarkest)
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(safeStringResource(R.string.dimension_complete), style = PixelHeading, color = GoldBright)
                Text(safeStringResource(nextDimension.storyRes), style = PixelSmall, color = Color.White, textAlign = TextAlign.Center)
                
                PixelDivider()
                
                Text(safeStringResource(R.string.hall_of_fame), style = PixelBody, color = GoldBright)
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    HallOfFameRow(safeStringResource(R.string.gil_earned_label), formatGold(gs.gilEarnedThisDim))
                    HallOfFameRow(safeStringResource(R.string.magicite_found_label), gs.magiciteEarnedThisDim.toString())
                    HallOfFameRow(safeStringResource(R.string.bosses_slain_label), gs.bossesKilledThisDim.toString())
                    HallOfFameRow(safeStringResource(R.string.items_found_label), gs.itemsFoundThisDim.toString())
                }

                PixelDivider()
                
                Text(safeStringResource(R.string.ascension_rewards), style = PixelBody, color = HpGreen)
                Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("💎", fontSize = 14.sp)
                        Text("+$bonusMagicite", style = PixelGold)
                    }
                    if (goldKept > 0) {
                        Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("🪙", fontSize = 14.sp)
                            Text("+${formatGold(goldKept)}", style = PixelGold)
                        }
                    }
                }
                
                Text(safeStringResource(R.string.dimension_reset_warning), style = PixelSmall, color = StoneGray, textAlign = TextAlign.Center)
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PixelButton(safeStringResource(R.string.cancel_button), onClick = onDismiss, Modifier.weight(1f))
                    PixelButton(safeStringResource(R.string.advance_button), onClick = onConfirm, active = true, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun HallOfFameRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = PixelSmall, color = StoneGray)
        Text(value, style = PixelSmall, color = Color.White)
    }
}

@Composable
fun HiddenJobUnlockDialog(heroClass: HeroClass, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(
            Modifier
                .fillMaxWidth()
                .background(BgDarkest)
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(safeStringResource(R.string.hidden_job_unlock_title), style = PixelHeading, color = GoldBright)
                Spacer(Modifier.height(4.dp))
                HeroSprite(heroClass, Modifier.size(64.dp))
                Spacer(Modifier.height(4.dp))
                Text(
                    safeStringResource(R.string.hidden_job_unlock_body, safeStringResource(heroClass.nameRes)),
                    style = PixelBody,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    safeStringResource(heroClass.descRes),
                    style = PixelSmall,
                    color = StoneGray,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                PixelButton(
                    safeStringResource(R.string.hidden_job_unlock_close),
                    onClick = onDismiss,
                    active = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            }
        }
    }
}
