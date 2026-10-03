package com.game.dungeon.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import com.game.dungeon.data.models.BattleSpeed
import com.game.dungeon.data.models.Enemy
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.models.MonsterType
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.DungeonViewModel.AbilityAnimationInfo
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun AbilityEffectsOverlay(
    animInfo: AbilityAnimationInfo,
    speed: BattleSpeed = BattleSpeed.NORMAL,
    heroes: List<Hero> = emptyList(),
    enemies: List<Enemy> = emptyList(),
    modifier: Modifier = Modifier
) {
    val duration = (animInfo.durationMs / speed.speedFactor).coerceAtLeast(100f).toLong()
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(animInfo.id) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, tween(duration.toInt(), easing = LinearEasing))
    }

    val progress = animProgress.value

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val heroIndex = heroes.indexOfFirst { it.id == animInfo.attackerId || it.id == animInfo.targetId }
            val enemyIndex = enemies.indexOfFirst { it.id == animInfo.attackerId || it.id == animInfo.targetId }

            val heroX = if (heroIndex >= 0 && heroes.isNotEmpty()) {
                w * (0.08f + 0.34f * ((heroIndex + 0.5f) / heroes.size))
            } else {
                w * 0.22f
            }

            val enemyX = if (enemyIndex >= 0 && enemies.isNotEmpty()) {
                w * (0.58f + 0.34f * ((enemyIndex + 0.5f) / enemies.size))
            } else {
                w * 0.78f
            }

            val centerY = h * 0.5f

            if (animInfo.isSummon) {
                drawSummonEffect(animInfo.summonName ?: "Ifrit", progress, w, h)
            } else if (animInfo.isBossAttack) {
                drawBossCinematicAttack(progress, w, h)
            } else if (animInfo.isEnemyAttack) {
                drawMonsterAttack(animInfo.monsterType, animInfo.isBossAttack, progress, heroX, centerY)
            } else {
                when (animInfo.heroClass) {
                    HeroClass.WARRIOR, HeroClass.KNIGHT -> drawWarriorSlash(progress, enemyX, centerY)
                    HeroClass.WHITE_MAGE -> drawWhiteMageHeal(progress, heroX, centerY)
                    HeroClass.BLACK_MAGE -> drawBlackMageElemental(progress, enemyX, centerY)
                    HeroClass.THIEF -> drawThiefRansack(progress, heroX, enemyX, centerY)
                    HeroClass.MONK -> drawMonkShockwave(progress, enemyX, centerY)
                    HeroClass.PALADIN -> drawPaladinHoly(progress, heroX, enemyX, centerY, h)
                    HeroClass.RED_MAGE -> drawRedMageDualcast(progress, enemyX, centerY)
                    HeroClass.SUMMONER -> drawSummonEffect(animInfo.summonName ?: "Ifrit", progress, w, h)
                    HeroClass.NINJA -> drawNinjaShuriken(progress, heroX, enemyX, centerY)
                    HeroClass.DRAGOON -> drawDragoonJump(progress, heroX, enemyX, h)
                    HeroClass.BARD -> drawBardSong(progress, heroX, h)
                    HeroClass.SAMURAI -> drawSamuraiZeninage(progress, enemyX, centerY)
                    HeroClass.NECROMANCER -> drawNecromancerSoulDrain(progress, heroX, enemyX, centerY)
                    HeroClass.MIME -> drawMimeMirror(progress, heroX, centerY)
                    HeroClass.ONION_KNIGHT -> drawOnionKnightSpin(progress, enemyX, centerY)
                    HeroClass.BLUE_MAGE -> drawBlueMageAura(progress, heroX, enemyX, centerY)
                    HeroClass.FREELANCER, null -> drawFreelancerRush(progress, heroX, enemyX, centerY)
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWarriorSlash(progress: Float, targetX: Float, targetY: Float) {
    val rng = Random(42)
    val alpha = (1f - progress).coerceIn(0f, 1f)
    
    // Dual Slash Arcs
    val slashPath1 = Path().apply {
        moveTo(targetX - 50f + progress * 20f, targetY - 60f)
        lineTo(targetX + 50f + progress * 20f, targetY + 60f)
    }
    val slashPath2 = Path().apply {
        moveTo(targetX + 60f - progress * 20f, targetY - 50f)
        lineTo(targetX - 60f - progress * 20f, targetY + 50f)
    }

    drawPath(slashPath1, Color.White.copy(alpha = alpha), style = Stroke(width = 8f))
    drawPath(slashPath1, GoldBright.copy(alpha = alpha), style = Stroke(width = 4f))
    
    if (progress > 0.3f) {
        drawPath(slashPath2, Color.White.copy(alpha = alpha), style = Stroke(width = 8f))
        drawPath(slashPath2, GoldDark.copy(alpha = alpha), style = Stroke(width = 4f))
    }

    // Sparkles
    repeat(16) { i ->
        val angle = rng.nextFloat() * 2f * Math.PI.toFloat()
        val dist = progress * 90f * (0.5f + rng.nextFloat() * 0.5f)
        val px = targetX + cos(angle) * dist
        val py = targetY + sin(angle) * dist
        val pColor = if (i % 2 == 0) GoldBright else Color.White
        drawRect(pColor.copy(alpha = alpha), topLeft = Offset(px, py), size = Size(6f, 6f))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWhiteMageHeal(progress: Float, heroX: Float, heroY: Float) {
    val alpha = sin(progress * Math.PI.toFloat()).coerceIn(0f, 1f)
    
    // Holy Light Pillar
    drawRect(
        brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = alpha * 0.8f), HpGreen.copy(alpha = alpha * 0.4f), Color.Transparent)),
        topLeft = Offset(heroX - 80f, 0f),
        size = Size(160f, size.height)
    )

    // Rising Green/Gold Crosses
    val rng = Random(101)
    repeat(12) { i ->
        val x = heroX - 70f + rng.nextFloat() * 140f
        val startY = heroY + 60f
        val y = startY - (progress * 160f) - (i * 10f)
        val cAlpha = ((1f - (progress * 1.2f)) * alpha).coerceIn(0f, 1f)
        val size = 12f + (i % 3) * 4f
        val color = if (i % 2 == 0) HpGreen else GoldBright

        // Draw pixel cross
        drawRect(color.copy(alpha = cAlpha), topLeft = Offset(x - size / 6f, y - size / 2f), size = Size(size / 3f, size))
        drawRect(color.copy(alpha = cAlpha), topLeft = Offset(x - size / 2f, y - size / 6f), size = Size(size, size / 3f))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBlackMageElemental(progress: Float, enemyX: Float, enemyY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    // Fire Explosion Center
    val radius = progress * 130f
    drawCircle(
        brush = Brush.radialGradient(listOf(Color.Yellow, EnemyRed, Color.Transparent), center = Offset(enemyX, enemyY), radius = radius.coerceAtLeast(10f)),
        radius = radius,
        center = Offset(enemyX, enemyY),
        alpha = alpha
    )

    // Ice Shards & Lightning Plasma Bolts
    repeat(18) { i ->
        val angle = (i / 18f) * 2f * Math.PI.toFloat()
        val dist = progress * 140f
        val px = enemyX + cos(angle) * dist
        val py = enemyY + sin(angle) * dist
        val pColor = when (i % 3) {
            0 -> EnemyRed
            1 -> Color(0xFF00E5FF) // Ice Cyan
            else -> GoldBright     // Lightning Yellow
        }
        drawRect(pColor.copy(alpha = alpha), topLeft = Offset(px, py), size = Size(8f, 8f))
        
        // Ice Crystal line
        if (i % 3 == 1) {
            drawLine(
                color = Color.Cyan.copy(alpha = alpha),
                start = Offset(enemyX, enemyY),
                end = Offset(px, py),
                strokeWidth = 3f
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawThiefRansack(progress: Float, heroX: Float, enemyX: Float, centerY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)
    val currentX = heroX + (enemyX - heroX) * progress

    // Shadow Clone Trail
    repeat(4) { i ->
        val trailX = currentX - (i * 25f)
        drawRect(
            color = Color.Black.copy(alpha = (0.5f - i * 0.1f) * alpha),
            topLeft = Offset(trailX - 20f, centerY - 30f),
            size = Size(40f, 60f)
        )
    }

    // Glittering Coin Drop Shower at target
    if (progress > 0.4f) {
        val coinRng = Random(888)
        repeat(12) {
            val cx = enemyX - 30f + coinRng.nextFloat() * 60f
            val cy = centerY - 40f + (progress * 80f) + (coinRng.nextFloat() * 20f)
            drawCircle(GoldBright.copy(alpha = alpha), radius = 5f, center = Offset(cx, cy))
            drawCircle(GoldDark.copy(alpha = alpha), radius = 2.5f, center = Offset(cx, cy))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMonkShockwave(progress: Float, enemyX: Float, enemyY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    // Expanding Shockwave Rings
    repeat(3) { i ->
        val r = (progress * 120f) - (i * 20f)
        if (r > 0f) {
            drawCircle(
                color = GoldBright.copy(alpha = alpha * (1f - i * 0.25f)),
                radius = r,
                center = Offset(enemyX, enemyY),
                style = Stroke(width = 6f - i * 1.5f)
            )
        }
    }

    // Impact Energy Burst Points
    val rng = Random(333)
    repeat(14) {
        val a = rng.nextFloat() * 2f * Math.PI.toFloat()
        val d = progress * 110f
        drawRect(
            color = Color.White.copy(alpha = alpha),
            topLeft = Offset(enemyX + cos(a) * d, enemyY + sin(a) * d),
            size = Size(6f, 6f)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPaladinHoly(progress: Float, heroX: Float, enemyX: Float, centerY: Float, h: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    // Sweeping Holy Sword Beam across enemies
    val beamX = enemyX - 100f + (progress * 200f)
    drawLine(
        color = Color.White.copy(alpha = alpha),
        start = Offset(beamX, 0f),
        end = Offset(beamX, h),
        strokeWidth = 12f
    )
    drawLine(
        color = GoldBright.copy(alpha = alpha),
        start = Offset(beamX - 10f, 0f),
        end = Offset(beamX - 10f, h),
        strokeWidth = 6f
    )

    // Party Light Shimmer over allies
    val rng = Random(999)
    repeat(10) {
        val px = heroX - 60f + rng.nextFloat() * 120f
        val py = centerY - 60f + rng.nextFloat() * 120f
        drawCircle(GoldBright.copy(alpha = alpha * 0.8f), radius = 6f, center = Offset(px, py))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRedMageDualcast(progress: Float, enemyX: Float, enemyY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    // First Cast: Fire Explosion
    if (progress < 0.6f) {
        val p1 = progress / 0.6f
        drawCircle(
            EnemyRed.copy(alpha = alpha),
            radius = p1 * 70f,
            center = Offset(enemyX - 20f, enemyY - 20f)
        )
    }

    // Second Cast: Ice Crystal Burst
    if (progress > 0.3f) {
        val p2 = (progress - 0.3f) / 0.7f
        drawCircle(
            Color.Cyan.copy(alpha = alpha),
            radius = p2 * 75f,
            center = Offset(enemyX + 20f, enemyY + 20f),
            style = Stroke(width = 8f)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawNinjaShuriken(progress: Float, heroX: Float, enemyX: Float, centerY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    // 3 Spinning Shuriken Projectiles
    repeat(3) { i ->
        val startY = centerY - 40f + (i * 40f)
        val projX = heroX + (enemyX - heroX) * progress

        drawRect(
            color = Color.LightGray.copy(alpha = alpha),
            topLeft = Offset(projX - 10f, startY - 2f),
            size = Size(20f, 4f)
        )
        drawRect(
            color = Color.LightGray.copy(alpha = alpha),
            topLeft = Offset(projX - 2f, startY - 10f),
            size = Size(4f, 20f)
        )
    }

    // Shadow Slash Cuts at impact
    if (progress > 0.6f) {
        drawLine(
            color = Color.White.copy(alpha = alpha),
            start = Offset(enemyX - 40f, centerY - 40f),
            end = Offset(enemyX + 40f, centerY + 40f),
            strokeWidth = 6f
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDragoonJump(progress: Float, heroX: Float, enemyX: Float, h: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    if (progress < 0.4f) {
        // Leaping Up Shadow
        val leapY = (h * 0.5f) - (progress * h * 1.2f)
        drawRect(Color.Cyan.copy(alpha = alpha), topLeft = Offset(heroX - 10f, leapY), size = Size(20f, 40f))
    } else {
        // Vertical Spear Plunge Crash onto Enemy
        val plungeProgress = (progress - 0.4f) / 0.6f
        val spearY = -100f + (plungeProgress * (h * 0.5f + 100f))

        drawLine(
            color = Color.White.copy(alpha = alpha),
            start = Offset(enemyX, spearY - 80f),
            end = Offset(enemyX, spearY),
            strokeWidth = 8f
        )
        drawLine(
            color = Color.Cyan.copy(alpha = alpha),
            start = Offset(enemyX - 6f, spearY - 80f),
            end = Offset(enemyX - 6f, spearY),
            strokeWidth = 4f
        )

        // Impact Dust Cloud
        if (plungeProgress > 0.8f) {
            drawCircle(Color.LightGray.copy(alpha = alpha * 0.7f), radius = (plungeProgress - 0.8f) * 200f, center = Offset(enemyX, h * 0.5f))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBardSong(progress: Float, heroX: Float, h: Float) {
    val alpha = sin(progress * Math.PI.toFloat()).coerceIn(0f, 1f)
    val rng = Random(555)

    // Rhythm Wave Rings over party
    drawCircle(
        color = Color(0xFFFF88CC).copy(alpha = alpha * 0.5f),
        radius = progress * 160f,
        center = Offset(heroX, h * 0.5f),
        style = Stroke(width = 6f)
    )

    // Floating Pixel Musical Notes
    repeat(8) { i ->
        val nx = heroX - 60f + (rng.nextFloat() * 120f)
        val ny = (h * 0.6f) - (progress * 120f) - (i * 15f)
        drawRect(Color(0xFFFF88CC).copy(alpha = alpha), topLeft = Offset(nx, ny), size = Size(10f, 10f))
        drawRect(Color.White.copy(alpha = alpha), topLeft = Offset(nx + 8f, ny - 6f), size = Size(4f, 12f))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSamuraiZeninage(progress: Float, enemyX: Float, centerY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    // Sakura Petal & Coin Scatter across enemy field
    val rng = Random(444)
    repeat(24) { i ->
        val px = (enemyX - 120f) + (rng.nextFloat() * 240f)
        val py = (centerY - 80f) + (rng.nextFloat() * 160f)
        val pColor = if (i % 2 == 0) Color(0xFFFFB7C5) else GoldBright
        drawRect(pColor.copy(alpha = alpha), topLeft = Offset(px, py), size = Size(8f, 8f))
    }

    // Fast Blade Arc
    val arcPath = Path().apply {
        moveTo(enemyX - 100f, centerY - 60f)
        quadraticTo(enemyX, centerY + 80f, enemyX + 100f, centerY - 60f)
    }
    drawPath(arcPath, Color.White.copy(alpha = alpha), style = Stroke(width = 6f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawNecromancerSoulDrain(progress: Float, heroX: Float, enemyX: Float, centerY: Float) {
    val alpha = sin(progress * Math.PI.toFloat()).coerceIn(0f, 1f)

    // Siphon Beam Target to Hero
    drawLine(
        brush = Brush.horizontalGradient(listOf(Color(0xFF330033), Color(0xFFAA44FF), Color.White)),
        start = Offset(enemyX, centerY),
        end = Offset(heroX, centerY),
        strokeWidth = 10f
    )

    // Traveling Soul Particles
    repeat(10) { i ->
        val t = ((progress + i * 0.1f) % 1.0f)
        val px = enemyX - (enemyX - heroX) * t
        val py = centerY + sin(t * Math.PI.toFloat() * 4f) * 15f
        drawCircle(Color(0xFFAA44FF).copy(alpha = alpha), radius = 6f, center = Offset(px, py))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMimeMirror(progress: Float, heroX: Float, centerY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)
    val r = progress * 100f

    drawCircle(
        color = Color(0xFFFFFF99).copy(alpha = alpha),
        radius = r,
        center = Offset(heroX, centerY),
        style = Stroke(width = 8f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawOnionKnightSpin(progress: Float, enemyX: Float, enemyY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)
    val rot = progress * 720f

    drawCircle(
        color = Color(0xFF88FF88).copy(alpha = alpha),
        radius = 20f + progress * 30f,
        center = Offset(enemyX, enemyY)
    )

    val rng = Random(909)
    repeat(12) { i ->
        val angle = (i / 12f) * 2f * Math.PI.toFloat() + Math.toRadians(rot.toDouble()).toFloat()
        val dist = progress * 100f
        val px = enemyX + cos(angle) * dist
        val py = enemyY + sin(angle) * dist
        
        drawRect(GoldBright.copy(alpha = alpha), topLeft = Offset(px - 4f, py - 1f), size = Size(8f, 2f))
        drawRect(GoldBright.copy(alpha = alpha), topLeft = Offset(px - 1f, py - 4f), size = Size(2f, 8f))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBlueMageAura(progress: Float, heroX: Float, enemyX: Float, centerY: Float) {
    val alpha = sin(progress * Math.PI.toFloat()).coerceIn(0f, 1f)

    drawCircle(
        color = Color(0xFF3366FF).copy(alpha = alpha * 0.8f),
        radius = 45f,
        center = Offset(heroX, centerY + 30f),
        style = Stroke(width = 4f)
    )

    val waveX = heroX + (enemyX - heroX) * progress
    drawCircle(
        brush = Brush.radialGradient(listOf(Color.White, Color(0xFF00E5FF), Color(0xFF0033FF), Color.Transparent), center = Offset(waveX, centerY), radius = 60f),
        radius = 60f,
        center = Offset(waveX, centerY),
        alpha = alpha
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFreelancerRush(progress: Float, heroX: Float, enemyX: Float, centerY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)
    val rushX = heroX + (enemyX - heroX) * progress

    repeat(5) { i ->
        val offsetY = (i - 2) * 15f
        drawLine(
            color = Color.White.copy(alpha = alpha),
            start = Offset(rushX - 40f, centerY + offsetY),
            end = Offset(rushX + 20f, centerY + offsetY),
            strokeWidth = 4f
        )
    }

    if (progress > 0.6f) {
        val rng = Random(123)
        repeat(8) {
            val dx = (rng.nextFloat() - 0.5f) * 60f
            val dy = (rng.nextFloat() - 0.5f) * 60f
            drawRect(GoldBright.copy(alpha = alpha), topLeft = Offset(enemyX + dx, centerY + dy), size = Size(6f, 6f))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSummonEffect(summonName: String, progress: Float, w: Float, h: Float) {
    val alpha = sin(progress * Math.PI.toFloat()).coerceIn(0f, 1f)
    val enemyX = w * 0.78f
    val centerY = h * 0.5f

    when (summonName) {
        "Shiva" -> {
            drawRect(Color(0xFF00E5FF).copy(alpha = alpha * 0.35f), topLeft = Offset.Zero, size = Size(w, h))

            val rng = Random(111)
            repeat(30) {
                val px = rng.nextFloat() * w
                val py = rng.nextFloat() * h
                drawRect(Color.White.copy(alpha = alpha), topLeft = Offset(px, py), size = Size(6f, 6f))
            }
            drawLine(
                color = Color.Cyan.copy(alpha = alpha),
                start = Offset(enemyX + 60f, centerY - 150f),
                end = Offset(enemyX - 60f, centerY + 150f),
                strokeWidth = 16f
            )
        }
        "Ramuh" -> {
            drawRect(Color(0xFF1A1A33).copy(alpha = alpha * 0.5f), topLeft = Offset.Zero, size = Size(w, h))

            repeat(4) { i ->
                val x = (w * 0.6f) + (i * 60f)
                drawLine(
                    color = GoldBright.copy(alpha = alpha),
                    start = Offset(x, 0f),
                    end = Offset(x - 20f, h),
                    strokeWidth = 10f
                )
            }
        }
        "Bahamut" -> {
            drawRect(Color.Black.copy(alpha = alpha * 0.7f), topLeft = Offset.Zero, size = Size(w, h))

            val radius = progress * 300f
            drawCircle(
                brush = Brush.radialGradient(listOf(Color.White, GoldBright, Color(0xFFAA44FF), Color.Transparent), center = Offset(enemyX, centerY), radius = radius.coerceAtLeast(10f)),
                radius = radius,
                center = Offset(enemyX, centerY),
                alpha = alpha
            )
        }
        else -> { // Ifrit
            drawRect(EnemyRed.copy(alpha = alpha * 0.4f), topLeft = Offset.Zero, size = Size(w, h))

            val rng = Random(222)
            repeat(35) {
                val px = enemyX - 100f + (rng.nextFloat() * 200f)
                val py = h - (progress * h * 0.9f)
                drawRect(GoldBright.copy(alpha = alpha), topLeft = Offset(px, py), size = Size(12f, 12f))
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMonsterAttack(monsterType: MonsterType?, isBossAttack: Boolean, progress: Float, heroX: Float, centerY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    if (isBossAttack || monsterType?.name?.contains("MAGE") == true) {
        drawCircle(
            color = Color(0xFFAA44FF).copy(alpha = alpha),
            radius = progress * 70f,
            center = Offset(heroX, centerY)
        )
    } else {
        repeat(3) { i ->
            val offsetY = (i - 1) * 20f
            drawLine(
                color = EnemyRed.copy(alpha = alpha),
                start = Offset(heroX - 30f, centerY + offsetY - 20f),
                end = Offset(heroX + 30f, centerY + offsetY + 20f),
                strokeWidth = 6f
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBossCinematicAttack(progress: Float, w: Float, h: Float) {
    val alpha = sin(progress * Math.PI.toFloat()).coerceIn(0f, 1f)
    val heroX = w * 0.22f
    val centerY = h * 0.5f

    drawRect(EnemyRed.copy(alpha = alpha * 0.35f), topLeft = Offset.Zero, size = Size(w, h))

    val beamY = centerY - 120f + (progress * 240f)
    drawLine(
        brush = Brush.horizontalGradient(listOf(EnemyRed, Color.Magenta, Color.White)),
        start = Offset(w, beamY),
        end = Offset(0f, beamY),
        strokeWidth = 14f
    )

    val rng = Random(5050)
    repeat(12) {
        val px = heroX - 80f + (rng.nextFloat() * 160f)
        val py = centerY - 80f + (rng.nextFloat() * 160f)
        drawCircle(EnemyRed.copy(alpha = alpha), radius = 8f, center = Offset(px, py))
    }
}
