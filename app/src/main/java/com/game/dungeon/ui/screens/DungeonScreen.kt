package com.game.dungeon.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.game.dungeon.R
import com.game.dungeon.data.models.*
import com.game.dungeon.ui.components.*
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.DungeonViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun DungeonScreen(
    viewModel: DungeonViewModel,
    onBack: () -> Unit,
    isMuted: Boolean,
    onToggleMusic: () -> Unit,
    navController: NavController? = null
) {
    val state by viewModel.battleState.collectAsState()
    val dimension = state.dimension
    val isBossFloor = dimension != null && FFDimensionData.getBossForFloor(dimension, state.currentFloor) != null

    Box(Modifier.fillMaxSize()) {
        DungeonBackground()
        
        Column(Modifier.fillMaxSize()) {
            DungeonTopBar(
                floor = state.currentFloor,
                dimension = dimension,
                currentBiome = state.currentBiome,
                gilTotal = state.gilEarnedThisRun,
                magiciteTotal = state.magiciteEarnedThisRun,
                isBossFloor = isBossFloor,
                onRetreat = { viewModel.retreat() },
                speed = state.speed,
                onSpeedChange = { viewModel.setSpeed(it) },
                isMuted = isMuted,
                onToggleMusic = onToggleMusic
            )
            
            BattleArea(
                modifier = Modifier.weight(1f),
                heroes = state.heroes,
                enemies = state.enemies,
                dyingHeroIds = state.dyingHeroIds,
                attackingUnitId = state.attackingHeroId,
                hitEnemyId = state.hitEnemyId,
                hitHeroId = state.hitHeroId,
                isCritical = state.isCriticalHit,
                dimension = dimension,
                floor = state.currentFloor
            )

            BattleLogPanel(state.battleLog)
        }

        // Biome Transition Banner / Floor Complete
        AnimatedVisibility(
            visible = state.showFloorBanner,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut(),
            modifier = Modifier.align(Center)
        ) {
            val biome = state.currentBiome
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(
                        if (biome != null && dimension != null) {
                            Brush.horizontalGradient(listOf(Color(dimension.mainColor), Color(dimension.accentColor)))
                        } else {
                            Brush.horizontalGradient(listOf(GoldDark, GoldBright, GoldDark))
                        }
                    )
                    .padding(12.dp),
                contentAlignment = Center
            ) {
                Column(horizontalAlignment = CenterHorizontally) {
                    if (dimension != null) {
                        Text("⚔️ ${stringResource(dimension.subtitleRes)} ⚔️", style = PixelSmall, color = Color.White)
                    }
                    Text(state.floorBannerText, style = PixelHeading, color = if (biome != null) GoldBright else BgDarkest)
                }
            }
        }

        // Boss Banner
        AnimatedVisibility(
            visible = state.showBossBanner,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Center)
        ) {
            GoldenBorderBox(Modifier.padding(20.dp).background(EnemyRed.copy(alpha = 0.9f))) {
                Text(safeStringResource(R.string.log_boss_defeated, state.bossBannerText), style = PixelTitle, color = Color.White, modifier = Modifier.padding(16.dp))
            }
        }

        // Boss Pulse Border
        if (isBossFloor) {
            val infiniteTransition = rememberInfiniteTransition(label = "boss_pulse")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 0.6f,
                animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse),
                label = "alpha"
            )
            Canvas(Modifier.fillMaxSize()) {
                drawRect(color = EnemyRed.copy(alpha = pulseAlpha), style = Stroke(width = 8.dp.toPx()))
            }
        }

        // Run Complete Overlay
        if (state.runComplete) {
            RunCompleteOverlay(
                currentFloor = state.currentFloor,
                gilEarnedThisRun = state.gilEarnedThisRun,
                magiciteEarnedThisRun = state.magiciteEarnedThisRun,
                gilLostToPenalty = state.gilLostToPenalty,
                fallenHeroes = state.fallenHeroes,
                itemsFoundThisRun = state.itemsFoundThisRun,
                originalPartySize = state.originalPartySize,
                onReturn = {
                    navController?.navigate("inn") {
                        popUpTo("inn") { inclusive = true }
                    } ?: onBack()
                }
            )
        }
    }
}

