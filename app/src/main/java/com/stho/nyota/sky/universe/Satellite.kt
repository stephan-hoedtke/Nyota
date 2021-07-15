package com.stho.nyota.sky.universe

import com.stho.nyota.sky.universe.Algorithms.getECI
import com.stho.nyota.sky.universe.Algorithms.getLocationForECI
import com.stho.nyota.sky.universe.Algorithms.getTopocentricFromPosition
import com.stho.nyota.sky.utilities.IDBObject
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.UTC
import java.util.*

/**
 * Created by shoedtke on 20.01.2017.
 *
 * name: short name
 * noradSatelliteNumber: number of the satellite in the NORAD catalog
 * description: human readable name
 * elements: serialized TLE (Two line elements)
 *  -------------------------------------------------------------
 *  name = ISS
 *  noradSatelliteNumber = 25544
 *  description = International Space Station
 *  -------------------------------------------------------------
 *  name = HST
 *  noradSatelliteNumber = 20580
 *  description = Hubble Space Telescope
 *
 *  see for the brightest satellites:
 *  https://www.n2yo.com/satellites/?c=1#:~:text=Satellites%20are%20visible%20when%20the,normally%20brighter%20than%20magnitude%204.
 */
class Satellite private constructor(override var id: Long, override val name: String, val friendlyName: String, val noradSatelliteNumber: Int, var elements: String) : AbstractSatellite(), IDBObject {

    init {
        updateElements(elements)
    }

    override val key: String =
        toKey(noradSatelliteNumber)

    override fun toString(): String =
        name

    override val uniqueTransientId: Long by lazy { System.nanoTime() }

    override var status: IDBObject.Status = IDBObject.Status.NEW

    override val isToDelete
        get() = IDBObject.isToDelete(status)

    override val isNew
        get() = IDBObject.isNew(status)

    override val isPersistent
        get() = IDBObject.isPersistent(status)

    override val imageId: Int
        get() = when (tle.noradSatelliteNumber) {
            ISS -> com.stho.nyota.R.drawable.satellite_iss
            HST -> com.stho.nyota.R.drawable.satellite_hubble
            CHEOPS -> com.stho.nyota.R.drawable.satellite_cheops
            else -> com.stho.nyota.R.drawable.satellite
        }

    override lateinit var tle: TLE
        private set

    fun updateElements(elements: String) {
        this.elements = elements
        this.tle = TLE.deserialize(elements)
    }

    fun updateTLE(tle: TLE) {
        this.elements = tle.serialize()
        this.tle = tle
    }

    override val largeImageId: Int
        get() = imageId

    val redIconId: Int
        get() = when (tle.noradSatelliteNumber) {
            ISS -> com.stho.nyota.R.drawable.satellite_iss128
            HST -> com.stho.nyota.R.drawable.satellite_hubble128
            CHEOPS -> com.stho.nyota.R.drawable.satellite_cheops128
            else -> com.stho.nyota.R.drawable.satellite128
        }

    val azimuthAsString: String
        get() = position?.azimuthAsString ?: ""

    val altitudeAsString: String
        get() = position?.altitudeAsString ?: ""

    fun updateFor(moment: Moment) {
        val julianDay: Double = moment.utc.julianDay
        this.updateFor(julianDay)
        position = getTopocentricFromPosition(moment, positionVelocity.position)
    }

    override fun updateFor(julianDay: Double) {
        positionVelocity = SatelliteAlgorithms.calculatePositionVelocity(tle, julianDay)
        location = getLocationForECI(positionVelocity.position, julianDay)
    }

    companion object {
        private const val ISS: Int = 25544
        private const val HST: Int = 20580
        private const val CHEOPS: Int = 44874

        private fun toKey(noradSatelliteNumber: Int): String =
            "SATELLITE:$noradSatelliteNumber"

        fun isValidKey(key: String): Boolean =
            key.startsWith("SATELLITE:")

        fun noradSatelliteNumberFromKey(key: String): Int =
            key.substring(10).toInt()

        fun createNewSatellite(name: String, displayName: String, noradSatelliteNumber: Int, elements: String): Satellite =
            Satellite(id = 0L, name, displayName, noradSatelliteNumber, elements)

        fun createSatelliteWithId(id: Long, name: String, displayName: String, noradSatelliteNumber: Int, elements: String): Satellite =
            Satellite(id, name, displayName, noradSatelliteNumber, elements)
    }
}