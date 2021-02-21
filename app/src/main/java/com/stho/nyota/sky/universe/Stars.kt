package com.stho.nyota.sky.universe

class Stars(private val constellations: Constellations) {

    private val list: ArrayList<Star> = ArrayList()
    private val names: HashMap<String, Star> = HashMap()
    private val catalog: HashMap<Int, Star> = HashMap()

    val size: Int
        get() = list.size

    fun add(star: Star) {
        list.add(star)
        if (star.hasName) {
            names[star.name] = star
        }
        if (star.hasFriendlyName) {
            names[star.friendlyName] = star
        }
        if (star.HD > 0) {
            catalog[star.HD] = star
        }
    }

    operator fun get(HD: Int): Star? =
        catalog[HD]

    operator fun get(starName: String): Star? =
        names[starName]

    fun setFriendlyName(star: Star, friendlyName: String) =
        star.setFriendlyName(friendlyName).also { names[friendlyName] = star }

    fun findStarByName(starName: String?): Star? =
        starName?.let { names[it] }

    fun findStarByKey(key: String): Star? =
        when (Star.isValidKey(key)) {
            true -> {
                val HD = Star.hdFromKey(key)
                get(HD)
            }
            false -> null
        }

    fun createWithId(id: Long, hd: Int, name: String, friendlyName: String, symbol: String, rightAscension: Double, declination: Double, magnitude: Double, distance: Double, constellationId: Long) {
        Star.createWithId(id, hd, name, friendlyName, Symbol.fromString(symbol), rightAscension, declination, magnitude, distance).also { star ->
            add(star)
            constellations.findConstellationById(constellationId)?.also { constellation ->
                constellation.register(star)
            }
        }
    }

    val values: Collection<Star>
        get() = list

}