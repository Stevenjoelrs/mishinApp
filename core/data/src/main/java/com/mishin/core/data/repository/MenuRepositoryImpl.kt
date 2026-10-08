package com.mishin.core.data.repository

import com.mishin.core.database.dao.MenuDao
import com.mishin.core.domain.model.MenuCategory
import com.mishin.core.domain.model.MenuItem
import com.mishin.core.domain.model.RecipeLine
import com.mishin.core.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Room-backed implementation of [MenuRepository].
 * TODO: Implement full mappers and DAO calls (Phase 3).
 */
class MenuRepositoryImpl @Inject constructor(
    private val dao: MenuDao
) : MenuRepository {

    override fun observeCategories(): Flow<List<MenuCategory>> = flowOf(emptyList())
    override suspend fun getCategoryById(id: String): MenuCategory? = null
    override suspend fun upsertCategory(category: MenuCategory) { /* TODO */ }
    override suspend fun deleteCategory(id: String) { /* TODO */ }
    override fun observeMenuItems(categoryId: String?, activeOnly: Boolean): Flow<List<MenuItem>> = flowOf(emptyList())
    override suspend fun getMenuItemById(id: String): MenuItem? = null
    override suspend fun upsertMenuItem(item: MenuItem) { /* TODO */ }
    override suspend fun deleteMenuItem(id: String) { /* TODO */ }
    override fun observeRecipeLines(menuItemId: String): Flow<List<RecipeLine>> = flowOf(emptyList())
    override suspend fun upsertRecipeLine(line: RecipeLine) { /* TODO */ }
    override suspend fun deleteRecipeLine(id: String) { /* TODO */ }
    override suspend fun clearRecipeLines(menuItemId: String) { /* TODO */ }
}
