package com.mishin.core.domain.repository

import com.mishin.core.domain.model.MenuCategory
import com.mishin.core.domain.model.MenuItem
import com.mishin.core.domain.model.RecipeLine
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for menu management.
 */
interface MenuRepository {

    // -- Categories --

    fun observeCategories(): Flow<List<MenuCategory>>
    suspend fun getCategoryById(id: String): MenuCategory?
    suspend fun upsertCategory(category: MenuCategory)
    suspend fun deleteCategory(id: String)

    // -- Menu Items --

    fun observeMenuItems(categoryId: String? = null, activeOnly: Boolean = true): Flow<List<MenuItem>>
    suspend fun getMenuItemById(id: String): MenuItem?
    suspend fun upsertMenuItem(item: MenuItem)
    suspend fun deleteMenuItem(id: String)

    // -- Recipes --

    /** Get all recipe lines for a menu item. */
    fun observeRecipeLines(menuItemId: String): Flow<List<RecipeLine>>
    suspend fun upsertRecipeLine(line: RecipeLine)
    suspend fun deleteRecipeLine(id: String)

    /** Delete all recipe lines for a menu item (when rebuilding the recipe). */
    suspend fun clearRecipeLines(menuItemId: String)
}
