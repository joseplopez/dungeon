package com.game.dungeon.ui.screens

import android.app.Activity
import android.content.Context
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.game.dungeon.R
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.models.MaterialCatalog
import com.game.dungeon.data.models.Rarity
import com.game.dungeon.monetization.InAppProduct
import com.game.dungeon.ui.components.*
import com.game.dungeon.ui.theme.*
import com.game.dungeon.ui.viewmodels.GachaBanner
import com.game.dungeon.ui.viewmodels.GachaReward
import com.game.dungeon.ui.viewmodels.GachaponViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GachaponScreen(
    onBack: () -> Unit,
    isMuted: Boolean = false,
    onToggleMusic: () -> Unit = {},
    viewModel: GachaponViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val uiState by viewModel.uiState.collectAsState()
    val gs by viewModel.gameState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.ensureCarouselInitialized(context)
    }

    val lazyListState = rememberLazyListState()
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    var containerWidthPx by remember { mutableFloatStateOf(0f) }

    val itemWidthDp = 120.dp
    val itemSpacingDp = 12.dp
    val itemWidthPx = with(density) { itemWidthDp.toPx() }
    val itemSpacingPx = with(density) { itemSpacingDp.toPx() }
    val totalItemWidthPx = itemWidthPx + itemSpacingPx

    // Animation physics when spin is triggered
    LaunchedEffect(uiState.isSpinning, uiState.targetWinningIndex, uiState.carouselItems) {
        if (uiState.isSpinning && uiState.carouselItems.isNotEmpty()) {
            val targetIndex = uiState.targetWinningIndex
            val centerOffsetPx = if (containerWidthPx > 0f) (containerWidthPx - itemWidthPx) / 2f else 0f
            val targetScrollOffsetPx = (targetIndex * totalItemWidthPx - centerOffsetPx).coerceAtLeast(0f)

            // Reset list to position 0
            lazyListState.scrollToItem(0, 0)

            // Deceleration curve for gacha wheel spin
            val animatable = Animatable(0f)
            animatable.animateTo(
                targetValue = targetScrollOffsetPx,
                animationSpec = tween(
                    durationMillis = 4200,
                    easing = CubicBezierEasing(0.05f, 0.85f, 0.15f, 1.0f)
                )
            ) {
                val currentPx = value
                val index = (currentPx / totalItemWidthPx).toInt().coerceAtLeast(0)
                val offset = (currentPx % totalItemWidthPx).toInt()
                coroutineScope.launch {
                    lazyListState.scrollToItem(index, offset)
                }
            }

            // Ensure exact alignment on finish
            val finalIndex = (targetScrollOffsetPx / totalItemWidthPx).toInt()
            val finalOffset = (targetScrollOffsetPx % totalItemWidthPx).toInt()
            lazyListState.scrollToItem(finalIndex, finalOffset)

            delay(400)
            viewModel.onSpinAnimationComplete()
        }
    }

    PixelTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDarkest)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header Bar
                GoldenBorderBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BgDarkest)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                PixelGoldDisplay(amount = gs?.gold ?: 0L)
                                PixelMagiciteDisplay(amount = gs?.magicite ?: 0L)
                            }

                            Text(
                                text = stringResource(R.string.gacha_screen_title),
                                style = PixelHeading,
                                color = GoldBright
                            )

                            Row(
                                verticalAlignment = CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MusicToggleButton(isMuted = isMuted, onToggle = onToggleMusic)
                                PixelButton(
                                    label = stringResource(R.string.gacha_close),
                                    onClick = onBack,
                                    enabled = !uiState.isSpinning && !uiState.isBillingInProcess,
                                    modifier = Modifier.size(32.dp),
                                    horizontalPadding = 0.dp,
                                    verticalPadding = 0.dp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Scrollable Central Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Banner Tabs Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GachaBannerTab(
                            title = stringResource(R.string.gacha_armory_tab),
                            isSelected = uiState.selectedBanner == GachaBanner.ARMORY,
                            enabled = !uiState.isSpinning && !uiState.isBillingInProcess,
                            onClick = { viewModel.selectBanner(GachaBanner.ARMORY, context) },
                            modifier = Modifier.weight(1f)
                        )
                        GachaBannerTab(
                            title = stringResource(R.string.gacha_alchemist_tab),
                            isSelected = uiState.selectedBanner == GachaBanner.ALCHEMIST,
                            enabled = !uiState.isSpinning && !uiState.isBillingInProcess,
                            onClick = { viewModel.selectBanner(GachaBanner.ALCHEMIST, context) },
                            modifier = Modifier.weight(1f)
                        )
                        GachaBannerTab(
                            title = stringResource(R.string.gacha_treasury_tab),
                            isSelected = uiState.selectedBanner == GachaBanner.TREASURY,
                            enabled = !uiState.isSpinning && !uiState.isBillingInProcess,
                            onClick = { viewModel.selectBanner(GachaBanner.TREASURY, context) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Banner Information Description
                    BannerDescriptionCard(
                        banner = uiState.selectedBanner,
                        highestFloor = gs?.highestFloor ?: 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    )

                    // Pity Progress Bar
                    GachaPityProgressBar(
                        pityCounter = uiState.gachaPityCounter,
                        isLegendary = uiState.isLegendaryPull,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    )

                    // Carousel Viewport Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(145.dp)
                            .padding(horizontal = 12.dp)
                            .onGloballyPositioned { layoutCoordinates ->
                                containerWidthPx = layoutCoordinates.size.width.toFloat()
                            }
                    ) {
                        PixelPanel(
                            modifier = Modifier.fillMaxSize(),
                            bgColor = Color(0xFF0D0B18),
                            borderColor = GoldBright
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Carousel LazyRow
                                LazyRow(
                                    state = lazyListState,
                                    userScrollEnabled = !uiState.isSpinning,
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(itemSpacingDp),
                                    verticalAlignment = CenterVertically,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Center)
                                ) {
                                    itemsIndexed(uiState.carouselItems) { index, reward ->
                                        GachaCarouselItem(
                                            reward = reward,
                                            isTarget = (!uiState.isSpinning && uiState.pendingClaimReward != null && index == uiState.targetWinningIndex),
                                            modifier = Modifier
                                                .width(itemWidthDp)
                                                .height(130.dp)
                                        )
                                    }
                                }

                                // Center Pointer / Selector Arrows
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val w = size.width
                                    val h = size.height
                                    val arrowSize = 16.dp.toPx()
                                    val centerX = w / 2f

                                    // Top Arrow pointing DOWN
                                    val topPath = Path().apply {
                                        moveTo(centerX - arrowSize, 0f)
                                        lineTo(centerX + arrowSize, 0f)
                                        lineTo(centerX, arrowSize * 1.2f)
                                        close()
                                    }
                                    drawPath(path = topPath, color = GoldBright)
                                    drawPath(
                                        path = topPath,
                                        color = Color.Black,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                                    )

                                    // Bottom Arrow pointing UP
                                    val bottomPath = Path().apply {
                                        moveTo(centerX - arrowSize, h)
                                        lineTo(centerX + arrowSize, h)
                                        lineTo(centerX, h - arrowSize * 1.2f)
                                        close()
                                    }
                                    drawPath(path = bottomPath, color = GoldBright)
                                    drawPath(
                                        path = bottomPath,
                                        color = Color.Black,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                                    )

                                    // Vertical subtle center guide lines
                                    drawLine(
                                        color = GoldBright.copy(alpha = 0.4f),
                                        start = Offset(centerX, arrowSize * 1.2f),
                                        end = Offset(centerX, h - arrowSize * 1.2f),
                                        strokeWidth = 2.dp.toPx()
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Spin Action & Tickets Control Area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Ticket Count & Free Spin Status Bar
                    Row(
                        modifier = Modifier
                            .background(BgMedium, RoundedCornerShape(4.dp))
                            .border(1.dp, GoldDark, RoundedCornerShape(4.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.gacha_tickets_format, uiState.gachaTickets),
                            style = PixelHeading,
                            color = GoldBright,
                            fontSize = 12.sp
                        )
                        if (uiState.isFreeAvailable) {
                            Text(
                                text = stringResource(R.string.gacha_free_spin_available),
                                style = PixelSmall,
                                color = HpGreen,
                                fontSize = 11.sp
                            )
                        } else if (uiState.timeUntilNextFreeMs > 0L) {
                            Text(
                                text = stringResource(
                                    R.string.gacha_free_spin_countdown,
                                    formatTimeRemaining(context, uiState.timeUntilNextFreeMs)
                                ),
                                style = PixelSmall,
                                color = StoneGray,
                                fontSize = 10.sp
                            )
                        }
                    }

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage!!,
                            style = PixelSmall,
                            color = EnemyRed,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Action Buttons Row: Watch Ad (+1), Spin (Free or 1 Ticket), Buy 10 Tickets ($0.99)
                    val canSpin = !uiState.isSpinning && !uiState.isBillingInProcess && (uiState.isFreeAvailable || uiState.gachaTickets > 0)

                    val spinButtonLabel = when {
                        uiState.isSpinning -> stringResource(R.string.gacha_spinning)
                        uiState.isBillingInProcess -> stringResource(R.string.gacha_processing)
                        uiState.isLegendaryPull && uiState.isFreeAvailable -> stringResource(R.string.gacha_legendary_free)
                        uiState.isLegendaryPull -> stringResource(R.string.gacha_legendary_ticket)
                        uiState.isFreeAvailable -> stringResource(R.string.gacha_free_spin)
                        uiState.gachaTickets > 0 -> stringResource(R.string.gacha_spin_ticket)
                        else -> stringResource(R.string.gacha_no_tickets)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = CenterVertically
                    ) {
                        // Watch Ad Button (+1 Ticket)
                        PixelButton(
                            label = stringResource(R.string.gacha_watch_ad_ticket),
                            onClick = { activity?.let { viewModel.watchAdForTicket(it) } },
                            enabled = !uiState.isSpinning && !uiState.isBillingInProcess,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                        )

                        // Main Spin Button
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .height(42.dp),
                            contentAlignment = Center
                        ) {
                            PixelButton(
                                label = spinButtonLabel,
                                onClick = { activity?.let { viewModel.spin(it) } },
                                enabled = canSpin,
                                active = canSpin,
                                modifier = Modifier.fillMaxSize()
                            )

                            if (uiState.isBillingInProcess) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 8.dp),
                                    color = GoldBright,
                                    strokeWidth = 2.dp
                                )
                            }
                        }

                        // Buy 10 Tickets Pack ($0.99)
                        PixelButton(
                            label = stringResource(
                                R.string.gacha_buy_tickets,
                                InAppProduct.MISC_PRODUCT.defaultPrice
                            ),
                            onClick = { activity?.let { viewModel.buyTenTickets(it) } },
                            enabled = !uiState.isSpinning && !uiState.isBillingInProcess,
                            modifier = Modifier
                                .weight(1.2f)
                                .height(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    // Celebratory Claim Dialog
    if (uiState.pendingClaimReward != null) {
        GachaResultDialog(
            reward = uiState.pendingClaimReward!!,
            onClaim = {
                viewModel.claimPendingReward()
            }
        )
    }
}

@Composable
fun GachaBannerTab(
    title: String,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PixelButton(
        label = title,
        onClick = onClick,
        enabled = enabled,
        active = isSelected,
        modifier = modifier.height(36.dp),
        fontSize = 11.sp
    )
}

@Composable
fun GachaPityProgressBar(
    pityCounter: Int,
    isLegendary: Boolean,
    modifier: Modifier = Modifier
) {
    val maxPity = 10
    val currentProgress = if (isLegendary) maxPity else pityCounter.coerceIn(0, maxPity)

    val infiniteTransition = rememberInfiniteTransition(label = "pityGlowTransition")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val glowColor by infiniteTransition.animateColor(
        initialValue = Color(0xFFFFD700),
        targetValue = Color(0xFFB15EFF),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowColor"
    )

    val borderColor = if (isLegendary) glowColor.copy(alpha = glowAlpha) else GoldDark
    val containerBg = if (isLegendary) glowColor.copy(alpha = 0.15f * glowAlpha) else BgPanel

    PixelPanel(
        modifier = modifier,
        bgColor = containerBg,
        borderColor = borderColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically
            ) {
                Text(
                    text = stringResource(
                        if (isLegendary) R.string.gacha_legendary_pull_ready else R.string.gacha_pity_progress
                    ),
                    style = PixelHeading,
                    color = if (isLegendary) glowColor else GoldBright,
                    fontSize = 11.sp
                )
                Text(
                    text = stringResource(
                        R.string.gacha_pity_count_format,
                        if (isLegendary) maxPity else pityCounter
                    ),
                    style = PixelHeading,
                    color = if (isLegendary) glowColor else Color.White,
                    fontSize = 11.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(BgDarkest, RoundedCornerShape(2.dp))
                    .border(1.dp, if (isLegendary) glowColor else Color.DarkGray, RoundedCornerShape(2.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                for (i in 0 until maxPity) {
                    val blockColor = getPityBlockColor(i, currentProgress, isLegendary, glowColor)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(1.dp))
                            .background(blockColor)
                    )
                }
            }
        }
    }
}

private fun getPityBlockColor(
    index: Int,
    currentProgress: Int,
    isLegendary: Boolean,
    glowColor: Color
): Color {
    if (index >= currentProgress) return Color(0xFF222034)
    if (isLegendary) return glowColor
    return if (index >= 7) Color(0xFFFF9800) else GoldBright
}

@Composable
fun BannerDescriptionCard(
    banner: GachaBanner,
    highestFloor: Int,
    modifier: Modifier = Modifier
) {
    val (title, description, rates) = when (banner) {
        GachaBanner.ARMORY -> Triple(
            stringResource(R.string.gacha_armory_banner_title),
            stringResource(R.string.gacha_armory_banner_description, highestFloor + 100),
            stringResource(R.string.gacha_armory_banner_rates)
        )
        GachaBanner.ALCHEMIST -> Triple(
            stringResource(R.string.gacha_alchemist_banner_title),
            stringResource(R.string.gacha_alchemist_banner_description),
            stringResource(R.string.gacha_alchemist_banner_rates)
        )
        GachaBanner.TREASURY -> Triple(
            stringResource(R.string.gacha_treasury_banner_title),
            stringResource(R.string.gacha_treasury_banner_description),
            stringResource(R.string.gacha_treasury_banner_rates)
        )
    }

    PixelPanel(
        modifier = modifier,
        bgColor = BgPanel,
        borderColor = GoldDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically
            ) {
                Text(
                    text = title,
                    style = PixelHeading,
                    color = GoldBright,
                    fontSize = 12.sp
                )
                Text(
                    text = description,
                    style = PixelSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = rates,
                style = PixelSmall,
                color = GoldBright,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun GachaCarouselItem(
    reward: GachaReward,
    isTarget: Boolean,
    modifier: Modifier = Modifier
) {
    GachaRewardCard(reward = reward, isTarget = isTarget, modifier = modifier)
}

@Composable
fun GachaRewardCard(
    reward: GachaReward,
    isTarget: Boolean,
    modifier: Modifier = Modifier
) {
    val rarityColor = Color(reward.rarity.color)
    val bgColor = if (isTarget) GoldBright.copy(alpha = 0.25f) else BgMedium

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(4.dp))
            .border(2.dp, if (isTarget) GoldBright else rarityColor, RoundedCornerShape(4.dp))
            .padding(6.dp),
        contentAlignment = Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Rarity Tag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(rarityColor.copy(alpha = 0.8f), RoundedCornerShape(2.dp))
                    .padding(vertical = 1.dp),
                contentAlignment = Center
            ) {
                Text(
                    text = reward.rarity.name,
                    style = PixelSmall,
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Central Visual Sprite / Icon / Emoji
            when (reward) {
                is GachaReward.GearReward -> {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(BgDarkest, RoundedCornerShape(6.dp))
                            .border(1.dp, rarityColor, RoundedCornerShape(6.dp)),
                        contentAlignment = Center
                    ) {
                        EquipmentSprite(
                            item = reward.item,
                            slot = reward.item.slot,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                is GachaReward.MaterialReward -> {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(BgDarkest, RoundedCornerShape(6.dp))
                            .border(1.dp, rarityColor, RoundedCornerShape(6.dp)),
                        contentAlignment = Center
                    ) {
                        Text(
                            text = reward.material.emoji,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is GachaReward.GilReward -> {
                    Text(
                    text = stringResource(R.string.gacha_gil_icon),
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center
                    )
                }
                is GachaReward.MagiciteReward -> {
                    Text(
                    text = stringResource(R.string.gacha_magicite_icon),
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Reward Name Display
            val rewardName = when (reward) {
                is GachaReward.GearReward -> stringResource(
                    R.string.item_name_template,
                    reward.item.emoji,
                    reward.item.name
                )
                is GachaReward.MaterialReward -> stringResource(
                    R.string.gacha_material_name_template,
                    reward.material.emoji,
                    stringResource(reward.material.nameRes)
                )
                is GachaReward.GilReward -> stringResource(
                    R.string.gacha_gil_amount_format,
                    formatGold(reward.amount)
                )
                is GachaReward.MagiciteReward -> stringResource(
                    R.string.gacha_magicite_amount_format,
                    formatMagicite(reward.amount)
                )
            }

            Text(
                text = rewardName,
                style = PixelBody,
                color = Color.White,
                fontSize = 10.sp,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )

            // Subtitle / Description
            val rewardSubtitle = when (reward) {
                is GachaReward.GearReward -> stringResource(
                    R.string.gacha_gear_subtitle_format,
                    stringResource(reward.item.slot.nameRes),
                    reward.item.floorFound
                )
                is GachaReward.MaterialReward -> stringResource(R.string.gacha_material_quantity_format, reward.amount)
                is GachaReward.GilReward -> reward.tierLabel
                is GachaReward.MagiciteReward -> stringResource(R.string.gacha_cosmic_currency)
            }

            Text(
                text = rewardSubtitle,
                style = PixelSmall,
                color = StoneGray,
                fontSize = 8.sp,
                maxLines = 1,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun GachaResultDialog(
    reward: GachaReward,
    onClaim: () -> Unit
) {
    val rarityColor = Color(reward.rarity.color)

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        GoldenBorderBox(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .widthIn(max = 380.dp)
                .wrapContentHeight()
                .background(BgDarkest)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.gacha_you_won),
                    style = PixelHeading,
                    color = GoldBright,
                    fontSize = 18.sp
                )

                PixelDivider()

                // Reward Card Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgMedium, RoundedCornerShape(6.dp))
                        .border(3.dp, rarityColor, RoundedCornerShape(6.dp))
                        .padding(16.dp),
                    contentAlignment = Center
                ) {
                    Column(
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (reward) {
                            is GachaReward.GearReward -> {
                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .background(BgDarkest, RoundedCornerShape(8.dp))
                                        .border(2.dp, rarityColor, RoundedCornerShape(8.dp)),
                                    contentAlignment = Center
                                ) {
                                    EquipmentSprite(
                                        item = reward.item,
                                        slot = reward.item.slot,
                                        modifier = Modifier.size(50.dp)
                                    )
                                }

                                Text(
                                    text = stringResource(
                                        R.string.item_name_template,
                                        reward.item.emoji,
                                        reward.item.name
                                    ),
                                    style = PixelHeading,
                                    color = GoldBright,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Box(
                                    modifier = Modifier
                                        .background(rarityColor, RoundedCornerShape(3.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = reward.item.rarity.name,
                                        style = PixelSmall,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = stringResource(
                                        R.string.gacha_gear_reward_details_format,
                                        stringResource(reward.item.slot.nameRes),
                                        reward.item.floorFound
                                    ),
                                    style = PixelBody,
                                    color = StoneGray,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            is GachaReward.MaterialReward -> {
                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .background(BgDarkest, RoundedCornerShape(8.dp))
                                        .border(2.dp, rarityColor, RoundedCornerShape(8.dp)),
                                    contentAlignment = Center
                                ) {
                                    Text(
                                        text = reward.material.emoji,
                                        fontSize = 42.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Text(
                                    text = stringResource(
                                        R.string.gacha_material_name_template,
                                        reward.material.emoji,
                                        stringResource(reward.material.nameRes)
                                    ),
                                    style = PixelHeading,
                                    color = GoldBright,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Box(
                                    modifier = Modifier
                                        .background(rarityColor, RoundedCornerShape(3.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = reward.material.rarity.name,
                                        style = PixelSmall,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = stringResource(R.string.gacha_quantity_format, reward.amount),
                                    style = PixelBody,
                                    color = StoneGray,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            is GachaReward.GilReward -> {
                                PixelGoldDisplay(
                                    amount = reward.amount,
                                    style = PixelHeading.copy(fontSize = 22.sp),
                                    iconSize = 30.sp
                                )

                                Text(
                                    text = stringResource(
                                        R.string.gacha_gil_reward_format,
                                        formatGold(reward.amount)
                                    ),
                                    style = PixelHeading,
                                    color = GoldBright,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Box(
                                    modifier = Modifier
                                        .background(rarityColor, RoundedCornerShape(3.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = reward.rarity.name,
                                        style = PixelSmall,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = reward.tierLabel,
                                    style = PixelBody,
                                    color = StoneGray,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            is GachaReward.MagiciteReward -> {
                                PixelMagiciteDisplay(
                                    amount = reward.amount,
                                    style = PixelHeading.copy(fontSize = 22.sp),
                                    iconSize = 30.sp
                                )

                                Text(
                                    text = stringResource(
                                        R.string.gacha_magicite_reward_format,
                                        formatMagicite(reward.amount)
                                    ),
                                    style = PixelHeading,
                                    color = GoldBright,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Box(
                                    modifier = Modifier
                                        .background(rarityColor, RoundedCornerShape(3.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = reward.rarity.name,
                                        style = PixelSmall,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = stringResource(R.string.gacha_cosmic_currency),
                                    style = PixelBody,
                                    color = StoneGray,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                PixelButton(
                    label = stringResource(R.string.gacha_claim_reward),
                    onClick = onClaim,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(42.dp),
                    active = true
                )
            }
        }
    }
}

private fun formatTimeRemaining(context: Context, ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return context.getString(R.string.gacha_time_remaining_format, hours, minutes, seconds)
}