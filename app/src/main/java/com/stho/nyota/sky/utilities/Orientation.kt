package com.stho.nyota.sky.utilities

import com.stho.nyota.sky.universe.IElement

/*******************************************************************************************************
    Orientation:
    - is the direction in which your eyes look into the phone.
    - orthogonal to the phones surface, going out from the back.
    - Z-axis of the phone

    Pitch:
    - is the direction from bottom to top of the phone
    - parallel to the phones surface, going out on the top.
    - Y-axis of the phone
    --> [-90 -- +90] positive: to the ground (it's not [-180 -- +180])

    Azimuth:
    - is the angle between Pitch (or the orientation) and the geographic north at the horizon plane.

    Roll:
    - [-180 -- +180] positive: to the left (it's not [-90 -- +90])
 *****************************************************************************************************/

class Orientation(val azimuth: Double, val pitch: Double, val direction: Double, val roll: Double) {

    fun getRotationToTargetAt(element: IElement): Float {
        val targetAzimuth = element.position?.azimuth ?: 0.0
        return Angle.getAngleDifference(x = targetAzimuth, y = azimuth).toFloat()
    }

    fun getRotationToNorth(): Float =
        Angle.normalizeTo180(-azimuth).toFloat()

    companion object {
        val defaultOrientation: Orientation
            get() = Orientation(0.0, 0.0, -90.0, 0.0)
    }
}

