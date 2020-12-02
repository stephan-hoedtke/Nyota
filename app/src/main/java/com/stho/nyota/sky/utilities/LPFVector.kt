package com.stho.nyota.sky.utilities

/**
 * Vector with low pass filter
 */
class LPFVector {
    private val timeCounter = TimeCounter()
    private var hasValues = false
    val values = floatArrayOf(0.0f, 0.0f, 0.0f)

    fun hasValues(): Boolean {
        return hasValues
    }

    fun update(newValues: FloatArray) {
        if (hasValues) {
            lowPassFilter(newValues)
        } else {
            set(newValues)
        }
    }

    private fun lowPassFilter(newValues: FloatArray) {
        val dt = timeCounter.averageTimeDifferenceInSeconds.toDouble()
        if (dt > 0) {
            val alpha = dt / (TIME_CONSTANT_IN_SECONDS + dt)
            values[0] += (alpha * (newValues[0] - values[0])).toFloat()
            values[1] += (alpha * (newValues[1] - values[1])).toFloat()
            values[2] += (alpha * (newValues[2] - values[2])).toFloat()
        } else {
            set(newValues)
        }
    }

    private fun set(newValues: FloatArray) {
        values[0] = newValues[0]
        values[1] = newValues[1]
        values[2] = newValues[2]
        hasValues = true
    }

    companion object {
        private const val TIME_CONSTANT_IN_SECONDS = 0.18f
    }
}