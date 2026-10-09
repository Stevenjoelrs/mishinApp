package com.mishin.core.data.repository

import com.mishin.core.common.util.DateTimeUtil
import com.mishin.core.database.dao.CatDao
import com.mishin.core.data.mapper.toDomain
import com.mishin.core.data.mapper.toEntity
import com.mishin.core.domain.model.Cat
import com.mishin.core.domain.model.CatStatus
import com.mishin.core.domain.repository.CatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Room-backed implementation of [CatRepository].
 * Exposes shelter cats as observable flows over the local database.
 */
class CatRepositoryImpl @Inject constructor(
    private val dao: CatDao
) : CatRepository {

    override fun observeCats(status: CatStatus?): Flow<List<Cat>> {
        val source = if (status != null) dao.observeByStatus(status.name) else dao.observeAll()
        return source.map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getCatById(id: String): Cat? = dao.getById(id)?.toDomain()

    override suspend fun upsertCat(cat: Cat) = dao.upsert(cat.toEntity())

    override suspend fun deleteCat(id: String) {
        val now = DateTimeUtil.nowIso()
        dao.softDelete(id, deletedAt = now, updatedAt = now)
    }

    override suspend fun updateCatStatus(id: String, status: CatStatus) {
        dao.updateStatus(id, status = status.name, updatedAt = DateTimeUtil.nowIso())
    }

    override fun observeCatCount(): Flow<Int> = dao.observeCount()
}
