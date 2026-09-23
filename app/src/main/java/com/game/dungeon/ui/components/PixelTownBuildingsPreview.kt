package com.game.dungeon.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(widthDp = 200, heightDp = 150)
@Composable
fun ColosseumPreview() {
    Box(Modifier.size(200.dp, 150.dp).background(Color(0xFF111827))) {
        Canvas(Modifier.fillMaxSize()) {
            drawDetailedColosseum()
        }
    }
}
