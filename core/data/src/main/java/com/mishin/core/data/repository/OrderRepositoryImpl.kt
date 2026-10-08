package com.mishin.core.data.repository

import com.mishin.core.database.dao.OrderDao
import com.mishin.core.domain.model.Order
import com.mishin.core.domain.model.OrderItem
import com.mishin.core.domain.model.OrderStatus
import com.mishin.core.domain.model.PaymentMethod
import com.mishin.core.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Stub implementation of [OrderRepository].
 * TODO: Implement transactional payment + inventory deduction (Phase 3).
 */
class OrderRepositoryImpl @Inject constructor(
    private val dao: OrderDao
) : OrderRepository {

    override fun observeOrders(status: OrderStatus?): Flow<List<Order>> = flowOf(emptyList())
    override fun observeTodayOrders(): Flow<List<Order>> = flowOf(emptyList())
    override suspend fun getOrderById(id: String): Order? = null
    override suspend fun createOrder(order: Order, items: List<OrderItem>) { /* TODO */ }
    override fun observeOrderItems(orderId: String): Flow<List<OrderItem>> = flowOf(emptyList())
    override suspend fun markAsPaid(orderId: String, paymentMethod: PaymentMethod) { /* TODO */ }
    override suspend fun cancelOrder(orderId: String) { /* TODO */ }
    override suspend fun getDailySalesTotal(dateIso: String): Double = 0.0
    override suspend fun getDailyOrderCount(dateIso: String): Int = 0
}
