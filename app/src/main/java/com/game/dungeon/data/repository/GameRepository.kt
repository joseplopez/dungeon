package com.game.dungeon.data.repository

import com.game.dungeon.data.db.GameDatabase
import com.game.dungeon.data.models.GameState
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.Item
import kotlinx.coroutines.flow.Flow
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
    fun newGame(): GameState = GameState()

    suspend fun addGil(amount: Long) {
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        val newGold = (current.gold + amount).coerceAtMost(current.maxGil)
        database.gameStateDao.upsert(current.copy(gold = newGold))
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
        val playerId = current.playerId
        if (playerId != null) {
            val party = database.heroDao.getPartyOnce()
            leaderboardRepository.uploadScore(playerId, current.playerName, current, party)
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
    fun getEquippedItems(heroId: String): Flow<List<Item>> = database.itemDao.getItemsForHeroFlow(heroId)
    suspend fun unequipAll(heroId: String) = database.itemDao.unequipAllFromHero(heroId)
    suspend fun saveItem(item: Item) = database.itemDao.upsert(item)
    suspend fun sellItem(item: Item) {
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        val newGold = (current.gold + item.sellValue).coerceAtMost(current.maxGil)
        database.gameStateDao.upsert(current.copy(gold = newGold))
        database.itemDao.delete(item)
    }
    suspend fun deleteItem(item: Item) = database.itemDao.delete(item)
    suspend fun clearInventory() = database.itemDao.deleteAll()
}
