package com.game.dungeon.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
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
    val isPreview = LocalInspectionMode.current

    LaunchedEffect(animInfo.id) {
        if (!isPreview) {
            animProgress.snapTo(0f)
            animProgress.animateTo(1f, tween(duration.toInt(), easing = LinearEasing))
        }
    }

    val progress = if (isPreview) 0.5f else animProgress.value

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val attackerHeroIndex = heroes.indexOfFirst { it.id == animInfo.attackerId }
            val targetEnemyIndex = enemies.indexOfFirst { it.id == animInfo.targetId }
            val targetHeroIndex = heroes.indexOfFirst { it.id == animInfo.targetId }

            val heroX = if (attackerHeroIndex >= 0 && heroes.isNotEmpty()) {
                w * (0.08f + 0.34f * ((attackerHeroIndex + 0.5f) / heroes.size))
            } else {
                w * 0.22f
            }

            val enemyX = if (targetEnemyIndex >= 0 && enemies.isNotEmpty()) {
                w * (0.58f + 0.34f * ((targetEnemyIndex + 0.5f) / enemies.size))
            } else {
                w * 0.78f
            }

            val targetHeroX = if (targetHeroIndex >= 0 && heroes.isNotEmpty()) {
                w * (0.08f + 0.34f * ((targetHeroIndex + 0.5f) / heroes.size))
            } else {
                heroX
            }

            val centerY = h * 0.5f
            val lvl = animInfo.abilityLevel.coerceAtLeast(1)

            if (animInfo.isSummon) {
                drawSummonEffect(animInfo.summonName ?: "Ifrit", lvl, progress, enemyX, centerY, w, h)
            } else if (animInfo.isBossAttack) {
                drawBossCinematicAttack(progress, w, h)
            } else if (animInfo.isEnemyAttack) {
                drawMonsterAttack(animInfo.monsterType, animInfo.isBossAttack, lvl, progress, heroX, centerY)
            } else {
                when (animInfo.heroClass) {
                    HeroClass.WARRIOR -> drawWarriorSlash(progress, lvl, enemyX, centerY)
                    HeroClass.KNIGHT -> drawKnightGuardSlash(progress, lvl, heroX, enemyX, centerY)
                    HeroClass.WHITE_MAGE -> drawWhiteMageHeal(progress, lvl, targetHeroX, centerY)
                    HeroClass.BLACK_MAGE -> drawBlackMageElemental(progress, lvl, heroX, enemyX, centerY)
                    HeroClass.THIEF -> drawThiefRansack(progress, lvl, heroX, enemyX, centerY)
                    HeroClass.MONK -> drawMonkShockwave(progress, lvl, enemyX, centerY)
                    HeroClass.PALADIN -> drawPaladinHoly(progress, lvl, heroX, enemyX, centerY, h)
                    HeroClass.RED_MAGE -> drawRedMageDualcast(progress, lvl, enemyX, centerY)
                    HeroClass.SUMMONER -> drawSummonEffect(animInfo.summonName ?: "Ifrit", lvl, progress, enemyX, centerY, w, h)
                    HeroClass.NINJA -> drawNinjaShuriken(progress, lvl, heroX, enemyX, centerY)
                    HeroClass.DRAGOON -> drawDragoonJump(progress, lvl, heroX, enemyX, h)
                    HeroClass.BARD -> drawBardSong(progress, lvl, heroX, h)
                    HeroClass.SAMURAI -> drawSamuraiZeninage(progress, lvl, enemyX, centerY)
                    HeroClass.NECROMANCER -> drawNecromancerSoulDrain(progress, lvl, heroX, enemyX, centerY)
                    HeroClass.MIME -> drawMimeMirror(progress, lvl, heroX, centerY)
                    HeroClass.ONION_KNIGHT -> drawOnionKnightSpin(progress, lvl, enemyX, centerY)
                    HeroClass.BLUE_MAGE -> drawBlueMageAura(progress, lvl, heroX, enemyX, centerY)
                    HeroClass.FREELANCER, null -> drawFreelancerRush(progress, lvl, heroX, enemyX, centerY)
                }
            }
        }
    }
}

