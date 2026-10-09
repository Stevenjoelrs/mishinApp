package com.mishin.feature.cats

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.mishin.core.common.constants.Defaults
import com.mishin.core.domain.model.CatSex
import com.mishin.core.domain.model.CatStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatsViewModelTest {

    private lateinit var catRepository: FakeCatRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        catRepository = FakeCatRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = CatsViewModel(catRepository)

    /** Reads items until [predicate] holds, so emission order never matters. */
    private suspend fun <T> TurbineTestContext<T>.awaitUntil(predicate: (T) -> Boolean): T {
        while (true) {
            val item = awaitItem()
            if (predicate(item)) return item
        }
    }

    // -- List state --

    @Test
    fun `cats flow mirrors the repository`() = runTest {
        catRepository.seed(shelterCat(name = "Misu"))
        val viewModel = viewModel()

        viewModel.cats.test {
            val cats = awaitUntil { it.isNotEmpty() }
            assertEquals(listOf("Misu"), cats.map { it.name })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cats flow starts empty when the repository has no cats`() {
        val viewModel = viewModel()

        assertTrue(viewModel.cats.value.isEmpty())
    }

    // -- Create --

    @Test
    fun `saveCat creates a new cat with defaults`() = runTest {
        val viewModel = viewModel()

        viewModel.saveCat(
            existing = null,
            name = "  Misu  ",
            ageMonthsText = "5",
            medicalCheck = true,
            tripleVaccine = true,
            sterilized = false,
            notes = "Muy cariñosa",
            imageUri = "content://photos/1"
        )

        val saved = catRepository.upserted.single()
        assertEquals("Misu", saved.name)
        assertEquals(5, saved.ageMonths)
        assertEquals(Defaults.DEFAULT_LOCATION_ID, saved.locationId)
        assertEquals(CatSex.UNKNOWN, saved.sex)
        assertEquals(CatStatus.UP_FOR_ADOPTION, saved.status)
        assertTrue(saved.id.isNotBlank())
        assertTrue(saved.intakeDate.isNotBlank())
        assertTrue(saved.createdAt.isNotBlank())
        assertEquals(saved.createdAt, saved.updatedAt)
        assertNull(saved.deletedAt)
        assertNull(saved.syncedAt)
        assertTrue(saved.medicalCheck)
        assertTrue(saved.tripleVaccine)
        assertFalse(saved.sterilized)
        assertEquals("Muy cariñosa", saved.notes)
        assertEquals("content://photos/1", saved.photoUri)
    }

    @Test
    fun `saveCat trims notes and drops blank notes and photo`() = runTest {
        val viewModel = viewModel()

        viewModel.saveCat(
            existing = null,
            name = "Luna",
            ageMonthsText = "",
            medicalCheck = false,
            tripleVaccine = false,
            sterilized = false,
            notes = "   ",
            imageUri = ""
        )

        val saved = catRepository.upserted.single()
        assertNull(saved.notes)
        assertNull(saved.photoUri)
    }

    @Test
    fun `saveCat keeps a blank age as null for a new cat`() = runTest {
        val viewModel = viewModel()

        viewModel.saveCat(
            existing = null,
            name = "Tom",
            ageMonthsText = "  ",
            medicalCheck = false,
            tripleVaccine = false,
            sterilized = false,
            notes = "",
            imageUri = null
        )

        val saved = catRepository.upserted.single()
        assertNull(saved.ageMonths)
    }

    @Test
    fun `saved cats show up in the cats flow`() = runTest {
        val viewModel = viewModel()

        viewModel.saveCat(
            existing = null,
            name = "Misu",
            ageMonthsText = "5",
            medicalCheck = false,
            tripleVaccine = false,
            sterilized = false,
            notes = "",
            imageUri = null
        )

        viewModel.cats.test {
            val cats = awaitUntil { it.isNotEmpty() }
            assertEquals(listOf("Misu"), cats.map { it.name })

            cancelAndIgnoreRemainingEvents()
        }
    }

    // -- Update --

    @Test
    fun `saveCat updates an existing cat keeping its identity`() = runTest {
        val existing = shelterCat(
            id = "cat_42",
            name = "Misu",
            ageMonths = 5,
            medicalCheck = false,
            photoUri = "content://photos/1"
        )
        val viewModel = viewModel()

        viewModel.saveCat(
            existing = existing,
            name = "  Luna  ",
            ageMonthsText = "7",
            medicalCheck = true,
            tripleVaccine = true,
            sterilized = true,
            notes = "Editada",
            imageUri = "content://photos/2"
        )

        val saved = catRepository.upserted.single()
        assertEquals(existing.id, saved.id)
        assertEquals(existing.createdAt, saved.createdAt)
        assertEquals(existing.locationId, saved.locationId)
        assertEquals(existing.intakeDate, saved.intakeDate)
        assertEquals("Luna", saved.name)
        assertEquals(7, saved.ageMonths)
        assertTrue(saved.medicalCheck)
        assertTrue(saved.tripleVaccine)
        assertTrue(saved.sterilized)
        assertEquals("Editada", saved.notes)
        assertEquals("content://photos/2", saved.photoUri)
        assertTrue(saved.updatedAt >= existing.updatedAt)
    }

    @Test
    fun `saveCat with a blank age keeps the existing age`() = runTest {
        val existing = shelterCat(id = "cat_42", name = "Misu", ageMonths = 5)
        val viewModel = viewModel()

        viewModel.saveCat(
            existing = existing,
            name = "Misu",
            ageMonthsText = "",
            medicalCheck = false,
            tripleVaccine = false,
            sterilized = false,
            notes = "",
            imageUri = null
        )

        val saved = catRepository.upserted.single()
        assertEquals(5, saved.ageMonths)
    }

    // -- Validation --

    @Test
    fun `saveCat ignores invalid input`() = runTest {
        val viewModel = viewModel()

        fun attempt(name: String, age: String) {
            viewModel.saveCat(
                existing = null,
                name = name,
                ageMonthsText = age,
                medicalCheck = false,
                tripleVaccine = false,
                sterilized = false,
                notes = "",
                imageUri = null
            )
        }

        attempt(name = "   ", age = "5") // blank name
        attempt(name = "Misu", age = "abc") // not a number
        attempt(name = "Misu", age = "-1") // negative months

        assertTrue(catRepository.upserted.isEmpty())
    }

    // -- Delete --

    @Test
    fun `deleteCat removes the cat from the repository`() = runTest {
        catRepository.seed(shelterCat(id = "cat_9"))
        val viewModel = viewModel()

        viewModel.deleteCat("cat_9")

        assertEquals(listOf("cat_9"), catRepository.deletedIds)
        assertNull(catRepository.getCatById("cat_9"))
        assertTrue(viewModel.cats.first().isEmpty())
    }
}
