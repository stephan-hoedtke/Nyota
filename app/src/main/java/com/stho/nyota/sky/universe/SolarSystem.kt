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

    fun findElementByKey(key: String): IElement? =
        when (key) {
            moon.key -> moon
            sun.key -> sun
            else -> findPlanetByKey(key)
        }

    fun findPlanetByKey(key: String): AbstractPlanet? =
        when (AbstractPlanet.isValidKey(key)) {
            true -> {
                val planetName = AbstractPlanet.nameFromKey(key)
                findPlanetByName(planetName)
            }
            false -> null
        }

    fun findPlanetByName(name: String): AbstractPlanet? =
        elements.find { e -> e.isPlanet && e.name == name} as AbstractPlanet?

}
