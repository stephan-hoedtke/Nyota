package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.*
import java.text.FieldPosition

/**
 * Common properties of
 */
interface IElement {

    /**
     * Unique identifier to specify the element. Used as argument to the element in a fragments.
     */
    val key: String

    /**
     * The name may be empty on special cases. Usually to be displayed in the UI.
     */
    val name: String

    val imageId: Int
    val largeImageId: Int

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