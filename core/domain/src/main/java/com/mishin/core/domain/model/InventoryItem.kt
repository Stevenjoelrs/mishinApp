package com.mishin.core.domain.model

/**
 * Inventory item — represents a trackable item in either the café or shelter.
 *
 * Stock is NEVER stored directly; it is always calculated as the sum of
 * [StockMovement] quantities. The fields here are reference/config values.
 */
data class InventoryItem(
    val id: String,
    val locationId: String,
    val name: String,
    val category: ItemCategory,
    val baseUnit: BaseUnit,
    /** Reference maximum stock level for this item. */
    val maxStock: Double,
    /** Alert threshold as percentage (0-100) of maxStock. */
    val alertThresholdPercent: Int,
    val supplierId: String?,
    /** Cost per base unit. */
    val costPerUnit: Double?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val syncedAt: String?
)

enum class ItemCategory {
    CAFE,
    SHELTER
}

enum class BaseUnit {
    GRAMS,
    MILLILITERS,
    UNITS
}
