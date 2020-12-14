package com.stho.nyota

import org.junit.Assert
import org.junit.Test
import com.stho.nyota.sky.utilities.Vector
import kotlin.math.abs
import kotlin.math.pow


class LowPassFilterUnitTest {

    class FakeTimeSource(var time: Double = 0.0) : TimeSource {
        override val elapsedRealtimeSeconds: Double
            get() = time
    }

    @Test
    fun lowPassFilter_forSimpleValue_isCorrect() {

        val startValue: FloatArray = getArray(0f, 0f, 0f)
        val targetValue: FloatArray = getArray(1f, 2f, 3f)
        val timeConstant = 0.2
        val timeSource = FakeTimeSource()

        timeSource.time = 100.0
        val lowPassFilter = LowPassFilter(timeConstant, timeSource)
        lowPassFilter.setAcceleration(startValue)

        for (i: Int in 0..15) {
            timeSource.time += 0.1
            lowPassFilter.setAcceleration(targetValue)
        }

        timeSource.time += 0.1
        val v: Vector = lowPassFilter.setAcceleration(targetValue)


        Assert.assertTrue("Values x after 1 second", startValue[0] < v.x && v.x < targetValue[0] && abs(v.x - targetValue[0]) < DELTA_15)
        Assert.assertTrue("Values y after 1 second", startValue[1] < v.y && v.y < targetValue[1] && abs(v.y - targetValue[1]) < DELTA_15)
        Assert.assertTrue("Values z after 1 second", startValue[2] < v.z && v.z < targetValue[2] && abs(v.z - targetValue[2]) < DELTA_15)
    }


    private fun getArray(x: Float, y: Float, z: Float): FloatArray =
        FloatArray(3).also {
            it[0] = x
            it[1] = y
            it[2] = z
        }

    companion object {
        private const val DELTA_15 = 0.01
    }
}