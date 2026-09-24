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
class PathfinderUITest {

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
    fun testPathfinderSelectorVisible_WhenPathfinderPurchasedAtFloor1() {
        runBlocking {
            val baseState = repository.getGameStateOnce() ?: GameState()
            val scriptedState = baseState.copy(
                pathfinderLevel = 1,
                highestFloor = 1,
                notifiedHiddenJobs = HeroClass.entries.filter { it.tier == 3 }.toSet()
            )
            repository.saveGameState(scriptedState)
        }

        composeTestRule.waitForIdle()

        // Verify Pathfinder selector is displayed even at highestFloor = 1
        composeTestRule.onNodeWithTag("PathfinderFloorSelector").assertIsDisplayed()
        composeTestRule.onNodeWithText("1 / 1", substring = true).assertIsDisplayed()
    }

    @Test
    fun testPathfinderFloorIncrement_ClampedToMaxFloor() {
        runBlocking {
            val baseState = repository.getGameStateOnce() ?: GameState()
            val scriptedState = baseState.copy(
                pathfinderLevel = 4,
                highestFloor = 30,
                notifiedHiddenJobs = HeroClass.entries.filter { it.tier == 3 }.toSet()
            )
            repository.saveGameState(scriptedState)
        }

        composeTestRule.waitForIdle()

        // Verify max floor is 30 for Pathfinder level 4 at highestFloor 30
        composeTestRule.onNodeWithTag("PathfinderFloorSelector").assertIsDisplayed()
        composeTestRule.onNodeWithText("1 / 30", substring = true).assertIsDisplayed()

        // Click +5
        composeTestRule.onNodeWithText("+5").performClick()
        composeTestRule.waitForIdle()

        // Verify start floor is now 6 / 30
        composeTestRule.onNodeWithText("6 / 30", substring = true).assertIsDisplayed()

        // Click -1
        composeTestRule.onNodeWithText("-1").performClick()
        composeTestRule.waitForIdle()

        // Verify start floor is now 5 / 30
        composeTestRule.onNodeWithText("5 / 30", substring = true).assertIsDisplayed()
    }
}
