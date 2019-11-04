package com.stho.software.nyota;

import com.stho.software.nyota.universe.Star;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Hour;
import com.stho.software.nyota.utilities.Moment;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class StarUnitTest extends AbstractAstronomicUnitTest {

    @Test
    public void PolarisInBernau_isCorrect() throws Exception {

        // http://eco.mtk.nao.ac.jp/cgi-bin/koyomi/cande/horizontal_rhip_en.cgi
        //      Polaris:
        //          R.A.: 2h 31m 47.07s / Dec.: 89° 15' 50.9"
        //          Lat.:52.6400° Lon.:13.4900° Hgt.: 55.0m LST:UT+1h ΔT=69s
        //              2018-12-04 0:00:00--> Alt: 53 14 10, Azi: 359 30 16, Hour angle: 21 58 50
        Moment moment = Moment.forUTC(
                getCity(52.64, 13.49, 0.055, "CET"),
                getCESTasUTC(2018, Calendar.DECEMBER, 4, 0, 0));

        Universe universe = Universe.getInstance();
        universe.updateFor(moment, true);
        assertEquals("Time", "2018-12-03 23:00:00 +0000", moment.toString());

        Star polaris = universe.findStarByName("Polaris");
        assertNotNull("Polaris", polaris);

        assertPosition("Polaris RA", Hour.fromHour(2, 31, 47.07).toDegree(), polaris.RA);
        assertPosition("Polaris Decl", Degree.fromPositive(89, 15, 50.9).angleInDegree, polaris.Decl);
        //assertPosition("Polaris hour angle", Hour.fromHour(21, 58, 50).angleInHours, hourAngle);
        assertPosition("Polaris altitude", Degree.fromPositive(53, 14, 10).angleInDegree, polaris.position.altitude);
        assertPosition("Polaris azimuth", Degree.fromPositive(359, 30, 16).angleInDegree, polaris.position.azimuth);
    }

    @Test
    public void SiriusInBernau_isCorrect() throws Exception {

        // http://eco.mtk.nao.ac.jp/cgi-bin/koyomi/cande/horizontal_rhip_en.cgi
        //      Sirius:
        //          R.A.: 6h 45m 9.25s / Dec.: -16° 42' 47.3"
        //          Lat.:52.6400° Lon.:13.4900° Hgt.: 55.0m LST:UT+1h ΔT=69s
        //              2018-12-04 0:00:00--> Alt: 15 53 15, Azi: 149 51 54, Hour angle: 21 58 50
        Moment moment = Moment.forUTC(
                getCity(52.64, 13.49, 0.055, "CET"),
                getCESTasUTC(2018, Calendar.DECEMBER, 4, 0, 0));

        Universe universe = Universe.getInstance();
        universe.updateFor(moment, true);
        assertEquals("Time", "2018-12-03 23:00:00 +0000", moment.toString());

        Star sirius = universe.findStarByName("Sirius");
        assertNotNull("Sirius", sirius);

        assertPosition("Sirius RA", Hour.fromHour(6, 45, 9.25).toDegree(), sirius.RA);
        assertPosition("Sirius Decl", Degree.fromNegative(16, 42, 47.3).angleInDegree, sirius.Decl);
        //assertPosition("Sirius hour angle", Hour.fromHour(21, 58, 50).angleInHours, hourAngle);
        assertPosition("Sirius altitude", Degree.fromPositive(15, 53, 15).angleInDegree, sirius.position.altitude);
        assertPosition("Sirius azimuth", Degree.fromPositive(149, 51, 54).angleInDegree, sirius.position.azimuth);
    }

}





