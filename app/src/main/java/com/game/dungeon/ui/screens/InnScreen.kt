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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.game.dungeon.data.models.*
import com.game.dungeon.ui.components.*
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.InnViewModel

@Composable
fun InnScreen(
    onNavigateToDungeon: (List<Hero>, Int) -> Unit,
    onNavigateToEquipment: (String) -> Unit,
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showDimensionResetDialog by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        InnBackground()
        Column(Modifier.fillMaxSize()) {
            InnTopBar(gil, magicite, isMuted, onToggleMusic)
            
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
                        }
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
            Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("⚡ ADVANCE TO ${nextDimension.title} ⚡", style = PixelSmall, color = GoldBright)
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
    onSend: () -> Unit
) {
    // Top: Pathfinder Floor Selector
    if ((pathfinderLevel > 0) && (highestFloor > 1)) {
        GoldenBorderBox(Modifier.fillMaxWidth().height(64.dp).background(BgDarkest)) {
            Row(Modifier.fillMaxSize().padding(horizontal = 6.dp), verticalAlignment = CenterVertically) {
                Text("FLOOR:", style = PixelSmall, color = GoldBright)
                Spacer(Modifier.width(4.dp))
                PixelButton("- 5", onClick = { onSetStartFloor(startFloor - 5) }, horizontalPadding = 4.dp, modifier = Modifier.width(40.dp).height(32.dp))
                Spacer(Modifier.width(2.dp))
                PixelButton("- 1", onClick = { onSetStartFloor(startFloor - 1) }, horizontalPadding = 4.dp, modifier = Modifier.width(40.dp).height(32.dp))
                
                Text(startFloor.toString(), style = PixelHeading, color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                
                PixelButton("+ 1", onClick = { onSetStartFloor(startFloor + 1) }, horizontalPadding = 4.dp, modifier = Modifier.width(40.dp).height(32.dp))
                Spacer(Modifier.width(2.dp))
                PixelButton("+ 5", onClick = { onSetStartFloor(startFloor + 5) }, horizontalPadding = 4.dp, modifier = Modifier.width(40.dp).height(32.dp))
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

        PixelButton(
            label = if (restCost > 0) "REST AT INN (${restCost}G)" else "REST AT INN (0G)",
            onClick = onRest,
            enabled = canAffordRest || (restCost == 0L && hiredHeroes.isNotEmpty() && woundedHeroes.isNotEmpty()),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        )

        PixelButton(
            label = if (canSend) "⚔ ENTER DUNGEON ⚔" else "NEED WARRIORS",
            onClick = onSend,
            enabled = canSend,
            active = canSend,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )
        
        if (woundedHeroes.isEmpty() && hiredHeroes.isNotEmpty()) {
            Text("Party is fully rested", style = PixelSmall, color = HpGreen, modifier = Modifier.padding(top = 2.dp))
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
            Text("HIRE WARRIORS", style = PixelHeading)
            Text("Permanent death active", style = PixelSmall, color = EnemyRed)
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
        modifier = Modifier.fillMaxWidth().height(80.dp),
        borderColor = if (canHire) Color(job.crystalColor.colorHex) else StoneGray
    ) {
        Row(Modifier.fillMaxSize().padding(4.dp), verticalAlignment = CenterVertically) {
            HeroSprite(job, Modifier.size(40.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(job.displayName, style = PixelBody, color = GoldBright, fontWeight = FontWeight.Bold)
                Text(
                    job.description, 
                    style = PixelSmall, 
                    color = StoneGray
                )
            }
            PixelButton(
                "${job.hireCost}G",
                onClick = onHire,
                enabled = canHire,
                modifier = Modifier.width(60.dp).fillMaxHeight()
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
                Text("PARTY (${hiredHeroes.size}/$maxPartySize)", style = PixelHeading)
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
        modifier = Modifier.fillMaxWidth().height(90.dp),
        borderColor = GoldDark
    ) {
        Row(Modifier.fillMaxSize().padding(4.dp), verticalAlignment = CenterVertically) {
            // Reorder Arrows
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Box(Modifier.size(24.dp).background(BgMedium).border(1.dp, GoldDark).clickable { onMoveUp() }, contentAlignment = Center) {
                    Text("▲", fontSize = 12.sp, color = GoldBright)
                }
                Box(Modifier.size(24.dp).background(BgMedium).border(1.dp, GoldDark).clickable { onMoveDown() }, contentAlignment = Center) {
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
                
                PixelHpBar(hero.currentHp, hero.maxHp, Modifier.fillMaxWidth().height(6.dp), barHeight = 6.dp)
                Spacer(Modifier.height(4.dp))
                
                Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PixelButton("EQUIP", onClick = onEquip, modifier = Modifier.height(30.dp).weight(1f))
                    Box(
                        Modifier.size(26.dp).background(HpRed).clickable { onFire() },
                        contentAlignment = Center
                    ) {
                        Text("✕", style = PixelSmall, color = Color.White)
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
        Text("EMPTY SLOT", style = PixelSmall, color = StoneGray.copy(alpha = 0.5f))
    }
}

@Composable
fun InnTopBar(gil: Long, magicite: Int, isMuted: Boolean, onToggleMusic: () -> Unit) {
    GoldenBorderBox(Modifier.fillMaxWidth().height(56.dp)) {
        Box(Modifier.fillMaxSize().background(BgDarkest)) {
            Row(
                Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("🪙", fontSize = 18.sp)
                        Text(formatGold(gil), style = PixelGold)
                    }
                    Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("💎", fontSize = 18.sp)
                        Text(magicite.toString(), style = PixelGold)
                    }
                }
                
                Text("THE INN", style = PixelHeading)

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
        GoldenBorderBox(Modifier.fillMaxWidth().background(BgDarkest).padding(16.dp)) {
            Column(horizontalAlignment = CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("DIMENSION COMPLETE!", style = PixelHeading, color = GoldBright)
                Text(nextDimension.storyIntro, style = PixelSmall, color = Color.White, textAlign = TextAlign.Center)
                
                PixelDivider()
                
                Text("HALL OF FAME", style = PixelBody, color = GoldBright)
                Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    HallOfFameRow("Gil Earned", formatGold(gs.gilEarnedThisDim))
                    HallOfFameRow("Magicite Found", gs.magiciteEarnedThisDim.toString())
                    HallOfFameRow("Bosses Slain", gs.bossesKilledThisDim.toString())
                    HallOfFameRow("Items Found", gs.itemsFoundThisDim.toString())
                }

                PixelDivider()
                
                Text("ASCENSION REWARDS", style = PixelBody, color = HpGreen)
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
                
                Text("Warning: Resets heroes, equipment and current floor.", style = PixelSmall, color = StoneGray, textAlign = TextAlign.Center)
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PixelButton("CANCEL", onClick = onDismiss, Modifier.weight(1f))
                    PixelButton("ADVANCE!", onClick = onConfirm, active = true, modifier = Modifier.weight(1f))
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
