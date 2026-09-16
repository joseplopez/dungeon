package com.game.dungeon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.game.dungeon.ui.components.safeStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.game.dungeon.R
import com.game.dungeon.data.models.LeaderboardEntry
import com.game.dungeon.ui.components.BottomPixelNav
import com.game.dungeon.ui.components.GoldenBorderBox
import com.game.dungeon.ui.components.MusicToggleButton
import com.game.dungeon.ui.components.PixelButton
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.LeaderboardViewModel
import java.util.concurrent.TimeUnit

import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.window.Dialog
import com.game.dungeon.ui.components.PixelDivider

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
    
    var selectedEntry by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<LeaderboardEntry?>(null) }
    var showAddFriendDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showEditNameDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    
    val clipboardManager = LocalClipboardManager.current

    Column(
        Modifier
            .fillMaxSize()
            .background(BgDarkest)
            .padding(12.dp)
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(safeStringResource(R.string.leaderboard_title), style = PixelHeading)
            MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
        }

        Spacer(Modifier.height(8.dp))

        // Player Info & Identity
        GoldenBorderBox(
            Modifier
                .fillMaxWidth()
                .background(BgDark)
                .clickable { showEditNameDialog = true }
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = gameState?.playerName ?: "...",
                        style = PixelBody,
                        color = GoldBright
                    )
                    if (gameState?.playerId == null) {
                        Text(
                            text = "CONNECTING...",
                            style = PixelSmall,
                            color = Color.Red,
                            modifier = Modifier.clickable { viewModel.ensureUserIdentity() }
                        )
                    } else {
                        Text(
                            text = safeStringResource(R.string.my_id_label, gameState?.playerId?.take(8) ?: "...."),
                            style = PixelSmall,
                            color = StoneGray
                        )
                    }
                }
                
                PixelButton(
                    label = "COPY ID",
                    onClick = {
                        gameState?.playerId?.let {
                            clipboardManager.setText(AnnotatedString(it))
                        }
                    },
                    modifier = Modifier.height(30.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Tabs & Add Friend
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PixelButton(
                label = safeStringResource(R.string.tab_global),
                onClick = { viewModel.selectTab(0) },
                active = selectedTab == 0,
                modifier = Modifier.weight(1f)
            )
            PixelButton(
                label = safeStringResource(R.string.tab_dimension),
                onClick = { viewModel.selectTab(1) },
                active = selectedTab == 1,
                modifier = Modifier.weight(1f)
            )
            PixelButton(
                label = safeStringResource(R.string.tab_friends),
                onClick = { viewModel.selectTab(2) },
                active = selectedTab == 2,
                modifier = Modifier.weight(1f)
            )
            
            if (selectedTab == 2) {
                PixelButton(
                    label = "+",
                    onClick = { showAddFriendDialog = true },
                    modifier = Modifier.width(40.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Leaderboard List
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(safeStringResource(R.string.rank_label), style = PixelSmall, color = GoldBright, modifier = Modifier.width(40.dp))
                    Text(safeStringResource(R.string.player_label), style = PixelSmall, color = GoldBright, modifier = Modifier.weight(1f))
                    Text(safeStringResource(R.string.floor_reached_short), style = PixelSmall, color = GoldBright, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
                    Text(safeStringResource(R.string.magicite_short), style = PixelSmall, color = GoldBright, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
                    Text(safeStringResource(R.string.time_short), style = PixelSmall, color = GoldBright, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
                }

                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("LOADING...", style = PixelBody, color = StoneGray)
                    }
                } else if (selectedTab == 2 && entries.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            safeStringResource(R.string.no_friends_desc),
                            style = PixelSmall,
                            color = StoneGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(entries) { entry ->
                            LeaderboardRow(entry, onClick = { selectedEntry = entry })
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        BottomPixelNav("leaderboard", navController)
    }

    if (showAddFriendDialog) {
        AddFriendDialog(
            onDismiss = { showAddFriendDialog = false },
            onAdd = { id ->
                viewModel.addFriend(id)
                showAddFriendDialog = false
            }
        )
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
fun AddFriendDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var text by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(
            Modifier
                .fillMaxWidth(0.9f)
                .background(BgDarkest)
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(safeStringResource(R.string.enter_friend_id_title), style = PixelHeading)
                
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text(safeStringResource(R.string.friend_id_hint), color = StoneGray) },
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
                        label = safeStringResource(R.string.add_button),
                        onClick = { if (text.isNotBlank()) onAdd(text.trim()) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun EditNameDialog(currentName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(currentName) }
    
    Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(
            Modifier
                .fillMaxWidth(0.9f)
                .background(BgDarkest)
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("CHANGE NAME", style = PixelHeading)
                
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
fun LeaderboardRow(entry: LeaderboardEntry, onClick: () -> Unit) {
    val bgColor = if (entry.isUser) GoldBright.copy(alpha = 0.1f) else Color.Transparent
    val textColor = if (entry.isUser) GoldBright else Color.White
    
    Row(
        Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#${entry.rank}",
            style = PixelSmall,
            color = when(entry.rank) {
                1 -> GoldBright
                2 -> Color(0xFFC0C0C0) // Silver
                3 -> Color(0xFFCD7F32) // Bronze
                else -> textColor
            },
            modifier = Modifier.width(40.dp)
        )
        Text(
            text = if (entry.isUser) safeStringResource(R.string.player_name_you) else entry.playerName,
            style = PixelSmall,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = entry.maxFloor.toString(),
            style = PixelSmall,
            color = textColor,
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = formatLargeNumber(entry.totalMagicite),
            style = PixelSmall,
            color = textColor,
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = formatTime(entry.fastestClearMs),
            style = PixelSmall,
            color = textColor,
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GhostRunDialog(entry: LeaderboardEntry, onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(
            Modifier
                .fillMaxWidth(0.9f)
                .background(BgDarkest)
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val displayName = if (entry.isUser) safeStringResource(R.string.player_name_you) else entry.playerName
                Text(safeStringResource(R.string.ghost_run_team_title, displayName), style = PixelHeading)
                
                com.game.dungeon.ui.components.PixelDivider()
                
                if (entry.team.isEmpty()) {
                    Text(safeStringResource(R.string.no_party_data), style = PixelSmall, color = StoneGray)
                } else {
                    entry.team.forEach { hero ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(hero.heroClass.emoji, fontSize = 20.sp)
                            Column(Modifier.weight(1f)) {
                                Text(hero.name, style = PixelBody)
                                Text(safeStringResource(R.string.hero_lvl_class_format, hero.level, safeStringResource(hero.heroClass.nameRes)), style = PixelSmall, color = StoneGray)
                            }
                        }
                    }
                }

                com.game.dungeon.ui.components.PixelDivider()
                
                PixelButton(label = safeStringResource(R.string.ghost_run_close), onClick = onDismiss)
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
fun formatTime(ms: Long): String {
    if (ms == 0L) return safeStringResource(R.string.empty_time)
    val hours = TimeUnit.MILLISECONDS.toHours(ms)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return if (hours > 0) {
        String.format(java.util.Locale.US, "%dh", hours)
    } else {
        String.format(java.util.Locale.US, "%dm%ds", minutes, seconds)
    }
}
