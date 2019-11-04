package com.stho.software.nyota.utilities;

/**
 * Vector with low pass filter
 */
public class LPFVector {
    private static final float TIME_CONSTANT_IN_SECONDS = 0.18f;
    private final TimeCounter timeCounter = new TimeCounter();
    private final float[] values = {0.0f, 0.0f, 0.0f};
    private boolean hasValues = false;

    public boolean hasValues() {return hasValues; }

    public float[] getValues() { return values; }

    public void update(float[] newValues) {
        if (hasValues) {
            this.lowPassFilter(newValues);
        } else {
            set(newValues);
        }
    }

    private void lowPassFilter(float[] newValues) {
        double dt = timeCounter.getAverageTimeDifferenceInSeconds();
        if (dt > 0) {
            double alpha = dt / (TIME_CONSTANT_IN_SECONDS + dt);
            values[0] += alpha * (newValues[0] - values[0]);
            values[1] += alpha * (newValues[1] - values[1]);
            values[2] += alpha * (newValues[2] - values[2]);
        } else {
            set(newValues);
        }
    }

    private void set(float[] newValues) {
        values[0] = newValues[0];
        values[1] = newValues[1];
        values[2] = newValues[2];
        hasValues = true;
    }
}
