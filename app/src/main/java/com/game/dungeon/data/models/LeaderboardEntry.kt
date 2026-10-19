package com.game.dungeon.data.models

data class LeaderboardEntry(
    val rank: Int,
    val playerId: String? = null,
    val playerName: String,
    val maxFloor: Int,
    val maxBossFloor: Int = 0,
    val dimension: Int = 1,
    val dimensionMaxFloor: Int = 0,
    val totalMagicite: Long,
    val fastestClearMs: Long,
    val currentDimTimeMs: Long = 0,
    val isUser: Boolean = false,
    val team: List<Hero> = emptyList()
)
