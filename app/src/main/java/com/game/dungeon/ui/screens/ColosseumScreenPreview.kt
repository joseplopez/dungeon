package com.game.dungeon.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.dungeon.data.models.AIPriority
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.models.LeaderboardEntry
import com.game.dungeon.ui.components.GoldenBorderBox
import com.game.dungeon.ui.components.MusicToggleButton
import com.game.dungeon.ui.components.HeroSprite
import com.game.dungeon.ui.components.PixelButton
import com.game.dungeon.ui.components.PixelDivider
import com.game.dungeon.ui.components.PixelPanel
import com.game.dungeon.ui.theme.*

// Mock Data for Previews
private val mockHero1 = Hero(heroClass = HeroClass.WARRIOR, name = "Warrior", currentHp = 250, currentMp = 30, level = 45, aiPriority = AIPriority.ATTACK)
private val mockHero2 = Hero(heroClass = HeroClass.BLACK_MAGE, name = "Black Mage", currentHp = 140, currentMp = 180, level = 44, aiPriority = AIPriority.MAGIC)
private val mockHero3 = Hero(heroClass = HeroClass.WHITE_MAGE, name = "White Mage", currentHp = 160, currentMp = 160, level = 44, aiPriority = AIPriority.HEAL)
private val mockHero4 = Hero(heroClass = HeroClass.NINJA, name = "Ninja", currentHp = 210, currentMp = 80, level = 42, aiPriority = AIPriority.ATTACK)

private val mockLeaderboard = listOf(
    LeaderboardEntry(playerId = "1", playerName = "GarlandX", maxFloor = 120, dimension = 5, totalMagicite = 8500, fastestClearMs = 1420000L, rank = 1, isUser = false, team = listOf(mockHero1, mockHero2, mockHero3, mockHero4)),
    LeaderboardEntry(playerId = "2", playerName = "Cecil_Paladin", maxFloor = 115, dimension = 5, totalMagicite = 7200, fastestClearMs = 1580000L, rank = 2, isUser = false, team = listOf(mockHero1, mockHero3)),
    LeaderboardEntry(playerId = "3", playerName = "Shadow_Ninja", maxFloor = 108, dimension = 4, totalMagicite = 6400, fastestClearMs = 1750000L, rank = 3, isUser = false, team = listOf(mockHero4, mockHero2)),
    LeaderboardEntry(playerId = "4", playerName = "Stranger (You)", maxFloor = 95, dimension = 4, totalMagicite = 5100, fastestClearMs = 2100000L, rank = 4, isUser = true, team = listOf(mockHero1, mockHero2, mockHero3)),
    LeaderboardEntry(playerId = "5", playerName = "Bartz_Freelancer", maxFloor = 88, dimension = 3, totalMagicite = 4200, fastestClearMs = 2450000L, rank = 5, isUser = false, team = listOf(mockHero1, mockHero2))
)

// ============================================================================
// OPTION 1 PREVIEW: Grand Arena Colosseum Dashboard
// Features a mini Colosseum Arena Canvas header banner + Top 3 Medals + Team Sprites inside table rows
// ============================================================================

