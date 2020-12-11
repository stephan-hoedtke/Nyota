package com.stho.nyota.sky.universe

import com.stho.nyota.sky.universe.Algorithms.getECI
import com.stho.nyota.sky.universe.Algorithms.getLocationForECI
import com.stho.nyota.sky.universe.Algorithms.getTopocentricFromPosition
import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Formatter
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
 */
class Satellite(override val name: String, val displayName: String, val noradSatelliteNumber: Int, var elements: String) : AbstractSatellite(), IDBObject {

    init {
        updateElements(elements)
    }

    override var id: Long = 0

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

    val updated: Calendar
        get() = UTC.forJulianDay(tle.Epoch).getGmt()

    fun updateFor(moment: Moment) {
        val julianDay: Double = moment.utc.julianDay
        this.updateFor(julianDay)
        // TODO relative is not used, why?
        val eci = getECI(moment.location, julianDay)
        val relative = positionVector.minus(eci)
        position = getTopocentricFromPosition(moment, positionVector)
    }

    override fun updateFor(julianDay: Double) {
        SatelliteAlgorithms.calculatePositionVelocity(tle, julianDay, positionVector, velocity)
        location = getLocationForECI(positionVector, julianDay)
    }

    companion object {
        private const val ISS: Int = 25544
        private const val HST: Int = 20580
        private const val CHEOPS: Int = 44874

        fun create(id: Long, name: String, displayName: String, noradSatelliteNumber: Int, elements: String): Satellite {
            return Satellite(name, displayName, noradSatelliteNumber, elements).apply {
                this.id = id
            }
        }
    }
}