package com.stho.nyota

import com.stho.nyota.sky.utilities.Angle
import org.junit.Test

import org.junit.Assert.*

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
        assertNotEquals(a, b)
    }
}