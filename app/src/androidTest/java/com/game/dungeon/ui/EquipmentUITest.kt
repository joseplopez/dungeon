package com.game.dungeon.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.models.Rarity
import com.game.dungeon.ui.components.PixelButton
import com.game.dungeon.ui.components.PixelPanel
import com.game.dungeon.ui.screens.InventoryChestPanel
import com.game.dungeon.ui.theme.PixelTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EquipmentUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testInventoryChestPanel_SellAllButtonTriggersDialogAndConfirm() {
        var sellAllActionCount = 0

        val sampleItems = listOf(
            Item("1", "Old Sword", ItemSlot.WEAPON, Rarity.COMMON, attackBonus = 5, emoji = "⚔️", floorFound = 1),
            Item("2", "Old Shield", ItemSlot.SHIELD, Rarity.COMMON, defenseBonus = 3, emoji = "🛡️", floorFound = 1)
        )

        composeTestRule.setContent {
            PixelTheme {
                var inventory by remember { mutableStateOf(sampleItems) }
                var showConfirmation by remember { mutableStateOf(false) }

                Box(Modifier.fillMaxSize()) {
                    InventoryChestPanel(
                        inventory = inventory,
                        onSelectItem = {},
                        onSellAllClick = { showConfirmation = true }
                    )

                    if (showConfirmation && inventory.isNotEmpty()) {
                        PixelPanel(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column {
                                Text("SELL ALL GEAR?")
                                PixelButton(
                                    label = "CANCEL",
                                    onClick = { showConfirmation = false }
                                )
                                PixelButton(
                                    label = "CONFIRM SELL ALL",
                                    onClick = {
                                        sellAllActionCount++
                                        inventory = emptyList()
                                        showConfirmation = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        composeTestRule.waitForIdle()

        // Verify "SELL ALL" button is visible
        composeTestRule.onNodeWithText("SELL ALL").assertIsDisplayed()

        // Click "SELL ALL" button
        composeTestRule.onNodeWithText("SELL ALL").performClick()
        composeTestRule.waitForIdle()

        // Verify confirmation dialog title is displayed
        composeTestRule.onNodeWithText("SELL ALL GEAR?").assertIsDisplayed()

        // Click CONFIRM SELL ALL
        composeTestRule.onNodeWithText("CONFIRM SELL ALL").performClick()
        composeTestRule.waitForIdle()

        // Verify action executed
        assertEquals(1, sellAllActionCount)
    }
}
