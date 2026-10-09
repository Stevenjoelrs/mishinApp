package com.mishin.core.data.repository

import com.mishin.core.common.util.DateTimeUtil
import com.mishin.core.database.dao.SettingsDao
import com.mishin.core.database.entity.AppSettingEntity
import com.mishin.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Room-backed implementation of [SettingsRepository].
 * Stores each setting as a row in the app_settings key-value table.
 */
class SettingsRepositoryImpl @Inject constructor(
    private val dao: SettingsDao
) : SettingsRepository {

    override fun observeValue(key: String): Flow<String?> =
        dao.observeByKey(key).map { it?.value }

    override suspend fun getValue(key: String): String? = dao.getValue(key)?.value

    override suspend fun setValue(key: String, value: String) {
        dao.upsert(
            AppSettingEntity(
                key = key,
                value = value,
                updatedAt = DateTimeUtil.nowIso()
            )
        )
    }
}
