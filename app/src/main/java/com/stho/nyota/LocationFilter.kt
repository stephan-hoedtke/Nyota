package com.stho.nyota


interface LocationFilter {
    fun onLocationChanged(location: android.location.Location)
    val currentLocation: com.stho.nyota.sky.utilities.Location
    val updateCounter: Long
    fun onResume()
}

/*
    The class takes updates of the current location by listening to onLocationChanged(android location).
    A handler will regularly read the updated location
 */
class SimpleLocationFilter : LocationFilter {

    override var updateCounter: Long = 0L
        private set

    override var currentLocation: com.stho.nyota.sky.utilities.Location = com.stho.nyota.sky.utilities.Location(0.0, 0.0)
        private set

    override fun onLocationChanged(location: android.location.Location) {
        currentLocation = com.stho.nyota.sky.utilities.Location.fromAndroidLocation(location)
        updateCounter++
    }

    override fun onResume() {
        updateCounter = 0L
    }
}