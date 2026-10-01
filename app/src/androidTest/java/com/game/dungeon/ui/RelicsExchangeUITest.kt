package com.game.dungeon.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.dungeon.ui.screens.GoldExchangeDialog
import com.game.dungeon.ui.theme.PixelTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RelicsExchangeUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testGoldExchangeDialog_SufficientGold_RendersAndTriggersExchange() {
        var exchangedCost = 0L
        var exchangedMagicite = 0

        composeTestRule.setContent {
            PixelTheme {
                GoldExchangeDialog(
                    currentGold = 100_000L,
                    currentMagicite = 20,
                    onExchange = { cost, amount ->
                        exchangedCost = cost
                        exchangedMagicite = amount
                    },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.waitForIdle()

        // Check header title or elements
        composeTestRule.onNodeWithText("GOLD TO GEMS EXCHANGE", substring = true, ignoreCase = true).assertIsDisplayed()

        // Find exchange button and click
        composeTestRule.onAllNodesWithText("Exchange", ignoreCase = true)[0].performClick()

        composeTestRule.waitForIdle()

        assertEquals(50_000L, exchangedCost)
        assertEquals(10, exchangedMagicite)
    }

    @Test
    fun testGoldExchangeDialog_InsufficientGold_ShowsNeedGil() {
        composeTestRule.setContent {
            PixelTheme {
                GoldExchangeDialog(
                    currentGold = 10_000L,
                    currentMagicite = 0,
                    onExchange = { _, _ -> },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.waitForIdle()

        // Check that insufficient funds notice/button text is displayed
        composeTestRule.onNodeWithText("Need", substring = true, ignoreCase = true).assertIsDisplayed()
    }
}
