package com.game.dungeon.data.repository

import android.content.Context
import androidx.room.withTransaction
import com.game.dungeon.data.db.CodexDao
import com.game.dungeon.data.db.GameDatabase
import com.game.dungeon.data.db.MaterialEntity
import com.game.dungeon.data.models.CodexBonuses
import com.game.dungeon.data.models.CodexMilestone
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.Item
import com.game.dungeon.data.models.ItemSlot
import com.game.dungeon.data.models.KillThreshold
import com.game.dungeon.data.models.MaterialCatalog
import com.game.dungeon.data.models.MaterialCodexDetail
import com.game.dungeon.data.models.MaterialInventoryItem
import com.game.dungeon.data.models.MonsterCodexDetail
import com.game.dungeon.data.models.MonsterLootTable
import com.game.dungeon.data.models.MonsterType
import com.game.dungeon.data.models.Rarity
import com.game.dungeon.data.models.RelicBonuses
import com.game.dungeon.data.models.SocketSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.pow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val database: GameDatabase,
    private val leaderboardRepository: LeaderboardRepository,
    private val codexDao: CodexDao,
    private val authRepository: AuthRepository
) {
    // Codex Flows
    val codexCompletionPercentage: Flow<Float> = codexDao.getCodexEntries().map { entries ->
        val totalCatalogSize = MonsterType.entries.size + MaterialCatalog.getAllKnownMaterials().size
        if (totalCatalogSize == 0) return@map 0f

        val discoveredEntries = entries.filter { it.isDiscovered }.map { it.entryId.uppercase() }.toSet()
        val validMonsters = MonsterType.entries.map { it.name.uppercase() }.toSet()
        val validMaterials = MaterialCatalog.getAllKnownMaterials().map { it.id.uppercase() }.toSet()
        val validCatalog = validMonsters + validMaterials

        val discoveredCount = discoveredEntries.count { it in validCatalog }
        ((discoveredCount.toFloat() / totalCatalogSize.toFloat()) * 100f).coerceAtMost(100f)
    }

    val monsterCodexDetails: Flow<List<MonsterCodexDetail>> = codexDao.getCodexEntries().map { entries ->
        val entryMap = entries.associateBy { it.entryId.uppercase() }
        MonsterType.entries.map { monsterType ->
            val entity = entryMap[monsterType.name.uppercase()]
            val count = entity?.killCount ?: 0
            val discovered = (entity?.isDiscovered == true) || count > 0
            MonsterCodexDetail(
                monsterType = monsterType,
                killCount = count,
                threshold = KillThreshold.fromKillCount(count),
                isDiscovered = discovered,
                drops = MonsterLootTable.getDropsForMonster(monsterType, isBoss = true)
            )
        }
    }

    val materialCodexDetails: Flow<List<MaterialCodexDetail>> = codexDao.getCodexEntries().map { entries ->
        val discoveredIds = entries.filter { it.isDiscovered }.map { it.entryId.lowercase() }.toSet()
        MaterialCatalog.getAllKnownMaterials().map { material ->
            val isDiscovered = discoveredIds.contains(material.id.lowercase())
            val dropSources = MonsterType.entries.filter { monsterType ->
                val drops = MonsterLootTable.getDropsForMonster(monsterType, isBoss = true)
                drops.any { it.materialId.equals(material.id, ignoreCase = true) }
            }
            MaterialCodexDetail(
                material = material,
                isDiscovered = isDiscovered,
                primaryDropSources = dropSources
            )
        }
    }

    // Codex Mutations & Milestone Calculations
    suspend fun recordMonsterKill(monsterTypeId: String) {
        if (monsterTypeId.isBlank()) return
        codexDao.incrementKillCount(entryId = monsterTypeId, category = "MONSTER", amount = 1)
    }

    suspend fun discoverMaterial(materialId: String) {
        if (materialId.isBlank()) return
        codexDao.discoverEntry(entryId = materialId, category = "MATERIAL")
    }

    suspend fun getCodexCompletionPercentage(): Float {
        val entries = codexDao.getAllEntriesOnce()
        val totalCatalogSize = MonsterType.entries.size + MaterialCatalog.getAllKnownMaterials().size
        if (totalCatalogSize == 0) return 0f

        val discoveredEntries = entries.filter { it.isDiscovered }.map { it.entryId.uppercase() }.toSet()
        val validMonsters = MonsterType.entries.map { it.name.uppercase() }.toSet()
        val validMaterials = MaterialCatalog.getAllKnownMaterials().map { it.id.uppercase() }.toSet()
        val validCatalog = validMonsters + validMaterials

        val discoveredCount = discoveredEntries.count { it in validCatalog }
        return ((discoveredCount.toFloat() / totalCatalogSize.toFloat()) * 100f).coerceAtMost(100f)
    }

    suspend fun getBonusFromCodex(): RelicBonuses {
        val percentage = getCodexCompletionPercentage()
        val codexBonuses = CodexMilestone.calculateBonuses(percentage)
        val gameState = getGameStateOnce() ?: GameState()
        return RelicBonuses.from(gameState, codexBonuses)
    }

    suspend fun getCodexBonuses(): CodexBonuses {
        val percentage = getCodexCompletionPercentage()
        return CodexMilestone.calculateBonuses(percentage)
    }

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

    suspend fun addMagicite(amount: Long) {
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

    suspend fun setHasSeenBossLairPopup(hasSeen: Boolean = true) {
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        database.gameStateDao.upsert(current.copy(hasSeenBossLairPopup = hasSeen))
    }

    suspend fun updateHighestBossLairFloor(floor: Int) {
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        val newState = if (floor > current.highestBossLairFloor) {
            val updated = current.copy(highestBossLairFloor = floor)
            database.gameStateDao.upsert(updated)
            updated
        } else {
            current
        }
        triggerFirebaseUpload(newState)
    }

    suspend fun setPermadeathProtectionActive(active: Boolean) {
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        database.gameStateDao.upsert(current.copy(isPermadeathProtectionActive = active))
    }

    suspend fun unlockPermadeathProtection() {
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        database.gameStateDao.upsert(current.copy(
            hasPermadeathProtectionUnlocked = true,
            isPermadeathProtectionActive = true
        ))
    }

    suspend fun triggerFirebaseUpload(state: GameState? = null) {
        val current = state ?: database.gameStateDao.getGameStateOnce() ?: return
        var sanitized = if (current.dimStartTime <= 0L) {
            val fixed = current.copy(dimStartTime = System.currentTimeMillis())
            database.gameStateDao.upsert(fixed)
            fixed
        } else {
            current
        }
        var playerId = sanitized.playerId
        if (playerId == null) {
            playerId = authRepository.getCurrentUserId() ?: authRepository.signInAnonymously()
            if (playerId != null) {
                sanitized = sanitized.copy(playerId = playerId)
                database.gameStateDao.upsert(sanitized)
            }
        }
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
        discoverMaterial(id)
    }

    suspend fun addMaterials(materials: Map<String, Int>) {
        materials.forEach { (id, amount) ->
            if (amount > 0) {
                database.materialDao.addMaterial(id, amount)
                discoverMaterial(id)
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

        if (item.enhancementLevel >= 10 && item.rarity != Rarity.MYTHIC) {
            return Result.failure(IllegalStateException("Item is already at maximum enhancement level (+10)."))
        }

        val requiredLevel = item.enhancementLevel + 1
        val isMythicAscension = requiredLevel > 10
        
        // Base gold calculation with exponential scaling past level 10
        val baseGoldCost = (requiredLevel * 100L * item.floorFound.coerceAtLeast(1)).coerceAtLeast(100L)
        val goldCost = if (isMythicAscension) {
            (baseGoldCost * (1.5).pow(requiredLevel - 10)).toLong()
        } else {
            baseGoldCost
        }

        // Determine required material and quantity
        val oreId = if (isMythicAscension) "mythic_shard" else when (item.rarity) {
            Rarity.COMMON -> "iron_ore"
            Rarity.RARE -> "mithril_ore"
            Rarity.EPIC -> "adamantite_ore"
            Rarity.LEGENDARY, Rarity.MYTHIC -> "orichalcum_ore"
        }
        
        val requiredOreCount = if (isMythicAscension) {
            (5 * (1.5).pow(requiredLevel - 10)).toInt()
        } else {
            requiredLevel
        }

        val state = database.gameStateDao.getGameStateOnce()
            ?: return Result.failure(IllegalStateException("Game state missing."))

        if (state.gold < goldCost) {
            return Result.failure(IllegalStateException("Insufficient gold. Required: $goldCost, available: ${state.gold}."))
        }

        val availableOre = database.materialDao.getMaterialById(oreId)?.amount ?: 0
        if (availableOre < requiredOreCount) {
            return Result.failure(IllegalStateException("Insufficient $oreId. Required: $requiredOreCount, available: $availableOre."))
        }

        val updatedItem = item.copy(enhancementLevel = requiredLevel)

        database.withTransaction {
            // Deduct cost
            database.gameStateDao.upsert(state.copy(gold = state.gold - goldCost))
            database.materialDao.deductMaterial(oreId, requiredOreCount)

            // Save updated item (preserves id, ownerId, etc.)
            database.itemDao.upsert(updatedItem)
        }

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

        // Append socket (preserves id, ownerId, etc.)
        val updatedSockets = item.sockets + SocketSlot()
        val updatedItem = item.copy(sockets = updatedSockets)

        database.withTransaction {
            // Deduct cost
            database.gameStateDao.upsert(state.copy(gold = state.gold - goldCost))
            database.materialDao.deductMaterial(oreId, requiredOreCount)

            database.itemDao.upsert(updatedItem)
        }

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

        val existingGem = item.sockets[socketIndex].socketedGem
        val gemMaterial = MaterialCatalog.getMaterial(gemMaterialId)
        val updatedSockets = item.sockets.toMutableList()
        updatedSockets[socketIndex] = updatedSockets[socketIndex].copy(socketedGem = gemMaterial)
        val updatedItem = item.copy(sockets = updatedSockets)

        database.withTransaction {
            // Return old socketed gem if present
            if (existingGem != null) {
                database.materialDao.addMaterial(existingGem.id, 1)
            }

            // Deduct target gem
            database.materialDao.deductMaterial(gemMaterialId, 1)

            // Insert new gem
            database.itemDao.upsert(updatedItem)
        }

        return Result.success(updatedItem)
    }

    suspend fun craftGem(gemId: String, requiredMaterials: Map<String, Int>): Result<Unit> {
        for ((matId, count) in requiredMaterials) {
            val currentAmount = database.materialDao.getMaterialById(matId)?.amount ?: 0
            if (currentAmount < count) {
                return Result.failure(IllegalStateException("Insufficient material $matId. Required: $count, available: $currentAmount."))
            }
        }

        database.withTransaction {
            for ((matId, count) in requiredMaterials) {
                database.materialDao.deductMaterial(matId, count)
            }

            database.materialDao.addMaterial(gemId, 1)
            discoverMaterial(gemId)
        }

        return Result.success(Unit)
    }

    suspend fun fuseGems(sourceGemId: String, targetGemId: String, count: Int = 3): Result<Unit> {
        if (count <= 0) return Result.failure(IllegalArgumentException("Count must be greater than 0."))

        val currentAmount = database.materialDao.getMaterialById(sourceGemId)?.amount ?: 0
        if (currentAmount < count) {
            return Result.failure(IllegalStateException("Insufficient source gems for fusion. Required: $count, available: $currentAmount."))
        }

        database.withTransaction {
            database.materialDao.deductMaterial(sourceGemId, count)
            database.materialDao.addMaterial(targetGemId, 1)
            discoverMaterial(targetGemId)
        }

        return Result.success(Unit)
    }

    suspend fun craftMythicEquipment(baseItemId: String, targetSetId: String = "", context: Context): Result<Item> {
        val baseItem = database.itemDao.getItemById(baseItemId)
            ?: return Result.failure(IllegalArgumentException("Item with ID $baseItemId not found."))

        if (baseItem.rarity != Rarity.LEGENDARY || baseItem.enhancementLevel < 10) {
            return Result.failure(IllegalStateException("Crafting Mythic Gear requires a +10 Legendary item."))
        }

        val shardCount = database.materialDao.getMaterialById("mythic_shard")?.amount ?: 0
        if (shardCount < 50) {
            return Result.failure(IllegalStateException("Insufficient Mythic Shards. Required: 50, available: $shardCount."))
        }

        val trophyCount = database.materialDao.getMaterialById("boss_trophy_3")?.amount ?: 0
        if (trophyCount < 5) {
            return Result.failure(IllegalStateException("Insufficient Boss Trophies. Required: 5, available: $trophyCount."))
        }

        val state = database.gameStateDao.getGameStateOnce()
        val currentDimension = state?.currentDimension ?: 1
        val craftFloor = maxOf(1000, state?.lifetimeHighestFloor ?: 0)
        val relicBonuses = state?.let { RelicBonuses.from(it) }

        val baseMythicItem = Item.random(
            floor = craftFloor,
            context = context,
            relicBonuses = relicBonuses,
            minRarity = Rarity.MYTHIC,
            dimension = currentDimension,
            forcedSlot = baseItem.slot
        )

        // Find the chosen set to fetch the proper lore name
        val chosenSet = com.game.dungeon.data.models.MythicSet.fromId(targetSetId)
            ?: com.game.dungeon.data.models.MythicSet.entries.first()

        val setName = try { context.getString(chosenSet.nameRes) } catch (_: Exception) { "Mythic Gear" }

        // Stamp the item with the chosen Set ID and name!
        val finalMythicItem = baseMythicItem.copy(
            setId = chosenSet.id,
            name = setName
        )

        database.withTransaction {
            database.materialDao.deductMaterial("mythic_shard", 50)
            database.materialDao.deductMaterial("boss_trophy_3", 5)
            database.itemDao.delete(baseItem)
            database.itemDao.upsert(finalMythicItem)
        }

        return Result.success(finalMythicItem)
    }

    suspend fun craftMythicEquipment(baseItemId: String, context: Context): Result<Item> =
        craftMythicEquipment(baseItemId, "", context)

    suspend fun craftMythicEquipment(baseItem: Item, targetSetId: String = "", context: Context): Result<Item> =
        craftMythicEquipment(baseItem.id, targetSetId, context)

    suspend fun craftMythicEquipment(baseItem: Item, context: Context): Result<Item> =
        craftMythicEquipment(baseItem.id, "", context)

    suspend fun seedDebugMythicCrafting(context: Context) {
        val shardsToAdd = if (com.game.dungeon.BuildConfig.INITIAL_MYTHIC_SHARDS > 0) com.game.dungeon.BuildConfig.INITIAL_MYTHIC_SHARDS else 500
        val trophiesToAdd = if (com.game.dungeon.BuildConfig.INITIAL_BOSS_TROPHY_3 > 0) com.game.dungeon.BuildConfig.INITIAL_BOSS_TROPHY_3 else 50

        database.withTransaction {
            database.materialDao.addMaterial("mythic_shard", shardsToAdd)
            database.materialDao.addMaterial("boss_trophy_3", trophiesToAdd)
            database.materialDao.addMaterial("ruby_gem_1", 100)
            codexDao.discoverEntry(entryId = "mythic_shard", category = "MATERIAL")
            codexDao.discoverEntry(entryId = "boss_trophy_3", category = "MATERIAL")

            val state = database.gameStateDao.getGameStateOnce()
            val dim = state?.currentDimension ?: 1

            val slots = listOf(
                ItemSlot.WEAPON, ItemSlot.WEAPON, ItemSlot.WEAPON,
                ItemSlot.ARMOR, ItemSlot.ARMOR, ItemSlot.ARMOR,
                ItemSlot.SHIELD, ItemSlot.SHIELD,
                ItemSlot.ACCESSORY, ItemSlot.ACCESSORY
            )

            val debugItems = slots.map { slot ->
                Item.random(
                    floor = 100,
                    context = context,
                    minRarity = Rarity.LEGENDARY,
                    dimension = dim,
                    forcedSlot = slot
                ).copy(enhancementLevel = 10)
            }
            database.itemDao.upsertAll(debugItems)
        }
    }

    suspend fun levelUpHero(heroId: String): Result<Hero> {
        val hero = database.heroDao.getHeroById(heroId)
            ?: return Result.failure(IllegalArgumentException("Hero with ID $heroId not found."))

        if (hero.level >= hero.maxLevel || hero.isCapped) {
            return Result.failure(IllegalStateException("Hero level is capped at ${hero.maxLevel}. Breakthrough required."))
        }

        val updatedHero = hero.copy(level = hero.level + 1)
        database.heroDao.upsert(updatedHero)
        return Result.success(updatedHero)
    }

    suspend fun uncapHeroLevel(heroId: String): Result<Hero> {
        val hero = database.heroDao.getHeroById(heroId)
            ?: return Result.failure(IllegalArgumentException("Hero with ID $heroId not found."))

        val trophyId = when {
            hero.maxLevel <= 20 -> "boss_trophy_1"
            hero.maxLevel <= 40 -> "boss_trophy_2"
            else -> "boss_trophy_3"
        }

        val trophyAmount = database.materialDao.getMaterialById(trophyId)?.amount ?: 0
        if (trophyAmount <= 0) {
            return Result.failure(IllegalStateException("Boss trophy ($trophyId) required to uncap hero level."))
        }

        val updatedHero = hero.copy(maxLevel = hero.maxLevel + 20)

        database.withTransaction {
            database.materialDao.deductMaterial(trophyId, 1)
            database.heroDao.upsert(updatedHero)
        }

        return Result.success(updatedHero)
    }
}
