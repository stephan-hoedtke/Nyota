package com.stho.nyota.ui.finder

import com.stho.nyota.Acceleration
import com.stho.nyota.LowPassFilter
import com.stho.nyota.sky.utilities.*


interface IOrientationFilter {
    fun onOrientationAnglesChanged(orientationAngles: FloatArray)
    val currentOrientation: Orientation
    val updateCounter: Long
}

/*
    The class takes updates of the orientation vector by listening to onOrientationAnglesChanged(angles).
    The values will be stored and smoothed with acceleration.
    A handler will regularly read the updated smoothed orientation
 */
class OrientationAccelerationFilter: IOrientationFilter {

    override var updateCounter: Long = 0L
        private set

    private val azimuthAcceleration: Acceleration = Acceleration(1.2)
    private val pitchAcceleration: Acceleration = Acceleration(0.7)
    private val rollAcceleration: Acceleration = Acceleration(0.7)
    private val lowPassFilter: LowPassFilter = LowPassFilter()

    override val currentOrientation: Orientation
        get() {
            val azimuth = azimuthAcceleration.position
            val pitch = pitchAcceleration.position
            val roll = rollAcceleration.position
            val direction = pitch - 90
            return Orientation(
                azimuth = Degree.normalize(azimuth),
                pitch = Degree.normalizeTo180(pitch),
                direction = Degree.normalizeTo180(direction),
                roll =  Degree.normalizeTo180(roll)
            )
        }

    override fun onOrientationAnglesChanged(orientationAngles: FloatArray) {
        val gravity: Vector = lowPassFilter.setAcceleration(orientationAngles)

        val roll = normalizeRoll(Radian.toDegrees(gravity.z))
        val pitch = normalizePitch(0 - Radian.toDegrees(gravity.y))
        val azimuth = normalizeAzimuth(Radian.toDegrees(gravity.x), roll)

        rollAcceleration.rotateTo(roll)
        pitchAcceleration.rotateTo(pitch)
        azimuthAcceleration.rotateTo(azimuth)

        updateCounter++
    }

    private fun normalizeRoll(roll: Double) =
        Degree.normalizeTo180(roll)

    private fun normalizePitch(pitch: Double) =
        Degree.normalizeTo180(pitch)

    private fun normalizeAzimuth(azimuth: Double, roll: Double) =
        when {
            roll < -90 -> Degree.normalize(0 - azimuth) // look at screen from below
            roll > 90 -> Degree.normalize(0 - azimuth) // look at screen from below
            else -> Degree.normalize(azimuth)
        }
}