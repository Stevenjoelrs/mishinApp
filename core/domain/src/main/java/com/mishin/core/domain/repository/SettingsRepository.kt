package com.mishin.core.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository for small key-value settings edited from admin screens
 * (karaoke pricing, promo copy, and similar screen-specific values).
 */
interface SettingsRepository {

    /** Observe a setting's value, emitting `null` while it has never been saved. */
    fun observeValue(key: String): Flow<String?>

    /** Read a setting's value once, or `null` if it has never been saved. */
    suspend fun getValue(key: String): String?

    /** Create or update a setting. */
    suspend fun setValue(key: String, value: String)
}
