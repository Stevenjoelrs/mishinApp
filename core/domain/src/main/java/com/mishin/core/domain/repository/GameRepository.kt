package com.mishin.core.domain.repository

import com.mishin.core.domain.model.BoardGame
import com.mishin.core.domain.model.GameLoan
import com.mishin.core.domain.model.GameStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for board games and loans.
 */
interface GameRepository {

    fun observeGames(status: GameStatus? = null): Flow<List<BoardGame>>
    suspend fun getGameById(id: String): BoardGame?
    suspend fun upsertGame(game: BoardGame)
    suspend fun deleteGame(id: String)

    /**
     * Find games suitable for a given group.
     * Filters by player count and optionally by max duration.
     */
    fun findSuitableGames(
        playerCount: Int,
        maxDurationMinutes: Int? = null
    ): Flow<List<BoardGame>>

    // -- Loans --

    fun observeActiveLoans(): Flow<List<GameLoan>>
    suspend fun startLoan(loan: GameLoan)

    /**
     * End a loan: sets the end time and returns the game to AVAILABLE.
     */
    suspend fun endLoan(loanId: String)
}