@Composable
fun DungeonTopBar(
    floor: Int,
    dimension: FFDimension?,
    currentBiome: FFBiome?,
    gilTotal: Long,
    magiciteTotal: Int,
    isBossFloor: Boolean,
    onRetreat: () -> Unit,
    speed: BattleSpeed,
    onSpeedChange: (BattleSpeed) -> Unit,
    isMuted: Boolean,
    onToggleMusic: () -> Unit
) {
    GoldenBorderBox(Modifier.fillMaxWidth().height(54.dp).background(BgDarkest)) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PixelButton(stringResource(R.string.retreat_button), onClick = onRetreat, modifier = Modifier.height(32.dp))
            
            Column(horizontalAlignment = CenterHorizontally) {
                Text(safeStringResource(R.string.floor_format, floor), style = PixelHeading, color = GoldBright)
                if (dimension != null) {
                    Text(stringResource(dimension.titleRes), style = PixelSmall, color = Color(dimension.mainColor))
                }
                Text(currentBiome?.let { stringResource(it.nameRes) } ?: "", style = PixelSmall, color = StoneGray)
            }

            if (isBossFloor) {
                val alpha by rememberInfiniteTransition(label = "").animateFloat(
                    initialValue = 0.3f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse), label = ""
                )
                Text(stringResource(R.string.boss_floor_warning), style = PixelHeading.copy(color = EnemyRed.copy(alpha = alpha)))
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(BattleSpeed.NORMAL to "1x", BattleSpeed.FAST to "2x", BattleSpeed.ULTRAFAST to "4x").forEach { (s, label) ->
                        PixelButton(label, onClick = { onSpeedChange(s) }, active = (speed == s), modifier = Modifier.width(44.dp).height(32.dp))
                    }
                }
            }

            Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(horizontalAlignment = End, verticalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("🪙", fontSize = 12.sp)
                        Text(formatGold(gilTotal), style = PixelGold)
                    }
                    Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("💎", fontSize = 12.sp)
                        Text("$magiciteTotal", style = PixelGold)
                    }
                }
                MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
            }
        }
    }
}

@Composable
fun BattleArea(
    modifier: Modifier,
    heroes: List<Hero>,
    enemies: List<Enemy>,
    dyingHeroIds: Set<String>,
    attackingUnitId: String?,
    hitEnemyId: String?,
    hitHeroId: String?,
    isCritical: Boolean,
    dimension: FFDimension?,
    floor: Int
) {
    Box(modifier.fillMaxSize()) {
        // Heroes
        Row(
            Modifier.fillMaxHeight().fillMaxWidth(0.5f).align(Alignment.CenterStart).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = CenterVertically
        ) {
            heroes.forEach { hero ->
                key(hero.id) {
                    HeroUnitDisplay(
                        hero = hero,
                        isAttacking = attackingUnitId == hero.id,
                        isHit = hitHeroId == hero.id,
                        isCritical = isCritical && hitHeroId == hero.id,
                        isDying = dyingHeroIds.contains(hero.id)
                    )
                }
            }
        }

        // Enemies
        Row(
            Modifier.fillMaxHeight().fillMaxWidth(0.5f).align(Alignment.CenterEnd).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = CenterVertically
        ) {
            enemies.forEach { enemy ->
                key(enemy.id) {
                    val bossTemplate = dimension?.let { FFDimensionData.getBossForFloor(it, floor) }
                    val isBoss = bossTemplate != null && stringResource(bossTemplate.nameRes) == enemy.name
                    EnemyUnitDisplay(
                        enemy = enemy,
                        isHit = hitEnemyId == enemy.id,
                        isCritical = isCritical && hitEnemyId == enemy.id,
                        isBoss = isBoss
                    )
                }
            }
        }
    }
}

