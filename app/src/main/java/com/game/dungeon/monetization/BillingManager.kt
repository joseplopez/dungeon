package com.game.dungeon.monetization

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import com.game.dungeon.R
import kotlinx.coroutines.flow.StateFlow

enum class ResourceType {
    GIL,
    MAGICITE
}

data class InAppProduct(
    val id: String,
    val resourceType: ResourceType,
    val rewardAmount: Long,
    val defaultPrice: String,
    val titleResId: Int,
    val iconEmoji: String
) {
    companion object {
        const val GIL_PACK_SMALL = "gil_pack_small"
        const val GIL_PACK_MEDIUM = "gil_pack_medium"
        const val GIL_PACK_LARGE = "gil_pack_large"

        const val MAGICITE_PACK_SMALL = "magicite_pack_small"
        const val MAGICITE_PACK_MEDIUM = "magicite_pack_medium"
        const val MAGICITE_PACK_LARGE = "magicite_pack_large"

        val ALL_PRODUCT_IDS = listOf(
            GIL_PACK_SMALL,
            GIL_PACK_MEDIUM,
            GIL_PACK_LARGE,
            MAGICITE_PACK_SMALL,
            MAGICITE_PACK_MEDIUM,
            MAGICITE_PACK_LARGE
        )

        val GIL_PRODUCTS = listOf(
            InAppProduct(
                id = GIL_PACK_SMALL,
                resourceType = ResourceType.GIL,
                rewardAmount = 5_000L,
                defaultPrice = "$0.49",
                titleResId = R.string.gil_pack_small_title,
                iconEmoji = "💰"
            ),
            InAppProduct(
                id = GIL_PACK_MEDIUM,
                resourceType = ResourceType.GIL,
                rewardAmount = 25_000L,
                defaultPrice = "$1.49",
                titleResId = R.string.gil_pack_medium_title,
                iconEmoji = "🪙"
            ),
            InAppProduct(
                id = GIL_PACK_LARGE,
                resourceType = ResourceType.GIL,
                rewardAmount = 100_000L,
                defaultPrice = "$3.99",
                titleResId = R.string.gil_pack_large_title,
                iconEmoji = "🏛️"
            )
        )

        val MAGICITE_PRODUCTS = listOf(
            InAppProduct(
                id = MAGICITE_PACK_SMALL,
                resourceType = ResourceType.MAGICITE,
                rewardAmount = 75L,
                defaultPrice = "$0.49",
                titleResId = R.string.magicite_pack_small_title,
                iconEmoji = "🔹"
            ),
            InAppProduct(
                id = MAGICITE_PACK_MEDIUM,
                resourceType = ResourceType.MAGICITE,
                rewardAmount = 500L,
                defaultPrice = "$1.99",
                titleResId = R.string.magicite_pack_medium_title,
                iconEmoji = "💎"
            ),
            InAppProduct(
                id = MAGICITE_PACK_LARGE,
                resourceType = ResourceType.MAGICITE,
                rewardAmount = 2000L,
                defaultPrice = "$4.99",
                titleResId = R.string.magicite_pack_large_title,
                iconEmoji = "👑"
            )
        )
    }
}

interface BillingManager {
    val isConnected: StateFlow<Boolean>
    val productDetailsMap: StateFlow<Map<String, ProductDetails>>

    fun startConnection()
    fun launchBillingFlow(
        activity: Activity,
        product: InAppProduct,
        onSuccess: (product: InAppProduct) -> Unit,
        onError: (message: String) -> Unit
    )
}
