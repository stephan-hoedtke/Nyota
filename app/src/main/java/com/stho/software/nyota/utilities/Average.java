package com.stho.software.nyota.utilities;

/**
 * Calculate the qualified arithmetic average of the recent input angle values
 * Mind: the average of 355 and 5 is not 180, but 0 !
 */
public class Average implements IAverage {

    private static final double TOLERANCE = 0.001;
    private static final int MAX_LENGTH = 20;
    private int length = 0;
    private int pos = 0;
    private final double[] array = new double[MAX_LENGTH];
    private double average = 0;

    public synchronized void setAngle(double degree) {
        registerNewValue(degree);
        takeNewAverage();
    }

    private void takeNewAverage() {
        double newAverage = getAverage();
        if (Math.abs(Angle.getAngleDifference(newAverage, average)) > TOLERANCE) {
            average = newAverage;
        }
    }

    public synchronized double getCurrentAngle() {
        return average;
    }

    /**
     * Calculating the circular mean of a list of angles
     * See: Mean of circular quantities, Wikipedia, https://en.wikipedia.org/wiki/Mean_of_circular_quantities
     *
     * @param array of angles [0° to 360°]
     * @param length number of valid values in array
     * @return circular mean [0° to 360°]
     */
    public static double getCircularAverage(double[] array, int length) {
        double sin = 0;
        double cos = 0;
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                sin += Degree.sin(array[i]);
                cos += Degree.cos(array[i]);
            }
            sin = sin / length;
            cos = cos / length;
        }
        return Degree.arcTan2(sin, cos);
    }

    private double getAverage() {
        return Average.getCircularAverage(array, length);
    }

    private synchronized void registerNewValue(double degree) {
        if (length > 0) {
            pos++;
            if (pos >= MAX_LENGTH)
                pos = 0;
        }
        if (length < MAX_LENGTH)
            length++;

        array[pos] = degree;
    }
}
