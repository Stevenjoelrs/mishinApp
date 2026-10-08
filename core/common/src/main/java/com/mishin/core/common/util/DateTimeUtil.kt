package com.mishin.core.common.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Date/time utilities using kotlinx-datetime.
 * All timestamps are stored as ISO-8601 UTC strings in the database.
 */
object DateTimeUtil {

    fun now(): Instant = Clock.System.now()

    fun nowIso(): String = now().toString()

    fun nowLocal(): LocalDateTime =
        now().toLocalDateTime(TimeZone.currentSystemDefault())

    fun parseInstant(iso: String): Instant = Instant.parse(iso)

    fun toLocal(instant: Instant): LocalDateTime =
        instant.toLocalDateTime(TimeZone.currentSystemDefault())
}
