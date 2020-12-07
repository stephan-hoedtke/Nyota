package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Hour


internal abstract class AbstractUniverseInitializer(protected var universe: Universe) {

    fun newStar(name: String, symbol: String, ascension: String, declination: String, brightness: Double): Star =
        newStar(name, symbol, Hour.fromHour(ascension), Degree.fromDegree(declination), brightness)

    fun getStar(name: String): Star? =
        universe.stars.findStarByName(name)

    private fun newStar(name: String, symbol: String, ascension: Hour, declination: Degree, brightness: Double): Star {
        var star = getStar(name);
        if (star == null) {
            star = Star(name, symbol, ascension.toDegree(), declination.toDegree(), brightness).also {
                universe.stars.add(it)
            }
        }
        return star
    }

    fun newConstellation(name: String, imageId: Int): Constellation {
        var constellation = universe.constellations.findConstellationByName(name)
        if (constellation == null) {
            constellation = Constellation(name, imageId).also {
                universe.constellations.add(it)
            }
        }
        return constellation
    }

    private fun getSatellite(name: String): Satellite? {
        return universe.satellites.findSatelliteByName(name)
    }

    fun newSatellite(name: String, displayName: String, noradSatelliteNumber: Int, elements: String): Satellite {
        var satellite = universe.satellites.findSatelliteByName(name)
        if (satellite == null) {
            satellite = Satellite(name, displayName, noradSatelliteNumber, elements).also {
                universe.satellites.add(it)
            }
        }
        return satellite
    }

    fun newSpecialElement(name: String, ascension: Hour, declination: Degree): SpecialElement =
        SpecialElement(name, ascension.toDegree(), declination.toDegree()).also {
            universe.specials.add(it)
        }

    fun asVIP(star: Star) {
        if (!universe.vip.contains(star)) {
            universe.vip.add(star)
        }
    }

}