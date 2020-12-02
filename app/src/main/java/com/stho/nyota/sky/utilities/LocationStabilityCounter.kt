package com.stho.nyota.sky.utilities

class LocationStabilityCounter {
    private var counter = 0
    fun reset() {
        counter = 0
    }

    val isStable: Boolean
        get() = if (counter > REQUIRED_COUNTER_VALUE) {
            true
        } else {
            counter++
            false
        }

    companion object {
        private const val REQUIRED_COUNTER_VALUE = 10
    }
}