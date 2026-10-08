package com.mishin.core.domain.repository

import com.mishin.core.domain.model.Reservation
import com.mishin.core.domain.model.ReservationStatus
import com.mishin.core.domain.model.Resource
import com.mishin.core.domain.model.ResourceType
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for resources and reservations.
 */
interface ReservationRepository {

    // -- Resources --

    fun observeResources(type: ResourceType? = null): Flow<List<Resource>>
    suspend fun getResourceById(id: String): Resource?
    suspend fun upsertResource(resource: Resource)
    suspend fun deleteResource(id: String)

    // -- Reservations --

    fun observeReservations(
        resourceId: String? = null,
        date: String? = null,
        status: ReservationStatus? = null
    ): Flow<List<Reservation>>

    fun observeTodayReservations(): Flow<List<Reservation>>
    suspend fun getReservationById(id: String): Reservation?
    suspend fun upsertReservation(reservation: Reservation)
    suspend fun updateReservationStatus(id: String, status: ReservationStatus)
    suspend fun deleteReservation(id: String)

    /**
     * Check if a new reservation would overlap with existing ones.
     * Applies buffer time between reservations.
     *
     * @return true if there IS a conflict.
     */
    suspend fun hasConflict(
        resourceId: String,
        startTime: String,
        endTime: String,
        excludeReservationId: String? = null
    ): Boolean
}
