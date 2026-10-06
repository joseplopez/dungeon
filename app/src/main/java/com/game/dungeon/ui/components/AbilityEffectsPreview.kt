package com.game.dungeon.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.dungeon.R
import com.game.dungeon.data.models.AIPriority
import com.game.dungeon.data.models.Enemy
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.models.MonsterType
import com.game.dungeon.ui.theme.GoldBright
import com.game.dungeon.ui.viewmodels.DungeonViewModel.AbilityAnimationInfo

private val previewHeroes = listOf(
    Hero(id = "h1", heroClass = HeroClass.BLACK_MAGE, name = "Vivi", currentHp = 100, currentMp = 50, aiPriority = AIPriority.ATTACK),
    Hero(id = "h2", heroClass = HeroClass.WHITE_MAGE, name = "Rosa", currentHp = 120, currentMp = 80, aiPriority = AIPriority.HEAL),
    Hero(id = "h3", heroClass = HeroClass.WARRIOR, name = "Cecil", currentHp = 250, currentMp = 30, aiPriority = AIPriority.ATTACK),
    Hero(id = "h4", heroClass = HeroClass.SUMMONER, name = "Rydia", currentHp = 110, currentMp = 90, aiPriority = AIPriority.ATTACK)
)

private val previewEnemies = listOf(
    Enemy(id = "e1", type = MonsterType.BEHEMOTH, name = "Behemoth", emoji = "👹", currentHp = 300, maxHp = 300, attack = 50, defense = 20, gilReward = 100, floor = 10, isBoss = true)
)

@Composable
fun AbilityEffectVisualCard(
    title: String,
    animInfo: AbilityAnimationInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B2F)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GoldBright)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$title (Level ${animInfo.abilityLevel})",
                color = GoldBright,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFF0F0F1A), shape = RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF3A3A52), shape = RoundedCornerShape(8.dp))
            ) {
                // Background Battle Field Simulation
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(start = 24.dp)
                            .size(64.dp)
                    ) {
                        animInfo.heroClass?.let { hClass ->
                            HeroSprite(heroClass = hClass, modifier = Modifier.fillMaxSize())
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(end = 24.dp)
                            .size(64.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawBehemoth()
                        }
                    }
                }

                // Ability Effect Overlay
                AbilityEffectsOverlay(
                    animInfo = animInfo,
                    heroes = previewHeroes,
                    enemies = previewEnemies,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(widthDp = 400, heightDp = 800)
@Composable
fun BlackMageAbilityLevelsPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A12))
            .padding(8.dp)
    ) {
        Text(
            text = "Black Mage Fireball / Elemental Spells",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(12.dp)
        )

        AbilityEffectVisualCard(
            title = "Level 1: Fire",
            animInfo = AbilityAnimationInfo(
                heroClass = HeroClass.BLACK_MAGE,
                abilityLevel = 1,
                abilityNameRes = R.string.skill_black_mage_t1_name,
                attackerId = "h1",
                targetId = "e1"
            )
        )

        AbilityEffectVisualCard(
            title = "Level 2: Fira / Dual Elemental",
            animInfo = AbilityAnimationInfo(
                heroClass = HeroClass.BLACK_MAGE,
                abilityLevel = 2,
                abilityNameRes = R.string.skill_black_mage_t2_name,
                attackerId = "h1",
                targetId = "e1"
            )
        )

        AbilityEffectVisualCard(
            title = "Level 3: Firaga / Meteor Apocalypse",
            animInfo = AbilityAnimationInfo(
                heroClass = HeroClass.BLACK_MAGE,
                abilityLevel = 3,
                abilityNameRes = R.string.skill_black_mage_t3_name,
                attackerId = "h1",
                targetId = "e1"
            )
        )
    }
}

@Preview(widthDp = 400, heightDp = 800)
@Composable
fun WhiteMageAbilityLevelsPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A12))
            .padding(8.dp)
    ) {
        Text(
            text = "White Mage Cure / Holy Spells",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(12.dp)
        )

        AbilityEffectVisualCard(
            title = "Level 1: Cure",
            animInfo = AbilityAnimationInfo(
                heroClass = HeroClass.WHITE_MAGE,
                abilityLevel = 1,
                abilityNameRes = R.string.skill_white_mage_t1_name,
                attackerId = "h2",
                targetId = "h2"
            )
        )

        AbilityEffectVisualCard(
            title = "Level 2: Cura Sacred Ring",
            animInfo = AbilityAnimationInfo(
                heroClass = HeroClass.WHITE_MAGE,
                abilityLevel = 2,
                abilityNameRes = R.string.skill_white_mage_t2_name,
                attackerId = "h2",
                targetId = "h2"
            )
        )

        AbilityEffectVisualCard(
            title = "Level 3: Curaga Holy Sanctuary",
            animInfo = AbilityAnimationInfo(
                heroClass = HeroClass.WHITE_MAGE,
                abilityLevel = 3,
                abilityNameRes = R.string.skill_white_mage_t3_name,
                attackerId = "h2",
                targetId = "h2"
            )
        )
    }
}

