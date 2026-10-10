package com.game.dungeon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.game.dungeon.R
import com.game.dungeon.data.models.*
import com.game.dungeon.ui.components.*
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.CodexTab
import com.game.dungeon.ui.viewmodels.CodexViewModel

private val enemyTemplatesMap: Map<MonsterType, FFEnemyTemplate> by lazy {
    FFDimensionData.dimensions.flatMap { it.enemies }.associateBy { it.type }
}

fun KillThreshold.getNameRes(): Int = when (this) {
    KillThreshold.UNDISCOVERED -> R.string.threshold_undiscovered
    KillThreshold.SIGHTED -> R.string.threshold_sighted
    KillThreshold.ANALYZED -> R.string.threshold_analyzed
    KillThreshold.MASTERED -> R.string.threshold_mastered
}

fun MaterialCategory.getNameRes(): Int = when (this) {
    MaterialCategory.ORE -> R.string.mat_cat_ore
    MaterialCategory.MONSTER_PART -> R.string.mat_cat_monster_part
    MaterialCategory.ESSENCE -> R.string.mat_cat_essence
    MaterialCategory.BOSS_TROPHY -> R.string.mat_cat_boss_trophy
}

@Composable
fun CodexScreen(
    navController: NavController,
    isMuted: Boolean,
    onToggleMusic: () -> Unit,
    onBack: () -> Unit,
    viewModel: CodexViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSupportDialog by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().background(BgDarkest)) {
        Column(Modifier.fillMaxSize()) {
            // Header
            GoldenBorderBox(
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(BgDarkest)
            ) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PixelButton(
                        label = safeStringResource(R.string.back_button),
                        onClick = onBack,
                        modifier = Modifier.height(34.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = safeStringResource(R.string.codex_title),
                            style = PixelHeading
                        )
                        Box(
                            Modifier
                                .background(BgDark, RoundedCornerShape(4.dp))
                                .border(1.dp, GoldDark, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = safeStringResource(R.string.codex_completion_format, uiState.completionPercentage),
                                style = PixelSmall,
                                color = GoldBright,
                                fontSize = 10.sp
                            )
                        }
                        BasicTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            singleLine = true,
                            textStyle = PixelSmall.copy(color = GoldBright, fontSize = 11.sp),
                            decorationBox = { innerTextField ->
                                Box(
                                    Modifier
                                        .background(BgDark, RoundedCornerShape(4.dp))
                                        .border(1.dp, GoldDark, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (uiState.searchQuery.isEmpty()) {
                                        Text(
                                            text = safeStringResource(R.string.codex_search_hint),
                                            style = PixelSmall,
                                            color = StoneGray,
                                            fontSize = 11.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                            modifier = Modifier
                                .widthIn(min = 120.dp, max = 200.dp)
                                .height(32.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SupportIconButton(onClick = { showSupportDialog = true })
                        MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
                    }
                }
            }

            // Compact Tab Row
            val tabs = listOf(
                CodexTab.BESTIARY to safeStringResource(R.string.codex_tab_bestiary),
                CodexTab.MATERIALS to safeStringResource(R.string.codex_tab_materials),
                CodexTab.EQUIPMENT to safeStringResource(R.string.codex_tab_milestones)
            )

            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                containerColor = BgMedium,
                contentColor = GoldBright,
                divider = { PixelDivider() },
                modifier = Modifier.height(36.dp)
            ) {
                tabs.forEach { (tab, label) ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        modifier = Modifier.height(36.dp),
                        text = {
                            Text(
                                label,
                                style = PixelBody,
                                color = if (uiState.selectedTab == tab) GoldBright else GoldDark,
                                fontSize = 11.sp
                            )
                        }
                    )
                }
            }

            // Main Content Area
            Box(
                Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                when (uiState.selectedTab) {
                    CodexTab.BESTIARY -> BestiaryTabContent(entries = uiState.bestiaryEntries)
                    CodexTab.MATERIALS -> MaterialTabContent(entries = uiState.materialEntries)
                    CodexTab.EQUIPMENT -> MilestonesTabContent(
                        completionPercentage = uiState.completionPercentage
                    )
                }
            }

            BottomPixelNav(navController.currentBackStackEntry?.destination?.route, navController)
        }

        if (showSupportDialog) {
            SupportDialog(onDismiss = { showSupportDialog = false })
        }
    }
}

@Composable
fun BestiaryTabContent(entries: List<MonsterCodexDetail>) {
    if (entries.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                safeStringResource(R.string.codex_no_results),
                style = PixelBody,
                color = StoneGray
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(entries) { detail ->
                MonsterCodexCard(detail)
            }
        }
    }
}

