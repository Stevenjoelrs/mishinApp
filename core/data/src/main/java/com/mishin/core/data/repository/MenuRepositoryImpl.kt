package com.mishin.core.data.repository

import com.mishin.core.common.util.DateTimeUtil
import com.mishin.core.database.dao.MenuDao
import com.mishin.core.data.mapper.toDomain
import com.mishin.core.data.mapper.toEntity
import com.mishin.core.domain.model.MenuCategory
import com.mishin.core.domain.model.MenuItem
import com.mishin.core.domain.model.RecipeLine
import com.mishin.core.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Room-backed implementation of [MenuRepository].
 */
class MenuRepositoryImpl @Inject constructor(
    private val dao: MenuDao
) : MenuRepository {

    // -- Categories --

    override fun observeCategories(): Flow<List<MenuCategory>> =
        dao.observeCategories().map { list -> list.map { it.toDomain() } }

    override suspend fun getCategoryById(id: String): MenuCategory? =
        dao.getCategoryById(id)?.toDomain()

    override suspend fun upsertCategory(category: MenuCategory) =
        dao.upsertCategory(category.toEntity())

    override suspend fun deleteCategory(id: String) {
        val now = DateTimeUtil.nowIso()
        dao.softDeleteCategory(id, deletedAt = now, updatedAt = now)
    }

    // -- Menu Items --

    override fun observeMenuItems(
        categoryId: String?,
        activeOnly: Boolean
    ): Flow<List<MenuItem>> = when {
        categoryId != null -> dao.observeItemsByCategory(categoryId)
        activeOnly -> dao.observeActiveItems()
        else -> dao.observeAllItems()
    }.map { list -> list.map { it.toDomain() } }

    override suspend fun getMenuItemById(id: String): MenuItem? =
        dao.getItemById(id)?.toDomain()

    override suspend fun upsertMenuItem(item: MenuItem) =
        dao.upsertItem(item.toEntity())

    override suspend fun deleteMenuItem(id: String) {
        val now = DateTimeUtil.nowIso()
        dao.softDeleteItem(id, deletedAt = now, updatedAt = now)
    }

    // -- Recipes --

    override fun observeRecipeLines(menuItemId: String): Flow<List<RecipeLine>> =
        dao.observeRecipeLines(menuItemId).map { list -> list.map { it.toDomain() } }

    override suspend fun upsertRecipeLine(line: RecipeLine) =
        dao.upsertRecipeLine(line.toEntity())

    override suspend fun deleteRecipeLine(id: String) = dao.deleteRecipeLine(id)

    override suspend fun clearRecipeLines(menuItemId: String) =
        dao.clearRecipeLines(menuItemId)
}