@Composable
fun HeroUnitDisplay(hero: Hero, isAttacking: Boolean, isHit: Boolean, isCritical: Boolean, isDying: Boolean) {
    val alphaAnim = remember { Animatable(1f) }
    val shakeOffset = remember { Animatable(0f) }
    
    LaunchedEffect(isDying) {
        if (isDying) {
            alphaAnim.animateTo(0f, tween(1000))
        }
    }

    LaunchedEffect(isHit) {
        if (isHit) {
            val intensity = if (isCritical) 12f else 6f
            repeat(3) {
                shakeOffset.animateTo(intensity, tween(30, easing = LinearEasing))
                shakeOffset.animateTo(-intensity, tween(30, easing = LinearEasing))
            }
            shakeOffset.animateTo(0f, tween(30))
        }
    }

    val lungeOffset by animateFloatAsState(
        targetValue = if (isAttacking) 24f else 0f,
        animationSpec = tween(100, easing = FastOutSlowInEasing),
        label = "lunge"
    )

    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier
            .offset(x = (lungeOffset + shakeOffset.value).dp)
            .graphicsLayer(alpha = alphaAnim.value)
    ) {
        if (!isDying) {
            PixelHpBar(hero.currentHp, hero.maxHp, Modifier.width(64.dp).height(10.dp))
            Spacer(Modifier.height(4.dp))
            PixelExpBar(hero.exp, hero.expToNextLevel, Modifier.width(64.dp).height(6.dp))
        } else {
            Text("RIP", style = PixelSmall, color = StoneGray, modifier = Modifier.padding(bottom = 12.dp))
        }
        
        Box(Modifier.size(80.dp)) {
            val flashAlpha = remember { Animatable(0f) }
            val scope = rememberCoroutineScope()
            
            LaunchedEffect(isHit) {
                if (isHit) {
                    scope.launch {
                        flashAlpha.snapTo(1f)
                        flashAlpha.animateTo(0f, tween(300))
                    }
                }
            }

            HeroSprite(
                hero.heroClass, 
                Modifier
                    .fillMaxSize()
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()
                        if (flashAlpha.value > 0f) {
                            drawRect(
                                color = (if (isCritical) GoldBright else EnemyRed).copy(alpha = flashAlpha.value),
                                blendMode = BlendMode.SrcIn
                            )
                        }
                    }
            )
            
            if (isDying) {
                // Dissolve effect
                Canvas(Modifier.fillMaxSize()) {
                    repeat(20) {
                        drawRect(
                            color = Color.Gray.copy(alpha = Random.nextFloat()),
                            topLeft = Offset(Random.nextFloat() * size.width, Random.nextFloat() * size.height),
                            size = Size(4f, 4f)
                        )
                    }
                }
            }
            if (isHit) {
                Box(Modifier.align(Center)) {
                    UnitHitParticles(isCritical)
                }
            }
        }
        Text(hero.name, style = PixelSmall, color = Color.White)
        
        if (!isDying && hero.hasSpecialAbility) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(3) { i ->
                    Box(Modifier.size(6.dp).background(if (i < hero.abilityCharge) GoldBright else StoneGray, CircleShape))
                }
            }
        }
    }
}

@Composable
fun EnemyUnitDisplay(enemy: Enemy, isHit: Boolean, isCritical: Boolean, isBoss: Boolean) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isHit) {
        if (isHit) {
            val intensity = if (isCritical) 12f else 6f
            repeat(3) {
                shakeOffset.animateTo(intensity, tween(30, easing = LinearEasing))
                shakeOffset.animateTo(-intensity, tween(30, easing = LinearEasing))
            }
            shakeOffset.animateTo(0f, tween(30))
        }
    }

    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier.offset(x = shakeOffset.value.dp)
    ) {
        if (isBoss) {
            Text(enemy.name, style = PixelHeading, color = EnemyRed)
            PixelHpBar(enemy.currentHp, enemy.maxHp, Modifier.width(100.dp))
        } else {
            PixelHpBar(enemy.currentHp, enemy.maxHp, Modifier.width(48.dp))
        }
        
        Box(Modifier.size(if (isBoss) 96.dp else 72.dp).graphicsLayer(scaleX = -1f)) {
            val flashAlpha = remember { Animatable(0f) }
            val scope = rememberCoroutineScope()

            LaunchedEffect(isHit) {
                if (isHit) {
                    scope.launch {
                        flashAlpha.snapTo(1f)
                        flashAlpha.animateTo(0f, tween(300))
                    }
                }
            }

            EnemySprite(
                enemy.name, 
                Modifier
                    .fillMaxSize()
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()
                        if (flashAlpha.value > 0f) {
                            drawRect(
                                color = (if (isCritical) GoldBright else EnemyRed).copy(alpha = flashAlpha.value),
                                blendMode = BlendMode.SrcIn
                            )
                        }
                    }
            )

            if (isHit) {
                Box(Modifier.align(Center)) {
                    UnitHitParticles(isCritical)
                }
            }
        }
        if (!isBoss) {
            Text(enemy.name, style = PixelSmall, color = EnemyRed)
        }
    }
}