private fun DrawScope.drawKnightGuardSlash(progress: Float, lvl: Int, heroX: Float, enemyX: Float, centerY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    // Golden Aegis Shield Aura Rings
    repeat(lvl) { i ->
        val radius = 40f + progress * (30f + i * 20f)
        drawCircle(
            color = GoldBright.copy(alpha = alpha * (0.7f - i * 0.15f)),
            radius = radius,
            center = Offset(heroX, centerY),
            style = Stroke(width = (8f - i * 2f).coerceAtLeast(3f))
        )
    }

    // Heavy Shield Slash Beams
    val slashPath1 = Path().apply {
        moveTo(enemyX - 40f, centerY - 60f)
        lineTo(enemyX + 40f, centerY + 60f)
    }
    drawPath(slashPath1, Color.White.copy(alpha = alpha), style = Stroke(width = 8f))
    drawPath(slashPath1, Color(0xFF4488FF).copy(alpha = alpha), style = Stroke(width = 4f))

    if (lvl >= 2) {
        val slashPath2 = Path().apply {
            moveTo(enemyX + 40f, centerY - 60f)
            lineTo(enemyX - 40f, centerY + 60f)
        }
        drawPath(slashPath2, GoldBright.copy(alpha = alpha), style = Stroke(width = 6f))
    }

    if (lvl >= 3) {
        val rng = Random(101)
        repeat(24) {
            val a = rng.nextFloat() * 2f * Math.PI.toFloat()
            val d = progress * 90f
            drawCircle(GoldBright.copy(alpha = alpha), radius = 4f, center = Offset(heroX + cos(a) * d, centerY + sin(a) * d))
        }
    }
}

private fun DrawScope.drawWarriorSlash(progress: Float, lvl: Int, targetX: Float, targetY: Float) {
    val rng = Random(42)
    val alpha = (1f - progress).coerceIn(0f, 1f)
    val numSlashes = when { lvl >= 3 -> 6; lvl >= 2 -> 4; else -> 2 }

    repeat(numSlashes) { i ->
        val angle = (i * Math.PI / numSlashes).toFloat()
        val cosA = cos(angle)
        val sinA = sin(angle)
        val len = 60f + lvl * 15f
        val path = Path().apply {
            moveTo(targetX - cosA * len, targetY - sinA * len)
            lineTo(targetX + cosA * len, targetY + sinA * len)
        }
        val strokeW = if (i % 2 == 0) 8f else 5f
        val col = if (i % 2 == 0) GoldBright else Color.White
        if (progress > i * 0.08f) {
            drawPath(path, col.copy(alpha = alpha), style = Stroke(width = strokeW))
        }
    }

    val particleCount = 12 * lvl
    repeat(particleCount) { i ->
        val angle = rng.nextFloat() * 2f * Math.PI.toFloat()
        val dist = progress * (80f + lvl * 30f) * (0.4f + rng.nextFloat() * 0.6f)
        val px = targetX + cos(angle) * dist
        val py = targetY + sin(angle) * dist
        val pColor = if (i % 2 == 0) GoldBright else EnemyRed
        drawRect(pColor.copy(alpha = alpha), topLeft = Offset(px, py), size = Size(6f, 6f))
    }
}

