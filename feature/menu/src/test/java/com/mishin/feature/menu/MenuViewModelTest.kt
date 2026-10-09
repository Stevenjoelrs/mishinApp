package com.mishin.feature.menu

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.mishin.core.common.constants.Defaults
import com.mishin.core.domain.model.MenuCategory
import com.mishin.core.domain.model.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {

    private lateinit var repository: FakeMenuRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = FakeMenuRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = MenuViewModel(repository)

    /** Reads items until [predicate] holds, so emission order never matters. */
    private suspend fun <T> TurbineTestContext<T>.awaitUntil(predicate: (T) -> Boolean): T {
        while (true) {
            val item = awaitItem()
            if (predicate(item)) return item
        }
    }

    private fun category(
        id: String = "cat_1",
        name: String = "Bebidas",
        sortOrder: Int = 1
    ) = MenuCategory(
        id = id,
        locationId = Defaults.DEFAULT_LOCATION_ID,
        name = name,
        description = null,
        sortOrder = sortOrder,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-01T00:00:00Z",
        deletedAt = null,
        syncedAt = null
    )

    private fun product(
        id: String = "item_1",
        name: String = "Latte",
        price: Double = 12.5,
        categoryId: String? = "cat_1",
        isActive: Boolean = true
    ) = MenuItem(
        id = id,
        locationId = Defaults.DEFAULT_LOCATION_ID,
        name = name,
        price = price,
        categoryId = categoryId,
        description = null,
        imageUri = null,
        isActive = isActive,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-01T00:00:00Z",
        deletedAt = null,
        syncedAt = null
    )

    // -- List state --

    @Test
    fun `categories state mirrors the repository`() = runTest {
        repository.seedCategories(category(id = "cat_1", name = "Bebidas"), category(id = "cat_2", name = "Comida"))
        val viewModel = viewModel()

        viewModel.categories.test {
            val categories = awaitUntil { it.isNotEmpty() }
            assertEquals(listOf("cat_1", "cat_2"), categories.map { it.id })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `products state mirrors the repository`() = runTest {
        repository.seedItems(product(id = "item_1", name = "Latte"), product(id = "item_2", name = "Tostada"))
        val viewModel = viewModel()

        viewModel.products.test {
            val products = awaitUntil { it.isNotEmpty() }
            assertEquals(listOf("item_1", "item_2"), products.map { it.id })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state starts empty when the repository has no data`() = runTest {
        val viewModel = viewModel()

        viewModel.categories.test {
            assertEquals(emptyList<MenuCategory>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.products.test {
            assertEquals(emptyList<MenuItem>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -- saveCategory --

    @Test
    fun `saveCategory creates a category with defaults`() = runTest {
        val viewModel = viewModel()

        viewModel.saveCategory(existing = null, name = "Postres", description = null)

        val created = repository.writtenCategories.single()
        assertTrue(created.id.isNotBlank())
        assertEquals(Defaults.DEFAULT_LOCATION_ID, created.locationId)
        assertEquals("Postres", created.name)
        assertNull(created.description)
        assertEquals(1, created.sortOrder)
        assertNull(created.deletedAt)
        assertNotNull(created.createdAt)
    }

    @Test
    fun `saveCategory trims the name and turns a blank description into null`() = runTest {
        val viewModel = viewModel()

        viewModel.saveCategory(existing = null, name = "  Postres  ", description = "   ")

        val created = repository.writtenCategories.single()
        assertEquals("Postres", created.name)
        assertNull(created.description)
    }

    @Test
    fun `saveCategory with a blank name is ignored`() = runTest {
        val viewModel = viewModel()

        viewModel.saveCategory(existing = null, name = "   ", description = "x")

        assertTrue(repository.writtenCategories.isEmpty())
        assertTrue(repository.actions.isEmpty())
    }

    @Test
    fun `saveCategory assigns sortOrder after the highest existing category`() = runTest {
        repository.seedCategories(category(id = "cat_1", sortOrder = 3), category(id = "cat_2", sortOrder = 5))
        val viewModel = viewModel()

        // The StateFlow is WhileSubscribed: keep a subscriber active so
        // categories.value reflects the seed before the save.
        viewModel.categories.test {
            awaitUntil { it.isNotEmpty() }

            viewModel.saveCategory(existing = null, name = "Postres", description = null)

            assertEquals(6, repository.writtenCategories.single().sortOrder)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveCategory updates an existing category keeping its identity`() = runTest {
        val existing = category(id = "cat_7", name = "Bebidas", sortOrder = 4)
        repository.seedCategories(existing)
        val viewModel = viewModel()

        viewModel.saveCategory(existing = existing, name = "Bebidas frías", description = "Verano")

        val updated = repository.writtenCategories.single()
        assertEquals("cat_7", updated.id)
        assertEquals(existing.createdAt, updated.createdAt)
        assertEquals(4, updated.sortOrder)
        assertEquals(existing.locationId, updated.locationId)
        assertEquals("Bebidas frías", updated.name)
        assertEquals("Verano", updated.description)
        assertTrue(updated.updatedAt >= existing.updatedAt)
    }

    // -- saveProduct --

    @Test
    fun `saveProduct creates a product with defaults`() = runTest {
        val viewModel = viewModel()

        viewModel.saveProduct(
            existing = null,
            name = "Mocha",
            price = 15.0,
            categoryId = "cat_1",
            description = "  ",
            imageUri = null
        )

        val created = repository.writtenItems.single()
        assertTrue(created.id.isNotBlank())
        assertEquals(Defaults.DEFAULT_LOCATION_ID, created.locationId)
        assertEquals("Mocha", created.name)
        assertEquals(15.0, created.price, 1e-9)
        assertEquals("cat_1", created.categoryId)
        assertNull(created.description)
        assertTrue(created.isActive)
        assertNull(created.deletedAt)
    }

    @Test
    fun `saveProduct with a blank name or negative price is ignored`() = runTest {
        val viewModel = viewModel()

        viewModel.saveProduct(existing = null, name = "   ", price = 10.0, categoryId = null, description = null, imageUri = null)
        viewModel.saveProduct(existing = null, name = "Mocha", price = -1.0, categoryId = null, description = null, imageUri = null)

        assertTrue(repository.writtenItems.isEmpty())
        assertTrue(repository.actions.isEmpty())
    }

    @Test
    fun `saveProduct accepts a zero price`() = runTest {
        val viewModel = viewModel()

        viewModel.saveProduct(existing = null, name = "Gratis", price = 0.0, categoryId = null, description = null, imageUri = null)

        assertEquals(0.0, repository.writtenItems.single().price, 1e-9)
    }

    @Test
    fun `saveProduct updates an existing product keeping its identity`() = runTest {
        val existing = product(id = "item_7", name = "Latte", price = 12.5, categoryId = "cat_1")
        repository.seedItems(existing)
        val viewModel = viewModel()

        viewModel.saveProduct(
            existing = existing,
            name = "Latte grande",
            price = 18.0,
            categoryId = "cat_2",
            description = "350ml",
            imageUri = "content://images/latte"
        )

        val updated = repository.writtenItems.single()
        assertEquals("item_7", updated.id)
        assertEquals(existing.createdAt, updated.createdAt)
        assertEquals(existing.locationId, updated.locationId)
        assertEquals("Latte grande", updated.name)
        assertEquals(18.0, updated.price, 1e-9)
        assertEquals("cat_2", updated.categoryId)
        assertEquals("350ml", updated.description)
        assertEquals("content://images/latte", updated.imageUri)
        assertTrue(updated.updatedAt >= existing.updatedAt)
    }

    // -- Deletes --

    @Test
    fun `deleteProduct clears recipe lines before deleting the item`() = runTest {
        val viewModel = viewModel()

        viewModel.deleteProduct("item_1")

        assertEquals(listOf("clearRecipeLines:item_1", "deleteMenuItem:item_1"), repository.actions)
    }

    @Test
    fun `deleteCategory delegates to the repository`() = runTest {
        val viewModel = viewModel()

        viewModel.deleteCategory("cat_1")

        assertEquals(listOf("deleteCategory:cat_1"), repository.actions)
    }
}
