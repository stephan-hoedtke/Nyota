package com.stho.nyota

import com.stho.nyota.sky.utilities.Ten
import org.junit.Assert
import org.junit.Test
import kotlin.math.exp

class RoundingUnitTest {

    @Test
    fun round_toTen_isCorrect() {
        round_toTen_isCorrect(4722.876, 4720.0)
        round_toTen_isCorrect(38.93, 40.0)
        round_toTen_isCorrect(10.00, 10.0)
        round_toTen_isCorrect(-17.382, -20.0)
        round_toTen_isCorrect(-32.382, -30.0)
    }

    private fun round_toTen_isCorrect(x: Double, expected: Double) {
        val actual = Ten.nearestTen(x)
        Assert.assertEquals("Nearest tens of $x", expected, actual, 0.00000001)
    }
}