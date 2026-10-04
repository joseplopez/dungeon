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
import com.game.dungeon.data.models.MonsterType

// --- CATEGORIZED MONSTER & BOSS LISTS ---

val regularMonsters = listOf(
    "Goblin", "Wolf", "Pirate", "Ogre", "Evil Eye", "Red Flan", "Bomb", "Sahagin", "Cockatrice",
    "Wild Rat", "Dark Knight", "Lamia", "Adamantoise", "Tonberry", "Zombie", "Wyvern", "Behemoth",
    "Toad", "Hellhound"
)

val ff1AndFF2Bosses = listOf("Emperor Mateus",
    "Garland", "Astos", "Lich", "Marilith", "Kraken", "Tiamat", "Chaos",
    "Leon", "Borghen", "Gottos", "Roundworm", "Cyclone"
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
fun MonsterSpriteItem() {

    LazyVerticalGrid(
        columns = GridCells.Fixed(12),
        modifier = Modifier.fillMaxSize()
    ) {
    for (monster in MonsterType.entries) {
        item {
        Card(
            modifier = Modifier.fillMaxWidth().padding(6.dp),
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
                        monster,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = monster.name,
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }
        }
    }
    }
}


// --- FOCUSED CATEGORIZED PREVIEWS ---


@Preview(name = "ALL", widthDp = 2000, heightDp = 3000)
@Composable
fun RegularMonstersALLPreview() {
    MonsterSpriteItem()
}

