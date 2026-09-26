package com.game.dungeon.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.game.dungeon.data.models.BiomeType

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun SeaShrinePreview() {
    Box(Modifier.fillMaxSize()) {
        DungeonBackground(biomeType = BiomeType.SEA_SHRINE)
    }
}
