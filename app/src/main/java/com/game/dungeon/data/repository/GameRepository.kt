package com.game.dungeon.data.repository

import com.game.dungeon.data.db.GameDatabase
import com.game.dungeon.data.db.MaterialEntity
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.models.MaterialCatalog
import com.game.dungeon.data.models.MaterialInventoryItem
import com.game.dungeon.data.models.Rarity
import com.game.dungeon.data.models.SocketSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val database: GameDatabase,
    private val leaderboardRepository: LeaderboardRepository,
) {
    fun getGameState(): Flow<GameState?> = database.gameStateDao.getGameState()
    suspend fun getGameStateOnce(): GameState? = database.gameStateDao.getGameStateOnce()
    suspend fun saveGameState(state: GameState) = database.gameStateDao.upsert(state)
    fun newGame(): GameState = GameState(
        jobMasteryLevels = if (com.game.dungeon.BuildConfig.INITIAL_MASTERY_LEVEL > 0) {
            com.game.dungeon.data.models.HeroClass.entries.associateWith { com.game.dungeon.BuildConfig.INITIAL_MASTERY_LEVEL }
        } else {
            emptyMap()
        }
    )

    suspend fun addGil(amount: Long, bypassCap: Boolean = false) {
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        val newGold = if (bypassCap) {
            current.gold + amount
        } else {
            (current.gold + amount).coerceAtMost(current.maxGil)
        }
        val newTotal = if (amount > 0) current.totalGilEarned + amount else current.totalGilEarned
        database.gameStateDao.upsert(current.copy(gold = newGold, totalGilEarned = newTotal))
    }

    suspend fun addMagicite(amount: Int) {
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        database.gameStateDao.upsert(current.copy(
            magicite = current.magicite + amount,
            magiciteEarnedThisDim = current.magiciteEarnedThisDim + amount,
            totalMagiciteEarned = current.totalMagiciteEarned + amount
        ))
    }

    suspend fun updateHighestFloor(floor: Int) {
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        val isNewDimMax = floor > current.highestFloor
        val isNewLifetimeMax = floor > current.lifetimeHighestFloor

        if (isNewDimMax || isNewLifetimeMax) {
            val nextHighestFloor = if (isNewDimMax) floor else current.highestFloor
            val nextLifetimeHighestFloor = if (isNewLifetimeMax) floor else current.lifetimeHighestFloor
            val newState = current.copy(
                highestFloor = nextHighestFloor,
                lifetimeHighestFloor = nextLifetimeHighestFloor
            )
            database.gameStateDao.upsert(newState)
            
            // IMMEDIATE Upload to Firebase
            triggerFirebaseUpload(newState)
        }
    }

    suspend fun triggerFirebaseUpload(state: GameState? = null) {
        val current = state ?: database.gameStateDao.getGameStateOnce() ?: return
        val sanitized = if (current.dimStartTime <= 0L) {
            val fixed = current.copy(dimStartTime = System.currentTimeMillis())
            database.gameStateDao.upsert(fixed)
            fixed
        } else {
            current
        }
        val playerId = sanitized.playerId
        if (playerId != null) {
            val party = database.heroDao.getPartyOnce()
            leaderboardRepository.uploadScore(playerId, sanitized.playerName, sanitized, party)
        }
    }

    suspend fun trackDimensionStats(gil: Long, items: Int, bosses: Int) {
        val current = database.gameStateDao.getGameStateOnce() ?: return
        database.gameStateDao.upsert(current.copy(
            gilEarnedThisDim = current.gilEarnedThisDim + gil,
            itemsFoundThisDim = current.itemsFoundThisDim + items,
            bossesKilledThisDim = current.bossesKilledThisDim + bosses
        ))
    }

    suspend fun recordBossDefeat(bossName: String) {
        if (bossName.isBlank()) return
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        val uniqueEntry = "${bossName}_${System.currentTimeMillis()}_${java.util.UUID.randomUUID().toString().take(8)}"
        database.gameStateDao.upsert(current.copy(
            bossesDefeatedNames = current.bossesDefeatedNames + uniqueEntry
        ))
    }

    suspend fun recordBossDefeats(bossNames: Set<String>) {
        if (bossNames.isEmpty()) return
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        val uniqueEntries = bossNames.map { bossName ->
            "${bossName}_${System.currentTimeMillis()}_${java.util.UUID.randomUUID().toString().take(8)}"
        }.toSet()
        database.gameStateDao.upsert(current.copy(
            bossesDefeatedNames = current.bossesDefeatedNames + uniqueEntries
        ))
    }

    // Heroes
    fun getRoster(): Flow<List<Hero>> = database.heroDao.getAllHeroes()
    fun getParty(): Flow<List<Hero>> = database.heroDao.getParty()
    suspend fun saveHero(hero: Hero) = database.heroDao.upsert(hero)
    suspend fun saveHeroes(heroes: List<Hero>) = database.heroDao.upsertAll(heroes)
    suspend fun removeHero(hero: Hero) {
        database.itemDao.unequipAllFromHero(hero.id)
        database.heroDao.delete(hero)
    }

    // Items
    fun getInventory(): Flow<List<Item>> = database.itemDao.getInventory()
    fun getEquippedItems(): Flow<List<Item>> = database.itemDao.getEquippedItems()
    fun getEquippedItems(heroId: String): Flow<List<Item>> = database.itemDao.getItemsForHeroFlow(heroId)
    suspend fun getAllItemsOnce(): List<Item> = database.itemDao.getAllItemsOnce()
    suspend fun unequipAll(heroId: String) = database.itemDao.unequipAllFromHero(heroId)
    suspend fun saveItem(item: Item) = database.itemDao.upsert(item)
    suspend fun saveItems(items: List<Item>) = database.itemDao.upsertAll(items)
    suspend fun sellItem(item: Item) {
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        val newGold = (current.gold + item.sellValue).coerceAtMost(current.maxGil)
        database.gameStateDao.upsert(current.copy(gold = newGold))
        database.itemDao.delete(item)
    }
    suspend fun deleteItem(item: Item) = database.itemDao.delete(item)
    suspend fun clearInventory() = database.itemDao.deleteAll()

    // Materials & Crafting
    fun getMaterials(): Flow<List<MaterialEntity>> = database.materialDao.getMaterials()

    fun getMaterialInventory(): Flow<List<MaterialInventoryItem>> =
        database.materialDao.getMaterials().map { entities ->
            entities.map { entity ->
                MaterialInventoryItem(
                    material = MaterialCatalog.getMaterial(entity.id),
                    amount = entity.amount
                )
            }
        }

    suspend fun getMaterialAmount(id: String): Int {
        return database.materialDao.getMaterialById(id)?.amount ?: 0
    }

    suspend fun addMaterial(id: String, amount: Int) {
        if (amount <= 0) return
        database.materialDao.addMaterial(id, amount)
    }

    suspend fun addMaterials(materials: Map<String, Int>) {
        materials.forEach { (id, amount) ->
            if (amount > 0) {
                database.materialDao.addMaterial(id, amount)
            }
        }
    }

    suspend fun deductMaterial(id: String, amount: Int): Boolean {
        if (amount <= 0) return true
        return database.materialDao.deductMaterial(id, amount)
    }

    // Equipment Modifications
    suspend fun enhanceEquipment(itemId: String): Result<Item> {
        val item = database.itemDao.getItemById(itemId)
            ?: return Result.failure(IllegalArgumentException("Item with ID $itemId not found."))

        if (item.enhancementLevel >= 10) {
            return Result.failure(IllegalStateException("Item is already at maximum enhancement level (+10)."))
        }

        val requiredLevel = item.enhancementLevel + 1
        val goldCost = (requiredLevel * 100L * item.floorFound.coerceAtLeast(1)).coerceAtLeast(100L)
        val oreId = when (item.rarity) {
            Rarity.COMMON -> "iron_ore"
            Rarity.RARE -> "mithril_ore"
            Rarity.EPIC -> "adamantite_ore"
            Rarity.LEGENDARY -> "orichalcum_ore"
        }
        val requiredOreCount = requiredLevel

        val state = database.gameStateDao.getGameStateOnce()
            ?: return Result.failure(IllegalStateException("Game state missing."))

        if (state.gold < goldCost) {
            return Result.failure(IllegalStateException("Insufficient gold. Required: $goldCost, available: ${state.gold}."))
        }

        val availableOre = database.materialDao.getMaterialById(oreId)?.amount ?: 0
        if (availableOre < requiredOreCount) {
            return Result.failure(IllegalStateException("Insufficient $oreId. Required: $requiredOreCount, available: $availableOre."))
        }

        // Deduct cost
        database.gameStateDao.upsert(state.copy(gold = state.gold - goldCost))
        database.materialDao.deductMaterial(oreId, requiredOreCount)

        // Save updated item (preserves id, ownerId, etc.)
        val updatedItem = item.copy(enhancementLevel = requiredLevel)
        database.itemDao.upsert(updatedItem)

        return Result.success(updatedItem)
    }

    suspend fun enhanceEquipment(item: Item): Result<Item> = enhanceEquipment(item.id)

    suspend fun addSocketSlot(itemId: String): Result<Item> {
        val item = database.itemDao.getItemById(itemId)
            ?: return Result.failure(IllegalArgumentException("Item with ID $itemId not found."))

        val maxSockets = when (item.slot) {
            ItemSlot.ACCESSORY -> 1
            else -> 3
        }

        if (item.sockets.size >= maxSockets) {
            return Result.failure(IllegalStateException("Maximum socket capacity ($maxSockets) reached for this item type."))
        }

        val currentSocketCount = item.sockets.size
        val goldCost = ((currentSocketCount + 1) * 250L * item.floorFound.coerceAtLeast(1)).coerceAtLeast(250L)
        val oreId = "iron_ore"
        val requiredOreCount = (currentSocketCount + 1) * 2

        val state = database.gameStateDao.getGameStateOnce()
            ?: return Result.failure(IllegalStateException("Game state missing."))

        if (state.gold < goldCost) {
            return Result.failure(IllegalStateException("Insufficient gold. Required: $goldCost, available: ${state.gold}."))
        }

        val availableOre = database.materialDao.getMaterialById(oreId)?.amount ?: 0
        if (availableOre < requiredOreCount) {
            return Result.failure(IllegalStateException("Insufficient $oreId. Required: $requiredOreCount, available: $availableOre."))
        }

        // Deduct cost
        database.gameStateDao.upsert(state.copy(gold = state.gold - goldCost))
        database.materialDao.deductMaterial(oreId, requiredOreCount)

        // Append socket (preserves id, ownerId, etc.)
        val updatedSockets = item.sockets + SocketSlot()
        val updatedItem = item.copy(sockets = updatedSockets)
        database.itemDao.upsert(updatedItem)

        return Result.success(updatedItem)
    }

    suspend fun addSocketSlot(item: Item): Result<Item> = addSocketSlot(item.id)

    suspend fun insertGem(itemId: String, socketIndex: Int, gemMaterialId: String): Result<Item> {
        val item = database.itemDao.getItemById(itemId)
            ?: return Result.failure(IllegalArgumentException("Item with ID $itemId not found."))

        if (socketIndex !in item.sockets.indices) {
            return Result.failure(IllegalArgumentException("Invalid socket index $socketIndex."))
        }

        val gemAmount = database.materialDao.getMaterialById(gemMaterialId)?.amount ?: 0
        if (gemAmount <= 0) {
            return Result.failure(IllegalStateException("Gem material $gemMaterialId is not available in inventory."))
        }

        // Return old socketed gem if present
        val existingGem = item.sockets[socketIndex].socketedGem
        if (existingGem != null) {
            database.materialDao.addMaterial(existingGem.id, 1)
        }

        // Deduct target gem
        database.materialDao.deductMaterial(gemMaterialId, 1)

        // Insert new gem
        val gemMaterial = MaterialCatalog.getMaterial(gemMaterialId)
        val updatedSockets = item.sockets.toMutableList()
        updatedSockets[socketIndex] = updatedSockets[socketIndex].copy(socketedGem = gemMaterial)

        val updatedItem = item.copy(sockets = updatedSockets)
        database.itemDao.upsert(updatedItem)

        return Result.success(updatedItem)
    }

    suspend fun craftGem(gemId: String, requiredMaterials: Map<String, Int>): Result<Unit> {
        for ((matId, count) in requiredMaterials) {
            val currentAmount = database.materialDao.getMaterialById(matId)?.amount ?: 0
            if (currentAmount < count) {
                return Result.failure(IllegalStateException("Insufficient material $matId. Required: $count, available: $currentAmount."))
            }
        }

        for ((matId, count) in requiredMaterials) {
            database.materialDao.deductMaterial(matId, count)
        }

        database.materialDao.addMaterial(gemId, 1)
        return Result.success(Unit)
    }

    suspend fun fuseGems(sourceGemId: String, targetGemId: String, count: Int = 3): Result<Unit> {
        if (count <= 0) return Result.failure(IllegalArgumentException("Count must be greater than 0."))

        val currentAmount = database.materialDao.getMaterialById(sourceGemId)?.amount ?: 0
        if (currentAmount < count) {
            return Result.failure(IllegalStateException("Insufficient source gems for fusion. Required: $count, available: $currentAmount."))
        }

        database.materialDao.deductMaterial(sourceGemId, count)
        database.materialDao.addMaterial(targetGemId, 1)

        return Result.success(Unit)
    }

    suspend fun uncapHeroLevel(heroId: String): Result<Hero> {
        val hero = database.heroDao.getHeroById(heroId)
            ?: return Result.failure(IllegalArgumentException("Hero with ID $heroId not found."))

        val trophyId = "boss_trophy_1"
        val trophyAmount = database.materialDao.getMaterialById(trophyId)?.amount ?: 0
        if (trophyAmount <= 0) {
            return Result.failure(IllegalStateException("Boss trophy ($trophyId) required to uncap hero level."))
        }

        database.materialDao.deductMaterial(trophyId, 1)

        val updatedHero = hero.copy(level = hero.level + 5)
        database.heroDao.upsert(updatedHero)

        return Result.success(updatedHero)
    }
}
