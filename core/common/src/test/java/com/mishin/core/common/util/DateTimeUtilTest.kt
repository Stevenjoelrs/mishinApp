package com.mishin.core.common.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class DateTimeUtilTest {

    @Test
    fun `nowIso returns a parseable ISO-8601 instant`() {
        val iso = DateTimeUtil.nowIso()

        val parsed = Instant.parse(iso)
        assertTrue(parsed <= Clock.System.now())
    }

    @Test
    fun `now lies between instants captured before and after it`() {
        val before = Clock.System.now()
        val now = DateTimeUtil.now()
        val after = Clock.System.now()

        assertTrue(now >= before)
        assertTrue(now <= after)
    }

    @Test
    fun `parseInstant throws on malformed input`() {
        val thrown = runCatching { DateTimeUtil.parseInstant("not-a-date") }.exceptionOrNull()

        assertTrue(thrown is IllegalArgumentException)
    }

    @Test
    fun `toLocal round-trips back to the same instant`() {
        val instant = DateTimeUtil.now()

        val local = DateTimeUtil.toLocal(instant)
        val roundTrip = local.toInstant(TimeZone.currentSystemDefault())

        assertEquals(instant, roundTrip)
    }

    @Test
    fun `nowLocal is close to toLocal of now`() {
        val localNow = DateTimeUtil.nowLocal()
        val converted = DateTimeUtil.toLocal(DateTimeUtil.now())

        val delta = converted.toInstant(TimeZone.currentSystemDefault()) -
            localNow.toInstant(TimeZone.currentSystemDefault())

        assertTrue(delta.absoluteValue <= 5.seconds)
    }
}
