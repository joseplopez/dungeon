package com.game.dungeon.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.game.dungeon.MainActivity
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.repository.GameRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class MasteryUITest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var repository: GameRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testJobDetailFullScreenView_OpenAndDismiss() {
        // Script State: Ensure Warrior job is unlocked and available
        runBlocking {
            val baseState = repository.getGameStateOnce() ?: GameState()
            val scriptedState = baseState.copy(
                unlockedJobs = setOf(HeroClass.FREELANCER, HeroClass.WARRIOR, HeroClass.WHITE_MAGE),
                jobMasteryLevels = mapOf(HeroClass.WARRIOR to 5),
                innLevel = 1
            )
            repository.saveGameState(scriptedState)
        }

        composeTestRule.waitForIdle()

        // 1. Navigate to Mastery Screen via Bottom Nav ("📈 MASTERIES")
        composeTestRule.onNodeWithText("📈 MASTERIES").performClick()
        composeTestRule.waitForIdle()

        // 2. Verify Training Grounds Header is displayed
        composeTestRule.onNodeWithText("TRAINING GROUNDS").assertIsDisplayed()

        // 3. Click on the Warrior Job Row
        composeTestRule.onNodeWithTag("JobMasteryRow_WARRIOR").performClick()
        composeTestRule.waitForIdle()

        // 4. Assert JobDetailFullScreenView is displayed with ability details & base stats
        composeTestRule.onNodeWithTag("JobDetailDialog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Power Strike").assertIsDisplayed()
        composeTestRule.onNodeWithText("Charged Ability").assertIsDisplayed()
        composeTestRule.onNodeWithText("Base Statistics").assertIsDisplayed()

        // 5. Dismiss View by clicking BACK
        composeTestRule.onNodeWithText("BACK").performClick()
        composeTestRule.waitForIdle()

        // 6. Verify Full Screen View is dismissed and we're back on Mastery Screen
        composeTestRule.onNodeWithTag("JobDetailDialog").assertDoesNotExist()
        composeTestRule.onNodeWithText("TRAINING GROUNDS").assertIsDisplayed()
    }
}
