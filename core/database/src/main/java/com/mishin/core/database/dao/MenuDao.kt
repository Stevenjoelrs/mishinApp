package com.mishin.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mishin.core.database.entity.MenuCategoryEntity
import com.mishin.core.database.entity.MenuItemEntity
import com.mishin.core.database.entity.RecipeLineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {

    // -- Categories --

    @Query("SELECT * FROM menu_categories WHERE deleted_at IS NULL ORDER BY sort_order")
    fun observeCategories(): Flow<List<MenuCategoryEntity>>

    @Query("SELECT * FROM menu_categories WHERE id = :id")
    suspend fun getCategoryById(id: String): MenuCategoryEntity?

    @Upsert
    suspend fun upsertCategory(category: MenuCategoryEntity)

    @Query("UPDATE menu_categories SET deleted_at = :deletedAt, updated_at = :updatedAt WHERE id = :id")
    suspend fun softDeleteCategory(id: String, deletedAt: String, updatedAt: String)

    // -- Menu Items --

    @Query("SELECT * FROM menu_items WHERE deleted_at IS NULL ORDER BY name")
    fun observeAllItems(): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE deleted_at IS NULL AND category_id = :categoryId ORDER BY name")
    fun observeItemsByCategory(categoryId: String): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE deleted_at IS NULL AND is_active = 1 ORDER BY name")
    fun observeActiveItems(): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE id = :id")
    suspend fun getItemById(id: String): MenuItemEntity?

    @Upsert
    suspend fun upsertItem(item: MenuItemEntity)

    @Query("UPDATE menu_items SET deleted_at = :deletedAt, updated_at = :updatedAt WHERE id = :id")
    suspend fun softDeleteItem(id: String, deletedAt: String, updatedAt: String)

    // -- Recipe Lines --

    @Query("SELECT * FROM recipe_lines WHERE menu_item_id = :menuItemId")
    fun observeRecipeLines(menuItemId: String): Flow<List<RecipeLineEntity>>

    @Query("SELECT * FROM recipe_lines WHERE menu_item_id = :menuItemId")
    suspend fun getRecipeLines(menuItemId: String): List<RecipeLineEntity>

    @Upsert
    suspend fun upsertRecipeLine(line: RecipeLineEntity)

    @Query("DELETE FROM recipe_lines WHERE id = :id")
    suspend fun deleteRecipeLine(id: String)

    @Query("DELETE FROM recipe_lines WHERE menu_item_id = :menuItemId")
    suspend fun clearRecipeLines(menuItemId: String)
}
