package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Hour
import java.lang.Exception


abstract class AbstractUniverseInitializer(protected var universe: Universe) {

    fun getStar(hd: Int): Star =
        universe.stars[hd] ?: throw Exception("Star $hd was not initialized yet")

    fun getStar(starName: String): Star =
        universe.stars[starName] ?: throw Exception("Star $starName was not initialized yet")

    fun getStar(rank: Int, symbol: Symbol): Star =
        universe.constellations[rank][symbol] // throws an exception if rank or symbol were not initialized

    fun getConstellation(rank: Int): Constellation =
        universe.constellations[rank] // throws an exception if rank was not initialized

    fun newGalaxy(name: String, imageId: Int, ascension: String, declination: String, brightness: Double, distance: Double): Galaxy =
        Galaxy(name, imageId, Hour.fromHour(ascension).angleInDegree, Degree.fromDegree(declination).angleInDegree, brightness, distance).also {
            universe.galaxies.add(it)
        }

    fun newAnything(name: String, ascension: String, declination: String, brightness: Double, distance: Double): Anything =
        Anything(name, Hour.fromHour(ascension).angleInDegree, Degree.fromDegree(declination).angleInDegree, brightness, distance).also {
            universe.any.add(it)
        }

    fun newHint(description: String, at: IElement): Hint =
        Hint.create(description, at).also {
            universe.hints.add(it)
        }

    fun newHint(description: String, from: Star, to: Star): Hint =
        Hint.create(description, from, to).also {
            universe.hints.add(it)
        }

    fun newTriangle(description: String, one: Star, two: Star, three: Star): Hint =
        Hint.create(description, one, two, three).also {
            universe.hints.add(it)
        }

    fun newRectangle(description: String, one: Star, two: Star, three: Star, four: Star): Hint =
        Hint.create(description, one, two, three, four).also {
            universe.hints.add(it)
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

    fun setFriendlyNameTo(hd: Int, friendlyName: String): Star =
        getStar(hd).also {
            universe.stars.setFriendlyName(it, friendlyName)
        }

    fun asVIP(star: Star) {
        if (!universe.vip.contains(star)) {
            universe.vip.add(star)
        }
    }
}

