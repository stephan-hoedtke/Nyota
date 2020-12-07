package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.City
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Satellites {

    private val map: HashMap<String, Satellite> = HashMap<String, Satellite>()
    private val array: ArrayList<Satellite> = ArrayList<Satellite>()

    val size: Int
        get() = array.size

    fun addAll(collection: Collection<Satellite>) {
        for (satellite in collection) {
            add(satellite)
        }
    }

    fun add(satellite: Satellite) {
        val satelliteName = satellite.name
        val existingSatellite = map[satelliteName]
        if (existingSatellite != null) {
            val index = array.indexOf(existingSatellite)
            array[index] = satellite
            map[satelliteName] = satellite
        }
        else {
            array.add(satellite)
            map[satelliteName] = satellite
        }
    }

    operator fun get(index: Int): Satellite =
        array[index]

    operator fun get(satelliteName: String): Satellite? =
        map[satelliteName]

    fun exists(satelliteName: String?): Boolean {
        return if (satelliteName != null)
            map.containsKey(satelliteName)
        else
            false
    }

    fun findSatelliteByIndex(index: Int): Satellite? =
        if (0 <= index && index < array.size)
            array[index]
        else
            null

    fun findSatelliteByName(satelliteName: String?): Satellite? {
        return if (satelliteName != null)
            map[satelliteName]
        else
            null
    }

    fun first(): Satellite =
        array.first()

    val values: List<Satellite>
        get() = array
 }
