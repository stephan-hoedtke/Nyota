package com.stho.nyota.sky.utilities

import java.util.*

interface IMoment {
    val utc: UTC
    val city: City
    val location: ILocation
    val timeZone: TimeZone
    val lst: Double
    val d: Double

    /**
     * Keep current location and timezone, but set a new time
     * @param timeInMillis UTC in milliseconds
     * @return a new moment instance
     */
    fun forUTC(timeInMillis: Long): IMoment

    /**
     * Keep current location and timezone, but set a new time
     * @param utc UTC
     * @return a new moment instance
     */
    fun forUTC(newUtc: UTC): IMoment
}
