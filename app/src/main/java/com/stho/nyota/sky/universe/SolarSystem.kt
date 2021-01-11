package com.stho.nyota.sky.universe

/**
 * Created by shoedtke on 31.08.2016.
 */
class SolarSystem : AbstractSolarSystem(), ISolarSystem {

    val planets
        get() = elements.filter { e -> e.isPlanet }

    init {
        sun = Sun()
        moon = Moon()
        mercury = Mercury()
        venus = Venus()
        mars = Mars()
        jupiter = Jupiter()
        saturn = Saturn()
        uranus = Uranus()
        neptune = Neptune()
        elements = listOf(sun, moon, mercury, venus, mars, jupiter, saturn, uranus, neptune)
    }

    fun findPlanetByName(name: String): AbstractPlanet? =
        elements.firstOrNull { e -> e.isPlanet && e.name == name} as AbstractPlanet?

}
