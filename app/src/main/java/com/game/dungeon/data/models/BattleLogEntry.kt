package com.game.dungeon.data.models

enum class LogType { HERO_ACTION, ENEMY_ACTION, ABILITY, SYSTEM }

data class BattleLogEntry(
    val message: String,
    val type: LogType
)
