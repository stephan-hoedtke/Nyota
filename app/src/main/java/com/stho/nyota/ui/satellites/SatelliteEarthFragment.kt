package com.stho.nyota.ui.satellites


// read: https://medium.com/mindorks/have-you-heard-about-open-street-map-d6c51dc00bea
// read: https://developer.blackberry.com/android/apisupport/replc_google_maps_with_openstreet.html
// read: https://www.maptiler.com/news/2017/01/openmaptiles-vector-tiles-from/
// --> https://osmdroid.github.io/osmdroid/Map-Sources.html

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.stho.nyota.AbstractFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.SatellitePreview
import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.Moment
import kotlinx.android.synthetic.main.fragment_satellite_earth.view.*
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow

class SatelliteEarthFragment : AbstractFragment() {

    private lateinit var viewModel: SatelliteEarthViewModel

    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    private var previewMarkers: ArrayList<Marker>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val satelliteName: String? = getSatelliteNameFromArguments()
        viewModel = createSatelliteEarthViewModel(satelliteName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_satellite_earth, container, false)
        createMapView(root.map, requireContext())
        viewModel.universeLD.observe(viewLifecycleOwner, Observer { universe -> onUpdateSatellite(universe.moment) })
        viewModel.currentLocationLD.observe(viewLifecycleOwner, Observer { city -> onUpdateCurrentLocation(city) })
        viewModel.zoomLD.observe(viewLifecycleOwner, Observer { zoom -> onUpdateZoom(zoom) })

