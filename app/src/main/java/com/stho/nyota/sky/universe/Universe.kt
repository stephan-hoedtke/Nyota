package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.sky.utilities.createDefaultBerlinBuch
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
            val tokens: List<String> = Constellation.splitConstellationStarName(name)
            if (tokens.size == 2) {
                findStarInConstellationByName(constellationName = tokens[0], starName = tokens[1])
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
}
