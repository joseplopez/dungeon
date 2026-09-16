package com.game.dungeon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.game.dungeon.R
import com.game.dungeon.data.models.*
import com.game.dungeon.ui.components.*
import com.game.dungeon.ui.components.safeStringResource
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.InnViewModel

@Composable
fun MasteryScreen(
    navController: NavController,
    isMuted: Boolean,
    onToggleMusic: () -> Unit,
    viewModel: InnViewModel = hiltViewModel()
) {
    val gs by viewModel.gameState.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(safeStringResource(R.string.tab_job_mastery), safeStringResource(R.string.tab_pets))

    Box(Modifier.fillMaxSize().background(BgDarkest)) {
        Column(Modifier.fillMaxSize()) {
            // Header
            GoldenBorderBox(Modifier.fillMaxWidth().height(56.dp).background(BgDarkest)) {
                Row(
                    Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PixelButton(safeStringResource(R.string.back_button), onClick = { navController.popBackStack() }, modifier = Modifier.height(36.dp))
                    Text(safeStringResource(R.string.training_grounds_title), style = PixelHeading)
                    MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
                }
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BgMedium,
                contentColor = GoldBright,
                divider = { PixelDivider() }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, style = PixelBody, color = if (selectedTab == index) GoldBright else StoneGray) }
                    )
                }
            }

            Box(Modifier.weight(1f).padding(8.dp)) {
                when (selectedTab) {
                    0 -> JobMasteryTab(gs ?: GameState())
                    1 -> PetsTab(gs ?: GameState(), 
                        onSelect = { viewModel.selectPet(it) },
                        onUnlock = { viewModel.unlockPet(it) }
                    )
                }
            }
            
            BottomPixelNav(navController.currentBackStackEntry?.destination?.route, navController)
        }
    }
}

@Composable
fun JobMasteryTab(gs: GameState) {
    val visibleJobs = HeroClass.entries.filter { job ->
        val isUnlocked = gs.unlockedJobs.contains(job) || job == HeroClass.FREELANCER
        val tierMet = if (job.tier >= 2) gs.innLevel >= 1 else true
        isUnlocked && tierMet
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(visibleJobs) { job ->
            val level = gs.getMasteryLevel(job)
            val exp = gs.getMasteryExp(job)
            val nextExp = gs.getMasteryNextLevelExp(job)
            val bonus = gs.getMasteryBonus(job)
            
            PixelPanel(Modifier.fillMaxWidth(), borderColor = Color(job.crystalColor.colorHex)) {
                Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    HeroSprite(job, Modifier.size(48.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(safeStringResource(job.nameRes), style = PixelBody, color = GoldBright)
                            Text("LV.$level", style = PixelBody, color = SystemCyan)
                        }
                        Spacer(Modifier.height(4.dp))
                        // EXP Bar
                        Box(Modifier.fillMaxWidth().height(8.dp).background(BgDarkest)) {
                            Box(Modifier.fillMaxWidth(exp.toFloat() / nextExp.coerceAtLeast(1).toFloat()).fillMaxHeight().background(SystemCyan))
                        }
                        Text(safeStringResource(R.string.mastery_exp_format, exp, nextExp), style = PixelSmall, color = StoneGray)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(safeStringResource(R.string.mastery_bonus_label), style = PixelSmall, color = StoneGray)
                        Text(safeStringResource(R.string.mastery_bonus_stat_format, bonus, job.masteryStatType.name), style = PixelBody, color = HpGreen)
                    }
                }
            }
        }
    }
}

@Composable
fun PetsTab(gs: GameState, onSelect: (PetType?) -> Unit, onUnlock: (PetType) -> Unit) {
    Column {
        Text(safeStringResource(R.string.select_companion_label), style = PixelBody, color = GoldBright, modifier = Modifier.padding(bottom = 8.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            item {
                PetCard(null, gs.selectedPet == null, true, gs, onSelect = { onSelect(null) })
            }
            items(PetType.entries) { pet ->
                val isUnlocked = gs.unlockedPets.contains(pet)
                PetCard(pet, gs.selectedPet == pet, isUnlocked, gs, onSelect = {
                    if (isUnlocked) onSelect(pet) else onUnlock(pet)
                })
            }
        }
    }
}

@Composable
fun PetCard(pet: PetType?, isSelected: Boolean, isUnlocked: Boolean, gs: GameState, onSelect: () -> Unit) {
    val color = if (isSelected) GoldBright else if (isUnlocked) GoldDark else StoneGray
    val canAfford = if (pet != null) gs.gold >= pet.unlockCost else true

    PixelPanel(
        modifier = Modifier.fillMaxWidth().height(105.dp).clickable { onSelect() },
        borderColor = color
    ) {
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (pet == null) {
                Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Text("🚫", fontSize = 24.sp)
                }
            } else {
                Box(Modifier.size(48.dp)) {
                    PetSprite(pet, Modifier.fillMaxSize().alpha(if (isUnlocked) 1f else 0.5f))
                    if (!isUnlocked) {
                        Text("🔒", fontSize = 16.sp, modifier = Modifier.align(Alignment.BottomEnd))
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (pet != null) safeStringResource(pet.nameRes) else safeStringResource(R.string.pet_none), style = PixelBody, color = if (isUnlocked) Color.White else StoneGray)
                    if (pet != null && isUnlocked) {
                        Text("LV.${gs.getPetLevel(pet)}", style = PixelSmall, color = SystemCyan)
                    }
                }
                if (pet != null) {
                    if (isUnlocked) {
                        val currentExp = gs.getPetExp(pet)
                        val nextExp = gs.getPetNextLevelExp(pet)
                        val bonusPct = gs.getPetBonusValue(pet) * 100f
                        
                        Text(safeStringResource(R.string.pet_bonus_format, bonusPct, pet.bonusType.name), style = PixelSmall, color = HpGreen)
                        Spacer(Modifier.height(4.dp))
                        Box(Modifier.fillMaxWidth().height(6.dp).background(BgDarkest)) {
                            Box(Modifier.fillMaxWidth(currentExp.toFloat() / nextExp.coerceAtLeast(1).toFloat()).fillMaxHeight().background(SystemCyan))
                        }
                    } else {
                        Text(safeStringResource(R.string.unlock_pet_format, pet.unlockCost), style = PixelSmall, color = if (canAfford) GoldBright else EnemyRed)
                    }
                }
            }
            if (isSelected) {
                Spacer(Modifier.width(4.dp))
                Text("✅", fontSize = 16.sp)
            }
        }
    }
}
