package com.stho.software.nyota.utilities;

/**
 * Created by shoedtke on 11.10.2016.
 */
class TimeCounter {

    private static final double NANOSECONDS = 1000000000;
    private double averageDifference = 0;
    private long startTime;

    TimeCounter() {
        startTime = System.nanoTime();
    }

    public synchronized float getAverageTimeDifferenceInSeconds() {
        long newTime = System.nanoTime();
        double difference = (newTime - startTime) / NANOSECONDS;
        averageDifference = (17 * averageDifference + difference) / 18;
        startTime = newTime;
        return (float) averageDifference;
    }

    public synchronized float getTimeDifferenceInSeconds() {
        long newTime = System.nanoTime();
        double difference = (newTime - startTime) / NANOSECONDS;
        startTime = newTime;
        return (float) difference;
    }
}
