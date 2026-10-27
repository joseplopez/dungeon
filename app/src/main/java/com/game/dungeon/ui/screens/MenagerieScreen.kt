package com.game.dungeon.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
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
    var showSafariInfo by remember { mutableStateOf(false) } // ADD THIS STATE

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

                    // Hide the title on Tier 2 to make room for the Safari buttons
                    if (selectedTab == 0 || !isSafariUnlocked) {
                        Text(safeStringResource(R.string.menagerie), style = PixelHeading, color = GoldBright)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Conditionally render Safari Actions in the top bar
                        if (selectedTab == 1 && isSafariUnlocked) {
                            val canStartSafari = state.safariTickets >= 1 && activeParty.isNotEmpty()
                            Text(safeStringResource(R.string.safari_tickets_format, state.safariTickets), style = PixelBody, color = GoldBright)
                            // ADD THE INFO BUTTON HERE:
                            PixelButton(label = "ℹ️", onClick = { showSafariInfo = true }, modifier = Modifier
                                .height(28.dp)
                                .width(36.dp), horizontalPadding = 0.dp)
                            PixelButton(label = "📺", onClick = { activity?.let { viewModel.watchSafariTicketAd(it) } }, modifier = Modifier
                                .height(28.dp)
                                .width(36.dp), horizontalPadding = 0.dp)
                            PixelButton(label = "💰", onClick = { activity?.let { viewModel.buyMiscPack(it) } }, modifier = Modifier
                                .height(28.dp)
                                .width(36.dp), horizontalPadding = 0.dp)
                            PixelButton(
                                label = safeStringResource(R.string.btn_enter_safari),
                                onClick = {
                                    if (viewModel.consumeSafariTicketForRun()) {
                                        onStartSafariRun(activeParty)
                                    }
                                },
                                enabled = canStartSafari,
                                active = canStartSafari,
                                modifier = Modifier.height(28.dp),
                                fontSize = 10.sp,
                                horizontalPadding = 8.dp
                            )
                        }

                        SupportIconButton(onClick = { showSupportDialog = true })
                        MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
                    }
                }
            }

            // Tabs immediately follow the Header now
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BgMedium,
                contentColor = GoldBright,
                divider = { PixelDivider() }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(safeStringResource(R.string.tab_tier_1_passives), style = PixelBody, color = if (selectedTab == 0) GoldBright else GoldDark) }
                )
                // Tier 2 is locked until Safari is unlocked
                Tab(
                    selected = selectedTab == 1,
                    onClick = { if (isSafariUnlocked) selectedTab = 1 },
                    enabled = isSafariUnlocked,
                    text = {
                        Text(
                            text = if (isSafariUnlocked) safeStringResource(R.string.tab_tier_2_cards) else "🔒 " + safeStringResource(R.string.tab_tier_2_cards),
                            style = PixelBody,
                            color = if (selectedTab == 1) GoldBright else if (isSafariUnlocked) GoldDark else StoneGray
                        )
                    }
                )
            }

            // Expanded Content Area
            Box(
                Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // Optional: Show equipped Tier 1 here if you want
                        Tier1PassivesTab(
                            gs = state,
                            onSelect = { viewModel.selectPet(it) },
                            onUnlock = { viewModel.unlockPet(it) }
                        )
                    }
                    1 -> Tier2CardsTab(
                        gs = state,
                        isSafariUnlocked = isSafariUnlocked,
                        onEquipTier2 = { petId -> viewModel.equipTier2Pet(petId) },
                        onRankUpTier2 = { petId -> viewModel.rankUpTier2Pet(petId) },
                    )
                }
            }

            BottomPixelNav(navController.currentBackStackEntry?.destination?.route, navController)
        }

        if (showSupportDialog) {
            SupportDialog(onDismiss = { showSupportDialog = false })
        }
        if (showSafariInfo) {
            SafariInfoDialog(onDismiss = { showSafariInfo = false })
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = safeStringResource(R.string.safari_locked_title),
                    style = PixelHeading,
                    color = EnemyRed,
                    fontSize = 16.sp
                )
                Text(
                    text = safeStringResource(R.string.safari_locked_desc),
                    style = PixelBody,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        // The Cards Grid
        LazyVerticalGrid(
            // Fixed(5) ensures the 5 cards perfectly stretch to fill the entire horizontal space
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp)
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
        Rarity.COMMON -> Color(0xFF3A4B5C)
        Rarity.RARE -> Color(0xFF1E2E4A)
        Rarity.EPIC -> Color(0xFF4A1E4A)
        Rarity.LEGENDARY -> Color(0xFF6B4C12)
        Rarity.MYTHIC -> Color(0xFF4A1E2E)
    }

    val neededCards = tier2Pet.getCardsNeededForNextRank()
    val canRankUp = tier2Pet.canRankUp()
    val isRankZero = tier2Pet.currentRank == 0

    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f) // Strict trading card shape
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(BgDarkest, rarityColor)
                )
            )
            .border(
                width = if (isEquipped) 3.dp else 1.dp,
                color = if (isEquipped) GoldBright else Color.DarkGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = !isTier1Locked && !isRankZero) {
                onEquip()
            }
    ) {
        // --- TOP LEFT: Rank Badge ---
        Box(
            Modifier
                .padding(8.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                .border(
                    1.dp,
                    if (!isRankZero) GoldBright else Color.DarkGray,
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Text(
                text = safeStringResource(R.string.tier2_rank_format, tier2Pet.currentRank),
                style = PixelSmall,
                color = if (!isRankZero) GoldBright else StoneGray,
                fontSize = 10.sp, // Increased from 8
                fontWeight = FontWeight.Bold
            )
        }

        // --- TOP RIGHT: Element Icon ---
        Text(
            text = petType.emoji,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopEnd),
            fontSize = 16.sp // Increased from 12
        )

        // --- CENTER: Sprite ---
        Box(
            Modifier
                .fillMaxSize()
                .padding(top = 36.dp, bottom = 72.dp) // Keeps sprite clear of top badges and bottom panel
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Fills the available center space so the sprite grows dynamically
            PetSprite(petType, Modifier.fillMaxSize())
        }

        // --- COMPACT TOP CHECKMARK ---
        if (isEquipped) {
            Text(
                text = "✅",
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
            )
        }

        // --- BOTTOM: Info & Action Panel ---
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = safeStringResource(petType.nameRes),
                style = PixelBody,
                color = GoldBright,
                fontSize = 12.sp, // Increased from 10
                fontWeight = FontWeight.Bold
            )

            // Dual-Stats Text
            val boostText = petType.tier2Boosts.joinToString(" & ") { it.displayName }
            Text(
                text = safeStringResource(R.string.tier2_boost_format, tier2Pet.currentRank * 3, boostText),
                style = PixelSmall,
                color = if (isRankZero) StoneGray else HpGreen,
                fontSize = 10.sp // Increased from 8
            )

            Spacer(Modifier.height(6.dp))

            if (canRankUp) {
                PixelButton(
                    label = safeStringResource(R.string.btn_rank_up),
                    onClick = onRankUp,
                    active = true,
                    horizontalPadding = 0.dp,
                    fontSize = 10.sp, // Increased from 8
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp) // Increased for easier tapping
                )
                Spacer(Modifier.height(6.dp))
            }

            // Collection Progress Bar at the very bottom
            val progress = if (tier2Pet.currentRank >= 5) 1f else (tier2Pet.cardsCollected.toFloat() / neededCards.toFloat())
            val cardsText = if (tier2Pet.currentRank >= 5) safeStringResource(R.string.max_level) else safeStringResource(R.string.cards_progress_format, tier2Pet.cardsCollected, neededCards)

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp) // Slightly thicker bar
                        .clip(RoundedCornerShape(3.dp)),
                    color = SystemCyan,
                    trackColor = Color.DarkGray
                )
                Text(
                    text = cardsText,
                    style = PixelSmall,
                    color = Color.White,
                    fontSize = 8.sp, // Increased from 6
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }

        // --- OVERLAY: Tier 1 Locked ---
        if (isTier1Locked) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🔒", fontSize = 28.sp)
                    Text(
                        text = safeStringResource(R.string.tier1_level_required),
                        style = PixelSmall,
                        color = EnemyRed,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SafariInfoDialog(onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        PixelPanel(
            Modifier
                .fillMaxWidth(0.88f)
                .widthIn(max = 380.dp)
                .heightIn(max = 400.dp)
                .background(BgDarkest),
            borderColor = SystemCyan
        ) {
            Column(
                Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("🌿", fontSize = 48.sp)
                Text(
                    text = safeStringResource(R.string.safari_info_title),
                    style = PixelTitle,
                    color = SystemCyan,
                    textAlign = TextAlign.Center
                )

                // Scrollable container for the lore text
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = safeStringResource(R.string.safari_info_desc),
                        style = PixelBody,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                PixelButton(
                    label = safeStringResource(R.string.btn_i_understand),
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    active = true
                )
            }
        }
    }
}