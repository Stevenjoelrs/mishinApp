package com.mishin.core.data.repository

import com.mishin.core.data.mapper.toEntity
import com.mishin.core.database.entity.BoardGameEntity
import com.mishin.core.database.entity.GameLoanEntity
import com.mishin.core.domain.model.BoardGame
import com.mishin.core.domain.model.GameDifficulty
import com.mishin.core.domain.model.GameLoan
import com.mishin.core.domain.model.GameStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRepositoryImplTest {

    private val dao = FakeGameDao()
    private val repository = GameRepositoryImpl(dao)

    private fun game(
        id: String = "game_1",
        name: String = "Catan",
        minPlayers: Int = 2,
        maxPlayers: Int = 4,
        durationMinutes: Int = 90,
        status: GameStatus = GameStatus.AVAILABLE,
        deletedAt: String? = null
    ) = BoardGame(
        id = id,
        locationId = "loc_1",
        name = name,
        minPlayers = minPlayers,
        maxPlayers = maxPlayers,
        durationMinutes = durationMinutes,
        difficulty = GameDifficulty.MEDIUM,
        status = status,
        missingPieces = null,
        imageUri = null,
        createdAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-02T00:00:00Z",
        deletedAt = deletedAt,
        syncedAt = null
    )

    private fun loan(
        id: String = "loan_1",
        boardGameId: String = "game_1",
        endTime: String? = null
    ) = GameLoanEntity(
        id = id,
        locationId = "loc_1",
        boardGameId = boardGameId,
        tableReference = "T1",
        startTime = "2026-02-01T10:00:00Z",
        endTime = endTime,
        createdAt = "2026-02-01T10:00:00Z",
        syncedAt = null
    )

    @Test
    fun `observeGames maps entities and hides soft-deleted games`() = runTest {
        dao.seedGames(
            game(id = "game_1", name = "Catan").toEntity(),
            game(id = "game_2", name = "Monopoly", deletedAt = "2026-01-05T00:00:00Z").toEntity()
        )

        val games = repository.observeGames().first()

        assertEquals(listOf("game_1"), games.map { it.id })
        assertEquals("Catan", games.first().name)
    }

    @Test
    fun `observeGames filters by status`() = runTest {
        dao.seedGames(
            game(id = "game_1", name = "Catan", status = GameStatus.AVAILABLE).toEntity(),
            game(id = "game_2", name = "Risk", status = GameStatus.MAINTENANCE).toEntity()
        )

        val maintenance = repository.observeGames(GameStatus.MAINTENANCE).first()

        assertEquals(listOf("game_2"), maintenance.map { it.id })
    }

    @Test
    fun `findSuitableGames without duration uses the player-count query`() = runTest {
        dao.seedGames(
            game(id = "game_1", name = "Catan", minPlayers = 2, maxPlayers = 4, durationMinutes = 90).toEntity(),
            game(id = "game_2", name = "Dixit", minPlayers = 3, maxPlayers = 6, durationMinutes = 30).toEntity(),
            game(id = "game_3", name = "Risk", minPlayers = 2, maxPlayers = 5, durationMinutes = 120, status = GameStatus.MAINTENANCE).toEntity()
        )

        val suitable = repository.findSuitableGames(playerCount = 4, maxDurationMinutes = null).first()

        assertEquals(listOf("game_1", "game_2"), suitable.map { it.id })
        assertEquals(listOf("findSuitableGames(4)"), dao.suitableQueries)
    }

    @Test
    fun `findSuitableGames with duration uses the duration query and excludes longer games`() = runTest {
        dao.seedGames(
            game(id = "game_1", name = "Catan", minPlayers = 2, maxPlayers = 4, durationMinutes = 90).toEntity(),
            game(id = "game_2", name = "Dixit", minPlayers = 3, maxPlayers = 6, durationMinutes = 30).toEntity()
        )

        val suitable = repository.findSuitableGames(playerCount = 4, maxDurationMinutes = 60).first()

        assertEquals(listOf("game_2"), suitable.map { it.id })
        assertEquals(listOf("findSuitableGamesWithDuration(4, 60)"), dao.suitableQueries)
    }

    @Test
    fun `endLoan with unknown loan id is a no-op`() = runTest {
        dao.seedGames(game(id = "game_1", status = GameStatus.IN_USE).toEntity())

        repository.endLoan("missing")

        assertEquals(emptyList<String>(), dao.endedLoans)
        assertEquals(emptyList<Pair<String, String>>(), dao.statusUpdates)
    }

    @Test
    fun `endLoan closes the loan and marks the game available`() = runTest {
        dao.seedGames(game(id = "game_1", status = GameStatus.IN_USE).toEntity())
        dao.seedLoans(loan(id = "loan_1", boardGameId = "game_1", endTime = null))

        repository.endLoan("loan_1")

        assertEquals(listOf("loan_1"), dao.endedLoans)
        assertNotNull(dao.getLoanById("loan_1")?.endTime)
        assertEquals(listOf("game_1" to "AVAILABLE"), dao.statusUpdates)
        val available = repository.observeGames(GameStatus.AVAILABLE).first()
        assertEquals(listOf("game_1"), available.map { it.id })
    }

    @Test
    fun `observeActiveLoans maps open loans only`() = runTest {
        dao.seedLoans(
            loan(id = "loan_1", boardGameId = "game_1", endTime = null),
            loan(id = "loan_2", boardGameId = "game_2", endTime = "2026-02-01T12:00:00Z")
        )

        val active = repository.observeActiveLoans().first()

        assertEquals(listOf("loan_1"), active.map { it.id })
        assertEquals("T1", active.first().tableReference)
    }

    @Test
    fun `startLoan then observeActiveLoans round-trips the loan`() = runTest {
        val newLoan = GameLoan(
            id = "loan_9",
            locationId = "loc_1",
            boardGameId = "game_1",
            tableReference = "T7",
            startTime = "2026-03-01T10:00:00Z",
            endTime = null,
            createdAt = "2026-03-01T10:00:00Z",
            syncedAt = null
        )

        repository.startLoan(newLoan)

        val active = repository.observeActiveLoans().first()
        assertEquals(listOf("loan_9"), active.map { it.id })
        assertEquals("T7", active.first().tableReference)
    }

    @Test
    fun `deleteGame soft-deletes and hides the game`() = runTest {
        dao.seedGames(game(id = "game_1", name = "Catan").toEntity())

        repository.deleteGame("game_1")

        assertEquals(emptyList<BoardGame>(), repository.observeGames().first())
        val deleted = repository.getGameById("game_1")
        assertNotNull(deleted)
        assertNotNull(deleted?.deletedAt)
        assertTrue(deleted!!.updatedAt > "2026-01-02T00:00:00Z")
    }

    @Test
    fun `getGameById returns null for unknown id`() = runTest {
        assertNull(repository.getGameById("missing"))
    }
}
