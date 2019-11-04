package com.stho.software.nyota;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.universe.Satellite;
import com.stho.software.nyota.universe.SatellitePreview;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.City;
import com.stho.software.nyota.utilities.Formatter;
import com.stho.software.nyota.utilities.Location;
import com.stho.software.nyota.utilities.Moment;

import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileCache;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.util.ArrayList;
import java.util.List;

public final class ISSMapViewActivity extends Activity implements LocationListener {

    private Universe universe = Universe.getInstance();
    private Handler handler = new Handler();
    private LocationManager locationManager;
    private City city = null;
    private MapView map;
    private Marker observerMarker;
    private Marker satelliteMarker;
    private List<Marker> previewMarkers;
    private org.osmdroid.views.overlay.Polygon visibilityMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        createLocationManager();
        setupCity();
        createMapView();

        Universe.getInstance().updateForNow();

        // Prevent it from going to sleep
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private static final OnlineTileSourceBase GoogleSat = new XYTileSource(
                "Google-Sat",
                0,
                19,
                256,
                ".png",
                new String[]{
                        "http://mt0.google.com",
                        "http://mt1.google.com",
                        "http://mt2.google.com",
                        "http://mt3.google.com",
                }) {
        @Override
        public String getTileURLString(MapTile aTile) {
            return getBaseUrl() + "/vt/lyrs=s&x=" + aTile.getX() + "&y=" + aTile.getY() + "&z=" + aTile.getZoomLevel();
        }
    };

    private void createMapView() {

        final ITileSource tileSource = GoogleSat;
        TileSourceFactory.addTileSource(tileSource);
        map = (MapView)findViewById(R.id.map);
        map.setTileSource(tileSource);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        MapTileCache cache = map.getTileProvider().createTileCache();
        IConfigurationProvider configuration = org.osmdroid.config.Configuration.getInstance();

        // Compass
        CompassOverlay compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        // Zoom and center
        IMapController controller = map.getController();
        controller.setZoom(3);
    }


    @Override
    public void onLocationChanged(android.location.Location location) {
        if (city != null && city.isAutomatic()) {
            city.setLocation(location);
            updateUI();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupCity();
        enableTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableTimer();
        disableLocationListener();
        clearMarkers();
    }

    private void enableTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Universe.getInstance().updateForNow();
                updateUI();
                handler.postDelayed(this, 1000);
            }
        }, 100);
    }

    private void updateUI() {
        if (city != null) {
            Moment newMoment = Moment.forNow(city);
            universe.updateFor(newMoment, false);
            Satellite satellite = universe.findSatelliteByName("ISS");
            displaySatellite(satellite, newMoment);
            displayTitle(newMoment, satellite);
        }
    }

    private void displaySatellite(Satellite satellite, Moment moment) {
        satellite.update(moment);
        try {
            if (map != null) {
                updateCityMarker(city);
                updateSatelliteMarker(satellite);
                updateVisibilityMarker(satellite);
                updatePreviewMarkers(satellite, moment.getLocation());
                map.invalidate();
            }
        } catch (Exception ex) {
            //ignore
        }
    }

    private void disableTimer() {
        handler.removeCallbacksAndMessages(null);
    }

    private void createLocationManager() {
        locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
    }

    private void setupCity() {
        city = Framework.getCity(getIntent());
        if (city != null && city.isAutomatic()) {
            enableLocationListener();
        } else {
            disableLocationListener();
        }
    }

    private void enableLocationListener() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        } catch (SecurityException ex) {
            //ignore
        }
    }

    private void disableLocationListener() {
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException ex) {
            //ignore
        }
    }

    private void updateCityMarker(City city) {
        GeoPoint you = new GeoPoint(city.getLatitude(), city.getAltitude());
        if (observerMarker == null) {
            observerMarker = new Marker(map);
            observerMarker.setPosition(you);
            observerMarker.setTitle(city.getName());
            observerMarker.setInfoWindow(new BasicInfoWindow(R.layout.marker_info_window, map));
            map.getOverlays().add(observerMarker);
        } else {
            observerMarker.setPosition(you);
        }
    }

    private void updateSatelliteMarker(Satellite satellite) {
        GeoPoint iss = new GeoPoint(satellite.getLocation().getLatitude(), satellite.getLocation().getLongitude());
        Drawable image = getResources().getDrawable(R.drawable.iss_red);
        if (satelliteMarker == null) {
            satelliteMarker = new Marker(map);
            satelliteMarker.setPosition(iss);
            satelliteMarker.setTitle(satellite.getName());
            satelliteMarker.setIcon(image);
            satelliteMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            satelliteMarker.setInfoWindow(new BasicInfoWindow(R.layout.marker_info_window, map));
            map.getOverlays().add(satelliteMarker);
            // Center when we draw the iss the first time.
            IMapController controller = map.getController();
            controller.setCenter(iss);
        } else {
            satelliteMarker.setPosition(iss);
        }
    }

    private void updateVisibilityMarker(Satellite satellite) {
        List<GeoPoint> points = SatellitePreview.getVisibilityPoints(satellite);
        if (visibilityMarker == null) {
            visibilityMarker = new Polygon();
            visibilityMarker.setFillColor(Color.TRANSPARENT);
            visibilityMarker.setStrokeColor(Color.GREEN);
            visibilityMarker.setVisible(true);
            visibilityMarker.setPoints(points);
            visibilityMarker.setStrokeWidth(1);
            map.getOverlays().add(visibilityMarker);
        } else {
            visibilityMarker.setPoints(points);
        }
    }

    private void updatePreviewMarkers(Satellite satellite, Location observer) {

        if (previewMarkers == null) {
            previewMarkers = new ArrayList<Marker>();

            List<SatellitePreview.PreviewPoint> points = SatellitePreview.getPreviewPoints(satellite, observer);

            for (SatellitePreview.PreviewPoint point : points) {

                Drawable image = this.getResources().getDrawable(point.getResourceId());

                Marker marker = new Marker(map);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                marker.setIcon(image);
                marker.setPosition(point);
                marker.setTitle(point.getTitle());
                marker.setInfoWindow(new BasicInfoWindow(R.layout.marker_info_window, map));

                previewMarkers.add(marker);
                map.getOverlays().add(marker);
            }
        }
    }

    private void clearMarkers() {

        if (previewMarkers != null) {
            for (Marker marker : previewMarkers) {
                marker.remove(map);
            }
            previewMarkers.clear();
            previewMarkers = null;
        }
    }

    protected void displayTitle(Moment moment, IElement element) {
        setTitle(element.getName()
                + Formatter.SPACE
                + Formatter.toString(moment.getUTC(), moment.getTimeZone(), Formatter.TimeFormat.DATETIME_TIMEZONE));
    }
}




