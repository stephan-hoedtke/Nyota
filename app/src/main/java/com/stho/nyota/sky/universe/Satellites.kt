package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.City
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Satellites {

    private val array: ArrayList<Satellite> = ArrayList<Satellite>()
    private val norad: HashMap<Int, Satellite> = HashMap<Int, Satellite>()

    val size: Int
        get() = array.size

    fun addAll(collection: Collection<Satellite>) {
        for (satellite in collection) {
            add(satellite)
        }
    }

    fun add(satellite: Satellite) {
        val noradSatelliteNumber = satellite.noradSatelliteNumber
        val existingSatellite = norad[satellite.noradSatelliteNumber]
        if (existingSatellite != null) {
            val index = array.indexOf(existingSatellite)
            array[index] = satellite
            norad[noradSatelliteNumber] = satellite
        }
        else {
            array.add(satellite)
            norad[noradSatelliteNumber] = satellite
        }
    }

    operator fun get(noradSatelliteNumber: Int): Satellite? =
        norad[noradSatelliteNumber]

    fun findSatelliteByIndex(index: Int): Satellite? =
        if (0 <= index && index < array.size)
            array[index]
        else
            null

    fun findSatelliteByName(satelliteName: String?): Satellite? =
        satelliteName?.let { array.find { s -> s.name == it } }

    fun findSatelliteByKey(key: String): Satellite? =
        when (Satellite.isValidKey(key)) {
            true -> {
                val noradSatelliteNumber = Satellite.noradSatelliteNumberFromKey(key)
                get(noradSatelliteNumber)
            }
            false -> null
        }

    val values: List<Satellite>
        get() = array

    internal fun createWithId(id: Long, name: String, displayName: String, noradSatelliteNumber: Int, elements: String) =
        Satellite.createSatelliteWithId(id, name, displayName, noradSatelliteNumber, elements).also { add(it) }
 }
