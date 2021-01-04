package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.*


/**
 * Created by shoedtke on 30.08.2016.
 */
class Sun : AbstractSolarSystemElement() {
    override val isSun: Boolean
        get() = true

    override val name: String
        get() = "Sun"

    override val imageId: Int
        get() = com.stho.nyota.R.drawable.sun

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.sun

    override fun updateBasics(d: Double) {
        N = 0.0
        i = 0.0
        w = Degree.normalize(282.9404 + 4.70935E-5 * d)
        a = 1.0 // AU
        e = 0.016709 - 1.151E-9 * d
        M = Degree.normalize(356.0470 + 0.9856002585 * d)
    }

    override fun updateHeliocentricLatitudeLongitude(d: Double) {
        ecl = 23.4393 - 3.563E-7 * d
        EA = Degree.normalize(M + e * Degree.RADEG * Degree.sin(M) * (1.0 + e * Degree.cos(M)))
        val xv = a * (Degree.cos(EA) - e)
        val yv = a * (Math.sqrt(1.0 - e * e) * Degree.sin(EA))
        v = Degree.arcTan2(yv, xv)
        mr = Math.sqrt(xv * xv + yv * yv)

        // mean longitude
        L = Degree.normalize(M + w)

        // The suns longitude
        longitude = Degree.normalize(v + w)
        latitude = 0.0
    }

    fun updateGeocentricAscensionDeclination() {

        // ecliptic rectangular geocentric coordinates
        x = mr * Degree.cos(longitude)
        y = mr * Degree.sin(longitude)
        z = 0.0

        // equatorial rectangular geocentric coordinates
        val xe = x
        val ye = y * Degree.cos(ecl)
        val ze = y * Degree.sin(ecl)

        // Sun's right ascension and declination
        RA = Degree.normalize(Degree.arcTan2(ye, xe))
        Decl = Degree.normalizeTo180(Degree.arcTan2(ze, Math.sqrt(xe * xe + ye * ye)))

        // Compute the geocentric distance:
        R = Math.sqrt(xe * xe + ye * ye + ze * ze)
    }

    private val ALTITUDE_SUNSET = -0.833 // Sun's upper limb touches the horizon; atmospheric refraction accounted for, in degree
    private val ALTITUDE_CIVIL_TWILIGHT = -6.0
    private val ALTITUDE_NAUTICAL_TWILIGHT_ALTITUDE = -12.0

    /**
     * Returns true if the sun's upper limb is over the horizon
     * @return
     */
    val isDayLight: Boolean
        get() = position!!.altitude >= ALTITUDE_SUNSET

    /**
     * Returns true if the sun is below the civil twilight altitude
     * @return
     */
    val isDark: Boolean
        get() = position!!.altitude < ALTITUDE_CIVIL_TWILIGHT

    /**
     * Returns true if the object is not in the shadow of the earth
     * @param height in km
     * @return
     */
    fun isVisibleAt(height: Double): Boolean {
        val angle = Degree.arcCos(Earth.RADIUS / (Earth.RADIUS + height))
        return position!!.altitude + angle > 0
    }

    override fun getHeightFor(moment: IMoment): Double {
        val sun = getSunFor(moment)
        return sun.height
    }

    private val height: Double
        get() = position!!.altitude - H0()

    override fun H0(): Double {
        return ALTITUDE_SUNSET
    }

    override fun getBasics(moment: Moment): PropertyList =
        super.getBasics(moment).apply {
            if (position?.isUp == true) {
                add(com.stho.nyota.R.drawable.sunrise, "Rise", position?.riseTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunset, "Set", position?.setTime, moment.timeZone)
            } else {
                add(com.stho.nyota.R.drawable.sunset, "Set", position?.setTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunrise, "Rise", position?.riseTime, moment.timeZone)
            }
        }

    override fun getDetails(moment: Moment): PropertyList =
        super.getDetails(moment).apply {
            if (position?.isUp == true) {
                add(com.stho.nyota.R.drawable.sunset, "Previous Set", position?.prevSetTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunrise, "Rise", position?.riseTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunset, "Set ", position?.setTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunrise, "Next Rise", position?.nextRiseTime, moment.timeZone)
            } else {
                add(com.stho.nyota.R.drawable.sunrise, "Previous Rise", position?.prevRiseTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunset, "Set ", position?.setTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunrise, "Rise", position?.riseTime, moment.timeZone)
                add(com.stho.nyota.R.drawable.sunset, "Next Set", position?.nextSetTime, moment.timeZone)
            }
            add(com.stho.nyota.R.drawable.empty, "In south", position?.inSouth, moment.timeZone)
            add(com.stho.nyota.R.drawable.empty, "Culmination angle", Degree.fromDegree(position!!.culmination))
        }

    companion object {

        const val RADIUS = 695700.0 // in km

        private fun getSunFor(moment: IMoment): Sun {
            val sun: Sun = getSunFor(moment.utc)
            sun.updateAzimuthAltitude(moment)
            return sun
        }

        // calculate RA and Decl for the sun at this time (independend of the current observer)
        private fun getSunFor(utc: UTC): Sun {
            val d = utc.dayNumber
            val sun = Sun()
            sun.updateBasics(d)
            sun.updateHeliocentricLatitudeLongitude(d)
            sun.updateGeocentricAscensionDeclination()
            return sun
        }
    }
}