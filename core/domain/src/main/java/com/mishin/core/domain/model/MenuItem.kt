package com.mishin.core.domain.model

/**
 * A product on the café menu.
 */
data class MenuItem(
    val id: String,
    val locationId: String,
    val name: String,
    val price: Double,
    val categoryId: String?,
    val description: String?,
    val imageUri: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val syncedAt: String?
)

/**
 * Menu category (Bebidas, Especial de la casa, Masitas, etc.)
 */
data class MenuCategory(
    val id: String,
    val locationId: String,
    val name: String,
    val description: String?,
    val sortOrder: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val syncedAt: String?
)
