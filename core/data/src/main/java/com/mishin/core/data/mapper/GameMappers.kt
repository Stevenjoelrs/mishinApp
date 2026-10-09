package com.mishin.core.data.mapper

import com.mishin.core.database.entity.BoardGameEntity
import com.mishin.core.database.entity.GameLoanEntity
import com.mishin.core.domain.model.BoardGame
import com.mishin.core.domain.model.GameDifficulty
import com.mishin.core.domain.model.GameLoan
import com.mishin.core.domain.model.GameStatus

/**
 * Mappers between game domain models and Room entities.
 * These keep the domain layer clean from Room annotations.
 */

// -- BoardGame --

fun BoardGameEntity.toDomain(): BoardGame = BoardGame(
    id = id,
    locationId = locationId,
    name = name,
    minPlayers = minPlayers,
    maxPlayers = maxPlayers,
    durationMinutes = durationMinutes,
    difficulty = GameDifficulty.valueOf(difficulty),
    status = GameStatus.valueOf(status),
    missingPieces = missingPieces,
    imageUri = imageUri,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncedAt = syncedAt
)

fun BoardGame.toEntity(): BoardGameEntity = BoardGameEntity(
    id = id,
    locationId = locationId,
    name = name,
    minPlayers = minPlayers,
    maxPlayers = maxPlayers,
    durationMinutes = durationMinutes,
    difficulty = difficulty.name,
    status = status.name,
    missingPieces = missingPieces,
    imageUri = imageUri,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncedAt = syncedAt
)

// -- GameLoan --

fun GameLoanEntity.toDomain(): GameLoan = GameLoan(
    id = id,
    locationId = locationId,
    boardGameId = boardGameId,
    tableReference = tableReference,
    startTime = startTime,
    endTime = endTime,
    createdAt = createdAt,
    syncedAt = syncedAt
)

fun GameLoan.toEntity(): GameLoanEntity = GameLoanEntity(
    id = id,
    locationId = locationId,
    boardGameId = boardGameId,
    tableReference = tableReference,
    startTime = startTime,
    endTime = endTime,
    createdAt = createdAt,
    syncedAt = syncedAt
)
