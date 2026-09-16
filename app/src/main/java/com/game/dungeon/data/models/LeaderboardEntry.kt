package com.game.dungeon.data.models

data class LeaderboardEntry(
    val rank: Int,
    val playerName: String,
    val maxFloor: Int,
    val dimension: Int = 1,
    val dimensionMaxFloor: Int = 0,
    val totalMagicite: Int,
    val fastestClearMs: Long,
    val currentDimTimeMs: Long = 0,
    val isUser: Boolean = false,
    val team: List<Hero> = emptyList()
)
