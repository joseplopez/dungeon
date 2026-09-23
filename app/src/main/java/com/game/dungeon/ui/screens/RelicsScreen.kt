package com.game.dungeon.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.game.dungeon.R
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

    var selectedRelicType by remember { mutableStateOf(RelicType.MAGIC) }
    var leftTab by remember { mutableIntStateOf(0) } // 0: RELICS, 1: AVAILABLE
    var rightTab by remember { mutableIntStateOf(0) } // 0: RELICS, 1: STATS

    val infiniteTransition = rememberInfiniteTransition(label = "relic_anim")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100000f,
        animationSpec = infiniteRepeatable(tween(100000, easing = LinearEasing)),
        label = "animTime"
    )

    PixelTheme {
        Box(Modifier.fillMaxSize()) {
            // Dark cosmic background with stars
            Canvas(Modifier.fillMaxSize()) {
                drawRect(Color(0xFF0B0618))
                val rng = Random(4242)
                repeat(120) {
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
                        PixelButton(
                            label = safeStringResource(R.string.back_button),
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.height(32.dp)
                        )
                        Column(horizontalAlignment = CenterHorizontally) {
                            Text(safeStringResource(R.string.relics_title), style = PixelHeading)
                            Text(
                                safeStringResource(selectedRelicType.nameRes).uppercase(),
                                style = PixelGold,
                                fontSize = 11.sp
                            )
                        }
                        Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("💎", fontSize = 14.sp)
                                Text("$magicite", style = PixelGold)
                            }
                            MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
                        }
                    }
                }

                // 3-Panel Main Dashboard
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val gs = gameState ?: GameState()
                    val dimension = gs.currentDimension

                    val allRelics = RelicType.entries.filter { type ->
                        when (type) {
                            RelicType.MAGNET -> dimension >= 2
                            RelicType.POCKETS -> dimension >= 3
                            RelicType.DOUBLE_LOOT -> dimension >= 4
                            else -> true
                        }
                    }

                    val ownedRelics = allRelics.filter { getRelicLevel(it, gs) > 0 }
                    val availableRelics = allRelics.filter { getRelicLevel(it, gs) == 0 }

                    // =========================================
                    // PANEL 1: LEFT GRID / INVENTORY (Weight 1.1f)
                    // =========================================
                    GoldenBorderBox(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                            .background(BgDarkest.copy(alpha = 0.85f))
                    ) {
                        Column(Modifier.fillMaxSize().padding(6.dp)) {
                            // Sub-Tabs Header
                            Row(Modifier.fillMaxWidth().height(32.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                PixelButton(
                                    label = "RELICS",
                                    onClick = { leftTab = 0 },
                                    active = (leftTab == 0),
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                )
                                PixelButton(
                                    label = "AVAILABLE",
                                    onClick = { leftTab = 1 },
                                    active = (leftTab == 1),
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            // Grid View
                            val displayList = if (leftTab == 0) (if (ownedRelics.isNotEmpty()) ownedRelics else allRelics) else availableRelics
                            
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(displayList) { relic ->
                                    val isSelected = (relic == selectedRelicType)
                                    val level = getRelicLevel(relic, gs)

                                    Box(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .background(if (isSelected) BgPanel else BgDarkest)
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) GoldBright else GoldDark.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable { selectedRelicType = relic }
                                            .padding(4.dp),
                                        contentAlignment = Center
                                    ) {
                                        Canvas(Modifier.fillMaxSize()) {
                                            drawRelicSprite(relic, animTime)
                                        }

                                        if (level > 0) {
                                            Box(
                                                Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .background(BgDarkest.copy(alpha = 0.9f), RoundedCornerShape(2.dp))
                                                    .padding(horizontal = 3.dp, vertical = 1.dp)
                                            ) {
                                                Text("L$level", style = PixelSmall, color = GoldBright, fontSize = 9.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // =========================================
                    // PANEL 2: CENTER DETAIL INSPECTOR (Weight 1.3f)
                    // =========================================
                    val selectedLevel = getRelicLevel(selectedRelicType, gs)
                    val upgradeCost = (selectedLevel + 1) * 10
                    val canAfford = magicite >= upgradeCost

                    GoldenBorderBox(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight()
                            .background(BgDarkest.copy(alpha = 0.9f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Large Canvas Relic Preview Box
                            GoldenBorderBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .background(BgPanel.copy(alpha = 0.8f))
                            ) {
                                Canvas(Modifier.fillMaxSize().padding(8.dp)) {
                                    drawRelicSprite(selectedRelicType, animTime)
                                }
                            }

                            // Title, Rarity & Cost
                            Column(horizontalAlignment = CenterHorizontally) {
                                Text(
                                    safeStringResource(selectedRelicType.nameRes).uppercase(),
                                    style = PixelHeading,
                                    color = GoldBright
                                )
                                Row(
                                    verticalAlignment = CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Legendary Artifact", style = PixelSmall, color = StoneGray)
                                    Text("•", style = PixelSmall, color = StoneGray)
                                    Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text("💎", fontSize = 11.sp)
                                        Text("$upgradeCost", style = PixelGold, fontSize = 11.sp)
                                    }
                                }
                            }

                            // Dashed Divider line
                            Canvas(Modifier.fillMaxWidth().height(2.dp)) {
                                drawLine(
                                    color = GoldDark.copy(alpha = 0.6f),
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                                )
                            }

                            Text("PERK UPGRADES", style = PixelGold, fontSize = 10.sp)

                            // Stat Boost Cards
                            val currentVal = getRelicBonusValue(selectedRelicType, selectedLevel)
                            val nextVal = getRelicBonusValue(selectedRelicType, selectedLevel + 1)
                            val suffix = getRelicSuffix(selectedRelicType)
                            val icon = getRelicIcon(selectedRelicType)

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                StatBoostCard(
                                    icon = icon,
                                    label = safeStringResource(selectedRelicType.nameRes),
                                    currentVal = "+$currentVal$suffix",
                                    nextVal = "+$nextVal$suffix"
                                )

                                StatBoostCard(
                                    icon = "⭐",
                                    label = safeStringResource(selectedRelicType.descRes),
                                    currentVal = "LVL $selectedLevel",
                                    nextVal = "LVL ${selectedLevel + 1}"
                                )
                            }

                            // Action Upgrade Button
                            PixelButton(
                                label = if (canAfford) "UPGRADE (💎 $upgradeCost)" else "NEED MAGICITE (💎 $upgradeCost)",
                                onClick = { viewModel.upgradeRelic(selectedRelicType) },
                                enabled = canAfford,
                                modifier = Modifier.fillMaxWidth().height(38.dp)
                            )
                        }
                    }

                    // =========================================
                    // PANEL 3: RIGHT ACTIVE PERKS SUMMARY (Weight 1.0f)
                    // =========================================
                    GoldenBorderBox(
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxHeight()
                            .background(BgDarkest.copy(alpha = 0.85f))
                    ) {
                        Column(Modifier.fillMaxSize().padding(6.dp)) {
                            // Sub-Tabs Header
                            Row(Modifier.fillMaxWidth().height(32.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                PixelButton(
                                    label = "RELICS",
                                    onClick = { rightTab = 0 },
                                    active = (rightTab == 0),
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                )
                                PixelButton(
                                    label = "STATS",
                                    onClick = { rightTab = 1 },
                                    active = (rightTab == 1),
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            // Active Perks Summary List / Game Stats List
                            if (rightTab == 0) {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(allRelics) { relic ->
                                        val lvl = getRelicLevel(relic, gs)
                                        val valStr = "+${getRelicBonusValue(relic, lvl)}${getRelicSuffix(relic)}"
                                        val relicIcon = getRelicIcon(relic)

                                        GoldenBorderBox(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(if (lvl > 0) BgPanel.copy(alpha = 0.8f) else BgDarkest),
                                            cornerSize = 0.dp
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                                verticalAlignment = CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(relicIcon, fontSize = 12.sp)
                                                    Text(
                                                        safeStringResource(relic.nameRes),
                                                        style = PixelSmall,
                                                        color = if (lvl > 0) GoldBright else StoneGray,
                                                        maxLines = 1
                                                    )
                                                }
                                                Text(
                                                    valStr,
                                                    style = PixelSmall,
                                                    color = if (lvl > 0) HpGreen else StoneGray,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    item { StatSummaryRow(icon = "🌌", label = "Dimension", value = "Dim ${gs.currentDimension}") }
                                    item { StatSummaryRow(icon = "🏰", label = "Highest Floor", value = "F${gs.highestFloor}") }
                                    item { StatSummaryRow(icon = "👑", label = "Lifetime Floor", value = "F${gs.lifetimeHighestFloor}") }
                                    item { StatSummaryRow(icon = "💎", label = "Total Magicite", value = "${gs.totalMagiciteEarned}") }
                                    item { StatSummaryRow(icon = "🪙", label = "Total Gil", value = "${gs.totalGilEarned}") }
                                    item { StatSummaryRow(icon = "🏦", label = "Gil Vault Cap", value = "${gs.maxGil}") }
                                    item { StatSummaryRow(icon = "⚡", label = "EXP Boost", value = "+${((gs.expMultiplier - 1.0f) * 100).toInt()}%") }
                                    item { StatSummaryRow(icon = "🏷️", label = "Upgrade Discount", value = "-${(gs.upgradeDiscount * 100).toInt()}%") }
                                    item { StatSummaryRow(icon = "💤", label = "Rest Discount", value = "-${(gs.restDiscount * 100).toInt()}%") }
                                }
                            }
                        }
                    }
                }

                BottomPixelNav(currentRoute, navController)
            }
        }
    }
}

@Composable
fun StatSummaryRow(
    icon: String,
    label: String,
    value: String
) {
    GoldenBorderBox(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgPanel.copy(alpha = 0.8f)),
        cornerSize = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(icon, fontSize = 12.sp)
                Text(
                    label,
                    style = PixelSmall,
                    color = Color.White,
                    maxLines = 1
                )
            }
            Text(
                value,
                style = PixelSmall,
                color = GoldBright,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun StatBoostCard(
    icon: String,
    label: String,
    currentVal: String,
    nextVal: String
) {
    GoldenBorderBox(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgPanel.copy(alpha = 0.9f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(icon, fontSize = 14.sp)
                Text(label, style = PixelSmall, color = Color.White, maxLines = 1)
            }
            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(currentVal, style = PixelSmall, color = SystemCyan, fontSize = 10.sp)
                Text("→", style = PixelSmall, color = StoneGray, fontSize = 10.sp)
                Text(nextVal, style = PixelSmall, color = GoldBright, fontSize = 10.sp)
            }
        }
    }
}

fun getRelicLevel(relicType: RelicType, gs: GameState): Int {
    return when (relicType) {
        RelicType.ATTACK -> gs.attackRelic
        RelicType.HP -> gs.hpRelic
        RelicType.MP -> gs.mpRelic
        RelicType.MAGIC -> gs.magicRelic
        RelicType.DEFENSE -> gs.defenseRelic
        RelicType.GOLD -> gs.goldRelic
        RelicType.MAGICITE_FIND -> gs.magiciteRelic
        RelicType.CRIT_CHANCE -> gs.critChanceRelic
        RelicType.CRIT_DAMAGE -> gs.critDamageRelic
        RelicType.MAGNET -> gs.magnetRelic
        RelicType.POCKETS -> gs.pocketsRelic
        RelicType.DOUBLE_LOOT -> gs.doubleLootRelic
    }
}

fun getRelicBonusValue(relicType: RelicType, level: Int): Int {
    return when (relicType) {
        RelicType.ATTACK -> level * 2
        RelicType.HP -> level * 15
        RelicType.MP -> level * 10
        RelicType.MAGIC -> level * 2
        RelicType.DEFENSE -> level * 2
        RelicType.GOLD -> level * 5
        RelicType.MAGICITE_FIND -> level
        RelicType.CRIT_CHANCE -> level
        RelicType.CRIT_DAMAGE -> level * 5
        RelicType.MAGNET -> level * 5
        RelicType.POCKETS -> level * 10
        RelicType.DOUBLE_LOOT -> level * 5
    }
}

fun getRelicIcon(relicType: RelicType): String {
    return when (relicType) {
        RelicType.ATTACK -> "⚔️"
        RelicType.HP -> "❤️"
        RelicType.MP -> "💙"
        RelicType.MAGIC -> "🔮"
        RelicType.DEFENSE -> "🛡️"
        RelicType.GOLD -> "🪙"
        RelicType.MAGICITE_FIND -> "💎"
        RelicType.CRIT_CHANCE -> "🎯"
        RelicType.CRIT_DAMAGE -> "💥"
        RelicType.MAGNET -> "🧲"
        RelicType.POCKETS -> "🎒"
        RelicType.DOUBLE_LOOT -> "🎁"
    }
}

fun getRelicSuffix(relicType: RelicType): String {
    return if (relicType == RelicType.GOLD || relicType == RelicType.MAGICITE_FIND ||
               relicType == RelicType.CRIT_CHANCE || relicType == RelicType.CRIT_DAMAGE ||
               relicType == RelicType.MAGNET || relicType == RelicType.POCKETS || 
               relicType == RelicType.DOUBLE_LOOT) "%" else ""
}
