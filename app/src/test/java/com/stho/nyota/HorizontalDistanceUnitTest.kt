package com.stho.nyota

import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.createDefaultBerlinBuch
import com.stho.nyota.sky.utilities.createDefaultMunich
import org.junit.Assert
import org.junit.Test

class HorizontalDistanceUnitTest {

    @Test
    fun horizontalDistance_Berlin_Munich_isCorrect() {
        val berlin = City.createDefaultBerlinBuch()
        val munich = City.createDefaultMunich()
        val expected = 520.0 // https://www.luftlinie.org/Berlin/M%C3%BCnchen

        val actual1 = berlin.location.getHorizontalDistanceInKmTo(munich.location)
        val actual2 = munich.location.getHorizontalDistanceInKmTo(berlin.location)

        Assert.assertEquals("Berlin-München", expected, actual1, 10.0)
        Assert.assertEquals("München-Berlin", expected, actual2, 10.0)
        Assert.assertEquals("Berlin-München = München-Berlin", actual1, actual2, DELTA)
    }

    @Test
    fun horizontalDistance_Berlin_Berlin_isCorrect() {
        val berlin = City.createDefaultBerlinBuch()
        val expected = 0.0 // https://www.luftlinie.org/Berlin/M%C3%BCnchen

        val actual = berlin.location.getHorizontalDistanceInKmTo(berlin.location)

        Assert.assertEquals("Berlin-Berlin", expected, actual, DELTA)
    }

    companion object {
        private const val DELTA = 0.000001
    }
}