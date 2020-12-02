package com.stho.nyota.sky.universe

/*
    convert android location altitude from m into km
 */
fun android.location.Location.altitudeInKm(): Double {
    return 0.001 * altitude;
}

