package com.stho.software.nyota;

import com.stho.software.nyota.universe.Satellite;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.City;
import com.stho.software.nyota.utilities.Moment;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by shoedtke on 31.01.2017.
 */

public class PerformanceUnitTest {

    final static int MAX = 1000;

    @Before
    public void Initialize() {
        City city = City.createNewDefaultBerlin();
        Moment moment = Moment.forNow(city);
        Universe.getInstance().updateFor(moment, false);
    }

    @Test
    public void UpdateUniverse_Performance() throws Exception {
        long startTime = System.currentTimeMillis();

        for (int i=0; i < MAX; i++) {
            Universe.getInstance().updateForNow();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        assertTrue("Update universe", elapsed < 1000);
    }

    @Test
    public void UpdateUniverseSatellite_Performance() throws Exception {
        long startTime = System.currentTimeMillis();

        for (int i=0; i < MAX; i++) {
            Satellite satellite = Universe.getInstance().findSatelliteByName("ISS");
            satellite.update(Universe.getInstance().getMomentForNow());
        }

        long elapsed = System.currentTimeMillis() - startTime;
        assertTrue("Update universe", elapsed < 100);
    }
}
