package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.City

class Constellations {

    private val map: HashMap<String, Constellation> = HashMap<String, Constellation>()

    val size: Int
        get() = map.size

    fun addAll(collection: Collection<Constellation>) {
        for (constellation in collection) {
            add(constellation)
        }
    }

    fun add(constellation: Constellation) {
        map[constellation.name] = constellation // automatically replace the old value if it exists
    }

    operator fun get(constellationName: String): Constellation? {
        return map[constellationName]
    }

    fun exists(constellationName: String?): Boolean {
        return if (constellationName != null)
            map.containsKey(constellationName)
        else
            false
    }

    fun findConstellationByName(constellationName: String?): Constellation? {
        return if (constellationName != null)
            return map[constellationName]
        else
            null
    }

    fun first(): Constellation {
        return map.values.first()
    }

    val values: Collection<Constellation>
        get() = map.values

}
