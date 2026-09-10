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
    private val database: GameDatabase
) {
    fun getGameState(): Flow<GameState?> = database.gameStateDao.getGameState()
    suspend fun saveGameState(state: GameState) = database.gameStateDao.upsert(state)
    fun newGame(): GameState = GameState()

    suspend fun addGil(amount: Long) {
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        database.gameStateDao.upsert(current.copy(gold = current.gold + amount))
    }

    suspend fun addMagicite(amount: Int) {
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        database.gameStateDao.upsert(current.copy(magicite = current.magicite + amount))
    }

    suspend fun updateHighestFloor(floor: Int) {
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        if (floor > current.highestFloor) {
            database.gameStateDao.upsert(current.copy(highestFloor = floor))
        }
    }

    // Heroes
    fun getRoster(): Flow<List<Hero>> = database.heroDao.getAllHeroes()
    fun getParty(): Flow<List<Hero>> = database.heroDao.getParty()
    suspend fun saveHero(hero: Hero) = database.heroDao.upsert(hero)
    suspend fun removeHero(hero: Hero) = database.heroDao.delete(hero)

    // Items
    fun getInventory(): Flow<List<Item>> = database.itemDao.getInventory()
    fun getEquippedItems(heroId: String): Flow<List<Item>> = database.itemDao.getItemsForHeroFlow(heroId)
    suspend fun unequipAll(heroId: String) = database.itemDao.unequipAllFromHero(heroId)
    suspend fun saveItem(item: Item) = database.itemDao.upsert(item)
    suspend fun sellItem(item: Item) {
        val current = database.gameStateDao.getGameStateOnce() ?: GameState()
        database.gameStateDao.upsert(current.copy(gold = current.gold + item.sellValue))
        // Instead of deleting, we can just delete from DB
    }
    suspend fun deleteItem(item: Item) = database.itemDao.delete(item)
    suspend fun clearInventory() = database.itemDao.deleteAll()
}
