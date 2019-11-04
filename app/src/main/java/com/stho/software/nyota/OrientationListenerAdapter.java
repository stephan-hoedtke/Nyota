package com.stho.software.nyota;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.stho.software.nyota.utilities.Orientation;

import static android.content.Context.SENSOR_SERVICE;

public class OrientationListenerAdapter {

    private SensorManager sensorManager;
    private Orientation orientation = new Orientation();

    void enableCompassListener(SensorEventListener listener) {
        final Activity activity = (Activity)listener;
        try {
            if (sensorManager == null) {
                sensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
            }
            sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception ex) {
            // ignore
        }
    }

    void disableCompassListener(SensorEventListener listener) {
        try {
            if (sensorManager != null) {
                sensorManager.unregisterListener(listener);
            }
        } catch (Exception ex) {
            // ignore
        }
    }

    void onSensorChanged(SensorEvent event) {
        orientation.updateSensorValues(event);
    }

    Orientation getOrientation() {
        return orientation;
    }
}
