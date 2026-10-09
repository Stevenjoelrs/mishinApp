package com.mishin.core.data.repository

import com.mishin.core.common.util.DateTimeUtil
import com.mishin.core.database.dao.GameDao
import com.mishin.core.data.mapper.toDomain
import com.mishin.core.data.mapper.toEntity
import com.mishin.core.domain.model.BoardGame
import com.mishin.core.domain.model.GameLoan
import com.mishin.core.domain.model.GameStatus
import com.mishin.core.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Room-backed implementation of [GameRepository].
 * Exposes board games and loans as observable flows over the local database.
 */
class GameRepositoryImpl @Inject constructor(
    private val dao: GameDao
) : GameRepository {

    override fun observeGames(status: GameStatus?): Flow<List<BoardGame>> {
        val source = if (status != null) dao.observeByStatus(status.name) else dao.observeAll()
        return source.map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getGameById(id: String): BoardGame? = dao.getById(id)?.toDomain()

    override suspend fun upsertGame(game: BoardGame) = dao.upsert(game.toEntity())

    override suspend fun deleteGame(id: String) {
        val now = DateTimeUtil.nowIso()
        dao.softDelete(id, deletedAt = now, updatedAt = now)
    }

    override fun findSuitableGames(
        playerCount: Int,
        maxDurationMinutes: Int?
    ): Flow<List<BoardGame>> {
        val source = if (maxDurationMinutes != null) {
            dao.findSuitableGamesWithDuration(playerCount, maxDurationMinutes)
        } else {
            dao.findSuitableGames(playerCount)
        }
        return source.map { entities -> entities.map { it.toDomain() } }
    }

    // -- Loans --

    override fun observeActiveLoans(): Flow<List<GameLoan>> =
        dao.observeActiveLoans().map { entities -> entities.map { it.toDomain() } }

    override suspend fun startLoan(loan: GameLoan) = dao.upsertLoan(loan.toEntity())

    override suspend fun endLoan(loanId: String) {
        val loan = dao.getLoanById(loanId) ?: return
        val now = DateTimeUtil.nowIso()
        dao.endLoan(loanId, endTime = now)
        dao.updateStatus(loan.boardGameId, status = GameStatus.AVAILABLE.name, updatedAt = now)
    }
}
