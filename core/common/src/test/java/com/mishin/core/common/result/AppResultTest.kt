package com.mishin.core.common.result

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppResultTest {

    // -- Status flags --

    @Test
    fun `success reports only isSuccess`() {
        val result = AppResult.Success(42)

        assertTrue(result.isSuccess)
        assertFalse(result.isError)
        assertFalse(result.isLoading)
    }

    @Test
    fun `error reports only isError`() {
        val result = AppResult.Error("boom")

        assertTrue(result.isError)
        assertFalse(result.isSuccess)
        assertFalse(result.isLoading)
        assertEquals("boom", result.message)
        assertNull(result.cause)
    }

    @Test
    fun `error keeps its cause`() {
        val cause = IllegalStateException("root")
        val result = AppResult.Error("boom", cause)

        assertEquals(cause, result.cause)
    }

    @Test
    fun `loading reports only isLoading`() {
        val result = AppResult.Loading

        assertTrue(result.isLoading)
        assertFalse(result.isSuccess)
        assertFalse(result.isError)
    }

    // -- getOrNull --

    @Test
    fun `getOrNull unwraps success`() {
        assertEquals("data", AppResult.Success("data").getOrNull())
    }

    @Test
    fun `getOrNull returns null for error and loading`() {
        assertNull(AppResult.Error("boom").getOrNull())
        assertNull(AppResult.Loading.getOrNull())
    }

    // -- map --

    @Test
    fun `map transforms success data`() {
        val result = AppResult.Success(2).map { it * 3 }

        assertEquals(AppResult.Success(6), result)
    }

    @Test
    fun `map passes error through without transforming`() {
        val error: AppResult<Int> = AppResult.Error("boom")
        var transformed = false

        val result = error.map { transformed = true; it + 1 }

        assertEquals(error, result)
        assertFalse(transformed)
    }

    @Test
    fun `map passes loading through`() {
        assertEquals(AppResult.Loading, AppResult.Loading.map { it })
    }

    // -- suspendMap --

    @Test
    fun `suspendMap transforms success data`() = runTest {
        val result = AppResult.Success(2).suspendMap { it * 3 }

        assertEquals(AppResult.Success(6), result)
    }

    @Test
    fun `suspendMap passes error through without transforming`() = runTest {
        val error: AppResult<Int> = AppResult.Error("boom")
        var transformed = false

        val result = error.suspendMap { transformed = true; it + 1 }

        assertEquals(error, result)
        assertFalse(transformed)
    }

    @Test
    fun `suspendMap passes loading through`() = runTest {
        assertEquals(AppResult.Loading, AppResult.Loading.suspendMap { it })
    }

    // -- Variance --

    @Test
    fun `error can be used where a typed result is expected`() {
        val result: AppResult<String> = AppResult.Error("boom")

        assertTrue(result.isError)
    }
}