private fun DrawScope.drawWhiteMageHeal(progress: Float, lvl: Int, heroX: Float, heroY: Float) {
    val alpha = sin(progress * Math.PI.toFloat()).coerceIn(0f, 1f)

    // Holy Light Column
    val pillarWidth = 120f + lvl * 40f
    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = alpha * 0.85f),
                GoldBright.copy(alpha = alpha * 0.5f),
                HpGreen.copy(alpha = alpha * 0.3f),
                Color.Transparent
            )
        ),
        topLeft = Offset(heroX - pillarWidth / 2f, 0f),
        size = Size(pillarWidth, size.height)
    )

    // Concentric sacred ground rune rings
    if (lvl >= 2) {
        repeat(lvl) { i ->
            val ringR = 40f + i * 25f + progress * 20f
            drawCircle(
                color = GoldBright.copy(alpha = alpha * (0.8f - i * 0.2f)),
                radius = ringR,
                center = Offset(heroX, heroY + 40f),
                style = Stroke(width = 4f)
            )
        }
    }

    // Rising Green/Gold Crosses & Star shower
    val rng = Random(101)
    val crossCount = 10 + lvl * 10
    repeat(crossCount) { i ->
        val x = heroX - (pillarWidth / 2f - 10f) + rng.nextFloat() * (pillarWidth - 20f)
        val startY = heroY + 80f
        val y = startY - (progress * 220f) - (i * 8f)
        val cAlpha = ((1f - (progress * 1.1f)) * alpha).coerceIn(0f, 1f)
        val size = 10f + (i % 3) * 4f
        val color = when (i % 3) {
            0 -> HpGreen
            1 -> GoldBright
            else -> Color.White
        }

        drawRect(color.copy(alpha = cAlpha), topLeft = Offset(x - size / 6f, y - size / 2f), size = Size(size / 3f, size))
        drawRect(color.copy(alpha = cAlpha), topLeft = Offset(x - size / 2f, y - size / 6f), size = Size(size, size / 3f))
    }
}

private fun DrawScope.drawBlackMageElemental(progress: Float, lvl: Int, heroX: Float, enemyX: Float, enemyY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    if (lvl == 1) {
        // Level 1: Fireball projectile flying from hero to target
        val projX = heroX + (enemyX - heroX) * progress
        val radius = 25f
        drawCircle(
            brush = Brush.radialGradient(listOf(Color.Yellow, EnemyRed, Color.Transparent), center = Offset(projX, enemyY), radius = radius),
            radius = radius,
            center = Offset(projX, enemyY),
            alpha = alpha
        )
        val rng = Random(77)
        repeat(12) {
            val px = projX - progress * 40f + (rng.nextFloat() - 0.5f) * 20f
            val py = enemyY + (rng.nextFloat() - 0.5f) * 20f
            drawRect(EnemyRed.copy(alpha = alpha), topLeft = Offset(px, py), size = Size(6f, 6f))
        }
    } else if (lvl == 2) {
        // Level 2 (Fira / Blizzara / Thundara): Triple orbiting elemental spheres
        val centerRadius = progress * 100f
        repeat(3) { i ->
            val angle = (i * 2f * Math.PI / 3f).toFloat() + progress * 6f
            val px = enemyX + cos(angle) * centerRadius
            val py = enemyY + sin(angle) * centerRadius
            val col = when (i) {
                0 -> EnemyRed
                1 -> Color(0xFF00E5FF)
                else -> GoldBright
            }
            drawCircle(col.copy(alpha = alpha), radius = 20f, center = Offset(px, py))
            drawLine(
                color = col.copy(alpha = alpha * 0.7f),
                start = Offset(enemyX, enemyY),
                end = Offset(px, py),
                strokeWidth = 4f
            )
        }
    } else {
        // Level 3 (Firaga / Meteor): Plunging Meteor Fireball & Tri-Elemental Explosion
        val meteorY = -100f + progress * (enemyY + 100f)
        val coreRadius = 45f + progress * 20f
        drawCircle(
            brush = Brush.radialGradient(listOf(Color.White, Color.Yellow, EnemyRed, Color(0xFFAA44FF), Color.Transparent), center = Offset(enemyX, meteorY), radius = coreRadius),
            radius = coreRadius,
            center = Offset(enemyX, meteorY),
            alpha = alpha
        )

        // Orbital Rune Rings
        repeat(2) { i ->
            drawCircle(
                color = if (i == 0) GoldBright else Color.Cyan,
                radius = coreRadius + 20f + i * 15f,
                center = Offset(enemyX, meteorY),
                style = Stroke(width = 4f)
            )
        }

        // Particle Burst
        val rng = Random(321)
        repeat(45) {
            val a = rng.nextFloat() * 2f * Math.PI.toFloat()
            val d = progress * 160f * (0.3f + rng.nextFloat() * 0.7f)
            val px = enemyX + cos(a) * d
            val py = enemyY + sin(a) * d
            val col = when (rng.nextInt(3)) {
                0 -> EnemyRed
                1 -> Color.Cyan
                else -> GoldBright
            }
            drawRect(col.copy(alpha = alpha), topLeft = Offset(px, py), size = Size(8f, 8f))
        }
    }
}

