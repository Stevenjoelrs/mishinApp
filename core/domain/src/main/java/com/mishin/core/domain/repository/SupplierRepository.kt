package com.mishin.core.domain.repository

import com.mishin.core.domain.model.Supplier
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for suppliers.
 */
interface SupplierRepository {

    fun observeSuppliers(): Flow<List<Supplier>>
    suspend fun getSupplierById(id: String): Supplier?
    suspend fun upsertSupplier(supplier: Supplier)
    suspend fun deleteSupplier(id: String)
}
