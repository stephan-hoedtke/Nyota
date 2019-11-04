package com.stho.software.nyota.utilities;

public class Clock {
    private long startMillis;
    private final long intervalInMillis;

    public Clock(long intervalInMillis) {
        this.intervalInMillis = intervalInMillis;
        this.startMillis = 0;
    }

    public boolean timeOver() {
        long millis = System.currentTimeMillis();
        if (millis - startMillis > intervalInMillis) {
            startMillis = millis;
            return true;
        }
        else {
            return false;
        }
    }
}
