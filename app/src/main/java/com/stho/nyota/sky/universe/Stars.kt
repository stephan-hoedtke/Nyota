package com.stho.nyota.sky.universe

class Stars() {

    private val array: ArrayList<Star> = ArrayList()
    private val map: HashMap<String, Star> = HashMap()

    val size: Int
        get() = map.size

    fun add(star: Star) {
        array.add(star)
        if (star.hasUniqueName) {
            map[star.name] = star
        }
    }

    operator fun get(starName: String): Star? =
        map[starName]

    fun exists(starName: String?): Boolean =
        starName?.let { map.containsKey(it) } ?: false

    fun findStarByName(starName: String?): Star? =
        starName?.let { map[it]}

    val values: Collection<Star>
        get() = map.values

}