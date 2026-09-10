package com.game.dungeon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.dungeon.ui.components.*
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.BattleViewModel

@Composable
fun BattleScreen(viewModel: BattleViewModel, floor: Int, onBack: () -> Unit) {
    val state by viewModel.battleState.collectAsState()

    LaunchedEffect(floor) {
        viewModel.startBattle(floor)
    }

    PixelTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            DungeonBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Floor ${state.currentFloor}", style = PixelHeading)
                    PixelButton(label = "RETREAT", onClick = onBack)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.weight(1f)) {
                    // Heroes
                    Column(modifier = Modifier.weight(1f)) {
                        Text("PARTY", style = PixelHeading)
                        LazyColumn {
                            items(state.heroes, key = { it.id }) { hero ->
                                PixelPanel(modifier = Modifier.padding(4.dp).fillMaxWidth()) {
                                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(hero.heroClass.emoji, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(hero.nickname, style = PixelBody)
                                            PixelHpBar(
                                                current = hero.currentHp,
                                                max = hero.maxHp,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Enemies
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ENEMIES", style = PixelHeading)
                        LazyColumn {
                            items(state.enemies, key = { it.id }) { enemy ->
                                PixelPanel(modifier = Modifier.padding(4.dp).fillMaxWidth(), borderColor = EnemyRed) {
                                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(enemy.emoji, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(enemy.name, style = PixelEnemy)
                                            PixelHpBar(
                                                current = enemy.currentHp,
                                                max = enemy.maxHp,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (!state.isRunning && state.enemies.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("VICTORY!", style = PixelTitle, color = GoldBright)
                    }
                }
            }
        }
    }
}
