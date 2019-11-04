package com.stho.software.nyota.utilities;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Orientation is the direction in which your eyes look into the phone. Orthogonal to the phones surface, going out from the back.
 * Azimuth: Angle between the orientation and the geographic north at the horizon plane.
 * Inclination: Angle between the orientation and the horizon plane.
 */
public class Orientation {

    private IAverage azimuth = new Average();
    private IAverage altitude = new Average();
    private IAverage direction = new Average();
    private IAverage roll = new Average();

    static class SensorValues {
        final LPFVector gravity = new LPFVector();
        final LPFVector geomagnetic = new LPFVector();
        final LPFVector rotation = new LPFVector();
        final LPFVector orientation = new LPFVector();
    }

    private final SensorValues sensorValues = new SensorValues();

    public synchronized float getAzimuth() {
        return (float)azimuth.getCurrentAngle();
    }

    public synchronized float getAltitude() {
        return (float)altitude.getCurrentAngle();
    }

    public synchronized float getDirection() {
        return (float)direction.getCurrentAngle();
    }

    public synchronized float getRoll() {
        return (float)roll.getCurrentAngle();
    }


    public synchronized void updateSensorValues(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorValues.gravity.update(event.values);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorValues.geomagnetic.update(event.values);
                break;

            case Sensor.TYPE_ROTATION_VECTOR:
                sensorValues.rotation.update(event.values);
                break;

            case Sensor.TYPE_ORIENTATION:
                sensorValues.orientation.update(event.values);
                break;
        }

        if (sensorValues.rotation.hasValues()) {
            updateByRotation(sensorValues.rotation);
        }
        else if (sensorValues.gravity.hasValues() && sensorValues.geomagnetic.hasValues()) {
            updateByGravityGeomagnetic(sensorValues.gravity, sensorValues.geomagnetic);
        }
        else if (sensorValues.orientation.hasValues()) {
            updateByOrientation(sensorValues.orientation);
        }
    }

    private void updateByRotation(LPFVector rotation) {
        float[] R = new float[9];
        float[] O = new float[3];
        SensorManager.getRotationMatrixFromVector(R, sensorValues.rotation.getValues());
        SensorManager.getOrientation(R, O);
        this.update(O);
        // setAzimuth((float)Math.toDegrees(O[0]), Angle.normalizeTo180((float)Math.toDegrees(O[2])));
  }

    private void updateByGravityGeomagnetic(LPFVector gravity, LPFVector geomagnetic) {
        float[] R = new float[9];
        float[] O = new float[3];
        SensorManager.getRotationMatrix(R, null, sensorValues.gravity.getValues(), sensorValues.geomagnetic.getValues());
        SensorManager.getOrientation(R, O);
        this.update(O);
        // setAzimuth((float)Math.toDegrees(O[0]), Angle.normalizeTo180((float)Math.toDegrees(O[2])));
    }

    private void updateByOrientation(LPFVector orientation) {
        this.update(orientation.getValues());
    }


    private void update(float[] O) {

        double a = Math.toDegrees(O[0]); // Azimuth
        double p = Math.toDegrees(O[1]); // Pitch [-90 -- +90] positive: to the ground (it's not [-180 -- +180])
        double r = Math.toDegrees(O[2]); // Roll [-180 -- +180] positive: to the left (it's not [-90 -- +90])

        if (-90 <= r && r <= +90) {  // the phones surface is looked at from above, orientation (orthogonal to the surface) is pointing into the earth
            azimuth.setAngle(a);
            altitude.setAngle(-p - 90);
            direction.setAngle(-p);
            roll.setAngle(r);
        }
        else { // the phones surface is looked at from below, orientation (orthogonal to the surface) is pointing into the sky
            azimuth.setAngle(Angle.normalize(a + 180));
            altitude.setAngle(-p);
            direction.setAngle(180 + p);
            if (r > 90)
                roll.setAngle(180 - r);
            else
                roll.setAngle(-r - 180);
        }
    }

    @Override
    public String toString() {
        return Angle.toString(getAzimuth(), Angle.AngleType.DEGREE_NORTH_EAST_SOUTH_WEST)
                + Formatter.SPACE
                + Angle.toString(getAltitude(), Angle.AngleType.ALTITUDE);

    }

    public String toString(boolean flat) {
        return Angle.toString(getAzimuth(), Angle.AngleType.DEGREE_NORTH_EAST_SOUTH_WEST)
                + Formatter.SPACE
                + Angle.toString((flat) ? getDirection() : getAltitude(), Angle.AngleType.ALTITUDE);
    }
}
