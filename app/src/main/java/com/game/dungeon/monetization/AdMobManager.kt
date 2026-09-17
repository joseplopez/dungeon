package com.game.dungeon.monetization

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.game.dungeon.BuildConfig
import com.game.dungeon.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdMobManager @Inject constructor() : AdManager {

    private var rewardedAd: RewardedAd? = null
    private var isAdLoading = false

    override fun init() {
        // Initialization is handled in DungeonApplication.
    }

    private fun loadRewardedAd(activity: Activity) {
        if (isAdLoading || rewardedAd != null) return
        
        isAdLoading = true
        Log.d("AdMobManager", "Loading Rewarded Ad: ${BuildConfig.REWARDED_AD_UNIT_ID}")
        
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            activity,
            BuildConfig.REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdMobManager", "Ad failed to load: ${adError.message}")
                    rewardedAd = null
                    isAdLoading = false
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("AdMobManager", "Ad loaded successfully")
                    rewardedAd = ad
                    isAdLoading = false
                }
            }
        )
    }

    override fun showRewardedAd(activity: Activity, onEarnedReward: () -> Unit) {
        showRewardedAd(activity, onEarnedReward, {})
    }

    override fun showRewardedAd(activity: Activity, onEarnedReward: () -> Unit, onAdClosed: () -> Unit) {
        val currentAd = rewardedAd
        if (currentAd != null) {
            currentAd.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("AdMobManager", "Ad dismissed full screen content.")
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    Log.e("AdMobManager", "Ad failed to show full screen content: ${adError.message}")
                    onAdClosed()
                }
            }
            currentAd.show(activity) { rewardItem ->
                Log.d("AdMobManager", "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onEarnedReward()
            }
            rewardedAd = null // Reset after showing
            loadRewardedAd(activity) // Pre-load next one
        } else {
            Toast.makeText(activity, activity.getString(R.string.ad_not_ready), Toast.LENGTH_SHORT).show()
            loadRewardedAd(activity)
            onAdClosed()
        }
    }
}
