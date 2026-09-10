package com.game.dungeon.data.models

enum class BattleSpeed(val delayMs: Long) {
    NORMAL(800L),
    FAST(400L),
    ULTRAFAST(150L)
}
