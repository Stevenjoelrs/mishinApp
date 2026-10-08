package com.mishin.core.domain.model

/**
 * A bookable resource: karaoke room, cinema screen, table, etc.
 */
data class Resource(
    val id: String,
    val locationId: String,
    val name: String,
    val type: ResourceType,
    val capacity: Int,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val syncedAt: String?
)

enum class ResourceType {
    KARAOKE,
    CINEMA,
    TABLE,
    GAME_AREA
}

/**
 * A reservation for a [Resource].
 */
data class Reservation(
    val id: String,
    val locationId: String,
    val resourceId: String,
    val customerName: String,
    val customerContact: String?,
    val numberOfPeople: Int,
    /** ISO-8601 datetime. */
    val startTime: String,
    /** ISO-8601 datetime. */
    val endTime: String,
    val status: ReservationStatus,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val syncedAt: String?
)

enum class ReservationStatus {
    PENDING,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    NO_SHOW
}
