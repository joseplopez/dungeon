package com.game.dungeon.monetization

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResourceShopTest {

    @Test
    fun testInAppProductDefinitions() {
        val gilProducts = InAppProduct.GIL_PRODUCTS
        assertEquals(3, gilProducts.size)
        
        val smallGil = gilProducts.find { it.id == InAppProduct.GIL_PACK_SMALL }
        assertNotNull(smallGil)
        assertEquals(ResourceType.GIL, smallGil!!.resourceType)
        assertEquals(5_000L, smallGil.rewardAmount)

        val mediumGil = gilProducts.find { it.id == InAppProduct.GIL_PACK_MEDIUM }
        assertNotNull(mediumGil)
        assertEquals(25_000L, mediumGil!!.rewardAmount)

        val largeGil = gilProducts.find { it.id == InAppProduct.GIL_PACK_LARGE }
        assertNotNull(largeGil)
        assertEquals(100_000L, largeGil!!.rewardAmount)

        val magiciteProducts = InAppProduct.MAGICITE_PRODUCTS
        assertEquals(3, magiciteProducts.size)

        val smallMagicite = magiciteProducts.find { it.id == InAppProduct.MAGICITE_PACK_SMALL }
        assertNotNull(smallMagicite)
        assertEquals(ResourceType.MAGICITE, smallMagicite!!.resourceType)
        assertEquals(75L, smallMagicite.rewardAmount)

        val mediumMagicite = magiciteProducts.find { it.id == InAppProduct.MAGICITE_PACK_MEDIUM }
        assertNotNull(mediumMagicite)
        assertEquals(500L, mediumMagicite!!.rewardAmount)

        val largeMagicite = magiciteProducts.find { it.id == InAppProduct.MAGICITE_PACK_LARGE }
        assertNotNull(largeMagicite)
        assertEquals(2000L, largeMagicite!!.rewardAmount)
    }

    @Test
    fun testAllProductIds() {
        val allIds = InAppProduct.ALL_PRODUCT_IDS
        assertEquals(6, allIds.size)
        assertTrue(allIds.contains(InAppProduct.GIL_PACK_SMALL))
        assertTrue(allIds.contains(InAppProduct.GIL_PACK_MEDIUM))
        assertTrue(allIds.contains(InAppProduct.GIL_PACK_LARGE))
        assertTrue(allIds.contains(InAppProduct.MAGICITE_PACK_SMALL))
        assertTrue(allIds.contains(InAppProduct.MAGICITE_PACK_MEDIUM))
        assertTrue(allIds.contains(InAppProduct.MAGICITE_PACK_LARGE))
    }

    @Test
    fun testAdRewardCalculations() {
        val totalGilEarned = 50_000L
        val gilReward = (totalGilEarned * 0.0025f).toLong().coerceAtLeast(100L)
        assertEquals(125L, gilReward)

        val lowGilEarned = 100L
        val minGilReward = (lowGilEarned * 0.0025f).toLong().coerceAtLeast(100L)
        assertEquals(100L, minGilReward)

        val totalMagiciteEarned = 4_000
        val magiciteReward = (totalMagiciteEarned * 0.005f).toInt().coerceAtLeast(10)
        assertEquals(20, magiciteReward)

        val lowMagiciteEarned = 10
        val minMagiciteReward = (lowMagiciteEarned * 0.005f).toInt().coerceAtLeast(10)
        assertEquals(10, minMagiciteReward)
    }
}
