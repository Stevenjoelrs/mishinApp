package com.mishin.core.domain.repository

import com.mishin.core.domain.model.SyncOperation
import com.mishin.core.domain.model.SyncQueueEntry
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for the sync outbox queue.
 * Manages pending writes that need to be pushed to Supabase.
 */
interface SyncRepository {

    fun observePendingEntries(): Flow<List<SyncQueueEntry>>
    suspend fun getPendingCount(): Int
    suspend fun enqueue(tableName: String, recordId: String, operation: SyncOperation)
    suspend fun markSynced(entryId: String)
    suspend fun markFailed(entryId: String, error: String)
    suspend fun retryFailed()
    suspend fun clearSynced()
}
