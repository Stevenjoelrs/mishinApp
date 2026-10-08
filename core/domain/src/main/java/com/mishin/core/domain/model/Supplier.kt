package com.mishin.core.domain.model

/**
 * Supplier contact info.
 */
data class Supplier(
    val id: String,
    val locationId: String,
    val name: String,
    val contactPhone: String?,
    val contactEmail: String?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val syncedAt: String?
)
