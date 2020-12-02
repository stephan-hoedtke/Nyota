package com.stho.nyota.sky.utilities

import kotlin.math.IEEErem

/**
 * Created by shoedtke on 20.01.2017.
 */
object Radian {
    private const val TWO_PI = Math.PI * 2
    @JvmStatic
    fun fromDegrees(degree: Double): Double {
        return Math.toRadians(degree)
    }

    fun toDegrees(radian: Double): Double {
        return Math.toDegrees(radian)
    }

    fun toDegrees180(radian: Double): Double {
        return Degree.normalizeTo180(Math.toDegrees(radian))
    }

    fun fromHour(hour: Double): Double {
        return hour * Math.PI / 12
    }

    fun normalize(radian: Double): Double {
        var r = radian.IEEErem(TWO_PI)
        if (r < 0) r += TWO_PI
        return r
    }
}