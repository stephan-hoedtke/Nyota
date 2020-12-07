package com.stho.nyota.sky.utilities

import java.security.InvalidParameterException
import java.util.regex.Pattern

/**
 * Created by shoedtke on 01.09.2016.
 */
class Hour {
    val angleInHours: Double
    val hour: Int // [0..24]
    val minute: Int
    val seconds: Double

    private constructor(angleInHours: Double) {
        this.angleInHours = normalize(angleInHours)
        hour = this.angleInHours.toInt()
        val minutes = 60 * (this.angleInHours - hour)
        minute = minutes.toInt()
        seconds = 60 * (minutes - minute)
    }

    private constructor(hour: Int, minute: Int, seconds: Double) {
        this.hour = Math.abs(hour)
        this.minute = minute
        this.seconds = seconds
        angleInHours = hour + minute / 60.0 + seconds / 3600.0
    }

    override fun toString(): String {
        return hour.toString() + "h " + minute + "m " + Formatter.df0.format(seconds) + "s"
    }

    fun toShortString(): String {
        return hour.toString() + "h " + minute + "m"
    }

    fun toDegree(): Double {
        return angleInHours * 15
    }

    companion object {
        internal fun fromDegree(angleInDegree: Double): Hour {
            return Hour(angleInDegree / 15)
        }

        internal fun fromHour(angleInHours: Double): Hour {
            return Hour(angleInHours)
        }

        internal fun fromHour(hour: Int, minute: Int, seconds: Double): Hour {
            return Hour(hour, minute, seconds)
        }

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

        fun normalize(hour: Double): Double {
            var h = hour
            while (h < 0) h += 24.0
            while (h > 24) h -= 24.0
            return h
        }
    }
}