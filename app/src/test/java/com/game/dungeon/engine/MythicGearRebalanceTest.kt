package com.game.dungeon.engine

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.dungeon.data.db.GameDatabase
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.models.Rarity
import com.game.dungeon.data.models.RelicBonuses
import com.game.dungeon.data.repository.GameRepository
import com.game.dungeon.data.repository.LeaderboardRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.lang.Math.pow

@RunWith(AndroidJUnit4::class)
class MythicGearRebalanceTest {

    private lateinit var db: GameDatabase
    private lateinit var leaderboardRepository: LeaderboardRepository
    private lateinit var repository: GameRepository
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, GameDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        leaderboardRepository = Mockito.mock(LeaderboardRepository::class.java)
        repository = GameRepository(db, leaderboardRepository, db.codexDao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testMythicRarityStatMultiplierIs2_5xOverLegendary() {
        val floor = 1000
        val relicBonuses = RelicBonuses.from(GameState())
        val legendaryItem = Item.random(
            floor = floor,
            context = context,
            relicBonuses = relicBonuses,
            minRarity = Rarity.LEGENDARY,
            dimension = 1,
            forcedSlot = ItemSlot.WEAPON
        )
        val mythicItem = Item.random(
            floor = floor,
            context = context,
            relicBonuses = relicBonuses,
            minRarity = Rarity.MYTHIC,
            dimension = 1,
            forcedSlot = ItemSlot.WEAPON
        )

        val legStat = maxOf(legendaryItem.attackBonus, legendaryItem.magicBonus)
        val mythStat = maxOf(mythicItem.attackBonus, mythicItem.magicBonus)

        assertTrue("Legendary stat should be > 0", legStat > 0)
        assertTrue("Mythic stat should be > 0", mythStat > 0)

        val ratio = mythStat.toDouble() / legStat.toDouble()
        assertEquals("Mythic base stat should be 2.5x Legendary base stat", 2.5, ratio, 0.05)
    }

    @Test
    fun testBossLairEffectiveFloorCalculation() {
        fun calcEffectiveFloor(currentFloor: Int): Int {
            return (1000.0 * pow(1.15, (currentFloor - 1).toDouble()))
                .toLong()
                .coerceAtMost(Int.MAX_VALUE.toLong())
                .toInt()
        }

        assertEquals(1000, calcEffectiveFloor(1))
        assertEquals(3517, calcEffectiveFloor(10))
        assertEquals(942310, calcEffectiveFloor(50))
        assertEquals(Int.MAX_VALUE, calcEffectiveFloor(500))
    }

    @Test
    fun testCraftMythicEquipmentUsesBaselineFloorFromLifetimeHighestFloor() = runBlocking {
        // Set lifetimeHighestFloor to 0 -> should baseline to maxOf(1000, 0) = 1000
        val gs0 = GameState(id = 1, lifetimeHighestFloor = 0)
        db.gameStateDao.upsert(gs0)

        val baseItem = Item(
            id = "leg_weapon_1",
            name = "Legendary Blade",
            slot = ItemSlot.WEAPON,
            rarity = Rarity.LEGENDARY,
            enhancementLevel = 10,
            emoji = "⚔️",
            floorFound = 10
        )
        db.itemDao.upsert(baseItem)
        db.materialDao.addMaterial("mythic_shard", 50)
        db.materialDao.addMaterial("boss_trophy_3", 5)

        val result0 = repository.craftMythicEquipment(baseItem, context)
        assertTrue(result0.isSuccess)
        val item0 = result0.getOrNull()
        assertNotNull(item0)
        assertEquals("Crafted Mythic floor should baseline to 1000 when lifetimeHighestFloor is 0", 1000, item0?.floorFound)

        // Now test with lifetimeHighestFloor = 5500 -> should use maxOf(1000, 5500) = 5500
        val gs5500 = gs0.copy(lifetimeHighestFloor = 5500)
        db.gameStateDao.upsert(gs5500)

        val baseItem2 = baseItem.copy(id = "leg_weapon_2")
        db.itemDao.upsert(baseItem2)
        db.materialDao.addMaterial("mythic_shard", 50)
        db.materialDao.addMaterial("boss_trophy_3", 5)

        val result5500 = repository.craftMythicEquipment(baseItem2, context)
        assertTrue(result5500.isSuccess)
        val item5500 = result5500.getOrNull()
        assertNotNull(item5500)
        assertEquals("Crafted Mythic floor should use lifetimeHighestFloor (5500)", 5500, item5500?.floorFound)
    }
}
