package com.stho.nyota.sky.universe

import androidx.core.text.isDigitsOnly
import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.sky.utilities.createDefaultBerlinBuch
import java.text.FieldPosition
import java.util.*
import kotlin.collections.ArrayList

// Implemented as static singleton. There is just one universe!
class Universe {

    val solarSystem = SolarSystem()
    val constellations = Constellations()
    val stars = Stars(constellations)
    val satellites = Satellites()
    val vip = ArrayList<Star>()
    val extra = ArrayList<Star>()
    val specials = ArrayList<SpecialElement>()
    val targets = Targets()

    fun setSatelliteTLE(satelliteName: String, elements: String) {
        satellites.findSatelliteByName(satelliteName)?.updateElements(elements)
    }

    /**
     * Returns the moment (observer + UTC) for which the Universe was calculated last
     * @return
     */
    // The moment (time + location
    var moment: Moment = Moment.forNow(City.createDefaultBerlinBuch())
        private set

    fun updateForNow(): Universe {
        val newMoment = moment.forNow()
        return updateFor(newMoment, true)
    }
    /**
     * forUTC the position (azimuth and altitude) for a given location and Local Sidereal Time (in angleInHours)
     * the phase, rise times etc will not be calculated (expensive)
     * @param moment time and location
     */
    @JvmOverloads
    fun updateFor(moment: Moment, calculatePhase: Boolean = false): Universe {
        this.moment = moment

        // calculate RA + Decl
        solarSystem.update(moment.utc)

        // calculate azimuth + altitude
        solarSystem.updateAzimuthAltitude(moment)

        if (calculatePhase)
            solarSystem.updatePhase(moment)

        // calculate azimuth + altitude for all objects
        for (star in stars.values) star.updateAzimuthAltitude(moment)
        for (satellite in satellites.values) satellite.updateFor(moment)
        for (constellation in constellations.values) constellation.updateAzimuthAltitude(moment)
        for (special in specials) special.updateAzimuthAltitude(moment)
        for (target in targets.values) target.updateAzimuthAltitude(moment)

        return this
    }

    fun findStarByName(name: String?): Star? =
        name?.let {
            // Usually the name should just be the HD-catalog-number ...
            if (it.isDigitsOnly()) {
                val hd: Int = it.toInt()
                stars[hd]
            } else {
                stars.findStarByName(it)
            }
        }

    private fun findStarInConstellationByName(constellationName: String, starName: String): Star? =
        findConstellationByName(constellationName)?.findStarInConstellationByName(starName)

    val zenit: Topocentric
        get() = Topocentric(0.0, 90.0)

    fun findConstellationByName(name: String?): Constellation? =
        name?.let { constellations[it] }

    fun findPlanetByName(name: String?): AbstractPlanet? =
        name?.let { solarSystem.findPlanetByName(it) }

    fun findElementByName(name: String?): IElement? =
        name?.let {
            solarSystem.elements.firstOrNull { e -> e.name == it }
                ?: satellites[it]
                ?: constellations[it]
                ?: targets[it]
                ?: findStarByName(name)
        }

    fun findNearestElementByPosition(position: Topocentric): IElement? {
        val tolerance = 10.0
        var distance = Topocentric.INVALID_DISTANCE
        var element: IElement? = null

        for (e in solarSystem.elements) {
            if (e.isNear(position, tolerance)) {
                val d = e.distanceTo(position)
                if (d < distance) {
                    distance = d
                    element = e
                }
            }
        }
        for (c in constellations.values) {
            if (c.isNear(position, tolerance)) {
                val d = c.distanceTo(position)
                if (d < distance) {
                    distance = d
                    element = c
                }
            }
        }
        for (s in stars.values) {
            if (s.isNear(position, tolerance)) {
                val d = s.distanceTo(position)
                if (d < distance) {
                    distance = d
                    element = s
                }
           }
        }
        for (s in satellites.values) {
            if (s.isNear(position, tolerance)) {
                val d = s.distanceTo(position)
                if (d < distance) {
                    distance = d
                    element = s
                }
            }
        }
        for (t in targets.values) {
            if (t.isNear(position, tolerance)) {
                val d = t.distanceTo(position)
                if (d < distance) {
                    distance = d
                    element = t
                }
            }
        }
        return element
    }
}
