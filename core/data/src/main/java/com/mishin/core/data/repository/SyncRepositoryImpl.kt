package com.mishin.core.data.repository

import com.mishin.core.database.dao.SyncQueueDao
import com.mishin.core.domain.model.SyncOperation
import com.mishin.core.domain.model.SyncQueueEntry
import com.mishin.core.domain.repository.SyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val dao: SyncQueueDao
) : SyncRepository {
    override fun observePendingEntries(): Flow<List<SyncQueueEntry>> = flowOf(emptyList())
    override suspend fun getPendingCount(): Int = 0
    override suspend fun enqueue(tableName: String, recordId: String, operation: SyncOperation) { /* TODO */ }
    override suspend fun markSynced(entryId: String) { /* TODO */ }
    override suspend fun markFailed(entryId: String, error: String) { /* TODO */ }
    override suspend fun retryFailed() { /* TODO */ }
    override suspend fun clearSynced() { /* TODO */ }
}
