package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.IMoment
import com.stho.nyota.sky.utilities.UTC

/**
 * Created by shoedtke on 16.09.2016.
 */
abstract class AbstractSolarSystem : ISolarSystem {
    protected var utc: UTC? = null

    lateinit var elements: List<AbstractSolarSystemElement>

    lateinit var sun: Sun
    lateinit var moon: Moon
    lateinit var mercury: Mercury
    lateinit var venus: Venus
    lateinit var mars: Mars
    lateinit var jupiter: Jupiter
    lateinit var saturn: Saturn
    lateinit var uranus: Uranus
    lateinit var neptune: Neptune

    /**
     * calculates the geocentric coordinates of the celestial object: RA (right ascension) Decl (declination)
     * @param utc the current time
     */
    override fun update(utc: UTC) {
        this.utc = utc
        updateForDayNumber(utc.dayNumber)
    }

    private fun updateForDayNumber(d: Double) {
        sun.updateBasics(d)
        sun.updateHeliocentricLatitudeLongitude(d)
        sun.updateGeocentricAscensionDeclination()

        for (element in elements) {
            if (element.isMoon || element.isPlanet) {
                element.updateBasics(d)
                element.updateHeliocentricLatitudeLongitude(d)
            }
        }

        moon.applyPerturbations(sun)
        jupiter.applyPerturbations(saturn)
        saturn.applyPerturbations(jupiter)
        uranus.applyPerturbations(jupiter, saturn)

        for (element in elements) {
            if (element.isMoon || element.isPlanet) {
                element.updateGeocentricAscensionDeclination(sun)
            }
        }
    }

    override fun updateAzimuthAltitude(moment: IMoment) {
        moon.applyTopocentricCorrection(moment)

        for (element in elements) {
            element.updateAzimuthAltitude(moment)
        }
    }

    override fun updatePhase(moment: IMoment) {
        sun.calculateSetRiseTimes(moment)
        moon.calculatePhase(sun)
        moon.calculateParallacticAngle(moment)
        moon.calculateMagnitude()
        moon.calculateSetRiseTimes(moment)
        moon.calculateShadows(sun)

        for (element in elements) {
            if (element.isPlanet) {
                val planet: AbstractPlanet = element as AbstractPlanet
                planet.calculatePhase(sun)
                planet.calculateMagnitude()
                planet.calculateParallacticAngle(moment)
                planet.calculateSetRiseTimes(moment)
            }
        }
    }

    companion object {
        /**
         * Calcuate geocentric position and horizontal position of the sun for the given moment
         * @param moment as UTC and observer location
         * @return Sun
         */
        @JvmStatic
        fun getSunFor(moment: IMoment): Sun {
            val sun = Sun()
            sun.updateBasics(moment.d)
            sun.updateHeliocentricLatitudeLongitude(moment.d)
            sun.updateGeocentricAscensionDeclination()
            sun.updateAzimuthAltitude(moment)
            return sun
        }
    }
}

