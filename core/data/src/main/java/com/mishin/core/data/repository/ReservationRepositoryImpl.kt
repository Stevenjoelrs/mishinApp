package com.mishin.core.data.repository

import com.mishin.core.database.dao.ReservationDao
import com.mishin.core.domain.model.*
import com.mishin.core.domain.repository.ReservationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val dao: ReservationDao
) : ReservationRepository {
    override fun observeResources(type: ResourceType?): Flow<List<Resource>> = flowOf(emptyList())
    override suspend fun getResourceById(id: String): Resource? = null
    override suspend fun upsertResource(resource: Resource) { /* TODO */ }
    override suspend fun deleteResource(id: String) { /* TODO */ }
    override fun observeReservations(resourceId: String?, date: String?, status: ReservationStatus?): Flow<List<Reservation>> = flowOf(emptyList())
    override fun observeTodayReservations(): Flow<List<Reservation>> = flowOf(emptyList())
    override suspend fun getReservationById(id: String): Reservation? = null
    override suspend fun upsertReservation(reservation: Reservation) { /* TODO */ }
    override suspend fun updateReservationStatus(id: String, status: ReservationStatus) { /* TODO */ }
    override suspend fun deleteReservation(id: String) { /* TODO */ }
    override suspend fun hasConflict(resourceId: String, startTime: String, endTime: String, excludeReservationId: String?): Boolean = false
}
