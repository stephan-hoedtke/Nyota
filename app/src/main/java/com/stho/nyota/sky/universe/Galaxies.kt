package com.stho.nyota.sky.universe

class Galaxies {

    private val array: ArrayList<Galaxy> = ArrayList<Galaxy>()

    val size: Int
        get() = array.size

    fun add(galaxy: Galaxy) {
        val existingGalaxy = array.find { x -> x.key == galaxy.key}
        if (existingGalaxy != null) {
            val index = array.indexOf(existingGalaxy)
            array[index] = galaxy
        }
        else {
            array.add(galaxy)
        }
    }

    operator fun get(key: String): Galaxy? =
        array.find { x -> x.key == key }

    fun findGalaxyByName(galaxyName: String?): Galaxy? =
        galaxyName?.let { array.find { x -> x.name == it } }

    fun findGalaxyByKey(key: String): Galaxy? =
        when (Galaxy.isValidKey(key)) {
            true -> get(key)
            false -> null
        }

    val values: List<Galaxy>
        get() = array

}
