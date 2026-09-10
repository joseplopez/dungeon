package com.game.dungeon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.game.dungeon.ui.theme.*

@Composable
fun BottomPixelNav(currentRoute: String?, navController: NavController) {
    GoldenBorderBox(
        Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(BgDarkest)
    ) {
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(
                "inn" to "⚗ INN",
                "town" to "🏰 TOWN"
            ).forEach { (route, label) ->
                val active = currentRoute == route
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (active) GoldBright.copy(alpha = 0.15f) else Color.Transparent)
                        .drawWithContent {
                            drawContent()
                            if (active) {
                                drawRect(
                                    color = GoldBright,
                                    topLeft = Offset(0f, size.height - 2.dp.toPx()),
                                    size = Size(size.width, 2.dp.toPx())
                                )
                            }
                        }
                        .clickable {
                            if (!active) {
                                navController.navigate(route) {
                                    launchSingleTop = true
                                    popUpTo("inn") { saveState = true }
                                    restoreState = true
                                }
                            }
                        },
                    contentAlignment = Center
                ) {
                    Text(
                        label,
                        style = PixelSmall.copy(
                            color = if (active) GoldBright else StoneGray,
                            fontSize = if (active) 12.sp else 10.sp
                        )
                    )
                }
            }
        }
    }
}
