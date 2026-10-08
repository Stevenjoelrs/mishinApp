package com.mishin.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    indices = [Index("created_at"), Index("status")]
)
data class OrderEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "location_id") val locationId: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "table_number") val tableNumber: String?,
    @ColumnInfo(name = "total") val total: Double,
    @ColumnInfo(name = "payment_method") val paymentMethod: String?,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String,
    @ColumnInfo(name = "deleted_at") val deletedAt: String?,
    @ColumnInfo(name = "synced_at") val syncedAt: String?
)

@Entity(
    tableName = "order_items",
    indices = [Index("order_id"), Index("menu_item_id")]
)
data class OrderItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "order_id") val orderId: String,
    @ColumnInfo(name = "menu_item_id") val menuItemId: String,
    @ColumnInfo(name = "menu_item_name") val menuItemName: String,
    @ColumnInfo(name = "quantity") val quantity: Int,
    @ColumnInfo(name = "price_snapshot") val priceSnapshot: Double,
    @ColumnInfo(name = "created_at") val createdAt: String
)
