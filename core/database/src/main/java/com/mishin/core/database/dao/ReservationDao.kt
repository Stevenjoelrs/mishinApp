package com.mishin.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mishin.core.database.entity.ReservationEntity
import com.mishin.core.database.entity.ResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {

    // -- Resources --

    @Query("SELECT * FROM resources WHERE deleted_at IS NULL ORDER BY name")
    fun observeResources(): Flow<List<ResourceEntity>>

    @Query("SELECT * FROM resources WHERE deleted_at IS NULL AND type = :type ORDER BY name")
    fun observeResourcesByType(type: String): Flow<List<ResourceEntity>>

    @Query("SELECT * FROM resources WHERE id = :id")
    suspend fun getResourceById(id: String): ResourceEntity?

    @Upsert
    suspend fun upsertResource(resource: ResourceEntity)

    @Query("UPDATE resources SET deleted_at = :deletedAt, updated_at = :updatedAt WHERE id = :id")
    suspend fun softDeleteResource(id: String, deletedAt: String, updatedAt: String)

    // -- Reservations --

    @Query("SELECT * FROM reservations WHERE deleted_at IS NULL ORDER BY start_time")
    fun observeAll(): Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE deleted_at IS NULL AND resource_id = :resourceId ORDER BY start_time")
    fun observeByResource(resourceId: String): Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE deleted_at IS NULL AND start_time >= :dayStart AND start_time < :dayEnd ORDER BY start_time")
    fun observeByDate(dayStart: String, dayEnd: String): Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE id = :id")
    suspend fun getById(id: String): ReservationEntity?

    @Upsert
    suspend fun upsert(reservation: ReservationEntity)

    @Query("UPDATE reservations SET status = :status, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, updatedAt: String)

    @Query("UPDATE reservations SET deleted_at = :deletedAt, updated_at = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: String, updatedAt: String)

    /**
     * Overlap detection query.
     * Returns count of conflicting reservations for a resource in a time range.
     * Excludes cancelled and no-show reservations.
     */
    @Query("""
        SELECT COUNT(*) FROM reservations 
        WHERE resource_id = :resourceId 
        AND deleted_at IS NULL
        AND status NOT IN ('CANCELLED', 'NO_SHOW')
        AND id != :excludeId
        AND start_time < :endTime 
        AND end_time > :startTime
    """)
    suspend fun countConflicts(
        resourceId: String,
        startTime: String,
        endTime: String,
        excludeId: String
    ): Int
}