        root.buttonZoomIn.setOnClickListener { viewModel.zoomIn() }
        root.buttonZoomOut.setOnClickListener { viewModel.zoomOut() }
        root.buttonHome.setOnClickListener { setCenter(viewModel.repository.currentAutomaticLocation) }
        root.buttonTarget.setOnClickListener { setCenter(viewModel.satellite.location) }
        return root
    }

    private fun createMapView(map: MapView, context: Context) {
        Configuration.getInstance().load(
            context, PreferenceManager.getDefaultSharedPreferences(
                context.applicationContext
            )
        )
        createMapView(map)
    }

    private fun createMapView(map: MapView) {
        map.minZoomLevel = SatelliteEarthViewModel.MIN_ZOOM
        map.maxZoomLevel = SatelliteEarthViewModel.MAX_ZOOM
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        map.controller.setZoom(viewModel.zoom)
        map.setMultiTouchControls(true)
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.isFlingEnabled = true
    }

    private fun resumeMapView() {
        view?.apply {
            map.onResume()
            map.addMapListener(mapListener)
            map.setOnTouchListener(touchListener)
        }
    }

    private fun pauseMapView() {
        view?.apply {
            map.removeMapListener(mapListener)
            map.onPause()
        }
    }

    private val mapListener by lazy {
        object : MapListener {
            override fun onScroll(event: ScrollEvent): Boolean {
                view?.also {
                    viewModel.updateCenter(it.map.mapCenter)
                }
                return true
            }

            override fun onZoom(event: ZoomEvent): Boolean {
                view?.also {
                    viewModel.updateZoom(it.map.zoomLevelDouble)
                }
                return true
            }
        }
    }

    private val touchListener by lazy {
        object : View.OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                // nothing to do for now...
                return false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumeMapView()
    }

    override fun onPause() {
        super.onPause()
        pauseMapView()
        clearMarkers()
    }

    private fun getSatelliteNameFromArguments(): String? {
        return arguments?.getString("SATELLITE")
    }

    private fun onUpdateSatellite(moment: Moment) {
        bind(moment, viewModel.satellite)
    }

    private fun bind(moment: Moment, satellite: Satellite) {
        displaySatellite(satellite, moment)
        displayTitle(satellite, moment)
        displayZoom()
        updateActionBar(satellite.displayName, toLocalDateString(moment))
    }

    private fun onUpdateZoom(zoom: Double) {
        view?.apply {
            map.controller.setZoom(zoom)
        }
    }

    private fun displaySatellite(satellite: Satellite, moment: Moment) {
        satellite.updateFor(moment)
        view?.apply {
            try {
                updateCityMarker(moment.city)
                updateSatelliteMarker(satellite)
                updateVisibilityMarker(satellite)
                updatePreviewMarkers(satellite, moment.location)
                map.invalidate()
            } catch (ex: Exception) {
                //ignore
            }
        }
    }

    private fun displayZoom() {
        view?.also {
            it.zoomInfo.text = viewModel.zoom.toString()
        }
    }

    private fun onUpdateCurrentLocation(city: City) {
        updateCurrentLocationMarker(city.location.toGeoPoint())
    }

    private fun updateCityMarker(city: City) {
        view?.apply {
            var marker = findMarkerById(cityMarkerId)
            if (marker == null) {
                marker = Marker(map).also {
                    it.id = cityMarkerId
                    it.title = city.name
                    it.position = city.location.toGeoPoint()
                    // it.subDescription = toDescription(position)
                    it.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    it.infoWindow = BasicInfoWindow(R.layout.marker_info_window, map)
                }
                map.overlays.add(marker)
                map.invalidate()
            }
            else {
                marker.also {
                    it.position.latitude = city.latitude
                    it.position.longitude = city.longitude
                    // it.subDescription = toDescription(position)
                }
            }
         }
    }

    private fun updateCurrentLocationMarker(you: GeoPoint) {
        view?.apply {
            var marker = findMarkerById(currentLocationMarkerId)
            if (marker == null) {
                marker = Marker(map).also {
                    it.setIconAnchorCenter(getIcon(R.drawable.target128))
                    it.id = currentLocationMarkerId
                    it.title = "You"
                    it.position = you
                    // it.subDescription = toDescription(position)
                    it.infoWindow = BasicInfoWindow(R.layout.marker_info_window, map)
                }
                map.overlays.add(marker)
                map.invalidate()
            } else {
                marker.also {
                    it.position.latitude = you.latitude
                    it.position.longitude = you.longitude
                    // it.subDescription = toDescription(position)
                    // it.setThisIcon(if (viewModel.useTracking) targetHotIcon else targetIcon)
                }
            }
        }
    }

    private fun updateSatelliteMarker(satellite: Satellite) {
        val iss = GeoPoint(satellite.location.latitude, satellite.location.longitude)
        view?.apply {
            var marker = findMarkerById(satelliteMarkerId)
            if (marker == null) {
                marker = Marker(map).also {
                    it.setIconAnchorCenter(getIcon(satellite.redIconId))
                    it.id = satelliteMarkerId
                    it.title = satellite.name
                    it.position = iss
                    // it.subDescription = toDescription(position)
                    it.infoWindow = BasicInfoWindow(R.layout.marker_info_window, map)
                }
                map.overlays.add(marker)
                map.controller.setCenter(iss) // Center when we draw the iss the first time.
                map.invalidate()
            } else {
                marker.also {
                    it.position.latitude = satellite.location.latitude
                    it.position.longitude = satellite.location.longitude
                    // it.subDescription = toDescription(position)
                    // it.setThisIcon(if (viewModel.useTracking) targetHotIcon else targetIcon)
                }
            }
        }
    }

    private fun updateVisibilityMarker(satellite: Satellite) {
        val points: List<GeoPoint> = SatellitePreview.getVisibilityPoints(satellite)
        view?.apply {
            var marker = findPolygonById(visibilityMarkerId)
            if (marker == null) {
                marker = Polygon().also {
                    it.id = visibilityMarkerId
                    it.fillPaint.color = Color.TRANSPARENT
                    it.outlinePaint.color = Color.RED
                    it.outlinePaint.strokeWidth = DEFAULT_STROKE_WIDTH
                    it.isVisible = true
                    it.points = points
                }
                map.overlays.add(marker)
                map.invalidate()
            } else {
                marker.also {
                    it.points = points
                }
            }
        }
    }

    private fun updatePreviewMarkers(
        satellite: Satellite,
        observer: com.stho.nyota.sky.utilities.Location
    ) {
        view?.apply {
            if (previewMarkers == null) {
                previewMarkers = ArrayList<Marker>().also {
                    // calculate and assign once only...
                    updatePreviewMarkers(satellite, observer, map, it)
                }
            }
        }
    }

    private fun updatePreviewMarkers(
        satellite: Satellite,
        observer: com.stho.nyota.sky.utilities.Location,
        map: MapView,
        previewMarkers: ArrayList<Marker>
    ) {
        val points = SatellitePreview.getPreviewPoints(satellite, observer)
        for (point in points) {

            val marker = Marker(map).also {
                it.setIconAnchorCenter(getIcon(point.resourceId))
                it.id = previewMarkerId
                it.title = point.title
                it.position = point
                // it.subDescription = toDescription(position)
                it.infoWindow = BasicInfoWindow(R.layout.marker_info_window, map)
            }
            map.overlays.add(marker)
            previewMarkers.add(marker)
        }
    }

    private fun clearMarkers() {
        view?.apply {
            previewMarkers?.also {
                for (marker: Marker in it) {
                    marker.remove(view?.map)
                }
                it.clear()
            }
            previewMarkers = null
        }
    }

    private fun setCenter(location: com.stho.nyota.sky.utilities.Location) {
        view?.apply {
            map.controller.setCenter(location.toGeoPoint())
        }
    }

    private fun getIcon(resourceId: Int): BitmapDrawable {
        return ContextCompat.getDrawable(requireActivity(), resourceId) as BitmapDrawable
    }

    private fun findMarkerById(id: String): Marker? {
        view?.map?.overlays?.forEach {
            if (it is Marker && it.id == id) {
                return it
            }
        }
        return null
    }

    private fun findPolygonById(id: String): Polygon? {
        view?.map?.overlays?.forEach {
            if (it is Polygon && it.id == id) {
                return it
            }
        }
        return null
    }

    private fun displayTitle(element: IElement, moment: Moment) {
        // TODO: what to be done?
//        setTitle(
//            element.getName()
//                    + Formatter.SPACE
//                    + Formatter.toString(
//                moment.getUTC(),
//                moment.getTimeZone(),
//                Formatter.TimeFormat.DATETIME_TIMEZONE
//            )
//        )
    }

    companion object {
        private const val cityMarkerId = "city"
        private const val previewMarkerId = "preview"
        private const val currentLocationMarkerId = "you"
        private const val satelliteMarkerId = "satellite"
        private const val visibilityMarkerId = "visibility"
        private const val DEFAULT_STROKE_WIDTH = 3f
    }
}

// Hack: the osmdroid has a bug calculating the anchor: see explanation in MeHere
fun Marker.setIconAnchorCenter(image: BitmapDrawable) {
    val fx: Float = Marker.ANCHOR_CENTER * image.bitmap.width / image.intrinsicWidth
    val fy: Float = Marker.ANCHOR_CENTER * image.bitmap.height / image.intrinsicHeight
    this.icon = image
    //this.image = image
    this.setAnchor(fx, fy)
}
