package com.stho.nyota

import com.stho.nyota.sky.universe.Star
import com.stho.nyota.sky.universe.Universe
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Hour
import com.stho.nyota.sky.utilities.Moment
import org.junit.Assert
import org.junit.Test
import java.util.*


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class StarUnitTest : AbstractAstronomicUnitTest() {

    @Test
    fun polarisInBernau_isCorrect() {

        // http://eco.mtk.nao.ac.jp/cgi-bin/koyomi/cande/horizontal_rhip_en.cgi
        //      Polaris:
        //          R.A.: 2h 31m 47.07s / Dec.: 89° 15' 50.9"
        //          Lat.:52.6400° Lon.:13.4900° Hgt.: 55.0m LST:UT+1h ΔT=69s
        //              2018-12-04 0:00:00--> Alt: 53 14 10, Azi: 359 30 16, Hour angle: 21 58 50
        val moment: Moment = Moment.forUTC(
            getCity(52.64, 13.49, 0.055, "CET"),
            getCESTasUTC(2018, Calendar.DECEMBER, 4, 0, 0)
        )
        val universe: Universe = Universe().apply {
            updateFor(moment, true)
        }

        Assert.assertEquals("GMT", "3 Dec 2018 23:00:00 +0000", moment.utc.toString())
        Assert.assertEquals("CET", "4 Dec 2018 00:00:00 +0100", moment.toString())

        val polaris: Star = universe.stars.findStarByName("Polaris")!!

        assertPosition("Polaris RA", Hour.fromHour(2, 31, 47.07).angleInDegree, polaris.RA)
        assertPosition("Polaris Decl", Degree.fromPositive(89, 15, 50.9).angleInDegree, polaris.Decl)
        assertPosition("Polaris altitude", Degree.fromPositive(53, 14, 10.0).angleInDegree, polaris.position!!.altitude)
        assertPosition("Polaris azimuth", Degree.fromPositive(359, 30, 16.0).angleInDegree, polaris.position!!.azimuth)
    }

    @Test
    fun siriusInBernau_isCorrect() {

        // http://eco.mtk.nao.ac.jp/cgi-bin/koyomi/cande/horizontal_rhip_en.cgi
        //      Sirius:
        //          R.A.: 6h 45m 9.25s / Dec.: -16° 42' 47.3"
        //          Lat.:52.6400° Lon.:13.4900° Hgt.: 55.0m LST:UT+1h ΔT=69s
        //              2018-12-04 0:00:00--> Alt: 15 53 15, Azi: 149 51 54, Hour angle: 21 58 50
        val moment: Moment = Moment.forUTC(
            getCity(52.64, 13.49, 0.055, "CET"),
            getCESTasUTC(2018, Calendar.DECEMBER, 4, 0, 0)
        )
        val universe: Universe = Universe().apply {
            updateFor(moment, true)
        }

        Assert.assertEquals("GMT", "3 Dec 2018 23:00:00 +0000", moment.utc.toString())
        Assert.assertEquals("CET", "4 Dec 2018 00:00:00 +0100", moment.toString())


        val sirius: Star = universe.stars.findStarByName("Sirius")!!

        assertPosition("Sirius RA", Hour.fromHour(6, 45, 9.25).angleInDegree, sirius.RA)
        assertPosition("Sirius Decl", Degree.fromNegative(16, 42, 47.3).angleInDegree, sirius.Decl)
        //assertPosition("Sirius hour angle", Hour.fromHour(21, 58, 50).angleInHours, hourAngle);
        assertPosition("Sirius altitude", Degree.fromPositive(15, 53, 15.0).angleInDegree, sirius.position!!.altitude)
        assertPosition("Sirius azimuth", Degree.fromPositive(149, 51, 54.0).angleInDegree, sirius.position!!.azimuth)
    }
}