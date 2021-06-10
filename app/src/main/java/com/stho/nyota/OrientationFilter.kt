package com.stho.nyota

import com.stho.nyota.sky.utilities.*
import com.stho.nyota.views.Rotation


/*
    The class takes updates of the orientation vector by listening to onOrientationChanged(rotationMatrix).
    The values will be stored and smoothed with acceleration.
    A handler will regularly read the updated smoothed orientation
 */
class OrientationAccelerationFilter: IOrientationFilter {

    private val azimuthAcceleration: Acceleration = Acceleration(ACCELERATION_FACTOR)
    private val pitchAcceleration: Acceleration = Acceleration(ACCELERATION_FACTOR)
    private val rollAcceleration: Acceleration = Acceleration(ACCELERATION_FACTOR)
    private val centerAzimuthAcceleration: Acceleration = Acceleration(ACCELERATION_FACTOR)
    private val centerAltitudeAcceleration: Acceleration = Acceleration(ACCELERATION_FACTOR)

    override val currentOrientation: Orientation
        get() = Orientation(
            azimuth = Degree.normalize(azimuthAcceleration.position),
            pitch = Degree.normalizeTo180(pitchAcceleration.position),
            roll = Degree.normalizeTo180(rollAcceleration.position),
            centerAzimuth = Degree.normalize(centerAzimuthAcceleration.position),
            centerAltitude = Degree.normalizeTo180(centerAltitudeAcceleration.position)
        )

    override fun onOrientationChanged(rotationMatrix: FloatArray) {
        val rotation = Rotation(rotationMatrix)
        val orientation = rotation.getOrientation()
        onOrientationChanged(orientation)
    }

    private fun onOrientationChanged(orientation: Orientation) {
        if (Rotation.requireAdjustmentForLookingAtThePhoneFromBelow(orientation)) {
            setOrientation(Rotation.adjustForLookingAtThePhoneFromBelow(orientation))
        } else {
            setOrientation(orientation)
        }
    }

    private fun setOrientation(orientation: Orientation) {
        azimuthAcceleration.rotateTo(orientation.azimuth)
        pitchAcceleration.rotateTo(orientation.pitch)
        rollAcceleration.rotateTo(orientation.roll)
        centerAzimuthAcceleration.rotateTo(orientation.centerAzimuth)
        centerAltitudeAcceleration.rotateTo(orientation.centerAltitude)
    }

    companion object {
        private const val ACCELERATION_FACTOR = 0.3
    }
}

