package com.mishin.core.domain.model

/**
 * A recipe line links a [MenuItem] to an [InventoryItem] with the
 * quantity consumed per unit sold.
 *
 * Example: "Latte" uses 18g of coffee beans + 200ml of milk.
 */
data class RecipeLine(
    val id: String,
    val menuItemId: String,
    val inventoryItemId: String,
    /** Amount of the inventory item consumed per 1 unit of the menu item. */
    val quantityUsed: Double,
    val createdAt: String,
    val updatedAt: String
)
