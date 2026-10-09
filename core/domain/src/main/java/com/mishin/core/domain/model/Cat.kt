package com.mishin.core.domain.model

/**
 * A cat in the shelter.
 * Medical/adoption data is kept minimal per the plan;
 * sensitive fields can be added in future iterations.
 */
data class Cat(
    val id: String,
    val locationId: String,
    val name: String,
    /** Approximate age in months. */
    val ageMonths: Int?,
    val sex: CatSex,
    val photoUri: String?,
    val status: CatStatus,
    val intakeDate: String,
    val notes: String?,
    /** Intake requirement: veterinary check done. */
    val medicalCheck: Boolean,
    /** Intake requirement: triple vaccine applied. */
    val tripleVaccine: Boolean,
    /** Intake requirement: sterilized. */
    val sterilized: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val syncedAt: String?
)

enum class CatSex {
    MALE,
    FEMALE,
    UNKNOWN
}

enum class CatStatus {
    IN_SHELTER,
    UP_FOR_ADOPTION,
    ADOPTED
}
