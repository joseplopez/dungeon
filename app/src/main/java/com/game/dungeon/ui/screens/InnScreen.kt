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
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
    viewModel: InnViewModel = hiltViewModel()
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
            
            // Dimension Advance Button
            if (highestFloor >= 100) {
                val dimension = FFDimensionData.getDimension(currentDimension)
                val nextDimension = FFDimensionData.getDimension(currentDimension + 1)
                val pulseAlpha by rememberInfiniteTransition(label = "").animateFloat(
                    initialValue = 0.6f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
                    label = ""
                )
                GoldenBorderBox(
                    Modifier.fillMaxWidth().height(36.dp)
                        .background(Color(dimension.mainColor).copy(alpha = 0.3f))
                        .border(2.dp, GoldBright.copy(alpha = pulseAlpha))
                        .clickable { showDimensionResetDialog = true }
                ) {
                    Row(Modifier.fillMaxSize().padding(horizontal = 12.dp),
                        verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text("⚡ ADVANCE TO ${nextDimension.title} ⚡", style = PixelSmall, color = GoldBright)
                    }
                }
            }

            Row(Modifier.weight(1f).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HirePanel(
                    modifier = Modifier.weight(0.5f),
                    unlockedJobs = unlockedJobs,
                    gil = gil,
                    partySize = hiredHeroes.size,
                    maxPartySize = maxPartySize,
                    onHire = { viewModel.hireHero(it) }
                )
                Column(Modifier.weight(0.5f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (pathfinderLevel > 0 && highestFloor > 1) {
                        GoldenBorderBox(Modifier.fillMaxWidth().height(60.dp).background(BgDarkest)) {
                            Row(Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalAlignment = CenterVertically) {
                                Text("START FLOOR:", style = PixelSmall, color = GoldBright)
                                Spacer(Modifier.width(12.dp))
                                PixelButton("-5", onClick = { viewModel.setStartFloor(startFloor - 5) }, modifier = Modifier.size(32.dp))
                                Spacer(Modifier.width(4.dp))
                                PixelButton("-1", onClick = { viewModel.setStartFloor(startFloor - 1) }, modifier = Modifier.size(32.dp))
                                
                                Text("$startFloor", style = PixelHeading, color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                
                                PixelButton("+1", onClick = { viewModel.setStartFloor(startFloor + 1) }, modifier = Modifier.size(32.dp))
                                Spacer(Modifier.width(4.dp))
                                PixelButton("+5", onClick = { viewModel.setStartFloor(startFloor + 5) }, modifier = Modifier.size(32.dp))
                            }
                        }
                    }

                    // REST AT INN Button
                    val woundedHeroes = hiredHeroes.filter { it.currentHp < it.maxHp }
                    val restCost = woundedHeroes.sumOf { hero ->
                        val hpMissingRatio = (hero.maxHp - hero.currentHp).toFloat() / hero.maxHp.toFloat()
                        val baseCost = hero.level * 10
                        (baseCost * hpMissingRatio).toLong().coerceAtLeast(1L)
                    }
                    val canAffordRest = gil >= restCost && restCost > 0

                    PixelButton(
                        label = if (restCost > 0) "REST AT INN (${restCost}G)" else "REST AT INN (0G)",
                        onClick = { viewModel.restAtInn() },
                        enabled = canAffordRest,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )

                    PartyPanel(
                        modifier = Modifier.weight(1f),
                        hiredHeroes = hiredHeroes,
                        maxPartySize = maxPartySize,
                        canSendToDungeon = canSendToDungeon,
                        onSend = { 
                            onNavigateToDungeon(hiredHeroes, startFloor)
                            viewModel.resetStartFloor()
                        },
                        onFire = { viewModel.fireHero(it) },
                        onEquip = onNavigateToEquipment,
                        onMoveUp = { viewModel.moveHeroUp(it) },
                        onMoveDown = { viewModel.moveHeroDown(it) }
                    )
                }
            }
            SendToDungeonBar(canSendToDungeon) { 
                onNavigateToDungeon(hiredHeroes, startFloor)
                viewModel.resetStartFloor()
            }
            
            BottomPixelNav(currentRoute, navController)
        }


    }

    if (showDimensionResetDialog) {
        val nextDimension = FFDimensionData.getDimension(currentDimension + 1)
        Dialog(onDismissRequest = { showDimensionResetDialog = false }) {
            GoldenBorderBox(Modifier.fillMaxWidth().background(BgDarkest).padding(16.dp)) {
                Column(horizontalAlignment = CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("ADVANCE DIMENSION?", style = PixelHeading, color = GoldBright)
                    Text(nextDimension.storyIntro, style = PixelSmall, color = Color.White, textAlign = TextAlign.Center)
                    PixelDivider()
                    Text("YOU WILL LOSE:", style = PixelSmall, color = EnemyRed)
                    Text("• All Gil\n• All hired heroes\n• All equipment\n• Floor progress", style = PixelSmall, color = StoneGray)
                    Text("YOU WILL KEEP:", style = PixelSmall, color = HpGreen)
                    Text("• All Relics\n• Unlocked job classes\n• Inn upgrades\n• Magicite", style = PixelSmall, color = StoneGray)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PixelButton("CANCEL", onClick = { showDimensionResetDialog = false }, Modifier.weight(1f))
                        PixelButton("ADVANCE!", onClick = { 
                            viewModel.advanceDimension()
                            showDimensionResetDialog = false 
                        }, active = true, modifier = Modifier.weight(1f))
                    }
                }
            }
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
            Text("Heroes die permanently", style = PixelSmall, color = EnemyRed)
            PixelDivider()
            Spacer(Modifier.height(4.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Always include Freelancer
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
        modifier = Modifier.fillMaxWidth().height(72.dp),
        borderColor = if (canHire) Color(job.crystalColor.colorHex) else StoneGray
    ) {
        Row(Modifier.fillMaxSize().padding(4.dp), verticalAlignment = CenterVertically) {
            HeroSprite(job, Modifier.size(48.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(job.displayName, style = PixelHeading, color = GoldBright)
                Text(job.description, style = PixelBody, color = StoneGray, maxLines = 1)
            }
            PixelButton(
                "${job.hireCost}G",
                onClick = onHire,
                enabled = canHire,
                modifier = Modifier.width(80.dp).fillMaxHeight()
            )
        }
    }
}

@Composable
fun PartyPanel(
    modifier: Modifier,
    hiredHeroes: List<Hero>,
    maxPartySize: Int,
    canSendToDungeon: Boolean,
    onSend: () -> Unit,
    onFire: (String) -> Unit,
    onEquip: (String) -> Unit,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit
) {
    Column(modifier) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = CenterVertically) {
            Text("PARTY (${hiredHeroes.size}/$maxPartySize)", style = PixelHeading)
            PixelButton("SEND ALL", onClick = onSend, enabled = canSendToDungeon)
        }
        PixelDivider()
        Spacer(Modifier.height(4.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(hiredHeroes) { hero ->
                PartyMemberCard(
                    hero = hero, 
                    onFire = { onFire(hero.id) }, 
                    onEquip = { onEquip(hero.id) },
                    onMoveUp = { onMoveUp(hero.id) },
                    onMoveDown = { onMoveDown(hero.id) }
                )
            }
            repeat(maxPartySize - hiredHeroes.size) {
                item { EmptyPartySlot() }
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
    GoldenBorderBox(Modifier.fillMaxWidth().height(120.dp)) {
        Row(Modifier.fillMaxSize().padding(8.dp), verticalAlignment = CenterVertically) {
            // Reorder Arrows
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(Modifier.size(32.dp).background(BgMedium).border(1.dp, GoldDark).clickable { onMoveUp() }, contentAlignment = Center) {
                    Text("▲", fontSize = 16.sp, color = GoldBright)
                }
                Box(Modifier.size(32.dp).background(BgMedium).border(1.dp, GoldDark).clickable { onMoveDown() }, contentAlignment = Center) {
                    Text("▼", fontSize = 16.sp, color = GoldBright)
                }
            }
            
            Spacer(Modifier.width(8.dp))
            HeroSprite(hero.heroClass, Modifier.size(56.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(hero.name, style = PixelBody, color = GoldBright)
                    Text(hero.heroClass.displayName, style = PixelSmall, color = GoldDark)
                }
                PixelHpBar(hero.currentHp, hero.maxHp, Modifier.fillMaxWidth().height(8.dp))
                Spacer(Modifier.height(6.dp))
                PixelExpBar(hero.exp, hero.expToNextLevel, Modifier.fillMaxWidth().height(6.dp))
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    Text("LVL:${hero.level}", style = PixelSmall, color = SystemCyan)
                    PixelButton(
                        label = "EQUIP",
                        onClick = onEquip,
                        modifier = Modifier.width(80.dp).height(32.dp)
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier.size(32.dp).background(HpRed).clickable { onFire() },
                contentAlignment = Center
            ) {
                Text("✕", style = PixelBody, color = Color.White)
            }
        }
    }
}

@Composable
fun EmptyPartySlot() {
    PixelPanel(
        Modifier.fillMaxWidth().height(80.dp),
        borderColor = StoneGray.copy(alpha = 0.5f),
        bgColor = Color.Black.copy(alpha = 0.2f)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Center) {
            Text("EMPTY SLOT", style = PixelSmall, color = StoneGray)
        }
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
                        Text("$magicite", style = PixelGold)
                    }
                }
                
                Text("THE INN", style = PixelHeading)

                MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
            }
        }
    }
}

@Composable
fun SendToDungeonBar(canSend: Boolean, onClick: () -> Unit) {
    GoldenBorderBox(Modifier.fillMaxWidth().height(48.dp)) {
        Box(
            Modifier.fillMaxSize()
                .background(if (canSend) GoldBright else StoneGray)
                .clickable(enabled = canSend) { onClick() },
            contentAlignment = Center
        ) {
            Text(
                if (canSend) "⚔ ENTER THE DUNGEON ⚔" else "HIRE WARRIORS FIRST",
                style = PixelHeading.copy(color = if (canSend) BgDarkest else Color.DarkGray)
            )
        }
    }
}
