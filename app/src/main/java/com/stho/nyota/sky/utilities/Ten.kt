package com.stho.nyota.sky.utilities

object Ten {
    /**
     * round a number to the nearest multiple of 10
     * a) 4722 -> 4720
     * b) 38 -> 4
     * c) 10 -> 10
     * d) -17 -> -20
     */
    fun nearest10(x: Double): Double =
        when {
            x > 0 -> ((x + 5.0) / 10.0).toInt() * 10.0
            x < 0 -> ((x - 5.0) / 10.0).toInt() * 10.0
            else -> 0.0
        }

    /**
     * round a number to the nearest multiple of 15
     * a) 37 -> 30
     * b) -241 -> -240
     * c) -239 -> -240
     */
    fun nearest15(x: Double): Double =
        when {
            x > 0 -> ((x + 7.5) / 15.0).toInt() * 15.0
            x < 0 -> ((x - 7.5) / 15.0).toInt() * 15.0
            else -> 0.0
        }
}