private fun DrawScope.drawThiefRansack(progress: Float, lvl: Int, heroX: Float, enemyX: Float, centerY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)
    val cloneCount = 3 + lvl * 2
    val currentX = heroX + (enemyX - heroX) * progress

    repeat(cloneCount) { i ->
        val trailX = currentX - (i * (30f - lvl * 5f))
        drawRect(
            color = Color.Black.copy(alpha = (0.6f - i * 0.08f) * alpha),
            topLeft = Offset(trailX - 20f, centerY - 30f),
            size = Size(40f, 60f)
        )
    }

    if (progress > 0.3f) {
        val coinRng = Random(888)
        val coinCount = 10 + lvl * 10
        repeat(coinCount) {
            val cx = enemyX - 40f + coinRng.nextFloat() * 80f
            val cy = centerY - 50f + (progress * 100f) + (coinRng.nextFloat() * 20f)
            drawCircle(GoldBright.copy(alpha = alpha), radius = 5f + lvl, center = Offset(cx, cy))
            drawCircle(GoldDark.copy(alpha = alpha), radius = 2.5f + lvl * 0.5f, center = Offset(cx, cy))
        }
    }
}

private fun DrawScope.drawMonkShockwave(progress: Float, lvl: Int, enemyX: Float, enemyY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)
    val ringCount = 2 + lvl * 2

    repeat(ringCount) { i ->
        val r = (progress * (100f + lvl * 25f)) - (i * 20f)
        if (r > 0f) {
            val col = if (i % 2 == 0) GoldBright else EnemyRed
            drawCircle(
                color = col.copy(alpha = alpha * (1f - i * 0.15f)),
                radius = r,
                center = Offset(enemyX, enemyY),
                style = Stroke(width = (8f - i * 1.5f).coerceAtLeast(2f))
            )
        }
    }

    val rng = Random(333)
    val sparkCount = 12 + lvl * 12
    repeat(sparkCount) {
        val a = rng.nextFloat() * 2f * Math.PI.toFloat()
        val d = progress * (100f + lvl * 20f)
        drawRect(
            color = Color.White.copy(alpha = alpha),
            topLeft = Offset(enemyX + cos(a) * d, enemyY + sin(a) * d),
            size = Size(6f + lvl, 6f + lvl)
        )
    }
}

private fun DrawScope.drawPaladinHoly(progress: Float, lvl: Int, heroX: Float, enemyX: Float, centerY: Float, h: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    val beamX = enemyX - 100f + (progress * 200f)
    val numBeams = if (lvl >= 3) 3 else if (lvl >= 2) 2 else 1

    repeat(numBeams) { i ->
        val offsetX = (i - (numBeams - 1) / 2f) * 30f
        drawLine(
            color = Color.White.copy(alpha = alpha),
            start = Offset(beamX + offsetX, 0f),
            end = Offset(beamX + offsetX, h),
            strokeWidth = 10f + lvl * 2f
        )
        drawLine(
            color = GoldBright.copy(alpha = alpha),
            start = Offset(beamX + offsetX - 8f, 0f),
            end = Offset(beamX + offsetX - 8f, h),
            strokeWidth = 5f
        )
    }

    val rng = Random(999)
    val starCount = 10 + lvl * 10
    repeat(starCount) {
        val px = heroX - 80f + rng.nextFloat() * 160f
        val py = centerY - 80f + rng.nextFloat() * 160f
        drawCircle(GoldBright.copy(alpha = alpha * 0.8f), radius = 4f + lvl * 2f, center = Offset(px, py))
    }
}

