package com.mishin.feature.cats

import com.mishin.core.domain.model.Cat
import com.mishin.core.domain.model.CatSex
import com.mishin.core.domain.model.CatStatus
import com.mishin.core.domain.repository.CatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory [CatRepository] that records every write so tests can assert
 * what the view model passed through.
 */
class FakeCatRepository : CatRepository {

    private val cats = MutableStateFlow<List<Cat>>(emptyList())

    val upserted = mutableListOf<Cat>()
    val deletedIds = mutableListOf<String>()

    fun seed(vararg items: Cat) {
        cats.value = items.toList()
    }

    override fun observeCats(status: CatStatus?): Flow<List<Cat>> =
        cats.map { list ->
            if (status == null) list else list.filter { it.status == status }
        }

    override suspend fun getCatById(id: String): Cat? =
        cats.value.firstOrNull { it.id == id }

    override suspend fun upsertCat(cat: Cat) {
        upserted += cat
        cats.value = cats.value.filterNot { it.id == cat.id } + cat
    }

    override suspend fun deleteCat(id: String) {
        deletedIds += id
        cats.value = cats.value.filterNot { it.id == id }
    }

    override suspend fun updateCatStatus(id: String, status: CatStatus) {
        cats.value = cats.value.map { if (it.id == id) it.copy(status = status) else it }
    }

    override fun observeCatCount(): Flow<Int> = cats.map { it.size }
}

/** Builds a [Cat] with sensible defaults for test fixtures. */
fun shelterCat(
    id: String = "cat_1",
    name: String = "Misu",
    ageMonths: Int? = 5,
    photoUri: String? = null,
    status: CatStatus = CatStatus.UP_FOR_ADOPTION,
    notes: String? = null,
    medicalCheck: Boolean = false,
    tripleVaccine: Boolean = false,
    sterilized: Boolean = false,
    createdAt: String = "2026-01-01T00:00:00Z",
    updatedAt: String = "2026-01-01T00:00:00Z"
): Cat = Cat(
    id = id,
    locationId = "loc_mishin_main",
    name = name,
    ageMonths = ageMonths,
    sex = CatSex.UNKNOWN,
    photoUri = photoUri,
    status = status,
    intakeDate = createdAt,
    notes = notes,
    medicalCheck = medicalCheck,
    tripleVaccine = tripleVaccine,
    sterilized = sterilized,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = null,
    syncedAt = null
)
