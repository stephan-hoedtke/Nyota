package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Hour
import java.lang.Exception


abstract class AbstractUniverseInitializer(protected var universe: Universe) {

    fun getStar(name: String): Star =
        universe.stars.findStarByName(name) ?: throw Exception("Star $name was not initialized yet")

    fun newStar(name: String, symbol: Symbol, ascension: String, declination: String, brightness: Double): Star =
        newStar(name, symbol, Hour.fromHour(ascension), Degree.fromDegree(declination), brightness)

    fun newStar(symbol: Symbol, ascension: String, declination: String, brightness: Double): Star =
        newStar(symbol, Hour.fromHour(ascension), Degree.fromDegree(declination), brightness)

    private fun newStar(name: String, symbol: Symbol, ascension: Hour, declination: Degree, brightness: Double): Star =
        universe.stars.findStarByName(name) ?:
            Star.create(name, symbol, ascension.angleInDegree, declination.angleInDegree, brightness).also {
                universe.stars.add(it)
            }

    private fun newStar(symbol: Symbol, ascension: Hour, declination: Degree, brightness: Double): Star =
        Star.create(symbol, ascension.angleInDegree, declination.angleInDegree, brightness).also {
            universe.stars.add(it)
        }

    fun newConstellation(name: String, imageId: Int): Constellation =
        universe.constellations.findConstellationByName(name) ?:
            Constellation(name, imageId).also {
                universe.constellations.add(it)
            }

    fun newSatellite(name: String, displayName: String, noradSatelliteNumber: Int, elements: String): Satellite =
        universe.satellites.findSatelliteByName(name) ?:
            Satellite.createNewSatellite(name, displayName, noradSatelliteNumber, elements).also {
                universe.satellites.add(it)
            }

    fun newSpecialElement(name: String, ascension: Hour, declination: Degree): SpecialElement =
        SpecialElement(name, ascension.angleInDegree, declination.angleInDegree).also {
            universe.specials.add(it)
        }

    fun asVIP(star: Star) {
        if (!universe.vip.contains(star)) {
            universe.vip.add(star)
        }
    }
}