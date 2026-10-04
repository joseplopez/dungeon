package com.game.dungeon.monetization

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.game.dungeon.BuildConfig
import com.game.dungeon.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdMobManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AdManager {

    private var rewardedAd: RewardedAd? = null
    private var isAdLoading = false
    private var retryAttempt = 0
    private val maxRetryAttempts = 5
    private val handler = Handler(Looper.getMainLooper())

    override fun init() {
        Log.d("AdMobManager", "Initializing AdMobManager and preloading Rewarded Ad")
        loadRewardedAd(context)
    }

    private fun loadRewardedAd(loadContext: Context = this.context) {
        if (isAdLoading || rewardedAd != null) {
            Log.d("AdMobManager", "Skipping ad load request. isAdLoading=$isAdLoading, rewardedAdLoaded=${rewardedAd != null}")
            return
        }

        isAdLoading = true
        Log.d("AdMobManager", "Loading Rewarded Ad (attempt ${retryAttempt + 1}): ${BuildConfig.REWARDED_AD_UNIT_ID}")

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            loadContext,
            BuildConfig.REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdMobManager", "Ad failed to load: ${adError.message} (code: ${adError.code})")
                    rewardedAd = null
                    isAdLoading = false

                    if (retryAttempt < maxRetryAttempts) {
                        val delayMillis = (1L shl retryAttempt) * 2000L // 2s, 4s, 8s, 16s, 32s
                        retryAttempt++
                        Log.d("AdMobManager", "Scheduling ad load retry #$retryAttempt in ${delayMillis}ms")
                        handler.postDelayed({
                            loadRewardedAd(loadContext)
                        }, delayMillis)
                    }
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("AdMobManager", "Ad loaded successfully")
                    rewardedAd = ad
                    isAdLoading = false
                    retryAttempt = 0
                }
            }
        )
    }

    override fun showRewardedAd(activity: Activity, onEarnedReward: () -> Unit) {
        showRewardedAd(activity, onEarnedReward) {}
    }

    override fun showRewardedAd(activity: Activity, onEarnedReward: () -> Unit, onAdClosed: () -> Unit) {
        val currentAd = rewardedAd
        if (currentAd != null) {
            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("AdMobManager", "Ad dismissed full screen content.")
                    rewardedAd = null
                    loadRewardedAd(activity)
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("AdMobManager", "Ad failed to show full screen content: ${adError.message}")
                    rewardedAd = null
                    loadRewardedAd(activity)
                    onAdClosed()
                }
            }
            currentAd.show(activity) { rewardItem ->
                Log.d("AdMobManager", "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onEarnedReward()
            }
            rewardedAd = null
            loadRewardedAd(activity)
        } else {
            Toast.makeText(activity, activity.getString(R.string.ad_not_ready), Toast.LENGTH_SHORT).show()
            retryAttempt = 0
            loadRewardedAd(activity)
            onAdClosed()
        }
    }
}
