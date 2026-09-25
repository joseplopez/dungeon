package com.game.dungeon.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.dungeon.ui.components.TownNpcSprite
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TownUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTownNpcSpritesRendering() {
        val npcIds = listOf("guard", "scholar", "adventurer", "merchant", "gladiator")

        npcIds.forEach { npcId ->
            val tag = "NpcSprite_$npcId"
            composeTestRule.setContent {
                Box(Modifier.size(100.dp).testTag(tag)) {
                    TownNpcSprite(npcId = npcId, modifier = Modifier.fillMaxSize())
                }
            }
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag(tag).assertIsDisplayed()
        }
    }
}
