package com.stho.nyota

import com.stho.nyota.sky.utilities.Vector

internal class LowPassFilter(private val timeConstant: Double = 0.2, private val timeSource: TimeSource = SystemClockTimeSource()) {

    private val gravity: Vector = Vector(0.0, 0.0, 9.78)
    private var startTime: Double = 0.0
    private var count: Long = 0L

    fun setAcceleration(acceleration: FloatArray): Vector {
        val dt: Double = getAverageTimeDifferenceInSeconds()
        lowPassFilter(acceleration, dt)
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

    private fun getAverageTimeDifferenceInSeconds(): Double =
        when {
            count < 1 -> {
                startTime = timeSource.elapsedRealtimeSeconds
                count = 1L
                0.0
            }
            else -> {
                (timeSource.elapsedRealtimeSeconds - startTime) / count++
            }
        }

}


