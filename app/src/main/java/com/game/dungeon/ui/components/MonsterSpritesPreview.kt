package com.game.dungeon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- CATEGORIZED MONSTER & BOSS LISTS ---

val regularMonsters = listOf(
    "Goblin", "Wolf", "Pirate", "Ogre", "Evil Eye", "Red Flan", "Bomb", "Sahagin", "Cockatrice",
    "Wild Rat", "Dark Knight", "Lamia", "Adamantoise", "Tonberry", "Zombie", "Wyvern", "Behemoth",
    "Toad", "Hellhound"
)

val ff1AndFF2Bosses = listOf(
    "Garland", "Astos", "Lich", "Marilith", "Kraken", "Tiamat", "Chaos",
    "Leon", "Borghen", "Gottos", "Roundworm", "Cyclone", "Emperor"
)

val ff3AndFF4Bosses = listOf(
    "Djinn", "Nepto Dragon", "Hein", "Garuda", "Goldor", "Xande", "Cloud of Darkness",
    "Mist Dragon", "Antlion", "Golbez", "Cagnazzo", "Barbariccia", "Scarmiglione", "Rubicante", "Dark Bahamut", "Zeromus"
)

val ff5AndFF6Bosses = listOf(
    "Wing Raptor", "Karlabos", "Ifrit", "Gilgamesh", "Atomos", "Exdeath", "Omega", "Neo Exdeath",
    "Whelk", "Vargas", "Number 024", "Ultros", "Typhon", "Air Force", "Guardian", "Ultima Weapon", "Kefka"
)

val ff7AndFF8Bosses = listOf(
    "Guard Scorpion", "Airbuster", "Rufus", "Hojo", "Bizarro Sephiroth", "Sephiroth", "Jenova",
    "NORG", "Edea", "Fujin & Raijin", "Seifer", "Adel", "Trauma", "Omega Weapon", "Ultimecia"
)

val ff9AndFF10Bosses = listOf(
    "Plant Brain", "Black Waltz", "Zorn & Thorn", "Ralvurahva", "Maliris", "Kuja", "Trance Kuja", "Necron",
    "Klikk", "Oblitzerator", "Evrae", "Seymour", "Jecht", "Penance", "Yu Yevon", "Sin"
)

@Composable
fun MonsterSpriteItem(monsterName: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1B24))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color(0xFF2E2342))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                EnemySprite(
                    enemyName = monsterName,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = monsterName,
                color = Color.White,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun MonsterGridPreview(title: String, monsters: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(12.dp)
    ) {
        Text(
            text = title,
            color = Color(0xFFFFD700),
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp, start = 6.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize()
        ) {
            items(monsters) { monsterName ->
                MonsterSpriteItem(monsterName = monsterName)
            }
        }
    }
}

// --- FOCUSED CATEGORIZED PREVIEWS ---

@Preview(name = "1. Regular Monsters", widthDp = 640, heightDp = 750)
@Composable
fun RegularMonstersPreview() {
    MonsterGridPreview(
        title = "Regular Monsters (19 Sprites)",
        monsters = regularMonsters
    )
}

@Preview(name = "2. FF1 & FF2 Bosses", widthDp = 640, heightDp = 580)
@Composable
fun FF1AndFF2BossesPreview() {
    MonsterGridPreview(
        title = "FF1 & FF2 Bosses (13 Sprites)",
        monsters = ff1AndFF2Bosses
    )
}

@Preview(name = "3. FF3 & FF4 Bosses", widthDp = 640, heightDp = 680)
@Composable
fun FF3AndFF4BossesPreview() {
    MonsterGridPreview(
        title = "FF3 & FF4 Bosses (16 Sprites)",
        monsters = ff3AndFF4Bosses
    )
}

@Preview(name = "4. FF5 & FF6 Bosses", widthDp = 640, heightDp = 720)
@Composable
fun FF5AndFF6BossesPreview() {
    MonsterGridPreview(
        title = "FF5 & FF6 Bosses (17 Sprites)",
        monsters = ff5AndFF6Bosses
    )
}

@Preview(name = "5. FF7 & FF8 Bosses", widthDp = 640, heightDp = 650)
@Composable
fun FF7AndFF8BossesPreview() {
    MonsterGridPreview(
        title = "FF7 & FF8 Bosses (15 Sprites)",
        monsters = ff7AndFF8Bosses
    )
}

@Preview(name = "6. FF9 & FF10 Bosses", widthDp = 640, heightDp = 680)
@Composable
fun FF9AndFF10BossesPreview() {
    MonsterGridPreview(
        title = "FF9 & FF10 Bosses (16 Sprites)",
        monsters = ff9AndFF10Bosses
    )
}
