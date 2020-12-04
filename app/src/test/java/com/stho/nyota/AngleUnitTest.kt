package com.stho.nyota

import com.stho.nyota.sky.utilities.Angle
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AngleUnitTest {

    @Test
    fun angle_normalize_isCorrect() {
        angle_normalize_isCorrect(10.0, 10.0)
        angle_normalize_isCorrect(270.0, 270.0)
        angle_normalize_isCorrect(370.0, 10.0)
    }

    private fun angle_normalize_isCorrect(a: Double, expected: Double) {
        val actual = Angle.normalize(a)
        assertEquals(expected, actual, 0.000001)
    }

    @Test
    fun angle_normalizeTo180_isCorrect() {
        angle_normalizeTo180_isCorrect(10.0, 10.0)
        angle_normalizeTo180_isCorrect(270.0, -90.0)
        angle_normalizeTo180_isCorrect(370.0, 10.0)
    }

    private fun angle_normalizeTo180_isCorrect(a: Double, expected: Double) {
        val actual = Angle.normalizeTo180(a)
        assertEquals(expected, actual, 0.000001)
    }

}