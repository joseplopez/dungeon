package com.game.dungeon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.game.dungeon.R
import com.game.dungeon.data.models.*
import com.game.dungeon.ui.theme.BgDarkest
import com.game.dungeon.ui.theme.PixelTheme
import com.game.dungeon.ui.viewmodels.DungeonViewModel

private fun createSample5Heroes(): List<Hero> {
    return listOf(
        Hero(id = "1", heroClass = HeroClass.WARRIOR, name = "Edgar", currentHp = 150, currentMp = 20, level = 10, exp = 50, expToNextLevel = 100, abilityCharge = 2, aiPriority = AIPriority.ATTACK),
        Hero(id = "2", heroClass = HeroClass.NINJA, name = "Lightning", currentHp = 120, currentMp = 30, level = 10, exp = 30, expToNextLevel = 100, abilityCharge = 1, aiPriority = AIPriority.ATTACK),
        Hero(id = "3", heroClass = HeroClass.WHITE_MAGE, name = "Rosa", currentHp = 90, currentMp = 80, level = 10, exp = 80, expToNextLevel = 100, abilityCharge = 3, aiPriority = AIPriority.HEAL),
        Hero(id = "4", heroClass = HeroClass.RED_MAGE, name = "Rosa", currentHp = 100, currentMp = 40, level = 10, exp = 10, expToNextLevel = 100, abilityCharge = 0, aiPriority = AIPriority.HEAL),
        Hero(id = "5", heroClass = HeroClass.BLACK_MAGE, name = "Penelo", currentHp = 80, currentMp = 100, level = 10, exp = 90, expToNextLevel = 100, abilityCharge = 2, aiPriority = AIPriority.MAGIC)
    )
}

private fun createSampleEnemies(): List<Enemy> {
    return listOf(
        Enemy(id = "e1", name = "Goblin", emoji = "👺", currentHp = 80, maxHp = 80, attack = 15, defense = 5, gilReward = 20, floor = 7),
        Enemy(id = "e2", name = "Skeleton", emoji = "💀", currentHp = 110, maxHp = 110, attack = 22, defense = 8, gilReward = 35, floor = 7)
    )
}

@Composable
fun DungeonPreviewContainer() {
    val heroes = createSample5Heroes()
    val enemies = createSampleEnemies()
    val dimension = FFDimensionData.dimensions.firstOrNull()
    val biome = dimension?.biomes?.firstOrNull()

    PixelTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDarkest)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                DungeonTopBar(
                    floor = 7,
                    dimension = dimension,
                    currentBiome = biome,
                    gilTotal = 148,
                    magiciteTotal = 11,
                    isBossFloor = false,
                    onRetreat = {},
                    speed = BattleSpeed.FAST,
                    onSpeedChange = {},
                    isMuted = false,
                    onToggleMusic = {},
                    boostFloorsRemaining = 0,
                    onWatchBoostAd = {},
                    onOpenSupport = {}
                )

                BattleArea(
                    modifier = Modifier.weight(1f),
                    heroes = heroes,
                    enemies = enemies,
                    dyingHeroIds = emptySet(),
                    attackingUnitId = null,
                    hitEnemyId = null,
                    hitHeroId = null,
                    isCritical = false,
                    dimension = dimension,
                    floor = 7
                )

                BattleLogPanel(
                    battleLog = listOf(
                        DungeonViewModel.FFLogEntry(
                            messageRes = R.string.log_physical_attack,
                            args = listOf("Lightning", "Goblin", 176),
                            type = DungeonViewModel.LogType.HERO_ATTACK
                        ),
                        DungeonViewModel.FFLogEntry(
                            messageRes = R.string.log_victory_xp,
                            args = listOf("Goblin", 100),
                            type = DungeonViewModel.LogType.ABILITY
                        ),
                        DungeonViewModel.FFLogEntry(
                            messageRes = R.string.log_physical_attack,
                            args = listOf("Penelo", "Goblin", 177),
                            type = DungeonViewModel.LogType.HERO_ATTACK
                        )
                    )
                )
            }
        }
    }
}

@Preview(name = "Small Screen (560x320) - 5 Heroes", widthDp = 560, heightDp = 320)
@Composable
fun Dungeon5HeroesSmallScreenPreview() {
    DungeonPreviewContainer()
}

@Preview(name = "Standard Screen (640x360) - 5 Heroes", widthDp = 640, heightDp = 360)
@Composable
fun Dungeon5HeroesStandardScreenPreview() {
    DungeonPreviewContainer()
}

@Preview(name = "Large Screen (800x400) - 5 Heroes", widthDp = 800, heightDp = 400)
@Composable
fun Dungeon5HeroesLargeScreenPreview() {
    DungeonPreviewContainer()
}

@Preview(name = "Tablet Screen (1024x600) - 5 Heroes", widthDp = 1024, heightDp = 600)
@Composable
fun Dungeon5HeroesTabletScreenPreview() {
    DungeonPreviewContainer()
}
