package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Degree.Companion.cos

/**
 * Created by shoedtke on 31.08.2016.
 */
abstract class AbstractPlanet(override val name: String) : AbstractSolarSystemElement() {

    override val key: String
        get() = toKey(name)

    override val isPlanet: Boolean
        get() = true

    override fun calculatePhase(sun: Sun) {
        elongation = Degree.arcCos((sun.R * sun.R + R * R - mr * mr) / (2 * sun.R * R))
        FV = Degree.arcCos((mr * mr + R * R - sun.R * sun.R) / (2 * mr * R))
        phase = (1 + cos(FV)) / 2
    }

    private val magnAsString: String
        get() = Formatter.df2.format(magn)

    abstract fun calculateMagnitude()

    override fun getBasics(moment: Moment): PropertyList =
        super.getBasics(moment).apply {
            if (position?.isUp == true) {
                add(com.stho.nyota.R.drawable.sunset, "Previous Set", position?.prevSetTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunrise, "Rise", position?.riseTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunset, "Set", position?.setTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunrise, "Next Rise", position?.nextRiseTime, moment.timeZone)
            } else {
                add(com.stho.nyota.R.drawable.sunrise, "Previous Rise", position?.prevRiseTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunset, "Set", position?.setTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunrise, "Rise", position?.riseTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunset, "Next Set", position?.nextSetTime, moment.timeZone)
            }
            add(com.stho.nyota.R.drawable.empty, "Magnitude", magnAsString)
            add(com.stho.nyota.R.drawable.empty, "In south", position?.inSouth, moment.timeZone)
            add(com.stho.nyota.R.drawable.empty, "Culmination angle", Degree.fromDegree(position!!.culmination))
        }

    override fun getDetails(moment: Moment): PropertyList =
        super.getDetails(moment).apply {
            add(com.stho.nyota.R.drawable.empty, "FV", Degree.fromDegree(FV))
            add(com.stho.nyota.R.drawable.empty, "Phase", Formatter.df3.format(phase))
            add(com.stho.nyota.R.drawable.empty, "Phase angle", Formatter.df0.format(phaseAngle))
            add(com.stho.nyota.R.drawable.empty, "Parallax", Formatter.df3.format(parallacticAngle))
        }

    override fun getHeightFor(moment: IMoment): Double =
        getPlanetFor(moment).height

    private val height: Double
        get() = position!!.altitude - H0()

    override fun H0(): Double =
        -0.583

    protected abstract fun getPlanetFor(moment: IMoment): AbstractPlanet

    companion object {

        private fun toKey(name: String): String =
            "PLANET:$name"

        fun isValidKey(key: String): Boolean =
            key.startsWith("PLANET:")

        fun nameFromKey(key: String): String =
            key.substring(7)
    }
}