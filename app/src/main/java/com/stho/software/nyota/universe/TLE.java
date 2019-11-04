package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Formatter;
import com.stho.software.nyota.utilities.JulianDay;
import com.stho.software.nyota.utilities.Radian;
import com.stho.software.nyota.utilities.UTC;

import java.util.Date;

/**
 * Two line elements
 */
public class TLE {
    private String elements;

    /** Epoch of the TLE as Julian Day */
    public double Epoch;

    public double bstar;

    /** Inclination (in radian) */
    public double xincl;

    /** Right Ascension of the Ascending Node (in radian) */
    public double xnodeo;

    /** Eccentricity (decimal) */
    public double eo;

    /** Argument of Perigee (in radian) */
    public double omegao;

    /** Mean Anomaly (in radian) */
    public double xmo;

    /** Mean Motion (in radian per minute) */
    public double xno;

    public int SatelliteNumber;

    public int RevolutionNumber;

    public String InternationalDesignator;

    /** Mean Distance in km */
    public double MeanDistanceFromEarth;

    /** Mean Motion (in revolutions per day) */
    public double RevolutionsPerDay;


    /** If the TLE is still valid or if aa new download is required */
    public boolean isOutdated()
    {
        UTC now = UTC.forNow();
        return ((now.getJulianDay() - this.Epoch) > 3);
    }

    public String serialize()
    {
        return this.elements;
    }

    public boolean deserialize(String elements) {
        if (!TLEParser.tryParse(this, elements))
            return false;

        this.elements = elements;
        return true;
    }

    public Date getDate() {
        return JulianDay.toUTC(Epoch).getTime();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("NORAD = %d\n", this.SatelliteNumber));
        sb.append(String.format("Epoch (UTC) = %s\n", Formatter.dateTime.format(JulianDay.toUTC(this.Epoch))));
        sb.append(String.format("Eccentricity = %$.3f", this.eo));
        sb.append(String.format("Inclination = %$.1f°", Radian.toDegrees(this.xincl)));
        sb.append(String.format("Mean Height = %$.f km", this.MeanDistanceFromEarth));

        double difference = this.eo * ( (this.MeanDistanceFromEarth + Algorithms.EARTH_RADIUS));

        sb.append(String.format("Perigee Height = %1$.f km", this.MeanDistanceFromEarth - difference));
        sb.append(String.format("Apogee Height = %$.f km", this.MeanDistanceFromEarth + difference));
        sb.append(String.format("Right Ascension of Ascend Node = %$.1f°", Radian.toDegrees(this.xnodeo)));
        sb.append(String.format("Argument of Perigee = %$.1f°", Radian.toDegrees(this.omegao)));
        sb.append(String.format("Revs Per Day = %$.1f", this.RevolutionsPerDay));
        sb.append(String.format("Mean anomaly = %$.1f°", Radian.toDegrees(this.xmo)));

        return sb.toString();
    }
}
