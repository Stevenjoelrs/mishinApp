package com.mishin.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mishin.core.database.entity.BoardGameEntity
import com.mishin.core.database.entity.GameLoanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM board_games WHERE deleted_at IS NULL ORDER BY name")
    fun observeAll(): Flow<List<BoardGameEntity>>

    @Query("SELECT * FROM board_games WHERE deleted_at IS NULL AND status = :status ORDER BY name")
    fun observeByStatus(status: String): Flow<List<BoardGameEntity>>

    @Query("""
        SELECT * FROM board_games 
        WHERE deleted_at IS NULL 
        AND status = 'AVAILABLE'
        AND min_players <= :playerCount 
        AND max_players >= :playerCount
        ORDER BY name
    """)
    fun findSuitableGames(playerCount: Int): Flow<List<BoardGameEntity>>

    @Query("""
        SELECT * FROM board_games 
        WHERE deleted_at IS NULL 
        AND status = 'AVAILABLE'
        AND min_players <= :playerCount 
        AND max_players >= :playerCount
        AND duration_minutes <= :maxDuration
        ORDER BY name
    """)
    fun findSuitableGamesWithDuration(playerCount: Int, maxDuration: Int): Flow<List<BoardGameEntity>>

    @Query("SELECT * FROM board_games WHERE id = :id")
    suspend fun getById(id: String): BoardGameEntity?

    @Upsert
    suspend fun upsert(game: BoardGameEntity)

    @Query("UPDATE board_games SET deleted_at = :deletedAt, updated_at = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: String, updatedAt: String)

    @Query("UPDATE board_games SET status = :status, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, updatedAt: String)

    // -- Loans --

    @Query("SELECT * FROM game_loans WHERE end_time IS NULL ORDER BY start_time DESC")
    fun observeActiveLoans(): Flow<List<GameLoanEntity>>

    @Query("SELECT * FROM game_loans WHERE id = :id")
    suspend fun getLoanById(id: String): GameLoanEntity?

    @Upsert
    suspend fun upsertLoan(loan: GameLoanEntity)

    @Query("UPDATE game_loans SET end_time = :endTime WHERE id = :loanId")
    suspend fun endLoan(loanId: String, endTime: String)
}
