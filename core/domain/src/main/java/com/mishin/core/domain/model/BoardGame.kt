package com.mishin.core.domain.model

/**
 * A board game in the café's collection.
 */
data class BoardGame(
    val id: String,
    val locationId: String,
    val name: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    /** Estimated duration in minutes. */
    val durationMinutes: Int,
    val difficulty: GameDifficulty,
    val status: GameStatus,
    val missingPieces: String?,
    val imageUri: String?,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val syncedAt: String?
)

enum class GameDifficulty {
    EASY,
    MEDIUM,
    HARD
}

enum class GameStatus {
    AVAILABLE,
    IN_USE,
    MAINTENANCE
}

/**
 * A game loan — tracks which game is at which table.
 */
data class GameLoan(
    val id: String,
    val locationId: String,
    val boardGameId: String,
    val tableReference: String,
    val startTime: String,
    val endTime: String?,
    val createdAt: String,
    val syncedAt: String?
)
