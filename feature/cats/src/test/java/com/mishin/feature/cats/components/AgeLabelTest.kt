package com.mishin.feature.cats.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AgeLabelTest {

    @Test
    fun `null age returns null`() {
        assertNull(ageLabel(null))
    }

    @Test
    fun `zero months renders as zero months`() {
        assertEquals("0 meses", ageLabel(0))
    }

    @Test
    fun `single month uses singular`() {
        assertEquals("1 mes", ageLabel(1))
    }

    @Test
    fun `months below one year render in months`() {
        assertEquals("5 meses", ageLabel(5))
        assertEquals("11 meses", ageLabel(11))
    }

    @Test
    fun `exactly one year renders as one year`() {
        assertEquals("1 año", ageLabel(12))
    }

    @Test
    fun `whole years render in years`() {
        assertEquals("2 años", ageLabel(24))
        assertEquals("3 años", ageLabel(36))
    }

    @Test
    fun `years with remainder combine years and months`() {
        assertEquals("1 año y 1 mes", ageLabel(13))
        assertEquals("2 años y 1 mes", ageLabel(25))
        assertEquals("2 años y 6 meses", ageLabel(30))
    }
}
