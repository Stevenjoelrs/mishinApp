package com.mishin.core.data.repository

import com.mishin.core.database.dao.CatDao
import com.mishin.core.database.dao.GameDao
import com.mishin.core.database.dao.InventoryDao
import com.mishin.core.database.dao.MenuDao
import com.mishin.core.database.dao.SettingsDao
import com.mishin.core.database.entity.AppSettingEntity
import com.mishin.core.database.entity.BoardGameEntity
import com.mishin.core.database.entity.CatEntity
import com.mishin.core.database.entity.GameLoanEntity
import com.mishin.core.database.entity.InventoryItemEntity
import com.mishin.core.database.entity.MenuCategoryEntity
import com.mishin.core.database.entity.MenuItemEntity
import com.mishin.core.database.entity.RecipeLineEntity
import com.mishin.core.database.entity.StockMovementEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/*
 * In-memory fakes that mirror each DAO's SQL contract (soft-delete filters,
 * ordering, status filters) so repository tests exercise the same behavior
 * Room would produce.
 */

class FakeCatDao : CatDao {

    private val store = MutableStateFlow<Map<String, CatEntity>>(emptyMap())

    fun seed(vararg cats: CatEntity) {
        store.value = cats.associateBy { it.id }
    }

    private fun live(m: Map<String, CatEntity>) =
        m.values.filter { it.deletedAt == null }.sortedBy { it.name }

    override fun observeAll(): Flow<List<CatEntity>> = store.map { live(it) }

    override fun observeByStatus(status: String): Flow<List<CatEntity>> =
        store.map { m -> live(m).filter { it.status == status } }

    override suspend fun getById(id: String): CatEntity? = store.value[id]

    override suspend fun upsert(cat: CatEntity) {
        store.update { it + (cat.id to cat) }
    }

    override suspend fun softDelete(id: String, deletedAt: String, updatedAt: String) {
        store.update { m ->
            m.mapValues { (_, v) ->
                if (v.id == id) v.copy(deletedAt = deletedAt, updatedAt = updatedAt) else v
            }
        }
    }

    override suspend fun updateStatus(id: String, status: String, updatedAt: String) {
        store.update { m ->
            m.mapValues { (_, v) ->
                if (v.id == id) v.copy(status = status, updatedAt = updatedAt) else v
            }
        }
    }

    override fun observeCount(): Flow<Int> =
        store.map { m -> m.values.count { it.deletedAt == null } }
}

class FakeGameDao : GameDao {

    private val games = MutableStateFlow<Map<String, BoardGameEntity>>(emptyMap())
    private val loans = MutableStateFlow<Map<String, GameLoanEntity>>(emptyMap())

    val suitableQueries = mutableListOf<String>()
    val statusUpdates = mutableListOf<Pair<String, String>>()
    val endedLoans = mutableListOf<String>()

    fun seedGames(vararg items: BoardGameEntity) {
        games.value = items.associateBy { it.id }
    }

    fun seedLoans(vararg items: GameLoanEntity) {
        loans.value = items.associateBy { it.id }
    }

    private fun live(m: Map<String, BoardGameEntity>) =
        m.values.filter { it.deletedAt == null }.sortedBy { it.name }

    override fun observeAll(): Flow<List<BoardGameEntity>> = games.map { live(it) }

    override fun observeByStatus(status: String): Flow<List<BoardGameEntity>> =
        games.map { m -> live(m).filter { it.status == status } }

    override fun findSuitableGames(playerCount: Int): Flow<List<BoardGameEntity>> {
        suitableQueries += "findSuitableGames($playerCount)"
        return games.map { m ->
            live(m).filter {
                it.status == "AVAILABLE" && it.minPlayers <= playerCount && it.maxPlayers >= playerCount
            }
        }
    }

    override fun findSuitableGamesWithDuration(
        playerCount: Int,
        maxDuration: Int
    ): Flow<List<BoardGameEntity>> {
        suitableQueries += "findSuitableGamesWithDuration($playerCount, $maxDuration)"
        return games.map { m ->
            live(m).filter {
                it.status == "AVAILABLE" &&
                    it.minPlayers <= playerCount &&
                    it.maxPlayers >= playerCount &&
                    it.durationMinutes <= maxDuration
            }
        }
    }

    override suspend fun getById(id: String): BoardGameEntity? = games.value[id]

    override suspend fun upsert(game: BoardGameEntity) {
        games.update { it + (game.id to game) }
    }

    override suspend fun softDelete(id: String, deletedAt: String, updatedAt: String) {
        games.update { m ->
            m.mapValues { (_, v) ->
                if (v.id == id) v.copy(deletedAt = deletedAt, updatedAt = updatedAt) else v
            }
        }
    }

    override suspend fun updateStatus(id: String, status: String, updatedAt: String) {
        statusUpdates += id to status
        games.update { m ->
            m.mapValues { (_, v) ->
                if (v.id == id) v.copy(status = status, updatedAt = updatedAt) else v
            }
        }
    }

    override fun observeActiveLoans(): Flow<List<GameLoanEntity>> =
        loans.map { m ->
            m.values.filter { it.endTime == null }.sortedByDescending { it.startTime }
        }

    override suspend fun getLoanById(id: String): GameLoanEntity? = loans.value[id]

    override suspend fun upsertLoan(loan: GameLoanEntity) {
        loans.update { it + (loan.id to loan) }
    }

    override suspend fun endLoan(loanId: String, endTime: String) {
        endedLoans += loanId
        loans.update { m ->
            m.mapValues { (_, v) ->
                if (v.id == loanId) v.copy(endTime = endTime) else v
            }
        }
    }
}

class FakeMenuDao : MenuDao {

