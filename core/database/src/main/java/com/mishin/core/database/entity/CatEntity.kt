package com.mishin.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cats")
data class CatEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "location_id") val locationId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "age_months") val ageMonths: Int?,
    @ColumnInfo(name = "sex") val sex: String,
    @ColumnInfo(name = "photo_uri") val photoUri: String?,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "intake_date") val intakeDate: String,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "medical_check", defaultValue = "0") val medicalCheck: Boolean,
    @ColumnInfo(name = "triple_vaccine", defaultValue = "0") val tripleVaccine: Boolean,
    @ColumnInfo(name = "sterilized", defaultValue = "0") val sterilized: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String,
    @ColumnInfo(name = "deleted_at") val deletedAt: String?,
    @ColumnInfo(name = "synced_at") val syncedAt: String?
)
