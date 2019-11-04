package com.stho.software.nyota;

import com.stho.software.nyota.universe.Sun;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.City;
import com.stho.software.nyota.utilities.Location;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.utilities.UTC;

import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class Summer2017Test extends AbstractAstronomicUnitTest {

    @Test
    public void Summer2017() throws Exception {

        // Calculate the moment of the highest Sun position
        // Calculate the day of the longest Sun above the horizon

        // Finding:
        // Tue 20th was reported as longest day (from sun-rise to sun-down) although 21st would have been correct.
        // More: Starting with 13:08 at Tue 20th the sun-rise was 4:41 and sun-set 21:34, while starting with 8:00 it was 4:42 and 21.33 respectively

        // 20.06.2017 20:07 --> 4:40 to 21.32
        // 20.06.2017 11:07 --> 4:42 to 21.33
        // 20.06.2017 09:07 --> 4:42 to 21.34

        // Bernau bei Berlin
        // 52°41'N / 13°35'E


        Location buch = getObserver(52.64, 13.49);

        UTC expectedRiseTime = getCESTasUTC(2017, Calendar.JUNE, 20, 4, 42);
        UTC expectedSetTime = getCESTasUTC(2017, Calendar.JUNE, 20, 21, 33);

        //Verify(new Moment(getCESTasUTC(2017, Calendar.JUNE, 20, 0, 1), buch), expectedRiseTime.addHours(-24), expectedSetTime.setHours(-24)); // UTC is 19.06.2016
        Verify(Moment.forUTC(new City("Buch", buch, TimeZone.getDefault()), getCESTasUTC(2017, Calendar.JUNE, 20, 9, 7)), expectedRiseTime, expectedSetTime);
        Verify(Moment.forUTC(new City("Buch", buch, TimeZone.getDefault()), getCESTasUTC(2017, Calendar.JUNE, 20, 13, 7)), expectedRiseTime, expectedSetTime);
        Verify(Moment.forUTC(new City("Buch", buch, TimeZone.getDefault()), getCESTasUTC(2017, Calendar.JUNE, 20, 20, 7)), expectedRiseTime, expectedSetTime);
        Verify(Moment.forUTC(new City("Buch", buch, TimeZone.getDefault()), getCESTasUTC(2017, Calendar.JUNE, 20, 23, 59)), expectedRiseTime, expectedSetTime);

    }

    private void Verify(Moment moment, UTC expectedRiseTime, UTC expectedSetTime) throws Exception {

        Universe universe = Universe.getInstance();
        universe.updateFor(moment, true);

        Sun sun = universe.solarSystem.sun;

        // For confirmation see:
        assertCalendar("Sunrise", expectedRiseTime, sun.position.riseTime);
        assertCalendar("Sunset", expectedSetTime, sun.position.setTime);
    }
}




