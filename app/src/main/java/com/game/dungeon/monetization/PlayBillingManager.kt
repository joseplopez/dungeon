package com.game.dungeon.monetization

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.game.dungeon.BuildConfig
import com.game.dungeon.data.repository.GameRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayBillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: GameRepository
) : BillingManager, PurchasesUpdatedListener {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _productDetailsMap = MutableStateFlow<Map<String, ProductDetails>>(emptyMap())
    override val productDetailsMap: StateFlow<Map<String, ProductDetails>> = _productDetailsMap.asStateFlow()

    private var pendingSuccessCallback: ((InAppProduct) -> Unit)? = null
    private var pendingErrorCallback: ((String) -> Unit)? = null
    private var pendingProduct: InAppProduct? = null

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .enableAutoServiceReconnection()
        .build()

    init {
        startConnection()
    }

    override fun startConnection() {
        if (billingClient.isReady) {
            _isConnected.value = true
            queryProducts()
            return
        }

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("PlayBillingManager", "Billing client setup successfully.")
                    _isConnected.value = true
                    queryProducts()
                } else {
                    Log.e("PlayBillingManager", "Billing setup failed: ${billingResult.debugMessage}")
                    _isConnected.value = false
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w("PlayBillingManager", "Billing service disconnected.")
                _isConnected.value = false
            }
        })
    }

    private fun queryProducts() {
        val productList = InAppProduct.ALL_PRODUCT_IDS.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productDetailsList = queryProductDetailsResult.productDetailsList
                val map = productDetailsList.associateBy { it.productId }
                _productDetailsMap.value = map
                Log.d("PlayBillingManager", "Queried ${map.size} products.")
            } else {
                Log.e("PlayBillingManager", "Failed to query products: ${billingResult.debugMessage}")
            }
        }
    }

    override fun launchBillingFlow(
        activity: Activity,
        product: InAppProduct,
        onSuccess: (product: InAppProduct) -> Unit,
        onError: (message: String) -> Unit
    ) {
        pendingSuccessCallback = onSuccess
        pendingErrorCallback = onError
        pendingProduct = product

        val details = _productDetailsMap.value[product.id]
        if (details != null && billingClient.isReady) {
            val offerToken = details.oneTimePurchaseOfferDetailsList?.firstOrNull()?.offerToken
            val productDetailsParamsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
            if (offerToken != null) {
                productDetailsParamsBuilder.setOfferToken(offerToken)
            }
            val productDetailsParamsList = listOf(productDetailsParamsBuilder.build())

            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            val responseCode = billingClient.launchBillingFlow(activity, flowParams).responseCode
            if (responseCode != BillingClient.BillingResponseCode.OK) {
                onError("Failed to launch billing flow ($responseCode)")
            }
        } else {
            // Fallback for Debug / Local testing if BillingClient is not connected or products not configured in Play Console yet
            if (BuildConfig.DEBUG) {
                Log.d("PlayBillingManager", "Debug purchase fallback for product: ${product.id}")
                handleProductReward(product)
                onSuccess(product)
            } else {
                onError("Product details unavailable")
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            pendingErrorCallback?.invoke("Cancelled")
            clearCallbacks()
        } else {
            pendingErrorCallback?.invoke(billingResult.debugMessage.ifBlank { "Purchase failed" })
            clearCallbacks()
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.consumeAsync(consumeParams) { billingResult, _ ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val currentProduct = pendingProduct
                    if (currentProduct != null) {
                        handleProductReward(currentProduct)
                        pendingSuccessCallback?.invoke(currentProduct)
                    } else {
                        // Match product by ID if pendingProduct was cleared
                        val matchedProduct = (InAppProduct.GIL_PRODUCTS + InAppProduct.MAGICITE_PRODUCTS)
                            .find { product -> purchase.products.contains(product.id) }
                        if (matchedProduct != null) {
                            handleProductReward(matchedProduct)
                        }
                    }
                    clearCallbacks()
                } else {
                    pendingErrorCallback?.invoke("Failed to process purchase")
                    clearCallbacks()
                }
            }
        }
    }

    private fun handleProductReward(product: InAppProduct) {
        scope.launch {
            when (product.resourceType) {
                ResourceType.GIL -> repository.addGil(product.rewardAmount, bypassCap = true)
                ResourceType.MAGICITE -> repository.addMagicite(product.rewardAmount.toInt())
            }
        }
    }

    private fun clearCallbacks() {
        pendingSuccessCallback = null
        pendingErrorCallback = null
        pendingProduct = null
    }
}
