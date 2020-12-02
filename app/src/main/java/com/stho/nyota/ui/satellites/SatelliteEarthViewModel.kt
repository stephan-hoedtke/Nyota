package com.stho.nyota.ui.satellites

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.stho.nyota.RepositoryViewModelArgs
import com.stho.nyota.repository.Repository
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.createDefaultBerlin
import org.osmdroid.api.IGeoPoint
import kotlin.math.max
import kotlin.math.min

class SatelliteEarthViewModel(application: Application, repository: Repository, val satellite: Satellite) : RepositoryViewModelArgs(application, repository) {

    val currentLocationLD: LiveData<City>
        get() = Transformations.map(repository.currentAutomaticMomentLD) { moment -> moment.city }

    val zoomLD: LiveData<Double>
        get() = repository.settings.zoomLD

    var zoom: Double
        get() = repository.settings.zoom
        set(value) {
            repository.settings.zoom = value
        }

    internal fun updateCenter(@Suppress("UNUSED_PARAMETER") point: IGeoPoint) {
        // ignore. We don't care about the center in the view model
    }

    internal fun zoomIn() {
        val newZoom = zoom + 1.0
        if (newZoom <= MAX_ZOOM) {
            zoom = min(MAX_ZOOM, newZoom)
        }
    }

    internal fun zoomOut() {
        val newZoom = zoom - 1.0
        if (newZoom >= MIN_ZOOM) {
            zoom = max(MIN_ZOOM, newZoom)
        }
    }

    internal fun updateZoom(newZoom: Double) {
        zoom = newZoom
    }

    companion object {
        internal const val MAX_ZOOM: Double = 21.0
        internal const val MIN_ZOOM: Double = 2.0
    }
}