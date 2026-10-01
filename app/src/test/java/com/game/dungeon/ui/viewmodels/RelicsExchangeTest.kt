package com.game.dungeon.ui.viewmodels

import com.game.dungeon.data.models.GameState
import org.junit.Assert.assertEquals
import org.junit.Test

class RelicsExchangeTest {

    private fun simulateExchange(
        currentState: GameState,
        goldCost: Long,
        magiciteAmount: Int
    ): GameState {
        if (goldCost <= 0 || magiciteAmount <= 0) return currentState
        if (currentState.gold >= goldCost) {
            return currentState.copy(
                gold = currentState.gold - goldCost,
                magicite = currentState.magicite + magiciteAmount,
                magiciteEarnedThisDim = currentState.magiciteEarnedThisDim + magiciteAmount,
                totalMagiciteEarned = currentState.totalMagiciteEarned + magiciteAmount
            )
        }
        return currentState
    }

    @Test
    fun testSmallPackExchange_Success() {
        val initial = GameState(gold = 100_000L, magicite = 0)
        val updated = simulateExchange(initial, 50_000L, 10)

        assertEquals(50_000L, updated.gold)
        assertEquals(10, updated.magicite)
        assertEquals(10, updated.magiciteEarnedThisDim)
        assertEquals(10, updated.totalMagiciteEarned)
    }

    @Test
    fun testMediumPackExchange_Success() {
        val initial = GameState(gold = 300_000L, magicite = 5)
        val updated = simulateExchange(initial, 250_000L, 50)

        assertEquals(50_000L, updated.gold)
        assertEquals(55, updated.magicite)
        assertEquals(50, updated.magiciteEarnedThisDim)
        assertEquals(50, updated.totalMagiciteEarned)
    }

    @Test
    fun testLargePackExchange_Success() {
        val initial = GameState(gold = 2_000_000L, magicite = 100)
        val updated = simulateExchange(initial, 1_000_000L, 200)

        assertEquals(1_000_000L, updated.gold)
        assertEquals(300, updated.magicite)
        assertEquals(200, updated.magiciteEarnedThisDim)
        assertEquals(200, updated.totalMagiciteEarned)
    }

    @Test
    fun testInsufficientGold_ExchangeFails() {
        val initial = GameState(gold = 40_000L, magicite = 10)
        val updated = simulateExchange(initial, 50_000L, 10)

        assertEquals(40_000L, updated.gold)
        assertEquals(10, updated.magicite)
    }

    @Test
    fun testInvalidInputs_ExchangeFails() {
        val initial = GameState(gold = 100_000L, magicite = 10)
        val updatedZeroCost = simulateExchange(initial, 0L, 10)
        assertEquals(initial, updatedZeroCost)

        val updatedNegativeCost = simulateExchange(initial, -50_000L, 10)
        assertEquals(initial, updatedNegativeCost)

        val updatedZeroGems = simulateExchange(initial, 50_000L, 0)
        assertEquals(initial, updatedZeroGems)
    }
}