@Composable
fun ColosseumOption1GrandDashboard() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedEntry by remember { mutableStateOf<LeaderboardEntry?>(mockLeaderboard[3]) }

    PixelTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(BgDarkest)
                .padding(6.dp)
        ) {
            Column(Modifier.fillMaxSize()) {
                // Top Header Bar with Back Button
                GoldenBorderBox(
                    Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .background(BgDark)
                ) {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PixelButton(
                            label = "◀ BACK",
                            onClick = {},
                            modifier = Modifier.height(28.dp)
                        )
                        Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("🏛️", fontSize = 16.sp)
                            Text("COLOSSEUM ARENA RANKINGS", style = PixelHeading, color = GoldBright)
                        }
                        Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("🪙 1,250G", style = PixelGold)
                            Text("💎 45", style = PixelGold)
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Colosseum Arena Graphic Header & Champion Status Bar
                GoldenBorderBox(
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color(0xFF140D1F))
                ) {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Player Champion Crest
                            Box(
                                Modifier
                                    .size(44.dp)
                                    .background(BgDarkest, RoundedCornerShape(4.dp))
                                    .border(1.5.dp, GoldBright, RoundedCornerShape(4.dp)),
                                contentAlignment = Center
                            ) {
                                Column(horizontalAlignment = CenterHorizontally) {
                                    Text("#4", style = PixelHeading, color = GoldBright, fontSize = 14.sp)
                                    Text("RANK", style = PixelSmall, color = StoneGray, fontSize = 8.sp)
                                }
                            }
                            Column {
                                Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Stranger (You)", style = PixelBody, color = GoldBright)
                                    Text("✏️", fontSize = 11.sp, modifier = Modifier.clickable { })
                                }
                                Text("ID: e8f3a12b • DIMENSION 4 • FLOOR 95", style = PixelSmall, color = StoneGray)
                            }
                        }

                        Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PixelButton("COPY ID", onClick = {}, modifier = Modifier.height(28.dp))
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Colosseum Arena Category Tabs
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PixelButton("🏟️ GLOBAL ARENA", onClick = { selectedTab = 0 }, active = selectedTab == 0, modifier = Modifier.weight(1f))
                    PixelButton("🌌 DIMENSION LEAGUE", onClick = { selectedTab = 1 }, active = selectedTab == 1, modifier = Modifier.weight(1f))
                    PixelButton("🤝 FRIENDS HALL", onClick = { selectedTab = 2 }, active = selectedTab == 2, modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(6.dp))

                // Rankings Table with Team Sprites inside rows
                GoldenBorderBox(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(BgDark)
                ) {
                    Column(Modifier.fillMaxSize()) {
                        // Table Header
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(BgMedium)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = CenterVertically
                        ) {
                            Text("RANK", style = PixelSmall, color = GoldBright, modifier = Modifier.width(42.dp))
                            Text("GLADIATOR / TEAM", style = PixelSmall, color = GoldBright, modifier = Modifier.weight(1f))
                            Text("MAX FLR", style = PixelSmall, color = GoldBright, modifier = Modifier.width(55.dp), textAlign = TextAlign.Center)
                            Text("MAG", style = PixelSmall, color = GoldBright, modifier = Modifier.width(55.dp), textAlign = TextAlign.Center)
                            Text("CLEAR TIME", style = PixelSmall, color = GoldBright, modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
                        }

                        LazyColumn(Modifier.fillMaxSize()) {
                            items(mockLeaderboard) { entry ->
                                val isSelected = selectedEntry?.playerId == entry.playerId
                                val rowBg = when {
                                    entry.isUser -> GoldBright.copy(alpha = 0.15f)
                                    isSelected -> Color(0xFF2C1F3D)
                                    else -> Color.Transparent
                                }

                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(rowBg)
                                        .clickable { selectedEntry = entry }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = CenterVertically
                                ) {
                                    // Rank Badge
                                    Box(Modifier.width(42.dp)) {
                                        when (entry.rank) {
                                            1 -> Text("🥇 #1", style = PixelSmall, color = GoldBright)
                                            2 -> Text("🥈 #2", style = PixelSmall, color = Color(0xFFC0C0C0))
                                            3 -> Text("🥉 #3", style = PixelSmall, color = Color(0xFFCD7F32))
                                            else -> Text("#${entry.rank}", style = PixelSmall, color = if (entry.isUser) GoldBright else Color.White)
                                        }
                                    }

                                    // Player Name & Hero Team Sprites
                                    Row(
                                        Modifier.weight(1f),
                                        verticalAlignment = CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            entry.playerName,
                                            style = PixelSmall,
                                            color = if (entry.isUser) GoldBright else Color.White,
                                            modifier = Modifier.width(110.dp)
                                        )

                                        // Mini Hero Team Canvas Sprites
                                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            entry.team.take(4).forEach { hero ->
                                                Box(
                                                    Modifier
                                                        .size(22.dp)
                                                        .background(BgDarkest, RoundedCornerShape(2.dp))
                                                        .border(0.5.dp, GoldDark, RoundedCornerShape(2.dp))
                                                ) {
                                                    HeroSprite(hero.heroClass, Modifier.fillMaxSize())
                                                }
                                            }
                                        }
                                    }

                                    Text("${entry.maxFloor}", style = PixelSmall, color = Color.White, modifier = Modifier.width(55.dp), textAlign = TextAlign.Center)
                                    Text("${entry.totalMagicite}", style = PixelSmall, color = Color.White, modifier = Modifier.width(55.dp), textAlign = TextAlign.Center)
                                    Text("23m 40s", style = PixelSmall, color = Color.White, modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
                                }
                                PixelDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// OPTION 2 PREVIEW: Dual-Pane Colosseum Arena (Left: Champion Card & Team Roster, Right: Ranked Leaderboard)
// ============================================================================

@Composable
fun ColosseumOption2DualPane() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedEntry by remember { mutableStateOf<LeaderboardEntry>(mockLeaderboard[0]) }

    PixelTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(BgDarkest)
                .padding(6.dp)
        ) {
            Column(Modifier.fillMaxSize()) {
                // Header
                GoldenBorderBox(
                    Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .background(BgDark)
                ) {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PixelButton("◀ BACK", onClick = {}, modifier = Modifier.height(28.dp))
                        Text("🏛️ COLOSSEUM ARENA STANDINGS", style = PixelHeading, color = GoldBright)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🪙 1,250G", style = PixelGold)
                            Text("💎 45", style = PixelGold)
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Dual Pane Layout
                Row(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // LEFT PANEL (36% width): Selected Champion Card & Full Roster Sprites
                    GoldenBorderBox(
                        Modifier
                            .weight(0.36f)
                            .fillMaxSize()
                            .background(Color(0xFF120B1A))
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = CenterHorizontally) {
                                Text("🏆 CHAMPION CARD", style = PixelHeading, color = GoldBright, fontSize = 12.sp)
                                Spacer(Modifier.height(4.dp))

                                // Rank Emblem
                                Box(
                                    Modifier
                                        .size(50.dp)
                                        .background(BgDarkest, RoundedCornerShape(6.dp))
                                        .border(2.dp, GoldBright, RoundedCornerShape(6.dp)),
                                    contentAlignment = Center
                                ) {
                                    Column(horizontalAlignment = CenterHorizontally) {
                                        Text("#${selectedEntry.rank}", style = PixelHeading, color = GoldBright, fontSize = 16.sp)
                                        Text("RANK", style = PixelSmall, color = StoneGray, fontSize = 8.sp)
                                    }
                                }

                                Spacer(Modifier.height(6.dp))
                                Text(selectedEntry.playerName, style = PixelBody, color = GoldBright)
                                Text("Dimension ${selectedEntry.dimension} • Floor ${selectedEntry.maxFloor}", style = PixelSmall, color = StoneGray)

                                Spacer(Modifier.height(8.dp))
                                PixelDivider()
                                Spacer(Modifier.height(8.dp))

                                Text("PARTY ROSTER", style = PixelSmall, color = GoldBright, fontSize = 10.sp)
                                Spacer(Modifier.height(6.dp))

                                // Large Team Sprites & Classes
                                Column(
                                    Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    selectedEntry.team.forEach { hero ->
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .background(BgDark, RoundedCornerShape(4.dp))
                                                .padding(4.dp),
                                            verticalAlignment = CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(Modifier.size(32.dp)) {
                                                HeroSprite(hero.heroClass, Modifier.fillMaxSize())
                                            }
                                            Column {
                                                Text(hero.name, style = PixelSmall, color = Color.White)
                                                Text("Lvl ${hero.level} ${hero.heroClass.name}", style = PixelSmall, color = GoldDark, fontSize = 9.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            PixelButton("⚔️ VIEW GHOST RUN", onClick = {}, modifier = Modifier.fillMaxWidth().height(32.dp))
                        }
                    }

                    // RIGHT PANEL (64% width): Ranked Leaderboard Standings Table
                    Column(
                        Modifier
                            .weight(0.64f)
                            .fillMaxSize()
                    ) {
                        // Category Tabs
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            PixelButton("GLOBAL", onClick = { selectedTab = 0 }, active = selectedTab == 0, modifier = Modifier.weight(1f).height(30.dp))
                            PixelButton("DIMENSION", onClick = { selectedTab = 1 }, active = selectedTab == 1, modifier = Modifier.weight(1f).height(30.dp))
                            PixelButton("FRIENDS", onClick = { selectedTab = 2 }, active = selectedTab == 2, modifier = Modifier.weight(1f).height(30.dp))
                        }

                        Spacer(Modifier.height(4.dp))

                        // Table
                        GoldenBorderBox(
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(BgDark)
                        ) {
                            LazyColumn(Modifier.fillMaxSize()) {
                                items(mockLeaderboard) { entry ->
                                    val isSelected = selectedEntry.playerId == entry.playerId
                                    val rowBg = if (isSelected) Color(0xFF2C1F3D) else Color.Transparent

                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .background(rowBg)
                                            .clickable { selectedEntry = entry }
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = CenterVertically
                                    ) {
                                        Text("#${entry.rank}", style = PixelSmall, color = if (entry.rank <= 3) GoldBright else Color.White, modifier = Modifier.width(36.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(entry.playerName, style = PixelSmall, color = if (entry.isUser) GoldBright else Color.White)
                                            Text("Dim ${entry.dimension} • Floor ${entry.maxFloor}", style = PixelSmall, color = StoneGray, fontSize = 9.sp)
                                        }
                                        Text("💎 ${entry.totalMagicite}", style = PixelSmall, color = GoldBright)
                                    }
                                    PixelDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// OPTION 3 PREVIEW: Coliseum Hall of Fame (3D Medal Podium Header + List)
// ============================================================================

@Composable
fun ColosseumOption3PodiumHeader() {
    var selectedTab by remember { mutableIntStateOf(0) }

    PixelTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(BgDarkest)
                .padding(6.dp)
        ) {
            Column(Modifier.fillMaxSize()) {
                // Header
                GoldenBorderBox(
                    Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .background(BgDark)
                ) {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PixelButton("◀ BACK", onClick = {}, modifier = Modifier.height(28.dp))
                        Text("🏛️ COLOSSEUM HALL OF FAME", style = PixelHeading, color = GoldBright)
                        MusicToggleButton(isMuted = false, onToggle = {})
                    }
                }

                Spacer(Modifier.height(6.dp))

                // TOP 3 PODIUM HEADER BOX
                GoldenBorderBox(
                    Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(Color(0xFF120B1C))
                ) {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // SILVER (#2)
                        PodiumPedestal(entry = mockLeaderboard[1], title = "🥈 SILVER", heightDp = 70, borderColor = Color(0xFFC0C0C0))

                        // GOLD (#1 CENTER)
                        PodiumPedestal(entry = mockLeaderboard[0], title = "🥇 GOLD CHAMPION", heightDp = 90, borderColor = GoldBright)

                        // BRONZE (#3)
                        PodiumPedestal(entry = mockLeaderboard[2], title = "🥉 BRONZE", heightDp = 60, borderColor = Color(0xFFCD7F32))
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Tabs
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PixelButton("GLOBAL", onClick = { selectedTab = 0 }, active = selectedTab == 0, modifier = Modifier.weight(1f))
                    PixelButton("DIMENSION", onClick = { selectedTab = 1 }, active = selectedTab == 1, modifier = Modifier.weight(1f))
                    PixelButton("FRIENDS", onClick = { selectedTab = 2 }, active = selectedTab == 2, modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(6.dp))

                // Remaining Rankings List
                GoldenBorderBox(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(BgDark)
                ) {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(mockLeaderboard) { entry ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("#${entry.rank}", style = PixelBody, color = if (entry.isUser) GoldBright else Color.White)
                                    Column {
                                        Text(entry.playerName, style = PixelBody, color = if (entry.isUser) GoldBright else Color.White)
                                        Text("Floor ${entry.maxFloor} • Magicite ${entry.totalMagicite}", style = PixelSmall, color = StoneGray)
                                    }
                                }
                                PixelButton("VIEW PARTY", onClick = {}, modifier = Modifier.height(28.dp))
                            }
                            PixelDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PodiumPedestal(entry: LeaderboardEntry, title: String, heightDp: Int, borderColor: Color) {
    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        // Hero Sprites on top of pedestal
        Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
            entry.team.take(2).forEach { hero ->
                Box(Modifier.size(24.dp)) {
                    HeroSprite(hero.heroClass, Modifier.fillMaxSize())
                }
            }
        }
        Text(entry.playerName, style = PixelSmall, color = borderColor, maxLines = 1, fontSize = 9.sp)

        Box(
            Modifier
                .fillMaxWidth()
                .height(heightDp.dp)
                .background(BgMedium, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .border(1.5.dp, borderColor, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
            contentAlignment = Center
        ) {
            Column(horizontalAlignment = CenterHorizontally) {
                Text(title, style = PixelSmall, color = borderColor, fontSize = 8.sp)
                Text("FLR ${entry.maxFloor}", style = PixelHeading, color = GoldBright, fontSize = 11.sp)
            }
        }
    }
}

// Previews
@Preview(device = "spec:width=1280dp,height=720dp,orientation=landscape")
@Composable
fun PreviewColosseumOption1() {
    ColosseumOption1GrandDashboard()
}

@Preview(device = "spec:width=1280dp,height=720dp,orientation=landscape")
@Composable
fun PreviewColosseumOption2() {
    ColosseumOption2DualPane()
}

@Preview(device = "spec:width=1280dp,height=720dp,orientation=landscape")
@Composable
fun PreviewColosseumOption3() {
    ColosseumOption3PodiumHeader()
}
