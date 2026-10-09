package com.mishin.feature.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mishin.core.common.constants.Defaults
import com.mishin.core.common.util.DateTimeUtil
import com.mishin.core.common.util.generateId
import com.mishin.core.domain.model.BoardGame
import com.mishin.core.domain.model.GameDifficulty
import com.mishin.core.domain.model.GameStatus
import com.mishin.core.domain.repository.GameRepository
import com.mishin.core.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the state for the admin "Karaoke y juegos" screen: the board game
 * collection (CRUD through [GameRepository]) plus the editable karaoke card
 * and promo banner copy, both persisted as key-value settings.
 */
@HiltViewModel
class GamesViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val games: StateFlow<List<BoardGame>> = gameRepository.observeGames()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val karaokePrice: StateFlow<String> =
        observeSetting(KARAOKE_PRICE_KEY, DEFAULT_KARAOKE_PRICE)

    val karaokePeriodMinutes: StateFlow<String> =
        observeSetting(KARAOKE_PERIOD_KEY, DEFAULT_KARAOKE_PERIOD)

    val karaokeDescription: StateFlow<String> =
        observeSetting(KARAOKE_DESCRIPTION_KEY, DEFAULT_KARAOKE_DESCRIPTION)

    val gamesPromo: StateFlow<String> =
        observeSetting(GAMES_PROMO_KEY, DEFAULT_GAMES_PROMO)

    private fun observeSetting(key: String, default: String): StateFlow<String> =
        settingsRepository.observeValue(key)
            .map { it ?: default }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = default
            )

    fun saveGame(
        existing: BoardGame?,
        name: String,
        minPlayers: Int,
        maxPlayers: Int,
        durationMinutes: Int,
        difficulty: GameDifficulty,
        status: GameStatus,
        missingPieces: String?,
        imageUri: String?
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty() || minPlayers < 1 || maxPlayers < minPlayers || durationMinutes < 1) {
            return
        }

        val now = DateTimeUtil.nowIso()
        viewModelScope.launch {
            val game = if (existing != null) {
                existing.copy(
                    name = trimmedName,
                    minPlayers = minPlayers,
                    maxPlayers = maxPlayers,
                    durationMinutes = durationMinutes,
                    difficulty = difficulty,
                    status = status,
                    missingPieces = missingPieces,
                    imageUri = imageUri,
                    updatedAt = now
                )
            } else {
                BoardGame(
                    id = generateId(),
                    locationId = Defaults.DEFAULT_LOCATION_ID,
                    name = trimmedName,
                    minPlayers = minPlayers,
                    maxPlayers = maxPlayers,
                    durationMinutes = durationMinutes,
                    difficulty = difficulty,
                    status = status,
                    missingPieces = missingPieces,
                    imageUri = imageUri,
                    createdAt = now,
                    updatedAt = now,
                    deletedAt = null,
                    syncedAt = null
                )
            }
            gameRepository.upsertGame(game)
        }
    }

    fun deleteGame(id: String) {
        viewModelScope.launch { gameRepository.deleteGame(id) }
    }

    fun saveKaraoke(price: String, periodMinutes: String, description: String) {
        val parsedPrice = price.trim().replace(',', '.').toDoubleOrNull()
        val parsedPeriod = periodMinutes.trim().toIntOrNull()
        if (parsedPrice == null || parsedPrice < 0 || parsedPeriod == null || parsedPeriod < 1) {
            return
        }
        val trimmedDescription = description.trim()
        if (trimmedDescription.isEmpty()) return

        viewModelScope.launch {
            settingsRepository.setValue(KARAOKE_PRICE_KEY, formatSettingNumber(parsedPrice))
            settingsRepository.setValue(KARAOKE_PERIOD_KEY, parsedPeriod.toString())
            settingsRepository.setValue(KARAOKE_DESCRIPTION_KEY, trimmedDescription)
        }
    }

    fun savePromo(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { settingsRepository.setValue(GAMES_PROMO_KEY, trimmed) }
    }

    private fun formatSettingNumber(value: Double): String =
        if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()

    companion object {
        const val KARAOKE_PRICE_KEY = "karaoke_price_bs"
        const val KARAOKE_PERIOD_KEY = "karaoke_period_minutes"
        const val KARAOKE_DESCRIPTION_KEY = "karaoke_description"
        const val GAMES_PROMO_KEY = "games_promo"

        private const val DEFAULT_KARAOKE_PRICE = "3"
        private const val DEFAULT_KARAOKE_PERIOD = "30"
        private const val DEFAULT_KARAOKE_DESCRIPTION =
            "Canta tus canciones favoritas con amigos, sin importar el tono."
        private const val DEFAULT_GAMES_PROMO =
            "Si lo armas en menos de una hora, tu consumo es gratis (hasta de 4 personas)."
    }
}
