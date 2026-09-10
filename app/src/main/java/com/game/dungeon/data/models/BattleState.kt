package com.game.dungeon.data.models

data class BattleState(
    val currentFloor: Int = 1,
    val heroes: List<Hero> = emptyList(),
    val enemies: List<Enemy> = emptyList(),
    val battleLog: List<BattleLogEntry> = emptyList(),
    val isRunning: Boolean = false,
    val speed: BattleSpeed = BattleSpeed.NORMAL,
    val goldEarned: Long = 0,
    val itemsFound: List<Item> = emptyList(),
    val bossesKilled: Int = 0,
    val floatingTexts: List<FloatingTextData> = emptyList(),
    val recentlyHitIds: Set<String> = emptySet(),
    val showFloorComplete: Boolean = false,
    val recentLoot: Item? = null,
    val attackingUnitId: String? = null,
    val hitUnitId: String? = null,
    val screenShake: Boolean = false,
    val impactSparks: List<ImpactSpark> = emptyList()
)

data class ImpactSpark(val x: Float, val y: Float, val id: String = java.util.UUID.randomUUID().toString())
