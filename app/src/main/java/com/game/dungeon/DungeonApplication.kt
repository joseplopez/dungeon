package com.game.dungeon

import android.app.Application
import com.game.dungeon.monetization.AdManager
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DungeonApplication : Application() {

    @Inject
    lateinit var adManager: AdManager

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {
            adManager.init()
        }
    }
}
