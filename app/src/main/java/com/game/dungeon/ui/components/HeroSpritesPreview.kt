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
import com.game.dungeon.data.models.HeroClass

@Composable
fun HeroSpriteItem(heroClass: HeroClass, modifier: Modifier = Modifier) {
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
                HeroSprite(
                    heroClass = heroClass,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = heroClass.name.replace("_", " "),
                color = Color.White,
                fontSize = 10.sp
            )
        }
    }
}

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun WarriorSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored Warrior (64x64 Detail)",
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
                HeroSprite(
                    heroClass = HeroClass.WARRIOR,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun WhiteMageSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored White Mage (64x64 Detail)",
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
                HeroSprite(
                    heroClass = HeroClass.WHITE_MAGE,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun BlackMageSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored Black Mage (22x26 Exact Grid)",
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
                HeroSprite(
                    heroClass = HeroClass.BLACK_MAGE,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun FreelancerSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored Freelancer (18x24 Exact Grid)",
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
                HeroSprite(
                    heroClass = HeroClass.FREELANCER,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun MonkSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored Monk (18x25 Exact Grid)",
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
                HeroSprite(
                    heroClass = HeroClass.MONK,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun RedMageSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored Red Mage (18x24 Exact Grid)",
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
                HeroSprite(
                    heroClass = HeroClass.RED_MAGE,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun ThiefSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored Thief (22x25 Exact Grid)",
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
                HeroSprite(
                    heroClass = HeroClass.THIEF,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun DragoonSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored Dragoon (15x26 Exact Grid)",
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
                HeroSprite(
                    heroClass = HeroClass.DRAGOON,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun NinjaSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored Ninja (16x26 Exact Grid)",
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
                HeroSprite(
                    heroClass = HeroClass.NINJA,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun SummonerSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored Summoner (16x25 Grid)",
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
                HeroSprite(
                    heroClass = HeroClass.SUMMONER,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


@Preview(widthDp = 640, heightDp = 640)
@Composable
fun PaladinSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored Paladin (16x24 Cecil Grid)",
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
                HeroSprite(
                    heroClass = HeroClass.PALADIN,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun SamuraiSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored Samurai (16x24 Cyan Grid)",
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
                HeroSprite(
                    heroClass = HeroClass.SAMURAI,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 640)
@Composable
fun BardSpritePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Refactored Bard (22x26 Edward Grid)",
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
                HeroSprite(
                    heroClass = HeroClass.BARD,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 840)
@Composable
fun AllHeroSpritesPreview() {
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
            items(HeroClass.entries) { heroClass ->
                HeroSpriteItem(heroClass = heroClass)
            }
        }
    }
}