@Composable
fun UnitHitParticles(isCritical: Boolean) {
    val particleColor = if (isCritical) GoldBright else EnemyRed
    val particleCount = if (isCritical) 20 else 12
    
    Box(Modifier.fillMaxSize()) {
        // Pixel Particles
        val particles = remember {
            List(particleCount) {
                val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
                val speed = Random.nextFloat() * 100f + 50f
                ParticleState(
                    x = 0f, // Center of parent
                    y = 0f,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    size = (Random.nextInt(2, 5)).dp
                )
            }
        }

        particles.forEach { p ->
            var px by remember { mutableStateOf(p.x) }
            var py by remember { mutableStateOf(p.y) }
            var alpha by remember { mutableStateOf(1f) }

            LaunchedEffect(Unit) {
                val startTime = System.currentTimeMillis()
                val duration = 400L
                while (System.currentTimeMillis() - startTime < duration) {
                    val elapsed = System.currentTimeMillis() - startTime
                    val progress = elapsed.toFloat() / duration
                    px = p.x + (p.vx * progress)
                    py = p.y + (p.vy * progress)
                    alpha = 1f - progress
                    delay(16)
                }
            }

            Box(
                Modifier
                    .size(p.size)
                    .offset(x = px.dp, y = py.dp)
                    .graphicsLayer(alpha = alpha)
                    .background(if(isCritical) GoldDark else particleColor)
            )
        }
    }
}

data class ParticleState(val x: Float, val y: Float, val vx: Float, val vy: Float, val size: androidx.compose.ui.unit.Dp)

@Composable
fun BattleLogPanel(battleLog: List<DungeonViewModel.FFLogEntry>) {
    GoldenBorderBox(Modifier.fillMaxWidth().height(100.dp).background(BgDarkest.copy(alpha = 0.9f))) {
        val listState = rememberLazyListState()
        LaunchedEffect(battleLog.size) { if (battleLog.isNotEmpty()) listState.animateScrollToItem(battleLog.size - 1) }
        
        val context = androidx.compose.ui.platform.LocalContext.current

        LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(8.dp)) {
            items(battleLog) { entry ->
                val color = when (entry.type) {
                    DungeonViewModel.LogType.HERO_ATTACK -> HeroBlue
                    DungeonViewModel.LogType.ENEMY_ATTACK -> EnemyRed
                    DungeonViewModel.LogType.ABILITY, DungeonViewModel.LogType.SUMMON -> GoldBright
                    DungeonViewModel.LogType.HEAL -> HpGreen
                    DungeonViewModel.LogType.BOSS -> Color.Magenta
                    DungeonViewModel.LogType.HERO_FELL -> StoneGray
                    else -> Color.White
                }
                
                val logText = remember(entry.messageRes, entry.args) {
                    formatSafeLogEntry(context, entry)
                }

                Text(logText, style = PixelSmall.copy(color = color))
            }
        }
    }
}

