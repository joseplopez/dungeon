package com.game.dungeon.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.game.dungeon.MainActivity
import com.game.dungeon.data.models.CrystalColor
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
class HiddenJobsUITest {

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
    fun testHiddenJobUnlockDialog_AndShopClueRow() {
        // Script state: Unlock all standard crystals, set lifetimeHighestFloor to 1000 to unlock Necromancer, but leave notifiedHiddenJobs empty
        runBlocking {
            val baseState = repository.getGameStateOnce() ?: GameState()
            val crystalMap = CrystalColor.entries
                .filter { it != CrystalColor.CLEAR && it != CrystalColor.HIDDEN }
                .associateWith { true }
            val standardJobs = HeroClass.entries.filter { it.tier == 1 || it.tier == 2 }.toSet()
            
            val scriptedState = baseState.copy(
                crystals = crystalMap,
                unlockedJobs = baseState.unlockedJobs + standardJobs,
                lifetimeHighestFloor = 1000,
                notifiedHiddenJobs = emptySet(),
                gold = 5000,
                innLevel = 1
            )
            repository.saveGameState(scriptedState)
        }

        composeTestRule.waitForIdle()

        // 1. Verify Hidden Job Unlock Dialog triggers immediately upon entering Inn
        composeTestRule.onNodeWithText("CONGRATULATIONS!").assertIsDisplayed()
        composeTestRule.onNodeWithText("You have discovered a legendary hidden job: Necromancer!", substring = true).assertIsDisplayed()

        // 2. Dismiss celebration dialog
        composeTestRule.onNodeWithText("AMAZING!").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("CONGRATULATIONS!").assertDoesNotExist()

        // 3. Navigate to Town via BottomNav
        composeTestRule.onNodeWithText("🏰 TOWN").performClick()
        composeTestRule.waitForIdle()

        // Scroll to the right to see the Crystal Shop
        composeTestRule.onNode(hasScrollAction()).performTouchInput { swipeLeft() }
        composeTestRule.onNode(hasScrollAction()).performTouchInput { swipeLeft() }
        composeTestRule.waitForIdle()

        // 4. Open Crystal Shop
        composeTestRule.onNodeWithText("CRYSTAL SHOP").performClick()
        composeTestRule.waitForIdle()

        // 5. Assert the mystery locked row "???" is visible
        composeTestRule.onNodeWithText("???").assertIsDisplayed()
        composeTestRule.onNodeWithText("A mysterious path awaits...").assertIsDisplayed()

        // Close Crystal Shop
        composeTestRule.onAllNodesWithText("X").onFirst().performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun testHiddenJobFullCycle_PurchaseHiringAndRun() {
        // 1. Script State: All standard jobs unlocked, all 4 hidden jobs discovered (not purchased), 
        // high Gil, high Barracks level
        runBlocking {
            val baseState = repository.getGameStateOnce() ?: GameState()
            
            // Standard crystals owned
            val crystalMap = CrystalColor.entries
                .filter { it.baseCost > 0 && !it.name.startsWith("HIDDEN") }
                .associateWith { true }
            
            val standardJobs = HeroClass.entries.filter { it.tier == 1 || it.tier == 2 }.toSet()
            
            val scriptedState = baseState.copy(
                crystals = crystalMap,
                unlockedJobs = standardJobs + HeroClass.FREELANCER,
                lifetimeHighestFloor = 1000, // Necromancer discovered
                currentDimension = 4,        // Mime discovered
                bossesDefeatedNames = (1..10).map { "Boss $it" }.toSet(), // Blue Mage discovered
                jobMasteryLevels = mapOf(HeroClass.WARRIOR to 100), // Onion Knight discovered
                gold = 50000, // Enough to buy all
                barracksLevel = 2, // Party size 5
                innLevel = 1,
                notifiedHiddenJobs = HeroClass.entries.filter { it.tier == 3 }.toSet() // Already seen dialogs
            )
            repository.saveGameState(scriptedState)
        }

        composeTestRule.waitForIdle()

        // 2. Go to Town -> Crystal Shop
        composeTestRule.onNodeWithText("🏰 TOWN").performClick()
        composeTestRule.waitForIdle()
        
        // Scroll to Crystal Shop
        composeTestRule.onNode(hasScrollAction()).performTouchInput { swipeLeft(); swipeLeft() }
        composeTestRule.onNodeWithText("CRYSTAL SHOP").performClick()
        composeTestRule.waitForIdle()

        // 3. Purchase all 4 hidden crystals (they should be there because discovered)
        val hiddenCrystals = listOf(
            "Onion Crystal" to "HIDDEN_ONION",
            "Mime Crystal" to "HIDDEN_MIME",
            "Death Crystal" to "HIDDEN_NECRO",
            "Lore Crystal" to "HIDDEN_BLUE"
        )
        hiddenCrystals.forEach { (_, enumName) ->
            // Scroll inside dialog to find the crystal row
            composeTestRule.onNode(hasScrollAction() and hasAnyAncestor(isDialog()))
                .performTouchInput { swipeUp() }
            
            // Buy button is a descendant of the tagged row
            composeTestRule.onNode(hasAnyAncestor(hasTestTag("CrystalRow_$enumName")) and hasText("G", substring = true)).performClick()
            composeTestRule.waitForIdle()
        }

        // Close Crystal Shop
        composeTestRule.onAllNodesWithText("X").onFirst().performClick()
        composeTestRule.waitForIdle()

        // 4. Go to Inn
        composeTestRule.onNodeWithText("⚗ INN").performClick()
        composeTestRule.waitForIdle()

        // 5. Hire the 4 hidden heroes
        val hiddenJobEnumNames = listOf("ONION_KNIGHT", "MIME", "NECROMANCER", "BLUE_MAGE")
        hiddenJobEnumNames.forEach { jobEnumName ->
            // Scroll inside the Hire Panel's LazyColumn (it's the first scrollable one on the left)
            composeTestRule.onAllNodes(hasScrollAction()).onFirst()
                .performScrollToNode(hasTestTag("HireRow_$jobEnumName"))

            // Hire button is a descendant of the tagged hire row
            composeTestRule.onNode(hasAnyAncestor(hasTestTag("HireRow_$jobEnumName")) and hasText("G", substring = true) and hasClickAction()).performClick()
            composeTestRule.waitForIdle()
        }

        // 6. Enter Dungeon
        composeTestRule.onNodeWithText("⚔ ENTER DUNGEON ⚔").performClick()
        composeTestRule.waitForIdle()

        // 7. Verify all 4 hidden jobs are in the party (on screen) via test tags
        val hiddenJobEnumNamesParty = listOf("ONION_KNIGHT", "MIME", "NECROMANCER", "BLUE_MAGE")
        hiddenJobEnumNamesParty.forEach { enumName ->
            composeTestRule.onNodeWithTag("HeroUnit_$enumName").assertIsDisplayed()
        }
    }
}
