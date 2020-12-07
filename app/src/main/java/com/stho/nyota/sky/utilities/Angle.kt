package com.stho.nyota.sky.utilities

import java.lang.Math.PI
import kotlin.math.abs

object Angle {
    fun getAngleDifference(x: Float, y: Float): Float =
        normalizeTo180(x - y)

    fun getAngleDifference(x: Double, y: Double): Double =
        normalizeTo180(x - y)

    fun normalize(angle: Float): Float =
        when {
            angle > 360 -> normalize(angle - 360)
            angle < 0 -> normalize(angle + 360)
            else -> angle
        }

    fun normalize(angle: Double): Double =
        when {
            angle > 360 -> normalize(angle - 360)
            angle < 0 -> normalize(angle + 360)
            else -> angle
        }

    fun normalizeTo180(angle: Float): Float =
        when {
            angle > 180 -> normalizeTo180(angle - 360)
            angle <= -180 -> normalizeTo180(angle + 360)
            else -> angle
        }

    fun normalizeTo180(angle: Double): Double =
        when {
            angle > 180 -> normalizeTo180(angle - 360)
            angle <= -180 -> normalizeTo180(angle + 360)
            else -> angle
        }

    fun toString(angle: Double, angleType: AngleType?): String =
        when (angleType) {
            AngleType.AZIMUTH -> {
                val alpha = normalize(angle)
                Formatter.df0.format(abs(alpha)) + "° " + northEastSouthWest(alpha)
            }
            AngleType.ALTITUDE -> {
                val alpha = normalizeTo180(angle)
                sign(alpha) + Formatter.df0.format(abs(alpha)) + "°"
            }
            AngleType.PITCH -> {
                val alpha = normalizeTo180(angle)
                sign(alpha) + Formatter.df0.format(abs(alpha)) + "°"
            }
            AngleType.LATITUDE -> {
                val alpha = normalizeTo180(angle)
                Formatter.df2.format(abs(alpha)) + "° " + northSouth(alpha)
            }
            AngleType.LONGITUDE -> {
                val alpha = normalizeTo180(angle)
                Formatter.df2.format(abs(alpha)) + "° " + eastWest(alpha)
            }
            AngleType.DEGREE_NORTH_EAST_SOUTH_WEST -> {
                val alpha = normalize(angle)
                Formatter.df0.format(abs(alpha)) + "° " + northEastSouthWest(alpha)
            }
            else -> {
                val alpha = normalizeTo180(angle)
                sign(alpha) + Formatter.df2.format(Math.abs(alpha)) + "°"
            }
        }

    fun toDegree(angle: Double): Double =
        angle * RADIANT_TO_DEGREE

    private fun sign(x: Double): String =
        if (x < 0) "-" else "+"

    private fun northSouth(angle: Double) =
        if (angle >= 0) "N" else "S"

    private fun eastWest(angle: Double) =
        if (angle >= 0) "E" else "W"

    /*
        angle in [0*45 - 22.5 , 1*45 - 22.5] -> DIRECTION[0] = "N"
        angle in [1*45 - 22.5 , 2*45 - 22.5] -> DIRECTION[1] = "NE"
        angle in [2*45 - 22.5 , 3*45 - 22.5] -> DIRECTION[3] = "E"
        ...
     */
    private fun northEastSouthWest(angle: Double): String =
        DIRECTION[((angle + 22.5) / 45.0).toInt()]

    enum class AngleType {
        DEGREE_NORTH_EAST_SOUTH_WEST, AZIMUTH, ALTITUDE, LATITUDE, LONGITUDE, PITCH
    }

    private const val RADIANT_TO_DEGREE = 180.0 / PI
    private val DIRECTION = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N")

}