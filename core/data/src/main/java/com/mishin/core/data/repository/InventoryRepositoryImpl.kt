package com.mishin.core.data.repository

import com.mishin.core.data.mapper.toDomain
import com.mishin.core.data.mapper.toEntity
import com.mishin.core.database.dao.InventoryDao
import com.mishin.core.domain.model.InventoryItem
import com.mishin.core.domain.model.ItemCategory
import com.mishin.core.domain.model.StockMovement
import com.mishin.core.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Room-backed implementation of [InventoryRepository].
 * When Supabase sync is added, it will also write to the sync queue here.
 */
class InventoryRepositoryImpl @Inject constructor(
    private val dao: InventoryDao
) : InventoryRepository {

    override fun observeItems(category: ItemCategory?): Flow<List<InventoryItem>> {
        return if (category != null) {
            dao.observeByCategory(category.name)
        } else {
            dao.observeAll()
        }.map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getItemById(id: String): InventoryItem? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun upsertItem(item: InventoryItem) {
        dao.upsert(item.toEntity())
        // TODO: enqueue sync
    }

    override suspend fun deleteItem(id: String) {
        val now = com.mishin.core.common.util.DateTimeUtil.nowIso()
        dao.softDelete(id, deletedAt = now, updatedAt = now)
        // TODO: enqueue sync
    }

    override suspend fun getCurrentStock(itemId: String): Double {
        return dao.getCurrentStock(itemId)
    }

    override fun observeCurrentStock(itemId: String): Flow<Double> {
        return dao.observeCurrentStock(itemId)
    }

    override fun observeItemsWithStock(category: ItemCategory?): Flow<List<Pair<InventoryItem, Double>>> {
        return observeItems(category).map { items ->
            items.map { item ->
                item to dao.getCurrentStock(item.id)
            }
        }
    }

    override suspend fun recordMovement(movement: StockMovement) {
        dao.insertMovement(movement.toEntity())
        // TODO: enqueue sync
    }

    override fun observeMovements(itemId: String): Flow<List<StockMovement>> {
        return dao.observeMovements(itemId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeLowStockItems(): Flow<List<Pair<InventoryItem, Double>>> {
        return observeItems().map { items ->
            items.mapNotNull { item ->
                val stock = dao.getCurrentStock(item.id)
                val threshold = item.maxStock * (item.alertThresholdPercent / 100.0)
                if (stock <= threshold) item to stock else null
            }
        }
    }
}
