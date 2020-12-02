package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Degree.Companion.cosines

/**
 * Created by shoedtke on 31.08.2016.
 */
abstract class AbstractPlanet : AbstractSolarSystemElement() {
    override val isPlanet: Boolean
        get() = true

    override fun calculatePhase(sun: Sun) {
        elongation = Degree.arcCos((sun.R * sun.R + R * R - mr * mr) / (2 * sun.R * R))
        FV = Degree.arcCos((mr * mr + R * R - sun.R * sun.R) / (2 * mr * R))
        phase = (1 + cosines(FV)) / 2
    }

    abstract fun calculateMagnitude()

    override fun getDetails(moment: Moment): PropertyList {
        val details: PropertyList = super.getDetails(moment)
        details.add(com.stho.nyota.R.drawable.sunset, "Set", position?.prevSetTime, moment.timeZone)
        details.add(com.stho.nyota.R.drawable.sunrise, "Rise", position?.riseTime, moment.timeZone)
        details.add(com.stho.nyota.R.drawable.sunset, "Set ", position?.setTime, moment.timeZone)
        details.add(com.stho.nyota.R.drawable.sunrise, "Rise", position?.nextRiseTime, moment.timeZone)
        details.add(com.stho.nyota.R.drawable.star, "FV", Degree.fromDegree(FV))
        details.add(com.stho.nyota.R.drawable.star, "Phase", Formatter.df3.format(phase))
        details.add(com.stho.nyota.R.drawable.star, "Phase angle", Formatter.df0.format(phaseAngle))
        details.add(com.stho.nyota.R.drawable.star, "Magnitude", Formatter.df2.format(magn))
        details.add(com.stho.nyota.R.drawable.star, "Parallax", Formatter.df3.format(parallacticAngle))
        details.add(com.stho.nyota.R.drawable.star, "In south", position?.inSouth, moment.timeZone)
        details.add(com.stho.nyota.R.drawable.star, "Culmination angle", Degree.fromDegree(position!!.culmination))
        return details
    }

    protected override fun getHeightFor(moment: IMoment): Double {
        val planet = getPlanetFor(moment)
        return planet.height
    }

    private val height: Double
        get() = position!!.altitude - H0()

    protected override fun H0(): Double {
        return -0.583
    }

    protected abstract fun getPlanetFor(moment: IMoment): AbstractPlanet
}