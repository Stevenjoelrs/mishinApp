package com.mishin.core.data.repository

import com.mishin.core.database.dao.AlertDao
import com.mishin.core.database.dao.OrderDao
import com.mishin.core.database.dao.ReservationDao
import com.mishin.core.database.dao.SupplierDao
import com.mishin.core.database.dao.SyncQueueDao
import com.mishin.core.domain.model.OrderStatus
import com.mishin.core.domain.model.PaymentMethod
import com.mishin.core.domain.model.ReservationStatus
import com.mishin.core.domain.model.SyncOperation
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Contract tests for the Phase-2 stub repositories: they must return safe
 * empty defaults and complete their write methods without touching the DAO.
 */
class StubRepositoriesTest {

    // -- Orders --

    @Test
    fun `order stub returns empty defaults`() = runTest {
        val repository = OrderRepositoryImpl(mockk<OrderDao>(relaxed = true))

        assertEquals(emptyList<Any>(), repository.observeOrders().first())
        assertEquals(emptyList<Any>(), repository.observeOrders(OrderStatus.PAID).first())
        assertEquals(emptyList<Any>(), repository.observeTodayOrders().first())
        assertNull(repository.getOrderById("any"))
        assertEquals(emptyList<Any>(), repository.observeOrderItems("any").first())
        assertEquals(0.0, repository.getDailySalesTotal("2026-01-01"), 1e-9)
        assertEquals(0, repository.getDailyOrderCount("2026-01-01"))
    }

    @Test
    fun `order stub writes complete as no-ops`() = runTest {
        val repository = OrderRepositoryImpl(mockk<OrderDao>(relaxed = true))

        repository.createOrder(order = anyOrder(), items = emptyList())
        repository.markAsPaid("order_1", PaymentMethod.CASH)
        repository.cancelOrder("order_1")
    }

    // -- Reservations --

    @Test
    fun `reservation stub returns empty defaults and never reports conflicts`() = runTest {
        val repository = ReservationRepositoryImpl(mockk<ReservationDao>(relaxed = true))

        assertEquals(emptyList<Any>(), repository.observeResources().first())
        assertEquals(emptyList<Any>(), repository.observeReservations().first())
        assertEquals(emptyList<Any>(), repository.observeTodayReservations().first())
        assertNull(repository.getResourceById("any"))
        assertNull(repository.getReservationById("any"))
        assertFalse(
            repository.hasConflict(
                resourceId = "res_1",
                startTime = "2026-01-01T10:00:00Z",
                endTime = "2026-01-01T11:00:00Z",
                excludeReservationId = null
            )
        )
    }

    @Test
    fun `reservation stub writes complete as no-ops`() = runTest {
        val repository = ReservationRepositoryImpl(mockk<ReservationDao>(relaxed = true))

        repository.upsertResource(resource = anyResource())
        repository.deleteResource("res_1")
        repository.upsertReservation(reservation = anyReservation())
        repository.updateReservationStatus("rsv_1", ReservationStatus.CANCELLED)
        repository.deleteReservation("rsv_1")
    }

    // -- Suppliers --

    @Test
    fun `supplier stub returns empty defaults and writes complete as no-ops`() = runTest {
        val repository = SupplierRepositoryImpl(mockk<SupplierDao>(relaxed = true))

        assertEquals(emptyList<Any>(), repository.observeSuppliers().first())
        assertNull(repository.getSupplierById("any"))
        repository.upsertSupplier(supplier = anySupplier())
        repository.deleteSupplier("sup_1")
    }

    // -- Sync queue --

    @Test
    fun `sync stub returns empty defaults and writes complete as no-ops`() = runTest {
        val repository = SyncRepositoryImpl(mockk<SyncQueueDao>(relaxed = true))

        assertEquals(emptyList<Any>(), repository.observePendingEntries().first())
        assertEquals(0, repository.getPendingCount())
        repository.enqueue(tableName = "cats", recordId = "cat_1", operation = SyncOperation.INSERT)
        repository.markSynced("entry_1")
        repository.markFailed("entry_1", error = "boom")
        repository.retryFailed()
        repository.clearSynced()
    }

    // -- Alerts --

    @Test
    fun `alert stub returns empty defaults with zero counts`() = runTest {
        val repository = AlertRepositoryImpl(mockk<AlertDao>(relaxed = true))

        assertEquals(emptyList<Any>(), repository.observeUnreadAlerts().first())
        assertEquals(emptyList<Any>(), repository.observeAllAlerts().first())
        assertEquals(0, repository.getUnreadCount())
        assertEquals(0, repository.observeUnreadCount().first())
        assertFalse(repository.hasActiveAlertForItem("inv_1"))
    }

    @Test
    fun `alert stub writes complete as no-ops`() = runTest {
        val repository = AlertRepositoryImpl(mockk<AlertDao>(relaxed = true))

        repository.createAlert(alert = anyAlert())
        repository.markAsRead("alert_1")
        repository.markAllAsRead()
    }

    // Minimal domain fixtures just to satisfy the write signatures.

    private fun anyOrder() = com.mishin.core.domain.model.Order(
        id = "order_1",
        locationId = "loc_1",
        status = OrderStatus.OPEN,
        tableNumber = "T1",
        total = 10.0,
        paymentMethod = null,
        notes = null,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-01T00:00:00Z",
        deletedAt = null,
        syncedAt = null
    )

    private fun anyResource() = com.mishin.core.domain.model.Resource(
        id = "res_1",
        locationId = "loc_1",
        name = "Mesa 1",
        type = com.mishin.core.domain.model.ResourceType.TABLE,
        capacity = 4,
        isActive = true,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-01T00:00:00Z",
        deletedAt = null,
        syncedAt = null
    )

    private fun anyReservation() = com.mishin.core.domain.model.Reservation(
        id = "rsv_1",
        locationId = "loc_1",
        resourceId = "res_1",
        customerName = "Ana",
        customerContact = null,
        numberOfPeople = 2,
        startTime = "2026-01-01T10:00:00Z",
        endTime = "2026-01-01T11:00:00Z",
        status = ReservationStatus.CONFIRMED,
        notes = null,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-01T00:00:00Z",
        deletedAt = null,
        syncedAt = null
    )

    private fun anySupplier() = com.mishin.core.domain.model.Supplier(
        id = "sup_1",
        locationId = "loc_1",
        name = "Café Exportadores",
        contactPhone = "555-0001",
        contactEmail = "ventas@cafe.example",
        notes = null,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-01T00:00:00Z",
        deletedAt = null,
        syncedAt = null
    )

    private fun anyAlert() = com.mishin.core.domain.model.Alert(
        id = "alert_1",
        locationId = "loc_1",
        type = com.mishin.core.domain.model.AlertType.LOW_STOCK,
        inventoryItemId = "inv_1",
        message = "Stock bajo",
        isRead = false,
        createdAt = "2026-01-01T00:00:00Z",
        syncedAt = null
    )
}
