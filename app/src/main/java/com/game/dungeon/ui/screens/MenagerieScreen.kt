package com.game.dungeon.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.game.dungeon.R
import com.game.dungeon.data.models.*
import com.game.dungeon.ui.components.*
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.InnViewModel

@Composable
fun MenagerieScreen(
    navController: NavController,
    isMuted: Boolean,
    onToggleMusic: () -> Unit,
    onStartSafariRun: (List<Hero>) -> Unit,
    viewModel: InnViewModel = hiltViewModel()
) {
    val gs by viewModel.gameState.collectAsState()
    val activeParty by viewModel.activeParty.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    var selectedTab by remember { mutableIntStateOf(0) }
    var showSupportDialog by remember { mutableStateOf(false) }

    val state = gs ?: GameState()
    val isSafariUnlocked = state.isSafariUnlocked()

    Box(
        Modifier
            .fillMaxSize()
            .background(BgDarkest)
            .testTag("MenagerieScreen")
    ) {
        Column(Modifier.fillMaxSize()) {
            // Header
            GoldenBorderBox(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(BgDarkest)
            ) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PixelButton(
                        label = safeStringResource(R.string.back_button),
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.height(36.dp)
                    )
                    Text("🐾 MENAGERIE", style = PixelHeading, color = GoldBright)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SupportIconButton(onClick = { showSupportDialog = true })
                        MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
                    }
                }
            }

            // Top Section: Currently Equipped Pets & Safari Ticket Actions
            MenagerieTopSection(
                gs = state,
                canStartSafari = (isSafariUnlocked && state.safariTickets >= 1 && activeParty.isNotEmpty()),
                onWatchAd = { activity?.let { viewModel.watchSafariTicketAd(it) } },
                onBuyMiscPack = { activity?.let { viewModel.buyMiscPack(it) } },
                onEnterSafari = {
                    if (viewModel.consumeSafariTicketForRun()) {
                        onStartSafariRun(activeParty)
                    }
                }
            )

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BgMedium,
                contentColor = GoldBright,
                divider = { PixelDivider() }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Tier 1 (Passives)", style = PixelBody, color = if (selectedTab == 0) GoldBright else GoldDark) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Tier 2 (Cards)", style = PixelBody, color = if (selectedTab == 1) GoldBright else GoldDark) }
                )
            }

            Box(
                Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                when (selectedTab) {
                    0 -> Tier1PassivesTab(
                        gs = state,
                        onSelect = { viewModel.selectPet(it) },
                        onUnlock = { viewModel.unlockPet(it) }
                    )
                    1 -> Tier2CardsTab(
                        gs = state,
                        isSafariUnlocked = isSafariUnlocked,
                        onEquipTier2 = { petId -> viewModel.equipTier2Pet(petId) },
                        onRankUpTier2 = { petId -> viewModel.rankUpTier2Pet(petId) }
                    )
                }
            }

            BottomPixelNav(navController.currentBackStackEntry?.destination?.route, navController)
        }

        if (showSupportDialog) {
            SupportDialog(onDismiss = { showSupportDialog = false })
        }
    }
}

@Composable
fun MenagerieTopSection(
    gs: GameState,
    canStartSafari: Boolean,
    onWatchAd: () -> Unit,
    onBuyMiscPack: () -> Unit,
    onEnterSafari: () -> Unit
) {
    GoldenBorderBox(
        Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .background(BgMedium)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tier 1 Equipped
                val t1Name = gs.selectedPet?.let { safeStringResource(it.nameRes) } ?: "None"
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Tier 1:", style = PixelSmall, color = StoneGray)
                    Text(t1Name, style = PixelSmall, color = GoldBright, fontWeight = FontWeight.Bold)
                }

                // Tier 2 Equipped
                val t2Pet = gs.equippedTier2PetId?.let { gs.tier2Pets[it] }
                val t2Name = gs.equippedTier2PetId?.let { id ->
                    PetType.entries.find { it.name == id }?.let { safeStringResource(it.nameRes) } ?: id
                } ?: "None"
                val t2Mult = t2Pet?.let { "${((it.getStatMultiplier() - 1f) * 100f).toInt()}%" } ?: "0%"
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Tier 2:", style = PixelSmall, color = StoneGray)
                    Text("$t2Name (+$t2Mult)", style = PixelSmall, color = SystemCyan, fontWeight = FontWeight.Bold)
                }

                // Tickets count
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("🎟️ Tickets:", style = PixelSmall, color = GoldBright)
                    Text(gs.safariTickets.toString(), style = PixelHeading, color = GoldBright)
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PixelButton(
                    label = "📺 Watch Ad (+1)",
                    onClick = onWatchAd,
                    horizontalPadding = 2.dp,
                    fontSize = 8.sp,
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                )
                PixelButton(
                    label = "💰 Buy Pack (+10)",
                    onClick = onBuyMiscPack,
                    horizontalPadding = 2.dp,
                    fontSize = 8.sp,
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                )
                PixelButton(
                    label = "🦁 Enter Safari (1 Ticket)",
                    onClick = onEnterSafari,
                    enabled = canStartSafari,
                    active = canStartSafari,
                    horizontalPadding = 2.dp,
                    fontSize = 8.sp,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(32.dp)
                )
            }
        }
    }
}

