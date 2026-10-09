package com.mishin.core.data.repository

import com.mishin.core.common.util.DateTimeUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SettingsRepositoryImplTest {

    private val dao = FakeSettingsDao()
    private val repository = SettingsRepositoryImpl(dao)

    @Test
    fun `setValue stores the value and getValue returns it`() = runTest {
        repository.setValue("karaoke_price_per_hour", "25.0")

        assertEquals("25.0", repository.getValue("karaoke_price_per_hour"))
    }

    @Test
    fun `getValue returns null for an unknown key`() = runTest {
        assertNull(repository.getValue("missing"))
    }

    @Test
    fun `observeValue emits the stored value and null for a missing key`() = runTest {
        assertEquals(null, repository.observeValue("missing").first())

        repository.setValue("promo_copy", "2x1 en mojitos")

        assertEquals("2x1 en mojitos", repository.observeValue("promo_copy").first())
    }

    @Test
    fun `setValue stamps a parseable updatedAt`() = runTest {
        repository.setValue("karaoke_price_per_hour", "25.0")

        val setting = dao.getValue("karaoke_price_per_hour")
        assertNotNull(setting)
        // Must be a valid ISO instant, proving DateTimeUtil.nowIso() was used.
        val parsed = DateTimeUtil.parseInstant(setting!!.updatedAt)
        assertNotNull(parsed)
    }

    @Test
    fun `setValue overwrites the previous value`() = runTest {
        repository.setValue("key", "first")
        repository.setValue("key", "second")

        assertEquals("second", repository.getValue("key"))
    }
}
