package com.game.dungeon.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.game.dungeon.R
import com.game.dungeon.data.models.*
import com.game.dungeon.ui.components.*
import com.game.dungeon.ui.theme.*
import kotlin.math.sin

// ============================================================================
// REFINED OPTION 3 PREVIEW
// ============================================================================


// ============================================================================
// REFINED OPTION 3: Immersive Guild Stage & Pathfinder Launch Dialog
// Scrollable multi-tiered candidate floor supporting 10+ unlocked jobs cleanly
// ============================================================================

@Composable
fun RefinedOption3GuildStage(
    hiredHeroes: List<Hero>,
    unlockedJobs: List<HeroClass>,
    gil: Long = 1250L,
    magicite: Int = 45,
    pathfinderLevel: Int = 2,
    highestFloor: Int = 40,
    maxPartySize: Int = 4
) {
    var selectedHireJob by remember { mutableStateOf<HeroClass?>(null) }
    var showPathfinderDialog by remember { mutableStateOf(false) }
    var chosenStartFloor by remember { mutableIntStateOf(10) }

    Box(
        Modifier
            .fillMaxSize()
            .background(GuildRoyalBlue)
            .padding(6.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            // Top Bar
            OptionTopBar(gil = gil, magicite = magicite)

            Spacer(Modifier.height(6.dp))

            Row(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // LEFT (70% width): Guild Stage with Multi-Tiered / Scrollable Hero Candidates
                Box(
                    Modifier
                        .weight(0.70f)
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
                            Text("🛡️ ADVENTURERS GUILD HALL", style = PixelHeading, color = GuildGoldAccent)
                            Text("TAP A HERO TO HIRE (${unlockedJobs.size} UNLOCKED)", style = PixelSmall, color = StoneGray)
                        }

                        Spacer(Modifier.height(4.dp))

                        // Tavern Stage Box with Horizontal Scroll for 10+ Candidates
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

                            // Moving candidate sprites animation
                            val infiniteTransition = rememberInfiniteTransition(label = "patrol")
                            val walkTime by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 6.28f,
                                animationSpec = infiniteRepeatable(tween(4500, easing = LinearEasing)),
                                label = "walk"
                            )

                            val scrollState = rememberScrollState()

                            // Multi-row horizontal scrollable stage floor
                            Row(
                                Modifier
                                    .fillMaxSize()
                                    .horizontalScroll(scrollState)
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(28.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                unlockedJobs.forEachIndexed { index, job ->
                                    // Stagger offset across 2 virtual floor rows
                                    val rowTier = index % 2
                                    val offsetY = (sin(walkTime + index * 1.4f) * 6).dp - (rowTier * 22).dp

                                    Column(
                                        Modifier
                                            .offset(y = offsetY)
                                            .clickable { selectedHireJob = job }
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

                        // Actions Row - Generous height & clean padding to prevent clipping
                        val woundedHeroes = hiredHeroes.filter { it.currentHp < it.maxHp }
                        val restCost = woundedHeroes.sumOf { (it.level * 10 * ((it.maxHp - it.currentHp).toFloat() / it.maxHp)).toLong().coerceAtLeast(1L) }

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            PixelButton(
                                label = "MASTERIES 📈",
                                onClick = {},
                                horizontalPadding = 4.dp,
                                modifier = Modifier.weight(1f).height(42.dp)
                            )
                            PixelButton(
                                label = if (restCost > 0) "REST (${restCost}G) 🛌" else "REST (FREE) 🛌",
                                onClick = {},
                                horizontalPadding = 4.dp,
                                modifier = Modifier.weight(1f).height(42.dp)
                            )
                            PixelButton(
                                label = "DEPART EXPEDITION ⚔️",
                                onClick = {
                                    if (pathfinderLevel > 0) {
                                        showPathfinderDialog = true
                                    }
                                },
                                active = hiredHeroes.isNotEmpty(),
                                enabled = hiredHeroes.isNotEmpty(),
                                horizontalPadding = 4.dp,
                                modifier = Modifier.weight(1.3f).height(42.dp)
                            )
                        }
                    }
                }

                // RIGHT (30% width): Squad Roster Panel
                Box(
                    Modifier
                        .weight(0.30f)
                        .fillMaxSize()
                        .background(Color(0xFF09121E))
                        .border(2.dp, GuildGoldAccent)
                        .padding(6.dp)
                ) {
                    OptionPartyPanelContent(
                        hiredHeroes = hiredHeroes,
                        maxPartySize = maxPartySize
                    )
                }
            }
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
            onHire = {}
        )
    }

    // Pathfinder Selector Popup on Expedition Launch
    if (showPathfinderDialog) {
        PathfinderFloorSelectDialog(
            pathfinderLevel = pathfinderLevel,
            highestFloor = highestFloor,
            initialStartFloor = chosenStartFloor,
            onConfirm = { floor ->
                chosenStartFloor = floor
                showPathfinderDialog = false
            },
            onDismiss = { showPathfinderDialog = false }
        )
    }
}


