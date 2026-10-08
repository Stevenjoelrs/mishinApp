package com.mishin.core.data.repository

import com.mishin.core.database.dao.CatDao
import com.mishin.core.domain.model.Cat
import com.mishin.core.domain.model.CatStatus
import com.mishin.core.domain.repository.CatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class CatRepositoryImpl @Inject constructor(
    private val dao: CatDao
) : CatRepository {
    override fun observeCats(status: CatStatus?): Flow<List<Cat>> = flowOf(emptyList())
    override suspend fun getCatById(id: String): Cat? = null
    override suspend fun upsertCat(cat: Cat) { /* TODO */ }
    override suspend fun deleteCat(id: String) { /* TODO */ }
    override suspend fun updateCatStatus(id: String, status: CatStatus) { /* TODO */ }
    override fun observeCatCount(): Flow<Int> = flowOf(0)
}
