package com.mishin.core.data.repository

import com.mishin.core.data.mapper.toEntity
import com.mishin.core.domain.model.BaseUnit
import com.mishin.core.domain.model.InventoryItem
import com.mishin.core.domain.model.ItemCategory
import com.mishin.core.domain.model.MovementType
import com.mishin.core.domain.model.StockMovement
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class InventoryRepositoryImplTest {

    private val dao = FakeInventoryDao()
    private val repository = InventoryRepositoryImpl(dao)

    private fun item(
        id: String = "inv_1",
        name: String = "Café",
        category: ItemCategory = ItemCategory.CAFE,
        maxStock: Double = 100.0,
        alertThresholdPercent: Int = 25
    ) = InventoryItem(
        id = id,
        locationId = "loc_1",
        name = name,
        category = category,
        baseUnit = BaseUnit.GRAMS,
        maxStock = maxStock,
        alertThresholdPercent = alertThresholdPercent,
        supplierId = null,
        costPerUnit = 0.08,
        notes = null,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-02T00:00:00Z",
        deletedAt = null,
        syncedAt = null
    )

    private fun movement(
        id: String = "mov_1",
        inventoryItemId: String = "inv_1",
        quantity: Double = 10.0,
        createdAt: String = "2026-01-02T00:00:00Z"
    ) = StockMovement(
        id = id,
        locationId = "loc_1",
        inventoryItemId = inventoryItemId,
        quantity = quantity,
        type = MovementType.PURCHASE,
        referenceOrderId = null,
        note = null,
        createdAt = createdAt,
        syncedAt = null
    )

    @Test
    fun `observeItems maps and hides soft-deleted items`() = runTest {
        dao.seedItems(
            item(id = "inv_1", name = "Café").toEntity(),
            item(id = "inv_2", name = "Leche").toEntity().copy(deletedAt = "2026-01-05T00:00:00Z")
        )

        val items = repository.observeItems().first()

        assertEquals(listOf("inv_1"), items.map { it.id })
        assertEquals("Café", items.first().name)
        assertEquals(ItemCategory.CAFE, items.first().category)
        assertEquals(BaseUnit.GRAMS, items.first().baseUnit)
    }

    @Test
    fun `observeItems filters by category`() = runTest {
        dao.seedItems(
            item(id = "inv_1", name = "Café", category = ItemCategory.CAFE).toEntity(),
            item(id = "inv_2", name = "Arena", category = ItemCategory.SHELTER).toEntity()
        )

        val shelter = repository.observeItems(ItemCategory.SHELTER).first()

        assertEquals(listOf("inv_2"), shelter.map { it.id })
    }

    @Test
    fun `upsertItem then getItemById round-trips`() = runTest {
        val original = item(id = "inv_9", name = "Azúcar")

        repository.upsertItem(original)

        assertEquals(original, repository.getItemById("inv_9"))
    }

    @Test
    fun `deleteItem soft-deletes and hides the item from the list`() = runTest {
        dao.seedItems(item(id = "inv_1").toEntity())

        repository.deleteItem("inv_1")

        assertEquals(emptyList<InventoryItem>(), repository.observeItems().first())
        val deleted = repository.getItemById("inv_1")
        assertNotNull(deleted)
        assertNotNull(deleted?.deletedAt)
    }

    @Test
    fun `getCurrentStock sums all movement quantities`() = runTest {
        dao.seedMovements(
            movement(id = "mov_1", quantity = 40.0).toEntity(),
            movement(id = "mov_2", quantity = -15.0).toEntity(),
            movement(id = "mov_3", inventoryItemId = "inv_other", quantity = 99.0).toEntity()
        )

        assertEquals(25.0, repository.getCurrentStock("inv_1"), 1e-9)
        assertEquals(0.0, repository.getCurrentStock("missing"), 1e-9)
    }

    @Test
    fun `observeCurrentStock emits the sum reactively`() = runTest {
        dao.seedMovements(movement(id = "mov_1", quantity = 10.0).toEntity())

        assertEquals(10.0, repository.observeCurrentStock("inv_1").first(), 1e-9)
    }

    @Test
    fun `observeItemsWithStock pairs each item with its stock`() = runTest {
        dao.seedItems(
            item(id = "inv_1", name = "Café").toEntity(),
            item(id = "inv_2", name = "Leche").toEntity()
        )
        dao.seedMovements(
            movement(id = "mov_1", inventoryItemId = "inv_1", quantity = 30.0).toEntity()
        )

        val pairs = repository.observeItemsWithStock().first()

        assertEquals(2, pairs.size)
        val byId = pairs.associate { (i, stock) -> i.id to stock }
        assertEquals(30.0, byId.getValue("inv_1"), 1e-9)
        assertEquals(0.0, byId.getValue("inv_2"), 1e-9)
    }

    @Test
    fun `observeLowStockItems includes items at or below the threshold and excludes above`() = runTest {
        dao.seedItems(
            // threshold 25% of 100 = 25 -> stock 25 is exactly at the threshold (inclusive).
            item(id = "inv_1", name = "Café", maxStock = 100.0, alertThresholdPercent = 25).toEntity(),
            // stock 50 > threshold 25 -> excluded.
            item(id = "inv_2", name = "Leche", maxStock = 100.0, alertThresholdPercent = 25).toEntity(),
            // no movements -> stock 0 -> included.
            item(id = "inv_3", name = "Azúcar", maxStock = 100.0, alertThresholdPercent = 25).toEntity()
        )
        dao.seedMovements(
            movement(id = "mov_1", inventoryItemId = "inv_1", quantity = 40.0).toEntity(),
            movement(id = "mov_2", inventoryItemId = "inv_1", quantity = -15.0).toEntity(),
            movement(id = "mov_3", inventoryItemId = "inv_2", quantity = 50.0).toEntity()
        )

        val lowStock = repository.observeLowStockItems().first()

        assertEquals(listOf("inv_1", "inv_3"), lowStock.map { (i, _) -> i.id }.sorted())
        assertEquals(25.0, lowStock.first { it.first.id == "inv_1" }.second, 1e-9)
    }

    @Test
    fun `observeMovements returns the item movements newest first`() = runTest {
        dao.seedMovements(
            movement(id = "mov_1", quantity = 10.0, createdAt = "2026-01-02T00:00:00Z").toEntity(),
            movement(id = "mov_2", quantity = -5.0, createdAt = "2026-01-03T00:00:00Z").toEntity(),
            movement(id = "mov_3", inventoryItemId = "inv_other", quantity = 7.0, createdAt = "2026-01-04T00:00:00Z").toEntity()
        )

        val movements = repository.observeMovements("inv_1").first()

        assertEquals(listOf("mov_2", "mov_1"), movements.map { it.id })
        assertEquals(-5.0, movements.first().quantity, 1e-9)
        assertEquals(MovementType.PURCHASE, movements.first().type)
    }
}