@Preview(widthDp = 400, heightDp = 900)
@Composable
fun SummonsGalleryPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A12))
            .padding(8.dp)
    ) {
        Text(
            text = "Eidolon Summons (with Pixel Sprites)",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(12.dp)
        )

        AbilityEffectVisualCard(
            title = "Summon Ifrit (Hellfire)",
            animInfo = AbilityAnimationInfo(
                heroClass = HeroClass.SUMMONER,
                isSummon = true,
                summonName = "Ifrit",
                abilityLevel = 2,
                attackerId = "h4",
                targetId = "e1"
            )
        )

        AbilityEffectVisualCard(
            title = "Summon Shiva (Diamond Dust)",
            animInfo = AbilityAnimationInfo(
                heroClass = HeroClass.SUMMONER,
                isSummon = true,
                summonName = "Shiva",
                abilityLevel = 2,
                attackerId = "h4",
                targetId = "e1"
            )
        )

        AbilityEffectVisualCard(
            title = "Summon Ramuh (Judgment Bolt)",
            animInfo = AbilityAnimationInfo(
                heroClass = HeroClass.SUMMONER,
                isSummon = true,
                summonName = "Ramuh",
                abilityLevel = 2,
                attackerId = "h4",
                targetId = "e1"
            )
        )

        AbilityEffectVisualCard(
            title = "Summon Bahamut (Giga Flare)",
            animInfo = AbilityAnimationInfo(
                heroClass = HeroClass.SUMMONER,
                isSummon = true,
                summonName = "Bahamut",
                abilityLevel = 3,
                attackerId = "h4",
                targetId = "e1"
            )
        )
    }
}

@Preview(widthDp = 400, heightDp = 800)
@Composable
fun WarriorOmnislashPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A12))
            .padding(8.dp)
    ) {
        Text(
            text = "Warrior Slash / Omnislash",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(12.dp)
        )

        AbilityEffectVisualCard(
            title = "Level 1: Double Slash",
            animInfo = AbilityAnimationInfo(
                heroClass = HeroClass.WARRIOR,
                abilityLevel = 1,
                abilityNameRes = R.string.skill_warrior_t1_name,
                attackerId = "h3",
                targetId = "e1"
            )
        )

        AbilityEffectVisualCard(
            title = "Level 2: Cross Slash",
            animInfo = AbilityAnimationInfo(
                heroClass = HeroClass.WARRIOR,
                abilityLevel = 2,
                abilityNameRes = R.string.skill_warrior_t2_name,
                attackerId = "h3",
                targetId = "e1"
            )
        )

        AbilityEffectVisualCard(
            title = "Level 3: Master Omnislash",
            animInfo = AbilityAnimationInfo(
                heroClass = HeroClass.WARRIOR,
                abilityLevel = 3,
                abilityNameRes = R.string.skill_warrior_t3_name,
                attackerId = "h3",
                targetId = "e1"
            )
        )
    }
}

@Preview(widthDp = 400, heightDp = 1000)
@Composable
fun AllAbilitiesInteractiveViewer() {
    var selectedLevel by remember { mutableStateOf(2) }
    val classes = HeroClass.entries

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B14))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ability Viewer",
                color = GoldBright,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1, 2, 3).forEach { lvl ->
                    Button(
                        onClick = { selectedLevel = lvl },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedLevel == lvl) GoldBright else Color(0xFF2E2342)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Lvl $lvl", color = if (selectedLevel == lvl) Color.Black else Color.White)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(classes) { heroClass ->
                AbilityEffectVisualCard(
                    title = heroClass.name.replace("_", " "),
                    animInfo = AbilityAnimationInfo(
                        heroClass = heroClass,
                        abilityLevel = selectedLevel,
                        attackerId = "h1",
                        targetId = "e1"
                    )
                )
            }
        }
    }
}
