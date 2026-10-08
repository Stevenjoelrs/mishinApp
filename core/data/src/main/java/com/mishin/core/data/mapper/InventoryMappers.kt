package com.mishin.core.data.mapper

import com.mishin.core.database.entity.InventoryItemEntity
import com.mishin.core.database.entity.StockMovementEntity
import com.mishin.core.domain.model.BaseUnit
import com.mishin.core.domain.model.InventoryItem
import com.mishin.core.domain.model.ItemCategory
import com.mishin.core.domain.model.MovementType
import com.mishin.core.domain.model.StockMovement

/**
 * Mappers between domain models and Room entities.
 * These keep the domain layer clean from Room annotations.
 */

// -- InventoryItem --

fun InventoryItemEntity.toDomain(): InventoryItem = InventoryItem(
    id = id,
    locationId = locationId,
    name = name,
    category = ItemCategory.valueOf(category),
    baseUnit = BaseUnit.valueOf(baseUnit),
    maxStock = maxStock,
    alertThresholdPercent = alertThresholdPercent,
    supplierId = supplierId,
    costPerUnit = costPerUnit,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncedAt = syncedAt
)

fun InventoryItem.toEntity(): InventoryItemEntity = InventoryItemEntity(
    id = id,
    locationId = locationId,
    name = name,
    category = category.name,
    baseUnit = baseUnit.name,
    maxStock = maxStock,
    alertThresholdPercent = alertThresholdPercent,
    supplierId = supplierId,
    costPerUnit = costPerUnit,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncedAt = syncedAt
)

// -- StockMovement --

fun StockMovementEntity.toDomain(): StockMovement = StockMovement(
    id = id,
    locationId = locationId,
    inventoryItemId = inventoryItemId,
    quantity = quantity,
    type = MovementType.valueOf(type),
    referenceOrderId = referenceOrderId,
    note = note,
    createdAt = createdAt,
    syncedAt = syncedAt
)

fun StockMovement.toEntity(): StockMovementEntity = StockMovementEntity(
    id = id,
    locationId = locationId,
    inventoryItemId = inventoryItemId,
    quantity = quantity,
    type = type.name,
    referenceOrderId = referenceOrderId,
    note = note,
    createdAt = createdAt,
    syncedAt = syncedAt
)