@Composable
fun Tier1PassivesTab(
    gs: GameState,
    onSelect: (PetType?) -> Unit,
    onUnlock: (PetType) -> Unit
) {
    PetsTab(gs = gs, onSelect = onSelect, onUnlock = onUnlock)
}

@Composable
fun Tier2CardsTab(
    gs: GameState,
    isSafariUnlocked: Boolean,
    onEquipTier2: (String?) -> Unit,
    onRankUpTier2: (String) -> Unit
) {
    if (!isSafariUnlocked) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("🔒 SAFARI DIMENSION LOCKED", style = PixelHeading, color = EnemyRed, fontSize = 16.sp)
                Text(
                    "Reach Dimension 11 & Lifetime Floor 1000 to unlock Tier 2 Companion Cards.",
                    style = PixelBody,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(PetType.entries) { petType ->
                val petId = petType.name
                val tier2Data = gs.tier2Pets[petId] ?: Tier2Pet(petId = petId, rarity = petType.rarity)
                val isEquipped = gs.equippedTier2PetId == petId
                val tier1Level = gs.getPetLevel(petType)
                val isTier1Locked = tier1Level < 50

                Tier2CardItem(
                    petType = petType,
                    tier2Pet = tier2Data,
                    isEquipped = isEquipped,
                    isTier1Locked = isTier1Locked,
                    onEquip = { onEquipTier2(if (isEquipped) null else petId) },
                    onRankUp = { onRankUpTier2(petId) }
                )
            }
        }
    }
}

@Composable
fun Tier2CardItem(
    petType: PetType,
    tier2Pet: Tier2Pet,
    isEquipped: Boolean,
    isTier1Locked: Boolean,
    onEquip: () -> Unit,
    onRankUp: () -> Unit
) {
    val rarityColor = when (petType.rarity) {
        Rarity.COMMON -> Color(0xFF2E3A2E)
        Rarity.RARE -> Color(0xFF1E2E4A)
        Rarity.EPIC -> Color(0xFF3E1E4A)
        Rarity.LEGENDARY -> Color(0xFF4A3A1E)
        Rarity.MYTHIC -> Color(0xFF4A1E2E)
    }

    val neededCards = tier2Pet.getCardsNeededForNextRank()
    val canRankUp = tier2Pet.canRankUp()

    Box(
        Modifier
            .fillMaxWidth()
            .height(175.dp)
            .background(rarityColor, RoundedCornerShape(6.dp))
            .border(2.dp, if (isEquipped) GoldBright else GoldDark, RoundedCornerShape(6.dp))
            .padding(6.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Left Hexagon Rank, Right Cards Progress Pill
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Top-Left Hexagon Rank Badge
                Box(
                    Modifier
                        .size(24.dp)
                        .background(GoldDark, RoundedCornerShape(4.dp))
                        .border(1.dp, GoldBright, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "R${tier2Pet.currentRank}",
                        style = PixelSmall,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Top-Right Cards Pill
                val cardsText = if (tier2Pet.currentRank >= 5) "MAX" else "${tier2Pet.cardsCollected}/$neededCards"
                Box(
                    Modifier
                        .background(BgDarkest, RoundedCornerShape(10.dp))
                        .border(1.dp, SystemCyan, RoundedCornerShape(10.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        cardsText,
                        style = PixelSmall,
                        color = SystemCyan,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Center Sprite
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                PetSprite(petType, Modifier.size(44.dp))
            }

            // Bottom Name Banner & Stat Boost
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    safeStringResource(petType.nameRes),
                    style = PixelBody,
                    color = GoldBright,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "+${tier2Pet.currentRank}% Stat Multiplier",
                    style = PixelSmall,
                    color = HpGreen,
                    fontSize = 8.sp
                )

                Spacer(Modifier.height(4.dp))

                // Actions Row: Upgrade / Equip
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (canRankUp) {
                        PixelButton(
                            label = "⬆ Rank Up",
                            onClick = onRankUp,
                            active = true,
                            horizontalPadding = 2.dp,
                            fontSize = 8.sp,
                            modifier = Modifier
                                .weight(1f)
                                .height(26.dp)
                        )
                    }

                    if (tier2Pet.currentRank > 0) {
                        PixelButton(
                            label = if (isEquipped) "Equipped" else "Equip",
                            onClick = onEquip,
                            active = isEquipped,
                            horizontalPadding = 2.dp,
                            fontSize = 8.sp,
                            modifier = Modifier
                                .weight(1f)
                                .height(26.dp)
                        )
                    }
                }
            }
        }

        // Overlay if Tier 1 pet is < level 50
        if (isTier1Locked) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🔒", fontSize = 20.sp)
                    Text(
                        "Requires Level 50 Tier 1",
                        style = PixelSmall,
                        color = EnemyRed,
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
