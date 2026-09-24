package com.game.dungeon.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.game.dungeon.ui.components.safeStringResource
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

    Box(Modifier.fillMaxSize()) {
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
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // LEFT: Hire Panel
                HirePanel(
                    modifier = Modifier.weight(0.3f),
                    unlockedJobs = unlockedJobs,
                    gil = gil,
                    partySize = hiredHeroes.size,
                    maxPartySize = maxPartySize
                ) { viewModel.hireHero(it) }

                // CENTER: Inn Hub Area
                Column(
                    modifier = Modifier.weight(0.35f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = CenterHorizontally
                ) {
                    HubArea(
                        pathfinderLevel = pathfinderLevel,
                        highestFloor = highestFloor,
                        startFloor = startFloor,
                        gil = gil,
                        hiredHeroes = hiredHeroes,
                        canSend = canSendToDungeon,
                        onSetStartFloor = { viewModel.setStartFloor(it) },
                        onRest = { viewModel.restAtInn() },
                        onSend = {
                            onNavigateToDungeon(hiredHeroes, startFloor)
                            viewModel.resetStartFloor()
                        },
                        onNavigateToMastery = onNavigateToMastery
                    )
                }

                // RIGHT: Party Panel
                PartyPanel(
                    modifier = Modifier.weight(0.35f),
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
            .height(36.dp)
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
            Text(safeStringResource(R.string.advance_banner_format, safeStringResource(nextDimension.titleRes)), style = PixelSmall, color = GoldBright)
        }
    }
}

