package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Hour


internal abstract class AbstractUniverseInitializer(protected var universe: Universe) {

    fun newStar(name: String, symbol: String, ascension: String, declination: String, brightness: Double): Star {
        return newStar(name, symbol, Hour.fromHour(ascension), Degree.fromDegree(declination), brightness)
    }

    fun getStar(name: String): Star? {
        return universe.stars.findStarByName(name)
    }

    private fun newStar(name: String, symbol: String, ascension: Hour, declination: Degree, brightness: Double): Star {
        var star = getStar(name);
        if (star == null) {
            star = Star(name, symbol, ascension.toDegree(), declination.toDegree(), brightness)
            universe.stars.add(star)
        }
        return star
    }

    private fun getConstellation(name: String): Constellation? {
        return universe.constellations.findConstellationByName(name)
    }

    fun newConstellation(name: String, imageId: Int): Constellation {
        var constellation = getConstellation(name)
        if (constellation == null) {
            constellation = Constellation(name, imageId)
            universe.constellations.add(constellation)
        }
        return constellation
    }

    private fun getSatellite(name: String): Satellite? {
        return universe.satellites.findSatelliteByName(name)
    }

    fun newSatellite(name: String, displayName: String, noradSatelliteNumber: Int, elements: String): Satellite {
        val satellite = Satellite(name, displayName, noradSatelliteNumber, elements)
        universe.satellites.add(satellite)
        return satellite
    }

    fun newSpecialElement(name: String, ascension: Hour, declination: Degree): SpecialElement {
        val special = SpecialElement(name, ascension.toDegree(), declination.toDegree())
        universe.specials.add(special)
        return special
    }

    fun asVIP(star: Star) {
        if (!universe.vip.contains(star)) {
            universe.vip.add(star)
        }
    }

}