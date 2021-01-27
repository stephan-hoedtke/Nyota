package com.stho.nyota.sky.universe

class Stars(private val constellations: Constellations) {

    private val array: ArrayList<Star> = ArrayList()
    private val map: HashMap<String, Star> = HashMap()
    private val catalog: HashMap<Int, Star> = HashMap()

    val size: Int
        get() = array.size

    fun add(star: Star) {
        array.add(star)
        if (star.hasName) {
            map[star.name] = star
        }
        if (star.HD > 0) {
            catalog[star.HD] = star
        }
    }

    operator fun get(HD: Int): Star? =
        catalog[HD]

    operator fun get(starName: String): Star? =
        map[starName]

    fun findStarByName(starName: String?): Star? =
        starName?.let { map[it] }

    fun createWithId(id: Long, hd: Int, name: String, friendlyName: String, symbol: String, rightAscension: Double, declination: Double, magnitude: Double, distance: Double, constellationId: Long) {
        Star.createWithId(id, hd, name, friendlyName, Symbol.fromString(symbol), rightAscension, declination, magnitude, distance).also { star ->
            add(star)
            constellations.findConstellationById(constellationId)?.also { constellation ->
                constellation.register(star)
            }
        }
    }

    val values: Collection<Star>
        get() = array

}