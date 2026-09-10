package com.game.dungeon.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.RelicType
import com.game.dungeon.ui.components.BottomPixelNav
import com.game.dungeon.ui.components.GoldenBorderBox
import com.game.dungeon.ui.components.MusicToggleButton
import com.game.dungeon.ui.components.PixelButton
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
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f).padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(RelicType.entries) { relicType ->
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
    }
    val cost = (level + 1) * 10
    val (icon, name, description, bonusText) = when (relicType) {
        RelicType.ATTACK -> Quad("⚔️", "Attack", "Physical damage bonus", "+${level * 2} ATK (next: +${(level + 1) * 2})")
        RelicType.HP -> Quad("❤️", "Vitality", "Maximum HP bonus", "+${level * 15} HP")
        RelicType.MP -> Quad("💙", "Spirit", "Maximum MP bonus", "+${level * 10} MP")
        RelicType.MAGIC -> Quad("🔮", "Magic", "Magic damage bonus", "+${level * 2} MAG")
        RelicType.GOLD -> Quad("🪙", "Fortune", "Gil earned bonus", "+${level * 5}% Gil")
        RelicType.MAGICITE_FIND -> Quad("💎", "Essence", "Magicite find chance", "+${level}% chance")
    }

    GoldenBorderBox(Modifier.fillMaxWidth().height(110.dp)) {
        Column(Modifier.fillMaxSize().padding(8.dp)) {
            Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(icon, fontSize = 18.sp)
                Column {
                    Text(name, style = PixelBody, color = GoldBright)
                    Text("Level $level", style = PixelSmall, color = GoldDark)
                }
            }
            Text(bonusText, style = PixelSmall, color = SystemCyan)
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically
            ) {
                Text("Cost: ${cost}💎", style = PixelSmall, color = if (magicite >= cost) GoldBright else EnemyRed)
                PixelButton(
                    label = "UPGRADE",
                    onClick = onUpgrade,
                    enabled = magicite >= cost,
                    modifier = Modifier.height(24.dp).width(72.dp)
                )
            }
        }
    }
}

data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
