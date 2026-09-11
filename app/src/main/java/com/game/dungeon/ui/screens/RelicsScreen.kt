package com.game.dungeon.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.RelicType
import com.game.dungeon.ui.components.*
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.RelicsViewModel
import kotlin.random.Random

@Composable
fun RelicsScreen(
    navController: NavController,
    isMuted: Boolean,
    onToggleMusic: () -> Unit,
    viewModel: RelicsViewModel = hiltViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val magicite = gameState?.magicite ?: 0
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(Modifier.fillMaxSize()) {
        // Dark cosmic background
        Canvas(Modifier.fillMaxSize()) {
            drawRect(Color(0xFF080412))
            val rng = Random(12345)
            repeat(150) {
                val x = rng.nextFloat() * size.width
                val y = rng.nextFloat() * size.height
                val brightness = rng.nextFloat() * 0.7f + 0.3f
                val starSize = rng.nextFloat() * 2f + 0.5f
                drawCircle(Color.White.copy(alpha = brightness), starSize, Offset(x, y))
            }
        }

        Column(Modifier.fillMaxSize()) {
            // Header
            GoldenBorderBox(Modifier.fillMaxWidth().height(48.dp).background(BgDarkest)) {
                Row(
                    Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PixelButton("◀ BACK", onClick = { navController.popBackStack() }, modifier = Modifier.height(32.dp))
                    Column(horizontalAlignment = CenterHorizontally) {
                        Text("RELICS", style = PixelHeading)
                        Text("💎 $magicite MAGICITE", style = PixelGold)
                    }
                    MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
                }
            }

            // Relic grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val dimension = gameState?.currentDimension ?: 1
                items(RelicType.entries.filter { type ->
                    when (type) {
                        RelicType.MAGNET -> dimension >= 2
                        RelicType.POCKETS -> dimension >= 3
                        RelicType.DOUBLE_LOOT -> dimension >= 4
                        else -> true
                    }
                }) { relicType ->
                    RelicCard(
                        relicType = relicType,
                        gameState = gameState ?: GameState(),
                        magicite = magicite,
                        onUpgrade = { viewModel.upgradeRelic(relicType) }
                    )
                }
            }
            BottomPixelNav(currentRoute, navController)
        }
    }
}

