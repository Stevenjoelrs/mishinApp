package com.mishin.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for inventory items.
 * Column names match the Supabase schema for seamless sync.
 */
@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "location_id")
    val locationId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "base_unit")
    val baseUnit: String,

    @ColumnInfo(name = "max_stock")
    val maxStock: Double,

    @ColumnInfo(name = "alert_threshold_percent")
    val alertThresholdPercent: Int,

    @ColumnInfo(name = "supplier_id")
    val supplierId: String?,

    @ColumnInfo(name = "cost_per_unit")
    val costPerUnit: Double?,

    @ColumnInfo(name = "notes")
    val notes: String?,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: String?,

    @ColumnInfo(name = "synced_at")
    val syncedAt: String?
)
