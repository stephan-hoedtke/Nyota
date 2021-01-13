package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.*
import java.text.FieldPosition

/**
 * Common properties of
 */
interface IElement {
    val name: String
    val imageId: Int
    val largeImageId: Int
    val uniqueName: String

    fun getBasics(moment: Moment): PropertyList
    fun getDetails(moment: Moment): PropertyList

    // Azimuth + Altitude for a local observer
    val position: Topocentric?

    // Returns the visibilityIcon
    val visibility: Int

    // Returns true if the elements is above the horizon
    val isVisible: Boolean

    fun isNear(otherPosition: Topocentric, toleranceInDegree: Double = 0.01): Boolean

    fun distanceTo(otherPosition: Topocentric): Double
}