package com.stho.software.nyota;

import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Hour;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class RegexUnitTest {


    // see here: https://en.wikipedia.org/wiki/List_of_stars_in_Virgo

    @Test
    public void hourPattern_isCorrect() throws Exception {
        areEqual("13h 25m 11.60", Hour.fromHour(13,25, 11.60));
        areEqual("13h 54m 42.20", Hour.fromHour(13, 54, 42.20));
        areEqual("11h 38m 24.09", Hour.fromHour(11, 38, 24.09));
        areEqual("23h 49m 51.95", Hour.fromHour(23, 49, 51.95));
        areEqual("2h 31m 4", Hour.fromHour(2, 31, 4));
    }

    private void areEqual(String hourString, Hour hour) {
        assertEquals(hourString, Hour.fromHour(hourString).toDegree(), hour.toDegree(), 0.000001);
    }

    @Test
    public void degreePattern_isCorrect() throws Exception {
        areEqual("+10° 57′ 32.8", Degree.fromPositive(10, 57, 32.8));
        areEqual("−01° 26′ 58.0", Degree.fromNegative(1, 26, 58.0));
        areEqual("+12° 40′ 57", Degree.fromPositive(12, 40, 57));
        areEqual("−03° 26′ 40", Degree.fromNegative(3, 26, 40));
        areEqual("+8° 15' 51", Degree.fromPositive(8, 15, 51));
    }

    private void areEqual(String degreeString, Degree degree) {
        assertEquals(degreeString, Degree.fromDegree(degreeString).toDegree(), degree.toDegree(), 0.000001);
    }

}