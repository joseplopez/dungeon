package com.game.dungeon.data.models

enum class BattleSpeed(val delayMs: Long, val speedFactor: Float) {
    NORMAL(800L, 1.0f),
    FAST(267L, 3.0f),
    ULTRAFAST(150L, 4.0f),
    HYPER(80L, 10.0f),
    INSTANT(0L, 100.0f)
}
