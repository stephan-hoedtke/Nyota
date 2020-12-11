package com.stho.nyota.sky.utilities

import java.security.InvalidParameterException
import java.util.regex.Pattern
import kotlin.math.IEEErem
import kotlin.math.abs

/**
 * Created by shoedtke on 01.09.2016.
 */
class Hour {

    val angleInHours: Double
    private val hour: Int // [0..24]
    private val minute: Int
    private val seconds: Double

    private constructor(angleInHours: Double) {
        this.angleInHours = normalize(angleInHours)
        hour = this.angleInHours.toInt()
        val minutes = 60 * (this.angleInHours - hour)
        minute = minutes.toInt()
        seconds = 60 * (minutes - minute)
    }

    private constructor(hour: Int, minute: Int, seconds: Double) {
        this.hour = abs(hour)
        this.minute = minute
        this.seconds = seconds
        angleInHours = hour + minute / 60.0 + seconds / 3600.0
    }

    override fun toString(): String =
        hour.toString() + "h " + minute + "m " + Formatter.df0.format(seconds) + "s"

    fun toShortString(): String =
        hour.toString() + "h " + minute + "m"

    val angleInDegree: Double
        get() = angleInHours * 15

    companion object {

        internal fun fromDegree(angleInDegree: Double): Hour =
            Hour(angleInDegree / 15)

        internal fun fromHour(angleInHours: Double): Hour =
            Hour(angleInHours)

        internal fun fromHour(hour: Int, minute: Int, seconds: Double): Hour =
            Hour(hour, minute, seconds)

        private val pattern = Pattern.compile("^(\\d+)[h]\\s(\\d+)[m]\\s(\\d+[.]*\\d*)$") // for:  13h 25m 11.60s

        fun fromHour(str: String): Hour {
            val m = pattern.matcher(str)
            if (m.find() && m.groupCount() == 3) {
                val hour = m.group(1)!!.toInt()
                val minute = m.group(2)!!.toInt()
                val seconds = m.group(3)!!.toDouble()
                return fromHour(hour, minute, seconds)
            }
            throw InvalidParameterException("Invalid hour $str")
        }

        fun normalize(hour: Double): Double =
            hour.IEEErem(24.0).let {
                when {
                    it < 0 -> it + 24.0
                    else -> it
                }
            }
    }
}