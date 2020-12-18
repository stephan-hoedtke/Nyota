package com.stho.nyota

import android.content.Context
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Bundle

class LocationServiceListener(context: Context, private val filter: ILocationFilter) : android.location.LocationListener {

    private var locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    var onError: (() -> Unit)? = null

    internal fun onResume() =
        enableLocationListener()

    internal fun onPause() =
        disableLocationListener()

    private fun enableLocationListener() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
        } catch (ex: SecurityException) {
            onError?.invoke()
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

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Ignored
    }

    override fun onProviderEnabled(provider: String?) {
        // Ignored
    }

    override fun onProviderDisabled(provider: String?) {
        // Ignored
    }


}