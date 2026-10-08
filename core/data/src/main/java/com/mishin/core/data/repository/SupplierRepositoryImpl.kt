package com.mishin.core.data.repository

import com.mishin.core.database.dao.SupplierDao
import com.mishin.core.domain.model.Supplier
import com.mishin.core.domain.repository.SupplierRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class SupplierRepositoryImpl @Inject constructor(
    private val dao: SupplierDao
) : SupplierRepository {
    override fun observeSuppliers(): Flow<List<Supplier>> = flowOf(emptyList())
    override suspend fun getSupplierById(id: String): Supplier? = null
    override suspend fun upsertSupplier(supplier: Supplier) { /* TODO */ }
    override suspend fun deleteSupplier(id: String) { /* TODO */ }
}
