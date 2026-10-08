package com.game.dungeon.engine

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import com.game.dungeon.data.models.*
import org.junit.Assert.*
import org.junit.Test

class GameplayProgressionTest {

    private class DummyContext : ContextWrapper(
        object : Context() {
            @Suppress("DEPRECATION")
            private val dummyResources = object : Resources(null, DisplayMetrics(), Configuration()) {
                override fun getText(id: Int): CharSequence = "TestString"
                override fun getText(id: Int, def: CharSequence?): CharSequence = "TestString"
                override fun getString(id: Int): String = "TestString"
                override fun getString(id: Int, vararg formatArgs: Any?): String = "TestStringFormatted"
                override fun getStringArray(id: Int): Array<String> = arrayOf("TestHero")
                override fun getQuantityString(id: Int, quantity: Int): String = "TestString"
                override fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any?): String = "TestString"
                override fun getQuantityText(id: Int, quantity: Int): CharSequence = "TestString"
                override fun getResourceName(resid: Int): String = "test_resource"
                override fun getResourceEntryName(resid: Int): String = "test_entry"
            }
            override fun getResources(): Resources = dummyResources
            override fun getAssets(): android.content.res.AssetManager = throw UnsupportedOperationException()
            override fun getPackageManager(): android.content.pm.PackageManager = throw UnsupportedOperationException()
            override fun getContentResolver(): android.content.ContentResolver = throw UnsupportedOperationException()
            override fun getMainLooper(): android.os.Looper = android.os.Looper.getMainLooper()
            override fun getApplicationContext(): Context = this
            override fun setTheme(resid: Int) {}
            override fun getTheme(): Resources.Theme = throw UnsupportedOperationException()
            override fun getClassLoader(): ClassLoader = javaClass.classLoader
            override fun getPackageName(): String = "com.game.dungeon"
            override fun getApplicationInfo(): android.content.pm.ApplicationInfo = android.content.pm.ApplicationInfo()
            override fun getPackageResourcePath(): String = ""
            override fun getPackageCodePath(): String = ""
            override fun getSharedPreferences(name: String?, mode: Int): android.content.SharedPreferences = throw UnsupportedOperationException()
            override fun moveSharedPreferencesFrom(from: Context?, name: String?): Boolean = false
            override fun deleteSharedPreferences(name: String?): Boolean = false
            override fun openFileInput(name: String?): java.io.FileInputStream = throw UnsupportedOperationException()
            override fun openFileOutput(name: String?, mode: Int): java.io.FileOutputStream = throw UnsupportedOperationException()
            override fun deleteFile(name: String?): Boolean = false
            override fun getFileStreamPath(name: String?): java.io.File = throw UnsupportedOperationException()
            override fun getDataDir(): java.io.File = throw UnsupportedOperationException()
            override fun getFilesDir(): java.io.File = throw UnsupportedOperationException()
            override fun getNoBackupFilesDir(): java.io.File = throw UnsupportedOperationException()
            override fun getExternalFilesDir(type: String?): java.io.File? = null
            override fun getExternalFilesDirs(type: String?): Array<java.io.File> = emptyArray()
            override fun getObbDir(): java.io.File = throw UnsupportedOperationException()
            override fun getObbDirs(): Array<java.io.File> = emptyArray()
            override fun getCacheDir(): java.io.File = throw UnsupportedOperationException()
            override fun getCodeCacheDir(): java.io.File = throw UnsupportedOperationException()
            override fun getExternalCacheDir(): java.io.File? = null
            override fun getExternalCacheDirs(): Array<java.io.File> = emptyArray()
            @Suppress("DEPRECATION")
            override fun getExternalMediaDirs(): Array<java.io.File> = emptyArray()
            override fun fileList(): Array<String> = emptyArray()
            override fun getDir(name: String?, mode: Int): java.io.File = throw UnsupportedOperationException()
            override fun openOrCreateDatabase(name: String?, mode: Int, factory: android.database.sqlite.SQLiteDatabase.CursorFactory?): android.database.sqlite.SQLiteDatabase = throw UnsupportedOperationException()
            override fun openOrCreateDatabase(name: String?, mode: Int, factory: android.database.sqlite.SQLiteDatabase.CursorFactory?, errorHandler: android.database.DatabaseErrorHandler?): android.database.sqlite.SQLiteDatabase = throw UnsupportedOperationException()
            override fun moveDatabaseFrom(from: Context?, name: String?): Boolean = false
            override fun deleteDatabase(name: String?): Boolean = false
            override fun getDatabasePath(name: String?): java.io.File = throw UnsupportedOperationException()
            override fun databaseList(): Array<String> = emptyArray()
            override fun getWallpaper(): android.graphics.drawable.Drawable = throw UnsupportedOperationException()
            override fun peekWallpaper(): android.graphics.drawable.Drawable = throw UnsupportedOperationException()
            override fun getWallpaperDesiredMinimumWidth(): Int = 0
            override fun getWallpaperDesiredMinimumHeight(): Int = 0
            override fun setWallpaper(bitmap: android.graphics.Bitmap?) {}
            override fun setWallpaper(snapshot: java.io.InputStream?) {}
            override fun clearWallpaper() {}
            override fun startActivity(intent: android.content.Intent?) {}
            override fun startActivity(intent: android.content.Intent?, options: android.os.Bundle?) {}
            override fun startActivities(intents: Array<out android.content.Intent>?) {}
            override fun startActivities(intents: Array<out android.content.Intent>?, options: android.os.Bundle?) {}
            override fun startIntentSender(intent: android.content.IntentSender?, fillInIntent: android.content.Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int) {}
            override fun startIntentSender(intent: android.content.IntentSender?, fillInIntent: android.content.Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int, options: android.os.Bundle?) {}
            override fun sendBroadcast(intent: android.content.Intent?) {}
            override fun sendBroadcast(intent: android.content.Intent?, receiverPermission: String?) {}
            override fun sendOrderedBroadcast(intent: android.content.Intent?, receiverPermission: String?) {}
            override fun sendOrderedBroadcast(intent: android.content.Intent, receiverPermission: String?, resultReceiver: android.content.BroadcastReceiver?, scheduler: android.os.Handler?, initialCode: Int, initialData: String?, initialExtras: android.os.Bundle?) {}
            override fun sendBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?) {}
            override fun sendBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?, receiverPermission: String?) {}
            override fun sendOrderedBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?, receiverPermission: String?, resultReceiver: android.content.BroadcastReceiver?, scheduler: android.os.Handler?, initialCode: Int, initialData: String?, initialExtras: android.os.Bundle?) {}
            override fun removeStickyBroadcast(intent: android.content.Intent?) {}
            override fun removeStickyBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?) {}
            override fun sendStickyBroadcast(intent: android.content.Intent?) {}
            override fun sendStickyBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?) {}
            override fun sendStickyOrderedBroadcast(intent: android.content.Intent?, resultReceiver: android.content.BroadcastReceiver?, scheduler: android.os.Handler?, initialCode: Int, initialData: String?, initialExtras: android.os.Bundle?) {}
            override fun sendStickyOrderedBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?, resultReceiver: android.content.BroadcastReceiver?, scheduler: android.os.Handler?, initialCode: Int, initialData: String?, initialExtras: android.os.Bundle?) {}
            override fun startInstrumentation(className: android.content.ComponentName, profileFile: String?, arguments: android.os.Bundle?): Boolean = false
            override fun registerReceiver(receiver: android.content.BroadcastReceiver?, filter: android.content.IntentFilter?): android.content.Intent? = null
            override fun registerReceiver(receiver: android.content.BroadcastReceiver?, filter: android.content.IntentFilter?, flags: Int): android.content.Intent? = null
            override fun registerReceiver(receiver: android.content.BroadcastReceiver?, filter: android.content.IntentFilter?, broadcastPermission: String?, scheduler: android.os.Handler?): android.content.Intent? = null
            override fun registerReceiver(receiver: android.content.BroadcastReceiver?, filter: android.content.IntentFilter?, broadcastPermission: String?, scheduler: android.os.Handler?, flags: Int): android.content.Intent? = null
            override fun unregisterReceiver(receiver: android.content.BroadcastReceiver?) {}
            override fun startService(service: android.content.Intent?): android.content.ComponentName? = null
            override fun startForegroundService(service: android.content.Intent?): android.content.ComponentName? = null
            override fun stopService(service: android.content.Intent?): Boolean = false
            override fun bindService(service: android.content.Intent, conn: android.content.ServiceConnection, flags: Int): Boolean = false
            override fun unbindService(conn: android.content.ServiceConnection) {}
            override fun getSystemService(name: String): Any? = null
            override fun getSystemServiceName(serviceClass: Class<*>): String? = null
            override fun checkPermission(permission: String, pid: Int, uid: Int): Int = 0
            override fun checkCallingPermission(permission: String): Int = 0
            override fun checkCallingOrSelfPermission(permission: String): Int = 0
            override fun checkSelfPermission(permission: String): Int = 0
            override fun enforcePermission(permission: String, pid: Int, uid: Int, message: String?) {}
            override fun enforceCallingPermission(permission: String, message: String?) {}
            override fun enforceCallingOrSelfPermission(permission: String, message: String?) {}
            override fun grantUriPermission(toPackage: String?, uri: android.net.Uri?, modeFlags: Int) {}
            override fun revokeUriPermission(uri: android.net.Uri?, modeFlags: Int) {}
            override fun revokeUriPermission(toPackage: String?, uri: android.net.Uri?, modeFlags: Int) {}
            override fun checkUriPermission(uri: android.net.Uri?, pid: Int, uid: Int, modeFlags: Int): Int = 0
            override fun checkCallingUriPermission(uri: android.net.Uri?, modeFlags: Int): Int = 0
            override fun checkCallingOrSelfUriPermission(uri: android.net.Uri?, modeFlags: Int): Int = 0
            override fun checkUriPermission(uri: android.net.Uri?, readPermission: String?, writePermission: String?, pid: Int, uid: Int, modeFlags: Int): Int = 0
            override fun enforceUriPermission(uri: android.net.Uri?, pid: Int, uid: Int, modeFlags: Int, message: String?) {}
            override fun enforceCallingUriPermission(uri: android.net.Uri?, modeFlags: Int, message: String?) {}
            override fun enforceCallingOrSelfUriPermission(uri: android.net.Uri?, modeFlags: Int, message: String?) {}
            override fun enforceUriPermission(uri: android.net.Uri?, readPermission: String?, writePermission: String?, pid: Int, uid: Int, modeFlags: Int, message: String?) {}
            override fun createPackageContext(packageName: String?, flags: Int): Context = this
            override fun createContextForSplit(splitName: String?): Context = this
            override fun createConfigurationContext(overrideConfiguration: Configuration): Context = this
            override fun createDisplayContext(display: android.view.Display): Context = this
            override fun createDeviceProtectedStorageContext(): Context = this
            override fun isDeviceProtectedStorage(): Boolean = false
        }
    )

    private val mockContext: Context = DummyContext()

    @Test
    fun testNoFloorSkips_EnemyTemplatesCoverAllFloors() {
        for (dimNum in 1..3) {
            val dimension = FFDimensionData.getDimension(dimNum)
            val maxFloor = dimNum * 100
            for (floor in 1..maxFloor) {
                val enemies = FFDimensionData.getEnemiesForFloor(dimension, floor)
                assertTrue("Floor $floor in Dimension $dimNum must have at least 1 enemy template", enemies.isNotEmpty())
            }
        }
    }

    @Test
    fun testDimensionMaxFloors() {
        assertEquals(100, FFDimensionData.getDimension(1).number * 100)
        assertEquals(200, FFDimensionData.getDimension(2).number * 100)
        assertEquals(300, FFDimensionData.getDimension(3).number * 100)
        assertEquals(400, FFDimensionData.getDimension(4).number * 100)
    }

    @Test
    fun testBossForMaxFloor() {
        for (dimNum in 1..3) {
            val dimension = FFDimensionData.getDimension(dimNum)
            val maxFloor = dimNum * 100
            val boss = FFDimensionData.getBossForFloor(dimension, maxFloor)
            assertNotNull("Dimension $dimNum max floor $maxFloor must have a boss", boss)
            assertTrue("Boss at max floor $maxFloor must be marked as boss", boss!!.isBoss)
        }
    }

    @Test
    fun testDimension11_InfiniteVoidPropertiesAndBossPacing() {
        val dim11 = FFDimensionData.getDimension(11)
        assertEquals(11, dim11.number)
        assertEquals(99999999, dim11.maxFloor)
        assertNotEquals(0, dim11.titleRes)
        assertNotEquals(0, dim11.subtitleRes)

        // Test regular enemies for high floors
        val regularEnemiesHighFloor = FFDimensionData.getEnemiesForFloor(dim11, 5000)
        assertTrue("Dimension 11 floor 5000 must have regular enemies", regularEnemiesHighFloor.isNotEmpty())

        // Test mini-boss on floor 50
        val miniBoss50 = FFDimensionData.getBossForFloor(dim11, 50)
        assertNotNull("Floor 50 in Dim 11 must have a mini-boss", miniBoss50)
        assertTrue("Mini-boss must be marked as boss", miniBoss50!!.isBoss)
        assertFalse("Floor 50 should be mini-boss, not major final boss", FFDimensionData.isMajorBoss(miniBoss50.type))

        // Test major boss on floor 100
        val majorBoss100 = FFDimensionData.getBossForFloor(dim11, 100)
        assertNotNull("Floor 100 in Dim 11 must have a major boss", majorBoss100)
        assertTrue("Major boss must be marked as boss", majorBoss100!!.isBoss)
        assertTrue("Floor 100 should be a major final boss", FFDimensionData.isMajorBoss(majorBoss100.type))

        // Test major boss on floor 200
        val majorBoss200 = FFDimensionData.getBossForFloor(dim11, 200)
        assertNotNull("Floor 200 in Dim 11 must have a major boss", majorBoss200)
        assertTrue("Floor 200 should be a major final boss", FFDimensionData.isMajorBoss(majorBoss200!!.type))

        // Test dynamic biome cycling
        val biome1 = dim11.getBiomeForFloor(5)
        val biome2 = dim11.getBiomeForFloor(15)
        assertNotEquals("Biomes should cycle every 10 floors", biome1.backgroundType, biome2.backgroundType)

        // Test high floor Enemy stat calculation safety
        val highFloorEnemy = Enemy.fromTemplate(regularEnemiesHighFloor.first(), floor = 99999999, context = mockContext, dimension = dim11)
        assertTrue("HP must be positive on floor 99999999", highFloorEnemy.maxHp > 0)
        assertTrue("Attack must be positive on floor 99999999", highFloorEnemy.attack > 0)
        assertTrue("Gil reward must be positive on floor 99999999", highFloorEnemy.gilReward > 0)
    }

    @Test
    fun testEnemyStatAndRewardScaling() {
        val template = FFEnemyTemplate(
            nameRes = 1,
            emoji = "👺",
            minFloor = 1,
            maxFloor = 300,
            gilReward = 10
        )

        val enemyFloor10 = Enemy.fromTemplate(template, floor = 10, context = mockContext)
        val enemyFloor100 = Enemy.fromTemplate(template, floor = 100, context = mockContext)
        val enemyFloor200 = Enemy.fromTemplate(template, floor = 200, context = mockContext)

        // Difficulty / Stats scaling
        assertTrue("Floor 100 HP must be greater than Floor 10 HP", enemyFloor100.maxHp > enemyFloor10.maxHp)
        assertTrue("Floor 200 HP must be greater than Floor 100 HP", enemyFloor200.maxHp > enemyFloor100.maxHp)
        assertTrue("Floor 100 ATK must be greater than Floor 10 ATK", enemyFloor100.attack > enemyFloor10.attack)

        // Gil Reward scaling
        assertTrue("Floor 100 Gil reward must be greater than Floor 10 Gil reward", enemyFloor100.gilDropped > enemyFloor10.gilDropped)
        assertTrue("Floor 200 Gil reward must be greater than Floor 100 Gil reward", enemyFloor200.gilDropped > enemyFloor100.gilDropped)
    }

    @Test
    fun testRebalancedGilAndMagiciteScaling() {
        val regularTemplate = FFEnemyTemplate(
            nameRes = 1,
            emoji = "👺",
            minFloor = 1,
            maxFloor = 1000,
            gilReward = 100,
            magiciteChance = 0.05f
        )
        val bossTemplate = FFEnemyTemplate(
            nameRes = 1,
            emoji = "🐉",
            minFloor = 1,
            maxFloor = 1000,
            gilReward = 1000,
            isBoss = true
        )

        val regularFloor1 = Enemy.fromTemplate(regularTemplate, floor = 1, context = mockContext)
        val regularFloor10 = Enemy.fromTemplate(regularTemplate, floor = 10, context = mockContext)
        val regularFloor100 = Enemy.fromTemplate(regularTemplate, floor = 100, context = mockContext)
        val regularFloor500 = Enemy.fromTemplate(regularTemplate, floor = 500, context = mockContext)

        // Monotonic Gil progression
        assertTrue(regularFloor10.gilDropped > regularFloor1.gilDropped)
        assertTrue(regularFloor100.gilDropped > regularFloor10.gilDropped)
        assertTrue(regularFloor500.gilDropped > regularFloor100.gilDropped)

        // Verify Gil scaling remains balanced at floor 100 (sub-linear multiplier ~4.5x, gil ~450)
        assertTrue("Floor 100 Gil reward should be balanced (< 800 for 100 base)", regularFloor100.gilDropped < 800)
        // Verify Gil scaling remains balanced at floor 500 (sub-linear multiplier ~14.35x, gil ~1435)
        assertTrue("Floor 500 Gil reward should be balanced (< 2500 for 100 base)", regularFloor500.gilDropped < 2500)

        // Boss magicite tests
        val bossFloor1 = Enemy.fromTemplate(bossTemplate, floor = 1, context = mockContext)
        val bossFloor100 = Enemy.fromTemplate(bossTemplate, floor = 100, context = mockContext)
        val bossFloor500 = Enemy.fromTemplate(bossTemplate, floor = 500, context = mockContext)

        assertTrue("Boss Floor 1 Magicite should be >= 3", bossFloor1.magiciteDropped >= 3)
        assertTrue("Boss Floor 100 Magicite should be around 10", bossFloor100.magiciteDropped in 8..12)
        assertTrue("Boss Floor 500 Magicite should be capped at 20", bossFloor500.magiciteDropped <= 20)

        // Regular monster magicite quantity check (never > 2 in Dimension 1)
        for (f in listOf(1, 10, 50, 100, 200, 500, 1000)) {
            val reg = Enemy.fromTemplate(regularTemplate, floor = f, context = mockContext)
            assertTrue("Regular enemy magicite drop quantity on floor $f must be <= 2", reg.magiciteDropped <= 2)
        }
    }

    @Test
    fun testMagiciteScalingAcrossDimensions() {
        val bossTemplate = FFEnemyTemplate(
            nameRes = 1, emoji = "🐉", minFloor = 1, maxFloor = 1000,
            gilReward = 1000, isBoss = true
        )
        val regTemplate = FFEnemyTemplate(
            nameRes = 1, emoji = "👺", minFloor = 1, maxFloor = 1000,
            gilReward = 100, magiciteChance = 1.0f
        )

        val dim1 = FFDimensionData.getDimension(1)
        val dim2 = FFDimensionData.getDimension(2)
        val dim5 = FFDimensionData.getDimension(5)
        val dim10 = FFDimensionData.getDimension(10)

        // Boss magicite in higher dimensions
        val bossDim1 = Enemy.fromTemplate(bossTemplate, floor = 100, context = mockContext, dimension = dim1)
        val bossDim2 = Enemy.fromTemplate(bossTemplate, floor = 100, context = mockContext, dimension = dim2)
        val bossDim5 = Enemy.fromTemplate(bossTemplate, floor = 100, context = mockContext, dimension = dim5)
        val bossDim10 = Enemy.fromTemplate(bossTemplate, floor = 100, context = mockContext, dimension = dim10)

        assertTrue("Dim 2 Boss magicite should be greater than Dim 1", bossDim2.magiciteDropped > bossDim1.magiciteDropped)
        assertTrue("Dim 5 Boss magicite should be greater than Dim 2", bossDim5.magiciteDropped > bossDim2.magiciteDropped)
        assertTrue("Dim 10 Boss magicite should be greater than Dim 5", bossDim10.magiciteDropped > bossDim5.magiciteDropped)

        // Verify late dimension boss yields significant magicite
        val boss1000Dim10 = Enemy.fromTemplate(bossTemplate, floor = 1000, context = mockContext, dimension = dim10)
        assertTrue("Dim 10 floor 1000 boss should yield >= 100 magicite", boss1000Dim10.magiciteDropped >= 100)

        // Regular monster magicite quantity in late dimensions (over multiple rolls due to drop chance)
        val dropsDim1 = List(100) { Enemy.fromTemplate(regTemplate, floor = 50, context = mockContext, dimension = dim1).magiciteDropped }
        val dropsDim10 = List(100) { Enemy.fromTemplate(regTemplate, floor = 50, context = mockContext, dimension = dim10).magiciteDropped }
        assertTrue("Dim 10 regular enemies should drop more magicite on average than Dim 1", dropsDim10.sum() > dropsDim1.sum())
    }

    @Test
    fun testEnemyAndBossDimensionStatScaling() {
        val regTemplate = FFEnemyTemplate(
            nameRes = 1,
            emoji = "👺",
            minFloor = 1,
            maxFloor = 500,
            gilReward = 50
        )
        val bossTemplate = FFEnemyTemplate(
            nameRes = 1,
            emoji = "🐉",
            minFloor = 1,
            maxFloor = 500,
            gilReward = 500,
            isBoss = true
        )

        val dim1 = FFDimensionData.getDimension(1)
        val dim2 = FFDimensionData.getDimension(2)
        val dim3 = FFDimensionData.getDimension(3)

        val regDim1 = Enemy.fromTemplate(regTemplate, floor = 50, context = mockContext, dimension = dim1)
        val regDim2 = Enemy.fromTemplate(regTemplate, floor = 50, context = mockContext, dimension = dim2)
        val regDim3 = Enemy.fromTemplate(regTemplate, floor = 50, context = mockContext, dimension = dim3)

        // Dimension 2 regular enemy should have ~30% higher stats than Dimension 1
        assertTrue("Dim 2 HP must be > Dim 1 HP", regDim2.maxHp > regDim1.maxHp)
        assertTrue("Dim 3 HP must be > Dim 2 HP", regDim3.maxHp > regDim2.maxHp)
        assertTrue("Dim 2 ATK must be > Dim 1 ATK", regDim2.attack > regDim1.attack)
        assertTrue("Dim 3 ATK must be > Dim 2 ATK", regDim3.attack > regDim2.attack)

        // Boss dimension scaling checks
        val bossDim1 = Enemy.fromTemplate(bossTemplate, floor = 100, context = mockContext, dimension = dim1)
        val bossDim2 = Enemy.fromTemplate(bossTemplate, floor = 100, context = mockContext, dimension = dim2)
        val bossDim3 = Enemy.fromTemplate(bossTemplate, floor = 100, context = mockContext, dimension = dim3)

        assertTrue("Dim 2 Boss HP must be > Dim 1 Boss HP", bossDim2.maxHp > bossDim1.maxHp)
        assertTrue("Dim 3 Boss HP must be > Dim 2 Boss HP", bossDim3.maxHp > bossDim2.maxHp)
        assertTrue("Dim 2 Boss ATK must be > Dim 1 Boss ATK", bossDim2.attack > bossDim1.attack)

        // Verify extra boss scaling in higher dimensions
        val regHpRatioDim2 = regDim2.maxHp.toFloat() / regDim1.maxHp
        val bossHpRatioDim2 = bossDim2.maxHp.toFloat() / bossDim1.maxHp
        assertTrue("Boss dimension scaling ratio should be higher than regular enemy dimension scaling ratio", bossHpRatioDim2 > regHpRatioDim2)
    }

    @Test
    fun testRebalancedItemStatScalingAcrossDimensions() {
        val itemDim1 = Item.random(floor = 100, context = mockContext, minRarity = Rarity.LEGENDARY, dimension = 1)
        
        // Verify stats on high floors stay within reasonable non-inflated bounds
        // Legendary item on floor 100 in Dim 1 should have HP < 400 (previously was > 1,400)
        assertTrue("Floor 100 Legendary item HP should be balanced (< 400)", itemDim1.hpBonus < 400)

        // Verify deterministic stat comparison across dimensions for same slot & rarity
        val bonusMult = 4.0f // Legendary
        val atkDim1 = ((2 + 100 / 8) * bonusMult * (1f + 0 * 0.12f)).toInt() // 14 * 4 = 56
        val atkDim3 = ((2 + 100 / 8) * bonusMult * (1f + 2 * 0.12f)).toInt() // 14 * 4 * 1.24 = 69
        val atkDim5 = ((2 + 100 / 8) * bonusMult * (1f + 4 * 0.12f)).toInt() // 14 * 4 * 1.48 = 82

        assertTrue("Higher dimension weapon ATK should scale deterministically", atkDim3 > atkDim1)
        assertTrue("Higher dimension weapon ATK should scale deterministically", atkDim5 > atkDim3)

        // Verify weapon attack bounds
        assertTrue("Weapon ATK in Dim 1 on Floor 100 should be around 56", atkDim1 in 50..65)
        assertTrue("Weapon ATK in Dim 5 on Floor 100 should be around 82", atkDim5 in 75..95)

        // Verify defense bounds
        val defDim1 = ((2 + 100 / 6) * bonusMult * 1.0f).toInt() // 18 * 4 = 72
        val defDim5 = ((2 + 100 / 6) * bonusMult * (1f + 4 * 0.12f)).toInt() // 18 * 4 * 1.48 = 106
        assertTrue("Armor DEF in Dim 1 on Floor 100 should be around 72", defDim1 in 60..80)
        assertTrue("Armor DEF in Dim 5 on Floor 100 should be around 106", defDim5 in 95..120)
    }

    @Test
    fun testItemCategoriesAndStatScalingByDimension() {
        val itemsDim1 = List(30) { Item.random(floor = 50, context = mockContext, minRarity = Rarity.LEGENDARY, dimension = 1) }
        val itemsDim3 = List(30) { Item.random(floor = 50, context = mockContext, minRarity = Rarity.LEGENDARY, dimension = 3) }

        val avgPowerDim1 = itemsDim1.map { it.powerScore }.average()
        val avgPowerDim3 = itemsDim3.map { it.powerScore }.average()

        assertTrue("Item from higher dimension should on average have higher power score for same rarity", avgPowerDim3 > avgPowerDim1)
    }

    @Test
    fun testMagicalWeaponsAndTenDimensionsCategoryGeneration() {
        for (dim in 1..10) {
            val items = List(50) { Item.random(floor = 50, context = mockContext, minRarity = Rarity.RARE, dimension = dim) }
            assertTrue("Items generated for Dimension $dim should not be empty", items.isNotEmpty())
        }

        var foundMagicalWeapon = false
        var foundPhysicalWeapon = false
        repeat(200) {
            val item = Item.random(floor = 50, context = mockContext, dimension = 1)
            if (item.slot == ItemSlot.WEAPON) {
                if (item.magicBonus > 0 && item.attackBonus == 0) {
                    foundMagicalWeapon = true
                }
                if (item.attackBonus > 0 && item.magicBonus == 0) {
                    foundPhysicalWeapon = true
                }
            }
        }
        assertTrue("Should be able to generate magical weapons with magicBonus", foundMagicalWeapon)
        assertTrue("Should be able to generate physical weapons with attackBonus", foundPhysicalWeapon)
    }

    @Test
    fun testDamageMitigationNever100PercentAndHasDiminishingReturns() {
        val floor = 100
        val k = 150f + floor * 0.8f

        val def100 = 100f
        val def500 = 500f
        val def10000 = 10000f

        val mit100 = (def100 / (def100 + k)).coerceAtMost(0.85f)
        val mit500 = (def500 / (def500 + k)).coerceAtMost(0.85f)
        val mit10000 = (def10000 / (def10000 + k)).coerceAtMost(0.85f)

        // Mitigation never reaches or exceeds 0.85 (85%)
        assertTrue("Mitigation must never exceed 0.85", mit10000 <= 0.85f)
        assertEquals(0.85f, mit10000, 0.001f)

        // Diminishing returns: Gain per point of defense from 0->100 is higher than from 100->500
        val gainPerDefLow = mit100 / 100f
        val gainPerDefHigh = (mit500 - mit100) / 400f
        assertTrue("Higher defense must have lower marginal mitigation gain per point", gainPerDefLow > gainPerDefHigh)
    }

    @Test
    fun testCritChanceAndPercentageStatsDiminishingReturns() {
        val hero = Hero(
            heroClass = HeroClass.WARRIOR,
            name = "Warrior",
            currentHp = 100,
            currentMp = 50,
            aiPriority = AIPriority.ATTACK
        )

        // Test CRIT_CHANCE with high raw values
        val dummyRelicBonusesHigh = RelicBonuses.from(
            GameState(critChanceRelic = 500, doubleLootRelic = 100, hpRelic = 10, defenseRelic = 10)
        )
        val stats = hero.calculateStats(emptyList(), dummyRelicBonusesHigh)
        val critChance = stats["CRIT_CHANCE"] ?: 0

        assertTrue("Crit chance must be capped at max 85%", critChance <= 85)
        assertEquals(85, critChance)

        // Test Double Loot Chance diminishing returns & max cap (75%)
        val doubleLootChanceEffective = dummyRelicBonusesHigh.effectiveDoubleLootChance
        assertTrue("Double loot chance must be capped at max 75%", doubleLootChanceEffective <= 75f)

        // Test low vs high incremental gain for double loot
        val relicLow = RelicBonuses.from(GameState(doubleLootRelic = 2)) // raw = 10
        val relicMid = RelicBonuses.from(GameState(doubleLootRelic = 10)) // raw = 50
        val gainLow = relicLow.effectiveDoubleLootChance / 10f
        val gainHigh = (relicMid.effectiveDoubleLootChance - relicLow.effectiveDoubleLootChance) / 40f
        assertTrue("Double loot chance must demonstrate diminishing returns", gainLow > gainHigh)
    }

    @Test
    fun testRelicHpAndDefenseScaling() {
        val gs0 = GameState(hpRelic = 0, defenseRelic = 0)
        val gs10 = GameState(hpRelic = 10, defenseRelic = 10)
        val gs50 = GameState(hpRelic = 50, defenseRelic = 50)

        val relics0 = RelicBonuses.from(gs0)
        val relics10 = RelicBonuses.from(gs10)
        val relics50 = RelicBonuses.from(gs50)

        assertEquals(0, relics0.hpBonus)
        assertEquals(0, relics0.defenseBonus)

        // Level 10: hpBonus = 10 * 60 + 10^2 * 2 = 600 + 200 = 800
        assertEquals(800, relics10.hpBonus)
        // Level 10: defenseBonus = 10 * 8 + 10^2 = 80 + 100 = 180
        assertEquals(180, relics10.defenseBonus)

        // Level 50: hpBonus = 50 * 60 + 50^2 * 2 = 3000 + 5000 = 8000
        assertEquals(8000, relics50.hpBonus)
        // Level 50: defenseBonus = 50 * 8 + 50^2 = 400 + 2500 = 2900
        assertEquals(2900, relics50.defenseBonus)

        assertTrue("Higher relic levels must provide significantly increased HP", relics50.hpBonus > relics10.hpBonus)
        assertTrue("Higher relic levels must provide significantly increased DEF", relics50.defenseBonus > relics10.defenseBonus)
    }

    @Test
    fun testRehireLastPartyCostAndEmptyState() {
        // Empty last party state
        val gsEmpty = GameState(lastPartyClasses = emptyList())
        assertTrue("Empty last party should produce empty missing classes", gsEmpty.lastPartyClasses.isEmpty())

        // Saved last party with WARRIOR and BLACK_MAGE
        val lastParty = listOf(HeroClass.WARRIOR, HeroClass.BLACK_MAGE)
        val gsSaved = GameState(lastPartyClasses = lastParty)
        assertEquals(2, gsSaved.lastPartyClasses.size)

        // Expected cost when neither is currently hired
        val expectedCost = HeroClass.WARRIOR.hireCost + HeroClass.BLACK_MAGE.hireCost
        val currentHired = emptyList<HeroClass>()
        
        val missing = lastParty.filter { !currentHired.contains(it) }
        val calculatedCost = missing.sumOf { it.hireCost.toLong() }
        assertEquals(expectedCost.toLong(), calculatedCost)

        // Partial team hired: WARRIOR already hired
        val currentHiredClasses = mutableListOf(HeroClass.WARRIOR)
        val missingPartial = mutableListOf<HeroClass>()
        for (job in lastParty) {
            if (currentHiredClasses.contains(job)) {
                currentHiredClasses.remove(job)
            } else {
                missingPartial.add(job)
            }
        }
        assertEquals(1, missingPartial.size)
        assertEquals(HeroClass.BLACK_MAGE, missingPartial.first())
        assertEquals(HeroClass.BLACK_MAGE.hireCost.toLong(), missingPartial.sumOf { it.hireCost.toLong() })
    }

    @Test
    fun testQuickEquipDistributionLogic() {
        val hero1 = Hero(id = "h1", heroClass = HeroClass.WARRIOR, name = "Hero 1", currentHp = 100, currentMp = 10, aiPriority = AIPriority.ATTACK, partyPosition = 0)
        val hero2 = Hero(id = "h2", heroClass = HeroClass.BLACK_MAGE, name = "Hero 2", currentHp = 100, currentMp = 10, aiPriority = AIPriority.ATTACK, partyPosition = 1)
        val heroes = listOf(hero1, hero2)

        val w1 = Item(id = "w1", name = "Iron Sword", slot = ItemSlot.WEAPON, rarity = Rarity.COMMON, attackBonus = 10, emoji = "🗡️", floorFound = 1)
        val w2 = Item(id = "w2", name = "Excalibur", slot = ItemSlot.WEAPON, rarity = Rarity.RARE, attackBonus = 50, emoji = "🗡️", floorFound = 1)
        val w3 = Item(id = "w3", name = "Bronze Sword", slot = ItemSlot.WEAPON, rarity = Rarity.COMMON, attackBonus = 5, emoji = "🗡️", floorFound = 1)

        val pool = mutableListOf(w1, w2, w3)

        val updatedItems = mutableListOf<Item>()
        val sortedHeroes = heroes.sortedBy { it.partyPosition }

        sortedHeroes.forEach { hero ->
            val bestWeapon = pool.filter { it.slot == ItemSlot.WEAPON }.maxByOrNull { it.powerScore }
            if (bestWeapon != null) {
                pool.remove(bestWeapon)
                updatedItems.add(bestWeapon.copy(ownerId = hero.id))
            }
        }

        val hero1Equipped = updatedItems.find { it.ownerId == hero1.id }
        assertNotNull(hero1Equipped)
        assertEquals("w2", hero1Equipped!!.id)

        val hero2Equipped = updatedItems.find { it.ownerId == hero2.id }
        assertNotNull(hero2Equipped)
        assertEquals("w1", hero2Equipped!!.id)

        assertTrue(pool.contains(w3))
    }

    @Test
    fun testHeroCopyPreservesBonusStatsAndCombatDamage() {
        val hero = Hero(
            id = "test_vivi",
            heroClass = HeroClass.BLACK_MAGE,
            name = "Vivi",
            currentHp = 5614,
            currentMp = 441,
            level = 1,
            aiPriority = AIPriority.ATTACK
        ).apply {
            attackBonus = 137
            defenseBonus = 4822
            hpBonus = 5529
            magicBonus = 122
            mpBonus = 351
            critChance = 42
            critDamage = 50
        }

        val copiedHero = hero.copy(currentHp = 5000)

        // Verify bonus stats are preserved on copy
        assertEquals(137, copiedHero.attackBonus)
        assertEquals(4822, copiedHero.defenseBonus)
        assertEquals(5529, copiedHero.hpBonus)
        assertEquals(122, copiedHero.magicBonus)
        assertEquals(351, copiedHero.mpBonus)
        assertEquals(42, copiedHero.critChance)
        assertEquals(50, copiedHero.critDamage)

        // Verify total stats match expected base + bonus
        assertEquals(hero.baseAttack + 137, copiedHero.attack)
        assertEquals(hero.baseDefense + 4822, copiedHero.defense)
        assertEquals(hero.baseMaxHp + 5529, copiedHero.maxHp)

        // Verify hero does not die on taking small damage when HP is high
        copiedHero.currentHp -= 113
        assertTrue("Hero with high hpBonus must remain alive after taking 113 damage", copiedHero.isAlive)
        assertEquals(4887, copiedHero.currentHp)
    }

    @Test
    fun testLategameBossStatsAreBalancedAndNotOverpowered() {
        val dim10 = FFDimensionData.getDimension(10)
        val boss1000 = FFDimensionData.getBossForFloor(dim10, 1000)
        assertNotNull("Dimension 10 floor 1000 boss must exist", boss1000)

        val sinBoss = Enemy.fromTemplate(boss1000!!, floor = 1000, context = mockContext, dimension = dim10)

        // Sin at floor 1000 in Dimension 10 should be challenging but not have 150k+ ATK and 3.6M+ HP
        assertTrue("Dim 10 max boss ATK must be balanced (< 40,000)", sinBoss.attack < 40000)
        assertTrue("Dim 10 max boss HP must be balanced (< 1,000,000)", sinBoss.maxHp < 1000000)

        val dim5 = FFDimensionData.getDimension(5)
        val boss500 = FFDimensionData.getBossForFloor(dim5, 500)
        assertNotNull("Dimension 5 floor 500 boss must exist", boss500)

        val neoExdeath = Enemy.fromTemplate(boss500!!, floor = 500, context = mockContext, dimension = dim5)

        assertTrue("Dim 5 max boss ATK must be balanced (< 15,000)", neoExdeath.attack < 15000)
        assertTrue("Dim 5 max boss HP must be balanced (< 250,000)", neoExdeath.maxHp < 250000)
    }

    @Test
    fun testVaultAtMaxLevelHasNoGilCap() {
        val gsNormal = GameState(vaultLevel = 5)
        assertTrue("Sub-max vault must have finite gil cap", gsNormal.maxGil < Long.MAX_VALUE)

        val gsMaxVault = GameState(vaultLevel = UpgradeType.VAULT.maxLevel)
        assertEquals("Max level vault must have no gil cap (Long.MAX_VALUE)", Long.MAX_VALUE, gsMaxVault.maxGil)
    }

    @Test
    fun testFormatAmountUsesAtMostOneDecimal() {
        assertEquals("15.1K", com.game.dungeon.ui.components.formatAmount(15079L))
        assertEquals("15K", com.game.dungeon.ui.components.formatAmount(15000L))
        assertEquals("1.5M", com.game.dungeon.ui.components.formatAmount(1500000L))
        assertEquals("2.5B", com.game.dungeon.ui.components.formatAmount(2500000000L))
        assertEquals("∞", com.game.dungeon.ui.components.formatAmount(Long.MAX_VALUE))
    }

    @Test
    fun testJobAbilityThreeTierProgression() {
        for (job in HeroClass.entries) {
            val abilities = JobAbilityData.getAbilitiesForJob(job)
            assertEquals("Each job class must have 3 ability tiers defined", 3, abilities.size)

            val level0Spec = JobAbilityData.getActiveAbilityForLevel(job, 0)
            assertNull("Level 0 / 0 mastery must return null for active ability", level0Spec)

            val tier1 = JobAbilityData.getActiveAbilityForLevel(job, 1)
            val tier2 = JobAbilityData.getActiveAbilityForLevel(job, 10)
            val tier3 = JobAbilityData.getActiveAbilityForLevel(job, 25)

            assertNotNull("Level 1 must unlock tier 1 ability", tier1)
            assertNotNull("Level 10 must unlock tier 2 ability", tier2)
            assertNotNull("Level 25 must unlock tier 3 ability", tier3)

            assertEquals(1, tier1!!.unlockLevel)
            assertEquals(10, tier2!!.unlockLevel)
            assertEquals(25, tier3!!.unlockLevel)

            assertNotEquals(tier1.nameRes, tier2.nameRes)
            assertNotEquals(tier2.nameRes, tier3.nameRes)
        }
    }

    @Test
    fun testJobAbilityUnlocksAndCombatExecution() {
        val engine = FFBattleEngine(mockContext)

        for (job in HeroClass.entries) {
            // Verify level 0 returns null (no ability unlocked at level 0 mastery)
            val level0Spec = JobAbilityData.getActiveAbilityForLevel(job, 0)
            assertNull("Level 0 job mastery must return null (no ability unlocked)", level0Spec)

            // Test level 0 mastery hero: ability should NOT trigger even when abilityCharge >= 3
            val level0Hero = Hero(
                id = "h0",
                heroClass = job,
                name = "Hero0",
                currentHp = 100,
                currentMp = 50,
                level = 1,
                abilityCharge = 2,
                aiPriority = job.defaultPriority
            )
            val eventsLevel0 = mutableListOf<FFBattleEvent>()
            kotlinx.coroutines.runBlocking {
                engine.runBattle(
                    heroes = listOf(level0Hero),
                    dimension = FFDimensionData.getDimension(1),
                    startFloor = 1,
                    speed = BattleSpeed.INSTANT,
                    relicBonuses = RelicBonuses.from(GameState(jobMasteryLevels = emptyMap())),
                    isPaused = { false },
                    onEvent = { eventsLevel0.add(it) }
                )
            }
            val abilityEventLvl0 = eventsLevel0.filterIsInstance<FFBattleEvent.AbilityUsed>().firstOrNull()
            assertNull("Hero with 0 job mastery level must NOT use job ability in combat", abilityEventLvl0)

            // Verify unlocked tier specs
            val tier1Spec = JobAbilityData.getActiveAbilityForLevel(job, 1)!!
            val tier2Spec = JobAbilityData.getActiveAbilityForLevel(job, 10)!!
            val tier3Spec = JobAbilityData.getActiveAbilityForLevel(job, 25)!!

            assertEquals(1, tier1Spec.unlockLevel)
            assertEquals(10, tier2Spec.unlockLevel)
            assertEquals(25, tier3Spec.unlockLevel)

            // Test combat execution when job mastery = 1, 10, 25
            for ((masteryLvl, expectedSpec) in listOf(
                1 to tier1Spec,
                10 to tier2Spec,
                25 to tier3Spec
            )) {
                val hero = Hero(
                    id = "h_$masteryLvl",
                    heroClass = job,
                    name = "Hero$masteryLvl",
                    currentHp = 100,
                    currentMp = 50,
                    level = 1,
                    abilityCharge = 2,
                    aiPriority = job.defaultPriority
                )
                val emittedEvents = mutableListOf<FFBattleEvent>()

                kotlinx.coroutines.runBlocking {
                    engine.runBattle(
                        heroes = listOf(hero),
                        dimension = FFDimensionData.getDimension(1),
                        startFloor = 1,
                        speed = BattleSpeed.INSTANT,
                        relicBonuses = RelicBonuses.from(GameState(jobMasteryLevels = mapOf(job to masteryLvl))),
                        isPaused = { false },
                        onEvent = { emittedEvents.add(it) }
                    )
                }

                val abilityEvent = emittedEvents.filterIsInstance<FFBattleEvent.AbilityUsed>().firstOrNull()
                assertNotNull("AbilityUsed event must be emitted when abilityCharge reaches 3 at job mastery level $masteryLvl for $job", abilityEvent)
                assertEquals("Ability used must match unlocked tier spec name at job mastery level $masteryLvl for $job", expectedSpec.nameRes, abilityEvent!!.nameRes)
            }
        }
    }

    @Test
    fun testUpgradeCostFormulasAndDiscount() {
        // Basic Upgrade Cost Formula: BaseCost * (Level + 1) for Lvl < 10
        assertEquals(300L, UpgradeType.VAULT.getCost(0))
        assertEquals(600L, UpgradeType.VAULT.getCost(1))
        assertEquals(3000L, UpgradeType.VAULT.getCost(9))
        // Basic Upgrade Cost Formula: BaseCost * Level^2 for Lvl >= 10
        assertEquals(30000L, UpgradeType.VAULT.getCost(10))

        // Advanced Upgrade Cost Formula: BaseCost * (Level + 1)^2
        assertEquals(500000L, UpgradeType.ALCHEMIST.getCost(0))
        assertEquals(2000000L, UpgradeType.ALCHEMIST.getCost(1))
        assertEquals(50000000L, UpgradeType.ALCHEMIST.getCost(9))

        assertEquals(2500000L, UpgradeType.FORGE.getCost(0))
        assertEquals(10000000L, UpgradeType.FORGE.getCost(1))
        assertEquals(250000000L, UpgradeType.FORGE.getCost(9))

        // Discount calculation test (Planning Lvl 10 = 20% discount)
        val gsDiscount = GameState(planningLevel = 10)
        assertEquals(0.20f, gsDiscount.upgradeDiscount, 0.001f)
        val rawCost = UpgradeType.ALCHEMIST.getCost(0)
        val discountedCost = (rawCost * (1f - gsDiscount.upgradeDiscount)).toLong()
        assertEquals(400000L, discountedCost)
    }

    @Test
    fun testAdvancedUpgradeStatMultipliers() {
        val gs = GameState(
            trainingLevel = 5,
            alchemistLevel = 5,
            libraryLevel = 5,
            forgeLevel = 5,
            warRoomLevel = 5
        )

        // Alchemist: +10% Magicite drop & +10% Magicite yield per level (Lvl 5 -> +50% yield)
        assertEquals(1.50f, gs.magiciteYieldBonus, 0.001f)
        assertEquals(0.50f, gs.alchemistDropChanceBonus, 0.001f)

        // Library: +20% Job Mastery EXP gain per level (+ Training Grounds +10% per level)
        // Lvl 5 Training (+50%) + Lvl 5 Library (+100%) = 2.50x
        assertEquals(2.50f, gs.masteryExpMultiplier, 0.001f)

        // Forge: +5% Mythic drop & +10% Mythic stats per level
        assertEquals(0.25f, gs.mythicDropBonus, 0.001f)
        assertEquals(0.50f, gs.mythicStatBonus, 0.001f)

        // War Room: +15% Crit DMG & +5% Ability Charge per level
        assertEquals(75f, gs.warRoomCritBonus, 0.001f)
        assertEquals(0.25f, gs.warRoomAbilityChargeBonus, 0.001f)

        // RelicBonuses mapping check
        val relics = RelicBonuses.from(gs)
        assertEquals(1.50f, relics.magiciteYieldBonus, 0.001f)
        assertEquals(0.50f, relics.alchemistDropChanceBonus, 0.001f)
        assertEquals(0.25f, relics.mythicDropBonus, 0.001f)
        assertEquals(0.50f, relics.mythicStatBonus, 0.001f)
        assertEquals(75, relics.warRoomCritBonus)
        assertEquals(0.25f, relics.warRoomAbilityChargeBonus, 0.001f)
    }
}
