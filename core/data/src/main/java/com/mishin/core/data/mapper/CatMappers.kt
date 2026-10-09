package com.mishin.core.data.mapper

import com.mishin.core.database.entity.CatEntity
import com.mishin.core.domain.model.Cat
import com.mishin.core.domain.model.CatSex
import com.mishin.core.domain.model.CatStatus

/**
 * Mappers between cat domain models and Room entities.
 * These keep the domain layer clean from Room annotations.
 */

fun CatEntity.toDomain(): Cat = Cat(
    id = id,
    locationId = locationId,
    name = name,
    ageMonths = ageMonths,
    sex = CatSex.valueOf(sex),
    photoUri = photoUri,
    status = CatStatus.valueOf(status),
    intakeDate = intakeDate,
    notes = notes,
    medicalCheck = medicalCheck,
    tripleVaccine = tripleVaccine,
    sterilized = sterilized,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncedAt = syncedAt
)

fun Cat.toEntity(): CatEntity = CatEntity(
    id = id,
    locationId = locationId,
    name = name,
    ageMonths = ageMonths,
    sex = sex.name,
    photoUri = photoUri,
    status = status.name,
    intakeDate = intakeDate,
    notes = notes,
    medicalCheck = medicalCheck,
    tripleVaccine = tripleVaccine,
    sterilized = sterilized,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncedAt = syncedAt
)
