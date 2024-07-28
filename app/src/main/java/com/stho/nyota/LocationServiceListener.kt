package com.stho.nyota

import android.content.Context
import android.location.LocationManager
import android.os.Bundle

interface ILocationFilter {
    fun onLocationChanged(location: android.location.Location)
}

class LocationServiceListener(context: Context, private val filter: ILocationFilter) : android.location.LocationListener {

    private var locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    internal fun onResume() =
        enableLocationListener()

    internal fun onPause() =
        disableLocationListener()

    private fun enableLocationListener() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
        } catch (ex: SecurityException) {
            // Ignore for now...
            // We implicitly check for success using locationFilter.updateCounter
        }
    }

    private fun disableLocationListener() {
        try {
            locationManager.removeUpdates(this)
        } catch (ex: SecurityException) {
            //ignore
        }
    }

    override fun onLocationChanged(location: android.location.Location) {
        filter.onLocationChanged(location)
    }
}