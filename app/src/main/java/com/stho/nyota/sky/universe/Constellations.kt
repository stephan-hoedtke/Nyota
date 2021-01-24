package com.stho.nyota.sky.universe

class Constellations {

    private val array: ArrayList<Constellation> = ArrayList()
    private val map: HashMap<String, Constellation> = HashMap<String, Constellation>()

    val size: Int
        get() = array.size

    fun add(constellation: Constellation) {
        array.add(constellation)
        map[constellation.name] = constellation // automatically replace the old value if it exists
    }

    operator fun get(constellationName: String): Constellation? =
        map[constellationName]

    fun findConstellationByName(constellationName: String?): Constellation? =
        constellationName?.let { return map[it] }

    fun first(): Constellation =
        array.first()

    val values: Collection<Constellation>
        get() = array

}
