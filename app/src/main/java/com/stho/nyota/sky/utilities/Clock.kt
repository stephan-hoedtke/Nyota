package com.stho.nyota.sky.utilities

class Clock(private val intervalInMillis: Long) {
    private var startMillis: Long = 0
    fun timeOver(): Boolean {
        val millis = System.currentTimeMillis()
        return if (millis - startMillis > intervalInMillis) {
            startMillis = millis
            true
        } else {
            false
        }
    }

}