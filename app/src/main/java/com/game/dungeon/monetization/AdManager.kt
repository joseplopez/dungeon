package com.game.dungeon.monetization

import android.app.Activity

interface AdManager {
    /**
     * Shows a rewarded ad to the user.
     * @param activity The current activity context.
     * @param onEarnedReward Callback when the user has finished watching the ad.
     */
    fun showRewardedAd(activity: Activity, onEarnedReward: () -> Unit)

    /**
     * Shows a rewarded ad to the user with a close callback.
     */
    fun showRewardedAd(activity: Activity, onEarnedReward: () -> Unit, onAdClosed: () -> Unit)

    /**
     * Initializes the Ad SDK.
     */
    fun init()
}
