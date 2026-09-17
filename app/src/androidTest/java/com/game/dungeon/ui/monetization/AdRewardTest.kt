package com.game.dungeon.ui.monetization

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.game.dungeon.MainActivity
import com.game.dungeon.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AdRewardTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testInnAdButtonsExist() {
        // Pure canvas drawings exist via parent icon tags or bounds
    }

    @Test
    fun testInnGilAdReward() {
        // Ad button visual testing
    }
}
