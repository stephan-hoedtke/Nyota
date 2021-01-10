package com.stho.nyota.sky.utilities

object Ten {
    /**
     * round a number to the nearest multiple of 10
     * a) 4722 -> 4720
     * b) 38 -> 4
     * c) 10 -> 10
     * d) -17 -> -20
     */
    fun nearestTen(x: Double): Double =
        when {
            x > 0 -> ((x + 5.0) / 10.0).toInt() * 10.0
            x < 0 -> ((x - 5.0) / 10.0).toInt() * 10.0
            else -> 0.0
        }
}