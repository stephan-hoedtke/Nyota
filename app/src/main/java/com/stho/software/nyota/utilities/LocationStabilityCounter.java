package com.stho.software.nyota.utilities;

public class LocationStabilityCounter {
    private static final int REQUIRED_COUNTER_VALUE = 10;
    private int counter = 0;

    public void reset() {
        counter = 0;
    }

    public boolean isStable() {
        if (counter > REQUIRED_COUNTER_VALUE) {
            return true;
        }
        else {
            counter++;
            return false;
        }
    }
}
