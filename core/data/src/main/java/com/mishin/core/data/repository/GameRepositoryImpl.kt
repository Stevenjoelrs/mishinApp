package com.mishin.core.data.repository

import com.mishin.core.database.dao.GameDao
import com.mishin.core.domain.model.*
import com.mishin.core.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val dao: GameDao
) : GameRepository {
    override fun observeGames(status: GameStatus?): Flow<List<BoardGame>> = flowOf(emptyList())
    override suspend fun getGameById(id: String): BoardGame? = null
    override suspend fun upsertGame(game: BoardGame) { /* TODO */ }
    override suspend fun deleteGame(id: String) { /* TODO */ }
    override fun findSuitableGames(playerCount: Int, maxDurationMinutes: Int?): Flow<List<BoardGame>> = flowOf(emptyList())
    override fun observeActiveLoans(): Flow<List<GameLoan>> = flowOf(emptyList())
    override suspend fun startLoan(loan: GameLoan) { /* TODO */ }
    override suspend fun endLoan(loanId: String) { /* TODO */ }
}
