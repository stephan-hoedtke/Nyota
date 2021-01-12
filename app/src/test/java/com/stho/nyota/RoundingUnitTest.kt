package com.stho.nyota

import com.stho.nyota.sky.utilities.Ten
import org.junit.Assert
import org.junit.Test

class RoundingUnitTest {

    @Test
    fun round_to10_isCorrect() {
        round_to10_isCorrect(4722.876, 4720.0)
        round_to10_isCorrect(38.93, 40.0)
        round_to10_isCorrect(10.00, 10.0)
        round_to10_isCorrect(-17.382, -20.0)
        round_to10_isCorrect(-32.382, -30.0)
    }

    private fun round_to10_isCorrect(x: Double, expected: Double) {
        val actual = Ten.nearest10(x)
        Assert.assertEquals("Nearest tens of $x", expected, actual, 0.00000001)
    }

    @Test
    fun round_to15_isCorrect() {
        round_to15_isCorrect(37.2, 30.0)
        round_to15_isCorrect(-241.0, -240.0)
        round_to15_isCorrect(-239.0, -240.0)
    }

    private fun round_to15_isCorrect(x: Double, expected: Double) {
        val actual = Ten.nearest15(x)
        Assert.assertEquals("Nearest tens of $x", expected, actual, 0.00000001)
    }

}