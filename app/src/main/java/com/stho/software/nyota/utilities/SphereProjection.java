package com.stho.software.nyota.utilities;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"UnnecessaryLocalVariable", "WeakerAccess"})
public class SphereProjection {

    private double centerAzimuth;
    private double cos;
    private double sin;
    private static final double TOLERANCE = Degree.cos(75);

    public void setCenter(double centerAzimuth, double centerAltitude) {
        this.centerAzimuth = centerAzimuth;
        this.cos = Degree.cos(centerAltitude);
        this.sin = Degree.sin(centerAltitude);
    }

    @Nullable
    public Point getImagePoint(double pointAzimuth, double pointAltitude) {
        double deltaAzimuth = pointAzimuth - centerAzimuth;
        double z = Degree.sin(pointAltitude);
        double L = Degree.cos(pointAltitude);
        double x = L * Degree.sin(deltaAzimuth);
        double y = L * Degree.cos(deltaAzimuth);
        double x1 = x;
        double y1 = z * cos - y * sin;
        double z1 = y * cos + z * sin;

        if (isExcluded(z1))
            return null;

        double factor = 1 / (y * cos + z * sin);
        double x2 = x1 * factor;
        double y2 = y1 * factor;

        return new Point(x2, y2);
    }

    private boolean isExcluded(double z1) {
        return (z1 < TOLERANCE);
    }
}

