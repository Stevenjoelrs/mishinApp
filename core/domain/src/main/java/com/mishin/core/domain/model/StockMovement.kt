package com.mishin.core.domain.model

/**
 * A stock movement entry — the ledger approach.
 *
 * Stock is computed as SUM(quantity) for an item. Positive = in, negative = out.
 * Movements are INSERT-ONLY; they are never updated or deleted.
 * This ensures audit trail integrity and avoids sync conflicts.
 */
data class StockMovement(
    val id: String,
    val locationId: String,
    val inventoryItemId: String,
    /** Positive for additions, negative for consumption/loss. */
    val quantity: Double,
    val type: MovementType,
    /** Reference to the order that caused this movement, if applicable. */
    val referenceOrderId: String?,
    val note: String?,
    val createdAt: String,
    val syncedAt: String?
)

enum class MovementType {
    PURCHASE,
    SALE,
    WASTE,
    ADJUSTMENT,
    DONATION,
    /** Reversal from a cancelled paid order. */
    REVERSAL
}
