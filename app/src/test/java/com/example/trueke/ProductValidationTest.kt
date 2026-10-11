package com.example.trueke

import com.example.trueke.utils.parseDistanceKm
import com.example.trueke.utils.parseReferenceValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProductValidationTest {

    @Test
    fun referenceValueAcceptsZeroAndPositiveInteger() {
        assertEquals(0, parseReferenceValue("0"))
        assertEquals(120000, parseReferenceValue("120000"))
    }

    @Test
    fun referenceValueAcceptsIntMaximum() {
        assertEquals(2147483647, parseReferenceValue("2147483647"))
    }

    @Test
    fun referenceValueTrimsSpaces() {
        assertEquals(100, parseReferenceValue(" 100 "))
    }

    @Test
    fun referenceValueRejectsNegativeNumber() {
        assertNull(parseReferenceValue("-1"))
    }

    @Test
    fun referenceValueRejectsOverflow() {
        assertNull(parseReferenceValue("2147483648"))
    }

    @Test
    fun referenceValueRejectsEmptyDecimalAndNonNumericInput() {
        listOf("", "   ", "1.5", "1,5", "abc").forEach {
            assertNull(parseReferenceValue(it))
        }
    }

    @Test
    fun distanceAcceptsZeroAndDecimalPoint() {
        assertEquals(0.0, requireNotNull(parseDistanceKm("0")), 0.0)
        assertEquals(2.5, requireNotNull(parseDistanceKm("2.5")), 0.0)
    }

    @Test
    fun distanceAcceptsDecimalCommaAndSurroundingSpaces() {
        assertEquals(2.5, requireNotNull(parseDistanceKm(" 2,5 ")), 0.0)
    }

    @Test
    fun distanceRejectsNegativeNumber() {
        assertNull(parseDistanceKm("-0.1"))
    }

    @Test
    fun distanceRejectsNaNAndInfinity() {
        listOf("NaN", "Infinity", "-Infinity").forEach {
            assertNull(parseDistanceKm(it))
        }
    }

    @Test
    fun distanceRejectsFloatingPointOverflow() {
        assertNull(parseDistanceKm("1e309"))
    }

    @Test
    fun distanceRejectsEmptyAndMalformedInput() {
        listOf("", "   ", "abc", "1,2,3", "1.234,5").forEach {
            assertNull(parseDistanceKm(it))
        }
    }
}
