package com.mishin.core.domain.model

/**
 * An order placed at the café.
 */
data class Order(
    val id: String,
    val locationId: String,
    val status: OrderStatus,
    val tableNumber: String?,
    val total: Double,
    val paymentMethod: PaymentMethod?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val syncedAt: String?
)

enum class OrderStatus {
    OPEN,
    PAID,
    CANCELLED
}

enum class PaymentMethod {
    CASH,
    CARD,
    TRANSFER
}

/**
 * A line item within an order.
 * The [priceSnapshot] captures the price at the moment of ordering,
 * so historical orders remain accurate even if prices change later.
 */
data class OrderItem(
    val id: String,
    val orderId: String,
    val menuItemId: String,
    val menuItemName: String,
    val quantity: Int,
    /** Price at the moment the order was placed. */
    val priceSnapshot: Double,
    val createdAt: String
)
