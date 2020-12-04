package com.stho.nyota.ui.finder

import android.os.SystemClock
import com.stho.nyota.sky.utilities.Angle

internal class Acceleration(private val factorInSeconds: Double = 1.1) {
    // s(t) = a * t^3 + b * t^2 + c * t + d
    // s(0) = s0
    // s(1) = s1
    // s'(0) = v0
    // s'(1) = 0
    // -->  a = v0 - 2 (s1 - s0)
    //      b = 3 (s1 - s0) - 2 v0
    //      c = v0
    //      d = s0
    private var v0: Double = 0.0
    private var s0: Double = 0.0
    private var s1: Double = 0.0
    private var a = 0.0
    private var b = 0.0
    private var c = 0.0
    private var d = 0.0
    private var t0: Long = SystemClock.elapsedRealtimeNanos()
    private val factor: Double = factorInSeconds / NANOSECONDS_PER_SECOND

    init {
        calculateFormula()
    }

    val position: Double
        get() {
            val t = getTime(SystemClock.elapsedRealtimeNanos())
            return getPosition(t)
        }

    fun update(targetPosition: Double) {
        val elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        val t = getTime(elapsedRealtimeNanos)
        val v = getSpeed(t)
        val s = getPosition(t)
        v0 = v
        s0 = s
        s1 = targetPosition
        t0 = elapsedRealtimeNanos
        calculateFormula()
    }

    fun rotateTo(targetAngle: Double) =
        update(Angle.normalize(targetAngle))

    private fun getTime(elapsedRealtimeNanos: Long): Double {
        val nanos = elapsedRealtimeNanos - t0
        return factor * nanos
    }

    private fun calculateFormula() {
        a = v0 - 2 * (s1 - s0)
        b = 3 * (s1 - s0) - 2 * v0
        c = v0
        d = s0
    }

    private fun getPosition(t: Double): Double {
        return when {
            t < 0 -> s0
            t > 1 -> s1
            else -> ((a * t + b) * t + c) * t + d
        }
    }

    private fun getSpeed(t: Double): Double {
        return when {
            t < 0 -> v0
            t > 1 -> 0.0
            else -> (3 * a * t + 2 * b) * t + c
        }
    }

    companion object {
        private const val NANOSECONDS_PER_SECOND = 1000000000.0
    }
}