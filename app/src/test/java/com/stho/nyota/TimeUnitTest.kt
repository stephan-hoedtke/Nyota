package com.stho.nyota

import org.junit.Assert
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TimeUnitTest {

    @Test
    fun nanoTime_isDifferentAlways() {
        val a = System.nanoTime()
        val b = System.nanoTime()
        Assert.assertNotEquals(a, b)
    }

    @Test
    fun timeUnit_toHours_isCorrect() {
        timeUnit_toHours_usingFullHours_isCorrect(120, 2.0)
        timeUnit_toHours_usingFullHours_isCorrect(90, 1.0)
        timeUnit_toHours_usingPartialHours_isNotCorrect(90, 1.5)
    }

    private fun timeUnit_toHours_usingFullHours_isCorrect(minutes: Long, expected: Double) {
        val millis = minutes * 60L * 1000L
        val actual = TimeUnit.MILLISECONDS.toHours(millis)
        Assert.assertEquals("$millis millis to hours", expected, actual.toDouble(), EPS)
    }

    private fun timeUnit_toHours_usingPartialHours_isNotCorrect(minutes: Long, expected: Double) {
        val millis = minutes * 60L * 1000L
        val actual = TimeUnit.MILLISECONDS.toHours(millis)
        Assert.assertNotEquals("$millis millis to hours", expected, actual.toDouble(), EPS)
    }


    companion object {
        private const val EPS = 0.000000001
    }
}