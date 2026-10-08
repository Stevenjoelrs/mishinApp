package com.mishin.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_categories")
data class MenuCategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "location_id") val locationId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "sort_order") val sortOrder: Int,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String,
    @ColumnInfo(name = "deleted_at") val deletedAt: String?,
    @ColumnInfo(name = "synced_at") val syncedAt: String?
)

@Entity(
    tableName = "menu_items",
    indices = [androidx.room.Index("category_id")]
)
data class MenuItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "location_id") val locationId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "category_id") val categoryId: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "image_uri") val imageUri: String?,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String,
    @ColumnInfo(name = "deleted_at") val deletedAt: String?,
    @ColumnInfo(name = "synced_at") val syncedAt: String?
)

@Entity(
    tableName = "recipe_lines",
    indices = [
        androidx.room.Index("menu_item_id"),
        androidx.room.Index("inventory_item_id")
    ]
)
data class RecipeLineEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "menu_item_id") val menuItemId: String,
    @ColumnInfo(name = "inventory_item_id") val inventoryItemId: String,
    @ColumnInfo(name = "quantity_used") val quantityUsed: Double,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)
