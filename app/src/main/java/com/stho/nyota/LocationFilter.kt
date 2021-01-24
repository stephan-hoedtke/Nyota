package com.stho.nyota



/*
    The class takes updates of the current location by listening to onLocationChanged(android location).
    A handler will regularly read the updated location
 */
class LocationFilter : ILocationFilter {

    private var updateCounter: Long = 0L
    private var stableCounter: Long = 0L

    var currentLocation: com.stho.nyota.sky.utilities.Location = com.stho.nyota.sky.utilities.Location(0.0, 0.0)
        private set

    override fun onLocationChanged(location: android.location.Location) {
        com.stho.nyota.sky.utilities.Location.fromAndroidLocation(location).also {
            if (currentLocation.isNearTo(it)) {
                stableCounter++
            } else {
                stableCounter = 0
            }
            currentLocation = it
            updateCounter++
        }
    }

    val isActive: Boolean
        get() = updateCounter > 0

    val isInActive: Boolean
        get() = !isActive

    val isStable: Boolean
        get() = stableCounter > NUMBER_REQUIRED_STABLE_UPDATES

    companion object {
        private const val NUMBER_REQUIRED_STABLE_UPDATES = 3
    }
}
