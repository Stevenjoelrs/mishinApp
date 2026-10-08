package com.mishin.core.domain.repository

import com.mishin.core.domain.model.Alert
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for system alerts.
 */
interface AlertRepository {

    fun observeUnreadAlerts(): Flow<List<Alert>>
    fun observeAllAlerts(): Flow<List<Alert>>
    suspend fun createAlert(alert: Alert)
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
    suspend fun getUnreadCount(): Int
    fun observeUnreadCount(): Flow<Int>

    /**
     * Check if an active (unread) low-stock alert already exists for this item.
     * Prevents duplicate alerts per threshold crossing.
     */
    suspend fun hasActiveAlertForItem(inventoryItemId: String): Boolean
}
