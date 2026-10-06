package com.game.dungeon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.dungeon.ui.theme.BgMedium
import com.game.dungeon.ui.theme.GoldBright
import com.game.dungeon.ui.theme.GoldDark

@Composable
fun PauseToggleButton(
    isPaused: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .background(if (isPaused) GoldBright.copy(alpha = 0.3f) else BgMedium)
            .border(1.dp, if (isPaused) GoldBright else GoldDark)
            .clickable { onToggle() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isPaused) "▶" else "⏸",
            fontSize = 16.sp
        )
    }
}
