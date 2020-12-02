package com.stho.nyota.sky.utilities

import java.security.InvalidParameterException
import java.util.regex.Pattern
import kotlin.math.*

/**
 * Created by shoedtke on 30.08.2016.
 */
class Degree {
    val angleInDegree: Double
    val sign: Int
    val degree: Int
    val minute: Int
    val seconds: Double

    private constructor(angleInDegree: Double) {
        this.angleInDegree = normalizeTo180(angleInDegree)
        sign = sign(this.angleInDegree).toInt()
        val degrees = abs(this.angleInDegree)
        degree = degrees.toInt()
        val minutes = 60 * (degrees - degree)
        minute = minutes.toInt()
        seconds = 60 * (minutes - minute)
    }

    private constructor(sign: Int, degree: Int, minute: Int, seconds: Double) {
        this.sign = sign
        this.degree = degree
        this.minute = minute
        this.seconds = seconds
        angleInDegree = this.sign * (this.degree + this.minute / 60.0 + this.seconds / 3600.0)
    }

    override fun toString(): String {
        return (if (sign < 0) "-" else "") + degree + "° " + minute + "' " + Formatter.df0.format(seconds) + "''"
    }

    fun toShortString(): String {
        return (if (sign < 0) "-" else "") + degree + "° " + minute + "'"
    }

    fun toDegree(): Double {
        return angleInDegree
    }

    companion object {
        fun fromDegree(angleInDegree: Double): Degree {
            return Degree(angleInDegree)
        }

        fun fromPositive(degree: Int, minute: Int, seconds: Double): Degree {
            return Degree(1, abs(degree), minute, seconds)
        }

        fun fromNegative(degree: Int, minute: Int, seconds: Double): Degree {
            return Degree(-1, abs(degree), minute, seconds)
        }

        private val pattern = Pattern.compile("^([+|−|-|–|-])(\\d+)[°]\\s(\\d+)[′|']\\s(\\d+[.]*\\d*)$") // for:  −11° 09′ 40.5

        fun fromDegree(str: String): Degree {
            val m = pattern.matcher(str)
            if (m.find() && m.groupCount() == 4) {
                val degree: Int= m.group(2)?.toInt() ?: 0
                val minute: Int= m.group(3)?.toInt() ?: 0
                val seconds: Double = m.group(4)?.toDouble() ?: 0.0
                return if (m.group(1) == "+") {
                    fromPositive(degree, minute, seconds)
                } else {
                    fromNegative(degree, minute, seconds)
                }
            }
            throw InvalidParameterException("Invalid degree $str")
        }

        /* convert from degree to radian */
        private const val DEGRAD = Math.PI / 180.0

        /* convert from radian to degree */
        internal const val RADEG = 180.0 / Math.PI

        fun sinus(degree: Double): Double {
            return sin(degree * DEGRAD)
        }

        fun tangent(degree: Double): Double {
            return tan(degree * DEGRAD)
        }

        fun cosines(degree: Double): Double {
            return cos(degree * DEGRAD)
        }

        fun arcTan2(y: Double, x: Double): Double {
            return RADEG * atan2(y, x)
        }

        fun arcSin(x: Double): Double {
            return RADEG * asin(x)
        }

        fun arcCos(x: Double): Double {
            return RADEG * acos(x)
        }

        fun normalize(degree: Double): Double {
            var a = Math.IEEEremainder(degree, 360.0)
            if (a < 0) a += 360.0
            return a
        }

        public fun normalizeTo180(degree: Double): Double {
            var a = Math.IEEEremainder(degree, 360.0)
            if (a > 180) a -= 360.0
            if (a < -180) a += 360.0
            return a
        }
    }
}