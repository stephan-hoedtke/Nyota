package com.stho.nyota

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Hour
import org.junit.Assert
import org.junit.Test

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class RegexUnitTest {

    // see here: https://en.wikipedia.org/wiki/List_of_stars_in_Virgo

    @Test
    fun hourPattern_isCorrect() {
        areEqual("13h 25m 11.60", Hour.fromHour(13, 25, 11.60))
        areEqual("13h 54m 42.20", Hour.fromHour(13, 54, 42.20))
        areEqual("11h 38m 24.09", Hour.fromHour(11, 38, 24.09))
        areEqual("23h 49m 51.95", Hour.fromHour(23, 49, 51.95))
        areEqual("2h 31m 4", Hour.fromHour(2, 31, 4.0))
    }

    private fun areEqual(hourString: String, hour: Hour) {
        Assert.assertEquals(hourString, Hour.fromHour(hourString).angleInDegree, hour.angleInDegree, 0.000001)
    }

    @Test
    fun degreePattern_isCorrect() {
        areEqual("+10° 57′ 32.8", Degree.fromPositive(10, 57, 32.8))
        areEqual("−01° 26′ 58.0", Degree.fromNegative(1, 26, 58.0))
        areEqual("+12° 40′ 57", Degree.fromPositive(12, 40, 57.0))
        areEqual("−03° 26′ 40", Degree.fromNegative(3, 26, 40.0))
        areEqual("+8° 15' 51", Degree.fromPositive(8, 15, 51.0))
    }

    private fun areEqual(degreeString: String, degree: Degree) {
        Assert.assertEquals(degreeString, Degree.fromDegree(degreeString).angleInDegree, degree.angleInDegree, 0.000001)
    }
}