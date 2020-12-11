package com.stho.nyota

import com.stho.nyota.sky.utilities.Angle
import com.stho.nyota.sky.utilities.Degree
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DegreeUnitTest {

    @Test
    fun degree_normalize_isCorrect() {
        degree_normalize_isCorrect(10.0, 10.0)
        degree_normalize_isCorrect(270.0, 270.0)
        degree_normalize_isCorrect(370.0, 10.0)
    }

    private fun degree_normalize_isCorrect(a: Double, expected: Double) {
        val actual = Degree.normalize(a)
        assertEquals(expected, actual, 0.000001)
    }

    @Test
    fun degree_normalizeTo180_isCorrect() {
        degree_normalizeTo180_isCorrect(10.0, 10.0)
        degree_normalizeTo180_isCorrect(270.0, -90.0)
        degree_normalizeTo180_isCorrect(370.0, 10.0)
    }

    private fun degree_normalizeTo180_isCorrect(a: Double, expected: Double) {
        val actual = Degree.normalizeTo180(a)
        assertEquals(expected, actual, DELTA)
    }

    companion object {
        private const val DELTA: Double = 0.00000001
    }
}