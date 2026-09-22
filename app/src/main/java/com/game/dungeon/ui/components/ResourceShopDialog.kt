package com.game.dungeon.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.billingclient.api.ProductDetails
import com.game.dungeon.R
import com.game.dungeon.monetization.InAppProduct
import com.game.dungeon.monetization.ResourceType
import com.game.dungeon.ui.theme.*

@Composable
fun ResourceShopDialog(
    initialResourceType: ResourceType,
    currentGil: Long,
    currentMagicite: Int,
    totalGilEarned: Long,
    totalMagiciteEarned: Int,
    productDetailsMap: Map<String, ProductDetails>,
    onWatchGilAd: (Activity) -> Unit,
    onWatchMagiciteAd: (Activity) -> Unit,
    onBuyProduct: (Activity, InAppProduct) -> Unit,
    onDismiss: () -> Unit
) {
    val activity = LocalContext.current as? Activity
    var selectedTab by remember { mutableStateOf(initialResourceType) }

    val freeGilReward = (totalGilEarned * 0.0025f).toLong().coerceAtLeast(100L)
    val freeMagiciteReward = (totalMagiciteEarned * 0.005f).toInt().coerceAtLeast(10)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        GoldenBorderBox(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .background(BgDarkest)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = safeStringResource(R.string.resource_shop_title),
                        style = PixelHeading.copy(fontSize = 18.sp),
                        color = GoldBright
                    )
                    PixelButton(
                        label = "✕",
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp),
                        horizontalPadding = 0.dp,
                        verticalPadding = 0.dp
                    )
                }

                PixelDivider()

                Spacer(Modifier.height(8.dp))

                // Tabs (Gil vs Magicite)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val gilSelected = selectedTab == ResourceType.GIL
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (gilSelected) BgMedium else BgDark)
                            .border(
                                width = if (gilSelected) 2.dp else 1.dp,
                                color = if (gilSelected) GoldBright else StoneGray
                            )
                            .clickable { selectedTab = ResourceType.GIL },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🪙 ${safeStringResource(R.string.tab_gil)} (${formatGold(currentGil)})",
                            style = PixelBody,
                            color = if (gilSelected) GoldBright else StoneGray
                        )
                    }

                    val magiciteSelected = selectedTab == ResourceType.MAGICITE
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (magiciteSelected) BgMedium else BgDark)
                            .border(
                                width = if (magiciteSelected) 2.dp else 1.dp,
                                color = if (magiciteSelected) Color(0xFF00E5FF) else StoneGray
                            )
                            .clickable { selectedTab = ResourceType.MAGICITE },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💎 ${safeStringResource(R.string.tab_magicite)} ($currentMagicite)",
                            style = PixelBody,
                            color = if (magiciteSelected) Color(0xFF00E5FF) else StoneGray
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Scrollable Content Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // --- FREE REWARDS SECTION ---
                    Text(
                        text = safeStringResource(R.string.free_reward_section),
                        style = PixelSmall,
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )

                    GoldenBorderBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgMedium)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (selectedTab == ResourceType.GIL) "🪙 +${formatGold(freeGilReward)} Gil" else "💎 +$freeMagiciteReward Magicite",
                                    style = PixelHeading,
                                    color = if (selectedTab == ResourceType.GIL) GoldBright else Color(0xFF00E5FF)
                                )
                                Text(
                                    text = safeStringResource(
                                        if (selectedTab == ResourceType.GIL) R.string.ad_gil_reward else R.string.ad_magicite_reward
                                    ),
                                    style = PixelSmall,
                                    color = StoneGray
                                )
                            }

                            PixelButton(
                                label = "📺 " + safeStringResource(
                                    if (selectedTab == ResourceType.GIL) R.string.watch_ad_for_gil else R.string.watch_ad_for_magicite,
                                    if (selectedTab == ResourceType.GIL) formatGold(freeGilReward) else freeMagiciteReward
                                ),
                                onClick = {
                                    if (activity != null) {
                                        if (selectedTab == ResourceType.GIL) {
                                            onWatchGilAd(activity)
                                        } else {
                                            onWatchMagiciteAd(activity)
                                        }
                                    }
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // --- IN-APP PURCHASE PACKS SECTION ---
                    Text(
                        text = safeStringResource(R.string.iap_packs_section),
                        style = PixelSmall,
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )

                    val products = if (selectedTab == ResourceType.GIL) {
                        InAppProduct.GIL_PRODUCTS
                    } else {
                        InAppProduct.MAGICITE_PRODUCTS
                    }

                    products.forEach { product ->
                        val productDetails = productDetailsMap[product.id]
                        val priceText = productDetails?.oneTimePurchaseOfferDetailsList?.firstOrNull()?.formattedPrice ?: product.defaultPrice

                        GoldenBorderBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BgMedium)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = product.iconEmoji,
                                        fontSize = 24.sp
                                    )
                                    Column {
                                        Text(
                                            text = safeStringResource(product.titleResId),
                                            style = PixelBody.copy(fontWeight = FontWeight.Bold),
                                            color = GoldBright
                                        )
                                        Text(
                                            text = if (product.resourceType == ResourceType.GIL) {
                                                "+${formatGold(product.rewardAmount)} Gil"
                                            } else {
                                                "+${product.rewardAmount} Magicite"
                                            },
                                            style = PixelSmall,
                                            color = if (product.resourceType == ResourceType.GIL) GoldBright else Color(0xFF00E5FF)
                                        )
                                    }
                                }

                                PixelButton(
                                    label = priceText,
                                    onClick = {
                                        if (activity != null) {
                                            onBuyProduct(activity, product)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
