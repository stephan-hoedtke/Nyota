package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.sky.utilities.createDefaultBerlin
import java.util.*

// Implemented as static singleton. There is just one universe!
class Universe {

    val solarSystem = SolarSystem()
    val stars = Stars()
    val constellations = Constellations()
    val satellites = Satellites()
    val vip = ArrayList<Star>()
    val specials = ArrayList<SpecialElement>()
    val targets = Targets()

    init {
        /* Solar system and stars... */
        UniverseInitializer(this).initialize()
    }

    fun setSatelliteTLE(satelliteName: String, elements: String) {
        satellites.findSatelliteByName(satelliteName)?.updateElements(elements)
    }


    /**
     * Returns the moment (observer + UTC) for which the Universe was calculated last
     * @return
     */
    // The moment (time + location
    var moment: Moment = Moment.forNow(City.createDefaultBerlin())
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

    fun findStarByName(name: String?): Star? {
        if (name != null && stars.exists(name)) {
            return stars[name]
        }
        return null
    }

    val zenit: Topocentric
        get() = Topocentric(0.0, 90.0)

    fun findConstellationByName(name: String?): Constellation? {
        if (name != null && constellations.exists(name)) {
            return constellations[name]
        }
        return null
    }

    fun findPlanetByName(name: String?): AbstractPlanet? {
        if (name != null) {
            for (celestial in solarSystem.planets) {
                if (celestial.name == name)
                    return celestial as AbstractPlanet
            }
        }
        return null
    }

    fun findElementByName(name: String?): IElement? {
        if (name != null) {

            // Any solar system element ?
            for (celestial in solarSystem.elements) {
                if (celestial.name == name)
                    return celestial
            }

            if (satellites.exists(name))
                return satellites[name]

            if (constellations.exists(name))
                return constellations[name]

            if (targets.exists(name))
                return targets[name]

            if (stars.exists(name))
                return stars[name]
        }
        return null
    }

    // may return null if there is no constellation that contains this star.
    fun findConstellationByStar(star: Star?): Constellation? {
        if (star != null) {
            for (constellation in constellations.values) {
                if (constellation.stars.contains(star)) {
                    return constellation
                }
            }
        }
        return null
    }
}
