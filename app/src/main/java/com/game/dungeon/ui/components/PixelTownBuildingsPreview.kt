package com.game.dungeon.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview(widthDp = 400, heightDp = 250)
@Composable
fun TownParallaxBackgroundPreview() {
    Box(Modifier.fillMaxSize()) {
        TownParallaxBackground(scrollOffset = 100f)
    }
}
