package com.stho.nyota.sky.utilities

/**
 * Created by shoedtke on 11.10.2016.
 */
internal class TimeCounter {
    private var averageDifference = 0.0
    private var startTime: Long = System.nanoTime()

    @get:Synchronized
    val averageTimeDifferenceInSeconds: Float
        get() {
            val newTime = System.nanoTime()
            val difference = (newTime - startTime) / NANOSECONDS
            averageDifference = (17 * averageDifference + difference) / 18
            startTime = newTime
            return averageDifference.toFloat()
        }

    @get:Synchronized
    val timeDifferenceInSeconds: Float
        get() {
            val newTime = System.nanoTime()
            val difference = (newTime - startTime) / NANOSECONDS
            startTime = newTime
            return difference.toFloat()
        }

    companion object {
        private const val NANOSECONDS = 1000000000.0
    }
}