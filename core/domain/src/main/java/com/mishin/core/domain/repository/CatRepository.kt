package com.mishin.core.domain.repository

import com.mishin.core.domain.model.Cat
import com.mishin.core.domain.model.CatStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for shelter cats.
 */
interface CatRepository {

    fun observeCats(status: CatStatus? = null): Flow<List<Cat>>
    suspend fun getCatById(id: String): Cat?
    suspend fun upsertCat(cat: Cat)
    suspend fun deleteCat(id: String)
    suspend fun updateCatStatus(id: String, status: CatStatus)
    fun observeCatCount(): Flow<Int>
}
