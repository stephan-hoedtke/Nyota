package com.stho.nyota.sky.universe

class Stars() {

    private val array: ArrayList<Star> = ArrayList()
    private val map: HashMap<String, Star> = HashMap()

    val size: Int
        get() = array.size

    fun add(star: Star) {
        array.add(star)
        if (star.nameIsUnique) {
            map[star.name] = star
        }
    }

    operator fun get(starName: String): Star? =
        map[starName]

    fun findStarByName(starName: String?): Star? =
        starName?.let { map[it] }

    val values: Collection<Star>
        get() = array

}