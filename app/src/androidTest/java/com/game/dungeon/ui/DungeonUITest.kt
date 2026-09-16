package com.game.dungeon.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.game.dungeon.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class DungeonUITest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testInnScreen_InitialState() {
        composeTestRule.waitForIdle()
        // Verify we start at THE INN
        composeTestRule.onNodeWithText("THE INN").assertIsDisplayed()
        
        // Verify Hire Panel is present
        composeTestRule.onNodeWithText("HIRE WARRIORS").assertIsDisplayed()
        
        // Verify Party section is present
        composeTestRule.onAllNodesWithText("PARTY", substring = true).onFirst().assertIsDisplayed()
    }

    @Test
    fun testHeroHiringAndFiring() {
        composeTestRule.waitForIdle()
        // Hiring buttons in InnScreen.kt show the cost, e.g. "0G" for Freelancer.
        composeTestRule.onNodeWithText("0G").performClick()
        composeTestRule.waitForIdle()

        // Verify hero appears in party.
        composeTestRule.onAllNodesWithText("Freelancer").onFirst().assertIsDisplayed()

        // Test Firing the hero (X button in PartyMemberCard)
        composeTestRule.onAllNodesWithText("X").onFirst().performClick()
        composeTestRule.waitForIdle()

        // Verify hero is gone from party or at least we see an EMPTY SLOT
        composeTestRule.onNodeWithText("EMPTY SLOT").assertIsDisplayed()
    }

    @Test
    fun testNavigation_ToTown_AndOpenUpgrades() {
        composeTestRule.waitForIdle()
        // Navigate to Town via BottomNav
        composeTestRule.onNodeWithText("🏰 TOWN").performClick()
        composeTestRule.waitForIdle()
        
        // Verify Town is displayed
        composeTestRule.onNodeWithText("GRAND CAPITAL").assertIsDisplayed()
        
        // Scroll to the right to bring buildings into view
        composeTestRule.onNode(hasScrollAction()).performTouchInput { swipeLeft() }
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("BARRACKS").performClick()
        composeTestRule.waitForIdle()
        
        // Verify Upgrades Dialog is open
        composeTestRule.onNodeWithText("TOWN UPGRADES").assertIsDisplayed()
        
        // Close Dialog
        composeTestRule.onAllNodesWithText("X").onFirst().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("TOWN UPGRADES").assertDoesNotExist()
    }

    @Test
    fun testNavigation_ToRelics() {
        composeTestRule.waitForIdle()
        // Navigate to Town via BottomNav
        composeTestRule.onNodeWithText("🏰 TOWN").performClick()
        composeTestRule.waitForIdle()
        
        // Scroll to the right to ensure RELICS building is on screen
        composeTestRule.onNode(hasScrollAction()).performTouchInput { swipeLeft() }
        composeTestRule.onNode(hasScrollAction()).performTouchInput { swipeLeft() }
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("RELICS").performClick()
        composeTestRule.waitForIdle()
        
        // Verify Relics Screen
        composeTestRule.onNodeWithText("RELICS").assertIsDisplayed()
        
        // Go back to Town
        composeTestRule.onNodeWithText("◀ BACK").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("GRAND CAPITAL").assertIsDisplayed()
    }

    @Test
    fun testNavigation_ToCrystalShop() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("🏰 TOWN").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNode(hasScrollAction()).performTouchInput { swipeLeft() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("CRYSTAL SHOP").performClick()
        composeTestRule.waitForIdle()

        // Verify Crystal Shop Dialog
        composeTestRule.onNodeWithText("Unlock new job classes").assertIsDisplayed()
        
        // Close Dialog
        composeTestRule.onAllNodesWithText("X").onFirst().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Unlock new job classes").assertDoesNotExist()
    }

    @Test
    fun testNavigation_ToMasteries() {
        composeTestRule.waitForIdle()
        // Masteries button is in InnScreen
        composeTestRule.onNodeWithText("📈 MASTERIES").performClick()
        composeTestRule.waitForIdle()

        // Verify Mastery Screen
        composeTestRule.onNodeWithText("TRAINING GROUNDS").assertIsDisplayed()
        composeTestRule.onNodeWithText("JOB MASTERY").assertIsDisplayed()
        composeTestRule.onNodeWithText("PETS").assertIsDisplayed()

        // Switch to PETS Tab
        composeTestRule.onNodeWithText("PETS").performClick()
        composeTestRule.waitForIdle()

        // Verify pets list is active and displays correct headers
        composeTestRule.onNodeWithText("SELECT A COMPANION").assertIsDisplayed()
        composeTestRule.onNodeWithText("NONE").assertIsDisplayed()
        
        // Go back via Back button
        composeTestRule.onNodeWithText("◀ BACK").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("THE INN").assertIsDisplayed()
    }

    @Test
    fun testNavigation_ToLeaderboard() {
        composeTestRule.waitForIdle()
        // Navigate via BottomNav
        composeTestRule.onNodeWithText("🏆 RANK").performClick()
        composeTestRule.waitForIdle()
        
        // Verify Leaderboard Screen Title
        composeTestRule.onNodeWithText("RANKINGS").assertIsDisplayed()
        
        // Verify Tabs
        composeTestRule.onNodeWithText("GLOBAL").assertIsDisplayed()
        composeTestRule.onNodeWithText("DIMENSION").assertIsDisplayed()
        composeTestRule.onNodeWithText("FRIENDS").assertIsDisplayed()

        // Go back to Inn
        composeTestRule.onNodeWithText("⚗ INN").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("THE INN").assertIsDisplayed()
    }

    @Test
    fun testEquipmentScreenFlow() {
        composeTestRule.waitForIdle()
        // 1. Hire a hero first
        composeTestRule.onNodeWithText("0G").performClick()
        composeTestRule.waitForIdle()

        // 2. Open Equipment screen
        composeTestRule.onNodeWithText("EQUIP").performClick()
        composeTestRule.waitForIdle()

        // 3. Verify Equipment Screen UI
        composeTestRule.onNodeWithText("STATS").assertIsDisplayed()
        composeTestRule.onNodeWithText("EQUIPPED").assertIsDisplayed()
        composeTestRule.onNodeWithText("INVENTORY").assertIsDisplayed()

        // 4. Go back
        composeTestRule.onNodeWithText("◀ BACK").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("THE INN").assertIsDisplayed()
    }

    @Test
    fun testDungeonRunFlow() {
        composeTestRule.waitForIdle()
        // 1. Hire a hero
        composeTestRule.onNodeWithText("0G").performClick()
        composeTestRule.waitForIdle()

        // 2. Enter Dungeon
        composeTestRule.onNodeWithText("⚔ ENTER DUNGEON ⚔").performClick()
        composeTestRule.waitForIdle()

        // 3. Verify Dungeon Screen
        composeTestRule.onNodeWithText("FLOOR 1", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("◀ RETREAT").assertIsDisplayed()

        // 4. Retreat
        composeTestRule.onNodeWithText("◀ RETREAT").performClick()
        composeTestRule.waitForIdle()

        // 5. Verify Run Complete / Return to Inn
        composeTestRule.onNodeWithText("⚔ RUN COMPLETE").assertIsDisplayed()
        composeTestRule.onNodeWithText("RETURN TO INN").performClick()
        composeTestRule.waitForIdle()

        // 6. Back at Inn
        composeTestRule.onNodeWithText("THE INN").assertIsDisplayed()
    }
}
