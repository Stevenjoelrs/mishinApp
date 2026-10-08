package com.mishin.core.data.repository

import com.mishin.core.database.dao.AlertDao
import com.mishin.core.domain.model.Alert
import com.mishin.core.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val dao: AlertDao
) : AlertRepository {
    override fun observeUnreadAlerts(): Flow<List<Alert>> = flowOf(emptyList())
    override fun observeAllAlerts(): Flow<List<Alert>> = flowOf(emptyList())
    override suspend fun createAlert(alert: Alert) { /* TODO */ }
    override suspend fun markAsRead(id: String) { /* TODO */ }
    override suspend fun markAllAsRead() { /* TODO */ }
    override suspend fun getUnreadCount(): Int = 0
    override fun observeUnreadCount(): Flow<Int> = flowOf(0)
    override suspend fun hasActiveAlertForItem(inventoryItemId: String): Boolean = false
}
