package com.mishin.feature.cats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mishin.core.common.constants.Defaults
import com.mishin.core.common.util.DateTimeUtil
import com.mishin.core.common.util.generateId
import com.mishin.core.domain.model.Cat
import com.mishin.core.domain.model.CatSex
import com.mishin.core.domain.model.CatStatus
import com.mishin.core.domain.repository.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the state for the admin "Lista de gatitos" screen: the shelter cat
 * list (CRUD through [CatRepository]) shown under the adoptions section.
 */
@HiltViewModel
class CatsViewModel @Inject constructor(
    private val catRepository: CatRepository
) : ViewModel() {

    val cats: StateFlow<List<Cat>> = catRepository.observeCats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun saveCat(
        existing: Cat?,
        name: String,
        ageMonthsText: String,
        medicalCheck: Boolean,
        tripleVaccine: Boolean,
        sterilized: Boolean,
        notes: String,
        imageUri: String?
    ) {
        val trimmedName = name.trim()
        val ageMonths = ageMonthsText.trim().toIntOrNull()?.takeIf { it >= 0 }
        val hasAgeInput = ageMonthsText.isNotBlank()
        if (trimmedName.isEmpty() || (hasAgeInput && ageMonths == null)) {
            return
        }

        val now = DateTimeUtil.nowIso()
        val trimmedNotes = notes.trim()
        val photo = imageUri?.ifBlank { null }
        viewModelScope.launch {
            val cat = if (existing != null) {
                existing.copy(
                    name = trimmedName,
                    ageMonths = if (hasAgeInput) ageMonths else existing.ageMonths,
                    medicalCheck = medicalCheck,
                    tripleVaccine = tripleVaccine,
                    sterilized = sterilized,
                    notes = trimmedNotes.ifBlank { null },
                    photoUri = photo,
                    updatedAt = now
                )
            } else {
                Cat(
                    id = generateId(),
                    locationId = Defaults.DEFAULT_LOCATION_ID,
                    name = trimmedName,
                    ageMonths = ageMonths,
                    sex = CatSex.UNKNOWN,
                    photoUri = photo,
                    status = CatStatus.UP_FOR_ADOPTION,
                    intakeDate = now,
                    notes = trimmedNotes.ifBlank { null },
                    medicalCheck = medicalCheck,
                    tripleVaccine = tripleVaccine,
                    sterilized = sterilized,
                    createdAt = now,
                    updatedAt = now,
                    deletedAt = null,
                    syncedAt = null
                )
            }
            catRepository.upsertCat(cat)
        }
    }

    fun deleteCat(id: String) {
        viewModelScope.launch { catRepository.deleteCat(id) }
    }
}
