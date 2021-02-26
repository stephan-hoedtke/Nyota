package com.stho.nyota.sky.utilities

internal class TimeCounter {
    private var startTime: Long = System.nanoTime()

    val timeInSeconds: Double
        get() {
            val newTime = System.nanoTime()
            return (newTime - startTime) / NANOSECONDS
        }

    companion object {
        private const val NANOSECONDS: Double = 1000000000.0
    }
}