@Composable
fun ColumnScope.HubArea(
    pathfinderLevel: Int,
    highestFloor: Int,
    startFloor: Int,
    gil: Long,
    hiredHeroes: List<Hero>,
    canSend: Boolean,
    onSetStartFloor: (Int) -> Unit,
    onRest: () -> Unit,
    onSend: () -> Unit,
    onNavigateToMastery: () -> Unit
) {
    // Top: Pathfinder Floor Selector
    if (pathfinderLevel > 0) {
        val maxStartFloor = (highestFloor * (pathfinderLevel * 0.25f)).toInt().coerceIn(1, highestFloor.coerceAtLeast(1))
        GoldenBorderBox(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(BgDarkest)
                .testTag("PathfinderFloorSelector")
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "🧭 ${safeStringResource(R.string.floor_label)} $startFloor / $maxStartFloor",
                    style = PixelBody,
                    color = GoldBright,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = CenterVertically
                ) {
                    PixelButton(
                        "-5",
                        onClick = { onSetStartFloor(startFloor - 5) },
                        enabled = startFloor > 1,
                        horizontalPadding = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                    )
                    PixelButton(
                        "-1",
                        onClick = { onSetStartFloor(startFloor - 1) },
                        enabled = startFloor > 1,
                        horizontalPadding = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                    )
                    PixelButton(
                        "+1",
                        onClick = { onSetStartFloor(startFloor + 1) },
                        enabled = startFloor < maxStartFloor,
                        horizontalPadding = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                    )
                    PixelButton(
                        "+5",
                        onClick = { onSetStartFloor(startFloor + 5) },
                        enabled = startFloor < maxStartFloor,
                        horizontalPadding = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                    )
                }
            }
        }
    }

    // Center: Visual Hub
    Box(
        Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Center
    ) {
        Canvas(Modifier.size(160.dp)) {
            drawDetailedInn()
        }
        
        // Potential NPC or small detail
        HeroSprite(
            heroClass = HeroClass.FREELANCER,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-20).dp)
                .size(32.dp)
        )
    }

    // Bottom: Hub Actions (Rest & Enter Dungeon)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        val woundedHeroes = hiredHeroes.filter { it.currentHp < it.maxHp }
        val restCost = woundedHeroes.sumOf { hero ->
            val hpMissingRatio = (hero.maxHp - hero.currentHp).toFloat() / hero.maxHp.toFloat()
            val baseCost = hero.level * 10
            (baseCost * hpMissingRatio).toLong().coerceAtLeast(1L)
        }
        val canAffordRest = restCost in 1..gil

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PixelButton(
                label = safeStringResource(R.string.masteries),
                onClick = onNavigateToMastery,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            )
            PixelButton(
                label = if (restCost > 0) safeStringResource(R.string.rest_at_inn_cost, restCost) else safeStringResource(R.string.rest_at_inn_free),
                onClick = onRest,
                enabled = canAffordRest || (restCost == 0L && hiredHeroes.isNotEmpty() && woundedHeroes.isNotEmpty()),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            )
        }

        PixelButton(
            label = if (canSend) safeStringResource(R.string.enter_dungeon) else safeStringResource(R.string.need_warriors),
            onClick = onSend,
            enabled = canSend,
            active = canSend,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
        
        if (woundedHeroes.isEmpty() && hiredHeroes.isNotEmpty()) {
            Text(safeStringResource(R.string.fully_rested), style = PixelSmall, color = HpGreen, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
fun HirePanel(
    modifier: Modifier,
    unlockedJobs: List<HeroClass>,
    gil: Long,
    partySize: Int,
    maxPartySize: Int,
    onHire: (HeroClass) -> Unit
) {
    GoldenBorderBox(modifier.fillMaxSize()) {
        Column(Modifier.padding(8.dp)) {
            Text(safeStringResource(R.string.hire_warriors), style = PixelHeading)
            Text(safeStringResource(R.string.permadeath_warning), style = PixelSmall, color = EnemyRed)
            PixelDivider()
            Spacer(Modifier.height(4.dp))
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(unlockedJobs) { job ->
                    HireJobRow(job, gil, partySize, maxPartySize, onHire = { onHire(job) })
                }
            }
        }
    }
}

@Composable
fun HireJobRow(
    job: HeroClass,
    gil: Long,
    partySize: Int,
    maxPartySize: Int,
    onHire: () -> Unit
) {
    val canHire = gil >= job.hireCost && partySize < maxPartySize
    PixelPanel(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .testTag("HireRow_${job.name}"),
        borderColor = if (canHire) Color(job.crystalColor.colorHex) else StoneGray
    ) {
        Row(Modifier
            .fillMaxSize()
            .padding(4.dp), verticalAlignment = CenterVertically) {
            HeroSprite(job, Modifier.size(40.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(safeStringResource(job.nameRes), style = PixelBody, color = GoldBright, fontWeight = FontWeight.Bold)
                Text(
                    safeStringResource(job.descRes), 
                    style = PixelSmall, 
                    color = StoneGray
                )
            }
            PixelButton(
                "${job.hireCost}G",
                onClick = onHire,
                enabled = canHire,
                modifier = Modifier
                    .width(60.dp)
                    .fillMaxHeight()
            )
        }
    }
}

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
    GoldenBorderBox(modifier.fillMaxSize()) {
        Column(Modifier.padding(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically
            ) {
                Text(safeStringResource(R.string.party_size_format, hiredHeroes.size, maxPartySize), style = PixelHeading)
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
    PixelPanel(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        borderColor = GoldDark
    ) {
        Row(Modifier
            .fillMaxSize()
            .padding(4.dp), verticalAlignment = CenterVertically) {
            // Reorder Arrows
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Box(Modifier
                    .size(24.dp)
                    .background(BgMedium)
                    .border(1.dp, GoldDark)
                    .clickable { onMoveUp() }, contentAlignment = Center) {
                    Text("▲", fontSize = 12.sp, color = GoldBright)
                }
                Box(Modifier
                    .size(24.dp)
                    .background(BgMedium)
                    .border(1.dp, GoldDark)
                    .clickable { onMoveDown() }, contentAlignment = Center) {
                    Text("▼", fontSize = 12.sp, color = GoldBright)
                }
            }
            
            Spacer(Modifier.width(8.dp))
            HeroSprite(hero.heroClass, Modifier.size(48.dp))
            Spacer(Modifier.width(8.dp))
            
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(hero.name, style = PixelBody, color = GoldBright, maxLines = 1)
                    Text("LV.${hero.level}", style = PixelSmall, color = SystemCyan)
                }
                
                PixelHpBar(hero.currentHp, hero.maxHp, Modifier
                    .fillMaxWidth()
                    .height(6.dp), barHeight = 6.dp)
                Spacer(Modifier.height(4.dp))
                
                Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PixelButton(safeStringResource(R.string.equip_button), onClick = onEquip, modifier = Modifier
                        .height(30.dp)
                        .weight(1f))
                    Box(
                        Modifier
                            .size(26.dp)
                            .background(HpRed)
                            .clickable { onFire() },
                        contentAlignment = Center
                    ) {
                        Text(
                            "X", 
                            style = PixelSmall.copy(textAlign = TextAlign.Center),
                            color = Color.White
                        )
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
            .height(40.dp)
            .background(Color.Black.copy(alpha = 0.1f))
            .border(1.dp, StoneGray.copy(alpha = 0.3f)),
        contentAlignment = Center
    ) {
        Text(safeStringResource(R.string.empty_slot), style = PixelSmall, color = StoneGray.copy(alpha = 0.5f))
    }
}

@Composable
fun InnTopBar(
    gil: Long,
    magicite: Int,
    isMuted: Boolean,
    onToggleMusic: () -> Unit,
    onOpenResourceShop: (com.game.dungeon.monetization.ResourceType) -> Unit
) {
    GoldenBorderBox(Modifier
        .fillMaxWidth()
        .height(56.dp)) {
        Box(Modifier
            .fillMaxSize()
            .background(BgDarkest)) {
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
                        Text("🪙", fontSize = 18.sp)
                        Text(formatGold(gil), style = PixelGold)
                        AdRewardIconButton(
                            isMagicite = false,
                            onClick = { onOpenResourceShop(com.game.dungeon.monetization.ResourceType.GIL) },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.clickable { onOpenResourceShop(com.game.dungeon.monetization.ResourceType.MAGICITE) },
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("💎", fontSize = 18.sp)
                        Text(magicite.toString(), style = PixelGold)
                        AdRewardIconButton(
                            isMagicite = true,
                            onClick = { onOpenResourceShop(com.game.dungeon.monetization.ResourceType.MAGICITE) },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Text(safeStringResource(R.string.building_inn), style = PixelHeading)

                MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
            }
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
        GoldenBorderBox(Modifier
            .fillMaxWidth()
            .background(BgDarkest)
            .padding(16.dp)) {
            Column(horizontalAlignment = CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(safeStringResource(R.string.dimension_complete), style = PixelHeading, color = GoldBright)
                Text(safeStringResource(nextDimension.storyRes), style = PixelSmall, color = Color.White, textAlign = TextAlign.Center)
                
                PixelDivider()
                
                Text(safeStringResource(R.string.hall_of_fame), style = PixelBody, color = GoldBright)
                Column(Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
        GoldenBorderBox(Modifier
            .fillMaxWidth()
            .background(BgDarkest)
            .padding(16.dp)) {
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
                PixelButton(safeStringResource(R.string.hidden_job_unlock_close), onClick = onDismiss, active = true, modifier = Modifier.fillMaxWidth().height(48.dp))
            }
        }
    }
}