// ============================================================================
// SHARED COMPONENTS & PREVIEWS
// ============================================================================

@Composable
private fun OptionTopBar(gil: Long, magicite: Int) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(BgDarkest)
            .border(2.dp, GoldBright)
            .padding(horizontal = 12.dp)
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("🪙", fontSize = 16.sp)
                    Text(formatGold(gil), style = PixelGold, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("💎", fontSize = 16.sp)
                    Text(magicite.toString(), style = PixelGold, fontWeight = FontWeight.Bold)
                }
            }

            Text("🍺 " + safeStringResource(R.string.building_inn), style = PixelHeading, color = GoldBright)

            MusicToggleButton(isMuted = false, onToggle = {})
        }
    }
}

@Composable
private fun OptionPartyPanelContent(
    hiredHeroes: List<Hero>,
    maxPartySize: Int
) {
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Text(safeStringResource(R.string.party_size_format, hiredHeroes.size, maxPartySize), style = PixelHeading, color = GoldBright)
        }
        PixelDivider()
        Spacer(Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(hiredHeroes) { hero ->
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
                                    .clickable { },
                                contentAlignment = Center
                            ) { Text("▲", fontSize = 8.sp, color = GoldBright) }
                            Box(
                                Modifier
                                    .size(18.dp)
                                    .background(BgDarkest)
                                    .border(1.dp, GoldDark)
                                    .clickable { },
                                contentAlignment = Center
                            ) { Text("▼", fontSize = 8.sp, color = GoldBright) }
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
                                    onClick = {},
                                    horizontalPadding = 2.dp,
                                    verticalPadding = 1.dp,
                                    fontSize = 11.sp,
                                    modifier = Modifier.height(26.dp).weight(1f)
                                )
                                Box(
                                    Modifier
                                        .size(26.dp)
                                        .background(HpRed, RoundedCornerShape(2.dp))
                                        .clickable { },
                                    contentAlignment = Center
                                ) { Text("X", style = PixelSmall, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            }
                        }
                    }
                }
            }

            repeat((maxPartySize - hiredHeroes.size).coerceAtLeast(0)) {
                item {
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
            }
        }
    }
}


// --- Sample Data ---
private val SampleHiredHeroes = listOf(
    Hero(id = "1", heroClass = HeroClass.FREELANCER, name = "Tidus", currentHp = 120, currentMp = 30, level = 5, aiPriority = AIPriority.ATTACK),
    Hero(id = "2", heroClass = HeroClass.BLACK_MAGE, name = "Vivi", currentHp = 80, currentMp = 70, level = 4, aiPriority = AIPriority.MAGIC)
)

private val Sample12UnlockedJobs = listOf(
    HeroClass.FREELANCER,
    HeroClass.WARRIOR,
    HeroClass.BLACK_MAGE,
    HeroClass.WHITE_MAGE,
    HeroClass.THIEF,
    HeroClass.MONK,
    HeroClass.KNIGHT,
    HeroClass.PALADIN,
    HeroClass.RED_MAGE,
    HeroClass.SUMMONER,
    HeroClass.NINJA,
    HeroClass.DRAGOON
)

@Preview(name = "Refined Option 3 - Guild Stage (12 Unlocked Jobs)", widthDp = 640, heightDp = 320)
@Composable
fun PreviewRefinedOption3GuildStage() {
    PixelTheme {
        RefinedOption3GuildStage(
            hiredHeroes = SampleHiredHeroes,
            unlockedJobs = Sample12UnlockedJobs
        )
    }
}