@Composable
fun RelicCard(
    relicType: RelicType,
    gameState: GameState,
    magicite: Int,
    onUpgrade: () -> Unit
) {
    val level = when (relicType) {
        RelicType.ATTACK -> gameState.attackRelic
        RelicType.HP -> gameState.hpRelic
        RelicType.MP -> gameState.mpRelic
        RelicType.MAGIC -> gameState.magicRelic
        RelicType.GOLD -> gameState.goldRelic
        RelicType.MAGICITE_FIND -> gameState.magiciteRelic
        RelicType.CRIT_CHANCE -> gameState.critChanceRelic
        RelicType.CRIT_DAMAGE -> gameState.critDamageRelic
        RelicType.MAGNET -> gameState.magnetRelic
        RelicType.POCKETS -> gameState.pocketsRelic
        RelicType.DOUBLE_LOOT -> gameState.doubleLootRelic
    }
    val cost = (level + 1) * 10
    
    val icon = when (relicType) {
        RelicType.ATTACK -> "⚔️"
        RelicType.HP -> "❤️"
        RelicType.MP -> "💙"
        RelicType.MAGIC -> "🔮"
        RelicType.GOLD -> "🪙"
        RelicType.MAGICITE_FIND -> "💎"
        RelicType.CRIT_CHANCE -> "🎯"
        RelicType.CRIT_DAMAGE -> "💥"
        RelicType.MAGNET -> "🧲"
        RelicType.POCKETS -> "🎒"
        RelicType.DOUBLE_LOOT -> "🎁"
    }
    
    val name = when (relicType) {
        RelicType.ATTACK -> "Attack"
        RelicType.HP -> "Vitality"
        RelicType.MP -> "Spirit"
        RelicType.MAGIC -> "Magic"
        RelicType.GOLD -> "Fortune"
        RelicType.MAGICITE_FIND -> "Essence"
        RelicType.CRIT_CHANCE -> "Hawk Eye"
        RelicType.CRIT_DAMAGE -> "Hitter"
        RelicType.MAGNET -> "Magnet"
        RelicType.POCKETS -> "Pockets"
        RelicType.DOUBLE_LOOT -> "Loot"
    }

    val desc = when (relicType) {
        RelicType.ATTACK -> "Phys Dmg"
        RelicType.HP -> "Max HP"
        RelicType.MP -> "Max MP"
        RelicType.MAGIC -> "Mag Dmg"
        RelicType.GOLD -> "Gil Gain"
        RelicType.MAGICITE_FIND -> "Find Rate"
        RelicType.CRIT_CHANCE -> "Crit Rate"
        RelicType.CRIT_DAMAGE -> "Crit Dmg"
        RelicType.MAGNET -> "Magci Drop"
        RelicType.POCKETS -> "Gold Keep"
        RelicType.DOUBLE_LOOT -> "Boss Double"
    }

    val currentVal = when (relicType) {
        RelicType.ATTACK -> level * 2
        RelicType.HP -> level * 15
        RelicType.MP -> level * 10
        RelicType.MAGIC -> level * 2
        RelicType.GOLD -> level * 5
        RelicType.MAGICITE_FIND -> level
        RelicType.CRIT_CHANCE -> level
        RelicType.CRIT_DAMAGE -> level * 5
        RelicType.MAGNET -> level * 5
        RelicType.POCKETS -> level * 10
        RelicType.DOUBLE_LOOT -> level * 5
    }
    val nextVal = when (relicType) {
        RelicType.ATTACK -> (level + 1) * 2
        RelicType.HP -> (level + 1) * 15
        RelicType.MP -> (level + 1) * 10
        RelicType.MAGIC -> (level + 1) * 2
        RelicType.GOLD -> (level + 1) * 5
        RelicType.MAGICITE_FIND -> (level + 1)
        RelicType.CRIT_CHANCE -> (level + 1)
        RelicType.CRIT_DAMAGE -> (level + 1) * 5
        RelicType.MAGNET -> (level + 1) * 5
        RelicType.POCKETS -> (level + 1) * 10
        RelicType.DOUBLE_LOOT -> (level + 1) * 5
    }
    val suffix = if (relicType == RelicType.GOLD || relicType == RelicType.MAGICITE_FIND ||
                     relicType == RelicType.CRIT_CHANCE || relicType == RelicType.CRIT_DAMAGE ||
                     relicType == RelicType.MAGNET || relicType == RelicType.POCKETS || 
                     relicType == RelicType.DOUBLE_LOOT) "%" else ""

    PixelPanel(
        modifier = Modifier.fillMaxWidth().height(125.dp),
        borderColor = if (magicite >= cost) GoldDark else EnemyRed
    ) {
        Column(Modifier.fillMaxSize().padding(4.dp)) {
            // Header: Icon, Name, Level
            Row(Modifier.fillMaxWidth(), verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(icon, fontSize = 20.sp)
                Column(horizontalAlignment = Alignment.End) {
                    Text(name.uppercase(), style = PixelGold, fontSize = 11.sp)
                    Text("LVL $level", style = PixelSmall, color = GoldDark, fontSize = 9.sp)
                }
            }

            // Comparison View
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Center) {
                Column(horizontalAlignment = CenterHorizontally) {
                    Text(desc, style = PixelSmall, color = StoneGray, fontSize = 9.sp)
                    Row(verticalAlignment = CenterVertically) {
                        Text("+$currentVal$suffix", style = PixelBody, color = SystemCyan, fontSize = 11.sp)
                        Text(" → ", style = PixelBody, color = GoldDark, fontSize = 11.sp)
                        Text("+$nextVal$suffix", style = PixelBody, color = GoldBright, fontSize = 11.sp)
                    }
                }
            }

            // Progress Bar (simple 10 segments)
            Row(Modifier.fillMaxWidth().height(3.dp).padding(horizontal = 4.dp), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                val progress = level % 10
                repeat(10) { i ->
                    Box(Modifier.weight(1f).fillMaxHeight().background(if (i < progress) GoldBright else StoneGray))
                }
            }

            Spacer(Modifier.height(6.dp))

            // Centered Upgrade Button with Cost
            PixelButton(
                label = "UPGRADE ($cost 💎)",
                onClick = onUpgrade,
                enabled = magicite >= cost,
                modifier = Modifier.fillMaxWidth().height(32.dp),
                horizontalPadding = 0.dp
            )
        }
    }
}
