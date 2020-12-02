package com.stho.nyota.sky.universe

class Stars() {

    private val map: HashMap<String, Star> = HashMap<String, Star>()

    val size: Int
        get() = map.size

    fun add(star: Star) {
        map[star.name] = star // automatically replace the old value if it exists
    }

    operator fun get(starName: String): Star? {
        return map[starName]
    }

    fun exists(starName: String?): Boolean {
        return if (starName != null)
            map.containsKey(starName)
        else
            false
    }

    fun findStarByName(starName: String?): Star? {
        return if (starName != null) {
            map[starName]
        } else {
            null
        }
    }

    val values: Collection<Star>
        get() = map.values

}