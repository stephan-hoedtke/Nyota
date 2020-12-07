package com.stho.nyota

import com.stho.nyota.sky.universe.Algorithms
import org.junit.Assert
import org.junit.Test

class AlgorithmMathsUnitTest {

    @Test
    fun algorithm_truncate_isCorrect() {
        double_truncate_isCorrect(1.123, 1.0)
        double_truncate_isCorrect(-1.123, -1.0)
        double_truncate_isCorrect(0.0, 0.0)
        double_truncate_isCorrect(1.987, 1.0)
        double_truncate_isCorrect(-1.987, -1.0)
    }

    private fun double_truncate_isCorrect(value: Double, expected: Double) {
        val actual: Double = Algorithms.truncate(value)
        Assert.assertEquals("Truncate $value", expected, actual, EPS)
    }

    @Test
    fun algorithm_decimals_isCorrect() {
        algorithm_decimals_isCorrect(1.123, 0.123)
        algorithm_decimals_isCorrect(-1.123, -0.123)
        algorithm_decimals_isCorrect(0.0, 0.0)
        algorithm_decimals_isCorrect(-1.123, -0.123)
        algorithm_decimals_isCorrect(1.987, 0.987)
        algorithm_decimals_isCorrect(-1.987, -0.987)
        algorithm_decimals_isCorrect(3.0, 0.0)
        algorithm_decimals_isCorrect(-3.0, 0.0)
    }

    private fun algorithm_decimals_isCorrect(value: Double, expected: Double) {
        val actual: Double = Algorithms.decimals(value)
        Assert.assertEquals("Truncate $value", expected, actual, EPS)
    }


    companion object {
        private const val EPS = 0.00000001
    }
}