package com.game.dungeon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.game.dungeon.R
import com.game.dungeon.data.models.LeaderboardEntry
import com.game.dungeon.ui.components.BottomPixelNav
import com.game.dungeon.ui.components.GoldenBorderBox
import com.game.dungeon.ui.components.HeroSprite
import com.game.dungeon.ui.components.MusicToggleButton
import com.game.dungeon.ui.components.PixelButton
import com.game.dungeon.ui.components.PixelDivider
import com.game.dungeon.ui.components.safeStringResource
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.LeaderboardViewModel
import java.util.concurrent.TimeUnit

@Composable
fun LeaderboardScreen(
    navController: NavController,
    isMuted: Boolean,
    onToggleMusic: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val entries by viewModel.leaderboardEntries.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedEntry by remember { mutableStateOf<LeaderboardEntry?>(null) }
    var showEditNameDialog by remember { mutableStateOf(false) }

    val userEntry = entries.find { it.isUser }
    val userRankText = userEntry?.let { "#${it.rank}" } ?: "#-"

    Column(
        Modifier
            .fillMaxSize()
            .background(BgDarkest)
            .padding(6.dp)
    ) {
        // Top Header Bar
        GoldenBorderBox(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(BgDark)
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PixelButton(
                    label = safeStringResource(R.string.back_button),
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.height(28.dp)
                )

                Row(
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🏛️", fontSize = 16.sp)
                    Text(
                        text = safeStringResource(R.string.leaderboard_title),
                        style = PixelHeading,
                        color = GoldBright
                    )
                }

                Row(
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🪙 ${gameState?.gold ?: 0}G", style = PixelGold)
                    Text("💎 ${gameState?.magicite ?: 0}", style = PixelGold)
                    MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // Colosseum Arena Player Champion Status Bar
        GoldenBorderBox(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFF140D1F))
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Player Champion Crest
                    Box(
                        Modifier
                            .size(36.dp)
                            .background(BgDarkest, RoundedCornerShape(4.dp))
                            .border(1.5.dp, GoldBright, RoundedCornerShape(4.dp)),
                        contentAlignment = Center
                    ) {
                        Column(horizontalAlignment = CenterHorizontally) {
                            Text(
                                text = userRankText,
                                style = PixelHeading,
                                color = GoldBright,
                                fontSize = 12.sp
                            )
                            Text(
                                text = safeStringResource(R.string.rank_label),
                                style = PixelSmall,
                                color = StoneGray,
                                fontSize = 7.sp
                            )
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = gameState?.playerName ?: "...",
                                style = PixelBody,
                                color = GoldBright
                            )
                            Text(
                                text = "✏️",
                                fontSize = 11.sp,
                                modifier = Modifier.clickable { showEditNameDialog = true }
                            )
                        }

                        Text(
                            text = "DIM ${gameState?.currentDimension ?: 1} • FLR ${gameState?.lifetimeHighestFloor ?: 0}",
                            style = PixelSmall,
                            color = StoneGray
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // HORIZONTAL SPLIT LAYOUT: LEFT = TOP 3 PODIUM HALL OF FAME, RIGHT = STANDINGS TABLE
        Row(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // LEFT PANE (35% Width): Hall of Fame Top 3 Podium
            GoldenBorderBox(
                Modifier
                    .weight(0.35f)
                    .fillMaxSize()
                    .background(Color(0xFF120B1C))
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🏆 HALL OF FAME",
                            style = PixelHeading,
                            color = GoldBright,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "TOP CHAMPIONS",
                            style = PixelSmall,
                            color = StoneGray,
                            fontSize = 8.sp
                        )

                        Spacer(Modifier.height(4.dp))
                        PixelDivider()
                        Spacer(Modifier.height(4.dp))

                        if (entries.isNotEmpty()) {
                            val topChampion = entries[0]
                            Text(
                                text = "👑 ${topChampion.playerName}",
                                style = PixelBody,
                                color = GoldBright,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                            Text(
                                text = "Dim ${topChampion.dimension} • Floor ${topChampion.maxFloor}",
                                style = PixelSmall,
                                color = StoneGray,
                                fontSize = 8.sp
                            )

                            if (topChampion.team.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = CenterVertically
                                ) {
                                    topChampion.team.take(4).forEach { hero ->
                                        Box(
                                            Modifier
                                                .size(24.dp)
                                                .background(BgDarkest, RoundedCornerShape(3.dp))
                                                .border(1.dp, GoldDark, RoundedCornerShape(3.dp))
                                        ) {
                                            HeroSprite(hero.heroClass, Modifier.fillMaxSize())
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "NO CHAMPIONS YET",
                                style = PixelSmall,
                                color = StoneGray,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }

                    if (entries.isNotEmpty()) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // SILVER (#2)
                            if (entries.size >= 2) {
                                PodiumPedestal(
                                    entry = entries[1],
                                    title = "🥈 " + safeStringResource(R.string.podium_silver),
                                    heightDp = 50,
                                    borderColor = Color(0xFFC0C0C0),
                                    modifier = Modifier.weight(1f),
                                    onClick = { selectedEntry = entries[1] }
                                )
                            } else {
                                Spacer(Modifier.weight(1f))
                            }

                            // GOLD (#1 CENTER)
                            PodiumPedestal(
                                entry = entries[0],
                                title = "🥇 " + safeStringResource(R.string.podium_gold),
                                heightDp = 68,
                                borderColor = GoldBright,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedEntry = entries[0] }
                            )

                            // BRONZE (#3)
                            if (entries.size >= 3) {
                                PodiumPedestal(
                                    entry = entries[2],
                                    title = "🥉 " + safeStringResource(R.string.podium_bronze),
                                    heightDp = 42,
                                    borderColor = Color(0xFFCD7F32),
                                    modifier = Modifier.weight(1f),
                                    onClick = { selectedEntry = entries[2] }
                                )
                            } else {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // RIGHT PANE (65% Width): Category Tabs & Standings Table
            Column(
                Modifier
                    .weight(0.65f)
                    .fillMaxSize()
            ) {
                // Category Tabs (GLOBAL, DIMENSION)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = CenterVertically
                ) {
                    PixelButton(
                        label = safeStringResource(R.string.tab_global),
                        onClick = { viewModel.selectTab(0) },
                        active = selectedTab == 0,
                        modifier = Modifier.weight(1f).height(28.dp)
                    )
                    PixelButton(
                        label = safeStringResource(R.string.tab_dimension),
                        onClick = { viewModel.selectTab(1) },
                        active = selectedTab == 1,
                        modifier = Modifier.weight(1f).height(28.dp)
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Leaderboard Standings Table
                GoldenBorderBox(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(BgDark)
                ) {
                    Column(Modifier.fillMaxSize()) {
                        // Table Header
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(BgMedium)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = CenterVertically
                        ) {
                            Text(
                                text = safeStringResource(R.string.rank_label),
                                style = PixelSmall,
                                color = GoldBright,
                                modifier = Modifier.width(36.dp)
                            )
                            Text(
                                text = safeStringResource(R.string.gladiator_team_header),
                                style = PixelSmall,
                                color = GoldBright,
                                modifier = Modifier.weight(1f)
                            )
                            val floorHeader = if (selectedTab == 1) "MAX DIM" else safeStringResource(R.string.floor_reached_short)
                            Text(
                                text = floorHeader,
                                style = PixelSmall,
                                color = GoldBright,
                                modifier = Modifier.width(55.dp),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = safeStringResource(R.string.magicite_short),
                                style = PixelSmall,
                                color = GoldBright,
                                modifier = Modifier.width(50.dp),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = safeStringResource(R.string.time_short),
                                style = PixelSmall,
                                color = GoldBright,
                                modifier = Modifier.width(60.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        if (isLoading) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Center) {
                                Text("LOADING...", style = PixelBody, color = StoneGray)
                            }
                        } else if (entries.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Center) {
                                Text(
                                    text = "NO RANKINGS YET",
                                    style = PixelSmall,
                                    color = StoneGray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            LazyColumn(Modifier.fillMaxSize()) {
                                items(entries) { entry ->
                                    LeaderboardRow(
                                        entry = entry,
                                        selectedTab = selectedTab,
                                        onClick = { selectedEntry = entry }
                                    )
                                    PixelDivider()
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        BottomPixelNav("leaderboard", navController)
    }

    if (showEditNameDialog) {
        EditNameDialog(
            currentName = gameState?.playerName ?: "",
            onDismiss = { showEditNameDialog = false },
            onSave = { name ->
                viewModel.updatePlayerName(name)
                showEditNameDialog = false
            }
        )
    }

    selectedEntry?.let { entry ->
        GhostRunDialog(entry, onDismiss = { selectedEntry = null })
    }
}

@Composable
private fun PodiumPedestal(
    entry: LeaderboardEntry,
    title: String,
    heightDp: Int,
    borderColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        // Hero Sprites on top of pedestal
        Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
            entry.team.take(2).forEach { hero ->
                Box(Modifier.size(20.dp)) {
                    HeroSprite(hero.heroClass, Modifier.fillMaxSize())
                }
            }
        }
        Text(
            text = entry.playerName,
            style = PixelSmall,
            color = borderColor,
            maxLines = 1,
            fontSize = 8.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(2.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(heightDp.dp)
                .background(BgMedium, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .border(1.5.dp, borderColor, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
            contentAlignment = Center
        ) {
            Column(horizontalAlignment = CenterHorizontally) {
                Text(
                    text = title,
                    style = PixelSmall,
                    color = borderColor,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "FLR ${entry.maxFloor}",
                    style = PixelHeading,
                    color = GoldBright,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun LeaderboardRow(entry: LeaderboardEntry, selectedTab: Int, onClick: () -> Unit) {
    val bgColor = if (entry.isUser) GoldBright.copy(alpha = 0.15f) else Color.Transparent
    val textColor = if (entry.isUser) GoldBright else Color.White

    Row(
        Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = CenterVertically
    ) {
        // Rank Badge
        Box(Modifier.width(36.dp)) {
            when (entry.rank) {
                1 -> Text("🥇 #1", style = PixelSmall, color = GoldBright)
                2 -> Text("🥈 #2", style = PixelSmall, color = Color(0xFFC0C0C0))
                3 -> Text("🥉 #3", style = PixelSmall, color = Color(0xFFCD7F32))
                else -> Text("#${entry.rank}", style = PixelSmall, color = textColor)
            }
        }

        // Player Name & Hero Team Sprites
        Row(
            Modifier.weight(1f),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (entry.isUser) safeStringResource(R.string.player_name_you) else entry.playerName,
                style = PixelSmall,
                color = textColor,
                modifier = Modifier.width(85.dp),
                maxLines = 1
            )

            // Mini Hero Team Canvas Sprites
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                entry.team.take(4).forEach { hero ->
                    Box(
                        Modifier
                            .size(18.dp)
                            .background(BgDarkest, RoundedCornerShape(2.dp))
                            .border(0.5.dp, GoldDark, RoundedCornerShape(2.dp))
                    ) {
                        HeroSprite(hero.heroClass, Modifier.fillMaxSize())
                    }
                }
            }
        }

        Text(
            text = (if (selectedTab == 1) entry.dimension else entry.maxFloor).toString(),
            style = PixelSmall,
            color = textColor,
            modifier = Modifier.width(55.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = formatLargeNumber(entry.totalMagicite),
            style = PixelSmall,
            color = textColor,
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = formatTime(entry.fastestClearMs, entry.currentDimTimeMs),
            style = PixelSmall,
            color = textColor,
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EditNameDialog(currentName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(currentName) }

    Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(
            Modifier
                .fillMaxWidth(0.9f)
                .background(BgDarkest)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(safeStringResource(R.string.edit_name_hint), style = PixelHeading)

                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BgMedium,
                        unfocusedContainerColor = BgMedium,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PixelButton(
                        label = safeStringResource(R.string.cancel_button_short),
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    PixelButton(
                        label = safeStringResource(R.string.save_button),
                        onClick = { if (text.isNotBlank()) onSave(text.trim()) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun GhostRunDialog(entry: LeaderboardEntry, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(
            Modifier
                .fillMaxWidth(0.95f)
                .background(BgDarkest)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val displayName = if (entry.isUser) safeStringResource(R.string.player_name_you) else entry.playerName
                Text(safeStringResource(R.string.ghost_run_team_title, displayName), style = PixelHeading)
                Text(
                    text = "DIM ${entry.dimension} • FLOOR ${entry.maxFloor} • 💎 ${entry.totalMagicite}",
                    style = PixelSmall,
                    color = GoldBright
                )

                PixelDivider()

                if (entry.team.isEmpty()) {
                    Text(
                        safeStringResource(R.string.no_party_data),
                        style = PixelSmall,
                        color = StoneGray,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    Column(
                        Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        entry.team.forEach { hero ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .background(BgDark, RoundedCornerShape(4.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = CenterVertically
                            ) {
                                Box(Modifier.size(36.dp)) {
                                    HeroSprite(hero.heroClass, Modifier.fillMaxSize())
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(hero.name, style = PixelBody, color = Color.White)
                                    Text(
                                        safeStringResource(
                                            R.string.hero_lvl_class_format,
                                            hero.level,
                                            safeStringResource(hero.heroClass.nameRes)
                                        ),
                                        style = PixelSmall,
                                        color = GoldDark,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                PixelDivider()

                PixelButton(
                    label = safeStringResource(R.string.ghost_run_close),
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(32.dp)
                )
            }
        }
    }
}

@Composable
fun formatLargeNumber(num: Int): String {
    return if (num >= 1000000) {
        safeStringResource(R.string.format_m, num / 1000000f)
    } else if (num >= 1000) {
        safeStringResource(R.string.format_k, num / 1000f)
    } else {
        num.toString()
    }
}

@Composable
fun formatTime(fastest: Long, current: Long): String {
    val MAX_VALID_MS = 864000000L // 10 days cap to avoid corrupt 55-year timestamps
    val validFastest = if (fastest > MAX_VALID_MS) 0L else fastest
    val validCurrent = if (current > MAX_VALID_MS) 0L else current

    val ms = if (validFastest > 0L) validFastest else validCurrent
    if (ms <= 0L) return safeStringResource(R.string.empty_time)
    val hours = TimeUnit.MILLISECONDS.toHours(ms)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return if (hours > 0) {
        String.format(java.util.Locale.US, "%dh %dm", hours, minutes)
    } else {
        String.format(java.util.Locale.US, "%dm %ds", minutes, seconds)
    }
}
