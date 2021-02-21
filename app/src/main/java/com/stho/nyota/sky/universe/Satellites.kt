package com.stho.nyota.sky.universe

import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Satellites {

    private val list: ArrayList<Satellite> = ArrayList<Satellite>()
    private val norad: HashMap<Int, Satellite> = HashMap<Int, Satellite>()

    val size: Int
        get() = list.size

    fun addAll(collection: Collection<Satellite>) {
        for (satellite in collection) {
            add(satellite)
        }
    }

    fun add(satellite: Satellite) {
        val noradSatelliteNumber = satellite.noradSatelliteNumber
        val existingSatellite = norad[satellite.noradSatelliteNumber]
        if (existingSatellite != null) {
            val index = list.indexOf(existingSatellite)
            list[index] = satellite
            norad[noradSatelliteNumber] = satellite
        }
        else {
            list.add(satellite)
            norad[noradSatelliteNumber] = satellite
        }
    }

    operator fun get(noradSatelliteNumber: Int): Satellite? =
        norad[noradSatelliteNumber]

    fun findSatelliteByIndex(index: Int): Satellite? =
        if (0 <= index && index < list.size)
            list[index]
        else
            null

    fun findSatelliteByName(satelliteName: String?): Satellite? =
        satelliteName?.let { list.find { s -> s.name == it } }

    fun findSatelliteByKey(key: String): Satellite? =
        when (Satellite.isValidKey(key)) {
            true -> {
                val noradSatelliteNumber = Satellite.noradSatelliteNumberFromKey(key)
                get(noradSatelliteNumber)
            }
            false -> null
        }

    val values: List<Satellite>
        get() = list

    internal fun createWithId(id: Long, name: String, displayName: String, noradSatelliteNumber: Int, elements: String) =
        Satellite.createSatelliteWithId(id, name, displayName, noradSatelliteNumber, elements).also { add(it) }
 }
