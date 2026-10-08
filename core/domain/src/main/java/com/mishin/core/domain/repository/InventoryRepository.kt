package com.mishin.core.domain.repository

import com.mishin.core.domain.model.InventoryItem
import com.mishin.core.domain.model.ItemCategory
import com.mishin.core.domain.model.StockMovement
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for inventory operations.
 * Implementations live in :core:data — the domain never knows about Room or Supabase.
 */
interface InventoryRepository {

    /** Observe all active inventory items, optionally filtered by category. */
    fun observeItems(category: ItemCategory? = null): Flow<List<InventoryItem>>

    /** Get a single item by ID. */
    suspend fun getItemById(id: String): InventoryItem?

    /** Insert or update an inventory item. */
    suspend fun upsertItem(item: InventoryItem)

    /** Soft-delete an inventory item. */
    suspend fun deleteItem(id: String)

    /**
     * Calculate current stock for an item by summing all its movements.
     * This is the single source of truth for stock levels.
     */
    suspend fun getCurrentStock(itemId: String): Double

    /** Observe current stock level reactively. */
    fun observeCurrentStock(itemId: String): Flow<Double>

    /** Observe all items with their current stock (for the list screen). */
    fun observeItemsWithStock(category: ItemCategory? = null): Flow<List<Pair<InventoryItem, Double>>>

    /** Record a stock movement (purchase, sale, waste, etc.). */
    suspend fun recordMovement(movement: StockMovement)

    /** Get movement history for an item. */
    fun observeMovements(itemId: String): Flow<List<StockMovement>>

    /**
     * Get items whose stock is below the alert threshold.
     * Returns pairs of (item, currentStock).
     */
    fun observeLowStockItems(): Flow<List<Pair<InventoryItem, Double>>>
}
