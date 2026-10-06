package com.game.dungeon.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.dungeon.R
import com.game.dungeon.data.models.AIPriority
import com.game.dungeon.data.models.Enemy
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.models.MonsterType
import com.game.dungeon.ui.viewmodels.DungeonViewModel.AbilityAnimationInfo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AbilityEffectsUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    private fun BattleSceneTestContainer(
        heroClass: HeroClass,
        animInfo: AbilityAnimationInfo
    ) {
        val hero = remember {
            Hero(id = "h1", heroClass = heroClass, name = "Hero", currentHp = 200, currentMp = 50, aiPriority = AIPriority.ATTACK)
        }
        val enemy = remember {
            Enemy(id = "e1", type = MonsterType.BEHEMOTH, name = "Behemoth", emoji = "👹", currentHp = 500, maxHp = 500, attack = 50, defense = 20, gilReward = 100, floor = 10, isBoss = true)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F0F1A)) // Dark JRPG battlefield background
        ) {
            // Battle Field Units
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 64.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hero Sprite on Left
                Box(modifier = Modifier.size(96.dp)) {
                    HeroSprite(heroClass = heroClass, modifier = Modifier.fillMaxSize())
                }

                // Monster Sprite on Right
                Box(modifier = Modifier.size(96.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawBehemoth()
                    }
                }
            }

            // Ability Effect Overlay
            AbilityEffectsOverlay(
                animInfo = animInfo,
                heroes = listOf(hero),
                enemies = listOf(enemy),
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    /**
     * Advances the Compose test clock frame-by-frame while sleeping in real time,
     * rendering the full live animation smoothly on screen for visual inspection.
     */
    private fun playAnimationInRealTime(durationMs: Long = 1500L) {
        composeTestRule.mainClock.autoAdvance = false
        val stepMs = 32L
        val totalSteps = (durationMs / stepMs).toInt()
        repeat(totalSteps + 1) {
            composeTestRule.mainClock.advanceTimeBy(stepMs)
            Thread.sleep(stepMs)
        }
    }

    @Test
    fun testBlackMageFiragaMeteor() {
        val animInfo = AbilityAnimationInfo(
            heroClass = HeroClass.BLACK_MAGE,
            abilityLevel = 3,
            abilityNameRes = R.string.skill_black_mage_t3_name,
            attackerId = "h1",
            targetId = "e1",
            durationMs = 1500L
        )

        composeTestRule.setContent {
            BattleSceneTestContainer(
                heroClass = HeroClass.BLACK_MAGE,
                animInfo = animInfo
            )
        }

        playAnimationInRealTime(1500L)
    }

    @Test
    fun testWhiteMageCuragaHolySanctuary() {
        val animInfo = AbilityAnimationInfo(
            heroClass = HeroClass.WHITE_MAGE,
            abilityLevel = 3,
            abilityNameRes = R.string.skill_white_mage_t3_name,
            attackerId = "h1",
            targetId = "h1",
            durationMs = 1500L
        )

        composeTestRule.setContent {
            BattleSceneTestContainer(
                heroClass = HeroClass.WHITE_MAGE,
                animInfo = animInfo
            )
        }

        playAnimationInRealTime(1500L)
    }

    @Test
    fun testSummonIfritHellfire() {

        val animInfo = AbilityAnimationInfo(
            heroClass = HeroClass.SUMMONER,
            isSummon = true,
            summonName = "Ifrit",
            abilityLevel = 2,
            attackerId = "h1",
            targetId = "e1",
            durationMs = 1500L
        )

        composeTestRule.setContent {
            BattleSceneTestContainer(
                heroClass = HeroClass.SUMMONER,
                animInfo = animInfo
            )
        }

        playAnimationInRealTime(1500L)
    }

    @Test
    fun testSummonShivaDiamondDust() {
        val animInfo = AbilityAnimationInfo(
            heroClass = HeroClass.SUMMONER,
            isSummon = true,
            summonName = "Shiva",
            abilityLevel = 2,
            attackerId = "h1",
            targetId = "e1",
            durationMs = 1500L
        )

        composeTestRule.setContent {
            BattleSceneTestContainer(
                heroClass = HeroClass.SUMMONER,
                animInfo = animInfo
            )
        }

        playAnimationInRealTime(1500L)
    }

    @Test
    fun testSummonRamuhJudgmentBolt() {
        val animInfo = AbilityAnimationInfo(
            heroClass = HeroClass.SUMMONER,
            isSummon = true,
            summonName = "Ramuh",
            abilityLevel = 2,
            attackerId = "h1",
            targetId = "e1",
            durationMs = 1500L
        )

        composeTestRule.setContent {
            BattleSceneTestContainer(
                heroClass = HeroClass.SUMMONER,
                animInfo = animInfo
            )
        }

        playAnimationInRealTime(1500L)
    }

    @Test
    fun testSummonBahamutGigaFlare() {
        val animInfo = AbilityAnimationInfo(
            heroClass = HeroClass.SUMMONER,
            isSummon = true,
            summonName = "Bahamut",
            abilityLevel = 3,
            attackerId = "h1",
            targetId = "e1",
            durationMs = 1500L
        )

        composeTestRule.setContent {
            BattleSceneTestContainer(
                heroClass = HeroClass.SUMMONER,
                animInfo = animInfo
            )
        }

        playAnimationInRealTime(1500L)
    }

    @Test
    fun testWarriorOmnislash() {
        val animInfo = AbilityAnimationInfo(
            heroClass = HeroClass.WARRIOR,
            abilityLevel = 3,
            abilityNameRes = R.string.skill_warrior_t3_name,
            attackerId = "h1",
            targetId = "e1",
            durationMs = 1500L
        )

        composeTestRule.setContent {
            BattleSceneTestContainer(
                heroClass = HeroClass.WARRIOR,
                animInfo = animInfo
            )
        }

        playAnimationInRealTime(1500L)
    }

    @Test
    fun testNinjaShuriken() {
        val animInfo = AbilityAnimationInfo(
            heroClass = HeroClass.NINJA,
            abilityLevel = 2,
            attackerId = "h1",
            targetId = "e1",
            durationMs = 1500L
        )

        composeTestRule.setContent {
            BattleSceneTestContainer(
                heroClass = HeroClass.NINJA,
                animInfo = animInfo
            )
        }

        playAnimationInRealTime(1500L)
    }

    @Test
    fun testDragoonJump() {
        val animInfo = AbilityAnimationInfo(
            heroClass = HeroClass.DRAGOON,
            abilityLevel = 2,
            attackerId = "h1",
            targetId = "e1",
            durationMs = 1500L
        )

        composeTestRule.setContent {
            BattleSceneTestContainer(
                heroClass = HeroClass.DRAGOON,
                animInfo = animInfo
            )
        }

        playAnimationInRealTime(1500L)
    }

    @Test
    fun testPaladinHoly() {
        val animInfo = AbilityAnimationInfo(
            heroClass = HeroClass.PALADIN,
            abilityLevel = 2,
            attackerId = "h1",
            targetId = "e1",
            durationMs = 1500L
        )

        composeTestRule.setContent {
            BattleSceneTestContainer(
                heroClass = HeroClass.PALADIN,
                animInfo = animInfo
            )
        }

        playAnimationInRealTime(1500L)
    }
}
