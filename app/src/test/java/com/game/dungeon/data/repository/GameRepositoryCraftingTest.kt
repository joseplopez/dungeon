package com.game.dungeon.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.dungeon.data.db.GameDatabase
import com.game.dungeon.data.models.AIPriority
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.models.Rarity
import com.game.dungeon.data.models.SocketSlot
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class GameRepositoryCraftingTest {

    private lateinit var database: GameDatabase
    private lateinit var leaderboardRepository: LeaderboardRepository
    private lateinit var repository: GameRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GameDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        leaderboardRepository = Mockito.mock(LeaderboardRepository::class.java)
        repository = GameRepository(database, leaderboardRepository, database.codexDao)
    }

    @After
    fun teardown() {
        database.close()
    }

    // --- enhanceEquipment Tests ---

    @Test
    fun testEnhanceEquipmentFailsWhenGoldIsInsufficient() = runBlocking {
        val item = Item(
            id = "sword_1",
            name = "Iron Sword",
            slot = ItemSlot.WEAPON,
            rarity = Rarity.COMMON,
            enhancementLevel = 0,
            emoji = "⚔️",
            floorFound = 1,
        )
        database.itemDao.upsert(item)
        database.gameStateDao.upsert(GameState(gold = 0L))
        database.materialDao.addMaterial("iron_ore", 10)

        val result = repository.enhanceEquipment("sword_1")

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception?.message?.contains("Insufficient gold") == true)
    }

    @Test
    fun testEnhanceEquipmentFailsWhenOresAreInsufficient() = runBlocking {
        val item = Item(
            id = "sword_1",
            name = "Iron Sword",
            slot = ItemSlot.WEAPON,
            rarity = Rarity.COMMON,
            enhancementLevel = 0,
            emoji = "⚔️",
            floorFound = 1,
        )
        database.itemDao.upsert(item)
        database.gameStateDao.upsert(GameState(gold = 10000L))
        // 0 iron_ore in materialDao

        val result = repository.enhanceEquipment("sword_1")

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception?.message?.contains("Insufficient iron_ore") == true)
    }

    @Test
    fun testEnhanceEquipmentSuccessDeductsCostAndIncrementsLevel() = runBlocking {
        val item = Item(
            id = "sword_1",
            name = "Iron Sword",
            slot = ItemSlot.WEAPON,
            rarity = Rarity.COMMON,
            enhancementLevel = 0,
            emoji = "⚔️",
            floorFound = 1,
        )
        database.itemDao.upsert(item)
        database.gameStateDao.upsert(GameState(gold = 1000L))
        database.materialDao.addMaterial("iron_ore", 5)

        val result = repository.enhanceEquipment("sword_1")

        assertTrue(result.isSuccess)
        val enhancedItem = result.getOrNull()
        assertNotNull(enhancedItem)
        assertEquals(1, enhancedItem?.enhancementLevel)

        // Check DB state
        val updatedState = database.gameStateDao.getGameStateOnce()
        assertEquals(900L, updatedState?.gold) // 1000 - (1 * 100 * 1) = 900

        val remainingOre = database.materialDao.getMaterialById("iron_ore")?.amount
        assertEquals(4, remainingOre) // 5 - 1 = 4
    }

    @Test
    fun testEnhanceEquipmentCappedAtPlus10() = runBlocking {
        val maxItem = Item(
            id = "max_sword",
            name = "Max Sword",
            slot = ItemSlot.WEAPON,
            rarity = Rarity.LEGENDARY,
            enhancementLevel = 10,
            emoji = "⚔️",
            floorFound = 10,
        )
        database.itemDao.upsert(maxItem)
        database.gameStateDao.upsert(GameState(gold = 1000000L))
        database.materialDao.addMaterial("orichalcum_ore", 100)

        val result = repository.enhanceEquipment("max_sword")

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception?.message?.contains("maximum enhancement level (+10)") == true)
    }

    // --- addSocketSlot Tests ---

    @Test
    fun testAddSocketSlotEnforcesMaxSocketsForWeaponsAndArmor() = runBlocking {
        val threeSockets = listOf(SocketSlot(), SocketSlot(), SocketSlot())
        val fullWeapon = Item(
            id = "full_weapon",
            name = "Full Weapon",
            slot = ItemSlot.WEAPON,
            rarity = Rarity.RARE,
            sockets = threeSockets,
            emoji = "⚔️",
            floorFound = 5,
        )
        database.itemDao.upsert(fullWeapon)
        database.gameStateDao.upsert(GameState(gold = 100000L))
        database.materialDao.addMaterial("iron_ore", 100)

        val result = repository.addSocketSlot("full_weapon")

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception?.message?.contains("Maximum socket capacity (3) reached") == true)
    }

    @Test
    fun testAddSocketSlotEnforcesMaxSocketsForAccessory() = runBlocking {
        val oneSocket = listOf(SocketSlot())
        val fullAccessory = Item(
            id = "full_ring",
            name = "Full Ring",
            slot = ItemSlot.ACCESSORY,
            rarity = Rarity.RARE,
            sockets = oneSocket,
            emoji = "💍",
            floorFound = 5,
        )
        database.itemDao.upsert(fullAccessory)
        database.gameStateDao.upsert(GameState(gold = 100000L))
        database.materialDao.addMaterial("iron_ore", 100)

        val result = repository.addSocketSlot("full_ring")

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception?.message?.contains("Maximum socket capacity (1) reached") == true)
    }

    @Test
    fun testAddSocketSlotSuccessAppendsSlotAndDeductsCosts() = runBlocking {
        val weapon = Item(
            id = "sword_sockets",
            name = "Socketable Sword",
            slot = ItemSlot.WEAPON,
            rarity = Rarity.RARE,
            sockets = emptyList(),
            emoji = "⚔️",
            floorFound = 1,
        )
        database.itemDao.upsert(weapon)
        database.gameStateDao.upsert(GameState(gold = 1000L))
        database.materialDao.addMaterial("iron_ore", 10)

        val result = repository.addSocketSlot("sword_sockets")

        assertTrue(result.isSuccess)
        val updatedItem = result.getOrNull()
        assertNotNull(updatedItem)
        assertEquals(1, updatedItem?.sockets?.size)

        // Cost check: gold = 250, iron_ore = 2
        val state = database.gameStateDao.getGameStateOnce()
        assertEquals(750L, state?.gold) // 1000 - 250 = 750

        val remainingOre = database.materialDao.getMaterialById("iron_ore")?.amount
        assertEquals(8, remainingOre) // 10 - 2 = 8
    }

    // --- fuseGems Tests ---

    @Test
    fun testFuseGemsFailsWhenSourceGemsAreInsufficient() = runBlocking {
        database.materialDao.addMaterial("ruby_gem_1", 2)

        val result = repository.fuseGems(
            sourceGemId = "ruby_gem_1",
            targetGemId = "ruby_gem_2",
            count = 3,
        )

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception?.message?.contains("Insufficient source gems for fusion") == true)
    }

    @Test
    fun testFuseGemsSuccessConsumesLowerTierAndGrantsHigherTier() = runBlocking {
        database.materialDao.addMaterial("ruby_gem_1", 5)

        val result = repository.fuseGems(
            sourceGemId = "ruby_gem_1",
            targetGemId = "ruby_gem_2",
            count = 3,
        )

        assertTrue(result.isSuccess)

        // Check remaining lower tier = 2 (5 - 3)
        val sourceAmount = database.materialDao.getMaterialById("ruby_gem_1")?.amount
        assertEquals(2, sourceAmount)

        // Check new higher tier = 1
        val targetAmount = database.materialDao.getMaterialById("ruby_gem_2")?.amount
        assertEquals(1, targetAmount)
    }

    @Test
    fun testFuseGemsFailsWithInvalidCount() = runBlocking {
        database.materialDao.addMaterial("ruby_gem_1", 10)

        val result = repository.fuseGems(
            sourceGemId = "ruby_gem_1",
            targetGemId = "ruby_gem_2",
            count = 0,
        )

        assertTrue(result.isFailure)
    }

    // --- uncapHeroLevel Tests ---

    @Test
    fun testUncapHeroLevelFailsWhenBossTrophyIsMissing() = runBlocking {
        val hero = Hero(
            id = "hero_1",
            heroClass = HeroClass.WARRIOR,
            name = "Warrior",
            currentHp = 100,
            currentMp = 20,
            level = 50,
            aiPriority = AIPriority.ATTACK,
        )
        database.heroDao.upsert(hero)
        // 0 boss_trophy_1 in materialDao

        val result = repository.uncapHeroLevel("hero_1")

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception?.message?.contains("Boss trophy (boss_trophy_1) required") == true)
    }

    @Test
    fun testUncapHeroLevelSuccessDeductsTrophyAndIncrementsLevel() = runBlocking {
        val hero = Hero(
            id = "hero_1",
            heroClass = HeroClass.WARRIOR,
            name = "Warrior",
            currentHp = 100,
            currentMp = 20,
            level = 20,
            maxLevel = 20,
            aiPriority = AIPriority.ATTACK,
        )
        database.heroDao.upsert(hero)
        database.materialDao.addMaterial("boss_trophy_1", 1)

        val result = repository.uncapHeroLevel("hero_1")

        assertTrue(result.isSuccess)
        val updatedHero = result.getOrNull()
        assertNotNull(updatedHero)
        assertEquals(40, updatedHero?.maxLevel) // maxLevel 20 + 20 = 40

        // Check trophy deducted
        val remainingTrophies = database.materialDao.getMaterialById("boss_trophy_1")?.amount ?: 0
        assertEquals(0, remainingTrophies)
    }

    @Test
    fun testLevelUpHeroRejectsWhenCapped() = runBlocking {
        val hero = Hero(
            id = "hero_1",
            heroClass = HeroClass.WARRIOR,
            name = "Warrior",
            currentHp = 100,
            currentMp = 20,
            level = 20,
            maxLevel = 20,
            aiPriority = AIPriority.ATTACK,
        )
        database.heroDao.upsert(hero)

        val result = repository.levelUpHero("hero_1")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("capped") == true)
    }

    @Test
    fun testLevelUpHeroSuccessWhenNotCapped() = runBlocking {
        val hero = Hero(
            id = "hero_1",
            heroClass = HeroClass.WARRIOR,
            name = "Warrior",
            currentHp = 100,
            currentMp = 20,
            level = 10,
            maxLevel = 20,
            aiPriority = AIPriority.ATTACK,
        )
        database.heroDao.upsert(hero)

        val result = repository.levelUpHero("hero_1")

        assertTrue(result.isSuccess)
        assertEquals(11, result.getOrNull()?.level)
    }

    // --- craftGem Test ---

    @Test
    fun testCraftGemSuccessConsumesMaterialsAndGrantsGem() = runBlocking {
        database.materialDao.addMaterial("fire_essence", 10)
        database.materialDao.addMaterial("iron_ore", 5)

        val recipe = mapOf(
            "fire_essence" to 3,
            "iron_ore" to 2,
        )

        val result = repository.craftGem("ruby_gem_1", recipe)

        assertTrue(result.isSuccess)
        assertEquals(7, database.materialDao.getMaterialById("fire_essence")?.amount) // 10 - 3
        assertEquals(3, database.materialDao.getMaterialById("iron_ore")?.amount) // 5 - 2
        assertEquals(1, database.materialDao.getMaterialById("ruby_gem_1")?.amount)
    }
}
