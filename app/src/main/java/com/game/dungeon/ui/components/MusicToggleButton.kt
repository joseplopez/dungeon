package com.game.dungeon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.dungeon.ui.theme.BgMedium
import com.game.dungeon.ui.theme.GoldBright
import com.game.dungeon.ui.theme.GoldDark

@Composable
fun MusicToggleButton(
    isMuted: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp) // Slightly smaller for top bar integration
            .background(BgMedium)
            .border(1.dp, GoldDark) // Thinner border
            .clickable { onToggle() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isMuted) "🔇" else "🔊",
            fontSize = 16.sp
        )
    }
}
