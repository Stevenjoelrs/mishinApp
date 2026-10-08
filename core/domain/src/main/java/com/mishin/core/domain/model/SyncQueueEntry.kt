package com.mishin.core.domain.model

/**
 * Entry in the sync outbox queue.
 * Tracks local writes that need to be pushed to Supabase.
 */
data class SyncQueueEntry(
    val id: String,
    val tableName: String,
    val recordId: String,
    val operation: SyncOperation,
    val attempts: Int,
    val lastError: String?,
    val createdAt: String
)

enum class SyncOperation {
    INSERT,
    UPDATE,
    DELETE
}