@Composable
fun MonsterCodexCard(detail: MonsterCodexDetail) {
    val template = enemyTemplatesMap[detail.monsterType]

    PixelPanel(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (detail.isDiscovered) GoldBright else StoneGray
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (detail.isDiscovered) (template?.emoji ?: "👾") else "❓",
                        fontSize = 22.sp
                    )
                    Text(
                        text = if (detail.isDiscovered) {
                            safeStringResource(template?.nameRes ?: R.string.enemy_goblin)
                        } else {
                            safeStringResource(R.string.codex_undiscovered)
                        },
                        style = PixelBody,
                        color = if (detail.isDiscovered) GoldBright else StoneGray
                    )
                }

                Box(
                    Modifier
                        .background(if (detail.isDiscovered) BgDark else BgDarkest, RoundedCornerShape(3.dp))
                        .border(1.dp, if (detail.isDiscovered) GoldDark else StoneGray, RoundedCornerShape(3.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = safeStringResource(detail.threshold.getNameRes()),
                        style = PixelSmall,
                        color = if (detail.isDiscovered) GoldBright else StoneGray,
                        fontSize = 9.sp
                    )
                }
            }

            PixelDivider()

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = safeStringResource(R.string.codex_kills_format, detail.killCount),
                    style = PixelSmall,
                    color = GoldDark
                )

                if (detail.isDiscovered && detail.drops.isNotEmpty()) {
                    val dropMaterialNames = detail.drops.map { drop ->
                        val mat = MaterialCatalog.getMaterial(drop.materialId)
                        safeStringResource(mat.nameRes)
                    }.distinct().take(2).joinToString(", ")

                    Text(
                        text = safeStringResource(R.string.codex_drops_label, dropMaterialNames),
                        style = PixelSmall,
                        color = StoneGray,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MaterialTabContent(entries: List<MaterialCodexDetail>) {
    if (entries.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                safeStringResource(R.string.codex_no_results),
                style = PixelBody,
                color = StoneGray
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(entries) { detail ->
                MaterialCodexCard(detail)
            }
        }
    }
}

@Composable
fun MaterialCodexCard(detail: MaterialCodexDetail) {
    val material = detail.material

    PixelPanel(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (detail.isDiscovered) GoldBright else StoneGray
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (detail.isDiscovered) material.emoji else "📦",
                        fontSize = 22.sp
                    )
                    Text(
                        text = if (detail.isDiscovered) {
                            safeStringResource(material.nameRes)
                        } else {
                            safeStringResource(R.string.codex_undiscovered)
                        },
                        style = PixelBody,
                        color = if (detail.isDiscovered) GoldBright else StoneGray
                    )
                }

                Box(
                    Modifier
                        .background(if (detail.isDiscovered) BgDark else BgDarkest, RoundedCornerShape(3.dp))
                        .border(1.dp, if (detail.isDiscovered) GoldDark else StoneGray, RoundedCornerShape(3.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (detail.isDiscovered) safeStringResource(material.category.getNameRes()) else safeStringResource(R.string.codex_undiscovered),
                        style = PixelSmall,
                        color = if (detail.isDiscovered) GoldBright else StoneGray,
                        fontSize = 9.sp
                    )
                }
            }

            PixelDivider()

            if (detail.isDiscovered) {
                Text(
                    text = safeStringResource(material.descriptionRes),
                    style = PixelSmall,
                    color = Color(0xFFE0E0E0),
                    fontSize = 10.sp
                )

                if (detail.primaryDropSources.isNotEmpty()) {
                    val sources = detail.primaryDropSources.map { monsterType ->
                        val template = enemyTemplatesMap[monsterType]
                        if (template != null) {
                            "${template.emoji} ${safeStringResource(template.nameRes)}"
                        } else {
                            monsterType.name
                        }
                    }.take(3).joinToString(", ")

                    Text(
                        text = safeStringResource(R.string.codex_drop_sources, sources),
                        style = PixelSmall,
                        color = GoldDark,
                        fontSize = 9.sp
                    )
                }
            } else {
                Text(
                    text = safeStringResource(R.string.codex_undiscovered),
                    style = PixelSmall,
                    color = StoneGray,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun MilestonesTabContent(
    completionPercentage: Float
) {
    val allMilestones = CodexMilestone.entries

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(allMilestones) { milestone ->
            val isUnlocked = completionPercentage >= milestone.requiredPercentage

            PixelPanel(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (isUnlocked) GoldBright else StoneGray
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = safeStringResource(R.string.codex_milestone_tier, milestone.requiredPercentage.toInt()),
                            style = PixelHeading,
                            color = if (isUnlocked) GoldBright else StoneGray
                        )

                        val bonusDescriptions = mutableListOf<String>()
                        if (milestone.goldMultiplierBonus > 0f) {
                            bonusDescriptions.add("🪙 +${(milestone.goldMultiplierBonus * 100).toInt()}% ${safeStringResource(R.string.stat_gil_find)}")
                        }
                        if (milestone.critChanceBonus > 0) {
                            bonusDescriptions.add("⚡ +${milestone.critChanceBonus}% ${safeStringResource(R.string.stat_crit_chance)}")
                        }
                        if (milestone.attackMultiplierBonus > 0f) {
                            bonusDescriptions.add("⚔️ +${(milestone.attackMultiplierBonus * 100).toInt()}% ${safeStringResource(R.string.stat_attack)}")
                        }
                        if (milestone.magiciteChanceBonus > 0f) {
                            bonusDescriptions.add("💎 +${(milestone.magiciteChanceBonus * 100).toInt()}% ${safeStringResource(R.string.stat_magicite_find)}")
                        }

                        Text(
                            text = bonusDescriptions.joinToString(" • "),
                            style = PixelSmall,
                            color = Color(0xFFE0E0E0)
                        )
                    }

                    Box(
                        Modifier
                            .background(if (isUnlocked) HpGreen.copy(alpha = 0.2f) else BgDark, RoundedCornerShape(4.dp))
                            .border(1.dp, if (isUnlocked) HpGreen else StoneGray, RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = safeStringResource(if (isUnlocked) R.string.codex_status_unlocked else R.string.codex_status_locked),
                            style = PixelBody,
                            color = if (isUnlocked) HpGreen else StoneGray
                        )
                    }
                }
            }
        }
    }
}
