package com.mishin.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mishin.core.database.dao.*
import com.mishin.core.database.entity.*

/**
 * The single Room database for the Mishin app.
 * Version 2 — added key-value app settings for admin-editable screen copy.
 * Version 3 — added cat intake requirement flags (medical check, triple
 * vaccine, sterilized).
 *
 * All entities follow the convention:
 * - UUID primary keys (client-generated)
 * - created_at, updated_at, deleted_at (soft delete), synced_at
 * - location_id for future multi-location support
 */
@Database(
    entities = [
        // Inventory
        InventoryItemEntity::class,
        StockMovementEntity::class,
        SupplierEntity::class,
        // Menu
        MenuCategoryEntity::class,
        MenuItemEntity::class,
        RecipeLineEntity::class,
        // Orders
        OrderEntity::class,
        OrderItemEntity::class,
        // Reservations
        ResourceEntity::class,
        ReservationEntity::class,
        // Games
        BoardGameEntity::class,
        GameLoanEntity::class,
        // Cats
        CatEntity::class,
        // System
        AlertEntity::class,
        SyncQueueEntryEntity::class,
        AppSettingEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class MishinDatabase : RoomDatabase() {

    abstract fun inventoryDao(): InventoryDao
    abstract fun menuDao(): MenuDao
    abstract fun orderDao(): OrderDao
    abstract fun reservationDao(): ReservationDao
    abstract fun gameDao(): GameDao
    abstract fun catDao(): CatDao
    abstract fun alertDao(): AlertDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun supplierDao(): SupplierDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        const val DATABASE_NAME = "mishin_db"
    }
}
