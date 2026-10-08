package com.mishin.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mishin.core.database.entity.InventoryItemEntity
import com.mishin.core.database.entity.StockMovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {

    @Query("SELECT * FROM inventory_items WHERE deleted_at IS NULL ORDER BY name")
    fun observeAll(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE deleted_at IS NULL AND category = :category ORDER BY name")
    fun observeByCategory(category: String): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE id = :id")
    suspend fun getById(id: String): InventoryItemEntity?

    @Upsert
    suspend fun upsert(item: InventoryItemEntity)

    @Query("UPDATE inventory_items SET deleted_at = :deletedAt, updated_at = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: String, updatedAt: String)

    // -- Stock movements --

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM stock_movements WHERE inventory_item_id = :itemId")
    suspend fun getCurrentStock(itemId: String): Double

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM stock_movements WHERE inventory_item_id = :itemId")
    fun observeCurrentStock(itemId: String): Flow<Double>

    @Upsert
    suspend fun insertMovement(movement: StockMovementEntity)

    @Query("SELECT * FROM stock_movements WHERE inventory_item_id = :itemId ORDER BY created_at DESC")
    fun observeMovements(itemId: String): Flow<List<StockMovementEntity>>
}
