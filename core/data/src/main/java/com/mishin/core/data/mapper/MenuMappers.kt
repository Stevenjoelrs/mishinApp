package com.mishin.core.data.mapper

import com.mishin.core.database.entity.MenuCategoryEntity
import com.mishin.core.database.entity.MenuItemEntity
import com.mishin.core.database.entity.RecipeLineEntity
import com.mishin.core.domain.model.MenuCategory
import com.mishin.core.domain.model.MenuItem
import com.mishin.core.domain.model.RecipeLine

/**
 * Mappers between menu domain models and Room entities.
 * These keep the domain layer clean from Room annotations.
 */

// -- MenuCategory --

fun MenuCategoryEntity.toDomain(): MenuCategory = MenuCategory(
    id = id,
    locationId = locationId,
    name = name,
    description = description,
    sortOrder = sortOrder,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncedAt = syncedAt
)

fun MenuCategory.toEntity(): MenuCategoryEntity = MenuCategoryEntity(
    id = id,
    locationId = locationId,
    name = name,
    description = description,
    sortOrder = sortOrder,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncedAt = syncedAt
)

// -- MenuItem --

fun MenuItemEntity.toDomain(): MenuItem = MenuItem(
    id = id,
    locationId = locationId,
    name = name,
    price = price,
    categoryId = categoryId,
    description = description,
    imageUri = imageUri,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncedAt = syncedAt
)

fun MenuItem.toEntity(): MenuItemEntity = MenuItemEntity(
    id = id,
    locationId = locationId,
    name = name,
    price = price,
    categoryId = categoryId,
    description = description,
    imageUri = imageUri,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncedAt = syncedAt
)

// -- RecipeLine --

fun RecipeLineEntity.toDomain(): RecipeLine = RecipeLine(
    id = id,
    menuItemId = menuItemId,
    inventoryItemId = inventoryItemId,
    quantityUsed = quantityUsed,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun RecipeLine.toEntity(): RecipeLineEntity = RecipeLineEntity(
    id = id,
    menuItemId = menuItemId,
    inventoryItemId = inventoryItemId,
    quantityUsed = quantityUsed,
    createdAt = createdAt,
    updatedAt = updatedAt
)