private fun DrawScope.drawRedMageDualcast(progress: Float, lvl: Int, enemyX: Float, enemyY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    if (progress < 0.5f) {
        val p1 = progress / 0.5f
        drawCircle(
            EnemyRed.copy(alpha = alpha),
            radius = p1 * (60f + lvl * 20f),
            center = Offset(enemyX - 25f, enemyY - 25f)
        )
    }

    if (progress > 0.25f) {
        val p2 = (progress - 0.25f) / 0.75f
        drawCircle(
            Color.Cyan.copy(alpha = alpha),
            radius = p2 * (65f + lvl * 20f),
            center = Offset(enemyX + 25f, enemyY + 25f),
            style = Stroke(width = 8f)
        )
    }

    if (lvl >= 3 && progress > 0.4f) {
        val p3 = (progress - 0.4f) / 0.6f
        drawCircle(
            GoldBright.copy(alpha = alpha),
            radius = p3 * 90f,
            center = Offset(enemyX, enemyY),
            style = Stroke(width = 6f)
        )
    }
}

private fun DrawScope.drawNinjaShuriken(progress: Float, lvl: Int, heroX: Float, enemyX: Float, centerY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)
    val shurikenCount = 3 * lvl

    repeat(shurikenCount) { i ->
        val startY = centerY - 50f + (i * (100f / shurikenCount.coerceAtLeast(1)))
        val projX = heroX + (enemyX - heroX) * progress

        drawRect(
            color = Color.LightGray.copy(alpha = alpha),
            topLeft = Offset(projX - 12f, startY - 3f),
            size = Size(24f, 6f)
        )
        drawRect(
            color = Color.LightGray.copy(alpha = alpha),
            topLeft = Offset(projX - 3f, startY - 12f),
            size = Size(6f, 24f)
        )
    }

    if (progress > 0.5f) {
        repeat(lvl) { i ->
            val off = (i - (lvl - 1) / 2f) * 20f
            drawLine(
                color = Color.White.copy(alpha = alpha),
                start = Offset(enemyX - 50f, centerY - 50f + off),
                end = Offset(enemyX + 50f, centerY + 50f + off),
                strokeWidth = 6f
            )
        }
    }
}

private fun DrawScope.drawDragoonJump(progress: Float, lvl: Int, heroX: Float, enemyX: Float, h: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    if (progress < 0.4f) {
        val leapY = (h * 0.5f) - (progress * h * 1.2f)
        drawRect(Color.Cyan.copy(alpha = alpha), topLeft = Offset(heroX - 12f, leapY), size = Size(24f, 48f))
    } else {
        val plungeProgress = (progress - 0.4f) / 0.6f
        val spearY = -100f + (plungeProgress * (h * 0.5f + 100f))

        val spearWidth = 8f + lvl * 3f
        drawLine(
            color = Color.White.copy(alpha = alpha),
            start = Offset(enemyX, spearY - 100f),
            end = Offset(enemyX, spearY),
            strokeWidth = spearWidth
        )
        drawLine(
            color = Color.Cyan.copy(alpha = alpha),
            start = Offset(enemyX - 8f, spearY - 100f),
            end = Offset(enemyX - 8f, spearY),
            strokeWidth = spearWidth / 2f
        )

        if (plungeProgress > 0.7f) {
            repeat(lvl) { i ->
                val r = (plungeProgress - 0.7f) * (150f + i * 50f)
                drawCircle(
                    color = Color.Cyan.copy(alpha = alpha * (0.8f - i * 0.2f)),
                    radius = r,
                    center = Offset(enemyX, h * 0.5f),
                    style = Stroke(width = 6f)
                )
            }
        }
    }
}

private fun DrawScope.drawBardSong(progress: Float, lvl: Int, heroX: Float, h: Float) {
    val alpha = sin(progress * Math.PI.toFloat()).coerceIn(0f, 1f)
    val rng = Random(555)

    repeat(lvl) { i ->
        drawCircle(
            color = Color(0xFFFF88CC).copy(alpha = alpha * (0.6f - i * 0.15f)),
            radius = progress * (140f + i * 40f),
            center = Offset(heroX, h * 0.5f),
            style = Stroke(width = 6f)
        )
    }

    val noteCount = 8 * lvl
    repeat(noteCount) { i ->
        val nx = heroX - 80f + (rng.nextFloat() * 160f)
        val ny = (h * 0.6f) - (progress * 150f) - (i * 10f)
        val col = if (i % 2 == 0) Color(0xFFFF88CC) else GoldBright
        drawRect(col.copy(alpha = alpha), topLeft = Offset(nx, ny), size = Size(10f, 10f))
        drawRect(Color.White.copy(alpha = alpha), topLeft = Offset(nx + 8f, ny - 6f), size = Size(4f, 12f))
    }
}

