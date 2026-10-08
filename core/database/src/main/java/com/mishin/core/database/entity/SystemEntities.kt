package com.mishin.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alerts",
    indices = [Index("is_read"), Index("inventory_item_id")]
)
data class AlertEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "location_id") val locationId: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "inventory_item_id") val inventoryItemId: String?,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "is_read") val isRead: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "synced_at") val syncedAt: String?
)

@Entity(
    tableName = "sync_queue",
    indices = [Index("table_name"), Index("created_at")]
)
data class SyncQueueEntryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "table_name") val tableName: String,
    @ColumnInfo(name = "record_id") val recordId: String,
    @ColumnInfo(name = "operation") val operation: String,
    @ColumnInfo(name = "attempts") val attempts: Int,
    @ColumnInfo(name = "last_error") val lastError: String?,
    @ColumnInfo(name = "created_at") val createdAt: String
)
