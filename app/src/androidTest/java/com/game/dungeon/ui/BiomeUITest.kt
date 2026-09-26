package com.game.dungeon.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.dungeon.data.models.BiomeType
import com.game.dungeon.ui.components.DungeonBackground
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BiomeUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun testBiomeRendering(biomeType: BiomeType) {
        val tag = "DungeonBg_${biomeType.name}"
        composeTestRule.setContent {
            Box(Modifier.fillMaxSize().testTag(tag)) {
                DungeonBackground(biomeType = biomeType)
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(tag).assertIsDisplayed()
        Thread.sleep(5000)
    }

    @Test
    fun testBiome_CorneliaCastle() {
        testBiomeRendering(BiomeType.CORNELIA_CASTLE)
    }

    @Test
    fun testBiome_ChaosShrine() {
        testBiomeRendering(BiomeType.CHAOS_SHRINE)
    }

    @Test
    fun testBiome_GurguVolcano() {
        testBiomeRendering(BiomeType.GURGU_VOLCANO)
    }

    @Test
    fun testBiome_SeaShrine() {
        testBiomeRendering(BiomeType.SEA_SHRINE)
    }

    @Test
    fun testBiome_EarthCave() {
        testBiomeRendering(BiomeType.EARTH_CAVE)
    }

    @Test
    fun testBiome_CrystalTower() {
        testBiomeRendering(BiomeType.CRYSTAL_TOWER)
    }

    @Test
    fun testBiome_MysidianTower() {
        testBiomeRendering(BiomeType.MYSIDIAN_TOWER)
    }

    @Test
    fun testBiome_Pandaemonium() {
        testBiomeRendering(BiomeType.PANDAEMONIUM)
    }

    @Test
    fun testBiome_MountOrdeals() {
        testBiomeRendering(BiomeType.MOUNT_ORDEALS)
    }

    @Test
    fun testBiome_BaronCastle() {
        testBiomeRendering(BiomeType.BARON_CASTLE)
    }

    @Test
    fun testBiome_AncientCastle() {
        testBiomeRendering(BiomeType.ANCIENT_CASTLE)
    }

    @Test
    fun testBiome_NarsheMines() {
        testBiomeRendering(BiomeType.NARSHE_MINES)
    }

    @Test
    fun testBiome_MagitekFactory() {
        testBiomeRendering(BiomeType.MAGITEK_FACTORY)
    }

    @Test
    fun testBiome_KefkaTower() {
        testBiomeRendering(BiomeType.KEFKA_TOWER)
    }

    @Test
    fun testBiome_FloatingContinent() {
        testBiomeRendering(BiomeType.FLOATING_CONTINENT)
    }

    @Test
    fun testBiome_MidgarSewers() {
        testBiomeRendering(BiomeType.MIDGAR_SEWERS)
    }

    @Test
    fun testBiome_ShinraBuilding() {
        testBiomeRendering(BiomeType.SHINRA_BUILDING)
    }

    @Test
    fun testBiome_NorthernCrater() {
        testBiomeRendering(BiomeType.NORTHERN_CRATER)
    }

    @Test
    fun testBiome_GoldenSaucer() {
        testBiomeRendering(BiomeType.GOLDEN_SAUCER)
    }

    @Test
    fun testBiome_BevelleTemple() {
        testBiomeRendering(BiomeType.BEVELLE_TEMPLE)
    }

    @Test
    fun testBiome_OmegaRuins() {
        testBiomeRendering(BiomeType.OMEGA_RUINS)
    }

    @Test
    fun testBiome_SinInterior() {
        testBiomeRendering(BiomeType.SIN_INTERIOR)
    }

    @Test
    fun testBiome_GenericDungeon() {
        testBiomeRendering(BiomeType.GENERIC_DUNGEON)
    }
}