private fun DrawScope.drawSamuraiZeninage(progress: Float, lvl: Int, enemyX: Float, centerY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    val rng = Random(444)
    val petalCount = 20 * lvl
    repeat(petalCount) { i ->
        val px = (enemyX - 140f) + (rng.nextFloat() * 280f)
        val py = (centerY - 100f) + (rng.nextFloat() * 200f)
        val pColor = if (i % 2 == 0) Color(0xFFFFB7C5) else GoldBright
        drawRect(pColor.copy(alpha = alpha), topLeft = Offset(px, py), size = Size(8f, 8f))
    }

    repeat(lvl) { i ->
        val offY = (i - (lvl - 1) / 2f) * 30f
        val arcPath = Path().apply {
            moveTo(enemyX - 120f, centerY - 60f + offY)
            quadraticTo(enemyX, centerY + 80f + offY, enemyX + 120f, centerY - 60f + offY)
        }
        drawPath(arcPath, Color.White.copy(alpha = alpha), style = Stroke(width = 6f))
    }
}

private fun DrawScope.drawNecromancerSoulDrain(progress: Float, lvl: Int, heroX: Float, enemyX: Float, centerY: Float) {
    val alpha = sin(progress * Math.PI.toFloat()).coerceIn(0f, 1f)

    repeat(lvl) { i ->
        val offY = (i - (lvl - 1) / 2f) * 15f
        drawLine(
            brush = Brush.horizontalGradient(listOf(Color(0xFF330033), Color(0xFFAA44FF), Color.White)),
            start = Offset(enemyX, centerY + offY),
            end = Offset(heroX, centerY + offY),
            strokeWidth = 8f
        )
    }

    val particleCount = 10 * lvl
    repeat(particleCount) { i ->
        val t = ((progress + i * 0.08f) % 1.0f)
        val px = enemyX - (enemyX - heroX) * t
        val py = centerY + sin(t * Math.PI.toFloat() * 4f) * (15f + lvl * 5f)
        drawCircle(Color(0xFFAA44FF).copy(alpha = alpha), radius = 5f + lvl, center = Offset(px, py))
    }
}

private fun DrawScope.drawMimeMirror(progress: Float, lvl: Int, heroX: Float, centerY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    repeat(lvl) { i ->
        val r = progress * (90f + i * 30f)
        drawCircle(
            color = Color(0xFFFFFF99).copy(alpha = alpha * (1f - i * 0.2f)),
            radius = r,
            center = Offset(heroX, centerY),
            style = Stroke(width = 8f - i * 2f)
        )
    }
}

private fun DrawScope.drawOnionKnightSpin(progress: Float, lvl: Int, enemyX: Float, enemyY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)
    val rot = progress * (720f + lvl * 360f)

    drawCircle(
        color = Color(0xFF88FF88).copy(alpha = alpha),
        radius = 20f + progress * (30f + lvl * 15f),
        center = Offset(enemyX, enemyY)
    )

    val sparkCount = 12 * lvl
    repeat(sparkCount) { i ->
        val angle = (i / sparkCount.toFloat()) * 2f * Math.PI.toFloat() + Math.toRadians(rot.toDouble()).toFloat()
        val dist = progress * (100f + lvl * 20f)
        val px = enemyX + cos(angle) * dist
        val py = enemyY + sin(angle) * dist

        drawRect(GoldBright.copy(alpha = alpha), topLeft = Offset(px - 4f, py - 1f), size = Size(8f, 2f))
        drawRect(GoldBright.copy(alpha = alpha), topLeft = Offset(px - 1f, py - 4f), size = Size(2f, 8f))
    }
}

