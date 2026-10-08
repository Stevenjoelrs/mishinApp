package com.mishin.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for stock movements (ledger entries).
 * INSERT-ONLY — never updated or deleted.
 */
@Entity(
    tableName = "stock_movements",
    foreignKeys = [
        ForeignKey(
            entity = InventoryItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["inventory_item_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("inventory_item_id"),
        Index("reference_order_id"),
        Index("created_at")
    ]
)
data class StockMovementEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "location_id")
    val locationId: String,

    @ColumnInfo(name = "inventory_item_id")
    val inventoryItemId: String,

    @ColumnInfo(name = "quantity")
    val quantity: Double,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "reference_order_id")
    val referenceOrderId: String?,

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "synced_at")
    val syncedAt: String?
)
