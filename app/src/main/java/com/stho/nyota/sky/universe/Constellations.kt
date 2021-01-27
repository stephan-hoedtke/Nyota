package com.stho.nyota.sky.universe

import java.lang.Exception

class Constellations {

    private val array: ArrayList<Constellation> = ArrayList()
    private val map: HashMap<String, Constellation> = HashMap<String, Constellation>()

    val size: Int
        get() = array.size

    fun add(constellation: Constellation) {
        array.add(constellation)
        map[constellation.name] = constellation // automatically replace the old value if it exists
    }

    operator fun get(constellationName: String): Constellation =
        map[constellationName] ?: throw Exception("Constellation $constellationName is not registered yet.")

    fun findConstellationById(constellationId: Long): Constellation? =
        if (constellationId > 0)
            array.firstOrNull { x -> x.id == constellationId }
        else
            null

    fun findConstellationByName(constellationName: String?): Constellation? =
        constellationName?.let { return map[it] }

    fun first(): Constellation =
        array.first()

    fun createWithId(id: Long, number: Int, name: String, abbreviation: String, english: String, german: String, link: String) {
        Constellation.createWithId(id, number, name, abbreviation, english, german, link).also {
            add(it)
        }
    }

    val values: Collection<Constellation>
        get() = array

}
