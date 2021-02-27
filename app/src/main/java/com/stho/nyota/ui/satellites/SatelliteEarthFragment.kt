package com.stho.nyota.ui.satellites


// read: https://medium.com/mindorks/have-you-heard-about-open-street-map-d6c51dc00bea
// read: https://developer.blackberry.com/android/apisupport/replc_google_maps_with_openstreet.html
// read: https://www.maptiler.com/news/2017/01/openmaptiles-vector-tiles-from/
// --> https://osmdroid.github.io/osmdroid/Map-Sources.html

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.stho.nyota.AbstractFragment
import com.stho.nyota.AbstractViewModel
import com.stho.nyota.R
import com.stho.nyota.databinding.FragmentSatelliteEarthBinding
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.SatellitePreview
import com.stho.nyota.sky.utilities.*
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow


class SatelliteEarthFragment : AbstractFragment() {

    private lateinit var viewModel: SatelliteEarthViewModel
    private var bindingReference: FragmentSatelliteEarthBinding? = null
    private val binding: FragmentSatelliteEarthBinding get() = bindingReference!!
    private val icons: HashMap<Int, BitmapDrawable> = HashMap()


    override val abstractViewModel: AbstractViewModel
        get() = viewModel

    private var previewMarkers: ArrayList<Marker>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val satelliteKey: String? = getSatelliteKeyFromArguments()
        viewModel = createSatelliteEarthViewModel(satelliteKey)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingReference = FragmentSatelliteEarthBinding.inflate(inflater, container, false)

        createMapView(binding.map, requireContext())

