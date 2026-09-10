package com.game.dungeon.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.game.dungeon.data.models.*
import com.game.dungeon.ui.components.*
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.TownViewModel

@Composable
fun TownScreen(
    navController: NavController,
    isMuted: Boolean,
    onToggleMusic: () -> Unit,
    viewModel: TownViewModel = hiltViewModel()
) {
    val gs by viewModel.gameState.collectAsState()
    val availableCrystals by viewModel.availableCrystals.collectAsState()
    val gil = gs?.gold ?: 0L
    val magicite = gs?.magicite ?: 0
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showCrystalShop by remember { mutableStateOf(false) }
    var showUpgrades by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    PixelTheme {
        Box(Modifier.fillMaxSize()) {
            // Parallax Background
            TownParallaxBackground(scrollState.value.toFloat())

            Column(Modifier.fillMaxSize()) {
                // Header
                GoldenBorderBox(Modifier.fillMaxWidth().height(40.dp)) {
                    Box(Modifier.fillMaxSize().background(BgDarkest)) {
                        Row(
                            Modifier.fillMaxSize().padding(horizontal = 12.dp),
                            verticalAlignment = CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("🪙", fontSize = 14.sp)
                                    Text(formatGold(gil), style = PixelGold)
                                }
                                Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("💎", fontSize = 14.sp)
                                    Text("$magicite", style = PixelGold)
                                }
                            }
                            Text("GRAND CAPITAL", style = PixelHeading)
                            MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
                        }
                    }
                }

                // Town View Area (Horizontally Scrollable)
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalScroll(scrollState)
                            .padding(bottom = 0.dp), // Position buildings on the ground
                        horizontalArrangement = Arrangement.spacedBy(60.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Spacer(Modifier.width(150.dp)) // Left margin

                        // 1. Inn Building
                        TownBuilding("THE INN") { 
                            navController.navigate("inn") {
                                launchSingleTop = true
                                popUpTo("inn") { saveState = true }
                                restoreState = true
                            }
                        }

                        // 2. Crystal Shop
                        TownBuilding("CRYSTAL SHOP") { 
                            showCrystalShop = true 
                        }

                        // 3. Upgrades Building
                        TownBuilding("BARRACKS") { 
                            showUpgrades = true 
                        }

                        // 4. Relics / Portal
                        TownBuilding("RELICS") {
                            navController.navigate("relics") {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }

                        Spacer(Modifier.width(250.dp)) // Right margin
                    }
                }

                BottomPixelNav(currentRoute, navController)
            }
        }
    }

    // Dialogs remain the same
    if (showCrystalShop) {
        CrystalShopDialog(
            availableCrystals = availableCrystals,
            unlockedJobs = gs?.unlockedJobs ?: setOf(HeroClass.FREELANCER),
            gil = gil,
            onBuy = { viewModel.buyCrystal(it) },
            onDismiss = { showCrystalShop = false }
        )
    }

    if (showUpgrades) {
        UpgradesDialog(
            gs = gs ?: GameState(),
            onUpgradeInn = { viewModel.upgradeInn() },
            onUpgradeArmory = { viewModel.upgradeArmory() },
            onUpgradeMagicShop = { viewModel.upgradeMagicShop() },
            onUpgradeBarracks = { viewModel.upgradeBarracks() },
            onUpgradeVault = { viewModel.upgradeVault() },
            onUpgradePathfinder = { viewModel.upgradePathfinder() },
            onDismiss = { showUpgrades = false }
        )
    }
}