private fun DrawScope.drawBlueMageAura(progress: Float, lvl: Int, heroX: Float, enemyX: Float, centerY: Float) {
    val alpha = sin(progress * Math.PI.toFloat()).coerceIn(0f, 1f)

    repeat(lvl) { i ->
        drawCircle(
            color = Color(0xFF3366FF).copy(alpha = alpha * (0.8f - i * 0.2f)),
            radius = 40f + i * 20f,
            center = Offset(heroX, centerY + 30f),
            style = Stroke(width = 4f)
        )
    }

    val waveX = heroX + (enemyX - heroX) * progress
    drawCircle(
        brush = Brush.radialGradient(listOf(Color.White, Color(0xFF00E5FF), Color(0xFF0033FF), Color.Transparent), center = Offset(waveX, centerY), radius = 50f + lvl * 15f),
        radius = 50f + lvl * 15f,
        center = Offset(waveX, centerY),
        alpha = alpha
    )
}

private fun DrawScope.drawFreelancerRush(progress: Float, lvl: Int, heroX: Float, enemyX: Float, centerY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)
    val rushX = heroX + (enemyX - heroX) * progress
    val lineCount = 5 + lvl * 2

    repeat(lineCount) { i ->
        val offsetY = (i - lineCount / 2) * 12f
        drawLine(
            color = Color.White.copy(alpha = alpha),
            start = Offset(rushX - 40f, centerY + offsetY),
            end = Offset(rushX + 20f, centerY + offsetY),
            strokeWidth = 4f
        )
    }

    if (progress > 0.5f) {
        val rng = Random(123)
        val sparkCount = 8 * lvl
        repeat(sparkCount) {
            val dx = (rng.nextFloat() - 0.5f) * (60f + lvl * 20f)
            val dy = (rng.nextFloat() - 0.5f) * (60f + lvl * 20f)
            drawRect(GoldBright.copy(alpha = alpha), topLeft = Offset(enemyX + dx, centerY + dy), size = Size(6f, 6f))
        }
    }
}

private fun DrawScope.drawSummonEffect(
    summonName: String,
    lvl: Int,
    progress: Float,
    enemyX: Float,
    centerY: Float,
    w: Float,
    h: Float
) {
    val alpha = sin(progress * Math.PI.toFloat()).coerceIn(0f, 1f)

    // Draw Pixel Avatar Sprite of the Summon floating in the sky above the enemy target
    val avatarSize = 120f + lvl * 30f
    val avatarY = (centerY - 130f).coerceAtLeast(avatarSize / 2f + 20f)

    val boxLeft = enemyX - avatarSize / 2f
    val boxTop = avatarY - avatarSize / 2f
    val boxRight = w - (boxLeft + avatarSize)
    val boxBottom = h - (boxTop + avatarSize)

    if (alpha > 0.01f) {
        val paint = Paint().apply { this.alpha = alpha }
        drawContext.canvas.saveLayer(
            Rect(boxLeft, boxTop, boxLeft + avatarSize, boxTop + avatarSize),
            paint
        )
        inset(left = boxLeft, top = boxTop, right = boxRight, bottom = boxBottom) {
            when (summonName) {
                "Shiva" -> drawShiva()
                "Ramuh" -> drawRamuh()
                "Bahamut" -> drawDarkBahamut()
                else -> drawIfrit()
            }
        }
        drawContext.canvas.restore()
    }

    when (summonName) {
        "Shiva" -> {
            val rng = Random(111)
            val count = 25 * lvl
            repeat(count) {
                val px = enemyX - 120f + rng.nextFloat() * 240f
                val py = centerY - 80f + rng.nextFloat() * 200f
                drawRect(Color.White.copy(alpha = alpha), topLeft = Offset(px, py), size = Size(8f, 8f))
            }

            repeat(lvl) { i ->
                val offX = (i - (lvl - 1) / 2f) * 30f
                drawLine(
                    color = Color.Cyan.copy(alpha = alpha),
                    start = Offset(enemyX + 60f + offX, centerY - 150f),
                    end = Offset(enemyX - 60f + offX, centerY + 150f),
                    strokeWidth = 12f + lvl * 2f
                )
            }
        }
        "Ramuh" -> {
            val boltCount = 3 + lvl * 2
            repeat(boltCount) { i ->
                val x = (enemyX - 100f) + (i * (200f / boltCount.coerceAtLeast(1)))
                drawLine(
                    color = GoldBright.copy(alpha = alpha),
                    start = Offset(x, 0f),
                    end = Offset(x - 25f, h),
                    strokeWidth = 8f + lvl * 2f
                )
            }
            drawCircle(
                color = Color(0xFF00FFFF).copy(alpha = alpha * 0.7f),
                radius = progress * (80f + lvl * 30f),
                center = Offset(enemyX, centerY),
                style = Stroke(width = 6f)
            )
        }
        "Bahamut" -> {
            val radius = progress * (200f + lvl * 60f)
            drawCircle(
                brush = Brush.radialGradient(listOf(Color.White, GoldBright, Color(0xFFAA44FF), Color.Transparent), center = Offset(enemyX, centerY), radius = radius.coerceAtLeast(10f)),
                radius = radius,
                center = Offset(enemyX, centerY),
                alpha = alpha
            )

            repeat(lvl) { i ->
                drawCircle(
                    color = GoldBright.copy(alpha = alpha * 0.8f),
                    radius = radius * (0.5f + i * 0.3f),
                    center = Offset(enemyX, centerY),
                    style = Stroke(width = 4f)
                )
            }
        }
        else -> { // Ifrit
            val rng = Random(222)
            val emberCount = 30 * lvl
            repeat(emberCount) {
                val px = enemyX - 120f + (rng.nextFloat() * 240f)
                val py = h - (progress * h * 0.95f)
                drawRect(GoldBright.copy(alpha = alpha), topLeft = Offset(px, py), size = Size(10f + lvl * 2f, 10f + lvl * 2f))
            }

            repeat(lvl) { i ->
                val pillarX = enemyX - 60f + i * 60f
                drawRect(
                    brush = Brush.verticalGradient(listOf(EnemyRed.copy(alpha = alpha), GoldBright.copy(alpha = alpha * 0.5f), Color.Transparent)),
                    topLeft = Offset(pillarX - 20f, centerY - 100f),
                    size = Size(40f, 200f)
                )
            }
        }
    }
}

