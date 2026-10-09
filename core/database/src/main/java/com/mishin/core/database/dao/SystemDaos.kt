package com.mishin.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mishin.core.database.entity.AlertEntity
import com.mishin.core.database.entity.AppSettingEntity
import com.mishin.core.database.entity.SyncQueueEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Query("SELECT * FROM alerts WHERE is_read = 0 ORDER BY created_at DESC")
    fun observeUnread(): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts ORDER BY created_at DESC")
    fun observeAll(): Flow<List<AlertEntity>>

    @Upsert
    suspend fun upsert(alert: AlertEntity)

    @Query("UPDATE alerts SET is_read = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE alerts SET is_read = 1")
    suspend fun markAllAsRead()

    @Query("SELECT COUNT(*) FROM alerts WHERE is_read = 0")
    suspend fun getUnreadCount(): Int

    @Query("SELECT COUNT(*) FROM alerts WHERE is_read = 0")
    fun observeUnreadCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM alerts WHERE is_read = 0 AND type = 'LOW_STOCK' AND inventory_item_id = :itemId")
    suspend fun countActiveAlertsForItem(itemId: String): Int
}

@Dao
interface SyncQueueDao {

    @Query("SELECT * FROM sync_queue ORDER BY created_at")
    fun observePending(): Flow<List<SyncQueueEntryEntity>>

    @Query("SELECT COUNT(*) FROM sync_queue")
    suspend fun getPendingCount(): Int

    @Upsert
    suspend fun upsert(entry: SyncQueueEntryEntity)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun delete(id: String)

    @Query("UPDATE sync_queue SET attempts = attempts + 1, last_error = :error WHERE id = :id")
    suspend fun markFailed(id: String, error: String)
}

@Dao
interface SupplierDao {

    @Query("SELECT * FROM suppliers WHERE deleted_at IS NULL ORDER BY name")
    fun observeAll(): Flow<List<com.mishin.core.database.entity.SupplierEntity>>

    @Query("SELECT * FROM suppliers WHERE id = :id")
    suspend fun getById(id: String): com.mishin.core.database.entity.SupplierEntity?

    @Upsert
    suspend fun upsert(supplier: com.mishin.core.database.entity.SupplierEntity)

    @Query("UPDATE suppliers SET deleted_at = :deletedAt, updated_at = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: String, updatedAt: String)
}

@Dao
interface SettingsDao {

    @Query("SELECT * FROM app_settings WHERE setting_key = :key")
    fun observeByKey(key: String): Flow<AppSettingEntity?>

    @Query("SELECT * FROM app_settings WHERE setting_key = :key")
    suspend fun getValue(key: String): AppSettingEntity?

    @Upsert
    suspend fun upsert(setting: AppSettingEntity)
}
