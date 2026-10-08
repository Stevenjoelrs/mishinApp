package com.mishin.core.domain.repository

import com.mishin.core.domain.model.Order
import com.mishin.core.domain.model.OrderItem
import com.mishin.core.domain.model.OrderStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for order management.
 *
 * Key invariant: when an order transitions to PAID, the implementation
 * must create stock movements for all order items based on their recipes
 * within a single transaction.
 */
interface OrderRepository {

    fun observeOrders(status: OrderStatus? = null): Flow<List<Order>>
    fun observeTodayOrders(): Flow<List<Order>>
    suspend fun getOrderById(id: String): Order?
    suspend fun createOrder(order: Order, items: List<OrderItem>)
    fun observeOrderItems(orderId: String): Flow<List<OrderItem>>

    /**
     * Mark order as paid and deduct inventory in a single transaction.
     * Creates stock movements based on recipe lines for each order item.
     */
    suspend fun markAsPaid(orderId: String, paymentMethod: com.mishin.core.domain.model.PaymentMethod)

    /**
     * Cancel a paid order and generate reversal stock movements.
     * Never deletes history — only adds reversal entries.
     */
    suspend fun cancelOrder(orderId: String)

    /** Get daily sales total. */
    suspend fun getDailySalesTotal(dateIso: String): Double

    /** Get order count for a given day. */
    suspend fun getDailyOrderCount(dateIso: String): Int
}