@Composable
fun RunCompleteOverlay(
    currentFloor: Int,
    gilEarnedThisRun: Long,
    magiciteEarnedThisRun: Int,
    gilLostToPenalty: Long,
    fallenHeroes: List<Hero>,
    itemsFoundThisRun: List<Item>,
    originalPartySize: Int,
    onReturn: () -> Unit
) {
    val scrollState = rememberScrollState()
    val allDead = fallenHeroes.size >= originalPartySize && originalPartySize > 0
    var selectedItemForDetail by remember { mutableStateOf<Item?>(null) }

    Box(
        Modifier
            .fillMaxSize()
            .background(BgDarkest.copy(alpha = 0.98f))
            .clickable(enabled = false) {}
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = CenterHorizontally
        ) {
            // Header
            Text(
                if (allDead) stringResource(R.string.party_wiped) else stringResource(R.string.run_complete),
                style = PixelTitle,
                color = if (allDead) EnemyRed else GoldBright
            )
            Text(safeStringResource(R.string.floor_reached_format, currentFloor), style = PixelHeading, color = GoldDark)

            Spacer(Modifier.height(16.dp))
            PixelDivider()
            Spacer(Modifier.height(16.dp))

            // Main Content Area (Two Columns)
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left Column: Rewards
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PixelPanel(Modifier.padding(8.dp), borderColor = GoldDark) {
                        Column(
                            Modifier.padding(16.dp),
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(stringResource(R.string.earned_rewards), style = PixelHeading, color = GoldBright)
                            Spacer(Modifier.height(8.dp))
                            
                            Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("🪙", fontSize = 24.sp)
                                Column {
                                    Text(formatGold(gilEarnedThisRun), style = PixelTitle, color = GoldBright)
                                    Text("GIL", style = PixelSmall, color = GoldDark)
                                }
                                if (gilLostToPenalty > 0) {
                                    Spacer(Modifier.width(8.dp))
                                    Column(horizontalAlignment = CenterHorizontally) {
                                        Text("-${formatGold(gilLostToPenalty)}", style = PixelHeading, color = EnemyRed)
                                        Text(stringResource(R.string.wipe_penalty), style = PixelSmall, color = EnemyRed)
                                    }
                                }
                            }
                            
                            Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("💎", fontSize = 24.sp)
                                Column {
                                    Text("$magiciteEarnedThisRun", style = PixelTitle, color = Color(0xFFAA44FF))
                                    Text("MAGICITE", style = PixelSmall, color = GoldDark)
                                }
                            }
                        }
                    }
                }

                // Right Column: Fallen & Loot (Scrollable)
                Column(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxHeight()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (fallenHeroes.isNotEmpty()) {
                        Text(stringResource(R.string.fallen_warriors), style = PixelHeading, color = EnemyRed)
                        val rows = fallenHeroes.chunked(4)
                        rows.forEach { rowHeroes ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                rowHeroes.forEach { hero ->
                                    Column(horizontalAlignment = CenterHorizontally, modifier = Modifier.width(60.dp)) {
                                        Box(Modifier.size(48.dp)) {
                                            HeroSprite(hero.heroClass, Modifier.fillMaxSize())
                                            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)))
                                            Text("💀", fontSize = 20.sp, modifier = Modifier.align(Center))
                                        }
                                        Text(hero.name, style = PixelSmall, color = StoneGray, maxLines = 1)
                                        Text(safeStringResource(R.string.relic_level_format, hero.level), style = PixelSmall, color = StoneGray, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }

                    if (itemsFoundThisRun.isNotEmpty()) {
                        Text(stringResource(R.string.loot_collected), style = PixelHeading, color = GoldBright)
                        val itemRows = itemsFoundThisRun.chunked(6)
                        itemRows.forEach { rowItems ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowItems.forEach { item ->
                                    Box(
                                        Modifier
                                            .size(44.dp)
                                            .background(BgPanel)
                                            .border(1.dp, Color(item.rarity.color))
                                            .clickable { selectedItemForDetail = item }
                                    ) {
                                        Text(item.emoji, fontSize = 24.sp, modifier = Modifier.align(Center))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            PixelButton(
                "RETURN TO INN",
                onClick = onReturn,
                modifier = Modifier.fillMaxWidth().height(44.dp)
            )
        }


        // Item Detail Overlay (using the one from EquipmentScreen or similar)
        selectedItemForDetail?.let { item ->
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable { selectedItemForDetail = null },
                contentAlignment = Center
            ) {
                PixelPanel(
                    Modifier.width(280.dp).clickable(enabled = false) {},
                    borderColor = Color(item.rarity.color)
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(item.emoji, fontSize = 48.sp)
                        Text(item.name, style = PixelHeading, color = Color(item.rarity.color))
                        
                        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (item.attackBonus > 0) Text("ATK: +${item.attackBonus}", style = PixelBody, color = GoldBright)
                            if (item.defenseBonus > 0) Text("DEF: +${item.defenseBonus}", style = PixelBody, color = GoldBright)
                            if (item.magicBonus > 0) Text("MAG: +${item.magicBonus}", style = PixelBody, color = GoldBright)
                            if (item.hpBonus > 0) Text("HP: +${item.hpBonus}", style = PixelBody, color = GoldBright)
                            if (item.critChanceBonus > 0) Text("CRIT %: +${item.critChanceBonus}%", style = PixelBody, color = HpGreen)
                            if (item.critDamageBonus > 0) Text("CRIT DMG: +${item.critDamageBonus}%", style = PixelBody, color = HpGreen)
                        }

                        Spacer(Modifier.height(8.dp))
                        
                        Text(safeStringResource(R.string.item_lvl_rarity_format, item.floorFound, item.rarity.name), style = PixelSmall, color = StoneGray)

                        PixelButton(stringResource(R.string.close_button), onClick = { selectedItemForDetail = null }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

