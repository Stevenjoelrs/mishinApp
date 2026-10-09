package com.mishin.feature.menu

import com.mishin.core.domain.model.MenuCategory
import com.mishin.core.domain.model.MenuItem
import com.mishin.core.domain.model.RecipeLine
import com.mishin.core.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory [MenuRepository] that records every write so tests can assert
 * call order (e.g. recipe cleanup before product deletion).
 */
class FakeMenuRepository : MenuRepository {

    private val categoryState = MutableStateFlow<List<MenuCategory>>(emptyList())
    private val itemState = MutableStateFlow<List<MenuItem>>(emptyList())
    private val recipeState = MutableStateFlow<List<RecipeLine>>(emptyList())

    val writtenCategories = mutableListOf<MenuCategory>()
    val writtenItems = mutableListOf<MenuItem>()
    val actions = mutableListOf<String>()

    fun seedCategories(vararg categories: MenuCategory) {
        categoryState.value = categories.toList()
    }

    fun seedItems(vararg items: MenuItem) {
        itemState.value = items.toList()
    }

    override fun observeCategories(): Flow<List<MenuCategory>> = categoryState

    override suspend fun getCategoryById(id: String): MenuCategory? =
        categoryState.value.firstOrNull { it.id == id }

    override suspend fun upsertCategory(category: MenuCategory) {
        actions += "upsertCategory:${category.id}"
        writtenCategories += category
        categoryState.value = categoryState.value.filterNot { it.id == category.id } + category
    }

    override suspend fun deleteCategory(id: String) {
        actions += "deleteCategory:$id"
        categoryState.value = categoryState.value.filterNot { it.id == id }
    }

    override fun observeMenuItems(categoryId: String?, activeOnly: Boolean): Flow<List<MenuItem>> =
        itemState.map { items ->
            items.filter { item ->
                (categoryId == null || item.categoryId == categoryId) &&
                    (!activeOnly || item.isActive)
            }
        }

    override suspend fun getMenuItemById(id: String): MenuItem? =
        itemState.value.firstOrNull { it.id == id }

    override suspend fun upsertMenuItem(item: MenuItem) {
        actions += "upsertMenuItem:${item.id}"
        writtenItems += item
        itemState.value = itemState.value.filterNot { it.id == item.id } + item
    }

    override suspend fun deleteMenuItem(id: String) {
        actions += "deleteMenuItem:$id"
        itemState.value = itemState.value.filterNot { it.id == id }
    }

    override fun observeRecipeLines(menuItemId: String): Flow<List<RecipeLine>> =
        recipeState.map { lines -> lines.filter { it.menuItemId == menuItemId } }

    override suspend fun upsertRecipeLine(line: RecipeLine) {
        recipeState.value = recipeState.value.filterNot { it.id == line.id } + line
    }

    override suspend fun deleteRecipeLine(id: String) {
        recipeState.value = recipeState.value.filterNot { it.id == id }
    }

    override suspend fun clearRecipeLines(menuItemId: String) {
        actions += "clearRecipeLines:$menuItemId"
        recipeState.value = recipeState.value.filterNot { it.menuItemId == menuItemId }
    }
}
