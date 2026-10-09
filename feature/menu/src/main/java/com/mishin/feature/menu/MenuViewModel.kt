package com.mishin.feature.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mishin.core.common.constants.Defaults
import com.mishin.core.common.util.DateTimeUtil
import com.mishin.core.common.util.generateId
import com.mishin.core.domain.model.MenuCategory
import com.mishin.core.domain.model.MenuItem
import com.mishin.core.domain.repository.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the state for the menu management screen (categories and products)
 * and applies CRUD operations through [MenuRepository] (offline-first, Room).
 */
@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: MenuRepository
) : ViewModel() {

    val categories: StateFlow<List<MenuCategory>> = repository.observeCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val products: StateFlow<List<MenuItem>> =
        repository.observeMenuItems(categoryId = null, activeOnly = false)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun saveCategory(existing: MenuCategory?, name: String, description: String?) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return

        val trimmedDescription = description?.trim()?.takeIf { it.isNotEmpty() }
        val now = DateTimeUtil.nowIso()

        viewModelScope.launch {
            val category = if (existing != null) {
                existing.copy(
                    name = trimmedName,
                    description = trimmedDescription,
                    updatedAt = now
                )
            } else {
                MenuCategory(
                    id = generateId(),
                    locationId = Defaults.DEFAULT_LOCATION_ID,
                    name = trimmedName,
                    description = trimmedDescription,
                    sortOrder = (categories.value.maxOfOrNull { it.sortOrder } ?: 0) + 1,
                    createdAt = now,
                    updatedAt = now,
                    deletedAt = null,
                    syncedAt = null
                )
            }
            repository.upsertCategory(category)
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch { repository.deleteCategory(id) }
    }

    fun saveProduct(
        existing: MenuItem?,
        name: String,
        price: Double,
        categoryId: String?,
        description: String?,
        imageUri: String?
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty() || price < 0) return

        val trimmedDescription = description?.trim()?.takeIf { it.isNotEmpty() }
        val now = DateTimeUtil.nowIso()

        viewModelScope.launch {
            val product = if (existing != null) {
                existing.copy(
                    name = trimmedName,
                    price = price,
                    categoryId = categoryId,
                    description = trimmedDescription,
                    imageUri = imageUri,
                    updatedAt = now
                )
            } else {
                MenuItem(
                    id = generateId(),
                    locationId = Defaults.DEFAULT_LOCATION_ID,
                    name = trimmedName,
                    price = price,
                    categoryId = categoryId,
                    description = trimmedDescription,
                    imageUri = imageUri,
                    isActive = true,
                    createdAt = now,
                    updatedAt = now,
                    deletedAt = null,
                    syncedAt = null
                )
            }
            repository.upsertMenuItem(product)
        }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            repository.clearRecipeLines(id)
            repository.deleteMenuItem(id)
        }
    }
}
