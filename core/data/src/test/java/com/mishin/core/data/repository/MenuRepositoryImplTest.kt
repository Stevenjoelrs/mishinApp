package com.mishin.core.data.repository

import com.mishin.core.data.mapper.toEntity
import com.mishin.core.domain.model.MenuCategory
import com.mishin.core.domain.model.MenuItem
import com.mishin.core.domain.model.RecipeLine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MenuRepositoryImplTest {

    private val dao = FakeMenuDao()
    private val repository = MenuRepositoryImpl(dao)

    private fun category(
        id: String = "cat_1",
        name: String = "Bebidas",
        sortOrder: Int = 1
    ) = MenuCategory(
        id = id,
        locationId = "loc_1",
        name = name,
        description = "Cafés y jugos",
        sortOrder = sortOrder,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-02T00:00:00Z",
        deletedAt = null,
        syncedAt = null
    )

    private fun item(
        id: String = "item_1",
        name: String = "Latte",
        categoryId: String? = "cat_1",
        isActive: Boolean = true
    ) = MenuItem(
        id = id,
        locationId = "loc_1",
        name = name,
        price = 12.5,
        categoryId = categoryId,
        description = null,
        imageUri = null,
        isActive = isActive,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-02T00:00:00Z",
        deletedAt = null,
        syncedAt = null
    )

    private fun recipeLine(id: String = "recipe_1", menuItemId: String = "item_1") = RecipeLine(
        id = id,
        menuItemId = menuItemId,
        inventoryItemId = "inv_1",
        quantityUsed = 0.018,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-01T00:00:00Z"
    )

    @Test
    fun `observeCategories maps and orders categories by sortOrder`() = runTest {
        dao.seedCategories(
            category(id = "cat_2", name = "Comida", sortOrder = 2).toEntity(),
            category(id = "cat_1", name = "Bebidas", sortOrder = 1).toEntity()
        )

        val categories = repository.observeCategories().first()

        assertEquals(listOf("cat_1", "cat_2"), categories.map { it.id })
        assertEquals("Bebidas", categories.first().name)
    }

    @Test
    fun `observeMenuItems routes to the category query when categoryId is set`() = runTest {
        dao.seedItems(
            item(id = "item_1", name = "Latte", categoryId = "cat_1").toEntity(),
            item(id = "item_2", name = "Tostada", categoryId = "cat_2").toEntity()
        )

        val items = repository.observeMenuItems(categoryId = "cat_1", activeOnly = false).first()

        assertEquals(listOf("item_1"), items.map { it.id })
        assertEquals(listOf("category:cat_1"), dao.observeItemsCalls)
    }

    @Test
    fun `observeMenuItems routes to the active query when activeOnly is true`() = runTest {
        dao.seedItems(
            item(id = "item_1", name = "Latte", isActive = true).toEntity(),
            item(id = "item_2", name = "Tostada", isActive = false).toEntity()
        )

        val items = repository.observeMenuItems(categoryId = null, activeOnly = true).first()

        assertEquals(listOf("item_1"), items.map { it.id })
        assertEquals(listOf("active"), dao.observeItemsCalls)
    }

    @Test
    fun `observeMenuItems routes to the all-items query by default`() = runTest {
        dao.seedItems(
            item(id = "item_1", name = "Latte", isActive = false).toEntity(),
            item(id = "item_2", name = "Tostada", isActive = true).toEntity()
        )

        val items = repository.observeMenuItems(categoryId = null, activeOnly = false).first()

        assertEquals(listOf("item_1", "item_2"), items.map { it.id })
        assertEquals(listOf("all"), dao.observeItemsCalls)
    }

    @Test
    fun `deleteCategory soft-deletes and hides the category`() = runTest {
        dao.seedCategories(category(id = "cat_1").toEntity())

        repository.deleteCategory("cat_1")

        assertEquals(emptyList<MenuCategory>(), repository.observeCategories().first())
        val deleted = repository.getCategoryById("cat_1")
        assertNotNull(deleted)
        assertNotNull(deleted?.deletedAt)
        assertTrue(deleted!!.updatedAt > "2026-01-02T00:00:00Z")
    }

    @Test
    fun `deleteMenuItem soft-deletes and hides the item`() = runTest {
        dao.seedItems(item(id = "item_1").toEntity())

        repository.deleteMenuItem("item_1")

        val items = repository.observeMenuItems(categoryId = null, activeOnly = false).first()
        assertEquals(emptyList<MenuItem>(), items)
        val deleted = repository.getMenuItemById("item_1")
        assertNotNull(deleted)
        assertNotNull(deleted?.deletedAt)
    }

    @Test
    fun `upsertMenuItem then getMenuItemById round-trips`() = runTest {
        val original = item(id = "item_9", name = "Mocha", categoryId = null, isActive = false)

        repository.upsertMenuItem(original)

        assertEquals(original, repository.getMenuItemById("item_9"))
    }

    @Test
    fun `upsertCategory then getCategoryById round-trips`() = runTest {
        val original = category(id = "cat_9", name = "Postres", sortOrder = 5)

        repository.upsertCategory(original)

        assertEquals(original, repository.getCategoryById("cat_9"))
    }

    @Test
    fun `observeRecipeLines maps lines for the requested menu item only`() = runTest {
        dao.seedRecipes(
            recipeLine(id = "recipe_1", menuItemId = "item_1").toEntity(),
            recipeLine(id = "recipe_2", menuItemId = "item_1").toEntity(),
            recipeLine(id = "recipe_3", menuItemId = "item_2").toEntity()
        )

        val lines = repository.observeRecipeLines("item_1").first()

        assertEquals(listOf("recipe_1", "recipe_2"), lines.map { it.id })
        assertEquals(0.018, lines.first().quantityUsed, 1e-9)
    }

    @Test
    fun `clearRecipeLines only clears lines of the given menu item`() = runTest {
        dao.seedRecipes(
            recipeLine(id = "recipe_1", menuItemId = "item_1").toEntity(),
            recipeLine(id = "recipe_2", menuItemId = "item_1").toEntity(),
            recipeLine(id = "recipe_3", menuItemId = "item_2").toEntity()
        )

        repository.clearRecipeLines("item_1")

        assertEquals(emptyList<RecipeLine>(), repository.observeRecipeLines("item_1").first())
        assertEquals(listOf("recipe_3"), repository.observeRecipeLines("item_2").first().map { it.id })
    }

    @Test
    fun `deleteRecipeLine removes a single line`() = runTest {
        dao.seedRecipes(
            recipeLine(id = "recipe_1", menuItemId = "item_1").toEntity(),
            recipeLine(id = "recipe_2", menuItemId = "item_1").toEntity()
        )

        repository.deleteRecipeLine("recipe_1")

        assertEquals(listOf("recipe_2"), repository.observeRecipeLines("item_1").first().map { it.id })
    }

    @Test
    fun `upsertRecipeLine round-trips through observeRecipeLines`() = runTest {
        val line = recipeLine(id = "recipe_9", menuItemId = "item_3")

        repository.upsertRecipeLine(line)

        assertEquals(listOf(line), repository.observeRecipeLines("item_3").first())
        assertNull(repository.getMenuItemById("unknown"))
    }
}
