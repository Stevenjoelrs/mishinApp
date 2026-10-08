package com.mishin.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "board_games")
data class BoardGameEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "location_id") val locationId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "min_players") val minPlayers: Int,
    @ColumnInfo(name = "max_players") val maxPlayers: Int,
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Int,
    @ColumnInfo(name = "difficulty") val difficulty: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "missing_pieces") val missingPieces: String?,
    @ColumnInfo(name = "image_uri") val imageUri: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String,
    @ColumnInfo(name = "deleted_at") val deletedAt: String?,
    @ColumnInfo(name = "synced_at") val syncedAt: String?
)

@Entity(
    tableName = "game_loans",
    indices = [Index("board_game_id")]
)
data class GameLoanEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "location_id") val locationId: String,
    @ColumnInfo(name = "board_game_id") val boardGameId: String,
    @ColumnInfo(name = "table_reference") val tableReference: String,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "end_time") val endTime: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "synced_at") val syncedAt: String?
)
