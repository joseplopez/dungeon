package com.game.dungeon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.game.dungeon.R
import com.game.dungeon.data.models.Hero
import com.game.dungeon.ui.components.*
import com.game.dungeon.ui.theme.*

@Composable
fun BossLairPrepScreen(
    party: List<Hero>,
    highestBossLairFloor: Int,
    onEnterBossLair: (startFloor: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    BossLairPrepDialog(
        party = party,
        highestBossLairFloor = highestBossLairFloor,
        onEnterBossLair = onEnterBossLair,
        onDismiss = onDismiss
    )
}

@Composable
fun BossLairPrepDialog(
    party: List<Hero>,
    highestBossLairFloor: Int,
    onEnterBossLair: (startFloor: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFloor by remember { mutableIntStateOf(highestBossLairFloor.coerceAtLeast(1)) }
    val maxStartFloor = highestBossLairFloor.coerceAtLeast(1)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        GoldenBorderBox(
            Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 460.dp)
                .wrapContentHeight()
                .background(BgDarkest)
                .testTag("BossLairPrepDialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Bar
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("💀", fontSize = 24.sp)
                        Column {
                            Text(safeStringResource(R.string.boss_lair_title), style = PixelHeading, color = GoldBright)
                            Text(safeStringResource(R.string.boss_lair_subtitle), style = PixelSmall, color = EnemyRed)
                        }
                    }
                    PixelButton(
                        label = "X",
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp),
                        horizontalPadding = 0.dp,
                        verticalPadding = 0.dp
                    )
                }

                PixelDivider()

                // Warning / Info Cards
                /*GoldenBorderBox(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF23080B))
                ) {
                    Column(
                        Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            safeStringResource(R.string.boss_lair_stat_spike_desc),
                            style = PixelSmall,
                            color = EnemyRed,
                            fontSize = 11.sp
                        )
                        Text(
                            safeStringResource(R.string.boss_lair_loot_desc),
                            style = PixelSmall,
                            color = GoldBright,
                            fontSize = 11.sp
                        )
                    }
                }*/

                // Party Assignment Requirement Check
                if (party.isEmpty()) {
                    GoldenBorderBox(
                        Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF3B1010))
                    ) {
                        Text(
                            safeStringResource(R.string.boss_lair_warning_no_heroes),
                            style = PixelBody,
                            color = EnemyRed,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                } else {
                    // Party Member Summary
                    GoldenBorderBox(
                        Modifier
                            .fillMaxWidth()
                            .background(BgPanel.copy(alpha = 0.8f))
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                safeStringResource(R.string.boss_lair_assigned_heroes, party.size),
                                style = PixelSmall,
                                color = StoneGray
                            )
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = CenterVertically
                            ) {
                                party.forEach { hero ->
                                    Box(
                                        Modifier
                                            .background(BgDark, RoundedCornerShape(4.dp))
                                            .border(1.dp, GoldDark, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            "${hero.name} Lvl.${hero.level}",
                                            style = PixelSmall,
                                            color = GoldBright,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF140A05), RoundedCornerShape(6.dp))
                        .border(1.5.dp, GoldDark, RoundedCornerShape(6.dp))
                        .padding(12.dp),
                    contentAlignment = Center
                ) {
                    Text(
                        safeStringResource(R.string.pathfinder_floor_range_format, selectedFloor, maxStartFloor),
                        style = PixelHeading,
                        color = GoldBright,
                        fontSize = 16.sp
                    )
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (maxStartFloor > 100) {
                        PixelButton("-100", onClick = { selectedFloor = (selectedFloor - 100).coerceAtLeast(1) }, modifier = Modifier.weight(1f).height(32.dp))
                    }
                    PixelButton("-10", onClick = { selectedFloor = (selectedFloor - 10).coerceAtLeast(1) }, modifier = Modifier.weight(1f).height(32.dp))
                    PixelButton("-1", onClick = { selectedFloor = (selectedFloor - 1).coerceAtLeast(1) }, modifier = Modifier.weight(1f).height(32.dp))
                    PixelButton("+1", onClick = { selectedFloor = (selectedFloor + 1).coerceAtMost(maxStartFloor) }, modifier = Modifier.weight(1f).height(32.dp))
                    PixelButton("+10", onClick = { selectedFloor = (selectedFloor + 10).coerceAtMost(maxStartFloor) }, modifier = Modifier.weight(1f).height(32.dp))
                    if (maxStartFloor > 100) {
                        PixelButton("+100", onClick = { selectedFloor = (selectedFloor + 100).coerceAtMost(maxStartFloor) }, modifier = Modifier.weight(1f).height(32.dp))
                    }
                }

                // Action Buttons
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PixelButton(
                        label = safeStringResource(R.string.cancel_button_short),
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    )
                    PixelButton(
                        label = safeStringResource(R.string.boss_lair_enter_button),
                        onClick = {
                            if (party.isNotEmpty()) {
                                onEnterBossLair(selectedFloor)
                            }
                        },
                        enabled = party.isNotEmpty(),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(42.dp)
                    )
                }
            }
        }
    }
}
