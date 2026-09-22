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
            .height(46.dp)
            .background(BgDarkest)
    ) {
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(
                "inn" to "⚗ INN",
                "town" to "🏰 TOWN",
                "leaderboard" to "🏆 RANK"
            ).forEach { (route, label) ->
                val active = currentRoute == route
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (active) BgMedium else Color.Transparent)
                        .drawWithContent {
                            drawContent()
                            val strokePx = 2.dp.toPx()
                            if (active) {
                                // Gold top highlight bar and border accent
                                drawRect(
                                    color = GoldBright,
                                    topLeft = Offset(0f, 0f),
                                    size = Size(size.width, strokePx)
                                )
                                drawRect(
                                    color = GoldDark,
                                    topLeft = Offset(0f, strokePx),
                                    size = Size(size.width, size.height - strokePx),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                                )
                            } else {
                                // Divider line on right side of inactive tabs
                                drawLine(
                                    color = StoneGray.copy(alpha = 0.4f),
                                    start = Offset(size.width - 1.dp.toPx(), 4.dp.toPx()),
                                    end = Offset(size.width - 1.dp.toPx(), size.height - 4.dp.toPx()),
                                    strokeWidth = 1.dp.toPx()
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