        binding.buttonZoomIn.setOnClickListener { viewModel.zoomIn() }
        binding.buttonZoomOut.setOnClickListener { viewModel.zoomOut() }
        binding.buttonHome.setOnClickListener { setCenter(viewModel.repository.currentAutomaticLocation) }
        binding.buttonTarget.setOnClickListener { setCenter(viewModel.satellite.location) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.universeLD.observe(viewLifecycleOwner, { universe -> onUpdateSatellite(universe.moment) })
        viewModel.currentAutomaticLocationLD.observe(viewLifecycleOwner, { location -> onUpdateCurrentAutomaticLocation(location) })
        viewModel.zoomLD.observe(viewLifecycleOwner, { zoom -> onUpdateZoom(zoom) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
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
        binding.map.onResume()
        binding.map.addMapListener(mapListener)
    }

    private fun pauseMapView() {
        binding.map.removeMapListener(mapListener)
        binding.map.onPause()
    }

    private val mapListener by lazy {
        object : MapListener {
            override fun onScroll(event: ScrollEvent): Boolean {
                viewModel.updateCenter(binding.map.mapCenter)
                return true
            }

            override fun onZoom(event: ZoomEvent): Boolean {
                viewModel.updateZoom(binding.map.zoomLevelDouble)
                return true
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

    private fun getSatelliteKeyFromArguments(): String? {
        return arguments?.getString("SATELLITE")
    }

    private fun onUpdateSatellite(moment: Moment) {
        bind(moment, viewModel.satellite)
    }

    private fun bind(moment: Moment, satellite: Satellite) {
        displaySatellite(satellite, moment)
        displayTitle(satellite, moment)
        displayZoom()
        updateActionBar(satellite.friendlyName)
    }

    private fun onUpdateZoom(zoom: Double) {
        binding.map.controller.setZoom(zoom)
    }

    private fun displaySatellite(satellite: Satellite, moment: Moment) {
        satellite.updateFor(moment)
        try {
            updateCityMarker(moment.city)
            updateSatelliteMarker(satellite)
            updateVisibilityMarker(satellite)
            updatePreviewMarkers(satellite, moment)
            binding.map.invalidate()
        } catch (ex: Exception) {
            //ignore
        }
    }

    private fun displayZoom() {
        binding.zoomInfo.text = viewModel.zoom.toString()
    }

    private fun onUpdateCurrentAutomaticLocation(location: Location) =
        updateCurrentLocationMarker(location.toGeoPoint())

    private fun updateCityMarker(city: City) {
        var marker = findMarkerById(cityMarkerId)
        if (marker == null) {
            marker = Marker(binding.map).also {
                it.id = cityMarkerId
                it.title = city.name
                it.position = city.location.toGeoPoint()
                it.subDescription = city.location.toString()
                it.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                it.infoWindow = BasicInfoWindow(R.layout.marker_info_window, binding.map)
            }
            binding.map.overlays.add(marker)
            binding.map.invalidate()
        }
        else {
            marker.also {
                it.position.latitude = city.latitude
                it.position.longitude = city.longitude
                it.subDescription = city.location.toString()
            }
        }
    }

    private fun updateCurrentLocationMarker(you: GeoPoint) {
        var marker = findMarkerById(currentLocationMarkerId)
        if (marker == null) {
            marker = Marker(binding.map).also {
                it.setIconAnchorCenter(getIcon(R.drawable.target_blue))
                it.id = currentLocationMarkerId
                it.title = "You"
                it.position = you
                it.infoWindow = BasicInfoWindow(R.layout.marker_info_window, binding.map)
            }
            binding.map.overlays.add(marker)
            binding.map.invalidate()
        } else {
            marker.also {
                it.position.latitude = you.latitude
                it.position.longitude = you.longitude
            }
        }
    }

    private fun updateSatelliteMarker(satellite: Satellite) {
        val iss = GeoPoint(satellite.location.latitude, satellite.location.longitude)
        var marker = findMarkerById(satelliteMarkerId)
        if (marker == null) {
            marker = Marker(binding.map).also {
                it.setIconAnchorCenter(getIcon(satellite.redIconId))
                it.id = satelliteMarkerId
                it.title = satellite.name
                it.position = iss
                it.infoWindow = BasicInfoWindow(R.layout.marker_info_window, binding.map)
            }
            binding.map.overlays.add(marker)
            binding.map.controller.setCenter(iss) // Center when we draw the iss the first time.
            binding.map.invalidate()
        } else {
            marker.also {
                it.position.latitude = satellite.location.latitude
                it.position.longitude = satellite.location.longitude
                // it.subDescription = toDescription(position)
                // it.setThisIcon(if (viewModel.useTracking) targetHotIcon else targetIcon)
            }
        }
    }

    private fun updateVisibilityMarker(satellite: Satellite) {
        val points: List<GeoPoint> = SatellitePreview.getVisibilityPoints(satellite)
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
            binding.map.overlays.add(marker)
            binding.map.invalidate()
        } else {
            marker.also {
                it.points = points
            }
        }
    }

    private fun updatePreviewMarkers(satellite: Satellite, observer: Moment) {
        if (previewMarkers == null) {
            previewMarkers = ArrayList<Marker>().also {
                // calculate and assign once only...
                updatePreviewMarkers(satellite, observer, binding.map, it)
            }
        }
    }

    private fun updatePreviewMarkers(satellite: Satellite, observer: Moment, map: MapView, previewMarkers: ArrayList<Marker>) {
        SatellitePreview.getPreviewPoints(satellite, observer).also {
            for (point in it) {
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
    }

    private fun clearMarkers() {
        previewMarkers?.also {
            for (marker: Marker in it) {
                marker.remove(binding.map)
            }
            it.clear()
        }
        previewMarkers = null
    }

    private fun setCenter(location: com.stho.nyota.sky.utilities.Location) {
        binding.map.controller.setCenter(location.toGeoPoint())
    }

    private fun findMarkerById(id: String): Marker? {
        binding.map.overlays?.forEach {
            if (it is Marker && it.id == id) {
                return it
            }
        }
        return null
    }

    private fun getIcon(resourceId: Int): BitmapDrawable =
        icons[resourceId] ?: createIconIntoCache(resourceId)

    private fun createIconIntoCache(resourceId: Int): BitmapDrawable =
        createIcon(resourceId).also { icons[resourceId] = it }

    private fun createIcon(resourceId: Int): BitmapDrawable =
        ContextCompat.getDrawable(requireActivity(), resourceId) as BitmapDrawable



    private fun findPolygonById(id: String): Polygon? {
        binding.map.overlays?.forEach {
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

// Hack: osmdroid has a bug calculating the anchor: see explanation in MeHere
private fun Marker.setIconAnchorCenter(image: BitmapDrawable) {
    val fx: Float = Marker.ANCHOR_CENTER * image.bitmap.width / image.intrinsicWidth
    val fy: Float = Marker.ANCHOR_CENTER * image.bitmap.height / image.intrinsicHeight
    this.icon = image
    //this.image = image
    this.setAnchor(fx, fy)
}

private fun GeoPoint.getPositionAsString(): String =
    Angle.toString(longitude, Angle.AngleType.LONGITUDE) + Formatter.SPACE + Angle.toString(latitude, Angle.AngleType.LATITUDE)