    private val categories = MutableStateFlow<Map<String, MenuCategoryEntity>>(emptyMap())
    private val items = MutableStateFlow<Map<String, MenuItemEntity>>(emptyMap())
    private val recipes = MutableStateFlow<Map<String, RecipeLineEntity>>(emptyMap())

    val observeItemsCalls = mutableListOf<String>()

    fun seedCategories(vararg entities: MenuCategoryEntity) {
        categories.value = entities.associateBy { it.id }
    }

    fun seedItems(vararg entities: MenuItemEntity) {
        items.value = entities.associateBy { it.id }
    }

    fun seedRecipes(vararg entities: RecipeLineEntity) {
        recipes.value = entities.associateBy { it.id }
    }

    override fun observeCategories(): Flow<List<MenuCategoryEntity>> =
        categories.map { m ->
            m.values.filter { it.deletedAt == null }.sortedBy { it.sortOrder }
        }

    override suspend fun getCategoryById(id: String): MenuCategoryEntity? = categories.value[id]

    override suspend fun upsertCategory(category: MenuCategoryEntity) {
        categories.update { it + (category.id to category) }
    }

    override suspend fun softDeleteCategory(id: String, deletedAt: String, updatedAt: String) {
        categories.update { m ->
            m.mapValues { (_, v) ->
                if (v.id == id) v.copy(deletedAt = deletedAt, updatedAt = updatedAt) else v
            }
        }
    }

    private fun liveItems(m: Map<String, MenuItemEntity>) =
        m.values.filter { it.deletedAt == null }.sortedBy { it.name }

    override fun observeAllItems(): Flow<List<MenuItemEntity>> {
        observeItemsCalls += "all"
        return items.map { liveItems(it) }
    }

    override fun observeItemsByCategory(categoryId: String): Flow<List<MenuItemEntity>> {
        observeItemsCalls += "category:$categoryId"
        return items.map { m -> liveItems(m).filter { it.categoryId == categoryId } }
    }

    override fun observeActiveItems(): Flow<List<MenuItemEntity>> {
        observeItemsCalls += "active"
        return items.map { m -> liveItems(m).filter { it.isActive } }
    }

    override suspend fun getItemById(id: String): MenuItemEntity? = items.value[id]

    override suspend fun upsertItem(item: MenuItemEntity) {
        items.update { it + (item.id to item) }
    }

    override suspend fun softDeleteItem(id: String, deletedAt: String, updatedAt: String) {
        items.update { m ->
            m.mapValues { (_, v) ->
                if (v.id == id) v.copy(deletedAt = deletedAt, updatedAt = updatedAt) else v
            }
        }
    }

    override fun observeRecipeLines(menuItemId: String): Flow<List<RecipeLineEntity>> =
        recipes.map { m -> m.values.filter { it.menuItemId == menuItemId } }

    override suspend fun getRecipeLines(menuItemId: String): List<RecipeLineEntity> =
        recipes.value.values.filter { it.menuItemId == menuItemId }

    override suspend fun upsertRecipeLine(line: RecipeLineEntity) {
        recipes.update { it + (line.id to line) }
    }

    override suspend fun deleteRecipeLine(id: String) {
        recipes.update { it - id }
    }

    override suspend fun clearRecipeLines(menuItemId: String) {
        recipes.update { m -> m.filterValues { it.menuItemId != menuItemId } }
    }
}

class FakeInventoryDao : InventoryDao {

    private val items = MutableStateFlow<Map<String, InventoryItemEntity>>(emptyMap())
    private val movements = MutableStateFlow<List<StockMovementEntity>>(emptyList())

    fun seedItems(vararg entities: InventoryItemEntity) {
        items.value = entities.associateBy { it.id }
    }

    fun seedMovements(vararg entities: StockMovementEntity) {
        movements.value = entities.toList()
    }

    override fun observeAll(): Flow<List<InventoryItemEntity>> =
        items.map { m -> m.values.filter { it.deletedAt == null }.sortedBy { it.name } }

    override fun observeByCategory(category: String): Flow<List<InventoryItemEntity>> =
        items.map { m ->
            m.values.filter { it.deletedAt == null && it.category == category }.sortedBy { it.name }
        }

    override suspend fun getById(id: String): InventoryItemEntity? = items.value[id]

    override suspend fun upsert(item: InventoryItemEntity) {
        items.update { it + (item.id to item) }
    }

    override suspend fun softDelete(id: String, deletedAt: String, updatedAt: String) {
        items.update { m ->
            m.mapValues { (_, v) ->
                if (v.id == id) v.copy(deletedAt = deletedAt, updatedAt = updatedAt) else v
            }
        }
    }

    override suspend fun getCurrentStock(itemId: String): Double =
        movements.value.filter { it.inventoryItemId == itemId }.sumOf { it.quantity }

    override fun observeCurrentStock(itemId: String): Flow<Double> =
        movements.map { list -> list.filter { it.inventoryItemId == itemId }.sumOf { it.quantity } }

    override suspend fun insertMovement(movement: StockMovementEntity) {
        movements.update { it + movement }
    }

    override fun observeMovements(itemId: String): Flow<List<StockMovementEntity>> =
        movements.map { list ->
            list.filter { it.inventoryItemId == itemId }.sortedByDescending { it.createdAt }
        }
}

class FakeSettingsDao : SettingsDao {

    private val store = MutableStateFlow<Map<String, AppSettingEntity>>(emptyMap())

    override fun observeByKey(key: String): Flow<AppSettingEntity?> =
        store.map { it[key] }

    override suspend fun getValue(key: String): AppSettingEntity? = store.value[key]

    override suspend fun upsert(setting: AppSettingEntity) {
        store.update { it + (setting.key to setting) }
    }
}
