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

val sampleMonsters = listOf(
    "Goblin",
    "Wolf",
    "Pirate",
    "Ogre",
    "Evil Eye",
    "Red Flan",
    "Bomb",
    "Sahagin",
    "Cockatrice",
    "Garland",
    "Lich",
    "Kraken",
    "Tiamat",
    "Chaos",
    "Wild Rat",
    "Dark Knight",
    "Lamia",
    "Adamantoise",
    "Tonberry",
    "Djinn",
    "Hein",
    "Emperor",
    "Cloud of Darkness"
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

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun GoblinSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored FF1 Goblin (16x18 Grid)",
                color = Color(0xFFFFD700),
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color(0xFF253B80)) // Classic FF1 JRPG Blue
                    .padding(16.dp)
            ) {
                EnemySprite(
                    enemyName = "Goblin",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun GarlandSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored FF1 Garland (22x26 Grid)",
                color = Color(0xFFFFD700),
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color(0xFF253B80)) // Classic FF1 JRPG Blue
                    .padding(16.dp)
            ) {
                EnemySprite(
                    enemyName = "Garland",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun LamiaSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored Lamia (20x24 Grid)",
                color = Color(0xFFFFD700),
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color(0xFF253B80)) // Classic FF1 JRPG Blue
                    .padding(16.dp)
            ) {
                EnemySprite(
                    enemyName = "Lamia",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 1000)
@Composable
fun AllMonsterSpritesPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(12.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize()
        ) {
            items(sampleMonsters) { monsterName ->
                MonsterSpriteItem(monsterName = monsterName)
            }
        }
    }
}
