package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.createDefaultBerlinBuch
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.cos

// Implemented as static singleton. There is just one universe!
class Universe {

    val solarSystem = SolarSystem()
    val constellations = Constellations()
    val stars = Stars(constellations)
    val satellites = Satellites()
    val galaxies = Galaxies()
    val specials = ArrayList<SpecialElement>()
    val targets = Targets()
    val any = ArrayList<Anything>()
    val vip = ArrayList<Star>()

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
        stars.values.forEach { star -> star.updateAzimuthAltitude(moment) }
        satellites.values.forEach { satellite -> satellite.updateFor(moment) }
        constellations.values.forEach { constellation -> constellation.updateAzimuthAltitude(moment) }
        specials.forEach { special -> special.updateAzimuthAltitude(moment) }
        targets.values.forEach { target -> target.updateAzimuthAltitude(moment) }
        galaxies.values.forEach { galaxy -> galaxy.updateAzimuthAltitude(moment) }
        any.forEach { anything -> anything.updateAzimuthAltitude(moment) }

        return this
    }

    val zenit: Topocentric
        get() = Topocentric(0.0, 90.0)

    fun findPlanetByName(name: String?): AbstractPlanet? =
        name?.let { solarSystem.findPlanetByName(it) }

    fun findElementByKey(key: String): IElement? =
        solarSystem.findElementByKey(key)
            ?: satellites.findSatelliteByKey(key)
            ?: constellations.findConstellationByKey(key)
            ?: stars.findStarByKey(key)
            ?: targets.findTargetByKey(key)

    fun findNearestElementByPosition(position: Topocentric, magnitude: Double, sensitivityAngle: Double): IElement? {
        val azimuthTolerance = sensitivityAngle / position.azimuthDistanceFactor
        var distance: Double = Topocentric.INVALID_DISTANCE
        var brightness: Double = AbstractElement.INVALID_MAGNITUDE
        var element: IElement? = null

        for (e in solarSystem.elements) {
            if (e.isNear(position, azimuthTolerance, sensitivityAngle)) {
                val b = e.magn
                val d = e.distanceTo(position)
                if (b < brightness || (b == brightness && d < distance)) {
                    brightness = b
                    distance = d
                    element = e
                }
            }
        }

        for (s in stars.values) {
            if (s.isBrighterThan(magnitude) && s.isNear(position, azimuthTolerance, sensitivityAngle)) {
                val b = s.magn
                val d = s.distanceTo(position)
                if (b < brightness || (b == brightness && d < distance)) {
                    brightness = b
                    distance = d
                    element = s
                }
           }
        }

        for (s in satellites.values) {
            if (s.isNear(position, azimuthTolerance, sensitivityAngle)) {
                val b = -10.0
                val d = s.distanceTo(position)
                if (b < brightness || (b == brightness && d < distance)) {
                    brightness = b
                    distance = d
                    element = s
                }
            }
        }

        for (t in targets.values) {
            if (t.isNear(position, azimuthTolerance, sensitivityAngle)) {
                val b = -10.0
                val d = t.distanceTo(position)
                if (b < brightness || (b == brightness && d < distance)) {
                    brightness = b
                    distance = d
                    element = t
                }
            }
        }

        for (g in galaxies.values) {
            if (g.isNear(position, azimuthTolerance, sensitivityAngle)) {
                val b = g.magn
                val d = g.distanceTo(position)
                if (b < brightness || (b == brightness && d < distance)) {
                    brightness = b
                    distance = d
                    element = g
                }
            }
        }

        return element
    }
}
