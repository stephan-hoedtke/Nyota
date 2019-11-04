package com.stho.software.nyota;

import android.app.Activity;
import android.app.AlertDialog;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;

import com.stho.software.nyota.utilities.LocationStabilityCounter;

import static android.content.Context.LOCATION_SERVICE;

public class LocationListenerAdapter {

    private LocationManager locationManager;
    private LocationStabilityCounter locationStabilityCounter = new LocationStabilityCounter();

    void enableLocationListener(final LocationListener listener, boolean showErrors) {
        final Activity activity = (Activity) listener;
        try {
            if (locationManager == null) {
                locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
            }
            if (locationManager != null) {

                Criteria criteria = new Criteria();
                criteria.setAltitudeRequired(true);
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setBearingRequired(true);

                String provider = locationManager.getBestProvider(criteria, true);
                if (provider != null) {
                    listener.onLocationChanged(locationManager.getLastKnownLocation(provider));
                    locationManager.requestLocationUpdates(provider, 1000, 0, listener);
                    locationStabilityCounter.reset();
                }
            }
        } catch (SecurityException ex) {
            if (showErrors) {
                showError(activity, "Location not available: " + ex.getMessage());
            }
        }
    }

    void disableLocationListener(LocationListener listener) {
        try {
            if (locationManager != null) {
                locationManager.removeUpdates(listener);
            }
        } catch (SecurityException ex) {
            // nothing
        }
    }

    boolean isStable() {
        return locationStabilityCounter.isStable();
    }

    void reset() {
        locationStabilityCounter.reset();
    }

    private void showError(final Activity activity, final String errorMessage) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("Error");
                alert.setMessage(errorMessage);
                alert.setPositiveButton("OK",null);
                alert.show();
            }
        });
    }
}
