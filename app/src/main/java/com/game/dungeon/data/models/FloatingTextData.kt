package com.game.dungeon.data.models

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class FloatingTextData(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val x: Float,
    val y: Float,
    val color: Color,
    val targetId: String? = null
)