private fun DrawScope.drawMonsterAttack(monsterType: MonsterType?, isBossAttack: Boolean, lvl: Int, progress: Float, heroX: Float, centerY: Float) {
    val alpha = (1f - progress).coerceIn(0f, 1f)

    if (isBossAttack || monsterType?.name?.contains("MAGE") == true) {
        drawCircle(
            color = Color(0xFFAA44FF).copy(alpha = alpha),
            radius = progress * (60f + lvl * 20f),
            center = Offset(heroX, centerY)
        )
    } else {
        val clawCount = 3 + (lvl - 1)
        repeat(clawCount) { i ->
            val offsetY = (i - (clawCount - 1) / 2f) * 18f
            drawLine(
                color = EnemyRed.copy(alpha = alpha),
                start = Offset(heroX - 35f, centerY + offsetY - 25f),
                end = Offset(heroX + 35f, centerY + offsetY + 25f),
                strokeWidth = 6f
            )
        }
    }
}

private fun DrawScope.drawBossCinematicAttack(progress: Float, w: Float, h: Float) {
    val alpha = sin(progress * Math.PI.toFloat()).coerceIn(0f, 1f)
    val heroX = w * 0.22f
    val centerY = h * 0.5f

    // Concentric Pulsing Void Shockwaves around hero
    repeat(3) { i ->
        val r = progress * (100f + i * 50f)
        drawCircle(
            color = EnemyRed.copy(alpha = alpha * (0.8f - i * 0.2f)),
            radius = r,
            center = Offset(heroX, centerY),
            style = Stroke(width = 6f)
        )
    }

    // High-Intensity Beam
    val beamY = centerY - 100f + (progress * 200f)
    drawLine(
        brush = Brush.horizontalGradient(listOf(EnemyRed, Color.Magenta, Color.White, EnemyRed)),
        start = Offset(w, beamY),
        end = Offset(0f, beamY),
        strokeWidth = 16f
    )

    val rng = Random(5050)
    repeat(30) {
        val px = heroX - 100f + (rng.nextFloat() * 200f)
        val py = centerY - 100f + (rng.nextFloat() * 200f)
        drawCircle(EnemyRed.copy(alpha = alpha), radius = 6f + rng.nextFloat() * 6f, center = Offset(px, py))
    }
}
