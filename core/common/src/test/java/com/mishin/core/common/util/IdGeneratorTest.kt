package com.mishin.core.common.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IdGeneratorTest {

    @Test
    fun `generateId returns an RFC 4122 version 4 UUID`() {
        val regex = Regex("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")

        val id = generateId()

        assertTrue("not a v4 uuid: $id", regex.matches(id))
        assertEquals(36, id.length)
    }

    @Test
    fun `generateId never repeats`() {
        val ids = (1..1_000).map { generateId() }

        assertEquals(1_000, ids.toSet().size)
    }

    @Test
    fun `successive ids differ`() {
        assertNotEquals(generateId(), generateId())
    }
}
