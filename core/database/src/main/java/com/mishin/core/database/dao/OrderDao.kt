package com.mishin.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.mishin.core.database.entity.OrderEntity
import com.mishin.core.database.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders WHERE deleted_at IS NULL ORDER BY created_at DESC")
    fun observeAll(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE deleted_at IS NULL AND status = :status ORDER BY created_at DESC")
    fun observeByStatus(status: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE deleted_at IS NULL AND created_at >= :dayStart AND created_at < :dayEnd ORDER BY created_at DESC")
    fun observeByDateRange(dayStart: String, dayEnd: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getById(id: String): OrderEntity?

    @Upsert
    suspend fun upsert(order: OrderEntity)

    @Insert
    suspend fun insertItems(items: List<OrderItemEntity>)

    @Query("SELECT * FROM order_items WHERE order_id = :orderId")
    fun observeOrderItems(orderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE order_id = :orderId")
    suspend fun getOrderItems(orderId: String): List<OrderItemEntity>

    @Query("SELECT COALESCE(SUM(total), 0) FROM orders WHERE status = 'PAID' AND created_at >= :dayStart AND created_at < :dayEnd")
    suspend fun getDailySalesTotal(dayStart: String, dayEnd: String): Double

    @Query("SELECT COUNT(*) FROM orders WHERE deleted_at IS NULL AND created_at >= :dayStart AND created_at < :dayEnd")
    suspend fun getDailyOrderCount(dayStart: String, dayEnd: String): Int
}
