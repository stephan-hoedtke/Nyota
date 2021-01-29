package com.stho.nyota.sky.utilities

import android.location.LocationManager
import java.util.*

class Cities {

    private val array: ArrayList<City> = ArrayList<City>()

    val size: Int
        get() = array.size

    internal fun ensureDefaultAutomaticCity() {
        val city = array.find { city -> city.isAutomatic }
        if (city == null) {
            array.add(automaticCity)
        }
    }

    fun createCityWithId(id: Long, name: String, location: Location, timeZone: TimeZone, isAutomatic: Boolean): City =
        if (isAutomatic) {
            // overwrite the only automatic city with DB values
            automaticCity.also {
                it.id = id
                it.name = name
                it.location = location
                it.timeZone = timeZone
            }
        } else {
            // overwrite the existing city, if it is existing
            findCityById(id)?.also {
                it.name = name
                it.location = location
                it.timeZone = timeZone
            } ?:
            // otherwise create it and add it to the list.
            City.createCityWithId(id, name, location, timeZone).also {
                array.add(it)
            }
        }

    internal fun add(city: City, overwrite: Boolean) {
        if (city.isAutomatic) {
            val existingCity = findAutomaticCity()
            if (existingCity == null) {
                array.add(automaticCity)
            }
        } else {
            val existingCity = findMatchingNonAutomaticCity(city.name)
            if (existingCity == null) {
                array.add(city)
            } else {
                if (overwrite) {
                    val index = array.indexOf(existingCity)
                    array[index] = city
                }
            }
        }
    }

    private fun findAutomaticCity(): City? =
        array.find { city -> city.isAutomatic }

    private fun findMatchingNonAutomaticCity(cityName: String?): City? =
        if (cityName == null) {
            null
        } else {
            array.find { city -> city.name.equals(cityName, ignoreCase = true) && !city.isAutomatic }
        }

    private fun findMatchingCity(cityName: String?): City? =
        if (cityName == null) {
            null
        } else {
            array.find { city -> city.name.equals(cityName, ignoreCase = true) }
        }

    operator fun get(index: Int): City =
        array[index]

    operator fun get(cityName: String): City? =
        findMatchingCity(cityName)

    fun exists(cityName: String?): Boolean =
        findMatchingCity(cityName) != null

    fun findCityByIndex(index: Int): City? =
        if (0 <= index && index < array.size)
            array[index]
        else
            null

    fun findCityById(id: Long): City? =
        array.find { city -> city.id == id }

    fun findCityByName(cityName: String?): City? =
        findMatchingCity(cityName)

    fun createDefaultCities() {
        val defaultCities = City.createCities()
        for (city in defaultCities) {
            add(city, overwrite = false)
        }
    }

    val values: List<City>
        get() = array

    fun delete(city: City) {
        array.remove(city)
    }

    fun undoDelete(position: Int, city: City) {
        array.add(position, city)
    }

    fun updateDistances(referenceLocation: ILocation) {
        for (city in values) {
            city.updateDistance(referenceLocation)
        }
    }

    companion object {
        val automaticCity: City by lazy {
            City.createNewAutomaticCity()
        }
    }
}

