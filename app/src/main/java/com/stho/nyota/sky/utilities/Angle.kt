package com.stho.nyota.sky.utilities

import java.lang.Math.PI

/**
 * Created by shoedtke on 28.08.2016.
 */
object Angle {
    fun getAngleDifference(x: Float, y: Float): Float {
        return normalizeTo180(x - y)
    }

    fun getAngleDifference(x: Double, y: Double): Double {
        return normalizeTo180(x - y)
    }

    fun rotateTo(from: Double, to: Double): Double {
        val difference: Double = getAngleDifference(from, to)
        return from + difference
    }

    fun normalize(angle: Float): Float {
        var alpha = angle
        while (alpha > 360) alpha -= 360f
        while (alpha < 0) alpha += 360f
        return alpha
    }

    fun normalize(angle: Double): Double {
        var alpha = angle
        alpha = Math.IEEEremainder(alpha, 360.0)
        if (alpha < 0) alpha += 360.0
        return alpha
    }

    fun normalizeTo180(angle: Float): Float {
        var alpha = angle
        while (alpha > 180) alpha -= 360f
        while (alpha <= -180) alpha += 360f
        return alpha
    }

    fun normalizeTo180(angle: Double): Double {
        var alpha = angle
        alpha = Math.IEEEremainder(alpha, 360.0)
        if (alpha < -180) alpha += 360.0
        if (alpha > 180) alpha -= 360.0
        return alpha
    }

    fun toString(angle: Double, angleType: AngleType?): String {
        val alpha: Double
        return when (angleType) {
            AngleType.AZIMUTH -> {
                alpha = normalize(angle)
                Formatter.df0.format(Math.abs(alpha)) + "° " + northEastSouthWest(alpha)
            }
            AngleType.ALTITUDE -> {
                alpha = normalizeTo180(angle)
                sign(alpha) + Formatter.df0.format(Math.abs(alpha)) + "°"
            }
            AngleType.LATITUDE -> {
                alpha = normalizeTo180(angle)
                Formatter.df2.format(Math.abs(alpha)) + "° " + if (angle >= 0) "N" else "S"
            }
            AngleType.LONGITUDE -> {
                alpha = normalizeTo180(angle)
                Formatter.df2.format(Math.abs(alpha)) + "° " + if (angle >= 0) "E" else "W"
            }
            AngleType.DEGREE_NORTH_EAST_SOUTH_WEST -> {
                alpha = normalize(angle)
                Formatter.df0.format(Math.abs(alpha)) + "° " + northEastSouthWest(alpha)
            }
            else -> {
                alpha = normalizeTo180(angle)
                sign(alpha) + Formatter.df2.format(Math.abs(alpha)) + "°"
            }
        }
    }

    fun toDegree(angle: Double): Float {
        return (angle * 180 / PI).toFloat()
    }

    private fun sign(x: Double): String {
        return if (x < 0) "-" else "+"
    }

    private fun northEastSouthWest(angle: Double): String {
        val n = ((if (angle < 0) angle + 282.5 else angle + 22.5) / 45).toInt()
        val x = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N")
        return x[n]
    }

    enum class AngleType {
        DEGREE_NORTH_EAST_SOUTH_WEST, AZIMUTH, ALTITUDE, LATITUDE, LONGITUDE
    }
}