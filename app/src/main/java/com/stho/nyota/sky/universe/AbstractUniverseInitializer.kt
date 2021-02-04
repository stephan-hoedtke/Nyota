package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Hour
import java.lang.Exception


abstract class AbstractUniverseInitializer(protected var universe: Universe) {

    fun getStar(hd: Int): Star =
        universe.stars[hd] ?: throw Exception("Star $hd was not initialized yet")

    fun newGalaxy(name: String, imageId: Int, ascension: String, declination: String, brightness: Double, distance: Double): Galaxy =
        Galaxy(name, imageId, Hour.fromHour(ascension).angleInDegree, Degree.fromDegree(declination).angleInDegree, brightness, distance).also {
            universe.galaxies.add(it)
        }

    fun newAnything(name: String, ascension: String, declination: String, brightness: Double, distance: Double): Anything =
        Anything(name, Hour.fromHour(ascension).angleInDegree, Degree.fromDegree(declination).angleInDegree, brightness, distance).also {
            universe.any.add(it)
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

    fun setFriendlyNameTo(hd: Int, name: String): Star? =
        universe.stars[hd]?.setFriendlyName(name)

    fun asVIP(star: Star?) {
        star?.also {
            if (!universe.vip.contains(it)) {
                universe.vip.add(it)
            }
        }
    }
}

