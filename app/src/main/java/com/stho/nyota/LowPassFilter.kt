package com.stho.nyota

import android.os.SystemClock
import com.stho.nyota.sky.utilities.Vector

internal class LowPassFilter(private val timeConstant: Double = 0.2, private val timeSource: Acceleration.TimeSource = Acceleration.SystemClockTimeSource()) {

    private val gravity: Vector = Vector(0.0, 0.0, 9.78)
    private var t1: Double = timeSource.seconds

    fun setAcceleration(acceleration: FloatArray): Vector {
        lowPassFilter(acceleration, timeDifferenceInSeconds())
        return gravity
    }

    fun reset() {
        gravity.x = 0.0
        gravity.y = 0.0
        gravity.z = 9.78
    }

    private fun lowPassFilter(acceleration: FloatArray, dt: Double) {
        if (dt > 0) {
            val alpha = dt / (timeConstant + dt)
            gravity.x += alpha * (acceleration[0] - gravity.x)
            gravity.y += alpha * (acceleration[1] - gravity.y)
            gravity.z += alpha * (acceleration[2] - gravity.z)
        } else {
            gravity.x = acceleration[0].toDouble()
            gravity.y = acceleration[1].toDouble()
            gravity.z = acceleration[2].toDouble()
        }
    }

    private fun timeDifferenceInSeconds(): Double {
        val t0 = t1
        t1 = timeSource.seconds
        return t1 - t0
    }
}


