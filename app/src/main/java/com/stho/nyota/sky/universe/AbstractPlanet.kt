package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Degree.Companion.cos

/**
 * Created by shoedtke on 31.08.2016.
 */
abstract class AbstractPlanet : AbstractSolarSystemElement() {
    override val isPlanet: Boolean
        get() = true

    override fun calculatePhase(sun: Sun) {
        elongation = Degree.arcCos((sun.R * sun.R + R * R - mr * mr) / (2 * sun.R * R))
        FV = Degree.arcCos((mr * mr + R * R - sun.R * sun.R) / (2 * mr * R))
        phase = (1 + cos(FV)) / 2
    }

    abstract fun calculateMagnitude()

    override fun getDetails(moment: Moment): PropertyList =
        super.getDetails(moment).apply {
            add(com.stho.nyota.R.drawable.sunset, "Set", position?.prevSetTime, moment.timeZone)
            add(com.stho.nyota.R.drawable.sunrise, "Rise", position?.riseTime, moment.timeZone)
            add(com.stho.nyota.R.drawable.sunset, "Set ", position?.setTime, moment.timeZone)
            add(com.stho.nyota.R.drawable.sunrise, "Rise", position?.nextRiseTime, moment.timeZone)
            add(com.stho.nyota.R.drawable.empty, "FV", Degree.fromDegree(FV))
            add(com.stho.nyota.R.drawable.empty, "Phase", Formatter.df3.format(phase))
            add(com.stho.nyota.R.drawable.empty, "Phase angle", Formatter.df0.format(phaseAngle))
            add(com.stho.nyota.R.drawable.empty, "Magnitude", Formatter.df2.format(magn))
            add(com.stho.nyota.R.drawable.empty, "Parallax", Formatter.df3.format(parallacticAngle))
            add(com.stho.nyota.R.drawable.empty, "In south", position?.inSouth, moment.timeZone)
            add(com.stho.nyota.R.drawable.empty, "Culmination angle", Degree.fromDegree(position!!.culmination))
        }

    override fun getHeightFor(moment: IMoment): Double =
        getPlanetFor(moment).height

    private val height: Double
        get() = position!!.altitude - H0()

    override fun H0(): Double =
        -0.583

    protected abstract fun getPlanetFor(moment: IMoment): AbstractPlanet
}