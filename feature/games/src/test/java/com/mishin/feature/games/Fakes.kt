package com.mishin.feature.games

import com.mishin.core.domain.model.BoardGame
import com.mishin.core.domain.model.GameDifficulty
import com.mishin.core.domain.model.GameLoan
import com.mishin.core.domain.model.GameStatus
import com.mishin.core.domain.repository.GameRepository
import com.mishin.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * In-memory [GameRepository] that records every write so tests can assert
 * what the view model passed through.
 */
class FakeGameRepository : GameRepository {

    private val games = MutableStateFlow<List<BoardGame>>(emptyList())

    val upserted = mutableListOf<BoardGame>()
    val deletedIds = mutableListOf<String>()

    fun seed(vararg items: BoardGame) {
        games.value = items.toList()
    }

    override fun observeGames(status: GameStatus?): Flow<List<BoardGame>> =
        games.map { list ->
            if (status == null) list else list.filter { it.status == status }
        }

    override suspend fun getGameById(id: String): BoardGame? =
        games.value.firstOrNull { it.id == id }

    override suspend fun upsertGame(game: BoardGame) {
        upserted += game
        games.value = games.value.filterNot { it.id == game.id } + game
    }

    override suspend fun deleteGame(id: String) {
        deletedIds += id
        games.value = games.value.filterNot { it.id == id }
    }

    override fun findSuitableGames(
        playerCount: Int,
        maxDurationMinutes: Int?
    ): Flow<List<BoardGame>> =
        games.map { list ->
            list.filter { game ->
                game.minPlayers <= playerCount &&
                    game.maxPlayers >= playerCount &&
                    (maxDurationMinutes == null || game.durationMinutes <= maxDurationMinutes)
            }
        }

    override fun observeActiveLoans(): Flow<List<GameLoan>> = flowOf(emptyList())

    override suspend fun startLoan(loan: GameLoan) = Unit

    override suspend fun endLoan(loanId: String) = Unit
}

/**
 * In-memory [SettingsRepository] backed by a single map, recording every write.
 */
class FakeSettingsRepository : SettingsRepository {

    private val state = MutableStateFlow<Map<String, String>>(emptyMap())

    val writes = mutableListOf<Pair<String, String>>()

    fun seed(key: String, value: String) {
        state.value = state.value + (key to value)
    }

    override fun observeValue(key: String): Flow<String?> = state.map { it[key] }

    override suspend fun getValue(key: String): String? = state.value[key]

    override suspend fun setValue(key: String, value: String) {
        writes += key to value
        state.value = state.value + (key to value)
    }
}

/** Builds a [BoardGame] with sensible defaults for test fixtures. */
fun boardGame(
    id: String = "game_1",
    name: String = "Catán",
    minPlayers: Int = 3,
    maxPlayers: Int = 4,
    durationMinutes: Int = 60,
    difficulty: GameDifficulty = GameDifficulty.MEDIUM,
    status: GameStatus = GameStatus.AVAILABLE,
    missingPieces: String? = null,
    imageUri: String? = null,
    createdAt: String = "2026-01-01T00:00:00Z",
    updatedAt: String = "2026-01-01T00:00:00Z"
): BoardGame = BoardGame(
    id = id,
    locationId = "loc_mishin_main",
    name = name,
    minPlayers = minPlayers,
    maxPlayers = maxPlayers,
    durationMinutes = durationMinutes,
    difficulty = difficulty,
    status = status,
    missingPieces = missingPieces,
    imageUri = imageUri,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = null,
    syncedAt = null
)
