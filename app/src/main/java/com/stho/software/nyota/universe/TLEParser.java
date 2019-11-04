package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Radian;

/**
 * Created by shoedtke on 20.01.2017.
 */
public class TLEParser {
    private final static double MINUTES_PER_DAY = 1440.0;
    private final static double EARTH_RADIUS = 6378.135;
    private final static double xke = 0.0743669161331734132;


    protected static boolean tryParse(TLE tle, String elements) {
        if (elements == null)
            return false;

        String[] lines = elements.split("[\\r\\n]+");
        if (lines.length < 2)
            return false;

        return tryParse(tle, lines[0], lines[1]);
    }

    private static boolean tryParse(TLE tle, String line1, String line2) {

        if (test(line1, '1') == false)
            return false;

        if (test(line2, '2') == false)
            return false;

        /*
            ISS
            1 25544U 98067A   02256.70033192  .00045618  00000-0  57184-3 0  1499
            2 25544  51.6396 328.6851 0018421 253.2171 244.7656 15.59086742 217834
        */

        tle.SatelliteNumber = parseInt(line1, 3, 7); // Satellite Number, 3-7, "25544"

        tle.InternationalDesignator = parseString(line1, 10, 17); // International Designator , 10-17, "98067A"

        int year = parseInt(line1, 19, 20); // Epoch Year (Last two digits of year), 19-20, "08",

        double day = parseDouble(line1, 21, 32); // Epoch (Day of the year and fractional portion of the day), 21-32, "264.51782528"

        tle.Epoch = getEpoch(year) + day;

        tle.bstar = parseDoubleExp(line1, 54, 61); // BSTAR drag term (decimal point assumed), 54-61, "-11606-4"

        tle.xmo = parseAngle(line2, 44, 51); // Mean Anomaly [Degrees], 44-51, "325.0288"

        tle.xnodeo = parseAngle(line2, 18, 25); // Right Ascension of the Ascending Node [Degrees], 18-25, "247.4627"

        tle.omegao = parseAngle(line2, 35, 42); // Argument of Perigee [Degrees], 35-42, "130.5360"

        tle.xincl = parseAngle(line2, 9, 16); // Inclination [Degrees], 9-16, "51.6516"

        tle.eo = parseDouble(line2, 27, 33, "0."); // Eccentricity (decimal point assumed), 27-33, "0006703" --> 0.0006703

        tle.RevolutionsPerDay = parseDouble(line2, 53, 63); //  	Mean Motion [Revs per day], 53-63, "15.72125391"

        tle.RevolutionNumber = parseInt(line2, 64, 68); // Revolution Number of Epoch (Revs), 64-68, "56353"

        // Secondary Calculated Figures

        tle.xno = tle.RevolutionsPerDay * 2 * Math.PI / MINUTES_PER_DAY;

        tle.MeanDistanceFromEarth = EARTH_RADIUS * (Math.pow(xke / tle.xno, 2.0 / 3.0) - 1);

        return true;
    }

    private static boolean test(String line, char identifier) {
        if (line == null)
            return false;

        if (verifyChecksum(line) == false)
            return false;

        if (line.charAt(0) != identifier)
            return false;

        return true;
    }

    /// <summary>
    /// Get the Epoch of the specified year: 17 --> Epoch(2017)
    /// </summary>
    private static double getEpoch(int year) {
        final double J1900 = 2415019.5;

        if (year < 57)
            year += 100; // Loop around Y2K

        return J1900 + year * 365.0 + ((year - 1) / 4);
    }

    private static double parseAngle(String line, int a, int b) {
        double degree = parseDouble(line, a, b);
        return Radian.fromDegrees(degree);
    }

    private static double parseDouble(String line, int a, int b, String prefix) {
        String substring = prefix + parseString(line, a, b);
        double d = Double.parseDouble(substring.trim());
        return d;
    }

    private static double parseDouble(String line, int a, int b) {
        String substring = parseString(line, a, b);
        double d = Double.parseDouble(substring.trim());
        return d;
    }

    private static int parseInt(String line, int a, int b) {
        String substring = parseString(line, a, b);
        int i = Integer.parseInt(substring.trim());
        return i;
    }

    private static double parseDoubleExp(String line, int a, int b) {
        String substring = parseString(line, a, b);

        String prefix = (substring.charAt(0) == '-') ? "-0." : "0.";
        String suffix = (substring.charAt(6) == '-') ? "E-" : "E";

        substring = prefix + substring.substring(1, 5) + suffix + substring.substring(7);

        double d = Double.parseDouble(substring);
        return d;
    }

    /**
     * Substring using 1-based positions a and b ---> results into substring from start := a-1 until end(after) := b
     */
    private static String parseString(String line, int a, int b) {
        return line.substring(a - 1, b);
    }

    /// <summary>
    /// Calculates the checksum of all but the last digits modulo 10 and compares it with the last. (Letters, blanks, periods, plus signs = 0; minus signs = 1)
    /// </summary>
    private static boolean verifyChecksum(String line) {
        int sum = 0;

        if (line.length() < 69)
            return false;

        for (int i = 0; i < 68; i++)
        {
            switch (line.charAt(i))
            {
                case '0': break;
                case '1': sum += 1; break;
                case '2': sum += 2; break;
                case '3': sum += 3; break;
                case '4': sum += 4; break;
                case '5': sum += 5; break;
                case '6': sum += 6; break;
                case '7': sum += 7; break;
                case '8': sum += 8; break;
                case '9': sum += 9; break;
                case '-': sum += 1; break;
            }
        }

        char checksum = (char)('0' + sum % 10);

        if (line.charAt(68) != checksum)
            return false;

        return true;
    }
}


