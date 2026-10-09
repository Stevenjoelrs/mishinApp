package com.mishin.core.data.repository

import com.mishin.core.data.mapper.toEntity
import com.mishin.core.domain.model.Cat
import com.mishin.core.domain.model.CatSex
import com.mishin.core.domain.model.CatStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CatRepositoryImplTest {

    private val dao = FakeCatDao()
    private val repository = CatRepositoryImpl(dao)

    private fun cat(
        id: String = "cat_1",
        name: String = "Misu",
        status: CatStatus = CatStatus.IN_SHELTER,
        sex: CatSex = CatSex.FEMALE,
        deletedAt: String? = null
    ) = Cat(
        id = id,
        locationId = "loc_1",
        name = name,
        ageMonths = 7,
        sex = sex,
        photoUri = "content://photos/$id",
        status = status,
        intakeDate = "2026-01-02T10:00:00Z",
        notes = "Cariñosa",
        medicalCheck = true,
        tripleVaccine = true,
        sterilized = false,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-02T00:00:00Z",
        deletedAt = deletedAt,
        syncedAt = null
    )

    @Test
    fun `observeCats maps entities and hides soft-deleted cats`() = runTest {
        dao.seed(
            cat(id = "cat_1", name = "Misu").toEntity(),
            cat(id = "cat_2", name = "Zoe", deletedAt = "2026-01-05T00:00:00Z").toEntity()
        )

        val cats = repository.observeCats().first()

        assertEquals(listOf("cat_1"), cats.map { it.id })
        assertEquals("Misu", cats.first().name)
        assertEquals(CatSex.FEMALE, cats.first().sex)
        assertEquals(7, cats.first().ageMonths)
    }

    @Test
    fun `observeCats filters by status`() = runTest {
        dao.seed(
            cat(id = "cat_1", name = "Misu", status = CatStatus.IN_SHELTER).toEntity(),
            cat(id = "cat_2", name = "Zoe", status = CatStatus.ADOPTED).toEntity()
        )

        val adopted = repository.observeCats(CatStatus.ADOPTED).first()

        assertEquals(listOf("cat_2"), adopted.map { it.id })
        assertEquals(CatStatus.ADOPTED, adopted.first().status)
    }

    @Test
    fun `getCatById maps a cat and returns null for unknown id`() = runTest {
        dao.seed(cat(id = "cat_1", name = "Misu").toEntity())

        assertEquals("Misu", repository.getCatById("cat_1")?.name)
        assertNull(repository.getCatById("missing"))
    }

    @Test
    fun `upsertCat persists the domain cat unchanged`() = runTest {
        val original = cat(id = "cat_9", name = "Nube", sex = CatSex.MALE)

        repository.upsertCat(original)

        assertEquals(original, repository.getCatById("cat_9"))
    }

    @Test
    fun `deleteCat soft-deletes with a timestamp`() = runTest {
        dao.seed(cat(id = "cat_1").toEntity())

        repository.deleteCat("cat_1")

        // Hidden from the observable list...
        assertEquals(emptyList<Cat>(), repository.observeCats().first())
        // ...but still readable by id with deletedAt stamped (matches SQL contract).
        val deleted = repository.getCatById("cat_1")
        assertNotNull(deleted)
        assertNotNull(deleted?.deletedAt)
        assertNotNull(deleted?.updatedAt)
    }

    @Test
    fun `updateCatStatus changes status and stamps updatedAt`() = runTest {
        dao.seed(cat(id = "cat_1", status = CatStatus.IN_SHELTER).toEntity())

        repository.updateCatStatus("cat_1", CatStatus.UP_FOR_ADOPTION)

        val updated = repository.getCatById("cat_1")
        assertEquals(CatStatus.UP_FOR_ADOPTION, updated?.status)
        assertNotNull(updated?.updatedAt)
        // Stamped with the current clock, so it must be newer than the seed value.
        assertTrue(updated!!.updatedAt > "2026-01-02T00:00:00Z")
    }

    @Test
    fun `observeCatCount counts only live cats`() = runTest {
        dao.seed(
            cat(id = "cat_1", name = "Misu").toEntity(),
            cat(id = "cat_2", name = "Zoe").toEntity(),
            cat(id = "cat_3", name = "Nube", deletedAt = "2026-01-05T00:00:00Z").toEntity()
        )

        assertEquals(2, repository.observeCatCount().first())
    }
}