@Composable
fun TownBuilding(name: String, onClick: () -> Unit) {
    Column(horizontalAlignment = CenterHorizontally, modifier = Modifier.width(200.dp)) {
        // Larger, more detailed programmatic sprites
        Box(
            modifier = Modifier
                .size(160.dp, 120.dp)
                .clickable { onClick() },
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(Modifier.fillMaxSize()) {
                when (name) {
                    "THE INN" -> drawDetailedInn()
                    "CRYSTAL SHOP" -> drawDetailedCrystalShop()
                    "BARRACKS" -> drawDetailedBarracks()
                    "RELICS" -> drawDetailedPortal()
                }
            }
        }
        
        Spacer(Modifier.height(40.dp))
        
        GoldenBorderBox(
            modifier = Modifier
                .background(BgDarkest.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
        ) {
            Text(
                name,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
                style = PixelBody,
                color = GoldBright,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
          

@Composable
fun CrystalShopDialog(
    availableCrystals: List<TownViewModel.CrystalData>,
    unlockedJobs: Set<HeroClass>,
    gil: Long,
    onBuy: (CrystalColor) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(Modifier.fillMaxWidth(0.7f).height(400.dp).background(BgDarkest).padding(16.dp)) {
            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = CenterVertically) {
                    Text("CRYSTAL SHOP", style = PixelHeading)
                    PixelButton("✕", onClick = onDismiss, modifier = Modifier.size(32.dp))
                }
                Text("Unlock new job classes", style = PixelSmall, color = GoldDark)
                PixelDivider()
                Spacer(Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(availableCrystals) { crystal ->
                        val isUnlocked = unlockedJobs.contains(crystal.unlocksJob)
                        CrystalShopRow(crystal, isUnlocked, gil, onBuy = { onBuy(crystal.color) })
                    }
                }
            }
        }
    }
}

@Composable
fun CrystalShopRow(
    crystal: TownViewModel.CrystalData,
    isUnlocked: Boolean,
    gil: Long,
    onBuy: () -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(4.dp), verticalAlignment = CenterVertically) {
        Canvas(Modifier.size(32.dp)) {
            val path = Path().apply {
                moveTo(size.width / 2f, 0f)
                lineTo(size.width, size.height * 0.4f)
                lineTo(size.width / 2f, size.height)
                lineTo(0f, size.height * 0.4f)
                close()
            }
            drawPath(path, color = Color(crystal.color.colorHex))
            drawPath(path, color = Color.White.copy(alpha = 0.3f), style = Stroke(2f))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(crystal.color.displayName, style = PixelBody, color = Color(crystal.color.colorHex))
            Text("Unlocks: ${crystal.unlocksJob.displayName}", style = PixelSmall, color = GoldDark)
        }
        if (isUnlocked) {
            Text("✓ OWNED", style = PixelSmall, color = HpGreen)
        } else {
            PixelButton(
                "${crystal.color.baseCost}G",
                onClick = onBuy,
                enabled = gil >= crystal.color.baseCost,
                modifier = Modifier.width(80.dp)
            )
        }
    }
}

@Composable
fun UpgradesDialog(
    gs: GameState,
    onUpgradeInn: () -> Unit,
    onUpgradeArmory: () -> Unit,
    onUpgradeMagicShop: () -> Unit,
    onUpgradeBarracks: () -> Unit,
    onUpgradeVault: () -> Unit,
    onUpgradePathfinder: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GoldenBorderBox(Modifier.fillMaxWidth(0.8f).height(450.dp).background(BgDarkest).padding(16.dp)) {
            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = CenterVertically) {
                    Text("TOWN UPGRADES", style = PixelHeading)
                    PixelButton("✕", onClick = onDismiss, modifier = Modifier.size(32.dp))
                }
                PixelDivider()
                Spacer(Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val upgrades = listOf(
                        Triple("🍺 Taproom", gs.innLevel, "Unlocks advanced jobs") to onUpgradeInn,
                        Triple("⚔️ Armory", gs.armoryLevel, "Better equipment drops") to onUpgradeArmory,
                        Triple("🔮 Magic Shop", gs.magicShopLevel, "Unlocks magic items") to onUpgradeMagicShop,
                        Triple("🏕 Barracks", gs.barracksLevel, "+1 party slot") to onUpgradeBarracks,
                        Triple("🏦 Vault", gs.vaultLevel, "+Gil storage cap") to onUpgradeVault,
                        Triple("🧭 Pathfinder", gs.pathfinderLevel, "Select start floor") to onUpgradePathfinder
                    )
                    items(upgrades) { (info, action) ->
                        val (label, level, desc) = info
                        PixelPanel(Modifier.fillMaxWidth().height(80.dp), borderColor = GoldDark) {
                            Row(Modifier.fillMaxSize().padding(4.dp), verticalAlignment = CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(label, style = PixelBody, color = GoldBright)
                                    Text("LVL $level - $desc", style = PixelSmall, color = StoneGray)
                                }
                                PixelButton("UP (500G)", onClick = action, enabled = gs.gold >= 500, modifier = Modifier.width(120.dp).height(40.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
