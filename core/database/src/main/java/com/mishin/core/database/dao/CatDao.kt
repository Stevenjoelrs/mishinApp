package com.mishin.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mishin.core.database.entity.CatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatDao {

    @Query("SELECT * FROM cats WHERE deleted_at IS NULL ORDER BY name")
    fun observeAll(): Flow<List<CatEntity>>

    @Query("SELECT * FROM cats WHERE deleted_at IS NULL AND status = :status ORDER BY name")
    fun observeByStatus(status: String): Flow<List<CatEntity>>

    @Query("SELECT * FROM cats WHERE id = :id")
    suspend fun getById(id: String): CatEntity?

    @Upsert
    suspend fun upsert(cat: CatEntity)

    @Query("UPDATE cats SET deleted_at = :deletedAt, updated_at = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: String, updatedAt: String)

    @Query("UPDATE cats SET status = :status, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, updatedAt: String)

    @Query("SELECT COUNT(*) FROM cats WHERE deleted_at IS NULL")
    fun observeCount(): Flow<Int>
}
